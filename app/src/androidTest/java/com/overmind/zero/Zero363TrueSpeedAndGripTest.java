package com.overmind.zero;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

/** Original Android app: V3-only passive traction, no neural reset,
 * and valid learning eligibility for voluntarily held material.
 * NO preset gait, resource steering, passive motor or fake energy. */
@RunWith(AndroidJUnit4.class)
public final class Zero363TrueSpeedAndGripTest {
    private final Instrumentation instrument=InstrumentationRegistry.getInstrumentation();
    private void ui(Runnable r){instrument.runOnMainSync(r);}
    private GameActivity launch(Context c){
        Intent intent=new Intent(c,GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Instrumentation.ActivityMonitor m=instrument.addMonitor(GameActivity.class.getName(),null,false);
        try{
            c.startActivity(intent);
            Activity a=instrument.waitForMonitorWithTimeout(m,30000);
            assertNotNull(a);assertTrue(a instanceof GameActivity);
            return (GameActivity)a;
        }finally{instrument.removeMonitor(m);}
    }
    private float floor(GameActivity g){
        AtomicReference<Float> a=new AtomicReference<>(-1f);
        ui(()->a.set(g.originalV363OriginalBodyTractionForQA()));
        return a.get();
    }
    private void done(GameActivity g){ui(g::finish);SystemClock.sleep(700);}

    @Test public void voluntaryGripIsNotAForeignMotorButActualWorldCorrectionStillIs(){
        assertTrue("legacy 2.2 keeps its prior strict hold semantics",
            MotorResponseJournalV172.externalMotionContaminated(0,0,0,false,true));
        assertFalse("V3 owns and can learn from self-chosen verified grip",
            MotorResponseJournalV172.externalMotorInterferenceV363(0,0,0,false));
        assertTrue("world collision correction remains a V3 veto",
            MotorResponseJournalV172.externalMotorInterferenceV363(.09f,0,0,false));
        assertTrue("actual moved objects remain a V3 veto",
            MotorResponseJournalV172.externalMotorInterferenceV363(0,0,.031f,false));
        assertTrue("storm remains a V3 veto",
            MotorResponseJournalV172.externalMotorInterferenceV363(0,0,0,true));
        assertTrue("invalid numeric physical sample remains a veto",
            MotorResponseJournalV172.externalMotorInterferenceV363(Float.NaN,0,0,false));
    }
    @Test public void v3OnlyFloorCalibratesWithoutChangingOldBodyPhysics(){
        SymbioteMatterV1 oldGel=new SymbioteMatterV1();
        assertEquals("V2.2 unchanged",.12f,oldGel.passiveFloorBaselineV363(),0f);
        SymbioteMatterV1 newGel=new SymbioteMatterV1();
        newGel.enableOriginalZero3TractionV363();
        assertEquals(.08f,newGel.passiveFloorBaselineV363(),0f);
        assertEquals(.12f,oldGel.passiveFloorBaselineV363(),0f);
        assertTrue("lower floor drag at otherwise identical local loading",
            SymbioteMatterV1.substrateTractionWithBaselineV363(.1f,.5f,.5f,.08f)<
            SymbioteMatterV1.substrateTraction(.1f,.5f,.5f));
        assertEquals("compression footholds retain the same 1.45 law",
            SymbioteMatterV1.substrateTraction(-.2f,.4f,.4f)-
            SymbioteMatterV1.substrateTractionWithBaselineV363(-.2f,.4f,.4f,.08f),
            .04f,1e-5f);
    }
    @Test public void realOriginalV3GameStartsAndRestoresCalibratedWithSameNeuralCheckpoint()throws Exception {
        Context context=instrument.getTargetContext();
        SharedPreferences p=context.getSharedPreferences(SaveSlotStore.WORKING,Context.MODE_PRIVATE);
        p.edit().clear().putBoolean(SymbioteMatterV1.ENABLED,true).commit();
        GameActivity first=launch(context);
        try{
            assertEquals("original V3 world's real material uses tested lower drag",.08f,floor(first),0f);
            assertTrue(first.originalV356DecisionsForQA()>=0);
        }finally{done(first);}
        String checkpoint=p.getString(Zero3OriginalBrainV356.KEY,"");
        assertTrue("V356 neural weights must be intact",checkpoint.length()>1000);
        long cursor=Zero3OriginalBrainV356.inspectSaved(checkpoint).decisions;
        GameActivity restored=launch(context);
        try{
            assertEquals("resume is NOT a return to high friction",.08f,floor(restored),0f);
            assertTrue("resume must NEVER clear saved old brain",restored.originalV356DecisionsForQA()>=cursor);
        }finally{done(restored);}
    }
}
