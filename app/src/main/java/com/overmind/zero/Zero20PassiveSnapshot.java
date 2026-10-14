package com.overmind.zero;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Explicit-share-only research digest, derived from a COPY of saved current
 * working-profile values. No reads of named slots, mutation, policy advice,
 * Android API, raw 35ms frame logs or extra game-thread work.
 */
public final class Zero20PassiveSnapshot {
    private Zero20PassiveSnapshot(){}

    private static String str(Map<String,?> values,String key){
        Object value=values==null?null:values.get(key);
        return value instanceof String?(String)value:"";
    }
    private static void finite(Map<String,Object> out,String key,float n){
        if(Float.isFinite(n))out.put(key,n);
    }
    private static Map<String,Object> section(String encoded,boolean valid){
        Map<String,Object> item=new LinkedHashMap<>();
        item.put("saved",!encoded.isEmpty());
        item.put("valid",valid);
        item.put("status",encoded.isEmpty()?"NOT_YET_OBSERVED":
            valid?"READY_TO_INSPECT":"INVALID_OR_INCOMPATIBLE_SAVE");
        return item;
    }
    public static Map<String,Object> fromSaved(Map<String,?> values){
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("schema","zero20-passive-observer-digest-v1");
        result.put("controlMode","ZERO22_PRIMARY_WITH_V20_V21_PASSIVE_EVIDENCE");
        result.put("dataOrigin","SAVED_WORKING_PROFILE_AT_EXPLICIT_SHARE");
        result.put("liveBehaviorChange",true);
        result.put("note","These fields summarize stored observers, not proof of higher intelligence; no raw 35ms frames.");

        String pwRaw=str(values,PredictiveWorldV20.KEY);
        PredictiveWorldV20 pw=new PredictiveWorldV20();
        boolean pwValid=pw.restore(pwRaw);
        Map<String,Object> world=section(pwRaw,pwValid);
        if(pwValid){
            world.put("observedTransitions",pw.transitions());
            world.put("trainedTransitions",pw.trained());
            world.put("heldoutTransitions",pw.validations());
            world.put("invalidSamples",pw.invalidSamples());
            finite(world,"heldoutError",pw.heldoutError());
            finite(world,"unchangedError",pw.baselineError());
            finite(world,"confidence",pw.confidence());
            if(pw.validations()<40)world.put("confidenceBlocker","UNDER_40_HELDOUT");
            else if(!Float.isFinite(pw.heldoutError())||!Float.isFinite(pw.baselineError()))
                world.put("confidenceBlocker","MISSING_VALIDATION_ERRORS");
            else if(pw.baselineError()<.025f)
                world.put("confidenceBlocker","BASELINE_CHANGE_TOO_SMALL");
            else if(pw.heldoutError()>=pw.baselineError()*.92f)
                world.put("confidenceBlocker","LESS_THAN_8_PERCENT_VALIDATED_GAIN");
            else world.put("confidenceBlocker","NONE");
        }
        result.put("predictiveWorld",world);

        String gpRaw=str(values,GripAffordanceV20.KEY);
        GripAffordanceV20 gp=new GripAffordanceV20();
        boolean gpValid=gp.restore(gpRaw);
        Map<String,Object> grip=section(gpRaw,gpValid);
        if(gpValid){
            grip.put("trainedIntervals",gp.trained());
            grip.put("heldoutIntervals",gp.validations());
            grip.put("physicalDisplacements",gp.motionEvents());
            grip.put("simultaneousGripFrames",gp.physicsHeldFrames());
            grip.put("distributedGripFrames",gp.physicsSpreadFrames());
            grip.put("genuineReleasedSpreadGrips",gp.fleetingSpreadIntervals());
            grip.put("contextsWithAlternativeActions",gp.contextsWithAlternatives());
            grip.put("invalidSamples",gp.invalid());
            finite(grip,"heldoutBrierError",gp.heldoutError());
            finite(grip,"baselineBrierError",gp.baselineError());
            finite(grip,"confidence",gp.confidence());
            if(gp.validations()<80)grip.put("confidenceBlocker","UNDER_80_HELDOUT");
            else if(gp.motionEvents()<12)grip.put("confidenceBlocker","UNDER_12_PHYSICAL_EVENTS");
            else if(gp.contextsWithAlternatives()<1)
                grip.put("confidenceBlocker","NO_ACTION_ALTERNATIVE_WITH_EVIDENCE");
            else if(!Float.isFinite(gp.heldoutError())||!Float.isFinite(gp.baselineError()))
                grip.put("confidenceBlocker","MISSING_VALIDATION_ERRORS");
            else if(gp.baselineError()<.035f)
                grip.put("confidenceBlocker","BASELINE_BRIER_BELOW_0_035");
            else if(gp.heldoutError()>=gp.baselineError()*.94f)
                grip.put("confidenceBlocker","LESS_THAN_6_PERCENT_VALIDATED_GAIN");
            else grip.put("confidenceBlocker","NONE");
        }
        result.put("physicalGrip",grip);

        String pcRaw=str(values,PhysicalConsequenceV20.KEY);
        PhysicalConsequenceV20 pc=new PhysicalConsequenceV20();
        boolean pcValid=pc.restore(pcRaw);
        Map<String,Object> physical=section(pcRaw,pcValid);
        if(pcValid){
            physical.put("trainedIntervals",pc.trained());
            physical.put("heldoutIntervals",pc.validations());
            physical.put("observedExternalMotionEvents",pc.physicalEvents());
            physical.put("actualEnergyContacts",pc.actualEnergyContacts());
            physical.put("singleRecentMoveEnergyAssociations",pc.adjacentEnergy());
            physical.put("ambiguousMultipleMoveEnergyAssociations",pc.ambiguousEnergy());
            physical.put("energyContactsWithoutRecentMovement",pc.unlinkedEnergy());
            physical.put("supportedContexts",pc.supportedContexts());
            physical.put("invalidSamples",pc.badSamples());
            finite(physical,"heldoutBrierError",pc.error());
            finite(physical,"baselineBrierError",pc.referenceError());
            finite(physical,"confidence",pc.confidence());
            physical.put("associationIsNotProofOfCausation",true);
        }
        result.put("physicalConsequences",physical);

        String tvRaw=str(values,TransferSkillsV20.KEY);
        TransferSkillsV20 tv=new TransferSkillsV20();
        boolean tvValid=tv.restore(tvRaw);
        Map<String,Object> transfer=section(tvRaw,tvValid);
        if(tvValid){
            transfer.put("livesSeen",tv.lives());
            transfer.put("decisionsSeen",tv.decisions());
            transfer.put("actualPositiveEnergyEvents",tv.realGoodEvents());
            transfer.put("actualNegativeEnergyEvents",tv.realBadEvents());
            transfer.put("candidateSkills",tv.candidates());
            transfer.put("independentlyCorroboratedSkills",tv.confirmed());
        }
        result.put("lifeTransfer",transfer);

        String fpRaw=str(values,FuturePlannerV20.KEY);
        FuturePlannerV20 fp=new FuturePlannerV20();
        boolean fpValid=fp.restore(fpRaw);
        Map<String,Object> planner=section(fpRaw,fpValid);
        if(fpValid){
            planner.put("checked",fp.checks());
            planner.put("eligible",fp.eligible());
            planner.put("abstained",fp.abstentions());
            planner.put("eligibleFraction",fp.checks()==0?0d:
                (double)fp.eligible()/fp.checks());
            // Planner has extra validation and error gates beyond confidence.
            if(!pwValid)planner.put("readiness","MISSING_WORLD_MODEL");
            else if(pw.validations()<60)planner.put("readiness","UNDER_60_WORLD_HELDOUT");
            else if(pw.confidence()<.10f)planner.put("readiness","WORLD_CONFIDENCE_BELOW_0_10");
            else if(!Float.isFinite(pw.heldoutError())||pw.heldoutError()>.18f)
                planner.put("readiness","WORLD_ERROR_ABOVE_0_18");
            else planner.put("readiness","WORLD_GATE_PASS_ONLY_NOT_MOTOR_PERMISSION");
        }
        result.put("futurePlanner",planner);

        String perRaw=str(values,PerceptualAssociationsV20.KEY);
        PerceptualAssociationsV20 per=new PerceptualAssociationsV20();
        boolean perValid=per.restore(perRaw);
        Map<String,Object> associations=section(perRaw,perValid);
        if(perValid){
            associations.put("hypotheticalCategories",per.categories());
            associations.put("partialSilhouetteViews",per.observations());
            associations.put("unknownOrOccludedViews",per.unknown());
            associations.put("ambiguousMultiSurfaceViews",per.ambiguous());
            associations.put("heldoutActionOutcomes",per.validations());
            associations.put("realExternalMotionCorrelations",per.realExternalEvents());
            associations.put("realEnergyContactsNotAttributedToObject",per.realEnergyEvents());
            finite(associations,"heldoutBrierError",per.heldoutError());
            finite(associations,"actionOnlyReferenceBrier",per.referenceError());
            finite(associations,"predictiveOnlyConfidence",per.predictiveConfidence());
            associations.put("objectIdentityVerified",false);
            associations.put("causalTechniqueVerified",false);
            associations.put("motorInterventionAllowed",false);
        }
        result.put("perceptualAssociations",associations);

        RecurrentCoreV22 active22=new RecurrentCoreV22();
        Map<String,Object> digest22=new LinkedHashMap<>();
        boolean present22=active22.restore(str(values,RecurrentCoreV22.KEY));
        digest22.put("present",present22);digest22.put("mode","PRIMARY_ACTIVE_LEARNING");
        digest22.put("livePolicyOverrideAllowed",true);
        digest22.put("continuousRealExperienceLearning",true);
        digest22.put("reason","OTA132 recurrent core selects all 16 live actions; physical and causal evidence submodels are learned skills, not an old policy");
        if(present22){digest22.put("summary",active22.summary());digest22.put("transitions",active22.transitions());
            digest22.put("replaySize",active22.replaySize());digest22.put("replayUpdates",active22.replayUpdates());
            digest22.put("plannedChoices",active22.plannedChoices());
            digest22.put("physicalReady",active22.physicalReady());
            digest22.put("energyReady",active22.energyReady());}
        result.put("zero22",digest22);
        // ZERO21 digest: no raw weights, object positions, instance tracks,
        // private slot names, tokens, or per-35ms frame arrays. User-initiated.
        Map<String,Object> zero21=new LinkedHashMap<>();
        String ucRaw=str(values,UnifiedCognitionV21.KEY);
        UnifiedCognitionV21 uc=new UnifiedCognitionV21();
        boolean ucValid=uc.restore(ucRaw);
        Map<String,Object> shared=section(ucRaw,ucValid);
        if(ucValid){
            shared.put("realDecisionIntervals",uc.observations());
            shared.put("realExternalMotionEvents",uc.physicalEvents());
            shared.put("realContactEnergyEvents",uc.energyContacts());
            shared.put("heldoutIntervals",uc.holdouts());
            shared.put("trainingIntervals",uc.training());
            shared.put("predictionConfidenceInformational",uc.confidence());
        }
        zero21.put("sharedEvidence",shared);
        String ocRaw=str(values,ObjectContinuityV21.KEY);
        ObjectContinuityV21 oc=new ObjectContinuityV21();
        boolean ocValid=oc.restore(ocRaw);
        Map<String,Object> objects=section(ocRaw,ocValid);
        if(ocValid){
            objects.put("anonymousRayMatches",oc.trackedViews());
            objects.put("ambiguousRayViews",oc.ambiguousViews());
            objects.put("possiblePhysicalEffects",oc.candidateEffects());
            objects.put("possibleDelayedEnergyLinksNotCausalProof",oc.delayedEnergyAssociations());
            objects.put("ambiguousEnergyLinks",oc.ambiguousEnergyAssociations());
            objects.put("unlinkedContactEnergy",oc.unlinkedEnergyContacts());
            objects.put("heldoutComparisons",oc.holdouts());
            objects.put("predictionConfidenceInformational",oc.confidence());
            objects.put("objectIdentityVerified",false);
        }
        zero21.put("objectContinuity",objects);
        String cmRaw=str(values,CompetenceMemoryV21.KEY);
        CompetenceMemoryV21 cm=new CompetenceMemoryV21();
        boolean cmValid=cm.restore(cmRaw);
        Map<String,Object> technique=section(cmRaw,cmValid);
        if(cmValid){
            technique.put("provenPhysicalHoldAndMotionFrames",cm.actualWitnesses());
            technique.put("anonymousIdentityUnresolved",cm.unresolvedIdentityWitnesses());
            technique.put("recordedPairs",cm.size());
            technique.put("motorInterventionAllowed",false);
        }
        zero21.put("competenceMemory",technique);
        String uaRaw=str(values,UnifiedArbiterV21.KEY);
        UnifiedArbiterV21 ua=new UnifiedArbiterV21();
        boolean uaValid=ua.restore(uaRaw);
        Map<String,Object> arb=section(uaRaw,uaValid);
        if(uaValid){
            arb.put("checked",ua.checks());
            arb.put("eligibleSuggestionsNeverActuated",ua.eligible());
            arb.put("abstained",ua.abstentions());
            arb.put("motorInterventionAllowed",false);
        }
        zero21.put("unifiedArbiter",arb);
        result.put("zero21",zero21);
        return result;
    }
}
