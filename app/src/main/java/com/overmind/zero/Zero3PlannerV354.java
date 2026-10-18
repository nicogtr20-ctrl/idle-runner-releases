package com.overmind.zero;

import java.io.*;

/** Up to three hypothetical steps; only actual consecutive validation transitions score a horizon. */
public final class Zero3PlannerV354 {
    static final class Step {
        final float[] observation,hidden,physical;final float uncertainty;
        Step(float[] o,float[] h,float[] p,float u){observation=o;hidden=h;physical=p;uncertainty=u;}
    }
    interface Predictor {Step step(float[] observation,float[] hidden,float[] action);float[] meanAction(float[] hidden);}
    private final double[][] error=new double[2][12],baselineError=new double[2][12];
    private final long[] count=new long[2];
    private float[] observation,hidden,prediction,baseline;
    private int age,predictiveUnits;private long start,uses;
    void topology(int units){
        if(predictiveUnits!=0&&predictiveUnits!=units){clear();for(int i=0;i<2;i++){count[i]=0;java.util.Arrays.fill(error[i],0);java.util.Arrays.fill(baselineError[i],0);}}
        predictiveUnits=units;
    }
    boolean trusted(int horizon){
        int i=horizon-2;if(i<0||i>1||count[i]<64)return false;
        for(int h=0;h<3;h++)if(baselineError[i][h]<1e-7||error[i][h]>=baselineError[i][h]*.9)return false;
        return true;
    }
    int select(float[] observed,float[] latent,float[][] options,Predictor model,boolean prerequisite){
        if(!prerequisite||!trusted(2))return -1;
        int horizon=trusted(3)?3:2,best=0;float scoreBest=-Float.MAX_VALUE;
        for(int a=0;a<options.length;a++){
            float[] o=observed,z=latent,action=options[a];float score=0,discount=1;
            for(int step=0;step<horizon;step++){
                Step p=model.step(o,z,action);score+=discount*(p.physical[0]-p.physical[1]-p.physical[2]-2*p.uncertainty);
                discount*=.98f;o=p.observation;z=p.hidden;action=model.meanAction(z);
            }
            if(score>scoreBest){scoreBest=score;best=a;}
        }
        uses++;return best;
    }
    void begin(long decision,float[] observed,float[] latent,float[] actualAction,Predictor model,boolean heldout,boolean observe,float[] baselineTarget){
        if(observe||!heldout){clear();return;}
        if(age>0){
            if(decision!=start+age){clear();return;}
            Step s=model.step(observation,hidden,actualAction);observation=s.observation;hidden=s.hidden;prediction=s.physical;age++;
        }else if(decision%15==12){
            Step s=model.step(observed,latent,actualAction);observation=s.observation;hidden=s.hidden;prediction=s.physical;
            baseline=baselineTarget.clone();age=1;start=decision;
        }
    }
    void complete(float[] actual,boolean heldout,boolean observe){
        if(age==0)return;
        if(!heldout||observe){clear();return;}
        if(age>=2){int i=age-2;count[i]++;
            for(int h=0;h<12;h++){double d=actual[h]-prediction[h],b=actual[h]-baseline[h];error[i][h]+=d*d;baselineError[i][h]+=b*b;}
        }
        if(age==3)clear();
    }
    private void clear(){age=0;observation=hidden=prediction=baseline=null;}
    public static final class Snapshot {
        public final long[] samples;public final boolean[] reliable;public final float[][] mse,baseline;public final long uses;
        Snapshot(Zero3PlannerV354 p){samples=p.count.clone();reliable=new boolean[]{p.trusted(2),p.trusted(3)};mse=new float[2][12];baseline=new float[2][12];uses=p.uses;
            for(int i=0;i<2;i++)for(int h=0;h<12;h++){mse[i][h]=p.count[i]==0?Float.NaN:(float)(p.error[i][h]/p.count[i]);baseline[i][h]=p.count[i]==0?Float.NaN:(float)(p.baselineError[i][h]/p.count[i]);}}
    }
    public Snapshot snapshot(){return new Snapshot(this);}
    byte[] encode()throws IOException{
        ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream d=new DataOutputStream(b);d.writeInt(354);d.writeLong(uses);d.writeInt(predictiveUnits);
        for(int i=0;i<2;i++){d.writeLong(count[i]);for(int h=0;h<12;h++){d.writeDouble(error[i][h]);d.writeDouble(baselineError[i][h]);}}
        d.writeInt(age);d.writeLong(start);
        if(age>0)for(float[] a:new float[][]{observation,hidden,prediction,baseline})for(float v:a)d.writeFloat(v);
        d.flush();return b.toByteArray();
    }
    static Zero3PlannerV354 decode(byte[] raw)throws IOException{
        if(raw==null||raw.length>4096)throw new IOException("V354 planner size");DataInputStream d=new DataInputStream(new ByteArrayInputStream(raw));if(d.readInt()!=354)throw new IOException("V354 planner schema");
        Zero3PlannerV354 p=new Zero3PlannerV354();p.uses=d.readLong();if(p.uses<0)throw new IOException("V354 planner uses");p.predictiveUnits=d.readInt();if(p.predictiveUnits<0||p.predictiveUnits>60)throw new IOException("V354 planner topology");
        for(int i=0;i<2;i++){p.count[i]=d.readLong();if(p.count[i]<0)throw new IOException("V354 planner samples");for(int h=0;h<12;h++){
            p.error[i][h]=d.readDouble();p.baselineError[i][h]=d.readDouble();if(!Double.isFinite(p.error[i][h])||!Double.isFinite(p.baselineError[i][h])||p.error[i][h]<0||p.baselineError[i][h]<0)throw new IOException("V354 planner evidence");}}
        p.age=d.readInt();p.start=d.readLong();if(p.age<0||p.age>2||p.start<0)throw new IOException("V354 planner cursor");
        if(p.age>0){p.observation=new float[194];p.hidden=new float[64];p.prediction=new float[12];p.baseline=new float[12];
            for(float[] a:new float[][]{p.observation,p.hidden,p.prediction,p.baseline})for(int i=0;i<a.length;i++){a[i]=d.readFloat();if(!Float.isFinite(a[i]))throw new IOException("V354 planner nonfinite");}}
        if(d.available()!=0)throw new IOException("V354 planner trailing bytes");return p;
    }
}
