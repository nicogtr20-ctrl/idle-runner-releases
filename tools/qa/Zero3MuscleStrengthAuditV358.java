package com.overmind.zero;

import java.util.Locale;

/**
 * READ-ONLY SCIENTIFIC QA: ORIGINAL SymbioteMatterV1 real force integration.
 * No app, live profiles, APK, rewards/gaits or physics implementation changed.
 * Compares virgin V2.2 motor readout vs virgin V3.0 readout under SAME body,
 * then 1,200 honest V3.0 physical choices in learned AND matched frozen arms with real coordinator learning.
 * It is a controlled stimulation fixture, NOT a test of autonomous navigation.
 */
public final class Zero3MuscleStrengthAuditV358 {
    private static final float DT=1f/60f;
    private static final int FRAMES=24,CHOICES=1200;
    private static final class Meter {
        double absExt,absLat,rms,impulse,work,displacement;
        double radialError;
        int frames,actions;
        int nNontrivial;
        void record(float[] cmd,SymbioteMatterV1 gel,MotorBrain.Body body,
                    Morphology.Surface skin,float beforeX,float beforeY,float beforeWork){
            for(int k=0;k<16;k++){
                absExt+=Math.abs(cmd[4*k]);
                absLat+=Math.abs(cmd[4*k+1]);
                rms+=cmd[4*k]*cmd[4*k]+cmd[4*k+1]*cmd[4*k+1];
                if(Math.abs(cmd[4*k])>.15f)nNontrivial++;
            }
            impulse+=gel.lastMuscleReactionImpulse;
            displacement+=Math.hypot(body.cx-beforeX,body.cy-beforeY);
            work+=Math.max(0,gel.work-beforeWork);
            for(int i=0;i<Morphology.POINTS;i++)
                radialError+=Math.abs(Math.hypot(skin.x[i]-body.cx,skin.y[i]-body.cy)-28f);
            frames++;
        }
        String summary(){
            double denom=Math.max(1,frames*16);
            return String.format(Locale.ROOT,
                "n=%d avg|extend|=%.6f avg|lateral|=%.6f rmsMuscle=%.6f fraction|ext|>0.15=%.4f realImpulse/frame=%.4f realWork/frame=%.8f travelledU/frame=%.6f radialError=%.5f",
                frames,absExt/denom,absLat/denom,
                Math.sqrt(rms/(denom*2)),nNontrivial/denom,
                impulse/Math.max(1,frames),work/Math.max(1,frames),
                displacement/Math.max(1,frames),
                radialError/Math.max(1,frames*Morphology.POINTS));
        }
    }
    static class World {
        final MotorBrain.Body body=new MotorBrain.Body();
        final Morphology.Surface skin=new Morphology.Surface(body);
        final SymbioteMatterV1 gel=new SymbioteMatterV1();
        World(){gel.reset(body,skin);}
    }
    private static void oldVirgin(){
        World w=new World();
        SymbioteMotorHeadV1 v22=new SymbioteMotorHeadV1();
        float[] distributed=new float[DistributedBodySense.FEATURES];
        float[] recurrent=new float[RecurrentCoreV22.Z];
        v22.chosen(0,distributed,recurrent,.5f,null,
            SymbioteMotorHeadV1.measuredSectorAngles(w.body,w.skin),w.body,w.skin);
        Meter m=new Meter();
        for(int j=0;j<FRAMES;j++){
            v22.fastTick(w.body,w.skin,DT);
            float bx=w.body.cx,by=w.body.cy,work=w.gel.work;
            w.gel.step(w.body,w.skin,v22.commands(),DT);
            m.record(v22.commands(),w.gel,w.body,w.skin,bx,by,work);
        }
        System.out.println("V358_OLD_22_VIRGIN_FIRST_DECISION "+m.summary());
    }
    private static void newVirginAndLearn(boolean learning,float gain){
        World w=new World();
        Zero3LearningFoundation.AdaptiveCortex cortex=new Zero3LearningFoundation.AdaptiveCortex(.013f,gain);
        Zero3LearningFoundation.Coordinator owner=new Zero3LearningFoundation.Coordinator(cortex);
        Meter first=new Meter(),early=new Meter(),late=new Meter(),all=new Meter();
        long physical=0,valid=0;double scores=0,sqScore=0,physicalImp=0;
        long beforeUpdates=0;
        for(int d=0;d<CHOICES;d++){
            boolean heldout=!learning||d%5==4;
            // Purely diagnostic intent fixture, direction fixed to separate
            // raw muscle learning from whether the GRU chooses a good target.
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    1,d,cortex.revision(),1f,0f,.5f,.5f,FRAMES*DT);
            owner.begin(intent,w.body,!heldout,heldout,true);
            float[] cmd=null;
            for(int j=0;j<FRAMES;j++){
                cmd=owner.actuate(w.body,w.skin,DT);
                float bx=w.body.cx,by=w.body.cy,priorWork=w.gel.work;
                w.gel.step(w.body,w.skin,cmd,DT);
                owner.observe(w.body,DT,Math.max(0,w.gel.work-priorWork),
                    w.gel.lastMuscleReactionImpulse,false,false);
                all.record(cmd,w.gel,w.body,w.skin,bx,by,priorWork);
                if(d==0)first.record(cmd,w.gel,w.body,w.skin,bx,by,priorWork);
                if(d<25)early.record(cmd,w.gel,w.body,w.skin,bx,by,priorWork);
                if(d>=CHOICES-25)late.record(cmd,w.gel,w.body,w.skin,bx,by,priorWork);
            }
            Zero3LearningFoundation.Experience e=owner.finish();
            scores+=e.score;sqScore+=e.score*e.score;physicalImp+=e.activeImpulse;
            if(e.physicalEvidence)physical++;
            if(e.train&&e.physicalEvidence&&!e.externalCorrection)valid++;
            if(d==24)beforeUpdates=cortex.updates();
        }
        String mode=(gain>1.1f?"GAIN24_":"GAIN10_")+(learning?"LEARNING":"FROZEN_SAME_PHYSICS");
        System.out.println("V358_"+mode+"_VIRGIN_FIRST_DECISION "+first.summary());
        System.out.println("V358_"+mode+"_EARLY_25_DECISIONS "+early.summary());
        System.out.println("V358_"+mode+"_LATE_25_DECISIONS "+late.summary());
        System.out.println("V358_"+mode+"_ALL "+all.summary());
        System.out.printf(Locale.ROOT,
            "V358_"+(gain>1.1f?"GAIN24_":"GAIN10_")+(learning?"LEARNING":"FROZEN")+"_GATE physicalEvidence=%d/%d eligibility=%d/%d cortexSynapseUpdatesEarly=%d cortexSynapseUpdatesTotal=%d trainedDecisions=%d discarded=%d averageScore=%+.7f rmsScore=%.7f averageImpulsePerDecision=%.4f%n",
            physical,CHOICES,valid,CHOICES,beforeUpdates,cortex.updates(),
            owner.trained(),owner.discarded(),scores/CHOICES,
            Math.sqrt(sqScore/CHOICES),physicalImp/CHOICES);
        if(physical==0)throw new AssertionError("V3 cortex cannot reach physical impulse threshold");
        if(learning&&cortex.updates()==0)
            throw new AssertionError("V3 cortex receives no useful training updates under controlled real muscle stimulation");
        if(!learning&&cortex.updates()!=0)
            throw new AssertionError("Frozen neural baseline illegally changed motor weights");
    }
    public static void main(String[] args){
        oldVirgin();
        // Same seed, same real body, same fixed intention; only gain differs.
        // Compare the two READ-ONLY gates with and without cortex updates.
        newVirginAndLearn(true,1f);
        newVirginAndLearn(false,1f);
        newVirginAndLearn(true,Zero3LearningFoundation.AdaptiveCortex.DEFAULT_ACTIVATION_GAIN_V358);
        newVirginAndLearn(false,Zero3LearningFoundation.AdaptiveCortex.DEFAULT_ACTIVATION_GAIN_V358);
        System.out.println("V358_MOTOR_GAIN_MATCHED_A_B_MEASURED=PASS (production gain only, actual physics unchanged)");
    }
}
