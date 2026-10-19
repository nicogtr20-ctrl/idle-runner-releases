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

    @Test public void previousV354ExecutiveOptionSurvivesVersionedVisionUpgrade() throws Exception {
        Zero3ExecutiveV354 learned=new Zero3ExecutiveV354();
        float[] sensor=new float[194];
        float[] action={1f,0f,.5f,.5f,.3f};
        // Real lifecycle API: 32 physical-evidence TRAIN observations create
        // one candidate policy; no hard-coded reward enters the app itself.
        for(int i=0;i<32;i++) {
            learned.propose(sensor,action,i,false);
            learned.complete(.15f,-.01f,0f,.01f,false,false,null,true);
        }
        assertEquals(1,learned.snapshot().skills.length);
        byte[] modern=learned.encode();
        // Reconstruct the wire layout of the immediately preceding V354
        // version: without the ONE per-option provenance flag.
        byte[] old=new byte[modern.length-1];
        System.arraycopy(modern,0,old,0,modern.length-5);
        System.arraycopy(modern,modern.length-4,old,old.length-4,4);
        java.nio.ByteBuffer.wrap(old).putInt(0,354);
        Zero3ExecutiveV354 migrated=Zero3ExecutiveV354.decode(old);
        assertEquals(32,migrated.snapshot().decisions);
        assertEquals(1,migrated.snapshot().skills.length);
        assertEquals(learned.snapshot().skills[0].id,migrated.snapshot().skills[0].id);
        Zero3ExecutiveV354 reread=Zero3ExecutiveV354.decode(migrated.encode());
        assertEquals(1,reread.snapshot().skills.length);
        assertEquals(32,reread.snapshot().decisions);
    }
}
