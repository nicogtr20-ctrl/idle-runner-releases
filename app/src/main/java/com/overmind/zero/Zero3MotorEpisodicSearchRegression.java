package com.overmind.zero;

import java.util.Locale;
import java.util.Random;

/**
 * G3 controlled physical experiment, NOT a gameplay gait and NOT a V3 release.
 * Each candidate is an autonomous random LOCAL parameter change evaluated on
 * a freshly reset REAL gel simulation. The operator prescribes only desired
 * motor intent (as with the other lab tests); the physical solver chooses
 * consequences and selection is based exclusively on measured outcome.
 *
 * Repeated life-like trials justify episodic candidate/rollback as a possible
 * complement to noisy online REINFORCE. Actual online integration remains TBD.
 */
public final class Zero3MotorEpisodicSearchRegression {
    private static final float[][] GOALS={{1,0},{0,1},{-1,0},{0,-1}};
    private static final int CANDIDATES=60, TRAIN_DECISIONS=24, TEST_DECISIONS=160;
    private static double objective(Zero3MotorRetentionRegression.Measures m){
        // Only physically observed signed displacement, thrash and work.
        return m.forward-.13*Math.max(0,m.gross-m.forward)-2*m.work;
    }
    public static void main(String[] args){
        Random rng=new Random(0x30E15A1L);
        double totalVirgin=0,totalTrained=0;
        int improved=0;
        for(int dir=0;dir<GOALS.length;dir++){
            float dx=GOALS[dir][0],dy=GOALS[dir][1];
            Zero3LearningFoundation.AdaptiveCortex current=
                new Zero3LearningFoundation.AdaptiveCortex();
            double best=objective(Zero3MotorRetentionRegression.session(
                current.forkForEvaluation(),dx,dy,TRAIN_DECISIONS,false));
            int accepted=0;
            for(int iteration=0;iteration<CANDIDATES;iteration++){
                int sector=rng.nextInt(16),channel=rng.nextInt(4);
                float delta=(rng.nextBoolean()?1f:-1f)*(.15f+rng.nextFloat()*.55f);
                Zero3LearningFoundation.AdaptiveCortex candidate=
                    current.forkCandidate(sector,channel,delta);
                double result=objective(Zero3MotorRetentionRegression.session(
                    candidate.forkForEvaluation(),dx,dy,TRAIN_DECISIONS,false));
                if(result>best+.05){
                    best=result;current=candidate;accepted++;
                }
            }
            Zero3MotorRetentionRegression.Measures virgin=
                Zero3MotorRetentionRegression.session(
                    new Zero3LearningFoundation.AdaptiveCortex(),
                    dx,dy,TEST_DECISIONS,false);
            Zero3MotorRetentionRegression.Measures learned=
                Zero3MotorRetentionRegression.session(
                    current.forkForEvaluation(),dx,dy,TEST_DECISIONS,false);
            if(learned.forward>virgin.forward)improved++;
            totalVirgin+=virgin.forward;totalTrained+=learned.forward;
            System.out.printf(Locale.US,
                "ZERO3 EPISODIC PHY-SEARCH direction=[%.0f %.0f] candidates=%d accepted=%d trainScore=%.3f EVAL-VIRGIN{%s} EVAL-LEARNED{%s}%n",
                dx,dy,CANDIDATES,accepted,best,virgin,learned);
        }
        System.out.printf(Locale.US,
            "ZERO3 EPISODIC PHY-SEARCH SUMMARY improvedDirections=%d/4 freshBodyTestForward virgin=%.3f learned=%.3f; trained and evaluated on SAME initial-body family, NO heldout scene claim%n",
            improved,totalVirgin,totalTrained);
    }
}
