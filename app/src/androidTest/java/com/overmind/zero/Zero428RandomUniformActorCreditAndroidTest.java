package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V428: joint-score eligibility when random impasse exploration bypasses
 * cognition selection. These source-level checks are not behavioral proof.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero428RandomUniformActorCreditAndroidTest {
    @Test public void randomizedIndependentUniformHeadingHasNoActorCredit(){
        assertTrue(Zero3RecurrentBrain.headingCreditShouldSkipV428(true,true,true));
    }
    @Test public void learnedSelectionOfUniformHeadingStillCreditsCompetingSet(){
        assertFalse(Zero3RecurrentBrain.headingCreditShouldSkipV428(true,true,false));
    }
    @Test public void randomlyChosenActorDependentHeadingRemainsEligible(){
        assertFalse(Zero3RecurrentBrain.headingCreditShouldSkipV428(true,false,true));
    }
    @Test public void legacyUniformImpassAlwaysCensored(){
        assertTrue(Zero3RecurrentBrain.headingCreditShouldSkipV428(false,true,false));
        assertTrue(Zero3RecurrentBrain.headingCreditShouldSkipV428(false,true,true));
    }
    @Test public void ordinaryOnPolicyHeadingIsUnchanged(){
        assertFalse(Zero3RecurrentBrain.headingCreditShouldSkipV428(false,false,false));
        assertFalse(Zero3RecurrentBrain.headingCreditShouldSkipV428(true,false,false));
    }
}
