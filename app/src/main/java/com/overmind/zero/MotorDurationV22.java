package com.overmind.zero;

import java.util.Locale;
import java.util.Random;

/**
 * ZERO172: experimentally complete physical motor options before judging them.
 * A random new proposal waits until the option boundary; harm or a strong
 * current neural alternative still interrupts immediately. Censored options
 * cannot masquerade as evidence for their originally selected duration.
 * ZERO 160: learn how long a self-selected 2.2 motor action is worth executing.
 * This is an action-duration controller, NOT a second action selector.
 * It never receives object identity, destination, target direction or colour.
 * Only real body motion, paid muscle energy and actual contact outcomes score
 * the temporal motor experiment. A separate heldout fifth never trains.
 */
public final class MotorDurationV22 {
    public static final String KEY="motor_duration_v160";
    public static final int ACTIONS=RecurrentCoreV22.A, OPTIONS=3;
    private static final int[] TICKS={1,2,4};
    private static final int CELLS=ACTIONS*OPTIONS;
    private final int[] trained=new int[CELLS],verified=new int[CELLS];
    private final float[] trainedMean=new float[CELLS],verifiedMean=new float[CELLS];
    // OTA165: new, independently heldout directional evidence. Historical
    // md160 reward means are preserved, never relabeled as direction evidence.
    private final int[] headingTrained=new int[CELLS],headingVerified=new int[CELLS];
    private final float[] headingTrainMean=new float[CELLS],headingVerifyMean=new float[CELLS];
    private final Random random=new Random(0x16022b0dL);
    private int selected=-1,option=-1,remaining,intervals,started;
    private boolean auditOnly;
    private float startX,startY,lastX,lastY,gross,gain,damage,cost,effect;
    private boolean hasPosition;
    private long choices,continuesCount,interruptsCount,episodes;
    private static float clip(float v,float low,float high){
        return Math.max(low,Math.min(high,v));
    }
    /** Keep learned estimates across deaths; censor the interrupted burst. */
    public void resetLife(){selected=-1;option=-1;remaining=intervals=0;hasPosition=false;}
    public void resumeLife(){resetLife();}
    public void interval(int performed,float x,float y,float energy,
                         float harm,float muscleCost,float movedObject){
        if(performed!=selected||option<0)return;
        intervals++;
        if(Float.isFinite(x)&&Float.isFinite(y)){
            if(hasPosition)gross+=Math.min(140f,(float)Math.hypot(x-lastX,y-lastY));
            else{startX=x;startY=y;hasPosition=true;}
            lastX=x;lastY=y;
        }
        if(Float.isFinite(energy))gain+=clip(energy,-100f,100f);
        if(Float.isFinite(harm))damage+=clip(harm,0,100f);
        if(Float.isFinite(muscleCost))cost+=clip(muscleCost,0,100f);
        if(Float.isFinite(movedObject))effect+=clip(movedObject,0,100f);
    }
    private void finish(){
        // An interrupted two/four-tick intervention was NOT a test of that
        // duration. Keep historical counters but do not train on censoring.
        if(selected<0||option<0||intervals!=TICKS[option])return;
        int at=selected*OPTIONS+option;
        float net=hasPosition?(float)Math.hypot(lastX-startX,lastY-startY):0f;
        // Measured motor skill quality ONLY; no motion reward enters the 2.2
        // value learner, and no attraction/aversion is hardcoded by signature.
        float speed=clip(net/(12f*intervals),0f,1f);
        float coherence=gross>1f?clip(net/gross,0f,1f):0f;
        float score=clip(.25f*speed*coherence+
            .55f*clip(Math.max(0f,gain)/(22f*intervals),0f,2f)+
            .12f*clip(effect/(8f*intervals),0f,1f)-
            .60f*clip(Math.max(Math.max(0f,-gain),damage),0f,35f)/35f-
            .15f*clip(cost/(2f*intervals),0f,1f),-1.5f,1.5f);
        // An action whose body goes sideways/backwards is NOT a successful
        // motor duration merely because it spent little muscle fuel. This
        // direction is the 2.2 brain's OWN action, not a food/location hint.
        float forward=0f;
        if(hasPosition&&net>0f){
            float angle=(selected%8)*(float)(Math.PI/4);
            forward=(lastX-startX)*(float)Math.cos(angle)+
                (lastY-startY)*(float)Math.sin(angle);
        }
        float headingScore=gross>1f?
            clip(forward/Math.max(1f,gross),-1f,1f)*
            clip(net/(3f*intervals),0f,1f):0f;
        int hCount=auditOnly?headingVerified[at]:headingTrained[at];
        float hOld=auditOnly?headingVerifyMean[at]:headingTrainMean[at];
        float hMean=hOld+(headingScore-hOld)/(Math.min(80,hCount)+1f);
        if(auditOnly){headingVerified[at]++;headingVerifyMean[at]=hMean;}
        else{headingTrained[at]++;headingTrainMean[at]=hMean;}
        int count=auditOnly?verified[at]:trained[at];
        float old=auditOnly?verifiedMean[at]:trainedMean[at];
        float mean=old+(score-old)/(Math.min(80,count)+1f);
        if(auditOnly){verified[at]++;verifiedMean[at]=mean;}
        else{trained[at]++;trainedMean[at]=mean;}
        episodes++;
    }
    private int chooseOption(int action){
        // Short, medium and long interventions are experimentally sampled,
        // never a scripted walking rhythm, heading or compelled food chase.
        // Still experiment with every duration, including short precision
        // movements, but do not blindly inherit md160's 15/16 preference
        // for one tick: those means never measured heading agreement.
        if(random.nextFloat()<.15f)return random.nextInt(OPTIONS);
        int best=-1;
        float value=-Float.MAX_VALUE;
        for(int k=0;k<OPTIONS;k++){
            int at=action*OPTIONS+k;
            // Physical learning + genuinely untouched validation are BOTH
            // required before duration predictions can control execution.
            // A 160 legacy score is NEVER considered validated directional
            // evidence. New action/duration holdouts must independently agree.
            if(headingTrained[at]<16||headingVerified[at]<6)continue;
            float observed=headingTrainMean[at],validated=headingVerifyMean[at];
            if(Math.abs(validated-observed)>.32f)continue;
            float old=.5f*(verifiedMean[at]+trainedMean[at]);
            float estimate=.25f*old+.40f*.5f*(observed+validated);
            if(estimate>value){value=estimate;best=k;}
        }
        // While mapping the real body, experimentally sample 2/4 decisions.
        // Dangerous contact, serious alternative or exploration can always
        // interrupt; NO locomotion pattern or world direction is prescribed.
        return best>=0?best:1+random.nextInt(OPTIONS-1);
    }
    /**
     * Retain the brain's OWN previous action only while its selected motor
     * interval is still running. Danger and strong current neural alternatives
     * interrupt. Independent exploration is sampled at option boundaries,
     * otherwise physical lag is repeatedly judged against a newer command.
     */
    public int select(int proposal,int greedy,float[] scores,boolean exploratory,
                      float reserve,float harmfulContact,float x,float y){
        if(proposal<0||proposal>=ACTIONS||greedy<0||greedy>=ACTIONS)
            return proposal;
        boolean safe=Float.isFinite(reserve)&&reserve>0f&&
                     harmfulContact<=0f;
        boolean challenger=false;
        if(scores!=null&&scores.length==ACTIONS&&selected>=0)
            challenger=Float.isFinite(scores[greedy])&&Float.isFinite(scores[selected])&&
                scores[greedy]-scores[selected]>.24f;
        if(selected>=0&&remaining>0&&safe&&!challenger){
            remaining--;
            continuesCount++;
            return selected;
        }
        if(selected>=0&&remaining>0)interruptsCount++;
        finish();
        selected=proposal;
        option=chooseOption(proposal);
        remaining=TICKS[option]-1;
        intervals=0;
        gross=gain=damage=cost=effect=0f;
        startX=lastX=x;startY=lastY=y;
        hasPosition=Float.isFinite(x)&&Float.isFinite(y);
        auditOnly=(++started%5)==0;
        choices++;
        return proposal;
    }
    public long starts(){return choices;}
    public long continuations(){return continuesCount;}
    public long interruptions(){return interruptsCount;}
    public long evaluated(){return episodes;}
    public static int ticksAt(int option){return TICKS[option];}
    public int selectedTicks(){return option<0?0:TICKS[option];}
    public int directionalSamples(int action,int duration){
        return headingTrained[action*OPTIONS+duration];
    }
    public int directionalHoldouts(int action,int duration){
        return headingVerified[action*OPTIONS+duration];
    }
    public int trained(int action,int duration){
        return trained[action*OPTIONS+duration];
    }
    public int heldout(int action,int duration){
        return verified[action*OPTIONS+duration];
    }
    public String encode(){
        StringBuilder out=new StringBuilder("md172|").append(choices)
            .append('|').append(continuesCount).append('|').append(interruptsCount)
            .append('|').append(episodes);
        for(int i=0;i<CELLS;i++)out.append('|').append(trained[i]).append(',')
            .append(verified[i]).append(',')
            .append(String.format(Locale.US,"%.7f",trainedMean[i])).append(',')
            .append(String.format(Locale.US,"%.7f",verifiedMean[i])).append(',')
            .append(headingTrained[i]).append(',').append(headingVerified[i])
            .append(',').append(String.format(Locale.US,"%.7f",headingTrainMean[i]))
            .append(',').append(String.format(Locale.US,"%.7f",headingVerifyMean[i]));
        return out.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>16000||
           !(raw.startsWith("md160|")||raw.startsWith("md165|")||raw.startsWith("md172|")))return false;
        try{
            boolean legacy=raw.startsWith("md160|");
            String[] s=raw.split("\\|",-1);
            if(s.length!=5+CELLS)return false;
            long a=Long.parseLong(s[1]),b=Long.parseLong(s[2]),
                 c=Long.parseLong(s[3]),d=Long.parseLong(s[4]);
            if(a<0||b<0||c<0||d<0||a>1000000000L||
               b>1000000000L||c>1000000000L||d>1000000000L)return false;
            int[] t=new int[CELLS],v=new int[CELLS];
            float[] tm=new float[CELLS],vm=new float[CELLS],
                htMean=new float[CELLS],hvMean=new float[CELLS];
            int[] ht=new int[CELLS],hv=new int[CELLS];
            for(int i=0;i<CELLS;i++){
                String[] row=s[i+5].split(",",-1);
                if(row.length!=(legacy?4:8))return false;
                t[i]=Integer.parseInt(row[0]);v[i]=Integer.parseInt(row[1]);
                tm[i]=Float.parseFloat(row[2]);vm[i]=Float.parseFloat(row[3]);
                if(t[i]<0||v[i]<0||t[i]>1000000000||v[i]>1000000000||
                   !Float.isFinite(tm[i])||!Float.isFinite(vm[i])||
                   Math.abs(tm[i])>1.501f||Math.abs(vm[i])>1.501f)return false;
                if(!legacy){
                    ht[i]=Integer.parseInt(row[4]);hv[i]=Integer.parseInt(row[5]);
                    htMean[i]=Float.parseFloat(row[6]);hvMean[i]=Float.parseFloat(row[7]);
                    if(ht[i]<0||hv[i]<0||ht[i]>t[i]||hv[i]>v[i]||
                       !Float.isFinite(htMean[i])||!Float.isFinite(hvMean[i])||
                       Math.abs(htMean[i])>1.001f||Math.abs(hvMean[i])>1.001f)
                        return false;
                }
            }
            // The old format mixed interrupted and completed durations.
            // Retain historical energy/contact/counter records, but only
            // newly completed experiments may grant directional confidence.
            if(!raw.startsWith("md172|")){
                java.util.Arrays.fill(ht,0);java.util.Arrays.fill(hv,0);
                java.util.Arrays.fill(htMean,0f);java.util.Arrays.fill(hvMean,0f);
            }
            System.arraycopy(ht,0,headingTrained,0,CELLS);
            System.arraycopy(hv,0,headingVerified,0,CELLS);
            System.arraycopy(htMean,0,headingTrainMean,0,CELLS);
            System.arraycopy(hvMean,0,headingVerifyMean,0,CELLS);
            System.arraycopy(t,0,trained,0,CELLS);
            System.arraycopy(v,0,verified,0,CELLS);
            System.arraycopy(tm,0,trainedMean,0,CELLS);
            System.arraycopy(vm,0,verifiedMean,0,CELLS);
            choices=a;continuesCount=b;interruptsCount=c;episodes=d;
            // Preserve holdout rotation across app restarts, not merely in RAM.
            started=(int)(a%5L);
            resetLife();return true;
        }catch(RuntimeException bad){return false;}
    }
}
