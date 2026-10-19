package com.overmind.zero;

import java.util.Locale;

/**
 * Read-only production GRU V358 audit: no physical game, APK, production
 * source, checkpoint or reward is modified. Detect fixed-right test control
 * and quantify first-choice and repeating cold-start directional bias.
 */
public final class Zero3DirectionalBiasAuditV359 {
    private static Zero3RecurrentBrain fresh() {
        Zero3RecurrentBrain b=new Zero3RecurrentBrain(194,true);
        b.enableCognitionV353();b.enableExecutiveV354();
        return b;
    }
    private static float[] senses(int seed){
        float[] s=new float[194];
        long state=0x71ABCEFL+seed*773L;
        for(int i=0;i<194;i++){
            state^=state<<13;state^=state>>>7;state^=state<<17;
            s[i]=(float)((state>>>40)/16777216.0*2.0-1.0)*.55f;
        }
        s[193]=1f;
        return s;
    }
    static final class Stats {
        int n,right,left,up,down,ties,zero;
        double xs,ys,positiveAbs,negativeAbs;
        void put(Zero3LearningFoundation.MotorIntent a){
            n++;xs+=a.x;ys+=a.y;
            if(a.x>0) {right++;positiveAbs+=a.x;}
            else if(a.x<0){left++;negativeAbs+=-a.x;}
            else ties++;
            if(a.y>0)up++;else if(a.y<0)down++;
            if(Math.abs(a.x-1f)<1e-6&&Math.abs(a.y)<1e-6)zero++;
        }
        String report(){
            return String.format(Locale.US,
                "n=%d right=%d left=%d up=%d down=%d ties=%d exactRightFallback=%d avgX=%+.6f avgY=%+.6f rightFraction=%.5f meanRightMagnitude=%.4f meanLeftMagnitude=%.4f",
                n,right,left,up,down,ties,zero,xs/n,ys/n,(double)right/n,
                right==0?0:positiveAbs/right,left==0?0:negativeAbs/left);
        }
    }
    private static Zero3LearningFoundation.MotorIntent sample(Zero3RecurrentBrain b,float[] input){
        Zero3LearningFoundation.MotorIntent intent=b.choose(input,1,0,0);
        b.abort();return intent;
    }
    public static void main(String[] args){
        Stats initialIdentical=new Stats();
        for(int i=0;i<40;i++)initialIdentical.put(sample(fresh(),new float[194]));
        System.out.println("V359_FRESH_IDENTICAL_ZERO_SENSORS "+initialIdentical.report());
        Stats varied=new Stats();
        for(int i=0;i<256;i++)varied.put(sample(fresh(),senses(i)));
        System.out.println("V359_FRESH_VARIED_SENSORS "+varied.report());
        Stats continuing=new Stats();
        Zero3RecurrentBrain b=fresh();
        for(int i=0;i<1024;i++)continuing.put(sample(b,senses(i%16)));
        System.out.println("V359_SAME_BRAIN_1024_UNTRAINED_CHOICES "+continuing.report());
        Stats lifereset=new Stats();
        Zero3RecurrentBrain alive=fresh();
        for(int i=0;i<128;i++){
            Zero3LearningFoundation.MotorIntent a=sample(alive,senses(i%6));
            lifereset.put(a);
            alive.resetLife();
        }
        System.out.println("V359_SAME_BRAIN_128_NEW_LIVES "+lifereset.report());
        // Fixed seed and identical input produce the same FIRST choice.
        if(initialIdentical.right==40||initialIdentical.left==40)
            System.out.println("V359_COLD_START_FIXED_AXIS_BIAS=REPRODUCIBLE");
        else throw new AssertionError("Determinism test invalid, original seed changed");
        if(continuing.right==1024||continuing.left==1024)
            System.out.println("V359_NO_LEARNING_DIRECTION_LOCK=REPRODUCIBLE");
        else System.out.println("V359_NO_LEARNING_DIRECTION_LOCK=ABSENT_WITH_SAMPLED_SEED");
        System.out.println("V359_STATIC_LAB_RIGHT_INTENT_ONLY_NOT_IN_GAME=review_GameActivity_Zero3OriginalBrain");
    }
}
