package com.overmind.zero;

import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.lang.reflect.Method;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * READ-ONLY FORENSIC: exact REAL 2026-10-25 Galaxy S21 V388 saved GRU3 and
 * physical training scene. Identical cortex, inner fluid, membrane, velocities,
 * 2-second heading: with physical room wall vs same scene with obstacles
 * removed. No GRU weights, checkpoints, live app, rewards, or OTA are changed.
 * Not a scripted behavior/brain test. Only actuator physics controllability.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero389ActualS21CausalMotorAndroidTest {
    private static void report(String message){
        android.util.Log.i("ZERO389_CAUSAL",message);
        Bundle b=new Bundle();
        b.putString("stream",message);
        InstrumentationRegistry.getInstrumentation().sendStatus(0,b);
    }
    private static String trainedVsVirgin(JSONObject user,boolean learned,
                                           boolean withWall,int dir)throws Exception{
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            user.getString("training_room_1_scene_v379"));
        assertNotNull(scene);assertNotNull(scene.matter);
        MotorBrain.Body body=scene.body;Morphology.Surface skin=scene.skin;
        SymbioteMatterV1 gel=scene.matter;
        java.util.ArrayList<UnknownWorld.Entity> world=withWall?
            scene.objects:new java.util.ArrayList<UnknownWorld.Entity>();
        Zero3LearningFoundation.AdaptiveCortex cortex;
        if(learned){
            Zero3OriginalBrainV356.Readout saved=
                Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
            java.lang.reflect.Field field=saved.getClass().getDeclaredField("cortex");
            field.setAccessible(true);
            // STRICT LIVE-STATE EVALUATION: the older forkForEvaluation()
            // copies the 512 W but silently clears 64 recurrent local states.
            // Here inspectSaved() already returned an isolated full CPU
            // restore; use THAT actual saved trained cortex directly, with
            // state, outputs and revision intact. Each trial parses a NEW
            // copy so no user's source can be modified.
            cortex=(Zero3LearningFoundation.AdaptiveCortex)field.get(saved);
        }else cortex=new Zero3LearningFoundation.AdaptiveCortex();
        Zero3LearningFoundation.Coordinator owner=
            new Zero3LearningFoundation.Coordinator(cortex);
        gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(-.04f);
        final float dt=1f/60f;
        float originalX=body.cx,originalY=body.cy,originalVx=body.cvx;
        float heading=dir==0?1f:dir==2?-1f:0f;
        float y=dir==1?1f:dir==3?-1f:0f;
        float totalImpulse=0f,absoluteCommand=0f;
        int framesWithWallTouch=0,externals=0;
        for(int k=0;k<5;k++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    7,k,cortex.revision(),heading,y,.5f,.5f,.4f);
            owner.begin(intent,body,false,true,false,true,true);
            for(int step=0;step<24;step++){
                float[] motor=owner.actuate(body,skin,dt);
                for(float v:motor)absoluteCommand+=Math.abs(v);
                float[] preSkinX=skin.x.clone(),preSkinY=skin.y.clone();
                float[] preBodyX=body.x.clone(),preBodyY=body.y.clone();
                float prevWork=gel.work;
                gel.step(body,skin,motor,dt);
                float[] preWorldX=skin.x.clone(),preWorldY=skin.y.clone();
                Zero387TactileFrame.begin(skin);
                if(withWall)
                    TrainingRoomV379.constrain(body,skin,world,
                        preSkinX,preSkinY,preBodyX,preBodyY);
                SymbioteWorldStepV171.interior(gel,body,skin,null,world,dt,
                    preWorldX,preWorldY);
                boolean touched=false;
                for(float touch:skin.touchSense)
                    if(touch>.2f){touched=true;break;}
                if(touched)framesWithWallTouch++;
                totalImpulse+=gel.lastMuscleReactionImpulse;
                owner.observe(body,dt,Math.max(0,gel.work-prevWork),
                    gel.lastMuscleReactionImpulse,false,false);
            }
            owner.finish();
        }
        float dx=body.cx-originalX,dy=body.cy-originalY;
        float forward=dx*heading+dy*y;
        float inertial=forward-(originalVx*heading)*2f;
        return "weight="+(learned?"USER_LEARNED":"NEVER_TRAINED")+
            ",wall="+withWall+",dir="+dir+",comDx="+dx+",comDy="+dy+
            ",along="+forward+",inertiaCorrected="+inertial+
            ",muscleImpulse="+totalImpulse+",absMotorCommands="+absoluteCommand+
            ",wallTouchFrames="+framesWithWallTouch;
    }
    @Test(timeout=720000)
    public void exactlySameBodyLearnedMotorVsVirginMotorWithoutWalls()throws Exception{
        File path=new File(InstrumentationRegistry.getInstrumentation()
            .getTargetContext().getExternalFilesDir(null),"zero385-user-readonly.json");
        JSONObject real=new JSONObject(new String(Files.readAllBytes(path.toPath()),
            StandardCharsets.UTF_8));
        for(boolean wall:new boolean[]{false,true}){
            for(int dir=0;dir<4;dir++){
                report("ZERO389_USER_VS_VIRGIN,"+
                    trainedVsVirgin(real,true,wall,dir));
                report("ZERO389_USER_VS_VIRGIN,"+
                    trainedVsVirgin(real,false,wall,dir));
            }
        }
        report("ZERO389_USER_VIRGIN_CAUSAL_PROFILE_COMPLETE=PASS_MEASUREMENT_ONLY");
    }

    @Test(timeout=720000)
    public void physicallyIdenticalV388RealS21ControlledHeadingVsWorldWall()
        throws Exception {
        File path=new File(InstrumentationRegistry.getInstrumentation()
            .getTargetContext().getExternalFilesDir(null),"zero385-user-readonly.json");
        assertTrue("Real original Galaxy S21 report must be mounted",path.isFile());
        JSONObject real=new JSONObject(new String(Files.readAllBytes(path.toPath()),
            StandardCharsets.UTF_8));
        Zero3OriginalBrainV356.Readout read=
            Zero3OriginalBrainV356.inspectSaved(real.getString("brain"));
        assertEquals("33,312 authentic user trained decisions; no virgin controller",
            33312L,read.decisions);
        assertEquals(19452,read.motorUpdates);
        String wall=real.getString("training_room_1_scene_v379");
        JSONObject clear=new JSONObject(wall);
        // Causal intervention ON WORLD ONLY. The exact 170 particle saved
        // coordinates and 170 saved velocities remain byte-identical.
        clear.getJSONObject("physical").put("objects",new JSONArray());
        real.put("training_no_wall_same_body_v389",clear.toString());
        assertEquals(new JSONObject(wall).getJSONObject("physical")
            .getJSONObject("body").getDouble("cx"),
            clear.getJSONObject("physical").getJSONObject("body").getDouble("cx"),0);
        assertTrue("Original physical wall fixture present",
            new JSONObject(wall).getJSONObject("physical")
            .getJSONArray("objects").length()>90);
        Method trial=Zero385MotorAuthorityABAndroidTest.class
            .getDeclaredMethod("trial",JSONObject.class,String.class,int.class,int.class);
        trial.setAccessible(true);
        Class<?> resultClass=Class.forName("com.overmind.zero.Zero385MotorAuthorityABAndroidTest$Result");
        Method csv=resultClass.getDeclaredMethod("csv");
        csv.setAccessible(true);
        report("ZERO389_REAL_S21_V388_PROFILE=33312:CORTEX_UPDATES=19452;"+
            "same real membrane/fluid/velocities, only obstacle objects removed");
        for(int mode:new int[]{1,3}){
            for(int heading=0;heading<(mode==3?1:4);heading++){
                for(String sceneKey:new String[]{"training_room_1_scene_v379",
                                                "training_no_wall_same_body_v389"}){
                    Object res=trial.invoke(null,real,sceneKey,mode,heading);
                    String line=(String)csv.invoke(res);
                    report("ZERO389_TRUE_MOTOR_CAUSAL,"+
                        (sceneKey.contains("no_wall")?"NO_WALL":"REAL_WALL")+
                        ",mode="+mode+",heading="+heading+","+line);
                }
            }
        }
        report("ZERO389_COMPLETED_FULL_CAUSAL_MATCHED_REAL_V388_USER_BODY=PASS_MEASUREMENT_ONLY");
    }
}
