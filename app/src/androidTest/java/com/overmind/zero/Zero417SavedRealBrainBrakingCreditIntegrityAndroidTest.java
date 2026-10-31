package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.json.JSONObject;
import static org.junit.Assert.*;

/**
 * V417 must NEVER rewrite or reset a real trained brain just by enabling
 * physically measured COM delta-velocity credit. This original Android QA
 * loads user's exact 33,312-decision 194-input/64-GRU trained checkpoint.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero417SavedRealBrainBrakingCreditIntegrityAndroidTest {
    @Test(timeout=900000)
    public void actualUser33312BrainWeightsAndV356CRCStayBitIdenticalAfterCreditOptIn()
            throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356 brain=Zero3OriginalBrainV356.load(
            InstrumentationRegistry.getInstrumentation().getTargetContext(),
            user.getString("brain"),true);
        try{
            assertEquals(33312L,brain.decisions());
            assertTrue(brain.realPhysicalMotorContextV412());
            String before=brain.checkpoint();
            String baseDiag=brain.localCreditDiagnosticsV400();
            assertTrue("Experimental neural credit must default OFF",
                baseDiag.contains("realVelocityCreditLab=false"));
            brain.enableGenuineComVelocityCreditLabV417();
            brain.requestLocalTemporalCreditLabV400();
            brain.enableImmediateOwnDecisionCreditLabV423();
            brain.enableSafeMotorMapEvidenceLabV420();
            String after=brain.checkpoint();
            assertEquals("New neural-learning evidence must not change any "+
                "existing source weight, recurrent slow state, V319 grip, "+
                "cursor or V356 CRC before REAL experience",before,after);
            String diagnostic=brain.localCreditDiagnosticsV400();
            assertTrue(diagnostic.contains("realVelocityCreditLab=true"));
            assertTrue("V418 only changes ephemeral learning eligibility, not saved state",
                diagnostic.contains("immediateOwnDecisionCreditLabV423=true"));
            assertTrue("V429 causal motor-map opt-in must remain transient",
                diagnostic.contains("safeMotorMapLabV420=true"));
            Zero3OriginalBrainV356.Readout saved=
                Zero3OriginalBrainV356.inspectSaved(after);
            assertEquals(33312L,saved.decisions);
            assertEquals(19452L,saved.motorUpdates);
            assertEquals(194,saved.sensoryInputs);
            assertEquals(64,saved.hiddenUnits);
            assertEquals(64,saved.actionOutputs);
            assertEquals(16,saved.adhesionSectors);
        }finally{
            brain.close();
        }
    }
}
