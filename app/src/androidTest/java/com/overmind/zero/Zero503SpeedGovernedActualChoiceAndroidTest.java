package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO503 genuine 256 self-issued rapid GRU directions and original body:
 * compare V501 efficient anatomical motor under 5 physically measured
 * speed governors, on the exact same initial tissue and prior COM drift.
 * Research only, no world reward intervention or velocity clipping.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero503SpeedGovernedActualChoiceAndroidTest {
    @Test(timeout=5400000)
    public void recordedOriginal256ActionsCompareFiveStablePhysicalSpeedCaps()
            throws Exception {
        JSONObject saved=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        Zero501RecordedSequentialMotorAndroidTest.Sample[] recorded=
            Zero501RecordedSequentialMotorAndroidTest.originalFlight(saved);
        int[] modes={1,2,3,4,8,9,10,11,12};
        for(int mode:modes) {
            Zero501RecordedSequentialMotorAndroidTest.Result outcome=
                Zero501RecordedSequentialMotorAndroidTest.replayWithTraction(
                    saved,recorded,mode,-.04f);
            assertEquals(256,outcome.decisions);
            Log.i("ZERO503_CAP",String.format(Locale.US,
                "mode=%d actualDecisions=%d signed=%.7f gross=%.7f "+
                "efficiency=%.7f aligned=%d opposed=%d actualPeakSpeed=%.4f",
                mode,outcome.decisions,outcome.signed,outcome.gross,
                outcome.efficiency(),outcome.aligned,outcome.opposed,
                outcome.peakSpeed));
        }
        Log.i("ZERO503_CAP",
            "ORIGINAL_256_ACTUAL_GRU_SPEED_GOVERNOR_AB_COMPLETE"+
            " researchOnly=true noArtificialAcceleration=true");
    }
}
