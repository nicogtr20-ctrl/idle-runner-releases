package com.overmind.zero;

import java.util.*;

/** Immutable diagnostic graph. Negative sources denote input ports, not neurons. */
public final class Zero3NeuralGraphV354 {
    public static final class Node {
        public final int id,module,index,inputs;public final String name;public final float activity,norm;
        Node(int id,int module,int index,int inputs,String name,float activity,float norm){this.id=id;this.module=module;this.index=index;this.inputs=inputs;this.name=name;this.activity=activity;this.norm=norm;}
    }
    public static final class Edge {
        public final int source,target,gate;public final float weight;
        Edge(int source,int target,int gate,float w){this.source=source;this.target=target;this.gate=gate;weight=w;}
    }
    public final Node[] nodes;public final Edge[] edges;public final long actualSynapses;public final int omittedEdges;
    public final float visualThreshold;public final String[] modules={"GRU centrale","Attore","Critic","Predizione sensi","Corteccia","Adesione","Modello fisico","Output fisici","Candidata shadow","Output shadow","Valore contestuale","Critic target"};
    private Zero3NeuralGraphV354(Builder b){nodes=b.nodes.toArray(new Node[0]);edges=b.edges.toArray(new Edge[0]);actualSynapses=b.synapses;omittedEdges=(int)Math.min(Integer.MAX_VALUE,b.synapses-edges.length);visualThreshold=.04f;}
    static final class Builder {
        final int[] moduleEdges=new int[12];
        final ArrayList<Node> nodes=new ArrayList<>();final ArrayList<Edge> edges=new ArrayList<>();long synapses;
        int node(int module,int index,int inputs,String name,float activity,float[] weights,int offset,int length){
            double sq=0;for(int i=0;i<length;i++)sq+=weights[offset+i]*weights[offset+i];
            int id=nodes.size();nodes.add(new Node(id,module,index,inputs,name,activity,(float)Math.sqrt(sq)));return id;
        }
        void edge(int source,int target,int gate,float weight){
            if(!Float.isFinite(weight))throw new IllegalStateException("V354 invalid graph weight");
            synapses++;if(Math.abs(weight)>=.04f&&edges.size()<1400&&moduleEdges[nodes.get(target).module]<140){edges.add(new Edge(source,target,gate,weight));moduleEdges[nodes.get(target).module]++;}
        }
        Zero3NeuralGraphV354 build(){return new Zero3NeuralGraphV354(this);}
    }
}
