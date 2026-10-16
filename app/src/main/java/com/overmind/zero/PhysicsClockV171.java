package com.overmind.zero;
/** Same real physical integration interval at every display rate and speed. */
public final class PhysicsClockV171 {
    public static final float DT=1f/60f;
    private double remainder;
    public int steps(float wallDelta,int speed){
        if(!Float.isFinite(wallDelta)||wallDelta<=0f)return 0;
        if(speed!=1&&speed!=5&&speed!=20)throw new IllegalArgumentException("Speed");
        remainder+=(double)Math.min(.038f,wallDelta)*speed;
        int count=(int)Math.floor((remainder+1e-8)/DT);
        remainder-=count*(double)DT;
        return count;
    }
    public void reset(){remainder=0;}
}
