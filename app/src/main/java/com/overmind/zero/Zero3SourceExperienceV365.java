package com.overmind.zero;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;

/**
 * V365: evidence-only action-conditioned energy model.
 *
 * The names of colours, food and damage do not exist here. Three raw
 * wavelengths and eight real visual bearings are just sensor channels.
 * A source becomes desirable or undesirable ONLY after a REAL material
 * touch changes the organism's actual reserve and the source's wavelength
 * was visible before one of ZERO's self-chosen actions.
 *
 * It scores ONLY three directions already sampled by the GRU actor. It
 * cannot generate a direction, move a body, target an object from a world
 * coordinate, award fictitious energy or force an escape reflex.
 */
final class Zero3SourceExperienceV365 {
    static final String KEY="zero3_source_outcome_v365";
    private static final int FREQUENCIES=3, DIRECTIONS=8, WINDOW=6;
    private final float[] learned=new float[FREQUENCIES*DIRECTIONS];
    private final int[] observedByFrequency=new int[FREQUENCIES];
    private final ArrayDeque<Trace> recent=new ArrayDeque<>(WINDOW);
    private long physicalContacts,attributedContacts,unseenContacts;
    private static final class Trace {
        final float[] spectral;final int heading;
        Trace(float[] seen,int heading){spectral=seen.clone();this.heading=heading;}
    }
    private static float clip(float a,float lo,float hi){return Math.max(lo,Math.min(hi,a));}
    private static int octant(float x,float y){
        if(!Float.isFinite(x)||!Float.isFinite(y)||x*x+y*y<.20f)
            throw new IllegalArgumentException("Unexecuted motor heading");
        return ((int)Math.floor((Math.atan2(y,x)+Math.PI/8)/(Math.PI/4))+8)%8;
    }
    void chosen(float[] senses194,float x,float y){
        float[] spectral=spectral(senses194);
        if(recent.size()==WINDOW)recent.removeFirst();
        recent.addLast(new Trace(spectral,octant(x,y)));
    }
    private static float[] spectral(float[] senses){
        if(senses==null||senses.length!=194)
            throw new IllegalArgumentException("V365 real spectral 194 observations missing");
        float[] v=new float[FREQUENCIES*DIRECTIONS];
        for(int i=0;i<v.length;i++){
            float value=senses[20+i];
            if(!Float.isFinite(value))throw new IllegalArgumentException("Nonfinite real photons");
            v[i]=clip(value,0,1);
        }
        return v;
    }
    /**
     * Called ONLY from original GameActivity after REAL material contact;
     * it gets the physical source wavelength, never its hidden physical law.
     * Positive and negative reserve outcomes train the SAME unbiased
     * coefficients with opposite observed signs.
     */
    void actualContact(int observedWavelength,float realReserveDelta){
        if(observedWavelength<0||observedWavelength>=FREQUENCIES||
           !Float.isFinite(realReserveDelta)||Math.abs(realReserveDelta)<.01f)return;
        physicalContacts++;
        float valence=clip(realReserveDelta/22f,-1f,1f),eligibility=1f;
        int matched=0,age=0;
        for(Iterator<Trace> it=recent.descendingIterator();it.hasNext()&&age++<WINDOW;){
            Trace action=it.next();
            float total=0f;for(int az=0;az<DIRECTIONS;az++)
                total+=action.spectral[observedWavelength*DIRECTIONS+az];
            if(total<.04f){eligibility*=.76f;continue;}
            matched++;
            for(int az=0;az<DIRECTIONS;az++){
                float photon=action.spectral[observedWavelength*DIRECTIONS+az];
                if(photon<.035f)continue;
                int relative=(az-action.heading+DIRECTIONS)%DIRECTIONS;
                int i=observedWavelength*DIRECTIONS+relative;
                float scale=.32f*eligibility*clip(photon,0,1);
                learned[i]=clip(learned[i]+scale*(valence-learned[i]),-1f,1f);
            }
            eligibility*=.76f;
        }
        if(matched>0){attributedContacts++;observedByFrequency[observedWavelength]++;}
        else unseenContacts++;
    }
    /** Learned action-conditioned energy expectation (NEVER hand-authored
     * blue attraction or red repulsion). Higher means physically observed
     * energy was historically gained in comparable sensory/action contexts.
     */
    float expectation(float[] senses194,float dx,float dy){
        if(attributedContacts==0)return 0f;
        float[] observations=spectral(senses194);
        int heading=octant(dx,dy);
        float predicted=0f,scale=0f;
        for(int freq=0;freq<FREQUENCIES;freq++){
            if(observedByFrequency[freq]==0)continue;
            for(int az=0;az<DIRECTIONS;az++){
                float photon=observations[freq*DIRECTIONS+az];
                if(photon<.025f)continue;
                int relative=(az-heading+DIRECTIONS)%DIRECTIONS;
                predicted+=photon*learned[freq*DIRECTIONS+relative];
                scale+=photon;
            }
        }
        return scale>0?clip(predicted/scale,-1,1):0f;
    }
    int chooseAmongActorProposals(float[] realSenses,float[][] proposals,int original){
        if(proposals==null||original<0||original>=proposals.length)
            throw new IllegalArgumentException("V365 candidate selection");
        if(attributedContacts==0)return original;
        float baseline=expectation(realSenses,proposals[original][0],proposals[original][1]);
        float highest=baseline;int best=original;
        for(int i=0;i<proposals.length;i++){
            if(i==original)continue;
            float predicted=expectation(realSenses,proposals[i][0],proposals[i][1]);
            if(predicted>highest+.008f){highest=predicted;best=i;}
        }
        return best; // NEVER generates a new motor action.
    }
    void newLife(){recent.clear();}
    long contacts(){return physicalContacts;}
    long matched(){return attributedContacts;}
    long unseen(){return unseenContacts;}
    String encode(){
        StringBuilder s=new StringBuilder("sv365|").append(physicalContacts).append('|')
            .append(attributedContacts).append('|').append(unseenContacts);
        for(int n:observedByFrequency)s.append('|').append(n);
        for(float w:learned)s.append('|').append(w);
        return s.toString();
    }
    boolean restore(String saved){
        if(saved==null||saved.isEmpty())return true;
        String[] a=saved.split("\\|",-1);
        if(a.length!=4+FREQUENCIES+FREQUENCIES*DIRECTIONS||
           !a[0].equals("sv365")||saved.length()>3500)return false;
        try {
            long pc=Long.parseLong(a[1]),ac=Long.parseLong(a[2]),uc=Long.parseLong(a[3]);
            if(pc<0||ac<0||uc<0||ac+uc!=pc)return false;
            int[] counts=new int[FREQUENCIES];float[] weights=new float[learned.length];
            for(int i=0;i<FREQUENCIES;i++){
                counts[i]=Integer.parseInt(a[4+i]);if(counts[i]<0||counts[i]>ac)return false;
            }
            for(int i=0;i<weights.length;i++){
                weights[i]=Float.parseFloat(a[4+FREQUENCIES+i]);
                if(!Float.isFinite(weights[i])||Math.abs(weights[i])>1f)return false;
            }
            System.arraycopy(counts,0,observedByFrequency,0,FREQUENCIES);
            System.arraycopy(weights,0,learned,0,weights.length);
            physicalContacts=pc;attributedContacts=ac;unseenContacts=uc;recent.clear();
            return true;
        }catch(RuntimeException bad){return false;}
    }
}
