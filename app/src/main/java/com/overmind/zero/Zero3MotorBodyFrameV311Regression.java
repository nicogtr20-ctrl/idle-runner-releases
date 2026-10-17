package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 V3.11: experimental single-body, body-frame, contact-conditioned
 * physics transition regression. Uses real gel solver and 64 muscles applied
 * per 0.025-second physics step; no production GameActivity or OTA integration.
 *
 * Body-frame measurements and action harmonics implement rotation-covariant
 * input/output geometry. The future action tape in test replay is KNOWN
 * from recorded real actions; no actual future BODY observations are fed to
 * an open-loop rollout. This is NOT a closed-loop autonomous controller.
 */
public final class Zero3MotorBodyFrameV311Regression {
    static final int STATE=22,ACT=24,INPUT=1+STATE+ACT,OUTPUT=STATE+3;
    static final int HISTORY=96,FRAMES=384;
    static final float DT=.025f;
    static final int[] ZONES={0,8};
    static final float[] FIT={0f,(float)(Math.PI/2)};
    static final float[] CAL={(float)(Math.PI/8),(float)(-Math.PI/8)};
    static final float[] BLIND={(float)(17*Math.PI/180),
        (float)(-39*Math.PI/180),(float)(71*Math.PI/180),
        (float)(-86*Math.PI/180)};
    private Zero3MotorBodyFrameV311Regression(){}
    static void check(boolean ok,String why){
        if(!ok)throw new AssertionError("ZERO3 V311 "+why);
    }
    static double clamp(double x,double lo,double hi){
        check(Double.isFinite(x),"nonfinite physical measurement/model");
        return Math.max(lo,Math.min(hi,x));
    }
    static double wrap(double a){
        while(a>Math.PI)a-=Math.PI*2;
        while(a<-Math.PI)a+=Math.PI*2;
        return a;
    }
    static double[] world(double radial,double tangential,double angle){
        return new double[]{Math.cos(angle)*radial-Math.sin(angle)*tangential,
            Math.sin(angle)*radial+Math.cos(angle)*tangential};
    }
    static double dotAxis(double x,double y,double angle){
        return Math.cos(angle)*x+Math.sin(angle)*y;
    }
    static double dotTangent(double x,double y,double angle){
        return -Math.sin(angle)*x+Math.cos(angle)*y;
    }
    static final class Observation {
        final double[] v=new double[STATE];
        final double theta;
        Observation(double angle){theta=angle;}
    }
    static Observation observe(Zero3MotorImpulseResponseRegression.Scenario t){
        MotorBrain.Body b=t.body;
        Morphology.Surface sk=t.skin;
        SymbioteMatterV1 gel=t.gel;
        final int per=Morphology.POINTS/16;
        double ax=0,ay=0;
        for(int i=0;i<per;i++){
            ax+=sk.x[i]-b.cx;ay+=sk.y[i]-b.cy;
        }
        double theta=Math.atan2(ay,ax);
        Observation o=new Observation(theta);
        double[] v=o.v;
        v[0]=dotAxis(b.cvx,b.cvy,theta)/10.;
        v[1]=dotTangent(b.cvx,b.cvy,theta)/10.;
        double sumR=0,sumR2=0,rv=0,tv=0,held=0,touch=0,omega=0;
        double[] harms=new double[8];
        for(int i=0;i<Morphology.POINTS;i++){
            double x=sk.x[i]-b.cx,y=sk.y[i]-b.cy;
            double sx=sk.vx[i]-b.cvx,sy=sk.vy[i]-b.cvy;
            double r=Math.hypot(x,y);
            sumR+=r;sumR2+=r*r;
            if(r>1e-4){
                rv+=(sx*x+sy*y)/r;
                tv+=(sy*x-sx*y)/r;
                omega+=(sy*x-sx*y)/(r*r);
            }
            boolean hasHold=sk.gripHold[i]>Morphology.GRIP_HOLD;
            double grip=hasHold?1.:0.;
            double contact=Math.max(0,Math.min(1,sk.touchSense[i]));
            held+=grip;touch+=contact;
            // Harmonic modes refer to real sector identities in the body
            // anatomical frame; world rotation does not change these phases.
            double phase=2*Math.PI*i/Morphology.POINTS;
            double c=Math.cos(phase),s=Math.sin(phase);
            harms[0]+=r*c;harms[1]+=r*s;
            harms[2]+=r*Math.cos(2*phase);
            harms[3]+=r*Math.sin(2*phase);
            harms[4]+=grip*c;harms[5]+=grip*s;
            harms[6]+=contact*c;harms[7]+=contact*s;
        }
        double fluidX=0,fluidY=0;
        for(int i=0;i<SymbioteMatterV1.INNER;i++){
            fluidX+=gel.vx[i]-b.cvx;
            fluidY+=gel.vy[i]-b.cvy;
        }
        fluidX/=SymbioteMatterV1.INNER;fluidY/=SymbioteMatterV1.INNER;
        int n=Morphology.POINTS;
        v[2]=sumR/n/28.-1.;
        v[3]=Math.sqrt(Math.max(0,sumR2/n-Math.pow(sumR/n,2)))/28.;
        v[4]=rv/n/10.;v[5]=tv/n/10.;
        v[6]=held/n;v[7]=touch/n;v[8]=gel.areaError/500.;
        v[9]=dotAxis(fluidX,fluidY,theta)/10.;
        v[10]=dotTangent(fluidX,fluidY,theta)/10.;
        for(int j=0;j<4;j++)v[11+j]=harms[j]/n/28.;
        for(int j=0;j<4;j++)v[15+j]=harms[4+j]/n;
        v[19]=omega/n; // radians per physics-time-second
        v[20]=gel.lastMuscleReactionImpulse/50.;
        v[21]=gel.lastElasticForceMagnitude/200.;
        for(int i=0;i<STATE;i++)v[i]=clamp(v[i],-5.,5.);
        return o;
    }
    static final class Trace {
        final Observation[] observed=new Observation[FRAMES+1];
        final float[][] executed=new float[FRAMES][64];
        final double[][] xy=new double[FRAMES][2];
        int contacted,gripping;
    }
    static Trace physicallyRecorded(int split,int dir,float pose,int condition,
                                     int zone,int channel,int sign){
        Zero3MotorImpulseResponseRegression.Scenario sc=
            new Zero3MotorImpulseResponseRegression.Scenario(dir,pose,condition);
        Trace t=new Trace();
        int seed=Zero3MotorLocalFrameV310Regression.scheduleSeed(
            0x311+split*319,dir,pose,condition,zone,channel,sign);
        double previousX=0,previousY=0;
        for(int frame=0;frame<HISTORY+FRAMES;frame++){
            int step=frame-HISTORY;
            if(step==0){
                t.observed[0]=observe(sc);
                previousX=sc.body.cx;previousY=sc.body.cy;
            }
            float[] actual=sc.cortex.command(sc.intent,sc.body,sc.skin,DT);
            // Controlled system-identification perturbation, NOT a stored gait.
            Zero3MotorLocalFrameV310Regression.excitation(
                frame,seed,zone,channel,sign,actual);
            if(step>=0){
                System.arraycopy(actual,0,t.executed[step],0,64);
                for(float a:t.executed[step])
                    check(Float.isFinite(a)&&Math.abs(a)<=1.00001f,
                        "invalid executed 16x4 physical actuator");
            }
            sc.gel.step(sc.body,sc.skin,actual,DT);
            check(sc.gel.invalid==0&&Float.isFinite(sc.body.cx)&&
                Float.isFinite(sc.body.cy),"invalid true gel physics");
            if(step>=0){
                t.observed[step+1]=observe(sc);
                t.xy[step][0]=sc.body.cx-previousX;
                t.xy[step][1]=sc.body.cy-previousY;
                previousX=sc.body.cx;previousY=sc.body.cy;
                if(t.observed[step].v[7]>.001||t.observed[step+1].v[7]>.001)
                    t.contacted++;
                if(t.observed[step].v[6]>.001||t.observed[step+1].v[6]>.001)
                    t.gripping++;
            }
        }
        for(int i=0;i<FRAMES;i++)
            check(t.observed[i]!=null&&t.observed[i+1]!=null &&
                Double.isFinite(t.xy[i][0])&&Double.isFinite(t.xy[i][1]),
                "incomplete real experience");
        return t;
    }
    static ArrayList<Trace> generate(int split,float[] poses,int[] states){
        ArrayList<Trace> traces=new ArrayList<>();
        for(int d=0;d<4;d++)for(float pose:poses)for(int cond:states)
            for(int channel=0;channel<4;channel++)for(int sector:ZONES)
                for(int sign:new int[]{-1,1})
                    traces.add(physicallyRecorded(split,d,pose,cond,sector,channel,sign));
        return traces;
    }
    /**
     * Body-frame 64-command reduction is computed from ONLY known action
     * values and the body state currently supplied to the model.
     * NO future grip, future contact, or future world coordinates are read.
     */
    static double[] features(double[] state,float[] commands,
                             boolean useAction,boolean gripAware){
        double[] x=new double[INPUT];x[0]=1.;
        for(int j=0;j<STATE;j++){
            // Ablate contact/hold signals from the state itself as well.
            boolean blocked=!gripAware &&
                (j==6||j==7||j==15||j==16||j==17||j==18);
            x[1+j]=blocked?0:state[j];
        }
        if(!useAction)return x;
        double[][] stats=new double[4][4]; // mean, cos, sin, abs
        for(int sector=0;sector<16;sector++){
            double phase=2*Math.PI*(sector+.5)/16.;
            double co=Math.cos(phase),si=Math.sin(phase);
            for(int ch=0;ch<4;ch++){
                double v=commands[4*sector+ch];
                stats[ch][0]+=v/16.;
                stats[ch][1]+=v*co/16.;
                stats[ch][2]+=v*si/16.;
                stats[ch][3]+=Math.abs(v)/16.;
            }
        }
        for(int ch=0;ch<4;ch++){
            int at=1+STATE+6*ch;
            for(int j=0;j<4;j++)x[at+j]=stats[ch][j];
            x[at+4]=gripAware?stats[ch][0]*state[6]:0.;
            x[at+5]=gripAware?stats[ch][0]*state[7]:0.;
        }
        for(double a:x)check(Double.isFinite(a),"invalid reduced executed command");
        return x;
    }
    /** Body-state deltas, two world-displacement residuals in body frame,
     * and anatomical axis rotation from actual post-solver states. */
    static double[] label(Trace trace,int k){
        Observation before=trace.observed[k],after=trace.observed[k+1];
        double[] y=new double[OUTPUT];
        for(int i=0;i<STATE;i++)y[i]=after.v[i]-before.v[i];
        double x=trace.xy[k][0],z=trace.xy[k][1];
        y[STATE]=1000.*(dotAxis(x,z,before.theta)-before.v[0]*10.*DT);
        y[STATE+1]=1000.*(dotTangent(x,z,before.theta)-before.v[1]*10.*DT);
        y[STATE+2]=10.*wrap(after.theta-before.theta);
        return y;
    }
    /**
     * Multivariate ridge regression from immutable FIT-only frames. Fixed
     * regularizer, no calibration/blind fitting. Features normalized on FIT.
     */
    static final class Model {
        final boolean action,gripAware;
        final double[] avg=new double[INPUT],scale=new double[INPUT];
        final double[][] w=new double[OUTPUT][INPUT];
        Model(ArrayList<Trace> training,boolean useAction,boolean contacts){
            action=useAction;gripAware=contacts;
            int samples=0;
            for(Trace t:training)for(int k=0;k<FRAMES;k+=4){
                double[] f=features(t.observed[k].v,t.executed[k],action,gripAware);
                for(int j=1;j<INPUT;j++)avg[j]+=f[j];
                samples++;
            }
            check(samples>1000,"few physical training frames");
            for(int j=1;j<INPUT;j++)avg[j]/=samples;
            for(Trace t:training)for(int k=0;k<FRAMES;k+=4){
                double[] f=features(t.observed[k].v,t.executed[k],action,gripAware);
                for(int j=1;j<INPUT;j++){
                    double delta=f[j]-avg[j];scale[j]+=delta*delta;
                }
            }
            scale[0]=1.;
            for(int j=1;j<INPUT;j++)
                scale[j]=Math.max(.02,Math.sqrt(scale[j]/samples));
            double[][] gram=new double[INPUT][INPUT],rhs=new double[INPUT][OUTPUT];
            for(Trace t:training)for(int k=0;k<FRAMES;k+=4){
                double[] x=features(t.observed[k].v,t.executed[k],action,gripAware);
                double[] y=label(t,k);
                for(int j=1;j<INPUT;j++)x[j]=(x[j]-avg[j])/scale[j];
                for(int j=0;j<INPUT;j++){
                    for(int i=0;i<=j;i++)gram[j][i]+=x[j]*x[i];
                    for(int h=0;h<OUTPUT;h++)rhs[j][h]+=x[j]*y[h];
                }
            }
            for(int j=0;j<INPUT;j++){
                for(int i=0;i<j;i++)gram[i][j]=gram[j][i];
                gram[j][j]+=8.;
            }
            Zero3MotorLocalFrameV310Regression.choleskySolve(gram,rhs,w);
        }
        double[] estimate(double[] s,float[] cmd){
            double[] f=features(s,cmd,action,gripAware);
            double[] prediction=new double[OUTPUT];
            for(int j=1;j<INPUT;j++)f[j]=(f[j]-avg[j])/scale[j];
            for(int h=0;h<OUTPUT;h++){
                double sum=0;
                for(int j=0;j<INPUT;j++)sum+=w[h][j]*f[j];
                prediction[h]=clamp(sum,-5.,5.);
            }
            prediction[STATE]=clamp(prediction[STATE]/1000.,-.2,.2);
            prediction[STATE+1]=clamp(prediction[STATE+1]/1000.,-.2,.2);
            prediction[STATE+2]=clamp(prediction[STATE+2]/10.,-.2,.2);
            return prediction;
        }
    }
    static final class Metrics {
        final int n;
        long contactFrames,holdFrames;
        double xy,ballistic,zero,body,persist;
        final double[] path=new double[3],still=new double[3],ball=new double[3];
        Metrics(ArrayList<Trace> records,Model model){
            n=records.size();
            final int[] windows={16,96,384};
            for(Trace t:records){
                contactFrames+=t.contacted;holdFrames+=t.gripping;
                Observation initial=t.observed[0];
                double[] state=initial.v.clone();
                double angle=initial.theta;
                double startVx=world(state[0]*10.,state[1]*10.,angle)[0];
                double startVy=world(state[0]*10.,state[1]*10.,angle)[1];
                double predX=0,predY=0,actualX=0,actualY=0;
                for(int k=0;k<FRAMES;k++){
                    Observation now=t.observed[k];
                    double dx=t.xy[k][0],dy=t.xy[k][1];
                    double[] prediction=model.estimate(now.v,t.executed[k]);
                    double[] step=world(
                        now.v[0]*10.*DT+prediction[STATE],
                        now.v[1]*10.*DT+prediction[STATE+1],now.theta);
                    double[] standard=world(now.v[0]*10.*DT,now.v[1]*10.*DT,now.theta);
                    xy+=sq(dx-step[0])+sq(dy-step[1]);
                    ballistic+=sq(dx-standard[0])+sq(dy-standard[1]);
                    zero+=sq(dx)+sq(dy);
                    for(int j=0;j<STATE;j++){
                        body+=sq(now.v[j]+prediction[j]-t.observed[k+1].v[j]);
                        persist+=sq(now.v[j]-t.observed[k+1].v[j]);
                    }
                    // True body future remains unused in the recurrent
                    // rollout. Only recorded known 64D actions are replayed.
                    double[] guessed=model.estimate(state,t.executed[k]);
                    double[] predictedWorld=world(
                        state[0]*10.*DT+guessed[STATE],
                        state[1]*10.*DT+guessed[STATE+1],angle);
                    predX+=predictedWorld[0];predY+=predictedWorld[1];
                    for(int j=0;j<STATE;j++)
                        state[j]=clamp(state[j]+guessed[j],-5.,5.);
                    angle=wrap(angle+guessed[STATE+2]);
                    actualX+=dx;actualY+=dy;
                    for(int j=0;j<windows.length;j++)if(k<windows[j]){
                        path[j]+=sq(predX-actualX)+sq(predY-actualY);
                        still[j]+=sq(actualX)+sq(actualY);
                        double elapsed=(k+1)*DT;
                        ball[j]+=sq(startVx*elapsed-actualX)+
                            sq(startVy*elapsed-actualY);
                    }
                }
            }
            double denom=n*FRAMES*2.;
            xy/=denom;ballistic/=denom;zero/=denom;
            body/=n*FRAMES*STATE;persist/=n*FRAMES*STATE;
            for(int j=0;j<3;j++){
                double d=n*windows[j]*2.;
                path[j]/=d;still[j]/=d;ball[j]/=d;
            }
        }
        static double sq(double x){return x*x;}
        boolean passes(Metrics noAction,Metrics withoutContact){
            return xy<.90*ballistic && body<.90*persist &&
                xy<.97*noAction.xy && xy<.97*withoutContact.xy &&
                path[0]<.90*Math.min(still[0],ball[0]) &&
                path[1]<.90*Math.min(still[1],ball[1]);
        }
        String result(){
            return String.format(Locale.US,
                "n=%d contactFrames=%d gripFrames=%d xy=%.9f ballistic=%.9f "+
                "zero=%.9f state=%.9f persist=%.9f "+
                "path16=%.9f still16=%.9f ball16=%.9f "+
                "path96=%.9f still96=%.9f ball96=%.9f "+
                "path384=%.9f still384=%.9f ball384=%.9f",
                n,contactFrames,holdFrames,xy,ballistic,zero,body,persist,
                path[0],still[0],ball[0],path[1],still[1],ball[1],
                path[2],still[2],ball[2]);
        }
    }
    /** Exact geometry identity: rotating the reference world-frame angle by
     * 90 degrees rotates (dx,dy) without changing either body-frame input. */
    static void rotationProperty(){
        for(double a:new double[]{-.7,0,.31,1.6}){
            double[] xy=world(.173,-.044,a);
            double[] rot=world(.173,-.044,a+Math.PI/2.);
            check(Math.abs(rot[0]+xy[1])<1e-10 &&
                Math.abs(rot[1]-xy[0])<1e-10,
                "body/world frame equivariance identity");
        }
    }
    public static void main(String[] args){
        rotationProperty();
        ArrayList<Trace> training=generate(101,FIT,new int[]{0,1,2});
        check(training.size()==384,"bad train scenes/trajectories");
        Model full=new Model(training,true,true);
        Model noAction=new Model(training,false,true);
        Model noContact=new Model(training,true,false);
        training.clear();
        ArrayList<Trace> cal=generate(202,CAL,new int[]{0,1,2});
        check(cal.size()==384,"bad calibration scenes/trajectories");
        Metrics cFull=new Metrics(cal,full);
        Metrics cWithoutAct=new Metrics(cal,noAction);
        Metrics cWithoutGrip=new Metrics(cal,noContact);
        boolean calibrated=cFull.passes(cWithoutAct,cWithoutGrip);
        System.out.println("ZERO3 V311 CAL FULL "+cFull.result());
        System.out.println("ZERO3 V311 CAL NO_ACTION "+cWithoutAct.result());
        System.out.println("ZERO3 V311 CAL NO_GRIP_TOUCH "+cWithoutGrip.result());
        System.out.println("ZERO3 V311 CAL GATE="+(calibrated?"PASS":"FAIL"));
        cal.clear();
        // No optimization on calibration or blind labels, ever.
        ArrayList<Trace> blind=generate(303,BLIND,new int[]{3});
        check(blind.size()==256,"bad independent blind trajectories");
        Metrics bFull=new Metrics(blind,full);
        Metrics bWithoutAct=new Metrics(blind,noAction);
        Metrics bWithoutGrip=new Metrics(blind,noContact);
        boolean validated=bFull.passes(bWithoutAct,bWithoutGrip);
        System.out.println("ZERO3 V311 BLIND FULL "+bFull.result());
        System.out.println("ZERO3 V311 BLIND NO_ACTION "+bWithoutAct.result());
        System.out.println("ZERO3 V311 BLIND NO_GRIP_TOUCH "+bWithoutGrip.result());
        System.out.printf(Locale.US,
            "ZERO3 V311 SUMMARY training=%d calibration=%d blind=%d "+
            "realSolverSteps=%d state=%d originalActuators=64 aggregatedActions=%d "+
            "rotationalFrameProperty=true futureStatesLeak=false "+
            "actionTapeKnown=true newWorldScenes=false "+
            "calibration=%s blind=%s PROMOTION=false G3_BLOCKED G5_BLOCKED NO_OTA%n",
            384,384,256,1024*(HISTORY+FRAMES),STATE,ACT,
            calibrated?"PASS":"FAIL",validated?"PASS":"FAIL");
    }
}
