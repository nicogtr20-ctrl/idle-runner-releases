package com.overmind.zero;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Temporal credit assignment from PHYSICALLY MEASURED energy changes.
 *
 * The learner observes its own recent (26-sense, action) history. When actual
 * contact changes energy, it updates action-value weights for actions it DID
 * perform, including preceding steps that may have led to that contact.
 *
 * No headings, target coordinates, colors, hidden object laws or "correct"
 * actions enter this component. Positive/negative energy is generic bodily
 * feedback, not a fixed route to food. No contact = no credit update.
 *
 * The learned weights are the preexisting persisted wm1 Q network; the
 * ephemeral 12-step trace is intentionally reset after simulated death.
 */
public final class ExperienceCredit {
    private static final int LENGTH=12;
    private static final float DECAY=.83f;
    private static final float RATE=.12f;
    private final ArrayDeque<Step> history=new ArrayDeque<>();
    public int positiveContacts=0,negativeContacts=0;
    public int updatedActions=0;

    private static final class Step {
        final float[] senses;
        final int action;
        Step(float[] sensed,int performed){
            senses=sensed.clone();action=performed;
        }
    }

    public void newLife(){history.clear();}
    public void restoreCounters(int positives,int negatives,int actions){
        positiveContacts=Math.max(0,positives);
        negativeContacts=Math.max(0,negatives);
        updatedActions=Math.max(0,actions);
    }
    public int traceSize(){return history.size();}
    public void performed(float[] sensed,int action){
        if(sensed==null||sensed.length!=SensorimotorMind.S||
           action<0||action>=SensorimotorMind.ACTIONS)return;
        for(float f:sensed)if(!Float.isFinite(f))return;
        if(history.size()>=LENGTH)history.removeFirst();
        history.addLast(new Step(sensed,action));
    }
    /** Energy delta measured AFTER the actual resource touches the body.
     * Q entries change only for previously selected, physically tried actions.
     */
    public void consequence(float actualEnergyDelta,float[] q){
        if(!Float.isFinite(actualEnergyDelta)||Math.abs(actualEnergyDelta)<.01f||
           q==null||q.length!=SensorimotorMind.VALUE_WEIGHTS||
           history.isEmpty())return;
        float consequence=Math.max(-2f,Math.min(2f,actualEnergyDelta/22f));
        if(consequence>0)positiveContacts++;
        else negativeContacts++;
        float eligibility=1f;
        Iterator<Step> iter=history.descendingIterator();
        while(iter.hasNext()&&eligibility>.08f){
            Step e=iter.next();
            int base=e.action*SensorimotorMind.P;
            float update=RATE*consequence*eligibility;
            for(int k=0;k<SensorimotorMind.S;k++){
                // Let the actual observed visual context learn the action's
                // value, without hand-selecting a "correct" orientation.
                float applied=q[base+k]+update*e.senses[k];
                q[base+k]=Math.max(-4f,Math.min(4f,applied));
            }
            q[base+SensorimotorMind.S]=Math.max(-4f,
                Math.min(4f,q[base+SensorimotorMind.S]+update*.35f));
            updatedActions++;
            eligibility*=DECAY;
        }
        // Do not repeatedly credit the same approach for multiple contacts,
        // and do not let one life carry attribution into another.
        history.clear();
    }

    public static void main(String[] args){
        ExperienceCredit credit=new ExperienceCredit();
        float[] q=new float[SensorimotorMind.VALUE_WEIGHTS];
        float[] visible=new float[SensorimotorMind.S];
        visible[0]=.20f;visible[14+2]=.82f;  // generic observed spectral sector
        credit.performed(visible,3);
        credit.performed(visible,3);
        if(q[3*SensorimotorMind.P+16]!=0||credit.positiveContacts!=0)
            throw new AssertionError("Cannot learn before real contact");
        credit.consequence(22f,q);
        if(!Float.isFinite(q[3*SensorimotorMind.P+16]))
            throw new AssertionError("Nonfinite learned value");
        if(q[3*SensorimotorMind.P+16]<.001f||
           q[2*SensorimotorMind.P+16]!=0)
            throw new AssertionError("Reward must apply to performed action/sensory history only");
        float positive=q[3*SensorimotorMind.P+16];
        float[] danger=visible.clone();danger[14+2]=0;danger[21]=.85f;
        credit.performed(danger,5);
        credit.consequence(-7f,q);
        if(q[5*SensorimotorMind.P+21]>=0||
           q[3*SensorimotorMind.P+16]!=positive)
            throw new AssertionError("Actual harmful contact must teach distinct action");
        if(credit.traceSize()!=0||credit.positiveContacts!=1||
           credit.negativeContacts!=1||credit.updatedActions!=3)
            throw new AssertionError("Temporal credit bookkeeping");
        credit.performed(visible,1);
        credit.newLife();
        credit.consequence(22,q);
        if(credit.positiveContacts!=1)
            throw new AssertionError("Dead life must not transfer action trace");
        credit.restoreCounters(8,3,57);
        if(credit.positiveContacts!=8||credit.negativeContacts!=3||
           credit.updatedActions!=57)throw new AssertionError("Counters not restored");
        System.out.println("ZERO-009.8 autonomy: physical contact trains preceding self-chosen actions; no prewired spectral direction");
    }
}
