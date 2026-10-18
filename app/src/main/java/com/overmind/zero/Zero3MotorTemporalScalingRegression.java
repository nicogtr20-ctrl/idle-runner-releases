package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 V3.7-B — blinded test of feature scaling WITHOUT outcome leakage.
 *
 * V3.7 showed almost zero benefit for real 96-frame muscle memory in a
 * ridge regression. History descriptors (velocity deltas, muscle averages)
 * have very different numerical scales; regularization may suppress them.
 *
 * Compare 52 static, 68 raw-history, 68 TRAIN-ONLY NORMALIZED history on
 * the same physical records and preregistered ridge penalty, then blind
 * evaluate on NEW angles +/-15 and +/-75 degrees (not previously tested).
 *
 * No threshold tuning with blind data; holdout gate remains 10% MSE
 * improvement against zero on ALL three outputs; no V3 production activation.
 */
public final class Zero3MotorTemporalScalingRegression {
    static final int DIM=Zero3MotorTemporalHistoryRegression.FEATURES;
    static final int BASE=Zero3MotorTemporalHistoryRegression.BASE;
    static final float[] FINAL_POSES={
        (float)(Math.PI/12),(float)(-Math.PI/12),
        (float)(5*Math.PI/12),(float)(-5*Math.PI/12)};
    static void assertValid(boolean b,String reason){
        if(!b)throw new AssertionError("ZERO3 MEMORY SCALING "+reason);
    }
    static final class Normalized {
        final double[] center=new double[DIM],scale=new double[DIM];
        final double[][] weights=new double[3][DIM];
        Normalized(ArrayList<Zero3MotorTemporalHistoryRegression.Case> train){
            assertValid(train!=null&&train.size()==384,"unexpected training dataset");
            for(int j=0;j<DIM;j++)scale[j]=1;
            for(int j=BASE;j<DIM;j++){
                double mean=0;
                for(Zero3MotorTemporalHistoryRegression.Case t:train)
                    mean+=t.historical[j];
                mean/=train.size();
                center[j]=mean;
                double variance=0;
                for(Zero3MotorTemporalHistoryRegression.Case t:train)
                    variance+=Math.pow(t.historical[j]-mean,2);
                // Fixed pre-result standard deviation floor avoids amplifying
                // machine epsilon noise in nearly constant physical channels.
                scale[j]=Math.max(.005,Math.sqrt(variance/train.size()));
                assertValid(Double.isFinite(scale[j]),"nonfinite training scale");
            }
            double[][] gram=new double[DIM][DIM],target=new double[3][DIM];
            for(Zero3MotorTemporalHistoryRegression.Case row:train){
                double[] x=normalized(row);
                double[] y={row.early,row.late,1000*row.work};
                for(int j=0;j<DIM;j++){
                    for(int k=0;k<DIM;k++)gram[j][k]+=x[j]*x[k];
                    for(int h=0;h<3;h++)target[h][j]+=x[j]*y[h];
                }
            }
            for(int h=0;h<3;h++){
                double[][] g=new double[DIM][DIM];
                for(int j=0;j<DIM;j++){
                    System.arraycopy(gram[j],0,g[j],0,DIM);
                    g[j][j]+=.5;
                }
                weights[h]=Zero3MotorResponseKernel.solve(g,target[h]);
            }
        }
        double[] normalized(Zero3MotorTemporalHistoryRegression.Case row){
            double[] z=new double[DIM];
            for(int j=0;j<DIM;j++){
                z[j]=(row.historical[j]-center[j])/scale[j];
                assertValid(Double.isFinite(z[j]),"bad normalized motor memory");
            }
            return z;
        }
        double[] predict(Zero3MotorTemporalHistoryRegression.Case row){
            double[] f=normalized(row),out=new double[3];
            for(int h=0;h<3;h++)
                for(int i=0;i<DIM;i++)out[h]+=weights[h][i]*f[i];
            out[2]/=1000;
            return out;
        }
        String scaleSummary(){
            double min=Double.POSITIVE_INFINITY,max=0;
            for(int j=BASE;j<DIM;j++){
                min=Math.min(min,scale[j]);max=Math.max(max,scale[j]);
            }
            return String.format(Locale.US,"historicalScaleMin=%.6f historicalScaleMax=%.6f",min,max);
        }
    }
    static class Metrics {
        final double[] mse=new double[3],zero=new double[3];
        final int n;
        Metrics(ArrayList<Zero3MotorTemporalHistoryRegression.Case> data,
                Normalized predictor){
            n=data.size();
            for(Zero3MotorTemporalHistoryRegression.Case row:data){
                double[] predicted=predictor.predict(row),actual=row.target();
                for(int j=0;j<3;j++){
                    mse[j]+=Math.pow(predicted[j]-actual[j],2);
                    zero[j]+=actual[j]*actual[j];
                }
            }
            for(int j=0;j<3;j++){mse[j]/=n;zero[j]/=n;}
        }
        boolean valid(){
            return mse[0]<.9*zero[0]&&mse[1]<.9*zero[1]&&mse[2]<.9*zero[2];
        }
        String format(){
            return String.format(Locale.US,
                "N=%d earlyMSE=%.9f zeroEarly=%.9f"+
                " delayedMSE=%.9f zeroDelayed=%.9f"+
                " workMSE=%.12f zeroWork=%.12f gate=%s",
                n,mse[0],zero[0],mse[1],zero[1],mse[2],zero[2],
                valid()?"PASS":"FAIL");
        }
    }
    public static void main(String[] args){
        ArrayList<Zero3MotorTemporalHistoryRegression.Case> train=
            Zero3MotorTemporalHistoryRegression.samples(
                Zero3MotorTemporalHistoryRegression.TRAIN,new int[]{0,1,2});
        ArrayList<Zero3MotorTemporalHistoryRegression.Case> calibration=
            Zero3MotorTemporalHistoryRegression.samples(
                Zero3MotorTemporalHistoryRegression.CAL,new int[]{0,1,2});
        assertValid(train.size()==384&&calibration.size()==384,"bad training partition");
        Zero3MotorTemporalHistoryRegression.Ridge state=
            new Zero3MotorTemporalHistoryRegression.Ridge(train,BASE);
        Zero3MotorTemporalHistoryRegression.Ridge unscaled=
            new Zero3MotorTemporalHistoryRegression.Ridge(train,DIM);
        Normalized scaled=new Normalized(train);
        // Gate fixed on calibration before any new blind data is generated.
        Metrics cal=new Metrics(calibration,scaled);
        boolean calibrationPass=cal.valid();
        System.out.println("ZERO3 V37B CAL NORMALIZED "+cal.format());
        System.out.println("ZERO3 V37B TRAIN-ONLY "+scaled.scaleSummary());
        ArrayList<Zero3MotorTemporalHistoryRegression.Case> blind=
            Zero3MotorTemporalHistoryRegression.samples(FINAL_POSES,new int[]{3});
        assertValid(blind.size()==256,"bad newly unseen pose partition");
        Zero3MotorTemporalHistoryRegression.Metrics stateStats=
            new Zero3MotorTemporalHistoryRegression.Metrics(state,blind);
        Zero3MotorTemporalHistoryRegression.Metrics rawStats=
            new Zero3MotorTemporalHistoryRegression.Metrics(unscaled,blind);
        Metrics normalStats=new Metrics(blind,scaled);
        System.out.println("ZERO3 V37B NEW-BLIND 52-INSTANT "+stateStats.line());
        System.out.println("ZERO3 V37B NEW-BLIND 68-RAW "+rawStats.line());
        System.out.println("ZERO3 V37B NEW-BLIND 68-TRAIN-NORMALIZED "+normalStats.format());
        double improvementVersusRaw=1-normalStats.mse[1]/rawStats.mse[1];
        double improvementVersusState=1-normalStats.mse[1]/stateStats.mse[1];
        int chosenPositive=0,chosen=0;
        for(int d=0;d<4;d++)for(float pose:FINAL_POSES){
            Zero3MotorTemporalHistoryRegression.Case best=null;
            double predicted=0;
            for(Zero3MotorTemporalHistoryRegression.Case row:blind){
                if(row.dir!=d||Math.abs(row.pose-pose)>.001f)continue;
                double[] p=scaled.predict(row);
                double value=p[0]+p[1]-45*Math.max(0,p[2]);
                if(calibrationPass&&value>predicted){predicted=value;best=row;}
            }
            if(best!=null){
                chosen++;
                if(best.signed()>0&&best.work<.005)chosenPositive++;
            }
        }
        System.out.printf(Locale.US,
            "ZERO3 V37B SUMMARY train=384 cal=384 newBlind=256"+
            " pairedPhysicalSteps=983040 trainingOnlyScaling=true"+
            " calibrated=%s newBlindGate=%s"+
            " lateMseGainVsRawPercent=%.3f lateMseGainVsInstantPercent=%.3f"+
            " selectedUsefulWithinCost=%d/%d"+
            " (research only; NO skill promotion, NO OTA)%n",
            Boolean.toString(calibrationPass),
            Boolean.toString(normalStats.valid()),
            improvementVersusRaw*100,improvementVersusState*100,
            chosenPositive,chosen);
    }
}
