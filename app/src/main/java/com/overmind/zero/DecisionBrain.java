package com.overmind.zero;

import java.util.*;

/**
 * ZERO-006: an initially small contextual value network that can grow one
 * hidden neuron only after replay-based generalization tests.
 *
 * Structure and training rules are human-designed; the network learns its
 * weights from observed consequences. No language or unearned knowledge.
 */
public final class DecisionBrain {
    public static final int INPUTS=7,BASE_HIDDEN=10,MAX_HIDDEN=24;
    public static final int PARAMS=INPUTS*BASE_HIDDEN+2*BASE_HIDDEN+1;
    private static final float LR=.025f;
    private final Random rnd;
    private final long seed;
    private int hidden=BASE_HIDDEN;
    private float[] weights;
    private float[] initialWeights;
    private final int[] encountered=new int[UnknownWorld.SIGNATURES];
    private final ArrayList<Sample> replay=new ArrayList<>();
    private float[] pending;
    private UnknownWorld.Entity intended;
    public int decisions,updates,explorations,abandoned;
    public int growthAttempts,growthAccepted,lastGrowthUpdate;
    public float lastPrediction,lastReward,averageError;
    public float lastGrowthGain=0f;
    private float plateauCheckpoint=Float.NaN;
    public static final class Sample {
        final float[] x;final float reward;
        Sample(float[] x,float reward){this.x=x.clone();this.reward=reward;}
    }
    public DecisionBrain(long seed){
        this.seed=seed;rnd=new Random(seed);
        weights=new float[parameterCount()];
        for(int i=0;i<weights.length;i++)weights[i]=(float)(rnd.nextGaussian()*.13);
        initialWeights=weights.clone();
    }
    public synchronized int hiddenCount(){return hidden;}
    public synchronized int parameterCount(){return INPUTS*hidden+2*hidden+1;}
    public synchronized float[] snapshot(){return weights.clone();}
    /** Values when each weight was created (not a pretend all-zero baseline). */
    public synchronized float[] initialSnapshot(){return initialWeights.clone();}
    public synchronized int[] encounters(){return encountered.clone();}
    public synchronized float parameterDrift(){
        float sum=0;
        for(int i=0;i<weights.length;i++)sum+=Math.abs(weights[i]-initialWeights[i]);
        return sum/Math.max(1,weights.length);
    }
    public synchronized int totalParametersEverAdded(){return 9*growthAccepted;}
    public synchronized int recentSamples(){return replay.size();}
    static float clamp(float v,float a,float b){return Math.max(a,Math.min(b,v));}
    public float[] senses(UnknownWorld.Entity e,float cx,float cy,float energy){
        float[] f=new float[INPUTS];
        if(e.signature>=0&&e.signature<3)f[e.signature]=1;
        f[3]=clamp((float)Math.hypot(e.x-cx,e.y-cy)/400,0,1.6f);
        f[4]=clamp(energy/100f,0,1);
        f[5]=1f/(float)Math.sqrt(1+encountered[Math.max(0,Math.min(2,e.signature))]);
        f[6]=1;
        return f;
    }
    private static float predict(float[] w,int hCount,float[] x,float[] activation){
        int inputEnd=INPUTS*hCount,outputStart=inputEnd+hCount;
        for(int j=0;j<hCount;j++){
            float z=w[inputEnd+j];
            for(int i=0;i<INPUTS;i++)z+=w[j*INPUTS+i]*x[i];
            activation[j]=(float)Math.tanh(z);
        }
        float result=w[w.length-1];
        for(int j=0;j<hCount;j++)result+=w[outputStart+j]*activation[j];
        return clamp(result,-3,3);
    }
    private float predict(float[] x){return predict(weights,hidden,x,new float[hidden]);}
    public synchronized float estimate(UnknownWorld.Entity e,float x,float y,float energy){
        return predict(senses(e,x,y,energy));
    }
    public synchronized UnknownWorld.Entity choose(ArrayList<UnknownWorld.Entity> objects,float cx,float cy,float reserve){
        ArrayList<UnknownWorld.Entity> candidates=new ArrayList<>();
        for(UnknownWorld.Entity e:objects){
            if(e.ready()&&Math.hypot(e.x-cx,e.y-cy)<=620)candidates.add(e);
        }
        if(candidates.isEmpty()){pending=null;intended=null;return null;}
        float epsilon=Math.max(.07f,.38f*(float)Math.exp(-updates/90f));
        UnknownWorld.Entity selected=null;
        if(rnd.nextFloat()<epsilon){
            selected=candidates.get(rnd.nextInt(candidates.size()));
            explorations++;
        }else{
            float best=-Float.MAX_VALUE;
            for(UnknownWorld.Entity e:candidates){
                float v=estimate(e,cx,cy,reserve);
                if(v>best){best=v;selected=e;}
            }
        }
        intended=selected;pending=senses(selected,cx,cy,reserve);
        lastPrediction=predict(pending);decisions++;
        return selected;
    }
    public synchronized void learn(UnknownWorld.Entity e,float before,float after,float displacement){
        if(e==null||e.signature<0||e.signature>=encountered.length)return;
        float novelty=1f/(float)Math.sqrt(1+encountered[e.signature]);
        float[] context=(e==intended&&pending!=null)
            ?pending.clone():senses(e,0,0,before);
        encountered[e.signature]++;
        float reward=clamp((after-before)/22f,-1.5f,1.5f)
            +clamp(displacement/55f,0,.35f)+.13f*novelty;
        learnSample(context,reward);
        if(e==intended){pending=null;intended=null;}
        lastReward=reward;
    }
    public synchronized void abandon(){
        if(pending!=null){
            learnSample(pending,-.10f);
            abandoned++;
        }
        pending=null;intended=null;
    }
    private void learnSample(float[] x,float reward){
        updateWeights(weights,hidden,x,reward,LR);
        if(replay.size()==48)replay.remove(0);
        replay.add(new Sample(x,reward));
        updates++;
        maybeGrow();
    }
    private void updateWeights(float[] w,int hCount,float[] x,float reward,float lr){
        float[] h=new float[hCount];
        float estimation=predict(w,hCount,x,h);
        float error=clamp(reward-estimation,-2,2);
        int biases=INPUTS*hCount,outputs=biases+hCount;
        float[] previousOutput=new float[hCount];
        for(int j=0;j<hCount;j++)previousOutput[j]=w[outputs+j];
        for(int j=0;j<hCount;j++){
            float signal=clamp(error*previousOutput[j]*(1-h[j]*h[j]),-1.5f,1.5f);
            w[biases+j]=clamp(w[biases+j]+lr*signal,-4,4);
            for(int i=0;i<INPUTS;i++){
                int k=j*INPUTS+i;
                w[k]=clamp(w[k]+lr*signal*x[i],-4,4);
            }
            w[outputs+j]=clamp(w[outputs+j]+lr*error*h[j],-4,4);
        }
        w[w.length-1]=clamp(w[w.length-1]+lr*error,-4,4);
        averageError=updates==0?Math.abs(error):averageError*.96f+Math.abs(error)*.04f;
    }

    /**
     * Start new neuron with zero output weight, so every prediction of the
     * grown network is identical to its predecessor before further training.
     */
    private static float[] expanded(float[] old,int oldHidden,float[] connections){
        int next=oldHidden+1;
        float[] w=new float[INPUTS*next+2*next+1];
        System.arraycopy(old,0,w,0,INPUTS*oldHidden);
        for(int i=0;i<INPUTS;i++)w[INPUTS*oldHidden+i]=connections[i];
        System.arraycopy(old,INPUTS*oldHidden,w,INPUTS*next,oldHidden);
        w[INPUTS*next+oldHidden]=connections[INPUTS];
        System.arraycopy(old,INPUTS*oldHidden+oldHidden,w,INPUTS*next+next,oldHidden);
        w[INPUTS*next+next+oldHidden]=0f; // function-preserving
        w[w.length-1]=old[old.length-1];
        return w;
    }
    private float holdoutLoss(float[] w,int hCount){
        float error=0;int n=0;
        for(int i=0;i<replay.size();i+=4){
            Sample s=replay.get(i);
            float delta=predict(w,hCount,s.x,new float[hCount])-s.reward;
            error+=delta*delta;n++;
        }
        return error/Math.max(1,n);
    }
    private void maybeGrow(){
        if(hidden>=MAX_HIDDEN||updates<96||updates-lastGrowthUpdate<96||updates%48!=0
            ||replay.size()<40||averageError<.14f)return;
        // Only attempt growth if error has plateaued since the last window.
        if(Float.isFinite(plateauCheckpoint)&&averageError<plateauCheckpoint*.88f){
            plateauCheckpoint=averageError;
            return;
        }
        plateauCheckpoint=averageError;
        growthAttempts++;
        lastGrowthUpdate=updates;
        float baseline=holdoutLoss(weights,hidden);
        float[] neuron=new float[INPUTS+1];
        for(int i=0;i<neuron.length;i++)neuron[i]=(float)(rnd.nextGaussian()*.18);
        float[] candidate=expanded(weights,hidden,neuron);
        float[] baselineInitial=expanded(initialWeights,hidden,neuron);
        // Train ONLY the new neuron's connections/output, preserving every
        // previously learned weight. Reserve 1/4 of samples for validation.
        int newHidden=hidden+1;
        int j=hidden,bias=INPUTS*newHidden+j,out=INPUTS*newHidden+newHidden+j;
        for(int epoch=0;epoch<26;epoch++){
            for(int i=0;i<replay.size();i++){
                if(i%4==0)continue;
                Sample sample=replay.get(i);
                float[] activation=new float[newHidden];
                float predicted=predict(candidate,newHidden,sample.x,activation);
                float err=clamp(sample.reward-predicted,-2,2);
                float previous=candidate[out];
                candidate[out]=clamp(candidate[out]+.022f*err*activation[j],-4,4);
                float signal=clamp(.022f*err*previous*(1-activation[j]*activation[j]),-.05f,.05f);
                candidate[bias]=clamp(candidate[bias]+signal,-4,4);
                for(int k=0;k<INPUTS;k++)
                    candidate[j*INPUTS+k]=clamp(candidate[j*INPUTS+k]+signal*sample.x[k],-4,4);
            }
        }
        float nextLoss=holdoutLoss(candidate,newHidden);
        if(Float.isFinite(nextLoss)&&nextLoss<baseline*.992f&&baseline-nextLoss>.0004f){
            lastGrowthGain=baseline-nextLoss;
            weights=candidate;
            initialWeights=baselineInitial;
            hidden=newHidden;
            growthAccepted++;
        }else lastGrowthGain=0;
    }
    public synchronized String serialize(){
        // v2 is backward-compatible with the v1 weights saved by v0.5.
        StringBuilder b=new StringBuilder("v2");
        b.append('|').append(hidden).append('|').append(decisions).append('|').append(updates)
         .append('|').append(explorations).append('|').append(abandoned)
         .append('|').append(averageError).append('|').append(growthAttempts)
         .append('|').append(growthAccepted).append('|').append(lastGrowthUpdate)
         .append('|').append(lastGrowthGain);
        for(int c:encountered)b.append('|').append(c);
        for(float v:weights)b.append('|').append(v);
        for(float v:initialWeights)b.append('|').append(v);
        return b.toString();
    }
    public synchronized boolean restore(String data){
        if(data==null||data.isEmpty())return false;
        String[] parts=data.split("\\|");
        try{
            if(parts.length>0&&"v1".equals(parts[0])){
                if(parts.length!=1+5+encountered.length+PARAMS)return false;
                int[] values=new int[5];
                for(int i=0;i<4;i++)values[i]=Integer.parseInt(parts[1+i]);
                float error=Float.parseFloat(parts[5]);
                int[] seen=new int[encountered.length];
                for(int i=0;i<seen.length;i++)seen[i]=Integer.parseInt(parts[6+i]);
                float[] learned=new float[PARAMS];
                for(int i=0;i<PARAMS;i++)learned[i]=Float.parseFloat(parts[6+seen.length+i]);
                if(!valid(values,error,seen,learned,initialWeights))return false;
                decisions=values[0];updates=values[1];explorations=values[2];abandoned=values[3];
                averageError=error;System.arraycopy(seen,0,encountered,0,seen.length);
                hidden=BASE_HIDDEN;weights=learned;growthAttempts=0;growthAccepted=0;
                lastGrowthUpdate=updates;lastGrowthGain=0;replay.clear();
                return true;
            }
            if(parts.length==0||!"v2".equals(parts[0]))return false;
            int h=Integer.parseInt(parts[1]);
            if(h<BASE_HIDDEN||h>MAX_HIDDEN)return false;
            int count=INPUTS*h+2*h+1;
            if(parts.length!=11+encountered.length+2*count)return false;
            int d=Integer.parseInt(parts[2]),u=Integer.parseInt(parts[3]);
            int explorationsVal=Integer.parseInt(parts[4]),ab=Integer.parseInt(parts[5]);
            float error=Float.parseFloat(parts[6]);
            int attempts=Integer.parseInt(parts[7]),accepted=Integer.parseInt(parts[8]);
            int last=Integer.parseInt(parts[9]);float gain=Float.parseFloat(parts[10]);
            int[] seen=new int[encountered.length];
            for(int i=0;i<seen.length;i++)seen[i]=Integer.parseInt(parts[11+i]);
            float[] current=new float[count],origin=new float[count];
            int from=11+seen.length;
            for(int i=0;i<count;i++)current[i]=Float.parseFloat(parts[from+i]);
            for(int i=0;i<count;i++)origin[i]=Float.parseFloat(parts[from+count+i]);
            if(!valid(new int[]{d,u,explorationsVal,ab},error,seen,current,origin)
                ||attempts<0||accepted<0||accepted>attempts||accepted!=h-BASE_HIDDEN
                ||last<0||last>u||!Float.isFinite(gain))return false;
            hidden=h;weights=current;initialWeights=origin;
            decisions=d;updates=u;explorations=explorationsVal;abandoned=ab;
            averageError=error;growthAttempts=attempts;growthAccepted=accepted;
            lastGrowthUpdate=last;lastGrowthGain=gain;
            System.arraycopy(seen,0,encountered,0,seen.length);
            pending=null;intended=null;replay.clear();
            return true;
        }catch(RuntimeException e){return false;}
    }
    private static boolean valid(int[] v,float error,int[] seen,float[] weights,float[] initial){
        for(int i:v)if(i<0)return false;
        if(!Float.isFinite(error)||error<0||false)return false;
        for(int i:seen)if(i<0)return false;
        for(float f:weights)if(!Float.isFinite(f)||Math.abs(f)>5)return false;
        for(float f:initial)if(!Float.isFinite(f)||Math.abs(f)>5)return false;
        return true;
    }
    public static void main(String[] args){
        ArrayList<UnknownWorld.Entity> stimuli=new ArrayList<>();
        for(int i=0;i<3;i++)stimuli.add(new UnknownWorld.Entity(90,10*i,i,i));
        DecisionBrain trained=new DecisionBrain(918);
        String v1Fixture=legacyFixture(trained);
        for(int t=0;t<900;t++){
            UnknownWorld.Entity e=stimuli.get(t%3);
            trained.learn(e,35,35+(e.signature==0?22:e.signature==1?-18:0),e.signature==2?13:0);
        }
        float positive=trained.estimate(stimuli.get(0),0,0,35);
        float negative=trained.estimate(stimuli.get(1),0,0,35);
        float neutral=trained.estimate(stimuli.get(2),0,0,35);
        if(!(positive>negative+.6f&&positive>neutral+.35f&&neutral>negative+.25f))
            throw new IllegalStateException("Decision training failed");
        DecisionBrain saved=new DecisionBrain(123);
        if(!saved.restore(trained.serialize())||saved.hidden!=trained.hidden
           ||saved.updates!=900||Math.abs(saved.estimate(stimuli.get(0),0,0,35)-positive)>.0001f)
            throw new IllegalStateException("v2 persistence failed");
        float[] unchanged=saved.snapshot();
        if(saved.restore("damaged|weights")||!Arrays.equals(unchanged,saved.snapshot()))
            throw new IllegalStateException("Corrupt state accepted");
        DecisionBrain migrated=new DecisionBrain(918);
        if(!migrated.restore(v1Fixture)||migrated.hidden!=10
             ||migrated.updates!=12||migrated.initialSnapshot().length!=91)
            throw new IllegalStateException("v1 migration failed");
        float[] proposed=expanded(migrated.snapshot(),10,new float[8]);
        float[] sample={0,1,0,.1f,.9f,.6f,1};
        float before=predict(migrated.weights,10,sample,new float[10]);
        float after=predict(proposed,11,sample,new float[11]);
        if(Math.abs(before-after)>.00001f||proposed.length!=100)
            throw new IllegalStateException("New neuron did not preserve past policy");
        System.out.printf(Locale.US,
           "ZERO-006 tests passed: base=%d actual=%d neurons=%d learned utility positive=%.3f neutral=%.3f negative=%.3f migration=v1->v2 preserved=true%n",
           PARAMS,trained.parameterCount(),trained.hiddenCount(),positive,neutral,negative);
    }
    private static String legacyFixture(DecisionBrain b){
        StringBuilder s=new StringBuilder("v1|4|12|2|1|0.8|3|2|1");
        for(float v:b.weights)s.append('|').append(v);
        return s.toString();
    }
}
