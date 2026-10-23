package com.overmind.zero;

import android.content.Context;
import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** ZERO383 motor source-of-truth: body capability MUST be measured, not
 * hallucinated from learned blue/red wavelengths. Original user brain is
 * loaded ephemerally for a counterfactual, never mutated/saved. */
@RunWith(AndroidJUnit4.class)
public final class Zero383TrustedMotorExplorationAndroidTest {
    private static float[] sensory(float vx,float vy){
        float[] x=new float[194];x[193]=1f;
        x[191]=vx/130f;x[192]=vy/130f;
        return x;
    }
    @Test public void wideExplorationIsGatedOnActualHeldoutActiveSteerability(){
        Zero3InertialResponseV377 physical=new Zero3InertialResponseV377();
        float[] atRest=sensory(0,0);
        assertEquals(0,physical.reliableSteeringDirectionsV383(atRest));
        // One successful motor heading alone cannot justify full directional
        // exploration. Verify another independent nonparallel actuator
        // response using actual material outcomes on heldout trials.
        for(int axis=0;axis<2;axis++){
            float[] action={axis==0?1f:0f,axis==1?1f:0f,.5f,.5f,.4f};
            for(int trial=0;trial<9;trial++){
                boolean heldout=trial>=5;
                physical.begin(atRest,action,heldout);
                physical.finish(action[0]*.8f,action[1]*.8f,.4f,heldout);
            }
            assertEquals(axis+1,physical.reliableSteeringDirectionsV383(atRest));
        }
        // The evidence is from this speed regime only. No invented motor
        // skill follows ZERO into a fast coasting state.
        assertEquals(0,physical.reliableSteeringDirectionsV383(sensory(6,0)));
    }
    @Test public void wavelengthContactOrPhysicalCoastingCannotUnlockWideActor(){
        Zero3InstrumentalCreditV371 instrument=new Zero3InstrumentalCreditV371();
        float[] x=sensory(0,0);
        x[20]=.95f; // visible blue alone
        assertFalse(instrument.wideMotorExplorationSupportedV383(x));
        Zero3InertialResponseV377 drift=new Zero3InertialResponseV377();
        float[] pre=sensory(3,0);
        float[] dir={1,0,.5f,.5f,.4f};
        for(int i=0;i<16;i++){
            boolean hold=i>=8;
            drift.begin(pre,dir,hold);
            // Exactly the original velocity's coasting, no self-propulsion.
            drift.finish(3f*.4f,0f,.4f,hold);
        }
        assertEquals(0,drift.reliableSteeringDirectionsV383(pre));
    }
    @Test public void old382RealLearnedCortexKeepsOriginalNeuronsAndNoUnsupportedWideNoise()
            throws Exception{
        Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        File file=new File(ctx.getExternalFilesDir(null),"zero383-user382-readonly.json");
        assertTrue("Real user V382 profile must be uploaded only into ephemeral test",file.isFile());
        JSONObject input=new JSONObject(new String(Files.readAllBytes(
            file.toPath()),StandardCharsets.UTF_8));
        Zero3OriginalBrainV356.Readout saved=
            Zero3OriginalBrainV356.inspectSaved(input.getString("brain"));
        assertEquals(10294,saved.decisions);
        Field coreField=Zero3OriginalBrainV356.Readout.class.getDeclaredField("core");
        coreField.setAccessible(true);
        Zero3RecurrentBrain raw=(Zero3RecurrentBrain)coreField.get(saved);
        byte[] original=raw.checkpointAtBoundaryV345();
        Field rng=Zero3RecurrentBrain.class.getDeclaredField("rng");
        Field noise=Zero3RecurrentBrain.class.getDeclaredField("lastNoise");
        Field scale=Zero3RecurrentBrain.class.getDeclaredField("selectedNoiseScaleV369");
        Field cognition=Zero3RecurrentBrain.class.getDeclaredField("cognition");
        rng.setAccessible(true);noise.setAccessible(true);
        scale.setAccessible(true);cognition.setAccessible(true);
        String[] lines=input.getString("flight").trim().split("\\n");
        assertEquals(257,lines.length);
        int n=0,illegalWide=0,legalWide=0,learnedContactSeen=0;
        double selectedNoise=0;
        for(int i=1;i<=64;i++){
            Zero3RecurrentBrain brain=Zero3RecurrentBrain.fromCheckpoint(original);
            rng.setLong(brain,rng.getLong(brain)^(-7046029254386353131L*i));
            String[] rec=lines[1+i*3%256].split(",");
            float[] obs=sensory(Float.parseFloat(rec[5]),Float.parseFloat(rec[6]));
            obs[188]=.45f;
            int blue=Integer.parseInt(rec[18]),red=Integer.parseInt(rec[20]);
            if(blue>=0)obs[20+blue]=Float.parseFloat(rec[19]);
            if(red>=0)obs[28+red]=Float.parseFloat(rec[21]);
            Zero3CognitionV353 cog=(Zero3CognitionV353)cognition.get(brain);
            boolean ready=cog.motorExplorationReadyV383(obs);
            if(cog.hasVisibleContactKnowledgeV369(obs))learnedContactSeen++;
            Zero3LearningFoundation.MotorIntent chosen=
                brain.choose(obs,1,10294,0);
            float[] ns=(float[])noise.get(brain),ss=(float[])scale.get(brain);
            float max=Math.max(ss[0],ss[1]);
            if(!ready&&max>1.001)illegalWide++;
            if(ready&&max>1.001)legalWide++;
            assertTrue("No greater than 2.5x even when physical steering verified",
                max<=2.501f);
            assertEquals("Learning cannot be destroyed by chooser",10294,
                saved.decisions);
            assertEquals(1f,(float)Math.hypot(chosen.x,chosen.y),.001f);
            selectedNoise+=Math.hypot(ns[0],ns[1]);n++;
        }
        assertEquals(0,illegalWide);
        assertTrue("Authentic saved GRU still recognizes its learned wavelengths",
            learnedContactSeen>0);
        assertArrayEquals("Original neural checkpoint must not mutate",original,
            raw.checkpointAtBoundaryV345());
        String report=String.format(Locale.US,
            "ZERO383_ACTUAL_SAVED_USER382 n=%d learnedContactSeen=%d unsupportedWide=%d physicallyAllowedWide=%d selectedMeanNoise=%.5f actualNeuralBytesUnchanged=true%n",
            n,learnedContactSeen,illegalWide,legalWide,selectedNoise/n);
        Bundle bundle=new Bundle();bundle.putString("stream",report);
        InstrumentationRegistry.getInstrumentation().sendStatus(0,bundle);
        System.out.print(report);
    }
}
