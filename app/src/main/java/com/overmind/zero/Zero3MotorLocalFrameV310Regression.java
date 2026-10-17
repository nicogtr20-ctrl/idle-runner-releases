package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 V3.10: 0.025-second actual state/action -> next physical state.
 * Offline identification of one real gel, never a live controller.
 *
 * 8 global measurements + (16 sectors x 4 local morphology/velocity values).
 * 64 muscle outputs consumed by SymbioteMatterV1 at the SAME frame.
 * Full, no-action, and global-only baselines are fitted from FIT alone.
 * Rollouts may see logged FUTURE ACTIONS, never FUTURE PHYSICAL STATES.
 */
public final class Zero3MotorLocalFrameV310Regression {
    static final int S=72,A=64,Y=S+2,FRAMES=384,HISTORY=96;
    static final int[][] ZONES={{0,8}};
    static final float DT=.025f;
    static final float[] FIT={0f,(float)(Math.PI/2)};
    static final float[] CAL={(float)(Math.PI/8),(float)(-Math.PI/8)};
    static final float[] BLIND={(float)(13*Math.PI/180),
        (float)(-29*Math.PI/180),(float)(53*Math.PI/180),(float)(-77*Math.PI/180)};
    private Zero3MotorLocalFrameV310Regression(){}
    static void require(boolean b,String reason){
        if(!b)throw new AssertionError("ZERO3 V310 "+reason);
    }
    static double clip(double d,double min,double max){
        require(Double.isFinite(d),"nonfinite state/estimate");
        return Math.max(min,Math.min(max,d));
    }
    static double[] sense(Zero3MotorImpulseResponseRegression.Scenario sc){
        MotorBrain.Body b=sc.body;
        Morphology.Surface skin=sc.skin;
        SymbioteMatterV1 gel=sc.gel;
        double[] o=new double[S];
        o[0]=b.cvx/10.;o[1]=b.cvy/10.;
        o[2]=gel.areaError/500.;
        double grip=0,touch=0,radius=0,fluidX=0,fluidY=0;
        final int per=Morphology.POINTS/16;
        for(int sector=0;sector<16;sector++){
            double rx=0,ry=0,vx=0,vy=0;
            for(int j=0;j<per;j++){
                int i=sector*per+j;
                double x=skin.x[i]-b.cx,y=skin.y[i]-b.cy;
                rx+=x;ry+=y;
                vx+=skin.vx[i]-b.cvx;vy+=skin.vy[i]-b.cvy;
                radius+=Math.hypot(x,y);
                grip+=skin.gripHold[i]>Morphology.GRIP_HOLD?1:0;
                touch+=skin.touchSense[i];
            }
            int at=8+4*sector;
            o[at]=rx/per/28.;
            o[at+1]=ry/per/28.;
            o[at+2]=vx/per/10.;
            o[at+3]=vy/per/10.;
        }
        for(int j=0;j<SymbioteMatterV1.INNER;j++){
            fluidX+=gel.vx[j]-b.cvx;
            fluidY+=gel.vy[j]-b.cvy;
        }
        o[3]=grip/Morphology.POINTS;
        o[4]=touch/Morphology.POINTS;
        o[5]=radius/Morphology.POINTS/28.-1.;
        o[6]=fluidX/SymbioteMatterV1.INNER/10.;
        o[7]=fluidY/SymbioteMatterV1.INNER/10.;
        for(int i=0;i<S;i++)o[i]=clip(o[i],-5.,5.);
        return o;
    }
    static final class Trace {
        final double[][] state=new double[FRAMES+1][];
        final float[][] command=new float[FRAMES][A];
        final double[][] displacement=new double[FRAMES][2];
    }
    /**
     * Split/group-dependent deterministic muscle perturbations supply
     * independent temporal excitation seeds. The seed is never a predictor
     * input, and neither are laboratory target direction or pose labels.
     */
    static int scheduleSeed(int group,int direction,float pose,int condition,
                            int zone,int channel,int sign){
        int h=0x74AF291D ^(group*0x1f123bb5);
        h=Integer.rotateLeft(h^Float.floatToIntBits(pose),11);
        h=Integer.rotateLeft(h^direction*991^condition*7907,13);
        h=Integer.rotateLeft(h^(zone*31+channel*197+sign*17),9);
        return h;
    }
    static int mix(int h){
        h^=h>>>16;h*=0x7feb352d;
        h^=h>>>15;h*=0x846ca68b;h^=h>>>16;
        return h;
    }
    static void excitation(int step,int seed,int zone,int channel,
                           int sign,float[] action){
        // Real laboratory perturbations vary by deterministic frame epochs.
        // A pulse lasts 6-17 frames, sometimes with distinct overlapping
        // neighbor impulses. Nothing prescribes a gait to Zero's brain.
        int epoch=step/11;
        for(int n=0;n<2;n++){
            int bits=mix(seed^((epoch+n*17)*0x9e3779b9));
            int site=(zone+((bits>>>8)&15))%16;
            int ch=(channel+n+((bits>>>12)&3))&3;
            float polarity=((bits&1)==0?sign:-sign);
            float amount=(.10f+.09f*((bits>>>2)&7)/7f)*polarity;
            if(n==1&&((bits>>>20)&3)==0)continue;
            for(int offset=-1;offset<=1;offset++){
                int at=((site+offset+16)%16)*4+ch;
                float add=amount*(2-Math.abs(offset))/2f;
                action[at]=(float)clip(action[at]+add,-1.,1.);
            }
        }
    }
    static Trace physical(int group,int dir,float pose,int condition,
                          int zone,int channel,int sign){
        Zero3MotorImpulseResponseRegression.Scenario sc=
            new Zero3MotorImpulseResponseRegression.Scenario(dir,pose,condition);
        Trace t=new Trace();
        int seed=scheduleSeed(group,dir,pose,condition,zone,channel,sign);
        double x=sc.body.cx,y=sc.body.cy;
        for(int frame=0;frame<HISTORY+FRAMES;frame++){
            int elapsed=frame-HISTORY;
            if(elapsed==0){
                t.state[0]=sense(sc);
                x=sc.body.cx;y=sc.body.cy;
            }
            float[] commands=sc.cortex.command(sc.intent,sc.body,sc.skin,DT);
            excitation(frame,seed,zone,channel,sign,commands);
            if(elapsed>=0){
                System.arraycopy(commands,0,t.command[elapsed],0,A);
                for(float v:t.command[elapsed])
                    require(Float.isFinite(v)&&Math.abs(v)<=1.00001f,
                        "executed actuator outside solver limits");
            }
            sc.gel.step(sc.body,sc.skin,commands,DT);
            require(sc.gel.invalid==0&&Float.isFinite(sc.body.cx)&&
                Float.isFinite(sc.body.cy),"nonfinite real gel solver");
            if(elapsed>=0){
                t.state[elapsed+1]=sense(sc);
                t.displacement[elapsed][0]=sc.body.cx-x;
                t.displacement[elapsed][1]=sc.body.cy-y;
                x=sc.body.cx;y=sc.body.cy;
            }
        }
        for(int i=0;i<FRAMES;i++){
            require(t.state[i]!=null&&t.state[i+1]!=null,"missing state step");
            require(Double.isFinite(t.displacement[i][0])&&
                    Double.isFinite(t.displacement[i][1]),"missing real motion");
        }
        return t;
    }
    static ArrayList<Trace> make(int group,float[] poses,int[] conditions){
        ArrayList<Trace> list=new ArrayList<>();
        for(int d=0;d<4;d++)for(float pose:poses)
            for(int c:conditions)for(int ch=0;ch<4;ch++)
                for(int zone:ZONES[0])for(int sign:new int[]{-1,1})
                    list.add(physical(group,d,pose,c,zone,ch,sign));
        return list;
    }
    static double[] truth(Trace t,int k){
        double[] y=new double[Y];
        for(int j=0;j<S;j++)y[j]=t.state[k+1][j]-t.state[k][j];
        y[S]=t.displacement[k][0];
        y[S+1]=t.displacement[k][1];
        return y;
    }
    /**
     * Feature mask keeps baselines truly blind to the ablated information.
     * Full: state 72 + action 64; no-action: state 72;
     * global: state 8 + action 64. A bias is always included.
     */
    static final class Model {
        final boolean actions,local;
        final int n;
        final double[] mean,sd;
        final double[][] weights;
        Model(ArrayList<Trace> train,boolean actions,boolean local){
            this.actions=actions;this.local=local;
            n=1+(local?S:8)+(actions?A:0);
            mean=new double[n];sd=new double[n];
            weights=new double[Y][n];
            int samples=0;
            for(Trace t:train)for(int k=0;k<FRAMES;k+=4){
                double[] x=features(t.state[k],t.command[k]);
                for(int j=1;j<n;j++)mean[j]+=x[j];
                samples++;
            }
            require(samples>100,"not enough training transitions");
            for(int j=1;j<n;j++)mean[j]/=samples;
            for(Trace t:train)for(int k=0;k<FRAMES;k+=4){
                double[] x=features(t.state[k],t.command[k]);
                for(int j=1;j<n;j++){
                    double delta=x[j]-mean[j];
                    sd[j]+=delta*delta;
                }
            }
            sd[0]=1.;
            for(int j=1;j<n;j++)sd[j]=Math.max(.02,Math.sqrt(sd[j]/samples));
            double[][] gram=new double[n][n],rhs=new double[n][Y];
            for(Trace t:train)for(int k=0;k<FRAMES;k+=4){
                double[] x=features(t.state[k],t.command[k]);
                double[] y=truth(t,k);
                for(int j=1;j<n;j++)x[j]=(x[j]-mean[j])/sd[j];
                for(int j=0;j<n;j++){
                    double v=x[j];
                    for(int l=0;l<=j;l++)gram[j][l]+=v*x[l];
                    for(int out=0;out<Y;out++)rhs[j][out]+=v*y[out];
                }
            }
            for(int j=0;j<n;j++){
                for(int l=0;l<j;l++)gram[l][j]=gram[j][l];
                gram[j][j]+=12.; // frozen regularization, training only
            }
            choleskySolve(gram,rhs,weights);
        }
        double[] features(double[] s,float[] action){
            double[] x=new double[n];x[0]=1;
            int count=local?S:8;
            System.arraycopy(s,0,x,1,count);
            if(actions)for(int i=0;i<A;i++)x[1+count+i]=action[i];
            return x;
        }
        double[] estimate(double[] s,float[] command){
            double[] x=features(s,command),y=new double[Y];
            for(int j=1;j<n;j++)x[j]=(x[j]-mean[j])/sd[j];
            for(int output=0;output<Y;output++){
                double sum=0;
                for(int j=0;j<n;j++)sum+=x[j]*weights[output][j];
                y[output]=clip(sum,-5.,5.);
            }
            return y;
        }
    }
    /** One shared Cholesky factor and simultaneous multioutput solve. */
    static void choleskySolve(double[][] gram,double[][] rhs,double[][] weights){
        int n=gram.length,out=rhs[0].length;
        double[][] lower=new double[n][n];
        for(int i=0;i<n;i++)for(int j=0;j<=i;j++){
            double sum=gram[i][j];
            for(int k=0;k<j;k++)sum-=lower[i][k]*lower[j][k];
            if(i==j){
                require(sum>1e-12&&Double.isFinite(sum),"ridge matrix not SPD");
                lower[i][j]=Math.sqrt(sum);
            }else lower[i][j]=sum/lower[j][j];
        }
        double[][] z=new double[n][out],solved=new double[n][out];
        for(int i=0;i<n;i++)for(int h=0;h<out;h++){
            double q=rhs[i][h];
            for(int j=0;j<i;j++)q-=lower[i][j]*z[j][h];
            z[i][h]=q/lower[i][i];
        }
        for(int i=n-1;i>=0;i--)for(int h=0;h<out;h++){
            double q=z[i][h];
            for(int j=i+1;j<n;j++)q-=lower[j][i]*solved[j][h];
            solved[i][h]=q/lower[i][i];
            require(Double.isFinite(solved[i][h]),"nonfinite ridge coefficient");
            weights[h][i]=solved[i][h];
        }
    }
    static final class Scores {
        final int trajectories;
        double xy,ballistic,zero,body,persist;
        final double[] path=new double[3],pathStill=new double[3],pathBallistic=new double[3];
        Scores(ArrayList<Trace> tests,Model model){
            trajectories=tests.size();
            final int[] horizons={16,96,384};
            for(Trace trace:tests){
                double[] state=trace.state[0].clone();
                double startVX=state[0]*10.,startVY=state[1]*10.;
                double px=0,py=0,rx=0,ry=0;
                for(int k=0;k<FRAMES;k++){
                    float[] command=trace.command[k];
                    double[] p=model.estimate(trace.state[k],command);
                    double x=trace.displacement[k][0],y=trace.displacement[k][1];
                    xy+=sq(p[S]-x)+sq(p[S+1]-y);
                    zero+=sq(x)+sq(y);
                    ballistic+=sq(trace.state[k][0]*10.*DT-x)+
                               sq(trace.state[k][1]*10.*DT-y);
                    for(int h=0;h<S;h++){
                        body+=sq(trace.state[k][h]+p[h]-trace.state[k+1][h]);
                        persist+=sq(trace.state[k+1][h]-trace.state[k][h]);
                    }
                    // The ONLY state fed back is our OWN estimated state.
                    double[] step=model.estimate(state,command);
                    px+=step[S];py+=step[S+1];
                    for(int h=0;h<S;h++)
                        state[h]=clip(state[h]+step[h],-5.,5.);
                    rx+=x;ry+=y;
                    double time=(k+1)*DT;
                    double q=sq(px-rx)+sq(py-ry);
                    double q0=sq(rx)+sq(ry);
                    double qb=sq(startVX*time-rx)+sq(startVY*time-ry);
                    for(int idx=0;idx<horizons.length;idx++)
                        if(k<horizons[idx]){
                            path[idx]+=q;
                            pathStill[idx]+=q0;
                            pathBallistic[idx]+=qb;
                        }
                }
            }
            double count=trajectories*FRAMES*2.;
            xy/=count;ballistic/=count;zero/=count;
            body/=trajectories*FRAMES*S;persist/=trajectories*FRAMES*S;
            for(int i=0;i<3;i++){
                double n=trajectories*horizons[i]*2.;
                path[i]/=n;pathStill[i]/=n;pathBallistic[i]/=n;
            }
        }
        static double sq(double x){return x*x;}
        boolean passes(Scores noAction,Scores globalOnly){
            return xy<.9*ballistic && body<.9*persist &&
                xy<.97*noAction.xy && xy<.97*globalOnly.xy &&
                path[0]<.9*Math.min(pathStill[0],pathBallistic[0]) &&
                path[1]<.9*Math.min(pathStill[1],pathBallistic[1]);
        }
        String report(){
            return String.format(Locale.US,
                "trajectories=%d xy1=%.9f ballistic1=%.9f zero1=%.9f "+
                "body1=%.9f persist1=%.9f "+
                "path16=%.9f still16=%.9f ball16=%.9f "+
                "path96=%.9f still96=%.9f ball96=%.9f "+
                "path384=%.9f still384=%.9f ball384=%.9f",
                trajectories,xy,ballistic,zero,body,persist,
                path[0],pathStill[0],pathBallistic[0],
                path[1],pathStill[1],pathBallistic[1],
                path[2],pathStill[2],pathBallistic[2]);
        }
    }
    public static void main(String[] args){
        ArrayList<Trace> fit=make(101,FIT,new int[]{0,1,2});
        require(fit.size()==384,"incorrect fit partition");
        Model full=new Model(fit,true,true);
        Model noAction=new Model(fit,false,true);
        Model noLocal=new Model(fit,true,false);
        fit.clear();
        ArrayList<Trace> cal=make(202,CAL,new int[]{0,1,2});
        require(cal.size()==384,"incorrect calibration partition");
        Scores a=new Scores(cal,full),b=new Scores(cal,noAction),
               c=new Scores(cal,noLocal);
        boolean eligible=a.passes(b,c);
        System.out.println("ZERO3 V310 CAL FULL "+a.report());
        System.out.println("ZERO3 V310 CAL NO_ACTION "+b.report());
        System.out.println("ZERO3 V310 CAL NO_LOCAL "+c.report());
        System.out.println("ZERO3 V310 CAL GATE="+(eligible?"PASS":"FAIL"));
        cal.clear();
        // No training, hyperparameter or normalization changes after CAL.
        ArrayList<Trace> blind=make(303,BLIND,new int[]{3});
        require(blind.size()==256,"incorrect new blind partition");
        Scores u=new Scores(blind,full),v=new Scores(blind,noAction),
               w=new Scores(blind,noLocal);
        boolean unseen=u.passes(v,w);
        System.out.println("ZERO3 V310 BLIND FULL "+u.report());
        System.out.println("ZERO3 V310 BLIND NO_ACTION "+v.report());
        System.out.println("ZERO3 V310 BLIND NO_LOCAL "+w.report());
        System.out.printf(Locale.US,
            "ZERO3 V310 FINAL train=384 calibration=384 blind=256 "+
            "solverSteps=491520 state=%d trueAction=%d "+
            "frameDt=%.3f actionTape=true futureStatesLeak=false "+
            "newWorldScenes=false calibration=%s blind=%s "+
            "promoteToGameplay=false G3_BLOCKED G5_BLOCKED NO_OTA%n",
            S,A,DT,eligible?"PASS":"FAIL",unseen?"PASS":"FAIL");
    }
}
