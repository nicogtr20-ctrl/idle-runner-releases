package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/** ZERO3 G2/G3: deterministic integration regression using real 96+74 physics.
 * Tests contracts/learning integrity; not proof of useful autonomous walking. */
public final class Zero3FoundationRegression {
    private static void ok(boolean v,String why){
        if(!v)throw new AssertionError("ZERO3 FOUNDATION: "+why);
    }
    private static final class Result {
        Zero3LearningFoundation.Experience e;
        float[] before,after;
        long updates;
        float finalNet;
    }
    private static Result run(Zero3LearningFoundation.AdaptiveCortex cortex,
            long decision,boolean training,boolean corrected,boolean teleport){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(b,s);
        Zero3LearningFoundation.Coordinator c=new Zero3LearningFoundation.Coordinator(cortex);
        Zero3LearningFoundation.MotorIntent intent=new Zero3LearningFoundation.MotorIntent(
                1,decision,cortex.revision(),1,0,1,0,.5f);
        c.begin(intent,b,training,!training);
        float startX=b.cx,startY=b.cy;
        Result r=new Result();r.before=cortex.weights();
        for(int t=0;t<20;t++){
            float[] command=c.actuate(b,s,.025f);
            ok(command.length==64,"64 physical outputs");
            for(float f:command)ok(Float.isFinite(f)&&Math.abs(f)<=1,"finite bounded efference");
            float work=gel.work;
            gel.step(b,s,command,.025f);
            c.observe(b,.025f,Math.max(0,gel.work-work),
                gel.lastMuscleReactionImpulse,corrected,t==10&&teleport);
            ok(gel.invalid==0&&MaterialGeometry.selfCrossings(s)==0,
                "actual body topology invalid");
        }
        r.e=c.finish();r.after=cortex.weights();r.updates=cortex.updates();
        r.finalNet=(float)Math.hypot(b.cx-startX,b.cy-startY);
        ok(!c.pending(),"finished decision remained active");
        return r;
    }
    public static void main(String[] args){
        Zero3LearningFoundation.AdaptiveCortex c=new Zero3LearningFoundation.AdaptiveCortex();
        Result real=run(c,0,true,false,false);
        ok(real.e.frames==20&&real.e.physicalEvidence,"real muscle execution unobserved");
        ok(real.e.intent.decisionId==0&&!real.e.heldout&&!real.e.censored,
            "wrong physical intent credited");
        ok(c.updates()==1&&!Arrays.equals(real.before,real.after),
            "valid real outcome failed to change trainable motor synapses");
        long revision=c.revision();

        Result validation=run(c,1,false,false,false);
        ok(validation.e.heldout&&Arrays.equals(validation.before,validation.after),
            "holdout changed trained weights");
        ok(c.revision()==revision,"holdout changed motor revision");

        Result disturbed=run(c,2,true,true,false);
        ok(disturbed.e.externalCorrection&&Arrays.equals(disturbed.before,disturbed.after),
            "externally corrected motion polluted motor gradients");
        Result interrupted=run(c,3,true,false,true);
        ok(interrupted.e.censored&&Arrays.equals(interrupted.before,interrupted.after),
            "teleport credited as motor ability");

        MotorBrain.Body b=new MotorBrain.Body();
        Zero3LearningFoundation.Coordinator guard=new Zero3LearningFoundation.Coordinator(c);
        Zero3LearningFoundation.MotorIntent trial=new Zero3LearningFoundation.MotorIntent(
            1,7,c.revision(),0,1,.8f,.2f,.5f);
        guard.begin(trial,b,true,false);
        boolean refused=false;
        try{guard.begin(trial,b,true,false);}catch(IllegalStateException expected){refused=true;}
        ok(refused,"accepted double begin");
        guard.abort();ok(!guard.pending(),"abort left outstanding trial");
        refused=false;
        try{guard.begin(trial,b,true,false);}catch(IllegalArgumentException expected){refused=true;}
        ok(refused,"repeated decision ID not rejected");

        refused=false;
        try{new Zero3LearningFoundation.MotorIntent(1,8,c.revision(),1,0,1,0,0);}
        catch(IllegalArgumentException expected){refused=true;}
        ok(refused,"invalid decision duration");
        refused=false;
        try{new Zero3LearningFoundation.MotorIntent(1,8,c.revision(),.2f,.2f,1,0,.6f);}
        catch(IllegalArgumentException expected){refused=true;}
        ok(refused,"non-normalized intent accepted");
        System.out.printf(Locale.US,
            "ZERO3 G2/G3 PASS realPhysicsFrames=%d net=%.4f updateCount=%d revision=%d holdoutUnchanged=true externalUnchanged=true teleportUnchanged=true noDuplicateDecision=true%n",
            real.e.frames,real.finalNet,c.updates(),c.revision());
    }
}
