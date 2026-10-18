package com.overmind.zero;

import java.util.*;
import java.nio.file.*;

/** Actual variable-duration gel execution, damage/death interruption and atomic resume. */
public final class Zero3VariableClockV352Regression {
    private static void yes(boolean ok,String why){if(!ok)throw new AssertionError("V352 "+why);}
    static Zero3RuntimeV325 born(long episode,float fuel,boolean danger,boolean food)throws Exception{
        MotorBrain.Body b=new MotorBrain.Body();Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(b,skin);
        for(int i=0;i<96;i++)gel.step(b,skin,new float[64],.025f);
        ScarcityLab room=new ScarcityLab(b.cx+180f,b.cy);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();world.add(room.food);
        if(danger)world.add(new UnknownWorld.Entity(skin.x[24],skin.y[24],1,1));
        if(food)world.add(new UnknownWorld.Entity(skin.x[24],skin.y[24],0,0));
        Zero3RuntimeV325 r=Zero3RuntimeV325.newborn(episode,0x352AB123L,b,skin,gel,world,room,fuel,0f);
        r.enableVariableResearchV352();r.enableSegmented();return r;
    }
    private static void advance(Zero3RuntimeV325 r,int n)throws Exception{
        for(int i=0;i<n;i++)r.advanceOneDecision();
    }
    public static void main(String[] args)throws Exception{
        Set<Integer> durations=new HashSet<>();Path dir=Files.createTempDirectory("zero352-resume-");
        byte[] reference,energy;
        try(Zero3RuntimeV325 full=born(352001,70f,false,false)){
            advance(full,96);Zero3SegmentedArchiveV328.Bundle b=full.segmentedSnapshot();
            for(Zero3FullCausalExperienceV322B e:b.experiences()){
                int steps=(int)(e.postTick-e.preTick);durations.add(steps);
                yes(steps>=10&&steps<=26&&steps==e.plannedSteps&&e.interruptionReason==0,"actual clock differs from intention");
            }
            yes(durations.size()>=2,"duration remains fixed");
            reference=Zero3OrganismStoreV322.encode(b.organism);energy=b.survival.encoded();
        }
        try(Zero3RuntimeV325 prefix=born(352001,70f,false,false)){
            advance(prefix,48);prefix.saveSegmented(dir);
        }
        try(Zero3RuntimeV325 restored=Zero3RuntimeV325.loadSegmented(dir)){
            advance(restored,48);Zero3SegmentedArchiveV328.Bundle b=restored.segmentedSnapshot();
            yes(b.organism.variableClock()&&b.survival.variableClock,"clock configuration lost");
            yes(Arrays.equals(reference,Zero3OrganismStoreV322.encode(b.organism))&&
                Arrays.equals(energy,b.survival.encoded()),"brain/body/energy not identical after variable-clock restart");
        }
        try(Zero3RuntimeV325 harmed=born(352002,70f,true,false)){
            harmed.advanceOneDecision();Zero3FullCausalExperienceV322B e=harmed.segmentedSnapshot().experiences().get(0);
            yes(e.interruptionReason==1&&e.postTick-e.preTick==1&&harmed.display().lastEnergyDamage>0f&&!harmed.ended(),"real damage did not interrupt promptly");
        }
        Path dead=Files.createTempDirectory("zero352-dead-");
        try(Zero3RuntimeV325 ended=born(352003,5f,true,true)){
            ended.advanceOneDecision();Zero3FullCausalExperienceV322B e=ended.segmentedSnapshot().experiences().get(0);
            yes(ended.ended()&&ended.energy()==0f&&e.interruptionReason==2&&e.postTick-e.preTick==1,
                "lethal contact did not terminate real interval");
            yes(ended.display().lastEnergyGained==0f,"later resource resurrected dead organism");
            ended.saveSegmented(dead);
        }
        try(Zero3RuntimeV325 ended=Zero3RuntimeV325.loadSegmented(dead)){
            yes(ended.ended()&&ended.decisions()==1,"restore resurrected physical death");
            boolean rejected=false;try{ended.advanceOneDecision();}catch(IllegalStateException correct){rejected=true;}
            yes(rejected,"postmortem neural credit");
        }
        // Censored duration must not receive a gradient, even though actual energy trains the critic.
        Zero3RecurrentBrain actor=new Zero3RecurrentBrain(194,true);
        float[] observation=new float[194];observation[188]=.1f;
        float[] weights=actor.parameterSnapshot();
        actor.choose(observation,352004,0,0);
        actor.consequenceObservedV352(observation,-1f,false,.025f,true,true);
        int durationOffset=3*64*(194+64+1)+4*65;
        float[] learned=actor.parameterSnapshot();
        for(int i=durationOffset;i<durationOffset+65;i++)yes(weights[i]==learned[i],"unexecuted duration received credit");
        System.out.println("V352 ACTUAL_VARIABLE_DURATION=PASS choices="+durations+
            " damageInterrupt=1_step deathInterrupt=1_step noResurrection=true censoredDurationCredit=false wholeLifeRestart=bitwise legacyClockUnchanged=true NO_G4_SCIENTIFIC_PROMOTION");
    }
}
