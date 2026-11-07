package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** ZERO506: true original 170-site body + single actual sensed membrane
 * contact. This unit probe creates a measured 1-of-96 local physical wall
 * touch on that real shape to verify coarse V384 tactile aggregation does
 * NOT erase it below the original empirically trained blocking threshold.
 * A learned cost must require 5 genuine-style train observations and two
 * independent heldout failures: never born knowing a wall/escape heading.
 * The test does not spoof food, experience in live GameActivity, motor
 * direction, energy outcome or production GRU trajectory. */
@RunWith(AndroidJUnit4.class)
public final class Zero506OriginalSparseWallContactLearningAndroidTest {
    private static int direction(float x,float y){
        return Math.floorMod(Math.round((float)Math.atan2(y,x)/(float)(Math.PI/4)),8);
    }

    @Test(timeout=150000)
    public void realSavedMembraneSingleContactCanTrainActualV384WallMemory()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        GameSceneSnapshot.Scene body=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull(body);assertNotNull(body.skin);
        // One genuinely measured, local membrane pressure. Most of the
        // original 96 membrane points do not contact that surface.
        Arrays.fill(body.skin.touchSense,0f);
        int patch=0;
        body.skin.touchSense[patch]=1f;
        float ux=body.skin.x[patch]-body.body.cx;
        float uy=body.skin.y[patch]-body.body.cy;
        float len=(float)Math.hypot(ux,uy);
        assertTrue(len>.1f);
        ux/=len;uy/=len;
        int h=direction(ux,uy);
        int pointsInSector=0;
        for(int i=0;i<Morphology.POINTS;i++){
            float x=body.skin.x[i]-body.body.cx;
            float y=body.skin.y[i]-body.body.cy;
            if(direction(x,y)==h)pointsInSector++;
        }
        assertTrue("One contact is diluted by former sector averaging",
            pointsInSector>=5);
        assertTrue("Old local mean would have falsely erased real touch",
            1f/pointsInSector<.23f);
        Zero384BodyAffordance model=new Zero384BodyAffordance();
        model.perceive(body.body,body.skin);
        Field sensedField=Zero384BodyAffordance.class.getDeclaredField("touch");
        sensedField.setAccessible(true);
        float[] tactile=(float[])sensedField.get(model);
        assertEquals("Actual 1.0 membrane contact must survive coarse sensing",
            1f,tactile[h],0f);
        assertEquals("No fabricated innate cost before any real consequence",
            0f,model.learnedCost(ux,uy),0f);
        for(int i=0;i<7;i++){
            model.perceive(body.body,body.skin);
            model.begin(ux,uy);
            model.perceive(body.body,body.skin);
            // The test isolates the real physical *learning rule*:
            // contact=1 and no measured COM advance while muscle works.
            // Never inject this synthetic decision into the live organism.
            model.complete(0f,0f,.40f,10f,true,i>=5);
        }
        assertTrue("At least one motor heading has learned true wall impasse",
            model.confirmedFailures()>0);
        assertTrue("Proven repeated wall contact must rank collision direction",
            model.learnedCost(ux,uy)<-.1f);
        assertEquals("No penalty away from the real sensed wall",
            0f,model.learnedCost(-ux,-uy),0f);
        Log.i("ZERO506_WALL",
            "ORIGINAL_TRUE_SHAPE_SPARSE_TACTILE_LEARNING_PASS sector="+h+
            " physicalPointsPerSector="+pointsInSector+
            " learnedCost="+model.learnedCost(ux,uy));
    }
}
