package com.overmind.zero;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.view.*;
import org.json.JSONObject;
import java.util.*;
import java.util.Locale;

/**
 * OVERMIND ZERO / EXPERIMENT 001
 * An invisible observer watches a randomly initialized motor brain learn
 * to move a deformable ferrofluid organism through trial and error.
 */
public final class GameActivity extends Activity {
    private Lab lab;
    // Android integration checks: the ACTUAL original physics owner (not V355 lab).
    boolean originalV356NeuralOwnerForQA(){return lab!=null&&lab.brain3!=null&&lab.useSymbiote;}
    long originalV356DecisionsForQA(){return lab==null||lab.brain3==null?-1:lab.brain3.decisions();}
    int originalV356LifeForQA(){return lab==null?-1:lab.episode;}
    void originalV356ForceDeathForQA(){if(lab!=null)lab.reserve=0f;}
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
        lab=new Lab(this);setContentView(lab);
    }
    @Override protected void onPause(){super.onPause();if(lab!=null)lab.suspend();}
    @Override protected void onResume(){super.onResume();if(lab!=null)lab.resume();}
    @Override protected void onDestroy(){
        // The ORIGINAL Activity may be recreated after a named save/rotation.
        // Free only V3's transient authority, never body, world or saved life.
        if(lab!=null&&lab.brain3!=null){lab.brain3.close();lab.brain3=null;}
        super.onDestroy();
    }
    private final class Lab extends View {
        final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        final Path path=new Path();
        final SharedPreferences prefs;
        final SharedPreferences gripPrefs;
        final int TEAL=0xff80f7e2,GOLD=0xfff7c67a,MUTED=0xff95b1bb;
        final float W=480f;
        MotorBrain.Trainer trainer;
        ContactLearning.Trainer handTrainer;
        Morphology.Trainer shapeTrainer;
        OnlineBodyLearning liveBody=new OnlineBodyLearning();
        MotionExploration journey=new MotionExploration();
        LifeStatistics lifeStats=new LifeStatistics();
        EnergyLifetimeCounters energyTotals=new EnergyLifetimeCounters();
        EpisodicMemory episodic=new EpisodicMemory();
        TemporalWorkingMemory workingMemory=new TemporalWorkingMemory();
        TemporalSkillBank temporalSkillBank=new TemporalSkillBank();
        BodyProprioception bodySchema=new BodyProprioception();
        ShelterEcology shelter=new ShelterEcology();
        DistributedBodySense distributedSense=new DistributedBodySense();
        LocalImpedanceSense localImpedance=new LocalImpedanceSense();
        FluidInteroceptionV1 innerSense=new FluidInteroceptionV1();
        ObjectExperienceV20 objectExperienceV20=new ObjectExperienceV20();
        ObjectContinuityV21 objectContinuityV21=new ObjectContinuityV21();
        CausalSequenceV23 causalSequenceV23=new CausalSequenceV23();
        CompetenceMemoryV21 competenceMemoryV21=new CompetenceMemoryV21();
        RecurrentCoreV22 recurrentCore=new RecurrentCoreV22();
        MotorDurationV22 motorDuration=new MotorDurationV22();
        SkillExecutionV22 skillExecution=new SkillExecutionV22();
        SpatialPerception spatialMap=new SpatialPerception();
        CausalExperiments causalExperiments=new CausalExperiments();
        InstrumentalNavigation navigation=new InstrumentalNavigation();
        GoalContinuity intentions=new GoalContinuity();
        GateBehaviorMetrics gateMetrics=new GateBehaviorMetrics();
        GateLifeJournal gateJournal=new GateLifeJournal();
        GripContactObserver gripObserver=new GripContactObserver();
        ContextualMotionProbe motionShadow=new ContextualMotionProbe();
        MortalityLearning mortality=new MortalityLearning();
        DecisionBlackbox decisionBlackbox=new DecisionBlackbox();
        // Passive, never read by the brain/actuators. Exact evidence about
        // blue gems, material pickup geometry, decorative trail and two-box
        // entrapment is saved independently of the learning weights.
        ContactGeometryForensics contactGeometry=new ContactGeometryForensics();
        MotorAutonomyGuard motorGuard=new MotorAutonomyGuard();
        MotorSkillBank skillBank=new MotorSkillBank();
        GripSkillBank gripBank=new GripSkillBank();
        // Read-only physical test instrumentation, scoped to the current room.
        int previousGripDrive=0,previousGripHeld=0,previousOpposedFrames=0;
        int ongoingUnsuccessfulGrip=0,boxFailedGripFrames=0;
        final java.util.WeakHashMap<UnknownWorld.Entity,float[]> boxLastPositions=new java.util.WeakHashMap<>();
        int gripRecoveryCooldown=0;
        int lastSkillStall=0;
        float environmentEffectSinceAction=0,eventEnergyDeltaSinceAction=0;
        final float[] energySinceAction=new float[UnknownWorld.SIGNATURES];
        int recalledManipulationEvents=0;
        float[] lastAppliedMotor,lastAppliedHand,lastAppliedMembrane;
        boolean techniqueUsedSinceAction=false;
        float liveStartX,liveStartY,liveStartEnergy;
        float travelSinceAction=0,forwardSinceAction=0;
        // Real box contact/valid opposing hold evidence in the current
        // decision interval, independent of the generic reward controller.
        int boxTouchFramesSinceAction=0,boxOpposedFramesSinceAction=0;
        float lifeDistance=0,totalTravel=0,bestLifeDistance=0;
        int liveStartContacts;
        int pushes=0,physicalContacts=0;
        float biggestDisplacement=0;
        float recentDisplacement=0;
        volatile float handInitial=Float.NaN,handTrained=Float.NaN;
        UnknownWorld.Mind mind=new UnknownWorld.Mind();
        SensorimotorMind sensorMind=new SensorimotorMind();
        UpdateClient updater;
        String installedVersion="0.3.7";
        String updaterStatus="";
        float updaterStatusUntil=0;
        ArrayList<UnknownWorld.Entity> environment=new ArrayList<>();
        UnknownWorld.Entity chosenObject;
        float reserve=57,goalTimer=0,worldTime=0;
        int contactsThisLife=0;
        String lastEvent="ZERO NON CONOSCE QUESTO MONDO";
        float eventTime=7;
        MotorBrain.Body creature=new MotorBrain.Body();
        Morphology.Surface surface=new Morphology.Surface(creature);
        boolean useSymbiote=false;
        SymbioteMatterV1 symMatter=null;
        SymbioteMotorHeadV1 symMotor=null;
        // Exactly one actor: the V3 neural transplant; original game remains the physics owner.
        Zero3OriginalBrainV356 brain3;
        String brain3Error="";
        // Frame-start physical positions for continuous collision detection.
        // These scratch arrays are NEVER part of ZERO's neural state/save.
        final float[] squarePrevSkinX=new float[Morphology.POINTS];
        final float[] squarePrevSkinY=new float[Morphology.POINTS];
        final float[] squarePrevBodyX=new float[MotorBrain.N];
        final float[] squarePrevBodyY=new float[MotorBrain.N];
        ScarcityLab chamber;
        boolean scarcity=false;
        float elapsedLife=0,nextChamber=70f;
        int chambersCleared=0,energyEaten=0,gripsEver=0;
        int boxesUncovered=0;
        float farthestGate=0;
        String chamberMessage="LA FAME RENDERA' PREZIOSA L'ENERGIA";
        float[] virgin;
        volatile boolean learning=true,enabled=true;
        Thread worker;
        boolean showHelp=true,resetArmed=false,simulationRunning=true,showBenchmark=false;
        boolean detailsExpanded=false,settingsExpanded=false;
        long lastFrame=0,lastSave=0;
        final PhysicsClockV171 physicsClock=new PhysicsClockV171();
        final java.util.IdentityHashMap<UnknownWorld.Entity,float[]> materialBefore=new java.util.IdentityHashMap<>();
        float H=850,clock=0,phase=0,uiTime=0,heading=0,trailClock=0;
        float[] trailX=new float[46],trailY=new float[46];
        int trailCount=0,speed=1,episode=0;
        // Separate TEST MAX mode: never part of x1/x5/x20 cycling.
        // Session-only, stopped by explicit STOP, Android pause or overheating.
        boolean testMax=false;
        long testMaxStartedNs=0,testMaxThermalCheckedNs=0;
        double testMaxSimSeconds=0;
        float testMaxActualRate=0f;
        int testMaxThermalStatus=0;
        static final float TEST_MAX_FIXED_DT=1f/60f;
        static final int TEST_MAX_MAX_STEPS=4096;
        static final long TEST_MAX_BUDGET_NS=80000000L;
        // Rendering-only light adaptation. Never persisted or fed to physics.
        final float[] cyanLeds=new float[Morphology.POINTS];
        final float[] redLeds=new float[Morphology.POINTS];
        long lastLedFrame=0;
        volatile float benchStart=Float.NaN,benchNow=Float.NaN;
        boolean benchWorking=false;
        float onScreenX=240,onScreenY=420;
        float resetNotice=0,helpNotice=0;
        final int[] darks={0xff060a13,0xff0d1823,0xff14262e,0xff06121c};

        Lab(Context c){
            super(c);setLayerType(View.LAYER_TYPE_HARDWARE,null);setFocusable(true);
            prefs=c.getSharedPreferences("zero_motorbrain_001",Context.MODE_PRIVATE);
            gripPrefs=c.getSharedPreferences(GripContactObserver.PREFS,Context.MODE_PRIVATE);
            load();
            try {
                installedVersion=GameActivity.this.getPackageManager().getPackageInfo(
                    GameActivity.this.getPackageName(),0).versionName;
            }catch(Exception ignored){}
            updater=new UpdateClient(GameActivity.this,(status)->{
                updaterStatus=status;
                updaterStatusUntil=uiTime+8.0f;
                invalidate();
            });
        }
        /** Consume reset requests only after dashboard left the foreground.
            Scoped keys are removed before reloading live runtime objects. */
        boolean consumeResetRequest(){
            String scope=prefs.getString(ResetDomains.REQUEST,"");
            if(!ResetDomains.valid(scope))return false;
            SharedPreferences.Editor edit=prefs.edit();
            if(ResetDomains.ALL.equals(scope))edit.clear();
            else for(String key:ResetDomains.keys(scope))edit.remove(key);
            edit.remove(ResetDomains.REQUEST);
            // Synchronous commit prevents a delayed save from resurrecting
            // partially cleared state after a destructive user action.
            if(!edit.commit())throw new IllegalStateException("Reset persistence failed");
            return true;
        }
        void load(){
            if(brain3!=null){brain3.close();brain3=null;}
            boxLastPositions.clear();boxFailedGripFrames=0;
            shelter=new ShelterEcology();
            java.util.Arrays.fill(cyanLeds,0f);
            java.util.Arrays.fill(redLeds,0f);
            lastLedFrame=0;
            // A passive report belongs to ONE app session, not a ZERO's
            // learned state. Preserve the previous session independently for
            // explicit upload before starting a clean observation stream.
            gripObserver=new GripContactObserver();
            String preceding=gripPrefs.getString(GripContactObserver.KEY,"");
            if(preceding.startsWith("go1|")&&preceding.length()<=60000){
                if(!gripPrefs.edit().putString(
                    GripContactObserver.PREVIOUS_SESSION,preceding)
                    .remove(GripContactObserver.KEY).commit())
                    throw new IllegalStateException("Cannot archive grip session");
            }else gripPrefs.edit().remove(GripContactObserver.KEY).apply();
            // Named slot loads/new-game atomically swap the working profile
            // while this Activity is paused. Discard any stale in-memory
            // controllers BEFORE the next frame or save.
            if(prefs.getBoolean(SaveSlotStore.PENDING_RELOAD,false) &&
               !prefs.edit().remove(SaveSlotStore.PENDING_RELOAD).commit())
                throw new IllegalStateException("Unable to apply save-slot switch");
            boolean wasReset=consumeResetRequest();
            // Old named profiles stay physically unchanged. Only the
            // explicitly created newborn profile gets fluid-body version 1.
            useSymbiote=prefs.getBoolean(SymbioteMatterV1.ENABLED,false);
            symMotor=useSymbiote?new SymbioteMotorHeadV1():null;
            if(symMotor!=null)
                {symMotor.restore(prefs.getString(SymbioteMotorHeadV1.KEY,""));
                 symMotor.restoreLocal(prefs.getString(SymbioteMotorHeadV1.LOCAL_KEY,""));
                 symMotor.restoreCoordination(prefs.getString(SymbioteCoordinationV162.KEY,""));
                 symMotor.restoreFast(prefs.getString(FastSomaticCircuitV167.KEY,""));
                 symMotor.restoreMemory(prefs.getString(SymbioteMotorSkillMemoryV169.KEY,""));
                 symMotor.restoreManipulation(prefs.getString(SymbioteMotorSkillMemoryV169.MANIPULATION_KEY,""));}
            contactGeometry=new ContactGeometryForensics();
            if(!wasReset)
                contactGeometry.restore(prefs.getString(ContactGeometryForensics.KEY,""));
            lifeStats=new LifeStatistics();
            boolean restoredLifeStats=lifeStats.restore(prefs.getString(LifeStatistics.KEY,""));
            energyTotals=new EnergyLifetimeCounters();
            if(!energyTotals.restore(prefs.getString(EnergyLifetimeCounters.KEY,"")))
                energyTotals.migrate(lifeStats);
            String initial=prefs.getString("virgin","");
            virgin=decode(initial);
            if(virgin==null){virgin=MotorBrain.virgin(new Random(System.nanoTime()));}
            float[] trained=decode(prefs.getString("best",""));
            if(trained==null)trained=MotorBrain.copy(virgin);
            trainer=new MotorBrain.Trainer(trained,21947L+prefs.getInt("attempts",0));
            trainer.attempts=prefs.getInt("attempts",0);
            trainer.accepted=prefs.getInt("accepted",0);
            trainer.initialFitness=prefs.getFloat("original",MotorBrain.fitness(virgin));
            float[] savedHand=decodeHand(prefs.getString("hand_brain_v04",""));
            handTrainer=new ContactLearning.Trainer(savedHand);
            handTrainer.attempts=prefs.getInt("hand_attempts_v04",0);
            handTrainer.accepted=prefs.getInt("hand_successes_v04",0);
            shapeTrainer=new Morphology.Trainer(decodeShape(prefs.getString("morph_brain_v07","")));
            shapeTrainer.attempts=prefs.getInt("morph_attempts_v07",0);
            shapeTrainer.accepted=prefs.getInt("morph_accepted_v07",0);
            shapeTrainer.gateAttempts=prefs.getInt("morph_gate_attempts_v08",0);
            shapeTrainer.gateAccepted=prefs.getInt("morph_gate_success_v08",0);
            // Do not reuse v0.4's 'physical_pushes' counter: it counted
            // collisions, not confirmed object displacement.
            pushes=prefs.getInt("objects_displaced_v041",0);
            physicalContacts=prefs.getInt("collisions_v041",0);
            biggestDisplacement=prefs.getFloat("max_displacement_v041",0);
            mind=new UnknownWorld.Mind();
            mind.deserialize(prefs.getString("world_knowledge_v03",""));
            sensorMind=new SensorimotorMind();
            sensorMind.restore(prefs.getString("sensorimotor_model_v09",""));
            sensorMind.restoreContactCounts(
                prefs.getInt("real_energy_credits_v098",0),
                prefs.getInt("real_damage_credits_v098",0),
                prefs.getInt("real_action_credits_v098",0));
            sensorMind.restoreOutcomeModel(prefs.getString("outcome_credit_v101",""));
            sensorMind.restorePlanningStats(prefs.getString("learned_planner_stats_v102",""));
            sensorMind.diagnostics.restore(
                prefs.getString("sensorimotor_diagnostics_v091",""));
            sensorMind.activeDiagnostics.restore(prefs.getString(LearningDiagnostics.ACTIVE_KEY,""));
            workingMemory=new TemporalWorkingMemory();
            workingMemory.restore(prefs.getString("temporal_working_memory_v106",""));
            sensorMind.attachWorkingMemory(workingMemory);
            temporalSkillBank=new TemporalSkillBank();
            temporalSkillBank.restore(prefs.getString("temporal_skills_v107",""));
            sensorMind.attachTemporalSkills(temporalSkillBank);
            bodySchema=new BodyProprioception();
            bodySchema.restore(prefs.getString(BodyProprioception.KEY,""));
            sensorMind.attachBodySchema(bodySchema);
            distributedSense=new DistributedBodySense();
            distributedSense.restore(prefs.getString(DistributedBodySense.KEY,""));
            sensorMind.attachDistributedBody(distributedSense);
            localImpedance=new LocalImpedanceSense();
            localImpedance.restore(prefs.getString(LocalImpedanceSense.KEY,""));
            sensorMind.attachImpedance(localImpedance);
            innerSense=new FluidInteroceptionV1();
            innerSense.restore(prefs.getString(FluidInteroceptionV1.KEY,""));
            sensorMind.attachFluidInteroception(innerSense,null);
            objectExperienceV20=new ObjectExperienceV20();
            objectExperienceV20.restore(prefs.getString(ObjectExperienceV20.KEY,""));
            sensorMind.attachObjectExperienceV20(objectExperienceV20);
            // Independent optional working-profile state; never reuses old ZERO slots.
            objectContinuityV21=new ObjectContinuityV21();
            objectContinuityV21.restore(prefs.getString(ObjectContinuityV21.KEY,""));
            sensorMind.attachObjectContinuityV21(objectContinuityV21);
            causalSequenceV23=new CausalSequenceV23();
            causalSequenceV23.restore(prefs.getString(CausalSequenceV23.KEY,""));
            sensorMind.attachCausalSequenceV23(causalSequenceV23);
            competenceMemoryV21=new CompetenceMemoryV21();
            competenceMemoryV21.restore(prefs.getString(CompetenceMemoryV21.KEY,""));
            recurrentCore=new RecurrentCoreV22();
            recurrentCore.restore(prefs.getString(RecurrentCoreV22.KEY,""));
            motorDuration=new MotorDurationV22();
            motorDuration.restore(prefs.getString(MotorDurationV22.KEY,""));
            sensorMind.attachActiveCore(recurrentCore);
            sensorMind.attachMotorDuration(motorDuration);
            sensorMind.attachSymbioteMotorHead(symMotor);
            // Android exclusively uses RecurrentCoreV22; historical 2.0
            // future/transfer and 2.1 shadow arbiters no longer run or persist.
            sensorMind.enableZero20ExperimentalAdvice(false);
            spatialMap=new SpatialPerception();
            spatialMap.restore(prefs.getString(SpatialPerception.KEY,""));
            sensorMind.attachSpatialPerception(spatialMap);
            causalExperiments=new CausalExperiments();
            causalExperiments.restore(prefs.getString(CausalExperiments.KEY,""));
            sensorMind.attachCausalExperiments(causalExperiments);
            navigation=new InstrumentalNavigation();
            navigation.restore(prefs.getString(InstrumentalNavigation.KEY,""));
            navigation.restoreContextual(prefs.getString(ContextualNavigationV172.KEY,""));
            if(symMotor!=null)symMotor.responseJournal().restore(prefs.getString(MotorResponseJournalV172.KEY,""));
            sensorMind.attachNavigation(navigation);
            intentions=new GoalContinuity();
            intentions.restore(prefs.getString(GoalContinuity.KEY,""));
            sensorMind.attachGoalContinuity(intentions);
            // Read-only five-minute flight recorder, isolated per working ZERO.
            decisionBlackbox=new DecisionBlackbox();
            decisionBlackbox.restoreLast(prefs.getString(DecisionBlackbox.LAST,""));
            decisionBlackbox.restoreCurrent(prefs.getString(DecisionBlackbox.CURRENT,""));
            sensorMind.attachDecisionBlackbox(decisionBlackbox);
            motionShadow=new ContextualMotionProbe();
            motionShadow.restore(prefs.getString(ContextualMotionProbe.KEY,""));
            sensorMind.attachMotionShadow(motionShadow);
            mortality=new MortalityLearning();
            mortality.restore(prefs.getString(MortalityLearning.KEY,""));
            sensorMind.attachMortalityLearning(mortality);
            gateJournal=new GateLifeJournal();
            gateJournal.restoreLast(prefs.getString(GateLifeJournal.LAST,""));
            gateJournal.restoreCurrent(prefs.getString(GateLifeJournal.CURRENT,""));
            gateMetrics=new GateBehaviorMetrics();
            gateMetrics.restore(prefs.getString(GateBehaviorMetrics.KEY,""));
            episodic=new EpisodicMemory();
            episodic.restore(prefs.getString("episodic_memory_v095",""));
            if(!useSymbiote)sensorMind.attachMemory(episodic);
            motorGuard=new MotorAutonomyGuard();
            motorGuard.restoreCounts(prefs.getInt("motor_recoveries_v097",0),
                prefs.getInt("motor_free_choices_v097",0));
            sensorMind.attachMotorGuard(motorGuard);
            skillBank=new MotorSkillBank();
            skillBank.restore(prefs.getString("motor_skills_v098",""));
            gripBank=new GripSkillBank();
            gripBank.restore(prefs.getString("grip_skills_v099",""));
            lastSkillStall=motorGuard.recoveryActivations;
            recalledManipulationEvents=prefs.getInt("physical_replays_v097",0);
            reserve=prefs.getFloat("zero_energy_v03",57f);
            totalTravel=prefs.getFloat("lived_distance_total_v093",0);
            bestLifeDistance=prefs.getFloat("lived_distance_record_v093",0);
            elapsedLife=prefs.getFloat("scarcity_elapsed_v08",0);
            scarcity=prefs.getBoolean("scarcity_active_v08",false);
            chambersCleared=prefs.getInt("room_success_v08",0);
            energyEaten=prefs.getInt("finite_energy_eaten_v08",0);
            boxesUncovered=prefs.getInt("square_uncovered_v1163",0);
            gripsEver=prefs.getInt("grip_events_v08",0);
            farthestGate=prefs.getFloat("gate_travel_v08",0);
            liveBody=new OnlineBodyLearning();
            for(int kind=0;kind<3;kind++){
                String k=kind==0?"motor":kind==1?"hand":"membrane";
                liveBody.restoreCounts(kind,prefs.getInt("online_accepted_"+k+"_v094",0),
                    prefs.getInt("online_rejected_"+k+"_v094",0));
            }
            journey=new MotionExploration();
            journey.restore(prefs.getInt("explored_cells_v094",0),
                prefs.getFloat("record_net_v094",0),
                prefs.getFloat("energy_spent_v094",0));
            speed=1;learning=true;simulationRunning=true;
            // A fresh install/explicit reset has no saved world. Regular
            // restarts and named slot loads rebuild exactly the stored scene.
            if(wasReset||!restoreScene(prefs.getString("physical_scene_v100",""))){
                resetEpisode();
                prefs.edit().remove("physical_scene_v100").apply();
            }else if(!restoredLifeStats){
                // Pre-0.10.4 scene: do not pretend we measured the full life.
                // The next real death starts the first complete statistics sample.
                lifeStats.markPartial();
            }
            save();
            try{
                if(!useSymbiote||symMatter==null)throw new java.io.IOException(
                    "Profilo originale privo del simbionte: nessuna conversione automatica");
                brain3=Zero3OriginalBrainV356.load(GameActivity.this,
                    prefs.getString(Zero3OriginalBrainV356.KEY,""));
                brain3Error="";
                sensorMind.setLearning(false);
                if(symMotor!=null)symMotor.setAdaptationEnabled(false);
            }catch(Exception failure){
                simulationRunning=false;
                brain3Error="ZERO 3.0: "+failure.getMessage();
            }
        }
        float[] decode(String s){
            if(s==null||s.isEmpty())return null;
            String[] chunks=s.split(",");
            if(chunks.length!=MotorBrain.PARAMS)return null;
            float[] out=new float[chunks.length];
            try{
                for(int i=0;i<out.length;i++){
                    out[i]=Float.parseFloat(chunks[i]);
                    if(!Float.isFinite(out[i]))return null;
                }
            }catch(Exception e){return null;}
            return out;
        }
        float[] decodeHand(String s){
            if(s==null||s.length()==0)return null;
            String[] chunks=s.split(",");
            if(chunks.length!=ContactLearning.PARAMS)return null;
            float[] w=new float[chunks.length];
            try{
                for(int i=0;i<w.length;i++){
                    w[i]=Float.parseFloat(chunks[i]);
                    if(!Float.isFinite(w[i]))return null;
                }
                return w;
            }catch(Exception ex){return null;}
        }
        float[] decodeShape(String s){
            if(s==null||s.length()==0)return null;
            String[] arr=s.split(",");
            if(arr.length!=Morphology.PARAMS&&arr.length!=Morphology.LEGACY_PARAMS)
                return null;
            float[] out=new float[arr.length];
            try {
                for(int i=0;i<out.length;i++){
                    out[i]=Float.parseFloat(arr[i]);
                    if(!Float.isFinite(out[i])||Math.abs(out[i])>5)return null;
                }
                return out;
            }catch(Exception e){return null;}
        }
        String encode(float[] values){
            StringBuilder b=new StringBuilder();
            for(int i=0;i<values.length;i++){
                if(i>0)b.append(',');
                b.append(values[i]);
            }
            return b.toString();
        }
        /** Physical checkpoint for a true saved game, not merely a brain
         * export. Named save slots copy this AFTER onPause has flushed it. */
        void saveScene(){
            if(creature==null||surface==null||environment==null)return;
            try{
                JSONObject snapshot=new JSONObject();
                snapshot.put("v",1);
                snapshot.put("physical",new JSONObject(
                    GameSceneSnapshot.capture(creature,surface,environment,chamber,
                        useSymbiote?symMatter:null)));
                snapshot.put("episode",episode);
                snapshot.put("speed",speed);
                snapshot.put("running",simulationRunning);
                snapshot.put("phase",phase);
                snapshot.put("elapsedLife",elapsedLife);
                snapshot.put("nextChamber",nextChamber);
                snapshot.put("clock",clock);
                snapshot.put("worldTime",worldTime);
                snapshot.put("lifeDistance",lifeDistance);
                snapshot.put("travelSinceAction",travelSinceAction);
                snapshot.put("forwardSinceAction",forwardSinceAction);
                snapshot.put("heading",heading);
                snapshot.put("contactsThisLife",contactsThisLife);
                snapshot.put("scarcity",scarcity);
                snapshot.put("journey",journey.saveLife());
                snapshot.put("currentAction",sensorMind.action);
                snapshot.put("liveStartX",liveStartX);
                snapshot.put("liveStartY",liveStartY);
                snapshot.put("liveStartEnergy",liveStartEnergy);
                snapshot.put("liveStartContacts",liveStartContacts);
                snapshot.put("previousGripDrive",previousGripDrive);
                snapshot.put("previousGripHeld",previousGripHeld);
                snapshot.put("previousOpposedFrames",previousOpposedFrames);
                snapshot.put("chamberMessage",chamberMessage);
                snapshot.put("lastEvent",lastEvent);
                snapshot.put("eventTime",eventTime);
                shelter.capture(environment);
                snapshot.put(ShelterEcology.SNAPSHOT,shelter.encode());
                // Original scene bytes + completed V3 neural weights in ONE
                // old working profile. Named saves copy the unchanged profile map.
                android.content.SharedPreferences.Editor sceneEditor=prefs.edit()
                    .putString("physical_scene_v100",snapshot.toString())
                    .putString(DecisionBlackbox.CURRENT,decisionBlackbox.encodeCurrent())
                    .putString(GateLifeJournal.CURRENT,gateJournal.encode())
                    .putString(ContactGeometryForensics.KEY,contactGeometry.encode());
                if(brain3!=null){
                    brain3.interrupt(); // Partial actions earn no fake reward.
                    sceneEditor.putString(Zero3OriginalBrainV356.KEY,brain3.checkpoint());
                }
                if(!sceneEditor.commit())
                    throw new IllegalStateException("Impossibile salvare la scena fisica");
                saveGripObserver();
            }catch(Exception e){
                throw new IllegalStateException("Non riesco a salvare posizione e stanza",e);
            }
        }
        /** Decode temporary objects FIRST; never partially install a
         * damaged scene or mix it with weights from another save slot. */
        boolean restoreScene(String raw){
            if(raw==null||raw.length()<100||raw.length()>2400000)return false;
            try{
                JSONObject o=new JSONObject(raw);
                if(o.getInt("v")!=1)return false;
                GameSceneSnapshot.Scene scene=GameSceneSnapshot.restore(
                    o.getJSONObject("physical").toString());
                if(scene==null||(useSymbiote!=(scene.matter!=null)))return false;
                ShelterEcology decodedShelter=new ShelterEcology();
                if(o.has(ShelterEcology.SNAPSHOT)&&
                   !decodedShelter.restore(o.getString(ShelterEcology.SNAPSHOT)))return false;
                int savedEpisode=o.getInt("episode");
                int action=o.getInt("currentAction");
                float savedPhase=(float)o.getDouble("phase");
                float savedElapsed=(float)o.getDouble("elapsedLife");
                float savedNext=(float)o.getDouble("nextChamber");
                if(savedEpisode<0||action<0||action>=SensorimotorMind.ACTIONS||
                   !Float.isFinite(savedPhase)||!Float.isFinite(savedElapsed)||
                   !Float.isFinite(savedNext)||savedPhase<0||savedPhase>14)return false;
                // Only after the complete physical structure was verified.
                creature=scene.body;surface=scene.skin;
                symMatter=scene.matter;
                sensorMind.setActualFluid(symMatter);
                chamber=scene.room;environment=scene.objects;
                shelter=decodedShelter;
                episode=savedEpisode;phase=savedPhase;
                speed=o.optInt("speed",1);
                if(speed!=1&&speed!=5&&speed!=20)speed=1;
                simulationRunning=o.optBoolean("running",true);
                elapsedLife=savedElapsed;nextChamber=savedNext;
                clock=(float)o.getDouble("clock");
                worldTime=(float)o.getDouble("worldTime");
                lifeDistance=(float)o.getDouble("lifeDistance");
                travelSinceAction=(float)o.getDouble("travelSinceAction");
                forwardSinceAction=(float)o.getDouble("forwardSinceAction");
                heading=(float)o.getDouble("heading");
                contactsThisLife=o.getInt("contactsThisLife");
                scarcity=o.getBoolean("scarcity");
                chamberMessage=o.getString("chamberMessage");
                lastEvent=o.getString("lastEvent");
                eventTime=(float)o.getDouble("eventTime");
                liveStartX=(float)o.getDouble("liveStartX");
                liveStartY=(float)o.getDouble("liveStartY");
                liveStartEnergy=(float)o.getDouble("liveStartEnergy");
                liveStartContacts=o.getInt("liveStartContacts");
                previousGripDrive=o.getInt("previousGripDrive");
                previousGripHeld=o.getInt("previousGripHeld");
                previousOpposedFrames=o.getInt("previousOpposedFrames");
                journey.loadLife(o.getString("journey"));
                if(useSymbiote&&symMatter!=null)
                            symMatter.observeBody(creature,surface);
                        else MaterialGeometry.observe(creature);
                sensorMind.resumeEpisode(SensorimotorMind.Senses.read(
                    creature,surface,chamber,environment,reserve),action);
                sensorMind.updateBodySense(creature,surface);
                sensorMind.updateDistributedSense(creature,surface,chamber,environment);
                sensorMind.updateImpedanceSense(creature,surface);
                sensorMind.updateInnerSense(creature,surface);
                sensorMind.resumeSpatialSense(creature,surface,chamber,environment);
                sensorMind.updateNavigationSense(creature,surface,chamber,environment);
                // No synthetic reward crosses process death; instead choose
                // a NEW real action on restored physical measurements BEFORE
                // the first resumed physics substep.
                if(useSymbiote&&symMotor!=null)
                    sensorMind.rearmAfterResume(creature,surface);
                motorGuard.resetLife(creature.cx,creature.cy);
                lastSkillStall=motorGuard.recoveryActivations;
                liveBody.abortTrial();
                trailCount=0;trailClock=0;
                // The room/current body's coordinates remain untouched.
                return true;
            }catch(Exception invalid){return false;}
        }

        void saveGripObserver(){
            gripPrefs.edit().putString(GripContactObserver.KEY,
                gripObserver.encode()).apply();
        }
        void save(){
            if(trainer==null)return;
            prefs.edit().putString("virgin",encode(virgin))
              .putString("best",encode(trainer.best))
              .putString("motor_skills_v098",skillBank.encode())
              .putString("grip_skills_v099",gripBank.encode())
              .putInt("real_energy_credits_v098",sensorMind.positiveContactCredits())
              .putInt("real_damage_credits_v098",sensorMind.negativeContactCredits())
              .putInt("real_action_credits_v098",sensorMind.historicalActionsCredited())
              .putString("outcome_credit_v101",sensorMind.outcomeModel())
              .putString("learned_planner_stats_v102",sensorMind.planningStats())
              .putInt("attempts",trainer.attempts)
              .putInt("accepted",trainer.accepted)
              .putFloat("original",trainer.initialFitness)
              .putString("world_knowledge_v03",mind.serialize())
              .putFloat("zero_energy_v03",reserve)
              .putFloat("lived_distance_total_v093",totalTravel)
              .putFloat("lived_distance_record_v093",bestLifeDistance)
              .putInt("explored_cells_v094",journey.totalCells)
              .putFloat("record_net_v094",journey.recordNet)
              .putFloat("energy_spent_v094",journey.totalEnergySpent)
              .putInt("online_accepted_motor_v094",liveBody.accepted(0))
              .putInt("online_rejected_motor_v094",liveBody.rejected(0))
              .putInt("online_accepted_hand_v094",liveBody.accepted(1))
              .putInt("online_rejected_hand_v094",liveBody.rejected(1))
              .putInt("online_accepted_membrane_v094",liveBody.accepted(2))
              .putInt("online_rejected_membrane_v094",liveBody.rejected(2))
              .putString("hand_brain_v04",encode(handTrainer.best))
              .putInt("hand_attempts_v04",handTrainer.attempts)
              .putInt("hand_successes_v04",handTrainer.accepted)
              .putInt("objects_displaced_v041",pushes)
              .putInt("collisions_v041",physicalContacts)
              .putFloat("max_displacement_v041",biggestDisplacement)
              .putString("sensorimotor_model_v09",sensorMind.serialize())
              .putString("sensorimotor_diagnostics_v091",sensorMind.diagnostics.encode())
              .putString(LearningDiagnostics.ACTIVE_KEY,sensorMind.activeDiagnostics.encode())
              .putString("episodic_memory_v095",episodic.encode())
              // 0.10.8: persist learned temporal readouts, hidden context and
              // consolidated action sequences alongside the existing brain.
              // Same working profile is captured atomically by the 12 named slots.
              .putString("temporal_working_memory_v106",workingMemory.encode())
              .putString("temporal_skills_v107",temporalSkillBank.encode())
              .putString(BodyProprioception.KEY,bodySchema.encode())
              .putString(DistributedBodySense.KEY,distributedSense.encode())
              .putString(LocalImpedanceSense.KEY,localImpedance.encode())
              .putString(FluidInteroceptionV1.KEY,innerSense.encode())
              // ZERO 2.0 shadow brain: optional independent persistence; no legacy changes.
              .putString(ObjectExperienceV20.KEY,objectExperienceV20.encode())
              .putString(ObjectContinuityV21.KEY,objectContinuityV21.encode())
              .putString(CausalSequenceV23.KEY,causalSequenceV23.encode())
              .putString(CompetenceMemoryV21.KEY,competenceMemoryV21.encode())
              .putString(RecurrentCoreV22.KEY,recurrentCore.encode())
               .putString(MotorDurationV22.KEY,motorDuration.encode())
              .putString(SymbioteMotorHeadV1.KEY,
                  symMotor==null?"":symMotor.encode())
              .putString(SymbioteMotorHeadV1.LOCAL_KEY,
                  symMotor==null?"":symMotor.encodeLocal())
              .putString(SymbioteCoordinationV162.KEY,
                  symMotor==null?"":symMotor.encodeCoordination())
              .putString(FastSomaticCircuitV167.KEY,
                  symMotor==null?"":symMotor.encodeFast())
              .putString(SymbioteMotorSkillMemoryV169.KEY,
                  symMotor==null?"":symMotor.encodeMemory())
              .putString(SymbioteMotorSkillMemoryV169.MANIPULATION_KEY,
                  symMotor==null?"":symMotor.encodeManipulation())
              .putString(SpatialPerception.KEY,spatialMap.encode())
              .putString(CausalExperiments.KEY,causalExperiments.encode())
              .putString(InstrumentalNavigation.KEY,navigation.encode())
              .putString(ContextualNavigationV172.KEY,navigation.encodeContextual())
              .putString(MotorResponseJournalV172.KEY,symMotor==null?"":symMotor.responseJournal().encode())
              .putString(GoalContinuity.KEY,intentions.encode())
              .putString(ContactGeometryForensics.KEY,contactGeometry.encode())
              .putString(GateBehaviorMetrics.KEY,gateMetrics.encode())
              .putString(ContextualMotionProbe.KEY,motionShadow.encode())
              .putString(MortalityLearning.KEY,mortality.encode())
              .putString(GateLifeJournal.CURRENT,gateJournal.encode())
              .putString(LifeStatistics.KEY,lifeStats.encode())
              .putString(EnergyLifetimeCounters.KEY,energyTotals.encode())
              .putInt("motor_recoveries_v097",motorGuard.recoveryActivations)
              .putInt("motor_free_choices_v097",motorGuard.freeChoices)
              .putInt("physical_replays_v097",recalledManipulationEvents)
              .putString("morph_brain_v07",encode(shapeTrainer.best))
              .putInt("morph_attempts_v07",shapeTrainer.attempts)
              .putInt("morph_accepted_v07",shapeTrainer.accepted)
              .putInt("morph_gate_attempts_v08",shapeTrainer.gateAttempts)
              .putInt("morph_gate_success_v08",shapeTrainer.gateAccepted)
              .putBoolean("scarcity_active_v08",scarcity)
              .putFloat("scarcity_elapsed_v08",elapsedLife)
              .putInt("room_success_v08",chambersCleared)
              .putInt("finite_energy_eaten_v08",energyEaten)
              .putInt("square_uncovered_v1163",boxesUncovered)
              .putInt("grip_events_v08",gripsEver+(chamber!=null?chamber.grasps:0))
              .putFloat("gate_travel_v08",Math.max(farthestGate,
                  chamber!=null?chamber.maxDoorTravel:0))
              .apply();
        }
        void resume(){
            // A reset requested from CERVELLO/ANALISI must replace objects in
            // memory before the next frame can save old weights again.
            if(prefs.contains(ResetDomains.REQUEST)||
               prefs.getBoolean(SaveSlotStore.PENDING_RELOAD,false))load();
            learning=true;
            enabled=true;lastFrame=0;physicsClock.reset();postInvalidateOnAnimation();
            // All adaptation happens in the visible world. Inherited motor
            // and membrane weights persist until a lived test is accepted.
            sensorMind.setLearning(brain3==null);
        }
        void suspend(){
            // Test speed is deliberately NOT persisted or resumed blindly.
            testMax=false;
            // Pause and FLUSH BOTH cognition and physical state before the
            // named-slot screen is allowed to inspect the working profile.
            enabled=false;physicsClock.reset();
            if(brain3!=null)brain3.interrupt();
            save();
            saveScene();
            lastFrame=0;
        }
        void resetEpisode(){
            // The original instant reincarnation remains authoritative.
            // Only recurrent activations reset, V3 neural weights are retained.
            if(brain3!=null)brain3.newLife();
            techniqueUsedSinceAction=false;
            shelter.capture(environment);
            shelter.newLife();
            gripObserver.newLife();
            contactGeometry.newLife();
            // Only initial boot or actual simulated death restarts the body.
            bestLifeDistance=Math.max(bestLifeDistance,lifeDistance);
            lifeDistance=0;travelSinceAction=0;forwardSinceAction=0;
            boxTouchFramesSinceAction=boxOpposedFramesSinceAction=0;
            environmentEffectSinceAction=0;eventEnergyDeltaSinceAction=0;
            java.util.Arrays.fill(energySinceAction,0f);
            episodic.resetLife(); // Forget transient attempt, NOT learned motor or memories.
            mortality.newLife(); // Keep learned terminal weights and tested evidence.
            previousGripDrive=previousGripHeld=previousOpposedFrames=0;
            gateMetrics.resetLife();
            gateJournal.newLife(); // Retain previous life snapshot, start clean.
            ongoingUnsuccessfulGrip=0;boxFailedGripFrames=0;gripRecoveryCooldown=0;
            boxLastPositions.clear();
            liveBody.abortTrial();
            creature.reset();surface.reset(creature);
            if(useSymbiote){
                symMatter=new SymbioteMatterV1();
                symMatter.reset(creature,surface);
                sensorMind.setActualFluid(symMatter);
            }else{
                symMatter=null;
                sensorMind.setActualFluid(null);
            }
            java.util.Arrays.fill(cyanLeds,0f);
            java.util.Arrays.fill(redLeds,0f);
            motorGuard.resetLife(creature.cx,creature.cy);
            lastSkillStall=motorGuard.recoveryActivations;
            journey.newLife(creature.cx,creature.cy);
            episode++;phase=0;trailCount=0;clock=0;
            chamber=null;
            // Existing placed materials have priority over newly generated props.
            environment=new ArrayList<UnknownWorld.Entity>();
            shelter.rehydrate(environment,creature.cx,creature.cy,null);
            environment.addAll(WorldPlacement.spawn(episode,creature.cx,creature.cy,
                scarcity,null,environment,surface));
            chosenObject=null;
            goalTimer=0;contactsThisLife=0;worldTime=0;
            reserve=Math.max(70,Math.min(92,reserve));
            liveStartX=creature.cx;liveStartY=creature.cy;
            liveStartEnergy=reserve;liveStartContacts=physicalContacts;
            if(brain3==null)sensorMind.resetEpisode(SensorimotorMind.Senses.read(
                creature,surface,chamber,environment,reserve));
            sensorMind.updateBodySense(creature,surface);
            sensorMind.updateDistributedSense(creature,surface,chamber,environment);
                sensorMind.updateImpedanceSense(creature,surface);
                sensorMind.updateInnerSense(creature,surface);
            // The newborn sees from actual membrane sites, never a centroid eye.
            sensorMind.updateSpatialSense(creature,surface,chamber,environment);
            sensorMind.updateNavigationSense(creature,surface,chamber,environment);
            heading=brain3!=null?(float)Math.atan2(brain3.directionY(),brain3.directionX()):
                (float)Math.atan2(sensorMind.motorY(),sensorMind.motorX());
        }
        void extendHabitat(){
            shelter.capture(environment);
            // Keep the same physical body, velocity, memories and camera.
            // Place additional objects outside the immediate viewport.
            episode++;phase=0;
            // A living ZERO's real contact must not be erased by the
            // periodic 13-second world-streaming pass. A still-concealed
            // source stays paired with its physical cover.
            HabitatContinuity.pruneDistant(environment,creature,surface,
                chamber==null?null:chamber.food,600f);
            // Revisited player-placed materials preclude overlapping
            // procedural spawns, never the other way around.
            shelter.rehydrate(environment,creature.cx,creature.cy,chamber);
            environment.addAll(WorldPlacement.spawn(
                episode,creature.cx,creature.cy,scarcity,chamber,environment,surface));
            excludeOrdinaryRoomObjects();
            // Oldest unengaged objects are recycled first; a touched or
            // currently gripped square never vanishes merely due to cap 24.
            HabitatContinuity.enforceLimit(environment,creature,surface,
                chamber==null?null:chamber.food);
            if(chosenObject!=null&&!environment.contains(chosenObject)){
                chosenObject=null;goalTimer=0;
            }
            lastEvent="NUOVA AREA ESPLORATA  •  ZERO NON SI AZZERA";
            eventTime=3;
        }
        void excludeOrdinaryRoomObjects(){
            HabitatContinuity.excludeRoomOverlap(
                environment,chamber,creature,surface);
        }
        void activateChamber(boolean manual){
            // Do not materialize a room over an existing box or stimulus:
            // choose a physically vacant nearby origin (no deletion).
            ScarcityLab next=HabitatContinuity.clearChamber(
                creature.cx,creature.cy,environment);
            if(next==null){
                nextChamber=elapsedLife+8f;
                lastEvent="STANZA RINVIATA • SPAZIO OCCUPATO";
                eventTime=2f;
                return;
            }
            if(chamber!=null){
                gripsEver+=chamber.grasps;
                farthestGate=Math.max(farthestGate,chamber.maxDoorTravel);
                environment.remove(chamber.food);
            }
            scarcity=true;
            // Test the door without a forced energy penalty.
            chamber=next;
            previousGripDrive=previousGripHeld=previousOpposedFrames=0;
            gateMetrics.resetLife();
            ongoingUnsuccessfulGrip=0;gripRecoveryCooldown=0;
            excludeOrdinaryRoomObjects();
            environment.add(chamber.food);
            nextChamber=elapsedLife+150f;
            chosenObject=null;goalTimer=0;
            chamberMessage="STIMOLI VISIVI • OSTACOLO FISICO";
            lastEvent="NUOVO ESPERIMENTO • STANZA E BLOCCO";
            eventTime=5;
            save();
        }
        void updateHabitat(float dt){
            worldTime+=dt;elapsedLife+=dt;
            lifeStats.tick(dt); // physics time, multiplied naturally by x1/x5/x20
            if(!scarcity&&elapsedLife>=70f)scarcity=true;
            // Physiological requirement is part of the organism, NOT a
            // scripted plan for solving the current room.
            float energyBeforeDrain=reserve;
            reserve=Math.max(0,reserve-(scarcity?.30f:.09f)*dt);
            mortality.energyFlows(dt,energyBeforeDrain-reserve,0,0);
            if(scarcity&&elapsedLife>=nextChamber&&
                (chamber==null||chamber.reachedEnergy)&&
                // A completed room stays physical while ZERO or even one
                // piece of its deformable membrane remains inside. It may
                // be recycled only after real exit, never on timer alone.
                !HabitatContinuity.roomOccupied(chamber,creature,surface))
                activateChamber(false);
            for(UnknownWorld.Entity object:environment){
                float ox=object.x,oy=object.y;
                object.tick(dt);
                // A free box collides with the current room instead of
                // tunnelling inside its gate. Roll back THIS frame only.
                if(object.isBox()&&chamber!=null&&
                   chamber.overlapsRoom(object.x,object.y,object.radius)){
                    object.x=ox;object.y=oy;object.vx=object.vy=0;
                }
            }
            // Real AABB barrier/barrier contacts. No displacement bonus.
            ShelterEcology.separateBoxes(environment);
            // Scarcity must not spawn new entities every rendered frame.
            if(environment.size()<3&&phase>=4f)extendHabitat();
            excludeOrdinaryRoomObjects();
            // No object selector or precomputed destination heading.
            chosenObject=null;
        }
        void resolveObjects(float dt){
            // Called AFTER the deformable body moves, so contact impulses
            // are consequences of its simulated physical movement.
            for(UnknownWorld.Entity object:environment){
                if(object.isBox()||object.concealed)continue;
                if(chamber!=null&&object==chamber.food)continue;
                if(object.isMovable()){
                    float impulse=Morphology.collide(surface,object,dt)
                        +ContactLearning.collide(creature,object,dt);
                    if(impulse>.65f){
                        object.everCollided=true;
                        if(object.contactCooldown<=0){
                            physicalContacts++;
                            contactsThisLife++;
                            object.contactCooldown=.75f;
                            mind.observe(object.signature,0,object.displacementEffect());
                        }
                    }
                    // Only report a success when an already-contacted object
                    // has demonstrably changed world position by >= 12 units.
                    // A mere collision or velocity spike does NOT qualify.
                    float shift=object.displacementFromSpawn();
                    object.peakDisplacement=Math.max(object.peakDisplacement,shift);
                    if(object.everCollided && !object.countedAsMoved && shift>=12f){
                        object.countedAsMoved=true;
                        // A physically verified change in an external object,
                        // regardless of object identity, can strengthen history.
                        environmentEffectSinceAction+=Math.min(18,shift);
                        pushes++;
                        lifeStats.diskMoved();
                        if(chosenObject==object){chosenObject=null;goalTimer=0;}
                        biggestDisplacement=Math.max(biggestDisplacement,shift);
                        recentDisplacement=shift;
                        lastEvent="DISCO SPOSTATO "+(int)shift+" UNITA  •  TOTALE "+pushes;
                        eventTime=4f;save();
                    }
                    if(object.everCollided && shift>biggestDisplacement+1f){
                        biggestDisplacement=shift;
                    }
                    continue;
                }
                if(!object.ready())continue;
                float dx=object.x-creature.cx,dy=object.y-creature.cy;
                float dist=(float)Math.hypot(dx,dy);
                // The original +18-unit fallback made ZERO collect a gem
                // while NONE of his material was touching it (OTA127 proved
                // material=0, source=extra18). Use actual material only;
                // no phantom centroid or decorative trail collider.
                if(MaterialGeometry.touches(creature,surface,object.x,object.y,
                        object.radius+2f)){
                    float before=reserve;
                    // Snapshot photons BEFORE a source is consumed. This is
                    // exactly what ZERO could sense at contact, not object law.
                    float[] sightAtTouch=SensorimotorMind.Senses.read(
                        creature,surface,chamber,environment,reserve);
                    float delta=object.touch(dx/Math.max(1,dist),dy/Math.max(1,dist));
                    reserve=Math.max(0,Math.min(100,reserve+delta));
                    lifeStats.contact(reserve-before); // actual clamped contact outcome
                    energyTotals.record(reserve-before,object.isRare());
                    mortality.energyFlows(0,0,Math.max(0,before-reserve),
                        Math.max(0,reserve-before));
                    // Only actual physical contact, never passive hunger,
                    // determines positive/negative autobiographical valence.
                    eventEnergyDeltaSinceAction+=reserve-before;
                    // Actual feedback trains the self-selected motor-action
                    // history; no color, target angle or food flag passed.
                    if(brain3==null)sensorMind.contactOutcome(reserve-before,object.signature,sightAtTouch);
                    energySinceAction[object.signature]+=reserve-before;
                    mind.observe(object.signature,delta,0);
                    if(delta>0){
                        // Record the EXACT accepted pickup, including whether
                        // it was physical material OR the legacy extra-18
                        // distance. The centroid trail is never a collider.
                        contactGeometry.pickup(episode,elapsedLife,sensorMind.action,
                            creature,surface,object,reserve-before,trailX,trailY,
                            trailCount,"habitat");
                        energyEaten++;
                    }
                    contactsThisLife++;chosenObject=null;goalTimer=0;
                    lastEvent="STIMOLO "+(object.signature+1)+"  ENERGIA "+(delta>=0?"+":"")+(int)delta;
                    eventTime=4;
                    if(delta<0){
                        // Damage slows actual distributed material, not the
                        // read-only virtual observation point.
                        for(int i=0;i<MotorBrain.N;i++){
                            creature.vx[i]*=.6f;creature.vy[i]*=.6f;
                        }
                        if(useSymbiote&&symMatter!=null)
                            symMatter.observeBody(creature,surface);
                        else MaterialGeometry.observe(creature);
                    }
                    save();
                }
            }
            eventTime=Math.max(0,eventTime-dt);
            if(reserve<=0){
                // Terminal supervision is the genuine end of a physical life,
                // NEVER a negative label on the gate, squares or last action.
                mortality.terminal(reserve/100f);
                if(chamber!=null){
                    gripsEver+=chamber.grasps;
                    farthestGate=Math.max(farthestGate,chamber.maxDoorTravel);
                }
                // Injury immediately preceding death must be recorded BEFORE
                // the transient episode buffer is reset.
                if(!useSymbiote&&eventEnergyDeltaSinceAction<0){
                    episodic.action(sensorMind.stateCopy(),sensorMind.action);
                    if(episodic.consequenceSeparated(0,energySinceAction,
                       lastAppliedMotor,lastAppliedHand,lastAppliedMembrane,false))save();
                }
                // Close ONE measured life only on real death, never on pause,
                // scene expansion, app restart or switching named save slots.
                // Freeze the last real motor choice (possibly shorter than 0.65s)
                // BEFORE death clears body and transient sensorimotor traces.
                if(brain3!=null)brain3.interrupt();
                else sensorMind.recordTerminal(SensorimotorMind.Senses.read(
                    creature,surface,chamber,environment,reserve),
                    environmentEffectSinceAction,creature);
                // Save a separate last-life trace even if the app is closed
                // before the next twelve-second physical-scene checkpoint.
                gateJournal.terminal(elapsedLife,reserve);
                gripObserver.terminal();
                saveGripObserver();
                // Freeze both complete traces atomically before reincarnation.
                if(!prefs.edit().putString(DecisionBlackbox.LAST,
                    decisionBlackbox.lastLife())
                    .putString(GateLifeJournal.LAST,gateJournal.lastLife())
                    .commit())
                    throw new IllegalStateException("Last-life recording failed");
                lifeStats.physical(journey.maxNetLife,journey.lifeCells,
                    chamber==null?0:chamber.maxDoorTravel);
                lifeStats.death();
                competenceMemoryV21.newLife();
                skillExecution.resetLife();
                reserve=70;lastEvent="CORPO ESAUSTO • MEMORIA CONSERVATA";eventTime=6;
                resetEpisode();
                nextChamber=elapsedLife+12f;
                save();
            }
        }
        @Override protected void onSizeChanged(int w,int h,int ow,int oh){
            super.onSizeChanged(w,h,ow,oh);
            H=h/(w/W);
        }
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            if(getWidth()==0)return;
            long now=System.nanoTime();
            float dt=lastFrame==0?0:Math.min(.038f,(now-lastFrame)*.000000001f);
            lastFrame=now;
            uiTime+=dt;
            // Never allow a V2.2 motor controller to take over after a V3
            // loading, sensor, checkpoint or device failure.
            if(brain3==null)simulationRunning=false;
            if(resetNotice>0)resetNotice-=dt;
            // Fixed-step headless-style burst: preserve ALL original physics,
            // senses, hunger, actions, learning and real event timestamps.
            // Wall clock controls CPU budget only, never simulated outcomes.
            boolean fullSpeed=testMax&&!settingsExpanded&&!showBenchmark;
            if(fullSpeed&&android.os.Build.VERSION.SDK_INT>=29&&
                now-testMaxThermalCheckedNs>2000000000L){
                testMaxThermalCheckedNs=now;
                android.os.PowerManager thermal=(android.os.PowerManager)
                    GameActivity.this.getSystemService(Context.POWER_SERVICE);
                if(thermal!=null){
                    testMaxThermalStatus=thermal.getCurrentThermalStatus();
                    if(testMaxThermalStatus>=android.os.PowerManager.THERMAL_STATUS_SEVERE){
                        stopTestMax("TEST MAX fermato: temperatura alta");
                        fullSpeed=false;
                    }
                }
            }
            final int frameSteps=fullSpeed?TEST_MAX_MAX_STEPS:
                physicsClock.steps(simulationRunning?dt:0f,speed);
            // Short bounded burst, always returning control to UI/touch.
            final long burstDeadline=System.nanoTime()+
                (testMaxThermalStatus>=android.os.PowerManager.THERMAL_STATUS_MODERATE
                    ?30000000L:TEST_MAX_BUDGET_NS);
            if(simulationRunning){
                for(int i=0;i<frameSteps;i++){
                    if(fullSpeed&&i>0&&(i&15)==0&&System.nanoTime()>=burstDeadline)break;
                    float slice=TEST_MAX_FIXED_DT;
                    // OTA144 CRITICAL: Android's first onDraw after launch,
                    // save-slot switch or Activity resume has lastFrame==0,
                    // so dt==0. The new physical fluid solver correctly
                    // rejects zero-duration integration. Do not execute ANY
                    // physics, sensor or life events until dt>0. In OTA143
                    // this unguarded first frame crashed every new Symbiote
                    // immediately and again on every app relaunch.
                    if(!Float.isFinite(slice)||slice<=0f)continue;
                    if(fullSpeed)testMaxSimSeconds+=slice;
                    float energyBeforeFrame=reserve;
                    float gateStart=chamber==null?0f:chamber.doorY;
                    long previousFrameGateHolds=chamber==null?0:chamber.gateHeldFrames;
                    materialBefore.keySet().retainAll(environment);
                    if(useSymbiote)for(UnknownWorld.Entity e:environment)if(e.isBox()){
                        float[] prior=materialBefore.get(e);
                        if(prior==null){prior=new float[2];materialBefore.put(e,prior);}
                        prior[0]=e.x;prior[1]=e.y;
                    }
                    int lifeStamp=episode;
                    updateHabitat(slice);
                    // ACTION is sampled from learned sensory-action
                    // values. There is no atan2(food), target teleport or
                    // fixed rule that pulling an opened gate is "correct".
                    float mx=brain3!=null?brain3.directionX():sensorMind.motorX();
                    float my=brain3!=null?brain3.directionY():sensorMind.motorY();
                    heading=(float)Math.atan2(my,mx);
                    float px=creature.cx+mx*145f,py=creature.cy+my*145f;
                    float beforeX=creature.cx,beforeY=creature.cy;
                    float worldEffectAtFrameStart=environmentEffectSinceAction;
                    // The 0.9.5 regression introduced old sensory episodes
                    // that could override ALL physically learned controllers.
                    // Even after the motor fix, old hand/membrane weights
                    // could overwrite grip output *specifically on tactile
                    // contact*, when grip is needed most. Remove all such
                    // actuator overrides. Episodic memory still influences
                    // ZERO's independently selected ACTIONS, but procedural
                    // skills come only from today's live trained controllers
                    // or physically tested candidate mutations.
                    if(!useSymbiote){
                        lastAppliedMotor=liveBody.weights(0,trainer.best);
                    lastAppliedHand=liveBody.weights(1,handTrainer.best);
                    lastAppliedMembrane=liveBody.weights(2,shapeTrainer.best);
                    float[] trialHand=lastAppliedHand,trialMembrane=lastAppliedMembrane;
                    boolean actualTouch=false;
                    for(float touch:surface.touchSense)if(touch>.2f){actualTouch=true;break;}
                    // Legacy motor genomes do not operate the living cortex.
                    if(!useSymbiote){
                        lastAppliedHand=skillExecution.hand(lastAppliedHand,
                            (brain3!=null?brain3.adhesion():sensorMind.adhesion())>.5f,actualTouch,liveBody.testingCandidate());
                        lastAppliedMembrane=skillExecution.membrane(lastAppliedMembrane,
                            (brain3!=null?brain3.adhesion():sensorMind.adhesion())>.5f,actualTouch,liveBody.testingCandidate());
                    }
                    if(!useSymbiote&&
                       (lastAppliedHand!=trialHand||lastAppliedMembrane!=trialMembrane)){
                        techniqueUsedSinceAction=true;
                        // An archived controller is not the incumbent tested
                        // in this body's comparison window. Discard the window
                        // rather than crediting another controller's outcome.
                        liveBody.abortTrial();
                    }
                    }
                    // OTA167: the same neural intent now drives fast
                    // proprioceptive recurrent refinement every real dt.
                    // Record efference from EXACTLY what physics receives.
                    if(useSymbiote&&symMatter!=null&&brain3!=null){
                        try{
                            brain3.actuate(episode,creature,surface,symMatter,
                                environment,chamber,reserve,worldTime,slice);
                            mx=brain3.directionX();my=brain3.directionY();
                        }catch(java.io.IOException failure){
                            simulationRunning=false;
                            brain3Error=failure.getMessage();break;
                        }
                    }else if(useSymbiote&&symMatter!=null&&symMotor!=null){
                        symMotor.fastTick(creature,surface,slice);
                        sensorMind.appliedSymbioteControls(symMotor.commands(),creature,surface);
                    }else if(brain3==null)sensorMind.appliedControls(lastAppliedMotor,lastAppliedHand,lastAppliedMembrane);
                    // Prevent fast material masses from skipping through a
                    // square between consecutive frames. Snapshot before any
                    // physical motion, after routine world streaming.
                    System.arraycopy(surface.x,0,squarePrevSkinX,0,Morphology.POINTS);
                    System.arraycopy(surface.y,0,squarePrevSkinY,0,Morphology.POINTS);
                    System.arraycopy(creature.x,0,squarePrevBodyX,0,MotorBrain.N);
                    System.arraycopy(creature.y,0,squarePrevBodyY,0,MotorBrain.N);
                    float oldCoreX=creature.cx,oldCoreY=creature.cy;
                    // True motor impulse from physical fluid reactions,
                    // recorded BEFORE the external collision solver runs.
                    float witnessedMuscleImpulse=0f;
                    float brain3WorkBefore=symMatter==null?0f:symMatter.work;
                    float uncorrectedX=oldCoreX,uncorrectedY=oldCoreY;
                    if(useSymbiote&&symMatter!=null&&(brain3!=null||symMotor!=null)){
                        // Keep OTA148/151 physics EXACTLY unchanged. Cortex
                        // work is an actually measured material quantity;
                        // debit a small, explicit metabolic energy cost from
                        // the SAME reserve that determines real survival.
                        // No positive feedback for distance or deformation.
                        float workBefore=symMatter.work;
                        symMatter.step(creature,surface,
                            brain3!=null?brain3.commands():symMotor.commands(),slice);
                        witnessedMuscleImpulse=symMatter.lastMuscleReactionImpulse;
                        uncorrectedX=creature.cx;uncorrectedY=creature.cy;
                        float freshWork=Math.max(0f,symMatter.work-workBefore);
                        if(Float.isFinite(freshWork)&&freshWork>0f){
                            // Physical work -> resource units calibration.
                            // Small compared to existing basal scarcity;
                            // only physically executed nonzero contraction
                            // draws fuel. An idle body pays no motor cost.
                            float actualFuel=Math.min(reserve,freshWork*8f);
                            if(actualFuel>0f){
                                reserve-=actualFuel;
                                if(brain3==null)sensorMind.motorMetabolicCost(actualFuel);
                                mortality.energyFlows(0f,actualFuel,0f,0f);
                            }
                        }
                    }else{
                        creature.step(lastAppliedMotor,mx,my,slice,lastAppliedHand,px,py);
                        surface.step(creature,lastAppliedMembrane,px,py,slice,
                            (brain3!=null?brain3.adhesion():sensorMind.adhesion()));
                    }
                    if(chamber!=null){
                        float doorBefore=chamber.doorY;
                        chamber.constrain(creature,surface,slice,(brain3!=null?brain3.adhesion():sensorMind.adhesion()));
                        gripObserver.gateFrame(surface,chamber,(brain3!=null?brain3.adhesion():sensorMind.adhesion()),
                            elapsedLife,slice);
                        float actuallyMoved=Math.abs(chamber.doorY-doorBefore);
                        if(Float.isFinite(actuallyMoved))
                            environmentEffectSinceAction+=Math.min(2,actuallyMoved);
                        farthestGate=Math.max(farthestGate,chamber.maxDoorTravel);
                        gateMetrics.observe(creature,chamber);
                        gateJournal.observe(creature,chamber,gateMetrics,elapsedLife,reserve);

                        // Preserve motor apparatus only when the PHYSICS
                        // confirmed adhesion held, not because ZERO touched
                        // the gate or because it was told to attempt a grip.
                        boolean physicalHold=chamber.gateHeldFrames>previousGripHeld;
                        boolean opposedNow=chamber.opposedContactFrames>previousOpposedFrames;
                        if(physicalHold){
                            if(!useSymbiote&&gripBank.observe(lastAppliedHand,lastAppliedMembrane,
                                true,opposedNow,
                                Math.max(0,actuallyMoved),sensorMind.stateCopy()))save();
                        }
                        previousGripHeld=chamber.gateHeldFrames;
                        previousOpposedFrames=chamber.opposedContactFrames;
                        boolean voluntaryGripAtGate=
                            chamber.gateDriveFrames>previousGripDrive;
                        previousGripDrive=chamber.gateDriveFrames;
                        if(gripRecoveryCooldown>0)gripRecoveryCooldown--;
                        // NO scripted grip: restoration is allowed ONLY when
                        // ZERO independently selected adhesion AND its body
                        // touched the solid without managing to hold.
                        if(voluntaryGripAtGate&&!physicalHold){
                            ongoingUnsuccessfulGrip++;
                        }else if(physicalHold||!voluntaryGripAtGate){
                            ongoingUnsuccessfulGrip=0;
                        }
                        if(!useSymbiote&&ongoingUnsuccessfulGrip>=120 &&
                           gripRecoveryCooldown==0 &&
                           !liveBody.testingCandidate()){
                            GripSkillBank.Pair historical=gripBank.retrieveAfterRepeatedFailure(
                                handTrainer.best,shapeTrainer.best,sensorMind.stateCopy());
                            if(historical!=null){
                                handTrainer.best=historical.hand;
                                shapeTrainer.best=historical.membrane;
                                liveBody.abortTrial();
                                ongoingUnsuccessfulGrip=0;
                                gripRecoveryCooldown=900;
                                lastEvent="TECNICA DI PRESA RECUPERATA";
                                eventTime=3;save();
                            }
                        }
                    }
                    // Only measured physical displacement after collisions
                    // and before simulated death can count as motor progress.
                    // OTA163: real traveled path must be computed after ALL
                    // physical collisions, from the same material center as
                    // coordination learning. Measuring here was premature.
                    // The smaller square puzzle has real static collisions
                    // and requires independent opposing adhesive contacts.
                    // Unlike round disks, pressing a box cannot translate it.
                    boolean anyBox=false;
                    for(UnknownWorld.Entity e:environment)if(e.isBox()){anyBox=true;break;}
                    if(anyBox&&chamber==null)
                        java.util.Arrays.fill(surface.touchSense,0f);
                    boolean boxTouchedThisFrame=false,boxOpposedThisFrame=false,boxAnyHeldThisFrame=false;
                    for(UnknownWorld.Entity e:environment){
                        if(!e.isBilateralBox())continue;
                        // Track real world displacement between physics frames;
                        // historical offset alone does not count as fresh motion.
                        float[] prior=boxLastPositions.put(e,new float[]{e.x,e.y});
                        float actualBoxMotion=prior==null?0f:
                            (float)Math.hypot(e.x-prior[0],e.y-prior[1]);
                        gripObserver.beforeBox(surface,e);
                        SquareGripPuzzle.Outcome grips=SquareGripPuzzle.constrain(
                            creature,surface,e,slice,
                            squarePrevSkinX,squarePrevSkinY,
                            squarePrevBodyX,squarePrevBodyY,
                            oldCoreX,oldCoreY);
                        gripObserver.boxFrame(surface,e,(brain3!=null?brain3.adhesion():sensorMind.adhesion()),
                            elapsedLife,slice);
                        if(grips.contacts>0)boxTouchedThisFrame=true;
                        if(grips.held>0)boxAnyHeldThisFrame=true;
                        if(e.bilateralHeldNow)boxOpposedThisFrame=true;
                        // A proven two-sided hold on an actual moving box
                        // archives the usable hand/membrane pair, independently
                        // of whether anything valuable is found underneath.
                        if(grips.opposingGrip&&actualBoxMotion>.005f){
                            // Reward only VERIFIED cover travel, never touch or intent.
                            environmentEffectSinceAction+=Math.min(2f,actualBoxMotion);
                            if(!useSymbiote&&gripBank.observe(lastAppliedHand,lastAppliedMembrane,
                               true,true,Math.min(2f,actualBoxMotion),
                               sensorMind.stateCopy()))save();
                            // Research-only archive of physically proven technique:
                            // self tissue and actual motion; anonymous sighting
                            // confidence is an uncertainty tag, not object truth.
                            if(!useSymbiote&&competenceMemoryV21.witnessed(lastAppliedHand,
                               lastAppliedMembrane,sensorMind.stateCopy(),
                               grips.opposingGrip,actualBoxMotion,
                               objectContinuityV21.currentTrackConfidence()))save();
                        }
                        if(grips.opposingGrip)e.peakDisplacement=Math.max(
                            e.peakDisplacement,e.displacementFromSpawn());
                        if(SquareGripPuzzle.uncover(e,environment)){
                            boxesUncovered++;
                            environmentEffectSinceAction+=Math.min(12f,e.displacementFromSpawn());
                            lastEvent="ENERGIA NASCOSTA SCOPERTA • BLOCCO SPOSTATO";
                            eventTime=6f;
                            save();
                        }
                    }
                    if(boxTouchedThisFrame)boxTouchFramesSinceAction++;
                    if(boxOpposedThisFrame)boxOpposedFramesSinceAction++;
                    // Recovery of a PROVEN physical grip is allowed only
                    // after ZERO himself selected adhesion, maintained real
                    // box contact and repeatedly failed to hold it. Never
                    // turn on grips by script and never count bare collision.
                    if(chamber==null&&gripRecoveryCooldown>0)gripRecoveryCooldown--;
                    boolean voluntaryBoxGrip=boxTouchedThisFrame&&(brain3!=null?brain3.adhesion():sensorMind.adhesion())>.5f;
                    if(voluntaryBoxGrip&&!boxAnyHeldThisFrame)boxFailedGripFrames++;
                    else boxFailedGripFrames=0;
                    if(!useSymbiote&&boxFailedGripFrames>=120&&gripRecoveryCooldown==0&&
                       !liveBody.testingCandidate()){
                        GripSkillBank.Pair oldGrip=gripBank.retrieveAfterRepeatedFailure(
                            handTrainer.best,shapeTrainer.best,sensorMind.stateCopy());
                        boxFailedGripFrames=0;
                        gripRecoveryCooldown=oldGrip==null?240:900;
                        if(oldGrip!=null){
                            handTrainer.best=oldGrip.hand;
                            shapeTrainer.best=oldGrip.membrane;
                            liveBody.abortTrial();
                            lastEvent="PRESA APPRESA RECUPERATA";
                            eventTime=3;save();
                        }
                    }
                    // Only INERT construction plates receive the unilateral
                    // physical pressure response. Energy-cover squares and
                    // the existing gate retain their two-sided adhesion.
                    for(UnknownWorld.Entity panel:environment){
                        if(!panel.isConstruction()||panel.consumed)continue;
                        PanelPhysics.Contact pushed=PanelPhysics.constrain(
                            creature,surface,panel,slice,
                            squarePrevSkinX,squarePrevSkinY,
                            squarePrevBodyX,squarePrevBodyY);
                        // No artificial reward on mere contact: displacement
                        // of a real material counts only through existing
                        // observed physical world outcomes.
                        if(pushed.pressureImpulse>0)
                            panel.peakDisplacement=Math.max(panel.peakDisplacement,
                                panel.displacementFromSpawn());
                    }
                    // Different covers cannot own the SAME material patch.
                    // The local finite-area contact solver also relaxes
                    // coincident tissue; it neither chooses an action nor
                    // artificially releases a valid separate-patch grip.
                    MaterialContactConstraints.reconcile(creature,surface,
                        environment,slice);
                    // Read centroid from ACTUAL muscle material, not a
                    // simulated nucleus. Collision projection may move it.
                    if(useSymbiote&&symMatter!=null){
                        // EXACTLY HERE: contact correction on 96 membrane,
                        // 10 old body masses and 64 liquid parcels of same gel.
                        SymbioteWorldStepV171.interior(symMatter,creature,surface,
                            chamber,environment,slice);
                    }else MaterialGeometry.observe(creature);
                    // Restore the union of physically valid object-owned holds
                    // for vision and next-frame tactile input only.
                    GripSurfaceComposition.compose(surface,chamber,environment);
                    // Sample only if two separately held energy covers
                    // physically constrain the SAME body; passive at most
                    // every 8 simulated seconds, including at TEST MAX.
                    contactGeometry.trap(episode,elapsedLife,sensorMind.action,
                        creature,surface,environment,trailX,trailY,trailCount);
                    // ZERO2 shadow: actual 96-site, simultaneously composed
                    // physics-owned holds sampled at every 35ms substep,
                    // including grips shorter than the ~650ms decision tick.
                    // This cannot command a hold or modify physical forces.
                    if(slice>0f)sensorMind.sampleGripPhysicsFrame(surface);
                    gripObserver.pruneBoxes(environment);
                    resolveObjects(slice);
                    // Weather reaches the actual deformable tissue AFTER
                    // solid collisions. Physical shadows protect only the
                    // points behind genuinely placed/solid blockers.
                    ShelterEcology.Exposure weather=shelter.step(
                        creature,surface,environment,chamber,worldTime,slice);
                    if(weather.energyLoss>0){
                        float actual=Math.min(reserve,weather.energyLoss);
                        reserve-=actual;
                        sensorMind.ambientDamage(actual);
                        mortality.energyFlows(0f,0f,actual,0f);
                        // Real experienced energy drain; never assign to a
                        // red resource signature or to moving a block.
                    }
                    if(lifeStamp==episode){
                        float newlyMeasuredEffect=Math.max(0,
                            environmentEffectSinceAction-worldEffectAtFrameStart);
                        motorGuard.observe(slice,creature.cx,creature.cy,newlyMeasuredEffect);
                        // If the learned policy/curriculum is stuck, an old
                        // physically proven motor can be recovered from the
                        // archive. No destination, force or gait is prescribed.
                        // Old MotorBrain candidate weights are dormant when
                        // physical movement uses the living cortex instead.
                        if(!useSymbiote&&motorGuard.recoveryActivations>lastSkillStall){
                            skillBank.stallEvent();
                            lastSkillStall=motorGuard.recoveryActivations;
                            if(!liveBody.testingCandidate()){
                                float[] alternate=skillBank.retrieve(trainer.best);
                                if(alternate!=null){
                                    trainer.best=alternate;
                                    liveBody.abortTrial();
                                    lastEvent="ABILITA MOTORIA RECUPERATA";
                                    eventTime=3;
                                    save();
                                }
                            }
                        }
                    }
                    if(chamber!=null&&!chamber.reachedEnergy&&
                        chamber.foodTouchedByBody(creature,surface)){
                        float prior=reserve;
                        reserve=Math.min(100,reserve+UnknownWorld.RARE_ENERGY);
                        lifeStats.contact(reserve-prior);
                        energyTotals.record(reserve-prior,true);
                        mortality.energyFlows(0,0,0,Math.max(0,reserve-prior));
                        lifeStats.roomSolved();
                        if(reserve>prior)
                            contactGeometry.pickup(episode,elapsedLife,sensorMind.action,
                                creature,surface,chamber.food,reserve-prior,
                                trailX,trailY,trailCount,"room");
                        eventEnergyDeltaSinceAction+=reserve-prior;
                        if(brain3==null)sensorMind.contactOutcome(reserve-prior,chamber.food.signature);
                        energySinceAction[chamber.food.signature]+=reserve-prior;
                        int gained=(int)(reserve-prior);
                        energyEaten++;chambersCleared++;
                        gateJournal.reward(creature,chamber,elapsedLife,reserve-prior);
                        chamberMessage="SCOPERTA RARA • RISERVA RIPRISTINATA";
                        lastEvent="SCOPERTA RARA • ENERGIA +"+gained;
                        eventTime=7;
                        nextChamber=elapsedLife+40;
                        save();
                    }
                    // OTA163: final body displacement is shared by the life
                    // odometer, temporal learners and real cortex credit.
                    // Same pre-physics material center; never count a respawn.
                    if(lifeStamp==episode){
                        float realDx=creature.cx-oldCoreX,realDy=creature.cy-oldCoreY;
                        float realTravel=(float)Math.hypot(realDx,realDy);
                        if(Float.isFinite(realTravel)&&realTravel<3f){
                            lifeDistance+=realTravel;totalTravel+=realTravel;
                            travelSinceAction+=realTravel;
                            forwardSinceAction+=realDx*mx+realDy*my;
                            bestLifeDistance=Math.max(bestLifeDistance,lifeDistance);
                        }
                    }
                    // OTA162: real actuator credit from the same completed
                    // material frame, after box/door collisions and object motion.
                    // No new physical force or action chooser. Exclude respawns.
                    if(lifeStamp==episode&&useSymbiote&&symMotor!=null&&brain3==null){
                        // A felt touch is useful somatic feedback, not
                        // evidence of exogenous displacement. OTA172 marked
                        // nearly every crawling interval as externally
                        // disturbed, preventing a real action->motion model.
                        // Preserve strict exclusion for solver impulses,
                        // actually moved objects, storms and held materials.
                        boolean heldExternal=false;
                        for(int siteIndex=0;siteIndex<Morphology.POINTS;siteIndex++)
                            if(surface.gripHold[siteIndex]>=Morphology.GRIP_HOLD){
                                heldExternal=true;break;
                            }
                        boolean externalDisturbance=
                            MotorResponseJournalV172.externalMotionContaminated(
                                creature.cx-uncorrectedX,creature.cy-uncorrectedY,
                                environmentEffectSinceAction-worldEffectAtFrameStart,
                                shelter.storm(worldTime),heldExternal);
                        float heldTravel=0f;boolean actualHold=false;
                        if(chamber!=null&&chamber.gateHeldFrames>previousFrameGateHolds){
                            heldTravel+=Math.abs(chamber.doorY-gateStart);actualHold=true;
                        }
                        for(UnknownWorld.Entity e:environment){
                            float[] old=materialBefore.get(e);
                            if(old==null)continue;
                            boolean held=e.isConstruction()?PanelPhysics.anyHeld(e):e.bilateralHeldNow;
                            if(held){actualHold=true;heldTravel+=(float)Math.hypot(e.x-old[0],e.y-old[1]);}
                        }
                        symMotor.observedManipulation(heldTravel,actualHold&&!shelter.storm(worldTime));
                        symMotor.observedPhysicalStep(oldCoreX,oldCoreY,
                            creature,surface,witnessedMuscleImpulse,
                            externalDisturbance,slice);
                    }
                    // The journal observes the lived world, never suggests a
                    // destination to ZERO. Do not count death/respawn as travel.
                    if(lifeStamp==episode){
                        journey.observe(creature.cx,creature.cy,energyBeforeFrame,reserve);
                        lifeStats.physical(journey.maxNetLife,journey.lifeCells,
                            chamber==null?0:chamber.maxDoorTravel);
                        // Episodic valence comes only from actual contact;
                        // energyBeforeFrame is retained for motion telemetry.
                    }
                    // Same ORIGINAL physics + energy/room/box lifecycle;
                    // only the live 3.0 neuron's completed action receives credit.
                    if(brain3!=null&&lifeStamp==episode){
                        try{
                            boolean holding=false;
                            for(float hold:surface.gripHold)
                                if(hold>=Morphology.GRIP_HOLD){holding=true;break;}
                            boolean external=MotorResponseJournalV172.externalMotionContaminated(
                                creature.cx-uncorrectedX,creature.cy-uncorrectedY,
                                environmentEffectSinceAction-worldEffectAtFrameStart,
                                shelter.storm(worldTime),holding);
                            brain3.observed(creature,surface,symMatter,environment,
                                chamber,reserve,worldTime,slice,
                                Math.max(0f,symMatter.work-brain3WorkBefore),
                                witnessedMuscleImpulse,external);
                            // Keep original HUD visual sensors refreshed but
                            // never enter the 2.2 decision/training loop.
                            sensorMind.updateBodySense(creature,surface);
                            sensorMind.updateDistributedSense(creature,surface,chamber,environment);
                            sensorMind.updateImpedanceSense(creature,surface);
                            sensorMind.updateInnerSense(creature,surface);
                        }catch(Exception failure){
                            simulationRunning=false;brain3Error=failure.getMessage();break;
                        }
                    }
                    // Legacy actor is strictly disabled whenever V3 is active.
                    if(brain3==null){
                    sensorMind.setLearning(true);
                    boolean actedFromMemory=!useSymbiote&&episodic.physicallyReplaying();
                    long beforeExperience=sensorMind.experiences;
                    // Snapshot the PREVIOUS decision before the model chooses
                    // the next one. Its sensory state is what ZERO actually saw.
                    boolean sensoryBoundary=sensorMind.observationDue(slice);
                    float[] observedBefore=sensoryBoundary?sensorMind.stateCopy():null;
                    int actionBefore=sensorMind.action;
                    sensorMind.observe(sensoryBoundary?SensorimotorMind.Senses.read(
                        creature,surface,chamber,environment,reserve):null,slice,
                        environmentEffectSinceAction,creature,surface,
                        chamber,environment);
                    if(sensorMind.experiences>beforeExperience){
                        if(!useSymbiote)skillExecution.boundary(competenceMemoryV21,sensorMind.stateCopy(),
                            sensorMind.action,environmentEffectSinceAction,liveBody.testingCandidate());
                        mortality.observed(reserve/100f);
                        boolean remembered=false;
                        if(!useSymbiote){
                            episodic.action(observedBefore,actionBefore);
                            int recallsBefore=episodic.repeatEffects;
                            remembered=episodic.consequenceSeparated(
                                environmentEffectSinceAction,energySinceAction,
                                lastAppliedMotor,lastAppliedHand,lastAppliedMembrane,
                                actedFromMemory);
                            if(episodic.repeatEffects>recallsBefore)
                                lifeStats.confirmedRecall();
                        }
                        // Object hypotheses are included in normal periodic checkpoints,
                        // pauses and physical events. Do not rewrite the entire
                        // profile on every 0.65-second observational update.
                        if(remembered||episodic.takeDirty()||
                           sensorMind.takeTemporalSkillsDirty())save();
                        // Remembering an action never substitutes an old
                        // physical controller or interrupts body experiments.
                        // The legacy genes do not actuate a Symbiote.
                        // Never accept their mutation because the separate
                        // 84-weight living cortex happened to find energy.
                        OnlineBodyLearning.Result result=
                            (useSymbiote||techniqueUsedSinceAction)?null:liveBody.observe(
                            sensorMind.lastReward,travelSinceAction,forwardSinceAction,
                            Math.max(0,physicalContacts-liveStartContacts),
                            liveStartEnergy,reserve,journey.takeNewCells(),
                            liveStartX,liveStartY,creature.cx,creature.cy,
                            chamber!=null,mx,my,
                            trainer.best,handTrainer.best,shapeTrainer.best,
                            environmentEffectSinceAction,
                            boxTouchFramesSinceAction,boxOpposedFramesSinceAction);
                        techniqueUsedSinceAction=false;
                        boxTouchFramesSinceAction=boxOpposedFramesSinceAction=0;
                        liveStartX=creature.cx;liveStartY=creature.cy;
                        travelSinceAction=0;forwardSinceAction=0;
                        environmentEffectSinceAction=0;eventEnergyDeltaSinceAction=0;
                        java.util.Arrays.fill(energySinceAction,0f);
                        liveStartEnergy=reserve;liveStartContacts=physicalContacts;
                        if(result!=null){
                            if(result.domain==0){
                                trainer.attempts++;
                                // Both controllers were exercised physically.
                                // Archive the incumbent BEFORE replacing it.
                                skillBank.observe(trainer.best,liveBody.lastBaselineNet,
                                    liveBody.lastBaselineScore);
                                if(result.accepted){
                                    skillBank.observe(result.weights,liveBody.lastCandidateNet,
                                        liveBody.lastCandidateScore);
                                    trainer.best=result.weights;
                                    trainer.accepted++;
                                }
                            }
                            else if(result.domain==1){handTrainer.attempts++;if(result.accepted){
                                handTrainer.best=result.weights;handTrainer.accepted++;}}
                            else {shapeTrainer.attempts++;
                                if(chamber!=null)shapeTrainer.gateAttempts++;
                                if(result.accepted){shapeTrainer.best=result.weights;
                                    shapeTrainer.accepted++;
                                    if(chamber!=null)shapeTrainer.gateAccepted++;}}
                            save();
                        }
                    }
                    }
                    phase+=slice;
                    if(phase>=13f){
                        // NOT a reset: the world expands around ZERO.
                        extendHabitat();
                    }
                    trailClock+=slice;
                    if(trailClock>.14f){
                        trailClock=0;
                        if(trailCount<trailX.length)trailCount++;
                        for(int n=trailCount-1;n>0;n--){trailX[n]=trailX[n-1];trailY[n]=trailY[n-1];}
                        trailX[0]=creature.cx;trailY[0]=creature.cy;
                    }
                }
            }
            if(fullSpeed&&testMaxStartedNs>0){
                float realSeconds=(System.nanoTime()-testMaxStartedNs)*1.0e-9f;
                if(realSeconds>.1f)testMaxActualRate=(float)(testMaxSimSeconds/realSeconds);
            }
            // Lightweight periodic checkpoint for app/task interruptions.
            // Normal neural saves remain more frequent than physical writes.
            if(simulationRunning&&lastSave+12000000000L<now){
                save();saveScene();lastSave=now;
            }
            c.save();float scale=getWidth()/W;c.scale(scale,scale);
            renderWorld(c);
            renderBody(c);
            renderHud(c);
            if(!brain3Error.isEmpty()){
                rounded(c,20,H-208,460,H-160,0xe82a1726,12);
                text(c,brain3Error.length()>72?brain3Error.substring(0,72):brain3Error,
                    240,H-178,10,0xffffd0ce,true);
            }
            c.restore();
            postInvalidateOnAnimation();
        }
        void rounded(Canvas c,float l,float t,float r,float b,int color,float rad){
            paint.setShader(null);paint.setColor(color);paint.setStyle(Paint.Style.FILL);
            c.drawRoundRect(l,t,r,b,rad,rad,paint);
        }
        void text(Canvas c,String s,float x,float y,int size,int color,boolean center){
            paint.setShader(null);paint.setColor(color);
            paint.setTextAlign(center?Paint.Align.CENTER:Paint.Align.LEFT);
            paint.setTextSize(size);
            paint.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));
            c.drawText(s,x,y,paint);
        }
        void ring(Canvas c,float x,float y,float radius,int color,float thickness){
            paint.setShader(null);paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(thickness);
            c.drawCircle(x,y,radius,paint);paint.setStyle(Paint.Style.FILL);
        }
        void renderWorld(Canvas c){
            paint.setShader(new LinearGradient(0,0,0,H,0xff060812,0xff17252e,Shader.TileMode.CLAMP));
            c.drawRect(0,0,W,H,paint);paint.setShader(null);
            float middle=(H-172+180)/2;
            onScreenY=middle;
            onScreenX=W*.5f;
            c.save();
            c.clipRect(0,149,W,H-169);
            c.translate(onScreenX-creature.cx*2.3f,onScreenY-creature.cy*2.3f);
            c.scale(2.3f,2.3f);
            // Simulated surface is an abstract habitat, not a rendered animation.
            float minX=(creature.cx-135),maxX=(creature.cx+135);
            float minY=creature.cy-(H-330)/4.6f,maxY=creature.cy+(H-330)/4.6f;
            paint.setColor(0x1436bcaf);paint.setStrokeWidth(.6f);
            for(int k=(int)Math.floor(minX/34)-1;k<(int)Math.ceil(maxX/34)+1;k++)
                c.drawLine(k*34,minY,k*34,maxY,paint);
            for(int k=(int)Math.floor(minY/34)-1;k<(int)Math.ceil(maxY/34)+1;k++)
                c.drawLine(minX,k*34,maxX,k*34,paint);
            for(int i=-4;i<=4;i++)for(int j=-5;j<=5;j++){
                float xx=i*53,yy=j*53;
                int hash=(i*1337+j*1987);
                float rr=2+Math.abs(hash)%4;
                paint.setColor(Math.abs(hash)%3==0?0x39307879:0x272e5d68);
                c.drawCircle(xx+((i*j)%17),yy,rr,paint);
            }
            if(chamber!=null){
                float x=chamber.entranceX(),gx=chamber.gateX(),cx=chamber.ox,cy=chamber.oy;
                paint.setShader(null);
                paint.setColor(0x1824b6b6);
                c.drawRect(x,cy+ScarcityLab.TOP,cx+ScarcityLab.END_X,
                    cy+ScarcityLab.BOTTOM,paint);
                paint.setColor(0xff4a7781);
                c.drawRect(x-ScarcityLab.WALL_HALF_THICKNESS,cy+ScarcityLab.TOP-10,x+ScarcityLab.WALL_HALF_THICKNESS,
                    cy-ScarcityLab.OPEN_HALF_HEIGHT,paint);
                c.drawRect(x-ScarcityLab.WALL_HALF_THICKNESS,cy+ScarcityLab.OPEN_HALF_HEIGHT,x+ScarcityLab.WALL_HALF_THICKNESS,
                    cy+ScarcityLab.BOTTOM+9,paint);
                c.drawRect(x-ScarcityLab.WALL_HALF_THICKNESS,cy+ScarcityLab.TOP-10,cx+ScarcityLab.END_X+ScarcityLab.WALL_HALF_THICKNESS,
                    cy+ScarcityLab.TOP+7,paint);
                c.drawRect(x-ScarcityLab.WALL_HALF_THICKNESS,cy+ScarcityLab.BOTTOM-ScarcityLab.WALL_HALF_THICKNESS,cx+ScarcityLab.END_X+ScarcityLab.WALL_HALF_THICKNESS,
                    cy+ScarcityLab.BOTTOM+9,paint);
                c.drawRect(cx+ScarcityLab.END_X-ScarcityLab.WALL_HALF_THICKNESS,cy+ScarcityLab.TOP-10,
                    cx+ScarcityLab.END_X+ScarcityLab.WALL_HALF_THICKNESS,cy+ScarcityLab.BOTTOM+10,paint);
                // A PHYSICALLY SOLID pane in the upper entrance wall.
                // Only optical occlusion is different; ScarcityLab's five
                // original solid slabs and gate collision are untouched.
                float glassX=x,glassTop=cy+OpticalCues.GLASS_TOP,
                      glassBottom=cy+OpticalCues.GLASS_BOTTOM;
                paint.setColor(0xff19343f);
                c.drawRoundRect(glassX-8,glassTop,glassX+8,
                    glassBottom,2.5f,2.5f,paint);
                paint.setColor(0xff367b86);
                c.drawRoundRect(glassX-5.8f,glassTop+2,glassX+5.8f,
                    glassBottom-2,1.5f,1.5f,paint);
                paint.setColor(0x88b7f9e9);
                c.drawLine(glassX-3.7f,glassTop+3,glassX-3.7f,
                    glassBottom-3,paint);
                paint.setColor(0xffdfb982);
                c.drawRoundRect(gx-ScarcityLab.DOOR_HALF_W,chamber.gateTop(),
                    gx+ScarcityLab.DOOR_HALF_W,chamber.gateBottom(),4,4,paint);
                paint.setColor(0xff0d1a22);
                c.drawCircle(gx,chamber.gateCenterY(),3,paint);
                if(chamber.openingFree())
                    text(c,"APERTA",gx,cy-87,10,TEAL,true);
                else text(c,"BLOCCATA",gx,cy-87,9,GOLD,true);
            }
            // Read-only, filmic wind: calm ribbons and air motes follow the
            // ACTUAL storm direction. No gameplay physics adjusted by view.
            WindArt.draw(c,paint,minX,maxX,minY,maxY,worldTime,uiTime,
                         shelter,environment);
            int[] colors={0xff64d8ca,0xffcc7da4,0xffd2b889};
            for(UnknownWorld.Entity object:environment){
                if(object.consumed||object.concealed)continue;
                float x=object.x,y=object.y;
                paint.setShader(new RadialGradient(x,y,35,new int[]{(object.ready()?0x2248dfc4:0x113d7377),0x003d7377},null,Shader.TileMode.CLAMP));
                c.drawCircle(x,y,35,paint);paint.setShader(null);
                // Object's original position remains visible in world space.
                // A line and an outline show actual physical displacement:
                // this is evidence, not an animation attached to ZERO.
                if(object.isMovable()){
                    float shifted=object.displacementFromSpawn();
                    if(shifted>3f){
                        paint.setShader(null);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(1.05f);
                        paint.setColor(0x7796dbcf);
                        c.drawLine(object.spawnX,object.spawnY,x,y,paint);
                        paint.setColor(0x66efbe76);
                        c.drawCircle(object.spawnX,object.spawnY,object.radius*.86f,paint);
                        paint.setStyle(Paint.Style.FILL);
                        if(shifted>=8){
                            text(c,(int)shifted+" U",x,y-object.radius-10,9,GOLD,true);
                        }
                    }
                }
                if(object.isConstruction()){
                    // Thin rigid metal plate, visually AND physically rotated.
                    // A clean bevel, directional brushed-metal reflection,
                    // three retaining ribs and small fasteners. No crate X.
                    float half=PanelPhysics.halfLength(object);
                    float thick=PanelPhysics.HALF_THICKNESS;
                    c.save();
                    c.rotate((float)Math.toDegrees(object.angle),x,y);
                    paint.setShader(null);
                    paint.setShadowLayer(9f,0f,3f,0x88000000);
                    paint.setColor(0xff06131d);
                    c.drawRoundRect(x-half-2.2f,y-thick-2.4f,
                                    x+half+2.2f,y+thick+2.4f,3.8f,3.8f,paint);
                    paint.clearShadowLayer();
                    paint.setShader(new LinearGradient(x-half,y-thick,
                       x-half,y+thick,new int[]{0xffa6d0dc,0xff466b80,
                                              0xff1d394b,0xff6f9bad},
                       new float[]{0,.22f,.78f,1f},Shader.TileMode.CLAMP));
                    c.drawRoundRect(x-half,y-thick,x+half,y+thick,2.9f,2.9f,paint);
                    paint.setShader(null);
                    paint.setColor(0x889bd6de);paint.setStrokeWidth(.65f);
                    c.drawLine(x-half+5,y-thick+2.5f,
                               x+half-5,y-thick+2.5f,paint);
                    paint.setColor(0xff264658);
                    for(int ribs=-1;ribs<=1;ribs++){
                        float px=x+ribs*half*.48f;
                        c.drawRoundRect(px-1.5f,y-thick+2,
                            px+1.5f,y+thick-2,.8f,.8f,paint);
                    }
                    paint.setColor(0xffbce0eb);
                    c.drawCircle(x-half+4.5f,y,1.35f,paint);
                    c.drawCircle(x+half-4.5f,y,1.35f,paint);
                    paint.setColor(0x775a9aab);
                    c.drawLine(x-half+6,y+thick-2.7f,
                               x+half-6,y+thick-2.7f,paint);
                    c.restore();
                    paint.setShader(null);paint.setStrokeWidth(1f);
                    continue;
                }
                if(object.isBox()){
                    // Subtle photons escaping under a REAL, still covering
                    // square. This matches the optical model's faint cue,
                    // and never shows the actual full +40 object prematurely.
                    boolean concealedLight=false;
                    for(UnknownWorld.Entity source:environment)
                        if(OpticalCues.coverOf(source,environment)==object){
                            concealedLight=true;break;
                        }
                    if(concealedLight){
                        // Purple photon spill is visible outside the
                        // opaque cover. This changes only rendering; ZERO's
                        // weak, directional optical signal remains physical.
                        float pulse=.85f+.15f*(float)Math.sin(uiTime*2.1f);
                        paint.setShader(new RadialGradient(x,y,38f,
                            new int[]{0x805f32d0,0x505f32d0,0x005f32d0},
                            null,Shader.TileMode.CLAMP));
                        paint.setAlpha((int)(255*pulse));
                        c.drawRoundRect(x-38,y-38,x+38,y+38,9,9,paint);
                        paint.setAlpha(255);
                        paint.setShader(null);
                    }
                    paint.setColor(0xff0a131e);
                    c.drawRoundRect(x-20.8f,y-20.8f,x+20.8f,y+20.8f,4,4,paint);
                    paint.setColor(0xffcaa875);
                    c.drawRoundRect(x-20,y-20,x+20,y+20,3,3,paint);
                    paint.setColor(0xff544335);
                    c.drawRoundRect(x-15,y-15,x+15,y+15,2,2,paint);
                    paint.setColor(0xffd6c7a2);
                    c.drawLine(x-12,y-12,x+12,y+12,paint);
                    c.drawLine(x+12,y-12,x-12,y+12,paint);
                    paint.setColor(0xffe4ce9b);
                    c.drawCircle(x,y,3,paint);
                    continue;
                }
                paint.setColor(0xff151c26);
                c.drawCircle(x,y,object.radius,paint);
                paint.setColor(object.isRare()?0xffb58cff:colors[object.signature]);
                c.drawCircle(x,y,object.radius*.63f,paint);
                if(object.isRare()){
                    paint.setColor(0xffffdf93);
                    c.drawCircle(x,y,object.radius*.27f,paint);
                    ring(c,x,y,object.radius+4+2*(float)Math.sin(uiTime*3),0x88d9a7ff,1.3f);
                }
                paint.setColor(0x66ffffff);
                c.drawCircle(x-object.radius*.25f,y-object.radius*.22f,object.radius*.17f,paint);
                if(object==chosenObject){
                    ring(c,x,y,object.radius+6+2*(float)Math.sin(uiTime*4),0x8885fae0,1f);
                }
                if(!object.ready())ring(c,x,y,object.radius+3,0x44455a64,1f);
            }
            for(int i=trailCount-1;i>=0;i--){
                int alpha=(int)(85*(1-(float)i/trailX.length));
                if(alpha<=0)continue;
                paint.setColor((alpha<<24)|0x20c6b0);
                c.drawCircle(trailX[i],trailY[i],1.8f,paint);
            }
            c.restore();
            // Halo around the real-time organism
            paint.setShader(new RadialGradient(onScreenX,onScreenY,131,new int[]{0x153de5c7,0x0a6ae6c7,0x002bd9b5},null,Shader.TileMode.CLAMP));
            c.drawCircle(onScreenX,onScreenY,131,paint);paint.setShader(null);
            text(c,"MONDO SCONOSCIUTO • STIMOLO",24,H-189,10,0xff718f99,false);
        }
        void renderBody(Canvas c){
            // Gentle independent LED easing in WALL time, not x20 simulation time.
            // Lights expose already measured physical state; they do not learn
            // or manipulate a single motor, tactile or policy weight.
            long frame=System.nanoTime();
            float ledDt=lastLedFrame==0?1f/60f:
                Math.max(.004f,Math.min(.07f,(frame-lastLedFrame)*1e-9f));
            lastLedFrame=frame;
            c.save();
            c.translate(onScreenX-creature.cx*2.3f,onScreenY-creature.cy*2.3f);
            c.scale(2.3f,2.3f);
            // Catmull-like quadratic smoothing through the simulation's actual
            // perimeter masses; NOT a fixed spider silhouette.
            float[] xx=surface.x, yy=surface.y;
            path.reset();
            boolean nearSolidSquare=false;
            for(UnknownWorld.Entity object:environment){
                if(object.isBox()&&Math.hypot(object.x-creature.cx,
                                             object.y-creature.cy)<190f){
                    nearSolidSquare=true;break;
                }
            }
            if(chamber!=null||nearSolidSquare){
                // Draw the ACTUAL straight bonded edges near rigid solids.
                // Quadratic smoothing may visually cut through a solid even
                // after every physical segment has been collision-corrected.
                path.moveTo(xx[0],yy[0]);
                for(int i=1;i<Morphology.POINTS;i++)
                    path.lineTo(xx[i],yy[i]);
            }else{
                float a0=(xx[Morphology.POINTS-1]+xx[0])*.5f;
                float b0=(yy[Morphology.POINTS-1]+yy[0])*.5f;
                path.moveTo(a0,b0);
                for(int i=0;i<Morphology.POINTS;i++){
                    int j=(i+1)%Morphology.POINTS;
                    path.quadTo(xx[i],yy[i],(xx[i]+xx[j])*.5f,
                        (yy[i]+yy[j])*.5f);
                }
            }
            path.close();
            paint.setShader(null);paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(13,0,4,0x7b000000);
            paint.setColor(0xff000208);c.drawPath(path,paint);paint.clearShadowLayer();
            paint.setShader(new RadialGradient(creature.cx-9,creature.cy-15,52,
                new int[]{0xff242629,0xff121417,0xff060709,0xff010203},
                new float[]{0f,.34f,.74f,1f},Shader.TileMode.CLAMP));
            c.drawPath(path,paint);paint.setShader(null);
            paint.setColor(0xff06080b);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(.9f);
            c.drawPath(path,paint);paint.setStyle(Paint.Style.FILL);
            // Each of the 96 black points lights ONLY from real signals:
            // cyan = actuation / local velocity / tactile pressure / local sight;
            // red = confirmed physically HELD adhesion, never mere press-on.
            for(int i=0;i<Morphology.POINTS;i++){
                float xxLocal=xx[i],yyLocal=yy[i];
                float relativeSpeed=(float)Math.hypot(
                    surface.vx[i]-creature.cvx,surface.vy[i]-creature.cvy);
                float pressure=Math.max(surface.touchSense[i],surface.touchPulse[i]);
                float localEye=distributedSense.node(i,DistributedBodySense.OPTIC);
                float activation=Math.min(1f,
                    .37f*Math.min(1f,surface.extension[i]/Morphology.MAX_REACH)+
                    .44f*Math.min(1f,relativeSpeed/120f)+
                    .65f*Math.min(1f,pressure)+
                    .20f*localEye);
                boolean holding=false;
                if(surface.gripHold[i]>=Morphology.GRIP_HOLD&&
                   surface.touchSense[i]>.5f)holding=true;
                float blueTarget=holding?activation*.24f:activation;
                float blueRate=1f-(float)Math.exp(-ledDt/
                    (blueTarget>cyanLeds[i]?.11f:.32f));
                cyanLeds[i]+=(blueTarget-cyanLeds[i])*blueRate;
                float redTarget=holding?1f:0f;
                float redRate=1f-(float)Math.exp(-ledDt/
                    (holding?.15f:.27f));
                redLeds[i]+=(redTarget-redLeds[i])*redRate;
                // Every point is black at rest. No alternating decorative LEDs.
                paint.setShader(null);paint.setAlpha(255);
                paint.setColor(0xff030508);
                c.drawCircle(xxLocal,yyLocal,1.2f,paint);
                float blue=Math.max(0,cyanLeds[i])*(1-.8f*redLeds[i]);
                if(blue>.015f){
                    paint.setColor(((int)(58*blue)<<24)|0x009ad8);
                    c.drawCircle(xxLocal,yyLocal,3.1f+blue*2.1f,paint);
                    paint.setColor(((int)(228*blue)<<24)|0x50deff);
                    c.drawCircle(xxLocal,yyLocal,1.1f+blue*.9f,paint);
                }
                if(redLeds[i]>.015f){
                    float red=redLeds[i];
                    paint.setShader(new RadialGradient(xxLocal,yyLocal,7.7f,
                        new int[]{0xffffb1a8,0xffff3041,0x99e70c26,0x00b00000},
                        new float[]{0f,.20f,.49f,1f},Shader.TileMode.CLAMP));
                    paint.setAlpha(Math.max(0,Math.min(255,(int)(red*255f))));
                    c.drawCircle(xxLocal,yyLocal,7.7f,paint);
                    paint.setShader(null);paint.setAlpha(255);
                    paint.setColor(((int)(240*red)<<24)|0xff263c);
                    c.drawCircle(xxLocal,yyLocal,1.8f,paint);
                    paint.setColor(((int)(230*red)<<24)|0xffd6c8);
                    c.drawCircle(xxLocal-.44f,yyLocal-.5f,.65f,paint);
                }
            }
            // No nucleus drawn: the entire connected deformable body is ZERO.
            // cx/cy only follow living tissue for camera and learned odometry.
            c.restore();
            text(c,"ZERO",onScreenX,onScreenY-104,14,0xffd7fff5,true);
        }
        /**
         * OTA 112: sparse game HUD. All text is diagnostic; the entire UI
         * must remain downstream of physics/brain, never feeding the policy.
         * Coordinates are logical (W=480), scaling automatically on Android.
         */
        void renderHud(Canvas c){
            // Energy dots are vertically stacked over the transparent world.
            hudEnergy(c,37,46,"BLU",energyTotals.blue,0xff58d4ff);
            hudEnergy(c,37,95,"ROSSA",energyTotals.red,0xffff5789);
            hudEnergy(c,37,144,"VIOLA",energyTotals.violet,0xffbc8bff);
            if(energyTotals.legacyPositive>0){
                text(c,"PRECEDENTI +"+energyTotals.legacyPositive,20,181,8,GOLD,false);
            }else if(!energyTotals.complete){
                text(c,"STORICO PARZIALE",20,181,8,GOLD,false);
            }

            // Top-right transparent/holographic vital panel, not a header.
            neonFrame(c,157,14,471,221,18);
            text(c,"NETTO "+(int)journey.netDistance+" U",172,40,13,0xffefffff,false);
            text(c,"VIA "+(int)lifeDistance+" U",303,40,12,0xffe8fbff,false);
            text(c,"MAX "+(int)journey.recordNet+" U • VITA "+
                LifeStatistics.duration(lifeStats.current().seconds),
                172,62,10,GOLD,false);
            neonOrb(c,438,48,20,0xff83eaff,simulationRunning);
            text(c,simulationRunning?"Ⅱ":"▶",438,55,15,0xffe8fdff,true);
            text(c,"ENERGIA "+(int)reserve+"/100",172,88,11,0xffc0fff3,false);
            neonBar(c,173,96,456,108,reserve,0xff63fff0,0xff28cbbb);
            String wind=shelter.storm(worldTime)?"TEMPESTA":
                shelter.warning(worldTime)?"VENTO VICINO":
                "VENTO FRA "+(int)shelter.untilStorm(worldTime)+"s";
            text(c,"TESSUTO "+(int)shelter.integrity+"/100",172,128,11,0xffc2cdff,false);
            text(c,wind,450,128,9,0xff97a8c9,true);
            neonBar(c,173,137,456,149,shelter.integrity,0xffb4b9ff,0xff648fea);
            dividerHud(c,171,161,453);
            text(c,"ESPERIENZE "+(brain3!=null?brain3.decisions():sensorMind.experiences)+
                " • DECISIONI "+(brain3!=null?brain3.decisions():sensorMind.decisions),
                173,179,10,0xffb3ced7,false);
            text(c,"DISCHI "+pushes+" • BLOCCHI "+boxesUncovered+" • ZONE "+journey.lifeCells+
                " • STANZE "+chambersCleared,173,195,9,GOLD,false);
            text(c,brain3!=null?
                "CERVELLO 3.0 • GRU64 • TRAIN "+brain3.trained()+
                    " • TEST "+brain3.validated():useSymbiote?
                "CERVELLO 2.2 • SKILL TEMPO "+temporalSkillBank.stored()+
                " • MOTORIE "+(symMotor==null?0:symMotor.skillMemory().verifiedCount()):
                "ABILITA "+skillBank.count()+" • RICORDI "+episodic.stored()+
                " • TECNICHE "+gripBank.count(),173,210,9,TEAL,false);

            // TEST MAX stop is its own small hit target, away from TEMPO.
            if(testMax&&!settingsExpanded){
                rounded(c,13,217,153,260,0xe6172534,9);
                text(c,"TEST MAX  x"+(int)testMaxActualRate,83,234,11,GOLD,true);
                text(c,"STOP TEST",83,249,11,0xffffa8a0,true);
            }
            // Only a small floating chevron, with no surrounding toolbar.
            smallChevron(c,447,250,detailsExpanded);
            if(detailsExpanded)renderDetails(c);

            // Bottom three-stop dock. Large thumb targets and central x1/x5/x20.
            renderDock(c);

            if(settingsExpanded)renderSettings(c);
            if(uiTime<updaterStatusUntil&&updaterStatus.length()>0){
                float ty=H-173;
                rounded(c,52,ty-28,428,ty+7,0xf21c3945,11);
                text(c,updaterStatus.length()>47?updaterStatus.substring(0,47):updaterStatus,
                    240,ty-5,10,0xffdcfdf5,true);
            }
            if(showBenchmark)renderBenchmark(c);
        }

        void hudEnergy(Canvas c,float x,float cy,String label,long total,int color){
            // Radial light over an otherwise transparent background.
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new RadialGradient(x,cy,24,
                new int[]{color&0x00ffffff|0x44000000,0x07162331,0x00000000},
                null,Shader.TileMode.CLAMP));
            c.drawCircle(x,cy,24,paint);paint.setShader(null);
            paint.setColor(0xc7081523);c.drawCircle(x,cy,14.5f,paint);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.4f);
            paint.setColor(color);c.drawCircle(x,cy,13.2f,paint);
            paint.setStyle(Paint.Style.FILL);paint.setColor(color);
            c.drawCircle(x,cy,9.4f,paint);
            paint.setColor(0x99ffffff);c.drawCircle(x-3,cy-3,3.1f,paint);
            text(c,String.valueOf(total),61,cy+4,17,0xfff4faff,false);
            text(c,label,61,cy+18,8,color,false);
        }
        void neonFrame(Canvas c,float l,float t,float r,float b,float radius){
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(l,t,r,b,
                new int[]{0xe0091928,0xba061522,0xe0081422},null,Shader.TileMode.CLAMP));
            c.drawRoundRect(l,t,r,b,radius,radius,paint);
            paint.setShader(null);paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.2f);paint.setColor(0x9947bbd6);
            c.drawRoundRect(l,t,r,b,radius,radius,paint);
            paint.setStrokeWidth(2.0f);paint.setColor(0xff7bf4f5);
            c.drawLine(l+24,t,l+108,t,paint);
            c.drawLine(r-67,b,r-19,b,paint);
            paint.setStyle(Paint.Style.FILL);
        }
        void neonOrb(Canvas c,float x,float y,float radius,int accent,
                     boolean luminous){
            paint.setShader(new RadialGradient(x,y,radius+7f,
                new int[]{luminous?0x6624eeed:0x4624a8c5,0x280a3147,0x00101926},
                null,Shader.TileMode.CLAMP));
            c.drawCircle(x,y,radius+7f,paint);paint.setShader(null);
            paint.setColor(0xec0c2530);c.drawCircle(x,y,radius,paint);
            paint.setColor(accent);paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(luminous?2.2f:1.4f);
            c.drawCircle(x,y,radius,paint);paint.setStyle(Paint.Style.FILL);
        }
        void neonBar(Canvas c,float l,float t,float r,float b,
                     float percent,int head,int tail){
            rounded(c,l,t,r,b,0xe2223446,7);
            float fraction=Math.max(0,Math.min(1,percent/100f));
            float end=l+(r-l)*fraction;
            if(end-l>.5f){
                paint.setShader(new LinearGradient(l,t,end,b,
                    new int[]{tail,head},null,Shader.TileMode.CLAMP));
                c.drawRoundRect(l,t,end,b,6,6,paint);
                paint.setShader(null);
            }
            paint.setColor(0x553e8195);paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(.8f);c.drawRoundRect(l,t,r,b,7,7,paint);
            paint.setStyle(Paint.Style.FILL);
        }
        void dividerHud(Canvas c,float l,float y,float r){
            paint.setShader(new LinearGradient(l,y,r,y,
                new int[]{0x006af4ee,0x7554c3df,0x006af4ee},null,Shader.TileMode.CLAMP));
            paint.setStrokeWidth(1f);c.drawLine(l,y,r,y,paint);paint.setShader(null);
        }
        void smallChevron(Canvas c,float x,float y,boolean up){
            paint.setShader(null);
            paint.setColor(0x333c7485);c.drawCircle(x,y,23,paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.2f);paint.setColor(0x807adee9);
            c.drawCircle(x,y,19,paint);
            paint.setStrokeWidth(2.8f);paint.setColor(0xff6cfcf0);
            int sign=up?-1:1;
            c.drawLine(x-8,y-3*sign,x,y+5*sign,paint);
            c.drawLine(x,y+5*sign,x+8,y-3*sign,paint);
            paint.setStyle(Paint.Style.FILL);
        }
        void renderDock(Canvas c){
            float top=H-143f,bottom=H-16f,centerY=H-77f;
            paint.setShader(new LinearGradient(0,top,W,bottom,
                new int[]{0xc8051320,0xe80c1e2e,0xc8051320},
                null,Shader.TileMode.CLAMP));
            c.drawRoundRect(12,top,468,bottom,38,38,paint);
            paint.setShader(null);paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.2f);paint.setColor(0x7659c9e6);
            c.drawRoundRect(12,top,468,bottom,38,38,paint);
            paint.setStyle(Paint.Style.FILL);
            dockButton(c,29,top+17,179,bottom-13,0xffa49dff,false,"CERVELLO");
            dockButton(c,300,top+17,450,bottom-13,0xff9fb5df,true,"IMPOSTAZIONI");
            neonOrb(c,240,centerY,49,0xff71ffee,true);
            paint.setColor(0x551deacb);paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4);c.drawCircle(240,centerY,54,paint);
            paint.setStyle(Paint.Style.FILL);
            // Play/fast-forward symbol: icon drawn as geometry, not emoji font.
            fastArrow(c,240,centerY-8,0xff83ffeb);
            text(c,"TEMPO x"+speed,240,centerY+35,12,0xffebfffc,true);
        }
        void dockButton(Canvas c,float l,float t,float r,float b,int tint,
                        boolean gear,String caption){
            paint.setShader(new LinearGradient(l,t,r,b,
                new int[]{0xde132d40,0xd909182a},null,Shader.TileMode.CLAMP));
            c.drawRoundRect(l,t,r,b,26,26,paint);paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.4f);
            paint.setColor(0x9772aac4);c.drawRoundRect(l,t,r,b,26,26,paint);
            paint.setStyle(Paint.Style.FILL);
            float x=(l+r)*.5f,y=t+29;
            if(gear)gearIcon(c,x,y,tint);else brainIcon(c,x,y,tint);
            text(c,caption,x,b-16,11,0xffe1f1fa,true);
        }
        void brainIcon(Canvas c,float x,float y,int tint){
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2.4f);
            paint.setColor(tint);
            for(int side=-1;side<=1;side+=2){
                c.drawArc(x+side*12-8,y-13,x+side*12+8,y+3,
                    side<0?125:235,205,false,paint);
                c.drawCircle(x+side*7,y+6,8,paint);
                c.drawLine(x+side*3,y-8,x+side*3,y+12,paint);
            }
            paint.setStyle(Paint.Style.FILL);
        }
        void gearIcon(Canvas c,float x,float y,int tint){
            paint.setColor(tint);paint.setStrokeWidth(2.6f);
            paint.setStyle(Paint.Style.STROKE);
            c.drawCircle(x,y,11,paint);c.drawCircle(x,y,4.2f,paint);
            for(int i=0;i<8;i++){
                double a=i*Math.PI/4;
                c.drawLine(x+(float)Math.cos(a)*12,y+(float)Math.sin(a)*12,
                           x+(float)Math.cos(a)*17,y+(float)Math.sin(a)*17,paint);
            }
            paint.setStyle(Paint.Style.FILL);
        }
        void fastArrow(Canvas c,float x,float y,int color){
            paint.setColor(color);paint.setStyle(Paint.Style.FILL);
            for(int i=0;i<2;i++){
                float offset=i*15-15;
                path.reset();
                path.moveTo(x+offset-8,y-11);
                path.lineTo(x+offset+7,y);
                path.lineTo(x+offset-8,y+11);
                path.close();c.drawPath(path,paint);
            }
        }
        void renderDetails(Canvas c){
            float bottom=Math.min(H-155,648);
            if(bottom<320)return;
            neonFrame(c,158,278,471,bottom,16);
            c.save();c.clipRect(164,283,465,bottom-8);
            text(c,"DATI TECNICI",174,303,12,TEAL,false);
            text(c,"AZIONE 2.2 "+(sensorMind.action+1)+"/16 • DIREZ "+
                (sensorMind.action%8+1)+"/8"+
                " • ADESIONE "+((brain3!=null?brain3.adhesion():sensorMind.adhesion())>0?"ON":"OFF"),
                174,324,9,0xffc3dee2,false);
            text(c,"URTI "+physicalContacts+" • DIST MAX "+(int)biggestDisplacement+
                " U • ESPERIENZE 2.2 "+recurrentCore.transitions(),174,341,9,MUTED,false);
            text(c,"MEMBRANA "+Morphology.POINTS+" PUNTI • FLUIDO "+SymbioteMatterV1.INNER+
                " • EST REALE "+(int)surface.peakExtension,174,357,9,MUTED,false);
            text(c,"PRESA E MANIPOLAZIONE",174,384,10,TEAL,false);
            text(c,"PRESE FISICHE "+(gripsEver+(chamber!=null?chamber.grasps:0))+
                " • SETTORI MUSCOLO "+SymbioteMatterV1.SECTORS,174,403,9,GOLD,false);
            if(chamber!=null){
                text(c,"PORTA "+(int)chamber.doorY+" U • DUE LATI "+
                    chamber.opposedContactFrames,174,421,9,MUTED,false);
                text(c,"TOCCHI "+chamber.tactileFrames+" • ADESIONI "+
                    chamber.adhesiveContactFrames,174,437,9,MUTED,false);
                text(c,"SENZA PRESA "+chamber.dualTouchNoGrip+" • TENUTA "+
                    chamber.gateHeldFrames,174,453,9,MUTED,false);
                text(c,"PRESA SCELTA "+chamber.gateDriveFrames+
                    " • COMANDO "+chamber.gateCommandFrames,174,469,9,MUTED,false);
            }else{
                text(c,"NESSUNA STANZA ATTIVA",174,424,9,MUTED,false);
            }
            text(c,"AMBIENTE E RISORSE",174,496,10,TEAL,false);
            text(c,(scarcity?"SCARSITA ATTIVA":"SCORTE NORMALI")+
                " • MATERIALI "+shelter.placedCount(),174,515,9,MUTED,false);
            text(c,"ENERGIE "+energyEaten+" • STANZE "+chambersCleared+
                " • SKILL 2.2 "+temporalSkillBank.stored(),174,531,9,MUTED,false);
            if(energyTotals.legacyPositive>0)
                text(c,"POSITIVI STORICI NON DIVISIBILI "+
                    energyTotals.legacyPositive,174,547,9,GOLD,false);
            c.restore();
        }
        float settingsTop(){
            return Math.max(75,Math.min(150,H*.13f));
        }
        float testButtonY(){
            float bottom=Math.min(H-155,settingsTop()+488);
            return Math.min(H-208,bottom+24);
        }
        void stopTestMax(String notice){
            testMax=false;
            testMaxStartedNs=0;
            testMaxThermalStatus=0;
            lastFrame=0;
            if(notice!=null){
                updaterStatus=notice;
                updaterStatusUntil=uiTime+5f;
            }
            invalidate();
        }
        void confirmTestMax(){
            if(testMax){stopTestMax("TEST MAX arrestato");return;}
            new android.app.AlertDialog.Builder(GameActivity.this)
                .setTitle("Avvia TEST MAX?")
                .setMessage("La simulazione avanzera alla massima velocita sostenibile. " +
                    "Le leggi fisiche, l'apprendimento e il tempo simulato restano " +
                    "invariati. Il telefono potrebbe scaldarsi. " +
                    "Premi STOP TEST per fermarti; uscendo dall'app il test si interrompe.")
                .setNegativeButton("ANNULLA",null)
                .setPositiveButton("AVVIA TEST", (dialog,which)->{
                    testMax=true;simulationRunning=true;
                    testMaxStartedNs=System.nanoTime();
                    testMaxThermalCheckedNs=0;
                    testMaxSimSeconds=0;testMaxActualRate=0;
                    settingsExpanded=false;
                    lastFrame=0;
                    postInvalidateOnAnimation();
                }).show();
        }
        void renderSettings(Canvas c){
            // In-app navigation. No new Activity; existing screens/OTA stay
            // intact and use their original brain-save transitions.
            paint.setShader(null);paint.setColor(0xdf020a12);
            c.drawRect(0,0,W,H,paint);
            float top=settingsTop(),bottom=Math.min(H-155,top+488);
            neonFrame(c,30,top,450,bottom,23);
            text(c,"IMPOSTAZIONI",56,top+36,21,0xfff0fff9,false);
            text(c,"GESTIONE • ANALISI • APP",56,top+55,10,TEAL,false);
            smallChevron(c,419,top+32,true);
            String[] names={
                "PARTITE E SALVATAGGI",
                "ANALISI CERVELLO 3.0",
                "STATISTICHE VITE",
                "PESI E PARAMETRI 3.0",
                "NEURONI 3D",
                "ATTIVA STANZA",
                "AGGIORNA APP"
            };
            String[] hints={
                "Slot, carica, nuovo ZERO",
                "GRU64, sensi e apprendimento reale",
                "Durata, eventi, medie 25 vite",
                "Parametri e neuroni 3.0",
                "Moduli e connessioni reali della rete 3.0",
                "Ambiente e cancello fisico",
                "Controlla la prossima OTA"
            };
            float first=top+78f,rowHeight=52f;
            c.save();c.clipRect(34,top+68,447,bottom-49);
            for(int i=0;i<names.length;i++){
                float y=first+i*rowHeight;
                rounded(c,44,y,436,y+47,0xba142c39,10);
                paint.setColor(0x6045a1b2);paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(.8f);
                c.drawRoundRect(44,y,436,y+47,10,10,paint);
                paint.setStyle(Paint.Style.FILL);
                text(c,names[i],59,y+20,12,0xffeff9fa,false);
                text(c,hints[i],59,y+36,9,MUTED,false);
                text(c,"›",414,y+30,23,TEAL,true);
            }
            c.restore();
            dividerHud(c,48,bottom-49,433);
            text(c,"VERSIONE "+installedVersion+" • OTA",55,bottom-23,11,GOLD,false);
            // This is NOT next to the TEMPO cycle. Its own deliberate,
            // settings-only control is followed by an explicit confirmation.
            float ty=testButtonY();
            rounded(c,105,ty,375,ty+47,
                testMax?0xc33b2930:0xc21d3944,13);
            paint.setColor(testMax?0xffffa094:0xff79ebd4);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.6f);
            c.drawRoundRect(105,ty,375,ty+47,13,13,paint);
            paint.setStyle(Paint.Style.FILL);
            text(c,testMax?"ARRESTA TEST MAX":"AVVIA TEST MAX",240,ty+20,14,
                 testMax?0xffffa094:0xffa6fff0,true);
            text(c,"Solo esperimenti • velocita adattiva",240,ty+37,9,MUTED,true);
            // Footer navigation remains visible; only its right side is active.
            paint.setColor(0xeb061320);
            rounded(c,12,H-143,468,H-16,0xeb071621,35);
            dockButton(c,29,H-126,179,H-29,0xffa49dff,false,"CERVELLO");
            neonOrb(c,240,H-77,49,0xff71ffee,true);
            fastArrow(c,240,H-86,0xff83ffeb);
            text(c,"TEMPO x"+speed,240,H-42,12,0xffebfffc,true);
            dockButton(c,300,H-126,450,H-29,0xff9fb5df,true,"CHIUDI");
        }
        void renderBenchmark(Canvas c){
            float top=H*.32f;
            rounded(c,20,top,460,top+294,0xf20e222e,22);
            text(c,"ESPERIMENTI CORPOREI REALI",240,top+31,15,TEAL,true);
            String d=liveBody.lastDomain==0?"MOTORE":
                     liveBody.lastDomain==1?"MANIPOLAZIONE":
                     liveBody.lastDomain==2?"MEMBRANA":"IN ATTESA";
            text(c,"ULTIMA PROVA: "+d,240,top+69,13,GOLD,true);
            text(c,"PESI SALVATI: "+String.format(Locale.US,"%.2f",liveBody.lastBaselineScore),
                 240,top+101,14,MUTED,true);
            text(c,"PESI SPERIMENTALI: "+String.format(Locale.US,"%.2f",liveBody.lastCandidateScore),
                 240,top+132,14,TEAL,true);
            text(c,"NETTO "+(int)liveBody.lastBaselineNet+" U → "+
                 (int)liveBody.lastCandidateNet+" U",
                 240,top+162,14,0xffd9e8e6,true);
            text(c,liveBody.lastDomain<0?"NESSUNA PROVA COMPLETATA":
                 liveBody.lastWasAccepted?"VARIANTE CONSERVATA":"VARIANTE SCARTATA",
                 240,top+195,14,liveBody.lastWasAccepted?TEAL:GOLD,true);
            text(c,"Finestre reali successive, NON A/B identico.",
                 240,top+229,11,MUTED,true);
            text(c,"TOCCA PER CHIUDERE",240,top+278,11,MUTED,true);
        }
        String format(float x){return String.format(Locale.US,"%+.1f",x);}
        void button(Canvas c,float l,float t,float r,float b,String caption,int color){
            rounded(c,l,t,r,b,0xff203640,13);
            paint.setColor(color);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.3f);
            c.drawRoundRect(l,t,r,b,13,13,paint);paint.setStyle(Paint.Style.FILL);
            text(c,caption,(l+r)*.5f,(t+b)*.5f+5,caption.length()>9?10:12,color,true);
        }
        void compare(){
            // Shows ONLY two actual windows from lived experience.
            // Legacy off-screen target-driven benchmark was misleading.
            showBenchmark=true;
        }
        @Override public boolean onTouchEvent(MotionEvent ev){
            if(ev.getActionMasked()!=MotionEvent.ACTION_UP)return true;
            float scale=getWidth()/W;
            float x=ev.getX()/scale,y=ev.getY()/scale;
            if(showBenchmark){
                showBenchmark=false;invalidate();return true;
            }
            // One-tap emergency stop, deliberately separated from x1/x5/x20.
            if(testMax&&!settingsExpanded&&x>=12&&x<=160&&y>=208&&y<=268){
                stopTestMax("TEST MAX arrestato");return true;
            }
            // Modal settings intercepts all taps outside explicit menu items.
            if(settingsExpanded){
                float top=settingsTop(),first=top+78f;
                if(y>top&&y<top+65&&x>382){
                    settingsExpanded=false;invalidate();return true;
                }
                if(y>=H-151){
                    if(x<179){
                        settingsExpanded=false;
                        save();
                        GameActivity.this.startActivity(new android.content.Intent(
                            GameActivity.this,BrainStatsActivity.class));
                    }else if(x<301){
                        speed=speed==1?5:speed==5?20:1;
                        settingsExpanded=false;
                    }else settingsExpanded=false;
                    invalidate();return true;
                }
                float ty=testButtonY();
                if(x>=96&&x<=384&&y>=ty&&y<=ty+54){
                    confirmTestMax();invalidate();return true;
                }
                if(x>=44&&x<=436&&y>=first&&y<first+7*52f){
                    int index=(int)((y-first)/52f);
                    settingsChoice(index);
                    invalidate();return true;
                }
                if(x<30||x>450||y<top||y>Math.min(H-155,top+488))
                    settingsExpanded=false;
                invalidate();return true;
            }
            // Full-width bottom navigation, with central speed control.
            if(y>=H-147){
                if(x<180){
                    save();
                    GameActivity.this.startActivity(new android.content.Intent(
                        GameActivity.this,BrainStatsActivity.class));
                }else if(x<300){
                    speed=speed==1?5:speed==5?20:1;
                }else{
                    detailsExpanded=false;settingsExpanded=true;
                }
                invalidate();return true;
            }
            // The top vital panel contains the only always-visible pause.
            if(x>=405&&y>=17&&y<=82){
                simulationRunning=!simulationRunning;
                lastFrame=0;invalidate();return true;
            }
            // Tiny transparent diagnostics chevron, hitbox larger than icon.
            if(x>=408&&y>=224&&y<=279){
                detailsExpanded=!detailsExpanded;
                invalidate();return true;
            }
            showHelp=false;
            return true;
        }
        void settingsChoice(int index){
            switch(index){
                case 0:
                    save();saveScene();
                    GameActivity.this.startActivity(new android.content.Intent(
                        GameActivity.this,SaveSlotsActivity.class));
                    break;
                case 1:
                    save();
                    GameActivity.this.startActivity(new android.content.Intent(
                        GameActivity.this,BrainToolsActivity.class));
                    break;
                case 2:
                    save();saveScene();
                    GameActivity.this.startActivity(new android.content.Intent(
                        GameActivity.this,StatisticsActivity.class));
                    break;
                case 3:
                    save();
                    GameActivity.this.startActivity(new android.content.Intent(
                        GameActivity.this,BrainStatsActivity.class));
                    break;
                case 4:
                    settingsExpanded=false;
                    if(brain3!=null){
                        try{
                            brain3.interrupt();
                            Zero3NeuralGraphV354 real=brain3.graph();
                            saveScene();
                            Zero3OriginalAtlasActivityV356.open(GameActivity.this,real);
                        }catch(Exception invalid){
                            brain3Error="Vista 3D: "+invalid.getMessage();
                            simulationRunning=false;
                        }
                    }
                    break;
                case 5:
                    settingsExpanded=false;
                    if(chamber==null)activateChamber(true);
                    else{
                        updaterStatus="Stanza fisica gia attiva";
                        updaterStatusUntil=uiTime+4f;
                    }
                    break;
                case 6:
                    updater.checkAndInstall();
                    break;
                default:break;
            }
        }
    }
}
