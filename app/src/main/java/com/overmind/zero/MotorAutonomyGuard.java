package com.overmind.zero;

/**
 * Passive, evidence-based protection against repeated episodic action choices
 * that leave ZERO physically stuck. Does NOT command a direction or alter
 * motor weights: temporarily suppresses positive recalled-policy bias only.
 *
 * A useful stationary interaction (an object actually moving) is not stuck.
 * Scores come from REAL post-collision nucleus positions and world effects.
 */
public final class MotorAutonomyGuard {
    static final float WINDOW_SECONDS=7f;
    static final float MIN_NET_PROGRESS=16f;
    static final float MIN_WORLD_EFFECT=1.25f;
    static final float RECOVERY_SECONDS=12f;
    private boolean initialized=false;
    private float startX,startY,windowSeconds,worldEffect,remaining=0;
    private int stalledWindows=0;
    private final int[] recentActions=new int[32];
    private final int[] recentCounts=new int[SensorimotorMind.ACTIONS];
    private int recentIndex=0,recentSize=0;
    public int recoveryActivations=0,freeChoices=0;
    public float lastNet=0,lastWorldEffect=0;

    public void resetLife(float x,float y){
        initialized=true;startX=x;startY=y;windowSeconds=0;worldEffect=0;
        remaining=0;stalledWindows=0;lastNet=0;lastWorldEffect=0;
        recentIndex=recentSize=0;
        java.util.Arrays.fill(recentCounts,0);
    }
    public void restoreCounts(int recoveries,int choices){
        recoveryActivations=Math.max(0,recoveries);
        freeChoices=Math.max(0,choices);
    }
    public boolean suppressPositiveRecall(){return remaining>0;}
    public float remainingSeconds(){return remaining;}
    public void decisionWhenSuppressed(){if(suppressPositiveRecall())freeChoices++;}
    /** Discourage only the repeated actions observed during real stalls:
     * do not assign a correct direction. */
    public void action(int selected){
        if(selected<0||selected>=SensorimotorMind.ACTIONS)return;
        if(recentSize==recentActions.length){
            recentCounts[recentActions[recentIndex]]--;
        }else recentSize++;
        recentActions[recentIndex]=selected;
        recentCounts[selected]++;
        recentIndex=(recentIndex+1)%recentActions.length;
    }
    public float stagnationBias(int candidate){
        if(!suppressPositiveRecall()||candidate<0||candidate>=recentCounts.length||
           recentSize<8)return 0;
        return -.95f*recentCounts[candidate]/recentSize;
    }
    public void observe(float dt,float x,float y,float measuredWorldChange){
        if(!Float.isFinite(dt)||dt<0||dt>1||
           !Float.isFinite(x)||!Float.isFinite(y)||
           !Float.isFinite(measuredWorldChange)||measuredWorldChange<0)return;
        if(!initialized){resetLife(x,y);return;}
        remaining=Math.max(0,remaining-dt);
        windowSeconds+=dt;
        worldEffect+=Math.min(12,measuredWorldChange);
        if(windowSeconds<WINDOW_SECONDS)return;
        float net=(float)Math.hypot(x-startX,y-startY);
        lastNet=net;lastWorldEffect=worldEffect;
        // Stationary grasp or manipulation may still be valuable.
        if(net<MIN_NET_PROGRESS&&worldEffect<MIN_WORLD_EFFECT){
            stalledWindows++;
            if(stalledWindows>=2){
                remaining=RECOVERY_SECONDS;
                recoveryActivations++;
                stalledWindows=0;
            }
        }else{
            stalledWindows=0;
            // Actual progress recovers trust in episodic policy sooner.
            if(remaining>0&&(net>MIN_NET_PROGRESS*2 ||
                              worldEffect>=MIN_WORLD_EFFECT*3))remaining=0;
        }
        startX=x;startY=y;windowSeconds=0;worldEffect=0;
    }
    public static void main(String[] args){
        MotorAutonomyGuard g=new MotorAutonomyGuard();
        g.resetLife(0,0);
        for(int i=0;i<14;i++)g.observe(1,0,0,0);
        if(!g.suppressPositiveRecall()||g.recoveryActivations!=1)
            throw new AssertionError("Repeatedly stationary organism did not get free choices");
        g.decisionWhenSuppressed();
        if(g.freeChoices!=1)throw new AssertionError("Decision counter");
        for(int i=0;i<32;i++)g.action(3);
        if(!(g.stagnationBias(3)<g.stagnationBias(5)))
            throw new AssertionError("Repeated stalled heading not discouraged");
        for(int i=0;i<7;i++)g.observe(1,80,0,0);
        if(g.suppressPositiveRecall())throw new AssertionError("Progress failed to release recall");
        int prior=g.recoveryActivations;
        g.resetLife(0,0);
        for(int i=0;i<21;i++)g.observe(1,0,0,1);
        if(g.recoveryActivations!=prior)throw new AssertionError("Stationary manipulation incorrectly disabled");
        g.resetLife(0,0);
        for(int i=0;i<14;i++)g.observe(1,0,0,0);
        if(g.recoveryActivations!=prior+1)throw new AssertionError("Life reset broke guard");
        g.restoreCounts(5,11);
        if(g.recoveryActivations!=5||g.freeChoices!=11)
            throw new AssertionError("Persisted statistics");
        System.out.println("ZERO-009.7 mobility watchdog: no scripted direction, real exploration recovers, useful stationary contact protected");
    }
}
