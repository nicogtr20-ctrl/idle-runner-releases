package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Original V3 real motor evidence, not an authored escape or gait. */
@RunWith(AndroidJUnit4.class)
public final class Zero390IsolatedMotorCreditAndroidTest {
    private static final float DT=1f/60f;
    static final class Trial {
        final Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        final Zero3LearningFoundation.Coordinator coordinator=
            new Zero3LearningFoundation.Coordinator(cortex);
        final MotorBrain.Body body=new MotorBrain.Body();
        final Morphology.Surface skin=new Morphology.Surface(body);
        Trial(){coordinator.enableCausalMotorCreditV390();}
        Zero3LearningFoundation.MotorIntent begin(long decision){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(1,decision,cortex.revision(),
                    1f,0f,.5f,.4f,.25f);
            coordinator.begin(intent,body,true,false,true,true,true);
            return intent;
        }
        Zero3LearningFoundation.Experience finish(float travelX,int touchSite,
                                                   float preVx){
            body.cvx=preVx;
            begin(0);
            float perFrame=travelX/12f;
            for(int i=0;i<12;i++){
                skin.touchSense[touchSite>=0?touchSite:0]=touchSite>=0?1f:0f;
                coordinator.actuate(body,skin,DT);
                body.cx+=perFrame;
                coordinator.observeContactV390(body,skin,DT,
                    0f,2f,false,false);
            }
            return coordinator.finish();
        }
    }
    private static int site(Trial t,boolean front){
        int selected=0;
        float best=front?Float.NEGATIVE_INFINITY:Float.POSITIVE_INFINITY;
        for(int i=0;i<Morphology.POINTS;i++){
            float dx=t.skin.x[i]-t.body.cx;
            if((front&&dx>best)||(!front&&dx<best)){
                selected=i;best=dx;
            }
        }
        return selected;
    }
    @Test public void eachOriginalCortexExperimentPerturbsExactlyOneRealMuscle(){
        Trial t=new Trial();
        int[] sectors=new int[16];
        int[] channels=new int[4];
        for(int i=0;i<48;i++){
            Zero3LearningFoundation.MotorIntent intent=
                new Zero3LearningFoundation.MotorIntent(1,i,t.cortex.revision(),
                    1f,0f,.5f,.5f,.25f);
            t.coordinator.begin(intent,t.body,true,false,true,true,true);
            assertEquals("Each original V3 physical probe must isolate ONE motor",
                1,t.cortex.activeProbeCountV390());
            sectors[t.cortex.isolatedSectorV390()]++;
            channels[t.cortex.lastActiveProbeChannel()]++;
            t.coordinator.abort();
        }
        for(int count:sectors)assertEquals(
            "All sixteen body sectors must be probed, no hidden directional bias",3,count);
        assertEquals(16,channels[0]);
        assertEquals(16,channels[1]);
        assertEquals("Integrated original V319 alone owns grip",0,channels[2]);
        assertEquals(16,channels[3]);
    }
    @Test public void rearWallOnlyAndRealOutwardMovementMayTeachOneLocalMuscle(){
        Trial t=new Trial();
        int rear=site(t,false);
        long before=t.cortex.updates();
        Zero3LearningFoundation.Experience result=t.finish(1.2f,rear,-2f);
        assertTrue("Actual eastward COM escape is recorded",result.deltaX>1f);
        assertTrue("Exactly one true measured rear contact",t.coordinator.rearContactFramesV390()>0);
        assertEquals(0,t.coordinator.frontContactFramesV390());
        assertEquals("A real retreat can teach an isolated muscle",before+1,t.cortex.updates());
    }
    @Test public void frontWallStoppingWestwardInertiaCannotBeRewardedAsEastThrust(){
        Trial t=new Trial();
        int front=site(t,true);
        long before=t.cortex.updates();
        Zero3LearningFoundation.Experience result=t.finish(-.2f,front,-4f);
        // PRE-velocity baseline falsely scores (-.2)-(-4*.2)=+.6U
        // even though the body actually moved WEST.
        assertTrue(result.deltaX<0);
        assertTrue(t.coordinator.frontContactFramesV390()>0);
        assertEquals("Never update motor weights from wall-arrested inertia",
            before,t.cortex.updates());
    }
    @Test public void rearWallWithNoRealTravelCannotFabricateAnEscape(){
        Trial t=new Trial();
        int rear=site(t,false);
        long before=t.cortex.updates();
        t.finish(0f,rear,-4f);
        assertTrue(t.coordinator.rearContactFramesV390()>0);
        assertEquals("Stalled rear-wall pose cannot train a fictitious thrust",
            before,t.cortex.updates());
    }
}
