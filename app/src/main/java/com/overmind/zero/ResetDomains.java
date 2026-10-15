package com.overmind.zero;

import java.util.*;

/** Explicit learning domains. A selective reset NEVER clears another network.
 * "all" intentionally removes the complete app-local learning profile.
 * The OTA signature/install package lives outside these preferences.
 */
public final class ResetDomains {
    private ResetDomains(){}
    public static final String REQUEST="pending_reset_v091";
    public static final String WORLD="world",CHOICE="choice",MOTOR="motor",
            MANIPULATION="manipulation",MEMBRANE="membrane",EXPLORATION="exploration",
            ROOM="room",EPISODES="episodes",TEMPORAL_SKILLS="temporal_skills",
            PROPRIOCEPTION="proprioception",SPATIAL="spatial",CAUSAL="causal",NAVIGATION="navigation",INTENTION="intention",GATE_METRICS="gate_metrics",ALL="all";
    public static final String[] TYPES={WORLD,CHOICE,MOTOR,MANIPULATION,MEMBRANE,
                                       EXPLORATION,ROOM,EPISODES,TEMPORAL_SKILLS,PROPRIOCEPTION,SPATIAL,CAUSAL,NAVIGATION,INTENTION,GATE_METRICS,ALL};
    public static String[] keys(String domain){
        switch(domain){
            case WORLD:return new String[]{RecurrentCoreV22.KEY,"sensorimotor_model_v09","sensorimotor_diagnostics_v091",UnifiedArbiterV21.KEY,
                "real_energy_credits_v098","real_damage_credits_v098","real_action_credits_v098",
                "outcome_credit_v101","learned_planner_stats_v102",
                "temporal_working_memory_v106",PredictiveWorldV20.KEY,FuturePlannerV20.KEY,MortalityLearning.KEY,UnifiedCognitionV21.KEY};
            case CHOICE:return new String[]{"decision_brain_v05"};
            case MOTOR:return new String[]{"virgin","best","attempts","accepted","original",
                                  "lived_distance_total_v093","lived_distance_record_v093",
                                  "explored_cells_v094","record_net_v094","energy_spent_v094",
                                  "online_accepted_motor_v094","online_rejected_motor_v094",
                                  "motor_recoveries_v097","motor_free_choices_v097",
                                  "motor_skills_v098",MotorDurationV22.KEY};
            case MANIPULATION:return new String[]{"hand_brain_v04","hand_attempts_v04",
                 "hand_successes_v04","objects_displaced_v041","collisions_v041","max_displacement_v041",
                 "online_accepted_hand_v094","online_rejected_hand_v094",GripAffordanceV20.KEY};
            case MEMBRANE:return new String[]{"morph_brain_v07","morph_attempts_v07",
                 "morph_accepted_v07","morph_gate_attempts_v08","morph_gate_success_v08",SymbioteMotorHeadV1.KEY,SymbioteMotorHeadV1.LOCAL_KEY,
                 "online_accepted_membrane_v094","online_rejected_membrane_v094",
                  "grip_skills_v099",CompetenceMemoryV21.KEY};
            case EXPLORATION:return new String[]{"world_knowledge_v03",
                                                  "visual_guided_v098","visual_avoided_v098"};
            case ROOM:return new String[]{"room_success_v08","finite_energy_eaten_v08",
                 "grip_events_v08","gate_travel_v08","scarcity_elapsed_v08",
                 "scarcity_active_v08"};
            case EPISODES:return new String[]{"episodic_memory_v095","physical_replays_v097"};
            case TEMPORAL_SKILLS:return new String[]{"temporal_skills_v107",TransferSkillsV20.KEY};
            case PROPRIOCEPTION:return new String[]{BodyProprioception.KEY,DistributedBodySense.KEY,LocalImpedanceSense.KEY,FluidInteroceptionV1.KEY};
            case SPATIAL:return new String[]{SpatialPerception.KEY,PerceptualAssociationsV20.KEY,ObjectExperienceV20.KEY,ObjectContinuityV21.KEY};
            case CAUSAL:return new String[]{CausalExperiments.KEY,PhysicalConsequenceV20.KEY,CausalSequenceV23.KEY};
            case NAVIGATION:return new String[]{InstrumentalNavigation.KEY};
            case INTENTION:return new String[]{GoalContinuity.KEY};
            case GATE_METRICS:return new String[]{GateBehaviorMetrics.KEY};
            case ALL:return new String[0]; // handled by Editor.clear()
            default:throw new IllegalArgumentException("Unknown reset scope: "+domain);
        }
    }
    public static boolean valid(String key){
        if(key==null)return false;
        for(String k:TYPES)if(k.equals(key))return true;
        return false;
    }
    public static void main(String[] args){
        HashSet<String> seen=new HashSet<>();
        for(String domain:TYPES){
            if(!valid(domain))throw new IllegalStateException("Unrecognized domain");
            for(String k:keys(domain))
                if(!seen.add(k))throw new IllegalStateException("Cross-domain shared key "+k);
        }
        if(seen.size()<24||!Arrays.asList(keys(WORLD)).contains("sensorimotor_model_v09")
           ||!Arrays.asList(keys(MEMBRANE)).contains("morph_brain_v07"))
            throw new IllegalStateException("Missing learned domain key");
        if(valid("typo")||keys(ALL).length!=0)
            throw new IllegalStateException("Unsafe reset command mapping");
        System.out.println("ZERO-009.1 reset domains verified: "+TYPES.length+" actions, "+
                           seen.size()+" independent keys, no overlaps");
    }
}
