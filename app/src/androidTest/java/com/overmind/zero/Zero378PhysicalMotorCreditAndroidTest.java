package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.Locale;
import static org.junit.Assert.*;

/** V378: the ORIGINAL 170-node living gel, 75% frozen V2.2 effector,
 * 25% trainable V3 cortex, real momentum and real work.
 * No world target, red/blue identity, COM force or simulated motor result.
 * Only the consequence score changes; every physical step is identical.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero378PhysicalMotorCreditAndroidTest {
    static final class Sample {
        float initialVX,initialVY,dx,dy,score,work,impulse;
        int frames;
    }
    private Sample measure(int turnSector,boolean corrected) {
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(b,skin);gel.enableOriginalZero3TractionV363();
        SymbioteMotorHeadV1 peripheral=new SymbioteMotorHeadV1();
        Zero3LearningFoundation.AdaptiveCortex cortex=new Zero3LearningFoundation.AdaptiveCortex();
        Zero3LearningFoundation.Coordinator coordinator=
            new Zero3LearningFoundation.Coordinator(cortex);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        Sample data=new Sample();
        final float dt=1f/60f;
        int decision=0;
        // Same physics verified in ZERO376: 45 decisions EAST, then one
        // 0.4-second EAST/WEST/NORTH turn at the measured speed.
        for(int stage=0;stage<2;stage++){
            int decisions=stage==0?45:1;
            int sector=stage==0?0:turnSector;
            float dx=(float)Math.cos(sector*Math.PI/4d);
            float dy=(float)Math.sin(sector*Math.PI/4d);
            for(int i=0;i<decisions;i++){
                Zero3LearningFoundation.MotorIntent intent=
                    new Zero3LearningFoundation.MotorIntent(
                        1,decision++,cortex.revision(),dx,dy,.5f,.5f,24f/60f);
                if(stage==1){data.initialVX=b.cvx;data.initialVY=b.cvy;}
                coordinator.begin(intent,b,false,true,true,true,corrected);
                Zero3MotorPeripheralBridgeV364.begin(peripheral,b,skin,null,
                    objects,dx,dy);
                for(int frame=0;frame<24;frame++){
                    float priorWork=gel.work;
                    float prevX=b.cx,prevY=b.cy;
                    float[] commands=coordinator.actuate(b,skin,dt);
                    Zero3MotorPeripheralBridgeV364.fastPhysicalRefine(commands,peripheral,b,skin,dt);
                    gel.step(b,skin,commands,dt);
                    float work=Math.max(0f,gel.work-priorWork);
                    coordinator.observe(b,dt,work,gel.lastMuscleReactionImpulse,false,false);
                    if(stage==1){
                        data.dx+=b.cx-prevX;data.dy+=b.cy-prevY;
                        data.work+=work;data.impulse+=gel.lastMuscleReactionImpulse;
                        data.frames++;
                    }
                }
                Zero3LearningFoundation.Experience result=coordinator.finish();
                if(stage==1){
                    assertTrue(result.physicalEvidence);
                    assertFalse(result.externalCorrection);
                    assertFalse(result.censored);
                    data.score=result.score;
                }
            }
        }
        assertEquals(0L,cortex.updates()); // frozen QA, no artificial learning
        assertEquals(24,data.frames);
        System.out.println(String.format(Locale.US,
            "ZERO378_REAL_GEL dir=%d corrected=%b PRE=(%.6f,%.6f) displacement=(%.6f,%.6f) credit=%.6f work=%.6f impulse=%.6f",
            turnSector,corrected,data.initialVX,data.initialVY,data.dx,data.dy,
            data.score,data.work,data.impulse));
        return data;
    }
    @Test(timeout=500000)
    public void brakingAndTurningReceiveCreditForChangingRealPhysicalMomentum(){
        Sample oldBrake=measure(4,false);
        Sample newBrake=measure(4,true);
        Sample newStraight=measure(0,true);
        Sample newNorth=measure(2,true);
        // A training-only score change must NOT apply a fake force or edit physics.
        assertEquals(oldBrake.dx,newBrake.dx,0.00005f);
        assertEquals(oldBrake.dy,newBrake.dy,0.00005f);
        assertEquals(oldBrake.initialVX,newBrake.initialVX,0.00005f);
        assertEquals(oldBrake.initialVY,newBrake.initialVY,0.00005f);
        // Still coasting east when the material starts actively braking west.
        assertTrue(newBrake.initialVX>2f);
        assertTrue(newBrake.dx>0f);
        assertTrue(newBrake.dx<newStraight.dx);
        // Old score wrongly punishes the scientifically observed brake.
        assertTrue("historical wrong negative brake credit",oldBrake.score<0f);
        assertTrue("new score must credit actual braking",newBrake.score>0f);
        assertTrue("brake earns more correction credit than coasting",
            newBrake.score>newStraight.score+.02f);
        // The true NORTH turn must change velocity even while initially
        // continuing south. No authored movement is permitted.
        assertTrue(newNorth.dy>newStraight.dy);
        assertTrue("transverse physical momentum change learns",newNorth.score>0f);
    }
}
