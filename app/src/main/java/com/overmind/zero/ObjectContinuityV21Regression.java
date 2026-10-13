package com.overmind.zero;

import java.util.Arrays;

/** No Android dependency, synthetic OBSERVABLE ray sequences only. */
public final class ObjectContinuityV21Regression {
    private static void yes(boolean ok,String msg){if(!ok)throw new AssertionError(msg);}
    private static float[] east(float r){
        float[] rays=new float[16];Arrays.fill(rays,1f);rays[0]=r;return rays;
    }
    private static float[] west(float r){
        float[] rays=new float[16];Arrays.fill(rays,1f);rays[8]=r;return rays;
    }
    public static void main(String[] args){
        ObjectContinuityV21 t=new ObjectContinuityV21();
        yes(t.currentAnonymousTrack()==-1&&t.confidence()==0,"virgin model");
        float[] a=east(4f/9);
        // Body moves by one ray step; apparent range changes. Same first hit
        // should remain consistent in the odometry frame, not be a NEW object.
        t.view(a,0,0,.16f,true);
        int stable=-1;
        for(int i=0;i<7;i++){
            t.view(east(3f/9),22,0,.20f,true);
            stable=t.currentAnonymousTrack();
        }
        yes(stable>0&&t.currentTrackConfidence()>.40f,
            "visible repeated obstacle not tracked across translation");
        yes(t.visibleTracks()==1,"egomotion created phantom duplicate objects");
        t.chosen(8);
        // Modest 44U observed shift within conservative correspondence gate;
        // larger 66U leaps must ABSTAIN rather than guess identity.
        // Visible same-ray obstacle displacement, with external-physics
        // evidence. Still only a POTENTIAL action/object association.
        t.view(east(5f/9),22,0,.20f,true);
        t.consequence(1.6f,true,false);
        yes(t.candidateEffects()>0,"physically witnessed candidate lost");
        t.energyContact(30f);
        yes(t.delayedEnergyAssociations()>0,
            "later real contact association not counted");
        // Opposite scene is not automatically the previously seen east track.
        t.view(west(4f/9),22,0,.4f,true);
        yes(t.visibleTracks()>=2,"distinct visible obstacle fused with eastern hit");
        // Regression from real-cover arena: multiple fresh clusters discovered
        // in the SAME ray sweep must never index the prior track count.
        ObjectContinuityV21 crowded=new ObjectContinuityV21();
        float[] both=east(4f/9);both[8]=4f/9;
        crowded.view(both,0,0,.3f,true);
        yes(crowded.visibleTracks()==2,"two new first-return clusters lost or crashed");
        String preserved=t.encode();
        ObjectContinuityV21 copy=new ObjectContinuityV21();
        yes(copy.restore(preserved)&&preserved.equals(copy.encode()),"snapshot mismatch");
        yes(copy.visibleTracks()==0&&copy.currentAnonymousTrack()==-1,
            "phantom physical object identity serialized into another life");
        for(String bad:Arrays.asList("oc21|broken",preserved+"|7",
                preserved.replaceFirst("oc21","nope"),
                preserved.replaceFirst("\\|1\\|","|NaN|"),
                preserved.substring(0,preserved.length()-3))){
            yes(!copy.restore(bad),"malformed snapshot accepted");
            yes(preserved.equals(copy.encode()),"corrupt restore mutated knowledge");
        }
        // No holdout should modify training counts or invent motion samples.
        long before=copy.candidateEffects();
        for(int i=0;i<90;i++){
            copy.view(east(4f/9),0,0,.3f,false);
            copy.chosen(8);
            copy.view(east(4f/9),0,0,.3f,false);
            copy.consequence(0,false,true);
        }
        yes(copy.candidateEffects()==before,"heldout counted new training success");
        yes(copy.confidence()==0,"unevidenced contact accidentally licensed policy");
        copy.resetLife();
        yes(copy.visibleTracks()==0&&copy.candidateEffects()==before,
            "death erased knowledge or preserved a phantom object");
        float[] broken=east(.5f);broken[3]=Float.NaN;
        copy.view(broken,0,0,.2f,true);
        yes(copy.invalidSamples()>0&&copy.currentAnonymousTrack()==-1,
            "nonfinite rays accepted");
        System.out.println("ZERO21 multi-view: egomotion, distinct observed objects, actual effect, delayed energy hypothesis, holdout, corrupt save, death PASS");
    }
}