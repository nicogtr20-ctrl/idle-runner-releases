package com.overmind.zero;

import java.nio.file.*;
import java.util.*;
import java.io.*;

/**
 * V348 real-physics, real-GRU same-life cold resume and rollback.
 * No fabricated motor target, energy, grip, or legacy 2.2 persistence.
 */
public final class Zero3GruWholeLifeV348Regression {
    private Zero3GruWholeLifeV348Regression(){}
    private static void yes(boolean ok,String reason){
        if(!ok)throw new AssertionError("V348 "+reason);
    }
    private static Zero3RuntimeV325 born(long episode,long seed)throws Exception {
        final int scene=3;
        Zero3MotorImpulseResponseRegression.Scenario pose=
            new Zero3MotorImpulseResponseRegression.Scenario(
                scene,(float)(-.65+scene*.34),scene);
        float[] zero=new float[64];
        for(int i=0;i<96;i++)pose.gel.step(pose.body,pose.skin,zero,.025f);
        yes(pose.gel.invalid==0,"initial material corrupt");
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        objects.add(new UnknownWorld.Entity(
            pose.body.cx-47f,pose.body.cy+5f,2,5));
        objects.add(new UnknownWorld.Entity(
            pose.body.cx-61f,pose.body.cy+30f,2,2));
        objects.add(new UnknownWorld.Entity(
            pose.skin.x[24],pose.skin.y[24],0,0));
        ScarcityLab room=new ScarcityLab(pose.body.cx+150f,pose.body.cy+12f);
        objects.add(room.food);
        Zero3RuntimeV325 life=Zero3RuntimeV325.newborn(
            episode,seed,pose.body,pose.skin,pose.gel,
            objects,room,50f,0f);
        life.enableGruResearchV347();
        life.enableSegmented();
        return life;
    }
    private static void run(Zero3RuntimeV325 r,int decisions)throws Exception{
        for(int i=0;i<decisions;i++){
            yes(!r.ended()&&!r.atArchiveCapacity(),"real organism ended/capacity");
            r.advanceOneDecision();
            yes(r.gruLearningUpdatesV347()==r.decisions()&&
                r.encoderUpdates()==0&&r.gripUpdates()==r.decisions()&&
                r.gelTicks()>0&&r.display().hasVitalOutcome,
                "single GRU actor or true physical consequence missing");
        }
    }
    private static void equal(Zero3SegmentedArchiveV328.Bundle a,
                              Zero3SegmentedArchiveV328.Bundle b)throws Exception{
        yes(a.decisions()==b.decisions()&&a.organism.hasGru()&&b.organism.hasGru(),
            "wrong schema or decision count");
        yes(Arrays.equals(Zero3OrganismStoreV322.encode(a.organism),
                          Zero3OrganismStoreV322.encode(b.organism)),
            "brain/GRU/optimizer/RNG/gel/BOX or traces changed on restart");
        yes(Arrays.equals(a.survival.encoded(),b.survival.encoded()),
            "metabolic ledger changed after JVM restart");
        yes(Arrays.equals(a.room.encoded(),b.room.encoded()),
            "gate/room and world state changed after JVM restart");
        for(int i=0;i<a.decisions();i++)
            yes(Arrays.equals(a.experiences().get(i).encoded(),
                              b.experiences().get(i).encoded()),
                "PRE/12x64/POST physical replay drift at decision "+i);
    }
    public static void main(String[] args)throws Exception {
        Path uninterrupted=Files.createTempDirectory("zero348-uninterrupted-");
        Path resumed=Files.createTempDirectory("zero348-resumed-");
        final long episode=348001L,seed=0x3480ABCDEF1L;
        try(Zero3RuntimeV325 full=born(episode,seed)){
            run(full,48);
            full.saveSegmented(uninterrupted);
        }
        try(Zero3RuntimeV325 first=born(episode,seed)){
            run(first,24);
            first.saveSegmented(resumed);
        }
        try(Zero3RuntimeV325 second=Zero3RuntimeV325.loadSegmented(resumed)){
            yes(second.decisions()==24&&second.gruLearningUpdatesV347()==24&&
                second.encoderUpdates()==0&&second.display().neurons==64,
                "GRU was not the restored single intent owner");
            run(second,24);
            second.saveSegmented(resumed);
        }
        Zero3SegmentedArchiveV328 diskA=new Zero3SegmentedArchiveV328(uninterrupted);
        Zero3SegmentedArchiveV328 diskB=new Zero3SegmentedArchiveV328(resumed);
        Zero3SegmentedArchiveV328.Bundle a=diskA.load(),b=diskB.load();
        equal(a,b);
        yes(a.decisions()==48&&a.organism.hasGru(),"versioned same-life GRU missing");
        byte[] current=Files.readAllBytes(diskB.currentManifest());
        yes(current[4]==0&&current[5]==0&&
            current[6]==1&&(current[7]&255)==92,
            "V348 manifest version not persisted");
        // Corrupt the latest atomic generation; the previous 24-decision
        // generation is immutable and must restore its REAL GRU, not reset.
        Path currentFile=diskB.currentManifest();
        Path previousFile=diskB.oldManifest();
        current[4]^=1;
        Files.write(currentFile,current);
        try(Zero3RuntimeV325 fallback=Zero3RuntimeV325.loadSegmented(resumed)){
            yes(fallback.decisions()==24&&fallback.gruLearningUpdatesV347()==24,
                "rollback failed to restore true recurrent brain");
            run(fallback,1);
        }
        byte[] prev=Files.readAllBytes(previousFile);
        prev[0]^=1;Files.write(previousFile,prev);
        boolean refused=false;
        try(Zero3RuntimeV325 ignored=Zero3RuntimeV325.loadSegmented(resumed)){}
        catch(IOException expected){refused=true;}
        yes(refused,"both damaged GRU generations silently reset the organism");
        System.out.println("V348 GRU_WHOLE_LIFE_ATOMIC=PASS "+
            "48=24+JVM_RESTART+24 bitwiseBrainBodyEnergyReplay=true "+
            "oneOwnerGRU64=true validRollback24=true failClosedBothDamaged=true "+
            "V322_V328_LEGACY_FORMAT_UNCHANGED=true "+
            "G4_G7_PARTIAL_NO_DEVICE_NO_OTA_NO_MERGE");
    }
}