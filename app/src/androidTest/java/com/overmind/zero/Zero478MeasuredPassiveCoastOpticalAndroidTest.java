package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V478 original 194D sensory, real-contact learned valence and PRE inertia.
 * No built-in red/blue meaning, no commanded escape, no new motor confidence. */
@RunWith(AndroidJUnit4.class)
public final class Zero478MeasuredPassiveCoastOpticalAndroidTest {
    private static final float[] EAST={1f,0f},WEST={-1f,0f};
    private static float[] seen(int band,float vx,float vy){
        float[] p=new float[194];
        p[191]=vx/130f;p[192]=vy/130f;
        p[20+band*8]=1f; // observed eye photon azimuth EAST
        return p;
    }
    private static Zero3ContactValenceV369 experienced(int band,float delta){
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.materialContact(band,delta);v.materialContact(band,delta);
        return v;
    }
    private static Zero3InstrumentalCreditV371 enabled(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        m.enableStrictInertialOpticalEvidenceLabV433();
        m.enableObservedDelayedContactWithoutMotorMapLabV434();
        m.enableAcceleratedDelayedContactEvidenceLabV435();
        m.enableCensorUnverifiedDelayedBankUnderDriftLabV442();
        m.enableMeasuredPassiveCoastOpticalLabV478();
        return m;
    }
    @Test public void realPreMomentumTowardLearnedHarmIsNotOptimisticallyZero(){
        float[] p=seen(1,1.2f,0f);
        Zero3ContactValenceV369 v=experienced(1,-7f);
        Zero3InstrumentalCreditV371 legacy=new Zero3InstrumentalCreditV371();
        legacy.enableStrictInertialOpticalEvidenceLabV433();
        legacy.enableObservedDelayedContactWithoutMotorMapLabV434();
        legacy.enableAcceleratedDelayedContactEvidenceLabV435();
        legacy.enableCensorUnverifiedDelayedBankUnderDriftLabV442();
        assertEquals(0f,legacy.score(p,EAST,v),0f);
        assertEquals(0f,legacy.opticalPreferenceV374(p,EAST,v),0f);
        Zero3InstrumentalCreditV371 m=enabled();
        float e=m.score(p,EAST,v),w=m.score(p,WEST,v);
        assertTrue("actual eastward PRE coast into learned injury is negative",e<-.0001f);
        assertEquals("unsupported west command cannot erase eastward PRE drift",e,w,0f);
        float oe=m.opticalPreferenceV374(p,EAST,v),
              ow=m.opticalPreferenceV374(p,WEST,v);
        assertTrue(oe<-.001f);
        assertEquals("optical risk is not fabricated motor control",oe,ow,0f);
        assertEquals(0,m.verifiedInertialResponseV377());
        String d=m.motorEvidenceDiagnosticsV443();
        assertTrue(d,d.contains("v478PassiveScore=2"));
        assertTrue(d,d.contains("v478PassiveOptical=2"));
    }
    @Test public void oppositeMeasuredCoastHasOppositeLearnedRisk(){
        Zero3InstrumentalCreditV371 m=enabled();
        Zero3ContactValenceV369 v=experienced(1,-7f);
        assertTrue(m.score(seen(1,-1.2f,0f),EAST,v)>.0001f);
        assertTrue(m.opticalPreferenceV374(seen(1,-1.2f,0f),WEST,v)>.001f);
        assertEquals(0,m.verifiedInertialResponseV377());
    }
    @Test public void realLearnedBenefitUsesSamePhysicsAndUnknownIsNeutral(){
        Zero3InstrumentalCreditV371 m=enabled();
        float[] p=seen(2,1.2f,0f);
        assertTrue(m.score(p,EAST,experienced(2,22f))>.0001f);
        assertTrue(m.opticalPreferenceV374(p,WEST,experienced(2,22f))>.001f);
        Zero3ContactValenceV369 unknown=new Zero3ContactValenceV369();
        assertEquals(0f,m.score(p,EAST,unknown),0f);
        assertEquals(0f,m.opticalPreferenceV374(p,WEST,unknown),0f);
    }
    @Test public void stationaryFallbackAndStrictPreconditionsRemainUnchanged(){
        Zero3InstrumentalCreditV371 m=enabled();
        float[] p=seen(1,0f,0f);
        Zero3ContactValenceV369 v=experienced(1,-7f);
        assertEquals(0f,m.score(p,EAST,v),0f);
        assertTrue("stationary visual weak hypothesis stays original",
            m.opticalPreferenceV374(p,EAST,v)<0f);
        assertTrue(m.motorEvidenceDiagnosticsV443().contains("v478PassiveScore=0"));
        try{
            new Zero3InstrumentalCreditV371().enableMeasuredPassiveCoastOpticalLabV478();
            fail("V478 must require strict no-invented-motor V433/V442");
        }catch(IllegalStateException expected){}
    }
}
