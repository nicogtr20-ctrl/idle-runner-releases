package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V433: real original V369 contact valence, V371 and V377 physical evidence contracts. */
@RunWith(AndroidJUnit4.class)
public final class Zero433InertialOpticalEvidenceAndroidTest {
    private static float[] observation(float vx,float vy){
        float[] x=new float[194];
        x[191]=vx/130f;x[192]=vy/130f;
        x[20+8]=1f; // measured wavelength band 1, world azimuth east
        return x;
    }
    private static Zero3ContactValenceV369 experienced(){
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        // Test fixture: two genuine material-contact measurements, never
        // an innate red label or any production-world reward.
        v.materialContact(1,-7f);
        v.materialContact(1,-7f);
        return v;
    }
    @Test public void unverifiedMovingHeadingIsNotMaterialTravel(){
        Zero3InstrumentalCreditV371 model=new Zero3InstrumentalCreditV371();
        float[] moving=observation(12f,0f);
        float[] east={1f,0f};
        float prior=model.opticalPreferenceV374(moving,east,experienced());
        assertTrue("legacy physically unsupported direction bias exists",prior<-.001f);
        model.enableStrictInertialOpticalEvidenceLabV433();
        assertEquals(0f,model.opticalPreferenceV374(moving,east,experienced()),0f);
        assertEquals(1L,model.unsupportedMovingOpticalPredictionsRejectedV433());
    }
    @Test public void stationaryFallbackPreservesLearnedContact(){
        Zero3InstrumentalCreditV371 model=new Zero3InstrumentalCreditV371();
        float[] stationary=observation(0f,0f);
        float[] east={1f,0f};
        float before=model.opticalPreferenceV374(stationary,east,experienced());
        model.enableStrictInertialOpticalEvidenceLabV433();
        assertEquals(before,model.opticalPreferenceV374(stationary,east,experienced()),0f);
        assertEquals(0L,model.unsupportedMovingOpticalPredictionsRejectedV433());
    }
    @Test public void noInventedContactValueOrMotorEvidence(){
        Zero3InstrumentalCreditV371 model=new Zero3InstrumentalCreditV371();
        model.enableStrictInertialOpticalEvidenceLabV433();
        Zero3ContactValenceV369 unknown=new Zero3ContactValenceV369();
        float[] moving=observation(-15f,8f);
        float[] west={-1f,0f};
        assertEquals(0f,model.opticalPreferenceV374(moving,west,unknown),0f);
        assertEquals(0,unknown.evidence(1));
        assertEquals(0,model.verifiedInertialResponseV377());
    }
}
