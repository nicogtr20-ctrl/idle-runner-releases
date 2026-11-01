package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO459 evidence gate tests. These controlled samples test bookkeeping,
 * NOT autonomous organism competence; original S21 + Wild >=10000 remain
 * mandatory, using authentic full 170-material original game physics.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero459LowPreMomentumPhysicalEvidenceAndroidTest {
    private static final float[] EAST={1f,0f,.5f,.5f,.30f};
    private static Zero3InstrumentalCreditV371 lab(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        m.enableStrictInertialOpticalEvidenceLabV433();
        m.enableLowPreMomentumEvidenceLabV459();
        return m;
    }
    private static void sample(Zero3InstrumentalCreditV371 m,
            float physicalPreVx,float genuineDx,boolean heldout){
        float[] pre=new float[194];
        pre[191]=physicalPreVx/130f;
        pre[188]=0f;pre[193]=1f;
        m.begin(pre,EAST,heldout);
        m.complete(genuineDx,0f,.30f,true,heldout,false);
    }
    @Test public void physicallyMeasuredLowSpeedTrainAndIndependentHeldoutCanValidate(){
        Zero3InstrumentalCreditV371 m=lab();
        for(int i=0;i<8;i++)sample(m,.10f,.45f,false);
        assertFalse("true trained values alone must never validate",m.motorVerified(0));
        for(int i=0;i<8;i++)sample(m,.10f,i%2==0?.47f:.43f,true);
        assertTrue("learned real LOW-PRE residual and heldout confirmed",m.motorVerified(0));
        assertEquals(1,m.verifiedDirections());
        assertTrue(m.lowEvidenceDiagnosticsV459().contains("lowHeldout=8"));
    }
    @Test public void manyHighMomentumCoastsNeverCertifyLowSpeedMuscles(){
        Zero3InstrumentalCreditV371 m=lab();
        for(int i=0;i<64;i++)sample(m,12f,3.6f,false);
        for(int i=0;i<64;i++)sample(m,12f,3.6f,true);
        // V458 unconditioned measurements include 128 real but coasting
        // observations; V459 must distinguish them from motor output.
        assertFalse(m.motorVerified(0));
        assertTrue(m.lowEvidenceDiagnosticsV459().contains("highSkipped=128"));
        assertEquals(0,m.verifiedDirections());
    }
    @Test public void lowSpeedPassiveCoastAndContradictedHeldoutMustFail(){
        Zero3InstrumentalCreditV371 coast=lab();
        for(int i=0;i<8;i++)sample(coast,.20f,.06f,false);
        for(int i=0;i<8;i++)sample(coast,.20f,.06f,true);
        assertFalse("true observed motion is not necessarily active propulsion",
            coast.motorVerified(0));
        Zero3InstrumentalCreditV371 contradicted=lab();
        for(int i=0;i<8;i++)sample(contradicted,0f,.45f,false);
        for(int i=0;i<8;i++)sample(contradicted,0f,-.45f,true);
        assertFalse("independent adverse physical response cannot be accepted",
            contradicted.motorVerified(0));
    }
    @Test public void optInMustNotModifyOldPolicyOrExistingV371Data(){
        Zero3InstrumentalCreditV371 old=new Zero3InstrumentalCreditV371();
        for(int i=0;i<8;i++)sample(old,20f,6.4f,false);
        for(int i=0;i<8;i++)sample(old,20f,i%2==0?6.5f:6.3f,true);
        assertTrue("legacy V458 bank remains as it was for comparison",
            old.motorVerified(0));
        old.enableStrictInertialOpticalEvidenceLabV433();
        old.enableLowPreMomentumEvidenceLabV459();
        assertFalse("high drift inherited PRE motion must not qualify as low",
            old.motorVerified(0));
        assertTrue(old.lowEvidenceDiagnosticsV459().contains("lowTrain=0"));
    }
}
