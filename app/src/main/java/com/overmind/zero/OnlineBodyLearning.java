package com.overmind.zero;

import java.util.Random;

/**
 * Small mutations of the body's previously learned weights tested exclusively
 * in ZERO's LIVED game world, not in hidden directional fitness simulations.
 * Comparison is between adjacent observational windows, matched for hunger,
 * presence of the room and approximate directional activity. The outcome
 * is an experimental heuristic, NOT a controlled causal A/B test.
 */
public final class OnlineBodyLearning {
    public static final int MOTOR=0,MANIPULATION=1,MEMBRANE=2;
    public static final int WINDOW=8;
    private static final float MIN_GAIN=.025f;
    private final Random rnd=new Random();
    private int domain=0,phase=0,samples=0;
    private float accumulated=0,baseScore=0,energyTotal=0,dirX=0,dirY=0;
    private float baseEnergy=0,baseDirX=0,baseDirY=0;
    private boolean baseRoom=false;
    private boolean roomInWindow=false,roomStarted=false,roomChanged=false;
    private float[] candidate;

    public static final class Result {
        public final int domain;
        public final boolean accepted;
        public final float[] weights;
        Result(int domain,boolean accepted,float[] weights){
            this.domain=domain;this.accepted=accepted;this.weights=weights;
        }
    }
    public float[] weights(int selected,float[] saved){
        return phase==1&&selected==domain&&candidate!=null?candidate:saved;
    }
    public void abortTrial(){
        phase=0;samples=0;accumulated=0;energyTotal=0;dirX=0;dirY=0;
        candidate=null;roomChanged=false;roomStarted=false;roomInWindow=false;
    }
    private static float[] mutate(float[] original,int which,boolean room,Random rnd){
        float[] changed=original.clone();
        int count=1+rnd.nextInt(3);
        for(int i=0;i<count;i++){
            // Only physical actuators are explored: no "open the gate" command.
            int k=(which==MEMBRANE&&room&&rnd.nextFloat()<.6f)
                  ?Morphology.LEGACY_PARAMS+rnd.nextInt(Morphology.GRIP_HEAD)
                  :rnd.nextInt(changed.length);
            float scale=which==MOTOR?.06f:which==MANIPULATION?.09f:.07f;
            changed[k]=Math.max(-3.0f,Math.min(3.0f,
                       changed[k]+(float)rnd.nextGaussian()*scale));
        }
        return changed;
    }
    /** One completed action (~0.65 simulated seconds), with real effects. */
    public Result observe(float reward,float traveled,int contacts,float energy,
                          boolean room,float directionX,float directionY,
                          float[] motor,float[] hand,float[] shape){
        if(!Float.isFinite(reward)||!Float.isFinite(traveled)||
           !Float.isFinite(energy)||!Float.isFinite(directionX)||
           !Float.isFinite(directionY))return null;
        if(samples==0){roomStarted=room;roomChanged=false;}
        if(roomStarted!=room)roomChanged=true;
        roomInWindow=room;
        // Generic bodily feedback, never an encoded target coordinate or
        // privileged gate state: reward comes from the sensorimotor agent.
        accumulated+=reward+.002f*Math.min(45,Math.max(0,traveled))
                           +.045f*Math.min(3,Math.max(0,contacts));
        energyTotal+=energy;
        dirX+=directionX;dirY+=directionY;
        if(++samples<WINDOW)return null;
        float score=accumulated/WINDOW,avgEnergy=energyTotal/WINDOW;
        float avgX=dirX/WINDOW,avgY=dirY/WINDOW;
        boolean stableRoom=!roomChanged;
        samples=0;accumulated=0;energyTotal=0;dirX=0;dirY=0;
        if(phase==0){
            baseScore=score;baseEnergy=avgEnergy;
            baseDirX=avgX;baseDirY=avgY;baseRoom=room;
            phase=1;
            candidate=mutate(domain==MOTOR?motor:domain==MANIPULATION?hand:shape,
                             domain,room,rnd);
            return null;
        }
        // If the world/energy/direction was too different, do not claim
        // improvement: reject the live experiment and try again later.
        boolean comparable=stableRoom&&baseRoom==room&&
            Math.abs(baseEnergy-avgEnergy)<=28f&&
            Math.hypot(baseDirX-avgX,baseDirY-avgY)<=.85;
        boolean accepted=comparable&&score>baseScore+MIN_GAIN;
        Result outcome=new Result(domain,accepted,accepted?candidate:null);
        domain=(domain+1)%3;
        abortTrial();
        return outcome;
    }
    public static void main(String[] args){
        float[] motor=MotorBrain.virgin(new Random(13));
        float[] hand=ContactLearning.virgin();
        float[] shape=new float[Morphology.PARAMS];
        OnlineBodyLearning b=new OnlineBodyLearning();
        if(b.weights(MOTOR,motor)!=motor)throw new AssertionError("Unexpected candidate");
        for(int i=0;i<WINDOW;i++)b.observe(0,0,0,70,false,1,0,motor,hand,shape);
        if(b.weights(MOTOR,motor)==motor||b.weights(MANIPULATION,hand)!=hand)
            throw new AssertionError("Candidate should run only in its own domain");
        Result result=null;
        for(int i=0;i<WINDOW;i++)result=b.observe(-1,0,0,70,false,1,0,motor,hand,shape);
        if(result==null||result.accepted||b.weights(MOTOR,motor)!=motor)
            throw new AssertionError("Failed experiment not rolled back");
        for(int i=0;i<WINDOW;i++)b.observe(0,0,0,70,true,1,0,motor,hand,shape);
        for(int i=0;i<WINDOW;i++)result=b.observe(.5f,15,2,69,true,1,0,motor,hand,shape);
        if(result==null||!result.accepted||result.domain!=MANIPULATION||
           result.weights.length!=hand.length)
            throw new AssertionError("Lived improvement was not accepted");
        if(shape[0]!=0||motor.length!=MotorBrain.PARAMS)
            throw new AssertionError("Saved weights were changed without acceptance");
        for(int i=0;i<WINDOW;i++)b.observe(0,0,0,70,true,1,0,motor,hand,shape);
        for(int i=0;i<WINDOW;i++)result=b.observe(1,10,0,30,false,-1,0,motor,hand,shape);
        if(result==null||result.accepted)
            throw new AssertionError("Different worlds must not imply improvement");
        System.out.println("ZERO-009.2: lived controller experiments, non-destructive rollback, matched-context guard passed");
    }
}
