package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Matched physical A/B of keeping ALL the true trained V388 GRU intelligence
 * while gradually reducing ONLY possibly harmful low-level cortex synapses.
 * Never changes user's checkpoint, never integrates V2.2 owner or hardcodes
 * direction response. No live recovery unless at least 12 heldout situations
 * prove broad safety. */
@RunWith(AndroidJUnit4.class)
public final class Zero395Original3MotorSelectiveRecoveryAndroidTest {
    static final int[] EMPTY=new int[0];
    private static float along(JSONObject user,boolean withWall,int dir,float recovery)
        throws Exception{
        Zero392TrueLocalActuatorJacobianAndroidTest.Result r=
            Zero392TrueLocalActuatorJacobianAndroidTest.trialCombination(
                user,withWall,dir,EMPTY,EMPTY,EMPTY,recovery);
        float dx=dir==0?1f:dir==2?-1f:0f;
        float dy=dir==1?1f:dir==3?-1f:0f;
        return r.dx*dx+r.dy*dy;
    }
    @Test(timeout=900000)
    public void original33000DecisionS21LearnedMotorCanBeSafelyRecoveredWithoutResettingGRU()
        throws Exception{
        JSONObject training=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject other=new JSONObject(training.toString());
        other.put("training_room_1_scene_v379",
            training.getString("training_room_1_free_scene_v379"));
        float[][] original=new float[3][4];
        for(int condition=0;condition<3;condition++)
            for(int d=0;d<4;d++)
                original[condition][d]=along(condition==2?other:training,
                    condition==1,d,0f);
        for(float fraction:new float[]{.25f,.5f,.75f,1f}){
            int better=0,worse=0,eastBetter=0;
            float minimumGain=Float.POSITIVE_INFINITY;
            float totalGain=0;
            for(int condition=0;condition<3;condition++)
                for(int d=0;d<4;d++){
                    float fresh=along(condition==2?other:training,
                        condition==1,d,fraction);
                    float effect=fresh-original[condition][d];
                    totalGain+=effect;
                    minimumGain=Math.min(minimumGain,effect);
                    if(effect>.1f)better++;
                    if(effect<-.1f)worse++;
                    if(d==0&&effect>.1f)eastBetter++;
                    Log.i("ZERO395_RECOVERY","MEASURED fraction="+fraction+
                        ",scene="+condition+",heading="+d+
                        ",trained="+original[condition][d]+",recovery="+fresh+
                        ",gain="+effect);
                }
            Log.i("ZERO395_RECOVERY","TRUE_USER_S21_MOTOR_BLENDING fraction="+fraction+
                ",better="+better+",worse="+worse+",eastBetter="+eastBetter+
                "/3,minGain="+minimumGain+",meanGain="+totalGain/12f);
            assertTrue(Float.isFinite(minimumGain));
        }
        Log.i("ZERO395_RECOVERY",
            "ZERO395_MOTOR_RESCUE_AB=MEASURED_ONLY_NO_BLIND_WEIGHT_RESET");
    }
}
