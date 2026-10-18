package com.overmind.zero;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONObject;
import android.util.Log;
import android.util.Base64;
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

            // V357: ORIGINAL episode is NOT a life identifier. It is also
            // incremented whenever extendHabitat streams a new world zone.
            // Force 4 such transitions while a GRU motor action is PENDING.
            // The brain must keep the SAME life, continue training, and NEVER
            // emit "Vita cambiata durante la decisione" or pause.
            for(int zone=0;zone<4;zone++){
                long completedBeforeZone=decisions(game);
                int sameLife=life(game);
                AtomicInteger areaBefore=new AtomicInteger();
                ui(()->areaBefore.set(game.originalV356WorldZoneForQA()));
                AtomicBoolean streamed=new AtomicBoolean();
                eventually("original zone extension during live GRU decision",15000,
                    ()->{
                        ui(()->{if(game.originalV356ExtendZoneDuringActionForQA())
                            streamed.set(true);});
                        return streamed.get();
                    });
                AtomicInteger areaAfter=new AtomicInteger();
                ui(()->areaAfter.set(game.originalV356WorldZoneForQA()));
                assertEquals("original zone counter must increment",areaBefore.get()+1,areaAfter.get());
                assertEquals("zone is not a new life",sameLife,life(game));
                eventually("GRU must continue after streamed original world zone",15000,
                    ()->decisions(game)>completedBeforeZone);
                AtomicReference<String> error=new AtomicReference<>("");
                ui(()->error.set(game.originalV356BrainErrorForQA()));
                assertEquals("V357: no neural auto-pause at original zone streaming","",error.get());
            }
            Log.i(TAG,"V357_FOUR_WORLD_ZONES_WITH_SAME_LIFE=PASS");

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
            eventually("original scene and FINAL V3 generation flushed on real onPause",30000,
                ()->{
                    if(!old.contains("physical_scene_v100"))return false;
                    String saved=old.getString(Zero3OriginalBrainV356.KEY,"");
                    if(saved.length()<1000)return false;
                    try{
                        return Zero3OriginalBrainV356.inspectSaved(saved).decisions>=completed;
                    }catch(Exception unfinished){return false;}
                });
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

    /**
     * V359: hazard is spawned at a REAL patch of the original membrane and
     * loses actual reserve via unmodified UnknownWorld.Entity.touch(-7).
     * No synthetic 'harm=7' label is supplied to the neural controller.
     */
    @Test public void originalRedContactReachesOnlyDamageChannelAndLegacyCheckpointLoads()throws Exception{
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences preferences=context.getSharedPreferences("zero_motorbrain_001",0);
        // Test-only emulator: isolate from the previous instrumentation case.
        assertTrue(preferences.edit().clear()
            .putBoolean(SymbioteMatterV1.ENABLED,true).commit());
        GameActivity game=launch(context);
        try{
            ui(game::originalV359RemovePhysicalRedForQA);
            eventually("a functioning V359 GRU",30000,()->decisions(game)>=2);
            AtomicLong earlierHurt=new AtomicLong();
            ui(()->earlierHurt.set(game.originalV359HarmDecisionsForQA()));
            long earlierDecision=decisions(game);
            AtomicBoolean injected=new AtomicBoolean();
            ui(()->injected.set(game.originalV359SpawnPhysicalRedForQA()));
            assertTrue("must contact ORIGINAL red energy at living patch",injected.get());
            eventually("red contact physically credited as pain, not as muscular fuel",30000,
                ()->{
                    AtomicLong count=new AtomicLong();
                    ui(()->count.set(game.originalV359HarmDecisionsForQA()));
                    return count.get()>earlierHurt.get();
                });
            AtomicReference<Float> pain=new AtomicReference<>(0f);
            ui(()->pain.set(game.originalV359MaxDamageForQA()));
            assertTrue("a genuine original red contact costs seven energy, no invented signal: "+pain.get(),
                pain.get()>=6.99f);
            assertTrue("motor decisions continue after real nociception",
                decisions(game)>=earlierDecision);
            Log.i(TAG,"V359_ORIGINAL_RED_HARM_PHYSICAL=PASS maxDamage="+pain.get());
            ui(game::finish);
            eventually("interoception checkpoint safely saved",30000,
                ()->preferences.getString(Zero3OriginalBrainV356.KEY,"").length()>1000);
            String saved=preferences.getString(Zero3OriginalBrainV356.KEY,"");
            Zero3OriginalBrainV356.Readout modern=Zero3OriginalBrainV356.inspectSaved(saved);
            assertTrue(modern.previousDamageV359>=0f);
            // Reproduce an OTA358 checkpoint by removing the OPTIONAL V359
            // physical-damage float, recomputing its CRC, and confirming
            // older saves are accepted with neutral (zero) missing stimulus.
            byte[] bytes=Base64.decode(saved,Base64.DEFAULT);
            assertTrue("V359 CRC payload includes 4 extra bytes",bytes.length>1000);
            byte[] old=new byte[bytes.length-4];
            System.arraycopy(bytes,0,old,0,old.length-4);
            java.util.zip.CRC32 crc=new java.util.zip.CRC32();
            crc.update(old,0,old.length-4);
            int checksum=(int)crc.getValue();
            for(int i=0;i<4;i++)old[old.length-4+i]=(byte)(checksum>>>(24-8*i));
            String legacy=Base64.encodeToString(old,Base64.NO_WRAP);
            Zero3OriginalBrainV356.Readout oldRead=Zero3OriginalBrainV356.inspectSaved(legacy);
            assertEquals(modern.decisions,oldRead.decisions);
            assertEquals("existing V358 saves lack old pain sensor; no reset",0f,
                oldRead.previousDamageV359,0f);
            Log.i(TAG,"V359_PREVIOUS_DAMAGE_OLD_CHECKPOINT_COMPAT=PASS decisions="+oldRead.decisions);
        }finally{ui(game::finish);}
    }


    private LinearLayout findShareCard(android.view.View root){
        if(root instanceof LinearLayout){
            LinearLayout group=(LinearLayout)root;
            for(int i=0;i<group.getChildCount();i++){
                android.view.View child=group.getChildAt(i);
                if(child instanceof TextView&&
                  ((TextView)child).getText().toString().contains("CONDIVIDI DATI CON GPT")&&
                  group.hasOnClickListeners())return group;
                LinearLayout nested=findShareCard(child);
                if(nested!=null)return nested;
            }
        }else if(root instanceof android.view.ViewGroup){
            android.view.ViewGroup group=(android.view.ViewGroup)root;
            for(int i=0;i<group.getChildCount();i++){
                LinearLayout found=findShareCard(group.getChildAt(i));
                if(found!=null)return found;
            }
        }
        return null;
    }

    @Test public void restoredV3ShareButtonAndGptReportCarryActualBrain()throws Exception{
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences working=context.getSharedPreferences("zero_motorbrain_001",0);
        assertTrue(working.edit().putBoolean(SymbioteMatterV1.ENABLED,true).commit());
        GameActivity game=launch(context);
        try{
            eventually("real GRU decisions for GPT telemetry",30000,()->decisions(game)>=2);
            ui(game::finish);
            eventually("neural profile captured before explicit GPT share",30000,()->{
                try{
                    return Zero3OriginalBrainV356.inspectSaved(
                        working.getString(Zero3OriginalBrainV356.KEY,"")).decisions>=2;
                }catch(Exception incomplete){return false;}
            });
            JSONObject complete=TelemetryGitHub.report(context);
            JSONObject v3=complete.getJSONObject("zero3Summary");
            assertTrue("Actual V3 checkpoint must validate",v3.getBoolean("checkpointValid"));
            assertEquals(194,v3.getInt("sensoryInputs"));
            assertEquals(64,v3.getInt("recurrentUnits"));
            assertEquals(64,v3.getInt("muscleOutputs"));
            assertEquals(16,v3.getInt("adhesionSectors"));
            assertTrue(v3.getLong("completedDecisions")>=2);
            assertEquals("Saved full GRU bytes included for GPT, not just counters",true,
                complete.getJSONObject("currentZero").has(Zero3OriginalBrainV356.KEY));
            assertTrue(complete.getString("gameplayBaseline").contains("ZERO 3.0"));
            assertTrue(complete.getString("predictionDiagnostic").contains("ZERO3"));
            // The actual content summary must survive the multipart index,
            // because GPT can open the compact current.json before shards.
            java.lang.reflect.Method build=TelemetryGitHub.class.getDeclaredMethod(
                "prepare",JSONObject.class);
            build.setAccessible(true);
            Object archive=build.invoke(null,complete);
            java.lang.reflect.Field indexField=archive.getClass().getDeclaredField("index");
            indexField.setAccessible(true);
            JSONObject index=new JSONObject((String)indexField.get(archive));
            assertTrue(index.has("zero3Summary"));
            assertEquals(v3.getLong("completedDecisions"),
                index.getJSONObject("zero3Summary").getLong("completedDecisions"));
            assertTrue(index.getJSONArray("parts").length()>0);
            Log.i(TAG,"V360_GPT_COMPLETE_NEURAL_CHECKPOINT_AND_INDEX=PASS decisions="+
                v3.getLong("completedDecisions"));

            // Launch the REAL old-fashioned original analysis Activity, not a
            // synthetic UI or the discarded V355 dashboard. It MUST show the
            // old missing sharing card in the ACTIVE V3 branch.
            Intent analysis=new Intent(context,BrainToolsActivity.class);
            analysis.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Instrumentation.ActivityMonitor a=android.addMonitor(
                BrainToolsActivity.class.getName(),null,false);
            Activity tools=null;
            try{
                context.startActivity(analysis);
                tools=android.waitForMonitorWithTimeout(a,30000);
                assertNotNull("3.0 analysis Activity",tools);
                final Activity shown=tools;
                java.util.concurrent.atomic.AtomicReference<LinearLayout> card=
                    new java.util.concurrent.atomic.AtomicReference<>();
                ui(()->card.set(findShareCard(shown.getWindow().getDecorView())));
                assertNotNull("V3 share button must be VISIBLE in ANALISI 3.0",card.get());
                Instrumentation.ActivityMonitor t=android.addMonitor(
                    TelemetryActivity.class.getName(),null,false);
                try{
                    ui(()->card.get().performClick());
                    Activity exporter=android.waitForMonitorWithTimeout(t,30000);
                    assertNotNull("V3 share must launch existing consent screen",exporter);
                    assertTrue(exporter instanceof TelemetryActivity);
                    ui(exporter::finish); // Never upload in tests.
                }finally{android.removeMonitor(t);}
                Log.i(TAG,"V360_ACTIVE_V3_ANALYSIS_SHARE_BUTTON_OPENS_GPT=PASS");
            }finally{
                if(tools!=null){final Activity closing=tools;ui(closing::finish);}
                android.removeMonitor(a);
            }
        }finally{ui(game::finish);}
    }

}
