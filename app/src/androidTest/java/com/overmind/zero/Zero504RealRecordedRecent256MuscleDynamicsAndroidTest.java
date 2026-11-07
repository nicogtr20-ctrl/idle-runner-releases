package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO504: true user's 256 most recent V489 GRU3 self-selected motor intents
 * (decision IDs 4491..4746) REPLAYED as research stimuli on exactly the saved
 * Nov 7 170-site final body, with SAME saved 512 motor synapses and local
 * cortex states. This is NOT a real 256-decision autonomous continuation:
 * the chosen old sequence was recorded in different historic physical
 * states. It isolates motor/tissue response to actual self-generated heading
 * switching, with no new actor, reward or fake COM impulse.
 *
 * V499 physical tendon transducer stays present. Only actual physical
 * muscle lowpass constants vary in controlled arms; CH2 grip is untouched.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero504RealRecordedRecent256MuscleDynamicsAndroidTest {
    static final float DT=1f/60f;
    private static final int[] CH={0,1,3};
    static JSONArray recorded()throws Exception{
        File p=new File(InstrumentationRegistry.getInstrumentation()
            .getTargetContext().getExternalFilesDir(null),
            "zero504-actual-recent256.json");
        assertTrue("Actual last 256 decision fixture required",p.isFile());
        JSONObject data=new JSONObject(new String(Files.readAllBytes(
            p.toPath()),StandardCharsets.UTF_8));
        assertEquals("20261107-105525-1972ed240cfcd",data.getString("reportId"));
        JSONArray trace=data.getJSONArray("decisions");
        assertEquals(256,trace.length());
        return trace;
    }
    static final class Report{
        final int count,opposed,aligned;
        final float signed,gross,endVx,endVy,impulse,seconds;
        final long invalid;
        Report(int count,int opposed,int aligned,float signed,float gross,
               float endVx,float endVy,float impulse,float seconds,long invalid){
            this.count=count;this.opposed=opposed;this.aligned=aligned;
            this.signed=signed;this.gross=gross;this.endVx=endVx;
            this.endVy=endVy;this.impulse=impulse;this.seconds=seconds;
            this.invalid=invalid;
        }
    }
    static Report replay(JSONObject user,JSONArray trace,float tau)throws Exception{
        return replay(user,trace,tau,false);
    }
    static Report replay(JSONObject user,JSONArray trace,float tau,
                         boolean useProductionMuscleDynamicsV507)throws Exception{
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull(s);assertNotNull(s.matter);
        s.matter.enableOriginalZero3TractionV363();
        s.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        cortex.enablePostureMotorTransducerV499();
        if(useProductionMuscleDynamicsV507){
            if(Math.abs(tau-.50f)>.00001f)
                throw new IllegalArgumentException("V507 production physical tau must be 0.5s");
            cortex.enableMuscleActivationDynamicsLabV507();
        }
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(cortex);
        float[] previous=new float[64];
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        float signed=0f,gross=0f,impulse=0f,seconds=0f;
        int opposed=0,aligned=0;
        int firstId=4491;
        long beforeInvalid=s.matter.invalid;
        float smooth=useProductionMuscleDynamicsV507||tau<=0f?
            1f:1f-(float)Math.exp(-DT/tau);
        for(int action=0;action<trace.length();action++){
            JSONArray d=trace.getJSONArray(action);
            int id=d.getInt(0);
            assertEquals("Keep precisely original adjacent 256 decisions",firstId+action,id);
            float ux=(float)d.getDouble(1);
            float uy=(float)d.getDouble(2);
            float directionLen=(float)Math.hypot(ux,uy);
            assertTrue(directionLen>.90f);
            ux/=directionLen;uy/=directionLen;
            float duration=(float)d.getDouble(3);
            assertTrue("Live first/last death-boundary decisions may be shorter",duration>0f&&duration<=.7f);
            int frames=Math.max(1,Math.min(42,Math.round(duration/DT)));
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    504,action,cortex.revision(),ux,uy,.5f,.5f,.4f);
            control.begin(intent,s.body,false,true,false,true,true);
            float prevX=s.body.cx,prevY=s.body.cy;
            for(int tick=0;tick<frames;tick++){
                float[] out=control.actuate(s.body,s.skin,DT);
                for(int sector=0;sector<16;sector++)
                    for(int ch:CH){
                        int at=4*sector+ch;
                        float delivered=previous[at]+
                            smooth*(out[at]-previous[at]);
                        previous[at]=delivered;
                        out[at]=delivered;
                    }
                float before=s.matter.work;
                s.matter.step(s.body,s.skin,out,DT);
                float[] gelX=s.skin.x.clone(),gelY=s.skin.y.clone();
                Zero387TactileFrame.begin(s.skin);
                SymbioteWorldStepV171.interior(s.matter,s.body,s.skin,null,
                    empty,DT,gelX,gelY);
                control.observe(s.body,DT,
                    Math.max(0f,s.matter.work-before),
                    s.matter.lastMuscleReactionImpulse,false,false);
                impulse+=s.matter.lastMuscleReactionImpulse;
                seconds+=DT;
                assertTrue(Float.isFinite(s.body.cx));
                assertTrue(Float.isFinite(s.body.cvx));
            }
            control.finish();
            float dx=s.body.cx-prevX,dy=s.body.cy-prevY;
            float part=dx*ux+dy*uy;
            signed+=part;
            gross+=(float)Math.hypot(dx,dy);
            if(part>.005f)aligned++;
            else if(part<-.005f)opposed++;
        }
        return new Report(256,opposed,aligned,signed,gross,s.body.cvx,s.body.cvy,
            impulse,seconds,s.matter.invalid-beforeInvalid);
    }
    @Test(timeout=1500000)
    public void exactRecordedOriginalGRUTraceDistinguishesFastMuscleOscillation()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        JSONArray trace=recorded();
        int tested=0;
        for(float tau:new float[]{0f,.10f,.25f,.50f}){
            Report result=replay(user,trace,tau);
            double efficiency=result.signed/Math.max(1d,result.gross);
            assertEquals(256,result.count);
            assertTrue(Double.isFinite(efficiency));
            Log.i("ZERO504_DYNAMIC",String.format(Locale.US,
                "ACTUAL_GRU_RECENT256_REPLAY tau=%.2f originalDecisions=%d "+
                "aligned=%d opposed=%d signed=%.6f gross=%.6f "+
                "efficiency=%.7f physicalEndV=(%.5f,%.5f) "+
                "physicalImpulse=%.3f realSeconds=%.3f newInvalid=%d",
                tau,result.count,result.aligned,result.opposed,
                result.signed,result.gross,efficiency,
                result.endVx,result.endVy,
                result.impulse,result.seconds,result.invalid));
            tested++;
        }
        assertEquals(4,tested);
        Log.i("ZERO504_DYNAMIC",
            "EXACT_USER_RECORDED_GRU256_REAL_MUSCLE_TIMESCALE_DIAGNOSTIC_COMPLETE"+
            " notAutonomousContinuation=true researchOnly=true");
    }
}
