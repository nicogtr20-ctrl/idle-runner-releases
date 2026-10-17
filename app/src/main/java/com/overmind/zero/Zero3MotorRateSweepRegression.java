package com.overmind.zero;

import java.util.Locale;

/** Registered research-grid only: same starts, physics, seeds, requested goals.
 * Scores are holdout runs without probe; NO auto-promotion from this grid.
 * Do not choose hyperparameters on the same data later reported as final test.
 */
public final class Zero3MotorRateSweepRegression {
    private static final float[] RATES={0f,.013f,.04f,.10f,.22f};
    private static final float[][] GOALS={{1,0},{0,1},{-1,0},{0,-1}};
    public static void main(String[] args){
        System.out.println("ZERO3 MOTOR RATE GRID (EXPLORATORY/TUNING, NOT BLIND HOLDOUT)");
        for(float rate:RATES){
            double summed=0,work=0;
            int positive=0;
            for(float[] dir:GOALS){
                Zero3LearningFoundation.AdaptiveCortex cortex=
                    new Zero3LearningFoundation.AdaptiveCortex(rate);
                Zero3MotorRetentionRegression.session(cortex,dir[0],dir[1],400,true);
                Zero3LearningFoundation.AdaptiveCortex evaluation=cortex.forkForEvaluation();
                Zero3MotorRetentionRegression.Measures outcome=
                    Zero3MotorRetentionRegression.session(evaluation,dir[0],dir[1],160,false);
                summed+=outcome.forward;
                work+=outcome.work;
                if(outcome.forward>0)positive++;
                System.out.printf(Locale.US,
                   "ZERO3 RATE %.3f direction=[%.0f,%.0f] test-forward=%.4f gross=%.4f work=%.6f trainingUpdates=%d%n",
                   rate,dir[0],dir[1],outcome.forward,outcome.gross,outcome.work,cortex.updates());
            }
            System.out.printf(Locale.US,
                "ZERO3 RATE SUMMARY lr=%.3f sum-signed-forward=%.4f positiveDirections=%d/4 summedWork=%.6f%n",
                rate,summed,positive,work);
        }
        System.out.println("ZERO3 RATE GRID exploratory; winner if any requires independent unseen seeds and scenes");
    }
}
