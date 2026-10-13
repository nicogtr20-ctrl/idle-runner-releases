package com.overmind.zero;

import java.util.Arrays;

/** Executable, zero-Android, falsifiable tests for the unified ZERO21 ledger. */
public final class UnifiedCognitionV21Regression {
    private static void require(boolean ok,String msg){
        if(!ok)throw new AssertionError(msg);
    }
    private static float[] senses(float reserve,float proximity,float contact){
        float[] x=new float[UnifiedCognitionV21.SENSES];
        x[0]=reserve;x[4]=contact;
        x[6]=proximity;
        return x;
    }
    private static void event(UnifiedCognitionV21 c,int action,float[] before,
                              float[] after,float motion,float energy,
                              boolean train,boolean heldout){
        c.chosen(action,before,new float[16],3,.1f,.2f);
        if(energy!=0)c.energyContact(energy);
        c.consequence(after,motion,train,heldout);
    }
    public static void main(String[] args){
        float[] full=senses(.90f,0f,0f);
        float[] lowNear=senses(.10f,.7f,.25f);
        require(UnifiedCognitionV21.context(full)==0,"wrong safe/far context");
        require(UnifiedCognitionV21.context(lowNear)==3,"wrong low/near context");
        UnifiedCognitionV21 c=new UnifiedCognitionV21();
        require(c.confidence()==0f,"virgin model supplied advice");
        event(c,8,lowNear,lowNear,1.5f,0f,true,false);
        event(c,8,lowNear,lowNear,0f,30f,true,false);
        event(c,8,lowNear,lowNear,0f,-7f,true,false);
        require(c.observations()==3&&c.training()==3,"missing real intervals");
        require(c.physicalEvents()==1&&c.energyContacts()==2,
                "mechanical change was confused with energy events");
        require(c.lastBelief().context==3&&c.lastBelief().provisionalHypothesis==3,
                "sensor-derived world/body snapshot lost");
        String saved=c.encode();
        // An interrupted interval must not be saved as a successful action.
        c.chosen(4,full,new float[16],-1,0,0);
        c.energyContact(10f);
        UnifiedCognitionV21 restored=new UnifiedCognitionV21();
        require(restored.restore(saved),"profile rejected");
        require(restored.energyContacts()==2&&restored.observations()==3,
                "phantom consequence on restore");
        require(restored.encode().equals(saved),"non-deterministic lossless save");
        restored.consequence(full,99f,true,false);
        require(restored.observations()==3,"restored pending action leaked");
        for(int i=0;i<140;i++){
            // TRAINING and HOLDOUT use a single real result, never a guessed target.
            event(restored,8,lowNear,lowNear,0f,0f,false,true);
        }
        require(restored.training()==3&&restored.holdouts()==140,
                "heldout contaminated learned weights");
        require(restored.confidence()==0f,"naive static world became trusted");
        String held=restored.encode();
        for(String bad:Arrays.asList(
                "uc21|broken",
                held.replaceFirst("uc21","unknown"),
                held.replaceFirst("\\|3\\|","|NaN|"),
                held.substring(0,held.length()-3),
                held+"|9",
                held.replaceFirst("\\|0\\|","|Infinity|"))){
            require(!restored.restore(bad),"corrupt model accepted: "+bad.substring(0,Math.min(28,bad.length())));
            require(restored.encode().equals(held),"corrupt model mutated live brain");
        }
        restored.resetLife();
        require(restored.observations()==143&&restored.training()==3,
                "death erased consolidated knowledge");
        require(restored.lastBelief().provisionalHypothesis==-1,
                "death retained phantom scene identity");
        float[] invalid=full.clone();invalid[4]=Float.NaN;
        restored.chosen(0,invalid,new float[16],-1,0,0);
        restored.energyContact(30);
        restored.consequence(full,3,true,false);
        require(restored.observations()==143&&restored.invalid()>0,
                "invalid sensory frame invented experience");
        // No external action selection, no grip commands and no class labels exist.
        require(restored.summary().contains("SHADOW"),"unsafe status labeling");
        System.out.println("ZERO21 unified evidence: sensor context, independent real outcomes, heldout isolation, atomic save, death and bad-input fail-closed PASS");
    }
}
