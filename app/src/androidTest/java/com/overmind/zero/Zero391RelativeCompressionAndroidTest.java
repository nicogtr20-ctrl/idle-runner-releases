package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public final class Zero391RelativeCompressionAndroidTest {
    private static final float DT=1f/60f;
    private static String trial(JSONObject user,boolean differential,boolean wall,int direction)
        throws Exception{
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            user.getString("training_room_1_scene_v379"));
        SymbioteMatterV1 gel=scene.matter;
        assertNotNull(gel);
        gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(-.04f);
        gel.configureRelativeCompressionLabV391(differential);
        ArrayList<UnknownWorld.Entity> world=wall?
            scene.objects:new ArrayList<UnknownWorld.Entity>();
        Zero3LearningFoundation.AdaptiveCortex c=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(user);
        Zero3LearningFoundation.Coordinator owner=new Zero3LearningFoundation.Coordinator(c);
        final float x0=scene.body.cx,y0=scene.body.cy;
        float vx=direction==0?1:direction==2?-1:0;
        float vy=direction==1?1:direction==3?-1:0;
        float totalImpulse=0,meanCompression=0,wallFrames=0,avgStance=.0f;
        for(int k=0;k<5;k++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(3,k,c.revision(),
                    vx,vy,.5f,.5f,.4f);
            owner.begin(intent,scene.body,false,true,false,true,true);
            for(int tick=0;tick<24;tick++){
                float[] cmd=owner.actuate(scene.body,scene.skin,DT);
                float compression=0;
                for(int sector=0;sector<16;sector++)
                    compression+=Math.max(0f,-cmd[sector*4])/16f;
                meanCompression+=compression/120f;
                float[] sx=scene.skin.x.clone(),sy=scene.skin.y.clone();
                float[] bx=scene.body.x.clone(),by=scene.body.y.clone();
                float work=gel.work;
                gel.step(scene.body,scene.skin,cmd,DT);
                float[] preCollisionX=scene.skin.x.clone(),
                        preCollisionY=scene.skin.y.clone();
                Zero387TactileFrame.begin(scene.skin);
                if(wall)TrainingRoomV379.constrain(scene.body,scene.skin,world,
                    sx,sy,bx,by);
                SymbioteWorldStepV171.interior(gel,scene.body,scene.skin,null,
                    world,DT,preCollisionX,preCollisionY);
                int contacts=0;for(float sense:scene.skin.touchSense)
                    if(sense>.23)contacts++;
                if(contacts>0)wallFrames++;
                totalImpulse+=gel.lastMuscleReactionImpulse;
                owner.observe(scene.body,DT,Math.max(0f,gel.work-work),
                    gel.lastMuscleReactionImpulse,false,false);
            }
            owner.finish();
        }
        float dx=scene.body.cx-x0,dy=scene.body.cy-y0;
        return "relative="+differential+",wall="+wall+",dir="+direction+
            ",along="+(dx*vx+dy*vy)+",dx="+dx+",dy="+dy+
            ",meanCompression="+meanCompression+
            ",wallContactFrames="+wallFrames+",muscleImpulse="+totalImpulse;
    }
    @Test(timeout=900000)
    public void actualS21FullRecurrentCortexABUniformGroundCompressionCanSuppressMotor()
        throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        for(boolean withWall:new boolean[]{false,true})
            for(int direction=0;direction<4;direction++)
                for(boolean differential:new boolean[]{false,true})
                    Log.i("ZERO391_FOOTING_AB",
                        "ZERO391_REAL_V388_S21,"+trial(user,differential,withWall,direction));
        Log.i("ZERO391_FOOTING_AB",
            "ZERO391_S21_COMPLETE=PASS_MEASURED_NOT_IMPROVEMENT_CERTIFIED");
    }
}
