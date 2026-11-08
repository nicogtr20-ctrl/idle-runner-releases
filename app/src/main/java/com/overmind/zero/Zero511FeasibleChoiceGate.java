package com.overmind.zero;

/**
 * An embodied GRU action must satisfy actual learned physical constraints.
 *
 * ZERO510's double monotonic optical veto prevented all 8,907 S21 choices:
 * turning round a physically verified wall temporarily reduces alignment
 * toward a POSITIVE reward behind it. That is NOT observed harm.
 *
 * Compare only verified negative-wavelength *approach risk*. Positive light
 * utility is an eventual objective, never proof that a wall can be crossed.
 * This class never creates a candidate, target, heading, force, reward or
 * learned fact. Only previously sampled GRU3 alternatives are eligible.
 */
final class Zero511FeasibleChoiceGate {
    static final float PHYSICALLY_BLOCKED=-.065f;
    static final float MIN_RELIEF=.05f;
    static final float MAX_ADDITIONAL_LEARNED_HARM=.012f;
    private Zero511FeasibleChoiceGate(){}
    static final class Audit {
        int considered, blockedOriginal, reliefOffered, hazardVetoed, changed;
    }
    static int choose(boolean confirmed,float[] physicalCost,
            float[] learnedNegativeApproachRisk,int selected,Audit audit){
        if(!confirmed)return selected;
        if(physicalCost==null||learnedNegativeApproachRisk==null||
           physicalCost.length==0||
           physicalCost.length!=learnedNegativeApproachRisk.length||
           selected<0||selected>=physicalCost.length)
            throw new IllegalArgumentException("V511 physical candidate evidence");
        if(audit!=null)audit.considered++;
        for(int i=0;i<physicalCost.length;i++)
            if(!Float.isFinite(physicalCost[i])||
               !Float.isFinite(learnedNegativeApproachRisk[i])||
               learnedNegativeApproachRisk[i]<0f)
                throw new IllegalArgumentException("V511 nonfinite or invalid learned risk");
        final float old=physicalCost[selected];
        if(old>PHYSICALLY_BLOCKED)return selected;
        if(audit!=null)audit.blockedOriginal++;
        int best=selected;
        float bestCost=old;
        float oldRisk=learnedNegativeApproachRisk[selected];
        for(int i=0;i<physicalCost.length;i++){
            if(i==selected||physicalCost[i]<old+MIN_RELIEF)continue;
            if(audit!=null)audit.reliefOffered++;
            // Reject only an INDEPENDENTLY learned harmful approach, never
            // the loss of a hypothetical straight-line shortcut to a reward.
            if(learnedNegativeApproachRisk[i]>
                oldRisk+MAX_ADDITIONAL_LEARNED_HARM){
                if(audit!=null)audit.hazardVetoed++;
                continue;
            }
            if(physicalCost[i]>bestCost){
                best=i;bestCost=physicalCost[i];
            }
        }
        if(best!=selected&&audit!=null)audit.changed++;
        return best;
    }
}
