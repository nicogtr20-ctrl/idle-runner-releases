package com.overmind.zero;

/**
 * ZERO396 research-only measured original GRU3 low-level motor recovery.
 *
 * An optional per-INTENTION motor authority confidence model, NOT a chooser,
 * goal, automatic escape or second cognitive brain. No direction has any
 * preset correction. A particular cortical parameter fraction is eligible
 * only after paired PHYSICAL tests on independently different material
 * contexts and heldout confirmation. Current production never calls it;
 * no user checkpoint or saved 12 profiles are mutated in this lab.
 */
final class Zero396MeasuredMotorRetuning {
    private static final float[] FRACTIONS={.25f,.50f,.75f,1f};
    private final float[][][] physicalGain=new float[8][4][3];
    private final boolean[][][] observed=new boolean[8][4][3];
    private final float[] selected=new float[8];
    private final boolean[] finalized=new boolean[8];
    private static final float MIN_REPRODUCIBLE_GAIN=.07f;
    static float fraction(int idx){
        if(idx<0||idx>=FRACTIONS.length)
            throw new IllegalArgumentException("fraction index");
        return FRACTIONS[idx];
    }
    void recordMeasuredPair(int heading,int fraction,int condition,float gain){
        if(heading<0||heading>=8||fraction<0||fraction>=4||
           condition<0||condition>=3||!Float.isFinite(gain))
            throw new IllegalArgumentException("not real physical motor observation");
        if(finalized[heading]||observed[heading][fraction][condition])
            throw new IllegalStateException("no repeated or post-final evidence");
        physicalGain[heading][fraction][condition]=gain;
        observed[heading][fraction][condition]=true;
    }
    float provisional(int heading){
        if(heading<0||heading>=8||finalized[heading])
            throw new IllegalArgumentException("motor category");
        float best=0f;float maxWorst=MIN_REPRODUCIBLE_GAIN;
        for(int fraction=0;fraction<4;fraction++){
            float worst=Float.POSITIVE_INFINITY;
            for(int condition=0;condition<3;condition++){
                if(!observed[heading][fraction][condition]){
                    worst=Float.NEGATIVE_INFINITY;break;
                }
                worst=Math.min(worst,physicalGain[heading][fraction][condition]);
            }
            if(worst>maxWorst+.0001f){
                best=FRACTIONS[fraction];maxWorst=worst;
            }
        }
        return best; // zero if no INDEPENDENT evidence, no innate heading
    }
    void confirmHeldout(int heading,float candidate,float observedGain){
        if(heading<0||heading>=8||finalized[heading]||
           !Float.isFinite(candidate)||!Float.isFinite(observedGain)||
           candidate!=provisional(heading))
            throw new IllegalArgumentException("unproven motor efficacy");
        // An independent holdout is necessary but never sufficient without all
        // three physically distinct training contexts agreeing in sign.
        selected[heading]=candidate>0f&&observedGain>MIN_REPRODUCIBLE_GAIN
                ?candidate:0f;
        finalized[heading]=true;
    }
    float approved(int heading){
        if(heading<0||heading>=8)throw new IllegalArgumentException("heading");
        return finalized[heading]?selected[heading]:0f;
    }
    boolean ready(int heading){
        if(heading<0||heading>=8)throw new IllegalArgumentException("heading");
        return finalized[heading];
    }
}
