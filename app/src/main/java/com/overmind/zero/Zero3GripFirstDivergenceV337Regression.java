package com.overmind.zero;

import java.util.*;
import java.util.Locale;

/**
 * ZERO V3.36 ACTIVE mechanical stress: decisions ARE chosen by authentic
 * recurrent V317 from real PRE sensors; original and experimental BOX solver
 * receive IDENTICAL 64 executed motor efferences from cortex + frozen V319.
 * Thus candidate has no autonomous branch of its own; it is a matched-action
 * physical counterfactual, NOT evidence of a learned G3 gait.
 */
public final class Zero3GripFirstDivergenceV337Regression {
    private static final int[] CASES={337,645};
    private static final float DT=.025f;
    private static final int DECISIONS=168, SUBSTEPS=12;
    private static final String OWNER=Zero3AutonomousLoopV317.OWNER;
    private Zero3GripFirstDivergenceV337Regression(){}

    private static void check(boolean value,String why){
        if(!value)throw new AssertionError("V336 ACTIVE "+why);
    }
    private static final class Metric {
        int steps,cross,wall,embedded,invalid,grip,bilateral,contact;
        double work,gross,net;
    }
    private static UnknownWorld.Entity box(
            Zero3BoxCrossingForensicV334Regression.State s){
        for(UnknownWorld.Entity e:s.p.world)
            if(e.isBilateralBox())return e;
        return null;
    }
    private static void record(
            Zero3BoxCrossingForensicV334Regression.State s,
            Metric m,float oldX,float oldY){
        m.steps++;
        m.cross+=MaterialGeometry.selfCrossings(s.p.skin)>0?1:0;
        UnknownWorld.Entity box=box(s);
        if(box!=null){
            m.wall+=SquareCollisionSolver.intersections(s.p.skin,box)>0?1:0;
            m.embedded+=Zero3ExactFaceTrialV336Regression.embedded(s)?1:0;
            if(box.bilateralHeldNow)m.bilateral++;
            boolean grip=false;
            for(float hold:box.boxGripHold)
                if(hold>=Morphology.GRIP_HOLD)grip=true;
            if(grip)m.grip++;
            if(SquareCollisionSolver.intersections(s.p.skin,box)==0){
                for(float sense:s.p.skin.touchSense)
                    if(sense>0.01f){m.contact++;break;}
            }
        }
        if(s.p.gel.invalid>0||
           !Float.isFinite(s.p.body.cx)||
           !Float.isFinite(s.p.body.cy)||
           !Float.isFinite(s.p.gel.work))
            m.invalid++;
        m.gross+=Math.hypot(s.p.body.cx-oldX,s.p.body.cy-oldY);
        m.work=s.p.gel.work;
    }
    private static boolean life(int ordinal,
              Zero3ContextAdhesionV319 frozenPretrained){
        boolean blind=ordinal>=600;
        Zero3BoxCrossingForensicV334Regression.State base=
            new Zero3BoxCrossingForensicV334Regression.State(ordinal,blind);
        Zero3BoxCrossingForensicV334Regression.State trial=
            new Zero3BoxCrossingForensicV334Regression.State(ordinal,blind);
        Zero3BoxCrossingForensicV334Regression.State faceOnly=
            new Zero3BoxCrossingForensicV334Regression.State(ordinal,blind);
        base.warmup();trial.warmup();faceOnly.warmup();
        base.spawn();trial.spawn();faceOnly.spawn();
        Zero3BoxCrossingForensicV334Regression.State linkOnly=
            new Zero3BoxCrossingForensicV334Regression.State(ordinal,blind);
        linkOnly.warmup();linkOnly.spawn();
        Zero3BoxCrossingForensicV334Regression.State guarded=
            new Zero3BoxCrossingForensicV334Regression.State(ordinal,blind);
        guarded.warmup();guarded.spawn();
        Metric original=new Metric(),candidate=new Metric(),
               face=new Metric(),link=new Metric(),selective=new Metric();
        int[] firstGeometry={-1,-1,-1,-1},firstOwners={-1,-1,-1,-1},
              firstSides={-1,-1,-1,-1},firstBilateral={-1,-1,-1,-1};
        String[] modes={"FACE_ONLY","FACE_PLUS_LINK","LINK_ONLY",
                        "SELECTIVE_PUSHOUT_PLUS_LINK"};
        long ep=(blind?3367000L:3361000L)+ordinal;
        Zero3AutonomousLoopV317.Encoder actor=
            new Zero3AutonomousLoopV317.Encoder(0x31720261017L);
        actor.resetEpisode();
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex(0f);
        Zero3LearningFoundation.Coordinator coordinator=
            new Zero3LearningFoundation.Coordinator(cortex);
        Zero3ContextAdhesionV319 head=frozenPretrained.frozenCopy();
        float[] last=null;
        double realActionMagnitude=0;
        try(Zero3MotorIntentV316.Authority authority=
                new Zero3MotorIntentV316.Authority(ep,OWNER)){
            for(int decision=0;decision<DECISIONS;decision++){
                Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
                    ep,decision,base.p.gel.ticks*(double)DT,true,
                    base.p.body,base.p.skin,base.p.gel,base.p.world,last);
                check(pre.intact(),"PRE provenance invalid");
                actor.before(pre);
                float[] intent4=actor.choose();
                coordinator.begin(new Zero3LearningFoundation.MotorIntent(
                    ep,decision,cortex.revision(),intent4[0],intent4[1],
                    intent4[2],intent4[3],SUBSTEPS*DT),
                    base.p.body,false,true,false);
                Zero3MotorIntentV316 issued=authority.issue(
                    OWNER,decision,0,336,intent4[0],intent4[1],
                    intent4[2],intent4[3],SUBSTEPS*DT,.35f);
                head.begin();
                float[] actual=null;
                for(int sub=0;sub<SUBSTEPS;sub++){
                    actual=coordinator.actuate(base.p.body,base.p.skin,DT);
                    head.actuate(actual,issued,base.p.body,base.p.skin,
                                 base.p.world);
                    actual=actual.clone();
                    for(float command:actual){
                        check(Float.isFinite(command),"nonfinite true command");
                        realActionMagnitude+=Math.abs(command);
                    }
                    float oldX=base.p.body.cx,oldY=base.p.body.cy,
                          cx=trial.p.body.cx,cy=trial.p.body.cy,
                          fx=faceOnly.p.body.cx,fy=faceOnly.p.body.cy,
                          lx=linkOnly.p.body.cx,ly=linkOnly.p.body.cy,
                          gx=guarded.p.body.cx,gy=guarded.p.body.cy;
                    float beforeWork=base.p.gel.work;
                    Zero3ExactFaceTrialV336Regression.cycle(base,0,actual);
                    Zero3ExactFaceTrialV336Regression.cycle(trial,2,actual);
                    Zero3ExactFaceTrialV336Regression.cycle(faceOnly,1,actual);
                    Zero3ExactFaceTrialV336Regression.cycle(linkOnly,3,actual);
                    Zero3ExactFaceTrialV336Regression.cycle(guarded,4,actual);
                    record(base,original,oldX,oldY);
                    record(trial,candidate,cx,cy);
                    record(faceOnly,face,fx,fy);
                    record(linkOnly,link,lx,ly);
                    record(guarded,selective,gx,gy);
                    Zero3BoxCrossingForensicV334Regression.State[] arms=
                        {faceOnly,trial,linkOnly,guarded};
                    int globalFrame=decision*SUBSTEPS+sub;
                    for(int m=0;m<4;m++){
                        Zero3BoxCrossingForensicV334Regression.State alt=arms[m];
                        if(firstGeometry[m]<0&&geometryDiffer(base,alt)){
                            firstGeometry[m]=globalFrame;
                            System.out.println("V337 FIRST_GEOMETRY id="+ordinal+
                                " arm="+modes[m]+" frame="+globalFrame+
                                " baseBox="+box(base).x+","+box(base).y+
                                " candidateBox="+box(alt).x+","+box(alt).y+
                                " baseCross="+MaterialGeometry.selfCrossings(base.p.skin)+
                                " alternateCross="+MaterialGeometry.selfCrossings(alt.p.skin));
                        }
                        if(firstOwners[m]<0&&gripTimerDiff(base,alt)){
                            firstOwners[m]=globalFrame;
                            System.out.println("V337 FIRST_OWNER_TIMER id="+ordinal+
                                " arm="+modes[m]+" frame="+globalFrame+
                                " baseTimers="+held(box(base))+
                                " candidateTimers="+held(box(alt))+
                                " baseSides="+faces(base)+
                                " candidateSides="+faces(alt));
                        }
                        if(firstSides[m]<0&&!faces(base).equals(faces(alt))){
                            firstSides[m]=globalFrame;
                            System.out.println("V337 FIRST_FACE_HISTOGRAM id="+ordinal+
                                " arm="+modes[m]+" frame="+globalFrame+
                                " baseSides="+faces(base)+
                                " candidateSides="+faces(alt)+
                                " baseBoxVel="+box(base).vx+","+box(base).vy+
                                " alternateBoxVel="+box(alt).vx+","+box(alt).vy);
                        }
                        if(firstBilateral[m]<0&&
                           box(base).bilateralHeldNow!=box(alt).bilateralHeldNow){
                            firstBilateral[m]=globalFrame;
                            System.out.println("V337 FIRST_BILATERAL_DIVERGENCE id="+ordinal+
                                " arm="+modes[m]+" frame="+globalFrame+
                                " original="+box(base).bilateralHeldNow+
                                " alternate="+box(alt).bilateralHeldNow+
                                " originalSides="+faces(base)+
                                " alternateSides="+faces(alt)+
                                " originalBoxXY="+box(base).x+","+box(base).y+
                                " alternateBoxXY="+box(alt).x+","+box(alt).y);
                        }
                    }
                    float measured=Math.max(0f,base.p.gel.work-beforeWork);
                    coordinator.observe(base.p.body,DT,measured,
                        Math.max(0,base.p.gel.lastMuscleReactionImpulse),
                        false,false);
                    head.observe(base.p.skin,box(base),measured);
                }
                check(actual!=null,"missing efference");
                head.end();
                Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
                    ep,decision+1,base.p.gel.ticks*(double)DT,false,
                    base.p.body,base.p.skin,base.p.gel,base.p.world,actual);
                check(pre.intact()&&post.intact()&&
                      post.solverTicks==pre.solverTicks+SUBSTEPS,
                      "bad sensor causal 12-step contract");
                actor.after(post,false);
                coordinator.finish(); // heldout, zero cortex update
                last=actual;
            }
        }
        check(original.steps==DECISIONS*SUBSTEPS&&
              candidate.steps==DECISIONS*SUBSTEPS&&
              face.steps==DECISIONS*SUBSTEPS&&
              link.steps==DECISIONS*SUBSTEPS&&
              selective.steps==DECISIONS*SUBSTEPS,
              "incomplete required physics");
        check(realActionMagnitude>0.1,
              "no truly actuated cortex output observed");
        boolean topology=candidate.cross==0&&candidate.wall==0&&
                         candidate.embedded==0&&candidate.invalid==0;
        boolean cost=candidate.work<=original.work*1.1+1e-6;
        boolean bilateralEvidence=original.bilateral>0;
        boolean gripGuard=!bilateralEvidence||
                candidate.bilateral>=.80*original.bilateral;
        String bilateralStatus=bilateralEvidence?(gripGuard?"PASS":"FAIL"):
                                               "NOT_TESTED";
        System.out.printf(Locale.US,
            "V336 ACTUATED_RESULT id=%d decisions=%d framesPerArm=%d "+
            "actionL1=%.8f baselineCross=%d trialCross=%d "+
            "baselineWall=%d trialWall=%d trialEmbedded=%d trialInvalid=%d "+
            "baselineWork=%.9f trialWork=%.9f "+
            "baselineGrip=%d trialGrip=%d "+
            "baselineBilateral=%d trialBilateral=%d "+
            "topology=%s workGate=%s bilateralGate=%s "+
            "faceOnlyCross=%d faceOnlyWork=%.9f faceOnlyGrip=%d "+
            "faceOnlyBilateral=%d faceOnlyWall=%d "+
            "candidateAutonomousPolicy=false G3_BLOCKED G5_BLOCKED NO_OTA%n",
            ordinal,DECISIONS,candidate.steps,realActionMagnitude,
            original.cross,candidate.cross,original.wall,candidate.wall,
            candidate.embedded,candidate.invalid,original.work,candidate.work,
            original.grip,candidate.grip,original.bilateral,
            candidate.bilateral,topology?"PASS":"FAIL",
            cost?"PASS":"FAIL",bilateralStatus,
            face.cross,face.work,face.grip,face.bilateral,face.wall);
        boolean linkTopology=link.cross==0&&link.wall==0&&
            link.embedded==0&&link.invalid==0;
        boolean linkWork=link.work<=original.work*1.10+1e-6;
        boolean linkGrip=bilateralEvidence&&
            link.bilateral>=.80*original.bilateral;
        System.out.printf(Locale.US,
            "V337 FOUR_ARM id=%d frames=%d originalCross=%d "+
            "faceCross=%d combinedCross=%d linkCross=%d "+
            "originalBilateral=%d faceBilateral=%d "+
            "combinedBilateral=%d linkBilateral=%d "+
            "originalGenericGrip=%d linkGenericGrip=%d "+
            "originalWork=%.9f linkWork=%.9f "+
            "linkSolidWall=%d linkEmbedded=%d linkInvalid=%d "+
            "linkTopology=%s linkWorkGate=%s linkBilateralGate=%s "+
            "G3_BLOCKED G5_BLOCKED NO_OTA%n",
            ordinal,link.steps,original.cross,face.cross,candidate.cross,
            link.cross,original.bilateral,face.bilateral,candidate.bilateral,
            link.bilateral,original.grip,link.grip,original.work,link.work,
            link.wall,link.embedded,link.invalid,linkTopology?"PASS":"FAIL",
            linkWork?"PASS":"FAIL",
            !bilateralEvidence?"NOT_TESTED":linkGrip?"PASS":"FAIL");
        boolean selectiveTopology=selective.cross==0&&
             selective.wall==0&&selective.embedded==0&&
             selective.invalid==0;
        boolean selectiveWork=selective.work<=original.work*1.10+1e-6;
        boolean selectiveBilateral=bilateralEvidence&&
             selective.bilateral>=.80*original.bilateral;
        System.out.printf(Locale.US,
            "V337 SELECTIVE_RESULT id=%d frames=%d "+
            "baselineCross=%d linkCross=%d selectiveCross=%d "+
            "baselineBilateral=%d linkBilateral=%d "+
            "selectiveBilateral=%d baselineGenericGrip=%d "+
            "selectiveGenericGrip=%d workBaseline=%.9f "+
            "workSelective=%.9f throughWall=%d embedded=%d invalid=%d "+
            "topology=%s workGate=%s bilateralGate=%s "+
            "G3_BLOCKED G5_BLOCKED NO_OTA%n",
            ordinal,selective.steps,original.cross,link.cross,selective.cross,
            original.bilateral,link.bilateral,selective.bilateral,
            original.grip,selective.grip,original.work,selective.work,
            selective.wall,selective.embedded,selective.invalid,
            selectiveTopology?"PASS":"FAIL",selectiveWork?"PASS":"FAIL",
            !bilateralEvidence?"NOT_TESTED":
               selectiveBilateral?"PASS":"FAIL");
        return selectiveTopology&&selectiveWork&&selectiveBilateral;
    }
    private static boolean geometryDiffer(
       Zero3BoxCrossingForensicV334Regression.State a,
       Zero3BoxCrossingForensicV334Regression.State b){
        if(Float.floatToIntBits(box(a).x)!=Float.floatToIntBits(box(b).x)||
           Float.floatToIntBits(box(a).y)!=Float.floatToIntBits(box(b).y))return true;
        for(int i=0;i<Morphology.POINTS;i++)
            if(Float.floatToIntBits(a.p.skin.x[i])!=
                       Float.floatToIntBits(b.p.skin.x[i])||
               Float.floatToIntBits(a.p.skin.y[i])!=
                       Float.floatToIntBits(b.p.skin.y[i]))return true;
        return false;
    }
    private static boolean gripTimerDiff(
       Zero3BoxCrossingForensicV334Regression.State a,
       Zero3BoxCrossingForensicV334Regression.State b){
        for(int i=0;i<Morphology.POINTS;i++)
            if(Float.floatToIntBits(box(a).boxGripHold[i])!=
               Float.floatToIntBits(box(b).boxGripHold[i]))return true;
        return false;
    }
    private static int held(UnknownWorld.Entity box){
        int n=0;
        for(float h:box.boxGripHold)
            if(h>=Morphology.GRIP_HOLD)n++;
        return n;
    }
    private static String faces(
         Zero3BoxCrossingForensicV334Regression.State s){
        int[] counts=new int[4];
        UnknownWorld.Entity e=box(s);
        for(int i=0;i<Morphology.POINTS;i++){
            float h=e.boxGripHold[i];
            if(h<Morphology.GRIP_HOLD||
               s.p.skin.gripCommand[i]<=Morphology.GRIP_THRESHOLD)continue;
            float dx=s.p.skin.x[i]-e.x,dy=s.p.skin.y[i]-e.y;
            float[] dd={Math.abs(dx+SquareGripPuzzle.HALF),
                        Math.abs(dx-SquareGripPuzzle.HALF),
                        Math.abs(dy+SquareGripPuzzle.HALF),
                        Math.abs(dy-SquareGripPuzzle.HALF)};
            int best=0;
            for(int k=1;k<4;k++)if(dd[k]<dd[best])best=k;
            if(dd[best]<=3f)counts[best]++;
        }
        return Arrays.toString(counts);
    }
    public static void main(String[] args){
        Zero3ContextAdhesionV319 frozen=
            Zero3FiveArmAblationV333Regression.gripPretraining();
        int robustValid=0;
        for(int id:CASES)if(life(id,frozen))robustValid++;
        System.out.println("V337 PHYSICS_GUARD safeCases="+robustValid+
            "/"+CASES.length+" SELECTIVE_CANDIDATE_PROMOTION="+
            (robustValid==CASES.length?
             "NOT_AUTHORIZED_NEEDS_NEW_HOLDOUT":"REJECTED")+
            " G3_BLOCKED G5_BLOCKED NO_OTA");
        System.out.println("V336 SCIENCE_GUARD robustValidCases="+
            robustValid+"/"+CASES.length+
            " candidatePromotion="+(robustValid==CASES.length?
              "NOT_AUTHORIZED_NEEDS_INDEPENDENT_BLIND":"REJECTED")+
            " 80_PERCENT_BILATERAL_REQUIRED=true");
        System.out.println("V337 ACTUATED_FINAL cases=2 "+
            "decisionsPerCase=168 framesPerCase=2016 "+
            "realV317AndV319Actuator=true matchedTape=true "+
            "NOT_AN_AUTONOMOUS_TRAINED_VS_FROZEN_BENCHMARK "+
            "G3_BLOCKED G5_BLOCKED NO_MERGE NO_OTA");
    }
}
