package com.overmind.zero;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

/**
 * ZERO neural atlas. All interactive views are read-only; measured values are
 * restored from the ONE local active 2.2 checkpoint. Not an old motor skill tree.
 * Subsystem grouping is navigation only, never a secondary action controller.
 */
public final class BrainStatsActivity extends Activity {
    private static final int BG=0xff07111e,CARD=0xff122537,CARD2=0xff183346;
    private static final int WHITE=0xffedf9f7,MUTED=0xffa8c2ce,TEAL=0xff6cf1d9;
    private static final int VIOLET=0xffb0a2ff,GOLD=0xffffca80,BLUE=0xff80caff;
    private static final int ORANGE=0xffffa5a2;
    private static final int PAGE=12;
    private SharedPreferences prefs;
    private String section="HOME", module="";
    private int coreOffset=0,motorOffset=0;
    private RecurrentCoreV22 core;
    private SymbioteMotorHeadV1 muscle;
    private BodyProprioception proprio;
    private DistributedBodySense distributed;
    private LocalImpedanceSense impedance;
    private FluidInteroceptionV1 inner;
    private SpatialPerception spatial;
    private InstrumentalNavigation navigation;
    private ObjectContinuityV21 objects;
    private CausalExperiments causal;
    private CausalSequenceV23 sequence;
    private TemporalWorkingMemory temporal;
    private TemporalSkillBank skills;
    private GoalContinuity goals;
    private GateBehaviorMetrics gates;
    private LifeStatistics lives;
    private boolean bCore,bMuscle,bProprio,bDistributed,bImpedance,bInner,
        bSpatial,bNavigation,bObjects,bCausal,bSequence,bTemporal,bSkills,bGoals,
        bGates,bLives;
    private long shownAt;

    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        prefs=getSharedPreferences(SaveSlotStore.WORKING,MODE_PRIVATE);
        loadReadOnly();
        render();
    }
    private void loadReadOnly(){
        core=new RecurrentCoreV22();
        muscle=new SymbioteMotorHeadV1();
        proprio=new BodyProprioception();
        distributed=new DistributedBodySense();
        impedance=new LocalImpedanceSense();
        inner=new FluidInteroceptionV1();
        spatial=new SpatialPerception();
        navigation=new InstrumentalNavigation();
        objects=new ObjectContinuityV21();
        causal=new CausalExperiments();
        sequence=new CausalSequenceV23();
        temporal=new TemporalWorkingMemory();
        skills=new TemporalSkillBank();
        goals=new GoalContinuity();
        gates=new GateBehaviorMetrics();
        lives=new LifeStatistics();
        bCore=core.restore(prefs.getString(RecurrentCoreV22.KEY,""));
        bMuscle=muscle.restore(prefs.getString(SymbioteMotorHeadV1.KEY,""));
        bProprio=proprio.restore(prefs.getString(BodyProprioception.KEY,""));
        bDistributed=distributed.restore(prefs.getString(DistributedBodySense.KEY,""));
        bImpedance=impedance.restore(prefs.getString(LocalImpedanceSense.KEY,""));
        bInner=inner.restore(prefs.getString(FluidInteroceptionV1.KEY,""));
        bSpatial=spatial.restore(prefs.getString(SpatialPerception.KEY,""));
        bNavigation=navigation.restore(prefs.getString(InstrumentalNavigation.KEY,""));
        bObjects=objects.restore(prefs.getString(ObjectContinuityV21.KEY,""));
        bCausal=causal.restore(prefs.getString(CausalExperiments.KEY,""));
        bSequence=sequence.restore(prefs.getString(CausalSequenceV23.KEY,""));
        bTemporal=temporal.restore(prefs.getString("temporal_working_memory_v106",""));
        bSkills=skills.restore(prefs.getString("temporal_skills_v107",""));
        bGoals=goals.restore(prefs.getString(GoalContinuity.KEY,""));
        bGates=gates.restore(prefs.getString(GateBehaviorMetrics.KEY,""));
        bLives=lives.restore(prefs.getString(LifeStatistics.KEY,""));
    }
    @Override public void onBackPressed(){
        if(!module.isEmpty()){module="";render();return;}
        if(!"HOME".equals(section)){section="HOME";render();return;}
        super.onBackPressed();
    }
    private int dp(float a){return (int)(a*getResources().getDisplayMetrics().density+.5f);}
    private GradientDrawable background(int start,int end,float radius,boolean border){
        GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            new int[]{start,end});
        g.setCornerRadius(dp(radius));
        if(border)g.setStroke(dp(1),0xff315066);
        return g;
    }
    private LinearLayout vertical(){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);return c;
    }
    private TextView label(String value,int size,int color,boolean strong){
        TextView t=new TextView(this);t.setText(value);t.setTextSize(size);
        t.setTextColor(color);
        if(strong)t.setTypeface(android.graphics.Typeface.DEFAULT,1);
        t.setLineSpacing(dp(3),1);return t;
    }
    private void add(LinearLayout parent,View child,int top){
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.topMargin=dp(top);parent.addView(child,p);
    }
    private void note(LinearLayout box,String text){
        add(box,label(text,12,MUTED,false),8);
    }
    private LinearLayout panel(LinearLayout parent,String title,int tint){
        LinearLayout c=vertical();
        c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.setBackground(background(CARD,CARD2,18,true));
        add(parent,c,12);
        add(c,label(title,15,tint,true),0);
        return c;
    }
    private void metric(LinearLayout c,String title,String value){
        LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);
        TextView name=label(title,13,MUTED,false);
        name.setPadding(0,0,dp(8),0);
        r.addView(name,new LinearLayout.LayoutParams(0,-2,1));
        TextView number=label(value,13,WHITE,true);r.addView(number);
        add(c,r,10);
    }
    private void line(LinearLayout c,String name,String value,String meaning,int accent){
        LinearLayout p=vertical();p.setPadding(dp(11),dp(12),dp(11),dp(12));
        p.setBackground(background(0xff152d43,0xff112437,14,true));
        add(c,p,10);add(p,label(name,14,accent,true),0);
        add(p,label(value,20,WHITE,true),5);
        if(meaning!=null&&!meaning.isEmpty())note(p,meaning);
    }
    private String num(long v){return String.format(Locale.ITALY,"%,d",v);}
    private String dec(double v){return Double.isFinite(v)?String.format(Locale.ITALY,"%.2f",v):"—";}
    private String pct(float v){
        return Float.isFinite(v)?String.format(Locale.ITALY,"%.1f%%",
            Math.max(0,Math.min(1,v))*100f):"—";
    }
    private String absent(boolean loaded,String present){
        return loaded?present:"Non ancora disponibile";
    }
    private View tap(LinearLayout parent,String title,String summary,int accent,Runnable action){
        LinearLayout c=vertical();c.setPadding(dp(16),dp(16),dp(16),dp(16));
        c.setBackground(background(0xff183449,0xff102434,17,true));
        add(parent,c,11);
        TextView t=label(title+"  ›",16,accent,true);c.addView(t);
        if(summary!=null)note(c,summary);
        c.setOnClickListener(v->action.run());
        c.setMinimumHeight(dp(78));c.setClickable(true);
        c.setContentDescription(title+". "+(summary==null?"":summary));
        return c;
    }
    private void tile(LinearLayout container,String title,String hint,int tint,String id){
        tap(container,title,hint,tint,()->{
            module=id;
            render();
        });
    }
    private void group(String id){section=id;module="";render();}
    private String title(String id){
        switch(id){
            case "CORE":return "Nucleo ricorrente";
            case "MUSCLE":return "Corteccia muscolare";
            case "TEMP":return "Memoria temporale";
            case "SKILLS":return "Abilità consolidate";
            case "GOALS":return "Continuità degli obiettivi";
            case "CAUSE":return "Esperimenti causa-effetto";
            case "SEQUENCE":return "Sequenze causali";
            case "PROPRIO":return "Propriocezione";
            case "DISTRIBUTED":return "Membrana distribuita";
            case "IMPEDANCE":return "Resistenza locale";
            case "FLUID":return "Interocezione";
            case "GATES":return "Tatto e prese";
            case "VISION":return "Vista reale";
            case "NAV":return "Navigazione appresa";
            case "SPACE":return "Mappa spaziale";
            case "OBJECTS":return "Memoria degli oggetti";
            case "WEIGHTS_CORE":return "Parametri del nucleo";
            case "WEIGHTS_MOTOR":return "Pesi muscolari";
            default:return id;
        }
    }
    private String sectionTitle(String section){
        switch(section){
            case "BRAIN":return "CERVELLO";
            case "BODY":return "CORPO";
            case "PERCEPTION":return "PERCEZIONE";
            case "SKILL":return "ABILITÀ";
            case "EVOLUTION":return "EVOLUZIONE";
            case "PARAMS":return "PARAMETRI";
            default:return "ZERO";
        }
    }
    private void render(){
        shownAt=SystemClock.uptimeMillis();
        FrameLayout frame=new FrameLayout(this);
        frame.setBackgroundColor(BG);
        frame.addView(new GridView(),new FrameLayout.LayoutParams(-1,-1));
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout body=vertical();
        body.setPadding(dp(18),dp(22),dp(18),dp(32));scroll.addView(body);
        frame.addView(scroll,new FrameLayout.LayoutParams(-1,-1));
        TextView back=label("‹    "+("HOME".equals(section)?"CERVELLO / ZERO":
            sectionTitle(section))+(module.isEmpty()?"":" / "+title(module)),19,WHITE,true);
        back.setPadding(dp(3),dp(5),0,dp(10));back.setOnClickListener(v->onBackPressed());
        body.addView(back);
        add(body,label("ARCHITETTURA 2.2   /   DATI SALVATI",11,TEAL,true),0);
        if("HOME".equals(section)){
            home(body);
        }else if(!module.isEmpty()){
            details(body,module);
        }else{
            groupPage(body,section);
        }
        setContentView(frame);
    }
    private void home(LinearLayout body){
        FrameLayout hero=new FrameLayout(this);
        hero.setBackground(background(0xff11263d,0xff091a2b,22,true));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(195));
        lp.topMargin=dp(15);body.addView(hero,lp);
        hero.addView(new NeuralView(),new FrameLayout.LayoutParams(-1,-1));
        TextView caption=label("ZERO 2.2  ·  SISTEMA ATTIVO",12,TEAL,true);
        FrameLayout.LayoutParams cp=new FrameLayout.LayoutParams(-2,-2,
            android.view.Gravity.BOTTOM|android.view.Gravity.CENTER_HORIZONTAL);
        cp.bottomMargin=dp(14);hero.addView(caption,cp);
        note(body,"UNA SOLA INTELLIGENZA • 16 AZIONI • CORPO SIMBIONTE");
        tile(body,"◉  CERVELLO","Nucleo decisionale, memoria, causalità e obiettivi",TEAL,"section-brain");
        tile(body,"⬡  CORPO","Muscoli, membrana, tatto, propriocezione, fluido",VIOLET,"section-body");
        tile(body,"◉  PERCEZIONE","Vista, navigazione, mappa e oggetti",BLUE,"section-perception");
        tile(body,"✦  ABILITÀ","Capacità osservate e sequenze realmente consolidate",GOLD,"section-skill");
        tile(body,"↗  EVOLUZIONE","Apprendimento, verifiche e confronto tra vite",ORANGE,"section-evolution");
        tile(body,"⌘  PARAMETRI","Pesi del nucleo e della corteccia, senza falsi livelli",TEAL,"section-params");
        add(body,label("Tocca una sezione per aprire i suoi sistemi. Tutte le schermate sono di sola lettura.",12,MUTED,false),16);
        tap(body,"ANALISI COMPLETA","Affidabilità di tutti i modelli e ripristini avanzati",BLUE,
            ()->startActivity(new Intent(this,BrainToolsActivity.class)));
        tap(body,"STATISTICHE VITE","Durata, energia, movimento netto e manipolazione",TEAL,
            ()->startActivity(new Intent(this,StatisticsActivity.class)));
        tap(body,"TORNA A ZERO","Riprendi la simulazione",MUTED,()->finish());
    }
    private void groupPage(LinearLayout body,String s){
        add(body,label(sectionTitle(s),27,WHITE,true),20);
        switch(s){
            case "BRAIN":
                note(body,"Unico decisore: 2.2. I modelli forniscono indizi verificati, non comandi paralleli.");
                tile(body,"NUCLEO 2.2",bCore?num(core.transitions())+" esperienze • "+
                    core.physicalReady()+"/16 azioni con previsione verificata":"Nessun salvataggio del nucleo",TEAL,"CORE");
                tile(body,"MEMORIA TEMPORALE",bTemporal?num(temporal.transitions)+
                    " transizioni • fiducia "+pct(temporal.confidence()):"In attesa di dati",VIOLET,"TEMP");
                tile(body,"CAUSA ED EFFETTO",bCausal?num(causal.experiments)+
                    " esperimenti":"In attesa di dati",BLUE,"CAUSE");
                tile(body,"SEQUENZE CAUSALI",bSequence?num(sequence.interventions())+
                    " interventi":"In attesa di dati",BLUE,"SEQUENCE");
                tile(body,"CONTINUITÀ DEGLI OBIETTIVI",bGoals?num(goals.confirmed)+
                    " progressi verificati":"In attesa di dati",GOLD,"GOALS");
                break;
            case "BODY":
                note(body,"Fisica del simbionte: 96 punti di superficie, "+SymbioteMatterV1.INNER+
                    " particelle interne e "+SymbioteMatterV1.SECTORS+" settori muscolari.");
                tile(body,"CORTECCIA MUSCOLARE",bMuscle?num(muscle.trainedOutcomes())+
                    " aggiornamenti":"Nessun checkpoint muscolare",VIOLET,"MUSCLE");
                tile(body,"PROPRIOCEZIONE",bProprio?pct(proprio.confidence())+
                    " affidabilità":"In attesa di dati",TEAL,"PROPRIO");
                tile(body,"MEMBRANA DISTRIBUITA",bDistributed?pct(distributed.confidence())+
                    " affidabilità":"In attesa di dati",VIOLET,"DISTRIBUTED");
                tile(body,"RESISTENZA E DEFORMAZIONE",bImpedance?pct(impedance.confidence())+
                    " affidabilità":"In attesa di dati",GOLD,"IMPEDANCE");
                tile(body,"INTEROCEZIONE",bInner?pct(inner.confidence())+
                    " affidabilità":"In attesa di dati",BLUE,"FLUID");
                tile(body,"TATTO / PRESE",bGates?num(gates.heldFrames)+
                    " frame di presa":"In attesa di dati",ORANGE,"GATES");
                break;
            case "PERCEPTION":
                note(body,"I sensi fisici osservano il mondo: nessun obiettivo nascosto è comunicato al cervello.");
                tile(body,"VISTA", "Luce percepita dalla membrana • 26D + segnali direzionali",TEAL,"VISION");
                tile(body,"NAVIGAZIONE",bNavigation?navigation.verifiedActions()+
                    "/16 effetti motori verificati":"In attesa di dati",BLUE,"NAV");
                tile(body,"MAPPA SPAZIALE",bSpatial?num(spatial.mappedCells())+
                    " celle ricordate":"In attesa di dati",VIOLET,"SPACE");
                tile(body,"OGGETTI E CONTINUITÀ",bObjects?num(objects.trackedViews())+
                    " corrispondenze visive":"In attesa di dati",GOLD,"OBJECTS");
                break;
            case "SKILL":
                skillPage(body);break;
            case "EVOLUTION":
                evolutionPage(body);break;
            case "PARAMS":
                note(body,"I numeri sono pesi reali salvati, non neuroni cresciuti o livelli di intelligenza.");
                tile(body,"PESI NUCLEO 2.2",num(core.parameterCount())+
                    " valori addestrabili • 12 per pagina",TEAL,"WEIGHTS_CORE");
                tile(body,"PESI MUSCOLARI",muscle.parameterCount()+
                    " parametri della corteccia reale",VIOLET,"WEIGHTS_MOTOR");
                LinearLayout links=panel(body,"ALTRI MODELLI APPRESI",BLUE);
                metric(links,"Propriocezione",num(BodyProprioception.PARAMETERS));
                metric(links,"Membrana",num(DistributedBodySense.PARAMETERS));
                metric(links,"Resistenza",num(LocalImpedanceSense.PARAMETERS));
                metric(links,"Interocezione",num(FluidInteroceptionV1.PARAMETERS));
                metric(links,"Mappa",num(SpatialPerception.PARAMETERS));
                metric(links,"Memoria temporale",num(TemporalWorkingMemory.PARAMETERS));
                note(links,"Queste dimensioni sono fisse. Tocca Corpo o Percezione per vedere verifiche e fiducia di ciascun modello.");
                break;
        }
        tap(body,"‹  TUTTE LE SEZIONI",null,TEAL,()->group("HOME"));
    }
    private void skillPage(LinearLayout body){
        LinearLayout k=panel(body,"SEQUENZE AUTONOME",GOLD);
        metric(k,"Conservate",absent(bSkills,num(skills.stored())));
        metric(k,"Consolidate",absent(bSkills,num(skills.consolidated())));
        metric(k,"Lunghezza massima",absent(bSkills,skills.longest()+" azioni"));
        metric(k,"Conferme massime",absent(bSkills,num(skills.strongestConfirmations())));
        metric(k,"Fiducia migliore",bSkills?pct(skills.bestConfidence()):"Non disponibile");
        metric(k,"Esecuzioni concluse",absent(bSkills,num(skills.completions)));
        metric(k,"Ripetizioni fallite",absent(bSkills,num(skills.failedReplays)));
        note(k,"Una sequenza è una capacità candidata soltanto dopo conferme ripetute. Può essere suggerita al 2.2, mai eseguita al suo posto.");
        tile(body,"ESPLORA LE SEQUENZE","Dettagli sulla loro affidabilità e memoria",VIOLET,"SKILLS");
        LinearLayout motor=panel(body,"COMPETENZE MOTORIE",TEAL);
        metric(motor,"Azioni con movimento verificato",bNavigation?
            navigation.verifiedActions()+" / 16":"Non disponibile");
        metric(motor,"Azioni con modello fisico affidabile",bCore?
            core.physicalReady()+" / 16":"Non disponibile");
        metric(motor,"Azioni con previsione energetica affidabile",bCore?
            core.energyReady()+" / 16":"Non disponibile");
        metric(motor,"Aggiornamenti della corteccia",bMuscle?
            num(muscle.trainedOutcomes()):"Non disponibile");
        note(motor,"Queste sono evidenze d'apprendimento, non prove che ZERO sappia già camminare bene.");
        LinearLayout goal=panel(body,"MANIPOLAZIONE E OBIETTIVI",BLUE);
        metric(goal,"Progressi obiettivo confermati",absent(bGoals,num(goals.confirmed)));
        metric(goal,"Prosecuzioni riuscite",absent(bGoals,num(goals.continued)));
        metric(goal,"Attraversamenti completi",absent(bGates,num(gates.realEntrances)));
        metric(goal,"Aperture della porta",absent(bGates,num(gates.opened)));
        tile(body,"CONTATTI E PRESE","Analisi della presa fisica e del cancello",ORANGE,"GATES");
        note(body,"La raccolta di energia, la manipolazione e il movimento si valutano soprattutto nelle Statistiche delle vite.");
        tap(body,"STATISTICHE DELLE VITE  ›",null,TEAL,
            ()->startActivity(new Intent(this,StatisticsActivity.class)));
    }
    private void comparison(LinearLayout body,String name,int metric,String unit){
        if(!bLives)return;
        int recent=lives.windowCount(0,25),previous=lives.windowCount(25,25);
        if(recent<1)return;
        double r=lives.average(metric,0,25);
        LinearLayout c=panel(body,name,BLUE);
        metric(c,"Ultime "+recent+" vite",dec(r)+" "+unit);
        if(previous>0){
            double old=lives.average(metric,25,25);
            metric(c,"Precedenti "+previous+" vite",dec(old)+" "+unit);
            metric(c,"Variazione osservata",(r>=old?"+":"")+dec(r-old)+" "+unit);
            double base=Math.max(.00001,Math.max(Math.abs(r),Math.abs(old)));
            comparisonBar(c,"PRECEDENTI",old/base,VIOLET);
            comparisonBar(c,"RECENTI",r/base,TEAL);
        }else note(c,"Non ci sono ancora vite precedenti da confrontare.");
    }
    private void comparisonBar(LinearLayout c,String name,double ratio,int tint){
        add(c,label(name,10,MUTED,true),10);
        LinearLayout bar=new LinearLayout(this);
        bar.setBackground(background(0xff233f51,0xff233f51,9,false));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(9));lp.topMargin=dp(5);
        c.addView(bar,lp);
        int percent=(int)(Math.max(0,Math.min(1,ratio))*1000);
        View fill=new View(this);fill.setBackground(background(tint,tint,9,false));
        bar.addView(fill,new LinearLayout.LayoutParams(0,-1,Math.max(0.001f,percent/1000f)));
        bar.addView(new View(this),new LinearLayout.LayoutParams(0,-1,
            Math.max(0.001f,1f-percent/1000f)));
    }
    private void evolutionPage(LinearLayout body){
        LinearLayout brain=panel(body,"APPRENDIMENTO DEL NUCLEO 2.2",TEAL);
        metric(brain,"Esperienze reali",absent(bCore,num(core.transitions())));
        metric(brain,"Aggiornamenti addestrati",absent(bCore,num(core.trainedTransitions())));
        metric(brain,"Verifiche indipendenti",absent(bCore,num(core.heldoutTransitions())));
        metric(brain,"Replay di esperienza",absent(bCore,num(core.replayUpdates())));
        metric(brain,"Decisioni totali",absent(bCore,num(core.decisionCount())));
        metric(brain,"Azioni con previsioni affidabili",bCore?
            core.physicalReady()+" / 16":"Non disponibile");
        note(brain,"L'architettura non cresce da 10 a 11 neuroni: i parametri sono fissi e si modificano con l'esperienza.");
        LinearLayout cortex=panel(body,"APPRENDIMENTO DEL CORPO",VIOLET);
        metric(cortex,"Decisioni motorie",absent(bMuscle,num(muscle.decisionCount())));
        metric(cortex,"Aggiornamenti appresi",absent(bMuscle,num(muscle.trainedOutcomes())));
        metric(cortex,"Verifiche indipendenti",absent(bMuscle,num(muscle.heldoutOutcomes())));
        metric(cortex,"Abilità temporali consolidate",absent(bSkills,num(skills.consolidated())));
        note(cortex,"Il numero di aggiornamenti non equivale automaticamente a un miglioramento del movimento.");
        add(body,label("CAMBIAMENTO DEL COMPORTAMENTO",18,GOLD,true),22);
        if(!bLives||lives.windowCount(0,25)==0){
            LinearLayout box=panel(body,"VITE COMPLETATE",GOLD);
            note(box,"Servono vite terminate e salvate per confrontare periodi diversi. Non è possibile inventare uno storico precedente.");
        }else{
            comparison(body,"SOPRAVVIVENZA",LifeStatistics.DURATION,"s");
            comparison(body,"RISORSE RACCOLTE",LifeStatistics.GOOD,"");
            comparison(body,"MOVIMENTO NETTO",LifeStatistics.NET,"U");
            comparison(body,"DISCHI SPOSTATI",LifeStatistics.DISKS,"");
            comparison(body,"ENERGIA RACCOLTA",LifeStatistics.GAIN,"");
            note(body,"Le differenze tra vite dipendono anche da posizione iniziale, casualità e ambiente. Non provano da sole l'apprendimento.");
        }
        tap(body,"STORICO COMPLETO DELLE VITE  ›",null,TEAL,
            ()->startActivity(new Intent(this,StatisticsActivity.class)));
    }
    private void details(LinearLayout body,String id){
        add(body,label(title(id),25,WHITE,true),18);
        switch(id){
            case "CORE":
                detailCore(body);break;
            case "MUSCLE":
                detailMuscle(body);break;
            case "PROPRIO":
                sensor(body,bProprio,proprio.transitions,proprio.validations,
                    proprio.confidence(),proprio.choiceInfluences,
                    "Comando richiesto e forma realmente raggiunta sono segnali distinti. Nessuna direzione artificiale.",TEAL);break;
            case "DISTRIBUTED":
                sensor(body,bDistributed,distributed.transitions,distributed.validations,
                    distributed.confidence(),distributed.choiceInfluences,
                    "Mappa locale dei 96 punti: tatto, deformazione, adesione e trazione.",VIOLET);break;
            case "IMPEDANCE":
                sensor(body,bImpedance,impedance.transitions,impedance.validations,
                    impedance.confidence(),impedance.choiceInfluences,
                    "Misura resistenza, compressione e scorrimento reale della membrana.",GOLD);
                if(bImpedance){LinearLayout card=panel(body,"CONTATTI REALI",GOLD);
                    metric(card,"Resistenze osservate",num(impedance.localResistances));}
                break;
            case "FLUID":
                sensor(body,bInner,inner.transitions,inner.validations,
                    inner.confidence(),inner.choiceInfluences,
                    "Interocezione interna: pressione, densità, scorrimento, tensione e movimento delle particelle.",BLUE);
                break;
            case "SPACE":
                sensor(body,bSpatial,spatial.transitions,spatial.validations,
                    spatial.confidence(),spatial.choiceInfluences,
                    "Mappa dei raggi realmente percepiti dalla superficie.",BLUE);
                if(bSpatial){LinearLayout card=panel(body,"MAPPA",TEAL);
                    metric(card,"Celle ricordate",num(spatial.mappedCells()));
                    metric(card,"Celle bloccate",num(spatial.blockedCells()));
                    metric(card,"Spostamento ricostruito",dec(spatial.odometryTravel())+" U");}
                break;
            case "TEMP":
                sensor(body,bTemporal,temporal.transitions,temporal.validationTransitions,
                    temporal.confidence(),temporal.contextDecisions,
                    "Prevede sequenze temporali: consiglia il decisore solo quando supera i controlli indipendenti.",VIOLET);
                break;
            case "CAUSE":
                sensor(body,bCausal,causal.experiments,causal.validations,
                    causal.confidence(),causal.choiceInfluences,
                    "Distingue gli effetti effettivamente osservati delle azioni e confronta spiegazioni alternative.",BLUE);
                break;
            case "SEQUENCE":
                LinearLayout seq=panel(body,"SEQUENZE VERIFICATE",BLUE);
                metric(seq,"Stato",bSequence?"Dati salvati":"Non disponibile");
                if(bSequence){
                    metric(seq,"Interventi osservati",num(sequence.interventions()));
                    metric(seq,"Collegamenti confermati",num(sequence.delayedLinks()));
                    metric(seq,"Verifiche esterne",num(sequence.validations()));
                    metric(seq,"Fiducia",pct(sequence.confidence()));
                }
                note(seq,"Associa eventi realmente concatenati. Non dispone dell'identità segreta degli oggetti.");
                break;
            case "VISION":
                LinearLayout sight=panel(body,"VISTA FISICA",TEAL);
                metric(sight,"Retina principale","26 segnali percettivi");
                metric(sight,"Direzioni ottiche","8 settori direzionali");
                metric(sight,"Raggi spaziali",SpatialPerception.RAYS+"");
                metric(sight,"Origine", "Membrana reale");
                note(sight,"Le immagini istantanee della scena non sono archiviate come una fotografia. Puoi vedere qui solo ciò che i modelli hanno appreso dall'esperienza.");
                tile(body,"ESPERIENZA VISIVA", "Contatti energetici e azioni apprese",BLUE,"NAV");
                tile(body,"MAPPA DEL MONDO","Raggi e celle osservate",VIOLET,"SPACE");
                break;
            case "NAV":
                LinearLayout nav=panel(body,"APPRENDIMENTO VISIVO",TEAL);
                metric(nav,"Stato",bNavigation?"Dati salvati":"Non disponibile");
                if(bNavigation){
                    metric(nav,"Azioni con movimento verificato",navigation.verifiedActions()+" / 16");
                    metric(nav,"Effetti spettrali osservati",num(navigation.knownEffects()));
                    metric(nav,"Energia positiva",num(navigation.positiveOutcomes));
                    metric(nav,"Energia negativa",num(navigation.negativeOutcomes));
                    metric(nav,"Decisioni influenzate",num(navigation.choicesInfluenced));
                }
                note(nav,"Non riceve posizioni nascoste delle energie: apprende a partire da luce, spostamento e conseguenze.");
                break;
            case "OBJECTS":
                LinearLayout obj=panel(body,"CONTINUITÀ DEGLI OGGETTI",GOLD);
                metric(obj,"Stato",bObjects?"Dati salvati":"Non disponibile");
                if(bObjects){
                    metric(obj,"Tracce osservate",num(objects.trackedViews()));
                    metric(obj,"Ambiguità",num(objects.ambiguousViews()));
                    metric(obj,"Contatti energetici associati",num(objects.delayedEnergyAssociations()));
                    metric(obj,"Fiducia",pct(objects.confidence()));
                }
                note(obj,"Gli oggetti sono anonimi; ZERO deve verificarne la continuità attraverso i propri sensi.");
                break;
            case "GOALS":
                LinearLayout goal=panel(body,"INTENZIONI APPRESE",GOLD);
                metric(goal,"Stato",bGoals?"Dati salvati":"Non disponibile");
                if(bGoals){
                    metric(goal,"Progressi verificati",num(goals.confirmed));
                    metric(goal,"Prosecuzioni riuscite",num(goals.continued));
                    metric(goal,"Abbandoni",num(goals.abandoned));
                    metric(goal,"Consigli forniti",num(goals.advice));
                }
                note(goal,"Un obiettivo è una tendenza a continuare un progresso percepito, non una meta preimpostata.");
                break;
            case "GATES":
                LinearLayout gate=panel(body,"TATTO, PRESE E PORTA",ORANGE);
                metric(gate,"Stato",bGates?"Dati salvati":"Non disponibile");
                if(bGates){
                    metric(gate,"Contatti della membrana",num(gates.skinTouchFrames)+" frame");
                    metric(gate,"Presa mantenuta",num(gates.heldFrames)+" frame");
                    metric(gate,"Presa bilaterale",num(gates.opposedFrames)+" frame");
                    metric(gate,"Aperture",num(gates.opened));
                    metric(gate,"Ingressi reali",num(gates.realEntrances));
                    metric(gate,"Attraversamenti parziali",num(gates.partialEntrances));
                }
                note(gate,"Sono contatti misurati, non miglioramenti garantiti.");
                break;
            case "SKILLS":
                LinearLayout k=panel(body,"SEQUENZE DEL 2.2",GOLD);
                metric(k,"Consolidate",absent(bSkills,num(skills.consolidated())));
                metric(k,"Sequenze totali",absent(bSkills,num(skills.stored())));
                metric(k,"Completate",absent(bSkills,num(skills.completions)));
                metric(k,"Fallimenti",absent(bSkills,num(skills.failedReplays)));
                metric(k,"Fiducia massima",bSkills?pct(skills.bestConfidence()):"Non disponibile");
                metric(k,"Passi massimi",absent(bSkills,num(skills.longest())));
                note(k,"Queste abilità vengono scoperte da esperienze reali e non sono script motori.");
                break;
            case "WEIGHTS_CORE":
                weightPage(body,true);break;
            case "WEIGHTS_MOTOR":
                weightPage(body,false);break;
        }
        tap(body,"‹  TORNA A "+sectionTitle(section),null,TEAL,()->{module="";render();});
    }
    private void sensor(LinearLayout body,boolean loaded,long count,int validations,
                        float confidence,int influences,String caption,int tint){
        LinearLayout c=panel(body,"APPRENDIMENTO SENSORIALE",tint);
        metric(c,"Stato",loaded?"Dati salvati":"Non disponibile");
        if(loaded){
            metric(c,"Esperienze",num(count));
            metric(c,"Verifiche indipendenti",num(validations));
            metric(c,"Affidabilità",pct(confidence));
            metric(c,"Decisioni influenzate",num(influences));
        }
        note(c,caption);
    }
    private void detailCore(LinearLayout body){
        LinearLayout c=panel(body,"UNICO DECISORE 2.2",TEAL);
        metric(c,"Stato",bCore?"Dati salvati":"Non disponibile");
        metric(c,"Ingressi",RecurrentCoreV22.F+"");
        metric(c,"Stato interno",RecurrentCoreV22.H+"");
        metric(c,"Azioni",RecurrentCoreV22.A+"");
        metric(c,"Pesi addestrabili",num(core.parameterCount()));
        if(bCore){
            metric(c,"Esperienze vissute",num(core.transitions()));
            metric(c,"Apprendimenti",num(core.trainedTransitions()));
            metric(c,"Verifiche indipendenti",num(core.heldoutTransitions()));
            metric(c,"Azioni con previsioni fidate",core.physicalReady()+" / 16");
            metric(c,"Previsioni energetiche fidate",core.energyReady()+" / 16");
        }
        note(c,"Le dimensioni della rete non crescono da sole. Evolvono i pesi e la qualità di previsione, misurabile solo su dati indipendenti.");
        tile(body,"ESPLORA I PESI","I singoli numeri, suddivisi per funzione",VIOLET,"WEIGHTS_CORE");
    }
    private void detailMuscle(LinearLayout body){
        LinearLayout c=panel(body,"MOTOR CONTROL DEL SIMBIONTE",VIOLET);
        metric(c,"Stato",bMuscle?"Dati salvati":"Non disponibile");
        metric(c,"Pesi addestrabili",muscle.parameterCount()+"");
        metric(c,"Settori della membrana",SymbioteMatterV1.SECTORS+"");
        metric(c,"Comandi per settore",SymbioteMatterV1.OUTPUTS+"");
        if(bMuscle){
            metric(c,"Decisioni muscolari",num(muscle.decisionCount()));
            metric(c,"Aggiornamenti appresi",num(muscle.trainedOutcomes()));
            metric(c,"Verifiche indipendenti",num(muscle.heldoutOutcomes()));
        }
        note(c,"Il cervello sceglie l'azione; questa corteccia ne traduce gli effetti in spinte, tensione e adesione sul corpo.");
        tile(body,"ESPLORA 84 PESI", "Paginazione read-only dei parametri muscolari",TEAL,"WEIGHTS_MOTOR");
    }
    private void weightPage(LinearLayout body,boolean brain){
        int total=brain?core.parameterCount():muscle.parameterCount();
        int offset=brain?coreOffset:motorOffset;
        offset=Math.max(0,Math.min(Math.max(0,total-1),offset/PAGE*PAGE));
        if(brain)coreOffset=offset;else motorOffset=offset;
        LinearLayout c=panel(body,brain?"NUCLEO 2.2":"CORTECCIA MUSCOLARE",brain?TEAL:VIOLET);
        boolean loaded=brain?bCore:bMuscle;
        metric(c,"Pesi disponibili",num(total));
        metric(c,"Dati salvati",loaded?"Valori reali":"Non ancora disponibili");
        note(c,"Questi numeri sono parametri addestrabili, non livelli di potenza o neuroni aggiunti. Consulta senza modificare l'apprendimento.");
        if(loaded){
            metric(c,"Intervallo",(offset+1)+" – "+Math.min(offset+PAGE,total)+" di "+num(total));
            for(int i=offset;i<Math.min(total,offset+PAGE);i++){
                String label=brain?bank(i):"Comando muscolare";
                float value=brain?core.parameterAt(i):muscle.parameterAt(i);
                metric(c,"#"+(i+1)+"  "+label,String.format(Locale.ITALY,"%+.5f",value));
            }
            final boolean b=brain;
            tap(body,"‹  12 PRECEDENTI","Pagina precedente",BLUE,()->{
                if(b)coreOffset=Math.max(0,coreOffset-PAGE);
                else motorOffset=Math.max(0,motorOffset-PAGE);
                render();
            });
            tap(body,"12 SUCCESSIVI  ›","Pagina successiva",TEAL,()->{
                if(b)coreOffset=Math.min(total-1,coreOffset+PAGE);
                else motorOffset=Math.min(total-1,motorOffset+PAGE);
                render();
            });
        }
    }
    private String bank(int index){
        int d=RecurrentCoreV22.dynamicsParameters(),h=RecurrentCoreV22.headParameters();
        return index<d?"Dinamica":index<d+h?"Azioni":
            index<d+2*h?"Energia":"Rischio";
    }
    /** Lightweight software 3D orbit graphic. Not a representation of neuron
     * count, not a generated chart, not used by the learner. 11fps max. */
    private final class NeuralView extends View {
        private final Paint paint=new Paint(3);
        NeuralView(){super(BrainStatsActivity.this);setContentDescription("Rete neurale decorativa tridimensionale in movimento");}
        @Override protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            float w=getWidth(),h=getHeight();
            float centerX=w*.5f,centerY=h*.48f;
            float unit=Math.min(w*.38f,h*.39f);
            float angle=(SystemClock.uptimeMillis()%18000)/18000f*6.283185f;
            final int count=44;
            float[] xx=new float[count],yy=new float[count],depth=new float[count];
            for(int i=0;i<count;i++){
                float longitude=(float)(i*2.39996323);
                float latitude=(float)Math.acos(1-2*(i+.5f)/count);
                float a=longitude+angle;
                float px=(float)(Math.cos(a)*Math.sin(latitude));
                float py=(float)(Math.cos(latitude));
                float z=(float)(Math.sin(a)*Math.sin(latitude));
                float perspective=1f+.22f*z;
                xx[i]=centerX+unit*px*perspective;
                yy[i]=centerY+unit*py*.78f*perspective;
                depth[i]=z;
            }
            for(int i=0;i<count;i++){
                for(int j=i+1;j<count;j++){
                    if((j-i!=3&&j-i!=8&&j-i!=13)||Math.abs(depth[j]-depth[i])>1.45f)continue;
                    int alpha=(int)(34+28*(depth[i]+depth[j]+2)/4f);
                    paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(dp(.7f));
                    paint.setColor(Color.argb(alpha,108,235,221));
                    canvas.drawLine(xx[i],yy[i],xx[j],yy[j],paint);
                }
            }
            for(int i=0;i<count;i++){
                float bright=(depth[i]+1)*.5f;
                int alpha=(int)(110+145*bright);
                int tint=i%7==0?VIOLET:TEAL;
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(22,Color.red(tint),Color.green(tint),Color.blue(tint)));
                canvas.drawCircle(xx[i],yy[i],dp(5+bright*3),paint);
                paint.setColor(Color.argb(alpha,Color.red(tint),Color.green(tint),Color.blue(tint)));
                canvas.drawCircle(xx[i],yy[i],dp(1.5f+bright*1.8f),paint);
            }
            paint.setStyle(Paint.Style.FILL);paint.setColor(0x55d1fff3);
            canvas.drawCircle(centerX,centerY,dp(3),paint);
            if(getWindowVisibility()==VISIBLE)postInvalidateDelayed(90);
        }
    }
    private final class GridView extends View{
        private final Paint p=new Paint(3);
        GridView(){super(BrainStatsActivity.this);}
        @Override protected void onDraw(Canvas c){
            int step=dp(54);
            p.setColor(0xff102739);p.setStrokeWidth(dp(.7f));
            for(int x=0;x<getWidth();x+=step)c.drawLine(x,0,x,getHeight(),p);
            for(int y=0;y<getHeight();y+=step)c.drawLine(0,y,getWidth(),y,p);
        }
    }
}