package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 V3.4 laboratory CLOSED-LOOP check: can an independently calibrated
 * prediction model help the real symbiote execute a continuous intention over
 * many consecutive decisions, without cheating its position or giving it
 * a prescribed gait? Only lab directions fixed; no autonomous brain yet.
 *
 * This is deliberately NOT counted as V3 lifelong learning: parameters are
 * pretrained in separate matched physical trials, then frozen.
 */
public final class Zero3MotorClosedLoopRegression {
    static final float DT=.025f, ROTATE=(float)(Math.PI/4);
    static final int STEPS=80,FRAMES=60;
    static final int[] REGIONS={0,4,8,12};
    static final float[][] DIR={{1,0},{0,1},{-1,0},{0,-1}};
    static void check(boolean x,String message){
        if(!x)throw new AssertionError("ZERO3 CLOSED LOOP "+message);
    }
    static class Metrics {
        double forward,gross,work;
        int proposals,positiveWindows,nonfinite,repairs;
        String show(){
            return String.format(Locale.US,
                "signed=%.3f gross=%.3f work=%.5f proposed=%d positiveWindows=%d repairs=%d",
                forward,gross,work,proposals,positiveWindows,repairs);
        }
    }
    static Metrics run(Zero3MotorResponseKernel.Predictor predictor,
                       int heading,float orientation,int initialCondition,
                       boolean guided,float referenceAxis){
        return run(predictor,heading,orientation,initialCondition,guided,referenceAxis,0);
    }
    static Metrics run(Zero3MotorResponseKernel.Predictor predictor,
                       int heading,float orientation,int initialCondition,
                       boolean guided,float referenceAxis,int settlingFrames){
        return run(predictor,heading,orientation,initialCondition,
            guided,referenceAxis,settlingFrames,false);
    }
    static Metrics run(Zero3MotorResponseKernel.Predictor predictor,
                       int heading,float orientation,int initialCondition,
                       boolean guided,float referenceAxis,int settlingFrames,
                       boolean adaptive){
        Zero3MotorImpulseResponseRegression.Scenario initial=
            new Zero3MotorImpulseResponseRegression.Scenario(
                heading,orientation,initialCondition);
        MotorBrain.Body body=initial.body;
        Morphology.Surface skin=initial.skin;
        SymbioteMatterV1 gel=initial.gel;
        Zero3LearningFoundation.AdaptiveCortex cortex=initial.cortex;
        Zero3LearningFoundation.MotorIntent intent=initial.intent;
        Metrics metrics=new Metrics();
        final float dx=DIR[heading][0],dy=DIR[heading][1];
        double priorWork=gel.work;
        Zero3MotorCreditWindow credit=settlingFrames==0?null:
            new Zero3MotorCreditWindow(settlingFrames);
        if(adaptive&&credit==null)throw new IllegalArgumentException("feedback needs delayed observation");
        Zero3MotorAdaptiveGate veto=adaptive?
            new Zero3MotorAdaptiveGate(settlingFrames,1.0,1.15):null;
        if(veto!=null)veto.start(body.cx,body.cy,gel.work,dx,dy);
        long processed=0;
        for(int d=0;d<STEPS;d++){
            float observedPose=Zero3MotorResponseKernel.relativePose(
                Zero3MotorResponseKernel.membraneAxis(body,skin),referenceAxis);
            boolean ready=(credit==null||credit.mayExplore((long)d*FRAMES))&&
                (veto==null||veto.mayTry());
            Zero3MotorResponseKernel.Suggestion p=guided&&ready?
                predictor.suggest(dx,dy,observedPose,REGIONS,.005):null;
            if(p!=null){
                if(credit!=null)
                    check(credit.begin(d,(long)d*FRAMES,body.cx,body.cy,gel.work),
                        "overlapping muscle intervention accepted");
                metrics.proposals++;
            }
            float oldX=body.cx,oldY=body.cy;
            float[] command=null;
            for(int f=0;f<FRAMES;f++){
                command=cortex.command(intent,body,skin,DT);
                if(p!=null&&f>=12&&f<24)
                    Zero3MotorResponseKernel.applyLocalPulse(command,p,.20f);
                float prevX=body.cx,prevY=body.cy;
                gel.step(body,skin,command,DT);
                if(credit!=null){
                    long globalFrame=(long)d*FRAMES+f;
                    credit.observe(globalFrame,body.cx,body.cy,gel.work);
                    if(veto!=null){
                        if(!veto.baselineReady())
                            veto.observeBaseline((int)globalFrame+1,body.cx,body.cy,gel.work);
                        if(credit.completedCount()>processed){
                            veto.acceptObserved(credit.lastCompleted());
                            processed=credit.completedCount();
                        }
                    }
                }
                check(gel.invalid==0&&Float.isFinite(body.cx)&&
                      Float.isFinite(body.cy)&&MaterialGeometry.selfCrossings(skin)==0,
                      "physical invalid at decision="+d+" frame="+f);
                metrics.gross+=Math.hypot(body.cx-prevX,body.cy-prevY);
            }
            float gain=(body.cx-oldX)*dx+(body.cy-oldY)*dy;
            if(gain>0)metrics.positiveWindows++;
            metrics.forward+=gain;
        }
        metrics.work=Math.max(0,gel.work-priorWork);
        metrics.repairs=(int)gel.topologyRepairs;
        return metrics;
    }
    public static void main(String[] args){
        ArrayList<Zero3MotorResponseKernel.PhysicalSample> rows=
            Zero3MotorKernelContractRegression.convert(
                Zero3MotorDynamicResponseRegression.sample(
                    new float[]{0,(float)(Math.PI/2)},new int[]{0}),1000);
        ArrayList<Zero3MotorResponseKernel.PhysicalSample> cal=
            Zero3MotorKernelContractRegression.convert(
                Zero3MotorDynamicResponseRegression.sample(
                    new float[]{(float)(Math.PI/8),(float)(-Math.PI/8)},
                    new int[]{0}),1001);
        Zero3MotorResponseKernel.Predictor predictor=
            new Zero3MotorResponseKernel.Predictor(rows,.5);
        check(predictor.calibrate(cal),"refused independent physical calibration");
        Zero3MotorImpulseResponseRegression.Scenario canonical=
            new Zero3MotorImpulseResponseRegression.Scenario(0,0);
        float axis=Zero3MotorResponseKernel.membraneAxis(
            canonical.body,canonical.skin);
        int wins=0,total=0;
        double baselineSigned=0,guidedSigned=0,baselineWork=0,guidedWork=0;
        for(int dir=0;dir<4;dir++)for(float angle:new float[]{0,ROTATE})
            for(int condition:new int[]{0,2}){
                Metrics base=run(predictor,dir,angle,condition,false,axis);
                Metrics guided=run(predictor,dir,angle,condition,true,axis);
                total++;
                if(guided.forward>base.forward&&guided.work<base.work*1.20+.005)
                    wins++;
                baselineSigned+=base.forward;guidedSigned+=guided.forward;
                baselineWork+=base.work;guidedWork+=guided.work;
                System.out.printf(Locale.US,
                    "ZERO3 CLOSED LOOP dir=[%.0f %.0f] pose=%.0f condition=%d "+
                    "decisions=%d FROZEN{%s} KERNEL{%s} deltaForward=%.3f deltaWork=%.5f%n",
                    DIR[dir][0],DIR[dir][1],angle*180/Math.PI,condition,
                    STEPS,base.show(),guided.show(),guided.forward-base.forward,
                    guided.work-base.work);
        }
        System.out.printf(Locale.US,
            "ZERO3 CLOSED LOOP SUMMARY 4directions x 2poses x 2initialStates x "+
            "80contiguousDecisions perArm=%d matchedCases=%d winsWithinCost=%d"+
            " baselineSigned=%.4f kernelSigned=%.4f"+
            " baselineWork=%.5f kernelWork=%.5f"+
            " pretrainedResponseWeightsOnly=true NO_ZERO3_SKILL_PROMOTION%n",
            STEPS,total,wins,baselineSigned,guidedSigned,baselineWork,guidedWork);
    }
}
