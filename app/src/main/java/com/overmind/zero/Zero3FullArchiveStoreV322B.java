package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * Laboratory V3.22/B: one crash-safe, SHA-256 verified generation for the
 * complete V322 same-life neural+physical state AND the full causal sensory/
 * efference replay. No split commits; no separate replay that can be stale.
 *
 * This is not the on-device GameActivity save format.
 */
public final class Zero3FullArchiveStoreV322B {
    static final int MAGIC=0x5A334142,VERSION=3222,MAX=8*1024*1024;
    public static final class Bundle {
        public final Zero3OrganismStoreV322.State organism;
        private final Zero3FullCausalExperienceV322B[] replay;
        private final long rngState;
        Bundle(Zero3OrganismStoreV322.State organism,
               List<Zero3FullCausalExperienceV322B> experiences,
               long actualWorldRng)throws IOException {
            if(organism==null||experiences==null||
               experiences.size()!=organism.nextDecision||
               actualWorldRng==0||organism.lastRngState!=actualWorldRng)
                throw new IOException("V322B world RNG / replay generation mismatch");
            this.organism=organism;
            rngState=actualWorldRng;
            replay=experiences.toArray(new Zero3FullCausalExperienceV322B[0]);
            Zero3OrganismStoreV322.Trace[] traces=organism.trace();
            for(int i=0;i<replay.length;i++){
                if(replay[i]==null||replay[i].decision!=i||
                   (i>0&&replay[i].preTick!=replay[i-1].postTick))
                    throw new IOException("missing, repeated, noncontiguous physical experience");
                replay[i].validate(traces[i],organism.episodeId);
            }
        }
        public int experienceCount(){return replay.length;}
        public long actualWorldRngState(){return rngState;}
        public Zero3FullCausalExperienceV322B experience(int id){
            return replay[id];
        }
        public List<Zero3FullCausalExperienceV322B> experiences(){
            return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(replay)));
        }
    }
    static byte[] encode(Bundle bundle)throws IOException {
        if(bundle==null)throw new IOException("null V322B archive");
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(bytes);
        byte[] organism=Zero3OrganismStoreV322.encode(bundle.organism);
        d.writeInt(MAGIC);d.writeInt(VERSION);d.writeLong(bundle.rngState);
        d.writeInt(organism.length);d.write(organism);
        d.writeInt(bundle.replay.length);
        for(Zero3FullCausalExperienceV322B experience:bundle.replay){
            byte[] record=experience.encoded();
            if(record.length<=100||record.length>Zero3FullCausalExperienceV322B.MAX_RECORD)
                throw new IOException("replay record too large");
            d.writeInt(record.length);d.write(record);
        }
        d.flush();
        if(bytes.size()>MAX)throw new IOException("V322B bound full replay exceeded");
        return bytes.toByteArray();
    }
    static Bundle decode(byte[] payload)throws IOException {
        if(payload==null||payload.length<200||payload.length>MAX)
            throw new IOException("V322B payload length");
        try{
            DataInputStream d=new DataInputStream(new ByteArrayInputStream(payload));
            if(d.readInt()!=MAGIC||d.readInt()!=VERSION)
                throw new IOException("V322B incompatible journal schema");
            long rng=d.readLong(),length=d.readInt();
            if(length<=100||length>Zero3OrganismStoreV322.MAX_STATE_BYTES)
                throw new IOException("V322B organism size invalid");
            byte[] original=new byte[(int)length];d.readFully(original);
            Zero3OrganismStoreV322.State organism=Zero3OrganismStoreV322.decode(original);
            int count=d.readInt();
            if(count<=0||count!=organism.nextDecision||count>256)
                throw new IOException("V322B invalid causal record count");
            ArrayList<Zero3FullCausalExperienceV322B> replay=new ArrayList<>();
            for(int i=0;i<count;i++){
                int bytes=d.readInt();
                if(bytes<100||bytes>Zero3FullCausalExperienceV322B.MAX_RECORD)
                    throw new IOException("V322B bad record dimensions");
                byte[] record=new byte[bytes];d.readFully(record);
                replay.add(Zero3FullCausalExperienceV322B.decode(record));
            }
            if(d.available()!=0)throw new IOException("V322B trailing archive bytes");
            Bundle result=new Bundle(organism,replay,rng);
            if(!Arrays.equals(encode(result),payload))
                throw new IOException("V322B noncanonical journal");
            return result;
        }catch(IllegalArgumentException|IllegalStateException error){
            throw new IOException("V322B invalid replay",error);
        }
    }
    private static byte[] sha(byte[] bytes){
        try{return MessageDigest.getInstance("SHA-256").digest(bytes);}
        catch(Exception error){throw new IllegalStateException(error);}
    }
    public static String fingerprint(Bundle bundle)throws IOException{
        StringBuilder out=new StringBuilder();
        for(byte x:sha(encode(bundle)))out.append(String.format(Locale.ROOT,"%02x",x&255));
        return out.toString();
    }
    private static final class Generation {
        final int slot;final long number;final byte[] raw;final Bundle data;
        Generation(int s,long n,byte[] b,Bundle d){slot=s;number=n;raw=b;data=d;}
    }
    private final Path directory;
    private long generation;
    private int active=-1;
    private byte[] verified;
    public Zero3FullArchiveStoreV322B(Path dir){
        if(dir==null)throw new IllegalArgumentException("missing private archive dir");
        directory=dir;
    }
    private Path data(int slot){return directory.resolve("v322b-joint-"+slot+".bin");}
    private Path manifest(boolean primary){return directory.resolve(
        primary?"v322b-current.mf":"v322b-backup.mf");}
    private static void atomic(Path target,byte[] payload)throws IOException{
        Path tmp=target.resolveSibling(target.getFileName()+".tmp");
        try{
            try(FileOutputStream f=new FileOutputStream(tmp.toFile())){
                f.write(payload);f.flush();f.getFD().sync();
            }
            Files.move(tmp,target,StandardCopyOption.ATOMIC_MOVE,
                       StandardCopyOption.REPLACE_EXISTING);
        }finally{Files.deleteIfExists(tmp);}
    }
    private static byte[] pointer(int slot,long gen,byte[] payload)throws IOException{
        ByteArrayOutputStream b=new ByteArrayOutputStream(88);
        DataOutputStream d=new DataOutputStream(b);
        d.writeInt(MAGIC);d.writeInt(VERSION);d.writeInt(slot);
        d.writeLong(gen);d.writeInt(payload.length);d.write(sha(payload));
        d.flush();d.write(sha(b.toByteArray()));d.flush();
        return b.toByteArray();
    }
    private Generation inspect(Path path)throws IOException {
        byte[] m=Files.readAllBytes(path);
        if(m.length!=88||!Arrays.equals(sha(Arrays.copyOf(m,56)),
                                       Arrays.copyOfRange(m,56,88)))
            throw new IOException("V322B manifest corrupted");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(m));
        if(d.readInt()!=MAGIC||d.readInt()!=VERSION)
            throw new IOException("V322B manifest version");
        int slot=d.readInt();long gen=d.readLong();int size=d.readInt();
        byte[] hash=new byte[32];d.readFully(hash);
        if(slot<0||slot>1||gen<=0||size<200||size>MAX)
            throw new IOException("V322B manifest invalid metadata");
        byte[] payload=Files.readAllBytes(data(slot));
        if(payload.length!=size||!Arrays.equals(sha(payload),hash))
            throw new IOException("V322B mixed or corrupted generation");
        return new Generation(slot,gen,payload,decode(payload));
    }
    public Bundle load()throws IOException{
        Generation g=null;
        try{g=inspect(manifest(true));}catch(IOException ignored){}
        if(g==null)try{g=inspect(manifest(false));}catch(IOException ignored){}
        if(g==null){active=-1;generation=0;verified=null;return null;}
        active=g.slot;generation=g.number;verified=g.raw;
        return g.data;
    }
    public void save(Bundle data)throws IOException{
        byte[] bytes=encode(data);
        if(!Arrays.equals(bytes,encode(decode(bytes))))
            throw new IOException("V322B noncanonical snapshot");
        Files.createDirectories(directory);
        if(generation>0&&(verified==null||
            !Arrays.equals(Files.readAllBytes(data(active)),verified)))
            throw new IOException("V322B source generation no longer valid");
        int target=active==0?1:0;
        atomic(data(target),bytes);
        if(generation>0)
            atomic(manifest(false),pointer(active,generation,verified));
        atomic(manifest(true),pointer(target,generation+1,bytes));
        generation++;active=target;verified=bytes;
    }
    public long generation(){return generation;}
    public Path activeFileForTest(){return data(active);}
    public Path oldFileForTest(){return data(1-active);}
    public Path currentManifestForTest(){return manifest(true);}
}
