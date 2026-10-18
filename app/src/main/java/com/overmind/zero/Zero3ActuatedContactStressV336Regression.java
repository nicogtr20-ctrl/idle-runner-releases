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
public final class Zero3ActuatedContactStressV336Regression {
    private static final int[] CASES={337,645};
    private static final float DT=.025f;
    private static final int DECISIONS=168, SUBSTEPS=12;
    private static final String OWNER=Zero3AutonomousLoopV317.OWNER;
    private Zero3ActuatedContactStressV336Regression(){}

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
        Metric original=new Metric(),candidate=new Metric(),
               face=new Metric();
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
                          fx=faceOnly.p.body.cx,fy=faceOnly.p.body.cy;
                    float beforeWork=base.p.gel.work;
                    Zero3ExactFaceTrialV336Regression.cycle(base,0,actual);
                    Zero3ExactFaceTrialV336Regression.cycle(trial,2,actual);
                    Zero3ExactFaceTrialV336Regression.cycle(faceOnly,1,actual);
                    record(base,original,oldX,oldY);
                    record(trial,candidate,cx,cy);
                    record(faceOnly,face,fx,fy);
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
              face.steps==DECISIONS*SUBSTEPS,
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
        return topology&&cost&&gripGuard&&bilateralEvidence;
    }
    public static void main(String[] args){
        Zero3ContextAdhesionV319 frozen=
            Zero3FiveArmAblationV333Regression.gripPretraining();
        int robustValid=0;
        for(int id:CASES)if(life(id,frozen))robustValid++;
        System.out.println("V336 SCIENCE_GUARD robustValidCases="+
            robustValid+"/"+CASES.length+
            " candidatePromotion="+(robustValid==CASES.length?
              "NOT_AUTHORIZED_NEEDS_INDEPENDENT_BLIND":"REJECTED")+
            " 80_PERCENT_BILATERAL_REQUIRED=true");
        System.out.println("V336 ACTUATED_FINAL cases=2 "+
            "decisionsPerCase=168 framesPerCase=2016 "+
            "realV317AndV319Actuator=true matchedTape=true "+
            "NOT_AN_AUTONOMOUS_TRAINED_VS_FROZEN_BENCHMARK "+
            "G3_BLOCKED G5_BLOCKED NO_MERGE NO_OTA");
    }
}
