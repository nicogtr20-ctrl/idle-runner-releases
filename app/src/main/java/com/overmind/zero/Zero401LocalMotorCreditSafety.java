package com.overmind.zero;

/**
 * ZERO401 non-commanding safety gate for the sole V3 motor cortex learner.
 * It cannot steer ZERO, inspect a gem, change a reward, add a motor force or
 * alter the 512 neural weights itself. It verifies whether a delayed, local
 * muscle-credit sample is causally interpretable from observed material data.
 */
final class Zero401LocalMotorCreditSafety {
    private Zero401LocalMotorCreditSafety(){}

    static boolean cleanPhysicalDecision(boolean probeRecorded,boolean physicalEvidence,
            boolean heldout,boolean terminal,boolean externallyDisturbed,
            boolean externalCorrection,boolean censored,boolean solidContact,
            float actualHarm){
        if(!Float.isFinite(actualHarm)||actualHarm<0f)
            throw new IllegalArgumentException("V401 invalid real harm");
        // A collision with a red material, or a measured storm, consumes
        // reserve and physically changes the experience. Motor locomotion
        // must NOT mistake that for clean actuator->COM credit. The high-level
        // GRU still receives the REAL harm and contact normally.
        return probeRecorded&&physicalEvidence&&!heldout&&!terminal&&
            !externallyDisturbed&&!externalCorrection&&!censored&&
            !solidContact&&actualHarm<=0f;
    }

    /**
     * ZERO414: V401 mistakenly censored EVERY wall touch, even when the
     * real original S21 cortex was physically retreating FROM a wall.
     * The already-existing V390 measured rear/front geometry is the
     * validated primitive. Strictly reward only the rare REAR-only contact
     * with actual signed material travel AWAY from the obstacle. The full
     * V401 harm/external/terminal/isolated motor guards still apply.
     *
     * Safety is a test hypothesis: 10k real red and 10k saved S21 tests
     * must both pass before production. It cannot choose a direction,
     * supply energy, or move a membrane node by itself.
     */
    static boolean rearWallCanTeachMuscleV414(boolean realWallContact,
            int realRearFrames,int realFrontFrames,float actualForward){
        if(!Float.isFinite(actualForward)||realRearFrames<0||realFrontFrames<0)
            throw new IllegalArgumentException("V414 invalid physical evidence");
        return realWallContact&&realRearFrames>0&&realFrontFrames==0&&
            actualForward>.05f;
    }

    // Reuse the old validated V390 proof: one actual sampled muscle and
    // meaningful VOLUNTARY effort, not a passive drift or multiple probes.
    static boolean hasPhysicalMotorProbe(float effort,int probeCount){
        if(!Float.isFinite(effort)||effort<0f||effort>1f)
            throw new IllegalArgumentException("V406 invalid effort");
        return effort>.01f&&probeCount==1;
    }

    static float nextDecisionDelayValidity(float prevX,float prevY,
            float nextX,float nextY){
        if(!Float.isFinite(prevX)||!Float.isFinite(prevY)||
           !Float.isFinite(nextX)||!Float.isFinite(nextY))
            throw new IllegalArgumentException("V401 nonfinite direction");
        float oldLen=(float)Math.hypot(prevX,prevY);
        float nowLen=(float)Math.hypot(nextX,nextY);
        if(oldLen<.001f||nowLen<.001f)return 0f;
        float dot=(prevX*nextX+prevY*nextY)/(oldLen*nowLen);
        // At >60 deg reversal/turn, next action's muscle response cannot
        // be credited as a delayed continuation of the previous action.
        // 0 deg: full original V332 0.82 delay; 60 deg+: none.
        // This is a bounded *eligibility* criterion, not an authored gait.
        return Math.max(0f,Math.min(1f,2f*(dot-.5f)));
    }
}
