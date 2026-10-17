package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

/**
 * E2 V3.16: two-generation crash-recoverable experiment log, NOT a brain
 * checkpoint, not Android save-profile, not a neural replay sampler.
 * Full 96x12 + 74x7 + 48 + 16 + 64D PRE/POST are stored, not summaries.
 * No opaque object identities, target directions or synthetic rewards.
 *
 * Safety policy: unsupported atomic rename, corrupt manifest/payload and
 * non-finite/trailing data fail closed or fall back to the previously
 * COMMITTED generation. Uncheckpointed RAM tail is never called durable.
 */
public final class Zero3ExperienceJournalV316 {
    public static final int MAGIC=0x5A333136,VERSION=316,MAX_RECORD=32768;
    public static final int MAX_BYTES=64*1024*1024,MAX_RECORDS=4096;
    private final Path root;
    private final ArrayList<Record> entries=new ArrayList<>();
    private long generation=0;
    private int activeSlot=-1;
    public long generation(){return generation;}
    public int size(){return entries.size();}
    public List<Record> records(){
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }
    public static final class Record {
        public final long episodeId,decisionId,preHash,postHash,preTicks,postTicks;
        public final int split,revision;
        public final String owner;
        private final byte[] bytes;
        private Record(long episode,long id,long pre,long post,long beforeTicks,long afterTicks,
                       int split,int rev,String owner,byte[] encoded) {
            episodeId=episode;decisionId=id;preHash=pre;postHash=post;
            preTicks=beforeTicks;postTicks=afterTicks;this.split=split;
            revision=rev;this.owner=owner;bytes=encoded.clone();
        }
        public byte[] encoded(){return bytes.clone();}
        public boolean sameData(Record other){return Arrays.equals(bytes,other.bytes);}
    }
    private static final class Meta {
        final int slot;final long generation,length;final byte[] sha;
        Meta(int slot,long generation,long length,byte[] sha){
            this.slot=slot;this.generation=generation;this.length=length;this.sha=sha;
        }
    }
    public Zero3ExperienceJournalV316(Path isolatedDirectory){
        check(isolatedDirectory!=null,"missing dedicated V3-only path");
        root=isolatedDirectory;
    }
    static void check(boolean b,String error){
        if(!b)throw new IllegalArgumentException("ZERO3 V316 JOURNAL "+error);
    }
    static byte[] sha(byte[] bytes){
        try{return MessageDigest.getInstance("SHA-256").digest(bytes);}
        catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}
    }
    private static void f(DataOutputStream out,float value)throws IOException{
        check(Float.isFinite(value),"nonfinite persisted physical value");out.writeFloat(value);
    }
    private static void floats(DataOutputStream out,float[] vals,int expected)throws IOException{
        check(vals!=null&&vals.length==expected,"sensor field missing or wrong width");
        for(float x:vals)f(out,x);
    }
    private static float[] readFloats(DataInputStream in,int count)throws IOException{
        float[] result=new float[count];
        for(int i=0;i<count;i++){result[i]=in.readFloat();check(Float.isFinite(result[i]),
            "nonfinite persisted sensor");}
        return result;
    }
    private static void frame(DataOutputStream out,Zero3SensoryFrameV315 v)throws IOException{
        check(v!=null&&v.intact(),"invalid sensor snapshot");
        out.writeLong(v.episodeId);out.writeLong(v.frameId);out.writeDouble(v.simulationTime);
        out.writeBoolean(v.preAction);out.writeLong(v.solverTicks);out.writeLong(v.hash);
        f(out,v.bodyCenterX);f(out,v.bodyCenterY);f(out,v.bodyVelX);f(out,v.bodyVelY);
        f(out,v.bodyAngle);f(out,v.gelAreaError);f(out,v.gelWork);
        out.writeBoolean(v.energyAvailable);out.writeBoolean(v.damageAvailable);
        out.writeBoolean(v.lastExecutedAvailable);
        floats(out,v.local96x12(),96*12);
        floats(out,v.membraneRays(),16);
        floats(out,v.globalProprioception(),48);
        floats(out,v.inner74x7(),74*7);
        if(v.lastExecutedAvailable)floats(out,v.lastExecuted64(),64);
    }
    private static final class Snapshot {
        long episode,frame,ticks,hash;double time;boolean pre,hasLast;
        float[] last;
    }
    private static Snapshot readFrame(DataInputStream in)throws IOException{
        Snapshot s=new Snapshot();
        s.episode=in.readLong();s.frame=in.readLong();s.time=in.readDouble();
        s.pre=in.readBoolean();s.ticks=in.readLong();s.hash=in.readLong();
        check(s.episode>0&&s.frame>=0&&Double.isFinite(s.time)&&s.time>=0,
              "invalid snapshot identity/time");
        for(int i=0;i<7;i++)check(Float.isFinite(in.readFloat()),"nonfinite body/gel state");
        boolean energy=in.readBoolean(),damage=in.readBoolean();
        check(!energy&&!damage,"laboratory unavailable energy/damage misrepresented");
        s.hasLast=in.readBoolean();
        readFloats(in,96*12);readFloats(in,16);readFloats(in,48);readFloats(in,74*7);
        if(s.hasLast)s.last=readFloats(in,64);
        return s;
    }
    /** A record keeps all original float bits, including complete raw senses. */
    static Record readRecord(byte[] raw)throws IOException{
        check(raw.length>0&&raw.length<=MAX_RECORD,"record length");
        DataInputStream in=new DataInputStream(new ByteArrayInputStream(raw));
        check(in.readInt()==MAGIC&&in.readInt()==VERSION,"schema mismatch");
        long episode=in.readLong(),decision=in.readLong();
        int split=in.readUnsignedByte(),revision=in.readInt();
        String owner=in.readUTF();
        check(episode>0&&decision>=0&&split<=2&&revision>=0&&
              !owner.isEmpty()&&owner.length()<=64,"invalid intention owner");
        float dirX=in.readFloat(),dirY=in.readFloat(),effort=in.readFloat(),
            adhesion=in.readFloat(),duration=in.readFloat(),confidence=in.readFloat();
        double direction=Math.hypot(dirX,dirY);
        check(Float.isFinite(dirX)&&Float.isFinite(dirY)&&
              (direction<1e-5||Math.abs(direction-1)<1e-3) &&
              Float.isFinite(effort)&&effort>=0&&effort<=1 &&
              Float.isFinite(adhesion)&&adhesion>=0&&adhesion<=1 &&
              Float.isFinite(duration)&&duration>0&&duration<=10 &&
              Float.isFinite(confidence)&&confidence>=0&&confidence<=1,
              "invalid persisted motor intention");
        Snapshot pre=readFrame(in),post=readFrame(in);
        long actionEp=in.readLong(),actionDecision=in.readLong(),actionFrame=in.readLong();
        int motorRevision=in.readInt(),perturbation=in.readInt();
        long seed=in.readLong();
        float[] requested=readFloats(in,64),executed=readFloats(in,64);
        float dx=in.readFloat(),dy=in.readFloat();
        check(Float.isFinite(dx)&&Float.isFinite(dy)&&
              pre.episode==episode&&post.episode==episode&&
              pre.frame==decision&&post.frame==decision+1 &&
              pre.pre&&!post.pre&&post.ticks==pre.ticks+1 &&
              Math.abs((post.time-pre.time)-Zero3CausalRecorderV315.DT)<1e-7 &&
              actionEp==episode&&actionDecision==decision&&actionFrame==decision &&
              motorRevision==revision&&pre.hash!=0&&post.hash!=0&&
              post.hasLast&&Arrays.equals(post.last,executed),
              "invalid real ordered sensory/action/outcome edge");
        for(float x:executed)check(Math.abs(x)<=1.0001f,"executed action out of bounds");
        for(float x:requested)check(Math.abs(x)<=1.0001f,"requested action out of bounds");
        check(in.available()==0,"unrecognized trailing record bytes");
        return new Record(episode,decision,pre.hash,post.hash,pre.ticks,post.ticks,
                          split,revision,owner,raw);
    }
    public void add(Zero3MotorIntentV316 intent,Zero3CausalRecorderV315.Experience e){
        check(intent!=null&&intent.intact()&&e!=null&&e.intact(),
              "missing or modified causal source");
        check(intent.episodeId==e.episodeId&&intent.decisionId==e.decisionId&&
              intent.controllerRevision==e.action.cortexRevision,
              "intent does not own the one physically executed action");
        check(entries.size()<MAX_RECORDS,"journal capacity");
        try{
            ByteArrayOutputStream bytes=new ByteArrayOutputStream(16000);
            DataOutputStream out=new DataOutputStream(bytes);
            out.writeInt(MAGIC);out.writeInt(VERSION);
            out.writeLong(e.episodeId);out.writeLong(e.decisionId);
            out.writeByte(e.split.ordinal());out.writeInt(intent.controllerRevision);
            out.writeUTF(intent.actorOwner);
            f(out,intent.directionX);f(out,intent.directionY);
            f(out,intent.desiredEffort);f(out,intent.adhesionPreference);
            f(out,intent.durationSimSeconds);f(out,intent.confidence);
            frame(out,e.pre);frame(out,e.post);
            out.writeLong(e.action.episodeId);out.writeLong(e.action.decisionId);
            out.writeLong(e.action.physicsFrameId);out.writeInt(e.action.cortexRevision);
            out.writeInt(e.action.perturbationId);out.writeLong(e.action.randomSeed);
            floats(out,e.action.requested64(),64);floats(out,e.action.executed64(),64);
            f(out,e.outcome.dx);f(out,e.outcome.dy);
            out.flush();
            Record record=readRecord(bytes.toByteArray());
            validateNext(entries,record);
            entries.add(record);
        }catch(IOException ex){throw new IllegalStateException("encode full experience",ex);}
    }
    private static void validateNext(List<Record> records,Record r){
        if(records.isEmpty())check(r.decisionId==0,"first episode must begin at 0");
        else{
            Record last=records.get(records.size()-1);
            if(last.episodeId==r.episodeId){
                check(r.decisionId==last.decisionId+1 &&
                      r.preTicks==last.postTicks &&
                      r.split==last.split&&r.owner.equals(last.owner),
                      "duplicate or split-owner changed within episode");
            }else{
                check(r.episodeId>last.episodeId&&r.decisionId==0,
                    "episodes not strictly increasing");
            }
        }
    }
    private static byte[] manifest(Meta m)throws IOException{
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        DataOutputStream out=new DataOutputStream(bytes);
        out.writeInt(MAGIC);out.writeInt(VERSION);out.writeInt(m.slot);
        out.writeLong(m.generation);out.writeLong(m.length);out.write(m.sha);out.flush();
        byte[] unsigned=bytes.toByteArray(),digest=sha(unsigned);
        out.write(digest);out.flush();return bytes.toByteArray();
    }
    private static Meta parseManifest(byte[] bytes)throws IOException{
        check(bytes.length==4+4+4+8+8+32+32,"manifest length");
        check(Arrays.equals(sha(Arrays.copyOf(bytes,bytes.length-32)),
              Arrays.copyOfRange(bytes,bytes.length-32,bytes.length)),
              "manifest digest");
        DataInputStream in=new DataInputStream(new ByteArrayInputStream(bytes));
        check(in.readInt()==MAGIC&&in.readInt()==VERSION,"manifest schema");
        int slot=in.readInt();long gen=in.readLong(),len=in.readLong();
        byte[] digest=new byte[32];in.readFully(digest);
        check(slot>=0&&slot<=1&&gen>0&&len>0&&len<=MAX_BYTES,
              "manifest generation bounds");
        return new Meta(slot,gen,len,digest);
    }
    private Path slot(int n){return root.resolve("zero3-v316-slot"+n+".bin");}
    private Path pointer(){return root.resolve("zero3-v316-active.mf");}
    private Path previous(){return root.resolve("zero3-v316-previous.mf");}
    private static void atomicWrite(Path target,byte[] bytes)throws IOException{
        Path temp=target.resolveSibling(target.getFileName().toString()+".tmp");
        try{
            try(FileOutputStream file=new FileOutputStream(temp.toFile())){
                file.write(bytes);file.flush();file.getFD().sync();
            }
            Files.move(temp,target,StandardCopyOption.ATOMIC_MOVE,
                       StandardCopyOption.REPLACE_EXISTING);
        }finally{Files.deleteIfExists(temp);}
    }
    private static void flushDirectoryBestEffort(Path root){
        try(java.nio.channels.FileChannel fd=java.nio.channels.FileChannel.open(
              root,StandardOpenOption.READ)){fd.force(true);}
        catch(IOException|UnsupportedOperationException ignored){}
    }
    public void checkpoint()throws IOException{
        check(!entries.isEmpty(),"cannot persist empty experiment");
        Files.createDirectories(root);
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        DataOutputStream out=new DataOutputStream(bytes);
        out.writeInt(MAGIC);out.writeInt(VERSION);out.writeInt(entries.size());
        for(Record r:entries){
            out.writeInt(r.bytes.length);out.write(r.bytes);
        }
        out.flush();byte[] payload=bytes.toByteArray();
        check(payload.length<=MAX_BYTES,"experience replay exceeds capacity");
        int target=activeSlot==0?1:0;
        Meta meta=new Meta(target,generation+1,payload.length,sha(payload));
        atomicWrite(slot(target),payload);flushDirectoryBestEffort(root);
        // Publish old pointer as fallback BEFORE committing new generation.
        if(generation>0&&Files.isRegularFile(pointer())){
            byte[] former=Files.readAllBytes(pointer());
            atomicWrite(previous(),former);
        }
        atomicWrite(pointer(),manifest(meta));flushDirectoryBestEffort(root);
        generation=meta.generation;activeSlot=target;
    }
    private static ArrayList<Record> unpack(byte[] contents)throws IOException{
        DataInputStream in=new DataInputStream(new ByteArrayInputStream(contents));
        check(in.readInt()==MAGIC&&in.readInt()==VERSION,"replay schema mismatch");
        int n=in.readInt();
        check(n>0&&n<=MAX_RECORDS,"invalid replay size");
        ArrayList<Record> result=new ArrayList<>(n);
        for(int j=0;j<n;j++){
            int size=in.readInt();check(size>0&&size<=MAX_RECORD,"bad record length");
            byte[] raw=new byte[size];in.readFully(raw);
            Record r=readRecord(raw);validateNext(result,r);result.add(r);
        }
        check(in.available()==0,"trailing replay bytes");
        return result;
    }
    private static final class Recovery {
        final Meta meta;final ArrayList<Record> records;
        Recovery(Meta meta,ArrayList<Record> records){
            this.meta=meta;this.records=records;
        }
    }
    private Recovery inspect(Path manifest)throws IOException{
        byte[] source=Files.readAllBytes(manifest);
        Meta m=parseManifest(source);
        byte[] payload=Files.readAllBytes(slot(m.slot));
        check(payload.length==m.length&&Arrays.equals(sha(payload),m.sha),
              "payload mismatch / corrupt committed replay");
        return new Recovery(m,unpack(payload));
    }
    public boolean load()throws IOException{
        entries.clear();generation=0;activeSlot=-1;
        Recovery recovered=null;
        try{recovered=inspect(pointer());}
        catch(IOException|IllegalArgumentException ignored){}
        if(recovered==null){
            try{recovered=inspect(previous());}
            catch(IOException|IllegalArgumentException ignored){}
        }
        if(recovered==null)return false; // never promote an uncommitted slot
        entries.addAll(recovered.records);
        generation=recovered.meta.generation;activeSlot=recovered.meta.slot;
        return true;
    }
    public boolean recoveredOlderGeneration()throws IOException{
        try{Meta m=parseManifest(Files.readAllBytes(pointer()));
            return generation>0&&generation<m.generation;}
        catch(IOException|IllegalArgumentException e){return generation>0;}
    }
    /** Fault injection fixture needs exact slot paths; no calls to legacy saves. */
    Path activePayloadForTest(){return slot(activeSlot);}
    Path activeManifestForTest(){return pointer();}
}
