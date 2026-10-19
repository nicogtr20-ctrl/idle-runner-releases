package com.overmind.zero;
import java.util.ArrayList;
import java.util.Locale;

/**
 * V364 LAB ONLY: Continuously chosen V3 GRU direction -> legacy proven
 * LOW-LEVEL BODY PERIPHERAL, never the obsolete V2.2 brain/arbiter.
 *
 * Explicitly excludes V2.2 energy/value/action chooser. The experiment
 * uses fixed cardinal directions solely to measure genuine actuator
 * bidirectional authority, no scripted live gait and no cheat target.
 *
 * Compare α=0 (V3 cortex only), α=.75 (hybrid), α=1 (old peripheral).
 * CH2 object grip remains V319's sole property in production: here no
 * world contacts, so its full command remains the V3 cortex channel.
 */
public final class Zero364MotorBlendAudit {
    static final float DT=1f/60f;
    static final int CHOICES=240,STEPS=24;
    private static void trial(int direction,float legacyShare){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(body,skin);
        gel.enableOriginalZero3TractionV363();
        SymbioteMotorHeadV1 peripheral=new SymbioteMotorHeadV1();
        Zero3LearningFoundation.AdaptiveCortex cortex=new Zero3LearningFoundation.AdaptiveCortex();
        Zero3LearningFoundation.Coordinator motor=new Zero3LearningFoundation.Coordinator(cortex);
        float[] zeroCore=new float[RecurrentCoreV22.Z];
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        double netX=0,netY=0,gross=0,work=0,impulse=0;
        for(int k=0;k<CHOICES;k++){
            float x=direction==0?1f:direction==2?-1f:0f;
            float y=direction==1?1f:direction==3?-1f:0f;
            Zero3LearningFoundation.MotorIntent chosen=
                new Zero3LearningFoundation.MotorIntent(1,k,cortex.revision(),x,y,.5f,.5f,STEPS*DT);
            motor.begin(chosen,body,false,true,true,true);
            peripheral.chosen(direction*2,
                DistributedBodySense.sense(body,skin,null,objects),zeroCore,0f,null,
                SymbioteMotorHeadV1.measuredSectorAngles(body,skin),body,skin);
            for(int f=0;f<STEPS;f++){
                float[] modern=motor.actuate(body,skin,DT);
                peripheral.fastTick(body,skin,DT);
                float[] older=peripheral.commands();
                float[] executed=modern.clone();
                for(int sector=0;sector<16;sector++){
                    // ONLY the neural motor's three physical muscle/tension
                    // channels; grip CH2 intentionally stays exclusively V3.
                    for(int channel:new int[]{0,1,3}){
                        int i=sector*4+channel;
                        executed[i]=Math.max(-1,Math.min(1,
                            (1f-legacyShare)*modern[i]+legacyShare*older[i]));
                    }
                }
                float x0=body.cx,y0=body.cy,prev=gel.work;
                gel.step(body,skin,executed,DT);
                double dx=body.cx-x0,dy=body.cy-y0;
                netX+=dx;netY+=dy;gross+=Math.hypot(dx,dy);
                work+=Math.max(0,gel.work-prev);impulse+=gel.lastMuscleReactionImpulse;
                motor.observe(body,DT,Math.max(0,gel.work-prev),gel.lastMuscleReactionImpulse,false,false);
            }
            motor.finish();
        }
        double along=direction==0?netX:direction==1?netY:
                     direction==2?-netX:-netY;
        System.out.printf(Locale.ROOT,
          "V364_BLEND_DIR%d oldShare=%.2f signed=%.5f gross=%.5f work=%.8f impulse=%.4f netX=%+.5f netY=%+.5f%n",
          direction,legacyShare,along,gross,work,impulse,netX,netY);
    }
    public static void main(String[] args){
        for(int dir=0;dir<4;dir++){
            trial(dir,0f);trial(dir,.5f);trial(dir,.75f);trial(dir,1f);
        }
    }
}
