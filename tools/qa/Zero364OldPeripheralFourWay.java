package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * V364 motor transplant RESEARCH ONLY. Compare the old proven V2.2
 * peripheral under four test CHOSEN headings with V363 original gel.
 * Does NOT start a second decision-maker, add behavior to the APK, or
 * imply the parent V3 brain has learned any red/blue navigation.
 */
public final class Zero364OldPeripheralFourWay {
    private static final float DT=1f/60f;
    private static final int DECISIONS=240, FRAMES=24;
    public static void main(String[] args){
        for(int direction=0;direction<4;direction++){
            MotorBrain.Body body=new MotorBrain.Body();
            Morphology.Surface skin=new Morphology.Surface(body);
            SymbioteMatterV1 gel=new SymbioteMatterV1();
            gel.reset(body,skin);
            gel.enableOriginalZero3TractionV363();
            SymbioteMotorHeadV1 oldMotor=new SymbioteMotorHeadV1();
            float[] neutralCore=new float[RecurrentCoreV22.Z];
            ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
            double gross=0,work=0,impulse=0,netX=0,netY=0;
            for(int d=0;d<DECISIONS;d++){
                float[] senses=DistributedBodySense.sense(body,skin,null,empty);
                // Each *test* choice is a direction; all ensuing commands
                // are those naturally produced by the legacy cortex.
                // No rotation of the body and no unphysical impulses.
                int action=direction*2;
                oldMotor.chosen(action,senses,neutralCore,0f,null,
                    SymbioteMotorHeadV1.measuredSectorAngles(body,skin),body,skin);
                for(int f=0;f<FRAMES;f++){
                    oldMotor.fastTick(body,skin,DT);
                    float x=body.cx,y=body.cy,before=gel.work;
                    gel.step(body,skin,oldMotor.commands(),DT);
                    double dx=body.cx-x,dy=body.cy-y;
                    gross+=Math.hypot(dx,dy);
                    netX+=dx;netY+=dy;
                    work+=Math.max(0,gel.work-before);
                    impulse+=gel.lastMuscleReactionImpulse;
                }
            }
            double forward=direction==0?netX:direction==1?netY:
                          direction==2?-netX:-netY;
            System.out.printf(Locale.ROOT,
                "V364_OLD_PERIPHERAL_DIR%d forward=%+.6f gross=%.6f work=%.8f impulse=%.4f netX=%+.6f netY=%+.6f%n",
                direction,forward,gross,work,impulse,netX,netY);
        }
    }
}
