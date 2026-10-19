package com.overmind.zero;

import org.junit.Test;
import static org.junit.Assert.*;

/** V361 original 3.0 brain keeps grip owned by V319, not the motor cortex. */
public final class Zero361MotorOwnershipTest {
    @Test public void originalGripIsNeverABlindMuscleProbe() {
        Zero3LearningFoundation.AdaptiveCortex cortex=new Zero3LearningFoundation.AdaptiveCortex();
        int[] counts=new int[4];
        for(int i=0;i<120;i++) {
            cortex.begin(true,null,true);
            int channel=cortex.lastActiveProbeChannel();
            assertTrue("wrong physical motor probe "+channel,channel==0||channel==1||channel==3);
            counts[channel]++;
            cortex.discard();
        }
        assertEquals(40,counts[0]);assertEquals(40,counts[1]);
        assertEquals(0,counts[2]);assertEquals(40,counts[3]);
        assertEquals(0,cortex.updates());
    }

    @Test public void laboratoryCortexStillExploresAllFourChannels() {
        Zero3LearningFoundation.AdaptiveCortex cortex=new Zero3LearningFoundation.AdaptiveCortex();
        int[] counts=new int[4];
        for(int i=0;i<120;i++) {
            cortex.begin(true,null,false);
            counts[cortex.lastActiveProbeChannel()]++;
            cortex.discard();
        }
        for(int i=0;i<4;i++)assertEquals(30,counts[i]);
    }

    @Test public void pureVisionMappingUsesTrueFrequencyThenAzimuth() {
        float[] x=new float[194];
        x[20+0*8+0]=.8f;x[20+1*8+6]=.4f;x[20+2*8+3]=.2f;
        float[] c=Zero3VisionContextV361.fromSensory194(x);
        assertEquals(.1f,c[0],1e-6f);
        assertEquals(.05f,c[1],1e-6f);
        assertEquals(.025f,c[2],1e-6f);
        assertEquals((.8f-.4f+.2f)/24f,c[3],1e-6f);
    }
}
