package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import java.util.Arrays;

/** V434 isolated model invariant; genuine 10k original Android gates are separate. */
@RunWith(AndroidJUnit4.class)
public final class Zero434ObservedDelayedContactAndroidTest {
    private static float[] seen(int band,float speed){
        float[] x=new float[194];
        x[191]=speed/130f;
        x[20+band*8]=1f; // physically sensed wavelength, not an energy label
        return x;
    }
    private static float[] east(){return new float[]{1f,0f,.5f,.5f,.3f};}
    private static void enable(Zero3InstrumentalCreditV371 m){
        m.enableStrictInertialOpticalEvidenceLabV433();
        m.enableObservedDelayedContactWithoutMotorMapLabV434();
    }
    private static void twoMeasuredLinks(Zero3InstrumentalCreditV371 m,
                                         Zero3ContactValenceV369 v,int band,float delta){
        for(int k=0;k<2;k++){
            float[] sight=seen(band,0f);
            // A completed clean actual movement PRECEDES an actual contact.
            m.begin(sight,east(),false);
            m.complete(6f,0f,.30f,true,false);
            m.begin(sight,east(),false);
            m.materialContactV374(Arrays.copyOfRange(sight,20,44),band,delta,false);
            v.materialContact(band,delta);
            // The contact-contaminated motor response is never trained as clean.
            m.complete(0f,0f,.30f,false,false);
        }
    }
    @Test public void noMotorMapAndNoDelayedLinkCannotInventValue(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.materialContact(1,-7f);v.materialContact(1,-7f);
        float[] x=seen(1,12f);
        assertEquals(0f,m.score(x,east(),v),0f);
        enable(m);
        assertEquals(0f,m.score(x,east(),v),0f);
        assertEquals(0,m.verifiedInertialResponseV377());
    }
    @Test public void trueNegativeDelayedContactOnlyScoresExperiencedBank(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        twoMeasuredLinks(m,v,1,-7f);
        float[] x=seen(1,12f);
        assertEquals(0f,m.score(x,east(),v),0f); // V433 comparator
        enable(m);
        assertTrue("real delayed harm must remain negative",m.score(x,east(),v)<-.001f);
        assertEquals(0f,m.score(x,new float[]{-1f,0f},v),0f);
        assertEquals(0f,m.opticalPreferenceV374(x,east(),v),0f);
        assertEquals(0,m.verifiedInertialResponseV377());
    }
    @Test public void truePositiveDelayedContactUsesSameGenericRule(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        twoMeasuredLinks(m,v,2,+7f);
        enable(m);
        assertTrue("real delayed gain must remain positive",m.score(seen(2,12f),east(),v)>.001f);
        assertEquals(0f,m.score(seen(1,12f),east(),v),0f);
    }
    @Test public void heldoutOrUnlinkedContactDoesNotManufactureBankEvidence(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        Zero3ContactValenceV369 v=new Zero3ContactValenceV369();
        v.materialContact(1,-7f);v.materialContact(1,-7f);
        float[] sight=seen(1,0f);
        m.begin(sight,east(),true);
        m.materialContactV374(Arrays.copyOfRange(sight,20,44),1,-7f,true);
        m.complete(0f,0f,.30f,false,true);
        enable(m);
        assertEquals(0f,m.score(seen(1,12f),east(),v),0f);
    }
    @Test public void requiresStrictV433AndNeverChangesMotorPhysics(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        try{m.enableObservedDelayedContactWithoutMotorMapLabV434();
            fail("V434 must require the V433 physical-evidence comparator");
        }catch(IllegalStateException expected){}
        assertEquals(0,m.verifiedInertialResponseV377());
        enable(m);
        assertEquals(0,m.verifiedInertialResponseV377());
    }
}
