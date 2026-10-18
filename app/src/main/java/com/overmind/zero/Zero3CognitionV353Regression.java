package com.overmind.zero;

import java.util.*;
import java.nio.file.*;

/** Physical integration plus explicitly synthetic neural isolation/codec probes. */
public final class Zero3CognitionV353Regression {
    private static void yes(boolean ok,String why){if(!ok)throw new AssertionError("V353 "+why);}
    private static Zero3RuntimeV325 born(long episode)throws Exception{
        Zero3RuntimeV325 r=Zero3WorldBirthV326.create(episode,0x353DEA11L);
        r.enableCognitiveResearchV353();r.enableSegmented();return r;
    }
    private static void advance(Zero3RuntimeV325 r,int stop)throws Exception{
        while(r.decisions()<stop){
            // Real material at membrane is a storage fixture, never survival evidence.
            if(r.decisions()%16==0)r.spawnBenchEnergyAtMembrane();
            r.advanceOneDecision();
        }
    }
    public static void main(String[] args)throws Exception{
        Zero3CognitionV353 model=new Zero3CognitionV353();
        float[] x=new float[194],a={1,0,.5f,.5f,.45f},target=new float[12];
        yes(model.select(x,new float[][]{a,a,a})==0&&model.snapshot().plannerUses==0,"unvalidated planner active");
        for(int n=0;n<1600;n++){
            x[0]=(float)Math.sin(n*.21);target[0]=x[0]*x[0];target[1]=x[0]*.2f;
            boolean heldout=Zero3CognitionV353.heldout(n);
            float[] weights=model.weights();
            model.begin(x,a);model.complete(target,heldout);
            if(heldout)yes(Arrays.equals(weights,model.weights()),"validation trained or promoted topology");
        }
        Zero3CognitionV353.Snapshot stats=model.snapshot();
        yes(stats.train==1280&&stats.heldout==320&&stats.episodicRecords==64,"bounded evidence counters");
        yes(stats.growthAccepted+stats.growthRejected>0,"shadow growth was never tested");
        yes(stats.predictiveUnits==36+6*stats.growthAccepted&&stats.predictiveUnits<=60,"fictional neuron count");
        byte[] bytes=model.encode();Zero3CognitionV353 restored=Zero3CognitionV353.decode(bytes);
        yes(Arrays.equals(bytes,restored.encode()),"cognition roundtrip");
        for(int n=0;n<40;n++)for(Zero3CognitionV353 c:new Zero3CognitionV353[]{model,restored}){
            c.begin(x,a);c.complete(target,Zero3CognitionV353.heldout(n));
        }
        yes(Arrays.equals(model.encode(),restored.encode()),"growth/evidence resume changed future");
        byte[] bad=bytes.clone();bad[0]^=1;boolean rejected=false;
        try{Zero3CognitionV353.decode(bad);}catch(java.io.IOException expected){rejected=true;}
        yes(rejected,"unknown cognition schema accepted");
        Zero3RecurrentBrain guarded=new Zero3RecurrentBrain(194,true);guarded.enableCognitionV353();
        guarded.choose(x,353899,0,0);boolean missing=false;
        try{guarded.consequenceObservedV352(x,0,false,.45f,false,false);}catch(IllegalStateException correct){missing=true;}
        yes(missing,"missing physical targets silently credited");guarded.physicalTargetsV353(target,false);
        boolean wrongPartition=false;
        try{guarded.consequenceObservedV352(x,0,true,.45f,false,false);}catch(IllegalStateException correct){wrongPartition=true;}
        yes(wrongPartition,"changed heldout partition silently credited");
        guarded.consequenceObservedV352(x,0,false,.45f,false,false);
        // Neural holdout includes target critics/value replay and recurrent parameters.
        Zero3RecurrentBrain brain=new Zero3RecurrentBrain(194,true);brain.enableCognitionV353();
        for(int n=0;n<10;n++){
            boolean heldout=Zero3CognitionV353.heldout(n);float[] weights=brain.learningStateV353();
            brain.choose(x,353900,n,0);brain.physicalTargetsV353(target,heldout);
            brain.consequenceObservedV352(x,.001f,heldout,.45f,false,false);
            if(heldout)yes(Arrays.equals(weights,brain.learningStateV353()),"GRU holdout trained");
        }
        yes(Arrays.equals(brain.checkpointAtBoundaryV345(),Zero3RecurrentBrain.fromCheckpoint(brain.checkpointAtBoundaryV345()).checkpointAtBoundaryV345()),"353 GRU codec");
        // A real validation decision must freeze both motor learners as well.
        try(Zero3RuntimeV325 motor=born(353002)){
            advance(motor,4);Zero3OrganismStoreV322.State pre=motor.segmentedSnapshot().organism;
            Zero3LearningFoundation.AdaptiveCortex c0=new Zero3LearningFoundation.AdaptiveCortex(),c1=new Zero3LearningFoundation.AdaptiveCortex();
            Zero3ContextAdhesionV319 g0=new Zero3ContextAdhesionV319(1,true),g1=new Zero3ContextAdhesionV319(1,true);
            pre.restoreGruModules(new Zero3AutonomousLoopV317.Encoder(1),c0,g0);
            advance(motor,5);motor.segmentedSnapshot().organism.restoreGruModules(new Zero3AutonomousLoopV317.Encoder(1),c1,g1);
            yes(Arrays.equals(c0.weights(),c1.weights()),"physical heldout trained cortex");
            yes(Arrays.equals(g0.captureAtDecisionBoundary().weights(),g1.captureAtDecisionBoundary().weights()),"physical heldout trained adhesion");
        }
        Path path=Files.createTempDirectory("zero353-full-");byte[] whole;
        try(Zero3RuntimeV325 reference=born(353001)){
            advance(reference,96);Zero3SegmentedArchiveV328.Bundle b=reference.segmentedSnapshot();
            whole=Zero3OrganismStoreV322.encode(b.organism);
            yes(b.organism.cognitiveMode()&&reference.gruLearningUpdatesV347()==77,"physical train/heldout partition");
            for(Zero3FullCausalExperienceV322B e:b.experiences()){
                yes(e.hasPartitionV353()&&e.heldoutV353()==Zero3CognitionV353.heldout(e.decision),"journal lost validation provenance");
            }
            yes(reference.cognitionSnapshot().heldout==19,"physical outcomes not evaluated");
        }
        try(Zero3RuntimeV325 prefix=born(353001)){advance(prefix,48);prefix.saveSegmented(path);}
        try(Zero3RuntimeV325 resumed=Zero3RuntimeV325.loadSegmented(path)){
            advance(resumed,96);
            yes(Arrays.equals(whole,Zero3OrganismStoreV322.encode(resumed.segmentedSnapshot().organism)),"full body/brain/targets/evidence restart differs");
        }
        System.out.println("V353 MULTI_OUTPUT_PHYSICAL_MODEL=PASS heads=12 ensemble=3 heldoutNoWeights=true journalPartition=true fullRestart=bitwise boundedEpisodes=64 growthTrials="+
            (stats.growthAccepted+stats.growthRejected)+" accepted="+stats.growthAccepted+" rejected="+stats.growthRejected+
            " NO_INDEPENDENT_BEHAVIORAL_OR_DEVICE_CERTIFICATION");
    }
}
