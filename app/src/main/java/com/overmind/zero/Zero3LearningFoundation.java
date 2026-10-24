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
        private boolean probing,pendingDecision,externalGripOwnerV361;
        private long randomState=0x61C8864680B583EBL,revision,updates;
        private final float[] contextBaseline=new float[8];
        private final long[] contextSamples=new long[8];
        private long perturbationEpisodes;
        private int activeChannel=-1,contextBucket;
        private float lastAcceptedGradientNorm,lastAdvantage;
        private final float learningRate;
        // Gain is on neural commands only, NOT the old SymbioteMatterV1
        // solver's muscle forces. Bounded tanh keeps all 64 values [-1,1].
        // A/B baselines can explicitly request 1.0 without changing the app.
        static final float DEFAULT_ACTIVATION_GAIN_V358=2.4f;
        private final float activationGain;
        /** No prescribed gait: a spatially correlated noise field in one muscle channel. */
        private void sampleProbe(){
            // Original-game grip replaces channel 2 after cortex.command().
            // Do not train a channel whose command did not reach the solver.
            int selected=(int)(perturbationEpisodes++%(externalGripOwnerV361?CHANNELS-1:CHANNELS));
            activeChannel=externalGripOwnerV361&&selected>=2?selected+1:selected;
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
        public AdaptiveCortex(){this(.013f,DEFAULT_ACTIVATION_GAIN_V358);}
        /** Research knob; not autonomously edited by UI and not an approved default. */
        public AdaptiveCortex(float rate){this(rate,DEFAULT_ACTIVATION_GAIN_V358);}
        /** Test-only comparator 1.0 vs the new V358 2.4 cortical activation. */
        AdaptiveCortex(float rate,float gain){
            finite(rate,"motor learning rate");finite(gain,"muscle activation gain");
            if(rate<0f||rate>.3f||gain<1f||gain>3f)
                throw new IllegalArgumentException("motor cortex configuration");
            learningRate=rate;activationGain=gain;
            for(int i=0;i<weights.length;i++)
                weights[i]=(random()-.5f)*.06f;
        }
        private float random(){
            randomState^=randomState<<13;randomState^=randomState>>>7;
            randomState^=randomState<<17;
            return (randomState>>>40)/16777216f;
        }
        /** Complete adaptive motor cortex state at a FINISHED decision. */
        public static final class State {
            private static final int[] SHAPES={512,64,64,512,64,8,8};
            private final float[][] arrays;
            private final long[] contextSamples;
            final long randomState,revision,updates,perturbationEpisodes;
            final int contextBucket;
            final float lastAcceptedGradientNorm,lastAdvantage,learningRate;
            private State(float[][] input,long[] counts,long rng,long rev,long updates,
                    long probes,int bucket,float norm,float advantage,float lr){
                if(input==null||input.length!=SHAPES.length||counts==null||
                    counts.length!=8||rng==0||rev<0||updates<0||updates>rev||
                    probes<0||bucket<0||bucket>=8||
                    !Float.isFinite(norm)||!Float.isFinite(advantage)||
                    !Float.isFinite(lr)||lr<0||lr>.3f)
                    throw new IllegalArgumentException("invalid cortex checkpoint metadata");
                arrays=new float[input.length][];
                for(int j=0;j<input.length;j++){
                    if(input[j]==null||input[j].length!=SHAPES[j])
                        throw new IllegalArgumentException("cortex checkpoint topology");
                    arrays[j]=input[j].clone();
                    for(float v:arrays[j])if(!Float.isFinite(v))
                        throw new IllegalArgumentException("nonfinite cortex value");
                }
                contextSamples=counts.clone();
                for(long n:contextSamples)if(n<0)
                    throw new IllegalArgumentException("invalid context sample");
                randomState=rng;revision=rev;this.updates=updates;
                perturbationEpisodes=probes;contextBucket=bucket;
                lastAcceptedGradientNorm=norm;lastAdvantage=advantage;learningRate=lr;
            }
            void write(java.io.DataOutputStream d)throws java.io.IOException{
                d.writeLong(randomState);d.writeLong(revision);d.writeLong(updates);
                d.writeLong(perturbationEpisodes);d.writeInt(contextBucket);
                d.writeFloat(lastAcceptedGradientNorm);
                d.writeFloat(lastAdvantage);d.writeFloat(learningRate);
                for(long v:contextSamples)d.writeLong(v);
                for(float[] arr:arrays)for(float v:arr)d.writeFloat(v);
            }
            static State read(java.io.DataInputStream d)throws java.io.IOException{
                long rng=d.readLong(),rev=d.readLong(),updates=d.readLong(),perturb=d.readLong();
                int bucket=d.readInt();
                float norm=d.readFloat(),adv=d.readFloat(),lr=d.readFloat();
                long[] counts=new long[8];
                for(int i=0;i<8;i++)counts[i]=d.readLong();
                float[][] arrays=new float[SHAPES.length][];
                for(int j=0;j<SHAPES.length;j++){
                    arrays[j]=new float[SHAPES[j]];
                    for(int i=0;i<arrays[j].length;i++)arrays[j][i]=d.readFloat();
                }
                return new State(arrays,counts,rng,rev,updates,perturb,
                                 bucket,norm,adv,lr);
            }
            public float learningRate(){return learningRate;}
        }
        private float[][] checkpointArrays(){
            return new float[][]{weights,state,noise,accumulated,output,
                                 features,contextBaseline};
        }
        public State checkpoint(){
            if(pendingDecision||probing||samples!=0||activeChannel!=-1)
                throw new IllegalStateException("cortex decision not finished");
            return new State(checkpointArrays(),contextSamples,randomState,
                revision,updates,perturbationEpisodes,contextBucket,
                lastAcceptedGradientNorm,lastAdvantage,learningRate);
        }
        public void restore(State s){
            if(s==null||pendingDecision||probing||samples!=0||revision!=0||updates!=0||
               Float.floatToIntBits(s.learningRate)!=Float.floatToIntBits(learningRate))
                throw new IllegalStateException("cortex restore requires fresh matching instance");
            float[][] own=checkpointArrays();
            for(int i=0;i<own.length;i++)
                System.arraycopy(s.arrays[i],0,own[i],0,own[i].length);
            System.arraycopy(s.contextSamples,0,contextSamples,0,8);
            randomState=s.randomState;revision=s.revision;updates=s.updates;
            perturbationEpisodes=s.perturbationEpisodes;
            contextBucket=s.contextBucket;
            lastAcceptedGradientNorm=s.lastAcceptedGradientNorm;
            lastAdvantage=s.lastAdvantage;
        }
        public long revision(){return revision;}
        public long updates(){return updates;}
        public float[] weights(){return weights.clone();}
        void inheritWeightsV354(float[] parent){
            if(pendingDecision||revision!=0||updates!=0||parent==null||parent.length!=weights.length)throw new IllegalStateException("V354 fresh motor cortex");
            for(float f:parent)if(!Float.isFinite(f)||Math.abs(f)>1.5f)throw new IllegalArgumentException("V354 inherited cortex weight");
            System.arraycopy(parent,0,weights,0,weights.length);
        }
        void appendGraphV354(Zero3NeuralGraphV354.Builder b){
            for(int u=0;u<64;u++){
                int id=b.node(4,u,8,"sector "+(u/4)+" channel "+(u%4),output[u],weights,u*8,8);
                for(int i=0;i<8;i++)b.edge(i==7&&u%4!=2?id:-2000-i,id,0,weights[u*8+i]);
            }
        }
        /**
         * V332 LAB ONLY: evaluate learned synaptic weights on a newborn,
         * identical slow-state cortex without modifying the live runtime.
         * A fresh evaluator cannot have learned or executed a decision.
         */
        void setWeightsV332ForEvaluation(float[] incoming){
            if(incoming==null||incoming.length!=weights.length||
               revision!=0||updates!=0||pendingDecision)
                throw new IllegalStateException("V332 evaluator must be virgin");
            for(float v:incoming)finite(v,"learned V332 evaluation weight");
            System.arraycopy(incoming,0,weights,0,weights.length);
        }

        /** Cold-start a read-only evaluation clone; no weights are trained. */
        public AdaptiveCortex forkForEvaluation(){
            AdaptiveCortex fresh=new AdaptiveCortex(learningRate);
            System.arraycopy(weights,0,fresh.weights,0,weights.length);
            fresh.revision=revision;
            return fresh;
        }
        /**
         * Laboratory/episodic search only: candidate candidate differs in a
         * spatially local bias group. The values and winning region are NOT a
         * predefined gait; they must be discovered by physical trials.
         */
        public AdaptiveCortex forkCandidate(int center,int channel,float delta){
            if(center<0||center>=SECTORS||channel<0||channel>=CHANNELS)
                throw new IllegalArgumentException("motor search group");
            finite(delta,"search delta");
            if(Math.abs(delta)>.8f)throw new IllegalArgumentException("search amplitude");
            AdaptiveCortex fresh=forkForEvaluation();
            for(int n=-3;n<=3;n++){
                int sector=(center+n+SECTORS)%SECTORS;
                int offset=(sector*CHANNELS+channel)*INPUTS;
                float taper=(4f-Math.abs(n))/4f;
                fresh.weights[offset]=limit(fresh.weights[offset]+delta*taper,-1.5f,1.5f);
            }
            fresh.revision++;
            return fresh;
        }
        /**
         * V3.32 isolated laboratory-only eligibility after physically EXECUTED
         * exploratory action, captured BEFORE Coordinator.finish clears it.
         * NOT an additional brain sense, not a counterfactual, never in live.
         */
        static final class LocalProbeV332 {
            final int selectedChannel;
            final float[] noise,meanFeatures;
            final int context;
            LocalProbeV332(int channel,int context,float[] n,float[] f){
                selectedChannel=channel;this.context=context;
                noise=n.clone();meanFeatures=f.clone();
            }
        }
        LocalProbeV332 captureProbeV332(){
            if(!pendingDecision||!probing||samples<1)
                throw new IllegalStateException("V332 probe requires executed motor steps");
            float[] averaged=new float[accumulated.length];
            for(int j=0;j<averaged.length;j++)
                averaged[j]=limit(accumulated[j]/samples,-2f,2f);
            return new LocalProbeV332(activeChannel,contextBucket,noise,averaged);
        }
        /**
         * Delayed measured local eligibility update. Each response is from
         * true post-solver skin and COM, NOT fixture coordinates or hidden
         * future world. This is an estimated learning signal, not the exact
         * counterfactual derivative from the V331 three-body probe.
         *
         * The previous action trace receives localNow plus 0.82 times the
         * next decision's actual localized response. CH2 is V319-owned.
         */
        void learnV332(LocalProbeV332 probe,float[] localNow,
                       float[] localNext){
            if(pendingDecision||probe==null||localNow==null||localNext==null||
               localNow.length!=SECTORS||localNext.length!=SECTORS)
                throw new IllegalStateException("V332 delayed eligibility invalid");
            if(learningRate<=0f||probe.selectedChannel==2)return;
            int ch=probe.selectedChannel;
            if(ch<0||ch>=CHANNELS)
                throw new IllegalArgumentException("V332 invalid active motor channel");
            float total=0f;
            for(int sector=0;sector<SECTORS;sector++){
                float measured=localNow[sector]+.82f*localNext[sector];
                finite(measured,"measured delayed local credit");
                float localAdv=limit(measured,-1f,1f);
                int out=sector*CHANNELS+ch;
                for(int feature=0;feature<INPUTS;feature++){
                    int idx=out*INPUTS+feature;
                    float delta=limit(learningRate*localAdv*probe.noise[out]*
                                      probe.meanFeatures[idx],-.002f,.002f);
                    float original=weights[idx];
                    weights[idx]=limit(original+delta,-1.5f,1.5f);
                    float applied=weights[idx]-original;
                    total+=applied*applied;
                }
            }
            lastAcceptedGradientNorm=(float)Math.sqrt(total);
            if(total>1e-16f){updates++;revision++;}
        }
        /**
         * V333 ablation, STRICTLY LAB ONLY. Mode 0: V332 local+delay,
         * 1: local without delay, 2: 0.82-delay with global average of the
         * *same measured sector responses*. Excludes V319-owned grip channel.
         * All probes originated from executed live-solver muscle commands;
         * this routine cannot query an unseen world or simulate the future.
         */
        void learnV333(LocalProbeV332 probe,float[] now,float[] later,int mode){
            if(mode<0||mode>2||pendingDecision||probe==null||
               now==null||later==null||
               now.length!=SECTORS||later.length!=SECTORS)
                throw new IllegalStateException("V333 ablation input invalid");
            if(learningRate<=0f||probe.selectedChannel==2)return;
            int channel=probe.selectedChannel;
            if(channel<0||channel>=CHANNELS)
                throw new IllegalArgumentException("V333 cortex channel");
            float[] measured=new float[SECTORS];
            float average=0f;
            for(int s=0;s<SECTORS;s++){
                finite(now[s],"V333 actual local response");
                finite(later[s],"V333 actual next response");
                measured[s]=limit(now[s]+(mode==1?0f:.82f)*later[s],-1f,1f);
                average+=measured[s]/SECTORS;
            }
            float sq=0f;
            for(int s=0;s<SECTORS;s++){
                final float credit=mode==2?average:measured[s];
                final int output=s*CHANNELS+channel;
                for(int j=0;j<INPUTS;j++){
                    final int index=output*INPUTS+j;
                    final float change=limit(learningRate*credit*
                      probe.noise[output]*probe.meanFeatures[index],-.002f,.002f);
                    float before=weights[index];
                    weights[index]=limit(before+change,-1.5f,1.5f);
                    float delta=weights[index]-before;
                    sq+=delta*delta;
                }
            }
            lastAcceptedGradientNorm=(float)Math.sqrt(sq);
            if(sq>1e-16f){updates++;revision++;}
        }
        public float[] lastCommands(){return output.clone();}
        public void begin(boolean explore){begin(explore,null);}
        public void begin(boolean explore,MotorIntent intent){
            begin(explore,intent,false);
        }
        void begin(boolean explore,MotorIntent intent,boolean gripExternallyOwned){
            if(pendingDecision)throw new IllegalStateException("unfinished cortex decision");
            pendingDecision=true;
            Arrays.fill(noise,0f);Arrays.fill(accumulated,0f);samples=0;
            probing=explore;externalGripOwnerV361=gripExternallyOwned;
            activeChannel=-1;lastAcceptedGradientNorm=0f;
            if(intent!=null){
                float angle=(float)Math.atan2(intent.y,intent.x);
                contextBucket=((int)Math.floor((angle+Math.PI)/(Math.PI/4)))&7;
            }
            if(explore)sampleProbe();
        }
        void discard(){
            pendingDecision=false;
            Arrays.fill(noise,0);Arrays.fill(accumulated,0);samples=0;probing=false;
            activeChannel=-1;externalGripOwnerV361=false;
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
                    float learned=(float)Math.tanh(activationGain*(z+noise[at]));
                    // ZERO386: physical voluntary effort MUST have real
                    // actuator authority. Previously effort existed only as
                    // an input feature: even effort=0 could produce full
                    // radial/tangential muscle power. This is a passive gain
                    // on the SOLE GRU3 cortex, not a heading, force or gait
                    // injected by another controller. 0.5 retains the exact
                    // prior motor amplitude, 0 silences CH0/1/3, 1 permits
                    // double learned amplitude within the existing [-1,1]
                    // physical solver envelope. CH2 is V319 grip-owned.
                    float gain=ch==2?1f:2f*intention.effort;
                    output[at]=limit(learned*gain,-1f,1f);
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
        // V378 ORIGINAL V3 ONLY: measured momentum at the PRE decision boundary.
        // This is not a force, target position, reward for distance, or schema field.
        private float startVxV378,startVyV378;
        private boolean momentumCreditV378;
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
            begin(intent,b,train,heldout,exploration,false,false);
        }
        public void begin(MotorIntent intent,MotorBrain.Body b,boolean train,boolean heldout,
                          boolean exploration,boolean externallyOwnedGripV361){
            begin(intent,b,train,heldout,exploration,externallyOwnedGripV361,false);
        }
        /** V378: opt-in ONLY on original ZERO3 S21. Other V3 lab/legacy scores remain identical. */
        public void begin(MotorIntent intent,MotorBrain.Body b,boolean train,boolean heldout,
                          boolean exploration,boolean externallyOwnedGripV361,
                          boolean momentumCreditV378){
            if(intent==null||b==null||active!=null)throw new IllegalStateException("pending or null");
            if(train==heldout)throw new IllegalArgumentException("must choose train XOR heldout");
            if(intent.decisionId<=lastDecision&&intent.episodeId==episode)
                throw new IllegalArgumentException("repeated decision ID");
            if(intent.revision!=cortex.revision())throw new IllegalArgumentException("stale motor revision");
            active=intent;this.train=train;this.heldout=heldout;external=false;censored=false;
            lastDecision=intent.decisionId;episode=intent.episodeId;startedRevision=cortex.revision();
            startX=lastX=endX=b.cx;startY=lastY=endY=b.cy;
            frames=0;gross=work=impulse=seconds=0;
            this.momentumCreditV378=momentumCreditV378;
            startVxV378=b.cvx;startVyV378=b.cvy;
            if(momentumCreditV378&&(!Float.isFinite(startVxV378)||!Float.isFinite(startVyV378)))
                throw new IllegalArgumentException("V378 nonfinite physical PRE momentum");
            cortex.begin(exploration,intent,externallyOwnedGripV361);
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
        public void observeWithDisturbanceV354(MotorBrain.Body b,float dt,float work,float impulse,int reasons){
            if(reasons<0||reasons>1)throw new IllegalArgumentException("unknown V354 physical disturbance");
            observe(b,dt,work,impulse,reasons!=0,false);
        }
        public Experience finish(){return finish(false);}
        public Experience finish(boolean interrupted){
            censored|=interrupted;
            if(active==null)throw new IllegalStateException("no decision to finish");
            if(cortex.revision()!=startedRevision)throw new IllegalStateException("weights changed during outcome");
            float dx=endX-startX,dy=endY-startY;
            float projection=dx*active.x+dy*active.y;
            float progress=projection/(4f+Math.max(gross,projection));
            if(momentumCreditV378){
                // A body already sliding EAST may physically brake toward WEST
                // yet keep moving EAST briefly. Absolute displacement called
                // that useful response negative (OTA377 failure).
                // Compare against unchanged PRE linear momentum over the SAME
                // real duration. It is an inertial reference, NOT a simulated
                // world, passive-drag estimate or proof of causal actuator work.
                // Valid cortex training still requires real muscle impulse,
                // zero world correction and an uncensored outcome below.
                float inertialDx=startVxV378*seconds,inertialDy=startVyV378*seconds;
                float correctionX=dx-inertialDx,correctionY=dy-inertialDy;
                float correction=correctionX*active.x+correctionY*active.y;
                float referenceDistance=(float)Math.hypot(inertialDx,inertialDy);
                progress=limit(correction/
                    (4f+Math.max(gross,referenceDistance)),-1f,1f);
            }
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
