package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO's bounded autobiographical experience: sensory states, actually chosen
 * actions, observed physical consequences and temporary body configurations.
 *
 * NEVER supplies a target coordinate, gate category or predefined solution.
 * Only outcomes measured AFTER acting are eligible for recording. At a later
 * encounter the memory contributes a SOFT, context-gated action preference
 * and (briefly) the actual physical weights present during that experience.
 * A replay is an experiment, not an unconditional script or a guarantee.
 */
public final class EpisodicMemory {
    public static final int DEPTH=9,MAX_EPISODES=18;
    private static final float MIN_EFFECT=3f, MATCH=.115f;
    private static final float[] FEATURE_WEIGHT=new float[SensorimotorMind.S];
    static {
        for(int i=0;i<FEATURE_WEIGHT.length;i++)
            FEATURE_WEIGHT[i]=i<3?.85f:i<6?1.35f:i<14?1.6f:1.35f;
    }
    public static final class Step {
        final float[] senses;
        final int action;
        Step(float[] s,int a){senses=s.clone();action=a;}
    }
    public static final class Event {
        final ArrayList<Step> trace=new ArrayList<>();
        float effect=0;
        float[] motor,hand,membrane;
    }
    private final ArrayList<Step> recent=new ArrayList<>();
    private final ArrayList<Event> events=new ArrayList<>();
    private Event selected=null;
    private int selectedIndex=-1,continuation=0,cooldown=0;
    private float[] replayMotor,replayHand,replayMembrane;
    private boolean replayActive=false;
    public int captured=0,matched=0,replayed=0,repeatEffects=0;
    public int lastTraceLength=0;
    public float lastEventEffect=0,lastSimilarity=0;
    private boolean replayAtAction=false;

    public int stored(){return events.size();}
    public boolean physicallyReplaying(){return replayActive;}
    public void resetLife(){
        recent.clear(); selected=null;selectedIndex=-1;
        replayActive=false;replayAtAction=false;continuation=0;cooldown=0;
        replayMotor=replayHand=replayMembrane=null;
    }
    /** Record just the real previous action when that action has concluded. */
    public void action(float[] before,int action){
        if(before==null||before.length!=SensorimotorMind.S||
           action<0||action>=SensorimotorMind.ACTIONS)return;
        for(float x:before)if(!Float.isFinite(x))return;
        if(recent.size()>=DEPTH)recent.remove(0);
        recent.add(new Step(before,action));
    }
    /**
     * Called after a completed real action, when physical displacement
     * or actual energy acquisition happened. No fabricated success labels.
     * Weights are a frozen copy of the *actual* controller used during
     * the interesting event, including a tentative mutant if active.
     */
    public boolean consequence(float physicalEffect,float energyGained,
                               float[] motor,float[] hand,float[] membrane){
        float effect=Math.max(0,Math.min(100,physicalEffect))+
                     Math.max(0,Math.min(80,energyGained))*.25f;
        if(!Float.isFinite(effect)||effect<MIN_EFFECT||recent.size()<2)return false;
        if(motor==null||hand==null||membrane==null||
           motor.length!=MotorBrain.PARAMS||
           hand.length!=ContactLearning.PARAMS||
           membrane.length!=Morphology.PARAMS)return false;
        for(float[] arr:new float[][]{motor,hand,membrane})
            for(float v:arr)if(!Float.isFinite(v)||Math.abs(v)>5)return false;
        Event e=new Event();e.effect=effect;
        for(Step s:recent)e.trace.add(new Step(s.senses,s.action));
        e.motor=motor.clone();e.hand=hand.clone();e.membrane=membrane.clone();
        // Retain bounded autobiographical diversity, not every duplicated
        // physics frame. A duplicate event must be meaningfully stronger.
        for(int i=0;i<events.size();i++){
            Event old=events.get(i);
            Step x=old.trace.get(0),y=e.trace.get(0);
            if(x.action==y.action&&distance(x.senses,y.senses)<.045f){
                if(e.effect<=old.effect*1.10f)return false;
                events.remove(i);break;
            }
        }
        if(events.size()>=MAX_EPISODES)events.remove(0);
        events.add(e);captured++;lastTraceLength=e.trace.size();
        lastEventEffect=e.effect;
        if(replayAtAction)repeatEffects++;
        return true;
    }
    private static float distance(float[] a,float[] b){
        float error=0,total=0;
        for(int i=0;i<a.length;i++){
            float w=FEATURE_WEIGHT[i];
            error+=w*Math.min(2,Math.abs(a[i]-b[i]));
            total+=w;
        }
        return error/total;
    }
    private static boolean salient(float[] s){
        // No repeated open-space action loop just because most channels=0.
        float obstacle=0;
        for(int i=6;i<14;i++)obstacle=Math.max(obstacle,s[i]);
        float colour=0;for(int i=14;i<s.length;i++)colour=Math.max(colour,s[i]);
        return obstacle>.22f||colour>.35f||s[4]>.012f||s[5]>.012f;
    }
    /** Suggest, but NEVER force, a remembered action in matching senses. */
    public int suggested(float[] senses){
        replayAtAction=false;replayActive=false;
        replayMotor=replayHand=replayMembrane=null;
        if(senses==null||senses.length!=SensorimotorMind.S||!salient(senses))return -1;
        if(cooldown>0){cooldown--;return -1;}
        Event best=null;int bestIndex=-1;float dist=MATCH;
        if(selected!=null&&continuation<selected.trace.size()){
            Step s=selected.trace.get(continuation);
            float d=distance(senses,s.senses);
            if(d<MATCH*1.5f){best=selected;bestIndex=continuation;dist=d;}
        }
        if(best==null){
            for(Event e:events){
                for(int i=0;i<e.trace.size();i++){
                    float d=distance(senses,e.trace.get(i).senses);
                    // Prefer starting earlier in a remembered sequence.
                    float adjusted=d+.004f*i;
                    if(adjusted<dist){
                        best=e;bestIndex=i;dist=adjusted;
                    }
                }
            }
        }
        if(best==null){selected=null;continuation=0;return -1;}
        if(best!=selected||bestIndex!=continuation)matched++;
        selected=best;selectedIndex=bestIndex;
        lastSimilarity=1-Math.min(1,dist);
        return best.trace.get(bestIndex).action;
    }
    /** The actual agent may choose a DIFFERENT action from the suggestion. */
    public void chosen(int actual,int suggestion){
        replayAtAction=false;replayActive=false;
        if(suggestion<0||selected==null||actual!=suggestion){
            selected=null;continuation=0;return;
        }
        replayed++;replayAtAction=true;
        replayActive=true;
        replayMotor=selected.motor;
        replayHand=selected.hand;
        replayMembrane=selected.membrane;
        continuation=selectedIndex+1;
        if(continuation>=selected.trace.size()){
            selected=null;continuation=0;cooldown=3;
        }
    }
    /** Reenact a real physical configuration only on a matching, chosen action. */
    public float[] weights(int type,float[] normal){
        if(!replayActive)return normal;
        return type==0?replayMotor:type==1?replayHand:replayMembrane;
    }
    /** Offline persistence does NOT contain the transient last few frames. */
    public String encode(){
        StringBuilder b=new StringBuilder("em1;").append(captured).append(';')
          .append(matched).append(';').append(replayed).append(';')
          .append(repeatEffects).append(';').append(events.size());
        for(Event e:events){
            b.append(';').append(e.effect).append(',').append(e.trace.size());
            for(Step s:e.trace){
                b.append(',').append(s.action);
                for(float v:s.senses)b.append(',').append(v);
            }
            for(float[] arr:new float[][]{e.motor,e.hand,e.membrane})
                for(float v:arr)b.append(',').append(v);
        }
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.isEmpty())return false;
        if(raw.length()>220000||!raw.startsWith("em1;"))return false;
        try{
            String[] groups=raw.split(";",-1);
            if(groups.length<6||groups.length>6+MAX_EPISODES)return false;
            int ct=Integer.parseInt(groups[1]),mt=Integer.parseInt(groups[2]),
                rp=Integer.parseInt(groups[3]),success=Integer.parseInt(groups[4]),
                n=Integer.parseInt(groups[5]);
            if(ct<0||mt<0||rp<0||success<0||n<0||n>MAX_EPISODES||
               groups.length!=6+n)return false;
            ArrayList<Event> loaded=new ArrayList<>();
            for(int g=0;g<n;g++){
                String[] v=groups[g+6].split(",",-1);int ix=0;
                float eff=Float.parseFloat(v[ix++]);
                int count=Integer.parseInt(v[ix++]);
                if(count<2||count>DEPTH||!Float.isFinite(eff)||eff<MIN_EFFECT||
                   v.length!=2+count*(1+SensorimotorMind.S)+
                        MotorBrain.PARAMS+ContactLearning.PARAMS+Morphology.PARAMS)
                    return false;
                Event e=new Event();e.effect=eff;
                for(int j=0;j<count;j++){
                    int act=Integer.parseInt(v[ix++]);
                    if(act<0||act>=SensorimotorMind.ACTIONS)return false;
                    float[] s=new float[SensorimotorMind.S];
                    for(int k=0;k<s.length;k++){
                        s[k]=Float.parseFloat(v[ix++]);
                        if(!Float.isFinite(s[k])||Math.abs(s[k])>3)return false;
                    }
                    e.trace.add(new Step(s,act));
                }
                e.motor=new float[MotorBrain.PARAMS];
                e.hand=new float[ContactLearning.PARAMS];
                e.membrane=new float[Morphology.PARAMS];
                for(float[] arr:new float[][]{e.motor,e.hand,e.membrane}){
                    for(int k=0;k<arr.length;k++){
                        arr[k]=Float.parseFloat(v[ix++]);
                        if(!Float.isFinite(arr[k])||Math.abs(arr[k])>5)return false;
                    }
                }
                loaded.add(e);
            }
            events.clear();events.addAll(loaded);
            captured=ct;matched=mt;replayed=rp;repeatEffects=success;
            if(!events.isEmpty()){
                Event e=events.get(events.size()-1);
                lastEventEffect=e.effect;lastTraceLength=e.trace.size();
            }
            resetLife();
            return true;
        }catch(RuntimeException e){return false;}
    }
    public static void main(String[] args){
        EpisodicMemory m=new EpisodicMemory();
        float[] s=new float[SensorimotorMind.S];s[6]=.72f;s[4]=.15f;
        float[] mot=MotorBrain.virgin(new java.util.Random(7));
        float[] hand=ContactLearning.virgin();
        float[] shape=new float[Morphology.PARAMS];
        for(int i=0;i<6;i++){float[] z=s.clone();z[1]=i*.025f;
            m.action(z,12);}
        if(m.consequence(1,0,mot,hand,shape)||m.stored()!=0)
            throw new AssertionError("No strong consequence");
        if(!m.consequence(9,0,mot,hand,shape)||m.stored()!=1||
           m.lastTraceLength!=6)throw new AssertionError("Event not stored");
        String saved=m.encode();EpisodicMemory n=new EpisodicMemory();
        if(!n.restore(saved)||n.stored()!=1)throw new AssertionError("Persistence");
        n.resetLife();
        int proposed=n.suggested(s);
        if(proposed!=12)throw new AssertionError("Recognized senses did not recall action");
        n.chosen(12,proposed);
        if(!n.physicallyReplaying()||n.weights(0,mot)==mot||n.replayed!=1)
            throw new AssertionError("Contextual body replay not selected");
        n.action(s,12);n.consequence(10,0,mot,hand,shape);
        if(n.repeatEffects!=1)throw new AssertionError("No measured replay success");
        n.resetLife();
        if(n.stored()!=1||n.replayed!=1)throw new AssertionError("Death erased recall");
        float[] blank=new float[SensorimotorMind.S];
        if(n.suggested(blank)!=-1||n.physicallyReplaying())
            throw new AssertionError("Blank space induced looping");
        EpisodicMemory z=new EpisodicMemory();
        String broken=saved.replaceFirst("em1","bad");
        if(z.restore(broken)||z.stored()!=0)
            throw new AssertionError("Bad persistence corrupted new memory");
        System.out.println("ZERO-009.5 episodic recall, consequence, weights, death, and format passed");
    }
}
