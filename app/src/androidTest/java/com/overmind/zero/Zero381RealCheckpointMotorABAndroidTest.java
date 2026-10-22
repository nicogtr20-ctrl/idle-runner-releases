package com.overmind.zero;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.json.JSONObject;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import static org.junit.Assert.*;

/**
 * READ-ONLY independent motor A/B using the REAL saved 380 cortex and motor
 * peripheral. Controls aren't used by GameActivity. No preferences, checkpoint
 * or signed APK is ever mutated or published by this scientific fixture.
 *
 * Tests the SAME original 170-point gel after a matched physical warm-up.
 * A GRU-chosen heading is held constant only in this motor-identification
 * test. Trial candidates: 0, .25, .75, 1.0 old frozen motor share.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero381RealCheckpointMotorABAndroidTest {
    private static final float DT=1f/60f;
    private static final int FRAMES=24,WARM=12,AFTER=8;
    private static final ArrayList<UnknownWorld.Entity> NO_SOLIDS=new ArrayList<>();

    private static final class Trial {
        final float share;final int afterDir;
        double eastWarmup,postFirstX,postFirstY,postEightX,postEightY,totalWork;
        float beforeVx,beforeVy,endVx,endVy;
        Trial(float share,int afterDir){this.share=share;this.afterDir=afterDir;}
    }
    private static Zero3LearningFoundation.AdaptiveCortex savedCortex(String saved)
            throws Exception {
        Zero3OriginalBrainV356.Readout state=Zero3OriginalBrainV356.inspectSaved(saved);
        Field f=Zero3OriginalBrainV356.Readout.class.getDeclaredField("cortex");
        f.setAccessible(true);
        return (Zero3LearningFoundation.AdaptiveCortex)f.get(state);
    }
    private static Trial trial(String checkpoint,String peripheralWeights,
                               float share,int afterDirection)throws Exception {
        Zero3LearningFoundation.AdaptiveCortex cortex=savedCortex(checkpoint);
        long beforeUpdates=cortex.updates();
        Zero3LearningFoundation.Coordinator motor=new Zero3LearningFoundation.Coordinator(cortex);
        SymbioteMotorHeadV1 peripheral=new SymbioteMotorHeadV1();
        assertTrue("Original motor weights did not restore",peripheral.restore(peripheralWeights));
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(body,skin);
        gel.enableOriginalZero3TractionV363();
        Trial result=new Trial(share,afterDirection);
        for(int d=0;d<WARM+AFTER;d++){
            boolean warm=d<WARM;
            int direction=warm?0:afterDirection;
            double angle=direction*Math.PI/4d;
            float directionX=(float)Math.cos(angle),directionY=(float)Math.sin(angle);
            Zero3LearningFoundation.MotorIntent choice=
                new Zero3LearningFoundation.MotorIntent(
                    1L,d,cortex.revision(),directionX,directionY,.5f,.5f,FRAMES*DT);
            motor.begin(choice,body,false,true,true,true,true);
            Zero3MotorPeripheralBridgeV364.begin(peripheral,body,skin,
                null,NO_SOLIDS,directionX,directionY);
            if(d==WARM){result.beforeVx=body.cvx;result.beforeVy=body.cvy;}
            double decisionX=0,decisionY=0;
            for(int t=0;t<FRAMES;t++){
                float[] actual=motor.actuate(body,skin,DT);
                peripheral.fastTickOriginalV370(body,skin,DT);
                float[] frozen=peripheral.commands();
                for(int site=0;site<SymbioteMatterV1.SECTORS;site++)
                    for(int ch:new int[]{0,1,3}){
                        int k=site*4+ch;
                        actual[k]=Math.max(-1f,Math.min(1f,
                            (1f-share)*actual[k]+share*frozen[k]));
                    }
                float px=body.cx,py=body.cy,oldWork=gel.work;
                gel.step(body,skin,actual,DT);
                double x=body.cx-px,y=body.cy-py;
                decisionX+=x;decisionY+=y;
                float work=Math.max(0f,gel.work-oldWork);
                result.totalWork+=work;
                motor.observe(body,DT,work,gel.lastMuscleReactionImpulse,false,false);
            }
            motor.finish();
            if(warm)result.eastWarmup+=decisionX;
            else{
                if(d==WARM){result.postFirstX=decisionX;result.postFirstY=decisionY;}
                result.postEightX+=decisionX;result.postEightY+=decisionY;
            }
        }
        result.endVx=body.cvx;result.endVy=body.cvy;
        assertEquals("Motor QA cannot overwrite trained cortex",beforeUpdates,cortex.updates());
        assertTrue("Muscle trial must have genuine metabolic work",result.totalWork>0);
        for(double v:new double[]{result.postFirstX,result.postFirstY,
            result.postEightX,result.postEightY,result.eastWarmup})
            assertTrue("Nonfinite material displacement",Double.isFinite(v));
        return result;
    }
    @Test public void compareSavedRealPhysicalBrainMotorSharesNoTraining()throws Exception {
        Context c=InstrumentationRegistry.getInstrumentation().getTargetContext();
        File profile=new File(c.getExternalFilesDir(null),"zero381-readonly-profile.json");
        assertTrue("Missing explicit telemetric QA input",profile.isFile());
        JSONObject saved=new JSONObject(new String(Files.readAllBytes(profile.toPath()),
            StandardCharsets.UTF_8));
        String checkpoint=saved.getString("brain");
        String peripheral=saved.getString("motorHead");
        assertTrue("No full genuine ZERO3 checkpoint",checkpoint.length()>500000);
        assertTrue("No full genuine old motor readout",peripheral.startsWith("smh1|"));
        StringBuilder audit=new StringBuilder("ZERO381_ACTUAL_CHECKPOINT_MOTOR_AB\n");
        for(float share:new float[]{0f,.25f,.75f,1f}){
            Trial east=trial(checkpoint,peripheral,share,0);
            Trial west=trial(checkpoint,peripheral,share,4);
            Trial north=trial(checkpoint,peripheral,share,2);
            audit.append(String.format(Locale.US,
                "OLD_SHARE=%.2f WARM_EAST=%.4f BASE_EAST_1=%.4f WEST_1=%.4f WEST_8=%.4f EAST_8=%.4f NORTH_1Y=%.4f NORTH_8Y=%.4f EAST_8Y=%.4f PRE_VX=%.4f WORK=%.7f%n",
                share,east.eastWarmup,east.postFirstX,west.postFirstX,
                west.postEightX,east.postEightX,north.postFirstY,north.postEightY,
                east.postEightY,west.beforeVx,west.totalWork));
        }
        String report=audit.toString();
        android.util.Log.i("ZERO381_READONLY_MOTOR_AB",report);
        System.out.print(report);
        android.os.Bundle status=new android.os.Bundle();
        status.putString("stream",report);
        InstrumentationRegistry.getInstrumentation().sendStatus(0,status);
    }
}