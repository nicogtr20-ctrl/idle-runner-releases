package com.overmind.zero;

import java.io.*;
import java.util.Arrays;

/**
 * Sensory/physical memory owned by the existing V353 cognition.
 * There is NO new actor or movement instruction. A consequence is associated
 * with a light only after a genuine energy contact, and a direction is
 * predicted only from the original physical body's measured response.
 *
 * Historical analogy: ExperienceCredit + InstrumentalNavigation, but there
 * is only ONE GRU3 chooser and existing V353 world predictors are untouched.
 */
final class Zero3InstrumentalCreditV371 {
    static final int VERSION=371, BINS=8, BANDS=3, TRACE=6;
    private final float[] learnedX=new float[BINS],learnedY=new float[BINS];
    private final int[] trained=new int[BINS],validated=new int[BINS];
    private final double[] error=new double[BINS],baseline=new double[BINS];
    private Zero3InertialResponseV377 inertia=new Zero3InertialResponseV377();
    // V489 QA-only: matched observed PRE-velocity and effort for the same
    // ORIGINAL real material outcome, against a pooled real negative control.
    private final Zero489MatchedPreMotorResponse matchedMotorV489=
        new Zero489MatchedPreMotorResponse();
    private boolean matchedPreMotorLabV489;
    // V505 diagnostic-only conservative loss envelope: choose with the
    // actually learned harmful wavelength, never hard-coded red/blue.
    // Disabled on regular V489 phone and original 3.0 checkpoints.
    private boolean physicalLearnedHarmEnvelopeLabV505;
    void enablePhysicalLearnedHarmEnvelopeLabV505(){
        physicalLearnedHarmEnvelopeLabV505=true;
    }

    private float pendingEffortV489;
    void enableMatchedPreMotorLabV489(){
        if(pendingSight!=null||!strictInertialOpticalEvidenceLabV433||
           !censorUnverifiedDelayedBankUnderDriftLabV442||
           !measuredPassiveCoastOpticalLabV478)
            throw new IllegalStateException("V489 needs real original V433/V442/V478 boundary");
        matchedPreMotorLabV489=true;
    }
    /** Old V377 model stays intact and takes precedence if verified.
     * V489 is ONLY actual separate heldout-confirmed real material evidence. */
    private float[] predictedPhysicalTravelV489(float[] observations,float[] proposed){
        float[] previous=inertia.expected(observations,proposed);
        return previous!=null?previous:
            matchedPreMotorLabV489?matchedMotorV489.predicted(observations,proposed):null;
    }
    // V433 LAB: without a heldout-validated motor response, high PRE inertia
    // cannot be replaced by the intended heading when estimating optical travel.
    private boolean strictInertialOpticalEvidenceLabV433;
    // V434 LAB: physically clean past approaches later followed by actual contact.
    // Observational bank association is NOT a verified motor response.
    private boolean observedDelayedContactWithoutMotorMapLabV434;
    // V435 research: a clean observed approach must also have a measured
    // positive acceleration ALONG its real travel, rather than passive coast.
    // This changes only the eligibility of historical delayed contact links.
    private boolean acceleratedDelayedContactEvidenceLabV435;
    // V442: historical observed bank is not a validated new motor response.
    private boolean censorUnverifiedDelayedBankUnderDriftLabV442;
    // V443 transient READ-ONLY evidence counters: evaluations, not decisions.
    // V475: ephemeral branch-reachability counters; never serialized or used in scoring.
    private boolean measuredPassiveCoastOpticalLabV478;
    private long v478PassiveScoreEvaluations,v478PassiveOpticalEvaluations;
    private long v475ScoreReconstructed, v475OpticalReconstructed,
        v475ScoreDriftBlocked, v475OpticalDriftBlocked;
    private long v443ScoreEvaluations, v443PredictedEvaluations,
        v443UnsupportedDriftEvaluations, v443CensoredNonzeroHistorical,
        v443CensoredZeroHistorical, v443UncensoredNonzeroHistorical,
        v443OpticalDriftSuppressed;
    String motorEvidenceDiagnosticsV443(){
        return "v443|scoreEvaluations="+v443ScoreEvaluations+
            "|predicted="+v443PredictedEvaluations+
            "|unsupportedDrift="+v443UnsupportedDriftEvaluations+
            "|censoredNonzeroHistorical="+v443CensoredNonzeroHistorical+
            "|censoredZeroHistorical="+v443CensoredZeroHistorical+
            "|uncensoredNonzeroHistorical="+v443UncensoredNonzeroHistorical+
            "|opticalDriftSuppressed="+v443OpticalDriftSuppressed+
            "|v478PassiveScore="+v478PassiveScoreEvaluations+
            "|v478PassiveOptical="+v478PassiveOpticalEvaluations+
            "|v475ScoreReconstructed="+v475ScoreReconstructed+
            "|v475OpticalReconstructed="+v475OpticalReconstructed+
            "|v475ScoreDriftBlocked="+v475ScoreDriftBlocked+
            "|v475OpticalDriftBlocked="+v475OpticalDriftBlocked+
            "|verifiedV377Banks="+inertia.reliableBanks()+
            "|verifiedV371Directions="+verifiedDirections()+
            "|delayedRealLinks="+delayedCredits()+"|"+inertia.motorValidationDiagnosticsV444()+
            "|"+motorBankEvidenceDiagnosticsV457()+
            "|v505HarmEnvelope="+physicalLearnedHarmEnvelopeLabV505+
            "|v489Enabled="+matchedPreMotorLabV489+"|"+matchedMotorV489.diagnostics();
    }
    // V457 read-only: inspect all original V371 bank TRAIN/HELDOUT
    // measurements against the V377 inertial validation already logged.
    // No new state, reward, action selection, threshold, or persistence.
    private String motorBankEvidenceDiagnosticsV457(){
        StringBuilder b=new StringBuilder("v457V371BankTrainHeldoutForwardLossBaselineMilli=");
        for(int i=0;i<BINS;i++){
            if(i>0)b.append(',');
            double angle=i*Math.PI/4d;
            double forward=learnedX[i]*Math.cos(angle)+learnedY[i]*Math.sin(angle);
            b.append(i).append(':').append(trained[i]).append('/')
                .append(validated[i]).append(':')
                .append(Math.round(forward*1000d)).append(':')
                .append(Math.round(error[i]*1000d)).append(':')
                .append(Math.round(baseline[i]*1000d)).append(':')
                .append(motorVerified(i)?1:0);
        }
        return b.toString();
    }
    void enableCensorUnverifiedDelayedBankUnderDriftLabV442(){
        if(pendingSight!=null||!strictInertialOpticalEvidenceLabV433||
           !observedDelayedContactWithoutMotorMapLabV434||
           !acceleratedDelayedContactEvidenceLabV435)
            throw new IllegalStateException("V442 requires V433/V434/V435 between decisions");
        censorUnverifiedDelayedBankUnderDriftLabV442=true;
    }
    private float pendingPreVxV435,pendingPreVyV435;
    private long passiveDelayedApproachesRejectedV435;
    void enableAcceleratedDelayedContactEvidenceLabV435(){
        if(pendingSight!=null||!observedDelayedContactWithoutMotorMapLabV434)
            throw new IllegalStateException("V435 requires V434 between decisions");
        acceleratedDelayedContactEvidenceLabV435=true;
    }
    long passiveDelayedApproachesRejectedV435(){return passiveDelayedApproachesRejectedV435;}
    void enableObservedDelayedContactWithoutMotorMapLabV434(){
        if(pendingSight!=null||!strictInertialOpticalEvidenceLabV433)
            throw new IllegalStateException("V434 requires V433 between decisions");
        observedDelayedContactWithoutMotorMapLabV434=true;
    }
    private long unsupportedMovingOpticalPredictionsRejectedV433;
    /** V478 LAB: an unsupported command does not cancel actual measured PRE
     * inertia. Compare its passive coast using learned wavelength consequences
     * identically for ALL unverified actions, never as motor authority. */
    void enableMeasuredPassiveCoastOpticalLabV478(){
        if(pendingSight!=null||!strictInertialOpticalEvidenceLabV433||
           !censorUnverifiedDelayedBankUnderDriftLabV442)
            throw new IllegalStateException("V478 requires V433/V442 at real decision boundary");
        measuredPassiveCoastOpticalLabV478=true;
    }
    void enableMidDriftRegimeLabV476(){
        if(pendingSight!=null)throw new IllegalStateException("V476 original decision pending");
        inertia.enableMidDriftRegimeLabV476();
    }
    void enableStrictInertialOpticalEvidenceLabV433(){
        if(pendingSight!=null)throw new IllegalStateException("V433 must start between decisions");
        strictInertialOpticalEvidenceLabV433=true;
    }
    long unsupportedMovingOpticalPredictionsRejectedV433(){
        return unsupportedMovingOpticalPredictionsRejectedV433;
    }
    // Real energy contact can retrospectively credit up to six physically
    // verified approaches without falsifying the V353 ONE-STEP predictor.
    private final float[][] delayedContactValue=new float[BANDS][BINS];
    private final int[][] delayedContactCount=new int[BANDS][BINS];
    // Real PRE visual evidence and POST movement of the last six completed
    // physically clean intentions. NEVER persist a pending/unexecuted event.
    private final float[][] recentSight=new float[TRACE][BANDS*8];
    private final float[] recentDX=new float[TRACE],recentDY=new float[TRACE];
    private final int[] recentBank=new int[TRACE];
    private int history;
    private long linkedContacts,ambiguousContacts,stalledActions;
    private float[] pendingSight;
    private int pendingBank=-1;
    private boolean pendingHeldout;
    private int lastBank=-1,stalledStreak;
    // Short-lived physically experienced damage: urgency, not innate hue.
    private int recentHarmBand=-1;
    private float recentHarmTime;
    private static float limit(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    /**
     * V441 LAB: V433 must not confuse a requested heading with material travel
     * when PRE COM drift is already physically measurable. Original V433's
     * 2 U/s gate missed every one of V440's 39 genuine red contacts (max
     * 1.785 U/s), even though 38 were already drifting toward the hazard.
     * The existing .35 physical-motion evidence floor is used only while
     * V433's strict, transient lab flag is ON. No neural/physics change.
     */
    private boolean unsupportedPreDriftV441(float[] observation194){
        double speed=Math.hypot(observation194[191]*130f,observation194[192]*130f);
        return speed>=(strictInertialOpticalEvidenceLabV433?.35d:2d);
    }

    private static int heading(float x,float y){
        if(!Float.isFinite(x)||!Float.isFinite(y)||Math.hypot(x,y)<.1)
            throw new IllegalArgumentException("V371 invalid physical intention");
        return Math.floorMod(Math.round((float)Math.atan2(y,x)/(float)(Math.PI/4)),BINS);
    }
    void begin(float[] observation194,float[] action,boolean heldout){
        if(pendingSight!=null||observation194==null||observation194.length!=194||
           action==null||action.length!=5)
            throw new IllegalStateException("V371 unfinished or malformed motor decision");
        for(int i=20;i<44;i++)if(!Float.isFinite(observation194[i])||
                observation194[i]<0||observation194[i]>1)
            throw new IllegalArgumentException("V371 invalid photons");
        pendingSight=Arrays.copyOfRange(observation194,20,44);
        pendingPreVxV435=observation194[191]*130f;
        pendingPreVyV435=observation194[192]*130f;
        pendingBank=heading(action[0],action[1]);pendingHeldout=heldout;
        pendingEffortV489=action[2];
        inertia.begin(observation194,action,heldout);
    }
    void complete(float dx,float dy,float seconds,boolean clean,boolean heldout){
        complete(dx,dy,seconds,clean,heldout,false);
    }
    void complete(float dx,float dy,float seconds,boolean clean,boolean heldout,
                  boolean physicallyBlocked){
        if(pendingSight==null||pendingHeldout!=heldout||
           !Float.isFinite(dx)||!Float.isFinite(dy)||
           !Float.isFinite(seconds)||seconds<=0||seconds>.651f)
            throw new IllegalStateException("V371 incomplete physical evidence");
        recentHarmTime=Math.max(0f,recentHarmTime-seconds);
        if(recentHarmTime==0f)recentHarmBand=-1;
        if(clean){
            if(matchedPreMotorLabV489){
                double angle=pendingBank*Math.PI/4d;
                matchedMotorV489.observe(pendingPreVxV435,pendingPreVyV435,
                    (float)Math.cos(angle),(float)Math.sin(angle),
                    pendingEffortV489,dx,dy,seconds,heldout);
            }
            inertia.finish(dx,dy,seconds,heldout);
            // Normalized to a physical 0.30s decision for comparable trials.
            // This is a measured displacement, NOT an authored motion target.
            float scale=.30f/seconds;
            float mx=limit(dx*scale,-120,120),my=limit(dy*scale,-120,120);
            // V473 LAB: V371 must attribute only motion ABOVE the already
            // observed PRE-decision COM velocity. A passive sliding body can
            // have repeatable heldout displacement without steering authority.
            // A measured residual is not itself proof of isolated muscle causation.
            // Strict Android behavior, regression and CRC gates remain mandatory.
            // V377 still receives the full genuine displacement unchanged;
            // wall/stall and delayed-contact traces still use real mx/my.
            float motorMx=limit(mx-.30f*pendingPreVxV435,-120,120);
            float motorMy=limit(my-.30f*pendingPreVyV435,-120,120);
            int bank=pendingBank;
            if(heldout){
                validated[bank]++;
                double e=(motorMx-learnedX[bank])*(motorMx-learnedX[bank])+
                         (motorMy-learnedY[bank])*(motorMy-learnedY[bank]);
                double zero=motorMx*motorMx+motorMy*motorMy;
                float alpha=validated[bank]==1?1f:.1f;
                error[bank]+=alpha*(e-error[bank]);
                baseline[bank]+=alpha*(zero-baseline[bank]);
            }else{
                float alpha=trained[bank]==0?1f:.10f;
                learnedX[bank]+=alpha*(motorMx-learnedX[bank]);
                learnedY[bank]+=alpha*(motorMy-learnedY[bank]);
                trained[bank]++;
                int slot=history%TRACE;
                // V435: only assign a past approach to its issued bank when
                // the measured travel has a positive change of velocity
                // relative to PRE momentum. Pure coasting and ordinary drag
                // are not evidence that the muscles drove that approach.
                // Preserve the six-CLEAN-decision temporal horizon: rejected
                // records occupy a zero-evidence slot, never an old link.
                float travel=(float)Math.hypot(mx,my);
                float activeX=mx-.30f*pendingPreVxV435;
                float activeY=my-.30f*pendingPreVyV435;
                float activeAlong=travel>.35f?(mx*activeX+my*activeY)/travel:0f;
                boolean linkedApproachEligible=!acceleratedDelayedContactEvidenceLabV435||
                    (travel>.35f&&activeAlong>.35f);
                if(linkedApproachEligible){
                    System.arraycopy(pendingSight,0,recentSight[slot],0,BANDS*8);
                    recentDX[slot]=mx;recentDY[slot]=my;
                }else{
                    Arrays.fill(recentSight[slot],0f);
                    recentDX[slot]=0f;recentDY[slot]=0f;
                    passiveDelayedApproachesRejectedV435++;
                }
                recentBank[slot]=bank;history++;
            }
            if(!heldout){
                // Stagnation is recognized from PHYSICS, not from staying in
                // one coordinate or from slow exploratory intention alone.
                float along=mx*(float)Math.cos(bank*Math.PI/4)+my*(float)Math.sin(bank*Math.PI/4);
                if(along<.35f){stalledStreak=lastBank==bank?stalledStreak+1:1;stalledActions++;}
                else stalledStreak=0;
                lastBank=bank;
            }
        }
        // Real obstacle contact can censor the motor model, not the
        // observation that the self-issued action failed to advance.
        // Never store a fake response, assign positive motor credit,
        // or directly command an escape turn.
        if(!clean&&!heldout&&physicallyBlocked){
            int bank=pendingBank;
            float along=(dx*(float)Math.cos(bank*Math.PI/4)+
                         dy*(float)Math.sin(bank*Math.PI/4))*.30f/seconds;
            if(along<.35f){
                stalledStreak=lastBank==bank?stalledStreak+1:1;
                stalledActions++;
            }else stalledStreak=0;
            lastBank=bank;
        }
        abort();
    }
    void contact(float[] observedPhotons,float actualDelta,boolean heldout){
        if(pendingSight==null||observedPhotons==null||observedPhotons.length!=24||
            !Float.isFinite(actualDelta))throw new IllegalStateException("V371 contact data invalid");
        if(heldout||actualDelta==0f)return;
        int found=-1;float peak=0f,runner=0f;
        for(int b=0;b<BANDS;b++){
            float x=0f;
            for(int ray=0;ray<8;ray++){
                float p=observedPhotons[b*8+ray];
                if(!Float.isFinite(p)||p<0f||p>1f)
                    throw new IllegalArgumentException("V371 photons");
                x=Math.max(x,p);
            }
            if(x>peak){runner=peak;peak=x;found=b;}
            else runner=Math.max(runner,x);
        }
        if(found<0||peak<.25f||peak<runner*1.15f){ambiguousContacts++;return;}
        linkTrueContactToPriorMotor(found,actualDelta);
    }
    /** Material-contact evidence has already identified the touched surface
     * wavelength. Never let a brighter NEIGHBOUR steal causal attribution. */
    void materialContactV374(float[] observedPhotons,int touchedBand,
                             float actualDelta,boolean heldout){
        if(pendingSight==null||observedPhotons==null||observedPhotons.length!=24||
           touchedBand<0||touchedBand>=BANDS||
           !Float.isFinite(actualDelta)||Math.abs(actualDelta)>100f)
            throw new IllegalArgumentException("V374 invalid touched surface");
        for(float p:observedPhotons)
            if(!Float.isFinite(p)||p<0||p>1)
                throw new IllegalArgumentException("V374 invalid observed photons");
        if(heldout||actualDelta==0f)return;
        linkTrueContactToPriorMotor(touchedBand,actualDelta);
    }
    private void linkTrueContactToPriorMotor(int found,float actualDelta){
        // A temporary risk emphasis is permitted ONLY after a real negative
        // energy observation with a non-ambiguous visible wavelength.
        if(actualDelta<0f){recentHarmBand=found;recentHarmTime=4.5f;}
        // Verify that PREVIOUS motor choices actually approached a visible
        // frequency that later caused real energy gain/loss. The signed
        // contact outcome itself is handled by V369, once, separately.
        boolean matched=false;
        for(int age=0;age<Math.min(history,TRACE);age++){
            int slot=Math.floorMod(history-1-age,TRACE);
            float[] sight=recentSight[slot];
            float projection=0f,best=0f;
            for(int ray=0;ray<8;ray++){
                float seen=sight[found*8+ray];
                float angle=(float)(ray*Math.PI/4d);
                float toward=recentDX[slot]*(float)Math.cos(angle)+
                    recentDY[slot]*(float)Math.sin(angle);
                if(seen>.08f&&toward>.35f&&seen*toward>best){
                    projection=toward;best=seen*toward;
                }
            }
            if(projection>.35f){
                matched=true;
                int bank=recentBank[slot];
                float actualValue=limit(actualDelta/(actualDelta>0f?22f:7f),-1.5f,1.5f);
                float discounted=(float)(actualValue*Math.pow(.76,age));
                float alpha=delayedContactCount[found][bank]==0?1f:.14f;
                delayedContactValue[found][bank]+=alpha*
                    (discounted-delayedContactValue[found][bank]);
                if(delayedContactCount[found][bank]<100000000)
                    delayedContactCount[found][bank]++;
            }
        }
        if(matched)linkedContacts++;else ambiguousContacts++;
    }
    /** Trust motor direction only if its OUT-OF-TRAIN physical displacement
     * estimate beats zero motion, with enough evidence. */
    /** V472 LAB: a squared-displacement floor of 1.0 discarded every
     * original S21/wild bank despite genuine TRAIN/HELDOUT measurements
     * in the 0.02..0.58 squared-unit range. Validate against the observed
     * zero-motion heldout baseline and require measured forward travel.
     * The V433 pre-drift guard still forbids inventing an action-to-travel
     * prediction for a moving body without V377 evidence. */
    boolean motorVerified(int bank){
        if(trained[bank]<4||validated[bank]<3||baseline[bank]<=0d||
           !(error[bank]<baseline[bank]*.90d))return false;
        double angle=bank*Math.PI/4d;
        double forward=learnedX[bank]*Math.cos(angle)+
            learnedY[bank]*Math.sin(angle);
        return forward>.10d;
    }
    /**
     * V474 experimental consistency repair: V473 stored a PRE-coast-subtracted
     * motor residual in learnedX/Y. Candidate scoring must reconstruct the
     * physically expected TOTAL travel, as V377.expected already does, rather
     * than misreading that residual as an absolute material displacement.
     * No reward, steering direction, body force or saved-state change.
     */
    static float[] reconstructRealTravelFromResidualV474(float[] pre194,
                                                          float residualX,float residualY){
        if(pre194==null||pre194.length!=194||
           !Float.isFinite(pre194[191])||!Float.isFinite(pre194[192])||
           !Float.isFinite(residualX)||!Float.isFinite(residualY))
            throw new IllegalArgumentException("V474 invalid PRE/residual");
        return new float[]{
            limit(residualX+.30f*pre194[191]*130f,-120f,120f),
            limit(residualY+.30f*pre194[192]*130f,-120f,120f)
        };
    }
    /** V374: once this body's REAL response for an intended heading has
     * been heldout-validated, use the measured physical bearing when judging
     * optical consequences. Otherwise the actor may select a commanded
     * heading toward safety that literally MOVES INTO the dangerous light.
     * Unverified headings retain only a weak visual hypothesis until real
     * material movement establishes a reliable action→body map.
     */
    float opticalPreferenceV374(float[] observation194,float[] chosen,
                               Zero3ContactValenceV369 contact){
        if(observation194==null||observation194.length!=194||
           chosen==null||chosen.length<2||contact==null)
            throw new IllegalArgumentException("V374 physical optical comparison");
        int bank=heading(chosen[0],chosen[1]);
        float[] predicted=predictedPhysicalTravelV489(observation194,chosen);
        if(predicted!=null){
            // V480 LAB: score() already scores the SAME heldout V377 travel
            // against the SAME actually learned wavelength consequences.
            // Adding its normalized bearing again is correlated evidence,
            // not a second physical motor observation.
            return 0f;
        }
        // V371's apparent motor skill can be pure balloon coasting.
        // Do not trust unconditional bank travel when already moving.
        if(unsupportedPreDriftV441(observation194)){
            // V433: commanded bearing is NOT evidence of material travel
            // while a drifting body has no validated action->inertia map.
            // The real negative contact remains learned; do not invent a
            // motor outcome or select a preset escape direction.
            if(strictInertialOpticalEvidenceLabV433){
                if(measuredPassiveCoastOpticalLabV478){
                    // Actual PRE COM momentum is physically known even though
                    // the proposed muscle command has NO verified steering.
                    // Identical value for every unsupported proposed heading.
                    // V479: the SAME measured passive displacement is already
                    // evaluated by score() for every unsupported action. V478
                    // additionally added its normalized direction as optical
                    // preference, counting one physical observation twice.
                    // Keep one evidence-backed term, not two correlated rewards.
                    v478PassiveOpticalEvaluations++;
                    return 0f;
                }
                unsupportedMovingOpticalPredictionsRejectedV433++;
                v443OpticalDriftSuppressed++;
                v475OpticalDriftBlocked++;
                return 0f;
            }
            return .35f*contact.utility(observation194,chosen[0],chosen[1]);
        }
        if(motorVerified(bank)){
            // V480: score() already reconstructs the identical measured
            // V371 residual plus PRE coast and scores that physical travel.
            // Do not count the same retinal/contact evidence twice.
            v475OpticalReconstructed++;
            return 0f;
        }
        return .35f*contact.utility(observation194,chosen[0],chosen[1]);
    }
    /** V434: only previously measured clean approaches linked to actual later
     * energy contacts can rank a motor bank while V377 travel is unverified.
     * No guessed displacement, innate colour or artificial reward.
     */
    private float observedDelayedContactScoreV434(float[] observation194,int bank,
            Zero3ContactValenceV369 contact){
        float reserve=limit((observation194[188]+1f)*.5f,0f,1f);
        float positiveNeed=.35f+1.25f*(1f-reserve);
        float reward=0f,observed=0f;
        for(int b=0;b<BANDS;b++){
            float value=contact.valueForInstrumentalV371(b);
            if(Math.abs(value)<.10f||delayedContactCount[b][bank]<2)continue;
            float brightness=0f;
            for(int ray=0;ray<BINS;ray++){
                float seen=observation194[20+b*BINS+ray];
                if(!Float.isFinite(seen)||seen<0f||seen>1f)
                    throw new IllegalArgumentException("V434 nonphysical retina");
                brightness=Math.max(brightness,seen);
            }
            if(brightness<=.08f)continue;
            observed+=brightness;
            reward+=.22f*delayedContactValue[b][bank]*brightness*
                (value>0f?positiveNeed:1.35f);
        }
        return observed<=.08f?0f:limit(reward/(.35f+observed),-.5f,.5f);
    }
    /**
     * Directional exposure to PREVIOUSLY EXPERIENCED harmful material only.
     * No known semantic color, object identity, unseen future contact, or
     * authored heading.  This quantity is a risk preference, NOT a second
     * independent observation of damage or a reward to learn from.
     * V489's physically predicted travel can be incorrect during a fast
     * direction change. Cover BOTH issued and model-predicted headings.
     */
    static float learnedMaterialHarmExposureV505(float[] observed194,
            float dx,float dy,Zero3ContactValenceV369 remembered){
        if(observed194==null||observed194.length!=194||remembered==null||
           !Float.isFinite(dx)||!Float.isFinite(dy))
            throw new IllegalArgumentException("V505 invalid actual physical sensory evidence");
        float norm=(float)Math.hypot(dx,dy);
        if(norm<.0001f)return 0f;
        float x=dx/norm,y=dy/norm,peak=0f;
        for(int band=0;band<3;band++){
            float value=remembered.valueForInstrumentalV371(band);
            if(value>=-.10f)continue;
            for(int ray=0;ray<8;ray++){
                float optical=observed194[20+8*band+ray];
                if(!Float.isFinite(optical)||optical<0f||optical>1f)
                    throw new IllegalArgumentException("V505 invalid observed photons");
                if(optical<=.15f)continue;
                float angle=(float)(Math.PI*ray/4d);
                float toward=Math.max(0f,x*(float)Math.cos(angle)+
                    y*(float)Math.sin(angle));
                peak=Math.max(peak,Math.min(1f,-value)*optical*toward);
            }
        }
        return Math.min(1f,Math.max(0f,peak));
    }
    float score(float[] observation194,float[] chosen,
                Zero3ContactValenceV369 contact){
        if(observation194==null||observation194.length!=194||
           chosen==null||chosen.length<2||contact==null)
            throw new IllegalArgumentException("V371 score shape");
        int bank=heading(chosen[0],chosen[1]);
        v443ScoreEvaluations++;
        float[] predicted=predictedPhysicalTravelV489(observation194,chosen);
        if(predicted!=null)v443PredictedEvaluations++;
        float mx,my;
        boolean passiveOnly=false;
        // V481: only an independently verified physical travel may use the
        // normalized measured optical bearing; never the requested heading.
        boolean verifiedTravel=predicted!=null;
        if(predicted!=null){mx=predicted[0];my=predicted[1];}
        else{
            boolean unsupportedDrift=unsupportedPreDriftV441(observation194);
            if(unsupportedDrift)v443UnsupportedDriftEvaluations++;
            if(unsupportedDrift||!motorVerified(bank)){
                float historical=observedDelayedContactWithoutMotorMapLabV434?
                    observedDelayedContactScoreV434(observation194,bank,contact):0f;
                if(unsupportedDrift&&censorUnverifiedDelayedBankUnderDriftLabV442){
                    if(Math.abs(historical)>0.000001f)v443CensoredNonzeroHistorical++;
                    else v443CensoredZeroHistorical++;
                    if(!measuredPassiveCoastOpticalLabV478){
                        v475ScoreDriftBlocked++;
                        return 0f;
                    }
                    // One physically measured PRE velocity for ALL unsupported
                    // commands: NOT a guessed actuator response or a heading.
                    mx=limit(.30f*observation194[191]*130f,-120f,120f);
                    my=limit(.30f*observation194[192]*130f,-120f,120f);
                    passiveOnly=true;
                    v478PassiveScoreEvaluations++;
                }else{
                    if(Math.abs(historical)>0.000001f)v443UncensoredNonzeroHistorical++;
                    return historical;
                }
            }else{
                v475ScoreReconstructed++;
                float[] totalTravel=reconstructRealTravelFromResidualV474(
                    observation194,learnedX[bank],learnedY[bank]);
                mx=totalTravel[0];my=totalTravel[1];
                verifiedTravel=true;
            }
        }
        float reward=0f,brightness=0f;
        // Sensor 188 is ACTUAL reserve in [-1,+1], not a food label.
        float currentReserve=limit((observation194[188]+1f)*.5f,0f,1f);
        float positiveNeed=.35f+1.25f*(1f-currentReserve);
        for(int b=0;b<BANDS;b++){
            float value=contact.valueForInstrumentalV371(b);
            if(Math.abs(value)<.10f)continue;
            float urgency=recentHarmBand==b&&recentHarmTime>0f?1.35f:1f;
            // Real injury remains costly at full reserve; actual positive
            // energy becomes more important as measured reserve decreases.
            value*=value>0f?positiveNeed:1.35f*urgency;
            float observedBand=0f;
            for(int ray=0;ray<8;ray++){
                float seen=observation194[20+b*8+ray];
                observedBand=Math.max(observedBand,seen);
                if(!Float.isFinite(seen)||seen<0||seen>1)
                    throw new IllegalArgumentException("V371 nonphysical rays");
                if(seen<.08f)continue;
                double theta=ray*Math.PI/4;
                float toward=limit((mx*(float)Math.cos(theta)+
                    my*(float)Math.sin(theta))/35f,-1,1);
                float strength=seen*seen;
                // V481: for verified V377/V371 travel, this tiny /35
                // magnitude term must NOT replace the previously learned
                // normalized optical bearing nor be counted twice.
                if(!verifiedTravel)reward+=value*strength*toward;
                brightness+=strength;
            }
            // An independent delayed outcome from TRUE previously
            // executed approaches; never label a one-step V353 prediction
            // with future energy or grant an imagined pickup.
            if(!passiveOnly&&delayedContactCount[b][bank]>=2&&observedBand>.08f)
                reward+=.22f*delayedContactValue[b][bank]*observedBand*
                    (value>0?positiveNeed:1.35f);
        }
        float utility=brightness>.015f?limit(reward/(.35f+brightness),-.5f,.5f):0f;
        if(verifiedTravel){
            // The SAME existing V369 contact valence is used ONCE, on the
            // measured/heldout-predicted physical bearing. PRE inertia may
            // point opposite the GRU action. Zero real travel gives zero
            // directional optical authority; no invented steering/reward.
            float travel=(float)Math.hypot(mx,my);
            if(travel>.0001f)
                utility+=.35f*contact.utility(observation194,mx/travel,my/travel);
        }
        // V505: physical harmful contacts have already taught a wavelength
        // a negative consequence. At rapid reversals, the learned V489
        // predictor can forecast safe movement while the issued intention
        // points directly at a visibly harmful source (V500 actual red
        // collisions with available safer GRU proposals). This is a
        // conservative risk COST, not another invented contact/reward.
        // Only previously learned harm and actually observed photons apply;
        // do not add positive-blue bias or override GRU sample generation.
        if(physicalLearnedHarmEnvelopeLabV505){
            float commanded=learnedMaterialHarmExposureV505(
                observation194,chosen[0],chosen[1],contact);
            float predictedHarm=0f;
            if(verifiedTravel){
                float predictedLength=(float)Math.hypot(mx,my);
                if(predictedLength>.0001f)
                    predictedHarm=learnedMaterialHarmExposureV505(
                        observation194,mx/predictedLength,my/predictedLength,
                        contact);
            }
            float risk=Math.max(commanded,predictedHarm);
            // Still one original cognition score/actor owner: physical
            // loss aversion weighs experienced risk more than uncertain
            // motor-score advantage, without prescribing a safe direction.
            utility-=.45f*risk;
        }
        // If a real action has repeated produced no forward material motion,
        // discourage repeating that exact bank, without selecting a direction.
        if(!passiveOnly&&bank==lastBank&&stalledStreak>=3)utility-=.04f;
        return limit(utility,-.5f,.5f);
    }
    /** Physical motor control, not a visually recognized gem, is the
     * evidence gate for the GRU's wide-angular exploration distribution.
     * Two distinct trusted steering headings are needed in the current
     * measured PRE-momentum regime. Previous/stored 371+377 evidence remains
     * byte-for-byte compatible across named neural checkpoints.
     */
    boolean wideMotorExplorationSupportedV383(float[] pre194){
        return inertia.reliableSteeringDirectionsV383(pre194)>=2;
    }
    boolean hasVerifiedDirectionalEvidence(float[] photons194,Zero3ContactValenceV369 v){
        if(photons194==null||v==null)return false;
        for(int i=0;i<BINS;i++)if(motorVerified(i)){
            float[] action={(float)Math.cos(i*Math.PI/4),(float)Math.sin(i*Math.PI/4)};
            if(Math.abs(score(photons194,action,v))>.015f)return true;
        }
        return false;
    }
    void abort(){
        pendingSight=null;pendingBank=-1;pendingHeldout=false;
        // An interrupted or dead original GRU3 decision is NOT a V489
        // physical observation, and has no reusable issued effort.
        pendingEffortV489=0f;
        inertia.abort();
    }
    void newLife(){abort();history=0;stalledStreak=0;lastBank=-1;
        recentHarmBand=-1;recentHarmTime=0f;}
    void copyTo(Zero3InstrumentalCreditV371 c){
        System.arraycopy(learnedX,0,c.learnedX,0,BINS);
        System.arraycopy(learnedY,0,c.learnedY,0,BINS);
        System.arraycopy(trained,0,c.trained,0,BINS);
        System.arraycopy(validated,0,c.validated,0,BINS);
        System.arraycopy(error,0,c.error,0,BINS);
        System.arraycopy(baseline,0,c.baseline,0,BINS);
        inertia.copyTo(c.inertia);
        matchedMotorV489.copyTo(c.matchedMotorV489);
        c.matchedPreMotorLabV489=matchedPreMotorLabV489;
        c.physicalLearnedHarmEnvelopeLabV505=physicalLearnedHarmEnvelopeLabV505;
        for(int b=0;b<BANDS;b++){
            System.arraycopy(delayedContactValue[b],0,c.delayedContactValue[b],0,BINS);
            System.arraycopy(delayedContactCount[b],0,c.delayedContactCount[b],0,BINS);
        }
        c.linkedContacts=linkedContacts;c.ambiguousContacts=ambiguousContacts;
        c.stalledActions=stalledActions;
        c.measuredPassiveCoastOpticalLabV478=measuredPassiveCoastOpticalLabV478;
        c.v478PassiveScoreEvaluations=v478PassiveScoreEvaluations;
        c.v478PassiveOpticalEvaluations=v478PassiveOpticalEvaluations;
    }
    int verifiedDirections(){int n=0;for(int i=0;i<BINS;i++)if(motorVerified(i))n++;return n;}
    int verifiedInertialResponseV377(){return inertia.reliableBanks();}
    long linkedContacts(){return linkedContacts;}
    long ambiguousContacts(){return ambiguousContacts;}
    long stalledActions(){return stalledActions;}
    boolean repeatedRealMotorStall(){return stalledStreak>=3;}
    float delayedValue(int band,int bank){return delayedContactValue[band][bank];}
    int delayedCount(int band,int bank){return delayedContactCount[band][bank];}
    long delayedCredits(){long n=0;for(int[] a:delayedContactCount)
        for(int v:a)n+=v;return n;}
    void write(DataOutputStream d)throws IOException{
        if(pendingSight!=null)throw new IOException("V371 pending checkpoint");
        d.writeInt(VERSION);d.writeLong(linkedContacts);d.writeLong(ambiguousContacts);
        d.writeLong(stalledActions);
        for(int i=0;i<BINS;i++){
            d.writeInt(trained[i]);d.writeInt(validated[i]);
            d.writeFloat(learnedX[i]);d.writeFloat(learnedY[i]);
            d.writeDouble(error[i]);d.writeDouble(baseline[i]);
        }
        for(int b=0;b<BANDS;b++)for(int a=0;a<BINS;a++){
            d.writeInt(delayedContactCount[b][a]);
            d.writeFloat(delayedContactValue[b][a]);
        }
        // A pause/resume inside one real life must NOT destroy six real
        // approaches that could shortly precede a negative contact.
        d.writeInt(history);d.writeInt(lastBank);d.writeInt(stalledStreak);
        d.writeInt(recentHarmBand);d.writeFloat(recentHarmTime);
        for(int i=0;i<TRACE;i++){
            for(float photon:recentSight[i])d.writeFloat(photon);
            d.writeFloat(recentDX[i]);d.writeFloat(recentDY[i]);
            d.writeInt(recentBank[i]);
        }
        inertia.write(d); // Optional V377 suffix after fully preserved V371.
    }
    static Zero3InstrumentalCreditV371 read(DataInputStream d)throws IOException{
        if(d.readInt()!=VERSION)throw new IOException("V371 schema");
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        m.linkedContacts=d.readLong();m.ambiguousContacts=d.readLong();m.stalledActions=d.readLong();
        if(m.linkedContacts<0||m.ambiguousContacts<0||m.stalledActions<0)
            throw new IOException("V371 counts");
        for(int i=0;i<BINS;i++){
            m.trained[i]=d.readInt();m.validated[i]=d.readInt();
            m.learnedX[i]=d.readFloat();m.learnedY[i]=d.readFloat();
            m.error[i]=d.readDouble();m.baseline[i]=d.readDouble();
            if(m.trained[i]<0||m.validated[i]<0||
               m.trained[i]>100000000||m.validated[i]>100000000||
               !Float.isFinite(m.learnedX[i])||!Float.isFinite(m.learnedY[i])||
               Math.abs(m.learnedX[i])>120||Math.abs(m.learnedY[i])>120||
               !Double.isFinite(m.error[i])||!Double.isFinite(m.baseline[i])||
               m.error[i]<0||m.baseline[i]<0)
                throw new IOException("V371 physical response invalid");
        }
        for(int b=0;b<BANDS;b++)for(int a=0;a<BINS;a++){
            m.delayedContactCount[b][a]=d.readInt();
            m.delayedContactValue[b][a]=d.readFloat();
            if(m.delayedContactCount[b][a]<0||m.delayedContactCount[b][a]>100000000||
               !Float.isFinite(m.delayedContactValue[b][a])||
               Math.abs(m.delayedContactValue[b][a])>1.5f)
                throw new IOException("V371 invalid delayed causal consequence");
        }
        m.history=d.readInt();m.lastBank=d.readInt();m.stalledStreak=d.readInt();
        m.recentHarmBand=d.readInt();m.recentHarmTime=d.readFloat();
        if(m.history<0||m.history>100000000||
           m.lastBank < -1||m.lastBank>=BINS||
           m.stalledStreak<0||m.stalledStreak>100000000||
           m.recentHarmBand < -1||m.recentHarmBand>=BANDS||
           !Float.isFinite(m.recentHarmTime)||m.recentHarmTime<0||m.recentHarmTime>4.5f)
            throw new IOException("V371 invalid recent-life context");
        for(int i=0;i<TRACE;i++){
            for(int p=0;p<BANDS*8;p++){
                m.recentSight[i][p]=d.readFloat();
                if(!Float.isFinite(m.recentSight[i][p])||
                   m.recentSight[i][p]<0f||m.recentSight[i][p]>1f)
                    throw new IOException("V371 impossible visual history");
            }
            m.recentDX[i]=d.readFloat();m.recentDY[i]=d.readFloat();
            m.recentBank[i]=d.readInt();
            if(!Float.isFinite(m.recentDX[i])||!Float.isFinite(m.recentDY[i])||
               Math.abs(m.recentDX[i])>120||Math.abs(m.recentDY[i])>120||
               m.recentBank[i]<0||m.recentBank[i]>=BINS)
                throw new IOException("V371 impossible physical history");
        }
        if(d.available()>0)m.inertia=Zero3InertialResponseV377.read(d);
        return m;
    }
}
