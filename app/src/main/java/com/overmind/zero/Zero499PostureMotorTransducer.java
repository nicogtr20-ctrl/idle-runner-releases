package com.overmind.zero;

/**
 * ZERO499 — physical, posture-aware transduction for the ONE original GRU3.
 *
 * The original recurrent brain remains the SOLE source of destination-free
 * heading, voluntary effort, object grip and decision duration.  The original
 * saved 512-weight/64-output cortex still runs on every true 60Hz tissue
 * frame and contributes a small trainable residual.  This low-level tendon
 * transducer maps that SAME requested velocity to the two existing physical
 * membrane muscle axes (radial CH0, tangential CH1); it never changes the
 * decision, touches CH2 object grip, invents energy, or accelerates the COM
 * except through the 170-site SymbioteMatterV1 substrate/material solver.
 *
 * Lab V498 on the user's genuine Nov7 4747-decision original body found the
 * exact radial+tangent projection generated real directional velocity on both
 * failed WEST and NORTH headings. Unlike the unbounded fixed-amplitude
 * experiment, this implementation reduces power near target velocity and
 * brakes real PRE inertia. Absolute speed is NOT taken as proof of safety:
 * 2x10k real S21/wild trials are mandatory before release.
 */
final class Zero499PostureMotorTransducer {
    static final int SECTORS=16, CHANNELS=4;
    static final float RESIDUAL=0.12f;
    static final float MAX_DRIVE=0.80f;
    static final float TARGET_MIN=2.0f, TARGET_PER_EFFORT=4.0f;
    // The membrane has delayed shear and internal momentum. A per-frame
    // 2D velocity-error vector steers the muscle field side-to-side faster
    // than physical tissue can settle (ZERO499 first 4-axis FAIL). Keep
    // ONLY the GRU's requested heading as the low-level muscle frame.
    // Real measured velocity governs FORCE, never switches intention.
    // ZERO503 A/B 256 real phone-issued neural decisions, original 170-site
    // material and all existing saved weights: total COM speed governor at
    // 5 physics units avoids the false extra acceleration when the GRU
    // requests a different heading every 0.45 seconds. Mode 9 yielded
    // +16.25368% signed/gross vs V499 +3.15700%, peak speed 17.1492
    // vs 20.1995. These figures are LAB replay, NOT autonomous success.
    private static final float SPEED_GOVERNOR_ONSET=5f;
    private static final float SPEED_GOVERNOR_WIDTH=5f;

    private static float clip(float x,float lo,float hi) {
        return Math.max(lo,Math.min(hi,x));
    }

    static void apply(Zero3LearningFoundation.MotorIntent intent,
            MotorBrain.Body body,Morphology.Surface skin,float[] motor){
        apply(intent,body,skin,motor,0);
    }
    /** V514 science-only controlled A/B against real opposite and side
     * momentum. Zero keeps the exact V508 production physics unchanged. */
    static float measuredGovernorV514(float vx,float vy,float ux,float uy,int mode){
        if(!Float.isFinite(vx)||!Float.isFinite(vy)||
           !Float.isFinite(ux)||!Float.isFinite(uy))
            throw new IllegalArgumentException("V514 nonfinite physical momentum");
        float speed=(float)Math.hypot(vx,vy);
        float baseline=1f/(1f+Math.max(0f,speed-SPEED_GOVERNOR_ONSET)/
            SPEED_GOVERNOR_WIDTH);
        if(mode==0)return baseline;
        float forward=vx*ux+vy*uy;
        switch(mode){
            case 18:
                return 1f/(1f+Math.max(0f,forward-SPEED_GOVERNOR_ONSET)/
                    SPEED_GOVERNOR_WIDTH);
            case 19: return forward<0f?1f:baseline;
            case 20: return forward<2f?Math.max(.9f,baseline):baseline;
            case 21: return forward<0f?Math.min(1f,baseline*1.8f):baseline;
            default:throw new IllegalArgumentException("V514 unknown measured A/B motor mode");
        }
    }
    static void apply(Zero3LearningFoundation.MotorIntent intent,
            MotorBrain.Body body,Morphology.Surface skin,float[] motor,int governorModeV514){
        if(intent==null||body==null||skin==null||motor==null||
           motor.length!=SECTORS*CHANNELS)
            throw new IllegalArgumentException("ZERO499 real motor anatomy required");
        if(!Float.isFinite(body.cvx)||!Float.isFinite(body.cvy)||
           !Float.isFinite(intent.effort)||!Float.isFinite(intent.x)||
           !Float.isFinite(intent.y))
            throw new IllegalArgumentException("ZERO499 nonfinite physical intention");
        float effort=clip(intent.effort,0f,1f);
        float len=(float)Math.hypot(intent.x,intent.y);
        if(len<.9f)throw new IllegalArgumentException("ZERO499 needs GRU3 heading");
        float ux=intent.x/len,uy=intent.y/len;
        // Corrected from the actual user's rapid neural-choice trace V503:
        // a direction-relative governor did not bound unwanted sideways
        // inertia. Measure REAL 2D COM speed for a scalar force reduction,
        // retaining the original requested GRU heading unmodified.
        float governor=measuredGovernorV514(
            body.cvx,body.cvy,ux,uy,governorModeV514);
        float drive=MAX_DRIVE*clip(2f*effort,0f,1f)*governor;
        // ZERO508: the original material solver applies CH0/CH1 along
        // SymbioteMatterV1.actuatorDirectionAt(), NOT along the radial
        // centroid of its six-site cortex sector. A concave living contour
        // may have a different actual local force basis. Reuse EXACT same
        // pre-step polygon handedness and 96 site normals as the solver.
        // V507 measured in 256 genuinely sampled user GRU decisions:
        // +24.5487% signed/gross vs +16.2537% using centroid radial axes.
        // CH1 is weaker than CH0 in the actual material (210 vs 680);
        // measured 1.75x tangential effort improved actual forward outcomes.
        // No extra action, center thrust, sight or semantic reward is made.
        final float handedness=SymbioteMatterV1.signedArea(skin)>=0f?1f:-1f;
        final float[] actualNormal=new float[2];
        final int pointsPerSector=Morphology.POINTS/SECTORS;
        for(int sector=0;sector<SECTORS;sector++){
            int offset=sector*CHANNELS;
            float nxSum=0f,nySum=0f;
            int start=sector*pointsPerSector;
            for(int i=start;i<start+pointsPerSector;i++){
                SymbioteMatterV1.actuatorDirectionAt(
                    skin,i,body.cx,body.cy,handedness,actualNormal);
                nxSum+=actualNormal[0];
                nySum+=actualNormal[1];
            }
            float length=(float)Math.hypot(nxSum,nySum);
            if(length<.01f){
                // A folded patch with opposing actual material normals
                // cannot transfer a well-defined sector-scale muscle
                // force. Retain learned output only, never invent a ray.
                motor[offset]=clip(RESIDUAL*motor[offset],-1f,1f);
                motor[offset+1]=clip(RESIDUAL*motor[offset+1],-1f,1f);
                motor[offset+3]=clip(RESIDUAL*motor[offset+3],-1f,1f);
                continue;
            }
            float nx=nxSum/length,ny=nySum/length;
            float radialDesired=nx*ux+ny*uy;
            float tangentialDesired=1.75f*(-ny*ux+nx*uy);
            motor[offset]=clip(drive*radialDesired+
                RESIDUAL*motor[offset],-1f,1f);
            motor[offset+1]=clip(drive*tangentialDesired+
                RESIDUAL*motor[offset+1],-1f,1f);
            motor[offset+3]=clip(RESIDUAL*motor[offset+3],-1f,1f);
            // CH2 stays the original V319 physical object-grip command.
        }
    }
    private Zero499PostureMotorTransducer(){}
}
