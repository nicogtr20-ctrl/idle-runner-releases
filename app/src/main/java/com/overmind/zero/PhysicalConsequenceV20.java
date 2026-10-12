package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * ZERO2 Gate E: observational physical event attribution.
 *
 * Inputs are ONLY actually chosen actions, self touch/adhesion sensation,
 * physically confirmed externally observed displacement and actual received
 * energy. No object identity, hidden positions, procedural cues or rewards.
 * A temporal link is NOT proof of causation and never steers ZERO.
 */
public final class PhysicalConsequenceV20 {
    public static final String KEY="physical_consequence_v20";
    private static final int ACTIONS=SensorimotorMind.ACTIONS,CONTEXTS=3;
    private static final int CASES=ACTIONS*CONTEXTS,WINDOW=6;
    private final int[] tries=new int[CASES],moves=new int[CASES];
    private final int[] contextTries=new int[CONTEXTS],contextMoves=new int[CONTEXTS];
    private final int[] recentMoveAge=new int[WINDOW];
    private int recentCount=0,pending=-1,context=0;
    private long intervals=0,trainIntervals=0,heldoutIntervals=0;
    private long physicalEvents=0,quietIntervals=0,actualEnergyContacts=0;
    private long adjacentEnergy=0,ambiguousEnergy=0,energyWithoutMove=0;
    private long badSamples=0;
    private float heldoutError=Float.NaN,baselineError=Float.NaN;
    public long intervals(){return intervals;}
    public long trained(){return trainIntervals;}
    public long validations(){return heldoutIntervals;}
    public long physicalEvents(){return physicalEvents;}
    public long actualEnergyContacts(){return actualEnergyContacts;}
    public long adjacentEnergy(){return adjacentEnergy;}
    public long ambiguousEnergy(){return ambiguousEnergy;}
    public long unlinkedEnergy(){return energyWithoutMove;}
    public long badSamples(){return badSamples;}
    public float error(){return heldoutError;}
    public float referenceError(){return baselineError;}
    public int differentActions(int ctx){
        if(ctx<0||ctx>=CONTEXTS)return 0;
        int n=0;
        for(int a=0;a<ACTIONS;a++)if(tries[ctx*ACTIONS+a]>=4)n++;
        return n;
    }
    public int supportedContexts(){
        int n=0;
        for(int c=0;c<CONTEXTS;c++)
            if(contextTries[c]>=35&&differentActions(c)>=2)n++;
        return n;
    }
    public float confidence(){
        if(heldoutIntervals<80||physicalEvents<12||supportedContexts()<1||
            !Float.isFinite(heldoutError)||!Float.isFinite(baselineError)||
            baselineError<.04f||heldoutError>=baselineError*.94f)return 0f;
        return Math.min(.38f,Math.max(0f,
            (baselineError-heldoutError)/baselineError));
    }
    private static boolean sensesValid(float[] v){
        if(v==null||v.length!=SensorimotorMind.S)return false;
        for(float x:v)if(!Float.isFinite(x)||Math.abs(x)>1.6f)return false;
        return true;
    }
    /** Only self-observed tactile state, never an object type. */
    private static int context(float[] senses){
        if(senses[5]>=.035f)return 2;
        if(senses[4]>=.045f)return 1;
        return 0;
    }
    private static float estimate(int successes,int count){
        // Laplace prior strongly expects an unchanged world. The same
        // estimator is used by both action-conditioned and context baseline.
        return (successes+0.5f)/(count+24f);
    }
    /** Prediction can be inspected even in virgin state but no action
     * advice can be produced without independent verification. */
    public float forecastExternalMovement(int action,float[] senses){
        if(action<0||action>=ACTIONS||!sensesValid(senses))return Float.NaN;
        return estimate(moves[context(senses)*ACTIONS+action],
                        tries[context(senses)*ACTIONS+action]);
    }
    public void resetLife(){
        pending=-1;recentCount=0;
        Arrays.fill(recentMoveAge,0);
    }
    /** Pending action is discarded after a restored partial interval. */
    public void resumeLife(){resetLife();}
    public void chosen(int action,float[] senses){
        if(action<0||action>=ACTIONS||!sensesValid(senses)){
            pending=-1;badSamples++;return;
        }
        pending=action;context=context(senses);
    }
    /** Receives physical effects ONLY AFTER the real solver has moved a
     * touched external object. Collision attempts alone are NOT success. */
    public void consequence(int action,float measuredExternalEffect,
                            boolean training,boolean heldout){
        if(pending!=action||action<0||action>=ACTIONS||
           !Float.isFinite(measuredExternalEffect)||
           measuredExternalEffect<0||measuredExternalEffect>1e8f){
            pending=-1;badSamples++;return;
        }
        int slot=context*ACTIONS+action;
        boolean moved=measuredExternalEffect>.015f;
        float actual=moved?1f:0f;
        if(heldout){
            float model=forecastExternalMovementFromSlot(slot);
            float base=estimate(contextMoves[context],contextTries[context]);
            float modelSquared=(actual-model)*(actual-model);
            float baselineSquared=(actual-base)*(actual-base);
            float alpha=heldoutIntervals==0?1f:.035f;
            heldoutError=heldoutIntervals==0?modelSquared:
                         (1f-alpha)*heldoutError+alpha*modelSquared;
            baselineError=heldoutIntervals==0?baselineSquared:
                          (1f-alpha)*baselineError+alpha*baselineSquared;
            heldoutIntervals++;
        }else if(training){
            tries[slot]++;
            contextTries[context]++;
            if(moved){moves[slot]++;contextMoves[context]++;}
            trainIntervals++;
        }
        for(int i=0;i<recentCount;i++)recentMoveAge[i]++;
        if(recentCount>0){
            int count=0;
            for(int i=0;i<recentCount;i++)
                if(recentMoveAge[i]<=WINDOW)recentMoveAge[count++]=recentMoveAge[i];
            recentCount=count;
        }
        if(moved){
            if(recentCount==WINDOW){
                System.arraycopy(recentMoveAge,1,recentMoveAge,0,WINDOW-1);
                recentCount--;
            }
            recentMoveAge[recentCount++]=0;
            physicalEvents++;
        }else quietIntervals++;
        intervals++;pending=-1;
    }
    private float forecastExternalMovementFromSlot(int slot){
        return estimate(moves[slot],tries[slot]);
    }
    /** Actual received energy only. No credit for a mere grip/movement.
     * "Adjacent" means one real movement within 6 decisions, not proof
     * that that object provided the contacted resource. */
    public void energyOutcome(float actualEnergyDelta){
        if(!Float.isFinite(actualEnergyDelta)||Math.abs(actualEnergyDelta)<.01f){
            if(!Float.isFinite(actualEnergyDelta))badSamples++;
            return;
        }
        actualEnergyContacts++;
        if(recentCount==1)adjacentEnergy++;
        else if(recentCount>1)ambiguousEnergy++;
        else energyWithoutMove++;
    }
    public String encode(){
        StringBuilder s=new StringBuilder("pe20|").append(intervals).append('|')
            .append(trainIntervals).append('|').append(heldoutIntervals)
            .append('|').append(physicalEvents).append('|').append(quietIntervals)
            .append('|').append(actualEnergyContacts).append('|')
            .append(adjacentEnergy).append('|').append(ambiguousEnergy)
            .append('|').append(energyWithoutMove).append('|').append(badSamples)
            .append('|').append(Float.isFinite(heldoutError)?heldoutError:-1f)
            .append('|').append(Float.isFinite(baselineError)?baselineError:-1f);
        for(int v:tries)s.append('|').append(v);
        for(int v:moves)s.append('|').append(v);
        for(int v:contextTries)s.append('|').append(v);
        for(int v:contextMoves)s.append('|').append(v);
        return s.toString();
    }
    /** Atomic optional profile restore; never mutates existing legacy keys. */
    public boolean restore(String raw){
        if(raw==null||raw.length()>18000||!raw.startsWith("pe20|"))return false;
        String[] p=raw.split("\\|",-1);
        if(p.length!=13+CASES*2+CONTEXTS*2)return false;
        try{
            long[] head=new long[10];
            for(int i=0;i<head.length;i++){
                head[i]=Long.parseLong(p[1+i]);
                if(head[i]<0)return false;
            }
            float prediction=Float.parseFloat(p[11]),base=Float.parseFloat(p[12]);
            if(!Float.isFinite(prediction)||!Float.isFinite(base)||
                prediction< -1f||prediction>1f||base< -1f||base>1f)return false;
            int[] t=new int[CASES],m=new int[CASES],
                tc=new int[CONTEXTS],mc=new int[CONTEXTS];
            int k=13;
            for(int i=0;i<CASES;i++)t[i]=Integer.parseInt(p[k++]);
            for(int i=0;i<CASES;i++)m[i]=Integer.parseInt(p[k++]);
            for(int i=0;i<CONTEXTS;i++)tc[i]=Integer.parseInt(p[k++]);
            for(int i=0;i<CONTEXTS;i++)mc[i]=Integer.parseInt(p[k++]);
            if(head[1]+head[2]!=head[0]||head[3]+head[4]!=head[0]||
               head[6]+head[7]+head[8]!=head[5]||
               head[5]>head[0]+head[9]+10000000L)return false;
            long learned=0,externalMoves=0;
            for(int c=0;c<CONTEXTS;c++){
                if(tc[c]<0||mc[c]<0||mc[c]>tc[c])return false;
                long ctxt=0,evt=0;
                for(int a=0;a<ACTIONS;a++){
                    int idx=c*ACTIONS+a;
                    if(t[idx]<0||m[idx]<0||m[idx]>t[idx])return false;
                    ctxt+=t[idx];evt+=m[idx];
                }
                if(ctxt!=tc[c]||evt!=mc[c])return false;
                learned+=ctxt;externalMoves+=evt;
            }
            if(learned!=head[1]||externalMoves>head[3])return false;
            System.arraycopy(t,0,tries,0,CASES);
            System.arraycopy(m,0,moves,0,CASES);
            System.arraycopy(tc,0,contextTries,0,CONTEXTS);
            System.arraycopy(mc,0,contextMoves,0,CONTEXTS);
            intervals=head[0];trainIntervals=head[1];
            heldoutIntervals=head[2];physicalEvents=head[3];
            quietIntervals=head[4];actualEnergyContacts=head[5];
            adjacentEnergy=head[6];ambiguousEnergy=head[7];
            energyWithoutMove=head[8];badSamples=head[9];
            heldoutError=prediction<0?Float.NaN:prediction;
            baselineError=base<0?Float.NaN:base;
            resetLife();
            return true;
        }catch(RuntimeException invalid){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
            "ZERO20 physical attribution: %d observed moves, %d energy contacts, "+
            "%d temporal associations (%d ambiguous), heldout %.5f vs %.5f, "+
            "confidence %.3f; SHADOW ONLY",
            physicalEvents,actualEnergyContacts,adjacentEnergy,ambiguousEnergy,
            heldoutError,baselineError,confidence());
    }
}
