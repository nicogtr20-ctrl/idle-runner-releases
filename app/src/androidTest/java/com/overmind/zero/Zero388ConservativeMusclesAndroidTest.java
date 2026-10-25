package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Conserved authentic S21 material, no scripted gait, preserve GRU64/194-schema. */
@RunWith(AndroidJUnit4.class)
public final class Zero388ConservativeMusclesAndroidTest {
    private static File savedS21(){
        return new File(InstrumentationRegistry.getInstrumentation().getTargetContext()
            .getExternalFilesDir(null),"zero385-user-readonly.json");
    }
    @Test public void heterogeneousLearnedShellForceMustSumToZeroOnActualS21()
        throws Exception{
        assertTrue("Real S21 physical checkpoint is required",savedS21().isFile());
        JSONObject data=new JSONObject(new String(Files.readAllBytes(savedS21().toPath()),
            StandardCharsets.UTF_8));
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            data.getString("training_room_1_scene_v379"));
        assertNotNull(scene);
        float[] k=new float[Morphology.POINTS],forceX=new float[k.length],
            forceY=new float[k.length];
        for(int i=0;i<k.length;i++){
            int sector=i/6;
            k[i]=85f+190f*(.5f+.42f*(float)Math.sin(1.7*sector+.3));
        }
        // Reproduce actual old nonconservative equation, NOT pretend it is
        // measured at the user's screenshot instant.
        float oldX=0,oldY=0,absolute=0;
        for(int i=0;i<k.length;i++){
            int p=(i+95)%96,n=(i+1)%96;
            float fx=(scene.skin.x[p]+scene.skin.x[n]-2*scene.skin.x[i])*k[i];
            float fy=(scene.skin.y[p]+scene.skin.y[n]-2*scene.skin.y[i])*k[i];
            oldX+=fx;oldY+=fy;absolute+=(float)Math.hypot(fx,fy);
        }
        assertTrue("Old radial spring really injected nonphysical net force",
            Math.hypot(oldX,oldY)>100f);
        SymbioteMatterV1.accumulateBalancedElasticV388(scene.skin.x,scene.skin.y,
            k,forceX,forceY);
        double fx=0,fy=0;
        for(int i=0;i<96;i++){fx+=forceX[i];fy+=forceY[i];}
        assertTrue("New elastic force must conserve momentum within rounding",
            Math.hypot(fx,fy)<.15f);
        // Compare rotated index origin: same world contour but shifted
        // material numbering MUST yield same physical force at each site.
        float[] sx=new float[96],sy=new float[96],sk=new float[96],
            outX=new float[96],outY=new float[96];
        for(int i=0;i<96;i++){
            int j=(i+17)%96;sx[i]=scene.skin.x[j];sy[i]=scene.skin.y[j];sk[i]=k[j];
        }
        SymbioteMatterV1.accumulateBalancedElasticV388(sx,sy,sk,outX,outY);
        for(int i=0;i<96;i++){
            int j=(i+17)%96;
            assertEquals("Membrane force must not depend on sector traversal",forceX[j],outX[i],.015f);
            assertEquals("Membrane force must not depend on sector traversal",forceY[j],outY[i],.015f);
        }
        android.util.Log.i("ZERO388_FORCE",
            "S21_REAL_ELASTIC_NET_OLD="+Math.hypot(oldX,oldY)+
            " NET_V388="+Math.hypot(fx,fy)+
            " TOTAL_ABSOLUTE="+absolute+" RESULT=PASS");
    }
    private static float[] sensoryMotorWithTangential(float tangentialSpeed){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        for(int i=0;i<Morphology.POINTS;i++){
            float dx=skin.x[i]-body.cx,dy=skin.y[i]-body.cy;
            float rr=(float)Math.hypot(dx,dy);
            if(rr<1e-4f)continue;
            skin.vx[i]=-dy/rr*tangentialSpeed;
            skin.vy[i]=dx/rr*tangentialSpeed;
        }
        Zero3LearningFoundation.MotorIntent intent=
            new Zero3LearningFoundation.MotorIntent(
                0,0,cortex.revision(),1f,0f,.5f,.5f,.4f);
        cortex.begin(false,intent,true);
        float[] actual=cortex.command(intent,body,skin,1f/60f).clone();
        cortex.discard();
        return actual;
    }
    @Test public void pureTangentialMaterialMotionMustReachSameLearnedCH1Neuron(){
        float[] still=sensoryMotorWithTangential(0f);
        float[] clockwise=sensoryMotorWithTangential(50f);
        float[] opposite=sensoryMotorWithTangential(-50f);
        double delta=0;
        for(int sector=0;sector<16;sector++){
            int ch1=sector*4+1;
            delta+=Math.abs(clockwise[ch1]-still[ch1]);
            delta+=Math.abs(opposite[ch1]-still[ch1]);
            assertTrue(Float.isFinite(clockwise[ch1]));
            // CH0/3 original learned input channel is unchanged by a
            // tangent-only movement; no brain/schema reset.
            assertEquals(still[sector*4],clockwise[sector*4],0f);
            assertEquals(still[sector*4+3],clockwise[sector*4+3],0f);
        }
        assertTrue("V387 was blind to CH1 tangent; V388 must really perceive it",
            delta>1e-6);
    }
    @Test public void selectedMotorResetOnlyClearsMotorCalibrationAndNotOriginalGRU(){
        HashSet<String> reset=new HashSet<>(Arrays.asList(
            ResetDomains.keys(ResetDomains.MOTOR)));
        assertTrue(reset.contains(Zero381MotorCalibration.KEY));
        assertTrue(reset.contains(Zero381MotorCalibration.DIRECT_KEY_V387));
        assertTrue(reset.contains(Zero381MotorFlightRecorder.KEY));
        assertFalse("Never wipe the real saved GRU64 on a scoped motor reset",
            reset.contains(Zero3OriginalBrainV356.KEY));
        assertFalse(reset.contains("zero3_original_brain_only_v356"));
    }
}
