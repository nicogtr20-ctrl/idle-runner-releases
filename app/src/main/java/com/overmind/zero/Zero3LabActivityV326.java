package com.overmind.zero;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Process;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.widget.*;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ZERO 3.26 — explicitly opted-in, self-contained ANDROID research UI.
 * The ONLY bridge is Zero3AndroidSessionV325; it never opens 2.2 save slots,
 * legacy GameActivity profiles or the updater. The dedicated worker owns all
 * physics and checkpoint mutations. UI receives only defensive Display data.
 *
 * This is an experimental visualization, NOT the promised final V3 HUD or
 * a proven autonomous locomotion/actor-critic system.
 */
public final class Zero3LabActivityV326 extends Activity {
    private static final int BG=0xff07111d,WHITE=0xffe6f5f8,
        MUTED=0xff9bb6c5,CYAN=0xff51cfe5,BLUE=0xff398ae5;
    private final ExecutorService worker=Executors.newSingleThreadExecutor(r->{
        Thread t=new Thread(r,"zero3-android-research");
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    });
    private volatile boolean running,leaving;
    private Zero3AndroidSessionV325 session;
    private TextView info,status;
    private BodyView body;
    private volatile Zero3RuntimeV325.Display latest;
    private long lastDraw;
    private Button start1,start5;
    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ScrollView scroller=new ScrollView(this);
        LinearLayout root=new LinearLayout(this);
        root.setBackgroundColor(BG);root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22,24,22,28);scroller.addView(root);
        setContentView(scroller);
        addText(root,"ZERO 3.0   •   LABORATORIO",22,WHITE);
        addText(root,"MODALITÀ SPERIMENTALE — non usa i salvataggi 2.2. "+
            "Il cervello V3 sceglie autonomamente; qui non è dimostrata la camminata.",
            12,MUTED);
        body=new BodyView(this);
        root.addView(body,new LinearLayout.LayoutParams(-1,
                    (int)(getResources().getDisplayMetrics().density*330)));
        info=addText(root,"Nessun organismo V3 caricato.",13,WHITE);
        status=addText(root,"Crea una vita vergine o riprendi un checkpoint V3.",
                       12,MUTED);
        row(root,"CREA ZERO 3.0 VERGINE","RIPRENDI SOLO V3",
             ()->confirmBirth(),()->enqueue(this::resume));
        row(root,"AVVIA x1","AVVIA x5",()->start(1),()->start(5));
        row(root,"PAUSA E SALVA","TORNA ALLA 2.2",
             ()->stopAndSave(false),()->{stopAndSave(true);});
        addText(root,"V3 SPERIMENTALE: replay a segmenti immutabili (V3.28), "+
            "limite provvisorio di 3.500 decisioni per vita. "+
            "Le prestazioni vanno ancora verificate sul Galaxy S21.",
            12,0xffffc98c);
        addText(root,"Ogni decisione = 12 passi fisici. Salvataggio automatico "+
            "ogni 8 decisioni completate; nessun salvataggio a metà di un'azione. "+
            "Una chiusura forzata può perdere fino alle ultime decisioni non salvate.",
            11,MUTED);
        try{session=Zero3AndroidSessionV325.create(this,"isolated-v326");}
        catch(IOException invalid){status.setText("Archivio V3 non disponibile: "+invalid.getMessage());}
    }
    private TextView addText(LinearLayout parent,String s,int size,int color){
        TextView v=new TextView(this);v.setText(s);v.setTextColor(color);
        v.setTextSize(size);v.setPadding(4,9,4,9);v.setTextIsSelectable(true);
        parent.addView(v);return v;
    }
    private void row(LinearLayout root,String left,String right,Runnable a,Runnable b){
        LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(r);
        Button l=new Button(this),other=new Button(this);
        l.setText(left);other.setText(right);l.setTextSize(11);other.setTextSize(11);
        r.addView(l,new LinearLayout.LayoutParams(0,-2,1));
        r.addView(other,new LinearLayout.LayoutParams(0,-2,1));
        l.setOnClickListener(v->a.run());other.setOnClickListener(v->b.run());
        if(left.startsWith("AVVIA")){start1=l;start5=other;}
    }
    private void enqueue(Action op){
        if(leaving||session==null){show("Sessione V3 indisponibile.");return;}
        worker.execute(()->{
            try{op.run();}catch(Exception e){show("V3: "+e.getMessage());}
        });
    }
    private interface Action {void run()throws Exception;}
    private void confirmBirth(){
        if(running){show("Prima interrompi la simulazione.");return;}
        new AlertDialog.Builder(this).setTitle("Nuovo organismo ZERO 3.0")
            .setMessage("Crea un cervello vergine in un ambiente fisico V3. "+
                "Nessun profilo 2.2 verrà letto o modificato. "+
                "Un archivio V3 esistente NON verrà sovrascritto.")
            .setNegativeButton("ANNULLA",null)
            .setPositiveButton("CREA V3",(dialog,which)->enqueue(()->{
                // A fresh episode ID avoids sharing the static motor authority.
                long episode=Math.max(1L,System.currentTimeMillis());
                Zero3RuntimeV325 organism=
                    Zero3WorldBirthV326.create(episode,0x33226017L^episode);
                try{
                    session.startNew(organism);
                    // V324 transaction format requires one COMPLETED physics
                    // decision; don't leave a newborn with no durable history.
                    session.step();
                    session.suspend();
                    session.resume();
                }catch(Exception rejected){session.close();organism.close();throw rejected;}
                publish("Nuova vita V3: prima decisione fisica salvata.");
            })).show();
    }
    private void resume()throws Exception{
        if(running)throw new IOException("Ferma la simulazione prima di caricare");
        session.resume();
        publish("Cervello V3 ripristinato dal checkpoint verificato.");
    }
    private void start(int factor){
        if(leaving||running)return;
        if(session==null||!session.active()){
            show("Prima crea o riprendi un organismo V3.");
            return;
        }
        running=true;
        enqueue(()->{
            try{
                show("Simulazione reale x"+factor+" attiva");
                int sinceSaved=0;
                while(running&&!leaving&&session.active()){
                    if(session.ended()){show("ZERO ha esaurito l'energia.");break;}
                    if(session.atArchiveCapacity()){
                        show("Limite del replay V3: "+
                            session.durableDecisionLimit()+
                            " decisioni. Salvataggio protetto per questo test.");
                        break;
                    }
                    session.step();
                    sinceSaved++;
                    if(sinceSaved>=8){session.suspend();session.resume();sinceSaved=0;}
                    long now=System.nanoTime();
                    if(now-lastDraw>130000000L||session.ended()){
                        lastDraw=now;publish("Decisione fisica completata");
                    }
                    // 0.3 s of physical simulation per choice; do not
                    // claim actual x1 accuracy until measured on device.
                    Thread.sleep(factor==1?260L:18L);
                }
            }catch(InterruptedException interrupted){
                Thread.currentThread().interrupt();
            }catch(Exception error){show("Errore ciclo V3: "+error.getMessage());}
            finally{
                running=false;
                try{
                    if(session.active()){session.suspend();session.resume();}
                    if(session.active()&&session.atArchiveCapacity())
                        publish("Limite archivio V3 raggiunto. Ultima vita salvata.");
                    else if(session.active())publish("In pausa; checkpoint verificato.");
                }catch(Exception error){
                    show("ATTENZIONE: salvataggio fallito: "+error.getMessage());
                }
            }
        });
    }
    private void stopAndSave(boolean exit){
        running=false;
        if(exit)leaving=true;
        if(session==null){if(exit)finish();return;}
        worker.execute(()->{
            try{
                if(session.active())session.suspend();
                show("Checkpoint V3 salvato in cartella privata.");
                if(exit)runOnUiThread(this::finish);
            }catch(Exception error){
                show("Salvataggio V3 NON riuscito: "+error.getMessage());
                if(exit)runOnUiThread(this::finish);
            }
        });
    }
    private void show(String message){
        runOnUiThread(()->{
            if(!isFinishing()&&!isDestroyed()&&status!=null)
                status.setText(message);
        });
    }
    private void publish(String message)throws Exception{
        Zero3RuntimeV325.Display s=session.display();
        latest=s;
        runOnUiThread(()->{
            if(isFinishing()||isDestroyed())return;
            info.setText(String.format(Locale.ITALY,
                "VITA %d   •   DECISIONI %d   •   ENERGIA %.1f/100\n"+
                "DANNO FISICO PRECEDENTE %.1f  •  INPUT VITALE FUSO\n"+
                "MATERIA 96 patch + 74 particelle interne   •   %d oggetti\n"+
                "GEL %d passi   •   CERVELLO ricorrente: %d stati\n"+
                "REPLAY COMPLETO IN RAM: %d record  •  NON SALVATI: %d%s",
                s.episode,s.decisions,s.reserve,s.priorDamage,s.objects,
                s.solverTicks,s.neurons,s.residentReplay,s.unsavedReplay,
                s.dead?"   •   VITA TERMINATA":""));
            status.setText(message);body.invalidate();
        });
    }
    @Override protected void onPause(){
        // Android does not guarantee CPU time after onPause; autosaves limit
        // loss of already completed decisions. Never block the UI on fsync.
        running=false;
        if(session!=null)worker.execute(()->{
            try{if(session.active())session.suspend();}
            catch(Exception e){show("V3 non salvato: "+e.getMessage());}
        });
        super.onPause();
    }
    @Override protected void onDestroy(){
        leaving=true;running=false;
        if(session!=null)worker.execute(()->{
            try{if(session.active())session.suspend();}
            catch(IOException ignored){}
            finally{session.close();}
        });
        worker.shutdown();
        super.onDestroy();
    }
    private final class BodyView extends View{
        private final Paint paint=new Paint(3);
        BodyView(Context c){super(c);}
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            c.drawColor(0xff0d2030);
            Zero3RuntimeV325.Display s=latest;
            if(s==null){
                paint.setColor(MUTED);paint.setTextSize(30f);
                c.drawText("ZERO 3.0  •  nessun dato vivo",18f,getHeight()/2f,paint);
                return;
            }
            float scale=Math.min(getWidth()/320f,getHeight()/285f);
            c.save();c.translate(getWidth()/2f,getHeight()/2f);
            c.scale(scale,scale);c.translate(-s.cx,-s.cy);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2);
            paint.setColor(0xff456e82);
            c.drawRect(s.cx-150,s.cy-130,s.cx+150,s.cy+130,paint);
            for(int i=0;i<s.objects;i++){
                float x=s.objectX[i],y=s.objectY[i];int type=s.objectLaw[i];
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(type==5?0xff82969d:type==3?0xff7359f0:0xff279dcc);
                if(type==5)c.drawRect(x-13,y-13,x+13,y+13,paint);
                else c.drawCircle(x,y,5f,paint);
            }
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(4f);
            paint.setColor(0xffa6a0bd);
            c.drawLine(s.doorX,s.doorY-69,s.doorX,s.doorY+69,paint);
            Path membrane=new Path();
            membrane.moveTo(s.membraneX[0],s.membraneY[0]);
            for(int i=1;i<s.membraneX.length;i++)
                membrane.lineTo(s.membraneX[i],s.membraneY[i]);
            membrane.close();
            paint.setColor(0xff52adff);paint.setStrokeWidth(2.2f);
            c.drawPath(membrane,paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(BLUE);
            for(int i=0;i<s.membraneX.length;i++)
                c.drawCircle(s.membraneX[i],s.membraneY[i],1.6f,paint);
            paint.setColor(0xffb8ecff);c.drawCircle(s.cx,s.cy,3.5f,paint);
            c.restore();
            paint.setColor(CYAN);paint.setTextSize(25f);
            c.drawText("CORPO REALE • VISUALIZZAZIONE V3",14f,30f,paint);
        }
    }
}
