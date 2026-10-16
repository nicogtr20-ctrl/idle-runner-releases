package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * ZERO 3.8 / G3-G5 LAB ONLY: learned recurrent response trajectory.
 *
 * Unlike the prior single-head regression to the outcome at 384 frames,
 * this model has an evolving latent motor state and a trained readout for
 * EACH of 32 consecutive twelve-frame physical horizons.
 *
 * All training labels come from two REAL, independently stepped bodies
 * (same pre-action history; single local impulse only in one body). The
 * model never receives true future body coordinates or rewards as inputs.
 * A lab matched-control difference is NOT an observation available to
 * the organism during an autonomous life; no live network/OTA integration.
 *
 * Fixed deterministic reservoir + learned ridge readout is a recurrent
 * state-space baseline, NOT a fully trained GRU and NOT proof of agency.
 */
public final class Zero3MotorRecurrentTrajectoryRegression {
    static final int HISTORY=96,HORIZON=384,STEP=12,BINS=HORIZON/STEP;
    static final int INPUT=Zero3MotorTemporalHistoryRegression.FEATURES;
    static final int HIDDEN=24, FEATURES=1+INPUT+HIDDEN+3;
    static final int[] SECTORS={0,8};
    static final float[] FIT={0,(float)(Math.PI/2)};
    static final float[] CAL={(float)(Math.PI/8),(float)(-Math.PI/8)};
    // Not used in any earlier V3.6/3.7/3.7-B final validation.
    static final float[] BLIND={(float)(Math.PI/18),(float)(-Math.PI/18),
                    (float)(4*Math.PI/9),(float)(-4*Math.PI/9)};
    static final float[][] DIR={{1,0},{0,1},{-1,0},{0,-1}};
    static void check(boolean a,String error){
        if(!a)throw new AssertionError("ZERO3 RECURRENT V38 "+error);
    }
    static final class Trajectory {
        final Zero3MotorTemporalHistoryRegression.Case caseData;
        final double[][] response=new double[BINS][2]; // signed displacement / work
        Trajectory(Zero3MotorTemporalHistoryRegression.Case sample){
            caseData=sample;
        }
        double immediate(){return response[0][0];}
        double late(){return response[BINS-1][0]-immediate();}
        double work(){return response[BINS-1][1];}
    }
    /**
     * Explicitly capture 96 REAL executed commands and observed proprioception
     * before the intervention. Both physical branches receive identical commands
     * until the intervention starts.
     */
    static Trajectory physical(int dir,float angle,int precondition,
                               int sector,int channel,int sign){
        Zero3MotorImpulseResponseRegression.Scenario control=
            new Zero3MotorImpulseResponseRegression.Scenario(dir,angle,precondition);
        Zero3MotorImpulseResponseRegression.Scenario acted=
            new Zero3MotorImpulseResponseRegression.Scenario(dir,angle,precondition);
        Zero3MotorTemporalHistoryRegression.MotorHistory memory=
            new Zero3MotorTemporalHistoryRegression.MotorHistory();
        Zero3MotorTemporalHistoryRegression.Schedule schedule=
            new Zero3MotorTemporalHistoryRegression.Schedule(
                Zero3MotorTemporalHistoryRegression.seed(dir,angle,precondition));
        double[] now=null,history=null;
        Trajectory data=null;
        for(int frame=0;frame<HISTORY+HORIZON;frame++){
            if(frame==HISTORY){
                now=Zero3MotorLongHorizonRegression.context(dir,acted.body,
                    acted.skin,acted.gel,sector,channel,sign);
                history=memory.features(dir,sector);
                check(Math.abs(acted.body.cx-control.body.cx)<.00001 &&
                      Math.abs(acted.body.cy-control.body.cy)<.00001 &&
                      Math.abs(acted.body.cvx-control.body.cvx)<.00001,
                      "paired bodies diverged before pulse");
                data=new Trajectory(new Zero3MotorTemporalHistoryRegression.Case(
                    dir,angle,precondition,sector,channel,sign,
                    now,history,0,0,0));
            }
            float[] m0=control.cortex.command(
                control.intent,control.body,control.skin,.025f);
            float[] m1=acted.cortex.command(
                acted.intent,acted.body,acted.skin,.025f);
            if(frame<HISTORY){
                schedule.apply(frame,m0);
                schedule.apply(frame,m1);
            }
            if(frame>=HISTORY&&frame<HISTORY+12)
                Zero3MotorTemporalHistoryRegression.pulse(
                    m1,sector,channel,sign);
            control.gel.step(control.body,control.skin,m0,.025f);
            acted.gel.step(acted.body,acted.skin,m1,.025f);
            check(control.gel.invalid==0&&acted.gel.invalid==0&&
                Float.isFinite(control.body.cx)&&Float.isFinite(acted.body.cx),
                "invalid physical evolution");
            if(frame<HISTORY)memory.append(m1,acted.body);
            else if((frame-HISTORY+1)%STEP==0){
                int bin=(frame-HISTORY+1)/STEP-1;
                data.response[bin][0]=(acted.body.cx-control.body.cx)*DIR[dir][0]+
                                    (acted.body.cy-control.body.cy)*DIR[dir][1];
                data.response[bin][1]=acted.gel.work-control.gel.work;
            }
        }
        check(data!=null,"missing physical trajectory");
        // Do NOT put the outcomes into the model input, even in training.
        Zero3MotorTemporalHistoryRegression.Case s=data.caseData;
        Trajectory result=new Trajectory(
            new Zero3MotorTemporalHistoryRegression.Case(dir,angle,precondition,
                sector,channel,sign,now,history,data.immediate(),data.late(),data.work()));
        for(int t=0;t<BINS;t++)System.arraycopy(data.response[t],0,result.response[t],0,2);
        check(Math.abs(result.caseData.early-result.immediate())<1e-9,
            "causal first-frame outcome mismatch");
        return result;
    }
    static ArrayList<Trajectory> collect(float[] poses,int[] conditions){
        ArrayList<Trajectory> result=new ArrayList<>();
        for(int direction=0;direction<4;direction++)
            for(float pose:poses)for(int condition:conditions)
                for(int channel=0;channel<4;channel++)
                    for(int sector:SECTORS)
                        for(int sign:new int[]{-1,1})
                            result.add(physical(direction,pose,condition,
                                sector,channel,sign));
        return result;
    }
    static ArrayList<Zero3MotorTemporalHistoryRegression.Case> cases(
        ArrayList<Trajectory> records){
        ArrayList<Zero3MotorTemporalHistoryRegression.Case> data=new ArrayList<>();
        for(Trajectory t:records)data.add(t.caseData);
        return data;
    }
    /** Deterministic untrained recurrent layer with real temporal state. */
    static final class Reservoir {
        final double[][] input=new double[HIDDEN][INPUT];
        final double[][] recurrent=new double[HIDDEN][HIDDEN];
        final double[] pulse=new double[HIDDEN],time=new double[HIDDEN];
        final double[] mean=new double[INPUT],std=new double[INPUT];
        Reservoir(ArrayList<Trajectory> train){
            for(Trajectory trial:train)
                for(int i=0;i<INPUT;i++)mean[i]+=trial.caseData.historical[i];
            for(int i=0;i<INPUT;i++)mean[i]/=train.size();
            for(Trajectory trial:train)
                for(int i=0;i<INPUT;i++){
                    double d=trial.caseData.historical[i]-mean[i];
                    std[i]+=d*d;
                }
            for(int i=0;i<INPUT;i++)
                std[i]=Math.max(.015,Math.sqrt(std[i]/train.size()));
            Random r=new Random(0x388a19fL);
            for(int j=0;j<HIDDEN;j++){
                for(int i=0;i<INPUT;i++)
                    input[j][i]=r.nextGaussian()*.65/Math.sqrt(INPUT);
                for(int k=0;k<HIDDEN;k++)
                    recurrent[j][k]=r.nextGaussian()*.65/Math.sqrt(HIDDEN);
                pulse[j]=r.nextGaussian()*.55;
                time[j]=r.nextGaussian()*.12;
            }
        }
        double[] normalized(double[] raw){
            check(raw.length==INPUT,"invalid sensory frame");
            double[] v=new double[INPUT];
            for(int i=0;i<INPUT;i++){
                v[i]=Math.max(-8,Math.min(8,(raw[i]-mean[i])/std[i]));
                check(Double.isFinite(v[i]),"invalid observed sensory input");
            }
            return v;
        }
        /**
         * No future physics observations are read in this rollout; latent
         * h(t) is propagated only by h(t-1), INITIAL sensed frame and
         * known local pulse command (bin zero).
         */
        double[][] rolloutFeatures(Trajectory trial){
            double[] x=normalized(trial.caseData.historical);
            double[] h=new double[HIDDEN];
            double[][] snapshots=new double[BINS][FEATURES];
            for(int t=0;t<BINS;t++){
                double[] next=new double[HIDDEN];
                double drive=t==0?trial.caseData.sign*.2:0;
                for(int j=0;j<HIDDEN;j++){
                    double z=drive*pulse[j]+time[j]*t/(double)BINS;
                    for(int i=0;i<INPUT;i++)z+=input[j][i]*x[i];
                    for(int k=0;k<HIDDEN;k++)z+=recurrent[j][k]*h[k];
                    next[j]=.7*h[j]+.3*Math.tanh(z);
                }
                h=next;
                double[] row=snapshots[t];row[0]=1;
                System.arraycopy(x,0,row,1,INPUT);
                System.arraycopy(h,0,row,1+INPUT,HIDDEN);
                row[FEATURES-3]=drive;
                row[FEATURES-2]=(t+1)/(double)BINS;
                row[FEATURES-1]=Math.log1p(t);
            }
            return snapshots;
        }
    }
    /** Fits measured cumulative physical response at EACH 12-frame horizon. */
    static final class LearnedDynamics {
        final Reservoir encoder;
        final double[][] w=new double[2][FEATURES];
        LearnedDynamics(ArrayList<Trajectory> train){
            encoder=new Reservoir(train);
            double[][] gram=new double[FEATURES][FEATURES];
            double[][] target=new double[2][FEATURES];
            for(Trajectory trace:train){
                double[][] x=encoder.rolloutFeatures(trace);
                for(int t=0;t<BINS;t++){
                    double[] f=x[t];
                    double[] y={trace.response[t][0],
                                1000*trace.response[t][1]};
                    for(int i=0;i<FEATURES;i++){
                        for(int j=0;j<FEATURES;j++)gram[i][j]+=f[i]*f[j];
                        for(int k=0;k<2;k++)target[k][i]+=f[i]*y[k];
                    }
                }
            }
            for(int k=0;k<2;k++){
                double[][] a=new double[FEATURES][FEATURES];
                for(int i=0;i<FEATURES;i++){
                    System.arraycopy(gram[i],0,a[i],0,FEATURES);
                    a[i][i]+=3;
                }
                w[k]=Zero3MotorResponseKernel.solve(a,target[k]);
            }
        }
        double[][] predict(Trajectory trace){
            double[][] features=encoder.rolloutFeatures(trace);
            double[][] response=new double[BINS][2];
            for(int t=0;t<BINS;t++){
                for(int k=0;k<2;k++)
                    for(int i=0;i<FEATURES;i++)
                        response[t][k]+=w[k][i]*features[t][i];
                response[t][1]/=1000;
            }
            return response;
        }
    }
    static final class Metrics {
        final int n;
        final double[] mse=new double[3],zero=new double[3];
        double pathMse,pathZeroMse;
        Metrics(ArrayList<Trajectory> cases,LearnedDynamics m){
            n=cases.size();
            for(Trajectory record:cases){
                double[][] pred=m.predict(record);
                double[] actual={record.immediate(),record.late(),record.work()};
                double[] estimation={pred[0][0],pred[BINS-1][0]-pred[0][0],
                                      pred[BINS-1][1]};
                for(int k=0;k<3;k++){
                    mse[k]+=Math.pow(actual[k]-estimation[k],2);
                    zero[k]+=actual[k]*actual[k];
                }
                for(int t=0;t<BINS;t++){
                    pathMse+=Math.pow(pred[t][0]-record.response[t][0],2);
                    pathZeroMse+=Math.pow(record.response[t][0],2);
                }
            }
            for(int k=0;k<3;k++){mse[k]/=n;zero[k]/=n;}
            pathMse/=n*BINS;pathZeroMse/=n*BINS;
        }
        boolean gate(){
            return mse[0]<zero[0]*.9&&mse[1]<zero[1]*.9&&
                   mse[2]<zero[2]*.9;
        }
        String show(){
            return String.format(Locale.US,
                "n=%d earlyMSE=%.9f zeroEarly=%.9f"+
                " lateMSE=%.9f zeroLate=%.9f"+
                " workMSE=%.12f zeroWork=%.12f"+
                " pathMSE=%.9f zeroPath=%.9f gate=%s",
                n,mse[0],zero[0],mse[1],zero[1],
                mse[2],zero[2],pathMse,pathZeroMse,gate());
        }
    }
    static double[] staticError(
        Zero3MotorTemporalHistoryRegression.Ridge old,
        ArrayList<Zero3MotorTemporalHistoryRegression.Case> samples){
        double[] mse=new double[3];
        for(Zero3MotorTemporalHistoryRegression.Case s:samples){
            double[] p=old.predict(s),actual=s.target();
            for(int j=0;j<3;j++)mse[j]+=Math.pow(p[j]-actual[j],2);
        }
        for(int j=0;j<3;j++)mse[j]/=samples.size();
        return mse;
    }
    public static void main(String[] args){
        ArrayList<Trajectory> fit=collect(FIT,new int[]{0,1,2});
        ArrayList<Trajectory> cal=collect(CAL,new int[]{0,1,2});
        check(fit.size()==384&&cal.size()==384,"missing physical train or calibration");
        LearnedDynamics recurrent=new LearnedDynamics(fit);
        Zero3MotorTemporalHistoryRegression.Ridge static68=
            new Zero3MotorTemporalHistoryRegression.Ridge(cases(fit),INPUT);
        Metrics calM=new Metrics(cal,recurrent);
        double[] cal68=staticError(static68,cases(cal));
        boolean calibrated=calM.gate();
        System.out.println("ZERO3 V38 CAL RECURRENT "+calM.show());
        System.out.printf(Locale.US,
            "ZERO3 V38 CAL STATIC68 early=%.9f late=%.9f work=%.12f%n",
            cal68[0],cal68[1],cal68[2]);

        // New holdout angles +/-10 and +/-80 degrees, precondition state 3.
        // Neither fitted weights nor the calibration gate ever see them.
        ArrayList<Trajectory> blind=collect(BLIND,new int[]{3});
        check(blind.size()==256,"incomplete blind physics");
        Metrics blindM=new Metrics(blind,recurrent);
        double[] blind68=staticError(static68,cases(blind));
        System.out.println("ZERO3 V38 NEW BLIND RECURRENT "+blindM.show());
        System.out.printf(Locale.US,
            "ZERO3 V38 NEW BLIND STATIC68 early=%.9f late=%.9f work=%.12f%n",
            blind68[0],blind68[1],blind68[2]);
        double lateGain=100*(1-blindM.mse[1]/blind68[1]);
        double delayedZeroGain=100*(1-blindM.mse[1]/blindM.zero[1]);
        int useful=0,unsafe=0,picks=0;
        double signed=0,extraWork=0;
        for(int d=0;d<4;d++)for(float pose:BLIND){
            Trajectory best=null;
            double bestScore=0;
            for(Trajectory v:blind){
                if(v.caseData.dir!=d||Math.abs(v.caseData.pose-pose)>.001f)continue;
                double[][] predicted=recurrent.predict(v);
                double score=predicted[BINS-1][0]-
                             45*Math.max(0,predicted[BINS-1][1]);
                if(calibrated&&score>bestScore){
                    bestScore=score;best=v;
                }
            }
            if(best!=null){
                picks++;
                if(best.response[BINS-1][0]>0&&best.response[BINS-1][1]<.005)
                    useful++;
                if(best.response[BINS-1][1]>=.005)unsafe++;
                signed+=best.response[BINS-1][0];
                extraWork+=best.response[BINS-1][1];
            }
        }
        System.out.printf(Locale.US,
            "ZERO3 V38 SUMMARY trainPairs=%d calPairs=%d newBlindPairs=%d"+
            " pairedPhysicsFrames=%d bins=%d recurrentUnits=%d"+
            " calibrationGate=%s blindGate=%s"+
            " delayedMseGainVsStaticPercent=%.3f delayedGainVsZeroPercent=%.3f"+
            " selectedUseful=%d/%d selectedCostly=%d"+
            " blindSelectedSigned=%.5f extraWork=%.7f"+
            " (no online learning / no game integration / no OTA)%n",
            fit.size(),cal.size(),blind.size(),
            (fit.size()+cal.size()+blind.size())*2*(HISTORY+HORIZON),
            BINS,HIDDEN,calibrated,blindM.gate(),
            lateGain,delayedZeroGain,useful,picks,unsafe,signed,extraWork);
    }
}
