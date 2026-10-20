package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import static org.junit.Assert.*;

/** Proven real mistake: setting adaptationEnabled=false did NOT previously
 * suppress the every-third-decision fast motor noise. Verify no separate
 * motor policy, and old training path still has its original probes.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero372FrozenPeripheralProbeAndroidTest {
    @Test public void singleGRU3FrozenPeripheralNeverExploresUncreditedJitter(){
        MotorBrain.Body b=new MotorBrain.Body();Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMotorHeadV1 head=new SymbioteMotorHeadV1();
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        for(int n=0;n<10;n++){
            float direction=(float)(n*Math.PI/5);
            Zero3MotorPeripheralBridgeV364.begin(head,b,skin,null,objects,
                (float)Math.cos(direction),(float)Math.sin(direction));
            assertFalse("V3 frozen peripheral cannot perform unauthorized muscle probe",
                head.fastCircuit().activelyExploringV372());
        }
    }
    @Test public void legacyControllerOriginalProbeRemainsAvailable(){
        MotorBrain.Body b=new MotorBrain.Body();Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMotorHeadV1 old=new SymbioteMotorHeadV1();
        old.setAdaptationEnabled(true);
        old.chosen(0,DistributedBodySense.sense(b,skin,null,new ArrayList<>()),
            new float[RecurrentCoreV22.Z],0f,null,
            SymbioteMotorHeadV1.measuredSectorAngles(b,skin),b,skin);
        assertTrue("old V2.2 first training exploration still works",
            old.fastCircuit().activelyExploringV372());
        old.chosen(0,DistributedBodySense.sense(b,skin,null,new ArrayList<>()),
            new float[RecurrentCoreV22.Z],0f,null,
            SymbioteMotorHeadV1.measuredSectorAngles(b,skin),b,skin);
        assertFalse("old V2.2 alternating natural training cadence still works",
            old.fastCircuit().activelyExploringV372());
    }
}
