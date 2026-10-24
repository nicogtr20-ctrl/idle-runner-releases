package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** No mixed V2.2/V3 physical response knowledge and no silent zero-sample V3. */
@RunWith(AndroidJUnit4.class)
public final class Zero387DirectMotorCalibrationAndroidTest {
    @Test public void directV3CalibratesItsOwnPhysicalResponseFromGenuineCompletedAction(){
        Zero381MotorCalibration archivedOld=new Zero381MotorCalibration();
        Zero381MotorCalibration sole3=new Zero381MotorCalibration();
        assertEquals(0,archivedOld.samples());
        sole3.beginDirectV387(.6f,.8f,2f,-1f);
        assertEquals(.6f,sole3.lastPeripheralX(),0f);
        assertEquals(.8f,sole3.lastPeripheralY(),0f);
        sole3.complete(.72f,.15f,.4f,50f,true,false);
        assertEquals("Direct cortex must finally observe completed clean motions",1,
            sole3.samples());
        assertEquals("Old V2.2 mapping must never learn V3 outcomes",0,
            archivedOld.samples());
        Zero381MotorCalibration reloaded=new Zero381MotorCalibration();
        assertTrue("Separate optional profile save",reloaded.restore(sole3.encode()));
        assertEquals(1,reloaded.samples());
        assertEquals(0,archivedOld.samples());
    }
    @Test public void disturbedOrAbortedDirectionsNeverLookLikePhysicalEvidence(){
        Zero381MotorCalibration direct=new Zero381MotorCalibration();
        direct.beginDirectV387(1f,0f,0f,0f);
        direct.complete(3f,0f,.4f,50f,false,false);
        assertEquals("Real wall correction not a clean learned actuator mapping",
            0,direct.samples());
        direct.beginDirectV387(-1f,0f,0f,0f);
        direct.abort();
        direct.complete(6f,0f,.4f,50f,true,false);
        assertEquals("Interrupted decisions have no fake motor-response result",
            0,direct.samples());
        direct.beginDirectV387(0f,1f,0f,0f);
        direct.complete(0f,.8f,.4f,50f,true,false);
        assertEquals(1,direct.samples());
    }
}
