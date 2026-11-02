package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V472 gate-only regression. Controlled measured-transition API fixtures
 * are NOT evidence of original autonomous gait: only the two original
 * >=10k Android physical runs can pass behavioral acceptance. */
@RunWith(AndroidJUnit4.class)
public final class Zero472V371HeldoutForwardScaleAndroidTest {
    private static final float[] EAST={1f,0f,0f,0f,.30f};
    private static final float[] NORTH={0f,1f,0f,0f,.30f};
    private static void observed(Zero3InstrumentalCreditV371 m,float[] action,
                                 float signedTravel,boolean heldout){
        float[] real194=new float[194];
        real194[193]=1f;
        m.begin(real194,action,heldout);
        m.complete(action[0]*signedTravel,action[1]*signedTravel,.30f,true,heldout);
    }
    private static void evidence(Zero3InstrumentalCreditV371 m,float[] action,
                                 float forward,boolean consistent){
        for(int i=0;i<8;i++)observed(m,action,forward,false);
        for(int i=0;i<8;i++){
            float measured=consistent?forward+(i%2==0?.015f:-.015f):-forward;
            observed(m,action,measured,true);
        }
    }
    @Test public void physicallySmallHeldoutForwardMotionIsNotRejectedByWrongSquaredUnitFloor(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        evidence(m,EAST,.13f,true);
        assertEquals("Positive heldout motion must not need squared distance >1",1,m.verifiedDirections());
        evidence(m,NORTH,.13f,true);
        assertEquals("Two independent evidence-backed bearings",2,m.verifiedDirections());
    }
    @Test public void reverseMotionIsNotMistakenForForwardAuthority(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        evidence(m,EAST,-.13f,true);
        assertEquals(0,m.verifiedDirections());
    }
    @Test public void inconsistentHeldoutMovementIsNotPromoted(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        evidence(m,EAST,.13f,false);
        assertEquals(0,m.verifiedDirections());
    }
}
