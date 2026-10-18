package com.overmind.zero;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;

/** Complete V3 navigation shell, one owner thread, one defensive provider, no legacy metrics. */
public final class Zero3DashboardActivityV354 extends Activity {
    private static final String[] PAGES={"Gioco","Panoramica","Cervello","Corpo","Abilità","Evoluzione","Percezione","Memoria e obiettivi","Analisi","Impostazioni"};
    private static final int BG=0xff070c17,CARD=0xff101e30,WHITE=0xffe8f7ff,MUTED=0xff9ab0c6,CYAN=0xff62e3e5;
    private static final ExecutorService SERIAL_OWNER=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"zero3-owner-v354");t.setPriority(Thread.MIN_PRIORITY);return t;});
    private final ExecutorService owner=SERIAL_OWNER;
    private Zero3AndroidSessionV325 session;private Zero3ProfileStoreV354 profiles;
    private volatile boolean running,destroyed;private volatile int page;
    private int slot=1,documentOperation;
    private TextView header,status,facts;private LinearLayout content;private BodyCanvas body;private Zero3AtlasViewV354 atlas;
    private volatile Zero3DashboardSnapshotV354 latest;
    private long lastPublish;private String checkpoint="Nessun salvataggio verificato in questa apertura";
    private interface Work{void run()throws Exception;}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    @Override public void onCreate(Bundle state){super.onCreate(state);
        slot=getSharedPreferences("zero3-ui-v354",0).getInt("slot",1);if(slot<1||slot>12)slot=1;
        LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(12),dp(14),dp(12),dp(8));root.setBackgroundColor(BG);setContentView(root);
        root.setOnApplyWindowInsetsListener((view,insets)->{
            int top,bottom;if(Build.VERSION.SDK_INT>=30){android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars());top=bars.top;bottom=bars.bottom;}
            else{top=insets.getSystemWindowInsetTop();bottom=insets.getSystemWindowInsetBottom();}
            view.setPadding(dp(12),dp(14)+top,dp(12),dp(8)+bottom);return insets;
        });root.requestApplyInsets();
        header=text(root,"ZERO 3.0  /  PROFILO "+slot,22,WHITE);status=text(root,"Versione candidata · prove sul Galaxy S21 da eseguire",12,MUTED);
        HorizontalScrollView horizontal=new HorizontalScrollView(this);LinearLayout nav=new LinearLayout(this);horizontal.addView(nav);root.addView(horizontal);
        for(int i=0;i<PAGES.length;i++){final int destination=i;button(nav,PAGES[i],()->showPage(destination));}
        ScrollView scroll=new ScrollView(this);content=new LinearLayout(this);content.setOrientation(1);scroll.addView(content);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));showPage(0);
        work(()->{profiles=Zero3AndroidSessionV325.profilesV354(this);session=Zero3AndroidSessionV325.profileV354(this,slot);
            try{session.resume();remember();publish("Profilo ripristinato e verificato");}catch(IOException empty){say(empty.getMessage());}});
    }
    private TextView text(LinearLayout parent,String value,int size,int color){TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(color);t.setPadding(dp(10),dp(10),dp(10),dp(10));t.setTextIsSelectable(true);parent.addView(t);return t;}
    private void button(LinearLayout parent,String label,Runnable action){Button b=new Button(this);b.setText(label);b.setTextSize(12);b.setMinHeight(dp(48));b.setContentDescription(label);b.setOnClickListener(v->action.run());parent.addView(b,new LinearLayout.LayoutParams(parent.getOrientation()==1?-1:-2,-2));}
    private void row(String[] labels,Runnable[] actions){LinearLayout line=new LinearLayout(this);content.addView(line);for(int i=0;i<labels.length;i++){Button b=new Button(this);b.setText(labels[i]);b.setTextSize(12);b.setMinHeight(dp(48));final Runnable a=actions[i];b.setOnClickListener(v->a.run());line.addView(b,new LinearLayout.LayoutParams(0,-2,1));}}
    public Zero3DashboardSnapshotV354 snapshotForQA(){return latest;}
    /** Read-only fence: queued pause/save operations complete before this snapshot. */
    public Future<Zero3DashboardSnapshotV354> snapshotAfterOwnerForQA(){return owner.submit(()->session.dashboardV354(page==2));}
    private void showPage(int destination){page=destination;content.removeAllViews();body=null;atlas=null;
        text(content,PAGES[page].toUpperCase(Locale.ITALY),20,CYAN);
        if(page==0||page==1||page==3){body=new BodyCanvas(this);content.addView(body,new LinearLayout.LayoutParams(-1,dp(page==0?340:260)));}
        if(page==2){atlas=new Zero3AtlasViewV354(this);content.addView(atlas,new LinearLayout.LayoutParams(-1,dp(450)));
            Spinner modules=new Spinner(this);String[] names={"Tutti i moduli","GRU centrale","Attore","Critic","Predizione sensi","Corteccia","Adesione","Modello fisico","Output fisici","Candidata shadow","Output shadow","Valore contestuale","Critic target"};
            modules.setBackgroundColor(CARD);
            modules.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,names){
                private View readable(View view){((TextView)view).setTextColor(WHITE);view.setBackgroundColor(CARD);return view;}
                @Override public View getView(int position,View recycled,ViewGroup parent){return readable(super.getView(position,recycled,parent));}
                @Override public View getDropDownView(int position,View recycled,ViewGroup parent){return readable(super.getDropDownView(position,recycled,parent));}
            });content.addView(modules);
            modules.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){}public void onItemSelected(android.widget.AdapterView<?> p,View v,int n,long id){if(atlas!=null)atlas.filter(n-1);}});
        }
        facts=text(content,"Carica o crea un profilo per osservare dati reali.",14,WHITE);
        GradientDrawable panel=new GradientDrawable();panel.setColor(CARD);panel.setCornerRadius(dp(14));panel.setStroke(dp(1),0xff264158);facts.setBackground(panel);
        if(page==0){row(new String[]{"×1","×5","×20","PAUSA"},new Runnable[]{()->start(1),()->start(5),()->start(20),this::pause});
            text(content,"TEST MAX è una simulazione accelerata; non certifica un'abilità.",12,MUTED);button(content,"TEST MAX — AREA SEPARATA",()->new AlertDialog.Builder(this).setTitle("Simulazione massima velocità")
                .setMessage("Avanza lo stesso organismo e continua l'apprendimento se attivo. Nessuna modifica al passo fisico. Conferma per avviare.").setNegativeButton("ANNULLA",null).setPositiveButton("AVVIA",(d,w)->start(0)).show());}
        if(page==9){
            button(content,"PROFILI ZERO3 — 12 SLOT",this::chooseSlot);button(content,"CREA NUOVO CERVELLO NELLO SLOT VUOTO",this::birth);
            button(content,"RIPRENDI PROFILO SELEZIONATO",()->{pause();work(()->{if(session.active())session.suspend();session.resume();publish("Ripresa verificata");});});
            button(content,"PAUSA E SALVA",this::pause);
            button(content,"APPRENDIMENTO / SOLA OSSERVAZIONE",()->{pause();work(()->{Zero3DashboardSnapshotV354 s=session.dashboardV354(false);session.observationModeV354(!s.observing);save();publish(s.observing?"Apprendimento attivo":"Sola osservazione: pesi e replay congelati");});});
            button(content,"NUOVA VITA EREDITANDO QUESTO CERVELLO",this::inheritLife);
            button(content,"CLONA IN UNO SLOT VUOTO",this::cloneSlot);
            button(content,"ESPORTA BACKUP VERIFICATO",()->document(1));button(content,"IMPORTA BACKUP IN SLOT VUOTO",()->document(2));
            button(content,"ESPORTA TELEMETRIA LOCALE",()->document(3));
            button(content,"AGGIORNA APP",()->new UpdateClient(this,this::say).checkAndInstall());
            button(content,"APRI PROFILI STORICI 2.2",()->{pause();getSharedPreferences("zero3-ui-v354",0).edit().putBoolean("default",false).apply();startActivity(new Intent(this,GameActivity.class));finish();});
            text(content,"I 12 slot ZERO3 sono separati dai 12 slot storici. Backup e importazione non sovrascrivono un profilo esistente. Versione configurazione354 · "+Zero3ConfigurationV354.HASH.substring(0,12)+". Salvataggio ogni32 decisioni: una chiusura forzata può perdere il tail non salvato.",12,MUTED);
        }
        if(page==8){button(content,"VERIFICA INTEGRITÀ SU CLONE DI LETTURA",()->{pause();work(()->{save();String result=profiles.status(slot);publish("Verifica archivio: "+result);});});button(content,"ESPORTA RAPPORTO DEL FRAME",()->document(3));}
        render();if(session!=null)work(()->{if(session.active())publish("Snapshot aggiornato");});
    }
    private void work(Work work){if(destroyed)return;owner.execute(()->{try{work.run();}catch(Exception e){say("ZERO3: "+e.getMessage());}});}
    private void say(String message){runOnUiThread(()->{if(!destroyed)status.setText(message);});}
    private void remember(){getSharedPreferences("zero3-ui-v354",0).edit().putInt("slot",slot).putBoolean("default",true).apply();}
    private void birth(){pause();new AlertDialog.Builder(this).setTitle("Nuovo cervello ZERO3 — slot "+slot).setMessage("Crea una vita e una rete vergini. Uno slot esistente viene protetto: usa uno dei 12 slot vuoti. Nessun dato 2.2 viene convertito.")
        .setNegativeButton("ANNULLA",null).setPositiveButton("CREA",(d,w)->work(()->{
            if(session.active())throw new IOException("Il profilo è già aperto: scegli uno slot vuoto");
            long ep=Math.max(1,System.currentTimeMillis());Zero3RuntimeV325 r=Zero3WorldBirthV354.create(ep,ep^0x354A12);
            try{session.startNewCompletionV354(r);session.step();save();remember();publish("Nuovo cervello salvato e verificato");}catch(Exception bad){r.close();session.close();throw bad;}
        })).show();}
    private void start(int speed){if(running)return;running=true;work(()->{
        if(session==null||!session.active()){running=false;throw new IOException("Prima crea o riprendi un profilo");}
        int unsaved=0;boolean fault=false;
        try{
            while(running&&!destroyed&&!session.ended()&&!session.atArchiveCapacity()){
                long from=System.nanoTime();session.step();unsaved++;
                if(unsaved>=Zero3ConfigurationV354.CHECKPOINT_INTERVAL){save();unsaved=0;}
                if(System.nanoTime()-lastPublish>600000000L){lastPublish=System.nanoTime();publish("Simulazione "+(speed==0?"TEST MAX":"×"+speed));}
                if(speed>0){long left=(long)(session.lastDecisionSeconds()*1e9/speed)-(System.nanoTime()-from);if(left>0)Thread.sleep(left/1000000,(int)(left%1000000));}
            }
        }catch(Exception bad){fault=true;session.close();try{session.resume();}catch(Exception missing){}say("Ciclo interrotto: "+bad.getMessage()+". Conservato l'ultimo checkpoint valido.");}
        finally{running=false;if(!fault&&session.active()){save();publish(session.ended()?"Vita terminata: cervello e memoria conservati":session.atArchiveCapacity()?"Archivio pieno: profilo conservato, nessuna esperienza aggiunta":"In pausa; checkpoint verificato");}}
    });}
    private void save()throws Exception{if(session!=null&&session.active()&&session.decisions()>0){session.suspend();session.resume();checkpoint="Checkpoint verificato · decisione "+session.decisions();}}
    private void pause(){running=false;work(()->{if(session!=null&&session.active()){save();publish("In pausa; salvataggio verificato");}});}
    private void publish(String message)throws Exception{Zero3DashboardSnapshotV354 s=session.dashboardV354(page==2);latest=s;runOnUiThread(()->{if(!destroyed){render();status.setText(message);}});}
    private static String value(float f){return Float.isFinite(f)?String.format(Locale.ITALY,"%.4f",f):"non disponibile";}
    private static String range(float[] a,int start,int count){if(a==null)return "non disponibile";StringBuilder b=new StringBuilder();for(int i=start;i<Math.min(a.length,start+count);i++)b.append(i==start?"":" · ").append(value(a[i]));return b.toString();}
    private void render(){Zero3DashboardSnapshotV354 s=latest;if(facts==null||s==null)return;header.setText("ZERO 3.0  /  PROFILO "+slot+"  /  "+s.body.decisions);
        if(body!=null)body.snapshot=s;if(body!=null)body.invalidate();if(atlas!=null)atlas.setGraph(s.graph);
        String common="Frame completato "+s.body.decisions+" · "+String.format(Locale.ITALY,"%.2f s simulati",s.elapsedLife)+"\n";
        StringBuilder b=new StringBuilder(common);
        switch(page){
            case 0:case 1:
                b.append(s.body.dead?"VITA TERMINATA":"ORGANISMO ATTIVO").append("\nRiserva ").append(value(s.body.reserve)).append(" /100\n").append(s.storm?"TEMPESTA":s.stormWarning?"TEMPESTA IN ARRIVO":"ARIA CALMA").append(" · integrità ").append(value(s.integrity)).append("\n")
                 .append(s.observing?"SOLA OSSERVAZIONE":"APPRENDIMENTO ATTIVO").append("\nTrain ").append(s.trained).append(" · validazione ").append(s.heldout).append(" · osservazione ").append(s.observations)
                 .append("\n").append(s.neurons).append(" unità neurali reali; nucleo ").append(s.body.neurons).append("\nIntenzione → movimento netto ").append(s.body.hasMotorOutcome?value(s.body.lastSignedProgress):"non disponibile dopo ripristino")
                 .append("\n").append(checkpoint).append("\nLa presenza di aggiornamenti non dimostra ancora una competenza.");break;
            case 2:
                b.append("Atlante: ogni nodo è un'unità di calcolo presente. Le porte sensoriali e i bias non sono neuroni. Layout3D illustrativo; pesi e unità provengono dalla rete.\n")
                 .append("Nucleo "+s.body.neurons+" · cortex64 · adesione16 · ingressi "+s.body.neuralInputs+"\n")
                 .append("Unità totali ").append(s.neurons).append("\nIl GRU usa truncated BPTT a1 passo. Le attività non acquisite restano indicate come mancanti.\n");appendPrediction(b,s);break;
            case 3:
                b.append("96 siti di membrana ·74 particelle interne\n16 settori ×4 canali: radiale, tangenziale, adesione, tensione\n")
                 .append("Touch solidi: ").append(range(s.touch,0,8)).append("\nComando adesivo: ").append(range(s.gripCommand,0,8))
                 .append("\nPresa fisica: ").append(range(s.held,0,8)).append("\nEfferenze reali, settore0: ").append(range(s.executed,0,4))
                 .append("\nCorrezioni congiunte ").append(s.physicalRepairs[1]).append(" · non risolte ").append(s.physicalRepairs[2])
                 .append("\nRisultato motorio disponibile: ").append(s.body.hasMotorOutcome?"sì":"no; appena ripristinato");break;
            case 4:
                if(s.executive==null)b.append("Memoria procedurale non presente in questo schema storico.");else{
                    b.append("Opzioni scoperte dai parametri scelti realmente dall'attore. Una candidata non è una competenza verificata.\n");
                    if(s.executive.skills.length==0)b.append("Nessuna candidata raccolta: occorrono esiti reali utili.");
                    for(Zero3ExecutiveV354.Skill k:s.executive.skills)b.append("\nOPZIONE #").append(k.id).append(" · ").append(k.status()).append("\nTrain ").append(k.train).append(" · A/B ").append(k.validation).append("/").append(k.controls)
                        .append(" · score ").append(value(k.score)).append(" / baseline ").append(value(k.baseline)).append("\nRichiami ").append(k.recalls).append(" · rollback ").append(k.reverts).append(" · energia media ").append(value(k.energy)).append("\n");
                    b.append("\nStorico opzioni ritirate ").append(s.executive.retiredHistory.length).append(" (ultime64 conservate).\nConsolidamento online distinto dalla certificazione su scene indipendenti.");}break;
            case 5:
                b.append("Plasticità effettiva: ").append(s.trained).append(" transizioni addestrate.\n");
                if(s.prediction!=null)b.append("Unità predittive mantenute ").append(s.prediction.predictiveUnits).append(" · shadow ").append(s.prediction.shadowUnits)
                    .append("\nCrescite accettate ").append(s.prediction.growthAccepted).append(" · respinte ").append(s.prediction.growthRejected).append("\nUna copia respinta non incrementa i neuroni mantenuti.\n");
                b.append("Resume mantiene pesi, ricorrenza, memoria e topologia nello stesso checkpoint. La morte conserva questo profilo.");break;
            case 6:
                b.append("ULTIMO PRE realmente ricevuto dal cervello\nVista spettrale anonima [20..43]: ").append(range(s.usedInput,20,24))
                    .append("\nRaggi solidi [44..59]: ").append(range(s.usedInput,44,16)).append("\nInterocezione [188..193]: ").append(range(s.usedInput,188,6))
                    .append("\nPOST fisico, stessi canali: ").append(range(s.postInput,188,6)).append("\nTatto/impulso/presa e comandi sono dati distinti. Nessuna coordinata di una gemma entra nel cervello.");break;
            case 7:
                b.append("Memoria di lavoro:64 stati GRU.\nJournal ").append(s.body.decisions).append(" esperienze complete; residenti ").append(s.body.residentReplay).append(" · non salvate ").append(s.body.unsavedReplay).append("\n");
                if(s.prediction!=null)b.append("Cache episodi TRAIN ").append(s.prediction.episodicRecords).append(" /64\n");
                if(s.executive!=null)b.append("Opzione corrente #").append(s.executive.goalId).append(" dalla decisione ").append(s.executive.goalSince).append("\n").append(s.executive.reason)
                    .append("\nValore contestuale ").append(value(s.executive.value)).append(" · ultimo bilancio ").append(value(s.executive.lastEnergy))
                    .append("\nProgresso predittivo su validazione ").append(value(s.executive.informationProgress)).append("; non è energia raccolta.\nInterruzioni ").append(s.executive.interruptions);
                if(s.executive!=null)b.append("\nVita genitore ").append(s.executive.parentEpisode).append(" · esperienze storiche ").append(s.executive.historicalDecisions);
                b.append("\nLe proposte immaginate non entrano nelle esperienze reali.");break;
            case 8:
                b.append("Train / validazione / sola osservazione: ").append(s.trained).append(" / ").append(s.heldout).append(" / ").append(s.observations)
                    .append("\nFisica: ").append(s.body.solverTicks).append(" passi da0,025s\n").append(checkpoint).append("\n");appendPrediction(b,s);
                b.append("\nGalaxy S21: heap, temperatura, batteria e stabilità richiedono la prova reale. Il pannello non inventa un esito device.");break;
            case 9:b.append("Profilo V3 ").append(slot).append(" /12\n").append(s.observing?"Sola osservazione: nessun aggiornamento dei pesi":"Training attivo con20% di validazione")
                .append("\n").append(checkpoint).append("\nApp offline per simulazione e apprendimento. La rete serve solo per AGGIORNA, avviato dall'utente.");break;
        }
        facts.setText(b.toString());
    }
    private void appendPrediction(StringBuilder b,Zero3DashboardSnapshotV354 s){if(s.prediction==null){b.append("Modello fisico non disponibile nello schema storico.");return;}
        for(int h=0;h<12;h++)b.append("\n").append(Zero3CognitionV353.HEAD_NAMES[h]).append(": ").append(s.prediction.reliable[h]?"utilizzabile":"in validazione")
            .append(" · MSE ").append(value(s.prediction.mse[h])).append(" / baseline ").append(value(s.prediction.baselineMse[h]));
        b.append("\nConfronti a un passo ").append(s.prediction.plannerUses);
        if(s.planner!=null)b.append(" · planner2–3 passi ").append(s.planner.uses).append("\nValidazioni orizzonte2/3: ").append(s.planner.samples[0]).append("/").append(s.planner.samples[1])
            .append(" · affidabili: ").append(s.planner.reliable[0]).append("/").append(s.planner.reliable[1]);
        b.append(". Orizzonti non affidabili esclusi dal controllo.");}
    private void chooseSlot(){pause();work(()->{String[] labels=new String[12];for(int i=0;i<12;i++)labels[i]="SLOT "+(i+1)+" · "+profiles.status(i+1);
        runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Profili ZERO3 — storici separati").setItems(labels,(d,index)->work(()->{
            if(session.active())session.suspend();session.close();slot=index+1;session=Zero3AndroidSessionV325.profileV354(this,slot);latest=null;remember();runOnUiThread(()->showPage(page));
            try{session.resume();publish("Profilo selezionato");}catch(IOException empty){runOnUiThread(()->{header.setText("ZERO3 / PROFILO "+slot);facts.setText("Slot vuoto o non recuperabile: "+empty.getMessage());});}
        })).show());});}
    private void inheritLife(){pause();work(()->{
        save();if(!session.active())throw new IOException("Prima carica il cervello da conservare");
        Zero3SegmentedArchiveV328.Bundle parent=new Zero3SegmentedArchiveV328(profiles.profile(slot)).load();
        if(parent==null)throw new IOException("Checkpoint genitore non disponibile");
        ArrayList<Integer> free=new ArrayList<>();for(int i=1;i<=12;i++)if(profiles.status(i).equals("VUOTO"))free.add(i);
        if(free.isEmpty())throw new IOException("Serve uno slot vuoto: il genitore non viene sovrascritto");
        String[] names=new String[free.size()];for(int i=0;i<names.length;i++)names[i]="NUOVA VITA NELLO SLOT "+free.get(i);
        runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Eredita pesi e opzioni, conserva l'originale").setItems(names,(d,n)->work(()->{
            session.suspend();session.close();slot=free.get(n);session=Zero3AndroidSessionV325.profileV354(this,slot);
            long episode=Math.max(1,System.currentTimeMillis());Zero3RuntimeV325 child=Zero3WorldBirthV354.create(episode,episode^0x354CD);
            try{session.startInheritedV354(child,parent.organism);session.step();save();remember();publish("Nuova vita: pesi, presa e opzioni ereditati; originale conservato");}
            catch(Exception bad){child.close();session.close();throw bad;}
        })).show());
    });}
    private void cloneSlot(){pause();work(()->{save();ArrayList<Integer> free=new ArrayList<>();for(int i=1;i<=12;i++)if(profiles.status(i).equals("VUOTO"))free.add(i);
        if(free.isEmpty())throw new IOException("Nessuno slot vuoto");String[] names=new String[free.size()];for(int i=0;i<names.length;i++)names[i]="SLOT "+free.get(i);
        runOnUiThread(()->new AlertDialog.Builder(this).setTitle("Copia completa del profilo").setItems(names,(d,n)->work(()->{profiles.cloneProfile(slot,free.get(n));say("Clone verificato nello slot "+free.get(n));})).show());});}
    private void document(int operation){pause();documentOperation=operation;Intent i=new Intent(operation==2?Intent.ACTION_OPEN_DOCUMENT:Intent.ACTION_CREATE_DOCUMENT);i.setType(operation==3?"application/json":"application/zip");i.addCategory(Intent.CATEGORY_OPENABLE);
        if(operation!=2)i.putExtra(Intent.EXTRA_TITLE,"ZERO3-slot"+slot+(operation==3?"-telemetria.json":"-backup.zip"));startActivityForResult(i,354);}
    @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request!=354||result!=RESULT_OK||data==null||data.getData()==null)return;Uri uri=data.getData();int operation=documentOperation;
        work(()->{save();if(operation==2){try(InputStream in=getContentResolver().openInputStream(uri)){profiles.importArchive(slot,in);}session.resume();publish("Backup importato e verificato");}
            else if(operation==1){try(OutputStream out=getContentResolver().openOutputStream(uri,"wt")){profiles.exportArchive(slot,out);}say("Backup esportato; originale conservato");}
            else{Zero3DashboardSnapshotV354 s=session.dashboardV354(false);JSONObject j=new JSONObject();j.put("schema",354);j.put("configuration",Zero3ConfigurationV354.HASH);j.put("episode",s.body.episode);j.put("decision",s.body.decisions);j.put("ticks",s.body.solverTicks);j.put("reserve",s.body.reserve);j.put("train",s.trained);j.put("validation",s.heldout);j.put("observations",s.observations);j.put("neurons",s.neurons);
                JSONArray inputs=new JSONArray();if(s.usedInput!=null)for(float x:s.usedInput)inputs.put(x);j.put("lastRealPRE",inputs);JSONArray motor=new JSONArray();if(s.executed!=null)for(float x:s.executed)motor.put(x);j.put("lastExecuted64",motor);
                JSONArray predictions=new JSONArray();if(s.prediction!=null)for(int h=0;h<12;h++){JSONObject p=new JSONObject();p.put("head",Zero3CognitionV353.HEAD_NAMES[h]);p.put("samples",s.prediction.heldout);p.put("mse",Float.isFinite(s.prediction.mse[h])?s.prediction.mse[h]:JSONObject.NULL);p.put("baseline",Float.isFinite(s.prediction.baselineMse[h])?s.prediction.baselineMse[h]:JSONObject.NULL);p.put("usable",s.prediction.reliable[h]);predictions.put(p);}j.put("prediction",predictions);
                try(OutputStream out=getContentResolver().openOutputStream(uri,"wt")){out.write(j.toString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8));}say("Telemetria esportata localmente su tua richiesta");}
        });}
    @Override protected void onPause(){super.onPause();pause();}
    @Override protected void onDestroy(){running=false;destroyed=true;owner.execute(()->{if(session!=null)session.close();});super.onDestroy();}
    private final class BodyCanvas extends View {
        Zero3DashboardSnapshotV354 snapshot;private final Paint p=new Paint(3);private final Path outline=new Path();private float zoom=1,panX,panY,lastX,lastY;private final ScaleGestureDetector scale;
        BodyCanvas(Context c){super(c);setContentDescription("Corpo fisico:96 punti membrana,74 particelle. Trascina per spostare la vista e pizzica per zoom; non controlla l'organismo.");scale=new ScaleGestureDetector(c,new ScaleGestureDetector.SimpleOnScaleGestureListener(){public boolean onScale(ScaleGestureDetector d){zoom=Math.max(.3f,Math.min(5,zoom*d.getScaleFactor()));invalidate();return true;}});}
        @Override protected void onDraw(Canvas c){c.drawColor(0xff0b1828);Zero3DashboardSnapshotV354 s=snapshot;if(s==null)return;float unit=Math.min(getWidth(),getHeight())/210f*zoom;
            c.save();c.translate(getWidth()/2f+panX,getHeight()/2f+panY);c.scale(unit,unit);c.translate(-s.body.cx,-s.body.cy);
            p.setColor(0xff22394d);p.setStrokeWidth(.5f);for(int k=-5;k<=5;k++){float x=(float)Math.floor(s.body.cx/40)*40+k*40;c.drawLine(x,s.body.cy-300,x,s.body.cy+300,p);}
            p.setColor(0xff15252e);for(float[] rect:s.solids)c.drawRect(rect[0],rect[1],rect[2],rect[3],p);
            for(int i=0;i<s.body.objectX.length;i++)if(s.body.objectVisible[i]){
                int law=s.body.objectLaw[i];p.setColor(s.body.objectColor[i]);float x=s.body.objectX[i],y=s.body.objectY[i];
                if(law==5)c.drawRect(x-20,y-20,x+20,y+20,p);
                else if(law==6){c.save();c.rotate((float)Math.toDegrees(s.body.objectAngle[i]),x,y);float half=s.body.objectHalfLength[i];c.drawRect(x-half,y-PanelPhysics.HALF_THICKNESS,x+half,y+PanelPhysics.HALF_THICKNESS,p);c.restore();}
                else c.drawCircle(x,y,s.body.objectRadius[i]*(law==2?1:.45f),p);
            }
            outline.reset();for(int i=0;i<96;i++){if(i==0)outline.moveTo(s.body.membraneX[i],s.body.membraneY[i]);else outline.lineTo(s.body.membraneX[i],s.body.membraneY[i]);}outline.close();p.setColor(0xff02070d);p.setStyle(Paint.Style.FILL);c.drawPath(outline,p);p.setColor(0xff439ba9);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(.65f);c.drawPath(outline,p);p.setStyle(Paint.Style.FILL);
            p.setColor(0xff225b71);for(int i=0;i<74;i++)c.drawCircle(s.innerX[i],s.innerY[i],1.2f,p);
            for(int i=0;i<96;i++){p.setColor(s.held[i]>=Morphology.GRIP_HOLD?0xffff526a:s.touch[i]>0?0xff7ee3e7:0xff33596d);c.drawCircle(s.body.membraneX[i],s.body.membraneY[i],s.held[i]>=Morphology.GRIP_HOLD?1.5f:.8f,p);}c.restore();
        }
        @Override public boolean onTouchEvent(MotionEvent e){scale.onTouchEvent(e);if(e.getActionMasked()==MotionEvent.ACTION_DOWN){lastX=e.getX();lastY=e.getY();getParent().requestDisallowInterceptTouchEvent(true);return true;}if(e.getActionMasked()==MotionEvent.ACTION_MOVE){if(!scale.isInProgress()){panX+=e.getX()-lastX;panY+=e.getY()-lastY;}lastX=e.getX();lastY=e.getY();invalidate();return true;}if(e.getActionMasked()==MotionEvent.ACTION_UP){getParent().requestDisallowInterceptTouchEvent(false);performClick();return true;}return true;}
        @Override public boolean performClick(){super.performClick();return true;}
    }
}
