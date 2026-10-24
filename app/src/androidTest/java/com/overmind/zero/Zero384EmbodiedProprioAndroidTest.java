package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V384 regression: no authored avoidance, physically learned impasse only,
 * separate desired extension vs achieved radial geometry, heldout gating,
 * lossless optional persistence, and absence of false clean-motor credit.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero384EmbodiedProprioAndroidTest {
    private static float[] all(float v){
        float[] values=new float[8];
        java.util.Arrays.fill(values,v);
        return values;
    }
    private static float[] westTouch(){
        float[] t=all(0);t[4]=1f;return t;
    }
    private static void train(Zero384BodyAffordance model,int count,boolean blocked){
        float[] touch=westTouch(),radius=all(.30f),command=all(.36f);
        for(int i=0;i<count;i++){
            model.measured(touch,radius,command);
            model.begin(-1,0);
            // Command extension remains substantial even if material did not move.
            model.measured(touch,radius,command);
            model.complete(blocked?0:-2,0,.4f,3f,true,i%5==0);
        }
    }
    @Test public void wallImpassIsLearnedFromActualRepeatedActionConsequences(){
        Zero384BodyAffordance model=new Zero384BodyAffordance();
        model.measured(westTouch(),all(.3f),all(1f));
        assertEquals("Commanded extension is not evidence of a blocked body",
            0f,model.learnedCost(-1,0),0f);
        assertFalse(model.verifiedImpass());
        train(model,25,true);
        assertTrue("Repeated real obstacle evidence must become usable",
            model.verifiedImpass());
        assertTrue(model.confirmedFailures()>0);
        assertTrue(model.learnedCost(-1,0)<-.20f);
        assertEquals("No forced opposite heading/target score",
            0f,model.learnedCost(1,0),0f);
        assertEquals("No lateral wall rule",0f,model.learnedCost(0,1),0f);
        assertEquals(25L,model.observedDecisions());
    }
    @Test public void successAndOpenWorldMustNotAcquireFalseObstaclePenalty(){
        Zero384BodyAffordance successful=new Zero384BodyAffordance();
        train(successful,35,false);
        assertFalse(successful.verifiedImpass());
        assertEquals(0f,successful.learnedCost(-1,0),0f);
        Zero384BodyAffordance noWall=new Zero384BodyAffordance();
        float[] clear=all(0),radius=all(.3f),effort=all(.8f);
        for(int i=0;i<30;i++){
            noWall.measured(clear,radius,effort);
            noWall.begin(-1,0);
            noWall.measured(clear,radius,effort);
            noWall.complete(0,0,.4f,3f,false,i%5==0);
        }
        assertFalse(noWall.verifiedImpass());
        assertEquals(0f,noWall.learnedCost(-1,0),0f);
    }
    @Test public void heldoutAndOptionalSaveRestoreAreRequiredAndCorruptionSafe(){
        Zero384BodyAffordance model=new Zero384BodyAffordance();
        float[] touch=westTouch(),radius=all(.3f),command=all(.5f);
        for(int i=0;i<19;i++){
            model.measured(touch,radius,command);
            model.begin(-1,0);
            model.measured(touch,radius,command);
            model.complete(0,0,.4f,2f,true,false);
        }
        assertFalse("Only training without independent evidence is untrusted",
            model.verifiedImpass());
        for(int i=0;i<3;i++){
            model.measured(touch,radius,command);
            model.begin(-1,0);
            model.measured(touch,radius,command);
            model.complete(0,0,.4f,2f,true,true);
        }
        assertTrue(model.verifiedImpass());
        String serialized=model.encode();
        Zero384BodyAffordance resumed=new Zero384BodyAffordance();
        assertTrue(resumed.restore(serialized));
        resumed.measured(touch,radius,command);
        assertEquals(serialized,resumed.encode());
        assertEquals(model.learnedCost(-1,0),resumed.learnedCost(-1,0),.00001f);
        assertFalse(resumed.restore(serialized.substring(0,serialized.length()-7)));
        assertEquals("Corrupt auxiliary must not partially destroy valid learnt evidence",
            serialized,resumed.encode());
        Zero384BodyAffordance oldProfile=new Zero384BodyAffordance();
        assertTrue("V383 profile starts neutral, original 194 sensors unchanged",
            oldProfile.restore(""));
        assertFalse(oldProfile.verifiedImpass());
    }
    @Test public void actualSkinGeometryAndCommandsRemainDistinct(){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        Zero384BodyAffordance model=new Zero384BodyAffordance();
        for(int i=0;i<Morphology.POINTS;i++){
            if(skin.x[i]<body.cx-10f)skin.touchSense[i]=1f;
            skin.extension[i]=Morphology.MAX_REACH;
        }
        model.perceive(body,skin); // physical shape, solid-contact only
        assertEquals("No innate reflex from a wall touch",
            0f,model.learnedCost(-1,0),0f);
        assertFalse(model.verifiedImpass());
    }
}
