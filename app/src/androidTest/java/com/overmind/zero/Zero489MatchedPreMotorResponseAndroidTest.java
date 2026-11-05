package com.overmind.zero;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * ZERO489 mathematical scientific guards. Synthetic controlled transitions
 * TEST ONLY; they do NOT count as real-world fitness or causal actuator proof.
 * Actual original-body 2x10000 Android runs remain the only behavior gates.
 */
public final class Zero489MatchedPreMotorResponseAndroidTest {
    private static float[] sensed(float vx,float vy){
        float[] x=new float[194];x[191]=vx/130f;x[192]=vy/130f;return x;
    }
    private static void trainPair(Zero489MatchedPreMotorResponse m,
                                  float vx,float vy,float effort,float east,float west){
        for(int i=0;i<70;i++){
            m.observe(vx,vy,1,0,effort,.30f*vx+east,.30f*vy,.30f,false);
            m.observe(vx,vy,-1,0,effort,.30f*vx+west,.30f*vy,.30f,false);
        }
        for(int i=0;i<10;i++){
            m.observe(vx,vy,1,0,effort,.30f*vx+east,.30f*vy,.30f,true);
            m.observe(vx,vy,-1,0,effort,.30f*vx+west,.30f*vy,.30f,true);
        }
    }
    @Test public void physicallyIdenticalPassiveDriftNeverBecomesMotorSteering(){
        Zero489MatchedPreMotorResponse m=new Zero489MatchedPreMotorResponse();
        trainPair(m,1,0,.7f,-.08f,-.08f); // same drag for both opposing headings
        assertNull(m.predicted(sensed(1,0),new float[]{1,0,.7f}));
        assertNull(m.predicted(sensed(1,0),new float[]{-1,0,.7f}));
        assertEquals(0,m.reliableBanks());
    }
    @Test public void trueHeldoutDifferentialCanLearnBrakingAgainstPreMomentum(){
        Zero489MatchedPreMotorResponse m=new Zero489MatchedPreMotorResponse();
        trainPair(m,1,0,.7f,.7f,-.55f);
        float[] forward=m.predicted(sensed(1,0),new float[]{1,0,.7f});
        float[] brake=m.predicted(sensed(1,0),new float[]{-1,0,.7f});
        assertNotNull("real physical policy response, unlike inherited coast",forward);
        assertNotNull("the opposite PRE-drift action must remain learnable",brake);
        assertTrue("different original muscle intentions have learned different actual travel",
            forward[0]-.5f>brake[0]);
        assertTrue(m.reliableBanks()>=2);
    }
    @Test public void heldoutObservationsNeverPretendToBeTrain(){
        Zero489MatchedPreMotorResponse m=new Zero489MatchedPreMotorResponse();
        for(int i=0;i<90;i++){
            m.observe(0,0,1,0,.7f,2,0,.3f,true);
            m.observe(0,0,-1,0,.7f,-2,0,.3f,true);
        }
        assertNull(m.predicted(sensed(0,0),new float[]{1,0,.7f}));
        assertEquals(0,m.reliableBanks());
    }
    @Test public void insufficientActionCoverageAndEffortAreNotPredicted(){
        Zero489MatchedPreMotorResponse m=new Zero489MatchedPreMotorResponse();
        for(int i=0;i<100;i++)m.observe(0,0,1,0,.7f,1,0,.3f,false);
        for(int i=0;i<20;i++)m.observe(0,0,1,0,.7f,1,0,.3f,true);
        assertNull("one issued direction never proves an action contrast",
            m.predicted(sensed(0,0),new float[]{1,0,.7f}));
        trainPair(m,0,0,.7f,.7f,-.7f);
        assertNotNull(m.predicted(sensed(0,0),new float[]{-1,0,.7f}));
        assertNull("different actual muscle effort cannot borrow prior response",
            m.predicted(sensed(0,0),new float[]{-1,0,.2f}));
    }
    @Test public void invalidRealInputRejectedAndPreservedOnCopy(){
        Zero489MatchedPreMotorResponse m=new Zero489MatchedPreMotorResponse();
        trainPair(m,0,0,.7f,1,-1);
        Zero489MatchedPreMotorResponse copy=new Zero489MatchedPreMotorResponse();
        m.copyTo(copy);
        assertNotNull(copy.predicted(sensed(0,0),new float[]{1,0,.7f}));
        try{
            m.observe(Float.NaN,0,1,0,.7f,1,0,.3f,false);
            fail("unobserved velocity must not be learned");
        }catch(IllegalArgumentException expected){}
        try{
            m.observe(0,0,1,0,1.2f,1,0,.3f,false);
            fail("impossible original muscle effort");
        }catch(IllegalArgumentException expected){}
    }
}
