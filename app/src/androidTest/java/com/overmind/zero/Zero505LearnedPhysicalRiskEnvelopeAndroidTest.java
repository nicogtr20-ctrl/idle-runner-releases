package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V505: contact-inferred harm only, no innate wavelength values.
 * V500 10k true Android failures showed selected trained actor headings
 * facing learned harm while the V489 motor-forecast score overwhelmed
 * an actually available safer candidate. Test the same original V353
 * instrumental score with no motor evidence and a real two-contact history.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero505LearnedPhysicalRiskEnvelopeAndroidTest {
    private static Zero3ContactValenceV369 learned(int band,float delta){
        Zero3ContactValenceV369 c=new Zero3ContactValenceV369();
        c.materialContact(band,delta);
        c.materialContact(band,delta);
        return c;
    }
    @Test
    public void riskComesOnlyFromExperiencedHarmNotFromWavelengthOrInbornRules(){
        float[] seen=new float[194];
        seen[20+1*8+0]=1f; // current actually observed spectral ray east
        Zero3ContactValenceV369 virgin=new Zero3ContactValenceV369();
        assertEquals(0f,Zero3InstrumentalCreditV371.
            learnedMaterialHarmExposureV505(seen,1f,0f,virgin),0f);
        Zero3ContactValenceV369 one=new Zero3ContactValenceV369();
        one.materialContact(1,-7f);
        assertEquals(0f,Zero3InstrumentalCreditV371.
            learnedMaterialHarmExposureV505(seen,1f,0f,one),0f);
        one.materialContact(1,-7f);
        assertEquals(1f,Zero3InstrumentalCreditV371.
            learnedMaterialHarmExposureV505(seen,1f,0f,one),.0001f);
        assertEquals(0f,Zero3InstrumentalCreditV371.
            learnedMaterialHarmExposureV505(seen,-1f,0f,one),.0001f);
        assertEquals(0f,Zero3InstrumentalCreditV371.
            learnedMaterialHarmExposureV505(seen,0f,1f,one),.0001f);
        Zero3ContactValenceV369 benefit=learned(1,22f);
        assertEquals(0f,Zero3InstrumentalCreditV371.
            learnedMaterialHarmExposureV505(seen,1f,0f,benefit),.0001f);
        // Same physical harmful outcome from a DIFFERENT wavelength.
        seen[20+1*8+0]=0f;seen[20+0*8+0]=1f;
        Zero3ContactValenceV369 other=learned(0,-7f);
        assertEquals(1f,Zero3InstrumentalCreditV371.
            learnedMaterialHarmExposureV505(seen,1f,0f,other),.0001f);
    }

    @Test
    public void oldUntrustedInertialMotorEarlyReturnDoesNotEraseLearnedRisk(){
        float[] photons=new float[194];
        photons[20+1*8+0]=.9f;
        Zero3ContactValenceV369 realHarm=learned(1,-7f);
        float[] intoHarm={1f,0f,.5f,.5f,.4f};
        float[] awayFromHarm={-1f,0f,.5f,.5f,.4f};
        Zero3InstrumentalCreditV371 original=new Zero3InstrumentalCreditV371();
        float oldUnsafe=original.score(photons,intoHarm,realHarm);
        float oldSafe=original.score(photons,awayFromHarm,realHarm);
        assertEquals(0f,oldUnsafe,0f);
        assertEquals(0f,oldSafe,0f);
        Zero3InstrumentalCreditV371 improved=new Zero3InstrumentalCreditV371();
        improved.enablePhysicalLearnedHarmEnvelopeLabV505();
        float penalty=improved.score(photons,intoHarm,realHarm);
        float harmless=improved.score(photons,awayFromHarm,realHarm);
        assertTrue("Experienced harm must alter original action ranking",
            penalty<-.2f);
        assertEquals("No punitive score for moving away from known hurt",
            0f,harmless,1e-5f);
        assertTrue(improved.motorEvidenceDiagnosticsV443().contains(
            "v505HarmEnvelope=true"));
        Log.i("ZERO505_RISK","MEASURED_CONTACT_ONLY_OPTICAL_RISK_GATE=PASS");
    }
}
