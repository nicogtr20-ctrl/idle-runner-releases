package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.lang.reflect.Field;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * ZERO421 exact ORIGINAL 64-muscle cortex, physical original body/skin,
 * V390 single-site experimentation and V332 gradient. A saturated muscle
 * delivers ZERO change in physical output and must receive ZERO credit.
 * Neither a mocked action, artificial body solver nor goal command.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero421OriginalDeliveredLocalProbeAndroidTest {
    private static Zero3LearningFoundation.MotorIntent intent(
            Zero3LearningFoundation.AdaptiveCortex cortex,float effort){
        return new Zero3LearningFoundation.MotorIntent(1,0,cortex.revision(),
            1f,0f,effort,.5f,.30f);
    }
    private static float[] weights(Zero3LearningFoundation.AdaptiveCortex cortex)
        throws Exception{
        Field f=cortex.getClass().getDeclaredField("weights");
        f.setAccessible(true);
        return (float[])f.get(cortex);
    }
    private static Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 execute(
            Zero3LearningFoundation.AdaptiveCortex cortex,float effort){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        Zero3LearningFoundation.MotorIntent requested=intent(cortex,effort);
        cortex.begin(true,requested,true);
        assertEquals(1,cortex.activeProbeCountV390());
        for(int i=0;i<6;i++)cortex.command(requested,body,skin,1f/60f);
        Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe=
            cortex.captureProbeV332();
        cortex.discard();
        return probe;
    }
    @Test public void saturatedOriginalMuscleHasExactlyZeroV332SynapticCredit()
        throws Exception{
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        cortex.enableIsolatedCreditV390();
        cortex.enableDeliveredLocalCreditLabV421();
        float[] initial=weights(cortex);
        // With actual gain=2 at full voluntary effort, every motor command
        // is at true +1 output clip; a small exploratory intervention cannot
        // alter the force actually fed to SymbioteMatterV1.step.
        for(int out=0;out<64;out++)initial[out*8]=1.5f;
        Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe=
            execute(cortex,1f);
        assertNotNull("V421 must preserve true measured actuator eligibility",
            probe.deliveredNoise);
        int active=-1;for(int i=0;i<64;i++)if(probe.noise[i]!=0f)active=i;
        assertTrue(active>=0);
        assertEquals("raw V332 noise is NOT necessarily executed",0f,
            probe.deliveredNoise[active],0f);
        float[] before=cortex.weights();
        float[] observed=new float[16];Arrays.fill(observed,1f);
        cortex.learnV332(probe,observed,new float[16]);
        assertArrayEquals("Saturated physical muscles must not change a weight",
            before,cortex.weights(),0f);
        assertEquals(0L,cortex.updates());
    }

    @Test public void nonSaturatedOriginalMuscleStillTrainsMeasuredCausalProbe()
        throws Exception{
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        cortex.enableIsolatedCreditV390();
        cortex.enableDeliveredLocalCreditLabV421();
        Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe=
            execute(cortex,.5f);
        int active=-1;for(int i=0;i<64;i++)if(probe.noise[i]!=0f)active=i;
        assertTrue(active>=0);
        assertNotNull(probe.deliveredNoise);
        assertTrue("Unsaturated real muscle must receive actual intervention",
            Math.abs(probe.deliveredNoise[active])>1e-6f);
        assertEquals(Math.signum(probe.noise[active]),
                     Math.signum(probe.deliveredNoise[active]),0f);
        float[] before=cortex.weights();
        float[] measured=new float[16];Arrays.fill(measured,.8f);
        cortex.learnV332(probe,measured,new float[16]);
        assertEquals(1L,cortex.updates());
        assertFalse(Arrays.equals(before,cortex.weights()));
    }

    @Test public void originalUnmodifiedModeRemainsLegacyV417Eligibility()
        throws Exception{
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        cortex.enableIsolatedCreditV390();
        Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe=
            execute(cortex,.5f);
        assertNull("Original saved cortex legacy mode must remain unchanged",
            probe.deliveredNoise);
        assertEquals(0L,cortex.updates());
    }
}
