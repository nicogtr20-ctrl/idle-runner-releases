package com.overmind.zero;

import java.util.Arrays;

/**
 * Release guard: ZERO's ONE recurrent decision continues to control cortex.
 * The motor learner may change only after a measured survival consequence.
 * No distance, exploratory movement or fake pickup can train the cortex.
 */
public final class SymbioteMotorConsequenceRegression {
    private static void require(boolean pass,String msg){
        if(!pass)throw new AssertionError(msg);
    }
    private static float[] weights(SymbioteMotorHeadV1 head){
        String[] parts=head.encode().split("\\|",-1);
        float[] w=new float[parts.length-6];
        for(int i=0;i<w.length;i++)w[i]=Float.parseFloat(parts[i+6]);
        return w;
    }
    private static float change(float[] a,float[] b){
        float d=0f;for(int i=0;i<a.length;i++)d+=Math.abs(a[i]-b[i]);return d;
    }
    private static void action(SymbioteMotorHeadV1 motor,float explore){
        // A true selected action's already-grounded body and cortex inputs.
        // No preprogrammed stroke or 'walk to food' motor command.
        float[] cortex=new float[DistributedBodySense.FEATURES];
        float[] recurrent=new float[RecurrentCoreV22.Z];
        cortex[DistributedBodySense.SHAPE]=.35f;
        recurrent[0]=.7f;
        motor.chosen(7,cortex,recurrent,explore);
    }
    public static void main(String[] args){
        SymbioteMotorHeadV1 learner=new SymbioteMotorHeadV1();
        float[] virgin=weights(learner);
        require(virgin.length==84,"OTA151 learned cortex weight width changed");
        action(learner,1f);
        learner.consequence(0f,0f,0f,0f,true,false);
        require(change(virgin,weights(learner))==0f,
            "uncorroborated waving became automatic learning reward");
        action(learner,1f);
        learner.consequence(0f,0f,0f,0f,true,true);
        require(change(virgin,weights(learner))==0f,
            "holdout with no actual outcome modified cortex");

        // The exact measured mechanical work from the real OTA151 solver
        // must be zero without actuation, positive under real muscle drive.
        MotorBrain.Body passiveBody=new MotorBrain.Body();
        Morphology.Surface passiveSkin=new Morphology.Surface(passiveBody);
        SymbioteMatterV1 passive=new SymbioteMatterV1();
        passive.reset(passiveBody,passiveSkin);
        float[] calm=new float[64];
        for(int i=0;i<26;i++)passive.step(passiveBody,passiveSkin,calm,.025f);
        require(passive.work==0f,
            "passive fluid supposedly consumed active motor work");

        MotorBrain.Body activeBody=new MotorBrain.Body();
        Morphology.Surface activeSkin=new Morphology.Surface(activeBody);
        SymbioteMatterV1 active=new SymbioteMatterV1();
        active.reset(activeBody,activeSkin);
        float[] drive=new float[64];
        drive[0]=.8f;drive[4]=.65f;drive[8]=-.45f;
        for(int i=0;i<26;i++)active.step(activeBody,activeSkin,drive,.025f);
        float work=active.work;
        require(Float.isFinite(work)&&work>.00001f,"real contractions produced no work");
        float reserve=70f;
        float charged=Math.min(reserve,work*8f);
        reserve-=charged;
        require(charged>0f&&reserve<70f&&
            Math.abs((70f-reserve)-charged)<.0001f,
            "claimed muscle metabolic energy not actually debited");

        action(learner,1f);
        learner.consequence(0f,0f,0f,charged,true,false);
        require(change(virgin,weights(learner))>0f,
            "charged real muscle work did not create any learning gradient");
        float[] fuelLearnt=weights(learner);
        action(learner,1f);
        learner.consequence(0f,0f,0f,charged,true,true);
        require(change(fuelLearnt,weights(learner))==0f,
            "heldout contaminated learned motor weights");
        // The same cortex also updates from REAL environmental energy
        // damage even if no contact with a food source has yet occurred.
        action(learner,1f);
        learner.consequence(0f,0f,3f,0f,true,false);
        require(change(fuelLearnt,weights(learner))>0f,
            "actual adverse storm consequence still ignored");
        float[] afterStorm=weights(learner);
        action(learner,1f);
        learner.consequence(26f,0f,0f,0f,true,false);
        require(change(afterStorm,weights(learner))>0f,
            "real positive food outcome failed to reinforce chosen cortex");
        // Deformations or spurious labels NEVER enter a reward. The only
        // four credit inputs are energetic gain, real harm, ambient energy
        // drained, and reserved fuel debit, all finite and bounded.
        String oldFormat=learner.encode();
        SymbioteMotorHeadV1 recovered=new SymbioteMotorHeadV1();
        require(recovered.restore(oldFormat)&&
                change(weights(learner),weights(recovered))==0f,
            "genuine learned motor skill lost during persistence");
        System.out.println("PASS OTA152: true charged cortex work, storm/food survival"+
            " consequence, zero-movement-reward, holdout isolation, save continuity");
    }
}
