package com.overmind.zero;

import java.util.Arrays;

/**
 * ZERO488 LAB ONLY: original measured muscle-effort conditioned historical baseline.
 * Index uses ONLY the actual V356-issued action bearing, measured
 * pre-decision COM speed and actually issued motor effort. No resource positions, colors, desired heading,
 * post-decision lookahead, motor forces or simulated action results.
 * Transient and never serialized into V356/512 synapses or the 12 slots.
 *
 * Prior physically eligible observations are available BEFORE scoring the
 * next completed action; that action enters history only AFTER any learning.
 */
final class Zero488EffortConditionedMotorBaseline {
    static final int HEADINGS=8, SPEED_REGIMES=3, EFFORT_REGIMES=2, WARMUP=3;
    private final double[] mean=new double[HEADINGS*SPEED_REGIMES*EFFORT_REGIMES];
    private final int[] samples=new int[HEADINGS*SPEED_REGIMES*EFFORT_REGIMES];
    private long accepted,centered;

    static int bucket(float dx,float dy,float preVx,float preVy,float effort) {
        if(!Float.isFinite(dx)||!Float.isFinite(dy)||
           !Float.isFinite(preVx)||!Float.isFinite(preVy)||!Float.isFinite(effort)||effort<0f||effort>1f||
           Math.hypot(dx,dy)<.1)
            throw new IllegalArgumentException("V488 original motor evidence shape");
        int heading=Math.floorMod(Math.round((float)Math.atan2(dy,dx)/
            (float)(Math.PI/4d)),HEADINGS);
        double speed=Math.hypot(preVx,preVy);
        int regime=speed<.35d?0:speed<2d?1:2;
        return (effort<.5f?0:1)*SPEED_REGIMES*HEADINGS+regime*HEADINGS+heading;
    }
    float advantage(float dx,float dy,float preVx,float preVy,float effort,float actualScore) {
        if(!Float.isFinite(actualScore)||Math.abs(actualScore)>100f)
            throw new IllegalArgumentException("V488 invalid observed physical credit");
        int i=bucket(dx,dy,preVx,preVy,effort);
        if(samples[i]<WARMUP)return 0f; // Never credit an uncalibrated baseline.
        centered++;
        return Math.max(-1f,Math.min(1f,actualScore-(float)mean[i]));
    }
    void accept(float dx,float dy,float preVx,float preVy,float effort,float actualScore) {
        if(!Float.isFinite(actualScore)||Math.abs(actualScore)>100f)
            throw new IllegalArgumentException("V488 invalid real physical outcome");
        int i=bucket(dx,dy,preVx,preVy,effort);
        double value=Math.max(-1d,Math.min(1d,actualScore));
        if(samples[i]<100000000)samples[i]++;
        // Online unbiased historical estimate for the first 32 real samples;
        // bounded recent adaptation when the material regime slowly changes.
        double alpha=samples[i]<=32?1d/samples[i]:.1d;
        mean[i]+=alpha*(value-mean[i]);
        accepted++;
    }
    int evidence(float dx,float dy,float preVx,float preVy,float effort) {
        return samples[bucket(dx,dy,preVx,preVy,effort)];
    }
    long accepted(){return accepted;}
    long centered(){return centered;}
    void newLife(){
        Arrays.fill(samples,0);
        Arrays.fill(mean,0d);
    }
}
