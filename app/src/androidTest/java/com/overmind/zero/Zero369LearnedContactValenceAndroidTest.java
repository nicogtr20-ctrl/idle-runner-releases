package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.*;
import java.util.Arrays;
import static org.junit.Assert.*;

/** Tests true unsupervised wavelength-to-energy association. Fixtures provide
 * photons+signed energy ONLY, never an object class, direction of reward, or
 * actor target. These are unit proofs, NOT blind-world survival certificates. */
@RunWith(AndroidJUnit4.class)
public final class Zero369LearnedContactValenceAndroidTest {
    private static float[] photons(int band,int ray,float bright){
        float[] p=new float[24];p[band*8+ray]=bright;return p;
    }
    private static float[] view(boolean swapSides){
        float[] x=new float[194];
        x[20+(swapSides?4:0)]=.85f; // wavelength 0
        x[28+(swapSides?0:4)]=.85f; // wavelength 1
        return x;
    }
    @Test public void negativeAndPositiveEnergyLearnOnlyFromObservedPhotons(){
        Zero3ContactValenceV369 brain=new Zero3ContactValenceV369();
        float[] a=photons(0,0,.91f),b=photons(1,4,.93f);
        float[] eastWest=view(false);
        assertEquals("No newborn color preference",0f,brain.utility(eastWest,1,0),0);
        assertEquals(0f,brain.utility(eastWest,-1,0),0);
        brain.contact(a,-7f);brain.contact(a,-6f);
        brain.contact(b,22f);brain.contact(b,18f);
        assertEquals(2,brain.evidence(0));assertEquals(2,brain.evidence(1));
        assertTrue(brain.learnedValue(0)<0);
        assertTrue(brain.learnedValue(1)>0);
        assertTrue("real-contact prediction favors learned safe visible bearing",
            brain.utility(eastWest,-1,0)>brain.utility(eastWest,1,0));
        assertTrue("rotated scene must reverse preferred direction",
            brain.utility(view(true),1,0)>brain.utility(view(true),-1,0));
    }
    @Test public void ambiguousContactCannotCauseInventedColorAssociation(){
        Zero3ContactValenceV369 brain=new Zero3ContactValenceV369();
        float[] ambiguous=photons(0,0,.84f);
        ambiguous[8+4]=.82f;
        brain.contact(ambiguous,-7f);
        brain.contact(new float[24],22f);
        assertEquals(0,brain.evidence(0));assertEquals(0,brain.evidence(1));
        assertEquals(2,brain.ambiguous());
        assertEquals(0f,brain.utility(view(false),1,0),0);
    }
    @Test public void oldV353NeuronsLoadAndNewContactMemoriesRoundTrip()throws Exception {
        Zero3CognitionV353 model=new Zero3CognitionV353();
        byte[] full=model.encode();
        // V369 persistence is a genuinely OPTIONAL tail after all prior
        // V353 matrix/evidence bytes. Old bytes are not reinterpreted.
        int appended=4+8+8+3*(4+8)+1+3*(4+4) + 4+8+8+8+8*(4+4+4+4+8+8) + 3*8*(4+4)+5*4+6*(24*4+4+4+4) + 4+16*(4+4+4+4+8+8); // V369+V371 with resumable six PRE/POST memories
        byte[] old=Arrays.copyOf(full,full.length-appended);
        Zero3CognitionV353 restored=Zero3CognitionV353.decode(old);
        assertEquals(0,restored.snapshot().contactAccepted);
        assertEquals(0,restored.snapshot().contactSamples[0]);
        assertArrayEquals("Original V353 checkpoint bytes remain an exact prefix",
            old,Arrays.copyOf(restored.encode(),old.length));
        restored.genuineContactV369(photons(0,0,.95f),-7f,false);
        restored.genuineContactV369(photons(1,4,.95f),22f,false);
        byte[] learned=restored.encode();
        Zero3CognitionV353 after=Zero3CognitionV353.decode(learned);
        assertArrayEquals(learned,after.encode());
        assertEquals(2,after.snapshot().contactAccepted);
        assertTrue(after.snapshot().contactValues[0]<0f);
        assertTrue(after.snapshot().contactValues[1]>0f);
    }
    @Test public void heldoutContactsDoNotTrainAssociations(){
        Zero3CognitionV353 model=new Zero3CognitionV353();
        model.genuineContactV369(photons(0,0,.9f),-7f,true);
        assertEquals(0,model.snapshot().contactSamples[0]);
        model.genuineContactV369(photons(0,0,.9f),-7f,false);
        assertEquals(1,model.snapshot().contactSamples[0]);
    }
}
