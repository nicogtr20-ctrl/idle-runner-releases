package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO502: original 256 real human-shared GRU choices, with their measured
 * durations, on EXACT saved real 170-site body. Compare the V499 original
 * turn RELEASE (-.04) to neutral 0 and genuine external floor-support
 * loading at +.025, +.05 and +.07. No "turn towards goal", no mutation,
 * no body teleport. Includes a second independent muscle model as control.
 *
 * Measured actual trajectory on free material only: not evidence of red/blue
 * avoidance or true autonomous model transfer.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero502RealDecisionTurnTractionAndroidTest {
    @Test(timeout=5400000)
    public void actualPhoneNeuralSwitchesVsPhysicalFootholdBraking()
            throws Exception {
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        Zero501RecordedSequentialMotorAndroidTest.Sample[] all=
            Zero501RecordedSequentialMotorAndroidTest.originalFlight(user);
        float[] brakes={-.04f,0f,.025f,.05f,.07f};
        for(float brake:brakes){
            Zero501RecordedSequentialMotorAndroidTest.Result r=
                Zero501RecordedSequentialMotorAndroidTest.replayWithTraction(
                    user,all,1,brake);
            Log.i("ZERO502_TRACTION",String.format(Locale.US,
                "physicalTurnBrake=%.3f model=V499 trueDecisions=%d "+
                "actualSigned=%.7f gross=%.7f efficiency=%.7f "+
                "actualAligned=%d actualOpposed=%d peakSpeed=%.4f",
                brake,r.decisions,r.signed,r.gross,r.efficiency(),
                r.aligned,r.opposed,r.peakSpeed));
            assertEquals(256,r.decisions);
        }
        for(float brake:new float[]{-.04f,.07f}){
            Zero501RecordedSequentialMotorAndroidTest.Result r=
                Zero501RecordedSequentialMotorAndroidTest.replayWithTraction(
                    user,all,6,brake);
            Log.i("ZERO502_TRACTION",String.format(Locale.US,
                "physicalTurnBrake=%.3f model=V501-lateral-damped trueDecisions=%d "+
                "actualSigned=%.7f gross=%.7f efficiency=%.7f "+
                "actualAligned=%d actualOpposed=%d peakSpeed=%.4f",
                brake,r.decisions,r.signed,r.gross,r.efficiency(),
                r.aligned,r.opposed,r.peakSpeed));
        }
        Log.i("ZERO502_TRACTION","ORIGINAL_256_REAL_CHOICE_FOOTHOLD_BRAKING_AB_COMPLETE"+
            " noForceInjected=true samePhysicalInitialBody=true noProductionMutation=true");
    }
}
