package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.Locale;
import static org.junit.Assert.*;

/** Scientific A/B in the ACTUAL original 170-node living material body,
 * NOT the test's fabricated COM force. Use V364 as currently signed:
 * 75% historical peripheral, 25% V3 adaptive cortex, no training or
 * friction changes. Control starts with SAME EAST acceleration, then
 * EAST/EAST, EAST/WEST, EAST/NORTH. No hidden destination in production.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero376DynamicTurnOriginalGelAndroidTest {
    static final class Result {
        final String name;
        float preVx,preVy,afterEarlyVx,afterEarlyVy;
        float eastWarmup,secondX,secondY,afterShortX,afterShortY;
        float totalWork,netMuscle;
        Result(String n){name=n;}
        void show(){
            String msg=String.format(Locale.US,
                "ZERO376_TRUE_PHYSICAL_TURN mode=%s warmEast=%.5f velBefore=(%.5f,%.5f) post0p4s=(%.5f,%.5f) turnAfter1s=(%.5f,%.5f) turnAfter10s=(%.5f,%.5f) postVel=(%.5f,%.5f) muscle=%.5f work=%.5f",
                name,eastWarmup,preVx,preVy,afterShortX,afterShortY,
                secondX,secondY,afterEarlyVx,afterEarlyVy,netMuscle,totalWork);
            System.out.println(msg);
            android.util.Log.i("ZERO376_REAL_GEL",msg);
        }
    }
    static Result trial(String name,int headingIndex,int warmup,int second){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(b,skin);gel.enableOriginalZero3TractionV363();
        SymbioteMotorHeadV1 peripheral=new SymbioteMotorHeadV1();
        Zero3LearningFoundation.AdaptiveCortex cortex=new Zero3LearningFoundation.AdaptiveCortex();
        Zero3LearningFoundation.Coordinator motor=new Zero3LearningFoundation.Coordinator(cortex);
        Result result=new Result(name);
        ArrayList<UnknownWorld.Entity> noExternalWorld=new ArrayList<>();
        final float dt=1f/60f;
        int cursor=0;
        for(int phase=0;phase<2;phase++){
            int n=phase==0?warmup:second;
            double angle=phase==0?0:headingIndex*Math.PI/4;
            float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
            for(int dec=0;dec<n;dec++){
                Zero3LearningFoundation.MotorIntent intent=
                    new Zero3LearningFoundation.MotorIntent(
                        1,cursor++,cortex.revision(),dx,dy,.5f,.5f,24f/60f);
                motor.begin(intent,b,false,true,true,true);
                Zero3MotorPeripheralBridgeV364.begin(peripheral,b,skin,
                    null,noExternalWorld,intent.x,intent.y);
                for(int frame=0;frame<24;frame++){
                    float px=b.cx,py=b.cy,oldWork=gel.work;
                    float[] commands=motor.actuate(b,skin,dt);
                    Zero3MotorPeripheralBridgeV364.fastPhysicalRefine(commands,
                        peripheral,b,skin,dt);
                    gel.step(b,skin,commands,dt);
                    float vx=b.cx-px,vy=b.cy-py;
                    if(phase==0)result.eastWarmup+=vx;
                    else {
                        result.secondX+=vx;result.secondY+=vy;
                        if(dec==0){result.afterShortX+=vx;result.afterShortY+=vy;}
                        if(dec==1&&frame==0){result.afterEarlyVx=b.cvx;result.afterEarlyVy=b.cvy;}
                    }
                    result.netMuscle+=gel.lastMuscleReactionImpulse;
                    result.totalWork+=Math.max(0f,gel.work-oldWork);
                    motor.observe(b,dt,Math.max(0f,gel.work-oldWork),
                        gel.lastMuscleReactionImpulse,false,false);
                }
                motor.finish();
            }
            if(phase==0){result.preVx=b.cvx;result.preVy=b.cvy;}
        }
        assertTrue("actual material exerted physical muscle impulses",result.netMuscle>0);
        assertEquals("controlled frozen test must not train V3 cortex",0,cortex.updates());
        result.show();return result;
    }
    @Test(timeout=500000)
    public void actualLivingBodyCanBrakeAndTurnAfterOppositeChosenHeading(){
        // Each independent world starts truly identical, with zero learned
        // weight updates, and warms up in the same heading for real time.
        Result continuous=trial("east_then_east",0,45,28);
        Result reversal=trial("east_then_west",4,45,28);
        Result perpendicular=trial("east_then_north",2,45,28);
        assertTrue("must have genuinely accelerated from rest, not a stationary fixture",
            continuous.eastWarmup>3f);
        // Comparisons against matched CONTINUED EAST exclude a deceptive
        // conclusion from passive coasting in an otherwise force-free body.
        // Loose 2U threshold avoids misleading CI from transient fluid inertia.
        assertTrue("reversal motor command failed to change material displacement: "+
            reversal.secondX+" versus "+continuous.secondX,
            reversal.secondX<continuous.secondX-2f);
        assertTrue("north turn failed to redirect material transverse motion: "+
            perpendicular.secondY+" versus "+continuous.secondY,
            perpendicular.secondY>continuous.secondY+2f);
        // A further factual threshold checks full braking rather than only
        // 'less coasting in the original direction'. Record and investigate
        // failure, DO NOT weaken it just to publish a new OTA.
        assertTrue("ZERO kept gliding east for 11 seconds despite WEST command: "+
            reversal.secondX,reversal.secondX<0f);
    }
}
