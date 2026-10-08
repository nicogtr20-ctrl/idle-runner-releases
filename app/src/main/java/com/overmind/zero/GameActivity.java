package com.overmind.zero;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.view.*;
import java.util.*;
import java.util.Locale;

/**
 * OVERMIND ZERO / EXPERIMENT 001
 * An invisible observer watches a randomly initialized motor brain learn
 * to move a deformable ferrofluid organism through trial and error.
 */
public final class GameActivity extends Activity {
    private Lab lab;
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setFlags(1024,1024);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
        lab=new Lab(this);setContentView(lab);
    }
    @Override protected void onPause(){super.onPause();if(lab!=null)lab.suspend();}
    @Override protected void onResume(){super.onResume();if(lab!=null)lab.resume();}
    private final class Lab extends View {
        final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        final Path path=new Path();
        final SharedPreferences prefs;
        final int TEAL=0xff80f7e2,GOLD=0xfff7c67a,MUTED=0xff95b1bb;
        final float W=480f;
        MotorBrain.Trainer trainer;
        UnknownWorld.Mind mind=new UnknownWorld.Mind();
        UpdateClient updater;
        String installedVersion="0.3.7";
        String updaterStatus="";
        float updaterStatusUntil=0;
        ArrayList<UnknownWorld.Entity> environment=new ArrayList<>();
        UnknownWorld.Entity chosenObject;
        float reserve=57,goalTimer=0,worldTime=0;
        int contactsThisLife=0;
        String lastEvent="ZERO NON CONOSCE QUESTO MONDO";
        float eventTime=7;
        MotorBrain.Body creature=new MotorBrain.Body();
        float[] virgin;
        volatile boolean learning=true,enabled=true;
        Thread worker;
        boolean showHelp=true,resetArmed=false,simulationRunning=true,showBenchmark=false;
        long lastFrame=0,lastSave=0;
        float H=850,clock=0,phase=0,uiTime=0,heading=0,trailClock=0;
        float[] trailX=new float[46],trailY=new float[46];
        int trailCount=0,speed=1,episode=0;
        volatile float benchStart=Float.NaN,benchNow=Float.NaN;
        boolean benchWorking=false;
        float onScreenX=240,onScreenY=420;
        float resetNotice=0,helpNotice=0;
        final int[] darks={0xff060a13,0xff0d1823,0xff14262e,0xff06121c};

        Lab(Context c){
            super(c);setLayerType(View.LAYER_TYPE_HARDWARE,null);setFocusable(true);
            prefs=c.getSharedPreferences("zero_motorbrain_001",Context.MODE_PRIVATE);
            load();
            try {
                installedVersion=GameActivity.this.getPackageManager().getPackageInfo(
                    GameActivity.this.getPackageName(),0).versionName;
            }catch(Exception ignored){}
            updater=new UpdateClient(GameActivity.this,(status)->{
                updaterStatus=status;
                updaterStatusUntil=uiTime+8.0f;
                invalidate();
            });
        }
        void load(){
            String initial=prefs.getString("virgin","");
            virgin=decode(initial);
            if(virgin==null){virgin=MotorBrain.virgin(new Random(System.nanoTime()));}
            float[] trained=decode(prefs.getString("best",""));
            if(trained==null)trained=MotorBrain.copy(virgin);
            trainer=new MotorBrain.Trainer(trained,21947L+prefs.getInt("attempts",0));
            trainer.attempts=prefs.getInt("attempts",0);
            trainer.accepted=prefs.getInt("accepted",0);
            trainer.initialFitness=prefs.getFloat("original",MotorBrain.fitness(virgin));
            mind.deserialize(prefs.getString("world_knowledge_v03",""));
            reserve=prefs.getFloat("zero_energy_v03",57f);
            speed=1;learning=true;simulationRunning=true;
            resetEpisode();
            save();
        }
        float[] decode(String s){
            if(s==null||s.isEmpty())return null;
            String[] chunks=s.split(",");
            if(chunks.length!=MotorBrain.PARAMS)return null;
            float[] out=new float[chunks.length];
            try{
                for(int i=0;i<out.length;i++){
                    out[i]=Float.parseFloat(chunks[i]);
                    if(!Float.isFinite(out[i]))return null;
                }
            }catch(Exception e){return null;}
            return out;
        }
        String encode(float[] values){
            StringBuilder b=new StringBuilder();
            for(int i=0;i<values.length;i++){
                if(i>0)b.append(',');
                b.append(values[i]);
            }
            return b.toString();
        }
        void save(){
            if(trainer==null)return;
            prefs.edit().putString("virgin",encode(virgin))
              .putString("best",encode(trainer.best))
              .putInt("attempts",trainer.attempts)
              .putInt("accepted",trainer.accepted)
              .putFloat("original",trainer.initialFitness)
              .putString("world_knowledge_v03",mind.serialize())
              .putFloat("zero_energy_v03",reserve)
              .apply();
        }
        void resume(){
            enabled=true;lastFrame=0;postInvalidateOnAnimation();
            if(worker!=null&&worker.isAlive())return;
            worker=new Thread(()->{
                int saved=0;
                while(enabled){
                    if(learning){
                        try{
                            if(trainer.attempt()){
                                if(trainer.accepted%8==0)save();
                            }
                            if(++saved%140==0)save();
                            Thread.sleep(12);
                        }catch(Exception e){
                            learning=false;
                        }
                    }else try{Thread.sleep(80);}catch(InterruptedException e){break;}
                }
            },"zero-neuroevolution");
            worker.setDaemon(true);worker.start();
        }
        void suspend(){
            // Stop the training worker while the app is backgrounded without
            // forgetting whether learning was enabled by the player.
            enabled=false;
            save();
            lastFrame=0;
        }
        void resetEpisode(){
            creature.reset();episode++;phase=0;trailCount=0;clock=0;
            environment=UnknownWorld.newHabitat(episode);
            chosenObject=null;goalTimer=0;contactsThisLife=0;worldTime=0;
            reserve=Math.max(42,Math.min(92,reserve));
            heading=episode*.31f;
        }
        void updateHabitat(float dt){
            worldTime+=dt;goalTimer-=dt;
            reserve=Math.max(0,reserve-.14f*dt);
            for(UnknownWorld.Entity object:environment)object.tick(dt);
            if(chosenObject==null||!chosenObject.ready()||goalTimer<=0){
                chosenObject=mind.choose(environment,creature.cx,creature.cy,reserve);
                goalTimer=2.8f;
            }
            if(chosenObject!=null){
                heading=(float)Math.atan2(chosenObject.y-creature.cy,chosenObject.x-creature.cx);
            }else{
                heading+=dt*.7f;
            }
            for(UnknownWorld.Entity object:environment){
                if(!object.ready())continue;
                float dx=object.x-creature.cx,dy=object.y-creature.cy;
                float distance=(float)Math.hypot(dx,dy);
                float nearest=distance;
                for(int j=0;j<MotorBrain.N;j++){
                    float d=(float)Math.hypot(object.x-creature.x[j],object.y-creature.y[j]);
                    if(d<nearest)nearest=d;
                }
                if(nearest<18+object.radius){
                    float nx=dx/Math.max(1,distance),ny=dy/Math.max(1,distance);
                    float delta=object.touch(nx,ny);
                    reserve=Math.max(0,Math.min(100,reserve+delta));
                    mind.observe(object.signature,delta,object.displacementEffect());
                    contactsThisLife++;chosenObject=null;goalTimer=0;
                    lastEvent="STIMOLO "+(object.signature+1)+"   ENERGIA "+(delta>=0?"+":"")+(int)delta;
                    eventTime=4;
                    if(delta<0) {
                        creature.cvx*=.6f;creature.cvy*=.6f;
                    }
                    save();
                }
            }
            eventTime=Math.max(0,eventTime-dt);
            if(reserve<=0){
                reserve=45;lastEvent="CORPO ESAUSTO • MEMORIA CONSERVATA";eventTime=6;
                resetEpisode();save();
            }
        }
        @Override protected void onSizeChanged(int w,int h,int ow,int oh){
            super.onSizeChanged(w,h,ow,oh);
            H=h/(w/W);
        }
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            if(getWidth()==0)return;
            long now=System.nanoTime();
            float dt=lastFrame==0?0:Math.min(.038f,(now-lastFrame)*.000000001f);
            lastFrame=now;
            uiTime+=dt;
            if(resetNotice>0)resetNotice-=dt;
            if(simulationRunning){
                for(int i=0;i<speed;i++){
                    float slice=Math.min(dt,.035f);
                    updateHabitat(slice);
                    creature.step(trainer.best,(float)Math.cos(heading),(float)Math.sin(heading),slice);
                    phase+=slice;
                    if(phase>=26f||Math.hypot(creature.cx,creature.cy)>520){
                        resetEpisode();break;
                    }
                    trailClock+=slice;
                    if(trailClock>.14f){
                        trailClock=0;
                        if(trailCount<trailX.length)trailCount++;
                        for(int n=trailCount-1;n>0;n--){trailX[n]=trailX[n-1];trailY[n]=trailY[n-1];}
                        trailX[0]=creature.cx;trailY[0]=creature.cy;
                    }
                }
            }
            c.save();float scale=getWidth()/W;c.scale(scale,scale);
            renderWorld(c);
            renderBody(c);
            renderHud(c);
            c.restore();
            postInvalidateOnAnimation();
        }
        void rounded(Canvas c,float l,float t,float r,float b,int color,float rad){
            paint.setShader(null);paint.setColor(color);paint.setStyle(Paint.Style.FILL);
            c.drawRoundRect(l,t,r,b,rad,rad,paint);
        }
        void text(Canvas c,String s,float x,float y,int size,int color,boolean center){
            paint.setShader(null);paint.setColor(color);
            paint.setTextAlign(center?Paint.Align.CENTER:Paint.Align.LEFT);
            paint.setTextSize(size);
            paint.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));
            c.drawText(s,x,y,paint);
        }
        void ring(Canvas c,float x,float y,float radius,int color,float thickness){
            paint.setShader(null);paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(thickness);
            c.drawCircle(x,y,radius,paint);paint.setStyle(Paint.Style.FILL);
        }
        void renderWorld(Canvas c){
            paint.setShader(new LinearGradient(0,0,0,H,0xff060812,0xff17252e,Shader.TileMode.CLAMP));
            c.drawRect(0,0,W,H,paint);paint.setShader(null);
            float middle=(H-172+180)/2;
            onScreenY=middle;
            onScreenX=W*.5f;
            c.save();
            c.clipRect(0,149,W,H-169);
            c.translate(onScreenX-creature.cx*2.3f,onScreenY-creature.cy*2.3f);
            c.scale(2.3f,2.3f);
            // Simulated surface is an abstract habitat, not a rendered animation.
            float minX=(creature.cx-135),maxX=(creature.cx+135);
            float minY=creature.cy-(H-330)/4.6f,maxY=creature.cy+(H-330)/4.6f;
            paint.setColor(0x1436bcaf);paint.setStrokeWidth(.6f);
            for(int k=(int)Math.floor(minX/34)-1;k<(int)Math.ceil(maxX/34)+1;k++)
                c.drawLine(k*34,minY,k*34,maxY,paint);
            for(int k=(int)Math.floor(minY/34)-1;k<(int)Math.ceil(maxY/34)+1;k++)
                c.drawLine(minX,k*34,maxX,k*34,paint);
            for(int i=-4;i<=4;i++)for(int j=-5;j<=5;j++){
                float xx=i*53,yy=j*53;
                int hash=(i*1337+j*1987);
                float rr=2+Math.abs(hash)%4;
                paint.setColor(Math.abs(hash)%3==0?0x39307879:0x272e5d68);
                c.drawCircle(xx+((i*j)%17),yy,rr,paint);
            }
            int[] colors={0xff64d8ca,0xffcc7da4,0xffd2b889};
            for(UnknownWorld.Entity object:environment){
                float x=object.x,y=object.y;
                paint.setShader(new RadialGradient(x,y,35,new int[]{(object.ready()?0x2248dfc4:0x113d7377),0x003d7377},null,Shader.TileMode.CLAMP));
                c.drawCircle(x,y,35,paint);paint.setShader(null);
                paint.setColor(0xff151c26);
                c.drawCircle(x,y,object.radius,paint);
                paint.setColor(colors[object.signature]);
                c.drawCircle(x,y,object.radius*.63f,paint);
                paint.setColor(0x66ffffff);
                c.drawCircle(x-object.radius*.25f,y-object.radius*.22f,object.radius*.17f,paint);
                if(object==chosenObject){
                    ring(c,x,y,object.radius+6+2*(float)Math.sin(uiTime*4),0x8885fae0,1f);
                }
                if(!object.ready())ring(c,x,y,object.radius+3,0x44455a64,1f);
            }
            for(int i=trailCount-1;i>=0;i--){
                int alpha=(int)(85*(1-(float)i/trailX.length));
                if(alpha<=0)continue;
                paint.setColor((alpha<<24)|0x20c6b0);
                c.drawCircle(trailX[i],trailY[i],1.8f,paint);
            }
            c.restore();
            // Halo around the real-time organism
            paint.setShader(new RadialGradient(onScreenX,onScreenY,131,new int[]{0x153de5c7,0x0a6ae6c7,0x002bd9b5},null,Shader.TileMode.CLAMP));
            c.drawCircle(onScreenX,onScreenY,131,paint);paint.setShader(null);
            text(c,"MONDO SCONOSCIUTO • STIMOLO",24,H-189,10,0xff718f99,false);
        }
        void renderBody(Canvas c){
            c.save();
            c.translate(onScreenX-creature.cx*2.3f,onScreenY-creature.cy*2.3f);
            c.scale(2.3f,2.3f);
            // Catmull-like quadratic smoothing through the simulation's actual
            // perimeter masses; NOT a fixed spider silhouette.
            float[] xx=creature.x, yy=creature.y;
            path.reset();
            float a0=(xx[MotorBrain.N-1]+xx[0])*.5f;
            float b0=(yy[MotorBrain.N-1]+yy[0])*.5f;
            path.moveTo(a0,b0);
            for(int i=0;i<MotorBrain.N;i++){
                int j=(i+1)%MotorBrain.N;
                path.quadTo(xx[i],yy[i],(xx[i]+xx[j])*.5f,(yy[i]+yy[j])*.5f);
            }
            path.close();
            paint.setShader(null);paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(13,0,4,0x7b000000);
            paint.setColor(0xff000208);c.drawPath(path,paint);paint.clearShadowLayer();
            paint.setShader(new RadialGradient(creature.cx-9,creature.cy-15,52,
                new int[]{0xff50656b,0xff172a31,0xff030509,0xff000207},
                new float[]{0f,.34f,.74f,1f},Shader.TileMode.CLAMP));
            c.drawPath(path,paint);paint.setShader(null);
            paint.setColor(0xaa55a49b);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(.75f);
            c.drawPath(path,paint);paint.setStyle(Paint.Style.FILL);
            // Metallic ferrofluid glints react to local strain and real actuation.
            for(int i=0;i<MotorBrain.N;i++){
                float dx=xx[i]-creature.cx,dy=yy[i]-creature.cy;
                float r=(float)Math.hypot(dx,dy);
                float a=creature.act[i];
                float glowRadius=.8f+Math.abs(a)*1.4f;
                if(i%2==0){
                    paint.setColor(0x8867f6d5);
                    c.drawCircle(xx[i]-dx*.11f,yy[i]-dy*.11f,glowRadius,paint);
                }
                if(r>33){
                    paint.setColor(0x4c8df5e6);c.drawCircle(xx[i],yy[i],1.4f,paint);
                }
            }
            paint.setColor(0x889de5da);
            c.drawOval(creature.cx-13,creature.cy-17,creature.cx+5,creature.cy-12,paint);
            paint.setColor(0xff030509);
            c.drawCircle(creature.cx,creature.cy,5,paint);
            paint.setColor(0xff9bffee);c.drawCircle(creature.cx-1,creature.cy-2,2.3f,paint);
            c.restore();
            text(c,"ZERO",onScreenX,onScreenY-104,14,0xffd7fff5,true);
        }
        void renderHud(Canvas c){
            rounded(c,12,12,468,145,0xeb0a1420,20);
            rounded(c,27,28,31,55,TEAL,2);
            text(c,"O V E R M I N D",43,39,14,TEAL,false);
            text(c,"ZERO / ESPERIMENTO 002",26,72,21,0xffe9faf6,false);
            rounded(c,339,104,459,138,0xff20444a,11);
            text(c,"AGGIORNA",399,127,12,TEAL,true);
            text(c,"NESSUNA CONOSCENZA DEGLI OGGETTI",27,99,12,MUTED,false);
            text(c,"VERSIONE "+installedVersion+"  •  OTA",27,120,10,0xff708d9d,false);
            text(c,"TENTATIVI",377,34,11,MUTED,false);
            text(c,String.valueOf(trainer.attempts),380,71,22,GOLD,false);
            rounded(c,13,158,467,232,0xca0e1a25,17);
            text(c,"PRESTAZIONE MISURATA",27,182,12,0xffb5d7d6,false);
            text(c,format(trainer.initialFitness)+" → "+format(trainer.bestFitness),27,210,20,0xffeffff8,false);
            text(c,"unità / episodio",283,212,11,MUTED,false);
            float y=H-168;
            rounded(c,12,y,468,H-12,0xe00d1923,21);
            text(c,"APPRENDIMENTO DA ZERO",25,y+25,13,TEAL,false);
            text(c,"ZERO scopre gli effetti toccando gli oggetti.",25,y+45,12,0xffacc5ca,false);
            button(c,19,y+61,153,H-26,learning?"FERMA":"ADDESTRA",TEAL);
            button(c,165,y+61,314,H-26,"TEMPO x"+speed,0xffbfdaec);
            button(c,326,y+61,462,H-26,"CONFRONTA",GOLD);
            text(c,simulationRunning?"ESPLORAZIONE AUTONOMA • CONTATTI "+mind.total:"SIMULAZIONE IN PAUSA",24,255,11,MUTED,false);
            rounded(c,25,276,456,287,0xff273746,5);
            rounded(c,25,276,25+431*reserve/100f,287,reserve>30?TEAL:0xffdf7591,5);
            text(c,"ENERGIA "+(int)reserve+"/100",25,307,12,0xffe0f7ed,false);
            text(c,"EFFETTI SCOPERTI "+mind.discoveries+"/3",260,307,11,GOLD,false);
            if(eventTime>0)text(c,lastEvent,25,329,11,0xffc4e5dc,false);
            if(uiTime<updaterStatusUntil&&updaterStatus.length()>0){
                rounded(c,13,338,467,387,0xef1f3944,13);
                text(c,updaterStatus.length()>52?updaterStatus.substring(0,52):updaterStatus,240,368,11,0xffd5fff4,true);
            }
            rounded(c,402,238,464,269,0xff233944,9);
            text(c,simulationRunning?"Ⅱ":"▶",433,259,18,0xffe0f7f3,true);
            if(showBenchmark){
                float top=H*.37f;
                rounded(c,26,top,454,top+198,0xf20e222e,22);
                text(c,"TEST SU DIREZIONI NUOVE",240,top+36,17,TEAL,true);
                text(c,"ZERO VERGINE   "+format(benchStart),240,top+79,17,MUTED,true);
                text(c,"ZERO ADDESTRATA   "+format(benchNow),240,top+117,17,0xfff6e2b3,true);
                text(c,benchWorking?"CONFRONTO IN CORSO":Float.isFinite(benchNow)?(benchNow>benchStart?"MIGLIORAMENTO MISURATO":"NESSUN MIGLIORAMENTO DIMOSTRATO"):"IN ATTESA",
                    240,top+157,12,benchNow>benchStart?TEAL:GOLD,true);
                text(c,"TOCCA PER CHIUDERE",240,top+185,11,MUTED,true);
            }
            if(resetNotice>0)text(c,"TOCCA DI NUOVO IL TITOLO PER AZZERARE LA MEMORIA",240,289,11,GOLD,true);
            if(showHelp&&mind.total<3)
                text(c,"Non sa quali oggetti aiutano o danneggiano.",240,355,12,0xffb9cfd2,true);
        }
        String format(float x){return String.format(Locale.US,"%+.1f",x);}
        void button(Canvas c,float l,float t,float r,float b,String caption,int color){
            rounded(c,l,t,r,b,0xff203640,13);
            paint.setColor(color);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.3f);
            c.drawRoundRect(l,t,r,b,13,13,paint);paint.setStyle(Paint.Style.FILL);
            text(c,caption,(l+r)*.5f,(t+b)*.5f+5,13,color,true);
        }
        void compare(){
            if(benchWorking)return;
            benchWorking=true;showBenchmark=true;
            float[] fresh=MotorBrain.copy(virgin),learned=MotorBrain.copy(trainer.best);
            new Thread(()->{
                float a=MotorBrain.benchmark(fresh),b=MotorBrain.benchmark(learned);
                post(()->{benchStart=a;benchNow=b;benchWorking=false;invalidate();});
            },"zero-benchmark").start();
        }
        @Override public boolean onTouchEvent(MotionEvent ev){
            if(ev.getActionMasked()!=MotionEvent.ACTION_UP)return true;
            float scale=getWidth()/W;
            float x=ev.getX()/scale,y=ev.getY()/scale;
            if(showBenchmark){showBenchmark=false;return true;}
            if(y>=H-108){
                if(x<157){learning=!learning;}
                else if(x<319){speed=speed==1?5:speed==5?20:1;}
                else compare();
                return true;
            }
            if(y>=104&&y<145&&x>=328){
                updater.checkAndInstall();
                return true;
            }
            if(y<137&&x<310){
                if(resetArmed&&resetNotice>0){
                    learning=false;
                    prefs.edit().clear().apply();
                    load();resetArmed=false;resetNotice=0;resume();
                }else{resetArmed=true;resetNotice=2.4f;}
                return true;
            }
            if(y>234&&y<280&&x>398){
                simulationRunning=!simulationRunning;lastFrame=0;return true;
            }
            showHelp=false;
            return true;
        }
    }
}
