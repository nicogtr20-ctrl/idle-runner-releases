package com.overmind.zero;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Date;

/**
 * The *only* telemetry trigger is this explicit user button.
 * No background tracking, analytics SDK, device identity or game mutation.
 * GitHub fine-grained token is configured once, kept encrypted on-device.
 */
public final class TelemetryActivity extends Activity {
    private static final int BG=0xff08121d,PANEL=0xff142833,WHITE=0xffe6faf6,
        CYAN=0xff78ebd8,MUTED=0xffa4bcc3,AMBER=0xffedc28b;
    private TextView status,link;
    private Button send,changeToken,copyLink;
    private volatile boolean running=false;
    private String reportUrl="";
    private int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
    private TextView text(String s,int size,int color,boolean bold){
        TextView t=new TextView(this);
        t.setText(s);t.setTextColor(color);t.setTextSize(size);
        t.setTextIsSelectable(!bold);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        t.setLineSpacing(dp(3),1.04f);return t;
    }
    private LinearLayout group(LinearLayout root){
        LinearLayout l=new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(16),dp(15),dp(16),dp(15));
        GradientDrawable bg=new GradientDrawable();
        bg.setColor(PANEL);bg.setCornerRadius(dp(18));l.setBackground(bg);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.topMargin=dp(14);root.addView(l,p);return l;
    }
    private void body(LinearLayout c,String msg){
        TextView t=text(msg,13,MUTED,false);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.topMargin=dp(9);c.addView(t,p);
    }
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);scroll.setBackgroundColor(BG);
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18),dp(30),dp(18),dp(24));scroll.addView(root);
        root.addView(text("CONDIVIDI DATI CON GPT",23,WHITE,true));
        body(root,"ZERO 2.0 • osservazione passiva • GitHub privato");
        LinearLayout description=group(root);
        description.addView(text("Che cosa viene condiviso",16,CYAN,true));
        body(description,"ZERO 2.0 analizza le esperienze REALI di ZERO ma NON decide i movimenti, non aggiunge energia e non cambia le leggi fisiche. Il cervello originale resta l\u0027unico a controllare il corpo.");
        body(description,"Insieme al profilo di gioco inviamo un riassunto dedicato: errori di previsione, affidabilita\u0027 contro il modello invariato, contatti energetici verificati, movimenti fisici, prese simultanee e motivi per cui il pianificatore si astiene. I dati grezzi dei 96 punti non vengono duplicati a ogni fotogramma.");
        body(description,"La copia del profilo di gioco ATTUALE: modelli neurali, "+
            "pesi motori, reti sensoriali, memorie, energie, vita e statistiche "+
            "storiche salvate. Include l'ultimo laboratorio A/B se disponibile "+
            "e la SCATOLA NERA degli ultimi 5 minuti simulati: sensi, azioni, "+
            "contributi dei cervelli, scelte casuali e movimento effettivo. "+
            "Se ZERO e' morto, include anche la traccia congelata della "+
            "sua ultima vita, senza modificare l'apprendimento. "+
            "Include anche l'OSSERVATORE PRESA: confronto cancello/casse, "+
            "comandi dei punti, contatti, tenute e interruzioni fisiche, "+
            "senza cambiare i comportamenti di ZERO.");
        body(description,"NON invia i 12 slot separati, nome/ID del telefono, "+
            "account personali o credenziali. Non modifica mai il personaggio, "+
            "non avvia addestramento e non carica nulla automaticamente.");
        body(description,"I dati saranno salvati nel repository GitHub "+
            "privato nicogtr20-ctrl/idle-runner-releases, nel ramo 'zero-telemetry'. "+
            "ChatGPT potra' leggerli tramite GitHub collegato, anche in "+
            "conversazioni successive.");
        LinearLayout config=group(root);
        config.addView(text("Collegamento GitHub",16,CYAN,true));
        body(config,"La prima volta crea un token GitHub fine-grained, "+
            "seleziona SOLTANTO il repository idle-runner-releases "+
            "e concedi Contents: Read and write. Non concedere permessi "+
            "ad altri repository. Il token verra' cifrato da Android Keystore "+
            "e restera' SOLO su questo telefono.");
        Button open=new Button(this);open.setText("APRI GITHUB PER CREARE IL TOKEN");
        open.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_VIEW,
            Uri.parse("https://github.com/settings/personal-access-tokens/new"))));
        config.addView(open);
        changeToken=new Button(this);
        changeToken.setText("CONFIGURA / CAMBIA TOKEN GITHUB");
        changeToken.setOnClickListener(v->askToken(false));config.addView(changeToken);
        LinearLayout actions=group(root);
        send=new Button(this);send.setText("CONDIVIDI DATI CON GPT");
        send.setOnClickListener(v->{
            if(TelemetryGitHub.hasToken(this))upload();
            else askToken(true);
        });
        actions.addView(send);
        status=text("Nessun caricamento effettuato.",13,AMBER,true);
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);
        sp.topMargin=dp(10);actions.addView(status,sp);
        link=text("",12,CYAN,false);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.topMargin=dp(10);actions.addView(link,lp);
        copyLink=new Button(this);copyLink.setText("COPIA LINK DEL REPORT");
        copyLink.setVisibility(Button.GONE);
        copyLink.setOnClickListener(v->{
            if(reportUrl.isEmpty())return;
            ((ClipboardManager)getSystemService(CLIPBOARD_SERVICE))
                .setPrimaryClip(ClipData.newPlainText("ZERO report GitHub",reportUrl));
            status.setText("Link copiato. Puoi condividerlo nella chat.");
        });
        actions.addView(copyLink);
        long last=getSharedPreferences(TelemetryGitHub.SETTINGS,MODE_PRIVATE)
            .getLong("last_success",0);
        if(last>0)status.setText("Ultimo caricamento riuscito: "+
            DateFormat.getDateTimeInstance().format(new Date(last)));
        TextView back=text("‹ TORNA AD ANALISI",16,CYAN,true);
        back.setGravity(Gravity.CENTER);
        back.setPadding(0,dp(23),0,dp(23));
        back.setOnClickListener(v->finish());root.addView(back);
        setContentView(scroll);
    }
    private void askToken(boolean after){
        if(running)return;
        EditText entry=new EditText(this);
        entry.setSingleLine(true);
        entry.setTextSize(15);
        entry.setInputType(InputType.TYPE_CLASS_TEXT|
            InputType.TYPE_TEXT_VARIATION_PASSWORD);
        entry.setHint("github_pat_...");
        new AlertDialog.Builder(this)
            .setTitle("Autorizza soltanto il tuo repository ZERO")
            .setMessage("Incolla il token fine-grained con Contents: "+
                "Read and write sul solo idle-runner-releases. "+
                "La credenziale rimane cifrata sul telefono e non appare nei report.")
            .setView(entry)
            .setNegativeButton("ANNULLA",(d,w)->{})
            .setPositiveButton(after?"SALVA E CONDIVIDI":"SALVA TOKEN",(d,w)->{
                try{
                    TelemetryGitHub.storeToken(this,entry.getText().toString());
                    status.setText("GitHub configurato in modo cifrato.");
                    if(after)upload();
                }catch(Exception error){
                    status.setText("Configurazione fallita: "+error.getMessage());
                }
            }).show();
    }
    private void upload(){
        if(running)return;
        running=true;
        send.setEnabled(false);changeToken.setEnabled(false);
        status.setText("Preparazione esportazione...");
        new Thread(()->{
            try{
                String url=TelemetryGitHub.upload(getApplicationContext(),
                    stage->runOnUiThread(()->{
                        if(!isFinishing()&&!isDestroyed())status.setText(stage);
                    }));
                reportUrl=url;
                runOnUiThread(()->{
                    if(isFinishing()||isDestroyed())return;
                    status.setText("Report salvato su GitHub privato. "+
                        "Ora puoi chiedere a GPT: analizza gli ultimi dati di ZERO.");
                    link.setText(url);copyLink.setVisibility(Button.VISIBLE);
                });
            }catch(Exception failure){
                runOnUiThread(()->{
                    if(isFinishing()||isDestroyed())return;
                    // Never place an entire malformed remote JSON or a
                    // large network payload into the screen's error label.
                    String reason=failure.getMessage();
                    if(reason==null||reason.trim().isEmpty())
                        reason="Errore di rete o lettura risposta GitHub";
                    reason=reason.replace((char)10,' ').replace((char)13,' ');
                    if(reason.length()>240)
                        reason=reason.substring(0,240)+"...";
                    status.setText("CARICAMENTO NON RIUSCITO: "+reason+
                        ". Il tuo ZERO e' invariato.");
                });
            }finally{
                running=false;
                runOnUiThread(()->{
                    if(!isFinishing()&&!isDestroyed()){
                        send.setEnabled(true);changeToken.setEnabled(true);
                    }
                });
            }
        },"zero-consented-report-upload").start();
    }
}
