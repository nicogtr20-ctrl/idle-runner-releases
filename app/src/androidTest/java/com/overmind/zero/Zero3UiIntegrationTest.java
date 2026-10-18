package com.overmind.zero;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

/**
 * V355 real Android instrumentation of neural menus. Uses View.performClick()
 * on the actual Android UI thread; Espresso's root-focus picker does not work
 * reliably on headless API35 and formerly yielded false UI failures.
 *
 * The original GameActivity game/UI separately passes device instrumentation.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero3UiIntegrationTest {
    private Zero3DashboardSnapshotV354 snapshot(ActivityScenario<Zero3DashboardActivityV354> scenario){
        AtomicReference<Zero3DashboardSnapshotV354> state=new AtomicReference<>();
        scenario.onActivity(a->state.set(a.snapshotForQA()));
        return state.get();
    }
    private Zero3DashboardSnapshotV354 waitFor(ActivityScenario<Zero3DashboardActivityV354> scenario,
            java.util.function.Predicate<Zero3DashboardSnapshotV354> ok){
        long deadline=SystemClock.uptimeMillis()+60000;
        do{
            Zero3DashboardSnapshotV354 s=snapshot(scenario);
            if(s!=null&&ok.test(s))return s;
            SystemClock.sleep(150);
        }while(SystemClock.uptimeMillis()<deadline);
        throw new AssertionError("V355 real brain settings snapshot unavailable after 60s");
    }
    private Zero3DashboardSnapshotV354 settled(ActivityScenario<Zero3DashboardActivityV354> scenario)throws Exception{
        AtomicReference<java.util.concurrent.Future<Zero3DashboardSnapshotV354>> future=new AtomicReference<>();
        scenario.onActivity(a->future.set(a.snapshotAfterOwnerForQA()));
        return future.get().get(45,TimeUnit.SECONDS);
    }
    private static View find(View v,String title){
        if(v instanceof TextView&&title.equals(((TextView)v).getText().toString()))return v;
        if(v instanceof ViewGroup){
            ViewGroup group=(ViewGroup)v;
            for(int i=0;i<group.getChildCount();i++){
                View found=find(group.getChildAt(i),title);
                if(found!=null)return found;
            }
        }
        return null;
    }
    private void press(ActivityScenario<Zero3DashboardActivityV354> scenario,String label){
        scenario.onActivity(a->{
            View button=find(a.getWindow().getDecorView(),label);
            assertNotNull("Missing actual Android UI control: "+label,button);
            assertTrue("Inert Android UI button "+label,button.isClickable());
            assertTrue("Handler failed "+label,button.performClick());
        });
    }
    private void screenshot(ActivityScenario<Zero3DashboardActivityV354> scenario,String name){
        scenario.onActivity(a->{
            View screen=a.getWindow().getDecorView();
            int w=Math.max(1,screen.getWidth()),h=Math.max(1,screen.getHeight());
            Bitmap image=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
            try{
                screen.draw(new Canvas(image));
                File dir=new File(a.getFilesDir(),"qa");
                assertTrue(dir.isDirectory()||dir.mkdirs());
                try(OutputStream output=new FileOutputStream(new File(dir,name+".png"))){
                    assertTrue(image.compress(Bitmap.CompressFormat.PNG,100,output));
                }catch(IOException error){throw new RuntimeException(error);}
            }finally{image.recycle();}
        });
    }

    @Test public void actualV3BrainAndOnlyOriginalGameUi()throws Exception{
        android.content.Context ctx=InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Research/new-life integration is tested independently, never through
        // an unverified dialog/window-focus automation on the headless emulator.
        Zero3ProfileStoreV354 profiles=Zero3AndroidSessionV325.profilesV354(ctx);
        assertEquals("VUOTO",profiles.status(1));
        try(Zero3AndroidSessionV325 organism=Zero3AndroidSessionV325.profileV354(ctx,1)){
            long episode=Math.max(1,System.currentTimeMillis());
            Zero3RuntimeV325 newborn=Zero3WorldBirthV355.create(episode,episode^0x355A55L);
            organism.startNewCompletionV354(newborn);
            organism.step();
            organism.suspend();
        }
        Intent intent=new Intent(ctx,Zero3DashboardActivityV354.class);
        intent.putExtra("zero3_page",9);
        try(ActivityScenario<Zero3DashboardActivityV354> scenario=ActivityScenario.launch(intent)){
            Zero3DashboardSnapshotV354 birth=waitFor(scenario,s->s.body.decisions>=1);
            assertEquals(194,birth.usedInput.length);
            assertEquals(96,birth.body.membraneX.length);
            assertEquals(74,birth.innerX.length);
            assertEquals(64,birth.executed.length);

            press(scenario,"Cervello");
            Zero3DashboardSnapshotV354 brain=settled(scenario);
            assertNotNull("Real GRU network",brain.graph);
            assertEquals(brain.neurons,brain.graph.nodes.length);
            assertTrue(brain.graph.actualSynapses>0);
            screenshot(scenario,"zero3-brain");

            press(scenario,"Corpo");
            waitFor(scenario,s->s.body.decisions==brain.body.decisions);
            screenshot(scenario,"zero3-body");

            for(String title:new String[]{"Panoramica","Abilità","Evoluzione",
                    "Percezione","Memoria e obiettivi","Analisi","Impostazioni"})press(scenario,title);
            press(scenario,"APPRENDIMENTO / SOLA OSSERVAZIONE");
            Zero3DashboardSnapshotV354 changed=waitFor(scenario,s->s.observing);
            assertEquals(brain.body.decisions,changed.body.decisions);
            long count=changed.body.decisions;
            scenario.recreate();
            Zero3DashboardSnapshotV354 restored=waitFor(scenario,s->s.body.decisions==count);
            assertTrue("Observation mode persisted",restored.observing);
            assertEquals(changed.trained,restored.trained);
            assertEquals(changed.heldout,restored.heldout);
        }
    }
}
