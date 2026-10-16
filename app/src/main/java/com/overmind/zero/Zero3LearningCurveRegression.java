package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO3 G3/G4 exploratory 400-decision motor curve, SAME virgin code and real
 * solver, TRAIN vs FROZEN. Screening only: no survival/food/obstacle rewards,
 * no claim that novel cortex learns useful locomotion.
 * Must never run inside normal phone gameplay or mutate saved profiles.
 */
public final class Zero3LearningCurveRegression {
    static final class Sample {
        double gross,agreement,work,net;
        int decisions,valid,update;
        float fromX,fromY;
        @Override public String toString(){
            return String.format(Locale.US,"decisions=%d gross=%.3f net=%.3f alignmentSum=%.3f work=%.3f valid=%d cortexUpdates=%d",
                decisions,gross,net,agreement,work,valid,update);
        }
    }
    static Sample run(int steps,boolean learn){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(body,skin);
        Zero3RecurrentBrain brain=new Zero3RecurrentBrain();
        Zero3LearningFoundation.AdaptiveCortex cortex=new Zero3LearningFoundation.AdaptiveCortex();
        Zero3LearningFoundation.Coordinator co=new Zero3LearningFoundation.Coordinator(cortex);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        Sample sample=new Sample();sample.fromX=body.cx;sample.fromY=body.cy;
        for(int d=0;d<steps;d++){
            float[] before=SensorimotorMind.Senses.read(body,skin,null,objects,70);
            Zero3LearningFoundation.MotorIntent intent=brain.choose(before,1,d,cortex.revision());
            co.begin(intent,body,learn,!learn);
            int frames=Math.max(4,(int)(intent.duration/.025f));
            for(int f=0;f<frames;f++){
                float work=gel.work;
                float[] muscles=co.actuate(body,skin,.025f);
                gel.step(body,skin,muscles,.025f);
                co.observe(body,.025f,Math.max(0f,gel.work-work),
                    gel.lastMuscleReactionImpulse,false,false);
                if(gel.invalid!=0||!Float.isFinite(body.cx)||!Float.isFinite(body.cy))
                    throw new AssertionError("physical instability at decision "+d);
            }
            Zero3LearningFoundation.Experience e=co.finish();
            sample.gross+=e.gross;
            sample.agreement+=e.deltaX*intent.x+e.deltaY*intent.y;
            sample.work+=e.work;
            if(e.physicalEvidence)sample.valid++;
            float[] next=SensorimotorMind.Senses.read(body,skin,null,objects,70);
            brain.consequence(next,-.005f*e.work,!learn);
            sample.decisions++;
        }
        sample.net=Math.hypot(body.cx-sample.fromX,body.cy-sample.fromY);
        sample.update=(int)cortex.updates();
        if(learn&&sample.update<steps/2)
            throw new AssertionError("motor learning stall: updates="+sample.update);
        if(!learn&&sample.update!=0)
            throw new AssertionError("frozen reference trained");
        return sample;
    }
    public static void main(String[] args){
        for(int count:new int[]{40,400,2000}){
            Sample frozen=run(count,false),learned=run(count,true);
            System.out.println("ZERO3 AUTONOMOUS LEARNING CURVE SCREENING steps="+count+
                " FROZEN{"+frozen+"} TRAIN{"+learned+"}");
        }
        // No assertion that TRAIN must outperform FROZEN: doing so would
        // disguise a performance regression as "an automated green test".
        System.out.println("ZERO3 G3 INTEGRITY PASS; motor-learning QUALITY is an empirical gate, not automatically passed");
    }
}
