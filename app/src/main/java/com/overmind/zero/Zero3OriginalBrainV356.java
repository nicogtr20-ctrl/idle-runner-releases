package com.overmind.zero;

import android.content.Context;
import android.util.Base64;
import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

/**
 * Exclusive V3 neuron controller for the UNMODIFIED original physical game.
 * Never allocates a V3 organism/world/room/body and never advances physics.
 * All pre/post frames reference GameActivity's real organism and materials.
 */
final class Zero3OriginalBrainV356 {
    static final String KEY="zero3_original_brain_only_v356";
    private static final int MAGIC=0x5A333536,VERSION=356,MAX=1200000;
    private static final String OWNER="original-game-zero3-v356";
    private Zero3RecurrentBrain brain;
    private Zero3LearningFoundation.AdaptiveCortex cortex;
    private Zero3LearningFoundation.Coordinator motor;
    private Zero3ContextAdhesionV319 grip;
    private Zero3MotorIntentV316.Authority authority;
    private Zero3LearningFoundation.MotorIntent choice;
    private Zero3MotorIntentV316 issued;
    private float[] executed64=new float[64],prior194;
    private long cursor,epoch;
    private float elapsed,beforeFuel,priorDamage,workAccum;
    // V359: pain is ONLY physically observed red-contact/weather loss.
    // It must not be fabricated from basal hunger or muscle metabolism.
    private float actualDamageDuringDecisionV359;
    // Physical motor RESERVE spent, not raw gel work, is the predicted cost.
    private float motorReserveSpentDuringDecisionV359;
    private long damagedDecisionsV359;
    private float maxVerifiedDecisionDamageV359;
    private boolean pending,heldout,disturbed;
    private boolean physicallyTouchedSolidDuringDecisionV380;
    // Empirically verified downstream actuator mapping, never an alternative brain.
    private final Zero381MotorCalibration motorResponseV381=new Zero381MotorCalibration();
    private final Zero381MotorFlightRecorder flightV381=new Zero381MotorFlightRecorder();
    private float preVxV381,preVyV381;

    // V361: read-only measured motor execution since this app session began.
    // These are not rewards, goals, authored directions, or neural input.
    private long measuredMotorDecisionsV361,measuredPhysicalV361,externalMotorDecisionsV361;
    private long alignedV361,opposedV361,almostStaticV361,solidContactStepsV361,heldContactStepsV361;
    private double sumIntentXV361,sumIntentYV361,sumActualXV361,sumActualYV361;
    private double sumForwardV361,sumGrossV361,sumWorkV361;
    private final long[] intentSectorsV361=new long[8];
    private int lastLife=-1;

    private Zero3OriginalBrainV356(){
        brain=new Zero3RecurrentBrain(194,true);
        brain.enableCognitionV353();
        brain.enableExecutiveV354();
        cortex=new Zero3LearningFoundation.AdaptiveCortex();
        grip=new Zero3ContextAdhesionV319(0x356A33L,true);
        motor=new Zero3LearningFoundation.Coordinator(cortex);
        epoch=Math.max(1,System.currentTimeMillis());
        authority=new Zero3MotorIntentV316.Authority(epoch,OWNER);
    }
    static Zero3OriginalBrainV356 load(Context context,String saved)throws IOException {
        return load(context,saved,true);
    }
    /** False for explicit newborn/reset. A new organism must not inherit
     * an archived experimental V354/V355 brain that lives in OTHER preferences.
     * Existing saved V356 brains still restore unchanged when allowed. */
    static Zero3OriginalBrainV356 load(Context context,String saved,
                                       boolean allowHistoricalImport)throws IOException {
        Zero3OriginalBrainV356 b=new Zero3OriginalBrainV356();
        try{
            if(!allowHistoricalImport){
                if(saved!=null&&!saved.isEmpty())
                    throw new IOException("Nuovo cervello V362 con checkpoint preesistente");
                return b; // true cursor=0, virgin weights, independent random seed
            }
            if(saved!=null&&!saved.isEmpty())b.restore(saved);
            else{
                // Reuse ONLY approved neurons of the previously installed V355,
                // NEVER V355's alternative world or physical life.
                int slot=context.getSharedPreferences("zero3-ui-v354",0).getInt("slot",1);
                if(slot<1||slot>12)slot=1;
                Zero3ProfileStoreV354 profiles=Zero3AndroidSessionV325.profilesV354(context);
                if(profiles.status(slot).startsWith("VITA ")){
                    Zero3SegmentedArchiveV328.Bundle archive=
                        new Zero3SegmentedArchiveV328(profiles.profile(slot)).load();
                    if(archive==null||!archive.organism.completionModeV354())
                        throw new IOException("Il profilo V355 non ha il cervello 3.0 completo");
                    b.brain=Zero3RecurrentBrain.fromCheckpoint(archive.organism.gruBytes());
                    archive.organism.restoreGruModules(
                        new Zero3AutonomousLoopV317.Encoder(1L),b.cortex,b.grip);
                    b.cursor=b.brain.trainingUpdates()+b.brain.validations()+b.brain.observationsV354();
                    if(b.cursor!=archive.organism.nextDecision)
                        throw new IOException("Il cervello V355 non coincide con lo storico");
                    b.brain.resetLife();
                    b.rearmAuthority();
                }
            }
            return b;
        }catch(IOException|RuntimeException failure){
            b.close();
            throw new IOException("Recupero neurale 3.0 fallito; nessun cervello azzerato",failure);
        }
    }
    private void rearmAuthority(){
        authority.close();epoch=Math.max(1,System.currentTimeMillis());
        authority=new Zero3MotorIntentV316.Authority(epoch,OWNER);
        if(cursor>0)authority.restoreCompletedDecisions(cursor);
    }
    long decisions(){return cursor;}
    long trained(){return brain.trainingUpdates();}
    long validated(){return brain.validations();}
    long observed(){return brain.observationsV354();}
    float previousDamageV359(){return priorDamage;}
    long damagedDecisionsV359(){return damagedDecisionsV359;}
    float maxVerifiedDecisionDamageV359(){return maxVerifiedDecisionDamageV359;}
    void restoreMotorObservationsV381(String response,String flight)throws IOException{
        if(!motorResponseV381.restore(response)||!flightV381.restore(flight))
            throw new IOException("Dati motori V381 corrotti: nessun salvataggio sovrascritto");
    }
    String motorResponseCheckpointV381(){return motorResponseV381.encode();}
    String flightCheckpointV381(){return flightV381.encode();}
    boolean motorResponseTrustedV381(){return motorResponseV381.trusted();}
    int motorResponseSamplesV381(){return motorResponseV381.samples();}
    float directionX(){return choice==null?0:choice.x;}
    float directionY(){return choice==null?0:choice.y;}
    float adhesion(){return choice==null?0:choice.adhesion;}
    boolean pending(){return pending;}
    /** The existing material collision records the photons visible AT TOUCH.
     * No object signature, object type or body-world target is revealed here.
     * This is part of the one GRU's V353 cognition, not the old controller.
     */
    void inheritedPhysicalContactPriorV369(float[] physicalMeans,int[] counts){
        if(pending)throw new IllegalStateException("V369 no archival learning mid decision");
        brain.inheritedPhysicalContactPriorV369(physicalMeans,counts);
    }
    /** A physical touch gives the local surface's observed wavelength only
     * AFTER collision. This is not an energy label or future target. */
    void realMaterialContactV374(int tactileOpticalBand,float[] allSeenPhotons,
                                  float actualContactDelta){
        if(!pending)throw new IllegalStateException("V374 no live original-world choice");
        brain.observedMaterialContactV374(
            tactileOpticalBand,allSeenPhotons,actualContactDelta,heldout);
    }
    void realEnergyContactV369(float[] perceivedSpectral24,float actualContactDelta){
        if(!pending)throw new IllegalStateException("V369 no actual GRU movement at contact");
        brain.realEnergyContactV369(perceivedSpectral24,actualContactDelta,heldout);
    }

    /** Session-only factual PRE intention versus POST physical displacement. */
    String motorDiagnosticV361(){
        return String.format(java.util.Locale.US,
            "m361|decisions=%d|physical=%d|externallyCorrected=%d|aligned=%d|opposed=%d|almostStatic=%d|solidSteps=%d|heldSteps=%d|intentX=%.5f|intentY=%.5f|actualX=%.5f|actualY=%.5f|signedProgress=%.5f|grossDistance=%.5f|materialWork=%.6f|azimuth=%s",
            measuredMotorDecisionsV361,measuredPhysicalV361,externalMotorDecisionsV361,
            alignedV361,opposedV361,almostStaticV361,solidContactStepsV361,heldContactStepsV361,
            sumIntentXV361,sumIntentYV361,sumActualXV361,sumActualYV361,
            sumForwardV361,sumGrossV361,sumWorkV361,
            java.util.Arrays.toString(intentSectorsV361));
    }
    float[] commands(){return executed64;}
    Zero3NeuralGraphV354 graph(){
        if(pending)throw new IllegalStateException("Atlante disponibile solo su decisione chiusa");
        Zero3NeuralGraphV354.Builder result=new Zero3NeuralGraphV354.Builder();
        brain.appendGraphV354(result);cortex.appendGraphV354(result);
        grip.appendGraphV354(result);return result.build();
    }
    private static float clip(float value){return Math.max(-1f,Math.min(1f,value));}
    /**
     * Exact V351 194-channel neural projection from the ORIGINAL physical game.
     * Null chamber is natural for the original open-world habitat.
     */
    private float[] sense(MotorBrain.Body body,Morphology.Surface skin,
            SymbioteMatterV1 matter,ArrayList<UnknownWorld.Entity> objects,
            ScarcityLab chamber,float reserve,double t,boolean before)throws IOException{
        if(matter==null||body==null||skin==null||objects==null)
            throw new IOException("Manca il corpo reale del simbionte");
        Zero3SensoryFrameV315 frame=Zero3SensoryFrameV315.capture(
            epoch,cursor,t,before,body,skin,matter,objects,executed64);
        float[] inputs=new float[194];
        System.arraycopy(Zero3AutonomousLoopV317.encode(frame),0,inputs,0,20);
        float[] spectral=SensorimotorMind.Senses.spectral8(body,skin,chamber,objects);
        System.arraycopy(spectral,0,inputs,20,24);
        for(int ray=0;ray<16;ray++){
            float angle=(float)(2*Math.PI*ray/16d);
            float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
            float[] eye=MaterialGeometry.eye(skin,body.cx,body.cy,dx,dy);
            float dist=SensorimotorMind.Senses.firstSolidDistance(chamber,objects,
                eye[0],eye[1],dx,dy,SpatialPerception.RANGE);
            inputs[44+ray]=Float.isFinite(dist)?clip(1f-dist/SpatialPerception.RANGE):0f;
        }
        int[] channels={Zero3SensoryFrameV315.TOUCH_SOLID,
            Zero3SensoryFrameV315.TOUCH_IMPULSE,Zero3SensoryFrameV315.GRIP_HELD,
            Zero3SensoryFrameV315.EXTENSION};
        for(int ch=0;ch<4;ch++)for(int sector=0;sector<16;sector++){
            float sum=0;
            for(int p=sector*6;p<sector*6+6;p++)sum+=frame.patch(p,channels[ch]);
            inputs[60+ch*16+sector]=clip(sum/6f);
        }
        System.arraycopy(executed64,0,inputs,124,64);
        inputs[188]=clip(reserve/50f-1f);
        inputs[189]=clip(priorDamage/40f);
        inputs[190]=clip(frame.gelAreaError);
        inputs[191]=clip(frame.bodyVelX/130f);
        inputs[192]=clip(frame.bodyVelY/130f);
        inputs[193]=1f;
        for(float x:inputs)if(!Float.isFinite(x))
            throw new IOException("Sensore fisico 3.0 non finito");
        return inputs;
    }
    /**
     * Called BEFORE the original symbiotic physics step: exclusively chooses
     * an intention and returns the 64 neural commands consumed by the solver.
     */
    /** Existing lab/legacy caller retains its original contract. */
    float[] actuate(int realLife,MotorBrain.Body body,Morphology.Surface skin,
            SymbioteMatterV1 matter,ArrayList<UnknownWorld.Entity> objects,
            ScarcityLab chamber,float reserve,float time,float dt)throws IOException{
        return actuate(realLife,body,skin,matter,null,objects,chamber,reserve,time,dt);
    }
    /** Sole V3 neural choice, with physically proven motor readout as an
     * entirely downstream peripheral in the original Android organism. */
    float[] actuate(int realLife,MotorBrain.Body body,Morphology.Surface skin,
            SymbioteMatterV1 matter,SymbioteMotorHeadV1 physicalPeripheralV364,
            ArrayList<UnknownWorld.Entity> objects,
            ScarcityLab chamber,float reserve,float time,float dt)throws IOException{
        try{
            if(!pending){
                prior194=sense(body,skin,matter,objects,chamber,reserve,time,true);
                beforeFuel=reserve;workAccum=0f;
                actualDamageDuringDecisionV359=0f;
                motorReserveSpentDuringDecisionV359=0f;elapsed=0;disturbed=false;
                physicallyTouchedSolidDuringDecisionV380=false;
                preVxV381=body.cvx;preVyV381=body.cvy;
                heldout=Zero3ConfigurationV354.heldout(cursor);
                choice=brain.choose(prior194,epoch,cursor,cortex.revision());
                issued=authority.issue(OWNER,cursor,(int)Math.min(Integer.MAX_VALUE,cortex.revision()),
                    356,choice.x,choice.y,choice.effort,choice.adhesion,choice.duration,
                    brain.measuredConfidenceV354());
                // Original 170-node gel only: physical motor credit is evaluated
                // against measured PRE momentum. No change to GRU action,
                // muscles, world forces, profile, or original 2.2 runtime.
                motor.begin(choice,body,!heldout,heldout,true,true,true);
                grip.begin(heldout);
                if(physicalPeripheralV364!=null){
                    float[] physicalHeading=motorResponseV381.begin(
                        choice.x,choice.y,body.cvx,body.cvy);
                    Zero3MotorPeripheralBridgeV364.begin(physicalPeripheralV364,
                        body,skin,chamber,objects,physicalHeading[0],physicalHeading[1]);
                }
                pending=true;lastLife=realLife;
            }
            if(realLife!=lastLife)throw new IOException("Vita cambiata durante la decisione");
            float[] out=motor.actuate(body,skin,dt);
            grip.originalTimestepV356(dt);
            grip.actuate(out,issued,body,skin,objects);
            if(physicalPeripheralV364!=null)
                Zero3MotorPeripheralBridgeV364.fastPhysicalRefine(
                    out,physicalPeripheralV364,body,skin,dt);
            // The proprioceptive efference matches EXACT post-blend channels
            // actually submitted to the original original-body solver.
            System.arraycopy(out,0,executed64,0,64);
            return out;
        }catch(RuntimeException failure){
            interrupt();throw new IOException("Scelta neurale 3.0 non valida",failure);
        }
    }
    /**
     * Called AFTER the original world collisions, using real muscle work,
     * forces, reserve and tissue outcomes (never a second solver).
     */
    void observed(MotorBrain.Body body,Morphology.Surface skin,SymbioteMatterV1 gel,
            ArrayList<UnknownWorld.Entity> objects,ScarcityLab chamber,
            float reserve,float time,float dt,float work,float impulse,
            float actualMotorReserveSpentThisFrame,float actualHarmThisPhysicalFrame,
            boolean external)throws IOException{
        observed(body,skin,gel,objects,chamber,reserve,time,dt,work,impulse,
            actualMotorReserveSpentThisFrame,actualHarmThisPhysicalFrame,external,false);
    }
    /** The original game's true fatal contact is a completed terminal event,
     * not a fabricated full-duration motor success. Keep all existing V353
     * predictors/GRU actor/critics and V354 options; censor only motor credit.
     * Call exactly once at death, before resetEpisode() erases the body. */
    void observed(MotorBrain.Body body,Morphology.Surface skin,SymbioteMatterV1 gel,
            ArrayList<UnknownWorld.Entity> objects,ScarcityLab chamber,
            float reserve,float time,float dt,float work,float impulse,
            float actualMotorReserveSpentThisFrame,float actualHarmThisPhysicalFrame,
            boolean external,boolean terminal)throws IOException{
        if(!pending)throw new IOException("V356 risultato senza un comando vero");
        if(terminal&&reserve>0f)
            throw new IOException("V368 terminal requires genuine zero reserve");
        // A damage reading is measured by the original world only. This value
        // NEVER includes ordinary hunger or fuel spent contracting muscles.
        if(!Float.isFinite(actualMotorReserveSpentThisFrame)||actualMotorReserveSpentThisFrame<0f||
           !Float.isFinite(actualHarmThisPhysicalFrame)||actualHarmThisPhysicalFrame<0f)
            throw new IOException("Danno interocettivo fisico non valido");
        try{
            actualDamageDuringDecisionV359+=actualHarmThisPhysicalFrame;
            motorReserveSpentDuringDecisionV359+=actualMotorReserveSpentThisFrame;
            if(!Float.isFinite(actualDamageDuringDecisionV359)||
               !Float.isFinite(motorReserveSpentDuringDecisionV359))
                throw new IllegalStateException("Danno interocettivo cumulato non finito");
            motor.observe(body,dt,Math.max(0,work),Math.max(0,impulse),external,false);
            for(float sensed:skin.touchSense)if(sensed>.2f){
                solidContactStepsV361++;
                physicallyTouchedSolidDuringDecisionV380=true;break;
            }
            for(float held:skin.gripHold)if(held>=Morphology.GRIP_HOLD){heldContactStepsV361++;break;}
            grip.observeWorldV354(skin,objects,chamber,Math.max(0,work));
            workAccum+=Math.max(0,work);elapsed+=dt;disturbed|=external;
            if(!terminal&&elapsed+0.0001f<choice.duration&&reserve>0)return;
            if(!terminal&&reserve>0f&&elapsed<.0249f){interrupt();return;}
            // The original weather step may exhaust the reserve AFTER resolveObjects.
            // Such a normal POST callback is also terminal, and must never
            // train an unexecuted motor duration as if it completed.
            boolean genuineTerminal=terminal||reserve<=0f;
            Zero3LearningFoundation.Experience outcome=motor.finish(genuineTerminal);
            // Physical measurements only: no reward or decisions are altered.
            measuredMotorDecisionsV361++;
            if(outcome.physicalEvidence)measuredPhysicalV361++;
            if(outcome.externalCorrection)externalMotorDecisionsV361++;
            float projection=outcome.deltaX*choice.x+outcome.deltaY*choice.y;
            float motionNet=(float)Math.hypot(outcome.deltaX,outcome.deltaY);
            if(motionNet<.2f)almostStaticV361++;
            else if(projection>.2f)alignedV361++;
            else if(projection<-.2f)opposedV361++;
            sumIntentXV361+=choice.x;sumIntentYV361+=choice.y;
            sumActualXV361+=outcome.deltaX;sumActualYV361+=outcome.deltaY;
            sumForwardV361+=projection;sumGrossV361+=outcome.gross;sumWorkV361+=outcome.work;
            int sector=((int)Math.floor((Math.atan2(choice.y,choice.x)+Math.PI/8)/
                (Math.PI/4))+8)%8;
            intentSectorsV361[sector]++;
            grip.end();pending=false;
            // POST and the next PRE now see the preceding decision's REAL pain.
            // Persist this exact measurement across a normal app restart.
            float harm=actualDamageDuringDecisionV359;
            priorDamage=harm;
            if(harm>0f){damagedDecisionsV359++;maxVerifiedDecisionDamageV359=Math.max(maxVerifiedDecisionDamageV359,harm);}
            float[] after=sense(body,skin,gel,objects,chamber,reserve,time,false);
            float net=reserve-beforeFuel;
            // Retain read-only physical command/outcome evidence. The V3
            // chooser, reinforcement and existing V378 credit are untouched.
            motorResponseV381.complete(outcome.deltaX,outcome.deltaY,elapsed,
                outcome.impulse,outcome.physicalEvidence&&!disturbed&&
                !outcome.externalCorrection&&!outcome.censored&&
                !physicallyTouchedSolidDuringDecisionV380&&!genuineTerminal,heldout);
            flightV381.completed(cursor,choice.x,choice.y,
                motorResponseV381.lastPeripheralX(),motorResponseV381.lastPeripheralY(),
                preVxV381,preVyV381,body.cvx,body.cvy,
                outcome.deltaX,outcome.deltaY,elapsed,outcome.impulse,
                Math.max(0f,net),harm,prior194,executed64,
                outcome.physicalEvidence,outcome.externalCorrection,
                physicallyTouchedSolidDuringDecisionV380);
            float touch=0,held=0,optical=0;
            for(int i=0;i<16;i++){touch+=after[60+i]/16f;held+=after[92+i]/16f;}
            for(int i=20;i<44;i++)optical+=Math.abs(after[i]-prior194[i])/48f;
            float[] targets={clip(net/100f),clip(harm/100f),
                clip(motorReserveSpentDuringDecisionV359/100f),
                after[191],after[192],clip(touch),clip(held),after[190],clip(optical),
                reserve<=0?1f:0f,clip(Math.max(0,net)/100f),clip(elapsed/.65f)};
            // Same REAL completed motor event feeds a single physical
            // action-to-response memory. Never use intended direction as
            // proof of moving in that direction; storms and solver impulses
            // are excluded from clean instrumentality.
            brain.witnessedMaterialMotionV371(outcome.deltaX,outcome.deltaY,
                elapsed,outcome.physicalEvidence,disturbed||outcome.externalCorrection||
                outcome.censored,heldout,
                physicallyTouchedSolidDuringDecisionV380);
            brain.physicalTargetsV353(targets,heldout);
            brain.consequenceObservedV352(after,net,heldout,elapsed,reserve<=0,genuineTerminal);
            brain.completeExecutiveV354(outcome.score,net,harm,
                motorReserveSpentDuringDecisionV359,heldout,
                outcome.physicalEvidence&&!disturbed&&!outcome.censored);
            cursor++;
        }catch(RuntimeException failure){
            interrupt();throw new IOException("Risultato neurale 3.0 incoerente",failure);
        }
    }
    /** Incomplete intentions receive no fictional reward on pause/death/reset. */
    void interrupt(){
        if(!pending)return;
        motor.abort();grip.abortOriginalV356();brain.abort();
        pending=false;choice=null;issued=null;elapsed=0;prior194=null;
        actualDamageDuringDecisionV359=0f;
        motorReserveSpentDuringDecisionV359=0f;
        physicallyTouchedSolidDuringDecisionV380=false;
        motorResponseV381.abort();
        // issued authority count is ahead one. New authority starts exactly at
        // completed cursor without contaminating or reusing an incomplete ID.
        rearmAuthority();
    }
    void newLife(){
        interrupt();brain.resetLife();lastLife=-1;
        // A new physical body has no previous-life pain stimulus.
        priorDamage=0f;actualDamageDuringDecisionV359=0f;
        motorReserveSpentDuringDecisionV359=0f;
    }
    void close(){interrupt();if(authority!=null)authority.close();}
    String checkpoint()throws IOException{
        if(pending)throw new IOException("Checkpoint vietato durante una decisione");
        try{
            ByteArrayOutputStream raw=new ByteArrayOutputStream();
            DataOutputStream d=new DataOutputStream(raw);
            d.writeInt(MAGIC);d.writeInt(VERSION);d.writeLong(cursor);
            byte[] gru=brain.checkpointAtBoundaryV345();d.writeInt(gru.length);d.write(gru);
            cortex.checkpoint().write(d);
            Zero3ContextAdhesionV319.State a=grip.captureAtDecisionBoundary();
            d.writeLong(a.rngState);d.writeBoolean(a.training);d.writeInt(a.decisions);
            d.writeInt(a.realCreditEvents);d.writeDouble(a.weightChangeSq);
            d.writeDouble(a.accountedAdhesionWork);d.writeDouble(a.observedGelWork);
            for(float w:a.weights())d.writeFloat(w);
            // Backward-compatible optional V359 tail inside the SAME V356 CRC.
            // Existing OTA358 checkpoints lack this float and remain readable.
            d.writeFloat(priorDamage);
            d.flush();byte[] payload=raw.toByteArray();
            CRC32 crc=new CRC32();crc.update(payload);
            d.writeInt((int)crc.getValue());d.flush();
            if(raw.size()>MAX)throw new IOException("Cervello 3.0 troppo grande");
            return Base64.encodeToString(raw.toByteArray(),Base64.NO_WRAP);
        }catch(RuntimeException failure){throw new IOException("Errore checkpoint 3.0",failure);}
    }
    /** Immutable read-only view of the SAME persisted V3 neuron system.
     * The legacy analytics screen is never allowed to display old 2.2 values. */
    static final class Readout {
        final long decisions,trained,heldout,observations,motorUpdates;
        final int sensoryInputs,hiddenUnits,actionOutputs,adhesionSectors;
        final float previousDamageV359;
        final Zero3CognitionV353.Snapshot cognition;
        final Zero3ExecutiveV354.Snapshot executive;
        final Zero3NeuralGraphV354 graph;
        private final Zero3RecurrentBrain core;
        private final Zero3LearningFoundation.AdaptiveCortex cortex;
        private final Zero3ContextAdhesionV319 grip;
        Readout(long count,Zero3RecurrentBrain b,
                Zero3LearningFoundation.AdaptiveCortex c,
                Zero3ContextAdhesionV319 g,float previousDamage){
            core=b;cortex=c;grip=g;previousDamageV359=previousDamage;
            decisions=count;trained=b.trainingUpdates();heldout=b.validations();
            observations=b.observationsV354();motorUpdates=c.updates();
            sensoryInputs=b.sensorWidth();hiddenUnits=b.realRecurrentUnits();
            actionOutputs=64;adhesionSectors=16;
            cognition=b.cognitionSnapshot();executive=b.executiveSnapshot();
            Zero3NeuralGraphV354.Builder builder=new Zero3NeuralGraphV354.Builder();
            b.appendGraphV354(builder);c.appendGraphV354(builder);
            g.appendGraphV354(builder);graph=builder.build();
        }
    }
    /** No world or physical objects allocated; no global actor authority. */
    static Readout inspectSaved(String saved)throws IOException {
        if(saved==null||saved.isEmpty())throw new IOException("Nessun checkpoint cerebrale V356 disponibile");
        byte[] bytes;
        try{bytes=Base64.decode(saved,Base64.DEFAULT);}
        catch(Exception e){throw new IOException("Formato neuronale V356 danneggiato",e);}
        if(bytes.length<128||bytes.length>MAX)throw new IOException("Lunghezza checkpoint V356 non valida");
        CRC32 crc=new CRC32();crc.update(bytes,0,bytes.length-4);
        int expected=((bytes[bytes.length-4]&255)<<24)|((bytes[bytes.length-3]&255)<<16)|
            ((bytes[bytes.length-2]&255)<<8)|(bytes[bytes.length-1]&255);
        if((int)crc.getValue()!=expected)throw new IOException("Integrità neuronale V356 non valida");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(bytes,0,bytes.length-4));
        if(d.readInt()!=MAGIC||d.readInt()!=VERSION)throw new IOException("Schema V356 incompatibile");
        long count=d.readLong();int size=d.readInt();
        if(count<0||count>100000||size<100||size>MAX)throw new IOException("Contatore/cervello V356 danneggiato");
        byte[] gru=new byte[size];d.readFully(gru);
        Zero3RecurrentBrain core=Zero3RecurrentBrain.fromCheckpoint(gru);
        Zero3LearningFoundation.AdaptiveCortex.State cortexState=
            Zero3LearningFoundation.AdaptiveCortex.State.read(d);
        long rng=d.readLong();boolean training=d.readBoolean();int decisions=d.readInt();
        int credits=d.readInt();double sq=d.readDouble(),gripCost=d.readDouble(),allWork=d.readDouble();
        float[] w=new float[128];for(int i=0;i<w.length;i++)w[i]=d.readFloat();
        // Zero remaining bytes = legitimate OTA356/357/358 save; four bytes
        // = optional pain memory from OTA359. Everything else is corruption.
        int tail=d.available();
        if(tail!=0&&tail!=4)throw new IOException("Checkpoint V359 con dati aggiuntivi sconosciuti");
        float previousDamage=tail==4?d.readFloat():0f;
        if(!Float.isFinite(previousDamage)||previousDamage<0f||previousDamage>1000f)
            throw new IOException("Danno precedente corrotto nel profilo");
        long neuralCount=core.trainingUpdates()+core.validations()+core.observationsV354();
        if(neuralCount!=count||decisions!=count||!core.completionModeV354())
            throw new IOException("Checkpoint V356 cervello/adesione incoerente");
        Zero3LearningFoundation.AdaptiveCortex newCortex=new Zero3LearningFoundation.AdaptiveCortex();
        newCortex.restore(cortexState);
        Zero3ContextAdhesionV319 newGrip=new Zero3ContextAdhesionV319(1L,true);
        newGrip.restoreAtDecisionBoundary(new Zero3ContextAdhesionV319.State(
            w,rng,training,decisions,credits,sq,gripCost,allWork));
        return new Readout(count,core,newCortex,newGrip,previousDamage);
    }
    private void restore(String saved)throws IOException{
        Readout restored=inspectSaved(saved);
        brain=restored.core;cortex=restored.cortex;
        motor=new Zero3LearningFoundation.Coordinator(cortex);
        grip=restored.grip;cursor=restored.decisions;
        priorDamage=restored.previousDamageV359;
        rearmAuthority();
    }
}
