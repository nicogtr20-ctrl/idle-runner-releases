package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V427 source-level off-policy eligibility contract. Not a 10k behavior claim. */
@RunWith(AndroidJUnit4.class)
public final class Zero427JointOffPolicyEligibilityAndroidTest {
    @Test public void legacySelectedNoiseCannotCreditUniformImpass(){
        assertTrue(Zero3RecurrentBrain.offPolicyHeadingShouldSkipV427(false,true));
    }
    @Test public void fullSampledSetScoreRemainsEligibleIfUniformCandidateWon(){
        assertFalse(Zero3RecurrentBrain.offPolicyHeadingShouldSkipV427(true,true));
    }
    @Test public void onPolicyHeadingNeverCensoredByImpassRule(){
        assertFalse(Zero3RecurrentBrain.offPolicyHeadingShouldSkipV427(false,false));
        assertFalse(Zero3RecurrentBrain.offPolicyHeadingShouldSkipV427(true,false));
    }
    @Test public void independentUniformCandidatesContributeNoLatentScore(){
        float[][] noise={{.05f,-.03f},{-.02f,.04f},{.01f,.02f},{.15f,.1f},{-.1f,.12f}};
        float[][] scale={{1,1},{1,1},{1,1},{1,1},{1,1}};
        float[] got=Zero426JointSampleActorScore.headingScore(noise,scale,true);
        float[] only=Zero426JointSampleActorScore.headingScore(
            new float[][]{noise[0],noise[1],noise[2]},
            new float[][]{scale[0],scale[1],scale[2]},false);
        assertArrayEquals(only,got,1e-5f);
    }
}
