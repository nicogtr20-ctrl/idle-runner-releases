package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * V399: actual user 33,312-decision V388 original low-level cortex + recurrent
 * physical state. Comparative CLOSED-LOOP train on truly integrated Android
 * 170-particle gel, V388 contact and world solver. Only learning rule varies.
 *
 * OLD = existing sole-V3 V390 global score, LOCAL = already-implemented V332
 * late sector-specific evidence, FROZEN = same initial weights no gradients.
 * User's checkpoint is reparsed fresh for every cohort and never written.
 * World intentions are scheduled for CAUSAL LAB only, never in GameActivity.
 * Training sources are actual user S21 body poses; wall and arrested fluid
 * are WITHHELD, never optimized with their success/failures.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero399S21LocalDelayedLearningAndroidTest {
    static final float DT=1f/60f;
    static final int OLD=0,LOCAL=1,FROZEN=2,EPISODES=32,
                     DECISIONS=16,STEPS=24;
    static final String[] LABELS={"OLD_GLOBAL","LOCAL_DELAY_V332","FROZEN"};
    static final class Pending {
        final Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe;
        final float[] evidence;
        Pending(Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 p,float[] e){
            probe=p;evidence=e.clone();
        }
    }
    static void stopVelocities(JSONObject root,String key)throws Exception{
        JSONArray array=root.getJSONArray(key);
        for(int i=0;i<array.length();i++)array.put(i,0d);
    }
    static JSONObject stopped(JSONObject original)throws Exception{
        JSONObject copy=new JSONObject(original.toString());
        JSONObject scene=new JSONObject(original.getString("training_room_1_free_scene_v379"));
        JSONObject p=scene.getJSONObject("physical");
        JSONObject body=p.getJSONObject("body");
        body.put("cvx",0d);body.put("cvy",0d);
        stopVelocities(body,"vx");stopVelocities(body,"vy");
        JSONObject shell=p.getJSONObject("surface");
        stopVelocities(shell,"vx");stopVelocities(shell,"vy");
        JSONObject gel=p.getJSONObject("symbiote");
        stopVelocities(gel,"vx");stopVelocities(gel,"vy");
        copy.put("training_room_1_scene_v379",scene.toString());
        return copy;
    }
    static JSONObject independent(JSONObject original)throws Exception{
        JSONObject copy=new JSONObject(original.toString());
        copy.put("training_room_1_scene_v379",
            original.getString("training_room_1_free_scene_v379"));
        return copy;
    }
    static final class Scene {
        final MotorBrain.Body body;
        final Morphology.Surface skin;
        final SymbioteMatterV1 gel;
        final ArrayList<UnknownWorld.Entity> world;
        Scene(JSONObject doc,boolean wall)throws Exception{
            GameSceneSnapshot.Scene snapshot=GameSceneSnapshot.workingPosture(
                doc.getString("training_room_1_scene_v379"));
            assertNotNull(snapshot);
            body=snapshot.body;skin=snapshot.skin;gel=snapshot.matter;
            gel.enableOriginalZero3TractionV363();
            gel.configurePhysicalTurnBrakeV382(-.04f);
            world=wall?snapshot.objects:new ArrayList<UnknownWorld.Entity>();
        }
    }
    static float[] heading(int h){
        return new float[]{h==0?1f:h==2?-1f:0f,
                           h==1?1f:h==3?-1f:0f};
    }
    static Zero3LearningFoundation.AdaptiveCortex evalCopy(
        Zero3LearningFoundation.AdaptiveCortex cortex){
        Zero3LearningFoundation.AdaptiveCortex c=
            new Zero3LearningFoundation.AdaptiveCortex();
        c.restore(cortex.checkpoint()); // all 512 weights and 64 recurrent states
        return c;
    }
    static float[] simulate(Scene scene,
            Zero3LearningFoundation.Coordinator control,
            Zero3LearningFoundation.AdaptiveCortex cortex,
            int h,long episode,long serial,boolean train,
            boolean explore,boolean wall)throws Exception{
        float[] dir=heading(h);
        Zero3LearningFoundation.MotorIntent intent=
            new Zero3LearningFoundation.MotorIntent(episode,serial,
                cortex.revision(),dir[0],dir[1],.5f,.5f,.4f);
        // LOCAL and FROZEN still execute one original single-site probe;
        // gradients only occur in explicitly chosen learning method.
        // Heldout evaluation must never contain motor probes, otherwise
        // post-training perturbation cursor changes the comparison.
        control.begin(intent,scene.body,train,!train,explore,true,true);
        float[] evidence=new float[16];
        for(int tick=0;tick<STEPS;tick++){
            float[] commands=control.actuate(scene.body,scene.skin,DT);
            float[] actual=commands.clone();
            float[] oldX=scene.skin.x.clone(),oldY=scene.skin.y.clone();
            float[] bodyX=scene.body.x.clone(),bodyY=scene.body.y.clone();
            float cx=scene.body.cx,cy=scene.body.cy;
            float prevWork=scene.gel.work;
            scene.gel.step(scene.body,scene.skin,actual,DT);
            float[] gelX=scene.skin.x.clone(),gelY=scene.skin.y.clone();
            Zero387TactileFrame.begin(scene.skin);
            if(wall)TrainingRoomV379.constrain(scene.body,scene.skin,
                scene.world,oldX,oldY,bodyX,bodyY);
            SymbioteWorldStepV171.interior(scene.gel,scene.body,scene.skin,
                null,scene.world,DT,gelX,gelY);
            float work=Math.max(0f,scene.gel.work-prevWork);
            if(wall)control.observeContactV390(scene.body,scene.skin,DT,work,
                scene.gel.lastMuscleReactionImpulse,false,false);
            else control.observe(scene.body,DT,work,
                scene.gel.lastMuscleReactionImpulse,false,false);
            float dx=scene.body.cx-cx,dy=scene.body.cy-cy;
            float comAlong=dx*dir[0]+dy*dir[1];
            float power=1e-6f;
            float[] sectorForce=new float[16];
            for(int k=0;k<16;k++){
                int at=k*4;
                sectorForce[k]=Math.abs(actual[at])+
                    Math.abs(actual[at+1])+Math.abs(actual[at+3]);
                power+=sectorForce[k];
            }
            for(int k=0;k<16;k++){
                float localX=0f,localY=0f;
                for(int i=k*6;i<k*6+6;i++){
                    localX+=scene.skin.x[i]-oldX[i]-dx;
                    localY+=scene.skin.y[i]-oldY[i]-dy;
                }
                float localAlong=(localX*dir[0]+localY*dir[1])/6f;
                float assignedWork=work*sectorForce[k]/power;
                // Exact approved V333 local-response formula, not a new
                // score invented after this heldout. Delayed at +0.82.
                evidence[k]+=.5f*comAlong/
                    (4f+Math.abs(dx)+Math.abs(dy))+
                    .5f*localAlong/(4f+Math.abs(localAlong))-
                    .01f*assignedWork;
            }
            assertEquals("zero external persistent topology crossing",
                0,MaterialGeometry.selfCrossings(scene.skin));
            assertEquals("no gel invalid material",0,scene.gel.invalid);
        }
        return evidence;
    }
    static void train(JSONObject actual,JSONObject other,
            Zero3LearningFoundation.AdaptiveCortex cortex,
            int arm)throws Exception{
        for(int ep=0;ep<EPISODES;ep++){
            // Own authentic original S21 position/velocity. The 16 decisions
            // share one real contiguous body, so V332 delay is not assigned
            // across artificial scene resets.
            Scene scene=new Scene(ep%2==0?actual:other,false);
            Zero3LearningFoundation.Coordinator control=
                new Zero3LearningFoundation.Coordinator(cortex);
            control.enableCausalMotorCreditV390();
            Pending pending=null;
            int h=(ep/2)%4;
            for(int decision=0;decision<DECISIONS;decision++){
                boolean old=arm==OLD;
                float[] local=simulate(scene,control,cortex,h,ep+10,
                    decision,old,true,false);
                Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe=null;
                if(arm==LOCAL)probe=cortex.captureProbeV332();
                control.finish();
                if(arm==LOCAL){
                    if(pending!=null)cortex.learnV332(
                        pending.probe,pending.evidence,local);
                    pending=new Pending(probe,local);
                }
            }
        }
    }
    static float evaluate(JSONObject doc,boolean wall,
            Zero3LearningFoundation.AdaptiveCortex source,int heading)
            throws Exception{
        Scene scene=new Scene(doc,wall);
        Zero3LearningFoundation.AdaptiveCortex cortex=evalCopy(source);
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(cortex);
        control.enableCausalMotorCreditV390();
        float bx=scene.body.cx,by=scene.body.cy;
        for(int d=0;d<5;d++){
            simulate(scene,control,cortex,heading,777,d,false,false,wall);
            control.finish();
        }
        float[] dir=heading(heading);
        return (scene.body.cx-bx)*dir[0]+(scene.body.cy-by)*dir[1];
    }
    @Test(timeout=900000)
    public void realS21OriginalBrainCompareOldVsLocalDelayedVsFrozen()
            throws Exception {
        JSONObject original=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        JSONObject independent=independent(original);
        JSONObject still=stopped(original);
        JSONObject[] contexts={original,original,independent,still};
        boolean[] walls={false,true,false,false};
        Zero3LearningFoundation.AdaptiveCortex[] cortex=
            new Zero3LearningFoundation.AdaptiveCortex[3];
        float[][] initial=new float[4][4];
        for(int c=0;c<4;c++)for(int h=0;h<4;h++)
            initial[c][h]=evaluate(contexts[c],walls[c],
                Zero390ActualUserMotorRehabilitationAndroidTest.trained(original),h);
        for(int arm=0;arm<3;arm++){
            cortex[arm]=Zero390ActualUserMotorRehabilitationAndroidTest.trained(original);
            long updates=cortex[arm].updates();
            train(original,independent,cortex[arm],arm);
            long changed=cortex[arm].updates()-updates;
            assertTrue(arm==FROZEN?changed==0:changed>0);
            float minGain=Float.POSITIVE_INFINITY,meanGain=0f;
            float eastLowest=Float.POSITIVE_INFINITY;
            int improved=0,degraded=0;
            for(int c=0;c<4;c++)for(int h=0;h<4;h++){
                float value=evaluate(contexts[c],walls[c],cortex[arm],h);
                float gain=value-initial[c][h];
                if(gain>.07f)improved++;
                if(gain<-.07f)degraded++;
                meanGain+=gain/16f;
                minGain=Math.min(minGain,gain);
                if(h==0)eastLowest=Math.min(eastLowest,value);
                Log.i("ZERO399_DELAY",String.format(Locale.US,
                    "S21_LOCAL_DELAY_AB arm=%s,scene=%d,heading=%d,"+
                    "original=%.6f,trained=%.6f,gain=%.6f",
                    LABELS[arm],c,h,initial[c][h],value,gain));
            }
            Log.i("ZERO399_DELAY",String.format(Locale.US,
                "S21_REAL_CORTEX_LEARNING arm=%s,updates=%d,"+
                "improved=%d,degraded=%d,minGain=%.6f,meanGain=%.6f,eastWorst=%.6f",
                LABELS[arm],changed,improved,degraded,minGain,meanGain,eastLowest));
        }
        Log.i("ZERO399_DELAY",
            "ZERO399_GENUINE_USER_CORTEX_3ARM=MEASURED_ONLY_NO_OTA");
    }
}
