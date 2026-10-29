package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** True physics A/B on the original S21 user brain, 16 independent
 * target-agnostic conditions, exact 170 material nodes and live collisions.
 * Directions are LAB probes only, NEVER commands in the real game.
 * Green Android runner means values measured, NOT approval to ship.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero412TrueS21MotorContextPhysicalABAndroidTest {
    static final float DT=1f/60f;
    static JSONObject savedFree(JSONObject user)throws Exception{
        JSONObject other=new JSONObject(user.toString());
        other.put("training_room_1_scene_v379",
            user.getString("training_room_1_free_scene_v379"));
        return other;
    }
    static JSONObject stopped(JSONObject user)throws Exception{
        JSONObject original=new JSONObject(user.toString());
        JSONObject scene=new JSONObject(user.getString(
            "training_room_1_free_scene_v379"));
        JSONObject physics=scene.getJSONObject("physical");
        JSONObject b=physics.getJSONObject("body");
        b.put("cvx",0d);b.put("cvy",0d);
        for(JSONObject part:new JSONObject[]{
                b,physics.getJSONObject("surface"),
                physics.getJSONObject("symbiote")}){
            for(String name:new String[]{"vx","vy"}){
                JSONArray values=part.getJSONArray(name);
                for(int i=0;i<values.length();i++)values.put(i,0d);
            }
        }
        original.put("training_room_1_scene_v379",scene.toString());
        return original;
    }
    static float signedProgress(JSONObject user,boolean wall,int heading,
            boolean contextual)throws Exception{
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            user.getString("training_room_1_scene_v379"));
        assertNotNull(scene);assertNotNull(scene.matter);
        SymbioteMatterV1 gel=scene.matter;
        gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(-.04f);
        ArrayList<UnknownWorld.Entity> world=wall?
            scene.objects:new ArrayList<UnknownWorld.Entity>();
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user);
        if(contextual)cortex.enablePhysicalContextV412();
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(cortex);
        float startX=scene.body.cx,startY=scene.body.cy;
        float ux=heading==0?1f:heading==2?-1f:0f;
        float uy=heading==1?1f:heading==3?-1f:0f;
        for(int decision=0;decision<5;decision++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(412,
                    decision,cortex.revision(),ux,uy,.5f,.5f,.4f);
            control.begin(intent,scene.body,false,true,false,true,true);
            for(int tick=0;tick<24;tick++){
                float[] muscles=control.actuate(scene.body,scene.skin,DT);
                float[] skinX=scene.skin.x.clone(),skinY=scene.skin.y.clone();
                float[] bodyX=scene.body.x.clone(),bodyY=scene.body.y.clone();
                float previous=gel.work;
                gel.step(scene.body,scene.skin,muscles,DT);
                float[] gelX=scene.skin.x.clone(),gelY=scene.skin.y.clone();
                Zero387TactileFrame.begin(scene.skin);
                if(wall)TrainingRoomV379.constrain(scene.body,scene.skin,
                    world,skinX,skinY,bodyX,bodyY);
                SymbioteWorldStepV171.interior(gel,scene.body,scene.skin,null,
                    world,DT,gelX,gelY);
                control.observe(scene.body,DT,Math.max(0f,gel.work-previous),
                    gel.lastMuscleReactionImpulse,false,false);
                assertEquals("No invalid gel with corrected sensory inputs",
                    0,gel.invalid);
            }
            control.finish();
        }
        float signed=(scene.body.cx-startX)*ux+(scene.body.cy-startY)*uy;
        assertTrue(Float.isFinite(signed));
        return signed;
    }
    @Test(timeout=900000)
    public void originalS21PhysicalMatrixShowsWhetherNewContextActuallySteersBetter()
            throws Exception{
        JSONObject saved=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject free=savedFree(saved),rest=stopped(saved);
        JSONObject[] scenes={saved,saved,free,rest};
        boolean[] wall={false,true,false,false};
        int wins=0,losses=0;
        float minGain=Float.POSITIVE_INFINITY,meanGain=0f,
            eastMinimum=Float.POSITIVE_INFINITY;
        for(int posture=0;posture<scenes.length;posture++){
            for(int direction=0;direction<4;direction++){
                float legacy=signedProgress(scenes[posture],wall[posture],
                    direction,false);
                float corrected=signedProgress(scenes[posture],wall[posture],
                    direction,true);
                float gain=corrected-legacy;
                meanGain+=gain/16f;
                minGain=Math.min(gain,minGain);
                if(gain>.07f)wins++;
                if(gain<-.07f)losses++;
                if(direction==0)eastMinimum=Math.min(eastMinimum,corrected);
                Log.i("ZERO412_MOTOR_AB",String.format(Locale.US,
                    "ACTUAL_USER_S21_ORIGINAL_BODY posture=%d wall=%s heading=%d"+
                    " legacyProgress=%.6f correctedProgress=%.6f gain=%.6f",
                    posture,wall[posture],direction,legacy,corrected,gain));
            }
        }
        Log.i("ZERO412_MOTOR_AB",String.format(Locale.US,
            "ZERO412_REAL_S21_16_MATERIAL_TRAJECTORIES_MEASURED"+
            " wins=%d losses=%d meanGain=%.6f minGain=%.6f eastWorst=%.6f",
            wins,losses,meanGain,minGain,eastMinimum));
        // DO NOT add a cherry-picked acceptance threshold here. The
        // original 10,000 unscripted GRU3 run and the 16 signed results
        // decide if the material/sense patch can be released.
    }
}
