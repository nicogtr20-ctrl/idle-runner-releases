package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * V3.28 real long-life durability experiment: independent Java processes use
 * the ACTUAL V325 neural actor, 64 motor outputs, 96 surface/74 gel inner
 * solver, scaricty ledger and material world. Only persistence is changed.
 *
 * Staged checkpoint boundaries and artificial interruption are set by the
 * benchmark, never by the actor; no motor direction or fake rewards.
 */
public final class Zero3LongLifeV328Regression {
    private static final int TARGET=Integer.parseInt(
        System.getProperty("v328.target","2000"));
    private static final int STOP=TARGET/2,INTERMEDIATE=(TARGET*3)/4;
    private static final long EPISODE=32820261017L+
        Long.parseLong(System.getProperty("v328.ordinal","1"));
    private static final long SEED=0x3282026A11L^EPISODE;
    private static void yes(boolean cond,String why){
        if(!cond)throw new AssertionError("V328 long-life "+why);
    }
    private static Zero3RuntimeV325 birth()throws Exception{
        Zero3RuntimeV325 r=Zero3WorldBirthV326.create(EPISODE,SEED);
        r.enableSegmented();
        return r;
    }
    private static void advance(Zero3RuntimeV325 r,int target,Path directory)
            throws Exception{
        while(r.decisions()<target){
            yes(!r.ended(),
                "physical organism died before "+target+
                " decisions; never fabricate neural credit or metabolic gains");
            int n=r.decisions();
            // World's real state-changing RNG, with no prescribed motor goal.
            if(n==64||n==512||n==1024||n==1536)
                r.spawnDisk(n%1024==0?130f:165f);
            // A controlled test habitat provides physically collectible blue
            // resources; true contact is needed for fuel (not a fake +22).
            // This evaluates STORAGE only, not survival-seeking policy.
            if(n%64==0&&r.energy()<65f)
                r.spawnBenchEnergyAtMembrane();
            r.advanceOneDecision();
            // Checkpoints at established decision boundaries. Reuse immutable
            // sealed segments: no need to rewrite history inside neural blob.
            if(directory!=null&&r.decisions()%128==0)
                r.saveSegmented(directory);
        }
        yes(r.decisions()==target&&r.completeExperiences()==target&&
            r.encoderUpdates()==target&&r.gripUpdates()==target,
            "physics/sensory/neural authority misaligned");
    }
    private static String fingerprint(Zero3RuntimeV325 r)throws Exception{
        Zero3SegmentedArchiveV328.Bundle b=r.segmentedSnapshot();
        MessageDigest sha=MessageDigest.getInstance("SHA-256");
        sha.update(Zero3OrganismStoreV322.encode(b.organism));
        sha.update(b.survival.encoded());sha.update(b.room.encoded());
        for(Zero3FullCausalExperienceV322B e:b.experiences())sha.update(e.encoded());
        return "life="+EPISODE+"\n"+
            "decision="+r.decisions()+"\n"+
            "gelTicks="+r.gelTicks()+"\n"+
            "energyBits="+Float.floatToRawIntBits(r.energy())+"\n"+
            "material="+b.organism.materialHash()+"\n"+
            "worldRng="+r.worldRng()+"\n"+
            "roomFoodIdentity="+r.roomFoodCanonical()+"\n"+
            "experiences="+b.records()+"\n"+
            "fullCausalDigest="+Zero3SegmentedArchiveV328.hex(sha.digest())+"\n";
    }
    private static void evidence(Path out,Zero3RuntimeV325 r)throws Exception{
        Files.write(out,fingerprint(r).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
    static void prefix(Path dir)throws Exception {
        yes(!Zero3SegmentedArchiveV328.hasAny(dir),"unexpected existing V328 life");
        try(Zero3RuntimeV325 r=birth()){
            advance(r,STOP,dir);r.saveSegmented(dir);
            yes(r.completeExperiences()==STOP,"missing startup half");
        }
        Zero3SegmentedArchiveV328 archive=new Zero3SegmentedArchiveV328(dir);
        Zero3SegmentedArchiveV328.Bundle b=archive.load();
        yes(b!=null&&b.records()==STOP&&archive.generation()>1,
            "prefix archives not independently recoverable");
        System.out.println("V328 PREFIX organism="+EPISODE+" decisions="+STOP+
            " manifests="+archive.generation());
    }
    static void resume(Path dir,Path out)throws Exception {
        try(Zero3RuntimeV325 r=Zero3RuntimeV325.loadSegmented(dir)){
            yes(r!=null&&r.episode()==EPISODE&&r.decisions()==STOP,
                "cold load did not restore exact authority");
            advance(r,TARGET,dir);r.saveSegmented(dir);
            evidence(out,r);
        }
        Zero3SegmentedArchiveV328 archive=new Zero3SegmentedArchiveV328(dir);
        Zero3SegmentedArchiveV328.Bundle b=archive.load();
        yes(b!=null&&b.records()==TARGET,"final 2000+ decisions not durable");
        int files=0;
        try(DirectoryStream<Path> stream=Files.newDirectoryStream(dir,
                                       "v328-segment-*.bin")){
            for(Path p:stream)files++;
        }
        yes(files>=Math.ceil((double)TARGET/Zero3SegmentedArchiveV328.CHUNK),
            "missing independently readable replay chunks");
        // Each live manifest remains a compact brain/physical/energy snapshot.
        yes(Files.size(archive.currentManifest())<2*1024*1024,
            "unbounded neural/physics manifest");
        System.out.println("V328 RESUMED_2000_PLUS_EXPERIENCES=PASS decisions="+
            TARGET+" segmentsOnDisk="+files);
    }
    static void reference(Path out)throws Exception {
        try(Zero3RuntimeV325 r=birth()){
            advance(r,TARGET,null);evidence(out,r);
        }
        System.out.println("V328 CONTINUOUS_REFERENCE=PASS decisions="+TARGET);
    }
    static void faults(Path dir)throws Exception{
        Zero3SegmentedArchiveV328 archive=new Zero3SegmentedArchiveV328(dir);
        Zero3SegmentedArchiveV328.Bundle live=archive.load();
        yes(live!=null&&live.records()==TARGET,
            "cannot fault an unverified completed lifetime");
        // An abandoned segment written before the generation manifest is
        // published is invisible to the loader, then safely garbage collected.
        Path orphan=dir.resolve("v328-segment-0123456789-orphan.bin");
        Files.write(orphan,new byte[]{1,2,3,4});
        yes(new Zero3SegmentedArchiveV328(dir).load().records()==TARGET,
            "orphan segment influenced the committed organism");
        archive.pruneOrphans();
        yes(!Files.exists(orphan),"uncommitted physical data still referenced");

        // Clone the two valid generations as an independent fault fixture.
        // Corrupt the *latest replay segment* without altering its manifest.
        // The loader must reject that manifest and recover the older life.
        Path chunkFault=Files.createTempDirectory("v328-segment-tamper-");
        try(DirectoryStream<Path> stream=Files.newDirectoryStream(dir,"v328-*")){
            for(Path p:stream)if(Files.isRegularFile(p))
                Files.copy(p,chunkFault.resolve(p.getFileName()));
        }
        Zero3SegmentedArchiveV328 isolated=
            new Zero3SegmentedArchiveV328(chunkFault);
        yes(isolated.load()!=null&&isolated.generation()==archive.generation(),
            "copied long-life checkpoint differs");
        try(RandomAccessFile f=new RandomAccessFile(isolated.currentTail().toFile(),"rw")){
            f.seek(f.length()/3);f.writeByte(0x19);
        }
        Zero3SegmentedArchiveV328 verifiedFallback=
            new Zero3SegmentedArchiveV328(chunkFault);
        Zero3SegmentedArchiveV328.Bundle tailRecovery=verifiedFallback.load();
        yes(tailRecovery!=null&&tailRecovery.records()<TARGET&&
            tailRecovery.records()>STOP,
            "tampered latest sensory/actuator segment was accepted");
        try(RandomAccessFile f=new RandomAccessFile(
                  verifiedFallback.currentManifest().toFile(),"rw")){
            f.setLength(9);
        }
        yes(new Zero3SegmentedArchiveV328(chunkFault).load()==null,
            "corrupting the only remaining generation resurrected replay");

        Path newest=archive.currentManifest();
        try(RandomAccessFile f=new RandomAccessFile(newest.toFile(),"rw")){
            f.seek(f.length()/2);f.writeByte(0x67);f.setLength(f.length()-3);
        }
        Zero3SegmentedArchiveV328 fallback=
            new Zero3SegmentedArchiveV328(dir);
        Zero3SegmentedArchiveV328.Bundle b=fallback.load();
        yes(b!=null&&b.records()==INTERMEDIATE||
            b!=null&&b.records()<TARGET&&b.records()>STOP,
            "generation fallback did not restore a previously complete life");
        int rollback=b.records();
        // Corrupt the remaining manifest too: never reset/fabricate life.
        try(RandomAccessFile f=new RandomAccessFile(fallback.currentManifest().toFile(),"rw")){
            f.setLength(9);
        }
        boolean failedClosed=false;
        try(Zero3RuntimeV325 invalid=Zero3RuntimeV325.loadSegmented(dir)){
            if(invalid!=null)throw new AssertionError("two invalid generations resurrected ZERO");
        }catch(IOException expected){failedClosed=true;}
        yes(failedClosed,"two invalid manifests silently erased V3 identity");
        System.out.println("V328 FAULT_ROLLBACK=PASS SEGMENT_TAMPER=PASS "+
            "ORPHAN_CHUNK_IGNORED=PASS DOUBLE_BAD_FAIL_CLOSED=PASS "+
            "backedUpDecision="+rollback);
    }
    public static void main(String[] args)throws Exception{
        yes(args.length>=2&&TARGET>=2000,"required long-life invocation");
        Path p=Paths.get(args[1]);
        switch(args[0]){
            case "prefix":prefix(p);break;
            case "resume":resume(p,Paths.get(args[2]));break;
            case "reference":reference(p);break;
            case "fault":faults(p);break;
            default:throw new IllegalArgumentException("V328 phase");
        }
    }
}
