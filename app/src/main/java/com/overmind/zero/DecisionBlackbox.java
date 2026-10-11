package com.overmind.zero;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * 0116.2: passive, bounded last-five-minutes decision flight recorder.
 * No randomness, learning, action selection, or world/reward modifications.
 * Samples only already computed decision terms and REAL finished movements.
 */
public final class DecisionBlackbox {
    public static final String CURRENT="decision_flight_current_v1162";
    public static final String LAST="decision_flight_last_v1162";
    public static final int TERMS=14, SENSES=SensorimotorMind.S;
    public static final int MAX_ENTRIES=475, MAX_TEXT=145000;
    private static final float WINDOW=300f;
    // Score terms in order, identical to SensorimotorMind.choose() summands.
    public static final String[] TERM_NAMES={
        "legacyQ","energeticCredit","threeStepLookahead","curiosity","jitter",
        "episodic","antiStall","temporal","temporalSkill","proprioception",
        "spatial","causal","instrumental","goalContinuity"
    };
    private final ArrayDeque<Record> events=new ArrayDeque<>();
    private Pending pending;
    private float time=0f;
    private String last="";
    private static final class Pending{
        int action,greedy;
        boolean exploration,guard,position;
        float epsilon,score,greedyScore,x,y;
        final short[] senses=new short[SENSES],terms=new short[TERMS];
    }
    private static final class Record{
        int tick,action,greedy,exploration,guard,terminal,motion;
        int eps,score,greedyScore,energyBefore,energyAfter,contactEnergy;
        int physical,dx,dy,error;
        final short[] senses=new short[SENSES],terms=new short[TERMS];
        String serialize(){
            StringBuilder b=new StringBuilder(250);
            int[] v={tick,action,greedy,exploration,guard,terminal,motion,
                eps,score,greedyScore,energyBefore,energyAfter,contactEnergy,
                physical,dx,dy,error};
            for(int x:v){if(b.length()>0)b.append(',');b.append(x);}
            for(short v1:senses)b.append(',').append(v1);
            for(short v1:terms)b.append(',').append(v1);
            return b.toString();
        }
        static Record parse(String s){
            String[] f=s.split(",",-1);
            if(f.length!=17+SENSES+TERMS)throw new IllegalArgumentException("trace fields");
            int[] nums=new int[f.length];
            for(int i=0;i<f.length;i++)nums[i]=Integer.parseInt(f[i]);
            if(nums[0]<0||nums[1]<0||nums[1]>=16||nums[2]<0||nums[2]>=16||
                nums[3]<0||nums[3]>1||nums[4]<0||nums[4]>1||
                nums[5]<0||nums[5]>1||nums[6]<0||nums[6]>1||
                nums[7]<0||nums[7]>1000)throw new IllegalArgumentException("invalid trace");
            Record r=new Record();
            r.tick=nums[0];r.action=nums[1];r.greedy=nums[2];
            r.exploration=nums[3];r.guard=nums[4];r.terminal=nums[5];
            r.motion=nums[6];r.eps=nums[7];r.score=nums[8];
            r.greedyScore=nums[9];r.energyBefore=nums[10];r.energyAfter=nums[11];
            r.contactEnergy=nums[12];r.physical=nums[13];r.dx=nums[14];
            r.dy=nums[15];r.error=nums[16];
            for(int i=0;i<SENSES;i++){
                if(nums[17+i]<-1600||nums[17+i]>1600)throw new IllegalArgumentException("sensor");
                r.senses[i]=(short)nums[17+i];
            }
            for(int i=0;i<TERMS;i++){
                int k=nums[17+SENSES+i];
                if(k<-30000||k>30000)throw new IllegalArgumentException("advisor");
                r.terms[i]=(short)k;
            }
            return r;
        }
    }
    private static int quant(float v,int scale){
        if(!Float.isFinite(v))return 0;
        return Math.round(Math.max(-30000f,Math.min(30000f,v*scale)));
    }
    private static short qShort(float v){return (short)quant(v,1000);}
    public void tick(float seconds){
        if(Float.isFinite(seconds)&&seconds>0&&seconds<=1f)
            time+=seconds;
    }
    public void chosen(float[] senses,int chosen,int greedy,boolean exploration,
                       boolean guard,float epsilon,float selectedScore,
                       float greedyScore,float[] terms,MotorBrain.Body body){
        if(senses==null||senses.length!=SENSES||terms==null||
           terms.length!=TERMS||chosen<0||chosen>=16||greedy<0||greedy>=16)return;
        Pending p=new Pending();
        p.action=chosen;p.greedy=greedy;p.exploration=exploration;p.guard=guard;
        p.epsilon=epsilon;p.score=selectedScore;p.greedyScore=greedyScore;
        for(int i=0;i<SENSES;i++)p.senses[i]=qShort(senses[i]);
        for(int i=0;i<TERMS;i++)p.terms[i]=qShort(terms[i]);
        if(body!=null&&Float.isFinite(body.cx)&&Float.isFinite(body.cy)){
            p.position=true;p.x=body.cx;p.y=body.cy;
        }
        pending=p;
    }
    /** A completed real 0.65s motor interval, NOT an imagined rollout.
     * No movement is invented when a JVM test supplies a null physical body. */
    public void finished(float[] after,float actualContactEnergy,float physicalEffect,
                         float modelError,MotorBrain.Body body){
        capture(after,actualContactEnergy,physicalEffect,modelError,body,false);
    }
    public void terminal(float[] after,float actualContactEnergy,float physicalEffect,
                         MotorBrain.Body body){
        capture(after,actualContactEnergy,physicalEffect,Float.NaN,body,true);
        last=encodeCurrent();
        pending=null;
    }
    private void capture(float[] after,float contact,float effect,float error,
                         MotorBrain.Body body,boolean death){
        Pending p=pending;
        if(p==null||after==null||after.length!=SENSES)return;
        Record r=new Record();
        r.tick=Math.max(0,Math.round(time*100));
        r.action=p.action;r.greedy=p.greedy;
        r.exploration=p.exploration?1:0;r.guard=p.guard?1:0;
        r.terminal=death?1:0;
        r.eps=quant(p.epsilon,1000);
        r.score=quant(p.score,1000);r.greedyScore=quant(p.greedyScore,1000);
        r.energyBefore=p.senses[0];
        r.energyAfter=quant(after[0],1000);
        r.contactEnergy=quant(contact,100);
        r.physical=quant(effect,100);
        r.error=quant(error,1000);
        if(p.position&&body!=null&&Float.isFinite(body.cx)&&Float.isFinite(body.cy)){
            r.motion=1;r.dx=quant(body.cx-p.x,10);
            r.dy=quant(body.cy-p.y,10);
        }
        System.arraycopy(p.senses,0,r.senses,0,SENSES);
        System.arraycopy(p.terms,0,r.terms,0,TERMS);
        events.addLast(r);
        while(events.size()>MAX_ENTRIES||(!events.isEmpty()&&
              r.tick-events.peekFirst().tick>Math.round(WINDOW*100)))
            events.removeFirst();
        pending=null;
    }
    public void newLife(){time=0;events.clear();pending=null;}
    /** Saved scene resumes existing trace. First partial action is omitted
     * since its original score breakdown is not persisted bit-exactly. */
    public void resumeLife(){pending=null;}
    public int currentCount(){return events.size();}
    public String lastLife(){return last;}
    public int lastCount(){
        if(last==null||last.isEmpty())return 0;
        int idx=last.indexOf('|',4);
        if(idx<0)return 0;
        try{return Integer.parseInt(last.substring(idx+1,last.indexOf('\n',idx))); }
        catch(Exception bad){return 0;}
    }
    public String encodeCurrent(){
        // Remove oldest entries only if required by the explicit export cap.
        ArrayList<String> lines=new ArrayList<>();
        int bytes=0;
        for(Record record:events){String line=record.serialize();lines.add(line);bytes+=line.length()+1;}
        int start=0;
        while(bytes>MAX_TEXT-1000&&start<lines.size())
            bytes-=lines.get(start++).length()+1;
        StringBuilder b=new StringBuilder(bytes+90);
        b.append("db1|").append(Math.max(0,Math.round(time*100))).append('|')
         .append(lines.size()-start).append('\n');
        for(int i=start;i<lines.size();i++)b.append(lines.get(i)).append('\n');
        return b.toString();
    }
    public boolean restoreCurrent(String encoded){
        if(encoded==null||encoded.isEmpty())return false;
        try{
            if(encoded.length()>MAX_TEXT||!encoded.startsWith("db1|"))
                return false;
            String[] lines=encoded.split("\n",-1);
            String[] head=lines[0].split("\\|",-1);
            if(head.length!=3||!head[0].equals("db1"))return false;
            int hundredths=Integer.parseInt(head[1]),count=Integer.parseInt(head[2]);
            if(hundredths<0||count<0||count>MAX_ENTRIES||lines.length!=count+2||
               !lines[lines.length-1].isEmpty())return false;
            ArrayDeque<Record> checked=new ArrayDeque<>();
            int tick=-1;
            for(int i=0;i<count;i++){
                Record row=Record.parse(lines[i+1]);
                if(row.tick<tick||row.tick>hundredths) return false;
                tick=row.tick;checked.addLast(row);
            }
            events.clear();events.addAll(checked);
            time=hundredths/100f;pending=null;
            return true;
        }catch(RuntimeException bad){return false;}
    }
    public boolean restoreLast(String encoded){
        if(encoded==null||encoded.isEmpty())return false;
        DecisionBlackbox candidate=new DecisionBlackbox();
        if(!candidate.restoreCurrent(encoded))return false;
        last=encoded;return true;
    }
    private static void require(boolean yes,String message){if(!yes)throw new AssertionError(message);}
    public static void main(String[] args){
        DecisionBlackbox rec=new DecisionBlackbox();
        float[] senses=new float[SENSES],parts=new float[TERMS];
        senses[0]=.7f;senses[14]=.7f;senses[18]=.9f;
        parts[0]=.30f;parts[12]=-.7f;
        require(rec.currentCount()==0,"must not invent experiences");
        MotorBrain.Body body=new MotorBrain.Body();body.cx=10;body.cy=0;
        for(int i=0;i<510;i++){
            rec.chosen(senses,0,2,i%10==0,i%7==0,.09f,-.2f,.3f,parts,body);
            rec.tick(.65f);
            body.cx+=3f;
            rec.finished(senses,0,0,.12f,body);
        }
        require(rec.currentCount()<=MAX_ENTRIES,"ring cap");
        String active=rec.encodeCurrent();
        require(active.length()<=MAX_TEXT,"profile must fit under cap");
        require(rec.restoreCurrent(active)&&active.equals(rec.encodeCurrent()),
            "saved ring trip not identical");
        rec.chosen(senses,4,0,true,false,.09f,-.5f,.6f,parts,body);
        rec.tick(.1f);
        senses[0]=0;
        rec.terminal(senses,-7,0,body);
        require(rec.lastLife().contains("db1|"),"last-life freeze");
        String last=rec.lastLife();
        rec.newLife();
        require(rec.currentCount()==0&&rec.lastLife().equals(last),
            "death must clear only current live trace");
        DecisionBlackbox loaded=new DecisionBlackbox();
        require(loaded.restoreLast(last)&&loaded.restoreCurrent(active),
            "named save trace not restorable");
        String old=loaded.encodeCurrent();
        require(!loaded.restoreCurrent(old+"corruption")&&old.equals(loaded.encodeCurrent()),
            "bad trace damaged previous valid snapshots");
        loaded.resumeLife();
        require(loaded.currentCount()>0,"resume erased five-minute ring");
        System.out.println("ZERO decision blackbox 0116.2 PASS: bounded five minutes, sensed advisors, real movement, death snapshot, save migration, no actuation");
    }
}
