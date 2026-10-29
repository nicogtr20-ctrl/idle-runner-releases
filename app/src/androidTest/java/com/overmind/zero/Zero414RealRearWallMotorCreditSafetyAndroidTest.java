package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Only measured rear-only retreat can bypass the V401 blanket wall gate.
 * No blue/red world identity is read, no motion is generated, no brain reset.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero414RealRearWallMotorCreditSafetyAndroidTest {
    static boolean qualifies(int rear,int front,float along,float redHarm,
            boolean perturb,boolean external){
        boolean wall=rear+front>0;
        boolean verified=Zero401LocalMotorCreditSafety.rearWallCanTeachMuscleV414(
            wall,rear,front,along);
        return Zero401LocalMotorCreditSafety.cleanPhysicalDecision(
            perturb,true,false,false,external,external,false,
            wall&&!verified,redHarm)&&
            Zero401LocalMotorCreditSafety.hasPhysicalMotorProbe(.5f,
                perturb?1:0);
    }
    @Test public void actualRearWallRetreatCanTeachOriginalCortexWithoutHardcodedHeading(){
        assertTrue("Independent positive true travel from genuine rear wall",
            qualifies(15,0,.20f,0f,true,false));
        assertFalse("Pure front-wall blocking never becomes muscle success",
            qualifies(0,15,1.5f,0f,true,false));
        assertFalse("Mixed rear+front contacts are never an independent foothold",
            qualifies(10,10,1.5f,0f,true,false));
        assertFalse("A passive inertial wall arrest is not forward escape",
            qualifies(10,0,-.01f,0f,true,false));
        assertFalse("Sub-threshold wall drift is not a robust motor derivative",
            qualifies(10,0,.005f,0f,true,false));
        assertFalse("The genuine red injury always wins over good locomotion",
            qualifies(10,0,.3f,7f,true,false));
        assertFalse("A storm or solver correction is NOT muscle force",
            qualifies(10,0,.3f,0f,true,true));
        assertFalse("No real isolated perturbation => NO eligible learning",
            qualifies(10,0,.3f,0f,false,false));
        assertTrue("Original clean no-wall healthy motor trial stays eligible",
            qualifies(0,0,.3f,0f,true,false));
    }
    @Test public void rearWallLearningRejectsInvalidCausalPhysicalSignals(){
        try{
            Zero401LocalMotorCreditSafety.rearWallCanTeachMuscleV414(
                true,4,0,Float.NaN);
            fail("Invalid motion");
        }catch(IllegalArgumentException expected){}
        try{
            Zero401LocalMotorCreditSafety.rearWallCanTeachMuscleV414(
                true,-1,0,1f);
            fail("Invalid contact data");
        }catch(IllegalArgumentException expected){}
        assertFalse(Zero401LocalMotorCreditSafety.rearWallCanTeachMuscleV414(
            false,5,0,.9f));
        assertFalse(Zero401LocalMotorCreditSafety.rearWallCanTeachMuscleV414(
            true,0,0,.9f));
    }
}
