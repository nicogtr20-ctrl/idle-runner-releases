package com.overmind.zero;

/**
 * ZERO420 science-only eligibility for V371/V377's higher neural motor
 * response map, grounded entirely in the original completed physics event.
 *
 * The actor still sees and learns true red damage, real optical contact,
 * actual reserve changes and terminal consequences; ONLY attribution of
 * movement to muscle commands is censored when a foreign physical cause
 * influenced the body. No action, direction, force, reward or save migration.
 */
final class Zero420MotorMapEligibility {
    private Zero420MotorMapEligibility(){}

    /**
     * V430: a rear-only retreat can be passive coasting. Positive displacement
     * alone does NOT demonstrate an actuator response. Require the measured
     * POST-minus-PRE COM velocity to have a positive component along the
     * actual issued GRU3 heading before allowing this exceptional wall trial
     * into the upper V371/V377 motor-response map. This is a necessary causal
     * evidence gate, not a reward, steering command or proof of sufficiency.
     */
    static boolean measuredForwardAccelerationV430(float preVx,float preVy,
            float postVx,float postVy,float intentX,float intentY){
        if(!Float.isFinite(preVx)||!Float.isFinite(preVy)||
           !Float.isFinite(postVx)||!Float.isFinite(postVy)||
           !Float.isFinite(intentX)||!Float.isFinite(intentY))
            throw new IllegalArgumentException("V430 requires real finite COM evidence");
        float length=(float)Math.hypot(intentX,intentY);
        if(length<.5f)return false;
        return ((postVx-preVx)*intentX+(postVy-preVy)*intentY)/length>0f;
    }

    static boolean fromRealDecision(
            boolean physicalEvidence, boolean disturbed,
            boolean externalCorrection, boolean censored,
            boolean realTerminal, float trueContactHarm,
            boolean solidTouched, boolean verifiedRearRetreat) {
        if(!Float.isFinite(trueContactHarm)||trueContactHarm<0f)
            throw new IllegalArgumentException(
                "ZERO420 motor eligibility requires measured nonnegative damage");
        // A rear-only escape needs REAL solid touch plus the V414 verified
        // positive forward displacement, with no forward contact.
        if(verifiedRearRetreat&&!solidTouched)
            throw new IllegalArgumentException(
                "ZERO420 rear-only claim requires actual material contact");
        return physicalEvidence&&!disturbed&&!externalCorrection&&!censored&&
            !realTerminal&&trueContactHarm==0f&&
            (!solidTouched||verifiedRearRetreat);
    }
}
