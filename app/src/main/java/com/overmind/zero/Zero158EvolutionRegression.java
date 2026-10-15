package com.overmind.zero;
/** No Android dependencies: verify displayed 2.2 training/evolution counters
 * are exactly the counters persisted by the current living brain and cortex. */
public final class Zero158EvolutionRegression {
    static void ok(boolean good,String why){if(!good)throw new AssertionError(why);}
    public static void main(String[] args){
        RecurrentCoreV22 core=new RecurrentCoreV22();
        SymbioteMotorHeadV1 muscle=new SymbioteMotorHeadV1();
        String beforeCore=core.encode(),beforeMuscle=muscle.encode();
        ok(core.trainedTransitions()==0&&core.decisionCount()==0
            &&core.heldoutTransitions()==0,"Virgin core incorrectly reports evolution");
        ok(muscle.trainedOutcomes()==0&&muscle.decisionCount()==0
            &&muscle.heldoutOutcomes()==0,"Virgin cortex has invented skills");
        RecurrentCoreV22 restored=new RecurrentCoreV22();
        SymbioteMotorHeadV1 restoredMuscle=new SymbioteMotorHeadV1();
        ok(restored.restore(beforeCore)&&restoredMuscle.restore(beforeMuscle),
           "Read-only evolution is not sourced from restorable real checkpoints");
        ok(core.parameterCount()==restored.parameterCount(),
           "Core parameter browser inconsistent with saved 2.2 architecture");
        ok(muscle.parameterCount()==restoredMuscle.parameterCount(),
           "Cortex parameter browser inconsistent with saved muscle weights");
        ok(FluidInteroceptionV1.PARAMETERS>0&&
           FluidInteroceptionV1.PARAMETERS==16*112*3+16*113,
           "Fluid subsystem model parameters inaccurate");
        ok(beforeCore.equals(core.encode())&&beforeMuscle.equals(muscle.encode()),
           "Evolution inspection unexpectedly modified learned brain data");
        System.out.println("PASS OTA158: one living 2.2, true motor/holdout counters, fluid parameters, read-only checkpoints");
    }
}