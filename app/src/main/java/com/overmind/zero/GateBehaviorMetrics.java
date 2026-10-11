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
    public float closest=Float.POSITIVE_INFINITY,maxTravel=0;
    private boolean wasOpened=false,wasInside=false;
    public void resetLife(){
        roomSeen=null;
        prevContact=prevDrive=prevCommand=prevHeld=prevOpposed=0;
        wasOpened=false;wasInside=false;
        previousCoreX=Float.NaN;crossedGatePlane=false;
    }
    private static float distanceToGate(MotorBrain.Body b,ScarcityLab r){
        float dx=Math.max(0,Math.abs(b.cx-r.gateX())-ScarcityLab.DOOR_HALF_W);
        float dy=Math.max(0,Math.abs(b.cy-r.gateCenterY())-ScarcityLab.DOOR_HALF_H);
        return (float)Math.hypot(dx,dy);
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
            wasInside=body.cx>r.entranceX()+ScarcityLab.WALL_HALF_THICKNESS+2&&
                Math.abs(body.cy-r.oy)<ScarcityLab.OPEN_HALF_HEIGHT;
            previousCoreX=body.cx;crossedGatePlane=
                body.cx>=r.gateX()+ScarcityLab.DOOR_HALF_W+MotorBrain.R;
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
        if(dist<=MotorBrain.R+3f)coreTouchFrames++;
        maxTravel=Math.max(maxTravel,r.maxDoorTravel);
        boolean open=r.openingFree();
        if(open&&!wasOpened)opened++;
        wasOpened=open;
        float atGate=r.gateX()+ScarcityLab.DOOR_HALF_W+MotorBrain.R;
        float atWall=r.entranceX()+ScarcityLab.WALL_HALF_THICKNESS+2f;
        boolean plausible=Float.isFinite(previousCoreX)&&
            Math.abs(body.cx-previousCoreX)<12f;
        if(plausible&&previousCoreX<atGate&&body.cx>=atGate){
            crossedGatePlane=true;
            gatePlaneCrossings++;
        }
        boolean inside=body.cx>atWall&&
            Math.abs(body.cy-r.oy)<ScarcityLab.OPEN_HALF_HEIGHT;
        if(!inside&&body.cx<r.gateX()-ScarcityLab.DOOR_HALF_W-MotorBrain.R)
            crossedGatePlane=false;
        if(inside&&!wasInside&&plausible&&crossedGatePlane){
            realEntrances++;
            if(!open)partialEntrances++;
            // Detect a successful-looking entrance even if the recorded
            // aperture was too small for the 54-unit nucleus. This is
            // evidence for a possible collision defect, not a victory flag.
            float upperGap=r.gateTop()-(r.oy-ScarcityLab.OPEN_HALF_HEIGHT);
            float lowerGap=(r.oy+ScarcityLab.OPEN_HALF_HEIGHT)-r.gateBottom();
            boolean clearance=Math.max(upperGap,lowerGap)>=2f*MotorBrain.R;
            if(!clearance)questionableEntrances++;
            if(open)passages++;
            crossedGatePlane=false;
        }
        wasInside=inside;
        previousCoreX=body.cx;
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
        b.cx=gate.gateX()-ScarcityLab.DOOR_HALF_W-10;
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
        core.cx=room.gateX()-70;core.cy=room.oy-24;
        partial.observe(core,room);
        for(float x=core.cx+4;x<=room.entranceX()+20;x+=4){
            core.cx=x;partial.observe(core,room);
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
