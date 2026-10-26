package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * True S21 V388 full saved 33,312-decision GRU and living 170-particle
 * physics, trialled with and without the actual room wall, and a second
 * user-saved pose. NEVER changes user data; 4th true geometry with velocities
 * arrested is withheld as independent physical holdout.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero396MeasuredMotorRetuningAndroidTest {
    private static final int[] EMPTY=new int[0];
    private static float travel(JSONObject doc,boolean wall,int h,float restored)
        throws Exception{
        Zero392TrueLocalActuatorJacobianAndroidTest.Result r=
            Zero392TrueLocalActuatorJacobianAndroidTest.trialCombination(
                doc,wall,h,EMPTY,EMPTY,EMPTY,restored);
        return h==0?r.dx:h==1?r.dy:h==2?-r.dx:-r.dy;
    }
    private static void zeros(JSONObject root,String field)throws Exception{
        JSONArray a=root.getJSONArray(field);
        for(int i=0;i<a.length();i++)a.put(i,0d);
    }
    private static JSONObject atRest(JSONObject origin)throws Exception{
        JSONObject copy=new JSONObject(origin.toString());
        JSONObject scene=new JSONObject(origin.getString("training_room_1_free_scene_v379"));
        JSONObject physical=scene.getJSONObject("physical");
        JSONObject body=physical.getJSONObject("body");
        body.put("cvx",0d);body.put("cvy",0d);
        zeros(body,"vx");zeros(body,"vy");
        JSONObject skin=physical.getJSONObject("surface");
        zeros(skin,"vx");zeros(skin,"vy");
        JSONObject gel=physical.getJSONObject("symbiote");
        zeros(gel,"vx");zeros(gel,"vy");
        copy.put("training_room_1_scene_v379",scene.toString());
        return copy;
    }
    @Test(timeout=900000)
    public void perDirectionMeasuredCorticalRecoveryRequiresDifferentTruePhysicalPoses()
        throws Exception{
        JSONObject original=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject second=new JSONObject(original.toString());
        second.put("training_room_1_scene_v379",
            original.getString("training_room_1_free_scene_v379"));
        JSONObject independentRest=atRest(original);
        Zero396MeasuredMotorRetuning learner=new Zero396MeasuredMotorRetuning();
        assertEquals("No prefixed steering east",0f,learner.approved(0),0f);
        assertEquals("No innate preferences for any of 8 headings",0f,
            learner.approved(5),0f);
        for(int heading=0;heading<4;heading++){
            JSONObject[] scenes={original,original,second};
            boolean[] walls={false,true,false};
            float[] baseline=new float[3];
            for(int scenario=0;scenario<3;scenario++)
                baseline[scenario]=travel(scenes[scenario],walls[scenario],heading,0f);
            for(int index=0;index<4;index++){
                float fraction=Zero396MeasuredMotorRetuning.fraction(index);
                for(int scenario=0;scenario<3;scenario++){
                    float goal=travel(scenes[scenario],walls[scenario],heading,fraction);
                    learner.recordMeasuredPair(heading,index,scenario,goal-baseline[scenario]);
                }
            }
            float suggested=learner.provisional(heading);
            float initialRest=travel(independentRest,false,heading,0f);
            float shadowRest=travel(independentRest,false,heading,suggested);
            float withheldGain=shadowRest-initialRest;
            learner.confirmHeldout(heading,suggested,withheldGain);
            Log.i("ZERO396_CORTEX_PHYSICAL",
                "REAL_S21_DIRECTION_CALIBRATION heading="+heading+
                ",suggestedFraction="+suggested+
                ",holdoutAtRestBaseline="+initialRest+
                ",holdoutAtRestSelected="+shadowRest+
                ",holdoutGain="+withheldGain+
                ",approved="+learner.approved(heading));
            assertTrue(learner.ready(heading));
            assertTrue("Measured fraction cannot bypass the physical holdout",
                learner.approved(heading)==0f || withheldGain>.07f);
        }
        for(int heading=4;heading<8;heading++)
            assertEquals("Unknown headings remain physically uncalibrated",
                0f,learner.approved(heading),0f);
        Log.i("ZERO396_CORTEX_PHYSICAL",
            "ZERO396_MULTICONTEXT_CAUSAL_MOTOR_BENCH=PASS_MEASUREMENT_ONLY");
    }
    @Test public void absentOrContradictoryPhysicalProofForbidsMotorRetuning(){
        Zero396MeasuredMotorRetuning model=new Zero396MeasuredMotorRetuning();
        for(int i=0;i<4;i++){
            for(int c=0;c<3;c++){
                float effect=i==0?.4f: i==1?-.2f:.01f;
                model.recordMeasuredPair(0,i,c,effect);
            }
        }
        assertEquals(.25f,model.provisional(0),0f);
        model.confirmHeldout(0,.25f,-.5f);
        assertEquals("Train gain alone NEVER justifies motor rollback",
            0f,model.approved(0),0f);
        assertEquals(0f,model.approved(6),0f);
    }
}
