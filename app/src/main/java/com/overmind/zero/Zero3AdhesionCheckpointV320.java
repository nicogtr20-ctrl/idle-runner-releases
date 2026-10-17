package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

/**
 * V3.20: tiny durable CHECKPOINT OF THE V3.19 ADHESION HEAD ONLY.
 *
 * Strict boundaries: no body/world state, no actor memory, no Android saves.
 * Durable two-slot model generations with SHA-256 payload AND manifest,
 * refusing unknown versions/architecture and truncated or trailing bytes.
 *
 * Safe boundary is between two completed neural intentions. Never snapshot
 * an in-flight physics frame or label this a complete brain checkpoint.
 */
public final class Zero3AdhesionCheckpointV320 {
    private static final int MAGIC=0x5A333230,VERSION=320;
    private static final int MAX_BYTES=8192;
    private final Path folder;
    private int currentSlot=-1;
    private long generation;
    public long generation(){return generation;}
    public int activeSlot(){return currentSlot;}
    public Zero3AdhesionCheckpointV320(Path isolated) {
        if(isolated==null)throw new IllegalArgumentException("missing checkpoint directory");
        folder=isolated;
    }
    static void verify(boolean ok,String message) {
        if(!ok)throw new IllegalArgumentException("ZERO3 V320 "+message);
    }
    static byte[] sha(byte[] bytes) {
        try{return MessageDigest.getInstance("SHA-256").digest(bytes);}
        catch(NoSuchAlgorithmException x){throw new IllegalStateException(x);}
    }
    static String fingerprint(byte[] bytes) {
        byte[] hash=sha(bytes);
        StringBuilder out=new StringBuilder(64);
        for(byte b:hash)out.append(String.format(java.util.Locale.ROOT,"%02x",b&255));
        return out.toString();
    }
    static byte[] encode(Zero3ContextAdhesionV319.State state)throws IOException {
        verify(state!=null,"missing motor weights");
        ByteArrayOutputStream bytes=new ByteArrayOutputStream(1024);
        DataOutputStream out=new DataOutputStream(bytes);
        out.writeInt(MAGIC);out.writeInt(VERSION);
        out.writeInt(Zero3ContextAdhesionV319.SECTORS);
        out.writeInt(Zero3ContextAdhesionV319.FEATURES);
        out.writeInt(Float.floatToIntBits(Zero3ContextAdhesionV319.GRIP_CREDIT));
        out.writeInt(Float.floatToIntBits(Zero3ContextAdhesionV319.COST_CREDIT));
        out.writeInt(Float.floatToIntBits(Zero3ContextAdhesionV319.DECAY));
        out.writeInt(Float.floatToIntBits(Zero3ContextAdhesionV319.STEP_RATE));
        out.writeInt(Float.floatToIntBits(Zero3ContextAdhesionV319.ADHESION_WORK_AT_FULL_PER_SECTOR));
        out.writeLong(state.rngState);out.writeBoolean(state.training);
        out.writeInt(state.decisions);out.writeInt(state.realCreditEvents);
        out.writeDouble(state.weightChangeSq);
        out.writeDouble(state.accountedAdhesionWork);
        out.writeDouble(state.observedGelWork);
        float[] weights=state.weights();
        verify(weights.length==Zero3ContextAdhesionV319.SECTORS*
                               Zero3ContextAdhesionV319.FEATURES,"bad weight shape");
        out.writeInt(weights.length);
        for(float w:weights){verify(Float.isFinite(w),"nonfinite weight");out.writeFloat(w);}
        out.flush();
        byte[] payload=bytes.toByteArray();
        verify(payload.length<MAX_BYTES,"head snapshot too large");
        return payload;
    }
    static Zero3ContextAdhesionV319.State decode(byte[] bytes)throws IOException {
        verify(bytes!=null&&bytes.length>0&&bytes.length<MAX_BYTES,"invalid size");
        DataInputStream in=new DataInputStream(new ByteArrayInputStream(bytes));
        verify(in.readInt()==MAGIC&&in.readInt()==VERSION,"schema mismatch");
        verify(in.readInt()==Zero3ContextAdhesionV319.SECTORS&&
               in.readInt()==Zero3ContextAdhesionV319.FEATURES,
               "architecture shape mismatch");
        verify(in.readInt()==Float.floatToIntBits(Zero3ContextAdhesionV319.GRIP_CREDIT)&&
               in.readInt()==Float.floatToIntBits(Zero3ContextAdhesionV319.COST_CREDIT)&&
               in.readInt()==Float.floatToIntBits(Zero3ContextAdhesionV319.DECAY)&&
               in.readInt()==Float.floatToIntBits(Zero3ContextAdhesionV319.STEP_RATE)&&
               in.readInt()==Float.floatToIntBits(
                    Zero3ContextAdhesionV319.ADHESION_WORK_AT_FULL_PER_SECTOR),
               "training rule/physics cost mismatch");
        long rng=in.readLong();
        boolean train=in.readBoolean();
        int updates=in.readInt(),credits=in.readInt();
        double norm=in.readDouble(),adhesion=in.readDouble(),work=in.readDouble();
        int n=in.readInt();
        verify(n==Zero3ContextAdhesionV319.SECTORS*
                  Zero3ContextAdhesionV319.FEATURES,"weight vector size mismatch");
        float[] w=new float[n];for(int i=0;i<n;i++)w[i]=in.readFloat();
        verify(in.available()==0,"trailing model bytes");
        return new Zero3ContextAdhesionV319.State(
            w,rng,train,updates,credits,norm,adhesion,work);
    }
    static final class Manifest {
        final long epoch;
        final int slot,size;
        final byte[] payloadSha;
        Manifest(long generation,int which,int n,byte[] sha){
            epoch=generation;slot=which;size=n;payloadSha=sha.clone();
        }
    }
    static byte[] manifestBytes(Manifest m)throws IOException {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream(100);
        DataOutputStream out=new DataOutputStream(bytes);
        out.writeInt(MAGIC);out.writeInt(VERSION);
        out.writeLong(m.epoch);out.writeInt(m.slot);out.writeInt(m.size);
        out.write(m.payloadSha);out.flush();
        byte[] raw=bytes.toByteArray();
        out.write(sha(raw));out.flush();
        return bytes.toByteArray();
    }
    static Manifest readManifest(byte[] bytes)throws IOException {
        verify(bytes!=null&&bytes.length==4+4+8+4+4+32+32,"manifest length");
        verify(Arrays.equals(sha(Arrays.copyOf(bytes,bytes.length-32)),
                 Arrays.copyOfRange(bytes,bytes.length-32,bytes.length)),
                 "manifest SHA-256 mismatch");
        DataInputStream in=new DataInputStream(new ByteArrayInputStream(bytes));
        verify(in.readInt()==MAGIC&&in.readInt()==VERSION,"manifest version");
        long epoch=in.readLong();int slot=in.readInt(),size=in.readInt();
        byte[] hash=new byte[32];in.readFully(hash);
        verify(epoch>0&&(slot==0||slot==1)&&size>0&&size<MAX_BYTES,
               "manifest invalid bounds");
        return new Manifest(epoch,slot,size,hash);
    }
    private Path payload(int slot){
        return folder.resolve("zero3-v320-head-"+slot+".bin");
    }
    private Path currentManifest(){return folder.resolve("zero3-v320-current.mf");}
    private Path backupManifest(){return folder.resolve("zero3-v320-backup.mf");}
    private static void atomicWrite(Path target,byte[] contents)throws IOException {
        Path tmp=target.resolveSibling(target.getFileName().toString()+".tmp");
        try{
            try(FileOutputStream out=new FileOutputStream(tmp.toFile())){
                out.write(contents);out.flush();out.getFD().sync();
            }
            Files.move(tmp,target,StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING);
        }finally{Files.deleteIfExists(tmp);}
    }
    private void flushDir(){
        try(java.nio.channels.FileChannel channel=
              java.nio.channels.FileChannel.open(folder,StandardOpenOption.READ)){
            channel.force(true);
        }catch(IOException|UnsupportedOperationException ignored){}
    }
    private static final class Verified {
        final Manifest m;
        final Zero3ContextAdhesionV319.State state;
        Verified(Manifest m,Zero3ContextAdhesionV319.State state){
            this.m=m;this.state=state;
        }
    }
    private Verified inspect(Path manifest)throws IOException{
        verify(Files.isRegularFile(manifest),"unconfirmed generation");
        verify(Files.size(manifest)<128,"oversized manifest");
        Manifest m=readManifest(Files.readAllBytes(manifest));
        Path slotFile=payload(m.slot);
        verify(Files.isRegularFile(slotFile)&&Files.size(slotFile)==m.size,
               "missing/truncated neural checkpoint");
        byte[] raw=Files.readAllBytes(slotFile);
        verify(Arrays.equals(sha(raw),m.payloadSha),"neural weights SHA mismatch");
        return new Verified(m,decode(raw));
    }
    public Zero3ContextAdhesionV319.State load()throws IOException{
        // A missing latest generation is NOT permission to select an
        // uncommitted slot: only a validated manifest may be loaded.
        Verified v=null;
        try{v=inspect(currentManifest());}
        catch(IOException|IllegalArgumentException ignored){}
        if(v==null){
            try{v=inspect(backupManifest());}
            catch(IOException|IllegalArgumentException ignored){}
        }
        if(v==null){
            currentSlot=-1;generation=0;
            return null; // fail closed, never fabricate weights
        }
        currentSlot=v.m.slot;generation=v.m.epoch;
        return v.state;
    }
    public void save(Zero3ContextAdhesionV319 motor)throws IOException{
        verify(motor!=null,"missing actual motor head");
        save(motor.captureAtDecisionBoundary());
    }
    public void save(Zero3ContextAdhesionV319.State state)throws IOException{
        byte[] raw=encode(state);
        Files.createDirectories(folder);
        // Before replacing any generation, VALIDATE current chosen rollback
        // payload and compute backup manifest from that verified snapshot.
        byte[] prev=null;
        if(generation>0){
            int slot=currentSlot;
            verify(slot==0||slot==1,"missing active rollback model");
            Path current=payload(slot);
            verify(Files.isRegularFile(current)&&
                   Files.size(current)>0&&Files.size(current)<MAX_BYTES,
                   "invalid rollback checkpoint on disk");
            byte[] verified=Files.readAllBytes(current);
            decode(verified); // rejects wrong version, nonfinite and truncation
            prev=manifestBytes(new Manifest(generation,slot,
                              verified.length,sha(verified)));
        }
        int target=currentSlot==0?1:0;
        Manifest next=new Manifest(generation+1,target,raw.length,sha(raw));
        atomicWrite(payload(target),raw);flushDir();
        if(prev!=null)atomicWrite(backupManifest(),prev);
        atomicWrite(currentManifest(),manifestBytes(next));flushDir();
        currentSlot=target;generation=next.epoch;
    }
    // Test-only fault injection uses these paths, never profile/OTA paths.
    Path activePayloadForTest(){return payload(currentSlot);}
    Path currentManifestForTest(){return currentManifest();}
    Path previousPayloadForTest(){return payload(currentSlot==0?1:0);}
    Path previousManifestForTest(){return backupManifest();}
}
