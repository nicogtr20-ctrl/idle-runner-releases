package com.overmind.zero;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;

/** V476 Android regressions: real physical-response formulas, NO scripted heading or reward.
 * Final same-SHA original Android S21/wild/regression/CRC gate trigger. */
public final class Zero476V377TransientMidDriftAndroidTest {
    private static float[] pre(float vx,float vy){
        float[] p=new float[194];
        p[191]=vx/130f;p[192]=vy/130f;
        return p;
    }
    private static final float[] EAST={1f,0f};
    private static void sample(Zero3InertialResponseV377 m,float speed,
                               float physicalDx,boolean heldout){
        m.begin(pre(speed,0),EAST,heldout);
        m.finish(physicalDx,0f,.30f,heldout);
    }
    private static Zero3InertialResponseV377 verifiedMid(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        m.enableMidDriftRegimeLabV476();
        // PRE speed=1 U/s, measured POST travel=.60 U in .30 s;
        // the actual observed residual=.30 above PRE coasting=.30.
        for(int k=0;k<4;k++)sample(m,1f,.60f,false);
        for(int k=0;k<3;k++)sample(m,1f,.60f,true);
        return m;
    }
    @Test public void midDriftNeedsIndependentPhysicalHeldoutAndDoesNotLeakIntoStationaryBank(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        m.enableMidDriftRegimeLabV476();
        for(int k=0;k<4;k++)sample(m,1f,.60f,false);
        assertNull("TRAIN alone cannot certify motor authority",m.expected(pre(1f,0),EAST));
        for(int k=0;k<3;k++)sample(m,1f,.60f,true);
        float[] predicted=m.expected(pre(1f,0),EAST);
        assertNotNull("genuine independent heldout improves on PRE coasting",predicted);
        assertEquals(.60f,predicted[0],.0002f);
        assertNull("intermediate evidence cannot become stationary motor proof",
            m.expected(pre(0f,0f),EAST));
        assertEquals(1,m.reliableSteeringDirectionsV383(pre(1f,0f)));
        assertEquals(0,m.reliableSteeringDirectionsV383(pre(0f,0f)));
        assertTrue(m.motorValidationDiagnosticsV444().contains("v476MidReliable=1"));
    }
    @Test public void unflaggedOriginalV377RetainsOldSpeedBoundary(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        for(int k=0;k<4;k++)sample(m,1f,.60f,false);
        for(int k=0;k<3;k++)sample(m,1f,.60f,true);
        assertNotNull("old V377 low-speed bank remains the default",m.expected(pre(1f,0f),EAST));
        assertEquals(0,m.reliableSteeringDirectionsV383(pre(3f,0f)));
        assertTrue(m.motorValidationDiagnosticsV444().contains("v476MidEnabled=false"));
    }
    @Test public void intermediateBankNeverChangesOriginalSerializedV377Schema()throws Exception{
        Zero3InertialResponseV377 m=verifiedMid();
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        m.write(new DataOutputStream(bytes));
        // Existing 16 original persisted banks: VERSION int + 16*(2 ints+2 floats+2 doubles).
        assertEquals(4+16*32,bytes.toByteArray().length);
        Zero3InertialResponseV377 restored=Zero3InertialResponseV377.read(
            new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
        assertNull("ephemeral lab bank MUST NOT be stored in original user checkpoint",
            restored.expected(pre(1f,0f),EAST));
        assertTrue(restored.motorValidationDiagnosticsV444().contains("v476MidEnabled=false"));
        Zero3InertialResponseV377 copy=new Zero3InertialResponseV377();
        m.copyTo(copy);
        assertNotNull("in-memory real-life copy preserves heldout evidence",
            copy.expected(pre(1f,0f),EAST));
    }
    @Test public void intermediateMotorMapCannotCertifyPassiveCoasting(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        m.enableMidDriftRegimeLabV476();
        for(int k=0;k<4;k++)sample(m,1f,.30f,false);
        for(int k=0;k<3;k++)sample(m,1f,.30f,true);
        assertNull("no measured motor residual over PRE coast",m.expected(pre(1f,0),EAST));
        assertEquals(0,m.reliableSteeringDirectionsV383(pre(1f,0f)));
    }

    @Test public void passiveViscousBrakingOppositeIssuedHeadingCannotCertifyFalseMotorAuthority(){
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        m.enableMidDriftRegimeLabV476();
        float[] west={-1f,0f};
        // PRE COM +1 U/s, passive damped POST +0.15 U in 0.30s.
        // Old residual -0.15 falsely points WEST despite zero WEST muscle.
        for(int k=0;k<7;k++){
            boolean heldout=k>=4;
            m.begin(pre(1f,0f),west,heldout);
            m.finish(.15f,0f,.30f,heldout);
        }
        assertNull("passive viscous braking cannot become certified WEST propulsion",
            m.expected(pre(1f,0f),west));
        assertTrue(m.motorValidationDiagnosticsV444().contains(
            "v477MidOpposingPreRejected=7"));
        assertTrue(m.motorValidationDiagnosticsV444().contains("v476MidTrain=0"));
        assertTrue(m.motorValidationDiagnosticsV444().contains("v476MidHeldout=0"));
    }
    @Test public void independentlyObservedAlignedMidPropulsionStillCertifies(){
        Zero3InertialResponseV377 m=verifiedMid();
        assertNotNull(m.expected(pre(1f,0f),EAST));
        assertEquals(.60f,m.expected(pre(1f,0f),EAST)[0],.0002f);
        assertTrue(m.motorValidationDiagnosticsV444().contains(
            "v477MidOpposingPreRejected=0"));
    }
}
