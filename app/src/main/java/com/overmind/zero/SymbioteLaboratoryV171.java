package com.overmind.zero;
import java.util.*;
/** Isolated, deterministic real symbiote+2.2 experiments. A compares frozen
 * synapses to B with online learning from the SAME saved neural checkpoint.
 * No electrodes/gait/target direction. No writes to Android preferences.
 * Streaming and rendering are excluded and explicitly reported. */
public final class SymbioteLaboratoryV171 {
    private static String text(Map<String,?> p,String k){Object v=p.get(k);return v instanceof String?(String)v:"";}
    public static final class Trial{
        public float net,gross,signed,energy,seconds,objectTravel;
        public int decisions,backwards,invalid,pickups;
        public String motorBefore,motorAfter;
    }
    public static Trial run(Map<String,?> saved,int seed,int scenario,float seconds,
                            boolean learn,BehavioralLaboratory.Cancel cancel){
        MotorBrain.Body body=new MotorBrain.Body();Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 matter=new SymbioteMatterV1();matter.reset(body,skin);
        SymbioteMotorHeadV1 head=new SymbioteMotorHeadV1();
        head.restore(text(saved,head.KEY));head.restoreLocal(text(saved,head.LOCAL_KEY));
        head.restoreFast(text(saved,FastSomaticCircuitV167.KEY));head.restoreCoordination(text(saved,SymbioteCoordinationV162.KEY));
        head.restoreMemory(text(saved,SymbioteMotorSkillMemoryV169.KEY));
        head.restoreManipulation(text(saved,SymbioteMotorSkillMemoryV169.MANIPULATION_KEY));
        RecurrentCoreV22 core=new RecurrentCoreV22();core.restore(text(saved,core.KEY));
        SensorimotorMind mind=new SensorimotorMind();mind.attachActiveCore(core);mind.attachSymbioteMotorHead(head);
        mind.restore(text(saved,"sensorimotor_model_v09"));mind.diagnostics.restore(text(saved,"sensorimotor_diagnostics_v091"));
        mind.restoreOutcomeModel(text(saved,"outcome_credit_v101"));
        mind.activeDiagnostics.restore(text(saved,LearningDiagnostics.ACTIVE_KEY));
        DistributedBodySense distributed=new DistributedBodySense();distributed.restore(text(saved,distributed.KEY));mind.attachDistributedBody(distributed);
        BodyProprioception proprio=new BodyProprioception();proprio.restore(text(saved,proprio.KEY));mind.attachBodySchema(proprio);
        LocalImpedanceSense impedance=new LocalImpedanceSense();impedance.restore(text(saved,impedance.KEY));mind.attachImpedance(impedance);
        FluidInteroceptionV1 inner=new FluidInteroceptionV1();inner.restore(text(saved,inner.KEY));mind.attachFluidInteroception(inner,matter);
        SpatialPerception space=new SpatialPerception();space.restore(text(saved,space.KEY));mind.attachSpatialPerception(space);
        InstrumentalNavigation nav=new InstrumentalNavigation();nav.restore(text(saved,nav.KEY));nav.restoreContextual(text(saved,ContextualNavigationV171.KEY));mind.attachNavigation(nav);
        CausalExperiments causal=new CausalExperiments();causal.restore(text(saved,causal.KEY));mind.attachCausalExperiments(causal);
        GoalContinuity goal=new GoalContinuity();goal.restore(text(saved,goal.KEY));mind.attachGoalContinuity(goal);
        TemporalWorkingMemory time=new TemporalWorkingMemory();time.restore(text(saved,"temporal_working_memory_v106"));mind.attachWorkingMemory(time);
        TemporalSkillBank skills=new TemporalSkillBank();skills.restore(text(saved,"temporal_skills_v107"));mind.attachTemporalSkills(skills);
        ObjectExperienceV20 experience=new ObjectExperienceV20();experience.restore(text(saved,experience.KEY));mind.attachObjectExperienceV20(experience);
        ObjectContinuityV21 objects=new ObjectContinuityV21();objects.restore(text(saved,objects.KEY));mind.attachObjectContinuityV21(objects);
        CausalSequenceV23 sequence=new CausalSequenceV23();sequence.restore(text(saved,sequence.KEY));mind.attachCausalSequenceV23(sequence);
        MortalityLearning mortality=new MortalityLearning();mortality.restore(text(saved,mortality.KEY));mind.attachMortalityLearning(mortality);
        MotorDurationV22 duration=new MotorDurationV22();duration.restore(text(saved,duration.KEY));mind.attachMotorDuration(duration);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();ScarcityLab room=null;
        float angle=(seed%4)*(float)Math.PI/2f;
        if(scenario==3){room=new ScarcityLab(body.cx,body.cy);world.add(room.food);}
        else if(scenario>0){
            world.add(new UnknownWorld.Entity(135f*(float)Math.cos(angle),135f*(float)Math.sin(angle),0,0));
            if(scenario==2){UnknownWorld.Entity panel=new UnknownWorld.Entity(85,12,2,6);panel.angle=.7f;world.add(panel);}
        }
        Trial out=new Trial();out.energy=70f;float originX=body.cx,originY=body.cy,ax=originX,ay=originY,effect=0;
        float dt=PhysicsClockV171.DT;float[] sx=new float[96],sy=new float[96],bx=new float[MotorBrain.N],by=new float[MotorBrain.N];
        mind.setLearning(learn);mind.resetEpisode(null);
        // First choice is grounded in current measured physical senses.
        mind.observe(SensorimotorMind.Senses.read(body,skin,room,world,out.energy),0f,0f,body,skin,room,world);
        out.motorBefore=Arrays.toString(head.verifiedMotorSnapshot());
        IdentityHashMap<UnknownWorld.Entity,float[]> old=new IdentityHashMap<>();
        for(UnknownWorld.Entity e:world)if(e.isBox())old.put(e,new float[2]);
        for(int f=0;f<(int)(seconds/dt)&&out.energy>0f;f++){
            if(cancel!=null&&cancel.stopped())return null;
            float x=body.cx,y=body.cy;float oldGate=room==null?0f:room.doorY;
            for(UnknownWorld.Entity e:world){if(e.isBox()){old.get(e)[0]=e.x;old.get(e)[1]=e.y;}e.tick(dt);}
            System.arraycopy(skin.x,0,sx,0,96);System.arraycopy(skin.y,0,sy,0,96);
            System.arraycopy(body.x,0,bx,0,bx.length);System.arraycopy(body.y,0,by,0,by.length);
            head.fastTick(body,skin,dt);mind.appliedSymbioteControls(head.commands(),body,skin);
            float work=matter.work;matter.step(body,skin,head.commands(),dt);
            float uncorrectedX=body.cx,uncorrectedY=body.cy;
            float cost=(matter.work-work)*8f;out.energy-=.3f*dt+cost;mind.motorMetabolicCost(cost);
            Arrays.fill(skin.touchSense,0f);
            if(room!=null)room.constrain(body,skin,dt,mind.adhesion());
            for(UnknownWorld.Entity e:world){
                if(e.isBilateralBox())SquareGripPuzzle.constrain(body,skin,e,dt,sx,sy,bx,by,x,y);
                else if(e.isConstruction())PanelPhysics.constrain(body,skin,e,dt,sx,sy,bx,by);
            }
            MaterialContactConstraints.reconcile(body,skin,world,dt);
            SymbioteWorldStepV171.interior(matter,body,skin,room,world,dt);
            GripSurfaceComposition.compose(skin,room,world);
            boolean external=Math.hypot(body.cx-uncorrectedX,body.cy-uncorrectedY)>.08f;
            for(int i=0;i<96;i++)external|=skin.touchSense[i]>.25f||skin.gripHold[i]>=Morphology.GRIP_HOLD;
            float heldTravel=0;boolean held=false;
            for(UnknownWorld.Entity e:world)if(e.isBox()){
                boolean grip=e.isConstruction()?PanelPhysics.anyHeld(e):e.bilateralHeldNow;
                if(grip){held=true;heldTravel+=Math.hypot(e.x-old.get(e)[0],e.y-old.get(e)[1]);}
            }
            if(room!=null){boolean grip=room.gateHeldFrames>0;
                if(grip){held=true;heldTravel+=Math.abs(room.doorY-oldGate);}}
            effect+=heldTravel;out.objectTravel+=heldTravel;head.observedManipulation(heldTravel,held);
            head.observedPhysicalStep(x,y,body,skin,matter.lastMuscleReactionImpulse,external,dt);
            out.gross+=Math.hypot(body.cx-x,body.cy-y);
            for(UnknownWorld.Entity e:world)if(!e.isBox()&&e.ready()&&!e.concealed&&
                MaterialGeometry.touches(body,skin,e.x,e.y,e.radius+2f)){
                float before=out.energy;float change=e.touch(0,0);out.energy=Math.max(0,Math.min(100,out.energy+change));
                mind.contactOutcome(out.energy-before,e.signature);if(change>0)out.pickups++;
            }
            int action=mind.action;long before=mind.experiences;boolean due=mind.observationDue(dt);
            mind.observe(due?SensorimotorMind.Senses.read(body,skin,room,world,out.energy):null,dt,effect,body,skin,room,world);
            if(mind.experiences>before){float forward=(body.cx-ax)*(float)Math.cos(action%8*Math.PI/4)+
                (body.cy-ay)*(float)Math.sin(action%8*Math.PI/4);out.signed+=forward;
                if(forward<0)out.backwards++;out.decisions++;ax=body.cx;ay=body.cy;effect=0;}
            out.seconds+=dt;out.invalid=(int)matter.invalid;
        }
        out.net=(float)Math.hypot(body.cx-originX,body.cy-originY);out.motorAfter=Arrays.toString(head.verifiedMotorSnapshot());return out;
    }
    public static CoreComparisonV22.Result compare(Map<String,?> profile,BehavioralLaboratory.Progress progress,
        BehavioralLaboratory.Cancel cancel,float seconds)throws Exception{
        CoreComparisonV22.Result result=new CoreComparisonV22.Result();StringBuilder lines=new StringBuilder();
        for(int scenario=1;scenario<=3;scenario++)for(int seed=0;seed<4;seed++){
            Trial[] pair=new Trial[2];
            for(int mode=0;mode<2;mode++){
                pair[mode]=run(profile,seed,scenario,seconds,mode==1,cancel);
                if(pair[mode]==null){result.symbioteReport=report(lines,result);return result;}
                result.finished++;if(progress!=null)progress.step(result.finished,24,"simbionte scena "+scenario+" / "+seed);
            }
            Trial a=pair[0],b=pair[1];double delta=b.energy-a.energy;
            if(delta>.01)result.wins++;else if(delta<-.01)result.losses++;else result.ties++;
            lines.append(String.format(Locale.US,"Scena %d/%d energia A/B %.2f/%.2f; netto %.2f/%.2f; percorso %.2f/%.2f; proiezione scelta %.2f/%.2f; raccolte %d/%d; invalidi %d/%d%n",scenario,seed,a.energy,b.energy,a.net,b.net,a.gross,b.gross,a.signed,b.signed,a.pickups,b.pickups,a.invalid,b.invalid));
        }
        result.symbioteReport=report(lines,result);return result;
    }
    private static String report(StringBuilder lines,CoreComparisonV22.Result r){
        return "SIMBIONTE / CERVELLO 2.2 — "+r.finished+"/24 prove\n"+
            "A: pesi congelati; B: apprendimento online. Stessi pesi iniziali, stessa scena.\n"+
            "96 membrana + 74 particelle reali, 64 attuatori neurali, dt fisso 1/60s; nessuna andatura imposta.\n"+
            "Energia: B migliore "+r.wins+", A migliore "+r.losses+", pari "+r.ties+".\n"+
            "Non include streaming, rendering o tempeste. Screening controllato, non prova di miglioramento sul telefono.\n"+lines;
    }
}
