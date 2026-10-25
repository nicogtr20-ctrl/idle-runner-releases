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
