package com.overmind.zero;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/** Player-facing save/load screen. This Activity opens only after the
 * simulation has been paused and its working auto-save was flushed.
 * Named saves are immutable until the player confirms an overwrite.
 */
public final class SaveSlotsActivity extends Activity {
    private static final int BG=0xff07121c,PANEL=0xff122732,WHITE=0xffe8fff9,
        TEAL=0xff81f5df,MUTED=0xff99b4bc,GOLD=0xfff2c27a,DANGER=0xfff2a0a0;
    private SaveSlotStore store;
    private LinearLayout content;
    int dp(float value){return Math.round(value*getResources().getDisplayMetrics().density);}
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        store=new SaveSlotStore(this);
        showSlots();
    }
    private GradientDrawable bg(int color,int radius){
        GradientDrawable g=new GradientDrawable();g.setColor(color);
        g.setCornerRadius(dp(radius));return g;
    }
    private TextView text(String s,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextSize(size);
        t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        t.setLineSpacing(dp(3),1f);return t;
    }
    private LinearLayout vertical(){
        LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;
    }
    private LinearLayout card(int color){
        LinearLayout l=vertical();l.setPadding(dp(15),dp(13),dp(15),dp(15));
        l.setBackground(bg(color,16));
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(-1,-2);
        params.topMargin=dp(11);content.addView(l,params);
        return l;
    }
    private void space(LinearLayout target,int amount){
        View v=new View(this);target.addView(v,new LinearLayout.LayoutParams(1,dp(amount)));
    }
    private void button(LinearLayout line,String label,int color,Runnable click){
        TextView b=text(label,12,color,true);
        b.setGravity(Gravity.CENTER);
        b.setPadding(dp(7),dp(12),dp(7),dp(12));
        b.setBackground(bg(0xff203944,11));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1f);
        p.rightMargin=dp(5);line.addView(b,p);
        b.setOnClickListener(v->click.run());
    }
    private LinearLayout actions(LinearLayout target){
        LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.topMargin=dp(8);target.addView(l,lp);return l;
    }
    private String date(long time){
        return DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,
            Locale.ITALY).format(new Date(time));
    }
    private void showError(Exception problem){
        String message=problem.getMessage();
        new AlertDialog.Builder(this).setTitle("OPERAZIONE NON RIUSCITA")
            .setMessage(message==null?"I dati non sono stati modificati.":message)
            .setPositiveButton("OK",null).show();
    }
    private void execute(Runnable operation,boolean returnToGame){
        try{
            operation.run();
            if(returnToGame){
                // GameActivity resumes its existing View but must rebuild
                // ALL brain objects from newly switched SharedPreferences.
                Intent i=new Intent(this,GameActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);finish();
            }else showSlots();
        }catch(Exception e){showError(e);}
    }
    private void confirm(String title,String message,String action,Runnable next){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message)
            .setNegativeButton("ANNULLA",null)
            .setPositiveButton(action,(dialog,button)->next.run()).show();
    }
    private void askName(String title,String value,java.util.function.Consumer<String> next){
        EditText name=new EditText(this);
        name.setSingleLine(true);
        name.setText(value);
        name.setSelectAllOnFocus(true);
        name.setTextColor(WHITE);
        name.setHint("Nome del personaggio");
        name.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(36)});
        LinearLayout wrap=vertical();wrap.setPadding(dp(20),dp(8),dp(20),0);
        wrap.addView(name,new LinearLayout.LayoutParams(-1,-2));
        AlertDialog dialog=new AlertDialog.Builder(this)
            .setTitle(title).setMessage("Scegli un nome per riconoscere questo ZERO.")
            .setView(wrap).setNegativeButton("ANNULLA",null)
            .setPositiveButton("CONFERMA",null).create();
        dialog.setOnShowListener(v->dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(b->{
                String clean=SaveSlotStore.cleanName(name.getText().toString());
                if(clean.length()==0){name.setError("Scrivi un nome");return;}
                dialog.dismiss();next.accept(clean);
            }));
        dialog.show();
    }
    private void chooseSave(int index,SaveSlotStore.Entry entry){
        askName(entry==null?"SALVA NUOVO ZERO":"SOVRASCRIVI SLOT "+(index+1),
            entry==null?"ZERO "+(index+1):entry.name,
            name->{
                Runnable action=()->execute(()->store.save(index,name),false);
                if(entry!=null)confirm("SOVRASCRIVERE "+entry.name+"?",
                    "Questo slot verrà sostituito dal personaggio attuale.\n\n"+
                    "Gli altri salvataggi rimarranno invariati.","SOVRASCRIVI",action);
                else action.run();
            });
    }
    private void chooseLoad(int index,SaveSlotStore.Entry entry){
        confirm("CARICARE «"+entry.name+"»?",
            "Ritornerai al cervello e ai progressi salvati il "+date(entry.time)+
            ".\n\nLa partita attuale verrà conservata automaticamente in "+
            "«RECUPERA ZERO PRECEDENTE» prima del cambio.\n\n"+
            "La posizione, il cancello, gli oggetti, il cervello e i progressi torneranno al punto salvato.",
            "CARICA",()->execute(()->store.load(index),true));
    }
    private void newGame(){
        confirm("CREARE UN NUOVO ZERO?",
            "Inizierà con un cervello vergine e il NUOVO CORPO SIMBIONTE fluido. "+
            "I salvataggi con nome non verranno cancellati.\n\n"+
            "Il personaggio attuale verrà conservato in «RECUPERA ZERO PRECEDENTE», "+
            "ma quel backup sarà sostituito al prossimo cambio partita.",
            "CONTINUA",()->confirm("ULTIMA CONFERMA",
                "Se non vuoi perdere questo ZERO, salvalo anche in uno slot con nome.\n\n"+
                "Vuoi davvero creare un nuovo personaggio?",
                "NUOVO ZERO",()->execute(()->store.newGame(),true)));
    }
    private void loadRecovery(){
        SaveSlotStore.Entry old=store.recovery();
        if(old==null)return;
        confirm("RECUPERARE IL PRECEDENTE ZERO?",
            "Ripristina il personaggio sostituito il "+date(old.time)+
            ". Prima di farlo, la partita attuale diventerà il nuovo backup di recupero.",
            "RECUPERA",()->execute(()->store.loadRecovery(),true));
    }
    private void showSlots(){
        ScrollView scroller=new ScrollView(this);
        scroller.setFillViewport(true);scroller.setBackgroundColor(BG);
        content=vertical();content.setPadding(dp(15),dp(24),dp(15),dp(38));
        scroller.addView(content);setContentView(scroller);
        content.addView(text("PARTITE DI ZERO",25,WHITE,true));
        content.addView(text("12 slot liberi • nomi personalizzati • caricamento sicuro",
            12,MUTED,false));
        LinearLayout now=card(0xff15392f);
        now.addView(text("PARTITA IN CORSO",15,TEAL,true));
        now.addView(text("Salvataggio automatico attivo. ZERO continua a imparare "+
            "nel personaggio attualmente caricato.",12,WHITE,false));
        space(now,7);
        now.addView(text("Gli slot con nome rimangono fermi al momento in cui premi SALVA. "+
            "Per aggiornare uno slot, premi SOVRASCRIVI.",11,MUTED,false));

        SaveSlotStore.Entry recovered=store.recovery();
        if(recovered!=null){
            LinearLayout backup=card(0xff283444);
            backup.addView(text("RECUPERA ZERO PRECEDENTE",14,GOLD,true));
            backup.addView(text("Backup di sicurezza • "+date(recovered.time),12,MUTED,false));
            LinearLayout buttons=actions(backup);
            button(buttons,"RECUPERA",TEAL,this::loadRecovery);
        }
        LinearLayout start=card(0xff312c37);
        start.addView(text("NUOVA PARTITA",14,GOLD,true));
        start.addView(text("Un ZERO vergine con corpo SYMBIOTE, senza alterare gli ZERO storici salvati.",12,MUTED,false));
        button(actions(start),"＋ CREA ZERO SIMBIONTE",GOLD,this::newGame);

        content.addView(text("I TUOI SLOT",17,WHITE,true));
        for(int i=0;i<SaveSlotStore.SLOTS;i++){
            final int slot=i;
            SaveSlotStore.Entry entry=store.get(i);
            LinearLayout panel=card(PANEL);
            panel.addView(text("SLOT "+(i+1)+"   "+
                (entry==null?store.occupied(i)?"⚠ DATI DANNEGGIATI":"＋ LIBERO":entry.name),
                14,entry==null?MUTED:TEAL,true));
            if(entry!=null){
                panel.addView(text("Salvato il "+date(entry.time)+" • "+
                    (entry.size/1024)+" KB",11,MUTED,false));
                LinearLayout first=actions(panel);
                button(first,"CARICA",TEAL,()->chooseLoad(slot,entry));
                button(first,"SOVRASCRIVI",GOLD,()->chooseSave(slot,entry));
                LinearLayout second=actions(panel);
                button(second,"RINOMINA",WHITE,()->askName("RINOMINA SLOT",entry.name,
                    name->execute(()->store.rename(slot,name),false)));
                button(second,"ELIMINA",DANGER,()->confirm("ELIMINARE «"+entry.name+"»?",
                    "Questo salvataggio sarà eliminato. "+
                    "Non perderai la partita attualmente in corso.",
                    "ELIMINA",()->execute(()->store.delete(slot),false)));
            }else if(store.occupied(i)){
                button(actions(panel),"ELIMINA SLOT DANNEGGIATO",DANGER,
                    ()->confirm("ELIMINARE IL FILE DANNEGGIATO?",
                        "Il contenuto non può essere caricato, ma nessun altro slot verrà modificato.",
                        "ELIMINA",()->execute(()->store.delete(slot),false)));
            }else button(actions(panel),"SALVA QUI",TEAL,()->chooseSave(slot,null));
        }
        LinearLayout end=card(0xff1b2a35);
        end.addView(text("COSA VIENE SALVATO",14,WHITE,true));
        end.addView(text("Tutto il personaggio: cervelli neurali, ricordi, abilità, "+
            "energia, statistiche, posizione e forma fisica della membrana, "+
            "oggetti e cancello. Ogni slot è una fotografia della partita in quel momento.",
            12,MUTED,false));
        button(actions(end),"‹ TORNA AL GIOCO",TEAL,this::finish);
    }
}
