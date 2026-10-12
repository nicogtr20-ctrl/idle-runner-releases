package com.overmind.zero;

import java.util.ArrayList;
import java.util.Random;

/** Controlled 0123 tests: sensed local geometry is real, learning is optional. */
public final class LocalImpedanceRegression {
    static void yes(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    public static void main(String[] args){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        float[] blank=Morphology.virgin(new Random(1));
        s.step(b,blank,80,0,MotorBrain.STEP);
        LocalImpedanceSense observer=new LocalImpedanceSense();
        float[] raw=LocalImpedanceSense.sense(b,s);
        yes(raw.length==960&&observer.localMap().length==960,
            "One or more 96-point proprioceptive channels missing");
        yes(observer.choiceBias(2)==0,"Virgin observer prescribed a motion");

        // Tissue point 5 remains against a physically touched obstacle while
        // points 20+ are free to move. This cannot be diagnosed from centroid.
        float oldX=s.x[5],oldY=s.y[5];
        float nx=s.normalX(5),ny=s.normalY(5);
        s.x[5]=oldX+nx*36;s.y[5]=oldY+ny*36;
        s.vx[5]=s.vy[5]=0;
        s.touchSense[5]=1;
        s.gripHold[5]=.4f;
        s.x[24]+=42f;s.vx[24]=80f;
        float[] strained=LocalImpedanceSense.sense(b,s);
        observer.feed(strained);
        yes(Math.abs(observer.node(5,LocalImpedanceSense.RADIAL_ERROR))>.2f,
            "Local motor/achieved-body mismatch disappeared");
        yes(observer.node(5,LocalImpedanceSense.HARD_CONTACT)>.9f&&
            observer.node(5,LocalImpedanceSense.HELD)>.7f,
            "A real local adhesion/touch was ignored");
        yes(observer.node(24,LocalImpedanceSense.HARD_CONTACT)==0,
            "Free moving tissue became falsely stuck");
        yes(observer.node(5,LocalImpedanceSense.STRETCH)>0&&
            observer.node(6,LocalImpedanceSense.STRETCH)>0,
            "Stretch transmission across connected real nodes lost");
        for(int i=0;i<3;i++)observer.feed(strained);
        yes(observer.persistentlyRestricted>=1,
            "Persistent local resistance undetected while body remains mobile");
        yes(observer.activeResistances<96,
            "Small trapped pseudopod mislabeled as full-body blockage");
        s.touchSense[5]=0;
        observer.feed(LocalImpedanceSense.sense(b,s));
        yes(observer.activeResistances==0,
            "No solid touch still produced a physically trapped point");
        // Environmental pressure does NOT imply a hard object collision.
        s.touchPulse[5]=.93f;
        s.touchSense[5]=0;
        observer.feed(LocalImpedanceSense.sense(b,s));
        yes(observer.node(5,LocalImpedanceSense.IMPACT)>.9f&&
            observer.node(5,LocalImpedanceSense.HARD_CONTACT)==0f&&
            observer.activeResistances==0,"Weather invented a stuck solid contact");

        // Explicit forward-model control: only actions actually executed may
        // train the model, on heldout data disjoint from updates.
        LocalImpedanceSense learning=new LocalImpedanceSense();
        float[] before=new float[LocalImpedanceSense.FEATURES];
        float[] after=new float[LocalImpedanceSense.FEATURES];
        for(int i=0;i<96;i++)after[i*10+LocalImpedanceSense.HARD_CONTACT]=.65f;
        for(int i=0;i<220;i++){
            learning.feed(before);learning.chosen(2);
            learning.consequence(after,2,.85f,0,true,false);
        }
        for(int i=0;i<40;i++){
            learning.feed(before);learning.chosen(2);
            learning.consequence(after,2,.85f,0,true,true);
        }
        yes(learning.validations==40&&learning.confidence()>0,
            "New local prediction didn't beat holdout constant-body baseline");
        yes(learning.choiceBias(2)>0&&learning.choiceBias(3)==0,
            "Untrained other action received a synthetic escape preference");
        String serial=learning.encode();
        LocalImpedanceSense saved=new LocalImpedanceSense();
        yes(saved.restore(serial)&&saved.validations==40&&
            saved.choiceBias(2)==learning.choiceBias(2),
            "Versioned learned local impedance memory not restored");
        saved.resetLife();
        yes(saved.confidence()>0&&saved.choiceBias(2)>0,
            "Ordinary death destroyed genuine learned local motor consequences");
        yes(!saved.restore(serial.substring(0,serial.length()/2))&&
            saved.confidence()>0,"Partial corrupt save altered a valid brain");
        LocalImpedanceSense virgin=new LocalImpedanceSense();
        yes(!virgin.restore("li1|x"),"Garbage accepted as a trained model");
        // No mutation to real body or motor coefficients from observation.
        yes(s.x[5]==oldX+nx*36&&s.y[5]==oldY+ny*36,
            "Read-only self-sensing altered the position of trapped tissue");
        yes(blank.length==Morphology.PARAMS,
            "96-node learning broke inherited motor coefficient dimensions");
        System.out.println("ZERO-0123 PASS: exact per-node intended/achieved discrepancy, "+
            "neighbor tension, independent free-vs-held movement, no weather "+
            "phantom collisions, delayed resistance, action-specific holdout "+
            "learning and lossless additive persistence");
    }
}