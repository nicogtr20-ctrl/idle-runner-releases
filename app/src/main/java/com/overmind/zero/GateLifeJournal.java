package com.overmind.zero;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * 0116.4 — passive meaningful-event journal across an ENTIRE lived episode.
 * Not a control policy, reward, grip instruction, memory replay, or target.
 * The full sequence survives the five-minute DecisionBlackbox horizon.
 */
public final class GateLifeJournal {
    public static final String CURRENT="gate_journal_current_v1164";
    public static final String LAST="gate_journal_last_v1164";
    private static final int MAX_EVENTS=260, MAX_TEXT=30000;
    private static final float APPROACH=47f, RETREAT=66f;
    private final ArrayDeque<String> events=new ArrayDeque<>();
    private String last="";
    private transient ScarcityLab seen;
    private boolean approaching=false,contact=false,command=false,held=false,
                    opposed=false,shifted=false;
    private int movementBucket=0,entranceCount=0;
    private int previousContact=0,previousCommand=0,previousHold=0,
                previousOpposed=0,previousEntry=0,previousCross=0;
    private float lastMovementTime=-9999f,lifeTime=0;
    private static int q(float x){return Float.isFinite(x)?Math.round(x*10):0;}
    public void newLife(){
        events.clear();seen=null;approaching=contact=command=held=opposed=shifted=false;
        movementBucket=entranceCount=previousContact=previousCommand=
            previousHold=previousOpposed=previousEntry=previousCross=0;
        lastMovementTime=-9999f;lifeTime=0;
    }
    public void resumeLife(){
        // On the next frame, align absolute room counters with real restored
        // values; never fabricate contact / new grip for a loaded checkpoint.
        seen=null;
    }
    public int count(){return events.size();}
    public String lastLife(){return last;}
    public String currentLife(){return encode();}
    private void append(String label,ScarcityLab room,MotorBrain.Body body,float energy){
        if(label==null||label.length()>24)return;
        String row=q(lifeTime)+","+label+","+
            q(room==null?0:room.doorY)+","+
            q(room==null?0:room.maxDoorTravel)+","+
            q(body==null?0:body.cx-(room==null?0:room.gateX()))+","+
            q(body==null?0:body.cy-(room==null?0:room.oy))+","+
            q(energy)+","+(room==null?0:room.gateHeldFrames)+","+
            (room==null?0:room.opposedContactFrames);
        events.addLast(row);
        while(events.size()>MAX_EVENTS)events.removeFirst();
    }
    public void observe(MotorBrain.Body body,ScarcityLab room,GateBehaviorMetrics m,
                        float time,float energy){
        if(room==null||body==null||m==null)return;
        if(Float.isFinite(time)&&time>=0)lifeTime=time;
        if(room!=seen){
            seen=room;
            approaching=contact=command=held=opposed=shifted=false;
            movementBucket=(int)(room.maxDoorTravel/5f);
            previousContact=room.gateContactFrames;
            previousCommand=room.gateCommandFrames;
            previousHold=room.gateHeldFrames;
            previousOpposed=room.opposedContactFrames;
            previousEntry=m.realEntrances;
            previousCross=m.gatePlaneCrossings;
            lastMovementTime=lifeTime;
            append("ROOM",room,body,energy);
        }
        float dist=(float)Math.hypot(
            Math.max(0,Math.abs(body.cx-room.gateX())-ScarcityLab.DOOR_HALF_W),
            Math.max(0,Math.abs(body.cy-room.gateCenterY())-ScarcityLab.DOOR_HALF_H));
        boolean near=dist<APPROACH;
        if(near&&!approaching)append("APPROACH",room,body,energy);
        if(!near&&approaching&&dist>RETREAT)append("LEAVE",room,body,energy);
        approaching=near&&dist<RETREAT || approaching&&dist<=RETREAT;
        boolean touch=room.gateContactFrames>previousContact;
        boolean cmd=room.gateCommandFrames>previousCommand;
        boolean grip=room.gateHeldFrames>previousHold;
        boolean two=room.opposedContactFrames>previousOpposed;
        if(touch&&!contact)append("TOUCH",room,body,energy);
        if(!touch&&contact)append("TOUCH_END",room,body,energy);
        if(cmd&&!command)append("GRIP_CMD",room,body,energy);
        if(!cmd&&command)append("CMD_END",room,body,energy);
        if(grip&&!held)append("HELD",room,body,energy);
        if(!grip&&held)append("RELEASE",room,body,energy);
        if(two&&!opposed)append("BILATERAL",room,body,energy);
        if(!two&&opposed)append("BILAT_END",room,body,energy);
        contact=touch;command=cmd;held=grip;opposed=two;
        int bucket=Math.max(0,(int)(room.maxDoorTravel/5f));
        if(bucket>movementBucket){
            movementBucket=bucket;
            lastMovementTime=lifeTime;
            shifted=true;
            append("MOVE_5U",room,body,energy);
        }else if(shifted&&near&&lifeTime-lastMovementTime>3f){
            shifted=false;append("STALLED",room,body,energy);
        }
        if(m.gatePlaneCrossings>previousCross)append("GATE_PLANE",room,body,energy);
        if(m.realEntrances>previousEntry){
            append(room.openingFree()?"ENTER_FULL":"ENTER_PART",room,body,energy);
            entranceCount+=m.realEntrances-previousEntry;
            if(m.questionableEntrances>0)
                append("CHECK_CLEARANCE",room,body,energy);
        }
        previousCross=m.gatePlaneCrossings;previousEntry=m.realEntrances;
        previousContact=room.gateContactFrames;
        previousCommand=room.gateCommandFrames;
        previousHold=room.gateHeldFrames;
        previousOpposed=room.opposedContactFrames;
    }
    public void reward(MotorBrain.Body body,ScarcityLab room,
                       float time,float realEnergy){
        if(Float.isFinite(time)&&time>=0)lifeTime=time;
        if(realEnergy>0)append("ROOM_ENERGY",room,body,realEnergy);
    }
    public void terminal(float time,float energy){
        if(Float.isFinite(time)&&time>=0)lifeTime=time;
        append("DEATH",seen,null,energy);
        last=encode();
    }
    public String encode(){
        ArrayList<String> out=new ArrayList<>(events);
        int chars=16;
        for(String row:out)chars+=row.length()+1;
        int start=0;
        while(chars>MAX_TEXT-120&&start<out.size())
            chars-=out.get(start++).length()+1;
        StringBuilder b=new StringBuilder(Math.min(chars+50,MAX_TEXT));
        b.append("gj1|").append(q(lifeTime)).append('|')
         .append(out.size()-start).append('\n');
        for(int i=start;i<out.size();i++)b.append(out.get(i)).append('\n');
        return b.toString();
    }
    private static boolean valid(String src){
        if(src==null||src.length()>MAX_TEXT||!src.startsWith("gj1|"))return false;
        try{
            String[] lines=src.split("\n",-1),head=lines[0].split("\\|",-1);
            if(head.length!=3)return false;
            int duration=Integer.parseInt(head[1]),count=Integer.parseInt(head[2]);
            if(duration<0||count<0||count>MAX_EVENTS||
               lines.length!=count+2||!lines[count+1].isEmpty())return false;
            int prev=-1;
            for(int i=1;i<=count;i++){
                String[] p=lines[i].split(",",-1);
                if(p.length!=9||p[1].isEmpty()||p[1].length()>24||
                   !p[1].matches("[A-Z0-9_]+"))return false;
                int tick=Integer.parseInt(p[0]);
                if(tick<prev||tick>duration)return false;prev=tick;
                for(int j=2;j<p.length;j++)Integer.parseInt(p[j]);
            }
            return true;
        }catch(RuntimeException invalid){return false;}
    }
    public boolean restoreCurrent(String src){
        if(!valid(src))return false;
        String[] l=src.split("\n",-1),h=l[0].split("\\|");
        ArrayDeque<String> next=new ArrayDeque<>();
        for(int i=1;i<l.length-1;i++)next.addLast(l[i]);
        events.clear();events.addAll(next);
        lifeTime=Integer.parseInt(h[1])/10f;
        resumeLife();return true;
    }
    public boolean restoreLast(String src){
        if(!valid(src))return false;last=src;return true;
    }
    private static void check(boolean b,String reason){
        if(!b)throw new AssertionError(reason);
    }
    public static void main(String[] args){
        GateLifeJournal log=new GateLifeJournal();
        GateBehaviorMetrics observer=new GateBehaviorMetrics();
        ScarcityLab gate=new ScarcityLab(0,0);
        MotorBrain.Body body=new MotorBrain.Body();
        body.cx=gate.gateX()-ScarcityLab.DOOR_HALF_W-18;
        log.observe(body,gate,observer,10,85);
        observer.observe(body,gate);
        log.observe(body,gate,observer,10.2f,85);
        gate.gateContactFrames++;gate.gateCommandFrames++;
        gate.gateHeldFrames++;gate.opposedContactFrames++;
        gate.doorY=12;gate.maxDoorTravel=12;
        log.observe(body,gate,observer,13,83);
        check(log.encode().contains("BILATERAL")&&
              log.encode().contains("MOVE_5U"),"Actual grip and motion not logged");
        log.reward(body,gate,1000,80);
        log.terminal(2500,0);
        String frozen=log.lastLife();
        check(frozen.contains("ROOM_ENERGY")&&frozen.contains("DEATH")&&
              frozen.contains("BILATERAL"),"Long-lived early events lost");
        log.newLife();
        check(log.count()==0&&log.lastLife().equals(frozen),
              "Death reset erased last-life journal");
        GateLifeJournal copy=new GateLifeJournal();
        check(copy.restoreLast(frozen)&&copy.restoreCurrent(frozen),
              "Trace did not survive named save");
        String good=copy.encode();
        check(!copy.restoreCurrent(good+"bad")&&copy.encode().equals(good),
              "Corrupt trace destroyed valid live log");
        check(log.encode().length()<=MAX_TEXT,"Export budget exceeded");
        System.out.println("GateLifeJournal PASS: real grip phases, movement, long life, death, reload");
    }
}
