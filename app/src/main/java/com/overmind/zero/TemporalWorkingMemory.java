package com.overmind.zero;

import java.util.Arrays;

/**
 * ZERO 0.10.6: short-term recurrent temporal context.
 *
 * This module never receives object IDs, target coordinates, room-success
 * labels or scripted sequences. A fixed recurrent reservoir integrates only
 * consecutive sensory changes and ZERO's own selected actions. Learned
 * readouts predict the next sensory delta and estimate action utility from
 * that internal temporal state.
 *
 * Temporal influence is gated by prediction quality measured on transitions
 * excluded from readout training. Until temporal context demonstrably beats
 * the ordinary one-step model on held-out data, it contributes ZERO to action
 * choice.
 */
public final class TemporalWorkingMemory {
    public static final int S=SensorimotorMind.S;
    public static final int ACTIONS=SensorimotorMind.ACTIONS;
    public static final int HIDDEN=16;
    public static final int P=HIDDEN+1;
    public static final int PREDICTION_WEIGHTS=ACTIONS*S*P;
    public static final int VALUE_WEIGHTS=ACTIONS*P;
    public static final int PARAMETERS=PREDICTION_WEIGHTS+VALUE_WEIGHTS;
    private static final int TRACE=8;
    private static final float PRED_LR=.024f,VALUE_LR=.035f,GAMMA=.80f;

    private final float[] hidden=new float[HIDDEN];
    private final float[] decisionHidden=new float[HIDDEN];
    private final float[] predictionWeights=new float[PREDICTION_WEIGHTS];
    private final float[] valueWeights=new float[VALUE_WEIGHTS];
    private final float[][] traceHidden=new float[TRACE][HIDDEN];
    private final int[] traceAction=new int[TRACE];
    private int traceSize=0,traceCursor=0,lastAction=-1;

    public long transitions=0;
    public int validationTransitions=0,contextWins=0,contextDecisions=0;
    public int delayedOutcomeEvents=0;
    public float temporalError=Float.NaN,baselineError=Float.NaN;

    private static float clamp(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }
    private static boolean validState(float[] state){
        if(state==null||state.length!=S)return false;
        for(float v:state)if(!Float.isFinite(v)||Math.abs(v)>1.7f)return false;
        return true;
    }
    /**
     * Deterministic zero-storage projection. These recurrent/input connections
     * contain no semantic knowledge and are NOT learned rewards. Learning is
     * entirely in the readouts below.
     */
    private static float projection(int a,int b,int salt){
        int x=(a+1)*0x45d9f3b ^ (b+11)*0x119de1f3 ^ salt*0x27d4eb2d;
        x^=(x>>>16);x*=0x7feb352d;x^=(x>>>15);x*=0x846ca68b;x^=(x>>>16);
        return ((x&0xffff)/32767.5f)-1f;
    }
    private static float[] advance(float[] previous,float[] before,int action,float[] after){
        float[] next=new float[HIDDEN];
        for(int j=0;j<HIDDEN;j++){
            float z=.56f*previous[j]+.20f*projection(j,action,17);
            for(int h=0;h<HIDDEN;h++)
                z+=.055f*projection(j,h,31)*previous[h];
            for(int k=0;k<S;k++){
                float delta=clamp(after[k]-before[k],-1.5f,1.5f);
                z+=.040f*projection(j,k,53)*after[k]
                  +.095f*projection(j,k,71)*delta;
            }
            next[j]=(float)Math.tanh(z);
        }
        return next;
    }
    private float delta(float[] context,int action,int dim){
        int base=(action*S+dim)*P;
        float out=predictionWeights[base+HIDDEN];
        for(int h=0;h<HIDDEN;h++)out+=predictionWeights[base+h]*context[h];
        return clamp(out,-1.5f,1.5f);
    }
    private float value(float[] context,int action){
        int base=action*P;
        float out=valueWeights[base+HIDDEN];
        for(int h=0;h<HIDDEN;h++)out+=valueWeights[base+h]*context[h];
        return clamp(out,-4f,4f);
    }
    private float bestValue(float[] context){
        float best=-Float.MAX_VALUE;
        for(int a=0;a<ACTIONS;a++)best=Math.max(best,value(context,a));
        return best;
    }
    public int parameterCount(){return PARAMETERS;}
    public float[] contextCopy(){return hidden.clone();}

    /** Context confidence is intentionally conservative and based only on
     * held-out transitions that did not update temporal prediction weights. */
    public float confidence(){
        if(validationTransitions<25||!Float.isFinite(temporalError)||
           !Float.isFinite(baselineError)||baselineError<.012f||
           temporalError>=baselineError*.96f)return 0f;
        return clamp((baselineError-temporalError)/baselineError,0f,.70f);
    }
    public float validationWinRate(){
        return validationTransitions==0?0f:(float)contextWins/validationTransitions;
    }

    /** Context-conditioned next observation, used for diagnostics/tests.
     * Current hidden state makes identical instantaneous senses distinguishable
     * when they were reached through different recent histories. */
    public float[] prediction(float[] current,int action){
        if(!validState(current)||action<0||action>=ACTIONS)
            throw new IllegalArgumentException("temporal prediction input");
        float[] out=current.clone();
        for(int d=0;d<S;d++)out[d]=clamp(current[d]+delta(hidden,action,d),-1.5f,1.5f);
        return out;
    }
    public float rawValue(int action){
        if(action<0||action>=ACTIONS)throw new IllegalArgumentException("action");
        return value(hidden,action);
    }
    /** Bounded advice. Before held-out evidence exists this is exactly zero. */
    public float choiceBias(int action){
        float c=confidence();
        if(c<=0)return 0;
        return clamp(value(hidden,action)*c*.36f,-.45f,.45f);
    }
    public void noteDecision(float largestAbsoluteBias){
        if(Float.isFinite(largestAbsoluteBias)&&largestAbsoluteBias>.01f)contextDecisions++;
    }

    public void resetLife(){
        Arrays.fill(hidden,0);Arrays.fill(decisionHidden,0);
        traceSize=traceCursor=0;lastAction=-1;
    }
    /** Resume the currently persisted physical life. Learned temporal state
     * remains; only the interrupted action anchor is recreated. */
    public void resumeAction(int action){
        if(action<0||action>=ACTIONS)return;
        lastAction=action;
        System.arraycopy(hidden,0,decisionHidden,0,HIDDEN);
        pushTrace(action,decisionHidden);
    }
    public void chosen(int action){
        if(action<0||action>=ACTIONS)throw new IllegalArgumentException("action");
        lastAction=action;
        System.arraycopy(hidden,0,decisionHidden,0,HIDDEN);
        pushTrace(action,decisionHidden);
    }
    private void pushTrace(int action,float[] context){
        int at=traceCursor;
        traceAction[at]=action;
        System.arraycopy(context,0,traceHidden[at],0,HIDDEN);
        traceCursor=(traceCursor+1)%TRACE;
        if(traceSize<TRACE)traceSize++;
    }

    /**
     * Learn one actual action transition. baselineRmse is the ordinary
     * one-step SensorimotorMind prediction error measured BEFORE it trains.
     * A validation transition is never used to fit temporal prediction
     * weights, making the confidence gate genuinely out-of-sample.
     */
    public void observe(float[] before,int action,float[] after,float reward,
                        float baselineRmse,boolean learn,boolean validation){
        if(!validState(before)||!validState(after)||action<0||action>=ACTIONS)return;
        if(lastAction!=action){
            lastAction=action;
            System.arraycopy(hidden,0,decisionHidden,0,HIDDEN);
            pushTrace(action,decisionHidden);
        }
        float sum=0;
        for(int d=0;d<S;d++){
            float predicted=before[d]+delta(decisionHidden,action,d);
            float err=after[d]-predicted;
            sum+=err*err;
            if(learn){
                int base=(action*S+d)*P;
                float g=PRED_LR*clamp(err,-1.5f,1.5f);
                for(int h=0;h<HIDDEN;h++)
                    predictionWeights[base+h]=clamp(
                        predictionWeights[base+h]+g*decisionHidden[h],-3f,3f);
                predictionWeights[base+HIDDEN]=clamp(
                    predictionWeights[base+HIDDEN]+g,-3f,3f);
            }
        }
        float rmse=(float)Math.sqrt(sum/S);
        if(validation&&Float.isFinite(baselineRmse)&&baselineRmse>=0){
            validationTransitions++;
            float alpha=validationTransitions==1?1f:.08f;
            temporalError=validationTransitions==1?rmse:
                temporalError*(1-alpha)+rmse*alpha;
            baselineError=validationTransitions==1?baselineRmse:
                baselineError*(1-alpha)+baselineRmse*alpha;
            if(rmse<baselineRmse)contextWins++;
        }

        float[] next=advance(decisionHidden,before,action,after);
        if(learn&&Float.isFinite(reward)){
            float target=clamp(reward+GAMMA*bestValue(next),-4f,4f);
            float td=clamp(target-value(decisionHidden,action),-1.5f,1.5f);
            int base=action*P;
            for(int h=0;h<HIDDEN;h++)
                valueWeights[base+h]=clamp(
                    valueWeights[base+h]+VALUE_LR*td*decisionHidden[h],-4f,4f);
            valueWeights[base+HIDDEN]=clamp(
                valueWeights[base+HIDDEN]+VALUE_LR*td,-4f,4f);
        }
        System.arraycopy(next,0,hidden,0,HIDDEN);
        transitions++;
        lastAction=-1;
    }

    /** Delayed, physically measured energetic consequence. It is credited
     * backwards through only the recent action contexts, never through an
     * object label or a hand-written solution. */
    public void energyOutcome(float actualDelta){
        if(!Float.isFinite(actualDelta)||Math.abs(actualDelta)<.001f)return;
        delayedOutcome(clamp(actualDelta/20f,-1.5f,1.5f));
    }
    /** Generic displacement of an external physical object. */
    public void physicalOutcome(float displacement){
        if(!Float.isFinite(displacement)||displacement<=.015f)return;
        delayedOutcome(clamp(displacement/22f,0f,1f)*.55f);
    }
    private void delayedOutcome(float outcome){
        if(traceSize==0)return;
        delayedOutcomeEvents++;
        float decay=1f;
        for(int age=0;age<traceSize;age++){
            int idx=(traceCursor-1-age+TRACE)%TRACE;
            int action=traceAction[idx];
            float[] ctx=traceHidden[idx];
            float error=clamp(outcome-value(ctx,action),-1.5f,1.5f);
            int base=action*P;
            float rate=VALUE_LR*.72f*decay;
            for(int h=0;h<HIDDEN;h++)
                valueWeights[base+h]=clamp(
                    valueWeights[base+h]+rate*error*ctx[h],-4f,4f);
            valueWeights[base+HIDDEN]=clamp(
                valueWeights[base+HIDDEN]+rate*error,-4f,4f);
            decay*=.78f;
        }
    }

    private static String safe(float value){
        return Float.isFinite(value)?Float.toString(value):"-1";
    }
    public String encode(){
        StringBuilder b=new StringBuilder("tw1|").append(transitions).append('|')
            .append(validationTransitions).append('|').append(contextWins).append('|')
            .append(contextDecisions).append('|').append(delayedOutcomeEvents).append('|')
            .append(safe(temporalError)).append('|').append(safe(baselineError));
        for(float v:hidden)b.append('|').append(v);
        for(float v:predictionWeights)b.append('|').append(v);
        for(float v:valueWeights)b.append('|').append(v);
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.isEmpty()||raw.length()>800000)return false;
        String[] p=raw.split("\\|",-1);
        int expected=8+HIDDEN+PARAMETERS;
        if(p.length!=expected||!"tw1".equals(p[0]))return false;
        try{
            long tr=Long.parseLong(p[1]);
            int val=Integer.parseInt(p[2]),wins=Integer.parseInt(p[3]),
                decisions=Integer.parseInt(p[4]),outcomes=Integer.parseInt(p[5]);
            float te=Float.parseFloat(p[6]),be=Float.parseFloat(p[7]);
            if(tr<0||val<0||wins<0||wins>val||decisions<0||outcomes<0||
               te< -1||be< -1||!Float.isFinite(te)||!Float.isFinite(be))return false;
            float[] h=new float[HIDDEN],pred=new float[PREDICTION_WEIGHTS],
                    values=new float[VALUE_WEIGHTS];
            int at=8;
            for(int i=0;i<HIDDEN;i++){
                h[i]=Float.parseFloat(p[at++]);
                if(!Float.isFinite(h[i])||Math.abs(h[i])>1.01f)return false;
            }
            for(int i=0;i<pred.length;i++){
                pred[i]=Float.parseFloat(p[at++]);
                if(!Float.isFinite(pred[i])||Math.abs(pred[i])>3.01f)return false;
            }
            for(int i=0;i<values.length;i++){
                values[i]=Float.parseFloat(p[at++]);
                if(!Float.isFinite(values[i])||Math.abs(values[i])>4.01f)return false;
            }
            System.arraycopy(h,0,hidden,0,HIDDEN);
            System.arraycopy(pred,0,predictionWeights,0,pred.length);
            System.arraycopy(values,0,valueWeights,0,values.length);
            transitions=tr;validationTransitions=val;contextWins=wins;
            contextDecisions=decisions;delayedOutcomeEvents=outcomes;
            temporalError=te<0?Float.NaN:te;
            baselineError=be<0?Float.NaN:be;
            Arrays.fill(decisionHidden,0);traceSize=traceCursor=0;lastAction=-1;
            return true;
        }catch(RuntimeException invalid){return false;}
    }

    private static float distance(float[] a,float[] b){
        float s=0;for(int i=0;i<a.length;i++){float d=a[i]-b[i];s+=d*d;}
        return (float)Math.sqrt(s);
    }

    public static void main(String[] args){
        TemporalWorkingMemory m=new TemporalWorkingMemory();
        float[] cueA=new float[S],cueB=new float[S],middle=new float[S];
        float[] outA=new float[S],outB=new float[S];
        cueA[9]=1f;cueB[9]=-1f;
        outA[11]=.9f;outB[11]=-.9f;

        // Same instantaneous middle state must become distinguishable from
        // the immediately preceding cue. One in five outcome transitions is
        // held out from prediction learning.
        for(int n=0;n<900;n++){
            boolean a=(n&1)==0;
            float[] cue=a?cueA:cueB,out=a?outA:outB;
            m.resetLife();
            m.chosen(2);
            m.observe(cue,2,middle,0,.60f,true,false);
            m.chosen(5);
            boolean held=n%5==0;
            m.observe(middle,5,out,0,.60f,!held,held);
        }
        m.resetLife();m.chosen(2);m.observe(cueA,2,middle,0,.60f,false,false);
        float[] ctxA=m.contextCopy(),predA=m.prediction(middle,5);
        m.resetLife();m.chosen(2);m.observe(cueB,2,middle,0,.60f,false,false);
        float[] ctxB=m.contextCopy(),predB=m.prediction(middle,5);
        if(distance(ctxA,ctxB)<.12f)
            throw new AssertionError("Recent histories collapsed to one state");
        if(predA[11]-predB[11]<.55f)
            throw new AssertionError("Temporal context did not disambiguate identical present senses: "+
                                     predA[11]+" vs "+predB[11]);
        if(m.validationTransitions<100||m.confidence()<=0||m.validationWinRate()<.60f)
            throw new AssertionError("Temporal model failed held-out evidence gate");

        // Delayed real outcome must change recent contextual action value.
        m.chosen(7);float before=m.rawValue(7);
        for(int i=0;i<8;i++)m.energyOutcome(20);
        float after=m.rawValue(7);
        if(!(after>before))
            throw new AssertionError("Delayed physical consequence not credited through working trace");

        String stored=m.encode();
        TemporalWorkingMemory copy=new TemporalWorkingMemory();
        if(!copy.restore(stored)||copy.transitions!=m.transitions||
           copy.validationTransitions!=m.validationTransitions||
           Math.abs(copy.confidence()-m.confidence())>.0001f)
            throw new AssertionError("Temporal working memory persistence failed");
        float[] persisted=copy.contextCopy();
        copy.resetLife();
        if(distance(persisted,copy.contextCopy())<.01f)
            throw new AssertionError("Life reset did not clear transient context");
        if(copy.transitions!=m.transitions)
            throw new AssertionError("Life reset destroyed learned temporal model");
        String corrupt=stored.replaceFirst("tw1","bad");
        if(copy.restore(corrupt))
            throw new AssertionError("Corrupt temporal state accepted");

        System.out.println("ZERO 0.10.6 temporal working memory: recurrent context, heldout gating, delayed consequence, persistence passed");
    }
}
