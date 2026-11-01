package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Research regressions for the actual V390 original cortex phase source and
 * material-frame contrast. No synthetic test substitutes for wild/S21 10k. */
@RunWith(AndroidJUnit4.class)
public final class Zero454PairedPhysicalMotorContrastAndroidTest {
    @Test public void truePhysicalABBARejectsConstantAndLinearPassiveDrift(){
        Zero454PairedMotorContrast c=new Zero454PairedMotorContrast();
        float[] passive={.12f,.14f,.16f,.18f};
        for(int i=0;i<4;i++)c.observe(i,passive[i],0f,1f,0f,false);
        assertEquals(1,c.validQuads());
        assertEquals("linear material acceleration alone is NOT muscle credit",
            0f,c.mean(),.000001f);
        c.reset();
        float[] probed={.19f,.14f,.16f,.25f};
        for(int i=0;i<4;i++)c.observe(i,probed[i],0f,1f,0f,false);
        assertEquals("only ON-minus-OFF signed real movement",.07f,
            c.mean(),.000001f);
        assertEquals(.07f,Zero454PairedMotorContrast.balancedContrast(
            .19f,.14f,.16f,.25f),.000001f);
    }
    @Test public void touchContaminatedOrPartialPhysicalQuadsNeverTeach(){
        Zero454PairedMotorContrast c=new Zero454PairedMotorContrast();
        for(int i=0;i<4;i++)c.observe(i,i==0||i==3?.2f:0f,0f,
            1f,0f,i==2);
        assertEquals(0,c.validQuads());
        assertTrue(Float.isNaN(c.mean()));
        for(int i=0;i<3;i++)c.observe(i,.3f,0f,1f,0f,false);
        assertEquals("incomplete 4-frame physically matched trial is not evidence",
            0,c.validQuads());
        c.observe(3,.3f,0f,1f,0f,false);
        assertEquals(1,c.validQuads());
        assertEquals(0f,c.mean(),.000001f);
    }
    @Test public void originalCortexReallyTogglesOnePhysicalProbeWithV319GripOwned(){
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        cortex.enableIsolatedCreditV390();
        cortex.enableDeliveredLocalCreditLabV421();
        cortex.enablePairedMotorContrastLabV454();
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        Zero3LearningFoundation.MotorIntent intent=
            new Zero3LearningFoundation.MotorIntent(454L,0L,cortex.revision(),
                1f,0f,.65f,.5f,.25f);
        cortex.begin(true,intent,true);
        assertEquals("only one original real motor probed",1,cortex.activeProbeCountV390());
        for(int i=0;i<8;i++){
            float[] muscle=cortex.command(intent,body,skin,1f/60f);
            assertEquals(i%4,cortex.lastPhysicalProbePhaseV454());
            assertEquals(Zero454PairedMotorContrast.probeOn(i%4),
                cortex.lastPhysicalProbePhaseV454()==0||
                cortex.lastPhysicalProbePhaseV454()==3);
            assertEquals(64,muscle.length);
            for(float v:muscle)assertTrue(Float.isFinite(v)&&Math.abs(v)<=1f);
        }
        Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe=
            cortex.captureProbeV332();
        assertNotNull(probe.deliveredNoise);
        assertNotEquals("V319 CH2 is never selected",2,probe.selectedChannel);
        cortex.discard();
    }
    @Test public void defaultOriginalCortexRemainsUnchangedWithoutLabEnable(){
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        assertFalse(cortex.pairedMotorContrastLabV454());
        Zero454PairedMotorContrast c=new Zero454PairedMotorContrast();
        assertTrue(Zero454PairedMotorContrast.probeOn(0));
        assertFalse(Zero454PairedMotorContrast.probeOn(1));
        assertFalse(Zero454PairedMotorContrast.probeOn(2));
        assertTrue(Zero454PairedMotorContrast.probeOn(3));
    }
}
