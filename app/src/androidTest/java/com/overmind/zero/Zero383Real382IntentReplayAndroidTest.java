package com.overmind.zero;

import android.content.Context;
import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import static org.junit.Assert.*;

/** Read-only, matched comparison of V382 floor traction using all 256
 * ACTUAL continuous GRU3 heading/duration intentions from user's latest
 * Android upload. The original V382 cortex, old low-level motor weights,
 * V363 traction, 96+64+10 nodes and body physics are unmodified. This
 * is a physical actuator-identification test, not a substitute for a
 * complete game run with energy/weather/obstacles. */
@RunWith(AndroidJUnit4.class)
public final class Zero383Real382IntentReplayAndroidTest {
    private static final float DT=1f/60f;
    static final class Result {
        int forward,against,neutral,hardTurns,hardForward,hardAgainst;
        double signed,gross,hardSigned,work,earlySign;
        double meanTransient;long frames;
    }
    static Result trial(String brain,String frozen,String recorder,float turnTraction)
        throws Exception {
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero381UserCheckpointClosedLoopAndroidTest.cortex(brain);
        long originalUpdates=cortex.updates();
        Zero3LearningFoundation.Coordinator motor=
            new Zero3LearningFoundation.Coordinator(cortex);
        SymbioteMotorHeadV1 old=new SymbioteMotorHeadV1();
        assertTrue("Original same-save peripheral not restored",old.restore(frozen));
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface surface=new Morphology.Surface(body);
        SymbioteMatterV1 tissue=new SymbioteMatterV1();
        tissue.reset(body,surface);
        tissue.enableOriginalZero3TractionV363();
        tissue.configurePhysicalTurnBrakeV382(turnTraction);
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        String[] lines=recorder.trim().split("\n");
        assertEquals("Use full real flight from the latest upload",257,lines.length);
        assertTrue(lines[0].startsWith("zmf381|1|10294"));
        Result out=new Result();
        float px=Float.NaN,py=Float.NaN;
        for(int i=1;i<lines.length;i++){
            String[] values=lines[i].split(",",-1);
            assertEquals("unmodified flight column schema",28,values.length);
            float x=Float.parseFloat(values[1]),y=Float.parseFloat(values[2]);
            float duration=Float.parseFloat(values[11]);
            // The read-only flight logs angles rounded to 3 decimals.
            // Normalize quantized logger coordinates back to a unit vector;
            // the genuine GRU owner issued normalized headings originally.
            float norm=(float)Math.hypot(x,y);
            assertTrue("saved GRU choice must be physical",norm>.97&&norm<1.03);
            x/=norm;y/=norm;
            int steps=Math.round(duration/DT);
            // A real life termination can finish a decision early; recorder
            // ID10111 has a valid physically observed 0.117-second interval.
            // Replay such authentic shortened decisions too, do not silently
            // replace the user's true actions with laboratory 0.4s actions.
            assertTrue("Authentic variable-duration physical episode",steps>=1&&steps<=40);
            boolean hard=Float.isFinite(px)&&Math.atan2(
                Math.abs(px*y-py*x),px*x+py*y)>Math.PI/2;
            if(hard)out.hardTurns++;
            Zero3LearningFoundation.MotorIntent action=
                new Zero3LearningFoundation.MotorIntent(1L,i,
                    cortex.revision(),x,y,.5f,.5f,steps*DT);
            motor.begin(action,body,false,true,true,true,true);
            Zero3MotorPeripheralBridgeV364.begin(old,body,surface,
                null,empty,x,y);
            tissue.observedOriginalMotorChangeV382(x,y);
            double dx=0,dy=0;
            for(int frame=0;frame<steps;frame++){
                float beforeX=body.cx,beforeY=body.cy,preWork=tissue.work;
                float[] muscles=motor.actuate(body,surface,DT);
                Zero3MotorPeripheralBridgeV364.fastPhysicalRefine(
                    muscles,old,body,surface,DT);
                tissue.step(body,surface,muscles,DT);
                dx+=body.cx-beforeX;dy+=body.cy-beforeY;
                out.meanTransient+=tissue.transientBrakeV382();
                out.frames++;
                motor.observe(body,DT,Math.max(0f,tissue.work-preWork),
                    tissue.lastMuscleReactionImpulse,false,false);
            }
            motor.finish();
            double signed=dx*x+dy*y,gross=Math.hypot(dx,dy);
            out.signed+=signed;out.gross+=gross;
            if(signed>.2)out.forward++;
            else if(signed<-.2)out.against++;
            else out.neutral++;
            if(hard){
                out.hardSigned+=signed;
                if(signed>.2)out.hardForward++;
                if(signed<-.2)out.hardAgainst++;
            }
            px=x;py=y;
        }
        out.work=tissue.work;
        out.meanTransient/=out.frames;
        assertEquals(originalUpdates,cortex.updates());
        assertTrue(out.work>0);
        return out;
    }
    @Test public void compareSameRealUser382DecisionsSameSavedOriginalMatter()throws Exception{
        Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        File f=new File(ctx.getExternalFilesDir(null),"zero383-user382-readonly.json");
        assertTrue("Exact user-uploaded 382 profile must be present",f.isFile());
        JSONObject input=new JSONObject(new String(Files.readAllBytes(f.toPath()),
            StandardCharsets.UTF_8));
        String brain=input.getString("brain"),frozen=input.getString("motorHead");
        String recorder=input.getString("flight");
        Zero3OriginalBrainV356 brainOwner=Zero3OriginalBrainV356.load(ctx,brain,true);
        assertEquals("Same persisted 382 brain, not reset",10294,brainOwner.decisions());
        brainOwner.close();
        StringBuilder log=new StringBuilder("ZERO383_USER382_REAL_INTENT_REPLAY\n");
        Result reference=null;
        for(float gain:new float[]{0f,-.02f,-.04f,-.06f}){
            Result a=trial(brain,frozen,recorder,gain);
            if(reference==null)reference=a;
            assertEquals("All controls must use same brain action sequence",reference.hardTurns,a.hardTurns);
            log.append(String.format(Locale.US,
                "turnDelta=%.3f n=%d hard=%d forward=%d against=%d signed=%.4f gross=%.4f signedEfficiency=%.4f hardSigned=%.4f hardForward=%d hardAgainst=%d meanTractionDelta=%.5f metabolicWork=%.7f%n",
                gain,a.forward+a.against+a.neutral,a.hardTurns,
                a.forward,a.against,a.signed,a.gross,
                a.signed/Math.max(.00001,a.gross),
                a.hardSigned,a.hardForward,a.hardAgainst,
                a.meanTransient,a.work));
        }
        Bundle status=new Bundle();status.putString("stream",log.toString());
        InstrumentationRegistry.getInstrumentation().sendStatus(0,status);
        System.out.print(log);
    }
}