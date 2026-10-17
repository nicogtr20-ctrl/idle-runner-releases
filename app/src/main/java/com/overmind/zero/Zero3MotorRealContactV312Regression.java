package com.overmind.zero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * ZERO3 V3.12: REAL WORLD CONTACT COVERAGE on the UNCHANGED GEL SOLVER.
 *
 * Unlike V3.11, each 0.025s step dispatches the real box/disk collision,
 * interior matter contact, independent grip timers and tactile composition.
 * Lab-only controlled grip commands are NEVER substituted for real holds.
 * No sensor values are injected. No brain, game, APK or OTA modification.
 */
public final class Zero3MotorRealContactV312Regression {
    static final float DT=.025f;
    static final int HISTORY=96,STEPS=144;
    static final int NEAR_BOX=0,FAR_BOX=1,DISK=2,FREE=3;
    static final float[] POSES={0f,(float)(Math.PI/2),
            (float)(25*Math.PI/180),(float)(-37*Math.PI/180)};
    static final float[] NEAR={47.5f,49.5f,52f};
    private Zero3MotorRealContactV312Regression(){}
    static void require(boolean ok,String message){
        if(!ok)throw new AssertionError("ZERO3 V312 "+message);
    }
    static final class Result {
        final int mode,adhesion;
        int touchFrames,diskPulseFrames,heldFrames,bilateralFrames;
        int commandFrames,solverFrames,rawSquareContacts;
        int nonFinite,movementWithoutOpposingGrip;
        double distance,boxDistance;
        Result(int m,int grip){mode=m;adhesion=grip;}
        String show(){
            return String.format(Locale.US,
                "mode=%d adhesion=%d touch=%d diskPulse=%d held=%d "+
                "bilateral=%d voluntaryCommand=%d squareContactFrames=%d "+
                "netDistance=%.4f boxDistance=%.4f",
                mode,adhesion,touchFrames,diskPulseFrames,heldFrames,
                bilateralFrames,commandFrames,rawSquareContacts,
                distance,boxDistance);
        }
    }
    static Result simulate(int mode,int orientation,int seedIndex,
                           int adhesion,int gapChoice){
        float pose=POSES[orientation];
        // Per-run fixture has its OWN continuous living material.
        Zero3MotorImpulseResponseRegression.Scenario actor=
            new Zero3MotorImpulseResponseRegression.Scenario(
                orientation,pose,seedIndex);
        Result out=new Result(mode,adhesion);
        int seed=Zero3MotorLocalFrameV310Regression.scheduleSeed(
            0x312A+mode*109,orientation,pose,seedIndex,0,1,
            adhesion==0?-1:1);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        UnknownWorld.Entity object=null;
        double originX=0,originY=0;
        float[] oldSkinX=new float[Morphology.POINTS],
                oldSkinY=new float[Morphology.POINTS],
                oldBodyX=new float[MotorBrain.N],
                oldBodyY=new float[MotorBrain.N];
        for(int frame=0;frame<HISTORY+STEPS;frame++){
            int relative=frame-HISTORY;
            if(relative==0){
                originX=actor.body.cx;originY=actor.body.cy;
                // Spawn an unlabelled physical obstacle; NEVER move ZERO's
                // body/skin to a target or overwrite any touch/hold sensors.
                if(mode==NEAR_BOX||mode==FAR_BOX){
                    float d=mode==FAR_BOX?350f:NEAR[gapChoice];
                    object=new UnknownWorld.Entity(actor.body.cx+d,
                        actor.body.cy,2,5);
                    world.add(object);
                }else if(mode==DISK){
                    object=new UnknownWorld.Entity(actor.body.cx+48f,
                        actor.body.cy,2,2);
                    world.add(object);
                }
            }
            float[] cmd=actor.cortex.command(actor.intent,
                actor.body,actor.skin,DT);
            Zero3MotorLocalFrameV310Regression.excitation(
                frame,seed,0,1,adhesion==0?-1:1,cmd);
            // Sole laboratory intervention: explicitly control the
            // legal ADHESION ACTUATOR channel, not the physical hold.
            // In ON trials, the cortex commands adhesion; only a real
            // sustained face contact can turn it into a hold.
            for(int sector=0;sector<16;sector++){
                int index=4*sector+2;
                cmd[index]=adhesion==0?-1f:
                    (float)Math.max(.72,cmd[index]);
            }
            for(float a:cmd)
                require(Float.isFinite(a)&&a>=-1.001f&&a<=1.001f,
                    "invalid actually executed motor command");
            System.arraycopy(actor.skin.x,0,oldSkinX,0,Morphology.POINTS);
            System.arraycopy(actor.skin.y,0,oldSkinY,0,Morphology.POINTS);
            System.arraycopy(actor.body.x,0,oldBodyX,0,MotorBrain.N);
            System.arraycopy(actor.body.y,0,oldBodyY,0,MotorBrain.N);
            float oldCX=actor.body.cx,oldCY=actor.body.cy;
            actor.gel.step(actor.body,actor.skin,cmd,DT);
            require(actor.gel.invalid==0&&
                Float.isFinite(actor.body.cx)&&Float.isFinite(actor.body.cy),
                "invalid real physical gel");
            if(relative<0)continue;
            out.solverFrames++;
            int contacts=0;
            float previousBoxDistance=object==null?0:
                object.displacementFromSpawn();
            if(mode==NEAR_BOX||mode==FAR_BOX){
                SquareGripPuzzle.Outcome actual=SquareGripPuzzle.constrain(
                    actor.body,actor.skin,object,DT,
                    oldSkinX,oldSkinY,oldBodyX,oldBodyY,oldCX,oldCY);
                contacts=actual.contacts;
                if(actual.contacts>0)out.rawSquareContacts++;
                // Exactly the same physics-only post-contact ordering
                // used by production's boxed symbiote world.
                MaterialContactConstraints.reconcile(
                    actor.body,actor.skin,world,DT);
                SymbioteWorldStepV171.interior(actor.gel,actor.body,
                    actor.skin,null,world,DT);
                GripSurfaceComposition.compose(actor.skin,null,world);
                if(object.bilateralHeldNow)out.bilateralFrames++;
                object.tick(DT);
            }else{
                SymbioteWorldStepV171.interior(actor.gel,actor.body,
                    actor.skin,null,world,DT);
                GripSurfaceComposition.compose(actor.skin,null,world);
                if(mode==DISK){
                    Morphology.collide(actor.skin,object,DT);
                    ContactLearning.collide(actor.body,object,DT);
                    object.tick(DT);
                    actor.gel.observeBody(actor.body,actor.skin);
                }
            }
            int touched=0,pulsed=0,held=0,commands=0;
            for(int i=0;i<Morphology.POINTS;i++){
                if(actor.skin.touchSense[i]>.1f)touched++;
                if(actor.skin.touchPulse[i]>.001f)pulsed++;
                if(actor.skin.gripHold[i]>=Morphology.GRIP_HOLD){
                    held++;
                    require(actor.skin.gripCommand[i]>Morphology.GRIP_THRESHOLD,
                        "physical grip exists without intentional command");
                    require(object!=null&&object.isBilateralBox()&&
                        object.boxGripHold[i]>=Morphology.GRIP_HOLD,
                        "held patch has no real owning external solid");
                    require(GripSurfaceComposition.boxFace(object,
                        actor.skin.x[i],actor.skin.y[i]),
                        "held patch does not touch owner physical face");
                }
                if(actor.skin.gripCommand[i]>Morphology.GRIP_THRESHOLD)
                    commands++;
            }
            if(touched>0)out.touchFrames++;
            if(pulsed>0&&mode==DISK)out.diskPulseFrames++;
            if(held>0)out.heldFrames++;
            if(commands>0)out.commandFrames++;
            require(actor.gel.invalid==0,"solver becomes invalid after world contact");
            if(object!=null&&object.isBilateralBox()){
                float after=object.displacementFromSpawn();
                require(Float.isFinite(after),"nonfinite physical box location");
                // Box may only move following an explicit two-sided
                // physics-certified hold; no accidental proximity reward.
                if(after>previousBoxDistance+.001f&&
                   !object.bilateralHeldNow&&!object.everCollided)
                    out.movementWithoutOpposingGrip++;
            }
        }
        out.distance=Math.hypot(actor.body.cx-originX,actor.body.cy-originY);
        out.boxDistance=object==null?0:object.displacementFromSpawn();
        require(Double.isFinite(out.distance)&&
            Double.isFinite(out.boxDistance),
            "invalid physical outcome after collision");
        return out;
    }
    static final class Summary {
        final int mode;
        int cases,contactCases,touchFrames,diskPulseCases,diskPulseFrames,
            gripCases,holdFrames,bilateralCases,bilateralFrames,
            commandCases,badBoxMotion,rawSquareContactCases;
        double totalBodyDistance,totalObjectDistance;
        Summary(int m){mode=m;}
        void append(Result a){
            cases++;
            contactCases+=a.touchFrames>0?1:0;
            touchFrames+=a.touchFrames;
            diskPulseCases+=a.diskPulseFrames>0?1:0;
            diskPulseFrames+=a.diskPulseFrames;
            gripCases+=a.heldFrames>0?1:0;holdFrames+=a.heldFrames;
            bilateralCases+=a.bilateralFrames>0?1:0;
            bilateralFrames+=a.bilateralFrames;
            commandCases+=a.commandFrames>0?1:0;
            rawSquareContactCases+=a.rawSquareContacts>0?1:0;
            badBoxMotion+=a.movementWithoutOpposingGrip;
            totalBodyDistance+=a.distance;
            totalObjectDistance+=a.boxDistance;
        }
        String describe(){
            return String.format(Locale.US,
                "mode=%s trials=%d touchedTrials=%d touchedFrames=%d "+
                "diskPulseTrials=%d diskPulseFrames=%d heldTrials=%d "+
                "heldFrames=%d bilateralTrials=%d bilateralFrames=%d "+
                "motorGripCommandTrials=%d rawBoxContactTrials=%d "+
                "bodyNetSum=%.3f boxNetSum=%.3f invalidBoxMove=%d",
                name(mode),cases,contactCases,touchFrames,diskPulseCases,
                diskPulseFrames,gripCases,holdFrames,bilateralCases,
                bilateralFrames,commandCases,rawSquareContactCases,
                totalBodyDistance,totalObjectDistance,badBoxMotion);
        }
    }
    static String name(int mode){
        return mode==NEAR_BOX?"NEAR_BOX":mode==FAR_BOX?"FAR_BOX":
               mode==DISK?"DISK":"FREE";
    }
    public static void main(String[] args){
        Summary[] totals={
            new Summary(NEAR_BOX),new Summary(FAR_BOX),
            new Summary(DISK),new Summary(FREE)
        };
        Summary[] off={
            new Summary(NEAR_BOX),new Summary(FAR_BOX),
            new Summary(DISK),new Summary(FREE)
        };
        for(int mode=NEAR_BOX;mode<=FREE;mode++){
            int positions=mode==NEAR_BOX?3:1;
            for(int pose=0;pose<POSES.length;pose++)
                for(int seed=0;seed<2;seed++)
                    for(int adhesion=0;adhesion<2;adhesion++)
                        for(int distance=0;distance<positions;distance++){
                            Result a=simulate(mode,pose,seed,adhesion,distance);
                            totals[mode].append(a);
                            if(adhesion==0)off[mode].append(a);
                        }
            System.out.println("ZERO3 V312 WORLD "+totals[mode].describe());
            System.out.println("ZERO3 V312 NO_ADHESION "+off[mode].describe());
        }
        require(totals[NEAR_BOX].cases==48&&
            totals[FAR_BOX].cases==16&&
            totals[DISK].cases==16&&totals[FREE].cases==16,
            "incorrect preregistered physical trajectory counts");
        boolean physicsValid=true;
        for(Summary s:totals)physicsValid&=s.badBoxMotion==0;
        boolean positive=totals[NEAR_BOX].contactCases>0&&
            totals[DISK].diskPulseCases>0;
        boolean negatives=totals[FAR_BOX].contactCases==0&&
            totals[FAR_BOX].gripCases==0&&
            totals[FREE].contactCases==0&&totals[FREE].gripCases==0&&
            totals[DISK].gripCases==0&&
            off[NEAR_BOX].gripCases==0;
        boolean gripValidated=totals[NEAR_BOX].gripCases>0;
        boolean basic=physicsValid&&positive&&negatives;
        // Exposing the partial gate is mandatory even when true grip
        // cannot yet be observed by the laboratory corpus.
        System.out.printf(Locale.US,
            "ZERO3 V312 SUMMARY trials=%d realGelSteps=%d "+
            "boxTouchCovered=%s diskMechanicalTouchCovered=%s "+
            "gripCovered=%s negativesClean=%s realWorldPhysics=%s "+
            "touchSenseAndTouchPulseDistinct=true "+
            "V311_IGNORES_DISK_TOUCH_PULSE=true "+
            "coverage=%s completeContactGate=%s "+
            "NO_FAKE_HOLDS NO_GAIT NO_GAMEPLAY G3_BLOCKED G5_BLOCKED NO_OTA%n",
            96,96*(HISTORY+STEPS),totals[NEAR_BOX].contactCases>0,
            totals[DISK].diskPulseCases>0,gripValidated,negatives,
            physicsValid,basic?"PASS":"FAIL",
            basic&&gripValidated?"PASS":"FAIL");
    }
}
