package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class Zero381PhysicalMotorCalibrationAndroidTest {
    @Test public void learningRequiresRealCleanIndependentMotorEvidence(){
        Zero381MotorCalibration c=new Zero381MotorCalibration();
        float[] a=c.begin(1,0,0,0);
        assertEquals(1f,a[0],0f);
        assertEquals(0f,a[1],0f);
        for(int i=0;i<600;i++){
            float x=i%4==0?1f:i%4==2?-1f:0f;
            float y=i%4==1?1f:i%4==3?-1f:0f;
            float[] mapped=c.begin(x,y,.2f,-.1f);
            if(!c.usedCorrection()){
                assertEquals("Untrusted motor mapping is identity",x,mapped[0],0f);
                assertEquals("Untrusted motor mapping is identity",y,mapped[1],0f);
            }else{
                assertTrue("Learned motor steering correction cannot invert GRU decision",
                    mapped[0]*x+mapped[1]*y>.93f);
            }
            if(i%7==0){
                c.complete(-100f,100f,.4f,10,false,false); // wall collision
                continue;
            }
            if(i%5==0){
                c.complete(0,0,.4f,0,true,false); // no muscle impulse
                continue;
            }
            float effectiveX=1.2f*x+.15f*y;
            float effectiveY=-.15f*x+1.1f*y;
            // Actual momentum + physical response * t^2
            float dx=.2f*.4f+effectiveX*.16f;
            float dy=-.1f*.4f+effectiveY*.16f;
            c.complete(dx,dy,.4f,12f,true,i%4==0);
        }
        assertTrue(c.samples()>256);
        assertTrue(c.validations()>64);
        assertTrue("Heldout inverse motor map must beat null",c.validationMse()<c.baselineMse());
        String previous=c.encode();
        Zero381MotorCalibration restored=new Zero381MotorCalibration();
        assertTrue("Same profile checkpoint reconstructs exact statistics",restored.restore(previous));
        assertEquals(previous,restored.encode());
        assertFalse("Invalid actuator checkpoint must never silently overwrite weights",
            restored.restore("zmr381|1|0|0|NaN"));
    }
    @Test public void sameEightWayBankMustStillHonorDifferentContinuousV3Angles(){
        MotorBrain.Body b0=new MotorBrain.Body();
        Morphology.Surface s0=new Morphology.Surface(b0);
        MotorBrain.Body b1=new MotorBrain.Body();
        Morphology.Surface s1=new Morphology.Surface(b1);
        SymbioteMotorHeadV1 p0=new SymbioteMotorHeadV1();
        SymbioteMotorHeadV1 p1=new SymbioteMotorHeadV1();
        java.util.ArrayList<UnknownWorld.Entity> none=new java.util.ArrayList<>();
        float theta=10f*(float)Math.PI/180f;
        assertEquals(0,Zero3MotorPeripheralBridgeV364.actionBankFromV3(1,0));
        assertEquals(0,Zero3MotorPeripheralBridgeV364.actionBankFromV3(
            (float)Math.cos(theta),(float)Math.sin(theta)));
        Zero3MotorPeripheralBridgeV364.begin(p0,b0,s0,null,none,1,0);
        Zero3MotorPeripheralBridgeV364.begin(p1,b1,s1,null,none,
            (float)Math.cos(theta),(float)Math.sin(theta));
        float[] commands0=p0.commands(),commands1=p1.commands();
        float total=0f;
        for(int i=0;i<64;i++)if(i%4!=2)
            total+=Math.abs(commands0[i]-commands1[i]);
        assertTrue("381 original V3 motor MUST NOT lose 10-degree information to eight-way rounding",
            total>.00001f);
        assertFalse("The historical frozen motor may never retrain its weights",
            p0.trainedOutcomes()>0);
        float[] out0=new float[64],out1=new float[64];
        p0.fastTickOriginalV370(b0,s0,1f/60f);
        p1.fastTickOriginalV370(b1,s1,1f/60f);
        System.arraycopy(p0.commands(),0,out0,0,64);
        System.arraycopy(p1.commands(),0,out1,0,64);
        float fastDifference=0f;
        for(int i=0;i<64;i++)if(i%4<2)
            fastDifference+=Math.abs(out0[i]-out1[i]);
        assertTrue("381 fast somatic V3 must also preserve continuous heading",fastDifference>.00001f);
    }
    @Test public void predictivePassiveDriftCanNeverBeMistakenForDirectionAuthority(){
        Zero381MotorCalibration c=new Zero381MotorCalibration();
        for(int n=0;n<800;n++){
            float x=n%4==0?1:n%4==2?-1:0;
            float y=n%4==1?1:n%4==3?-1:0;
            c.begin(x,y,0f,0f);
            // Momentum-subtracted movement is strictly opposite to the
            // chosen direction, even when it is statistically predictable.
            c.complete(-x*.20f,-y*.20f,.4f,12f,true,n%5==0);
        }
        assertTrue(c.samples()>256);
        assertTrue(c.validations()>64);
        assertFalse("Predicting drift is not proof of controllable muscle authority",
            c.trusted());
        float[] a=c.begin(0f,1f,2f,0f);
        assertEquals(0f,a[0],0f);
        assertEquals(1f,a[1],0f);
        assertFalse(c.usedCorrection());
    }
    @Test public void neutralCortexHasNoPrewiredMotorCorrection(){
        Zero381MotorCalibration c=new Zero381MotorCalibration();
        for(int i=0;i<8;i++){
            float a=i*(float)Math.PI/4f;
            float x=(float)Math.cos(a),y=(float)Math.sin(a);
            float[] intended=c.begin(x,y,3,-2);
            assertEquals(x,intended[0],1e-6f);
            assertEquals(y,intended[1],1e-6f);
            assertFalse(c.usedCorrection());
            c.complete(0,0,.4f,0f,false,true);
        }
        assertEquals(0,c.samples());
        assertFalse(c.trusted());
    }
    @Test public void boundedFlightRecordStoresActualAndOnly256Decisions(){
        Zero381MotorFlightRecorder m=new Zero381MotorFlightRecorder();
        float[] optical=new float[194];optical[20]=.7f;optical[31]=.3f;
        float[] muscles=new float[64];muscles[0]=.4f;
        for(int i=0;i<300;i++)m.completed(i,1f,0f,1f,0f,
            1f,0f,-.5f,0f,0.3f,0f,.4f,2f,0f,0f,
            optical,muscles,true,false,false);
        String data=m.encode();
        assertEquals(257,data.split("\n").length);
        assertEquals(300,m.counted());
        Zero381MotorFlightRecorder reconstructed=new Zero381MotorFlightRecorder();
        assertTrue(reconstructed.restore(data));
        assertEquals(data,reconstructed.encode());
        assertTrue(data.contains("299,"));
        assertFalse(data.contains("\n0,"));
    }
}
