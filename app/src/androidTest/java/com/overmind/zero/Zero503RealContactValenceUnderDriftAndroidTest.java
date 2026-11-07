package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO503: the ACTUAL V369 learning mechanism receives two genuine net
 * contact outcomes per wavelength in this isolated optics fixture.
 * Before any experienced contact, neither wavelength has a built-in sign.
 * When drifting without a V371/V377 verified direction, original V489
 * suppresses choice-dependent optical credit. V503 can rank already
 * learned contact valence weakly, and only if activated with V499 tissue
 * authority, without inventing a motor velocity model or new actor.
 *
 * This verifies computation only; >=10000 real wild decisions are needed
 * to verify actual-world behaviour / red-contact safety.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero503RealContactValenceUnderDriftAndroidTest {
    private static Zero3InstrumentalCreditV371 instrumental(boolean v503){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        m.enableStrictInertialOpticalEvidenceLabV433();
        m.enableObservedDelayedContactWithoutMotorMapLabV434();
        m.enableAcceleratedDelayedContactEvidenceLabV435();
        m.enableCensorUnverifiedDelayedBankUnderDriftLabV442();
        m.enableMeasuredPassiveCoastOpticalLabV478();
        m.enableMatchedPreMotorLabV489();
        if(v503)m.enableMotorEnabledOpticalHypothesisLabV503();
        return m;
    }
    private static float[] vision(){
        float[] o=new float[194];
        // Body is physically moving sufficiently to trigger the original
        // V441 guard; the optical evidence must remain learned, not innate.
        o[191]=1.8f/130f;
        o[192]=.5f/130f;
        o[20]=.90f;     // wavelength 0: physically seen on EAST ray
        o[20+8+4]=.85f; // wavelength 1: physically seen on WEST ray
        return o;
    }
    private static float score(Zero3InstrumentalCreditV371 m,
            Zero3ContactValenceV369 c,float[] o,float x,float y){
        return m.opticalPreferenceV374(o,new float[]{x,y,.5f,.5f,.4f},c);
    }
    @Test public void noInnateColorSignAndNoFictitiousRiskBeforeRealContact(){
        Zero3ContactValenceV369 c=new Zero3ContactValenceV369();
        float[] obs=vision();
        Zero3InstrumentalCreditV371 v503=instrumental(true);
        assertEquals(0f,score(v503,c,obs,-1,0),0f);
        assertEquals(0f,score(v503,c,obs,1,0),0f);
        assertEquals(0,c.evidence(0));
        assertEquals(0,c.evidence(1));
        Log.i("ZERO503_OPTICAL","NO_INNATE_VALENCE_ZERO_BEFORE_EXPERIENCE_PASS");
    }
    @Test public void actualExperiencedHarmAndEnergyCanRankTwoHeadingChoicesWhileDrifting(){
        Zero3ContactValenceV369 c=new Zero3ContactValenceV369();
        // Distinct net material contacts recorded by the genuine existing
        // V374 association API. No scripted red/blue innate label.
        for(int i=0;i<2;i++){
            c.materialContact(0,-7f);
            c.materialContact(1,+7f);
        }
        assertEquals(2,c.evidence(0));
        assertEquals(2,c.evidence(1));
        float[] obs=vision();
        Zero3InstrumentalCreditV371 old=instrumental(false);
        Zero3InstrumentalCreditV371 newer=instrumental(true);
        assertTrue(c.hasVisibleLearnedHarmV422(obs));
        assertEquals(0f,score(old,c,obs,-1,0),0f);
        assertEquals(0f,score(old,c,obs,+1,0),0f);
        float west=score(newer,c,obs,-1,0);
        float east=score(newer,c,obs,+1,0);
        assertTrue("Learned west positive should rank over harmful east",
            west>0f&&east<0f&&west-east>.025f);
        assertTrue("No invented high-confidence directional oracle",
            Math.abs(west)<=.0875f&&Math.abs(east)<=.0875f);
        Log.i("ZERO503_OPTICAL",
            "EXPERIENCED_PHOTON_VALENCE_RANKING_WITHOUT_V371_DIRECTION_PASS "+
            "west="+west+" east="+east);
    }
    @Test public void aRealContactWithInsufficientIndependentEvidenceCannotInventCertainty(){
        Zero3ContactValenceV369 c=new Zero3ContactValenceV369();
        c.materialContact(0,-7f);
        float[] obs=vision();
        Zero3InstrumentalCreditV371 m=instrumental(true);
        assertEquals(0f,score(m,c,obs,1f,0f),0f);
        assertEquals(0f,score(m,c,obs,-1f,0f),0f);
        Log.i("ZERO503_OPTICAL","ONE_CONTACT_IS_NOT_DIRECTIONAL_CERTAINTY_PASS");
    }
}
