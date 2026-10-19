package com.overmind.zero;

/**
 * V367 physical 2D source-bearing context for existing executive V354.
 * PRE194 spectral rays: [frequency][8 azimuths]. There is NO hidden object
 * location, class label, goal or action in this representation.
 *
 * First 15 entries are bitwise the old V366 layout to migrate saved options.
 * Last 3 are measured Y projection for each source wavelength. Before this,
 * blue NORTH/red SOUTH and the reverse aliased at the executive even though
 * the single GRU actor already received different raw 194 sensory vectors.
 */
final class Zero3VisionContextV367 {
    static final int PRIOR=15, EXTENDED=18;
    private Zero3VisionContextV367(){}
    static float[] fromSensory194(float[] senses) {
        float[] result=new float[EXTENDED];
        System.arraycopy(Zero3VisionContextV366.fromSensory194(senses),
            0,result,0,PRIOR);
        for(int spectrum=0;spectrum<3;spectrum++)
            for(int az=0;az<8;az++)
                result[PRIOR+spectrum]+=senses[20+spectrum*8+az]*
                    (float)Math.sin(az*Math.PI/4)/8f;
        for(float v:result)if(!Float.isFinite(v))
            throw new IllegalArgumentException("V367 nonfinite real spectral bearing");
        return result;
    }
}
