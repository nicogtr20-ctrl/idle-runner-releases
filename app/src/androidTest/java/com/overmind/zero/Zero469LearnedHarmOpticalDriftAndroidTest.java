package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V469 one-variable original V371/V369 safety-gate ablation; not release proof.
 * The separate original 2x10000 Android runs decide whether the hypothesis is safe.
 * The default strict V433 behavior is deliberately unchanged without opt-in.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero469LearnedHarmOpticalDriftAndroidTest {
    private static float[] sight(float speed){
        float[] x=new float[194];
        x[191]=speed/130f;
        x[20+8]=1f; // actual sensed band-1 east photon, NOT a red class
        return x;
    }
    private static float[] east(){return new float[]{1f,0f};}
    private static float[] west(){return new float[]{-1f,0f};}
    @Test public void strictDefaultRemainsZeroWithoutLabOptIn(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        m.enableStrictInertialOpticalEvidenceLabV433();
        Zero3ContactValenceV369 c=new Zero3ContactValenceV369();
        c.materialContact(1,-7f);c.materialContact(1,-7f);
        assertEquals(0f,m.opticalPreferenceV374(sight(12f),east(),c),0f);
        assertEquals(0,m.verifiedInertialResponseV377());
    }
    @Test public void onlyRealLearnedVisibleHarmRestoresWeakHypothesis(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        m.enableStrictInertialOpticalEvidenceLabV433();
        m.enableLearnedHarmDriftOpticalAblationLabV469();
        Zero3ContactValenceV369 c=new Zero3ContactValenceV369();
        assertEquals(0f,m.opticalPreferenceV374(sight(12f),east(),c),0f);
        c.materialContact(1,-7f);
        assertEquals(0f,m.opticalPreferenceV374(sight(12f),east(),c),0f);
        c.materialContact(1,-7f);
        float toward=m.opticalPreferenceV374(sight(12f),east(),c);
        float away=m.opticalPreferenceV374(sight(12f),west(),c);
        assertTrue("real harm must disfavor actor-proposed east",toward<-.001f);
        assertTrue("opposite actor sample must compare with real photons",away>.001f);
        assertEquals(0,m.verifiedInertialResponseV377());
    }
    @Test public void positiveOnlyAndInvisibleHarmCannotOpenGate(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        m.enableStrictInertialOpticalEvidenceLabV433();
        m.enableLearnedHarmDriftOpticalAblationLabV469();
        Zero3ContactValenceV369 c=new Zero3ContactValenceV369();
        c.materialContact(1,+22f);c.materialContact(1,+22f);
        assertEquals(0f,m.opticalPreferenceV374(sight(12f),east(),c),0f);
        Zero3ContactValenceV369 hurt=new Zero3ContactValenceV369();
        hurt.materialContact(1,-7f);hurt.materialContact(1,-7f);
        assertEquals(0f,m.opticalPreferenceV374(new float[194],east(),hurt),0f);
    }
    @Test public void strictV433RequiredBeforeAblation(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        try{m.enableLearnedHarmDriftOpticalAblationLabV469();
            fail("strict original V433 required");
        }catch(IllegalStateException expected){}
    }
}
