package com.overmind.zero;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

/** Player-facing, read-only inspection of the ONE living ZERO 2.2 brain.
 * Never updates learner weights or confuses historical motor genes with cortex.
 */
public final class BrainStatsActivity extends Activity {
    private static final int BG=0xff08101b,PANEL=0xff142733,WHITE=0xffe2f7f4,
        CYAN=0xff78ebd8,MUTED=0xffa6bfc6,GOLD=0xffeac18a;
    private static final int PAGE=12;
    private RecurrentCoreV22 core;
    private SymbioteMotorHeadV1 cortex;
    private LinearLayout coreWeights,motorWeights;
    private TextView corePosition,motorPosition;
    private int coreOffset=0,motorOffset=0;
    private boolean coreLoaded,motorLoaded;

    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        SharedPreferences p=getSharedPreferences(SaveSlotStore.WORKING,MODE_PRIVATE);
        core=new RecurrentCoreV22();
        cortex=new SymbioteMotorHeadV1();
        coreLoaded=core.restore(p.getString(RecurrentCoreV22.KEY,""));
        motorLoaded=cortex.restore(p.getString(SymbioteMotorHeadV1.KEY,""));
        show(p);
    }
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private TextView text(String s,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextSize(size);
        t.setTextColor(color);t.setLineSpacing(dp(3),1f);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return t;
    }
    private LinearLayout col(){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);return c;
    }
    private LinearLayout card(LinearLayout parent,String title){
        LinearLayout c=col();c.setPadding(dp(16),dp(16),dp(16),dp(16));
        GradientDrawable bg=new GradientDrawable();bg.setColor(PANEL);bg.setCornerRadius(dp(17));
        c.setBackground(bg);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.topMargin=dp(12);parent.addView(c,lp);
        c.addView(text(title,16,CYAN,true));return c;
    }
    private void note(LinearLayout group,String value){
        TextView v=text(value,12,MUTED,false);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(8);
        group.addView(v,lp);
    }
    private void row(LinearLayout group,String name,String val){
        LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);
        l.setPadding(0,dp(9),0,0);
        l.addView(text(name,13,MUTED,false),new LinearLayout.LayoutParams(0,-2,1));
        l.addView(text(val,13,WHITE,true));group.addView(l);
    }
    private void action(LinearLayout c,String name,Runnable fn){
        Button b=new Button(this);b.setAllCaps(false);b.setText(name);
        b.setOnClickListener(v->fn.run());c.addView(b);
    }
    private static String readable(float n){
        return Float.isFinite(n)?String.format(Locale.ITALY,"%+.6f",n):"—";
    }
    private static String coreBank(int index){
        int dynamics=RecurrentCoreV22.dynamicsParameters();
        int head=RecurrentCoreV22.headParameters();
        return index<dynamics?"Dinamica sensoriale":index<dynamics+head?
            "Valore delle azioni":index<dynamics+2*head?"Energia prevista":"Rischio previsto";
    }
    private void renderCore(){
        coreWeights.removeAllViews();
        if(!coreLoaded){corePosition.setText("Non disponibile • nessun nucleo salvato");return;}
        int total=core.parameterCount();
        coreOffset=Math.max(0,Math.min(total-1,coreOffset/PAGE*PAGE));
        corePosition.setText("Parametri "+(coreOffset+1)+"–"+
            Math.min(total,coreOffset+PAGE)+" / "+total);
        for(int i=coreOffset;i<Math.min(total,coreOffset+PAGE);i++)
            row(coreWeights,"#"+(i+1)+"  "+coreBank(i),readable(core.parameterAt(i)));
    }
    private void renderMotor(){
        motorWeights.removeAllViews();
        if(!motorLoaded){motorPosition.setText("Non disponibile • nessuna corteccia salvata");return;}
        int total=cortex.parameterCount();
        motorOffset=Math.max(0,Math.min(total-1,motorOffset/PAGE*PAGE));
        motorPosition.setText("Parametri "+(motorOffset+1)+"–"+
            Math.min(total,motorOffset+PAGE)+" / "+total);
        for(int i=motorOffset;i<Math.min(total,motorOffset+PAGE);i++)
            row(motorWeights,"Peso muscolare #"+(i+1),readable(cortex.parameterAt(i)));
    }
    private void show(SharedPreferences p){
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        LinearLayout body=col();body.setPadding(dp(17),dp(27),dp(17),dp(28));
        scroll.addView(body);
        body.addView(text("CERVELLO 2.2",25,WHITE,true));
        note(body,"PARAMETRI • APPRENDIMENTO • EVOLUZIONE • ABILITA");
        note(body,"Solo modelli attivi del corpo simbionte. I valori sono letti dall'ultimo salvataggio, non aggiornati in tempo reale. Non è un punteggio di intelligenza.");

        LinearLayout coreInfo=card(body,"01 / NUCLEO DECISIONALE");
        row(coreInfo,"Stato",coreLoaded?"Salvato e valido":"Non ancora salvato");
        row(coreInfo,"Ingressi fisici",RecurrentCoreV22.F+"");
        row(coreInfo,"Memoria ricorrente",RecurrentCoreV22.H+" stati");
        row(coreInfo,"Azioni possibili",RecurrentCoreV22.A+"");
        row(coreInfo,"Pesi appresi",core.parameterCount()+"");
        row(coreInfo,"Dinamica",RecurrentCoreV22.dynamicsParameters()+"");
        row(coreInfo,"Valore / energia / rischio",RecurrentCoreV22.headParameters()+" ciascuno");
        note(coreInfo,coreLoaded?core.summary():"Nessuna misura ancora disponibile.");
        note(coreInfo,"I pesi non sono neuroni nuovi né livelli di evoluzione. La qualità va valutata con i risultati fuori addestramento e con le azioni fisiche.");
        corePosition=text("",12,GOLD,true);coreInfo.addView(corePosition);
        coreWeights=col();coreInfo.addView(coreWeights);
        action(coreInfo,"‹  12 PARAMETRI PRECEDENTI",()->{coreOffset=Math.max(0,coreOffset-PAGE);renderCore();});
        action(coreInfo,"12 PARAMETRI SUCCESSIVI  ›",()->{
            coreOffset=Math.min(core.parameterCount()-1,coreOffset+PAGE);renderCore();
        });
        renderCore();

        LinearLayout motor=card(body,"02 / CORTECCIA MUSCOLARE");
        row(motor,"Stato",motorLoaded?"Salvata e valida":"Non ancora salvata");
        row(motor,"Pesi addestrabili",cortex.parameterCount()+"");
        row(motor,"Settori fisici",SymbioteMatterV1.SECTORS+"");
        row(motor,"Comandi locali",SymbioteMatterV1.OUTPUTS+" per settore");
        note(motor,motorLoaded?cortex.summary():"La prima scelta muscolare verrà registrata dopo il gioco.");
        note(motor,"Questi sono i pesi che controllano davvero il corpo. Le vecchie reti mano/motore/membrana non sono la corteccia del simbionte.");
        motorPosition=text("",12,GOLD,true);motor.addView(motorPosition);
        motorWeights=col();motor.addView(motorWeights);
        action(motor,"‹  12 PESI PRECEDENTI",()->{motorOffset=Math.max(0,motorOffset-PAGE);renderMotor();});
        action(motor,"12 PESI SUCCESSIVI  ›",()->{
            motorOffset=Math.min(cortex.parameterCount()-1,motorOffset+PAGE);renderMotor();
        });
        renderMotor();

        LinearLayout sensors=card(body,"03 / MODELLI SENSORIALI COLLEGATI");
        BodyProprioception proprio=new BodyProprioception();
        SpatialPerception spatial=new SpatialPerception();
        CausalExperiments causal=new CausalExperiments();
        InstrumentalNavigation nav=new InstrumentalNavigation();
        TemporalWorkingMemory time=new TemporalWorkingMemory();
        boolean hasProprio=proprio.restore(p.getString(BodyProprioception.KEY,""));
        boolean hasSpace=spatial.restore(p.getString(SpatialPerception.KEY,""));
        boolean hasCausal=causal.restore(p.getString(CausalExperiments.KEY,""));
        boolean hasNav=nav.restore(p.getString(InstrumentalNavigation.KEY,""));
        boolean hasTime=time.restore(p.getString("temporal_working_memory_v106",""));
        row(sensors,"Propriocezione",hasProprio?proprio.parameterCount()+" parametri":"Senza dati");
        row(sensors,"Membrana distribuita",DistributedBodySense.PARAMETERS+" parametri");
        row(sensors,"Resistenza locale",LocalImpedanceSense.PARAMETERS+" parametri");
        row(sensors,"Mappa spaziale",hasSpace?spatial.parameterCount()+" parametri":"Senza dati");
        row(sensors,"Conseguenze causali",hasCausal?causal.learnedParameters()+" parametri":"Senza dati");
        row(sensors,"Navigazione visiva",hasNav?nav.parameterCount()+" parametri":"Senza dati");
        row(sensors,"Memoria temporale",hasTime?time.parameterCount()+" parametri":"Senza dati");
        note(sensors,"Ogni modello contribuisce solo quando i propri dati ne giustificano l'affidabilità. Il numero dei parametri non ne dimostra l'efficacia.");

        LinearLayout actions=card(body,"ALTRE ANALISI");
        action(actions,"Analisi e affidabilità  ›",()->startActivity(
            new android.content.Intent(this,BrainToolsActivity.class)));
        action(actions,"Statistiche delle vite  ›",()->startActivity(
            new android.content.Intent(this,StatisticsActivity.class)));
        action(actions,"‹ TORNA A ZERO",()->finish());
        setContentView(scroll);
    }
}
