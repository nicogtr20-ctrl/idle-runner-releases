package com.overmind.zero;

import java.util.ArrayList;
import java.util.Random;

/**
 * Learned, bounded autobiographical experience.
 * Stores *actually lived* sensations/actions and physical weights, including
 * outcomes that helped (positive) and harmed (negative) energy homeostasis.
 * No hardwired object color, door opening script, object identity, or oracle.
 *
 * Contextual experiences are NOT proof of causal understanding.
 */
public final class EpisodicMemory {
    public static final int DEPTH=36,MAX_EPISODES=24;
    private static final float MIN_EFFECT=3f,MATCH=.115f;
    private static final int MAX_ENCODED=650000;
    private static final float[] FEATURE_WEIGHT=new float[SensorimotorMind.S];
    static {
        for(int i=0;i<FEATURE_WEIGHT.length;i++)
            FEATURE_WEIGHT[i]=i<3?.85f:i<6?1.35f:i<14?1.6f:1.35f;
    }
    static final class Step {
        final float[] senses;final int action;
        Step(float[] s,int a){senses=s.clone();action=a;}
    }
    static final class Event {
        final ArrayList<Step> trace=new ArrayList<>();
        float effect;
        int confirmed=1,failures=0;
        float[] motor,hand,membrane;
        boolean harmful(){return effect<0;}
        float priority(){return Math.min(12,confirmed)*1.8f+
            Math.min(90,Math.abs(effect))*.12f-Math.min(10,failures)*.8f;}
    }
    private final ArrayList<Step> recent=new ArrayList<>();
    private final ArrayList<Event> events=new ArrayList<>();
    private Event selected=null;
    private int selectedIndex=-1,continuation=0,cooldown=0;
    private int replayActionsWithoutEffect=0;
    private float[] replayMotor,replayHand,replayMembrane;
    private boolean replayActive=false,replayAtAction=false,dirty=false;
    private int aversionAction=-1;
    private float aversionStrength=0;
    public int captured=0,matched=0,replayed=0,repeatEffects=0;
    public int avoided=0,consolidated=0;
    public int lastTraceLength=0;
    public float lastEventEffect=0,lastSimilarity=0;
    public int stored(){return events.size();}
    public int positiveCount(){
        int k=0;for(Event e:events)if(!e.harmful())k++;return k;
    }
    public int negativeCount(){
        int k=0;for(Event e:events)if(e.harmful())k++;return k;
    }
    public int longestTrace(){
        int n=0;for(Event e:events)n=Math.max(n,e.trace.size());return n;
    }
    public int strongestConfirmations(){
        int n=0;for(Event e:events)n=Math.max(n,e.confirmed);return n;
    }
    public boolean physicallyReplaying(){return replayActive;}
    public boolean takeDirty(){boolean d=dirty;dirty=false;return d;}
    public void resetLife(){
        recent.clear();selected=null;selectedIndex=-1;continuation=0;cooldown=0;
        replayActionsWithoutEffect=0;replayActive=false;replayAtAction=false;
        replayMotor=replayHand=replayMembrane=null;
        aversionAction=-1;aversionStrength=0;
    }
    public void action(float[] senses,int action){
        if(senses==null||senses.length!=SensorimotorMind.S||
            action<0||action>=SensorimotorMind.ACTIONS)return;
        for(float value:senses)if(!Float.isFinite(value))return;
        if(recent.size()>=DEPTH)recent.remove(0);
        recent.add(new Step(senses,action));
    }
    private static boolean salient(float[] s){
        float obstacle=0;
        for(int i=6;i<14;i++)obstacle=Math.max(obstacle,s[i]);
        float colour=0;
        for(int i=14;i<s.length;i++)colour=Math.max(colour,s[i]);
        return obstacle>.22f||colour>.35f||s[4]>.012f||s[5]>.012f;
    }
    private static float distance(float[] a,float[] b){
        float sum=0,total=0;
        for(int i=0;i<a.length;i++){
            float w=FEATURE_WEIGHT[i];
            sum+=w*Math.min(2,Math.abs(a[i]-b[i]));
            total+=w;
        }
        return sum/total;
    }
    private static boolean validWeights(float[] motor,float[] hand,float[] membrane){
        if(motor==null||hand==null||membrane==null||
           motor.length!=MotorBrain.PARAMS||hand.length!=ContactLearning.PARAMS||
           membrane.length!=Morphology.PARAMS)return false;
        for(float[] array:new float[][]{motor,hand,membrane})
            for(float v:array)if(!Float.isFinite(v)||Math.abs(v)>5)return false;
        return true;
    }
    /** Preserve earlier 0.9.5 API, where the second input was energy GAIN. */
    public boolean consequence(float physicalEffect,float energyDelta,
                               float[] motor,float[] hand,float[] membrane){
        return consequence(physicalEffect,energyDelta,motor,hand,membrane,replayAtAction);
    }
    /**
     * Actual energyDelta from physical resource TOUCH, never passive hunger.
     * Positive contact and generic obstacle displacement are both beneficial;
     * damage is intrinsically negative even if no object visibly moves.
     */
    public boolean consequence(float physicalEffect,float energyDelta,
                               float[] motor,float[] hand,float[] membrane,
                               boolean performedRecall){
        if(!Float.isFinite(physicalEffect)||!Float.isFinite(energyDelta)||
           !validWeights(motor,hand,membrane))return false;
        float effect=Math.min(100,Math.max(0,physicalEffect))+
                     Math.min(80,Math.max(0,energyDelta))*.25f-
                     Math.min(80,Math.max(0,-energyDelta))*.60f;
        if(Math.abs(effect)<MIN_EFFECT||recent.size()<2)return false;
        if(performedRecall && effect>0){
            repeatEffects++;replayActionsWithoutEffect=0;dirty=true;
        }
        Event candidate=new Event();candidate.effect=effect;
        // Begin at the first detectable environmental cue rather than always
        // cutting at nine. Vary length from two to 36 lived actions; include
        // up to two approach actions preceding the first salient perception.
        int start=-1;
        for(int i=0;i<recent.size();i++){
            if(salient(recent.get(i).senses)){start=Math.max(0,i-2);break;}
        }
        if(start<0)start=Math.max(0,recent.size()-4);
        for(int i=start;i<recent.size();i++){
            Step s=recent.get(i);
            candidate.trace.add(new Step(s.senses,s.action));
        }
        candidate.motor=motor.clone();candidate.hand=hand.clone();
        candidate.membrane=membrane.clone();
        // Comparing the sensory snapshot near the outcome also makes older
        // 9-step memories eligible for consolidation after migration.
        Step end=candidate.trace.get(candidate.trace.size()-1);
        for(Event old:events){
            Step tail=old.trace.get(old.trace.size()-1);
            if(old.harmful()==candidate.harmful() &&
                tail.action==end.action &&
                distance(tail.senses,end.senses)<.054f){
                old.confirmed=Math.min(999,old.confirmed+1);
                consolidated++;
                if(Math.abs(effect)>Math.abs(old.effect)*1.06f){
                    old.effect=effect;
                    old.motor=candidate.motor;old.hand=candidate.hand;
                    old.membrane=candidate.membrane;
                    old.trace.clear();old.trace.addAll(candidate.trace);
                }
                lastEventEffect=effect;lastTraceLength=candidate.trace.size();
                dirty=true;
                recent.clear();
                return true;
            }
        }
        if(events.size()>=MAX_EPISODES){
            int weakest=0;
            for(int i=1;i<events.size();i++)
                if(events.get(i).priority()<events.get(weakest).priority())weakest=i;
            if(candidate.priority()<events.get(weakest).priority()){
                recent.clear();return false;
            }
            events.remove(weakest);
        }
        events.add(candidate);captured++;
        lastTraceLength=candidate.trace.size();lastEventEffect=effect;
        recent.clear();dirty=true;
        return true;
    }
    /**
     * Identify the best remembered beneficial ACTION and the most relevant
     * harmful ACTION separately. A harmful episode never reenacts its body.
     */
    public int suggested(float[] senses){
        return suggested(senses,true);
    }
    /**
     * A measured locomotion stall temporarily suppresses positive recall.
     * Harmful experience warnings remain enabled. This NEVER decides heading.
     */
    public int suggested(float[] senses,boolean allowPositive){
        replayAtAction=false;replayActive=false;
        replayMotor=replayHand=replayMembrane=null;
        aversionAction=-1;aversionStrength=0;
        if(senses==null||senses.length!=SensorimotorMind.S||
           !salient(senses))return -1;
        if(replayActionsWithoutEffect>=12){
            if(selected!=null){selected.failures++;dirty=true;}
            cooldown=30;replayActionsWithoutEffect=0;
            selected=null;continuation=0;
        }
        Event positive=null;int positiveIndex=-1;
        float posDist=MATCH;
        float negDist=MATCH;
        for(Event e:events){
            for(int i=0;i<e.trace.size();i++){
                float dist=distance(senses,e.trace.get(i).senses);
                float adjusted=dist+.004f*i;
                if(e.harmful()){
                    // The last few actions are closest to the actual injury.
                    // Do not condemn every exploratory action in a long walk.
                    if(i>=e.trace.size()-3&&adjusted<negDist){
                        negDist=adjusted;
                        aversionAction=e.trace.get(i).action;
                        aversionStrength=Math.min(1.3f,.48f+.07f*Math.min(9,e.confirmed));
                    }
                }else if(adjusted<posDist){
                    posDist=adjusted;positive=e;positiveIndex=i;
                }
            }
        }
        if(selected!=null&&continuation<selected.trace.size()&&!selected.harmful()){
            Step step=selected.trace.get(continuation);
            float d=distance(senses,step.senses);
            if(d<MATCH*1.25f){positive=selected;positiveIndex=continuation;posDist=d;}
        }
        if(negDist+.01f<posDist)positive=null;
        if(!allowPositive)positive=null;
        if(cooldown>0){cooldown--;positive=null;}
        selected=positive;selectedIndex=positiveIndex;
        if(positive==null){continuation=0;return -1;}
        matched++;lastSimilarity=1-Math.min(1,posDist);
        return positive.trace.get(positiveIndex).action;
    }
    public float bias(int action,int positiveSuggestion){
        float positive=0;
        if(action==positiveSuggestion&&selected!=null)
            positive=Math.min(1.15f,.45f+.07f*Math.min(10,selected.confirmed));
        float negative=action==aversionAction?aversionStrength:0;
        return positive-negative;
    }
    public void chosen(int actual,int suggestion){
        replayAtAction=false;replayActive=false;
        if(aversionAction>=0&&actual!=aversionAction)avoided++;
        if(suggestion<0||selected==null||actual!=suggestion){
            selected=null;continuation=0;return;
        }
        replayed++;replayActionsWithoutEffect++;
        replayAtAction=true;replayActive=true;
        replayMotor=selected.motor;replayHand=selected.hand;
        replayMembrane=selected.membrane;
        continuation=selectedIndex+1;
        if(continuation>=selected.trace.size()){
            selected=null;continuation=0;cooldown=3;
        }
    }
    /**
     * The best saved MOTOR controller is NEVER overridden by an old episode.
     * Prior physical snapshots of hand/membrane may be tried ONLY during
     * verified membrane contact and when GameActivity is not testing a
     * candidate. A remembered action alone changes no actuator weights.
     */
    public float[] weights(int type,float[] normal){
        return weights(type,normal,false);
    }
    public float[] weights(int type,float[] normal,boolean realTactileContact){
        if(!replayActive||!realTactileContact||type==0)return normal;
        return type==1?replayHand:type==2?replayMembrane:normal;
    }

    /** em2 extends em1 without rewriting old motor/world network formats. */
    public String encode(){
        StringBuilder b=new StringBuilder("em2;").append(captured).append(';')
           .append(matched).append(';').append(replayed).append(';')
           .append(repeatEffects).append(';').append(avoided).append(';')
           .append(consolidated).append(';').append(events.size());
        for(Event e:events){
            b.append(';').append(e.effect).append(',').append(e.confirmed)
             .append(',').append(e.failures).append(',').append(e.trace.size());
            for(Step t:e.trace){
                b.append(',').append(t.action);
                for(float v:t.senses)b.append(',').append(v);
            }
            for(float[] arr:new float[][]{e.motor,e.hand,e.membrane})
                for(float v:arr)b.append(',').append(v);
        }
        return b.toString();
    }
    /** Parse into a temporary list, commit only after full validation. */
    public boolean restore(String raw){
        if(raw==null||raw.isEmpty()||raw.length()>MAX_ENCODED)return false;
        boolean legacy=raw.startsWith("em1;"),modern=raw.startsWith("em2;");
        if(!legacy&&!modern)return false;
        try{
            String[] group=raw.split(";",-1);
            int header=legacy?6:8;
            if(group.length<header||group.length>header+MAX_EPISODES)return false;
            int ct=Integer.parseInt(group[1]),mt=Integer.parseInt(group[2]),
                rp=Integer.parseInt(group[3]),success=Integer.parseInt(group[4]);
            int av=legacy?0:Integer.parseInt(group[5]);
            int cons=legacy?0:Integer.parseInt(group[6]);
            int count=Integer.parseInt(group[header-1]);
            if(ct<0||mt<0||rp<0||success<0||av<0||cons<0||
                count<0||count>MAX_EPISODES||group.length!=header+count)return false;
            ArrayList<Event> decoded=new ArrayList<>();
            for(int n=0;n<count;n++){
                String[] v=group[header+n].split(",",-1);int index=0;
                Event e=new Event();e.effect=Float.parseFloat(v[index++]);
                if(!legacy){
                    e.confirmed=Integer.parseInt(v[index++]);
                    e.failures=Integer.parseInt(v[index++]);
                }
                int steps=Integer.parseInt(v[index++]);
                if(steps<2||steps>DEPTH||e.confirmed<1||e.confirmed>999||
                    e.failures<0||e.failures>999||
                    !Float.isFinite(e.effect)||Math.abs(e.effect)<MIN_EFFECT||
                    v.length!=(legacy?2:4)+steps*(1+SensorimotorMind.S)+
                        MotorBrain.PARAMS+ContactLearning.PARAMS+Morphology.PARAMS)
                    return false;
                for(int t=0;t<steps;t++){
                    int act=Integer.parseInt(v[index++]);
                    if(act<0||act>=SensorimotorMind.ACTIONS)return false;
                    float[] senses=new float[SensorimotorMind.S];
                    for(int k=0;k<senses.length;k++){
                        senses[k]=Float.parseFloat(v[index++]);
                        if(!Float.isFinite(senses[k])||Math.abs(senses[k])>3)return false;
                    }
                    e.trace.add(new Step(senses,act));
                }
                e.motor=new float[MotorBrain.PARAMS];
                e.hand=new float[ContactLearning.PARAMS];
                e.membrane=new float[Morphology.PARAMS];
                for(float[] arr:new float[][]{e.motor,e.hand,e.membrane}){
                    for(int k=0;k<arr.length;k++){
                        arr[k]=Float.parseFloat(v[index++]);
                        if(!Float.isFinite(arr[k])||Math.abs(arr[k])>5)return false;
                    }
                }
                decoded.add(e);
            }
            events.clear();events.addAll(decoded);
            captured=ct;matched=mt;replayed=rp;repeatEffects=success;
            avoided=av;consolidated=cons;
            lastEventEffect=events.isEmpty()?0:events.get(events.size()-1).effect;
            lastTraceLength=events.isEmpty()?0:events.get(events.size()-1).trace.size();
            resetLife();dirty=false;
            return true;
        }catch(RuntimeException e){return false;}
    }
    public static void main(String[] args){
        EpisodicMemory m=new EpisodicMemory();
        float[] safe=new float[SensorimotorMind.S];safe[14]=.8f;safe[6]=.5f;
        float[] hazard=safe.clone();hazard[14]=0;hazard[18]=.8f;
        float[] motor=MotorBrain.virgin(new Random(5)),
           hand=ContactLearning.virgin(),skin=new float[Morphology.PARAMS];
        // One prolonged lived encounter may need over 9 actual actions.
        for(int i=0;i<27;i++){float[] x=safe.clone();x[1]=i*.006f;m.action(x,4);}
        if(!m.consequence(0,22,motor,hand,skin)||m.lastTraceLength<20||
           m.positiveCount()!=1||m.negativeCount()!=0)
            throw new AssertionError("Variable-length energy-positive event lost");
        String snapshot=m.encode();
        for(int i=0;i<12;i++)m.action(hazard,7);
        if(!m.consequence(0,-7,motor,hand,skin)||m.negativeCount()!=1)
            throw new AssertionError("Real energy loss must be remembered");
        if(m.suggested(hazard)!=-1||m.bias(7,-1)>=-.3f)
            throw new AssertionError("Negative experience should discourage, not replay");
        if(m.suggested(safe)!=4||m.bias(4,4)<.3f)
            throw new AssertionError("Similar positive experience was forgotten");
        m.chosen(4,4);
        if(!m.physicallyReplaying()||m.weights(0,motor,true)!=motor||
            m.weights(1,hand)!=hand||m.weights(2,skin)!=skin||
            m.weights(1,hand,true)==hand||m.weights(2,skin,true)==skin)
            throw new AssertionError("Memory must never replace learned motor; physical grip needs real contact");
        int old=m.consolidated;
        for(int i=0;i<10;i++)m.action(safe,4);
        if(!m.consequence(0,22,motor,hand,skin,true)||
           m.consolidated<=old||m.stored()!=2||m.strongestConfirmations()<2)
            throw new AssertionError("Repeated helpful experiences must consolidate");
        m.resetLife();
        if(m.stored()!=2)throw new AssertionError("Death erased autobiographical memory");
        EpisodicMemory restored=new EpisodicMemory();
        if(!restored.restore(m.encode())||restored.stored()!=2||
           restored.negativeCount()!=1||restored.consolidated<1)
            throw new AssertionError("em2 persistence invalid");
        EpisodicMemory corrupted=new EpisodicMemory();
        if(corrupted.restore("em2;no")||corrupted.stored()!=0)
            throw new AssertionError("Corrupt memory accepted");
        // Simulate real 0.9.5 on-disk format.
        EpisodicMemory legacy=new EpisodicMemory();
        String oldFormat=snapshot.replaceFirst("em2;", "em1;");
        String[] oldGroups=oldFormat.split(";",-1);
        String[] head=oldGroups[8].split(",",-1);
        StringBuilder oldEvent=new StringBuilder(head[0]).append(",").append(head[3]);
        // Old format allowed at most 9 actions; use newest nine.
        int steps=Integer.parseInt(head[3]),first=Math.max(0,steps-9);
        oldEvent.setLength(0);oldEvent.append(head[0]).append(",").append(steps-first);
        for(int i=first;i<steps;i++)
            for(int k=0;k<1+SensorimotorMind.S;k++)
                oldEvent.append(",").append(head[4+i*(1+SensorimotorMind.S)+k]);
        int tail=4+steps*(1+SensorimotorMind.S);
        for(int k=tail;k<head.length;k++)oldEvent.append(",").append(head[k]);
        String em1="em1;1;0;0;0;1;"+oldEvent;
        if(!legacy.restore(em1)||legacy.stored()!=1||legacy.positiveCount()!=1)
            throw new AssertionError("v0.9.5 em1 memory migration failed");
        if(!legacy.encode().startsWith("em2;"))
            throw new AssertionError("migration did not upgrade format");
        System.out.println("ZERO-009.6 passed: varied episodes, beneficial/dangerous recall, consolidation and em1 migration");
    }
}
