package com.overmind.zero;

import java.util.Locale;
import java.util.Arrays;

/**
 * G3 holdout: compares the EXACT same initial body and physical solver
 * between newborn controller and learned controller, both with ZERO probe.
 * Training experience from a separate body is never included in evaluation.
 * A negative comparison blocks the learning gate even if JVM checks are green.
 */
public final class Zero3MotorRetentionRegression {
    static final int TRAIN_DECISIONS=400, EVAL_DECISIONS=160;
    static final float DT=.025f;
    static final float[][] INTENTS={{1,0},{0,1},{-1,0},{0,-1}};
    static final class Measures {
        double forward,gross,work,net;
        long updates;
        public String toString(){return String.format(Locale.US,
            "forward=%.3f gross=%.3f net=%.3f work=%.4f updates=%d",
            forward,gross,net,work,updates);}
    }
    static void rotate(float[] xx,float[] yy,float cx,float cy,float cosine,float sine){
        for(int k=0;k<xx.length;k++){
            float x=xx[k]-cx,y=yy[k]-cy;
            xx[k]=cx+cosine*x-sine*y;
            yy[k]=cy+sine*x+cosine*y;
        }
    }
    static Measures session(Zero3LearningFoundation.AdaptiveCortex cortex,
        float dx,float dy,int decisions,boolean train){
        return session(cortex,dx,dy,decisions,train,0f);
    }
    static Measures session(Zero3LearningFoundation.AdaptiveCortex cortex,
        float dx,float dy,int decisions,boolean train,float rotateBodyRadians){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 matter=new SymbioteMatterV1();
        matter.reset(body,skin);
        if(rotateBodyRadians!=0){
            float cs=(float)Math.cos(rotateBodyRadians),sn=(float)Math.sin(rotateBodyRadians);
            rotate(skin.x,skin.y,body.cx,body.cy,cs,sn);
            rotate(body.x,body.y,body.cx,body.cy,cs,sn);
            rotate(matter.x,matter.y,body.cx,body.cy,cs,sn);
            matter.observeBody(body,skin);
        }
        Zero3LearningFoundation.Coordinator coordinator=new Zero3LearningFoundation.Coordinator(cortex);
        float startX=body.cx,startY=body.cy;
        Measures summary=new Measures();
        for(int d=0;d<decisions;d++){
            Zero3LearningFoundation.MotorIntent intent=new Zero3LearningFoundation.MotorIntent(
                71,d,cortex.revision(),dx,dy,1f,.3f,.5f);
            coordinator.begin(intent,body,train,!train,train);
            for(int f=0;f<20;f++){
                float effort=matter.work;
                float[] actual=coordinator.actuate(body,skin,DT);
                matter.step(body,skin,actual,DT);
                coordinator.observe(body,DT,Math.max(0f,matter.work-effort),
                    matter.lastMuscleReactionImpulse,false,false);
                if(matter.invalid!=0||MaterialGeometry.selfCrossings(skin)!=0)
                    throw new AssertionError("invalid material: direction "+dx+" "+dy);
            }
            Zero3LearningFoundation.Experience e=coordinator.finish();
            summary.forward+=dx*e.deltaX+dy*e.deltaY;
            summary.gross+=e.gross;
            summary.work+=e.work;
        }
        summary.net=Math.hypot(body.cx-startX,body.cy-startY);
        summary.updates=cortex.updates();
        return summary;
    }
    public static void main(String[] args){
        int improved=0;
        double totalLearn=0,totalVirgin=0;
        double[] virginForward=new double[4],learnedForward=new double[4];
        double[] virginCost=new double[4],learnedCost=new double[4];
        int index=0;
        for(float[] intent:INTENTS){
            Zero3LearningFoundation.AdaptiveCortex trained=
                new Zero3LearningFoundation.AdaptiveCortex();
            Measures during=session(trained,intent[0],intent[1],TRAIN_DECISIONS,true);
            Zero3LearningFoundation.AdaptiveCortex cold=trained.forkForEvaluation();
            Zero3LearningFoundation.AdaptiveCortex virgin=
                new Zero3LearningFoundation.AdaptiveCortex();
            float[] beforeHoldout=cold.weights();
            Measures scoreTrain=session(cold,intent[0],intent[1],EVAL_DECISIONS,false);
            Measures scoreVirgin=session(virgin,intent[0],intent[1],EVAL_DECISIONS,false);
            if(cold.updates()!=0||!Arrays.equals(beforeHoldout,cold.weights()))
                throw new AssertionError("holdout evaluation mutated weights");
            if(virgin.updates()!=0)throw new AssertionError("virgin control trained");
            if(scoreTrain.forward>scoreVirgin.forward)improved++;
            totalLearn+=scoreTrain.forward;totalVirgin+=scoreVirgin.forward;
            virginForward[index]=scoreVirgin.forward;learnedForward[index]=scoreTrain.forward;
            virginCost[index]=scoreVirgin.work;learnedCost[index]=scoreTrain.work;index++;
            System.out.printf(Locale.US,
                "ZERO3 G3 RETENTION dir=[%.0f,%.0f] train400{%s} virgin160{%s} learned160{%s}%n",
                intent[0],intent[1],during,scoreVirgin,scoreTrain);
        }
        Zero3MotorPromotionGate.Decision gate=Zero3MotorPromotionGate.evaluate(
            virginForward,learnedForward,virginCost,learnedCost);
        System.out.println("ZERO3 G3 PROMOTION="+(gate.promote?"CANDIDATE":"REJECTED")+
            " "+gate.reason+" (NO on-device promotion)");
        System.out.printf(Locale.US,
            "ZERO3 G3 RETENTION SUMMARY improvedDirections=%d/4 totalForwardVirgin=%.3f totalForwardLearned=%.3f; G3 efficacy requires stronger independent multi-seed evidence%n",
            improved,totalVirgin,totalLearn);
    }
}