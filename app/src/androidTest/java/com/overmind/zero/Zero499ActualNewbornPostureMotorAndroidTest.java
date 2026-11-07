package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO499 actual Nov7 4747-decision 170-particle body and cortex integration:
 * source-proven V498 anatomical mapping plus measured COM velocity feedback.
 * No invented resource heading, output thrust, 2.2 controller or reset of
 * saved 512 synapses/64 slow states; V319 CH2 bit-for-bit unchanged.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero499ActualNewbornPostureMotorAndroidTest {
    static final float DT=1f/60f;

    static final class Trial{
        final float vx,vy,dx,dy,impulse;
        Trial(float vx,float vy,float dx,float dy,float impulse){
            this.vx=vx;this.vy=vy;this.dx=dx;this.dy=dy;
            this.impulse=impulse;
        }
        float forwardSpeed(int h){
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(vx,vy,h);
        }
        float forwardMotion(int h){
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(dx,dy,h);
        }
    }
    static Trial actual(JSONObject user,int heading,boolean v499,int decisions)
            throws Exception {
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull(s);assertNotNull(s.matter);
        s.matter.enableOriginalZero3TractionV363();
        s.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        float[] original512=cortex.weights();
        if(v499)cortex.enablePostureMotorTransducerV499();
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(cortex);
        float ix=heading==0?1f:heading==2?-1f:0f;
        float iy=heading==1?1f:heading==3?-1f:0f;
        float startX=s.body.cx,startY=s.body.cy,imp=0f;
        long initialInvalid=s.matter.invalid;
        ArrayList<UnknownWorld.Entity> noWorld=new ArrayList<>();
        for(int choice=0;choice<decisions;choice++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    499,choice,cortex.revision(),ix,iy,.5f,.5f,.4f);
            control.begin(intent,s.body,false,true,false,true,true);
            for(int tick=0;tick<24;tick++){
                float[] commanded=control.actuate(s.body,s.skin,DT);
                float before=s.matter.work;
                s.matter.step(s.body,s.skin,commanded,DT);
                float[] gx=s.skin.x.clone(),gy=s.skin.y.clone();
                Zero387TactileFrame.begin(s.skin);
                SymbioteWorldStepV171.interior(s.matter,s.body,s.skin,null,
                    noWorld,DT,gx,gy);
                control.observe(s.body,DT,
                    Math.max(0f,s.matter.work-before),
                    s.matter.lastMuscleReactionImpulse,false,false);
                imp+=s.matter.lastMuscleReactionImpulse;
                assertTrue("Actual COM finite",Float.isFinite(s.body.cvx));
            }
            control.finish();
        }
        assertEquals("Corrected 170-site physics remains topologically valid",
            initialInvalid,s.matter.invalid);
        assertEquals("No 512-weight mutation without physical learning",
            1871L,cortex.updates());
        assertArrayEquals("All 512 saved motor synapses preserved exactly",
            original512,cortex.weights(),0f);
        return new Trial(s.body.cvx,s.body.cvy,s.body.cx-startX,
            s.body.cy-startY,imp);
    }

    @Test(timeout=1500000)
    public void originalNov7BodyCanActuallyFollowAllFourGRUHeadings()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        int directional=0;
        for(int heading=0;heading<4;heading++){
            Trial original=actual(user,heading,false,8);
            Trial rescued=actual(user,heading,true,8);
            float v=rescued.forwardSpeed(heading);
            Log.i("ZERO499_MOTOR",String.format(Locale.US,
                "heading=%d oldFinal=%.7f newFinal=%.7f "+
                "oldForwardTravel=%.7f newForwardTravel=%.7f "+
                "newWorldV=(%.7f,%.7f) newImpulse=%.4f",
                heading,original.forwardSpeed(heading),v,
                original.forwardMotion(heading),rescued.forwardMotion(heading),
                rescued.vx,rescued.vy,rescued.impulse));
            if(v>0f)directional++;
            if(heading==2||heading==3){
                assertTrue("ZERO499 must genuinely reverse previously wrong-way "+
                    "COM velocity on saved body h="+heading+" got "+v,v>.40f);
                assertTrue("ZERO499 must have actual forward COM displacement "+
                    "after 8 real decisions h="+heading,
                    rescued.forwardMotion(heading)>0f);
            }
        }
        assertEquals("All four issued V3 headings must be physically reachable",
            4,directional);
        Log.i("ZERO499_MOTOR","ACTUAL_ORIGINAL_4747_POSTURE_SERVO_4_DIR_PASS");
    }

    @Test(timeout=1500000)
    public void originalCH2GripAndZeroEffortRemainUntouchedByPhysicalTendon()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        Zero3LearningFoundation.AdaptiveCortex old=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        Zero3LearningFoundation.AdaptiveCortex corrected=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        corrected.enablePostureMotorTransducerV499();
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull(s);
        for(float effort:new float[]{0f,.5f}){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    499,17+Math.round(effort*10),old.revision(),
                    -1f,0f,effort,.8f,.4f);
            old.begin(false,intent,true);
            corrected.begin(false,intent,true);
            float[] baseline=old.command(intent,s.body,s.skin,DT);
            float[] mapped=corrected.command(intent,s.body,s.skin,DT);
            for(int sector=0;sector<16;sector++){
                int i=sector*4;
                assertEquals("CH2 external V319 remains bit-identical",
                    baseline[i+2],mapped[i+2],0f);
                if(effort==0f){
                    assertEquals(0f,mapped[i],0f);
                    assertEquals(0f,mapped[i+1],0f);
                    assertEquals(0f,mapped[i+3],0f);
                }
            }
            old.discard();corrected.discard();
        }
        Log.i("ZERO499_MOTOR","EXTERNAL_V319_GRIP_AND_ZERO_EFFORT_PASS");
    }
}
