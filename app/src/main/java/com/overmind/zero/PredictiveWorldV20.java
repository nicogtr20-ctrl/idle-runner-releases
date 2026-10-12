package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * ZERO 2.0 / GATE A: lightweight recurrent action-conditioned sensory world
 * model. Learns exclusively from ZERO's own 26 real observations and executed
 * action. No world object labels, location, physics shortcut, reward or motor
 * commands. Production use is SHADOW ONLY while independently validated.
 *
 * 20 sensory channels = genuine obstacle rays and spectral samples 6..25.
 * Each learned output predicts DELTA, not absolute input. Exponentially
 * smoothed prior lived observations provide a low-dimensional temporal state.
 */
public final class PredictiveWorldV20 {
    public static final String KEY="predictive_world_v20";
    public static final int S=SensorimotorMind.S,A=SensorimotorMind.ACTIONS;
    public static final int OFFSET=6,D=S-OFFSET,HEAD=5,WEIGHTS=A*D*HEAD;
    private static final float LR=.010f,CONTEXT_ALPHA=.16f;
    /** Pure observed sensor dynamics; absence of change is the default. */
    private final float[] changeRate=new float[A*D];
    private final int[] actionSamples=new int[A];
    private final float[] weights=new float[WEIGHTS];
    private final float[] trace=new float[D],before=new float[D],pendingContext=new float[D];
    private int pending=-1,validationCount=0;
    private long transitions=0,trainCount=0,invalidSamples=0;
    private long nonChangeReadouts=0,selectiveReadouts=0;
    private float heldoutError=Float.NaN,unchangedError=Float.NaN,
                  lastNovelty=0f,lastForecastError=Float.NaN;
    private static float clip(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }
    private static boolean valid(float[] s){
        if(s==null||s.length!=S)return false;
        for(float v:s)if(!Float.isFinite(v)||Math.abs(v)>1.6f)return false;
        return true;
    }
    public long transitions(){return transitions;}
    public long trained(){return trainCount;}
    public int validations(){return validationCount;}
    public long invalidSamples(){return invalidSamples;}
    public float heldoutError(){return heldoutError;}
    public float baselineError(){return unchangedError;}
    public float latestNovelty(){return lastNovelty;}
    public float forecastError(){return lastForecastError;}
    public long nonChangeReadouts(){return nonChangeReadouts;}
    public long selectiveReadouts(){return selectiveReadouts;}
    public int evidenceForAction(int action){return action>=0&&action<A?actionSamples[action]:0;}
    /** Train-only change propensity, never supplied by game/world semantics. */
    private float gate(int action,int output){
        float occurrences=clip((actionSamples[action]-5f)/32f,0f,1f);
        float frequency=clip((changeRate[action*D+output]-.06f)*1.12f,0f,1f);
        return occurrences*frequency;
    }
    /** Reported as evidence, never converted into steering or reward. */
    public float confidence(){
        if(validationCount<40||!Float.isFinite(heldoutError)||
            !Float.isFinite(unchangedError)||unchangedError<.025f||
            heldoutError>=unchangedError*.92f)return 0;
        return clip((unchangedError-heldoutError)/unchangedError,0,.6f);
    }
    public void resetLife(){
        Arrays.fill(trace,0f);Arrays.fill(before,0f);
        Arrays.fill(pendingContext,0f);
        pending=-1;lastNovelty=0;lastForecastError=Float.NaN;
    }
    /** On resume never invent a completed decision interval. */
    public void resumeLife(){resetLife();}
    public void chosen(int action,float[] observation){
        if(!valid(observation)||action<0||action>=A){
            pending=-1;invalidSamples++;return;
        }
        System.arraycopy(observation,OFFSET,before,0,D);
        System.arraycopy(trace,0,pendingContext,0,D);
        pending=action;
    }
    private float delta(int action,int output,float[] obs,float[] context){
        int prev=(output+D-1)%D,next=(output+1)%D;
        int off=(action*D+output)*HEAD;
        float x=weights[off]*obs[output]+weights[off+1]*obs[prev]+
            weights[off+2]*obs[next]+weights[off+3]*context[output]+
            weights[off+4];
        return clip(x,-1f,1f);
    }
    /** Counterfactual read-out; NEVER counts as an executed action. */
    public float[] predictedObservation(int action,float[] sensed){
        if(!valid(sensed)||action<0||action>=A)return null;
        float[] result=sensed.clone(),instant=new float[D];
        System.arraycopy(sensed,OFFSET,instant,0,D);
        for(int j=0;j<D;j++)
            result[OFFSET+j]=clip(instant[j]+gate(action,j)*
                delta(action,j,instant,trace),-1.6f,1.6f);
        // An unknown action/channel remains unchanged until repeatedly
        // witnessed consequences earn the right to predict a difference.
        for(int j=0;j<D;j++){
            if(gate(action,j)<.025f)nonChangeReadouts++;
            else selectiveReadouts++;
        }
        return result;
    }
    public void consequence(float[] observation,int action,boolean train,boolean heldout){
        if(!valid(observation)||action<0||action>=A||pending!=action){
            pending=-1;invalidSamples++;return;
        }
        float sq=0,unchanged=0;
        // Predict all features from the SAME prior representation before
        // learning any weight. Updates are deferred until errors are fixed.
        float[] residual=new float[D];
        for(int j=0;j<D;j++){
            float change=observation[OFFSET+j]-before[j];
            float prediction=gate(action,j)*delta(action,j,before,pendingContext);
            residual[j]=change-prediction;
            sq+=residual[j]*residual[j];
            unchanged+=change*change;
        }
        float e=(float)Math.sqrt(sq/D),base=(float)Math.sqrt(unchanged/D);
        lastForecastError=e;
        lastNovelty=clip(e,0,1);
        if(heldout){
            validationCount++;
            float alpha=validationCount==1?1f:.055f;
            heldoutError=validationCount==1?e:
                heldoutError*(1-alpha)+e*alpha;
            unchangedError=validationCount==1?base:
                unchangedError*(1-alpha)+base*alpha;
        }else if(train){
            for(int j=0;j<D;j++){
                int prev=(j+D-1)%D,next=(j+1)%D;
                int off=(action*D+j)*HEAD;
                float changed=Math.abs(observation[OFFSET+j]-before[j]);
                float event=clip((changed-.025f)/.075f,0f,1f);
                int at=action*D+j;
                // Separate test-life observations NEVER update propensity.
                changeRate[at]=clip(changeRate[at]*.965f+
                                    event*.035f,0f,1f);
                // The predictor can only move gradually against a strong
                // no-change prior. Weight decay prevents repeated idle
                // motion from accumulating a phantom sensory transition.
                float g=clip(LR*residual[j]*(.25f+.75f*gate(action,j)),
                             -.018f,.018f);
                weights[off]=clip(weights[off]*.9998f+g*before[j],-2,2);
                weights[off+1]=clip(weights[off+1]*.9998f+g*before[prev],-2,2);
                weights[off+2]=clip(weights[off+2]*.9998f+g*before[next],-2,2);
                weights[off+3]=clip(weights[off+3]*.9998f+g*pendingContext[j],-2,2);
                weights[off+4]=clip(weights[off+4]*.9998f+g,-2,2);
            }
            if(actionSamples[action]<Integer.MAX_VALUE)actionSamples[action]++;
            trainCount++;
        }
        // Temporal context is only from an ACTUAL new observation, never
        // from predicted trajectories. It is transient within a real life.
        for(int j=0;j<D;j++){
            trace[j]=clip(trace[j]*(1-CONTEXT_ALPHA)+
                observation[OFFSET+j]*CONTEXT_ALPHA,-1.6f,1.6f);
        }
        transitions++;pending=-1;
    }
    /** Retains previous v20 key, upgrades payload atomically to pw21. */
    public String encode(){
        StringBuilder b=new StringBuilder("pw21|").append(transitions).append('|')
            .append(trainCount).append('|').append(validationCount).append('|')
            .append(invalidSamples).append('|')
            .append(Float.isFinite(heldoutError)?heldoutError:-1f).append('|')
            .append(Float.isFinite(unchangedError)?unchangedError:-1f);
        for(float w:weights)b.append('|').append(w);
        for(int n:actionSamples)b.append('|').append(n);
        for(float rate:changeRate)b.append('|').append(rate);
        return b.toString();
    }
    /** All-or-nothing; legacy pw20 weights restored with zero speculative
     * readiness until sufficient NEW actual train observations are seen. */
    public boolean restore(String raw){
        if(raw==null||raw.length()>160000)return false;
        boolean old=raw.startsWith("pw20|"),next=raw.startsWith("pw21|");
        if(!old&&!next)return false;
        String[] s=raw.split("\\|",-1);
        if(s.length!=(old?7+WEIGHTS:7+WEIGHTS+A+A*D))return false;
        try{
            long t=Long.parseLong(s[1]),trained=Long.parseLong(s[2]),
                invalid=Long.parseLong(s[4]);
            int val=Integer.parseInt(s[3]);
            float err=Float.parseFloat(s[5]),base=Float.parseFloat(s[6]);
            if(t<0||trained<0||trained>t||val<0||val>t||
                invalid<0||!Float.isFinite(err)||!Float.isFinite(base)||
                err< -1||err>3||base< -1||base>3)return false;
            float[] candidate=new float[WEIGHTS],activity=new float[A*D];
            int[] counts=new int[A];
            for(int i=0;i<WEIGHTS;i++){
                candidate[i]=Float.parseFloat(s[i+7]);
                if(!Float.isFinite(candidate[i])||Math.abs(candidate[i])>2.001f)
                    return false;
            }
            if(next){
                for(int i=0;i<A;i++){
                    counts[i]=Integer.parseInt(s[7+WEIGHTS+i]);
                    if(counts[i]<0||counts[i]>trained)return false;
                }
                for(int i=0;i<A*D;i++){
                    activity[i]=Float.parseFloat(s[7+WEIGHTS+A+i]);
                    if(!Float.isFinite(activity[i])||activity[i]<0||
                        activity[i]>1.0001f)return false;
                }
            }
            System.arraycopy(candidate,0,weights,0,WEIGHTS);
            System.arraycopy(activity,0,changeRate,0,A*D);
            System.arraycopy(counts,0,actionSamples,0,A);
            transitions=t;trainCount=trained;validationCount=val;
            invalidSamples=invalid;
            heldoutError=err<0?Float.NaN:err;
            unchangedError=base<0?Float.NaN:base;
            nonChangeReadouts=selectiveReadouts=0;
            resetLife();
            return true;
        }catch(RuntimeException malformed){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
            "ZERO 2.0 selective change: lived=%d train=%d heldout=%d e=%.5f baseline=%.5f confidence=%.3f (SHADOW ONLY)",
            transitions,trainCount,validationCount,
            heldoutError,unchangedError,confidence());
    }
}
