package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO501: physically replay the 256 ORIGINAL self-chosen V489/4747 actions
 * exactly as recorded by the user's phone, including their true durations.
 * Input heads come from original phone flight recorder, not scripted or
 * favorable same-direction laboratory tasks. These are counterfactual
 * perturbations on the exact same final saved body; no new neural actor,
 * game changes, object identities, synthetic COM thrust or file mutation.
 *
 * Experimental axial vs body-velocity damping comparisons. This test is
 * strictly research: heading/effect sequences are historical, and no 10k
 * autonomous policy can be certified by these offline physical replays.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero501RecordedSequentialMotorAndroidTest {
    static final float DT=1f/60f;
    static final int NMODES=13;
    static float limit(float x,float min,float max){
        return Math.max(min,Math.min(max,x));
    }
    static final class Sample{
        float x,y,dt;int n;
        Sample(float x,float y,float dt){
            float length=(float)Math.hypot(x,y);
            if(length<.9f||!Float.isFinite(length))
                throw new IllegalArgumentException("actual original action");
            this.x=x/length;this.y=y/length;this.dt=dt;
            this.n=Math.max(2,Math.round(dt/DT));
        }
    }
    static Sample[] originalFlight(JSONObject user)throws Exception{
        String raw=user.getString("motorRecorderCsv");
        String[] ls=raw.trim().split("\\n");
        assertTrue("Actual uploaded 256-decision flight required",ls.length>=257);
        ArrayList<Sample> result=new ArrayList<>();
        for(int i=1;i<ls.length;i++){
            String[] c=ls[i].split(",");
            if(c.length<13)continue;
            Sample s=new Sample(Float.parseFloat(c[1]),
                Float.parseFloat(c[2]),Float.parseFloat(c[11]));
            assertTrue(s.dt>0f&&s.dt<=.5f);
            result.add(s);
        }
        assertEquals(256,result.size());
        return result.toArray(new Sample[0]);
    }

    static final class Result{
        final int mode;float signed,gross,impulse,peakSpeed,peakStretch;
        int opposed,aligned,almost,decisions;
        Result(int mode){this.mode=mode;}
        float efficiency(){return signed/Math.max(.0001f,gross);}
    }
    static void mechanicalBodyController(int mode,float[] motor,
            MotorBrain.Body body,Morphology.Surface skin,Sample s,
            float[] filtered){
        float effort=.5f;
        float ux=s.x,uy=s.y,drive=.8f;
        float tgt=4f;
        float forward=body.cvx*ux+body.cvy*uy;
        float crossx=body.cvx-forward*ux,crossy=body.cvy-forward*uy;
        float cross=(float)Math.hypot(crossx,crossy);
        float br=0f;
        float governor=1f;
        switch(mode){
            case 2:drive=.80f;break;
            case 3:drive=.55f;break;
            case 4:drive=.35f;break;
            case 5:
                drive=.8f;
                governor=1f/(1f+Math.max(0f,forward-tgt*.5f)/(tgt*.7f));
                br=.15f;
                break;
            case 6:
                drive=.8f;
                governor=1f/(1f+Math.max(0f,forward-tgt*.5f)/(tgt*.7f));
                br=.30f;
                break;
            case 7:
                drive=.55f;
                governor=1f/(1f+Math.max(0f,forward-tgt*.50f)/(tgt*.70f));
                br=.22f;
                break;
            case 8:
                drive=.8f;
                governor=1f/(1f+Math.max(0f,(float)Math.hypot(
                    body.cvx,body.cvy)-8f)/8f);
                break;
            case 9:
                drive=.8f;
                governor=1f/(1f+Math.max(0f,(float)Math.hypot(
                    body.cvx,body.cvy)-5f)/5f);
                break;
            case 10:
                drive=.8f;
                governor=1f/(1f+Math.max(0f,(float)Math.hypot(
                    body.cvx,body.cvy)-11f)/10f);
                break;
            case 11:
                drive=.8f;
                governor=1f/(1f+Math.max(0f,forward-6f)/7f);
                break;
            case 12:
                drive=.8f;
                // Dampen both forward overshoot and orthogonal coasting
                // without changing the GRU-issued muscle target axis.
                governor=1f/(1f+Math.max(0f,forward-5f)/8f+
                    Math.max(0f,cross-7f)/8f);
                break;
            default:throw new IllegalArgumentException("mode "+mode);
        }
        drive*=governor;
        float crossGain=cross>.01f?Math.min(br,cross*.06f):0f;
        float targetX=drive*ux-(cross>.01f?crossGain*crossx/cross:0f);
        float targetY=drive*uy-(cross>.01f?crossGain*crossy/cross:0f);
        // Smooth only the corrective axis (not neural heading). The measured
        // true COM vector never becomes a second desired future destination.
        if(mode>=5&&mode<=7){
            float alpha=1f-(float)Math.exp(-DT/.30f);
            filtered[0]+=(targetX-filtered[0])*alpha;
            filtered[1]+=(targetY-filtered[1])*alpha;
            targetX=filtered[0];targetY=filtered[1];
        }
        for(int k=0;k<16;k++){
            int idx=k*4;
            float dx=0f,dy=0f;
            for(int j=k*6;j<k*6+6;j++){
                dx+=skin.x[j]-body.cx;
                dy+=skin.y[j]-body.cy;
            }
            float magnitude=(float)Math.hypot(dx,dy);
            if(magnitude<.01f)continue;
            dx/=magnitude;dy/=magnitude;
            float radial=dx*targetX+dy*targetY;
            float tangential=-dy*targetX+dx*targetY;
            motor[idx]=limit(radial+.12f*motor[idx],-1f,1f);
            motor[idx+1]=limit(tangential+.12f*motor[idx+1],-1f,1f);
            motor[idx+3]=limit(.12f*motor[idx+3],-1f,1f);
        }
    }

    static Result replay(JSONObject user,Sample[] samples,int mode)
            throws Exception{
        return replayWithTraction(user,samples,mode,-.04f);
    }
    static Result replayWithTraction(JSONObject user,Sample[] samples,
            int mode,float turnTraction)throws Exception{
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull(scene);assertNotNull(scene.matter);
        SymbioteMatterV1 matter=scene.matter;
        matter.enableOriginalZero3TractionV363();
        matter.configurePhysicalTurnBrakeV382(turnTraction);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        if(mode==1)cortex.enablePostureMotorTransducerV499();
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(cortex);
        long invalid=matter.invalid;
        float[] filtered={0f,0f};
        ArrayList<UnknownWorld.Entity> noWorld=new ArrayList<>();
        Result result=new Result(mode);
        for(int action=0;action<samples.length;action++){
            Sample recorded=samples[action];
            matter.observedOriginalMotorChangeV382(recorded.x,recorded.y);
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    501,action,cortex.revision(),recorded.x,
                    recorded.y,.5f,.5f,recorded.dt);
            control.begin(intent,scene.body,false,true,false,true,true);
            float sx=scene.body.cx,sy=scene.body.cy;
            for(int k=0;k<recorded.n;k++){
                float[] act=control.actuate(scene.body,scene.skin,DT);
                if(mode>=2)mechanicalBodyController(mode,act,
                    scene.body,scene.skin,recorded,filtered);
                float prev=matter.work;
                matter.step(scene.body,scene.skin,act,DT);
                float[] preX=scene.skin.x.clone(),preY=scene.skin.y.clone();
                Zero387TactileFrame.begin(scene.skin);
                SymbioteWorldStepV171.interior(matter,scene.body,scene.skin,
                    null,noWorld,DT,preX,preY);
                control.observe(scene.body,DT,Math.max(0f,matter.work-prev),
                    matter.lastMuscleReactionImpulse,false,false);
                result.impulse+=matter.lastMuscleReactionImpulse;
                float speed=(float)Math.hypot(scene.body.cvx,scene.body.cvy);
                result.peakSpeed=Math.max(result.peakSpeed,speed);
                assertTrue("Finite actual COM",Float.isFinite(speed));
                assertTrue("Bounded meaningful material velocity",speed<140f);
            }
            control.finish();
            float dx=scene.body.cx-sx,dy=scene.body.cy-sy;
            float gross=(float)Math.hypot(dx,dy);
            float toward=dx*recorded.x+dy*recorded.y;
            result.signed+=toward;
            result.gross+=gross;
            if(toward>.01f)result.aligned++;
            else if(toward<-.01f)result.opposed++;
            else result.almost++;
            result.decisions++;
        }
        assertEquals("Original material must stay topologically valid",
            invalid,matter.invalid);
        assertEquals(1871L,cortex.updates());
        return result;
    }

    @Test(timeout=5400000)
    public void original256AbruptVoluntaryTurnsValidateMechanicalCourseTracking()
            throws Exception {
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        Sample[] samples=originalFlight(user);
        Result[] results=new Result[NMODES];
        for(int mode=0;mode<NMODES;mode++){
            Result outcome=replay(user,samples,mode);
            results[mode]=outcome;
            Log.i("ZERO501_TRACE",String.format(Locale.US,
                "mode=%d realDecisions=%d signed=%.7f gross=%.7f "+
                "efficiency=%.7f aligned=%d opposed=%d almost=%d "+
                "peakSpeed=%.4f realImpulse=%.3f",
                mode,outcome.decisions,outcome.signed,outcome.gross,
                outcome.efficiency(),outcome.aligned,outcome.opposed,
                outcome.almost,outcome.peakSpeed,outcome.impulse));
        }
        float best=-Float.MAX_VALUE;int bestMode=-1;
        for(int mode=1;mode<NMODES;mode++){
            if(results[mode].efficiency()>best){
                best=results[mode].efficiency();bestMode=mode;
            }
        }
        Log.i("ZERO501_TRACE",String.format(Locale.US,
            "ORIGINAL_USER_256_TURNS_PHYSICAL_AB_COMPLETE "+
            "originalEfficiency=%.7f v499Efficiency=%.7f "+
            "bestCandidate=%d bestEfficiency=%.7f "+
            "noAutonomousOrHazardClaim=true",results[0].efficiency(),
            results[1].efficiency(),bestMode,best));
    }
}
