package com.overmind.zero;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Deterministic 0.10.8 regression: two newly added learning domains must
 * round-trip through exactly the complete typed profile used by named saves.
 * Android GameActivity's load/save wiring is gated separately in CI.
 */
public final class TemporalPersistenceRegression {
    private TemporalPersistenceRegression(){}
    private static void ensure(boolean ok,String message){
        if(!ok)throw new AssertionError(message);
    }
    private static float[] context(float v){
        float[] a=new float[TemporalWorkingMemory.HIDDEN];
        Arrays.fill(a,v);
        return a;
    }
    private static TemporalWorkingMemory trainedMemory(){
        TemporalWorkingMemory m=new TemporalWorkingMemory();
        float[] before=new float[SensorimotorMind.S];
        float[] after=new float[SensorimotorMind.S];
        after[9]=.6f;after[12]=.35f;
        for(int i=0;i<12;i++){
            m.chosen(2);
            m.observe(before,2,after,.21f,.65f,true,false);
        }
        ensure(m.transitions==12,"No real transitions in temporal test");
        ensure(m.encode().startsWith("tw1|"),"Wrong temporal serialized format");
        return m;
    }
    private static TemporalSkillBank trainedSkills(){
        TemporalSkillBank skills=new TemporalSkillBank();
        for(int life=0;life<3;life++){
            skills.resetLife();
            skills.chosen(context(.15f),3);
            skills.chosen(context(.18f),11);
            skills.physicalOutcome(12f);
        }
        ensure(skills.consolidated()>0,"Repeated successful sequence not consolidated");
        return skills;
    }
    private static Map<String,Object> snapshot(TemporalWorkingMemory memory,
                                                TemporalSkillBank skills){
        Map<String,Object> m=new HashMap<>();
        m.put("best","0,0,0"); // unrelated original motor bytes are preserved
        m.put("episodic_memory_v095","em3;historical");
        m.put("life_statistics_v104","ls2|historical");
        m.put("temporal_working_memory_v106",memory.encode());
        m.put("temporal_skills_v107",skills.encode());
        return m;
    }
    private static void verify(Map<String,Object> profile,
                               TemporalWorkingMemory memory,TemporalSkillBank skills){
        TemporalWorkingMemory loadedMemory=new TemporalWorkingMemory();
        TemporalSkillBank loadedSkills=new TemporalSkillBank();
        ensure(loadedMemory.restore((String)profile.get("temporal_working_memory_v106")),
            "App-reloaded temporal memory did not decode");
        ensure(loadedSkills.restore((String)profile.get("temporal_skills_v107")),
            "App-reloaded skill bank did not decode");
        ensure(loadedMemory.transitions==memory.transitions,
            "Learned temporal transitions lost on app restart");
        ensure(Arrays.equals(loadedMemory.contextCopy(),memory.contextCopy()),
            "Recurrent hidden temporal context lost on restart");
        ensure(loadedMemory.encode().equals(memory.encode()),
            "Temporal predictor/value learned weights not preserved");
        ensure(loadedSkills.encode().equals(skills.encode()),
            "Consolidated action sequences or their outcomes not preserved");
        loadedMemory.resumeAction(2);
        loadedSkills.resumeLife();
        ensure(loadedSkills.consolidated()==skills.consolidated()&&
               loadedSkills.advice(context(.15f),.5f)!=null,
            "Stored skill not recalled in matching context");
        loadedMemory.resetLife();loadedSkills.resetLife();
        ensure(loadedMemory.transitions==memory.transitions&&
               loadedSkills.consolidated()==skills.consolidated(),
            "Death destroyed long-term temporal learning");
    }
    public static void main(String[] args){
        TemporalWorkingMemory memory=trainedMemory();
        TemporalSkillBank skills=trainedSkills();
        Map<String,Object> current=snapshot(memory,skills);
        // Saving in a named slot uses this existing codec over ALL preferences,
        // including new temporal fields. Never change the slot format.
        byte[] frozen=SaveProfileCodec.encode(current);
        Map<String,Object> installed=SaveProfileCodec.decode(frozen);
        ensure(current.equals(installed),"Named slot lost or modified profile values");
        ensure("em3;historical".equals(installed.get("episodic_memory_v095")),
            "Old episodic memory corrupted by new state");
        verify(installed,memory,skills);

        // A different ZERO is independent. Loading the first slot must undo
        // all changes made by the second ZERO to the active working profile.
        TemporalWorkingMemory secondMemory=new TemporalWorkingMemory();
        TemporalSkillBank secondSkills=new TemporalSkillBank();
        Map<String,Object> second=snapshot(secondMemory,secondSkills);
        byte[] savedSecond=SaveProfileCodec.encode(second);
        Map<String,Object> afterSecond=SaveProfileCodec.decode(savedSecond);
        ensure(!current.get("temporal_working_memory_v106")
                .equals(afterSecond.get("temporal_working_memory_v106")),
            "Different ZERO memories unexpectedly mixed");
        ensure(!current.get("temporal_skills_v107")
                .equals(afterSecond.get("temporal_skills_v107")),
            "Different ZERO skill banks unexpectedly mixed");
        verify(SaveProfileCodec.decode(frozen),memory,skills);
        ensure(frozen.length<SaveProfileCodec.MAX_BYTES,
            "New learned weights exceed legacy safe profile capacity");
        ensure(new TemporalWorkingMemory().encode().startsWith("tw1|")&&
               new TemporalSkillBank().encode().startsWith("ts1|"),
            "Virgin ZERO cannot start new temporal memories");
        System.out.println("ZERO-0108 PASS: learned temporal weights + hidden state + skill chunks "
            +"survive app-style persistence, death and 12-slot-compatible profile switching");
    }
}
