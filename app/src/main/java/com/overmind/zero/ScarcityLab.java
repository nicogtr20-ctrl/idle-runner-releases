package com.overmind.zero;

import java.util.Locale;

/** ZERO-008. Deterministic mechanics of one physical chamber challenge.
 * No semantic "arm" identifiers exist: opposite-sided simultaneous surface
 * constraints are measured from contact positions and relative velocities.
 * The food is inaccessible to the core before the obstacle is moved.
 */
public final class ScarcityLab {
    public static final float ENTRY_X=115,END_X=314,TOP=-105,BOTTOM=105;
    public static final float OPEN_HALF_HEIGHT=43f,DOOR_HALF_W=13,DOOR_HALF_H=34;
    public final float ox,oy;
    public final UnknownWorld.Entity food;
    public float doorY=0,doorVelocity=0,dualContactTime=0;
    public int grasps=0,oneSidedContacts=0,opposedContactFrames=0;
    public boolean reachedEnergy=false,everOpened=false,coreEntered=false;
    public int preventedTunnels=0,blockedSegments=0;
    public float maxDoorTravel=0;
    private boolean wasGrasping=false;
    private final float[] temp=new float[4];

    public ScarcityLab(float x,float y){
        ox=x;oy=y;
        food=new UnknownWorld.Entity(ox+242,oy,0,0);
    }
    public float entranceX(){return ox+ENTRY_X;}
    public float gateX(){return entranceX();}
    public float gateCenterY(){return oy+doorY;}
    public float gateTop(){return gateCenterY()-DOOR_HALF_H;}
    public float gateBottom(){return gateCenterY()+DOOR_HALF_H;}
    public boolean openingFree(){return doorY>=77f;}
    public float foodDistance(float x,float y){return (float)Math.hypot(food.x-x,food.y-y);}

    /**
     * Coupled physical friction: an immovable sliding gate needs compressive
     * surface contact on BOTH opposite faces, with physically distinct
     * material particles. Single-sided pushes never translate the gate.
     */
    public float solveGate(Morphology.Surface skin,float dt){
        float lx=gateX()-DOOR_HALF_W,rx=gateX()+DOOR_HALF_W;
        float cy=gateCenterY();
        int leftCount=0,rightCount=0,leftIdx=-1,rightIdx=-1;
        float lv=0,rv=0;
        for(int i=0;i<Morphology.POINTS;i++){
            float x=skin.x[i],y=skin.y[i];
            if(Math.abs(y-cy)>DOOR_HALF_H+2)continue;
            float dl=Math.abs(x-lx),dr=Math.abs(x-rx);
            if(dl<=3.7f&&x<gateX()){
                leftCount++;leftIdx=i;lv+=skin.vy[i];
                // Impenetrability of the gate's solid left face
                if(x>lx-1.8f){skin.x[i]=lx-1.8f;skin.vx[i]=Math.min(0,skin.vx[i])*.40f;}
            }else if(dr<=3.7f&&x>=gateX()){
                rightCount++;rightIdx=i;rv+=skin.vy[i];
                if(x<rx+1.8f){skin.x[i]=rx+1.8f;skin.vx[i]=Math.max(0,skin.vx[i])*.40f;}
            }
        }
        boolean independent=leftCount>0&&rightCount>0;
        // Prevent the same near-neighbor folded membrane from being counted as
        // two independent protrusions just because it curves around a corner.
        if(independent){
            int apart=Math.abs(leftIdx-rightIdx);
            independent=Math.min(apart,Morphology.POINTS-apart)>=Morphology.POINTS/10;
        }
        if(independent){
            opposedContactFrames++;
            dualContactTime+=dt;
            if(!wasGrasping&&dualContactTime>=.075f){grasps++;wasGrasping=true;}
            // Friction from the measured membrane's velocity; NO scripted
            // door "open" impulse when both sides touch.
            float desired=(lv/leftCount+rv/rightCount)*.5f;
            float acceleration=clamp((desired-doorVelocity)*5.5f,-160f,160f);
            doorVelocity+=acceleration*dt;
        }else{
            if(leftCount+rightCount>0)oneSidedContacts++;
            dualContactTime=0;wasGrasping=false;
        }
        float drag=(float)Math.exp(-(independent?3f:8f)*dt);
        doorVelocity=clamp(doorVelocity*drag,-80,80);
        float previous=doorY;
        doorY=clamp(doorY+doorVelocity*dt,0,105);
        if(doorY==0||doorY==105)doorVelocity=0;
        maxDoorTravel=Math.max(maxDoorTravel,doorY);
        if(openingFree())everOpened=true;
        return doorY-previous;
    }
    static float clamp(float x,float a,float b){return Math.min(b,Math.max(a,x));}

    /** Rectangular static walls and a sliding door. Core and inner particles
     * are physical obstacles, so the food cannot be reached through walls.
     */
    public void constrain(MotorBrain.Body body,Morphology.Surface skin,float dt){
        // The membrane is already simulated. A local segment must not thread
        // directly THROUGH the gate: only bending around the top/bottom edges
        // is allowed.
        // First reject illegal penetration, then measure legal opposite grips.
        // A tunneled particle must never count as a second hand.
        // Gate is an actual physical barrier for the body center and old
        // motor particles, even when its movement is still locked.
        float x=gateX(),yc=gateCenterY();
        applyToCore(body,x-DOOR_HALF_W,yc-DOOR_HALF_H,
            x+DOOR_HALF_W,yc+DOOR_HALF_H,10);
        // A closed gate fills the core-sized opening (only 9-unit gaps for
        // narrow pseudopodia). A core of radius 10 cannot squeeze through.
        if(!coreEntered && !openingFree() && body.cx>x-DOOR_HALF_W-10
             && body.cx<ox+END_X+10
             && Math.abs(body.cy-oy)<BOTTOM+11){
            body.cx=x-DOOR_HALF_W-10;
            body.cvx=Math.min(0,body.cvx);
            preventedTunnels++;
        }
        if(openingFree() && body.cx>x+DOOR_HALF_W+10)coreEntered=true;
        for(int k=0;k<MotorBrain.N;k++){
            for(int w=0;w<6;w++){
                boundaries(w,temp);
                sweptProject(body.x,body.y,body.vx,body.vy,k,
                        temp[0],temp[1],temp[2],temp[3],1.5f,dt);
            }
            sweptProject(body.x,body.y,body.vx,body.vy,k,
                x-DOOR_HALF_W,yc-DOOR_HALF_H,x+DOOR_HALF_W,yc+DOOR_HALF_H,1.5f,dt);
        }
        for(int i=0;i<Morphology.POINTS;i++){
            for(int w=0;w<6;w++){
                boundaries(w,temp);
                sweptProject(skin.x,skin.y,skin.vx,skin.vy,i,
                    temp[0],temp[1],temp[2],temp[3],1.5f,dt);
            }
            // Contact on gate edges not already resolved by grip physics
            sweptProject(skin.x,skin.y,skin.vx,skin.vy,i,
                x-DOOR_HALF_W,yc-DOOR_HALF_H,x+DOOR_HALF_W,yc+DOOR_HALF_H,1.5f,dt);
        }
        // A polygon can cut through a thin wall even when every vertex
        // remains outside. Correct crossing edges, not just individual nodes.
        enforceMembraneSegments(skin);
        solveGate(skin,dt);
        for(int w=0;w<6;w++){
            boundaries(w,temp);
            applyToCore(body,temp[0],temp[1],temp[2],temp[3],10);
        }
        // Reapply after all wall corrections, not only before them.
        if(!coreEntered&&!openingFree()&&body.cx>x-DOOR_HALF_W-10
             && body.cx<ox+END_X+10
             && Math.abs(body.cy-oy)<BOTTOM+11){
            body.cx=x-DOOR_HALF_W-10;body.cvx=Math.min(0,body.cvx);
            preventedTunnels++;
        }
    }

    /** Swept AABB contact prevents high-speed particles tunneling between
      * consecutive frames and being ejected from the wrong rectangle face. */
    private boolean sweptProject(float[] px,float[] py,float[] vx,float[] vy,int i,
                                 float l,float t,float r,float b,float radius,float dt){
        float nowX=px[i],nowY=py[i];
        float oldX=nowX-vx[i]*dt,oldY=nowY-vy[i]*dt;
        float el=l-radius,er=r+radius,et=t-radius,eb=b+radius;
        // Resolve horizontal plane crossing at the entry face.
        if(oldX<=el&&nowX>el&&vx[i]>0){
            float frac=(el-oldX)/Math.max(.00001f,nowX-oldX);
            float crossY=oldY+(nowY-oldY)*frac;
            if(crossY>=et&&crossY<=eb){
                px[i]=el;py[i]=crossY;vx[i]=0;preventedTunnels++;return true;
            }
        }
        if(oldX>=er&&nowX<er&&vx[i]<0){
            float frac=(er-oldX)/Math.min(-.00001f,nowX-oldX);
            float crossY=oldY+(nowY-oldY)*frac;
            if(crossY>=et&&crossY<=eb){
                px[i]=er;py[i]=crossY;vx[i]=0;preventedTunnels++;return true;
            }
        }
        if(oldY<=et&&nowY>et&&vy[i]>0){
            float frac=(et-oldY)/Math.max(.00001f,nowY-oldY);
            float crossX=oldX+(nowX-oldX)*frac;
            if(crossX>=el&&crossX<=er){
                py[i]=et;px[i]=crossX;vy[i]=0;preventedTunnels++;return true;
            }
        }
        if(oldY>=eb&&nowY<eb&&vy[i]<0){
            float frac=(eb-oldY)/Math.min(-.00001f,nowY-oldY);
            float crossX=oldX+(nowX-oldX)*frac;
            if(crossX>=el&&crossX<=er){
                py[i]=eb;px[i]=crossX;vy[i]=0;preventedTunnels++;return true;
            }
        }
        return project(px,py,vx,vy,i,l,t,r,b,radius);
    }

    static boolean segmentIntersectsBox(float ax,float ay,float bx,float by,
                                        float l,float t,float r,float b){
        // Liang-Barsky clipping on solid rectangle, also catches a segment
        // crossing a box when both endpoints are outside it.
        float dx=bx-ax,dy=by-ay;
        // Fast allocation-free Liang-Barsky equivalent; called hundreds
        // of times per physics tick at 96 membrane points.
        float enter=0f,leave=1f;
        if(Math.abs(dx)<1e-7f){if(ax<l||ax>r)return false;}
        else{
            float a=(l-ax)/dx,b=(r-ax)/dx;
            enter=Math.max(enter,Math.min(a,b));
            leave=Math.min(leave,Math.max(a,b));
        }
        if(Math.abs(dy)<1e-7f){if(ay<t||ay>b)return false;}
        else{
            float a=(t-ay)/dy,b=(b-ay)/dy;
            enter=Math.max(enter,Math.min(a,b));
            leave=Math.min(leave,Math.max(a,b));
        }
        return enter<=leave;
    }

    private void enforceMembraneSegments(Morphology.Surface skin){
        for(int pass=0;pass<3;pass++){
            boolean found=false;
            for(int i=0;i<Morphology.POINTS;i++){
                int j=(i+1)%Morphology.POINTS;
                // Check both gate and immutable walls.
                for(int box=-1;box<6;box++){
                    float l,t,r,b;
                    if(box<0){l=gateX()-DOOR_HALF_W;t=gateTop();
                        r=gateX()+DOOR_HALF_W;b=gateBottom();}
                    else{boundaries(box,temp);l=temp[0];t=temp[1];
                        r=temp[2];b=temp[3];}
                    if(!segmentIntersectsBox(skin.x[i],skin.y[i],skin.x[j],skin.y[j],
                                             l+.20f,t+.20f,r-.20f,b-.20f))continue;
                    // Pull the farther segment endpoint toward the same safe
                    // side as the body, instead of letting a straight membrane
                    // edge pass through a solid box.
                    int move=Math.hypot(skin.x[i]-ox,skin.y[i]-oy)
                              >Math.hypot(skin.x[j]-ox,skin.y[j]-oy)?i:j;
                    int stay=move==i?j:i;
                    float sx=skin.x[stay],sy=skin.y[stay];
                    if(sx<l){skin.x[move]=l-2.0f;skin.vx[move]=Math.min(0,skin.vx[move]);}
                    else if(sx>r){skin.x[move]=r+2.0f;skin.vx[move]=Math.max(0,skin.vx[move]);}
                    else if(sy<t){skin.y[move]=t-2.0f;skin.vy[move]=Math.min(0,skin.vy[move]);}
                    else{skin.y[move]=b+2.0f;skin.vy[move]=Math.max(0,skin.vy[move]);}
                    blockedSegments++;found=true;
                }
            }
            if(!found)break;
        }
    }

    /** Wall slabs: entrance outside the opening, far wall, ceiling, floor. */
    private void boundaries(int id,float[] r){
        float l=entranceX(),right=ox+END_X;
        float t=oy+TOP,b=oy+BOTTOM;
        switch(id){
            case 0:fill(r,l-7,t-9,l+7,oy-OPEN_HALF_HEIGHT);break;
            case 1:fill(r,l-7,oy+OPEN_HALF_HEIGHT,l+7,b+9);break;
            case 2:fill(r,l-7,t-9,right+7,t+7);break;
            case 3:fill(r,l-7,b-7,right+7,b+9);break;
            case 4:fill(r,right-7,t-9,right+7,b+9);break;
            default:fill(r,l-7,oy-OPEN_HALF_HEIGHT-7,l+7,oy-OPEN_HALF_HEIGHT);break;
        }
    }
    private static void fill(float[] r,float a,float b,float c,float d){
        r[0]=a;r[1]=b;r[2]=c;r[3]=d;
    }
    private static boolean project(float[] x,float[] y,float[] vx,float[] vy,
                                   int i,float l,float t,float r,float b,float radius){
        float px=x[i],py=y[i];
        float cx=clamp(px,l,r),cy=clamp(py,t,b);
        float dx=px-cx,dy=py-cy;
        float d=(float)Math.hypot(dx,dy);
        if(d>=radius)return false;
        float nx,ny;
        if(d<1e-4f){
            float dl=px-l,dr=r-px,dt=py-t,db=b-py;
            float min=Math.min(Math.min(dl,dr),Math.min(dt,db));
            nx=dl==min?-1f:dr==min?1f:0;
            ny=dt==min?-1f:db==min?1f:0;
            // Point is inside the rectangle: move outside from nearest face.
            float shift=radius+min;
            x[i]+=nx*shift;y[i]+=ny*shift;
        }else{
            nx=dx/d;ny=dy/d;
            x[i]+=nx*(radius-d);y[i]+=ny*(radius-d);
        }
        float speed=vx[i]*nx+vy[i]*ny;
        if(speed<0){vx[i]-=speed*nx;vy[i]-=speed*ny;}
        return true;
    }
    private void applyToCore(MotorBrain.Body b,float l,float t,float r,float bot,float radius){
        float[] px={b.cx},py={b.cy},vx={b.cvx},vy={b.cvy};
        if(project(px,py,vx,vy,0,l,t,r,bot,radius)){
            b.cx=px[0];b.cy=py[0];b.cvx=vx[0];b.cvy=vy[0];
        }
    }
    public boolean foodTouchedByCore(MotorBrain.Body body){
        if(reachedEnergy||!food.ready())return false;
        if((float)Math.hypot(body.cx-food.x,body.cy-food.y)<food.radius+9){
            reachedEnergy=true;food.touch(0,0);return true;
        }
        return false;
    }
    /**
     * Honest physical training trial: no manual assignment of door movement,
     * grasping or food. The controller gets only a target direction and local
     * sensor states, exactly as in the visible simulation.
     */
    public static float trial(float[] motor,float[] hand,float[] shape,float offsetY){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ScarcityLab lab=new ScarcityLab(-72,offsetY);
        float minGateDistance=200;
        for(int frame=0;frame<150;frame++){
            float dt=MotorBrain.STEP;
            float dx=lab.food.x-body.cx,dy=lab.food.y-body.cy;
            float len=(float)Math.hypot(dx,dy);
            if(len>.01f){dx/=len;dy/=len;}
            body.step(motor,dx,dy,dt,hand,lab.food.x,lab.food.y);
            skin.step(body,shape,lab.food.x,lab.food.y,dt);
            lab.constrain(body,skin,dt);
            float dist=(float)Math.hypot(body.cx-lab.gateX(),body.cy-lab.gateCenterY());
            minGateDistance=Math.min(minGateDistance,dist);
            if(lab.foodTouchedByCore(body))break;
        }
        float approach=clamp(110-minGateDistance,0,110);
        float score=.08f*approach
            +2.6f*lab.maxDoorTravel
            +.09f*Math.min(400,lab.opposedContactFrames)
            +4f*Math.min(2,lab.grasps)
            +(lab.reachedEnergy?260f:0f)-skin.totalEffort*.12f;
        return Float.isFinite(score)?score:-100000f;
    }
    public static float trainingFitness(float[] motor,float[] hand,float[] shape){
        return (trial(motor,hand,shape,-10)+trial(motor,hand,shape,12))*.5f;
    }
    public static float heldoutFitness(float[] motor,float[] hand,float[] shape){
        return trial(motor,hand,shape,25);
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface membrane=new Morphology.Surface(body);
        ScarcityLab chamber=new ScarcityLab(-115,0); // gate at x=0
        // A unilateral contact must NEVER translate a closed door.
        // All unused masses are kept away from the gate in the fixture.
        for(int i=0;i<Morphology.POINTS;i++){
            membrane.x[i]=-180;membrane.y[i]=-180;
            membrane.vx[i]=membrane.vy[i]=0;
        }
        membrane.x[0]=-ScarcityLab.DOOR_HALF_W-1;
        membrane.y[0]=0;membrane.vy[0]=65;
        for(int i=0;i<22;i++)chamber.solveGate(membrane,MotorBrain.STEP);
        if(chamber.maxDoorTravel>0.0001f||chamber.grasps!=0)
            throw new IllegalStateException("Single-sided push bypassed grip");
        // Two nonadjacent membrane particles on opposite faces physically
        // move together: tangential sliding friction can displace the door.
        int opposite=Morphology.POINTS/2;
        membrane.x[opposite]=ScarcityLab.DOOR_HALF_W+1;
        membrane.y[opposite]=0;membrane.vy[opposite]=65;
        for(int i=0;i<70;i++){
            membrane.y[0]=chamber.gateCenterY();
            membrane.y[opposite]=chamber.gateCenterY();
            chamber.solveGate(membrane,MotorBrain.STEP);
        }
        if(chamber.grasps<1||chamber.maxDoorTravel<15)
            throw new IllegalStateException("Opposing physical contacts failed to move gate");
        // Regression: a living core is larger than both little gaps
        // around the blocker. It cannot appear on the other side.
        ScarcityLab solid=new ScarcityLab(-115,0);
        MotorBrain.Body trespasser=new MotorBrain.Body();
        trespasser.cx=30;trespasser.cy=0;trespasser.cvx=500;
        Morphology.Surface thin=new Morphology.Surface(trespasser);
        solid.constrain(trespasser,thin,MotorBrain.STEP);
        if(trespasser.cx>solid.gateX()-DOOR_HALF_W-9.5f)
            throw new IllegalStateException("Core clipped through closed doorway");
        if(solid.doorY!=0)
            throw new IllegalStateException("Illicit contact moved solid blocker");
        // Another regression: two vertices can be outside the rectangle
        // while the membrane segment between them cuts through it.
        if(!segmentIntersectsBox(-24,0,24,0,-13,-34,13,34))
            throw new IllegalStateException("Missed segment crossing solid blocker");
        if(segmentIntersectsBox(-24,42,24,42,-13,-34,13,34))
            throw new IllegalStateException("Legal route above blocker rejected");
        // Swept collision must stop a fast particle before passing through.
        float[] tx={-2f},ty={2f},tvx={650},tvy={0};
        solid.sweptProject(tx,ty,tvx,tvy,0,-13,-34,13,34,1.5f,.03f);
        if(tx[0]>-14.4f)throw new IllegalStateException("Swept particle tunneled");
                ScarcityLab unopened=new ScarcityLab(0,0);
        if(unopened.foodTouchedByCore(new MotorBrain.Body()))
            throw new IllegalStateException("Core ate unreachable food from afar");
        float[] motor=MotorBrain.virgin(new java.util.Random(918273));
        float[] hand=ContactLearning.virgin();
        float[] shape=Morphology.virgin(new java.util.Random(14790));
        float score=trainingFitness(motor,hand,shape);
        if(!Float.isFinite(score)||score<=-99999)
            throw new IllegalStateException("Chamber training reward not finite");
        UnknownWorld.Entity supply=new UnknownWorld.Entity(0,0,0,0);
        if(supply.touch(0,0)<=0||supply.ready())
            throw new IllegalStateException("Consumed energy respawned");
        System.out.printf(Locale.US,"ZERO-008 physical test passed: points=%d unilateralTravel=0 bilateralTravel=%.2f grips=%d finiteEnergy=true%n",
                Morphology.POINTS,chamber.maxDoorTravel,chamber.grasps);
    }
}
