package com.overmind.zero;

import java.util.List;

/**
 * Physics-only finite-area constraints for ZERO's real membrane.
 *
 * A single particle cannot be adhesively owned by two rigid covers in one
 * physical frame. Distinct particles remain free to hold different covers.
 * When adjacent particles have become co-located, a small local material
 * exclusion impulse separates them into available space. This is NOT
 * movement toward a reward, a force-free teleport or an automatic grip OFF
 * action: only physically impossible duplicated contacts are revoked.
 */
public final class MaterialContactConstraints {
    private MaterialContactConstraints(){}
    private static final float COINCIDENT=0.70f;
    private static final float MIN_DIST2=COINCIDENT*COINCIDENT;
    private static final int MAX_REPAIRS=20;
    private static final float HALF=SquareGripPuzzle.HALF;
    public static long historicalRepairs=0; // session diagnostic, not cognition

    private static float faceGap(UnknownWorld.Entity box,float x,float y){
        float dx=Math.abs(x-box.x),dy=Math.abs(y-box.y);
        float a=dy<=HALF+4f?Math.abs(dx-HALF):Float.POSITIVE_INFINITY;
        float b=dx<=HALF+4f?Math.abs(dy-HALF):Float.POSITIVE_INFINITY;
        return Math.min(a,b);
    }
    private static boolean insideCover(float x,float y,List<UnknownWorld.Entity> objects){
        for(UnknownWorld.Entity b:objects){
            if(!b.isBilateralBox()||b.consumed)continue;
            // A legal face point at +/-20 must remain available for gripping.
            if(Math.abs(x-b.x)<HALF-.06f&&Math.abs(y-b.y)<HALF-.06f)
                return true;
        }
        return false;
    }
    /** Read the actual box-owned physics timers, not the visual orange dots.
     * Tie breaking uses geometry and world coordinates, never list order.
     * Preserves multiple legal holds using DIFFERENT body particles.
     */
    private static int resolveMultiOwner(Morphology.Surface s,
                                         List<UnknownWorld.Entity> objects){
        int changed=0;
        for(int i=0;i<Morphology.POINTS;i++){
            UnknownWorld.Entity chosen=null;
            float best=Float.POSITIVE_INFINITY;
            for(UnknownWorld.Entity box:objects){
                if(!box.isBilateralBox()||box.consumed)continue;
                if(box.boxGripHold[i]<=0)continue;
                float gap=faceGap(box,s.x[i],s.y[i]);
                // Drop stale holds that no longer touch their own cover.
                if(gap>4f||s.gripCommand[i]<=Morphology.GRIP_THRESHOLD){
                    box.boxGripHold[i]=0f;changed++;continue;
                }
                if(chosen==null||gap<best-.0001f||
                   (Math.abs(gap-best)<=.0001f&&
                    (box.x<chosen.x||(box.x==chosen.x&&box.y<chosen.y)))){
                    chosen=box;best=gap;
                }
            }
            if(chosen==null)continue;
            for(UnknownWorld.Entity box:objects){
                if(box==chosen||!box.isBilateralBox())continue;
                if(box.boxGripHold[i]>0){box.boxGripHold[i]=0f;changed++;}
            }
        }
        return changed;
    }
    private static void releaseDuplicateAt(int i,Morphology.Surface skin,
                                           List<UnknownWorld.Entity> objects){
        skin.gripHold[i]=0f;
        // The neural gripCommand is NEVER modified: if it chooses to keep
        // gripping, the real tissue may reattach at a new valid face.
        for(UnknownWorld.Entity box:objects)
            if(box.isBilateralBox())box.boxGripHold[i]=0f;
    }
    private static boolean overlapsEarlier(Morphology.Surface s,int i,
                                           float x,float y,float minDist2){
        for(int j=0;j<i;j++){
            float dx=x-s.x[j],dy=y-s.y[j];
            if(dx*dx+dy*dy<minDist2)return true;
        }
        return false;
    }
    /** Called AFTER all box solvers and BEFORE visual/tactile composition.
     * Only impossible co-location receives local repulsion. A legal sustained
     * single-cover grip is untouched; there is no objective or heading.
     */
    public static int reconcile(MotorBrain.Body body,Morphology.Surface skin,
                                List<UnknownWorld.Entity> boxes,float dt){
        if(body==null||skin==null||boxes==null||!Float.isFinite(dt)||dt<=0)
            return 0;
        int changed=resolveMultiOwner(skin,boxes);
        boolean degenerate=false;
        for(int i=0;i<Morphology.POINTS;i++){
            int j=(i+1)%Morphology.POINTS;
            float dx=skin.x[i]-skin.x[j],dy=skin.y[i]-skin.y[j];
            if(dx*dx+dy*dy<MIN_DIST2){degenerate=true;break;}
        }
        if(!degenerate)return changed;
        int fixed=0;
        for(int i=1;i<Morphology.POINTS&&fixed<MAX_REPAIRS;i++){
            if(!overlapsEarlier(skin,i,skin.x[i],skin.y[i],MIN_DIST2))
                continue;
            // Use each particle's ACTUAL muscle anchor for a direction, not
            // the nearest energy or a direction chosen by a programmer.
            float ax=skin.anchorX(i)-skin.x[i];
            float ay=skin.anchorY(i)-skin.y[i];
            float phase=(float)Math.atan2(ay,ax);
            if(ax*ax+ay*ay<.01f)
                phase=(float)(2*Math.PI*i/Morphology.POINTS);
            boolean moved=false;
            for(int k=0;k<3&&!moved;k++){
                float radius=k==0?1.0f:k==1?2.0f:3.5f;
                // Choose the first free candidate nearest the existing
                // elastic anchor; rejected candidates cannot pass through
                // a cover interior.
                for(int angleIndex=0;angleIndex<16;angleIndex++){
                    int n=(angleIndex%2==0)?angleIndex/2:-(angleIndex+1)/2;
                    float angle=phase+n*(float)(Math.PI/8);
                    float nx=skin.x[i]+radius*(float)Math.cos(angle);
                    float ny=skin.y[i]+radius*(float)Math.sin(angle);
                    if(insideCover(nx,ny,boxes)||
                       overlapsEarlier(skin,i,nx,ny,MIN_DIST2))continue;
                    float ddx=nx-skin.x[i],ddy=ny-skin.y[i];
                    releaseDuplicateAt(i,skin,boxes);
                    skin.x[i]=nx;skin.y[i]=ny;
                    skin.vx[i]=Math.max(-230,Math.min(230,
                        skin.vx[i]+ddx/Math.max(dt,.016f)*.35f));
                    skin.vy[i]=Math.max(-230,Math.min(230,
                        skin.vy[i]+ddy/Math.max(dt,.016f)*.35f));
                    fixed++;moved=true;break;
                }
            }
            if(!moved){
                // A crowded channel can prevent movement this frame.
                // Duplicate material cannot continue claiming its full grip.
                releaseDuplicateAt(i,skin,boxes);
            }
        }
        historicalRepairs+=fixed;
        // A moved particle no longer supplies a phantom bilateral hold:
        // calculate this-frame box eligibility from surviving real holds.
        for(UnknownWorld.Entity box:boxes){
            if(!box.isBilateralBox())continue;
            int[] sides={-1,-1,-1,-1};int n=0;
            for(int i=0;i<Morphology.POINTS;i++){
                if(box.boxGripHold[i]<Morphology.GRIP_HOLD)continue;
                float dx=skin.x[i]-box.x,dy=skin.y[i]-box.y;
                if(faceGap(box,skin.x[i],skin.y[i])>4f){
                    box.boxGripHold[i]=0f;continue;
                }
                float left=Math.abs(dx+HALF),right=Math.abs(dx-HALF);
                float top=Math.abs(dy+HALF),bottom=Math.abs(dy-HALF);
                int side=0;float v=left;
                if(right<v){side=1;v=right;}
                if(top<v){side=2;v=top;}
                if(bottom<v)side=3;
                sides[side]=i;n++;
            }
            box.bilateralHeldNow=n>=2&&
                (separate(sides[0],sides[1])||separate(sides[2],sides[3]));
        }
        return changed+fixed;
    }
    private static boolean separate(int a,int b){
        if(a<0||b<0)return false;
        int d=Math.abs(a-b);
        return Math.min(d,Morphology.POINTS-d)>=Morphology.POINTS/10;
    }
}
