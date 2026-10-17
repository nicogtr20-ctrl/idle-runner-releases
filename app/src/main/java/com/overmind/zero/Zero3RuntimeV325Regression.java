package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * V3.25 independently instantiated SAME dedicated V3 runtime in every phase.
 * Unlike earlier V322-V324 drivers the production-like runtime owns the
 * issuer, motor+grip cortex, solver, causal replay, room and metabolic commit.
 * This fixture provides only initial world geometry and spawn timing.
 */
public final class Zero3RuntimeV325Regression {
    static final int MODE=Integer.parseInt(System.getProperty("zero3.mode","1"));
    static final int ORDINAL=Integer.parseInt(System.getProperty("zero3.ordinal","77"));
    static final long EPISODE=1500000L+ORDINAL;
    static final long WORLD_SEED=0x332250000L^(ORDINAL*0x10000003L)^MODE;
    private Zero3RuntimeV325Regression(){}
    private static void check(boolean ok,String message){
        if(!ok)throw new AssertionError("ZERO3 V325 "+message);
    }
    private static Zero3RuntimeV325 virgin()throws Exception{
        int pose=(ORDINAL+3)%4;
        float angle=(float)(-.73+ORDINAL*.219);
        Zero3MotorImpulseResponseRegression.Scenario s=
            new Zero3MotorImpulseResponseRegression.Scenario(
                pose,angle,ORDINAL%4);
        float[] still=new float[64];
        for(int i=0;i<96;i++)s.gel.step(s.body,s.skin,still,.025f);
        check(s.gel.invalid==0,"initial physical gel invalid");
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        if(MODE!=0){
            float distance=MODE==3?340f:MODE==2?48f:45.9f;
            world.add(new UnknownWorld.Entity(s.body.cx+distance,
                s.body.cy+(ORDINAL%3)*2,2,MODE==2?2:5));
            if(MODE==4)world.add(new UnknownWorld.Entity(
                s.body.cx-47f,s.body.cy+11f,2,2));
        }
        // A real material-source contact at birth only for half the poses.
        if(ORDINAL%4==0||ORDINAL%4==1){
            int patch=20+ORDINAL%10;
            world.add(new UnknownWorld.Entity(s.skin.x[patch],
                s.skin.y[patch],ORDINAL%4==0?0:1,ORDINAL%4==0?0:1));
        }
        ScarcityLab room=new ScarcityLab(s.body.cx+125,s.body.cy);
        world.add(room.food);
        return Zero3RuntimeV325.newborn(EPISODE,WORLD_SEED,
            s.body,s.skin,s.gel,world,room,75f+(ORDINAL%6)*2f,68.7f);
    }
    private static void advance(Zero3RuntimeV325 runtime,int target)throws Exception{
        while(runtime.decisions()<target){
            int n=runtime.decisions();
            if(MODE!=0&&(n==24||n==60))
                runtime.spawnDisk(MODE==3?210f:135f);
            runtime.advanceOneDecision();
        }
        check(runtime.decisions()==target&&runtime.completeExperiences()==target,
              "incomplete physical replay");
    }
    private static String evidence(Zero3RuntimeV325 runtime)throws Exception{
        check(runtime.decisions()==96,"short life");
        Zero3RoomArchiveV324.Bundle bundle=runtime.snapshot();
        return "runtimeWholeHash="+runtime.fingerprint()+
            "\nroomHash="+runtime.currentRoomHash()+
            "\nbodyHash="+bundle.life.joint.organism.materialHash()+
            "\nepisode="+runtime.episode()+
            "\nmode="+MODE+" ordinal="+ORDINAL+
            "\nauthorityNext="+runtime.decisions()+
            "\nencoderUpdates="+runtime.encoderUpdates()+
            "\ncortexUpdates="+runtime.cortexUpdates()+
            "\ngripUpdates="+runtime.gripUpdates()+
            "\nfullReplayRecords="+runtime.completeExperiences()+
            "\nrealGelTicks="+runtime.gelTicks()+
            "\nworldRng="+runtime.worldRng()+
            "\nworldObjects="+runtime.objects()+
            "\nroomFoodIdentity="+runtime.roomFoodCanonical()+
            "\nroomPhysicsContactFrames="+runtime.contactFrames()+
            "\nmetabolicReserveBits="+Float.floatToRawIntBits(runtime.energy())+
            "\nmetabolicScarcity="+bundle.life.survival.scarcity+
            "\nterminal="+runtime.ended()+"\n";
    }
    private static void write(Path p,String result)throws Exception{
        Files.write(p,result.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
    }
    static void prefix(Path dir)throws Exception {
        check(!Files.exists(dir)||new Zero3RoomArchiveV324(dir).load()==null,
              "wrong existing V3 profile");
        try(Zero3RuntimeV325 runtime=virgin()){
            advance(runtime,24);runtime.save(dir);
            advance(runtime,48);runtime.save(dir);
            check(new Zero3RoomArchiveV324(dir).load()!=null,
                  "V325 generation not committed");
            check(new Zero3RoomArchiveV324(dir).generation()==0,
                  "a new store is not loaded until load() is called");
            Zero3RoomArchiveV324 verify=new Zero3RoomArchiveV324(dir);
            check(verify.load()!=null&&verify.generation()==2,
                  "V325 lost previous checkpoint generation");
        }
        System.out.println("ZERO3 V325 PREFIX 48 DECISIONS / 2 VERIFIED GENERATIONS");
    }
    static void resume(Path dir,Path output)throws Exception {
        try(Zero3RuntimeV325 runtime=Zero3RuntimeV325.load(dir)){
            check(runtime!=null&&runtime.decisions()==48,
                  "V325 did not reopen same life at decision 48");
            check(runtime.episode()==EPISODE&&runtime.worldRng()!=0,
                  "foreign runtime episode or world state");
            advance(runtime,96);
            runtime.save(dir);
            write(output,evidence(runtime));
            Zero3RoomArchiveV324 disk=new Zero3RoomArchiveV324(dir);
            check(disk.load()!=null&&disk.generation()==3,"lost gen3 during resume");
        }
        System.out.println("ZERO3 V325 RESUME 96 DECISIONS");
    }
    static void reference(Path result)throws Exception{
        try(Zero3RuntimeV325 runtime=virgin()){
            advance(runtime,96);
            write(result,evidence(runtime));
        }
        System.out.println("ZERO3 V325 REFERENCE 96 DECISIONS");
    }
    static void corrupt(Path dir)throws Exception{
        Zero3RoomArchiveV324 store=new Zero3RoomArchiveV324(dir);
        Zero3RoomArchiveV324.Bundle latest=store.load();
        check(latest!=null&&store.generation()==3,
              "no 3-generation V325 same-life state");
        damage(store.latestFile());
        try(Zero3RuntimeV325 recovered=Zero3RuntimeV325.load(dir)){
            check(recovered!=null&&recovered.decisions()==48&&
                  recovered.completeExperiences()==48&&
                  recovered.encoderUpdates()==48&&recovered.gripUpdates()==48,
                  "rollback fabricated previously uncommitted decision");
            recovered.save(dir);
        }
        Zero3RoomArchiveV324 current=new Zero3RoomArchiveV324(dir);
        check(current.load()!=null&&current.generation()==3,
              "recovery recommit not durable");
        Files.write(current.currentManifest(),new byte[]{2,4,6,8});
        Zero3RoomArchiveV324 previous=new Zero3RoomArchiveV324(dir);
        check(previous.load()!=null&&previous.generation()==2,
              "corrupted current manifest did not fall back");
        damage(previous.latestFile());damage(previous.oldFile());
        check(Zero3RuntimeV325.load(dir)==null,
              "two corrupted generations recovered invalid data");
        System.out.println("ZERO3 V325 FAULT=PASS ROLLBACK_EXACT=48 DOUBLE_BAD_FAIL_CLOSED");
    }
    private static void damage(Path file)throws Exception{
        try(RandomAccessFile f=new RandomAccessFile(file.toFile(),"rw")){
            check(f.length()>300,"short generation");
            f.seek(f.length()/2);f.writeByte(0x56);f.setLength(f.length()-4);
            f.getFD().sync();
        }
    }
    public static void main(String[] args)throws Exception{
        check(args.length>=2&&MODE>=0&&MODE<=4,"invalid V325 runtime phase");
        Path path=Paths.get(args[1]);
        switch(args[0]){
            case "prefix":prefix(path);break;
            case "resume":resume(path,Paths.get(args[2]));break;
            case "reference":reference(path);break;
            case "fault":corrupt(path);break;
            default:throw new IllegalArgumentException("unknown V325 phase");
        }
    }
}
