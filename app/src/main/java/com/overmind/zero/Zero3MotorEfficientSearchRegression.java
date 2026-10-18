package com.overmind.zero;

import java.util.Locale;
import java.util.Random;

/**
 * ZERO3 exploratory alternative to online score-function motor updates:
 * reward-and-cost-aware candidate/rollback on the ACTUAL physical solver.
 *
 * An external fixed motor intent is used ONLY in this lab to isolate whether
 * the cortex can improve one skill; there is NO predefined gait, heading
 * sequence, gem position, artificial acceleration or training from imagined
 * physics. ZERO's autonomous actor does not use this class.
 *
 * Every proposed modification is LOCAL, then evaluated from a fresh physical
 * state in TWO training poses. 45 / -45 degree poses are never used for
 * selecting candidates; they're independent pose conditions for diagnosis.
 *
 * Research result is NOT a live Android policy nor proof of lifelong learning.
 */
public final class Zero3MotorEfficientSearchRegression {
    private static final float[][] INTENTS={{1,0},{0,1},{-1,0},{0,-1}};
    private static final float TRAIN_ROTATED=(float)(Math.PI/2);
    private static final float TEST_A=(float)(Math.PI/4);
    private static final float TEST_B=(float)(-Math.PI/4);
    private static final int PROPOSALS=64,TRAIN_DECISIONS=24,TEST_DECISIONS=160;

    private static double utility(Zero3MotorRetentionRegression.Measures m){
        // Protect against massive internal thrashing and high active work.
        // These are metrics measured by the TRUE material integration.
        return m.forward-.34*Math.max(0,m.gross-Math.max(0,m.forward))-90*m.work;
    }
    private static final class Pair{
        final Zero3MotorRetentionRegression.Measures original,rotated;
        final double score;
        Pair(Zero3LearningFoundation.AdaptiveCortex brain,float x,float y){
            original=Zero3MotorRetentionRegression.session(
                brain.forkForEvaluation(),x,y,TRAIN_DECISIONS,false);
            rotated=Zero3MotorRetentionRegression.session(
                brain.forkForEvaluation(),x,y,TRAIN_DECISIONS,false,TRAIN_ROTATED);
            score=utility(original)+utility(rotated);
        }
        boolean saferThan(Pair earlier){
            return original.forward>=earlier.original.forward-2.0&&
                   rotated.forward>=earlier.rotated.forward-2.0&&
                   original.work<=Math.max(.001,earlier.original.work)*1.2+0.0005&&
                   rotated.work<=Math.max(.001,earlier.rotated.work)*1.2+0.0005;
        }
    }
    private static String readable(Zero3MotorRetentionRegression.Measures m){
        return String.format(Locale.US,"signed=%.3f gross=%.3f work=%.5f",
            m.forward,m.gross,m.work);
    }
    public static void main(String[] args){
        Random rng=new Random(0x3016A5C2L);
        int improvements=0;
        double totalVirgin=0,totalLearned=0,baseCost=0,learnedCost=0;
        for(float[] direction:INTENTS){
            final float x=direction[0],y=direction[1];
            Zero3LearningFoundation.AdaptiveCortex champion=
                new Zero3LearningFoundation.AdaptiveCortex();
            Pair current=new Pair(champion,x,y);
            int accepted=0,attempted=0;
            for(int i=0;i<PROPOSALS;i++){
                int sector=rng.nextInt(16),channel=rng.nextInt(4);
                float delta=(rng.nextBoolean()?1f:-1f)*
                    (.04f+rng.nextFloat()*.36f);
                Zero3LearningFoundation.AdaptiveCortex candidate=
                    champion.forkCandidate(sector,channel,delta);
                Pair trial=new Pair(candidate,x,y);
                attempted++;
                if(trial.saferThan(current)&&trial.score>current.score+.035){
                    champion=candidate;
                    current=trial;
                    accepted++;
                }
            }
            // All 64 proposals were selected solely on {0°, 90°};
            // {+45°, -45°} NEVER contributes to training/selection.
            Zero3MotorRetentionRegression.Measures virginA=
                Zero3MotorRetentionRegression.session(
                    new Zero3LearningFoundation.AdaptiveCortex(),x,y,TEST_DECISIONS,false,TEST_A);
            Zero3MotorRetentionRegression.Measures learnedA=
                Zero3MotorRetentionRegression.session(
                    champion.forkForEvaluation(),x,y,TEST_DECISIONS,false,TEST_A);
            Zero3MotorRetentionRegression.Measures virginB=
                Zero3MotorRetentionRegression.session(
                    new Zero3LearningFoundation.AdaptiveCortex(),x,y,TEST_DECISIONS,false,TEST_B);
            Zero3MotorRetentionRegression.Measures learnedB=
                Zero3MotorRetentionRegression.session(
                    champion.forkForEvaluation(),x,y,TEST_DECISIONS,false,TEST_B);
            double virginUtility=utility(virginA)+utility(virginB);
            double learnedUtility=utility(learnedA)+utility(learnedB);
            boolean improved=learnedUtility>virginUtility&&
                learnedA.work<=virginA.work*1.2+.001&&
                learnedB.work<=virginB.work*1.2+.001;
            if(improved)improvements++;
            totalVirgin+=virginUtility;totalLearned+=learnedUtility;
            baseCost+=virginA.work+virginB.work;
            learnedCost+=learnedA.work+learnedB.work;
            System.out.printf(Locale.US,
                "ZERO3 ROBUST MOTOR SEARCH goal=[%.0f %.0f] proposals=%d accepted=%d"+
                " selectedTrainScore=%.3f HELDOUT_45{virgin{%s} learned{%s}}"+
                " HELDOUT_NEG45{virgin{%s} learned{%s}}"+
                " heldoutVirginUtility=%.3f heldoutLearnedUtility=%.3f"+
                " improvedWithinCost=%s%n",
                x,y,attempted,accepted,current.score,
                readable(virginA),readable(learnedA),
                readable(virginB),readable(learnedB),
                virginUtility,learnedUtility,Boolean.toString(improved));
        }
        System.out.printf(Locale.US,
            "ZERO3 ROBUST MOTOR SEARCH SUMMARY trainingPhysicalDecisions=%d"+
            " directionWiseImprovedWithinCost=%d/4 blindPoseUtilityVirgin=%.3f"+
            " blindPoseUtilityLearned=%.3f blindPoseCostVirgin=%.4f"+
            " blindPoseCostLearned=%.4f; no production promotion;"+
            " independent pose is NOT independent world/seed%n",
            4*PROPOSALS*2*TRAIN_DECISIONS,
            improvements,totalVirgin,totalLearned,baseCost,learnedCost);
    }
}
