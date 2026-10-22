package com.overmind.zero;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import static org.junit.Assert.*;

/** Only the real saved V380 V3 physical cortex and frozen peripheral.
 * No generated rewards, no GRU state training, no user-profile writes. */
@RunWith(AndroidJUnit4.class)
public final class Zero381UserCheckpointClosedLoopAndroidTest {
    static final float DT=1f/60f;
    static final int STEPS=24;
    static final ArrayList<UnknownWorld.Entity> NONE=new ArrayList<>();
    static Zero3LearningFoundation.AdaptiveCortex cortex(String raw)throws Exception {
        Zero3OriginalBrainV356.Readout snapshot=Zero3OriginalBrainV356.inspectSaved(raw);
        Field f=Zero3OriginalBrainV356.Readout.class.getDeclaredField("cortex");
        f.setAccessible(true);
        return (Zero3LearningFoundation.AdaptiveCortex)f.get(snapshot);
    }
    static final class Sim {
        final MotorBrain.Body body=new MotorBrain.Body();
        final Morphology.Surface skin=new Morphology.Surface(body);
        final SymbioteMatterV1 gel=new SymbioteMatterV1();
        final SymbioteMotorHeadV1 old=new SymbioteMotorHeadV1();
        final Zero3LearningFoundation.AdaptiveCortex cortex;
        final Zero3LearningFoundation.Coordinator motor;
        int decisions;
        Sim(String checkpoint,String savedPeripheral)throws Exception{
            cortex=cortex(checkpoint);
            motor=new Zero3LearningFoundation.Coordinator(cortex);
            assertTrue(old.restore(savedPeripheral));
            gel.reset(body,skin);gel.enableOriginalZero3TractionV363();
        }
        float[] decision(float headingX,float headingY,
                         Zero381MotorCalibration cal,boolean training,boolean validate)throws Exception{
            float firstVx=body.cvx,firstVy=body.cvy;
            float x=headingX,y=headingY;
            if(cal!=null){
                float[] applied=cal.begin(x,y,firstVx,firstVy);
                x=applied[0];y=applied[1];
            }
            Zero3LearningFoundation.MotorIntent choice=new Zero3LearningFoundation.MotorIntent(
                1L,decisions++,cortex.revision(),headingX,headingY,.5f,.5f,STEPS*DT);
            motor.begin(choice,body,false,true,true,true,true);
            Zero3MotorPeripheralBridgeV364.begin(old,body,skin,null,NONE,x,y);
            float dx=0,dy=0,impulse=0;
            for(int f=0;f<STEPS;f++){
                float[] executed=motor.actuate(body,skin,DT);
                Zero3MotorPeripheralBridgeV364.fastPhysicalRefine(executed,old,body,skin,DT);
                float priorX=body.cx,priorY=body.cy,priorWork=gel.work;
                gel.step(body,skin,executed,DT);
                dx+=body.cx-priorX;dy+=body.cy-priorY;
                impulse+=gel.lastMuscleReactionImpulse;
                motor.observe(body,DT,Math.max(0,gel.work-priorWork),
                    gel.lastMuscleReactionImpulse,false,false);
            }
            motor.finish();
            if(cal!=null)cal.complete(dx,dy,STEPS*DT,impulse,training,validate);
            return new float[]{dx,dy};
        }
    }
    @Test public void learnIndependentMotorResponseAndMeasureMatchedReversal()throws Exception{
        Context c=InstrumentationRegistry.getInstrumentation().getTargetContext();
        File f=new File(c.getExternalFilesDir(null),"zero381-readonly-profile.json");
        assertTrue(f.isFile());
        JSONObject source=new JSONObject(new String(Files.readAllBytes(f.toPath()),StandardCharsets.UTF_8));
        String saved=source.getString("brain"),old=source.getString("motorHead");
        Zero381MotorCalibration calibration=new Zero381MotorCalibration();
        Sim experiment=new Sim(saved,old);
        for(int decision=0;decision<1200;decision++){
            // Each action heading is an EXTERNAL physics diagnostic choice,
            // never a rule added to the game. All real gel/body forces still run.
            double a=(decision%8)*Math.PI/4;
            experiment.decision((float)Math.cos(a),(float)Math.sin(a),
                calibration,true,decision%5==0);
        }
        assertTrue("Physically trained samples never stored",calibration.samples()>750);
        assertTrue("Independent heldout physical motor evaluation missing",
            calibration.validations()>150);
        StringBuilder report=new StringBuilder(String.format(Locale.US,
            "ZERO381_REAL_SAVED_CLOSED_LOOP trained=%d heldout=%d trusted=%s mse=%.6f nullMse=%.6f%n",
            calibration.samples(),calibration.validations(),calibration.trusted(),
            calibration.validationMse(),calibration.baselineMse()));
        for(int direction:new int[]{4,2}){
            for(int candidate=0;candidate<2;candidate++){
                Sim s=new Sim(saved,old);
                Zero381MotorCalibration m=candidate==0?null:new Zero381MotorCalibration();
                if(candidate!=0)assertTrue(m.restore(calibration.encode()));
                float dx=0,dy=0;
                for(int k=0;k<12;k++)s.decision(1,0,m,false,false);
                for(int k=0;k<8;k++){
                    float a=direction==4?-1:0,b=direction==2?1:0;
                    float[] move=s.decision(a,b,m,false,false);
                    dx+=move[0];dy+=move[1];
                }
                report.append(String.format(Locale.US,
                    "TARGET=%s CALIBRATED=%s DX=%.6f DY=%.6f ACTUATOR_CORRECTED=%s%n",
                    direction==4?"WEST":"NORTH",candidate!=0,
                    dx,dy,m!=null&&m.usedCorrection()));
            }
        }
        android.os.Bundle status=new android.os.Bundle();
        status.putString("stream",report.toString());
        InstrumentationRegistry.getInstrumentation().sendStatus(0,status);
        System.out.print(report);
        // No automatic conclusion that inverse calibration solved the
        // problem: the signed phone build must be gated by these real numbers.
    }
}
