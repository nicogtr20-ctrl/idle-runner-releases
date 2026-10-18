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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.*;

/**
 * V355: the GAME is the ORIGINAL GameActivity; this Activity is ONLY
 * 3.0 cognitive navigation, real brain data, settings and verification.
 * No second game/physics owner is started in this test.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero3UiIntegrationTest {
    private Zero3DashboardSnapshotV354 snapshot(ActivityScenario<Zero3DashboardActivityV354> scenario){
        AtomicReference<Zero3DashboardSnapshotV354> out=new AtomicReference<>();
        scenario.onActivity(activity->out.set(activity.snapshotForQA()));
        return out.get();
    }
    private Zero3DashboardSnapshotV354 waitFor(ActivityScenario<Zero3DashboardActivityV354> scenario,
            java.util.function.Predicate<Zero3DashboardSnapshotV354> ready){
        long end=SystemClock.uptimeMillis()+60000;
        do{
            Zero3DashboardSnapshotV354 s=snapshot(scenario);
            if(s!=null&&ready.test(s))return s;
            SystemClock.sleep(200);
        }while(SystemClock.uptimeMillis()<end);
        throw new AssertionError("ZERO3 neural setting/snapshot was not available within 60 seconds");
    }
    private Zero3DashboardSnapshotV354 settled(ActivityScenario<Zero3DashboardActivityV354> scenario)throws Exception{
        AtomicReference<java.util.concurrent.Future<Zero3DashboardSnapshotV354>> out=new AtomicReference<>();
        scenario.onActivity(a->out.set(a.snapshotAfterOwnerForQA()));
        return out.get().get(45,TimeUnit.SECONDS);
    }
    private void nav(String label){onView(withText(label)).perform(click());}
    private void option(String label){onView(withText(label)).perform(scrollTo(),click());}
    private void screenshot(String name)throws Exception{
        File dir=new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir(),"qa");
        assertTrue(dir.isDirectory()||dir.mkdirs());
        Bitmap img=InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        assertNotNull(img);
        try(OutputStream out=new FileOutputStream(new File(dir,name+".png"))){
            assertTrue(img.compress(Bitmap.CompressFormat.PNG,100,out));
        }finally{img.recycle();}
    }

    @Test public void actualV3BrainAndOnlyOriginalGameUi()throws Exception{
        Intent intent=new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(),
            Zero3DashboardActivityV354.class);
        intent.putExtra("zero3_page",9);
        try(ActivityScenario<Zero3DashboardActivityV354> scenario=ActivityScenario.launch(intent)){
            option("CREA NUOVO CERVELLO NELLO SLOT VUOTO");
            nav("CREA");
            Zero3DashboardSnapshotV354 born=waitFor(scenario,s->s.body.decisions>=1);
            assertEquals(194,born.usedInput.length);
            assertEquals(96,born.body.membraneX.length);
            assertEquals(74,born.innerX.length);
            assertEquals(64,born.executed.length);
            nav("Cervello");
            Zero3DashboardSnapshotV354 graph=settled(scenario);
            assertNotNull("Actual GRU graph",graph.graph);
            assertEquals(graph.neurons,graph.graph.nodes.length);
            assertTrue(graph.graph.actualSynapses>0);
            screenshot("zero3-brain");
            nav("Corpo");waitFor(scenario,s->s.body.decisions==graph.body.decisions);
            screenshot("zero3-body");
            for(String tab:new String[]{"Panoramica","Abilità","Evoluzione",
                    "Percezione","Memoria e obiettivi","Analisi","Impostazioni"})nav(tab);
            option("APPRENDIMENTO / SOLA OSSERVAZIONE");
            Zero3DashboardSnapshotV354 observer=waitFor(scenario,s->s.observing);
            assertEquals(graph.body.decisions,observer.body.decisions);
            long committed=observer.body.decisions;
            scenario.recreate();
            Zero3DashboardSnapshotV354 again=waitFor(scenario,s->s.body.decisions==committed);
            assertTrue("Observation mode persisted",again.observing);
            assertEquals(observer.trained,again.trained);
            assertEquals(observer.heldout,again.heldout);
        }
    }
}
