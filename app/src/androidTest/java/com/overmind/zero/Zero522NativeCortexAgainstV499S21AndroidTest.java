package com.overmind.zero;
import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Locale;
import static org.junit.Assert.*;

/** Exact learned 33312-S21, 170-site world, original 256 neural actions.
 * Compare truly unmodified cortex CH0/1/3 against V499 which uses only
 * 12 percent of those trained outputs. Open-loop physical diagnostic only. */
@RunWith(AndroidJUnit4.class)
public final class Zero522NativeCortexAgainstV499S21AndroidTest {
    @Test(timeout=5400000)
    public void trueTrainedCortexVersusV499OnUserSavedTwoPhysicalRooms()
            throws Exception{
        JSONObject fixture=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        Zero3OriginalBrainV356.Readout brain=
            Zero3OriginalBrainV356.inspectSaved(fixture.getString("brain"));
        assertEquals(33312L,brain.decisions);
        assertEquals(19452L,brain.motorUpdates);
        Zero501RecordedSequentialMotorAndroidTest.Sample[] actual=
            Zero501RecordedSequentialMotorAndroidTest.originalFlight(
                new JSONObject().put("motorRecorderCsv",
                    fixture.getString("motorRecorderCsv")));
        assertEquals(256,actual.length);
        String[] rooms={"training_room_1_scene_v379",
            "training_room_1_free_scene_v379"};
        float[] deltas=new float[2];
        for(int i=0;i<rooms.length;i++){
            String room=rooms[i],raw=fixture.getString(room);
            GameSceneSnapshot.Scene scene=GameSceneSnapshot.workingPosture(raw);
            assertNotNull(scene);
            int walls=0;
            for(UnknownWorld.Entity e:scene.objects)
                if(e.isTrainingWall()&&!e.consumed)walls++;
            assertEquals(i==0?94:0,walls);
            Zero501RecordedSequentialMotorAndroidTest.Result rawCortex=
                Zero518ActualTrainedS21PhysicalResponseAndroidTest.replay(
                    fixture,actual,raw,-1,true);
            Zero501RecordedSequentialMotorAndroidTest.Result override=
                Zero518ActualTrainedS21PhysicalResponseAndroidTest.replay(
                    fixture,actual,raw,0,true);
            assertEquals(256,rawCortex.decisions);
            assertEquals(256,override.decisions);
            deltas[i]=rawCortex.efficiency()-override.efficiency();
            Log.i("ZERO522_CORTEX",String.format(Locale.US,
                "ORIGINAL_33312_S21_CORTEX_MOTOR_AB room=%s walls=%d "+
                "issuedNeuralDecisions=%d nativeEff=%.6f v499Eff=%.6f "+
                "difference=%.6f nativeSigned=%.6f nativeGross=%.6f "+
                "v499Signed=%.6f v499Gross=%.6f nativeAligned=%d "+
                "nativeOpposed=%d v499Aligned=%d v499Opposed=%d",
                room,walls,actual.length,rawCortex.efficiency(),
                override.efficiency(),deltas[i],rawCortex.signed,
                rawCortex.gross,override.signed,override.gross,
                rawCortex.aligned,rawCortex.opposed,
                override.aligned,override.opposed));
        }
        Log.i("ZERO522_CORTEX",String.format(Locale.US,
            "REAL_S21_FULL_512_CORTEX_V499_TWO_ROOM_AB_COMPLETE "+
            "with94WallsGain=%.6f independentFreeGain=%.6f "+
            "autonomousResultUnproven=true",deltas[0],deltas[1]));
        assertTrue(Float.isFinite(deltas[0])&&Float.isFinite(deltas[1]));
    }
}
