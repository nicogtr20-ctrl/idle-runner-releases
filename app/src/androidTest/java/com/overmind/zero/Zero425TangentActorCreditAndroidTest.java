package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** Mathematical motor eligibility, NOT an autonomous 10k success fixture. */
@RunWith(AndroidJUnit4.class)
public final class Zero425TangentActorCreditAndroidTest {
    @Test public void radialProbeCannotTrainUnexecutedHeading(){
        float[] g=Zero425TangentActorCredit.realizedHeadingNoise(.2f,0f,.5f,0f);
        assertEquals(0f,g[0],1e-6f);
        assertEquals(0f,g[1],1e-6f);
    }
    @Test public void trueTangentProbeRetainsSignedPhysicalHeadingGradient(){
        float[] g=Zero425TangentActorCredit.realizedHeadingNoise(0f,.2f,.5f,0f);
        assertEquals(0f,g[0],1e-6f);
        assertEquals(.4f,g[1],1e-6f);
        float[] opposite=Zero425TangentActorCredit.realizedHeadingNoise(0f,-.2f,.5f,0f);
        assertEquals(-.4f,opposite[1],1e-6f);
    }
    @Test public void saturatedAxisAndZeroVectorHaveNoInventedDerivative(){
        float[] g=Zero425TangentActorCredit.realizedHeadingNoise(.2f,0f,1.2f,.3f);
        assertEquals(0f,g[0],1e-6f);
        assertEquals(0f,g[1],1e-6f);
        float[] z=Zero425TangentActorCredit.realizedHeadingNoise(.2f,.2f,0f,0f);
        assertEquals(0f,z[0],0f);
        assertEquals(0f,z[1],0f);
    }
}
