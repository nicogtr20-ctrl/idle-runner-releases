package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * The existing 12-save 3.0 format must survive serious autonomous learning.
 * The original V356 checkpoint counted long but arbitrarily refused to load
 * if count >100000. V319 validation multiplied two ints (overflow after
 * 134217727 decisions) and rejected otherwise valid persisted hand memories.
 * This test changes neither format, weights nor the 194 original sensors.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero407ExtendedCheckpointSafetyAndroidTest {
    @Test public void originalV356NoLongerExpiresAfter100000RealDecisions(){
        assertTrue(Zero3OriginalBrainV356.supportedDecisionCounterV407(0));
        assertTrue(Zero3OriginalBrainV356.supportedDecisionCounterV407(33312));
        assertTrue(Zero3OriginalBrainV356.supportedDecisionCounterV407(100000));
        assertTrue("Previously refused actual trained user brain after 100k actions",
            Zero3OriginalBrainV356.supportedDecisionCounterV407(100001));
        assertTrue(Zero3OriginalBrainV356.supportedDecisionCounterV407(500000));
        assertTrue(Zero3OriginalBrainV356.supportedDecisionCounterV407(
            Integer.MAX_VALUE-1L));
        assertFalse(Zero3OriginalBrainV356.supportedDecisionCounterV407(-1L));
        assertFalse("V319 32-bit grip owner must not wrap",
            Zero3OriginalBrainV356.supportedDecisionCounterV407(
                Integer.MAX_VALUE));
        assertFalse(Zero3OriginalBrainV356.supportedDecisionCounterV407(
            (long)Integer.MAX_VALUE+1L));
    }

    @Test public void realGripCheckpointCreditCountNeverOverflowsBeforeLongRun(){
        float[] weights=new float[Zero3ContextAdhesionV319.SECTORS*
            Zero3ContextAdhesionV319.FEATURES];
        // 2_000_000_000 * 16 wraps as signed int and was NEGATIVE,
        // making a perfectly valid 3.0 grip memory impossible to restore.
        Zero3ContextAdhesionV319.State valid=
            new Zero3ContextAdhesionV319.State(
                weights,0x319L,true,2_000_000_000,1_000_000,
                0d,0d,0d);
        assertEquals(2_000_000_000,valid.decisions);
        assertEquals(1_000_000,valid.realCreditEvents);
        Zero3ContextAdhesionV319 head=new Zero3ContextAdhesionV319(0x319L,true);
        head.restoreAtDecisionBoundary(valid);
        Zero3ContextAdhesionV319.State copy=head.captureAtDecisionBoundary();
        assertEquals(valid.decisions,copy.decisions);
        assertEquals(valid.realCreditEvents,copy.realCreditEvents);
        try{
            new Zero3ContextAdhesionV319.State(weights,0x319L,
                true,2,33,0,0,0);
            fail("More than 16 physical grip credits per decision is invalid");
        }catch(IllegalArgumentException expected){}
    }
}
