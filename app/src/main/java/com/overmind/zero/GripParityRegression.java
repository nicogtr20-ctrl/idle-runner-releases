package com.overmind.zero;

/**
 * Diagnostic experiment: same 0.82 virgin action adhesion against stationary
 * gate and small square. Touch and inward pressure must not be confused
 * with a learned understanding of doors or boxes.
 */
public final class GripParityRegression {
    static void check(boolean ok,String reason){if(!ok)throw new AssertionError(reason);}
    public static void main(String[] args){
        final float dt=.035f;
        final int holdFrames=(int)Math.ceil(Morphology.GRIP_HOLD/dt)+12;
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ScarcityLab gate=new ScarcityLab(0,0);
        int left=Morphology.POINTS/2,right=0;
        int gateHeld=0;
        for(int frame=0;frame<holdFrames;frame++){
            for(int i=0;i<Morphology.POINTS;i++){
                skin.x[i]=gate.gateX()-85;skin.y[i]=-100;
                skin.gripCommand[i]=0;
            }
            skin.x[left]=gate.gateX()-ScarcityLab.DOOR_HALF_W;
            skin.y[left]=gate.gateCenterY();
            skin.x[right]=gate.gateX()+ScarcityLab.DOOR_HALF_W;
            skin.y[right]=gate.gateCenterY();
            skin.gripCommand[left]=.82f;
            skin.gripCommand[right]=.82f;
            gate.solveGate(skin,dt,.82f);
            // Voluntary grip plus real touch is NOT immediately active.
            if((frame+1)*dt<Morphology.GRIP_HOLD-.001f)
                check(skin.gripHold[left]<Morphology.GRIP_HOLD&&
                      skin.gripHold[right]<Morphology.GRIP_HOLD,
                      "Gate lit orange before five continuous seconds");
            if(skin.gripHold[left]>=Morphology.GRIP_HOLD&&
               skin.gripHold[right]>=Morphology.GRIP_HOLD)gateHeld++;
        }
        check(gateHeld>0,
            "Even voluntary untouched gate grip cannot engage physically");
        System.out.println("Gate virgin action .82: heldFrames="+gateHeld+
            " latch="+skin.gripHold[left]+","+skin.gripHold[right]);
        skin.gripCommand[left]=0f;
        gate.solveGate(skin,dt,0f);
        check(skin.gripHold[left]==0f,"Gate dwell not reset on released command");
        skin.gripCommand[left]=.82f;
        gate.solveGate(skin,dt,.82f);
        check(skin.gripHold[left]<Morphology.GRIP_HOLD,
              "Gate regained physical grip immediately after one-frame release");

        body=new MotorBrain.Body();
        skin=new Morphology.Surface(body);
        UnknownWorld.Entity box=new UnknownWorld.Entity(110,0,2,5);
        int boxHeld=0,boxTwo=0,contacted=0,edgeFix=0;
        for(int frame=0;frame<holdFrames;frame++){
            for(int i=0;i<Morphology.POINTS;i++){
                double angle=2*Math.PI*i/Morphology.POINTS;
                skin.x[i]=box.x+36f*(float)Math.cos(angle);
                skin.y[i]=box.y+36f*(float)Math.sin(angle);
                skin.gripCommand[i]=0;
            }
            skin.x[left]=box.x-SquareGripPuzzle.HALF;
            skin.y[left]=box.y;
            skin.x[right]=box.x+SquareGripPuzzle.HALF;
            skin.y[right]=box.y;
            // Snapshot of valid contact on the near face, just as a mobile
            // game frame samples the previous physically corrected state.
            float[] oldX=skin.x.clone(),oldY=skin.y.clone();
            // The membrane continues to PRESS against the solid even while
            // trying to hold it: this must not erase legal adhesion.
            skin.x[left]+=.8f;
            skin.x[right]-=.8f;
            skin.vx[left]=30;skin.vx[right]=-30;
            skin.gripCommand[left]=.82f;
            skin.gripCommand[right]=.82f;
            SquareGripPuzzle.Outcome o=SquareGripPuzzle.constrain(body,skin,box,dt,
                  oldX,oldY,body.x.clone(),body.y.clone(),body.cx,body.cy);
            if((frame+1)*dt<Morphology.GRIP_HOLD-.001f)
                check(o.held==0&&!o.opposingGrip&&
                      skin.gripHold[left]<Morphology.GRIP_HOLD&&
                      skin.gripHold[right]<Morphology.GRIP_HOLD,
                      "Box lit orange or moved before five continuous seconds");
            if(o.contacts>0)contacted++;
            if(o.held>=2)boxHeld++;
            if(o.opposingGrip)boxTwo++;
            if(SquareCollisionSolver.intersections(skin,box)>0)edgeFix++;
        }
        System.out.println("Box virgin action .82: contactsFrames="+contacted+
            " heldFrames="+boxHeld+" bilateralFrames="+boxTwo+
            " gripLeft="+skin.gripHold[left]+" gripRight="+skin.gripHold[right]+
            " crossedLinks="+edgeFix);
        check(contacted>=6,"Control geometry never contacts box");
        check(edgeFix==0,"Audit fixture has invalid penetrating links");
        // Deliberately assert the expected physical parity; with defective
        // solver this fails and proves the source of the user's complaint.
        check(boxHeld>0&&boxTwo>0,
              "BUG: box CCD erases ordinary sustained two-sided adhesive contact");
        check(skin.touchSense[left]>.5f&&skin.touchSense[right]>.5f&&
              skin.gripHold[left]>=Morphology.GRIP_HOLD&&
              skin.gripHold[right]>=Morphology.GRIP_HOLD,
              "Real box hold did not meet orange-render condition");
        skin.gripCommand[left]=0f;
        SquareGripPuzzle.constrain(body,skin,box,dt);
        check(skin.gripHold[left]==0f,"Box dwell not reset on released command");
        skin.gripCommand[left]=.82f;
        SquareGripPuzzle.constrain(body,skin,box,dt);
        check(skin.gripHold[left]<Morphology.GRIP_HOLD,
              "Box regained physical grip immediately after one-frame release");
        // Opposite test: a particle teleporting straight THROUGH the square
        // cannot keep its accumulated grip. The new exception protects
        // ordinary same-face contact only, not tunneling.
        float[] previousX=skin.x.clone(),previousY=skin.y.clone();
        skin.gripHold[left]=.26f;
        skin.x[left]=box.x+SquareGripPuzzle.HALF+18f;
        SquareCollisionSolver.constrain(body,skin,box,previousX,previousY,
            body.x.clone(),body.y.clone(),body.cx,body.cy);
        check(skin.gripHold[left]==0f,
              "Illegal full-width crossing retained adhesive hold");
        System.out.println("GripParityRegression PASS: both surfaces can hold .82 action, orange at box, crossing cancels illegal hold");
    }
}
