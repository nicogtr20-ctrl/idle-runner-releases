package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;

/** Real 96-node local perception + 96 optical probes and persistent learning. */
public final class DistributedBodyRegression {
    private static void must(boolean yes,String explanation){
        if(!yes)throw new AssertionError(explanation);
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        DistributedBodySense live=new DistributedBodySense();
        must(live.localMap().length==96*9,"Every physical patch must be addressable");
        must(live.choiceBias(0)==0,"Virgin high-resolution observer influenced motor choice");
        float[] clean=DistributedBodySense.sense(body,skin,null,objects);
        must(clean.length==DistributedBodySense.FEATURES,"96-node map truncated");
        skin.touchPulse[5]=.72f;
        skin.extension[5]=44f;
        skin.gripCommand[5]=1f;
        skin.gripHold[5]=.12f;
        skin.vx[5]=100;
        float[] active=DistributedBodySense.sense(body,skin,null,objects);
        live.feed(active);
        must(live.node(5,DistributedBodySense.TOUCH)>.7f,
            "Real touch was lost before visual/readout processing");
        must(live.node(5,DistributedBodySense.MOTOR)>.35f,
            "Real local membrane actuation was lost");
        must(live.node(5,DistributedBodySense.GRIP_DRIVE)>.6f&&
            live.node(5,DistributedBodySense.GRIP_HELD)>.2f,
            "Grip command and real hold must remain INDEPENDENT channels");
        must(live.node(4,DistributedBodySense.TOUCH)==0f,
            "One physical touch must not be invented on adjacent particles");
        float[] pooled=live.pooledMap();
        must(pooled[DistributedBodySense.TOUCH*16]>0,
            "Contact event must survive high-resolution -> sector aggregation");
        // Occlusion/nearest exterior solid: sensor origin is real local tissue.
        UnknownWorld.Entity obstacle=new UnknownWorld.Entity(170,0,2,5);
        objects.add(obstacle);
        float[] original=DistributedBodySense.sense(body,skin,null,objects);
        float far=original[0*9+DistributedBodySense.OPTIC];
        float centerX=body.cx,centerY=body.cy;
        skin.x[0]=body.cx+94f;skin.y[0]=body.cy;
        float[] extended=DistributedBodySense.sense(body,skin,null,objects);
        float near=extended[0*9+DistributedBodySense.OPTIC];
        must(near>far&&body.cx==centerX&&body.cy==centerY,
            "Visual probe did not follow protruding real tissue");
        objects.clear();
        float[] empty=DistributedBodySense.sense(body,skin,null,objects);
        must(empty[DistributedBodySense.OPTIC]==0,
            "A nonexistent physical obstacle emitted optical data");

        // Controlled predictor experiment only. Real app trains exclusively on
        // genuinely executed actions via SensorimotorMind.observe().
        DistributedBodySense learner=new DistributedBodySense();
        float[] before=new float[DistributedBodySense.FEATURES],
                after=new float[DistributedBodySense.FEATURES];
        for(int i=0;i<96;i++)after[i*9+DistributedBodySense.TOUCH]=.45f;
        for(int i=0;i<200;i++){
            learner.feed(before);learner.chosen(2);
            learner.consequence(after,2,.9f,0f,true,false);
        }
        for(int i=0;i<45;i++){
            learner.feed(before);learner.chosen(2);
            learner.consequence(after,2,.9f,0f,true,true);
        }
        must(learner.validations==45&&learner.confidence()>0,
            "Held-out predictive improvement could not be validated");
        must(learner.choiceBias(2)>0,
            "Validated real-body model cannot advise independent action");
        String saved=learner.encode();
        DistributedBodySense copy=new DistributedBodySense();
        must(copy.restore(saved)&&copy.validations==learner.validations,
            "Persistent high-resolution learning was lost");
        must(copy.choiceBias(2)==learner.choiceBias(2),
            "Restored model changed learned action advice");
        copy.resetLife();
        must(copy.confidence()>0,
            "Ordinary death must not erase lifetime-trained sensory weights");
        DistributedBodySense malformed=new DistributedBodySense();
        must(!malformed.restore(saved.substring(0,saved.length()/2)),
            "Partial save was accepted as valid");
        must(!malformed.restore(saved.replace("|","X")),
            "Corrupt sensory save was accepted");
        System.out.println("ZERO-0121 PASS: all 96 patches: nine local signals, "+
            "near-field visual rays, touch/grip separation, genuine protrusion, "+
            "holdout-gated learning, additive save, death continuity");
    }
}