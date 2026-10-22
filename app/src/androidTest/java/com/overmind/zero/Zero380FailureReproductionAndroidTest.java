package com.overmind.zero;

import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static org.junit.Assert.*;

/** Demonstrates precisely the broken physical and neural conditions observed
 * in ZERO379. No weights, goals, actions, or persisted histories are altered. */
@RunWith(AndroidJUnit4.class)
public final class Zero380FailureReproductionAndroidTest {
    @Test public void realWallDoesNotConstrain64InternalLiquidMasses(){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(b,skin);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        UnknownWorld.Entity wall=TrainingRoomV379.wall(gel.x[0],gel.y[0]);
        objects.add(wall);
        assertTrue(Math.abs(gel.x[0]-wall.x)<wall.radius);
        assertTrue(Math.abs(gel.y[0]-wall.y)<wall.radius);
        TrainingRoomV379.constrain(b,skin,objects);
        SymbioteWorldStepV171.interior(gel,b,skin,null,objects,1f/60f);
        assertTrue("ZERO379 BUG: physical tile leaves liquid inside solid wall",
            Math.abs(gel.x[0]-wall.x)<wall.radius &&
            Math.abs(gel.y[0]-wall.y)<wall.radius);
        assertTrue("ZERO379 wall deflection is treated as EXTERNAL disturbance",
            MotorResponseJournalV172.externalMotorInterferenceV363(.09f,0f,0f,false));
    }
    @Test public void learnedPhysicalRedIsIgnoredAt100UnitsWhenCandidateIsRight(){
        Zero3CognitionV353 cognition=new Zero3CognitionV353();
        float[] redContact=new float[24];redContact[8]=1f;
        cognition.genuineContactV369(redContact,-7f,false);
        cognition.genuineContactV369(redContact,-7f,false);
        assertTrue(cognition.contactValenceScoreV369(oneRedAtEast(0.56f),1,0)<0f);
        float[] sight=oneRedAtEast(.56f);
        float[][] proposals={
            {1f,0f,.5f,.5f,.3f}, // current actor walks towards red
            {0f,1f,.5f,.5f,.3f}  // equally attainable orthogonal escape
        };
        assertEquals("ZERO379 BUG: 0.35/8 weakens red enough to trigger 0.025 dead zone",
            0,cognition.select(sight,proposals));
    }
    @Test public void learnedBlueAt100UnitsDoesNotSteerOrthogonalProposal(){
        Zero3CognitionV353 cognition=new Zero3CognitionV353();
        float[] blueContact=new float[24];blueContact[0]=1f;
        cognition.genuineContactV369(blueContact,22f,false);
        cognition.genuineContactV369(blueContact,22f,false);
        float[] sight=new float[194];sight[20]=.56f;
        assertTrue(cognition.contactValenceScoreV369(sight,1,0)>0f);
        float[][] proposals={
            {0f,1f,.5f,.5f,.3f}, // actor's original irrelevant direction
            {1f,0f,.5f,.5f,.3f}  // toward learned blue
        };
        assertEquals("ZERO379 BUG: 0.025 deadzone prevents observed-blue correction",
            0,cognition.select(sight,proposals));
    }
    private static float[] oneRedAtEast(float brightness){
        float[] sight=new float[194];
        sight[20+8]=brightness;
        return sight;
    }
}