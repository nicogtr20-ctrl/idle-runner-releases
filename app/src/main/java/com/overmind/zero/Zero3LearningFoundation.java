package com.overmind.zero;

import java.util.Arrays;

/**
 * ZERO 3.0 G2/G3 FOUNDATION — experimental pure-Java, physically grounded
 * intention -> live 16x4 cortex -> measured outcome -> exactly-once update.
 *
 * NOT the production ZERO 3.0 brain, not actor-critic or a complete gait learner.
 * No GameActivity or save-slot integration; old ZERO 2.2 remains untouched.
 * This class exposes auditable contracts before attempting neural integration.
 */
public final class Zero3LearningFoundation {
    public static final int SECTORS=16, CHANNELS=4, OUTPUTS=SECTORS*CHANNELS;
    public static final int INPUTS=8;
    private Zero3LearningFoundation(){}
    static float limit(float x,float lo,float hi){return Math.max(lo,Math.min(hi,x));}
    static void finite(float x,String name){
        if(!Float.isFinite(x))throw new IllegalArgumentException("nonfinite "+name);
    }

    /** Continuous voluntary intention, NEVER translated into external thrust. */
    public static final class MotorIntent {
        public final long episodeId,decisionId,revision;
        public final float x,y,effort,adhesion,duration;
        public MotorIntent(long episode,long decision,long weights,float dx,float dy,
                float effort,float adhesion,float duration){
            if(episode<0||decision<0||weights<0)throw new IllegalArgumentException("identity");
            finite(dx,"direction x");finite(dy,"direction y");
            finite(effort,"effort");finite(adhesion,"adhesion");finite(duration,"duration");
            if(effort<0||effort>1||adhesion<0||adhesion>1||duration<=0||duration>5)
                throw new IllegalArgumentException("bounds");
            float n=(float)Math.hypot(dx,dy);
            if(n>1.0001f)throw new IllegalArgumentException("direction magnitude");
            if(n>0f&&n<.9999f)throw new IllegalArgumentException("unnormalized direction");
            episodeId=episode;decisionId=decision;revision=weights;
            x=dx;y=dy;this.effort=effort;this.adhesion=adhesion;this.duration=duration;
        }
    }

    /** Immutable final physically observed transition, not a predicted one. */
    public static final class Experience {
        public final MotorIntent intent;
        public final int frames;
        public final boolean heldout,train,physicalEvidence,externalCorrection,censored;
        public final float deltaX,deltaY,gross,work,activeImpulse,seconds,score;
        Experience(MotorIntent intent,int frames,boolean heldout,boolean train,
                boolean physical,boolean corrected,boolean censored,
                float x,float y,float gross,float work,float impulse,float seconds,float score){
            this.intent=intent;this.frames=frames;this.heldout=heldout;this.train=train;
            physicalEvidence=physical;externalCorrection=corrected;this.censored=censored;
            deltaX=x;deltaY=y;this.gross=gross;this.work=work;
            activeImpulse=impulse;this.seconds=seconds;this.score=score;
        }
    }

    /**
     * Learns a local 4-channel nonlinear policy, shared by all 16 sectors.
     * A stable per-decision perturbation is applied to physical commands; the
     * perturbation itself and the experienced features are recorded for credit.
     * No rhythmic sinusoids, no world object coordinates, no artificial motion.
     */
    public static final class AdaptiveCortex {
        private final float[] weights=new float[OUTPUTS*INPUTS];
        private final float[] state=new float[OUTPUTS];
        private final float[] noise=new float[OUTPUTS];
        private final float[] accumulated=new float[OUTPUTS*INPUTS];
        private final float[] output=new float[OUTPUTS];
        private final float[] features=new float[INPUTS];
        private int samples;
        private boolean probing;
        private long randomState=0x61C8864680B583EBL,revision,updates;
        private final float[] contextBaseline=new float[8];
        private final long[] contextSamples=new long[8];
        private long perturbationEpisodes;
        private int activeChannel=-1,contextBucket;
        private float lastAcceptedGradientNorm,lastAdvantage;
        private final float learningRate;
        /** No prescribed gait: a spatially correlated noise field in one muscle channel. */
        private void sampleProbe(){
            activeChannel=(int)(perturbationEpisodes++%CHANNELS);
            float[] anchors=new float[4];
            for(int i=0;i<4;i++)anchors[i]=random()*2f-1f;
            for(int sector=0;sector<SECTORS;sector++){
                int group=sector/4,step=sector%4;
                float t=step*.25f;
                float v=anchors[group]*(1-t)+anchors[(group+1)%4]*t;
                noise[sector*CHANNELS+activeChannel]=.14f*v;
            }
        }
        public float lastAcceptedGradientNorm(){return lastAcceptedGradientNorm;}
        public float lastAdvantage(){return lastAdvantage;}
        public int lastActiveProbeChannel(){return activeChannel;}
        public AdaptiveCortex(){this(.013f);}
        /** Research knob; not autonomously edited by UI and not an approved default. */
        public AdaptiveCortex(float rate){
            finite(rate,"motor learning rate");
            if(rate<0f||rate>.3f)throw new IllegalArgumentException("motor learning rate");
            learningRate=rate;
            for(int i=0;i<weights.length;i++)
                weights[i]=(random()-.5f)*.06f;
        }
        private float random(){
            randomState^=randomState<<13;randomState^=randomState>>>7;
            randomState^=randomState<<17;
            return (randomState>>>40)/16777216f;
        }
        public long revision(){return revision;}
        public long updates(){return updates;}
        public float[] weights(){return weights.clone();}
        /** Cold-start a read-only evaluation clone; no weights are trained. */
        public AdaptiveCortex forkForEvaluation(){
            AdaptiveCortex fresh=new AdaptiveCortex(learningRate);
            System.arraycopy(weights,0,fresh.weights,0,weights.length);
            fresh.revision=revision;
            return fresh;
        }
        public float[] lastCommands(){return output.clone();}
        public void begin(boolean explore){begin(explore,null);}
        public void begin(boolean explore,MotorIntent intent){
            Arrays.fill(noise,0f);Arrays.fill(accumulated,0f);samples=0;
            probing=explore;activeChannel=-1;lastAcceptedGradientNorm=0f;
            if(intent!=null){
                float angle=(float)Math.atan2(intent.y,intent.x);
                contextBucket=((int)Math.floor((angle+Math.PI)/(Math.PI/4)))&7;
            }
            if(explore)sampleProbe();
        }
        void discard(){
            Arrays.fill(noise,0);Arrays.fill(accumulated,0);samples=0;probing=false;
            activeChannel=-1;
        }
        /**
         * Actual geometry is read every physical substep. OUTPUTS are the
         * exact commands consumed by SymbioteMatterV1.step(), no other actor.
         */
        public float[] command(MotorIntent intention,MotorBrain.Body b,Morphology.Surface s,float dt){
            if(intention==null||b==null||s==null)throw new IllegalArgumentException("motor arguments");
            finite(dt,"dt");
            if(dt<=0||dt>.04f)throw new IllegalArgumentException("dt");
            final int per=Morphology.POINTS/SECTORS;
            for(int k=0;k<SECTORS;k++){
                float dx=0,dy=0,rv=0,held=0,contact=0,r=0;
                for(int j=0;j<per;j++){
                    int i=k*per+j;
                    float xx=s.x[i]-b.cx,yy=s.y[i]-b.cy;
                    dx+=xx;dy+=yy;
                    float rr=(float)Math.hypot(xx,yy);
                    r+=rr;
                    if(rr>.01f)rv+=((s.vx[i]-b.cvx)*xx+(s.vy[i]-b.cvy)*yy)/rr;
                    held+=s.gripHold[i]>Morphology.GRIP_HOLD?1:0;
                    contact+=s.touchSense[i];
                }
                float mag=(float)Math.hypot(dx,dy);
                if(mag<.0001f){dx=1;dy=0;mag=1;}
                float nx=dx/mag,ny=dy/mag;
                float projection=nx*intention.x+ny*intention.y;
                float tangent=-ny*intention.x+nx*intention.y;
                features[0]=1;
                features[1]=projection;
                features[2]=tangent;
                features[3]=limit(r/per/80f,0f,2f);
                features[4]=limit(rv/per/100f,-1f,1f);
                features[5]=limit((held+contact)/per,0f,2f);
                features[6]=intention.effort;
                for(int ch=0;ch<CHANNELS;ch++){
                    int at=k*CHANNELS+ch;
                    features[7]=ch==2?intention.adhesion:state[at];
                    float z=0f;
                    for(int f=0;f<INPUTS;f++)z+=weights[at*INPUTS+f]*features[f];
                    // Exploration is an intervention, not a scripted gait.
                    output[at]=limit((float)Math.tanh(z+noise[at]),-1f,1f);
                    if(probing)for(int f=0;f<INPUTS;f++)
                        accumulated[at*INPUTS+f]+=features[f];
                    // Slow local state supports future recurrent evolution;
                    // never a pre-programmed rhythm.
                    state[at]=limit(state[at]*.85f+output[at]*.15f,-1,1);
                }
            }
            if(probing)samples++;
            return output; // non-allocating physics command buffer
        }
        void learn(float score,boolean valid){
            if(!valid||!probing||samples<1){discard();return;}
            finite(score,"motor score");
            float baseline=contextBaseline[contextBucket];
            float advantage=limit(score-baseline,-1f,1f);
            lastAdvantage=advantage;
            float effectiveL2=0f;
            // Credit ONLY the selected muscle channel's localized intervention.
            // This stochastic estimate remains unproven until matched lab A/B.
            final float lr=learningRate;
            for(int out=0;out<OUTPUTS;out++)for(int f=0;f<INPUTS;f++){
                int ix=out*INPUTS+f;
                float feat=limit(accumulated[ix]/samples,-2,2);
                float delta=limit(lr*advantage*noise[out]*feat,-.002f,.002f);
                float previous=weights[ix];
                weights[ix]=limit(previous+delta,-1.5f,1.5f);
                float actual=weights[ix]-previous;
                effectiveL2+=actual*actual;
            }
            float beta=contextSamples[contextBucket]<24 ? .12f : .025f;
            contextBaseline[contextBucket]+=beta*(score-contextBaseline[contextBucket]);
            contextSamples[contextBucket]++;
            lastAcceptedGradientNorm=(float)Math.sqrt(effectiveL2);
            if(effectiveL2>1e-16f){updates++;revision++;}
            discard();
        }
    }

    /**
     * One owner for physical decisions and outcomes. Cannot credit the wrong
     * revision, reuse a decision ID, or update synapses on holdout/contamination.
     */
    public static final class Coordinator {
        private final AdaptiveCortex cortex;
        private MotorIntent active;
        private long lastDecision=-1,episode=-1,startedRevision;
        private boolean train,heldout,external,censored;
        private float startX,startY,lastX,lastY,endX,endY,gross,work,impulse,seconds;
        private int frames;
        private long trained,validated,discarded;
        public Coordinator(AdaptiveCortex cortex){
            if(cortex==null)throw new IllegalArgumentException("cortex");
            this.cortex=cortex;
        }
        public long trained(){return trained;}
        public long validated(){return validated;}
        public long discarded(){return discarded;}
        public boolean pending(){return active!=null;}
        public void begin(MotorIntent intent,MotorBrain.Body b,boolean train,boolean heldout){
            begin(intent,b,train,heldout,train);
        }
        /** Optional matched heldout experiment uses exploration but NEVER gradients. */
        public void begin(MotorIntent intent,MotorBrain.Body b,boolean train,boolean heldout,
                          boolean exploration){
            if(intent==null||b==null||active!=null)throw new IllegalStateException("pending or null");
            if(train==heldout)throw new IllegalArgumentException("must choose train XOR heldout");
            if(intent.decisionId<=lastDecision&&intent.episodeId==episode)
                throw new IllegalArgumentException("repeated decision ID");
            if(intent.revision!=cortex.revision())throw new IllegalArgumentException("stale motor revision");
            active=intent;this.train=train;this.heldout=heldout;external=false;censored=false;
            lastDecision=intent.decisionId;episode=intent.episodeId;startedRevision=cortex.revision();
            startX=lastX=endX=b.cx;startY=lastY=endY=b.cy;
            frames=0;gross=work=impulse=seconds=0;
            cortex.begin(exploration,intent);
        }
        public float[] actuate(MotorBrain.Body body,Morphology.Surface skin,float dt){
            if(active==null)throw new IllegalStateException("no decision");
            if(cortex.revision()!=startedRevision)throw new IllegalStateException("weights changed mid-action");
            return cortex.command(active,body,skin,dt);
        }
        public void observe(MotorBrain.Body after,float dt,float physicalWork,
                            float muscleImpulse,boolean solverCorrection,boolean teleport){
            if(active==null||after==null)throw new IllegalStateException("no decision");
            finite(dt,"dt");finite(physicalWork,"work");finite(muscleImpulse,"impulse");
            if(dt<=0||dt>.04f||physicalWork<0||muscleImpulse<0)
                throw new IllegalArgumentException("invalid physical outcome");
            gross+=(float)Math.hypot(after.cx-lastX,after.cy-lastY);
            lastX=endX=after.cx;lastY=endY=after.cy;
            work+=physicalWork;impulse+=muscleImpulse;seconds+=dt;frames++;
            external|=solverCorrection;censored|=teleport;
        }
        public Experience finish(){
            if(active==null)throw new IllegalStateException("no decision to finish");
            if(cortex.revision()!=startedRevision)throw new IllegalStateException("weights changed during outcome");
            float dx=endX-startX,dy=endY-startY;
            float projection=dx*active.x+dy*active.y;
            float progress=projection/(4f+Math.max(gross,projection));
            float idlePenalty=.01f*work;
            float score=limit(progress-idlePenalty,-1f,1f);
            boolean physical=frames>=4&&impulse>.5f&&!censored;
            boolean valid=train&&physical&&!external&&active.effort>.01f&&
                          (active.x!=0f||active.y!=0f);
            MotorIntent intent=active;
            Experience e=new Experience(intent,frames,heldout,train,physical,
                external,censored,dx,dy,gross,work,impulse,seconds,score);
            cortex.learn(score,valid);
            if(valid)trained++;else if(heldout&&physical&&!external)validated++;else discarded++;
            active=null;
            return e;
        }
        public void abort(){
            if(active==null)return;
            cortex.discard();active=null;discarded++;
        }
    }
}
