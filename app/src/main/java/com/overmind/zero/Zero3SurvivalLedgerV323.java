package com.overmind.zero;

import java.io.*;
import java.util.*;

/**
 * V3.23 lab observer of EXISTING GameActivity energy equations.
 * No invented contact reward or navigation; never feeds privileged object
 * identity back to the actor. These values are NOT yet in V315 sensors.
 */
public final class Zero3SurvivalLedgerV323 {
    static final int MAGIC=0x5A333233,VERSION=323,RECORD_SIZE=4+8*2+4*6+4*3;
    private float reserve,elapsed,worldTime;
    private boolean scarcity,active,variableClock;
    public void enableVariableClockV352(){good(!active&&next==0,"clock must be chosen at birth");variableClock=true;}
    private int next,positiveTouches,negativeTouches,consumedSources;
    private float basalSpent,motorSpent,energyGained,negativeContactEnergy;
    private float decisionBefore,decisionBasal,decisionMotor,decisionGain,decisionDamage;
    private long decisionStartTick;
    private final ArrayList<Trace> history=new ArrayList<>();
    public Zero3SurvivalLedgerV323(float initial,float elapsed,float worldTime,boolean scarce){
        if(!Float.isFinite(initial)||initial<0||initial>100||
           !Float.isFinite(elapsed)||elapsed<0||!Float.isFinite(worldTime)||
           worldTime<0)throw new IllegalArgumentException("invalid energy initial conditions");
        reserve=initial;this.elapsed=elapsed;this.worldTime=worldTime;scarcity=scarce;
    }
    public static final class Trace{
        final int decision,positive,negative,consumed;
        final long startTick,endTick;
        final float before,after,basal,motor,gained,damage;
        Trace(int n,long from,long to,float pre,float post,float basal,
              float motor,float gained,float damage,int positive,int negative,
              int consumed){this(n,from,to,pre,post,basal,motor,gained,damage,positive,negative,consumed,false);}
        Trace(int n,long from,long to,float pre,float post,float basal,float motor,float gained,float damage,int positive,int negative,int consumed,boolean variable){
            if(n<0||(variable?(to-from<1||to-from>26):to-from!=12)||!Float.isFinite(pre)||!Float.isFinite(post)||
               pre<0||pre>100||post<0||post>100)
                throw new IllegalArgumentException("bad energy trace");
            decision=n;startTick=from;endTick=to;before=pre;after=post;
            this.basal=basal;this.motor=motor;this.gained=gained;
            this.damage=damage;this.positive=positive;this.negative=negative;
            this.consumed=consumed;
        }
        void write(DataOutputStream d)throws IOException {
            d.writeInt(decision);d.writeLong(startTick);d.writeLong(endTick);
            d.writeFloat(before);d.writeFloat(after);d.writeFloat(basal);
            d.writeFloat(motor);d.writeFloat(gained);d.writeFloat(damage);
            d.writeInt(positive);d.writeInt(negative);d.writeInt(consumed);
        }
        static Trace read(DataInputStream d)throws IOException{return read(d,false);}
        static Trace read(DataInputStream d,boolean variable)throws IOException{
            return new Trace(d.readInt(),d.readLong(),d.readLong(),d.readFloat(),
                 d.readFloat(),d.readFloat(),d.readFloat(),d.readFloat(),
                 d.readFloat(),d.readInt(),d.readInt(),d.readInt(),variable);
        }
    }
    private static void good(boolean ok,String why) {
        if(!ok)throw new IllegalStateException("V323 "+why);
    }
    private float d(int kind){
        return Math.max(0f,Math.min(reserve,kind==0?.09f:.30f));
    }
    public void begin(int decision,long gelTicks){
        good(!active&&decision==next&&gelTicks>=0,"noncontiguous decision start");
        active=true;decisionStartTick=gelTicks;decisionBefore=reserve;
        decisionBasal=decisionMotor=decisionGain=decisionDamage=0;
        partialSteps=0;
        thisDecisionPositive=thisDecisionNegative=thisDecisionConsumed=0;
    }
    private int partialSteps,thisDecisionPositive,thisDecisionNegative,thisDecisionConsumed;
    /** Called AFTER actual gel.step, never before computing true work. */
    public void physicalStep(float dt,float actualWork,
                             MotorBrain.Body b,Morphology.Surface skin,
                             List<UnknownWorld.Entity> objects){physicalStep(dt,actualWork,b,skin,objects,0f);}
    public void physicalStep(float dt,float actualWork,MotorBrain.Body b,Morphology.Surface skin,
                             List<UnknownWorld.Entity> objects,float measuredWeatherLoss){
        good(Float.isFinite(measuredWeatherLoss)&&measuredWeatherLoss>=0,"invalid weather outcome");
        good(active&&dt>0&&dt<=.04f&&Float.isFinite(dt)&&
             actualWork>=0&&Float.isFinite(actualWork)&&
             partialSteps<(variableClock?26:12)&&b!=null&&skin!=null&&objects!=null,
             "invalid real physics step");
        worldTime+=dt;elapsed+=dt;
        if(!scarcity&&elapsed>=70f)scarcity=true;
        // GameActivity.updateHabitat exact basal drain; positive costs are
        // always debited BEFORE any material resource contact in that frame.
        float basalBefore=reserve;
        reserve=Math.max(0f,reserve-(scarcity?.30f:.09f)*dt);
        float actualBasal=basalBefore-reserve;
        basalSpent+=actualBasal;decisionBasal+=actualBasal;
        float fuel=Math.min(reserve,actualWork*8f);
        reserve-=fuel;motorSpent+=fuel;decisionMotor+=fuel;
        // Reuse the same collision/consumption code as GameActivity:
        // MaterialGeometry.touches, entity.ready, entity.touch and clamp.
        for(UnknownWorld.Entity item:objects){
            if(variableClock&&reserve<=0f)break; // death is terminal even within this physical frame
            if(item.concealed||item.isBox()||item.isMovable()||!item.ready())
                continue;
            if(!MaterialGeometry.touches(b,skin,item.x,item.y,item.radius+2f))
                continue;
            float dx=item.x-b.cx,dy=item.y-b.cy;
            float len=(float)Math.hypot(dx,dy);
            float raw=item.touch(dx/Math.max(1f,len),dy/Math.max(1f,len));
            float before=reserve;
            reserve=Math.max(0f,Math.min(100f,reserve+raw));
            float realDelta=reserve-before;
            if(realDelta>0){
                energyGained+=realDelta;decisionGain+=realDelta;
                positiveTouches++;thisDecisionPositive++;
                if(item.consumed){consumedSources++;thisDecisionConsumed++;}
            }else if(realDelta<0){
                negativeContactEnergy-=realDelta;
                decisionDamage-=realDelta;
                negativeTouches++;thisDecisionNegative++;
            }
        }
        float weatherDebit=Math.min(reserve,measuredWeatherLoss);
        reserve-=weatherDebit;negativeContactEnergy+=weatherDebit;decisionDamage+=weatherDebit;
        partialSteps++;
        good(Float.isFinite(reserve)&&reserve>=0&&reserve<=100,
             "metabolic value nonfinite/overflow");
    }
    public void finish(int decision,long gelTicks){
        good(active&&decision==next&&gelTicks-decisionStartTick==partialSteps&&
             (variableClock?partialSteps>=1&&partialSteps<=26:partialSteps==12),"incomplete decision cannot commit energy");
        history.add(new Trace(decision,decisionStartTick,gelTicks,decisionBefore,
                 reserve,decisionBasal,decisionMotor,decisionGain,decisionDamage,
                 thisDecisionPositive,thisDecisionNegative,thisDecisionConsumed,variableClock));
        active=false;next++;
    }
    public int decisions(){return next;}
    public float worldTimeV354(){return worldTime;}
    public float reserve(){return reserve;}
    /** Only the PRIOR completed physical event may enter PRE sensing. */
    public float previousDecisionDamage(){
        good(!active,"cannot observe unfinished current metabolic decision");
        return history.isEmpty()?0f:history.get(history.size()-1).damage;
    }
    /** Real energy spent on adverse material contacts across this life. */
    public float observedDamageTotal(){
        good(!active,"unfinished energy observation");
        return negativeContactEnergy;
    }
    public boolean scarcity(){return scarcity;}
    public float basalSpent(){return basalSpent;}
    public float motorSpent(){return motorSpent;}
    public float energyGained(){return energyGained;}
    public float damagedEnergy(){return negativeContactEnergy;}
    public int positiveTouches(){return positiveTouches;}
    public int negativeTouches(){return negativeTouches;}
    public int consumedSources(){return consumedSources;}
    public State checkpoint(){
        good(!active&&next==history.size(),"cannot snapshot unfinished metabolism");
        return new State(reserve,elapsed,worldTime,scarcity,next,positiveTouches,
                         negativeTouches,consumedSources,basalSpent,motorSpent,
                         energyGained,negativeContactEnergy,history,variableClock);
    }
    public void restore(State s){
        good(s!=null&&!active&&next==0&&history.isEmpty(),
             "metabolic restore requires virgin observer");
        variableClock=s.variableClock;reserve=s.reserve;elapsed=s.elapsed;worldTime=s.worldTime;scarcity=s.scarcity;
        next=s.next;positiveTouches=s.positive;negativeTouches=s.negative;
        consumedSources=s.consumed;basalSpent=s.basal;motorSpent=s.motor;
        energyGained=s.gained;negativeContactEnergy=s.damage;
        history.addAll(s.history);
    }
    public static final class State{
        final float reserve,elapsed,worldTime,basal,motor,gained,damage;
        final boolean scarcity,variableClock;
        final int next,positive,negative,consumed;
        final ArrayList<Trace> history;
        State(float reserve,float elapsed,float worldTime,boolean scarcity,
              int n,int p,int neg,int consumed,float basal,float motor,
              float gained,float damage,List<Trace> history){
            this(reserve,elapsed,worldTime,scarcity,n,p,neg,consumed,basal,motor,gained,damage,history,false);
        }
        State(float reserve,float elapsed,float worldTime,boolean scarcity,int n,int p,int neg,int consumed,float basal,float motor,float gained,float damage,List<Trace> history,boolean variable){
            variableClock=variable;
            if(!Float.isFinite(reserve)||reserve<0||reserve>100||
               !Float.isFinite(elapsed)||elapsed<0||
               !Float.isFinite(worldTime)||worldTime<0||
               n<0||history==null||history.size()!=n||
               p<0||neg<0||consumed<0||consumed>p)
                throw new IllegalArgumentException("invalid metabolic snapshot");
            for(float x:new float[]{basal,motor,gained,damage})
                if(!Float.isFinite(x)||x<0)
                    throw new IllegalArgumentException("invalid metabolic cost");
            this.reserve=reserve;this.elapsed=elapsed;this.worldTime=worldTime;
            this.scarcity=scarcity;next=n;positive=p;negative=neg;
            this.consumed=consumed;this.basal=basal;this.motor=motor;
            this.gained=gained;this.damage=damage;
            this.history=new ArrayList<>(history);
            for(int i=0;i<n;i++){
                Trace t=this.history.get(i);
                if(t.decision!=i||(i>0&&t.startTick!=this.history.get(i-1).endTick))
                    throw new IllegalArgumentException("noncontiguous survival replay");
            }
        }
        byte[] encoded()throws IOException{
            ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream d=new DataOutputStream(b);
            d.writeInt(MAGIC);d.writeInt(variableClock?352:VERSION);
            d.writeFloat(reserve);d.writeFloat(elapsed);d.writeFloat(worldTime);
            d.writeBoolean(scarcity);d.writeInt(next);d.writeInt(positive);
            d.writeInt(negative);d.writeInt(consumed);
            d.writeFloat(basal);d.writeFloat(motor);d.writeFloat(gained);d.writeFloat(damage);
            for(Trace t:history)t.write(d);d.flush();return b.toByteArray();
        }
        static State decoded(byte[] bytes)throws IOException{
            if(bytes==null||bytes.length<53||bytes.length>600053)
                throw new IOException("invalid V323 state payload length");
            DataInputStream d=new DataInputStream(new ByteArrayInputStream(bytes));
            if(d.readInt()!=MAGIC)throw new IOException("metabolic magic");
            int schema=d.readInt();
            if(schema!=VERSION&&schema!=352)throw new IOException("incompatible metabolic schema");
            float r=d.readFloat(),elapsed=d.readFloat(),world=d.readFloat();
            boolean scarcity=d.readBoolean();int n=d.readInt(),p=d.readInt(),
                neg=d.readInt(),consumed=d.readInt();
            float basal=d.readFloat(),motor=d.readFloat(),gain=d.readFloat(),loss=d.readFloat();
            if(n<0||n>10000||bytes.length!=53+56*n)
                throw new IOException("metabolic count/length mismatch");
            ArrayList<Trace> trace=new ArrayList<>();
            try{
                for(int i=0;i<n;i++)trace.add(Trace.read(d,schema==352));
                return new State(r,elapsed,world,scarcity,n,p,neg,consumed,
                                 basal,motor,gain,loss,trace,schema==352);
            }catch(IllegalArgumentException x){throw new IOException("invalid metabolic trace",x);}
        }
    }
}
