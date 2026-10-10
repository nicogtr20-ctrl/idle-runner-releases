package com.overmind.zero;

import java.util.Locale;

/**
 * Evidence-gated persistence of an experienced sensory objective.
 * No hand-coded resource, hazard, target-coordinate, gate or motor heading.
 * The "intention" is just the raw observed wavelength that gained/lost
 * actual energy, together with a short-lived verified approach/withdrawal
 * tendency. Motion is never directly rewarded for its own sake.
 *
 * A previous action must have actually changed observed stimulus proximity
 * in the useful direction before it can influence the next one. If the
 * evidence stops improving or becomes occluded the intention is abandoned.
 */
public final class GoalContinuity {
    public static final String KEY="goal_continuity_v114";
    private int pending=-1,signal=-1,sign=0,steps=0,failures=0;
    private float lastBrightness=0;
    public int confirmed=0,continued=0,abandoned=0,failed=0,advice=0;
    public long observed=0;
    public float lastProgress=0;
    private static float clamp(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }
    private static float brightness(InstrumentalNavigation n,int s){
        if(n==null||s<0||s>=InstrumentalNavigation.SIGNALS)return 0;
        float[] scene=n.visible();float max=0;
        for(int d=0;d<InstrumentalNavigation.SECTORS;d++)
            max=Math.max(max,scene[s*InstrumentalNavigation.SECTORS+d]);
        return max;
    }
    /** No hidden object identities. Choose a currently seen, previously
     * experienced spectral signal using only learned energy valence. */
    private int candidate(InstrumentalNavigation n){
        if(n==null)return -1;
        float best=.14f;int chosen=-1;
        for(int s=0;s<InstrumentalNavigation.SIGNALS;s++){
            float value=n.signalValue(s);
            if(Math.abs(value)<.16f||n.spectralContacts()[s]<1)continue;
            float b=brightness(n,s);
            float utility=Math.abs(value)*b*b;
            if(b>.17f&&utility>best){best=utility;chosen=s;}
        }
        return chosen;
    }
    public void resetLife(){
        pending=-1;signal=-1;sign=0;steps=0;failures=0;lastBrightness=0;
        lastProgress=0;
    }
    public void resumeLife(){resetLife();}
    public int activeSignal(){return signal;}
    public int verifiedSteps(){return steps;}
    public boolean active(){return signal>=0&&steps>0;}
    public void chosen(InstrumentalNavigation nav,int action,float energy){
        if(nav==null)return;
        int best=candidate(nav);
        // Intentions arise only from previously learned spectral energy
        // outcomes and actual verified motor efficacy.
        if(best<0){
            if(active())abandoned++;
            resetLife();return;
        }
        if(signal!=best){
            if(active())abandoned++;
            signal=best;sign=nav.signalValue(best)>0?1:-1;
            steps=0;failures=0;
        }
        float selected=nav.bias(action,energy);
        if(selected<.10f){
            pending=-1;
            return;
        }
        pending=action;
        lastBrightness=brightness(nav,signal);
    }
    /** Called at the end of a physically integrated action. Visible
     * brightness change is a PROXY for relative proximity; it is not a
     * guaranteed distance metric or a claim of object understanding. */
    public void consequence(InstrumentalNavigation nav,int action,
                            float actualTravel){
        if(nav==null||pending!=action||signal<0)return;
        observed++;
        float now=brightness(nav,signal);
        float delta=(now-lastBrightness)*sign;
        lastProgress=delta;
        pending=-1;
        if(now<.075f||actualTravel<3f||delta<-.012f){
            failed++;failures++;
            if(failures>=2||now<.075f){
                if(active())abandoned++;
                signal=-1;steps=0;failures=0;
            }
            return;
        }
        if(delta>.009f){
            if(steps==0)confirmed++;
            else continued++;
            steps=Math.min(5,steps+1);failures=0;
        }else{
            failures++;
            if(failures>=2){
                if(active())abandoned++;
                signal=-1;steps=0;failures=0;
            }
        }
    }
    /** Modest preference for actions still predicted to improve the same
     * sensory goal. Never a forced action or a bonus for just traveling.
     * Completely OFF in a virgin model and in an A/B control condition. */
    public float bias(InstrumentalNavigation nav,int action,float energy){
        if(!active()||nav==null||candidate(nav)!=signal)return 0;
        float learned=nav.bias(action,energy);
        if(learned<=.10f)return 0;
        float persistence=clamp(.16f+.045f*steps,0,.43f);
        return clamp(learned*persistence,0,.48f);
    }
    public void noteDecision(float peak){
        if(peak>.02f)advice++;
    }
    public String encode(){
        return String.format(Locale.US,"gc1|%d|%d|%d|%d|%d|%d",
            confirmed,continued,abandoned,failed,advice,observed);
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>120||!raw.startsWith("gc1|"))return false;
        try{
            String[] fields=raw.split("\\|",-1);
            if(fields.length!=7)return false;
            int a=Integer.parseInt(fields[1]),b=Integer.parseInt(fields[2]),
                c=Integer.parseInt(fields[3]),d=Integer.parseInt(fields[4]),
                e=Integer.parseInt(fields[5]);
            long f=Long.parseLong(fields[6]);
            if(a<0||b<0||c<0||d<0||e<0||f<0)return false;
            confirmed=a;continued=b;abandoned=c;failed=d;advice=e;observed=f;
            resetLife();return true;
        }catch(RuntimeException bad){return false;}
    }
    private static void check(boolean ok,String msg){
        if(!ok)throw new AssertionError(msg);
    }
    public static void main(String[] args){
        InstrumentalNavigation n=new InstrumentalNavigation();
        GoalContinuity g=new GoalContinuity();
        MotorBrain.Body b=new MotorBrain.Body();
        java.util.ArrayList<UnknownWorld.Entity> scene=new java.util.ArrayList<>();
        UnknownWorld.Entity signal=new UnknownWorld.Entity(140,0,0,0);
        scene.add(signal);
        n.sense(b,null,scene);
        g.chosen(n,0,.2f);
        check(!g.active()&&g.bias(n,0,.2f)==0,
            "Virgin state invented an intention without experienced energy");
        n.energyOutcome(22,0,true);
        for(int j=0;j<32;j++){
            b.cx=0;b.cy=0;n.sense(b,null,scene);n.chosen(0,b);
            b.cx+=24;
            n.consequence(0,b,j%5!=0,j%5==0);
        }
        b.cx=0;b.cy=0;n.sense(b,null,scene);
        check(n.bias(0,.2f)>.10f,"Sensorimotor motor learning absent");
        g.chosen(n,0,.2f);
        check(g.bias(n,0,.2f)==0,"Intention given priority before real progress");
        b.cx=20;n.sense(b,null,scene);
        g.consequence(n,0,20);
        check(g.active()&&g.confirmed==1&&g.bias(n,0,.2f)>0,
            "Actual approach progress did not trigger conditional continuity");
        g.chosen(n,0,.2f);
        b.cx=37;n.sense(b,null,scene);g.consequence(n,0,17);
        check(g.continued==1,"Verified successive approach not retained");
        g.chosen(n,0,.2f);
        b.cx=0;n.sense(b,null,scene);g.consequence(n,0,37);
        g.chosen(n,0,.2f);
        b.cx=-22;n.sense(b,null,scene);g.consequence(n,0,22);
        check(!g.active()&&g.abandoned>0,
            "Repeated actual regression never abandoned intention");
        String saved=g.encode();GoalContinuity restored=new GoalContinuity();
        check(restored.restore(saved)&&restored.encode().equals(saved)&&
            !restored.active(),"Stats did not restore or stale intent resumed");
        check(!new GoalContinuity().restore("gc1|NaN"),
            "Invalid memory accepted");
        System.out.println("ZERO-0114 PASS: no synthetic goal, progress-gated " +
          "persistence, disappointment, autonomous abandonment and safe recall");
    }
}
