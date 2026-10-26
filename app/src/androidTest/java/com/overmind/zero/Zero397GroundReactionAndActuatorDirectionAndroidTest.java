package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO397 genuine S21 physical-dynamics LAB. Tests both conservation and
 * independent physical contexts, with original saved GRU3 motor signals.
 * Strictly no user save writes, no direction scripts in the actual game.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero397GroundReactionAndActuatorDirectionAndroidTest {
    private static final int[] EMPTY=new int[0];
    private static final float DT=1f/60f;

    static float along(JSONObject doc,boolean wall,int heading,boolean revised)
            throws Exception {
        Zero392TrueLocalActuatorJacobianAndroidTest.Result result=
            Zero392TrueLocalActuatorJacobianAndroidTest.trialCombination(
                doc,wall,heading,EMPTY,EMPTY,EMPTY,0f,revised);
        float v=heading==0?result.dx:heading==1?result.dy:
            heading==2?-result.dx:-result.dy;
        assertTrue("Physical displacement finite",Float.isFinite(v));
        return v;
    }

    @Test public void physicalFootholdTransfersMissingMomentumToFloorNotCore()
            throws Exception {
        JSONObject source=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        GameSceneSnapshot.Scene original=GameSceneSnapshot.workingPosture(
            source.getString("training_room_1_scene_v379"));
        assertNotNull(original);
        Morphology.Surface skin=original.skin;
        Arrays.fill(skin.vx,0f);Arrays.fill(skin.vy,0f);
        Arrays.fill(skin.gripHold,0f);
        final int site=6;
        final int n=MotorBrain.N+SymbioteMatterV1.INNER;
        float[] px=new float[n],py=new float[n],
                vx=new float[n],vy=new float[n];
        Arrays.fill(px,100000f);Arrays.fill(py,100000f);
        px[0]=skin.x[site];py[0]=skin.y[site];vx[0]=12f;
        float[] mobility=new float[Morphology.POINTS];
        Arrays.fill(mobility,1f);
        mobility[site]=.1f;
        float preSkinV=skin.vx[site],preFluidV=vx[0];
        float transferred=SymbioteMatterV1.exchangeCortexFluidShear(
            skin,px,py,vx,vy,DT,mobility);
        float fluidDelta=vx[0]-preFluidV;
        float skinDelta=skin.vx[site]-preSkinV;
        assertTrue("A loaded foothold absorbs real shear",transferred>0f);
        assertTrue("Liquid loses its velocity to the loaded cortex",fluidDelta<0f);
        assertTrue("Physical stance prevents interior from freely dragging cortex",
            skinDelta>0f&&skinDelta<Math.abs(fluidDelta));
        float netWeighted=skinDelta/Morphology.POINTS+fluidDelta/n;
        assertEquals("Missing internal momentum exactly accounted by substrate",
            -transferred/Morphology.POINTS,netWeighted,2e-5f);
        Log.i("ZERO397_MATERIAL","GROUND_REACTION_MOMENTUM=PASS"+
            ",groundImpulse="+transferred+",skinDV="+skinDelta+
            ",fluidDV="+fluidDelta+",netWeighted="+netWeighted);
    }

    @Test public void actualS21ConcaveCortexNeverCommandsRadiallyInvertedExtension()
            throws Exception {
        JSONObject doc=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            doc.getString("training_room_1_scene_v379"));
        assertNotNull(scene);
        float sign=SymbioteMatterV1.signedArea(scene.skin)>=0?1f:-1f;
        float[] dir=new float[2],rotated=new float[2];
        float cx=scene.body.cx,cy=scene.body.cy;
        float[] oldX=scene.skin.x.clone(),oldY=scene.skin.y.clone();
        for(int i=0;i<Morphology.POINTS;i++){
            float rx=oldX[i]-cx,ry=oldY[i]-cy;
            float d=(float)Math.hypot(rx,ry);
            if(d<.01f)continue;
            SymbioteMatterV1.actuatorDirectionAt(scene.skin,i,cx,cy,sign,dir);
            float projection=(dir[0]*rx+dir[1]*ry)/d;
            assertTrue("Cannot invert CH0 extension on concave material site "+i,
                projection>.10f);
            assertEquals(1f,(float)Math.hypot(dir[0],dir[1]),1e-4f);
        }
        // All local geometry vectors must rotate with the tissue, not the world.
        for(int i=0;i<Morphology.POINTS;i++){
            scene.skin.x[i]=-oldY[i];scene.skin.y[i]=oldX[i];
        }
        for(int i=0;i<Morphology.POINTS;i++){
            SymbioteMatterV1.actuatorDirectionAt(scene.skin,i,-cy,cx,sign,rotated);
            scene.skin.x[i]=oldX[i];scene.skin.y[i]=oldY[i];
            SymbioteMatterV1.actuatorDirectionAt(scene.skin,i,cx,cy,sign,dir);
            scene.skin.x[i]=-oldY[i];scene.skin.y[i]=oldX[i];
            assertEquals("Actuator rotation x="+i,-dir[1],rotated[0],1e-3f);
            assertEquals("Actuator rotation y="+i,dir[0],rotated[1],1e-3f);
        }
        Log.i("ZERO397_MATERIAL","CONCAVE_ACTUATOR_OUTWARD_AND_ROTATION=PASS");
    }

    private static void stopVelocities(JSONObject root,String field)
            throws Exception {
        JSONArray array=root.getJSONArray(field);
        for(int i=0;i<array.length();i++)array.put(i,0d);
    }
    private static JSONObject independentRest(JSONObject original)throws Exception{
        JSONObject copy=new JSONObject(original.toString());
        JSONObject scene=new JSONObject(
            original.getString("training_room_1_free_scene_v379"));
        JSONObject physical=scene.getJSONObject("physical");
        JSONObject body=physical.getJSONObject("body");
        body.put("cvx",0d);body.put("cvy",0d);
        stopVelocities(body,"vx");stopVelocities(body,"vy");
        JSONObject skin=physical.getJSONObject("surface");
        stopVelocities(skin,"vx");stopVelocities(skin,"vy");
        JSONObject liquid=physical.getJSONObject("symbiote");
        stopVelocities(liquid,"vx");stopVelocities(liquid,"vy");
        copy.put("training_room_1_scene_v379",scene.toString());
        return copy;
    }

    @Test(timeout=900000)
    public void realS21MotorResponseAcrossIndependentPhysicalWorlds()
            throws Exception {
        JSONObject original=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject second=new JSONObject(original.toString());
        second.put("training_room_1_scene_v379",
            original.getString("training_room_1_free_scene_v379"));
        JSONObject atRest=independentRest(original);
        JSONObject[] scenes={original,original,second,atRest};
        boolean[] walls={false,true,false,false};
        float[] minGain={Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,
                         Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY};
        int improvements=0,degradations=0;
        for(int scene=0;scene<scenes.length;scene++)for(int h=0;h<4;h++){
            float oldTravel=along(scenes[scene],walls[scene],h,false);
            float revised=along(scenes[scene],walls[scene],h,true);
            float gain=revised-oldTravel;
            minGain[h]=Math.min(minGain[h],gain);
            if(gain>.07f)improvements++;
            if(gain<-.07f)degradations++;
            Log.i("ZERO397_MATERIAL","REAL_S21_LIVE_POSTURE case="+scene+
                ",heading="+h+",baseline="+oldTravel+
                ",groundReaction="+revised+",gain="+gain);
        }
        Log.i("ZERO397_MATERIAL","REAL_S21_PHYSICAL_AB=MEASURED_ONLY"+
            ",improvements="+improvements+",degradations="+degradations+
            ",minEast="+minGain[0]+",minSouth="+minGain[1]+
            ",minWest="+minGain[2]+",minNorth="+minGain[3]);
        assertEquals("Four signed directional groups were measured",4,minGain.length);
        for(float value:minGain)assertTrue(Float.isFinite(value));
    }
}
