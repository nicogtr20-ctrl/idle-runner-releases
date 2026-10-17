package com.overmind.zero;

/**
 * ZERO V3.39 research bridge only, NOT a production Android runtime change.
 *
 * Exactly the V322B real WORLD sequence with the V338 SELECTIVE BOX solver
 * and V338 bounded transactional joint membrane/solid constraints AFTER
 * all world operations. Each independent closed-loop cortex / organism
 * gets its OWN body, gel, sensors, adhesion and actor. No matched action tape.
 */
final class Zero3JointWorldV339 {
    static final float DT=.025f;
    static long attempted,repaired,unresolved;
    private Zero3JointWorldV339(){}
    static void step(Zero3OrganismStoreV322.Physical p,
           float[] oldSkinX,float[] oldSkinY,
           float[] oldBodyX,float[] oldBodyY,
           float oldCoreX,float oldCoreY,
           float[] afterGelX,float[] afterGelY){
        if(p==null||afterGelX==null||afterGelY==null||
           afterGelX.length!=Morphology.POINTS||
           afterGelY.length!=Morphology.POINTS)
            throw new IllegalArgumentException("V339 actual aftergel snapshot missing");
        boolean boxes=false;
        for(UnknownWorld.Entity object:p.world){
            if(object.isBilateralBox()){
                // Exact V338D lab mode6 adhesion path, never the unsafe
                // V336 face-only path. True ownership and two-sided motor.
                SquareGripPuzzle.constrain(p.body,p.skin,object,DT,
                    oldSkinX,oldSkinY,oldBodyX,oldBodyY,
                    oldCoreX,oldCoreY,null,false,true,true);
                boxes=true;
            }
        }
        if(boxes)
            MaterialContactConstraints.reconcile(p.body,p.skin,p.world,DT);
        SymbioteWorldStepV171.interior(
            p.gel,p.body,p.skin,null,p.world,DT);
        GripSurfaceComposition.compose(p.skin,null,p.world);
        for(UnknownWorld.Entity object:p.world){
            if(object.isMovable()){
                Morphology.collide(p.skin,object,DT);
                ContactLearning.collide(p.body,object,DT);
                p.gel.observeBody(p.body,p.skin);
            }
            object.tick(DT);
        }
        // MUST be last. Contact/reconcile/box.tick can introduce crossings.
        // Returning UNRESOLVED never pretends an unsafe world is valid.
        for(UnknownWorld.Entity object:p.world)if(object.isBilateralBox()){
            Zero3JointBoxConstraintV338.Result result=
                Zero3JointBoxConstraintV338.solve(p.skin,object,
                                                   afterGelX,afterGelY);
            if(result.attempted){
                attempted++;
                if(result.repaired)repaired++;
                if(result.unresolved)unresolved++;
            }
        }
    }
    static boolean invalid(Zero3OrganismStoreV322.Physical p){
        if(p.gel.invalid!=0||MaterialGeometry.selfCrossings(p.skin)>0)
            return true;
        for(UnknownWorld.Entity object:p.world)if(object.isBilateralBox()){
            if(SquareCollisionSolver.intersections(p.skin,object)>0)
                return true;
            float h=SquareGripPuzzle.HALF-1e-4f;
            for(int i=0;i<Morphology.POINTS;i++)
                if(Math.abs(p.skin.x[i]-object.x)<h&&
                   Math.abs(p.skin.y[i]-object.y)<h)
                    return true;
        }
        return false;
    }
}
