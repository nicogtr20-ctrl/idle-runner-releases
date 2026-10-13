package com.overmind.zero;

import java.util.Arrays;

/** Tests actual-effect evidence; intentionally never executes a motor pair. */
public final class CompetenceMemoryV21Regression {
    private static void yes(boolean b,String s){if(!b)throw new AssertionError(s);}
    public static void main(String[] args){
        CompetenceMemoryV21 memory=new CompetenceMemoryV21();
        float[] hand=new float[ContactLearning.PARAMS];
        float[] membrane=new float[Morphology.PARAMS];
        float[] senses=new float[SensorimotorMind.S];senses[0]=.70f;senses[4]=.25f;
        yes(!memory.witnessed(hand,membrane,senses,false,2f,1f),
            "plain touch must never archive");
        yes(!memory.witnessed(hand,membrane,senses,true,0f,1f),
            "grip command with zero real external displacement archived");
        yes(memory.size()==0,"false proven technique");
        for(int i=0;i<8;i++)memory.witnessed(hand,membrane,senses,true,.5f,.9f);
        yes(memory.size()==1&&memory.actualWitnesses()==8,"actual grip archive");
        yes(memory.candidate(senses)==null,"one life cannot prove transfer");
        memory.newLife();
        for(int i=0;i<7;i++)memory.witnessed(hand,membrane,senses,true,.5f,.9f);
        CompetenceMemoryV21.Technique candidate=memory.candidate(senses);
        yes(candidate!=null&&candidate.distinctLives==2&&candidate.realMotionFrames==15,
            "proof over two lives not consolidated");
        candidate.hand[0]=4f;
        yes(memory.candidate(senses).hand[0]==0,"mutable reference leaked");
        float[] different=senses.clone();different[14]=1f;different[15]=1f;
        different[16]=1f;different[17]=1f;different[18]=1f;
        yes(memory.candidate(different)==null,"incompatible context reused");
        String saved=memory.encode();
        CompetenceMemoryV21 restored=new CompetenceMemoryV21();
        yes(restored.restore(saved)&&saved.equals(restored.encode()),"saved motor pair changed");
        restored.resumeLife();
        yes(restored.candidate(senses)!=null,"resume lost established skill");
        for(String corrupt:Arrays.asList("cm21|wat",saved+"|3",saved.substring(0,saved.length()-4),
            saved.replaceFirst("cm21","cm22"),saved.replaceFirst(",0.0",",NaN"))){
            yes(!restored.restore(corrupt),"corrupt pair accepted");
            yes(restored.encode().equals(saved),"atomic restore failed");
        }
        yes(memory.summary().contains("SHADOW"),"misleading motor authority");
        System.out.println("ZERO21 durable competence: real opposed grip and motion only, independent lives, context, defensive copies, migration safety PASS");
    }
}
