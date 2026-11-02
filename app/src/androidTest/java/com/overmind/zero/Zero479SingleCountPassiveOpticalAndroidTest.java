package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V479 one measured PRE drift, one learned contact consequence.
 * No imagined steering, red labels, synthetic reward or saved-state edits. */
@RunWith(AndroidJUnit4.class)
public final class Zero479SingleCountPassiveOpticalAndroidTest {
    private static final float[] EAST={1f,0f},WEST={-1f,0f};
    private static Zero3InstrumentalCreditV371 enabled(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        m.enableStrictInertialOpticalEvidenceLabV433();
        m.enableObservedDelayedContactWithoutMotorMapLabV434();
        m.enableAcceleratedDelayedContactEvidenceLabV435();
        m.enableCensorUnverifiedDelayedBankUnderDriftLabV442();
        m.enableMeasuredPassiveCoastOpticalLabV478();
        return m;
    }
    private static float[] sensed(float vx){
        float[] p=new float[194];
        p[191]=vx/130f;
        p[20+8]=1f; // observed band-1 photons, east retinal ray
        return p;
    }
    private static Zero3ContactValenceV369 learned(float delta){
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.materialContact(1,delta);
        v.materialContact(1,delta);
        return v;
    }
    @Test public void harmCoastHasOneMeasuredTermNotTwo(){
        Zero3InstrumentalCreditV371 m=enabled();
        float[] p=sensed(1.2f);
        Zero3ContactValenceV369 v=learned(-7f);
        float east=m.score(p,EAST,v),west=m.score(p,WEST,v);
        assertTrue("genuine measured coast into learned injury",east<0f);
        assertEquals("no unmeasured steering attributed to west",east,west,0f);
        assertEquals("no second copy of same passive injury",0f,
            m.opticalPreferenceV374(p,EAST,v),0f);
        assertEquals(0f,m.opticalPreferenceV374(p,WEST,v),0f);
        assertEquals(0,m.verifiedInertialResponseV377());
    }
    @Test public void reversedCoastAndPositiveContactRemainPhysical(){
        Zero3InstrumentalCreditV371 m=enabled();
        assertTrue(m.score(sensed(-1.2f),EAST,learned(-7f))>0f);
        assertTrue(m.score(sensed(1.2f),WEST,learned(7f))>0f);
        assertEquals(0f,m.opticalPreferenceV374(sensed(-1.2f),WEST,learned(-7f)),0f);
    }
    @Test public void noDriftKeepsExistingUnverifiedOpticalHypothesis(){
        Zero3InstrumentalCreditV371 m=enabled();
        assertTrue(m.opticalPreferenceV374(sensed(0f),EAST,learned(-7f))<0f);
        assertEquals(0f,m.score(sensed(0f),EAST,learned(-7f)),0f);
    }
}
