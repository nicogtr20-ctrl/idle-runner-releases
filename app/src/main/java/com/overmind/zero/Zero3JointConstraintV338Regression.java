package com.overmind.zero;

import java.util.Arrays;

/**
 * V338 deterministic independent synthetic feasibility checks.
 * This does NOT authorize the real matched neural/BOX world candidate.
 */
public final class Zero3JointConstraintV338Regression {
    private Zero3JointConstraintV338Regression(){}
    private static void check(boolean yes,String message){
        if(!yes)throw new AssertionError("V338 JOINT "+message);
    }
    static final class Setup {
        final MotorBrain.Body body=new MotorBrain.Body();
        final Morphology.Surface s=new Morphology.Surface(body);
        final UnknownWorld.Entity box=new UnknownWorld.Entity(0,0,2,5);
        Setup(){
            for(int i=0;i<Morphology.POINTS;i++){
                double angle=2.0*Math.PI*i/Morphology.POINTS;
                s.x[i]=31f*(float)Math.cos(angle);
                s.y[i]=31f*(float)Math.sin(angle);
                s.vx[i]=i*.001f;
                s.vy[i]=-i*.001f;
                s.gripCommand[i]=0f;s.gripHold[i]=0f;
            }
            check(Zero3JointBoxConstraintV338.measure(s,box).total()==0,
                  "fixture reference topology must start legal");
        }
    }
    static boolean same(float[] a,float[] b){
        if(a.length!=b.length)return false;
        for(int i=0;i<a.length;i++)
            if(Float.floatToIntBits(a[i])!=Float.floatToIntBits(b[i]))
                return false;
        return true;
    }
    private static void stableNoChanges(){
        Setup f=new Setup();
        float[] x=f.s.x.clone(),y=f.s.y.clone(),
                vx=f.s.vx.clone(),vy=f.s.vy.clone();
        Zero3JointBoxConstraintV338.Result r=
             Zero3JointBoxConstraintV338.solve(f.s,f.box,x,y);
        check(!r.attempted&&!r.repaired&&!r.unresolved&&
             same(x,f.s.x)&&same(y,f.s.y)&&
             same(vx,f.s.vx)&&same(vy,f.s.vy),
             "zero-defect projection must be bitwise identity");
        System.out.println("V338 UNIT identity PASS");
    }
    private static void correctedSmallEmbedWithLockedFace(){
        Setup f=new Setup();
        f.s.x[0]=SquareGripPuzzle.HALF;
        f.s.y[0]=0;
        f.s.gripCommand[0]=1;
        f.s.gripHold[0]=.22f;
        f.box.boxGripHold[0]=.22f;
        float[] beforeX=f.s.x.clone(),beforeY=f.s.y.clone();
        beforeX[48]=-24.5f;
        f.s.x[48]=-19.9f;
        check(Zero3JointBoxConstraintV338.measure(f.s,f.box).total()>0,
              "synthetic single embedded patch missing");
        float oldHold=f.box.boxGripHold[0],oldBoxX=f.box.x;
        Zero3JointBoxConstraintV338.Result r=
            Zero3JointBoxConstraintV338.solve(f.s,f.box,beforeX,beforeY);
        check(r.attempted&&r.repaired&&!r.unresolved&&
              r.finalCross==0&&r.finalWalls==0&&r.finalEmbedded==0,
              "small embedded patch not jointly resolved: "+r.line());
        check(f.s.x[0]==SquareGripPuzzle.HALF&&f.s.y[0]==0&&
              f.s.gripCommand[0]==1f&&f.s.gripHold[0]==.22f&&
              f.box.boxGripHold[0]==oldHold&&f.box.x==oldBoxX,
              "legal real held face changed by safety repair");
        System.out.println("V338 UNIT anchored-neighbor-local-embed PASS "+r.line());
    }
    private static void impossibleCannotTeleport(){
        Setup f=new Setup();
        float[] targetX=f.s.x.clone(),targetY=f.s.y.clone();
        f.s.x[0]=0;f.s.y[0]=0;
        float[] postX=f.s.x.clone(),postY=f.s.y.clone(),
                postVX=f.s.vx.clone(),postVY=f.s.vy.clone();
        Zero3JointBoxConstraintV338.Result r=
           Zero3JointBoxConstraintV338.solve(f.s,f.box,targetX,targetY);
        check(r.attempted&&r.unresolved&&!r.repaired&&
              same(postX,f.s.x)&&same(postY,f.s.y)&&
              same(postVX,f.s.vx)&&same(postVY,f.s.vy),
              "unresolvable >9 physical jump must fail without mutation");
        System.out.println("V338 UNIT impossible-fail-closed PASS "+r.line());
    }
    private static void impossibleHeldAnchor(){
        Setup f=new Setup();
        f.s.x[0]=0;f.s.y[0]=0;
        f.s.gripCommand[0]=1f;f.s.gripHold[0]=.3f;
        f.box.boxGripHold[0]=.3f;
        float[] postX=f.s.x.clone(),postY=f.s.y.clone();
        float[] priorX=postX.clone(),priorY=postY.clone();
        priorX[0]=31;
        Zero3JointBoxConstraintV338.Result r=
             Zero3JointBoxConstraintV338.solve(f.s,f.box,priorX,priorY);
        check(r.unresolved&&same(postX,f.s.x)&&same(postY,f.s.y)&&
              f.box.boxGripHold[0]==.3f,
              "illegal hold must never move/forge/erase adhesive owner");
        System.out.println("V338 UNIT anchored-impossible-fail-closed PASS");
    }
    public static void main(String[] args){
        stableNoChanges();
        correctedSmallEmbedWithLockedFace();
        impossibleCannotTeleport();
        impossibleHeldAnchor();
        System.out.println("V338 JOINT_UNIT PASS identity, feasible "+
            "single membrane repair, immovable real hold, "+
            "no teleport, failure is transactional NO_OTA G3_BLOCKED");
    }
}
