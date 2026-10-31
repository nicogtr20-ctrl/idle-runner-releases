package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

/** ZERO422 isolated actor coverage proof; REAL physical survival remains
 * mandatory in two separate original game >=10000-decision Android tests. */
@RunWith(AndroidJUnit4.class)
public final class Zero422LearnedHarmCandidateCoverageAndroidTest {
    private static Zero3RecurrentBrain brain(){
        Zero3RecurrentBrain b=new Zero3RecurrentBrain(194,true);
        b.enableCognitionV353();b.enableLearnedHarmCandidateCoverageLabV422();
        return b;
    }
    private static Zero3CognitionV353 cognition(Zero3RecurrentBrain brain)
        throws Exception{
        Field f=Zero3RecurrentBrain.class.getDeclaredField("cognition");
        f.setAccessible(true);return (Zero3CognitionV353)f.get(brain);
    }
    private static int offered(Zero3RecurrentBrain b,float[] x){
        b.choose(x,4L,0L,0L);
        String evidence=b.optionCoverageForQA();
        b.abort();
        String marker="offered=";
        int first=evidence.indexOf(marker)+marker.length();
        assertTrue(first>=marker.length());
        int last=evidence.indexOf('|',first);
        return Integer.parseInt(evidence.substring(first,last));
    }
    private static float[] retina(int band,float strength){
        float[] x=new float[194];x[193]=1f;
        x[20+band*8]=strength;
        return x;
    }
    @Test public void twoActualNegativeMaterialConsequencesEnableOnlyVisualCandidateCoverage()
        throws Exception{
        Zero3RecurrentBrain b=brain();
        float[] harmfulPhoton=retina(1,.88f);
        assertEquals("No innate red hazard identity",3,offered(b,harmfulPhoton));
        cognition(b).materialContactV374(1,new float[24],-7f,false);
        assertEquals("One sample is insufficient: use existing V369 evidence",
            3,offered(b,harmfulPhoton));
        cognition(b).materialContactV374(1,new float[24],-7f,false);
        assertEquals("Existing GRU now has more options to judge itself",
            7,offered(b,harmfulPhoton));
        assertTrue(b.learnedHarmCoverageDiagnosticsV422().contains("expanded=1"));
        assertEquals("Never open wide candidate bank without current photons",
            3,offered(b,retina(2,.88f)));
    }
    @Test public void positiveMaterialDoesNotTriggerDangerExploration()
        throws Exception{
        Zero3RecurrentBrain b=brain();
        cognition(b).materialContactV374(2,new float[24],7f,false);
        cognition(b).materialContactV374(2,new float[24],7f,false);
        assertEquals(3,offered(b,retina(2,.80f)));
        assertTrue(b.learnedHarmCoverageDiagnosticsV422().contains("expanded=0"));
    }
    @Test public void legacyOriginalDecisionBankUnchangedWhenOptInDisabled()
        throws Exception{
        Zero3RecurrentBrain b=new Zero3RecurrentBrain(194,true);
        b.enableCognitionV353();
        cognition(b).materialContactV374(1,new float[24],-7f,false);
        cognition(b).materialContactV374(1,new float[24],-7f,false);
        assertEquals("V417 comparator is unchanged without experimental opt-in",
            3,offered(b,retina(1,.95f)));
    }
}
