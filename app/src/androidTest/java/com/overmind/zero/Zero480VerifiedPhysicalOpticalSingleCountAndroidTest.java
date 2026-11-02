package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V480 causal accounting regression only; full original 2x10k decides behavior.
 * A heldout-verified material travel and the same learned retina are one
 * physical observation, not two independent optical consequences. */
@RunWith(AndroidJUnit4.class)
public final class Zero480VerifiedPhysicalOpticalSingleCountAndroidTest {
    private static final float[] EAST={1f,0f,.4f,0f,.3f};
    private static float[] sight(){
        float[] p=new float[194];
        p[20+8]=1f; // observed wavelength band 1, retinal east ray
        return p;
    }
    private static Zero3ContactValenceV369 contact(float delta){
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.materialContact(1,delta);v.materialContact(1,delta);
        return v;
    }
    private static Zero3InstrumentalCreditV371 trained(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        float[] p=sight();
        for(int k=0;k<7;k++){
            boolean heldout=k>=4;
            m.begin(p,EAST,heldout);
            m.complete(.30f,0f,.30f,true,heldout);
        }
        assertTrue("real-material V377 TRAIN+heldout evidence",m.verifiedInertialResponseV377()>0);
        return m;
    }
    @Test public void verifiedTravelIntoExperiencedHarmCountsOnce(){
        Zero3InstrumentalCreditV371 m=trained();
        float[] p=sight();
        Zero3ContactValenceV369 harm=contact(-7f);
        assertTrue("physical predicted travel into learned harm",m.score(p,EAST,harm)<0f);
        assertEquals("V480 removes duplicate normalized V377 optical term",
            0f,m.opticalPreferenceV374(p,EAST,harm),0f);
    }
    @Test public void verifiedTravelTowardExperiencedBenefitCountsOnce(){
        Zero3InstrumentalCreditV371 m=trained();
        float[] p=sight();
        assertTrue(m.score(p,EAST,contact(22f))>0f);
        assertEquals(0f,m.opticalPreferenceV374(p,EAST,contact(22f)),0f);
    }
    @Test public void unverifiedStationaryHypothesisIsNotRemoved(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        float[] p=sight();
        Zero3ContactValenceV369 harm=contact(-7f);
        assertEquals(0f,m.score(p,EAST,harm),0f);
        assertTrue("stationary unverified optical hypothesis remains",
            m.opticalPreferenceV374(p,EAST,harm)<0f);
    }
}
