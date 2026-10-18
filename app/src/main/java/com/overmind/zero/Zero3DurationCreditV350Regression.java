package com.overmind.zero;

import java.util.Arrays;

/** V350: never train an action dimension which physical V325 did not execute. */
public final class Zero3DurationCreditV350Regression {
    private Zero3DurationCreditV350Regression(){}
    private static void check(boolean ok,String msg){
        if(!ok)throw new AssertionError("V350 "+msg);
    }
    private static float[] sensed(int n,int phase){
        float[] values=new float[Zero3RecurrentBrain.SENSOR];
        for(int j=0;j<values.length;j++)
            values[j]=(float)(.37*Math.sin((j+1)*.113*(n+1)+phase*.097));
        return values;
    }
    private static boolean equal(float a,float b){
        return Float.floatToRawIntBits(a)==Float.floatToRawIntBits(b);
    }
    private static void physicalIdentical(Zero3LearningFoundation.MotorIntent a,
                                          Zero3LearningFoundation.MotorIntent b){
        check(equal(a.x,b.x)&&equal(a.y,b.y)&&equal(a.effort,b.effort)&&
              equal(a.adhesion,b.adhesion),
              "unexecuted duration changed a genuine physical motor output");
    }
    public static void main(String[] args){
        final int stride=Zero3RecurrentBrain.HIDDEN+1;
        final int start=3*Zero3RecurrentBrain.HIDDEN*
            (Zero3RecurrentBrain.SENSOR+Zero3RecurrentBrain.HIDDEN+1)+4*stride;
        Zero3RecurrentBrain original=new Zero3RecurrentBrain();
        Zero3RecurrentBrain measured=new Zero3RecurrentBrain();
        float[] untouched=measured.parameterSnapshot();
        boolean rejectedFakeCredit=false;
        for(int i=0;i<40;i++){
            Zero3LearningFoundation.MotorIntent oldChoice=
                original.choose(sensed(i,0),35001L,i,0);
            Zero3LearningFoundation.MotorIntent physicalChoice=
                measured.choose(sensed(i,0),35001L,i,0);
            physicalIdentical(oldChoice,physicalChoice);
            float reward=(i%5==0?.43f:(i%5==1?-.19f:.08f));
            original.consequence(sensed(i,1),reward,false);
            measured.consequenceFixedClockV350(sensed(i,1),reward,false);
            float[] old=original.parameterSnapshot(),actual=measured.parameterSnapshot();
            check(old.length==actual.length&&actual.length==untouched.length,
                  "actor topology changed");
            for(int j=0;j<actual.length;j++){
                if(j>=start&&j<start+stride){
                    check(equal(actual[j],untouched[j]),
                          "fixed .30s gave fictitious gradient to duration weight "+j);
                    if(!equal(old[j],actual[j]))rejectedFakeCredit=true;
                }else check(equal(old[j],actual[j]),
                    "V350 changed executed actor/GRU/critic/world weights at "+j);
            }
        }
        check(rejectedFakeCredit,"old reward was not perturbing ignored duration");
        byte[] complete=measured.checkpointAtBoundaryV345();
        Zero3RecurrentBrain restored=new Zero3RecurrentBrain();
        restored.restoreAtBoundaryV345(complete);
        check(Arrays.equals(complete,restored.checkpointAtBoundaryV345()),
              "V345 checkpoint did not preserve V350 masked duration");
        for(int i=40;i<64;i++){
            float[] pre=sensed(i,0),post=sensed(i,1);
            Zero3LearningFoundation.MotorIntent a=measured.choose(pre,35001L,i,0);
            Zero3LearningFoundation.MotorIntent b=restored.choose(pre,35001L,i,0);
            physicalIdentical(a,b);
            check(equal(a.duration,b.duration),"duration RNG/hidden not restored");
            measured.consequenceFixedClockV350(post,-.13f,false);
            restored.consequenceFixedClockV350(post,-.13f,false);
            check(Arrays.equals(measured.checkpointAtBoundaryV345(),
                                restored.checkpointAtBoundaryV345()),
                  "V350 learned actor changed across cold GRU checkpoint");
        }
        System.out.println("V350 NO_FICTITIOUS_DURATION_CREDIT=PASS "+
            "64_neural_updates_without_fifth_actor_learning=true "+
            "same_physical_four_channel_actor_outputs=true "+
            "same_GruCriticWorld_except_fifth_actor=true "+
            "cold_restore_24_further_identical=true "+
            "VARIABLE_DURATION_NOT_IMPLEMENTED NO_G4_PROMOTION NO_OTA");
    }
}
