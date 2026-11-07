package com.overmind.zero;

/**
 * ZERO506: original-GRU3-only anatomical muscle inverse with true physical
 * speed-load regulation, selected by the V501/V503 counterfactual measured
 * *actual phone* 256 self-issued rapid turns. This is no autonomous
 * navigator, goal chooser, innate wave color, COM teleport or 2.2 motor.
 *
 * V503-mode-9 matched 256-decision genuine 170-particle replay:
 * signed/gross +.1625368 (baseline V499 +.03157, old cortex -.09151);
 * peak saved-world speed 17.1492 (V499 20.1995).
 *
 * The one learned 512-synapse cortex is executed on every true frame and
 * retains a bounded physical residual. No duplicate brain or stored state.
 * V319 independent CH2 object grip is never touched. Only actual pre-step
 * material pose and COM velocity steer the physical CH0/1 muscle vectors.
 * This is a candidate requiring original 2x10k S21/wild autonomy gates.
 */
final class Zero506PhysicalPostureSpeedMotor {
    static final int SECTORS=16;
    static final float MAX_DRIVE=.80f;
    static final float NEURAL_RESIDUAL=.12f;
    static final float SPEED_SOFT_LIMIT=5f;
    private static float clip(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }

    static void apply(Zero3LearningFoundation.MotorIntent intent,
            MotorBrain.Body body,Morphology.Surface skin,float[] muscle){
        if(intent==null||body==null||skin==null||muscle==null||
           muscle.length!=SECTORS*4)
            throw new IllegalArgumentException("V506 real original motor input");
        if(!Float.isFinite(intent.x)||!Float.isFinite(intent.y)||
           !Float.isFinite(intent.effort)||!Float.isFinite(body.cvx)||
           !Float.isFinite(body.cvy))
            throw new IllegalArgumentException("V506 nonfinite original body");
        float norm=(float)Math.hypot(intent.x,intent.y);
        if(norm<.9f)
            throw new IllegalArgumentException("V506 original neural heading unavailable");
        float ux=intent.x/norm,uy=intent.y/norm;
        float speed=(float)Math.hypot(body.cvx,body.cvy);
        // Measured speed magnitudes only. Do not replace the intentional
        // direction with the velocity error: V499 first A/B proved the latter
        // caused self-amplifying orthogonal acceleration.
        float governor=1f/(1f+Math.max(0f,speed-SPEED_SOFT_LIMIT)/
            SPEED_SOFT_LIMIT);
        float drive=MAX_DRIVE*clip(intent.effort*2f,0f,1f)*governor;
        int pointsPerSector=Morphology.POINTS/SECTORS;
        for(int sector=0;sector<SECTORS;sector++){
            int at=sector*4;
            float ax=0f,ay=0f;
            for(int k=0;k<pointsPerSector;k++){
                int i=sector*pointsPerSector+k;
                ax+=skin.x[i]-body.cx;
                ay+=skin.y[i]-body.cy;
            }
            float radial=(float)Math.hypot(ax,ay);
            if(radial<.01f){
                muscle[at]=clip(NEURAL_RESIDUAL*muscle[at],-1f,1f);
                muscle[at+1]=clip(NEURAL_RESIDUAL*muscle[at+1],-1f,1f);
                muscle[at+3]=clip(NEURAL_RESIDUAL*muscle[at+3],-1f,1f);
                continue;
            }
            float nx=ax/radial,ny=ay/radial;
            float radialMotor=nx*ux+ny*uy;
            float tangentMotor=-ny*ux+nx*uy;
            // CH0 pressure supplies real posterior floor footholds, CH1
            // tangential traction supplies controlled steering; internal
            // liquid receives equal-and-opposite muscle reaction in the
            // existing SymbioteMatterV1.step() physics.
            muscle[at]=clip(drive*radialMotor+
                NEURAL_RESIDUAL*muscle[at],-1f,1f);
            muscle[at+1]=clip(drive*tangentMotor+
                NEURAL_RESIDUAL*muscle[at+1],-1f,1f);
            muscle[at+3]=clip(NEURAL_RESIDUAL*muscle[at+3],-1f,1f);
            // No touch of CH2 (original independent V319 grip).
        }
    }
    private Zero506PhysicalPostureSpeedMotor(){}
}
