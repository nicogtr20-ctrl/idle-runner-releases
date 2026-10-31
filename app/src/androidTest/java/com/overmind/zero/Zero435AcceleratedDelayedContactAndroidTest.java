package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import java.util.Arrays;

/** V435 pure causal eligibility invariants. 2x10k real Android trials are separate. */
@RunWith(AndroidJUnit4.class)
public final class Zero435AcceleratedDelayedContactAndroidTest {
    private static float[] observation(int band,float preVx){
        float[] x=new float[194];
        x[191]=preVx/130f;
        x[20+band*8]=1f;
        return x;
    }
    private static float[] east(){return new float[]{1f,0f,.5f,.5f,.3f};}
    private static void enable(Zero3InstrumentalCreditV371 m,boolean v435){
        m.enableStrictInertialOpticalEvidenceLabV433();
        m.enableObservedDelayedContactWithoutMotorMapLabV434();
        if(v435)m.enableAcceleratedDelayedContactEvidenceLabV435();
    }
    private static void twoContacts(Zero3InstrumentalCreditV371 m,
                                    Zero3ContactValenceV369 valence,
                                    int band,float delta,float preSpeed,float actualDx){
        for(int k=0;k<2;k++){
            float[] s=observation(band,preSpeed);
            m.begin(s,east(),false);
            m.complete(actualDx,0f,.30f,true,false);
            m.begin(s,east(),false);
            m.materialContactV374(Arrays.copyOfRange(s,20,44),band,delta,false);
            valence.materialContact(band,delta);
            m.complete(0f,0f,.30f,false,false);
        }
    }
    @Test public void cannotEnableWithoutV434AndWithoutPendingDecision(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        try{m.enableAcceleratedDelayedContactEvidenceLabV435();fail("requires V434");}
        catch(IllegalStateException expected){}
        enable(m,false);
        float[] s=observation(1,0f);
        m.begin(s,east(),false);
        try{m.enableAcceleratedDelayedContactEvidenceLabV435();fail("pending");}
        catch(IllegalStateException expected){}
        m.abort();
        m.enableAcceleratedDelayedContactEvidenceLabV435();
    }
    @Test public void passiveCoastingCannotBecomeHistoricalHazardBank(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        enable(m,true);
        // 20 units/s PRE * 0.30 s = 6 units actual: no active acceleration.
        twoContacts(m,v,1,-7f,20f,6f);
        assertEquals(0,m.delayedCount(1,0));
        assertEquals(2L,m.passiveDelayedApproachesRejectedV435());
        assertEquals(0f,m.score(observation(1,12f),east(),v),0f);
    }
    @Test public void measuredForwardAccelerationStillLinksTrueHarm(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        enable(m,true);
        // 9 measured units vs 6 passive baseline, 3 active units forward.
        twoContacts(m,v,1,-7f,20f,9f);
        assertTrue(m.delayedCount(1,0)>=2);
        assertEquals(0L,m.passiveDelayedApproachesRejectedV435());
        assertTrue(m.score(observation(1,12f),east(),v)<-.001f);
        assertEquals(0,m.verifiedInertialResponseV377());
    }
    @Test public void v434ComparatorStillAcceptsPassiveCleanTravel(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        enable(m,false);
        twoContacts(m,v,1,-7f,20f,6f);
        assertTrue(m.delayedCount(1,0)>=2);
        assertTrue(m.score(observation(1,12f),east(),v)<-.001f);
    }
    @Test public void realPositiveContactHasSameCausalEligibility(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        enable(m,true);
        twoContacts(m,v,2,22f,20f,9f);
        assertTrue(m.score(observation(2,12f),east(),v)>.001f);
        assertEquals(0f,m.score(observation(1,12f),east(),v),0f);
    }
}
