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
