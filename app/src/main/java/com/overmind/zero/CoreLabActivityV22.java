package com.overmind.zero;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Build;
import android.os.PowerManager;
import android.os.Process;
import android.widget.*;
import java.util.*;

/** Isolated phone validation. Only writes the separate report preferences. */
public final class CoreLabActivityV22 extends Activity {
    private volatile boolean canceled,running;
    private Thread worker;
    private long thermalCheck;
    private TextView status,report;
    private Button run;
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);ScrollView scroll=new ScrollView(this);
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(30,40,30,30);
        root.setBackgroundColor(0xff07121d);scroll.addView(root);setContentView(scroll);
        text(root,"ZERO 2.2 • CONFRONTO ESTESO",21);
        text(root,"Dodici coppie dello stesso profilo salvato negli stessi ambienti. Controllo precedente e nucleo attivo: energia, danni, sopravvivenza e prese fisiche. Ogni prova arriva fino a 170 secondi simulati o alla morte. Le copie imparano localmente; il tuo ZERO resta invariato.",15);
        text(root,"Arena ridotta: casse, lastre e pericoli reali, senza streaming o tempeste. Lo screening può mostrare anche regressioni. Nessun pareggio o singola vittoria certifica intelligenza generale.",14);
        run=new Button(this);run.setText("AVVIA 24 PROVE ISOLATE");root.addView(run);
        Button stop=new Button(this);stop.setText("INTERROMPI");root.addView(stop);
        status=text(root,"Pronto",15);
        report=text(root,getSharedPreferences(CoreComparisonV22.RESULTS,MODE_PRIVATE).getString("last","Nessun report."),13);
        run.setOnClickListener(v->start());stop.setOnClickListener(v->{canceled=true;status.setText("Interruzione richiesta");});
        Button back=new Button(this);back.setText("TORNA AD ANALISI");back.setOnClickListener(v->finish());root.addView(back);
    }
    private TextView text(LinearLayout root,String value,int size){TextView v=new TextView(this);v.setText(value);v.setTextColor(0xffdffff4);v.setTextSize(size);v.setPadding(0,14,0,14);v.setTextIsSelectable(true);root.addView(v);return v;}
    private boolean stopped(){
        if(canceled||Thread.currentThread().isInterrupted())return true;
        long now=System.nanoTime();
        if(Build.VERSION.SDK_INT>=29&&now-thermalCheck>2000000000L){
            thermalCheck=now;PowerManager power=(PowerManager)getSystemService(Context.POWER_SERVICE);
            if(power!=null&&power.getCurrentThermalStatus()>=PowerManager.THERMAL_STATUS_SEVERE)canceled=true;
        }
        return canceled;
    }
    private void start(){
        if(running)return;
        final CoreComparisonV22.Profile p;
        try{p=CoreComparisonV22.capture(new LinkedHashMap<>(getSharedPreferences(SaveSlotStore.WORKING,MODE_PRIVATE).getAll()));}
        catch(RuntimeException invalid){status.setText("Profilo non valido: torna al gioco e salva.");return;}
        running=true;canceled=false;thermalCheck=0;run.setEnabled(false);status.setText("Preparazione...");
        worker=new Thread(()->{
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try{
                CoreComparisonV22.Result result=CoreComparisonV22.run(p,(done,total,label)->runOnUiThread(()->{
                    if(!isFinishing()&&!isDestroyed())status.setText(done+" / "+total+" • "+label);
                }),this::stopped);
                String text=result.report();boolean complete=result.finished==result.total()&&!canceled;
                if(complete)getSharedPreferences(CoreComparisonV22.RESULTS,MODE_PRIVATE).edit().putString("last",text).putLong("at",System.currentTimeMillis()).apply();
                runOnUiThread(()->{if(!isFinishing()&&!isDestroyed()){report.setText(text);status.setText(complete?"Completato. Report incluso in Condividi dati.":"Interrotto. Report parziale non salvato.");}});
            }catch(Exception error){runOnUiThread(()->{if(!isFinishing()&&!isDestroyed())status.setText("Prova interrotta: "+error.getClass().getSimpleName());});}
            finally{running=false;runOnUiThread(()->{if(!isFinishing()&&!isDestroyed())run.setEnabled(true);});}
        },"zero22-isolated-comparison");worker.start();
    }
    @Override protected void onStop(){canceled=true;if(worker!=null)worker.interrupt();super.onStop();}
}
