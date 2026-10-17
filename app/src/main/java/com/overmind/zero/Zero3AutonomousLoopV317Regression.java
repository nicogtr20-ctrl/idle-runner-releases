package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Matched holdout of actual actor-selected 0.30s intentions, with and
 * without BOTH learning mechanisms. No target/reward labels in the actor.
 *
 * This is an engineering probe of an E3 loop, NOT a G3 pass unless every
 * preregistered quality condition succeeds on independent scenes.
 */
public final class Zero3AutonomousLoopV317Regression {
    static final float DT=Zero3AutonomousLoopV317.DT;
    static final int TRAIN_EPISODES=16,TRAIN_DECISIONS=128;
    static final int EVAL_EPISODES=8,EVAL_DECISIONS=96;
    static final int WARMUP=96;
    static final int FREE=0,BOX=1,DISK=2,FAR=3;
    private Zero3AutonomousLoopV317Regression(){}
    static void check(boolean ok,String problem){
        if(!ok)throw new AssertionError("ZERO3 V317 "+problem);
    }
    static final class Group {
        int lives,decisions,gelFrames,contacts,grips,
            adhesiveSectors,adhesiveAndContact,nonFinite,
            directionIntents,unintendedMutation,physicalFailures;
        double sumAgreement,sumWork,sumUseful,sumGross,sumNet,
               predictionMse,persistenceMse;
        final double[] familyAgreement=new double[4],
                       familyWork=new double[4],
                       familyUseful=new double[4];
        final int[] familyDecisions=new int[4];
        void add(int mode,Zero3AutonomousLoopV317.Decision d) {
            decisions++;
            gelFrames+=d.realPhysicsSteps;
            contacts+=d.realContacts;grips+=d.realGrip;
            adhesiveSectors+=d.adhesiveCommandSectors;
            adhesiveAndContact+=d.adhesiveCommandAtContactSites;
            if(d.intent.desiredEffort>.01f&&
               Math.hypot(d.intent.directionX,d.intent.directionY)>.9)
                directionIntents++;
            check(d.before.intact()&&d.after.intact()&&
                  Float.isFinite(d.displacementX)&&
                  Float.isFinite(d.displacementY)&&
                  Float.isFinite(d.work)&&d.realPhysicsSteps==
                      Zero3AutonomousLoopV317.STEPS,
                  "invalid physical intent-to-outcome experience");
            check(d.intent.decisionId>=0&&d.intent.intact()&&
                  d.actionEvidenceHash!=0,
                  "action sequence not owned by chosen intention");
            sumAgreement+=d.alignment;
            sumWork+=d.work;
            sumGross+=d.gross;
            sumNet+=Math.hypot(d.displacementX,d.displacementY);
            double useful=Math.max(0,
                d.displacementX*d.intent.directionX+
                d.displacementY*d.intent.directionY);
            sumUseful+=useful;
            predictionMse+=d.predictionMse();
            persistenceMse+=d.persistenceMse();
            familyAgreement[mode]+=d.alignment;
            familyWork[mode]+=d.work;
            familyUseful[mode]+=useful;
            familyDecisions[mode]++;
        }
        String report(){
            return String.format(Locale.US,
                "episodes=%d autonomousDecisions=%d realGelSteps=%d "+
                "directionIntents=%d touchPatchEvents=%d gripPatchEvents=%d "+
                "adhesiveMotorSectorFrames=%d adhesionAndContactPatchFrames=%d "+
                "meanIntentAlignment=%.8f netDistanceSum=%.5f "+
                "grossDistanceSum=%.5f workSum=%.8f usefulSum=%.5f "+
                "workPerUseful=%.8f recurrentPredictionMSE=%.9f "+
                "persistenceMSE=%.9f",
                lives,decisions,gelFrames,directionIntents,
                contacts,grips,adhesiveSectors,adhesiveAndContact,
                sumAgreement/Math.max(1,decisions),
                sumNet,sumGross,sumWork,sumUseful,
                sumWork/(1.+sumUseful),
                predictionMse/Math.max(1,decisions),
                persistenceMse/Math.max(1,decisions));
        }
    }
    static Zero3MotorImpulseResponseRegression.Scenario scene(
                 int id,boolean evaluation){
        int direction=(id+1)%4;
        float angle=evaluation?(float)(.13 + ((id%8)*.29-.94)):
                               (float)((id*0.23)-.83);
        int condition=(id+1)%4;
        return new Zero3MotorImpulseResponseRegression.Scenario(
            direction,angle,condition);
    }
    static ArrayList<UnknownWorld.Entity> populate(
             Zero3MotorImpulseResponseRegression.Scenario sc,int mode){
        ArrayList<UnknownWorld.Entity> obj=new ArrayList<>();
        if(mode!=FREE){
            float dx=mode==FAR?350:mode==BOX?47.5f:48f;
            obj.add(new UnknownWorld.Entity(sc.body.cx+dx,sc.body.cy,
                                            2,mode==DISK?2:5));
        }
        return obj;
    }
    static void warm(Zero3MotorImpulseResponseRegression.Scenario sc){
        float[] neutral=new float[64];
        for(int i=0;i<WARMUP;i++){
            sc.gel.step(sc.body,sc.skin,neutral,DT);
            check(sc.gel.invalid==0,"invalid physically neutral warmup");
        }
    }
    static void life(Group summary,Zero3AutonomousLoopV317.Loop loop,
                     int id,int length,boolean evaluation) {
        int mode=id%4;
        Zero3MotorImpulseResponseRegression.Scenario sc=scene(id,evaluation);
        warm(sc);
        ArrayList<UnknownWorld.Entity> world=populate(sc,mode);
        float[] last=null;
        long episode=(evaluation?100000L:20000L)+id;
        try(Zero3MotorIntentV316.Authority owner=
            new Zero3MotorIntentV316.Authority(episode,Zero3AutonomousLoopV317.OWNER)){
            loop.encoder.resetEpisode();
            for(int n=0;n<length;n++){
                long revision=loop.cortex.revision();
                Zero3AutonomousLoopV317.Decision d=loop.act(owner,episode,
                    n,sc,world,mode,last);
                check(d.before.solverTicks+Zero3AutonomousLoopV317.STEPS==
                      d.after.solverTicks,
                      "outcome did not emerge from exactly 12 real solver ticks");
                // This is one validated upper-level intention, backed by
                // 12 physical actuator tapes, not 12 separate decisions.
                last=d.after.lastExecuted64();
                summary.add(mode,d);
                if(evaluation&&loop.cortex.revision()!=revision)
                    summary.unintendedMutation++;
            }
            check(owner.nextDecision()==length,
                  "actor issued wrong number of intentions");
        }
        summary.lives++;
    }
    static Group evaluate(Zero3AutonomousLoopV317.Encoder model,
            Zero3LearningFoundation.AdaptiveCortex cortex,int seedOffset){
        // Evaluation uses an untuned frozen copy of each model's weights.
        Zero3AutonomousLoopV317.Loop tester=new Zero3AutonomousLoopV317.Loop(
            model.evaluationClone(),cortex.forkForEvaluation(),false);
        Group result=new Group();
        for(int i=0;i<EVAL_EPISODES;i++)
            life(result,tester,100+seedOffset+i,EVAL_DECISIONS,true);
        check(tester.encoder.updates()==0&&tester.cortex.updates()==0,
              "holdout accidentally trained or changed weights");
        check(result.unintendedMutation==0,
              "cortex revision changed during held-out evaluation");
        return result;
    }
    public static void main(String[] args){
        final long actorInit=0x31720261017L;
        Zero3AutonomousLoopV317.Encoder learningActor=
            new Zero3AutonomousLoopV317.Encoder(actorInit);
        Zero3AutonomousLoopV317.Encoder frozenActor=
            new Zero3AutonomousLoopV317.Encoder(actorInit);
        Zero3LearningFoundation.AdaptiveCortex trainCortex=
            new Zero3LearningFoundation.AdaptiveCortex(.013f);
        Zero3LearningFoundation.AdaptiveCortex initialCortex=
            new Zero3LearningFoundation.AdaptiveCortex(0f);
        check(Arrays.equals(trainCortex.weights(),initialCortex.weights()),
              "A/B cortex did not start at exactly identical weights");
        Zero3AutonomousLoopV317.Loop learningLoop=
            new Zero3AutonomousLoopV317.Loop(learningActor,trainCortex,true);
        Group train=new Group();
        for(int i=0;i<TRAIN_EPISODES;i++)
            life(train,learningLoop,i,TRAIN_DECISIONS,false);
        check(train.lives==TRAIN_EPISODES&&
              train.decisions==TRAIN_EPISODES*TRAIN_DECISIONS&&
              train.gelFrames==TRAIN_EPISODES*TRAIN_DECISIONS*
                               Zero3AutonomousLoopV317.STEPS,
              "did not experience 2048 actor-chosen real motor decisions");
        double diff=0;
        float[] trained=trainCortex.weights(),before=initialCortex.weights();
        for(int i=0;i<trained.length;i++)
            diff+=(trained[i]-before[i])*(trained[i]-before[i]);
        // The following EVAL test uses new held-out scene seeds and poses,
        // and NEVER feeds outcomes back to weight updates.
        Group learnt=evaluate(learningActor,trainCortex,0);
        Group control=evaluate(frozenActor,initialCortex,0);
        check(learnt.decisions==EVAL_EPISODES*EVAL_DECISIONS&&
              control.decisions==learnt.decisions,
              "unpaired matched holdout");
        check(learnt.directionIntents>0&&control.directionIntents>0,
              "actor never selected a nonzero voluntary intention");
        double trainedAlign=learnt.sumAgreement/learnt.decisions;
        double frozenAlign=control.sumAgreement/control.decisions;
        int familyWins=0;
        for(int k=0;k<4;k++){
            double own=learnt.familyAgreement[k]/
                       Math.max(1,learnt.familyDecisions[k]);
            double baseline=control.familyAgreement[k]/
                       Math.max(1,control.familyDecisions[k]);
            boolean win=own>baseline+0.1*Math.abs(baseline);
            if(win)familyWins++;
            System.out.printf(Locale.US,
                "ZERO3 V317 FAMILY=%d learnedAlignment=%.8f "+
                "frozenAlignment=%.8f learnedWork=%.8f "+
                "frozenWork=%.8f learnedUseful=%.5f frozenUseful=%.5f "+
                "improvedBy10Pct=%s%n",k,own,baseline,
                learnt.familyWork[k],control.familyWork[k],
                learnt.familyUseful[k],control.familyUseful[k],win);
        }
        boolean learningActuallyOccurred=learningActor.updates()>0&&
            trainCortex.updates()>0&&diff>1e-12&&
            learningActor.learningL2()>1e-12f;
        boolean improves=trainedAlign>frozenAlign+.1*Math.abs(frozenAlign);
        boolean predicts=learnt.predictionMse<=learnt.persistenceMse;
        boolean efficiency=learnt.sumWork/(1+learnt.sumUseful) <=
                           control.sumWork/(1+control.sumUseful);
        boolean scientific=learningActuallyOccurred&&improves&&predicts&&
                           efficiency&&familyWins>=3;
        System.out.println("ZERO3 V317 TRAIN "+train.report());
        System.out.println("ZERO3 V317 BLIND LEARNED "+learnt.report());
        System.out.println("ZERO3 V317 BLIND FROZEN "+control.report());
        System.out.printf(Locale.US,
            "ZERO3 V317 SUMMARY realActorChosenTrainDecisions=%d "+
            "trainRealGelSteps=%d blindEvalDecisionsPerArm=%d "+
            "blindRealGelStepsTotal=%d recurrentTrainingUpdates=%d "+
            "cortexTrainingUpdates=%d cortexWeightChangeL2=%.10f "+
            "encoderChangeL2=%.10f matchedSceneFamiliesWon=%d "+
            "learningOccurred=%s holdoutImproved=%s "+
            "holdoutPredictionBeatsPersistence=%s costNotWorse=%s "+
            "scientificMotorGate=%s autonomyChooseIntent=true "+
            "labNoExternalGoals=true holdoutTraining=false "+
            "NO_GAIT G3_BLOCKED G5_BLOCKED NO_OTA%n",
            train.decisions,train.gelFrames,EVAL_EPISODES*EVAL_DECISIONS,
            learnt.gelFrames+control.gelFrames,
            learningActor.updates(),trainCortex.updates(),Math.sqrt(diff),
            Math.sqrt(learningActor.learningL2()),familyWins,
            learningActuallyOccurred,improves,predicts,efficiency,
            scientific?"PASS":"FAIL");
    }
}
