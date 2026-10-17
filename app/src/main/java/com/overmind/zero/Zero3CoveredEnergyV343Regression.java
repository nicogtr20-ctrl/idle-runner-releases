package com.overmind.zero;

import java.util.ArrayList;

/** Actual World entities + V323 metabolic sensor, no fake cortical reward. */
public final class Zero3CoveredEnergyV343Regression {
    private Zero3CoveredEnergyV343Regression(){}
    private static void require(boolean ok,String message){
        if(!ok)throw new AssertionError("V343 covered energy "+message);
    }
    private static ArrayList<UnknownWorld.Entity> paired(
            float x,float y){
        ArrayList<UnknownWorld.Entity> list=new ArrayList<>();
        list.add(new UnknownWorld.Entity(x,y,2,5));
        UnknownWorld.Entity source=new UnknownWorld.Entity(x,y,0,4);
        source.concealed=true;
        list.add(source);
        return list;
    }
    private static void concealmentInvariant(){
        ArrayList<UnknownWorld.Entity> world=paired(44f,12f);
        UnknownWorld.Entity box=world.get(0),food=world.get(1);
        Zero3CoveredEnergyV343 bridge=
           Zero3CoveredEnergyV343.laboratory(world);
        require(bridge.coveredSources()==1&&food.concealed,
                "incorrect start");
        require(OpticalCues.coverOf(food,world)==box,
                "one legitimate weak spectral leak expected");
        bridge.afterWorld(world);
        require(bridge.exposedSources()==0&&food.concealed,
                "static box gave free energy");
        // A saved single-sided adhesion timer cannot stand in for an
        // actual two-sided physical hold event: only real bilateralHeldNow
        // flag from SquareGripPuzzle can advance this state machine.
        box.boxGripHold[0]=Morphology.GRIP_HOLD;
        bridge.afterWorld(world);
        require(food.concealed,"single patch grant");
        // Counterfactual transport WITHOUT prior bilateral cannot reveal.
        ArrayList<UnknownWorld.Entity> teleported=paired(0f,0f);
        Zero3CoveredEnergyV343 isolated=
            Zero3CoveredEnergyV343.laboratory(teleported);
        teleported.get(0).x=SquareGripPuzzle.UNCOVER_DISTANCE+3f;
        isolated.afterWorld(teleported);
        require(isolated.exposedSources()==0&&teleported.get(1).concealed,
            "unvalidated position change exposed food");
        // Here only the physical event flag is simulated: this tests the
        // bridge, NOT learned manipulation by an actor.
        box.bilateralHeldNow=true;
        bridge.afterWorld(world);
        require(food.concealed&&bridge.validatedHeldFrames()==1,
                "holding still grants reward");
        box.bilateralHeldNow=false;
        box.x+=SquareGripPuzzle.UNCOVER_DISTANCE-0.2f;
        bridge.afterWorld(world);
        require(food.concealed,"under-distance reward");
        box.x+=.6f;
        bridge.afterWorld(world);
        require(bridge.exposedSources()==1&&!food.concealed&&
                OpticalCues.coverOf(food,world)==null,
                "real uncovered food was not revealed");
        bridge.afterWorld(world);
        require(bridge.exposedSources()==1,
                "duplicate transition repeated reward");
        require(food.ready(),"uncovered finite food not collectable");
        System.out.println("V343 PROOF physicalCover true bilateral before displacement "+
            "release>=UNCOVER_DISTANCE; no free touch/single grip/teleport "+
            "and exactlyOneReveal=PASS");
    }
    private static void actualEnergyContact(){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ArrayList<UnknownWorld.Entity> world=
            paired(skin.x[0],skin.y[0]);
        UnknownWorld.Entity box=world.get(0),food=world.get(1);
        require(MaterialGeometry.touches(body,skin,
                food.x,food.y,food.radius+2f),
                "material source genuinely touches physical membrane fixture");
        Zero3CoveredEnergyV343 bridge=
             Zero3CoveredEnergyV343.laboratory(world);
        Zero3SurvivalLedgerV323 energy=
             new Zero3SurvivalLedgerV323(50f,0f,0f,false);
        energy.begin(0,0);
        for(int f=0;f<12;f++){
            bridge.afterWorld(world);
            energy.physicalStep(.025f,0f,body,skin,world);
        }
        energy.finish(0,12);
        require(energy.consumedSources()==0&&energy.energyGained()==0f&&
                food.concealed,
                "food leaked through still-closed physical cover");
        // Actual collision success is not proven by these manually supplied
        // physical flags. This unit fixture validates consequence ordering.
        box.bilateralHeldNow=true;
        bridge.afterWorld(world);
        box.bilateralHeldNow=false;
        box.x+=SquareGripPuzzle.UNCOVER_DISTANCE+1f;
        bridge.afterWorld(world);
        require(!food.concealed&&
                bridge.exposedSources()==1,
                "realistic uncover event did not change source state");
        energy.begin(1,12);
        for(int f=0;f<12;f++)
            energy.physicalStep(.025f,0f,body,skin,world);
        energy.finish(1,24);
        require(food.consumed&&energy.consumedSources()==1&&
                energy.positiveTouches()==1&&
                energy.energyGained()>39.99f&&
                energy.energyGained()<=40f,
                "uncovered real contact did not provide exactly finite +40");
        energy.begin(2,24);
        for(int f=0;f<12;f++)
            energy.physicalStep(.025f,0f,body,skin,world);
        energy.finish(2,36);
        require(energy.consumedSources()==1&&
                energy.positiveTouches()==1&&
                bridge.exposedSources()==1,
                "same energy was re-harvested");
        System.out.println("V343 PROOF after physical source contact "+
             "finiteActualEnergy="+energy.energyGained()+
             " consumedExactlyOnce=true zeroGripReward=true");
    }
    private static void emptyControl(){
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        UnknownWorld.Entity box=new UnknownWorld.Entity(10f,0f,2,5);
        empty.add(box);
        Zero3CoveredEnergyV343 bridge=
            Zero3CoveredEnergyV343.laboratory(empty);
        box.bilateralHeldNow=true;
        bridge.afterWorld(empty);
        box.x+=SquareGripPuzzle.UNCOVER_DISTANCE+8f;
        bridge.afterWorld(empty);
        require(bridge.exposedSources()==0&&bridge.coveredSources()==0,
                "empty physical BOX fabricated reward");
        System.out.println("V343 PROOF identical empty BOX zeroReward=true");
    }
    public static void main(String[] args){
        concealmentInvariant();
        actualEnergyContact();
        emptyControl();
        System.out.println("V343 COVERED_ENERGY_GATE PASS causeBeforeEffect "+
            "realTouchOnly, V315_energyUnavailable=true "+
            "V317_valueLearningUnproven=true LAB_ONLY G3_BLOCKED G5_BLOCKED NO_OTA");
    }
}
