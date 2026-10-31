package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** ZERO431: paired physical ON/OFF/OFF/ON intervention runs through the
 * unchanged original S21 170-material and original 64 muscle solver.
 * These are causal instrument checks, NOT 10000-decision approval. */
@RunWith(AndroidJUnit4.class)
public final class Zero431BalancedRealMotorInterventionAndroidTest {
    private static final float DT=1f/60f;

    @Test public void matchedRealOriginalS21GelAndCortexExecuteBalancedABBA()
            throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            user.getString("training_room_1_free_scene_v379"));
        assertNotNull(scene);assertNotNull(scene.matter);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user);
        cortex.enablePhysicalContextV412();
        cortex.enableDeliveredLocalCreditLabV421();
        cortex.enablePairedPhysicalProbeLabV431();
        Zero3LearningFoundation.Coordinator coordinator=
            new Zero3LearningFoundation.Coordinator(cortex);
        coordinator.enableCausalMotorCreditV390();
        scene.matter.enableOriginalZero3TractionV363();
        scene.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.MotorIntent action=
            new Zero3LearningFoundation.MotorIntent(
                431,0,cortex.revision(),1f,0f,.5f,.5f,.4f);
        coordinator.begin(action,scene.body,true,false,true,true,true);
        assertEquals("Original V390 owner exposes ONE muscle only",1,
            cortex.activeProbeCountV390());
        float signedOn=0f,signedOff=0f;
        for(int frame=0;frame<24;frame++){
            float preVx=scene.body.cvx;
            float preVy=scene.body.cvy;
            float[] cmd=coordinator.actuate(scene.body,scene.skin,DT);
            boolean expected=(frame%4==0||frame%4==3);
            assertEquals("Correct balanced original motor probe frame "+frame,
                expected,cortex.lastAppliedProbeV431());
            float[] oldSkinX=scene.skin.x.clone(),
                    oldSkinY=scene.skin.y.clone();
            float oldWork=scene.matter.work;
            scene.matter.step(scene.body,scene.skin,cmd,DT);
            float[] afterSkinX=scene.skin.x.clone(),
                    afterSkinY=scene.skin.y.clone();
            SymbioteWorldStepV171.interior(
                scene.matter,scene.body,scene.skin,null,
                new ArrayList<UnknownWorld.Entity>(),DT,
                afterSkinX,afterSkinY);
            coordinator.observeContactV390(scene.body,scene.skin,DT,
                Math.max(0f,scene.matter.work-oldWork),
                Math.max(0f,scene.matter.lastMuscleReactionImpulse),
                false,false);
            float dv=scene.body.cvx-preVx;
            if(expected)signedOn+=dv;else signedOff+=dv;
            assertEquals("No synthetic body invalidity",0,scene.matter.invalid);
        }
        assertEquals("24 true material substeps must yield 6 balanced quartets",
            6,coordinator.abbaValidQuadsV431());
        float expected=(signedOn-signedOff)/12f;
        assertEquals("Actual sensory PRE→POST momentum contrast, not"+
            " hand-authored target, exactly defines the gradient",expected,
            coordinator.abbaObservedEffectV431(),1e-4f);
        assertTrue(Float.isFinite(expected));
        Log.i("ZERO431_ABBA","ZERO431_REAL_ACTUAL_S21_24_SUBSTEP_PAIR"+
            " physicalGradient="+expected+
            " realOnDV="+signedOn+" realOffDV="+signedOff);
        coordinator.abort();
    }

    @Test public void offOffFramesNeverCarryAnyV431ProbeEvenAtHighEffort(){
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        cortex.enableIsolatedCreditV390();
        cortex.enablePairedPhysicalProbeLabV431();
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        Zero3LearningFoundation.MotorIntent intention=
            new Zero3LearningFoundation.MotorIntent(
                431,0,cortex.revision(),0f,1f,1f,.5f,.4f);
        cortex.begin(true,intention,true);
        for(int i=0;i<16;i++){
            cortex.command(intention,body,skin,DT);
            assertEquals("No invented passive motor intervention",i%4==0||i%4==3,
                cortex.lastAppliedProbeV431());
        }
        cortex.discard();
    }

    @Test public void existingOriginalMotorProbeUnchangedWithoutExplicitLabOptIn(){
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        cortex.enableIsolatedCreditV390();
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        Zero3LearningFoundation.MotorIntent intention=
            new Zero3LearningFoundation.MotorIntent(
                431,0,cortex.revision(),0f,1f,.5f,.5f,.4f);
        cortex.begin(true,intention,true);
        for(int i=0;i<10;i++){
            cortex.command(intention,body,skin,DT);
            assertTrue("V417 original remains unchanged",cortex.lastAppliedProbeV431());
        }
        cortex.discard();
    }

    /** Independent physical-gradient validation against V413's TRUE +/-0.40
     * identical-start 170-material counterfactual. Do not count mere ABBA
     * execution as a correct motor-LEARNING direction. */
    @Test(timeout=1500000)
    public void learnedABBAProbeSignBeatsUnpairedV415MuscleGradientBaseline()
            throws Exception{
        JSONObject saved=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(saved);
        JSONObject rest=Zero412TrueS21MotorContextPhysicalABAndroidTest.stopped(saved);
        Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario[] contexts={
            new Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario(
                "FREE_S21_EAST",free,false,0,0f,0f),
            new Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario(
                "REST_S21_EAST",rest,false,0,0f,0f)
        };
        int[] probes={0,3,7,11,15,19,23,27,31,35,39,43,47};
        int compared=0,matched=0,opposed=0,ambiguous=0;
        for(Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario env:contexts){
            for(int trialIndex:probes){
                GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
                    env.scene.getString("training_room_1_scene_v379"));
                Zero3LearningFoundation.AdaptiveCortex cortex=
                    Zero390ActualUserMotorRehabilitationAndroidTest.trained(saved);
                cortex.enablePhysicalContextV412();
                cortex.enableDeliveredLocalCreditLabV421();
                cortex.enablePairedPhysicalProbeLabV431();
                Zero3LearningFoundation.Coordinator controller=
                    new Zero3LearningFoundation.Coordinator(cortex);
                controller.enableCausalMotorCreditV390();
                scene.matter.enableOriginalZero3TractionV363();
                scene.matter.configurePhysicalTurnBrakeV382(-.04f);
                // Advance only deterministic V390 probe selector, with
                // ZERO execution and ZERO synaptic update until target site.
                for(int skipped=0;skipped<trialIndex;skipped++){
                    Zero3LearningFoundation.MotorIntent skip=
                        new Zero3LearningFoundation.MotorIntent(
                            431,skipped,cortex.revision(),1f,0f,.5f,.5f,.4f);
                    controller.begin(skip,scene.body,true,false,true,true,true);
                    controller.abort();
                }
                Zero3LearningFoundation.MotorIntent selected=
                    new Zero3LearningFoundation.MotorIntent(
                        431,trialIndex,cortex.revision(),1f,0f,.5f,.5f,.4f);
                controller.begin(selected,scene.body,true,false,true,true,true);
                int sector=cortex.isolatedSectorV390(),
                    channel=cortex.lastActiveProbeChannel();
                for(int frame=0;frame<24;frame++){
                    float[] cmd=controller.actuate(scene.body,scene.skin,DT);
                    float oldWork=scene.matter.work;
                    scene.matter.step(scene.body,scene.skin,cmd,DT);
                    float[] afterSkinX=scene.skin.x.clone(),
                            afterSkinY=scene.skin.y.clone();
                    SymbioteWorldStepV171.interior(
                        scene.matter,scene.body,scene.skin,null,
                        new ArrayList<UnknownWorld.Entity>(),DT,
                        afterSkinX,afterSkinY);
                    controller.observeContactV390(scene.body,scene.skin,DT,
                        Math.max(0f,scene.matter.work-oldWork),
                        Math.max(0f,scene.matter.lastMuscleReactionImpulse),
                        false,false);
                }
                float observedABBA=controller.abbaObservedEffectV431();
                Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 trace=
                    cortex.captureProbeV332();
                float delivered=trace.deliveredNoise[sector*4+channel];
                controller.abort();
                Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial minus=
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                        env,saved,2,sector,channel,-1);
                Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial plus=
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                        env,saved,2,sector,channel,+1);
                float truePhysical=plus.along(env.heading)-
                    minus.along(env.heading);
                float inferred=observedABBA*Math.signum(delivered);
                if(Math.abs(truePhysical)<.005f||
                   Math.abs(inferred)<.0002f){ambiguous++;continue;}
                compared++;
                if(truePhysical*inferred>0f)matched++;else opposed++;
            }
        }
        float accuracy=matched/(float)Math.max(1,compared);
        Log.i("ZERO431_ABBA","ZERO431_TRUE_ORIGINAL_S21_PAIRED_SIGN_AUDIT"+
            " compared="+compared+" matched="+matched+
            " opposite="+opposed+" ambiguous="+ambiguous+
            " agreement="+accuracy+" legacyV415=0.480226");
        assertTrue("A real differential proof needs >=10 informative"+
            " S21 interventions, not merely a successful emulator compilation",
            compared>=10);
        assertTrue("The proposed causal estimator must demonstrably exceed"+
            " V415's ~48% sign agreement before any release",accuracy>=.65f);
    }

}