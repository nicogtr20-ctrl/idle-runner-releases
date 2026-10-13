package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Zero21 durable, evidence-ranked experimental HAND+MEMBRANE archive.
 * Observes ONLY actually applied motor coefficients + actual opposed tissue
 * holding + physical displacement, already established by existing physics.
 * The policy cannot use or execute these archived pairs: NO automatic grip.
 * Old GripSkillBank remains sole production recovery mechanism.
 */
public final class CompetenceMemoryV21 {
    public static final String KEY="competence_memory_v21";
    public static final int MAX=8;
    private static final String FORMAT="cm21";
    private static final int H=ContactLearning.PARAMS,M=Morphology.PARAMS,S=SensorimotorMind.S;
    public static final class Technique {
        public final float[] hand,membrane,context;
        public final int realMotionFrames,distinctLives;
        public final float observedMotion,contextTrust;
        Technique(float[] h,float[] m,float[] c,int f,int l,float d,float t){
            hand=h.clone();membrane=m.clone();context=c.clone();
            realMotionFrames=f;distinctLives=l;observedMotion=d;contextTrust=t;
        }
    }
    private static final class Sample {
        float[] hand,membrane,context;
        int frames,lives,lastLife;
        float motion,trust;
        Sample(float[] h,float[] m,float[] c,int life,float shift,float q){
            hand=h.clone();membrane=m.clone();context=c.clone();
            frames=1;lives=1;lastLife=life;motion=shift;trust=q;
        }
        float strength(){return Math.min(300,frames)*.10f+
            Math.min(20,lives)*2f+Math.min(100,motion)*.15f;}
    }
    private final ArrayList<Sample> entries=new ArrayList<>();
    private int lifeOrdinal=1;
    private long witnessed=0,discarded=0,unattributed=0;
    private static boolean finite(float[] a,int n,float bound){
        if(a==null||a.length!=n)return false;
        for(float f:a)if(!Float.isFinite(f)||Math.abs(f)>bound)return false;
        return true;
    }
    private static float rms(float[] a,float[] b){
        double v=0;
        for(int i=0;i<a.length;i++)v+=(a[i]-b[i])*(a[i]-b[i]);
        return (float)Math.sqrt(v/a.length);
    }
    private static float distance(float[] a,float[] b){
        float max=0,sum=0;
        for(int i=4;i<S;i++){
            float d=Math.min(2f,Math.abs(a[i]-b[i]));
            sum+=d;max=Math.max(max,d);
        }
        return .65f*sum/(S-4)+.35f*max;
    }
    /**
     * A physically witnessed hold event is a necessary condition, not an
     * object-type label. Track confidence describes perception uncertainty.
     */
    public boolean witnessed(float[] hand,float[] membrane,float[] context,
                             boolean opposedActualHold,float externalMotion,
                             float perceivedTrackTrust){
        if(!opposedActualHold||!Float.isFinite(externalMotion)||
           externalMotion<=.005f||externalMotion>10000f)return false;
        if(!finite(hand,H,5)||!finite(membrane,M,5)||!finite(context,S,1.6f)||
           !Float.isFinite(perceivedTrackTrust)){
            discarded++;return false;
        }
        witnessed++;
        if(perceivedTrackTrust<.4f)unattributed++;
        float trust=Math.max(0f,Math.min(1f,perceivedTrackTrust));
        for(Sample s:entries){
            if(rms(s.hand,hand)<.02f&&rms(s.membrane,membrane)<.02f&&
               distance(s.context,context)<.24f){
                s.frames=Math.min(1000000,s.frames+1);
                if(s.lastLife!=lifeOrdinal){s.lives++;s.lastLife=lifeOrdinal;}
                s.motion=Math.min(100000,s.motion+Math.min(10,externalMotion));
                s.trust+=.08f*(trust-s.trust);
                return false;
            }
        }
        Sample newSkill=new Sample(hand,membrane,context,lifeOrdinal,
                                  externalMotion,trust);
        if(entries.size()>=MAX){
            int least=0;
            for(int i=1;i<entries.size();i++)
                if(entries.get(i).strength()<entries.get(least).strength())least=i;
            if(entries.get(least).strength()>=newSkill.strength()){
                discarded++;return false;
            }
            entries.remove(least);
        }
        entries.add(newSkill);return true;
    }
    public int size(){return entries.size();}
    public long actualWitnesses(){return witnessed;}
    public long unresolvedIdentityWitnesses(){return unattributed;}
    public void newLife(){lifeOrdinal=Math.min(1000000000,lifeOrdinal+1);}
    public void resumeLife(){} // a restart is not evidence of a second life.
    /**
     * Evaluation-only readout. Not used in Android action or motor restore.
     * Requiring >1 actual lives avoids "10,000 frames = 10,000 successes".
     */
    public Technique candidate(float[] sensory){
        if(!finite(sensory,S,1.6f))return null;
        Sample winner=null;float best=Float.NEGATIVE_INFINITY;
        for(Sample s:entries){
            float d=distance(s.context,sensory);
            if(d>.25f||s.frames<6||s.lives<2)continue;
            float utility=s.strength()-d*10f;
            if(utility>best){best=utility;winner=s;}
        }
        if(winner==null)return null;
        return new Technique(winner.hand,winner.membrane,winner.context,
            winner.frames,winner.lives,winner.motion,winner.trust);
    }
    public String encode(){
        StringBuilder b=new StringBuilder(FORMAT).append('|').append(lifeOrdinal)
            .append('|').append(witnessed).append('|').append(discarded)
            .append('|').append(unattributed).append('|').append(entries.size());
        for(Sample s:entries){
            b.append('|').append(s.frames).append(',').append(s.lives)
             .append(',').append(s.lastLife).append(',').append(s.motion)
             .append(',').append(s.trust);
            for(float v:s.hand)b.append(',').append(v);
            for(float v:s.membrane)b.append(',').append(v);
            for(float v:s.context)b.append(',').append(v);
        }
        return b.toString();
    }
    private static long counter(String x){
        long n=Long.parseLong(x);
        if(n<0||n>1000000000L)throw new IllegalArgumentException("bad count");
        return n;
    }
    public boolean restore(String raw){
        if(raw==null||!raw.startsWith(FORMAT+"|")||raw.length()>24000)return false;
        try{
            String[] a=raw.split("\\|",-1);
            if(a.length<6||a.length>6+MAX)return false;
            int ordinal=(int)counter(a[1]);
            long wc=counter(a[2]),dr=counter(a[3]),un=counter(a[4]);
            int n=(int)counter(a[5]);
            if(ordinal<1||n>MAX||a.length!=6+n||un>wc)return false;
            ArrayList<Sample> restored=new ArrayList<>();
            for(int k=0;k<n;k++){
                String[] f=a[k+6].split(",",-1);
                if(f.length!=5+H+M+S)return false;
                int frames=(int)counter(f[0]),lives=(int)counter(f[1]),
                    last=(int)counter(f[2]);
                float motion=Float.parseFloat(f[3]),trust=Float.parseFloat(f[4]);
                if(frames<1||lives<1||lives>frames||last<1||last>ordinal||
                   !Float.isFinite(motion)||motion<0||motion>100000||
                   !Float.isFinite(trust)||trust<0||trust>1)return false;
                float[] h=new float[H],m=new float[M],s=new float[S];int j=5;
                for(int i=0;i<H;i++)h[i]=Float.parseFloat(f[j++]);
                for(int i=0;i<M;i++)m[i]=Float.parseFloat(f[j++]);
                for(int i=0;i<S;i++)s[i]=Float.parseFloat(f[j++]);
                if(!finite(h,H,5)||!finite(m,M,5)||!finite(s,S,1.6f))return false;
                Sample tmp=new Sample(h,m,s,last,motion,trust);
                tmp.frames=frames;tmp.lives=lives;tmp.lastLife=last;
                restored.add(tmp);
            }
            entries.clear();entries.addAll(restored);
            lifeOrdinal=ordinal;witnessed=wc;discarded=dr;unattributed=un;
            return true;
        }catch(RuntimeException ex){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
          "ZERO21 competence SHADOW: stored=%d physically witnessed=%d identity-uncertain=%d discarded=%d only advisory; original GripSkillBank controls recovery",
          entries.size(),witnessed,unattributed,discarded);
    }
}
