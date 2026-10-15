package com.overmind.zero;

import java.io.*;
import java.util.*;

/**
 * Neurogenesis attached to the ONLY running RecurrentCoreV22.
 *
 * A new node is a genuine recurrent nonlinear basis neuron: trainable input
 * connections from actual 164 sensory + 24 core recurrent channels, a learned
 * self-feedback connection, bias, and sixteen trainable outgoing connections
 * feeding action values. None is a separate chooser or scripted behavior.
 *
 * Candidates learn on executed TRAIN transitions only. Held-out experiences
 * are evaluated against the unchanged incumbent predictor, never used for
 * gradients/replay. No candidate enters the live policy until its independent
 * prediction MSE significantly exceeds baseline on enough live holdouts.
 */
public final class NeurogenesisV22 {
    public static final int ORIGINAL=RecurrentCoreV22.H, MAX_EXTRA=16;
    static final int MAGIC=0x4e473232;
    private static final int[] INPUT={
        0,1,2,6,23,26,50,82,105,138,144,160,
        RecurrentCoreV22.F,RecurrentCoreV22.F+3,
        RecurrentCoreV22.F+11,RecurrentCoreV22.F+19
    };
    private static final int INPUTS=INPUT.length,PARAMETERS=INPUTS+2;
    private static final int ACTIONS=RecurrentCoreV22.A;
    private static final float MIN_HOLDOUT_ERROR=.00025f;
    private final ArrayList<Node> grown=new ArrayList<>();
    private Node candidate;
    private long trainingEvents,validationEvents,lastAttemptTraining;
    private int attempts,rejections,candidateTrain,candidateValidation;
    private double baselineLoss,candidateLoss;
    private float lastAcceptedGain;
    private static final class Node {
        final float[] in=new float[PARAMETERS],out=new float[ACTIONS];
        float previous,heldoutGain;
        int age;
        float activate(float[] z){
            float sum=in[INPUTS+1]+in[INPUTS]*previous;
            for(int k=0;k<INPUTS;k++)sum+=in[k]*z[INPUT[k]];
            return (float)Math.tanh(sum);
        }
        void advance(float x){previous=x;age++;}
        float contribution(float x,int action){
            return clamp(out[action]*x,-.18f,.18f);
        }
        void fit(float[] z,float activation,int action,float err){
            float previousOutput=out[action];
            out[action]=clamp(out[action]+.085f*clamp(err,-1,1)*activation,-.8f,.8f);
            float gradient=.024f*clamp(err,-1,1)*previousOutput*(1-activation*activation);
            for(int k=0;k<INPUTS;k++)
                in[k]=clamp(in[k]+gradient*z[INPUT[k]],-1.4f,1.4f);
            in[INPUTS]=clamp(in[INPUTS]+gradient*previous,-.8f,.8f);
            in[INPUTS+1]=clamp(in[INPUTS+1]+gradient,-1.4f,1.4f);
        }
        void write(DataOutputStream d)throws IOException{
            for(float v:in)d.writeFloat(v);
            for(float v:out)d.writeFloat(v);
            d.writeFloat(previous);d.writeFloat(heldoutGain);d.writeInt(age);
        }
        static Node read(DataInputStream d)throws IOException{
            Node node=new Node();
            for(int i=0;i<PARAMETERS;i++)node.in[i]=bounded(d.readFloat(),1.401f);
            for(int i=0;i<ACTIONS;i++)node.out[i]=bounded(d.readFloat(),.801f);
            node.previous=bounded(d.readFloat(),1.001f);
            node.heldoutGain=bounded(d.readFloat(),1.001f);
            node.age=d.readInt();if(node.age<0||node.age>1000000000)throw new IOException("neuron age");
            return node;
        }
    }
    private static float clamp(float v,float lo,float hi){return Math.max(lo,Math.min(hi,v));}
    private static float bounded(float x,float bound)throws IOException{
        if(!Float.isFinite(x)||Math.abs(x)>bound)throw new IOException("invalid neuron weight");
        return x;
    }
    public int initialNeurons(){return ORIGINAL;}
    public int addedNeurons(){return grown.size();}
    public int totalNeurons(){return ORIGINAL+grown.size();}
    public int maxNeurons(){return ORIGINAL+MAX_EXTRA;}
    public boolean candidateInTraining(){return candidate!=null;}
    public int growthAttempts(){return attempts;}
    public int rejectedAttempts(){return rejections;}
    public float lastAcceptedImprovement(){return lastAcceptedGain;}
    public long trainingEvents(){return trainingEvents;}
    public long validationEvents(){return validationEvents;}
    /** The real connection strength of a validated additional neuron. */
    public float neuronStrength(int index){
        if(index<0||index>=grown.size())throw new IndexOutOfBoundsException();
        Node n=grown.get(index);float s=0;
        for(float v:n.out)s+=Math.abs(v);
        return s/ACTIONS;
    }
    public int addedParameters(){return grown.size()*(PARAMETERS+ACTIONS);}
    private Node propose(){
        Random rnd=new Random(12719L+104729L*attempts);
        Node n=new Node();
        for(int k=0;k<INPUTS;k++)n.in[k]=(float)(rnd.nextGaussian()*.24);
        n.in[INPUTS]=(float)(rnd.nextGaussian()*.12);
        n.in[INPUTS+1]=(float)(rnd.nextGaussian()*.16);
        for(int a=0;a<ACTIONS;a++)n.out[a]=(float)(rnd.nextGaussian()*.008);
        return n;
    }
    /** Read-only, bounded influence of actual validated extra recurrent nodes. */
    public float actionCorrection(float[] realState,int action){
        if(realState==null||realState.length!=RecurrentCoreV22.Z
           ||action<0||action>=ACTIONS)return 0;
        float sum=0;
        for(Node node:grown)
            sum+=node.contribution(node.activate(realState),action)*
                clamp(node.heldoutGain,0,.65f);
        return clamp(sum,-.15f,.15f);
    }
    /**
     * Exactly one executed physical decision. Called BEFORE updating the
     * incumbent reward model to keep the measured baseline non-peeking.
     * The heldout target is never passed to a fit() call.
     */
    public void observe(float[] realState,int action,float actualEarned,
                        float incumbentPrediction,boolean train,boolean holdout){
        if(realState==null||realState.length!=RecurrentCoreV22.Z||
           action<0||action>=ACTIONS||
           !Float.isFinite(actualEarned)||!Float.isFinite(incumbentPrediction))
            return;
        if(!train&&!holdout)return;
        float base=incumbentPrediction;
        for(Node n:grown){
            float x=n.activate(realState);
            float correction=n.contribution(x,action)*clamp(n.heldoutGain,0,.65f);
            if(train)n.fit(realState,x,action,actualEarned-base);
            base+=correction;
            n.advance(x);
        }
        if(train)trainingEvents++;
        else validationEvents++;
        if(grown.size()>=MAX_EXTRA)return;
        if(candidate==null&&train&&trainingEvents>=128&&
           trainingEvents-lastAttemptTraining>=128){
            attempts++;
            candidate=propose();
            candidateTrain=candidateValidation=0;
            baselineLoss=candidateLoss=0;
            lastAttemptTraining=trainingEvents;
        }
        if(candidate==null)return;
        float x=candidate.activate(realState);
        float improvementTerm=candidate.contribution(x,action);
        if(train){
            candidate.fit(realState,x,action,actualEarned-base);
            candidateTrain++;
        }else if(holdout&&candidateTrain>=64){
            float residual=actualEarned-base;
            baselineLoss+=residual*residual;
            float newError=residual-improvementTerm;
            candidateLoss+=newError*newError;
            candidateValidation++;
        }
        candidate.advance(x);
        if(candidateTrain>=80&&candidateValidation>=32){
            double ratio=baselineLoss/Math.max(1,candidateValidation);
            float gain=ratio>MIN_HOLDOUT_ERROR
                ?(float)clamp((float)(1-candidateLoss/Math.max(1e-12,baselineLoss)),-1,1):0;
            if(gain>=.08f){
                candidate.heldoutGain=clamp(gain,.08f,.65f);
                grown.add(candidate);
                lastAcceptedGain=gain;
                candidate=null;
                lastAttemptTraining=trainingEvents;
            }else if(candidateValidation>=64){
                rejections++;
                candidate=null;
                lastAttemptTraining=trainingEvents;
            }
        }
    }
    public void resetEpisode(){
        for(Node n:grown)n.previous=0;
        if(candidate!=null)candidate.previous=0;
    }
    public boolean hasPersistence(){return true;}
    public void write(DataOutputStream out)throws IOException{
        out.writeInt(MAGIC);
        out.writeLong(trainingEvents);out.writeLong(validationEvents);
        out.writeLong(lastAttemptTraining);
        out.writeInt(attempts);out.writeInt(rejections);
        out.writeFloat(lastAcceptedGain);
        out.writeInt(grown.size());
        for(Node n:grown)n.write(out);
        out.writeBoolean(candidate!=null);
        if(candidate!=null){
            candidate.write(out);
            out.writeInt(candidateTrain);out.writeInt(candidateValidation);
            out.writeDouble(baselineLoss);out.writeDouble(candidateLoss);
        }
    }
    public void read(DataInputStream in)throws IOException{
        long t=in.readLong(),v=in.readLong(),last=in.readLong();
        int tries=in.readInt(),rejected=in.readInt();
        float gain=bounded(in.readFloat(),1.001f);
        int count=in.readInt();
        if(t<0||v<0||last<0||last>t||t>1000000000000L||v>1000000000000L||
           tries<0||tries>100000000||rejected<0||rejected>tries||
           count<0||count>MAX_EXTRA)throw new IOException("neuron counters");
        ArrayList<Node> restored=new ArrayList<>();
        for(int i=0;i<count;i++)restored.add(Node.read(in));
        boolean has=in.readBoolean();Node next=null;int trained=0,held=0;
        double originalLoss=0,challengerLoss=0;
        if(has){
            if(count>=MAX_EXTRA)throw new IOException("candidate at capacity");
            next=Node.read(in);
            trained=in.readInt();held=in.readInt();
            originalLoss=in.readDouble();challengerLoss=in.readDouble();
            if(trained<0||held<0||!Double.isFinite(originalLoss)||
               !Double.isFinite(challengerLoss)||originalLoss<0||challengerLoss<0||
               trained>10000000||held>10000000)throw new IOException("candidate fitness");
        }
        grown.clear();grown.addAll(restored);
        candidate=next;candidateTrain=trained;candidateValidation=held;
        baselineLoss=originalLoss;candidateLoss=challengerLoss;
        trainingEvents=t;validationEvents=v;lastAttemptTraining=last;
        attempts=tries;rejections=rejected;lastAcceptedGain=gain;
    }
    public void copyFrom(NeurogenesisV22 other){
        try{
            ByteArrayOutputStream b=new ByteArrayOutputStream();
            DataOutputStream d=new DataOutputStream(b);other.write(d);d.flush();
            DataInputStream in=new DataInputStream(new ByteArrayInputStream(b.toByteArray()));
            if(in.readInt()!=MAGIC)throw new IOException("neuron schema");
            read(in);
        }catch(IOException ex){throw new IllegalStateException(ex);}
    }
}
