package com.overmind.zero;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.zip.*;

/** Capacity, causal training, strict heldout selection, recurrence, single
 * primary policy, save migration and exact visible neuron-count regression. */
public final class Zero159NeurogenesisRegression {
    private static void ok(boolean yes,String msg){if(!yes)throw new AssertionError(msg);}
    private static void trainFixture(NeurogenesisV22 ng,boolean withHoldout){
        for(int i=0;i<2500;i++){
            float[] z=new float[RecurrentCoreV22.Z];
            float stimulus=((i*37)%101)/50f-1f;
            z[6]=stimulus;z[0]=.65f;
            z[RecurrentCoreV22.F]=(float)Math.sin(i*.29f)*.15f;
            // ONLY the executed action receives an outcome; heldout is never
            // used to update any incoming/recurrent/outgoing neuron weight.
            int action=3;
            float earned=.03f+.65f*stimulus,baseline=.03f;
            boolean heldout=withHoldout&&i%5==0;
            ng.observe(z,action,earned,baseline,!heldout,heldout);
        }
    }
    private static String legacyVersion(String encoded)throws Exception{
        String[] p=encoded.split("\\|",-1);
        byte[] packed=Base64CodecV22.decode(p[2]);
        InflaterInputStream input=new InflaterInputStream(new ByteArrayInputStream(packed));
        ByteArrayOutputStream raw=new ByteArrayOutputStream();
        byte[] buf=new byte[4096];for(int len;(len=input.read(buf))>=0;)raw.write(buf,0,len);
        byte[] bytes=raw.toByteArray();
        int mark=-1;
        for(int i=bytes.length-4;i>=0;i--){
            int k=((bytes[i]&255)<<24)|((bytes[i+1]&255)<<16)|
                  ((bytes[i+2]&255)<<8)|(bytes[i+3]&255);
            if(k==NeurogenesisV22.MAGIC){mark=i;break;}
        }
        ok(mark>100,"Missing optional growth trailer");
        byte[] old=Arrays.copyOf(bytes,mark);
        CRC32 crc=new CRC32();crc.update(old);
        ByteArrayOutputStream zipped=new ByteArrayOutputStream();
        DeflaterOutputStream out=new DeflaterOutputStream(zipped);
        out.write(old);out.close();
        return "rc22|"+crc.getValue()+"|"+Base64CodecV22.encode(zipped.toByteArray());
    }
    public static void main(String[] args)throws Exception{
        NeurogenesisV22 noProof=new NeurogenesisV22();
        trainFixture(noProof,false);
        ok(noProof.totalNeurons()==24&&noProof.addedNeurons()==0,
            "Unvalidated candidate was inserted into live brain");
        NeurogenesisV22 grown=new NeurogenesisV22();
        trainFixture(grown,true);
        ok(grown.growthAttempts()>=1,"Genuine candidate was never trained");
        ok(grown.addedNeurons()>0,
            "Independent heldout improvement never admitted truly useful new neuron");
        ok(grown.totalNeurons()==24+grown.addedNeurons()&&grown.maxNeurons()==40,
            "Neuron cardinality differs from real topology");
        float[] z=new float[RecurrentCoreV22.Z];z[0]=.65f;z[6]=.8f;
        float signal=grown.actionCorrection(z,3);
        ok(Float.isFinite(signal)&&Math.abs(signal)>.000001f,
            "Accepted neuron is disconnected from active action scoring");
        float[] brainParams=new float[grown.addedParameters()];
        for(int k=0;k<brainParams.length;k++)brainParams[k]=grown.parameterAt(k);
        DataOutputStream d;
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        d=new DataOutputStream(out);grown.write(d);d.close();
        NeurogenesisV22 restored=new NeurogenesisV22();
        DataInputStream in=new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        ok(in.readInt()==NeurogenesisV22.MAGIC,"Growth serialization magic");
        restored.read(in);
        ok(restored.totalNeurons()==grown.totalNeurons()&&
           restored.addedParameters()==brainParams.length,
           "Saved expanded topology changed under restoration");
        for(int k=0;k<brainParams.length;k++)
            ok(Float.floatToIntBits(brainParams[k])==
               Float.floatToIntBits(restored.parameterAt(k)),
               "Expanded neuronal synapse lost after persistence");
        ok(Math.abs(restored.actionCorrection(z,3)-signal)<1e-7f,
            "Accepted neurogenesis disconnected after restore");
        RecurrentCoreV22 live=new RecurrentCoreV22();
        String base=live.encode();
        ok(live.totalNeurons()==24,"Virgin active brain should begin at exactly 24 neurons");
        // Inject only validated real candidate topology in a controlled
        // test; production neurogenesis is fed by consequence() live outcomes.
        Field field=RecurrentCoreV22.class.getDeclaredField("neuralGrowth");
        field.setAccessible(true);
        ((NeurogenesisV22)field.get(live)).copyFrom(grown);
        ok(live.totalNeurons()==grown.totalNeurons(),"Core did not adopt accepted nodes");
        RecurrentCoreV22 clone=new RecurrentCoreV22();
        ok(clone.restore(live.encode())&&clone.totalNeurons()==live.totalNeurons()
           &&clone.encode().equals(live.encode()),
           "Expanded saved brain failed atomic exact checkpoint roundtrip");
        ok(clone.parameterCount()>new RecurrentCoreV22().parameterCount(),
           "New neural synapses excluded from actual parameter inspector");
        // OTA158 rc22 payload had no growth section. Full migration must
        // preserve the original recurrent state and all learned weights.
        RecurrentCoreV22 backward=new RecurrentCoreV22();
        ok(backward.restore(legacyVersion(base))&&backward.totalNeurons()==24,
            "Old trained rc22 snapshot no longer loads as unexpanded 2.2");
        ok(backward.parameterAt(1)==live.parameterAt(1),
           "Legacy model trained coefficients were overwritten");
        String complete=clone.encode();
        ok(!clone.restore(complete.substring(0,complete.length()-9))&&
           clone.encode().equals(complete),
           "Corrupt grown checkpoint overwrote healthy saved neurons");
        System.out.println("PASS ZERO159: train/holdout separation, real accepted recurrent nodes, policy influence, exact count, checkpoint and OTA158 migration");
    }
}