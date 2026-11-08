package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.lang.reflect.Field;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * True frozen S21 user's Oct25 33312 GRU3 cortex/170-site body/world.
 * Eight strict equal-start A/B trials: four physical headings, old floor
 * contact vs optional passive contacted-retraction mobility. All same
 * 96-site material, old CH2 grip, original learned cortical synapses and
 * real TrainingRoom tile collision solvers. No COM velocity injection,
 * red/blue valuation, world goal, autonomous-behavior success claim.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero511ActualS21ContactRetractionPhysicsAndroidTest {
    static final float DT=1f/60f;
    static final float[][] CARDINAL={{1f,0f},{0f,1f},{-1f,0f},{0f,-1f}};
    @Test(timeout=5400000)
    public void fourDirectionsOriginalRealWallUnderMatchedMaterialReleaseAB()
            throws Exception{
        JSONObject fixture=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject realScene=new JSONObject(fixture.getString("training_room_1_scene_v379"));
        JSONObject physical=realScene.getJSONObject("physical");
        final String tissue=physical.toString();
        assertEquals(96,physical.getJSONObject("surface").getJSONArray("x").length());
        Zero3OriginalBrainV356.Readout original=
            Zero3OriginalBrainV356.inspectSaved(fixture.getString("brain"));
        assertEquals(33312L,original.decisions);
        final Field motorField=original.getClass().getDeclaredField("cortex");
        motorField.setAccessible(true);
        for(int heading=0;heading<4;heading++){
            for(int variant=0;variant<2;variant++){
                GameSceneSnapshot.Scene scene=GameSceneSnapshot.restore(tissue);
                assertNotNull(scene);assertNotNull(scene.matter);
                SymbioteMatterV1 matter=scene.matter;
                matter.enableOriginalZero3TractionV363();
                matter.configurePhysicalTurnBrakeV382(-.04f);
                matter.configurePhysicalContactRetractionLabV511(variant==1);
                Zero3OriginalBrainV356.Readout restored=
                    Zero3OriginalBrainV356.inspectSaved(fixture.getString("brain"));
                Zero3LearningFoundation.AdaptiveCortex cortex=
                    (Zero3LearningFoundation.AdaptiveCortex)motorField.get(restored);
                cortex.enablePhysicalContextV412();
                cortex.enablePostureMotorTransducerV499();
                Zero3LearningFoundation.Coordinator control=
                    new Zero3LearningFoundation.Coordinator(cortex);
                float x0=scene.body.cx,y0=scene.body.cy;
                long invalidBefore=matter.invalid,realContactFrames=0;
                final float dx=CARDINAL[heading][0],dy=CARDINAL[heading][1];
                float totalMotion=0f,totalGross=0f;
                for(int action=0;action<12;action++){
                    float preX=scene.body.cx,preY=scene.body.cy;
                    Zero3LearningFoundation.MotorIntent intent=
                        new Zero3LearningFoundation.MotorIntent(
                            511,action,cortex.revision(),dx,dy,.5f,.5f,.4f);
                    control.begin(intent,scene.body,false,true,false,true,true);
                    for(int frame=0;frame<24;frame++){
                        float[] issued=control.actuate(scene.body,scene.skin,DT);
                        float[] oldSkinX=scene.skin.x.clone(),oldSkinY=scene.skin.y.clone(),
                            oldBodyX=scene.body.x.clone(),oldBodyY=scene.body.y.clone();
                        float workBefore=matter.work;
                        matter.step(scene.body,scene.skin,issued,DT);
                        // EXACT real GameActivity transaction: preserve
                        // the tissue AFTER material force integration but
                        // BEFORE world collision projection.
                        float[] preWorldSkinX=scene.skin.x.clone();
                        float[] preWorldSkinY=scene.skin.y.clone();
                        Zero387TactileFrame.begin(scene.skin);
                        // This is the actual ORIGINAL S21 solid tile solver.
                        TrainingRoomV379.constrain(scene.body,scene.skin,
                            scene.objects,oldSkinX,oldSkinY,oldBodyX,oldBodyY);
                        SymbioteWorldStepV171.interior(matter,scene.body,scene.skin,
                            null,scene.objects,DT,preWorldSkinX,preWorldSkinY);
                        for(float contact:scene.skin.touchSense)
                            if(contact>.5f)realContactFrames++;
                        control.observe(scene.body,DT,
                            Math.max(0f,matter.work-workBefore),
                            matter.lastMuscleReactionImpulse,false,false);
                    }
                    control.finish();
                    float vx=scene.body.cx-preX,vy=scene.body.cy-preY;
                    totalMotion+=vx*dx+vy*dy;
                    totalGross+=(float)Math.hypot(vx,vy);
                }
                assertEquals("same original actual finite physical constraints",
                    invalidBefore,matter.invalid);
                assertTrue(Float.isFinite(totalMotion)&&Float.isFinite(totalGross));
                assertTrue("S21 body touches real wall",
                    realContactFrames>50);
                if(variant==0)assertEquals(0,matter.physicalContactRetractionFramesV511);
                Log.i("ZERO511_CONTACT",String.format(Locale.ROOT,
                    "actualUserS21=true heading=%d touchRelease=%s "+
                    "forward=%.5f gross=%.5f efficiency=%.6f "+
                    "netX=%.5f netY=%.5f contactPatchFrames=%d "+
                    "releasingPatchFrames=%d cortexUpdates=%d",
                    heading,variant==1,totalMotion,totalGross,
                    totalMotion/Math.max(1f,totalGross),
                    scene.body.cx-x0,scene.body.cy-y0,
                    realContactFrames,matter.physicalContactRetractionFramesV511,
                    cortex.updates()));
            }
        }
        Log.i("ZERO511_CONTACT","REAL_S21_4_HEADINGS_MATCHED_CONTACT_RETRACTION_AB_COMPLETE");
    }
}
