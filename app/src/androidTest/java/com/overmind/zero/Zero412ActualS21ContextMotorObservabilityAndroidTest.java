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
public final class Zero412ActualS21ContextMotorObservabilityAndroidTest {
    static final float DT=1f/60f;
    static float[] commands(JSONObject original,float vx,float vy,int heading)
            throws Exception{
        return commands(original,vx,vy,heading,0,true);
    }
    static float[] commands(JSONObject original,float vx,float vy,int heading,
            int materialContactMode,boolean activatePhysicalContext)throws Exception{
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
        // Exact 33,312 real past GRU3 decisions and 512 original trained
        // weights. Only the addition of new measured input changes.
        if(activatePhysicalContext)cortex.enablePhysicalContextV412();
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
    public void realS21OriginalCortexNowFeelsUniformPhysicalMomentum()
            throws Exception{
        JSONObject original=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout saved=
            Zero3OriginalBrainV356.inspectSaved(original.getString("brain"));
        assertEquals(33312L,saved.decisions);
        assertEquals(19452L,saved.motorUpdates);
        for(int heading=0;heading<4;heading++){
            float[] originalRest=commands(original,0f,0f,heading,0,false);
            float[] legacyDrift=commands(original,65f,0f,heading,0,false);
            for(int k=0;k<64;k++)assertEquals(
                "V411 ROOT-CAUSE baseline is still reproducible",
                originalRest[k],legacyDrift[k],1e-6f);
            float[] rest=commands(original,0f,0f,heading,0,true);
            float[] pushedEast=commands(original,65f,0f,heading,0,true);
            float[] pushedWest=commands(original,-65f,0f,heading,0,true);
            float[] pushedNorth=commands(original,0f,-65f,heading,0,true);
            double max=0,nonzero=0;
            for(int k=0;k<64;k++){
                if(k%4==2){
                    assertEquals("Externally V319-owned GRIP unaffected by V412",
                        rest[k],pushedEast[k],1e-6f);
                    continue;
                }
                max=Math.max(max,Math.abs(rest[k]-pushedEast[k]));
                max=Math.max(max,Math.abs(rest[k]-pushedWest[k]));
                max=Math.max(max,Math.abs(rest[k]-pushedNorth[k]));
                nonzero+=Math.abs(rest[k]);
            }
            assertTrue("Original trained 512-weight cortex must now actually"+
                " distinguish physically different 65u/s inertial drift",
                max>.00001);
            assertTrue(nonzero>.01);
            Log.i("ZERO412_PHYSICAL",
                "TRUE_S21_MOTOR_VELOCITY_CONTEXT_REPAIRED heading="+heading+
                ",maxPhysicalCommandDelta="+max);
        }
        Log.i("ZERO412_PHYSICAL",
            "ZERO412_REAL_S21_V411_INERTIAL_ALIAS=REPAIRED");
    }

    @Test(timeout=900000)
    public void actualS21OriginalCortexDistinguishesWallFromFoothold()
            throws Exception{
        JSONObject original=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        for(int heading=0;heading<4;heading++){
            float[] baselineGrip=commands(original,0f,0f,heading,1,false);
            float[] baselineWall=commands(original,0f,0f,heading,2,false);
            for(int k=0;k<64;k++)
                assertEquals("V411 original contact alias reproduced",
                    baselineGrip[k],baselineWall[k],1e-6f);
            float[] foothold=commands(original,0f,0f,heading,1,true);
            float[] wall=commands(original,0f,0f,heading,2,true);
            float max=0f;
            for(int k=0;k<64;k++){
                if(k%4==2){
                    assertEquals("V319 grip solver stays external to V412",
                        foothold[k],wall[k],1e-6f);
                    continue;
                }
                max=Math.max(max,Math.abs(foothold[k]-wall[k]));
            }
            assertTrue("Original 33k motor weights must now actually distinguish"+
                " foothold from a physically blocked wall",
                max>.00001f);
            Log.i("ZERO412_PHYSICAL",
                "TRUE_S21_WALL_GRIP_CONTEXT_REPAIRED heading="+heading+
                ",maxPhysicalCommandDelta="+max);
        }
        Log.i("ZERO412_PHYSICAL",
            "ZERO412_REAL_S21_V411_GROUND_WALL_ALIAS=REPAIRED");
    }

    @Test(timeout=900000)
    public void addingSensesDoesNotChange512SavedSynapsesOrMakePassiveThrust()
            throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user);
        float[] before=cortex.weights();
        long updates=cortex.updates();
        cortex.enablePhysicalContextV412();
        assertArrayEquals("All ORIGINAL trained efferent synapses untouched",
            before,cortex.weights(),0f);
        assertEquals(updates,cortex.updates());
        assertEquals(512,before.length);
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            user.getString("training_room_1_scene_v379"));
        Zero3LearningFoundation.MotorIntent none=
            new Zero3LearningFoundation.MotorIntent(
                11,5,cortex.revision(),0f,0f,0f,.5f,.4f);
        cortex.begin(false,none,true);
        float[] muscle=cortex.command(none,scene.body,scene.skin,DT);
        for(int ch=0;ch<64;ch++)if(ch%4!=2)
            assertEquals("Zero voluntary effort must NEVER gain thrust from"+
                " observing momentum or contact",0f,muscle[ch],0f);
        cortex.discard();
        assertArrayEquals(before,cortex.weights(),0f);
        Log.i("ZERO412_PHYSICAL","ZERO412_ORIGINAL_512_WEIGHT_SAVE_COMPATIBILITY=PASS");
    }
}
