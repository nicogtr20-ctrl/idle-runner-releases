package com.overmind.zero;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.View;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.Assert.*;

/**
 * The genuine original game (not a rewritten laboratory or alternate V3
 * world) must own the actual V3 neural actor, preserve its original world,
 * die and reincarnate automatically, persist the SAME working-game profile
 * and recover it on cold activity recreation. No physical reset in normal use.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero3OriginalGameV356Test {
    private long decisions(ActivityScenario<GameActivity> scenario){
        AtomicLong n=new AtomicLong(-1);
        scenario.onActivity(activity->n.set(activity.originalV356DecisionsForQA()));
        return n.get();
    }
    private int episode(ActivityScenario<GameActivity> scenario){
        AtomicInteger n=new AtomicInteger(-1);
        scenario.onActivity(activity->n.set(activity.originalV356LifeForQA()));
        return n.get();
    }
    private void until(long limitMs,java.util.function.BooleanSupplier done)throws Exception{
        long start=SystemClock.uptimeMillis();
        while(SystemClock.uptimeMillis()-start<limitMs){
            if(done.getAsBoolean())return;
            SystemClock.sleep(175);
        }
        throw new AssertionError("Original game + V3 neuronal owner did not progress within "+limitMs+" ms");
    }
    private void capture(ActivityScenario<GameActivity> scenario,String filename){
        scenario.onActivity(activity->{
            View original=activity.getWindow().getDecorView();
            assertTrue(original.getWidth()>0&&original.getHeight()>0);
            Bitmap screenshot=Bitmap.createBitmap(original.getWidth(),
                original.getHeight(),Bitmap.Config.ARGB_8888);
            try{
                original.draw(new Canvas(screenshot));
                File dir=new File(activity.getFilesDir(),"qa");
                assertTrue(dir.isDirectory()||dir.mkdirs());
                try(FileOutputStream stream=new FileOutputStream(new File(dir,filename))){
                    assertTrue(screenshot.compress(Bitmap.CompressFormat.PNG,100,stream));
                }catch(java.io.IOException io){throw new IllegalStateException(io);}
            }finally{screenshot.recycle();}
        });
    }
    @Test public void originalWorldAliveThenDeathAutomaticallyRenewsGru()throws Exception{
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences old=context.getSharedPreferences("zero_motorbrain_001",0);
        // A fresh symbiote on the QA emulator, never touching any user device.
        assertTrue(old.edit().putBoolean(SymbioteMatterV1.ENABLED,true).commit());
        try(ActivityScenario<GameActivity> scenario=ActivityScenario.launch(
                new Intent(context,GameActivity.class))){
            AtomicBoolean actor=new AtomicBoolean();
            scenario.onActivity(activity->{
                actor.set(activity.originalV356NeuralOwnerForQA());
                assertEquals(GameActivity.class,activity.getClass());
                assertTrue("Original fullscreen immersive flag",
                    (activity.getWindow().getAttributes().flags&1024)!=0);
            });
            assertTrue("The original game, not alternate V355 runtime, must own V3",actor.get());
            until(25000,()->decisions(scenario)>=2);
            long before=decisions(scenario);
            int lifetime=episode(scenario);
            assertTrue(lifetime>0);
            capture(scenario,"zero3-v356-original-game.png");
            // Real original reserve death triggers the original resetEpisode;
            // neural weights and completed experience MUST survive.
            scenario.onActivity(GameActivity::originalV356ForceDeathForQA);
            until(25000,()->episode(scenario)>lifetime);
            until(25000,()->decisions(scenario)>before);
            long after=decisions(scenario);
            assertTrue(after>before);
            scenario.recreate();
            until(20000,()->decisions(scenario)>=after);
            assertTrue(old.contains(Zero3OriginalBrainV356.KEY));
            assertTrue(old.getString(Zero3OriginalBrainV356.KEY,"").length()>1000);
            scenario.onActivity(activity->assertTrue(activity.originalV356NeuralOwnerForQA()));
        }
    }
}
