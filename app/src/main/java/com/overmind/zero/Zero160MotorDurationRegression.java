package com.overmind.zero;

/** Real observation-only motor-duration credit, holdout and safety regression. */
public final class Zero160MotorDurationRegression {
    private static void ok(boolean condition,String message){
        if(!condition)throw new AssertionError(message);
    }
    private static float[] scores(float a,float b){
        float[] s=new float[RecurrentCoreV22.A];
        s[a>b?0:1]=Math.max(a,b);
        return s;
    }
    public static void main(String[] args){
        MotorDurationV22 model=new MotorDurationV22();
        float[] ranked=new float[RecurrentCoreV22.A];
        ranked[5]=.05f;ranked[6]=.03f;
        int repeat=0,failed=0;
        for(int life=0;life<80;life++){
            model.resetLife();
            float x=0;
            int chosen=model.select(5,5,ranked,false,.75f,0f,x,0);
            ok(chosen==5,"Duration must NEVER choose its own direction");
            for(int i=0;i<4;i++){
                x+=4f;
                model.interval(chosen,x,0,0f,0f,.002f,0f);
                int following=model.select(6,5,ranked,false,.70f,0f,x,0);
                if(following==5)repeat++;
                if(following==6)failed++;
                chosen=following;
            }
            // On a fresh life, a genuinely harmful contact MUST interrupt a
            // long motor option; no forced near-death persistence.
            model.resetLife();
            chosen=model.select(4,4,ranked,false,.75f,0f,0,0);
            model.interval(chosen,3,0,-7f,7f,.02f,0f);
            int stop=model.select(6,6,ranked,false,.75f,7f,3,0);
            ok(stop==6,"Known harmful contact failed to interrupt motor burst");
        }
        ok(repeat>0,"No continuous motor actuation sampled");
        ok(failed>0,"All motor acts locked in a prescribed duration");
        ok(model.continuations()>0&&model.interruptions()>0,
            "Control decisions lack continuity/interruption evidence");
        ok(model.evaluated()>50,"Real experimental motor sequences not credited");
        int training=0,validation=0;
        for(int a=0;a<RecurrentCoreV22.A;a++)
            for(int k=0;k<MotorDurationV22.OPTIONS;k++){
                training+=model.trained(a,k);
                validation+=model.heldout(a,k);
            }
        ok(training>0&&validation>0,
            "Motor-duration heldout data was not kept out of training");
        String saved=model.encode();
        MotorDurationV22 restored=new MotorDurationV22();
        ok(restored.restore(saved)&&restored.evaluated()==model.evaluated(),
            "Working checkpoint did not preserve learned burst statistics");
        ok(saved.equals(restored.encode()),"Motor-duration bytewise roundtrip failed");
        ok(!restored.restore("md160|0|0|0|0|broken")&&
            saved.equals(restored.encode()),
            "Malformed motor checkpoint changed valid learned weights");
        restored.resetLife();
        ok(restored.evaluated()==model.evaluated(),
            "Death deleted learned motor statistics");
        float[] decisive=new float[RecurrentCoreV22.A];
        decisive[1]=.6f;
        for(int i=0;i<100;i++){
            restored.resetLife();
            restored.select(0,0,new float[RecurrentCoreV22.A],false,.7f,0f,0,0);
            int changed=restored.select(1,1,decisive,false,.7f,0f,0,0);
            ok(changed==1,"Superior active 2.2 policy was overridden");
        }
        System.out.println("ZERO160 duration PASS: independent 2.2 action, voluntary multi-tick motor, interruption, heldout and saved memory");
    }
}
