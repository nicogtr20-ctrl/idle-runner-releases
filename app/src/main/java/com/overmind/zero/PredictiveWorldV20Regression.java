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
        check(!loaded.restore(beforeCorruption.replaceFirst("pw21","pw19"))&&
            beforeCorruption.equals(loaded.encode()),
            "Invalid version modified learned state");
        String broken=beforeCorruption.replaceFirst("\\|0\\.0\\|","|NaN|");
        check(!loaded.restore(broken)&&beforeCorruption.equals(loaded.encode()),
            "Nonfinite state corrupted previously learned weights");
        // Read and preserve the exact previous v20 weight schema. A legacy
        // profile MUST NOT wipe the user's learned old motor or brain.
        String[] parts=saved.split("\\|",-1);
        StringBuilder old=new StringBuilder("pw20");
        for(int i=1;i<7+PredictiveWorldV20.WEIGHTS;i++)
            old.append('|').append(parts[i]);
        PredictiveWorldV20 migrated=new PredictiveWorldV20();
        check(migrated.restore(old.toString())&&
            migrated.trained()==model.trained()&&
            migrated.validations()==model.validations()&&
            migrated.encode().startsWith("pw21|")&&
            migrated.evidenceForAction(2)==0,
            "Optional pw20->pw21 migration lost weights/counters or hallucinated readiness");
        migrated.resetLife();
        check(Math.abs(migrated.predictedObservation(2,now)[7]-now[7])<1e-6f,
            "Uncalibrated migrated profile fabricated motion from old weights");
        float[] blankSpectral=now.clone();
        for(int k=14;k<26;k++)blankSpectral[k]=0f;
        float[] noPhantom=model.predictedObservation(2,blankSpectral);
        for(int k=14;k<26;k++)
            check(noPhantom[k]==0f,
                "Predictor hallucinated spectral resource in a completely unseen band");
        // A stationary sensor stream should learn NO action-caused changes.
        PredictiveWorldV20 stationary=new PredictiveWorldV20();
        for(int i=0;i<2000;i++){
            float[] still=scenario(r,i%2);
            stationary.chosen(2,still);
            stationary.consequence(still,2,i%5!=0,i%5==0);
        }
        float[] noChange=stationary.predictedObservation(2,now);
        check(noChange!=null&&Math.abs(noChange[7]-now[7])<.005f&&
            stationary.confidence()==0&&stationary.nonChangeReadouts()>0,
            "Selective model hallucinated dynamics in a no-change world");
        // Cross-life/heldout examples may change EVALUATION counters but
        // cannot alter training-only sample count or change-rate weights.
        int trainEvidence=stationary.evidenceForAction(2);
        stationary.resetLife();
        stationary.chosen(2,now);
        stationary.consequence(after(now,2),2,false,true);
        check(stationary.evidenceForAction(2)==trainEvidence,
            "Holdout leaked outcome into selective train-only motion gate");
        // Most important integration property: the original learned model,
        // motor decisions and exploration RNG must remain BIT-FOR-BIT unchanged
        // while the optional ZERO 2.0 brain is merely observing.
        SensorimotorMind baseline=new SensorimotorMind();
        SensorimotorMind experimental=new SensorimotorMind();
        PredictiveWorldV20 shadow=new PredictiveWorldV20();
        experimental.attachPredictiveWorldV20(shadow);
        Random stimuli=new Random(781222);
        float[] initial=scenario(stimuli,0);
        baseline.resetEpisode(initial);
        experimental.resetEpisode(initial);
        baseline.choose(initial);experimental.choose(initial);
        check(baseline.action==experimental.action,
            "Virgin shadow module altered first live action");
        for(int i=0;i<360;i++){
            float[] sensed=scenario(stimuli,i%16);
            // Mix known and newly observed outcomes; must not change the old
            // world model or the old action RNG in the shadow branch.
            baseline.observe(sensed,.70f);
            experimental.observe(sensed,.70f);
            check(baseline.action==experimental.action,
                "ZERO 2.0 shadow altered decision "+i);
            check(baseline.serialize().equals(experimental.serialize()),
                "ZERO 2.0 shadow corrupted baseline learned weights "+i);
        }
        check(shadow.transitions()>300,
            "Shadow model did not receive genuine chosen action intervals");
        // True embodiment A/B, not synthetic sensor injection: same body,
        // 96-point membrane, energy/hazard/gate challenges, seeds and motors.
        // Shadow learning MUST NEVER affect the executed path or earned energy.
        BehavioralLaboratory.Profile profile=new BehavioralLaboratory.Profile();
        float maxPathDifference=0f;
        int physicalPairs=0;
        for(int scene=0;scene<BehavioralLaboratory.CASES;scene++){
            for(int seed:BehavioralLaboratory.VARIANTS){
                profile.shadowWorldV20=false;
                BehavioralLaboratory.Trial oldZero=BehavioralLaboratory.simulate(
                    profile,scene,seed,true,null);
                profile.shadowWorldV20=true;
                BehavioralLaboratory.Trial zero20=BehavioralLaboratory.simulate(
                    profile,scene,seed,true,null);
                check(oldZero!=null&&zero20!=null,"Physical scene not completed");
                float distance=Math.abs(oldZero.traveled-zero20.traveled);
                maxPathDifference=Math.max(maxPathDifference,distance);
                check(distance<.0001f&&
                      Math.abs(oldZero.gain-zero20.gain)<.0001f&&
                      Math.abs(oldZero.damage-zero20.damage)<.0001f&&
                      oldZero.actualDecisions==zero20.actualDecisions&&
                      oldZero.energeticContacts==zero20.energeticContacts&&
                      oldZero.damagingContacts==zero20.damagingContacts&&
                      oldZero.gateOpenings==zero20.gateOpenings&&
                      oldZero.gatePassages==zero20.gatePassages,
                      "Physically lived control vs ZERO 2.0 shadow diverged in "+
                       "scene "+scene+" seed "+seed);
                physicalPairs++;
            }
        }
        check(physicalPairs==9,"Unmatched physical pairs");
        System.out.println("ZERO 2.0 GATE A PASS: action-specific forecasts, validated holdout, no motor outputs, death/resume, corrupt/old save isolation; "+loaded.summary());
    }
}
