package com.overmind.zero;

/**
 * True two-sided adhesion on a small 40x40 rigid square.
 *
 * The box has NO motor commands and is never moved by the reward system,
 * normal pushing, touch alone, or hidden object identity. It accelerates
 * ONLY when two DISTINCT deformable membrane masses hold opposite faces.
 * Unlike the gate, both physical velocity components are allowed.
 */
public final class SquareGripPuzzle {
    private SquareGripPuzzle(){}
    public static final float HALF=20f;
    public static final float DIAGONAL=28.284271f;
    public static final float UNCOVER_DISTANCE=55f;
    private static final float FACE_MARGIN=4f;
    private static final float MAX_SPEED=78f;
    private static float clamp(float v,float lo,float hi){
        return Math.min(hi,Math.max(lo,v));
    }
    public static final class Outcome{
        public final boolean opposingGrip;
        public final int contacts,held;
        Outcome(boolean two,int c,int h){opposingGrip=two;contacts=c;held=h;}
    }
    private static int closestFace(float dx,float dy){
        float left=Math.abs(dx+HALF),right=Math.abs(dx-HALF),
              top=Math.abs(dy+HALF),bottom=Math.abs(dy-HALF);
        float best=left;int face=0;
        if(right<best){best=right;face=1;}
        if(top<best){best=top;face=2;}
        if(bottom<best){face=3;}
        return face;
    }
    private static void pushOut(float[] x,float[] y,float[] vx,float[] vy,
                                int index,float cx,float cy,float clearance){
        float dx=x[index]-cx,dy=y[index]-cy;
        if(Math.abs(dx)>HALF+clearance||Math.abs(dy)>HALF+clearance)return;
        if(Math.abs(dx)>HALF||Math.abs(dy)>HALF)return; // outside solid
        switch(closestFace(dx,dy)){
            case 0: x[index]=cx-HALF-clearance;vx[index]=Math.min(0,vx[index]);break;
            case 1: x[index]=cx+HALF+clearance;vx[index]=Math.max(0,vx[index]);break;
            case 2: y[index]=cy-HALF-clearance;vy[index]=Math.min(0,vy[index]);break;
            default:y[index]=cy+HALF+clearance;vy[index]=Math.max(0,vy[index]);break;
        }
    }
    /** Non-penetrating membrane edges: opposite contacts count only when
     * the elastic perimeter does NOT cut through the solid square. */
    private static void resolveCrossingLinks(Morphology.Surface skin,
                                             UnknownWorld.Entity box){
        float l=box.x-HALF,r=box.x+HALF,t=box.y-HALF,b=box.y+HALF;
        for(int i=0;i<Morphology.POINTS;i++){
            int j=(i+1)%Morphology.POINTS;
            if(!ScarcityLab.crossesInterior(skin.x[i],skin.y[i],
                                            skin.x[j],skin.y[j],l,t,r,b))
                continue;
            float costLeft=Math.max(0,skin.x[i]-(l-1.5f))+
                           Math.max(0,skin.x[j]-(l-1.5f));
            float costRight=Math.max(0,(r+1.5f)-skin.x[i])+
                            Math.max(0,(r+1.5f)-skin.x[j]);
            float costTop=Math.max(0,skin.y[i]-(t-1.5f))+
                          Math.max(0,skin.y[j]-(t-1.5f));
            float costBottom=Math.max(0,(b+1.5f)-skin.y[i])+
                             Math.max(0,(b+1.5f)-skin.y[j]);
            int side=0;float cost=costLeft;
            if(costRight<cost){cost=costRight;side=1;}
            if(costTop<cost){cost=costTop;side=2;}
            if(costBottom<cost){side=3;}
            for(int n=0;n<2;n++){
                int k=n==0?i:j;
                if(side==0){skin.x[k]=l-1.5f;skin.vx[k]=Math.min(0,skin.vx[k]);}
                else if(side==1){skin.x[k]=r+1.5f;skin.vx[k]=Math.max(0,skin.vx[k]);}
                else if(side==2){skin.y[k]=t-1.5f;skin.vy[k]=Math.min(0,skin.vy[k]);}
                else {skin.y[k]=b+1.5f;skin.vy[k]=Math.max(0,skin.vy[k]);}
                skin.gripHold[k]=0; // impossible through-solid grip never counts
            }
        }
    }
    private static boolean sufficientlySeparate(int i,int j){
        if(i<0||j<0)return false;
        int d=Math.abs(i-j);
        return Math.min(d,Morphology.POINTS-d)>=Morphology.POINTS/10;
    }
    /**
     * A previously held, voluntary adhesive contact is a short physical
     * tether. It preserves a legal face hold through small outward wobbles,
     * never creates a first hold, crosses solid walls or pulls a one-sided box.
     */
    static boolean stabilizeExistingGrip(Morphology.Surface skin,
                                          UnknownWorld.Entity box,int i,
                                          float oldX,float oldY){
        if(skin.gripHold[i]<Morphology.GRIP_HOLD||
           skin.gripCommand[i]<=Morphology.GRIP_THRESHOLD||
           !Float.isFinite(oldX)||!Float.isFinite(oldY)||
           Math.hypot(skin.x[i]-oldX,skin.y[i]-oldY)>9f)return false;
        float ox=oldX-box.x,oy=oldY-box.y;
        float cx=skin.x[i]-box.x,cy=skin.y[i]-box.y;
        // Same former face and within a bounded stretch, no edge wrapping.
        if(Math.abs(oy)<=HALF&&Math.abs(ox+HALF)<=3f&&
           cx<=-HALF+1f&&cx>=-HALF-8f&&Math.abs(cy)<=HALF){
            skin.x[i]=box.x-HALF;skin.vx[i]=box.vx;return true;
        }
        if(Math.abs(oy)<=HALF&&Math.abs(ox-HALF)<=3f&&
           cx>=HALF-1f&&cx<=HALF+8f&&Math.abs(cy)<=HALF){
            skin.x[i]=box.x+HALF;skin.vx[i]=box.vx;return true;
        }
        if(Math.abs(ox)<=HALF&&Math.abs(oy+HALF)<=3f&&
           cy<=-HALF+1f&&cy>=-HALF-8f&&Math.abs(cx)<=HALF){
            skin.y[i]=box.y-HALF;skin.vy[i]=box.vy;return true;
        }
        if(Math.abs(ox)<=HALF&&Math.abs(oy-HALF)<=3f&&
           cy>=HALF-1f&&cy<=HALF+8f&&Math.abs(cx)<=HALF){
            skin.y[i]=box.y+HALF;skin.vy[i]=box.vy;return true;
        }
        return false;
    }
    public static Outcome constrain(MotorBrain.Body body,Morphology.Surface skin,
                                    UnknownWorld.Entity box,float dt){
        return constrain(body,skin,box,dt,null,null,null,null,
                         body==null?0f:body.cx,body==null?0f:body.cy);
    }
    /** The original bilateral-grip policy is unchanged: extra arguments
     * are only the prior physical positions for continuous collisions. */
    public static Outcome constrain(MotorBrain.Body body,Morphology.Surface skin,
                                    UnknownWorld.Entity box,float dt,
                                    float[] previousSkinX,float[] previousSkinY,
                                    float[] previousBodyX,float[] previousBodyY,
                                    float previousCoreX,float previousCoreY){
        if(body==null||skin==null||box==null||!box.isBilateralBox()||
           !Float.isFinite(dt)||dt<=0)return new Outcome(false,0,0);
        // The scratch surface is shared, but this timer belongs ONLY to box.
        System.arraycopy(box.boxGripHold,0,skin.gripHold,0,Morphology.POINTS);
        SquareCollisionSolver.constrain(body,skin,box,previousSkinX,
            previousSkinY,previousBodyX,previousBodyY,
            previousCoreX,previousCoreY);
        resolveCrossingLinks(skin,box);
        if(previousSkinX!=null&&previousSkinY!=null)
            for(int i=0;i<Morphology.POINTS;i++)
                stabilizeExistingGrip(skin,box,i,previousSkinX[i],previousSkinY[i]);
        int[] sides={-1,-1,-1,-1};
        float sumVx=0,sumVy=0;int held=0,contacts=0;
        for(int i=0;i<Morphology.POINTS;i++){
            float dx=skin.x[i]-box.x,dy=skin.y[i]-box.y;
            boolean xFace=Math.abs(Math.abs(dx)-HALF)<=FACE_MARGIN&&Math.abs(dy)<=HALF+FACE_MARGIN;
            boolean yFace=Math.abs(Math.abs(dy)-HALF)<=FACE_MARGIN&&Math.abs(dx)<=HALF+FACE_MARGIN;
            if(xFace||yFace){
                int face=closestFace(dx,dy);
                skin.touchSense[i]=1f;
                skin.touchPulse[i]=Math.max(skin.touchPulse[i],.6f);
                contacts++;
                if(skin.gripCommand[i]>Morphology.GRIP_THRESHOLD){
                    skin.gripHold[i]=Math.min(.5f,skin.gripHold[i]+dt);
                    if(skin.gripHold[i]>=Morphology.GRIP_HOLD){
                        sides[face]=i;
                        sumVx+=skin.vx[i];sumVy+=skin.vy[i];held++;
                    }
                }else skin.gripHold[i]=0;
            }else{
                // Holding another box or the gate cannot retain THIS box's
                // timer when its own face contact is no longer present.
                skin.gripHold[i]=0f;
            }
            pushOut(skin.x,skin.y,skin.vx,skin.vy,i,box.x,box.y,1.15f);
        }
        boolean opposing=sufficientlySeparate(sides[0],sides[1])||
                         sufficientlySeparate(sides[2],sides[3]);
        // Collision solvers may reposition a cover ONLY when its own
        // physical opposite-side grip was confirmed. Past everCollided
        // history and a single red point never authorize displacement.
        box.bilateralHeldNow=opposing&&held>=2;
        if(box.bilateralHeldNow){
            float desiredX=clamp(sumVx/held,-MAX_SPEED,MAX_SPEED);
            float desiredY=clamp(sumVy/held,-MAX_SPEED,MAX_SPEED);
            float alpha=Math.min(1,dt*5f);
            box.vx=clamp(box.vx+(desiredX-box.vx)*alpha,-MAX_SPEED,MAX_SPEED);
            box.vy=clamp(box.vy+(desiredY-box.vy)*alpha,-MAX_SPEED,MAX_SPEED);
            box.everCollided=true; // confirmed *bilateral physical grip*
        }
        // Only ten real living tissue masses collide, not the logical centroid.
        // Simple pressure NEVER drives box velocity.
        for(int i=0;i<MotorBrain.N;i++)
            pushOut(body.x,body.y,body.vx,body.vy,i,box.x,box.y,1.5f);
        MaterialGeometry.observe(body);
        // Revalidate the perimeter AFTER ordinary contact/pressure/traction.
        // Those corrections can otherwise introduce new through-wall chords.
        SquareCollisionSolver.finish(body,skin,box);
        System.arraycopy(skin.gripHold,0,box.boxGripHold,0,Morphology.POINTS);
        return new Outcome(opposing,contacts,held);
    }
    public static boolean uncovered(UnknownWorld.Entity box){
        return box!=null&&box.isBilateralBox()&&box.everCollided&&
               box.displacementFromSpawn()>=UNCOVER_DISTANCE;
    }
    /** Reward source must be pre-existing, invisible and same exact anchor.
     * Never creates free energy on its own, or on a mere one-sided collision. */
    public static boolean uncover(UnknownWorld.Entity box,
                                  java.util.List<UnknownWorld.Entity> objects){
        if(!uncovered(box)||box.countedAsMoved)return false;
        for(UnknownWorld.Entity e:objects){
            if(e.concealed&&e.physicalLaw()==4&&
               e.spawnX==box.spawnX&&e.spawnY==box.spawnY){
                e.concealed=false;
                box.countedAsMoved=true;
                return true;
            }
        }
        return false;
    }
    private static void require(boolean b,String error){
        if(!b)throw new IllegalStateException(error);
    }
    public static void main(String[] args){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        UnknownWorld.Entity box=new UnknownWorld.Entity(110,0,2,5);
        UnknownWorld.Entity reward=new UnknownWorld.Entity(110,0,0,4);
        reward.concealed=true;
        java.util.ArrayList<UnknownWorld.Entity> world=new java.util.ArrayList<>();
        world.add(box);world.add(reward);
        for(int j=0;j<35;j++){
            int left=4,right=Morphology.POINTS/2+4;
            s.x[left]=box.x-HALF;s.y[left]=box.y;
            s.x[right]=box.x+HALF;s.y[right]=box.y;
            s.vx[left]=s.vx[right]=65;
            s.gripCommand[left]=s.gripCommand[right]=0f;
            constrain(b,s,box,.035f);box.tick(.035f);
        }
        require(box.displacementFromSpawn()<.01f&&!box.everCollided,
                "Touch/pressure moved a square with no grip");
        require(!uncover(box,world)&&reward.concealed,
                "Mere touch uncovered hidden energy");
        // One-sided adhesion must not move even after a sustained hold.
        for(int j=0;j<25;j++){
            int left=4,right=Morphology.POINTS/2+4;
            s.x[left]=box.x-HALF;s.y[left]=box.y;
            s.x[right]=box.x+HALF;s.y[right]=box.y;
            s.vx[left]=s.vx[right]=65;
            s.gripCommand[left]=1;s.gripCommand[right]=0;
            constrain(b,s,box,.035f);box.tick(.035f);
        }
        require(box.displacementFromSpawn()<.01f&&!box.everCollided,
                "One-sided adhesion moved square");
        // A legal perimeter surrounds the block without traversing its
        // interior; two genuinely separated points now grip opposite faces.
        // Genuine bilateral adhesion and movement along both x and y.
        for(int j=0;j<85;j++){
            for(int i=0;i<Morphology.POINTS;i++){
                double angle=Math.PI*2*i/Morphology.POINTS;
                s.x[i]=box.x+36f*(float)Math.cos(angle);
                s.y[i]=box.y+36f*(float)Math.sin(angle);
            }
            int left=Morphology.POINTS/2,right=0;
            s.x[left]=box.x-HALF;s.y[left]=box.y;
            s.x[right]=box.x+HALF;s.y[right]=box.y;
            s.vx[left]=s.vx[right]=75;s.vy[left]=s.vy[right]=12;
            s.gripCommand[left]=s.gripCommand[right]=1;
            constrain(b,s,box,.035f);box.tick(.035f);
        }
        require(box.displacementFromSpawn()>UNCOVER_DISTANCE&&box.everCollided,
                "Two-sided real grip cannot displace free square");
        require(uncover(box,world)&&!reward.concealed,
                "Physically moving square did not reveal rare finite energy");
        require(!uncover(box,world)&&reward.touch(0,0)==40f&&
                reward.touch(0,0)==0f,
                "Hidden energy paid twice or free on movement");
        System.out.println("SquareGripPuzzle PASS: static collision, bilateral grip, multi-axis motion, one-time 40-energy reveal");
    }
}