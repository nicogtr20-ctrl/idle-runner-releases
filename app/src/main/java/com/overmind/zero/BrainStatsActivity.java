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
    private int coreOffset=0,motorOffset=0,localOffset=0,selectedSkill=0;
    private RecurrentCoreV22 core;
    private SymbioteMotorHeadV1 muscle;
    private MotorDurationV22 motorDuration;
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
    private boolean bCoordination,bLocal,bFast,bMotorMemory,bDuration,bCore,bMuscle,bProprio,bDistributed,bImpedance,bInner,
        bSpatial,bNavigation,bObjects,bCausal,bSequence,bTemporal,bSkills,bGoals,
        bGates,bLives;
    private long shownAt;
    private boolean firstResume=true;

    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        prefs=getSharedPreferences(SaveSlotStore.WORKING,MODE_PRIVATE);
        loadReadOnly();
        render();
    }
    @Override protected void onResume(){
        super.onResume();
        if(firstResume){firstResume=false;return;}
        // Returning from analysis/reset or stats must never show stale saved
        // cortex weights or obsolete skill counts.
        if(prefs!=null){loadReadOnly();render();}
    }
    private void loadReadOnly(){
        core=new RecurrentCoreV22();
        muscle=new SymbioteMotorHeadV1();
        motorDuration=new MotorDurationV22();
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
        bLocal=muscle.restoreLocal(prefs.getString(SymbioteMotorHeadV1.LOCAL_KEY,""));
        bCoordination=muscle.restoreCoordination(prefs.getString(SymbioteCoordinationV162.KEY,""));
        bFast=muscle.restoreFast(prefs.getString(FastSomaticCircuitV167.KEY,""));
        bMotorMemory=muscle.restoreMemory(prefs.getString(SymbioteMotorSkillMemoryV169.KEY,""));
        bDuration=motorDuration.restore(prefs.getString(MotorDurationV22.KEY,""));
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
            if(id.startsWith("section-")){
                switch(id){
                    case "section-brain":group("BRAIN");return;
                    case "section-body":group("BODY");return;
                    case "section-perception":group("PERCEPTION");return;
                    case "section-skill":group("SKILL");return;
                    case "section-evolution":group("EVOLUTION");return;
                    case "section-params":group("PARAMS");return;
                }
            }
            module=id;
            render();
        });
    }
    private void homePair(LinearLayout parent,String left,String leftHint,int leftTint,
                          String leftId,String right,String rightHint,int rightTint,String rightId){
        LinearLayout row=new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,-2);rp.topMargin=dp(11);
        parent.addView(row,rp);
        homeCard(row,left,leftHint,leftTint,leftId,true);
        homeCard(row,right,rightHint,rightTint,rightId,false);
    }
    private void homeCard(LinearLayout row,String name,String subtitle,int accent,String id,
                          boolean first){
        LinearLayout card=vertical();card.setPadding(dp(12),dp(15),dp(12),dp(15));
        card.setBackground(background(0xff19354b,0xff102235,17,true));
        // Fixed equal-height dashboard cards: wrap-content rows must not
        // measure MATCH_PARENT children to zero on some Android devices.
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,dp(127),1);
        if(first)cp.rightMargin=dp(5);else cp.leftMargin=dp(5);
        row.addView(card,cp);
        add(card,label(name,15,accent,true),0);
        add(card,label(subtitle,11,MUTED,false),6);
        card.setMinimumHeight(dp(115));
        card.setContentDescription(name+". "+subtitle);
        card.setOnClickListener(v->{
            switch(id){
                case "section-brain":group("BRAIN");break;
                case "section-body":group("BODY");break;
                case "section-perception":group("PERCEPTION");break;
                case "section-skill":group("SKILL");break;
                case "section-evolution":group("EVOLUTION");break;
                case "section-params":group("PARAMS");break;
            }
        });
    }
    private void group(String id){section=id;module="";render();}
    private String title(String id){
        switch(id){
            case "CORE":return "Nucleo ricorrente";
            case "MUSCLE":return "Corteccia muscolare";
            case "DURATION":return "Durata motoria appresa";
            case "TEMP":return "Memoria temporale";
            case "SKILLS":return "Abilità consolidate";
            case "SKILL_ENTRY":return "Sequenza #"+(selectedSkill+1);
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
            case "WEIGHTS_LOCAL":return "Rete regionale corticale";
            case "COORDINATION":return "Coordinazione sensomotoria";
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
        LinearLayout growth=panel(body,"RETE NEURALE REALE",TEAL);
        metric(growth,"Neuroni attivi",bCore?num(core.totalNeurons()):"Non ancora disponibile");
        metric(growth,"Nucleo originario",bCore?num(core.initialNeurons()):"—");
        metric(growth,"Nuovi neuroni accettati",bCore?num(core.addedNeurons()):"—");
        if(bCore){
            metric(growth,"Candidature testate",num(core.growthAttempts()));
            metric(growth,"Scartate",num(core.rejectedGrowths()));
            metric(growth,"Nuova candidatura",core.growingCandidate()?
                "In verifica sulle esperienze":"Nessuna in corso");
        }
        note(growth,"Ogni punto della rete animata rappresenta un vero neurone ricorrente: verde = iniziale, viola = aggiunto dopo test indipendenti. Nessun punto fittizio.");
        homePair(body,"◉ CERVELLO","Memorie, nucleo e causalità",TEAL,"section-brain",
            "⬡ CORPO","Muscoli, tatto e sensori",VIOLET,"section-body");
        homePair(body,"◉ PERCEZIONE","Vista, mondo e oggetti",BLUE,"section-perception",
            "✦ ABILITÀ","Sequenze e capacità",GOLD,"section-skill");
        homePair(body,"↗ EVOLUZIONE","Esperienza e progressi",ORANGE,"section-evolution",
            "⌘ PARAMETRI","Pesi neurali reali",TEAL,"section-params");
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
                tile(body,"NUCLEO 2.2",bCore?num(core.totalNeurons())+" neuroni • "+
                    core.addedNeurons()+" aggiunti • "+core.physicalReady()+
                    "/16 previsioni fidate":"Nessun salvataggio del nucleo",TEAL,"CORE");
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
                tile(body,"MEMORIA MOTORIA DEL SIMBIONTE",bMotorMemory?
                    muscle.skillMemory().verifiedCount()+" tecniche verificate • "+
                    muscle.skillMemory().candidatesCount()+" in osservazione":
                    "Nessuna tecnica verificata ancora",TEAL,"MUSCLE");
                tile(body,"COORDINAZIONE MOTORIA",bCoordination?
                     num(muscle.coordination().trainedIntervals())+" intervalli fisici appresi":
                     "Nuovo modello sul movimento reale",TEAL,"COORDINATION");
                tile(body,"PLASTICITÀ LOCALE",bLocal?num(muscle.localTrainedOutcomes())+
                     " aggiornamenti • "+muscle.localParameterCount()+" connessioni":"Nuove connessioni a zero",TEAL,"MUSCLE");
                tile(body,"DURATA DEL MOVIMENTO",bDuration?num(motorDuration.continuations())+
                     " prosecuzioni • "+num(motorDuration.evaluated())+
                     " sequenze valutate":"Inizia dagli esperimenti reali",GOLD,"DURATION");
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
                tile(body,"VISTA DALLA MEMBRANA","Luce rilevata dal corpo reale",TEAL,"VISION");
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
                note(body,"I numeri sono pesi reali del nucleo: comprendono anche le connessioni dei neuroni aggiunti, solo dopo crescita validata.");
                tile(body,"PESI NUCLEO 2.2",num(core.parameterCount())+
                    " valori reali (incluse "+core.addedNeurons()+" nuove unità) • 12 per pagina",TEAL,"WEIGHTS_CORE");
                tile(body,"PESI MUSCOLARI",muscle.parameterCount()+
                    " parametri condivisi della corteccia",VIOLET,"WEIGHTS_MOTOR");
                tile(body,"CONNESSIONI REGIONALI",muscle.localParameterCount()+
                    " parametri distinti tra 16 settori",TEAL,"WEIGHTS_LOCAL");
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
        if(bSkills&&skills.stored()>0){
            add(body,label("SEQUENZE SCOPERTE",17,GOLD,true),20);
            for(int i=0;i<skills.stored();i++){
                final int index=i;
                tap(body,"SEQUENZA #"+(i+1),
                    skills.skillLengthAt(i)+" azioni • "+skills.skillConfirmationsAt(i)+
                    " conferme • fiducia "+pct(skills.skillConfidenceAt(i)),GOLD,()->{
                        selectedSkill=index;module="SKILL_ENTRY";render();
                    });
            }
        }else note(body,"ZERO non ha ancora conservato sequenze temporali nel salvataggio.");
        tile(body,"RIEPILOGO ABILITÀ","Consolidamento, esecuzioni ed errori",VIOLET,"SKILLS");
        LinearLayout motor=panel(body,"COMPETENZE MOTORIE",TEAL);
        metric(motor,"Azioni con movimento verificato",bNavigation?
            navigation.verifiedActions()+" / 16":"Non disponibile");
        metric(motor,"Azioni con modello fisico affidabile",bCore?
            core.physicalReady()+" / 16":"Non disponibile");
        metric(motor,"Azioni con previsione energetica affidabile",bCore?
            core.energyReady()+" / 16":"Non disponibile");
        metric(motor,"Aggiornamenti della corteccia",bMuscle?
            num(muscle.trainedOutcomes()):"Non disponibile");
        metric(motor,"Tecniche motorie confermate su vite diverse",bMotorMemory?
            muscle.skillMemory().verifiedCount()+" / "+
            SymbioteMotorSkillMemoryV169.MAX_CHAMPIONS:"Non disponibili");
        metric(motor,"Candidati motori da verificare",bMotorMemory?
            num(muscle.skillMemory().candidatesCount()):"Non disponibili");
        metric(motor,"Configurazioni motorie recuperate",bMotorMemory?
            num(muscle.skillMemory().recoveryCount()):"Non disponibili");
        metric(motor,"Verifiche fisiche indipendenti",bMotorMemory?
            num(muscle.skillMemory().measuredHeldouts()):"Non disponibili");
        metric(motor,"Connessioni motorie veloci",bFast?
            num(muscle.fastCircuit().parameterCount()):"Non disponibili");
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
        metric(brain,"Neuroni ricorrenti",absent(bCore,
            num(core.initialNeurons())+" iniziali + "+num(core.addedNeurons())+
            " aggiunti = "+num(core.totalNeurons())));
        metric(brain,"Proposte di crescita",absent(bCore,num(core.growthAttempts())));
        metric(brain,"Proposte rifiutate",absent(bCore,num(core.rejectedGrowths())));
        metric(brain,"Ultimo miglioramento verificato",bCore?
            pct(core.lastGrowthGain()):"Non disponibile");
        metric(brain,"Azioni con previsioni affidabili",bCore?
            core.physicalReady()+" / 16":"Non disponibile");
        note(brain,"La neurogenesi aggiunge vere unità ricorrenti e connessioni ai punteggi del 2.2 solo se migliorano le previsioni energetiche su esperienze mai usate per addestrarle. Se non migliorano, vengono scartate.");
        LinearLayout cortex=panel(body,"APPRENDIMENTO DEL CORPO",VIOLET);
        metric(cortex,"Decisioni motorie",absent(bMuscle,num(muscle.decisionCount())));
        metric(cortex,"Aggiornamenti appresi",absent(bMuscle,num(muscle.trainedOutcomes())));
         metric(cortex,"Azioni mantenute",absent(bDuration,num(motorDuration.continuations())));
         metric(cortex,"Sequenze motorie esaminate",absent(bDuration,num(motorDuration.evaluated())));
        metric(cortex,"Verifiche indipendenti",absent(bMuscle,num(muscle.heldoutOutcomes())));
        metric(cortex,"Tecniche motorie verificate",bMotorMemory?
            num(muscle.skillMemory().verifiedCount()):"Non disponibili");
        metric(cortex,"Recuperi di tecniche consolidate",bMotorMemory?
            num(muscle.skillMemory().recoveryCount()):"Non disponibili");
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
             case "DURATION":
                 detailDuration(body);break;
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
            case "SKILL_ENTRY":
                LinearLayout entry=panel(body,"DETTAGLIO DELLA SEQUENZA APPRESA",GOLD);
                if(!bSkills||selectedSkill<0||selectedSkill>=skills.stored()){
                    note(entry,"La sequenza non è presente nel checkpoint.");
                }else{
                    metric(entry,"Numero", "#"+(selectedSkill+1));
                    metric(entry,"Lunghezza",num(skills.skillLengthAt(selectedSkill))+" azioni");
                    metric(entry,"Conferme",num(skills.skillConfirmationsAt(selectedSkill)));
                    metric(entry,"Fallimenti",num(skills.skillFailuresAt(selectedSkill)));
                    metric(entry,"Completamenti",num(skills.skillCompletionsAt(selectedSkill)));
                    metric(entry,"Affidabilità",pct(skills.skillConfidenceAt(selectedSkill)));
                    int[] actions=skills.skillActionsAt(selectedSkill);
                    StringBuilder chain=new StringBuilder();
                    for(int i=0;i<actions.length;i++){
                        if(i>0)chain.append("  →  ");
                        chain.append("A").append(actions[i]+1);
                    }
                    line(entry,"Sequenza delle azioni",chain.toString(),
                        "Le A1–A16 sono intenzioni apprese; il modello decide sempre se seguirle.",TEAL);
                }
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
            case "WEIGHTS_LOCAL":
                localWeightPage(body);break;
            case "COORDINATION":
                detailCoordination(body);break;
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
        metric(c,"Neuroni ricorrenti attuali",bCore?num(core.totalNeurons()):"Non disponibile");
        metric(c,"Originari / aggiunti",bCore?
            core.initialNeurons()+" / "+core.addedNeurons():"Non disponibile");
        metric(c,"Tentativi / scarti",bCore?
            core.growthAttempts()+" / "+core.rejectedGrowths():"Non disponibile");
        if(bCore){
            metric(c,"Esperienze vissute",num(core.transitions()));
            metric(c,"Apprendimenti",num(core.trainedTransitions()));
            metric(c,"Verifiche indipendenti",num(core.heldoutTransitions()));
            metric(c,"Azioni con previsioni fidate",core.physicalReady()+" / 16");
            metric(c,"Previsioni energetiche fidate",core.energyReady()+" / 16");
        }
        note(c,"Questa rete parte da 24 neuroni ricorrenti e può aumentare gradualmente con vere unità trainabili. Nessun neurone è aggiunto per decorazione o per premiare una vita lunga.");
        tile(body,"ESPLORA I PESI","I singoli numeri, suddivisi per funzione",VIOLET,"WEIGHTS_CORE");
    }
    private void detailDuration(LinearLayout body){
         LinearLayout c=panel(body,"DURATA DELLE AZIONI 2.2",GOLD);
         metric(c,"Stato",bDuration?"Esperienze salvate":"Nessuna esperienza");
         if(bDuration){
             metric(c,"Azioni iniziate",num(motorDuration.starts()));
             metric(c,"Prosecuzioni",num(motorDuration.continuations()));
             metric(c,"Interruzioni",num(motorDuration.interruptions()));
             metric(c,"Sequenze valutate",num(motorDuration.evaluated()));
             for(int t=0;t<MotorDurationV22.OPTIONS;t++){
                 int train=0,test=0;
                 for(int a=0;a<RecurrentCoreV22.A;a++){
                     train+=motorDuration.trained(a,t);
                     test+=motorDuration.heldout(a,t);
                 }
                 metric(c,"Opzione "+new int[]{1,2,4}[t]+" intervalli",train+
                     " esperienze + "+test+" verifiche escluse dal training");
             }
         }
         note(c,"Il nucleo ricorrente sceglie la direzione. ZERO esplora la durata dell'azione con conseguenze fisiche reali. Ogni 0,65 s verifica pericolo, esplorazione e alternative.");
     }
     private void detailMuscle(LinearLayout body){
        LinearLayout c=panel(body,"CONTROLLO MUSCOLARE DEL SIMBIONTE",VIOLET);
        metric(c,"Stato",bMuscle?"Dati salvati":"Non disponibile");
        metric(c,"Pesi originari condivisi",muscle.parameterCount()+"");
        metric(c,"Connessioni regionali",muscle.localParameterCount()+"");
        metric(c,"Aggiornamenti regionali",bLocal?num(muscle.localTrainedOutcomes()):"0 • memoria nuova");
        metric(c,"Settori della membrana",SymbioteMatterV1.SECTORS+"");
        metric(c,"Esiti di movimento osservati",num(muscle.coordination().intervals()));
        metric(c,"Coordinazione allenata",num(muscle.coordination().trainedIntervals()));
        metric(c,"Comandi per settore",SymbioteMatterV1.OUTPUTS+"");
        if(bMuscle){
            metric(c,"Decisioni muscolari",num(muscle.decisionCount()));
            metric(c,"Aggiornamenti appresi",num(muscle.trainedOutcomes()));
            metric(c,"Verifiche indipendenti",num(muscle.heldoutOutcomes()));
        }
        note(c,"Il cervello sceglie l'azione; questa corteccia ne traduce gli effetti in spinte, tensione e adesione sul corpo.");
        tile(body,"ESPLORA 84 PESI", "Rete condivisa originale: dati invariati",TEAL,"WEIGHTS_MOTOR");
        tile(body,"ESPLORA 320 CONNESSIONI LOCALI", "Plasticità separata per settore e comando",VIOLET,"WEIGHTS_LOCAL");
        tile(body,"COORDINAZIONE REALE", "Spinte antagoniste e movimenti eseguiti",TEAL,"COORDINATION");
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
    private void detailCoordination(LinearLayout body){
        LinearLayout c=panel(body,"COORDINAZIONE FISICA — ZERO 2.2",TEAL);
        SymbioteCoordinationV162 k=muscle.coordination();
        metric(c,"Dati salvati",bCoordination?"Sì • movimento reale":"Non ancora osservati");
        metric(c,"Frame materiali verificati",num(k.frames()));
        metric(c,"Intervalli fisici osservati",num(k.intervals()));
        metric(c,"Intervalli allenati",num(k.trainedIntervals()));
        metric(c,"Validazioni senza training",num(k.heldoutIntervals()));
        if(k.intervals()>0){
            metric(c,"Spinte nominali contrapposte",pct(k.cancellation()));
            metric(c,"Realizzazione intenzione",String.format(Locale.ITALY,
                "%+.1f%%",k.intentRealization()*100f));
        }
        note(c,"Spinte nominali contrapposte: confronto vettoriale dei 16 comandi, non prova che l'attrito sia sprecato. Direzione realizzata: proiezione misurata del baricentro sull'azione scelta dal cervello; valori negativi significano movimento contrario. Solo la corteccia muscolare locale apprende questo esito, MAI il valore del nucleo 2.2.");
        for(int a=0;a<8;a++)if(k.actionSamples(a)>0)
            metric(c,"Azione "+a+" • campioni",k.actionSamples(a)+
                " • media "+String.format(Locale.ITALY,"%+.2f",k.actionBaseline(a)));
    }
    private void localWeightPage(LinearLayout body){
        int total=muscle.localParameterCount();
        int offset=Math.max(0,Math.min(Math.max(0,total-1),localOffset/PAGE*PAGE));
        localOffset=offset;
        LinearLayout c=panel(body,"CONNESSIONI MOTORIE REGIONALI",VIOLET);
        metric(c,"Pesi fisici",num(total));
        metric(c,"Salvataggio",bLocal?"Presente":"Ancora assente • valori iniziali zero");
        metric(c,"Conseguenze allenanti",num(muscle.localTrainedOutcomes()));
        note(c,"Ogni gruppo di 5 pesi appartiene a uno dei 64 attuatori: quattro comandi per ciascuno dei 16 settori. Non sono arti predefiniti.");
        if(bLocal){
            metric(c,"Intervallo",(offset+1)+" – "+Math.min(offset+PAGE,total)+" di "+total);
            for(int i=offset;i<Math.min(total,offset+PAGE);i++){
                int site=i/5,sector=site/4,channel=site%4;
                metric(c,"#"+(i+1)+" • S"+(sector+1)+" C"+(channel+1),
                    String.format(Locale.ITALY,"%+.5f",muscle.localParameterAt(i)));
            }
        }
        tap(body,"‹  12 PRECEDENTI","Pagina precedente",BLUE,()->{
            localOffset=Math.max(0,localOffset-PAGE);render();
        });
        tap(body,"12 SUCCESSIVI  ›","Pagina successiva",TEAL,()->{
            localOffset=Math.min(total-1,localOffset+PAGE);render();
        });
    }
    private String bank(int index){
        int d=RecurrentCoreV22.dynamicsParameters(),h=RecurrentCoreV22.headParameters();
        return index<d?"Dinamica":index<d+h?"Azioni":
            index<d+2*h?"Energia":index<d+3*h?"Rischio":"Neurone aggiunto";
    }
    /** 3D projection of the ACTUAL restored recurrent neuron count. Each
     * dot is a neuron; grown connections affect live 2.2 choices, and are
     * tinted violet here only after independent heldout acceptance. 11fps. */
    private final class NeuralView extends View {
        private final Paint paint=new Paint(3);
        NeuralView(){super(BrainStatsActivity.this);
            setContentDescription("Rete ricorrente 2.2: "+core.totalNeurons()+
                " neuroni reali, "+core.addedNeurons()+" aggiunti dopo verifica");}
        @Override protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            float w=getWidth(),h=getHeight();
            float centerX=w*.5f,centerY=h*.48f;
            float unit=Math.min(w*.38f,h*.39f);
            float angle=(SystemClock.uptimeMillis()%18000)/18000f*6.283185f;
            final int count=core.totalNeurons();
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
            // Draw only REAL structural connections. The original 24
            // recurrent units each use a neighbouring hidden unit (fixed
            // .15 recurrence). Each grown neuron receives FOUR trainable
            // recurrent inputs from original neurons 0,3,11,19.
            int original=core.initialNeurons();
            for(int i=0;i<count;i++){
                for(int j=i+1;j<count;j++){
                    float link=0f;
                    if(j<original&&(j==i+1||(i==0&&j==original-1)))
                        link=.15f;
                    else if(i<original&&j>=original)
                        link=core.grownNeuronRecurrentLink(j-original,i);
                    if(Math.abs(link)<.001f||
                       Math.abs(depth[j]-depth[i])>1.65f)continue;
                    int alpha=(int)(34+185f*Math.min(1f,Math.abs(link)/1.4f));
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(dp(.55f+Math.min(1f,Math.abs(link))));
                    int tint=j>=original?VIOLET:TEAL;
                    paint.setColor(Color.argb(alpha,
                        Color.red(tint),Color.green(tint),Color.blue(tint)));
                    canvas.drawLine(xx[i],yy[i],xx[j],yy[j],paint);
                }
            }
            for(int i=0;i<count;i++){
                float bright=(depth[i]+1)*.5f;
                int alpha=(int)(110+145*bright);
                // Exactly the base 24 and accepted additional plastic nodes.
                int tint=i>=core.initialNeurons()?VIOLET:TEAL;
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(22,Color.red(tint),Color.green(tint),Color.blue(tint)));
                canvas.drawCircle(xx[i],yy[i],dp(5+bright*3),paint);
                paint.setColor(Color.argb(alpha,Color.red(tint),Color.green(tint),Color.blue(tint)));
                canvas.drawCircle(xx[i],yy[i],dp(1.5f+bright*1.8f),paint);
            }
            // The active overlay shows total/added neuron count without
            // inventing a neuron at the visual center.
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xffe4fff8);
            paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            paint.setTextSize(dp(11));
            String countLabel=count+" NEURONI";
            canvas.drawText(countLabel,centerX-paint.measureText(countLabel)*.5f,
                centerY+dp(5),paint);
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