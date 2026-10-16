package com.overmind.zero;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Locale;

/** Experimental ZERO3 G4 screening: true GRU/actor + cortex + body.
 * Synthetically controlled sensor task tests gradients ONLY. Physical loop
 * uses actual measured physics but does NOT certify autonomous locomotion. */
public final class Zero3RecurrentBrainRegression {
    static void ok(boolean v,String msg){if(!v)throw new AssertionError("ZERO3 G4 "+msg);}
    static float[] senses(float input){
        float[] x=new float[SensorimotorMind.S];
        for(int i=0;i<x.length;i++)x[i]=(i%5==0?-input:input*.5f);
        return x;
    }
    public static void main(String[] args){
        Zero3RecurrentBrain b=new Zero3RecurrentBrain();
        ok(b.realRecurrentUnits()==64,"faked neuron count");
        float[] initial=b.parameterSnapshot();
        float[] old=senses(.4f),now=senses(.65f);
        Zero3LearningFoundation.MotorIntent i=b.choose(old,1,0,0);
        ok(Float.isFinite(i.x)&&Float.isFinite(i.y),"invalid actor intent");
        boolean refused=false;try{b.choose(old,1,1,0);}
        catch(IllegalStateException expected){refused=true;}
        ok(refused,"allowed second choice without consequence");
        b.consequence(now,.5f,false);
        float[] after=b.parameterSnapshot();
        ok(!Arrays.equals(initial,after),"GRU/actor/critic/model did not train");
        ok(b.trainingUpdates()==1,"training counter");
        for(int j=1;j<6;j++){
            i=b.choose(senses(.05f*j),1,j,0);
            b.consequence(senses(.1f+j*.08f),.1f,false);
        }
        ok(b.recurrentUpdates()>0,"GRU recurrent weights do not adapt");
        float[] holdBefore=b.parameterSnapshot();
        i=b.choose(now,1,7,0);
        b.consequence(old,.7f,true);
        ok(Arrays.equals(holdBefore,b.parameterSnapshot()),
            "heldout leaked into learning");
        ok(b.validations()==1&&Float.isFinite(b.heldoutModelError())
            &&Float.isFinite(b.heldoutBaselineError()),"invalid independent prediction stats");

        // Now use the autonomous actor's real intention to drive the REAL
        // cortex and 96+74 material. No hand-authored movements or gems.
        Zero3RecurrentBrain policy=new Zero3RecurrentBrain();
        Zero3LearningFoundation.AdaptiveCortex motor=new Zero3LearningFoundation.AdaptiveCortex();
        Zero3LearningFoundation.Coordinator coordinator=new Zero3LearningFoundation.Coordinator(motor);
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(body,skin);
        float x0=body.cx,y0=body.cy,totalGross=0;
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        for(int decision=0;decision<8;decision++){
            float[] observation=SensorimotorMind.Senses.read(body,skin,null,world,70f);
            Zero3LearningFoundation.MotorIntent intent=policy.choose(
                observation,1,decision,motor.revision());
            coordinator.begin(intent,body,true,false);
            // This is a G4 integration screening using real dynamics, not a
            // claim of 8 choices learning to walk.
            int frames=Math.max(4,(int)(intent.duration/.025f));
            for(int frame=0;frame<frames;frame++){
                float[] cmd=coordinator.actuate(body,skin,.025f);
                float prevWork=gel.work;
                gel.step(body,skin,cmd,.025f);
                coordinator.observe(body,.025f,Math.max(0,gel.work-prevWork),
                    gel.lastMuscleReactionImpulse,false,false);
                ok(gel.invalid==0&&MaterialGeometry.selfCrossings(skin)==0,
                    "corrupted physical gel");
            }
            Zero3LearningFoundation.Experience e=coordinator.finish();
            ok(e.intent.decisionId==decision&&!e.heldout&&e.frames==frames,
                "brain/physical outcome join incorrect");
            totalGross+=e.gross;
            float[] next=SensorimotorMind.Senses.read(body,skin,null,world,70f);
            // Actual motor energy expense (no fabricated food reward).
            policy.consequence(next,-.005f*e.work,false);
        }
        ok(policy.trainingUpdates()==8&&motor.updates()>0,
            "actual closed-loop V3 train did not execute");
        ok(totalGross>0f,"new cortex never produced any real material motion");
        System.out.printf(Locale.US,
            "ZERO3 G4 EXPERIMENT PASS GRU=64 train=%d recurrentUpdates=%d holdout=1 motorUpdates=%d realGross=%.5f realNet=%.5f; independent success of locomotion NOT TESTED%n",
            policy.trainingUpdates(),b.recurrentUpdates(),motor.updates(),totalGross,
            (float)Math.hypot(body.cx-x0,body.cy-y0));
    }
}
