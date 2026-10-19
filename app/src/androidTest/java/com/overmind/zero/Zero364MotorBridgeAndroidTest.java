package com.overmind.zero;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Real Android implementation: ZERO3 alone chooses a heading;
 * revived LOW-LEVEL MOTOR peripheral cannot choose it, trigger grip,
 * alter weights, or manufacture a physical displacement.
 * Stable GRU-V356 checkpoint remains untouched.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero364MotorBridgeAndroidTest {
    @Test public void headingAuthorityBelongsToGRU3Only(){
        for(int az=0;az<8;az++){
            double a=az*Math.PI/4;
            assertEquals(az,Zero3MotorPeripheralBridgeV364.actionBankFromV3(
                (float)Math.cos(a),(float)Math.sin(a)));
        }
        assertThrows(IllegalArgumentException.class,
            ()->Zero3MotorPeripheralBridgeV364.actionBankFromV3(0,0));
        assertThrows(IllegalArgumentException.class,
            ()->Zero3MotorPeripheralBridgeV364.actionBankFromV3(Float.NaN,1));
    }
    @Test public void preservedV319GripCannotBeReplacedByLegacyMotor(){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface surface=new Morphology.Surface(b);
        SymbioteMatterV1 tissue=new SymbioteMatterV1();
        tissue.reset(b,surface);
        SymbioteMotorHeadV1 peripheral=new SymbioteMotorHeadV1();
        Zero3MotorPeripheralBridgeV364.begin(peripheral,b,surface,null,
            new ArrayList<>(),-1f,0f);
        float[] action=new float[64];
        for(int sector=0;sector<16;sector++){
            action[sector*4+2]=sector/20f;
            action[sector*4]=.2f;
            action[sector*4+1]=-.2f;
        }
        float[] recorded=action.clone();
        Zero3MotorPeripheralBridgeV364.fastPhysicalRefine(action,peripheral,b,surface,1f/60f);
        int changed=0;
        for(int sector=0;sector<16;sector++){
            assertEquals("CH2 neural grip V319 must be byte-identical",
                Float.floatToIntBits(recorded[sector*4+2]),
                Float.floatToIntBits(action[sector*4+2]));
            if(Math.abs(action[sector*4]-recorded[sector*4])>1e-5)changed++;
        }
        assertTrue("low-level motor peripheral actually participates",changed>0);
        assertEquals("V3 cortex physical command still contributes",.75f,
            Zero3MotorPeripheralBridgeV364.PROVEN_CANDIDATE_SHARE,0f);
    }
    @Test public void genuineGelCanMoveOnAllFourChosenAxesWithoutExternalThrust(){
        for(int direction=0;direction<4;direction++){
            MotorBrain.Body body=new MotorBrain.Body();
            Morphology.Surface skin=new Morphology.Surface(body);
            SymbioteMatterV1 gel=new SymbioteMatterV1();
            gel.reset(body,skin);gel.enableOriginalZero3TractionV363();
            SymbioteMotorHeadV1 peripheral=new SymbioteMotorHeadV1();
            Zero3LearningFoundation.AdaptiveCortex cortex=new Zero3LearningFoundation.AdaptiveCortex();
            Zero3LearningFoundation.Coordinator coordinator=new Zero3LearningFoundation.Coordinator(cortex);
            float x=direction==0?1f:direction==2?-1f:0f;
            float y=direction==1?1f:direction==3?-1f:0f;
            double dx=0,dy=0,realWork=0;
            for(int d=0;d<120;d++){
                Zero3LearningFoundation.MotorIntent intent=new Zero3LearningFoundation.MotorIntent(
                    1,d,cortex.revision(),x,y,.5f,.5f,24f/60f);
                coordinator.begin(intent,body,false,true,true,true);
                Zero3MotorPeripheralBridgeV364.begin(peripheral,body,skin,
                    null,new ArrayList<>(),intent.x,intent.y);
                for(int i=0;i<24;i++){
                    float[] cmd=coordinator.actuate(body,skin,1f/60f);
                    Zero3MotorPeripheralBridgeV364.fastPhysicalRefine(cmd,peripheral,body,skin,1f/60f);
                    float px=body.cx,py=body.cy,old=gel.work;
                    gel.step(body,skin,cmd,1f/60f);
                    dx+=body.cx-px;dy+=body.cy-py;
                    realWork+=Math.max(0f,gel.work-old);
                    coordinator.observe(body,1f/60f,Math.max(0f,gel.work-old),
                        gel.lastMuscleReactionImpulse,false,false);
                }
                coordinator.finish();
            }
            double projection=dx*x+dy*y;
            assertTrue("genuine motor reaction MUST advance direction "+direction+" actual "+projection,
                projection>10d);
            assertTrue("physically measured cost must remain real",realWork>0d);
            assertEquals("evaluation must not secretly train V3 cortex",0,cortex.updates());
        }
    }
}
