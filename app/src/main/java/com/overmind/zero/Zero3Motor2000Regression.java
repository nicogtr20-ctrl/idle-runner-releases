package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * ZERO3 G3 LONG-HORIZON test. 2,000 consecutive REAL decisions per motor
 * intention, 20 physical frames/decision = 40,000 physical steps. Neither
 * training checkpoints nor controlled evaluation inherit past world position.
 *
 * 400 decisions are only a screening point; 1,000 and 2,000 are mandatory.
 * Lab-intentions are researcher-selected ONLY to isolate cortex learning;
 * gameplay brain does not receive a gait or food coordinates.
 *
 * Metrics compare online training with a same-seed frozen-but-probing controller,
 * and a learned, probe-free policy against a virgin probe-free policy on fresh
 * simulated bodies, including a rotated pose not seen during training.
 *
 * Success of this program means integrity/reporting, NOT G3 motor competence.
 */
public final class Zero3Motor2000Regression {
    private static final int TOTAL=2000,FRAMES_PER_DECISION=20;
    private static final int[] CHECKPOINTS={400,1000,2000};
    private static final float DT=.025f, ROTATION=(float)(Math.PI/2);
    private static final float[][] INTENTS={{1,0},{0,1},{-1,0},{0,-1}};

    private static void check(boolean ok,String message){
        if(!ok)throw new AssertionError("ZERO3 LONG-2000 "+message);
    }
    private static final class Run {
        final MotorBrain.Body body=new MotorBrain.Body();
        final Morphology.Surface skin=new Morphology.Surface(body);
        final SymbioteMatterV1 matter=new SymbioteMatterV1();
        final Zero3LearningFoundation.AdaptiveCortex cortex;
        final Zero3LearningFoundation.Coordinator co;
        final boolean training;
        double signed,gross,work;
        int decisions,physical;
        Run(boolean training){
            this.training=training;
            cortex=new Zero3LearningFoundation.AdaptiveCortex();
            co=new Zero3LearningFoundation.Coordinator(cortex);
            matter.reset(body,skin);
        }
        void decision(float x,float y){
            final int id=decisions;
            final Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(109,id,cortex.revision(),
                    x,y,1f,.3f,.5f);
            // Frozen control has the same perturbation mechanism without updating.
            co.begin(intent,body,training,!training,true);
            for(int t=0;t<FRAMES_PER_DECISION;t++){
                float priorWork=matter.work;
                float[] muscle=co.actuate(body,skin,DT);
                matter.step(body,skin,muscle,DT);
                co.observe(body,DT,Math.max(0f,matter.work-priorWork),
                    matter.lastMuscleReactionImpulse,false,false);
                if(matter.invalid!=0||!Float.isFinite(body.cx)||!Float.isFinite(body.cy)||
                   MaterialGeometry.selfCrossings(skin)!=0)
                    throw new AssertionError("invalid gel at decision="+id+" frame="+t);
            }
            Zero3LearningFoundation.Experience e=co.finish();
            check(e.intent.decisionId==id&&e.frames==FRAMES_PER_DECISION,
                "broken causal decision/frame join");
            if(e.physicalEvidence)physical++;
            signed+=x*e.deltaX+y*e.deltaY;
            gross+=e.gross;
            work+=e.work;
            decisions++;
        }
    }
    private static final class Measurement {
        final Zero3MotorRetentionRegression.Measures base,trained,rotatedBase,rotatedTrained;
        Measurement(Zero3LearningFoundation.AdaptiveCortex network,float x,float y){
            Zero3LearningFoundation.AdaptiveCortex virgin=new Zero3LearningFoundation.AdaptiveCortex();
            float[] learnedBefore=network.weights();
            base=Zero3MotorRetentionRegression.session(virgin,x,y,160,false);
            trained=Zero3MotorRetentionRegression.session(network.forkForEvaluation(),x,y,160,false);
            rotatedBase=Zero3MotorRetentionRegression.session(
                new Zero3LearningFoundation.AdaptiveCortex(),x,y,160,false,ROTATION);
            rotatedTrained=Zero3MotorRetentionRegression.session(
                network.forkForEvaluation(),x,y,160,false,ROTATION);
            check(Arrays.equals(learnedBefore,network.weights()),
                "checkpoint validation modified training weights");
            check(virgin.updates()==0,"virgin controller acquired gradients");
        }
    }
    public static void main(String[] args){
        int validated=0;
        double totalBase=0,totalLearned=0,totalRotatedBase=0,totalRotatedLearned=0;
        for(float[] xy:INTENTS){
            Run learning=new Run(true),frozen=new Run(false);
            for(int decision=1;decision<=TOTAL;decision++){
                learning.decision(xy[0],xy[1]);
                frozen.decision(xy[0],xy[1]);
                if(decision==CHECKPOINTS[0]||decision==CHECKPOINTS[1]||
                    decision==CHECKPOINTS[2]){
                    Measurement m=new Measurement(learning.cortex,xy[0],xy[1]);
                    if(m.trained.forward>m.base.forward)validated++;
                    if(decision==TOTAL){
                        totalBase+=m.base.forward;
                        totalLearned+=m.trained.forward;
                        totalRotatedBase+=m.rotatedBase.forward;
                        totalRotatedLearned+=m.rotatedTrained.forward;
                    }
                    System.out.printf(Locale.US,
                        "ZERO3 LONG-2000 intent=[%.0f,%.0f] checkpoint=%d of %d"+
                        " ONLINE_TRAIN{forward=%.3f gross=%.3f work=%.4f updates=%d}"+
                        " MATCHED_FROZEN_PROBE{forward=%.3f gross=%.3f work=%.4f updates=%d}"+
                        " FRESH_ZERO_NOISE{virgin=%.3f learned=%.3f}"+
                        " ROTATED_90_ZERO_NOISE{virgin=%.3f learned=%.3f}%n",
                        xy[0],xy[1],decision,TOTAL,learning.signed,learning.gross,
                        learning.work,learning.cortex.updates(),frozen.signed,frozen.gross,
                        frozen.work,frozen.cortex.updates(),m.base.forward,m.trained.forward,
                        m.rotatedBase.forward,m.rotatedTrained.forward);
                }
            }
            check(learning.decisions==TOTAL&&frozen.decisions==TOTAL,
                "did not complete 2000 experiences");
            check(frozen.cortex.updates()==0,"frozen experiment modified weights");
            System.out.printf(Locale.US,
                "ZERO3 LONG-2000 DIRECTION COMPLETE [%.0f %.0f] realPhysicsFramesPerArm=%d"+
                " liveTrainMotorUpdates=%d validPhysicalDecisions=%d%n",
                xy[0],xy[1],TOTAL*FRAMES_PER_DECISION,learning.cortex.updates(),learning.physical);
        }
        System.out.printf(Locale.US,
            "ZERO3 LONG-2000 SUMMARY 4directions x 2000 consecutive experiences"+
            " checkPoints=400/1000/2000 zeroNoiseFreshBase=%.3f zeroNoiseFreshTrained=%.3f"+
            " rotatedBase=%.3f rotatedTrained=%.3f improvementsAcross12Checks=%d/12"+
            " (motor efficacy NOT implicitly approved)%n",
            totalBase,totalLearned,totalRotatedBase,totalRotatedLearned,validated);
    }
}
