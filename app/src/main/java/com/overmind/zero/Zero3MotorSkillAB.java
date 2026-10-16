package com.overmind.zero;

import java.util.Locale;

/**
 * Laboratory ONLY: holds an arbitrary researcher-chosen high-level intention
 * constant to isolate cortex controllability. This choice never exists inside
 * the autonomous actor or gameplay. The cortex must discover motor actuation.
 */
public final class Zero3MotorSkillAB {
    static final class Data {
        double forward,net,gross,work;
        int updates;long endRevision;
    }
    static Data run(int choices,float x,float y,boolean train){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(b,s);
        Zero3LearningFoundation.AdaptiveCortex cortex=new Zero3LearningFoundation.AdaptiveCortex();
        Zero3LearningFoundation.Coordinator co=new Zero3LearningFoundation.Coordinator(cortex);
        Data out=new Data();float sx=b.cx,sy=b.cy;
        for(int d=0;d<choices;d++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(1,d,cortex.revision(),x,y,1f,.3f,.5f);
            co.begin(intent,b,train,!train);
            for(int f=0;f<20;f++){
                float effortBefore=gel.work;
                gel.step(b,s,co.actuate(b,s,.025f),.025f);
                co.observe(b,.025f,Math.max(0,gel.work-effortBefore),gel.lastMuscleReactionImpulse,false,false);
                if(gel.invalid!=0||MaterialGeometry.selfCrossings(s)!=0)
                    throw new AssertionError("physically invalid body");
            }
            Zero3LearningFoundation.Experience e=co.finish();
            out.gross+=e.gross;out.forward+=x*e.deltaX+y*e.deltaY;
            out.work+=e.work;
        }
        out.net=Math.hypot(b.cx-sx,b.cy-sy);
        out.updates=(int)cortex.updates();out.endRevision=cortex.revision();
        return out;
    }
    public static void main(String[] args){
        int steps=400;float[][] headings={{1,0},{0,1},{-1,0},{0,-1}};
        for(float[] dir:headings){
            Data off=run(steps,dir[0],dir[1],false),on=run(steps,dir[0],dir[1],true);
            System.out.printf(Locale.US,
                "ZERO3 MOTOR LAB intent=[%.0f %.0f] decisions=%d FROZEN{forward=%.3f net=%.3f gross=%.3f work=%.3f} TRAIN{forward=%.3f net=%.3f gross=%.3f work=%.3f updates=%d}%n",
                dir[0],dir[1],steps,off.forward,off.net,off.gross,off.work,
                on.forward,on.net,on.gross,on.work,on.updates);
        }
        // No assertion of victory: all four directions are preregistered and
        // displayed to reveal regressions instead of hiding adverse cases.
    }
}
