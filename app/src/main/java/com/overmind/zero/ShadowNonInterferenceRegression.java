package com.overmind.zero;

/** Verification only: two exactly equivalent seeded minds must learn and
 * select exactly the same actions with the new read-only shadow enabled. */
public final class ShadowNonInterferenceRegression {
    private static void check(boolean v,String reason){
        if(!v)throw new AssertionError(reason);
    }
    public static void main(String[] args){
        SensorimotorMind original=new SensorimotorMind();
        SensorimotorMind watched=new SensorimotorMind();
        ContextualMotionProbe predictor=new ContextualMotionProbe();
        watched.attachMotionShadow(predictor);
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        original.updateBodySense(body,skin);
        watched.updateBodySense(body,skin);
        float[] sensations=new float[SensorimotorMind.S];
        sensations[0]=.65f;
        check(original.choose(sensations)==watched.choose(sensations),
            "Probe altered the first decision");
        for(int i=0;i<320;i++){
            sensations[0]=.25f+(i%10)*.035f;
            sensations[6]=(i%6)*.10f;
            sensations[15]=(i%5)*.13f;
            body.cx+=(i%2==0?1.25f:-.75f);
            body.cy+=(i%3==0?.8f:-.4f);
            original.observe(sensations,.68f,0f,body,skin);
            watched.observe(sensations,.68f,0f,body,skin);
            check(original.action==watched.action,
                "Action diverged with passive shadow at step "+i);
            check(original.experiences==watched.experiences,
                "Experiences diverged at step "+i);
            check(original.explorations==watched.explorations,
                "Exploratory random stream diverged at step "+i);
        }
        check(original.serialize().equals(watched.serialize()),
            "Passive shadow changed learned policy parameters");
        check(predictor.evaluations>0,"The shadow has not observed real transitions");
        System.out.println("ZERO 0116.4 PASS: 320 real learned transitions, choices, "+
            "random exploration and all old brain weights unchanged");
    }
}