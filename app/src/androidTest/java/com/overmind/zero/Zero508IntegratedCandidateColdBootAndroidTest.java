package com.overmind.zero;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO502: a real Android candidate build MUST activate the restored V489
 * original neural settings AND ZERO499 true-anatomy transducer on cold birth,
 * before any motor decision. Normal/stable build does not opt-in. Never
 * install onto user's phone here. No second actor, no archived V2.2 head.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero508IntegratedCandidateColdBootAndroidTest {
    @Test(timeout=150000)
    public void actualOriginalActivityBootsAsCandidateWithV499AndSavedV356()
            throws Exception {
        Instrumentation instrument=InstrumentationRegistry.getInstrumentation();
        Context c=instrument.getTargetContext();
        assertTrue("Must test candidate-flagged binary, not ordinary debug",
            c.getResources().getBoolean(R.bool.zero3_candidate));
        SharedPreferences prefs=c.getSharedPreferences(
            SaveSlotStore.WORKING,Context.MODE_PRIVATE);
        assertTrue("Fresh birth must not load a historical or reset brain",
            prefs.edit().clear().putBoolean(SymbioteMatterV1.ENABLED,true)
                .putBoolean(SaveSlotStore.FRESH_ZERO3_BIRTH,true).commit());
        Intent intent=new Intent(c,GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
            Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Instrumentation.ActivityMonitor monitor=instrument.addMonitor(
            GameActivity.class.getName(),null,false);
        GameActivity launched;
        try {
            c.startActivity(intent);
            Activity a=instrument.waitForMonitorWithTimeout(monitor,30000);
            assertTrue("Actual original GameActivity must boot",a instanceof GameActivity);
            launched=(GameActivity)a;
        }finally{instrument.removeMonitor(monitor);}
        try {
            boolean ready=false;
            AtomicReference<String> diagnostic=new AtomicReference<>("not started");
            for(int k=0;k<250&&!ready;k++){
                AtomicBoolean check=new AtomicBoolean(false);
                instrument.runOnMainSync(()->{
                    check.set(launched.originalV356NeuralOwnerForQA()&&
                        launched.originalV499PostureMotorEnabledForQA()&&
                        launched.originalV507MuscleActivationEnabledForQA()&&
                        launched.originalV443MotorEvidenceDiagnosticsForQA()
                            .contains("v503OpticalEnabled=true"));
                    diagnostic.set("owner="+launched.originalV356NeuralOwnerForQA()+
                        " V499="+launched.originalV499PostureMotorEnabledForQA()+
                        " V507="+launched.originalV507MuscleActivationEnabledForQA()+
                        " V503="+launched.originalV443MotorEvidenceDiagnosticsForQA()
                            .contains("v503OpticalEnabled=true")+
                        " error="+launched.originalV356BrainErrorForQA()+
                        " decisions="+launched.originalV356DecisionsForQA());
                });
                ready=check.get();
                if(!ready)android.os.SystemClock.sleep(100);
            }
            assertTrue("Original candidate must activate V499 on real game boot: "+
                diagnostic.get(),ready);
            assertTrue("Original GRU3 checkpoint was actually committed",
                prefs.getString(Zero3OriginalBrainV356.KEY,"").length()>1000);
            android.util.Log.i("ZERO508_PHONE",
                "ORIGINAL_CANDIDATE_V508_INTEGRATED_SENSORIMOTOR_BOOT_PASS "+
                diagnostic.get());
        }finally{
            instrument.runOnMainSync(launched::finish);
        }
    }
}
