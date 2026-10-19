package com.overmind.zero;

/**
 * V366 backwards-compatible executive context expansion.
 * The ORIGINAL 12 V361 dimensions are preserved byte-for-byte, with
 * 3 additional per-wavelength horizontal bearing features.
 *
 * Zero's visual cortex already sees the complete 194-dimensional vector.
 * This solely prevents colour-left/right MIRROR alias in episodic option
 * context. No colours mean good/bad, no direction or reward is issued.
 */
final class Zero3VisionContextV366 {
    static final int LEGACY=12,EXTENDED=15;
    private Zero3VisionContextV366(){}
    static float[] fromSensory194(float[] senses){
        float[] result=new float[EXTENDED];
        float[] old=Zero3VisionContextV361.fromSensory194(senses);
        System.arraycopy(old,0,result,0,LEGACY);
        for(int spectrum=0;spectrum<3;spectrum++){
            for(int az=0;az<8;az++){
                // Physical azimuth 0 is east, 4 west. This cosine is the
                // measured x projection of a light ray, NOT a goal vector.
                result[LEGACY+spectrum]+=senses[20+spectrum*8+az]*
                    (float)Math.cos(az*Math.PI/4)/8f;
            }
        }
        for(float v:result)if(!Float.isFinite(v))
            throw new IllegalArgumentException("Nonfinite V366 existing vision");
        return result;
    }
    static float[] originalV361Padded(float[] senses){
        float[] result=new float[EXTENDED];
        System.arraycopy(Zero3VisionContextV361.fromSensory194(senses),
            0,result,0,LEGACY);
        return result;
    }
}
