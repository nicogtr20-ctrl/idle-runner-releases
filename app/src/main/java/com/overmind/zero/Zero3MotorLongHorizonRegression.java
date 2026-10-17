package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * V3.6: real-material 384-frame response prediction conditioned on measured
 * anatomy, pre-pulse momentum and local shell strain. NOT a controller.
 *
 * Matched baseline and intervention are separately simulated laboratory
 * counterfactuals. The live organism cannot observe the baseline branch,
 * and these experimental samples cannot be credited as life experience.
 *
 * Train: poses 0/90, preconditions 0/1/2; calibrate: +/-22.5, 0/1/2.
 * BLIND final: +/-30 and +/-60 with precondition 3 (never fit/calibrated).
 * All splits contain independently evolved true symbiote material.
 */
public final class Zero3MotorLongHorizonRegression {
    static final float DT=.025f;
    static final int FRAMES=384, PULSE_START=12, PULSE_END=24, F=52;
    static final int[] SECTORS={0,8};
    static final float[] FIT_POSES={0,(float)(Math.PI/2)};
    static final float[] CAL_POSES={(float)(Math.PI/8),(float)(-Math.PI/8)};
    static final float[] BLIND_POSES={(float)(Math.PI/6),(float)(-Math.PI/6),
                                (float)(Math.PI/3),(float)(-Math.PI/3)};
    static final float[][] DIRECTIONS={{1,0},{0,1},{-1,0},{0,-1}};
    static final float REFERENCE_AXIS;
    static{
        Zero3MotorImpulseResponseRegression.Scenario fresh=
            new Zero3MotorImpulseResponseRegression.Scenario(0,0);
        REFERENCE_AXIS=Zero3MotorResponseKernel.membraneAxis(fresh.body,fresh.skin);
    }
    static void check(boolean ok,String msg){
        if(!ok)throw new AssertionError("ZERO3 LONG RESPONSE "+msg);
    }
    static class Trial {
        final int direction,precondition,sector,channel,sign;
        final float poseGroup,observedPose;
        final double[] context,baseFeatures;
        final double early,late,extraWork;
        Trial(int direction,float poseGroup,int condition,int sector,int channel,
              int sign,double[] context,double early,double late,double work){
            this.direction=direction;this.poseGroup=poseGroup;
            precondition=condition;this.sector=sector;this.channel=channel;
            this.sign=sign;this.context=context;this.early=early;
            this.late=late;extraWork=work;
            baseFeatures=new double[42];
            System.arraycopy(context,0,baseFeatures,0,42);
            observedPose=(float)Math.atan2(context[3],context[4]);
        }
        double[] targets(){return new double[]{early,late,extraWork};}
        double signedGain(){return early+late;}
    }
    static float clamp(float x,float bound){
        return Math.max(-bound,Math.min(bound,x));
    }
    static double[] context(int direction,MotorBrain.Body b,
                            Morphology.Surface skin,SymbioteMatterV1 gel,
                            int sector,int channel,int sign){
        float dirX=DIRECTIONS[direction][0],dirY=DIRECTIONS[direction][1];
        float pose=Zero3MotorResponseKernel.relativePose(
            Zero3MotorResponseKernel.membraneAxis(b,skin),REFERENCE_AXIS);
        double[] x=new double[F];
        double[] base=Zero3MotorResponseKernel.encode(
            dirX,dirY,pose,sector,channel,sign);
        System.arraycopy(base,0,x,0,base.length);
        double meanR=0,rSq=0,radialVel=0,held=0;
        for(int i=0;i<Morphology.POINTS;i++){
            float vx=skin.x[i]-b.cx,vy=skin.y[i]-b.cy;
            double r=Math.hypot(vx,vy);
            meanR+=r;rSq+=r*r;
            if(r>1e-3)radialVel+=((skin.vx[i]-b.cvx)*vx+
                                  (skin.vy[i]-b.cvy)*vy)/r;
            held+=skin.gripHold[i]>Morphology.GRIP_HOLD?1:0;
        }
        meanR/=Morphology.POINTS;
        double stdR=Math.sqrt(Math.max(0,rSq/Morphology.POINTS-meanR*meanR));
        double localR=0,localRV=0;
        int sites=Morphology.POINTS/16;
        for(int j=0;j<sites;j++){
            int k=sector*sites+j;
            double rx=skin.x[k]-b.cx,ry=skin.y[k]-b.cy;
            double rr=Math.hypot(rx,ry);
            localR+=rr;
            if(rr>1e-3)localRV+=((skin.vx[k]-b.cvx)*rx+
                                (skin.vy[k]-b.cvy)*ry)/rr;
        }
        x[42]=clamp(b.cvx/10f,3);
        x[43]=clamp(b.cvy/10f,3);
        x[44]=clamp((dirX*b.cvx+dirY*b.cvy)/10f,3);
        x[45]=(meanR/28)-1;
        x[46]=stdR/28;
        x[47]=clamp((float)(radialVel/Morphology.POINTS/10),3);
        x[48]=held/Morphology.POINTS;
        x[49]=localR/sites/28-1;
        x[50]=clamp((float)(localRV/sites/10),3);
        x[51]=Math.min(1,Math.max(0,gel.areaError/500));
        for(double y:x)check(Double.isFinite(y),"nonfinite proprioception feature");
        return x;
    }
    static Trial run(int direction,float pose,int condition,int sector,int channel,int sign){
        Zero3MotorImpulseResponseRegression.Scenario untouched=
            new Zero3MotorImpulseResponseRegression.Scenario(direction,pose,condition);
        Zero3MotorImpulseResponseRegression.Scenario pulse=
            new Zero3MotorImpulseResponseRegression.Scenario(direction,pose,condition);
        double[] preAction=null;
        double early=0;
        for(int frame=0;frame<FRAMES;frame++){
            if(frame==PULSE_START)
                preAction=context(direction,pulse.body,pulse.skin,pulse.gel,
                                  sector,channel,sign);
            untouched.tick(frame,-1,channel,sign);
            pulse.tick(frame,sector,channel,sign);
            if(frame==PULSE_END-1)
                early=(pulse.body.cx-untouched.body.cx)*DIRECTIONS[direction][0]+
                      (pulse.body.cy-untouched.body.cy)*DIRECTIONS[direction][1];
        }
        double total=(pulse.body.cx-untouched.body.cx)*DIRECTIONS[direction][0]+
                     (pulse.body.cy-untouched.body.cy)*DIRECTIONS[direction][1];
        check(preAction!=null,"missing pre-action sensory state");
        return new Trial(direction,pose,condition,sector,channel,sign,
            preAction,early,total-early,pulse.work-untouched.work);
    }
    static ArrayList<Trial> records(float[] poses,int[] states){
        ArrayList<Trial> a=new ArrayList<>();
        for(int d=0;d<4;d++)for(float p:poses)for(int s:states)
            for(int ch=0;ch<4;ch++)for(int sector:SECTORS)
                for(int sign:new int[]{-1,1})
                    a.add(run(d,p,s,sector,ch,sign));
        return a;
    }
    static final class Ridge {
        final int dim;
        final double[][] weights;
        Ridge(ArrayList<Trial> training,int dim){
            this.dim=dim;weights=new double[3][dim];
            double[][] gram=new double[dim][dim],target=new double[3][dim];
            for(Trial t:training){
                double[] f=dim==F?t.context:t.baseFeatures;
                double[] y={t.early,t.late,1000*t.extraWork};
                for(int i=0;i<dim;i++){
                    for(int j=0;j<dim;j++)gram[i][j]+=f[i]*f[j];
                    for(int h=0;h<3;h++)target[h][i]+=f[i]*y[h];
                }
            }
            for(int h=0;h<3;h++){
                double[][] g=new double[dim][dim];
                for(int i=0;i<dim;i++){
                    System.arraycopy(gram[i],0,g[i],0,dim);
                    g[i][i]+=.5;
                }
                weights[h]=Zero3MotorResponseKernel.solve(g,target[h]);
            }
        }
        double[] predict(Trial sample){
            double[] x=dim==F?sample.context:sample.baseFeatures;
            double[] y=new double[3];
            for(int h=0;h<3;h++)
                for(int i=0;i<dim;i++)y[h]+=weights[h][i]*x[i];
            y[2]/=1000;
            return y;
        }
    }
    static class Stats{
        int size;
        final double[] mse=new double[3],zero=new double[3];
        Stats(Ridge m,ArrayList<Trial> tests){
            size=tests.size();
            for(Trial t:tests){
                double[] p=m.predict(t),real=t.targets();
                for(int h=0;h<3;h++){
                    mse[h]+=Math.pow(p[h]-real[h],2);
                    zero[h]+=real[h]*real[h];
                }
            }
            for(int h=0;h<3;h++){mse[h]/=size;zero[h]/=size;}
        }
        boolean beatsZero(){
            for(int i=0;i<3;i++)
                if(!(mse[i]<zero[i]*.9))return false;
            return true;
        }
        String format(){
            return String.format(Locale.US,
                "N=%d immediate=%.9f zeroImmediate=%.9f"+
                " delayed=%.9f zeroDelayed=%.9f cost=%.12f zeroCost=%.12f"+
                " beatsZero=%s",
                size,mse[0],zero[0],mse[1],zero[1],mse[2],zero[2],
                Boolean.toString(beatsZero()));
        }
    }
    public static void main(String[] args){
        ArrayList<Trial> fit=records(FIT_POSES,new int[]{0,1,2});
        ArrayList<Trial> calib=records(CAL_POSES,new int[]{0,1,2});
        check(fit.size()==384&&calib.size()==384,"bad 384-step splits");
        Ridge anatomyOnly=new Ridge(fit,42),sensory=new Ridge(fit,F);
        Stats simpleCal=new Stats(anatomyOnly,calib),senseCal=new Stats(sensory,calib);
        System.out.println("ZERO3 LONG384 CALIB old42 "+simpleCal.format());
        System.out.println("ZERO3 LONG384 CALIB state52 "+senseCal.format());
        // Fixed before final: only calibrations determine whether a model may
        // suggest a probe. Final poses and state=3 cannot change this choice.
        boolean eligible=senseCal.beatsZero();
        ArrayList<Trial> blind=records(BLIND_POSES,new int[]{3});
        check(blind.size()==256,"unexpected final split");
        Stats simpleFinal=new Stats(anatomyOnly,blind);
        Stats sensoryFinal=new Stats(sensory,blind);
        System.out.println("ZERO3 LONG384 BLIND old42 "+simpleFinal.format());
        System.out.println("ZERO3 LONG384 BLIND state52 "+sensoryFinal.format());
        int useful=0,costOkay=0;
        double signed=0,utility=0,totalCost=0;
        for(int d=0;d<4;d++)for(float pose:BLIND_POSES){
            Trial selected=null;
            double high=0;
            for(Trial t:blind){
                if(t.direction!=d||Math.abs(t.poseGroup-pose)>.001f)continue;
                double[] p=sensory.predict(t);
                double score=p[0]+p[1]-45*Math.max(0,p[2]);
                if(eligible&&score>high){selected=t;high=score;}
            }
            double real=selected==null?0:selected.signedGain();
            double cost=selected==null?0:selected.extraWork;
            if(real>0)useful++;
            if(cost<.005)costOkay++;
            signed+=real;totalCost+=cost;
            utility+=real-45*Math.max(0,cost);
            System.out.printf(Locale.US,
                "ZERO3 LONG384 CASE direction=%d pose=%.1f"+
                " selected=%s actualSigned=%.5f actualWork=%.8f predictedUtility=%.5f%n",
                d,pose*180/Math.PI,selected==null?"NONE":
                    (selected.sector+"/"+selected.channel+"/"+selected.sign),
                real,cost,high);
        }
        System.out.printf(Locale.US,
            "ZERO3 LONG384 SUMMARY realPairedTrain=%d calibration=%d blind=%d"+
            " pairedSolverFrames=%d calibrationGate=%s"+
            " usefulBlind=%d/16 costBelow0.005=%d/16"+
            " blindSigned=%.5f blindUtility=%.5f blindExtraWork=%.7f"+
            " (NOT LONG-LIFE LEARNING; no motor promotion)%n",
            fit.size(),calib.size(),blind.size(),
            (fit.size()+calib.size()+blind.size())*2*FRAMES,
            Boolean.toString(eligible),useful,costOkay,signed,utility,totalCost);
    }
}
