package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Experiment on the genuine user 33,312-decision trained GRU3 motor cortex.
 * Does an unchanged neural choice, body contour and local relative tissue
 * velocity produce the same 64 motor commands when its ENTIRE living body
 * is actually drifting quickly versus stationary? Demonstrates observable
 * input aliasing of the unchanged 8-feature × 64-output motor synapses.
 *
 * NOT a prescribed gait and no simulation/reward/motor changes.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero411ActualS21MotorInertiaObservabilityAndroidTest {
    static final float DT=1f/60f;
    static float[] commands(JSONObject original,float vx,float vy,int heading)
            throws Exception{
        return commands(original,vx,vy,heading,0);
    }
    static float[] commands(JSONObject original,float vx,float vy,int heading,
            int materialContactMode)throws Exception{
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            original.getString("training_room_1_scene_v379"));
        assertNotNull("Actual saved original S21 material shape required",scene);
        assertNotNull(scene.matter);
        if(materialContactMode!=0){
            if(materialContactMode!=1&&materialContactMode!=2)
                throw new IllegalArgumentException("V411 contact-mode experiment");
            // Identical 6-node physically saved S21 patch. Exchange ONLY
            // the true sensory cause: held substrate versus wall touch.
            // Existing V3 cortex feature[5] combines them into one number.
            int sector=5;
            for(int i=sector*6;i<sector*6+6;i++){
                scene.skin.gripHold[i]=materialContactMode==1?
                    Morphology.GRIP_HOLD+.2f:0f;
                scene.skin.touchSense[i]=materialContactMode==2?1f:0f;
            }
        }
        float preX=scene.body.cvx,preY=scene.body.cvy;
        scene.body.cvx+=vx;scene.body.cvy+=vy;
        for(int i=0;i<MotorBrain.N;i++){
            scene.body.vx[i]+=vx;scene.body.vy[i]+=vy;
        }
        for(int i=0;i<Morphology.POINTS;i++){
            scene.skin.vx[i]+=vx;scene.skin.vy[i]+=vy;
        }
        for(int i=0;i<scene.matter.vx.length;i++){
            scene.matter.vx[i]+=vx;scene.matter.vy[i]+=vy;
        }
        assertEquals(preX+vx,scene.body.cvx,0f);
        assertEquals(preY+vy,scene.body.cvy,0f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(original);
        float x=heading==0?1f:heading==2?-1f:0f;
        float y=heading==1?1f:heading==3?-1f:0f;
        Zero3LearningFoundation.MotorIntent action=
            new Zero3LearningFoundation.MotorIntent(
                4011,20+heading,cortex.revision(),x,y,.5f,.5f,.4f);
        cortex.begin(false,action,true);
        float[] executed=cortex.command(action,scene.body,scene.skin,DT).clone();
        cortex.discard();
        for(float power:executed)assertTrue(Float.isFinite(power));
        return executed;
    }

    @Test(timeout=900000)
    public void genuineLearnedS21MotorSynapsesCannotDistinguishBalloonDriftFromRest()
            throws Exception {
        JSONObject original=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout snapshot=
            Zero3OriginalBrainV356.inspectSaved(original.getString("brain"));
        assertEquals(33312L,snapshot.decisions);
        assertEquals(19452L,snapshot.motorUpdates);
        for(int heading=0;heading<4;heading++){
            float[] rest=commands(original,0f,0f,heading);
            float[] inertialEast=commands(original,65f,0f,heading);
            float[] inertialWest=commands(original,-65f,0f,heading);
            float[] inertialNorth=commands(original,0f,-65f,heading);
            float maxDiff=0f,activePower=0f;
            for(int i=0;i<rest.length;i++){
                maxDiff=Math.max(maxDiff,Math.abs(rest[i]-inertialEast[i]));
                maxDiff=Math.max(maxDiff,Math.abs(rest[i]-inertialWest[i]));
                maxDiff=Math.max(maxDiff,Math.abs(rest[i]-inertialNorth[i]));
                activePower+=Math.abs(rest[i]);
                assertEquals("Local relative information aliases WHOLE-BODY EAST inertia",
                    rest[i],inertialEast[i],1e-6f);
                assertEquals("Local relative information aliases WHOLE-BODY WEST inertia",
                    rest[i],inertialWest[i],1e-6f);
                assertEquals("Local relative information aliases WHOLE-BODY NORTH inertia",
                    rest[i],inertialNorth[i],1e-6f);
            }
            assertTrue("Original 33k trained motor actually commands muscles",
                activePower>0.01f);
            Log.i("ZERO411_OBSERVATION",
                "ACTUAL_S21_MOTOR_INERTIAL_CONTEXT_ALIAS heading="+heading+
                ",all64CommandMaxDiff="+maxDiff+",motorPower="+activePower+
                ",wholeBodySpeedDelta=65.0");
        }
        Log.i("ZERO411_OBSERVATION",
            "ZERO411_33312_GENUINE_S21_MOTOR_INERTIA_UNOBSERVABLE=LAB_CONFIRMED");
    }
    @Test(timeout=900000)
    public void genuineS21MotorCannotTellSubstrateFootholdFromSolidWallContact()
            throws Exception {
        JSONObject original=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        for(int heading=0;heading<4;heading++){
            float[] substrate=commands(original,0f,0f,heading,1);
            float[] wall=commands(original,0f,0f,heading,2);
            assertEquals(64,substrate.length);
            for(int motor=0;motor<64;motor++)
                assertEquals("Original eight-feature V3 motor aliases genuine "+
                    "FOOTHOLD and WALL contact at same membrane sector "+motor,
                    substrate[motor],wall[motor],1e-6f);
            Log.i("ZERO411_OBSERVATION",
                "ACTUAL_S21_CONTACT_HOLD_VS_WALL_ALIAS heading="+heading+
                ",motorOutputs=64,differingPhysicalContactTypes=2");
        }
        Log.i("ZERO411_OBSERVATION",
            "ZERO411_33312_GENUINE_S21_GRIP_WALL_INPUT_ALIAS=LAB_CONFIRMED");
    }

}
