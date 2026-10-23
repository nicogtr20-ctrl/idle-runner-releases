package com.overmind.zero;

import android.os.Bundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.Locale;
import static org.junit.Assert.*;

/** Real 170-node V381 motor, changes only measured temporary surface drag.
 * Candidate is lab-only, and is NOT enabled in GameActivity unless A/B
 * proves reliable improvement across actual saved cortical checkpoints. */
@RunWith(AndroidJUnit4.class)
public final class Zero382BrakePhysicalABAndroidTest {
    static final float DT=1f/60f;
    static final class Trial {
        float firstX,firstY,fullX,fullY,warmX,work,endVx,endVy;
    }
    static Trial run(float gain,int terminalDirection){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(b,s);gel.enableOriginalZero3TractionV363();
        gel.configurePhysicalTurnBrakeV382(gain);
        SymbioteMotorHeadV1 old=new SymbioteMotorHeadV1();
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex();
        Zero3LearningFoundation.Coordinator coordinator=
            new Zero3LearningFoundation.Coordinator(cortex);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        Trial trial=new Trial();
        for(int dec=0;dec<20;dec++){
            int bearing=dec<12?0:terminalDirection;
            double theta=bearing*Math.PI/4;
            float gx=(float)Math.cos(theta),gy=(float)Math.sin(theta);
            Zero3LearningFoundation.MotorIntent choice=
                new Zero3LearningFoundation.MotorIntent(1L,dec,
                    cortex.revision(),gx,gy,.5f,.5f,24*DT);
            coordinator.begin(choice,b,false,true,true,true,true);
            Zero3MotorPeripheralBridgeV364.begin(old,b,s,null,world,gx,gy);
            gel.observedOriginalMotorChangeV382(gx,gy);
            float totalX=0,totalY=0;
            for(int frame=0;frame<24;frame++){
                float oldX=b.cx,oldY=b.cy,preWork=gel.work;
                float[] cmds=coordinator.actuate(b,s,DT);
                Zero3MotorPeripheralBridgeV364.fastPhysicalRefine(cmds,old,b,s,DT);
                gel.step(b,s,cmds,DT);
                float dx=b.cx-oldX,dy=b.cy-oldY;
                totalX+=dx;totalY+=dy;
                coordinator.observe(b,DT,Math.max(0,gel.work-preWork),
                    gel.lastMuscleReactionImpulse,false,false);
            }
            coordinator.finish();
            if(dec<12)trial.warmX+=totalX;
            else {
                trial.fullX+=totalX;trial.fullY+=totalY;
                if(dec==12){trial.firstX=totalX;trial.firstY=totalY;}
            }
        }
        trial.work=gel.work;trial.endVx=b.cvx;trial.endVy=b.cvy;
        assertEquals("Same original learning disabled in science A/B",
            0,cortex.updates());
        return trial;
    }
    @Test public void observeMatchedOriginalGelBrakingAcrossActualFloorCoefficients(){
        StringBuilder out=new StringBuilder("ZERO382_REAL_MATERIAL_A_B\n");
        for(float gain:new float[]{0f,-.02f,-.04f,-.06f,.02f,.04f,.06f}){
            Trial east=run(gain,0),west=run(gain,4),north=run(gain,2);
            out.append(String.format(Locale.US,
                "gain=%.3f eastWarm=%.4f continueEast=%.4f WESTfirst=%.4f WEST8=%.4f WESTY=%.4f NORTHfirstY=%.4f NORTH8Y=%.4f work=%.7f%n",
                gain,east.warmX,east.fullX,west.firstX,
                west.fullX,west.fullY,north.firstY,north.fullY,west.work));
            assertTrue(west.work>0&&Double.isFinite(west.fullX));
            assertTrue(east.warmX>2);
        }
        Bundle b=new Bundle();b.putString("stream",out.toString());
        InstrumentationRegistry.getInstrumentation().sendStatus(0,b);
        System.out.print(out);
    }
}