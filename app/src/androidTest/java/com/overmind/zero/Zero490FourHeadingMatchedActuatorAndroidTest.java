package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO490 four-heading actual 170-site physics diagnostic.
 * All four headings share the same saved S21 free posture and identical
 * world-frame PRE velocity; each +/- original CH0/CH1/CH3 intervention
 * restarts the same material, skin, body and trained 512-weight cortex.
 * CH2 is NOT replaced: original V319 grip remains in every trial.
 * No scripted policy, no learning update and no production modification.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero490FourHeadingMatchedActuatorAndroidTest {
    private static final int[] CHANNELS={0,1,3};
    private static final float PRE_X=-1f, PRE_Y=0f;
    private static float projectedVelocity(
            Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial t,
            int heading) {
        switch(heading){
            case 0:return t.deltaVx;
            case 1:return t.deltaVy;
            case 2:return -t.deltaVx;
            default:return -t.deltaVy;
        }
    }

    private static float projectedFirstAcceleration(
            Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial t,
            int heading) {
        switch(heading){
            case 0:return t.initialAx;
            case 1:return t.initialAy;
            case 2:return -t.initialAx;
            default:return -t.initialAy;
        }
    }

    @Test(timeout=1500000)
    public void originalS21FourHeadingsMatchedDeliveredMuscleSigns()
            throws Exception {
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout saved=
            Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
        assertEquals(33312L,saved.decisions);
        assertEquals(19452L,saved.motorUpdates);
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(user);
        int comparisons=0,nonzero=0,opposing=0;
        for(int heading=0;heading<4;heading++){
            Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario s=
                new Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario(
                    "ZERO490_S21_MATCHED_HEADING_"+heading,
                    free,false,heading,PRE_X,PRE_Y);
            Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial passive=
                Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                    s,user,0,-1,-1,0);
            Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial full=
                Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                    s,user,1,-1,-1,0);
            float observed=projectedVelocity(full,heading);
            float abovePassive=observed-projectedVelocity(passive,heading);
            int headingNonzero=0,headingOpposing=0,headingFirstNonzero=0;
            int delayedOnly=0,firstOpposingToFull=0;
            float fullFirstAbovePassive=projectedFirstAcceleration(full,heading)-
                projectedFirstAcceleration(passive,heading);
            float min=Float.POSITIVE_INFINITY,max=Float.NEGATIVE_INFINITY;
            for(int sector=0;sector<16;sector++){
                for(int channel:CHANNELS){
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial minus=
                        Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                            s,user,2,sector,channel,-1);
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial plus=
                        Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                            s,user,2,sector,channel,+1);
                    assertEquals(0L,minus.invalid);
                    assertEquals(0L,plus.invalid);
                    float derivative=projectedVelocity(plus,heading)-
                        projectedVelocity(minus,heading);
                    assertTrue(Float.isFinite(derivative));
                    float firstDerivative=projectedFirstAcceleration(plus,heading)-
                        projectedFirstAcceleration(minus,heading);
                    assertTrue(Float.isFinite(firstDerivative));
                    if(Math.abs(firstDerivative)>1e-4f){
                        headingFirstNonzero++;
                        if(fullFirstAbovePassive*firstDerivative<0f)
                            firstOpposingToFull++;
                    }else if(Math.abs(derivative)>1e-4f){
                        delayedOnly++;
                    }
                    min=Math.min(min,derivative);
                    max=Math.max(max,derivative);
                    comparisons++;
                    if(Math.abs(derivative)>1e-4f){
                        nonzero++;headingNonzero++;
                        if(abovePassive*derivative<0f){
                            opposing++;headingOpposing++;
                        }
                    }
                }
            }
            Log.i("ZERO490_PHYSICS",String.format(Locale.US,
                "heading=%d preV=(%.3f,%.3f) passiveProjectedDV=%.7f"+
                " fullProjectedDV=%.7f fullAbovePassive=%.7f"+
                " nonzeroJacobian=%d/48 opposingToFull=%d"+
                " firstFrameNonzero=%d/48 firstOpposing=%d delayedOnly=%d"+
                " fullFirstAbovePassive=%.7f"+
                " jacobianMin=%.7f jacobianMax=%.7f"+
                " passiveContacts=%d fullContacts=%d",
                heading,PRE_X,PRE_Y,
                projectedVelocity(passive,heading),observed,abovePassive,
                headingNonzero,headingOpposing,headingFirstNonzero,
                firstOpposingToFull,delayedOnly,fullFirstAbovePassive,min,max,
                passive.contactFrames,full.contactFrames));
            assertTrue("ZERO490 heading="+heading+
                " has no measurable motor response across 48 channel/sector pairs",
                headingNonzero>0);
            assertTrue("ZERO490 heading="+heading+
                " has no measurable FIRST-FRAME actuator impulse across 48 pairs; "+
                "24-frame displacement alone cannot establish local credit sign",
                headingFirstNonzero>0);
        }
        assertEquals("four headings x sixteen sectors x three original channels",
            192,comparisons);
        assertTrue("physical motor derivative must be measurable",nonzero>0);
        Log.i("ZERO490_PHYSICS",
            "MATCHED_ORIGINAL_170_SITE_FOUR_HEADING_COMPLETE comparisons="+
            comparisons+" nonzero="+nonzero+" opposing="+opposing+
            " researchOnly=true autonomousBehaviorNotValidated=true");
    }
}
