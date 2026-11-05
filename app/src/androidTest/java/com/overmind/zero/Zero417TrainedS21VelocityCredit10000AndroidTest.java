package com.overmind.zero;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Iterator;
import org.json.JSONObject;
import org.json.JSONArray;
import static org.junit.Assert.*;

/**
 * V366 SCIENTIFIC BLIND BEHAVIOUR GATE, not a scripted motor test.
 * Run the original Android game, freshly newborn V356 GRU, 10000 COMPLETE
 * autonomous intentions and every real frame in existing TEST MAX engine.
 * No target coordinates, reward editing, synthetic damage, prescribed
 * muscle outputs, injected sample, or 'red' condition read by the actor.
 *
 * This test MAY FAIL; a failure prohibits OTA, never retune the threshold
 * after seeing these 2000 real experiences.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero417TrainedS21VelocityCredit10000AndroidTest {
    private static double field(String diagnostic,String key){
        String m="|"+key+"=";
        int i=diagnostic.indexOf(m);
        if(i<0)throw new AssertionError("missing genuine V361 motor statistic "+key);
        int start=i+m.length(),end=diagnostic.indexOf('|',start);
        if(end<0)end=diagnostic.length();
        return Double.parseDouble(diagnostic.substring(start,end));
    }
    private static long[] actualCumulativeMaterials(String serialized){
        // elt1 persists an UNBOUNDED real-contact ledger, not the newest
        // 50 complete lives of the old LifeStatistics HUD. This is essential
        // beyond 2000 decisions: history must not wrap and fake recovery.
        if(serialized==null||!serialized.startsWith("elt1|"))
            throw new AssertionError("Not the real cumulative material ledger");
        String[] pieces=serialized.split("\\|",-1);
        assertEquals("V406 original full-life physical contact ledger",6,pieces.length);
        long blue=Long.parseLong(pieces[1]),red=Long.parseLong(pieces[2]),
            violet=Long.parseLong(pieces[3]),legacy=Long.parseLong(pieces[4]);
        assertTrue(blue>=0&&red>=0&&violet>=0&&legacy>=0);
        return new long[]{blue,red,violet,legacy};
    }
    private static final class Window {
        final long decisions,blue,red,violet,updates,harmRejects,turnFilters;
        // Actual original motor opposition, near-static events and external
        // collisions. These come from physical POST measurements, not
        // generated/scheduled intentions; critical for wall and braking audit.
        final long opposed,almostStatic,solidContact,externalCorrection;
        final double signed,gross;
        Window(String counters,String cortex,String locomotion,long d){
            long[] c=actualCumulativeMaterials(counters);
            decisions=d;blue=c[0];red=c[1];violet=c[2];
            signed=field(locomotion,"signedProgress");
            gross=field(locomotion,"grossDistance");
            opposed=(long)field(locomotion,"opposed");
            almostStatic=(long)field(locomotion,"almostStatic");
            solidContact=(long)field(locomotion,"solidSteps");
            externalCorrection=(long)field(locomotion,"externallyCorrected");
            updates=(long)field(cortex,"weightUpdates");
            harmRejects=(long)field(cortex,"harmRejected");
            turnFilters=(long)field(cortex,"turnFiltered");
        }
        String metric(int bucket,Window prev){
            double intervalSigned=signed-prev.signed,
                intervalGross=gross-prev.gross;
            return String.format(Locale.ROOT,
                "ZERO417_TRAINED_S21_1000 decision=%d bucket=%d blueCum=%d redCum=%d violetCum=%d"+
                " blueWindow=%d redWindow=%d signedWindow=%.5f grossWindow=%.5f efficiencyWindow=%.6f"+
                " signedTotal=%.5f grossTotal=%.5f efficiencyTotal=%.6f weightUpdates=%d"+
                " harmRejected=%d turnFiltered=%d"+
                " opposedMovementWindow=%d staticMovementWindow=%d"+
                " wallContactFramesWindow=%d externalCorrectionsWindow=%d",
                decisions,bucket,blue,red,violet,blue-prev.blue,red-prev.red,
                intervalSigned,intervalGross,
                intervalSigned/Math.max(1d,intervalGross),
                signed,gross,signed/Math.max(1d,gross),updates,
                harmRejects,turnFilters,
                opposed-prev.opposed,almostStatic-prev.almostStatic,
                solidContact-prev.solidContact,
                externalCorrection-prev.externalCorrection);
        }
    }
    @Test(timeout=5400000)
    public void naturalUnseenPhysicalLivesRecoverUsefulMotorAndEnergyAvoidance()throws Exception {
        Instrumentation instrument=InstrumentationRegistry.getInstrumentation();
        Context c=instrument.getTargetContext();
        SharedPreferences p=c.getSharedPreferences(SaveSlotStore.WORKING,Context.MODE_PRIVATE);
        // The user's ACTUAL 2026-10-25 trained V388 33,312-decision GRU3
        // and exact 170-material stuck S21 training-room physical scene,
        // not a synthetic virgin body or scheduled heading. The 12 real
        // slots live on the user's separate phone and are never written.
        JSONObject fixture=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout readout=
            Zero3OriginalBrainV356.inspectSaved(fixture.getString("brain"));
        assertEquals(33312L,readout.decisions);
        assertEquals(19452L,readout.motorUpdates);
        JSONObject saved=new JSONObject(fixture.getString(
            "training_room_1_scene_v379"));
        JSONArray entities=saved.getJSONObject("physical").getJSONArray("objects");
        final JSONObject oldBody=saved.getJSONObject("physical").getJSONObject("body");
        final float expectedOriginalX=(float)oldBody.getDouble("cx"),
                    expectedOriginalY=(float)oldBody.getDouble("cy");
        final int expectedOriginalObjects=entities.length();
        // V464 read-only: count actual collectible blue resources in the saved S21 scene.
        int savedBlueTotal=0,savedBlueReady=0;
        JSONArray items=new JSONArray();
        for(int i=0;i<entities.length();i++){
            JSONObject object=entities.getJSONObject(i);
            int law=object.getInt("law");
            if(law==0){
                savedBlueTotal++;
                if(!object.optBoolean("consumed",false)&&
                   object.optDouble("cooldown",0d)<=0d)savedBlueReady++;
            }
            float x=(float)object.getDouble("sx"),y=(float)object.getDouble("sy");
            // Reconstruct only the user's original placed materials. The
            // 96 fixed perimeter wall nodes are regenerated by the original
            // TrainingRoomV379.newEntities() at an actual physical death.
            if(law==7&&(Math.abs(x)==TrainingRoomV379.HALF_W||
                          Math.abs(y)==TrainingRoomV379.HALF_H))continue;
            int type=law==0?TrainingRoomV379.BLUE:
                law==1?TrainingRoomV379.RED:
                law==3?TrainingRoomV379.PURPLE:
                law==5?TrainingRoomV379.BOX:
                law==7?TrainingRoomV379.WALL:-1;
            assertTrue("Unknown saved physical material cannot be invented",type>=0);
            JSONArray item=new JSONArray();
            item.put(type);item.put(x);item.put(y);items.put(item);
        }
        android.util.Log.i("ZERO417_USER_S21_LONG",
            "ZERO464_SAVED_S21_BLUE_AVAILABILITY total="+savedBlueTotal+
            " initiallyReady="+savedBlueReady+
            " alreadyUnavailable="+(savedBlueTotal-savedBlueReady)+
            " strictAdditionalBlueGate=5");
        JSONObject layout=new JSONObject();
        layout.put("version",1);layout.put("items",items);
        TrainingRoomV379 reconstructed=TrainingRoomV379.decode(layout.toString());
        assertEquals(items.length(),reconstructed.size());
        SharedPreferences.Editor editor=p.edit().clear();
        JSONObject historical=fixture.getJSONObject("originalPrefs");
        Iterator<String> keys=historical.keys();
        while(keys.hasNext()){
            String key=keys.next();
            Object value=historical.get(key);
            if(value instanceof String)editor.putString(key,(String)value);
        }
        editor.putString(Zero3OriginalBrainV356.KEY,fixture.getString("brain"));
        editor.putString(TrainingRoomV379.SCENE_KEY,
            fixture.getString("training_room_1_scene_v379"));
        editor.putString(TrainingRoomV379.LAYOUT_KEY,layout.toString());
        editor.putBoolean(TrainingRoomV379.ACTIVE_KEY,true);
        editor.putBoolean(SymbioteMatterV1.ENABLED,true);
        editor.putBoolean(SaveSlotStore.FRESH_ZERO3_BIRTH,false);
        assertTrue("Saved user profile and REAL room seeded atomically",
            editor.commit());
        Intent intent=new Intent(c,GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // The real original game repaints continuously. startActivitySync()
        // incorrectly waits for a permanently idle UI and times out. The
        // already approved V362 tests use the activity monitor protocol.
        Instrumentation.ActivityMonitor monitor=instrument.addMonitor(
            GameActivity.class.getName(),null,false);
        GameActivity original;
        try {
            c.startActivity(intent);
            Activity created=instrument.waitForMonitorWithTimeout(monitor,30000);
            assertNotNull("original repainting organism actually launched",created);
            assertTrue(created instanceof GameActivity);
            original=(GameActivity)created;
        }finally{instrument.removeMonitor(monitor);}
        try {
            // The original Activity draws continuously; do not block its
            // main looper with forced onDraw() calls from the test!
            // Activity monitor returns when launch is observed, potentially
            // BEFORE onCreate()/Lab.load() completes. A previous V403
            // laboratory trial misreported that as loss of the GRU owner.
            boolean ready=false;
            for(int wait=0;wait<300&&!ready;wait++){
                AtomicBoolean has=new AtomicBoolean(false);
                instrument.runOnMainSync(()->has.set(
                    original.originalV356NeuralOwnerForQA()));
                ready=has.get();
                if(!ready)android.os.SystemClock.sleep(100);
            }
            assertTrue("Original sole GRU3 must really finish boot",ready);
            AtomicLong initial=new AtomicLong(-1);
            AtomicLong initialLife=new AtomicLong(-1);
            AtomicReference<Window> initialWindow=new AtomicReference<>();
            // V412: snapshot STARTING state and PRE physical GRU3 stats on
            // the same genuine Activity UI transaction BEFORE enabling MAX
            // speed; otherwise the Android compositor may advance decisions,
            // drop the observed Activity, or trigger onDraw while a different
            // test thread snapshots, corrupting the 10x1000 science baseline.
            instrument.runOnMainSync(()->{
                initial.set(original.originalV356DecisionsForQA());
                initialLife.set(original.originalV356LifeForQA());
                original.originalV400EnableLocalTemporalMotorCreditForQA();
                original.originalV414AllowVerifiedRearOnlyWallCreditForQA();
                original.originalV417EnableMeasuredBrakingCreditForQA();
                // V429 sole behavioral difference from V428: prevent original
                // V371/V377 high-level response learning from RED damping,
                // front-wall arrest or externally disturbed motor samples.
                original.originalV420EnableSafeHighLevelMotorMapForQA();
                // ZERO424: ONE additional isolated variable versus V423: use the
                // actual delivered (post-tanh/effort/clip) V390 muscle probe
                // for the SAME decision's V423 measured COM-gradient update.
                original.originalV421EnableDeliveredLocalProbeForQA();
                original.originalV423EnableImmediateOwnMotorCreditForQA();
                // V487 isolated ACTUAL-ORIGINAL behavioral A/B: historical
                // physically eligible ON baseline; not a scripted heading.
                original.originalV487EnableObservedMotorBaselineCreditForQA();
                // ZERO454 only new scientific motor change versus V453:
                // original real V390 muscle pulses ON/OFF/OFF/ON and learns
                // signed physical COM acceleration contrast in SAME action.
                // ZERO455 matched-control: intentionally do NOT enable V454 ABBA.
                // All other original 10k flags, physical body, seed and gates are unchanged.
                android.util.Log.i("ZERO455_MATCHED_CONTROL",
                    "V454_ABBA_OFF=true,V421_DELIVERED_ON=true,V423_OWN_CREDIT_ON=true");
                // ZERO425: one new high-level actor gradient eligibility
                // correction; original 10k gates and physical world unchanged.
                original.originalV425EnableTangentActorCreditForQA();
                assertTrue("V425 actual original GRU3 actor flag",
                    original.originalV425TangentActorCreditEnabledForQA());
                original.originalV426EnableJointSampleActorScoreForQA();
                assertTrue("V426 original S21 joint actor score flag",
                    original.originalV426JointSampleActorScoreEnabledForQA());
                // ZERO431 is the only new behavioral change versus V430:
                // require measured acceleration for LOWER V332 rear-wall credit.
                original.originalV432EnableSignedRearDeliveredProbeForQA();
                original.originalV433EnableStrictInertialOpticalEvidenceForQA();
                original.originalV434EnableObservedDelayedContactForQA();
                original.originalV435EnableAcceleratedDelayedContactForQA();
                // V442 sole behavioral change versus V441: do not treat
                // historical bank association as a verified new motor action.
                original.originalV442EnableCensorUnverifiedDelayedBankUnderDriftForQA();
                // V476 ONLY behavioral variable: independent heldout-validated
                // intermediate PRE drift 0.35..2.0 in transient V377 banks.
                original.originalV476EnableTransientMidDriftModelForQA();
                // V478 sole behavioral delta: learned optical consequence of
                // ACTUAL PRE inertia, same for every unverified command.
                original.originalV478EnableMeasuredPassiveCoastOpticalForQA();
                // V489: compare actual candidate-conditioned material movement
                // to real same-PRE/effort context baseline on disjoint heldout.
                // No authored red escape direction or external muscle force.
                original.originalV489EnableMatchedPreMotorForQA();
                // ZERO461 sole new scientific variable from V457: preserve
                // original learned CH2 grip while suppressing only its 16
                // random exploratory logits; actual sole GRU3 muscle train
                // and V377 heldout outcomes remain unchanged.
                original.originalV461EnableDeterministicGripForQA();
                // V436: only new behavioral variable versus V435. Wider actor-sampled
                // choices require genuine previously learned, currently visible harm.
                original.originalV422EnableLearnedHarmCandidatesForQA();
                initialWindow.set(new Window(
                    original.originalV406TrueLifetimeMaterialContactsForQA(),
                    original.originalV400LocalMotorDiagnosticsForQA(),
                    original.originalV366MotorDiagnosticForQA(),
                    initial.get()));
                // V467 exact real death snapshots; no behavioral intervention.
                original.originalV467EnableExactDeathBoundaryForQA();
                original.originalV412ResumeTrueS21TrainingForQA(
                    expectedOriginalX,expectedOriginalY,expectedOriginalObjects);
            });
            assertEquals("Must resume EXACT user original 33,312 GRU3 decisions",
                33312L,initial.get());
            final int TARGET=10000, MAX_BURSTS=24000;
            final long finalCursor=initial.get()+TARGET;
            long cursor=initial.get(),nextBucket=1000;
            Window previous=initialWindow.get();
            final Window[] windows=new Window[11];
            windows[0]=previous;
            // V466 diagnostic ONLY: locate the actual original-life boundary
            // within a 1000-decision window. No body, GRU, reward, or test gate
            // changes. Polling can lag a few genuine decisions, so these are
            // approximate event positions, NOT pre/post death force samples.
            final long[] lastObservedLifeV466={initialLife.get()};
            final Window[] previousLifeSnapshotV466={previous};
            for(int burst=0;burst<MAX_BURSTS && cursor<finalCursor;burst++){
                AtomicLong result=new AtomicLong(-1);
                AtomicLong priorLifeV466=new AtomicLong(-1);
                AtomicLong currentLifeV466=new AtomicLong(-1);
                AtomicReference<Window> lifeSnapshotV466=new AtomicReference<>();
                // The game advances autonomously on its normal UI clock.
                // Test thread only observes a sampled decision cursor.
                android.os.SystemClock.sleep(260);
                instrument.runOnMainSync(()->{
                    result.set(original.originalV356DecisionsForQA());
                    long life=original.originalV356LifeForQA();
                    if(life!=lastObservedLifeV466[0]){
                        priorLifeV466.set(lastObservedLifeV466[0]);
                        currentLifeV466.set(life);
                        lifeSnapshotV466.set(new Window(
                            original.originalV406TrueLifetimeMaterialContactsForQA(),
                            original.originalV400LocalMotorDiagnosticsForQA(),
                            original.originalV366MotorDiagnosticForQA(),
                            result.get()));
                        lastObservedLifeV466[0]=life;
                    }
                });
                cursor=result.get();
                Window lifeBoundaryV466=lifeSnapshotV466.get();
                if(lifeBoundaryV466!=null){
                    Window prior=previousLifeSnapshotV466[0];
                    android.util.Log.i("ZERO417_USER_S21_LONG",
                        "ZERO466_REAL_REBIRTH_BOUNDARY decision="+cursor+
                        " fromLife="+priorLifeV466.get()+
                        " toLife="+currentLifeV466.get()+
                        " approximateBucket="+((cursor-initial.get()-1)/1000+1)+
                        " signedSincePriorBoundary="+(lifeBoundaryV466.signed-prior.signed)+
                        " grossSincePriorBoundary="+(lifeBoundaryV466.gross-prior.gross)+
                        " blueSincePriorBoundary="+(lifeBoundaryV466.blue-prior.blue)+
                        " signedSinceStart="+(lifeBoundaryV466.signed-windows[0].signed)+
                        " grossSinceStart="+(lifeBoundaryV466.gross-windows[0].gross)+
                        " blueSinceStart="+(lifeBoundaryV466.blue-windows[0].blue));
                    previousLifeSnapshotV466[0]=lifeBoundaryV466;
                }
                // V397: never misreport a lost original Android brain owner
                // as a harmless missing statistics field at the next 200
                // decisions. This is a SCIENTIFIC instrumentation failure,
                // not a score for neural locomotion.
                boolean owner=original.originalV356NeuralOwnerForQA();
                if(burst%30==0 || !owner){
                    android.util.Log.i("ZERO397_AUTONOMY",
                        "REAL_ORIGINAL_LIFECYCLE burst="+burst+
                        ",cursor="+cursor+",owner="+owner+
                        ",destroyed="+original.isDestroyed()+
                        ",finishing="+original.isFinishing()+
                        ",error="+original.originalV356BrainErrorForQA());
                }
                assertTrue("Original GRU3 owner vanished at physical burst "+
                    burst+" (cursor="+cursor+",destroyed="+original.isDestroyed()+
                    ",finishing="+original.isFinishing()+",error="+
                    original.originalV356BrainErrorForQA()+")",owner);
                String error=original.originalV356BrainErrorForQA();
                assertTrue("real world must not stop due to neural or physics error: "+error,
                    error==null||error.isEmpty());
                // Capture all metrics from one original GAME THREAD instant.
                // This does not mutate the original actor, material or brain.
                if(cursor-initial.get()>=nextBucket){
                    AtomicReference<Window> snap=new AtomicReference<>();
                    // V465 read-only: capture actual physical rebirth serial
                    // on the SAME UI transaction as each 1000-decision window.
                    // This tests whether inverse locomotion windows coincide
                    // with death and genuine training-room respawn; it does
                    // not change the actor, rewards, world or saved brain.
                    AtomicLong sampledLife=new AtomicLong(-1);
                    instrument.runOnMainSync(()->{
                        snap.set(new Window(
                            original.originalV406TrueLifetimeMaterialContactsForQA(),
                            original.originalV400LocalMotorDiagnosticsForQA(),
                            original.originalV366MotorDiagnosticForQA(),
                            original.originalV356DecisionsForQA()));
                        sampledLife.set(original.originalV356LifeForQA());
                    });
                    Window now=snap.get();
                    assertNotNull(now);
                    int window=(int)(nextBucket/1000);
                    assertTrue("Long-run material contact counts must never wrap",
                        now.red>=previous.red&&now.blue>=previous.blue&&
                        now.violet>=previous.violet);
                    assertTrue("Cumulative cortical updates must never decrease",
                        now.updates>=previous.updates);
                    assertTrue("All 10k original physical motor metrics must stay finite",
                        Double.isFinite(now.signed)&&Double.isFinite(now.gross));
                    assertTrue("Original material travel must not decrease",now.gross>=previous.gross);
                    windows[window]=now;
                    // V465: do not claim the saved four ready blues form a
                    // lifetime 10k ceiling: real deaths recreate five blues.
                    // Count only genuine pickups in the original contact ledger.
                    android.util.Log.i("ZERO417_USER_S21_LONG",
                        "ZERO465_WINDOW_LIFE_RESOURCE bucket="+window+
                        " lifeSerial="+sampledLife.get()+
                        " rebirthsSinceStart="+(sampledLife.get()-initialLife.get())+
                        " newBlueThisWindow="+(now.blue-previous.blue)+
                        " newBlueCumulative="+(now.blue-windows[0].blue));
                    String status=now.metric(window,previous);
                    System.out.println(status);
                    android.util.Log.i("ZERO417_USER_S21_LONG",status);
                    previous=now;
                    nextBucket+=1000;
                }
                if(burst%200==0){
                    String errorState=original.originalV356BrainErrorForQA();
                    android.util.Log.i("ZERO417_USER_S21_LONG",
                        "ORIGINAL_LIFECYCLE cursor="+cursor+
                        " owner="+owner+" error="+errorState+
                        " latestGoal="+(nextBucket-1000)+
                        " originalBoot="+initial.get());
                }
            }
            AtomicReference<Window> endSnapshot=new AtomicReference<>();
            AtomicLong endLife=new AtomicLong(-1);
            instrument.runOnMainSync(()->{
                endSnapshot.set(new Window(
                    original.originalV406TrueLifetimeMaterialContactsForQA(),
                    original.originalV400LocalMotorDiagnosticsForQA(),
                    original.originalV366MotorDiagnosticForQA(),
                    original.originalV356DecisionsForQA()));
                endLife.set(original.originalV356LifeForQA());
            });
            Window last=endSnapshot.get();
            assertNotNull(last);
            android.util.Log.i("ZERO417_USER_S21_LONG",
                "ZERO464_ORIGINAL_LIFE_RESOURCE_AUDIT initialLife="+initialLife.get()+
                " finalLife="+endLife.get()+
                " rebirths="+(endLife.get()-initialLife.get())+
                " savedReadyBlue="+savedBlueReady+
                " collectedNewBlue="+(last.blue-initialWindow.get().blue));
            if(windows[10]==null&&last.decisions-initial.get()>=TARGET){
                assertTrue(last.red>=previous.red&&last.blue>=previous.blue);
                String lastMetric=last.metric(10,previous);
                windows[10]=last;
                android.util.Log.i("ZERO417_USER_S21_LONG",lastMetric);
            }
            String motor=original.originalV366MotorDiagnosticForQA();
            String local=original.originalV400LocalMotorDiagnosticsForQA();
            assertTrue("V487 new live original physical baseline must be explicitly enabled, "+
                "not the V486 read-only diagnostic: "+local,
                local.contains("|v487ObservedBaseline=true|")&&
                local.contains("|v487Accepted=")&&local.contains("|v487Centered=")&&
                local.contains("|v488EffortConditioned=true|"));
            android.util.Log.i("ZERO417_USER_S21_LONG","ZERO443_MOTOR_EVIDENCE "+
                original.originalV443MotorEvidenceDiagnosticsForQA());
            android.util.Log.i("ZERO417_USER_S21_LONG","ZERO436_LEARNED_HARM_COVERAGE "+
                original.originalV422LearnedHarmCandidateDiagnosticsForQA());
            assertTrue("V429 real high-level causal motor map enabled",
                local.contains("safeMotorMapLabV420=true"));
            android.util.Log.i("ZERO417_USER_S21_LONG",
                "ZERO417_ORIGINAL_PHYSICAL_REAR_WALL_CREDIT "+local);
            assertTrue("Must test actual original V414 rear-only research mode",
                local.contains("rearWallLab=true")&&
                local.contains("realVelocityCreditLab=true")&&
                local.contains("immediateOwnDecisionCreditLabV423=true")&&
                local.contains("deliveredLocalProbeLabV421=true"));
            double actualNewSigned=last.signed-windows[0].signed;
            double actualNewGross=last.gross-windows[0].gross;
            double ratio=actualNewSigned/Math.max(1d,actualNewGross);
            String outcome=String.format(Locale.ROOT,
                "ZERO417_ORIGINAL_USER_S21_10000_RESULT decisions=%d baseline=%d blue=%d red=%d"+
                " violet=%d signed=%.5f gross=%.5f efficiency=%.6f"+
                " localWeightUpdates=%d localHarmRejected=%d localTurnsFiltered=%d"+
                " motorOwner=%s",
                last.decisions,initial.get(),last.blue,last.red,last.violet,
                last.signed,last.gross,ratio,last.updates,last.harmRejects,
                last.turnFilters,original.originalV356NeuralOwnerForQA());
            System.out.println(outcome);
            android.util.Log.i("ZERO417_USER_S21_LONG",outcome);
            android.util.Log.i("ZERO417_USER_S21_LONG",
                "TRUE_USER_33312_RESTORED_GRU3=YES,REAL_TRAINING_170_MATERIAL=YES"+
                ",NO_SCHEDULED_DIRECTION=YES,NO_RED_AVOIDANCE_CLAIM=YES"+
                ",NEW_SESSION_SIGNED="+actualNewSigned+
                ",NEW_SESSION_GROSS="+actualNewGross);
            // SOURCE-LEVEL correctness is unconditional; BEHAVIOUR
            // acceptance is intentionally strict (no lowering thresholds).
            assertTrue("Must execute >=10000 COMPLETE original autonomous decisions: "+outcome,
                last.decisions-initial.get()>=TARGET);
            assertTrue("Real physical body must be moving: "+outcome,actualNewGross>100d);
            assertTrue("Actual GRU3 local cortex must update real original weights: "+outcome,
                last.updates>0&&local.contains("enabled=true"));
            assertTrue("Each thousand original decisions must be sampled, never 2k x5",
                java.util.Arrays.stream(windows).allMatch(x->x!=null));
            // V412 predeclared stability criterion (NOT new directional
            // instructions). No hiding an intermittent inverse motor just
            // because large late runs compensate bad early windows.
            int wellDirectedWindows=0,oppositeWindows=0;
            for(int i=1;i<=10;i++){
                double incrementSigned=windows[i].signed-windows[i-1].signed;
                double incrementGross=windows[i].gross-windows[i-1].gross;
                double windowRatio=incrementSigned/Math.max(1d,incrementGross);
                if(windowRatio>=.10d)wellDirectedWindows++;
                if(windowRatio<0d)oppositeWindows++;
                android.util.Log.i("ZERO417_USER_S21_LONG",
                    "ZERO417_DIRECTIONAL_STABILITY_WINDOW index="+i+
                    " ratio="+windowRatio+" opposite="+(windowRatio<0d)+
                    " wallFrames="+(windows[i].solidContact-
                                    windows[i-1].solidContact));
            }
            android.util.Log.i("ZERO417_USER_S21_LONG",
                "ZERO417_WALL_TRACTION_STABILITY_10000"+
                " alignedWindows="+wellDirectedWindows+
                " oppositeWindows="+oppositeWindows);
            assertTrue("V414 real rear-wall teaching must generalize to "+
                "at least nine separately measured 1000-decision segments",
                wellDirectedWindows>=9);
            assertEquals("The full repaired saved S21 must have NO 1000-decision "+
                "segments where actual body moves against its own chosen intent",
                0,oppositeWindows);
            // We record all 10 windows even if scientific acceptance fails.
            // No red-specific hard approval from an environment with zero
            // observed harmful contact; note this as INCONCLUSIVE instead.
            assertTrue("V414 trained S21 10000 original choices must exceed a "+
                "meaningful 25% signed motion ratio: "+
                outcome,ratio>=.25d);
            assertTrue("Original GRU3 10k locomotion must exceed the pre-existing 12% gate: "+
                    outcome,ratio>=.12d);
            assertTrue("Original 10k world must yield genuinely positive material contacts: "+
                    outcome,last.blue>windows[0].blue);
            assertTrue("A user-trained organism must collect at least five "+
                    "additional real blue resources in its original room: "+outcome,
                    last.blue-windows[0].blue>=5);
            // The original uploaded Training 1 had BLUE only, no dangerous
            // red material. This genuine S21 test is for motor/foothold and
            // blue reach, NOT for claiming red hazard recognition. The separate
            // newborn 10k original-world test covers actual reds.
            assertEquals("Original S21 blue-only training room must not be "+
                "misrepresented as a red-avoidance trial",
                windows[0].red,last.red);
            android.util.Log.i("ZERO417_USER_S21_LONG",
                "ZERO417_REAL_TRAINED_S21_10000_MOTOR_GATE=PASS");
        } finally {
            instrument.runOnMainSync(original::finish);
            // No artificial Canvas or detached physics owner to recycle.
        }
    }
}
