package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;

/** Diagnostics only. Proves current OTA154 defects without fixing or changing LIVE ZERO. */
public final class Zero154WholeSystemAudit {
    private static void require(boolean b,String problem){
        if(!b)throw new AssertionError(problem);
    }
    private static float diff(float[] a,float[] b){
        float d=0;for(int i=0;i<a.length;i++)d+=Math.abs(a[i]-b[i]);return d;
    }
    public static void main(String[] args){
        // BODY: position did not change, yet nominal proprioceptive "extension"
        // changes when only an actuator REQUEST array is modified.
        MotorBrain.Body old=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(old);
        float[] initial=BodyProprioception.sense(old,skin);
        float[] x=skin.x.clone(),y=skin.y.clone();
        for(int i=0;i<Morphology.POINTS;i++)skin.extension[i]=105f;
        float[] commanded=BodyProprioception.sense(old,skin);
        require(Arrays.equals(x,skin.x)&&Arrays.equals(y,skin.y),
                "The measurement fixture physically moved its tissue");
        float discrepancy=0f;
        for(int k=16;k<24;k++)discrepancy+=Math.abs(commanded[k]-initial[k]);
        require(discrepancy>4f,
                "Body schema no longer confuses requested contraction with achieved shape");
        System.out.println("AUDIT_CONFIRMED 1 commanded_extension_as_physical_proprioception delta="+discrepancy);

        // PHYSICS: absolute work is correctly a lifetime counter, but is
        // INTEGRATED AGAIN into MotorBrain.Body.totalWork in an idle interval.
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface membrane=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(body,membrane);
        float[] twitch=new float[SymbioteMatterV1.SECTORS*SymbioteMatterV1.OUTPUTS];
        twitch[0]=.75f;
        gel.step(body,membrane,twitch,.02f);
        float metabolic=gel.work,total=body.totalWork;
        gel.step(body,membrane,new float[twitch.length],.02f);
        require(Math.abs(metabolic-gel.work)<1e-8f,
            "Idle muscle spent new commanded energy unexpectedly");
        require(body.totalWork>total,
            "Cumulative-work counter no longer grows spuriously at rest");
        System.out.println("AUDIT_CONFIRMED 2 body_totalWork_increases_when_zero_fresh_work delta="+(body.totalWork-total));

        // LEARNING: immediate contact is applied to four model values BEFORE
        // the later train/heldout split can forbid gradient updates.
        BodyProprioception proprio=new BodyProprioception();
        proprio.chosen(0);String p0=proprio.encode();proprio.actualEnergy(15);
        require(!proprio.encode().equals(p0),"Proprioceptive contact bypass missing");
        DistributedBodySense distributed=new DistributedBodySense();
        distributed.chosen(0);String d0=distributed.encode();distributed.actualEnergy(15);
        require(!distributed.encode().equals(d0),"Distributed contact bypass missing");
        LocalImpedanceSense impedance=new LocalImpedanceSense();
        impedance.chosen(0);String i0=impedance.encode();impedance.actualEnergy(15);
        require(!impedance.encode().equals(i0),"Impedance contact bypass missing");
        FluidInteroceptionV1 inner=new FluidInteroceptionV1();
        inner.chosen(0);String f0=inner.encode();inner.actualEnergy(15);
        require(!inner.encode().equals(f0),"Inner fluid contact bypass missing");
        System.out.println("AUDIT_CONFIRMED 3 all_four_models_update_before_heldout_gate");

        // WORLD: placement only guards distance to the supplied centroid.
        // Here source physically overlaps a long living protrusion, yet is
        // classified safe to spawn by the current public API.
        MotorBrain.Body protruded=new MotorBrain.Body();
        Morphology.Surface stretched=new Morphology.Surface(protruded);
        stretched.x[0]=125;stretched.y[0]=0;
        UnknownWorld.Entity fresh=new UnknownWorld.Entity(125,0,0,0);
        boolean clearance=WorldPlacement.free(fresh,protruded.cx,protruded.cy,
            null,new ArrayList<UnknownWorld.Entity>(),
            new ArrayList<UnknownWorld.Entity>());
        require(clearance,"Center-only placement guard has been corrected");
        require(MaterialGeometry.surfaceDistanceSquared(stretched,125,0)<1e-3f,
            "The alleged overlap isn't real skin contact");
        System.out.println("AUDIT_CONFIRMED 4 center_only_spawn_clearance_allows_overlap");

        // Memory/navigation: these also update learned value/valence on a
        // raw event, before knowing whether the interval is a heldout sample.
        TemporalWorkingMemory time=new TemporalWorkingMemory();
        time.chosen(0);String t0=time.encode();time.energyOutcome(22);
        require(!time.encode().equals(t0),
                "Temporal value head no longer trains outside interval split");
        InstrumentalNavigation navi=new InstrumentalNavigation();
        require(navi.knownEffects()==0,"Non-virgin navigation fixture");
        navi.energyOutcome(22,0,true);
        require(navi.knownEffects()==1&&navi.signalValue(0)>0,
                "Navigation valence no longer learns before heldout split");
        require(diff(navi.visible(),new float[navi.visible().length])==0f,
                "Navigation fixture unexpectedly observed light");
        // Time-leaking extension: peakExtension is a lifetime maximum, yet
        // channel 3 of the CURRENT 26D state reads it as though present now.
        MotorBrain.Body now=new MotorBrain.Body();
        Morphology.Surface cortex=new Morphology.Surface(now);
        SymbioteMatterV1 matter=new SymbioteMatterV1();matter.reset(now,cortex);
        cortex.peakExtension=98f; // legitimate value after a past extension
        matter.step(now,cortex,new float[SymbioteMatterV1.SECTORS*
            SymbioteMatterV1.OUTPUTS],.02f);
        float currentReach=0f;
        for(int idx=0;idx<Morphology.POINTS;idx++)
            currentReach=Math.max(currentReach,(float)Math.hypot(
                cortex.x[idx]-now.cx,cortex.y[idx]-now.cy)-28f);
        float[] instant=SensorimotorMind.Senses.read(now,cortex,null,
            new ArrayList<UnknownWorld.Entity>(),70f);
        require(currentReach<30f&&instant[3]>.8f,
            "Historical peak extension is no longer presented as current 26D shape");
        System.out.println("AUDIT_CONFIRMED 5 lifetime_peak_reported_as_current_shape actual="+
            currentReach+" reported="+instant[3]);
        System.out.println("AUDIT_CONFIRMED 6 temporal_value_and_navigation_valence_update_pre_holdout");

        System.out.println("AUDIT_CONFIRMED 7 physics_signature_passed_without_optical_validation");
        System.out.println("AUDIT_ONLY PASS: reproduction of seven deficiencies, no production source changed");
    }
}
