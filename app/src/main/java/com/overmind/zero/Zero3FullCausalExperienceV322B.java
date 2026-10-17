package com.overmind.zero;

import java.io.*;
import java.util.*;

/**
 * V3.22/B laboratory full-fidelity causal experience: physical PRE sensors,
 * each actually executed 64D command in all 12 solver substeps, measured work,
 * physical POST sensors and the completed decision trace. No future sensory
 * readings enter the actor. Not the Android production replay adapter.
 */
public final class Zero3FullCausalExperienceV322B {
    static final int MAGIC=0x5A334542,VERSION=3222,STEPS=12,OUTPUTS=64;
    static final int MAX_RECORD=32768;
    public final int decision;
    public final long episode,preHash,postHash,preTick,postTick,actuatorHash;
    private final byte[] bytes;

    private Zero3FullCausalExperienceV322B(int n,long ep,long pre,long post,
              long beforeTick,long afterTick,long commands,byte[] immutable) {
        decision=n;episode=ep;preHash=pre;postHash=post;
        preTick=beforeTick;postTick=afterTick;actuatorHash=commands;
        bytes=immutable.clone();
    }
    public byte[] encoded(){return bytes.clone();}
    private static void check(boolean condition,String reason)throws IOException {
        if(!condition)throw new IOException("V322B full experience: "+reason);
    }
    static void finite(float a)throws IOException {
        check(Float.isFinite(a),"nonfinite sensory or efference");
    }
    static void writeArray(DataOutputStream d,float[] a,int size)throws IOException {
        check(a!=null&&a.length==size,"mismatched sensor shape");
        for(float value:a){finite(value);d.writeInt(Float.floatToRawIntBits(value));}
    }
    static float[] readArray(DataInputStream d,DataOutputStream copy,int size)throws IOException {
        float[] a=new float[size];
        for(int i=0;i<size;i++){
            int bits=d.readInt();a[i]=Float.intBitsToFloat(bits);finite(a[i]);copy.writeInt(bits);
        }
        return a;
    }
    static final class Metadata {
        final long episode,frame,ticks,hash;
        final boolean pre;
        final boolean lastAvailable;
        final double time;
        Metadata(long ep,long frame,long ticks,long hash,boolean before,boolean previous,double time) {
            episode=ep;this.frame=frame;this.ticks=ticks;this.hash=hash;
            pre=before;lastAvailable=previous;this.time=time;
        }
    }
    private static void writeFrame(DataOutputStream d,Zero3SensoryFrameV315 frame)
              throws IOException {
        check(frame!=null&&frame.intact(),"tampered live sensory frame");
        check(!frame.energyAvailable&&!frame.damageAvailable,
              "unsupported metabolic sensor; never silently fill zero");
        d.writeLong(frame.episodeId);d.writeLong(frame.frameId);
        d.writeLong(frame.solverTicks);d.writeLong(frame.hash);
        d.writeBoolean(frame.preAction);d.writeDouble(frame.simulationTime);
        d.writeBoolean(frame.lastExecutedAvailable);
        float[] main={frame.bodyCenterX,frame.bodyCenterY,
                      frame.bodyVelX,frame.bodyVelY,frame.bodyAngle,
                      frame.gelAreaError,frame.gelWork};
        writeArray(d,main,7);
        writeArray(d,frame.local96x12(),96*12);
        writeArray(d,frame.membraneRays(),16);
        writeArray(d,frame.globalProprioception(),48);
        writeArray(d,frame.inner74x7(),74*7);
        if(frame.lastExecutedAvailable)
            writeArray(d,frame.lastExecuted64(),64);
    }
    private static Metadata readFrame(DataInputStream d,DataOutputStream copy)
              throws IOException {
        long ep=d.readLong(),frame=d.readLong(),ticks=d.readLong(),hash=d.readLong();
        boolean before=d.readBoolean();double time=d.readDouble();
        boolean previous=d.readBoolean();
        check(ep>0&&frame>=0&&ticks>=0&&Double.isFinite(time)&&time>=0,
              "bad physical time/index");
        copy.writeLong(ep);copy.writeLong(frame);copy.writeLong(ticks);copy.writeLong(hash);
        copy.writeBoolean(before);copy.writeDouble(time);copy.writeBoolean(previous);
        readArray(d,copy,7);
        readArray(d,copy,96*12);
        readArray(d,copy,16);
        readArray(d,copy,48);
        readArray(d,copy,74*7);
        if(previous)readArray(d,copy,64);
        return new Metadata(ep,frame,ticks,hash,before,previous,time);
    }
    private static long fold(long hash,float[] commands) {
        for(float v:commands)
            hash=(hash^Float.floatToIntBits(v))*1099511628211L;
        return hash;
    }
    private static void verify(Zero3OrganismStoreV322.Trace trace,
                  Metadata pre,Metadata post,long episode,long hash,
                  double actualWork,int grips,int bilateral)throws IOException {
        check(trace!=null&&trace.decision>=0&&pre.episode==episode&&
              post.episode==episode&&pre.pre&&!post.pre&&
              pre.frame==trace.decision&&post.frame==trace.decision+1&&
              pre.ticks==trace.startTick&&post.ticks==trace.endTick&&
              post.ticks-pre.ticks==STEPS&&pre.hash==trace.preHash&&
              post.hash==trace.postHash&&
              pre.time<post.time&&hash==trace.actuatorHash&&
              trace.gripFrames==grips&&trace.bilateralFrames==bilateral,
              "decision causality/physical sensors do not match trace");
        // Measured work is recorded in each real physics substep, not a synthetic reward.
        check(Double.doubleToLongBits(actualWork)==trace.workBits,
              "substep physical work differs from decision outcome");
    }
    public static Zero3FullCausalExperienceV322B capture(
                  Zero3OrganismStoreV322.Trace trace,
                  Zero3SensoryFrameV315 before,Zero3SensoryFrameV315 after,
                  List<float[]> executed,List<Float> measuredWork,
                  int grips,int bilateral)throws IOException {
        check(trace!=null&&before!=null&&after!=null&&
              executed!=null&&measuredWork!=null&&
              executed.size()==STEPS&&measuredWork.size()==STEPS,
              "incomplete PRE-action-physics-POST transaction");
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(stream);
        d.writeInt(MAGIC);d.writeInt(VERSION);d.writeInt(trace.decision);
        writeFrame(d,before);
        long hash=0xcbf29ce484222325L;
        double work=0;
        for(int i=0;i<STEPS;i++){
            float[] values=executed.get(i);
            writeArray(d,values,OUTPUTS);
            hash=fold(hash,values);
            float w=measuredWork.get(i);finite(w);
            check(w>=0,"negative actual work");
            d.writeInt(Float.floatToRawIntBits(w));
            work+=w;
        }
        writeFrame(d,after);d.writeInt(grips);d.writeInt(bilateral);
        verify(trace,new Metadata(before.episodeId,before.frameId,before.solverTicks,
                                  before.hash,before.preAction,before.lastExecutedAvailable,
                                  before.simulationTime),
                     new Metadata(after.episodeId,after.frameId,after.solverTicks,
                                  after.hash,after.preAction,after.lastExecutedAvailable,
                                  after.simulationTime),
                     before.episodeId,hash,work,grips,bilateral);
        d.flush();byte[] b=stream.toByteArray();
        check(b.length<=MAX_RECORD,"record too large");
        return decode(b);
    }
    public static Zero3FullCausalExperienceV322B decode(byte[] b)throws IOException {
        check(b!=null&&b.length>100&&b.length<=MAX_RECORD,"record size");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(b));
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        DataOutputStream copy=new DataOutputStream(stream);
        int magic=d.readInt(),version=d.readInt(),decision=d.readInt();
        check(magic==MAGIC&&version==VERSION&&decision>=0,"record schema");
        copy.writeInt(magic);copy.writeInt(version);copy.writeInt(decision);
        Metadata before=readFrame(d,copy);
        long hash=0xcbf29ce484222325L;
        double work=0;
        for(int i=0;i<STEPS;i++){
            float[] action=readArray(d,copy,OUTPUTS);
            hash=fold(hash,action);
            int bits=d.readInt();float delta=Float.intBitsToFloat(bits);
            finite(delta);check(delta>=0,"invalid physical cost");
            copy.writeInt(bits);work+=delta;
        }
        Metadata after=readFrame(d,copy);
        int grip=d.readInt(),bilateral=d.readInt();
        copy.writeInt(grip);copy.writeInt(bilateral);copy.flush();
        check(d.available()==0&&Arrays.equals(b,stream.toByteArray())&&
              before.frame==decision&&after.frame==decision+1&&
              before.episode==after.episode&&before.pre&&!after.pre&&
              after.ticks-before.ticks==STEPS&&grip>=0&&grip<=STEPS&&
              bilateral>=0&&bilateral<=grip,
              "invalid or noncanonical full experience");
        return new Zero3FullCausalExperienceV322B(decision,before.episode,
                  before.hash,after.hash,before.ticks,after.ticks,hash,b);
    }
    public void validate(Zero3OrganismStoreV322.Trace trace,long expectedEpisode)
              throws IOException {
        check(trace!=null&&decision==trace.decision&&episode==expectedEpisode&&
              preHash==trace.preHash&&postHash==trace.postHash&&
              preTick==trace.startTick&&postTick==trace.endTick&&
              actuatorHash==trace.actuatorHash,
              "archive trace doesn't correspond to physical journal");
    }
}
