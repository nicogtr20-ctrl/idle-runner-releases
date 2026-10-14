package com.overmind.zero;

import java.util.Arrays;

/** Deterministic real-causality-path fixture, with no simulator object oracle. */
public final class CausalSequenceV23Regression {
    private static void ok(boolean v,String message){
        if(!v)throw new AssertionError(message);
    }
    private static final float[] BASE=new float[26];
    private static final float[] SEEN=new float[26];
    static{
        BASE[0]=.55f;BASE[4]=.08f;SEEN[0]=.55f;SEEN[14]=.40f;
    }
    private static void act(CausalSequenceV23 m,RecurrentCoreV22 brain,
                            int command,float[] sight,int id,float confidence,
                            float[] next,int sameTrack,float external,
                            boolean heldout){
        m.chosen(command,sight,id,confidence,brain.state());
        m.consequence(next,sameTrack,external,!heldout,heldout,brain);
    }
    private static void trial(CausalSequenceV23 m,RecurrentCoreV22 brain,
                              int first,boolean beneficial,boolean heldout){
        m.resetLife();
        // The moving object has been touched, seen multiple times and
        // corroborated by independently measured actual external displacement.
        act(m,brain,first,BASE,7,.90f,BASE,7,12,heldout);
        act(m,brain,4,BASE,-1,0f,SEEN,-1,0,heldout);
        if(beneficial){
            m.chosen(4,SEEN,-1,0f,brain.state());
            m.contactEnergy(22,0); // REAL positive consequence-only signal.
            m.consequence(SEEN,-1,0,!heldout,heldout,brain);
        }else{
            for(int i=0;i<64;i++)
                act(m,brain,4,BASE,-1,0f,BASE,-1,0,heldout);
        }
    }
    public static void main(String[] args){
        RecurrentCoreV22 brain=new RecurrentCoreV22();
        brain.sense(BASE,null,null,null);
        CausalSequenceV23 model=new CausalSequenceV23();
        ok(model.confidence()==0&&model.choiceBias(3,BASE,.8f)==0,
            "untrained associations seized control");
        // A brightening optical source is not by itself a cause.
        model.contactEnergy(22,0);
        model.consequence(SEEN,-1,0,true,false,brain);
        ok(model.delayedLinks()==0&&model.missed()==1,
            "energy without object intervention fabricated causal credit");
        // A moved object without independent same-track corroboration is
        // not a legal link, even when optics and energy later change.
        model.resetLife();
        act(model,brain,3,BASE,7,.90f,BASE,-1,12,false);
        act(model,brain,4,BASE,-1,0,SEEN,-1,0,false);
        model.contactEnergy(22,0);
        model.consequence(SEEN,-1,0,true,false,brain);
        ok(model.delayedLinks()==0,"self-motion hallucinated moved object");

        // Train on independent witnessed positive and negative sequences.
        for(int i=0;i<120;i++)trial(model,brain,3,true,false);
        for(int i=0;i<120;i++)trial(model,brain,6,false,false);
        ok(model.trained()==240&&model.delayedLinks()==120,
            "bounded sequence outcomes not trained on true past acts");
        ok(model.confidence()==0,"training evidence alone granted live authority");
        String persisted=model.encode();
        CausalSequenceV23 restored=new CausalSequenceV23();
        ok(restored.restore(persisted)&&restored.encode().equals(persisted),
            "learned multi-decision sequences did not persist across lives");
        ok(!restored.restore("cs23|bad")&&restored.encode().equals(persisted),
            "corrupt save destroyed validated causal memory");

        // Holdout observations are untouched by learner counters/Q and
        // validate independent prediction against the unconditional prior.
        long trained=restored.trained();
        for(int i=0;i<45;i++)trial(restored,brain,3,true,true);
        for(int i=0;i<45;i++)trial(restored,brain,6,false,true);
        ok(restored.trained()==trained,"heldout mutated training statistics");
        ok(restored.validations()==90&&restored.confidence()>0,
            "truly predictive physical->optical->contact sequences never validate");
        // At verified focus and touch, a successful prior action should
        // score above a repeatedly unsuccessful one. No hint in open space.
        float success=restored.choiceBias(3,BASE,.9f);
        float failure=restored.choiceBias(6,BASE,.9f);
        ok(success>0&&failure<0,
            "validated associations cannot affect live learned action utility");
        ok(restored.choiceBias(3,BASE,0f)==0,
            "past object success created universal direction bias");

        // Real energy can train an earlier Q state only AFTER validation;
        // nonmatching, ambiguous or heldout outcomes never claim a cause.
        String before=brain.encode();
        trial(restored,brain,3,true,false);
        ok(!brain.encode().equals(before),"verified real delayed energy never reached Q");
        float[] noLight=BASE.clone();
        restored.resetLife();
        act(restored,brain,3,BASE,7,.9f,BASE,7,12,false);
        act(restored,brain,4,BASE,-1,0,noLight,-1,0,false);
        restored.contactEnergy(22,0);
        restored.consequence(noLight,-1,0,true,false,brain);
        ok(restored.missed()>=2,"uninformative light attributed causal energy");

        // 40 real intervening motor decisions: long-term causal credit
        // must survive well beyond the immediate physical-change frame.
        long oldLinks=restored.delayedLinks();
        restored.resetLife();
        act(restored,brain,3,BASE,7,.9f,BASE,7,12,false);
        act(restored,brain,4,BASE,-1,0,SEEN,-1,0,false);
        for(int k=0;k<40;k++)
            act(restored,brain,4,SEEN,-1,0,SEEN,-1,0,false);
        restored.chosen(4,SEEN,-1,0f,brain.state());
        restored.contactEnergy(22,0);
        restored.consequence(SEEN,-1,0,true,false,brain);
        ok(restored.delayedLinks()==oldLinks+1,
            "41-step visual->contact delay was lost by premature expiry");

        // Two equally eligible object moves are NOT automatically awarded
        // the same energy effect. An ambiguous chain is censored, not a
        // fabricated positive/negative sample for either object.
        long trainedBeforeAmbiguity=restored.trained();
        long ambiguousBefore=restored.ambiguous();
        restored.resetLife();
        act(restored,brain,3,BASE,7,.9f,BASE,7,12,false);
        act(restored,brain,6,BASE,8,.9f,BASE,8,12,false);
        act(restored,brain,4,BASE,-1,0,SEEN,-1,0,false);
        restored.chosen(4,SEEN,-1,0f,brain.state());
        restored.contactEnergy(22,0);
        restored.consequence(SEEN,-1,0,true,false,brain);
        ok(restored.ambiguous()==ambiguousBefore+1&&
           restored.trained()==trainedBeforeAmbiguity,
           "ambiguous competing object moves created false training evidence");

        // Existing experienced 2.2 must remain the sole chooser.
        SensorimotorMind a=new SensorimotorMind(),c=new SensorimotorMind();
        RecurrentCoreV22 coreA=new RecurrentCoreV22(),coreB=new RecurrentCoreV22();
        a.attachActiveCore(coreA);c.attachActiveCore(coreB);
        a.attachCausalSequenceV23(new CausalSequenceV23());
        for(int i=0;i<36;i++)
            ok(a.choose(BASE)==c.choose(BASE),
                "unvalidated sequence changed untouched primary policy");
        System.out.println("PASS OTA136: same-ray-track witnesses, delayed real contact, 64-step temporal memory, heldout confidence, learned pair, persistence, Q-credit, no old policy");
    }
}
