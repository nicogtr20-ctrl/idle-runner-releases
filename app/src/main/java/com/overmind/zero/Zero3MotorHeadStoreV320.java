package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/** V3.20 experimental MOTOR-HEAD-ONLY checkpoint. No Android save access. */
public final class Zero3MotorHeadStoreV320 {
    static final int MAGIC=0x5A333230,VERSION=320;
    private final Path root;
    private long generation;
    private int slot=-1;
    private byte[] verified;
    public Zero3MotorHeadStoreV320(Path dir){if(dir==null)throw new IllegalArgumentException();root=dir;}
    static void check(boolean ok,String reason)throws IOException{
        if(!ok)throw new IOException("V320 "+reason);
    }
    static byte[] digest(byte[] bytes){
        try{return MessageDigest.getInstance("SHA-256").digest(bytes);}
        catch(Exception e){throw new IllegalStateException(e);}
    }
    static byte[] encode(Zero3ContextAdhesionV319.State x)throws IOException{
        ByteArrayOutputStream b=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(b);
        d.writeInt(MAGIC);d.writeInt(VERSION);d.writeInt(16);d.writeInt(8);
        d.writeBoolean(x.training);d.writeLong(x.rngState);
        d.writeInt(x.decisions);d.writeInt(x.realCreditEvents);
        d.writeDouble(x.weightChangeSq);d.writeDouble(x.accountedAdhesionWork);
        d.writeDouble(x.observedGelWork);
        for(float v:x.weights())d.writeFloat(v);
        d.flush();return b.toByteArray();
    }
    static Zero3ContextAdhesionV319.State decode(byte[] b)throws IOException{
        check(b.length==4*4+1+8+4*2+8*3+128*4,"length");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(b));
        check(d.readInt()==MAGIC&&d.readInt()==VERSION,"schema");
        check(d.readInt()==16&&d.readInt()==8,"network shape");
        boolean training=d.readBoolean();long rng=d.readLong();
        int updates=d.readInt(),grips=d.readInt();
        double squared=d.readDouble(),adhesion=d.readDouble(),total=d.readDouble();
        float[] weights=new float[128];
        for(int i=0;i<weights.length;i++)weights[i]=d.readFloat();
        check(d.available()==0,"trailing data");
        try{return new Zero3ContextAdhesionV319.State(
            weights,rng,training,updates,grips,squared,adhesion,total);}
        catch(IllegalArgumentException ex){throw new IOException("invalid state",ex);}
    }
    private Path file(int s){return root.resolve("motor-"+s+".bin");}
    private Path manifest(boolean current){return root.resolve(current?"current.mf":"previous.mf");}
    static void atomic(Path file,byte[] bytes)throws IOException{
        Path temp=file.resolveSibling(file.getFileName()+".tmp");
        try{
            try(FileOutputStream out=new FileOutputStream(temp.toFile())){
                out.write(bytes);out.flush();out.getFD().sync();
            }
            Files.move(temp,file,StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING);
        }finally{Files.deleteIfExists(temp);}
    }
    private static byte[] pointer(int s,long gen,byte[] payload)throws IOException{
        ByteArrayOutputStream b=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(b);
        d.writeInt(MAGIC);d.writeInt(VERSION);d.writeInt(s);
        d.writeLong(gen);d.writeInt(payload.length);d.write(digest(payload));d.flush();
        d.write(digest(b.toByteArray()));d.flush();
        return b.toByteArray();
    }
    static final class Restored {
        final int slot;final long generation;
        final byte[] raw;final Zero3ContextAdhesionV319.State state;
        Restored(int s,long g,byte[] r,Zero3ContextAdhesionV319.State x){
            slot=s;generation=g;raw=r;state=x;
        }
    }
    private Restored verify(Path m)throws IOException{
        byte[] pointer=Files.readAllBytes(m);
        check(pointer.length==4+4+4+8+4+32+32,"pointer length");
        check(Arrays.equals(digest(Arrays.copyOf(pointer,pointer.length-32)),
            Arrays.copyOfRange(pointer,pointer.length-32,pointer.length)),
            "pointer SHA");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(pointer));
        check(d.readInt()==MAGIC&&d.readInt()==VERSION,"pointer schema");
        int s=d.readInt();long gen=d.readLong();int n=d.readInt();
        check((s==0||s==1)&&gen>0&&n==4*4+1+8+4*2+8*3+128*4,"pointer bounds");
        byte[] sha=new byte[32];d.readFully(sha);
        byte[] raw=Files.readAllBytes(file(s));
        check(raw.length==n&&Arrays.equals(sha,digest(raw)),"snapshot SHA");
        return new Restored(s,gen,raw,decode(raw));
    }
    public Zero3ContextAdhesionV319.State load()throws IOException{
        Restored r=null;
        try{r=verify(manifest(true));}catch(IOException ignored){}
        if(r==null)try{r=verify(manifest(false));}catch(IOException ignored){}
        if(r==null){slot=-1;generation=0;verified=null;return null;}
        slot=r.slot;generation=r.generation;verified=r.raw;
        return r.state;
    }
    public void save(Zero3ContextAdhesionV319 head)throws IOException{
        byte[] raw=encode(head.captureAtDecisionBoundary());
        check(Arrays.equals(raw,encode(decode(raw))),"not reproducible");
        Files.createDirectories(root);
        if(generation>0){
            check(verified!=null&&Arrays.equals(Files.readAllBytes(file(slot)),verified),
                "verified predecessor lost");
        }
        int next=slot==0?1:0;
        atomic(file(next),raw);
        if(generation>0)atomic(manifest(false),pointer(slot,generation,verified));
        atomic(manifest(true),pointer(next,generation+1,raw));
        slot=next;generation++;verified=raw;
    }
    public long generation(){return generation;}
    public Path latestFile(){return file(slot);}
    public Path oldFile(){return file(1-slot);}
    public Path manifestFile(){return manifest(true);}
    public static String fingerprint(Zero3ContextAdhesionV319.State s)throws IOException{
        StringBuilder b=new StringBuilder();
        for(byte x:digest(encode(s)))b.append(String.format("%02x",x&255));
        return b.toString();
    }
}
