package com.overmind.zero;

import java.nio.file.*;
import java.util.*;
import java.io.*;

public final class Zero3CompletionV354Regression {
    private static void yes(boolean v,String why){if(!v)throw new AssertionError("V354 "+why);}
    private static Zero3RuntimeV325 born(long ep)throws Exception{
        Zero3RuntimeV325 r=Zero3WorldBirthV326.create(ep,0x354AB12);r.enableCompletionV354();r.enableSegmented();return r;
    }
    private static void advance(Zero3RuntimeV325 r,int target)throws Exception{
        while(r.decisions()<target){int n=r.decisions();if(n==32)r.observationModeV354(true);if(n==48)r.observationModeV354(false);
            if(n%32==0)r.spawnBenchEnergyAtMembrane();r.advanceOneDecision();}
    }
    public static void main(String[] args)throws Exception{
        Path root=Files.createTempDirectory("zero354-profiles-");Zero3ProfileStoreV354 profiles=new Zero3ProfileStoreV354(root);byte[] reference;
        try(Zero3RuntimeV325 full=born(354001)){
            advance(full,32);Zero3OrganismStoreV322.State old=full.segmentedSnapshot().organism;
            Zero3RecurrentBrain oldBrain=Zero3RecurrentBrain.fromCheckpoint(old.gruBytes());
            advance(full,48);Zero3RecurrentBrain frozen=Zero3RecurrentBrain.fromCheckpoint(full.segmentedSnapshot().organism.gruBytes());
            yes(Arrays.equals(oldBrain.learningStateV353(),frozen.learningStateV353()),"observe updated actor/GRU/critic/replay");
            yes(oldBrain.cognitionSnapshot().train==frozen.cognitionSnapshot().train&&oldBrain.cognitionSnapshot().heldout==frozen.cognitionSnapshot().heldout,"observe trained model");
            yes(frozen.observationsV354()==16,"observe decisions lost");
            advance(full,96);reference=Zero3OrganismStoreV322.encode(full.segmentedSnapshot().organism);
            Zero3DashboardSnapshotV354 screen=full.dashboardV354(true);
            yes(screen.graph.nodes.length==screen.neurons&&screen.innerX.length==74&&screen.body.membraneX.length==96&&screen.executed.length==64,"provider shape/neuron mismatch");
            yes(screen.graph.actualSynapses>screen.graph.edges.length,"graph needs explicit filter");
            for(int i=0;i<8;i++){Zero3DashboardSnapshotV354 view=full.dashboardV354(true);view.innerX[0]+=100;view.executed[0]=1;}
            yes(Arrays.equals(reference,Zero3OrganismStoreV322.encode(full.segmentedSnapshot().organism)),"dashboard changed organism");
            for(Zero3FullCausalExperienceV322B e:full.segmentedSnapshot().experiences()){
                yes(e.intentionV354()!=null,"intent provenance missing");
                yes((e.partitionV354()==2)==(e.decision>=32&&e.decision<48),"observe provenance changed");
            }
        }
        try(Zero3RuntimeV325 prefix=born(354001)){advance(prefix,48);prefix.saveSegmented(profiles.profile(1));}
        try(Zero3RuntimeV325 resumed=Zero3RuntimeV325.loadSegmented(profiles.profile(1))){
            yes(resumed.observationModeV354(),"observe mode reset on restore");advance(resumed,96);
            yes(Arrays.equals(reference,Zero3OrganismStoreV322.encode(resumed.segmentedSnapshot().organism)),"V354 cold restore differs");resumed.saveSegmented(profiles.profile(1));
        }
        profiles.status(12); // An opened but empty directory remains a valid destination.
        Zero3SegmentedArchiveV328.Bundle parent=new Zero3SegmentedArchiveV328(profiles.profile(1)).load();
        byte[] parentBytes=Zero3OrganismStoreV322.encode(parent.organism);
        Zero3RecurrentBrain parentBrain=Zero3RecurrentBrain.fromCheckpoint(parent.organism.gruBytes());
        Zero3LearningFoundation.AdaptiveCortex parentMotor=new Zero3LearningFoundation.AdaptiveCortex();Zero3ContextAdhesionV319 parentGrip=new Zero3ContextAdhesionV319(1,true);
        parent.organism.restoreGruModules(new Zero3AutonomousLoopV317.Encoder(1),parentMotor,parentGrip);
        try(Zero3RuntimeV325 child=Zero3WorldBirthV354.create(354003,0x354ABE)){
            child.inheritCompletionV354(parent.organism);child.enableSegmented();
            yes(Arrays.equals(parentBrain.parameterSnapshot(),child.recurrentParametersV354()),"new life lost central learned weights");
            yes(Arrays.equals(parentMotor.weights(),child.corticalParametersV354())&&Arrays.equals(parentGrip.captureAtDecisionBoundary().weights(),child.adhesionParametersV354()),"new life lost motor or grip weights");
            yes(child.executiveSnapshot().parentEpisode==parent.organism.episodeId&&child.executiveSnapshot().historicalDecisions==96,"new life lost provenance");
            child.advanceOneDecision();child.saveSegmented(profiles.profile(4));
        }
        try(Zero3RuntimeV325 child=Zero3RuntimeV325.loadSegmented(profiles.profile(4))){yes(child.decisions()==1&&!child.ended()&&child.executiveSnapshot().parentEpisode==354001,"inherited child resume");}
        yes(Arrays.equals(parentBytes,Zero3OrganismStoreV322.encode(new Zero3SegmentedArchiveV328(profiles.profile(1)).load().organism)),"heritage modified original life");
        profiles.cloneProfile(1,12);yes(profiles.status(12).contains("96 decisioni"),"profile clone failed");
        ByteArrayOutputStream backup=new ByteArrayOutputStream();profiles.exportArchive(1,backup);
        profiles.importArchive(2,new ByteArrayInputStream(backup.toByteArray()));yes(profiles.status(2).contains("96 decisioni"),"validated backup import failed");
        boolean protectedSlot=false;try{profiles.importArchive(1,new ByteArrayInputStream(backup.toByteArray()));}catch(IOException correct){protectedSlot=true;}
        yes(protectedSlot,"import overwrote original");
        // Synthetic option protocol only: creates actual samples, never labels phone skills.
        Zero3ExecutiveV354 executive=new Zero3ExecutiveV354();float[] obs=new float[194],action={1,0,.5f,.5f,.45f};
        for(int n=0;n<320;n++){executive.propose(obs,action,n,false);executive.complete(.25f,.01f,0,.01f,Zero3ConfigurationV354.heldout(n),false,null);}
        yes(executive.snapshot().skills.length>0,"no candidate collected from real actor parameters");
        Zero3ExecutiveV354 lifecycle=new Zero3ExecutiveV354();boolean championSeen=false,retiredSeen=false;
        for(int n=0;n<1200;n++){
            lifecycle.propose(obs,action,n,false);boolean recalled=lifecycle.usedMemory();
            float motor=championSeen?(recalled?-.8f:.1f):(recalled?.7f:.1f);
            lifecycle.complete(motor,.01f,0,.01f,Zero3ConfigurationV354.heldout(n),false,null);
            for(Zero3ExecutiveV354.Skill k:lifecycle.snapshot().skills){if(k.state==Zero3ExecutiveV354.CHAMPION)championSeen=true;if(k.state==Zero3ExecutiveV354.RETIRED)retiredSeen=true;}
        }
        yes(championSeen&&retiredSeen,"option A/B probation/retirement was not exercised");
        byte[] options=executive.encode();yes(Arrays.equals(options,Zero3ExecutiveV354.decode(options).encode()),"option lifecycle persistence");
        System.out.println("V354 COMPLETION_INTEGRATION=PASS observerFrozen=true providerReadOnly=true neurons="+
            "actual graph=true slots12=true backupCloneImport=true originalProtected=true wholeLifeRestart=bitwise NO_PHONE_OR_BEHAVIORAL_CERTIFICATION");
    }
}
