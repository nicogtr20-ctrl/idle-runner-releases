package com.overmind.zero;
import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** S21 REAL 94-wall tissue and separate real saved free room.
 * No motor override, scripted heading or fake gem in actual autonomous game. */
@RunWith(AndroidJUnit4.class)
public final class Zero520ActualS21TactileChoiceGateAndroidTest {
    @Test public void realS21WallContactAndLearnedHarmProtectedGRUSamples()throws Exception{
        JSONObject fixture=Zero390ActualUserMotorRehabilitationAndroidTest.fixture();
        GameSceneSnapshot.Scene s=GameSceneSnapshot.workingPosture(
            fixture.getString("training_room_1_scene_v379"));
        assertNotNull(s);
        Zero384BodyAffordance m=new Zero384BodyAffordance();
        m.perceive(s.body,s.skin);
        float left=m.liveForwardContactLoadV520(-1f,0f);
        float right=m.liveForwardContactLoadV520(1f,0f);
        Log.i("ZERO520_GEOMETRY","S21 wall-facing left="+left+" free right="+right);
        assertTrue("Genuine S21 wall touch",left>.50f);
        assertTrue("Genuine S21 opposite membrane free",right<.02f);
        Zero520LiveTactileChoiceGate.Audit a=new Zero520LiveTactileChoiceGate.Audit();
        assertEquals(1,Zero520LiveTactileChoiceGate.choose(true,
            new float[]{left,right},new float[]{0f,0f},0,a));
        assertEquals(1L,a.physicalRedirects);
        assertEquals("No independent physically confirmed impasse: no hijack",0,
            Zero520LiveTactileChoiceGate.choose(false,
                new float[]{left,right},new float[]{0f,0f},0,a));
        assertEquals("Must NEVER switch into actually learned negative risk",0,
            Zero520LiveTactileChoiceGate.choose(true,
                new float[]{left,right},new float[]{0f,.80f},0,a));
        GameSceneSnapshot.Scene free=GameSceneSnapshot.workingPosture(
            fixture.getString("training_room_1_free_scene_v379"));
        assertNotNull(free);
        m.perceive(free.body,free.skin);
        assertEquals(0f,m.liveForwardContactLoadV520(-1f,0f),1e-5f);
        assertEquals(0,Zero520LiveTactileChoiceGate.choose(true,
            new float[]{0f,0f},new float[]{0f,0f},0,a));
        Log.i("ZERO520_GEOMETRY",
            "REAL_S21_TACTILE_CHOICE_GEOMETRY_AND_NEGATIVE_HARM_GATES_PASS");
    }
}
