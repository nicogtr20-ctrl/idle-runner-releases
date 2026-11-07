package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.Locale;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO507: exact user phone-shared original 4747-decision cortex and
 * REAL last 256 GRU-selected heading sequence, matched original material.
 * V504 measured a .50s muscle time constant raising physical short-trace
 * signed/gross from -2.668% to +12.715%, with lower total impulse.
 *
 * V507 integrates that dynamic response INTO the ONLY true 64-motor cortex
 * rather than applying it after an observation the cortex would miss.
 * Compare the original V499, the older lab physical actuator smoothing
 * oracle and new production-wirable V507 motor, on same exact state.
 * This replay is physically real but NOT a natural new GRU continuation.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero507RealOriginalMuscleDynamicsEquivalenceAndroidTest {
    @Test(timeout=1500000)
    public void realOriginalCortexPeripheryMatchesPhysicalSmoothed256Trace()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        JSONArray trace=Zero504RealRecordedRecent256MuscleDynamicsAndroidTest.recorded();
        Zero504RealRecordedRecent256MuscleDynamicsAndroidTest.Report unfiltered=
            Zero504RealRecordedRecent256MuscleDynamicsAndroidTest.replay(
                user,trace,0f);
        Zero504RealRecordedRecent256MuscleDynamicsAndroidTest.Report external=
            Zero504RealRecordedRecent256MuscleDynamicsAndroidTest.replay(
                user,trace,.50f,false);
        Zero504RealRecordedRecent256MuscleDynamicsAndroidTest.Report v507=
            Zero504RealRecordedRecent256MuscleDynamicsAndroidTest.replay(
                user,trace,.50f,true);
        float baseline=unfiltered.signed/Math.max(1f,unfiltered.gross);
        float laboratory=external.signed/Math.max(1f,external.gross);
        float integrated=v507.signed/Math.max(1f,v507.gross);
        Log.i("ZERO507_DYNAMIC",String.format(Locale.US,
            "EXACT_ORIGINAL_NEWBORN_256_MUSCLE_ACTUATION "+
            "V499eff=%.7f matchedExternalEff=%.7f "+
            "V507IntegratedEff=%.7f "+
            "originalImpulse=%.4f matchedExternalImpulse=%.4f "+
            "V507Impulse=%.4f originalAligned=%d "+
            "integratedAligned=%d integratedOpposed=%d "+
            "relativeSignedGain=%.7f",
            baseline,laboratory,integrated,unfiltered.impulse,
            external.impulse,v507.impulse,unfiltered.aligned,
            v507.aligned,v507.opposed,integrated-baseline));
        assertTrue("Internal V507 should rescue many short-gru direction reversals",
            integrated>baseline+.035f);
        assertTrue("Production internal filter must reproduce real V504 matched "+
            "physical sensitivity (no fictional force or direction)",
            Math.abs(integrated-laboratory)<.08f);
        assertTrue("Muscle kinetics must lower total physical effort",
            v507.impulse<unfiltered.impulse*.88f);
        assertTrue("No physically invalid original tissue transitions",
            v507.invalid==0L);
        Log.i("ZERO507_DYNAMIC",
            "ORIGINAL_LAST256_ACTUAL_TISSUE_V507_INTEGRATED_FILTER_PASS"+
            " researchReplayNotAutonomous=true");
    }
}
