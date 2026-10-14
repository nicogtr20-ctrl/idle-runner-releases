package com.overmind.zero;

import java.util.*;
import java.lang.reflect.*;

public final class RecurrentCoreV22Regression {
    static void yes(boolean b,String m){if(!b)throw new AssertionError(m);}
    static int max(float[] x){int w=0;for(int i=1;i<x.length;i++)if(x[i]>x[w])w=i;return w;}
    public static void main(String[] args)throws Exception{
        // Thousands of unvalidated training samples cannot seize control.
        RecurrentCoreV22 untrusted=new RecurrentCoreV22();
        float[] noChange=new float[26];noChange[0]=.6f;
        for(int i=0;i<2000;i++){
            untrusted.sense(noChange,null,null,null);
            untrusted.chosen(i%16);
            untrusted.sense(noChange,null,null,null);
            untrusted.consequence(0,0,i%5!=0,i%5==0,false);
        }
        float[] priorScores=new float[16];for(int a=0;a<16;a++)priorScores[a]=(a-8)*.07f;
        untrusted.sense(noChange,null,null,null);
        yes(Arrays.equals(priorScores,untrusted.evaluate(priorScores)),
            "Unvalidated recurrent weights overrode a learned legacy policy");
        yes(untrusted.learningBlend()>=.89f,"Untrusted fixture lacks trained samples");
        // OTA130: reproduce REAL checkpoint failure: 16/16 physical heldout
        // models accurate, but energy prediction worse than no-change.
        RecurrentCoreV22 split=new RecurrentCoreV22();
        Field sf=RecurrentCoreV22.class.getDeclaredField("samples");
        Field vf=RecurrentCoreV22.class.getDeclaredField("validation");
        Field mf=RecurrentCoreV22.class.getDeclaredField("modelError");
        Field bf=RecurrentCoreV22.class.getDeclaredField("baselineError");
        Field ef=RecurrentCoreV22.class.getDeclaredField("energyError");
        Field ebf=RecurrentCoreV22.class.getDeclaredField("energyBase");
        Field qf=RecurrentCoreV22.class.getDeclaredField("q");
        for(Field field:new Field[]{sf,vf,mf,bf,ef,ebf,qf})field.setAccessible(true);
        ((int[])sf.get(split))[3]=240;
        ((int[])vf.get(split))[3]=60;
        ((float[])mf.get(split))[3]=.060f;
        ((float[])bf.get(split))[3]=.080f;
        ((float[])ef.get(split))[3]=.012f;
        ((float[])ebf.get(split))[3]=.0002f;
        ((float[])qf.get(split))[3*(RecurrentCoreV22.Z+1)+RecurrentCoreV22.Z]=2f;
        yes(split.confidence(3)>.15f,"Physical heldout confidence was wrongly disabled by energy error");
        yes(split.energyConfidence(3)==0&&split.energyReady()==0,
            "Unproven energy forecasting was allowed into imagination");
        float[] physicalSense=new float[26];physicalSense[0]=.7f;
        split.sense(physicalSense,null,null,null);
        float[] safePrior=new float[16];safePrior[3]=-.01f;
        float[] physicallyGuided=split.evaluate(safePrior);
        yes(Arrays.equals(physicallyGuided,safePrior),
            "Physical confidence without independently validated action OUTCOMES changed expert policy");
        for(int a=0;a<16;a++)if(a!=3)yes(physicallyGuided[a]==safePrior[a],
            "Unvalidated action displaced expert policy");
        yes(Math.abs(split.exploration(.19f)-.19f)<.000001f,
            "Untrusted energy planning altered expert exploration");
        // Reproduce the OTA130 observed transient gate with enormous Q bias:
        // even when holdout energy briefly meets the old MSE threshold,
        // the Q and long horizon must remain SHADOW for live decisions.
        ((float[])ef.get(split))[3]=.0001f;
        ((float[])ebf.get(split))[3]=.001f;
        yes(split.energyConfidence(3)>0,
            "Test fixture did not reproduce transient energy-confidence spike");
        float[] spikeInput=safePrior.clone();spikeInput[3]=-3f;
        float[] spikeOutput=split.evaluate(spikeInput);
        yes(Arrays.equals(spikeInput,spikeOutput),
            "Transient energy confidence overwrote existing effective action preferences");
        yes(Math.abs(split.exploration(.09f)-.09f)<.000001f,
            "Transient energy confidence reduced exploration below experienced policy floor");
        yes(split.confidence(3)>0&&split.energyReady()==1&&split.physicalReady()==1,
            "Core lost independent evidence during protected live decisions");
        // Restore energy-regression fixture before persistence checks.
        ((float[])ef.get(split))[3]=.012f;
        ((float[])ebf.get(split))[3]=.0002f;
        yes(split.plannedChoices()==0,"Legacy planned counter mixed physical-only choices");
        // Synthetic aggregate fixtures must obey checkpoint invariants:
        // per-action sample counts cannot exceed total lived transitions.
        Field livedField=RecurrentCoreV22.class.getDeclaredField("transitions");
        livedField.setAccessible(true);livedField.setLong(split,500L);
        String physicalSave=split.encode();
        RecurrentCoreV22 loadedSplit=new RecurrentCoreV22();
        yes(loadedSplit.restore(physicalSave)&&loadedSplit.energyConfidence(3)==0
            &&Math.abs(loadedSplit.confidence(3)-split.confidence(3))<.0001f,
            "Split confidence lost continuity across save/restore");
        RecurrentCoreV22 core=new RecurrentCoreV22();float[] s=new float[26];s[0]=.5f;s[6]=.4f;
        float[] local=new float[DistributedBodySense.FEATURES],origins=new float[32];
        core.sense(s,local,origins,new float[6]);float[] initial=core.state();
        local[DistributedBodySense.GRIP_HELD]=1;origins[0]=50;
        core.sense(s,local,origins,new float[6]);
        yes(!Arrays.equals(initial,core.state()),"distributed body and origins missing from shared state");
        local[DistributedBodySense.GRIP_HELD]=0;origins[0]=0;
        for(int i=0;i<1800;i++){
            int a=i%16;core.sense(s,local,origins,new float[6]);core.chosen(a);
            float[] next=s.clone();next[0]+=(a==3?.14f:a==5?-.14f:-.005f);next[1]=a==3?.2f:0;
            core.sense(next,local,origins,new float[6]);
            boolean held=i%5==0;core.consequence(a==3?14:a==5?-14:0,a==5?14:0,!held,held,false);
        }
        yes(core.replaySize()==96,"bounded replay including rare events");
        core.sense(s,local,origins,new float[6]);
        float[] scores=core.evaluate(null);
        yes(core.confidence(3)>0,"heldout world evidence never became usable");
        yes(core.plannedChoices()>0,"future model still observer-only");
        yes(max(scores)==3,"learned beneficial action must outrank harm and idle: "+Arrays.toString(scores));
        yes(scores[5]<scores[3],"harm value lost");
        String encoded=core.encode();RecurrentCoreV22 restored=new RecurrentCoreV22();
        yes(restored.restore(encoded)&&restored.encode().equals(encoded),"atomic persistence and state");
        float[] z=core.state(),future=core.imagine(z,3);
        yes(future[0]>z[0]+.02f,"energy did not evolve in imagination");
        yes(!Arrays.equals(Arrays.copyOfRange(z,RecurrentCoreV22.F,RecurrentCoreV22.Z),Arrays.copyOfRange(future,RecurrentCoreV22.F,RecurrentCoreV22.Z)),"imagined recurrence frozen");
        yes(core.encode().equals(encoded),"imagination mutated evidence or persistent state");
        yes(!restored.restore(encoded.substring(0,encoded.length()-7))&&restored.encode().equals(encoded),"corrupt save destroyed good weights");
        int replayBefore=core.replaySize();long replayCount=core.replayUpdates();
        core.chosen(3);core.sense(s,local,origins,new float[6]);core.consequence(14,0,false,true,false);
        yes(core.replaySize()==replayBefore&&core.replayUpdates()==replayCount,"heldout entered replay");
        core.chosen(2);float[] dead=s.clone();dead[0]=0;core.sense(dead,local,origins,new float[6]);core.consequence(0,0,true,false,true);
        long lived=core.transitions();String beforeReset=core.encode();core.resetLife();
        yes(core.transitions()==lived&&core.replaySize()==96,"death erased durable evidence");
        restored.restore(beforeReset);float[] memory=restored.state();restored.resumeLife();
        yes(Arrays.equals(memory,restored.state()),"resume erased recurrent memory");
        Map<String,Object> slot=new LinkedHashMap<>();slot.put(RecurrentCoreV22.KEY,encoded);
        yes(SaveProfileCodec.decode(SaveProfileCodec.encode(slot)).get(RecurrentCoreV22.KEY).equals(encoded),"named slot codec");
        // Identical physics observations at identical decision intervals,
        // skipping redundant per-frame Senses.read must leave legacy identical.
        SensorimotorMind eager=new SensorimotorMind(),lazy=new SensorimotorMind();
        eager.begin(s);lazy.begin(s);
        for(int frame=0;frame<1200;frame++){
            float[] seen=s.clone();seen[6]=.2f+(frame%13)*.01f;
            eager.observe(seen,1f/60f,0);
            lazy.observe(lazy.observationDue(1f/60f)?seen:null,1f/60f,0,null,null,null,null);
            yes(eager.action==lazy.action,"boundary sensor optimization altered actions");
        }
        yes(eager.serialize().equals(lazy.serialize()),"boundary sensor optimization changed legacy learning");
        // OTA132: the recurrent controller owns ALL live action selection.
        // Even two wildly different old TD policies must yield identical
        // action sequences when given identical rc22 state and observations.
        SensorimotorMind virginLegacy=new SensorimotorMind();
        SensorimotorMind experiencedLegacy=new SensorimotorMind();
        experiencedLegacy.restore(eager.serialize());
        RecurrentCoreV22 brainA=new RecurrentCoreV22(),brainB=new RecurrentCoreV22();
        yes(brainA.restore(encoded)&&brainB.restore(encoded),
            "Migrated recurrent state not accessible in both profiles");
        long savedRealTransitions=brainA.transitions();
        virginLegacy.attachActiveCore(brainA);
        experiencedLegacy.attachActiveCore(brainB);
        int same=0;
        for(int i=0;i<120;i++){
            int a=virginLegacy.choose(s),b=experiencedLegacy.choose(s);
            yes(a==b,"Legacy Q policy influenced PRIMARY action at "+i+": "+a+"/"+b);
            same++;
        }
        yes(brainA.primaryDecisionsSession()==120&&brainB.primaryDecisionsSession()==120,
            "Primary does not own every decision");
        yes(brainA.summary().contains("ZERO 2.2 PRIMARY:"),
            "Brain incorrectly described as passive shadow");
        float[] primaryScores=brainA.primaryScores(null);
        yes(max(primaryScores)==3,
            "A genuinely learned positive action is lost by the sole arbiter");
        yes(brainA.primaryExploration(.01f)>brainA.primaryExploration(.9f),
            "Low energy must increase the PRIMARY learner's own experimentation");
        yes(core.physicalReady()>0,"Predictive world learning was lost");
        yes(brainA.transitions()==savedRealTransitions,
            "Non-executed policy queries fabricated real experience");
        System.out.println("PASS ZERO22 OTA132: recurrent PRIMARY owns 120/120 live choices independent of prior TD, intact saves, sensorimotor evidence, real recurrence, imagination, rare replay, terminal, corruption");

    }
}
