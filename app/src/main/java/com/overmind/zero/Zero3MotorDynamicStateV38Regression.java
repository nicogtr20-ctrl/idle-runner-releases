package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 V3.8 -- experimental discrete-time causal motor state-space model.
 *
 * Each training trace consists of TWO independent real physical gel solvers.
 * Both receive identical 96-frame motor experience, then only one receives
 * a short local pulse. We record 32 successive observed 12-frame responses.
 * The fit predicts a ONE-BLOCK physical state transition (delta displacement).
 *
 * Crucially, on calibration / blind the model forecasts all 32 blocks
 * recursively using ONLY its OWN predicted previous delta and pre-action
 * proprioception; it cannot access future physical outcomes.
 *
 * This diagnoses multistep dynamics. A paired laboratory baseline is NOT
 * information available inside a single organism's real lifetime. Nothing
 * here is wired to gameplay, APK, world-model or skill persistence.
 */
public final class Zero3MotorDynamicStateV38Regression {
    static final int HISTORY=96, BLOCK=12, STEPS=32;
    static final int MODEL=72, CONTEXT=Zero3MotorLongHorizonRegression.F;
    static final int[] ZONES={0,8};
    static final float[][] DIR={{1,0},{0,1},{-1,0},{0,-1}};
    static final float[] FIT={0f,(float)(Math.PI/2)};
    static final float[] CAL={(float)(Math.PI/8),(float)(-Math.PI/8)};
    // NOT used by previous V3.7 or V3.7-B final holdout evaluations.
    static final float[] BLIND={(float)(Math.PI/18),(float)(-Math.PI/18),
                                 (float)(11*Math.PI/36),(float)(-11*Math.PI/36)};
    static void check(boolean good,String why){
        if(!good)throw new AssertionError("ZERO3 V38 "+why);
    }
    static final class Trace {
        final int dir,sector,channel,sign,condition;
        final float pose;
        final double[] context;
        final double[] real=new double[STEPS+1];
        final double[] cost=new double[STEPS+1];
        Trace(int d,float p,int state,int z,int ch,int s,double[] sensed){
            dir=d;pose=p;condition=state;sector=z;channel=ch;sign=s;
            context=sensed;
            check(sensed.length==CONTEXT,"bad proprioception dimension");
        }
    }
    static Trace realPhysics(int d,float pose,int state,int sector,int channel,int sign){
        Zero3MotorImpulseResponseRegression.Scenario control=
            new Zero3MotorImpulseResponseRegression.Scenario(d,pose,state);
        Zero3MotorImpulseResponseRegression.Scenario treated=
            new Zero3MotorImpulseResponseRegression.Scenario(d,pose,state);
        Zero3MotorTemporalHistoryRegression.Schedule sched=
            new Zero3MotorTemporalHistoryRegression.Schedule(
                Zero3MotorTemporalHistoryRegression.seed(d,pose,state));
        double[] sensed=null;
        int end=HISTORY+BLOCK*STEPS;
        for(int frame=0;frame<end;frame++){
            if(frame==HISTORY){
                check(Math.abs(control.body.cx-treated.body.cx)<1e-5&&
                      Math.abs(control.body.cy-treated.body.cy)<1e-5&&
                      Math.abs(control.body.cvx-treated.body.cvx)<1e-5,
                      "matched physical motor history was not identical");
                sensed=Zero3MotorLongHorizonRegression.context(
                    d,treated.body,treated.skin,treated.gel,sector,channel,sign);
            }
            float[] a=control.cortex.command(control.intent,
                control.body,control.skin,.025f);
            float[] b=treated.cortex.command(treated.intent,
                treated.body,treated.skin,.025f);
            if(frame<HISTORY){
                sched.apply(frame,a);
                sched.apply(frame,b);
            }
            if(frame>=HISTORY&&frame<HISTORY+BLOCK)
                Zero3MotorTemporalHistoryRegression.pulse(b,sector,channel,sign);
            control.gel.step(control.body,control.skin,a,.025f);
            treated.gel.step(treated.body,treated.skin,b,.025f);
            check(control.gel.invalid==0&&treated.gel.invalid==0&&
                  Float.isFinite(control.body.cx)&&Float.isFinite(treated.body.cx),
                  "nonfinite real body");
            if(frame==HISTORY-1)
                check(sensed==null,"cannot read future before action");
        }
        // Above produces physical states, but does not preserve checkpoint
        // labels. Collect checkpoints via a second identical matched replay.
        // Explicitly assert deterministic same-history trajectories in replay.
        Zero3MotorImpulseResponseRegression.Scenario ctrl=
            new Zero3MotorImpulseResponseRegression.Scenario(d,pose,state);
        Zero3MotorImpulseResponseRegression.Scenario intervention=
            new Zero3MotorImpulseResponseRegression.Scenario(d,pose,state);
        Trace t=new Trace(d,pose,state,sector,channel,sign,sensed);
        for(int frame=0;frame<end;frame++){
            float[] a=ctrl.cortex.command(ctrl.intent,ctrl.body,ctrl.skin,.025f);
            float[] b=intervention.cortex.command(
                intervention.intent,intervention.body,intervention.skin,.025f);
            if(frame<HISTORY){
                sched.apply(frame,a);sched.apply(frame,b);
            }
            if(frame>=HISTORY&&frame<HISTORY+BLOCK)
                Zero3MotorTemporalHistoryRegression.pulse(b,sector,channel,sign);
            ctrl.gel.step(ctrl.body,ctrl.skin,a,.025f);
            intervention.gel.step(intervention.body,intervention.skin,b,.025f);
            if(frame>=HISTORY&&(frame-HISTORY+1)%BLOCK==0){
                int k=(frame-HISTORY+1)/BLOCK;
                t.real[k]=(intervention.body.cx-ctrl.body.cx)*DIR[d][0]+
                          (intervention.body.cy-ctrl.body.cy)*DIR[d][1];
                t.cost[k]=intervention.gel.work-ctrl.gel.work;
            }
        }
        check(Math.abs(ctrl.body.cx-control.body.cx)<1e-5&&
              Math.abs(intervention.body.cx-treated.body.cx)<1e-5,
              "replayed true physics not deterministic");
        return t;
    }
    static ArrayList<Trace> collect(float[] poses,int[] conditions){
        ArrayList<Trace> out=new ArrayList<>();
        for(int dir=0;dir<4;dir++)for(float pose:poses)
            for(int condition:conditions)
                for(int channel=0;channel<4;channel++)
                    for(int zone:ZONES)for(int sign:new int[]{-1,1})
                        out.add(realPhysics(dir,pose,condition,zone,channel,sign));
        return out;
    }
    /** Past delta alone is dynamically recurrent. Never read real future
     * delta in rollout; teacher forcing used ONLY during FIT. */
    static double[] feature(Trace trace,int step,double previous,double before){
        double[] x=new double[MODEL];
        System.arraycopy(trace.context,0,x,0,CONTEXT);
        double delta=previous-before;
        x[52]=previous;
        x[53]=delta;
        x[54]=before;
        double time=(double)step/STEPS;
        x[55]=time;x[56]=time*time;
        x[57]=Math.exp(-.14*step);
        x[58]=Math.exp(-.035*step);
        x[59]=Math.exp(-.008*step);
        x[60]=step==0?1:0;
        x[61]=step<4?1:0;
        x[62]=trace.sign*x[57];
        x[63]=trace.sign*x[58];
        x[64]=trace.context[24]*x[57];
        x[65]=trace.context[25]*x[58];
        x[66]=trace.context[26+trace.channel]*x[57];
        x[67]=trace.context[30+trace.channel]*x[58];
        x[68]=delta*x[57];
        x[69]=previous*time;
        x[70]=delta*time;
        x[71]=1;
        for(double v:x)check(Double.isFinite(v),"nonfinite physics feature");
        return x;
    }
    static final class Model {
        final double[] weights=new double[MODEL];
        final double[] center=new double[MODEL],scale=new double[MODEL];
        final double maxDelta;
        Model(ArrayList<Trace> training){
            // Normalization parameters estimated ONLY from teacher-forced
            // TRAIN trajectories; never from calibration or blind results.
            int n=training.size()*STEPS;
            for(Trace t:training)for(int k=0;k<STEPS;k++){
                double[] x=feature(t,k,t.real[k],k==0?0:t.real[k-1]);
                for(int j=0;j<MODEL;j++)center[j]+=x[j];
            }
            for(int j=0;j<MODEL;j++)center[j]/=n;
            double bound=0;
            for(Trace t:training)for(int k=0;k<STEPS;k++){
                double[] x=feature(t,k,t.real[k],k==0?0:t.real[k-1]);
                for(int j=0;j<MODEL;j++)
                    scale[j]+=(x[j]-center[j])*(x[j]-center[j]);
                bound=Math.max(bound,Math.abs(t.real[k+1]-t.real[k]));
            }
            for(int j=0;j<MODEL;j++)
                scale[j]=Math.max(.01,Math.sqrt(scale[j]/n));
            maxDelta=Math.max(.03,bound*1.25);
            double[][] gram=new double[MODEL][MODEL];
            double[] targets=new double[MODEL];
            for(Trace t:training)for(int k=0;k<STEPS;k++){
                double[] f=feature(t,k,t.real[k],k==0?0:t.real[k-1]);
                for(int j=0;j<MODEL;j++)f[j]=(f[j]-center[j])/scale[j];
                double output=t.real[k+1]-t.real[k];
                for(int j=0;j<MODEL;j++){
                    targets[j]+=f[j]*output;
                    for(int l=0;l<MODEL;l++)gram[j][l]+=f[j]*f[l];
                }
            }
            for(int j=0;j<MODEL;j++)gram[j][j]+=8; // fixed regularizer
            double[] w=Zero3MotorResponseKernel.solve(gram,targets);
            System.arraycopy(w,0,weights,0,MODEL);
        }
        double[] rollout(Trace t){
            double[] predicted=new double[STEPS+1];
            for(int k=0;k<STEPS;k++){
                double[] x=feature(t,k,predicted[k],
                    k==0?0:predicted[k-1]);
                double increment=0;
                for(int j=0;j<MODEL;j++)
                    increment+=weights[j]*(x[j]-center[j])/scale[j];
                // Bound derived solely from physically observed TRAIN.
                increment=Math.max(-maxDelta,Math.min(maxDelta,increment));
                predicted[k+1]=predicted[k]+increment;
                check(Double.isFinite(predicted[k+1]),"nonfinite free rollout");
            }
            return predicted;
        }
    }
    static final class StaticFinal {
        final Zero3MotorTemporalHistoryRegression.Ridge ridge;
        // Control: immediate, delayed outcome regression trained on the SAME
        // physical traces, with only current 52D body observations.
        final double[][] coefficients=new double[1][CONTEXT];
        StaticFinal(ArrayList<Trace> traces){
            // Existing V3.7-B model uses Case; here use exact same traces.
            double[][] gram=new double[CONTEXT][CONTEXT];
            double[] target=new double[CONTEXT];
            for(Trace t:traces){
                double[] x=t.context;
                for(int j=0;j<CONTEXT;j++){
                    target[j]+=x[j]*t.real[STEPS];
                    for(int i=0;i<CONTEXT;i++)gram[j][i]+=x[j]*x[i];
                }
            }
            for(int j=0;j<CONTEXT;j++)gram[j][j]+=.5;
            coefficients[0]=Zero3MotorResponseKernel.solve(gram,target);
            ridge=null;
        }
        double predict(Trace t){
            double p=0;
            for(int j=0;j<CONTEXT;j++)p+=coefficients[0][j]*t.context[j];
            return p;
        }
    }
    static final class Check {
        double endpointMse,zeroMse,staticMse,trajectoryMse,trajectoryZero;
        int n,positives;
        String describe(){
            return String.format(Locale.US,
                "n=%d zeroEndpointMse=%.9f dynamicEndpointMse=%.9f"+
                " static52EndpointMse=%.9f dynamicTrajectoryMse=%.9f"+
                " zeroTrajectoryMse=%.9f endpointsWithCorrectSign=%d",
                n,zeroMse/n,endpointMse/n,staticMse/n,
                trajectoryMse/(n*STEPS),trajectoryZero/(n*STEPS),positives);
        }
        boolean beatsZero(){
            return endpointMse<zeroMse*.9&&trajectoryMse<trajectoryZero*.9;
        }
    }
    static Check assess(ArrayList<Trace> cases,Model model,StaticFinal stat){
        Check out=new Check();
        for(Trace t:cases){
            double[] predicted=model.rollout(t);
            double last=t.real[STEPS],finalForecast=predicted[STEPS];
            double staticForecast=stat.predict(t);
            out.n++;
            out.zeroMse+=last*last;
            out.endpointMse+=Math.pow(last-finalForecast,2);
            out.staticMse+=Math.pow(last-staticForecast,2);
            if(finalForecast*last>0)out.positives++;
            for(int k=1;k<=STEPS;k++){
                out.trajectoryMse+=Math.pow(t.real[k]-predicted[k],2);
                out.trajectoryZero+=t.real[k]*t.real[k];
            }
        }
        return out;
    }
    public static void main(String[] args){
        ArrayList<Trace> train=collect(FIT,new int[]{0,1,2});
        ArrayList<Trace> calibration=collect(CAL,new int[]{0,1,2});
        check(train.size()==384&&calibration.size()==384,"bad partitions");
        Model dynamical=new Model(train);
        StaticFinal static52=new StaticFinal(train);
        Check cal=assess(calibration,dynamical,static52);
        boolean calibrated=cal.beatsZero();
        System.out.println("ZERO3 V38 CAL "+cal.describe()+
            " gate="+(calibrated?"PASS":"FAIL"));
        // Crucially, no additional fitting is allowed after reading CAL.
        ArrayList<Trace> blind=collect(BLIND,new int[]{3});
        check(blind.size()==256,"bad independent blind count");
        Check holdout=assess(blind,dynamical,static52);
        System.out.println("ZERO3 V38 BLIND "+holdout.describe()+
            " gate="+(holdout.beatsZero()?"PASS":"FAIL"));
        System.out.printf(Locale.US,
            "ZERO3 V38 SUMMARY train=%d calibration=%d blind=%d"+
            " solverSteps=%d freeRollout=true realCheckpointBlocks=32"+
            " calibrated=%s independentHoldout=%s"+
            " lateImprovementVsStaticPercent=%.3f"+
            " NO_ANDROID_ACTIVATION NO_GAIT NO_OTA%n",
            train.size(),calibration.size(),blind.size(),
            (train.size()+calibration.size()+blind.size())*4*
                (HISTORY+BLOCK*STEPS),
            Boolean.toString(calibrated),
            Boolean.toString(holdout.beatsZero()),
            100*(1-(holdout.endpointMse/Math.max(1e-12,holdout.staticMse))));
    }
}
