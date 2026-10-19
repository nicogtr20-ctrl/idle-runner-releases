package com.overmind.zero;
import java.util.Arrays;

/**
 * ZERO3 V366 — READ-ONLY PROOF of the retained V354 context alias.
 * Uses production V361 source; no neural retrain, no world alteration,
 * no color preferences coded into ZERO.
 */
public final class Zero366LeftRightVisionAliasAudit {
    private static void require(boolean t,String message){if(!t)throw new AssertionError(message);}
    private static float[] senseWithTwoSignatures(int blueBearing,int redBearing){
        float[] x=new float[194];
        x[20+0*8+blueBearing]=.8f;
        x[20+1*8+redBearing]=.8f;
        x[188]=1;x[189]=0;x[190]=0;x[191]=0;x[192]=0;
        return x;
    }
    public static void main(String[] argv){
        // World-fixed bearings of the actual production spectral8:
        // 0 EAST/right, 4 WEST/left.
        float[] blueLeftRedRight=senseWithTwoSignatures(4,0);
        float[] blueRightRedLeft=senseWithTwoSignatures(0,4);
        require(!Arrays.equals(blueLeftRedRight,blueRightRedLeft),
            "real 194D scene must distinguish mirrored spectral source positions");
        float[] a=Zero3VisionContextV361.fromSensory194(blueLeftRedRight);
        float[] b=Zero3VisionContextV361.fromSensory194(blueRightRedLeft);
        require(Arrays.equals(a,b),
            "exact executive visual context alias expected from current 12D projection");
        require(a[0]>0&&a[1]>0&&Math.abs(a[3])<1e-6,
            "both wavelength energies present but their opposing azimuths cancel");
        System.out.println("ZERO366_REPRODUCED_DISTINCT_194D_RED_BLUE_MIRRORS_SAME_V354_12D_CONTEXT=PASS");
        System.out.println("blue WEST red EAST -> "+Arrays.toString(a));
        System.out.println("blue EAST red WEST -> "+Arrays.toString(b));
        System.out.println("ZERO366_LIMITATION: GRU still receives uncompressed 194D visual observations, so this is an executive/context alias, NOT proof the entire brain is blind.");
    }
}
