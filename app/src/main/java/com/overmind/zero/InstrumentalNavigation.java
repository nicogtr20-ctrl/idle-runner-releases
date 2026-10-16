package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * ZERO 0.11.2: motion is a learnable MEANS to change access to an
 * outcome, not itself an energetic reward.
 *
 * Two independently lived relations:
 * (1) observed spectral signal -> actual energy delta after touching it;
 * (2) self-selected motor action -> ACTUAL nucleus displacement.
 *
 * A candidate motor command is valued only for its PREDICTED ability to
 * change approach to previously beneficial/harmful observed wavelengths.
 * The geometric dot product uses local physical rays, never a world
 * coordinate of any object, name of a colour, hardcoded 'go to food'
 * command, or a scripted gate opening.
 *
 * No contact => no learned valence => no goal-directed steering.
 * No observed reliable motion => no instrumental steering.
 * The original 26-sense policy, memories and motor controllers persist.
 */
public final class InstrumentalNavigation {
    public static final String KEY="instrumental_navigation_v112";
    public static final int SIGNALS=UnknownWorld.SIGNATURES,SECTORS=8,
                            ACTIONS=SensorimotorMind.ACTIONS;
    private final float[] valence=new float[SIGNALS];
    private final int[] effects=new int[SIGNALS];
    private final float[] motorDx=new float[ACTIONS],motorDy=new float[ACTIONS];
    private final int[] trials=new int[ACTIONS],holdouts=new int[ACTIONS];
    private final float[] testError=new float[ACTIONS],zeroError=new float[ACTIONS];
    private final float[] visual=new float[SIGNALS*SECTORS];
    private float startX,startY;
    private int pending=-1;
    private final ContextualNavigationV171 contextual=new ContextualNavigationV171();
    private boolean embodiedContext=false;
    public void motorContext(MotorBrain.Body b,Morphology.Surface s,float[] weights){
        embodiedContext=true;contextual.context(b,s,weights);
    }
    public String encodeContextual(){return contextual.encode();}
    public boolean restoreContextual(String raw){return contextual.restore(raw);}
    public long completed=0;
    public int positiveOutcomes=0,negativeOutcomes=0,choicesInfluenced=0,
               approachedPositive=0,avoidedNegative=0,uncertain=0;
    public float latestMovement=0,latestUtility=0;

    private static float clamp(float f,float lo,float hi){
        return Math.max(lo,Math.min(hi,f));
    }
    public float[] visible(){return visual.clone();}
    public int[] spectralContacts(){return effects.clone();}
    public int[] actionTrials(){return trials.clone();}
    /** Read-only diagnostic baseline. The old policy uses the same means. */
    public float motionEstimateX(int action){
        return action<0||action>=ACTIONS?0f:embodiedContext?contextual.x(action):motorDx[action];
    }
    public float motionEstimateY(int action){
        return action<0||action>=ACTIONS?0f:embodiedContext?contextual.y(action):motorDy[action];
    }
    public boolean motionEstimateVerified(int action){
        if(embodiedContext)return contextual.verified(action);
        return action>=0&&action<ACTIONS&&trials[action]>=3&&
            holdouts[action]>=2&&zeroError[action]>=2f&&
            testError[action]<zeroError[action]*.99f;
    }
    public float signalValue(int s){return s<0||s>=SIGNALS?0:valence[s];}
    public int knownEffects(){int n=0;for(int v:effects)n+=v;return n;}
    public int learnedActions(){
        int n=0;for(int a=0;a<ACTIONS;a++)if(trials[a]>=3)n++;return n;
    }
    public int verifiedActions(){
        if(embodiedContext)return contextual.verified();
        int n=0;for(int a=0;a<ACTIONS;a++)if(motionEstimateVerified(a))n++;return n;
    }
    public void sense(MotorBrain.Body b,ScarcityLab room,
                      java.util.ArrayList<UnknownWorld.Entity> objects){
        sense(b,null,room,objects);
    }
    /** The navigational retina shares the active 26D physical eye origins. */
    public void sense(MotorBrain.Body b,Morphology.Surface skin,ScarcityLab room,
                      java.util.ArrayList<UnknownWorld.Entity> objects){
        if(b==null||objects==null)return;
        float[] sample=SensorimotorMind.Senses.spectral8(b,skin,room,objects);
        if(sample.length!=visual.length)return;
        System.arraycopy(sample,0,visual,0,visual.length);
        if(pending<0){startX=b.cx;startY=b.cy;}
    }
    public void resetLife(){
        pending=-1;contextual.cancel();Arrays.fill(visual,0);
        latestMovement=latestUtility=0;
    }
    public void resumeLife(){pending=-1;contextual.cancel();}
    public void chosen(int action,MotorBrain.Body b){
        if(action<0||action>=ACTIONS||b==null)return;
        pending=action;startX=b.cx;startY=b.cy;
        if(embodiedContext)contextual.chosen(action);
    }
    /** Ground every sign in the measured change of ENERGY from contact.
     * A physical moved disk has zero valence unless it actually changes
     * energy. Never infer a resource from its colour or physical law. */
    public void energyOutcome(float delta,int observedSignature,boolean learning){
        if(!learning||!Float.isFinite(delta)||Math.abs(delta)<.01f)return;
        if(observedSignature<0||observedSignature>=SIGNALS)return;
        int s=observedSignature;
        float actual=clamp(delta/(delta>0?22f:7f),-1.8f,1.8f);
        float alpha=effects[s]==0?1f:.24f;
        valence[s]=clamp(valence[s]+alpha*(actual-valence[s]),-1.8f,1.8f);
        effects[s]++;
        if(delta>0)positiveOutcomes++;else negativeOutcomes++;
    }
    /** Lived action outcomes, not the intended action vector. A command
     * which fails to move the body will be learned as nearly ineffective.
     * Held-out samples evaluate honest prediction vs zero displacement. */
    public void consequence(int action,MotorBrain.Body b,boolean learning,
                            boolean heldout){consequence(action,b,learning,heldout,true);}
    public void consequence(int action,MotorBrain.Body b,boolean learning,
        boolean heldout,boolean clean){
        if(pending!=action||b==null||!Float.isFinite(b.cx)||!Float.isFinite(b.cy))return;
        float dx=clamp(b.cx-startX,-120,120),dy=clamp(b.cy-startY,-120,120);
        latestMovement=(float)Math.hypot(dx,dy);
        if(embodiedContext)contextual.consequence(action,dx,dy,learning,heldout,clean);
        if(heldout){
            holdouts[action]++;
            float err=(float)Math.hypot(dx-motorDx[action],dy-motorDy[action]);
            float zero=latestMovement;
            float alpha=holdouts[action]==1?1f:.12f;
            testError[action]+=alpha*(err-testError[action]);
            zeroError[action]+=alpha*(zero-zeroError[action]);
        }else if(learning){
            float alpha=trials[action]==0?1f:.15f;
            motorDx[action]+=alpha*(dx-motorDx[action]);
            motorDy[action]+=alpha*(dy-motorDy[action]);
            trials[action]++;
        }
        completed++;
        pending=-1;
    }
    /** Value comes from EXPECTED access to a valuable visible signal;
     * not a payoff on travel length, spinning, raw changed pixels or
     * the act of selecting a heading. No learned values -> exact zero. */
    public float bias(int action,float energyFraction){
        if(!motionEstimateVerified(action))return 0;
        float dx=motionEstimateX(action),dy=motionEstimateY(action);
        if(!Float.isFinite(dx)||!Float.isFinite(dy))return 0;
        float score=0,weight=0;
        float need=.35f+1.25f*clamp(1-energyFraction,0,1);
        for(int signal=0;signal<SIGNALS;signal++){
            if(effects[signal]==0||Math.abs(valence[signal])<.12f)continue;
            float sign=valence[signal];
            for(int sector=0;sector<SECTORS;sector++){
                float seen=visual[signal*SECTORS+sector];
                if(seen<.08f)continue;
                double angle=sector*2*Math.PI/SECTORS;
                float projection=(float)(dx*Math.cos(angle)+dy*Math.sin(angle));
                float tendency=clamp(projection/42f,-1,1);
                // Attraction is stronger when energy is low; aversion never
                // vanishes with high energy, because injury is still costly.
                float value=sign>0?sign*need:sign*1.35f;
                float visibility=seen*seen;
                score+=visibility*value*tendency;
                weight+=visibility;
            }
        }
        if(weight<.015f)return 0;
        return clamp(1.85f*score/(.34f+weight),-1.6f,1.6f);
    }
    public void noteSelection(float maxAbs){
        if(maxAbs>.03f)choicesInfluenced++;
    }
    public int parameterCount(){return SIGNALS+ACTIONS*2;}
    public String encode(){
        StringBuilder s=new StringBuilder("in1|").append(completed).append('|')
          .append(positiveOutcomes).append('|').append(negativeOutcomes).append('|')
          .append(choicesInfluenced).append('|').append(approachedPositive).append('|')
          .append(avoidedNegative).append('|').append(uncertain);
        for(float v:valence)s.append('|').append(v);
        for(int v:effects)s.append('|').append(v);
        for(float v:motorDx)s.append('|').append(v);
        for(float v:motorDy)s.append('|').append(v);
        for(int v:trials)s.append('|').append(v);
        for(int v:holdouts)s.append('|').append(v);
        for(float v:testError)s.append('|').append(v);
        for(float v:zeroError)s.append('|').append(v);
        return s.toString();
    }
    public boolean restore(String raw){
        if(raw==null||!raw.startsWith("in1|")||raw.length()>20000)return false;
        try{
            String[] a=raw.split("\\|",-1);
            if(a.length!=8+SIGNALS*2+ACTIONS*6)return false;
            long count=Long.parseLong(a[1]);
            int[] counters=new int[6];
            for(int i=0;i<6;i++)counters[i]=Integer.parseInt(a[i+2]);
            if(count<0)return false;
            for(int n:counters)if(n<0)return false;
            float[] va=new float[SIGNALS],mx=new float[ACTIONS],my=new float[ACTIONS],
                err=new float[ACTIONS],zero=new float[ACTIONS];
            int[] effectsNew=new int[SIGNALS],tr=new int[ACTIONS],ho=new int[ACTIONS];
            int p=8;
            for(int j=0;j<SIGNALS;j++)va[j]=Float.parseFloat(a[p++]);
            for(int j=0;j<SIGNALS;j++)effectsNew[j]=Integer.parseInt(a[p++]);
            for(int j=0;j<ACTIONS;j++)mx[j]=Float.parseFloat(a[p++]);
            for(int j=0;j<ACTIONS;j++)my[j]=Float.parseFloat(a[p++]);
            for(int j=0;j<ACTIONS;j++)tr[j]=Integer.parseInt(a[p++]);
            for(int j=0;j<ACTIONS;j++)ho[j]=Integer.parseInt(a[p++]);
            for(int j=0;j<ACTIONS;j++)err[j]=Float.parseFloat(a[p++]);
            for(int j=0;j<ACTIONS;j++)zero[j]=Float.parseFloat(a[p++]);
            for(int j=0;j<SIGNALS;j++)
                if(!Float.isFinite(va[j])||Math.abs(va[j])>1.801f||effectsNew[j]<0)return false;
            for(int j=0;j<ACTIONS;j++)
                if(!Float.isFinite(mx[j])||!Float.isFinite(my[j])||
                   Math.abs(mx[j])>120||Math.abs(my[j])>120||tr[j]<0||ho[j]<0||
                   !Float.isFinite(err[j])||err[j]<0||err[j]>250||
                   !Float.isFinite(zero[j])||zero[j]<0||zero[j]>180)return false;
            System.arraycopy(va,0,valence,0,SIGNALS);
            System.arraycopy(effectsNew,0,effects,0,SIGNALS);
            System.arraycopy(mx,0,motorDx,0,ACTIONS);
            System.arraycopy(my,0,motorDy,0,ACTIONS);
            System.arraycopy(tr,0,trials,0,ACTIONS);
            System.arraycopy(ho,0,holdouts,0,ACTIONS);
            System.arraycopy(err,0,testError,0,ACTIONS);
            System.arraycopy(zero,0,zeroError,0,ACTIONS);
            completed=count;positiveOutcomes=counters[0];negativeOutcomes=counters[1];
            choicesInfluenced=counters[2];approachedPositive=counters[3];
            avoidedNegative=counters[4];uncertain=counters[5];
            resetLife();return true;
        }catch(RuntimeException bad){return false;}
    }
    private static void require(boolean ok,String message){
        if(!ok)throw new AssertionError(message);
    }
    public static void main(String[] args){
        InstrumentalNavigation n=new InstrumentalNavigation();
        MotorBrain.Body b=new MotorBrain.Body();
        java.util.ArrayList<UnknownWorld.Entity> world=new java.util.ArrayList<>();
        world.add(new UnknownWorld.Entity(b.cx+115,b.cy,0,0));
        world.add(new UnknownWorld.Entity(b.cx,b.cy-135,1,1));
        n.sense(b,null,world);
        require(n.visible()[0]>0&&n.visible()[1*SECTORS+6]>0,
            "Physical world not visible in E/N sectors");
        require(n.bias(0,.4f)==0,"Virgin motion/energy relation hardcoded");
        n.energyOutcome(22,0,true);n.energyOutcome(-7,1,true);
        require(n.signalValue(0)>0&&n.signalValue(1)<0&&n.signalValue(2)==0,
            "Valence does not come exclusively from observed energy");
        require(n.bias(0,.4f)==0,"Knowing energy alone should not magically teach motion");
        float x=b.cx,y=b.cy;
        for(int action=0;action<8;action++){
            float angle=(float)(action*Math.PI/4);
            for(int j=0;j<64;j++){
                b.cx=x;b.cy=y;
                n.sense(b,null,world);n.chosen(action,b);
                b.cx+=Math.cos(angle)*30;b.cy+=Math.sin(angle)*30;
                n.consequence(action,b,j%4!=0,j%4==0);
            }
        }
        b.cx=x;b.cy=y;n.sense(b,null,world);
        require(n.verifiedActions()>=8,"Observed actuator models did not beat zero displacement");
        require(n.bias(0,.2f)>n.bias(4,.2f),
            "Positive observed energy did not favor learned approaching movement");
        require(n.bias(6,.2f)<n.bias(2,.2f),
            "Negative energy did not favor learned withdrawal");
        require(n.bias(8,.2f)==0,
            "Untested grip action illegally inherited a movement map");
        // Integration regression: ZERO's LIVE selector must actually use
        // the learned spectral/motor relation, not just display diagnostic
        // scores in ANALISI. No target object/coordinates are passed to it.
        SensorimotorMind controller=new SensorimotorMind();
        controller.attachNavigation(n);
        controller.experiences=20000; // below random exploration floor remains 9%.
        float[] physiology=new float[SensorimotorMind.S];
        physiology[0]=.18f;
        UnknownWorld.Entity red=world.get(1);
        world.remove(red);
        b.cx=x;b.cy=y;
        controller.updateNavigationSense(b,null,world);
        int east=0,west=0;
        for(int i=0;i<160;i++){
            int a=controller.choose(physiology)%8;
            if(a==0)east++;
            if(a==4)west++;
        }
        require(east>west*3+10,
            "Actual autonomous action selector still ignores learned access to energy");
        world.clear();world.add(red);
        controller.updateNavigationSense(b,null,world);
        int north=0,south=0;
        for(int i=0;i<160;i++){
            int a=controller.choose(physiology)%8;
            if(a==6)north++;
            if(a==2)south++;
        }
        require(south>north*3+10,
            "Actual autonomous action selector still ignores learned energy injury");
        world.clear();
        world.add(new UnknownWorld.Entity(b.cx+115,b.cy,0,0));
        world.add(red);
        n.sense(b,null,world);
        String saved=n.encode();
        InstrumentalNavigation restored=new InstrumentalNavigation();
        require(restored.restore(saved)&&saved.equals(restored.encode()),
            "Instrumental weights or energetics lost after restart");
        // Transient light is deliberately NOT persisted. On load the same
        // physical world is sensed again before any steering is available.
        restored.sense(b,null,world);
        require(restored.bias(0,.2f)>restored.bias(4,.2f),
            "Learned instrumental motion unavailable after new real sensation");
        restored.resetLife();
        require(restored.knownEffects()==2&&restored.verifiedActions()>=8,
            "Death erased learned instrumentality");
        require(!new InstrumentalNavigation().restore(saved.replaceFirst("in1","in0")),
            "Corrupt instrumental state accepted");
        System.out.println(String.format(Locale.US,
            "ZERO-0112 PASS: physical 8-sector wavelengths, lived positive/negative energy, learned action motion, heldout validity, approach/avoid and saved independent brains; verified actions=%d",n.verifiedActions()));
    }
}
