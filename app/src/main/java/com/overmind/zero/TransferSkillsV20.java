package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO 2.0 gate B: observational discovery of action motifs preceding
 * REAL energetic outcomes. No invented goal, target, object identity, contact
 * energy, or autonomous motor replay. Pure SHADOW: does not control actions.
 *
 * Extends the original 2..8 action TemporalSkillBank with bounded 3..12 step
 * transferable candidates; repeated evidence MUST span distinct physical
 * lives, with completely separate holdout lives. Correlation is NOT causality.
 */
public final class TransferSkillsV20 {
    public static final String KEY="transfer_skills_v20";
    static final int MIN_LEN=3,MAX_LEN=12,MAX_SKILLS=32,CTX=8,A=16;
    private static final float MATCH=.28f;
    private static final class Motif {
        final int[] token=new int[MAX_LEN],example=new int[MAX_LEN];
        final float[] context=new float[CTX];
        int length,trainedLives,heldoutLives,harmfulLives;
        long lastTrain=-1,lastHeldout=-1,lastHarm=-1;
        float positiveEnergy;
        int confidencePermille(){
            if(trainedLives<2||heldoutLives<1||harmfulLives>trainedLives+heldoutLives)
                return 0;
            float support=Math.min(1f,trainedLives/5f);
            float validated=Math.min(1f,heldoutLives/3f);
            float safety=(trainedLives+heldoutLives)/
                (float)(trainedLives+heldoutLives+harmfulLives*2);
            return Math.round(1000*support*validated*safety);
        }
    }
    public static final class Advice {
        public final int firstAction,length,confidencePermille;
        Advice(int action,int length,int trust){
            firstAction=action;this.length=length;confidencePermille=trust;
        }
    }
    private final ArrayList<Motif> skills=new ArrayList<>();
    private final int[] sequence=new int[MAX_LEN];
    private final float[][] contexts=new float[MAX_LEN][CTX];
    private int head=0,size=0;
    private long life=0,decisions=0,goodEvents=0,badEvents=0,candidateEvents=0;
    private int retired=0;
    static int reduced(int action){return ((action&7)>>1)+((action>>>3)&1)*4;}
    private static boolean valid(float[] senses){
        if(senses==null||senses.length!=SensorimotorMind.S)return false;
        for(float v:senses)if(!Float.isFinite(v)||Math.abs(v)>1.6f)return false;
        return true;
    }
    private static void abstractSenses(float[] sensed,float[] out){
        // Context is a derived real sensory representation, never world state.
        // Four ray sectors + four spectral summary sectors.
        for(int i=0;i<4;i++){
            out[i]=(sensed[6+i*2]+sensed[7+i*2])*.5f;
            out[i+4]=(sensed[14+i*3]+sensed[15+i*3]+sensed[16+i*3])/3f;
        }
    }
    private static float difference(float[] a,float[] b){
        float v=0;
        for(int i=0;i<CTX;i++)v+=Math.abs(a[i]-b[i]);
        return v/CTX;
    }
    public void resetLife(){
        life++;
        head=0;size=0;
    }
    /** Restored weights survive app restart, but incomplete action history
     * is never assigned an imagined completed energy outcome. */
    public void resumeLife(){head=0;size=0;}
    public void chosen(int action,float[] sensed){
        if(action<0||action>=A||!valid(sensed))return;
        sequence[head]=action;
        abstractSenses(sensed,contexts[head]);
        head=(head+1)%MAX_LEN;
        size=Math.min(MAX_LEN,size+1);
        decisions++;
    }
    private int previousIndex(int age){return (head-1-age+MAX_LEN)%MAX_LEN;}
    private boolean match(Motif m,int len){
        if(m.length!=len)return false;
        int start=previousIndex(len-1);
        if(difference(m.context,contexts[start])>MATCH)return false;
        for(int j=0;j<len;j++)
            if(m.token[j]!=reduced(sequence[previousIndex(len-1-j)]))
                return false;
        return true;
    }
    /** Genuine actual clamped energy change, never locomotion or frame delta. */
    public void energyOutcome(float actualEnergyDelta){
        if(!Float.isFinite(actualEnergyDelta)||Math.abs(actualEnergyDelta)<.1f)
            return;
        if(actualEnergyDelta>0)goodEvents++;else badEvents++;
        if(size<MIN_LEN)return;
        // Holdout is by WHOLE LIFE: discoveries cannot train on the life
        // later used to evaluate their reliability.
        boolean heldout=(life%4==0);
        for(int len=MIN_LEN;len<=size;len++){
            Motif found=null;
            for(Motif existing:skills)
                if(match(existing,len)){found=existing;break;}
            if(actualEnergyDelta<0){
                if(found!=null&&found.lastHarm!=life){
                    found.harmfulLives++;found.lastHarm=life;
                }
                continue;
            }
            if(found==null){
                if(heldout)continue; // no candidate creation from holdout
                found=new Motif();
                found.length=len;
                for(int i=0;i<len;i++){
                    int a=sequence[previousIndex(len-1-i)];
                    found.token[i]=reduced(a);found.example[i]=a;
                }
                System.arraycopy(contexts[previousIndex(len-1)],0,
                    found.context,0,CTX);
                if(skills.size()==MAX_SKILLS){
                    int worst=-1,lowest=Integer.MAX_VALUE;
                    for(int i=0;i<skills.size();i++){
                        Motif candidate=skills.get(i);
                        int importance=candidate.trainedLives*4+
                            candidate.heldoutLives*7-
                            candidate.harmfulLives*4;
                        if(importance<lowest){lowest=importance;worst=i;}
                    }
                    if(worst>=0&&lowest>=4)continue; // never evict proven
                    skills.remove(worst);
                    retired++;
                }
                skills.add(found);
            }
            if(heldout){
                if(found.lastHeldout!=life){
                    found.heldoutLives++;found.lastHeldout=life;
                }
            }else if(found.lastTrain!=life){
                found.trainedLives++;found.lastTrain=life;
                found.positiveEnergy+=Math.min(100f,actualEnergyDelta);
            }
            candidateEvents++;
        }
    }
    /** Read-only curiosity/planning research output; ZERO does NOT follow it. */
    public Advice observeSuggestion(float[] sensed){
        if(!valid(sensed))return null;
        float[] now=new float[CTX];abstractSenses(sensed,now);
        Motif best=null;float score=0;
        for(Motif m:skills){
            int conf=m.confidencePermille();
            if(conf==0)continue;
            float closeness=difference(now,m.context);
            if(closeness>MATCH)continue;
            float benefit=conf*(1f-closeness/MATCH);
            if(benefit>score){score=benefit;best=m;}
        }
        return best==null?null:new Advice(best.example[0],best.length,
                                          best.confidencePermille());
    }
    public int candidates(){return skills.size();}
    public int confirmed(){
        int n=0;for(Motif m:skills)if(m.confidencePermille()>0)n++;
        return n;
    }
    public int longestConfirmed(){
        int n=0;for(Motif m:skills)
            if(m.confidencePermille()>0)n=Math.max(n,m.length);
        return n;
    }
    public long lives(){return life;}
    public long decisions(){return decisions;}
    public long realGoodEvents(){return goodEvents;}
    public long realBadEvents(){return badEvents;}
    public String encode(){
        StringBuilder b=new StringBuilder("tv20|").append(life).append('|')
            .append(decisions).append('|').append(goodEvents).append('|')
            .append(badEvents).append('|').append(candidateEvents).append('|')
            .append(retired).append('|').append(skills.size());
        for(Motif m:skills){
            b.append('|').append(m.length).append(',').append(m.trainedLives)
                .append(',').append(m.heldoutLives).append(',')
                .append(m.harmfulLives).append(',').append(m.positiveEnergy)
                .append(',').append(m.lastTrain).append(',')
                .append(m.lastHeldout).append(',').append(m.lastHarm);
            for(int i=0;i<m.length;i++)
                b.append(',').append(m.token[i]).append(',').append(m.example[i]);
            for(float c:m.context)b.append(',').append(c);
        }
        return b.toString();
    }
    /** Transactional restore: legacy profiles simply start empty. */
    public boolean restore(String raw){
        if(raw==null||raw.length()>110000||!raw.startsWith("tv20|"))return false;
        try{
            String[] p=raw.split("\\|",-1);
            if(p.length<8||!"tv20".equals(p[0]))return false;
            long lives=Long.parseLong(p[1]),steps=Long.parseLong(p[2]),
                good=Long.parseLong(p[3]),bad=Long.parseLong(p[4]),
                events=Long.parseLong(p[5]);
            int evictions=Integer.parseInt(p[6]),n=Integer.parseInt(p[7]);
            if(lives<0||steps<0||good<0||bad<0||events<0||evictions<0||
                n<0||n>MAX_SKILLS||p.length!=8+n)return false;
            ArrayList<Motif> parsed=new ArrayList<>();
            for(int i=0;i<n;i++){
                String[] parts=p[i+8].split(",",-1);
                Motif m=new Motif();
                if(parts.length<8)return false;
                m.length=Integer.parseInt(parts[0]);
                if(m.length<MIN_LEN||m.length>MAX_LEN||
                    parts.length!=8+2*m.length+CTX)return false;
                m.trainedLives=Integer.parseInt(parts[1]);
                m.heldoutLives=Integer.parseInt(parts[2]);
                m.harmfulLives=Integer.parseInt(parts[3]);
                m.positiveEnergy=Float.parseFloat(parts[4]);
                m.lastTrain=Long.parseLong(parts[5]);
                m.lastHeldout=Long.parseLong(parts[6]);
                m.lastHarm=Long.parseLong(parts[7]);
                if(m.trainedLives<0||m.heldoutLives<0||m.harmfulLives<0||
                    m.trainedLives>lives||m.heldoutLives>lives||
                    m.harmfulLives>lives||!Float.isFinite(m.positiveEnergy)||
                    m.positiveEnergy<0||m.positiveEnergy>lives*101f||
                    m.lastTrain>=lives+1||m.lastHeldout>=lives+1||
                    m.lastHarm>=lives+1||m.lastTrain< -1||
                    m.lastHeldout< -1||m.lastHarm< -1)return false;
                int k=8;
                for(int j=0;j<m.length;j++){
                    m.token[j]=Integer.parseInt(parts[k++]);
                    m.example[j]=Integer.parseInt(parts[k++]);
                    if(m.token[j]<0||m.token[j]>=8||m.example[j]<0||
                        m.example[j]>=A||m.token[j]!=reduced(m.example[j]))
                        return false;
                }
                for(int j=0;j<CTX;j++){
                    m.context[j]=Float.parseFloat(parts[k++]);
                    if(!Float.isFinite(m.context[j])||
                        Math.abs(m.context[j])>1.6f)return false;
                }
                parsed.add(m);
            }
            skills.clear();skills.addAll(parsed);
            life=lives;decisions=steps;goodEvents=good;badEvents=bad;
            candidateEvents=events;retired=evictions;
            resumeLife();return true;
        }catch(RuntimeException corrupt){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
            "ZERO20 B shadow: %d/%d confirmed, longest %d actions, "+
            "%d actual positive and %d negative energy contacts in %d lives",
            confirmed(),skills.size(),longestConfirmed(),goodEvents,badEvents,life);
    }
}
