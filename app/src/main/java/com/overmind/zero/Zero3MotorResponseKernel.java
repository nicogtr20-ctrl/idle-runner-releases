package com.overmind.zero;

import java.util.ArrayList;
import java.util.List;

/**
 * ZERO3 experimental learned response module; pure Java, no Android side effects.
 *
 * A trained response matrix maps current *physically observed* anatomical
 * orientation + the ONE brain-owned intent + a local candidate muscle pulse
 * into three distinct consequences: immediate displacement, delayed
 * displacement, and differential muscular work.
 *
 * IMPORTANT:
 *  - This class never chooses a food target or high-level intention.
 *  - It cannot move the body: only a supervised caller may apply a chosen
 *    small perturbation to the 64 real efference commands.
 *  - Laboratory matched-control samples must NEVER be mislabeled as normal
 *    self-generated experiences inside a live life.
 *  - Uncalibrated models have no authority to select or apply pulses.
 */
public final class Zero3MotorResponseKernel {
    public static final int FEATURES=42, TARGETS=3, SECTORS=16, CHANNELS=4;
    private Zero3MotorResponseKernel(){}
    static void finite(double x,String what){
        if(!Double.isFinite(x))throw new IllegalArgumentException("nonfinite "+what);
    }
    /**
     * Pure feature encoding, identical for learning and inference. 'pose' is
     * the anatomical axis measured from real shell patches relative to the
     * initial unrotated membrane frame, NOT a hidden lab rotation/compass.
     */
    public static double[] encode(float dx,float dy,float pose,int sector,
                                  int channel,int sign){
        finite(dx,"intent dx");finite(dy,"intent dy");finite(pose,"observed pose");
        if(sector<0||sector>=SECTORS||channel<0||channel>=CHANNELS||
           (sign!=1&&sign!=-1))throw new IllegalArgumentException("actuator descriptor");
        double mag=Math.hypot(dx,dy);
        if(Math.abs(mag-1)>1e-3)throw new IllegalArgumentException("intent not unit length");
        double[] x=new double[FEATURES];
        double sn=Math.sin(pose),cs=Math.cos(pose);
        double world=(sector+.5)*Math.PI*2/SECTORS+pose;
        double wx=Math.cos(world),wy=Math.sin(world);
        double radial=wx*dx+wy*dy,tangent=-wy*dx+wx*dy;
        x[0]=1;x[1]=dx;x[2]=dy;x[3]=sn;x[4]=cs;
        x[5+channel]=1;x[9]=wx;x[10]=wy;x[11]=sign;
        x[12]=sign*dx;x[13]=sign*dy;
        x[14]=sign*sn;x[15]=sign*cs;
        x[16]=sign*wx;x[17]=sign*wy;
        x[18+channel]=sign;
        x[22]=radial;x[23]=tangent;
        x[24]=sign*radial;x[25]=sign*tangent;
        x[26+channel]=sign*radial;
        x[30+channel]=sign*tangent;
        x[34+channel]=sign*wx;
        x[38+channel]=sign*wy;
        return x;
    }
    /** Anatomical orientation actually measured on the living shell. */
    public static float membraneAxis(MotorBrain.Body b,Morphology.Surface shell){
        if(b==null||shell==null)throw new IllegalArgumentException("missing living body");
        double x=0,y=0;
        for(int k=0;k<Morphology.POINTS/SECTORS;k++){
            x+=shell.x[k]-b.cx;y+=shell.y[k]-b.cy;
        }
        double v=Math.atan2(y,x);
        finite(v,"skin anatomical axis");
        return (float)v;
    }
    public static float relativePose(float currentAxis,float lifeReferenceAxis){
        finite(currentAxis,"anatomical axis");finite(lifeReferenceAxis,"anatomical reference");
        double a=currentAxis-lifeReferenceAxis;
        return (float)Math.atan2(Math.sin(a),Math.cos(a));
    }
    public static final class PhysicalSample {
        public final double[] features;
        public final double immediate,delayed,extraWork;
        public final long episode,decision;
        /** Records actual post-physics effects, never world-model fantasies. */
        public PhysicalSample(double[] features,double immediate,double delayed,double extraWork,
                              long episode,long decision,int observedFrames,
                              boolean externalCorrection){
            if(features==null||features.length!=FEATURES||episode<0||decision<0||
               observedFrames<12||externalCorrection)
                throw new IllegalArgumentException("ineligible physical sample");
            for(double x:features)finite(x,"feature");
            finite(immediate,"real initial response");
            finite(delayed,"real delayed response");
            finite(extraWork,"real differential muscular work");
            this.features=features.clone();
            this.immediate=immediate;this.delayed=delayed;this.extraWork=extraWork;
            this.episode=episode;this.decision=decision;
        }
        public double[] targets(){return new double[]{immediate,delayed,extraWork};}
    }
    public static final class Consequence {
        public final double immediate,delayed,extraWork;
        Consequence(double e,double l,double w){
            immediate=e;delayed=l;extraWork=w;
        }
        public double signedGain(){return immediate+delayed;}
        public double netUtility(){return signedGain()-45*Math.max(0,extraWork);}
    }
    public static final class Suggestion {
        public final int sector,channel,sign;
        public final Consequence prediction;
        Suggestion(int sector,int channel,int sign,Consequence prediction){
            this.sector=sector;this.channel=channel;this.sign=sign;
            this.prediction=prediction;
        }
    }
    /**
     * Immutable learned parameters after fit. Calibrating sets a volatile
     * readiness bit only; inference NEVER changes network parameters.
     * 'calibrated' means usable only for exploratory lab, NOT production skill.
     */
    public static final class Predictor {
        private final double[][] weights=new double[TARGETS][FEATURES];
        private final int trainingCount;
        private boolean calibrated;
        private double[] calibrationMse=new double[TARGETS],baselineMse=new double[TARGETS];
        public Predictor(List<PhysicalSample> training,double ridge){
            if(training==null||training.size()<64)
                throw new IllegalArgumentException("insufficient real training data");
            finite(ridge,"ridge penalty");
            if(ridge<.001||ridge>100)throw new IllegalArgumentException("ridge bounds");
            double[][] gram=new double[FEATURES][FEATURES];
            double[][] target=new double[TARGETS][FEATURES];
            for(PhysicalSample t:training){
                double[] x=t.features;
                double[] y={t.immediate,t.delayed,1000*t.extraWork};
                for(int j=0;j<FEATURES;j++){
                    for(int k=0;k<FEATURES;k++)gram[j][k]+=x[j]*x[k];
                    for(int o=0;o<TARGETS;o++)target[o][j]+=x[j]*y[o];
                }
            }
            for(int o=0;o<TARGETS;o++){
                double[][] regularized=new double[FEATURES][FEATURES];
                for(int i=0;i<FEATURES;i++){
                    System.arraycopy(gram[i],0,regularized[i],0,FEATURES);
                    regularized[i][i]+=ridge;
                }
                weights[o]=solve(regularized,target[o]);
            }
            trainingCount=training.size();
        }
        public int trainingCount(){return trainingCount;}
        public boolean isLabCalibrated(){return calibrated;}
        public double[] calibrationError(){return calibrationMse.clone();}
        public double[] zeroBaselineError(){return baselineMse.clone();}
        public double[] parameterSnapshot(){
            double[] w=new double[TARGETS*FEATURES];
            for(int i=0;i<TARGETS;i++)System.arraycopy(weights[i],0,w,i*FEATURES,FEATURES);
            return w;
        }
        public Consequence predictRaw(double[] x){
            if(x==null||x.length!=FEATURES)throw new IllegalArgumentException("bad feature");
            double[] p=new double[TARGETS];
            for(int i=0;i<TARGETS;i++)
                for(int j=0;j<FEATURES;j++){
                    finite(x[j],"input");
                    p[i]+=weights[i][j]*x[j];
                }
            return new Consequence(p[0],p[1],p[2]/1000);
        }
        /**
         * MUST receive a dataset separate from training. The caller owns
         * provenance; this API refuses exact episode+decision keys repeated
         * across fitting and calibration via caller check in the lab.
         */
        public boolean calibrate(List<PhysicalSample> validation){
            if(validation==null||validation.size()<64)
                throw new IllegalArgumentException("insufficient calibration");
            double[] mse=new double[TARGETS],zero=new double[TARGETS];
            for(PhysicalSample row:validation){
                double[] real=row.targets();
                Consequence c=predictRaw(row.features);
                double[] p={c.immediate,c.delayed,c.extraWork};
                for(int i=0;i<TARGETS;i++){
                    mse[i]+=(p[i]-real[i])*(p[i]-real[i]);
                    zero[i]+=real[i]*real[i];
                }
            }
            for(int i=0;i<TARGETS;i++){
                calibrationMse[i]=mse[i]/validation.size();
                baselineMse[i]=zero[i]/validation.size();
            }
            calibrated=calibrationMse[0]<baselineMse[0]*.9 &&
                       calibrationMse[1]<baselineMse[1]*.9 &&
                       calibrationMse[2]<baselineMse[2]*.9;
            return calibrated;
        }
        /**
         * Restricted experiment only: choose a single LOCAL muscle pulse
         * from predictions conditioned on the current proprioceptive state.
         * No target location/gait choice enters this method. Never runs unless
         * calibration is passed. A null suggestion means no intervention.
         */
        public Suggestion suggest(float dirX,float dirY,float measuredPose,
                                  int[] allowedSectors,double maximumExtraWork){
            if(!calibrated)return null;
            finite(maximumExtraWork,"work budget");
            if(maximumExtraWork<=0||allowedSectors==null)
                throw new IllegalArgumentException("budget/region mask");
            Suggestion best=null;
            double maxScore=0;
            for(int sec:allowedSectors)for(int ch=0;ch<CHANNELS;ch++)
                for(int sign:new int[]{-1,1}){
                    Consequence c=predictRaw(encode(dirX,dirY,measuredPose,sec,ch,sign));
                    double u=c.netUtility();
                    if(c.signedGain()>0 && c.extraWork<=maximumExtraWork && u>maxScore){
                        maxScore=u;best=new Suggestion(sec,ch,sign,c);
                    }
                }
            return best;
        }
    }
    /** Updates a provided ACTUAL efference array only when explicitly called. */
    public static void applyLocalPulse(float[] actual64,Suggestion action,float amplitude){
        if(actual64==null||actual64.length!=SECTORS*CHANNELS||action==null)
            throw new IllegalArgumentException("invalid executed muscle field");
        finite(amplitude,"pulse amplitude");
        if(amplitude<=0||amplitude>.2f)throw new IllegalArgumentException("unsafe pulse");
        for(int d=-2;d<=2;d++){
            int i=((action.sector+d+SECTORS)%SECTORS)*CHANNELS+action.channel;
            double v=actual64[i]+amplitude*action.sign*(3-Math.abs(d))/3;
            actual64[i]=(float)Math.max(-1,Math.min(1,v));
        }
    }
    static double[] solve(double[][] a,double[] rhs){
        int n=rhs.length;double[] b=rhs.clone();
        for(int col=0;col<n;col++){
            int best=col;
            for(int r=col+1;r<n;r++)
                if(Math.abs(a[r][col])>Math.abs(a[best][col]))best=r;
            double[] temp=a[best];a[best]=a[col];a[col]=temp;
            double x=b[best];b[best]=b[col];b[col]=x;
            if(Math.abs(a[col][col])<1e-10)
                throw new IllegalArgumentException("ill-conditioned response fit");
            double d=a[col][col];
            for(int k=col;k<n;k++)a[col][k]/=d;
            b[col]/=d;
            for(int r=0;r<n;r++)if(r!=col){
                double scale=a[r][col];
                for(int k=col;k<n;k++)a[r][k]-=scale*a[col][k];
                b[r]-=scale*b[col];
            }
        }
        return b;
    }
}
