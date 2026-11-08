package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO510: negative-only physical constraint from the user's EXACT saved
 * 11145 completed S21 embodied-material memory. Genuine historical train
 * and heldout counts; current tissue had a verified left/back wall contact.
 * Actor candidates are explicit test inputs, not new runtime directions.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero510RealS21EmbodiedDecisionRegressionAndroidTest {
    // Read-only exact zero3_embodied_affordance_v384 saved Oct25 in the
    // original 33312 GRU decision real physical scene.
    static final String REAL_USER_BODY_MEMORY="eb384|1|11145|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,8,7,0|0,0,0,0,0|0,0,2,2,0|0,0,30,18,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,2,2,0|0,0,31,22,0|1,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|10,10,61,35,1|15,12,3,2,1|698,442,107,59,169|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|16,12,3,2,1|734,486,105,68,161|686,494,134,92,133|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|749,453,116,73,77|655,438,140,94,85|12,6,65,45,1|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|291,93,78,27,13|0,0,4,3,0|0,0,3,1,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,0,0,0|0,0,15,4,0|0,0,0,0,0";
    @Test
    public void actualUserWallEvidenceSelectsLessBlockedExistingNeuralCandidate(){
        Zero384BodyAffordance observed=new Zero384BodyAffordance();
        assertTrue("Restore user's EXACT physically learned, heldout-vetted motor wall memory",
            observed.restore(REAL_USER_BODY_MEMORY));
        float[] touch={0f,0f,0f,.6842105f,1f,.45454545f,0f,0f};
        observed.measured(touch,new float[8],new float[8]);
        assertTrue("Known actual S21 wall impasse",observed.verifiedImpass());
        assertTrue("Real S21 adverse SW motion evidence",
            observed.learnedCost(-.70710678f,-.70710678f)<-.16f);
        assertEquals("No opposite-side experience fabricates a success",
            0f,observed.learnedCost(1f,0f),.00001f);
        float[] cost={observed.learnedCost(-.70710678f,-.70710678f),
            observed.learnedCost(1f,0f),
            observed.learnedCost(0f,-1f)};
        float[] neutralOptical={0f,0f,0f};
        float[] neutralValence={0f,0f,0f};
        assertEquals("The same original GRU proposed a less obstructed direction",
            1,Zero510EmbodiedChoiceGate.choose(true,cost,neutralOptical,neutralValence,0));
        assertEquals("No independent heldout proof means NO change",
            0,Zero510EmbodiedChoiceGate.choose(false,cost,neutralOptical,neutralValence,0));
        assertEquals("Already unblocked original choice is never overridden",
            1,Zero510EmbodiedChoiceGate.choose(true,cost,neutralOptical,neutralValence,1));
        assertEquals("Safety never invents an unoffered action",
            0,Zero510EmbodiedChoiceGate.choose(true,new float[]{cost[0]},
                new float[]{0f},new float[]{0f},0));
        assertEquals("Do not escape a proven wall by entering learned optical harm",
            0,Zero510EmbodiedChoiceGate.choose(true,cost,
                new float[]{0f,-.03f,-.03f},neutralValence,0));
        assertEquals("Independent contact value also cannot worsen",
            0,Zero510EmbodiedChoiceGate.choose(true,cost,neutralOptical,
                new float[]{0f,-.02f,-.02f},0));
    }
    @Test
    public void neverManufactureEvidentialSuccessFromOneUnverifiedEncounter(){
        Zero384BodyAffordance body=new Zero384BodyAffordance();
        float[] touch=new float[8];touch[4]=1f;
        body.measured(touch,new float[8],new float[8]);
        for(int i=0;i<12;i++){
            body.begin(-1f,0f);
            body.complete(0f,0f,.4f,5f,true,false);
        }
        assertFalse("No heldout outcomes: repeated failure is still unverified",
            body.verifiedImpass());
    }
}
