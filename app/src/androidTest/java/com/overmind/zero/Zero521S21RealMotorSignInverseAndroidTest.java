package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V521: retain the exact original real S21 33312-decision brain and 170
 * physical nodes. The 256 neural headings ACTUALLY logged by the user are
 * not fabricated directions. Compare unchanged transducer sign to the
 * ONLY 3 possible 2-axis muscle-sign counterfactuals, with unchanged
 * tissue, true 94-wall membrane CCD and separate genuine S21 free world.
 *
 * THIS IS OPEN-LOOP MOTOR CAUSAL DIAGNOSIS, NOT autonomous safety certification.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero521S21RealMotorSignInverseAndroidTest {
    @Test(timeout=5400000)
    public void trainedOriginalS21SavedFullWallAndIndependentFreeMuscleSignAB()
            throws Exception{
        JSONObject fixture=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero501RecordedSequentialMotorAndroidTest.Sample[] trace=
            Zero501RecordedSequentialMotorAndroidTest.originalFlight(
                new JSONObject().put("motorRecorderCsv",
                    fixture.getString("motorRecorderCsv")));
        assertEquals(256,trace.length);
        String[] scenes={
            "training_room_1_scene_v379",
            "training_room_1_free_scene_v379"
        };
        int contextIndex=0;
        double[] bestGain=new double[scenes.length];
        for(String name:scenes){
            String raw=fixture.getString(name);
            GameSceneSnapshot.Scene initial=GameSceneSnapshot.workingPosture(raw);
            assertNotNull("Must use user's exact saved S21 body",initial);
            assertNotNull(initial.matter);
            int realWalls=0;
            for(UnknownWorld.Entity entity:initial.objects)
                if(entity.isTrainingWall()&&!entity.consumed)realWalls++;
            assertEquals("Actual 94 wall S21 training snapshot",
                contextIndex==0?94:0,realWalls);
            float baseline=Float.NaN;
            float best=-2f;
            int bestMode=-1;
            for(int mode:new int[]{0,31,32,33}){
                Zero501RecordedSequentialMotorAndroidTest.Result z=
                    Zero518ActualTrainedS21PhysicalResponseAndroidTest.replay(
                        fixture,trace,raw,mode,true);
                if(mode==0)baseline=z.efficiency();
                else if(z.efficiency()>best){
                    best=z.efficiency();bestMode=mode;
                }
                Log.i("ZERO521_SIGN",String.format(Locale.US,
                    "savedS21=%s realWalls=%d mode=%d neuralActions=%d "+
                    "physicalSigned=%.6f gross=%.6f trackingEfficiency=%.6f "+
                    "forwardActions=%d opposedActions=%d peakSpeed=%.5f "+
                    "realMuscleImpulse=%.2f",
                    name,realWalls,mode,z.decisions,z.signed,z.gross,
                    z.efficiency(),z.aligned,z.opposed,z.peakSpeed,z.impulse));
                assertEquals("All original 256 real GRU choices executed",
                    256,z.decisions);
            }
            bestGain[contextIndex]=best-baseline;
            Log.i("ZERO521_SIGN",String.format(Locale.US,
                "REAL_S21_96_MEMBRANE_170_BODY_MOTOR_SIGN_AB context=%s "+
                "originalEfficiency=%.6f bestInverseEfficiency=%.6f "+
                "bestInverseMode=%d signedGain=%.6f walls=%d "+
                "autonomousSuccessUnproven=true",
                name,baseline,best,bestMode,best-baseline,realWalls));
            contextIndex++;
        }
        Log.i("ZERO521_SIGN",String.format(Locale.US,
            "REAL_S21_TWO_INDEPENDENT_PHYSICAL_CONTEXTS_SIGN_AB_FINISHED "+
            "trainedWallGain=%.6f independentFreeGain=%.6f",
            bestGain[0],bestGain[1]));
        // A pass certifies execution and full saved-body invariants ONLY.
        // Sign inversion is an intervention, never validated by an in-sample
        // best mode. The 10k autonomy gate remains separate and mandatory.
        assertTrue(Double.isFinite(bestGain[0])&&Double.isFinite(bestGain[1]));
    }
}
