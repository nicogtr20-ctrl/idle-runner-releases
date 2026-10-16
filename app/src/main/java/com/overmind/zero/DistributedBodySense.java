package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ZERO 0121: independent readouts from EVERY material perimeter particle.
 * The original 48-channel body model, 26-input policy and all motor weights
 * are left untouched. Nine actual local measurements per particle are pooled
 * only for a small, held-out-validated learned action model; the full 96x9
 * local map is retained for local sensing and truthful visual diagnostics.
 *
 * "Sight" is a short outward obstacle probe at every physical particle.
 * It has no knowledge of object labels, energy or hidden game objectives.
 * The original independently tested spectral and 16-ray vision remain intact.
 */
public final class DistributedBodySense {
    public static final String KEY="distributed_body_v121";
    public static final int POINTS=Morphology.POINTS, CHANNELS=9, SECTORS=16;
    public static final int FEATURES=POINTS*CHANNELS, SUMMARY=SECTORS*CHANNELS;
    public static final int ACTIONS=SensorimotorMind.ACTIONS;
    public static final int TOUCH=0, MOTOR=1, GRIP_DRIVE=2, GRIP_HELD=3,
        RADIAL_SPEED=4, TANGENTIAL_SPEED=5, SHAPE=6, STRAIN=7, OPTIC=8;
    private static final int HEAD=4, PRED=ACTIONS*SUMMARY*HEAD;
    private static final int VALUE_WIDTH=SUMMARY+1, VALUES=ACTIONS*VALUE_WIDTH;
    public static final int PARAMETERS=PRED+VALUES;
    private static final float PRED_LR=.009f, VALUE_LR=.009f;
    private static float clip(float x,float lo,float hi){return Math.max(lo,Math.min(hi,x));}

    private final float[] current=new float[FEATURES], pooled=new float[SUMMARY],
        before=new float[SUMMARY], predictor=new float[PRED], values=new float[VALUES];
    private int lastAction=-1;
    public long transitions=0;
    public int validations=0, choiceInfluences=0;
    public float predictionError=Float.NaN, unchangedError=Float.NaN;

    /** Pure physical measurement; never actuates tissue or changes the world.
     * Called at decision boundaries, NOT on every accelerated physics substep.
     * Rays from all 96 membrane sites are limited to six 22-unit samples.
     */
    public static float[] sense(MotorBrain.Body b,Morphology.Surface s,
                                ScarcityLab room,ArrayList<UnknownWorld.Entity> objects){
        if(b==null||s==null)throw new IllegalArgumentException("Missing body");
        float[] out=new float[FEATURES];
        for(int i=0;i<POINTS;i++){
            int next=(i+1)%POINTS,prev=(i+POINTS-1)%POINTS;
            float dx=s.x[i]-b.cx,dy=s.y[i]-b.cy;
            float radius=(float)Math.hypot(dx,dy);
            float nx,ny;
            if(radius>.0001f){nx=dx/radius;ny=dy/radius;}
            else{
                float edx=s.x[next]-s.x[prev],edy=s.y[next]-s.y[prev];
                float len=(float)Math.hypot(edx,edy);
                nx=len>.0001f?-edy/len:1;ny=len>.0001f?edx/len:0;
            }
            float vx=s.vx[i]-b.cvx,vy=s.vy[i]-b.cvy;
            float distanceNext=(float)Math.hypot(s.x[next]-s.x[i],s.y[next]-s.y[i]);
            float distancePrev=(float)Math.hypot(s.x[prev]-s.x[i],s.y[prev]-s.y[i]);
            int p=i*CHANNELS;
            out[p+TOUCH]=clip(Math.max(s.touchSense[i],s.touchPulse[i]),0,1);
            out[p+MOTOR]=clip(s.extension[i]/Morphology.MAX_REACH,0,1);
            out[p+GRIP_DRIVE]=clip(s.gripCommand[i]/1.4f,0,1);
            out[p+GRIP_HELD]=clip(s.gripHold[i]/.5f,0,1);
            out[p+RADIAL_SPEED]=clip((vx*nx+vy*ny)/130f,-1,1);
            out[p+TANGENTIAL_SPEED]=clip((-vx*ny+vy*nx)/130f,-1,1);
            out[p+SHAPE]=clip(radius/Morphology.MAX_RADIAL,0,1);
            // Local edge stretching, never an artificial hand/skeleton label.
            out[p+STRAIN]=clip((distanceNext+distancePrev)/60f,0,1);
            float obstacle=0;
            if(objects!=null){
                float hit=SensorimotorMind.Senses.firstSolidDistance(room,objects,
                    s.x[i],s.y[i],nx,ny,132f);
                if(Float.isFinite(hit))obstacle=clip(1f-hit/154f,0f,1f);

            }
            out[p+OPTIC]=obstacle;
        }
        return out;
    }
    /** The full node map remains accessible: it is NOT discarded by pooling. */
    public float node(int index,int channel){
        if(index<0||index>=POINTS||channel<0||channel>=CHANNELS)return 0;
        return current[index*CHANNELS+channel];
    }
    public float[] localMap(){return current.clone();}
    public float[] pooledMap(){return pooled.clone();}
    private static boolean valid(float[] raw){
        if(raw==null||raw.length!=FEATURES)return false;
        for(float x:raw)if(!Float.isFinite(x)||x< -1.001f||x>1.001f)return false;
        return true;
    }
    public void feed(float[] raw){
        if(!valid(raw))return;
        System.arraycopy(raw,0,current,0,FEATURES);
        Arrays.fill(pooled,0f);
        for(int i=0;i<POINTS;i++){
            int sector=i*SECTORS/POINTS;
            for(int ch=0;ch<CHANNELS;ch++){
                int at=ch*SECTORS+sector;
                float value=raw[i*CHANNELS+ch];
                if(ch==TOUCH||ch==GRIP_DRIVE||ch==GRIP_HELD||ch==OPTIC)
                    pooled[at]=Math.max(pooled[at],value);
                else pooled[at]+=value/(POINTS/SECTORS);
            }
        }
    }
    public void resetLife(){
        Arrays.fill(current,0);Arrays.fill(pooled,0);
        Arrays.fill(before,0);lastAction=-1;
    }
    public void resumeLife(){Arrays.fill(before,0);lastAction=-1;}
    public void chosen(int action){
        if(action<0||action>=ACTIONS)return;
        lastAction=action;
        System.arraycopy(pooled,0,before,0,SUMMARY);
    }
    private float delta(float[] state,int action,int feature){
        int k=feature%SECTORS,base=(action*SUMMARY+feature)*HEAD;
        int off=feature-k;
        return clip(predictor[base]*state[feature]+
            predictor[base+1]*state[off+(k+SECTORS-1)%SECTORS]+
            predictor[base+2]*state[off+(k+1)%SECTORS]+
            predictor[base+3],-1.5f,1.5f);
    }
    private float value(float[] state,int action){
        float v=values[action*VALUE_WIDTH+SUMMARY];
        for(int j=0;j<SUMMARY;j++)v+=values[action*VALUE_WIDTH+j]*state[j]/SECTORS;
        return clip(v,-3,3);
    }
    public float confidence(){
        if(validations<32||!Float.isFinite(predictionError)||
           !Float.isFinite(unchangedError)||unchangedError<.018f||
           predictionError>=unchangedError*.96f)return 0;
        return clip((unchangedError-predictionError)/unchangedError,0,.45f);
    }
    public float choiceBias(int action){
        if(action<0||action>=ACTIONS)return 0f;
        return clip(value(pooled,action)*confidence()*.24f,-.12f,.12f);
    }
    public void noteDecision(float peak){if(peak>.008f)choiceInfluences++;}
    public void consequence(float[] after,int action,float reward,float externalMotion,
                            boolean learn,boolean heldout){
        if(!valid(after)||action<0||action>=ACTIONS)return;
        if(lastAction!=action){feed(after);return;}
        feed(after);
        float mse=0,baseMse=0;
        for(int j=0;j<SUMMARY;j++){
            float change=pooled[j]-before[j],error=change-delta(before,action,j);
            mse+=error*error;baseMse+=change*change;
            if(learn&&!heldout){
                int k=j%SECTORS,off=j-k,start=(action*SUMMARY+j)*HEAD;
                float g=PRED_LR*clip(error,-1,1);
                predictor[start]=clip(predictor[start]+g*before[j],-2.5f,2.5f);
                predictor[start+1]=clip(predictor[start+1]+
                    g*before[off+(k+SECTORS-1)%SECTORS],-2.5f,2.5f);
                predictor[start+2]=clip(predictor[start+2]+
                    g*before[off+(k+1)%SECTORS],-2.5f,2.5f);
                predictor[start+3]=clip(predictor[start+3]+g,-2.5f,2.5f);
            }
        }
        if(heldout){
            float rms=(float)Math.sqrt(mse/SUMMARY);
            float baseline=(float)Math.sqrt(baseMse/SUMMARY);
            float weight=validations==0?1:.08f;
            predictionError=validations==0?rms:(1-weight)*predictionError+weight*rms;
            unchangedError=validations==0?baseline:(1-weight)*unchangedError+weight*baseline;
            validations++;
        }
        if(learn&&!heldout&&Float.isFinite(reward)&&Float.isFinite(externalMotion)){
            float earned=clip(reward+.025f*clip(externalMotion,0,12),-2,2);
            float residual=clip(earned-value(before,action),-1.5f,1.5f);
            int start=action*VALUE_WIDTH;
            for(int j=0;j<SUMMARY;j++)
                values[start+j]=clip(values[start+j]+
                    VALUE_LR*residual*before[j]/SECTORS,-3,3);
            values[start+SUMMARY]=clip(values[start+SUMMARY]+VALUE_LR*residual,-3,3);
        }
        transitions++;
        lastAction=-1;
    }
    public void actualEnergy(float outcome){
        if(lastAction<0||!Float.isFinite(outcome)||Math.abs(outcome)<.01f)return;
        float earned=clip(outcome/30f,-1,1),rate=VALUE_LR*.55f;
        int start=lastAction*VALUE_WIDTH;
        for(int j=0;j<SUMMARY;j++)
            values[start+j]=clip(values[start+j]+rate*earned*before[j]/SECTORS,-3,3);
        values[start+SUMMARY]=clip(values[start+SUMMARY]+rate*earned,-3,3);
    }
    public String encode(){
        StringBuilder b=new StringBuilder("db1|").append(transitions).append('|')
            .append(validations).append('|').append(choiceInfluences).append('|')
            .append(Float.isFinite(predictionError)?predictionError:-1).append('|')
            .append(Float.isFinite(unchangedError)?unchangedError:-1);
        for(float f:predictor)b.append('|').append(f);
        for(float f:values)b.append('|').append(f);
        return b.toString();
    }
    public boolean restore(String data){
        if(data==null||data.length()>600000||!data.startsWith("db1|"))return false;
        String[] a=data.split("\\|",-1);
        if(a.length!=6+PARAMETERS)return false;
        try{
            long n=Long.parseLong(a[1]);int v=Integer.parseInt(a[2]),used=Integer.parseInt(a[3]);
            float error=Float.parseFloat(a[4]),baseline=Float.parseFloat(a[5]);
            if(n<0||v<0||v>n||used<0||!Float.isFinite(error)||!Float.isFinite(baseline)||
                error< -1||baseline< -1||error>5||baseline>5)return false;
            float[] p=new float[PRED],val=new float[VALUES];
            int ix=6;
            for(int i=0;i<p.length;i++){
                p[i]=Float.parseFloat(a[ix++]);
                if(!Float.isFinite(p[i])||Math.abs(p[i])>2.501f)return false;
            }
            for(int i=0;i<val.length;i++){
                val[i]=Float.parseFloat(a[ix++]);
                if(!Float.isFinite(val[i])||Math.abs(val[i])>3.001f)return false;
            }
            System.arraycopy(p,0,predictor,0,PRED);
            System.arraycopy(val,0,values,0,VALUES);
            transitions=n;validations=v;choiceInfluences=used;
            predictionError=error<0?Float.NaN:error;
            unchangedError=baseline<0?Float.NaN:baseline;
            resumeLife();
            return true;
        }catch(RuntimeException ex){return false;}
    }
}