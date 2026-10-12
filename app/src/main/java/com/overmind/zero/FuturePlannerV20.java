package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * ZERO 2.0 gate C research. Pessimistic imagination using only the VALIDATED
 * action-conditioned learned world predictor and learned actor values.
 *
 * Horizon 5 > old planner depth 3; at most 16 * 2^4 = 256 leaves.
 * Reads optional cross-life skill candidates, but never executes them.
 * No target coordinates, object IDs, fixed plans, motor biases or reward.
 */
public final class FuturePlannerV20 {
    public static final String KEY="future_planner_v20";
    public static final int ACTIONS=SensorimotorMind.ACTIONS,DEPTH=5,BEAM=2;
    public interface Value {
        float value(float[] sensed,int action);
    }
    private long checks=0,eligible=0,abstentions=0,optionChecks=0;
    private int suggested=-1;
    private float lastConfidence=0,lastMargin=0;
    private static float bound(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }
    private static float safeValue(Value value,float[] senses,int a){
        float v=value.value(senses,a);
        return Float.isFinite(v)?bound(v,-8,8):-8;
    }
    private static int[] topTwo(Value v,float[] senses){
        int first=0,second=1;
        if(safeValue(v,senses,second)>safeValue(v,senses,first)){
            int t=first;first=second;second=t;
        }
        for(int a=2;a<ACTIONS;a++){
            float x=safeValue(v,senses,a);
            if(x>safeValue(v,senses,first)){
                second=first;first=a;
            }else if(x>safeValue(v,senses,second)){second=a;}
        }
        return new int[]{first,second};
    }
    private static boolean valid(float[] v){
        if(v==null||v.length!=SensorimotorMind.S)return false;
        for(float x:v)if(!Float.isFinite(x)||Math.abs(x)>1.6f)return false;
        return true;
    }
    private static float rollout(PredictiveWorldV20 world,Value value,
                                 float[] state,int depth,float discount,float uncertainty){
        if(depth>=DEPTH)return 0f;
        int[] next=topTwo(value,state);
        float best=-Float.MAX_VALUE;
        for(int a:next){
            float[] after=world.predictedObservation(a,state);
            if(!valid(after))return Float.NaN;
            float reward=safeValue(value,after,a);
            // Increasing model uncertainty makes speculative futures worse,
            // rather than overconfidently encouraging endless loops.
            float step=reward-uncertainty*(depth+1)*(depth+1);
            float future=rollout(world,value,after,depth+1,discount,uncertainty);
            if(!Float.isFinite(future))return Float.NaN;
            best=Math.max(best,step+discount*future);
        }
        return best;
    }
    /** Returns no motor command. Only metrics survive the call. */
    public void evaluate(float[] actualSenses,PredictiveWorldV20 world,
                         Value values,TransferSkillsV20 options){
        checks++;suggested=-1;lastConfidence=0;lastMargin=0;
        if(!valid(actualSenses)||world==null||values==null||
            world.validations()<60||world.confidence()<.10f||
            !Float.isFinite(world.heldoutError())||
            world.heldoutError()>.18f){
            abstentions++;return;
        }
        float trust=world.confidence();
        float error=world.heldoutError();
        float[] scores=new float[ACTIONS];
        float max=-Float.MAX_VALUE,nextMax=-Float.MAX_VALUE;
        int winner=-1;
        try{
            for(int a=0;a<ACTIONS;a++){
                float[] after=world.predictedObservation(a,actualSenses);
                if(!valid(after)){abstentions++;return;}
                float future=rollout(world,values,after,1,.67f,
                                     error*(1f-trust)*1.6f);
                if(!Float.isFinite(future)){abstentions++;return;}
                // Baseline Q is from actually lived original policy. Predicted
                // future observations NEVER earn a real experience.
                scores[a]=safeValue(values,actualSenses,a)+.67f*future;
                if(scores[a]>max){
                    nextMax=max;max=scores[a];winner=a;
                }else if(scores[a]>nextMax)nextMax=scores[a];
            }
            if(options!=null&&options.observeSuggestion(actualSenses)!=null)
                optionChecks++; // observation only; no unjustified motor bias.
        }catch(RuntimeException invalidModel){
            abstentions++;return;
        }
        if(winner<0){abstentions++;return;}
        eligible++;
        suggested=winner;lastConfidence=trust;
        lastMargin=bound(max-nextMax,0,100);
    }
    public long checks(){return checks;}
    public long eligible(){return eligible;}
    public long abstentions(){return abstentions;}
    public int shadowSuggestion(){return suggested;}
    public float latestConfidence(){return lastConfidence;}
    public float latestMargin(){return lastMargin;}
    public long optionChecks(){return optionChecks;}
    public String encode(){
        return String.format(Locale.US,"fp20|%d|%d|%d|%d",
                            checks,eligible,abstentions,optionChecks);
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>140||!raw.startsWith("fp20|"))return false;
        try{
            String[] p=raw.split("\\|",-1);
            if(p.length!=5)return false;
            long c=Long.parseLong(p[1]),e=Long.parseLong(p[2]),
                a=Long.parseLong(p[3]),o=Long.parseLong(p[4]);
            if(c<0||e<0||a<0||o<0||e>c||a>c||o>e||
                e+a!=c)return false;
            checks=c;eligible=e;abstentions=a;optionChecks=o;
            suggested=-1;lastConfidence=lastMargin=0;
            return true;
        }catch(RuntimeException invalid){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
            "ZERO20 C shadow: %d valid / %d attempts; option evidence=%d, confidence=%.3f, NO motor effect",
            eligible,checks,optionChecks,lastConfidence);
    }
}
