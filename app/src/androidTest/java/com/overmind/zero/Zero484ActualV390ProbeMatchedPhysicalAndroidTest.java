package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO484 read-only matched original-S21 physical causal audit.
 * The ON arm uses the actual V390 random single-muscle neural probe
 * (raw amplitude <= .14) through the real tanh/effort/clipped CH0/1/3
 * output. OFF is an independent original-cortex/170-material clone
 * starting from the exact same saved state, posture and PRE velocity.
 * No live policy, weights, GRU, rewards, directions or saved slots change.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero484ActualV390ProbeMatchedPhysicalAndroidTest {
    private static final float DT=1f/60f;
    private static final int FRAMES=24;
    private static final float[] PRE_WEST={.35f,1f,2f};

    static final class Result {
        final float firstX,firstY,lastX,lastY,delivered,raw;
        final int site,channel,contacts;
        Result(float fx,float fy,float lx,float ly,float d,float r,
                int site,int ch,int contacts){
            firstX=fx;firstY=fy;lastX=lx;lastY=ly;
            delivered=d;raw=r;this.site=site;channel=ch;
            this.contacts=contacts;
        }
    }

    private static Zero3LearningFoundation.AdaptiveCortex cloneCortex(
            Zero3LearningFoundation.AdaptiveCortex.State state){
        Zero3LearningFoundation.AdaptiveCortex c=
            new Zero3LearningFoundation.AdaptiveCortex(state.learningRate());
        c.restore(state);
        c.enablePhysicalContextV412();
        c.enableDeliveredLocalCreditLabV421();
        return c;
    }

    private static Result run(JSONObject free,
            Zero3LearningFoundation.AdaptiveCortex.State state,
            float speed,int heading,boolean probe)throws Exception {
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            free.getString("training_room_1_scene_v379"));
        assertNotNull(scene);
        assertNotNull(scene.matter);
        Zero413MatchedPhysicalMotorCausalityAndroidTest.shiftWholeBody(
            scene,-speed,0f);
        scene.matter.enableOriginalZero3TractionV363();
        scene.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=cloneCortex(state);
        Zero3LearningFoundation.Coordinator motor=
            new Zero3LearningFoundation.Coordinator(cortex);
        motor.enableCausalMotorCreditV390();
        float ux=heading==0?1f:heading==2?-1f:0f;
        float uy=heading==1?1f:heading==3?-1f:0f;
        Zero3LearningFoundation.MotorIntent intent=
            new Zero3LearningFoundation.MotorIntent(
                484L,0L,cortex.revision(),ux,uy,.5f,.5f,.4f);
        motor.begin(intent,scene.body,false,true,probe,true,true);
        int site=probe?cortex.isolatedSectorV390():-1;
        int ch=probe?cortex.lastActiveProbeChannel():-1;
        if(probe){
            assertEquals(1,cortex.activeProbeCountV390());
            assertTrue(site>=0&&site<16);
            assertTrue(ch==0||ch==1||ch==3);
        }
        float startX=scene.body.cvx,startY=scene.body.cvy;
        float firstX=0f,firstY=0f;
        int contacts=0;
        ArrayList<UnknownWorld.Entity> emptyWorld=new ArrayList<>();
        for(int frame=0;frame<FRAMES;frame++){
            float[] cmd=motor.actuate(scene.body,scene.skin,DT);
            float previousWork=scene.matter.work;
            float[] previousSkinX=scene.skin.x.clone();
            float[] previousSkinY=scene.skin.y.clone();
            scene.matter.step(scene.body,scene.skin,cmd,DT);
            float[] gelX=scene.skin.x.clone(),gelY=scene.skin.y.clone();
            Zero387TactileFrame.begin(scene.skin);
            for(float touch:scene.skin.touchSense)
                if(touch>.23f){contacts++;break;}
            SymbioteWorldStepV171.interior(scene.matter,scene.body,
                scene.skin,null,emptyWorld,DT,gelX,gelY);
            if(frame==0){
                firstX=scene.body.cvx-startX;
                firstY=scene.body.cvy-startY;
            }
            motor.observeContactV390(scene.body,scene.skin,DT,
                Math.max(0f,scene.matter.work-previousWork),
                scene.matter.lastMuscleReactionImpulse,false,false);
            assertEquals("original physical solver valid",0L,scene.matter.invalid);
        }
        float delivered=0f,raw=0f;
        if(probe){
            Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 p=
                cortex.captureProbeV332();
            int idx=site*4+ch;
            assertNotNull(p.deliveredNoise);
            delivered=p.deliveredNoise[idx];
            raw=p.noise[idx];
            assertTrue("original V390 raw perturbation is bounded",
                Math.abs(raw)>=.0209f&&Math.abs(raw)<=.14001f);
            assertEquals("only one original cortex site sampled",
                ch,p.selectedChannel);
        }
        motor.finish();
        assertEquals("no original user cortex synapses updated",
            state.updates,cortex.updates());
        return new Result(firstX,firstY,
            scene.body.cvx-startX,scene.body.cvy-startY,
            delivered,raw,site,ch,contacts);
    }

    @Test(timeout=1500000)
    public void actualNeuralV390ProbeHasMatchedPhysicalNoProbeCounterfactual()
            throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout saved=
            Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
        assertEquals(33312L,saved.decisions);
        assertEquals(19452L,saved.motorUpdates);
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(user);
        Zero3LearningFoundation.AdaptiveCortex.State original=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user).checkpoint();
        int total=0,measured=0,physical=0,discordant=0,firstPhysical=0;
        for(float speed:PRE_WEST){
            Zero3LearningFoundation.AdaptiveCortex sequence=cloneCortex(original);
            sequence.enableIsolatedCreditV390();
            HashSet<Integer> visited=new HashSet<>();
            int localMeasured=0,localPhysical=0,localDiscordant=0;
            for(int sample=0;sample<48;sample++){
                int heading=sample%4; // balanced lab reference, NEVER live steering
                Zero3LearningFoundation.AdaptiveCortex.State state=
                    sequence.checkpoint();
                Result on=run(free,state,speed,heading,true);
                Result off=run(free,state,speed,heading,false);
                assertTrue("unique actual CH0/1/3 probe",visited.add(
                    on.site*4+on.channel));
                float ux=heading==0?1f:heading==2?-1f:0f;
                float uy=heading==1?1f:heading==3?-1f:0f;
                float difference=(on.lastX-off.lastX)*ux+
                    (on.lastY-off.lastY)*uy;
                float first=(on.firstX-off.firstX)*ux+
                    (on.firstY-off.firstY)*uy;
                float unpaired=on.lastX*ux+on.lastY*uy;
                assertTrue(Float.isFinite(difference));
                assertTrue(Float.isFinite(on.delivered));
                if(Math.abs(on.delivered)>1e-8f){
                    measured++;localMeasured++;
                    assertEquals(Math.signum(on.raw),
                        Math.signum(on.delivered),0f);
                }
                if(Math.abs(difference)>1e-6f){
                    physical++;localPhysical++;
                    if(Math.abs(on.delivered)>1e-8f&&
                       unpaired*on.delivered*difference<0f){
                        discordant++;localDiscordant++;
                    }
                }
                if(Math.abs(first)>1e-7f)firstPhysical++;
                total++;
                // Advance the REAL original V390 RNG/probe sequence without
                // executing or learning from a fabricated extra decision.
                Zero3LearningFoundation.MotorIntent dummy=
                    new Zero3LearningFoundation.MotorIntent(
                        484L,0L,sequence.revision(),ux,uy,.5f,.5f,.4f);
                sequence.begin(true,dummy,true);
                sequence.discard();
            }
            assertEquals("all original 16 x CH0/1/3 sampled",48,visited.size());
            Log.i("ZERO484_CAUSAL",String.format(Locale.US,
                "ACTUAL_V390_S21 speedWest=%.3f sampled=48"+
                " deliveredNonzero=%d physicalNonzero=%d"+
                " V417UnpairedSignDiscordance=%d",
                speed,localMeasured,localPhysical,localDiscordant));
        }
        Log.i("ZERO484_CAUSAL",
            "ORIGINAL_S21_ACTUAL_V390_MATCHED_CAUSAL"+
            " samples="+total+" deliveredNonzero="+measured+
            " physicalNonzero="+physical+" firstFramePhysical="+firstPhysical+
            " unpairedSignDiscordance="+discordant+
            " originalGRUWeightsUntouched=true liveBehaviorChanged=false");
        assertEquals(144,total);
        assertTrue("actual small neural probes reach original material",measured>0);
        assertTrue("matched physical contrast must be measurable",physical>0);
    }
}
