package com.overmind.zero;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.view.View;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

/** Read-only player-facing report; never feeds statistics into ZERO's policy. */
public final class StatisticsActivity extends Activity {
    private static final int BG=0xff09121d,PANEL=0xff142733,TEXT=0xffe5f9f5;
    private static final int CYAN=0xff82eadb,MUTED=0xffa1bbc5,GREEN=0xff65dfae;
    private static final int RED=0xffef9397,GOLD=0xfff6c783;
    private static final int WINDOW=25;
    private LifeStatistics stats;

    int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        SharedPreferences prefs=getSharedPreferences("zero_motorbrain_001",Context.MODE_PRIVATE);
        stats=new LifeStatistics();stats.restore(prefs.getString(LifeStatistics.KEY,""));
        showStats();
    }
    TextView label(String s,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextColor(color);
        t.setTextSize(size);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        t.setLineSpacing(dp(2),1f);return t;
    }
    LinearLayout col(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    void gap(LinearLayout p,int h){
        View v=new View(this);p.addView(v,new LinearLayout.LayoutParams(1,dp(h)));
    }
    LinearLayout panel(LinearLayout p){
        LinearLayout box=col();box.setPadding(dp(15),dp(15),dp(15),dp(15));
        android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();
        bg.setColor(PANEL);bg.setCornerRadius(dp(17));box.setBackground(bg);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.topMargin=dp(12);p.addView(box,lp);return box;
    }
    String num(double x){
        if(Double.isNaN(x))return "—";
        return String.format(Locale.ITALY,"%.1f",x);
    }
    String metric(int id,double value){
        if(Double.isNaN(value))return "—";
        if(id==LifeStatistics.DURATION)return LifeStatistics.duration(value);
        if(id==LifeStatistics.GAIN||id==LifeStatistics.DAMAGE)return num(value);
        return String.format(Locale.ITALY,"%.0f",value)+(id==LifeStatistics.NET||id==LifeStatistics.GATE?" U":"");
    }
    void line(LinearLayout p,String k,String v,int color){
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);
        TextView a=label(k,12,MUTED,false),b=label(v,14,color,true);
        row.addView(a,new LinearLayout.LayoutParams(0,-2,1));
        b.setGravity(Gravity.END);row.addView(b);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(9);
        p.addView(row,lp);
    }
    boolean lowerIsBetter(int metric){
        return metric==LifeStatistics.HARM||metric==LifeStatistics.DAMAGE;
    }
    String delta(int metric,double recent,double previous){
        double difference=recent-previous;
        String sign=difference>0?"+":difference<0?"−":"±";
        double amount=Math.abs(difference);
        String abs=metric==LifeStatistics.DURATION?
            String.format(Locale.ITALY,"%.0f s",amount):
            String.format(Locale.ITALY,"%.1f",amount)+(metric==LifeStatistics.NET||
                metric==LifeStatistics.GATE?" U":"");
        double denom=Math.abs(previous);
        if(denom<1e-9)return sign+abs;
        double pct=Math.abs(difference)/denom*100.0;
        return sign+abs+"  ("+sign+String.format(Locale.ITALY,"%.0f%%",pct)+")";
    }
    int trendColor(int metric,double recent,double previous){
        double diff=recent-previous;
        if(Math.abs(diff)<1e-9)return MUTED;
        boolean better=lowerIsBetter(metric)?diff<0:diff>0;
        return better?GREEN:RED;
    }
    double evolutionIndex(){
        if(stats.historyCount()<50)return Double.NaN;
        int[] metrics={LifeStatistics.DURATION,LifeStatistics.GOOD,LifeStatistics.HARM,
            LifeStatistics.DISKS,LifeStatistics.CELLS,LifeStatistics.NET,
            LifeStatistics.GATE,LifeStatistics.ROOMS,LifeStatistics.RECALL};
        double sum=0;int used=0;
        for(int m:metrics){
            double a=stats.average(m,0,WINDOW),b=stats.average(m,WINDOW,WINDOW);
            if(Double.isNaN(a)||Double.isNaN(b)||(a==0&&b==0))continue;
            // Symmetric relative change is bounded and does not explode at baseline zero.
            double d=2.0*(a-b)/(Math.abs(a)+Math.abs(b)+1e-9);
            if(lowerIsBetter(m))d=-d;
            d=Math.max(-1,Math.min(1,d));
            sum+=d;used++;
        }
        return used==0?0:100.0*sum/used;
    }
    void showStats(){
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        LinearLayout body=col();body.setPadding(dp(16),dp(26),dp(16),dp(30));
        scroll.addView(body);
        body.addView(label("ZERO / STATISTICHE",24,TEXT,true));
        gap(body,5);
        body.addView(label("Misure del mondo reale simulato • osservazione soltanto • non alterano il cervello",12,MUTED,false));

        LifeStatistics.Life live=stats.current();
        LinearLayout now=panel(body);
        now.addView(label("VITA IN CORSO",15,CYAN,true));
        gap(now,8);
        now.addView(label(LifeStatistics.duration(live.seconds),34,TEXT,true));
        now.addView(label("Tempo SIMULATO: a x5 o x20 cresce 5 o 20 volte più velocemente. In pausa resta fermo.",12,MUTED,false));
        if(live.partial){
            gap(now,8);
            now.addView(label("Questa vita era già in corso prima dell'attivazione completa delle statistiche. Non entrerà nelle medie: la prossima morte avvierà un campione completo.",12,GOLD,false));
        }
        gap(now,8);
        int[] essential={LifeStatistics.GOOD,LifeStatistics.HARM,LifeStatistics.GAIN,
            LifeStatistics.DAMAGE,LifeStatistics.DISKS,LifeStatistics.CELLS,
            LifeStatistics.NET,LifeStatistics.GATE,LifeStatistics.ROOMS,LifeStatistics.RECALL};
        for(int m:essential)line(now,LifeStatistics.LABELS[m],metric(m,live.value(m)),TEXT);

        LinearLayout averages=panel(body);
        averages.addView(label("CAPACITA' ATTUALE • ULTIME 25 VITE",15,CYAN,true));
        int seen=stats.sampleCount(WINDOW);
        averages.addView(label(seen==0?"Nessuna vita completa misurata ancora.":
            "Media mobile su "+seen+(seen==WINDOW?"":" / "+WINDOW)+" vite complete. Riduce il peso della singola partita fortunata o sfortunata.",12,MUTED,false));
        if(seen>0){
            int[] chosen={LifeStatistics.DURATION,LifeStatistics.GOOD,LifeStatistics.HARM,
                LifeStatistics.GAIN,LifeStatistics.DAMAGE,LifeStatistics.DISKS,
                LifeStatistics.CELLS,LifeStatistics.NET,LifeStatistics.GATE,
                LifeStatistics.ROOMS,LifeStatistics.RECALL};
            for(int m:chosen)line(averages,LifeStatistics.LABELS[m],
                metric(m,stats.average(m,0,WINDOW)),TEXT);
        }

        LinearLayout trend=panel(body);
        trend.addView(label("TENDENZA • 25 VS 25 PRECEDENTI",15,CYAN,true));
        trend.addView(label("Confronta due blocchi distinti di 25 vite. Il confronto si attiva soltanto a 50 vite complete per evitare che poche casualità sembrino apprendimento.",12,MUTED,false));
        if(stats.historyCount()<50){
            line(trend,"Raccolta dati",stats.historyCount()+" / 50 vite",GOLD);
        }else{
            double index=evolutionIndex();
            int indexColor=Math.abs(index)<2?MUTED:index>0?GREEN:RED;
            line(trend,"Tendenza evolutiva",
                (index>0?"+":"")+String.format(Locale.ITALY,"%.0f%%",index),indexColor);
            trend.addView(label("Indice osservativo sintetico: combina più misure reali con pesi uguali. NON è una ricompensa e ZERO non lo vede.",11,MUTED,false));
            int[] trendMetrics={LifeStatistics.DURATION,LifeStatistics.GOOD,LifeStatistics.HARM,
                LifeStatistics.DISKS,LifeStatistics.CELLS,LifeStatistics.NET,
                LifeStatistics.GATE,LifeStatistics.ROOMS,LifeStatistics.RECALL};
            for(int m:trendMetrics){
                double recent=stats.average(m,0,WINDOW);
                double previous=stats.average(m,WINDOW,WINDOW);
                line(trend,LifeStatistics.LABELS[m],delta(m,recent,previous),
                    trendColor(m,recent,previous));
            }
        }

        LinearLayout lifetime=panel(body);
        lifetime.addView(label(stats.lifetimeComplete()?
            "STORICO COMPLETO DI ZERO":"STORICO DISPONIBILE DI ZERO",15,CYAN,true));
        if(stats.lifetimeComplete()){
            lifetime.addView(label("Queste medie usano TUTTE le vite complete dalla nascita di questo salvataggio. Le vite vecchie non vengono più eliminate quando il grafico supera quota 50.",12,MUTED,false));
        }else{
            lifetime.addView(label("Questo ZERO esisteva già prima della telemetria completa: il passato non misurato non può essere ricostruito. Da questo aggiornamento in poi, però, le nuove vite resteranno nelle medie per sempre.",12,GOLD,false));
        }
        line(lifetime,"Vite concluse registrate",String.valueOf(stats.completed()),TEXT);
        line(lifetime,"Vite incluse nello storico",String.valueOf(stats.lifetimeCount()),TEXT);
        if(stats.lifetimeCount()>0){
            int[] lifeMetrics={LifeStatistics.DURATION,LifeStatistics.GOOD,LifeStatistics.HARM,
                LifeStatistics.DISKS,LifeStatistics.CELLS,LifeStatistics.NET,
                LifeStatistics.GATE,LifeStatistics.ROOMS,LifeStatistics.RECALL};
            gap(lifetime,7);
            lifetime.addView(label("MEDIE STORICHE",12,GOLD,true));
            for(int m:lifeMetrics)line(lifetime,LifeStatistics.LABELS[m],
                metric(m,stats.lifetimeAverage(m)),TEXT);
            gap(lifetime,9);
            lifetime.addView(label("RECORD ASSOLUTI",12,GOLD,true));
            for(int m:new int[]{LifeStatistics.DURATION,LifeStatistics.GOOD,
                LifeStatistics.DISKS,LifeStatistics.CELLS,LifeStatistics.NET,
                LifeStatistics.GATE,LifeStatistics.ROOMS,LifeStatistics.RECALL})
                line(lifetime,LifeStatistics.LABELS[m],metric(m,stats.lifetimeRecord(m)),GOLD);
        }

        LinearLayout pace=panel(body);
        pace.addView(label("EFFICIENZA • ULTIME 25 VITE",15,CYAN,true));
        pace.addView(label("Eventi per minuto di tempo simulato: una vita lunga non sembra automaticamente più capace solo perché dura di più.",12,MUTED,false));
        if(seen>0){
            for(int m:new int[]{LifeStatistics.GOOD,LifeStatistics.HARM,LifeStatistics.DISKS}){
                double total=0,seconds=0;
                for(int i=0;i<seen;i++){LifeStatistics.Life l=stats.recent(i);
                    total+=l.value(m);seconds+=l.seconds;}
                line(pace,LifeStatistics.LABELS[m]+"/min",seconds>0?num(total*60/seconds):"—",TEXT);
            }
        }else line(pace,"Confronto","In attesa di vite complete",GOLD);

        LinearLayout graph=panel(body);
        graph.addView(label("ULTIME 50 VITE COMPLETE",15,CYAN,true));
        graph.addView(label("Dettaglio cronologico: vecchie a sinistra, recenti a destra. Lo storico totale continua oltre queste 50.",12,MUTED,false));
        if(stats.historyCount()<2)
            line(graph,"Grafici","Disponibili dopo 2 vite",GOLD);
        else {
            addChart(graph,"Sopravvivenza (secondi simulati)",LifeStatistics.DURATION,CYAN);
            addChart(graph,"Risorse positive raccolte",LifeStatistics.GOOD,GREEN);
            addChart(graph,"Contatti dannosi",LifeStatistics.HARM,RED);
        }

        LinearLayout notes=panel(body);
        notes.addView(label("COME LEGGERE I RISULTATI",15,CYAN,true));
        gap(notes,8);
        notes.addView(label("I punti vengono classificati in base all'energia davvero guadagnata o persa, non a colori riconosciuti in anticipo da ZERO. La fame naturale NON conta come contatto dannoso. Distanza e porta sono massimi raggiunti in una singola vita.",12,MUTED,false));
        gap(notes,7);
        notes.addView(label("Il confronto 25 vs 25 misura una tendenza, non dimostra da solo che una strategia sia stata appresa: ambiente e casualità possono ancora influenzare i risultati. Per questo lo storico completo resta separato dalla capacità attuale.",12,MUTED,false));
        gap(notes,7);
        notes.addView(label("Una vita ancora in corso non viene trattata come fallimento. Le statistiche appartengono al personaggio attivo e vengono salvate insieme ai suoi slot, senza mescolare altri ZERO.",12,MUTED,false));

        TextView back=label("‹ TORNA A ZERO",17,CYAN,true);back.setGravity(Gravity.CENTER);
        back.setPadding(0,dp(23),0,dp(16));back.setOnClickListener(v->finish());
        body.addView(back);setContentView(scroll);
    }

    void addChart(LinearLayout parent,String name,int metric,int color){
        gap(parent,10);parent.addView(label(name,12,TEXT,true));
        TrendChart view=new TrendChart(this,stats,metric,color);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(112));
        lp.topMargin=dp(5);parent.addView(view,lp);
    }

    private static final class TrendChart extends View {
        private final LifeStatistics stats;private final int metric,color;
        private final Paint pen=new Paint(Paint.ANTI_ALIAS_FLAG);
        TrendChart(Context c,LifeStatistics s,int m,int color){super(c);
            stats=s;metric=m;this.color=color;}
        @Override protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            int n=stats.historyCount();if(n<2)return;
            float w=getWidth(),h=getHeight(),left=5,top=10,right=w-6,bottom=h-14;
            pen.setStrokeWidth(1);pen.setColor(0xff355260);
            for(int i=0;i<=3;i++){
                float y=top+(bottom-top)*i/3f;
                canvas.drawLine(left,y,right,y,pen);
            }
            double max=1;
            for(int i=0;i<n;i++)max=Math.max(max,stats.recent(i).value(metric));
            Path points=new Path();
            for(int i=0;i<n;i++){
                float x=left+(right-left)*i/(n-1f);
                float y=bottom-(float)(stats.recent(n-1-i).value(metric)/max)*(bottom-top);
                if(i==0)points.moveTo(x,y);else points.lineTo(x,y);
            }
            pen.setColor(color);pen.setStrokeWidth(3.2f);pen.setStyle(Paint.Style.STROKE);
            canvas.drawPath(points,pen);pen.setStyle(Paint.Style.FILL);
            pen.setTextSize(22);pen.setColor(0xffb8ccd0);
            canvas.drawText("0",left,bottom+12,pen);
            String high=String.format(Locale.ITALY,"%.0f",max);
            canvas.drawText(high,left,top+17,pen);
        }
    }
}
