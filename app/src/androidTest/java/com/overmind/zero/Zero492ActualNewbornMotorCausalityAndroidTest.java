package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Research-only SAME-PHYSICAL-SNAPSHOT motor A/B using the user's Nov 7
 * 2026 V489 newly shared 4,747-decision newborn, NOT the old 33k S21.
 * The archived live 170-site posture, all material velocities and actual
 * 512 synapses/64 slow motor states are restored afresh for every trial.
 * No neural weights, player save, rewards, or real app are modified.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero492ActualNewbornMotorCausalityAndroidTest {
    private static final float DT=1f/60f;
    private static final int[] CHANNELS={0,1,3};

    static JSONObject realNewborn()throws Exception{
        File file=new File(InstrumentationRegistry.getInstrumentation()
            .getTargetContext().getExternalFilesDir(null),
            "zero492-nov07-newborn-readonly.json");
        assertTrue("Missing 2026-11-07 live saved newborn physical fixture",
            file.isFile());
        JSONObject p=new JSONObject(new String(Files.readAllBytes(
            file.toPath()),StandardCharsets.UTF_8));
        assertEquals("20261107-105525-1972ed240cfcd",p.getString("reportId"));
        return p;
    }
    static Zero3LearningFoundation.AdaptiveCortex cortex(JSONObject user)
            throws Exception{
        Zero3OriginalBrainV356.Readout s=
            Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
        assertEquals("Exact newly shared 3.0 brain required",4747L,s.decisions);
        java.lang.reflect.Field f=s.getClass().getDeclaredField("cortex");
        f.setAccessible(true);
        Zero3LearningFoundation.AdaptiveCortex c=
            (Zero3LearningFoundation.AdaptiveCortex)f.get(s);
        assertEquals(1871L,c.updates());
        c.enablePhysicalContextV412();
        return c;
    }
    static final class Outcome{
        final float preVx,preVy,dx,dy,dvx,dvy,ax,ay,impulse;
        final long newInvalid;
        Outcome(float preVx,float preVy,float dx,float dy,float dvx,
                float dvy,float ax,float ay,float impulse,long newInvalid){
            this.preVx=preVx;this.preVy=preVy;this.dx=dx;this.dy=dy;
            this.dvx=dvx;this.dvy=dvy;this.ax=ax;this.ay=ay;
            this.impulse=impulse;this.newInvalid=newInvalid;
        }
        float signedVelocity(int heading){
            return project(dvx,dvy,heading);
        }
        float signedMotion(int heading){
            return project(dx,dy,heading);
        }
        float signedFirst(int heading){
            return project(ax,ay,heading);
        }
    }
    static float project(float x,float y,int heading){
        switch(heading){
            case 0:return x;
            case 1:return y;
            case 2:return -x;
            case 3:return -y;
            default:throw new IllegalArgumentException("heading");
        }
    }

    static Outcome run(JSONObject user,int heading,int mode,
            int sector,int channel,int sign)throws Exception {
        // Restore user-supplied real saved free-world material (without walls).
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull("User's live physical 170-site snapshot must restore",s);
        assertNotNull(s.matter);
        assertNotNull(s.skin);
        SymbioteMatterV1 gel=s.matter;
        gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex trained=cortex(user);
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(trained);
        final float ux=heading==0?1f:heading==2?-1f:0f;
        final float uy=heading==1?1f:heading==3?-1f:0f;
        Zero3LearningFoundation.MotorIntent intent=
            new Zero3LearningFoundation.MotorIntent(492,0,trained.revision(),
                ux,uy,.5f,.5f,.4f);
        control.begin(intent,s.body,false,true,false,true,true);
        float startX=s.body.cx,startY=s.body.cy;
        float preVx=s.body.cvx,preVy=s.body.cvy;
        long oldInvalid=gel.invalid;
        float firstX=Float.NaN,firstY=Float.NaN,impulse=0f;
        ArrayList<UnknownWorld.Entity> noObjects=new ArrayList<>();
        for(int tick=0;tick<24;tick++){
            float[] cmd=control.actuate(s.body,s.skin,DT);
            if(mode==0){
                for(int site=0;site<16;site++)
                    for(int ch:CHANNELS)cmd[site*4+ch]=0f;
            }else if(mode==2){
                assertTrue(sector>=0&&sector<16);
                assertTrue(channel==0||channel==1||channel==3);
                int at=sector*4+channel;
                cmd[at]=Math.max(-1f,Math.min(1f,cmd[at]+.40f*sign));
            }
            float prevVx=s.body.cvx,prevVy=s.body.cvy,previous=gel.work;
            gel.step(s.body,s.skin,cmd,DT);
            float[] gelX=s.skin.x.clone(),gelY=s.skin.y.clone();
            Zero387TactileFrame.begin(s.skin);
            SymbioteWorldStepV171.interior(gel,s.body,s.skin,null,
                noObjects,DT,gelX,gelY);
            if(tick==0){
                firstX=(s.body.cvx-prevVx)/DT;
                firstY=(s.body.cvy-prevVy)/DT;
            }
            control.observe(s.body,DT,Math.max(0f,gel.work-previous),
                gel.lastMuscleReactionImpulse,false,false);
            impulse+=gel.lastMuscleReactionImpulse;
            assertTrue("No nonfinite COM",Float.isFinite(s.body.cvx));
        }
        control.finish();
        return new Outcome(preVx,preVy,s.body.cx-startX,s.body.cy-startY,
            s.body.cvx-preVx,s.body.cvy-preVy,firstX,firstY,impulse,
            gel.invalid-oldInvalid);
    }

    @Test(timeout=1500000)
    public void liveNewbornActualPhysicalPostureMeasuresFourDirectionsAndWrongSignMuscles()
            throws Exception{
        JSONObject user=realNewborn();
        int noAuthority=0,opposite=0,toward=0,totalTrials=0;
        for(int heading=0;heading<4;heading++){
            Outcome passive=run(user,heading,0,-1,-1,0);
            Outcome active=run(user,heading,1,-1,-1,0);
            float deltaV=active.signedVelocity(heading)-
                passive.signedVelocity(heading);
            float first=active.signedFirst(heading)-
                passive.signedFirst(heading);
            float deltaMotion=active.signedMotion(heading)-
                passive.signedMotion(heading);
            assertEquals(0L,passive.newInvalid);
            assertEquals(0L,active.newInvalid);
            assertTrue(Float.isFinite(deltaV));
            if(deltaV>.0001f)toward++;
            else if(deltaV<-.0001f)opposite++;
            else noAuthority++;
            totalTrials+=2;
            Log.i("ZERO492_NEWBORN",String.format(Locale.US,
                "ORIGINAL_USER_4747 heading=%d preV=(%.5f,%.5f) "+
                "passiveMotion=%.7f activeMotion=%.7f netMuscleMotion=%.7f "+
                "netMuscleDV=%.7f firstFrameNetMuscleAccel=%.7f impulse=%.4f",
                heading,active.preVx,active.preVy,
                passive.signedMotion(heading),active.signedMotion(heading),
                deltaMotion,deltaV,first,active.impulse));
        }
        int measurableWestNorth=0,wrongSignWestNorth=0;
        for(int heading:new int[]{2,3}){
            Outcome full=run(user,heading,1,-1,-1,0);
            float min=Float.POSITIVE_INFINITY,max=Float.NEGATIVE_INFINITY;
            float firstMin=Float.POSITIVE_INFINITY,firstMax=Float.NEGATIVE_INFINITY;
            int beneficial=0,harmful=0;
            for(int sector=0;sector<16;sector++){
                for(int channel:CHANNELS){
                    Outcome plus=run(user,heading,2,sector,channel,1);
                    Outcome minus=run(user,heading,2,sector,channel,-1);
                    float effect=plus.signedVelocity(heading)-
                        minus.signedVelocity(heading);
                    float firstEffect=plus.signedFirst(heading)-
                        minus.signedFirst(heading);
                    assertTrue(Float.isFinite(effect));
                    assertTrue(Float.isFinite(firstEffect));
                    min=Math.min(min,effect);max=Math.max(max,effect);
                    firstMin=Math.min(firstMin,firstEffect);
                    firstMax=Math.max(firstMax,firstEffect);
                    if(Math.abs(effect)>.0001f)measurableWestNorth++;
                    if(effect>.0001f)beneficial++;
                    else if(effect<-.0001f){harmful++;wrongSignWestNorth++;}
                    totalTrials+=2;
                }
            }
            Log.i("ZERO492_NEWBORN",String.format(Locale.US,
                "ORIGINAL_USER_4747_SINGLE_MUSCLE heading=%d realControlDV=%.7f "+
                "beneficialLocalSigns=%d harmfulLocalSigns=%d "+
                "dVRange=[%.7f,%.7f] firstFrameRange=[%.7f,%.7f]",
                heading,full.signedVelocity(heading),beneficial,harmful,
                min,max,firstMin,firstMax));
        }
        assertEquals(200,totalTrials);
        Log.i("ZERO492_NEWBORN",
            "ORIGINAL_USER_4747_PHYSICAL_CAUSALITY_COMPLETE totalTrials="+
            totalTrials+" fourHeadingsGood="+toward+" fourHeadingsBad="+opposite+
            " fourHeadingsNegligible="+noAuthority+
            " nonzeroMuscleDerivativesWestNorth="+measurableWestNorth+
            " oppositeSignMusclesWestNorth="+wrongSignWestNorth+
            " readOnly=true autonomousBehaviorNotProven=true");
    }
}
