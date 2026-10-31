package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * ZERO420 integration contract: V371/V377 must never attribute
 * wall-arrest, actual RED damage or external solver motion to muscles.
 * Original contact negative valence remains the GRU3's responsibility.
 * No new alternative actor, body simulator, training label or checkpoint.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero420MotorMapEligibilityAndroidTest {
    private static boolean eligible(boolean real,boolean disturbed,boolean external,
            boolean censored,boolean terminal,float harm,boolean wall,boolean rear){
        return Zero420MotorMapEligibility.fromRealDecision(real,disturbed,external,
            censored,terminal,harm,wall,rear);
    }
    @Test public void genuineUnobstructedRealMuscleCanTrain(){
        assertTrue(eligible(true,false,false,false,false,0f,false,false));
    }
    @Test public void genuineRearOnlyEscapeCanTrainButNeverFrontalWall(){
        assertTrue(eligible(true,false,false,false,false,0f,true,true));
        assertFalse(eligible(true,false,false,false,false,0f,true,false));
        try{
            eligible(true,false,false,false,false,0f,false,true);
            fail("Cannot claim verified wall escape without real solid contact");
        }catch(IllegalArgumentException expected){}
    }
    @Test public void actualHarmAlwaysCensorsMotorButNotWorldContact(){
        assertFalse(eligible(true,false,false,false,false,7f,false,false));
        assertFalse(eligible(true,false,false,false,false,2f,true,true));
        assertFalse(eligible(true,false,false,false,false,.001f,false,false));
        try{eligible(true,false,false,false,false,Float.NaN,false,false);
            fail("Unmeasured harm cannot be treated as clean motor");
        }catch(IllegalArgumentException expected){}
    }
    @Test public void stormTeleportTerminalAndNoActualProbeCannotTrain(){
        assertFalse(eligible(true,true,false,false,false,0,false,false));
        assertFalse(eligible(true,false,true,false,false,0,false,false));
        assertFalse(eligible(true,false,false,true,false,0,false,false));
        assertFalse(eligible(true,false,false,false,true,0,false,false));
        assertFalse(eligible(false,false,false,false,false,0,false,false));
    }
    @Test public void passiveRearWallCoastingCannotBeCreditedAsOwnAcceleration(){
        assertFalse(Zero420MotorMapEligibility.measuredForwardAccelerationV430(
            3f,0f,1f,0f,1f,0f));
        assertFalse(Zero420MotorMapEligibility.measuredForwardAccelerationV430(
            0f,0f,0f,0f,1f,0f));
    }
    @Test public void measuredForwardVelocityChangeCanPassRearOnlyEvidenceGate(){
        assertTrue(Zero420MotorMapEligibility.measuredForwardAccelerationV430(
            0f,0f,1f,0f,1f,0f));
        assertTrue(Zero420MotorMapEligibility.measuredForwardAccelerationV430(
            -2f,0f,-1f,0f,1f,0f));
    }
    @Test public void sidewaysMotionAndCorruptSensorsCannotForgeRearAcceleration(){
        assertFalse(Zero420MotorMapEligibility.measuredForwardAccelerationV430(
            0f,0f,0f,2f,1f,0f));
        try{
            Zero420MotorMapEligibility.measuredForwardAccelerationV430(
                Float.NaN,0f,1f,0f,1f,0f);
            fail("Unmeasured PRE COM must never authorize motor learning");
        }catch(IllegalArgumentException expected){}
    }
    @Test public void passiveRearRetreatCannotChangeOriginalV377Weights()throws Exception{
        Zero3InstrumentalCreditV371 motor=new Zero3InstrumentalCreditV371();
        byte[] original=v377Bytes(motor);
        boolean realRearOnly=true;
        boolean positiveAcceleration=Zero420MotorMapEligibility.measuredForwardAccelerationV430(
            2f,0f,1f,0f,1f,0f);
        genuineMotorEvent(motor,eligible(true,false,false,false,false,0f,
            true,realRearOnly&&positiveAcceleration),true);
        assertArrayEquals("Passive rear-wall coasting cannot teach V377",original,v377Bytes(motor));
    }
    private static byte[] v377Bytes(Zero3InstrumentalCreditV371 motor)throws Exception{
        Field f=Zero3InstrumentalCreditV371.class.getDeclaredField("inertia");
        f.setAccessible(true);
        Zero3InertialResponseV377 inertia=(Zero3InertialResponseV377)f.get(motor);
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        inertia.write(new DataOutputStream(out));
        return out.toByteArray();
    }
    private static void genuineMotorEvent(Zero3InstrumentalCreditV371 motor,
                                         boolean clean,boolean touched)throws Exception{
        float[] actual194=new float[194];
        float[] issued={1f,0f,.5f,.5f,.3f};
        motor.begin(actual194,issued,false);
        motor.complete(3f,0f,.30f,clean,false,touched);
    }
    @Test public void originalV371DoesNotTrainOnCensoredRedOrFrontWall()throws Exception{
        Zero3InstrumentalCreditV371 motor=new Zero3InstrumentalCreditV371();
        byte[] original=v377Bytes(motor);
        // Same original experienced actual physical movement; only its
        // causal eligibility differs. Keep RED valence learning elsewhere.
        genuineMotorEvent(motor,eligible(true,false,false,false,false,7f,
            false,false),false);
        assertArrayEquals("Real RED velocity damping is not a motor action",
            original,v377Bytes(motor));
        genuineMotorEvent(motor,eligible(true,false,false,false,false,0f,
            true,false),true);
        assertArrayEquals("Front wall stopping must not update V377 motor map",
            original,v377Bytes(motor));
        genuineMotorEvent(motor,eligible(true,false,false,false,false,0f,
            true,true),true);
        assertFalse("Physically verified rear-only retreat remains teachable",
            Arrays.equals(original,v377Bytes(motor)));
    }
}
