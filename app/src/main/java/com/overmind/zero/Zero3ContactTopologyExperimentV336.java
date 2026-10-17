package com.overmind.zero;

/**
 * ZERO 3.36 LAB ONLY alternative projection for a shell chord touching a
 * rectangular solid. Never called in the existing production physics path.
 *
 * Important: a solid-box chord test and a membrane self-crossing test are
 * separate invariants. A different face is legal only when all incident
 * membrane edges avoid the solid, the complete shell stays simple, and the
 * move stays local (no teleport across the 40x40 square).
 */
final class Zero3ContactTopologyExperimentV336 {
    private Zero3ContactTopologyExperimentV336(){}
    private static final float MAX_PATCH_MOVE=12f;

    static boolean tryAlternate(Morphology.Surface shell,
            UnknownWorld.Entity square,int a,int b,float clearance,
            float[] px,float[] py,float[] pvx,float[] pvy,
            float[] pHold,float[] pTouch) {
        if(shell==null||square==null||px.length!=2||py.length!=2)
            return false;
        if(pHold[0]>=Morphology.GRIP_HOLD||
           pHold[1]>=Morphology.GRIP_HOLD)return false;

        // Keep the actual original correction for the fallback.
        float[] oldX={shell.x[a],shell.x[b]};
        float[] oldY={shell.y[a],shell.y[b]};
        float[] oldVX={shell.vx[a],shell.vx[b]};
        float[] oldVY={shell.vy[a],shell.vy[b]};
        float[] oldHold={shell.gripHold[a],shell.gripHold[b]};
        float[] oldTouch={shell.touchSense[a],shell.touchSense[b]};
        float l=square.x-SquareGripPuzzle.HALF,
              r=square.x+SquareGripPuzzle.HALF,
              t=square.y-SquareGripPuzzle.HALF,
              bot=square.y+SquareGripPuzzle.HALF;
        float[] line={l-clearance,r+clearance,t-clearance,bot+clearance};
        float best=Float.POSITIVE_INFINITY;
        int winningSide=-1;
        for(int side=0;side<4;side++){
            for(int k=0;k<2;k++){
                int id=k==0?a:b;
                shell.x[id]=px[k];shell.y[id]=py[k];
                shell.vx[id]=pvx[k];shell.vy[id]=pvy[k];
                shell.gripHold[id]=0f; // invalid through-solid chord
                shell.touchSense[id]=0f;
                if(side==0&&shell.x[id]>line[side]){
                    shell.x[id]=line[side];
                    shell.vx[id]=Math.min(0,shell.vx[id]);
                }else if(side==1&&shell.x[id]<line[side]){
                    shell.x[id]=line[side];
                    shell.vx[id]=Math.max(0,shell.vx[id]);
                }else if(side==2&&shell.y[id]>line[side]){
                    shell.y[id]=line[side];
                    shell.vy[id]=Math.min(0,shell.vy[id]);
                }else if(side==3&&shell.y[id]<line[side]){
                    shell.y[id]=line[side];
                    shell.vy[id]=Math.max(0,shell.vy[id]);
                }
            }
            boolean valid=MaterialGeometry.selfCrossings(shell)==0;
            float work=0f;
            for(int k=0;k<2;k++){
                int id=k==0?a:b;
                float dx=shell.x[id]-px[k],dy=shell.y[id]-py[k];
                float dist=(float)Math.hypot(dx,dy);
                if(!Float.isFinite(dist)||dist>MAX_PATCH_MOVE)valid=false;
                work+=dist*dist;
                if(Math.abs(shell.x[id]-square.x)<
                       SquareGripPuzzle.HALF-1e-4f&&
                   Math.abs(shell.y[id]-square.y)<
                       SquareGripPuzzle.HALF-1e-4f)valid=false;
            }
            int n=Morphology.POINTS;
            int before=(a+n-1)%n,after=(b+1)%n;
            if(crosses(shell,before,a,l,t,r,bot)||
               crosses(shell,a,b,l,t,r,bot)||
               crosses(shell,b,after,l,t,r,bot))valid=false;
            if(valid&&work<best){
                best=work;winningSide=side;
            }
        }
        // Reset the interim candidates before committing one.
        for(int k=0;k<2;k++){
            int id=k==0?a:b;
            shell.x[id]=oldX[k];shell.y[id]=oldY[k];
            shell.vx[id]=oldVX[k];shell.vy[id]=oldVY[k];
            shell.gripHold[id]=oldHold[k];
            shell.touchSense[id]=oldTouch[k];
        }
        if(winningSide<0)return false;
        for(int k=0;k<2;k++){
            int id=k==0?a:b;
            shell.x[id]=px[k];shell.y[id]=py[k];
            shell.vx[id]=pvx[k];shell.vy[id]=pvy[k];
            shell.gripHold[id]=0f;
            shell.touchSense[id]=0f;
            if(winningSide==0&&shell.x[id]>line[winningSide]){
                shell.x[id]=line[winningSide];
                shell.vx[id]=Math.min(0,shell.vx[id]);
            }else if(winningSide==1&&shell.x[id]<line[winningSide]){
                shell.x[id]=line[winningSide];
                shell.vx[id]=Math.max(0,shell.vx[id]);
            }else if(winningSide==2&&shell.y[id]>line[winningSide]){
                shell.y[id]=line[winningSide];
                shell.vy[id]=Math.min(0,shell.vy[id]);
            }else if(winningSide==3&&shell.y[id]<line[winningSide]){
                shell.y[id]=line[winningSide];
                shell.vy[id]=Math.max(0,shell.vy[id]);
            }
        }
        if(MaterialGeometry.selfCrossings(shell)!=0||
           crosses(shell,(a+Morphology.POINTS-1)%Morphology.POINTS,a,l,t,r,bot)||
           crosses(shell,a,b,l,t,r,bot)||
           crosses(shell,b,(b+1)%Morphology.POINTS,l,t,r,bot)){
            throw new IllegalStateException("V336 alternate violated tested invariant");
        }
        return true;
    }
    private static boolean crosses(Morphology.Surface skin,int i,int j,
                       float l,float t,float r,float b){
        return ScarcityLab.crossesInterior(skin.x[i],skin.y[i],
                                          skin.x[j],skin.y[j],l,t,r,b);
    }
}
