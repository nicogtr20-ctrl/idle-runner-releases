package com.overmind.zero;

/**
 * ZERO510: decision-time consistency between independent physical experience
 * and the ONE actor's already-sampled actions.
 *
 * V509's post-score rule was ineffective in 9,211 real S21 decisions because
 * it also required the defective world-model total to improve. This gate
 * treats independent real train+heldout BODY obstruction as a negative causal
 * constraint, while genuine learned optical consequences remain inviolable.
 *
 * NOT a new controller, fixed direction, named obstacle, positive reward,
 * target vector, COM force, or persistence format. It may only choose from
 * the actual GRU sample set; cognition and executive still receive the one
 * executed motor action and observe its true consequence.
 */
final class Zero510EmbodiedChoiceGate {
    private Zero510EmbodiedChoiceGate(){}
    static final float MIN_VERIFIED_OBSTRUCTION=.07f;
    static final float MIN_RELIEF=.065f;
    static final float OPTICAL_TOLERANCE=.00001f;

    static int choose(boolean independentlyVerified,float[] physicalCosts,
            float[] learnedOptical,float[] learnedContactValue,int preferred){
        if(!independentlyVerified)return preferred;
        if(physicalCosts==null||learnedOptical==null||learnedContactValue==null||
           physicalCosts.length==0||
           physicalCosts.length!=learnedOptical.length||
           physicalCosts.length!=learnedContactValue.length||
           preferred<0||preferred>=physicalCosts.length)
            throw new IllegalArgumentException("V510 physical candidate evidence shape");
        for(int i=0;i<physicalCosts.length;i++)
            if(!Float.isFinite(physicalCosts[i])||
               !Float.isFinite(learnedOptical[i])||
               !Float.isFinite(learnedContactValue[i]))
                throw new IllegalArgumentException("V510 nonfinite physical evidence");
        float blocked=physicalCosts[preferred];
        if(blocked>-MIN_VERIFIED_OBSTRUCTION)return preferred;
        float relief=Math.max(MIN_RELIEF,-blocked*.20f);
        int best=preferred;
        float bestEvidence=blocked;
        for(int i=0;i<physicalCosts.length;i++){
            if(i==preferred||physicalCosts[i]<blocked+relief||
               learnedOptical[i]+OPTICAL_TOLERANCE<learnedOptical[preferred]||
               learnedContactValue[i]+OPTICAL_TOLERANCE<learnedContactValue[preferred])
                continue;
            // No special direction or speculative model score is inserted.
            // "Less physically blocked" is a verified negative constraint;
            // untested escape paths are hypotheses, not fake successes.
            if(physicalCosts[i]>bestEvidence){
                best=i;bestEvidence=physicalCosts[i];
            }
        }
        return best;
    }
}
