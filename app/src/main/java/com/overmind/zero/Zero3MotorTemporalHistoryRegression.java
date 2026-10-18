package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * ZERO3 V3.7 LAB: pre-action temporal memory of REAL executed muscle commands
 * and physics, compared fairly with a current-state-only model.
 *
 * 96 warmup frames contain small, irregular, seed-deterministic exploratory
 * motor pulses. They are applied IDENTICALLY to both matched real solvers.
 * Before the tested pulse, capture the last 96 REAL states/efference arrays.
 * Then apply exactly ONE local intervention in one physical branch for 12
 * frames and observe the delayed response until 384 frames after pulse start.
 *
 * Train / calibration / blind differ by pose, preconditioned state and seed.
 * Neither fitted model nor its features can see outcomes from the future.
 * Laboratory pulses are not a preprogrammed gait, food attraction or an
 * autonomous game learning policy. No V3 weights or OTA are modified.
 */
public final class Zero3MotorTemporalHistoryRegression {
    static final int WARMUP=96,HORIZON=384,TOTAL=WARMUP+HORIZON;
    static final int BASE=Zero3MotorLongHorizonRegression.F,FEATURES=BASE+16;
    static final int[] REGIONS={0,8};
    static final float DT=.025f;
    static final float[][] DIR={{1,0},{0,1},{-1,0},{0,-1}};
    static final float[] TRAIN={0f,(float)(Math.PI/2)};
    static final float[] CAL={(float)(Math.PI/8),(float)(-Math.PI/8)};
    static final float[] BLIND={(float)(Math.PI/6),(float)(-Math.PI/6),
                                 (float)(Math.PI/3),(float)(-Math.PI/3)};
    static void check(boolean yes,String message){
        if(!yes)throw new AssertionError("ZERO3 V37 TEMPORAL "+message);
    }
    /** Sliding history explicitly stores ACTUAL commands and POST-physics state. */
    static final class MotorHistory {
        final float[][] efference=new float[WARMUP][64];
        final float[] x=new float[WARMUP],y=new float[WARMUP];
        final float[] vx=new float[WARMUP],vy=new float[WARMUP];
        int count;
        void append(float[] executed,MotorBrain.Body body){
            check(executed!=null&&executed.length==64,"not actual 64 muscles");
            check(count<WARMUP,"history contains post-intervention data");
            System.arraycopy(executed,0,efference[count],0,64);
            x[count]=body.cx;y[count]=body.cy;
            vx[count]=body.cvx;vy[count]=body.cvy;
            count++;
        }
        double[] features(int dir,int sector){
            check(count==WARMUP,"insufficient history before action");
            double[] out=new double[16];
            float dx=DIR[dir][0],dy=DIR[dir][1];
            int[] lags={4,12,32,80};
            for(int j=0;j<lags.length;j++){
                int previous=WARMUP-1-lags[j],recent=WARMUP-1;
                out[j]=((x[recent]-x[previous])*dx+
                        (y[recent]-y[previous])*dy)/lags[j]/10.0;
                out[4+j]=((vx[recent]-vx[previous])*dx+
                          (vy[recent]-vy[previous])*dy)/10.0;
            }
            for(int channel=0;channel<4;channel++){
                for(int window: new int[]{12,80}){
                    double sum=0;
                    for(int t=WARMUP-window;t<WARMUP;t++)
                        for(int offset=-2;offset<=2;offset++)
                            sum+=efference[t][((sector+offset+16)%16)*4+channel];
                    out[(window==12?8:12)+channel]=sum/(window*5);
                }
            }
            for(double v:out)check(Double.isFinite(v),"nonfinite history");
            return out;
        }
    }
    /** Intent, pose, preconditioning and prior-muscle sequence are fixed
     * for EVERY candidate in a matched group. No candidate-specific history
     * or baseline selection confounding is permitted. */
    static int seed(int dir,float pose,int condition){
        long k=0x6938B14DL;
        k^=(long)dir*0x9e3779b9L;
        k^=(long)condition*0x85ebca6bL;
        k^=(long)Float.floatToIntBits(pose)*0xc2b2ae35L;
        return (int)(k^(k>>>32));
    }
    static final class Schedule {
        final int[] start={8,30,55,78};
        final int[] sector=new int[4],channel=new int[4],sign=new int[4];
        Schedule(int seed){
            Random random=new Random(seed);
            for(int i=0;i<4;i++){
                sector[i]=random.nextInt(16);
                channel[i]=random.nextInt(4);
                sign[i]=random.nextBoolean()?1:-1;
            }
        }
        void apply(int frame,float[] field){
            for(int i=0;i<4;i++){
                if(frame<start[i]||frame>=start[i]+10)continue;
                for(int offset=-1;offset<=1;offset++){
                    int idx=((sector[i]+offset+16)%16)*4+channel[i];
                    float v=field[idx]+.12f*sign[i]*(2-Math.abs(offset))/2;
                    field[idx]=Math.max(-1f,Math.min(1f,v));
                }
            }
        }
    }
    static final class Case {
        final int dir,condition,sector,channel,sign;
        final float pose;
        final double[] state=new double[BASE],historical=new double[FEATURES];
        final double early,late,work;
        Case(int direction,float angle,int stateId,int muscleSector,
             int muscleChannel,int polarity,double[] now,double[] trace,
             double e,double l,double w){
            dir=direction;pose=angle;condition=stateId;sector=muscleSector;
            channel=muscleChannel;sign=polarity;
            check(now.length==BASE&&trace.length==16,"wrong input contract");
            System.arraycopy(now,0,state,0,BASE);
            System.arraycopy(now,0,historical,0,BASE);
            System.arraycopy(trace,0,historical,BASE,trace.length);
            early=e;late=l;work=w;
            for(double value:historical)check(Double.isFinite(value),"bad feature");
            check(Double.isFinite(early)&&Double.isFinite(late)&&
                  Double.isFinite(work),"bad observed consequence");
        }
        double signed(){return early+late;}
        double[] target(){return new double[]{early,late,work};}
    }
    static void pulse(float[] array,int sector,int channel,int polarity){
        for(int offset=-2;offset<=2;offset++){
            int i=((sector+offset+16)%16)*4+channel;
            float next=array[i]+.2f*polarity*(3-Math.abs(offset))/3;
            array[i]=Math.max(-1,Math.min(1,next));
        }
    }
    static Case experiment(int dir,float pose,int condition,
                           int sector,int channel,int sign){
        Zero3MotorImpulseResponseRegression.Scenario untouched=
            new Zero3MotorImpulseResponseRegression.Scenario(dir,pose,condition);
        Zero3MotorImpulseResponseRegression.Scenario active=
            new Zero3MotorImpulseResponseRegression.Scenario(dir,pose,condition);
        MotorHistory history=new MotorHistory();
        Schedule schedule=new Schedule(seed(dir,pose,condition));
        double[] pre=null,trace=null;
        double early=0;
        for(int frame=0;frame<TOTAL;frame++){
            if(frame==WARMUP){
                pre=Zero3MotorLongHorizonRegression.context(dir,active.body,
                    active.skin,active.gel,sector,channel,sign);
                trace=history.features(dir,sector);
                // Two bodies must be physically identical immediately before
                // their ONLY differing intervention is delivered.
                check(Math.abs(active.body.cx-untouched.body.cx)<.00001 &&
                      Math.abs(active.body.cy-untouched.body.cy)<.00001 &&
                      Math.abs(active.body.cvx-untouched.body.cvx)<.00001,
                      "pre-action paired trajectories diverged");
            }
            float[] rest=untouched.cortex.command(
                untouched.intent,untouched.body,untouched.skin,DT);
            float[] act=active.cortex.command(
                active.intent,active.body,active.skin,DT);
            if(frame<WARMUP){
                schedule.apply(frame,rest);
                schedule.apply(frame,act);
            }
            if(frame>=WARMUP&&frame<WARMUP+12)
                pulse(act,sector,channel,sign);
            untouched.gel.step(untouched.body,untouched.skin,rest,DT);
            active.gel.step(active.body,active.skin,act,DT);
            check(untouched.gel.invalid==0&&active.gel.invalid==0&&
                  Float.isFinite(active.body.cx)&&Float.isFinite(active.body.cy),
                  "body invalid during actual solver step");
            if(frame<WARMUP)history.append(act,active.body);
            if(frame==WARMUP+11)
                early=(active.body.cx-untouched.body.cx)*DIR[dir][0]+
                      (active.body.cy-untouched.body.cy)*DIR[dir][1];
        }
        double finish=(active.body.cx-untouched.body.cx)*DIR[dir][0]+
                      (active.body.cy-untouched.body.cy)*DIR[dir][1];
        check(pre!=null&&trace!=null,"no pre-action memories");
        return new Case(dir,pose,condition,sector,channel,sign,
            pre,trace,early,finish-early,active.gel.work-untouched.gel.work);
    }
    static ArrayList<Case> samples(float[] poses,int[] conditions){
        ArrayList<Case> rows=new ArrayList<>();
        for(int dir=0;dir<4;dir++)for(float pose:poses)
            for(int condition:conditions)
                for(int channel=0;channel<4;channel++)
                    for(int sector:REGIONS)
                        for(int sign:new int[]{-1,1})
                            rows.add(experiment(dir,pose,condition,
                                sector,channel,sign));
        return rows;
    }
    static final class Ridge {
        final int dim;
        final double[][] w;
        Ridge(ArrayList<Case> train,int dim){
            this.dim=dim;w=new double[3][dim];
            double[][] gram=new double[dim][dim],ys=new double[3][dim];
            for(Case sample:train){
                double[] x=dim==BASE?sample.state:sample.historical;
                double[] targets={sample.early,sample.late,1000*sample.work};
                for(int i=0;i<dim;i++){
                    for(int j=0;j<dim;j++)gram[i][j]+=x[i]*x[j];
                    for(int j=0;j<3;j++)ys[j][i]+=x[i]*targets[j];
                }
            }
            for(int o=0;o<3;o++){
                double[][] g=new double[dim][dim];
                for(int i=0;i<dim;i++){
                    System.arraycopy(gram[i],0,g[i],0,dim);g[i][i]+=.5;
                }
                w[o]=Zero3MotorResponseKernel.solve(g,ys[o]);
            }
        }
        double[] predict(Case sample){
            double[] input=dim==BASE?sample.state:sample.historical;
            double[] value=new double[3];
            for(int o=0;o<3;o++)
                for(int i=0;i<dim;i++)value[o]+=w[o][i]*input[i];
            value[2]/=1000;
            return value;
        }
    }
    static final class Metrics {
        final double[] mse=new double[3],zero=new double[3];
        final int count;
        Metrics(Ridge network,ArrayList<Case> holdout){
            count=holdout.size();
            for(Case sample:holdout){
                double[] prediction=network.predict(sample),actual=sample.target();
                for(int i=0;i<3;i++){
                    mse[i]+=Math.pow(prediction[i]-actual[i],2);
                    zero[i]+=Math.pow(actual[i],2);
                }
            }
            for(int i=0;i<3;i++){mse[i]/=count;zero[i]/=count;}
        }
        boolean beatsZero(){
            return mse[0]<zero[0]*.9&&mse[1]<zero[1]*.9&&
                   mse[2]<zero[2]*.9;
        }
        String line(){
            return String.format(Locale.US,
                "N=%d immediate=%.9f zeroImmediate=%.9f "+
                "late=%.9f zeroLate=%.9f cost=%.12f zeroCost=%.12f "+
                "gate=%s",count,mse[0],zero[0],mse[1],zero[1],
                mse[2],zero[2],beatsZero()?"PASS":"FAIL");
        }
    }
    public static void main(String[] args){
        ArrayList<Case> fit=samples(TRAIN,new int[]{0,1,2});
        ArrayList<Case> validation=samples(CAL,new int[]{0,1,2});
        check(fit.size()==384&&validation.size()==384,"broken split");
        Ridge staticModel=new Ridge(fit,BASE);
        Ridge temporal=new Ridge(fit,FEATURES);
        Metrics staticCal=new Metrics(staticModel,validation);
        Metrics temporalCal=new Metrics(temporal,validation);
        System.out.println("ZERO3 V37 CAL 52 CURRENT-STATE "+staticCal.line());
        System.out.println("ZERO3 V37 CAL 68 MOTOR-HISTORY "+temporalCal.line());
        // Frozen BEFORE first unseen sample outcome is consulted.
        boolean calibrated=temporalCal.beatsZero();
        ArrayList<Case> blind=samples(BLIND,new int[]{3});
        check(blind.size()==256,"blind split count");
        Metrics staticBlind=new Metrics(staticModel,blind);
        Metrics temporalBlind=new Metrics(temporal,blind);
        System.out.println("ZERO3 V37 BLIND 52 CURRENT-STATE "+staticBlind.line());
        System.out.println("ZERO3 V37 BLIND 68 MOTOR-HISTORY "+temporalBlind.line());
        boolean improvedLate=temporalBlind.mse[1]<staticBlind.mse[1];
        boolean holdoutPassed=temporalBlind.beatsZero();
        int useful=0,cheap=0;
        double totalForward=0,totalCost=0;
        for(int dir=0;dir<4;dir++)for(float pose:BLIND){
            Case choice=null;
            double highest=0;
            for(Case sample:blind){
                if(sample.dir!=dir||Math.abs(sample.pose-pose)>.001f)continue;
                double[] p=temporal.predict(sample);
                double score=p[0]+p[1]-45*Math.max(0,p[2]);
                if(calibrated&&score>highest){choice=sample;highest=score;}
            }
            if(choice!=null&&choice.signed()>0)useful++;
            if(choice==null||choice.work<.005)cheap++;
            if(choice!=null){totalForward+=choice.signed();totalCost+=choice.work;}
        }
        System.out.printf(Locale.US,
            "ZERO3 V37 SUMMARY train=%d calibration=%d blind=%d"+
            " pairedPhysicalSteps=%d warmupRealFrames=%d causalHorizon=%d"+
            " calibrationGate=%s blindGate=%s blindLongHistoryBeats52=%s"+
            " usefulChosen=%d/16 costBelow0.005=%d/16 signedGain=%.5f"+
            " extraWork=%.7f NO_LEARNED_GAIT NO_OTA%n",
            fit.size(),validation.size(),blind.size(),
            (fit.size()+validation.size()+blind.size())*2*TOTAL,
            WARMUP,HORIZON,Boolean.toString(calibrated),
            Boolean.toString(holdoutPassed),Boolean.toString(improvedLate),
            useful,cheap,totalForward,totalCost);
    }
}
