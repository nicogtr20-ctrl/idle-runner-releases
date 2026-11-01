package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO461 isolated V319 physical grip exploration control experiment.
 * These tests verify the exact original 16*CH2 head, not organism
 * competence (the original 2x10000 live Android behavioral gates decide).
 */
@RunWith(AndroidJUnit4.class)
public final class Zero461V319OriginalGripExplorationAblationAndroidTest {
    @Test public void realOriginalHeadStillExploresWhenLabIsNotEnabled(){
        Zero3ContextAdhesionV319 head=new Zero3ContextAdhesionV319(0x356A33L,true);
        assertFalse(head.deterministicGripPhysicalEvidenceLabV461());
        head.begin(false);
        assertTrue("original V319 16 CH2 exploratory logits must exist",
            head.appliedGripProbeL1V461()>.01f);
        head.abortOriginalV356();
        assertEquals(0L,head.suppressedV319ProbeDecisionsV461());
    }
    @Test public void labRemovesOnlyPhysicalGripPerturbationAndPreservesRngConsumption(){
        Zero3ContextAdhesionV319 baseline=new Zero3ContextAdhesionV319(0x356A33L,true);
        Zero3ContextAdhesionV319 ablated=new Zero3ContextAdhesionV319(0x356A33L,true);
        Zero3ContextAdhesionV319.State initial=ablated.captureAtDecisionBoundary();
        ablated.enableDeterministicGripPhysicalEvidenceLabV461();
        Zero3ContextAdhesionV319.State enabled=ablated.captureAtDecisionBoundary();
        assertEquals("experimental flag must not advance saved V319 RNG",
            initial.rngState,enabled.rngState);
        assertArrayEquals("no change to learned neural grip weights",
            initial.weights(),enabled.weights(),0f);
        for(int i=0;i<5;i++){
            baseline.begin(i%5==0);
            ablated.begin(i%5==0);
            assertTrue(baseline.appliedGripProbeL1V461()>.01f);
            assertEquals(0f,ablated.appliedGripProbeL1V461(),0f);
            baseline.abortOriginalV356();
            ablated.abortOriginalV356();
            assertEquals("exact independent V319 RNG draws preserved, even while noise gated",
                baseline.captureAtDecisionBoundary().rngState,
                ablated.captureAtDecisionBoundary().rngState);
        }
        assertEquals(5L,ablated.suppressedV319ProbeDecisionsV461());
    }
    @Test public void cannotEnableMidActuationAndFrozenGripIsUnaffected(){
        Zero3ContextAdhesionV319 head=new Zero3ContextAdhesionV319(0x356A33L,true);
        head.begin(false);
        boolean refused=false;
        try{head.enableDeterministicGripPhysicalEvidenceLabV461();}
        catch(IllegalStateException ok){refused=true;}
        assertTrue("never change original physical muscle actuation in-flight",refused);
        head.abortOriginalV356();
        head.enableDeterministicGripPhysicalEvidenceLabV461();
        assertTrue(head.deterministicGripPhysicalEvidenceLabV461());
        Zero3ContextAdhesionV319 freeze=new Zero3ContextAdhesionV319(0x356A33L,false);
        freeze.begin(true);
        assertEquals("existing frozen V319 stays deterministic",
            0f,freeze.appliedGripProbeL1V461(),0f);
        freeze.abortOriginalV356();
    }
    @Test public void zero462CannotInventPositiveGripExplorationCreditWithoutDeliveredProbe(){
        // V461 suppresses every actual CH2 perturbation. An observed legal
        // grip is still real, but not evidence of a sampled logit gradient.
        assertEquals(0f,Zero3ContextAdhesionV319.gripProbeCreditFactorV462(0f,true),0f);
        assertEquals(0f,Zero3ContextAdhesionV319.gripProbeCreditFactorV462(.8f,true),0f);
        // Normal original V319 must retain its historical credit scale.
        assertEquals(.15f,Zero3ContextAdhesionV319.gripProbeCreditFactorV462(0f,false),0f);
        assertEquals(.8f,Zero3ContextAdhesionV319.gripProbeCreditFactorV462(-.8f,false),0f);
    }

}
