package com.overmind.zero;

import java.util.*;
import java.util.Locale;

/**
 * ZERO V3.36 independent paired contact experiment. Zero motor only: test
 * a candidate legal-face correction without changing real-world dispatch.
 * Old V333 ordinal fixtures are DIAGNOSTIC ONLY, never holdout.
 */
public final class Zero3ExactFaceTrialV336Regression {
    private static final int[] CASES={333,337,341,641,645,649};
    private static final float DT=.025f;
    private Zero3ExactFaceTrialV336Regression(){}

    private static void check(boolean yes,String why){
        if(!yes)throw new AssertionError("V336 "+why);
    }
    private static void candidateWorld(
         Zero3BoxCrossingForensicV334Regression.State s,
         float[] sx,float[] sy,float[] bx,float[] by,
         float oldCX,float oldCY,boolean linksAware){
        boolean boxes=false;
        for(UnknownWorld.Entity e:s.p.world){
            if(e.isBilateralBox()){
                // The ONLY candidate difference: skip ejection of legal
                // exact face contacts already at the solid boundary.
                SquareGripPuzzle.constrain(s.p.body,s.p.skin,e,DT,
                     sx,sy,bx,by,oldCX,oldCY,null,true,linksAware);
                boxes=true;
            }
        }
        if(boxes)
            MaterialContactConstraints.reconcile(s.p.body,s.p.skin,
                                                 s.p.world,DT);
        SymbioteWorldStepV171.interior(s.p.gel,s.p.body,s.p.skin,
                                      null,s.p.world,DT);
        GripSurfaceComposition.compose(s.p.skin,null,s.p.world);
        for(UnknownWorld.Entity e:s.p.world){
            if(e.isMovable()){
                Morphology.collide(s.p.skin,e,DT);
                ContactLearning.collide(s.p.body,e,DT);
                s.p.gel.observeBody(s.p.body,s.p.skin);
            }
            e.tick(DT);
        }
    }
    private static void cycle(
       Zero3BoxCrossingForensicV334Regression.State s,
       int mode,float[] muscle){
        float[] sx=s.p.skin.x.clone(),sy=s.p.skin.y.clone(),
                bx=s.p.body.x.clone(),by=s.p.body.y.clone();
        float cx=s.p.body.cx,cy=s.p.body.cy;
        s.p.gel.step(s.p.body,s.p.skin,muscle,DT);
        if(mode!=0)candidateWorld(s,sx,sy,bx,by,cx,cy,mode==2);
        else Zero3MultiSceneRestartV322BRegression.actualWorldStep(
                   s.p,sx,sy,bx,by,cx,cy);
    }
    private static boolean embedded(
              Zero3BoxCrossingForensicV334Regression.State s){
        for(UnknownWorld.Entity e:s.p.world)if(e.isBilateralBox())
            for(int i=0;i<Morphology.POINTS;i++)
                if(Math.abs(s.p.skin.x[i]-e.x)<SquareGripPuzzle.HALF-1e-4f&&
                   Math.abs(s.p.skin.y[i]-e.y)<SquareGripPuzzle.HALF-1e-4f)
                    return true;
        return false;
    }
    public static void main(String[] args){
        int improvedFirst=0,regressedFirst=0,penetrating=0,
            unresolved=0,normalChanged=0;
        int combinedImproved=0,combinedWorse=0,combinedUnresolved=0,
            combinedPenetrated=0,combinedChangedControls=0;
        for(int id:CASES){
            boolean blind=id>=600;
            Zero3BoxCrossingForensicV334Regression.State original=
                new Zero3BoxCrossingForensicV334Regression.State(id,blind);
            Zero3BoxCrossingForensicV334Regression.State candidate=
                new Zero3BoxCrossingForensicV334Regression.State(id,blind);
            Zero3BoxCrossingForensicV334Regression.State combined=
                new Zero3BoxCrossingForensicV334Regression.State(id,blind);
            original.warmup();candidate.warmup();combined.warmup();
            original.spawn();candidate.spawn();combined.spawn();
            check(MaterialGeometry.selfCrossings(original.p.skin)==0&&
                  MaterialGeometry.selfCrossings(candidate.p.skin)==0,
                  "already crossed before intervention");
            float[] motors=new float[64];
            for(int sub=0;sub<2;sub++){
                cycle(original,0,motors);
                cycle(candidate,1,motors);
                cycle(combined,2,motors);
                int base=MaterialGeometry.selfCrossings(original.p.skin),
                    tried=MaterialGeometry.selfCrossings(candidate.p.skin);
                UnknownWorld.Entity box=null;
                for(UnknownWorld.Entity e:candidate.p.world)
                    if(e.isBilateralBox()){box=e;break;}
                int through=box==null?0:
                     SquareCollisionSolver.intersections(candidate.p.skin,box);
                boolean badEmbed=embedded(candidate);
                boolean improved=base>tried;
                if(improved)improvedFirst++;
                if(tried>base)regressedFirst++;
                if(tried>0)unresolved++;
                if(through>0||badEmbed)penetrating++;
                if(id!=337&&id!=645&&
                   (tried!=base||through>0||badEmbed))normalChanged++;
                int linked=MaterialGeometry.selfCrossings(combined.p.skin);
                UnknownWorld.Entity combinedBox=null;
                for(UnknownWorld.Entity e:combined.p.world)
                    if(e.isBilateralBox()){combinedBox=e;break;}
                int linkedThrough=combinedBox==null?0:
                    SquareCollisionSolver.intersections(combined.p.skin,combinedBox);
                boolean linkedEmbedded=embedded(combined);
                if(linked<base)combinedImproved++;
                if(linked>base)combinedWorse++;
                if(linked>0)combinedUnresolved++;
                if(linkedThrough>0||linkedEmbedded)combinedPenetrated++;
                if(id!=337&&id!=645&&
                   (linked!=base||linkedThrough>0||linkedEmbedded))
                    combinedChangedControls++;
                System.out.println("V336 COMBINED_COMPARE id="+id+
                    " sub="+sub+" originalCross="+base+
                    " combinedCross="+linked+
                    " throughBox="+linkedThrough+
                    " embedded="+linkedEmbedded+
                    " physicalGuardExperimental=true");
                System.out.println(String.format(Locale.US,
                    "V336 FACE_COMPARE id=%d sub=%d baselineCross=%d "+
                    "candidateCross=%d penetrating=%s throughWall=%d "+
                    "candidateGrip=%s baselineGrip=%s "+
                    "changed=%s",
                    id,sub,base,tried,badEmbed,through,
                    box!=null&&box.bilateralHeldNow,
                    findBilateral(original),
                    improved));
            }
        }
        System.out.println("V336 COMBINED_TRIAL matchedBirths=6"+
            " improvedFrames="+combinedImproved+
            " worsenedFrames="+combinedWorse+
            " unresolvedFrames="+combinedUnresolved+
            " penetration="+combinedPenetrated+
            " controlRegressions="+combinedChangedControls+
            " verdict="+(combinedWorse==0&&combinedUnresolved==0&&
                combinedPenetrated==0&&combinedChangedControls==0?
                "SHORT_SCREEN_ONLY_NOT_PROMOTION":"REJECTED")+
            " G3_BLOCKED G5_BLOCKED NO_OTA");
        // Reporting a negative result is part of the protocol. Never
        // treat reducing one crossing while leaving another as full fix.
        String verdict=regressedFirst==0&&penetrating==0&&
                       unresolved==0&&normalChanged==0?
                       "CANDIDATE_SCREEN_ONLY_NOT_G3":"REJECTED";
        System.out.println("V336 FACE_TRIAL totalMatchedBirths=6"+
            " improvedFrames="+improvedFirst+
            " worsenedFrames="+regressedFirst+
            " remainingCrossingFrames="+unresolved+
            " penViolations="+penetrating+
            " controlRegressions="+normalChanged+
            " verdict="+verdict+
            " G3_BLOCKED G5_BLOCKED NO_OTA");
    }
    private static boolean findBilateral(
         Zero3BoxCrossingForensicV334Regression.State s){
        for(UnknownWorld.Entity e:s.p.world)
            if(e.isBilateralBox())return e.bilateralHeldNow;
        return false;
    }
}
