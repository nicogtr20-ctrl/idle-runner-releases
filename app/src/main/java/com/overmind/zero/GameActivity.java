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
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
        lab=new Lab(this);setContentView(lab);
    }
    @Override protected void onPause(){super.onPause();if(lab!=null)lab.suspend();}
    @Override protected void onResume(){super.onResume();if(lab!=null)lab.resume();}
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
        EpisodicMemory episodic=new EpisodicMemory();
        TemporalWorkingMemory workingMemory=new TemporalWorkingMemory();
        TemporalSkillBank temporalSkillBank=new TemporalSkillBank();
        BodyProprioception bodySchema=new BodyProprioception();
        ShelterEcology shelter=new ShelterEcology();
        DistributedBodySense distributedSense=new DistributedBodySense();
        LocalImpedanceSense localImpedance=new LocalImpedanceSense();
        PredictiveWorldV20 predictiveWorldV20=new PredictiveWorldV20();
        TransferSkillsV20 transferV20=new TransferSkillsV20();
        FuturePlannerV20 futureV20=new FuturePlannerV20();
        PhysicalConsequenceV20 physicalConsequenceV20=new PhysicalConsequenceV20();
        GripAffordanceV20 gripAffordanceV20=new GripAffordanceV20();
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
        MotorAutonomyGuard motorGuard=new MotorAutonomyGuard();
        MotorSkillBank skillBank=new MotorSkillBank();
        GripSkillBank gripBank=new GripSkillBank();
        // Read-only physical test instrumentation, scoped to the current room.
        int previousGripDrive=0,previousGripHeld=0,previousOpposedFrames=0;
        int ongoingUnsuccessfulGrip=0;
        int gripRecoveryCooldown=0;
        int lastSkillStall=0;
        float environmentEffectSinceAction=0,eventEnergyDeltaSinceAction=0;
        final float[] energySinceAction=new float[UnknownWorld.SIGNATURES];
        int recalledManipulationEvents=0;
        float[] lastAppliedMotor,lastAppliedHand,lastAppliedMembrane;
        float liveStartX,liveStartY,liveStartEnergy;
        float travelSinceAction=0,forwardSinceAction=0;
        float lifeDistance=0,totalTravel=0,bestLifeDistance=0;
        int liveStartContacts;
        int pushes=0,physicalContacts=0;
        float biggestDisplacement=0;
        float recentDisplacement=0;
        volatile float handInitial=Float.NaN,handTrained=Float.NaN;
        UnknownWorld.Mind mind=new UnknownWorld.Mind();
        DecisionBrain decisionBrain=new DecisionBrain(90517L);
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
        long lastFrame=0,lastSave=0;
        float H=850,clock=0,phase=0,uiTime=0,heading=0,trailClock=0;
        float[] trailX=new float[46],trailY=new float[46];
        int trailCount=0,speed=1,episode=0;
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
            shelter=new ShelterEcology();
            java.util.Arrays.fill(cyanLeds,0f);
            java.util.Arrays.fill(redLeds,0f);
            lastLedFrame=0;
            gripObserver=new GripContactObserver();
            gripPrefs.edit().remove(GripContactObserver.KEY).apply();
            // Named slot loads/new-game atomically swap the working profile
            // while this Activity is paused. Discard any stale in-memory
            // controllers BEFORE the next frame or save.
            if(prefs.getBoolean(SaveSlotStore.PENDING_RELOAD,false) &&
               !prefs.edit().remove(SaveSlotStore.PENDING_RELOAD).commit())
                throw new IllegalStateException("Unable to apply save-slot switch");
            boolean wasReset=consumeResetRequest();
            lifeStats=new LifeStatistics();
            boolean restoredLifeStats=lifeStats.restore(prefs.getString(LifeStatistics.KEY,""));
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
            decisionBrain=new DecisionBrain(90517L);
            decisionBrain.restore(prefs.getString("decision_brain_v05",""));
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
            predictiveWorldV20=new PredictiveWorldV20();
            predictiveWorldV20.restore(prefs.getString(PredictiveWorldV20.KEY,""));
            sensorMind.attachPredictiveWorldV20(predictiveWorldV20);
            transferV20=new TransferSkillsV20();
            transferV20.restore(prefs.getString(TransferSkillsV20.KEY,""));
            sensorMind.attachTransferSkillsV20(transferV20);
            futureV20=new FuturePlannerV20();
            futureV20.restore(prefs.getString(FuturePlannerV20.KEY,""));
            sensorMind.attachFuturePlannerV20(futureV20);
            physicalConsequenceV20=new PhysicalConsequenceV20();
            physicalConsequenceV20.restore(prefs.getString(PhysicalConsequenceV20.KEY,""));
            sensorMind.attachPhysicalConsequenceV20(physicalConsequenceV20);
            gripAffordanceV20=new GripAffordanceV20();
            gripAffordanceV20.restore(prefs.getString(GripAffordanceV20.KEY,""));
            sensorMind.attachGripAffordanceV20(gripAffordanceV20);
            spatialMap=new SpatialPerception();
            spatialMap.restore(prefs.getString(SpatialPerception.KEY,""));
            sensorMind.attachSpatialPerception(spatialMap);
            causalExperiments=new CausalExperiments();
            causalExperiments.restore(prefs.getString(CausalExperiments.KEY,""));
            sensorMind.attachCausalExperiments(causalExperiments);
            navigation=new InstrumentalNavigation();
            navigation.restore(prefs.getString(InstrumentalNavigation.KEY,""));
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
            sensorMind.attachMemory(episodic);
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
                    GameSceneSnapshot.capture(creature,surface,environment,chamber)));
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
                if(!prefs.edit().putString("physical_scene_v100",snapshot.toString())
                    .putString(DecisionBlackbox.CURRENT,decisionBlackbox.encodeCurrent())
                    .putString(GateLifeJournal.CURRENT,gateJournal.encode())
                    .commit())
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
                if(scene==null)return false;
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
                MaterialGeometry.observe(creature);
                sensorMind.resumeEpisode(SensorimotorMind.Senses.read(
                    creature,surface,chamber,environment,reserve),action);
                sensorMind.updateBodySense(creature,surface);
                sensorMind.updateDistributedSense(creature,surface,chamber,environment);
                sensorMind.updateImpedanceSense(creature,surface);
                sensorMind.resumeSpatialSense(creature,surface,chamber,environment);
                sensorMind.updateNavigationSense(creature,chamber,environment);
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
              .putString("decision_brain_v05",decisionBrain.serialize())
              .putString("sensorimotor_model_v09",sensorMind.serialize())
              .putString("sensorimotor_diagnostics_v091",sensorMind.diagnostics.encode())
              .putString("episodic_memory_v095",episodic.encode())
              // 0.10.8: persist learned temporal readouts, hidden context and
              // consolidated action sequences alongside the existing brain.
              // Same working profile is captured atomically by the 12 named slots.
              .putString("temporal_working_memory_v106",workingMemory.encode())
              .putString("temporal_skills_v107",temporalSkillBank.encode())
              .putString(BodyProprioception.KEY,bodySchema.encode())
              .putString(DistributedBodySense.KEY,distributedSense.encode())
              .putString(LocalImpedanceSense.KEY,localImpedance.encode())
              // ZERO 2.0 shadow brain: optional independent persistence; no legacy changes.
              .putString(PredictiveWorldV20.KEY,predictiveWorldV20.encode())
              .putString(TransferSkillsV20.KEY,transferV20.encode())
              .putString(FuturePlannerV20.KEY,futureV20.encode())
              .putString(PhysicalConsequenceV20.KEY,physicalConsequenceV20.encode())
              .putString(GripAffordanceV20.KEY,gripAffordanceV20.encode())
              .putString(SpatialPerception.KEY,spatialMap.encode())
              .putString(CausalExperiments.KEY,causalExperiments.encode())
              .putString(InstrumentalNavigation.KEY,navigation.encode())
              .putString(GoalContinuity.KEY,intentions.encode())
              .putString(GateBehaviorMetrics.KEY,gateMetrics.encode())
              .putString(ContextualMotionProbe.KEY,motionShadow.encode())
              .putString(MortalityLearning.KEY,mortality.encode())
              .putString(GateLifeJournal.CURRENT,gateJournal.encode())
              .putString(LifeStatistics.KEY,lifeStats.encode())
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
            enabled=true;lastFrame=0;postInvalidateOnAnimation();
            // All adaptation happens in the visible world. Inherited motor
            // and membrane weights persist until a lived test is accepted.
            sensorMind.setLearning(true);
        }
        void suspend(){
            // Pause and FLUSH BOTH cognition and physical state before the
            // named-slot screen is allowed to inspect the working profile.
            enabled=false;
            save();
            saveScene();
            lastFrame=0;
        }
        void resetEpisode(){
            shelter.capture(environment);
            shelter.newLife();
            gripObserver.newLife();
            // Only initial boot or actual simulated death restarts the body.
            bestLifeDistance=Math.max(bestLifeDistance,lifeDistance);
            lifeDistance=0;travelSinceAction=0;forwardSinceAction=0;
            environmentEffectSinceAction=0;eventEnergyDeltaSinceAction=0;
            java.util.Arrays.fill(energySinceAction,0f);
            episodic.resetLife(); // Forget transient attempt, NOT learned motor or memories.
            mortality.newLife(); // Keep learned terminal weights and tested evidence.
            previousGripDrive=previousGripHeld=previousOpposedFrames=0;
            gateMetrics.resetLife();
            gateJournal.newLife(); // Retain previous life snapshot, start clean.
            ongoingUnsuccessfulGrip=0;gripRecoveryCooldown=0;
            liveBody.abortTrial();
            creature.reset();surface.reset(creature);
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
                scarcity,null,environment));
            chosenObject=null;decisionBrain.abandon();
            goalTimer=0;contactsThisLife=0;worldTime=0;
            reserve=Math.max(70,Math.min(92,reserve));
            liveStartX=creature.cx;liveStartY=creature.cy;
            liveStartEnergy=reserve;liveStartContacts=physicalContacts;
            sensorMind.resetEpisode(SensorimotorMind.Senses.read(
                creature,surface,chamber,environment,reserve));
            sensorMind.updateBodySense(creature,surface);
            sensorMind.updateDistributedSense(creature,surface,chamber,environment);
                sensorMind.updateImpedanceSense(creature,surface);
            // The newborn sees from actual membrane sites, never a centroid eye.
            sensorMind.updateSpatialSense(creature,surface,chamber,environment);
            sensorMind.updateNavigationSense(creature,chamber,environment);
            heading=(float)Math.atan2(sensorMind.motorY(),sensorMind.motorX());
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
                episode,creature.cx,creature.cy,scarcity,chamber,environment));
            excludeOrdinaryRoomObjects();
            // Oldest unengaged objects are recycled first; a touched or
            // currently gripped square never vanishes merely due to cap 24.
            HabitatContinuity.enforceLimit(environment,creature,surface,
                chamber==null?null:chamber.food);
            if(chosenObject!=null&&!environment.contains(chosenObject)){
                decisionBrain.abandon();
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
                (chamber==null||chamber.reachedEnergy))
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
                        decisionBrain.learn(object,reserve,reserve,shift);
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
                float nearest=Float.POSITIVE_INFINITY;
                for(int k=0;k<MotorBrain.N;k++){
                    float d=(float)Math.hypot(object.x-creature.x[k],object.y-creature.y[k]);
                    if(d<nearest)nearest=d;
                }
                for(int k=0;k<Morphology.POINTS;k++){
                    float d=(float)Math.hypot(object.x-surface.x[k],object.y-surface.y[k]);
                    if(d<nearest)nearest=d;
                }
                if(MaterialGeometry.touches(creature,surface,object.x,object.y,
                        object.radius+2f)||nearest<18+object.radius){
                    float before=reserve;
                    float delta=object.touch(dx/Math.max(1,dist),dy/Math.max(1,dist));
                    reserve=Math.max(0,Math.min(100,reserve+delta));
                    lifeStats.contact(reserve-before); // actual clamped contact outcome
                    mortality.energyFlows(0,0,Math.max(0,before-reserve),
                        Math.max(0,reserve-before));
                    // Only actual physical contact, never passive hunger,
                    // determines positive/negative autobiographical valence.
                    eventEnergyDeltaSinceAction+=reserve-before;
                    // Actual feedback trains the self-selected motor-action
                    // history; no color, target angle or food flag passed.
                    sensorMind.contactOutcome(reserve-before,object.signature);
                    energySinceAction[object.signature]+=reserve-before;
                    mind.observe(object.signature,delta,0);
                    decisionBrain.learn(object,before,reserve,0);
                    if(delta>0)energyEaten++;
                    contactsThisLife++;chosenObject=null;goalTimer=0;
                    lastEvent="STIMOLO "+(object.signature+1)+"  ENERGIA "+(delta>=0?"+":"")+(int)delta;
                    eventTime=4;
                    if(delta<0){
                        // Damage slows actual distributed material, not the
                        // read-only virtual observation point.
                        for(int i=0;i<MotorBrain.N;i++){
                            creature.vx[i]*=.6f;creature.vy[i]*=.6f;
                        }
                        MaterialGeometry.observe(creature);
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
                if(eventEnergyDeltaSinceAction<0){
                    episodic.action(sensorMind.stateCopy(),sensorMind.action);
                    if(episodic.consequenceSeparated(0,energySinceAction,
                       lastAppliedMotor,lastAppliedHand,lastAppliedMembrane,false))save();
                }
                // Close ONE measured life only on real death, never on pause,
                // scene expansion, app restart or switching named save slots.
                // Freeze the last real motor choice (possibly shorter than 0.65s)
                // BEFORE death clears body and transient sensorimotor traces.
                sensorMind.recordTerminal(SensorimotorMind.Senses.read(
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
            if(resetNotice>0)resetNotice-=dt;
            if(simulationRunning){
                for(int i=0;i<speed;i++){
                    float slice=Math.min(dt,.035f);
                    float energyBeforeFrame=reserve;
                    int lifeStamp=episode;
                    updateHabitat(slice);
                    // ACTION is sampled from learned sensory-action
                    // values. There is no atan2(food), target teleport or
                    // fixed rule that pulling an opened gate is "correct".
                    float mx=sensorMind.motorX(),my=sensorMind.motorY();
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
                    lastAppliedMotor=liveBody.weights(0,trainer.best);
                    lastAppliedHand=liveBody.weights(1,handTrainer.best);
                    lastAppliedMembrane=liveBody.weights(2,shapeTrainer.best);
                    // Prevent fast material masses from skipping through a
                    // square between consecutive frames. Snapshot before any
                    // physical motion, after routine world streaming.
                    System.arraycopy(surface.x,0,squarePrevSkinX,0,Morphology.POINTS);
                    System.arraycopy(surface.y,0,squarePrevSkinY,0,Morphology.POINTS);
                    System.arraycopy(creature.x,0,squarePrevBodyX,0,MotorBrain.N);
                    System.arraycopy(creature.y,0,squarePrevBodyY,0,MotorBrain.N);
                    float oldCoreX=creature.cx,oldCoreY=creature.cy;
                    creature.step(lastAppliedMotor,mx,my,slice,lastAppliedHand,px,py);
                    surface.step(creature,lastAppliedMembrane,px,py,slice,
                        sensorMind.adhesion());
                    if(chamber!=null){
                        float doorBefore=chamber.doorY;
                        chamber.constrain(creature,surface,slice,sensorMind.adhesion());
                        gripObserver.gateFrame(surface,chamber,sensorMind.adhesion(),
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
                            if(gripBank.observe(lastAppliedHand,lastAppliedMembrane,
                                true,opposedNow,
                                Math.max(0,actuallyMoved)))save();
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
                        if(ongoingUnsuccessfulGrip>=120 &&
                           gripRecoveryCooldown==0 &&
                           !liveBody.testingCandidate()){
                            GripSkillBank.Pair historical=gripBank.retrieve(
                                handTrainer.best,shapeTrainer.best);
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
                    float ddx=creature.cx-beforeX,ddy=creature.cy-beforeY;
                    float physicalTravel=(float)Math.hypot(ddx,ddy);
                    if(Float.isFinite(physicalTravel)&&physicalTravel<80){
                        lifeDistance+=physicalTravel;totalTravel+=physicalTravel;
                        travelSinceAction+=physicalTravel;
                        forwardSinceAction+=ddx*mx+ddy*my;
                        bestLifeDistance=Math.max(bestLifeDistance,lifeDistance);
                    }
                    // The smaller square puzzle has real static collisions
                    // and requires independent opposing adhesive contacts.
                    // Unlike round disks, pressing a box cannot translate it.
                    boolean anyBox=false;
                    for(UnknownWorld.Entity e:environment)if(e.isBox()){anyBox=true;break;}
                    if(anyBox&&chamber==null)
                        java.util.Arrays.fill(surface.touchSense,0f);
                    for(UnknownWorld.Entity e:environment){
                        if(!e.isBilateralBox())continue;
                        gripObserver.beforeBox(surface,e);
                        SquareGripPuzzle.Outcome grips=SquareGripPuzzle.constrain(
                            creature,surface,e,slice,
                            squarePrevSkinX,squarePrevSkinY,
                            squarePrevBodyX,squarePrevBodyY,
                            oldCoreX,oldCoreY);
                        gripObserver.boxFrame(surface,e,sensorMind.adhesion(),
                            elapsedLife,slice);
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
                    // Read centroid from ACTUAL muscle material, not a
                    // simulated nucleus. Collision projection may move it.
                    MaterialGeometry.observe(creature);
                    // Restore the union of physically valid object-owned holds
                    // for vision and next-frame tactile input only.
                    GripSurfaceComposition.compose(surface,chamber,environment);
                    // ZERO2 shadow: actual 96-site, simultaneously composed
                    // physics-owned holds sampled at every 35ms substep,
                    // including grips shorter than the ~650ms decision tick.
                    // This cannot command a hold or modify physical forces.
                    sensorMind.sampleGripPhysicsFrame(surface);
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
                        if(motorGuard.recoveryActivations>lastSkillStall){
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
                        mortality.energyFlows(0,0,0,Math.max(0,reserve-prior));
                        lifeStats.roomSolved();
                        eventEnergyDeltaSinceAction+=reserve-prior;
                        sensorMind.contactOutcome(reserve-prior,chamber.food.signature);
                        energySinceAction[chamber.food.signature]+=reserve-prior;
                        int gained=(int)(reserve-prior);
                        energyEaten++;chambersCleared++;
                        gateJournal.reward(creature,chamber,elapsedLife,reserve-prior);
                        decisionBrain.learn(chamber.food,prior,reserve,0);
                        chamberMessage="SCOPERTA RARA • RISERVA RIPRISTINATA";
                        lastEvent="SCOPERTA RARA • ENERGIA +"+gained;
                        eventTime=7;
                        nextChamber=elapsedLife+40;
                        save();
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
                    // Real temporal observation AFTER collisions, hunger
                    // and any energy actually collected.
                    sensorMind.setLearning(true);
                    boolean actedFromMemory=episodic.physicallyReplaying();
                    long beforeExperience=sensorMind.experiences;
                    // Snapshot the PREVIOUS decision before the model chooses
                    // the next one. Its sensory state is what ZERO actually saw.
                    float[] observedBefore=sensorMind.stateCopy();
                    int actionBefore=sensorMind.action;
                    sensorMind.observe(SensorimotorMind.Senses.read(
                        creature,surface,chamber,environment,reserve),slice,
                        environmentEffectSinceAction,creature,surface,
                        chamber,environment);
                    if(sensorMind.experiences>beforeExperience){
                        mortality.observed(reserve/100f);
                        episodic.action(observedBefore,actionBefore);
                        int recallsBefore=episodic.repeatEffects;
                        boolean remembered=episodic.consequenceSeparated(
                            environmentEffectSinceAction,energySinceAction,
                            lastAppliedMotor,lastAppliedHand,lastAppliedMembrane,
                            actedFromMemory);
                        if(episodic.repeatEffects>recallsBefore)lifeStats.confirmedRecall();
                        if(remembered||episodic.takeDirty()||
                           sensorMind.takeTemporalSkillsDirty())save();
                        // Remembering an action never substitutes an old
                        // physical controller or interrupts body experiments.
                        OnlineBodyLearning.Result result=liveBody.observe(
                            sensorMind.lastReward,travelSinceAction,forwardSinceAction,
                            Math.max(0,physicalContacts-liveStartContacts),
                            liveStartEnergy,reserve,journey.takeNewCells(),
                            liveStartX,liveStartY,creature.cx,creature.cy,
                            chamber!=null,mx,my,
                            trainer.best,handTrainer.best,shapeTrainer.best,
                            environmentEffectSinceAction);
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
            // Lightweight periodic checkpoint for app/task interruptions.
            // Normal neural saves remain more frequent than physical writes.
            if(simulationRunning&&lastSave+12000000000L<now){
                save();saveScene();lastSave=now;
            }
            c.save();float scale=getWidth()/W;c.scale(scale,scale);
            renderWorld(c);
            renderBody(c);
            renderHud(c);
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
                        paint.setShader(new RadialGradient(x,y,34f,
                            new int[]{0x5530d9ce,0x2530d9ce,0x0030d9ce},
                            null,Shader.TileMode.CLAMP));
                        c.drawRoundRect(x-34,y-34,x+34,y+34,8,8,paint);
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
        void renderHud(Canvas c){
            rounded(c,12,12,468,145,0xeb0a1420,20);
            rounded(c,27,28,31,55,TEAL,2);
            text(c,"O V E R M I N D",43,39,14,TEAL,false);
            text(c,"ZERO / LASTRE 0124",26,72,21,0xffe9faf6,false);
            rounded(c,339,104,459,138,0xff20444a,11);
            text(c,"AGGIORNA",399,127,12,TEAL,true);
            rounded(c,217,104,333,138,0xff283b58,11);
            text(c,"CERVELLO",275,127,12,0xffd2c4fc,true);
            text(c,"AZIONI AUTONOME • PREVISIONI APPRESE",27,99,11,MUTED,false);
            text(c,"VERSIONE "+installedVersion+"  •  OTA",27,120,10,0xff708d9d,false);
            text(c,"TENTATIVI",377,34,11,MUTED,false);
            text(c,String.valueOf(trainer.attempts),380,71,22,GOLD,false);
            rounded(c,13,158,467,232,0xca0e1a25,17);
            text(c,"MOVIMENTO REALE • NETTO / PERCORSO",27,182,11,0xffb5d7d6,false);
            text(c,"NETTO "+(int)journey.netDistance+" U  •  VIA "+(int)lifeDistance+" U",
                27,210,19,0xffeffff8,false);
            text(c,"MAX "+(int)journey.recordNet,363,181,10,GOLD,false);
            text(c,"VITA "+LifeStatistics.duration(lifeStats.current().seconds),338,225,11,GOLD,false);
            float y=H-168;
            rounded(c,12,y,468,H-12,0xe00d1923,21);
            text(c,"APPRENDIMENTO DA ZERO",25,y+25,13,TEAL,false);
            text(c,"MONDO + CORPO + PRESA • APPRENDIMENTO ON",25,y+45,11,0xffacc5ca,false);
            button(c,18,y+61,100,H-26,"PARTITE",GOLD);
            button(c,106,y+61,188,H-26,"ANALISI",TEAL);
            button(c,194,y+61,276,H-26,"STATISTICHE",TEAL);
            button(c,282,y+61,364,H-26,"TEMPO x"+speed,0xffbfdaec);
            button(c,370,y+61,463,H-26,"PROVE",GOLD);
            text(c,simulationRunning?"ESPERIENZE SENSORIALI "+sensorMind.experiences+
               "  •  DECISIONI "+sensorMind.decisions:"SIMULAZIONE IN PAUSA",24,255,11,MUTED,false);
            rounded(c,25,276,456,287,0xff273746,5);
            rounded(c,25,276,25+431*reserve/100f,287,reserve>30?TEAL:0xffdf7591,5);
            text(c,"ENERGIA "+(int)reserve+"/100",25,307,12,0xffe0f7ed,false);
            text(c,"DISCHI "+pushes+"  •  BLOCCHI "+boxesUncovered,260,307,11,GOLD,false);
            String windText=shelter.storm(worldTime)?"TEMPESTA "+(shelter.wind(worldTime)>0?"→":"←"):
                shelter.warning(worldTime)?"VENTO IN ARRIVO":"VENTO FRA "+(int)shelter.untilStorm(worldTime)+"s";
            text(c,"TESSUTO "+(int)shelter.integrity+"/100  •  "+windText+
                "  •  MATERIALI POSATI "+shelter.placedCount(),25,544,10,
                shelter.storm(worldTime)?0xffe2a6bf:TEAL,false);
            text(c,"URTI "+physicalContacts+"  •  DISTANZA MASSIMA "+(int)biggestDisplacement+" U  •  TENTATIVI MANIP. "+handTrainer.attempts,
                25,329,10,0xffa6c7c9,false);
            if(eventTime>0)text(c,lastEvent,25,350,11,0xffc4e5dc,false);
            text(c,"AZIONE "+(sensorMind.action%8+1)+" / 8  •  PRESA "+
                (sensorMind.adhesion()>0?"ON":"OFF")+
                "  •  ERRORE PREVISIONE "+format(sensorMind.averagePredictionError),
                25,402,10,TEAL,false);
            // Older stimulus-choice model is historical, not the active policy.
            if(chosenObject!=null){
                text(c,"SCELTO DALLA RETE: SEGNALE "+(chosenObject.signature+1)
                    +"  •  PREVISIONE "+format(decisionBrain.lastPrediction),
                    25,398,10,0xffaac3cb,false);
            }
            text(c,"ZONE "+journey.lifeCells+" • ABILITA "+skillBank.count()+
                " • RICORDI "+episodic.stored()+
                " (+"+episodic.positiveCount()+"/-"+episodic.negativeCount()+")"
                +" • RICHIAMI "+episodic.replayed+
                (episodic.physicallyReplaying()?" • RICORDO ATTIVO":""),
                25,377,10,0xffb5d7d6,false);
            text(c,"MEMBRANA "+Morphology.POINTS+"  •  ESTENSIONE "+
                  (int)surface.peakExtension+" U  •  PROVE PRESA "+
                  shapeTrainer.gateAttempts,
                  25,421,11,GOLD,false);
            text(c,(scarcity?"SCARSITA' ATTIVA":"SCORTE IN ESAURIMENTO")+
                "  •  ENERGIE PRESE "+energyEaten+"  •  STANZE RISOLTE "+chambersCleared,
                25,443,11,scarcity?0xffef9f91:TEAL,false);
            if(chamber!=null){
                text(c,"PORTA "+(int)chamber.doorY+" U  •  PRESE "+
                    (gripsEver+chamber.grasps)+"  •  DUE LATI "+
                    chamber.opposedContactFrames,25,465,11,GOLD,false);
                text(c,"TOCCHI "+chamber.tactileFrames+"  •  ADESIONI "+
                    chamber.adhesiveContactFrames+"  •  SENZA PRESA "+
                    chamber.dualTouchNoGrip,25,486,10,TEAL,false);
                text(c,"PRESA SCELTA "+chamber.gateDriveFrames+" • COMANDO "+
                    chamber.gateCommandFrames+" • TENUTA "+chamber.gateHeldFrames+
                    " • TECNICHE "+gripBank.count(),25,504,10,GOLD,false);
            }else text(c,"TOCCA QUI PER ATTIVARE TEST STANZA E FAME",25,465,11,GOLD,false);
            if(uiTime<updaterStatusUntil&&updaterStatus.length()>0){
                rounded(c,13,338,467,387,0xef1f3944,13);
                text(c,updaterStatus.length()>52?updaterStatus.substring(0,52):updaterStatus,240,368,11,0xffd5fff4,true);
            }
            rounded(c,402,238,464,269,0xff233944,9);
            text(c,simulationRunning?"Ⅱ":"▶",433,259,18,0xffe0f7f3,true);
            if(showBenchmark){
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
                    (int)liveBody.lastCandidateNet+" U",240,top+162,14,0xffd9e8e6,true);
                text(c,liveBody.lastDomain<0?"NESSUNA PROVA COMPLETATA":
                    liveBody.lastWasAccepted?"VARIANTE CONSERVATA":"VARIANTE SCARTATA",
                    240,top+195,14,liveBody.lastWasAccepted?TEAL:GOLD,true);
                text(c,"Finestre reali successive, NON A/B identico.",240,top+229,11,MUTED,true);
                text(c,"TOCCA PER CHIUDERE",240,top+278,11,MUTED,true);
            }
            if(resetNotice>0)text(c,"TOCCA DI NUOVO IL TITOLO PER AZZERARE LA MEMORIA",240,289,11,GOLD,true);
            if(showHelp&&mind.total<3)
                text(c,"Non sa quali oggetti aiutano o danneggiano.",240,355,12,0xffb9cfd2,true);
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
            if(showBenchmark){showBenchmark=false;return true;}
            if(y>442&&y<480&&x<380){
                activateChamber(true);
                invalidate();return true;
            }
            if(y>=H-108){
                if(x<104){
                    save();
                    saveScene();
                    GameActivity.this.startActivity(new android.content.Intent(
                        GameActivity.this,SaveSlotsActivity.class));
                }else if(x<192){
                    save();
                    GameActivity.this.startActivity(new android.content.Intent(
                        GameActivity.this,BrainToolsActivity.class));
                }else if(x<280){
                    save();saveScene();
                    GameActivity.this.startActivity(new android.content.Intent(
                        GameActivity.this,StatisticsActivity.class));
                }else if(x<368){speed=speed==1?5:speed==5?20:1;}
                else compare();
                return true;
            }
            if(y>=104&&y<145&&x>=339){
                updater.checkAndInstall();
                return true;
            }
            if(y>=104&&y<145&&x>=217&&x<334){
                save();
                GameActivity.this.startActivity(
                    new android.content.Intent(GameActivity.this,BrainStatsActivity.class));
                return true;
            }
            if(y<137&&x<217){
                save();
                GameActivity.this.startActivity(new android.content.Intent(
                    GameActivity.this,BrainToolsActivity.class));
                return true;
            }
            if(y>234&&y<280&&x>398){
                simulationRunning=!simulationRunning;lastFrame=0;return true;
            }
            showHelp=false;
            return true;
        }
    }
}
