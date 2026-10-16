package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * ZERO167: a fast trainable sensorimotor loop downstream of the ONE 2.2 brain.
 *
 * The brain alone selects one of 16 intents. This circuit has no world-object
 * coordinates, target identity, target force, prescribed waveform, clock phase
 * or gait. Its learnable recurrent membrane states can eventually develop
 * oscillations from physical feedback. Zero weights are EXACTLY neutral.
 *
 * No training ever occurs on heldout decisions. Only post-contact REAL body
 * displacement relative to the brain's own selected heading trains the
 * motor-skill readout, never the brain's value or imagined episodes.
 */
public final class FastSomaticCircuitV167 {
    public static final String KEY="somatic_fast_v167";
    static final int S=SymbioteMatterV1.SECTORS, CHANNELS=2,
        INPUTS=10, N=S*CHANNELS, PARAMETERS=N*INPUTS;
    private final float[] weights=new float[PARAMETERS];
    private final float[] state=new float[N],drift=new float[N];
    private final float[] eligibility=new float[PARAMETERS],input=new float[INPUTS],
        next=new float[N];
    private final float[] baseline=new float[RecurrentCoreV22.A];
    private final int[] baselineSamples=new int[RecurrentCoreV22.A];
    private long frames,trainings,holdouts,episodes;
    private long seed=0x16722ba5L;
    private int action=-1,intervalFrames;
    private boolean exploratoryTrial=true;
    private float netX,netY,gross,lastAgreement;
    private static float clip(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    private float random(){
        seed^=seed<<13;seed^=seed>>>7;seed^=seed<<17;
        return ((seed>>>40)/16777216f-.5f)*2f;
    }
    public int parameterCount(){return PARAMETERS;}
    public float parameterAt(int i){return weights[i];}
    /** Protected neural snapshot; never includes random motor exploration. */
    public float[] copySkillWeights(){return weights.clone();}
    public void writeSkillWeights(float[] destination,int offset){
        if(destination==null||offset<0||destination.length-offset<PARAMETERS)
            throw new IllegalArgumentException("Motor snapshot shape");
        System.arraycopy(weights,0,destination,offset,PARAMETERS);
    }
    /** Adopt ONLY verified historical synapses, not another brain decision.
     * Preserve all true trained/heldout counters and metabolic baselines. */
    public boolean installVerifiedSkill(float[] learned){
        if(learned==null||learned.length!=PARAMETERS)return false;
        for(float w:learned)if(!Float.isFinite(w)||Math.abs(w)>1.101f)return false;
        System.arraycopy(learned,0,weights,0,PARAMETERS);
        Arrays.fill(state,0f);Arrays.fill(drift,0f);Arrays.fill(eligibility,0f);
        return true;
    }
    public long trained(){return trainings;}
    public long validated(){return holdouts;}
    public long physicalFrames(){return frames;}
    public float lastDirectionalAgreement(){return lastAgreement;}
    public int activeAction(){return action;}
    /** Resets the experimental trial, not learned synaptic parameters. */
    public void chosen(int selected){chosen(selected,true);}
    /** Genuine causal motor experiments: one actuator layer explores at a time. */
    public void chosen(int selected,boolean allowExploration){
        if(selected<0||selected>=RecurrentCoreV22.A)return;
        action=selected;exploratoryTrial=allowExploration;
        intervalFrames=0;netX=netY=gross=0f;
        Arrays.fill(eligibility,0f);
        if(!allowExploration)Arrays.fill(drift,0f);
    }
    /**
     * Called BEFORE each real physical step, independently of decision rate.
     * Only radial and tangential commands can adapt. Adhesion and stiffness
     * are byte-for-byte those of the original trained cortex.
     */
    public void advance(MotorBrain.Body body,Morphology.Surface skin,
                        float[] base,float[] commands,float dt){
        if(body==null||skin==null||base==null||commands==null||
           base.length!=S*SymbioteMatterV1.OUTPUTS||
           commands.length!=base.length||!Float.isFinite(dt)||dt<=0||dt>.04f)
            throw new IllegalArgumentException("Invalid ZERO167 physics tick");
        System.arraycopy(base,0,commands,0,base.length);
        if(action<0)return;
        float theta=(action%8)*(float)Math.PI/4f;
        float goalX=(float)Math.cos(theta),goalY=(float)Math.sin(theta);
        float alpha=1f-(float)Math.exp(-5.2f*dt);
        // Reuse arrays on every ~25ms physics step; no per-frame GC.
        for(int sector=0;sector<S;sector++){
            // Local membrane patch is TRUE material; no fixed birth-angle,
            // artificial bone, imaginary central anchor or energy coordinate.
            int patch=sector*(Morphology.POINTS/S)+3;
            float dx=skin.x[patch]-body.cx,dy=skin.y[patch]-body.cy;
            float rr=(float)Math.hypot(dx,dy);
            float rx=rr>.001f?dx/rr:(float)Math.cos(sector*Math.PI*2/S);
            float ry=rr>.001f?dy/rr:(float)Math.sin(sector*Math.PI*2/S);
            float rv=(skin.vx[patch]-body.cvx)*rx+
                     (skin.vy[patch]-body.cvy)*ry;
            float tv=-(skin.vx[patch]-body.cvx)*ry+
                      (skin.vy[patch]-body.cvy)*rx;
            float contact=clip(Math.max(skin.touchSense[patch],
                skin.touchPulse[patch]),0f,1f);
            for(int ch=0;ch<CHANNELS;ch++){
                int i=sector*CHANNELS+ch,other=sector*CHANNELS+(1-ch);
                int left=((sector+S-1)%S)*CHANNELS+ch;
                int right=((sector+1)%S)*CHANNELS+ch;
                input[0]=1f;
                input[1]=clip((rr-28f)/48f,-1f,1f);
                input[2]=clip(rv/65f,-1f,1f);
                input[3]=clip(tv/65f,-1f,1f);
                input[4]=rx*goalX+ry*goalY;
                input[5]=-ry*goalX+rx*goalY;
                input[6]=contact;
                input[7]=state[i];
                input[8]=state[other];
                input[9]=.5f*(state[left]+state[right]);
                float z=0f;
                int off=i*INPUTS;
                for(int j=0;j<INPUTS;j++)z+=weights[off+j]*input[j];
                // Correlated MOTOR EXPLORATION, not a sinusoidal waveform:
                // each sector can test changes within a held brain action.
                float decay=(float)Math.pow(.92f,dt/.025f);
                float diffusion=.08f*(float)Math.sqrt((1f-decay*decay)/(1f-.92f*.92f));
                drift[i]=exploratoryTrial?
                    clip(decay*drift[i]+diffusion*random(),-.55f,.55f):0f;
                float proposed=(float)Math.tanh(z+.38f*drift[i]);
                next[i]=clip(state[i]+alpha*(proposed-state[i]),-1f,1f);
                // Eligibility of the actual random motor perturbations is
                // retained for the post-physics, independent heldout decision.
                float derivative=1f-proposed*proposed;
                for(int j=0;j<INPUTS;j++)
                    eligibility[off+j]+=drift[i]*derivative*input[j];
                // Keep established cortex muscle commands primary.
                int motor=sector*SymbioteMatterV1.OUTPUTS+ch;
                commands[motor]=clip(base[motor]+.42f*next[i],-1f,1f);
            }
        }
        System.arraycopy(next,0,state,0,N);
        intervalFrames++;frames++;
    }
    /** Real centroid delta ONLY AFTER all substrate and obstacle contacts. */
    public void observed(float beforeX,float beforeY,MotorBrain.Body now){
        if(action<0||intervalFrames<=0||now==null)return;
        float dx=now.cx-beforeX,dy=now.cy-beforeY;
        if(!Float.isFinite(dx)||!Float.isFinite(dy))return;
        float m=(float)Math.hypot(dx,dy);
        if(m>3f)return; // no teleport, respawn or invalid physics reward
        netX+=dx;netY+=dy;gross+=m;
    }
    /**
     * An experimental motor option closes exactly with the active recurrent
     * decision. Validation windows cannot update ANY learned weight/baseline.
     */
    public void consequence(boolean learn,boolean heldout){
        if(action<0)return;
        int oldAction=action;
        float theta=(oldAction%8)*(float)Math.PI/4f;
        float forward=netX*(float)Math.cos(theta)+netY*(float)Math.sin(theta);
        float score=gross>=.8f?
            clip(forward/Math.max(2f,gross),-1f,1f)*
            clip(gross/3f,0f,1f):0f;
        lastAgreement=score;
        if(heldout){holdouts++;episodes++;}
        else if(learn){
            float advantage=clip(score-baseline[oldAction],-1f,1f);
            if(intervalFrames>=8&&gross>=.8f){
                float lr=.024f/(float)Math.sqrt(1+trainings/6000f);
                for(int k=0;k<PARAMETERS;k++){
                    float gradient=clip(eligibility[k]/intervalFrames,-.65f,.65f);
                    weights[k]=clip(weights[k]+clip(lr*advantage*gradient,
                        -.002f,.002f),-1.1f,1.1f);
                }
            }
            int seen=baselineSamples[oldAction]++;
            baseline[oldAction]+= (score-baseline[oldAction])/
                Math.min(96f,seen+1f);
            trainings++;episodes++;
        }
        action=-1;exploratoryTrial=true;
        intervalFrames=0;netX=netY=gross=0f;
        Arrays.fill(eligibility,0f);
    }
    /** Death cancels current motor reflex, NEVER erases the learned weights. */
    public void resetLife(){
        action=-1;intervalFrames=0;netX=netY=gross=0;
        Arrays.fill(eligibility,0f);
        Arrays.fill(state,0f);Arrays.fill(drift,0f);
    }
    public void resumeLife(){resetLife();}
    /** Optional independent save key. No existing smh1, cl161, co162 or rc22 changes. */
    public String encode(){
        StringBuilder s=new StringBuilder("sf167|").append(frames)
            .append('|').append(trainings).append('|').append(holdouts)
            .append('|').append(episodes).append('|').append(seed);
        for(float w:weights)s.append('|').append(w);
        for(float x:baseline)s.append('|').append(x);
        for(int count:baselineSamples)s.append('|').append(count);
        return s.toString();
    }
    public boolean restore(String raw){
        if(raw==null||!raw.startsWith("sf167|")||raw.length()>24000)return false;
        try{
            String[] p=raw.split("\\|",-1);
            if(p.length!=6+PARAMETERS+2*RecurrentCoreV22.A)return false;
            long f=Long.parseLong(p[1]),t=Long.parseLong(p[2]),
                h=Long.parseLong(p[3]),e=Long.parseLong(p[4]),
                newSeed=Long.parseLong(p[5]);
            if(f<0||t<0||h<0||e<0||t+h>e||e>1000000000L||
                f>10000000000L||newSeed==0)return false;
            float[] newWeights=new float[PARAMETERS],
                    newBaseline=new float[RecurrentCoreV22.A];
            int[] samples=new int[RecurrentCoreV22.A];
            int at=6;
            for(int i=0;i<newWeights.length;i++){
                newWeights[i]=Float.parseFloat(p[at++]);
                if(!Float.isFinite(newWeights[i])||Math.abs(newWeights[i])>1.101f)
                    return false;
            }
            for(int i=0;i<newBaseline.length;i++){
                newBaseline[i]=Float.parseFloat(p[at++]);
                if(!Float.isFinite(newBaseline[i])||Math.abs(newBaseline[i])>1.001f)
                    return false;
            }
            for(int i=0;i<samples.length;i++){
                samples[i]=Integer.parseInt(p[at++]);
                if(samples[i]<0||samples[i]>t)return false;
            }
            System.arraycopy(newWeights,0,weights,0,PARAMETERS);
            System.arraycopy(newBaseline,0,baseline,0,baseline.length);
            System.arraycopy(samples,0,baselineSamples,0,samples.length);
            frames=f;trainings=t;holdouts=h;episodes=e;seed=newSeed;
            resetLife();
            return true;
        }catch(RuntimeException bad){return false;}
    }
}
