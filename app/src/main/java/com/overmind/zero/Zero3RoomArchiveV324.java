package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * V3.24 experimental single-generation neural+physics+sensory+survival+ROOM
 * atomic checkpoint, INCLUDING gate private material hysteresis & room.food ID.
 * Does not write to any GameActivity 2.2 profile or OTA update pointer.
 */
public final class Zero3RoomArchiveV324 {
    static final int MAGIC=0x5A333234,VERSION=324,MAX=8*1024*1024;
    public static final class Bundle {
        public final Zero3SurvivalArchiveV323.Bundle life;
        public final Zero3RoomStateV324 room;
        Bundle(Zero3SurvivalArchiveV323.Bundle life,Zero3RoomStateV324 room)
              throws IOException {
            if(life==null||room==null)throw new IOException("missing completed room/life");
            // Validate every room field and the real entity in the SAME world
            // before any pending snapshot generation becomes durable.
            Zero3OrganismStoreV322.Physical p=life.joint.organism.physical();
            room.restore(p.world);
            this.life=life;this.room=room;
        }
        public Zero3RoomStateV324.Restored chamber()throws IOException {
            Zero3OrganismStoreV322.Physical p=life.joint.organism.physical();
            return room.restore(p.world);
        }
    }
    static byte[] encode(Bundle v)throws IOException {
        if(v==null)throw new IOException("null V324 archive");
        byte[] organism=Zero3SurvivalArchiveV323.encode(v.life);
        byte[] room=v.room.encoded();
        ByteArrayOutputStream raw=new ByteArrayOutputStream();
        DataOutputStream o=new DataOutputStream(raw);
        o.writeInt(MAGIC);o.writeInt(VERSION);
        o.writeInt(organism.length);o.write(organism);
        o.writeInt(room.length);o.write(room);o.flush();
        if(raw.size()>MAX)throw new IOException("oversized room/life archive");
        return raw.toByteArray();
    }
    static Bundle decode(byte[] raw)throws IOException {
        if(raw==null||raw.length<500||raw.length>MAX)
            throw new IOException("V324 archive length");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(raw));
        if(d.readInt()!=MAGIC||d.readInt()!=VERSION)
            throw new IOException("unsupported V324 archive");
        int plen=d.readInt();
        if(plen<400||plen>MAX-Zero3RoomStateLimit())throw new IOException("body byte count");
        byte[] prior=new byte[plen];d.readFully(prior);
        int rlen=d.readInt();
        if(rlen<40||rlen>Zero3RoomStateLimit()||d.available()!=rlen)
            throw new IOException("room byte count");
        byte[] roomBytes=new byte[rlen];d.readFully(roomBytes);
        Bundle bundle=new Bundle(Zero3SurvivalArchiveV323.decode(prior),
                                 Zero3RoomStateV324.decode(roomBytes));
        if(!Arrays.equals(raw,encode(bundle)))
            throw new IOException("noncanonical joint room/brain archive");
        return bundle;
    }
    private static int Zero3RoomStateLimit(){return 16384;}
    private static byte[] hash(byte[] raw){
        try{return MessageDigest.getInstance("SHA-256").digest(raw);}
        catch(Exception error){throw new IllegalStateException(error);}
    }
    public static String fingerprint(Bundle b)throws IOException {
        byte[] v=hash(encode(b));
        StringBuilder s=new StringBuilder();
        for(byte x:v)s.append(String.format(Locale.ROOT,"%02x",x&255));
        return s.toString();
    }
    private static void atomic(Path to,byte[] bytes)throws IOException {
        Path tmp=to.resolveSibling(to.getFileName()+".tmp");
        try{
            try(FileOutputStream f=new FileOutputStream(tmp.toFile())){
                f.write(bytes);f.flush();f.getFD().sync();
            }
            Files.move(tmp,to,StandardCopyOption.ATOMIC_MOVE,
                       StandardCopyOption.REPLACE_EXISTING);
        }finally{Files.deleteIfExists(tmp);}
    }
    private static byte[] manifest(int slot,long generation,byte[] raw)
              throws IOException {
        ByteArrayOutputStream b=new ByteArrayOutputStream(88);
        DataOutputStream d=new DataOutputStream(b);
        d.writeInt(MAGIC);d.writeInt(VERSION);d.writeInt(slot);
        d.writeLong(generation);d.writeInt(raw.length);d.write(hash(raw));
        d.flush();d.write(hash(b.toByteArray()));d.flush();return b.toByteArray();
    }
    private final Path root;
    private int active=-1;private long generation;private byte[] verified;
    public Zero3RoomArchiveV324(Path folder){
        if(folder==null)throw new IllegalArgumentException("missing room archive dir");
        root=folder;
    }
    private Path data(int n){return root.resolve("v324-room-"+n+".bin");}
    private Path pointer(boolean current){return root.resolve(
        current?"v324-current.mf":"v324-backup.mf");}
    private static final class Recovery {
        final int slot;final long gen;final byte[] bytes;final Bundle life;
        Recovery(int s,long g,byte[] b,Bundle l){slot=s;gen=g;bytes=b;life=l;}
    }
    private Recovery verify(Path ptr)throws IOException {
        byte[] mf=Files.readAllBytes(ptr);
        if(mf.length!=88||!Arrays.equals(hash(Arrays.copyOf(mf,56)),
                    Arrays.copyOfRange(mf,56,88)))
            throw new IOException("room manifest corrupted");
        DataInputStream i=new DataInputStream(new ByteArrayInputStream(mf));
        if(i.readInt()!=MAGIC||i.readInt()!=VERSION)
            throw new IOException("wrong room manifest");
        int slot=i.readInt();long generation=i.readLong();int length=i.readInt();
        byte[] digest=new byte[32];i.readFully(digest);
        if(slot<0||slot>1||generation<=0||length<500||length>MAX)
            throw new IOException("bad room generation");
        byte[] raw=Files.readAllBytes(data(slot));
        if(raw.length!=length||!Arrays.equals(hash(raw),digest))
            throw new IOException("mixed or corrupt room life");
        return new Recovery(slot,generation,raw,decode(raw));
    }
    public Bundle load()throws IOException {
        Recovery valid=null;
        try{valid=verify(pointer(true));}catch(IOException ignored){}
        if(valid==null)try{valid=verify(pointer(false));}catch(IOException ignored){}
        if(valid==null){active=-1;generation=0;verified=null;return null;}
        active=valid.slot;generation=valid.gen;verified=valid.bytes;
        return valid.life;
    }
    public void save(Bundle b)throws IOException {
        byte[] raw=encode(b);
        if(!Arrays.equals(raw,encode(decode(raw))))
            throw new IOException("room+life archive roundtrip differs");
        Files.createDirectories(root);
        if(generation>0&&(verified==null||
                !Arrays.equals(verified,Files.readAllBytes(data(active)))))
            throw new IOException("previous validated room generation missing");
        int slot=active==0?1:0;
        atomic(data(slot),raw);
        if(generation>0)
            atomic(pointer(false),manifest(active,generation,verified));
        atomic(pointer(true),manifest(slot,generation+1,raw));
        active=slot;generation++;verified=raw;
    }
    public long generation(){return generation;}
    public Path latestFile(){return data(active);}
    public Path oldFile(){return data(1-active);}
    public Path currentManifest(){return pointer(true);}
}
