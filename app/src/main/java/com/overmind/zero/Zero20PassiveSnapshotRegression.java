package com.overmind.zero;

import java.util.HashMap;
import java.util.Map;

/** Pure-Java QA: report must be explanatory, safe and READ ONLY. */
public final class Zero20PassiveSnapshotRegression {
    private Zero20PassiveSnapshotRegression(){}
    private static void check(boolean pass,String message){
        if(!pass)throw new AssertionError(message);
    }
    @SuppressWarnings("unchecked")
    private static Map<String,Object> section(Map<String,Object> report,String name){
        return (Map<String,Object>)report.get(name);
    }
    public static void main(String[] ignored){
        Map<String,Object> saved=new HashMap<>();
        Map<String,Object> empty=Zero20PassiveSnapshot.fromSaved(saved);
        check("SHADOW_ONLY".equals(empty.get("controlMode")),"must never advertise motor access");
        check(!section(empty,"predictiveWorld").get("valid").equals(Boolean.TRUE),
            "legacy profile must not pretend trained");
        check("NOT_YET_OBSERVED".equals(section(empty,"physicalGrip").get("status")),
            "missing shadow profile must not be labelled corrupt");
        check("NOT_YET_OBSERVED".equals(section(empty,"futurePlanner").get("status")),
            "missing plan must not be labelled motor ready");

        saved.put(PredictiveWorldV20.KEY,new PredictiveWorldV20().encode());
        saved.put(GripAffordanceV20.KEY,new GripAffordanceV20().encode());
        saved.put(PhysicalConsequenceV20.KEY,new PhysicalConsequenceV20().encode());
        saved.put(TransferSkillsV20.KEY,new TransferSkillsV20().encode());
        saved.put(FuturePlannerV20.KEY,new FuturePlannerV20().encode());
        saved.put("best","DO_NOT_TOUCH_MOTOR_WEIGHTS");
        Map<String,Object> valid=Zero20PassiveSnapshot.fromSaved(saved);
        for(String key:new String[]{"predictiveWorld","physicalGrip","physicalConsequences",
                "lifeTransfer","futurePlanner"})
            check(Boolean.TRUE.equals(section(valid,key).get("valid")),
                "missing valid model in section "+key);
        check("UNDER_40_HELDOUT".equals(section(valid,"predictiveWorld").get("confidenceBlocker")),
            "untrained world cannot be called confident");
        check("UNDER_80_HELDOUT".equals(section(valid,"physicalGrip").get("confidenceBlocker")),
            "untrained physical grip cannot be called confident");
        check("UNDER_60_WORLD_HELDOUT".equals(section(valid,"futurePlanner").get("readiness")),
            "untrained planner cannot be called ready");
        check(saved.size()==6&&"DO_NOT_TOUCH_MOTOR_WEIGHTS".equals(saved.get("best")),
            "must not change existing memory or base motor weights");
        check(!valid.toString().contains("DO_NOT_TOUCH_MOTOR_WEIGHTS"),
            "digest must not copy raw motor weights");
        check(!valid.toString().contains("ga21|")&&!valid.toString().contains("pw21|"),
            "digest must not copy raw learning weights");

        saved.put(PredictiveWorldV20.KEY,"pw21|damaged");
        Map<String,Object> broken=Zero20PassiveSnapshot.fromSaved(saved);
        check("INVALID_OR_INCOMPATIBLE_SAVE".equals(
                section(broken,"predictiveWorld").get("status")),
                "malformed saved model must not be reported as genuine learning");
        check("MISSING_WORLD_MODEL".equals(section(broken,"futurePlanner").get("readiness")),
                "corrupt world can never enable planner");
        check("pw21|damaged".equals(saved.get(PredictiveWorldV20.KEY)),
                "read-only digest must not overwrite corrupt originals");
        System.out.println("PASS zero20 passive metrics snapshot, virgin/corrupt saves, no raw weights, no mutations");
    }
}
