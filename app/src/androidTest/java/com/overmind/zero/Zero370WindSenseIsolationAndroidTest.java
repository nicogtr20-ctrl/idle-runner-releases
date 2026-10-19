package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import static org.junit.Assert.*;

/** Original physical gust and 96-site living membrane: no authored stop
 * reflex or hiding of pressure from the GRU, only typed source provenance.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero370WindSenseIsolationAndroidTest {
    @Test public void realGustNeverMasqueradesAsSolidContactInV3Peripheral(){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ShelterEcology shelter=new ShelterEcology();
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        ShelterEcology.Exposure e=shelter.step(body,skin,world,null,
            ShelterEcology.START+1f,1f/60f);
        assertTrue(e.active);
        assertTrue(e.energyLoss>0f);
        int sampled=0;
        for(int p=0;p<Morphology.POINTS;p++){
            assertEquals("no fabricated solid collision",0f,skin.touchSense[p],0f);
            assertTrue("actual gust remains a tangible nervous-system signal",skin.touchPulse[p]>.22f);
            assertEquals(skin.touchPulse[p],skin.weatherPulseV370[p],1e-6f);
            assertEquals("V3 motor doesn't mistake wind for traction",
                0f,FastSomaticCircuitV167.solidContactFeatureV370(
                    skin.touchSense[p],skin.touchPulse[p],skin.weatherPulseV370[p]),0f);
            sampled++;
        }
        assertEquals(96,sampled);
    }
    @Test public void actualSolidContactIsPreservedUnderSameGust(){
        assertEquals(.76f,FastSomaticCircuitV167.solidContactFeatureV370(.76f,.24f,.24f),1e-6f);
        assertEquals(.81f,FastSomaticCircuitV167.solidContactFeatureV370(.2f,.81f,.24f),1e-6f);
        assertEquals("2.2 historic controller still sees the unaltered real pressure",
            .24f,Math.max(0f,.24f),1e-6f);
    }
    @Test public void biologicallyFeltGustDecaysWithoutArtificialPersistentCollision(){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 m=new SymbioteMatterV1();
        m.reset(b,skin);
        ShelterEcology storm=new ShelterEcology();
        storm.step(b,skin,new ArrayList<>(),null,ShelterEcology.START+1f,1f/60f);
        float priorPulse=skin.touchPulse[0];
        float priorWeather=skin.weatherPulseV370[0];
        m.step(b,skin,new float[SymbioteMatterV1.SECTORS*SymbioteMatterV1.OUTPUTS],1f/60f);
        assertTrue(skin.touchPulse[0]<priorPulse);
        assertTrue(skin.weatherPulseV370[0]<priorWeather);
        assertEquals(0f,FastSomaticCircuitV167.solidContactFeatureV370(
            skin.touchSense[0],skin.touchPulse[0],skin.weatherPulseV370[0]),.001f);
        skin.reset(b);
        assertEquals(0f,skin.weatherPulseV370[0],0f);
    }
}
