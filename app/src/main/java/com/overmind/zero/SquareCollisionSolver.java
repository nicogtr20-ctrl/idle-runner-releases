package com.overmind.zero;

/**
 * 0116.7 purely physical CCD for the existing 40x40 movable square.
 * Does NOT exert forces on a square, hold a grip, reward contact, modify a
 * motor controller, or change a single sensor / decision channel.
 */
final class SquareCollisionSolver {
    // Membrane particles are surface sample points, not circles. An exact
    // face contact at +/-20 is LEGAL and must retain real gripHold.
    // Non-zero particle clearance would wipe every valid two-sided hold.
    private static final float POINT_CLEARANCE=0f;
    private static final float CORE_CLEARANCE=6f;
    private static final float BODY_CLEARANCE=1.5f;
    private static final float LINK_CLEARANCE=1.6f;
    private static final int LINK_PASSES=16;
    private SquareCollisionSolver(){}

    private static float clamp(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }
    /**
     * Slab-based segment / expanded AABB intersection. This includes the
     * case in which BOTH endpoints lie outside but the path crosses the box.
     * When there is no valid old position, project the current mass normally.
     */
    static boolean project(float[] px,float[] py,float[] vx,float[] vy,int i,
                           float priorX,float priorY,
                           float l,float t,float r,float b,float radius) {
        float x=px[i],y=py[i];
        if(!Float.isFinite(x)||!Float.isFinite(y)||
           !Float.isFinite(priorX)||!Float.isFinite(priorY))return false;
        float left=l-radius,right=r+radius,top=t-radius,bottom=b+radius;
        boolean initiallyOutside=priorX<=left||priorX>=right||
                                 priorY<=top||priorY>=bottom;
        float dx=x-priorX,dy=y-priorY,entry=0f,exit=1f;
        int face=-1;
        if(initiallyOutside && (Math.abs(dx)>1e-5f||Math.abs(dy)>1e-5f)){
            if(Math.abs(dx)<1e-7f) {
                if(priorX<=left||priorX>=right)exit=-1f;
            }else{
                float t0=(left-priorX)/dx,t1=(right-priorX)/dx;
                float near=Math.min(t0,t1),far=Math.max(t0,t1);
                if(near>=entry){entry=near;face=dx>0?0:1;}
                exit=Math.min(exit,far);
            }
            if(Math.abs(dy)<1e-7f){
                if(priorY<=top||priorY>=bottom)exit=-1f;
            }else{
                float t0=(top-priorY)/dy,t1=(bottom-priorY)/dy;
                float near=Math.min(t0,t1),far=Math.max(t0,t1);
                if(near>=entry){entry=near;face=dy>0?2:3;}
                exit=Math.min(exit,far);
            }
            if(face>=0 && entry<=exit && entry>=0 && entry<=1){
                float hit=Math.max(0f,entry-0.0001f);
                px[i]=priorX+dx*hit;
                py[i]=priorY+dy*hit;
                if(face==0){px[i]=Math.min(px[i],left);vx[i]=Math.min(0,vx[i]);}
                else if(face==1){px[i]=Math.max(px[i],right);vx[i]=Math.max(0,vx[i]);}
                else if(face==2){py[i]=Math.min(py[i],top);vy[i]=Math.min(0,vy[i]);}
                else {py[i]=Math.max(py[i],bottom);vy[i]=Math.max(0,vy[i]);}
                return true;
            }
        }
        // If a restored scene was already embedded or had been warped by a
        // previous frame, eject to nearest face. Never apply impulse to box.
        if(x<=left||x>=right||y<=top||y>=bottom)return false;
        float dl=x-left,dr=right-x,dt=y-top,db=bottom-y;
        float best=dl;int side=0;
        if(dr<best){best=dr;side=1;}
        if(dt<best){best=dt;side=2;}
        if(db<best){side=3;}
        if(side==0){px[i]=left;vx[i]=Math.min(0,vx[i]);}
        else if(side==1){px[i]=right;vx[i]=Math.max(0,vx[i]);}
        else if(side==2){py[i]=top;vy[i]=Math.min(0,vy[i]);}
        else {py[i]=bottom;vy[i]=Math.max(0,vy[i]);}
        return true;
    }

    /**
     * An elastic edge cannot be a chord through the square's interior even
     * when both endpoints are individually outside. Move the two endpoints
     * to the nearest shared supporting face. This is geometric constraint,
     * NOT a grip or a shortcut to the opposite side.
     */
    private static boolean correctLink(float[] x,float[] y,float[] vx,
                                       float[] vy,int a,int b,
                                       float l,float t,float r,float bottom,
                                       float clearance,Morphology.Surface gripOwner){
        if(!ScarcityLab.crossesInterior(x[a],y[a],x[b],y[b],l,t,r,bottom))
            return false;
        float left=l-clearance,right=r+clearance;
        float top=t-clearance,low=bottom+clearance;
        float costLeft=Math.max(0,x[a]-left)+Math.max(0,x[b]-left);
        float costRight=Math.max(0,right-x[a])+Math.max(0,right-x[b]);
        float costTop=Math.max(0,y[a]-top)+Math.max(0,y[b]-top);
        float costBottom=Math.max(0,low-y[a])+Math.max(0,low-y[b]);
        int side=0;float min=costLeft;
        if(costRight<min){min=costRight;side=1;}
        if(costTop<min){min=costTop;side=2;}
        if(costBottom<min){side=3;}
        for(int k=0;k<2;k++){
            int idx=k==0?a:b;
            if(side==0 && x[idx]>left){x[idx]=left;vx[idx]=Math.min(0,vx[idx]);}
            else if(side==1 && x[idx]<right){x[idx]=right;vx[idx]=Math.max(0,vx[idx]);}
            else if(side==2 && y[idx]>top){y[idx]=top;vy[idx]=Math.min(0,vy[idx]);}
            else if(side==3 && y[idx]<low){y[idx]=low;vy[idx]=Math.max(0,vy[idx]);}
            // Only a penetrated link cancels a physically invalid hold.
            if(gripOwner!=null){
                gripOwner.gripHold[idx]=0f;
                gripOwner.touchSense[idx]=0f;
            }
        }
        return true;
    }
    static int repairLinks(float[] x,float[] y,float[] vx,float[] vy,
                           int n,UnknownWorld.Entity square,float clearance,
                           Morphology.Surface skin){
        int count=0;
        float left=square.x-SquareGripPuzzle.HALF,
              right=square.x+SquareGripPuzzle.HALF,
              top=square.y-SquareGripPuzzle.HALF,
              bottom=square.y+SquareGripPuzzle.HALF;
        for(int pass=0;pass<LINK_PASSES;pass++){
            int fixed=0;
            for(int i=0;i<n;i++){
                int j=(i+1)%n;
                if(correctLink(x,y,vx,vy,i,j,left,top,right,bottom,
                               clearance,skin))fixed++;
            }
            count+=fixed;
            if(fixed==0)break;
        }
        return count;
    }
    /**
     * Runs after movement and BEFORE grip evaluation, so illegal
     * through-wall contacts cannot become bilateral grip successes.
     */
    static void constrain(MotorBrain.Body body,Morphology.Surface skin,
                          UnknownWorld.Entity box,float[] oldSkinX,float[] oldSkinY,
                          float[] oldBodyX,float[] oldBodyY,
                          float oldCoreX,float oldCoreY){
        float l=box.x-SquareGripPuzzle.HALF,r=box.x+SquareGripPuzzle.HALF;
        float t=box.y-SquareGripPuzzle.HALF,b=box.y+SquareGripPuzzle.HALF;
        for(int i=0;i<Morphology.POINTS;i++){
            float px=oldSkinX==null?skin.x[i]:oldSkinX[i];
            float py=oldSkinY==null?skin.y[i]:oldSkinY[i];
            if(project(skin.x,skin.y,skin.vx,skin.vy,i,px,py,
                       l,t,r,b,POINT_CLEARANCE)){
                // Proximity/touch is later computed from actual corrected
                // membrane geometry by SquareGripPuzzle. No synthetic grip.
                skin.gripHold[i]=0f;
            }
        }
        for(int i=0;i<MotorBrain.N;i++)
            project(body.x,body.y,body.vx,body.vy,i,
                    oldBodyX==null?body.x[i]:oldBodyX[i],
                    oldBodyY==null?body.y[i]:oldBodyY[i],
                    l,t,r,b,BODY_CLEARANCE);
        float[] cx={body.cx},cy={body.cy},cvx={body.cvx},cvy={body.cvy};
        project(cx,cy,cvx,cvy,0,oldCoreX,oldCoreY,
                l,t,r,b,CORE_CLEARANCE);
        body.cx=cx[0];body.cy=cy[0];body.cvx=cvx[0];body.cvy=cvy[0];
        repairLinks(skin.x,skin.y,skin.vx,skin.vy,Morphology.POINTS,box,
                    LINK_CLEARANCE,skin);
        repairLinks(body.x,body.y,body.vx,body.vy,MotorBrain.N,box,
                    BODY_CLEARANCE,null);
    }
    /** Body or membrane adjustments can reintroduce an edge crossing.
     * After the original square-grip solver, require all links outside.
     */
    static void finish(MotorBrain.Body body,Morphology.Surface skin,
                       UnknownWorld.Entity box){
        repairLinks(skin.x,skin.y,skin.vx,skin.vy,Morphology.POINTS,box,
                    LINK_CLEARANCE,skin);
        repairLinks(body.x,body.y,body.vx,body.vy,MotorBrain.N,box,
                    BODY_CLEARANCE,null);
    }
    static int intersections(Morphology.Surface s,UnknownWorld.Entity b){
        int n=0;
        float l=b.x-SquareGripPuzzle.HALF,r=b.x+SquareGripPuzzle.HALF,
              t=b.y-SquareGripPuzzle.HALF,bot=b.y+SquareGripPuzzle.HALF;
        for(int i=0;i<Morphology.POINTS;i++)
            if(ScarcityLab.crossesInterior(s.x[i],s.y[i],
                 s.x[(i+1)%Morphology.POINTS],s.y[(i+1)%Morphology.POINTS],
                 l,t,r,bot))n++;
        return n;
    }
    private static void test(boolean v,String message){
        if(!v)throw new AssertionError(message);
    }
    public static void main(String[] args){
        UnknownWorld.Entity b=new UnknownWorld.Entity(0,0,2,5);
        // High velocity crossing: BOTH endpoints completely outside.
        float[] x={-55},y={0},vx={3700},vy={0};
        test(project(x,y,vx,vy,0,-55,0,-20,-20,20,20,1.15f)==false,
             "Static outside point should remain outside");
        x[0]=65;
        test(project(x,y,vx,vy,0,-55,0,-20,-20,20,20,1.15f),
             "Swept CCD did not detect complete tunneling");
        test(x[0]<=-21.15f&&vx[0]<=0,
             "Swept CCD teleported node to far side of square");
        // Regression: ZERO is already touching the near face at the
        // frame boundary. Entry time is EXACTLY zero. A fast inward
        // movement must still stop, not teleport to the opposite side.
        x[0]=65;y[0]=0;vx[0]=4200;
        test(project(x,y,vx,vy,0,-20,0,-20,-20,20,20,0f),
             "Face contact at t=0 skipped swept collision");
        test(x[0]<=-20f&&vx[0]<=0,
             "Previously touching node teleported through the box");
        // A link with both endpoints outside must not pass through the solid.
        float[] lx={-26,26},ly={0,0},lvx={0,0},lvy={0,0};
        // n=2 ring has two opposite duplicated edges; still must resolve.
        test(repairLinks(lx,ly,lvx,lvy,2,b,1.6f,null)>0,
             "Opposite-edge chord was undetected");
        test(!ScarcityLab.crossesInterior(lx[0],ly[0],lx[1],ly[1],
                                          -20,-20,20,20),
             "Solver left an impossible chord through the square");
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        for(int i=0;i<Morphology.POINTS;i++){
            double a=Math.PI*2*i/Morphology.POINTS;
            skin.x[i]=Math.round((float)Math.cos(a)*34f);
            skin.y[i]=Math.round((float)Math.sin(a)*34f);
        }
        float[] oldX=skin.x.clone(),oldY=skin.y.clone(),
                oldBX=body.x.clone(),oldBY=body.y.clone();
        // A material particle shot right across, and a neighbor link
        // passing through, must both end outside.
        oldX[0]=-34;oldY[0]=0;
        skin.x[0]=59;skin.y[0]=0;skin.vx[0]=3800;
        constrain(body,skin,b,oldX,oldY,oldBX,oldBY,body.cx,body.cy);
        finish(body,skin,b);
        test(intersections(skin,b)==0,"Membrane still cuts through box");
        test(skin.x[0]<-20||skin.y[0]<=-20||skin.y[0]>=20,
             "Fast material node ended beyond the box");
        test(body.cx<=-26||body.cx>=26||body.cy<=-26||body.cy>=26,
             "Physical core remains inside square");
        System.out.println("SquareCollisionSolver PASS: swept through-box CCD, "+
            "segments, body masses, core, non-penetrating membrane perimeter");
    }
}
