package com.overmind.zero;

import android.content.Context;
import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Restore the exact user-uploaded V381 cortex and frozen peripheral, then
 * run matched real 170-node EAST->WEST/NORTH trials using physical traction
 * only. Exact V381 brain and old motor weights are never written by this test. */
@RunWith(AndroidJUnit4.class)
public final class Zero382UserSavedBodyBrakeAndroidTest {
    static class Result{
        float firstX,firstY,fullX,fullY,warmX,work;
    }
    static Result run(String brain,String motor,float drag,int bearing)throws Exception{
        Zero381UserCheckpointClosedLoopAndroidTest.Sim s=
            new Zero381UserCheckpointClosedLoopAndroidTest.Sim(brain,motor);
        s.gel.configurePhysicalTurnBrakeV382(drag);
        Result result=new Result();
        for(int i=0;i<20;i++){
            int direction=i<12?0:bearing;
            float a=(float)(direction*Math.PI/4);
            float[] delta=s.decision((float)Math.cos(a),
                                     (float)Math.sin(a),null,false,false);
            if(i<12)result.warmX+=delta[0];
            else{
                if(i==12){result.firstX=delta[0];result.firstY=delta[1];}
                result.fullX+=delta[0];result.fullY+=delta[1];
            }
        }
        result.work=s.gel.work;
        return result;
    }
    @Test public void exactUploaded381CortexMeasuredTurningAndSafeResume()throws Exception{
        Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        File file=new File(ctx.getExternalFilesDir(null),"zero382-uploaded-checkpoint.json");
        assertTrue("Missing explicit user's real saved cortex",file.isFile());
        JSONObject o=new JSONObject(new String(Files.readAllBytes(file.toPath()),
            StandardCharsets.UTF_8));
        String brain=o.getString("brain"),motor=o.getString("motorHead");
        String calib=o.getString("calibration"),flight=o.getString("flight");
        Zero3OriginalBrainV356 restored=Zero3OriginalBrainV356.load(ctx,brain,true);
        assertEquals("Must test actual 9997-decision V381 user",9997,restored.decisions());
        String sameBefore=restored.checkpoint();
        restored.restoreMotorObservationsV381(calib,flight);
        assertEquals(sameBefore,restored.checkpoint());
        assertEquals("Original flight must restore exactly",9997,restored.flightCountV381());
        // Reproduce the screenshot's failure on the *same* authentic neural
        // checkpoint, proving invalid auxiliary data cannot block recovery.
        restored.restoreMotorObservationsV381("garbled","bad flight\n1,2");
        assertEquals(sameBefore,restored.checkpoint());
        restored.close();

        StringBuilder report=new StringBuilder("ZERO382_EXACT_USER381_MATERIAL_BRAKE_AB\n");
        for(float drag:new float[]{0f,-.02f,-.04f,-.06f,.02f}){
            Result east=run(brain,motor,drag,0),
                   west=run(brain,motor,drag,4),
                   north=run(brain,motor,drag,2);
            report.append(String.format(Locale.US,
                "tractionDelta=%.3f warmEast=%.5f straightEast=%.5f WESTfirst=%.5f WEST8=%.5f NORTHfirstY=%.5f NORTH8Y=%.5f work=%.7f%n",
                drag,east.warmX,east.fullX,west.firstX,west.fullX,
                north.firstY,north.fullY,west.work));
            assertEquals("A/B must warm identically before a change",
                east.warmX,west.warmX,1e-3f);
            assertTrue(Float.isFinite(west.fullX));
        }
        Bundle b=new Bundle();
        b.putString("stream",report.toString());
        InstrumentationRegistry.getInstrumentation().sendStatus(0,b);
        System.out.println(report);
    }
}