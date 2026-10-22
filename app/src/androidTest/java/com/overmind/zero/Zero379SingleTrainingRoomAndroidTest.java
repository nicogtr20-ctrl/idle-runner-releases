package com.overmind.zero;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.Assert.*;

/** Genuine edit/vision/collision/checkpoint test; never injects a training goal
 * or a reward into ZERO's actual V3 neural owner. */
@RunWith(AndroidJUnit4.class)
public final class Zero379SingleTrainingRoomAndroidTest {
    @Test public void editableLayoutRoundTripAndPhysicalFixedWall(){
        TrainingRoomV379 room=new TrainingRoomV379();
        assertTrue(room.place(TrainingRoomV379.BLUE,-100,96));
        assertTrue(room.place(TrainingRoomV379.RED,100,96));
        assertTrue(room.place(TrainingRoomV379.PURPLE,-108,-96));
        assertTrue(room.place(TrainingRoomV379.BOX,96,-96));
        assertTrue(room.place(TrainingRoomV379.WALL,144,0));
        assertFalse(room.place(TrainingRoomV379.WALL,145,1)); // same tile
        assertEquals(5,room.size());
        TrainingRoomV379 loaded=TrainingRoomV379.decode(room.encode());
        assertEquals(room.encode(),loaded.encode());
        assertTrue(loaded.place(TrainingRoomV379.ERASER,144,0));
        assertEquals(4,loaded.size());
        assertTrue(loaded.newEntities().size()>90); // solid perimeter
        ArrayList<UnknownWorld.Entity> walls=new ArrayList<>();
        UnknownWorld.Entity wall=TrainingRoomV379.wall(50,0);
        walls.add(wall);
        assertTrue(wall.isTrainingWall());
        assertFalse(wall.isBox());
        float sight=SensorimotorMind.Senses.firstSolidDistance(null,walls,
            0,0,1,0,154f);
        assertEquals(38f,sight,.01f); // x=50, near wall face=38
        assertEquals(0f,SensorimotorMind.Senses.spectral8(
            new MotorBrain.Body(),null,walls)[16],.0001f);
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        wall=TrainingRoomV379.wall(skin.x[0],skin.y[0]);
        walls.clear();walls.add(wall);
        TrainingRoomV379.constrain(body,skin,walls);
        assertTrue("Actual skin contact, not a virtual hit",skin.touchPulse[0]>0);
        assertEquals("Wall does not move",wall.spawnX,wall.x,0f);
        assertEquals("Wall does not move",wall.spawnY,wall.y,0f);
    }
    @Test(timeout=180000)
    public void realOriginalGameUsesTheSameBrainAndPreservesFreeScene(){
        Instrumentation android=InstrumentationRegistry.getInstrumentation();
        Context context=android.getTargetContext();
        SharedPreferences prefs=context.getSharedPreferences("zero_motorbrain_001",0);
        assertTrue(prefs.edit().clear()
            .putBoolean(SymbioteMatterV1.ENABLED,true).commit());
        Instrumentation.ActivityMonitor monitor=android.addMonitor(
            GameActivity.class.getName(),null,false);
        GameActivity game=null;
        try{
            Intent launch=new Intent(context,GameActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(launch);
            game=(GameActivity)android.waitForMonitorWithTimeout(monitor,30000);
            assertNotNull("GameActivity must launch",game);
            GameActivity current=game;
            AtomicLong before=new AtomicLong();
            android.runOnMainSync(()->{
                assertTrue("Neural owner failed to load: "+
                    current.originalV356BrainErrorForQA(),
                    current.originalV356NeuralOwnerForQA());
                before.set(current.originalV356DecisionsForQA());
                current.originalV379EnterTrainingForQA();
            });
            AtomicBoolean active=new AtomicBoolean();
            android.runOnMainSync(()->active.set(current.originalV379TrainingActiveForQA()));
            assertTrue("Training must be entered without creating a new actor",active.get());
            assertTrue(prefs.getBoolean(TrainingRoomV379.ACTIVE_KEY,false));
            String free=prefs.getString(TrainingRoomV379.FREE_SCENE_KEY,"");
            String training=prefs.getString(TrainingRoomV379.SCENE_KEY,"");
            assertTrue("Original physical world must remain separately saved",free.length()>100);
            assertTrue("Training physical body must have own scene",training.length()>100);
            AtomicBoolean added=new AtomicBoolean();
            android.runOnMainSync(()->{
                assertTrue(current.originalV356NeuralOwnerForQA());
                assertEquals(before.get(),current.originalV356DecisionsForQA());
                added.set(current.originalV379PlaceForQA(TrainingRoomV379.BLUE,-96,-96));
                current.originalV379ExitTrainingForQA();
            });
            assertTrue("Direct touchscreen-editor placement persisted",added.get());
            android.runOnMainSync(()->{
                assertFalse(current.originalV379TrainingActiveForQA());
                assertTrue(current.originalV356NeuralOwnerForQA());
                assertEquals("Same original neural experience counter after the room switch",
                    before.get(),current.originalV356DecisionsForQA());
            });
            assertEquals(free.length()>0,true);
            assertFalse(prefs.getBoolean(TrainingRoomV379.ACTIVE_KEY,true));
            assertTrue(prefs.getString(TrainingRoomV379.LAYOUT_KEY,"").contains("items"));
            assertTrue(prefs.getString("physical_scene_v100","").length()>100);
            assertTrue(prefs.getString(Zero3OriginalBrainV356.KEY,"").length()>1000);
        }finally{
            if(game!=null){GameActivity closing=game;android.runOnMainSync(closing::finish);}
            android.removeMonitor(monitor);
        }
    }
}
