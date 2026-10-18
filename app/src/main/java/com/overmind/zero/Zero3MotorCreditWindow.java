package com.overmind.zero;

/**
 * ZERO3 experimental motor temporal-credit ledger.
 *
 * A local exploration pulse is an intervention with a long physical effect:
 * old 48-frame rewards are empirically premature in 13 of 16 tested bodies.
 *
 * The ledger blocks a SECOND exploratory pulse until a prior one has been
 * physically observed for the requested settling window. It never chooses
 * a direction, sets a gait, or gives synthetic reward. The outcome is an
 * OBSERVED displacement, not a laboratory matched-control causal difference.
 * Never use it as a direct actor-critic gradient without further validation.
 */
public final class Zero3MotorCreditWindow {
    public static final class Observed {
        public final long decisionId,startFrame,endFrame,measuredFrames;
        public final double deltaX,deltaY,work;
        Observed(long id,long start,long end,double dx,double dy,double work){
            decisionId=id;startFrame=start;endFrame=end;measuredFrames=end-start;
            deltaX=dx;deltaY=dy;this.work=work;
        }
    }
    private final int observationWindowFrames;
    private long lastFrame=-1,lastDecisionId=-1,openedAt=-1;
    private boolean pending;
    private float x0,y0,work0;
    private Observed lastCompleted;
    private long completed,suppressed,aborted;
    public Zero3MotorCreditWindow(int frames){
        if(frames<72||frames>2000)
            throw new IllegalArgumentException("physical observation window 72..2000");
        observationWindowFrames=frames;
    }
    public int observationWindowFrames(){return observationWindowFrames;}
    public long completedCount(){return completed;}
    public long suppressedCount(){return suppressed;}
    public long abortedCount(){return aborted;}
    public boolean pending(){return pending;}
    public Observed lastCompleted(){return lastCompleted;}
    /** Must not grant credit or permit further exploration before observation. */
    public boolean mayExplore(long frame){
        return !pending&&frame>lastFrame;
    }
    public boolean begin(long decisionId,long frame,float bodyX,float bodyY,float work){
        if(decisionId<0||frame<0||decisionId<=lastDecisionId||frame<=lastFrame)
            throw new IllegalArgumentException("invalid motor decision timeline");
        if(!Float.isFinite(bodyX)||!Float.isFinite(bodyY)||!Float.isFinite(work))
            throw new IllegalArgumentException("nonfinite motor body state");
        if(pending){suppressed++;return false;}
        pending=true;lastDecisionId=decisionId;openedAt=frame;
        x0=bodyX;y0=bodyY;work0=work;
        return true;
    }
    /**
     * Call only after the REAL solver integrated this frame. Never call with
     * an unmeasured intended position or a model-generated future value.
     */
    public void observe(long frame,float x,float y,float cumulativeWork){
        if(frame<=lastFrame||frame<0||!Float.isFinite(x)||!Float.isFinite(y)||
           !Float.isFinite(cumulativeWork))
            throw new IllegalArgumentException("invalid observed frame");
        lastFrame=frame;
        if(pending&&frame>=openedAt+observationWindowFrames){
            lastCompleted=new Observed(lastDecisionId,openedAt,frame,
                x-x0,y-y0,Math.max(0,cumulativeWork-work0));
            pending=false;openedAt=-1;completed++;
        }
    }
    public void abort(){
        if(pending){pending=false;openedAt=-1;aborted++;}
    }
}
