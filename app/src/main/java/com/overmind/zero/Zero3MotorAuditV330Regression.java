package com.overmind.zero;

import java.util.Locale;

/**
 * E3 SCOPING, not G3 final skill promotion:
 * 4 world families x 5 independently rotated/seeded episodes, 96 genuine
 * decisions/arm. Paired TRAIN Cortex(.013) vs FROZEN Cortex(0.0), SAME
 * V317 actor recurrent learning, SAME V319 contextual adhesion,
 * SAME brain init, SAME exploratory perturbation schedule, SAME world.
 *
 * No prescribed heading, no simulated trajectory label, no artificial
 * movement reward. Negative outcomes remain in the full log.
 */
public final class Zero3MotorAuditV330Regression {
    private static final int FAMILIES=4,PER=5,DECISIONS=96;
    private static final String[] NAMES={"FREE","BOX_NEAR","DISK","BOX_FAR"};
    static final class Measures{
        int decisions,physical,chosen,motorUpdates,gripUpdates;
        int held,bilateral,wrongWay,valid;
        double signed,gross,work,netX,netY,inertiaProxy,residual,
               cancellation,bodyEffort,impulse,alignment;
        double energyStart,energyEnd;
        void add(Zero3MotorEvidenceV330.Measure e){
            if(e==null||e.executedSubsteps!=12||!Double.isFinite(e.work)||
                !Double.isFinite(e.signedAlongIntent)||e.gross<0||
                e.work<0||e.cancellationProxy<-.001||e.cancellationProxy>1.001)
                throw new AssertionError("V330 invalid real motor evidence");
            decisions++;if(e.physical)physical++;
            if(e.selectedMovement)chosen++;
            if(e.signedAlongIntent<0&&e.selectedMovement)wrongWay++;
            signed+=e.signedAlongIntent;gross+=e.gross;work+=e.work;
            netX+=e.dx;netY+=e.dy;
            inertiaProxy+=e.inertiaProxyAlongIntent;
            residual+=e.residualBeyondInertiaProxy;
            cancellation+=e.cancellationProxy;
            bodyEffort+=e.meanMotorMagnitude;
            impulse+=e.impulse;
            held+=e.gripFrames;bilateral+=e.bilateralFrames;
            if(!e.censored&&e.physical)valid++;
            alignment+=e.selectedMovement?
                e.signedAlongIntent/Math.max(e.gross,1e-5f):0;
        }
        void merge(Measures r){
            decisions+=r.decisions;physical+=r.physical;chosen+=r.chosen;
            motorUpdates+=r.motorUpdates;gripUpdates+=r.gripUpdates;
            held+=r.held;bilateral+=r.bilateral;wrongWay+=r.wrongWay;valid+=r.valid;
            signed+=r.signed;gross+=r.gross;work+=r.work;
            netX+=r.netX;netY+=r.netY;inertiaProxy+=r.inertiaProxy;
            residual+=r.residual;cancellation+=r.cancellation;
            bodyEffort+=r.bodyEffort;impulse+=r.impulse;alignment+=r.alignment;
            energyStart+=r.energyStart;energyEnd+=r.energyEnd;
        }
        double progressEfficiency(){return signed/(4+gross);}
        double workPerUseful(){return work/(1+Math.max(0,signed));}
        double signedPerDecision(){return signed/Math.max(1,chosen);}
        double cancellationMean(){return cancellation/Math.max(1,decisions);}
    }
    private static Measures one(int family,int ordinal,boolean plastic)throws Exception{
        long episode=3300000L+family*1000L+ordinal;
        long rng=0x33020261017L+family*131L+ordinal*7703L;
        try(Zero3RuntimeV325 r=Zero3MotorLabBirthV330.newborn(
                    episode,rng,family,ordinal,plastic)){
            Measures a=new Measures();
            a.energyStart=r.energy();
            for(int d=0;d<DECISIONS;d++){
                if(r.ended())break; // TERMINAL CENSOR: no fake continuation
                r.advanceOneDecision();
                Zero3MotorEvidenceV330.Measure e=r.lastMotorEvidence();
                if(e.episode!=episode||e.decision!=d)
                    throw new AssertionError("V330 wrong actor/owner decision");
                a.add(e);
            }
            a.motorUpdates=(int)r.cortexUpdates();
            a.gripUpdates=(int)r.gripUpdates();
            a.energyEnd=r.energy();
            if(a.decisions!=DECISIONS)
                throw new AssertionError("V330 organism died: record actual censor without relabeling PASS "+
                                         NAMES[family]+ " ordinal="+ordinal+" steps="+a.decisions);
            return a;
        }
    }
    private static void pair(int family,int ordinal,Measures learned,Measures frozen)
              throws Exception{
        Measures l=one(family,ordinal,true);
        Measures f=one(family,ordinal,false);
        if(l.decisions!=f.decisions||l.physical!=f.physical||
           l.motorUpdates==0||f.motorUpdates!=0||
           l.gripUpdates!=f.gripUpdates)
            throw new AssertionError("V330 paired online cortex update control invalid");
        learned.merge(l);frozen.merge(f);
        System.out.printf(Locale.US,
            "V330 MATCHED_SCENE=%s ordinal=%d decisions=%d"+
            " trainSigned=%.6f frozenSigned=%.6f trainGross=%.6f frozenGross=%.6f"+
            " trainWork=%.8f frozenWork=%.8f trainCarry=%.6f frozenCarry=%.6f"+
            " trainResidual=%.6f frozenResidual=%.6f trainCancel=%.5f frozenCancel=%.5f"+
            " trainHeld=%d frozenHeld=%d trainBilateral=%d frozenBilateral=%d"+
            " trainUpdates=%d frozenUpdates=%d%n",
            NAMES[family],ordinal,l.decisions,l.signed,f.signed,
            l.gross,f.gross,l.work,f.work,l.inertiaProxy,f.inertiaProxy,
            l.residual,f.residual,l.cancellationMean(),f.cancellationMean(),
            l.held,f.held,l.bilateral,f.bilateral,l.motorUpdates,f.motorUpdates);
    }
    public static void main(String[] args)throws Exception{
        Measures totalLearn=new Measures(),totalFrozen=new Measures();
        int sceneWins=0,validScenes=0;
        for(int family=0;family<FAMILIES;family++){
            Measures learned=new Measures(),frozen=new Measures();
            for(int ordinal=0;ordinal<PER;ordinal++)pair(family,ordinal,learned,frozen);
            totalLearn.merge(learned);totalFrozen.merge(frozen);
            double d=learned.progressEfficiency()-frozen.progressEfficiency();
            double floor=Math.max(.05,.10*Math.abs(frozen.progressEfficiency()));
            boolean won=d>=floor&&
                learned.workPerUseful()<=frozen.workPerUseful()*1.2;
            if(won)sceneWins++;
            if(learned.valid>=DECISIONS*PER*.8&&
               frozen.valid>=DECISIONS*PER*.8)validScenes++;
            System.out.printf(Locale.US,
               "V330 FAMILY=%s measuredRealDecisions=%d"+
               " trainSigned=%.8f frozenSigned=%.8f"+
               " trainNet=%.6f frozenNet=%.6f"+
               " trainEfficiency=%.8f frozenEfficiency=%.8f"+
               " trainCostPerUseful=%.8f frozenCostPerUseful=%.8f"+
               " trainWrongWay=%d frozenWrongWay=%d"+
               " trainCanceled=%.5f frozenCanceled=%.5f"+
               " trainPhysical=%d frozenPhysical=%d"+
               " TRAINED_BETTER=%s%n",
               NAMES[family],learned.decisions,
               learned.signed,frozen.signed,
               Math.hypot(learned.netX,learned.netY),
               Math.hypot(frozen.netX,frozen.netY),
               learned.progressEfficiency(),frozen.progressEfficiency(),
               learned.workPerUseful(),frozen.workPerUseful(),
               learned.wrongWay,frozen.wrongWay,
               learned.cancellationMean(),frozen.cancellationMean(),
               learned.physical,frozen.physical,won?"YES":"NO");
        }
        boolean prereg=sceneWins>=3&&validScenes==4;
        System.out.printf(Locale.US,
            "V330 G3_DIAGNOSTIC_LAB realDecisionsPerArm=%d"+
            " matchedIndependentWorlds=%d physicalTrain=%d physicalFrozen=%d"+
            " cortexUpdatesTrain=%d cortexUpdatesFrozen=%d"+
            " trainedSceneWins=%d/%d validSceneFamilies=%d"+
            " overallTrainSigned=%.6f overallFrozenSigned=%.6f"+
            " MOTOR_GATE=%s NO_GAIT_SCRIPT NO_OTA%n",
            totalLearn.decisions,FAMILIES*PER,totalLearn.physical,totalFrozen.physical,
            totalLearn.motorUpdates,totalFrozen.motorUpdates,sceneWins,FAMILIES,
            validScenes,totalLearn.signed,totalFrozen.signed,
            prereg?"PRELIMINARY_PASS_NOT_RETENTION":"FAIL");
        if(totalLearn.decisions!=DECISIONS*FAMILIES*PER||
           totalFrozen.decisions!=DECISIONS*FAMILIES*PER||
           totalLearn.motorUpdates<1||totalFrozen.motorUpdates!=0||
           totalLearn.gripUpdates!=totalFrozen.gripUpdates)
            throw new AssertionError("V330 motor physical paired test incomplete");
        // Technical PASS only, independently of learned-performance gate.
        System.out.println("V330 REAL_ACTOR_REAL_PHYSICS_MOTOR_FORENSICS=PASS "+
            "20_SCENE_PAIRED_EVIDENCE=PASS "+
            "SCIENTIFIC_G3_RETAINS_PREEXISTING_STATUS=BLOCKED");
    }
}
