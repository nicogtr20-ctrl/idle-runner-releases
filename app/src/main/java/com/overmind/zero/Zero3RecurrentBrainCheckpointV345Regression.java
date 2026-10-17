package com.overmind.zero;

import java.util.Arrays;
import java.util.zip.CRC32;

/** V345 G4 prerequisite ONLY: deterministic GRU actor/critic cold state. */
public final class Zero3RecurrentBrainCheckpointV345Regression {
    private Zero3RecurrentBrainCheckpointV345Regression(){}
    private static void yes(boolean state,String reason){
        if(!state)throw new AssertionError("V345 GRU "+reason);
    }
    private static float[] sensory(int n,int turn){
        float[] raw=new float[SensorimotorMind.S];
        for(int j=0;j<raw.length;j++)
            raw[j]=(float)(.37*Math.sin((j+1)*.113*(n+1)+
                                         turn*.091));
        return raw;
    }
    private static float reward(int n){
        return (n%5-2)*.13f;
    }
    private static void sameIntent(Zero3LearningFoundation.MotorIntent a,
                                   Zero3LearningFoundation.MotorIntent b){
        yes(a.episodeId==b.episodeId&&a.decisionId==b.decisionId&&
            a.revision==b.revision&&
            Float.floatToRawIntBits(a.x)==Float.floatToRawIntBits(b.x)&&
            Float.floatToRawIntBits(a.y)==Float.floatToRawIntBits(b.y)&&
            Float.floatToRawIntBits(a.effort)==Float.floatToRawIntBits(b.effort)&&
            Float.floatToRawIntBits(a.adhesion)==Float.floatToRawIntBits(b.adhesion)&&
            Float.floatToRawIntBits(a.duration)==Float.floatToRawIntBits(b.duration),
            "actor intention drift after cold restore");
    }
    private static byte[] withChecksum(byte[] bytes){
        byte[] result=bytes.clone();
        CRC32 crc=new CRC32();crc.update(result,0,result.length-4);
        int x=(int)crc.getValue(),n=result.length;
        result[n-4]=(byte)(x>>>24);result[n-3]=(byte)(x>>>16);
        result[n-2]=(byte)(x>>>8);result[n-1]=(byte)x;
        return result;
    }
    private static void failedAtomicRestore(byte[] broken){
        Zero3RecurrentBrain fresh=new Zero3RecurrentBrain();
        byte[] untouched=fresh.checkpointAtBoundaryV345();
        boolean failed=false;
        try{fresh.restoreAtBoundaryV345(broken);}
        catch(IllegalArgumentException expected){failed=true;}
        yes(failed,"invalid restore falsely accepted");
        yes(Arrays.equals(untouched,fresh.checkpointAtBoundaryV345()),
            "failed decode partially mutated neuron weights or RNG");
    }
    public static void main(String[] args){
        Zero3RecurrentBrain source=new Zero3RecurrentBrain();
        for(int n=0;n<24;n++){
            source.choose(sensory(n,0),3451,n,0);
            source.consequence(sensory(n,1),reward(n),n%9==0);
        }
        yes(source.trainingUpdates()>0&&source.validations()>0&&
            source.recurrentUpdates()>0,
            "synthetic pretraining did not change real GRU/critic");
        byte[] checkpoint=source.checkpointAtBoundaryV345();
        Zero3RecurrentBrain restored=new Zero3RecurrentBrain();
        restored.restoreAtBoundaryV345(checkpoint);
        yes(Arrays.equals(checkpoint,restored.checkpointAtBoundaryV345()),
            "complete learned GRU actor critic hidden RNG fingerprint mismatch");
        boolean repeated=false;
        try{restored.restoreAtBoundaryV345(checkpoint);}
        catch(IllegalStateException expected){repeated=true;}
        yes(repeated,"replayed checkpoint allowed overwrite of live state");
        for(int n=24;n<80;n++){
            float[] pre=sensory(n,0),post=sensory(n,1);
            sameIntent(source.choose(pre,3451,n,0),
                       restored.choose(pre,3451,n,0));
            if(n==36){
                boolean busy=false;
                try{restored.checkpointAtBoundaryV345();}
                catch(IllegalStateException expected){busy=true;}
                yes(busy,"pending decision was serialized");
            }
            source.consequence(post,reward(n),n%9==0);
            restored.consequence(post,reward(n),n%9==0);
            yes(Arrays.equals(source.parameterSnapshot(),
                              restored.parameterSnapshot()),
                "GRU/actor/critic/world weights drifted at decision "+n);
            yes(Arrays.equals(source.recurrentState(),restored.recurrentState()),
                "64 actual recurrent units drifted after restore "+n);
        }
        yes(source.trainingUpdates()==restored.trainingUpdates()&&
            source.recurrentUpdates()==restored.recurrentUpdates()&&
            source.validations()==restored.validations(),
            "actual optimizer counters drifted");
        yes(Arrays.equals(source.checkpointAtBoundaryV345(),
                          restored.checkpointAtBoundaryV345()),
            "final whole neural checkpoint not bit identical");
        byte[] bad=checkpoint.clone();bad[110]^=0x40;
        failedAtomicRestore(bad);
        failedAtomicRestore(Arrays.copyOf(checkpoint,checkpoint.length-9));
        byte[] corruptVersion=checkpoint.clone();
        corruptVersion[7]=(byte)0x00;
        failedAtomicRestore(withChecksum(corruptVersion));
        byte[] appended=Arrays.copyOf(checkpoint,checkpoint.length+4);
        failedAtomicRestore(withChecksum(appended));
        System.out.println("V345 GRU64_ACTOR_DUAL_CRITIC_CHECKPOINT=PASS "+
             "56_further_identical_choices=true "+
             "weights_and_recurrence_bitwise=true "+
             "heldout_preserved=true ongoingDecisionReject=true "+
             "corrupt_truncated_version_extra_reject_atomic=true "+
             "V325_NOT_SWITCHED G4_PENDING G7_PENDING NO_OTA");
    }
}
