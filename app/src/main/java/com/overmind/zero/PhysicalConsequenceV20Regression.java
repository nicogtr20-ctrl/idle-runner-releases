package com.overmind.zero;

import java.util.Random;

/** Gate E: action/contact/outcome evidence, independent heldout, atomic reset. */
public final class PhysicalConsequenceV20Regression {
    private static void check(boolean yes,String why){
        if(!yes)throw new AssertionError(why);
    }
    private static float[] seen(float contact,float grip){
        float[] v=new float[SensorimotorMind.S];
        v[0]=.6f;v[4]=contact;v[5]=grip;
        for(int i=6;i<v.length;i++)v[i]=.17f+i*.001f;
        return v;
    }
    public static void main(String[] args){
        PhysicalConsequenceV20 learning=new PhysicalConsequenceV20();
        float[] bare=seen(0,0),touch=seen(.08f,0),held=seen(.12f,.08f);
        check(learning.confidence()==0&&!learning.restore(""),
            "A virgin model must not know a physical cause");
        check(Float.isNaN(learning.forecastExternalMovement(-1,bare)),
            "Invalid action must produce no oracle forecast");
        String virgin=learning.encode();
        // True no-op physical events. Grip/touch does not in itself imply motion.
        learning.chosen(1,held);
        learning.consequence(1,0f,true,false);
        check(learning.physicalEvents()==0,
            "Contact alone masqueraded as an external displacement");
        learning.energyOutcome(16f);
        check(learning.unlinkedEnergy()==1&&learning.adjacentEnergy()==0,
            "Directly touched energy was falsely linked to a prior move");
        // Paired, varied actual-action interventions: the same observable
        // tactile context, two different actions and different real effects.
        // This synthetic benchmark validates the estimator's mechanics, not
        // a claim that ZERO has discovered that real-world intervention.
        for(int i=0;i<180;i++){
            learning.chosen(2,touch);
            learning.consequence(2,.85f,true,false);
            learning.resetLife(); // independent lifetimes / no carried trace
            learning.chosen(7,touch);
            learning.consequence(7,0f,true,false);
            learning.resetLife();
        }
        check(learning.forecastExternalMovement(2,touch)>
              learning.forecastExternalMovement(7,touch)+.6f,
            "Could not distinguish learned physical effect from no-op");
        check(learning.physicalEvents()==180,"False movement event count");
        // Positive energy after a measured movement can be temporally
        // associated, not automatically credited to that particular object.
        learning.chosen(2,touch);
        learning.consequence(2,.9f,true,false);
        learning.energyOutcome(22f);
        check(learning.adjacentEnergy()==1,
            "Failed to observe a genuinely delayed physical-energy association");
        learning.resetLife();
        learning.chosen(2,touch);learning.consequence(2,1f,true,false);
        learning.chosen(2,touch);learning.consequence(2,1f,true,false);
        learning.energyOutcome(22f);
        check(learning.ambiguousEnergy()==1,
            "Two possible physical causes must remain ambiguous");
        // Heldout must never update trained empirical physical rates.
        int oldSupported=learning.supportedContexts();
        float oldPrediction=learning.forecastExternalMovement(2,touch);
        int oldTraining=(int)learning.trained();
        for(int i=0;i<240;i++){
            int a=i%2==0?2:7;
            learning.chosen(a,touch);
            learning.consequence(a,a==2?1f:0f,false,true);
        }
        check(learning.trained()==oldTraining&&
              learning.forecastExternalMovement(2,touch)==oldPrediction&&
              learning.supportedContexts()==oldSupported,
              "Heldout experiences leaked into training weights");
        check(learning.validations()>=240&&learning.confidence()>.08f&&
              learning.error()<learning.referenceError()*.8f,
              "Action-conditional model failed heldout same-contact control");
        check(learning.forecastExternalMovement(2,bare)<
              learning.forecastExternalMovement(2,touch),
              "Tactile context was ignored");
        long oldTransition=learning.intervals();
        learning.chosen(2,held);
        learning.consequence(2,Float.NaN,true,false);
        check(learning.intervals()==oldTransition&&learning.badSamples()>0,
              "Nonfinite physical effect fabricated a trained action outcome");
        String stored=learning.encode();
        PhysicalConsequenceV20 saved=new PhysicalConsequenceV20();
        check(saved.restore(stored)&&stored.equals(saved.encode()),
            "Physical consequence save did not roundtrip");
        saved.resetLife();
        check(saved.physicalEvents()==learning.physicalEvents(),
            "Death erased independently supported physical knowledge");
        saved.chosen(2,held);
        saved.resumeLife();
        saved.consequence(2,1f,true,false);
        check(saved.trained()==learning.trained()&&
              saved.physicalEvents()==learning.physicalEvents(),
              "Restored unobserved action altered trained physical evidence");
        String before=saved.encode();
        check(!saved.restore(before+"|extra")&&before.equals(saved.encode()),
            "Trailing corruption damaged good save");
        check(!saved.restore(before.replaceFirst("pe20","pe19"))&&
              before.equals(saved.encode()),"Unknown profile schema accepted");
        String[] parts=before.split("\\|",-1);
        parts[11]="NaN";
        String broken=String.join("|",parts);
        check(!saved.restore(broken)&&before.equals(saved.encode()),
            "Nonfinite state improperly partially restored");
        check(!saved.restore(virgin.substring(0,virgin.length()/2))&&
              before.equals(saved.encode()),
            "Truncated virgin key overwrote trained knowledge");
        // A new observational component must not change any original choice.
        SensorimotorMind baseline=new SensorimotorMind();
        SensorimotorMind watching=new SensorimotorMind();
        PhysicalConsequenceV20 tracer=new PhysicalConsequenceV20();
        watching.attachPhysicalConsequenceV20(tracer);
        float[] first=seen(0,0);
        baseline.resetEpisode(first);watching.resetEpisode(first);
        baseline.choose(first);watching.choose(first);
        Random rng=new Random(2202020);
        for(int i=0;i<440;i++){
            float[] sensed=seen(rng.nextFloat()*.1f,rng.nextFloat()*.08f);
            if(i%59==0){
                baseline.contactOutcome(22f,0);
                watching.contactOutcome(22f,0);
            }
            baseline.observe(sensed,.70f);
            watching.observe(sensed,.70f);
            check(baseline.action==watching.action&&
                  baseline.serialize().equals(watching.serialize()),
                  "Passive cause observer interfered with baseline policy "+i);
        }
        check(tracer.intervals()>400&&tracer.actualEnergyContacts()>2,
            "Observer was not wired to real chosen-action lifetimes");
        System.out.println("ZERO20 Gate E PASS: true movement vs no-op, energy ambiguity, heldout integrity, death/save, 440 old brain parity; "+tracer.summary());
    }
}
