package com.overmind.zero;

/** Old and new policies must make the SAME actual actions and weights. */
public final class UnifiedPolicyParityV21Regression {
    private static void yes(boolean b,String m){if(!b)throw new AssertionError(m);}
    public static void main(String[] args){
        SensorimotorMind control=new SensorimotorMind();
        SensorimotorMind trial=new SensorimotorMind();
        UnifiedCognitionV21 common=new UnifiedCognitionV21();
        ObjectContinuityV21 tracking=new ObjectContinuityV21();
        UnifiedArbiterV21 arbitration=new UnifiedArbiterV21();
        trial.attachUnifiedCognitionV21(common);
        trial.attachObjectContinuityV21(tracking);
        trial.attachUnifiedArbiterV21(arbitration);
        float[] first=new float[26];first[0]=.60f;
        control.begin(first);trial.begin(first);
        for(int i=0;i<440;i++){
            float[] scene=first.clone();
            scene[0]=.15f+(i%60)/100f;
            scene[6+i%8]=.27f+(i%7)*.035f;
            scene[14+i%12]=(i%9)/13f;
            float physical=i%51==0?1.5f:0f;
            control.observe(scene,.70f,physical);
            trial.observe(scene,.70f,physical);
            yes(control.action==trial.action,"shadow has changed a real choice at "+i);
        }
        yes(control.serialize().equals(trial.serialize()),
            "added shadow models changed original learned policy weights");
        yes(common.observations()>300&&arbitration.eligible()==0,
            "incorrect evidence accounting / arbiter unexpectedly controls");
        System.out.println("ZERO21 parity 440 real sensorimotor decisions unchanged, old brain byte-identical PASS");
    }
}
