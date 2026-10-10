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
    double baselineWithoutLast(int metric){
        int n=Math.min(10,stats.historyCount()-1);
        if(n<=0)return Double.NaN;
        double total=0;
        for(int j=1;j<=n;j++)total+=stats.recent(j).value(metric);
        return total/n;
    }
    boolean lowerIsBetter(int metric){
        return metric==LifeStatistics.HARM||metric==LifeStatistics.DAMAGE;
    }
    String delta(int metric,double difference){
        String sign=difference>0?"+":difference<0?"−":"±";
        double amount=Math.abs(difference);
        return sign+(metric==LifeStatistics.DURATION?
            String.format(Locale.ITALY,"%.0f s",amount):
            String.format(Locale.ITALY,"%.1f",amount)+(metric==LifeStatistics.NET||
                metric==LifeStatistics.GATE?" U":""));
    }
    void showStats(){
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        LinearLayout body=col();body.setPadding(dp(16),dp(26),dp(16),dp(30));
        scroll.addView(body);
        body.addView(label("ZERO / STATISTICHE",24,TEXT,true));
        gap(body,5);
        body.addView(label("Misure del mondo reale simulato • non alterano il cervello",12,MUTED,false));
        LifeStatistics.Life live=stats.current();
        LinearLayout now=panel(body);
        now.addView(label("VITA IN CORSO",15,CYAN,true));
        gap(now,8);
        now.addView(label(LifeStatistics.duration(live.seconds),34,TEXT,true));
        now.addView(label("Tempo SIMULATO: a x5 o x20 cresce 5 o 20 volte più velocemente. In pausa resta fermo.",12,MUTED,false));
        if(live.partial){
            gap(now,8);
            now.addView(label("Questa vita era già in corso prima dell'aggiornamento. I dati incompleti non entreranno nelle medie: attendi la prossima morte.",12,GOLD,false));
        }
        gap(now,8);
        int[] essential={LifeStatistics.GOOD,LifeStatistics.HARM,LifeStatistics.GAIN,
            LifeStatistics.DAMAGE,LifeStatistics.DISKS,LifeStatistics.CELLS,
            LifeStatistics.NET,LifeStatistics.GATE,LifeStatistics.ROOMS,LifeStatistics.RECALL};
        for(int m:essential){
            line(now,LifeStatistics.LABELS[m],metric(m,live.value(m)),TEXT);
        }
        LinearLayout averages=panel(body);
        averages.addView(label("MEDIA DELLE ULTIME 10 VITE",15,CYAN,true));
        int seen=stats.sampleCount(10);
        averages.addView(label(seen==0?"Nessuna vita completa misurata ancora.":
            "Basata su "+seen+" vite completate (non sulla vita in corso).",12,MUTED,false));
        if(seen>0){
            int[] chosen={LifeStatistics.DURATION,LifeStatistics.GOOD,LifeStatistics.HARM,
                LifeStatistics.GAIN,LifeStatistics.DAMAGE,LifeStatistics.DISKS,
                LifeStatistics.CELLS,LifeStatistics.NET,LifeStatistics.GATE,LifeStatistics.ROOMS};
            for(int m:chosen)line(averages,LifeStatistics.LABELS[m],
                metric(m,stats.average(m,10)),TEXT);
        }
        LinearLayout trend=panel(body);
        trend.addView(label("ULTIMA VITA VS PRECEDENTI",15,CYAN,true));
        trend.addView(label("Differenza rispetto alla media delle 10 vite precedenti (oppure alle disponibili). Verde significa meglio per quella metrica; rosso peggio.",12,MUTED,false));
        if(stats.historyCount()<2)line(trend,"Confronto","Servono almeno 2 vite",GOLD);
        else{
            LifeStatistics.Life last=stats.recent(0);
            for(int m:new int[]{LifeStatistics.DURATION,LifeStatistics.GOOD,
                LifeStatistics.HARM,LifeStatistics.DISKS,LifeStatistics.NET,
                LifeStatistics.GATE,LifeStatistics.CELLS}){
                double diff=last.value(m)-baselineWithoutLast(m);
                int color=diff==0?MUTED:(lowerIsBetter(m)?diff<0:diff>0)?GREEN:RED;
                line(trend,LifeStatistics.LABELS[m],delta(m,diff),color);
            }
        }
        LinearLayout pace=panel(body);
        pace.addView(label("EFFICIENZA PER MINUTO DI GIOCO",15,CYAN,true));
        pace.addView(label("Normalizzata per tempo simulato, così una vita lunga non sembra automaticamente più capace.",12,MUTED,false));
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
        graph.addView(label("Ordinamento cronologico: vecchie a sinistra, recenti a destra.",12,MUTED,false));
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
        notes.addView(label("Una vita ancora in corso non viene confrontata come un fallimento. La prima vita parziale dopo l'aggiornamento è esclusa. Le statistiche appartengono al personaggio attivo e vengono salvate con i suoi 12 slot, senza mescolare altri ZERO.",12,MUTED,false));
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
