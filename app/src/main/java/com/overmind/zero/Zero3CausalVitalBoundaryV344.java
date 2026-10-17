package com.overmind.zero;

/**
 * V344 actual-run-time boundary for physical vital observations.
 *
 * No setter, no fabricated or inferred resource location, and no value
 * shaping. PRE strictly precedes the decision; POST is constructed only
 * after the real V323 metabolism has committed 12 physical solver frames.
 * Does not alter the V315/V317/V328 persistent data schema.
 */
final class Zero3CausalVitalBoundaryV344 {
    private static void need(boolean valid,String detail){
        if(!valid)throw new IllegalStateException("ZERO3 V344 vital boundary "+detail);
    }
    private Zero3CausalVitalBoundaryV344(){}
    static final class Before{
        final long episode,sensoryHash,firstPhysicalTick;
        final int decision;
        final float reserve,previousDamage,fuelRatio;
        final float earnedEver,basalEver,motorEver,harmedEver;
        final int positiveEver,negativeEver,sourcesEver;
        private Before(Zero3SensoryFrameV315 pre,Zero3SurvivalLedgerV323 v,
                       int decision){
            this.episode=pre.episodeId;
            this.decision=decision;
            this.sensoryHash=pre.hash;
            this.firstPhysicalTick=pre.solverTicks;
            reserve=v.reserve();
            fuelRatio=reserve/100f;
            previousDamage=v.previousDecisionDamage();
            earnedEver=v.energyGained();
            basalEver=v.basalSpent();
            motorEver=v.motorSpent();
            harmedEver=v.damagedEnergy();
            positiveEver=v.positiveTouches();
            negativeEver=v.negativeTouches();
            sourcesEver=v.consumedSources();
        }
    }
    static final class After{
        final long episode,beforeSensorHash,afterSensorHash;
        final int completedDecision;
        final long physicalTicks;
        final float beforeReserve,afterReserve,netEnergy,actualGained,
                    actualBasal,actualMotor,actualDamage;
        final int positiveTouches,negativeTouches,consumedSources;
        private After(Before b,Zero3SensoryFrameV315 post,
                      Zero3SurvivalLedgerV323 v,float gain,float basal,
                      float motor,float damage){
            episode=b.episode;completedDecision=b.decision;
            beforeSensorHash=b.sensoryHash;afterSensorHash=post.hash;
            physicalTicks=post.solverTicks-b.firstPhysicalTick;
            beforeReserve=b.reserve;afterReserve=v.reserve();
            netEnergy=afterReserve-beforeReserve;
            actualGained=gain;actualBasal=basal;
            actualMotor=motor;actualDamage=damage;
            positiveTouches=v.positiveTouches()-b.positiveEver;
            negativeTouches=v.negativeTouches()-b.negativeEver;
            consumedSources=v.consumedSources()-b.sourcesEver;
        }
    }
    static Before before(int n,Zero3SensoryFrameV315 pre,
                         Zero3SurvivalLedgerV323 v){
        need(pre!=null&&v!=null&&pre.preAction&&pre.intact()&&
             n>=0&&pre.frameId==n&&v.decisions()==n,
             "PRE must be a genuine complete previous-state observation");
        float fuel=v.reserve(), harm=v.previousDecisionDamage();
        need(Float.isFinite(fuel)&&fuel>=0f&&fuel<=100f&&
             Float.isFinite(harm)&&harm>=0f,
             "PRE measured vital fields missing or invalid");
        return new Before(pre,v,n);
    }
    static After after(Before b,Zero3SensoryFrameV315 post,
                       Zero3SurvivalLedgerV323 v){
        need(b!=null&&post!=null&&v!=null&&post.intact()&&
             !post.preAction&&post.episodeId==b.episode&&
             post.frameId==(long)b.decision+1&&
             v.decisions()==b.decision+1&&
             post.solverTicks-b.firstPhysicalTick==
                 Zero3RuntimeV325.SUBSTEPS,
             "POST cannot be future data / incomplete or foreign decision");
        float gain=v.energyGained()-b.earnedEver,
              basal=v.basalSpent()-b.basalEver,
              motor=v.motorSpent()-b.motorEver,
              damage=v.damagedEnergy()-b.harmedEver;
        need(Float.isFinite(gain)&&Float.isFinite(basal)&&
             Float.isFinite(motor)&&Float.isFinite(damage)&&
             gain>=-1e-5f&&basal>=-1e-5f&&
             motor>=-1e-5f&&damage>=-1e-5f,
             "nonfinite / decreasing real physical energy counters");
        After outcome=new After(b,post,v,gain,basal,motor,damage);
        need(outcome.positiveTouches>=0&&outcome.negativeTouches>=0&&
             outcome.consumedSources>=0,
             "negative real physical source count");
        float conservation=gain-basal-motor-damage;
        need(Math.abs(outcome.netEnergy-conservation)<=.002f,
             "real-world fuel balance inconsistent with physics");
        return outcome;
    }
}
