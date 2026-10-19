package com.overmind.zero;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * V369: a learning submodule of the ONE V353/GRU brain, not another actor.
 * Learns from optically observable wavelengths and real energy change exactly
 * at material contact. No hidden object class, target position, object ID,
 * color semantics, scripted heading, inferred energy or reward injection.
 *
 * The photosensitive input is the existing 3 frequency x 8 angle retina.
 * Ambiguous close simultaneous wavelength observations are NOT assigned a
 * fictitious cause. Stored values start zero for all virgin/migrated brains.
 */
final class Zero3ContactValenceV369 {
    static final int BANDS=3, BEAMS=8, PERSISTENCE=369;
    private final double[] outcomes=new double[BANDS];
    private final int[] evidence=new int[BANDS];
    private long accepted,ambiguous;
    private static void senses(float[] x,int at,int len){
        if(x==null||x.length<at+len)throw new IllegalArgumentException("V369 sensory shape");
        for(int i=at;i<at+len;i++)if(!Float.isFinite(x[i])||x[i]<0f||x[i]>1f)
            throw new IllegalArgumentException("V369 physical visual range");
    }
    void contact(float[] retina24,float measuredNet){
        senses(retina24,0,BANDS*BEAMS);
        if(!Float.isFinite(measuredNet)||Math.abs(measuredNet)>100f)
            throw new IllegalArgumentException("V369 unphysical energy contact");
        if(measuredNet==0f)return;
        float[] strongest=new float[BANDS];
        for(int c=0;c<BANDS;c++)
            for(int ray=0;ray<BEAMS;ray++)
                strongest[c]=Math.max(strongest[c],retina24[c*BEAMS+ray]);
        int index=0;
        for(int c=1;c<BANDS;c++)
            if(strongest[c]>strongest[index])index=c;
        float runner=0f;
        for(int c=0;c<BANDS;c++)
            if(c!=index)runner=Math.max(runner,strongest[c]);
        // Ambiguous simultaneous stimuli are data we have NOT identified.
        // Brightness ratios are observation quality, never innate valence.
        if(strongest[index]<.25f||strongest[index]<1.15f*runner){
            ambiguous++;return;
        }
        double biologicalValue=Math.max(-1d,Math.min(1d,measuredNet/7d));
        outcomes[index]+=biologicalValue;
        evidence[index]++;
        accepted++;
    }
    /** Directional utility for actor PROPOSALS, not a preselected heading.
     * V369 is inactive until two distinct real contacts establish a band.
     * It consults ONLY current physical photons and learned outcome signs. */
    float utility(float[] observation194,float dx,float dy){
        senses(observation194,20,BANDS*BEAMS);
        if(!Float.isFinite(dx)||!Float.isFinite(dy))
            throw new IllegalArgumentException("V369 actor proposal");
        float total=0f;
        for(int c=0;c<BANDS;c++){
            if(evidence[c]<2)continue;
            float value=(float)(outcomes[c]/evidence[c]);
            if(Math.abs(value)<.1f)continue;
            for(int ray=0;ray<BEAMS;ray++){
                double angle=ray*Math.PI/4d;
                float alignment=(float)(Math.cos(angle)*dx+Math.sin(angle)*dy);
                total+=value*observation194[20+c*BEAMS+ray]*alignment/BEAMS;
            }
        }
        return Math.max(-.25f,Math.min(.25f,total));
    }
    boolean hasReliableVisibleEvidence(float[] observation194){
        senses(observation194,20,BANDS*BEAMS);
        for(int c=0;c<BANDS;c++)if(evidence[c]>=2&&Math.abs(outcomes[c]/evidence[c])>.1)
            for(int ray=0;ray<BEAMS;ray++)
                if(observation194[20+c*BEAMS+ray]>.15f)return true;
        return false;
    }
    int evidence(int frequency){return evidence[frequency];}
    float learnedValue(int frequency){
        return evidence[frequency]==0?0f:(float)(outcomes[frequency]/evidence[frequency]);
    }
    long accepted(){return accepted;}
    long ambiguous(){return ambiguous;}
    void copyTo(Zero3ContactValenceV369 other){
        System.arraycopy(outcomes,0,other.outcomes,0,BANDS);
        System.arraycopy(evidence,0,other.evidence,0,BANDS);
        other.accepted=accepted;other.ambiguous=ambiguous;
    }
    void write(DataOutputStream d)throws IOException{
        d.writeInt(PERSISTENCE);d.writeLong(accepted);d.writeLong(ambiguous);
        for(int c=0;c<BANDS;c++){d.writeInt(evidence[c]);d.writeDouble(outcomes[c]);}
    }
    static Zero3ContactValenceV369 read(DataInputStream d)throws IOException {
        if(d.readInt()!=PERSISTENCE)throw new IOException("V369 contact memory schema");
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.accepted=d.readLong();v.ambiguous=d.readLong();
        if(v.accepted<0||v.accepted>100000000L||
            v.ambiguous<0||v.ambiguous>100000000L)
            throw new IOException("V369 contact count");
        long total=0;
        for(int i=0;i<BANDS;i++){
            v.evidence[i]=d.readInt();v.outcomes[i]=d.readDouble();total+=v.evidence[i];
            if(v.evidence[i]<0||v.evidence[i]>100000000||!Double.isFinite(v.outcomes[i])||
               Math.abs(v.outcomes[i])>v.evidence[i]+1e-5)
                throw new IOException("V369 invalid sensory consequence evidence");
        }
        if(total!=v.accepted)throw new IOException("V369 contact evidence mismatch");
        return v;
    }
}
