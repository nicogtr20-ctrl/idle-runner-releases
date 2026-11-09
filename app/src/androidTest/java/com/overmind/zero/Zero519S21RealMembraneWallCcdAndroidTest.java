package com.overmind.zero;
import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Locale;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** V519: critical V518 replay omission. Compare real 94-wall
 * membrane/particle CCD in original saved S21 training scene against
 * identical replay lacking only outer membrane wall constraints.
 * Same genuine 256 GRU-issued headings, same original 33312-trained
 * cortex and saved 170-site material state, no arbitrary motor targets.
 * Never claim this open-loop replay certifies autonomous competence. */
@RunWith(AndroidJUnit4.class)
public final class Zero519S21RealMembraneWallCcdAndroidTest {
    @Test(timeout=5400000)
    public void savedS21MeasurePreviouslyOmittedExternalMembraneCollision()
            throws Exception {
        JSONObject fixture=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        String saved=fixture.getString("training_room_1_scene_v379");
        GameSceneSnapshot.Scene initial=GameSceneSnapshot.workingPosture(saved);
        assertNotNull(initial);
        int walls=0;
        for(UnknownWorld.Entity e:initial.objects)
            if(e.isTrainingWall()&&!e.consumed)walls++;
        assertEquals("Exact original S21 94 physical walls",94,walls);
        Zero501RecordedSequentialMotorAndroidTest.Sample[] trace=
            Zero501RecordedSequentialMotorAndroidTest.originalFlight(
                new JSONObject().put("motorRecorderCsv",
                    fixture.getString("motorRecorderCsv")));
        assertEquals(256,trace.length);
        double measuredDelta=Double.NaN;
        for(int mode:new int[]{0,14,22}){
            Zero501RecordedSequentialMotorAndroidTest.Result incomplete=
                Zero518ActualTrainedS21PhysicalResponseAndroidTest.replay(
                    fixture,trace,saved,mode,false);
            Zero501RecordedSequentialMotorAndroidTest.Result full=
                Zero518ActualTrainedS21PhysicalResponseAndroidTest.replay(
                    fixture,trace,saved,mode,true);
            assertEquals(256,full.decisions);
            Log.i("ZERO519_WALL",String.format(Locale.US,
                "realS21NeuralCommands=256 wallTiles=%d mode=%d "+
                "interiorOnlyEff=%.7f fullOuterMembraneCcdEff=%.7f "+
                "delta=%.7f interiorSigned=%.5f fullSigned=%.5f "+
                "interiorGross=%.5f fullGross=%.5f "+
                "fullAligned=%d fullOpposed=%d",
                walls,mode,incomplete.efficiency(),full.efficiency(),
                full.efficiency()-incomplete.efficiency(),
                incomplete.signed,full.signed,incomplete.gross,
                full.gross,full.aligned,full.opposed));
            if(mode==0)measuredDelta=full.efficiency()-incomplete.efficiency();
        }
        assertTrue("Real membrane collision comparison must be finite",
            Double.isFinite(measuredDelta));
        Log.i("ZERO519_WALL",String.format(Locale.US,
            "EXACT_S21_REAL_94_WALL_MEMBRANE_CCD_AB_COMPLETE "+
            "baselineDelta=%.7f honestOpenLoopReplayOnly=true "+
            "autonomousNavigationUnproved=true",measuredDelta));
    }
}
