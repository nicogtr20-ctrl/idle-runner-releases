package com.overmind.zero;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

/**
 * A/B research arena with the same LIVE 96-site surface, bilateral 40x40
 * energy covers, concealed finite energy, panel and contact sensors.
 * Never moves objects/creature toward a scripted target.
 * ZERO21 shadowCompare verifies new unified observers against control
 * on identical hidden covers and unchanged physical brain/motor action. Examiner sees
 * locations solely to measure outcomes; policy only sees Senses.read().
 *
 * Not the complete Android lifetime, procedural world streaming, storm and
 * online mutations; results must never be labelled full game parity.
 */
public final class Zero20CoveredEnergyBenchmark {
    static final float DT=.035f, MAX_SECONDS=128f, DRAIN=.31f;
    // Research-only alternative duration: observe genuine energy depletion.
    // Normal 128s runs remain bit-for-bit unchanged; Android never calls this.
    static final float LONG_SURVIVAL_SECONDS=170f;
    static final int FRAMES=(int)(MAX_SECONDS/DT);
    static int episodeFrames(){
        return Boolean.getBoolean("zero20.longSurvivalDiagnostic")
            ?(int)(LONG_SURVIVAL_SECONDS/DT):FRAMES;
    }
    static class Profile {
        float[] motor=MotorBrain.virgin(new Random(918273));
        float[] hand=ContactLearning.virgin();
        float[] membrane=Morphology.virgin(new Random(14790));
        String world="",outcome="",diagnostics="",planner="",temporal="",
            skills="",episodes="",body="",spatial="",causal="",nav="",
            intention="",v20="",transfer="",future="",physicalChain="",gripAffordance="";
        Profile copy(){
            Profile o=new Profile();
            o.motor=motor.clone();o.hand=hand.clone();o.membrane=membrane.clone();
            o.world=world;o.outcome=outcome;o.diagnostics=diagnostics;o.planner=planner;
            o.temporal=temporal;o.skills=skills;o.episodes=episodes;o.body=body;
            o.spatial=spatial;o.causal=causal;o.nav=nav;o.intention=intention;
            o.v20=v20;o.transfer=transfer;o.future=future;
            o.physicalChain=physicalChain;o.gripAffordance=gripAffordance;return o;
        }
    }
    static float[] floats(String raw){
        String[] words=raw.split(",");
        float[] f=new float[words.length];
        for(int i=0;i<f.length;i++)f[i]=Float.parseFloat(words[i]);
        return f;
    }
    static Profile load(String file)throws Exception{
        Profile p=new Profile();
        if(file==null)return p;
        Properties props=new Properties();
        try(FileInputStream in=new FileInputStream(file)){props.load(in);}
        if(props.containsKey("best")){
            float[] f=floats(props.getProperty("best"));
            if(f.length!=MotorBrain.PARAMS)throw new AssertionError("motor weights");
            p.motor=f;
        }
        if(props.containsKey("hand_brain_v04")){
            float[] f=floats(props.getProperty("hand_brain_v04"));
            if(f.length!=ContactLearning.PARAMS)throw new AssertionError("hand weights");
            p.hand=f;
        }
        if(props.containsKey("morph_brain_v07")){
            float[] f=floats(props.getProperty("morph_brain_v07"));
            p.membrane=Morphology.migrate(f);
            if(p.membrane.length!=Morphology.PARAMS)
                throw new AssertionError("membrane weights");
        }
        String[][] names={
            {"sensorimotor_model_v09","world"},{"outcome_credit_v101","outcome"},
            {"sensorimotor_diagnostics_v091","diagnostics"},
            {"learned_planner_stats_v102","planner"},
            {"temporal_working_memory_v106","temporal"},
            {"temporal_skills_v107","skills"},
            {"episodic_memory_v095","episodes"},
            {BodyProprioception.KEY,"body"},{SpatialPerception.KEY,"spatial"},
            {CausalExperiments.KEY,"causal"},{InstrumentalNavigation.KEY,"nav"},
            {GoalContinuity.KEY,"intention"},
            {PredictiveWorldV20.KEY,"v20"},
            {TransferSkillsV20.KEY,"transfer"},
            {FuturePlannerV20.KEY,"future"},
            {PhysicalConsequenceV20.KEY,"physicalChain"},
            {GripAffordanceV20.KEY,"gripAffordance"}
        };
        for(String[] entry:names){
            String raw=props.getProperty(entry[0],"");
            if("world".equals(entry[1]))p.world=raw;
            else if("outcome".equals(entry[1]))p.outcome=raw;
            else if("diagnostics".equals(entry[1]))p.diagnostics=raw;
            else if("planner".equals(entry[1]))p.planner=raw;
            else if("temporal".equals(entry[1]))p.temporal=raw;
            else if("skills".equals(entry[1]))p.skills=raw;
            else if("episodes".equals(entry[1]))p.episodes=raw;
            else if("body".equals(entry[1]))p.body=raw;
            else if("spatial".equals(entry[1]))p.spatial=raw;
            else if("causal".equals(entry[1]))p.causal=raw;
            else if("nav".equals(entry[1]))p.nav=raw;
            else if("intention".equals(entry[1]))p.intention=raw;
            else if("v20".equals(entry[1]))p.v20=raw;
            else if("transfer".equals(entry[1]))p.transfer=raw;
            else if("future".equals(entry[1]))p.future=raw;
            else if("physicalChain".equals(entry[1]))p.physicalChain=raw;
            else if("gripAffordance".equals(entry[1]))p.gripAffordance=raw;
        }
        return p;
    }
    static final class Rig {
        final SensorimotorMind mind=new SensorimotorMind();
        final PredictiveWorldV20 world=new PredictiveWorldV20();
        final TransferSkillsV20 transfer=new TransferSkillsV20();
        final FuturePlannerV20 future=new FuturePlannerV20();
        final PhysicalConsequenceV20 physicalChain=new PhysicalConsequenceV20();
        final GripAffordanceV20 gripAffordance=new GripAffordanceV20();
        final TemporalWorkingMemory temporal=new TemporalWorkingMemory();
        final TemporalSkillBank skills=new TemporalSkillBank();
        final EpisodicMemory episodes=new EpisodicMemory();
        final BodyProprioception body=new BodyProprioception();
        final SpatialPerception spatial=new SpatialPerception();
        final CausalExperiments causal=new CausalExperiments();
        final InstrumentalNavigation nav=new InstrumentalNavigation();
        final GoalContinuity intention=new GoalContinuity();
        final MotorAutonomyGuard guard=new MotorAutonomyGuard();
        Rig(Profile p,boolean experiment){
            if(!p.world.isEmpty()&&!mind.restore(p.world))
                throw new IllegalArgumentException("Original saved ZERO brain failed validation");
            mind.restoreOutcomeModel(p.outcome);
            mind.diagnostics.restore(p.diagnostics);
            mind.restorePlanningStats(p.planner);
            world.restore(p.v20);transfer.restore(p.transfer);future.restore(p.future);
            physicalChain.restore(p.physicalChain);
            gripAffordance.restore(p.gripAffordance);
            temporal.restore(p.temporal);skills.restore(p.skills);
            episodes.restore(p.episodes);body.restore(p.body);
            spatial.restore(p.spatial);causal.restore(p.causal);
            nav.restore(p.nav);intention.restore(p.intention);
            mind.attachPredictiveWorldV20(world);
            mind.attachTransferSkillsV20(transfer);
            mind.attachFuturePlannerV20(future);
            mind.attachPhysicalConsequenceV20(physicalChain);
            mind.attachGripAffordanceV20(gripAffordance);
            mind.attachWorkingMemory(temporal);
            mind.attachTemporalSkills(skills);
            mind.attachMemory(episodes);
            mind.attachBodySchema(body);
            mind.attachSpatialPerception(spatial);
            mind.attachCausalExperiments(causal);
            mind.attachNavigation(nav);
            mind.attachGoalContinuity(intention);
            mind.attachMotorGuard(guard);
            // ZERO21 paired SHADOW mode: ONE side gets only non-actuating
            // shared cognition, multi-view tracking and unified arbitration.
            // The historical research-only motor advice is OFF for both sides.
            boolean zero21=Boolean.getBoolean("zero21.shadowCompare");
            if(zero21&&experiment){
                mind.attachUnifiedCognitionV21(new UnifiedCognitionV21());
                mind.attachObjectContinuityV21(new ObjectContinuityV21());
                mind.attachUnifiedArbiterV21(new UnifiedArbiterV21());
            }
            mind.enableZero20ExperimentalAdvice(experiment&&!zero21);
        }
        Profile export(Profile physical){
            Profile out=physical.copy();
            out.world=mind.serialize();out.outcome=mind.outcomeModel();
            out.diagnostics=mind.diagnostics.encode();
            out.planner=mind.planningStats();
            out.temporal=temporal.encode();out.skills=skills.encode();
            out.episodes=episodes.encode();out.body=body.encode();
            out.spatial=spatial.encode();out.causal=causal.encode();
            out.nav=nav.encode();out.intention=intention.encode();
            out.v20=world.encode();out.transfer=transfer.encode();
            out.future=future.encode();out.physicalChain=physicalChain.encode();
            out.gripAffordance=gripAffordance.encode();
            return out;
        }
    }
    static final class Outcome {
        int seed,layout,positive,harmful,uncovered,opposedFrames,failedFrames;
        int physicallyConfirmedCoverMotionFrames;
        float duration,net,positiveEnergy,damage,travel,remaining;
        long decisions,eligible;int advised;
        boolean died;
        String json(String phase){
            return String.format(Locale.US,
                "ZERO20_ARENA {\"phase\":\"%s\",\"seed\":%d,\"layout\":%d,"+
                "\"seconds\":%.3f,\"gain\":%.3f,\"damage\":%.3f,"+
                "\"net\":%.3f,\"reserve\":%.3f,\"travel\":%.3f,"+
                "\"covers\":%d,\"opposedFrames\":%d,\"failedFrames\":%d,"+
                "\"physicalCoverMoveFrames\":%d,"+
                "\"positiveContacts\":%d,\"harmfulContacts\":%d,"+
                "\"died\":%s,\"decisions\":%d,\"validatedPlans\":%d,\"adviceChoices\":%d}",
                phase,seed,layout,duration,positiveEnergy,damage,net,
                remaining,travel,uncovered,opposedFrames,failedFrames,
                physicallyConfirmedCoverMotionFrames,positive,
                harmful,died,decisions,eligible,advised);
        }
    }
    static void addCover(ArrayList<UnknownWorld.Entity> objects,
                         float theta,float distance){
        float x=(float)Math.cos(theta)*distance,y=(float)Math.sin(theta)*distance;
        UnknownWorld.Entity cover=new UnknownWorld.Entity(x,y,2,5);
        UnknownWorld.Entity hidden=new UnknownWorld.Entity(x,y,0,4);
        hidden.concealed=true;
        objects.add(cover);objects.add(hidden);
    }
    static Outcome episode(Rig rig,Profile p,int seed,int layout)throws Exception{
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        // Examiner-only layout; ZERO policy is NOT supplied a target direction.
        float theta=(seed*.247f)+(layout*.33f);
        addCover(objects,theta,113+(seed%3)*7);
        if(layout!=0)addCover(objects,theta+1.25f,143);
        if(layout==2||layout==4||layout==5||layout==6){
            float x=(float)Math.cos(theta+2.15f)*175f;
            float y=(float)Math.sin(theta+2.15f)*175f;
            objects.add(new UnknownWorld.Entity(x,y,2,6));
        }
        if(layout==3||layout==4||layout==6){
            float a=theta+2.6f;
            objects.add(new UnknownWorld.Entity((float)Math.cos(a)*138,
                                                 (float)Math.sin(a)*138,1,1));
        }
        // During training, renewable ground stimulus varies. Still finite
        // and touch-only; zero free energy per tick and no scripted directions.
        if(layout==0||layout==3||layout==5){
            float a=theta-1.65f;
            objects.add(new UnknownWorld.Entity((float)Math.cos(a)*128,
                                                 (float)Math.sin(a)*128,0,0));
        }
        float reserve=40f,dx0=body.cx,dy0=body.cy,effect=0f;
        Outcome out=new Outcome();out.seed=seed;out.layout=layout;
        rig.guard.resetLife(body.cx,body.cy);
        rig.mind.resetEpisode(SensorimotorMind.Senses.read(body,skin,null,objects,reserve));
        rig.mind.updateBodySense(body,skin);
        rig.mind.updateSpatialSense(body,skin,null,objects);
        rig.mind.updateNavigationSense(body,null,objects);
        long before=rig.mind.decisions,beforeValid=rig.future.eligible();
        float[] effects=new float[UnknownWorld.SIGNATURES];
        for(int frame=0;frame<episodeFrames();frame++){
            float t=frame*DT;
            if(reserve<=0){out.died=true;break;}
            reserve=Math.max(0,reserve-DRAIN*DT);
            for(UnknownWorld.Entity e:objects){
                float previousX=e.x,previousY=e.y;
                // A bilateral grip from the PRIOR solved frame may have
                // supplied the physical velocity integrated by tick().
                boolean heldInLastFrame=e.isBilateralBox()&&e.bilateralHeldNow;
                e.tick(DT);
                if(heldInLastFrame){
                    float reallyMoved=(float)Math.hypot(e.x-previousX,e.y-previousY);
                    if(Float.isFinite(reallyMoved)&&reallyMoved>.015f){
                        effect+=Math.min(18f,reallyMoved);
                        out.physicallyConfirmedCoverMotionFrames++;
                    }
                }
            }
            // Only the examiner records world-space object motion. Neither
            // cover identity nor source coordinates are given to ZERO policy.
            float[] coverX=new float[objects.size()],coverY=new float[objects.size()];
            for(int k=0;k<objects.size();k++){
                UnknownWorld.Entity o=objects.get(k);
                if(o.isBilateralBox()){coverX[k]=o.x;coverY[k]=o.y;}
            }
            float[] sx=skin.x.clone(),sy=skin.y.clone();
            float[] bx=body.x.clone(),by=body.y.clone();
            float oldX=body.cx,oldY=body.cy;
            float mx=rig.mind.motorX(),my=rig.mind.motorY();
            float targetX=body.cx+mx*145f,targetY=body.cy+my*145f;
            body.step(p.motor,mx,my,DT,p.hand,targetX,targetY);
            skin.step(body,p.membrane,targetX,targetY,DT,rig.mind.adhesion());
            java.util.Arrays.fill(skin.touchSense,0);
            int heldThis=0,opposedThis=0,contactThis=0;
            for(UnknownWorld.Entity e:objects){
                if(!e.isBilateralBox())continue;
                SquareGripPuzzle.Outcome grab=SquareGripPuzzle.constrain(
                    body,skin,e,DT,sx,sy,bx,by,oldX,oldY);
                heldThis+=grab.held;contactThis+=grab.contacts;
                if(grab.opposingGrip)opposedThis++;
                if(SquareGripPuzzle.uncover(e,objects)){
                    out.uncovered++;
                    // Exactly mirrors live GameActivity: a physically
                    // verified cover displacement is an environmental effect,
                    // never an invented energy reward for grip alone.
                    effect+=Math.min(12f,e.displacementFromSpawn());
                }
            }
            for(UnknownWorld.Entity e:objects){
                if(e.isConstruction()&&!e.consumed)
                    PanelPhysics.constrain(body,skin,e,DT,sx,sy,bx,by);
            }
            MaterialGeometry.observe(body);
            GripSurfaceComposition.compose(skin,null,objects);
            // Match the real app exactly: one simultaneous 96-site tissue
            // reading per physical substep AFTER owned hold composition.
            rig.mind.sampleGripPhysicsFrame(skin);
            ShelterEcology.separateBoxes(objects);
            // FIX: previous research arena always passed physicalEffect=0,
            // losing actual physical feedback even after confirmed adhesion.
            // Use ONLY genuine integrated motion of a held cover (not a
            // velocity command, pressure event, suggested grip or source).
            for(int k=0;k<objects.size();k++){
                UnknownWorld.Entity o=objects.get(k);
                if(!o.isBilateralBox()||!o.bilateralHeldNow)continue;
                float delta=(float)Math.hypot(o.x-coverX[k],o.y-coverY[k]);
                if(Float.isFinite(delta)&&delta>.015f){
                    effect+=Math.min(18f,delta);
                    out.physicallyConfirmedCoverMotionFrames++;
                }
            }
            if(opposedThis>0)out.opposedFrames++;
            if(contactThis>0&&heldThis>0&&opposedThis==0)out.failedFrames++;
            // Collision did not invent energy. The source pays only when
            // revealed and then actually touched by living material.
            for(UnknownWorld.Entity e:objects){
                if(e.isBox()||e.concealed||!e.ready())continue;
                float near=(float)Math.hypot(body.cx-e.x,body.cy-e.y);
                for(int j=0;j<MotorBrain.N;j++)
                    near=Math.min(near,(float)Math.hypot(body.x[j]-e.x,body.y[j]-e.y));
                for(int j=0;j<Morphology.POINTS;j++)
                    near=Math.min(near,(float)Math.hypot(skin.x[j]-e.x,skin.y[j]-e.y));
                if(near>=18f+e.radius)continue;
                float prior=reserve;
                float directionX=e.x-body.cx,directionY=e.y-body.cy;
                float norm=Math.max(1f,(float)Math.hypot(directionX,directionY));
                reserve=Math.max(0,Math.min(100,
                    reserve+e.touch(directionX/norm,directionY/norm)));
                float gain=reserve-prior;
                if(Math.abs(gain)>.01f){
                    rig.mind.contactOutcome(gain,e.signature);
                    effects[e.signature]+=gain;
                    if(gain>0){out.positive++;out.positiveEnergy+=gain;}
                    else{out.harmful++;out.damage-=gain;}
                }
            }
            float travel=(float)Math.hypot(body.cx-dx0,body.cy-dy0);
            if(Float.isFinite(travel)&&travel<120f)out.travel+=travel;
            dx0=body.cx;dy0=body.cy;
            rig.guard.observe(DT,body.cx,body.cy,effect);
            long oldExperience=rig.mind.experiences;
            int lastAction=rig.mind.action;
            float[] lastSense=rig.mind.stateCopy();
            float[] next=SensorimotorMind.Senses.read(body,skin,null,objects,reserve);
            rig.mind.observe(next,DT,effect,body,skin,null,objects);
            if(rig.mind.experiences>oldExperience){
                rig.episodes.action(lastSense,lastAction);
                rig.episodes.consequenceSeparated(effect,effects,
                    p.motor,p.hand,p.membrane,false);
                java.util.Arrays.fill(effects,0);
                effect=0;
            }
            out.duration=t+DT;
        }
        out.died=out.died||reserve<=0;
        out.remaining=reserve;out.net=out.positiveEnergy-out.damage;
        out.decisions=rig.mind.decisions-before;
        out.eligible=rig.future.eligible()-beforeValid;
        out.advised=rig.mind.zero20DecisionsInfluenced();
        return out;
    }
    static double quality(Outcome e){return e.net+e.uncovered*5+e.duration*.1f-e.damage*.3f;}
    public static void main(String[] args)throws Exception{
        Profile source=load(args.length>0?args[0]:null);
        String label=args.length>0?"LATEST_SAVED_BRAIN":"VIRGIN_SANITY";
        boolean fresh=args.length>1&&"--fresh".equals(args[1]);
        if(args.length>1&&!fresh)throw new IllegalArgumentException("Unknown split");
        if(Boolean.getBoolean("zero20.longSurvivalDiagnostic")){
            if(LONG_SURVIVAL_SECONDS<=40f/DRAIN)
                throw new AssertionError("Long survival scenario does not reach natural depletion");
            System.out.printf(Locale.US,
                "ZERO20_SURVIVAL_AUDIT {\"windowSeconds\":%.1f,\"initialEnergy\":40.0,\"drainPerSecond\":%.2f,\"untouchedDepletionSeconds\":%.3f}%n",
                LONG_SURVIVAL_SECONDS,DRAIN,40f/DRAIN);
        }
        // TRAIN: only the control agent learns in first set of seed/layout
        // pairs. The shared resulting checkpoint is then cloned ONCE.
        Rig training=new Rig(source,false);
        for(int i=0;i<14;i++){
            Outcome run=episode(training,source,11+i*4,i%3);
            if(i==0||i==13)
                System.out.println("ZERO20_TRAIN "+run.json(label));
        }
        Profile shared=training.export(source);
        Rig checkA=new Rig(shared,false),checkB=new Rig(shared,true);
        if(checkA.world.trained()!=checkB.world.trained()||
           !checkA.mind.serialize().equals(checkB.mind.serialize()))
            throw new AssertionError("Train/test starting checkpoints differ");
        int pairs=0,wins=0,losses=0,ties=0,changed=0;
        float changeEnergy=0,changeTime=0,changeDamage=0;
        long eligible=0;
        // Original historical splits are development diagnostics and have
        // already been inspected. NEW split is independent and is never
        // used to tune the tested model in this same immutable run.
        for(int layout=fresh?5:3;layout<=(fresh?6:4);layout++){
            for(int seed:(fresh?new int[]{173,181,197,211,223,239}:
                                 new int[]{101,113,127,139,151,163})){
                Rig control=new Rig(shared,false);
                Rig candidate=new Rig(shared,true);
                Outcome base=episode(control,shared,seed,layout);
                Outcome active=episode(candidate,shared,seed,layout);
                if(!Float.isFinite(base.duration)||!Float.isFinite(active.duration)||
                   !Float.isFinite(base.net)||!Float.isFinite(active.net))
                    throw new AssertionError("Physics became nonfinite");
                System.out.println("ZERO20_AB "+base.json("CONTROL"));
                System.out.println("ZERO20_AB "+active.json("ACTIVE"));
                int delta=Double.compare(quality(active),quality(base));
                if(delta>0)wins++;else if(delta<0)losses++;else ties++;
                if(Math.abs(active.travel-base.travel)>.001f||
                   active.decisions!=base.decisions)changed++;
                changeEnergy+=active.net-base.net;
                changeTime+=active.duration-base.duration;
                changeDamage+=active.damage-base.damage;
                eligible+=active.eligible;
                pairs++;
            }
        }
        if(pairs!=12)throw new AssertionError("Missing matched pairs");
        if(Boolean.getBoolean("zero21.shadowCompare")&&
            (wins!=0||losses!=0||changed!=0||eligible>0))
            throw new AssertionError("Research observer changed real autonomous behavior");
        System.out.printf(Locale.US,
            "ZERO20_SUMMARY {\"profile\":\"%s\",\"split\":\"%s\",\"pairs\":%d,\"wins\":%d,"+
            "\"losses\":%d,\"ties\":%d,\"changedPaths\":%d,"+
            "\"candidateEligiblePlans\":%d,\"trainingWorldConfidence\":%.5f,"+
            "\"trainingHeldoutError\":%.6f,\"trainingUnchangedError\":%.6f,"+
            "\"trainingHeldoutSamples\":%d,\"trainedWorldActions\":%d,"+
            "\"corroboratedSkills\":%d,\"trainingPhysicalEvents\":%d,"+
            "\"linkedEnergyEvents\":%d,"+
            "\"gripTrainIntervals\":%d,\"gripHeldSnapshots\":%d,"+
            "\"gripSpreadSnapshots\":%d,\"gripObservedMoves\":%d,"+
            "\"gripHeldoutSamples\":%d,\"gripError\":%.6f,"+
            "\"gripBaselineError\":%.6f,\"gripConfidence\":%.5f,"+
            "\"microGripFrames\":%d,\"microHeldFrames\":%d,"+
            "\"microSpreadFrames\":%d,\"fleetingSpreadActions\":%d,"+
            "\"meanEnergyDelta\":%.4f,\"meanSurvivalDeltaSec\":%.4f,"+
            "\"meanDamageDelta\":%.4f,\"promotable\":false}%n",
            label,fresh?"FRESH":"HISTORICAL",pairs,wins,losses,ties,changed,eligible,training.world.confidence(),
            training.world.heldoutError(),training.world.baselineError(),
            training.world.validations(),training.world.trained(),
            training.transfer.confirmed(),training.physicalChain.physicalEvents(),
            training.physicalChain.adjacentEnergy(),
            training.gripAffordance.trained(),training.gripAffordance.realHeld(),
            training.gripAffordance.spreadHeld(),training.gripAffordance.motionEvents(),
            training.gripAffordance.validations(),training.gripAffordance.heldoutError(),
            training.gripAffordance.baselineError(),training.gripAffordance.confidence(),
            training.gripAffordance.physicsSamples(),training.gripAffordance.physicsHeldFrames(),
            training.gripAffordance.physicsSpreadFrames(),
            training.gripAffordance.fleetingSpreadIntervals(),
            changeEnergy/pairs,changeTime/pairs,changeDamage/pairs);
        // NEVER claim readiness automatically from a small heldout suite.
        System.out.println("ZERO20_ARENA FINISHED: Real bilateral covers, hidden finite sources, no oracle actions; sandbox-only evidence; no APK");
    }
}
