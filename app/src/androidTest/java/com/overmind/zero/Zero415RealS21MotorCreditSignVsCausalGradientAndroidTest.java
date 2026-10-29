package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Scientific diagnosis of a suspected root error: V332/V400 credit is a
 * correlation of the selected muscle noise and RAW skin+COM response,
 * not the actual actuator-induced motor effect. Compare the EXACT returned
 * V400 per-sector learning measurement against a true matched local
 * finite-difference muscle Jacobian on the ORIGINAL saved S21 170-body.
 *
 * No alteration to game, backend, saved cortex, or 10k live acceptance.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero415RealS21MotorCreditSignVsCausalGradientAndroidTest {
    @Test(timeout=1100000)
    public void trainedS21ProvesWhetherV332LocalCreditPointsTowardActualMotorEffect()
            throws Exception{
        JSONObject saved=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout neural=
            Zero3OriginalBrainV356.inspectSaved(saved.getString("brain"));
        assertEquals(33312L,neural.decisions);
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(saved);
        JSONObject atRest=Zero412TrueS21MotorContextPhysicalABAndroidTest.stopped(saved);
        Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario[] contexts={
            new Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario(
                "ACTUAL_USER_WALL_EAST",saved,true,0,0,0),
            new Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario(
                "ACTUAL_USER_FREE_EAST",saved,false,0,0,0),
            new Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario(
                "INDEPENDENT_USER_REST_EAST",atRest,false,0,0,0),
            new Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario(
                "INDEPENDENT_USER_WEST",free,false,2,0,0)
        };
        int examined=0,opposite=0,agrees=0,tooTiny=0;
        int[] byChannelTotal=new int[3],byChannelWrong=new int[3];
        float worstCausal=0f,worstWrongCredit=0f;
        for(Zero413MatchedPhysicalMotorCausalityAndroidTest.Scenario env:contexts){
            int localCount=0,localWrong=0,localAgree=0;
            double avgTrueAbs=0,avgPredAbs=0;
            for(int sec=0;sec<16;sec++){
                for(int c=0;c<3;c++){
                    int ch=c==2?3:c;
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial minus=
                        Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                            env,saved,2,sec,ch,-1);
                    Zero413MatchedPhysicalMotorCausalityAndroidTest.Trial plus=
                        Zero413MatchedPhysicalMotorCausalityAndroidTest.run(
                            env,saved,2,sec,ch,+1);
                    float actualGradient=plus.along(env.heading)-
                        minus.along(env.heading);
                    float v332LocalGradient=plus.rawV400Credit[sec]-
                        minus.rawV400Credit[sec];
                    // Test only meaningful actual actuator response.
                    if(Math.abs(actualGradient)<.005f||
                       Math.abs(v332LocalGradient)<.000001f){
                        tooTiny++;
                        continue;
                    }
                    examined++;localCount++;
                    byChannelTotal[c]++;
                    avgTrueAbs+=Math.abs(actualGradient);
                    avgPredAbs+=Math.abs(v332LocalGradient);
                    if(actualGradient*v332LocalGradient<0f){
                        opposite++;localWrong++;byChannelWrong[c]++;
                        if(Math.abs(actualGradient)>Math.abs(worstCausal)){
                            worstCausal=actualGradient;
                            worstWrongCredit=v332LocalGradient;
                        }
                    }else{
                        agrees++;localAgree++;
                    }
                }
            }
            Log.i("ZERO415_CREDIT",String.format(Locale.US,
                "ZERO415_TRUE_S21_CAUSAL_CREDIT_SIGN context=%s"+
                " measuredInterventions=%d physicallyOppositeWrongCredits=%d"+
                " matchedSignCredits=%d wrongFraction=%.6f"+
                " meanAbsRealEffect=%.7f meanAbsRawV332Signal=%.7f",
                env.name,localCount,localWrong,localAgree,
                localWrong/(double)Math.max(1,localCount),
                avgTrueAbs/Math.max(1,localCount),
                avgPredAbs/Math.max(1,localCount)));
        }
        Log.i("ZERO415_CREDIT",String.format(Locale.US,
            "ZERO415_REAL_S21_MOTOR_CREDIT_CAUSAL_SIGN_AUDIT"+
            " measuredActualEffects=%d oppositeWeightLearningDirections=%d"+
            " matchingDirections=%d tinyOrAmbiguous=%d mismatchRate=%.6f"+
            " channel0Wrong=%d/%d channel1Wrong=%d/%d channel3Wrong=%d/%d"+
            " strongestContradictionActualDelta=%.7f correspondingCreditDelta=%.7f",
            examined,opposite,agrees,tooTiny,
            opposite/(double)Math.max(1,examined),
            byChannelWrong[0],byChannelTotal[0],
            byChannelWrong[1],byChannelTotal[1],
            byChannelWrong[2],byChannelTotal[2],
            worstCausal,worstWrongCredit));
        assertEquals("96 genuine isolated muscle pairs per physical context",
            192,examined+tooTiny);
        assertTrue("At least one original trained S21 muscle must actually"+
            " have a causally measurable physical effect",examined>0);
        // This diagnostic never asserts that *a wrong gradient is fine*.
        // The actual mismatch count above is the scientific result.
    }
}
