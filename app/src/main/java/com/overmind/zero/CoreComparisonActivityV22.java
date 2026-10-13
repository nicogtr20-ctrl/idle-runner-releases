package com.overmind.zero;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Process;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;
import java.util.Random;

/**
 * Read-only comparison of a SNAPSHOT of one ZERO against an ablation of its
 * learned instrumental navigation. ALL runs live in private heap clones.
 *
 * Writes only to separate zero_behavior_lab_v113 preferences, NEVER into
 * zero_motorbrain_001 or named save slots. No "training" button and no
 * interventions affecting the real organism.
 */
public final class CoreComparisonActivityV22 extends Activity {
    private static final int BG=0xff07121d,PANEL=0xff132832,TEAL=0xff85edda,
        MUTED=0xffa1bdc5,WHITE=0xffe8fff8,GOLD=0xfff1c586;
    private static final String RESULTS=CoreComparisonV22.RESULTS;
    private volatile boolean canceled=false,running=false;
    private Thread worker;
    private TextView status,report;
    private Button run,cancel;

    private int dp(float v){return Math.round(v*getResources().getDisplayMetrics().density);}
    private TextView txt(String message,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(message);t.setTextColor(color);
        t.setTextSize(size);t.setTextIsSelectable(!bold);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        t.setLineSpacing(dp(3),1f);
        return t;
    }
    private GradientDrawable bg(int color,int radius){
        GradientDrawable d=new GradientDrawable();
        d.setColor(color);d.setCornerRadius(dp(radius));return d;
    }
    private LinearLayout card(LinearLayout parent){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(16),dp(16),dp(16),dp(16));c.setBackground(bg(PANEL,18));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.topMargin=dp(12);parent.addView(c,lp);return c;
    }
    private void paragraph(LinearLayout parent,String t){
        TextView v=txt(t,13,MUTED,false);LinearLayout.LayoutParams lp=
            new LinearLayout.LayoutParams(-1,-2);
        lp.topMargin=dp(9);parent.addView(v,lp);
    }
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        LinearLayout root=new LinearLayout(this);
        root.setPadding(dp(17),dp(28),dp(17),dp(28));
        root.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(root);
        root.addView(txt("CONFRONTO CERVELLO 2.2",23,WHITE,true));
        paragraph(root,"TEST FISICI RIPETIBILI • NON MODIFICA ZERO");
        LinearLayout description=card(root);
        description.addView(txt("Che cosa misuriamo?",16,TEAL,true));
        paragraph(description,"Confronto tra il controllo precedente (A) e il nuovo cervello ricorrente con recupero delle tecniche (B), su copie identiche del profilo attuale.");
        paragraph(description,"Dodici coppie: casse con energia nascosta, lastre e pericoli. Ogni prova dura fino a 170 secondi simulati. Gli esiti comprendono energia, danni, sopravvivenza e prese fisiche.");
        paragraph(description,"Non modifica o allena il personaggio reale. L'arena mantiene controllori corporei fissi e non riproduce tempeste o espansione del mondo. E' una verifica iniziale, non una certificazione di intelligenza.");
        LinearLayout controls=card(root);
        run=new Button(this);run.setText("AVVIA CONFRONTO A/B");
        controls.addView(run);
        cancel=new Button(this);cancel.setText("INTERROMPI");
        cancel.setEnabled(false);controls.addView(cancel);
        status=txt("Pronto per 24 prove fisiche isolate",13,GOLD,true);
        paragraph(controls,"Per interrompere la simulazione tocca INTERROMPI "+
            "o esci dalla schermata: i salvataggi reali non vengono toccati.");
        controls.addView(status);
        run.setOnClickListener(v->runLab());
        cancel.setOnClickListener(v->{canceled=true;status.setText("Interruzione richiesta...");});
        LinearLayout outputs=card(root);
        outputs.addView(txt("RISULTATI ULTIMO CONFRONTO",16,TEAL,true));
        report=txt(getSharedPreferences(RESULTS,MODE_PRIVATE)
            .getString("last","Nessuna prova ancora completata."),12,WHITE,false);
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);
        rp.topMargin=dp(13);outputs.addView(report,rp);
        TextView back=txt("‹ TORNA AD ANALISI",16,TEAL,true);
        back.setGravity(Gravity.CENTER);
        back.setPadding(0,dp(22),0,dp(22));
        back.setOnClickListener(v->finish());root.addView(back);
        setContentView(scroll);
    }
    private CoreComparisonV22.Profile captured(){
        return CoreComparisonV22.capture(getSharedPreferences("zero_motorbrain_001",MODE_PRIVATE).getAll());
    }
    private void runLab(){
        if(running)return;
        final CoreComparisonV22.Profile source=captured();
        running=true;canceled=false;run.setEnabled(false);cancel.setEnabled(true);
        status.setText("Preparazione di prove fisiche... non chiudere per ottenere il report completo.");
        worker=new Thread(()->{
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try{
                CoreComparisonV22.Result result=CoreComparisonV22.run(
                    source,(done,total,label)->runOnUiThread(()->{
                        if(!isFinishing()&&!isDestroyed())status.setText(
                            String.format(Locale.US,"%d / %d • %s",done,total,label));
                    }),()->canceled||Thread.currentThread().isInterrupted());
                String text=result.report();
                boolean complete=result.finished==result.total();
                if(complete&&!canceled){
                    getSharedPreferences(RESULTS,MODE_PRIVATE).edit()
                        .putString("last",text).apply();
                }
                runOnUiThread(()->{
                    if(isFinishing()||isDestroyed())return;
                    report.setText(text);
                    status.setText(complete&&!canceled?
                        "24 prove completate — risultati isolati dal personaggio.":
                        "Confronto interrotto — report parziale non salvato.");
                });
            }catch(Throwable failure){
                runOnUiThread(()->{
                    if(isFinishing()||isDestroyed())return;
                    status.setText("Esperimento interrotto: "+failure.getClass().getSimpleName());
                });
            }finally{
                running=false;
                runOnUiThread(()->{
                    if(!isFinishing()&&!isDestroyed()){
                        run.setEnabled(true);cancel.setEnabled(false);
                    }
                });
            }
        },"zero-private-behavior-lab");
        worker.start();
    }
    @Override protected void onDestroy(){
        canceled=true;
        if(worker!=null)worker.interrupt();
        super.onDestroy();
    }
}
