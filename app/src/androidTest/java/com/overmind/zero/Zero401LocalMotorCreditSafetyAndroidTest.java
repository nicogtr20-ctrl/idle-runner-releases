package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** True original GRU3 motor training must never credit a red collision,
    a storm impulse or a changed-heading successor action as locomotion. */
@RunWith(AndroidJUnit4.class)
public final class Zero401LocalMotorCreditSafetyAndroidTest {
    private boolean qualifies(float harm,boolean solid,boolean correction,
            boolean heldout,boolean terminal){
        return Zero401LocalMotorCreditSafety.cleanPhysicalDecision(
            true,true,heldout,terminal,correction,correction,false,solid,harm);
    }
    @Test public void realMeasuredRedAndWeatherHarmNeverBecomeMotorLearningSuccess(){
        assertTrue("Uncontaminated original muscle acts can learn",
            qualifies(0f,false,false,false,false));
        assertFalse("An actual damaging red contact must not train a muscle",
            qualifies(7f,false,false,false,false));
        assertFalse("Real storm energy damage is an exogenous event",
            qualifies(.05f,false,false,false,false));
        assertFalse("Real wall impulse can be a phantom motor gradient",
            qualifies(0f,true,false,false,false));
        assertFalse("Teleport or collision correction is not motor effort",
            qualifies(0f,false,true,false,false));
        assertFalse("Heldout brain weights never update",
            qualifies(0f,false,false,true,false));
        assertFalse("Terminal GRU decision cannot fabricate a full motor credit",
            qualifies(0f,false,false,false,true));
        assertFalse("No physical muscle evidence cannot train",
            Zero401LocalMotorCreditSafety.cleanPhysicalDecision(
                true,false,false,false,false,false,false,false,0f));
        try{
            qualifies(Float.NaN,false,false,false,false);
            fail("Nonfinite real damage must fail closed");
        }catch(IllegalArgumentException expected){}
    }
    @Test public void delayedResponseCannotCrossAbruptRealNeuralReversal(){
        assertEquals("Same intention retains the already validated V332 delay",
            1f,Zero401LocalMotorCreditSafety.nextDecisionDelayValidity(1,0,1,0),1e-6f);
        assertEquals("Same direction with different normalization is harmless",
            1f,Zero401LocalMotorCreditSafety.nextDecisionDelayValidity(1,0,2,0),1e-6f);
        float halfTurn=Zero401LocalMotorCreditSafety.nextDecisionDelayValidity(
            1,0,(float)Math.sqrt(3f)/2f,.5f);
        assertTrue("Natural 30 degree turn attenuates not reverses credit",
            halfTurn>0f&&halfTurn<1f);
        assertEquals("A perpendicular new GRU action does not belong to old muscle",
            0f,Zero401LocalMotorCreditSafety.nextDecisionDelayValidity(1,0,0,1),1e-6f);
        assertEquals("Opposite next choice cannot reward prior muscle",
            0f,Zero401LocalMotorCreditSafety.nextDecisionDelayValidity(1,0,-1,0),1e-6f);
        assertEquals("Stationary valid zero vector cannot support delayed evidence",
            0f,Zero401LocalMotorCreditSafety.nextDecisionDelayValidity(0,0,1,0),1e-6f);
    }
    @Test public void harmfulRealS21EpisodeExcludesPriorTemporalTrace(){
        // Same energy debit as a real red material and same V400 delayed
        // completed-experience gate. No artificial negative neural reward.
        float physicalRedHarm=7f;
        boolean v400PreviouslyAllowed=true;
        boolean v401=Zero401LocalMotorCreditSafety.cleanPhysicalDecision(
            true,true,false,false,false,false,false,false,physicalRedHarm);
        assertTrue(v400PreviouslyAllowed);
        assertFalse("V401 must reject genuine red contamination",v401);
        assertEquals(0f,Zero401LocalMotorCreditSafety.nextDecisionDelayValidity(
            -.4f,.916515f,.4f,-.916515f),1e-6f);
    }
}
