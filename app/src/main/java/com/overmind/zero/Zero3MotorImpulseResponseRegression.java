package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 G3 motor impulse-response laboratory: measures *actual physical*
 * response to a localized actuator pulse against a matched unperturbed world.
 * The pulse is injected into the 64 commands consumed by the REAL gel solver,
 * NEVER by moving body coordinates, inventing energy, or looking at gems.
 *
 * Learns a small action-/pose-conditioned causal RESPONSE PREDICTOR by ridge
 * regression on actual paired transitions. Prediction is not acted on in the
 * shipped game. Validation uses +45/-45 degree poses that are not trained on.
 * No walking gait is hardcoded; this is an isolated controllability test.
 */
public final class Zero3MotorImpulseResponseRegression {
    private static final int F=42, OUTPUTS=3;
    private static final int WARMUP=12, PULSE=12, SETTLE=48;
    private static final float DT=.025f,AMP=.20f;
    private static final float[][] DIRS={{1,0},{0,1},{-1,0},{0,-1}};
    private static final int[] SECTORS={0,4,8,12};
    private static final float[] TRAIN_POSES={0f,(float)(Math.PI/2)};
    private static final float[] TEST_POSES={
        (float)(Math.PI/8),(float)(-Math.PI/8),
        (float)(Math.PI/4),(float)(-Math.PI/4),
        (float)(3*Math.PI/8),(float)(-3*Math.PI/8)};

    private static void assertTrue(boolean b,String msg){
        if(!b)throw new AssertionError("ZERO3 PHYSICAL RESPONSE "+msg);
    }
    private static class Scenario {
        final MotorBrain.Body body=new MotorBrain.Body();
        final Morphology.Surface skin=new Morphology.Surface(body);
        final SymbioteMatterV1 gel=new SymbioteMatterV1();
        final Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex(0f);
        final Zero3LearningFoundation.MotorIntent intent;
        final float observedPose;
        double work,gross,impulse;
        float prevX,prevY;
        Scenario(int direction,float rotation){
            gel.reset(body,skin);
            // Proprioceptive anatomical frame, measured from the actual skin.
            // Never pass the laboratory's hidden rotation to the predictor.
            float baseAngle=skinAxisAngle();
            if(rotation!=0){
                float cs=(float)Math.cos(rotation),sn=(float)Math.sin(rotation);
                Zero3MotorRetentionRegression.rotate(skin.x,skin.y,body.cx,body.cy,cs,sn);
                Zero3MotorRetentionRegression.rotate(body.x,body.y,body.cx,body.cy,cs,sn);
                Zero3MotorRetentionRegression.rotate(gel.x,gel.y,body.cx,body.cy,cs,sn);
                gel.observeBody(body,skin);
            }
            float angle=(float)(skinAxisAngle()-baseAngle);
            while(angle>Math.PI)angle-=(float)(2*Math.PI);
            while(angle<-Math.PI)angle+=(float)(2*Math.PI);
            observedPose=angle;
            intent=new Zero3LearningFoundation.MotorIntent(
                300,0,0,DIRS[direction][0],DIRS[direction][1],1f,.3f,.5f);
            prevX=body.cx;prevY=body.cy;
        }
        /** Measured orientation from the true shell, not a world/oracle angle. */
        private float skinAxisAngle(){
            double x=0,y=0;
            for(int k=0;k<Morphology.POINTS/16;k++){
                x+=skin.x[k]-body.cx;
                y+=skin.y[k]-body.cy;
            }
            return (float)Math.atan2(y,x);
        }
        void tick(int frame,int sector,int channel,float sign){
            float[] muscles=cortex.command(intent,body,skin,DT);
            if(sector>=0&&frame>=WARMUP&&frame<WARMUP+PULSE){
                for(int offset=-2;offset<=2;offset++){
                    int idx=((sector+offset+16)%16)*4+channel;
                    float strength=AMP*(3-Math.abs(offset))/3f*sign;
                    muscles[idx]=Math.max(-1f,Math.min(1f,muscles[idx]+strength));
                }
            }
            float before=gel.work;
            gel.step(body,skin,muscles,DT);
            assertTrue(gel.invalid==0&&Float.isFinite(body.cx)&&Float.isFinite(body.cy),
                "nonfinite/collapsed physical matter");
            gross+=Math.hypot(body.cx-prevX,body.cy-prevY);
            prevX=body.cx;prevY=body.cy;
            work+=Math.max(0,gel.work-before);
            impulse+=gel.lastMuscleReactionImpulse;
        }
    }
    private static class Trial {
        final int dir,channel,sector,polarity;
        final float pose;
        final double early,late,extraWork,grossDelta,delayedFraction;
        final double[] features;
        Trial(int dir,float pose,int sector,int channel,int sign,
              double early,double late,double cost,double gross){
            this.dir=dir;this.pose=pose;this.sector=sector;
            this.channel=channel;polarity=sign;
            this.early=early;this.late=late;extraWork=cost;grossDelta=gross;
            delayedFraction=Math.abs(late)/(1e-9+Math.abs(early)+Math.abs(late));
            features=features(dir,pose,sector,channel,sign);
        }
        double signedResponse(){return early+late;}
        double usefulScore(){return signedResponse()-45*Math.max(0,extraWork);}
    }
    /**
     * Physically motivated, rotationally aware descriptors. A muscle sector
     * is not a fixed world compass heading: use the body's current orientation
     * to project local radial/tangent axes onto a voluntary intent.
     *
     * Channel-interaction terms let adhesion and radial/tangential responses
     * differ; no learned gait or resource coordinates are encoded.
     */
    private static double[] features(int dir,float pose,int sector,int channel,int sign){
        double[] x=new double[F];
        double dx=DIRS[dir][0],dy=DIRS[dir][1];
        double sn=Math.sin(pose),cs=Math.cos(pose);
        double local=(sector+.5)*Math.PI*2/16;
        double world=local+pose;
        double wx=Math.cos(world),wy=Math.sin(world);
        double radial=wx*dx+wy*dy;
        double tangent=-wy*dx+wx*dy;
        x[0]=1;
        x[1]=dx;x[2]=dy;x[3]=sn;x[4]=cs;
        x[5+channel]=1;
        x[9]=wx;x[10]=wy;x[11]=sign;
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
    private static Trial physical(int dir,float pose,int sector,int channel,int sign){
        Scenario base=new Scenario(dir,pose);
        Scenario probe=new Scenario(dir,pose);
        assertTrue(Math.abs(base.observedPose-probe.observedPose)<.0001f,
            "paired bodies have different proprioceptive orientation");
        assertTrue(Math.abs(probe.observedPose-pose)<.001f,
            "skin orientation reconstruction failed");
        double early=0;
        final int total=WARMUP+PULSE+SETTLE;
        for(int frame=0;frame<total;frame++){
            base.tick(frame,-1,channel,sign);
            probe.tick(frame,sector,channel,sign);
            if(frame==WARMUP+PULSE-1){
                early=(probe.body.cx-base.body.cx)*DIRS[dir][0]+
                    (probe.body.cy-base.body.cy)*DIRS[dir][1];
            }
        }
        double finalDifference=(probe.body.cx-base.body.cx)*DIRS[dir][0]+
                               (probe.body.cy-base.body.cy)*DIRS[dir][1];
        return new Trial(dir,probe.observedPose,sector,channel,sign,early,
                         finalDifference-early,probe.work-base.work,
                         probe.gross-base.gross);
    }
    /** Fixed-size ridge least squares; only physically observed training pairs. */
    private static final class ResponseModel {
        final double[][] weights=new double[OUTPUTS][F];
        ResponseModel(ArrayList<Trial> training){
            double[][] gram=new double[F][F];
            double[][] target=new double[OUTPUTS][F];
            for(Trial t:training){
                double[] x=t.features;
                double[] ys={t.early,t.late,1000*t.extraWork};
                for(int j=0;j<F;j++){
                    for(int k=0;k<F;k++)gram[j][k]+=x[j]*x[k];
                    for(int o=0;o<OUTPUTS;o++)target[o][j]+=x[j]*ys[o];
                }
            }
            for(int o=0;o<OUTPUTS;o++){
                double[][] regularized=new double[F][F];
                for(int j=0;j<F;j++){
                    System.arraycopy(gram[j],0,regularized[j],0,F);
                    regularized[j][j]+=.5; // regularization controls overfit
                }
                weights[o]=solve(regularized,target[o]);
            }
        }
        double[] infer(Trial t){
            double[] p=new double[OUTPUTS];
            for(int i=0;i<OUTPUTS;i++){
                for(int j=0;j<F;j++)p[i]+=weights[i][j]*t.features[j];
            }
            p[2]/=1000;
            return p;
        }
    }
    private static double[] solve(double[][] a,double[] rhs){
        final int n=rhs.length;
        double[] b=rhs.clone();
        for(int pivot=0;pivot<n;pivot++){
            int best=pivot;
            for(int r=pivot+1;r<n;r++)
                if(Math.abs(a[r][pivot])>Math.abs(a[best][pivot]))best=r;
            double[] sw=a[pivot];a[pivot]=a[best];a[best]=sw;
            double v=b[pivot];b[pivot]=b[best];b[best]=v;
            assertTrue(Math.abs(a[pivot][pivot])>1e-10,"singular regression");
            double div=a[pivot][pivot];
            for(int col=pivot;col<n;col++)a[pivot][col]/=div;
            b[pivot]/=div;
            for(int r=0;r<n;r++){
                if(r==pivot)continue;
                double scale=a[r][pivot];
                for(int col=pivot;col<n;col++)a[r][col]-=scale*a[pivot][col];
                b[r]-=scale*b[pivot];
            }
        }
        return b;
    }
    private static void printError(ResponseModel m,ArrayList<Trial> holdout){
        double[] mse=new double[OUTPUTS],zero=new double[OUTPUTS],delayAbs=new double[2];
        for(Trial t:holdout){
            double[] p=m.infer(t);
            double[] actual={t.early,t.late,t.extraWork};
            for(int o=0;o<OUTPUTS;o++){
                mse[o]+=Math.pow(p[o]-actual[o],2);
                zero[o]+=Math.pow(actual[o],2);
            }
            delayAbs[0]+=Math.abs(t.early);
            delayAbs[1]+=Math.abs(t.late);
        }
        for(int o=0;o<OUTPUTS;o++){
            mse[o]/=holdout.size();zero[o]/=holdout.size();
            assertTrue(Double.isFinite(mse[o]),"nonfinite prediction");
        }
        System.out.printf(Locale.US,
            "ZERO3 IMPULSE MODEL holdout=%d earlyMSE=%.9f zeroEarlyMSE=%.9f"+
            " lateMSE=%.9f zeroLateMSE=%.9f costMSE=%.12f zeroCostMSE=%.12f"+
            " totalEarlyAbs=%.3f totalDelayedAbs=%.3f%n",
            holdout.size(),mse[0],zero[0],mse[1],zero[1],
            mse[2],zero[2],delayAbs[0],delayAbs[1]);
    }
    public static void main(String[] args){
        ArrayList<Trial> training=new ArrayList<>();
        ArrayList<Trial> holdout=new ArrayList<>();
        // Experimental interventions are not 2,000 consecutive life decisions:
        // they are REPEATED matched real-physics counterfactual pair trials.
        for(int dir=0;dir<DIRS.length;dir++){
            for(float pose:TRAIN_POSES)
                for(int channel=0;channel<4;channel++)
                    for(int sector:SECTORS)
                        for(int sign:new int[]{-1,1})
                            training.add(physical(dir,pose,sector,channel,sign));
            for(float pose:TEST_POSES)
                for(int channel=0;channel<4;channel++)
                    for(int sector:SECTORS)
                        for(int sign:new int[]{-1,1})
                            holdout.add(physical(dir,pose,sector,channel,sign));
        }
        assertTrue(training.size()==256&&holdout.size()==768,"unexpected train/heldout");
        ResponseModel model=new ResponseModel(training);
        printError(model,holdout);
        double fullGain=0,totalCost=0,selectedSigned=0,randomSigned=0;
        int better=0,costControlled=0,positive=0;
        // 4 directions x 2 unseen heldout poses: choose purely from model
        // predictions, then score physically observed unseen-pose outcomes.
        for(int dir=0;dir<4;dir++)for(float pose:TEST_POSES){
            Trial selected=null,random=null;
            double bestPred=0; // do nothing is allowed
            for(Trial t:holdout){
                if(t.dir!=dir||Math.abs(t.pose-pose)>.001f)continue;
                if(random==null)random=t;
                double[] p=model.infer(t);
                double projected=p[0]+p[1]-45*Math.max(0,p[2]);
                if(Double.isFinite(projected)&&projected>bestPred){
                    bestPred=projected;selected=t;
                }
            }
            double realised=selected==null?0:selected.usefulScore();
            double signed=selected==null?0:selected.signedResponse();
            double cost=selected==null?0:selected.extraWork;
            if(realised>0)better++;
            if(signed>0)positive++;
            if(cost<.005)costControlled++;
            totalCost+=cost;fullGain+=realised;selectedSigned+=signed;
            randomSigned+=random.signedResponse();
            System.out.printf(Locale.US,
                "ZERO3 IMPULSE HELDOUT intent=[%.0f %.0f] poseDeg=%.1f"+
                " selected=%s predictedUtility=%.5f observedUtility=%.5f"+
                " signedDisplacementGain=%.5f extraWork=%.7f"+
                " matchedRandomSignedGain=%.5f%n",
                DIRS[dir][0],DIRS[dir][1],pose*180/Math.PI,
                selected==null?"none":("sector"+selected.sector+"/channel"+
                    selected.channel+"/sign"+selected.polarity),
                bestPred,realised,signed,cost,random.signedResponse());
        }
        int scenarios=DIRS.length*TEST_POSES.length;
        System.out.printf(Locale.US,
            "ZERO3 IMPULSE RESPONSE SUMMARY trainPhysicalPairs=%d heldoutPhysicalPairs=%d"+
            " totalPhysicalFrames=%d blindPoseCases=%d usefulPositive=%d/%d"+
            " positiveDirectional=%d/%d costBelow0.005=%d/%d"+
            " totalObservedUtility=%.5f totalSignedGain=%.5f"+
            " totalAddedWork=%.8f randomBaselineSignedGain=%.5f"+
            " (NO AUTOMATIC MOTOR PROMOTION; no independent worlds/seeds)%n",
            training.size(),holdout.size(),
            (training.size()+holdout.size())*2*(WARMUP+PULSE+SETTLE),
            scenarios,better,scenarios,positive,scenarios,costControlled,scenarios,
            fullGain,selectedSigned,totalCost,randomSigned);
    }
}
