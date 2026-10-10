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
    // Performed-action traces give temporally distant actual resource
    // contacts a learning signal. They NEVER add a geometric direction bias.
    private final ExperienceCredit contactCredit=new ExperienceCredit();
    public synchronized void contactOutcome(float actualEnergyDelta,int visuallyObservedSignature){
        if(learning)contactCredit.consequence(actualEnergyDelta,visuallyObservedSignature);
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
    }
    public synchronized void resetEpisode(float[] first){
        // Mortality resets bodily position, never destroys the learned model.
        if(first==null){trialAction=-1;elapsed=0;contactCredit.newLife();return;}
        begin(first);
    }
    public synchronized float motorX(){return (float)Math.cos((action%8)*Math.PI/4);}
    public synchronized float motorY(){return (float)Math.sin((action%8)*Math.PI/4);}
    public synchronized float adhesion(){return action>=8?.82f:0f;}
    public synchronized int decisionCount(){return (int)Math.min(Integer.MAX_VALUE,decisions);}
    public synchronized float[] stateCopy(){return trialState.clone();}
    public synchronized int parameterCount(){return PARAMETERS;}

    private static float bounded(float x){return Math.max(-1.5f,Math.min(1.5f,x));}
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
        // Exploration is intrinsic to learning; no action is mapped to a
        // named "correct" behavior. Stable choices come from learned value.
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
            float score=q+earnedValue+curiosity+jitter+memoryBias+diversity;
            if(score>best){best=score;chosen=a;}
        }
        float epsilon=Math.max(.09f,.52f/(1+experiences/120f));
        if(protectMotor)epsilon=Math.max(epsilon,.28f);
        actionExploratory=false;
        if(random.nextFloat()<epsilon){
            chosen=random.nextInt(ACTIONS);explorations++;
            actionExploratory=true;
        }
        if(action!=chosen)actionsReconsidered++;
        if(episodic!=null)episodic.chosen(chosen,proposed);
        if(motorGuard!=null)motorGuard.action(chosen);
        // This is a *self-selected* real action, not the direction of a
        // resource. If later actual contact changes energy, it can be
        // credited retrospectively.
        contactCredit.performed(senses,chosen);
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
        if(after.length!=S)throw new IllegalArgumentException("sensory width");
        if(trialAction<0){choose(after);return;}
        elapsed+=Math.max(0,dt);
        if(elapsed<.65f)return;
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
        // Reward only actual measured velocity aligned with ZERO's own
        // selected action. No target coordinates and no synthetic impulse.
        float ax=(float)Math.cos((trialAction%8)*Math.PI/4);
        float ay=(float)Math.sin((trialAction%8)*Math.PI/4);
        float realForward=Math.max(-1f,Math.min(1f,after[1]*ax+after[2]*ay));
        lastReward=.10f*Math.min(2,sensoryChange)
                    +.24f*realForward
                    +.3f*progress-.012f;
        if(learning&&physicalEffect>.015f)
            contactCredit.displaced(physicalEffect);
        if(learning&&!holdout){
            float td=bounded(lastReward+GAMMA*bestFuture(after)-estimate(before,trialAction));
            int base=trialAction*P;
            for(int k=0;k<S;k++)
                value[base+k]=Math.max(-4,Math.min(4,value[base+k]+VALUE_LR*td*before[k]));
            value[base+S]=Math.max(-4,Math.min(4,value[base+S]+VALUE_LR*td));
        }
        diagnostics.record(error,(float)Math.sqrt(naiveErr/S),
            lastReward,actionExploratory);
        experiences++;
        learnedControl=(float)Math.min(1,experiences/1500.);
        choose(after);
    }

    /** Shared physically observable fields: physiology, tactile feedback,
        motion, eight short-range obstacle rays and twelve spectral sectors. */
    public static final class Senses {
        private Senses(){}
        public static float[] read(MotorBrain.Body b,Morphology.Surface skin,
                                   ScarcityLab lab,ArrayList<UnknownWorld.Entity> objects,
                                   float energy){
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
                if(!e.ready())continue;
                float dx=e.x-b.cx,dy=e.y-b.cy;
                float d=(float)Math.hypot(dx,dy);
                if(d>210||d<.1f)continue;
                int sector=(int)Math.floor((Math.atan2(dy,dx)+Math.PI)/(Math.PI/2));
                sector=Math.max(0,Math.min(3,sector));
                if(occluded(lab,b.cx,b.cy,e.x,e.y,objects,e))continue;
                int channel=14+e.signature*4+sector;
                if(channel<14||channel>=S)continue;
                float bright=1-d/230f;
                v[channel]=Math.max(v[channel],bright);
                // The rare resource emits a SECOND visual wavelength. Its
                // physical effect is not supplied to the learner as a label.
                // Existing 26-dimensional weight matrices need no migration.
                if(e.isRare())v[14+2*4+sector]=Math.max(v[14+2*4+sector],bright*.85f);
            }
            return v;
        }
        private static boolean occluded(ScarcityLab lab,float ax,float ay,float bx,float by,
                                        ArrayList<UnknownWorld.Entity> objects,UnknownWorld.Entity self){
            if(lab==null)return false;
            for(int i=1;i<15;i++){
                float t=i/15f;
                if(solid(lab,ax+(bx-ax)*t,ay+(by-ay)*t,objects,self))return true;
            }
            return false;
        }
        static boolean solid(ScarcityLab lab,float x,float y,ArrayList<UnknownWorld.Entity> obj){
            return solid(lab,x,y,obj,null);
        }
        private static boolean solid(ScarcityLab lab,float x,float y,ArrayList<UnknownWorld.Entity> obj,
                                     UnknownWorld.Entity ignore){
            if(lab!=null){
                float l=lab.entranceX(),r=lab.ox+ScarcityLab.END_X;
                float t=lab.oy+ScarcityLab.TOP,b=lab.oy+ScarcityLab.BOTTOM;
                float w=ScarcityLab.WALL_HALF_THICKNESS;
                boolean front=x>=l-w&&x<=l+w&&y>=t-10&&y<=b+10&&
                    (y<=lab.oy-ScarcityLab.OPEN_HALF_HEIGHT||
                     y>=lab.oy+ScarcityLab.OPEN_HALF_HEIGHT);
                boolean ceiling=x>=l-w&&x<=r+w&&y>=t-10&&y<=t+w;
                boolean floor=x>=l-w&&x<=r+w&&y>=b-w&&y<=b+10;
                boolean back=x>=r-w&&x<=r+w&&y>=t-10&&y<=b+10;
                boolean gate=x>=lab.gateX()-ScarcityLab.DOOR_HALF_W&&
                    x<=lab.gateX()+ScarcityLab.DOOR_HALF_W&&
                    y>=lab.gateTop()&&y<=lab.gateBottom();
                if(front||ceiling||floor||back||gate)return true;
            }
            for(UnknownWorld.Entity e:obj){
                if(e==ignore||!e.isMovable()||!e.ready())continue;
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
