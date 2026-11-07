package com.overmind.zero;

import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.Locale;
import static org.junit.Assert.*;

/**
 * ZERO493 research-only causal motor persistence test: restore the exact
 * user's shared Nov-07 4747-decision original 170-site physical state and
 * the untouched 512 original motor weights + 64 slow cortex states.
 *
 * Compare identical material states with motor ON vs motor OFF under 1, 2,
 * 4, 8 or 12 sequential neural motor decisions of the SAME voluntary
 * heading. Also compare alternating headings: switching a command before
 * the body reverses may destroy steering despite individually useful motors.
 *
 * This is not an autonomous behavioral test. Exogenous headings exist only
 * in this isolated Android physics benchmark, never in GameActivity.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero493ActualNewbornSustainedSteeringAndroidTest {
    private static final float DT=1f/60f;
    private static final int[] CHANNELS={0,1,3};
    static final class Trial {
        final float preX,preY,postX,postY,dx,dy,impulse;
        final int steps;
        Trial(float preX,float preY,float postX,float postY,
                float dx,float dy,float impulse,int steps){
            this.preX=preX;this.preY=preY;this.postX=postX;this.postY=postY;
            this.dx=dx;this.dy=dy;this.impulse=impulse;this.steps=steps;
        }
        float signedTravel(int h) {
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(dx,dy,h);
        }
        float signedVelocity(int h){
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(postX,postY,h);
        }
        String summary(){
            return String.format(Locale.US,
                "postVx=%.6f postVy=%.6f dx=%.6f dy=%.6f impulse=%.5f",
                postX,postY,dx,dy,impulse);
        }
    }
    static Trial replay(JSONObject user,int heading,int decisions,
            boolean powered,boolean alternate) throws Exception{
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull("Actual newborn original scene must be restorable",s);
        assertNotNull(s.matter);
        s.matter.enableOriginalZero3TractionV363();
        s.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        Zero3LearningFoundation.Coordinator controller=
            new Zero3LearningFoundation.Coordinator(cortex);
        final float preX=s.body.cvx,preY=s.body.cvy;
        final float x=s.body.cx,y=s.body.cy;
        long beforeInvalid=s.matter.invalid;
        ArrayList<UnknownWorld.Entity> noObjects=new ArrayList<>();
        float impulse=0f;
        for(int step=0;step<decisions;step++){
            int h=alternate&&(step%2==1)?0:heading;
            float ux=h==0?1f:h==2?-1f:0f;
            float uy=h==1?1f:h==3?-1f:0f;
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    493,step,cortex.revision(),ux,uy,.5f,.5f,.4f);
            controller.begin(intent,s.body,false,true,false,true,true);
            for(int tick=0;tick<24;tick++){
                float[] muscles=controller.actuate(s.body,s.skin,DT);
                if(!powered){
                    for(int sector=0;sector<16;sector++)
                        for(int ch:CHANNELS)
                            muscles[sector*4+ch]=0f;
                }
                float priorWork=s.matter.work;
                s.matter.step(s.body,s.skin,muscles,DT);
                float[] postGelX=s.skin.x.clone(),postGelY=s.skin.y.clone();
                Zero387TactileFrame.begin(s.skin);
                SymbioteWorldStepV171.interior(s.matter,s.body,s.skin,null,
                    noObjects,DT,postGelX,postGelY);
                controller.observe(s.body,DT,
                    Math.max(0f,s.matter.work-priorWork),
                    s.matter.lastMuscleReactionImpulse,false,false);
                impulse+=s.matter.lastMuscleReactionImpulse;
                assertTrue("Physical COM finite",Float.isFinite(s.body.cvx));
                assertTrue("Physical position finite",Float.isFinite(s.body.cx));
            }
            controller.finish();
        }
        assertEquals("No invalid topological motor updates",beforeInvalid,
            s.matter.invalid);
        return new Trial(preX,preY,s.body.cvx,s.body.cvy,
            s.body.cx-x,s.body.cy-y,impulse,decisions);
    }

    @Test(timeout=1500000)
    public void exactUserOriginalMotorCanItReverseSoutheastInertiaWithPersistentIntent()
            throws Exception {
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        int[] lengths={1,2,4,8,12};
        int compared=0,successfulPhysicalReversals=0;
        for(int heading:new int[]{2,3}){
            for(int n:lengths){
                Trial passive=replay(user,heading,n,false,false);
                Trial actual=replay(user,heading,n,true,false);
                float motorDV=(actual.signedVelocity(heading)-
                    passive.signedVelocity(heading));
                float motorDX=actual.signedTravel(heading)-
                    passive.signedTravel(heading);
                boolean reversed=actual.signedVelocity(heading)>0f;
                if(reversed)successfulPhysicalReversals++;
                assertTrue(Float.isFinite(motorDV));
                assertTrue(Float.isFinite(motorDX));
                compared++;
                Log.i("ZERO493_PERSISTENCE",String.format(Locale.US,
                    "heading=%d decisions=%d time=%.2fs "+
                    "passiveTravel=%.6f actualTravel=%.6f "+
                    "muscleTravelGain=%.6f passiveFinalVelocity=%.6f "+
                    "actualFinalVelocity=%.6f muscleVelocityGain=%.6f "+
                    "reversedPhysicalVelocity=%s actual=(%s)",
                    heading,n,n*.4f,
                    passive.signedTravel(heading),actual.signedTravel(heading),
                    motorDX,passive.signedVelocity(heading),
                    actual.signedVelocity(heading),motorDV,
                    Boolean.toString(reversed),actual.summary()));
            }
        }
        // Eight decisions with WEST-only vs same absolute duration of
        // WEST/EAST alternation, all on the exact same initial real body.
        Trial westHeld=replay(user,2,8,true,false);
        Trial westAlternating=replay(user,2,8,true,true);
        Trial westPassive=replay(user,2,8,false,false);
        float heldWestDV=westHeld.signedVelocity(2)-
            westPassive.signedVelocity(2);
        float alternatingWestDV=westAlternating.signedVelocity(2)-
            westPassive.signedVelocity(2);
        Log.i("ZERO493_PERSISTENCE",String.format(Locale.US,
            "SWITCHING_AB SAME_3.2S westOnlyFinalVx=%.7f "+
            "westEastAlternationFinalVx=%.7f passiveFinalVx=%.7f "+
            "westOnlyMotorDV=%.7f alternateMotorDV=%.7f "+
            "heldWestTravel=%.7f alternateWestTravel=%.7f",
            westHeld.postX,westAlternating.postX,westPassive.postX,
            heldWestDV,alternatingWestDV,westHeld.signedTravel(2),
            westAlternating.signedTravel(2)));
        assertEquals(10,compared);
        Log.i("ZERO493_PERSISTENCE",
            "SAME_ORIGINAL_4747_MOTOR_PERSISTENCE_COMPLETE"+
            " compared="+compared+" physicalReversals="+successfulPhysicalReversals+
            " noScriptedLiveBehavior=true");
    }
}
