package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO495 actual-user original new-brain causal MOTOR LEARNING audit.
 * Exact 2026-11-07 Nov7 4747-decision checkpoint and 170 original particles.
 * Same initial live body, same issued cardinal heading and physical solver.
 * Each independent trial changes ONLY physical actuator's 512 local synapses:
 * old learned 1871-update weights vs genuine virgin initialization, while
 * preserving old 64 slow states as a separate matched alternative.
 *
 * Also compare a fully virgin slow-state/weights configuration and true
 * passive muscle-off. No actual app brain/weights or production is modified.
 * The cardinal directions are LAB-only probes, not scripted live policy.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero495ActualNewbornTrainedVsVirginCortexAndroidTest {
    static final int OFF=0,TRAINED=1,VIRGIN_WEIGHTS_SAVED_STATE=2,
            COMPLETE_VIRGIN=3;
    static final float DT=1f/60f;
    private static final int[] CHANNELS={0,1,3};
    static float[] weights(Zero3LearningFoundation.AdaptiveCortex c)throws Exception{
        Field f=c.getClass().getDeclaredField("weights");
        f.setAccessible(true);
        return (float[])f.get(c);
    }
    static final class Trial {
        final float dx,dy,vx,vy,work,impulse;
        Trial(float dx,float dy,float vx,float vy,float work,float impulse){
            this.dx=dx;this.dy=dy;this.vx=vx;this.vy=vy;
            this.work=work;this.impulse=impulse;
        }
        float movement(int h){
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(dx,dy,h);
        }
        float speed(int h){
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(vx,vy,h);
        }
    }
    static Trial replay(JSONObject user,int heading,int decisions,int profile)
            throws Exception{
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull(s);assertNotNull(s.matter);
        s.matter.enableOriginalZero3TractionV363();
        s.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        if(profile==VIRGIN_WEIGHTS_SAVED_STATE){
            Zero3LearningFoundation.AdaptiveCortex virgin=
                new Zero3LearningFoundation.AdaptiveCortex();
            System.arraycopy(weights(virgin),0,weights(cortex),0,512);
        }else if(profile==COMPLETE_VIRGIN){
            cortex=new Zero3LearningFoundation.AdaptiveCortex();
            cortex.enablePhysicalContextV412();
        }
        Zero3LearningFoundation.Coordinator motor=
            new Zero3LearningFoundation.Coordinator(cortex);
        float initialX=s.body.cx,initialY=s.body.cy;
        float initialWork=s.matter.work;
        long invalid=s.matter.invalid;
        float impulse=0f;
        final float x=heading==0?1f:heading==2?-1f:0f;
        final float y=heading==1?1f:heading==3?-1f:0f;
        ArrayList<UnknownWorld.Entity> emptyWorld=new ArrayList<>();
        for(int decision=0;decision<decisions;decision++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    495,decision,cortex.revision(),x,y,.5f,.5f,.4f);
            motor.begin(intent,s.body,false,true,false,true,true);
            for(int tick=0;tick<24;tick++){
                float[] output=motor.actuate(s.body,s.skin,DT);
                if(profile==OFF)
                    for(int sector=0;sector<16;sector++)
                        for(int ch:CHANNELS)output[sector*4+ch]=0f;
                float lastWork=s.matter.work;
                s.matter.step(s.body,s.skin,output,DT);
                float[] preWorldGelX=s.skin.x.clone(),
                        preWorldGelY=s.skin.y.clone();
                Zero387TactileFrame.begin(s.skin);
                SymbioteWorldStepV171.interior(s.matter,s.body,s.skin,null,
                    emptyWorld,DT,preWorldGelX,preWorldGelY);
                motor.observe(s.body,DT,
                    Math.max(0f,s.matter.work-lastWork),
                    s.matter.lastMuscleReactionImpulse,false,false);
                impulse+=s.matter.lastMuscleReactionImpulse;
                assertTrue(Float.isFinite(s.body.cvx));
            }
            motor.finish();
        }
        assertEquals("No invalid cortex material dynamics",invalid,
            s.matter.invalid);
        return new Trial(s.body.cx-initialX,s.body.cy-initialY,
            s.body.cvx,s.body.cvy,s.matter.work-initialWork,impulse);
    }

    @Test(timeout=1500000)
    public void realUserOriginalTrainedVsVirginMotorSynapseOnlyAB()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        int cases=0;
        for(int heading:new int[]{2,3}){
            for(int decisions:new int[]{1,4,8,12}){
                Trial passive=replay(user,heading,decisions,OFF);
                Trial trained=replay(user,heading,decisions,TRAINED);
                Trial newWeights=replay(user,heading,decisions,
                    VIRGIN_WEIGHTS_SAVED_STATE);
                Trial virgin=replay(user,heading,decisions,COMPLETE_VIRGIN);
                cases+=4;
                Log.i("ZERO495_LEARNING",String.format(Locale.US,
                    "heading=%d decisions=%d "+
                    "passiveFinal=%.7f learnedFinal=%.7f "+
                    "freshWeightsSameStateFinal=%.7f "+
                    "fullVirginFinal=%.7f "+
                    "learnedTravel=%.7f freshWeightsTravel=%.7f "+
                    "fullVirginTravel=%.7f passiveTravel=%.7f "+
                    "learnedNetMotorDV=%.7f freshNetMotorDV=%.7f "+
                    "freshMinusLearned=%.7f learnedImpulse=%.4f "+
                    "freshImpulse=%.4f virginImpulse=%.4f",
                    heading,decisions,passive.speed(heading),
                    trained.speed(heading),newWeights.speed(heading),
                    virgin.speed(heading),trained.movement(heading),
                    newWeights.movement(heading),virgin.movement(heading),
                    passive.movement(heading),
                    trained.speed(heading)-passive.speed(heading),
                    newWeights.speed(heading)-passive.speed(heading),
                    newWeights.speed(heading)-trained.speed(heading),
                    trained.impulse,newWeights.impulse,virgin.impulse));
            }
        }
        assertEquals(32,cases);
        Log.i("ZERO495_LEARNING",
            "ORIGINAL_4747_TRAINED_VS_VIRGIN_ACTUATION_COMPLETE"+
            " cases="+cases+" actualOriginalMaterial=true"+
            " noProductionWeightsModified=true");
    }
}
