package com.overmind.zero;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.Assert.*;

/**
 * V366 SCIENTIFIC BLIND BEHAVIOUR GATE, not a scripted motor test.
 * Run the original Android game, freshly newborn V356 GRU, 2000 COMPLETE
 * autonomous intentions and every real frame in existing TEST MAX engine.
 * No target coordinates, reward editing, synthetic damage, prescribed
 * muscle outputs, injected sample, or 'red' condition read by the actor.
 *
 * This test MAY FAIL; a failure prohibits OTA, never retune the threshold
 * after seeing these 2000 real experiences.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero366OriginalAutonomousSurvival2000Test {
    private static double field(String diagnostic,String key){
        String m="|"+key+"=";
        int i=diagnostic.indexOf(m);
        if(i<0)throw new AssertionError("missing genuine V361 motor statistic "+key);
        int start=i+m.length(),end=diagnostic.indexOf('|',start);
        if(end<0)end=diagnostic.length();
        return Double.parseDouble(diagnostic.substring(start,end));
    }
    private static int recordedHarms(String serialized){
        if(!serialized.startsWith("ls2|"))throw new AssertionError("Not original life statistics");
        String[] pieces=serialized.split("\\|");
        int completed=Integer.parseInt(pieces[1]);
        int historyCount=Integer.parseInt(pieces[3]);
        assertTrue("recorded life count is bounded by actual death events",
            completed>=0&&historyCount>=0&&historyCount<=50&&completed>=historyCount);
        int harms=0;
        // ls2 wire: count|CURRENT|HISTORY_COUNT|life0|life1..., not
        // count|life0|life1...! Include ongoing-life harm too, because
        // source contacts are real even if ZERO has not died yet.
        for(int i=0;i<=historyCount;i++){
            int index=i==historyCount?2:4+i;
            String[] life=pieces[index].split(",");
            harms+=Integer.parseInt(life[2]);
        }
        return harms;
    }
    @Test(timeout=900000)
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
        Bitmap surface=Bitmap.createBitmap(720,1080,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(surface);
        try {
            // Original organism is intentionally continuously repainting.
            assertTrue("only real V3 GRU may drive body",original.originalV356NeuralOwnerForQA());
            assertEquals("must be a virgin causal actor",0,original.originalV356DecisionsForQA());
            final int TARGET=2000, MAX_BURSTS=9500;
            int earlyHarm=-1,earlyPickups=-1;
            long cursor=0;
            for(int burst=0;burst<MAX_BURSTS && cursor<TARGET;burst++){
                AtomicLong result=new AtomicLong(-1);
                instrument.runOnMainSync(()->result.set(
                    original.originalV366AdvanceActualWorldForQA(canvas)));
                cursor=result.get();
                String error=original.originalV356BrainErrorForQA();
                assertTrue("real world must not stop due to neural or physics error: "+error,
                    error==null||error.isEmpty());
                if(earlyHarm<0&&cursor>=1000){
                    earlyHarm=recordedHarms(original.originalV366ActualLifeStatisticsForQA());
                    earlyPickups=original.originalV366ActualPositivePickupsForQA();
                }
                if(burst%200==0){
                    String diag=original.originalV366MotorDiagnosticForQA();
                    System.out.printf(Locale.ROOT,
                        "ZERO366_TRUE_BLIND_PROGRESS decisions=%d picked=%d harmCompleted=%d gross=%.3f directed=%.3f%n",
                        cursor,original.originalV366ActualPositivePickupsForQA(),
                        recordedHarms(original.originalV366ActualLifeStatisticsForQA()),
                        field(diag,"grossDistance"),field(diag,"signedProgress"));
                }
            }
            int pickups=original.originalV366ActualPositivePickupsForQA();
            int red=recordedHarms(original.originalV366ActualLifeStatisticsForQA());
            String motor=original.originalV366MotorDiagnosticForQA();
            double signed=field(motor,"signedProgress"),gross=field(motor,"grossDistance");
            System.out.printf(Locale.ROOT,
                "ZERO366_BLIND_2000_REAL_ORIGINAL_RESULT decisions=%d blue=%d red=%d redFirst=%d redLater=%d blueFirst=%d signed=%.5f gross=%.5f efficiency=%.4f actualX=%+.4f actualY=%+.4f%n",
                cursor,pickups,red,earlyHarm,earlyHarm<0?-1:red-earlyHarm,
                earlyPickups,signed,gross,signed/Math.max(1,gross),
                field(motor,"actualX"),field(motor,"actualY"));
            assertTrue("must finish 2000 genuine GRU intentions, NO lab script",cursor>=TARGET);
            assertTrue("must physically follow intent far better than OTA363 1.10% ratio",
                signed/Math.max(1,gross)>=.12);
            assertTrue("must learn to collect positive energy in the ORIGINAL habitat",
                pickups>0);
            assertTrue("early half must contain comparable physical red-contact evidence",
                earlyHarm>=0);
            assertTrue("real late red harm must improve on early contact rate",
                red-earlyHarm<earlyHarm);
            System.out.println("ZERO366_BLIND_2000_UNSCRIPTED_SURVIVAL_G3_G5_PRELIMINARY=PASS_NOT_PHONE");
        } finally {
            instrument.runOnMainSync(original::finish);
            surface.recycle();
        }
    }
}
