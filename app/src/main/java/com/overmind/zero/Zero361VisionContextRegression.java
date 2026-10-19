package com.overmind.zero;

/** Pure JVM contract against the on-phone executive's real 194-channel input. */
public final class Zero361VisionContextRegression {
    private static void near(float got, float wanted) {
        if (Math.abs(got - wanted) > 0.00001f)
            throw new AssertionError("visual executive mixing: got " + got + " expected " + wanted);
    }
    public static void main(String[] args) {
        float[] x = new float[194];
        x[20 + 0 * 8 + 0] = 0.8f; // frequency 0, azimuth 0
        x[20 + 1 * 8 + 6] = 0.4f; // frequency 1, azimuth 6
        x[20 + 2 * 8 + 3] = 0.2f; // frequency 2, azimuth 3
        x[188] = .65f; x[189] = .4f; x[190] = -.2f;
        x[191] = .4f; x[192] = -.2f;
        float[] c = Zero3VisionContextV361.fromSensory194(x);
        near(c[0], .1f); near(c[1], .05f); near(c[2], .025f);
        near(c[3], (.8f - .4f + .2f) / 24f);
        near(c[8], .65f); near(c[9], .4f);
        near(c[10], -.2f); near(c[11], .1f);
        float[] noLight = new float[194];
        float[] zero = Zero3VisionContextV361.fromSensory194(noLight);
        for (int k = 0; k < 12; k++) near(zero[k], 0f);
        boolean rejected = false;
        try { Zero3VisionContextV361.fromSensory194(new float[24]); }
        catch (IllegalArgumentException expected) { rejected = true; }
        if (!rejected) throw new AssertionError("wrong sensory width accepted");
        // Each of the 24 positions must affect exactly one FREQUENCY aggregate.
        for (int f = 0; f < 3; f++) for (int a = 0; a < 8; a++) {
            float[] single = new float[194];
            single[20 + f * 8 + a] = 1f;
            float[] features = Zero3VisionContextV361.fromSensory194(single);
            for (int k = 0; k < 3; k++) near(features[k], k == f ? .125f : 0f);
        }
        System.out.println("ZERO361_REAL_VISUAL_FREQUENCY_AZIMUTH=PASS");
    }
}
