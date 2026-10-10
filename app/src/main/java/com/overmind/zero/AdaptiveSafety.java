package com.overmind.zero;

import java.util.Locale;

/**
 * Experience-grounded reaction to real losses, NOT an "avoid red" reflex.
 * After loss of energy from an actually touched wavelength, briefly give
 * additional voice to physically VERIFIED motor actions that predict lower
 * visual exposure to ANY frequency previously found energetically harmful.
 *
 * No named hazards, object coordinates, forced headings, escape teleports,
 * or bonus for arbitrary locomotion. Scores are relative: even if all
 * options are imperfect, ZERO can prefer the least harmful learned move.
 * The real brain still chooses the action; this only supplies bounded bias.
 */
public final class AdaptiveSafety {
    public static final String KEY="adaptive_safety_v115";
    private int observedSignal=-1, pendingAction=-1, attemptsWithoutProgress=0;
    private float timeLeft=0, initialBrightness=0;
    public int livedDamage=0,repeatDamage=0,verifiedWithdrawals=0,
        noProgress=0,advisedDecisions=0,unresolved=0;
    public float lastChange=0;
    private static float clamp(float x,float a,float b){
        return Math.max(a,Math.min(b,x));
    }
    private static float intensity(InstrumentalNavigation nav,int signal){
        if(nav==null||signal<0||signal>=InstrumentalNavigation.SIGNALS)return 0;
        float[] v=nav.visible();float maximum=0;
        for(int k=0;k<InstrumentalNavigation.SECTORS;k++)
            maximum=Math.max(maximum,v[signal*InstrumentalNavigation.SECTORS+k]);
        return maximum;
    }
    public boolean active(){return timeLeft>0&&observedSignal>=0;}
    public int activeSignal(){return active()?observedSignal:-1;}
    public void resetLife(){
        observedSignal=-1;pendingAction=-1;timeLeft=0;
        initialBrightness=0;attemptsWithoutProgress=0;lastChange=0;
    }
    public void resumeLife(){resetLife();}
    public void elapsed(float dt){
        timeLeft=Math.max(0,timeLeft-Math.max(0,dt));
        if(timeLeft==0){observedSignal=-1;pendingAction=-1;}
    }
    public void contact(float actualEnergyDelta,int wavelength,InstrumentalNavigation nav){
        if(!Float.isFinite(actualEnergyDelta)||actualEnergyDelta>=-.01f||
           wavelength<0||wavelength>=InstrumentalNavigation.SIGNALS||nav==null)
            return;
        // Valence must be LEARNED from this or a preceding real contact.
        if(nav.signalValue(wavelength)>=-.08f||nav.spectralContacts()[wavelength]<1)
            return;
        livedDamage++;
        if(active()&&observedSignal==wavelength)repeatDamage++;
        observedSignal=wavelength;
        timeLeft=4.5f;
        pendingAction=-1;
        attemptsWithoutProgress=0;
        initialBrightness=intensity(nav,wavelength);
    }
    /** Signed advantage of actual predicted movement away from learned
     * dangerous observed light. Spatial integration has already accounted
     * for *all* simultaneously visible harmful wavelengths.
     */
    public float bias(InstrumentalNavigation nav,int action){
        if(!active()||nav==null)return 0;
        float exposure=nav.learnedHarmExposure(action);
        float urgency=clamp(timeLeft/4.5f,.35f,1);
        return clamp(-.95f*urgency*exposure,-.95f,.95f);
    }
    public void chosen(InstrumentalNavigation nav,int action){
        if(!active()||nav==null)return;
        pendingAction=action;
        initialBrightness=intensity(nav,observedSignal);
    }
    /** Measure actual after-action sensory change; don't praise an
     * intended escape when the body remained pressed into a hazard.
     * With a fixed visible wavelength, reduced brightness means that the
     * body probably increased range; keep uncertainty explicit.
     */
    public void consequence(InstrumentalNavigation nav,int action,float realDisplacement){
        if(!active()||nav==null||pendingAction!=action)return;
        float after=intensity(nav,observedSignal);
        lastChange=initialBrightness-after;
        pendingAction=-1;
        if(realDisplacement>=3&&lastChange>.012f){
            verifiedWithdrawals++;
            attemptsWithoutProgress=0;
            // Once withdrawn, keep only modest transient caution; do not
            // prevent later exploration of new energy opportunities.
            timeLeft=Math.min(timeLeft,1.2f);
        }else if(after>.08f){
            attemptsWithoutProgress++;
            noProgress++;
            if(attemptsWithoutProgress>=3)unresolved++;
        }
    }
    public void noteChoice(float peak){
        if(active()&&peak>.03f)advisedDecisions++;
    }
    public String encode(){
        return String.format(Locale.US,"as1|%d|%d|%d|%d|%d|%d",
            livedDamage,repeatDamage,verifiedWithdrawals,noProgress,
            advisedDecisions,unresolved);
    }
    public boolean restore(String value){
        if(value==null||value.length()>160||!value.startsWith("as1|"))return false;
        try{
            String[] a=value.split("\\|",-1);
            if(a.length!=7)return false;
            int[] data=new int[6];
            for(int i=0;i<6;i++){
                data[i]=Integer.parseInt(a[i+1]);
                if(data[i]<0)return false;
            }
            livedDamage=data[0];repeatDamage=data[1];
            verifiedWithdrawals=data[2];noProgress=data[3];
            advisedDecisions=data[4];unresolved=data[5];
            resetLife();return true;
        }catch(RuntimeException error){return false;}
    }
    private static void check(boolean c,String explanation){
        if(!c)throw new AssertionError(explanation);
    }
    public static void main(String[] args){
        AdaptiveSafety r=new AdaptiveSafety();
        InstrumentalNavigation nav=new InstrumentalNavigation();
        MotorBrain.Body body=new MotorBrain.Body();
        java.util.ArrayList<UnknownWorld.Entity> scene=new java.util.ArrayList<>();
        scene.add(new UnknownWorld.Entity(95,0,1,1));
        nav.sense(body,null,scene);
        r.contact(-7,1,nav);
        check(!r.active()&&r.bias(nav,0)==0,
            "New organism received a hazard rule before any lived consequence");
        nav.energyOutcome(-7,1,true);
        r.contact(-7,1,nav);
        check(r.active()&&r.bias(nav,0)==0,
            "Damage alone implies an impossible known motor action");
        for(int a=0;a<8;a++){
            for(int t=0;t<48;t++){
                body.cx=body.cy=0;nav.sense(body,null,scene);
                nav.chosen(a,body);
                body.cx+=(float)Math.cos(Math.PI*a/4)*25;
                body.cy+=(float)Math.sin(Math.PI*a/4)*25;
                nav.consequence(a,body,t%5!=0,t%5==0);
            }
        }
        body.cx=body.cy=0;nav.sense(body,null,scene);
        check(nav.learnedHarmExposure(0)>0&&nav.learnedHarmExposure(4)<0,
            "Unscripted learned motor effects failed to differentiate risk");
        check(r.bias(nav,4)>r.bias(nav,0),
            "Following actual damage the safe learned action is not preferred");
        r.chosen(nav,4);body.cx=-20;nav.sense(body,null,scene);
        r.consequence(nav,4,20);
        check(r.verifiedWithdrawals==1&&r.timeLeft<=1.2f,
            "Only real witnessed withdrawal may count as successful");
        String saved=r.encode();AdaptiveSafety clone=new AdaptiveSafety();
        check(clone.restore(saved)&&clone.encode().equals(saved)&&!clone.active(),
            "Historical safety episodes persist; phantom active threats must not");
        check(!new AdaptiveSafety().restore("as1|bad"),"Corrupt state accepted");
        System.out.println("ZERO-0115 PASS: damage-grounded brief risk priority, "+
            "verified withdrawal, no preset colours/actions, persisted life statistics");
    }
}
