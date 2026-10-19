package com.overmind.zero;

/**
 * V361: projects the exact 3 frequencies x 8 world-azimuth sensory samples
 * into the 12-dimensional executive context. This is a view of REAL vision,
 * not a food label, navigation command, target direction or reward.
 *
 * spectral8() writes index [frequency * 8 + azimuth]. Its former executive
 * reader accidentally used [azimuth * 3 + frequency], scrambling cues.
 */
final class Zero3VisionContextV361 {
    private Zero3VisionContextV361() {}

    static float[] fromSensory194(float[] input) {
        if (input == null || input.length != 194)
            throw new IllegalArgumentException("V361 sensory dimensions");
        float[] c = new float[12];
        for (int frequency = 0; frequency < 3; frequency++) {
            for (int azimuth = 0; azimuth < 8; azimuth++) {
                float photons = input[20 + frequency * 8 + azimuth];
                if (!Float.isFinite(photons))
                    throw new IllegalArgumentException("V361 nonfinite vision");
                c[frequency] += photons / 8f;
                // Preserve the executive's previous scalar hemispheric format;
                // only correct the indexing, not the meaning of its slots.
                c[3] += (azimuth < 4 ? 1f : -1f) * photons / 24f;
            }
        }
        for (int i = 0; i < 4; i++)
            c[4 + i] = (input[i * 2] + input[i * 2 + 1]) * .5f;
        c[8] = input[188];
        c[9] = input[189];
        c[10] = input[190];
        c[11] = (input[191] + input[192]) * .5f;
        for (float v : c)
            if (!Float.isFinite(v)) throw new IllegalArgumentException("V361 nonfinite executive context");
        return c;
    }
}
