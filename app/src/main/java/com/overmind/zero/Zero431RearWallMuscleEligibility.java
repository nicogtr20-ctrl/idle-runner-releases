package com.overmind.zero;

/**
 * ZERO431 laboratory-only local-cortex motor-credit eligibility.
 * A rear-wall retreat is not an actuator success merely because the
 * material travels forward: PRE-existing momentum can produce that travel.
 * Keep V414 real rear-only contact geometry and additionally require a
 * measured positive POST-minus-PRE COM velocity component along the
 * actually issued GRU3 heading. This is necessary, not sufficient, evidence.
 * No reward, heading, force, sensor, weight or checkpoint is created.
 */
final class Zero431RearWallMuscleEligibility {
    private Zero431RearWallMuscleEligibility(){}

    static boolean verifiedRearRetreat(boolean realWallContact,
            int realRearFrames,int realFrontFrames,float signedDisplacement,
            float preVx,float preVy,float postVx,float postVy,
            float issuedX,float issuedY){
        if(!Zero401LocalMotorCreditSafety.rearWallCanTeachMuscleV414(
                realWallContact,realRearFrames,realFrontFrames,signedDisplacement))
            return false;
        return Zero420MotorMapEligibility.measuredForwardAccelerationV430(
                preVx,preVy,postVx,postVy,issuedX,issuedY);
    }
}
