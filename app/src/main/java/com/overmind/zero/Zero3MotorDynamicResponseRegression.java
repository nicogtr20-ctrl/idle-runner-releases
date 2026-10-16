package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 V3.4 — challenge a predictor trained on pristine bodies with
 * previously deforming / moving bodies. Actual tissue preconditioning is
 * done with real muscular force, NEVER moving the centroid by hand.
 *
 * Explicitly a distribution-shift / falsification test: a green executable
 * means only that measurements ran, NOT that the predictor is calibrated.
 */
public final class Zero3MotorDynamicResponseRegression {
    private static final int[] CENTERS={0,4,8,12};
    private static final float[] TRAIN={0f,(float)(Math.PI/2)};
    private static final float[] CALIB={(float)(Math.PI/8),(float)(-Math.PI/8)};
    private static final float[] BLIND={(float)(Math.PI/4),(float)(-Math.PI/4)};
    private static final float[][] HEADINGS={{1f,0f},{0f,1f},{-1f,0f},{0f,-1f}};
    private static final int[] STATE_CASES={1,2,3};
    static void check(boolean yes,String msg){
        if(!yes)throw new AssertionError("ZERO3 V3.4 "+msg);
    }
    static ArrayList<Zero3MotorImpulseResponseRegression.Trial> sample(
        float[] poses,int[] states){
        ArrayList<Zero3MotorImpulseResponseRegression.Trial> samples=new ArrayList<>();
        for(int dir=0;dir<4;dir++)for(float pose:poses)
            for(int state:states)for(int ch=0;ch<4;ch++)
                for(int sector:CENTERS)for(int sign:new int[]{-1,1})
                    samples.add(Zero3MotorImpulseResponseRegression.physical(
                        dir,pose,sector,ch,sign,state));
        return samples;
    }
    static class Score {
        double[] model=new double[3],zero=new double[3];
        int n;
        void add(double[] actual,double[] predicted){
            n++;
            for(int i=0;i<3;i++){
                model[i]+=(actual[i]-predicted[i])*(actual[i]-predicted[i]);
                zero[i]+=actual[i]*actual[i];
            }
        }
        String show(){
            return String.format(Locale.US,
                "n=%d earlyMSE=%.8f earlyZero=%.8f lateMSE=%.8f lateZero=%.8f"+
                " costMSE=%.12f costZero=%.12f",n,
                model[0]/n,zero[0]/n,model[1]/n,zero[1]/n,
                model[2]/n,zero[2]/n);
        }
        boolean better(){
            return model[0]<zero[0]*.9 && model[1]<zero[1]*.9 &&
                   model[2]<zero[2]*.9;
        }
    }
    private static Score evaluate(
        Zero3MotorImpulseResponseRegression.ResponseModel model,
        ArrayList<Zero3MotorImpulseResponseRegression.Trial> sample){
        Score s=new Score();
        for(Zero3MotorImpulseResponseRegression.Trial t:sample){
            s.add(new double[]{t.early,t.late,t.extraWork},model.infer(t));
        }
        return s;
    }
    public static void main(String[] args){
        ArrayList<Zero3MotorImpulseResponseRegression.Trial> training=
            sample(TRAIN,new int[]{0});
        ArrayList<Zero3MotorImpulseResponseRegression.Trial> calibrate=
            sample(CALIB,new int[]{0});
        check(training.size()==256 && calibrate.size()==256,
            "unexpected data partition");
        Zero3MotorImpulseResponseRegression.ResponseModel model=
            new Zero3MotorImpulseResponseRegression.ResponseModel(training);
        Score calibration=evaluate(model,calibrate);
        boolean trusted=calibration.better();
        System.out.println("ZERO3 DYNAMIC calibrationRest "+calibration.show()+
            " gate="+(trusted?"ACCEPTED_FOR_PRISTINE_ONLY":"REJECTED"));

        ArrayList<Zero3MotorImpulseResponseRegression.Trial> blind=
            sample(BLIND,STATE_CASES);
        check(blind.size()==768,"unexpected blind size");
        for(int condition:STATE_CASES){
            ArrayList<Zero3MotorImpulseResponseRegression.Trial> subset=
                new ArrayList<>();
            for(int i=0;i<blind.size();i++){
                // Each condition appears in a complete 32-trial block.
                if((i/32)%STATE_CASES.length==condition-1)subset.add(blind.get(i));
            }
            Score score=evaluate(model,subset);
            System.out.println("ZERO3 DYNAMIC unseenCondition="+condition+" "+
                score.show()+" gate="+(score.better()?"BEATS_ZERO":"FAILS_ZERO"));
        }
        Score overall=evaluate(model,blind);
        double sumBenefit=0,sumRandom=0,sumCost=0;
        int successes=0,chosen=0,costly=0;
        for(int dir=0;dir<4;dir++)for(float pose:BLIND)
            for(int condition:STATE_CASES){
                Zero3MotorImpulseResponseRegression.Trial pick=null,reference=null;
                double prediction=0;
                for(Zero3MotorImpulseResponseRegression.Trial t:blind){
                    if(t.dir!=dir || t.sector<0 ||
                        (int)Math.round(t.pose*1000)!=(int)Math.round(
                            // The true body has shifted away from the externally
                            // specified pose; group by the model-observable range.
                            observedFor(dir,pose,condition)*1000))continue;
                    // Never use observed test outcomes for selection.
                    if(reference==null)reference=t;
                    double[] pred=model.infer(t);
                    double expected=pred[0]+pred[1]-45*Math.max(0,pred[2]);
                    if(trusted&&expected>prediction){prediction=expected;pick=t;}
                }
                if(reference==null)continue; // diagnostic coverage below
                double delta=pick==null?0:pick.signedResponse();
                double cost=pick==null?0:pick.extraWork;
                if(delta>0&&cost<.005)successes++;
                if(cost>=.005)costly++;
                sumBenefit+=delta;sumCost+=cost;
                sumRandom+=reference.signedResponse();chosen++;
            }
        // In fact geometrical state is not a fixed rotation after warmup.
        // Quantify errors; this test must not silently be called a success.
        System.out.printf(Locale.US,
            "ZERO3 DYNAMIC SUMMARY training=%d calibration=%d blind=%d"+
            " blindEarlyMSE=%.8f blindZeroEarly=%.8f"+
            " blindLateMSE=%.8f blindZeroLate=%.8f"+
            " blindCostMSE=%.12f blindZeroCost=%.12f"+
            " blindPredictionGate=%s chosen=%d/24 successWithinWork=%d"+
            " costly=%d signed=%.4f extraWork=%.7f random=%.4f"+
            " (DO NOT PROMOTE to game on pristine-only calibration)%n",
            training.size(),calibrate.size(),blind.size(),
            overall.model[0]/overall.n,overall.zero[0]/overall.n,
            overall.model[1]/overall.n,overall.zero[1]/overall.n,
            overall.model[2]/overall.n,overall.zero[2]/overall.n,
            overall.better()?"BEATS_ZERO":"FAILS_ZERO",
            chosen,successes,costly,sumBenefit,sumCost,sumRandom);
        check(overall.n==768,"incomplete physics experiments");
    }
    private static float observedFor(int dir,float pose,int condition){
        return new Zero3MotorImpulseResponseRegression.Scenario(
            dir,pose,condition).observedPose;
    }
}
