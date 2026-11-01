package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Controlled eligibility checks only; original 2x10k determines competence. */
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
