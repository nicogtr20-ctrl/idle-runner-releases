package com.overmind.zero;
import java.util.*;

/**
 * 0117.1: THREE boxes on ONE real 96-point surface, with the gate solver
 * running in the same frame. The fixtures supply actuator commands and
 * velocities; no code commands or trains the real neural controller.
 */
public final class Zero3SelectiveMultiObjectV337Regression {
    private static final float DT=.035f;
    private static final int[] TOP={7,19,32};
    private static final int[] BOTTOM={80,68,55};
    private static final float[] XX={-130f,0f,130f};
    private static void ok(boolean yes,String why){
        if(!yes)throw new AssertionError(why);
    }
    private static final class World {
        final MotorBrain.Body body=new MotorBrain.Body();
        final Morphology.Surface skin=new Morphology.Surface(body);
        final ScarcityLab gate=new ScarcityLab(0,0);
        final UnknownWorld.Entity[] box=new UnknownWorld.Entity[3];
        final ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        World(){
            for(int k=0;k<3;k++){
                box[k]=new UnknownWorld.Entity(XX[k],300f,2,5);
                objects.add(box[k]);
            }
        }
        void frame(boolean[] grip,int[] order,boolean withGate){
            // One continuous rectangular skin perimeter around all 3 solids.
            // Different membrane particles land on opposite real faces.
            for(int i=0;i<Morphology.POINTS;i++){
                float x,y;
                if(i<40){x=-200f+400f*i/39f;y=250f;}
                else if(i<48){x=200f;y=250f+100f*(i-39)/9f;}
                else if(i<88){x=200f-400f*(i-48)/39f;y=350f;}
                else{x=-200f;y=350f-100f*(i-87)/9f;}
                skin.x[i]=x;skin.y[i]=y;
                skin.vx[i]=skin.vy[i]=skin.gripCommand[i]=0f;
            }
            // Taper the contact indentations so every real membrane link
            // remains shorter than the unchanged 23-unit material limit.
            for(int k=0;k<3;k++)for(int d=-2;d<=2;d++){
                skin.y[TOP[k]+d]=250f+10f*(3-Math.abs(d));
                skin.y[BOTTOM[k]+d]=350f-10f*(3-Math.abs(d));
            }
            for(int k=0;k<3;k++){
                skin.x[TOP[k]]=skin.x[BOTTOM[k]]=box[k].x;
                skin.y[TOP[k]]=box[k].y-20f;
                skin.y[BOTTOM[k]]=box[k].y+20f;
                if(grip[k]){
                    skin.gripCommand[TOP[k]]=skin.gripCommand[BOTTOM[k]]=.82f;
                    float velocity=k==1?-55f:55f;
                    skin.vx[TOP[k]]=skin.vx[BOTTOM[k]]=velocity;
                }
            }
            float[] oldX=skin.x.clone(),oldY=skin.y.clone();
            float[] oldBX=body.x.clone(),oldBY=body.y.clone();
            float oldCX=body.cx,oldCY=body.cy;
            if(withGate)gate.constrain(body,skin,DT,.82f);
            for(int k:order){
                SquareGripPuzzle.constrain(body,skin,box[k],DT,
                    oldX,oldY,oldBX,oldBY,oldCX,oldCY,
                    null,false,true,true);
            }
            GripSurfaceComposition.compose(skin,withGate?gate:null,objects);
            for(UnknownWorld.Entity x:box)x.tick(DT);
            for(UnknownWorld.Entity x:box)
                ok(SquareCollisionSolver.intersections(skin,x)==0,
                    "Invalid physical perimeter crossing at "+x.x);
        }
    }
    private static final int[] ORDER={0,1,2},REVERSE={2,1,0};
    private static World scenario(boolean[] commanded,int[] order,boolean gate){
        World w=new World();
        for(int f=0;f<18;f++)w.frame(commanded,order,gate);
        for(int i=0;i<3;i++){
            float moved=w.box[i].displacementFromSpawn();
            if(commanded[i]){
                ok(w.box[i].boxGripHold[TOP[i]]>=Morphology.GRIP_HOLD&&
                   w.box[i].boxGripHold[BOTTOM[i]]>=Morphology.GRIP_HOLD,
                   "Box "+i+" failed holds: top="+w.box[i].boxGripHold[TOP[i]]+
                   " bottom="+w.box[i].boxGripHold[BOTTOM[i]]+
                   " x="+w.skin.x[TOP[i]]+" y="+w.skin.y[TOP[i]]+
                   " boxX="+w.box[i].x+" boxY="+w.box[i].y+
                   " moved="+moved);
                ok(moved>0.4f&&w.box[i].everCollided,
                   "Gripped box "+i+" was not moved by its own patch velocities");
                ok(w.skin.gripHold[TOP[i]]>=Morphology.GRIP_HOLD&&
                   w.skin.gripHold[BOTTOM[i]]>=Morphology.GRIP_HOLD,
                   "Orange surface failed to compose valid box "+i+" hold");
            }else{
                ok(moved==0f&&!w.box[i].everCollided,
                   "Uncommanded box "+i+" moved or inherited adhesion");
                ok(w.box[i].boxGripHold[TOP[i]]==0f,
                   "Uncommanded box "+i+" inherited a different box timer");
            }
        }
        return w;
    }
    private static void compareOrder(boolean[] mask){
        World f=scenario(mask,ORDER,true),r=scenario(mask,REVERSE,true);
        for(int k=0;k<3;k++){
            ok(Math.abs(f.box[k].x-r.box[k].x)<.0001f&&
               Math.abs(f.box[k].y-r.box[k].y)<.0001f,
               "Box movement depends on physics iteration order: "+k);
            for(int i=0;i<Morphology.POINTS;i++)
                ok(Math.abs(f.box[k].boxGripHold[i]-r.box[k].boxGripHold[i])<.0001f,
                   "Timer depends on box iteration order: "+k+"/"+i);
        }
    }
    private static void gateIndependence(){
        World w=new World();
        int left=4,right=52;
        for(int f=0;f<8;f++){
            for(int i=0;i<Morphology.POINTS;i++){
                w.skin.x[i]=w.gate.gateX()-90f;w.skin.y[i]=-100f;
                w.skin.gripCommand[i]=0f;
            }
            w.skin.x[left]=w.gate.gateX()-ScarcityLab.DOOR_HALF_W;
            w.skin.x[right]=w.gate.gateX()+ScarcityLab.DOOR_HALF_W;
            w.skin.y[left]=w.skin.y[right]=w.gate.gateCenterY();
            w.skin.gripCommand[left]=w.skin.gripCommand[right]=.82f;
            w.gate.solveGate(w.skin,DT,.82f);
        }
        ok(w.gate.gateGripHold[left]>=Morphology.GRIP_HOLD&&
           w.gate.gateGripHold[right]>=Morphology.GRIP_HOLD,
           "Gate must independently sustain actual bilateral hold");
        // Gate release must not impact established grips on three boxes.
        w.box[0].boxGripHold[TOP[0]]=.24f;
        w.box[1].boxGripHold[TOP[1]]=.30f;
        w.box[2].boxGripHold[TOP[2]]=.41f;
        w.skin.x[left]=w.gate.gateX()-90f;
        w.gate.solveGate(w.skin,DT,.82f);
        ok(w.gate.gateGripHold[left]==0f,"Gate did not release own lost contact");
        ok(w.box[0].boxGripHold[TOP[0]]==.24f&&
           w.box[1].boxGripHold[TOP[1]]==.30f&&
           w.box[2].boxGripHold[TOP[2]]==.41f,
           "Releasing gate wrongly released any box");
        // No box processing may ever change gate-owned timers.
        float gateHold=w.gate.gateGripHold[right];
        w.frame(new boolean[]{true,true,true},ORDER,false);
        ok(w.gate.gateGripHold[right]==gateHold,
            "A different box changed the gate's grip timer");
    }
    private static void selectiveRelease(){
        World w=new World();
        boolean[] mask={true,true,true};
        for(int i=0;i<12;i++)w.frame(mask,ORDER,true);
        mask[1]=false;
        for(int i=0;i<5;i++)w.frame(mask,ORDER,true);
        ok(w.box[1].boxGripHold[TOP[1]]==0f,
            "Disabled middle box kept adhesive hold");
        ok(w.box[0].boxGripHold[TOP[0]]>=Morphology.GRIP_HOLD&&
           w.box[2].boxGripHold[TOP[2]]>=Morphology.GRIP_HOLD,
           "Releasing B also released A or C");
    }
    public static void main(String[] args){
        for(int mask=1;mask<8;mask++){
            boolean[] commands={(mask&1)!=0,(mask&2)!=0,(mask&4)!=0};
            compareOrder(commands);
        }
        selectiveRelease();
        gateIndependence();
        System.out.println("V337 SELECTIVE_LAB PASS: 3 boxes, all 7 selection combinations, order invariance, selective release, gate isolation");
    }
}
