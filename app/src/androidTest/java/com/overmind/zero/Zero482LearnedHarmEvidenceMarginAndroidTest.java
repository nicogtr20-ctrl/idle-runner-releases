package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V482 eligibility regression; ONLY original Android 2x10000 decides behavior. */
@RunWith(AndroidJUnit4.class)
public final class Zero482LearnedHarmEvidenceMarginAndroidTest {
    private static float[] seen(){
        float[] observation=new float[194];
        observation[20+8]=.9f; // genuine wavelength-1 retinal photon slot
        return observation;
    }
    @Test public void noLearnedContactKeepsUncertainSceneMargin(){
        assertEquals(.025f,Zero3CognitionV353.learnedHarmSelectionMarginV482(
            new Zero3ContactValenceV369(),seen()),0f);
    }
    @Test public void realBenefitDoesNotWaiveConservativeMargin(){
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.materialContact(1,22f);v.materialContact(1,22f);
        assertEquals(.005f,Zero3CognitionV353.learnedHarmSelectionMarginV482(v,seen()),0f);
    }
    @Test public void onlyRepeatedRealHarmAndVisiblePhotonsWaiveDeadband(){
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.materialContact(1,-7f);
        assertEquals(.025f,Zero3CognitionV353.learnedHarmSelectionMarginV482(v,seen()),0f);
        v.materialContact(1,-7f);
        assertTrue(v.hasVisibleLearnedHarmV422(seen()));
        assertEquals(0f,Zero3CognitionV353.learnedHarmSelectionMarginV482(v,seen()),0f);
    }
    @Test public void learnedHarmWithoutCurrentlyVisiblePhotonsCannotWaive(){
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.materialContact(1,-7f);v.materialContact(1,-7f);
        float[] observation=seen();observation[28]=.1f; // below actual V422 visibility
        assertFalse(v.hasVisibleLearnedHarmV422(observation));
        assertEquals(.025f,Zero3CognitionV353.learnedHarmSelectionMarginV482(v,observation),0f);
    }
}
