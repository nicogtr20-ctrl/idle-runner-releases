package com.overmind.zero;

/** Real gate and square on SAME surface in the SAME physical frames. */
public final class GateBoxGripIsolationRegression {
    static void ok(boolean b,String s){if(!b)throw new AssertionError(s);}
    public static void main(String[] args){
        final float dt=.035f;
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ScarcityLab gate=new ScarcityLab(0,0);
        UnknownWorld.Entity box=new UnknownWorld.Entity(-120,0,2,5);
        int a=0,b=Morphology.POINTS/2,held=0,bilateral=0,orange=0;
        for(int f=0;f<9;f++){
            for(int i=0;i<Morphology.POINTS;i++){
                double theta=2*Math.PI*i/Morphology.POINTS;
                skin.x[i]=box.x+36f*(float)Math.cos(theta);
                skin.y[i]=box.y+36f*(float)Math.sin(theta);
                skin.gripCommand[i]=0f;
            }
            skin.x[a]=box.x+SquareGripPuzzle.HALF;skin.y[a]=box.y;
            skin.x[b]=box.x-SquareGripPuzzle.HALF;skin.y[b]=box.y;
            float[] oldX=skin.x.clone(),oldY=skin.y.clone();
            float[] oldBX=body.x.clone(),oldBY=body.y.clone();
            float cx=body.cx,cy=body.cy;
            skin.x[a]-=.8f;skin.x[b]+=.8f;
            skin.gripCommand[a]=skin.gripCommand[b]=.82f;
            gate.constrain(body,skin,dt,.82f);
            SquareGripPuzzle.Outcome outcome=SquareGripPuzzle.constrain(
                body,skin,box,dt,oldX,oldY,oldBX,oldBY,cx,cy);
            if(outcome.held>=2)held++;
            if(outcome.opposingGrip)bilateral++;
            if(skin.gripHold[a]>=Morphology.GRIP_HOLD&&
               skin.gripHold[b]>=Morphology.GRIP_HOLD&&
               skin.touchSense[a]>.5f&&skin.touchSense[b]>.5f)orange++;
            ok(SquareCollisionSolver.intersections(skin,box)==0,
                "Illegal through-box membrane link");
        }
        System.out.println("Simultaneous gate+square held="+held+
            " bilateral="+bilateral+" orange="+orange);
        ok(held>=4&&bilateral>=4&&orange>=4,
            "Processing gate destroys sustained grip on distant square");
        ok(gate.gateHeldFrames==0,"Square grip falsely counted at gate");
        // Gate cannot erase an unrelated square's established grip.
        float prior=box.boxGripHold[a];
        gate.solveGate(skin,dt,0f);
        ok(box.boxGripHold[a]==prior,
            "Gate cleared a square's independently owned hold");

        Morphology.Surface other=new Morphology.Surface(new MotorBrain.Body());
        ScarcityLab door=new ScarcityLab(0,0);
        for(int f=0;f<8;f++){
            for(int i=0;i<Morphology.POINTS;i++){
                other.x[i]=door.gateX()-85;other.y[i]=-100;
                other.gripCommand[i]=0;
            }
            other.x[a]=door.gateX()+ScarcityLab.DOOR_HALF_W;
            other.x[b]=door.gateX()-ScarcityLab.DOOR_HALF_W;
            other.y[a]=other.y[b]=door.gateCenterY();
            other.gripCommand[a]=other.gripCommand[b]=.82f;
            door.solveGate(other,dt,.82f);
        }
        ok(other.gripHold[a]>=Morphology.GRIP_HOLD,
            "Ordinary gate hold no longer works");
        other.x[a]=door.gateX()+90;
        door.solveGate(other,dt,.82f);
        ok(other.gripHold[a]==0,
            "Lost gate touch must immediately release gate hold");
        UnknownWorld.Entity independentBox=new UnknownWorld.Entity(-120,0,2,5);
        independentBox.boxGripHold[a]=.5f;
        door.solveGate(other,dt,0f);
        ok(independentBox.boxGripHold[a]==.5f,
            "Distant square grip must not be erased by the gate");
        other.x[a]=door.gateX()+ScarcityLab.DOOR_HALF_W;
        other.y[a]=door.gateCenterY();
        door.solveGate(other,dt,.82f);
        ok(other.gripHold[a]<Morphology.GRIP_HOLD,
            "New gate contact improperly borrows existing square hold");
        other.gripCommand[a]=0;
        door.solveGate(other,dt,0f);
        ok(other.gripHold[a]==0,
            "Gate must still require an independent local grip command");
        System.out.println("PASS gate and box independent without neural changes");
    }
}
