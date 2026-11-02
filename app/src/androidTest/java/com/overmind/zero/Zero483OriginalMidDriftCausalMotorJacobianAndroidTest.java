package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO483: genuine original S21 170-material matched physical interventions.
 * A diagnostic, NOT a live motor policy or an autonomous locomotion gate.
 * Each +/- muscle intervention starts from the identical saved body, velocity,
 * cortex and original ground law. The actual V417 unpaired delta COM velocity
 * is compared to the physically delivered differential for CH0/CH1/CH3.
 * No saved brain, motor weights, GRU3 choices, force or rewards are modified.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero483OriginalMidDriftCausalMotorJacobianAndroidTest {
    private static final float DT=1f/60f;
    private static final float[] PRE_WEST_SPEED={.35f,1.0f,2.0f};
    private static final int[] MUSCLE_CHANNELS={0,1,3};

    @Test(timeout=1500000)
    public void matchedS21MidDriftMeasuresActualMuscleDeltaAbovePassiveCoast()
            throws Exception {
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout saved=
            Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
        assertEquals(33312L,saved.decisions);
        assertEquals(19452L,saved.motorUpdates);
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(user);
        int comparisons=0,nonzero=0,oppositeUnpaired=0,oppositeNet=0;
        for(float speed:PRE_WEST_SPEED){
            Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario scenario=
                new Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario(
                    "ORIGINAL_S21_FREE_PRE_WEST_"+speed,free,false,0,-speed,0f);
            Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial passive=
                Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                    scenario,user,0,-1,-1,0);
            Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial full=
                Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                    scenario,user,1,-1,-1,0);
            // This is precisely the unpaired direction used by V417:
            // observed POST minus PRE COM velocity along the issued heading.
            float unpairedV417=full.deltaVx;
            // Same-body passive subtraction is an observational control,
            // not a proven single-muscle derivative.
            float netAbovePassive=full.deltaVx-passive.deltaVx;
            int speedNonzero=0,speedOpposite=0,speedOppositeNet=0;
            float minEffect=Float.POSITIVE_INFINITY;
            float maxEffect=Float.NEGATIVE_INFINITY;
            for(int sector=0;sector<16;sector++)for(int channel:MUSCLE_CHANNELS){
                Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial minus=
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                        scenario,user,2,sector,channel,-1);
                Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial plus=
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                        scenario,user,2,sector,channel,+1);
                assertEquals(0L,minus.invalid);
                assertEquals(0L,plus.invalid);
                float physicalJacobian=plus.deltaVx-minus.deltaVx;
                assertTrue(Float.isFinite(physicalJacobian));
                minEffect=Math.min(minEffect,physicalJacobian);
                maxEffect=Math.max(maxEffect,physicalJacobian);
                comparisons++;
                if(Math.abs(physicalJacobian)>.0001f){
                    nonzero++;speedNonzero++;
                    if(unpairedV417*physicalJacobian<0f){
                        oppositeUnpaired++;speedOpposite++;
                    }
                    if(netAbovePassive*physicalJacobian<0f){
                        oppositeNet++;speedOppositeNet++;
                    }
                }
            }
            Log.i("ZERO483_CAUSAL",String.format(Locale.US,
                "MID_DRIFT_S21 speedWest=%.3f passiveDVx=%.6f"+
                " fullDVx=%.6f unpairedV417=%.6f netAbovePassive=%.6f"+
                " differentialNonzero=%d/48 oppositeUnpaired=%d"+
                " oppositeNet=%d minJacobian=%.6f maxJacobian=%.6f",
                speed,passive.deltaVx,full.deltaVx,unpairedV417,
                netAbovePassive,speedNonzero,speedOpposite,speedOppositeNet,
                minEffect,maxEffect));
        }
        Log.i("ZERO483_CAUSAL",
            "ORIGINAL_S21_MATCHED_MOTOR_JACOBIAN comparisons="+comparisons+
            " physicalNonzero="+nonzero+
            " unpairedSignDiscordance="+oppositeUnpaired+
            " passiveSubtractedSignDiscordance="+oppositeNet+
            " labOnly=true liveMotorChanged=false");
        assertEquals("three PRE speeds x 16 sectors x three original muscle channels",
            144,comparisons);
        assertTrue("actual 170-material muscle differential must be measurable",
            nonzero>0);
    }

    @Test
    public void ordinaryFreeFloorTractionDoesNotCensorV454PairedQuad()
            throws Exception {
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(user);
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            free.getString("training_room_1_scene_v379"));
        assertNotNull(scene);
        scene.matter.enableOriginalZero3TractionV363();
        scene.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user);
        cortex.enablePhysicalContextV412();
        cortex.enableDeliveredLocalCreditLabV421();
        Zero3LearningFoundation.Coordinator motor=
            new Zero3LearningFoundation.Coordinator(cortex);
        motor.enableCausalMotorCreditV390();
        motor.enablePairedOriginalPhysicalProbeLabV454();
        Zero3LearningFoundation.MotorIntent intention=
            new Zero3LearningFoundation.MotorIntent(
                483L,0L,cortex.revision(),1f,0f,.65f,.5f,.25f);
        motor.begin(intention,scene.body,false,true,true,true,true);
        int actualTouchFrames=0;
        for(int frame=0;frame<8;frame++){
            float[] command=motor.actuate(scene.body,scene.skin,DT);
            float previousWork=scene.matter.work;
            scene.matter.step(scene.body,scene.skin,command,DT);
            Zero387TactileFrame.begin(scene.skin);
            for(float touch:scene.skin.touchSense)
                if(touch>.23f){actualTouchFrames++;break;}
            motor.observeContactV390(scene.body,scene.skin,DT,
                Math.max(0f,scene.matter.work-previousWork),
                scene.matter.lastMuscleReactionImpulse,false,false);
        }
        assertEquals("free floor drag is real but not a wall-touch flag",
            0,actualTouchFrames);
        assertEquals("two actual four-substep original-body ABBA quads",
            2,motor.validPairedQuadsV454());
        Log.i("ZERO483_CAUSAL",
            "ORIGINAL_FREE_FLOOR_CONTACT_PROVENANCE touchFrames="+
            actualTouchFrames+" validPairedQuads="+motor.validPairedQuadsV454()+
            " genuineFloorDrag=true physicalSolver=true");
        motor.finish();
    }
}
