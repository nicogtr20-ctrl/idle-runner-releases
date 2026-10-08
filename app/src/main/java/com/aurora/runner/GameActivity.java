package com.aurora.runner;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import java.util.*;

public final class GameActivity extends Activity {
  private RunnerView game;
  @Override public void onCreate(Bundle b) {
    super.onCreate(b);
    getWindow().setFlags(1024,1024);
    getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
    game = new RunnerView();
    setContentView(game);
  }
  @Override protected void onPause() {
    super.onPause();
    if(game != null) game.active=false;
  }
  @Override protected void onResume() {
    super.onResume();
    if(game != null) {game.active=true;game.lastFrame=0;game.postInvalidateOnAnimation();}
  }

  private final class RunnerView extends View {
    static final float W=480f, G=2350f;
    static final int MENU=0, PLAYING=1, PAUSE=2, DEAD=3;
    final Paint p=new Paint(3);
    final Random rand=new Random(21);
    final ArrayList<GoldCoin> coins=new ArrayList<>();
    final ArrayList<Barrier> barriers=new ArrayList<>();
    final ArrayList<Spark> sparks=new ArrayList<>();
    final int[] skyA={0xff1c1d49,0xff073955,0xff10122c,0xff312044};
    final int[] skyB={0xffe17f90,0xff7dcab7,0xff9a6fba,0xffdb9aac};
    final int[] distant={0xff6d4274,0xff497b85,0xff44416c,0xff78517e};
    final int[] near={0xff3b2d5a,0xff205766,0xff272e56,0xff4b365d};
    final int[] earth={0xff302745,0xff254454,0xff1f2547,0xff48344c};
    final int[] accent={0xffffbf8a,0xff8aeed2,0xffd2a8ff,0xffffaad6};
    final String[] names={"SUNSET SHORE","MINT MOUNTAINS","NEON HORIZON","MOON GARDEN"};
    int status=MENU, worldIndex=0, hp=3, jumps=0, score=0;
    float H=920, floor=725, py, vy, holdFor=0, grace=0;
    float traveled=0, speed=290, hazardGap=540, coinGap=210, elapsed=0, flash=0;
    float banner=0, invincible=0, popup=0, popupX, popupY, cameraShake=0;
    boolean grounded=true, pressed=false, active=true;
    long lastFrame=0;
    final float playerX=116;

    RunnerView() {
      super(GameActivity.this);
      setFocusable(true);
      reset();
      status=MENU;
    }
    void reset() {
      status=PLAYING;hp=3;jumps=0;score=0;worldIndex=0;
      speed=290;traveled=0;hazardGap=600;coinGap=185;elapsed=0;
      flash=0;banner=0;invincible=0;cameraShake=0;popup=0;
      floor=H*.79f;py=floor;vy=0;grounded=true;pressed=false;holdFor=0;grace=0;
      coins.clear();barriers.clear();sparks.clear();
    }
    @Override protected void onSizeChanged(int w,int h,int ow,int oh){
      super.onSizeChanged(w,h,ow,oh);
      H=h/(w/W);floor=H*.79f;
      if(status!=PLAYING)py=floor;
    }
    @Override protected void onDraw(Canvas out){
      super.onDraw(out);
      if(!active||getWidth()==0)return;
      long now=System.nanoTime();
      float dt=lastFrame==0?0:Math.min(.034f,(now-lastFrame)*.000000001f);
      lastFrame=now;
      if(status==PLAYING)step(dt); else elapsed+=dt*.28f;
      out.save();out.scale(getWidth()/W,getWidth()/W);
      if(cameraShake>0&&status==PLAYING)out.translate((float)Math.sin(elapsed*115)*cameraShake*4,0);
      background(out);
      for(GoldCoin coin:coins)drawCoin(out,coin);
      for(Barrier hazard:barriers)drawBarrier(out,hazard);
      drawPlayer(out);
      for(Spark s:sparks){
        p.setColor(fade(s.color, s.life/s.maxLife));
        out.drawCircle(s.x,s.y,s.r*Math.max(.1f,s.life/s.maxLife),p);
      }
      hud(out);
      if(status!=PLAYING)overlay(out);
      else if(banner>0){
        p.setColor(fade(0xff171b39,Math.min(1,banner)));
        out.drawRoundRect(55,H*.28f,425,H*.28f+110,18,18,p);
        writing(out,"NEW AREA",240,H*.28f+38,17,accent[worldIndex],true);
        writing(out,names[worldIndex],240,H*.28f+80,25,0xffffffff,true);
      }
      if(popup>0)writing(out,"+1",popupX,popupY,24,0xffffe18c,true);
      out.restore();
      postInvalidateOnAnimation();
    }
    void step(float dt){
      elapsed+=dt;
      speed=Math.min(408,290+traveled/1600f);
      traveled+=speed*dt;
      int next=(int)(traveled/2600)%4;
      if(next!=worldIndex){
        worldIndex=next;banner=2.2f;
        burst(410,floor-165,22,accent[worldIndex]);
      }
      banner=Math.max(0,banner-dt);
      cameraShake=Math.max(0,cameraShake-2.5f*dt);
      invincible=Math.max(0,invincible-dt);
      popup=Math.max(0,popup-dt);
      if(popup>0)popupY-=36*dt;
      if(!grounded){
        if(pressed&&vy<0&&holdFor<.17f){
          holdFor+=dt;
          vy-=1180f*dt;
        }
        vy+=G*dt;
        py+=vy*dt;
        grace=Math.max(0,grace-dt);
        if(py>=floor){
          py=floor;vy=0;grounded=true;jumps=0;holdFor=0;
          dust(116,floor,8);
        }
      }else {py=floor;grace=.085f;}
      hazardGap-=speed*dt;
      coinGap-=speed*dt;
      if(hazardGap<=0){
        barriers.add(new Barrier(520,rand.nextInt(3)));
        hazardGap=480+rand.nextInt(210);
      }
      if(coinGap<=0){
        boolean elevated=rand.nextBoolean();
        for(int i=0;i<6;i++){
          float arc=(float)Math.sin(Math.PI*i/5f);
          float y=floor-92-arc*(elevated?103:48);
          coins.add(new GoldCoin(524+i*48,y));
        }
        coinGap=520+rand.nextInt(200);
      }
      for(Iterator<GoldCoin> it=coins.iterator();it.hasNext();){
        GoldCoin c=it.next();c.x-=speed*dt;
        if(Math.abs(c.x-playerX)<33&&Math.abs(c.y-(py-46))<44){
          score++;popup=.48f;popupX=c.x;popupY=c.y-18;
          burst(c.x,c.y,10,0xffffcf71);it.remove();
        }else if(c.x < -35)it.remove();
      }
      for(Iterator<Barrier> it=barriers.iterator();it.hasNext();){
        Barrier b=it.next();b.x-=speed*dt;
        if(b.x < -80){it.remove();continue;}
        if(invincible==0 && playerX+17>b.x-b.width/2+8 && playerX-17<b.x+b.width/2-8
            && py-5>floor-b.height+7 && py-75<floor-3){
          hp--;invincible=1.55f;cameraShake=1;burst(playerX,py-40,23,0xffff6f93);
          if(hp<=0){status=DEAD;pressed=false;}
        }
      }
      for(Iterator<Spark> it=sparks.iterator();it.hasNext();){
        Spark s=it.next();s.life-=dt;
        if(s.life<=0){it.remove();continue;}
        s.x+=s.vx*dt;s.y+=s.vy*dt;s.vy+=390*dt;
      }
    }
    void triggerJump(){
      if(status!=PLAYING)return;
      // Every new touch-down is a new jump press. A second press in the air
      // immediately triggers the second jump, even if the two taps are very fast.
      if(grounded||grace>0){
        grounded=false;grace=0;jumps=1;vy=-730;holdFor=0;
        dust(playerX,py,10);
      }else if(jumps<2){
        jumps=2;vy=-685;holdFor=0;
        burst(playerX,py-25,17,accent[worldIndex]);
      }
    }
    @Override public boolean onTouchEvent(MotionEvent e){
      int a=e.getActionMasked();
      if(a==MotionEvent.ACTION_DOWN||a==MotionEvent.ACTION_POINTER_DOWN){
        float x=e.getX(e.getActionIndex())/(getWidth()/W);
        float y=e.getY(e.getActionIndex())/(getWidth()/W);
        if(status==MENU||status==DEAD){reset();return true;}
        if(x>389&&y<105){
          status=status==PLAYING?PAUSE:PLAYING;
          pressed=false;lastFrame=0;return true;
        }
        if(status==PAUSE){status=PLAYING;lastFrame=0;return true;}
        pressed=true;triggerJump();
        return true;
      }
      if(a==MotionEvent.ACTION_UP||a==MotionEvent.ACTION_POINTER_UP||a==MotionEvent.ACTION_CANCEL){
        pressed=false;
        if(!grounded&&vy< -110)vy*=.67f;
        return true;
      }
      return true;
    }
    void background(Canvas c){
      int idx=worldIndex;
      p.setShader(new LinearGradient(0,0,0,floor+90,skyA[idx],skyB[idx],Shader.TileMode.CLAMP));
      c.drawRect(0,0,W,H,p);p.setShader(null);
      for(int i=0;i<44;i++){
        float x=(i*103+17)%477,y=(i*79+24)%(int)(floor*.69f);
        float opacity=idx==2?.7f:.3f;
        p.setColor(fade(0xffffffff,opacity*(.65f+.35f*(float)Math.sin(elapsed*2+i))));
        c.drawCircle(x,y,i%9==0?1.7f:.9f,p);
      }
      float sunX=365,sunY=floor*.245f;
      p.setColor(fade(accent[idx],.10f));c.drawCircle(sunX,sunY,112,p);
      p.setColor(fade(accent[idx],.17f));c.drawCircle(sunX,sunY,81,p);
      p.setColor(fade(accent[idx],.23f));c.drawCircle(sunX,sunY,58,p);
      p.setColor(idx==2?0xffe7dbfb:0xffffdbc3);
      c.drawCircle(sunX,sunY,38,p);
      for(int i=0;i<5;i++){
        float x=wrap(i*175-traveled*.07f,720)-145;
        float y=floor*(.16f+i%3*.11f);
        p.setColor(fade(0xffffffff,idx==2?.07f:.16f));
        c.drawOval(x-55,y,x+85,y+28,p);
        c.drawOval(x-22,y-21,x+55,y+28,p);
      }
      mountains(c,.12f,floor-245,104,distant[idx]);
      mountains(c,.34f,floor-119,104,near[idx]);
      scenery(c);
      p.setColor(earth[idx]);c.drawRect(0,floor,W,H,p);
      p.setColor(accent[idx]);c.drawRect(0,floor,W,floor+5,p);
      p.setColor(fade(accent[idx],.14f));c.drawRect(0,floor+5,W,floor+18,p);
      float offset=traveled%62;
      for(int i=0;i<10;i++){
        float x=i*62-offset;
        p.setColor(fade(0xffffffff,.045f));
        c.drawRoundRect(x+8,floor+30,x+55,floor+69,6,6,p);
        p.setColor(fade(accent[idx],.12f));
        c.drawRoundRect(x+22,floor+89,x+44,floor+93,3,3,p);
      }
    }
    void mountains(Canvas c,float factor,float baseline,float amplitude,int color){
      float offset=(traveled*factor)%210;
      Path path=new Path();
      path.moveTo(-250,floor);
      for(int i=-2;i<5;i++){
        float x=i*210-offset;
        path.lineTo(x,baseline+45);
        path.quadTo(x+64,baseline-amplitude*1.25f,x+111,baseline-amplitude*.7f);
        path.quadTo(x+151,baseline-amplitude*.15f,x+210,baseline+45);
      }
      path.lineTo(W+230,floor);path.close();
      p.setColor(color);c.drawPath(path,p);
    }
    void scenery(Canvas c){
      float offset=(traveled*.68f)%168;
      for(int i=-1;i<5;i++){
        float x=i*168-offset+36;
        if(worldIndex==1){
          p.setColor(fade(accent[worldIndex],.38f));
          Path crystal=new Path();
          crystal.moveTo(x,floor-100);crystal.lineTo(x+27,floor-34);
          crystal.lineTo(x+12,floor);crystal.lineTo(x-13,floor);
          crystal.lineTo(x-22,floor-40);crystal.close();c.drawPath(crystal,p);
        }else if(worldIndex==2){
          p.setColor(fade(0xffd9a7f9,.34f));
          c.drawRoundRect(x-12,floor-96,x+18,floor,4,4,p);
          p.setColor(fade(0xfff5c1e8,.42f));
          c.drawRoundRect(x-2,floor-78,x+5,floor-60,2,2,p);
          c.drawRoundRect(x+8,floor-50,x+14,floor-32,2,2,p);
        }else{
          p.setColor(fade(accent[worldIndex],.37f));
          c.drawRoundRect(x-4,floor-65,x+5,floor,4,4,p);
          c.drawOval(x-32,floor-90,x+31,floor-46,p);
          p.setColor(fade(0xffffedc0,.24f));
          c.drawCircle(x+12,floor-72,11,p);
        }
      }
    }
    void drawCoin(Canvas c,GoldCoin b){
      float x=b.x,y=b.y+(float)Math.sin(elapsed*7+x*.018f)*2;
      float glint=(float)Math.sin(elapsed*5+x*.02f);
      p.setColor(fade(0xffffd26e,.20f));c.drawCircle(x,y,27,p);
      p.setColor(0xffb97630);c.drawCircle(x,y,20.3f,p);
      p.setColor(0xffffd56b);c.drawCircle(x,y-1.4f,18.2f,p);
      p.setColor(0xffffedaf);c.drawCircle(x-4,y-5,8,p);
      p.setColor(0xffc58c36);p.setStyle(Paint.Style.STROKE);
      p.setStrokeWidth(2);c.drawCircle(x,y-1,12.7f,p);
      p.setStyle(Paint.Style.FILL);
      p.setColor(fade(0xffffffff,.48f+.4f*Math.max(0,glint)));
      c.drawOval(x-10,y-12,x-2,y-7,p);
    }
    void drawBarrier(Canvas c,Barrier b){
      float x=b.x,y=floor;
      p.setColor(fade(0xff111329,.32f));c.drawOval(x-32,y-7,x+33,y+7,p);
      if(b.type==0){
        p.setColor(0xff614c69);c.drawRoundRect(x-24,y-b.height,x+24,y,5,5,p);
        p.setColor(0xff986c87);c.drawRoundRect(x-20,y-b.height+4,x+20,y-6,4,4,p);
        p.setColor(0xffffc778);
        c.drawRoundRect(x-23,y-b.height+13,x+23,y-b.height+20,2,2,p);
        c.drawRoundRect(x-23,y-19,x+23,y-12,2,2,p);
      }else if(b.type==1){
        p.setColor(0xff5a526c);
        Path rock=new Path();rock.moveTo(x-31,y);rock.lineTo(x-22,y-b.height*.73f);
        rock.lineTo(x-3,y-b.height);rock.lineTo(x+23,y-b.height*.84f);
        rock.lineTo(x+30,y-9);rock.lineTo(x+16,y);rock.close();c.drawPath(rock,p);
        p.setColor(0xffaa8fa4);c.drawOval(x-16,y-b.height+8,x+4,y-b.height+13,p);
      }else{
        Path spikes=new Path();spikes.moveTo(x-32,y);
        for(int i=0;i<3;i++){
          float ox=x-32+i*21;
          spikes.lineTo(ox+11,y-b.height-(i==1?9:0));spikes.lineTo(ox+21,y);
        }
        spikes.close();p.setColor(0xffc45c85);c.drawPath(spikes,p);
        p.setColor(0xffffadc0);
        c.drawLine(x-18,y-22,x-10,y-b.height+12,p);
      }
    }
    void drawPlayer(Canvas c){
      float running=(float)Math.sin(elapsed*21);
      float bob=grounded&&status==PLAYING?running*2.9f:0;
      float opacity=(invincible>0&&((int)(elapsed*13)%2==0))?.4f:1f;
      c.save();c.translate(playerX,py-43+bob);
      c.rotate(grounded?running*3.1f:(jumps==2?8:-13));
      p.setStrokeCap(Paint.Cap.ROUND);
      p.setStrokeWidth(10);p.setColor(fade(0xff46536e,opacity));
      float stride=grounded?running*16:-6;
      c.drawLine(-8,16,-16+stride,34,p);
      c.drawLine(8,16,14-stride,34,p);
      p.setStrokeWidth(9);p.setColor(fade(0xffffb48f,opacity));
      c.drawLine(-16+stride,34,-5+stride,34,p);
      c.drawLine(14-stride,34,26-stride,34,p);

      Path cape=new Path();cape.moveTo(-9,-19);
      cape.quadTo(-35-(float)Math.sin(elapsed*10)*6,-30,-55,-9);
      cape.quadTo(-42,7,-9,14);cape.close();
      p.setColor(fade(0xffed608c,opacity));c.drawPath(cape,p);

      p.setColor(fade(0xffffc785,opacity));
      c.drawRoundRect(-17,-20,17,25,12,12,p);
      p.setColor(fade(0xffe98b68,opacity));
      c.drawRoundRect(-17,-20,17,-6,8,8,p);

      p.setStrokeWidth(8);p.setColor(fade(0xffffc09a,opacity));
      float arm=grounded?running*9:3;
      c.drawLine(-14,-7,-23,6-arm,p);
      c.drawLine(15,-7,25,6+arm,p);

      p.setColor(fade(0xff344a69,opacity));
      c.drawRoundRect(-22,-59,22,-13,19,19,p);
      p.setColor(fade(0xff647d9e,opacity));
      c.drawRoundRect(-19,-56,19,-27,15,15,p);
      p.setColor(fade(0xffd9f8ff,opacity));
      c.drawRoundRect(-13,-47,17,-29,7,7,p);
      p.setColor(fade(0xff34516c,opacity));
      c.drawCircle(-2,-38,3,p);c.drawCircle(10,-38,3,p);
      p.setColor(fade(0xffffffff,opacity));
      c.drawCircle(11,-43,3.5f,p);
      c.restore();
    }
    void hud(Canvas c){
      p.setColor(fade(0xff151832,.73f));
      c.drawRoundRect(15,22,174,94,18,18,p);
      c.drawRoundRect(185,22,384,94,18,18,p);
      c.drawRoundRect(396,22,465,94,18,18,p);
      for(int i=0;i<3;i++)heart(c,41+i*42,52,i<hp?0xffff739b:0xff6e6079);
      writing(c,"HEALTH",92,84,11,0xffcfcede,true);
      writing(c,String.format(Locale.US,"%04d",(int)(traveled/15)),242,54,23,0xfffbf4ff,true);
      writing(c,"DISTANCE",243,81,11,0xffd8cadb,true);
      p.setColor(0xffffd36e);c.drawCircle(348,48,10,p);
      writing(c,""+score,365,61,20,0xffffffff,false);
      writing(c,status==PAUSE?"▶":"Ⅱ",430,69,27,0xffffffff,true);
      p.setColor(fade(0xffffffff,.15f));
      c.drawRoundRect(15,108,465,114,3,3,p);
      p.setColor(accent[worldIndex]);
      c.drawRoundRect(15,108,15+(traveled%2600)/2600*450,114,3,3,p);
      if(jumps==2&&!grounded){
        writing(c,"DOUBLE JUMP!",240,floor-148,15,accent[worldIndex],true);
      }
    }
    void heart(Canvas c,float x,float y,int color){
      Path a=new Path();a.moveTo(x,y+11);
      a.cubicTo(x-25,y-4,x-15,y-18,x,y-6);
      a.cubicTo(x+15,y-18,x+25,y-4,x,y+11);
      p.setColor(color);c.drawPath(a,p);
    }
    void overlay(Canvas c){
      p.setColor(fade(0xff0a1025,.77f));
      c.drawRoundRect(26,H*.30f,454,H*.72f,32,32,p);
      p.setColor(accent[worldIndex]);
      c.drawRoundRect(65,H*.30f+12,415,H*.30f+17,2,2,p);
      if(status==MENU){
        writing(c,"AURORA",240,H*.38f,49,0xffffffff,true);
        writing(c,"DASH",240,H*.43f,54,accent[worldIndex],true);
        writing(c,"RUN • JUMP • EXPLORE",240,H*.49f,18,0xfff6e4ef,true);
        writing(c,"Tap: low jump   •   Hold: high jump",240,H*.56f,16,0xffd7cde3,true);
        writing(c,"Tap again mid-air: DOUBLE JUMP",240,H*.59f,16,0xffd7cde3,true);
        writing(c,"TAP TO PLAY",240,H*.665f,25,0xffffd784,true);
      }else if(status==PAUSE){
        writing(c,"PAUSED",240,H*.41f,46,0xffffffff,true);
        writing(c,"TAP TO RESUME",240,H*.62f,25,0xffffd784,true);
      }else{
        writing(c,"RUN OVER",240,H*.40f,45,0xffffffff,true);
        writing(c,"COINS   "+score,240,H*.49f,23,0xffffd784,true);
        writing(c,"DISTANCE   "+(int)(traveled/15),240,H*.54f,20,0xffe4d5e9,true);
        writing(c,"TAP TO RETRY",240,H*.65f,26,0xffffd784,true);
      }
    }
    void writing(Canvas c,String t,float x,float y,float size,int color,boolean center){
      p.setShader(null);p.setColor(color);p.setTypeface(Typeface.create("sans-serif-black",Typeface.BOLD));
      p.setTextSize(size);p.setTextAlign(center?Paint.Align.CENTER:Paint.Align.LEFT);
      c.drawText(t,x,y,p);
    }
    int fade(int color,float alpha){
      return (color&0x00ffffff)|((int)(255*Math.max(0,Math.min(1,alpha)))<<24);
    }
    float wrap(float n,float d){return (n%d+d)%d;}
    void burst(float x,float y,int count,int color){
      for(int i=0;i<count;i++){
        float a=(float)(rand.nextDouble()*Math.PI*2);
        float v=75+rand.nextFloat()*145;
        sparks.add(new Spark(x,y,(float)Math.cos(a)*v,(float)Math.sin(a)*v,2+rand.nextFloat()*4,.38f+rand.nextFloat()*.26f,color));
      }
    }
    void dust(float x,float y,int count){
      for(int i=0;i<count;i++)
        sparks.add(new Spark(x,y-4,(rand.nextFloat()-.5f)*170,-30-rand.nextFloat()*95,2+rand.nextFloat()*3,.28f+rand.nextFloat()*.20f,accent[worldIndex]));
    }
    final class GoldCoin {float x,y;GoldCoin(float xx,float yy){x=xx;y=yy;}}
    final class Barrier {
      float x,height,width;int type;
      Barrier(float xx,int t){x=xx;type=t;height=t==0?61:t==1?67:48;width=t==2?63:57;}
    }
    final class Spark {
      float x,y,vx,vy,r,life,maxLife;int color;
      Spark(float xx,float yy,float dx,float dy,float rr,float ll,int cc){
        x=xx;y=yy;vx=dx;vy=dy;r=rr;life=ll;maxLife=ll;color=cc;
      }
    }
  }
}
