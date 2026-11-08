package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class Zero511VerifiedPhysicalAndNegativeRiskAndroidTest {
    private static Zero384BodyAffordance realSavedS21(){
        Zero384BodyAffordance b=new Zero384BodyAffordance();
        assertTrue(b.restore(Zero510RealS21EmbodiedDecisionRegressionAndroidTest.REAL_USER_BODY_MEMORY));
        b.measured(new float[]{0,0,0,.6842105f,1f,.45454545f,0,0},
            new float[8],new float[8]);
        assertTrue(b.verifiedImpass());
        return b;
    }
    @Test public void realS21BlockedBluePathCanSelectAlreadySampledSideStep(){
        Zero384BodyAffordance b=realSavedS21();
        float[] cost={b.learnedCost(-.7071068f,-.7071068f),
            b.learnedCost(1f,0f),b.learnedCost(0f,-1f)};
        assertTrue(cost[0]<-.15f);
        Zero511FeasibleChoiceGate.Audit audit=new Zero511FeasibleChoiceGate.Audit();
        int chosen=Zero511FeasibleChoiceGate.choose(true,cost,
            new float[]{0,0,0},0,audit);
        assertEquals(1,chosen);
        assertEquals(1,audit.changed);
        assertTrue(audit.reliefOffered>0);
        assertEquals("No untested motor success fabricated",0,
            Zero511FeasibleChoiceGate.choose(false,cost,new float[]{0,0,0},0,null));
    }
    @Test public void formerlyRejectedPositiveOpticalGoalLossIsNotPhysicalHarm(){
        Zero384BodyAffordance b=realSavedS21();
        float[] cost={b.learnedCost(-.7071068f,-.7071068f),
            b.learnedCost(1f,0f)};
        Zero3ContactValenceV369 valence=new Zero3ContactValenceV369();
        valence.materialContact(0,7f);
        valence.materialContact(0,7f);
        float[] retina=new float[194];
        retina[20+0*8+5]=1f; // positive light to SW, physically verified wall
        float positiveToward=valence.utility(retina,-.7071068f,-.7071068f);
        float positiveAway=valence.utility(retina,1f,0f);
        assertTrue("Old V510 double optic veto would wrongly refuse this feasible action",
            positiveAway<positiveToward);
        float[] risk={valence.negativeApproachRiskV511(
                retina,-.7071068f,-.7071068f),
            valence.negativeApproachRiskV511(retina,1f,0f)};
        assertEquals(0f,risk[0],.0001f);assertEquals(0f,risk[1],.0001f);
        assertEquals(1,Zero511FeasibleChoiceGate.choose(true,cost,risk,0,null));
    }
    @Test public void realLearnedHarmStillVetoesUnsafeAlternative(){
        Zero384BodyAffordance b=realSavedS21();
        float[] cost={b.learnedCost(-.7071068f,-.7071068f),
            b.learnedCost(1f,0f)};
        Zero3ContactValenceV369 valence=new Zero3ContactValenceV369();
        valence.materialContact(1,-7f);
        valence.materialContact(1,-7f);
        float[] retina=new float[194];retina[20+1*8+0]=1f;
        float oldRisk=valence.negativeApproachRiskV511(
            retina,-.7071068f,-.7071068f);
        float newRisk=valence.negativeApproachRiskV511(
            retina,1f,0f);
        assertTrue(newRisk>oldRisk+.012f);
        assertEquals("Never swap learned wall harm for learned physical red harm",0,
            Zero511FeasibleChoiceGate.choose(true,cost,
                new float[]{oldRisk,newRisk},0,null));
    }
}
