package com.overmind.zero;

/**
 * ZERO 0120: the center is an observation, never a mass.
 * All physical material is in the ten motor masses and the ninety-six
 * independently actuated linked perimeter masses. No artificial nucleus
 * is accelerated, collided or tethered. Position/velocity are read-only
 * aggregate quantities recalculated FROM material positions and velocities.
 */
public final class MaterialGeometry {
    private MaterialGeometry(){}
    public static void observe(MotorBrain.Body b){
        if(b==null)return;
        float x=0,y=0,vx=0,vy=0;
        for(int i=0;i<MotorBrain.N;i++){
            x+=b.x[i];y+=b.y[i];vx+=b.vx[i];vy+=b.vy[i];
        }
        float inv=1f/MotorBrain.N;
        b.cx=x*inv;b.cy=y*inv;b.cvx=vx*inv;b.cvy=vy*inv;
    }
    /** Distributed optical sensor facing a given world direction: physical
     * membrane patch, not an imaginary central eye/nucleus. Pure readout;
     * the central sensory weights and visual wavelengths are unchanged.
     */
    /** Shape diagnostic: count true non-adjacent edge intersections.
     * A truly filled fluid body should not degenerate into a folded web.
     * No feedback to the neural agent or reward.
     */
    private static float cross(float ax,float ay,float bx,float by,float cx,float cy){
        return (bx-ax)*(cy-ay)-(by-ay)*(cx-ax);
    }
    public static int selfCrossings(Morphology.Surface s){
        if(s==null)return 0;
        int count=0,n=Morphology.POINTS;
        for(int i=0;i<n;i++){
            int next=(i+1)%n;
            float ax=s.x[i],ay=s.y[i],bx=s.x[next],by=s.y[next];
            for(int j=i+2;j<n;j++){
                int after=(j+1)%n;
                if(after==i)continue;
                float cx=s.x[j],cy=s.y[j],dx=s.x[after],dy=s.y[after];
                float a=cross(ax,ay,bx,by,cx,cy),d=cross(ax,ay,bx,by,dx,dy);
                float e=cross(cx,cy,dx,dy,ax,ay),g=cross(cx,cy,dx,dy,bx,by);
                if((a>1e-4f&&d< -1e-4f||a< -1e-4f&&d>1e-4f)&&
                   (e>1e-4f&&g< -1e-4f||e< -1e-4f&&g>1e-4f))count++;
            }
        }
        return count;
    }
    public static float[] eye(Morphology.Surface s,float cx,float cy,
                              float dx,float dy){
        if(s==null)return new float[]{cx,cy};
        float norm=(float)Math.hypot(dx,dy);
        if(norm<.00001f)return new float[]{cx,cy};
        dx/=norm;dy/=norm;
        float best=-Float.MAX_VALUE,px=cx,py=cy;
        for(int i=0;i<Morphology.POINTS;i++){
            float x=s.x[i]-cx,y=s.y[i]-cy;
            float front=x*dx+y*dy;
            float sideways=Math.abs(x*dy-y*dx);
            // A patch has to face the source, not be a remote tentacle
            // extending at right angles to the intended sight-line.
            if(front>best&&front>0&&sideways<=Math.max(12f,.38f*front)){
                best=front;px=s.x[i];py=s.y[i];
            }
        }
        return new float[]{px,py};
    }
    public static float surfaceDistanceSquared(Morphology.Surface s,float x,float y){
        if(s==null)return Float.POSITIVE_INFINITY;
        float best=Float.POSITIVE_INFINITY;
        for(int i=0;i<Morphology.POINTS;i++){
            int j=(i+1)%Morphology.POINTS;
            float ex=s.x[j]-s.x[i],ey=s.y[j]-s.y[i];
            float d=ex*ex+ey*ey;
            float t=d>1e-10f?Math.max(0,Math.min(1,
                ((x-s.x[i])*ex+(y-s.y[i])*ey)/d)):0;
            float dx=x-(s.x[i]+t*ex),dy=y-(s.y[i]+t*ey);
            best=Math.min(best,dx*dx+dy*dy);
        }
        return best;
    }
    private static boolean hasArea(Morphology.Surface s){
        double area=0;
        for(int i=0;i<Morphology.POINTS;i++){
            int j=(i+1)%Morphology.POINTS;
            area+=(double)s.x[i]*s.y[j]-(double)s.x[j]*s.y[i];
        }
        return Math.abs(area)>2.0;
    }
    /** Food is absorbed ONLY where living material touches it, not where a
     * calculated centroid happens to be. The polygon interior also counts
     * as distributed material, independent of shape concavity.
     */
    public static boolean touches(MotorBrain.Body b,Morphology.Surface s,
                                  float x,float y,float radius){
        if(!Float.isFinite(x)||!Float.isFinite(y)||radius<0)return false;
        if(s!=null){
            // A self-crossed or area-less membrane is NOT a filled body.
            // OTA127 recorded 40 edge intersections while ZERO unfolded:
            // odd/even fill of that broken polygon can create phantom food
            // contacts even after removing the old +18-unit proximity rule.
            // Only a genuinely simple, non-zero-area contour has interior.
            // Real membrane edges and individual muscle masses below remain
            // fully eligible regardless of shape complexity.
            if(BodyIntegrity.encloses(s.x,s.y,x,y)&&
               selfCrossings(s)==0&&hasArea(s))return true;
            if(surfaceDistanceSquared(s,x,y)<radius*radius)return true;
        }
        if(b!=null){
            for(int i=0;i<MotorBrain.N;i++){
                float dx=x-b.x[i],dy=y-b.y[i];
                if(dx*dx+dy*dy<radius*radius)return true;
            }
        }
        return false;
    }
    public static void main(String[] args){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        if(!touches(b,s,0,0,5))throw new AssertionError("Body interior not material");
        if(touches(b,s,400,400,5))throw new AssertionError("Phantom remote contact");
        // A center outside a concave membrane is not itself a collider;
        // shifting 10 material masses MUST shift logical position exactly.
        float ox=b.cx;
        for(int i=0;i<MotorBrain.N;i++)b.x[i]+=19f;
        MaterialGeometry.observe(b);
        if(Math.abs(b.cx-ox-19f)>.0001f)throw new AssertionError("Ghost physical nucleus");
        b.cx+=400f;
        MaterialGeometry.observe(b);
        if(Math.abs(b.cx-ox-19f)>.0001f)throw new AssertionError("Centroid was independent mass");
        System.out.println("ZERO 0120: center is derived, surface contact geometry physical");
    }
}
