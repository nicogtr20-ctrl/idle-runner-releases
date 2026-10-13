package com.overmind.zero;

import java.util.*;

/**
 * Two independently learned consequences of self-chosen actions:
 * energy from a physically touched, OBSERVED visual signature, and generic
 * real external-object displacement. No "open door" or "avoid red" rule.
 * The credits are correlational, not proofs of causality.
 */
public final class ExperienceCredit {
    private static final int LENGTH=12,WIDTH=SensorimotorMind.VALUE_WEIGHTS;
    private final ArrayDeque<Step> history=new ArrayDeque<>();
    private final float[] energetic=new float[WIDTH],mechanical=new float[WIDTH];
    public int positiveContacts=0,negativeContacts=0,updatedActions=0;
    public int physicalEvents=0,sourceMatched=0,ambiguousContacts=0;
    private static final class Step {
        final float[] senses;final int action;
        Step(float[] s,int a){senses=s.clone();action=a;}
    }
    static float clip(float x,float lo,float hi){return Math.max(lo,Math.min(hi,x));}
    public void newLife(){history.clear();}
    public void restoreCounters(int positives,int negatives,int credits){
        positiveContacts=Math.max(0,positives);negativeContacts=Math.max(0,negatives);
        updatedActions=Math.max(0,credits);
    }
    public int traceSize(){return history.size();}
    public void performed(float[] senses,int action){
        if(senses==null||senses.length!=SensorimotorMind.S||
            action<0||action>=SensorimotorMind.ACTIONS)return;
        for(float f:senses)if(!Float.isFinite(f))return;
        if(history.size()==LENGTH)history.removeFirst();
        history.addLast(new Step(senses,action));
    }
    public float energeticValue(float[] senses,int action){
        if(senses==null||senses.length!=SensorimotorMind.S||
            action<0||action>=SensorimotorMind.ACTIONS)return 0;
        int base=action*SensorimotorMind.P;
        float energy=0;
        for(int i=14;i<SensorimotorMind.S;i++)
            energy+=energetic[base+i]*senses[i];
        return clip(energy,-1.5f,1.5f);
    }
    /** A learned manipulation skill is a CAPABILITY, not a metabolic goal.
     * Actual tactile/obstacle sensors gate its contribution to the action
     * selector. Bare motion and historical moved objects cannot compete as a
     * permanent reward when ZERO needs energy. No hue/target/box labels. */
    public float mechanicalValue(float[] senses,int action){
        if(senses==null||senses.length!=SensorimotorMind.S||
           action<0||action>=SensorimotorMind.ACTIONS)return 0;
        int base=action*SensorimotorMind.P;
        float value=0;
        for(int i=3;i<14;i++)value+=mechanical[base+i]*senses[i];
        return clip(value,0f,1.5f);
    }
    public static float affordance(float[] senses){
        if(senses==null||senses.length!=SensorimotorMind.S)return 0;
        float obstacle=0f;
        for(int i=6;i<14;i++)obstacle=Math.max(obstacle,Math.max(0,senses[i]));
        float tactile=Math.max(Math.max(0,senses[4]),Math.max(0,senses[5]));
        float fromProximity=clip((obstacle-.22f)/.44f,0,1);
        return Math.max(fromProximity,clip(tactile*8f,0,1));
    }
    /** Contextual utility used by decisions AND hypothetical rollouts;
     * legacy value() remains read-only compatible for older observations.
     * The energy outcome estimate stays independent of physical competence. */
    public float adaptiveValue(float[] senses,int action){
        float energetic=energeticValue(senses,action);
        float physical=mechanicalValue(senses,action);
        float reserve=senses==null||senses.length==0?0f:clip(senses[0],0f,1f);
        float weight=(.12f+.48f*reserve)*affordance(senses);
        return energetic+physical*weight;
    }
    public float value(float[] senses,int action){
        if(senses==null||senses.length!=SensorimotorMind.S||
            action<0||action>=SensorimotorMind.ACTIONS)return 0;
        int base=action*SensorimotorMind.P;
        float energy=0,physical=0;
        for(int i=14;i<SensorimotorMind.S;i++)
            energy+=energetic[base+i]*senses[i];
        for(int i=3;i<14;i++)
            physical+=mechanical[base+i]*senses[i];
        return clip(energy,-1.5f,1.5f)+clip(physical,0f,1.5f);
    }
    /** Negative and positive energy affect ONLY features of the wavelength
     * actually touched and visibly sensed within recent performed actions.
     * If it was occluded/unseen, register uncertainty instead of inventing
     * an association with a nearby wall or gate.
     */
    public void consequence(float actualDelta,int signature){
        if(!Float.isFinite(actualDelta)||Math.abs(actualDelta)<.01f)return;
        if(actualDelta>0)positiveContacts++;else negativeContacts++;
        if(signature<0||signature>=UnknownWorld.SIGNATURES){
            ambiguousContacts++;return;
        }
        float valence=clip(actualDelta/22f,-1.5f,1.5f);
        int start=14+signature*4,age=0,matched=0;
        float eligibility=1;
        for(Iterator<Step> it=history.descendingIterator();it.hasNext()&&age++<6;){
            Step s=it.next();
            float seen=0;
            for(int k=start;k<start+4;k++)seen=Math.max(seen,s.senses[k]);
            if(seen>.06f){
                int base=s.action*SensorimotorMind.P;
                for(int k=start;k<start+4;k++)
                    energetic[base+k]=clip(energetic[base+k]+
                        .16f*valence*eligibility*s.senses[k],-2.5f,2.5f);
                matched++;updatedActions++;
            }
            eligibility*=.76f;
        }
        if(matched>0)sourceMatched++;else ambiguousContacts++;
    }
    /** Only real physical movement of an external object. A movement effect
     * remains a positive piece of information EVEN if harmful contact occurs
     * in the same interval; the two value channels do not overwrite.
     */
    public void displaced(float actualDisplacement){
        if(!Float.isFinite(actualDisplacement)||actualDisplacement<=.015f)return;
        physicalEvents++;
        float eligibility=1;int age=0;
        for(Iterator<Step> it=history.descendingIterator();it.hasNext()&&age++<5;){
            Step s=it.next();float sensed=0;
            for(int k=3;k<14;k++)sensed=Math.max(sensed,s.senses[k]);
            if(sensed>.015f){
                int base=s.action*SensorimotorMind.P;
                for(int k=3;k<14;k++)
                    mechanical[base+k]=clip(mechanical[base+k]+
                        .075f*Math.min(2f,actualDisplacement)*eligibility*s.senses[k],0,2.5f);
                updatedActions++;
            }
            eligibility*=.76f;
        }
    }
    public String encode(){
        StringBuilder b=new StringBuilder("ec2|").append(positiveContacts).append('|')
            .append(negativeContacts).append('|').append(updatedActions).append('|')
            .append(physicalEvents).append('|').append(sourceMatched).append('|')
            .append(ambiguousContacts);
        for(float x:energetic)b.append('|').append(x);
        for(float x:mechanical)b.append('|').append(x);
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||!raw.startsWith("ec2|")||raw.length()>42000)return false;
        String[] parts=raw.split("\\|",-1);
        if(parts.length!=7+WIDTH*2)return false;
        try{
            int[] count=new int[6];
            for(int i=0;i<count.length;i++){
                count[i]=Integer.parseInt(parts[1+i]);
                if(count[i]<0)return false;
            }
            float[] e=new float[WIDTH],m=new float[WIDTH];
            for(int i=0;i<WIDTH;i++){
                e[i]=Float.parseFloat(parts[7+i]);
                m[i]=Float.parseFloat(parts[7+WIDTH+i]);
                if(!Float.isFinite(e[i])||Math.abs(e[i])>2.5f||
                   !Float.isFinite(m[i])||m[i]<0||m[i]>2.5f)return false;
            }
            System.arraycopy(e,0,energetic,0,WIDTH);
            System.arraycopy(m,0,mechanical,0,WIDTH);
            positiveContacts=count[0];negativeContacts=count[1];updatedActions=count[2];
            physicalEvents=count[3];sourceMatched=count[4];ambiguousContacts=count[5];
            history.clear();return true;
        }catch(RuntimeException failure){return false;}
    }
    public static void main(String[] args){
        ExperienceCredit c=new ExperienceCredit();
        float[] atGate=new float[SensorimotorMind.S];atGate[4]=.6f;atGate[8]=.7f;
        float[] nearRed=atGate.clone();nearRed[20]=.85f; // signature 1 / sector 2
        c.performed(atGate,10);
        c.performed(nearRed,7);
        c.displaced(3.5f);
        c.consequence(-7,1);
        if(c.value(atGate,10)<=0)throw new AssertionError("Physical success erased by red damage");
        if(c.value(nearRed,7)>=c.value(atGate,7))
            throw new AssertionError("Source-conditioned red consequence missing");
        float[] invisible=new float[SensorimotorMind.S];invisible[16]=.9f;
        float old=c.value(invisible,7);
        c.consequence(22,0);
        if(old!=c.value(invisible,7)||c.ambiguousContacts!=1)
            throw new AssertionError("Invented credit for unobserved signature");
        float[] starving=new float[SensorimotorMind.S];
        starving[0]=.002f;starving[14]=.60f;starving[18]=.51f;
        float mechanicFromPast=c.mechanicalValue(atGate,10);
        if(mechanicFromPast<=0)throw new AssertionError("Missing mechanical evidence");
        if(c.adaptiveValue(starving,10)!=c.energeticValue(starving,10))
            throw new AssertionError("Unrelated old mechanical skill drives starvation behavior");
        float[] nearby=starving.clone();nearby[6]=.93f;nearby[8]=.7f;nearby[4]=.3f;
        float near=c.adaptiveValue(nearby,10);
        if(near<=c.adaptiveValue(starving,10)||
           near-c.energeticValue(nearby,10)>.20f*1.5f)
            throw new AssertionError("Nearby mechanical affordance not gated by metabolic need");
        float[] fed=nearby.clone();fed[0]=.9f;
        if(c.adaptiveValue(fed,10)<=near)
            throw new AssertionError("Physical exploration remained equally urgent at low energy");
        String saved=c.encode();
        ExperienceCredit recovered=new ExperienceCredit();
        if(!recovered.restore(saved)||recovered.value(nearRed,7)!=c.value(nearRed,7)||
           recovered.physicalEvents!=1)throw new AssertionError("Separated values not persistent");
        if(recovered.restore(saved+"x")||!saved.equals(recovered.encode()))
            throw new AssertionError("Corrupt profile damaged existing learned values");
        recovered.newLife();
        if(recovered.traceSize()!=0||recovered.value(atGate,10)<=0)
            throw new AssertionError("Life reset destroyed learning");
        System.out.println("ZERO 0.10.1 real energy-source credit and independent physical outcome tests pass");
    }
}
