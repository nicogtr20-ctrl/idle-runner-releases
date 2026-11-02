package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V474 math regression of V473 motor residual -> physical TOTAL travel.
 * Does not substitute for original 10k S21/wild autonomous Android tests.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero474V371ResidualPhysicalTravelAndroidTest {
    private static float[] pre(float vx,float vy){
        float[] p=new float[194];
        p[191]=vx/130f;
        p[192]=vy/130f;
        p[193]=1f;
        return p;
    }
    @Test public void stationaryBodyKeepsMeasuredMotorResidual(){
        float[] travel=Zero3InstrumentalCreditV371
            .reconstructRealTravelFromResidualV474(pre(0f,0f),.18f,-.12f);
        assertEquals(.18f,travel[0],.00001f);
        assertEquals(-.12f,travel[1],.00001f);
    }
    @Test public void driftingBodyReconstructsCoastPlusActualResidual(){
        float[] travel=Zero3InstrumentalCreditV371
            .reconstructRealTravelFromResidualV474(pre(.30f,-.20f),.18f,.06f);
        assertEquals(.27f,travel[0],.00001f);
        assertEquals(0f,travel[1],.00001f);
    }
    @Test public void reverseCoastCanOvercomeLearnedForwardResidual(){
        float[] travel=Zero3InstrumentalCreditV371
            .reconstructRealTravelFromResidualV474(pre(-.30f,0f),.04f,0f);
        assertEquals(-.05f,travel[0],.00001f);
        assertEquals(0f,travel[1],.00001f);
    }
    @Test public void impossibleSensorOrMotorValueCannotCreateTravel(){
        float[] p=pre(0f,0f);p[191]=Float.NaN;
        try{
            Zero3InstrumentalCreditV371.reconstructRealTravelFromResidualV474(p,.1f,0f);
            fail("invalid PRE cannot produce a physical prediction");
        }catch(IllegalArgumentException expected){}
    }
}
