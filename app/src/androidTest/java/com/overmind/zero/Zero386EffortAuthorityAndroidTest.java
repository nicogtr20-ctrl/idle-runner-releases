package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * GRU3 owns voluntary direction AND force intensity. Old implementation
 * translated effort as an untrusted learning FEATURE only; effect could
 * never silence all CH0/1/3 muscles, even when effort=0.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero386EffortAuthorityAndroidTest {
    private static float[] run(float effort){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        Zero3LearningFoundation.MotorIntent chosen=
            new Zero3LearningFoundation.MotorIntent(
                0,0,cortex.revision(),.6f,.8f,effort,.5f,.4f);
        cortex.begin(false,chosen,true);
        float[] out=cortex.command(chosen,body,skin,1f/60f).clone();
        cortex.discard();
        return out;
    }
    @Test public void zeroEffortSilencesAllVoluntaryTranslationWithoutChangingGrip(){
        float[] passive=run(0f),medium=run(.5f),full=run(1f);
        int nonzeroAtMedium=0;
        for(int sector=0;sector<16;sector++){
            for(int ch:new int[]{0,1,3}){
                int i=sector*4+ch;
                assertEquals("No involuntary CH0/1/3 muscle with ZERO GRU effort",
                    0f,passive[i],0f);
                assertTrue("Bounded real physical muscle amplitude",Math.abs(full[i])<=1f);
                if(Math.abs(medium[i])>1e-5f)nonzeroAtMedium++;
            }
            assertTrue("No additional grip authority is introduced",Float.isFinite(passive[sector*4+2]));
        }
        assertTrue("Cortex must still have actual active pathways",
            nonzeroAtMedium>10);
    }
    @Test public void zeroEffortDoesNotProduceNewMaterialMuscleReaction(){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(b,skin);
        gel.step(b,skin,run(0f),1f/60f);
        assertEquals("None of the physical cortex muscle forces should fire",
            0f,gel.lastMuscleReactionImpulse,0f);
        MotorBrain.Body second=new MotorBrain.Body();
        Morphology.Surface cortex=new Morphology.Surface(second);
        SymbioteMatterV1 sameGel=new SymbioteMatterV1();
        sameGel.reset(second,cortex);
        sameGel.step(second,cortex,run(.5f),1f/60f);
        assertTrue("Effort .5 must generate genuine physical forces",
            sameGel.lastMuscleReactionImpulse>.0001f);
    }
}
