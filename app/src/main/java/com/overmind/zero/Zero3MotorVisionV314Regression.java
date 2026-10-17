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
public final class Zero3MotorVisionV314Regression {
    static final int STATE=41,ACT=24,INPUT=1+STATE+ACT,OUTPUT=STATE+4;
    static final int HISTORY=96,FRAMES=144;
    static final float DT=.025f;
    static final int[] ZONES={0,8};
    static final float[] FIT={0f,(float)(100*Math.PI/180)};
    static final float[] CAL={(float)(-24*Math.PI/180),(float)(46*Math.PI/180)};
    static final float[] BLIND={(float)(12*Math.PI/180),(float)(-53*Math.PI/180),
        (float)(69*Math.PI/180),(float)(-84*Math.PI/180)};
    static final int FREE=0,BOX_NEAR=1,BOX_FAR=2,DISK_NEAR=3,DISK_FAR=4;
    static final float[][] BOX_GAP={{45f,52f},{47f,54f},{44f,50f}};
    static final float[][] DISK_GAP={{46f,53f},{45f,51f},{47f,52f}};
    private Zero3MotorVisionV314Regression(){}
    static void check(boolean ok,String why){
        if(!ok)throw new AssertionError("ZERO3 V314 "+why);
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
        double pulse=0,pulseCos=0,pulseSin=0;
        for(int i=0;i<Morphology.POINTS;i++){
            double p=Math.max(0.,Math.min(1.,sk.touchPulse[i]));
            double a=2*Math.PI*i/Morphology.POINTS;
            pulse+=p;pulseCos+=p*Math.cos(a);pulseSin+=p*Math.sin(a);
        }
        v[22]=pulse/Morphology.POINTS;
        v[23]=pulseCos/Morphology.POINTS;
        v[24]=pulseSin/Morphology.POINTS;
        for(int i=0;i<STATE;i++)v[i]=clamp(v[i],-5.,5.);
        return o;
    }
    /**
     * Read the real membrane-ray geometry BEFORE action. This is the same
     * physical eye + ray/occlusion solver used by SpatialPerception;
     * no object coordinates, ID, law or class become learner inputs.
     * Alignment uses only the body axis actually observed at this moment.
     */
    static Observation observeWithPhysicalSight(
            Zero3MotorImpulseResponseRegression.Scenario sc,
            ArrayList<UnknownWorld.Entity> world) {
        Observation frame=observe(sc);
        for(int ray=0;ray<16;ray++){
            double a=frame.theta+Math.PI*2*ray/16;
            float nx=(float)Math.cos(a),ny=(float)Math.sin(a);
            float[] eye=MaterialGeometry.eye(sc.skin,sc.body.cx,sc.body.cy,nx,ny);
            float distance=SensorimotorMind.Senses.firstSolidDistance(
                null,world,eye[0],eye[1],nx,ny,SpatialPerception.RANGE);
            frame.v[25+ray]=Float.isFinite(distance)
                ?clamp(1.0-distance/SpatialPerception.RANGE,0,1):0.;
        }
        return frame;
    }
    static final class Trace {
        final Observation[] observed=new Observation[FRAMES+1];
        final float[][] executed=new float[FRAMES][64];
        final double[][] xy=new double[FRAMES][2];
        int contacted,gripping,pulsed,scene,visible,contactOnsets;
    }
    /**
     * One uninterrupted real organism. Sensors are only sampled before
     * muscle command and AFTER the object's actual physics solver.
     * No future state, object ID, physical law or gem signal enters model.
     */
    static Trace physicallyRecorded(int split,int dir,float pose,int condition,
                                    int seedIndex,int scene,int adhesion) {
        Zero3MotorImpulseResponseRegression.Scenario sc=
            new Zero3MotorImpulseResponseRegression.Scenario(dir,pose,condition);
        Trace t=new Trace();
        t.scene=scene;
        int seed=Zero3MotorLocalFrameV310Regression.scheduleSeed(
            0x313+split*1729+scene*61,dir,pose,condition,seedIndex,1,
            adhesion==0?-1:1);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        UnknownWorld.Entity object=null;
        float[] prevSX=new float[Morphology.POINTS],prevSY=new float[Morphology.POINTS],
                prevBX=new float[MotorBrain.N],prevBY=new float[MotorBrain.N];
        double lastX=0,lastY=0;
        for(int frame=0;frame<HISTORY+FRAMES;frame++){
            int step=frame-HISTORY;
            if(step==0){
                lastX=sc.body.cx;lastY=sc.body.cy;
                if(scene!=FREE){
                    boolean box=scene==BOX_NEAR||scene==BOX_FAR;
                    float d=box?BOX_GAP[split][scene==BOX_NEAR?0:1]:
                                DISK_GAP[split][scene==DISK_NEAR?0:1];
                    // The stimulus is environmental geometry: it is NOT a
                    // direction or reward passed to ZERO's cortex.
                    float bearing=(float)((seedIndex==0?-1:1)*
                        (scene==BOX_FAR||scene==DISK_FAR?.45:.25));
                    float ox=(float)Math.cos(bearing)*d;
                    float oy=(float)Math.sin(bearing)*d;
                    object=new UnknownWorld.Entity(sc.body.cx+ox,
                        sc.body.cy+oy,2,box?5:2);
                    objects.add(object);
                }
                t.observed[0]=observeWithPhysicalSight(sc,objects);
            }
            float[] cmd=sc.cortex.command(sc.intent,sc.body,sc.skin,DT);
            Zero3MotorLocalFrameV310Regression.excitation(
                frame,seed,0,1,adhesion==0?-1:1,cmd);
            for(int sector=0;sector<16;sector++)
                cmd[4*sector+2]=adhesion==0?-1f:
                    (float)Math.max(.72,cmd[4*sector+2]);
            for(float value:cmd)check(Float.isFinite(value)&&
                value>=-1.00001f&&value<=1.00001f,"executed physical muscle out of bounds");
            System.arraycopy(sc.skin.x,0,prevSX,0,Morphology.POINTS);
            System.arraycopy(sc.skin.y,0,prevSY,0,Morphology.POINTS);
            System.arraycopy(sc.body.x,0,prevBX,0,MotorBrain.N);
            System.arraycopy(sc.body.y,0,prevBY,0,MotorBrain.N);
            float coreX=sc.body.cx,coreY=sc.body.cy;
            if(step>=0)System.arraycopy(cmd,0,t.executed[step],0,64);
            sc.gel.step(sc.body,sc.skin,cmd,DT);
            check(sc.gel.invalid==0&&Float.isFinite(sc.body.cx)&&
                  Float.isFinite(sc.body.cy),"invalid physical gel");
            if(step<0)continue;
            if(object!=null&&object.isBilateralBox()){
                SquareGripPuzzle.constrain(sc.body,sc.skin,object,DT,
                    prevSX,prevSY,prevBX,prevBY,coreX,coreY);
                MaterialContactConstraints.reconcile(sc.body,sc.skin,objects,DT);
                SymbioteWorldStepV171.interior(sc.gel,sc.body,sc.skin,null,objects,DT);
                GripSurfaceComposition.compose(sc.skin,null,objects);
                object.tick(DT);
            }else{
                SymbioteWorldStepV171.interior(sc.gel,sc.body,sc.skin,null,objects,DT);
                GripSurfaceComposition.compose(sc.skin,null,objects);
                if(object!=null){
                    Morphology.collide(sc.skin,object,DT);
                    ContactLearning.collide(sc.body,object,DT);
                    object.tick(DT);
                    sc.gel.observeBody(sc.body,sc.skin);
                }
            }
            check(sc.gel.invalid==0&&Float.isFinite(sc.body.cx)&&
                 Float.isFinite(sc.body.cy),"invalid post-collision world state");
            t.observed[step+1]=observeWithPhysicalSight(sc,objects);
            t.xy[step][0]=sc.body.cx-lastX;
            t.xy[step][1]=sc.body.cy-lastY;
            lastX=sc.body.cx;lastY=sc.body.cy;
            if(t.observed[step+1].v[7]>.001)t.contacted++;
            if(t.observed[step+1].v[6]>.001)t.gripping++;
            if(t.observed[step+1].v[22]>.001)t.pulsed++;
            boolean beforeContact=hasContact(t.observed[step].v),
                    afterContact=hasContact(t.observed[step+1].v);
            if(!beforeContact&&afterContact)t.contactOnsets++;
            if(hasVision(t.observed[step].v))t.visible++;
        }
        for(int k=0;k<FRAMES;k++)
            check(t.observed[k]!=null&&t.observed[k+1]!=null&&
                  Double.isFinite(t.xy[k][0])&&Double.isFinite(t.xy[k][1]),
                  "missing causal real world experience");
        return t;
    }
    static ArrayList<Trace> generate(int split,float[] poses,int[] conditions){
        ArrayList<Trace> traces=new ArrayList<>();
        for(int dir=0;dir<poses.length;dir++)for(float pose:new float[]{poses[dir]})
            for(int condition:conditions)for(int seed=0;seed<2;seed++)
                for(int adhesion=0;adhesion<2;adhesion++)
                    for(int scene=FREE;scene<=DISK_FAR;scene++)
                        traces.add(physicallyRecorded(split,dir,pose,condition,
                            seed,scene,adhesion));
        return traces;
    }
    static boolean hasContact(double[] s){
        return s[6]>.001||s[7]>.001||s[22]>.001;
    }
    static boolean hasVision(double[] s){
        for(int i=25;i<STATE;i++)if(s[i]>.0001)return true;
        return false;
    }
    /**
     * Body-frame 64-command reduction is computed from ONLY known action
     * values and the body state currently supplied to the model.
     * NO future grip, future contact, or future world coordinates are read.
     */
    static double[] features(double[] state,float[] commands,
                             boolean useAction,boolean gripAware,
                             boolean useVision){
        double[] x=new double[INPUT];x[0]=1.;
        for(int j=0;j<STATE;j++){
            // Ablate contact/hold signals from the state itself as well.
            boolean blocked=!gripAware &&
                (j==6||j==7||j==15||j==16||j==17||j==18||j==22||j==23||j==24);
            x[1+j]=(blocked||(!useVision&&j>=25))?0:state[j];
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
        y[STATE+3]=(!hasContact(before.v)&&hasContact(after.v))?1.:0.;
        return y;
    }
    /**
     * Multivariate ridge regression from immutable FIT-only frames. Fixed
     * regularizer, no calibration/blind fitting. Features normalized on FIT.
     */
    static final class Model {
        final boolean action,gripAware,vision;
        final double[] avg=new double[INPUT],scale=new double[INPUT];
        final double[][] w=new double[OUTPUT][INPUT];
        Model(ArrayList<Trace> training,boolean useAction,boolean contacts,boolean useVision){
            action=useAction;gripAware=contacts;vision=useVision;
            int samples=0;
            for(Trace t:training)for(int k=0;k<FRAMES;k+=2){
                double[] f=features(t.observed[k].v,t.executed[k],action,gripAware,vision);
                for(int j=1;j<INPUT;j++)avg[j]+=f[j];
                samples++;
            }
            check(samples>1000,"few physical training frames");
            for(int j=1;j<INPUT;j++)avg[j]/=samples;
            for(Trace t:training)for(int k=0;k<FRAMES;k+=2){
                double[] f=features(t.observed[k].v,t.executed[k],action,gripAware,vision);
                for(int j=1;j<INPUT;j++){
                    double delta=f[j]-avg[j];scale[j]+=delta*delta;
                }
            }
            scale[0]=1.;
            for(int j=1;j<INPUT;j++)
                scale[j]=Math.max(.02,Math.sqrt(scale[j]/samples));
            double[][] gram=new double[INPUT][INPUT],rhs=new double[INPUT][OUTPUT];
            for(Trace t:training)for(int k=0;k<FRAMES;k+=2){
                double[] x=features(t.observed[k].v,t.executed[k],action,gripAware,vision);
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
            double[] f=features(s,cmd,action,gripAware,vision);
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
            prediction[STATE+3]=clamp(prediction[STATE+3],0.,1.);
            return prediction;
        }
    }
    static final class Metrics {
        final int n;
        int onsets,contactAlready,contactTraces,holdTraces,visibleSamples,blindOnsets;
        double xy,ballistic,zero,state,persist;
        double onsetXY,onsetBall,alreadyXY,alreadyBall;
        double brier,brierOnsets;
        final double[] path=new double[3],still=new double[3],ball=new double[3];
        Metrics(ArrayList<Trace> traces,Model model,double prior) {
            n=traces.size();
            final int[] horizons={16,48,144};
            for(Trace t:traces){
                if(t.contacted>0||t.pulsed>0)contactTraces++;
                if(t.gripping>0)holdTraces++;
                double[] predicted=t.observed[0].v.clone();
                double angle=t.observed[0].theta;
                double[] initialVelocity=world(predicted[0]*10.,
                                                 predicted[1]*10.,angle);
                double px=0,py=0,rx=0,ry=0;
                for(int k=0;k<FRAMES;k++){
                    Observation now=t.observed[k],after=t.observed[k+1];
                    if(hasVision(now.v))visibleSamples++;
                    double[] y=model.estimate(now.v,t.executed[k]);
                    double[] pred=world(now.v[0]*10.*DT+y[STATE],
                                        now.v[1]*10.*DT+y[STATE+1],now.theta);
                    double[] linear=world(now.v[0]*10.*DT,
                                           now.v[1]*10.*DT,now.theta);
                    double dx=t.xy[k][0],dy=t.xy[k][1];
                    double e=sq(pred[0]-dx)+sq(pred[1]-dy);
                    double base=sq(linear[0]-dx)+sq(linear[1]-dy);
                    xy+=e;ballistic+=base;zero+=sq(dx)+sq(dy);
                    boolean was=hasContact(now.v),will=hasContact(after.v);
                    if(was){
                        contactAlready++;
                        alreadyXY+=e;alreadyBall+=base;
                    }else if(will){
                        onsets++;
                        onsetXY+=e;onsetBall+=base;
                    }
                    if(!was){
                        double prob=y[STATE+3];
                        brier+=sq(prob-(will?1.:0.));
                        if(will)brierOnsets+=sq(1.-prob);
                    }
                    for(int j=0;j<25;j++){
                        state+=sq(now.v[j]+y[j]-after.v[j]);
                        persist+=sq(now.v[j]-after.v[j]);
                    }
                    // Keep future WORLD data out of the rollout. The future
                    // 64D action TAPE is known from recorded physics only.
                    double[] estimate=model.estimate(predicted,t.executed[k]);
                    double[] worldDelta=world(predicted[0]*10.*DT+estimate[STATE],
                                               predicted[1]*10.*DT+estimate[STATE+1],angle);
                    px+=worldDelta[0];py+=worldDelta[1];
                    for(int j=0;j<STATE;j++)
                        predicted[j]=clamp(predicted[j]+estimate[j],-5.,5.);
                    // Observed ray closeness only valid in [0,1], never
                    // substitute real future rays from simulator.
                    for(int j=25;j<STATE;j++)
                        predicted[j]=clamp(predicted[j],0.,1.);
                    angle=wrap(angle+estimate[STATE+2]);
                    rx+=dx;ry+=dy;
                    for(int idx=0;idx<3;idx++)if(k<horizons[idx]){
                        path[idx]+=sq(px-rx)+sq(py-ry);
                        still[idx]+=sq(rx)+sq(ry);
                        double time=(k+1)*DT;
                        ball[idx]+=sq(initialVelocity[0]*time-rx)+
                            sq(initialVelocity[1]*time-ry);
                    }
                }
            }
            double d=n*FRAMES*2.;
            xy/=d;ballistic/=d;zero/=d;
            state/=n*FRAMES*25.;persist/=n*FRAMES*25.;
            if(onsets>0){onsetXY/=onsets*2.;onsetBall/=onsets*2.;brierOnsets/=onsets;}
            if(contactAlready>0){alreadyXY/=contactAlready*2.;alreadyBall/=contactAlready*2.;}
            // The Brier score only measures pre-step NONCONTACT frames.
            int opportunities=n*FRAMES-contactAlready;
            if(opportunities>0)brier/=opportunities;
            for(int idx=0;idx<3;idx++){
                double count=n*horizons[idx]*2.;
                path[idx]/=count;still[idx]/=count;ball[idx]/=count;
            }
        }
        static double sq(double x){return x*x;}
        boolean passes(Metrics noVision,Metrics noAction,Metrics noTouch,
                       double priorBrier){
            return contactTraces>=10&&onsets>=8&&visibleSamples>0 &&
                xy<.9*ballistic && xy<.97*noVision.xy && xy<.97*noAction.xy &&
                onsetXY<.9*onsetBall&&onsetXY<.97*noVision.onsetXY &&
                alreadyXY<.9*alreadyBall&&alreadyXY<.97*noTouch.alreadyXY &&
                path[0]<.9*Math.min(still[0],ball[0]) &&
                path[1]<.9*Math.min(still[1],ball[1]) &&
                brier<.95*noVision.brier && brier<.95*priorBrier &&
                brierOnsets<=noVision.brierOnsets;
        }
        String details(){
            return String.format(Locale.US,
                "n=%d contactTraces=%d holdTraces=%d onsetEvents=%d "+
                "alreadyContactFrames=%d visiblePreActionFrames=%d "+
                "xy=%.9f ballistic=%.9f state=%.9f persist=%.9f "+
                "onsetXY=%.9f onsetBall=%.9f alreadyXY=%.9f alreadyBall=%.9f "+
                "onsetBrier=%.9f brierOnPositives=%.9f "+
                "path16=%.9f still16=%.9f ball16=%.9f "+
                "path48=%.9f still48=%.9f ball48=%.9f "+
                "path144=%.9f still144=%.9f ball144=%.9f",
                n,contactTraces,holdTraces,onsets,contactAlready,visibleSamples,
                xy,ballistic,state,persist,onsetXY,onsetBall,
                alreadyXY,alreadyBall,brier,brierOnsets,
                path[0],still[0],ball[0],
                path[1],still[1],ball[1],
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
    static double fitOnsetPrevalence(ArrayList<Trace> fit){
        int e=0,n=0;
        for(Trace t:fit)for(int k=0;k<FRAMES;k++){
            if(!hasContact(t.observed[k].v)){
                n++;
                if(hasContact(t.observed[k+1].v))e++;
            }
        }
        check(n>100,"no fit non-contact opportunities");
        return (double)e/n;
    }
    static double baselineBrier(ArrayList<Trace> ts,double rate){
        double total=0;
        int n=0;
        for(Trace t:ts)for(int k=0;k<FRAMES;k++){
            boolean pre=hasContact(t.observed[k].v);
            if(pre)continue;
            boolean event=hasContact(t.observed[k+1].v);
            total+=(rate-(event?1.:0.))*(rate-(event?1.:0.));
            n++;
        }
        check(n>0,"no holdout noncontact samples");
        return total/n;
    }
    static void visionSanity(){
        Zero3MotorImpulseResponseRegression.Scenario sc=
            new Zero3MotorImpulseResponseRegression.Scenario(0,0f,0);
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        Observation none=observeWithPhysicalSight(sc,empty);
        check(!hasVision(none.v),"invented obstacle with empty world");
        UnknownWorld.Entity near=new UnknownWorld.Entity(sc.body.cx+72f,sc.body.cy,2,5);
        empty.add(near);
        Observation visible=observeWithPhysicalSight(sc,empty);
        check(hasVision(visible.v),"box did not appear in real material vision");
        double largest=0;
        for(int k=25;k<STATE;k++)largest=Math.max(largest,visible.v[k]);
        check(largest>0.1,"material optical scan has no valid depth");
        // Optical occlusion/continuity are delegated to production's
        // firstSolidDistance and MaterialGeometry.eye implementation.
    }
    public static void main(String[] args){
        rotationProperty();visionSanity();
        ArrayList<Trace> fit=generate(0,FIT,new int[]{0,1,2});
        check(fit.size()==120,"incorrect FIT trajectory count");
        double fitPrior=fitOnsetPrevalence(fit);
        Model full=new Model(fit,true,true,true);
        Model noVision=new Model(fit,true,true,false);
        Model noAction=new Model(fit,false,true,true);
        Model noTouch=new Model(fit,true,false,true);
        fit.clear();
        ArrayList<Trace> cal=generate(1,CAL,new int[]{0,1,2});
        check(cal.size()==120,"incorrect CAL trajectory count");
        Metrics a=new Metrics(cal,full,fitPrior),b=new Metrics(cal,noVision,fitPrior),
                d=new Metrics(cal,noAction,fitPrior),e=new Metrics(cal,noTouch,fitPrior);
        double cPrior=baselineBrier(cal,fitPrior);
        boolean calibrated=a.passes(b,d,e,cPrior);
        System.out.println("ZERO3 V314 FIT ONSET_PREVALENCE="+fitPrior);
        System.out.println("ZERO3 V314 CAL FULL "+a.details());
        System.out.println("ZERO3 V314 CAL NO_VISION "+b.details());
        System.out.println("ZERO3 V314 CAL NO_ACTION "+d.details());
        System.out.println("ZERO3 V314 CAL NO_TOUCH "+e.details());
        System.out.println("ZERO3 V314 CAL PRIOR_BRIER="+cPrior);
        System.out.println("ZERO3 V314 CAL GATE="+(calibrated?"PASS":"FAIL"));
        cal.clear();
        ArrayList<Trace> blind=generate(2,BLIND,new int[]{3});
        check(blind.size()==80,"incorrect independent BLIND trajectory count");
        Metrics u=new Metrics(blind,full,fitPrior),v=new Metrics(blind,noVision,fitPrior),
                w=new Metrics(blind,noAction,fitPrior),z=new Metrics(blind,noTouch,fitPrior);
        double bPrior=baselineBrier(blind,fitPrior);
        boolean validated=u.passes(v,w,z,bPrior);
        System.out.println("ZERO3 V314 BLIND FULL "+u.details());
        System.out.println("ZERO3 V314 BLIND NO_VISION "+v.details());
        System.out.println("ZERO3 V314 BLIND NO_ACTION "+w.details());
        System.out.println("ZERO3 V314 BLIND NO_TOUCH "+z.details());
        System.out.println("ZERO3 V314 BLIND PRIOR_BRIER="+bPrior);
        System.out.println("ZERO3 V314 BLIND GATE="+(validated?"PASS":"FAIL"));
        System.out.printf(Locale.US,
            "ZERO3 V314 SUMMARY fit=120 calibration=120 blind=80 "+
            "realSolverSteps=%d recordedTransitions=%d "+
            "state=%d bodyState=25 actualMuscles=64 "+
            "physicallyMeasuredMembraneRays=16 opticalSanity=true "+
            "futureVisionLeak=false futureStateLeak=false futureActionTapeKnown=true "+
            "autonomousExperiences=false calibration=%s blind=%s "+
            "NO_GAMEPLAY G3_BLOCKED G5_BLOCKED NO_OTA%n",
            320*(HISTORY+FRAMES),320*FRAMES,STATE,
            calibrated?"PASS":"FAIL",validated?"PASS":"FAIL");
    }
}
