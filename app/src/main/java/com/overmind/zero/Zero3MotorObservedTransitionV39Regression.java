package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 V3.9 — SINGLE-BODY observed state(t), executedAction(t) -> state(t+1).
 * Research E2 only: 12-frame material transitions with a real 64-actuator
 * efference summary. No paired counterfactual is a model input.
 *
 * The 32-step open-loop evaluation gets a prerecorded FUTURE action tape,
 * not future sensor states. It therefore does NOT prove live planning,
 * self-generated action forecasting, autonomous walking, or G3/G5.
 */
public final class Zero3MotorObservedTransitionV39Regression {
    static final int STATE=18, ACTION=64, DIM=1+STATE+ACTION;
    static final int OUT=STATE+3, HISTORY=96, BLOCK=12, STEPS=32;
    static final float DT=.025f;
    static final int[] ZONES={0,8};
    static final float[] FIT={0f,(float)(Math.PI/2)};
    static final float[] CAL={(float)(Math.PI/8),(float)(-Math.PI/8)};
    static final float[] BLIND={(float)(Math.PI/18),(float)(-Math.PI/18),
        (float)(11*Math.PI/36),(float)(-11*Math.PI/36)};

    private Zero3MotorObservedTransitionV39Regression(){}
    static void check(boolean value,String explanation){
        if(!value)throw new AssertionError("ZERO3 V39 "+explanation);
    }
    static double bounded(double value){
        check(Double.isFinite(value),"nonfinite sensed state");
        return Math.max(-5.,Math.min(5.,value));
    }
    /** Only measurements an isolated gel body can read at this frame. */
    static double[] observe(Zero3MotorImpulseResponseRegression.Scenario a){
        MotorBrain.Body b=a.body;
        Morphology.Surface s=a.skin;
        SymbioteMatterV1 g=a.gel;
        double radial=0,radialSq=0,vr=0,vt=0,held=0,touch=0;
        double x0=0,y0=0,local0=0,local8=0;
        int per=Morphology.POINTS/16;
        for(int i=0;i<Morphology.POINTS;i++){
            double xx=s.x[i]-b.cx,yy=s.y[i]-b.cy;
            double r=Math.hypot(xx,yy);
            radial+=r;radialSq+=r*r;
            if(r>.0001){
                vr+=((s.vx[i]-b.cvx)*xx+(s.vy[i]-b.cvy)*yy)/r;
                vt+=((s.vx[i]-b.cvx)*(-yy)+(s.vy[i]-b.cvy)*xx)/r;
            }
            held+=s.gripHold[i]>Morphology.GRIP_HOLD?1:0;
            touch+=s.touchSense[i];
            if(i<per){x0+=xx;y0+=yy;local0+=r;}
            if(i>=8*per&&i<9*per)local8+=r;
        }
        double mean=radial/Morphology.POINTS;
        double variance=Math.max(0,radialSq/Morphology.POINTS-mean*mean);
        double mag=Math.hypot(x0,y0);
        double ix=0,iy=0;
        for(int i=0;i<SymbioteMatterV1.INNER;i++){
            ix+=g.vx[i]-b.cvx;iy+=g.vy[i]-b.cvy;
        }
        double[] v={
            b.cvx/10.,b.cvy/10.,mean/28.-1,Math.sqrt(variance)/28.,
            vr/Morphology.POINTS/10.,vt/Morphology.POINTS/10.,
            held/Morphology.POINTS,touch/Morphology.POINTS,
            g.areaError/500.,g.lastMuscleReactionImpulse/50.,
            g.lastElasticForceMagnitude/200.,
            mag>.0001?x0/mag:0,mag>.0001?y0/mag:0,
            local0/per/28.-1,local8/per/28.-1,
            ix/SymbioteMatterV1.INNER/10.,iy/SymbioteMatterV1.INNER/10.,
            Math.hypot(ix,iy)/SymbioteMatterV1.INNER/10.
        };
        check(v.length==STATE,"sensor state width");
        for(int i=0;i<STATE;i++)v[i]=bounded(v[i]);
        return v;
    }
    static final class Trace {
        final double[][] state=new double[STEPS+1][STATE];
        final double[][] action=new double[STEPS][ACTION];
        final double[][] output=new double[STEPS][3]; // real dx,dy,work
    }
    /**
     * One live physical body per experiment. Deterministic muscle excitations
     * are laboratory-only system identification; never a shipped gait.
     */
    static Trace collectOne(int dir,float pose,int condition,
                            int zone,int channel,int sign){
        Zero3MotorImpulseResponseRegression.Scenario a=
            new Zero3MotorImpulseResponseRegression.Scenario(dir,pose,condition);
        Zero3MotorTemporalHistoryRegression.Schedule schedule=
            new Zero3MotorTemporalHistoryRegression.Schedule(
                Zero3MotorTemporalHistoryRegression.seed(dir,pose,condition));
        Trace record=new Trace();
        double beforeX=0,beforeY=0,beforeWork=0;
        int end=HISTORY+STEPS*BLOCK;
        for(int frame=0;frame<end;frame++){
            if(frame==HISTORY){
                record.state[0]=observe(a);
                beforeX=a.body.cx;beforeY=a.body.cy;
                beforeWork=a.gel.work;
            }
            float[] muscles=a.cortex.command(a.intent,a.body,a.skin,DT);
            schedule.apply(frame%HISTORY,muscles);
            if(frame>=HISTORY){
                int block=(frame-HISTORY)/BLOCK;
                if(block%4==0)
                    Zero3MotorTemporalHistoryRegression.pulse(muscles,zone,channel,
                        ((block/4)&1)==0?sign:-sign);
                if(block%4==2)
                    Zero3MotorTemporalHistoryRegression.pulse(muscles,
                        (zone+block/2)%16,(channel+1)%4,-sign);
                for(int j=0;j<ACTION;j++){
                    check(Float.isFinite(muscles[j])&&Math.abs(muscles[j])<=1.00001f,
                        "not a legal executed 16x4 command");
                    record.action[block][j]+=muscles[j]/BLOCK;
                }
            }
            a.gel.step(a.body,a.skin,muscles,DT);
            check(a.gel.invalid==0&&Float.isFinite(a.body.cx)&&
                Float.isFinite(a.body.cy),"invalid real physical material");
            if(frame>=HISTORY&&(frame-HISTORY+1)%BLOCK==0){
                int k=(frame-HISTORY+1)/BLOCK;
                record.state[k]=observe(a);
                double work=a.gel.work-beforeWork;
                check(work>=-1e-5&&Double.isFinite(work),"invalid work accounting");
                record.output[k-1][0]=a.body.cx-beforeX;
                record.output[k-1][1]=a.body.cy-beforeY;
                record.output[k-1][2]=Math.max(0,work);
                beforeX=a.body.cx;beforeY=a.body.cy;beforeWork=a.gel.work;
            }
        }
        return record;
    }
    static ArrayList<Trace> collect(float[] poses,int[] conditions){
        ArrayList<Trace> rows=new ArrayList<>();
        for(int dir=0;dir<4;dir++)for(float pose:poses)
            for(int state:conditions)for(int channel=0;channel<4;channel++)
                for(int zone:ZONES)for(int sign:new int[]{-1,1})
                    rows.add(collectOne(dir,pose,state,zone,channel,sign));
        return rows;
    }
    static double[] input(double[] state,double[] action,boolean useActions){
        double[] x=new double[DIM];
        x[0]=1;
        System.arraycopy(state,0,x,1,STATE);
        if(useActions)System.arraycopy(action,0,x,1+STATE,ACTION);
        return x;
    }
    static double[] target(Trace t,int k){
        double[] y=new double[OUT];
        for(int i=0;i<STATE;i++)y[i]=t.state[k+1][i]-t.state[k][i];
        System.arraycopy(t.output[k],0,y,STATE,3);
        y[OUT-1]*=1000.; // only numerical target scaling
        return y;
    }
    /**
     * All normalization, gram matrices, and weights are TRAIN ONLY.
     * Separate no-action ridge fit is an identical-capacity ablation.
     */
    static final class Model {
        final boolean actions;
        final double[] center=new double[DIM],scale=new double[DIM];
        final double[][] weights=new double[OUT][DIM];
        Model(ArrayList<Trace> train,boolean actionEnabled){
            actions=actionEnabled;
            int count=train.size()*STEPS;
            for(Trace t:train)for(int k=0;k<STEPS;k++){
                double[] x=input(t.state[k],t.action[k],actions);
                for(int i=1;i<DIM;i++)center[i]+=x[i]/count;
            }
            for(Trace t:train)for(int k=0;k<STEPS;k++){
                double[] x=input(t.state[k],t.action[k],actions);
                for(int i=1;i<DIM;i++){
                    double z=x[i]-center[i];
                    scale[i]+=z*z/count;
                }
            }
            scale[0]=1;
            for(int i=1;i<DIM;i++)scale[i]=Math.max(.03,Math.sqrt(scale[i]));
            double[][] gram=new double[DIM][DIM],right=new double[OUT][DIM];
            for(Trace t:train)for(int k=0;k<STEPS;k++){
                double[] x=input(t.state[k],t.action[k],actions),y=target(t,k);
                for(int i=0;i<DIM;i++)x[i]=(x[i]-center[i])/scale[i];
                for(int i=0;i<DIM;i++){
                    for(int j=0;j<=i;j++)gram[i][j]+=x[i]*x[j];
                    for(int h=0;h<OUT;h++)right[h][i]+=x[i]*y[h];
                }
            }
            for(int i=0;i<DIM;i++){
                for(int j=0;j<i;j++)gram[j][i]=gram[i][j];
                gram[i][i]+=12.; // fixed before evaluating CAL or BLIND
            }
            for(int h=0;h<OUT;h++){
                double[][] copy=new double[DIM][DIM];
                for(int i=0;i<DIM;i++)
                    System.arraycopy(gram[i],0,copy[i],0,DIM);
                weights[h]=Zero3MotorResponseKernel.solve(copy,right[h]);
                for(double w:weights[h])check(Double.isFinite(w),"nonfinite fitted weight");
            }
        }
        double[] predict(double[] state,double[] action){
            double[] x=input(state,action,actions),y=new double[OUT];
            for(int i=0;i<DIM;i++)x[i]=(x[i]-center[i])/scale[i];
            for(int h=0;h<OUT;h++)
                for(int i=0;i<DIM;i++)y[h]+=weights[h][i]*x[i];
            y[OUT-1]/=1000.;
            for(double v:y)check(Double.isFinite(v),"nonfinite prediction");
            return y;
        }
    }
    static final class Metrics {
        final int n;
        double one,oneZero,oneBallistic,stateError;
        double path,pathZero,pathBallistic;
        Metrics(ArrayList<Trace> cases,Model model){
            n=cases.size();
            for(Trace t:cases){
                double px=0,py=0,rx=0,ry=0;
                double[] state=t.state[0].clone();
                double startingVx=t.state[0][0]*10.,startingVy=t.state[0][1]*10.;
                for(int k=0;k<STEPS;k++){
                    // One-step: real state at the beginning of this block.
                    double[] now=model.predict(t.state[k],t.action[k]);
                    double bx=t.state[k][0]*10.*DT*BLOCK;
                    double by=t.state[k][1]*10.*DT*BLOCK;
                    double dx=t.output[k][0],dy=t.output[k][1];
                    one+=sq(now[STATE]-dx)+sq(now[STATE+1]-dy);
                    oneZero+=sq(dx)+sq(dy);
                    oneBallistic+=sq(bx-dx)+sq(by-dy);
                    for(int i=0;i<STATE;i++)
                        stateError+=sq(t.state[k][i]+now[i]-t.state[k+1][i]);
                    // Open-loop over observed ACTUATOR tape: no future STATES.
                    double[] forecast=model.predict(state,t.action[k]);
                    px+=forecast[STATE];py+=forecast[STATE+1];
                    for(int i=0;i<STATE;i++)
                        state[i]=bounded(state[i]+forecast[i]);
                    rx+=dx;ry+=dy;
                    double ballisticX=(k+1)*startingVx*DT*BLOCK;
                    double ballisticY=(k+1)*startingVy*DT*BLOCK;
                    path+=sq(px-rx)+sq(py-ry);
                    pathZero+=sq(rx)+sq(ry);
                    pathBallistic+=sq(ballisticX-rx)+sq(ballisticY-ry);
                }
            }
            double denom=n*STEPS*2.;
            one/=denom;oneZero/=denom;oneBallistic/=denom;
            path/=denom;pathZero/=denom;pathBallistic/=denom;
            stateError/=n*STEPS*STATE;
        }
        static double sq(double a){return a*a;}
        boolean acceptableComparedWith(Metrics noActions){
            return one<.9*Math.min(oneZero,oneBallistic) &&
                   path<.9*Math.min(pathZero,pathBallistic) &&
                   one<.97*noActions.one && path<.97*noActions.path;
        }
        String describe(){
            return String.format(Locale.US,
                "n=%d oneStepXY=%.9f zero=%.9f ballistic=%.9f "+
                "stateOneStep=%.9f path32=%.9f pathZero=%.9f pathBallistic=%.9f",
                n,one,oneZero,oneBallistic,stateError,
                path,pathZero,pathBallistic);
        }
    }
    public static void main(String[] args){
        ArrayList<Trace> fit=collect(FIT,new int[]{0,1,2});
        ArrayList<Trace> cal=collect(CAL,new int[]{0,1,2});
        check(fit.size()==384&&cal.size()==384,"incorrect train/cal partitions");
        Model withActions=new Model(fit,true);
        Model noActions=new Model(fit,false);
        Metrics calAction=new Metrics(cal,withActions);
        Metrics calAblation=new Metrics(cal,noActions);
        boolean eligible=calAction.acceptableComparedWith(calAblation);
        System.out.println("ZERO3 V39 CAL ACTION "+calAction.describe());
        System.out.println("ZERO3 V39 CAL NO_ACTION "+calAblation.describe());
        System.out.println("ZERO3 V39 CAL GATE="+(eligible?"PASS":"FAIL"));
        // Evaluation is read-only: do not retune after seeing calibration.
        ArrayList<Trace> blind=collect(BLIND,new int[]{3});
        check(blind.size()==256,"incorrect blind count");
        Metrics unseenAction=new Metrics(blind,withActions);
        Metrics unseenAblation=new Metrics(blind,noActions);
        boolean blindGate=unseenAction.acceptableComparedWith(unseenAblation);
        System.out.println("ZERO3 V39 BLIND ACTION "+unseenAction.describe());
        System.out.println("ZERO3 V39 BLIND NO_ACTION "+unseenAblation.describe());
        System.out.printf(Locale.US,
            "ZERO3 V39 FINAL fit=%d cal=%d blind=%d solverSteps=%d "+
            "stateDim=%d actualActuators=%d horizonBlocks=%d "+
            "knownFutureActionTape=true futureStatesLeak=false "+
            "calibration=%s blind=%s promotion=false "+
            "G3_BLOCKED G5_BLOCKED NO_GAMEPLAY NO_OTA%n",
            fit.size(),cal.size(),blind.size(),
            (fit.size()+cal.size()+blind.size())*(HISTORY+BLOCK*STEPS),
            STATE,ACTION,STEPS,eligible?"PASS":"FAIL",blindGate?"PASS":"FAIL");
    }
}
