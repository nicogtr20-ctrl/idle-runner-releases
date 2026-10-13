package com.overmind.zero;

import java.util.Locale;

/**
 * Unified ZERO21 SHADOW arbitration laboratory.
 * Potential actions are ranked with ONE homeostatic/risk/competence ledger.
 * Only actually lived contact energy is survival value; mechanically moving
 * an object is a context-gated capability, not a general-purpose reward.
 * Imagined trajectories are borrowed ONLY from an independently validated
 * planner, never used to train any model or to alter motor choices.
 * This class has no policy bias, action, actuator or RNG interface.
 */
public final class UnifiedArbiterV21 {
    public static final String KEY="unified_arbiter_v21";
    private static final String FORMAT="ua21";
    private long evaluations=0,eligible=0,abstained=0,agreement=0;
    private int proposed=-1;
    private float confidence,margin;
    private static float clip(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }
    private static boolean sensory(float[] s){
        if(s==null||s.length!=UnifiedCognitionV21.SENSES)return false;
        for(float v:s)if(!Float.isFinite(v)||Math.abs(v)>2f)return false;
        return true;
    }
    /**
     * Call AFTER the incumbent has already selected an action. Rejects if
     * learned outcome and world predictors fail independent heldout tests.
     */
    public void evaluate(float[] senses,UnifiedCognitionV21 evidence,
                         PredictiveWorldV20 world,FuturePlannerV20 future,
                         ObjectContinuityV21 perceived){
        evaluations++;proposed=-1;confidence=margin=0;
        if(!sensory(senses)||evidence==null||world==null||future==null||
           evidence.confidence()<.10f||world.confidence()<.10f||
           future.latestConfidence()<.10f||future.shadowSuggestion()<0||
           !Float.isFinite(world.heldoutError())||
           !Float.isFinite(world.baselineError())||
           world.heldoutError()>=world.baselineError()*.92f){
            abstained++;return;
        }
        final float energy=senses[0];
        final boolean visibleNear=senses[4]>.02f;
        float distance=0;
        for(int j=6;j<14;j++)distance=Math.max(distance,senses[j]);
        boolean relevant=distance>.30f||visibleNear;
        float best=-Float.MAX_VALUE,second=-Float.MAX_VALUE;
        int winner=-1;
        for(int a=0;a<UnifiedCognitionV21.ACTIONS;a++){
            long n=evidence.opportunities(a,senses);
            if(n<12)continue; // sparse experiences are UNKNOWN, not zero benefit
            float earned=clip(evidence.netEnergyMean(a,senses)/22f,-2f,2f);
            float risk=evidence.harmfulRate(a,senses);
            float learnedMechanical=evidence.physicalProbability(a,senses);
            float mechanicalCredit=relevant&&energy>.40f?
                .06f*learnedMechanical:0f;
            // No global 'move an object = food'; opportunity cost rises with hunger.
            float uncertainty=(float)(.40/Math.sqrt(n));
            float score=earned*(energy<.35f?2f:1f)-
                (energy<.35f?2.5f:1.3f)*risk +
                mechanicalCredit-uncertainty;
            if(score>best){second=best;best=score;winner=a;}
            else if(score>second)second=score;
        }
        if(winner<0||second==-Float.MAX_VALUE||!Float.isFinite(best)||
           best-second<.15f||future.shadowSuggestion()!=winner){
            abstained++;return;
        }
        // No single candidate from an inaccurate object tracker becomes true.
        // Its uncertainty only makes a non-contact mechanical hypothesis weaker.
        if(relevant&&visibleNear&&perceived!=null&&
           perceived.currentAnonymousTrack()<0&&
           evidence.physicalProbability(winner,senses)>.2f){
            abstained++;return;
        }
        proposed=winner;
        margin=best-second;
        confidence=clip(Math.min(evidence.confidence(),world.confidence()),0f,.5f);
        eligible++;agreement++;
    }
    public int shadowSuggestion(){return proposed;}
    public long checks(){return evaluations;}
    public long eligible(){return eligible;}
    public long abstentions(){return abstained;}
    public float confidence(){return confidence;}
    public float margin(){return margin;}
    public void resetLife(){proposed=-1;confidence=margin=0;}
    public String encode(){
        return String.format(Locale.US,"%s|%d|%d|%d|%d",FORMAT,
            evaluations,eligible,abstained,agreement);
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>150||!raw.startsWith(FORMAT+"|"))return false;
        try{
            String[] s=raw.split("\\|",-1);
            if(s.length!=5)return false;
            long n=Long.parseLong(s[1]),e=Long.parseLong(s[2]),
                 a=Long.parseLong(s[3]),g=Long.parseLong(s[4]);
            if(n<0||n>1000000000L||e<0||a<0||g<0||n!=e+a||g>e)return false;
            evaluations=n;eligible=e;abstained=a;agreement=g;
            resetLife();return true;
        }catch(RuntimeException invalid){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
            "ZERO21 arbiter SHADOW: eligible=%d abstained=%d checks=%d margin=%.3f confidence=%.3f no motor authority",
            eligible,abstained,evaluations,margin,confidence);
    }
}
