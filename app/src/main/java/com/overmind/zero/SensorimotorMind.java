package com.overmind.zero;

import java.util.*;

/**
 * ZERO-009: online embodied model-based learning.
 *
 * All stimuli are sensory readings. No door identifier, target coordinate,
 * room-success flag, scripted target direction, or "keep pulling" rule ever
 * reaches this learner. It chooses between generic movement/adhesion motor
 * commands and learns a transition model and action values after actual
 * experience in the continuously running world.
 *
 * Drives used for reinforcement are energy homeostasis and generic measured
 * sensorimotor change. The physics/senses/learning objective ARE designed,
 * so this does NOT establish free will or symbolic causal understanding.
 */
public final class SensorimotorMind {
    public static final int S=26;
    public static final int ACTIONS=16;  // 8 physical headings x 2 adhesion intensities
    public static final int P=S+1;
    public static final int FORWARD_WEIGHTS=ACTIONS*S*P;
    public static final int VALUE_WEIGHTS=ACTIONS*P;
    public static final int PARAMETERS=FORWARD_WEIGHTS+VALUE_WEIGHTS;
    private static final float MODEL_LR=.018f,VALUE_LR=.04f,GAMMA=.78f;
    private final Random random=new Random(60909);
    private transient EpisodicMemory episodic;
    private transient MotorAutonomyGuard motorGuard;
    private transient TemporalWorkingMemory temporal;
    private transient TemporalSkillBank temporalSkills;
    private transient BodyProprioception bodySchema;
    private transient SpatialPerception spatial;
    private transient CausalExperiments causal;
    private transient InstrumentalNavigation navigation;
    private transient GoalContinuity intention;
    // Passive flight recorder: reads EXISTING scores, never changes an action.
    private transient DecisionBlackbox blackbox;
    // Pure empirical observer; its predicted motion is NEVER scored or acted upon.
    private transient ContextualMotionProbe motionShadow;
    private transient MortalityLearning mortality;
    public synchronized void attachMortalityLearning(MortalityLearning model){mortality=model;}
    public synchronized void attachMotionShadow(ContextualMotionProbe probe){motionShadow=probe;}
    public synchronized void attachDecisionBlackbox(DecisionBlackbox recorder){blackbox=recorder;}
    public synchronized void attachGoalContinuity(GoalContinuity c){intention=c;}
    public synchronized GoalContinuity goalContinuity(){return intention;}
    // Benchmark-only ablation: sensors, learning and all other policy terms
    // still run. Default production behavior is unchanged (true).
    private transient boolean navigationAdviceEnabled=true;
    public synchronized void setNavigationAdviceEnabled(boolean enabled){
        navigationAdviceEnabled=enabled;
    }
    private float actualEnergySinceChoice=0f;
    private transient MotorBrain.Body observedBody;
    // Performed-action traces give temporally distant actual resource
    // contacts a learning signal. They NEVER add a geometric direction bias.
    private final ExperienceCredit contactCredit=new ExperienceCredit();
    private final LearnedSequencePlanner planner=new LearnedSequencePlanner();
    public synchronized String planningStats(){return planner.encode();}
    public synchronized boolean restorePlanningStats(String saved){return planner.restore(saved);}
    public synchronized LearnedSequencePlanner planner(){return planner;}
    public synchronized void contactOutcome(float actualEnergyDelta,int visuallyObservedSignature){
        if(learning){
            contactCredit.consequence(actualEnergyDelta,visuallyObservedSignature);
            if(temporal!=null)temporal.energyOutcome(actualEnergyDelta);
            if(temporalSkills!=null)temporalSkills.energyOutcome(actualEnergyDelta);
            if(bodySchema!=null)bodySchema.actualEnergy(actualEnergyDelta);
            if(causal!=null)causal.energyOutcome(actualEnergyDelta);
            if(navigation!=null)navigation.energyOutcome(actualEnergyDelta,
                visuallyObservedSignature,true);
            actualEnergySinceChoice+=actualEnergyDelta;
        }
    }
    public synchronized String outcomeModel(){return contactCredit.encode();}
    public synchronized boolean restoreOutcomeModel(String stored){
        return contactCredit.restore(stored);
    }
    public synchronized int physicalOutcomeEvents(){return contactCredit.physicalEvents;}
    public synchronized int sourceMatchedEvents(){return contactCredit.sourceMatched;}
    public synchronized int ambiguousOutcomeEvents(){return contactCredit.ambiguousContacts;}
    public synchronized void restoreContactCounts(int positive,int negative,int credits){
        contactCredit.restoreCounters(positive,negative,credits);
    }
    public synchronized int positiveContactCredits(){return contactCredit.positiveContacts;}
    public synchronized int negativeContactCredits(){return contactCredit.negativeContacts;}
    public synchronized int historicalActionsCredited(){return contactCredit.updatedActions;}
    public synchronized void attachMemory(EpisodicMemory memory){episodic=memory;}
    public synchronized void attachMotorGuard(MotorAutonomyGuard guard){motorGuard=guard;}
    public synchronized void attachWorkingMemory(TemporalWorkingMemory memory){temporal=memory;}
    public synchronized TemporalWorkingMemory workingMemory(){return temporal;}
    public synchronized void attachTemporalSkills(TemporalSkillBank bank){temporalSkills=bank;}
    public synchronized void attachBodySchema(BodyProprioception map){bodySchema=map;}
    public synchronized BodyProprioception bodySchema(){return bodySchema;}
    public synchronized void attachSpatialPerception(SpatialPerception model){spatial=model;}
    public synchronized SpatialPerception spatialPerception(){return spatial;}
    public synchronized void attachCausalExperiments(CausalExperiments model){causal=model;}
    public synchronized CausalExperiments causalExperiments(){return causal;}
    public synchronized void attachNavigation(InstrumentalNavigation m){navigation=m;}
    public synchronized InstrumentalNavigation navigation(){return navigation;}
    public synchronized void updateNavigationSense(MotorBrain.Body b,ScarcityLab room,
        ArrayList<UnknownWorld.Entity> objects){
        observedBody=b;
        if(navigation!=null)navigation.sense(b,room,objects);
    }
    public synchronized void updateSpatialSense(MotorBrain.Body b,ScarcityLab room,
        ArrayList<UnknownWorld.Entity> objects){
        if(spatial!=null)spatial.observeEnvironment(b,room,objects);
    }
    public synchronized void resumeSpatialSense(MotorBrain.Body b,ScarcityLab room,
        ArrayList<UnknownWorld.Entity> objects){
        if(spatial!=null){spatial.resumeLife(b.cx,b.cy);
            spatial.observeEnvironment(b,room,objects);}
    }
    /** Feed the current physical shape at the start/resume of a life. */
    public synchronized void updateBodySense(MotorBrain.Body b,Morphology.Surface skin){
        if(bodySchema!=null)bodySchema.feed(BodyProprioception.sense(b,skin));
    }
    public synchronized TemporalSkillBank temporalSkills(){return temporalSkills;}
    public synchronized boolean takeTemporalSkillsDirty(){
        return temporalSkills!=null&&temporalSkills.takeDirty();
    }
    private final float[] model=new float[FORWARD_WEIGHTS];
    private final float[] value=new float[VALUE_WEIGHTS];
    private final float[] trialState=new float[S];
    private int trialAction=-1;
    private boolean learning=true;
    public final LearningDiagnostics diagnostics=new LearningDiagnostics();
    private boolean actionExploratory=false;
    private float elapsed=0;
    public long experiences=0,explorations=0,decisions=0;
    public float predictionError=Float.NaN,estimatedValue=0,lastReward=0,averagePredictionError=0;
    public float learnedControl=0;
    private float previousError=Float.NaN;
    public int actionsReconsidered=0;
    public int action=0;

    public SensorimotorMind(){}
    public synchronized void setLearning(boolean enabled){learning=enabled;}
    public synchronized void begin(float[] observation){
        if(observation.length!=S)throw new IllegalArgumentException("sensory width");
        System.arraycopy(observation,0,trialState,0,S);
        trialAction=-1;elapsed=0;
        contactCredit.newLife();
        if(temporal!=null)temporal.resetLife();
        if(temporalSkills!=null)temporalSkills.resetLife();
        if(bodySchema!=null)bodySchema.resetLife();
        if(spatial!=null)spatial.resetLife();
        if(causal!=null)causal.resetLife();
        if(navigation!=null)navigation.resetLife();
        if(intention!=null)intention.resetLife();
        if(blackbox!=null)blackbox.newLife();
        if(motionShadow!=null)motionShadow.resetLife();
        actualEnergySinceChoice=0;
    }
    public synchronized void resetEpisode(float[] first){
        // Mortality resets bodily position and short-term context, never
        // destroys learned temporal/world parameters.
        if(first==null){
            trialAction=-1;elapsed=0;contactCredit.newLife();
            if(temporal!=null)temporal.resetLife();
            if(temporalSkills!=null)temporalSkills.resetLife();
            if(bodySchema!=null)bodySchema.resetLife();
            if(spatial!=null)spatial.resetLife();
            if(causal!=null)causal.resetLife();
            if(navigation!=null)navigation.resetLife();
            if(intention!=null)intention.resetLife();
            if(blackbox!=null)blackbox.newLife();
            if(motionShadow!=null)motionShadow.resetLife();
            actualEnergySinceChoice=0;
            return;
        }
        begin(first);
    }
    /** Resume the same persisted physical life without pretending a death
     * occurred. The saved recurrent context survives app restarts/slot loads. */
    public synchronized void resumeEpisode(float[] observation,int currentAction){
        if(observation==null||observation.length!=S||
           currentAction<0||currentAction>=ACTIONS)
            throw new IllegalArgumentException("resume");
        System.arraycopy(observation,0,trialState,0,S);
        action=currentAction;trialAction=currentAction;elapsed=0;
        if(temporal!=null)temporal.resumeAction(currentAction);
        if(temporalSkills!=null)temporalSkills.resumeLife();
        if(bodySchema!=null)bodySchema.resumeLife();
        // Spatial map is reinstated with the saved physical body separately.
        if(causal!=null)causal.resumeLife();
        if(navigation!=null)navigation.resumeLife();
        if(intention!=null)intention.resumeLife();
        if(blackbox!=null)blackbox.resumeLife();
        if(motionShadow!=null)motionShadow.resetLife();
        actualEnergySinceChoice=0;
    }
    public synchronized float motorX(){return (float)Math.cos((action%8)*Math.PI/4);}
    public synchronized float motorY(){return (float)Math.sin((action%8)*Math.PI/4);}
    public synchronized float adhesion(){return action>=8?.82f:0f;}
    /** End-of-life logger path, BEFORE the existing transient brain reset. */
    public synchronized void recordTerminal(float[] observed,float physicalEffect,
                                            MotorBrain.Body body){
        if(blackbox!=null)blackbox.terminal(observed,actualEnergySinceChoice,
                                           physicalEffect,body);
    }
    public synchronized int decisionCount(){return (int)Math.min(Integer.MAX_VALUE,decisions);}
    public synchronized float[] stateCopy(){return trialState.clone();}
    public synchronized int parameterCount(){return PARAMETERS;}

    private static float bounded(float x){return Math.max(-1.5f,Math.min(1.5f,x));}
    private static float clampEnergyReward(float amount){
        return Math.max(-1.5f,Math.min(1.5f,amount/22f));
    }
    private float predictFeature(float[] state,int a,int dim){
        int start=(a*S+dim)*P;
        float z=model[start+S];
        for(int k=0;k<S;k++)z+=model[start+k]*state[k];
        return bounded(state[dim]+z);
    }
    /** Predicted sensory change from own actuation; no privileged game state. */
    public synchronized float[] prediction(float[] state,int a){
        float[] out=new float[S];
        for(int i=0;i<S;i++)out[i]=predictFeature(state,a,i);
        return out;
    }
    private float estimate(float[] state,int a){
        int base=a*P;
        float q=value[base+S];
        for(int k=0;k<S;k++)q+=value[base+k]*state[k];
        return Math.max(-6,Math.min(6,q));
    }
    private float bestFuture(float[] state){
        float best=-Float.MAX_VALUE;
        for(int a=0;a<ACTIONS;a++)best=Math.max(best,estimate(state,a));
        return best;
    }

    public synchronized int choose(float[] senses){
        if(senses.length!=S)throw new IllegalArgumentException("sensor width");
        boolean protectMotor=motorGuard!=null&&motorGuard.suppressPositiveRecall();
        if(protectMotor)motorGuard.decisionWhenSuppressed();
        int proposed=episodic==null?-1:episodic.suggested(senses,!protectMotor);
        int chosen=0;
        float best=-Float.MAX_VALUE;
        // These arrays are written only when the user enables a recorder.
        // They are not involved in the policy's comparison or randomness.
        float[][] traceTerms=blackbox==null?null:
            new float[ACTIONS][DecisionBlackbox.TERMS];
        float[] traceScores=blackbox==null?null:new float[ACTIONS];
        float[] temporalContext=temporal==null?null:temporal.contextCopy();
        TemporalSkillBank.Advice skillAdvice=
            temporalSkills==null||temporal==null?null:
            temporalSkills.advice(temporalContext,temporal.confidence());
        // Offline counterfactual predictions never train the world model or
        // give reward for imagined outcomes. The holdout gate prevents the
        // planner from amplifying an untrained, inaccurate world model.
        float[] planned=planner.evaluate(senses,new LearnedSequencePlanner.Model(){
            @Override public float[] predict(float[] state,int a){
                return SensorimotorMind.this.prediction(state,a);
            }
            @Override public float value(float[] state,int a){
                return SensorimotorMind.this.estimate(state,a)+contactCredit.value(state,a);
            }
        },diagnostics);
        // Exploration is intrinsic to learning; no action is mapped to a
        // named "correct" behavior. Stable choices come from learned value.
        float largestTemporalBias=0;
        float largestBodyBias=0;
        float largestSpatialBias=0;
        float largestCausalBias=0;
        float largestInstrumentalBias=0;
        float largestIntentionBias=0;
        for(int a=0;a<ACTIONS;a++){
            float q=estimate(senses,a);
            float predictedChange=0;
            for(int d=6;d<S;d++){
                predictedChange+=Math.abs(predictFeature(senses,a,d)-senses[d]);
            }
            float curiosity=.035f*Math.min(3,predictedChange);
            float jitter=(random.nextFloat()-.5f)*(.15f+.18f/(1+experiences/45f));
            // Remembered effects bias a choice but never force it.
            float memoryBias=episodic==null?0:episodic.bias(a,proposed);
            // No hand-crafted energy-colour / angular steering. Only
            // action-values learned from ZERO's own physical experiences
            // and generic curiosity/memory/anti-stall terms influence choice.
            float diversity=motorGuard==null?0:motorGuard.stagnationBias(a);
            // Physically observed outcomes maintain INDEPENDENT energetic
            // and external-displacement values, combined only at choice time.
            // No hidden object identity, hardcoded heading or navigation.
            float earnedValue=contactCredit.value(senses,a);
            // Only a validated lived-death predictor can modulate energy-
            // SOURCE evidence. Never assign death to action, grip or geometry.
            if(mortality!=null)
                earnedValue+=mortality.energyAdvice(senses[0],
                    contactCredit.energeticValue(senses,a));
            // Recurrent recent-history advice is exactly zero until its
            // temporal prediction wins on held-out real transitions.
            float temporalBias=temporal==null?0:temporal.choiceBias(a);
            // 96 physical masses -> 8-sector proprioceptive map; learned
            // action advice is zero until its held-out predictions beat
            // the unchanged-body baseline on real future contacts.
            float bodyBias=bodySchema==null?0:bodySchema.choiceBias(a);
            largestBodyBias=Math.max(largestBodyBias,Math.abs(bodyBias));
            // Learned values from observed 16-direction geometry. Absolutely
            // no spatial policy bias until out-of-training predictions win.
            float spatialBias=spatial==null?0:spatial.choiceBias(a);
            largestSpatialBias=Math.max(largestSpatialBias,Math.abs(spatialBias));
            // Experimental evidence must show heldout predictive advantage
            // AND repeated randomized alternatives in a similar situation.
            float causalBias=causal==null||spatial==null?0:causal.choiceBias(spatial,a);
            largestCausalBias=Math.max(largestCausalBias,Math.abs(causalBias));
            float skillBias=skillAdvice!=null&&skillAdvice.action==a?skillAdvice.bias:0;
            largestTemporalBias=Math.max(largestTemporalBias,Math.abs(temporalBias));
            // Treat old locomotion-centered TD values as a limited prior:
            // walking is a MEANS, not an intrinsically rewarding goal.
            float legacyQ=.38f*Math.max(-2f,Math.min(2f,q));
            float instrumental=navigation==null||!navigationAdviceEnabled?0:
                navigation.bias(a,senses[0]);
            largestInstrumentalBias=Math.max(largestInstrumentalBias,Math.abs(instrumental));
            float intentBias=intention==null||navigation==null||!navigationAdviceEnabled?0:
                intention.bias(navigation,a,senses[0]);
            largestIntentionBias=Math.max(largestIntentionBias,Math.abs(intentBias));
            float score=legacyQ+earnedValue+planned[a]*.50f+curiosity+jitter+memoryBias+
                        diversity+temporalBias+skillBias+bodyBias+spatialBias+causalBias+
                        instrumental+intentBias;
            if(traceTerms!=null){
                traceScores[a]=score;
                float[] t=traceTerms[a];
                t[0]=legacyQ;t[1]=earnedValue;t[2]=planned[a]*.50f;
                t[3]=curiosity;t[4]=jitter;t[5]=memoryBias;t[6]=diversity;
                t[7]=temporalBias;t[8]=skillBias;t[9]=bodyBias;
                t[10]=spatialBias;t[11]=causalBias;t[12]=instrumental;
                t[13]=intentBias;
            }
            if(score>best){best=score;chosen=a;}
        }
        int greedyAction=chosen;
        float epsilon=Math.max(.09f,.52f/(1+experiences/120f));
        if(protectMotor)epsilon=Math.max(epsilon,.28f);
        actionExploratory=false;
        if(random.nextFloat()<epsilon){
            chosen=random.nextInt(ACTIONS);explorations++;
            actionExploratory=true;
        }
        if(motionShadow!=null)motionShadow.chosen(senses,chosen,observedBody);
        if(blackbox!=null){
            blackbox.chosen(senses,chosen,greedyAction,actionExploratory,
                protectMotor,epsilon,traceScores[chosen],traceScores[greedyAction],
                traceTerms[chosen],observedBody);
        }
        if(action!=chosen)actionsReconsidered++;
        if(episodic!=null)episodic.chosen(chosen,proposed);
        if(motorGuard!=null)motorGuard.action(chosen);
        // This is a *self-selected* real action, not the direction of a
        // resource. If later actual contact changes energy, it can be
        // credited retrospectively.
        contactCredit.performed(senses,chosen);
        if(temporalSkills!=null&&temporalContext!=null)
            temporalSkills.chosen(temporalContext,chosen);
        if(temporal!=null){
            temporal.chosen(chosen);
            temporal.noteDecision(largestTemporalBias);
        }
        if(bodySchema!=null){
            bodySchema.chosen(chosen);
            bodySchema.noteDecision(largestBodyBias);
        }
        if(spatial!=null){
            spatial.chosen(chosen);
            spatial.noteDecision(largestSpatialBias);
        }
        if(causal!=null&&spatial!=null){
            causal.chosen(spatial,chosen,actionExploratory);
            causal.noteDecision(largestCausalBias);
        }
        if(navigation!=null){
            navigation.chosen(chosen,observedBody);
            navigation.noteSelection(largestInstrumentalBias);
        }
        if(intention!=null&&navigation!=null){
            if(navigationAdviceEnabled)intention.chosen(navigation,chosen,senses[0]);
            intention.noteDecision(largestIntentionBias);
        }
        actualEnergySinceChoice=0f;
        action=chosen;decisions++;
        estimatedValue=estimate(senses,chosen);
        System.arraycopy(senses,0,trialState,0,S);
        trialAction=chosen;
        elapsed=0;
        return chosen;
    }

    /** Called with one observed outcome of a real action, not an off-screen
        scripted training example. One action has enough time for motor force
        to affect the body (approx 0.65 simulation seconds). */
    public synchronized void observe(float[] after,float dt){
        observe(after,dt,0);
    }
    /**
     * Generic positive motion of a physically contacted obstacle. This is
     * AFTER physics: neither an action command nor a gate/room success label.
     */
    public synchronized void observe(float[] after,float dt,float physicalEffect){
        observe(after,dt,physicalEffect,null,null);
    }
    /** Physical sensor is sampled ONLY at decision boundaries, rather
     * than 96 atan2 calculations for every x20 physics subframe. */
    public synchronized void observe(float[] after,float dt,float physicalEffect,
                                     MotorBrain.Body body,Morphology.Surface skin){
        observe(after,dt,physicalEffect,body,skin,null,null);
    }
    public synchronized void observe(float[] after,float dt,float physicalEffect,
                                     MotorBrain.Body body,Morphology.Surface skin,
                                     ScarcityLab room,ArrayList<UnknownWorld.Entity> objects){
        if(after.length!=S)throw new IllegalArgumentException("sensory width");
        if(trialAction<0){
            if(bodySchema!=null&&body!=null&&skin!=null)
                bodySchema.feed(BodyProprioception.sense(body,skin));
            if(spatial!=null&&body!=null&&objects!=null)
                spatial.observeEnvironment(body,room,objects);
            updateNavigationSense(body,room,objects);
            choose(after);return;
        }
        if(blackbox!=null)blackbox.tick(Math.max(0,dt));
        elapsed+=Math.max(0,dt);
        if(elapsed<.65f)return;
        observedBody=body;
        if(navigation!=null&&body!=null&&objects!=null)
            navigation.sense(body,room,objects);
        float[] bodyAfter=bodySchema!=null&&body!=null&&skin!=null?
            BodyProprioception.sense(body,skin):null;
        if(bodySchema!=null&&bodyAfter!=null)bodySchema.feed(bodyAfter);
        if(spatial!=null&&body!=null&&objects!=null)
            spatial.observeEnvironment(body,room,objects);
        float[] before=trialState;
        boolean holdout=diagnostics.shouldHoldout();
        float err=0,naiveErr=0,sensoryChange=0;
        for(int k=0;k<S;k++){
            float pred=predictFeature(before,trialAction,k);
            float delta=after[k]-pred;
            err+=delta*delta;
            float unchanged=after[k]-before[k];naiveErr+=unchanged*unchanged;
            if(k>=6)sensoryChange+=Math.abs(after[k]-before[k]);
            if(learning&&!holdout){
                float g=MODEL_LR*bounded(delta);
                int base=(trialAction*S+k)*P;
                for(int j=0;j<S;j++)
                    model[base+j]=Math.max(-3,Math.min(3,model[base+j]+g*before[j]));
                model[base+S]=Math.max(-3,Math.min(3,model[base+S]+g));
            }
        }
        float error=(float)Math.sqrt(err/S);
        // "Progress" means correcting a prediction error. It is a generic
        // drive, not a manually recognized moving door or solved room.
        float progress=Float.isNaN(previousError)?0:
                       Math.max(-.10f,Math.min(.10f,previousError-error));
        previousError=error;
        predictionError=error;
        averagePredictionError=experiences==0?error:
                averagePredictionError*.98f+error*.02f;
        // Energy change and external-object displacement are NO LONGER
        // mixed into this generic TD target. Their actual, source-linked
        // consequences train independent value channels above.
        // Moving/turning is NOT the reward. Reward real acquired/lost
        // energy; a very small genuine sensory learning-progress term
        // remains for exploration in the absence of known resources.
        float energyOutcome=clampEnergyReward(actualEnergySinceChoice);
        lastReward=energyOutcome+.025f*Math.min(2,sensoryChange)
                    +.15f*progress-.018f;
        if(temporal!=null){
            temporal.observe(before,trialAction,after,lastReward,error,
                learning&&!holdout,holdout);
            if(temporalSkills!=null)temporalSkills.transition(temporal.contextCopy());
            if(learning&&physicalEffect>.015f){
                temporal.physicalOutcome(physicalEffect);
                if(temporalSkills!=null)temporalSkills.physicalOutcome(physicalEffect);
            }
        }
        if(learning&&physicalEffect>.015f)
            contactCredit.displaced(physicalEffect);
        if(bodySchema!=null&&bodyAfter!=null)
            bodySchema.consequence(bodyAfter,trialAction,lastReward,physicalEffect,
                                   learning&&!holdout,holdout);
        if(spatial!=null&&body!=null&&objects!=null)
            spatial.consequence(trialAction,lastReward,learning&&!holdout,holdout);
        if(causal!=null&&spatial!=null&&body!=null&&objects!=null)
            causal.consequence(spatial,trialAction,lastReward,learning&&!holdout,holdout);
        if(motionShadow!=null)motionShadow.finished(
            trialAction,body,learning,
            navigation==null?0f:navigation.motionEstimateX(trialAction),
            navigation==null?0f:navigation.motionEstimateY(trialAction),
            navigation!=null&&navigation.motionEstimateVerified(trialAction));
        if(navigation!=null&&body!=null)
            navigation.consequence(trialAction,body,learning&&!holdout,holdout);
        if(intention!=null&&navigation!=null&&body!=null&&navigationAdviceEnabled)
            intention.consequence(navigation,trialAction,navigation.latestMovement);
        if(learning&&!holdout){
            float td=bounded(lastReward+GAMMA*bestFuture(after)-estimate(before,trialAction));
            int base=trialAction*P;
            for(int k=0;k<S;k++)
                value[base+k]=Math.max(-4,Math.min(4,value[base+k]+VALUE_LR*td*before[k]));
            value[base+S]=Math.max(-4,Math.min(4,value[base+S]+VALUE_LR*td));
        }
        diagnostics.record(error,(float)Math.sqrt(naiveErr/S),
            lastReward,actionExploratory);
        if(blackbox!=null)blackbox.finished(after,actualEnergySinceChoice,
            physicalEffect,error,body);
        experiences++;
        learnedControl=(float)Math.min(1,experiences/1500.);
        choose(after);
    }

    /** Shared physically observable fields: physiology, tactile feedback,
        motion, eight short-range obstacle rays and twelve spectral sectors. */
    public static final class Senses {
        private Senses(){}
        /** Eight directional sectors of OBSERVED spectral light. This is
         * perception only; no coordinates or semantic names reach learning.
         * A visible inactive (cooling-down) hazard stays optically present. */
        public static float[] spectral8(MotorBrain.Body b,ScarcityLab lab,
                                         ArrayList<UnknownWorld.Entity> objects){
            return spectral8(b,lab,objects,true);
        }
        /** cueEnabled=false is the control for paired perceptual lab tests. */
        public static float[] spectral8(MotorBrain.Body b,ScarcityLab lab,
                                         ArrayList<UnknownWorld.Entity> objects,
                                         boolean cueEnabled){
            float[] v=new float[UnknownWorld.SIGNATURES*8];
            if(b==null||objects==null)return v;
            for(UnknownWorld.Entity e:objects){
                if(e.consumed||e.concealed)continue;
                float dx=e.x-b.cx,dy=e.y-b.cy;
                float d=(float)Math.hypot(dx,dy);
                if(!Float.isFinite(d)||d<.1f||d>210f)continue;
                if(occluded(lab,b.cx,b.cy,e.x,e.y,objects,e,cueEnabled))continue;
                int sector=((int)Math.floor((Math.atan2(dy,dx)+Math.PI/8)/
                    (Math.PI/4))+8)%8;
                float bright=Math.max(0f,1f-d/230f);
                v[e.signature*8+sector]=Math.max(v[e.signature*8+sector],bright);
                if(e.isRare())v[2*8+sector]=Math.max(v[2*8+sector],bright*.85f);
            }
            if(cueEnabled){
                for(UnknownWorld.Entity e:objects){
                    UnknownWorld.Entity cover=OpticalCues.coverOf(e,objects);
                    if(cover==null)continue;
                    float px=OpticalCues.rimX(b.cx,b.cy,cover),
                          py=OpticalCues.rimY(b.cx,b.cy,cover);
                    float dx=px-b.cx,dy=py-b.cy;
                    float strength=OpticalCues.leakBrightness(
                        (float)Math.hypot(dx,dy));
                    if(strength<=0||
                       occluded(lab,b.cx,b.cy,px,py,objects,e,true))continue;
                    int sector=((int)Math.floor((Math.atan2(dy,dx)+Math.PI/8)/
                        (Math.PI/4))+8)%8;
                    v[e.signature*8+sector]=Math.max(v[e.signature*8+sector],strength);
                    // Existing rare-wavelength mixing; no new category label.
                    v[2*8+sector]=Math.max(v[2*8+sector],strength*.85f);
                }
            }
            return v;
        }
        public static float[] read(MotorBrain.Body b,Morphology.Surface skin,
                                   ScarcityLab lab,ArrayList<UnknownWorld.Entity> objects,
                                   float energy){
            return read(b,skin,lab,objects,energy,true);
        }
        /** Paired baseline mode is for test fixtures only, not a new motor input. */
        public static float[] read(MotorBrain.Body b,Morphology.Surface skin,
                                   ScarcityLab lab,ArrayList<UnknownWorld.Entity> objects,
                                   float energy,boolean cueEnabled){
            float[] v=new float[S];
            v[0]=Math.max(0,Math.min(1,energy/100));
            v[1]=bounded(b.cvx/150f);
            v[2]=bounded(b.cvy/150f);
            v[3]=Math.max(0,Math.min(1,skin.peakExtension/110f));
            int contact=0,grip=0;
            for(int j=0;j<Morphology.POINTS;j++){
                if(skin.touchSense[j]>.2f)contact++;
                if(skin.gripHold[j]>=Morphology.GRIP_HOLD)grip++;
            }
            v[4]=(float)contact/Morphology.POINTS;
            v[5]=(float)grip/Morphology.POINTS;
            for(int r=0;r<8;r++){
                float ang=(float)(r*Math.PI/4);
                float vx=(float)Math.cos(ang),vy=(float)Math.sin(ang);
                float proximity=0;
                for(int step=1;step<=7;step++){
                    float distance=step*22;
                    float x=b.cx+vx*distance,y=b.cy+vy*distance;
                    if(solid(lab,x,y,objects)){
                        proximity=1-distance/175f;break;
                    }
                }
                v[6+r]=proximity;
            }
            // Raw channel identities are VISUAL frequencies, not "food",
            // "danger" or action values. Occluded objects are invisible.
            for(UnknownWorld.Entity e:objects){
                if(e.consumed||e.concealed)continue;
                float dx=e.x-b.cx,dy=e.y-b.cy;
                float d=(float)Math.hypot(dx,dy);
                if(d>210||d<.1f)continue;
                int sector=(int)Math.floor((Math.atan2(dy,dx)+Math.PI)/(Math.PI/2));
                sector=Math.max(0,Math.min(3,sector));
                if(occluded(lab,b.cx,b.cy,e.x,e.y,objects,e,cueEnabled))continue;
                int channel=14+e.signature*4+sector;
                if(channel<14||channel>=S)continue;
                float bright=1-d/230f;
                v[channel]=Math.max(v[channel],bright);
                // The rare resource emits a SECOND visual wavelength. Its
                // physical effect is not supplied to the learner as a label.
                // Existing 26-dimensional weight matrices need no migration.
                if(e.isRare())v[14+2*4+sector]=Math.max(v[14+2*4+sector],bright*.85f);
            }
            if(cueEnabled){
                // Optical source is the same existing +40 energy beneath the
                // physical square. Only weak light escaping its near rim is
                // detectable; energy and body cannot cross the cover.
                for(UnknownWorld.Entity e:objects){
                    UnknownWorld.Entity cover=OpticalCues.coverOf(e,objects);
                    if(cover==null)continue;
                    float px=OpticalCues.rimX(b.cx,b.cy,cover),
                          py=OpticalCues.rimY(b.cx,b.cy,cover);
                    float dx=px-b.cx,dy=py-b.cy;
                    float brightness=OpticalCues.leakBrightness(
                        (float)Math.hypot(dx,dy));
                    if(brightness<=0||
                       occluded(lab,b.cx,b.cy,px,py,objects,e,true))continue;
                    int sector=(int)Math.floor((Math.atan2(dy,dx)+Math.PI)/(Math.PI/2));
                    sector=Math.max(0,Math.min(3,sector));
                    v[14+e.signature*4+sector]=
                        Math.max(v[14+e.signature*4+sector],brightness);
                    v[14+2*4+sector]=Math.max(v[14+2*4+sector],brightness*.85f);
                }
            }
            return v;
        }
        private static boolean occluded(ScarcityLab lab,float ax,float ay,float bx,float by,
                                        ArrayList<UnknownWorld.Entity> objects,UnknownWorld.Entity self,
                                        boolean transparentGlass){
            // Real movable solids occlude light even outside the chamber.
            for(int i=1;i<15;i++){
                float t=i/15f;
                if(solid(lab,ax+(bx-ax)*t,ay+(by-ay)*t,objects,self,
                         transparentGlass))return true;
            }
            return false;
        }
        static boolean solid(ScarcityLab lab,float x,float y,ArrayList<UnknownWorld.Entity> obj){
            return solid(lab,x,y,obj,null,false);
        }
        private static boolean solid(ScarcityLab lab,float x,float y,ArrayList<UnknownWorld.Entity> obj,
                                     UnknownWorld.Entity ignore,boolean transparentGlass){
            if(lab!=null){
                float l=lab.entranceX(),r=lab.ox+ScarcityLab.END_X;
                float t=lab.oy+ScarcityLab.TOP,b=lab.oy+ScarcityLab.BOTTOM;
                float w=ScarcityLab.WALL_HALF_THICKNESS;
                boolean front=x>=l-w&&x<=l+w&&y>=t-10&&y<=b+10&&
                    (y<=lab.oy-ScarcityLab.OPEN_HALF_HEIGHT||
                     y>=lab.oy+ScarcityLab.OPEN_HALF_HEIGHT)&&
                    !(transparentGlass&&OpticalCues.frontWindow(lab,x,y));
                boolean ceiling=x>=l-w&&x<=r+w&&y>=t-10&&y<=t+w;
                boolean floor=x>=l-w&&x<=r+w&&y>=b-w&&y<=b+10;
                boolean back=x>=r-w&&x<=r+w&&y>=t-10&&y<=b+10;
                boolean gate=x>=lab.gateX()-ScarcityLab.DOOR_HALF_W&&
                    x<=lab.gateX()+ScarcityLab.DOOR_HALF_W&&
                    y>=lab.gateTop()&&y<=lab.gateBottom();
                if(front||ceiling||floor||back||gate)return true;
            }
            for(UnknownWorld.Entity e:obj){
                if(e==ignore||e.concealed||!e.ready())continue;
                if(e.isBox()&&Math.abs(x-e.x)<=SquareGripPuzzle.HALF&&
                   Math.abs(y-e.y)<=SquareGripPuzzle.HALF)return true;
                if(!e.isMovable())continue;
                if(Math.hypot(x-e.x,y-e.y)<e.radius)return true;
            }
            return false;
        }
    }

    /** Text form persisted only at suspend/save; invalid data leaves
        currently initialized model untouched. */
    public synchronized String serialize(){
        StringBuilder s=new StringBuilder("wm1|").append(experiences).append('|')
            .append(explorations).append('|').append(decisions).append('|')
            .append(averagePredictionError).append('|').append(action);
        for(float f:model)s.append('|').append(f);
        for(float f:value)s.append('|').append(f);
        return s.toString();
    }
    public synchronized boolean restore(String saved){
        if(saved==null||saved.length()==0)return false;
        String[] entries=saved.split("\\|");
        if(entries.length!=6+PARAMETERS||!"wm1".equals(entries[0]))return false;
        try{
            long exp=Long.parseLong(entries[1]),ex=Long.parseLong(entries[2]),dec=Long.parseLong(entries[3]);
            float err=Float.parseFloat(entries[4]);int a=Integer.parseInt(entries[5]);
            if(exp<0||ex<0||dec<0||a<0||a>=ACTIONS||!Float.isFinite(err)||err<0)return false;
            float[] m=new float[FORWARD_WEIGHTS],q=new float[VALUE_WEIGHTS];
            for(int i=0;i<m.length;i++){
                m[i]=Float.parseFloat(entries[i+6]);
                if(!Float.isFinite(m[i])||Math.abs(m[i])>4)return false;
            }
            for(int i=0;i<q.length;i++){
                q[i]=Float.parseFloat(entries[i+6+m.length]);
                if(!Float.isFinite(q[i])||Math.abs(q[i])>5)return false;
            }
            System.arraycopy(m,0,model,0,m.length);
            System.arraycopy(q,0,value,0,q.length);
            experiences=exp;explorations=ex;decisions=dec;averagePredictionError=err;action=a;
            trialAction=-1;elapsed=0;
            return true;
        }catch(RuntimeException e){return false;}
    }

    /** Controlled tests, not a declaration that the actual live organism
        can already infer opening a new door. */
    public static void main(String[] args){
        SensorimotorMind mind=new SensorimotorMind();
        float[] s=new float[S],t=new float[S];
        mind.choose(s);
        // Strictly compare prediction against controlled transition, then learn.
        float errBefore=0,errAfter=0;
        for(int i=0;i<200;i++){
            int a=mind.action;
            t[0]=.5f;
            t[6]=a==3?.85f:.05f;
            mind.observe(t,.67f);
            System.arraycopy(t,0,s,0,S);
        }
        int chosen=mind.action;
        if(mind.experiences!=200||mind.decisions<201||mind.parameterCount()!=PARAMETERS)
            throw new IllegalStateException("Online transition experience missing");
        float[] before=mind.prediction(s,chosen);
        for(int i=0;i<30;i++)mind.observe(s,.67f);
        float[] after=mind.prediction(s,mind.action);
        for(float v:after)if(!Float.isFinite(v))throw new IllegalStateException("Nonfinite prediction");
        SensorimotorMind loaded=new SensorimotorMind();
        if(!loaded.restore(mind.serialize())||loaded.experiences!=mind.experiences
           ||Math.abs(loaded.estimatedValue)>1000)
            throw new IllegalStateException("Persistent model rejected");
        String corrupt=mind.serialize().replaceFirst("wm1","broken");
        if(loaded.restore(corrupt)||loaded.experiences!=mind.experiences)
            throw new IllegalStateException("Corrupted snapshot overwrote good model");
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        objects.add(new UnknownWorld.Entity(50,0,0,0));
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        float[] reading=Senses.read(body,skin,null,objects,55);
        if(reading.length!=S||reading[0]!=.55f)
            throw new IllegalStateException("Physical sensory readings failed");
        // Occlusion test: geometric wall blocks VISUAL channel, even when
        // the source is elsewhere in world data.
        ScarcityLab lab=new ScarcityLab(-115,0);
        objects.clear();
        objects.add(lab.food);
        body.cx=-55;body.cy=0;
        float[] hidden=Senses.read(body,skin,lab,objects,55);
        float sum=0;for(int i=14;i<S;i++)sum+=hidden[i];
        if(sum>.01f)throw new IllegalStateException("Learner can see through closed barrier");
        SensorimotorMind controlled=new SensorimotorMind();
        float[] base=new float[S],outcome=new float[S];
        outcome[7]=.72f;outcome[18]=.41f;
        float initialLoss=0,finalLoss=0;
        for(int a=0;a<ACTIONS;a++){
            float[] y=controlled.prediction(base,a);
            initialLoss+=(y[7]-.72f)*(y[7]-.72f);
            initialLoss+=(y[18]-.41f)*(y[18]-.41f);
        }
        for(int n=0;n<760;n++){
            controlled.resetEpisode(base);
            controlled.choose(base);
            controlled.observe(outcome,.70f);
        }
        for(int a=0;a<ACTIONS;a++){
            float[] y=controlled.prediction(base,a);
            finalLoss+=(y[7]-.72f)*(y[7]-.72f);
            finalLoss+=(y[18]-.41f)*(y[18]-.41f);
        }
        if(!(finalLoss<initialLoss*.76f))
            throw new IllegalStateException("Forward model did not learn repeated sensory consequences: "+
                initialLoss+" -> "+finalLoss);
        System.out.printf(Locale.US,
            "ZERO-009 predictive learning: initial error=%.4f learned error=%.4f%n",
            initialLoss,finalLoss);
        System.out.printf(Locale.US,
            "ZERO-009 passed: senses=%d actions=%d learned_weights=%d online_exp=%d prediction_error=%.4f persistent=true occlusion=true%n",
            S,ACTIONS,PARAMETERS,mind.experiences,mind.averagePredictionError);
    }
}
