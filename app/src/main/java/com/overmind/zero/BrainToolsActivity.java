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
        paragraph(evidence,"Ogni quinta esperienza, a partire da questo aggiornamento, viene misurata e NON usata per correggere i pesi. Il riferimento suppone che i sensori restino invariati. È un test di previsione, non la prova che sappia risolvere la stanza.");
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
        paragraph(physical,"I contatori partono dalla 0.9.4: non sono ricostruibili dai tentativi precedenti. Una zona è una cella di 75 unità, conteggiata una volta per vita. Le varianti sono confrontate su finestre successive, NON nelle stesse condizioni iniziali: dati indicativi, non una prova causale.");
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
        line(memory,"Effetti positivi nuovamente osservati",
             String.valueOf(episodes.repeatEffects),CYAN);
        line(memory,"Scelte diverse da un ricordo dannoso",
             String.valueOf(episodes.avoided),AMBER);
        line(memory,"Lunghezza ultimo ricordo",
             episodes.lastTraceLength+" azioni",WHITE);
        line(memory,"Ultimo effetto fisico ricordato",
             number(episodes.lastEventEffect),AMBER);
        paragraph(memory,"Dalla 0.9.6 ZERO ricorda sequenze vissute di lunghezza variabile (fino a 36 azioni), effetti benefici e dannosi osservati e rafforza i ricordi ripetutamente confermati. Un effetto energetico deriva dal CONTATTO, non dalla fame naturale. Il ricordo positivo può orientare una scelta, quello dannoso può penalizzare l'azione già osservata; nessun colore è programmato come buono o cattivo. I vecchi ricordi 0.9.5 migrano senza azzerare gli altri cervelli. 'Evitati' indica scelte differenti in contesti simili, non la prova di aver compreso il pericolo.");
        LinearLayout reset=panel(body);
        reset.addView(label("AZZERA APPRENDIMENTO",17,AMBER,true));
        paragraph(reset,"Ogni voce ritorna allo stato vergine SOLO per quel sistema. Altre memorie e reti rimangono. L'azzeramento ricarica il corpo nella mappa per applicare i nuovi parametri in sicurezza.");
        resetButton(reset,"MODELLO DEL MONDO", "11.664 pesi, errori, grafici, esperienze e decisioni.",ResetDomains.WORLD,false);
        resetButton(reset,"SCELTA DEGLI STIMOLI","Pesi, nuovi neuroni, decisioni e tentativi di crescita.",ResetDomains.CHOICE,false);
        resetButton(reset,"CONTROLLO DEL CORPO","62 pesi motori e i relativi tentativi e miglioramenti.",ResetDomains.MOTOR,false);
        resetButton(reset,"MANIPOLAZIONE","18 pesi, urti e contatori di dischi spostati.",ResetDomains.MANIPULATION,false);
        resetButton(reset,"MEMBRANA E PRESA","82 pesi, prove fisiche e miglioramenti della membrana.",ResetDomains.MEMBRANE,false);
        resetButton(reset,"MEMORIA DEGLI STIMOLI","Associazioni storiche separate dai cervelli.",ResetDomains.EXPLORATION,false);
        resetButton(reset,"STANZA E SOPRAVVIVENZA","Successi, energie, prese e cronologia della scarsità.",ResetDomains.ROOM,false);
        resetButton(reset,"MEMORIA EPISODICA","Ricordi vissuti, sequenze di azioni e tentativi di richiamo.",ResetDomains.EPISODES,false);
        resetButton(reset,"AZZERA TUTTO","CANCELLA tutti i cervelli, statistiche, memoria ed esperienze.",ResetDomains.ALL,true);
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
