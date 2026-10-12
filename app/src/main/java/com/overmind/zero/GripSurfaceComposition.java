package com.overmind.zero;
import java.util.List;

/**
 * Combines the visual/tactile result of real solids AFTER their independent
 * physics solvers. This is read-only for the gate and box hold timers.
 * Neither the shape controller nor its 96 commands are altered.
 */
final class GripSurfaceComposition {
    private GripSurfaceComposition(){}
    static boolean boxFace(UnknownWorld.Entity b,float x,float y){
        float dx=x-b.x,dy=y-b.y,h=SquareGripPuzzle.HALF,m=4f;
        return (Math.abs(Math.abs(dx)-h)<=m&&Math.abs(dy)<=h+m)||
               (Math.abs(Math.abs(dy)-h)<=m&&Math.abs(dx)<=h+m);
    }
    static boolean gateFace(ScarcityLab g,float x,float y){
        if(g==null)return false;
        float cx=g.gateX(),cy=g.gateCenterY();
        return Math.abs(y-cy)<=ScarcityLab.DOOR_HALF_H+2f&&
            ((x<cx&&Math.abs(x-(cx-ScarcityLab.DOOR_HALF_W))<=3.7f)||
             (x>=cx&&Math.abs(x-(cx+ScarcityLab.DOOR_HALF_W))<=3.7f));
    }
    static void compose(Morphology.Surface skin,ScarcityLab gate,
                        List<UnknownWorld.Entity> objects){
        for(int i=0;i<Morphology.POINTS;i++){
            float hold=0;
            boolean touch=false;
            boolean local=skin.gripCommand[i]>Morphology.GRIP_THRESHOLD;
            if(gate!=null&&gateFace(gate,skin.x[i],skin.y[i])){
                touch=true;
                if(local)hold=Math.max(hold,gate.gateGripHold[i]);
            }
            if(objects!=null)for(UnknownWorld.Entity box:objects){
                if(box.isBilateralBox()&&boxFace(box,skin.x[i],skin.y[i])){
                    touch=true;
                    if(local)hold=Math.max(hold,box.boxGripHold[i]);
                }else if(box.isConstruction()&&
                         PanelPhysics.touching(box,skin.x[i],skin.y[i])){
                    touch=true;
                    // Same material-owned timer as the real one-point plate
                    // actuator: red LED cannot light for someone else's grip.
                    if(PanelPhysics.held(box,i,skin))
                        hold=Math.max(hold,box.boxGripHold[i]);
                }
            }
            // Shared fields are OUTPUTS of physics, never storage for any one
            // object's actual accumulated adhesive timer.
            skin.gripHold[i]=hold;
            skin.touchSense[i]=touch?1f:0f;
        }
    }
}
