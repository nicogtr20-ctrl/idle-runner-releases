package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * ZERO498: exact saved Nov-7 V489 original newborn 4747-decision cortex,
 * 170 real particles, all pre-velocities, real V363 substrate traction.
 *
 * This is a diagnostic library of generic mechanical transductions,
 * each of which receives ONLY the original voluntary GRU heading and the
 * local membrane geometry. Absolutely no target, object label, scripted
 * world direction, pose rotation or artificial COM force is introduced.
 *
 * Compare against the intact original cortex AND real muscle OFF, on same
 * true physical user posture. These are laboratory interventions only;
 * most modes are NOT approved for live game or real training.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero498PhysicsGroundedMotorSynergyAndroidTest {
    static final float DT=1f/60f, AMP=.80f;
    static float clip(float x){return Math.max(-1f,Math.min(1f,x));}
    static float dot(float x,float y,float dx,float dy){return x*dx+y*dy;}
    static void move(float[] cmd, MotorBrain.Body body,Morphology.Surface skin,
            float desireX,float desireY,int mode) {
        if(mode==0)return;
        for(int sector=0;sector<16;sector++){
            int offset=4*sector;
            if(mode==1){
                cmd[offset]=cmd[offset+1]=cmd[offset+3]=0f;
                continue;
            }
            float x=0,y=0;
            for(int p=6*sector;p<6*(sector+1);p++){
                x+=skin.x[p]-body.cx;y+=skin.y[p]-body.cy;
            }
            float n=(float)Math.hypot(x,y);
            if(n<.01f){x=1f;y=0;n=1f;}
            float radial=dot(x/n,y/n,desireX,desireY);
            float tangent=dot(-y/n,x/n,desireX,desireY);
            float c0=0f,c1=0f;
            switch(mode){
                case 2:c0= AMP*radial;break;
                case 3:c0=-AMP*radial;break;
                case 4:c1= AMP*tangent;break;
                case 5:c1=-AMP*tangent;break;
                case 6:c0= AMP*radial;c1= AMP*tangent;break;
                case 7:c0=-AMP*radial;c1=-AMP*tangent;break;
                case 8:c0= AMP*radial;c1=-AMP*tangent;break;
                case 9:c0=-AMP*radial;c1=AMP*tangent;break;
                case 10:c0=-.4f-.4f*radial;break;
                case 11:c0=-.4f+.4f*radial;break;
                case 12:c0=-.4f-.4f*radial;c1=-AMP*tangent;break;
                case 13:c0=-.4f+.4f*radial;c1=AMP*tangent;break;
                case 14:c0=-.4f-.4f*radial;c1=AMP*tangent;break;
                case 15:c0=-.4f+.4f*radial;c1=-AMP*tangent;break;
                case 16:cmd[offset]=clip(cmd[offset]+.45f*radial);continue;
                case 17:cmd[offset]=clip(cmd[offset]-.45f*radial);continue;
                case 18:cmd[offset+1]=clip(cmd[offset+1]+.45f*tangent);continue;
                case 19:cmd[offset+1]=clip(cmd[offset+1]-.45f*tangent);continue;
                default:throw new IllegalArgumentException("experimental mode");
            }
            cmd[offset]=clip(c0);
            cmd[offset+1]=clip(c1);
            cmd[offset+3]=0f;
        }
    }
    static final class Result {
        final float dx,dy,vx,vy,impulse,work;
        Result(float dx,float dy,float vx,float vy,float impulse,float work){
            this.dx=dx;this.dy=dy;this.vx=vx;this.vy=vy;
            this.impulse=impulse;this.work=work;
        }
        float towardVelocity(int h){
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(vx,vy,h);
        }
        float towardTravel(int h){
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(dx,dy,h);
        }
    }
    static Result physics(JSONObject user,int heading,int mode,int decisions)
            throws Exception{
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull("Nov7 original physical snapshot is valid",s);
        assertNotNull(s.matter);
        s.matter.enableOriginalZero3TractionV363();
        s.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex trained=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        Zero3LearningFoundation.Coordinator controller=
            new Zero3LearningFoundation.Coordinator(trained);
        float ux=heading==0?1f:heading==2?-1f:0f;
        float uy=heading==1?1f:heading==3?-1f:0f;
        float startX=s.body.cx,startY=s.body.cy,startWork=s.matter.work;
        float impulse=0f;
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        long invalid=s.matter.invalid;
        for(int decision=0;decision<decisions;decision++){
            Zero3LearningFoundation.MotorIntent intention=
                new Zero3LearningFoundation.MotorIntent(
                    498,decision,trained.revision(),ux,uy,.5f,.5f,.4f);
            controller.begin(intention,s.body,false,true,false,true,true);
            for(int tick=0;tick<24;tick++){
                float[] actual=controller.actuate(s.body,s.skin,DT);
                move(actual,s.body,s.skin,ux,uy,mode);
                float before=s.matter.work;
                s.matter.step(s.body,s.skin,actual,DT);
                float[] gx=s.skin.x.clone(),gy=s.skin.y.clone();
                Zero387TactileFrame.begin(s.skin);
                SymbioteWorldStepV171.interior(s.matter,s.body,s.skin,null,
                    empty,DT,gx,gy);
                controller.observe(s.body,DT,Math.max(0f,s.matter.work-before),
                    s.matter.lastMuscleReactionImpulse,false,false);
                impulse+=s.matter.lastMuscleReactionImpulse;
                assertTrue(Float.isFinite(s.body.cx));
                assertTrue(Float.isFinite(s.body.cvx));
            }
            controller.finish();
        }
        assertEquals("No invalid physical material",invalid,s.matter.invalid);
        return new Result(s.body.cx-startX,s.body.cy-startY,
            s.body.cvx,s.body.cvy,impulse,s.matter.work-startWork);
    }

    @Test(timeout=1500000)
    public void exactNov7BodyFindsPhysicallyEffectiveGenericInverseMuscleTransductions()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        int comparisons=0;
        for(int heading:new int[]{2,3}){
            int best=-1;
            float bestVelocity=-Float.MAX_VALUE;
            float bestTravel=-Float.MAX_VALUE;
            float originalVelocity=0f,passiveVelocity=0f;
            for(int mode=0;mode<=19;mode++){
                Result result=physics(user,heading,mode,8);
                float desired=result.towardVelocity(heading);
                float traveled=result.towardTravel(heading);
                if(mode==0)originalVelocity=desired;
                if(mode==1)passiveVelocity=desired;
                if(mode>=2&&desired>bestVelocity){
                    bestVelocity=desired;bestTravel=traveled;best=mode;
                }
                Log.i("ZERO498_INVERSE",String.format(Locale.US,
                    "heading=%d mode=%d duration=3.2 "+
                    "realFinalHeadingVelocity=%.7f realHeadingTravel=%.7f "+
                    "worldV=(%.7f,%.7f) impulse=%.3f work=%.6f",
                    heading,mode,desired,traveled,result.vx,result.vy,
                    result.impulse,result.work));
                comparisons++;
            }
            Result hold=physics(user,heading,best,12);
            Log.i("ZERO498_INVERSE",String.format(Locale.US,
                "summary heading=%d originalV=%.7f passiveV=%.7f "+
                "inSampleBestMode=%d bestV=%.7f bestTravel=%.7f "+
                "best12FinalV=%.7f best12Travel=%.7f",
                heading,originalVelocity,passiveVelocity,best,bestVelocity,
                bestTravel,hold.towardVelocity(heading),
                hold.towardTravel(heading)));
        }
        assertEquals(40,comparisons);
        Log.i("ZERO498_INVERSE",
            "ORIGINAL_4747_DYNAMIC_ANATOMICAL_MUSCLE_INVERSE_COMPLETE"+
            " comparisons="+comparisons+" bodyAndBrainPreserved=true");
    }
}
