package com.overmind.zero;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/** Active sensory recurrent controller. No simulator object, target or recipe.
 * Fixed recurrent reservoir + learned action-conditioned readouts (NOT Dreamer).
 * Real energy, terminal and body outcomes train the controller; imagination
 * never produces training samples. Held-out transitions never enter replay.
 */
public final class RecurrentCoreV22 {
    public static final String KEY="recurrent_core_v22";
    public static final int A=16,F=164,H=24,Z=F+H,HEAD=12;
    private static final int WIDTH=Z+1,RECENT=72,RARE=24;
    private final float[] dynamics=new float[A*F*HEAD],q=new float[A*WIDTH],
        reward=new float[A*WIDTH],risk=new float[A*WIDTH];
    private final NeurogenesisV22 neuralGrowth=new NeurogenesisV22();
    private final int[] samples=new int[A],validation=new int[A];
    private final float[] modelError=new float[A],baselineError=new float[A],
        energyError=new float[A],energyBase=new float[A];
    private final float[] hidden=new float[H],current=new float[F];
    private float[] pending;
    private int pendingAction=-1,recentCursor,rareCursor;
    private final ArrayList<Experience> recent=new ArrayList<>(),rare=new ArrayList<>();
    private final Random replayRandom=new Random(22013);
    private long transitions,trainSteps,replays,choices,modelChoices,invalid,terminals;
    private int lastAction=-1;
    private float lastMargin,lastConfidence;
    // Runtime-only diagnostics: no persistence schema change or memory reset.
    private long primaryDecisionsSession;
    private int intervalsWithoutEnergy;
    private static final class Experience {
        float[] before,after;int action;float earned,harm;boolean terminal;
        Experience(float[] b,float[] n,int a,float r,float d,boolean t){
            before=b.clone();after=n.clone();action=a;earned=r;harm=d;terminal=t;
        }
    }
    private static float clip(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    private static boolean valid(float[] x,int n){
        if(x==null||x.length!=n)return false;
        for(float v:x)if(!Float.isFinite(v)||Math.abs(v)>1.601f)return false;
        return true;
    }
    /** 26 senses + 16x5 measured membrane channels + 16x2 physical eye
     * offsets + 16 observed depth rays + four anonymous focus features + six projections of ACTUALLY APPLIED control coefficients.
     * No entity identity or energy colour semantics are added.
     */
    public void sense(float[] s,float[] local,float[] origins,float[] controls){
        sense(s,local,origins,controls,null,null);
    }
    public void sense(float[] s,float[] local,float[] origins,float[] controls,float[] rays,float[] belief){
        if(!valid(s,26)){invalid++;pending=null;pendingAction=-1;return;}
        Arrays.fill(current,0);System.arraycopy(s,0,current,0,26);
        int[] channels={DistributedBodySense.TOUCH,DistributedBodySense.GRIP_HELD,
            DistributedBodySense.SHAPE,DistributedBodySense.STRAIN,DistributedBodySense.OPTIC};
        if(local!=null&&local.length==DistributedBodySense.FEATURES){
            for(int i=0;i<Morphology.POINTS;i++)for(int c=0;c<5;c++){
                float v=local[i*DistributedBodySense.CHANNELS+channels[c]];
                if(Float.isFinite(v))current[26+(i*16/Morphology.POINTS)*5+c]+=clip(v,-1,1)/6f;
            }
        }
        if(origins!=null&&origins.length==32)for(int i=0;i<32;i++)
            current[106+i]=Float.isFinite(origins[i])?clip(origins[i]/160f,-1,1):0;
        if(controls!=null&&controls.length==6)for(int i=0;i<6;i++)
            current[138+i]=Float.isFinite(controls[i])?clip(controls[i],-1,1):0;
        if(rays!=null&&rays.length==16)for(int i=0;i<16;i++)
            current[144+i]=Float.isFinite(rays[i])?clip(rays[i],0,1):1;
        if(belief!=null&&belief.length==4)for(int i=0;i<4;i++)
            current[160+i]=Float.isFinite(belief[i])?clip(belief[i],-1,1):0;
    }
    /** Called by Android with real weights, no mutation or execution here. */
    public static float[] controlState(float[] motor,float[] hand,float[] membrane){
        float[] result=new float[6];float[][] groups={motor,hand,membrane};
        for(int g=0;g<3;g++)if(groups[g]!=null&&groups[g].length>0){
            for(int i=0;i<groups[g].length;i++){
                float v=groups[g][i];if(!Float.isFinite(v))continue;
                result[2*g]+=v/groups[g].length/5f;
                result[2*g+1]+=v*((i&1)==0?1:-1)/groups[g].length/5f;
            }
        }
        return result;
    }
    // Same recurrence for lived and imagined states. Pure, deterministic;
    // rolling out never changes hidden, counters, replay, weights or RNG.
    private float[] advance(float[] x,float[] memory,int action){
        float[] next=new float[H];
        for(int h=0;h<H;h++){
            float sum=.45f*memory[h]+.15f*memory[(h+H-1)%H];
            for(int k=0;k<12;k++){
                int f=(h*19+k*11)%F;
                sum+=x[f]*(((h+k)&1)==0?.10f:-.10f);
            }
            if(action>=0)sum+=((action+3*h)%7-3)*.035f;
            next[h]=(float)Math.tanh(sum);
        }
        return next;
    }
    private static float[] join(float[] x,float[] h){
        float[] z=new float[Z];System.arraycopy(x,0,z,0,F);System.arraycopy(h,0,z,F,H);return z;
    }
    public float[] state(){return join(current,hidden);}
    private float[] head(float[] z,int out){
        int h=out%H;
        return new float[]{1,z[out],z[(out+F-1)%F],z[(out+1)%F],
            z[0],z[1],z[2],z[4],z[F+h],z[F+(h+7)%H],z[F+(h+13)%H],z[138+(out%6)]};
    }
    private float forecastDelta(float[] z,int action,int out){
        int h=out%H,off=(action*F+out)*HEAD;
        float sum=dynamics[off]+dynamics[off+1]*z[out]+dynamics[off+2]*z[(out+F-1)%F]+
            dynamics[off+3]*z[(out+1)%F]+dynamics[off+4]*z[0]+dynamics[off+5]*z[1]+
            dynamics[off+6]*z[2]+dynamics[off+7]*z[4]+dynamics[off+8]*z[F+h]+
            dynamics[off+9]*z[F+(h+7)%H]+dynamics[off+10]*z[F+(h+13)%H]+dynamics[off+11]*z[138+(out%6)];
        return clip(sum,-.75f,.75f);
    }
    public float[] imagine(float[] z,int action){
        if(!valid(z,Z)||action<0||action>=A)return null;
        float[] x=new float[F];
        for(int j=0;j<F;j++)x[j]=clip(z[j]+forecastDelta(z,action,j),j==1||j==2||(j>=106&&j<144)||j==160||j==161?-1f:0f,1f);
        // The control coefficients do not spontaneously mutate in imagination.
        System.arraycopy(z,138,x,138,6);
        return join(x,advance(x,Arrays.copyOfRange(z,F,Z),action));
    }
    private float read(float[] weights,float[] z,int action){
        int off=action*WIDTH;float sum=weights[off+Z];
        for(int k=0;k<Z;k++)sum+=weights[off+k]*z[k];
        return clip(sum,-2,2);
    }
    private void fit(float[] weights,float[] z,int action,float target,float lr){
        int off=action*WIDTH;float norm=1;
        for(float v:z)norm+=v*v;
        float grad=lr*clip(target-read(weights,z,action),-2,2)/norm;
        for(int k=0;k<Z;k++)weights[off+k]=clip(weights[off+k]+grad*z[k],-3,3);
        weights[off+Z]=clip(weights[off+Z]+grad,-3,3);
    }
    private float value(float[] z,int a){return read(q,z,a)-.30f*Math.max(0,read(risk,z,a));}
    private int greedy(float[] z){int win=0;float best=-Float.MAX_VALUE;
        for(int a=0;a<A;a++){float v=value(z,a);if(v>best){win=a;best=v;}}return win;}
    /** Split existing holdout MSE into energy and other 157 sensory channels.
     * All EMA components came from the same held-out experiences; no save migration. */
    public float confidence(int a){
        if(a<0||a>=A||samples[a]<24||validation[a]<8)return 0;
        float physicalModel=Math.max(0,modelError[a]*(F-6)-energyError[a])/(F-7);
        float physicalBase=Math.max(0,baselineError[a]*(F-6)-energyBase[a])/(F-7);
        if(physicalBase<.00001f||physicalModel>=physicalBase*.96f)return 0;
        return clip(1-physicalModel/physicalBase,0,.8f);
    }
    /** Only trusted energy forecasts may authorize an imagined energy future. */
    public float energyConfidence(int a){
        float physical=confidence(a);
        return physical>0&&energyError[a]<=energyBase[a]+.00002f?physical:0;
    }
    private float rollout(float[] start,int first){
        float[] z=start;float total=0,discount=1;int a=first;
        for(int depth=0;depth<3;depth++){
            float trust=energyConfidence(a);
            if(trust<=0)break;
            total+=discount*(read(reward,z,a)-.30f*Math.max(0,read(risk,z,a)));
            z=imagine(z,a);discount*=.90f;a=greedy(z);
        }
        return total+(energyConfidence(a)>0?discount*value(z,a):0);
    }
    /** Returns a score for EVERY action BEFORE the real choice. Old scores
     * are a bounded migration prior which decays with actual new evidence.
     * Information seeking is affordable and bounded, never a colour recipe.
     */
    public float[] evaluate(float[] oldScores){
        float[] z=state(),scores=new float[A];
        float maturity=learningBlend();
        lastConfidence=0;
        for(int a=0;a<A;a++){
            float physicalTrust=confidence(a),energyTrust=energyConfidence(a);
            lastConfidence=Math.max(lastConfidence,physicalTrust);
            float prior=oldScores==null?0:oldScores[a];
            float information=.009f*clip(current[0]/.4f,0,1)/(float)Math.sqrt(1+samples[a]);
            // Value and risk heads learn from real outcomes. Never use
            // multi-step energy imagination without independent energy validity.
            float learned=value(z,a)+(energyTrust>0?.35f*energyTrust*(rollout(z,a)-value(z,a)):0)+information;
            // Preserve old decisions with no validated physical evidence.
            // Physical-only evidence can cautiously use observed-outcome value.
            float authority=oldScores==null?1f:energyTrust>0?
                Math.min(.85f,maturity*energyTrust):
                Math.min(.16f,.35f*maturity*physicalTrust);
            float delta=authority*(learned-prior);
            if(energyTrust<=0)delta=clip(delta,-.12f,.12f);
            scores[a]=oldScores==null?learned:prior+delta;
        }
        int best=0;float runner=-Float.MAX_VALUE;
        for(int a=1;a<A;a++)if(scores[a]>scores[best])best=a;
        for(int a=0;a<A;a++)if(a!=best)runner=Math.max(runner,scores[a]);
        lastMargin=scores[best]-runner;choices++;
        // Historical planned counter remains energy-validated, not physical-only.
        if(energyConfidence(best)>0)modelChoices++;
        // OTA131 safety boundary: The 2.2 readout is still trained, evaluated
        // and checkpointed, but NO 2.2 value/risk prediction may control the
        // organism before independent held-out OUTCOME validation exists.
        // A good world-state forecast is not proof of a good action.
        // Existing expert/skill signals win unchanged, including low-energy
        // survival and opposing-face box manipulation. This boundary also
        // blocks transient false-positive energyConfidence spikes.
        return oldScores==null?scores:oldScores.clone();
    }
    /** SINGLE LIVE ARBITER (OTA132). The legacy TD policy is never queried.
     * The external array contains only empirically learned, bounded
     * affordance evidence from navigation/causal/object subsystems; it is
     * NOT a second policy or a privileged resource heading.
     * Q/reward/risk and recurrence remain the actual primary brain.
     */
    public float[] primaryScores(float[] learnedAffordance){
        float[] scores=evaluate(null);
        for(int a=0;a<A;a++){
            float evidence=learnedAffordance!=null&&learnedAffordance.length==A
                &&Float.isFinite(learnedAffordance[a])
                ?clip(learnedAffordance[a],-1.2f,1.2f):0;
            // The reward learner owns the main score. Empirical motor/energy
            // affordance is a subordinate, evidence-backed feature.
            scores[a]+= .40f*evidence;
            // Experiences per action are real, not imagined. Mild intrinsic
            // action uncertainty preserves discovery while reusing skills.
            // New neurons join the SAME 2.2 arbiter after independent proof.
            scores[a]+=neuralGrowth.actionCorrection(state(),a);
            scores[a]+=.075f/(float)Math.sqrt(1+samples[a]/16f);
            if(a==lastAction&&intervalsWithoutEnergy>=10)
                scores[a]-=.14f*clip((intervalsWithoutEnergy-10)/20f,0,1);
        }
        lastMargin=0;
        int best=0;for(int a=1;a<A;a++)if(scores[a]>scores[best])best=a;
        float second=-Float.MAX_VALUE;
        for(int a=0;a<A;a++)if(a!=best)second=Math.max(second,scores[a]);
        lastMargin=scores[best]-second;
        primaryDecisionsSession++;
        return scores;
    }
    /** Exploration belongs to the primary learner. Sparse energy makes
     * experimentation MORE likely, not less. No target, colour or script. */
    public float primaryExploration(float reserve){
        return clip(.12f+.10f*(1f-clip(reserve,0,1))
            +.09f*clip(intervalsWithoutEnergy/70f,0,1),.12f,.31f);
    }
    /** N-step credit for a lived, uniquely traced earlier intervention.
     * External evidence must already have passed a held-out predictive gate.
     * The user never gives a resource coordinate or a scripted action.
     * Existing rc22 save schema, recurrence and replay are unchanged.
     * This does NOT create a synthetic physical transition or a fake reward:
     * it modifies only the value of an actually performed earlier action.
     */
    public void learnVerifiedDelayedCredit(float[] observedPrior,int action,
                                          float realPositiveContact,float trust){
        if(action<0||action>=A||!valid(observedPrior,Z)||
           !Float.isFinite(realPositiveContact)||realPositiveContact<=0||
           !Float.isFinite(trust)||trust<=0||trust>.35f)return;
        float delta=clip(realPositiveContact/22f,0,1.8f);
        float target=clip(read(q,observedPrior,action)+
            .32f*trust*delta,-2f,2f);
        fit(q,observedPrior,action,target,.12f);
    }
    public long primaryDecisionsSession(){return primaryDecisionsSession;}
    public int intervalsWithoutEnergy(){return intervalsWithoutEnergy;}
    public float learningBlend(){
        long actualTrain=0;for(int n:samples)actualTrain+=n;
        return Math.min(.9f,actualTrain/1600f);
    }
    /** OTA131: preserve incumbent exploration EXACTLY.
     * A transient heldout energy MSE advantage must never suppress exploration
     * or lock a starving ZERO into one repeated action.
     */
    public float exploration(float previous){
        return previous;
    }
    public void chosen(int a){
        if(a<0||a>=A){invalid++;pending=null;pendingAction=-1;return;}
        pending=state();pendingAction=a;lastAction=a;
    }
    private void train(Experience e,boolean model){
        int a=e.action;
        if(model)for(int j=0;j<F;j++){
            if(j>=138&&j<144)continue;
            float[] x=head(e.before,j);float error=e.after[j]-e.before[j]-forecastDelta(e.before,a,j);
            float norm=1;for(float v:x)norm+=v*v;
            float grad=.10f*clip(error,-1,1)/norm;int off=(a*F+j)*HEAD;
            for(int k=0;k<HEAD;k++)dynamics[off+k]=clip(dynamics[off+k]*.99999f+grad*x[k],-2,2);
        }
        fit(reward,e.before,a,e.earned,.18f);
        fit(risk,e.before,a,e.harm,.12f);
        float future=e.terminal?0:value(e.after,greedy(e.after));
        fit(q,e.before,a,clip(e.earned+.90f*future,-2,2),.18f);
        trainSteps++;
    }
    /** One actual executed decision interval, including terminal intervals.
     * net reserve change includes hunger and weather; contact harm is measured
     * separately. Never train on imagined reward or a held-out experience.
     */
    public void consequence(float actualContactEnergy,float actualDamage,boolean train,boolean holdout,boolean terminal){
        consequence(actualContactEnergy,actualDamage,0f,train,holdout,terminal);
    }
    /** Verified external displacement has a small separate learning signal:
     * it is never granted for mere touch, grip command or imaginary motion. */
    public void consequence(float actualContactEnergy,float actualDamage,
                            float verifiedExternalMotion,boolean train,boolean holdout,boolean terminal){
        if(pending==null||pendingAction<0)return;
        if(!Float.isFinite(actualContactEnergy)||!Float.isFinite(actualDamage)||actualDamage<0||
           !Float.isFinite(verifiedExternalMotion)||verifiedExternalMotion<0){
            invalid++;pending=null;pendingAction=-1;return;
        }
        int a=pendingAction;
        float[] nextHidden=advance(current,hidden,a),after=join(current,nextHidden);
        float earned=clip(current[0]-pending[0]-(terminal?.20f:0)
            +Math.min(.018f,verifiedExternalMotion*.0025f),-1,1);
        float harm=clip(actualDamage/100f,0,1);
        Experience e=new Experience(pending,after,a,earned,harm,terminal);
        // Train new connections only on executed non-heldout actions;
        // holdouts only measure whether a candidate beats the old predictor.
        neuralGrowth.observe(e.before,a,e.earned,read(reward,e.before,a),
            train&&!holdout,holdout);
        if(holdout){
            float error=0,base=0;
            for(int j=0;j<F;j++){
                if(j>=138&&j<144)continue;
                float delta=after[j]-pending[j],res=delta-forecastDelta(pending,a,j);
                error+=res*res;base+=delta*delta;
            }
            validation[a]++;
            float alpha=validation[a]==1?1:.08f;
            modelError[a]+=(error/(F-6)-modelError[a])*alpha;
            baselineError[a]+=(base/(F-6)-baselineError[a])*alpha;
            float deltaEnergy=after[0]-pending[0];
            float ee=deltaEnergy-forecastDelta(pending,a,0);
            energyError[a]+=(ee*ee-energyError[a])*alpha;
            energyBase[a]+=(deltaEnergy*deltaEnergy-energyBase[a])*alpha;
        }else if(train){
            train(e,true);if(samples[a]<Integer.MAX_VALUE)samples[a]++;
            if(recent.size()<RECENT)recent.add(e);else recent.set(recentCursor++%RECENT,e);
            if(Math.abs(actualContactEnergy)>.5f||terminal){
                if(rare.size()<RARE)rare.add(e);else rare.set(rareCursor++%RARE,e);
            }
            for(int k=0;k<2;k++){
                ArrayList<Experience> pool=k==1&&!rare.isEmpty()?rare:recent;
                if(!pool.isEmpty()){
                    // Replay of rare energy events teaches value/risk, not the
                    // typical frequency of physical world-state transitions.
                    train(pool.get(replayRandom.nextInt(pool.size())),k==0);replays++;
                }
            }
        }
        // Count ACTUAL positive energy events, including heldout transitions;
        // this is a runtime-only discovery/stagnation measure, not a reward.
        if(actualContactEnergy>.01f)intervalsWithoutEnergy=0;
        else intervalsWithoutEnergy=Math.min(1000000,intervalsWithoutEnergy+1);
        System.arraycopy(nextHidden,0,hidden,0,H);
        transitions++;if(terminal)terminals++;
        pending=null;pendingAction=-1;
    }
    public void resetLife(){Arrays.fill(hidden,0);Arrays.fill(current,0);pending=null;pendingAction=-1;lastAction=-1;intervalsWithoutEnergy=0;neuralGrowth.resetEpisode();}
    public void resumeLife(){pending=null;pendingAction=-1;}
    /** Read-only lifetime learning counters. Never changes the policy. */
    public int initialNeurons(){return neuralGrowth.initialNeurons();}
    public int addedNeurons(){return neuralGrowth.addedNeurons();}
    public int totalNeurons(){return neuralGrowth.totalNeurons();}
    public int maxNeurons(){return neuralGrowth.maxNeurons();}
    public int growthAttempts(){return neuralGrowth.growthAttempts();}
    public int rejectedGrowths(){return neuralGrowth.rejectedAttempts();}
    public boolean growingCandidate(){return neuralGrowth.candidateInTraining();}
    public float lastGrowthGain(){return neuralGrowth.lastAcceptedImprovement();}
    public float neuronConnectionStrength(int newIndex){
        return neuralGrowth.neuronStrength(newIndex);
    }
    public long trainedTransitions(){return trainSteps;}
    public long decisionCount(){return choices;}
    public long terminalEvents(){return terminals;}
    public int heldoutTransitions(){int n=0;for(int v:validation)n+=v;return n;}
    public long transitions(){return transitions;}
    public long replayUpdates(){return replays;}
    /** Read-only trainable weights for the local parameter inspector.
     * Order: dynamics, action-value, energy expectation, hazard expectation.
     * No reference to mutable training storage is exposed.
     */
    public int parameterCount(){return dynamics.length+q.length+reward.length+risk.length+neuralGrowth.addedParameters();}
    public float parameterAt(int index){
        if(index<0||index>=parameterCount())throw new IndexOutOfBoundsException();
        if(index<dynamics.length)return dynamics[index];
        index-=dynamics.length;
        if(index<q.length)return q[index];
        index-=q.length;
        if(index<reward.length)return reward[index];
        index-=reward.length;
        if(index<risk.length)return risk[index];
        return neuralGrowth.parameterAt(index-risk.length);
    }
    public static int dynamicsParameters(){return A*F*HEAD;}
    public static int headParameters(){return A*(Z+1);}
    public int replaySize(){return recent.size()+rare.size();}
    public long plannedChoices(){return modelChoices;}
    public int physicalReady(){int n=0;for(int a=0;a<A;a++)if(confidence(a)>0)n++;return n;}
    public int energyReady(){int n=0;for(int a=0;a<A;a++)if(energyConfidence(a)>0)n++;return n;}
    public String summary(){
        float livePhysicalTrust=0;
        for(int a=0;a<A;a++)livePhysicalTrust=Math.max(livePhysicalTrust,confidence(a));
        return String.format(Locale.US,
            "ZERO 2.2 PRIMARY: lived=%d replay=%d/%d modelProbes=%d primaryDecisionsSession=%d intervalsNoEnergy=%d physicalReady=%d/%d physicalConfidence=%.3f energyReady=%d/%d margin=%.4f terminal=%d invalid=%d",
            transitions,replaySize(),replays,modelChoices,primaryDecisionsSession,intervalsWithoutEnergy,
            physicalReady(),A,livePhysicalTrust,energyReady(),A,lastMargin,terminals,invalid)
            +String.format(Locale.US," neurons=%d (%d original+%d grown) attempts=%d rejected=%d heldoutGain=%.3f",
                totalNeurons(),initialNeurons(),addedNeurons(),growthAttempts(),rejectedGrowths(),lastGrowthGain());
    }
    private static void array(DataOutputStream out,float[] a)throws IOException{for(float v:a)out.writeFloat(v);}
    private static void readArray(DataInputStream in,float[] a,float bound)throws IOException{
        for(int i=0;i<a.length;i++){float v=in.readFloat();if(!Float.isFinite(v)||Math.abs(v)>bound)throw new IOException("weight");a[i]=v;}
    }
    private static void bank(DataOutputStream out,ArrayList<Experience> list)throws IOException{
        out.writeInt(list.size());for(Experience e:list){out.writeInt(e.action);out.writeFloat(e.earned);out.writeFloat(e.harm);out.writeBoolean(e.terminal);array(out,e.before);array(out,e.after);}
    }
    private static void bank(DataInputStream in,ArrayList<Experience> list,int max)throws IOException{
        int n=in.readInt();if(n<0||n>max)throw new IOException("replay size");
        for(int i=0;i<n;i++){
            int a=in.readInt();float r=in.readFloat(),d=in.readFloat();boolean t=in.readBoolean();
            if(a<0||a>=A||!Float.isFinite(r)||Math.abs(r)>1||!Float.isFinite(d)||d<0||d>1)throw new IOException("sample");
            float[] b=new float[Z],e=new float[Z];readArray(in,b,1.601f);readArray(in,e,1.601f);list.add(new Experience(b,e,a,r,d,t));
        }
    }
    /** Compact compressed, checksummed payload, comfortably under slot limit.
     * Decode validates into a temporary object before touching live weights.
     */
    public String encode(){try{
        ByteArrayOutputStream raw=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(raw);
        out.writeInt(0x5a433232);for(long v:new long[]{transitions,trainSteps,replays,choices,modelChoices,invalid,terminals})out.writeLong(v);
        for(int[] list:new int[][]{samples,validation})for(int n:list)out.writeInt(n);
        for(float[] list:new float[][]{dynamics,q,reward,risk,modelError,baselineError,energyError,energyBase,hidden,current})array(out,list);
        bank(out,recent);bank(out,rare);
        neuralGrowth.write(out);out.flush();byte[] body=raw.toByteArray();CRC32 crc=new CRC32();crc.update(body);
        ByteArrayOutputStream compressed=new ByteArrayOutputStream();DeflaterOutputStream zip=new DeflaterOutputStream(compressed);zip.write(body);zip.close();
        return "rc22|"+crc.getValue()+"|"+Base64CodecV22.encode(compressed.toByteArray());
    }catch(IOException ex){throw new IllegalStateException(ex);}}
    public boolean restore(String text){
        if(text==null||text.length()>2000000||!text.startsWith("rc22|"))return false;
        try{
            String[] p=text.split("\\|",-1);if(p.length!=3)return false;
            InflaterInputStream zip=new InflaterInputStream(new ByteArrayInputStream(Base64CodecV22.decode(p[2])));
            ByteArrayOutputStream raw=new ByteArrayOutputStream();byte[] buffer=new byte[4096];int n;
            while((n=zip.read(buffer))!=-1){if(raw.size()+n>600000)return false;raw.write(buffer,0,n);}zip.close();
            byte[] bytes=raw.toByteArray();CRC32 crc=new CRC32();crc.update(bytes);if(crc.getValue()!=Long.parseLong(p[1]))return false;
            DataInputStream in=new DataInputStream(new ByteArrayInputStream(bytes));if(in.readInt()!=0x5a433232)return false;
            RecurrentCoreV22 c=new RecurrentCoreV22();long[] counters=new long[7];for(int i=0;i<7;i++){counters[i]=in.readLong();if(counters[i]<0||counters[i]>1000000000000L)return false;}
            if(counters[4]>counters[3]||counters[6]>counters[0])return false;
            for(int[] list:new int[][]{c.samples,c.validation})for(int i=0;i<A;i++){list[i]=in.readInt();if(list[i]<0||list[i]>counters[0])return false;}
            for(float[] list:new float[][]{c.dynamics,c.q,c.reward,c.risk})readArray(in,list,3.001f);
            for(float[] list:new float[][]{c.modelError,c.baselineError,c.energyError,c.energyBase}){readArray(in,list,10);for(float v:list)if(v<0)return false;}
            readArray(in,c.hidden,1.001f);readArray(in,c.current,1.601f);
            bank(in,c.recent,RECENT);bank(in,c.rare,RARE);
            // The unmodified OTA158 checkpoint legitimately lacks this trailer.
            if(in.available()>0){
                if(in.readInt()!=NeurogenesisV22.MAGIC)return false;
                c.neuralGrowth.read(in);
            }
            if(in.available()!=0)return false;
            for(float[][] pair:new float[][][]{ {c.dynamics,dynamics},{c.q,q},{c.reward,reward},{c.risk,risk}, {c.modelError,modelError},{c.baselineError,baselineError},{c.energyError,energyError},{c.energyBase,energyBase},{c.hidden,hidden},{c.current,current}})System.arraycopy(pair[0],0,pair[1],0,pair[0].length);
            System.arraycopy(c.samples,0,samples,0,A);System.arraycopy(c.validation,0,validation,0,A);
            transitions=counters[0];trainSteps=counters[1];replays=counters[2];choices=counters[3];modelChoices=counters[4];invalid=counters[5];terminals=counters[6];
            recent.clear();recent.addAll(c.recent);rare.clear();rare.addAll(c.rare);
            neuralGrowth.copyFrom(c.neuralGrowth);
            recentCursor=rareCursor=0;resumeLife();return true;
        }catch(IOException|RuntimeException ex){return false;}
    }
}
