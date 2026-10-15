package com.overmind.zero;

import java.util.Locale;

/**
 * OTA161 stress-only physics: multiple consecutive cortex bursts must not
 * overextend into a persistent one-dimensional web or freeze after reversal.
 * These externally stimulated trial signals NEVER enter ZERO's real policy.
 */
public final class Zero161OverstretchRegression {
    private static void need(boolean ok,String msg){if(!ok)throw new AssertionError("ZERO161 LONG "+msg);}
    private static float radius(MotorBrain.Body b,Morphology.Surface s,int sector){
        float sum=0;
        for(int i=sector*6;i<sector*6+6;i++)
            sum+=(float)Math.hypot(s.x[i]-b.cx,s.y[i]-b.cy);
        return sum/6f;
    }
    private static float[] triple(){
        float[] a=new float[64];
        for(int s:new int[]{0,5,10}){
            a[s*4]=.96f;a[((s+1)%16)*4]=.47f;
            a[((s+15)%16)*4]=.47f;
        }
        return a;
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(body,skin);
        float[] grow=triple();
        for(int k=0;k<180;k++)gel.step(body,skin,grow,.025f);
        float p0=radius(body,skin,0),p5=radius(body,skin,5),p10=radius(body,skin,10);
        long repairsAtPeak=gel.topologyRepairs;
        float maximum=Math.max(p0,Math.max(p5,p10));
        float[] release=grow.clone();
        release[5*4]=-.92f;release[4*4]=-.52f;release[6*4]=-.52f;
        for(int k=0;k<65;k++)gel.step(body,skin,release,.025f);
        float change=radius(body,skin,5)-p5;
        long repairsAfter=gel.topologyRepairs-repairsAtPeak;
        for(int k=0;k<120;k++)gel.step(body,skin,new float[64],.025f);
        System.out.printf(Locale.US,
            "ZERO161 LONG 180-step triple=[%.2f %.2f %.2f] max=%.2f retraction=%.2f repairs=[%d +%d] selfContacts=%d remainingAreaErr=%.4f afterRestRadius=[%.2f %.2f %.2f]%n",
            p0,p5,p10,maximum,change,repairsAtPeak,repairsAfter,
            gel.selfContactPairs,gel.areaRelativeError(skin),
            radius(body,skin,0),radius(body,skin,5),radius(body,skin,10));
        need(gel.invalid==0,"matter became non-finite");
        need(MaterialGeometry.selfCrossings(skin)==0,"surface self-crossed");
        need(gel.areaRelativeError(skin)<.16f,"lost physical material area");
        need(maximum<78f,"extended cortex forms an overlong filament");
        need(change< -3f,"long-held arm cannot retract");
        need(repairsAtPeak+repairsAfter<50,"emergency topological rollbacks still dominate motion");
    }
}
