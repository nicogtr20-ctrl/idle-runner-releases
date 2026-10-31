package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V431 original 170-material PRE/POST evidence eligibility, never a motor command.
 * The actual GameActivity 2x10000 tests validate full behavior separately.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero431RearWallMuscleEligibilityAndroidTest {
    private static boolean rear(float displacement,float pre,float post){
        return Zero431RearWallMuscleEligibility.verifiedRearRetreat(
            true,12,0,displacement,pre,0f,post,0f,1f,0f);
    }
    @Test public void passiveOutwardCoastingCannotTeachOriginalLocalCortex(){
        assertTrue("V414 previously admitted this measured displacement",
            Zero401LocalMotorCreditSafety.rearWallCanTeachMuscleV414(
                true,12,0,.20f));
        assertFalse("slowing positive drift is not a motor acceleration",
            rear(.20f,3f,1f));
        assertFalse("constant outward drift is not motor acceleration",
            rear(.20f,2f,2f));
    }
    @Test public void realMeasuredAccelerationAndRearOnlyContactRemainEligible(){
        assertTrue(rear(.20f,0f,1f));
        assertTrue(rear(.20f,-2f,-1f));
        assertFalse(rear(.01f,0f,2f));
    }
    @Test public void frontAndMixedWallsCannotBypassExistingV414(){
        assertFalse(Zero431RearWallMuscleEligibility.verifiedRearRetreat(
            true,0,12,1f,0f,0f,2f,0f,1f,0f));
        assertFalse(Zero431RearWallMuscleEligibility.verifiedRearRetreat(
            true,12,1,1f,0f,0f,2f,0f,1f,0f));
        assertFalse(Zero431RearWallMuscleEligibility.verifiedRearRetreat(
            false,12,0,1f,0f,0f,2f,0f,1f,0f));
    }
    @Test public void lateralAccelerationIsNotForwardMotorEvidence(){
        assertFalse(Zero431RearWallMuscleEligibility.verifiedRearRetreat(
            true,12,0,.2f,0f,0f,0f,3f,1f,0f));
    }
    @Test public void originalV401StillRejectsHarmAndMissingProbe(){
        assertFalse(Zero401LocalMotorCreditSafety.cleanPhysicalDecision(
            true,true,false,false,false,false,false,false,7f));
        assertFalse(Zero401LocalMotorCreditSafety.hasPhysicalMotorProbe(.5f,0));
        assertTrue(Zero401LocalMotorCreditSafety.cleanPhysicalDecision(
            true,true,false,false,false,false,false,false,0f));
    }
    @Test public void nonfiniteMeasuredCOMFailsClosed(){
        try{
            Zero431RearWallMuscleEligibility.verifiedRearRetreat(
                true,12,0,.2f,Float.NaN,0f,1f,0f,1f,0f);
            fail("invalid PRE COM cannot teach");
        }catch(IllegalArgumentException expected){}
    }
}
