package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class Zero387TactileContactAndroidTest {
    @Test public void trueWallContactIsNotMemorizedAfterMaterialLeavesTheWall(){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        UnknownWorld.Entity wall=TrainingRoomV379.wall(skin.x[0],skin.y[0]);
        ArrayList<UnknownWorld.Entity> environment=new ArrayList<>();
        environment.add(wall);
        TrainingRoomV379.constrain(body,skin,environment);
        assertEquals("Initial physical collision really occurred",1f,skin.touchSense[0],0f);
        float retainedPulse=skin.touchPulse[0];
        assertTrue(retainedPulse>0f);
        for(int i=0;i<Morphology.POINTS;i++){skin.x[i]+=300f;skin.y[i]+=300f;}
        for(int i=0;i<MotorBrain.N;i++){body.x[i]+=300f;body.y[i]+=300f;}
        body.cx+=300f;body.cy+=300f;
        Zero387TactileFrame.begin(skin);
        TrainingRoomV379.constrain(body,skin,environment);
        assertEquals("No phantom old wall remains in the V384 learning signal",
            0f,skin.touchSense[0],0f);
        assertEquals("Touch pulse decays separately, never erased as history",
            retainedPulse,skin.touchPulse[0],0f);
    }
    @Test public void constantLegalFaceContactRemainsSensedEvenWithoutNewPenetration(){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        UnknownWorld.Entity wall=TrainingRoomV379.wall(50f,0f);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        objects.add(wall);
        skin.x[0]=50f-wall.radius;skin.y[0]=0f;
        Zero387TactileFrame.begin(skin);
        TrainingRoomV379.constrain(body,skin,objects);
        assertEquals("Standing on a valid wall face is still a real contact",
            1f,skin.touchSense[0],0f);
        Zero387TactileFrame.begin(skin);
        TrainingRoomV379.constrain(body,skin,objects);
        assertEquals("No flickering when zero penetration but true touch",
            1f,skin.touchSense[0],0f);
    }
    @Test public void actualInterpenetratingMembraneEdgeIsNotReportedAsNoTouch(){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        for(int i=0;i<Morphology.POINTS;i++){
            skin.x[i]=100f;skin.y[i]=100f;
            skin.vx[i]=skin.vy[i]=0f;
        }
        skin.x[0]=-30f;skin.y[0]=0f;
        skin.x[1]=30f;skin.y[1]=0f;
        UnknownWorld.Entity box=TrainingRoomV379.wall(0f,0f);
        Zero387TactileFrame.begin(skin);
        int fixed=SquareCollisionSolver.repairLinks(skin.x,skin.y,skin.vx,skin.vy,
            Morphology.POINTS,box,1.6f,skin);
        assertTrue("Our actually crossed material segment must be corrected",fixed>0);
        assertTrue("Moving the endpoint onto the supporting solid face is a contact",
            skin.touchSense[0]>.2f||skin.touchSense[1]>.2f);
        assertTrue("Physical tactile pulse is retained",
            skin.touchPulse[0]>.2f||skin.touchPulse[1]>.2f);
    }
}
