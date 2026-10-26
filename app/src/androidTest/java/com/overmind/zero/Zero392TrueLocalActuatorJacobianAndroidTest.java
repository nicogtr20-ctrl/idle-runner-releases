package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Precise physical actuator Jacobian on authentic V388-trained S21 cortex.
 * One sector+one original CH0/CH1/CH3 muscle's signal +/-0.30, no new forces,
 * no authored policy, no replay to the user's checkpoint. All alternatives
 * start from the SAME 170 material positions, speeds and saved 64 recurrent
 * motor states. Evaluates 96 true local interventions in free space, top six
 * physically discovered improvements at the real west Training wall.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero392TrueLocalActuatorJacobianAndroidTest {
    static final float DT=1f/60f,INTERVENTION=.30f;
    static final class Result {
        final int sector,channel,sign;
        final float dx,dy,work,impulse,compression;
        Result(int s,int ch,int sign,float dx,float dy,float work,
               float impulse,float compression){
            sector=s;channel=ch;this.sign=sign;this.dx=dx;this.dy=dy;
            this.work=work;this.impulse=impulse;this.compression=compression;
        }
        String printable(){
            return "sector="+sector+",ch="+channel+",sign="+sign+
                ",dx="+dx+",dy="+dy+",impulse="+impulse+
                ",meanNegativeCH0="+compression;
        }
    }
    static Result trial(JSONObject user,boolean wall,int sector,int channel,int sign)
        throws Exception{
        return trialCombination(user,wall,0,sector<0?new int[0]:new int[]{sector},
            sector<0?new int[0]:new int[]{channel},
            sector<0?new int[0]:new int[]{sign});
    }
    /** Causal physical-only laboratory, never an alternate live AI actor. */
    static Result trialCombination(JSONObject user,boolean wall,int direction,
            int[] sites,int[] channels,int[] signs)throws Exception{
        return trialCombination(user,wall,direction,sites,channels,signs,0f);
    }
    static Result trialCombination(JSONObject user,boolean wall,int direction,
            int[] sites,int[] channels,int[] signs,float virginFraction)throws Exception{
        if(!Float.isFinite(virginFraction)||virginFraction<0f||virginFraction>1f)
            throw new IllegalArgumentException("untrusted blend fraction");
        if(sites.length!=channels.length||channels.length!=signs.length)
            throw new IllegalArgumentException("mismatched actual muscle interventions");
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            user.getString("training_room_1_scene_v379"));
        assertNotNull(scene);
        SymbioteMatterV1 gel=scene.matter;
        gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(-.04f);
        ArrayList<UnknownWorld.Entity> world=wall?
            scene.objects:new ArrayList<UnknownWorld.Entity>();
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user);
        if(virginFraction>0f){
            // Strict LAB ONLY: physically compare recovery of damaged V3
            // motor cortical coefficients. Leave original GRU64 decision
            // weights, readout, recurring cortex states and 12 saved slots
            // untouched; a NEW user checkpoint is parsed for each trial.
            java.lang.reflect.Field f=
                Zero3LearningFoundation.AdaptiveCortex.class.getDeclaredField("weights");
            f.setAccessible(true);
            float[] actual=(float[])f.get(cortex);
            float[] virgin=new Zero3LearningFoundation.AdaptiveCortex().weights();
            for(int k=0;k<actual.length;k++)
                actual[k]=(1f-virginFraction)*actual[k]+virginFraction*virgin[k];
        }
        Zero3LearningFoundation.Coordinator coordinator=
            new Zero3LearningFoundation.Coordinator(cortex);
        float firstX=scene.body.cx,firstY=scene.body.cy;
        float impulse=0,meanCompression=0f;
        float headingX=direction==0?1f:direction==2?-1f:0f;
        float headingY=direction==1?1f:direction==3?-1f:0f;
        for(int decision=0;decision<5;decision++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(3,decision,
                    cortex.revision(),headingX,headingY,.5f,.5f,.4f);
            coordinator.begin(intent,scene.body,false,true,false,true,true);
            for(int tick=0;tick<24;tick++){
                float[] muscles=coordinator.actuate(scene.body,scene.skin,DT);
                for(int c=0;c<sites.length;c++){
                    if(sites[c]<0||sites[c]>=16||
                       (channels[c]!=0&&channels[c]!=1&&channels[c]!=3)||
                       (signs[c]!=1&&signs[c]!=-1))
                        throw new IllegalArgumentException("invalid physical motor site");
                    int motorIndex=sites[c]*4+channels[c];
                    muscles[motorIndex]=Math.max(-1f,Math.min(1f,
                        muscles[motorIndex]+INTERVENTION*signs[c]));
                }
                for(int k=0;k<16;k++)
                    meanCompression+=Math.max(0f,-muscles[k*4])/16f/120f;
                float[] preX=scene.skin.x.clone(),preY=scene.skin.y.clone();
                float[] preBodyX=scene.body.x.clone(),
                        preBodyY=scene.body.y.clone();
                float oldWork=gel.work;
                gel.step(scene.body,scene.skin,muscles,DT);
                float[] gelX=scene.skin.x.clone(),gelY=scene.skin.y.clone();
                Zero387TactileFrame.begin(scene.skin);
                if(wall)TrainingRoomV379.constrain(scene.body,scene.skin,
                    world,preX,preY,preBodyX,preBodyY);
                SymbioteWorldStepV171.interior(gel,scene.body,scene.skin,null,
                    world,DT,gelX,gelY);
                impulse+=gel.lastMuscleReactionImpulse;
                coordinator.observe(scene.body,DT,
                    Math.max(0f,gel.work-oldWork),
                    gel.lastMuscleReactionImpulse,false,false);
            }
            coordinator.finish();
        }
        return new Result(sites.length==1?sites[0]:-1,
            sites.length==1?channels[0]:-1,sites.length==1?signs[0]:0,
            scene.body.cx-firstX,scene.body.cy-firstY,gel.work,impulse,meanCompression);
    }

    @Test(timeout=900000)
    public void originalS21SavedCortexDetectWhichMusclesReallyGenerateEastwardTranslation()
        throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Result passive=trial(user,false,-1,-1,0);
        Log.i("ZERO392_JACOBIAN","ORIGINAL_USER_V388_CORTEX_BASELINE_FREE "+passive.printable());
        ArrayList<Result> alternatives=new ArrayList<>();
        for(int ch:new int[]{0,1,3}){
            for(int site=0;site<16;site++){
                for(int sign:new int[]{-1,+1}){
                    Result experiment=trial(user,false,site,ch,sign);
                    alternatives.add(experiment);
                }
            }
        }
        alternatives.sort((a,b)->Float.compare(b.dx,a.dx));
        assertEquals(96,alternatives.size());
        int improvements=0;
        for(Result a:alternatives)if(a.dx>passive.dx+.05f)improvements++;
        Log.i("ZERO392_JACOBIAN","LOCAL_MUSCLE_SENSITIVITY_BASELINE="+
            passive.dx+",IMPROVING_EXPERIMENTS="+improvements+"/96"+
            ",BEST="+alternatives.get(0).dx+",WORST="+
            alternatives.get(alternatives.size()-1).dx);
        Result wallBaseline=trial(user,true,-1,-1,0);
        Log.i("ZERO392_JACOBIAN","ORIGINAL_USER_V388_WALL_BASELINE "+wallBaseline.printable());
        for(int i=0;i<6;i++){
            Result best=alternatives.get(i);
            Result withWall=trial(user,true,best.sector,best.channel,best.sign);
            Log.i("ZERO392_JACOBIAN","PHYSICALLY_DISCOVERED_MOTOR_"+
                i+" FREE "+best.printable()+" WALL "+withWall.printable());
        }
        Log.i("ZERO392_JACOBIAN",
            "ZERO392_JACOBIAN_COMPLETE=PASS_MEASUREMENT_ONLY_NO_UNPROVEN_AUTOMATIC_ACTION");
    }
}
