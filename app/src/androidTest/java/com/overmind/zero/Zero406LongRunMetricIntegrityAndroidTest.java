package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Zero406: no false late red avoidance after the rolling last-50-lives
 * UI sample is exceeded. Original cumulative counters remain authoritative.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero406LongRunMetricIntegrityAndroidTest {
    @Test public void actualHarmCannotDisappearWhenUserExceedsFiftyRealLives(){
        LifeStatistics history=new LifeStatistics();
        EnergyLifetimeCounters lifetime=new EnergyLifetimeCounters();
        for(int life=0;life<81;life++){
            history.tick(1f);
            history.contact(-7d);
            lifetime.record(-7f,false);
            history.death();
        }
        assertEquals(81,history.completed());
        assertEquals(50,history.historyCount());
        assertEquals("True lifetime harmful contacts never go backwards",
            81L,lifetime.red);
        assertEquals("Original lifetime aggregate must also retain 81 harms",
            81d*7d,history.lifetimeAverage(LifeStatistics.DAMAGE)*
                history.lifetimeCount(),1e-5);
        String saved=lifetime.encode();
        EnergyLifetimeCounters restored=new EnergyLifetimeCounters();
        assertTrue(restored.restore(saved));
        assertEquals(81L,restored.red);
        // The old 2k test incorrectly counted only 50 most recent lives;
        // it would label 31 genuine harms as if they never happened.
        long oldUiOnly=0;
        for(int i=0;i<history.historyCount();i++)
            oldUiOnly+=history.recent(i).harm;
        assertEquals(50L,oldUiOnly);
        assertEquals(31L,restored.red-oldUiOnly);
    }

    @Test public void originalSingleMuscleEligibilityRejectsPassiveDrift(){
        assertTrue(Zero401LocalMotorCreditSafety.hasPhysicalMotorProbe(.5f,1));
        assertFalse(Zero401LocalMotorCreditSafety.hasPhysicalMotorProbe(0f,1));
        assertFalse(Zero401LocalMotorCreditSafety.hasPhysicalMotorProbe(.005f,1));
        assertFalse(Zero401LocalMotorCreditSafety.hasPhysicalMotorProbe(.5f,0));
        assertFalse(Zero401LocalMotorCreditSafety.hasPhysicalMotorProbe(.5f,2));
        try{
            Zero401LocalMotorCreditSafety.hasPhysicalMotorProbe(Float.NaN,1);
            fail("Corrupted muscle effort must fail closed");
        }catch(IllegalArgumentException expected){}
    }

    @Test public void noFalseMotorGradientForActualRedOrOppositeFutureChoice(){
        assertFalse(Zero401LocalMotorCreditSafety.cleanPhysicalDecision(
            true,true,false,false,false,false,false,false,7f));
        assertFalse(Zero401LocalMotorCreditSafety.cleanPhysicalDecision(
            true,true,false,false,false,false,false,false,.01f));
        assertEquals(0f,Zero401LocalMotorCreditSafety.nextDecisionDelayValidity(
            1f,0f,-1f,0f),0f);
    }
}
