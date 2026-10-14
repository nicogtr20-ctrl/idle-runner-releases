package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * Learnable distributed motor readout OF the sole RecurrentCoreV22 controller.
 * Not an action chooser: the 2.2's selected action is the only intent.
 * Sixteen anonymous membrane sectors have four differentiable outputs each:
 * pressure bias, tangential drive, voluntary local adhesion, cortex tension.
 * No target/entity identity or scripted pseudopod command.
 */
public final class SymbioteMotorHeadV1 {
    public static final String KEY="symbiote_motor_head_v1";
    private static final int N=SymbioteMatterV1.SECTORS,CHANNELS=SymbioteMatterV1.OUTPUTS;
    private static final int FEATURE=14,OUTPUTS=N*CHANNELS;
    private final float[] weights=new float[CHANNELS*FEATURE];
    private final float[] feature=new float[N*FEATURE],commands=new float[OUTPUTS];
    private final float[] noise=new float[OUTPUTS],pending=new float[OUTPUTS];
    private float[] previousState;
    private int lastAction=-1;
    private long seed=0x3910b15dceef41L,choices,updates,heldout;
    private float meanReward=0f;
    public SymbioteMotorHeadV1(){
        // Small unbiased initial pathways. This is not a gait or hand recipe.
        for(int i=0;i<weights.length;i++)weights[i]=random()*.056f;
    }
    private static float clip(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    private float random(){
        seed^=seed<<13;seed^=seed>>>7;seed^=seed<<17;
        return ((seed>>>40)/16777216f-.5f)*2;
    }
    private static boolean valid(float[] a,int n){
        if(a==null||a.length!=n)return false;
        for(float v:a)if(!Float.isFinite(v)||Math.abs(v)>5)return false;
        return true;
    }
    /** The 96×9 patch map is grounded exclusively in local touch, strain,
     * velocity, shape and optic observations. 164D state provides generic
     * interoceptive context. The action's angle is a bodily heading cue,
     * not a named object location or an automatic chosen "arm".
     */
    public void chosen(int action,float[] bodySensors,float[] recurrent,
                       float exploration){
        if(action<0||action>=RecurrentCoreV22.A||
           !valid(recurrent,RecurrentCoreV22.Z)||
           bodySensors==null||bodySensors.length!=DistributedBodySense.FEATURES)
            return;
        choices++;
        lastAction=action;
        previousState=recurrent.clone();
        float goal=action%8*(float)Math.PI/4f;
        for(int sector=0;sector<N;sector++){
            float a=(sector+.5f)*(float)Math.PI*2/N;
            int base=sector*FEATURE;
            for(int j=0;j<DistributedBodySense.CHANNELS;j++){
                float value=0;
                for(int k=0;k<Morphology.POINTS/N;k++)
                    value+=bodySensors[(sector*(Morphology.POINTS/N)+k)*
                        DistributedBodySense.CHANNELS+j]/(Morphology.POINTS/N);
                feature[base+j]=clip(value,-1,1);
            }
            feature[base+9]=(float)Math.cos(a-goal);
            feature[base+10]=(float)Math.sin(a-goal);
            feature[base+11]=clip(recurrent[0],0,1);
            feature[base+12]=clip(recurrent[RecurrentCoreV22.F+sector%
                                    RecurrentCoreV22.H],-1,1);
            feature[base+13]=1;
            for(int channel=0;channel<CHANNELS;channel++){
                float z=0;
                for(int j=0;j<FEATURE;j++)z+=weights[channel*FEATURE+j]*feature[base+j];
                int at=sector*CHANNELS+channel;
                float explore=clip(exploration,0,1)*.48f;
                noise[at]=random()*explore;
                // Exploration is localized and smooth enough not to make a
                // single 96-node patch teleport relative to its neighbours.
                // Actuator gain makes newborn cortex commands physically usable.
                // The active 2.2 action bank 8..15 explicitly requests grip;
                // action%8 alone in OTA143 had discarded this motor intention.
                float gripIntent=channel==2&&action>=8?.55f:0f;
                commands[at]=(float)Math.tanh(2.8f*z+1.4f*noise[at]+gripIntent);
                pending[at]=commands[at];
            }
        }
    }
    public float[] commands(){return commands;}
    /**
     * Only lived reserve gained/lost, real injury, and measured effort train
     * the downstream neural motor channel. Heldout intervals cannot train.
     * REINFORCE with an updated reward baseline; no imagined success data.
     */
    public void consequence(float actualContactEnergy,float actualDamage,
                            float effort,boolean train,boolean holdout){
        if(lastAction<0)return;
        if(!Float.isFinite(actualContactEnergy)||!Float.isFinite(actualDamage)||
           !Float.isFinite(effort)||actualDamage<0||effort<0){lastAction=-1;return;}
        float reward=clip(actualContactEnergy/35f-actualDamage/90f-.0008f*effort,-2,2);
        float advantage=clip(reward-meanReward,-2,2);
        if(holdout)heldout++;
        else if(train){
            float lr=.0018f;
            float[] gradient=new float[weights.length];
            for(int sector=0;sector<N;sector++){
                for(int channel=0;channel<CHANNELS;channel++){
                    int at=sector*CHANNELS+channel;
                    float derivative=1-pending[at]*pending[at];
                    // Use the actual bounded exploratory perturbation as an
                    // unbiased score-function direction, not privileged info.
                    float g=lr*advantage*noise[at]*derivative/N;
                    int base=sector*FEATURE;
                    for(int j=0;j<FEATURE;j++)
                        gradient[channel*FEATURE+j]+=g*feature[base+j];
                }
            }
            for(int i=0;i<weights.length;i++)
                weights[i]=clip(weights[i]+gradient[i],-2.5f,2.5f);
            meanReward=meanReward*.995f+reward*.005f;
            updates++;
        }
        lastAction=-1;
    }
    public void resetLife(){lastAction=-1;Arrays.fill(commands,0);Arrays.fill(noise,0);}
    public void resumeLife(){resetLife();}
    public String summary(){return String.format(Locale.US,
        "SYMBIOTE cortex motor head: decisions=%d trained=%d heldout=%d",choices,updates,heldout);}
    /** Compact backward-compatible schema, no side effects on malformed data. */
    public String encode(){
        StringBuilder s=new StringBuilder("smh1").append('|').append(seed)
            .append('|').append(choices).append('|').append(updates)
            .append('|').append(heldout).append('|').append(meanReward);
        for(float w:weights)s.append('|').append(w);
        return s.toString();
    }
    public boolean restore(String text){
        if(text==null||text.length()>12000||!text.startsWith("smh1|"))return false;
        try{
            String[] p=text.split("\\|",-1);
            if(p.length!=6+weights.length)return false;
            long newSeed=Long.parseLong(p[1]);
            long decisions=Long.parseLong(p[2]),learnt=Long.parseLong(p[3]),hold=Long.parseLong(p[4]);
            float baseline=Float.parseFloat(p[5]);
            if(decisions<0||learnt<0||hold<0||learnt>decisions||
               hold>decisions||learnt+hold>decisions||!Float.isFinite(baseline)||
               Math.abs(baseline)>2.01f)return false;
            float[] newWeights=new float[weights.length];
            for(int i=0;i<weights.length;i++){
                newWeights[i]=Float.parseFloat(p[6+i]);
                if(!Float.isFinite(newWeights[i])||Math.abs(newWeights[i])>2.501f)return false;
            }
            seed=newSeed;choices=decisions;updates=learnt;heldout=hold;meanReward=baseline;
            System.arraycopy(newWeights,0,weights,0,weights.length);
            resetLife();return true;
        }catch(RuntimeException e){return false;}
    }
}
