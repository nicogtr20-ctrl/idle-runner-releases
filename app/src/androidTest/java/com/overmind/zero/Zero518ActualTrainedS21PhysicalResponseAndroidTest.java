package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO518: real user S21 33312-trained GRU/cortex, 256 actually issued
 * neural headings, current saved 170-site body AND all saved training-room
 * solids. This fills the critical mismatch: V517 only measured a completely
 * different 4747-decision newborn physical checkpoint and removed world solids.
 *
 * A/B only; NOTHING is enabled on the user's game or saved in any slot.
 * The real room starts from precisely the same snapshot for each arm.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero518ActualTrainedS21PhysicalResponseAndroidTest {
    private static final float DT=1f/60f;
    static Zero501RecordedSequentialMotorAndroidTest.Result replay(
            JSONObject fixture,
            Zero501RecordedSequentialMotorAndroidTest.Sample[] trace,
            String savedScene,int mode)throws Exception{
        return replay(fixture,trace,savedScene,mode,false);
    }
    // V519: the prior V518 replay missed GameActivity's membrane/solid
    // surface collision; interior-only physics is NOT the live game.
    static Zero501RecordedSequentialMotorAndroidTest.Result replay(
            JSONObject fixture,
            Zero501RecordedSequentialMotorAndroidTest.Sample[] trace,
            String savedScene,int mode,boolean fullWall)throws Exception{
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(savedScene);
        assertNotNull("S21 genuine saved room must restore",scene);
        assertNotNull("S21 170-site gel",scene.matter);
        assertNotNull("S21 real training-room world objects",scene.objects);
        assertTrue("S21 room or independent saved free-room must contain real solids",scene.objects.size()>=24);
        SymbioteMatterV1 matter=scene.matter;
        matter.enableOriginalZero3TractionV363();
        matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(fixture);
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(cortex);
        Zero501RecordedSequentialMotorAndroidTest.Result out=
            new Zero501RecordedSequentialMotorAndroidTest.Result(mode);
        float[] filtered={0f,0f};
        final long initialInvalid=matter.invalid;
        for(int i=0;i<trace.length;i++){
            Zero501RecordedSequentialMotorAndroidTest.Sample saved=trace[i];
            matter.observedOriginalMotorChangeV382(saved.x,saved.y);
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    518,i,cortex.revision(),saved.x,saved.y,.5f,.5f,saved.dt);
            control.begin(intent,scene.body,false,true,false,true,true);
            float beforeX=scene.body.cx,beforeY=scene.body.cy;
            for(int frame=0;frame<saved.n;frame++){
                float[] motor=control.actuate(scene.body,scene.skin,DT);
                if(mode==14)
                    Zero501RecordedSequentialMotorAndroidTest.mechanicalBodyController(
                        14,motor,scene.body,scene.skin,saved,filtered);
                else if(mode==0)
                    Zero499PostureMotorTransducer.apply(
                        intent,scene.body,scene.skin,motor);
                else
                    Zero499PostureMotorTransducer.apply(
                        intent,scene.body,scene.skin,motor,mode);
                float beforeWork=matter.work;
                // GameActivity snapshots tissue AND ten physical particles
                // before gel.step to prevent tunnelling between 60Hz frames.
                float[] priorSkinX=fullWall?scene.skin.x.clone():null;
                float[] priorSkinY=fullWall?scene.skin.y.clone():null;
                float[] priorBodyX=fullWall?scene.body.x.clone():null;
                float[] priorBodyY=fullWall?scene.body.y.clone():null;
                matter.step(scene.body,scene.skin,motor,DT);
                float[] preWorldX=scene.skin.x.clone(),preWorldY=scene.skin.y.clone();
                Zero387TactileFrame.begin(scene.skin);
                if(fullWall){
                    // EXACT live GameActivity TRAINING world membrane CCD.
                    // V518 previously simulated walls for fluid nodes ONLY.
                    // This includes the stored 94 real user wall tiles.
                    TrainingRoomV379.constrain(scene.body,scene.skin,scene.objects,
                        priorSkinX,priorSkinY,priorBodyX,priorBodyY);
                    MaterialContactConstraints.reconcile(
                        scene.body,scene.skin,scene.objects,DT);
                }
                SymbioteWorldStepV171.interior(matter,scene.body,scene.skin,
                    scene.room,scene.objects,DT,preWorldX,preWorldY);
                if(fullWall)GripSurfaceComposition.compose(
                    scene.skin,scene.room,scene.objects);
                control.observe(scene.body,DT,
                    Math.max(0f,matter.work-beforeWork),
                    matter.lastMuscleReactionImpulse,false,false);
                out.impulse+=matter.lastMuscleReactionImpulse;
                out.peakSpeed=Math.max(out.peakSpeed,
                    (float)Math.hypot(scene.body.cvx,scene.body.cvy));
            }
            control.finish();
            float dx=scene.body.cx-beforeX,dy=scene.body.cy-beforeY;
            float forward=dx*saved.x+dy*saved.y;
            out.signed+=forward;out.gross+=(float)Math.hypot(dx,dy);
            if(forward>=0f)out.aligned++;else out.opposed++;
            out.decisions++;
        }
        assertEquals("Unchanged real recorded neural episode count",
            trace.length,out.decisions);
        assertEquals("No new invalid physical 170-site steps",initialInvalid,matter.invalid);
        assertTrue("S21 active material displacement should be measurable",out.gross>5f);
        assertTrue("Finite genuine body response",Float.isFinite(out.efficiency()));
        return out;
    }
    @Test(timeout=5400000)
    public void actualS21WorldAndFreeOriginalGRUFlightComparePhysicalSteering()
            throws Exception{
        JSONObject fixture=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero501RecordedSequentialMotorAndroidTest.Sample[] trace=
            Zero501RecordedSequentialMotorAndroidTest.originalFlight(
                new JSONObject().put("motorRecorderCsv",
                    fixture.getString("motorRecorderCsv")));
        assertEquals(256,trace.length);
        String[] contexts={"training_room_1_scene_v379",
            "training_room_1_free_scene_v379"};
        for(String context:contexts){
            double baseline=Double.NaN,best=-Double.MAX_VALUE;
            int bestMode=-1;
            for(int mode:new int[]{0,14,22,23,24,25}){
                Zero501RecordedSequentialMotorAndroidTest.Result z=
                    replay(fixture,trace,fixture.getString(context),mode);
                Log.i("ZERO518_S21",String.format(Locale.US,
                    "context=%s mode=%d recordedS21NeuralDecisions=%d "+
                    "efficiency=%.6f signed=%.6f gross=%.6f aligned=%d "+
                    "opposed=%d peakSpeed=%.5f impulse=%.2f",
                    context,mode,z.decisions,z.efficiency(),z.signed,
                    z.gross,z.aligned,z.opposed,z.peakSpeed,z.impulse));
                if(mode==0)baseline=z.efficiency();
                else if(mode>=22&&z.efficiency()>best){
                    best=z.efficiency();bestMode=mode;
                }
            }
            Log.i("ZERO518_S21",String.format(Locale.US,
                "EXACT_TRAINED_S21_ORIGINAL_170_BODY_WORLD_256_DECISION_AB "+
                "context=%s baseline=%.6f bestV517=%.6f bestMode=%d "+
                "improvement=%.6f noAutonomousClaim=true",
                context,baseline,best,bestMode,best-baseline));
            // V517 cannot be promoted from newborn traces alone: prove it
            // on the true trained S21 physical body in BOTH preserved rooms.
            assertTrue("V517 servo must improve actual trained S21 motor "+
                "by >=2 points in every independently saved room",
                best>=baseline+.02d);
        }
    }
}
