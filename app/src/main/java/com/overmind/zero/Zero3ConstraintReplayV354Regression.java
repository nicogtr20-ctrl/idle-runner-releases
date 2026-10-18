package com.overmind.zero;

import java.nio.file.*;
import java.util.*;

/** Captured real failure, not a hand-posed successful grip. */
public final class Zero3ConstraintReplayV354Regression {
    public static void main(String[] args)throws Exception{
        List<String> rows=Files.readAllLines(Paths.get(args.length==0?"tools/fixtures/zero354-contact-1514.txt":args[0]));
        MotorBrain.Body body=new MotorBrain.Body();Morphology.Surface s=new Morphology.Surface(body);
        float[] gx=new float[96],gy=new float[96],px=new float[96],py=new float[96];ScarcityLab room=null;
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        for(String row:rows){String[] a=row.split(" ");
            if(a[0].equals("ROOM")){room=new ScarcityLab(Float.parseFloat(a[1]),Float.parseFloat(a[2]));room.doorY=Float.parseFloat(a[3]);}
            else if(a[0].equals("S")){int i=Integer.parseInt(a[1]);s.x[i]=Float.parseFloat(a[2]);s.y[i]=Float.parseFloat(a[3]);s.vx[i]=Float.parseFloat(a[4]);s.vy[i]=Float.parseFloat(a[5]);s.gripCommand[i]=Float.parseFloat(a[6]);gx[i]=Float.parseFloat(a[8]);gy[i]=Float.parseFloat(a[9]);px[i]=Float.parseFloat(a[10]);py[i]=Float.parseFloat(a[11]);}
            else if(a[0].equals("BOX")){UnknownWorld.Entity e=new UnknownWorld.Entity(Float.parseFloat(a[4]),Float.parseFloat(a[5]),2,Integer.parseInt(a[1]));e.x=Float.parseFloat(a[2]);e.y=Float.parseFloat(a[3]);e.angle=Float.parseFloat(a[6]);for(int i=0;i<96;i++)e.boxGripHold[i]=Float.parseFloat(a[7+i]);objects.add(e);}
        }
        float[][] timers=new float[objects.size()][];for(int i=0;i<timers.length;i++)timers[i]=objects.get(i).boxGripHold.clone();
        float[] commands=s.gripCommand.clone();GripSurfaceComposition.compose(s,room,objects);int before=0;for(float h:s.gripHold)if(h>=Morphology.GRIP_HOLD)before++;
        Zero3JointConstraintV354.Result result=Zero3JointConstraintV354.solve(s,objects,room,gx,gy,px,py);
        GripSurfaceComposition.compose(s,room,objects);int after=0;for(float h:s.gripHold)if(h>=Morphology.GRIP_HOLD)after++;
        System.out.println("captured initial="+result.initial+" remaining="+result.remaining+" moved="+result.moved+" held="+before+"/"+after);
        if(!result.repaired||Zero3JointConstraintV354.defects(s,objects,room,null)!=0||after<before||!Arrays.equals(commands,s.gripCommand))throw new AssertionError("V354 captured failure unresolved/hold lost");
        for(int i=0;i<timers.length;i++)if(!Arrays.equals(timers[i],objects.get(i).boxGripHold))throw new AssertionError("V354 altered owner timers");
        System.out.println("V354 CAPTURED1514=PASS crossings0=true allSolids0=true heldBefore="+before+" heldAfter="+after+" timerAndCommandUnchanged=true");
    }
}
