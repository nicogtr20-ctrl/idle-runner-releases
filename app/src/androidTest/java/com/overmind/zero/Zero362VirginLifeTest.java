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

/**
 * Real original Android GameActivity (not a parallel V3 world).
 * Must distinguish explicit NEW ZERO from one-time old V355 migration;
 * must preserve saved V356 neural weights on a regular cold reopen.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero362VirginLifeTest {
    private final Instrumentation instrument=InstrumentationRegistry.getInstrumentation();
    private void ui(Runnable action){instrument.runOnMainSync(action);}
    private GameActivity launch(Context ctx){
        Intent intent=new Intent(ctx,GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Instrumentation.ActivityMonitor monitor=instrument.addMonitor(
            GameActivity.class.getName(),null,false);
        try {
            ctx.startActivity(intent);
            Activity created=instrument.waitForMonitorWithTimeout(monitor,30000);
            assertNotNull("original game must launch",created);
            assertTrue(created instanceof GameActivity);
            return (GameActivity)created;
        }finally{instrument.removeMonitor(monitor);}
    }
    private long decisions(GameActivity g){
        java.util.concurrent.atomic.AtomicLong result=new java.util.concurrent.atomic.AtomicLong();
        ui(()->result.set(g.originalV356DecisionsForQA()));
        return result.get();
    }
    private float fuel(GameActivity g){
        AtomicReference<Float> result=new AtomicReference<>(-1f);
        ui(()->result.set(g.originalV362LifeStartFuelForQA()));
        return result.get();
    }
    private void finish(GameActivity g){ui(g::finish);SystemClock.sleep(700);}
    @Test public void explicitNewGameStartsAtZeroAndFullFuelWithoutDeletingOldBrain()throws Exception {
        Context ctx=instrument.getTargetContext();
        SharedPreferences working=ctx.getSharedPreferences(SaveSlotStore.WORKING,Context.MODE_PRIVATE);
        working.edit().clear().putBoolean(SymbioteMatterV1.ENABLED,true).commit();
        GameActivity old=launch(ctx);
        try {
            assertEquals(100f,fuel(old),0f);
            long earlier=decisions(old);
            assertTrue(earlier>=0);
        }finally {finish(old);}
        // First game checkpoint must exist so the old profile can be rescued.
        String previous=working.getString(Zero3OriginalBrainV356.KEY,"");
        assertTrue(previous.length()>1000);
        long previousDecisions=Zero3OriginalBrainV356.inspectSaved(previous).decisions;
        SaveSlotStore store=new SaveSlotStore(ctx);
        store.newGame();
        assertTrue("fresh-birth marker must survive until atomic neural commit",
            working.getBoolean(SaveSlotStore.FRESH_ZERO3_BIRTH,false));
        assertFalse("old trained GRU must not be in newborn working profile",
            working.contains(Zero3OriginalBrainV356.KEY));
        GameActivity newborn=launch(ctx);
        long justBorn;
        try {
            assertEquals("no inherited 70-92 fuel",100f,fuel(newborn),0f);
            assertFalse("virgin marker must be cleared with real V3 checkpoint",
                working.getBoolean(SaveSlotStore.FRESH_ZERO3_BIRTH,false));
            String fresh=working.getString(Zero3OriginalBrainV356.KEY,"");
            assertTrue("newborn checkpoint persisted before any Android frame",fresh.length()>1000);
            assertEquals("fresh birth MUST be zero decisions; NEVER old 4000",0,
                Zero3OriginalBrainV356.inspectSaved(fresh).decisions);
            justBorn=decisions(newborn);
            assertTrue("fresh GRU cannot inherit old history",justBorn<100);
        }finally{finish(newborn);}
        String bornSaved=working.getString(Zero3OriginalBrainV356.KEY,"");
        long beforeRestart=Zero3OriginalBrainV356.inspectSaved(bornSaved).decisions;
        GameActivity resumed=launch(ctx);
        try {
            assertEquals("restart must restore the SAME physical life without 100 fuel reset",
                100f,fuel(resumed),0f);
            assertTrue("saved young GRU has not regressed or imported 4000",
                decisions(resumed)>=beforeRestart&&decisions(resumed)<100);
        }finally{finish(resumed);}
        assertEquals("old complete GRU is still in separate recovery slot",
            true,store.recovery()!=null);
        assertTrue(previousDecisions>=0);
    }
    @Test public void resetAllStartsANewZero3WithoutAccidentallyDisablingSymbiote()throws Exception {
        Context ctx=instrument.getTargetContext();
        SharedPreferences working=ctx.getSharedPreferences(SaveSlotStore.WORKING,Context.MODE_PRIVATE);
        working.edit().clear().putBoolean(SymbioteMatterV1.ENABLED,true).commit();
        GameActivity first=launch(ctx);
        try {assertEquals(100f,fuel(first),0f);}
        finally {finish(first);}
        assertTrue(working.edit().putString(ResetDomains.REQUEST,ResetDomains.ALL).commit());
        GameActivity renewed=launch(ctx);
        try {
            assertEquals(100f,fuel(renewed),0f);
            assertTrue(working.getBoolean(SymbioteMatterV1.ENABLED,false));
            assertFalse(working.getBoolean(SaveSlotStore.FRESH_ZERO3_BIRTH,false));
            assertEquals(0,Zero3OriginalBrainV356.inspectSaved(
                working.getString(Zero3OriginalBrainV356.KEY,"")).decisions);
            assertTrue(decisions(renewed)<100);
        }finally{finish(renewed);}
    }
}
