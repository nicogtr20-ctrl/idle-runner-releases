package com.overmind.zero;

import java.util.ArrayList;

/**
 * Remembers REAL, sustained physical adhesion of the 96-point membrane.
 * Nothing here chooses a destination, commands an adhesion action or opens
 * a gate. Previously successful hand/membrane weights can be restored only
 * after repeated real contact while an adhesion action was independently
 * selected, but no hold could be achieved.
 *
 * This cannot recover skills already overwritten before 0.9.9.
 */
public final class GripSkillBank {
    public static final int MAX=5;
    private static final float SAME=.018f;
    static final class Skill {
        final float[] hand,membrane;
        int heldFrames,opposedFrames;
        float doorMotion;
        Skill(float[] h,float[] m){hand=h.clone();membrane=m.clone();}
        float rank(){return Math.min(60,heldFrames)*.10f+
            Math.min(100,opposedFrames)*2.0f+
            Math.min(100,doorMotion)*5f;}
    }
    public static final class Pair {
        public final float[] hand,membrane;
        Pair(Skill skill){hand=skill.hand.clone();membrane=skill.membrane.clone();}
    }
    private final ArrayList<Skill> entries=new ArrayList<>();
    public int restored=0,archived=0;
    public int count(){return entries.size();}
    private static boolean valid(float[] w,int required){
        if(w==null||w.length!=required)return false;
        for(float x:w)if(!Float.isFinite(x)||Math.abs(x)>5)return false;
        return true;
    }
    private static float diff(float[] a,float[] b){
        float sum=0;
        for(int i=0;i<a.length;i++){float d=a[i]-b[i];sum+=d*d;}
        return (float)Math.sqrt(sum/a.length);
    }
    private static boolean same(Skill s,float[] hand,float[] membrane){
        return diff(s.hand,hand)<SAME&&diff(s.membrane,membrane)<SAME;
    }
    /**
     * A record is allowed ONLY for a held adhesion point, after physics.
     * No score from merely touching the door or merely choosing PRESA ON.
     */
    public boolean observe(float[] hand,float[] membrane,boolean held,
                           boolean opposed,float actualDoorMotion){
        if(!held||!valid(hand,ContactLearning.PARAMS)||
           !valid(membrane,Morphology.PARAMS)||
           !Float.isFinite(actualDoorMotion)||actualDoorMotion<0)return false;
        for(Skill s:entries){
            if(same(s,hand,membrane)){
                s.heldFrames=Math.min(100000,s.heldFrames+1);
                if(opposed)s.opposedFrames=Math.min(100000,s.opposedFrames+1);
                s.doorMotion=Math.min(100000,s.doorMotion+Math.min(10,actualDoorMotion));
                return false;
            }
        }
        Skill candidate=new Skill(hand,membrane);
        candidate.heldFrames=1;
        if(opposed)candidate.opposedFrames=1;
        candidate.doorMotion=actualDoorMotion;
        if(entries.size()==MAX){
            int worst=0;
            for(int i=1;i<entries.size();i++)
                if(entries.get(i).rank()<entries.get(worst).rank())worst=i;
            if(candidate.rank()<=entries.get(worst).rank())return false;
            entries.remove(worst);
        }
        entries.add(candidate);archived++;
        return true;
    }
    /** A controlled alternative: never the same pair. The body only applies
        the returned weights after measured failed contact + chosen adhesion.
        That action remains entirely under SensorimotorMind control. */
    public Pair retrieve(float[] hand,float[] membrane){
        if(!valid(hand,ContactLearning.PARAMS)||!valid(membrane,Morphology.PARAMS))
            return null;
        Skill best=null;
        for(Skill s:entries){
            if(same(s,hand,membrane))continue;
            if(best==null||s.rank()>best.rank())best=s;
        }
        if(best==null)return null;
        restored++;
        return new Pair(best);
    }
    public String encode(){
        StringBuilder b=new StringBuilder("grip1|")
            .append(restored).append('|').append(archived).append('|').append(entries.size());
        for(Skill s:entries){
            b.append('|').append(s.heldFrames).append(',').append(s.opposedFrames)
             .append(',').append(s.doorMotion);
            for(float f:s.hand)b.append(',').append(f);
            for(float f:s.membrane)b.append(',').append(f);
        }
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||!raw.startsWith("grip1|")||raw.length()>27000)return false;
        try{
            String[] parts=raw.split("\\|",-1);
            if(parts.length<4||parts.length>4+MAX)return false;
            int former=Integer.parseInt(parts[1]),saved=Integer.parseInt(parts[2]),
                n=Integer.parseInt(parts[3]);
            if(former<0||saved<0||n<0||n>MAX||parts.length!=4+n)return false;
            ArrayList<Skill> parsed=new ArrayList<>();
            for(int j=0;j<n;j++){
                String[] nums=parts[j+4].split(",",-1);
                if(nums.length!=3+ContactLearning.PARAMS+Morphology.PARAMS)return false;
                int held=Integer.parseInt(nums[0]),opposed=Integer.parseInt(nums[1]);
                float motion=Float.parseFloat(nums[2]);
                if(held<1||held>100000||opposed<0||opposed>held||
                   !Float.isFinite(motion)||motion<0||motion>100000)return false;
                float[] hand=new float[ContactLearning.PARAMS];
                float[] membrane=new float[Morphology.PARAMS];
                for(int i=0;i<hand.length;i++)hand[i]=Float.parseFloat(nums[i+3]);
                for(int i=0;i<membrane.length;i++)
                    membrane[i]=Float.parseFloat(nums[i+3+hand.length]);
                if(!valid(hand,hand.length)||!valid(membrane,membrane.length))return false;
                Skill item=new Skill(hand,membrane);
                item.heldFrames=held;item.opposedFrames=opposed;item.doorMotion=motion;
                parsed.add(item);
            }
            entries.clear();entries.addAll(parsed);
            restored=former;archived=saved;return true;
        }catch(RuntimeException ignored){return false;}
    }
    public static void main(String[] args){
        GripSkillBank bank=new GripSkillBank();
        float[] hand=ContactLearning.virgin(),skin=Morphology.virgin(new java.util.Random(12));
        if(bank.observe(hand,skin,false,false,0)||bank.count()!=0)
            throw new AssertionError("Ordinary touches cannot save gripping skill");
        if(!bank.observe(hand,skin,true,false,0)||bank.count()!=1)
            throw new AssertionError("Real sustained grip not retained");
        bank.observe(hand,skin,true,true,4);
        float[] alternative=skin.clone();alternative[0]+=.4f;
        bank.observe(hand,alternative,true,true,12);
        if(bank.count()!=2)throw new AssertionError("Two physical skills not kept");
        Pair earlier=bank.retrieve(hand,alternative);
        if(earlier==null||earlier.membrane[0]!=skin[0]||bank.restored!=1)
            throw new AssertionError("Physically proven alternative not recovered");
        if(bank.retrieve(hand,alternative)==null)
            throw new AssertionError("Valid alternate cannot vanish");
        GripSkillBank reloaded=new GripSkillBank();
        if(!reloaded.restore(bank.encode())||reloaded.count()!=2||reloaded.restored!=2)
            throw new AssertionError("Skill storage did not roundtrip");
        if(reloaded.restore("grip1|0|1|1|NaN,1,3")||reloaded.count()!=2)
            throw new AssertionError("Broken storage overwrote valid skills");
        System.out.println("ZERO-009.9 actual sustained grip archive and no automatic grip command verified");
    }
}
