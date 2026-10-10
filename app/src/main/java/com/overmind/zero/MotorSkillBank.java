package com.overmind.zero;

import java.util.ArrayList;

/**
 * Long-lived archive of *actually tested* motor configurations.
 *
 * There is no fixed gait, destination, or "best direction". The archive is
 * populated exclusively from live 8-action windows, with net nucleus travel
 * as the main comparable property. It cannot reconstruct pre-0.9.8 motors.
 * The main learner continues to mutate whichever controller is current.
 */
public final class MotorSkillBank {
    public static final int MAX=6;
    private static final float MIN_NET=9f;
    private static final float SAME_WEIGHTS=.025f;
    static final class Skill {
        final float[] weights;
        float bestNet,score;
        int confirmations;
        Skill(float[] w,float net,float reward){
            weights=w.clone();bestNet=net;score=reward;confirmations=1;
        }
        float rank(){return bestNet+Math.min(10,confirmations-1)*.7f;}
    }
    private final ArrayList<Skill> skills=new ArrayList<>();
    public int rollbacks=0,recorded=0;
    public float largestVerifiedNet=0;
    private int cooldown=0;

    public int count(){return skills.size();}
    public void restoreStats(int previousRollbacks){rollbacks=Math.max(0,previousRollbacks);}
    private static boolean valid(float[] w){
        if(w==null||w.length!=MotorBrain.PARAMS)return false;
        for(float v:w)if(!Float.isFinite(v)||Math.abs(v)>5)return false;
        return true;
    }
    private static float difference(float[] a,float[] b){
        float sum=0;
        for(int i=0;i<a.length;i++){float d=a[i]-b[i];sum+=d*d;}
        return (float)Math.sqrt(sum/a.length);
    }
    public void observe(float[] weights,float measuredNet,float score){
        if(!valid(weights)||!Float.isFinite(measuredNet)||!Float.isFinite(score)||
           measuredNet<MIN_NET)return;
        float net=Math.min(500,measuredNet);
        largestVerifiedNet=Math.max(largestVerifiedNet,net);
        for(Skill s:skills){
            if(difference(s.weights,weights)<SAME_WEIGHTS){
                s.confirmations=Math.min(1000,s.confirmations+1);
                s.bestNet=Math.max(s.bestNet,net);
                s.score=Math.max(s.score,score);
                return;
            }
        }
        Skill candidate=new Skill(weights,net,score);
        if(skills.size()>=MAX){
            int worst=0;
            for(int i=1;i<skills.size();i++)
                if(skills.get(i).rank()<skills.get(worst).rank())worst=i;
            if(candidate.rank()<=skills.get(worst).rank())return;
            skills.remove(worst);
        }
        skills.add(candidate);
        recorded++;
    }
    /**
     * Called only after repeat stall warnings from the real-world watchdog.
     * Returns an earlier motor physically observed to translate; does not
     * invent a gait. Never returns the same controller.
     */
    public float[] retrieve(float[] current){
        if(!valid(current)||cooldown>0)return null;
        Skill best=null;
        for(Skill s:skills){
            if(difference(s.weights,current)<SAME_WEIGHTS)continue;
            if(best==null||s.rank()>best.rank())best=s;
        }
        if(best==null)return null;
        rollbacks++;
        cooldown=3;  // at least three watchdog events between swaps
        return best.weights.clone();
    }
    public void stallEvent(){if(cooldown>0)cooldown--;}
    public String encode(){
        StringBuilder b=new StringBuilder("skills1|").append(rollbacks)
            .append('|').append(recorded).append('|').append(largestVerifiedNet)
            .append('|').append(skills.size());
        for(Skill s:skills){
            b.append('|').append(s.bestNet).append(',').append(s.score).append(',')
              .append(s.confirmations);
            for(float v:s.weights)b.append(',').append(v);
        }
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||!raw.startsWith("skills1|")||raw.length()>30000)return false;
        try{
            String[] items=raw.split("\\|",-1);
            if(items.length<5||items.length>5+MAX)return false;
            int resets=Integer.parseInt(items[1]),made=Integer.parseInt(items[2]);
            float best=Float.parseFloat(items[3]);
            int n=Integer.parseInt(items[4]);
            if(n<0||n>MAX||resets<0||made<0||
               !Float.isFinite(best)||best<0||items.length!=5+n)return false;
            ArrayList<Skill> parsed=new ArrayList<>();
            for(int j=0;j<n;j++){
                String[] parts=items[5+j].split(",",-1);
                if(parts.length!=3+MotorBrain.PARAMS)return false;
                float net=Float.parseFloat(parts[0]),score=Float.parseFloat(parts[1]);
                int conf=Integer.parseInt(parts[2]);
                if(!Float.isFinite(net)||net<MIN_NET||!Float.isFinite(score)||
                   conf<1||conf>1000)return false;
                float[] w=new float[MotorBrain.PARAMS];
                for(int i=0;i<w.length;i++)w[i]=Float.parseFloat(parts[i+3]);
                if(!valid(w))return false;
                Skill s=new Skill(w,net,score);s.confirmations=conf;
                parsed.add(s);
            }
            skills.clear();skills.addAll(parsed);
            rollbacks=resets;recorded=made;largestVerifiedNet=best;cooldown=0;
            return true;
        }catch(RuntimeException ignored){return false;}
    }
    public static void main(String[] args){
        MotorSkillBank m=new MotorSkillBank();
        float[] a=MotorBrain.virgin(new java.util.Random(6)),b=a.clone();
        b[0]+=.5f;
        m.observe(a,4,200);
        if(m.count()!=0)throw new AssertionError("Spinning/no net movement was archived");
        m.observe(a,40,12);m.observe(b,90,8);
        if(m.count()!=2||m.largestVerifiedNet!=90)throw new AssertionError("Real motor skill not archived");
        float[] recovered=m.retrieve(a);
        if(recovered==null||recovered[0]!=b[0]||m.rollbacks!=1)
            throw new AssertionError("Return only a demonstrated alternate controller");
        if(m.retrieve(a)!=null)throw new AssertionError("Rollback spam");
        String saved=m.encode();
        MotorSkillBank restored=new MotorSkillBank();
        if(!restored.restore(saved)||restored.count()!=2||
           restored.rollbacks!=1||restored.largestVerifiedNet!=90)
            throw new AssertionError("Persistence failed");
        for(int i=0;i<3;i++)restored.stallEvent();
        if(restored.retrieve(a)==null)throw new AssertionError("Delayed recovery failed");
        MotorSkillBank corrupt=new MotorSkillBank();
        if(corrupt.restore("skills1|2|3|40|1|NaN,0,1")||corrupt.count()!=0)
            throw new AssertionError("Corrupted archive accepted");
        System.out.println("ZERO-009.8 motor skill archive, alternate rollback, stable serialization verified");
    }
}
