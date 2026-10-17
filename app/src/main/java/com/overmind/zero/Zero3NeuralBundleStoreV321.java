package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * V3.21 research neural bundle: recurrent actor + 64-channel motor cortex +
 * 16-sector contextual adhesion head. One atomic checkpoint, not 3 torn files.
 *
 * NOT a full organism BrainSnapshotV3: body, world, sensory buffer, replay,
 * structural growth and Android save-profile remain external/unimplemented.
 */
public final class Zero3NeuralBundleStoreV321 {
    static final int MAGIC=0x5A333231,VERSION=321,MAX_SIZE=32768;
    public static final class Bundle {
        public final int nextEpisode;
        public final Zero3AutonomousLoopV317.Encoder.State encoder;
        public final Zero3LearningFoundation.AdaptiveCortex.State cortex;
        public final Zero3ContextAdhesionV319.State adhesive;
        Bundle(int next,Zero3AutonomousLoopV317.Encoder.State a,
               Zero3LearningFoundation.AdaptiveCortex.State c,
               Zero3ContextAdhesionV319.State h){
            if(next<1||next>100000||a==null||c==null||h==null||
                !h.training||h.decisions!=next*96||a.updates!=h.decisions)
                throw new IllegalArgumentException("V321 inconsistent trained module counters");
            nextEpisode=next;encoder=a;cortex=c;adhesive=h;
        }
        public void restore(Zero3AutonomousLoopV317.Encoder e,
                            Zero3LearningFoundation.AdaptiveCortex c,
                            Zero3ContextAdhesionV319 h){
            if(e==null||c==null||h==null)
                throw new IllegalArgumentException("missing neural module");
            // Preflight EVERY subsystem before mutating ANY weights. A
            // mismatched rate or busy motor must not half-restore the brain.
            Zero3AutonomousLoopV317.Encoder.State freshEncoder=e.checkpoint();
            Zero3LearningFoundation.AdaptiveCortex.State freshCortex=c.checkpoint();
            Zero3ContextAdhesionV319.State freshAdhesive=h.captureAtDecisionBoundary();
            if(freshEncoder.steps!=0||freshEncoder.updates!=0||
               freshCortex.revision!=0||freshCortex.updates!=0||
               Float.floatToIntBits(freshCortex.learningRate())!=
                  Float.floatToIntBits(cortex.learningRate())||
               freshAdhesive.decisions!=0||freshAdhesive.realCreditEvents!=0||
               freshAdhesive.training!=adhesive.training)
                throw new IllegalStateException("neural restore cannot atomically preflight all modules");
            e.restore(encoder);c.restore(cortex);h.restoreAtDecisionBoundary(adhesive);
        }
    }
    static Bundle capture(int completedEpisode,
                          Zero3AutonomousLoopV317.Encoder e,
                          Zero3LearningFoundation.AdaptiveCortex c,
                          Zero3ContextAdhesionV319 h){
        return new Bundle(completedEpisode,e.checkpoint(),
                          c.checkpoint(),h.captureAtDecisionBoundary());
    }
    static byte[] encode(Bundle bundle)throws IOException{
        ByteArrayOutputStream b=new ByteArrayOutputStream(MAX_SIZE);
        DataOutputStream d=new DataOutputStream(b);
        d.writeInt(MAGIC);d.writeInt(VERSION);d.writeInt(16);d.writeInt(20);
        d.writeInt(64);d.writeInt(8);d.writeInt(16);d.writeInt(8);
        d.writeInt(bundle.nextEpisode);
        bundle.encoder.write(d);bundle.cortex.write(d);
        byte[] motor=Zero3MotorHeadStoreV320.encode(bundle.adhesive);
        d.writeInt(motor.length);d.write(motor);d.flush();
        byte[] data=b.toByteArray();
        if(data.length>MAX_SIZE)throw new IOException("neural bundle oversized");
        return data;
    }
    static Bundle decode(byte[] data)throws IOException{
        if(data.length<100||data.length>MAX_SIZE)
            throw new IOException("neural bundle length");
        try{
            DataInputStream d=new DataInputStream(new ByteArrayInputStream(data));
            if(d.readInt()!=MAGIC||d.readInt()!=VERSION||
               d.readInt()!=16||d.readInt()!=20||
               d.readInt()!=64||d.readInt()!=8||
               d.readInt()!=16||d.readInt()!=8)
                throw new IOException("neural topology/version mismatch");
            int next=d.readInt();
            Zero3AutonomousLoopV317.Encoder.State e=
                Zero3AutonomousLoopV317.Encoder.State.read(d);
            Zero3LearningFoundation.AdaptiveCortex.State c=
                Zero3LearningFoundation.AdaptiveCortex.State.read(d);
            int length=d.readInt();
            if(length!=569)throw new IOException("unexpected motor-head shape");
            byte[] h=new byte[length];d.readFully(h);
            if(d.available()!=0)throw new IOException("trailing brain bytes");
            return new Bundle(next,e,c,Zero3MotorHeadStoreV320.decode(h));
        }catch(IllegalArgumentException|IllegalStateException exception){
            throw new IOException("corrupted neural state",exception);
        }
    }
    private static byte[] sha(byte[] bytes){
        try{return MessageDigest.getInstance("SHA-256").digest(bytes);}
        catch(Exception e){throw new IllegalStateException(e);}
    }
    private final Path dir;
    private int active=-1;
    private long generation;
    private byte[] verified;
    public Zero3NeuralBundleStoreV321(Path path){
        if(path==null)throw new IllegalArgumentException("missing lab-only directory");
        dir=path;
    }
    private Path data(int s){return dir.resolve("v321-neural-"+s+".bin");}
    private Path manifest(boolean primary){return dir.resolve(primary?"v321-current.mf":"v321-previous.mf");}
    private static void atomic(Path target,byte[] bytes)throws IOException{
        Path tmp=target.resolveSibling(target.getFileName()+".tmp");
        try{
            try(FileOutputStream f=new FileOutputStream(tmp.toFile())){
                f.write(bytes);f.flush();f.getFD().sync();
            }
            Files.move(tmp,target,StandardCopyOption.ATOMIC_MOVE,
                       StandardCopyOption.REPLACE_EXISTING);
        }finally{Files.deleteIfExists(tmp);}
    }
    private static byte[] pointer(int slot,long gen,byte[] data)throws IOException{
        ByteArrayOutputStream b=new ByteArrayOutputStream();
        DataOutputStream out=new DataOutputStream(b);
        out.writeInt(MAGIC);out.writeInt(VERSION);
        out.writeInt(slot);out.writeLong(gen);out.writeInt(data.length);
        out.write(sha(data));out.flush();
        out.write(sha(b.toByteArray()));out.flush();
        return b.toByteArray();
    }
    private static final class Recovery {
        final int slot;final long gen;final byte[] raw;final Bundle bundle;
        Recovery(int slot,long gen,byte[] bytes,Bundle bundle){
            this.slot=slot;this.gen=gen;raw=bytes;this.bundle=bundle;
        }
    }
    private Recovery verified(Path manifest)throws IOException{
        byte[] m=Files.readAllBytes(manifest);
        if(m.length!=4+4+4+8+4+32+32||
           !Arrays.equals(sha(Arrays.copyOf(m,m.length-32)),
                          Arrays.copyOfRange(m,m.length-32,m.length)))
            throw new IOException("manifest integrity failure");
        DataInputStream in=new DataInputStream(new ByteArrayInputStream(m));
        if(in.readInt()!=MAGIC||in.readInt()!=VERSION)
            throw new IOException("manifest version");
        int slot=in.readInt();long gen=in.readLong();int length=in.readInt();
        byte[] hash=new byte[32];in.readFully(hash);
        if(slot<0||slot>1||gen<=0||length<100||length>MAX_SIZE)
            throw new IOException("manifest invalid range");
        byte[] b=Files.readAllBytes(data(slot));
        if(b.length!=length||!Arrays.equals(sha(b),hash))
            throw new IOException("neural payload integrity failure");
        return new Recovery(slot,gen,b,decode(b));
    }
    public Bundle load()throws IOException{
        Recovery r=null;
        try{r=verified(manifest(true));}catch(IOException ignored){}
        if(r==null)try{r=verified(manifest(false));}catch(IOException ignored){}
        if(r==null){active=-1;generation=0;verified=null;return null;}
        active=r.slot;generation=r.gen;verified=r.raw;
        return r.bundle;
    }
    public void save(Bundle b)throws IOException{
        byte[] encoded=encode(b);
        if(!Arrays.equals(encoded,encode(decode(encoded))))
            throw new IOException("bundle roundtrip noncanonical");
        Files.createDirectories(dir);
        if(generation>0){
            if(verified==null||!Arrays.equals(Files.readAllBytes(data(active)),verified))
                throw new IOException("last verified neural generation disappeared");
        }
        int target=active==0?1:0;
        atomic(data(target),encoded);
        if(generation>0)
            atomic(manifest(false),pointer(active,generation,verified));
        atomic(manifest(true),pointer(target,generation+1,encoded));
        generation++;active=target;verified=encoded;
    }
    public long generation(){return generation;}
    public Path activeFileForTest(){return data(active);}
    public Path oldFileForTest(){return data(1-active);}
    public Path currentManifestForTest(){return manifest(true);}
    public static String fingerprint(Bundle b)throws IOException{
        StringBuilder out=new StringBuilder();
        for(byte v:sha(encode(b)))out.append(String.format("%02x",v&255));
        return out.toString();
    }
}
