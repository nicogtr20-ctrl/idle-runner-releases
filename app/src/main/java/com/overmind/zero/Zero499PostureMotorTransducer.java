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
        float speed=(float)Math.hypot(body.cvx,body.cvy);
        float over=Math.max(0f,speed-SPEED_GOVERNOR_ONSET);
        float governor=1f/(1f+over/SPEED_GOVERNOR_WIDTH);
        float drive=MAX_DRIVE*clip(2f*effort,0f,1f)*governor;
        float rx=ux,ry=uy;
        for(int sector=0;sector<SECTORS;sector++){
            int offset=sector*CHANNELS;
            float dx=0f,dy=0f;
            int start=sector*(Morphology.POINTS/SECTORS);
            for(int i=start;i<start+Morphology.POINTS/SECTORS;i++){
                dx+=skin.x[i]-body.cx;
                dy+=skin.y[i]-body.cy;
            }
            float radial=(float)Math.hypot(dx,dy);
            if(radial<.01f){
                // Degenerate membrane patch: no synthesized world direction
                // can be justified. Keep original learned output only.
                motor[offset]=clip(RESIDUAL*motor[offset],-1f,1f);
                motor[offset+1]=clip(RESIDUAL*motor[offset+1],-1f,1f);
                motor[offset+3]=clip(RESIDUAL*motor[offset+3],-1f,1f);
                continue;
            }
            float nx=dx/radial,ny=dy/radial;
            float radialDesired=nx*rx+ny*ry;
            float tangentDesired=-ny*rx+nx*ry;
            // CH0 inward force ALSO increases real ground loading; CH1
            // is the existing tangential muscle. Nothing bypasses real
            // equal-and-opposite fluid force, friction or grip.
            motor[offset]=clip(drive*radialDesired+
                RESIDUAL*motor[offset],-1f,1f);
            motor[offset+1]=clip(drive*tangentDesired+
                RESIDUAL*motor[offset+1],-1f,1f);
            // CH3 is a neural stiffness residual. Physical neutral stiffness
            // already exists; large cortical random tension can block stance.
            motor[offset+3]=clip(RESIDUAL*motor[offset+3],-1f,1f);
            // NEVER touch CH2; V319 owns the externally applied physical grip.
        }
    }
    private Zero499PostureMotorTransducer(){}
}
