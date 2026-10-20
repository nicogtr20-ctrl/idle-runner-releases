package com.overmind.zero;

import java.io.*;
import java.util.Arrays;

/**
 * Sensory/physical memory owned by the existing V353 cognition.
 * There is NO new actor or movement instruction. A consequence is associated
 * with a light only after a genuine energy contact, and a direction is
 * predicted only from the original physical body's measured response.
 *
 * Historical analogy: ExperienceCredit + InstrumentalNavigation, but there
 * is only ONE GRU3 chooser and existing V353 world predictors are untouched.
 */
final class Zero3InstrumentalCreditV371 {
    static final int VERSION=371, BINS=8, BANDS=3, TRACE=6;
    private final float[] learnedX=new float[BINS],learnedY=new float[BINS];
    private final int[] trained=new int[BINS],validated=new int[BINS];
    private final double[] error=new double[BINS],baseline=new double[BINS];
    // Real PRE visual evidence and POST movement of the last six completed
    // physically clean intentions. NEVER persist a pending/unexecuted event.
    private final float[][] recentSight=new float[TRACE][BANDS*8];
    private final float[] recentDX=new float[TRACE],recentDY=new float[TRACE];
    private final int[] recentBank=new int[TRACE];
    private int history;
    private long linkedContacts,ambiguousContacts,stalledActions;
    private float[] pendingSight;
    private int pendingBank=-1;
    private boolean pendingHeldout;
    private int lastBank=-1,stalledStreak;
    private static float limit(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    private static int heading(float x,float y){
        if(!Float.isFinite(x)||!Float.isFinite(y)||Math.hypot(x,y)<.1)
            throw new IllegalArgumentException("V371 invalid physical intention");
        return Math.floorMod(Math.round((float)Math.atan2(y,x)/(float)(Math.PI/4)),BINS);
    }
    void begin(float[] observation194,float[] action,boolean heldout){
        if(pendingSight!=null||observation194==null||observation194.length!=194||
           action==null||action.length!=5)
            throw new IllegalStateException("V371 unfinished or malformed motor decision");
        for(int i=20;i<44;i++)if(!Float.isFinite(observation194[i])||
                observation194[i]<0||observation194[i]>1)
            throw new IllegalArgumentException("V371 invalid photons");
        pendingSight=Arrays.copyOfRange(observation194,20,44);
        pendingBank=heading(action[0],action[1]);pendingHeldout=heldout;
    }
    void complete(float dx,float dy,float seconds,boolean clean,boolean heldout){
        if(pendingSight==null||pendingHeldout!=heldout||
           !Float.isFinite(dx)||!Float.isFinite(dy)||
           !Float.isFinite(seconds)||seconds<=0||seconds>.651f)
            throw new IllegalStateException("V371 incomplete physical evidence");
        if(clean){
            // Normalized to a physical 0.30s decision for comparable trials.
            // This is a measured displacement, NOT an authored motion target.
            float scale=.30f/seconds;
            float mx=limit(dx*scale,-120,120),my=limit(dy*scale,-120,120);
            int bank=pendingBank;
            if(heldout){
                validated[bank]++;
                double e=(mx-learnedX[bank])*(mx-learnedX[bank])+
                         (my-learnedY[bank])*(my-learnedY[bank]);
                double zero=mx*mx+my*my;
                float alpha=validated[bank]==1?1f:.1f;
                error[bank]+=alpha*(e-error[bank]);
                baseline[bank]+=alpha*(zero-baseline[bank]);
            }else{
                float alpha=trained[bank]==0?1f:.10f;
                learnedX[bank]+=alpha*(mx-learnedX[bank]);
                learnedY[bank]+=alpha*(my-learnedY[bank]);
                trained[bank]++;
                int slot=history%TRACE;
                System.arraycopy(pendingSight,0,recentSight[slot],0,BANDS*8);
                recentDX[slot]=mx;recentDY[slot]=my;recentBank[slot]=bank;history++;
            }
            if(!heldout){
                // Stagnation is recognized from PHYSICS, not from staying in
                // one coordinate or from slow exploratory intention alone.
                float along=mx*(float)Math.cos(bank*Math.PI/4)+my*(float)Math.sin(bank*Math.PI/4);
                if(along<.35f){stalledStreak=lastBank==bank?stalledStreak+1:1;stalledActions++;}
                else stalledStreak=0;
                lastBank=bank;
            }
        }
        abort();
    }
    void contact(float[] observedPhotons,float actualDelta,boolean heldout){
        if(pendingSight==null||observedPhotons==null||observedPhotons.length!=24||
            !Float.isFinite(actualDelta))throw new IllegalStateException("V371 contact data invalid");
        if(heldout||actualDelta==0f)return;
        int found=-1;float peak=0f,runner=0f;
        for(int b=0;b<BANDS;b++){
            float x=0f;
            for(int ray=0;ray<8;ray++){
                float p=observedPhotons[b*8+ray];
                if(!Float.isFinite(p)||p<0f||p>1f)
                    throw new IllegalArgumentException("V371 photons");
                x=Math.max(x,p);
            }
            if(x>peak){runner=peak;peak=x;found=b;}
            else runner=Math.max(runner,x);
        }
        if(found<0||peak<.25f||peak<runner*1.15f){ambiguousContacts++;return;}
        // Verify that PREVIOUS motor choices actually approached a visible
        // frequency that later caused real energy gain/loss. The signed
        // contact outcome itself is handled by V369, once, separately.
        boolean matched=false;
        for(int age=0;age<Math.min(history,TRACE);age++){
            int slot=Math.floorMod(history-1-age,TRACE);
            float[] sight=recentSight[slot];
            float projection=0f,best=0f;
            for(int ray=0;ray<8;ray++){
                float seen=sight[found*8+ray];
                float angle=(float)(ray*Math.PI/4d);
                float toward=recentDX[slot]*(float)Math.cos(angle)+
                    recentDY[slot]*(float)Math.sin(angle);
                if(seen>.08f&&toward>.35f&&seen*toward>best){
                    projection=toward;best=seen*toward;
                }
            }
            if(projection>.35f){matched=true;break;}
        }
        if(matched)linkedContacts++;else ambiguousContacts++;
    }
    /** Trust motor direction only if its OUT-OF-TRAIN physical displacement
     * estimate beats zero motion, with enough evidence. */
    boolean motorVerified(int bank){
        return trained[bank]>=4&&validated[bank]>=3&&baseline[bank]>1.0&&
            error[bank]<baseline[bank]*.90;
    }
    float score(float[] observation194,float[] chosen,
                Zero3ContactValenceV369 contact){
        if(observation194==null||observation194.length!=194||
           chosen==null||chosen.length<2||contact==null)
            throw new IllegalArgumentException("V371 score shape");
        int bank=heading(chosen[0],chosen[1]);
        if(!motorVerified(bank))return 0f;
        float mx=learnedX[bank],my=learnedY[bank];
        float reward=0f,brightness=0f;
        for(int b=0;b<BANDS;b++){
            float value=contact.valueForInstrumentalV371(b);
            if(Math.abs(value)<.10f)continue;
            for(int ray=0;ray<8;ray++){
                float seen=observation194[20+b*8+ray];
                if(!Float.isFinite(seen)||seen<0||seen>1)
                    throw new IllegalArgumentException("V371 nonphysical rays");
                if(seen<.08f)continue;
                double theta=ray*Math.PI/4;
                float toward=limit((mx*(float)Math.cos(theta)+
                    my*(float)Math.sin(theta))/35f,-1,1);
                float strength=seen*seen;
                reward+=value*strength*toward;
                brightness+=strength;
            }
        }
        float utility=brightness>.015f?limit(reward/(.35f+brightness),-.5f,.5f):0f;
        // If a real action has repeated produced no forward material motion,
        // discourage repeating that exact bank, without selecting a direction.
        if(bank==lastBank&&stalledStreak>=3)utility-=.04f;
        return limit(utility,-.5f,.5f);
    }
    boolean hasVerifiedDirectionalEvidence(float[] photons194,Zero3ContactValenceV369 v){
        if(photons194==null||v==null)return false;
        for(int i=0;i<BINS;i++)if(motorVerified(i)){
            float[] action={(float)Math.cos(i*Math.PI/4),(float)Math.sin(i*Math.PI/4)};
            if(Math.abs(score(photons194,action,v))>.015f)return true;
        }
        return false;
    }
    void abort(){pendingSight=null;pendingBank=-1;pendingHeldout=false;}
    void newLife(){abort();history=0;stalledStreak=0;lastBank=-1;}
    void copyTo(Zero3InstrumentalCreditV371 c){
        System.arraycopy(learnedX,0,c.learnedX,0,BINS);
        System.arraycopy(learnedY,0,c.learnedY,0,BINS);
        System.arraycopy(trained,0,c.trained,0,BINS);
        System.arraycopy(validated,0,c.validated,0,BINS);
        System.arraycopy(error,0,c.error,0,BINS);
        System.arraycopy(baseline,0,c.baseline,0,BINS);
        c.linkedContacts=linkedContacts;c.ambiguousContacts=ambiguousContacts;
        c.stalledActions=stalledActions;
    }
    int verifiedDirections(){int n=0;for(int i=0;i<BINS;i++)if(motorVerified(i))n++;return n;}
    long linkedContacts(){return linkedContacts;}
    long ambiguousContacts(){return ambiguousContacts;}
    long stalledActions(){return stalledActions;}
    void write(DataOutputStream d)throws IOException{
        if(pendingSight!=null)throw new IOException("V371 pending checkpoint");
        d.writeInt(VERSION);d.writeLong(linkedContacts);d.writeLong(ambiguousContacts);
        d.writeLong(stalledActions);
        for(int i=0;i<BINS;i++){
            d.writeInt(trained[i]);d.writeInt(validated[i]);
            d.writeFloat(learnedX[i]);d.writeFloat(learnedY[i]);
            d.writeDouble(error[i]);d.writeDouble(baseline[i]);
        }
    }
    static Zero3InstrumentalCreditV371 read(DataInputStream d)throws IOException{
        if(d.readInt()!=VERSION)throw new IOException("V371 schema");
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        m.linkedContacts=d.readLong();m.ambiguousContacts=d.readLong();m.stalledActions=d.readLong();
        if(m.linkedContacts<0||m.ambiguousContacts<0||m.stalledActions<0)
            throw new IOException("V371 counts");
        for(int i=0;i<BINS;i++){
            m.trained[i]=d.readInt();m.validated[i]=d.readInt();
            m.learnedX[i]=d.readFloat();m.learnedY[i]=d.readFloat();
            m.error[i]=d.readDouble();m.baseline[i]=d.readDouble();
            if(m.trained[i]<0||m.validated[i]<0||
               m.trained[i]>100000000||m.validated[i]>100000000||
               !Float.isFinite(m.learnedX[i])||!Float.isFinite(m.learnedY[i])||
               Math.abs(m.learnedX[i])>120||Math.abs(m.learnedY[i])>120||
               !Double.isFinite(m.error[i])||!Double.isFinite(m.baseline[i])||
               m.error[i]<0||m.baseline[i]<0)
                throw new IOException("V371 physical response invalid");
        }
        return m;
    }
}
