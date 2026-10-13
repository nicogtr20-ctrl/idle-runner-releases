package com.overmind.zero;

import java.util.Random;

/**
 * Regression from real phone evidence: successive online MANIPULATION and
 * MEMBRANE mutations must not overwrite a genuinely verified bilateral box
 * grip merely because a later window wandered or touched a different resource.
 * No target access, force injection, physics override or synthetic training.
 */
public final class BoxGripRetentionRegression {
    private BoxGripRetentionRegression(){}
    static void require(boolean good,String why){if(!good)throw new AssertionError(why);}
    private static final float[] MOTOR=MotorBrain.virgin(new Random(11));
    private static final float[] HAND=ContactLearning.virgin();
    private static final float[] MEMBRANE=Morphology.virgin(new Random(9));
    private static OnlineBodyLearning.Result window(OnlineBodyLearning x,
            boolean gainedNovelty,int actualContacts,int actualOpposed){
        OnlineBodyLearning.Result r=null;
        for(int i=0;i<OnlineBodyLearning.WINDOW;i++){
            r=x.observe(0f,2f,1f,0,70f,70f,gainedNovelty?2:0,
                 0f,0f,5f,0f,false,1f,0f,MOTOR,HAND,MEMBRANE,
                 0f,actualContacts,actualOpposed);
        }
        return r;
    }
    private static void toHand(OnlineBodyLearning x){
        // Existing online learning must see four real mobile windows first.
        for(int i=0;i<4;i++)window(x,false,0,0);
        require(x.activeDomain()==OnlineBodyLearning.MANIPULATION,
            "Could not enter hand-candidate testing without touching hidden state");
    }
    public static void main(String[] ignored){
        OnlineBodyLearning x=new OnlineBodyLearning();
        toHand(x);
        require(window(x,false,12,9)==null,"hand base window should spawn candidate");
        OnlineBodyLearning.Result badHand=window(x,true,12,0);
        require(badHand!=null&&!badHand.accepted&&
                x.protectedBoxTechniqueRejections==1&&
                x.activeDomain()==OnlineBodyLearning.MEMBRANE,
            "Novel exploration overwrote real held box hand technique");
        require(window(x,false,12,9)==null,"membrane base window should spawn candidate");
        OnlineBodyLearning.Result badSkin=window(x,true,0,0);
        require(badSkin!=null&&!badSkin.accepted&&
                x.protectedBoxTechniqueRejections==2,
            "New membrane erased previously verified grip without comparable contact");

        OnlineBodyLearning good=new OnlineBodyLearning();
        toHand(good);
        window(good,false,12,8);
        OnlineBodyLearning.Result valid=window(good,true,12,8);
        require(valid!=null&&valid.accepted&&good.protectedBoxTechniqueRejections==0,
            "Candidate with equally verified grip and genuine exploration must still learn");

        OnlineBodyLearning noProof=new OnlineBodyLearning();
        toHand(noProof);
        window(noProof,false,12,0);
        OnlineBodyLearning.Result exploratory=window(noProof,true,12,0);
        require(exploratory!=null&&exploratory.accepted,
            "Virgin hand must retain exploratory trial-and-error without false grip proof");
        System.out.println("PASS: genuine two-sided square-grip memory retained on hand/membrane updates; new physical discoveries remain possible");
    }
}
