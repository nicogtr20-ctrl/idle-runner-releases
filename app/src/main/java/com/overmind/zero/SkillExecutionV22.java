package com.overmind.zero;

/** Closed-loop, transient use of CONSOLIDATED controls. Never overwrites the
 * learner's incumbent weights; re-evaluated every decision, interrupted on
 * lost contact, changed policy, body experiment, or repeated lack of effect.
 */
public final class SkillExecutionV22 {
    private CompetenceMemoryV21.Technique technique;
    private int misses;
    private float cooldown;
    public long starts,successes,aborts;
    public void boundary(CompetenceMemoryV21 archive,float[] sensed,int action,
                         float effect,boolean experiment){
        if(technique!=null){if(effect>.015f){successes++;misses=0;}else misses++;}
        if(experiment||sensed==null||sensed[4]<.02f||action<8||misses>=3){
            if(technique!=null){aborts++;cooldown=4;}
            technique=null;misses=0;
        }
        if(cooldown>0){cooldown-=.65f;return;}
        CompetenceMemoryV21.Technique candidate=experiment||action<8||sensed==null||sensed[4]<.02f?
            null:archive.candidate(sensed);
        if(candidate==null){technique=null;return;}
        if(technique==null)starts++;
        technique=candidate;
    }
    public float[] hand(float[] baseline,boolean adhesion,boolean touching,boolean experiment){
        return technique!=null&&adhesion&&touching&&!experiment?technique.hand:baseline;
    }
    public float[] membrane(float[] baseline,boolean adhesion,boolean touching,boolean experiment){
        return technique!=null&&adhesion&&touching&&!experiment?technique.membrane:baseline;
    }
    public void resetLife(){technique=null;misses=0;cooldown=0;}
}
