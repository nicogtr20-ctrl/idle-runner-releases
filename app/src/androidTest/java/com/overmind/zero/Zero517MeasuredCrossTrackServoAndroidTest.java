package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Isolated original 170-particle real material AB; neural commands are
 * the user's *actual* saved 256 decisions in their original chronology.
 * No fake velocity reset, red/blue reward, authored direction or COM force.
 * This is not autonomous evidence and never authorizes an OTA. */
@RunWith(AndroidJUnit4.class)
public final class Zero517MeasuredCrossTrackServoAndroidTest {
    @Test(timeout=5400000)
    public void original256CompareUnchangedAndMeasuredCrossTrackTendonServo()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        Zero501RecordedSequentialMotorAndroidTest.Sample[] trace=
            Zero501RecordedSequentialMotorAndroidTest.originalFlight(user);
        double baseline=Double.NaN,best=-Double.MAX_VALUE;
        int bestMode=-1;
        for(int mode:new int[]{14,22,23,24,25}){
            Zero501RecordedSequentialMotorAndroidTest.Result result=
                Zero501RecordedSequentialMotorAndroidTest.replayWithTraction(
                    user,trace,mode,-.04f);
            assertEquals(256,result.decisions);
            assertTrue("Non-finite material tracking",Float.isFinite(result.efficiency()));
            assertTrue("Non-physical zero motion",result.gross>500f);
            Log.i("ZERO517_SERVO",String.format(Locale.US,
                "motorMode=%d originalNeuralActions=256 signed=%.6f gross=%.6f "+
                "efficiency=%.6f aligned=%d opposed=%d peakSpeed=%.4f "+
                "realMuscleImpulse=%.2f",
                mode,result.signed,result.gross,result.efficiency(),
                result.aligned,result.opposed,result.peakSpeed,result.impulse));
            if(mode==14)baseline=result.efficiency();
            else if(result.efficiency()>best){
                best=result.efficiency();bestMode=mode;
            }
        }
        Log.i("ZERO517_SERVO",String.format(Locale.US,
            "EXACT_ORIGINAL_256_NEURAL_170_BODY_SERVO_AB baseline=%.6f "+
            "best=%.6f bestMode=%d improvement=%.6f "+
            "noAutonomousReleaseClaim=true",baseline,best,bestMode,best-baseline));
        assertTrue("V517 requires at least +2 percentage points of genuine "+
            "physical directional efficiency vs untouched V508 real-body "+
            "mode14; no promotion if the physical servo is worse",
            best>=baseline+.02d);
    }
}
