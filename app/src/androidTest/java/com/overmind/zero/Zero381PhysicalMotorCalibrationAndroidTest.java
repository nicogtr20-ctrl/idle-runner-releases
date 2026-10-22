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
