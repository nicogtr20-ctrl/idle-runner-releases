package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Arrays;
import java.lang.reflect.Field;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO494 research-only decomposition of the observed wrong-way cumulative
 * muscle thrust in the user's exact 4747-decision original V489 cortex:
 * - passive muscles (no active CH0/1/3),
 * - intact learned recurrent 64-output cortex,
 * - fixed first-frame 64-channel cortical efference through real tissue,
 * - same learned weights/features but restart 64 local recurrent states at
 *   each completed decision,
 * - same learned weights/features but restart 64 local states before every
 *   physical tick.
 *
 * Both frozen-output and state reset are DIAGNOSTIC counterfactuals only;
 * no app code, save profile, trained weights, GRU choice or physics is edited.
 * Identical original saved membrane/inner material and first body velocity
 * are restored before EVERY individual experiment.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero494OriginalNewbornCortexStateVsDynamicBodyAndroidTest {
    static final int PASSIVE=0,LIVE=1,FROZEN_COMMAND=2,RESET_AT_DECISION=3,
            RESET_EVERY_FRAME=4;
    static final float DT=1f/60f;
    static final int[] MUSCLES={0,1,3};

    static final class Result {
        final float px,py,vx,vy,dx,dy,activeImpulse;
        final int decisions,mode;
        Result(float px,float py,float vx,float vy,float dx,float dy,
                float activeImpulse,int decisions,int mode){
            this.px=px;this.py=py;this.vx=vx;this.vy=vy;this.dx=dx;
            this.dy=dy;this.activeImpulse=activeImpulse;
            this.decisions=decisions;this.mode=mode;
        }
        float finalToward(int heading){
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(vx,vy,heading);
        }
        float travelToward(int heading){
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(dx,dy,heading);
        }
    }
    static Result test(JSONObject user,int h,int n,int mode)throws Exception{
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull(s);assertNotNull(s.matter);
        SymbioteMatterV1 material=s.matter;
        material.enableOriginalZero3TractionV363();
        material.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        Field stateField=cortex.getClass().getDeclaredField("state");
        stateField.setAccessible(true);
        float[] liveState=(float[])stateField.get(cortex);
        final float[] originalState=liveState.clone();
        final float startX=s.body.cx,startY=s.body.cy;
        final float startVx=s.body.cvx,startVy=s.body.cvy;
        Zero3LearningFoundation.Coordinator controller=
            new Zero3LearningFoundation.Coordinator(cortex);
        final float ux=h==0?1f:h==2?-1f:0f;
        final float uy=h==1?1f:h==3?-1f:0f;
        final ArrayList<UnknownWorld.Entity> noObjects=new ArrayList<>();
        float[] frozen=null;
        float impulse=0f;
        long initialInvalid=material.invalid;
        for(int decision=0;decision<n;decision++){
            // Explicitly isolate previous cortical recurrence while leaving
            // the user's 512 trained weights and 170 particle pose unchanged.
            if(mode==RESET_AT_DECISION)
                System.arraycopy(originalState,0,liveState,0,64);
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(494,decision,
                    cortex.revision(),ux,uy,.5f,.5f,.4f);
            controller.begin(intent,s.body,false,true,false,true,true);
            for(int frame=0;frame<24;frame++){
                if(mode==RESET_EVERY_FRAME)
                    System.arraycopy(originalState,0,liveState,0,64);
                float[] outputs=controller.actuate(s.body,s.skin,DT);
                if(frozen==null&&mode==FROZEN_COMMAND)
                    frozen=outputs.clone();
                if(mode==PASSIVE){
                    for(int i=0;i<16;i++)
                        for(int ch:MUSCLES)outputs[i*4+ch]=0f;
                }else if(mode==FROZEN_COMMAND){
                    System.arraycopy(frozen,0,outputs,0,64);
                }
                float prevWork=material.work;
                material.step(s.body,s.skin,outputs,DT);
                float[] gelX=s.skin.x.clone(),gelY=s.skin.y.clone();
                Zero387TactileFrame.begin(s.skin);
                SymbioteWorldStepV171.interior(material,s.body,s.skin,null,
                    noObjects,DT,gelX,gelY);
                controller.observe(s.body,DT,
                    Math.max(0f,material.work-prevWork),
                    material.lastMuscleReactionImpulse,false,false);
                impulse+=material.lastMuscleReactionImpulse;
                assertTrue(Float.isFinite(s.body.cvx));
                assertTrue(Float.isFinite(s.body.cvy));
            }
            controller.finish();
        }
        assertEquals("Original material stays valid",initialInvalid,material.invalid);
        assertEquals("Cortex 512 weights must remain frozen",
            1871L,cortex.updates());
        return new Result(startVx,startVy,s.body.cvx,s.body.cvy,
            s.body.cx-startX,s.body.cy-startY,impulse,n,mode);
    }

    @Test(timeout=1500000)
    public void actualNewbornMotorRecurrenceVersusFrozenEfferenceSeparatesRootCause()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        int cases=0;
        for(int heading:new int[]{2,3}){
            for(int decisions:new int[]{1,4,8,12}){
                Result passive=test(user,heading,decisions,PASSIVE);
                Result base=test(user,heading,decisions,LIVE);
                assertTrue(Float.isFinite(base.vx));
                for(int mode:new int[]{FROZEN_COMMAND,RESET_AT_DECISION,RESET_EVERY_FRAME}){
                    Result alternative=test(user,heading,decisions,mode);
                    float dV=alternative.finalToward(heading)-
                        passive.finalToward(heading);
                    float dX=alternative.travelToward(heading)-
                        passive.travelToward(heading);
                    float changeVsLive=alternative.finalToward(heading)-
                        base.finalToward(heading);
                    Log.i("ZERO494_CAUSAL",String.format(Locale.US,
                        "heading=%d decisions=%d mode=%d "+
                        "passiveFinal=%.7f liveFinal=%.7f trialFinal=%.7f "+
                        "trialVelocityGain=%.7f trialMotionGain=%.7f "+
                        "gainVsLiveVelocity=%.7f trueActuationImpulse=%.4f",
                        heading,decisions,mode,
                        passive.finalToward(heading),
                        base.finalToward(heading),alternative.finalToward(heading),
                        dV,dX,changeVsLive,alternative.activeImpulse));
                    cases++;
                }
            }
        }
        assertEquals(24,cases);
        Log.i("ZERO494_CAUSAL",
            "ORIGINAL_NEWBORN_RECURRENT_VS_BODY_CAUSAL_AUDIT_COMPLETE"+
            " comparisons="+cases+" physicalOnly=true noProductionMutation=true");
    }
}
