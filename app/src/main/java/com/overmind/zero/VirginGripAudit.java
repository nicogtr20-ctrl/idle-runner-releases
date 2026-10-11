package com.overmind.zero;

import java.util.Random;

/** Read-only reproducible comparison: virgin membrane, same action, same real
 * face contact against gate vs 40x40 square. Only a JVM test; NEVER shipped. */
public final class VirginGripAudit {
    static void ok(boolean yes,String message){if(!yes)throw new AssertionError(message);}
    static void place(Morphology.Surface s,int i,float x,float y){
        s.x[i]=x;s.y[i]=y;s.vx[i]=s.vy[i]=0;
    }
    public static void main(String[] args){
        float[] brain=Morphology.virgin(new Random(511));
        float[] in=new float[Morphology.INPUTS];
        float[] output=new float[3];
        Morphology.output(brain,in,0,output);
        ok(Math.abs(output[2])<.000001f,
            "A newly born membrane should not have learned grip head weights");
        float off=Math.max(0,output[2]),on=Math.max(0,output[2]+.82f);
        ok(off<=Morphology.GRIP_THRESHOLD && on>Morphology.GRIP_THRESHOLD,
            "Virgin adhesion-action + touch must already enable local grip");

        ScarcityLab gate=new ScarcityLab(0,0);
        MotorBrain.Body gateBody=new MotorBrain.Body();
        Morphology.Surface gs=new Morphology.Surface(gateBody);
        UnknownWorld.Entity box=new UnknownWorld.Entity(110,0,2,5);
        MotorBrain.Body boxBody=new MotorBrain.Body();
        Morphology.Surface bs=new Morphology.Surface(boxBody);
        final float dt=.035f;
        final int p=0;
        place(gs,p,gate.gateX()-ScarcityLab.DOOR_HALF_W,0);
        place(bs,p,box.x-SquareGripPuzzle.HALF,0);
        gate.solveGate(gs,dt,0);
        SquareGripPuzzle.constrain(boxBody,bs,box,dt);
        ok(gs.touchSense[p]>.5f && bs.touchSense[p]>.5f,
            "One obstacle did not report same physical first touch");
        ok(gs.gripHold[p]==0 && bs.gripHold[p]==0,
            "Touch alone must NEVER turn point orange");

        int gateHeldFrame=-1,boxHeldFrame=-1;
        for(int i=1;i<=8;i++){
            // No learning, no skill bank. This is the SAME global active
            // adhesion choice applied to previously detected touch.
            gs.gripCommand[p]=on;bs.gripCommand[p]=on;
            gate.solveGate(gs,dt,.82f);
            SquareGripPuzzle.constrain(boxBody,bs,box,dt);
            if(gateHeldFrame<0&&gs.gripHold[p]>=Morphology.GRIP_HOLD)
                gateHeldFrame=i;
            if(boxHeldFrame<0&&bs.gripHold[p]>=Morphology.GRIP_HOLD)
                boxHeldFrame=i;
        }
        ok(gateHeldFrame==boxHeldFrame && gateHeldFrame>=0,
            "A stable contacted virgin point activates on gate "+
            gateHeldFrame+" but box "+boxHeldFrame);
        ok(gs.touchSense[p]>.5f && bs.touchSense[p]>.5f,
            "One face lost actual contact on static fixture");
        ok(gs.gripHold[p]>=Morphology.GRIP_HOLD &&
           bs.gripHold[p]>=Morphology.GRIP_HOLD,
            "Orange local grip should become visible on BOTH solids");

        // A global PRESA ON signal when there is no surface contact cannot
        // create sustained local adhesion on EITHER surface.
        place(bs,p,box.x-SquareGripPuzzle.HALF-15,0);
        bs.gripCommand[p]=on;
        java.util.Arrays.fill(bs.touchSense,0f); // same per-frame clearing as GameActivity
        SquareGripPuzzle.constrain(boxBody,bs,box,dt);
        ok(bs.gripHold[p]<Morphology.GRIP_HOLD,
            "Detached box point retained invalid local adhesion");

        // Verify what the UI truthfully means: requires BOTH a physical
        // hold and tactile touch, not simply global PRESA ON.
        boolean orangeGate=gs.gripHold[p]>=Morphology.GRIP_HOLD&&gs.touchSense[p]>.5f;
        boolean orangeBox=bs.gripHold[p]>=Morphology.GRIP_HOLD&&bs.touchSense[p]>.5f;
        ok(orangeGate&&!orangeBox,"Surface UI grip thresholds inconsistent");
        // Compare small inward motion against stable contact:
        // the original gate physically projects the surface without
        // clearing accumulated hold. The new swept box solver may clear
        // every hold on even tiny repeated face penetration.
        Morphology.Surface gj=new Morphology.Surface(new MotorBrain.Body());
        Morphology.Surface bj=new Morphology.Surface(new MotorBrain.Body());
        MotorBrain.Body bb=new MotorBrain.Body();
        place(gj,p,gate.gateX()-ScarcityLab.DOOR_HALF_W,0);
        place(bj,p,box.x-SquareGripPuzzle.HALF,0);
        int gateOrange=0,boxOrange=0;
        for(int i=0;i<12;i++){
            gj.x[p]=gate.gateX()-ScarcityLab.DOOR_HALF_W+.7f;
            gj.y[p]=gate.gateCenterY();
            gj.gripCommand[p]=on;
            gate.solveGate(gj,dt,.82f);
            if(gj.gripHold[p]>=Morphology.GRIP_HOLD)gateOrange++;
            float[] priorX=bj.x.clone(),priorY=bj.y.clone();
            float[] priorBX=bb.x.clone(),priorBY=bb.y.clone();
            bj.x[p]=box.x-SquareGripPuzzle.HALF+.7f;
            bj.y[p]=box.y;
            bj.gripCommand[p]=on;
            java.util.Arrays.fill(bj.touchSense,0f);
            SquareGripPuzzle.constrain(bb,bj,box,dt,
                priorX,priorY,priorBX,priorBY,bb.cx,bb.cy);
            if(bj.gripHold[p]>=Morphology.GRIP_HOLD)boxOrange++;
        }
        System.out.println("VirginGripAudit probe: gentle sustained inward pressure "+
            "gateOrangeFrames="+gateOrange+", boxOrangeFrames="+boxOrange+
            ", gateHold="+gj.gripHold[p]+", boxHold="+bj.gripHold[p]);
        ok(gateOrange>0,"Expected gate orange under sustained adhesion");
        ok(boxOrange<gateOrange,
            "Repeated shallow box penetration did not show predicted reset");
        System.out.println("VirginGripAudit PASS: initial neural grip head 0; " +
            "same global PRESA ON yields same local hold with stable " +
            "face contact (gate="+gateHeldFrame+", box="+boxHeldFrame+
            "); loss of contact cancels orange, no learning needed.");
    }
}