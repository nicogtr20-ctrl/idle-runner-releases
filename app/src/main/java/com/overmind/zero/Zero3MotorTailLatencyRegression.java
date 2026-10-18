package com.overmind.zero;

import java.util.Locale;

/**
 * ZERO3 diagnostic: trace the ACTUAL delayed effect of a single local muscle
 * pulse for up to 360 post-pulse frames using matched independently stepped
 * physical organisms. No output is learned, no scripted gait is injected.
 *
 * A gain after 48 settle frames is not necessarily the final effect. This
 * experiment measures the unobserved temporal tail / sign reversal instead
 * of pretending that a one-step reward gives exact muscle credit.
 */
public final class Zero3MotorTailLatencyRegression {
    static final float DT=.025f;
    static final int[] CHECKPOINTS={24,72,144,264,384};
    static final float[][] INTENTS={{1,0},{0,1},{-1,0},{0,-1}};
    private static double gain(Zero3MotorImpulseResponseRegression.Scenario a,
                               Zero3MotorImpulseResponseRegression.Scenario b,
                               int heading){
        return (b.body.cx-a.body.cx)*INTENTS[heading][0]+
               (b.body.cy-a.body.cy)*INTENTS[heading][1];
    }
    public static void main(String[] args){
        int n=0,reversed=0,changedBeyondFortyEight=0,invalid=0;
        double finalSigned=0,shortSigned=0,absLateChange=0;
        for(int dir=0;dir<4;dir++)for(float pose:new float[]{
                0,(float)(Math.PI/4)})
            for(int condition:new int[]{0,2}){
                Zero3MotorImpulseResponseRegression.Scenario baseline=
                    new Zero3MotorImpulseResponseRegression.Scenario(dir,pose,condition);
                Zero3MotorImpulseResponseRegression.Scenario acted=
                    new Zero3MotorImpulseResponseRegression.Scenario(dir,pose,condition);
                if(Math.hypot(baseline.body.cx-acted.body.cx,
                    baseline.body.cy-acted.body.cy)>.00001)
                    throw new AssertionError("not an identical starting physical state");
                int sector=dir*4,channel=0,sign=-1;
                double[] signed=new double[CHECKPOINTS.length];
                double[] cost=new double[CHECKPOINTS.length];
                int mark=0;
                for(int f=0;f<CHECKPOINTS[CHECKPOINTS.length-1];f++){
                    baseline.tick(f,-1,channel,sign);
                    acted.tick(f,sector,channel,sign);
                    if(f+1==CHECKPOINTS[mark]){
                        signed[mark]=gain(baseline,acted,dir);
                        cost[mark]=acted.work-baseline.work;
                        mark++;
                    }
                    if(baseline.gel.invalid!=0||acted.gel.invalid!=0)
                        throw new AssertionError("invalid real physical material");
                }
                if(mark!=CHECKPOINTS.length)throw new AssertionError("missing delayed measurement");
                n++;
                double shortResult=signed[1],longResult=signed[4];
                double change=longResult-shortResult;
                absLateChange+=Math.abs(change);
                if(Math.abs(change)>.1)changedBeyondFortyEight++;
                if(shortResult*longResult<0)reversed++;
                shortSigned+=shortResult;finalSigned+=longResult;
                System.out.printf(Locale.US,
                    "ZERO3 MOTOR TAIL dir=[%.0f %.0f] poseDeg=%.1f initial=%d"+
                    " afterPulse=%.5f after48=%.5f after120=%.5f"+
                    " after240=%.5f after360=%.5f lateDelta=%.5f"+
                    " workDeltaAfter48=%.8f workDeltaAfter360=%.8f%n",
                    INTENTS[dir][0],INTENTS[dir][1],pose*180/Math.PI,condition,
                    signed[0],signed[1],signed[2],signed[3],signed[4],change,
                    cost[1],cost[4]);
            }
        System.out.printf(Locale.US,
            "ZERO3 MOTOR TEMPORAL TAIL SUMMARY cases=%d"+
            " changesBeyond48GreaterThan0.1=%d signReversals=%d"+
            " summedShortResponse=%.5f summedLongResponse=%.5f"+
            " meanAbsoluteLongTail=%.5f"+
            " (PHYSICAL CREDIT LAG ONLY; no learned walking)%n",
            n,changedBeyondFortyEight,reversed,shortSigned,finalSigned,absLateChange/n);
    }
}
