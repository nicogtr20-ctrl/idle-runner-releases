package com.overmind.zero;

import android.app.Activity;
import android.os.Bundle;
import android.content.*;
import android.graphics.*;
import android.view.*;
import java.util.*;
import java.util.Locale;

/**
 * Player-facing brain skill tree / diagnostics screen. All values are real
 * saved neural weights and simulator counters, not synthetic game levels.
 */
public final class BrainStatsActivity extends Activity {
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);
        setContentView(new Dashboard(this));
    }
    private final class Dashboard extends View {
        final Paint p=new Paint(3);
        final float W=480;
        float H=840,offset=0,downX,downY,lastY;
        boolean dragged=false;
        int tab=0,brainType=0;
        final int bg=0xff08101b,panel=0xff112330,stroke=0xff24414a,
                  cyan=0xff7cf5df,gold=0xfff0c077,muted=0xff91adb6,
                  white=0xffe5f8f5,purple=0xffb2a6ee;
        final float[] movementStart,movementNow,handStart,handNow;
        final float[] choiceStart,choiceNow;
        final DecisionBrain decisions;
        final int motorAttempts,motorAccepted,handAttempts,handAccepted;
        final int pushed,collisions,contacts;
        final float motorInitialFitness,motorNowFitness;
        final float[] predicted=new float[3];
        Dashboard(Context ctx){
            super(ctx);setLayerType(View.LAYER_TYPE_HARDWARE,null);
            SharedPreferences pref=ctx.getSharedPreferences("zero_motorbrain_001",MODE_PRIVATE);
            movementStart=parse(pref.getString("virgin",""),MotorBrain.PARAMS);
            movementNow=parse(pref.getString("best",""),MotorBrain.PARAMS);
            handNow=parse(pref.getString("hand_brain_v04",""),ContactLearning.PARAMS);
            handStart=new float[ContactLearning.PARAMS];
            decisions=new DecisionBrain(90517L);
            decisions.restore(pref.getString("decision_brain_v05",""));
            choiceStart=decisions.initialSnapshot();
            choiceNow=decisions.snapshot();
            motorAttempts=pref.getInt("attempts",0);
            motorAccepted=pref.getInt("accepted",0);
            handAttempts=pref.getInt("hand_attempts_v04",0);
            handAccepted=pref.getInt("hand_successes_v04",0);
            pushed=pref.getInt("objects_displaced_v041",0);
            collisions=pref.getInt("collisions_v041",0);
            String mem=pref.getString("world_knowledge_v03","");
            UnknownWorld.Mind m=new UnknownWorld.Mind();m.deserialize(mem);
            contacts=m.total;
            motorInitialFitness=pref.getFloat("original",0);
            // Only compute when page opens: never run costly simulations in draw().
            motorNowFitness=MotorBrain.fitness(movementNow);
            for(int i=0;i<3;i++)
                predicted[i]=decisions.estimate(new UnknownWorld.Entity(100,0,i,i),0,0,75);
            setFocusable(true);
        }
        float[] parse(String s,int count){
            float[] out=new float[count];
            if(s==null)return out;
            String[] splits=s.split(",");
            if(splits.length!=count)return out;
            try{
                for(int i=0;i<count;i++)out[i]=Float.parseFloat(splits[i]);
            }catch(Exception ex){return new float[count];}
            return out;
        }
        @Override protected void onSizeChanged(int w,int h,int oldw,int oldh){
            super.onSizeChanged(w,h,oldw,oldh);
            H=h/(w/W);
        }
        void rect(Canvas c,float l,float t,float r,float b,int color,int rad){
            p.setColor(color);p.setStyle(Paint.Style.FILL);
            c.drawRoundRect(l,t,r,b,rad,rad,p);
        }
        void label(Canvas c,String value,float x,float y,int size,int color,boolean center){
            p.setColor(color);p.setShader(null);
            p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));
            p.setTextSize(size);p.setTextAlign(center?Paint.Align.CENTER:Paint.Align.LEFT);
            c.drawText(value,x,y,p);
        }
        void bar(Canvas c,float x,float y,float width,float percent,int color){
            rect(c,x,y,x+width,y+8,0xff25444b,4);
            rect(c,x,y,x+Math.max(2,width*Math.max(0,Math.min(1,percent))),y+8,color,4);
        }
        void line(Canvas c,float y){
            p.setColor(0xff1e3845);p.setStrokeWidth(1);
            c.drawLine(26,y,454,y,p);
        }
        String val(float f){return String.format(Locale.US,"%+.3f",f);}
        String score(float f){return String.format(Locale.US,"%+.1f",f);}
        float[] originals(){return brainType==0?choiceStart:brainType==1?movementStart:handStart;}
        float[] current(){return brainType==0?choiceNow:brainType==1?movementNow:handNow;}
        String kind(){return brainType==0?"DECISIONI":brainType==1?"MOVIMENTO":"MANIPOLAZIONE";}
        float drift(float[] a,float[] b){
            float sum=0;for(int i=0;i<b.length;i++)sum+=Math.abs(b[i]-a[i]);
            return sum/Math.max(1,b.length);
        }
        void header(Canvas c){
            rect(c,0,0,W,160,bg,0);
            rect(c,16,17,464,95,0xff12232f,20);
            label(c,"‹",33,55,27,cyan,false);
            label(c,"CERVELLO / ZERO",66,49,20,white,false);
            label(c,"STATISTICHE REALI • SALVATAGGIO LOCALE",66,73,10,muted,false);
            String[] tabs={"ABILITA","PARAMETRI","EVOLUZIONE"};
            for(int k=0;k<3;k++){
                float left=16+k*151;
                rect(c,left,106,left+145,150,k==tab?0xff27504f:0xff152a35,12);
                label(c,tabs[k],left+72,133,12,k==tab?cyan:muted,true);
            }
        }
        @Override protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            if(getWidth()==0)return;
            Canvas c=canvas;
            c.save();c.scale(getWidth()/W,getWidth()/W);
            c.drawColor(bg);
            p.setColor(0xff10252e);p.setStrokeWidth(1);
            for(int x=0;x<=480;x+=48)c.drawLine(x,160,x,H-66,p);
            for(int y=160;y<H-66;y+=48)c.drawLine(0,y,W,y,p);
            c.save();c.clipRect(0,160,W,H-63);
            c.translate(0,-offset);
            if(tab==0)drawSkills(c);
            else if(tab==1)drawParameters(c);
            else drawEvolution(c);
            c.restore();
            header(c);
            rect(c,0,H-62,480,H,bg,0);
            line(c,H-61);
            label(c,"‹ TORNA A ZERO",240,H-24,15,cyan,true);
            c.restore();
        }
        float hero(Canvas c,String title,String value,String caption,float top){
            rect(c,16,top,464,top+113,panel,19);
            label(c,title,32,top+28,12,muted,false);
            label(c,value,32,top+71,31,gold,false);
            label(c,caption,32,top+97,11,cyan,false);
            return top+129;
        }
        float skill(Canvas c,float top,String title,String main,String description,
                    float progress,int color){
            rect(c,17,top,463,top+89,panel,15);
            label(c,title,31,top+25,14,color,false);
            label(c,main,445,top+25,16,white,true);
            bar(c,31,top+39,412,progress,color);
            label(c,description,31,top+71,11,muted,false);
            return top+101;
        }
        void drawSkills(Canvas c){
            float y=177;
            int total=movementNow.length+handNow.length+choiceNow.length;
            int original=movementStart.length+handStart.length+DecisionBrain.PARAMS;
            y=hero(c,"POTENZA NEURALE / PARAMETRI",String.valueOf(total),
                original+" ALLA NASCITA  •  +"+(total-original)+" NUOVI",y);
            y=skill(c,y,"SCELTA DEGLI STIMOLI",
                choiceNow.length+" pesi",decisions.updates+" esperienze  •  "+decisions.decisions+" decisioni",
                Math.min(1f,decisions.updates/500f),cyan);
            y=skill(c,y,"CONTROLLO DEL CORPO",
                movementNow.length+" pesi",motorAttempts+" tentativi  •  "+motorAccepted+" miglioramenti",
                Math.min(1f,motorAttempts/30000f),purple);
            y=skill(c,y,"MANIPOLAZIONE",
                handNow.length+" pesi",pushed+" dischi spostati  •  "+collisions+" urti",
                Math.min(1f,pushed/100f),gold);
            y=skill(c,y,"ESPLORAZIONE",
                ""+decisions.explorations,decisions.abandoned+" tentativi interrotti  •  "+contacts+" contatti",
                Math.min(1f,decisions.explorations/150f),cyan);
            y=skill(c,y,"CRESCITA DEL CERVELLO",
                decisions.hiddenCount()+" neuroni",
                decisions.growthAccepted+" ampliamenti  •  "+decisions.growthAttempts+" tentati",
                (float)(decisions.hiddenCount()-10)/(DecisionBrain.MAX_HIDDEN-10),gold);
            label(c,"I progressi delle barre sono scale grafiche, non livelli di intelligenza.",25,y+18,10,muted,false);
            rect(c,17,y+39,463,y+147,0xff152e36,16);
            label(c,"VALORI DI APPRENDIMENTO",29,y+67,13,cyan,false);
            label(c,"Movimento: "+score(motorInitialFitness)+"  →  "+score(motorNowFitness),30,y+93,14,white,false);
            label(c,"Errore recente delle decisioni: "+String.format(Locale.US,"%.3f",decisions.averageError),
                30,y+119,12,muted,false);
        }
        void drawParameters(Canvas c){
            float y=175;
            rect(c,16,y,464,y+77,panel,16);
            label(c,"RETI NEURALI / PESI VERI",29,y+24,15,cyan,false);
            label(c,"Valore originale  →  peso appreso",29,y+51,12,muted,false);
            y+=91;
            String[] names={"SCELTA","CORPO","MANIP."};
            for(int i=0;i<3;i++){
                float x=17+i*150;
                rect(c,x,y,x+144,y+43,brainType==i?0xff27615b:0xff1b3440,12);
                label(c,names[i],x+72,y+27,12,brainType==i?cyan:muted,true);
            }
            y+=58;
            float[] a=originals(),b=current();
            float movement=drift(a,b);
            rect(c,17,y,463,y+110,panel,15);
            label(c,kind()+"  •  "+b.length+" PESI",32,y+26,16,white,false);
            label(c,"Variazione media assoluta",32,y+51,12,muted,false);
            label(c,String.format(Locale.US,"%.4f",movement),442,y+52,16,gold,true);
            bar(c,32,y+69,411,Math.min(1f,movement/.8f),cyan);
            label(c,"Ogni riga mostra i numeri del cervello memorizzato.",32,y+97,11,muted,false);
            y+=125;
            rect(c,17,y,463,y+34,0xff24454b,9);
            label(c,"PESO",27,y+23,12,white,false);
            label(c,"INIZIALE",133,y+23,11,muted,false);
            label(c,"ATTUALE",258,y+23,11,cyan,false);
            label(c,"DELTA",381,y+23,11,gold,false);
            y+=38;
            for(int i=0;i<b.length;i++){
                int color=i%2==0?0xff132631:0xff172c36;
                rect(c,17,y,463,y+37,color,5);
                label(c,"#"+String.format(Locale.US,"%03d",i+1),27,y+23,11,muted,false);
                label(c,val(a[i]),133,y+23,11,white,false);
                label(c,val(b[i]),258,y+23,11,cyan,false);
                label(c,val(b[i]-a[i]),381,y+23,10,gold,false);
                y+=40;
            }
            label(c,"FINE DEI PARAMETRI  •  SCORRI VERSO L'ALTO",240,y+25,11,muted,true);
        }
        void drawEvolution(Canvas c){
            float y=175;
            y=hero(c,"ARCHITETTURA DEL CERVELLO",decisions.hiddenCount()+" neuroni",
              "10 INIZIALI  →  "+decisions.hiddenCount()+" ATTUALI",y);
            rect(c,17,y,463,y+225,panel,18);
            label(c,"SCHEMA DELLA RETE DI SCELTA",30,y+29,15,cyan,false);
            int n=decisions.hiddenCount();
            // A visual schematic of actual topology, not pseudo-neurons.
            for(int i=0;i<7;i++){
                p.setColor(0xff77d8d2);
                c.drawCircle(78,y+56+i*21,4,p);
            }
            for(int i=0;i<n;i++){
                float pos=y+55+i*155f/Math.max(1,n-1);
                p.setColor(i<10?purple:gold);
                c.drawCircle(228,pos,i<10?3.2f:4.8f,p);
            }
            p.setColor(gold);c.drawCircle(399,y+134,10,p);
            p.setStrokeWidth(.5f);p.setColor(0x304cead4);
            for(int i=0;i<7;i++)for(int j=0;j<n;j++)
                c.drawLine(82,y+56+i*21,225,y+55+j*155f/Math.max(1,n-1),p);
            for(int j=0;j<n;j++)
                c.drawLine(232,y+55+j*155f/Math.max(1,n-1),389,y+134,p);
            label(c,"7 ingressi",78,y+215,10,muted,true);
            label(c,n+" nascosti",228,y+215,10,muted,true);
            label(c,"1 uscita",400,y+215,10,muted,true);
            y+=240;
            y=skill(c,y,"NUOVI PARAMETRI","+"+decisions.totalParametersEverAdded(),
                "9 connessioni per ogni nuovo neurone",
                (float)(n-10)/(DecisionBrain.MAX_HIDDEN-10),gold);
            y=skill(c,y,"CANDIDATI DI CRESCITA",""+decisions.growthAttempts,
                decisions.growthAccepted+" accettati dopo test su esperienze diverse",
                decisions.growthAttempts==0?0:(float)decisions.growthAccepted/decisions.growthAttempts,cyan);
            y=skill(c,y,"ERRORE DI PREVISIONE",
                String.format(Locale.US,"%.3f",decisions.averageError),
                "Errore recente: più basso è generalmente meglio",
                1-Math.min(1,decisions.averageError/2),purple);
            rect(c,17,y,463,y+144,panel,16);
            label(c,"REGOLA DI EVOLUZIONE",29,y+29,14,gold,false);
            label(c,"Quando l'errore resta alto, ZERO prova",29,y+56,12,white,false);
            label(c,"un nuovo neurone mantenendo i vecchi pesi.",29,y+77,12,white,false);
            label(c,"Lo conserva soltanto se migliora su dati",29,y+98,12,white,false);
            label(c,"non usati per addestrare il candidato.",29,y+119,12,white,false);
        }
        float contentMax(){
            if(tab==1)return 175+91+58+125+38+current().length*40+52;
            if(tab==2)return 177+129+240+101*3+144+36;
            return 177+129+101*5+39+147+60;
        }
        void limitScroll(){
            offset=Math.max(0,Math.min(offset,Math.max(0,contentMax()-(H-70))));
        }
        @Override public boolean onTouchEvent(android.view.MotionEvent ev){
            float sx=ev.getX()/(getWidth()/W),sy=ev.getY()/(getWidth()/W);
            switch(ev.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    downX=sx;downY=sy;lastY=sy;dragged=false;return true;
                case MotionEvent.ACTION_MOVE:
                    if(Math.abs(sy-downY)>7)dragged=true;
                    if(dragged&&sy>160&&sy<H-62){
                        offset-=sy-lastY;limitScroll();invalidate();
                    }
                    lastY=sy;return true;
                case MotionEvent.ACTION_UP:
                    if(dragged)return true;
                    if(sy>H-66||(sy<96&&sx<122)){finish();return true;}
                    if(sy>=105&&sy<=155){
                        tab=Math.max(0,Math.min(2,(int)((sx-16)/151)));
                        offset=0;invalidate();return true;
                    }
                    if(tab==1&&sy>=175+91-offset&&sy<=175+91+51-offset){
                        brainType=Math.max(0,Math.min(2,(int)((sx-17)/150)));
                        offset=0;invalidate();return true;
                    }
                    return true;
            }
            return true;
        }
    }
}
