package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V432 signed physical-response eligibility; 2x10000 actual Android
 * original-body tests separately decide behavioral acceptance. */
@RunWith(AndroidJUnit4.class)
public final class Zero432RearWallDeliveredProbeAndroidTest {
    private static Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe(
            int channel,float actual){
        float[] delivered=new float[64];
        if(channel>=0&&channel<4)delivered[4*3+channel]=actual;
        return new Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332(
            channel,0,new float[64],new float[64],delivered);
    }
    @Test public void deliveredNegativeMotorFeedbackIsNotCensored(){
        assertFalse(Zero420MotorMapEligibility.measuredForwardAccelerationV430(
            3f,0f,1f,0f,1f,0f));
        assertTrue(Zero432RearWallDeliveredProbe.eligible(
            true,12,0,.2f,probe(0,-.03f)));
    }
    @Test public void positiveDeliveredMotorEvidenceIsAlsoEligible(){
        assertTrue(Zero432RearWallDeliveredProbe.eligible(
            true,12,0,.2f,probe(1,.03f)));
    }
    @Test public void saturatedAndMissingDeliveredPerturbationsAreRejected(){
        assertFalse(Zero432RearWallDeliveredProbe.eligible(
            true,12,0,.2f,probe(0,0f)));
        assertFalse(Zero432RearWallDeliveredProbe.eligible(
            true,12,0,.2f,null));
        assertFalse(Zero432RearWallDeliveredProbe.eligible(
            true,12,0,.2f,new Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332(
                0,0,new float[64],new float[64])));
        assertFalse(Zero432RearWallDeliveredProbe.eligible(
            true,12,0,.2f,probe(2,.1f)));
    }
    @Test public void frontMixedAndAbsentWallsStayExcluded(){
        assertFalse(Zero432RearWallDeliveredProbe.eligible(
            true,12,1,.2f,probe(0,.2f)));
        assertFalse(Zero432RearWallDeliveredProbe.eligible(
            true,0,12,.2f,probe(0,.2f)));
        assertFalse(Zero432RearWallDeliveredProbe.eligible(
            false,12,0,.2f,probe(0,.2f)));
        assertFalse(Zero432RearWallDeliveredProbe.eligible(
            true,12,0,.01f,probe(0,.2f)));
    }
    @Test public void nonfiniteDeliveredEvidenceFailsClosed(){
        try{
            Zero432RearWallDeliveredProbe.eligible(
                true,12,0,.2f,probe(0,Float.NaN));
            fail("NaN motor evidence");
        }catch(IllegalArgumentException expected){}
    }
}
