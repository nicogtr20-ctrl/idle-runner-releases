package com.overmind.zero;

/**
 * V426 research-only score of the ACTUAL independently sampled heading option
 * set, not of the single candidate selected by a learned/planned argmax.
 * sample() is 0.20 * (sum of six U[0,1) - 3), not Gaussian sigma=.20.
 * Score-function derivative is for the latent sampled vector; clipping and
 * normalization are downstream deterministic transformations.
 * This is NOT a steering policy or a synthetic reward.
 */
final class Zero426JointSampleActorScore {
    private Zero426JointSampleActorScore(){}
    static float latentScore(float deliveredNoise,float scale){
        if(!Float.isFinite(deliveredNoise)||!Float.isFinite(scale)||scale<=0f)
            throw new IllegalArgumentException("V426 finite sampled noise/scale");
        if(deliveredNoise==0f)return 0f;
        double width=.20d*scale;
        double t=3d-Math.abs(deliveredNoise)/width;
        // The exact 6-uniform Irwin-Hall density is zero at the endpoints.
        // 24-bit PRNG makes the endpoints vanishingly rare; bound the score.
        if(t<=1e-7d)return Math.copySign(3f,deliveredNoise);
        if(t>3d+1e-5d)throw new IllegalArgumentException("V426 noise outside actual sampler");
        t=Math.min(3d,t);
        double a=Math.max(0d,t-1d),b=Math.max(0d,t-2d);
        double f=t*t*t*t*t-6d*Math.pow(a,5)+15d*Math.pow(b,5);
        double fp=5d*t*t*t*t-30d*Math.pow(a,4)+75d*Math.pow(b,4);
        if(!(f>0d)||!Double.isFinite(f)||!Double.isFinite(fp))
            return Math.copySign(3f,deliveredNoise);
        double score=Math.signum(deliveredNoise)*fp/f/width;
        return (float)Math.max(-3d,Math.min(3d,score));
    }
    static float[] headingScore(float[][] noise,float[][] scale,
                                boolean overwrittenUniformHeadings){
        if(noise==null||scale==null||noise.length!=scale.length||noise.length<1)
            throw new IllegalArgumentException("V426 candidate set");
        float[] result=new float[2];
        for(int k=0;k<noise.length;k++){
            if(noise[k]==null||scale[k]==null||noise[k].length<2||scale[k].length<2)
                throw new IllegalArgumentException("V426 candidate shape");
            // Embodied impasse proposals k>=3 replace the two sampled
            // heading coordinates with a uniform independent angle.
            if(overwrittenUniformHeadings&&k>=3)continue;
            for(int j=0;j<2;j++)result[j]+=latentScore(noise[k][j],scale[k][j]);
        }
        result[0]=Math.max(-3f,Math.min(3f,result[0]));
        result[1]=Math.max(-3f,Math.min(3f,result[1]));
        return result;
    }
}
