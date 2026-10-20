package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V374: RED-near-BLUE must never assign touch outcome to the
 * brightest DIFFERENT entity. All semantic utility comes only from
 * REAL signed reserve deltas at material contact. No hard-coded food
 * direction, negative wavelength or movement script.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero374RealTouchOpticalIdentityAndroidTest {
    static float[] mixed(float a,float b){
        float[] photons=new float[24];
        photons[0*8+4]=a; // an actual observed wavelength WEST
        photons[1*8+0]=b; // a different wavelength EAST
        return photons;
    }
    static float[] full194(){
        float[] x=new float[194];
        float[] p=mixed(.90f,.99f);
        System.arraycopy(p,0,x,20,p.length);
        x[188]=-.30f; // genuine finite reserve
        return x;
    }
    static float[] action(float dx){return new float[]{dx,0f,.6f,.5f,.30f};}

    @Test public void touchedDimmerSourceLearnsItsOwnTrueOutcome(){
        Zero3ContactValenceV369 c=new Zero3ContactValenceV369();
        float[] scene=mixed(.90f,.99f);
        // Existing legacy path did not receive the touched optical identity:
        // it would attribute a positive event to brighter band 1.
        Zero3ContactValenceV369 legacy=new Zero3ContactValenceV369();
        legacy.contact(scene,+22f);
        assertEquals(0,legacy.evidence(0));
        assertEquals(1,legacy.evidence(1));
        c.materialContact(0,+22f);
        c.materialContact(0,+22f);
        c.materialContact(1,-7f);
        c.materialContact(1,-7f);
        assertEquals(2,c.evidence(0));
        assertEquals(2,c.evidence(1));
        assertTrue(c.learnedValue(0)>0);
        assertTrue(c.learnedValue(1)<0);
        assertTrue(c.utility(full194(),-1f,0f)>.10f);
        assertTrue(c.utility(full194(),1f,0f)<-.10f);
    }

    @Test public void neuralOneStepSelectionFollowsActuallyLearnedConsequences(){
        Zero3CognitionV353 cognition=new Zero3CognitionV353();
        float[] scene=mixed(.90f,.99f);
        cognition.materialContactV374(0,scene,+22f,false);
        cognition.materialContactV374(0,scene,+22f,false);
        cognition.materialContactV374(1,scene,-7f,false);
        cognition.materialContactV374(1,scene,-7f,false);
        // Actor proposes EAST first. Neither heading is pre-programmed as
        // good: AFTER observations WEST is the better of both proposals.
        float[][] proposals={action(1),action(-1)};
        assertEquals(1,cognition.select(full194(),proposals));
        assertTrue(cognition.realContactPreferenceV374(full194(),proposals[1])>
                   cognition.realContactPreferenceV374(full194(),proposals[0])+.05f);
        // Mirror physical photon locations: preference reverses.
        float[] swapped=full194();
        swapped[20]=.90f;swapped[24]=0f;
        swapped[28]=0f;swapped[32]=.99f;
        assertEquals(0,cognition.select(swapped,proposals));
    }

    @Test public void actualTouchBandCannotBeFabricatedAndHeldoutCannotTeach(){
        Zero3CognitionV353 cognition=new Zero3CognitionV353();
        float[] scene=mixed(.90f,.99f);
        cognition.materialContactV374(0,scene,+22f,true);
        cognition.materialContactV374(1,scene,-7f,true);
        assertEquals(0f,cognition.contactValenceScoreV369(full194(),-1,0),0f);
        for(int band:new int[]{-1,3}){
            try {
                cognition.materialContactV374(band,scene,-7f,false);
                fail("Invalid material optical frequency accepted");
            }catch(IllegalArgumentException expected){}
        }
    }

    @Test public void mixedSignalsCreditTruePriorApproachesButNotNearbySource(){
        Zero3InstrumentalCreditV371 m=new Zero3InstrumentalCreditV371();
        float[] scene=full194();
        for(int i=0;i<6;i++){
            m.begin(scene,action(-1f),false);
            m.complete(-9f,0f,.3f,true,false);
        }
        m.begin(scene,action(-1f),false);
        m.materialContactV374(mixed(.90f,.99f),0,+22f,false);
        assertEquals(1,m.linkedContacts());
        assertTrue(m.delayedCount(0,4)>0);
        assertEquals("the brighter unrelated red cannot steal a real blue pickup",
                     0,m.delayedCount(1,4));
        m.abort();
        m.begin(scene,action(1f),true);
        m.materialContactV374(mixed(.90f,.99f),1,-7f,true);
        assertEquals(0,m.delayedCount(1,0));
        m.abort();
    }
}
