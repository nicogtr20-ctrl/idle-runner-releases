package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Read-only V475 forensic counter regression; never asserts a motor cure. */
@RunWith(AndroidJUnit4.class)
public final class Zero475V474FallbackReachabilityAndroidTest {
    @Test public void unsupportedMeasuredDriftStillCensorsOpticalAndRecordsWhy(){
        Zero3InstrumentalCreditV371 memory=new Zero3InstrumentalCreditV371();
        memory.enableStrictInertialOpticalEvidenceLabV433();
        float[] pre=new float[194];
        pre[191]=.5f/130f; // actual measured PRE drift > V441's 0.35 U/s
        pre[193]=1f;
        Zero3ContactValenceV369 learned=new Zero3ContactValenceV369();
        float[] chosen={1f,0f};
        assertEquals(0f,memory.opticalPreferenceV374(pre,chosen,learned),0f);
        String diag=memory.motorEvidenceDiagnosticsV443();
        assertTrue(diag,diag.contains("v475OpticalDriftBlocked=1"));
        assertTrue(diag,diag.contains("v475OpticalReconstructed=0"));
    }
}
