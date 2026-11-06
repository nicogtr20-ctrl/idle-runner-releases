package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.lang.reflect.Field;
import java.util.Arrays;
import static org.junit.Assert.*;

/** ZERO490: direct V332 learn() path, not only learnV332(probe,...).
 * Tests real 64-channel cortex transfer and actual post-clip intervention.
 * Lab-only regression; no substitute actuator or goal controller.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero490DeliveredLearnPathAndroidTest {
    private static float[] mutableWeights(Zero3LearningFoundation.AdaptiveCortex c)
            throws Exception {
        Field f=c.getClass().getDeclaredField("weights");
        f.setAccessible(true);
        return (float[])f.get(c);
    }
    private static void decision(Zero3LearningFoundation.AdaptiveCortex c,
                                 float effort) {
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        Zero3LearningFoundation.MotorIntent intent=
            new Zero3LearningFoundation.MotorIntent(1,0,c.revision(),
                1f,0f,effort,.5f,.30f);
        c.begin(true,intent,true);
        assertEquals(1,c.activeProbeCountV390());
        for(int i=0;i<6;i++) c.command(intent,body,skin,1f/60f);
    }
    @Test public void v421SaturatedDeliveredZeroMustNotUpdateDirectLearn()
            throws Exception {
        Zero3LearningFoundation.AdaptiveCortex c=
            new Zero3LearningFoundation.AdaptiveCortex();
        c.enableIsolatedCreditV390();
        c.enableDeliveredLocalCreditLabV421();
        float[] w=mutableWeights(c);
        for(int out=0;out<64;out++) w[out*8]=1.5f;
        decision(c,1f);
        Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 p=c.captureProbeV332();
        int active=-1;
        for(int i=0;i<64;i++) if(p.noise[i]!=0f) active=i;
        assertTrue(active>=0);
        assertNotNull(p.deliveredNoise);
        assertEquals(0f,p.deliveredNoise[active],0f);
        float[] before=c.weights();
        c.learn(1f,true);
        assertArrayEquals("No physical motor change means no V332 weight credit",
            before,c.weights(),0f);
        assertEquals(0L,c.updates());
    }
    @Test public void v421UnsaturatedDirectLearnRetainsRealCredit()
            throws Exception {
        Zero3LearningFoundation.AdaptiveCortex c=
            new Zero3LearningFoundation.AdaptiveCortex();
        c.enableIsolatedCreditV390();
        c.enableDeliveredLocalCreditLabV421();
        decision(c,.5f);
        Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 p=c.captureProbeV332();
        int active=-1;
        for(int i=0;i<64;i++) if(p.noise[i]!=0f) active=i;
        assertTrue(active>=0);
        assertTrue(Math.abs(p.deliveredNoise[active])>1e-6f);
        float[] before=c.weights();
        c.learn(1f,true);
        assertFalse("Nonclipped delivered probe must remain trainable",
            Arrays.equals(before,c.weights()));
        assertEquals(1L,c.updates());
    }
}
