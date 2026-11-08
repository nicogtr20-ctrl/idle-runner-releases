package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Same Nov 7 user original 4747-decision checkpoint and EXACT 96-point shape.
 * Same 256 past ORIGINAL self-issued GRU directions and recorded durations.
 * Only contrast the 96 material solver's true local surface force-normal
 * with V499's stale 16-sector centroid approximation. No new goal, color,
 * rewards, synthetic center thrust, resets or trained weights.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero507ActualMembraneActuatorFrameAndroidTest {
    @Test(timeout=5400000)
    public void physicallyMatchedNormalIsTestedAgainstActualOriginal256Directions()
            throws Exception{
        JSONObject saved=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        Zero501RecordedSequentialMotorAndroidTest.Sample[] trace=
            Zero501RecordedSequentialMotorAndroidTest.originalFlight(saved);
        for(int mode:new int[]{9,13,14,15,16,17}){
            Zero501RecordedSequentialMotorAndroidTest.Result result=
                Zero501RecordedSequentialMotorAndroidTest.replayWithTraction(
                    saved,trace,mode,-.04f);
            assertEquals(256,result.decisions);
            Log.i("ZERO507_NORMAL",String.format(Locale.US,
                "solverFrameMode=%d trueGRUdecisions=%d signed=%.7f "+
                "gross=%.7f efficiency=%.7f aligned=%d opposed=%d "+
                "actualPeakSpeed=%.4f actualImpulse=%.3f",
                mode,result.decisions,result.signed,result.gross,
                result.efficiency(),result.aligned,result.opposed,
                result.peakSpeed,result.impulse));
        }
        Log.i("ZERO507_NORMAL",
            "REAL_96_TISSUE_NORMAL_256_ORIGINAL_CHOICES_6_AB_COMPLETE"+
            " originalBrainUntouched=true noSynthethicThrust=true");
    }
}
