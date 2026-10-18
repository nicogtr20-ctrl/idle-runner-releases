package com.overmind.zero;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

/**
 * A live, continuously redrawn REAL game can never satisfy Espresso's
 * waitForIdle, including ActivityScenario.launch()/onActivity(). Use Android
 * ActivityMonitor with async startActivity and runOnMainSync; these perform
 * actual app lifecycle and physical frames but never demand animation idle.
 * Tests native GameActivity + REAL V3 learning, genuine original death/rebirth,
 * completed neural/physical checkpoint and cold Activity recreation.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero3OriginalGameV356Test {
    private static final String TAG="ZERO3_V356_QA";
    private final Instrumentation android=InstrumentationRegistry.getInstrumentation();
    private void ui(Runnable action){android.runOnMainSync(action);}
    private long decisions(GameActivity game){
        AtomicLong result=new AtomicLong();
        ui(()->result.set(game.originalV356DecisionsForQA()));
        return result.get();
    }
    private int life(GameActivity game){
        AtomicInteger result=new AtomicInteger();
        ui(()->result.set(game.originalV356LifeForQA()));
        return result.get();
    }
    private void eventually(String condition,long timeoutMs,java.util.function.BooleanSupplier ready){
        long end=SystemClock.uptimeMillis()+timeoutMs;
        do{
            if(ready.getAsBoolean())return;
            SystemClock.sleep(180);
        }while(SystemClock.uptimeMillis()<end);
        throw new AssertionError(condition+" not reached after "+timeoutMs+" ms");
    }
    private void capture(GameActivity game,String filename){
        ui(()->{
            View view=game.getWindow().getDecorView();
            assertTrue(view.getWidth()>0&&view.getHeight()>0);
            Bitmap bitmap=Bitmap.createBitmap(view.getWidth(),view.getHeight(),
                Bitmap.Config.ARGB_8888);
            try{
                view.draw(new Canvas(bitmap));
                File dir=new File(game.getFilesDir(),"qa");
                assertTrue(dir.isDirectory()||dir.mkdirs());
                try(FileOutputStream out=new FileOutputStream(new File(dir,filename))){
                    assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG,100,out));
                }catch(java.io.IOException error){throw new RuntimeException(error);}
            }finally{bitmap.recycle();}
        });
    }
    private GameActivity launch(Context context){
        Intent intent=new Intent(context,GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // startActivitySync internally waits for MessageQueue idle, which a
        // 60fps canvas game intentionally never reaches. ActivityMonitor
        // instead observes the actual OS lifecycle without any idle fence.
        Instrumentation.ActivityMonitor monitor=android.addMonitor(
            GameActivity.class.getName(),null,false);
        try{
            context.startActivity(intent);
            android.app.Activity created=android.waitForMonitorWithTimeout(monitor,30000);
            assertNotNull("Original game Activity not launched",created);
            assertTrue("Only original GameActivity may launch",created instanceof GameActivity);
            return (GameActivity)created;
        }finally{
            android.removeMonitor(monitor);
        }
    }
    @Test public void originalWorldAliveThenDeathAutomaticallyRenewsGru()throws Exception{
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences old=context.getSharedPreferences("zero_motorbrain_001",0);
        assertTrue(old.edit().putBoolean(SymbioteMatterV1.ENABLED,true).commit());
        GameActivity game=launch(context);
        try{
            AtomicBoolean owner=new AtomicBoolean();
            ui(()->{
                owner.set(game.originalV356NeuralOwnerForQA());
                assertEquals(GameActivity.class,game.getClass());
                assertTrue((game.getWindow().getAttributes().flags&1024)!=0);
            });
            assertTrue("ONE V3 neural actor over ORIGINAL physics",owner.get());
            eventually("GRU3 decisions in original world",30000,()->decisions(game)>=2);
            long before=decisions(game);int lifeBefore=life(game);
            Log.i(TAG,"ORIGINAL_WORLD_V3_REAL_DECISIONS="+before+",life="+lifeBefore);
            capture(game,"zero3-v356-original-game.png");

            // The real original resolveObjects() owns death and automatically
            // calls its unchanged resetEpisode(); there is no V355 dead end.
            ui(game::originalV356ForceDeathForQA);
            eventually("original life recreation",30000,()->life(game)>lifeBefore);
            eventually("V3 neurons still choosing on inherited life",30000,
                ()->decisions(game)>before);
            long completed=decisions(game);
            Log.i(TAG,"ORIGINAL_REBIRTH_PASS="+life(game)+",decisions="+completed);

            ui(game::finish);
            // Closing the original activity writes physical_scene_v100
            // AND zero3_original_brain_only_v356 in the SAME working profile.
            eventually("original profile persisted",30000,
                ()->old.contains("physical_scene_v100")&&
                  old.getString(Zero3OriginalBrainV356.KEY,"").length()>1000);
            Zero3OriginalBrainV356.Readout read=
                Zero3OriginalBrainV356.inspectSaved(
                    old.getString(Zero3OriginalBrainV356.KEY,""));
            assertTrue(read.decisions>=completed);
            assertEquals(194,read.sensoryInputs);
            assertEquals(64,read.hiddenUnits);
            assertTrue(read.graph.nodes.length>64);
            Log.i(TAG,"NEURAL_CHECKPOINT_VALIDATED="+read.decisions);

            GameActivity resumed=launch(context);
            try{
                eventually("cold activity restored V3",30000,
                    ()->decisions(resumed)>=read.decisions);
                AtomicBoolean ownerRestored=new AtomicBoolean();
                ui(()->ownerRestored.set(resumed.originalV356NeuralOwnerForQA()));
                assertTrue("V3 exclusive neural actor survived cold activity restart",
                    ownerRestored.get());
                Log.i(TAG,"V356_ORIGINAL_APP_LIVES_AND_SAVES=PASS");
            }finally{ui(resumed::finish);}
        }finally{ui(game::finish);}
    }
}
