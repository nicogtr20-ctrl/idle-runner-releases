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
 * ZERO497 pure research, original Nov-7 4747-decision learned motor on the
 * exact user's 170-material body. All 64 output synapses and slow recurrent
 * neural units are restored unchanged. Compare the actual original neural
 * CH0/CH1/CH3 commands against all 16 cyclic sector reallocations of those
 * SAME commands, on identical saved posture/velocity with the SAME heading.
 *
 * No injected body impulse, world rotation, 2.2 motor, stored weight change,
 * autonomous selection or live scripted gait. Original V319 object CH2 grip
 * untouched. This demonstrates whether the motor's POST-CORTEX sector mapping
 * alone can give useful directional traction in a formerly wrong-way body.
 * The 16-way best sample is optimistically chosen IN-SAMPLE for diagnosis
 * only, not a learned validated policy or production motor.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero497OriginalBodyNeuralMusclePhaseAlignmentAndroidTest {
    static final float DT=1f/60f;
    static final int[] CH={0,1,3};

    static final class Outcome{
        final float vx,vy,dx,dy,impulse;
        Outcome(float vx,float vy,float dx,float dy,float impulse){
            this.vx=vx;this.vy=vy;this.dx=dx;this.dy=dy;
            this.impulse=impulse;
        }
        float directionSpeed(int heading) {
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(vx,vy,heading);
        }
        float directionTravel(int heading) {
            return Zero492ActualNewbornMotorCausalityAndroidTest.project(dx,dy,heading);
        }
    }
    static Outcome replay(JSONObject user,int heading,int turns,int decisions)
            throws Exception{
        GameSceneSnapshot.Scene s=GameSceneSnapshot.restore(
            user.getString("physical_scene_v100"));
        assertNotNull(s);assertNotNull(s.matter);
        s.matter.enableOriginalZero3TractionV363();
        s.matter.configurePhysicalTurnBrakeV382(-.04f);
        Zero3LearningFoundation.AdaptiveCortex cortex=
            Zero492ActualNewbornMotorCausalityAndroidTest.cortex(user);
        Zero3LearningFoundation.Coordinator motor=
            new Zero3LearningFoundation.Coordinator(cortex);
        float x=heading==0?1f:heading==2?-1f:0f;
        float y=heading==1?1f:heading==3?-1f:0f;
        final float beginX=s.body.cx,beginY=s.body.cy;
        long initialInvalid=s.matter.invalid;
        float impulse=0f;
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        for(int dec=0;dec<decisions;dec++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(
                    497,dec,cortex.revision(),x,y,.5f,.5f,.4f);
            motor.begin(intent,s.body,false,true,false,true,true);
            for(int frame=0;frame<24;frame++){
                float[] out=motor.actuate(s.body,s.skin,DT);
                if(turns!=0){
                    float[] first=out.clone();
                    for(int sector=0;sector<16;sector++){
                        int src=((sector+turns)%16)*4;
                        int dst=sector*4;
                        for(int ch:CH)out[dst+ch]=first[src+ch];
                    }
                }
                float oldWork=s.matter.work;
                s.matter.step(s.body,s.skin,out,DT);
                float[] gx=s.skin.x.clone(),gy=s.skin.y.clone();
                Zero387TactileFrame.begin(s.skin);
                SymbioteWorldStepV171.interior(s.matter,s.body,s.skin,null,
                    empty,DT,gx,gy);
                motor.observe(s.body,DT,Math.max(0f,s.matter.work-oldWork),
                    s.matter.lastMuscleReactionImpulse,false,false);
                impulse+=s.matter.lastMuscleReactionImpulse;
                assertTrue(Float.isFinite(s.body.cvx));
                assertTrue(Float.isFinite(s.body.cvy));
            }
            motor.finish();
        }
        assertEquals("Actual 170-site solver valid",initialInvalid,s.matter.invalid);
        return new Outcome(s.body.cvx,s.body.cvy,
            s.body.cx-beginX,s.body.cy-beginY,impulse);
    }

    @Test(timeout=1500000)
    public void actualUserMaterialSixteenMuscleSectorRotationsAgainstOriginalRightBias()
            throws Exception{
        JSONObject user=Zero492ActualNewbornMotorCausalityAndroidTest.realNewborn();
        int total=0;
        for(int heading:new int[]{2,3}){
            Outcome original=replay(user,heading,0,8);
            int best=0,worst=0,reverseCount=0;
            float top=-Float.MAX_VALUE,bottom=Float.MAX_VALUE;
            for(int shift=0;shift<16;shift++){
                Outcome observed=replay(user,heading,shift,8);
                float signed=observed.directionSpeed(heading);
                if(signed>top){top=signed;best=shift;}
                if(signed<bottom){bottom=signed;worst=shift;}
                if(signed>0f)reverseCount++;
                Log.i("ZERO497_MOTOR",String.format(Locale.US,
                    "heading=%d shifts=%d decisions=8 "+
                    "signedFinalV=%.7f signedTravel=%.7f "+
                    "actualV=(%.7f,%.7f) muscleImpulse=%.4f",
                    heading,shift,signed,observed.directionTravel(heading),
                    observed.vx,observed.vy,observed.impulse));
                total++;
            }
            Outcome extendedBest=replay(user,heading,best,12);
            Log.i("ZERO497_MOTOR",String.format(Locale.US,
                "BEST_IN_SAMPLE heading=%d n=8 originalSignedFinalV=%.7f "+
                "bestSectorShift=%d bestSignedFinalV=%.7f "+
                "worstSectorShift=%d worstSignedFinalV=%.7f "+
                "shiftsWithActuallyReversedVelocity=%d "+
                "chosenShiftAt12SignedFinalV=%.7f "+
                "chosenShiftAt12SignedTravel=%.7f",
                heading,original.directionSpeed(heading),best,top,
                worst,bottom,reverseCount,
                extendedBest.directionSpeed(heading),
                extendedBest.directionTravel(heading)));
        }
        assertEquals(32,total);
        Log.i("ZERO497_MOTOR",
            "ORIGINAL_USER_4747_MUSCLE_SECTOR_32_WAYS_COMPLETE"+
            " cases="+total+" worldAndBodyUnmodified=true"+
            " physicalSolverReal=true researchOnly=true");
    }
}
