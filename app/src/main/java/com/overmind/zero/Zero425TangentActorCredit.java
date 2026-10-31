package com.overmind.zero;

/**
 * ZERO425 research only: local Jacobian of the ORIGINAL GRU3 unit-heading
 * actuator, after the existing +/-1 clip and unit-vector normalization.
 * The world receives only a unit direction, never its discarded radial
 * magnitude or pre-clip saturation. No prescribed direction or reward.
 */
final class Zero425TangentActorCredit {
    private Zero425TangentActorCredit(){}
    static float[] realizedHeadingNoise(float noiseX,float noiseY,
                                         float preClipX,float preClipY){
        if(!Float.isFinite(noiseX)||!Float.isFinite(noiseY)||
           !Float.isFinite(preClipX)||!Float.isFinite(preClipY))
            throw new IllegalArgumentException("nonphysical GRU3 actor probe");
        float sx=Math.max(-1f,Math.min(1f,preClipX));
        float sy=Math.max(-1f,Math.min(1f,preClipY));
        float length=(float)Math.hypot(sx,sy);
        if(length<.00001f)return new float[]{0f,0f};
        float ux=sx/length,uy=sy/length;
        // An axis already saturated by the real output clip has zero local
        // derivative. Only the unsaturated perturbation can rotate heading.
        float nx=(preClipX<=-1f||preClipX>=1f)?0f:noiseX;
        float ny=(preClipY<=-1f||preClipY>=1f)?0f:noiseY;
        float radial=nx*ux+ny*uy;
        return new float[]{(nx-radial*ux)/length,(ny-radial*uy)/length};
    }
}
