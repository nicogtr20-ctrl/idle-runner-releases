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
    public final long episode,preHash,postHash,preTick,postTick,actuatorHash,workBits;
    public final int gripFrames,bilateralFrames,plannedSteps,interruptionReason;
    private final byte[] bytes;
    private final float[] unifiedPre,unifiedPost;

    private Zero3FullCausalExperienceV322B(int n,long ep,long pre,long post,
              long beforeTick,long afterTick,long commands,long workBits,
              int gripFrames,int bilateralFrames,byte[] immutable,float[] upre,float[] upost,int planned,int reason) {
        plannedSteps=planned;interruptionReason=reason;decision=n;episode=ep;preHash=pre;postHash=post;
        preTick=beforeTick;postTick=afterTick;actuatorHash=commands;
        this.workBits=workBits;this.gripFrames=gripFrames;
        this.bilateralFrames=bilateralFrames;
        bytes=immutable.clone();
        unifiedPre=upre==null?null:upre.clone();unifiedPost=upost==null?null:upost.clone();
    }
    public boolean hasPartitionV353(){return bytes[6]==1&&((bytes[7]&255)==97||(bytes[7]&255)==98);}
    public boolean heldoutV353(){return hasPartitionV353()&&partitionV354()==1;}
    public int partitionV354(){return (bytes[7]&255)==98?bytes[bytes.length-29]&255:((bytes[7]&255)==97?bytes[bytes.length-1]&255:-1);}
    public float[] intentionV354(){
        if((bytes[7]&255)!=98)return null;
        try{DataInputStream d=new DataInputStream(new ByteArrayInputStream(bytes,bytes.length-28,28));float[] a=new float[5];for(int i=0;i<5;i++)a[i]=d.readFloat();return a;}catch(IOException impossible){throw new IllegalStateException(impossible);}
    }
    static Zero3FullCausalExperienceV322B withModeV354(Zero3FullCausalExperienceV322B record,int mode,float[] intent,long option)throws IOException{
        check(record!=null&&(record.bytes[7]&255)==96&&mode>=0&&mode<=2&&intent!=null&&intent.length==5&&option>=0,"V354 causal metadata");
        ByteArrayOutputStream b=new ByteArrayOutputStream();byte[] raw=record.bytes.clone();raw[7]=98;b.write(raw);
        DataOutputStream d=new DataOutputStream(b);d.writeByte(mode);writeArray(d,intent,5);d.writeLong(option);d.flush();return decode(b.toByteArray());
    }
    static Zero3FullCausalExperienceV322B withPartitionV353(Zero3FullCausalExperienceV322B record,boolean heldout)throws IOException{
        check(record!=null&&(record.bytes[7]&255)==96,"V353 requires V352 physical record");
        byte[] encoded=Arrays.copyOf(record.bytes,record.bytes.length+1);encoded[7]=97;
        encoded[encoded.length-1]=(byte)(heldout?1:0);return decode(encoded);
    }
    public boolean hasUnifiedSensesV351(){return unifiedPre!=null;}
    public float[] neuralBeforeV351(){return unifiedPre==null?null:unifiedPre.clone();}
    public float[] neuralAfterV351(){return unifiedPost==null?null:unifiedPost.clone();}
    static Zero3FullCausalExperienceV322B withUnified(
            Zero3FullCausalExperienceV322B record,
            Zero3GruSensoryV347.Unified pre,Zero3GruSensoryV347.Unified post)throws IOException{
        check(record!=null&&!record.hasUnifiedSensesV351()&&pre!=null&&post!=null&&
            pre.intact()&&post.intact()&&pre.preAction&&!post.preAction&&
            pre.episode==record.episode&&post.episode==record.episode&&
            pre.decision==record.decision&&post.decision==record.decision+1&&
            pre.mechanicalHash==record.preHash&&post.mechanicalHash==record.postHash&&
            pre.ticks==record.preTick&&post.ticks==record.postTick,
            "V351 future/foreign/missing unified sensory record");
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        byte[] base=record.encoded();
        base[4]=0;base[5]=0;base[6]=1;base[7]=95; // version351, legacy bytes otherwise identical
        stream.write(base);
        DataOutputStream out=new DataOutputStream(stream);
        writeArray(out,pre.neural(),194);writeArray(out,post.neural(),194);out.flush();
        return decode(stream.toByteArray());
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
                  double actualWork,int grips,int bilateral,int steps)throws IOException {
        check(trace!=null&&trace.decision>=0&&pre.episode==episode&&
              post.episode==episode&&pre.pre&&!post.pre&&
              pre.frame==trace.decision&&post.frame==trace.decision+1&&
              pre.ticks==trace.startTick&&post.ticks==trace.endTick&&
              post.ticks-pre.ticks==steps&&pre.hash==trace.preHash&&
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
        return captureRecord(trace,before,after,executed,measuredWork,grips,bilateral,VERSION,12,0,null,null);
    }
    static Zero3FullCausalExperienceV322B captureVariableV352(
            Zero3OrganismStoreV322.Trace trace,Zero3SensoryFrameV315 before,Zero3SensoryFrameV315 after,
            List<float[]> executed,List<Float> work,int grips,int bilateral,int planned,int reason,
            Zero3GruSensoryV347.Unified pre,Zero3GruSensoryV347.Unified post)throws IOException {
        check(pre!=null&&post!=null&&pre.intact()&&post.intact()&&pre.preAction&&!post.preAction&&
              pre.mechanicalHash==before.hash&&post.mechanicalHash==after.hash&&
              pre.episode==before.episodeId&&post.episode==after.episodeId&&
              pre.decision==before.frameId&&post.decision==after.frameId,"V352 wrong unified lineage");
        return captureRecord(trace,before,after,executed,work,grips,bilateral,352,planned,reason,pre.neural(),post.neural());
    }
    private static Zero3FullCausalExperienceV322B captureRecord(
            Zero3OrganismStoreV322.Trace trace,Zero3SensoryFrameV315 before,Zero3SensoryFrameV315 after,
            List<float[]> executed,List<Float> measuredWork,int grips,int bilateral,
            int version,int planned,int reason,float[] upre,float[] upost)throws IOException {
        int steps=version==352?(int)(trace.endTick-trace.startTick):STEPS;
        check(trace!=null&&before!=null&&after!=null&&
              executed!=null&&measuredWork!=null&&
              executed.size()==steps&&measuredWork.size()==steps,
              "incomplete PRE-action-physics-POST transaction");
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(stream);
        d.writeInt(MAGIC);d.writeInt(version);d.writeInt(trace.decision);
        if(version==352){d.writeInt(steps);d.writeInt(planned);d.writeInt(reason);}
        writeFrame(d,before);
        long hash=0xcbf29ce484222325L;
        double work=0;
        for(int i=0;i<steps;i++){
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
                     before.episodeId,hash,work,grips,bilateral,steps);
        if(version==352){writeArray(d,upre,194);writeArray(d,upost,194);}
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
        check(magic==MAGIC&&(version==VERSION||version==351||version==352||version==353||version==354)&&decision>=0,"record schema");
        copy.writeInt(magic);copy.writeInt(version);copy.writeInt(decision);
        int steps=STEPS,planned=STEPS,reason=0;
        if(version==352||version==353||version==354){
            steps=d.readInt();planned=d.readInt();reason=d.readInt();
            copy.writeInt(steps);copy.writeInt(planned);copy.writeInt(reason);
            check(steps>=1&&steps<=26&&planned>=10&&planned<=26&&steps<=planned&&
                  reason>=0&&reason<=2&&(reason!=0||steps==planned),"V352 false duration/interruption");
        }
        Metadata before=readFrame(d,copy);
        long hash=0xcbf29ce484222325L;
        double work=0;
        for(int i=0;i<steps;i++){
            float[] action=readArray(d,copy,OUTPUTS);
            hash=fold(hash,action);
            int bits=d.readInt();float delta=Float.intBitsToFloat(bits);
            finite(delta);check(delta>=0,"invalid physical cost");
            copy.writeInt(bits);work+=delta;
        }
        Metadata after=readFrame(d,copy);
        int grip=d.readInt(),bilateral=d.readInt();
        copy.writeInt(grip);copy.writeInt(bilateral);
        float[] unifiedPre=null,unifiedPost=null;
        if(version==351||version==352||version==353||version==354){
            unifiedPre=readArray(d,copy,194);unifiedPost=readArray(d,copy,194);
            for(float[] sense:new float[][]{unifiedPre,unifiedPost})for(float v:sense)
                check(v>=-1f&&v<=1f,"V351 neural input out of range");
            check((unifiedPre[193]==1f)==before.lastAvailable&&
                  (unifiedPost[193]==1f)==after.lastAvailable,
                  "V351 efference availability mismatch");
            if(version==352||version==353||version==354)check((reason!=2||unifiedPost[188]==-1f)&&
                (reason!=1||unifiedPost[189]>0f),"V352 interruption lacks real vital evidence");
        }
        if(version==353||version==354){
            int partition=d.readUnsignedByte();copy.writeByte(partition);
            check(partition<=(version==354?2:1)&&(partition==2||(partition==1)==(version==354?Zero3ConfigurationV354.heldout(decision):Zero3CognitionV353.heldout(decision))),"V353 partition changed after observation");
        }
        if(version==354){
            float[] intent=readArray(d,copy,5);long option=d.readLong();copy.writeLong(option);
            check(option>=0&&Math.abs(Math.hypot(intent[0],intent[1])-1)<.001&&intent[2]>=0&&intent[2]<=1&&
                intent[3]>=0&&intent[3]<=1&&intent[4]>=.2499&&intent[4]<=.6501,"V354 invalid actual intention");
        }
        copy.flush();
        check(d.available()==0&&Arrays.equals(b,stream.toByteArray())&&
              before.frame==decision&&after.frame==decision+1&&
              before.episode==after.episode&&before.pre&&!after.pre&&
              after.ticks-before.ticks==steps&&grip>=0&&grip<=steps&&
              bilateral>=0&&bilateral<=grip,
              "invalid or noncanonical full experience");
        return new Zero3FullCausalExperienceV322B(decision,before.episode,
                  before.hash,after.hash,before.ticks,after.ticks,hash,
                  Double.doubleToLongBits(work),grip,bilateral,b,unifiedPre,unifiedPost,planned,reason);
    }
    public void validate(Zero3OrganismStoreV322.Trace trace,long expectedEpisode)
              throws IOException {
        check(trace!=null&&decision==trace.decision&&episode==expectedEpisode&&
              preHash==trace.preHash&&postHash==trace.postHash&&
              preTick==trace.startTick&&postTick==trace.endTick&&
              actuatorHash==trace.actuatorHash&&
              workBits==trace.workBits&&gripFrames==trace.gripFrames&&
              bilateralFrames==trace.bilateralFrames,
              "archive trace doesn't correspond to physical journal");
    }
}
