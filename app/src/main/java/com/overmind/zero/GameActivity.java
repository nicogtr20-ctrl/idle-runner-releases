package com.overmind.zero;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.view.*;
import java.util.*;
import java.util.Locale;

public final class GameActivity extends Activity {
  private Habitat habitat;
  @Override public void onCreate(Bundle b) {
    super.onCreate(b);
    getWindow().setFlags(1024,1024);
    getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
    habitat=new Habitat(this);setContentView(habitat);
  }
  @Override protected void onPause(){super.onPause();if(habitat!=null){habitat.persist();habitat.running=false;}}
  @Override protected void onResume(){super.onResume();if(habitat!=null){habitat.running=true;habitat.lastFrame=0;habitat.postInvalidateOnAnimation();}}
  @Override public void onBackPressed(){if(habitat!=null){habitat.paused=!habitat.paused;habitat.persist();}}

  private static final class Spot {
    float x,y,amount,delay;int type;boolean examined=false,hit=false;
    Spot(float x,float y,int t,float a){this.x=x;this.y=y;type=t;amount=a;}
  }
  private final class Habitat extends View {
    static final float VW=480f, WORLD=2800f, PI=3.14159265f;
    static final int ENERGY=0, ARTIFACT=1, HAZARD=2, NEST=3;
    final Paint p=new Paint(3);
    final Path path=new Path();
    final ArrayList<Spot> spots=new ArrayList<>();
    final Random rnd=new Random(7173);
    final SharedPreferences save;
    final float[][] q=new float[12][4];
    final boolean[] visited=new boolean[28*28];
    final float[] rates={1f,3f,6f};
    float vh=900,camX=1150,camY=1080,zx=1400,zy=1400,vx,vy,heading=0;
    float energy=75,mass=0,agentExp=0,lifeTime=0,thinking=0,goalX=1600,goalY=1500;
    float targetLife=0,storedReward=0,glow=0,particlesTimer=0,stagePulse=0,focusGlow=0;
    float notificationTime=4,viewHelp=8,saveClock=0;
    int xp=0,stepNum=0,stage=0,task=2,prevState=-1,prevAction=2,mode=0,speedIndex=0;
    int discoveries=0,nests=0,collected=0,researched=0,hits=0;
    boolean alive=true,paused=false,running=true,follow=true,dragged=false;
    long lastFrame=0;
    String notification="ZERO SI E RISVEGLIATA",observation="";
    int CYAN=0xff5cead7, ICE=0xffbaffee, PINK=0xfff07898, GOLD=0xffffc879;
    float downX,downY,lastTouchX,lastTouchY,dragDistance=0;
    final String[] moods={"RACCOGLIE ENERGIA","STUDIA UN SEGNALE","ESPLORA L'IGNOTO","CERCA DI ESPANDERSI"};
    Habitat(Context c){
      super(c);setLayerType(View.LAYER_TYPE_HARDWARE,null);
      save=c.getSharedPreferences("overmind_save_v1",Context.MODE_PRIVATE);
      createWorld();restore();setFocusable(true);
    }
    void createWorld(){
      spots.clear();
      spots.add(new Spot(1480,1405,ENERGY,130));
      spots.add(new Spot(1250,1530,ENERGY,130));
      spots.add(new Spot(1670,1290,ENERGY,130));
      spots.add(new Spot(1540,1730,ARTIFACT,1));
      spots.add(new Spot(1210,1190,ARTIFACT,1));
      for(int i=0;i<78;i++){
        float x=115+rnd.nextFloat()*(WORLD-230),y=115+rnd.nextFloat()*(WORLD-230);
        spots.add(new Spot(x,y,ENERGY,50+rnd.nextInt(85)));
      }
      for(int i=0;i<35;i++){
        float x=115+rnd.nextFloat()*(WORLD-230),y=115+rnd.nextFloat()*(WORLD-230);
        spots.add(new Spot(x,y,ARTIFACT,1));
      }
      for(int i=0;i<31;i++){
        float x=150+rnd.nextFloat()*(WORLD-300),y=150+rnd.nextFloat()*(WORLD-300);
        if(distance(x,y,1400,1400)<210){i--;continue;}
        spots.add(new Spot(x,y,HAZARD,1));
      }
    }
    void restore(){
      xp=save.getInt("xp",0);agentExp=save.getFloat("exp",0);
      energy=save.getFloat("energy",75);mass=save.getFloat("mass",0);
      zx=save.getFloat("zx",1400);zy=save.getFloat("zy",1400);
      lifeTime=save.getFloat("lifetime",0);stepNum=save.getInt("steps",0);
      collected=save.getInt("collected",0);researched=save.getInt("researched",0);
      discoveries=save.getInt("discoveries",0);hits=save.getInt("hits",0);
      stage=mass>=92?3:mass>=45?2:mass>=17?1:0;
      String explored=save.getString("map","");
      for(int i=0;i<visited.length&&i<explored.length();i++)visited[i]=explored.charAt(i)=='1';
      String learned=save.getString("brain","");
      if(!learned.isEmpty()){
        String[] data=learned.split(",");
        for(int i=0;i<Math.min(data.length,48);i++){
          try{q[i/4][i%4]=Float.parseFloat(data[i]);}catch(Exception ignored){}
        }
      }
      String world=save.getString("world","");
      if(!world.isEmpty()){
        String[] parts=world.split(";");
        for(int i=0;i<Math.min(parts.length,spots.size());i++){
          try{
            String[] a=parts[i].split(",");
            spots.get(i).amount=Float.parseFloat(a[0]);
            spots.get(i).examined=a.length>1&&a[1].equals("1");
            spots.get(i).hit=a.length>2&&a[2].equals("1");
          }catch(Exception ignored){}
        }
      }
      String colonies=save.getString("nests","");
      if(!colonies.isEmpty()){
        for(String s:colonies.split(";")) {
          try{String[] a=s.split(",");spots.add(new Spot(Float.parseFloat(a[0]),Float.parseFloat(a[1]),NEST,1));nests++;}catch(Exception ignored){}
        }
      }
      alive=energy>0;
      camX=clamp(zx-VW*.5f,0,WORLD-VW);
      camY=clamp(zy-450,0,WORLD-vh);
      chooseGoal(true);
    }
    void persist(){
      SharedPreferences.Editor e=save.edit();
      e.putInt("xp",xp).putFloat("exp",agentExp).putFloat("energy",energy).putFloat("mass",mass)
       .putFloat("zx",zx).putFloat("zy",zy).putFloat("lifetime",lifeTime)
       .putInt("steps",stepNum).putInt("collected",collected)
       .putInt("researched",researched).putInt("discoveries",discoveries).putInt("hits",hits);
      StringBuilder brain=new StringBuilder(),grid=new StringBuilder(),world=new StringBuilder(),colonies=new StringBuilder();
      for(int i=0;i<12;i++)for(int j=0;j<4;j++)brain.append(q[i][j]).append(',');
      for(boolean b:visited)grid.append(b?'1':'0');
      for(Spot s:spots) {
        if(s.type==NEST){colonies.append(s.x).append(',').append(s.y).append(';');}
        else world.append(s.amount).append(',').append(s.examined?'1':'0').append(',').append(s.hit?'1':'0').append(';');
      }
      e.putString("brain",brain.toString()).putString("map",grid.toString())
        .putString("world",world.toString()).putString("nests",colonies.toString()).apply();
    }
    @Override protected void onSizeChanged(int w,int h,int ow,int oh){
      super.onSizeChanged(w,h,ow,oh);vh=h/(w/VW);
      camY=clamp(zy-vh*.5f,0,WORLD-vh);
    }
    @Override protected void onDraw(Canvas c){
      super.onDraw(c);
      if(!running||getWidth()==0)return;
      long now=System.nanoTime();float dt=lastFrame==0?0:Math.min(.035f,(now-lastFrame)*.000000001f);lastFrame=now;
      if(!paused&&alive){
        float scaled=dt*rates[speedIndex];
        while(scaled>0){float slice=Math.min(scaled,.04f);update(slice);scaled-=slice;}
      } else glow+=dt;
      if(follow){
        camX+= (clamp(zx-VW*.5f,0,WORLD-VW)-camX)*Math.min(1,dt*3.7f);
        camY+= (clamp(zy-vh*.47f,0,Math.max(0,WORLD-vh))-camY)*Math.min(1,dt*3.7f);
      }
      notificationTime=Math.max(0,notificationTime-dt);
      viewHelp=Math.max(0,viewHelp-dt);
      focusGlow=Math.max(0,focusGlow-dt);
      c.save();c.scale(getWidth()/VW,getWidth()/VW);
      background(c);
      c.save();c.translate(-camX,-camY);
      terrain(c);
      for(Spot s:spots)if(s.type==NEST)drawNestWires(c,s);
      for(Spot s:spots)if(visible(s.x,s.y,95))drawSpot(c,s);
      drawZero(c);
      c.restore();
      overlayUI(c);
      if(!alive)drawDeath(c);
      c.restore();
      postInvalidateOnAnimation();
    }
    boolean visible(float x,float y,float margin){
      return x>=camX-margin&&x<=camX+VW+margin&&y>=camY-margin&&y<=camY+vh+margin;
    }
    void update(float dt){
      lifeTime+=dt;glow+=dt;saveClock+=dt;
      if(saveClock>15){saveClock=0;persist();}
      energy-=dt*(.13f+Math.min(.19f,(float)Math.hypot(vx,vy)*.0015f)+stage*.03f);
      if(energy<=0){energy=0;alive=false;vx=vy=0;message("IL NUCLEO E COLLASSATO");persist();return;}
      float dirX=goalX-zx,dirY=goalY-zy;
      float d=(float)Math.hypot(dirX,dirY);
      if(d<25||targetLife>10||thinking<=0) {
        if(thinking<=0||d<23||targetLife>10)chooseGoal(false);
        dirX=goalX-zx;dirY=goalY-zy;d=(float)Math.hypot(dirX,dirY);
      }
      thinking-=dt;targetLife+=dt;
      float ax=d>1?dirX/d:0,ay=d>1?dirY/d:0;
      // Local hazard memory: the organism needs to get hurt once before it can confidently avoid a rift.
      for(Spot s:spots)if(s.type==HAZARD&&s.hit&&distance(zx,zy,s.x,s.y)<145){
        float hd=distance(zx,zy,s.x,s.y);float w=(145-hd)/145;
        ax+=(zx-s.x)/Math.max(1,hd)*2.2f*w;
        ay+=(zy-s.y)/Math.max(1,hd)*2.2f*w;
      }
      float len=(float)Math.hypot(ax,ay);if(len>0.05){ax/=len;ay/=len;}
      float desired=85+stage*17;
      vx+=(ax*desired-vx)*Math.min(1,dt*3.8f);
      vy+=(ay*desired-vy)*Math.min(1,dt*3.8f);
      zx=clamp(zx+vx*dt,48,WORLD-48);
      zy=clamp(zy+vy*dt,48,WORLD-48);
      if(Math.hypot(vx,vy)>8)heading=(float)Math.atan2(vy,vx);
      int cellX=(int)(zx/100),cellY=(int)(zy/100);
      int idx=Math.min(27,cellY)*28+Math.min(27,cellX);
      if(idx>=0&&idx<visited.length&&!visited[idx]){
        visited[idx]=true;discoveries++;gain(2,2,1.5f);
        if(discoveries%9==0)message("NUOVA REGIONE MAPPATA  +2 XP");
      }
      for(Spot s:spots){
        if(distance(zx,zy,s.x,s.y)>43)continue;
        if(s.type==ENERGY&&s.amount>0){
          float extract=Math.min(s.amount,30f*dt);
          s.amount-=extract;
          energy=Math.min(maxEnergy(),energy+extract*.93f);
          mass+=extract*.10f;
          storedReward+=extract*.065f;
          agentExp+=extract*.10f;
          if(s.amount<=0.3){
            s.amount=0;s.delay=46+rnd.nextInt(50);collected++;
            gain(4,6,1.4f);
            if(collected%3==0)message("FONTE ASSIMILATA  +4 XP");
            thinking=0;
          }
        }
        if(s.type==ARTIFACT&&!s.examined&&task==1){
          s.amount-=dt*.45f;
          if(s.amount<=0){
            s.examined=true;researched++;mass+=5;
            gain(16,20,7f);
            message("ANOMALIA DECIFRATA  +16 XP");
            thinking=0;
          }
        }
        if(s.type==HAZARD&&s.delay<=0){
          s.delay=5.0f;energy=Math.max(0,energy-19);
          if(!s.hit)message("ZERO REGISTRA UN PERICOLO");
          s.hit=true;hits++;storedReward-=8f;thinking=0;
        }
        if(s.type==NEST&&s.delay<=0){
          s.delay=12;energy=Math.min(maxEnergy(),energy+6);
        }
      }
      for(Spot s:spots){
        if(s.delay>0)s.delay-=dt;
        if(s.type==ENERGY&&s.amount<=0&&s.delay<=0){
          s.amount=40+rnd.nextInt(50);s.delay=0;
        }
      }
      if(mass>=17&&stage<1){stage=1;stagePulse=2.5f;message("MUTAZIONE: ARTI ORGANICI");gain(18,20,4);}
      if(mass>=45&&stage<2){stage=2;stagePulse=2.5f;message("MUTAZIONE: SIMBIONTE");gain(24,25,5);}
      if(mass>=92&&stage<3){stage=3;stagePulse=2.5f;message("MUTAZIONE: RETE VIVENTE");gain(34,35,7);}
      stagePulse=Math.max(0,stagePulse-dt);
      if(task==3&&stage>=2&&d<31&&mass>52&&energy>52&&nests<8){
        boolean near=false;
        for(Spot s:spots)if(s.type==NEST&&distance(zx,zy,s.x,s.y)<200)near=true;
        if(!near){
          spots.add(new Spot(zx,zy,NEST,1));nests++;mass-=7;energy-=18;
          gain(20,30,9);message("ZERO CREA UN NODO DI RETE");
          thinking=0;
        }
      }
      if(energy<8)storedReward-=dt*.5f;
      if(lifeTime%60<dt)gain(3,4,1f);
    }
    float maxEnergy(){return 100+stage*25;}
    void gain(int currency,float learning,float reward){
      xp+=currency;agentExp+=learning;storedReward+=reward;
    }
    int state(){
      int level=energy<36?0:energy<80?1:2;
      boolean nearEnergy=false,danger=false;
      for(Spot s:spots){
        float d=distance(zx,zy,s.x,s.y);
        if(s.type==ENERGY&&s.amount>1&&d<290)nearEnergy=true;
        if(s.type==HAZARD&&s.hit&&d<120)danger=true;
      }
      return level*4+(nearEnergy?2:0)+(danger?1:0);
    }
    void chooseGoal(boolean first){
      int st=state();
      if(!first&&prevState>=0){
        float best=q[st][0];for(int i=1;i<4;i++)best=Math.max(best,q[st][i]);
        q[prevState][prevAction]+= .15f*(storedReward+.76f*best-q[prevState][prevAction]);
        stepNum++;
      }
      storedReward=0;
      Spot nearestEnergy=nearest(ENERGY,true);
      Spot nearestStudy=nearest(ARTIFACT,true);
      float[] score={
        (energy<35?12:energy<72?5:1)+(nearestEnergy==null?-4:0),
        energy>24&&nearestStudy!=null?4.3f:-10,
        3.3f+(energy>70?1.8f:0),
        stage>=2&&mass>52&&energy>58&&nests<8?5.5f:-20
      };
      for(int i=0;i<4;i++)score[i]+=q[st][i]*.44f;
      float epsilon=Math.max(.075f,.25f-stepNum*.00035f);
      int chosen=0;
      if(rnd.nextFloat()<epsilon){
        ArrayList<Integer> valid=new ArrayList<>();
        for(int i=0;i<4;i++)if(score[i]>-6)valid.add(i);
        chosen=valid.isEmpty()?0:valid.get(rnd.nextInt(valid.size()));
      }else for(int i=1;i<4;i++)if(score[i]>score[chosen])chosen=i;
      if(energy<26&&nearestEnergy!=null)chosen=0;
      task=chosen;prevState=st;prevAction=chosen;thinking=3.8f+rnd.nextFloat()*2;targetLife=0;
      if(chosen==0&&nearestEnergy!=null){goalX=nearestEnergy.x;goalY=nearestEnergy.y;}
      else if(chosen==1&&nearestStudy!=null){goalX=nearestStudy.x;goalY=nearestStudy.y;}
      else if(chosen==3){
        float angle=rnd.nextFloat()*PI*2;
        goalX=clamp(zx+(float)Math.cos(angle)*145,90,WORLD-90);
        goalY=clamp(zy+(float)Math.sin(angle)*145,90,WORLD-90);
      }else {
        // Favor unexplored sectors over returning to already mapped space.
        float best=-999999f,tx=zx,ty=zy;
        for(int k=0;k<28;k++){
          float x=80+rnd.nextFloat()*(WORLD-160),y=80+rnd.nextFloat()*(WORLD-160);
          int cx=Math.min(27,(int)(x/100)),cy=Math.min(27,(int)(y/100));
          float dist=distance(zx,zy,x,y);
          float value=(!visited[cy*28+cx]?530:0)-Math.abs(dist-290)*.6f;
          for(Spot s:spots)if(s.type==HAZARD&&s.hit&&distance(x,y,s.x,s.y)<80)value-=300;
          if(value>best){best=value;tx=x;ty=y;}
        }
        goalX=tx;goalY=ty;
      }
    }
    Spot nearest(int type,boolean available){
      Spot best=null;float dBest=Float.MAX_VALUE;
      for(Spot s:spots){
        if(s.type!=type||(available&&((type==ENERGY&&s.amount<=1)||(type==ARTIFACT&&s.examined))))continue;
        float d=distance(zx,zy,s.x,s.y);
        if(d<dBest){dBest=d;best=s;}
      }
      return best;
    }
    float distance(float x,float y,float xx,float yy){return (float)Math.hypot(x-xx,y-yy);}
    float clamp(float v,float lo,float hi){return Math.max(lo,Math.min(hi,v));}
    void message(String s){notification=s;notificationTime=3.4f;}

    void background(Canvas c){
      p.setShader(new LinearGradient(0,0,0,vh,0xff060a13,0xff101929,Shader.TileMode.CLAMP));
      c.drawRect(0,0,VW,vh,p);p.setShader(null);
      p.setColor(0xff0b1725);c.drawRect(0,125,VW,vh-140,p);
    }
    int hash(int x,int y){
      int v=x*374761393+y*668265263+198491317;v=(v^(v>>13))*1274126177;return v^(v>>>16);
    }
    void terrain(Canvas c){
      int minX=Math.max(0,(int)(camX/70)-2),maxX=Math.min(40,(int)((camX+VW)/70)+2);
      int minY=Math.max(0,(int)(camY/70)-2),maxY=Math.min(40,(int)((camY+vh)/70)+2);
      p.setStyle(Paint.Style.STROKE);
      p.setStrokeWidth(1);
      p.setColor(0x18215a65);
      for(int x=minX;x<=maxX;x++)c.drawLine(x*70,minY*70,x*70,maxY*70,p);
      for(int y=minY;y<=maxY;y++)c.drawLine(minX*70,y*70,maxX*70,y*70,p);
      p.setStyle(Paint.Style.FILL);
      for(int ix=minX;ix<maxX;ix++)for(int iy=minY;iy<maxY;iy++){
        int h=hash(ix,iy),a=Math.abs(h),bx=ix*70,by=iy*70;
        if(a%5==0){
          p.setColor(a%3==0?0x1731b7ab:0x183f497f);
          c.drawCircle(bx+25+(a%27),by+35,13+(a%29),p);
        }
        if(a%7==0){
          p.setColor(0x2a357c79);p.setStrokeWidth(1.7f);
          c.drawLine(bx+6,by+36,bx+22,by+20,p);
          c.drawLine(bx+22,by+20,bx+47,by+27,p);
          p.setColor(0x4a76c1ae);c.drawCircle(bx+47,by+27,1.4f,p);
        }
        if(a%13==0){
          p.setColor(0x264e6878);
          c.drawRoundRect(bx+9,by+12,bx+42,by+37,5,5,p);
          p.setColor(0x2c91b5c0);c.drawRect(bx+15,by+18,bx+35,by+20,p);
        }
      }
      // World edge is deliberately visible; ZERO lives in a closed artificial ecosystem.
      p.setColor(0x9041a9a2);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(5);
      c.drawRoundRect(22,22,WORLD-22,WORLD-22,28,28,p);p.setStyle(Paint.Style.FILL);
      // Shadowy organic residue around ZERO; more pronounced as it evolves.
      if(stage>0){
        for(int k=0;k<stage*2;k++){
          float a=k*6.28f/(stage*2)+glow*.12f;
          float len=22+mass*.46f;
          p.setColor(0x1926caac);
          c.drawOval(zx+(float)Math.cos(a)*len-17,zy+(float)Math.sin(a)*len-8,
             zx+(float)Math.cos(a)*len+17,zy+(float)Math.sin(a)*len+8,p);
        }
      }
    }
    void drawSpot(Canvas c,Spot s){
      float bob=(float)Math.sin(glow*2.6+s.x*.03)*3;
      if(s.type==ENERGY){
        if(s.amount<=0){p.setColor(0x334e8191);c.drawCircle(s.x,s.y,10,p);return;}
        float r=17+Math.min(10,s.amount*.045f);
        orb(c,s.x,s.y,40,0x142debc7,0x1820e4b8);
        p.setColor(0xff123c4a);c.drawOval(s.x-r,s.y+r*.55f,s.x+r,s.y+r*.85f,p);
        path.reset();path.moveTo(s.x-14,s.y+bob);path.lineTo(s.x-4,s.y-20+bob);
        path.lineTo(s.x+10,s.y-25+bob);path.lineTo(s.x+20,s.y+6+bob);
        path.lineTo(s.x+3,s.y+21+bob);path.close();
        p.setColor(0xff1b8e91);c.drawPath(path,p);
        path.reset();path.moveTo(s.x-4,s.y-20+bob);path.lineTo(s.x+10,s.y-25+bob);
        path.lineTo(s.x+6,s.y+9+bob);path.lineTo(s.x+3,s.y+21+bob);path.close();
        p.setColor(0xff69f6d9);c.drawPath(path,p);
        p.setColor(0xffd4fff0);c.drawCircle(s.x+4,s.y-7+bob,3,p);
        ring(c,s.x,s.y,27,0x4844dcca,1.5f);
      }else if(s.type==ARTIFACT){
        if(s.examined){p.setColor(0x5a507f8d);c.drawCircle(s.x,s.y,10,p);return;}
        orb(c,s.x,s.y,39,0x14e7a658,0x22ffc471);
        p.setColor(0xff39354b);c.drawRoundRect(s.x-20,s.y-12,s.x+20,s.y+13,6,6,p);
        p.setColor(0xffaa8c78);c.drawRoundRect(s.x-15,s.y-9,s.x+15,s.y+9,3,3,p);
        ring(c,s.x,s.y,15,GOLD,2);
        p.setColor(GOLD);c.drawCircle(s.x,s.y,4,p);
        p.setColor(0xffead1a9);c.drawRect(s.x-14,s.y+16,s.x+14,s.y+18,p);
      }else if(s.type==HAZARD){
        orb(c,s.x,s.y,55,0x14f14a83,0x14cf567e);
        float pulse=(float)Math.sin(glow*4+s.x)*5;
        path.reset();path.moveTo(s.x-15,s.y-27);
        path.lineTo(s.x+9,s.y-10+pulse);path.lineTo(s.x-5,s.y+4);
        path.lineTo(s.x+17,s.y+25);path.lineTo(s.x-10,s.y+16);
        path.lineTo(s.x-21,s.y-3);path.close();
        p.setColor(0xff7b295d);c.drawPath(path,p);
        p.setColor(PINK);p.setStrokeWidth(3);
        c.drawLine(s.x+9,s.y-10+pulse,s.x-5,s.y+4,p);
        c.drawLine(s.x-5,s.y+4,s.x+17,s.y+25,p);
        ring(c,s.x,s.y,36+4*(float)Math.sin(glow*2),0x66ff658e,1.5f);
      }else if(s.type==NEST){
        orb(c,s.x,s.y,67,0x1922a78d,0x1016ab90);
        p.setColor(0xff070b14);c.drawCircle(s.x,s.y,22,p);
        for(int i=0;i<7;i++){
          float a=i*PI*2/7+glow*.05f;
          p.setColor(i%2==0?0xff187868:0xff2a9f88);
          c.drawCircle(s.x+(float)Math.cos(a)*14,s.y+(float)Math.sin(a)*14,6,p);
        }
        p.setColor(CYAN);c.drawCircle(s.x,s.y,7,p);
      }
    }
    void drawNestWires(Canvas c,Spot s){
      p.setStrokeWidth(2);p.setColor(0x3454c3af);
      float dx=zx-s.x,dy=zy-s.y;
      if(Math.hypot(dx,dy)<440){
        path.reset();path.moveTo(s.x,s.y);
        path.quadTo((s.x+zx)*.5f+25,(s.y+zy)*.5f-22,zx,zy);
        p.setStyle(Paint.Style.STROKE);c.drawPath(path,p);p.setStyle(Paint.Style.FILL);
      }
      for(int k=0;k<8;k++){
        float a=k*PI*.25f;float reach=47+(k%3)*22;
        p.setColor(0x5534a992);p.setStrokeWidth(2.5f);
        c.drawLine(s.x,s.y,s.x+(float)Math.cos(a)*reach,s.y+(float)Math.sin(a)*reach,p);
      }
    }
    void orb(Canvas c,float x,float y,float r,int color1,int color2){
      p.setColor(color1);c.drawCircle(x,y,r,p);
      p.setColor(color2);c.drawCircle(x,y,r*.67f,p);
    }
    void ring(Canvas c,float x,float y,float r,int color,float width){
      p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(width);
      c.drawCircle(x,y,r,p);p.setStyle(Paint.Style.FILL);
    }
    void drawZero(Canvas c){
      c.save();c.translate(zx,zy);c.rotate((float)Math.toDegrees(heading)+90);
      float breathing=1+.04f*(float)Math.sin(glow*5);
      float size=(stage==0?15:stage==1?22:stage==2?29:36)*breathing;
      orb(c,0,0,size*2.3f,0x0626d8bf,0x1522dabc);
      // Continuous soft, organic, spider-like limbs. They articulate as ZERO travels.
      int limbCount=stage==0?2:stage==1?6:8;
      for(int i=0;i<limbCount;i++){
        float a=i*PI*2/limbCount+(float)Math.sin(glow*.8f)*.12f;
        float reach=size*(stage==0?1.3f:2.5f);
        float motion=(float)Math.sin(glow*6+i*1.6f)*7;
        float x1=(float)Math.cos(a)*size*.65f,y1=(float)Math.sin(a)*size*.65f;
        float ex=(float)Math.cos(a)*reach+motion;
        float ey=(float)Math.sin(a)*reach+motion*.5f;
        path.reset();path.moveTo(x1,y1);
        path.cubicTo((float)Math.cos(a+.25f)*reach*.5f,
          (float)Math.sin(a+.25f)*reach*.5f,
          (float)Math.cos(a-.32f)*reach*.86f,
          (float)Math.sin(a-.32f)*reach*.86f,ex,ey);
        p.setStyle(Paint.Style.STROKE);p.setStrokeCap(Paint.Cap.ROUND);
        p.setColor(0xff38b9a9);p.setStrokeWidth(stage==0?2:5.0f);
        c.drawPath(path,p);
        p.setColor(0xff020610);p.setStrokeWidth(stage==0?4:7.5f);
        c.drawPath(path,p);p.setStyle(Paint.Style.FILL);
        p.setColor(0xff68e5d5);c.drawCircle(ex,ey,stage==0?1.4f:2.1f,p);
      }
      // Irregular liquid silhouette.
      path.reset();
      for(int i=0;i<=24;i++){
        float a=i*PI*2/24;
        float rr=size*(.94f+.10f*(float)Math.sin(i*2.7+glow*4)
                 +.055f*(float)Math.sin(i*5.2-glow*3));
        float x=(float)Math.cos(a)*rr,y=(float)Math.sin(a)*rr;
        if(i==0)path.moveTo(x,y);else path.lineTo(x,y);
      }
      path.close();
      p.setColor(0xff153946);c.drawPath(path,p);
      p.setColor(0xff03050e);
      c.save();c.scale(.86f,.85f);c.drawPath(path,p);c.restore();
      p.setColor(0x8843d9c6);c.drawOval(-size*.45f,-size*.62f,size*.2f,-size*.35f,p);
      if(stage>=1){
        for(int i=0;i<3+stage;i++){
          float a=i*PI*2/(3+stage)+glow*.25f;
          p.setColor(0xff1d716f);
          c.drawCircle((float)Math.cos(a)*size*.65f,(float)Math.sin(a)*size*.55f,1.4f+stage*.6f,p);
        }
      }
      p.setColor(0xffdafdf1);c.drawOval(-size*.38f,-size*.54f,size*.36f,-size*.17f,p);
      p.setColor(0xff49cfb8);c.drawCircle(0,-size*.33f,3.3f,p);
      c.restore();
      if(stagePulse>0){
        ring(c,zx,zy,55+(2.5f-stagePulse)*44,((int)(160*stagePulse/2.5f)<<24)|0x42f5df,4);
      }
      p.setColor(0x9d5cead7);
      p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.4f);
      c.drawArc(zx-45,zy-45,zx+45,zy+45,215,110,false,p);
      p.setStyle(Paint.Style.FILL);
      if(stage>0) {
        label(c,"ZERO",zx,zy-60-stage*9,12,ICE,true);
      }
    }
    void panel(Canvas c,float l,float t,float r,float b,int fill,float radius){
      p.setColor(fill);c.drawRoundRect(l,t,r,b,radius,radius,p);
    }
    void label(Canvas c,String text,float x,float y,float size,int color,boolean center){
      p.setColor(color);p.setShader(null);p.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));
      p.setTextAlign(center?Paint.Align.CENTER:Paint.Align.LEFT);
      p.setTextSize(size);c.drawText(text,x,y,p);
    }
    void overlayUI(Canvas c){
      panel(c,12,12,468,130,0xe20a1522,20);
      p.setColor(CYAN);c.drawRoundRect(28,27,32,58,2,2,p);
      label(c,"O V E R M I N D",44,35,12,ICE,false);
      label(c,"ZERO / "+stageName(),44,59,18,0xfff4fffd,false);
      label(c,"ESPERIENZA",310,34,10,0xff8dabc1,false);
      label(c,""+xp+" XP",310,60,24,GOLD,false);
      label(c,"ENERGIA",28,86,10,0xff9abac4,false);
      label(c,(int)energy+" / "+(int)maxEnergy(),384,87,11,ICE,false);
      panel(c,28,99,451,110,0xff273849,6);
      panel(c,28,99,28+423*clamp(energy/maxEnergy(),0,1),110,energy<27?PINK:CYAN,6);
      label(c,"OBIETTIVO  "+moods[task],26,154,12,ICE,false);
      label(c,"MEMORIA "+stepNum+"  •  NODI "+nests,26,173,10,0xff8297aa,false);
      panel(c,304,143,372,183,0xe1193040,12);
      label(c,follow?"SEGUE":"MAPPA",338,168,12,ICE,true);
      panel(c,379,143,429,183,0xe1193040,12);
      label(c,"x"+(int)rates[speedIndex],404,168,15,ICE,true);
      panel(c,436,143,470,183,0xe1193040,12);
      label(c,paused?"▶":"Ⅱ",453,168,16,ICE,true);

      float base=vh-162;
      panel(c,10,base,470,vh-10,0xf00b1724,22);
      label(c,mode==0?"INTERVENTI NEL MONDO  •  ZERO NON TI VEDE":"TOCCA IL TERRENO PER APPLICARE",24,base+24,11,mode==0?0xffb2c7d3:GOLD,false);
      power(c,15,base+37,151,vh-22,1,"ENERGIA","18 XP",CYAN);
      power(c,164,base+37,313,vh-22,2,"RELIQUIA","35 XP",GOLD);
      power(c,326,base+37,465,vh-22,3,"ANOMALIA","28 XP",PINK);
      if(notificationTime>0){
        float y=base-51;
        panel(c,26,y,454,y+37,0xe5203040,13);
        label(c,notification,240,y+24,12,GOLD,true);
      }
      if(viewHelp>0&&lifeTime<4){
        panel(c,28,195,452,261,0xc5152833,14);
        label(c,"LEI VIVE. LEI IMPARA. LEI NON TI CONOSCE.",240,222,13,ICE,true);
        label(c,"Osserva • Spendi XP • Cambia il mondo",240,245,12,0xffabc2d2,true);
      }
      if(focusGlow>0&&!observation.isEmpty()){
        panel(c,25,189,455,224,0xe52b3446,11);
        label(c,observation,240,212,12,ICE,true);
      }
      // Spatial progress and evolution tracked separately from spendable XP.
      label(c,"EVOLUZIONE",27,base-14,10,0xff96adbf,false);
      float progress=stage==0?mass/17:stage==1?(mass-17)/28:stage==2?(mass-45)/47:1;
      panel(c,117,base-23,240,base-16,0xff28394b,4);
      panel(c,117,base-23,117+123*clamp(progress,0,1),base-16,CYAN,4);
      label(c,"SCOPERTE "+discoveries,254,base-14,10,0xffa9bdcc,false);
    }
    String stageName(){return stage==0?"NUCLEO":stage==1?"PROTOFORMA":stage==2?"SIMBIONTE":"COLONIA";}
    void power(Canvas c,float l,float t,float r,float b,int which,String name,String price,int glowColor){
      boolean selected=mode==which;
      panel(c,l,t,r,b,selected?0xff234b53:0xff172938,13);
      if(selected){p.setColor(glowColor);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);
        c.drawRoundRect(l,t,r,b,13,13,p);p.setStyle(Paint.Style.FILL);}
      float mid=(l+r)/2;
      if(which==1) {
        orb(c,mid,t+23,18,0x303aefd1,0x353df0c4);
        p.setColor(CYAN);c.drawCircle(mid,t+23,7,p);
      }else if(which==2){
        p.setColor(0xffc79664);c.drawRoundRect(mid-11,t+13,mid+11,t+33,4,4,p);
        ring(c,mid,t+23,6,GOLD,2);
      }else{
        ring(c,mid,t+23,12,PINK,2);
        p.setColor(PINK);c.drawLine(mid-9,t+23,mid+7,t+13,p);
        c.drawLine(mid+7,t+13,mid-3,t+34,p);
      }
      label(c,name,mid,b-30,12,ICE,true);
      label(c,price,mid,b-11,12,xp>= (which==1?18:which==2?35:28)?glowColor:0xff748292,true);
    }
    void drawDeath(Canvas c){
      panel(c,30,vh*.32f,450,vh*.66f,0xf20b1324,25);
      label(c,"ZERO E COLLASSATA",240,vh*.42f,26,PINK,true);
      label(c,"La memoria resta. L'organismo puo rinascere.",240,vh*.48f,13,ICE,true);
      label(c,"TOCCA PER RIGENERARE",240,vh*.60f,19,GOLD,true);
    }
    @Override public boolean onTouchEvent(MotionEvent e){
      if(getWidth()==0)return true;
      float x=e.getX(e.getActionIndex())/(getWidth()/VW);
      float y=e.getY(e.getActionIndex())/(getWidth()/VW);
      switch(e.getActionMasked()){
        case MotionEvent.ACTION_DOWN:
          downX=x;downY=y;lastTouchX=x;lastTouchY=y;dragDistance=0;dragged=false;return true;
        case MotionEvent.ACTION_MOVE:
          if(downY<vh-180&&downY>190&&!paused&&alive){
            float dx=x-lastTouchX,dy=y-lastTouchY;
            dragDistance+=(float)Math.hypot(dx,dy);
            if(dragDistance>12&&!dragged){dragged=true;follow=false;}
            if(dragged){camX=clamp(camX-dx,0,WORLD-VW);camY=clamp(camY-dy,0,Math.max(0,WORLD-vh));}
          }
          lastTouchX=x;lastTouchY=y;return true;
        case MotionEvent.ACTION_UP:
          if(!alive){
            energy=67;mass=Math.max(0,mass*.75f);
            stage=mass>=92?3:mass>=45?2:mass>=17?1:0;
            zx=1400;zy=1400;alive=true;follow=true;task=2;thinking=0;
            xp=Math.max(0,xp-10);message("NUOVO CICLO: MEMORIA CONSERVATA");persist();return true;
          }
          if(y>=vh-125){
            int pick=x<158?1:x<320?2:3;
            mode=mode==pick?0:pick;return true;
          }
          if(y<188&&y>130){
            if(x>430){paused=!paused;lastFrame=0;persist();}
            else if(x>377)speedIndex=(speedIndex+1)%3;
            else if(x>301){follow=true;}
            return true;
          }
          if(!dragged&&y>192&&y<vh-172){
            float wx=x+camX,wy=y+camY;
            if(mode!=0){
              int cost=mode==1?18:mode==2?35:28;
              if(xp<cost){message("ESPERIENZA INSUFFICIENTE");return true;}
              if(wx<40||wy<40||wx>WORLD-40||wy>WORLD-40){message("FUORI DALL'ECOSISTEMA");return true;}
              xp-=cost;
              if(mode==1){spots.add(new Spot(wx,wy,ENERGY,120));message("NUOVA FONTE ENERGETICA");}
              else if(mode==2){spots.add(new Spot(wx,wy,ARTIFACT,1));message("UNA RELIQUIA E APPARSA");}
              else{spots.add(new Spot(wx,wy,HAZARD,1));message("ANOMALIA NELL'ECOSISTEMA");}
              mode=0;persist();return true;
            }
            Spot nearest=null;float nearestD=68;
            for(Spot s:spots){
              float d=distance(wx,wy,s.x,s.y);
              if(d<nearestD){nearest=s;nearestD=d;}
            }
            if(nearest==null){observation="TERRA INESPLORATA";focusGlow=2.1f;}
            else{
              observation=nearest.type==ENERGY?"FONTE • ENERGIA "+(int)nearest.amount:
                nearest.type==ARTIFACT?(nearest.examined?"RELIQUIA GIA DECIFRATA":"RELIQUIA SCONOSCIUTA"):
                nearest.type==HAZARD?"ANOMALIA • PERICOLO":"NODO ORGANICO ATTIVO";
              focusGlow=2.2f;
            }
          }
          return true;
        case MotionEvent.ACTION_CANCEL:return true;
        default:return true;
      }
    }
  }
}
