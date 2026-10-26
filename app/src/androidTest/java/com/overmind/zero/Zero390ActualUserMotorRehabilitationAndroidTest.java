package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * PURE RESEARCH A/B. The exact user's SAVED V388-trained 33,312 GRU decisions
 * and 19,452 cortical updates. This never touches the running app or user
 * checkpoint. 128 controlled, repeated actual 170-body material experiments
 * of same physical initial pose, before/after trained cortex vs V388 noisy
 * multi-sector credit. Direction input is scheduled LAB ONLY; this is not
 * an autonomous agent/gameplay performance claim.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero390ActualUserMotorRehabilitationAndroidTest {
    static final float DT=1f/60f;
    static JSONObject fixture()throws Exception{
        File path=new File(InstrumentationRegistry.getInstrumentation()
            .getTargetContext().getExternalFilesDir(null),"zero385-user-readonly.json");
        assertTrue("Must use actual user-provided 2026-10-25 trained V388 GRU",path.isFile());
        return new JSONObject(new String(Files.readAllBytes(path.toPath()),
            StandardCharsets.UTF_8));
    }
    static Zero3LearningFoundation.AdaptiveCortex trained(JSONObject user)
        throws Exception{
        Zero3OriginalBrainV356.Readout r=
            Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
        assertEquals(33312L,r.decisions);
        assertEquals(19452,r.motorUpdates);
        java.lang.reflect.Field f=r.getClass().getDeclaredField("cortex");
        f.setAccessible(true);
        return (Zero3LearningFoundation.AdaptiveCortex)f.get(r);
    }
    static float execute(JSONObject user,Zero3LearningFoundation.AdaptiveCortex c,
            boolean v390,int heading,boolean learn,long serial,int segments)
            throws Exception{
        return execute(user,c,v390,heading,learn,serial,segments,false);
    }
    static float execute(JSONObject user,Zero3LearningFoundation.AdaptiveCortex c,
            boolean v390,int heading,boolean learn,long serial,int segments,
            boolean useActualDeliveredProbeV397)throws Exception{
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            user.getString("training_room_1_scene_v379"));
        assertNotNull(scene);assertNotNull(scene.matter);
        SymbioteMatterV1 gel=scene.matter;
        gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(-.04f);
        float startX=scene.body.cx,startY=scene.body.cy;
        float dx=heading==0?1f:heading==2?-1f:0f;
        float dy=heading==1?1f:heading==3?-1f:0f;
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(c);
        if(v390)control.enableCausalMotorCreditV390();
        if(useActualDeliveredProbeV397)control.enableActualDeliveredMotorProbeLabV397();
        for(int k=0;k<segments;k++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    17,serial*10+k,c.revision(),dx,dy,.5f,.5f,.4f);
            control.begin(intent,scene.body,learn,!learn,learn,true,true);
            for(int j=0;j<24;j++){
                float[] cmd=control.actuate(scene.body,scene.skin,DT);
                float previous=gel.work;
                gel.step(scene.body,scene.skin,cmd,DT);
                Zero387TactileFrame.begin(scene.skin);
                if(v390)
                    control.observeContactV390(scene.body,scene.skin,DT,
                        Math.max(0,gel.work-previous),
                        gel.lastMuscleReactionImpulse,false,false);
                else
                    control.observe(scene.body,DT,
                        Math.max(0,gel.work-previous),
                        gel.lastMuscleReactionImpulse,false,false);
            }
            control.finish();
        }
        return (scene.body.cx-startX)*dx+(scene.body.cy-startY)*dy;
    }
    @Test(timeout=900000)
    public void sameRealSavedMotorCompareLocalVsDistributedCreditOverRepeatedTrueGel()
        throws Exception{
        JSONObject user=fixture();
        for(boolean localized:new boolean[]{false,true}){
            Zero3LearningFoundation.AdaptiveCortex virginClone=trained(user);
            float priorEast=execute(user,virginClone,localized,0,false,1000,5);
            Zero3LearningFoundation.AdaptiveCortex cortex=trained(user);
            long start=cortex.updates();
            for(int i=0;i<128;i++){
                // Fully balanced laboratory sampling of directions, never
                // a hidden steering policy in the actual application.
                int h=i%4;
                float progress=execute(user,cortex,localized,h,true,i,1);
                if(i%32==0)android.util.Log.i("ZERO390_S21_AB",
                    "sample="+i+" scheme="+(localized?"ONE_SECTOR":"OLD_16_SECTORS")+
                    " realDirectionalProgress="+progress);
            }
            float afterEast=execute(user,cortex,localized,0,false,3000,5);
            float afterWest=execute(user,cortex,localized,2,false,4000,5);
            assertTrue(Float.isFinite(afterEast));
            assertTrue(Float.isFinite(afterWest));
            assertTrue("Must have trained some actual V3 physical motor trials",
                cortex.updates()>start);
            android.util.Log.i("ZERO390_S21_AB",
                "PROFILE=33312 USER_SAVED_CORTEX scheme="+
                (localized?"ISOLATED_ONE_SECTOR":"LEGACY_CORRELATED_SECTORS")+
                " beforeEast="+priorEast+" afterEast="+afterEast+
                " afterWest="+afterWest+" updates="+(cortex.updates()-start));
        }
    }
}
