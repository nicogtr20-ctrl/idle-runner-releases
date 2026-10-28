package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Original autoprofile checkpoints cannot stochastically erase an in-flight
 * real GRU3 motor sample because an unrelated UI wall-clock save timer fired.
 * The normal Activity pause/stop explicit flush is unchanged.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero410OriginalNeuralDecisionBoundaryCheckpointAndroidTest {
    @Test public void pendingOriginalMuscleNeverAbortedByWallClockTimer(){
        final long twelve=12_000_000_000L;
        final long now=18_000_000_000L,prior=0L;
        assertFalse("Cortex decision is in flight even though timer expired",
            GameActivity.originalV410MayAutoCheckpoint(true,now,prior,true));
        assertTrue("Same physical state must save on FIRST natural boundary",
            GameActivity.originalV410MayAutoCheckpoint(true,now,prior,false));
        assertFalse("Do not save every 60hz frame",
            GameActivity.originalV410MayAutoCheckpoint(true,twelve,prior,false));
        assertTrue(GameActivity.originalV410MayAutoCheckpoint(
            true,twelve+1,prior,false));
    }
    @Test public void saveClockCannotTriggerWhileGamePausedOrFromCorruptedTime(){
        assertFalse(GameActivity.originalV410MayAutoCheckpoint(
            false,40_000_000_000L,0L,false));
        assertFalse(GameActivity.originalV410MayAutoCheckpoint(
            true,3_000_000_000L,7_000_000_000L,false));
        // V410 uses monotonic elapsed difference: adding a huge uptime
        // cannot wrap priorSave + 12s into a negative signed long.
        long before=Long.MAX_VALUE-4_000_000_000L;
        assertFalse(GameActivity.originalV410MayAutoCheckpoint(
            true,Long.MAX_VALUE-1L,before,false));
        assertTrue(GameActivity.originalV410MayAutoCheckpoint(
            true,Long.MAX_VALUE-1L,Long.MAX_VALUE-18_000_000_000L,false));
    }
    @Test public void alwaysPreservePriorOriginalCortexAndSaveFormat(){
        assertEquals("GRU3 dimensionality unaffected",194,
            Zero3ConfigurationV354.INPUTS);
        assertEquals(64,Zero3ConfigurationV354.GRU);
        assertEquals(12,Zero3ConfigurationV354.PROFILES);
        assertEquals(16,Zero3ConfigurationV354.SECTORS);
        assertEquals(4,Zero3ConfigurationV354.CHANNELS);
    }
}
