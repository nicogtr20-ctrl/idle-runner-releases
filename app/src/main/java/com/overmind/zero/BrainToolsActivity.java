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
    void showPage(){
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        LinearLayout body=vertical();pad(body,17,24);scroll.addView(body);
        TextView title=label("CERVELLO / ANALISI",25,WHITE,true);body.addView(title);
        paragraph(body,"Monitoraggio reale • apprendimento automatico sempre attivo");
        LinearLayout sharePanel=panel(body);
        sharePanel.addView(label("CONDIVIDI DATI CON GPT  ↗",17,CYAN,true));
        paragraph(sharePanel,"Carica i modelli appresi, le statistiche delle vite "+
            "e il laboratorio del SOLO ZERO attuale sul repository GitHub "+
            "privato. Nessuna modifica al cervello e nessun upload automatico.");
        sharePanel.setOnClickListener(v->
            startActivity(new Intent(this,TelemetryActivity.class)));
        LinearLayout openLab=panel(body);
        openLab.addView(label("LABORATORIO COMPORTAMENTALE  ›",17,CYAN,true));
        paragraph(openLab,"Confronto fisico A/B: controllo precedente e cervello ricorrente 2.2, su copie dello stesso ZERO negli stessi ambienti. Non modifica il personaggio.");
        openLab.setOnClickListener(v->
            startActivity(new Intent(this,BehavioralLabActivity.class)));
        LinearLayout extendedLab=panel(body);
        extendedLab.addView(label("CONFRONTO 2.2 ESTESO / CASSE  ›",17,CYAN,true));
        paragraph(extendedLab,"24 prove isolate fino a 170 secondi simulati: energia, danni, sopravvivenza e prese reali. Lavora sul telefono e conserva soltanto il report.");
        extendedLab.setOnClickListener(v->startActivity(new Intent(this,CoreLabActivityV22.class)));
        LinearLayout linkStats=panel(body);
        linkStats.addView(label("STATISTICHE DELLE VITE  ›",17,CYAN,true));
        paragraph(linkStats,"Durata simulata della vita in corso, confronto con le ultime 10 vite completate e grafici delle ultime 50. A x5/x20 anche il tempo di gioco accelera.");
        linkStats.setOnClickListener(v->startActivity(new Intent(this,StatisticsActivity.class)));

        LearningDiagnostics d=new LearningDiagnostics();
        boolean loaded=d.restore(prefs.getString("sensorimotor_diagnostics_v091",""));
        SensorimotorMind model=new SensorimotorMind();
        model.restore(prefs.getString("sensorimotor_model_v09",""));
        LinearLayout evidence=panel(body);
        evidence.addView(label("MODELLO DEL MONDO",16,CYAN,true));
        line(evidence,"Esperienze accumulate",String.valueOf(model.experiences),WHITE);
        line(evidence,"Campioni valutati, NON addestrati",String.valueOf(d.heldoutTransitions),AMBER);
        line(evidence,"Errore medio fuori addestramento",d.heldoutTransitions>0?number(d.modelError):"—",WHITE);
        line(evidence,"Riferimento: nessun cambiamento",d.heldoutTransitions>0?number(d.baselineError):"—",MUTED);
        if(d.heldoutTransitions>0){
            String result=d.modelError<d.baselineError?
                "Il modello prevede meglio del riferimento sui campioni esclusi.":
                "Il modello non supera ancora il riferimento sui campioni esclusi.";
            paragraph(evidence,result);
        }
        paragraph(evidence,"Ogni quinta esperienza viene valutata fuori addestramento. Dalla 0.10.2 ZERO usa previsioni di sequenze fino a 3 azioni SOLO quando l'errore su campioni esclusi supera quello di un modello che prevede nessun cambiamento.");
        LearnedSequencePlanner plans=new LearnedSequencePlanner();
        plans.restore(prefs.getString("learned_planner_stats_v102",""));
        line(evidence,"Valutazioni di sequenze previste",String.valueOf(plans.attempts),WHITE);
        line(evidence,"Previsioni attendibili utilizzate",String.valueOf(plans.enabled),CYAN);
        line(evidence,"Previsioni rifiutate per incertezza",String.valueOf(plans.abstained),AMBER);
        line(evidence,"Scelte influenzate da previsioni apprese",String.valueOf(plans.changed),WHITE);
        paragraph(evidence,"Le previsioni sono ipotesi del modello sui propri sensori, non eventi fisici e non prove di causa-effetto. La pianificazione non impone direzioni, obiettivi o la manovra per aprire il cancello. Un modello poco affidabile viene ignorato.");
        MortalityLearning mortality=new MortalityLearning();
        mortality.restore(prefs.getString(MortalityLearning.KEY,""));
        LinearLayout endOfLife=panel(body);
        endOfLife.addView(label("ESPERIENZA DELLA MORTE / PREVISIONE",16,CYAN,true));
        paragraph(endOfLife,"ZERO non riceve la spiegazione preimpostata che la morte dipende dall'energia. "+
            "Osserva invece il proprio livello di energia e le variazioni che precedono la fine "+
            "reale di una vita; le conseguenze delle azioni e dei contatti restano separate.");
        line(endOfLife,"Morti osservate",String.valueOf(mortality.deaths),WHITE);
        line(endOfLife,"Vite interamente escluse dall'addestramento",
            String.valueOf(mortality.heldoutLives),AMBER);
        line(endOfLife,"Esempi terminali usati per imparare",
            String.valueOf(mortality.trainedTerminal),WHITE);
        line(endOfLife,"Esempi di sopravvivenza usati per imparare",
            String.valueOf(mortality.trainedSurvival),WHITE);
        line(endOfLife,"Esempi verificati senza allenamento (morte / vita)",
            mortality.heldoutDeaths+" / "+mortality.heldoutSurvival,WHITE);
        line(endOfLife,"Rischio stimato con energia al 10% / 80%",
            String.format(Locale.US,"%.1f%% / %.1f%%",
                100*mortality.riskAt(.1f,-.03f),100*mortality.riskAt(.8f,0)),WHITE);
        line(endOfLife,"Affidabilita' predittiva verificata",
            String.format(Locale.US,"%.1f%%",100*mortality.confidence()),CYAN);
        line(endOfLife,"Valutazioni di consigli energetici",
            String.valueOf(mortality.adviceEvents),WHITE);
        line(endOfLife,"Morti con / senza danno recente osservato",
            mortality.deathsWithRecentHarm+" / "+mortality.deathsWithoutRecentHarm,AMBER);
        line(endOfLife,"Ultima morte: consumo / danno / recupero recenti",
            String.format(Locale.US,"%.1f / %.1f / %.1f",
                mortality.lastRecentDrain,mortality.lastRecentHarm,
                mortality.lastRecentGain),WHITE);
        paragraph(endOfLife,"Le ultime misure sono fatti fisici, NON una diagnosi causale definitiva. "+
            "ZERO non penalizza automaticamente un cancello, una cassa o un'azione "+
            "eseguita mentre ha subito un danno. Solo dopo molte vite tenute "+
            "FUORI dall'addestramento e previsioni piu' accurate, "+
            "il rischio appreso puo' dare un contributo LIMITATO ai valori "+
            "energetici gia' attribuiti a fonti osservate. Nessuna azione e' obbligata.");
        LinearLayout graph=panel(body);
        graph.addView(label("EVOLUZIONE ERRORE DI PREVISIONE",15,CYAN,true));
        if(d.points()>0){
            Graph plot=new Graph(this,d);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(205));
            lp.topMargin=dp(12);graph.addView(plot,lp);
            paragraph(graph,"Verde: previsioni del cervello    •    Oro: riferimento senza cambiamenti. Ogni punto riassume 5 esperienze escluse dall'addestramento.");
        }else paragraph(graph,"Il grafico comparirà dopo almeno 25 nuove esperienze dalla v0.9.1.");
        LinearLayout decisions=panel(body);
        decisions.addView(label("AZIONI OSSERVATE",16,CYAN,true));
        line(decisions,"Decisioni scelte dal modello",String.valueOf(d.modelActions),WHITE);
        line(decisions,"Decisioni esplorative casuali",String.valueOf(d.exploreActions),WHITE);
        line(decisions,"Ricompensa media modello",average(d.modelRewardSum,d.modelActions),AMBER);
        line(decisions,"Ricompensa media esplorazione",average(d.exploreRewardSum,d.exploreActions),AMBER);
        paragraph(decisions,"Confronto osservazionale: le situazioni affrontate dalle due scelte possono essere diverse. NON è un esperimento controllato a parità di mondo, né prova di intelligenza causale.");
        LinearLayout physical=panel(body);
        physical.addView(label("ADATTAMENTO CORPOREO REALE",16,CYAN,true));
        line(physical,"Zone esplorate (somma delle vite)",
             String.valueOf(prefs.getInt("explored_cells_v094",0)),WHITE);
        line(physical,"Record distanza netta",
             ((int)prefs.getFloat("record_net_v094",0))+" U",CYAN);
        line(physical,"Energia consumata misurata",
             ((int)prefs.getFloat("energy_spent_v094",0))+" U",WHITE);
        line(physical,"MOTORE · varianti conservate / scartate",
             prefs.getInt("online_accepted_motor_v094",0)+" / "+
             prefs.getInt("online_rejected_motor_v094",0),WHITE);
        line(physical,"MANIPOLAZIONE · conservate / scartate",
             prefs.getInt("online_accepted_hand_v094",0)+" / "+
             prefs.getInt("online_rejected_hand_v094",0),WHITE);
        line(physical,"MEMBRANA · conservate / scartate",
             prefs.getInt("online_accepted_membrane_v094",0)+" / "+
             prefs.getInt("online_rejected_membrane_v094",0),WHITE);
        line(physical,"Recuperi da immobilità (0.9.7)",
             String.valueOf(prefs.getInt("motor_recoveries_v097",0)),CYAN);
        line(physical,"Decisioni temporaneamente libere dai ricordi",
             String.valueOf(prefs.getInt("motor_free_choices_v097",0)),WHITE);
        MotorSkillBank skills=new MotorSkillBank();
        skills.restore(prefs.getString("motor_skills_v098",""));
        line(physical,"Tecniche motorie verificate e conservate",skills.count()+" / "+MotorSkillBank.MAX,CYAN);
        line(physical,"Record traslazione in una prova motoria",
             number(skills.largestVerifiedNet)+" U",WHITE);
        line(physical,"Tecniche precedenti recuperate",String.valueOf(skills.rollbacks),AMBER);
        line(physical,"Contatti energetici accreditati al cervello",
             String.valueOf(prefs.getInt("real_energy_credits_v098",0)),CYAN);
        line(physical,"Contatti dannosi accreditati al cervello",
             String.valueOf(prefs.getInt("real_damage_credits_v098",0)),AMBER);
        line(physical,"Azioni realmente eseguite con esito accreditato",
             String.valueOf(prefs.getInt("real_action_credits_v098",0)),WHITE);
        SensorimotorMind causal=new SensorimotorMind();
        causal.restoreOutcomeModel(prefs.getString("outcome_credit_v101",""));
        line(physical,"Spostamenti esterni osservati (separati)",String.valueOf(causal.physicalOutcomeEvents()),CYAN);
        line(physical,"Contatti abbinati a segnale visivo",String.valueOf(causal.sourceMatchedEvents()),WHITE);
        line(physical,"Contatti senza segnale visivo attribuibile",String.valueOf(causal.ambiguousOutcomeEvents()),AMBER);
        paragraph(physical,"Dalla 0.10.1, spostamento fisico e guadagno/perdita energetica hanno due valori appresi indipendenti. Il danno da una fonte visiva non cancella il valore fisico di aver mosso il cancello. Eventi con fonte invisibile rimangono incerti: non vengono attribuiti a caso ad un ostacolo. Questi sono indizi associativi, non prova di ragionamento causale.");
                paragraph(physical,"Correzione 0.9.8: eliminato il bonus direzionale per le sfere. ZERO riceve soltanto le conseguenze energetiche dei contatti realmente avvenuti: le sue precedenti azioni effettivamente eseguite aggiornano i valori appresi della rete neurale. Le 26 percezioni (inclusi gli stimoli visivi) rimangono input del cervello, ma nessun colore induce automaticamente ad andare verso una certa direzione. Il comportamento di ricerca delle risorse deve emergere dai valori appresi, non è garantito. L'archivio delle tecniche motorie, il recupero antistallo e i ricordi preesistenti sono conservati.");
        GripSkillBank gripHistory=new GripSkillBank();
        gripHistory.restore(prefs.getString("grip_skills_v099",""));
        line(physical,"Tecniche di presa fisica archiviate",
            gripHistory.count()+" / "+GripSkillBank.MAX,CYAN);
        line(physical,"Configurazioni di presa recuperate",
            String.valueOf(gripHistory.restored),AMBER);
        paragraph(physical,"0.9.9: le prese salvate sono soltanto quelle realmente mantenute al contatto, non TOCCHI o tentativi. La memoria episodica non sovrascrive più i pesi della membrana nel momento della presa. Il recupero può avvenire solo se ZERO sceglie l'adesione ma non riesce a mantenerla dopo molte prove al cancello. Se una tecnica efficace è stata persa prima della 0.9.9 non può essere ricostruita dal nulla.");
        EpisodicMemory episodes=new EpisodicMemory();
        episodes.restore(prefs.getString("episodic_memory_v095",""));
        LinearLayout memory=panel(body);
        memory.addView(label("MEMORIA DELLE ESPERIENZE",16,CYAN,true));
        line(memory,"Esperienze significative conservate",
             String.valueOf(episodes.stored())+" / "+EpisodicMemory.MAX_EPISODES,WHITE);
        line(memory,"Esperienze favorevoli",
             String.valueOf(episodes.positiveCount()),CYAN);
        line(memory,"Esperienze dannose",
             String.valueOf(episodes.negativeCount()),AMBER);
        line(memory,"Sequenza più lunga",
             episodes.longestTrace()+" / "+EpisodicMemory.DEPTH+" azioni",WHITE);
        line(memory,"Ricordi consolidati da conferme",
             String.valueOf(episodes.consolidated),WHITE);
        line(memory,"Conferme del ricordo più stabile",
             String.valueOf(episodes.strongestConfirmations()),CYAN);
        line(memory,"Esperienze registrate nel tempo",
             String.valueOf(episodes.captured),WHITE);
        line(memory,"Situazioni riconosciute",
             String.valueOf(episodes.matched),WHITE);
        line(memory,"Azioni realmente richiamate",
             String.valueOf(episodes.replayed),CYAN);
        line(memory,"Riprove fisiche con membrana in contatto",
             String.valueOf(prefs.getInt("physical_replays_v097",0)),WHITE);
        line(memory,"Effetti positivi nuovamente osservati",
             String.valueOf(episodes.repeatEffects),CYAN);
        line(memory,"Scelte diverse da un ricordo dannoso",
             String.valueOf(episodes.avoided),AMBER);
        line(memory,"Lunghezza ultimo ricordo",
             episodes.lastTraceLength+" azioni",WHITE);
        line(memory,"Ultimo effetto fisico ricordato",
             number(episodes.lastEventEffect),AMBER);
        paragraph(memory,"Dalla 0.9.9 la memoria episodica rimane memoria di situazioni, azioni e conseguenze. Non può più sostituire motore, mano o membrana con vecchi pesi, neanche al cancello: i ricordi restano disponibili per influenzare le decisioni autonomamente scelte, non i controller fisici. La banca delle prese conserva separatamente SOLO configurazioni verificate da un'adesione reale. I ricordi em1/em2 migrano automaticamente al formato em3 con provenienza dei nuovi eventi, senza perdere i vecchi ricordi; il modello del mondo rimane intatto.");

        TemporalSkillBank chunks=new TemporalSkillBank();
        chunks.restore(prefs.getString("temporal_skills_v107",""));
        LinearLayout skillMemory=panel(body);
        skillMemory.addView(label("ABILITA TEMPORALI AUTONOME",16,CYAN,true));
        line(skillMemory,"Sequenze candidate conservate",
             chunks.stored()+" / "+TemporalSkillBank.MAX_SKILLS,WHITE);
        line(skillMemory,"Sequenze consolidate da ripetizioni",
             String.valueOf(chunks.consolidated()),CYAN);
        line(skillMemory,"Sequenza più lunga",
             chunks.longest()+" / "+TemporalSkillBank.MAX_LEN+" azioni",WHITE);
        line(skillMemory,"Conferme massime di una tecnica",
             String.valueOf(chunks.strongestConfirmations()),CYAN);
        line(skillMemory,"Suggerimenti di skill",
             String.valueOf(chunks.suggestions),WHITE);
        line(skillMemory,"Esecuzioni iniziate",
             String.valueOf(chunks.activations),WHITE);
        line(skillMemory,"Esecuzioni con nuovo effetto positivo",
             String.valueOf(chunks.completions),CYAN);
        line(skillMemory,"Replay falliti",
             String.valueOf(chunks.failedReplays),AMBER);
        line(skillMemory,"Interruzioni per scelta/stato diverso",
             String.valueOf(chunks.interruptions),AMBER);
        paragraph(skillMemory,"Una sequenza nasce soltanto dalle azioni realmente scelte da ZERO prima di un effetto positivo reale. Una singola fortuna resta candidata: servono almeno due ricorrenze compatibili prima che possa influenzare una scelta. Durante il replay, se il contesto temporale diverge la skill viene interrotta; non esiste una procedura programmata per porte o oggetti.");

        BodyProprioception bodyMap=new BodyProprioception();
        bodyMap.restore(prefs.getString(BodyProprioception.KEY,""));
        LinearLayout proprio=panel(body);
        proprio.addView(label("PERCEZIONE DEL PROPRIO CORPO",16,CYAN,true));
        line(proprio,"Punti fisici della membrana",String.valueOf(Morphology.POINTS),WHITE);
        line(proprio,"Settori corporei percepiti",String.valueOf(BodyProprioception.SECTORS),CYAN);
        line(proprio,"Segnali tattili/propriocettivi",String.valueOf(BodyProprioception.FEATURES),WHITE);
        line(proprio,"Transizioni corpo-azione vissute",String.valueOf(bodyMap.transitions),WHITE);
        line(proprio,"Valutazioni fuori addestramento",String.valueOf(bodyMap.validations),WHITE);
        line(proprio,"Errore previsione tattile",bodyMap.validations>0?
            number(bodyMap.predictionError):"—",WHITE);
        line(proprio,"Errore senza prevedere cambiamento",bodyMap.validations>0?
            number(bodyMap.unchangedError):"—",MUTED);
        line(proprio,"Affidabilita' appresa per le scelte",
            String.format(Locale.US,"%.1f%%",100*bodyMap.confidence()),CYAN);
        line(proprio,"Scelte realmente influenzate",String.valueOf(bodyMap.choiceInfluences),WHITE);
        line(proprio,"Situazioni con contatto reale",String.valueOf(bodyMap.contactObservations),WHITE);
        float[] patch=bodyMap.sensed();
        String[] dir={"E","SE","S","SO","O","NO","N","NE"};
        for(int k=0;k<BodyProprioception.SECTORS;k++){
            String state=String.format(Locale.US,
                "T %.2f P %.2f E %.2f V %+.2f | Rmin %.0fU Rmax %.0fU",
                patch[k],patch[8+k],patch[16+k],patch[24+k],
                patch[40+k]*155f,patch[32+k]*155f);
            line(proprio,"Settore "+dir[k],state,WHITE);
        }
        paragraph(proprio,"ZERO e' una MASSA DISTRIBUITA, priva di nucleo fisico. "+
            "Il centro e' solo il baricentro osservato delle dieci masse interne, "+
            "non puo' collidere o raccogliere energia. Sei segnali per ognuno degli "+
            "otto settori: contatto, presa, estensione, velocita' radiale, "+
            "raggio piu' lontano e piu' vicino della membrana. Questi 48 "+
            "segnali derivano soltanto da materia reale e vengono imparati "+
            "attraverso esperienze vissute, senza nomi di porte o oggetti. "+
            "Le 26 percezioni e 16 azioni principali conservano i parametri "+
            "originari. La mappa corporea geometrica 0120 ricomincia vergine "+
            "perche' il vecchio schema dei sensori aveva 32 dimensioni.");

        SpatialPerception space=new SpatialPerception();
        space.restore(prefs.getString(SpatialPerception.KEY,""));
        LinearLayout spatial=panel(body);
        spatial.addView(label("PERCEZIONE DELLO SPAZIO",16,CYAN,true));
        paragraph(spatial,"Mappa costruita SOLO attraverso 16 raggi sensoriali e movimenti realmente vissuti. Scuro = sconosciuto; azzurro = osservato libero; arancio = ostacolo osservato. La croce indica ZERO.");
        SpatialMapView view=new SpatialMapView(this,space);
        LinearLayout.LayoutParams lpMap=new LinearLayout.LayoutParams(-1,dp(245));
        lpMap.topMargin=dp(11);
        spatial.addView(view,lpMap);
        line(spatial,"Direzioni rilevate",SpatialPerception.RAYS+" raggi",WHITE);
        line(spatial,"Portata massima",String.format(Locale.US,"%.0f U",SpatialPerception.RANGE),WHITE);
        line(spatial,"Celle osservate / memoria",
            space.mappedCells()+" / "+SpatialPerception.MAX_CELLS,CYAN);
        line(spatial,"Celle libere osservate",String.valueOf(space.freeCells()),WHITE);
        line(spatial,"Ostacoli osservati",String.valueOf(space.blockedCells()),AMBER);
        line(spatial,"Scansioni realmente vissute",String.valueOf(space.sweeps),WHITE);
        line(spatial,"Azioni e conseguenze osservate",String.valueOf(space.transitions),WHITE);
        line(spatial,"Prove fuori addestramento",String.valueOf(space.validations),WHITE);
        line(spatial,"Errore di previsione spaziale",
            space.validations>0?number(space.error):"—",WHITE);
        line(spatial,"Errore senza previsione",
            space.validations>0?number(space.baseline):"—",MUTED);
        line(spatial,"Affidabilita' appresa",
            String.format(Locale.US,"%.1f%%",100*space.confidence()),CYAN);
        line(spatial,"Scelte influenzate",
            String.valueOf(space.choiceInfluences),WHITE);
        float[] ranges=space.ranges();
        for(int pair=0;pair<8;pair++){
            int r=pair*2;
            String readings=String.format(Locale.US,"%.0f U / %.0f U",
                space.distance(r),
                space.distance(r+1));
            line(spatial,(r*360/SpatialPerception.RAYS)+"° / "+
                ((r+1)*360/SpatialPerception.RAYS)+"°",readings,WHITE);
        }
        paragraph(spatial,"Nessuna zona nascosta viene rivelata. Le celle dietro ostacoli restano sconosciute. Il cervello NON riceve una mappa completa del mondo né istruzioni di percorso: i suggerimenti spaziali rimangono a zero finche' le previsioni non superano almeno 30 verifiche fuori addestramento. La mappa locale si rinnova ad ogni vita; i pesi appresi restano salvati nel singolo ZERO.");

        CausalExperiments causes=new CausalExperiments();
        causes.restore(prefs.getString(CausalExperiments.KEY,""));
        LinearLayout experiment=panel(body);
        experiment.addView(label("ESPERIMENTI / CAUSA ED EFFETTO",16,CYAN,true));
        paragraph(experiment,"ZERO confronta come si sposta il proprio corpo con i cambiamenti realmente osservati nelle stesse celle della mappa. Nessuna porta o soluzione pre-programmata.");
        line(experiment,"Esperimenti osservati",String.valueOf(causes.experiments),WHITE);
        line(experiment,"Spostamenti del corpo senza variazioni degli ostacoli",
            String.valueOf(causes.motionWithoutChange),WHITE);
        line(experiment,"Variazioni spaziali con corpo quasi fermo",
            String.valueOf(causes.changeWithoutMotion),CYAN);
        line(experiment,"Celle cambiate (occupato/libero)",
            String.valueOf(causes.worldChanges),WHITE);
        line(experiment,"Nuove celle libere osservate",
            String.valueOf(causes.newlyVisible),WHITE);
        line(experiment,"Aperture possibili osservate",
            String.valueOf(causes.tentativeOpenings),AMBER);
        line(experiment,"Passaggi attraversati davvero",
            String.valueOf(causes.confirmedPassages),CYAN);
        line(experiment,"Alternative provate casualmente",
            String.valueOf(causes.testedAlternatives()),WHITE);
        line(experiment,"Ipotesi confrontabili tra azioni",
            String.valueOf(causes.confirmedHypotheses()),WHITE);
        line(experiment,"Previsioni verificate fuori addestramento",
            String.valueOf(causes.validations),WHITE);
        line(experiment,"Errore modello per azione",
            causes.validations>0?number(causes.modelError):"—",WHITE);
        line(experiment,"Errore riferimento stesso contesto",
            causes.validations>0?number(causes.referenceError):"—",MUTED);
        line(experiment,"Fiducia sperimentale",
            String.format(Locale.US,"%.1f%%",100f*causes.confidence()),CYAN);
        line(experiment,"Scelte influenzate da prove",
            String.valueOf(causes.choiceInfluences),WHITE);
        line(experiment,"Ultimo spostamento del proprio corpo",
            String.format(Locale.US,"%.1f U",causes.lastBodyMove),WHITE);
        line(experiment,"Ultimi cambiamenti dell'ambiente",
            String.format(Locale.US,"%.0f celle",causes.lastEnvironmentalChange),WHITE);
        paragraph(experiment,"Un ostacolo che sembra scomparso e' soltanto un'ipotesi di apertura. Si conta un passaggio solo quando il corpo attraversa effettivamente quella cella in seguito. Prima di cambiare le proprie decisioni ZERO deve superare almeno 30 prove non usate per apprendere, prevedere meglio del riferimento e aver provato diverse azioni in situazioni simili. Associazioni e verifiche non costituiscono una prova di causalita' certa.");

        ObjectExperienceV20 objects20=new ObjectExperienceV20();
        objects20.restore(prefs.getString(ObjectExperienceV20.KEY,""));
        LinearLayout objectPanel=panel(body);
        objectPanel.addView(label("ZERO 2.0 / OGGETTI ED ESPERIENZE",16,CYAN,true));
        paragraph(objectPanel,"Categorie sensoriali ipotetiche costruite da raggi e firme energetiche: nessun ID di cassa, contenuto nascosto o regola speciale nel cervello. Gli eventi energetici casuali non vengono automaticamente attribuiti all'oggetto.");
        line(objectPanel,"Categorie sensoriali apprese",String.valueOf(objects20.categories()),WHITE);
        line(objectPanel,"Osservazioni reali",String.valueOf(objects20.observations()),WHITE);
        line(objectPanel,"Spostamenti esterni registrati",String.valueOf(objects20.physicalSuccesses()),CYAN);
        line(objectPanel,"Possibili collegamenti con energia (non prove)",String.valueOf(objects20.possibleCausalLinks()),AMBER);
        line(objectPanel,"Fiducia predittiva verificata",String.format(Locale.US,"%.1f%%",objects20.confidence()*100),CYAN);
        paragraph(objectPanel,"Le ipotesi probabilistiche diventano suggerimenti lievi SOLO dopo confronti su episodi non usati per l'apprendimento. Un'energia toccata per caso non annulla una presa riuscita. Le capacità di presa vere rimangono archiviate separatamente.");
        
        RecurrentCoreV22 active22=new RecurrentCoreV22();
        boolean loaded22=active22.restore(prefs.getString(RecurrentCoreV22.KEY,""));
        LinearLayout activePanel=panel(body);
        activePanel.addView(label("ZERO 2.2 / CONTROLLO ATTIVO",16,CYAN,true));
        paragraph(activePanel,"Memoria ricorrente, corpo e sensori condivisi. Valuta le azioni prima di eseguirle. Le previsioni contribuiscono solo con evidenza fuori addestramento; il replay usa esperienze realmente vissute. La crescita delle capacita va verificata nelle partite.");
        paragraph(activePanel,loaded22?active22.summary():"Nucleo nuovo: in attesa del primo salvataggio.");
        // ZERO21 research readout: only genuine saved observer measurements.
        UnifiedCognitionV21 unified21=new UnifiedCognitionV21();
        unified21.restore(prefs.getString(UnifiedCognitionV21.KEY,""));
        ObjectContinuityV21 continuity21=new ObjectContinuityV21();
        continuity21.restore(prefs.getString(ObjectContinuityV21.KEY,""));
        CompetenceMemoryV21 competence21=new CompetenceMemoryV21();
        competence21.restore(prefs.getString(CompetenceMemoryV21.KEY,""));
        UnifiedArbiterV21 arbiter21=new UnifiedArbiterV21();
        arbiter21.restore(prefs.getString(UnifiedArbiterV21.KEY,""));
        LinearLayout unifiedPanel=panel(body);
        unifiedPanel.addView(label("ZERO 2.1 / CERVELLO CONDIVISO",16,CYAN,true));
        paragraph(unifiedPanel,"Sperimentale in sola osservazione. Collega 26 sensi, azioni autonome, vere conseguenze e ipotesi di continuità degli ostacoli. Le associazioni NON sono cause dimostrate e le proposte del pianificatore non comandano il corpo.");
        line(unifiedPanel,"Intervalli di esperienza reali",String.valueOf(unified21.observations()),WHITE);
        line(unifiedPanel,"Previsioni fuori addestramento",String.valueOf(unified21.holdouts()),WHITE);
        line(unifiedPanel,"Effetti esterni osservati",String.valueOf(unified21.physicalEvents()),WHITE);
        line(unifiedPanel,"Contatti energetici vissuti",String.valueOf(unified21.energyContacts()),CYAN);
        line(unifiedPanel,"Fiducia del modello condiviso",String.format(Locale.US,"%.1f%%",unified21.confidence()*100f),CYAN);
        line(unifiedPanel,"Confronti multivista degli ostacoli",String.valueOf(continuity21.trackedViews()),WHITE);
        line(unifiedPanel,"Vista ambigua / non attribuibile",String.valueOf(continuity21.ambiguousViews()),AMBER);
        line(unifiedPanel,"Possibili effetti su ostacoli",String.valueOf(continuity21.candidateEffects()),WHITE);
        line(unifiedPanel,"Energia dopo uno spostamento (solo ipotesi)",String.valueOf(continuity21.delayedEnergyAssociations()),AMBER);
        line(unifiedPanel,"Presa fisica confermata / campioni",String.valueOf(competence21.actualWitnesses()),CYAN);
        line(unifiedPanel,"Tecniche HAND + MEMBRANA archiviate",String.valueOf(competence21.size()),WHITE);
        line(unifiedPanel,"Proposte future ammesse (non usate)",String.valueOf(arbiter21.eligible()),WHITE);
        line(unifiedPanel,"Pianificazioni rifiutate per incertezza",String.valueOf(arbiter21.abstentions()),AMBER);
        paragraph(unifiedPanel,"Le identità dei singoli ostacoli non vengono salvate come verità. Una tecnica di presa richiede una vera presa opposta e spostamento fisico; archiviarla non significa eseguirla. Zero 2.1 non ha ancora prova di maggior sopravvivenza e non deve ricevere potere motorio senza test A/B.");

        InstrumentalNavigation navigation=new InstrumentalNavigation();
        navigation.restore(prefs.getString(InstrumentalNavigation.KEY,""));
        LinearLayout instrument=panel(body);
        instrument.addView(label("MOVIMENTO COME MEZZO / ENERGIA",16,CYAN,true));
        paragraph(instrument,"ZERO impara in modo indipendente (1) quale frequenza della luce ha preceduto energia positiva o negativa al contatto e (2) quale spostamento reale del corpo consegue a ciascuna azione. Un movimento non riceve un premio solo perche' cammina. Le frequenze non hanno nomi o valore preimpostato.");
        int[] contacts=navigation.spectralContacts();
        for(int i=0;i<contacts.length;i++)
            line(instrument,"Frequenza visiva "+(i+1)+" / contatti vissuti",
                 String.format(Locale.US,"%d  •  valore energia %+.2f",
                     contacts[i],navigation.signalValue(i)),
                 navigation.signalValue(i)>0?CYAN:
                     navigation.signalValue(i)<0?AMBER:WHITE);
        line(instrument,"Movimenti realmente verificati",
             navigation.verifiedActions()+" / 16",WHITE);
        line(instrument,"Tipi di movimenti osservati",
             String.valueOf(navigation.learnedActions()),WHITE);
        line(instrument,"Esperienze motorie registrate",
             String.valueOf(navigation.completed),WHITE);
        line(instrument,"Conseguenze energetiche positive",
             String.valueOf(navigation.positiveOutcomes),CYAN);
        line(instrument,"Conseguenze energetiche negative",
             String.valueOf(navigation.negativeOutcomes),AMBER);
        line(instrument,"Scelte con influenza della navigazione",
             String.valueOf(navigation.choicesInfluenced),CYAN);
        paragraph(instrument,"Se ZERO non ha ancora visto un effetto energetico o non ha verificato i propri movimenti, questa rete resta neutra. I valori diventano utili quando puo' scegliere azioni che, sulla base dell'esperienza, sembrano avvicinarlo a uno stimolo benefico o allontanarlo da uno dannoso. E' un modello sperimentale, non un comando di inseguimento.");

        GoalContinuity intentions=new GoalContinuity();
        intentions.restore(prefs.getString(GoalContinuity.KEY,""));
        LinearLayout intentPanel=panel(body);
        intentPanel.addView(label("CONTINUITA' DELLE INTENZIONI",16,CYAN,true));
        paragraph(intentPanel,"ZERO conserva brevemente una preferenza per uno stimolo noto soltanto dopo avere osservato che un movimento reale ha migliorato il proprio accesso a quello stimolo. Due regressioni o la perdita della vista interrompono la preferenza. Nessun premio aggiunto per camminare o girare.");
        line(intentPanel,"Primi progressi confermati",String.valueOf(intentions.confirmed),WHITE);
        line(intentPanel,"Progressi consecutivi verificati",String.valueOf(intentions.continued),CYAN);
        line(intentPanel,"Tentativi abbandonati",String.valueOf(intentions.abandoned),AMBER);
        line(intentPanel,"Azioni senza progresso",String.valueOf(intentions.failed),WHITE);
        line(intentPanel,"Scelte influenzate da continuita'",String.valueOf(intentions.advice),CYAN);
        GateBehaviorMetrics gate=new GateBehaviorMetrics();
        gate.restore(prefs.getString(GateBehaviorMetrics.KEY,""));
        LinearLayout gatePanel=panel(body);
        gatePanel.addView(label("DIAGNOSI FISICA DEL CANCELLO",16,CYAN,true));
        paragraph(gatePanel,"Solo misure reali del mondo, NON istruzioni fornite al cervello. Distingue il raggiungimento della porta dalle condizioni meccaniche necessarie per muoverla.");
        line(gatePanel,"Distanza minima dalla porta",String.format(Locale.US,"%.1f U",gate.closestDistance()),WHITE);
        line(gatePanel,"Vicino alla porta (frame fisici)",String.valueOf(gate.nearbyFrames),WHITE);
        line(gatePanel,"Nucleo contro la porta",String.valueOf(gate.coreTouchFrames),WHITE);
        line(gatePanel,"Membrana a contatto",String.valueOf(gate.skinTouchFrames),WHITE);
        line(gatePanel,"Adesione selezionata al contatto",String.valueOf(gate.driveFrames),WHITE);
        line(gatePanel,"Presa comandata dalla membrana",String.valueOf(gate.commandFrames),WHITE);
        line(gatePanel,"Presa realmente mantenuta",String.valueOf(gate.heldFrames),WHITE);
        line(gatePanel,"Presa sui lati opposti",String.valueOf(gate.opposedFrames),WHITE);
        line(gatePanel,"Massimo spostamento reale",String.format(Locale.US,"%.1f U",gate.maxTravel),CYAN);
        line(gatePanel,"Aperture / attraversamenti",gate.opened+" / "+gate.passages,CYAN);
        paragraph(gatePanel,"Ultimo stadio osservato: "+gate.obstacleStage()+". Le misure derivano dall'ultima copia salvata e non aggiornano la schermata durante il movimento.");

        LinearLayout reset=panel(body);
        reset.addView(label("AZZERA APPRENDIMENTO",17,AMBER,true));
        paragraph(reset,"Ogni voce ritorna allo stato vergine SOLO per quel sistema. Altre memorie e reti rimangono. L'azzeramento ricarica il corpo nella mappa per applicare i nuovi parametri in sicurezza.");
        resetButton(reset,"MODELLO DEL MONDO", "Predizione sensoriale, memoria temporale, errori, esperienze e decisioni.",ResetDomains.WORLD,false);
        resetButton(reset,"SCELTA DEGLI STIMOLI","Pesi, nuovi neuroni, decisioni e tentativi di crescita.",ResetDomains.CHOICE,false);
        resetButton(reset,"CONTROLLO DEL CORPO","62 pesi motori e i relativi tentativi e miglioramenti.",ResetDomains.MOTOR,false);
        resetButton(reset,"MANIPOLAZIONE","18 pesi, urti e contatori di dischi spostati.",ResetDomains.MANIPULATION,false);
        resetButton(reset,"MEMBRANA E PRESA","82 pesi, prove fisiche e miglioramenti della membrana.",ResetDomains.MEMBRANE,false);
        resetButton(reset,"MEMORIA DEGLI STIMOLI","Associazioni storiche separate dai cervelli.",ResetDomains.EXPLORATION,false);
        resetButton(reset,"STANZA E SOPRAVVIVENZA","Successi, energie, prese e cronologia della scarsità.",ResetDomains.ROOM,false);
        resetButton(reset,"MEMORIA EPISODICA","Ricordi vissuti, sequenze di azioni e tentativi di richiamo.",ResetDomains.EPISODES,false);
        resetButton(reset,"ABILITA TEMPORALI","Sequenze autonome consolidate e cronologia dei loro replay.",ResetDomains.TEMPORAL_SKILLS,false);
        resetButton(reset,"PERCEZIONE CORPOREA","Mappa tattile, previsioni e valori motori appresi; non cancella la membrana fisica.",ResetDomains.PROPRIOCEPTION,false);
        resetButton(reset,"PERCEZIONE SPAZIALE","Azzeramento SOLO dei raggi, della memoria locale degli ostacoli e dei pesi spaziali.",ResetDomains.SPATIAL,false);
        resetButton(reset,"ESPERIMENTI CAUSALI","Azzera SOLO esperimenti, alternative provate e ipotesi apprese, senza toccare i sensori o la memoria spaziale.",ResetDomains.CAUSAL,false);
        resetButton(reset,"NAVIGAZIONE E UTILITA","Azzera solo le associazioni tra energia visiva e moti realmente imparati, non gli altri cervelli.",ResetDomains.NAVIGATION,false);
        resetButton(reset,"CONTINUITA' DELLE INTENZIONI","Azzera solo i tentativi e le statistiche del mantenimento delle intenzioni.",ResetDomains.INTENTION,false);
        resetButton(reset,"DIAGNOSTICA CANCELLO","Azzera soltanto le misure di contatto e presa, non il comportamento fisico o la memoria.",ResetDomains.GATE_METRICS,false);
        resetButton(reset,"AZZERA TUTTO","CANCELLA tutti i cervelli, statistiche, memoria ed esperienze.",ResetDomains.ALL,true);
        LinearLayout saves=panel(body);
        saves.addView(label("LE TUE PARTITE",17,CYAN,true));
        paragraph(saves,"Salva il cervello attuale in uno slot con nome, carica un altro ZERO oppure crea un nuovo personaggio vergine. Gli slot salvati non vengono cancellati dai reset del cervello.");
        TextView openSaves=label("▣  SALVA / CARICA PARTITA  ›",16,CYAN,true);
        openSaves.setPadding(dp(3),dp(18),dp(3),dp(12));
        openSaves.setOnClickListener(v->{
            startActivity(new Intent(this,SaveSlotsActivity.class));finish();
        });
        saves.addView(openSaves);
        TextView back=label("‹ TORNA A ZERO",17,CYAN,true);
        back.setGravity(Gravity.CENTER);
        back.setPadding(0,dp(24),0,dp(20));
        back.setOnClickListener(v->finish());
        body.addView(back);
        setContentView(scroll);
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
