package com.overmind.zero;

import java.util.ArrayList;
import java.util.Random;

/** Reproduces OTA127 double-box same-96-patch collapse and phantom +18 pickup.
 * Also asserts that ZERO's voluntary grips and ordinary legal 2-sided
 * manipulation survive the physics-only corrective rules.
 */
public final class MaterialContactRegression {
    private static void yes(boolean ok,String reason){
        if(!ok)throw new AssertionError(reason);
    }
    private static int unique(Morphology.Surface s,float margin){
        int result=0;float[] xx=new float[Morphology.POINTS];
        float[] yy=new float[Morphology.POINTS];
        for(int i=0;i<Morphology.POINTS;i++){
            boolean seen=false;
            for(int j=0;j<result;j++)
                if(Math.hypot(s.x[i]-xx[j],s.y[i]-yy[j])<margin){seen=true;break;}
            if(!seen){xx[result]=s.x[i];yy[result++]=s.y[i];}
        }
        return result;
    }
    private static int shared(UnknownWorld.Entity a,UnknownWorld.Entity b){
        int result=0;
        for(int i=0;i<Morphology.POINTS;i++)
            if(a.boxGripHold[i]>=Morphology.GRIP_HOLD&&
               b.boxGripHold[i]>=Morphology.GRIP_HOLD)result++;
        return result;
    }
    private static void phantomEnergy(){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        for(int i=0;i<MotorBrain.N;i++){
            body.x[i]=50+i*2;body.y[i]=50;
        }
        // Replicate real numbers from recorded "source=extra18,
        // material=0" on physical body but remove hidden assumptions:
        // closest real mass at 32.4 from a radius15 blue gem.
        body.x[0]=32.4f;body.y[0]=0;
        for(int i=0;i<Morphology.POINTS;i++){
            skin.x[i]=125;skin.y[i]=80;
        }
        UnknownWorld.Entity blue=new UnknownWorld.Entity(0,0,0,0);
        float radius=blue.radius;
        boolean oldFallback=Math.hypot(body.x[0],body.y[0])<radius+18;
        yes(oldFallback&&
            !MaterialGeometry.touches(body,skin,blue.x,blue.y,radius+2),
            "Cannot reproduce old incorrect +18 blue-energy collection");
        yes(blue.ready(),"Untouched blue energy no longer exists");
        body.x[0]=15f;
        yes(MaterialGeometry.touches(body,skin,blue.x,blue.y,radius+2),
            "Real internal tissue contact is incorrectly rejected");
        yes(blue.touch(0,0)==22&&!blue.ready()&&blue.touch(0,0)==0,
            "Finite blue gem duplicate payout");
    }
    private static void twoBoxes(){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        UnknownWorld.Entity a=new UnknownWorld.Entity(0,0,2,5);
        UnknownWorld.Entity c=new UnknownWorld.Entity(-38.2f,-36.5f,2,5);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        world.add(a);world.add(c);
        // Real OTA127 snapshot: every one of the 96 membrane particles
        // was at ONE red point near the mutually facing box corners, and
        // all 96 had independent, positive adhesion timers to BOTH boxes.
        for(int i=0;i<Morphology.POINTS;i++){
            s.x[i]=-17f;s.y[i]=-21f;
            s.gripCommand[i]=1f;s.gripHold[i]=.25f;
            a.boxGripHold[i]=c.boxGripHold[i]=.25f;
        }
        yes(unique(s,.65f)==1&&shared(a,c)==Morphology.POINTS,
            "OTA127 96-on-96 double-contact fixture not reproduced");
        float oldAx=a.x,oldBx=c.x;
        int total=0;
        for(int step=0;step<45;step++){
            total+=MaterialContactConstraints.reconcile(b,s,world,.035f);
            yes(shared(a,c)==0,
                "Same membrane particle still adhesively held by two boxes");
            yes(a.x==oldAx&&c.x==oldBx&&a.vx==0f&&c.vx==0f,
                "Contact consistency mysteriously moved an external box");
            for(int i=0;i<Morphology.POINTS;i++){
                yes(s.gripCommand[i]==1f,
                    "The physical repair changed ZERO's voluntary grip ON");
                for(UnknownWorld.Entity box:world)
                    yes(!(Math.abs(s.x[i]-box.x)<19.94f&&
                          Math.abs(s.y[i]-box.y)<19.94f),
                        "Separating physical material moved through a solid");
            }
        }
        int separated=unique(s,.65f);
        yes(separated>=40,
            "The 96 overlapped patches remain permanently a single red point: "+
            separated+", corrections="+total);
        yes(total>0,"No physical repair after impossible collapsed material");
    }
    private static void separateLegitimateGrips(){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        UnknownWorld.Entity a=new UnknownWorld.Entity(-150,0,2,5);
        UnknownWorld.Entity c=new UnknownWorld.Entity(150,0,2,5);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        world.add(a);world.add(c);
        for(int i=0;i<Morphology.POINTS;i++){
            double theta=2*Math.PI*i/Morphology.POINTS;
            s.x[i]=(float)(70*Math.cos(theta));
            s.y[i]=(float)(70*Math.sin(theta));
        }
        s.x[10]=a.x-SquareGripPuzzle.HALF;s.y[10]=a.y;
        s.x[58]=c.x+SquareGripPuzzle.HALF;s.y[58]=c.y;
        s.gripCommand[10]=s.gripCommand[58]=1f;
        a.boxGripHold[10]=.25f;c.boxGripHold[58]=.25f;
        float oldX=s.x[10],oldY=s.y[10];
        MaterialContactConstraints.reconcile(b,s,world,.035f);
        yes(a.boxGripHold[10]>=Morphology.GRIP_HOLD&&
            c.boxGripHold[58]>=Morphology.GRIP_HOLD,
            "Distinct valid box-face grips wrongly removed");
        yes(s.x[10]==oldX&&s.y[10]==oldY,
            "Independent real material contact was teleported");
        yes(shared(a,c)==0,"Impossible ownership overlap in legal scene");
    }
    public static void main(String[] args){
        phantomEnergy();
        twoBoxes();
        separateLegitimateGrips();
        System.out.println("PASS: OTA127 double 96-patch collapsed trap disperses by physical finite-area exclusion, no shared owners, real energy contact only and legal independent grips retained");
    }
}
