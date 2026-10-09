package com.overmind.zero;

import java.util.Locale;

/** ZERO-008. Deterministic mechanics of one physical chamber challenge.
 * No semantic "arm" identifiers exist: opposite-sided simultaneous surface
 * constraints are measured from contact positions and relative velocities.
 * The food is inaccessible to the core before the obstacle is moved.
 */
public final class ScarcityLab {
    public static final float ENTRY_X=115,END_X=314,TOP=-105,BOTTOM=105;
    public static final float OPEN_HALF_HEIGHT=43f,DOOR_HALF_W=14,DOOR_HALF_H=69;
    // Free-standing gate IN FRONT of the doorway. Its 138-unit height
    // overlaps the entire 86-unit opening so there are no initial cracks.
    public static final float DOOR_FRONT_OFFSET=34f,DOOR_MAX_TRAVEL=170f;
    public static final float WALL_HALF_THICKNESS=10f,MAX_LINK_SPAN=23f;
    public final float ox,oy;
    public final UnknownWorld.Entity food;
    public float doorY=0,doorVelocity=0,dualContactTime=0;
    public int grasps=0,oneSidedContacts=0,opposedContactFrames=0;
    public boolean reachedEnergy=false,everOpened=false;
    public float maxDoorTravel=0;
    public int solidLinkCorrections=0;
    private boolean wasGrasping=false;
    private final float[] temp=new float[4];

    public ScarcityLab(float x,float y){
        ox=x;oy=y;
        food=new UnknownWorld.Entity(ox+242,oy,0,0);
    }
    public float entranceX(){return ox+ENTRY_X;}
    public float gateX(){return entranceX()-DOOR_FRONT_OFFSET;}
    public float gateCenterY(){return oy+doorY;}
    public float gateTop(){return gateCenterY()-DOOR_HALF_H;}
    public float gateBottom(){return gateCenterY()+DOOR_HALF_H;}
    public boolean openingFree(){return gateTop()>oy+OPEN_HALF_HEIGHT+3f;}
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
        doorY=clamp(doorY+doorVelocity*dt,0,DOOR_MAX_TRAVEL);
        if(doorY==0||doorY==DOOR_MAX_TRAVEL)doorVelocity=0;
        maxDoorTravel=Math.max(maxDoorTravel,doorY);
        if(openingFree())everOpened=true;
        return doorY-previous;
    }
    static float clamp(float x,float a,float b){return Math.min(b,Math.max(a,x));}

    /** Rectangular static walls and a sliding door. Core and inner particles
     * are physical obstacles, so the food cannot be reached through walls.
     */
    public void constrain(MotorBrain.Body body,Morphology.Surface skin,float dt){
        // Resolve the actual edges BEFORE sensing a bilateral grasp. A
        // membrane that has tunneled through the obstacle must not count as
        // an appendage legitimately placed on its opposite face.
        resolveSolidLinks(skin);
        solveGate(skin,dt);
        // Gate is an actual physical barrier for the body center and old
        // motor particles, even when its movement is still locked.
        float x=gateX(),yc=gateCenterY();
        applyToCore(body,x-DOOR_HALF_W,yc-DOOR_HALF_H,
            x+DOOR_HALF_W,yc+DOOR_HALF_H,10,dt);
        for(int k=0;k<MotorBrain.N;k++){
            for(int w=0;w<5;w++){
                boundaries(w,temp);
                sweptProject(body.x,body.y,body.vx,body.vy,k,
                        temp[0],temp[1],temp[2],temp[3],1.5f,dt);
            }
            sweptProject(body.x,body.y,body.vx,body.vy,k,
                x-DOOR_HALF_W,yc-DOOR_HALF_H,x+DOOR_HALF_W,yc+DOOR_HALF_H,1.5f,dt);
        }
        for(int i=0;i<Morphology.POINTS;i++){
            for(int w=0;w<5;w++){
                boundaries(w,temp);
                sweptProject(skin.x,skin.y,skin.vx,skin.vy,i,
                    temp[0],temp[1],temp[2],temp[3],1.5f,dt);
            }
            // Contact on gate edges not already resolved by grip physics
            sweptProject(skin.x,skin.y,skin.vx,skin.vy,i,
                x-DOOR_HALF_W,yc-DOOR_HALF_H,x+DOOR_HALF_W,yc+DOOR_HALF_H,1.5f,dt);
        }
        for(int w=0;w<5;w++){
            boundaries(w,temp);
            applyToCore(body,temp[0],temp[1],temp[2],temp[3],10,dt);
        }
        // A point may be outside a solid while the edge between two points
        // cuts straight through it. Never render or simulate such a crossing.
        resolveSolidLinks(skin);
    }

    /** Continuous segment/rectangle intersection (strict interior), using
     * slab clipping. Does not mistake a legitimate grazing contact for a
     * membrane passing through a physical obstruction. */
    static boolean crossesInterior(float ax,float ay,float bx,float by,
                                   float l,float t,float r,float b){
        float dx=bx-ax,dy=by-ay;
        float enter=0,leave=1;
        if(Math.abs(dx)<.000001f){
            if(ax<=l+.02f||ax>=r-.02f)return false;
        }else{
            float v0=(l+.02f-ax)/dx,v1=(r-.02f-ax)/dx;
            enter=Math.max(enter,Math.min(v0,v1));
            leave=Math.min(leave,Math.max(v0,v1));
        }
        if(Math.abs(dy)<.000001f){
            if(ay<=t+.02f||ay>=b-.02f)return false;
        }else{
            float v0=(t+.02f-ay)/dy,v1=(b-.02f-ay)/dy;
            enter=Math.max(enter,Math.min(v0,v1));
            leave=Math.min(leave,Math.max(v0,v1));
        }
        return leave>enter+.00001f&&leave>=0&&enter<=1;
    }
    /** Repair one impossible crossing by keeping the entire connected edge
     * outside the closest face. This works for ANY neighboring pair of the
     * 96 masses; there are no preset arms, slots, or special joint indices. */
    private static boolean correctLink(Morphology.Surface skin,int a,int b,
                                       float l,float t,float r,float bottom){
        float ax=skin.x[a],ay=skin.y[a],bx=skin.x[b],by=skin.y[b];
        if(!crossesInterior(ax,ay,bx,by,l,t,r,bottom))return false;
        float clearance=1.6f;
        float left=l-clearance,right=r+clearance;
        float top=t-clearance,low=bottom+clearance;
        float costLeft=Math.max(0,ax-left)+Math.max(0,bx-left);
        float costRight=Math.max(0,right-ax)+Math.max(0,right-bx);
        float costTop=Math.max(0,ay-top)+Math.max(0,by-top);
        float costBottom=Math.max(0,low-ay)+Math.max(0,low-by);
        int face=0;float best=costLeft;
        if(costRight<best){face=1;best=costRight;}
        if(costTop<best){face=2;best=costTop;}
        if(costBottom<best){face=3;}
        for(int i=0;i<2;i++){
            int idx=i==0?a:b;
            if(face==0&&skin.x[idx]>left){
                skin.x[idx]=left;skin.vx[idx]=Math.min(0,skin.vx[idx]);
            }else if(face==1&&skin.x[idx]<right){
                skin.x[idx]=right;skin.vx[idx]=Math.max(0,skin.vx[idx]);
            }else if(face==2&&skin.y[idx]>top){
                skin.y[idx]=top;skin.vy[idx]=Math.min(0,skin.vy[idx]);
            }else if(face==3&&skin.y[idx]<low){
                skin.y[idx]=low;skin.vy[idx]=Math.max(0,skin.vy[idx]);
            }
        }
        return true;
    }
    public void resolveSolidLinks(Morphology.Surface skin){
        // Neighboring membrane particles are bonded: fixing a collision
        // cannot leave a huge straight bridge across an entire wall.
        // Resolve both springs and solid geometry iteratively.
        for(int pass=0;pass<7;pass++){
            int repairs=0;
            for(int i=0;i<Morphology.POINTS;i++){
                int j=(i+1)%Morphology.POINTS;
                float dx=skin.x[j]-skin.x[i],dy=skin.y[j]-skin.y[i];
                float distance=(float)Math.hypot(dx,dy);
                if(distance>MAX_LINK_SPAN&&Float.isFinite(distance)){
                    float shrink=(distance-MAX_LINK_SPAN)*.5f/distance;
                    float sx=dx*shrink,sy=dy*shrink;
                    skin.x[i]+=sx;skin.y[i]+=sy;
                    skin.x[j]-=sx;skin.y[j]-=sy;
                    skin.vx[i]*=.75f;skin.vy[i]*=.75f;
                    skin.vx[j]*=.75f;skin.vy[j]*=.75f;
                    repairs++;
                }
            }
            for(int i=0;i<Morphology.POINTS;i++){
                int j=(i+1)%Morphology.POINTS;
                for(int w=0;w<5;w++){
                    boundaries(w,temp);
                    if(correctLink(skin,i,j,temp[0],temp[1],temp[2],temp[3]))
                        repairs++;
                }
                if(correctLink(skin,i,j,gateX()-DOOR_HALF_W,gateTop(),
                   gateX()+DOOR_HALF_W,gateBottom()))repairs++;
            }
            solidLinkCorrections+=repairs;
            if(repairs==0)break;
        }
    }
    /** Swept collision: stop a moving mass at the first face it reaches.
     * Discrete nearest-face projection alone can eject a fast particle on the
     * wrong side of a wall and falsely make a pseudopod pass through it. */
    private boolean sweptProject(float[] px,float[] py,float[] vx,float[] vy,
                                 int i,float l,float t,float r,float b,
                                 float radius,float dt){
        if(dt<=0)return project(px,py,vx,vy,i,l,t,r,b,radius);
        float x=px[i],y=py[i],ox=x-vx[i]*dt,oy=y-vy[i]*dt;
        float left=l-radius,right=r+radius,top=t-radius,bottom=b+radius;
        if(ox<=left&&x>left&&vx[i]>0){
            float frac=(left-ox)/(x-ox);
            float hitY=oy+(y-oy)*frac;
            if(hitY>=top&&hitY<=bottom){
                px[i]=left;py[i]=hitY;vx[i]=0;return true;
            }
        }
        if(ox>=right&&x<right&&vx[i]<0){
            float frac=(right-ox)/(x-ox);
            float hitY=oy+(y-oy)*frac;
            if(hitY>=top&&hitY<=bottom){
                px[i]=right;py[i]=hitY;vx[i]=0;return true;
            }
        }
        if(oy<=top&&y>top&&vy[i]>0){
            float frac=(top-oy)/(y-oy);
            float hitX=ox+(x-ox)*frac;
            if(hitX>=left&&hitX<=right){
                py[i]=top;px[i]=hitX;vy[i]=0;return true;
            }
        }
        if(oy>=bottom&&y<bottom&&vy[i]<0){
            float frac=(bottom-oy)/(y-oy);
            float hitX=ox+(x-ox)*frac;
            if(hitX>=left&&hitX<=right){
                py[i]=bottom;px[i]=hitX;vy[i]=0;return true;
            }
        }
        return project(px,py,vx,vy,i,l,t,r,b,radius);
    }

    /** Wall slabs: entrance outside the opening, far wall, ceiling, floor. */
    private void boundaries(int id,float[] r){
        float l=entranceX(),right=ox+END_X;
        float t=oy+TOP,b=oy+BOTTOM;
        float half=WALL_HALF_THICKNESS;
        switch(id){
            case 0:fill(r,l-half,t-10,l+half,oy-OPEN_HALF_HEIGHT);break;
            case 1:fill(r,l-half,oy+OPEN_HALF_HEIGHT,l+half,b+10);break;
            case 2:fill(r,l-half,t-10,right+half,t+half);break;
            case 3:fill(r,l-half,b-half,right+half,b+10);break;
            case 4:fill(r,right-half,t-10,right+half,b+10);break;
            default:throw new IllegalArgumentException("Invalid wall");
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
    private void applyToCore(MotorBrain.Body b,float l,float t,float r,float bot,float radius,float dt){
        float[] px={b.cx},py={b.cy},vx={b.cvx},vy={b.cvy};
        if(sweptProject(px,py,vx,vy,0,l,t,r,bot,radius,dt)){
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
        // Trial starts OUTSIDE the new freestanding gate, like the phone.
        ScarcityLab lab=new ScarcityLab(0,offsetY);
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
        ScarcityLab chamber=new ScarcityLab(-115,0);
        // The 10-unit channel accommodates a thin pseudopod, but NOT the
        // 20-unit-diameter core: opening cannot simply be walked around.
        float corridor=chamber.entranceX()-WALL_HALF_THICKNESS-
                (chamber.gateX()+DOOR_HALF_W);
        if(corridor>=20f||corridor<=3f)
            throw new IllegalStateException("Wall-gate gap allows full-body bypass");
        if(!(chamber.gateX()+DOOR_HALF_W<chamber.entranceX()-WALL_HALF_THICKNESS-5))
            throw new IllegalStateException("Gate must be IN FRONT of wall, not embedded");
        if(chamber.gateTop()>=-OPEN_HALF_HEIGHT||chamber.gateBottom()<=OPEN_HALF_HEIGHT)
            throw new IllegalStateException("Long gate does not cover opening");
        for(int i=0;i<Morphology.POINTS;i++){
            membrane.x[i]=-220;membrane.y[i]=-180;
            membrane.vx[i]=membrane.vy[i]=0;
        }
        membrane.x[0]=chamber.gateX()-DOOR_HALF_W-1;
        membrane.y[0]=0;membrane.vy[0]=65;
        for(int i=0;i<22;i++)chamber.solveGate(membrane,MotorBrain.STEP);
        if(chamber.maxDoorTravel>0.0001f||chamber.grasps!=0)
            throw new IllegalStateException("Unilateral push bypassed gate");
        int opposite=Morphology.POINTS/2;
        membrane.x[opposite]=chamber.gateX()+DOOR_HALF_W+1;
        membrane.y[opposite]=0;membrane.vy[opposite]=65;
        for(int i=0;i<130;i++){
            membrane.y[0]=chamber.gateCenterY();
            membrane.y[opposite]=chamber.gateCenterY();
            chamber.solveGate(membrane,MotorBrain.STEP);
        }
        if(chamber.grasps<1||chamber.maxDoorTravel<15)
            throw new IllegalStateException("Genuine opposed contact cannot slide freestanding gate");
        ScarcityLab fixtureLab=new ScarcityLab(-115,0);
        Morphology.Surface fixture=new Morphology.Surface(new MotorBrain.Body());
        for(int i=0;i<Morphology.POINTS;i++){
            fixture.x[i]=-220;fixture.y[i]=-180;
            fixture.vx[i]=fixture.vy[i]=0;
        }
        // Through-gate edge: both end particles outside the rectangle
        float gate=fixtureLab.gateX();
        fixture.x[0]=gate-DOOR_HALF_W-4;fixture.y[0]=0;
        fixture.x[1]=gate+DOOR_HALF_W+4;fixture.y[1]=0;
        fixtureLab.resolveSolidLinks(fixture);
        for(int i=0;i<Morphology.POINTS;i++){
            int j=(i+1)%Morphology.POINTS;
            if(crossesInterior(fixture.x[i],fixture.y[i],fixture.x[j],fixture.y[j],
                gate-DOOR_HALF_W,fixtureLab.gateTop(),
                gate+DOOR_HALF_W,fixtureLab.gateBottom()))
                throw new IllegalStateException("Solid door still crossed by membrane edge");
        }
        // Through-WALL edge (previous regression only tested the gate)
        for(int i=0;i<Morphology.POINTS;i++){
            fixture.x[i]=-220;fixture.y[i]=-180;
            fixture.vx[i]=fixture.vy[i]=0;
        }
        fixture.x[0]=-26;fixture.y[0]=76;fixture.x[1]=26;fixture.y[1]=76;
        fixtureLab.resolveSolidLinks(fixture);
        for(int i=0;i<Morphology.POINTS;i++){
            int j=(i+1)%Morphology.POINTS;
            for(int w=0;w<5;w++){
                fixtureLab.boundaries(w,fixtureLab.temp);
                if(crossesInterior(fixture.x[i],fixture.y[i],fixture.x[j],fixture.y[j],
                    fixtureLab.temp[0],fixtureLab.temp[1],
                    fixtureLab.temp[2],fixtureLab.temp[3]))
                    throw new IllegalStateException("Membrane crosses solid WALL");
            }
        }
        // High-speed particles and core must stop at first contact.
        float[] px={9},py={74},vx={900},vy={0};
        fixtureLab.sweptProject(px,py,vx,vy,0,-10,43,10,115,2,.035f);
        if(px[0]>-11.9f)throw new IllegalStateException("Fast wall tunneling");
        MotorBrain.Body core=new MotorBrain.Body();
        core.cx=5;core.cy=74;core.cvx=900;
        fixtureLab.applyToCore(core,-10,43,10,115,9,.035f);
        if(core.cx>-18.9f)throw new IllegalStateException("Core passed solid wall");
        if(crossesInterior(gate-18,-DOOR_HALF_H-6,
              gate,-DOOR_HALF_H-6,gate-DOOR_HALF_W,
              -DOOR_HALF_H,gate+DOOR_HALF_W,DOOR_HALF_H))
            throw new IllegalStateException("Legitimate route around door blocked");
        UnknownWorld.Entity finite=new UnknownWorld.Entity(0,0,0,0);
        if(finite.touch(0,0)<=0||finite.ready())
            throw new IllegalStateException("Consumed energy revived");
        float[] motor=MotorBrain.virgin(new java.util.Random(918273));
        float[] hand=ContactLearning.virgin();
        float[] shape=Morphology.virgin(new java.util.Random(14790));
        if(!Float.isFinite(trainingFitness(motor,hand,shape)))
            throw new IllegalStateException("Physical challenge simulation NaN");
        System.out.printf(Locale.US,
             "ZERO-008.2 physics passed: frontDoorGap=%.1f doorHeight=%.0f openingHeight=%.0f unilateral=0 bilateralTravel=%.2f wallEdgesSolid=true coreSweep=true%n",
             chamber.entranceX()-WALL_HALF_THICKNESS-(chamber.gateX()+DOOR_HALF_W),
             2*DOOR_HALF_H,2*OPEN_HALF_HEIGHT,chamber.maxDoorTravel);
    }
}
