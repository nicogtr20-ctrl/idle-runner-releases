package com.overmind.zero;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.util.Locale;
import org.json.JSONObject;

/** Read-only quantitative learning evaluation + explicitly confirmed resets.
 * Never edits trained weights itself: GameActivity consumes the request upon
 * returning to foreground, atomically rebuilds only the selected network.
 */
public final class BrainToolsActivity extends Activity {
    private static final int BG=0xff08101b,PANEL=0xff142733,WHITE=0xffe2f7f4;
    private static final int CYAN=0xff78ebd8,MUTED=0xffa6bfc6,AMBER=0xffeac18a;
    private SharedPreferences prefs;
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        prefs=getSharedPreferences("zero_motorbrain_001",MODE_PRIVATE);
        showPage();
    }
    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView label(String text,int size,int color,boolean bold){
        TextView t=new TextView(this);
        t.setText(text);t.setTextColor(color);t.setTextSize(size);
        if(bold)t.setTypeface(android.graphics.Typeface.DEFAULT,1);
        t.setLineSpacing(dp(3),1f);
        return t;
    }
    LinearLayout vertical(){
        LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }
    android.graphics.drawable.GradientDrawable rounded(int color,int radius){
        android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable();
        d.setColor(color);d.setCornerRadius(dp(radius));return d;
    }
    void pad(LinearLayout l,int horizontal,int vertical){
        l.setPadding(dp(horizontal),dp(vertical),dp(horizontal),dp(vertical));
    }
    LinearLayout panel(LinearLayout parent){
        LinearLayout card=vertical();pad(card,16,15);card.setBackground(rounded(PANEL,18));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.topMargin=dp(12);parent.addView(card,lp);return card;
    }
    void line(LinearLayout l,String name,String val,int color){
        LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);
        TextView left=label(name,13,MUTED,false);
        TextView right=label(val,15,color,true);
        r.addView(left,new LinearLayout.LayoutParams(0,-2,1));
        r.addView(right);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(8);
        l.addView(r,lp);
    }
    void paragraph(LinearLayout l,String v){
        TextView t=label(v,12,MUTED,false);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.topMargin=dp(10);l.addView(t,p);
    }
    String number(float n){return String.format(Locale.US,"%.4f",n);}
    String average(float sum,long count){
        return count==0?"—":String.format(Locale.US,"%+.4f",sum/count);
    }
    /** Dashboard of the ACTIVE brain only. Former observer snapshots and
     * obsolete motor genomes are never presented as trained ZERO 2.2 skills.
     * This Activity reads saved preferences without training or mutating them.
     */
    void showPage(){
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        LinearLayout body=vertical();pad(body,17,24);scroll.addView(body);
        body.addView(label("ZERO • CERVELLO 2.2",25,WHITE,true));
        paragraph(body,"UN SOLO DECISORE • CORPO SIMBIONTE • DATI DELL'ULTIMO SALVATAGGIO");
        paragraph(body,"Questa schermata è diagnostica. I valori si aggiornano dopo un salvataggio, non a ogni fotogramma. I modelli possono essere attivi ma non ancora attendibili.");

        RecurrentCoreV22 core=new RecurrentCoreV22();
        boolean coreReady=core.restore(prefs.getString(RecurrentCoreV22.KEY,""));
        SymbioteMotorHeadV1 muscle=new SymbioteMotorHeadV1();
        boolean muscleReady=muscle.restore(prefs.getString(SymbioteMotorHeadV1.KEY,""));
        boolean symbiote=prefs.getBoolean(SymbioteMatterV1.ENABLED,true);
        LinearLayout active=panel(body);
        active.addView(label("01 / SISTEMA ATTIVO",17,CYAN,true));
        line(active,"Cervello",coreReady?"RICORRENTE 2.2":"Non ancora salvato",coreReady?CYAN:AMBER);
        line(active,"Corpo",symbiote?"SIMBIONTE":"Modalità storica",symbiote?CYAN:AMBER);
        line(active,"Decisioni possibili",RecurrentCoreV22.A+"",WHITE);
        line(active,"Ingressi al nucleo",RecurrentCoreV22.F+"",WHITE);
        line(active,"Pesi del nucleo",core.parameterCount()+"",WHITE);
        line(active,"Pesi muscolari totali",SymbioteMotorSkillMemoryV169.WIDTH+"",WHITE);
        line(active,"Passo fisico","1/60 s a tutte le velocità",WHITE);
        paragraph(active,coreReady?core.summary():"Il nucleo sarà disponibile dopo il primo checkpoint completo.");
        LearningDiagnostics activePrediction=new LearningDiagnostics();
        if(activePrediction.restore(prefs.getString(LearningDiagnostics.ACTIVE_KEY,""))){
            line(active,"Verifiche del predittore 2.2",activePrediction.heldoutTransitions+"",WHITE);
            line(active,"Errore 2.2 / nessun cambiamento",String.format(Locale.US,"%.4f / %.4f",activePrediction.modelError,activePrediction.baselineError),CYAN);
        }
        paragraph(active,muscleReady?muscle.summary():"La corteccia muscolare non ha ancora un checkpoint valido.");
        paragraph(active,"Le azioni sono scelte dal nucleo 2.2. Il controllore muscolare traduce le 16 scelte in contrazioni locali; non esiste un secondo cervello attivo.");

        LinearLayout linkParameters=panel(body);
        linkParameters.addView(label("PARAMETRI E PESI DEL 2.2  ›",17,CYAN,true));
        paragraph(linkParameters,"Guarda i pesi realmente salvati nel nucleo ricorrente e nei muscoli, oltre ai parametri dei sensori collegati. Nessun confronto artificiale con il cervello vecchio.");
        linkParameters.setOnClickListener(v->startActivity(new Intent(this,BrainStatsActivity.class)));

        LinearLayout bodySense=panel(body);
        bodySense.addView(label("02 / SENSI CORPOREI",17,CYAN,true));
        BodyProprioception proprio=new BodyProprioception();
        DistributedBodySense distributed=new DistributedBodySense();
        LocalImpedanceSense impedance=new LocalImpedanceSense();
        FluidInteroceptionV1 inner=new FluidInteroceptionV1();
        boolean bp=proprio.restore(prefs.getString(BodyProprioception.KEY,""));
        boolean bd=distributed.restore(prefs.getString(DistributedBodySense.KEY,""));
        boolean bi=impedance.restore(prefs.getString(LocalImpedanceSense.KEY,""));
        boolean bf=inner.restore(prefs.getString(FluidInteroceptionV1.KEY,""));
        line(bodySense,"Propriocezione",bp?proprio.validations+" verifiche":"In attesa di dati",WHITE);
        line(bodySense,"Corpo distribuito",bd?distributed.validations+" verifiche":"In attesa di dati",WHITE);
        line(bodySense,"Resistenza e contatti",bi?impedance.validations+" verifiche":"In attesa di dati",WHITE);
        line(bodySense,"Fluido interno",bf?inner.validations+" verifiche":"In attesa di dati",WHITE);
        if(bp)line(bodySense,"Fiducia propriocezione",percent(proprio.confidence()),CYAN);
        if(bd)line(bodySense,"Fiducia corpo distribuito",percent(distributed.confidence()),CYAN);
        if(bi)line(bodySense,"Fiducia resistenza",percent(impedance.confidence()),CYAN);
        if(bf)line(bodySense,"Fiducia interocezione",percent(inner.confidence()),CYAN);
        paragraph(bodySense,"I quattro modelli contribuiscono ciascuno una sola volta, quando il proprio confronto fuori addestramento li considera attendibili. La richiesta di allungamento e la forma realmente raggiunta sono misure diverse.");

        LinearLayout spatialPanel=panel(body);
        spatialPanel.addView(label("03 / PERCEZIONE DEL MONDO",17,CYAN,true));
        SpatialPerception spatial=new SpatialPerception();
        CausalExperiments causal=new CausalExperiments();
        InstrumentalNavigation navigation=new InstrumentalNavigation();
        ObjectContinuityV21 objects=new ObjectContinuityV21();
        CausalSequenceV23 sequence=new CausalSequenceV23();
        boolean sp=spatial.restore(prefs.getString(SpatialPerception.KEY,""));
        boolean ca=causal.restore(prefs.getString(CausalExperiments.KEY,""));
        boolean nav=navigation.restore(prefs.getString(InstrumentalNavigation.KEY,""));
        boolean oc=objects.restore(prefs.getString(ObjectContinuityV21.KEY,""));
        boolean seq=sequence.restore(prefs.getString(CausalSequenceV23.KEY,""));
        if(sp){
            SpatialMapView map=new SpatialMapView(this,spatial);
            LinearLayout.LayoutParams mapSize=new LinearLayout.LayoutParams(-1,dp(190));
            spatialPanel.addView(map,mapSize);
            line(spatialPanel,"Celle osservate",spatial.mappedCells()+"",WHITE);
            line(spatialPanel,"Verifiche spaziali",spatial.validations+"",WHITE);
            line(spatialPanel,"Fiducia spaziale",percent(spatial.confidence()),CYAN);
        }else line(spatialPanel,"Mappa fisica","Nessun salvataggio disponibile",AMBER);
        line(spatialPanel,"Esperimenti causa/effetto",ca?causal.experiments+"":"Non ancora registrati",WHITE);
        if(ca)line(spatialPanel,"Fiducia causale",percent(causal.confidence()),CYAN);
        navigation.restoreContextual(prefs.getString(ContextualNavigationV171.KEY,""));
        if(symbiote)navigation.motorContext(null,null,muscle.verifiedMotorSnapshot());
        line(spatialPanel,"Movimenti verificati nel contesto",symbiote?"Verificati sulla postura nel gioco":nav?navigation.verifiedActions()+" / 16":"In attesa di dati",WHITE);
        paragraph(spatialPanel,"Fiducia zero: il sensore raccoglie dati, ma non ha ancora superato la verifica predittiva. Il contributo alle scelte resta sospeso; non significa sensore scollegato.");
        line(spatialPanel,"Tracce oggetti anonimi",oc?objects.trackedViews()+"":"In attesa di dati",WHITE);
        line(spatialPanel,"Sequenze causali",seq?sequence.validations()+" prove esterne":"In attesa di dati",WHITE);
        paragraph(spatialPanel,"Continuità degli oggetti e sequenze causali restano utili anche se alcuni file conservano nomi 2.1/2.3. Il vecchio arbitro 2.1 e il pianificatore 2.0 non comandano il corpo.");

        LinearLayout memory=panel(body);
        memory.addView(label("04 / MEMORIA E OBIETTIVI",17,CYAN,true));
        TemporalWorkingMemory temporal=new TemporalWorkingMemory();
        TemporalSkillBank skills=new TemporalSkillBank();
        GoalContinuity goals=new GoalContinuity();
        boolean tm=temporal.restore(prefs.getString("temporal_working_memory_v106",""));
        boolean ts=skills.restore(prefs.getString("temporal_skills_v107",""));
        boolean go=goals.restore(prefs.getString(GoalContinuity.KEY,""));
        line(memory,"Memoria temporale",tm?temporal.transitions+" esperienze":"In attesa di dati",WHITE);
        if(tm){
            line(memory,"Verifiche temporali",temporal.validationTransitions+"",WHITE);
            line(memory,"Fiducia temporale",percent(temporal.confidence()),CYAN);
        }
        line(memory,"Abilità temporali",ts?skills.stored()+" conservate":"In attesa di dati",WHITE);
        if(ts){
            line(memory,"Sequenze consolidate",skills.consolidated()+"",CYAN);
            line(memory,"Esecuzioni riuscite",skills.completions+"",WHITE);
        }
        SymbioteMotorSkillMemoryV169 manipulation=new SymbioteMotorSkillMemoryV169(true);
        manipulation.restore(prefs.getString(SymbioteMotorSkillMemoryV169.MANIPULATION_KEY,""));
        line(memory,"Tecniche di manipolazione verificate",manipulation.verifiedCount()+"",CYAN);
        line(memory,"Tecniche da verificare",manipulation.candidatesCount()+"",WHITE);
        line(memory,"Progressi obiettivi",go?goals.confirmed+" verificati":"In attesa di dati",WHITE);
        paragraph(memory,"La memoria può suggerire un'azione, ma non imporla. ZERO deve confermare con l'esperienza che una sequenza funziona; non riceve coordinate, regole di soluzione o premi per il semplice movimento.");

        GateBehaviorMetrics gate=new GateBehaviorMetrics();
        boolean gateLoaded=gate.restore(prefs.getString(GateBehaviorMetrics.KEY,""));
        LinearLayout contacts=panel(body);
        contacts.addView(label("05 / PRESE E CANCELLO",16,CYAN,true));
        line(contacts,"Presa fisica mantenuta",gateLoaded?gate.heldFrames+" frame":"In attesa di dati",WHITE);
        line(contacts,"Presa sui lati opposti",gateLoaded?gate.opposedFrames+" frame":"In attesa di dati",WHITE);
        line(contacts,"Aperture / attraversamenti",gateLoaded?
            gate.opened+" / "+gate.passages:"In attesa di dati",WHITE);
        paragraph(contacts,"Misure di contatto reale: non sono abilità preprogrammate e non scelgono le azioni.");

        LinearLayout stats=panel(body);
        stats.addView(label("STATISTICHE DELLE VITE  ›",17,CYAN,true));
        paragraph(stats,"Vita attuale, medie delle ultime 25 vite complete, confronto fra 25 e 25 precedenti e storico complessivo. Casualità e ambiente possono influenzare le differenze.");
        stats.setOnClickListener(v->startActivity(new Intent(this,StatisticsActivity.class)));

        LinearLayout share=panel(body);
        share.addView(label("CONDIVIDI DATI CON GPT  ↗",17,CYAN,true));
        paragraph(share,"Invio manuale e privato dell'ultimo profilo salvato, inclusi i pesi del 2.2, le memorie, il corpo e le statistiche. I campi legacy eventualmente presenti sono materiale storico, non funzioni attive.");
        share.setOnClickListener(v->startActivity(new Intent(this,TelemetryActivity.class)));

        LinearLayout lab=panel(body);
        lab.addView(label("LABORATORI • TEST ISOLATI",17,CYAN,true));
        paragraph(lab,"Sul simbionte i confronti usano il corpo fluido e gli attuatori neurali attuali: A mantiene congelati i pesi, B apprende online. Le vecchie partite mantengono i laboratori storici. Streaming e tempeste non fanno parte delle arene controllate.");
        TextView basic=label("CONFRONTO FISICO BREVE  ›",15,WHITE,true);
        basic.setPadding(0,dp(17),0,dp(12));basic.setOnClickListener(v->
            startActivity(new Intent(this,BehavioralLabActivity.class)));lab.addView(basic);
        TextView extended=label("PROVE ESTESE 2.2 / CASSE  ›",15,WHITE,true);
        extended.setPadding(0,dp(12),0,dp(12));
        extended.setOnClickListener(v->startActivity(new Intent(this,CoreLabActivityV22.class)));
        lab.addView(extended);

        LinearLayout resets=panel(body);
        resets.addView(label("RIPRISTINA APPRENDIMENTO • AVANZATO",16,AMBER,true));
        paragraph(resets,"Attenzione: le opzioni qui sotto eliminano davvero i pesi e le esperienze dei moduli indicati. Sono separate per evitare di cancellare involontariamente il 2.2.");
        resetButton(resets,"NUCLEO 2.2 E PREVISIONE","Elimina il nucleo ricorrente, la memoria temporale di lavoro e la diagnostica del modello. Non cancella la fisica.",ResetDomains.WORLD,false);
        resetButton(resets,"CORTECCIA MUSCOLARE","Azzera i pesi appresi del motore del simbionte e i vecchi dati di membrana associati.",ResetDomains.MEMBRANE,false);
        resetButton(resets,"ABILITÀ TEMPORALI","Azzera le sequenze apprese senza cancellare il nucleo principale.",ResetDomains.TEMPORAL_SKILLS,false);
        resetButton(resets,"SENSI CORPOREI","Azzera propriocezione, membrana distribuita, resistenza e interocezione; non cambia il corpo fisico.",ResetDomains.PROPRIOCEPTION,false);
        resetButton(resets,"MEMORIA SPAZIALE E OGGETTI","Azzera mappa spaziale, continuità degli oggetti e associazioni relative.",ResetDomains.SPATIAL,false);
        resetButton(resets,"APPRENDIMENTO CAUSALE","Azzera esperimenti, memoria delle sequenze causali e relativi dati.",ResetDomains.CAUSAL,false);
        resetButton(resets,"NAVIGAZIONE VISIVA","Azzera associazioni fra stimoli luminosi, risultati energetici e spostamenti.",ResetDomains.NAVIGATION,false);
        resetButton(resets,"CONTINUITÀ DEGLI OBIETTIVI","Azzera i progressi di continuità degli obiettivi.",ResetDomains.INTENTION,false);
        resetButton(resets,"DIAGNOSTICA DEI CONTATTI","Azzera solo le metriche osservate di presa/cancello.",ResetDomains.GATE_METRICS,false);
        resetButton(resets,"AZZERA COMPLETAMENTE ZERO","Cancella cervelli, apprendimento, statistiche e situazione attuale.",ResetDomains.ALL,true);

        LinearLayout saves=panel(body);
        saves.addView(label("PARTITE E SALVATAGGI",16,CYAN,true));
        paragraph(saves,"Puoi creare un nuovo ZERO oppure gestire i salvataggi. Le vecchie partite non hanno priorità sulla nuova architettura.");
        TextView slots=label("GESTISCI ZERO  ›",16,CYAN,true);
        slots.setPadding(dp(3),dp(16),dp(3),dp(12));
        slots.setOnClickListener(v->{startActivity(new Intent(this,SaveSlotsActivity.class));finish();});
        saves.addView(slots);
        TextView back=label("‹ TORNA A ZERO",17,CYAN,true);
        back.setGravity(Gravity.CENTER);back.setPadding(0,dp(24),0,dp(20));
        back.setOnClickListener(v->finish());body.addView(back);
        setContentView(scroll);
    }

    String percent(float v){
        return Float.isFinite(v)?String.format(Locale.ITALY,"%.1f%%",v*100f):"—";
    }
    void resetButton(LinearLayout l,String name,String description,String code,boolean full){
        LinearLayout box=vertical();
        box.setPadding(dp(13),dp(12),dp(13),dp(12));
        box.setBackground(rounded(full?0xff443039:0xff213943,12));
        TextView head=label((full?"! ":"↺ ")+name,14,full?0xffffb4ab:WHITE,true);
        box.addView(head);box.addView(label(description,11,MUTED,false));
        box.setOnClickListener(v->confirmReset(code,name,description,full,false));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.topMargin=dp(9);l.addView(box,lp);
    }
    void confirmReset(String code,String name,String detail,boolean full,boolean finalStep){
        String message=full?
          finalStep?"ULTIMA CONFERMA. Tutta la memoria locale di ZERO verrà cancellata definitivamente. L'installazione Android rimane.":
                   "Vuoi davvero riportare ZERO allo stato iniziale? Tutti i parametri, le esperienze e i progressi andranno persi.":
          "Vuoi azzerare SOLO «"+name+"»?\n\n"+detail+
          "\n\nLe altre sezioni manterranno i propri parametri.";
        AlertDialog.Builder d=new AlertDialog.Builder(this)
            .setTitle(full?(finalStep?"CONFERMA DEFINITIVA":"AZZERA TUTTO"):name)
            .setMessage(message)
            .setNegativeButton("ANNULLA",(a,b)->{});
        d.setPositiveButton(full&&!finalStep?"CONTINUA":"AZZERA",
            (a,b)->{
                if(full&&!finalStep){
                    confirmReset(code,name,detail,true,true);
                    return;
                }
                if(!ResetDomains.valid(code))return;
                // A request is staged, NOT executed while this Activity
                // might be holding a stale copy of the models.
                boolean saved=prefs.edit().putString(ResetDomains.REQUEST,code).commit();
                if(!saved){
                    new AlertDialog.Builder(this).setMessage("Impossibile registrare l'azzeramento.")
                        .setPositiveButton("OK",null).show();
                    return;
                }
                Intent target=new Intent(this,GameActivity.class);
                target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(target);
                finish();
            }).show();
    }
    /** Player-only visualization of *observed* occupancy, not the true map. */
    private static final class SpatialMapView extends View {
        final SpatialPerception model;
        final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        SpatialMapView(Context c,SpatialPerception m){super(c);model=m;}
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            int radius=8,side=radius*2+1;
            byte[] visible=model.localMap(radius);
            float step=Math.min((getWidth()-12f)/side,(getHeight()-12f)/side);
            float left=(getWidth()-side*step)/2f,top=(getHeight()-side*step)/2f;
            paint.setStrokeWidth(Math.max(1f,step*.055f));
            for(int y=0;y<side;y++)for(int x=0;x<side;x++){
                byte occupancy=visible[y*side+x];
                paint.setColor(occupancy<0?0xff28677c:
                    occupancy>0?0xffda9872:0xff14212c);
                float l=left+x*step,t=top+y*step;
                c.drawRect(l+1,t+1,l+step-1,t+step-1,paint);
            }
            paint.setColor(0xff83ecda);
            float x=left+(radius+.5f)*step,y=top+(radius+.5f)*step;
            c.drawCircle(x,y,step*.34f,paint);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2f);
            c.drawCircle(x,y,step*.46f,paint);paint.setStyle(Paint.Style.FILL);
        }
    }
    private static final class Graph extends View {
        final Paint pen=new Paint(3);final LearningDiagnostics history;
        Graph(Context c,LearningDiagnostics data){super(c);history=data;}
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            float w=getWidth(),h=getHeight(),left=9,right=w-9,top=16,bottom=h-20;
            float max=.01f;
            for(int i=0;i<history.points();i++){
                max=Math.max(max,Math.max(history.errorAt(i),history.baselineAt(i)));
            }
            pen.setStrokeWidth(1f);pen.setColor(0xff324b57);
            for(int j=0;j<4;j++){
                float y=top+(bottom-top)*j/3;c.drawLine(left,y,right,y,pen);
            }
            drawSeries(c,true,0xff73e7d2,max,left,right,top,bottom);
            drawSeries(c,false,0xfff1bf77,max,left,right,top,bottom);
        }
        void drawSeries(Canvas c,boolean model,int color,float max,
                        float left,float right,float top,float bottom){
            if(history.points()<1)return;
            pen.setStrokeWidth(3f);pen.setColor(color);pen.setStyle(Paint.Style.STROKE);
            Path trace=new Path();
            for(int i=0;i<history.points();i++){
                float x=left+(right-left)*i/Math.max(1,history.points()-1);
                float v=model?history.errorAt(i):history.baselineAt(i);
                float y=bottom-(bottom-top)*(Math.max(0,Math.min(max,v))/max);
                if(i==0)trace.moveTo(x,y);else trace.lineTo(x,y);
            }
            c.drawPath(trace,pen);pen.setStyle(Paint.Style.FILL);
        }
    }
}
