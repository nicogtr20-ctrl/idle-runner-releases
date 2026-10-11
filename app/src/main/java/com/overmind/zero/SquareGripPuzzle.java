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
    private static boolean sufficientlySeparate(int i,int j){
        if(i<0||j<0)return false;
        int d=Math.abs(i-j);
        return Math.min(d,Morphology.POINTS-d)>=Morphology.POINTS/10;
    }
    public static Outcome constrain(MotorBrain.Body body,Morphology.Surface skin,
                                    UnknownWorld.Entity box,float dt){
        if(body==null||skin==null||box==null||!box.isBox()||
           !Float.isFinite(dt)||dt<=0)return new Outcome(false,0,0);
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
                // Do not retain an adhesive hold after leaving the square.
                // The gate runs before this solver when one is present, and
                // square placements cannot overlap it.
                if(skin.touchSense[i]<.5f)skin.gripHold[i]=0;
            }
            pushOut(skin.x,skin.y,skin.vx,skin.vy,i,box.x,box.y,1.15f);
        }
        boolean opposing=sufficientlySeparate(sides[0],sides[1])||
                         sufficientlySeparate(sides[2],sides[3]);
        if(opposing&&held>=2){
            float desiredX=clamp(sumVx/held,-MAX_SPEED,MAX_SPEED);
            float desiredY=clamp(sumVy/held,-MAX_SPEED,MAX_SPEED);
            float alpha=Math.min(1,dt*5f);
            box.vx=clamp(box.vx+(desiredX-box.vx)*alpha,-MAX_SPEED,MAX_SPEED);
            box.vy=clamp(box.vy+(desiredY-box.vy)*alpha,-MAX_SPEED,MAX_SPEED);
            box.everCollided=true; // confirmed *bilateral physical grip*
        }
        // Collision against the core and each of the ten real body masses;
        // simple pressure corrects penetration but NEVER drives box velocity.
        for(int i=0;i<MotorBrain.N;i++)
            pushOut(body.x,body.y,body.vx,body.vy,i,box.x,box.y,1.5f);
        float dx=body.cx-box.x,dy=body.cy-box.y;
        if(Math.abs(dx)<HALF+6&&Math.abs(dy)<HALF+6){
            float m=Math.min(HALF+6-Math.abs(dx),HALF+6-Math.abs(dy));
            if(HALF+6-Math.abs(dx)<=HALF+6-Math.abs(dy)){
                if(dx>=0){body.cx+=m;body.cvx=Math.max(0,body.cvx);}
                else {body.cx-=m;body.cvx=Math.min(0,body.cvx);}
            }else{
                if(dy>=0){body.cy+=m;body.cvy=Math.max(0,body.cvy);}
                else {body.cy-=m;body.cvy=Math.min(0,body.cvy);}
            }
        }
        return new Outcome(opposing,contacts,held);
    }
    public static boolean uncovered(UnknownWorld.Entity box){
        return box!=null&&box.isBox()&&box.everCollided&&
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
        // Genuine bilateral adhesion and movement along both x and y.
        for(int j=0;j<85;j++){
            int left=4,right=Morphology.POINTS/2+4;
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