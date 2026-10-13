package com.overmind.zero;

/** A shadow proposal NEVER becomes an action, even under a synthetic test. */
public final class UnifiedArbiterV21Regression {
    private static void yes(boolean b,String msg){if(!b)throw new AssertionError(msg);}
    public static void main(String[] args){
        float[] sensed=new float[26];sensed[0]=.10f;sensed[6]=.65f;
        UnifiedArbiterV21 lab=new UnifiedArbiterV21();
        UnifiedCognitionV21 evidence=new UnifiedCognitionV21();
        PredictiveWorldV20 world=new PredictiveWorldV20();
        FuturePlannerV20 planner=new FuturePlannerV20();
        ObjectContinuityV21 visible=new ObjectContinuityV21();
        for(int i=0;i<400;i++)
            lab.evaluate(sensed,evidence,world,planner,visible);
        yes(lab.checks()==400&&lab.eligible()==0&&lab.abstentions()==400,
            "untrained forecast created policy authority");
        yes(lab.shadowSuggestion()==-1&&lab.confidence()==0,
            "model should abstain without physical validation");
        String snapshot=lab.encode();
        UnifiedArbiterV21 loaded=new UnifiedArbiterV21();
        yes(loaded.restore(snapshot)&&loaded.encode().equals(snapshot),
            "invalid serializable arbiter");
        yes(!loaded.restore("ua21|NaN|2|3|1")&&loaded.encode().equals(snapshot),
            "invalid restore corrupted arbiter state");
        loaded.resetLife();
        yes(loaded.checks()==400&&loaded.shadowSuggestion()==-1,
            "new life erased aggregate evidence or reused imaginary plan");
        System.out.println("ZERO21 arbiter: energy urgency, prediction abstention, no steering and atomic saves PASS");
    }
}
