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
        ContactLearning.Trainer handTrainer;
        Morphology.Trainer shapeTrainer;
        int pushes=0,physicalContacts=0;
        float biggestDisplacement=0;
        float recentDisplacement=0;
        volatile float handInitial=Float.NaN,handTrained=Float.NaN;
        UnknownWorld.Mind mind=new UnknownWorld.Mind();
        DecisionBrain decisionBrain=new DecisionBrain(90517L);
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
        Morphology.Surface surface=new Morphology.Surface(creature);
        ScarcityLab chamber;
        boolean scarcity=false;
        float elapsedLife=0,nextChamber=70f;
        int chambersCleared=0,energyEaten=0,gripsEver=0;
        float farthestGate=0;
        String chamberMessage="LA FAME RENDERA' PREZIOSA L'ENERGIA";
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
            float[] savedHand=decodeHand(prefs.getString("hand_brain_v04",""));
            handTrainer=new ContactLearning.Trainer(savedHand);
            handTrainer.attempts=prefs.getInt("hand_attempts_v04",0);
            handTrainer.accepted=prefs.getInt("hand_successes_v04",0);
            shapeTrainer=new Morphology.Trainer(decodeShape(prefs.getString("morph_brain_v07","")));
            shapeTrainer.attempts=prefs.getInt("morph_attempts_v07",0);
            shapeTrainer.accepted=prefs.getInt("morph_accepted_v07",0);
            shapeTrainer.gateAttempts=prefs.getInt("morph_gate_attempts_v08",0);
            shapeTrainer.gateAccepted=prefs.getInt("morph_gate_success_v08",0);
            // Do not reuse v0.4's 'physical_pushes' counter: it counted
            // collisions, not confirmed object displacement.
            pushes=prefs.getInt("objects_displaced_v041",0);
            physicalContacts=prefs.getInt("collisions_v041",0);
            biggestDisplacement=prefs.getFloat("max_displacement_v041",0);
            mind=new UnknownWorld.Mind();
            mind.deserialize(prefs.getString("world_knowledge_v03",""));
            decisionBrain=new DecisionBrain(90517L);
            decisionBrain.restore(prefs.getString("decision_brain_v05",""));
            reserve=prefs.getFloat("zero_energy_v03",57f);
            elapsedLife=prefs.getFloat("scarcity_elapsed_v08",0);
            scarcity=prefs.getBoolean("scarcity_active_v08",false);
            chambersCleared=prefs.getInt("room_success_v08",0);
            energyEaten=prefs.getInt("finite_energy_eaten_v08",0);
            gripsEver=prefs.getInt("grip_events_v08",0);
            farthestGate=prefs.getFloat("gate_travel_v08",0);
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
        float[] decodeHand(String s){
            if(s==null||s.length()==0)return null;
            String[] chunks=s.split(",");
            if(chunks.length!=ContactLearning.PARAMS)return null;
            float[] w=new float[chunks.length];
            try{
                for(int i=0;i<w.length;i++){
                    w[i]=Float.parseFloat(chunks[i]);
                    if(!Float.isFinite(w[i]))return null;
                }
                return w;
            }catch(Exception ex){return null;}
        }
        float[] decodeShape(String s){
            if(s==null||s.length()==0)return null;
            String[] arr=s.split(",");
            if(arr.length!=Morphology.PARAMS)return null;
            float[] out=new float[Morphology.PARAMS];
            try {
                for(int i=0;i<out.length;i++){
                    out[i]=Float.parseFloat(arr[i]);
                    if(!Float.isFinite(out[i])||Math.abs(out[i])>5)return null;
                }
                return out;
            }catch(Exception e){return null;}
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
              .putString("hand_brain_v04",encode(handTrainer.best))
              .putInt("hand_attempts_v04",handTrainer.attempts)
              .putInt("hand_successes_v04",handTrainer.accepted)
              .putInt("objects_displaced_v041",pushes)
              .putInt("collisions_v041",physicalContacts)
              .putFloat("max_displacement_v041",biggestDisplacement)
              .putString("decision_brain_v05",decisionBrain.serialize())
              .putString("morph_brain_v07",encode(shapeTrainer.best))
              .putInt("morph_attempts_v07",shapeTrainer.attempts)
              .putInt("morph_accepted_v07",shapeTrainer.accepted)
              .putInt("morph_gate_attempts_v08",shapeTrainer.gateAttempts)
              .putInt("morph_gate_success_v08",shapeTrainer.gateAccepted)
              .putBoolean("scarcity_active_v08",scarcity)
              .putFloat("scarcity_elapsed_v08",elapsedLife)
              .putInt("room_success_v08",chambersCleared)
              .putInt("finite_energy_eaten_v08",energyEaten)
              .putInt("grip_events_v08",gripsEver+(chamber!=null?chamber.grasps:0))
              .putFloat("gate_travel_v08",Math.max(farthestGate,
                  chamber!=null?chamber.maxDoorTravel:0))
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
                            // Preserve the motor already learned in earlier
                            // versions; train extra manipulation muscles on
                            // measured physical outcomes, not scripted impulses.
                            // Shape trials run on copies of the same body and
                            // collision physics used onscreen. Prioritize them
                            // without discarding existing motor learning.
                            if(saved%29==0)
                                shapeTrainer.attemptGate(trainer.best,handTrainer.best);
                            else if(saved%7==0)
                                shapeTrainer.attempt(trainer.best,handTrainer.best);
                            else if(saved%5==0)
                                handTrainer.attempt(trainer.best);
                            else trainer.attempt();
                            if(++saved%35==0)save();
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
            // Only initial boot or actual simulated death restarts the body.
            creature.reset();surface.reset(creature);
            episode++;phase=0;trailCount=0;clock=0;
            chamber=null;
            environment=UnknownWorld.newHabitat(episode,creature.cx,creature.cy,scarcity);
            chosenObject=null;decisionBrain.abandon();
            goalTimer=0;contactsThisLife=0;worldTime=0;
            reserve=Math.max(42,Math.min(92,reserve));
            heading=episode*.31f;
        }
        void extendHabitat(){
            // Keep the same physical body, velocity, memories and camera.
            // Place additional objects outside the immediate viewport.
            episode++;phase=0;
            for(int i=environment.size()-1;i>=0;i--){
                UnknownWorld.Entity e=environment.get(i);
                if(e!= (chamber!=null?chamber.food:null)
                        && (e.consumed ||
                            Math.hypot(e.x-creature.cx,e.y-creature.cy)>600))
                    environment.remove(i);
            }
            environment.addAll(UnknownWorld.newHabitat(
                episode,creature.cx,creature.cy,scarcity));
            while(environment.size()>24){
                if(environment.get(0)==(chamber!=null?chamber.food:null)){
                    environment.remove(1);
                }else environment.remove(0);
            }
            if(chosenObject!=null&&!environment.contains(chosenObject)){
                decisionBrain.abandon();
                chosenObject=null;goalTimer=0;
            }
            lastEvent="NUOVA AREA ESPLORATA  •  ZERO NON SI AZZERA";
            eventTime=3;
        }
        void activateChamber(boolean manual){
            if(chamber!=null){
                gripsEver+=chamber.grasps;
                farthestGate=Math.max(farthestGate,chamber.maxDoorTravel);
                environment.remove(chamber.food);
            }
            scarcity=true;
            if(manual)reserve=Math.min(58,reserve);
            chamber=new ScarcityLab(creature.cx,creature.cy);
            environment.add(chamber.food);
            nextChamber=elapsedLife+95f;
            chosenObject=null;goalTimer=0;
            chamberMessage="RISORSA DIETRO LA PORTA: SERVE UNA PRESA";
            lastEvent="NUOVO ESPERIMENTO • STANZA E BLOCCO";
            eventTime=5;
            save();
        }
        void updateHabitat(float dt){
            worldTime+=dt;elapsedLife+=dt;goalTimer-=dt;
            if(!scarcity&&elapsedLife>=70f)scarcity=true;
            // Scarcity is explicit, increasing metabolic urgency.
            reserve=Math.max(0,reserve-(scarcity?.70f:.14f)*dt);
            if(scarcity&&chamber==null&&elapsedLife>=nextChamber)
                activateChamber(false);
            for(UnknownWorld.Entity object:environment)object.tick(dt);
            if(chosenObject==null||!chosenObject.ready()||goalTimer<=0){
                if(chosenObject!=null&&goalTimer<=0)decisionBrain.abandon();
                chosenObject=decisionBrain.choose(environment,creature.cx,creature.cy,reserve);
                if(chosenObject==null){
                    extendHabitat();
                    chosenObject=decisionBrain.choose(environment,creature.cx,creature.cy,reserve);
                }
                goalTimer=9f;
            }
            if(chosenObject!=null){
                heading=(float)Math.atan2(chosenObject.y-creature.cy,chosenObject.x-creature.cx);
            }else heading+=dt*.7f;
        }
        void resolveObjects(float dt){
            // Called AFTER the deformable body moves, so contact impulses
            // are consequences of its simulated physical movement.
            for(UnknownWorld.Entity object:environment){
                if(chamber!=null&&object==chamber.food)continue;
                if(object.isMovable()){
                    float impulse=Morphology.collide(surface,object,dt)
                        +ContactLearning.collide(creature,object,dt);
                    if(impulse>.65f){
                        object.everCollided=true;
                        if(object.contactCooldown<=0){
                            physicalContacts++;
                            contactsThisLife++;
                            object.contactCooldown=.75f;
                            mind.observe(object.signature,0,object.displacementEffect());
                        }
                    }
                    // Only report a success when an already-contacted object
                    // has demonstrably changed world position by >= 12 units.
                    // A mere collision or velocity spike does NOT qualify.
                    float shift=object.displacementFromSpawn();
                    object.peakDisplacement=Math.max(object.peakDisplacement,shift);
                    if(object.everCollided && !object.countedAsMoved && shift>=12f){
                        object.countedAsMoved=true;
                        pushes++;
                        decisionBrain.learn(object,reserve,reserve,shift);
                        if(chosenObject==object){chosenObject=null;goalTimer=0;}
                        biggestDisplacement=Math.max(biggestDisplacement,shift);
                        recentDisplacement=shift;
                        lastEvent="DISCO SPOSTATO "+(int)shift+" UNITA  •  TOTALE "+pushes;
                        eventTime=4f;save();
                    }
                    if(object.everCollided && shift>biggestDisplacement+1f){
                        biggestDisplacement=shift;
                    }
                    continue;
                }
                if(!object.ready())continue;
                float dx=object.x-creature.cx,dy=object.y-creature.cy;
                float dist=(float)Math.hypot(dx,dy);
                float nearest=dist;
                for(int k=0;k<MotorBrain.N;k++){
                    float d=(float)Math.hypot(object.x-creature.x[k],object.y-creature.y[k]);
                    if(d<nearest)nearest=d;
                }
                for(int k=0;k<Morphology.POINTS;k++){
                    float d=(float)Math.hypot(object.x-surface.x[k],object.y-surface.y[k]);
                    if(d<nearest)nearest=d;
                }
                if(nearest<18+object.radius){
                    float before=reserve;
                    float delta=object.touch(dx/Math.max(1,dist),dy/Math.max(1,dist));
                    reserve=Math.max(0,Math.min(100,reserve+delta));
                    mind.observe(object.signature,delta,0);
                    decisionBrain.learn(object,before,reserve,0);
                    if(delta>0)energyEaten++;
                    contactsThisLife++;chosenObject=null;goalTimer=0;
                    lastEvent="STIMOLO "+(object.signature+1)+"  ENERGIA "+(delta>=0?"+":"")+(int)delta;
                    eventTime=4;
                    if(delta<0){creature.cvx*=.6f;creature.cvy*=.6f;}
                    save();
                }
            }
            eventTime=Math.max(0,eventTime-dt);
            if(reserve<=0){
                if(chamber!=null){
                    gripsEver+=chamber.grasps;
                    farthestGate=Math.max(farthestGate,chamber.maxDoorTravel);
                }
                reserve=45;lastEvent="CORPO ESAUSTO • MEMORIA CONSERVATA";eventTime=6;
                resetEpisode();
                nextChamber=elapsedLife+12f;
                save();
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
                    float[] hand=(chosenObject!=null&&chosenObject.isMovable())
                        ?handTrainer.best:null;
                    creature.step(trainer.best,(float)Math.cos(heading),
                        (float)Math.sin(heading),slice,hand,
                        chosenObject!=null?chosenObject.x:0,
                        chosenObject!=null?chosenObject.y:0);
                    // No preset appendages: learned local actuation of each
                    // mass in the continuous deformable surface.
                    surface.step(creature,shapeTrainer.best,
                        chosenObject!=null?chosenObject.x:creature.cx,
                        chosenObject!=null?chosenObject.y:creature.cy,slice);
                    if(chamber!=null){
                        chamber.constrain(creature,surface,slice);
                        farthestGate=Math.max(farthestGate,chamber.maxDoorTravel);
                    }
                    resolveObjects(slice);
                    if(chamber!=null&&!chamber.reachedEnergy&&
                        chamber.foodTouchedByCore(creature)){
                        float prior=reserve;
                        reserve=Math.min(100,reserve+22);
                        energyEaten++;chambersCleared++;
                        decisionBrain.learn(chamber.food,prior,reserve,0);
                        chamberMessage="SUCCESSO: ZERO HA RAGGIUNTO L'ENERGIA";
                        lastEvent="RISORSA RAGGIUNTA DIETRO IL BLOCCO";
                        eventTime=7;
                        nextChamber=elapsedLife+40;
                        save();
                    }
                    phase+=slice;
                    if(phase>=13f){
                        // NOT a reset: the world expands around ZERO.
                        extendHabitat();
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
            if(chamber!=null){
                float x=chamber.entranceX(),cx=chamber.ox,cy=chamber.oy;
                paint.setShader(null);
                paint.setColor(0x1824b6b6);
                c.drawRect(x,cy+ScarcityLab.TOP,cx+ScarcityLab.END_X,
                    cy+ScarcityLab.BOTTOM,paint);
                paint.setColor(0xff4a7781);
                c.drawRect(x-7,cy+ScarcityLab.TOP-9,x+7,
                    cy-ScarcityLab.OPEN_HALF_HEIGHT,paint);
                c.drawRect(x-7,cy+ScarcityLab.OPEN_HALF_HEIGHT,x+7,
                    cy+ScarcityLab.BOTTOM+9,paint);
                c.drawRect(x-7,cy+ScarcityLab.TOP-9,cx+ScarcityLab.END_X+7,
                    cy+ScarcityLab.TOP+7,paint);
                c.drawRect(x-7,cy+ScarcityLab.BOTTOM-7,cx+ScarcityLab.END_X+7,
                    cy+ScarcityLab.BOTTOM+9,paint);
                c.drawRect(cx+ScarcityLab.END_X-7,cy+ScarcityLab.TOP-9,
                    cx+ScarcityLab.END_X+7,cy+ScarcityLab.BOTTOM+9,paint);
                paint.setColor(0xffdfb982);
                c.drawRoundRect(x-ScarcityLab.DOOR_HALF_W,chamber.gateTop(),
                    x+ScarcityLab.DOOR_HALF_W,chamber.gateBottom(),4,4,paint);
                paint.setColor(0xff0d1a22);
                c.drawCircle(x,chamber.gateCenterY(),3,paint);
                if(chamber.openingFree())
                    text(c,"APERTA",x,cy-57,10,TEAL,true);
                else text(c,"BLOCCATA",x,cy-57,9,GOLD,true);
            }
                        int[] colors={0xff64d8ca,0xffcc7da4,0xffd2b889};
            for(UnknownWorld.Entity object:environment){
                if(object.consumed)continue;
                float x=object.x,y=object.y;
                paint.setShader(new RadialGradient(x,y,35,new int[]{(object.ready()?0x2248dfc4:0x113d7377),0x003d7377},null,Shader.TileMode.CLAMP));
                c.drawCircle(x,y,35,paint);paint.setShader(null);
                // Object's original position remains visible in world space.
                // A line and an outline show actual physical displacement:
                // this is evidence, not an animation attached to ZERO.
                if(object.isMovable()){
                    float shifted=object.displacementFromSpawn();
                    if(shifted>3f){
                        paint.setShader(null);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(1.05f);
                        paint.setColor(0x7796dbcf);
                        c.drawLine(object.spawnX,object.spawnY,x,y,paint);
                        paint.setColor(0x66efbe76);
                        c.drawCircle(object.spawnX,object.spawnY,object.radius*.86f,paint);
                        paint.setStyle(Paint.Style.FILL);
                        if(shifted>=8){
                            text(c,(int)shifted+" U",x,y-object.radius-10,9,GOLD,true);
                        }
                    }
                }
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
            float[] xx=surface.x, yy=surface.y;
            path.reset();
            float a0=(xx[Morphology.POINTS-1]+xx[0])*.5f;
            float b0=(yy[Morphology.POINTS-1]+yy[0])*.5f;
            path.moveTo(a0,b0);
            for(int i=0;i<Morphology.POINTS;i++){
                int j=(i+1)%Morphology.POINTS;
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
            for(int i=0;i<Morphology.POINTS;i++){
                float dx=xx[i]-creature.cx,dy=yy[i]-creature.cy;
                float r=(float)Math.hypot(dx,dy);
                float stretch=surface.extension[i];
                float glowRadius=.55f+Math.min(2.5f,stretch*.035f);
                if(i%2==0){
                    paint.setColor(0x8867f6d5);
                    c.drawCircle(xx[i]-dx*.04f,yy[i]-dy*.04f,glowRadius,paint);
                }
                if(r>48){
                    paint.setColor(0x8c97f5dc);c.drawCircle(xx[i],yy[i],1.65f,paint);
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
            text(c,"ZERO / ESPERIMENTO 007",26,72,21,0xffe9faf6,false);
            rounded(c,339,104,459,138,0xff20444a,11);
            text(c,"AGGIORNA",399,127,12,TEAL,true);
            rounded(c,217,104,333,138,0xff283b58,11);
            text(c,"CERVELLO",275,127,12,0xffd2c4fc,true);
            text(c,"ENERGIA FINITA • PRESA BILATERALE",27,99,11,MUTED,false);
            text(c,"VERSIONE "+installedVersion+"  •  OTA",27,120,10,0xff708d9d,false);
            text(c,"TENTATIVI",377,34,11,MUTED,false);
            text(c,String.valueOf(trainer.attempts),380,71,22,GOLD,false);
            rounded(c,13,158,467,232,0xca0e1a25,17);
            text(c,"MOVIMENTO • PUNTEGGIO TRAINING",27,182,11,0xffb5d7d6,false);
            text(c,format(trainer.initialFitness)+" → "+format(trainer.bestFitness),27,210,20,0xffeffff8,false);
            text(c,"unità / episodio",283,212,11,MUTED,false);
            float y=H-168;
            rounded(c,12,y,468,H-12,0xe00d1923,21);
            text(c,"APPRENDIMENTO DA ZERO",25,y+25,13,TEAL,false);
            text(c,"96 masse • porta mobile solo con due contatti.",25,y+45,11,0xffacc5ca,false);
            button(c,19,y+61,153,H-26,learning?"FERMA":"ADDESTRA",TEAL);
            button(c,165,y+61,314,H-26,"TEMPO x"+speed,0xffbfdaec);
            button(c,326,y+61,462,H-26,"CONFRONTA",GOLD);
            text(c,simulationRunning?"ESPLORAZIONE AUTONOMA • CONTATTI "+mind.total:"SIMULAZIONE IN PAUSA",24,255,11,MUTED,false);
            rounded(c,25,276,456,287,0xff273746,5);
            rounded(c,25,276,25+431*reserve/100f,287,reserve>30?TEAL:0xffdf7591,5);
            text(c,"ENERGIA "+(int)reserve+"/100",25,307,12,0xffe0f7ed,false);
            text(c,"DISCHI SPOSTATI "+pushes,261,307,12,GOLD,false);
            text(c,"URTI "+physicalContacts+"  •  DISTANZA MASSIMA "+(int)biggestDisplacement+" U  •  TENTATIVI MANIP. "+handTrainer.attempts,
                25,329,10,0xffa6c7c9,false);
            if(eventTime>0)text(c,lastEvent,25,350,11,0xffc4e5dc,false);
            text(c,"SCELTE: "+decisionBrain.decisions+"  •  ESPERIENZE: "
                 +decisionBrain.updates+"  •  ESPLORAZIONI: "+decisionBrain.explorations,
                 25,377,11,TEAL,false);
            if(chosenObject!=null){
                text(c,"SCELTO DALLA RETE: SEGNALE "+(chosenObject.signature+1)
                    +"  •  PREVISIONE "+format(decisionBrain.lastPrediction),
                    25,398,10,0xffaac3cb,false);
            }
            text(c,"MEMBRANA "+Morphology.POINTS+"  •  ESTENSIONE "+
                  (int)surface.peakExtension+" U  •  PROVE PRESA "+
                  shapeTrainer.gateAttempts,
                  25,421,11,GOLD,false);
            text(c,(scarcity?"SCARSITA' ATTIVA":"SCORTE IN ESAURIMENTO")+
                "  •  ENERGIE PRESE "+energyEaten+"  •  STANZE RISOLTE "+chambersCleared,
                25,443,11,scarcity?0xffef9f91:TEAL,false);
            if(chamber!=null){
                text(c,"PORTA "+(int)chamber.doorY+" U  •  PRESE "+
                    (gripsEver+chamber.grasps)+"  •  DUE LATI "+
                    chamber.opposedContactFrames,25,465,11,GOLD,false);
            }else text(c,"TOCCA QUI PER ATTIVARE TEST STANZA E FAME",25,465,11,GOLD,false);
            if(uiTime<updaterStatusUntil&&updaterStatus.length()>0){
                rounded(c,13,338,467,387,0xef1f3944,13);
                text(c,updaterStatus.length()>52?updaterStatus.substring(0,52):updaterStatus,240,368,11,0xffd5fff4,true);
            }
            rounded(c,402,238,464,269,0xff233944,9);
            text(c,simulationRunning?"Ⅱ":"▶",433,259,18,0xffe0f7f3,true);
            if(showBenchmark){
                float top=H*.32f;
                rounded(c,20,top,460,top+294,0xf20e222e,22);
                text(c,"VERIFICA SU DIREZIONI NUOVE",240,top+31,16,TEAL,true);
                text(c,"MOVIMENTO • VERGINE  "+format(benchStart),240,top+69,14,MUTED,true);
                text(c,"MOVIMENTO • APPRESO  "+format(benchNow),240,top+97,14,GOLD,true);
                text(c,"MANIPOLAZIONE",240,top+146,17,TEAL,true);
                text(c,"PESI INIZIALI    "+format(handInitial),240,top+177,15,MUTED,true);
                text(c,"PESI APPRESI     "+format(handTrained),240,top+207,15,GOLD,true);
                text(c,benchWorking?"TEST IN ESECUZIONE...":Float.isFinite(handTrained)
                    ?(handTrained>handInitial?"VANTAGGIO MISURATO":"NESSUN VANTAGGIO MISURATO")
                    :"IN ATTESA",240,top+244,12,handTrained>handInitial?TEAL:GOLD,true);
                text(c,"TOCCA PER CHIUDERE",240,top+278,11,MUTED,true);
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
            float[] handLearned=handTrainer.best.clone();
            new Thread(()->{
                float a=MotorBrain.benchmark(fresh),b=MotorBrain.benchmark(learned);
                float c=ContactLearning.heldout(learned,ContactLearning.virgin());
                float d=ContactLearning.heldout(learned,handLearned);
                post(()->{
                    benchStart=a;benchNow=b;handInitial=c;handTrained=d;
                    benchWorking=false;invalidate();
                });
            },"zero-benchmark").start();
        }
        @Override public boolean onTouchEvent(MotionEvent ev){
            if(ev.getActionMasked()!=MotionEvent.ACTION_UP)return true;
            float scale=getWidth()/W;
            float x=ev.getX()/scale,y=ev.getY()/scale;
            if(showBenchmark){showBenchmark=false;return true;}
            if(y>442&&y<480&&x<380){
                activateChamber(true);
                invalidate();return true;
            }
            if(y>=H-108){
                if(x<157){learning=!learning;}
                else if(x<319){speed=speed==1?5:speed==5?20:1;}
                else compare();
                return true;
            }
            if(y>=104&&y<145&&x>=339){
                updater.checkAndInstall();
                return true;
            }
            if(y>=104&&y<145&&x>=217&&x<334){
                save();
                GameActivity.this.startActivity(
                    new android.content.Intent(GameActivity.this,BrainStatsActivity.class));
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
