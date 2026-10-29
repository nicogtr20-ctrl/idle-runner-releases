package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Zero413 CAUSAL physical test on genuine V388 33,312-decision trained S21.
 * Every intervention begins with the SAME exact restored 170 material sites,
 * original velocities, wall objects, original 512 motor weights and 64 slow
 * cortical states. The ONLY experimental variable is a real 3.0 muscle
 * command sent to SymbioteMatterV1.step. No high-level direction is injected
 * into GameActivity and no experiment is taught or saved to the user's brain.
 *
 * This is an actuator-physics/credit IDENTIFICATION experiment, NOT a 10k
 * autonomous behavioral substitute. ZERO412 10k still gates release.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero413MatchedPhysicalMotorCausalityAndroidTest {
    private static final float DT=1f/60f, PERTURBATION=.40f;
    private static final int FRAMES=24;
    private static final int PASSIVE=0,FULL=1,INTERVENTION=2,ISOLATED=3;
    private static final int[] CHANNELS={0,1,3};

    static final class Trial {
        final float dx,dy,deltaVx,deltaVy,initialAx,initialAy,work,impulse;
        final int invalid,contactFrames;
        Trial(float dx,float dy,float vx,float vy,float firstAx,float firstAy,
                float work,float impulse,int invalid,int contacts){
            this.dx=dx;this.dy=dy;deltaVx=vx;deltaVy=vy;
            initialAx=firstAx;initialAy=firstAy;this.work=work;
            this.impulse=impulse;this.invalid=invalid;contactFrames=contacts;
        }
        float along(int heading){
            return heading==0?dx:heading==1?dy:heading==2?-dx:-dy;
        }
        float brakeAgainstWest(){
            return deltaVx;
        }
        String serialize(){
            return String.format(Locale.US,
                "dx=%.6f dy=%.6f dVx=%.6f dVy=%.6f firstAx=%.6f"+
                " firstAy=%.6f work=%.6f impulse=%.6f invalid=%d contacts=%d",
                dx,dy,deltaVx,deltaVy,initialAx,initialAy,
                work,impulse,invalid,contactFrames);
        }
    }

    static final class Scenario {
        final String name;
        final JSONObject scene;
        final boolean wall;
        final float extraX,extraY;
        final int heading;
        Scenario(String name,JSONObject scene,boolean wall,int heading,
                float extraX,float extraY){
            this.name=name;this.scene=scene;this.wall=wall;
            this.heading=heading;this.extraX=extraX;this.extraY=extraY;
        }
    }

    static void shiftWholeBody(GameSceneSnapshot.Scene s,float vx,float vy){
        // Purely change TRUE initial momentum of EVERY real material particle.
        // No magic center displacement, spring reset or controller target.
        s.body.cvx+=vx;s.body.cvy+=vy;
        for(int i=0;i<MotorBrain.N;i++){
            s.body.vx[i]+=vx;s.body.vy[i]+=vy;
        }
        for(int i=0;i<Morphology.POINTS;i++){
            s.skin.vx[i]+=vx;s.skin.vy[i]+=vy;
        }
        for(int i=0;i<s.matter.vx.length;i++){
            s.matter.vx[i]+=vx;s.matter.vy[i]+=vy;
        }
    }

    static Trial run(Scenario situation,JSONObject original,int mode,
            int sector,int channel,int sign)throws Exception{
        GameSceneSnapshot.Scene s=GameSceneSnapshot.workingPosture(
            situation.scene.getString("training_room_1_scene_v379"));
        assertNotNull("Original S21 real saved posture required",s);
        assertNotNull(s.matter);
        if(situation.extraX!=0f||situation.extraY!=0f)
            shiftWholeBody(s,situation.extraX,situation.extraY);
        SymbioteMatterV1 gel=s.matter;
        gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(-.04f);
        ArrayList<UnknownWorld.Entity> world=situation.wall?
            s.objects:new ArrayList<UnknownWorld.Entity>();
        Zero3LearningFoundation.AdaptiveCortex trained=
            Zero390ActualUserMotorRehabilitationAndroidTest.trained(original);
        trained.enablePhysicalContextV412();
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(trained);
        float ux=situation.heading==0?1f:situation.heading==2?-1f:0f;
        float uy=situation.heading==1?1f:situation.heading==3?-1f:0f;
        Zero3LearningFoundation.MotorIntent intention=
            new Zero3LearningFoundation.MotorIntent(
                413,0,trained.revision(),ux,uy,.5f,.5f,.4f);
        control.begin(intention,s.body,false,true,false,true,true);
        final float startX=s.body.cx,startY=s.body.cy;
        final float startVx=s.body.cvx,startVy=s.body.cvy;
        float firstAx=0f,firstAy=0f,impulse=0f;
        int contacts=0;
        for(int tick=0;tick<FRAMES;tick++){
            float[] command=control.actuate(s.body,s.skin,DT);
            // Never edit the cortex's command array: actuate returns a
            // mutable scratch buffer that represents actual outgoing muscle
            // actions, and any intervention is restricted to this one frame.
            if(mode==PASSIVE){
                Arrays.fill(command,0f);
            }else if(mode==ISOLATED){
                for(int site=0;site<16;site++)
                    for(int motor:new int[]{0,1,3})
                        command[site*4+motor]=0f;
                command[sector*4+channel]=sign*.7f;
            }else if(mode==INTERVENTION){
                int at=sector*4+channel;
                command[at]=Math.max(-1f,Math.min(1f,
                    command[at]+PERTURBATION*sign));
            }
            final float preVx=s.body.cvx,preVy=s.body.cvy;
            float[] preX=s.skin.x.clone(),preY=s.skin.y.clone();
            float[] bodyX=s.body.x.clone(),bodyY=s.body.y.clone();
            float lastWork=gel.work;
            gel.step(s.body,s.skin,command,DT);
            float[] gelX=s.skin.x.clone(),gelY=s.skin.y.clone();
            Zero387TactileFrame.begin(s.skin);
            if(situation.wall){
                TrainingRoomV379.constrain(s.body,s.skin,world,
                    preX,preY,bodyX,bodyY);
                int count=0;
                for(int i=0;i<Morphology.POINTS;i++)
                    if(s.skin.touchSense[i]>.1f)count++;
                if(count>0)contacts++;
            }
            SymbioteWorldStepV171.interior(gel,s.body,s.skin,null,
                world,DT,gelX,gelY);
            if(tick==0){
                firstAx=(s.body.cvx-preVx)/DT;
                firstAy=(s.body.cvy-preVy)/DT;
            }
            control.observe(s.body,DT,Math.max(0f,gel.work-lastWork),
                gel.lastMuscleReactionImpulse,false,false);
            impulse+=gel.lastMuscleReactionImpulse;
            assertEquals("True original S21 physics must stay valid",
                0,gel.invalid);
        }
        control.finish();
        float dx=s.body.cx-startX,dy=s.body.cy-startY;
        Trial out=new Trial(dx,dy,s.body.cvx-startVx,s.body.cvy-startVy,
            firstAx,firstAy,gel.work,impulse,gel.invalid,contacts);
        assertTrue(Float.isFinite(out.dx));
        assertTrue(Float.isFinite(out.deltaVx));
        assertTrue(Float.isFinite(out.firstAx));
        return out;
    }

    @Test(timeout=1500000)
    public void same170BodyIsolatedMuscleCounterfactualsFindControllabilityVsLearning()
            throws Exception{
        JSONObject user=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout saved=
            Zero3OriginalBrainV356.inspectSaved(user.getString("brain"));
        assertEquals(33312L,saved.decisions);
        assertEquals(19452L,saved.motorUpdates);
        JSONObject free=Zero412TrueS21MotorContextPhysicalABAndroidTest.savedFree(user);
        JSONObject stopped=Zero412TrueS21MotorContextPhysicalABAndroidTest.stopped(user);
        Scenario[] contexts={
            new Scenario("ORIGINAL_S21_DRIFT_EAST",user,false,0,0,0),
            new Scenario("ORIGINAL_S21_WEST_WALL",user,true,0,0,0),
            new Scenario("INDEPENDENT_S21_EAST",free,false,0,0,0),
            new Scenario("INDEPENDENT_S21_REST_EAST",stopped,false,0,0,0),
            new Scenario("INDEPENDENT_S21_WESTWARD_60U",free,false,0,-60,0),
            new Scenario("ORIGINAL_S21_WEST_CHECK",user,false,2,0,0)
        };
        int totalMuscles=0,totalImprovers=0,totalBrakers=0,contextsWithAny=0;
        for(Scenario situation:contexts){
            Trial passive=run(situation,user,PASSIVE,-1,-1,0);
            Trial actual=run(situation,user,FULL,-1,-1,0);
            float originalPhysicalEffect=actual.along(situation.heading)-
                passive.along(situation.heading);
            float originalBrakeEffect=actual.brakeAgainstWest()-
                passive.brakeAgainstWest();
            Trial best=null;
            int bestSector=-1,bestChannel=-1,bestSign=0;
            float bestImprove=-Float.MAX_VALUE,bestBrake=-Float.MAX_VALUE;
            int improved=0,braking=0,signReversals=0;
            for(int sector=0;sector<16;sector++){
                for(int channel:CHANNELS){
                    Trial minus=run(situation,user,INTERVENTION,
                        sector,channel,-1);
                    Trial plus=run(situation,user,INTERVENTION,
                        sector,channel,+1);
                    Trial[] experiments={minus,plus};
                    float signedEffect=plus.along(situation.heading)-
                        minus.along(situation.heading);
                    if(Math.abs(signedEffect)>.001f)signReversals++;
                    for(int index=0;index<experiments.length;index++){
                        Trial trial=experiments[index];
                        int sign=index==0?-1:1;
                        float delta=trial.along(situation.heading)-
                            actual.along(situation.heading);
                        float brake=trial.brakeAgainstWest()-
                            actual.brakeAgainstWest();
                        if(delta>.02f)improved++;
                        if(brake>.05f)braking++;
                        if(delta>bestImprove){
                            bestImprove=delta;best=trial;
                            bestSector=sector;bestChannel=channel;
                            bestSign=sign;
                        }
                        bestBrake=Math.max(bestBrake,brake);
                    }
                    totalMuscles+=2;
                }
            }
            if(improved>0)contextsWithAny++;
            totalImprovers+=improved;totalBrakers+=braking;
            assertNotNull(best);
            Log.i("ZERO413_CAUSAL",String.format(Locale.US,
                "ZERO413_TRUE_MATCHED_S21 scenario=%s heading=%d"+
                " passiveSigned=%.6f cortexSigned=%.6f trueMuscleEffect=%.6f"+
                " cortexMinusPassiveBrakeWest=%.6f bestOneMuscleGain=%.6f"+
                " bestSector=%d bestChannel=%d bestSign=%d"+
                " isolatedImprovers=%d/96 brakingMuscles=%d/96"+
                " bilateralSignEffects=%d/48 bestBrakeChange=%.6f"+
                " passiveDVx=%.6f cortexDVx=%.6f bestDVx=%.6f"+
                " passiveFirstAx=%.6f cortexFirstAx=%.6f bestFirstAx=%.6f"+
                " passiveWallFrames=%d cortexWallFrames=%d",
                situation.name,situation.heading,passive.along(situation.heading),
                actual.along(situation.heading),originalPhysicalEffect,
                originalBrakeEffect,bestImprove,bestSector,bestChannel,bestSign,
                improved,braking,signReversals,bestBrake,passive.deltaVx,
                actual.deltaVx,best.deltaVx,passive.initialAx,
                actual.initialAx,best.initialAx,passive.contactFrames,
                actual.contactFrames));
            // Explicitly apply the discovered actuator alone against an
            // otherwise passive true S21 body, avoiding the false conclusion
            // that a correlated trained cortex is necessary for its authority.
            Trial isolated=run(situation,user,ISOLATED,bestSector,
                bestChannel,bestSign);
            Log.i("ZERO413_CAUSAL",String.format(Locale.US,
                "ZERO413_TRUE_ISOLATED_S21 scenario=%s isolatedDx=%.6f"+
                " isolatedDVx=%.6f isolatedFirstAx=%.6f"+
                " isolatedVsPassiveGain=%.6f physicalMotorWork=%.6f",
                situation.name,isolated.dx,isolated.deltaVx,
                isolated.initialAx,
                isolated.along(situation.heading)-
                    passive.along(situation.heading),
                isolated.work));
        }
        Log.i("ZERO413_CAUSAL",
            "ZERO413_GENUINE_33312_CORTEX_MATCHED_MOTOR_CAUSALITY_COMPLETE"+
            " contexts="+contexts.length+" actualCounterfactuals="+totalMuscles+
            " improving="+totalImprovers+" braking="+totalBrakers+
            " scenariosWithPhysicalMotorImprovement="+contextsWithAny);
        // A green science test measures what happened. It NEVER turns an
        // inverse signed displacement into positive motion by assertion.
        assertEquals(576,totalMuscles);
    }
}
