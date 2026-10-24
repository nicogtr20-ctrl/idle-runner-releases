package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * FORENSIC, not a new actor. Uses the precise S21 saved gel, V356 cortex and
 * independently saved old motor, NOT invented rewards or a scripted live gait.
 * Compare three downstream blend strengths and a passive physical baseline
 * from the same exact initial condition. No checkpoint is modified.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero385MotorAuthorityABAndroidTest {
    private static final float DT=1f/60f;
    private static void report(String message){
        System.out.println(message);
        android.os.Bundle b=new android.os.Bundle();
        b.putString("stream",message);
        InstrumentationRegistry.getInstrumentation().sendStatus(0,b);
    }
    static final class Result {
        final String scene;final int mode,angle;
        float dx,dy,along,perp,deltaAgainstCoast,reach,work,impulse;
        int invalid,stalled;
        Result(String scene,int mode,int angle){this.scene=scene;this.mode=mode;this.angle=angle;}
        String csv(){
            return scene+","+mode+","+angle+","+dx+","+dy+","+along+","+perp+","+
                deltaAgainstCoast+","+reach+","+work+","+impulse+","+invalid+","+stalled;
        }
    }
    private static String inputFile(){
        return new File(InstrumentationRegistry.getInstrumentation().getTargetContext()
            .getExternalFilesDir(null),"zero385-user-readonly.json").getAbsolutePath();
    }
    private static Zero3LearningFoundation.AdaptiveCortex savedCortex(String checkpoint)
        throws Exception{
        Zero3OriginalBrainV356.Readout readout=Zero3OriginalBrainV356.inspectSaved(checkpoint);
        Field field=Zero3OriginalBrainV356.Readout.class.getDeclaredField("cortex");
        field.setAccessible(true);
        return ((Zero3LearningFoundation.AdaptiveCortex)field.get(readout))
            .forkForEvaluation();
    }
    private static Result trial(JSONObject user,String sceneKey,int mode,int angle)
        throws Exception{
        GameSceneSnapshot.Scene snapshot=GameSceneSnapshot.workingPosture(user.getString(sceneKey));
        assertNotNull("Authentic original user fluid physical scene",snapshot);
        assertNotNull(snapshot.matter);
        final boolean walls=sceneKey.startsWith("training");
        MotorBrain.Body body=snapshot.body;Morphology.Surface skin=snapshot.skin;
        SymbioteMatterV1 gel=snapshot.matter;ArrayList<UnknownWorld.Entity> world=
            walls?snapshot.objects:new ArrayList<>();
        gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(-.04f);
        SymbioteMotorHeadV1 old=new SymbioteMotorHeadV1();
        assertTrue("User's actual archived V2.2 motor loads",old.restore(user.getString("motor")));
        assertTrue("User's zero-valued local motor loads",old.restoreLocal(user.getString("local")));
        assertTrue("User's zero-valued fast circuit loads",old.restoreFast(user.getString("fast")));
        old.setAdaptationEnabled(false);
        Zero3LearningFoundation.AdaptiveCortex cortex=savedCortex(user.getString("brain"));
        Zero3LearningFoundation.Coordinator controller=
            new Zero3LearningFoundation.Coordinator(cortex);
        float theta=(float)(angle*Math.PI/2);
        float x=(float)Math.cos(theta),y=(float)Math.sin(theta);
        if(Math.abs(x)<.0001f)x=0;if(Math.abs(y)<.0001f)y=0;
        Result r=new Result(walls?"wall":"free",mode,angle);
        float startX=body.cx,startY=body.cy;
        float initVx=body.cvx,initVy=body.cvy,meanPulse=0;
        // Two authentic physics seconds, SAME user gel/motor/cortex start,
        // only the diagnostic stimulus varies. Frozen means no learning here.
        for(int decision=0;decision<5;decision++){
            Zero3LearningFoundation.MotorIntent intent=new Zero3LearningFoundation.MotorIntent(
                1,decision,cortex.revision(),x,y,.5f,.5f,.4f);
            controller.begin(intent,body,false,true,false,true,true);
            if(mode==0||mode==2)Zero3MotorPeripheralBridgeV364.begin(old,body,skin,null,world,x,y);
            for(int frame=0;frame<24;frame++){
                float[] commands=controller.actuate(body,skin,DT);
                if(mode==0||mode==2){
                    Zero3MotorPeripheralBridgeV364.fastPhysicalRefine(commands,old,body,skin,DT);
                    if(mode==2){ // OLD motor only: no actor ownership, diagnostic only.
                        float[] fromOld=old.commands();
                        for(int k=0;k<16;k++)for(int ch:new int[]{0,1,3})
                            commands[k*4+ch]=fromOld[k*4+ch];
                    }
                }else if(mode==3)Arrays.fill(commands,0f);
                float[] prevX=skin.x.clone(),prevY=skin.y.clone();
                float[] prevBx=body.x.clone(),prevBy=body.y.clone();
                float before=gel.work;
                gel.step(body,skin,commands,DT);
                r.work+=gel.work-before;
                r.impulse+=gel.lastMuscleReactionImpulse;
                if(walls){
                    Arrays.fill(skin.touchSense,0f);
                    TrainingRoomV379.constrain(body,skin,world,prevX,prevY,prevBx,prevBy);
                    SymbioteWorldStepV171.interior(gel,body,skin,null,world,DT);
                    int touched=0;for(float v:skin.touchSense)if(v>.2f)touched++;
                    if(touched>20)r.stalled++;
                }
                controller.observe(body,DT,Math.max(0,gel.work-before),
                    gel.lastMuscleReactionImpulse,false,false);
            }
            controller.finish();
        }
        r.dx=body.cx-startX;r.dy=body.cy-startY;
        r.along=r.dx*x+r.dy*y;
        r.perp=-r.dx*y+r.dy*x;
        float reach=0;for(int i=0;i<Morphology.POINTS;i++)
            reach=Math.max(reach,(float)Math.hypot(skin.x[i]-body.cx,
                skin.y[i]-body.cy));
        r.reach=reach;r.invalid=(int)gel.invalid;
        r.deltaAgainstCoast=r.along-(initVx*x+initVy*y)*2f;
        assertTrue("No nonfinite cortex/body",Float.isFinite(r.dx)&&
            Float.isFinite(r.dy)&&Float.isFinite(r.along)&&Float.isFinite(r.reach));
        return r;
    }
    static String reversalTrial(JSONObject user,String sceneKey,float brake)
        throws Exception{
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(user.getString(sceneKey));
        assertNotNull(scene);assertNotNull(scene.matter);
        SymbioteMatterV1 gel=scene.matter;
        gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(brake);
        Zero3LearningFoundation.AdaptiveCortex cortex=savedCortex(user.getString("brain"));
        Zero3LearningFoundation.Coordinator owner=
            new Zero3LearningFoundation.Coordinator(cortex);
        float startX=scene.body.cx,startY=scene.body.cy;
        float[] segments=new float[6];
        float totalImpulse=0f;
        int crossingFrames=0;
        final float[] commands={1f,1f,-1f,-1f,1f,1f};
        for(int d=0;d<commands.length;d++){
            float heading=commands[d];
            gel.observedOriginalMotorChangeV382(heading,0f);
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(1,d,cortex.revision(),
                    heading,0,.5f,.5f,.4f);
            owner.begin(intent,scene.body,false,true,false,true,true);
            float x0=scene.body.cx;
            for(int step=0;step<24;step++){
                float[] motor=owner.actuate(scene.body,scene.skin,DT);
                float w=gel.work;
                gel.step(scene.body,scene.skin,motor,DT);
                totalImpulse+=gel.lastMuscleReactionImpulse;
                if(MaterialGeometry.selfCrossings(scene.skin)>0)crossingFrames++;
                owner.observe(scene.body,DT,Math.max(0,gel.work-w),
                    gel.lastMuscleReactionImpulse,false,false);
            }
            owner.finish();
            segments[d]=scene.body.cx-x0;
        }
        return "scene="+sceneKey+",brake="+brake+
            ",comX="+(scene.body.cx-startX)+",comY="+(scene.body.cy-startY)+
            ",phaseX="+java.util.Arrays.toString(segments)+
            ",impulse="+totalImpulse+",crossedFrames="+crossingFrames;
    }

    @Test(timeout=900000)
    public void actualS21VelocityReversalComparesPassivePhysicalBraking() throws Exception{
        JSONObject user=new JSONObject(new String(Files.readAllBytes(
            new File(inputFile()).toPath()),StandardCharsets.UTF_8));
        for(String scene:new String[]{"physical_scene_v100",
                                      "training_room_1_scene_v379"}){
            for(float brake:new float[]{-.04f,0f,.025f,.05f})
                report("ZERO386_TRUE_REVERSAL_AB,"+reversalTrial(user,scene,brake));
        }
    }

    @Test(timeout=900000)
    public void savedRealS21TrainingSceneCrossingMustRecoverAfterWorldContact()
        throws Exception{
        File file=new File(inputFile());
        assertTrue("Read-only original S21 physical data required",file.isFile());
        JSONObject user=new JSONObject(new String(Files.readAllBytes(file.toPath()),
            StandardCharsets.UTF_8));
        GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(
            user.getString("training_room_1_scene_v379"));
        assertNotNull(scene);assertNotNull(scene.matter);
        assertEquals("Actual observed crossed membrane segments from S21",
            1,MaterialGeometry.selfCrossings(scene.skin));
        float beforeX=scene.body.cx,beforeY=scene.body.cy;
        int remaining=scene.matter.reconcileExternalContourV386(
            scene.body,scene.skin,1f/60f);
        report("ZERO386_S21_CROSSING_FIX,remaining="+remaining+
            ",areaError="+scene.matter.areaRelativeError(scene.skin)+
            ",move="+Math.hypot(scene.body.cx-beforeX,scene.body.cy-beforeY));
        assertEquals("A true self-crossed membrane must not persist",0,remaining);
        assertTrue("Preserve true cortex area",scene.matter.areaRelativeError(scene.skin)<.15f);
        assertTrue("Never teleport living center to a world goal",
            Math.hypot(scene.body.cx-beforeX,scene.body.cy-beforeY)<6f);
    }
    @Test(timeout=900000)
    public void testSavedPhysicalMotorAuthorityVsFrozenUntrainedReadout() throws Exception{
        File fixture=new File(inputFile());
        assertTrue("Read-only authentic S21 data must be provided by CI",fixture.isFile());
        JSONObject user=new JSONObject(new String(Files.readAllBytes(fixture.toPath()),
            StandardCharsets.UTF_8));
        String raw=user.getString("brain");
        Zero3OriginalBrainV356.Readout status=Zero3OriginalBrainV356.inspectSaved(raw);
        assertEquals("Exact S21 saved neural decision counter, not a virgin brain",
            22081,status.decisions);
        assertEquals(12797,status.motorUpdates);
        String motor=user.getString("motor");
        assertTrue("Confirmed zero-update historical motor",motor.split("\\|")[3].equals("0"));
        String local=user.getString("local");
        for(String s:local.split("\\|"))if(!s.equals("cl161")){
            assertEquals("No learned local motor weights",0f,Float.parseFloat(s),0f);
        }
        report("ZERO385_AUTHENTIC_S21_TRIAL,scene,mode(0=75%,1=cortex-only,2=old-only,3=coast),intent-90deg,dx,dy,along,perp,along-pre-velocity-2s,reach,work,muscle-impulse,invalid,heavy-wall-contact-frames");
        for(String scene:new String[]{"physical_scene_v100","training_room_1_scene_v379"}){
            for(int mode=0;mode<4;mode++){
                for(int angle=0;angle<(mode==3?1:4);angle++){
                    Result r=trial(user,scene,mode,angle);
                    report("ZERO385_AUTHENTIC_S21_TRIAL,"+r.csv());
                }
            }
        }
        report("ZERO385_NO_BEHAVIOR_PATCH_NO_USER_PROFILE_CHANGE_COMPLETE");
    }
}
