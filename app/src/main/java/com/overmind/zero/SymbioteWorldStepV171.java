package com.overmind.zero;
import java.util.List;
/** Shared live/laboratory dispatch: liquid and shell see the same solids. */
public final class SymbioteWorldStepV171 {
    private SymbioteWorldStepV171(){}
    public static void interior(SymbioteMatterV1 gel,MotorBrain.Body body,
        Morphology.Surface skin,ScarcityLab room,List<UnknownWorld.Entity> objects,float dt){
        interior(gel,body,skin,room,objects,dt,null,null);
    }
    static void interior(SymbioteMatterV1 gel,MotorBrain.Body body,
        Morphology.Surface skin,ScarcityLab room,List<UnknownWorld.Entity> objects,
        float dt,float[] preWorldSkinX,float[] preWorldSkinY){
        if(room!=null)room.constrainInterior(gel,dt);
        for(UnknownWorld.Entity e:objects){
            if(e.consumed)continue;
            if(e.isConstruction())PanelPhysics.constrainInterior(gel,e,dt);
            else if(e.isBilateralBox())SquareCollisionSolver.constrainInterior(gel,e,dt);
            else if(e.isTrainingWall())TrainingRoomV379.constrainInner(gel,e,dt);
        }
        // World obstacles can fold the 96-point contour AFTER gel.step has
        // already completed its own topology pass. Repair at the actual
        // post-collision boundary, before sight, touch and proprioception read.
        if(MaterialGeometry.selfCrossings(skin)>0){
            if(preWorldSkinX!=null&&preWorldSkinY!=null)
                gel.rejectNewWorldCrossingsV386(body,skin,
                    preWorldSkinX,preWorldSkinY,dt);
            else gel.reconcileExternalContourV386(body,skin,dt);
        }
        gel.observeBody(body,skin);
    }
}
