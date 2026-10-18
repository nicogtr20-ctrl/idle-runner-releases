package com.overmind.zero;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.*;

/** Real Android activity + real organism. No mocked diagnostic provider. */
@RunWith(AndroidJUnit4.class)
public final class Zero3UiIntegrationTest {
    private Zero3DashboardSnapshotV354 snapshot(ActivityScenario<Zero3DashboardActivityV354> scenario){
        AtomicReference<Zero3DashboardSnapshotV354> out=new AtomicReference<>();scenario.onActivity(a->out.set(a.snapshotForQA()));return out.get();
    }
    private Zero3DashboardSnapshotV354 await(ActivityScenario<Zero3DashboardActivityV354> scenario,java.util.function.Predicate<Zero3DashboardSnapshotV354> ready){
        long end=SystemClock.uptimeMillis()+30000;
        do{Zero3DashboardSnapshotV354 s=snapshot(scenario);if(s!=null&&ready.test(s))return s;SystemClock.sleep(100);}while(SystemClock.uptimeMillis()<end);
        throw new AssertionError("Real ZERO3 state did not reach the expected boundary");
    }
    private void tap(String text){onView(withText(text)).perform(scrollTo(),click());}
    private void screenshot(String name)throws Exception{
        File directory=new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir(),"qa");directory.mkdirs();
        Bitmap bitmap=InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();assertNotNull(bitmap);
        try(OutputStream out=new FileOutputStream(new File(directory,name+".png"))){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);}bitmap.recycle();
    }
    @Test public void realV3ProfilesTrainingObserverNavigationAndNeurons()throws Exception{
        Intent intent=new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(),Zero3DashboardActivityV354.class);
        try(ActivityScenario<Zero3DashboardActivityV354> scenario=ActivityScenario.launch(intent)){
            tap("Impostazioni");tap("CREA NUOVO CERVELLO NELLO SLOT VUOTO");tap("CREA");
            Zero3DashboardSnapshotV354 birth=await(scenario,s->s.body.decisions>=1);assertEquals(194,birth.usedInput.length);
            tap("Gioco");tap("×20");await(scenario,s->s.body.decisions>=16);tap("PAUSA");
            tap("Cervello");Zero3DashboardSnapshotV354 graph=await(scenario,s->s.graph!=null);
            assertEquals(graph.neurons,graph.graph.nodes.length);assertTrue(graph.graph.actualSynapses>0);assertEquals(96,graph.body.membraneX.length);assertEquals(74,graph.innerX.length);assertEquals(64,graph.executed.length);
            screenshot("zero3-brain");
            long decision=graph.body.decisions;
            for(String page:new String[]{"Panoramica","Corpo","Abilità","Evoluzione","Percezione","Memoria e obiettivi","Analisi","Impostazioni"})tap(page);
            Zero3DashboardSnapshotV354 read=snapshot(scenario);assertEquals(decision,read.body.decisions);
            tap("APPRENDIMENTO / SOLA OSSERVAZIONE");Zero3DashboardSnapshotV354 observer=await(scenario,s->s.observing);
            long trained=observer.trained,validation=observer.heldout,from=observer.body.decisions;
            tap("Gioco");tap("×20");await(scenario,s->s.body.decisions>=from+8);tap("PAUSA");
            tap("Analisi");Zero3DashboardSnapshotV354 after=await(scenario,s->s.body.decisions>=from+8);
            assertEquals(trained,after.trained);assertEquals(validation,after.heldout);assertTrue(after.observations>=8);
            screenshot("zero3-analysis-observer");tap("Gioco");screenshot("zero3-body");
        }
    }
}
