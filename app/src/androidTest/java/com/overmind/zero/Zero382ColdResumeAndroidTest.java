package com.overmind.zero;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** User's exact screenshot failure: V381 auxiliaries are not a brain. */
@RunWith(AndroidJUnit4.class)
public final class Zero382ColdResumeAndroidTest {
    @Test public void corruptedMotorExtrasCannotStopRealV3BrainColdLoad()throws Exception{
        Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences prefs=ctx.getSharedPreferences("zero_motorbrain_001",0);
        assertTrue(prefs.edit().clear()
            .putBoolean(SymbioteMatterV1.ENABLED,true).commit());
        Zero3OriginalBrainV356 source=Zero3OriginalBrainV356.load(ctx,"",false);
        String main=source.checkpoint();
        long decisions=source.decisions();source.close();
        assertTrue("V3 checkpoint must exist",main.length()>1000);
        assertTrue(prefs.edit()
            .putString(Zero3OriginalBrainV356.KEY,main)
            .putString(Zero381MotorCalibration.KEY,"zmr381|1|0|broken")
            .putString(Zero381MotorFlightRecorder.KEY,"zmf381|1|256\nbroken row")
            .commit());
        // First: direct original brain restores independently of diagnostics.
        Zero3OriginalBrainV356 recovered=Zero3OriginalBrainV356.load(ctx,main,true);
        recovered.restoreMotorObservationsV381(
            prefs.getString(Zero381MotorCalibration.KEY,""),
            prefs.getString(Zero381MotorFlightRecorder.KEY,""));
        assertEquals(decisions,recovered.decisions());
        assertEquals(main,recovered.checkpoint());
        assertFalse(recovered.auxiliaryRestoreWarningV382().isEmpty());
        recovered.close();
        // Second: the same physical GameActivity cold-opens and saves,
        // without replacing the authenticated real V356 brain with a virgin.
        Instrumentation test=InstrumentationRegistry.getInstrumentation();
        for(int attempt=0;attempt<2;attempt++){
            Instrumentation.ActivityMonitor mon=test.addMonitor(
                GameActivity.class.getName(),null,false);
            GameActivity activity=null;
            try{
                Intent launch=new Intent(ctx,GameActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ctx.startActivity(launch);
                activity=(GameActivity)test.waitForMonitorWithTimeout(mon,30000);
                assertNotNull("Real game must cold-launch",activity);
                final GameActivity game=activity;
                test.runOnMainSync(()->{
                    assertTrue("Cold load blocked by corrupted optional data: "+
                        game.originalV356BrainErrorForQA(),
                        game.originalV356NeuralOwnerForQA());
                    assertEquals("Original brain was silently replaced",decisions,
                        game.originalV356DecisionsForQA());
                });
            }finally{
                if(activity!=null){
                    final GameActivity closing=activity;
                    test.runOnMainSync(closing::finish);
                    test.waitForIdleSync();
                }
                test.removeMonitor(mon);
            }
            assertTrue("Original neural checkpoint must remain intact",
                prefs.getString(Zero3OriginalBrainV356.KEY,"").length()>1000);
        }
    }
    @Test public void independentlyCorruptAuxArchivesDoNotEraseValidCounterparts()throws Exception{
        Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        Zero3OriginalBrainV356 original=Zero3OriginalBrainV356.load(ctx,"",false);
        String checkpoint=original.checkpoint();
        original.restoreMotorObservationsV381("garbage","");
        assertFalse(original.auxiliaryRestoreWarningV382().isEmpty());
        assertEquals(0,original.motorResponseSamplesV381());
        assertEquals(0,original.flightCountV381());
        assertEquals(checkpoint,original.checkpoint());
        // Recovery should never create a fake learned direction or training.
        Zero381MotorCalibration fresh=new Zero381MotorCalibration();
        assertTrue(original.motorResponseCheckpointV381().equals(fresh.encode()));
        original.close();
    }
}