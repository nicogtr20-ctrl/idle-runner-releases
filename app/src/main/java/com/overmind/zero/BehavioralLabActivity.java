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
 * Writes only to separate zero_behavior_lab_v22 preferences, NEVER into
 * zero_motorbrain_001 or named save slots. No "training" button and no
 * interventions affecting the real organism.
 */
public final class BehavioralLabActivity extends Activity {
    private static final int BG=0xff07121d,PANEL=0xff132832,TEAL=0xff85edda,
        MUTED=0xffa1bdc5,WHITE=0xffe8fff8,GOLD=0xfff1c586;
    private static final String RESULTS="zero_behavior_lab_v22";
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
        root.addView(txt("LABORATORIO COMPORTAMENTALE",23,WHITE,true));
        paragraph(root,"TEST FISICI RIPETIBILI • NON MODIFICA ZERO");
        LinearLayout description=card(root);
        description.addView(txt("Che cosa misuriamo?",16,TEAL,true));
        paragraph(description,"Tre prove: raggiungere energia, evitare danno e "+
            "interagire con una stanza dotata di cancello. Tre varianti di "+
            "posizione/velocita' iniziale per ciascuna.");
        paragraph(description,"Confronto cervello 2.2: A usa il controllo precedente, B usa il nuovo controllore ricorrente. Ogni coppia parte dalla stessa copia dello ZERO e dalle stesse condizioni fisiche. L'apprendimento delle due copie resta isolato dal personaggio reale.");
        paragraph(description,"Ogni prova usa il motore fisico reale del gioco "+
            "per 32 secondi simulati. I risultati riportano veri contatti, "+
            "energia guadagnata/persa, vicinanza, spostamento del cancello "+
            "e attraversamento. Non forniamo bersagli o traiettorie al cervello.");
        paragraph(description,"La prova usa una COPIA dello ZERO attualmente "+
            "salvato, non cancella né allena il personaggio reale. "+
            "Se il cervello ricorrente non ha ancora imparato, "+
            "un pareggio e' normale e non e' una dimostrazione di successo.");
        LinearLayout controls=card(root);
        run=new Button(this);run.setText("AVVIA CONFRONTO A/B");
        controls.addView(run);
        cancel=new Button(this);cancel.setText("INTERROMPI");
        cancel.setEnabled(false);controls.addView(cancel);
        status=txt("Pronto per 18 prove fisiche isolate",13,GOLD,true);
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
    private static float[] readFloats(String saved,int expected,float[] fallback){
        if(saved==null||saved.isEmpty())return fallback.clone();
        String[] p=saved.split(",");
        if(p.length!=expected)return fallback.clone();
        float[] result=new float[expected];
        try{
            for(int i=0;i<expected;i++){
                result[i]=Float.parseFloat(p[i]);
                if(!Float.isFinite(result[i])||Math.abs(result[i])>5)
                    return fallback.clone();
            }
        }catch(RuntimeException bad){return fallback.clone();}
        return result;
    }
    private BehavioralLaboratory.Profile captured(){
        SharedPreferences prefs=getSharedPreferences("zero_motorbrain_001",MODE_PRIVATE);
        BehavioralLaboratory.Profile p=new BehavioralLaboratory.Profile();
        p.motor=readFloats(prefs.getString("best",""),MotorBrain.PARAMS,
            MotorBrain.virgin(new Random(918273)));
        p.hands=readFloats(prefs.getString("hand_brain_v04",""),ContactLearning.PARAMS,
            ContactLearning.virgin());
        String raw=prefs.getString("morph_brain_v07","");
        if(raw.split(",").length==Morphology.LEGACY_PARAMS)
            p.membrane=Morphology.migrate(
                readFloats(raw,Morphology.LEGACY_PARAMS,
                           Morphology.virgin(new Random(14790))));
        else p.membrane=readFloats(raw,Morphology.PARAMS,
                                  Morphology.virgin(new Random(14790)));
        p.world=prefs.getString("sensorimotor_model_v09","");
        p.navigation=prefs.getString(InstrumentalNavigation.KEY,"");
        p.outcome=prefs.getString("outcome_credit_v101","");
        p.diagnostics=prefs.getString("sensorimotor_diagnostics_v091","");
        p.planning=prefs.getString("learned_planner_stats_v102","");
        p.memory=prefs.getString("episodic_memory_v095","");
        p.temporal=prefs.getString("temporal_working_memory_v106","");
        p.skills=prefs.getString("temporal_skills_v107","");
        p.body=prefs.getString(BodyProprioception.KEY,"");
        p.spatial=prefs.getString(SpatialPerception.KEY,"");
        p.causal=prefs.getString(CausalExperiments.KEY,"");
        p.intent=prefs.getString(GoalContinuity.KEY,"");
        p.activeCoreComparison=true;
        p.recurrent=prefs.getString(RecurrentCoreV22.KEY,"");
        p.distributed=prefs.getString(DistributedBodySense.KEY,"");
        p.impedance=prefs.getString(LocalImpedanceSense.KEY,"");
        return p;
    }
    private void runLab(){
        if(running)return;
        final BehavioralLaboratory.Profile source=captured();
        running=true;canceled=false;run.setEnabled(false);cancel.setEnabled(true);
        status.setText("Preparazione di prove fisiche... non chiudere per ottenere il report completo.");
        worker=new Thread(()->{
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try{
                BehavioralLaboratory.Result result=BehavioralLaboratory.run(
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
                        "18 prove completate — risultati isolati dal personaggio.":
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
