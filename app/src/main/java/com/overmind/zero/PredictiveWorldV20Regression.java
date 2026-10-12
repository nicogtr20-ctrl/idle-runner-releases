package com.overmind.zero;

import java.util.Random;

/** ZERO 2.0: independent non-oracle model, heldout/persistence + losslessness. */
public final class PredictiveWorldV20Regression {
    private static void check(boolean ok,String message){
        if(!ok)throw new AssertionError(message);
    }
    private static float[] scenario(Random r,int action){
        float[] s=new float[SensorimotorMind.S];
        s[0]=.72f; // Energy physiology is observed, NOT modeled as a goal here.
        for(int i=6;i<s.length;i++)s[i]=.2f+r.nextFloat()*.3f;
        return s;
    }
    private static float[] after(float[] before,int action){
        float[] next=before.clone();
        for(int j=6;j<next.length;j++){
            // Different genuine actions predict DIFFERENT sensory changes;
            // no semantic resource labels or hidden object state involved.
            next[j]+=(action%2==0?.13f:-.13f)*(j%3==0?1f:.75f);
        }
        return next;
    }
    public static void main(String[] argv){
        PredictiveWorldV20 model=new PredictiveWorldV20();
        check(model.transitions()==0&&model.confidence()==0,
            "Virgin model invented evidence");
        check(model.predictedObservation(-1,new float[26])==null,
            "Invalid hypothetical action accepted");
        check(!model.restore(""),"Old save must safely initialize virgin");
        String blank=model.encode();
        model.chosen(1,new float[26]);
        model.consequence(new float[26],1,false,true);
        check(model.transitions()==1&&model.validations()==1&&model.trained()==0,
            "Heldout trained the predictor");
        String onlyValidation=model.encode();
        check(!blank.equals(onlyValidation),"Validation counter did not advance");
        // After real independent lived action samples, readout should have
        // significantly lower error than unchanged-observation baseline.
        model.resetLife();
        Random r=new Random(204020);
        for(int i=0;i<12500;i++){
            int a=i%2==0?2:7;
            float[] observation=scenario(r,a);
            float[] actual=after(observation,a);
            model.chosen(a,observation);
            model.consequence(actual,a,i%6!=0,i%6==0);
        }
        check(model.transitions()==12501&&model.trained()>9000,
            "Action-conditioned learning did not accumulate actual transitions");
        check(model.validations()>2000,
            "Heldout was not separately tracked");
        check(model.confidence()>.05f,
            "Forecast failed to beat unchanged-state heldout baseline: "+model.summary());
        float[] now=scenario(r,2);
        float[] forecast=model.predictedObservation(2,now);
        check(forecast!=null&&forecast.length==now.length&&
            forecast[7]>now[7]+.04f,"Action 2 failed to learn positive physical delta");
        float[] opposite=model.predictedObservation(7,now);
        check(opposite[7]<now[7]-.04f,
            "Action 7 failed to learn negative physical delta");
        check(Math.abs(forecast[0]-now[0])<1e-6f,
            "Shadow model changed physiology instead of only sensed world");
        String saved=model.encode();
        PredictiveWorldV20 loaded=new PredictiveWorldV20();
        check(loaded.restore(saved)&&loaded.encode().equals(saved),
            "World model readouts/counters lost across save");
        long transitions=loaded.transitions();
        loaded.resetLife();
        check(loaded.transitions()==transitions&&loaded.confidence()>0,
            "Death erased lifetime model weights");
        float[] bad=now.clone();bad[8]=Float.NaN;
        loaded.chosen(2,bad);
        loaded.consequence(after(now,2),2,true,false);
        check(loaded.transitions()==transitions&&loaded.invalidSamples()>0,
            "Nonfinite observation fabricated learning");
        loaded.chosen(2,now);
        loaded.resumeLife();
        loaded.consequence(after(now,2),2,true,false);
        check(loaded.transitions()==transitions,
            "Resume fabricated a finished action");
        String beforeCorruption=loaded.encode();
        check(!loaded.restore(beforeCorruption+"|0")&&
            beforeCorruption.equals(loaded.encode()),
            "Truncated/extended serialization modified learned state");
        check(!loaded.restore(beforeCorruption.replaceFirst("pw20","pw19"))&&
            beforeCorruption.equals(loaded.encode()),
            "Invalid version modified learned state");
        String broken=beforeCorruption.replaceFirst("\\|0\\.0\\|","|NaN|");
        check(!loaded.restore(broken)&&beforeCorruption.equals(loaded.encode()),
            "Nonfinite state corrupted previously learned weights");
        System.out.println("ZERO 2.0 GATE A PASS: action-specific forecasts, validated holdout, no motor outputs, death/resume, corrupt/old save isolation; "+loaded.summary());
    }
}
