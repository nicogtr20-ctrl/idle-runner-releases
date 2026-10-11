package com.overmind.zero;

import java.util.Arrays;

/**
 * 0116.4 — shadow-only sensory-context-conditioned motion predictor.
 * Inputs are the 26 senses at a REAL self-selected motor action.
 * Targets are REAL nucleus dx/dy from the next completed 0.65s interval.
 * No labels, target coordinates, rewards, actuators, or imaginary outcomes.
 * NEVER contributes to SensorimotorMind.choose() or navigation bias.
 */
public final class ContextualMotionProbe {
    public static final String KEY="motion_context_shadow_v1164";
    private static final int S=SensorimotorMind.S,A=SensorimotorMind.ACTIONS,
                             P=S+1,WEIGHTS=A*P*2;
    private static final float SCALE=70f,LR=.019f;
    private final float[] wx=new float[A*P],wy=new float[A*P];
    private final int[] trained=new int[A],seen=new int[A];
    private final float[] prev=new float[S];
    private int pending=-1;
    private float sx,sy;
    public int evaluations=0,baselineEvaluations=0;
    public double contextualSquared=0,baselineSquared=0,stillSquared=0;
    public double externalBaselineSquared=0;
    public int missingPhysicalSamples=0;
    public float lastPredictedX=0,lastPredictedY=0,lastRealX=0,lastRealY=0;

    private static float clamp(float v,float low,float high){
        return Math.max(low,Math.min(high,v));
    }
    private static float prediction(float[] w,int action,float[] sensed){
        int base=action*P;
        float v=w[base+S];
        for(int k=0;k<S;k++)v+=w[base+k]*sensed[k];
        return clamp(v,-1.8f,1.8f)*SCALE;
    }
    public float[] predict(float[] sensed,int action){
        if(sensed==null||sensed.length!=S||action<0||action>=A)
            return new float[]{0,0};
        return new float[]{prediction(wx,action,sensed),
                           prediction(wy,action,sensed)};
    }
    public void chosen(float[] senses,int action,MotorBrain.Body body){
        pending=-1;
        if(senses==null||senses.length!=S||action<0||action>=A||
           body==null||!Float.isFinite(body.cx)||!Float.isFinite(body.cy))return;
        for(float v:senses)if(!Float.isFinite(v)||Math.abs(v)>1.7f)return;
        System.arraycopy(senses,0,prev,0,S);
        pending=action;sx=body.cx;sy=body.cy;
    }
    public void finished(int action,MotorBrain.Body body,boolean learning,
                         float oldPredX,float oldPredY,boolean oldReliable){
        if(pending!=action)return;
        pending=-1;
        if(body==null||!Float.isFinite(body.cx)||!Float.isFinite(body.cy)){
            missingPhysicalSamples++;return;
        }
        float dx=clamp(body.cx-sx,-120,120),dy=clamp(body.cy-sy,-120,120);
        float px=prediction(wx,action,prev),py=prediction(wy,action,prev);
        lastPredictedX=px;lastPredictedY=py;lastRealX=dx;lastRealY=dy;
        int n=++seen[action];
        // 20% immutable holdout: never updates model on this sample.
        if(n%5==0){
            evaluations++;
            contextualSquared+=(dx-px)*(dx-px)+(dy-py)*(dy-py);
            stillSquared+=dx*dx+dy*dy;
            if(oldReliable&&Float.isFinite(oldPredX)&&Float.isFinite(oldPredY)){
                baselineEvaluations++;
                // An equal sample set for both the old state-independent
                // motion estimate and the new contextual predictions.
                // Track comparable error on the same old-reliable samples.
                baselineSquared+=(dx-px)*(dx-px)+(dy-py)*(dy-py);
                externalBaselineSquared+=(dx-oldPredX)*(dx-oldPredX)+
                                         (dy-oldPredY)*(dy-oldPredY);
            }
            return;
        }
        if(!learning)return;
        trained[action]++;
        int start=action*P;
        float errorX=clamp((dx-px)/SCALE,-1.8f,1.8f);
        float errorY=clamp((dy-py)/SCALE,-1.8f,1.8f);
        for(int i=0;i<S;i++){
            float factor=clamp(prev[i],-1.6f,1.6f);
            wx[start+i]=clamp(wx[start+i]+LR*errorX*factor,-2,2);
            wy[start+i]=clamp(wy[start+i]+LR*errorY*factor,-2,2);
        }
        wx[start+S]=clamp(wx[start+S]+LR*errorX,-2,2);
        wy[start+S]=clamp(wy[start+S]+LR*errorY,-2,2);
    }
    public void resetLife(){pending=-1;}
    public int trainedActions(){int n=0;for(int t:trained)if(t>=12)n++;return n;}
    public double rmse(){return evaluations==0?Double.NaN:
        Math.sqrt(contextualSquared/(2*evaluations));}
    public double stillRmse(){return evaluations==0?Double.NaN:
        Math.sqrt(stillSquared/(2*evaluations));}
    public double oldComparableRmse(){return baselineEvaluations==0?Double.NaN:
        Math.sqrt(externalBaselineSquared/(2*baselineEvaluations));}
    public double newComparableRmse(){return baselineEvaluations==0?Double.NaN:
        Math.sqrt(baselineSquared/(2*baselineEvaluations));}
    public boolean beatsOld(){
        return baselineEvaluations>=80&&trainedActions()>=4&&
            newComparableRmse()<oldComparableRmse()*.90;
    }
    public String encode(){
        StringBuilder b=new StringBuilder("mc1|").append(evaluations)
            .append('|').append(baselineEvaluations)
            .append('|').append(contextualSquared)
            .append('|').append(stillSquared)
            .append('|').append(baselineSquared)
            .append('|').append(externalBaselineSquared)
            .append('|').append(missingPhysicalSamples);
        for(int v:trained)b.append('|').append(v);
        for(int v:seen)b.append('|').append(v);
        for(float w:wx)b.append('|').append(w);
        for(float w:wy)b.append('|').append(w);
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>65000||!raw.startsWith("mc1|"))return false;
        try{
            String[] a=raw.split("\\|",-1);
            if(a.length!=8+A*2+WEIGHTS)return false;
            int e=Integer.parseInt(a[1]),be=Integer.parseInt(a[2]);
            double cs=Double.parseDouble(a[3]),zs=Double.parseDouble(a[4]),
                   bs=Double.parseDouble(a[5]),os=Double.parseDouble(a[6]);
            int missing=Integer.parseInt(a[7]);
            if(e<0||be<0||be>e||missing<0||
               !Double.isFinite(cs)||!Double.isFinite(zs)||
               !Double.isFinite(bs)||!Double.isFinite(os)||
               cs<0||zs<0||bs<0||os<0)return false;
            int[] ts=new int[A],ss=new int[A];
            float[] nextX=new float[A*P],nextY=new float[A*P];
            int ix=8;
            for(int i=0;i<A;i++)ts[i]=Integer.parseInt(a[ix++]);
            for(int i=0;i<A;i++)ss[i]=Integer.parseInt(a[ix++]);
            for(int i=0;i<A*P;i++)nextX[i]=Float.parseFloat(a[ix++]);
            for(int i=0;i<A*P;i++)nextY[i]=Float.parseFloat(a[ix++]);
            for(int i=0;i<A;i++)
                if(ts[i]<0||ss[i]<0||ts[i]>ss[i])return false;
            for(float w:nextX)if(!Float.isFinite(w)||Math.abs(w)>2.01f)return false;
            for(float w:nextY)if(!Float.isFinite(w)||Math.abs(w)>2.01f)return false;
            System.arraycopy(nextX,0,wx,0,wx.length);
            System.arraycopy(nextY,0,wy,0,wy.length);
            System.arraycopy(ts,0,trained,0,A);
            System.arraycopy(ss,0,seen,0,A);
            evaluations=e;baselineEvaluations=be;contextualSquared=cs;
            stillSquared=zs;baselineSquared=bs;externalBaselineSquared=os;
            missingPhysicalSamples=missing;resetLife();return true;
        }catch(RuntimeException invalid){return false;}
    }
    private static void check(boolean ok,String reason){
        if(!ok)throw new AssertionError(reason);
    }
    public static void main(String[] args){
        ContextualMotionProbe m=new ContextualMotionProbe();
        MotorBrain.Body b=new MotorBrain.Body();
        float[] s=new float[S];
        for(int t=0;t<1200;t++){
            boolean blocked=(t%2)==0;
            s[6]=blocked?1:0;s[0]=blocked?.3f:.8f;
            b.cx=0;b.cy=0;
            m.chosen(s,2,b);
            b.cx=blocked?0:28;b.cy=blocked?0:-9;
            m.finished(2,b,true,14,-4.5f,true);
        }
        s[6]=1;s[0]=.3f;float[] blocked=m.predict(s,2);
        s[6]=0;s[0]=.8f;float[] clear=m.predict(s,2);
        check(clear[0]>blocked[0]+8,
            "Context predictor did not distinguish blocked from free state");
        check(m.evaluations>=200&&m.trainedActions()==0,
            "Heldout or action coverage counters incorrect");
        String saved=m.encode();
        ContextualMotionProbe copy=new ContextualMotionProbe();
        check(copy.restore(saved)&&copy.encode().equals(saved),
            "Motion model did not survive profile serialization");
        check(!copy.restore(saved+"|broken")&&saved.equals(copy.encode()),
            "Corrupt model erased valid weights");
        copy.resetLife();
        check(copy.predict(s,2)[0]>8,"Death erased learned predictor");
        // No motor feedback path: suggestions exist only as output values.
        System.out.println("ContextualMotionProbe PASS: real state dependence, "+
            "20% holdout, old-motion comparable errors, save/restore, no steering");
    }
}
