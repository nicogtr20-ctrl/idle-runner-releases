package com.overmind.zero;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Locale;

/**
 * Passive contact/adhesion flight recorder. Every input is a copy/read of an
 * ALREADY executed motor/physics step. No reference to decision, reward,
 * mutation, collision or training APIs is kept or returned.
 *
 * This observer lives outside the 12 named character saves, in its own
 * SharedPreferences file. Only an explicit GPT share exports its contents.
 */
final class GripContactObserver {
    static final String PREFS="zero_grip_observer_v1169";
    static final String KEY="report";
    private static final int MAX_SAMPLES=190;
    private static final float SAMPLE_INTERVAL=.80f;
    private static final float TOUCH_MARGIN=4f;
    private final Totals gate=new Totals(),boxes=new Totals();
    private final Contact gateContact=new Contact(0);
    private final IdentityHashMap<UnknownWorld.Entity,Contact> boxContacts=
        new IdentityHashMap<>();
    private final ArrayDeque<String> samples=new ArrayDeque<>();
    private final float[] beforeHold=new float[Morphology.POINTS];
    private final boolean[] beforeFace=new boolean[Morphology.POINTS];
    private UnknownWorld.Entity pendingBox;
    private ScarcityLab observedGate;
    private int nextBoxId=1,life=0;
    private String lastLife="";

    static final class Totals {
        long touchFrames,onFrames,offFrames,contactPoints,commandPoints,
            heldPoints,orangePoints,vetoPoints,offCommandPoints,
            onNoOrangeFrames,onNoCommandFrames,commandNoOrangeFrames,
            interruptedHolds,sameFaceDrops,geometryDrops,positiveMotionFrames;
        float maximumDwell,maximumHold;
        void add(int touch,int cmd,int held,int orange,int veto,int offCmd,
                 int interrupted,int same,int geometry,float dwell,float maxHold,
                 boolean on,boolean motion){
            if(touch==0)return;
            touchFrames++;
            if(on)onFrames++;else offFrames++;
            contactPoints+=touch;commandPoints+=cmd;
            heldPoints+=held;orangePoints+=orange;
            vetoPoints+=veto;offCommandPoints+=offCmd;
            if(on&&orange==0)onNoOrangeFrames++;
            if(on&&cmd==0)onNoCommandFrames++;
            if(cmd>0&&orange==0)commandNoOrangeFrames++;
            interruptedHolds+=interrupted;sameFaceDrops+=same;
            geometryDrops+=geometry;
            if(motion)positiveMotionFrames++;
            maximumDwell=Math.max(maximumDwell,dwell);
            maximumHold=Math.max(maximumHold,maxHold);
        }
        String encode(String type){
            return String.format(Locale.US,
                "TOTAL,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                type,touchFrames,onFrames,offFrames,contactPoints,commandPoints,
                heldPoints,orangePoints,vetoPoints,offCommandPoints,
                onNoOrangeFrames,onNoCommandFrames,commandNoOrangeFrames,
                interruptedHolds,sameFaceDrops,geometryDrops,
                Math.round(maximumDwell*1000),Math.round(maximumHold*1000));
        }
    }
    private static final class Contact{
        final int id;
        final float[] touchDwell=new float[Morphology.POINTS];
        boolean active=false;
        float nextSample=Float.NEGATIVE_INFINITY;
        Contact(int id){this.id=id;}
        float countDwell(int i,boolean touching,float dt){
            touchDwell[i]=touching?Math.min(300f,touchDwell[i]+Math.max(0,dt)):0;
            return touchDwell[i];
        }
    }
    void newLife(){
        // Preserve the previous complete life and cumulative session totals.
        if(!samples.isEmpty())lastLife=eventText();
        samples.clear();boxContacts.clear();pendingBox=null;observedGate=null;
        for(int i=0;i<Morphology.POINTS;i++)gateContact.touchDwell[i]=0;
        gateContact.active=false;gateContact.nextSample=Float.NEGATIVE_INFINITY;
        life++;
    }
    void terminal(){
        if(!samples.isEmpty())lastLife=eventText();
    }
    private static boolean boxFace(UnknownWorld.Entity box,float x,float y){
        float dx=x-box.x,dy=y-box.y;
        return (Math.abs(Math.abs(dx)-SquareGripPuzzle.HALF)<=TOUCH_MARGIN &&
                Math.abs(dy)<=SquareGripPuzzle.HALF+TOUCH_MARGIN) ||
               (Math.abs(Math.abs(dy)-SquareGripPuzzle.HALF)<=TOUCH_MARGIN &&
                Math.abs(dx)<=SquareGripPuzzle.HALF+TOUCH_MARGIN);
    }
    private static boolean gateFace(ScarcityLab r,float x,float y){
        float lx=r.gateX()-ScarcityLab.DOOR_HALF_W;
        float rx=r.gateX()+ScarcityLab.DOOR_HALF_W;
        return Math.abs(y-r.gateCenterY())<=ScarcityLab.DOOR_HALF_H+2 &&
            (Math.abs(x-lx)<=3.7f&&x<r.gateX() ||
             Math.abs(x-rx)<=3.7f&&x>=r.gateX());
    }
    private void push(String line){
        samples.addLast(line);
        while(samples.size()>MAX_SAMPLES)samples.removeFirst();
    }
    /** Sample all 96 points AFTER gate physics, BEFORE square physics. */
    void gateFrame(Morphology.Surface skin,ScarcityLab room,
                   float drive,float elapsed,float dt){
        if(room==null)return;
        if(observedGate!=room){
            observedGate=room;
            gateContact.active=false;
            gateContact.nextSample=Float.NEGATIVE_INFINITY;
            for(int i=0;i<Morphology.POINTS;i++)gateContact.touchDwell[i]=0;
        }
        int contact=0,cmd=0,held=0,orange=0,veto=0,offCmd=0;
        float longest=0,maxHold=0;
        boolean on=drive>.1f;
        for(int i=0;i<Morphology.POINTS;i++){
            boolean touching=gateFace(room,skin.x[i],skin.y[i]) &&
                skin.touchSense[i]>.5f;
            longest=Math.max(longest,gateContact.countDwell(i,touching,dt));
            if(!touching)continue;
            contact++;
            boolean commanded=skin.gripCommand[i]>Morphology.GRIP_THRESHOLD;
            if(commanded){cmd++;if(!on)offCmd++;}
            else if(on)veto++;
            maxHold=Math.max(maxHold,skin.gripHold[i]);
            if(skin.gripHold[i]>=Morphology.GRIP_HOLD){
                held++;orange++;
            }
        }
        gate.add(contact,cmd,held,orange,veto,offCmd,0,0,0,
                 longest,maxHold,on,Math.abs(room.doorVelocity)>.01f);
        sample(gateContact,"G",on,contact,cmd,held,orange,veto,0,0,0,
               longest,maxHold,room.maxDoorTravel,elapsed);
    }
    /** Snapshot only: no physics hook, no mutation of any particle. */
    void beforeBox(Morphology.Surface skin,UnknownWorld.Entity box){
        pendingBox=box;
        for(int i=0;i<Morphology.POINTS;i++){
            beforeHold[i]=box.boxGripHold[i];
            beforeFace[i]=boxFace(box,skin.x[i],skin.y[i]);
        }
    }
    /** Sample immediately AFTER the unmodified square solver. */
    void boxFrame(Morphology.Surface skin,UnknownWorld.Entity box,
                  float drive,float elapsed,float dt){
        // A diagnostic must NEVER abort or alter a running game.
        if(box==null||box!=pendingBox){pendingBox=null;return;}
        pendingBox=null;
        Contact state=boxContacts.get(box);
        if(state==null){state=new Contact(nextBoxId++);boxContacts.put(box,state);}
        int contact=0,cmd=0,held=0,orange=0,veto=0,offCmd=0;
        int drop=0,same=0,geometry=0;
        float longest=0,maxHold=0;
        boolean on=drive>.1f;
        for(int i=0;i<Morphology.POINTS;i++){
            boolean face=boxFace(box,skin.x[i],skin.y[i]);
            // An exact physical face is the ground truth for this box:
            // touchSense may still describe a DIFFERENT nearby object.
            longest=Math.max(longest,state.countDwell(i,face,dt));
            boolean commanded=skin.gripCommand[i]>Morphology.GRIP_THRESHOLD;
            if(beforeHold[i]>0&&skin.gripHold[i]<.00001f&&commanded){
                drop++;
                if(beforeFace[i]&&face)same++;
                else geometry++;
            }
            if(!face)continue;
            contact++;
            if(commanded){cmd++;if(!on)offCmd++;}
            else if(on)veto++;
            maxHold=Math.max(maxHold,skin.gripHold[i]);
            if(skin.gripHold[i]>=Morphology.GRIP_HOLD){
                held++;
                if(skin.touchSense[i]>.5f)orange++;
            }
        }
        boxes.add(contact,cmd,held,orange,veto,offCmd,drop,same,geometry,
                  longest,maxHold,on,box.displacementFromSpawn()>.2f);
        sample(state,"B",on,contact,cmd,held,orange,veto,drop,same,geometry,
               longest,maxHold,box.displacementFromSpawn(),elapsed);
    }
    /** Recycle observer-only references when habitat streaming removes boxes. */
    void pruneBoxes(java.util.ArrayList<UnknownWorld.Entity> living){
        if(boxContacts.size()<=32||living==null)return;
        java.util.Iterator<UnknownWorld.Entity> it=boxContacts.keySet().iterator();
        while(it.hasNext())if(!living.contains(it.next()))it.remove();
    }
    private void sample(Contact state,String kind,boolean on,int touching,
                        int cmd,int held,int orange,int veto,int drop,int same,
                        int geometry,float dwell,float hold,float movement,float time){
        if(touching==0){
            if(state.active){
                push(String.format(Locale.US,"END,%d,%d,%s,%d",
                    life,Math.round(time*1000),kind,state.id));
                state.active=false;
            }
            return;
        }
        if(!state.active||time>=state.nextSample){
            push(String.format(Locale.US,
                "S,%d,%d,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                life,Math.round(time*1000),kind,state.id,on?1:0,
                touching,cmd,held,orange,veto,drop,same,geometry,
                Math.round(dwell*1000),Math.round(hold*1000),
                Math.round(movement*10)));
            state.nextSample=time+SAMPLE_INTERVAL;state.active=true;
        }
    }
    private String eventText(){
        StringBuilder b=new StringBuilder();
        for(String s:samples)b.append(s).append('\n');
        return b.toString();
    }
    String encode(){
        StringBuilder b=new StringBuilder(18000);
        b.append("go1|session-counters|life=").append(life).append('\n')
         .append("# TOTAL:type,touchFrames,onFrames,offFrames,contactPoints,commandPoints,heldPoints,orangePoints,vetoPoints,offCommandPoints,onNoOrangeFrames,onNoCommandFrames,commandNoOrangeFrames,interruptedHolds,sameFaceDrops,geometryDrops,maxDwellMs,maxHoldMs\n")
         .append(gate.encode("GATE")).append('\n')
         .append(boxes.encode("BOX")).append('\n')
         .append("# S:life,simTimeMs,type(G/B),objectId,on,touchPoints,commandPoints,heldPoints,orangePoints,vetoPoints,interruptedHolds,sameFaceDrops,geometryDrops,maxDwellMs,maxHoldMs,travelTenths\n")
         .append("# Interrupted holds are observed before/after square physics; source is not assumed.\n")
         .append("CURRENT\n").append(eventText())
         .append("PREVIOUS_LIFE\n").append(lastLife);
        // Rolling event buffer is bounded; cap preserved last-life text too.
        return b.toString();
    }
    Totals gateTotals(){return gate;}
    Totals boxTotals(){return boxes;}
}
