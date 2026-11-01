package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V446 one-variable motor-map trust regression.
 * These controlled V377 transitions verify the estimator's mathematical
 * acceptance contract; only the independent ORIGINAL Android 2x10000
 * autonomous runs can establish locomotion or red avoidance.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero446HeldoutForwardAuthorityAndroidTest {
    private static final float[] EAST={1f,0f}, NORTH={0f,1f};
    private static void measured(Zero3InertialResponseV377 m,float[] direction,
                                 float forward,boolean heldout){
        float[] original194=new float[194];
        original194[193]=1f;
        m.begin(original194,direction,heldout);
        // A real displacement vector is passed through the existing V377
        // measured-transition API. This test does NOT train a new GRU.
        m.finish(direction[0]*forward,direction[1]*forward,.30f,heldout);
    }
    private static void evidence(Zero3InertialResponseV377 m,float[] dir,
                                 float trainedForward,boolean positiveHeldout){
        for(int i=0;i<8;i++)measured(m,dir,trainedForward,false);
        for(int i=0;i<8;i++){
            float observed=positiveHeldout?
                trainedForward+(i%2==0?.15f:-.15f):-trainedForward;
            measured(m,dir,observed,true);
        }
    }
    @Test public void lowCoastVarianceCannotHideIndependentlyVerifiedRealForwardControl(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        evidence(m,EAST,.13f,true);
        float[] x=new float[194];
        assertTrue("Measured 0.13 forward with heldout improvement is valid",
            m.reliable(x,EAST));
        assertNotNull("Only actual learned displacement may be predicted",
            m.expected(x,EAST));
        assertEquals(1,m.reliableBanks());
        evidence(m,NORTH,.13f,true);
        assertEquals("Two independent physical headings, no scripted direction",
            2,m.reliableSteeringDirectionsV383(x));
    }
    @Test public void predictableBrakingMustNotMasqueradeAsForwardAuthority(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        evidence(m,EAST,-.13f,true);
        assertFalse("Negative learned forward response cannot steer",
            m.reliable(new float[194],EAST));
        assertEquals(0,m.reliableBanks());
    }
    @Test public void trainingFitWithoutHeldoutImprovementCannotBeTrusted(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        evidence(m,EAST,.13f,false);
        assertFalse("Heldout disagrees with training despite positive mean",
            m.reliable(new float[194],EAST));
        assertNull(m.expected(new float[194],EAST));
    }
}
