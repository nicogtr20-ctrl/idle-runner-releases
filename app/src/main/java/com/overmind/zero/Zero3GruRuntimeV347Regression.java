package com.overmind.zero;

import java.util.Locale;
import java.util.ArrayList;

/** Actual V325 owner research with GRU64 as ONE intention source. */
public final class Zero3GruRuntimeV347Regression {
    private Zero3GruRuntimeV347Regression(){}
    private static void check(boolean state,String reason){
        if(!state)throw new AssertionError("V347 "+reason);
    }
    /** Four mechanically distinct births; energy always comes from genuine
     * physical contact. Scenario 3 has real blue food touching the membrane,
     * scenario 2 true harmful red, never a granted actor reward. */
    private static Zero3RuntimeV325 born(int scene,long episode,long seed)
            throws Exception{
        Zero3MotorImpulseResponseRegression.Scenario pose=
            new Zero3MotorImpulseResponseRegression.Scenario(
                scene,(float)(-.65+scene*.34),scene);
        float[] zero=new float[64];
        for(int i=0;i<96;i++)
            pose.gel.step(pose.body,pose.skin,zero,.025f);
        check(pose.gel.invalid==0,"prebirth warmup corrupted material");
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        objects.add(new UnknownWorld.Entity(
            pose.body.cx+(scene%2==0?47f:-47f),
            pose.body.cy+(scene-2)*5f,2,5));
        if(scene>=1)objects.add(new UnknownWorld.Entity(
            pose.body.cx-61f,pose.body.cy+30f,2,2));
        if(scene==2)
            objects.add(new UnknownWorld.Entity(
                pose.skin.x[32],pose.skin.y[32],1,1));
        if(scene==3)
            objects.add(new UnknownWorld.Entity(
                pose.skin.x[24],pose.skin.y[24],0,0));
        ScarcityLab room=new ScarcityLab(pose.body.cx+150f,
                                         pose.body.cy+scene*4f);
        objects.add(room.food);
        return Zero3RuntimeV325.newborn(
            episode,seed,pose.body,pose.skin,pose.gel,
            objects,room,scene==3?50f:100f,0f);
    }
    public static void main(String[] args)throws Exception{
        int completed=0,full=0,faults=0;
        long allGru=0,allMotor=0,allPhysics=0;
        double actualGained=0,realEnergyCost=0;
        for(int scenario=0;scenario<4;scenario++){
            long episode=347001L+scenario,worldSeed=0x3470ABCDEF0L+scenario*1171L;
            int n=0;String reason="NONE";
            try(Zero3RuntimeV325 organism=
                    born(scenario,episode,worldSeed)){
                long bornTicks=organism.gelTicks();
                organism.enableGruResearchV347();
                check(organism.display().neurons==Zero3RecurrentBrain.HIDDEN,
                    "GRU64 neuron count is actually shown");
                check(organism.gruLearningUpdatesV347()==0,
                    "GRU learned before real world decisions");
                for(int decision=0;decision<128;decision++){
                    if(organism.ended()){reason="REAL_METABOLIC_TERMINAL";break;}
                    try{organism.advanceOneDecision();}
                    catch(IllegalStateException error){
                        reason="CENSORED_PHYSICAL_OR_CAUSAL_"+
                               error.getMessage().replace(' ','_');
                        break;
                    }
                    n++;
                    Zero3RuntimeV325.Display real=organism.display();
                    check(real.decisions==n&&
                          organism.gruLearningUpdatesV347()==n&&
                          organism.encoderUpdates()==0&&
                          real.hasVitalOutcome&&
                          real.lastEnergySources>=0&&
                          real.neurons==64&&
                          Float.isFinite(real.lastEnergyNet),
                          "real single-owner GRU/physiology counter drift");
                    check(Math.abs(real.lastEnergyNet-
                            (real.lastEnergyGained-real.lastEnergyBasal-
                             real.lastEnergyMotor-real.lastEnergyDamage))<.002f,
                          "unearned energy reward reached actual GRU");
                    actualGained+=real.lastEnergyGained;
                    realEnergyCost+=real.lastEnergyBasal+
                                    real.lastEnergyMotor+
                                    real.lastEnergyDamage;
                    if(n==1){
                        boolean rejected=false;
                        try{organism.snapshot();}
                        catch(IllegalStateException expected){rejected=true;}
                        check(rejected,"unpersisted GRU overwritten onto V317 save");
                    }
                }
                completed+=n;
                allGru+=organism.gruLearningUpdatesV347();
                allMotor+=organism.cortexUpdates();
                allPhysics+=organism.gelTicks()-bornTicks;
                if(n==128)full++;else faults++;
                System.out.printf(Locale.US,
                     "V347 REAL_ACTOR scene=%d completed=%d/128 "+
                     "gruLearned=%d cortexLearned=%d v317ActorUpdates=%d "+
                     "trueWorldSubsteps=%d reserve=%.6f reason=%s "+
                     "singleMotorOwner=GRU_64 noFakeFuelOrGrip=true "+
                     "fixed12stepDurationLimit=true UNPERSISTABLE=true%n",
                     scenario,n,organism.gruLearningUpdatesV347(),
                     organism.cortexUpdates(),organism.encoderUpdates(),
                     organism.gelTicks()-bornTicks,organism.energy(),reason);
            }
        }
        check(actualGained>0d,
              "none of the actual V323 blue physical contact outcomes reached GRU");
        check(completed==allGru&&allPhysics==12L*completed,
              "GRU decisions not coupled to exactly 12 true physical substeps");
        System.out.printf(Locale.US,
            "V347 G4_LIVE_GRU_RESULT fullLives=%d/4 totalDecisions=%d "+
            "gruUpdates=%d realMotorUpdates=%d physicalSteps=%d "+
            "actualEnergyGained=%.8f realEnergySpent=%.8f "+
            "censored=%d NO_BLIND_VALUE_PROOF "+
            "G4_AND_G7_NOT_PROMOTED NO_OTA NO_MERGE%n",
            full,completed,allGru,allMotor,allPhysics,
            actualGained,realEnergyCost,faults);
    }
}
