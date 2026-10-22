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
    @Test public void realWallDoesConstrainAll64InternalLiquidMasses(){
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
        assertFalse("V380: no interior liquid mass inside a solid wall",
            Math.abs(gel.x[0]-wall.x)<wall.radius &&
            Math.abs(gel.y[0]-wall.y)<wall.radius);
        assertTrue("ZERO379 wall deflection is treated as EXTERNAL disturbance",
            MotorResponseJournalV172.externalMotorInterferenceV363(.09f,0f,0f,false));
    }
    @Test public void learnedPhysicalRedChangesSelectionAwayFromKnownHarm(){
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
        assertEquals("V380: once real red harm has been learned, select the safer available option",
            1,cognition.select(sight,proposals));
    }
    @Test public void learnedBlueMakesPhysicallyRecognizedLightRelevant(){
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
        assertEquals("V380: real blue intake makes physically better offered direction selectable",
            1,cognition.select(sight,proposals));
    }
    @Test public void censoredWallBlockCausesStallWithoutFalseMotorTraining(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        float[] pre=new float[194],right={1f,0f,.5f,.5f,.3f};
        for(int i=0;i<3;i++){
            m.begin(pre,right,false);
            // Contact / external wall correction remains censored for
            // motor response model, yet witnesses ZERO's actual impasse.
            m.complete(0f,0f,.3f,false,false,true);
        }
        assertTrue("V380 measured physically blocked repeats open exploration",
            m.repeatedRealMotorStall());
        assertEquals("No false training on collisions",0,m.verifiedDirections());
        assertEquals("No fake inertia training",0,m.verifiedInertialResponseV377());
    }
    @Test public void unlearnedColorsStillHaveNoInnatePreference(){
        Zero3CognitionV353 c=new Zero3CognitionV353();
        float[] retina=new float[194];retina[20]=.56f;retina[28]=.56f;
        float[][] a={{1f,0f,.5f,.5f,.3f},{-1f,0f,.5f,.5f,.3f}};
        assertEquals("Colors have no built-in reward sign",0,c.select(retina,a));
    }
    @Test public void physicalWallUsesSweptCollisionNoTeleportation(){
        MotorBrain.Body b=new MotorBrain.Body();Morphology.Surface s=new Morphology.Surface(b);
        ArrayList<UnknownWorld.Entity> wall=new ArrayList<>();
        wall.add(TrainingRoomV379.wall(120f,0f));
        float[] oldSX=s.x.clone(),oldSY=s.y.clone();
        float[] oldBX=b.x.clone(),oldBY=b.y.clone();
        s.x[0]=190f;s.y[0]=0f;s.vx[0]=9000f;
        oldSX[0]=50f;oldSY[0]=0f;
        TrainingRoomV379.constrain(b,s,wall,oldSX,oldSY,oldBX,oldBY);
        assertTrue("V380 swept skin cannot cross a complete wall tile",
            s.x[0]<=108.05f);
        assertTrue("Physical velocity is not injected",s.vx[0]<=0f);
    }
    private static float[] oneRedAtEast(float brightness){
        float[] sight=new float[194];
        sight[20+8]=brightness;
        return sight;
    }
}