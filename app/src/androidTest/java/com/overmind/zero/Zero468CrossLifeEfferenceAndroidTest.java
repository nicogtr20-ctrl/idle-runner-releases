package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V468: a newly born original body has no previous body's issued muscle echo. */
@RunWith(AndroidJUnit4.class)
public final class Zero468CrossLifeEfferenceAndroidTest {
    @Test public void originalNewbornCannotSensePreviousLifeMuscleCommands() throws Exception {
        Zero3OriginalBrainV356 brain=Zero3OriginalBrainV356.load(
            InstrumentationRegistry.getInstrumentation().getTargetContext(),null,false);
        try {
            float[] old=brain.commands();
            assertEquals(64,old.length);
            for(int i=0;i<old.length;i++)old[i]=i%2==0?.75f:-.5f;
            assertEquals(.75f,brain.commands()[0],0f);
            brain.newLife();
            for(float value:brain.commands())assertEquals(0f,value,0f);
            assertEquals(0L,brain.decisions());
            Zero3OriginalBrainV356.Readout state=
                Zero3OriginalBrainV356.inspectSaved(brain.checkpoint());
            assertEquals(194,state.sensoryInputs);
            assertEquals(64,state.hiddenUnits);
            assertEquals(64,state.actionOutputs);
            assertEquals(16,state.adhesionSectors);
        } finally { brain.close(); }
    }
}
