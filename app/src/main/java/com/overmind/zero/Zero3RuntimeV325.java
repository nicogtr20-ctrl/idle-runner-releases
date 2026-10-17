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
    private static final long ENCODER_SEED=0x31720261017L;
    private static final long GRIP_SEED=0x31920261017L;
    private final long episode;
    private final Zero3AutonomousLoopV317.Encoder encoder;
    private final Zero3LearningFoundation.AdaptiveCortex cortex;
    private final Zero3ContextAdhesionV319 grip;
    private final Zero3LearningFoundation.Coordinator coordinator;
    private final Zero3MotorIntentV316.Authority authority;
    private final MotorBrain.Body body;
    private final Morphology.Surface skin;
    private final SymbioteMatterV1 gel;
    private final ArrayList<UnknownWorld.Entity> world;
    private final ScarcityLab room;
    private final Zero3SurvivalLedgerV323 metabolism;
    private final ArrayList<Zero3OrganismStoreV322.Trace> trace;
    private final ArrayList<Zero3FullCausalExperienceV322B> replay;
    private long rngState;
    private int next;
    private float[] last64;
    private boolean inDecision,closed,ended;
    // Opt-in only. Old V324 runtime and its research validation remain
    // restricted to 240; V328 owns separate manifests, not legacy slots.
    private boolean segmented;
    private Zero3SegmentedArchiveV328 segmentedStore;

    private Zero3RuntimeV325(long episode,long seed,MotorBrain.Body body,
          Morphology.Surface skin,SymbioteMatterV1 gel,
          ArrayList<UnknownWorld.Entity> world,ScarcityLab room,
          Zero3SurvivalLedgerV323 metabolism,int cursor,
          List<Zero3OrganismStoreV322.Trace> traces,
          List<Zero3FullCausalExperienceV322B> experiences,
          float[] previous,boolean restore)throws IOException {
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
        this.replay=new ArrayList<>(experiences);
        this.next=cursor;
        this.last64=previous==null?null:previous.clone();
        encoder=new Zero3AutonomousLoopV317.Encoder(ENCODER_SEED);
        cortex=new Zero3LearningFoundation.AdaptiveCortex(.013f);
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
            0,Collections.emptyList(),Collections.emptyList(),null,false);
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
            state.last64(),true);
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
        public final int decisions,neurons,objects;
        public final float reserve,priorDamage,cx,cy,doorX,doorY;
        public final boolean dead;
        public final float[] membraneX,membraneY,objectX,objectY;
        public final int[] objectLaw;
        private Display(Zero3RuntimeV325 r){
            episode=r.episode;solverTicks=r.gel.ticks;
            decisions=r.next;neurons=Zero3AutonomousLoopV317.HIDDEN;
            objects=r.world.size();reserve=r.metabolism.reserve();
            priorDamage=r.metabolism.previousDecisionDamage();
            dead=r.ended;cx=r.body.cx;cy=r.body.cy;
            doorX=r.room.gateX();doorY=r.room.gateCenterY();
            membraneX=r.skin.x.clone();membraneY=r.skin.y.clone();
            objectX=new float[objects];objectY=new float[objects];
            objectLaw=new int[objects];
            for(int i=0;i<objects;i++){
                UnknownWorld.Entity e=r.world.get(i);
                objectX[i]=e.x;objectY[i]=e.y;objectLaw[i]=e.physicalLaw();
            }
        }
    }
    public Display display(){
        require(!closed&&!inDecision,"render only finished physical states");
        return new Display(this);
    }
    public int decisions(){return next;}
    public boolean atArchiveCapacity(){
        return next>=(segmented?Zero3SegmentedArchiveV328.MAX_DECISIONS:
                       MAX_DURABLE_DECISIONS);
    }
    public int durableDecisionLimit(){
        return segmented?Zero3SegmentedArchiveV328.MAX_DECISIONS:
                         MAX_DURABLE_DECISIONS;
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
    private static long nextRng(long state){
        state^=state<<13;state^=state>>>7;state^=state<<17;
        return state==0?0x3322BBEEFEDL:state;
    }
    private UnknownWorld.Entity firstBox(){
        for(UnknownWorld.Entity e:world)if(e.isBilateralBox())return e;
        return null;
    }
    private void physics(float[] motors){
        float[] sx=skin.x.clone(),sy=skin.y.clone();
        float[] bx=body.x.clone(),by=body.y.clone();
        float cx=body.cx,cy=body.cy;
        gel.step(body,skin,motors,DT);
        boolean hasBoxes=false;
        for(UnknownWorld.Entity object:world)if(object.isBilateralBox()){
            SquareGripPuzzle.constrain(body,skin,object,DT,
                                       sx,sy,bx,by,cx,cy);
            hasBoxes=true;
        }
        if(hasBoxes)MaterialContactConstraints.reconcile(body,skin,world,DT);
        // Room's real collision volume and all solid boxes/panels have their
        // own owner: no exogenous movement, no scripted locomotion.
        SymbioteWorldStepV171.interior(gel,body,skin,room,world,DT);
        for(UnknownWorld.Entity object:world)if(object.isMovable()){
            Morphology.collide(skin,object,DT);
            ContactLearning.collide(body,object,DT);
            gel.observeBody(body,skin);
        }
        room.solveGate(skin,DT);
        GripSurfaceComposition.compose(skin,room,world);
        for(UnknownWorld.Entity object:world)object.tick(DT);
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
            // V3.27 active somatic interoception: sampled strictly BEFORE
            // choice, never the energy gain/damage of the pending action.
            encoder.before(before,metabolism.reserve()/100f,
                           metabolism.previousDecisionDamage());
            float[] choice=encoder.choose();
            Zero3MotorIntentV316 intent=authority.issue(OWNER,n,
                (int)cortex.revision(),325,choice[0],choice[1],
                choice[2],choice[3],SUBSTEPS*DT,.35f);
            Zero3LearningFoundation.MotorIntent motor=
                new Zero3LearningFoundation.MotorIntent(
                    episode,n,cortex.revision(),choice[0],choice[1],
                    choice[2],choice[3],SUBSTEPS*DT);
            coordinator.begin(motor,body,true,false,true);
            grip.begin();
            metabolism.begin(n,gel.ticks);
            long actions=0xcbf29ce484222325L;
            ArrayList<float[]> efferences=new ArrayList<>();
            ArrayList<Float> costs=new ArrayList<>();
            double work=0d;
            int gripFrames=0,bilateral=0;
            float[] last=null;
            for(int i=0;i<SUBSTEPS;i++){
                float[] command=coordinator.actuate(body,skin,DT);
                grip.actuate(command,intent,body,skin,world);
                float[] executed=command.clone();
                efferences.add(executed);
                for(float a:executed){
                    require(Float.isFinite(a)&&Math.abs(a)<=1.00001f,
                            "invalid issued actuator");
                    actions=(actions^Float.floatToIntBits(a))*1099511628211L;
                }
                float priorWork=gel.work;
                physics(command);
                float actualCost=Math.max(0f,gel.work-priorWork);
                costs.add(actualCost);work+=actualCost;
                metabolism.physicalStep(DT,actualCost,body,skin,world);
                coordinator.observe(body,DT,actualCost,
                    Math.max(0f,gel.lastMuscleReactionImpulse),false,false);
                UnknownWorld.Entity box=firstBox();
                grip.observe(skin,box,actualCost);
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
                last=executed;
            }
            grip.end();
            Zero3LearningFoundation.Experience result=coordinator.finish();
            require(result.frames==SUBSTEPS&&result.intent.decisionId==n,
                    "cortex credited incomplete/foreign physical decision");
            Zero3SensoryFrameV315 after=Zero3SensoryFrameV315.capture(
                episode,n+1,gel.ticks*(double)DT,false,
                body,skin,gel,world,last);
            require(before.intact()&&after.intact()&&
                    after.solverTicks-before.solverTicks==SUBSTEPS,
                    "invalid PRE/POST timing");
            encoder.after(after,true);
            Zero3OrganismStoreV322.Trace event=new Zero3OrganismStoreV322.Trace(
                n,before.solverTicks,after.solverTicks,before.hash,after.hash,
                actions,gripFrames,bilateral,work);
            Zero3FullCausalExperienceV322B record=
                Zero3FullCausalExperienceV322B.capture(
                    event,before,after,efferences,costs,gripFrames,bilateral);
            metabolism.finish(n,gel.ticks);
            trace.add(event);
            replay.add(record);
            last64=last.clone();
            next++;
            ended=metabolism.reserve()<=0;
            require(authority.nextDecision()==next&&
                    metabolism.decisions()==next&&
                    encoder.updates()==next&&grip.updates()==next&&
                    cortex.updates()<=next,
                    "physiology/actor/neural counters drifted after experience");
        }finally{
            inDecision=false;
        }
    }
    /** Capture a single schema-consistent immutable joint V324 generation. */
    public Zero3RoomArchiveV324.Bundle snapshot()throws IOException{
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
        require(segmented&&!closed&&!inDecision&&next>0&&
                next==metabolism.decisions()&&next==replay.size()&&
                next==trace.size()&&authority.nextDecision()==next,
                "V328 requires complete matching actor/physics/energy replay");
        Zero3OrganismStoreV322.State organ=Zero3OrganismStoreV322.capture(
                episode,next,rngState,encoder,cortex,grip,
                body,skin,gel,world,last64,trace);
        return new Zero3SegmentedArchiveV328.Bundle(
                organ,metabolism.checkpoint(),
                Zero3RoomStateV324.capture(room,world),replay);
    }
    public void saveSegmented(Path privateV3Folder)throws IOException{
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
            b.experiences(),s.last64(),true);
        try{
            s.restoreModules(result.encoder,result.cortex,result.grip);
            require(result.encoder.updates()==s.nextDecision&&
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
