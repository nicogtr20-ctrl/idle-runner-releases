package com.overmind.zero;

import java.util.*;

/**
 * ZERO-005: on-device, online-trained neural value estimator for deciding which
 * unknown stimulus to inspect next. No LLM/pretrained vocabulary or labels
 * describing objects as "energy", "danger", or "movable".
 *
 * Input: one-hot raw sensory signature, distance, current reserve, exposure
 * count and a bias. Output: predicted utility of an attempted interaction.
 * A small epsilon-greedy policy tests alternatives. Weights update only
 * following actual observed outcomes, not from scripted destination labels.
 *
 * This is a contextual-bandit learner, not an AGI or general world model.
 */
public final class DecisionBrain {
    public static final int INPUTS=7,HIDDEN=10,PARAMS=INPUTS*HIDDEN+HIDDEN+HIDDEN+1;
    private static final float LR=.025f;
    private final Random rnd;
    private final float[] weights=new float[PARAMS];
    private final int[] encountered=new int[UnknownWorld.SIGNATURES];
    private float[] pending;
    private UnknownWorld.Entity intended;
    public int decisions,updates,explorations,abandoned;
    public float lastPrediction,lastReward,averageError;

    public DecisionBrain(long seed){
        rnd=new Random(seed);
        // Random and symmetric around zero: NO baked-in object preferences.
        for(int i=0;i<weights.length;i++){
            weights[i]=(float)(rnd.nextGaussian()*.13);
        }
    }
    public float[] snapshot(){return weights.clone();}
    public int[] encounters(){return encountered.clone();}
    static float clamp(float x,float low,float high){return Math.max(low,Math.min(high,x));}
    public float[] senses(UnknownWorld.Entity e,float cx,float cy,float reserve){
        float[] f=new float[INPUTS];
        if(e.signature>=0&&e.signature<3)f[e.signature]=1f;
        f[3]=clamp((float)Math.hypot(e.x-cx,e.y-cy)/400f,0,1.6f);
        f[4]=clamp(reserve/100f,0,1);
        f[5]=1f/(float)Math.sqrt(1+encountered[Math.max(0,Math.min(2,e.signature))]);
        f[6]=1f;
        return f;
    }
    private float predict(float[] inputs,float[] h){
        for(int j=0;j<HIDDEN;j++){
            float z=weights[INPUTS*HIDDEN+j];
            for(int i=0;i<INPUTS;i++)z+=weights[j*INPUTS+i]*inputs[i];
            h[j]=(float)Math.tanh(z);
        }
        float out=weights[PARAMS-1];
        for(int j=0;j<HIDDEN;j++)
            out+=weights[INPUTS*HIDDEN+HIDDEN+j]*h[j];
        return clamp(out,-3f,3f);
    }
    public float estimate(UnknownWorld.Entity e,float x,float y,float energy){
        return predict(senses(e,x,y,energy),new float[HIDDEN]);
    }
    public UnknownWorld.Entity choose(ArrayList<UnknownWorld.Entity> objects,float x,float y,float reserve) {
        ArrayList<UnknownWorld.Entity> candidates=new ArrayList<>();
        for(UnknownWorld.Entity e:objects){
            if(!e.ready())continue;
            if(Math.hypot(e.x-x,e.y-y)>620)continue;
            candidates.add(e);
        }
        if(candidates.isEmpty()){intended=null;pending=null;return null;}
        // Exploration is a learning algorithm parameter, not a programmed
        // preference for a particular object class.
        float epsilon=Math.max(.07f,.38f*(float)Math.exp(-updates/90f));
        boolean explore=rnd.nextFloat()<epsilon;
        UnknownWorld.Entity chosen=null;
        if(explore){
            chosen=candidates.get(rnd.nextInt(candidates.size()));
            explorations++;
        }else{
            float high=-Float.MAX_VALUE;
            for(UnknownWorld.Entity e:candidates){
                float q=estimate(e,x,y,reserve);
                if(q>high){high=q;chosen=e;}
            }
        }
        intended=chosen;
        pending=senses(chosen,x,y,reserve);
        lastPrediction=predict(pending,new float[HIDDEN]);
        decisions++;
        return chosen;
    }

    public void learn(UnknownWorld.Entity e,float energyBefore,float energyAfter,float displacement){
        if(e==null)return;
        final int sig=e.signature;
        if(sig<0||sig>=encountered.length)return;
        float oldNovelty=1f/(float)Math.sqrt(1+encountered[sig]);
        encountered[sig]++;
        // Only physiological change, discovered material displacement and the
        // actual surprise of novel observations enter this feedback. The
        // learner never receives the environment's private law or category.
        float reward=clamp((energyAfter-energyBefore)/22f,-1.5f,1.5f)
            +clamp(displacement/55f,0,.35f)
            +.13f*oldNovelty;
        // Update the selected action with its original context, or learn an
        // incidental interaction using the current sensors as weak evidence.
        float[] features=(e==intended&&pending!=null)
            ?pending
            :senses(e,0,0,energyBefore);
        update(features,reward);
        if(e==intended){intended=null;pending=null;}
        lastReward=reward;
    }
    public void abandon(){
        if(pending!=null){
            // Failed attempts waste time and physiological energy, but do not
            // assert that the unknown target itself is poisonous.
            update(pending,-.10f);
            abandoned++;
        }
        pending=null;intended=null;
    }
    private void update(float[] x,float reward){
        float[] h=new float[HIDDEN];
        float prediction=predict(x,h);
        float error=clamp(reward-prediction,-2,2);
        float[] outputOld=new float[HIDDEN];
        int outBase=INPUTS*HIDDEN+HIDDEN;
        for(int j=0;j<HIDDEN;j++)outputOld[j]=weights[outBase+j];
        // SGD backprop on squared prediction error with gradient clipping.
        for(int j=0;j<HIDDEN;j++){
            float signal=clamp(error*outputOld[j]*(1-h[j]*h[j]),-1.5f,1.5f);
            weights[INPUTS*HIDDEN+j]=clamp(weights[INPUTS*HIDDEN+j]+LR*signal,-4,4);
            for(int i=0;i<INPUTS;i++){
                int index=j*INPUTS+i;
                weights[index]=clamp(weights[index]+LR*signal*x[i],-4,4);
            }
            weights[outBase+j]=clamp(weights[outBase+j]+LR*error*h[j],-4,4);
        }
        weights[PARAMS-1]=clamp(weights[PARAMS-1]+LR*error,-4,4);
        averageError=updates==0?Math.abs(error):averageError*.96f+Math.abs(error)*.04f;
        updates++;
    }
    public String serialize(){
        StringBuilder out=new StringBuilder("v1");
        out.append('|').append(decisions).append('|').append(updates)
           .append('|').append(explorations).append('|').append(abandoned)
           .append('|').append(averageError);
        for(int k:encountered)out.append('|').append(k);
        for(float w:weights)out.append('|').append(w);
        return out.toString();
    }
    public boolean restore(String content){
        if(content==null||content.isEmpty())return false;
        String[] parts=content.split("\\|");
        if(parts.length!=1+5+encountered.length+PARAMS||!"v1".equals(parts[0]))
            return false;
        try{
            int d=Integer.parseInt(parts[1]),u=Integer.parseInt(parts[2]);
            int exp=Integer.parseInt(parts[3]),ab=Integer.parseInt(parts[4]);
            float err=Float.parseFloat(parts[5]);
            int[] counts=new int[encountered.length];
            float[] learned=new float[PARAMS];
            for(int i=0;i<counts.length;i++)counts[i]=Integer.parseInt(parts[6+i]);
            for(int i=0;i<learned.length;i++)
                learned[i]=Float.parseFloat(parts[6+counts.length+i]);
            if(d<0||u<0||exp<0||ab<0||!Float.isFinite(err))return false;
            for(int c:counts)if(c<0)return false;
            for(float w:learned)if(!Float.isFinite(w)||Math.abs(w)>5)return false;
            decisions=d;updates=u;explorations=exp;abandoned=ab;averageError=err;
            System.arraycopy(counts,0,encountered,0,counts.length);
            System.arraycopy(learned,0,weights,0,weights.length);
            pending=null;intended=null;
            return true;
        }catch(RuntimeException e){return false;}
    }
    public static void main(String[] args){
        ArrayList<UnknownWorld.Entity> stimuli=new ArrayList<>();
        for(int i=0;i<3;i++)stimuli.add(new UnknownWorld.Entity(90,10*i,i,i));
        DecisionBrain naive=new DecisionBrain(918);
        float virgin=naive.estimate(stimuli.get(0),0,0,35)
             -naive.estimate(stimuli.get(1),0,0,35);
        DecisionBrain trained=new DecisionBrain(918);
        // Synthetic outcomes are only used by CI as an environment simulator.
        // The Android brain observes actual physics instead.
        for(int t=0;t<900;t++){
            UnknownWorld.Entity e=stimuli.get(t%3);
            trained.learn(e,35f,35f+(e.signature==0?22:e.signature==1?-18:0),
                e.signature==2?13:0);
        }
        float pos=trained.estimate(stimuli.get(0),0,0,35);
        float neg=trained.estimate(stimuli.get(1),0,0,35);
        float neu=trained.estimate(stimuli.get(2),0,0,35);
        if(!(pos>neg+.6f&&pos>neu+.35f&&neu>neg+.25f))
            throw new IllegalStateException("Learned decision values not separated");
        String state=trained.serialize();
        DecisionBrain persisted=new DecisionBrain(1);
        if(!persisted.restore(state)||persisted.updates!=trained.updates||
           Math.abs(persisted.estimate(stimuli.get(0),0,0,35)-pos)>.0001f)
            throw new IllegalStateException("Decision learning persistence failed");
        float[] before=persisted.snapshot();
        if(persisted.restore("damaged|weights"))throw new IllegalStateException("Accepted damaged state");
        if(!Arrays.equals(before,persisted.snapshot()))throw new IllegalStateException("Corrupted valid weights");
        System.out.printf(Locale.US,
            "ZERO-005 neural decisions passed: parameters=%d previous_bias=%.3f learned_scores=[%.3f, %.3f, %.3f] updates=%d persistent=true%n",
            PARAMS,virgin,pos,neu,neg,persisted.updates);
    }
}
