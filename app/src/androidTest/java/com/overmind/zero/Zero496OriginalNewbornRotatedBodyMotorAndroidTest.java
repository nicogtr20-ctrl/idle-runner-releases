package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO496: diagnose absolute X/Y motor defects against material-geometry bias.
 * Original 2026-11-07 saved 4747-decision cortex, intact 512 synapses, 64
 * local states, 170 real body particles. A genuine coordinate rotation of
 * ALL material positions and velocities together with the chosen heading
 * should produce the SAME physical response after inverse rotation.
 *
 * Includes a different experiment: with truly zero original inertia, rotate
 * body shape 180 degrees but request the SAME world WEST. If sign changes,
 * body morphology/sector-to-ground traction is directionally significant.
 * Never rotates the real game, forces a heading, or touches phone saves.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero496OriginalNewbornRotatedBodyMotorAndroidTest {
    static final float DT=1f/60f;
    static float[] rot(float x,float y,int turns){
        switch(turns&3){
            case 0:return new float[]{x,y};
            case 1:return new float[]{-y,x};
            case 2:return new float[]{-x,-y};
            default:return new float[]{y,-x};
        }
    }
    static void turnArrays(JSONObject obj,String posx,String posy,
            float cx,float cy,int turns,boolean positions)throws Exception{
        JSONArray a=obj.getJSONArray(posx),b=obj.getJSONArray(posy);
        assertEquals(a.length(),b.length());
        for(int i=0;i<a.length();i++){
            float x=(float)a.getDouble(i),y=(float)b.getDouble(i);
            if(positions){x-=cx;y-=cy;}
            float[] p=rot(x,y,turns);
            a.put(i,positions?p[0]+cx:p[0]);
            b.put(i,positions?p[1]+cy:p[1]);
        }
    }
    static String rotated(String original,int turns,boolean zeroAllVelocity)
            throws Exception{
        JSONObject root=new JSONObject(original);
        JSONObject body=root.getJSONObject("body");
        JSONObject skin=root.getJSONObject("surface");
        JSONObject gel=root.getJSONObject("symbiote");
        float cx=(float)body.getDouble("cx"),cy=(float)body.getDouble("cy");
        for(JSONObject o:new JSONObject[]{body,skin,gel}){
            turnArrays(o,"x","y",cx,cy,turns,true);
            turnArrays(o,"vx","vy",0,0,turns,false);
            if(zeroAllVelocity){
                JSONArray vx=o.getJSONArray("vx"),vy=o.getJSONArray("vy");
                for(int i=0;i<vx.length();i++){vx.put(i,0d);vy.put(i,0d);}
            }
        }
        float[] cv=rot((float)body.getDouble("cvx"),
            (float)body.getDouble("cvy"),turns);
        body.put("cvx",zeroAllVelocity?0:cv[0]);
        body.put("cvy",zeroAllVelocity?0:cv[1]);
        return root.toString();
    }
    static final class Trial {
        final float dx,dy,postX,postY,first64[];
        Trial(float dx,float dy,float vx,float vy,float[] cmd){
            this.dx=dx;this.dy=dy;this.postX=vx;this.postY=vy;
            this.first64=cmd;
        }
    }
    static Trial replay(JSONObject user,int originalHeading,int decisions,
            int turns,boolean rotateHeading,boolean zeroMomentum)
            throws Exception{
        String src=rotated(user.getString("physical_scene_v100"),
            turns,zeroMomentum);
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(src);
        assertNotNull("Rotation preserves original material validity",s);
        assertNotNull(s.matter);
        s.matter.enableOriginalZero3TractionV363();
        s.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        Zero3LearningFoundation.Coordinator motor=
            new Zero3LearningFoundation.Coordinator(cortex);
        int h=rotateHeading?(originalHeading+turns)%4:originalHeading;
        float x=h==0?1f:h==2?-1f:0f;
        float y=h==1?1f:h==3?-1f:0f;
        float startX=s.body.cx,startY=s.body.cy;
        long inv=s.matter.invalid;
        float[] first=null;
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        for(int k=0;k<decisions;k++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    496,k,cortex.revision(),x,y,.5f,.5f,.4f);
            motor.begin(intent,s.body,false,true,false,true,true);
            for(int f=0;f<24;f++){
                float[] out=motor.actuate(s.body,s.skin,DT);
                if(first==null)first=out.clone();
                float beforeWork=s.matter.work;
                s.matter.step(s.body,s.skin,out,DT);
                float[] gx=s.skin.x.clone(),gy=s.skin.y.clone();
                Zero387TactileFrame.begin(s.skin);
                SymbioteWorldStepV171.interior(s.matter,s.body,s.skin,null,
                    empty,DT,gx,gy);
                motor.observe(s.body,DT,Math.max(0,s.matter.work-beforeWork),
                    s.matter.lastMuscleReactionImpulse,false,false);
            }
            motor.finish();
        }
        assertEquals("No invalid physical area/topology",inv,s.matter.invalid);
        return new Trial(s.body.cx-startX,s.body.cy-startY,
            s.body.cvx,s.body.cvy,first);
    }

    @Test(timeout=1500000)
    public void sameRealNewbornPhysicalSolverCoRotationalAndBodyShapeDiagnostics()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        int cases=0;
        for(int heading:new int[]{2,3}){
            for(int n:new int[]{1,8,12}){
                Trial base=replay(user,heading,n,0,true,false);
                for(int turns:new int[]{1,2}){
                    Trial rotated=replay(user,heading,n,turns,true,false);
                    float[] expected=rot(base.dx,base.dy,turns);
                    float[] expectedV=rot(base.postX,base.postY,turns);
                    float commandError=0f;
                    for(int i=0;i<64;i++)
                        commandError=Math.max(commandError,
                            Math.abs(base.first64[i]-rotated.first64[i]));
                    float displacementError=(float)Math.hypot(
                        expected[0]-rotated.dx,expected[1]-rotated.dy);
                    float velocityError=(float)Math.hypot(
                        expectedV[0]-rotated.postX,expectedV[1]-rotated.postY);
                    Log.i("ZERO496_ROTATION",String.format(Locale.US,
                        "equivariance originalH=%d decisions=%d rotateQuarter=%d "+
                        "commandLinf=%.8f traveledError=%.7f velocityError=%.7f "+
                        "baseV=(%.7f,%.7f) rotatedV=(%.7f,%.7f)",
                        heading,n,turns,commandError,displacementError,
                        velocityError,base.postX,base.postY,
                        rotated.postX,rotated.postY));
                    assertTrue(Float.isFinite(commandError));
                    assertTrue(Float.isFinite(velocityError));
                    cases++;
                }
            }
        }
        for(int heading:new int[]{2,3}){
            Trial original=replay(user,heading,8,0,false,true);
            Trial turned=replay(user,heading,8,2,false,true);
            Log.i("ZERO496_ROTATION",String.format(Locale.US,
                "bodyMorphologyZeroVelocity requestH=%d n=8 "+
                "originalPostV=(%.7f,%.7f) rotateBodyOnlyPostV=(%.7f,%.7f) "+
                "originalTravel=(%.7f,%.7f) rotateBodyOnlyTravel=(%.7f,%.7f)",
                heading,original.postX,original.postY,
                turned.postX,turned.postY,original.dx,original.dy,
                turned.dx,turned.dy));
            cases++;
        }
        assertEquals(14,cases);
        Log.i("ZERO496_ROTATION",
            "EXACT_NEWBORN_ROTATION_MOTOR_CAUSALITY_COMPLETE cases="+cases+
            " researchOnly=true productionUnchanged=true");
    }
}
