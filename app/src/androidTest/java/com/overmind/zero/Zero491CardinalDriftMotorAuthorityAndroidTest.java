package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO491 diagnostic for the actual phone symptom: chosen heading disagrees
 * with an already drifting living body. Measures the CAUSAL difference between
 * the identical original 170-particle S21 physical state with cortex muscles
 * OFF and ON, including instantaneous acceleration.
 *
 * Not a navigator, a scripted motor, a second body solver, or a release gate.
 * A positive number means the cortex adds physical acceleration in the
 * requested direction relative to unchanged passive inertia; it does NOT
 * imply that the body has already reversed its position or velocity.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero491CardinalDriftMotorAuthorityAndroidTest {
    private static final int[] HEADINGS={0,1,2,3}; // E,S,W,N in screen coordinates
    // No shift, gentle phone-like diagonal drift, reversed diagonal, and
    // the previously observed strong S21 left/right inertia.
    private static final float[][] SHIFTS={
        {0f,0f},{1.2f,.35f},{-1.2f,-.35f},{60f,0f},{-60f,0f}
    };

    private static float projection(float x,float y,int h){
        switch(h){
            case 0:return x;
            case 1:return y;
            case 2:return -x;
            case 3:return -y;
            default:throw new IllegalArgumentException("heading "+h);
        }
    }

    @Test(timeout=1500000)
    public void sameMaterialSameInertiaMeasuresCardinalSteeringAndBraking()
            throws Exception {
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(user);
        int comparisons=0;
        int signedPositive=0,signedNegative=0,nearZero=0;
        for(int regime=0;regime<SHIFTS.length;regime++){
            float sx=SHIFTS[regime][0],sy=SHIFTS[regime][1];
            for(int heading:HEADINGS){
                Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario same=
                    new Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario(
                        "ZERO491_S21_MOMENTUM_"+regime+"_HEADING_"+heading,
                        free,false,heading,sx,sy);
                Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial passive=
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                        same,user,0,-1,-1,0);
                Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial actual=
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                        same,user,1,-1,-1,0);
                float dV=projection(actual.deltaVx-passive.deltaVx,
                    actual.deltaVy-passive.deltaVy,heading);
                float first=projection(actual.initialAx-passive.initialAx,
                    actual.initialAy-passive.initialAy,heading);
                float dX=projection(actual.dx-passive.dx,
                    actual.dy-passive.dy,heading);
                float passiveTravel=projection(passive.dx,passive.dy,heading);
                float actualTravel=projection(actual.dx,actual.dy,heading);
                assertEquals(0L,passive.invalid);
                assertEquals(0L,actual.invalid);
                assertTrue("Finite measured physical acceleration",Float.isFinite(first));
                assertTrue("Finite measured end velocity",Float.isFinite(dV));
                assertTrue("Finite displacement",Float.isFinite(dX));
                assertTrue("Muscle impulse cannot be negative",actual.impulse>=0f);
                if(dV>1e-4f)signedPositive++;
                else if(dV<-1e-4f)signedNegative++;
                else nearZero++;
                comparisons++;
                Log.i("ZERO491_DRIFT",String.format(Locale.US,
                    "regime=%d shift=(%.3f,%.3f) heading=%d "+
                    "motorAbovePassiveDV=%.7f firstAxAbovePassive=%.7f "+
                    "motorAbovePassiveDX=%.7f passiveTravel=%.7f "+
                    "realTravel=%.7f passiveContacts=%d realContacts=%d "+
                    "physicalImpulse=%.6f",
                    regime,sx,sy,heading,dV,first,dX,
                    passiveTravel,actualTravel,
                    passive.contactFrames,actual.contactFrames,actual.impulse));
            }
        }
        assertEquals(20,comparisons);
        Log.i("ZERO491_DRIFT","MATCHED_ORIGINAL_S21_DONE comparisons="+comparisons+
            " accelerateTowardChosen="+signedPositive+
            " accelerateAgainstChosen="+signedNegative+
            " negligibleAuthority="+nearZero+
            " researchOnly=true noProductionForceInjected=true");
    }
}
