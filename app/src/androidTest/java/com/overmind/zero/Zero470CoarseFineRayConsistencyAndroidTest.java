package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** ZERO470: 8 coarse wall rays must agree with the same 16 real chamber-aware rays. */
@RunWith(AndroidJUnit4.class)
public final class Zero470CoarseFineRayConsistencyAndroidTest {
    @Test public void coarseRaysAreMaxOfSamePhysicalFinePairsWithoutTouchingOtherSenses() {
        float[] sensed=new float[194];
        for(int i=0;i<sensed.length;i++)sensed[i]=(i%17)/20f;
        for(int i=0;i<8;i++){
            sensed[8+i]=.99f; // deliberately contradictory old coarse reading
            sensed[44+2*i]=i/16f;
            sensed[45+2*i]=(8-i)/16f;
        }
        float[] before=sensed.clone();
        Zero3OriginalBrainV356.reconcileChamberAwareCoarseRaysV470(sensed);
        for(int i=0;i<8;i++)
            assertEquals(Math.max(before[44+2*i],before[45+2*i]),sensed[8+i],0f);
        for(int i=0;i<sensed.length;i++)
            if(i<8||i>=16)assertEquals("non-ray sense "+i,before[i],sensed[i],0f);
    }

    @Test public void malformedInputCannotBeSilentlyProjected() {
        try {
            Zero3OriginalBrainV356.reconcileChamberAwareCoarseRaysV470(new float[193]);
            fail("must reject wrong GRU3 sensor shape");
        } catch(IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("194"));
        }
    }
}
