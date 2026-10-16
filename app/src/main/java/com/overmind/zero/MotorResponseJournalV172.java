package com.overmind.zero;
import java.util.*;
/** Bounded passive flight record of the actual command and post-contact body
 * response. No scoring, random generator, actuation, learning or coordinates. */
public final class MotorResponseJournalV172 {
    public static final String KEY="motor_response_journal_v172";
    private static final int LIMIT=128,FIELDS=21;
    private final ArrayDeque<String> records=new ArrayDeque<>();
    private long completed=0;private int action=-1,age=0,previous=-1,frames=0;
    private float seconds,dx,dy,gross,startVx,startVy,endVx,endVy,impulse;
    private boolean external;
    private final float[] command=new float[4];
    public void begin(int selected,MotorBrain.Body b){
        action=selected;age=selected==previous?age+1:1;previous=selected;
        frames=0;seconds=dx=dy=gross=impulse=0;external=false;Arrays.fill(command,0);
        startVx=b==null?0:b.cvx;startVy=b==null?0:b.cvy;endVx=startVx;endVy=startVy;
    }
    public void frame(float x,float y,MotorBrain.Body b,float[] controls,float active,boolean disturbed,float dt){
        if(action<0||b==null||controls==null||controls.length!=64||!Float.isFinite(dt)||dt<=0)return;
        float mx=b.cx-x,my=b.cy-y;if(!Float.isFinite(mx)||!Float.isFinite(my))return;
        dx+=mx;dy+=my;gross+=(float)Math.hypot(mx,my);endVx=b.cvx;endVy=b.cvy;
        seconds+=dt;impulse+=Math.max(0,active);external|=disturbed;frames++;
        for(int i=0;i<64;i++)command[i%4]+=controls[i]/16f;
    }
    private static int q(float x){return Float.isFinite(x)?Math.round(Math.max(-30000f,Math.min(30000f,x*1000f))):0;}
    public void finish(boolean train,boolean held,boolean directionalEligible,boolean fastProbe){
        if(action<0||frames==0){action=-1;return;}
        StringBuilder s=new StringBuilder();
        int[]v={action,age,frames,q(seconds),q(startVx),q(startVy),q(endVx),q(endVy),q(dx),q(dy),q(gross),q(impulse),external?1:0,train?1:0,held?1:0,directionalEligible?1:0,fastProbe?1:0,q(command[0]/frames),q(command[1]/frames),q(command[2]/frames),q(command[3]/frames)};
        for(int i:v){if(s.length()>0)s.append(',');s.append(i);}records.add(s.toString());while(records.size()>LIMIT)records.removeFirst();completed++;action=-1;
    }
    public void resetLife(){action=previous=-1;age=0;frames=0;}
    /** Contact alone is proprioceptive evidence, not exogenous motion.
     * Reject a locomotor trial only for measured forced displacement,
     * externally moved objects, storms, or an actually held surface.
     * Invalid measurements fail closed; this method never changes physics. */
    public static boolean externalMotionContaminated(float correctionX,
            float correctionY,float worldEffectDelta,boolean storm,boolean held){
        return !Float.isFinite(correctionX)||!Float.isFinite(correctionY)||
            !Float.isFinite(worldEffectDelta)||
            Math.hypot(correctionX,correctionY)>.08f||
            worldEffectDelta>.03f||storm||held;
    }
    public long completed(){return completed;}
    public String encode(){StringBuilder s=new StringBuilder("mr172|").append(completed).append('\n');for(String r:records)s.append(r).append('\n');return s.toString();}
    public boolean restore(String raw){
        if(raw==null||raw.length()>50000||!raw.startsWith("mr172|"))return false;
        try{String[]rows=raw.split("\n",-1);long count=Long.parseLong(rows[0].substring(6));if(count<0||count>1000000000L||rows.length>LIMIT+2||rows.length<2||!rows[rows.length-1].isEmpty())return false;
            ArrayDeque<String> parsed=new ArrayDeque<>();
            for(int i=1;i<rows.length-1;i++){String[]f=rows[i].split(",",-1);if(f.length!=FIELDS)return false;int[]v=new int[FIELDS];for(int j=0;j<FIELDS;j++){v[j]=Integer.parseInt(f[j]);if(Math.abs((long)v[j])>1000000000L)return false;}
                if(v[0]<0||v[0]>=16||v[1]<1||v[2]<1||v[3]<0)return false;
                for(int j=12;j<=16;j++)if(v[j]<0||v[j]>1)return false;
                parsed.add(rows[i]);}
            if(parsed.size()>count)return false;records.clear();records.addAll(parsed);completed=count;resetLife();return true;
        }catch(RuntimeException bad){return false;}
    }
    public String summary(){return "Risposte fisiche registrate: "+completed+". Ultimi "+records.size()+" comandi disponibili nei dati condivisi: durata effettiva, velocità prima/dopo, spostamento, disturbi esterni e credito motorio.";}
}
