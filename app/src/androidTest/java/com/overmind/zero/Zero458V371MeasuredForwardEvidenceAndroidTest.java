package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Controlled eligibility checks only; V459 additionally rejects passive PRE coast.\n * Genuine original S21 and wild 2x10k plus regression/CRC gates decide competence. */
@RunWith(AndroidJUnit4.class)
public final class Zero458V371MeasuredForwardEvidenceAndroidTest {
    private static final float[] EAST={1f,0f,0f,0f,0f};
    private static void sample(Zero3InstrumentalCreditV371 m,float dx,boolean heldout){
        float[] pre=new float[194];pre[193]=1f;
        m.begin(pre,EAST,heldout);
        m.complete(dx,0f,.30f,true,heldout);
    }
    @Test public void independentlyValidatedSubunitForwardMotionCanPass(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        for(int i=0;i<8;i++)sample(m,.42f,false);
        assertFalse(m.motorVerified(0));
        for(int i=0;i<8;i++)sample(m,i%2==0?.44f:.40f,true);
        assertTrue(m.motorVerified(0));
        assertEquals(1,m.verifiedDirections());
    }
    @Test public void passiveCoastMustNotBecomeCertifiedMotorAuthority(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        for(int i=0;i<16;i++){
            float[] pre=new float[194];
            pre[193]=1f;
            // 2 units/s PRE east, .30 seconds, .60 measured east:
            // all of the observed displacement is passive PRE momentum.
            pre[191]=2f/130f;
            boolean heldout=i>=8;
            m.begin(pre,EAST,heldout);
            m.complete(.60f,0f,.30f,true,heldout);
        }
        assertFalse(m.motorVerified(0));
        assertEquals(0,m.verifiedDirections());
    }
    @Test public void lowForwardMotionIsNotMotorAuthority(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        for(int i=0;i<8;i++)sample(m,.12f,false);
        for(int i=0;i<8;i++)sample(m,.12f,true);
        assertFalse(m.motorVerified(0));
    }
    @Test public void independentHeldoutContradictionMustReject(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        for(int i=0;i<8;i++)sample(m,.42f,false);
        for(int i=0;i<8;i++)sample(m,-.42f,true);
        assertFalse(m.motorVerified(0));
    }
}
