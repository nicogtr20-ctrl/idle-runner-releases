package com.overmind.zero;

import java.util.Locale;

/** EXTERNAL OBSERVER of actual gate physics, never fed to a motor policy.
 * Records where the pipeline stops: approach, contact, grip intent,
 * adhesion, bilateral hold, gate translation, opening, or passage.
 */
public final class GateBehaviorMetrics {
    public static final String KEY="gate_behavior_v114";
    private transient ScarcityLab roomSeen;
    private int prevContact,prevDrive,prevCommand,prevHeld,prevOpposed;
    public long nearbyFrames=0,coreTouchFrames=0,skinTouchFrames=0,
        driveFrames=0,commandFrames=0,heldFrames=0,opposedFrames=0;
    public int opened=0,passages=0;
    // Additional read-only metrics: crossing a partly open gate is NOT
    // equivalent to geometrically valid clearance or full-opening status.
    public int gatePlaneCrossings=0,realEntrances=0,partialEntrances=0,
               questionableEntrances=0;
    private float previousCoreX=Float.NaN;
    private boolean crossedGatePlane=false;
    private boolean priorActualMaterialAcross=false;
    public float closest=Float.POSITIVE_INFINITY,maxTravel=0;
    private boolean wasOpened=false,wasInside=false;
    public void resetLife(){
        roomSeen=null;
        prevContact=prevDrive=prevCommand=prevHeld=prevOpposed=0;
        wasOpened=false;wasInside=false;
        previousCoreX=Float.NaN;crossedGatePlane=false;
        priorActualMaterialAcross=false;
    }
    private static float distanceToGate(MotorBrain.Body b,ScarcityLab r){
        float best=Float.POSITIVE_INFINITY;
        for(int i=0;i<MotorBrain.N;i++){
            float dx=Math.max(0,Math.abs(b.x[i]-r.gateX())-ScarcityLab.DOOR_HALF_W);
            float dy=Math.max(0,Math.abs(b.y[i]-r.gateCenterY())-ScarcityLab.DOOR_HALF_H);
            best=Math.min(best,(float)Math.hypot(dx,dy));
        }
        return best;
    }
    /** A geometric reference point cannot cross a wall: count actual
     * distributed masses, and require them to be physically in the opening.
     */
    private static int beyondOpening(MotorBrain.Body b,float plane,ScarcityLab r){
        int count=0;
        float top=r.oy-ScarcityLab.OPEN_HALF_HEIGHT+2f;
        float bottom=r.oy+ScarcityLab.OPEN_HALF_HEIGHT-2f;
        for(int i=0;i<MotorBrain.N;i++)
            if(b.x[i]>=plane&&b.y[i]>top&&b.y[i]<bottom)count++;
        return count;
    }
    /** Called AFTER chamber.constrain, with no reward or action output. */
    public void observe(MotorBrain.Body body,ScarcityLab r){
        if(body==null||r==null)return;
        if(roomSeen!=r){
            roomSeen=r;
            // A new lab room or a physical scene restored from save
            // starts at existing counters, not zeroed cumulative history.
            prevContact=r.gateContactFrames;
            prevDrive=r.gateDriveFrames;
            prevCommand=r.gateCommandFrames;
            prevHeld=r.gateHeldFrames;
            prevOpposed=r.opposedContactFrames;
            wasOpened=r.openingFree();
            wasInside=beyondOpening(body,r.entranceX()+
                ScarcityLab.WALL_HALF_THICKNESS+2f,r)>=6;
            previousCoreX=body.cx;
            priorActualMaterialAcross=beyondOpening(body,
                r.gateX()+ScarcityLab.DOOR_HALF_W+2f,r)>=6;
            crossedGatePlane=priorActualMaterialAcross;
        }else{
            skinTouchFrames+=Math.max(0,r.gateContactFrames-prevContact);
            driveFrames+=Math.max(0,r.gateDriveFrames-prevDrive);
            commandFrames+=Math.max(0,r.gateCommandFrames-prevCommand);
            heldFrames+=Math.max(0,r.gateHeldFrames-prevHeld);
            opposedFrames+=Math.max(0,r.opposedContactFrames-prevOpposed);
            prevContact=r.gateContactFrames;prevDrive=r.gateDriveFrames;
            prevCommand=r.gateCommandFrames;prevHeld=r.gateHeldFrames;
            prevOpposed=r.opposedContactFrames;
        }
        float dist=distanceToGate(body,r);
        closest=Math.min(closest,dist);
        // The spawn itself is ~67 units from the rectangular gate.
        // 45 units requires real approach, not merely spawning nearby.
        if(dist<=45f)nearbyFrames++;
        if(dist<=3f)coreTouchFrames++;
        maxTravel=Math.max(maxTravel,r.maxDoorTravel);
        boolean open=r.openingFree();
        if(open&&!wasOpened)opened++;
        wasOpened=open;
        float atGate=r.gateX()+ScarcityLab.DOOR_HALF_W+2f;
        float atWall=r.entranceX()+ScarcityLab.WALL_HALF_THICKNESS+2f;
        boolean plausible=Float.isFinite(previousCoreX)&&
            Math.abs(body.cx-previousCoreX)<12f;
        boolean actualAcross=beyondOpening(body,atGate,r)>=6;
        if(plausible&&!priorActualMaterialAcross&&actualAcross){
            crossedGatePlane=true;
            gatePlaneCrossings++;
        }
        boolean inside=beyondOpening(body,atWall,r)>=6;
        if(!inside&&beyondOpening(body,r.gateX()-
             ScarcityLab.DOOR_HALF_W-2f,r)==0)crossedGatePlane=false;
        if(inside&&!wasInside&&plausible&&crossedGatePlane){
            realEntrances++;
            if(!open)partialEntrances++;
            // The virtual center has no width. Verify actual material
            // vertical span at the plane instead of a fictional 54U nucleus.
            float minY=Float.POSITIVE_INFINITY,maxY=Float.NEGATIVE_INFINITY;
            for(int i=0;i<MotorBrain.N;i++){
                if(body.x[i]>=r.gateX()-ScarcityLab.DOOR_HALF_W-6f&&
                   body.x[i]<=r.gateX()+ScarcityLab.DOOR_HALF_W+6f){
                    minY=Math.min(minY,body.y[i]);maxY=Math.max(maxY,body.y[i]);
                }
            }
            boolean clearance=!Float.isFinite(minY)||
                (minY>=r.oy-ScarcityLab.OPEN_HALF_HEIGHT&&
                 maxY<=r.oy+ScarcityLab.OPEN_HALF_HEIGHT);
            if(!clearance)questionableEntrances++;
            if(open)passages++;
            crossedGatePlane=false;
        }
        wasInside=inside;
        previousCoreX=body.cx;
        priorActualMaterialAcross=actualAcross;
    }
    public float closestDistance(){return Float.isFinite(closest)?closest:-1;}
    public String obstacleStage(){
        if(realEntrances>0)return "entrata fisica rilevata (anche con varco parziale)";
        if(passages>0)return "passaggio realmente attraversato";
        if(opened>0)return "cancello fisicamente aperto, non ancora attraversato";
        if(maxTravel>.1f)return "cancello spostato, apertura non riuscita";
        if(opposedFrames>0)return "presa bilaterale, senza movimento";
        if(heldFrames>0)return "presa reale, manca coordinazione bilaterale";
        if(commandFrames>0)return "comando di presa, non trattenuta";
        if(driveFrames>0)return "adesione selezionata, presa inefficace";
        if(skinTouchFrames>0)return "contatto di membrana, nessuna presa riuscita";
        if(coreTouchFrames>0)return "corpo contro il cancello";
        if(nearbyFrames>0)return "raggiunto il vicinato del cancello";
        return "nessun avvicinamento rilevato";
    }
    public String encode(){
        return String.format(Locale.US,
            "gb2|%d|%d|%d|%d|%d|%d|%d|%d|%d|%d|%.5f|%.5f|%d|%d|%d|%d",
            nearbyFrames,coreTouchFrames,skinTouchFrames,driveFrames,
            commandFrames,heldFrames,opposedFrames,opened,passages,
            (long)0,closestDistance(),maxTravel,gatePlaneCrossings,
            realEntrances,partialEntrances,questionableEntrances);
    }
    public boolean restore(String source){
        if(source==null||source.length()>350||
           !(source.startsWith("gb1|")||source.startsWith("gb2|")))
            return false;
        try{
            String[] x=source.split("\\|",-1);
            boolean next=x[0].equals("gb2");
            if(x.length!=(next?17:13))return false;
            long[] a=new long[7];
            for(int i=0;i<a.length;i++){a[i]=Long.parseLong(x[i+1]);if(a[i]<0)return false;}
            int o=Integer.parseInt(x[8]),p=Integer.parseInt(x[9]);
            long reserved=Long.parseLong(x[10]);
            float d=Float.parseFloat(x[11]),travel=Float.parseFloat(x[12]);
            int crosses=next?Integer.parseInt(x[13]):0,
                entries=next?Integer.parseInt(x[14]):0,
                partial=next?Integer.parseInt(x[15]):0,
                uncertain=next?Integer.parseInt(x[16]):0;
            if(o<0||p<0||reserved!=0||!Float.isFinite(d)||d< -1||
               !Float.isFinite(travel)||travel<0||travel>ScarcityLab.DOOR_MAX_TRAVEL||
               crosses<0||entries<0||partial<0||uncertain<0||
               partial>entries||uncertain>entries||entries>crosses)
                return false;
            nearbyFrames=a[0];coreTouchFrames=a[1];skinTouchFrames=a[2];
            driveFrames=a[3];commandFrames=a[4];heldFrames=a[5];
            opposedFrames=a[6];opened=o;passages=p;
            closest=d<0?Float.POSITIVE_INFINITY:d;maxTravel=travel;
            gatePlaneCrossings=crosses;realEntrances=entries;
            partialEntrances=partial;questionableEntrances=uncertain;
            resetLife();return true;
        }catch(RuntimeException e){return false;}
    }
    public static void main(String[] args){
        GateBehaviorMetrics m=new GateBehaviorMetrics();
        MotorBrain.Body b=new MotorBrain.Body();
        ScarcityLab gate=new ScarcityLab(0,0);
        m.observe(b,gate);
        if(m.passages!=0||m.opened!=0||m.maxTravel!=0||m.nearbyFrames!=0)
            throw new AssertionError("Invented opening with untouched door");
        float deltaX=gate.gateX()-ScarcityLab.DOOR_HALF_W-10-b.cx;
        for(int i=0;i<MotorBrain.N;i++)b.x[i]+=deltaX;
        MaterialGeometry.observe(b);
        m.observe(b,gate);
        if(m.nearbyFrames<1||m.coreTouchFrames<1)
            throw new AssertionError("Failed to count proximity and core contact");
        gate.gateContactFrames=6;gate.gateDriveFrames=5;
        gate.gateCommandFrames=3;gate.gateHeldFrames=2;
        gate.opposedContactFrames=1;gate.maxDoorTravel=15;
        m.observe(b,gate);
        if(m.skinTouchFrames!=6||m.driveFrames!=5||
           m.commandFrames!=3||m.heldFrames!=2||
           m.opposedFrames!=1||m.maxTravel!=15||
           m.passages!=0||m.opened!=0)
            throw new AssertionError("Physics diagnostics not separated properly");
        String state=m.encode();
        GateBehaviorMetrics restored=new GateBehaviorMetrics();
        if(!restored.restore(state)||!state.equals(restored.encode()))
            throw new AssertionError("Gate measurements were not preserved");
        if(new GateBehaviorMetrics().restore("gb1|bad"))
            throw new AssertionError("Corrupt gate metrics accepted");
        // Synthetic measurements are coordinates passed to an observer;
        // they do not move the real body or alter ScarcityLab physics.
        GateBehaviorMetrics partial=new GateBehaviorMetrics();
        ScarcityLab room=new ScarcityLab(0,0);
        MotorBrain.Body core=new MotorBrain.Body();
        room.doorY=95;
        float toX=room.gateX()-70-core.cx,toY=room.oy-24-core.cy;
        for(int i=0;i<MotorBrain.N;i++){core.x[i]+=toX;core.y[i]+=toY;}
        MaterialGeometry.observe(core);
        partial.observe(core,room);
        float stop=room.entranceX()+55;
        for(float x=core.cx+4;x<=stop;x+=4){
            float change=x-core.cx;
            for(int i=0;i<MotorBrain.N;i++)core.x[i]+=change;
            MaterialGeometry.observe(core);
            partial.observe(core,room);
        }
        if(partial.gatePlaneCrossings!=1||partial.realEntrances!=1||
           partial.partialEntrances!=1||partial.opened!=0)
            throw new AssertionError("Failed partial geometric crossing detector");
        GateBehaviorMetrics copy=new GateBehaviorMetrics();
        if(!copy.restore(partial.encode())||!copy.encode().equals(partial.encode()))
            throw new AssertionError("gb2 persisted crossing counters incorrectly");
        GateBehaviorMetrics historical=new GateBehaviorMetrics();
        if(!historical.restore("gb1|3|2|1|1|1|1|1|0|0|0|1.0|0.0")||
            historical.partialEntrances!=0)
            throw new AssertionError("Historic gb1 save migration broken");
        System.out.println("ZERO-0114 PASS: independent observed gate proximity, "+
            "contact, adhesion, bilateral grip and genuine movement");
    }
}
