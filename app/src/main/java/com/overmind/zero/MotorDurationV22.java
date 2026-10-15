package com.overmind.zero;

import java.util.Locale;
import java.util.Random;

/**
 * ZERO 160: learn how long a self-selected 2.2 motor action is worth executing.
 * This is an action-duration controller, NOT a second action selector.
 * It never receives object identity, destination, target direction or colour.
 * Only real body motion, paid muscle energy and actual contact outcomes score
 * the temporal motor experiment. A separate heldout fifth never trains.
 */
public final class MotorDurationV22 {
    public static final String KEY="motor_duration_v160";
    public static final int ACTIONS=RecurrentCoreV22.A, OPTIONS=3;
    private static final int[] TICKS={1,2,4};
    private static final int CELLS=ACTIONS*OPTIONS;
    private final int[] trained=new int[CELLS],verified=new int[CELLS];
    private final float[] trainedMean=new float[CELLS],verifiedMean=new float[CELLS];
    private final Random random=new Random(0x16022b0dL);
    private int selected=-1,option=-1,remaining,intervals,started;
    private boolean auditOnly;
    private float startX,startY,lastX,lastY,gross,gain,damage,cost,effect;
    private boolean hasPosition;
    private long choices,continuesCount,interruptsCount,episodes;
    private static float clip(float v,float low,float high){
        return Math.max(low,Math.min(high,v));
    }
    /** Keep learned estimates across deaths; censor the interrupted burst. */
    public void resetLife(){selected=-1;option=-1;remaining=intervals=0;hasPosition=false;}
    public void resumeLife(){resetLife();}
    public void interval(int performed,float x,float y,float energy,
                         float harm,float muscleCost,float movedObject){
        if(performed!=selected||option<0)return;
        intervals++;
        if(Float.isFinite(x)&&Float.isFinite(y)){
            if(hasPosition)gross+=Math.min(140f,(float)Math.hypot(x-lastX,y-lastY));
            else{startX=x;startY=y;hasPosition=true;}
            lastX=x;lastY=y;
        }
        if(Float.isFinite(energy))gain+=clip(energy,-100f,100f);
        if(Float.isFinite(harm))damage+=clip(harm,0,100f);
        if(Float.isFinite(muscleCost))cost+=clip(muscleCost,0,100f);
        if(Float.isFinite(movedObject))effect+=clip(movedObject,0,100f);
    }
    private void finish(){
        if(selected<0||option<0||intervals<=0)return;
        int at=selected*OPTIONS+option;
        float net=hasPosition?(float)Math.hypot(lastX-startX,lastY-startY):0f;
        // Measured motor skill quality ONLY; no motion reward enters the 2.2
        // value learner, and no attraction/aversion is hardcoded by signature.
        float speed=clip(net/(12f*intervals),0f,1f);
        float coherence=gross>1f?clip(net/gross,0f,1f):0f;
        float score=clip(.25f*speed*coherence+
            .55f*clip(Math.max(0f,gain)/(22f*intervals),0f,2f)+
            .12f*clip(effect/(8f*intervals),0f,1f)-
            .60f*clip(Math.max(0f,-gain)+damage,0f,35f)/35f-
            .15f*clip(cost/(2f*intervals),0f,1f),-1.5f,1.5f);
        int count=auditOnly?verified[at]:trained[at];
        float old=auditOnly?verifiedMean[at]:trainedMean[at];
        float mean=old+(score-old)/(Math.min(80,count)+1f);
        if(auditOnly){verified[at]++;verifiedMean[at]=mean;}
        else{trained[at]++;trainedMean[at]=mean;}
        episodes++;
    }
    private int chooseOption(int action){
        // Short, medium and long interventions are experimentally sampled,
        // never a scripted walking rhythm, heading or compelled food chase.
        if(random.nextFloat()<.22f)return random.nextInt(OPTIONS);
        int best=-1;
        float value=-Float.MAX_VALUE;
        for(int k=0;k<OPTIONS;k++){
            int at=action*OPTIONS+k;
            // Physical learning + genuinely untouched validation are BOTH
            // required before duration predictions can control execution.
            if(trained[at]<16||verified[at]<6)continue;
            float validated=verifiedMean[at],inSample=trainedMean[at];
            if(Math.abs(validated-inSample)>.30f)continue;
            float estimate=.5f*(validated+inSample);
            if(estimate>value){value=estimate;best=k;}
        }
        return best>=0?best:random.nextInt(OPTIONS);
    }
    /**
     * Retain the brain's OWN previous action only while its selected motor
     * interval is still running. New high-confidence policy changes, random
     * exploration, danger contacts and near-death reserve ALWAYS interrupt.
     */
    public int select(int proposal,int greedy,float[] scores,boolean exploratory,
                      float reserve,float harmfulContact,float x,float y){
        if(proposal<0||proposal>=ACTIONS||greedy<0||greedy>=ACTIONS)
            return proposal;
        boolean safe=Float.isFinite(reserve)&&reserve>.16f&&
                     !exploratory&&harmfulContact<=0f;
        boolean challenger=false;
        if(scores!=null&&scores.length==ACTIONS&&selected>=0)
            challenger=Float.isFinite(scores[greedy])&&Float.isFinite(scores[selected])&&
                scores[greedy]-scores[selected]>.24f;
        if(selected>=0&&remaining>0&&safe&&!challenger){
            remaining--;
            continuesCount++;
            return selected;
        }
        if(selected>=0&&remaining>0)interruptsCount++;
        finish();
        selected=proposal;
        option=chooseOption(proposal);
        remaining=TICKS[option]-1;
        intervals=0;
        gross=gain=damage=cost=effect=0f;
        startX=lastX=x;startY=lastY=y;
        hasPosition=Float.isFinite(x)&&Float.isFinite(y);
        auditOnly=(++started%5)==0;
        choices++;
        return proposal;
    }
    public long starts(){return choices;}
    public long continuations(){return continuesCount;}
    public long interruptions(){return interruptsCount;}
    public long evaluated(){return episodes;}
    public int trained(int action,int duration){
        return trained[action*OPTIONS+duration];
    }
    public int heldout(int action,int duration){
        return verified[action*OPTIONS+duration];
    }
    public String encode(){
        StringBuilder out=new StringBuilder("md160|").append(choices)
            .append('|').append(continuesCount).append('|').append(interruptsCount)
            .append('|').append(episodes);
        for(int i=0;i<CELLS;i++)out.append('|').append(trained[i]).append(',')
            .append(verified[i]).append(',')
            .append(String.format(Locale.US,"%.7f",trainedMean[i])).append(',')
            .append(String.format(Locale.US,"%.7f",verifiedMean[i]));
        return out.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>10000||!raw.startsWith("md160|"))return false;
        try{
            String[] s=raw.split("\\|",-1);
            if(s.length!=5+CELLS)return false;
            long a=Long.parseLong(s[1]),b=Long.parseLong(s[2]),
                 c=Long.parseLong(s[3]),d=Long.parseLong(s[4]);
            if(a<0||b<0||c<0||d<0||a>1000000000L||
               b>1000000000L||c>1000000000L||d>1000000000L)return false;
            int[] t=new int[CELLS],v=new int[CELLS];
            float[] tm=new float[CELLS],vm=new float[CELLS];
            for(int i=0;i<CELLS;i++){
                String[] row=s[i+5].split(",",-1);
                if(row.length!=4)return false;
                t[i]=Integer.parseInt(row[0]);v[i]=Integer.parseInt(row[1]);
                tm[i]=Float.parseFloat(row[2]);vm[i]=Float.parseFloat(row[3]);
                if(t[i]<0||v[i]<0||t[i]>1000000000||v[i]>1000000000||
                   !Float.isFinite(tm[i])||!Float.isFinite(vm[i])||
                   Math.abs(tm[i])>1.501f||Math.abs(vm[i])>1.501f)return false;
            }
            System.arraycopy(t,0,trained,0,CELLS);
            System.arraycopy(v,0,verified,0,CELLS);
            System.arraycopy(tm,0,trainedMean,0,CELLS);
            System.arraycopy(vm,0,verifiedMean,0,CELLS);
            choices=a;continuesCount=b;interruptsCount=c;episodes=d;
            resetLife();return true;
        }catch(RuntimeException bad){return false;}
    }
}
