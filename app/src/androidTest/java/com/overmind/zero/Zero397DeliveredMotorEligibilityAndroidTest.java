package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V397 true physical motor synaptic-eligibility evidence, no saved brain
 * rewrites and no V2.2 fallback. Same genuine user 33,312-decision V388
 * original cortex, original 170-mass material, matched train/readout.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero397DeliveredMotorEligibilityAndroidTest {
    private static float[] privateArray(Object obj,String name)throws Exception{
        Field f=obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return (float[])f.get(obj);
    }
    @Test public void saturatedMotorCannotAcquirePhantomCausalNoiseCredit()
        throws Exception{
        JSONObject original=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            original.getString("training_room_1_scene_v379"));
        assertNotNull(scene);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        float[] weights=privateArray(cortex,"weights");
        Arrays.fill(weights,0f);
        for(int motor=0;motor<64;motor++)weights[motor*8]=1.5f;
        cortex.enableIsolatedCreditV390();
        cortex.enableMeasuredProbeCreditLabV397();
        Zero3LearningFoundation.MotorIntent intent=
            new Zero3LearningFoundation.MotorIntent(
                70,1,cortex.revision(),1f,0f,1f,.5f,.4f);
        cortex.begin(true,intent,true);
        float[] actual=cortex.command(intent,scene.body,scene.skin,1f/60f);
        float[] nominal=privateArray(cortex,"noise"),
                delivered=privateArray(cortex,"deliveredProbeV397");
        int probes=0;
        for(int i=0;i<64;i++)if(Math.abs(nominal[i])>1e-8f){
            probes++;
            assertEquals("Actual CH0/1/3 command is saturated",1f,actual[i],0f);
            assertEquals("No learned update when ACTUAL command cannot change",
                0f,delivered[i],0f);
        }
        assertEquals("V390 probes one true muscle only",1,probes);
        cortex.discard();
        Log.i("ZERO397_NEURAL","SATURATED_MUSCLE_ZERO_FALSE_CREDIT=PASS");
    }

    @Test(timeout=900000)
    public void savedS21NeuralCortexMatchedActualVsNominalMuscleEligibility()
        throws Exception{
        JSONObject original=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject second=new JSONObject(original.toString());
        second.put("training_room_1_scene_v379",
            original.getString("training_room_1_free_scene_v379"));
        for(boolean measured:new boolean[]{false,true}){
            Zero3LearningFoundation.AdaptiveCortex cortex=
                Zero390ActualUserMotorRehabilitationAndroidTest.trained(original);
            long start=cortex.updates();
            for(int i=0;i<128;i++){
                int heading=i%4;
                Zero390ActualUserMotorRehabilitationAndroidTest.execute(
                    original,cortex,true,heading,true,i,1,measured);
            }
            assertTrue("Original GRU3 must learn actual executed episodes",
                cortex.updates()>start);
            for(int heading=0;heading<4;heading++){
                float old=Zero390ActualUserMotorRehabilitationAndroidTest.execute(
                    original,Zero390ActualUserMotorRehabilitationAndroidTest.trained(original),
                    true,heading,false,7000+heading,5,measured);
                float trained=Zero390ActualUserMotorRehabilitationAndroidTest.execute(
                    original,cortex,true,heading,false,8000+heading,5,measured);
                float freshPose=Zero390ActualUserMotorRehabilitationAndroidTest.execute(
                    second,cortex,true,heading,false,9000+heading,5,measured);
                assertTrue(Float.isFinite(old)&&Float.isFinite(trained)&&
                    Float.isFinite(freshPose));
                Log.i("ZERO397_NEURAL","TRUE_S21_SPARSE_CREDIT measured="+measured+
                    ",heading="+heading+",before="+old+",after="+trained+
                    ",otherPose="+freshPose+",updates="+(cortex.updates()-start));
            }
        }
        Log.i("ZERO397_NEURAL",
            "DELIVERED_PROBE_ELIGIBILITY_AB=MEASURED_ONLY_NO_USER_RESET");
    }
}
