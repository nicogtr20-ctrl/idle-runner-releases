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
public final class Zero417WildOriginalVelocityCredit10000AndroidTest {
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
        final double signed,gross;
        Window(String counters,String cortex,String locomotion,long d){
            long[] c=actualCumulativeMaterials(counters);
            decisions=d;blue=c[0];red=c[1];violet=c[2];
            signed=field(locomotion,"signedProgress");
            gross=field(locomotion,"grossDistance");
            updates=(long)field(cortex,"weightUpdates");
            harmRejects=(long)field(cortex,"harmRejected");
            turnFilters=(long)field(cortex,"turnFiltered");
        }
        String metric(int bucket,Window prev){
            double intervalSigned=signed-prev.signed,
                intervalGross=gross-prev.gross;
            return String.format(Locale.ROOT,
                "ZERO417_WILD_ACTUAL_1000 decision=%d bucket=%d blueCum=%d redCum=%d violetCum=%d"+
                " blueWindow=%d redWindow=%d signedWindow=%.5f grossWindow=%.5f efficiencyWindow=%.6f"+
                " signedTotal=%.5f grossTotal=%.5f efficiencyTotal=%.6f weightUpdates=%d"+
                " harmRejected=%d turnFiltered=%d",
                decisions,bucket,blue,red,violet,blue-prev.blue,red-prev.red,
                intervalSigned,intervalGross,
                intervalSigned/Math.max(1d,intervalGross),
                signed,gross,signed/Math.max(1d,gross),updates,
                harmRejects,turnFilters);
        }
    }
    @Test(timeout=5400000)
    public void naturalUnseenPhysicalLivesRecoverUsefulMotorAndEnergyAvoidance()throws Exception {
        Instrumentation instrument=InstrumentationRegistry.getInstrumentation();
        Context c=instrument.getTargetContext();
        SharedPreferences p=c.getSharedPreferences(SaveSlotStore.WORKING,Context.MODE_PRIVATE);
        assertTrue(p.edit().clear().putBoolean(SymbioteMatterV1.ENABLED,true)
            .putBoolean(SaveSlotStore.FRESH_ZERO3_BIRTH,true).commit());
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
            AtomicReference<Window> initialWindow=new AtomicReference<>();
            // V412: snapshot STARTING state and PRE physical GRU3 stats on
            // the same genuine Activity UI transaction BEFORE enabling MAX
            // speed; otherwise the Android compositor may advance decisions,
            // drop the observed Activity, or trigger onDraw while a different
            // test thread snapshots, corrupting the 10x1000 science baseline.
            instrument.runOnMainSync(()->{
                initial.set(original.originalV356DecisionsForQA());
                original.originalV400EnableLocalTemporalMotorCreditForQA();
                original.originalV414AllowVerifiedRearOnlyWallCreditForQA();
                original.originalV417EnableMeasuredBrakingCreditForQA();
                original.originalV418EnableIndependentDecisionCreditForQA();
                initialWindow.set(new Window(
                    original.originalV406TrueLifetimeMaterialContactsForQA(),
                    original.originalV400LocalMotorDiagnosticsForQA(),
                    original.originalV366MotorDiagnosticForQA(),
                    initial.get()));
                original.originalV366EnableRealTestMaxForQA();
            });
            assertTrue("fresh original GRU3 start",initial.get()>=0&&initial.get()<10);
            final int TARGET=10000, MAX_BURSTS=24000;
            final long finalCursor=initial.get()+TARGET;
            long cursor=initial.get(),nextBucket=1000;
            Window previous=initialWindow.get();
            final Window[] windows=new Window[11];
            windows[0]=previous;
            for(int burst=0;burst<MAX_BURSTS && cursor<finalCursor;burst++){
                AtomicLong result=new AtomicLong(-1);
                // The game advances autonomously on its normal UI clock.
                // Test thread only observes a sampled decision cursor.
                android.os.SystemClock.sleep(260);
                instrument.runOnMainSync(()->result.set(
                    original.originalV356DecisionsForQA()));
                cursor=result.get();
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
                    instrument.runOnMainSync(()->snap.set(new Window(
                        original.originalV406TrueLifetimeMaterialContactsForQA(),
                        original.originalV400LocalMotorDiagnosticsForQA(),
                        original.originalV366MotorDiagnosticForQA(),
                        original.originalV356DecisionsForQA())));
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
                    String status=now.metric(window,previous);
                    System.out.println(status);
                    android.util.Log.i("ZERO417_WILD_LONG",status);
                    previous=now;
                    nextBucket+=1000;
                }
                if(burst%200==0){
                    String errorState=original.originalV356BrainErrorForQA();
                    android.util.Log.i("ZERO417_WILD_LONG",
                        "ORIGINAL_LIFECYCLE cursor="+cursor+
                        " owner="+owner+" error="+errorState+
                        " latestGoal="+(nextBucket-1000)+
                        " originalBoot="+initial.get());
                }
            }
            AtomicReference<Window> endSnapshot=new AtomicReference<>();
            instrument.runOnMainSync(()->endSnapshot.set(new Window(
                original.originalV406TrueLifetimeMaterialContactsForQA(),
                original.originalV400LocalMotorDiagnosticsForQA(),
                original.originalV366MotorDiagnosticForQA(),
                original.originalV356DecisionsForQA())));
            Window last=endSnapshot.get();
            assertNotNull(last);
            if(windows[10]==null&&last.decisions-initial.get()>=TARGET){
                assertTrue(last.red>=previous.red&&last.blue>=previous.blue);
                String lastMetric=last.metric(10,previous);
                windows[10]=last;
                android.util.Log.i("ZERO417_WILD_LONG",lastMetric);
            }
            String motor=original.originalV366MotorDiagnosticForQA();
            String local=original.originalV400LocalMotorDiagnosticsForQA();
            android.util.Log.i("ZERO417_WILD_LONG","ZERO417_REAR_WALL_RULE "+local);
            assertTrue("Real wild trial must use V414 same original neural owner",
                local.contains("rearWallLab=true")&&
                local.contains("realVelocityCreditLab=true")&&
                local.contains("independentDecisionCreditLab=true"));
            double ratio=last.signed/Math.max(1d,last.gross);
            String outcome=String.format(Locale.ROOT,
                "ZERO417_WILD_REAL_10000_RESULT decisions=%d baseline=%d blue=%d red=%d"+
                " violet=%d signed=%.5f gross=%.5f efficiency=%.6f"+
                " localWeightUpdates=%d localHarmRejected=%d localTurnsFiltered=%d"+
                " motorOwner=%s",
                last.decisions,initial.get(),last.blue,last.red,last.violet,
                last.signed,last.gross,ratio,last.updates,last.harmRejects,
                last.turnFilters,original.originalV356NeuralOwnerForQA());
            System.out.println(outcome);
            android.util.Log.i("ZERO417_WILD_LONG",outcome);
            // SOURCE-LEVEL correctness is unconditional; BEHAVIOUR
            // acceptance is intentionally strict (no lowering thresholds).
            assertTrue("Must execute >=10000 COMPLETE original autonomous decisions: "+outcome,
                last.decisions-initial.get()>=TARGET);
            assertTrue("Real physical body must be moving: "+outcome,last.gross>100d);
            assertTrue("Actual GRU3 local cortex must update real original weights: "+outcome,
                last.updates>0&&local.contains("enabled=true"));
            assertTrue("Each thousand original decisions must be sampled, never 2k x5",
                java.util.Arrays.stream(windows).allMatch(x->x!=null));
            // We record all 10 windows even if scientific acceptance fails.
            // No red-specific hard approval from an environment with zero
            // observed harmful contact; note this as INCONCLUSIVE instead.
            assertTrue("Original GRU3 10k locomotion must exceed the pre-existing 12% gate: "+
                    outcome,ratio>=.12d);
            assertTrue("Original 10k world must yield genuinely positive material contacts: "+
                    outcome,last.blue+last.violet>0);
            // Red avoidance is evaluated independently per 1000. To claim
            // improvement we require genuine harmful exposure and a lower
            // rate in 2nd five-thousand than first five-thousand.
            long firstHalf=windows[5].red-windows[0].red,
                 lastHalf=windows[10].red-windows[5].red;
            assertTrue("Real harmful contact evidence is absent; cannot claim avoidance: "+
                outcome,firstHalf+lastHalf>0);
            assertTrue("Genuine late 5000-decisions red rate must improve: "+
                outcome,lastHalf<firstHalf);
            // Historical 10k V412 marked 90 red, 8 blue as 'success'.
            // This is behaviorally unacceptable. A physical correction
            // cannot ship while the real GRU3 keeps touching reds ~11x more
            // frequently than blues. Register this independent strict goal
            // before reading V414 test measurements.
            assertTrue("10k genuine original world is not a competent "+
                "hazard avoider if red contacts exceed twice actual blues: "+
                outcome,last.red<=2L*last.blue);
            android.util.Log.i("ZERO417_WILD_LONG",
                "ZERO417_WILD_RED_AVOIDANCE_STRICT_10000=PASS");
        } finally {
            instrument.runOnMainSync(original::finish);
            // No artificial Canvas or detached physics owner to recycle.
        }
    }
}
