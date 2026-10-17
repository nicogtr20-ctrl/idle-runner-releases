package com.overmind.zero;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * V3.22 research-only, decision-boundary coordinated snapshot. Persists the
 * three V3.21 neural modules, all primitive physical fields (including private
 * gel history and actual grip timers), current entity state, last 64 efferences,
 * authority cursor, and an immutable causal digest tape in ONE generation.
 *
 * This is NOT the production BrainSnapshotV3: no full GameActivity ecosystem,
 * survival energy/damage, procedural replay or Android power-loss guarantee.
 * Never load it over an existing 2.2 profile.
 */
public final class Zero3OrganismStoreV322 {
    private static final int MAGIC=0x5A333232, VERSION=322, MAX=2*1024*1024;
    public static final class Trace {
        public final int decision;
        public final long startTick,endTick,preHash,postHash,actuatorHash;
        public final int gripFrames,bilateralFrames;
        public final long workBits;
        public Trace(int decision,long start,long end,long before,long after,
                     long actions,int grip,int bilateral,double work) {
            if(decision<0||start<0||end-start!=12||grip<0||grip>12||
               bilateral<0||bilateral>grip||!Double.isFinite(work)||work<0)
                throw new IllegalArgumentException("V322 invalid causal decision");
            this.decision=decision;startTick=start;endTick=end;
            preHash=before;postHash=after;actuatorHash=actions;
            gripFrames=grip;bilateralFrames=bilateral;
            workBits=Double.doubleToLongBits(work);
        }
        void write(DataOutputStream d)throws IOException {
            d.writeInt(decision);d.writeLong(startTick);d.writeLong(endTick);
            d.writeLong(preHash);d.writeLong(postHash);d.writeLong(actuatorHash);
            d.writeInt(gripFrames);d.writeInt(bilateralFrames);d.writeLong(workBits);
        }
        static Trace read(DataInputStream d)throws IOException {
            int id=d.readInt();long t=d.readLong(),end=d.readLong();
            long pre=d.readLong(),post=d.readLong(),act=d.readLong();
            int grip=d.readInt(),bilateral=d.readInt();long work=d.readLong();
            Trace result=new Trace(id,t,end,pre,post,act,grip,bilateral,
                                   Double.longBitsToDouble(work));
            if(result.workBits!=work)throw new IOException("noncanonical real work");
            return result;
        }
    }
    public static final class Physical {
        public final MotorBrain.Body body;
        public final Morphology.Surface skin;
        public final SymbioteMatterV1 gel;
        public final ArrayList<UnknownWorld.Entity> world;
        Physical(MotorBrain.Body b,Morphology.Surface s,SymbioteMatterV1 g,
                 ArrayList<UnknownWorld.Entity> w) {
            body=b;skin=s;gel=g;world=w;
        }
    }
    /** Immutable in-memory bytes: never retain aliases to mutable live material. */
    public static final class State {
        public final long episodeId;
        public final int nextDecision;
        public final long lastRngState; // fixture/environment RNG supplied by owner
        public final Trace[] records;
        private final byte[] material;
        private final byte[] neural;
        private final float[] lastActuators;
        State(long episode,int cursor,long rng,Trace[] tape,byte[] phys,
              byte[] brain,float[] last)throws IOException {
            if(episode<=0||cursor<=0||cursor>100000||rng==0||
               tape==null||tape.length!=cursor||phys==null||brain==null||
               last==null||last.length!=64)
                throw new IOException("inconsistent episode snapshot");
            for(float value:last)if(!Float.isFinite(value))
                throw new IOException("nonfinite last actuator");
            for(int i=0;i<tape.length;i++){
                if(tape[i]==null||tape[i].decision!=i||
                   (i>0&&tape[i].startTick!=tape[i-1].endTick))
                    throw new IOException("missing, repeated or reordered physics decision");
            }
            episodeId=episode;nextDecision=cursor;lastRngState=rng;
            records=tape.clone();material=phys.clone();neural=brain.clone();
            lastActuators=last.clone();
        }
        public float[] last64(){return lastActuators.clone();}
        public Physical physical()throws IOException {return decodePhysical(material);}
        public String materialHash(){return hex(sha(material));}
        public void restoreModules(Zero3AutonomousLoopV317.Encoder encoder,
                   Zero3LearningFoundation.AdaptiveCortex cortex,
                   Zero3ContextAdhesionV319 head)throws IOException {
            if(encoder==null||cortex==null||head==null)
                throw new IOException("missing module for cold restore");
            // Decode and validate every module BEFORE applying any of their weights.
            try {
                DataInputStream d=new DataInputStream(new ByteArrayInputStream(neural));
                Zero3AutonomousLoopV317.Encoder.State e=
                    Zero3AutonomousLoopV317.Encoder.State.read(d);
                Zero3LearningFoundation.AdaptiveCortex.State c=
                    Zero3LearningFoundation.AdaptiveCortex.State.read(d);
                int size=d.readInt();
                if(size!=569)throw new IOException("invalid adhesive neural dimensions");
                byte[] raw=new byte[size];d.readFully(raw);
                if(d.available()!=0)throw new IOException("trailing neural data");
                Zero3ContextAdhesionV319.State h=Zero3MotorHeadStoreV320.decode(raw);
                Zero3AutonomousLoopV317.Encoder.State freshE=encoder.checkpoint();
                Zero3LearningFoundation.AdaptiveCortex.State freshC=cortex.checkpoint();
                Zero3ContextAdhesionV319.State freshH=head.captureAtDecisionBoundary();
                if(freshE.steps!=0||freshE.updates!=0||
                   freshC.revision!=0||freshC.updates!=0||
                   freshH.decisions!=0||freshH.realCreditEvents!=0||
                   Float.floatToIntBits(freshC.learningRate())!=
                       Float.floatToIntBits(c.learningRate())||
                   freshH.training!=h.training||
                   e.updates!=nextDecision||h.decisions!=nextDecision||
                   c.updates!=nextDecision)
                    throw new IOException("neural counters/config mismatch at joint decision");
                // Only brand-new modules are legal restore targets.
                encoder.restore(e);cortex.restore(c);head.restoreAtDecisionBoundary(h);
            } catch(IllegalArgumentException|IllegalStateException error) {
                throw new IOException("neural preflight failed; no in-flight restore",error);
            }
        }
    }

    private static final class FieldCodec {
        private static Field[] sorted(Class<?> type)throws IOException {
            Field[] f=type.getDeclaredFields();
            ArrayList<Field> usable=new ArrayList<>();
            for(Field field:f)if(!Modifier.isStatic(field.getModifiers())&&!field.isSynthetic()){
                Class<?> t=field.getType();
                if(t!=float.class&&t!=double.class&&t!=int.class&&
                   t!=long.class&&t!=boolean.class&&t!=float[].class&&
                   t!=int[].class&&t!=long[].class&&t!=boolean[].class)
                    throw new IOException("new unsupported physical field: "+field.getName());
                field.setAccessible(true);usable.add(field);
            }
            usable.sort(Comparator.comparing(Field::getName));
            return usable.toArray(new Field[0]);
        }
        static int tag(Class<?> t) {
            if(t==float.class)return 1;if(t==double.class)return 2;
            if(t==int.class)return 3;if(t==long.class)return 4;
            if(t==boolean.class)return 5;if(t==float[].class)return 6;
            if(t==int[].class)return 7;if(t==long[].class)return 8;
            if(t==boolean[].class)return 9;
            throw new IllegalArgumentException("unsupported physical state type");
        }
        static void write(DataOutputStream d,Object target)throws IOException {
            try {
                Field[] fields=sorted(target.getClass());
                d.writeUTF(target.getClass().getName());d.writeInt(fields.length);
                for(Field f:fields){
                    int tag=tag(f.getType());d.writeUTF(f.getName());d.writeByte(tag);
                    if(tag==1)d.writeInt(Float.floatToRawIntBits(f.getFloat(target)));
                    else if(tag==2)d.writeLong(Double.doubleToRawLongBits(f.getDouble(target)));
                    else if(tag==3)d.writeInt(f.getInt(target));
                    else if(tag==4)d.writeLong(f.getLong(target));
                    else if(tag==5)d.writeBoolean(f.getBoolean(target));
                    else {
                        Object a=f.get(target);int n=Array.getLength(a);
                        if(n>4096)throw new IOException("oversized material field");
                        d.writeInt(n);
                        for(int i=0;i<n;i++){
                            if(tag==6)d.writeInt(Float.floatToRawIntBits(Array.getFloat(a,i)));
                            if(tag==7)d.writeInt(Array.getInt(a,i));
                            if(tag==8)d.writeLong(Array.getLong(a,i));
                            if(tag==9)d.writeBoolean(Array.getBoolean(a,i));
                        }
                    }
                }
            }catch(ReflectiveOperationException error){
                throw new IOException("cannot read material state",error);
            }
        }
        static void read(DataInputStream d,Object target)throws IOException {
            try{
                if(!target.getClass().getName().equals(d.readUTF()))
                    throw new IOException("physical object type changed");
                Field[] fields=sorted(target.getClass());
                if(d.readInt()!=fields.length)throw new IOException("missing material fields");
                for(Field f:fields){
                    int tag=tag(f.getType());
                    if(!f.getName().equals(d.readUTF())||d.readUnsignedByte()!=tag)
                        throw new IOException("material schema changed: "+f.getName());
                    boolean constant=Modifier.isFinal(f.getModifiers());
                    if(tag<=5){
                        if(tag==1){
                            float v=Float.intBitsToFloat(d.readInt());
                            if(constant&&Float.floatToRawIntBits(f.getFloat(target))!=
                               Float.floatToRawIntBits(v))
                                throw new IOException("immutable geometry field mismatch");
                            if(!constant)f.setFloat(target,v);
                        } else if(tag==2){
                            double v=Double.longBitsToDouble(d.readLong());
                            if(constant&&Double.doubleToRawLongBits(f.getDouble(target))!=
                               Double.doubleToRawLongBits(v))
                                throw new IOException("immutable double mismatch");
                            if(!constant)f.setDouble(target,v);
                        } else if(tag==3){
                            int v=d.readInt();
                            if(constant&&f.getInt(target)!=v)throw new IOException("immutable int mismatch");
                            if(!constant)f.setInt(target,v);
                        } else if(tag==4){
                            long v=d.readLong();
                            if(constant&&f.getLong(target)!=v)throw new IOException("immutable long mismatch");
                            if(!constant)f.setLong(target,v);
                        } else {
                            boolean v=d.readBoolean();
                            if(constant&&f.getBoolean(target)!=v)throw new IOException("immutable flag mismatch");
                            if(!constant)f.setBoolean(target,v);
                        }
                    } else {
                        Object dst=f.get(target);int length=d.readInt();
                        if(length<0||length!=Array.getLength(dst))
                            throw new IOException("material array length changed");
                        for(int i=0;i<length;i++){
                            if(tag==6)Array.setFloat(dst,i,Float.intBitsToFloat(d.readInt()));
                            if(tag==7)Array.setInt(dst,i,d.readInt());
                            if(tag==8)Array.setLong(dst,i,d.readLong());
                            if(tag==9)Array.setBoolean(dst,i,d.readBoolean());
                        }
                    }
                }
            }catch(ReflectiveOperationException error){
                throw new IOException("cannot restore material state",error);
            }
        }
    }

    private static byte[] encodePhysical(MotorBrain.Body b,Morphology.Surface skin,
                   SymbioteMatterV1 gel,List<UnknownWorld.Entity> objects)throws IOException {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(bytes);
        FieldCodec.write(d,b);FieldCodec.write(d,skin);FieldCodec.write(d,gel);
        if(objects==null||objects.size()>128)throw new IOException("world entity count");
        d.writeInt(objects.size());
        for(UnknownWorld.Entity e:objects){
            d.writeInt(Float.floatToRawIntBits(e.spawnX));
            d.writeInt(Float.floatToRawIntBits(e.spawnY));
            d.writeInt(e.signature);d.writeInt(e.physicalLaw());
            FieldCodec.write(d,e);
        }
        d.flush();return bytes.toByteArray();
    }
    private static Physical decodePhysical(byte[] bytes)throws IOException{
        try{
            DataInputStream d=new DataInputStream(new ByteArrayInputStream(bytes));
            MotorBrain.Body body=new MotorBrain.Body();
            Morphology.Surface skin=new Morphology.Surface(body);
            SymbioteMatterV1 gel=new SymbioteMatterV1();
            FieldCodec.read(d,body);FieldCodec.read(d,skin);FieldCodec.read(d,gel);
            int count=d.readInt();
            if(count<0||count>128)throw new IOException("world count out of bounds");
            ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
            for(int i=0;i<count;i++){
                float sx=Float.intBitsToFloat(d.readInt()),sy=Float.intBitsToFloat(d.readInt());
                int sign=d.readInt(),law=d.readInt();
                if(!Float.isFinite(sx)||!Float.isFinite(sy)||
                   sign<0||sign>=UnknownWorld.SIGNATURES||law<0||law>6)
                    throw new IOException("invalid world entity constructor");
                UnknownWorld.Entity e=new UnknownWorld.Entity(sx,sy,sign,law);
                FieldCodec.read(d,e);objects.add(e);
            }
            if(d.available()!=0||gel.invalid!=0)
                throw new IOException("invalid or trailing material bytes");
            return new Physical(body,skin,gel,objects);
        }catch(IllegalArgumentException error){throw new IOException("invalid material",error);}
    }
    public static State capture(long episode,int next,long worldRng,
                    Zero3AutonomousLoopV317.Encoder encoder,
                    Zero3LearningFoundation.AdaptiveCortex cortex,
                    Zero3ContextAdhesionV319 head,
                    MotorBrain.Body b,Morphology.Surface s,SymbioteMatterV1 gel,
                    List<UnknownWorld.Entity> world,float[] last,List<Trace> trace)
                    throws IOException {
        if(encoder==null||cortex==null||head==null||b==null||s==null||gel==null||
           trace==null||last==null||next!=trace.size()||
           encoder.updates()!=next||cortex.updates()!=next||head.updates()!=next||
           gel.invalid!=0||trace.get(next-1).endTick!=gel.ticks)
            throw new IOException("non-boundary, missing or inconsistent V322 snapshot");
        // These methods reject any pending PRE/action/POST transaction.
        ByteArrayOutputStream n=new ByteArrayOutputStream();
        DataOutputStream nd=new DataOutputStream(n);
        encoder.checkpoint().write(nd);cortex.checkpoint().write(nd);
        byte[] grip=Zero3MotorHeadStoreV320.encode(head.captureAtDecisionBoundary());
        nd.writeInt(grip.length);nd.write(grip);nd.flush();
        byte[] phys=encodePhysical(b,s,gel,world);
        return new State(episode,next,worldRng,trace.toArray(new Trace[0]),
                         phys,n.toByteArray(),last);
    }
    static byte[] encode(State state)throws IOException {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(bytes);
        d.writeInt(MAGIC);d.writeInt(VERSION);d.writeLong(state.episodeId);
        d.writeInt(state.nextDecision);d.writeLong(state.lastRngState);
        d.writeInt(state.material.length);d.write(state.material);
        d.writeInt(state.neural.length);d.write(state.neural);
        for(float x:state.lastActuators)d.writeInt(Float.floatToRawIntBits(x));
        d.writeInt(state.records.length);
        for(Trace entry:state.records)entry.write(d);
        d.flush();
        if(bytes.size()>MAX)throw new IOException("overlarge V322 organism state");
        return bytes.toByteArray();
    }
    static State decode(byte[] bytes)throws IOException {
        if(bytes==null||bytes.length<100||bytes.length>MAX)
            throw new IOException("V322 state byte count");
        try{
            DataInputStream d=new DataInputStream(new ByteArrayInputStream(bytes));
            if(d.readInt()!=MAGIC||d.readInt()!=VERSION)
                throw new IOException("V322 schema incompatible");
            long episode=d.readLong();int cursor=d.readInt();long rng=d.readLong();
            int physLen=d.readInt();
            if(physLen<100||physLen>MAX/2)throw new IOException("material byte count");
            byte[] phys=new byte[physLen];d.readFully(phys);
            decodePhysical(phys); // verify shape + material before any neural restore
            int neuralLen=d.readInt();
            if(neuralLen<100||neuralLen>32768)throw new IOException("neural byte count");
            byte[] neural=new byte[neuralLen];d.readFully(neural);
            float[] last=new float[64];
            for(int i=0;i<64;i++)last[i]=Float.intBitsToFloat(d.readInt());
            int count=d.readInt();
            if(count!=cursor||count<1||count>100000)
                throw new IOException("causal trace length mismatch");
            Trace[] trace=new Trace[count];
            for(int i=0;i<count;i++)trace[i]=Trace.read(d);
            if(d.available()!=0)throw new IOException("trailing checkpoint bytes");
            State state=new State(episode,cursor,rng,trace,phys,neural,last);
            // Validate neural counters/config by cold restoring into a laboratory
            // clone, not into the user-provided target.
            state.restoreModules(new Zero3AutonomousLoopV317.Encoder(0x317L),
                new Zero3LearningFoundation.AdaptiveCortex(.013f),
                new Zero3ContextAdhesionV319(0x319L,true));
            return state;
        }catch(IllegalArgumentException error){throw new IOException("V322 state invalid",error);}
    }
    private static byte[] sha(byte[] data) {
        try{return MessageDigest.getInstance("SHA-256").digest(data);}
        catch(Exception error){throw new IllegalStateException(error);}
    }
    private static String hex(byte[] digest){
        StringBuilder out=new StringBuilder();
        for(byte b:digest)out.append(String.format(Locale.ROOT,"%02x",b&255));
        return out.toString();
    }
    private final Path dir;
    private int active=-1;
    private long generation;
    private byte[] verified;
    public Zero3OrganismStoreV322(Path directory){
        if(directory==null)throw new IllegalArgumentException("no V322 directory");
        dir=directory;
    }
    private Path data(int slot){return dir.resolve("v322-whole-"+slot+".bin");}
    private Path pointer(boolean current){return dir.resolve(current?"v322-current.mf":"v322-previous.mf");}
    private static void atomic(Path path,byte[] bytes)throws IOException {
        Path tmp=path.resolveSibling(path.getFileName()+".tmp");
        try{
            try(FileOutputStream f=new FileOutputStream(tmp.toFile())){
                f.write(bytes);f.flush();f.getFD().sync();
            }
            Files.move(tmp,path,StandardCopyOption.ATOMIC_MOVE,
                       StandardCopyOption.REPLACE_EXISTING);
        }finally{Files.deleteIfExists(tmp);}
    }
    private static byte[] manifest(int slot,long generation,byte[] payload)throws IOException{
        ByteArrayOutputStream b=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(b);
        d.writeInt(MAGIC);d.writeInt(VERSION);d.writeInt(slot);d.writeLong(generation);
        d.writeInt(payload.length);d.write(sha(payload));d.flush();
        d.write(sha(b.toByteArray()));d.flush();
        return b.toByteArray();
    }
    private static final class Recovery {
        final int slot;final long generation;final byte[] raw;final State state;
        Recovery(int s,long g,byte[] b,State st){slot=s;generation=g;raw=b;state=st;}
    }
    private Recovery inspect(Path manifest)throws IOException{
        byte[] m=Files.readAllBytes(manifest);
        if(m.length!=84||!Arrays.equals(sha(Arrays.copyOf(m,52)),
                                       Arrays.copyOfRange(m,52,84)))
            throw new IOException("manifest SHA256 fail");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(m));
        if(d.readInt()!=MAGIC||d.readInt()!=VERSION)throw new IOException("manifest version");
        int slot=d.readInt();long gen=d.readLong();int n=d.readInt();
        byte[] hash=new byte[32];d.readFully(hash);
        if(slot<0||slot>1||gen<=0||n<100||n>MAX)throw new IOException("invalid manifest");
        byte[] raw=Files.readAllBytes(data(slot));
        if(raw.length!=n||!Arrays.equals(sha(raw),hash))
            throw new IOException("joint organism payload altered");
        return new Recovery(slot,gen,raw,decode(raw));
    }
    public State load()throws IOException{
        Recovery r=null;
        try{r=inspect(pointer(true));}catch(IOException ignored){}
        if(r==null)try{r=inspect(pointer(false));}catch(IOException ignored){}
        if(r==null){active=-1;generation=0;verified=null;return null;}
        active=r.slot;generation=r.generation;verified=r.raw;
        return r.state;
    }
    public void save(State state)throws IOException{
        byte[] raw=encode(state);
        if(!Arrays.equals(raw,encode(decode(raw))))
            throw new IOException("organism snapshot noncanonical");
        Files.createDirectories(dir);
        if(generation>0&&(verified==null||
           !Arrays.equals(Files.readAllBytes(data(active)),verified)))
            throw new IOException("latest verified generation unavailable");
        int target=active==0?1:0;
        atomic(data(target),raw);
        if(generation>0)atomic(pointer(false),manifest(active,generation,verified));
        atomic(pointer(true),manifest(target,generation+1,raw));
        active=target;generation++;verified=raw;
    }
    public long generation(){return generation;}
    public Path activeFileForTest(){return data(active);}
    public Path previousFileForTest(){return data(1-active);}
    public Path manifestForTest(){return pointer(true);}
    public static String fingerprint(State s)throws IOException{return hex(sha(encode(s)));}
}
