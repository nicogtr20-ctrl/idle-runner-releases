package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 credit timing response: 80 consecutive decisions in each of the
 * existing 16 real-body tests. Compare no extra motor pulse, pulses every
 * decision (known regression), and a physically justified 384-frame
 * pending-credit interval. A held intention is only a lab controllability
 * task, never a programmed gait for autonomous gameplay.
 */
public final class Zero3MotorCreditWindowRegression {
    static void check(boolean x,String reason){
        if(!x)throw new AssertionError("ZERO3 CREDIT WINDOW "+reason);
    }
    static void testTimeline(){
        Zero3MotorCreditWindow ledger=new Zero3MotorCreditWindow(384);
        check(ledger.mayExplore(0),"fresh ledger locked");
        check(ledger.begin(0,0,0,0,0),"failed to own real actuation window");
        for(int frame=0;frame<384;frame++){
            ledger.observe(frame,frame*.001f,0,frame*.0001f);
            check(!ledger.mayExplore(frame+1),"unresolved pulse unlocked");
        }
        check(ledger.pending(),"premature training outcome");
        ledger.observe(384,.384f,0,.0384f);
        check(!ledger.pending()&&ledger.completedCount()==1&&
            ledger.lastCompleted().measuredFrames==384,
            "incorrect delayed physical credit completion");
        check(ledger.mayExplore(385),"completed pulse remained locked");
        check(ledger.begin(1,385,.384f,0,.0384f),
            "refused new independent intervention after actual completion");
        ledger.abort();
        check(ledger.abortedCount()==1 && !ledger.pending(),
            "censored motor intervention was credited");
    }
    public static void main(String[] args){
        testTimeline();
        ArrayList<Zero3MotorResponseKernel.PhysicalSample> train=
            Zero3MotorKernelContractRegression.convert(
                Zero3MotorDynamicResponseRegression.sample(
                    new float[]{0,(float)(Math.PI/2)},new int[]{0}),401);
        ArrayList<Zero3MotorResponseKernel.PhysicalSample> calib=
            Zero3MotorKernelContractRegression.convert(
                Zero3MotorDynamicResponseRegression.sample(
                    new float[]{(float)(Math.PI/8),(float)(-Math.PI/8)},new int[]{0}),402);
        Zero3MotorResponseKernel.Predictor predictor=
            new Zero3MotorResponseKernel.Predictor(train,.5);
        check(predictor.calibrate(calib),"predictor untrusted");
        Zero3MotorImpulseResponseRegression.Scenario canonical=
            new Zero3MotorImpulseResponseRegression.Scenario(0,0);
        float reference=Zero3MotorResponseKernel.membraneAxis(canonical.body,canonical.skin);
        int total=0,winsNaive=0,winsDelayed=0,delayedCheaper=0;
        double baseSum=0,naiveSum=0,delayedSum=0;
        double baseCost=0,naiveCost=0,delayedCost=0;
        int pulsesNaive=0,pulsesDelayed=0;
        for(int dir=0;dir<4;dir++)for(float pose:new float[]{
                0,(float)(Math.PI/4)})
            for(int initial:new int[]{0,2}){
                Zero3MotorClosedLoopRegression.Metrics base=
                    Zero3MotorClosedLoopRegression.run(
                        predictor,dir,pose,initial,false,reference);
                Zero3MotorClosedLoopRegression.Metrics naive=
                    Zero3MotorClosedLoopRegression.run(
                        predictor,dir,pose,initial,true,reference);
                Zero3MotorClosedLoopRegression.Metrics guarded=
                    Zero3MotorClosedLoopRegression.run(
                        predictor,dir,pose,initial,true,reference,384);
                boolean goodNaive=naive.forward>base.forward &&
                    naive.work<base.work*1.20+.005;
                boolean goodGuarded=guarded.forward>base.forward &&
                    guarded.work<base.work*1.20+.005;
                if(goodNaive)winsNaive++;
                if(goodGuarded)winsDelayed++;
                if(guarded.work<naive.work)delayedCheaper++;
                check(guarded.proposals<=
                    (Zero3MotorClosedLoopRegression.STEPS+6)/7+1,
                    "credit window allowed repeated interference");
                baseSum+=base.forward;naiveSum+=naive.forward;delayedSum+=guarded.forward;
                baseCost+=base.work;naiveCost+=naive.work;delayedCost+=guarded.work;
                pulsesNaive+=naive.proposals;pulsesDelayed+=guarded.proposals;
                total++;
                System.out.printf(Locale.US,
                    "ZERO3 CREDIT 384 dir=[%.0f %.0f] pose=%.0f initial=%d"+
                    " BASE{%.3f work=%.6f}"+
                    " NAIVE{%.3f work=%.6f pulses=%d}"+
                    " PENDING-CREDIT{%.3f work=%.6f pulses=%d}"+
                    " guardedBeatsBase=%s%n",
                    Zero3MotorClosedLoopRegression.DIR[dir][0],
                    Zero3MotorClosedLoopRegression.DIR[dir][1],
                    pose*180/Math.PI,initial,
                    base.forward,base.work,naive.forward,naive.work,naive.proposals,
                    guarded.forward,guarded.work,guarded.proposals,
                    Boolean.toString(goodGuarded));
            }
        System.out.printf(Locale.US,
            "ZERO3 CREDIT WINDOW SUMMARY cases=%d baselineSigned=%.5f"+
            " naiveSigned=%.5f guardedSigned=%.5f baselineWork=%.6f"+
            " naiveWork=%.6f guardedWork=%.6f"+
            " naiveWinsWithinCost=%d/%d guardedWinsWithinCost=%d/%d"+
            " guardedCheaperThanNaive=%d/%d"+
            " naivePulses=%d guardedPulses=%d"+
            " physicalOutcomeLedger=true automaticPromotion=false%n",
            total,baseSum,naiveSum,delayedSum,baseCost,naiveCost,delayedCost,
            winsNaive,total,winsDelayed,total,delayedCheaper,total,
            pulsesNaive,pulsesDelayed);
    }
}
