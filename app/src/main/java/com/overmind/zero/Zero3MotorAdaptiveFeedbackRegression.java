package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 V3.5 — pre-registered conservative stateful motor probe veto.
 * Compares three controllers on identical real-physics starts:
 * baseline, 384-frame non-overlap, 384-frame + feedback abstention.
 *
 * Do not call this motor learning until an independent test shows that
 * repeated body feedback improves continuous direction/cost performance.
 * The adaptive gate never uses a laboratory counterfactual to make an
 * on-life decision; it sees only signed real displacement and actual work.
 */
public final class Zero3MotorAdaptiveFeedbackRegression {
    private static final float[][] PHASE_POSES={
        {0,(float)(Math.PI/4)},
        {(float)(Math.PI/6),(float)(Math.PI/3)}
    };
    private static final int[][] PHASE_STATES={{0,2},{1,3}};
    private static final int WINDOW=384;
    static class Summary{
        final String label;
        int count,guardWins,adaptiveWins,betterThanGuard,adaptiveCheaper;
        double baseForward,guardForward,adaptiveForward;
        double baseWork,guardWork,adaptiveWork;
        int totalGuardPulses,totalAdaptivePulses;
        Summary(String label){this.label=label;}
        void record(int dir,float pose,int state,
            Zero3MotorClosedLoopRegression.Metrics base,
            Zero3MotorClosedLoopRegression.Metrics guard,
            Zero3MotorClosedLoopRegression.Metrics adaptive){
            count++;
            if(guard.forward>base.forward&&guard.work<base.work*1.2+.005)guardWins++;
            if(adaptive.forward>base.forward&&adaptive.work<base.work*1.2+.005)adaptiveWins++;
            if(adaptive.forward>guard.forward)betterThanGuard++;
            if(adaptive.work<=guard.work)adaptiveCheaper++;
            baseForward+=base.forward;guardForward+=guard.forward;adaptiveForward+=adaptive.forward;
            baseWork+=base.work;guardWork+=guard.work;adaptiveWork+=adaptive.work;
            totalGuardPulses+=guard.proposals;totalAdaptivePulses+=adaptive.proposals;
            System.out.printf(Locale.US,
                "ZERO3 V35 %s dir=[%.0f %.0f] angle=%.1f initial=%d"+
                " BASE{forward=%.3f work=%.6f}"+
                " GUARD{forward=%.3f work=%.6f probes=%d}"+
                " FEEDBACK{forward=%.3f work=%.6f probes=%d}"+
                " deltaFeedbackVsBase=%.3f%n",
                label,Zero3MotorClosedLoopRegression.DIR[dir][0],
                Zero3MotorClosedLoopRegression.DIR[dir][1],
                pose*180/Math.PI,state,
                base.forward,base.work,guard.forward,guard.work,guard.proposals,
                adaptive.forward,adaptive.work,adaptive.proposals,
                adaptive.forward-base.forward);
        }
        void report(){
            System.out.printf(Locale.US,
                "ZERO3 V35 SUMMARY phase=%s cases=%d 80decisionsEach"+
                " baseForward=%.5f guardForward=%.5f adaptiveForward=%.5f"+
                " baseWork=%.6f guardWork=%.6f adaptiveWork=%.6f"+
                " guardWinsVsBase=%d/%d adaptiveWinsVsBase=%d/%d"+
                " feedbackBeatsGuard=%d/%d feedbackCheaper=%d/%d"+
                " guardPulses=%d adaptivePulses=%d"+
                " (OBSERVATIONAL FEEDBACK, NOT PROVEN CAUSAL CREDIT)%n",
                label,count,baseForward,guardForward,adaptiveForward,
                baseWork,guardWork,adaptiveWork,guardWins,count,
                adaptiveWins,count,betterThanGuard,count,adaptiveCheaper,count,
                totalGuardPulses,totalAdaptivePulses);
        }
    }
    private static Summary phase(Zero3MotorResponseKernel.Predictor predictor,
        float referenceAxis,int phase){
        Summary result=new Summary(phase==0?"SCREEN":"UNSEEN_POSE_STATE");
        for(int dir=0;dir<4;dir++)
            for(float pose:PHASE_POSES[phase])
                for(int condition:PHASE_STATES[phase]){
                    Zero3MotorClosedLoopRegression.Metrics base=
                        Zero3MotorClosedLoopRegression.run(
                            predictor,dir,pose,condition,false,referenceAxis);
                    Zero3MotorClosedLoopRegression.Metrics guard=
                        Zero3MotorClosedLoopRegression.run(
                            predictor,dir,pose,condition,true,referenceAxis,WINDOW);
                    Zero3MotorClosedLoopRegression.Metrics adaptive=
                        Zero3MotorClosedLoopRegression.run(
                            predictor,dir,pose,condition,true,referenceAxis,WINDOW,true);
                    if(adaptive.proposals>guard.proposals||
                       adaptive.work<0||!Double.isFinite(adaptive.forward))
                        throw new AssertionError("adaptive bad physical A/B");
                    result.record(dir,pose,condition,base,guard,adaptive);
                }
        result.report();
        return result;
    }
    public static void main(String[] args){
        ArrayList<Zero3MotorResponseKernel.PhysicalSample> fit=
            Zero3MotorKernelContractRegression.convert(
                Zero3MotorDynamicResponseRegression.sample(
                    new float[]{0,(float)(Math.PI/2)},new int[]{0}),900);
        ArrayList<Zero3MotorResponseKernel.PhysicalSample> calibration=
            Zero3MotorKernelContractRegression.convert(
                Zero3MotorDynamicResponseRegression.sample(
                    new float[]{(float)(Math.PI/8),(float)(-Math.PI/8)},new int[]{0}),901);
        Zero3MotorResponseKernel.Predictor predictor=
            new Zero3MotorResponseKernel.Predictor(fit,.5);
        if(!predictor.calibrate(calibration))
            throw new AssertionError("response predictor failed dedicated calibration");
        Zero3MotorImpulseResponseRegression.Scenario virgin=
            new Zero3MotorImpulseResponseRegression.Scenario(0,0);
        float reference=Zero3MotorResponseKernel.membraneAxis(virgin.body,virgin.skin);
        phase(predictor,reference,0);
        phase(predictor,reference,1);
        System.out.println("ZERO3 V35 experimental selection rule frozen before "+
            "unseen phase, not integrated in autonomous cortex or OTA");
    }
}
