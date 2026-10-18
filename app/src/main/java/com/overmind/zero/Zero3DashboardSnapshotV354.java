package com.overmind.zero;

/** Defensive one-boundary provider: UI rendering never calls choose/train/physics. */
public final class Zero3DashboardSnapshotV354 {
    public final int schema=354;
    public final Zero3RuntimeV325.Display body;
    public final Zero3CognitionV353.Snapshot prediction;
    public final Zero3ExecutiveV354.Snapshot executive;
    public final Zero3NeuralGraphV354 graph;
    public final Zero3PlannerV354.Snapshot planner;
    public final boolean storm,stormWarning;public final int wind;public final float integrity;public final float[][] solids;
    public final boolean observing;public final float elapsedLife;
    public final long trained,heldout,observations;
    public final int neurons;
    public final float[] innerX,innerY,touch,held,gripCommand,impulse,executed,usedInput,postInput;
    public final long[] physicalRepairs;
    Zero3DashboardSnapshotV354(Zero3RuntimeV325.Display body,Zero3CognitionV353.Snapshot prediction,
        Zero3ExecutiveV354.Snapshot executive,Zero3NeuralGraphV354 graph,Zero3PlannerV354.Snapshot planner,boolean storm,boolean warning,int wind,float integrity,float[][] solids,float elapsedLife,boolean observing,
        long trained,long heldout,long observations,int neurons,float[] ix,float[] iy,float[] touch,float[] held,float[] grip,float[] pulse,
        float[] executed,float[] used,float[] post,long[] repairs){
        this.body=body;this.prediction=prediction;this.executive=executive;this.graph=graph;this.planner=planner;this.storm=storm;stormWarning=warning;this.wind=wind;this.integrity=integrity;this.solids=new float[solids.length][];for(int i=0;i<solids.length;i++)this.solids[i]=solids[i].clone();this.observing=observing;this.elapsedLife=elapsedLife;
        this.trained=trained;this.heldout=heldout;this.observations=observations;this.neurons=neurons;
        innerX=ix.clone();innerY=iy.clone();this.touch=touch.clone();this.held=held.clone();gripCommand=grip.clone();impulse=pulse.clone();
        this.executed=executed==null?null:executed.clone();usedInput=used==null?null:used.clone();postInput=post==null?null:post.clone();physicalRepairs=repairs.clone();
    }
}
