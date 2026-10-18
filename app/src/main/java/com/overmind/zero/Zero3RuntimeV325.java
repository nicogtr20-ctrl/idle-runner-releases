package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * ZERO 3.25 — FIRST DEDICATED V3 SESSION OWNER (research mode).
 *
 * Unlike the regression drivers this runtime itself owns:
 * the single issuing authority, actor, motor cortex, grip neurons,
 * the 96-site/74-inner actual solver, physics-owned objects, room,
 * energy ledger, decision-boundary journal and atomic whole-life save.
 *
 * Deliberately NOT wired to 2.2 GameActivity/onPause, save slots or OTA.
 * Android integration must be an opt-in V3 entrypoint, not a replacement
 * of living legacy brains. Not G3 locomotion or G5 world-model proof.
 */
public final class Zero3RuntimeV325 implements AutoCloseable {
    public static final int SUBSTEPS=12;
    /**
     * Transitional V3.27 safeguard, NOT the 2000+ experiences goal.
     * V322B decoder rejects >256 full sensory/action records, and the
     * nested V324 archive is currently restricted to 8MiB. Stop at a
     * *verified completed decision* before attempting undurable growth.
     * V3.28 must implement chunked replay with atomic generation linkage.
     */
    public static final int MAX_DURABLE_DECISIONS=240;
    public static final float DT=.025f;
    private static final String OWNER=Zero3AutonomousLoopV317.OWNER;
    private static final String OWNER_GRU_V347="zero3-v347-gru64-actual";
    private static final long ENCODER_SEED=0x31720261017L;
    private static final long GRIP_SEED=0x31920261017L;
    private final long episode;
    private final Zero3AutonomousLoopV317.Encoder encoder;
    private final Zero3LearningFoundation.AdaptiveCortex cortex;
    private final Zero3ContextAdhesionV319 grip;
    private final Zero3LearningFoundation.Coordinator coordinator;
    private Zero3MotorIntentV316.Authority authority;
    private final MotorBrain.Body body;
    private final Morphology.Surface skin;
    private final SymbioteMatterV1 gel;
    private final ArrayList<UnknownWorld.Entity> world;
    private ScarcityLab room;
    private final ShelterEcology shelterV354=new ShelterEcology();
    private float weatherLossV354,nextRoomV354=150f;
    private boolean windDisturbanceV354;
    private final Zero3SurvivalLedgerV323 metabolism;
    private final ArrayList<Zero3OrganismStoreV322.Trace> trace;
    private List<Zero3FullCausalExperienceV322B> replay;
    private long rngState;
    private int next;
    private float[] last64;
    private boolean inDecision,closed,ended;
    // V330 read-only outcome; never part of neural learning or durable checkpoint.
    private Zero3MotorEvidenceV330.Measure lastMotorEvidence;
    // V344 immutable REAL metabolic outcome, view-only. Not a second actor,
    // not reward shaping, and not a new checkpoint field.
    private Zero3CausalVitalBoundaryV344.After lastVitalOutcome;
    // Opt-in only. Old V324 runtime and its research validation remain
    // restricted to 240; V328 owns separate manifests, not legacy slots.
    private boolean segmented;
    private Zero3SegmentedArchiveV328 segmentedStore;
    // V346 explicit in-memory G1 lab only, NEVER an APK default.
    private boolean jointPhysicsResearchV346,jointPhysicsResearchV354;
    private Zero3RecurrentBrain gruResearchV347;
    private long jointAttemptsV346,jointRepairedV346,jointUnresolvedV346;

    private Zero3RuntimeV325(long episode,long seed,MotorBrain.Body body,
          Morphology.Surface skin,SymbioteMatterV1 gel,
          ArrayList<UnknownWorld.Entity> world,ScarcityLab room,
          Zero3SurvivalLedgerV323 metabolism,int cursor,
          List<Zero3OrganismStoreV322.Trace> traces,
          List<Zero3FullCausalExperienceV322B> experiences,
          float[] previous,boolean restore,float motorLearningRate)throws IOException {
        if(episode<=0||seed==0||body==null||skin==null||gel==null||
           world==null||room==null||metabolism==null||cursor<0||
           traces==null||experiences==null||
           traces.size()!=cursor||experiences.size()!=cursor||
           !world.contains(room.food)||
           (previous==null&&cursor>0)||(previous!=null&&previous.length!=64))
            throw new IOException("V325 incomplete runtime ownership");
        this.episode=episode;this.rngState=seed;
        this.body=body;this.skin=skin;this.gel=gel;
        this.world=world;this.room=room;this.metabolism=metabolism;
        this.trace=new ArrayList<>(traces);
        // V329 disk-backed journals must never be copied into RAM on cold load.
        this.replay=experiences instanceof Zero3ReplayJournalV329
            ? experiences:new ArrayList<>(experiences);
        this.next=cursor;
        this.last64=previous==null?null:previous.clone();
        encoder=new Zero3AutonomousLoopV317.Encoder(ENCODER_SEED);
        cortex=new Zero3LearningFoundation.AdaptiveCortex(motorLearningRate);
        grip=new Zero3ContextAdhesionV319(GRIP_SEED,true);
        coordinator=new Zero3LearningFoundation.Coordinator(cortex);
        encoder.resetEpisode();
        // Unique active issuer is in-process; checkpoint stores the cursor,
        // which a NEW issuer resumes without replaying credited decisions.
        Zero3MotorIntentV316.Authority oneIssuer=
            new Zero3MotorIntentV316.Authority(episode,OWNER);
        try{
            if(restore)oneIssuer.restoreCompletedDecisions(next);
        }catch(RuntimeException invalidResume){
            oneIssuer.close();
            throw new IOException("V325 invalid authority cursor",invalidResume);
        }
        authority=oneIssuer;
        ended=metabolism.reserve()<=0;
    }
    private static void require(boolean ok,String reason){
        if(!ok)throw new IllegalStateException("ZERO3 V325 "+reason);
    }
    /** The caller supplies physical birth/warmup and real object identities. */
    public static Zero3RuntimeV325 newborn(long episode,long rng,
            MotorBrain.Body b,Morphology.Surface skin,SymbioteMatterV1 gel,
            List<UnknownWorld.Entity> entities,ScarcityLab room,
            float initialEnergy,float elapsedLife)throws IOException {
        if(room==null||entities==null||!entities.contains(room.food)||initialEnergy<=0||
           initialEnergy>100||elapsedLife<0)
            throw new IOException("V325 newborn needs physical room food and positive fuel");
        return new Zero3RuntimeV325(episode,rng,b,skin,gel,
            new ArrayList<>(entities),room,
            new Zero3SurvivalLedgerV323(initialEnergy,elapsedLife,elapsedLife,
                                        elapsedLife>=70f),
            0,Collections.emptyList(),Collections.emptyList(),null,false,.013f);
    }
    /** SCIENTIFIC A/B ONLY: zero plasticity freezes cortical weights while
     * preserving initial weights, exploration, actor and trained V319 grip.
     * Never invoked by the Android Activity or regular checkpoint restore.
     */
    public static Zero3RuntimeV325 newbornMotorBench(long episode,long rng,
            MotorBrain.Body b,Morphology.Surface s,SymbioteMatterV1 gel,
            List<UnknownWorld.Entity> entities,ScarcityLab room,
            float initialEnergy,float motorLearningRate)throws IOException{
        if(room==null||entities==null||!entities.contains(room.food)||
           (motorLearningRate!=0f&&motorLearningRate!=.013f)||
           initialEnergy<=0||initialEnergy>100)
            throw new IOException("V330 requires paired real motor experiment");
        return new Zero3RuntimeV325(episode,rng,b,s,gel,new ArrayList<>(entities),
            room,new Zero3SurvivalLedgerV323(initialEnergy,0f,0f,false),
            0,Collections.emptyList(),Collections.emptyList(),null,false,motorLearningRate);
    }
    /** Validate a WHOLE checkpoint before allocating a fresh neural owner. */
    public static Zero3RuntimeV325 recovered(Zero3RoomArchiveV324.Bundle bundle)
            throws IOException{
        if(bundle==null)throw new IOException("missing V325 V324 archive");
        Zero3OrganismStoreV322.State state=bundle.life.joint.organism;
        Zero3OrganismStoreV322.Physical physical=state.physical();
        Zero3RoomStateV324.Restored restored=bundle.room.restore(physical.world);
        Zero3SurvivalLedgerV323.State metabolismState=bundle.life.survival;
        Zero3SurvivalLedgerV323 fuel=
            new Zero3SurvivalLedgerV323(100f,0f,0f,false);
        fuel.restore(metabolismState);
        if(fuel.decisions()!=state.nextDecision||
           bundle.life.joint.actualWorldRngState()!=state.lastRngState)
            throw new IOException("mixed V325 authority/energy/world generation");
        Zero3RuntimeV325 runtime=new Zero3RuntimeV325(state.episodeId,
            state.lastRngState,physical.body,physical.skin,physical.gel,
            restored.objects,restored.room,fuel,state.nextDecision,
            Arrays.asList(state.trace()),bundle.life.joint.experiences(),
            state.last64(),true,.013f);
        try {
            state.restoreModules(runtime.encoder,runtime.cortex,runtime.grip);
            require(runtime.encoder.updates()==runtime.next&&
                    runtime.grip.updates()==runtime.next&&
                    runtime.cortex.updates()<=runtime.next,
                    "neural learning counters do not match restored authority");
            return runtime;
        }catch(IOException|RuntimeException failure){
            runtime.close();
            throw new IOException("V325 whole-life cold restore failed",failure);
        }
    }
    /** Load from the isolated V3 storage folder only, never a legacy slot. */
    public static Zero3RuntimeV325 load(Path sandbox)throws IOException{
        Zero3RoomArchiveV324 disk=new Zero3RoomArchiveV324(sandbox);
        Zero3RoomArchiveV324.Bundle bundle=disk.load();
        return bundle==null?null:recovered(bundle);
    }
    /**
     * Read-only defensive rendering data. No privileged world knowledge is
     * delivered to the neural actor; UI never alters the physical body.
     * Obtained on the runtime owner thread at a completed decision boundary.
     */
    public static final class Display {
        public final long episode,solverTicks;
        public final int decisions,neurons,objects,residentReplay,unsavedReplay,neuralInputs;
        public final float reserve,priorDamage,cx,cy,doorX,doorY;
        public final boolean dead,hasMotorOutcome,hasVitalOutcome;
        public final float lastEnergyNet,lastEnergyGained,lastEnergyBasal,
                           lastEnergyMotor,lastEnergyDamage;
        public final int lastEnergySources;
        public final float lastIntentX,lastIntentY,actualMotorDX,actualMotorDY;
        public final float lastSignedProgress,lastInertiaProjection,lastMuscleCancellation;
        public final float[] membraneX,membraneY,objectX,objectY;
        public final int[] objectLaw,objectColor;
        public final float[] objectRadius,objectAngle,objectHalfLength;public final boolean[] objectVisible;
        private Display(Zero3RuntimeV325 r){
            episode=r.episode;solverTicks=r.gel.ticks;
            decisions=r.next;neurons=r.gruResearchV347==null?
                Zero3AutonomousLoopV317.HIDDEN:Zero3RecurrentBrain.HIDDEN;
            neuralInputs=r.neuralInputWidth();
            objects=r.world.size();reserve=r.metabolism.reserve();
            residentReplay=r.residentFullExperiences();
            unsavedReplay=r.unsavedFullExperiences();
            priorDamage=r.metabolism.previousDecisionDamage();
            dead=r.ended;cx=r.body.cx;cy=r.body.cy;
            Zero3MotorEvidenceV330.Measure m=r.lastMotorEvidence;
            hasMotorOutcome=m!=null;
            Zero3CausalVitalBoundaryV344.After vital=r.lastVitalOutcome;
            hasVitalOutcome=vital!=null;
            // Numerical zeros are only valid with hasVitalOutcome=true.
            // After restoring a V328 snapshot the report remains unavailable
            // until an actual new physical decision is committed.
            lastEnergyNet=vital==null?0f:vital.netEnergy;
            lastEnergyGained=vital==null?0f:vital.actualGained;
            lastEnergyBasal=vital==null?0f:vital.actualBasal;
            lastEnergyMotor=vital==null?0f:vital.actualMotor;
            lastEnergyDamage=vital==null?0f:vital.actualDamage;
            lastEnergySources=vital==null?0:vital.consumedSources;
            lastIntentX=m==null?0f:m.directionX;
            lastIntentY=m==null?0f:m.directionY;
            actualMotorDX=m==null?0f:m.dx;
            actualMotorDY=m==null?0f:m.dy;
            lastSignedProgress=m==null?0f:m.signedAlongIntent;
            lastInertiaProjection=m==null?0f:m.inertiaProxyAlongIntent;
            lastMuscleCancellation=m==null?0f:m.cancellationProxy;
            doorX=r.room.gateX();doorY=r.room.gateCenterY();
            membraneX=r.skin.x.clone();membraneY=r.skin.y.clone();
            objectX=new float[objects];objectY=new float[objects];
            objectLaw=new int[objects];objectColor=new int[objects];objectRadius=new float[objects];objectAngle=new float[objects];objectHalfLength=new float[objects];objectVisible=new boolean[objects];
            for(int i=0;i<objects;i++){
                UnknownWorld.Entity e=r.world.get(i);
                objectX[i]=e.x;objectY[i]=e.y;objectLaw[i]=e.physicalLaw();objectAngle[i]=e.angle;objectRadius[i]=e.radius;objectHalfLength[i]=e.isConstruction()?PanelPhysics.halfLength(e):0;
                objectVisible[i]=!e.concealed&&!e.consumed;
                objectColor[i]=e.isBox()?0xff243340:e.isMovable()?0xffa5b4c7:e.isRare()?0xffb17bff:e.signature==0?0xff4da7ff:e.signature==1?0xffff516a:0xff7dd9d1;
            }
        }
    }
    public Zero3DashboardSnapshotV354 dashboardV354(boolean graph)throws IOException{
        require(!closed&&!inDecision,"dashboard boundary");
        float[] ix=new float[74],iy=new float[74];System.arraycopy(body.x,0,ix,0,10);System.arraycopy(body.y,0,iy,0,10);
        System.arraycopy(gel.x,0,ix,10,64);System.arraycopy(gel.y,0,iy,10,64);
        Zero3CognitionV353.Snapshot p=cognitionSnapshot();
        Zero3FullCausalExperienceV322B last=next==0?null:replay.get(next-1);
        int units=gruResearchV347==null?20+64+16:64+5+2+(gruResearchV347.variableClock()?2:0)+gruResearchV347.sensorWidth()+64+16+
            (p==null?0:p.predictiveUnits+36+(p.shadowUnits==0?0:p.shadowUnits+36))+(completionModeV354()?1:0);
        return new Zero3DashboardSnapshotV354(display(),p,executiveSnapshot(),graph&&gruResearchV347!=null?neuralGraphV354():null,
            gruResearchV347==null?null:gruResearchV347.plannerSnapshotV354(),completionModeV354()&&shelterV354.storm(metabolism.worldTimeV354()),
            completionModeV354()&&shelterV354.warning(metabolism.worldTimeV354()),completionModeV354()?shelterV354.wind(metabolism.worldTimeV354()):0,
            completionModeV354()?shelterV354.integrity:Float.NaN,room.collisionRectanglesV354(),metabolism.worldTimeV354(),
            observationModeV354(),gruLearningUpdatesV347(),gruResearchV347==null?0:gruResearchV347.validations(),observationDecisionsV354(),units,
            ix,iy,skin.touchSense,skin.gripHold,skin.gripCommand,skin.touchPulse,last64,
            last==null?null:last.neuralBeforeV351(),last==null?null:last.neuralAfterV351(),jointPhysicsCountersV346());
    }
    public Zero3NeuralGraphV354 neuralGraphV354(){
        require(!closed&&!inDecision&&gruResearchV347!=null,"graph at a completed GRU boundary");
        Zero3NeuralGraphV354.Builder b=new Zero3NeuralGraphV354.Builder();
        gruResearchV347.appendGraphV354(b);cortex.appendGraphV354(b);grip.appendGraphV354(b);return b.build();
    }
    /**
     * V355: rendering-only bridge to the ORIGINAL GameActivity painter.
     * All references belong to this organism. The single Android UI thread
     * may read them only outside advanceOneDecision(); it must never call a
     * physics, neural, world or mutating method on any of these objects.
     * This avoids building a second simulated (2.2) body behind the picture.
     */
    static final class CanvasStateV355 {
        final MotorBrain.Body body;
        final Morphology.Surface skin;
        final SymbioteMatterV1 gel;
        final ArrayList<UnknownWorld.Entity> world;
        final ScarcityLab room;
        final ShelterEcology shelter;
        final float reserve,worldTime;
        final int decisions,positive,negative,consumed;
        final long train,validation,observations;
        final boolean dead;
        CanvasStateV355(Zero3RuntimeV325 r){
            body=r.body;skin=r.skin;gel=r.gel;world=r.world;room=r.room;
            shelter=r.shelterV354;reserve=r.metabolism.reserve();
            worldTime=r.metabolism.worldTimeV354();decisions=r.next;
            positive=r.metabolism.positiveTouches();
            negative=r.metabolism.negativeTouches();
            consumed=r.metabolism.consumedSources();
            train=r.gruLearningUpdatesV347();
            validation=r.gruResearchV347==null?0:r.gruResearchV347.validations();
            observations=r.observationDecisionsV354();dead=r.ended;
        }
    }
    CanvasStateV355 canvasStateV355(){
        require(!closed&&!inDecision&&completionModeV354(),
            "V355 renderer requires one completed 3.0 decision boundary");
        return new CanvasStateV355(this);
    }
    public Display display(){
        require(!closed&&!inDecision,"render only finished physical states");
        return new Display(this);
    }
    public int decisions(){return next;}
    public boolean atArchiveCapacity(){
        return next>=durableDecisionLimit();
    }
    public int durableDecisionLimit(){
        return segmented?Zero3SegmentedArchiveV328.maxFor(
                gruResearchV347!=null):MAX_DURABLE_DECISIONS;
    }
    /** Enable ONLY for an isolated V3 research folder; no V324 migration
     * is performed until the first valid V328 generation is durably written.
     */
    public void enableSegmented(){
        require(!closed&&!inDecision,"segmented mode only at decision boundary");
        segmented=true;
    }
    public long episode(){return episode;}
    public long gelTicks(){return gel.ticks;}
    public float energy(){return metabolism.reserve();}
    public boolean ended(){return ended;}
    public long worldRng(){return rngState;}
    public int objects(){return world.size();}
    public boolean roomFoodCanonical(){return world.contains(room.food);}
    public int contactFrames(){return room.gateContactFrames;}
    public long cortexUpdates(){return cortex.updates();}
    public long gripUpdates(){return grip.updates();}
    public int encoderUpdates(){return encoder.updates();}
    public int completeExperiences(){return replay.size();}
    public Zero3MotorEvidenceV330.Measure lastMotorEvidence(){
        require(!closed&&!inDecision,"V330 outcome only at completed decision");
        return lastMotorEvidence;
    }
    public int residentFullExperiences(){
        return replay instanceof Zero3ReplayJournalV329
            ? ((Zero3ReplayJournalV329)replay).residentRecords():replay.size();
    }
    public int unsavedFullExperiences(){
        return replay instanceof Zero3ReplayJournalV329
            ? ((Zero3ReplayJournalV329)replay).unsavedCount():replay.size();
    }
    public String currentRoomHash()throws IOException{
        return Zero3RoomStateV324.capture(room,world).fingerprint();
    }
    /** Spawn an actual disk with a V3-owned RNG at a COMPLETED boundary.
     * Not a hidden target or prescriptive direction to the neural actor.
     */
    public void spawnDisk(float minDistance)throws IOException {
        require(!closed&&!inDecision&&!ended&&minDistance>0&&world.size()<120,
                "world spawn must be at a complete active boundary");
        rngState=nextRng(rngState);
        float angle=(rngState>>>40)/16777216f*(float)(Math.PI*2);
        rngState=nextRng(rngState);
        float distance=minDistance+(rngState>>>40)/16777216f*12f;
        world.add(new UnknownWorld.Entity(
            body.cx+distance*(float)Math.cos(angle),
            body.cy+distance*(float)Math.sin(angle),2,2));
    }
    /**
     * Controlled WORLD TEST FIXTURE only: materializes a finite real blue
     * resource at one true membrane site, randomly selected by world RNG.
     * Does NOT increase energy directly or inject any preferred motor vector.
     * Physical MaterialGeometry contact and Entity.touch must deliver the
     * actual 22 units on the next solver substep. This artificial abundance
     * invalidates claims of an independently learned survival strategy.
     */
    public void spawnBenchEnergyAtMembrane(){
        require(!closed&&!inDecision&&!ended&&world.size()<120,
                "bench material only at a live physical boundary");
        rngState=nextRng(rngState);
        int patch=(int)((rngState>>>1)%Morphology.POINTS);
        world.add(new UnknownWorld.Entity(skin.x[patch],skin.y[patch],0,0));
    }
    private static long nextRng(long state){
        state^=state<<13;state^=state>>>7;state^=state<<17;
        return state==0?0x3322BBEEFEDL:state;
    }
    private UnknownWorld.Entity firstBox(){
        for(UnknownWorld.Entity e:world)if(e.isBilateralBox())return e;
        return null;
    }
    /**
     * Research-only physics A/B. The default runtime/legacy saves remain
     * unchanged. A V346 physics world cannot be serialized into V324/V328:
     * the mode must first be versioned into a whole-organism checkpoint.
     */
    public void enableJointPhysicsResearchV346(){
        require(!closed&&!inDecision&&!ended&&next==0&&
                !segmented&&!jointPhysicsResearchV346&&gruResearchV347==null,
                "joint contact research only for a fresh isolated organism");
        jointPhysicsResearchV346=true;
    }
    public void enableJointPhysicsResearchV354(){
        require(!closed&&!inDecision&&!ended&&next==0&&!jointPhysicsResearchV346&&gruResearchV347==null,
            "V354 candidate only on a virgin isolated body");jointPhysicsResearchV354=true;
    }
    /** Experimental G4 ONLY; sole true actor, not parallel shadow. */
    public void enableGruResearchV347(){
        require(!closed&&!inDecision&&!ended&&next==0&&
                !jointPhysicsResearchV346&&gruResearchV347==null,
                "GRU actor requires isolated virgin real V3 world");
        Zero3RecurrentBrain newActor=new Zero3RecurrentBrain();
        authority.close();
        authority=new Zero3MotorIntentV316.Authority(episode,OWNER_GRU_V347);
        gruResearchV347=newActor;
    }
    public void enableUnifiedResearchV351(){
        require(!closed&&!inDecision&&!ended&&next==0&&
                !jointPhysicsResearchV346&&gruResearchV347==null,
                "V351 requires isolated virgin real world");
        Zero3RecurrentBrain nextBrain=new Zero3RecurrentBrain(194);
        authority.close();
        authority=new Zero3MotorIntentV316.Authority(episode,OWNER_GRU_V347);
        gruResearchV347=nextBrain;
    }
    public void enableVariableResearchV352(){
        enableUnifiedResearchV351();
        gruResearchV347=new Zero3RecurrentBrain(194,true);
        metabolism.enableVariableClockV352();
    }
    public void enableCognitiveResearchV353(){
        enableVariableResearchV352();gruResearchV347.enableCognitionV353();
    }
    public void enableCompletionV354(){
        enableCognitiveResearchV353();gruResearchV347.enableExecutiveV354();
        world.addAll(WorldPlacement.spawn((int)(rngState&0x7fffffff),body.cx,body.cy,metabolism.scarcity(),room,world,skin));
        HabitatContinuity.excludeRoomOverlap(world,room,body,skin);
    }
    float[] recurrentParametersV354(){return gruResearchV347.parameterSnapshot();}
    float[] corticalParametersV354(){return cortex.weights();}
    float[] adhesionParametersV354(){return grip.captureAtDecisionBoundary().weights();}
    public void inheritCompletionV354(Zero3OrganismStoreV322.State parent)throws IOException{
        require(next==0&&gruResearchV347==null,"inherit only into a virgin physical life");
        if(parent==null||!parent.completionModeV354())throw new IOException("V354 parent checkpoint required");
        Zero3LearningFoundation.AdaptiveCortex oldMotor=new Zero3LearningFoundation.AdaptiveCortex();
        Zero3ContextAdhesionV319 oldGrip=new Zero3ContextAdhesionV319(1,true);
        parent.restoreGruModules(new Zero3AutonomousLoopV317.Encoder(1),oldMotor,oldGrip);
        enableCompletionV354();gruResearchV347=Zero3RecurrentBrain.fromCheckpoint(parent.gruBytes()).inheritedV354(parent.episodeId,rngState);
        cortex.inheritWeightsV354(oldMotor.weights());grip.inheritWeightsV354(oldGrip.captureAtDecisionBoundary().weights());
        if(!shelterV354.restore(gruResearchV347.ecologyStateV354()))throw new IOException("V354 inherited structures");
        world.removeIf(e->e!=room.food);
        shelterV354.rehydrate(world,body.cx,body.cy,room);
        world.addAll(WorldPlacement.spawn((int)(rngState&0x7fffffff),body.cx,body.cy,metabolism.scarcity(),room,world,skin));
    }
    public boolean completionModeV354(){return gruResearchV347!=null&&gruResearchV347.completionModeV354();}
    public void observationModeV354(boolean enabled){require(!inDecision&&!closed&&completionModeV354(),"questa modalità richiede un profilo V354");gruResearchV347.observingV354(enabled);}
    public boolean observationModeV354(){return completionModeV354()&&gruResearchV347.observingV354();}
    public long observationDecisionsV354(){return gruResearchV347==null?0:gruResearchV347.observationsV354();}
    public Zero3ExecutiveV354.Snapshot executiveSnapshot(){return gruResearchV347==null?null:gruResearchV347.executiveSnapshot();}
    public Zero3CognitionV353.Snapshot cognitionSnapshot(){
        require(!closed&&!inDecision,"cognition snapshot only at a completed boundary");
        return gruResearchV347==null?null:gruResearchV347.cognitionSnapshot();
    }
    public float lastDecisionSeconds(){return next==0?0f:(trace.get(next-1).endTick-trace.get(next-1).startTick)*DT;}
    public int neuralInputWidth(){return gruResearchV347==null?20:gruResearchV347.sensorWidth();}
    public long gruLearningUpdatesV347(){
        return gruResearchV347==null?0:gruResearchV347.trainingUpdates();
    }
    Zero3OrganismStoreV322.Trace[] completedTracesV354(){return trace.toArray(new Zero3OrganismStoreV322.Trace[0]);}
    public long[] jointPhysicsCountersV346(){
        return new long[]{jointAttemptsV346,jointRepairedV346,
                          jointUnresolvedV346};
    }
    private void physics(float[] motors,float intendedAdhesion){
        weatherLossV354=0;windDisturbanceV354=false;
        if(completionModeV354()){
            float time=metabolism.worldTimeV354();
                room.reachedEnergy|=room.food.consumed;
                if(room.reachedEnergy&&metabolism.worldTimeV354()>=nextRoomV354&&!HabitatContinuity.roomOccupied(room,body,skin)){
                    ScarcityLab fresh=HabitatContinuity.clearChamber(body.cx,body.cy,world);
                    if(fresh!=null){world.remove(room.food);room=fresh;world.add(room.food);nextRoomV354=metabolism.worldTimeV354()+150f;}
                    else nextRoomV354=metabolism.worldTimeV354()+8f;
                }

            if((int)((time+DT)/13f)>(int)(time/13f)){
                shelterV354.capture(world);HabitatContinuity.pruneDistant(world,body,skin,room.food,600f);
                shelterV354.rehydrate(world,body.cx,body.cy,room);rngState=nextRng(rngState);
                world.addAll(WorldPlacement.spawn((int)(rngState&0x7fffffff),body.cx,body.cy,metabolism.scarcity(),room,world,skin));
                HabitatContinuity.excludeRoomOverlap(world,room,body,skin);HabitatContinuity.enforceLimit(world,body,skin,room.food);
            }
        }
        float[] sx=skin.x.clone(),sy=skin.y.clone();
        float[] bx=body.x.clone(),by=body.y.clone();
        float cx=body.cx,cy=body.cy;
        gel.step(body,skin,motors,DT);
        float[] afterGelX=(jointPhysicsResearchV346||jointPhysicsResearchV354||completionModeV354())?skin.x.clone():null;
        float[] afterGelY=(jointPhysicsResearchV346||jointPhysicsResearchV354||completionModeV354())?skin.y.clone():null;
        if(completionModeV354())room.constrain(body,skin,DT,intendedAdhesion);
        boolean hasBoxes=false;
        for(UnknownWorld.Entity object:world)if(object.isBilateralBox()){
            if(jointPhysicsResearchV346)
                SquareGripPuzzle.constrain(body,skin,object,DT,
                    sx,sy,bx,by,cx,cy,null,false,true,true);
            else SquareGripPuzzle.constrain(body,skin,object,DT,
                                            sx,sy,bx,by,cx,cy);
            hasBoxes=true;
        }
        if(completionModeV354())for(UnknownWorld.Entity panel:world)if(panel.isConstruction())PanelPhysics.constrain(body,skin,panel,DT,sx,sy,bx,by);
        if(hasBoxes)MaterialContactConstraints.reconcile(body,skin,world,DT);
        // Room's real collision volume and all solid boxes/panels have their
        // own owner: no exogenous movement, no scripted locomotion.
        SymbioteWorldStepV171.interior(gel,body,skin,room,world,DT);
        for(UnknownWorld.Entity object:world)if(object.isMovable()){
            Morphology.collide(skin,object,DT);
            ContactLearning.collide(body,object,DT);
            gel.observeBody(body,skin);
        }
        if(!completionModeV354())room.solveGate(skin,DT);
        GripSurfaceComposition.compose(skin,room,world);
        for(UnknownWorld.Entity object:world){
            float x=object.x,y=object.y;object.tick(DT);
            if(completionModeV354()&&object.isBox()&&room.overlapsRoom(object.x,object.y,object.radius)){
                object.x=x;object.y=y;object.vx=object.vy=0;
            }
        }
        if(completionModeV354()){ShelterEcology.separateBoxes(world);for(UnknownWorld.Entity box:world)if(box.isBilateralBox())SquareGripPuzzle.uncover(box,world);}
        if(jointPhysicsResearchV346){
            // MUST be last (including gate + grip composition + box tick).
            for(UnknownWorld.Entity object:world)
                if(object.isBilateralBox()){
                    Zero3JointBoxConstraintV338.Result fix=
                        Zero3JointBoxConstraintV338.solve(
                            skin,object,afterGelX,afterGelY);
                    if(fix.attempted){
                        jointAttemptsV346++;
                        if(fix.repaired)jointRepairedV346++;
                        if(fix.unresolved)jointUnresolvedV346++;
                    }
                }
            require(jointUnresolvedV346==0&&
                    MaterialGeometry.selfCrossings(skin)==0,
                    "V346 real final-WORLD membrane topology unresolved");
            for(UnknownWorld.Entity object:world)
                if(object.isBilateralBox())
                    require(Zero3JointBoxConstraintV338.measure(
                            skin,object).total()==0,
                            "V346 real final-WORLD BOX interior defect");
        }
        if(jointPhysicsResearchV354||completionModeV354()){
            Zero3JointConstraintV354.Result fix=Zero3JointConstraintV354.solve(skin,world,room,afterGelX,afterGelY,sx,sy);
            if(fix.attempted){jointAttemptsV346++;if(fix.repaired)jointRepairedV346++;else jointUnresolvedV346++;}
            require(fix.remaining==0,"V354 simultaneous topology/solid conflict unresolved decision="+next+" tick="+gel.ticks+" defects="+fix.remaining);
            GripSurfaceComposition.compose(skin,room,world);
        }
        if(completionModeV354()){
            ShelterEcology.Exposure weather=shelterV354.step(body,skin,world,room,metabolism.worldTimeV354()+DT,DT);
            weatherLossV354=weather.energyLoss;windDisturbanceV354=weather.active&&weather.exposedFraction>0;
        }
        require(gel.invalid==0,"invalid physical gel after solver");
    }
    /**
     * Exactly one decision, one PRE, 12 actual substeps, one POST, then a
     * journal append/energy commit. Never take a checkpoint mid-decision.
     */
    public void advanceOneDecision()throws IOException {
        require(!closed&&!inDecision&&!ended,"runtime not ready for a decision");
        require(!atArchiveCapacity(),
                "V3 RESEARCH ARCHIVE CAPACITY REACHED: SAVE BEFORE NEXT DECISION");
        require(authority.nextDecision()==next&&
                metabolism.decisions()==next&&
                replay.size()==next&&trace.size()==next,
                "unbalanced pre-decision state");
        inDecision=true;
        try{
            int n=next;
            Zero3SensoryFrameV315 before=Zero3SensoryFrameV315.capture(
                episode,n,gel.ticks*(double)DT,true,body,skin,gel,world,last64);
            // V344: same measured PRE vital features as V327, but their
            // decision/frame/hash lineage is now validated against the one
            // true V323 ledger. This does not alter a single neural input.
            Zero3CausalVitalBoundaryV344.Before vitalBefore=
                Zero3CausalVitalBoundaryV344.before(n,before,metabolism);
            boolean unified=gruResearchV347!=null&&gruResearchV347.sensorWidth()==194;
            Zero3GruSensoryV347.Unified unifiedBefore=unified?
                Zero3GruSensoryV347.unifiedBefore(before,vitalBefore,body,skin,room,world):null;
            boolean variable=gruResearchV347!=null&&gruResearchV347.variableClock();
            boolean cognitive=gruResearchV347!=null&&gruResearchV347.cognitiveMode();
            boolean heldout=cognitive&&(completionModeV354()?Zero3ConfigurationV354.heldout(n):Zero3CognitionV353.heldout(n));
            boolean observe=observationModeV354();
            long selectedOption=0;
            int plannedSteps=SUBSTEPS;
            float[] choice;
            if(gruResearchV347==null){
                encoder.before(before,vitalBefore.fuelRatio,
                               vitalBefore.previousDamage);
                choice=encoder.choose();
            }else{
                Zero3LearningFoundation.MotorIntent proposal=
                    gruResearchV347.choose(
                        unified?unifiedBefore.neural():Zero3GruSensoryV347.before(before,vitalBefore),
                        episode,n,cortex.revision());
                // Only V352/V353 execute the actor duration. Historical cores retain
                // the original 12-step causal contract.
                if(variable)plannedSteps=Math.max(10,Math.min(26,Math.round(proposal.duration/DT)));
                selectedOption=gruResearchV347.selectedOptionV354();
                choice=new float[]{proposal.x,proposal.y,
                                   proposal.effort,proposal.adhesion};
            }
            Zero3MotorIntentV316 intent=authority.issue(
                gruResearchV347==null?OWNER:OWNER_GRU_V347,n,
                (int)cortex.revision(),completionModeV354()?354:325,choice[0],choice[1],
                choice[2],choice[3],plannedSteps*DT,completionModeV354()?gruResearchV347.measuredConfidenceV354():.35f);
            Zero3LearningFoundation.MotorIntent motor=
                new Zero3LearningFoundation.MotorIntent(
                    episode,n,cortex.revision(),choice[0],choice[1],
                    choice[2],choice[3],plannedSteps*DT);
            // PRE momentum is observed from material, never a target direction.
            final float initialVX=body.cvx,initialVY=body.cvy;
            coordinator.begin(motor,body,!heldout&&!observe,heldout||observe,true);
            Zero3MotorEvidenceV330.Accumulator audit=
                new Zero3MotorEvidenceV330.Accumulator(episode,n,
                    motor.x,motor.y,initialVX,initialVY,variable);
            grip.begin(heldout||observe);
            metabolism.begin(n,gel.ticks);
            long actions=0xcbf29ce484222325L;
            ArrayList<float[]> efferences=new ArrayList<>();
            ArrayList<Float> costs=new ArrayList<>();
            long repairsBefore=jointRepairedV346;
            float ambientDebit=0;int disturbanceMask=0;
            double work=0d;
            int gripFrames=0,bilateral=0,actualSteps=0,interruption=0;
            float[] last=null;
            for(int i=0;i<plannedSteps;i++){
                float[] command=coordinator.actuate(body,skin,DT);
                grip.actuate(command,intent,body,skin,world);
                audit.motorSubstep(command,skin,body);
                float[] executed=command.clone();
                efferences.add(executed);
                for(float a:executed){
                    require(Float.isFinite(a)&&Math.abs(a)<=1.00001f,
                            "invalid issued actuator");
                    actions=(actions^Float.floatToIntBits(a))*1099511628211L;
                }
                float priorWork=gel.work;
                physics(command,motor.adhesion);
                float actualCost=Math.max(0f,gel.work-priorWork);
                costs.add(actualCost);work+=actualCost;
                float damageBefore=metabolism.damagedEnergy();
                metabolism.physicalStep(DT,actualCost,body,skin,world,weatherLossV354);
                ambientDebit+=metabolism.weatherDebitV354();if(windDisturbanceV354)disturbanceMask|=1;
                boolean newlyHarmed=metabolism.damagedEnergy()>damageBefore;
                if(completionModeV354())coordinator.observeWithDisturbanceV354(body,DT,actualCost,
                    Math.max(0f,gel.lastMuscleReactionImpulse),windDisturbanceV354?1:0);
                else coordinator.observe(body,DT,actualCost,Math.max(0f,gel.lastMuscleReactionImpulse),false,false);
                UnknownWorld.Entity box=firstBox();
                if(completionModeV354())grip.observeWorldV354(skin,world,room,actualCost);else grip.observe(skin,box,actualCost);
                boolean held=false;
                for(int patch=0;patch<Morphology.POINTS;patch++){
                    if(skin.gripHold[patch]>=Morphology.GRIP_HOLD){
                        boolean boxOwner=false;
                        for(UnknownWorld.Entity e:world)if(e.isBox()&&
                            e.boxGripHold[patch]>=Morphology.GRIP_HOLD)
                            boxOwner=true;
                        boolean gateOwner=room.gateGripHold[patch]>=Morphology.GRIP_HOLD;
                        require(boxOwner||gateOwner,
                                "skin borrowed a physically nonexistent grip");
                        held=true;
                    }
                }
                if(held)gripFrames++;
                if(box!=null&&box.bilateralHeldNow)bilateral++;
                last=executed;actualSteps++;
                if(variable&&(metabolism.reserve()<=0f||newlyHarmed)){
                    interruption=metabolism.reserve()<=0f?2:1;
                    break;
                }
            }
            grip.end();
            Zero3LearningFoundation.Experience result=coordinator.finish(interruption!=0);
            require(result.frames==actualSteps&&result.intent.decisionId==n,
                    "cortex credited incomplete/foreign physical decision");
            Zero3SensoryFrameV315 after=Zero3SensoryFrameV315.capture(
                episode,n+1,gel.ticks*(double)DT,false,
                body,skin,gel,world,last);
            require(before.intact()&&after.intact()&&
                    after.solverTicks-before.solverTicks==actualSteps,
                    "invalid PRE/POST timing");
            if(gruResearchV347==null)encoder.after(after,true);
            Zero3OrganismStoreV322.Trace event=new Zero3OrganismStoreV322.Trace(
                n,before.solverTicks,after.solverTicks,before.hash,after.hash,
                actions,gripFrames,bilateral,work,variable);
            Zero3FullCausalExperienceV322B record=null;
            metabolism.finish(n,gel.ticks);
            // Only after the complete actual 12-step physics / metabolism
            // can a POST outcome be exposed. It cannot affect this choice.
            lastVitalOutcome=variable?Zero3CausalVitalBoundaryV344.afterVariableV352(vitalBefore,after,metabolism,actualSteps):
                Zero3CausalVitalBoundaryV344.after(vitalBefore,after,metabolism);
            Zero3GruSensoryV347.Unified unifiedAfter=unified?
                Zero3GruSensoryV347.unifiedAfter(after,lastVitalOutcome,body,skin,room,world):null;
            if(variable)record=Zero3FullCausalExperienceV322B.captureVariableV352(
                event,before,after,efferences,costs,gripFrames,bilateral,plannedSteps,interruption,unifiedBefore,unifiedAfter);
            else{
                record=Zero3FullCausalExperienceV322B.capture(event,before,after,efferences,costs,gripFrames,bilateral);
                if(unified)record=Zero3FullCausalExperienceV322B.withUnified(record,unifiedBefore,unifiedAfter);
            }
            if(cognitive){
                record=completionModeV354()?Zero3FullCausalExperienceV322B.withModeV354(record,observe?2:heldout?1:0,
                    new float[]{motor.x,motor.y,motor.effort,motor.adhesion,motor.duration},selectedOption,motor.revision,(int)(jointRepairedV346-repairsBefore),ambientDebit,disturbanceMask|((jointRepairedV346>repairsBefore)?4:0)):
                    Zero3FullCausalExperienceV322B.withPartitionV353(record,heldout);
                gruResearchV347.physicalTargetsV353(Zero3CognitionV353.targets(unifiedBefore.neural(),
                    unifiedAfter.neural(),lastVitalOutcome,actualSteps*DT),heldout);
            }
            if(variable)gruResearchV347.consequenceObservedV352(
                unifiedAfter.neural(),lastVitalOutcome.netEnergy,heldout,actualSteps*DT,metabolism.reserve()<=0f,interruption!=0);
            else if(gruResearchV347!=null)
                gruResearchV347.consequenceFixedClockV350(
                    unified?unifiedAfter.neural():Zero3GruSensoryV347.after(after,lastVitalOutcome),
                    lastVitalOutcome.netEnergy,false);
            if(completionModeV354()){
                gruResearchV347.completeExecutiveV354(result.score,lastVitalOutcome.netEnergy,lastVitalOutcome.actualDamage,(float)work,heldout,result.physicalEvidence&&!result.externalCorrection&&!result.censored);
                gruResearchV347.ecologyStateV354(shelterV354.encode(),nextRoomV354);
                gruResearchV347.repairCountersV354(jointPhysicsCountersV346());
            }
            trace.add(event);
            replay.add(record);
            last64=last.clone();
            lastMotorEvidence=audit.complete(result,gripFrames,bilateral);
            next++;
            ended=metabolism.reserve()<=0;
            require(authority.nextDecision()==next&&
                    metabolism.decisions()==next&&
                    (gruResearchV347==null?encoder.updates()==next:
                         gruResearchV347.trainingUpdates()+gruResearchV347.validations()+gruResearchV347.observationsV354()==next&&
                         encoder.updates()==0)&&
                    grip.updates()==next&&cortex.updates()<=next,
                    "physiology/actor/neural counters drifted after experience");
        }finally{
            inDecision=false;
        }
    }
    /** Capture a single schema-consistent immutable joint V324 generation. */
    public Zero3RoomArchiveV324.Bundle snapshot()throws IOException{
        require(!jointPhysicsResearchV346&&!jointPhysicsResearchV354&&gruResearchV347==null,
                "V346/V347 research unsnapshottable without full archive");
        require(!closed&&!inDecision&&next>0&&
                next==metabolism.decisions()&&next==replay.size()&&
                next==trace.size()&&authority.nextDecision()==next,
                "cannot snapshot a pending or mismatched decision");
        Zero3OrganismStoreV322.State neurons=
            Zero3OrganismStoreV322.capture(
                episode,next,rngState,encoder,cortex,grip,
                body,skin,gel,world,last64,trace);
        Zero3FullArchiveStoreV322B.Bundle causal=
            new Zero3FullArchiveStoreV322B.Bundle(neurons,replay,rngState);
        Zero3SurvivalArchiveV323.Bundle survival=
            new Zero3SurvivalArchiveV323.Bundle(causal,metabolism.checkpoint());
        return new Zero3RoomArchiveV324.Bundle(
            survival,Zero3RoomStateV324.capture(room,world));
    }
    /**
     * V3.28 compact active life + content-addressed FULL historical journal.
     * The old snapshot()/save() path remains V324 for scientific regressions;
     * all new V3 Android sessions use this path, preserving legacy V3 data.
     */
    public Zero3SegmentedArchiveV328.Bundle segmentedSnapshot()throws IOException{
        require(!jointPhysicsResearchV346&&!jointPhysicsResearchV354,
                "V346 physics is not approved for persistent archives");
        require(segmented&&!closed&&!inDecision&&next>0&&
                next==metabolism.decisions()&&next==replay.size()&&
                next==trace.size()&&authority.nextDecision()==next,
                "V328 requires complete matching actor/physics/energy replay");
        Zero3OrganismStoreV322.State organ=gruResearchV347==null?
            Zero3OrganismStoreV322.capture(
                episode,next,rngState,encoder,cortex,grip,
                body,skin,gel,world,last64,trace):
            Zero3OrganismStoreV322.captureGru(
                episode,next,rngState,encoder,cortex,grip,
                gruResearchV347.checkpointAtBoundaryV345(),
                body,skin,gel,world,last64,trace);
        return new Zero3SegmentedArchiveV328.Bundle(
                organ,metabolism.checkpoint(),
                Zero3RoomStateV324.capture(room,world),replay);
    }
    public void saveSegmented(Path privateV3Folder)throws IOException{
        require(!jointPhysicsResearchV346&&!jointPhysicsResearchV354,
                "cannot persist rejected V346 research physics");
        require(segmented,"V328 save not enabled");
        if(segmentedStore==null){
            segmentedStore=new Zero3SegmentedArchiveV328(privateV3Folder);
            Zero3SegmentedArchiveV328.Bundle prev=segmentedStore.load();
            if(prev!=null)
                require(prev.organism.episodeId==episode&&
                        prev.decisions()<=next,
                        "V328 refuses overwriting different or future V3 brain");
            else if(Zero3SegmentedArchiveV328.hasAny(privateV3Folder))
                throw new IOException("V328 corrupted existing history: fail closed");
        }
        segmentedStore.save(segmentedSnapshot());
        // Flush all previously committed full PRE/12x64/POST objects and
        // hold only an indexed view over the verified immutable chunk files.
        replay=segmentedStore.openReplay();
        // Orphan files are never reachable by a manifest; prune periodically
        // after both generations have been fully committed and verified.
        if(next%128==0)segmentedStore.pruneOrphans();
    }
    public static Zero3RuntimeV325 loadSegmented(Path privateV3Folder)
            throws IOException{
        Zero3SegmentedArchiveV328 disk=
            new Zero3SegmentedArchiveV328(privateV3Folder);
        Zero3SegmentedArchiveV328.Bundle b=disk.load();
        if(b==null){
            if(Zero3SegmentedArchiveV328.hasAny(privateV3Folder))
                throw new IOException("both V328 generations invalid; no reset");
            // Explicit V324 research migration only; no legacy 2.2 slot.
            Zero3RuntimeV325 earlier=load(privateV3Folder);
            if(earlier!=null)earlier.enableSegmented();
            return earlier;
        }
        Zero3OrganismStoreV322.State s=b.organism;
        Zero3OrganismStoreV322.Physical physical=s.physical();
        Zero3RoomStateV324.Restored restored=b.room.restore(physical.world);
        Zero3SurvivalLedgerV323 fuel=
            new Zero3SurvivalLedgerV323(100f,0f,0f,false);
        fuel.restore(b.survival);
        Zero3RuntimeV325 result=new Zero3RuntimeV325(s.episodeId,s.lastRngState,
            physical.body,physical.skin,physical.gel,restored.objects,
            restored.room,fuel,s.nextDecision,Arrays.asList(s.trace()),
            disk.openReplay(),s.last64(),true,.013f);
        try{
            if(s.hasGru()){
                Zero3RecurrentBrain restoredGru=Zero3RecurrentBrain.fromCheckpoint(s.gruBytes());
                s.restoreGruModules(result.encoder,result.cortex,
                                    result.grip);
                require(restoredGru.trainingUpdates()+restoredGru.validations()+restoredGru.observationsV354()==s.nextDecision,
                        "V348 GRU updates disagree with real experiences");
                // Close the temporary V317 owner BEFORE claiming the episode
                // as GRU; the single-owner guard rejects two live issuers.
                result.authority.close();
                Zero3MotorIntentV316.Authority nextAuthority=
                    new Zero3MotorIntentV316.Authority(s.episodeId,OWNER_GRU_V347);
                nextAuthority.restoreCompletedDecisions(s.nextDecision);
                result.authority=nextAuthority;
                result.gruResearchV347=restoredGru;
                if(restoredGru.completionModeV354()){
                    if(!result.shelterV354.restore(restoredGru.ecologyStateV354()))throw new IOException("V354 ecology restore rejected");
                    result.nextRoomV354=restoredGru.nextRoomV354();
                    long[] repairs=restoredGru.repairCountersV354();result.jointAttemptsV346=repairs[0];result.jointRepairedV346=repairs[1];result.jointUnresolvedV346=repairs[2];
                }
            }else s.restoreModules(result.encoder,result.cortex,result.grip);
            require((s.hasGru()?result.encoder.updates()==0&&
                    result.gruResearchV347.trainingUpdates()+result.gruResearchV347.validations()+result.gruResearchV347.observationsV354()==s.nextDecision:
                    result.encoder.updates()==s.nextDecision)&&
                    result.grip.updates()==s.nextDecision,
                    "V328 neural replay and restored authority disagree");
            result.segmented=true;result.segmentedStore=disk;
            return result;
        }catch(IOException|RuntimeException bad){
            result.close();
            throw new IOException("V328 segmented cold restore rejected",bad);
        }
    }
    /** Isolated caller-provided app-private V3 path. Never save 2.2 prefs. */
    public void save(Path privateV3Folder)throws IOException {
        require(!jointPhysicsResearchV346&&!jointPhysicsResearchV354&&gruResearchV347==null,
                "cannot save V346/V347 unversioned research mode");
        Zero3RoomArchiveV324 disk=new Zero3RoomArchiveV324(privateV3Folder);
        Zero3RoomArchiveV324.Bundle previous=disk.load();
        if(previous==null){
            // No valid generation must NOT be mistaken for a virgin folder
            // when old checkpoint files still exist but both are corrupt.
            if(Files.exists(privateV3Folder.resolve("v324-current.mf"))||
               Files.exists(privateV3Folder.resolve("v324-backup.mf"))||
               Files.exists(privateV3Folder.resolve("v324-room-0.bin"))||
               Files.exists(privateV3Folder.resolve("v324-room-1.bin")))
                throw new IOException("V325 existing checkpoint invalid; refusing destructive new generation");
        }else{
            Zero3OrganismStoreV322.State prev=previous.life.joint.organism;
            if(prev.episodeId!=episode||prev.nextDecision>next)
                throw new IOException("V325 refusing foreign or future generation");
        }
        disk.save(snapshot());
    }
    public String fingerprint()throws IOException{
        return Zero3RoomArchiveV324.fingerprint(snapshot());
    }
    @Override public void close(){
        if(!closed){
            closed=true;
            authority.close();
        }
    }
}
