package com.overmind.zero;

import java.util.ArrayDeque;
import java.util.Locale;

/**
 * ZERO 0.11.8: learns a PREDICTION of the end of a lived episode from
 * preceding own-energy observations. There is no gate, square, red-object,
 * signature, heading, chosen action or target in its learned features.
 * A "terminal" supervision signal means ONLY a real ended life; neither
 * a cause-of-death label nor a punishment of the last motor action.
 *
 * Holdouts are whole LIVES (every fifth), not neighboring correlated frames.
 * If this model is not validated, its contribution to the policy is 0.
 */
public final class MortalityLearning {
    public static final String KEY="mortality_learning_v118";
    private static final int HORIZON=16,MAX_RECENT=HORIZON+1;
    private static final float LR=.085f;
    private static final class Sample {
        final float energy,change;
        Sample(float e,float c){energy=e;change=c;}
    }
    private final ArrayDeque<Sample> recent=new ArrayDeque<>();
    private final float[] weights=new float[3];
    private float lastEnergy=Float.NaN;
    private int steps=0;
    public int deaths=0,trainedTerminal=0,trainedSurvival=0;
    public int heldoutLives=0,heldoutDeaths=0,heldoutSurvival=0;
    private double heldoutDeathSquared=0,heldoutSurvivalSquared=0;
    public int deathsWithRecentHarm=0,deathsWithoutRecentHarm=0;
    public float lastRecentDrain=0,lastRecentHarm=0,lastRecentGain=0;
    private float recentDrain=0,recentHarm=0,recentGain=0;
    public int adviceEvents=0;
    private boolean holdout(){return (deaths+1)%5==0;}

    private static float clamp(float v,float lo,float hi){
        return Math.max(lo,Math.min(hi,v));
    }
    private static boolean valid(float x){return Float.isFinite(x);}
    private float predict(Sample s){
        float z=clamp(weights[0]+weights[1]*s.energy+
                         weights[2]*s.change,-12,12);
        return (float)(1.0/(1.0+Math.exp(-z)));
    }
    public float riskAt(float energy,float recentChange){
        if(!valid(energy)||!valid(recentChange))return 0.5f;
        return predict(new Sample(clamp(energy,0,1),clamp(recentChange,-1,1)));
    }
    private void label(Sample s,boolean terminal,float strength){
        float predicted=predict(s);
        if(holdout()){
            if(terminal){heldoutDeathSquared+=(1-predicted)*(1-predicted);
                heldoutDeaths++;}
            else{heldoutSurvivalSquared+=predicted*predicted;
                heldoutSurvival++;}
        }else{
            float gradient=LR*strength*((terminal?1f:0f)-predicted);
            weights[0]=clamp(weights[0]+gradient,-8,8);
            weights[1]=clamp(weights[1]+gradient*s.energy,-8,8);
            weights[2]=clamp(weights[2]+gradient*s.change,-8,8);
            if(terminal)trainedTerminal++;else trainedSurvival++;
        }
    }
    /** One completed REAL motor interval, approximately 0.65 simulated s. */
    public void observed(float normalizedEnergy){
        if(!valid(normalizedEnergy)||normalizedEnergy<0||normalizedEnergy>1)return;
        float change=valid(lastEnergy)?normalizedEnergy-lastEnergy:0;
        lastEnergy=normalizedEnergy;
        recent.addLast(new Sample(normalizedEnergy,clamp(change,-1,1)));
        steps++;
        // We know an old observation was survived beyond our bounded horizon;
        // label only that old observation, never the current one as "safe".
        if(recent.size()>MAX_RECENT){
            Sample old=recent.removeFirst();
            if(steps%3==0)label(old,false,1f);
        }
    }
    /** Physics-only accounting, independent of action, contacts and gate motion. */
    public void energyFlows(float dt,float passiveLoss,float contactHarm,float contactGain){
        if(!valid(dt)||dt<0||dt>.5f||
           !valid(passiveLoss)||!valid(contactHarm)||!valid(contactGain)||
           passiveLoss<0||contactHarm<0||contactGain<0)return;
        float decay=(float)Math.exp(-dt/6f);
        recentDrain=clamp(recentDrain*decay+passiveLoss,0,200f);
        recentHarm=clamp(recentHarm*decay+contactHarm,0,200f);
        recentGain=clamp(recentGain*decay+contactGain,0,200f);
    }
    /** Called exactly once, BEFORE resetEpisode, on physically genuine death.
     * All buffered sensor states (not action identities) receive terminal
     * evidence. A final red contact cannot punish a simultaneous box/grip.
     */
    public void terminal(float finalNormalizedEnergy){
        if(!valid(finalNormalizedEnergy)||finalNormalizedEnergy>.0001f||
           finalNormalizedEnergy<0)return;
        if(recent.isEmpty()||recent.peekLast().energy>.0001f)
            observed(0f);
        deaths++;
        // Holdout determination is the just-completed life, not the next one.
        // label() uses (deaths+1)%5, so undo temporarily to pick the old life.
        deaths--;
        int index=0,amount=recent.size();
        for(Sample s:recent){
            float howRecent=1f-(amount-1-index)/(float)HORIZON;
            label(s,true,clamp(.35f+.65f*howRecent,.35f,1f));
            index++;
        }
        if(holdout())heldoutLives++;
        deaths++;
        lastRecentDrain=recentDrain;
        lastRecentHarm=recentHarm;
        lastRecentGain=recentGain;
        if(recentHarm>.1f)deathsWithRecentHarm++;
        else deathsWithoutRecentHarm++;
        newLife();
    }
    public void newLife(){
        recent.clear();steps=0;lastEnergy=Float.NaN;
        recentDrain=recentHarm=recentGain=0;
    }
    /** Reliability requires whole-life heldouts with BOTH outcomes. The
     * balanced Brier score is a conservative comparison to guessing 50%.
     * The relationship (lower energy -> higher risk) must emerge in DATA.
     */
    public float confidence(){
        if(heldoutLives<5||heldoutDeaths<30||heldoutSurvival<100||
           trainedTerminal<80||trainedSurvival<100)return 0;
        double balanced=.5*(heldoutDeathSquared/heldoutDeaths+
                            heldoutSurvivalSquared/heldoutSurvival);
        float separation=riskAt(.1f,-.03f)-riskAt(.8f,0);
        if(balanced>=.21||separation<=.12f)return 0;
        return clamp((float)((.25-balanced)/.25),0,.65f);
    }
    /** Optional bounded learned survival value. Nothing happens until
     * sufficient held-out evidence. Only energy-related consequences already
     * learned independently from visually source-matched REAL contacts can
     * be reweighted; NO action/door/box/obstacle gets a generic death penalty.
     */
    public float energyAdvice(float energy,float earnedEnergeticValue){
        float confidence=confidence();
        if(confidence==0||!valid(earnedEnergeticValue)||!valid(energy))return 0;
        float q=clamp(earnedEnergeticValue,-1f,1f);
        float bias=clamp(.35f*confidence*riskAt(energy,0)*q,-.18f,.18f);
        if(Math.abs(bias)>.001f)adviceEvents++;
        return bias;
    }
    public String encode(){
        StringBuilder s=new StringBuilder("ml1|").append(deaths).append('|')
            .append(trainedTerminal).append('|').append(trainedSurvival).append('|')
            .append(heldoutLives).append('|').append(heldoutDeaths).append('|')
            .append(heldoutSurvival).append('|').append(heldoutDeathSquared).append('|')
            .append(heldoutSurvivalSquared).append('|').append(deathsWithRecentHarm).append('|')
            .append(deathsWithoutRecentHarm).append('|').append(lastRecentDrain).append('|')
            .append(lastRecentHarm).append('|').append(lastRecentGain).append('|')
            .append(recentDrain).append('|').append(recentHarm).append('|')
            .append(recentGain).append('|').append(steps).append('|')
            .append(valid(lastEnergy)?lastEnergy:-1).append('|').append(adviceEvents);
        for(float w:weights)s.append('|').append(w);
        s.append('|').append(recent.size());
        for(Sample r:recent)s.append('|').append(r.energy).append(',').append(r.change);
        return s.toString();
    }
    public boolean restore(String value){
        if(value==null||!value.startsWith("ml1|")||value.length()>5000)return false;
        try{
            String[] p=value.split("\\|",-1);
            if(p.length<24)return false;
            int i=1;
            int d=Integer.parseInt(p[i++]),tt=Integer.parseInt(p[i++]),
                ts=Integer.parseInt(p[i++]),hl=Integer.parseInt(p[i++]),
                hd=Integer.parseInt(p[i++]),hs=Integer.parseInt(p[i++]);
            double ed=Double.parseDouble(p[i++]),es=Double.parseDouble(p[i++]);
            int rh=Integer.parseInt(p[i++]),rn=Integer.parseInt(p[i++]);
            float ld=Float.parseFloat(p[i++]),lh=Float.parseFloat(p[i++]),
                lg=Float.parseFloat(p[i++]),rd=Float.parseFloat(p[i++]),
                rr=Float.parseFloat(p[i++]),rg=Float.parseFloat(p[i++]);
            int st=Integer.parseInt(p[i++]);
            float last=Float.parseFloat(p[i++]);
            int advice=Integer.parseInt(p[i++]);
            float[] w=new float[3];for(int j=0;j<3;j++)w[j]=Float.parseFloat(p[i++]);
            int n=Integer.parseInt(p[i++]);
            if(p.length!=i+n||d<0||tt<0||ts<0||hl<0||hl>d||hd<0||
               hs<0||rh<0||rn<0||rh+rn!=d||st<0||advice<0||
               !Double.isFinite(ed)||!Double.isFinite(es)||ed<0||es<0||
               n<0||n>MAX_RECENT||st<n||!valid(last)||last< -1||
               last>1||!valid(ld)||!valid(lh)||!valid(lg)||
               !valid(rd)||!valid(rr)||!valid(rg)||
               ld<0||lh<0||lg<0||rd<0||rr<0||rg<0)return false;
            for(float v:w)if(!valid(v)||Math.abs(v)>8)return false;
            ArrayDeque<Sample> saved=new ArrayDeque<>();
            for(int j=0;j<n;j++){
                String[] a=p[i++].split(",",-1);
                if(a.length!=2)return false;
                float e=Float.parseFloat(a[0]),c=Float.parseFloat(a[1]);
                if(!valid(e)||e<0||e>1||!valid(c)||Math.abs(c)>1)return false;
                saved.addLast(new Sample(e,c));
            }
            deaths=d;trainedTerminal=tt;trainedSurvival=ts;heldoutLives=hl;
            heldoutDeaths=hd;heldoutSurvival=hs;
            heldoutDeathSquared=ed;heldoutSurvivalSquared=es;
            deathsWithRecentHarm=rh;deathsWithoutRecentHarm=rn;
            lastRecentDrain=ld;lastRecentHarm=lh;lastRecentGain=lg;
            recentDrain=rd;recentHarm=rr;recentGain=rg;steps=st;
            lastEnergy=last<0?Float.NaN:last;adviceEvents=advice;
            System.arraycopy(w,0,weights,0,3);
            recent.clear();recent.addAll(saved);return true;
        }catch(RuntimeException invalid){return false;}
    }
    public static void main(String[] args){
        MortalityLearning m=new MortalityLearning();
        if(m.confidence()!=0||m.energyAdvice(.01f,1f)!=0)
            throw new AssertionError("Virgin ZERO received programmed death aversion");
        // 35 actually ended lives; each fifth WHOLE life held out.
        for(int life=0;life<35;life++){
            m.newLife();
            float e=.9f;
            for(int t=0;t<75;t++){
                e=clamp(e-.012f,0,1);
                m.energyFlows(.65f,1.2f,0,0);
                if(t==25&&life%3==0){
                    e=clamp(e-.04f,0,1);
                    m.energyFlows(0,0,4,0);
                }
                if(e==0)break;
                m.observed(e);
            }
            m.terminal(0);
        }
        if(m.deaths!=35||m.heldoutLives!=7||
           m.deathsWithRecentHarm==0||m.deathsWithoutRecentHarm==0)
            throw new AssertionError("Wrong physical death accounting");
        if(m.trainedTerminal==0||m.trainedSurvival==0||
           m.heldoutDeaths==0||m.heldoutSurvival==0)
            throw new AssertionError("No held-out death/survival comparisons");
        if(m.riskAt(.05f,-.02f)<=m.riskAt(.8f,0))
            throw new AssertionError("Did not learn relationship of energy to death");
        float before=m.riskAt(.1f,0);
        String saved=m.encode();
        MortalityLearning restored=new MortalityLearning();
        if(!restored.restore(saved)||!saved.equals(restored.encode()))
            throw new AssertionError("Saved terminal experience not restored");
        if(restored.restore(saved+"|invalid")||
           Math.abs(restored.riskAt(.1f,0)-before)>1e-6)
            throw new AssertionError("Corrupt profile overwrote learned experience");
        m.newLife();m.observed(.4f);m.observed(.39f);
        float noContact=m.riskAt(.39f,-.01f);
        // Manipulating any obstacle is NOT part of the mortality model.
        m.energyFlows(.02f,0,0,0);
        if(Math.abs(noContact-m.riskAt(.39f,-.01f))>1e-8)
            throw new AssertionError("World manipulation altered learned mortality risk");
        System.out.println(String.format(Locale.US,
            "Terminal experience pass: deaths=%d holdoutLives=%d risk low %.3f high %.3f confidence %.3f",
            m.deaths,m.heldoutLives,m.riskAt(.05f,-.02f),m.riskAt(.8f,0),m.confidence()));
    }
}
