package com.overmind.zero;
import java.util.List;
/** Shared live/laboratory dispatch: liquid and shell see the same solids. */
public final class SymbioteWorldStepV171 {
    private SymbioteWorldStepV171(){}
    public static void interior(SymbioteMatterV1 gel,MotorBrain.Body body,
        Morphology.Surface skin,ScarcityLab room,List<UnknownWorld.Entity> objects,float dt){
        if(room!=null)room.constrainInterior(gel,dt);
        for(UnknownWorld.Entity e:objects){
            if(e.consumed)continue;
            if(e.isConstruction())PanelPhysics.constrainInterior(gel,e,dt);
            else if(e.isBilateralBox())SquareCollisionSolver.constrainInterior(gel,e,dt);
        }
        gel.observeBody(body,skin);
    }
}
