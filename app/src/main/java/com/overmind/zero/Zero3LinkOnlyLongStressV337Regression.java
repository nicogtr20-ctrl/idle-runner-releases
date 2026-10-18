package com.overmind.zero;

import java.util.Locale;

/**
 * ZERO 3.36 *physically integrated* contact stress. Actual SymbioteMatterV1,
 * 96-point shell, 74 inner fluid parcels, square/contact/world step, 2000
 * consecutive frames in each historically failing morphology. Muscle = 0
 * for conservative isolation; this does not test or claim learned gait.
 *
 * V333 old ordinal fixtures are reused ONLY as forensic regressions,
 * never as new blind evidence.
 */
public final class Zero3LinkOnlyLongStressV337Regression {
    private static final int[] CASES={333,337,341,641,645,649};
    private static final float DT=.025f;
    private Zero3LinkOnlyLongStressV337Regression(){}

    static final class Stats {
        int frames,badCrossFrames,badWallFrames,badPointFrames;
        int badGelFrames,baselineCrossFrames,baselineWallFrames;
        int realGripFrames,bilateralFrames;
        double work;
    }
    static Stats run(int ordinal,int horizon){
        boolean blind=ordinal>=600;
        Zero3BoxCrossingForensicV334Regression.State base=
            new Zero3BoxCrossingForensicV334Regression.State(ordinal,blind);
        Zero3BoxCrossingForensicV334Regression.State candidate=
            new Zero3BoxCrossingForensicV334Regression.State(ordinal,blind);
        base.warmup();candidate.warmup();base.spawn();candidate.spawn();
        float[] a=new float[64];
        Stats s=new Stats();
        for(int frame=0;frame<horizon;frame++){
            Zero3ExactFaceTrialV336Regression.cycle(base,0,a);
            Zero3ExactFaceTrialV336Regression.cycle(candidate,3,a);
            s.frames++;
            if(MaterialGeometry.selfCrossings(base.p.skin)>0)
                s.baselineCrossFrames++;
            int cross=MaterialGeometry.selfCrossings(candidate.p.skin);
            if(cross>0)s.badCrossFrames++;
            UnknownWorld.Entity box=null,originalBox=null;
            for(UnknownWorld.Entity e:candidate.p.world)
                if(e.isBilateralBox()){box=e;break;}
            for(UnknownWorld.Entity e:base.p.world)
                if(e.isBilateralBox()){originalBox=e;break;}
            if(box!=null){
                if(SquareCollisionSolver.intersections(candidate.p.skin,box)>0)
                    s.badWallFrames++;
                if(Zero3ExactFaceTrialV336Regression.embedded(candidate))
                    s.badPointFrames++;
                if(box.bilateralHeldNow)s.bilateralFrames++;
                boolean grip=false;
                for(float hold:box.boxGripHold)
                    if(hold>=Morphology.GRIP_HOLD)grip=true;
                if(grip)s.realGripFrames++;
            }
            if(originalBox!=null&&
               SquareCollisionSolver.intersections(base.p.skin,originalBox)>0)
                s.baselineWallFrames++;
            if(candidate.p.gel.invalid>0||
               !Float.isFinite(candidate.p.body.cx)||
               !Float.isFinite(candidate.p.body.cy)||
               !Float.isFinite(candidate.p.gel.work))
                s.badGelFrames++;
            if(frame%500==499||frame==horizon-1)
                System.out.println(String.format(Locale.US,
                    "V337 LINK_STRESS_CHECK id=%d frame=%d/%d baselineCross=%d "+
                    "candidateCross=%d throughWall=%d embedded=%d "+
                    "invalid=%d work=%.9f",ordinal,frame+1,horizon,
                    s.baselineCrossFrames,s.badCrossFrames,s.badWallFrames,
                    s.badPointFrames,s.badGelFrames,(double)candidate.p.gel.work));
            s.work=candidate.p.gel.work;
        }
        return s;
    }
    public static void main(String[] argv){
        int failures=0,total=0,violations=0,baselineCross=0;
        double work=0;
        for(int id:CASES){
            // Two previously censored faces receive 2000 continuous physical
            // solver frames; four controls 256 frames each, fixed in advance.
            int horizon=(id==337||id==645)?2000:256;
            Stats s=run(id,horizon);
            total+=s.frames;
            work+=s.work;
            baselineCross+=s.baselineCrossFrames;
            int problems=s.badCrossFrames+s.badWallFrames+
                         s.badPointFrames+s.badGelFrames;
            if(problems>0)failures++;
            violations+=problems;
            System.out.println(String.format(Locale.US,
                "V337 LINK_STRESS_RESULT id=%d frames=%d "+
                "candidateSelfCrossFrames=%d baselineSelfCrossFrames=%d "+
                "candidateSolidLinks=%d candidateEmbedded=%d "+
                "badGelFrames=%d gripFrames=%d bilateralFrames=%d "+
                "work=%.9f gate=%s",
                id,s.frames,s.badCrossFrames,s.baselineCrossFrames,
                s.badWallFrames,s.badPointFrames,s.badGelFrames,
                s.realGripFrames,s.bilateralFrames,s.work,
                problems==0?"PASS_SHORT_PHYSICS_ONLY":"FAIL"));
        }
        System.out.println("V337 LINK_LONG_STRESS totalRealSolverFrames="+total+
            " failingCases="+failures+" totalViolations="+violations+
            " baselineCrossFrames="+baselineCross+
            " measuredWork="+String.format(Locale.US,"%.9f",work)+
            " verdict="+(failures==0?"SCREEN_PASS_NOT_G3":"FAIL")+
            " realGripNotTestedWithZeroMotor=true"+
            " G3_BLOCKED G5_BLOCKED NO_OTA");
    }
}
