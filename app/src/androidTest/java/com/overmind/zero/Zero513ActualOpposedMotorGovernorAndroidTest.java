package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V513: identical original user GRU3 256 recorded neural decisions,
 * same saved 170-site physical starting state, 96 actual membrane
 * normals, same tissue, CH1, local 512 weights, no object reward or
 * artificial center force. Only 5 measured speed governor laws differ.
 * Research replay, NOT independent autonomous competence.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero513ActualOpposedMotorGovernorAndroidTest {
    @Test(timeout=5400000)
    public void original256DecisionsComparePhysicalInertiaBrakingOnly()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        Zero501RecordedSequentialMotorAndroidTest.Sample[] trace=
            Zero501RecordedSequentialMotorAndroidTest.originalFlight(user);
        double baseline=Double.NaN;
        int variants=0;
        for(int mode:new int[]{14,18,19,20,21}){
            Zero501RecordedSequentialMotorAndroidTest.Result r=
                Zero501RecordedSequentialMotorAndroidTest.replayWithTraction(
                    user,trace,mode,-.04f);
            assertEquals(256,r.decisions);
            assertTrue("Physical body response must be finite",Float.isFinite(r.efficiency()));
            assertTrue(r.gross>500f);
            if(mode==14)baseline=r.efficiency();
            else if(Math.abs(r.efficiency()-baseline)>.001f)variants++;
            Log.i("ZERO513_BRAKE",String.format(Locale.US,
                "motorMode=%d originallyIssuedDecisions=256 signed=%.6f "+
                "gross=%.6f efficiency=%.6f aligned=%d opposed=%d "+
                "peakSpeed=%.4f physicalMuscleImpulse=%.2f",
                mode,r.signed,r.gross,r.efficiency(),r.aligned,
                r.opposed,r.peakSpeed,r.impulse));
        }
        assertTrue("Braking A/B must materially test at least one different actual regime",variants>=1);
        Log.i("ZERO513_BRAKE",
            "TRUE_ORIGINAL_256_NEURAL_COURSE_ACTUAL_170_SITE_PHYSICS_AB_COMPLETE noCenterThrust=true noRewardEdit=true");
    }
}
