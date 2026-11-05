package com.overmind.zero;

import java.util.Arrays;

/**
 * ZERO489 QA ONLY. Learn an action-conditional physical movement association
 * from the ORIGINAL material solver's CLEAN transitions, matched on the
 * MEASURED pre-decision COM velocity sector/magnitude and the issued effort.
 *
 * No motor intervention, fabricated reward, resource semantics, stored
 * checkpoint field or alternative action owner. The shared context-only
 * empirical baseline is the negative control for the action-conditioned
 * residual. An independently heldout sample must outperform it, not just
 * beat the physically false no-drag constant-velocity assumption of V377.
 *
 * This is observational evidence, NOT guaranteed same-state causality.
 * Fail CLOSED until ≥2 real action directions have been observed in the
 * same PRE velocity/effort context and heldout prediction improves.
 */
final class Zero489MatchedPreMotorResponse {
    static final int SPEED=3, PRE_DIRECTIONS=8, EFFORT=2,
                     ACTION_DIRECTIONS=8, CONTEXTS=SPEED*PRE_DIRECTIONS*EFFORT,
                     BANKS=CONTEXTS*ACTION_DIRECTIONS;
    static final int MIN_TRAIN=8, MIN_HOLDOUT=4, MIN_CONTEXT=24;
    private final int[] contextTrain=new int[CONTEXTS],bankTrain=new int[BANKS],
        bankHeldout=new int[BANKS],measuredHoldout=new int[BANKS];
    private final float[] contextX=new float[CONTEXTS],contextY=new float[CONTEXTS],
        bankX=new float[BANKS],bankY=new float[BANKS],
        validationX=new float[BANKS],validationY=new float[BANKS];
    private final double[] bankMse=new double[BANKS],contextMse=new double[BANKS];
    private long acceptedTrain,acceptedHeldout,physicalPredictions,validationEligible;

    private static int sector(float x,float y) {
        return Math.floorMod(Math.round((float)Math.atan2(y,x)/
            (float)(Math.PI/4d)),PRE_DIRECTIONS);
    }
    static int context(float preVx,float preVy,float effort){
        if(!Float.isFinite(preVx)||!Float.isFinite(preVy)||
           !Float.isFinite(effort)||effort<0||effort>1)
            throw new IllegalArgumentException("ZERO489 real PRE/effort invalid");
        double speed=Math.hypot(preVx,preVy);
        int regime=speed<.35d?0:speed<2d?1:2;
        int preSector=regime==0?0:sector(preVx,preVy);
        return (regime*PRE_DIRECTIONS+preSector)*EFFORT+
            (effort<.5f?0:1);
    }
    private static int bank(int context,float actionX,float actionY){
        if(!Float.isFinite(actionX)||!Float.isFinite(actionY)||
           Math.hypot(actionX,actionY)<.1)
            throw new IllegalArgumentException("ZERO489 real issued heading invalid");
        return context*ACTION_DIRECTIONS+sector(actionX,actionY);
    }
    private int otherHeadings(int c,int self){
        int n=0;
        for(int h=0;h<ACTION_DIRECTIONS;h++)
            if(c*ACTION_DIRECTIONS+h!=self&&
               bankTrain[c*ACTION_DIRECTIONS+h]>=MIN_TRAIN)n++;
        return n;
    }
    /**
     * Called ONLY after a clean, uninjured, nonwall real original V356
     * transition. TRAIN and independent HELDOUT are disjoint. No ground-truth
     * future simulator or world map is provided.
     */
    void observe(float preVx,float preVy,float ax,float ay,float effort,
                 float dx,float dy,float seconds,boolean heldout){
        if(!Float.isFinite(dx)||!Float.isFinite(dy)||
           !Float.isFinite(seconds)||seconds<=0f||seconds>.651f)
            throw new IllegalArgumentException("ZERO489 genuine measured material transition");
        int c=context(preVx,preVy,effort),b=bank(c,ax,ay);
        float factor=.30f/seconds;
        // The same observed PRE momentum is subtracted for all headings
        // within a context; do not mistake global drift as one motor effect.
        float residualX=limit(dx*factor-.30f*preVx);
        float residualY=limit(dy*factor-.30f*preVy);
        if(heldout){
            acceptedHeldout++;
            bankHeldout[b]++;
            float alpha=bankHeldout[b]==1?1f:.12f;
            validationX[b]+=alpha*(residualX-validationX[b]);
            validationY[b]+=alpha*(residualY-validationY[b]);
            // A heldout action compares both models frozen BEFORE this
            // observation. Never leak heldout data into TRAIN means.
            if(bankTrain[b]>=MIN_TRAIN&&contextTrain[c]>=MIN_CONTEXT&&
               otherHeadings(c,b)>0){
                double eBank=squared(residualX-bankX[b],residualY-bankY[b]);
                double eContext=squared(residualX-contextX[c],residualY-contextY[c]);
                int n=++measuredHoldout[b];
                double beta=n==1?1d:.12d;
                bankMse[b]+=beta*(eBank-bankMse[b]);
                contextMse[b]+=beta*(eContext-contextMse[b]);
                validationEligible++;
            }
        }else{
            acceptedTrain++;
            int bn=++bankTrain[b],cn=++contextTrain[c];
            float ab=bn==1?1f:Math.min(.12f,1f/bn+.05f);
            float ac=cn==1?1f:Math.min(.08f,1f/cn+.025f);
            bankX[b]+=ab*(residualX-bankX[b]);
            bankY[b]+=ab*(residualY-bankY[b]);
            contextX[c]+=ac*(residualX-contextX[c]);
            contextY[c]+=ac*(residualY-contextY[c]);
        }
    }
    private static float limit(float x){return Math.max(-120f,Math.min(120f,x));}
    private static double squared(double x,double y){return x*x+y*y;}
    private boolean reliable(int c,int b){
        if(bankTrain[b]<MIN_TRAIN||measuredHoldout[b]<MIN_HOLDOUT||
           contextTrain[c]<MIN_CONTEXT||otherHeadings(c,b)==0)return false;
        // For a truly shared passive coast, all actions predict the same
        // displacement and the action-conditioned differential stays zero.
        float deltaX=bankX[b]-contextX[c],deltaY=bankY[b]-contextY[c];
        double contrast=squared(deltaX,deltaY);
        if(contrast<.01d)return false;  // <0.10 units of real differential
        // Heldout cohort must agree on the *sign* of the differential, not
        // merely show low variance from a nearly zero-motion predictor.
        double heldoutDX=validationX[b]-contextX[c],
               heldoutDY=validationY[b]-contextY[c];
        if(deltaX*heldoutDX+deltaY*heldoutDY<.25d*contrast)return false;
        // Shared context-only model is the rigorous same-PRE negative
        // control. Require a meaningful heldout loss improvement.
        return contextMse[b]>.0001d&&bankMse[b]<contextMse[b]*.85d;
    }
    float[] predicted(float[] observation194,float[] action){
        if(observation194==null||observation194.length!=194||
           action==null||action.length<3)return null;
        float vx=observation194[191]*130f,vy=observation194[192]*130f;
        int c=context(vx,vy,action[2]),b=bank(c,action[0],action[1]);
        if(!reliable(c,b))return null;
        physicalPredictions++;
        return new float[]{limit(.30f*vx+bankX[b]),limit(.30f*vy+bankY[b])};
    }
    int reliableBanks(){
        int n=0;for(int b=0;b<BANKS;b++)if(reliable(b/ACTION_DIRECTIONS,b))n++;
        return n;
    }
    String diagnostics(){
        return "v489|train="+acceptedTrain+"|heldout="+acceptedHeldout+
            "|eligibleHeldout="+validationEligible+
            "|reliableMatchedBanks="+reliableBanks()+
            "|physicalPredictions="+physicalPredictions;
    }
    void copyTo(Zero489MatchedPreMotorResponse other){
        for(int i=0;i<CONTEXTS;i++){
            other.contextTrain[i]=contextTrain[i];
            other.contextX[i]=contextX[i];other.contextY[i]=contextY[i];
        }
        for(int i=0;i<BANKS;i++){
            other.bankTrain[i]=bankTrain[i];
            other.bankHeldout[i]=bankHeldout[i];
            other.measuredHoldout[i]=measuredHoldout[i];
            other.bankX[i]=bankX[i];other.bankY[i]=bankY[i];
            other.validationX[i]=validationX[i];
            other.validationY[i]=validationY[i];
            other.bankMse[i]=bankMse[i];other.contextMse[i]=contextMse[i];
        }
        other.acceptedTrain=acceptedTrain;other.acceptedHeldout=acceptedHeldout;
        other.physicalPredictions=physicalPredictions;
        other.validationEligible=validationEligible;
    }
}
