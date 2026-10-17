package com.overmind.zero;

import java.util.Arrays;

/**
 * ZERO3 experimental trainable 64-GRU actor/dual-critic/world-prediction core.
 * Runs locally without Android, external knowledge, target labels or servers.
 * This is NOT yet integrated in GameActivity or a complete ZERO3 learner.
 *
 * One-step truncated recurrent backprop is deliberately explicit: gradients
 * update all GRU gate matrices from real next-observation prediction error;
 * no fixed reservoir. Off-policy replay/target networks and long BPTT remain
 * subsequent engineering gates before production adoption.
 */
public final class Zero3RecurrentBrain {
    public static final int SENSOR=SensorimotorMind.S, HIDDEN=64, MOTOR=5, ACTION=4;
    private static final float MODEL_LR=.008f, CRITIC_LR=.009f, ACTOR_LR=.003f;
    private final float[] wz=new float[HIDDEN*(SENSOR+HIDDEN+1)];
    private final float[] wr=new float[wz.length],wn=new float[wz.length];
    private final float[] actor=new float[MOTOR*(HIDDEN+1)];
    private final float[][] critic={new float[HIDDEN+1],new float[HIDDEN+1]};
    private final float[] world=new float[SENSOR*(HIDDEN+ACTION+1)];
    private final float[] state=new float[HIDDEN],prior=new float[HIDDEN];
    private final float[] gatedZ=new float[HIDDEN],gatedR=new float[HIDDEN],
        candidate=new float[HIDDEN],decisionState=new float[HIDDEN];
    private final float[] before=new float[SENSOR],lastNoise=new float[MOTOR],
        action=new float[ACTION],worldInput=new float[HIDDEN+ACTION+1];
    private long rng=0x34EA7CA51DA1L,trainCount,validationCount,recurrentUpdates;
    private boolean pending;
    private float heldoutModelError=Float.NaN,heldoutBaselineError=Float.NaN;
    private final float[] proposal=new float[MOTOR];
    private float storedValue;
    public Zero3RecurrentBrain(){
        for(int i=0;i<wz.length;i++){
            wz[i]=(random()-.5f)*.035f;
            wr[i]=(random()-.5f)*.035f;
            wn[i]=(random()-.5f)*.035f;
        }
        for(int i=0;i<actor.length;i++)actor[i]=(random()-.5f)*.025f;
    }
    private float random(){
        rng^=rng<<13;rng^=rng>>>7;rng^=rng<<17;
        return (rng>>>40)/16777216f;
    }
    private float sample(){
        float x=0;for(int i=0;i<6;i++)x+=random();
        return (x-3f)*.20f;
    }
    private static float clip(float v,float lo,float hi){return Math.max(lo,Math.min(hi,v));}
    private static float sigmoid(float v){return 1f/(1f+(float)Math.exp(-clip(v,-16f,16f)));}
    private static float[] sense(float[] raw){
        if(raw==null||raw.length!=SENSOR)throw new IllegalArgumentException("sensor width");
        float[] x=raw.clone();
        for(int i=0;i<x.length;i++){
            if(!Float.isFinite(x[i]))throw new IllegalArgumentException("invalid sensor");
            x[i]=clip(x[i],-1f,1f);
        }
        return x;
    }
    private static void forward(float[] x,float[] from,float[] to,
        float[] z,float[] r,float[] n,float[] wz,float[] wr,float[] wn){
        int stride=SENSOR+HIDDEN+1;
        for(int i=0;i<HIDDEN;i++){
            int at=i*stride;
            float iz=wz[at+stride-1],ir=wr[at+stride-1];
            for(int j=0;j<SENSOR;j++){iz+=wz[at+j]*x[j];ir+=wr[at+j]*x[j];}
            for(int j=0;j<HIDDEN;j++){iz+=wz[at+SENSOR+j]*from[j];ir+=wr[at+SENSOR+j]*from[j];}
            z[i]=sigmoid(iz);r[i]=sigmoid(ir);
        }
        for(int i=0;i<HIDDEN;i++){
            int at=i*stride;
            float in=wn[at+stride-1];
            for(int j=0;j<SENSOR;j++)in+=wn[at+j]*x[j];
            for(int j=0;j<HIDDEN;j++)in+=wn[at+SENSOR+j]*r[j]*from[j];
            n[i]=(float)Math.tanh(in);
            to[i]=from[i]+z[i]*(n[i]-from[i]);
        }
    }
    private static float read(float[] w,float[] hidden){
        float v=w[HIDDEN];
        for(int k=0;k<HIDDEN;k++)v+=w[k]*hidden[k];
        return clip(v,-10,10);
    }
    private static float dot(float[] w,int at,float[] h,int size){
        float a=w[at+size];
        for(int i=0;i<size;i++)a+=w[at+i]*h[i];
        return a;
    }
    public int realRecurrentUnits(){return HIDDEN;}
    public long trainingUpdates(){return trainCount;}
    public long recurrentUpdates(){return recurrentUpdates;}
    public long validations(){return validationCount;}
    public float heldoutModelError(){return heldoutModelError;}
    public float heldoutBaselineError(){return heldoutBaselineError;}
    public void resetLife(){
        if(pending)throw new IllegalStateException("censor unfinished experience first");
        Arrays.fill(state,0f);
    }
    public void abort(){
        pending=false;
    }
    /**
     * The only source of high-level intentional directions in the experimental
     * autonomous loop. No object, world coordinates or reward class enters.
     */
    public Zero3LearningFoundation.MotorIntent choose(float[] observation,
            long episode,long decision,long cortexRevision){
        if(pending)throw new IllegalStateException("unfinished decision");
        float[] x=sense(observation);
        System.arraycopy(state,0,prior,0,HIDDEN);
        forward(x,prior,state,gatedZ,gatedR,candidate,wz,wr,wn);
        System.arraycopy(state,0,decisionState,0,HIDDEN);
        System.arraycopy(x,0,before,0,SENSOR);
        storedValue=.5f*(read(critic[0],state)+read(critic[1],state));
        for(int out=0;out<MOTOR;out++){
            int at=out*(HIDDEN+1);
            proposal[out]=dot(actor,at,state,HIDDEN);
            lastNoise[out]=sample();
        }
        float sx=clip(proposal[0]+lastNoise[0],-1,1);
        float sy=clip(proposal[1]+lastNoise[1],-1,1);
        float len=(float)Math.hypot(sx,sy);
        if(len<.00001f){sx=1;sy=0;len=1;}
        float dx=sx/len,dy=sy/len;
        float effort=sigmoid(proposal[2]+lastNoise[2]);
        float adhesion=sigmoid(proposal[3]+lastNoise[3]);
        float duration=.25f+.4f*sigmoid(proposal[4]+lastNoise[4]);
        action[0]=dx;action[1]=dy;action[2]=effort;action[3]=adhesion;
        Arrays.fill(worldInput,0);
        System.arraycopy(decisionState,0,worldInput,0,HIDDEN);
        System.arraycopy(action,0,worldInput,HIDDEN,ACTION);
        worldInput[worldInput.length-1]=1f;
        pending=true;
        return new Zero3LearningFoundation.MotorIntent(episode,decision,cortexRevision,
                dx,dy,effort,adhesion,duration);
    }
    public float[] recurrentState(){return state.clone();}
    /** Flatten trainable parameters, excluding transient hidden state / RNG. */
    public float[] parameterSnapshot(){
        int size=wz.length+wr.length+wn.length+actor.length+world.length+2*(HIDDEN+1);
        float[] x=new float[size];int at=0;
        for(float[] a:new float[][]{wz,wr,wn,actor,critic[0],critic[1],world}){
            System.arraycopy(a,0,x,at,a.length);at+=a.length;
        }
        return x;
    }
    /**
     * Feed a REAL post-physics observation and REAL reward, never imagination.
     * The heldout branch evaluates but does not update any trainable weight.
     * Currently TD actor/dual critics and one-step recurrent world model.
     */
    public void consequence(float[] rawNext,float realSurvivalReward,boolean heldout){
        if(!pending)throw new IllegalStateException("missing decision");
        if(!Float.isFinite(realSurvivalReward))throw new IllegalArgumentException("nonfinite reward");
        float[] next=sense(rawNext);
        pending=false;
        float[] predictionError=new float[SENSOR];
        float modelMse=0,baselineMse=0;
        float[] dh=new float[HIDDEN];
        final int WIDTH=worldInput.length;
        for(int d=0;d<SENSOR;d++){
            int at=d*WIDTH;
            float predicted=0;
            for(int j=0;j<WIDTH;j++)predicted+=world[at+j]*worldInput[j];
            float actual=next[d]-before[d];
            float err=clip(actual-predicted,-2,2);
            predictionError[d]=err;
            modelMse+=err*err;baselineMse+=actual*actual;
            if(!heldout){
                for(int j=0;j<HIDDEN;j++)dh[j]+=err*world[at+j];
                for(int j=0;j<WIDTH;j++)
                    world[at+j]=clip(world[at+j]+MODEL_LR*err*worldInput[j],-1.5f,1.5f);
            }
        }
        if(heldout){
            float err=modelMse/SENSOR,base=baselineMse/SENSOR;
            float a=validationCount==0?1f:.06f;
            heldoutModelError=validationCount==0?err:heldoutModelError*(1-a)+a*err;
            heldoutBaselineError=validationCount==0?base:heldoutBaselineError*(1-a)+a*base;
            validationCount++;
            return;
        }
        // Actual next-state bootstrap. Preview never consumes or trains the
        // next experience; it is NOT synthetic world physics.
        float[] preview=new float[HIDDEN];
        forward(next,state,preview,new float[HIDDEN],new float[HIDDEN],
                new float[HIDDEN],wz,wr,wn);
        float nextValue=.5f*(read(critic[0],preview)+read(critic[1],preview));
        float target=clip(realSurvivalReward+.98f*nextValue,-3,3);
        float advantage=clip(target-storedValue,-2,2);
        for(int which=0;which<2;which++){
            float value=read(critic[which],decisionState);
            float err=clip(target-value,-2,2);
            for(int j=0;j<HIDDEN;j++)
                critic[which][j]=clip(critic[which][j]+CRITIC_LR*err*decisionState[j],-2,2);
            critic[which][HIDDEN]=clip(critic[which][HIDDEN]+CRITIC_LR*err,-2,2);
        }
        for(int k=0;k<MOTOR;k++){
            float explorationGradient=clip(lastNoise[k]/(.2f*.2f),-3f,3f);
            int at=k*(HIDDEN+1);
            for(int j=0;j<HIDDEN;j++)
                actor[at+j]=clip(actor[at+j]+ACTOR_LR*advantage*explorationGradient*decisionState[j],-1.5f,1.5f);
            actor[at+HIDDEN]=clip(actor[at+HIDDEN]+ACTOR_LR*advantage*explorationGradient,-1.5f,1.5f);
        }
        // One-step truncated-BPTT through ALL GRU gates (the past hidden state
        // is detached). This does not falsely claim long-range BPTT.
        float[] dn=new float[HIDDEN],dz=new float[HIDDEN],dr=new float[HIDDEN];
        int stride=SENSOR+HIDDEN+1;
        for(int i=0;i<HIDDEN;i++){
            dn[i]=clip(dh[i]*gatedZ[i]*(1-candidate[i]*candidate[i]),-.75f,.75f);
            dz[i]=clip(dh[i]*(candidate[i]-prior[i])*gatedZ[i]*(1-gatedZ[i]),-.75f,.75f);
        }
        for(int j=0;j<HIDDEN;j++){
            float sum=0;
            for(int i=0;i<HIDDEN;i++)sum+=dn[i]*wn[i*stride+SENSOR+j]*prior[j];
            dr[j]=clip(sum*gatedR[j]*(1-gatedR[j]),-.75f,.75f);
        }
        float changed=0;
        for(int i=0;i<HIDDEN;i++){
            int at=i*stride;
            for(int j=0;j<SENSOR;j++){
                float old=wn[at+j];
                wn[at+j]=clip(old+MODEL_LR*dn[i]*before[j],-1.5f,1.5f);
                wz[at+j]=clip(wz[at+j]+MODEL_LR*dz[i]*before[j],-1.5f,1.5f);
                wr[at+j]=clip(wr[at+j]+MODEL_LR*dr[i]*before[j],-1.5f,1.5f);
                changed+=Math.abs(wn[at+j]-old);
            }
            for(int j=0;j<HIDDEN;j++){
                int ix=at+SENSOR+j;
                wn[ix]=clip(wn[ix]+MODEL_LR*dn[i]*gatedR[j]*prior[j],-1.5f,1.5f);
                wz[ix]=clip(wz[ix]+MODEL_LR*dz[i]*prior[j],-1.5f,1.5f);
                wr[ix]=clip(wr[ix]+MODEL_LR*dr[i]*prior[j],-1.5f,1.5f);
            }
            wn[at+stride-1]=clip(wn[at+stride-1]+MODEL_LR*dn[i],-1.5f,1.5f);
            wz[at+stride-1]=clip(wz[at+stride-1]+MODEL_LR*dz[i],-1.5f,1.5f);
            wr[at+stride-1]=clip(wr[at+stride-1]+MODEL_LR*dr[i],-1.5f,1.5f);
        }
        if(changed>0f)recurrentUpdates++;
        trainCount++;
    }
}
