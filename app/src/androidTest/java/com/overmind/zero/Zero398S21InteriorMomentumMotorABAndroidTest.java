package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V398: genuine original saved user 33,312-decision GRU3 cortex and its 170
 * live material nodes. The environment and velocity field are replayed from
 * actual S21 uploads, never scripted to a target. A/B changes TWO material
 * rheology parameters independently of the neural intent and motor outputs.
 *
 * First THREE contexts are calibration, fourth is withheld at-rest posture.
 * All gains + true signed motion must be reported: green CI means the study
 * RAN, not that any motor version is eligible for deployment.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero398S21InteriorMomentumMotorABAndroidTest {
    static final int[] EMPTY=new int[0];
    static final float[] DAMP={2.3f,6f,12f,24f};
    static final float[] SHEAR={.55f,1.20f,2.50f};
    private static final float[][] PARAMS={
        {2.3f,.55f}, {6f,.55f}, {12f,.55f}, {24f,.55f},
        {2.3f,1.20f}, {2.3f,2.50f},
        {6f,1.20f}, {12f,1.20f}, {12f,2.50f}, {24f,2.50f}
    };
    static float travel(JSONObject doc,boolean wall,int heading,float drag,float shear)
        throws Exception {
        Zero392TrueLocalActuatorJacobianAndroidTest.Result r=
            Zero392TrueLocalActuatorJacobianAndroidTest.trialCombination(
                doc,wall,heading,EMPTY,EMPTY,EMPTY,0f,false,drag,shear);
        float progress=heading==0?r.dx:heading==1?r.dy:
                       heading==2?-r.dx:-r.dy;
        assertTrue(Float.isFinite(progress));
        return progress;
    }
    static void zeroVector(JSONObject root,String key)throws Exception{
        JSONArray values=root.getJSONArray(key);
        for(int i=0;i<values.length();i++)values.put(i,0d);
    }
    static JSONObject rested(JSONObject original)throws Exception{
        JSONObject copy=new JSONObject(original.toString());
        JSONObject scene=new JSONObject(original.getString(
            "training_room_1_free_scene_v379"));
        JSONObject p=scene.getJSONObject("physical");
        JSONObject b=p.getJSONObject("body");
        b.put("cvx",0d);b.put("cvy",0d);
        zeroVector(b,"vx");zeroVector(b,"vy");
        JSONObject skin=p.getJSONObject("surface");
        zeroVector(skin,"vx");zeroVector(skin,"vy");
        JSONObject inner=p.getJSONObject("symbiote");
        zeroVector(inner,"vx");zeroVector(inner,"vy");
        copy.put("training_room_1_scene_v379",scene.toString());
        return copy;
    }

    @Test(timeout=900000)
    public void originalS21ActualRheologyGridWithGenuineHoldout()
            throws Exception {
        JSONObject s21=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject second=new JSONObject(s21.toString());
        second.put("training_room_1_scene_v379",
            s21.getString("training_room_1_free_scene_v379"));
        JSONObject rest=rested(s21);
        JSONObject[] docs={s21,s21,second,rest};
        boolean[] wall={false,true,false,false};
        float[][] reference=new float[4][4];
        for(int condition=0;condition<4;condition++)for(int h=0;h<4;h++)
            reference[condition][h]=travel(docs[condition],wall[condition],
                h,2.3f,.55f);
        for(float[] p:PARAMS){
            float minGain=Float.POSITIVE_INFINITY;
            float minEastGain=Float.POSITIVE_INFINITY;
            float holdoutMinGain=Float.POSITIVE_INFINITY;
            float minRealEast=Float.POSITIVE_INFINITY;
            int increases=0,decreases=0;
            float[] sumByHeading=new float[4];
            for(int condition=0;condition<4;condition++){
                for(int heading=0;heading<4;heading++){
                    float actual=travel(docs[condition],wall[condition],
                        heading,p[0],p[1]);
                    float gain=actual-reference[condition][heading];
                    if(condition!=3)minGain=Math.min(minGain,gain);
                    else holdoutMinGain=Math.min(holdoutMinGain,gain);
                    if(heading==0){
                        minEastGain=Math.min(minEastGain,gain);
                        minRealEast=Math.min(minRealEast,actual);
                    }
                    sumByHeading[heading]+=gain;
                    if(gain>.07f)increases++;
                    if(gain<-.07f)decreases++;
                }
            }
            Log.i("ZERO398_RHEOLOGY",String.format(Locale.US,
                "REAL_S21_FULL_GEOMETRY sweepDrag=%.2f,shear=%.2f,"+
                "trainMin=%.6f,heldoutMin=%.6f,eastMinGain=%.6f,"+
                "eastWorstActual=%.6f,increases=%d,decreases=%d,"+
                "meanEastGain=%.6f,meanWestGain=%.6f,"+
                "meanSouthGain=%.6f,meanNorthGain=%.6f",
                p[0],p[1],minGain,holdoutMinGain,minEastGain,
                minRealEast,increases,decreases,sumByHeading[0]/4f,
                sumByHeading[2]/4f,sumByHeading[1]/4f,
                sumByHeading[3]/4f));
        }
        Log.i("ZERO398_RHEOLOGY",
            "ZERO398_ALL_MATERIAL_SWEEPS=MEASURED_NOT_CERTIFIED");
    }

    @Test public void originalV397PhysicalLawIsDefaultWithoutAnyNewMotorAuthority()
        throws Exception {
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        // Configuration is strictly a scientific, downstream material model.
        // The V3 cortex still owns 100% of CH0/CH1/CH3 and V319 owns CH2.
        try {
            gel.configureInteriorDynamicsLabV398(100f,1f);
            fail("Unphysical damping must be rejected");
        }catch(IllegalArgumentException expected){}
        try {
            gel.configureInteriorDynamicsLabV398(5f,5f);
            fail("Unphysical shear must be rejected");
        }catch(IllegalArgumentException expected){}
        gel.configureInteriorDynamicsLabV398(2.3f,.55f);
        assertEquals("Sole original V3 body untouched at newborn",
            0,gel.ticks);
    }
}
