package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Exact latent-score mathematics; NOT an autonomous behavior pass. */
@RunWith(AndroidJUnit4.class)
public final class Zero426JointSampleActorScoreAndroidTest {
    @Test public void actualSixUniformScoreIsOddAndCentered(){
        assertEquals(0f,Zero426JointSampleActorScore.latentScore(0f,1f),0f);
        float p=Zero426JointSampleActorScore.latentScore(.08f,1f);
        assertTrue(p>0f&&p<=3f);
        assertEquals(-p,Zero426JointSampleActorScore.latentScore(-.08f,1f),1e-5f);
    }
    @Test public void actualNoiseScaleTransformsScoreWithoutFakeGaussianSigma(){
        float a=Zero426JointSampleActorScore.latentScore(.02f,1f);
        float b=Zero426JointSampleActorScore.latentScore(.04f,2f);
        assertEquals(a*.5f,b,1e-4f);
    }
    @Test public void allFreshAlternativesNotOnlyWinnerContribute(){
        float[][] n={{.02f,-.02f},{-.01f,.03f},{.03f,-.01f}};
        float[][] s={{1f,1f},{1f,1f},{1f,1f}};
        float[] score=Zero426JointSampleActorScore.headingScore(n,s,false);
        float sx=0f,sy=0f;
        for(int k=0;k<n.length;k++){
            sx+=Zero426JointSampleActorScore.latentScore(n[k][0],1f);
            sy+=Zero426JointSampleActorScore.latentScore(n[k][1],1f);
        }
        assertEquals(Math.max(-3f,Math.min(3f,sx)),score[0],1e-5f);
        assertEquals(Math.max(-3f,Math.min(3f,sy)),score[1],1e-5f);
    }
    @Test public void independentUniformImpassHeadingsCannotCreateActorGradient(){
        float[][] n={{0f,0f},{0f,0f},{0f,0f},{.1f,.1f},{.1f,.1f}};
        float[][] s={{1f,1f},{1f,1f},{1f,1f},{1f,1f},{1f,1f}};
        float[] score=Zero426JointSampleActorScore.headingScore(n,s,true);
        assertEquals(0f,score[0],0f);assertEquals(0f,score[1],0f);
        assertTrue(Zero426JointSampleActorScore.headingScore(n,s,false)[0]>0f);
    }
    @Test public void freshlySampledCandidateZeroContributesCurrentActorGradient(){
        // choose() samples lastNoise freshly BEFORE k=0 uses it.
        float[][] n={{.08f,-.06f},{0f,0f},{0f,0f}};
        float[][] scales={{1f,1f},{1f,1f},{1f,1f}};
        float[] score=Zero426JointSampleActorScore.headingScore(n,scales,false);
        assertEquals(Zero426JointSampleActorScore.latentScore(.08f,1f),score[0],1e-5f);
        assertEquals(Zero426JointSampleActorScore.latentScore(-.06f,1f),score[1],1e-5f);
        n[0][0]=-.08f;n[0][1]=.06f;
        float[] opposite=Zero426JointSampleActorScore.headingScore(n,scales,false);
        assertEquals(-score[0],opposite[0],1e-5f);
        assertEquals(-score[1],opposite[1],1e-5f);
    }
    @Test public void learnedHarmSevenChoicesCreditAllSevenFreshDraws(){
        float[][] n={{.20f,-.20f},{.01f,-.02f},{-.01f,.03f},
            {.02f,.01f},{-.03f,.01f},{.02f,-.02f},{.01f,.02f}};
        float[][] s={{1f,1f},{1f,1f},{1f,1f},{1.75f,1.75f},
            {1.75f,1.75f},{2.5f,2.5f},{2.5f,2.5f}};
        float sx=0f,sy=0f;
        for(int k=0;k<n.length;k++){
            sx+=Zero426JointSampleActorScore.latentScore(n[k][0],s[k][0]);
            sy+=Zero426JointSampleActorScore.latentScore(n[k][1],s[k][1]);
        }
        float[] score=Zero426JointSampleActorScore.headingScore(n,s,false);
        assertEquals(Math.max(-3f,Math.min(3f,sx)),score[0],1e-5f);
        assertEquals(Math.max(-3f,Math.min(3f,sy)),score[1],1e-5f);
    }
    @Test public void singleFreshCandidateHasCurrentHeadingScore(){
        float[][] n={{.07f,-.09f}},s={{1f,1f}};
        float[] score=Zero426JointSampleActorScore.headingScore(n,s,false);
        assertEquals(Zero426JointSampleActorScore.latentScore(.07f,1f),score[0],1e-5f);
        assertEquals(Zero426JointSampleActorScore.latentScore(-.09f,1f),score[1],1e-5f);
    }
    @Test public void firstOriginalGRUChoiceAndRngStayUnchanged(){
        Zero3RecurrentBrain base=new Zero3RecurrentBrain(194,true);
        Zero3RecurrentBrain trial=new Zero3RecurrentBrain(194,true);
        trial.enableJointSampleActorScoreLabV426();
        assertTrue(trial.jointSampleActorScoreEnabledV426());
        float[] sensed=new float[194];sensed[193]=1f;
        Zero3LearningFoundation.MotorIntent a=base.choose(sensed,0,0,0);
        Zero3LearningFoundation.MotorIntent b=trial.choose(sensed,0,0,0);
        assertEquals(a.x,b.x,0f);assertEquals(a.y,b.y,0f);
        assertEquals(a.effort,b.effort,0f);assertEquals(a.adhesion,b.adhesion,0f);
        assertEquals(a.duration,b.duration,0f);
        base.abort();trial.abort();
    }
}
