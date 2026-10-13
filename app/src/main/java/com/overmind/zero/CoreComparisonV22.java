package com.overmind.zero;

import java.util.*;

/** Phone-run paired physical screening. Clones ONE saved profile, never
 * writes live learning. Real 96-point tissue, real covers, plates, hazards;
 * original control versus active recurrent control. This reduced arena has
 * fixed controls and no world streaming/storms: it is NOT full game parity.
 */
public final class CoreComparisonV22 {
    public static final String RESULTS="zero_core_lab_v22";
    private static final int[] SEEDS={101,211,307,409},LAYOUTS={3,4,6};
    public static final class Profile {
        final Map<String,?> saved;
        final Zero20CoveredEnergyBenchmark.Profile physical;
        Profile(Map<String,?> values){saved=new LinkedHashMap<>(values);physical=new Zero20CoveredEnergyBenchmark.Profile();
            physical.motor=weights(values,"best",MotorBrain.PARAMS,physical.motor);
            physical.hand=weights(values,"hand_brain_v04",ContactLearning.PARAMS,physical.hand);
            physical.membrane=weights(values,"morph_brain_v07",Morphology.PARAMS,physical.membrane);
            physical.world=text(values,"sensorimotor_model_v09");physical.outcome=text(values,"outcome_credit_v101");
            physical.diagnostics=text(values,"sensorimotor_diagnostics_v091");physical.planner=text(values,"learned_planner_stats_v102");
            physical.temporal=text(values,"temporal_working_memory_v106");physical.skills=text(values,"temporal_skills_v107");
            physical.episodes=text(values,"episodic_memory_v095");physical.body=text(values,BodyProprioception.KEY);
            physical.spatial=text(values,SpatialPerception.KEY);physical.causal=text(values,CausalExperiments.KEY);
            physical.nav=text(values,InstrumentalNavigation.KEY);physical.intention=text(values,GoalContinuity.KEY);
            physical.v20=text(values,PredictiveWorldV20.KEY);physical.transfer=text(values,TransferSkillsV20.KEY);
            physical.future=text(values,FuturePlannerV20.KEY);physical.physicalChain=text(values,PhysicalConsequenceV20.KEY);
            physical.gripAffordance=text(values,GripAffordanceV20.KEY);
        }
    }
    public static Profile capture(Map<String,?> saved){return new Profile(saved);}
    private static String text(Map<String,?> map,String key){Object v=map.get(key);return v instanceof String?(String)v:"";}
    private static float[] weights(Map<String,?> map,String key,int n,float[] fallback){
        String raw=text(map,key);if(raw.isEmpty())return fallback.clone();
        float[] f=Zero20CoveredEnergyBenchmark.floats(raw);
        if(key.equals("morph_brain_v07"))f=Morphology.migrate(f);
        if(f.length!=n)throw new IllegalArgumentException("Invalid saved controls");
        for(float v:f)if(!Float.isFinite(v)||Math.abs(v)>5)throw new IllegalArgumentException("Invalid coefficient");return f;
    }
    private static Zero20CoveredEnergyBenchmark.Rig rig(Profile p,boolean active){
        Zero20CoveredEnergyBenchmark.Rig r=new Zero20CoveredEnergyBenchmark.Rig(p.physical.copy(),false);
        DistributedBodySense d=new DistributedBodySense();d.restore(text(p.saved,DistributedBodySense.KEY));r.mind.attachDistributedBody(d);
        LocalImpedanceSense i=new LocalImpedanceSense();i.restore(text(p.saved,LocalImpedanceSense.KEY));r.mind.attachImpedance(i);
        ObjectExperienceV20 o=new ObjectExperienceV20();o.restore(text(p.saved,ObjectExperienceV20.KEY));r.mind.attachObjectExperienceV20(o);
        ObjectContinuityV21 c=new ObjectContinuityV21();c.restore(text(p.saved,ObjectContinuityV21.KEY));r.mind.attachObjectContinuityV21(c);
        PerceptualAssociationsV20 a=new PerceptualAssociationsV20();a.restore(text(p.saved,PerceptualAssociationsV20.KEY));r.mind.attachPerceptualAssociationsV20(a);
        MortalityLearning m=new MortalityLearning();m.restore(text(p.saved,MortalityLearning.KEY));r.mind.attachMortalityLearning(m);
        r.competence.restore(text(p.saved,CompetenceMemoryV21.KEY));
        if(active){RecurrentCoreV22 core=new RecurrentCoreV22();core.restore(text(p.saved,RecurrentCoreV22.KEY));r.mind.attachActiveCore(core);}
        r.activeCore=active;return r;
    }
    public static final class Result {
        public int finished,wins,losses,ties;
        public double energyDelta,damageDelta,survivalDelta,gripDelta;
        private final StringBuilder details=new StringBuilder();
        public int total(){return SEEDS.length*LAYOUTS.length*2;}
        public String report(){int pairs=finished/2;return String.format(Locale.US,
            "ZERO 2.2 / CONFRONTO FISICO\n%d/%d prove, %d coppie. Vittorie %d, sconfitte %d, pareggi %d.\n"+
            "ATTIVO meno CONTROLLO: energia netta %+.3f, danni %+.3f, sopravvivenza %+.3f s, frame prese opposte %+.3f (medie).\n"+
            "Cloni identici del profilo salvato; 170s simulati per prova, apprendimento locale in entrambe. Il CONTROLLO usa la policy precedente; ATTIVO usa il nuovo nucleo e recupero contestuale.\n"+
            "Arena con casse reali, energia finita, lastre e pericoli. Nessuna tempesta, mutazione corporea o streaming. Non certifica il gioco intero o superintelligenza. Dodici coppie sono uno screening.\n\n%s",
            finished,total(),pairs,wins,losses,ties,energyDelta/Math.max(1,pairs),damageDelta/Math.max(1,pairs),
            survivalDelta/Math.max(1,pairs),gripDelta/Math.max(1,pairs),details.toString());}
    }
    public static Result run(Profile p,BehavioralLaboratory.Progress progress,BehavioralLaboratory.Cancel cancel)throws Exception{
        Result report=new Result();
        for(int layout:LAYOUTS)for(int seed:SEEDS){
            if(cancel!=null&&cancel.stopped())return report;
            Zero20CoveredEnergyBenchmark.Outcome[] pair=new Zero20CoveredEnergyBenchmark.Outcome[2];
            for(int mode=0;mode<2;mode++){
                Zero20CoveredEnergyBenchmark.Rig r=rig(p,mode==1);
                pair[mode]=Zero20CoveredEnergyBenchmark.episode(r,p.physical.copy(),seed,layout,(int)(170f/Zero20CoveredEnergyBenchmark.DT),cancel);
                if(pair[mode]==null)return report;
                report.finished++;report.details.append(pair[mode].json(mode==1?"ACTIVE22":"CONTROL")).append('\n');
                if(progress!=null)progress.step(report.finished,report.total(),"scena "+layout+" seed "+seed+" "+(mode==1?"ATTIVO":"CONTROLLO"));
            }
            Zero20CoveredEnergyBenchmark.Outcome a=pair[0],b=pair[1];
            double delta=b.net-a.net+.1*(b.duration-a.duration)-.3*(b.damage-a.damage);
            if(delta>.01)report.wins++;else if(delta<-.01)report.losses++;else report.ties++;
            report.energyDelta+=b.net-a.net;report.damageDelta+=b.damage-a.damage;
            report.survivalDelta+=b.duration-a.duration;report.gripDelta+=b.opposedFrames-a.opposedFrames;
        }
        return report;
    }
    public static void main(String[] args)throws Exception{
        Profile p=capture(Collections.emptyMap());
        Result canceled=run(p,null,()->true);
        if(canceled.finished!=0||canceled.report().isEmpty())throw new AssertionError("cancel");
        Zero20CoveredEnergyBenchmark.Outcome a=Zero20CoveredEnergyBenchmark.episode(rig(p,true),p.physical.copy(),101,4,120,null);
        Zero20CoveredEnergyBenchmark.Outcome b=Zero20CoveredEnergyBenchmark.episode(rig(p,true),p.physical.copy(),101,4,120,null);
        if(a.decisions<1||a.net!=b.net||a.travel!=b.travel||a.duration!=b.duration)throw new AssertionError("isolated paired determinism");
        System.out.println("PASS ZERO22 physical arena: same-source clones, repeatability, cancellation, real tissue/cover physics; no claim of uplift");
    }
}
