package com.overmind.zero;

/**
 * ZERO3 V3.5 lab-only conservative feedback veto.
 *
 * This controller does NOT decide an intention, set a gait or claim a causal
 * advantage from an uncontrolled life. It only accepts/rejects a SECOND
 * voluntary exploratory intervention using ACTUAL delayed outcomes.
 * No motor weights are updated and no model is promoted.
 *
 * Mandatory passive 384-frame baseline precedes any probe. Subsequent
 * 384-frame outcomes are compared with the pre-intervention trend. This
 * is an observational trend comparison, not a counterfactual estimate:
 * under nonstationary body states it can be wrong. Safety of this heuristic
 * must be measured on holdout simulated bodies before use.
 */
public final class Zero3MotorAdaptiveGate {
    private final int baselineFrames;
    private final double minVelocityRatio,maxWorkRatio;
    private int passiveCount;
    private float originX,originY,originWork,dx,dy;
    private double referenceSpeed,referenceCost;
    private boolean initialized,baselineReady,blocked;
    private long feedbackCount,acceptedCount,refusedCount;
    public Zero3MotorAdaptiveGate(int frames,double minRatio,double costRatio){
        if(frames<72||frames>2000||!Double.isFinite(minRatio)||
           !Double.isFinite(costRatio)||minRatio<0||minRatio>2||
           costRatio<1||costRatio>5)throw new IllegalArgumentException("adaptive threshold");
        baselineFrames=frames;minVelocityRatio=minRatio;maxWorkRatio=costRatio;
    }
    public void start(float x,float y,float work,float intentionX,float intentionY){
        if(initialized||!Float.isFinite(x)||!Float.isFinite(y)||!Float.isFinite(work)||
           Math.abs(Math.hypot(intentionX,intentionY)-1)>.001)
            throw new IllegalArgumentException("adaptive baseline");
        originX=x;originY=y;originWork=work;dx=intentionX;dy=intentionY;
        initialized=true;
    }
    public void observeBaseline(int actualFrame,float x,float y,float work){
        if(!initialized||baselineReady||actualFrame<=passiveCount||
           !Float.isFinite(x)||!Float.isFinite(y)||!Float.isFinite(work))
            throw new IllegalArgumentException("baseline chronology");
        passiveCount=actualFrame;
        if(passiveCount>=baselineFrames){
            referenceSpeed=((x-originX)*dx+(y-originY)*dy)/passiveCount;
            referenceCost=Math.max(0,work-originWork)/passiveCount;
            baselineReady=true;
        }
    }
    public boolean mayTry(){
        return initialized&&baselineReady&&!blocked;
    }
    /**
     * Caller MUST pass a matured real-frame ledger; no invented observation.
     * We do not pretend that these observations isolate pulse causality.
     */
    public void acceptObserved(Zero3MotorCreditWindow.Observed outcome){
        if(!mayTry()||outcome==null||outcome.measuredFrames<baselineFrames)
            throw new IllegalStateException("premature credit or halted policy");
        feedbackCount++;
        double measured=((outcome.deltaX*dx)+(outcome.deltaY*dy))/
                        outcome.measuredFrames;
        double muscleCost=outcome.work/outcome.measuredFrames;
        // When passive velocity is backwards, demand actual positive
        // progression; do not interpret less backwards as proven advantage.
        double minimum=referenceSpeed>0?referenceSpeed*minVelocityRatio:0;
        boolean accepted=measured>minimum&&
            muscleCost<=referenceCost*maxWorkRatio+1e-7;
        if(accepted){
            acceptedCount++;
            referenceSpeed=.8*referenceSpeed+.2*measured;
            referenceCost=.8*referenceCost+.2*muscleCost;
        }else{
            refusedCount++;
            blocked=true; // no more exploratory pulses in this life
        }
    }
    public boolean blocked(){return blocked;}
    public long feedbackCount(){return feedbackCount;}
    public long acceptedCount(){return acceptedCount;}
    public long refusedCount(){return refusedCount;}
}
