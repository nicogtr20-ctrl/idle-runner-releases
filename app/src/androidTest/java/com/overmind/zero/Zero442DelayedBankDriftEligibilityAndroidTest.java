package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Arrays;
import static org.junit.Assert.*;

/** V442 physical eligibility, no new heading, reward, or checkpoint. */
@RunWith(AndroidJUnit4.class)
public final class Zero442DelayedBankDriftEligibilityAndroidTest {
    private static float[] obs(int band,float vx){
        float[] x=new float[194];x[191]=vx/130f;x[20+band*8]=1f;return x;
    }
    private static float[] east(){return new float[]{1f,0f,.5f,.5f,.3f};}
    private static void enable(Zero3InstrumentalCreditV371 m){
        m.enableStrictInertialOpticalEvidenceLabV433();
        m.enableObservedDelayedContactWithoutMotorMapLabV434();
        m.enableAcceleratedDelayedContactEvidenceLabV435();
    }
    private static void twoLinks(Zero3InstrumentalCreditV371 m,
                                 Zero3ContactValenceV369 v,int band,float delta){
        for(int k=0;k<2;k++){
            float[] x=obs(band,20f);
            m.begin(x,east(),false);
            m.complete(9f,0f,.30f,true,false); // 9 actual vs 6 passive
            m.begin(x,east(),false);
            m.materialContactV374(Arrays.copyOfRange(x,20,44),band,delta,false);
            v.materialContact(band,delta);
            m.complete(0f,0f,.30f,false,false);
        }
    }
    @Test public void requiresV433V434V435AndCompletedDecision(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        try{m.enableCensorUnverifiedDelayedBankUnderDriftLabV442();fail("requires prerequisites");}
        catch(IllegalStateException expected){}
        enable(m);m.begin(obs(1,0f),east(),false);
        try{m.enableCensorUnverifiedDelayedBankUnderDriftLabV442();fail("pending");}
        catch(IllegalStateException expected){}
        m.abort();m.enableCensorUnverifiedDelayedBankUnderDriftLabV442();
    }
    @Test public void driftingNegativeHistoricalBankIsNotNewActuatorEvidence(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        enable(m);twoLinks(m,v,1,-7f);
        assertTrue(m.delayedCount(1,0)>=2);
        assertEquals(0,m.verifiedInertialResponseV377());
        assertTrue(m.score(obs(1,1.2f),east(),v)<-.001f);
        m.enableCensorUnverifiedDelayedBankUnderDriftLabV442();
        assertEquals(0f,m.score(obs(1,1.2f),east(),v),0f);
        assertTrue(v.evidence(1)>=2);
        assertTrue(m.delayedCount(1,0)>=2);
    }
    @Test public void slowRealContactBankStillRanksBothSigns(){
        for(int b=1;b<=2;b++){
            Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
            Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
            enable(m);twoLinks(m,v,b,b==1?-7f:22f);
            float before=m.score(obs(b,.2f),east(),v);
            assertTrue(Math.abs(before)>.001f);
            m.enableCensorUnverifiedDelayedBankUnderDriftLabV442();
            assertEquals(before,m.score(obs(b,.2f),east(),v),0f);
        }
    }
    @Test public void driftingPositiveBankAlsoCensoredWithoutErasingGain(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        enable(m);twoLinks(m,v,2,22f);
        assertTrue(m.score(obs(2,1.2f),east(),v)>.001f);
        m.enableCensorUnverifiedDelayedBankUnderDriftLabV442();
        assertEquals(0f,m.score(obs(2,1.2f),east(),v),0f);
        assertTrue(v.evidence(2)>=2);
        assertEquals(0f,m.score(obs(1,1.2f),east(),v),0f);
    }
}
