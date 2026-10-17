package com.overmind.zero;

import java.util.Arrays;

/**
 * V3.18: physically executed, TRAINABLE six-feature x 16 sector motor head.
 *
 * No world IDs / targets enter features. This head is the sole producer of
 * the adhesion actuator channel 2 in the experimental V3.18 circuit:
 * the real gel and world solvers decide whether any patch actually grips.
 *
 * Exploration perturbs neural LOGITS, not collision flags or skin.gripHold.
 * Gradients use only observed legal held patches and actual actuation costs.
 */
public final class Zero3LearnedAdhesionV318 {
    static final int SECTORS=16,FEATURES=6;
    private final float[] weights=new float[SECTORS*FEATURES];
    private final float[] baseline=new float[SECTORS];
    private final float[][] sums=new float[SECTORS][FEATURES];
    private final float[] perturb=new float[SECTORS];
    private final float[] noTouchWork=new float[SECTORS];
    private final int[] realHoldSteps=new int[SECTORS];
    private final float[] recent=new float[SECTORS];
    private final Rng rng;
    private final boolean learning;
    private boolean pending;
    private int steps,updates,positiveGripUpdates;
    private double learnedL2;
    private long sourcedContacts;
    private static final float LR=.16f;
    public Zero3LearnedAdhesionV318(long seed,boolean learning){
        this.learning=learning;this.rng=new Rng(seed);
    }
    public Zero3LearnedAdhesionV318 frozenCopy(){
        Zero3LearnedAdhesionV318 out=new Zero3LearnedAdhesionV318(0x318L,false);
        System.arraycopy(weights,0,out.weights,0,weights.length);
        return out;
    }
    public int updates(){return updates;}
    public int positiveGripUpdates(){return positiveGripUpdates;}
    public double learnedL2(){return learnedL2;}
    public double weightL2(){
        double x=0;for(float v:weights)x+=v*v;return Math.sqrt(x);
    }
    public int sourcedContacts(){return (int)Math.min(Integer.MAX_VALUE,sourcedContacts);}
    static final class Rng {
        private long s;
        Rng(long seed){s=seed==0?0x3E57AB18499C6FD7L:seed;}
        float uniform(){s^=s<<13;s^=s>>>7;s^=s<<17;return (s>>>40)/16777216f;}
    }
    static float sigmoid(float x){
        x=Math.max(-10f,Math.min(10f,x));
        return (float)(1.0/(1.0+Math.exp(-x)));
    }
    static float bound(float v,float a,float b){
        return Math.max(a,Math.min(b,v));
    }
    public void begin(){
        if(pending)throw new IllegalStateException("previous intention has not ended");
        pending=true;steps=0;
        Arrays.fill(realHoldSteps,0);
        Arrays.fill(noTouchWork,0f);
        Arrays.fill(recent,0f);
        for(int i=0;i<SECTORS;i++)Arrays.fill(sums[i],0f);
        // Lab-only random exploration of neuron logit for TRAIN.
        // Smooth correlated field, no fixed gait, physical grip stays unforced.
        float common=learning?(rng.uniform()*2f-1f)*2.0f:0f;
        float[] anchor=new float[4];
        for(int i=0;i<4;i++)
            anchor[i]=learning?(rng.uniform()*2f-1f)*.6f:0f;
        for(int k=0;k<SECTORS;k++){
            int sector=k/4;float fraction=(k%4)/4f;
            perturb[k]=common+(anchor[sector]*(1-fraction)+
                                anchor[(sector+1)%4]*fraction);
        }
    }
    /**
     * Filled directly into the real executed64 motor tape BEFORE gel.step,
     * replacing ONLY the voluntary channel 2 owned by this motor head.
     * Actual touch / hold arrays remain exclusively owned by world physics.
     */
    public void actuate(float[] executed64,
                        Zero3MotorIntentV316 chosen,
                        MotorBrain.Body body,Morphology.Surface skin,
                        java.util.ArrayList<UnknownWorld.Entity> objects){
        if(!pending||executed64==null||executed64.length!=64||chosen==null)
            throw new IllegalStateException("missing active motor intention");
        for(int sector=0;sector<SECTORS;sector++){
            final int first=sector*(Morphology.POINTS/SECTORS);
            float touch=0,pulse=0,held=0,optical=0;
            for(int q=0;q<Morphology.POINTS/SECTORS;q++){
                int i=first+q;
                touch+=bound(skin.touchSense[i],0,1);
                pulse+=bound(skin.touchPulse[i],0,1);
                if(skin.gripHold[i]>=Morphology.GRIP_HOLD)held++;
                // Optical input is measured below from the REAL membrane
                // and obstacle ray tracer, before physical actuation.
            }
            int eyeSite=first+3;
            float nx=skin.x[eyeSite]-body.cx,ny=skin.y[eyeSite]-body.cy;
            float radius=(float)Math.hypot(nx,ny);
            if(radius>.00001f){nx/=radius;ny/=radius;}
            else{nx=1;ny=0;}
            float distance=SensorimotorMind.Senses.firstSolidDistance(
                null,objects,skin.x[eyeSite],skin.y[eyeSite],
                nx,ny,132f);
            optical=Float.isFinite(distance)?
                bound(1f-distance/132f,0f,1f):0f;
            float[] f={
                1f,chosen.adhesionPreference,
                touch/6f,pulse/6f,held/6f,optical
            };
            for(int j=0;j<FEATURES;j++){
                if(!Float.isFinite(f[j]))throw new IllegalArgumentException("invalid motor feature");
                sums[sector][j]+=f[j];
            }
            float z=-1.4f+perturb[sector];
            int at=sector*FEATURES;
            for(int j=0;j<FEATURES;j++)z+=weights[at+j]*f[j];
            float command=sigmoid(z);
            executed64[4*sector+2]=command;
            recent[sector]=command;
            if(touch+pulse<.05f)
                noTouchWork[sector]+=command;
        }
        steps++;
    }
    /**
     * Physical post-collision outcome, never a synthetic "grip succeeded"
     * bit computed from logit or touch alone.
     */
    public void observe(Morphology.Surface skin,UnknownWorld.Entity box){
        if(!pending||steps==0)throw new IllegalStateException("no executed physics");
        for(int sector=0;sector<SECTORS;sector++){
            int start=sector*6;
            boolean held=false;
            for(int j=0;j<6;j++){
                int i=start+j;
                if(skin.gripHold[i]>=Morphology.GRIP_HOLD){
                    if(box==null||!box.isBilateralBox()||
                       box.boxGripHold[i]<Morphology.GRIP_HOLD||
                       skin.gripCommand[i]<=Morphology.GRIP_THRESHOLD)
                        throw new IllegalStateException("fabricated physical grip");
                    held=true;
                }
                if(skin.touchSense[i]>.01f||skin.touchPulse[i]>.001f)
                    sourcedContacts++;
            }
            if(held)realHoldSteps[sector]++;
        }
    }
    public boolean end(){
        if(!pending||steps==0)throw new IllegalStateException("decision not physically observed");
        pending=false;
        boolean realGrip=false;
        if(learning){
            for(int k=0;k<SECTORS;k++){
                // Only actual sustained box grip gives positive credit.
                float grip=(float)realHoldSteps[k]/steps;
                if(grip>0)realGrip=true;
                // Real attempted motor output in absence of contact costs
                // actuator effort, not a hardcoded proximity success.
                float penalty=.025f*noTouchWork[k]/steps;
                float score=grip-penalty;
                float advantage=bound(score-baseline[k],-1f,1f);
                baseline[k]+=.03f*(score-baseline[k]);
                if(grip>0)positiveGripUpdates++;
                // One-parameter-at-a-time experimental perturbation,
                // credited only after the true physical outcome.
                for(int j=0;j<FEATURES;j++){
                    int ix=k*FEATURES+j;
                    float dw=bound(LR*advantage*perturb[k]*
                        (sums[k][j]/steps),-.12f,.12f);
                    float previous=weights[ix];
                    weights[ix]=bound(previous+dw,-4f,4f);
                    float actual=weights[ix]-previous;
                    learnedL2+=actual*actual;
                }
            }
            updates++;
        }else{
            for(int holds:realHoldSteps)if(holds>0)realGrip=true;
        }
        return realGrip;
    }
}
