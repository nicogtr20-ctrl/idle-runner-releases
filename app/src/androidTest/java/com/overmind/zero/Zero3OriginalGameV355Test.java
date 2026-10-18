package com.overmind.zero;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.SystemClock;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;

/**
 * Regression against accidentally replacing the original game with a
 * stripped-down V3 dashboard. Runs on the SAME signed-candidate UI code.
 * Never opens or mutates any historical 2.2 profile.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero3OriginalGameV355Test {
    @Test public void originalGameCanvasStartsAndResumesCompletedGru()throws Exception{
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        if(!context.getResources().getBoolean(R.bool.zero3_candidate))return;
        SharedPreferences chosen=context.getSharedPreferences("zero3-ui-v354",0);
        int previous=chosen.getInt("slot",1);
        // Isolate from the V354 settings and cognition instrumentation in slot 1.
        assertTrue(chosen.edit().putInt("slot",12).commit());
        try{
            Intent intent=new Intent(context,GameActivity.class);
            intent.putExtra("zero3_qa_pause_v355",true);
            try(ActivityScenario<GameActivity> scenario=ActivityScenario.launch(intent)){
                AtomicBoolean real=new AtomicBoolean();
                AtomicInteger count=new AtomicInteger();
                scenario.onActivity(activity->{
                    real.set(activity.originalZero3OwnerForQA());
                    count.set(activity.originalZero3DecisionCountForQA());
                    assertEquals(GameActivity.class,activity.getClass());
                    assertTrue((activity.getWindow().getAttributes().flags&1024)!=0);
                });
                assertTrue("Original fullscreen painter must host GRU3, not V2",real.get());
                assertTrue("Birth must be physically completed and checkpointed",count.get()>=1);
                File qa=new File(context.getFilesDir(),"qa");assertTrue(qa.isDirectory()||qa.mkdirs());
                Bitmap screenshot=InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
                assertNotNull(screenshot);
                try(FileOutputStream out=new FileOutputStream(new File(qa,"zero3-original-game.png"))){
                    assertTrue(screenshot.compress(Bitmap.CompressFormat.PNG,100,out));
                }finally{screenshot.recycle();}
                scenario.recreate();
                scenario.onActivity(activity->{
                    assertTrue("Cold-resumed original canvas must still own V3",activity.originalZero3OwnerForQA());
                    assertTrue(activity.originalZero3DecisionCountForQA()>=count.get());
                });
            }
        }finally{
            assertTrue(chosen.edit().putInt("slot",previous).commit());
        }
    }
}
