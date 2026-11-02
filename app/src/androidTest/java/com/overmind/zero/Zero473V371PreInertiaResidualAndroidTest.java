package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V473 pure attribution checks. These fixtures are not autonomous gameplay
 * and cannot replace either original >=10000-decision Android acceptance test.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero473V371PreInertiaResidualAndroidTest {
    private static final float[] EAST={1f,0f,0f,0f,.30f};

    private static void measured(Zero3InstrumentalCreditV371 m,
                                 float preVx,float actualDx,boolean heldout) {
        float[] original194=new float[194];
        original194[191]=preVx/130f;
        original194[193]=1f;
        m.begin(original194,EAST,heldout);
        m.complete(actualDx,0f,.30f,true,heldout);
    }
    private static void evidence(Zero3InstrumentalCreditV371 m,
                                 float preVx,float activeDx) {
        for(int i=0;i<8;i++)measured(m,preVx,preVx*.30f+activeDx,false);
        for(int i=0;i<8;i++)measured(m,preVx,
            preVx*.30f+activeDx+(i%2==0?.005f:-.005f),true);
    }

    @Test public void repeatablePassiveCoastCannotCertifySteering() {
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        evidence(m,1.2f,0f);
        assertEquals("Repeatable body drift is not muscle-driven authority",
                     0,m.verifiedDirections());
    }
    @Test public void genuineForwardResidualCanCertifyHeldoutSteering() {
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        evidence(m,1.2f,.18f);
        assertEquals(1,m.verifiedDirections());
    }
    @Test public void oppositeResidualDoesNotCertifyForwardSteering() {
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        evidence(m,1.2f,-.18f);
        assertEquals(0,m.verifiedDirections());
    }
}
