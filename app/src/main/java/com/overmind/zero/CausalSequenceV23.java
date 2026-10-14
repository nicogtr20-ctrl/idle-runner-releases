package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Lived causal-sequence memory for the ONE ZERO22 policy.
 * No Entity, target coordinates, resource names, hidden object ID or legal
 * manipulation flag enters this learner. Anonymous IDs come exclusively from
 * occluded first-return ray continuity. A hypothesis needs: one uniquely
 * tracked, physically corroborated movement, a subsequent optical change,
 * and a genuinely touched resource of the observed wavelength.
 *
 * Training and holdout are separated at the INITIAL intervention. A plausible
 * association is never treated as proof: heldout error must beat a prior
 * based on all training interventions before it may affect choice or Q.
 */
public final class CausalSequenceV23 {
    public static final String KEY="causal_sequence_v23";
    private static final int A=16, SIGNALS=3, HORIZON=64, MAX_ANCHORS=12;
    private static final float LIGHT_RISE=.105f;
    private final long[] attempts=new long[A],success=new long[A],
        pairAttempts=new long[A*A],pairSuccess=new long[A*A];
    private final ArrayList<Anchor> active=new ArrayList<>();
    private long interventions,confirmedLinks,ambiguousLinks,unlinkedEnergy,
        expirations,holdouts,validLinks,invalid,completed,qualifiedMoves;
    private double modelLoss,referenceLoss;
    private int tick, pendingAction=-1,pendingTrack=-1;
    private float pendingConfidence,pendingEnergy;
    private float[] pendingState,pendingLight;
    private boolean pendingSeen;
    private float queuedEnergy;
    private int queuedSignal=-1;
    private static final class Anchor{
        final int tick,action,track;
        final float[] state,lightBefore,lightPeak;
        final boolean holdout;
        final boolean[] follows=new boolean[A];
        int riseSignal=-1;
        boolean censored;
        int riseTick=-1;
        Anchor(int t,int a,int id,float[] z,float[] before,boolean held){
            tick=t;action=a;track=id;state=z.clone();
            lightBefore=before.clone();lightPeak=before.clone();holdout=held;
        }
        boolean saw(int signal){return signal>=0&&signal<SIGNALS&&
            lightPeak[signal]-lightBefore[signal]>=LIGHT_RISE;}
    }
    private static float clip(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    private static float[] light(float[] s){
        float[] result=new float[SIGNALS];
        if(s==null||s.length!=SensorimotorMind.S)return result;
        for(int k=0;k<SIGNALS;k++)for(int j=0;j<4;j++)
            result[k]=Math.max(result[k],clip(s[14+k*4+j],0,1));
        return result;
    }
    private static boolean finite(float[] a,int n){
        if(a==null||a.length!=n)return false;
        for(float f:a)if(!Float.isFinite(f))return false;
        return true;
    }
    public void chosen(int action,float[] senses,int track,float confidence,
                       float[] recurrentState){
        pendingAction=-1;pendingSeen=false;pendingState=null;
        if(action<0||action>=A||!finite(senses,SensorimotorMind.S)||
           !finite(recurrentState,RecurrentCoreV22.Z)||!Float.isFinite(confidence)){
            invalid++;return;
        }
        pendingAction=action;pendingTrack=track;
        pendingConfidence=confidence;pendingEnergy=senses[0];
        pendingLight=light(senses);pendingState=recurrentState.clone();
        pendingSeen=track>=0&&confidence>=.44f&&senses[4]>.015f;
        // Remember only choices following a currently *observed* change.
        if(active.size()==1){
            Anchor a=active.get(0);
            int k=a.riseSignal;
            if(a.riseTick>=0&&tick-a.riseTick<=12&&tick>a.tick&&
               k>=0&&light(senses)[k]>.12f)
                a.follows[action]=true;
        }
    }
    /** Real contact may occur between decision boundaries. Only the final
     * real consequence boundary attributes it to a previous action. */
    public void contactEnergy(float actualDelta,int observedSignal){
        if(!Float.isFinite(actualDelta)||actualDelta==0)return;
        if(actualDelta>0){
            if(observedSignal<0||observedSignal>=SIGNALS){queuedSignal=-2;}
            else if(queuedSignal==-1)queuedSignal=observedSignal;
            else if(queuedSignal!=observedSignal)queuedSignal=-2;
            queuedEnergy+=actualDelta;
        }
    }
    private double globalRate(){
        long n=0,k=0;
        for(int a=0;a<A;a++){n+=attempts[a];k+=success[a];}
        return (k+1d)/(n+2d);
    }
    private double prediction(int action){
        return (success[action]+1d)/(attempts[action]+2d);
    }
    public float confidence(){
        if(holdouts<60||validLinks<14)return 0;
        double base=referenceLoss/holdouts,model=modelLoss/holdouts;
        if(base<.004||model>=base*.94)return 0;
        return clip((float)((base-model)/base),0,.35f);
    }
    private void resolve(Anchor a,boolean positive,RecurrentCoreV22 core,
                         float contactEnergy){
        completed++;
        if(positive){confirmedLinks++;if(a.holdout)validLinks++;}
        if(a.censored)return;
        int action=a.action;
        if(a.holdout){
            holdouts++;
            double guess=prediction(action),base=globalRate();
            double y=positive?1:0;
            modelLoss+=(guess-y)*(guess-y);
            referenceLoss+=(base-y)*(base-y);
        }else{
            // No data from the heldout phase may ever update a Q head.
            if(attempts[action]<1000000000L)attempts[action]++;
            if(positive&&success[action]<1000000000L)success[action]++;
            for(int follow=0;follow<A;follow++)if(a.follows[follow]){
                int index=action*A+follow;
                if(pairAttempts[index]<1000000000L)pairAttempts[index]++;
                if(positive&&pairSuccess[index]<1000000000L)pairSuccess[index]++;
            }
            // Only *previously validated* causal associations may propagate
            // REAL delayed energy backward to the recurrent value head.
            // No invented episode, privileged target or Q feedback on a
            // negative/censored/suspected link.
            float trust=confidence();
            if(positive&&trust>0&&core!=null&&Float.isFinite(contactEnergy)){
                int age=tick-a.tick;
                float discount=(float)Math.pow(.96,Math.max(0,age));
                core.learnVerifiedDelayedCredit(a.state,action,contactEnergy,
                                               trust*discount);
            }
        }
    }
    /** Complete exactly one real 0.65s action. Corroboration must be supplied
     * by independent depth-ray identity AND measured external displacement. */
    public void consequence(float[] after,int corroboratedTrack,
                            float actualExternalMovement,
                            boolean train,boolean holdout,
                            RecurrentCoreV22 core){
        tick++;
        float[] seen=light(after);
        for(Anchor a:active){
            for(int k=0;k<SIGNALS;k++)
                a.lightPeak[k]=Math.max(a.lightPeak[k],seen[k]);
            for(int k=0;k<SIGNALS;k++)if(a.saw(k)){
                if(a.riseTick<0)a.riseTick=tick;
                if(a.riseSignal<0)a.riseSignal=k;
            }
        }
        // An object cannot be called moved just because its optical image
        // changed when ZERO changed viewpoint. A same-ID before/after
        // corroboration and independent physical movement are mandatory.
        if(pendingAction>=0&&pendingSeen&&actualExternalMovement>.015f&&
           corroboratedTrack==pendingTrack&&pendingTrack>=0&&
           pendingState!=null&&finite(after,SensorimotorMind.S)){
            qualifiedMoves++;interventions++;
            if(active.size()>=MAX_ANCHORS){
                resolve(active.remove(0),false,core,0);
                expirations++;
            }
            active.add(new Anchor(tick,pendingAction,pendingTrack,
                                  pendingState,pendingLight,holdout));
        }
        if(queuedEnergy>.01f){
            ArrayList<Anchor> eligible=new ArrayList<>();
            for(Anchor a:active)if(tick-a.tick>=2&&tick-a.tick<=HORIZON&&
                a.saw(queuedSignal)&&!a.censored)eligible.add(a);
            if(eligible.size()==1){
                Anchor witness=eligible.get(0);
                active.remove(witness);
                resolve(witness,true,core,queuedEnergy);
            }else if(eligible.size()>1){
                // Multiple possible causes: no confident attribution, and
                // do not manufacture failures for the competing candidates.
                ambiguousLinks++;
                for(Anchor a:eligible){a.censored=true;active.remove(a);}
            }else unlinkedEnergy++;
        }
        queuedEnergy=0;queuedSignal=-1;
        for(int i=active.size()-1;i>=0;i--){
            Anchor a=active.get(i);
            if(tick-a.tick>=HORIZON){
                active.remove(i);resolve(a,false,core,0);expirations++;
            }
        }
        pendingAction=-1;pendingState=null;pendingSeen=false;
    }
    /** Strict heldout gate first. Modest, contextual advice is a component
     * of the recurrent brain's scores, NOT a separate actor. */
    public float choiceBias(int action,float[] senses,float focusConfidence){
        if(action<0||action>=A||senses==null||
           senses.length!=SensorimotorMind.S||!Float.isFinite(focusConfidence))
            return 0;
        float trust=confidence();
        if(trust<=0)return 0;
        // Physical manipulation is relevant only around a perceived,
        // independently tracked near-touch object, never in open space.
        float candidate=focusConfidence>=.44f&&senses[4]>.015f?1:0;
        if(candidate>0&&attempts[action]>=15){
            double uplift=prediction(action)-globalRate();
            return clip((float)uplift*trust*1.3f,-.16f,.16f);
        }
        // After a measured object intervention, remember which next action
        // was actually followed by a delayed positive energy outcome.
        if(active.size()==1&&active.get(0).riseSignal>=0&&
           light(senses)[active.get(0).riseSignal]>.12f){
            Anchor a=active.get(0);
            int ix=a.action*A+action;
            if(pairAttempts[ix]>=12&&pairSuccess[ix]>=3){
                float p=(pairSuccess[ix]+1f)/(pairAttempts[ix]+2f);
                return clip((float)(p-globalRate())*trust*.6f,-.09f,.09f);
            }
        }
        return 0;
    }
    public void resetLife(){
        active.clear();tick=0;pendingAction=-1;pendingState=null;
        pendingSeen=false;queuedEnergy=0;queuedSignal=-1;
    }
    public void resumeLife(){resetLife();}
    public long interventions(){return interventions;}
    public long qualifiedMoves(){return qualifiedMoves;}
    public long delayedLinks(){return confirmedLinks;}
    public long ambiguous(){return ambiguousLinks;}
    public long missed(){return unlinkedEnergy;}
    public long validations(){return holdouts;}
    public long trained(){long n=0;for(long v:attempts)n+=v;return n;}
    public String summary(){
        return String.format(Locale.US,
            "ZERO23 sequences: tracked-moves=%d candidates=%d links=%d ambiguous=%d unlinked=%d trained=%d heldout=%d confidence=%.4f",
            qualifiedMoves,interventions,confirmedLinks,ambiguousLinks,
            unlinkedEnergy,trained(),holdouts,confidence());
    }
    /** Cross-life statistical evidence is durable. Local object IDs and
     * event states deliberately are not, preventing false ID resurrection. */
    public String encode(){
        StringBuilder b=new StringBuilder("cs23");
        for(long n:new long[]{interventions,confirmedLinks,ambiguousLinks,
            unlinkedEnergy,expirations,holdouts,validLinks,invalid,completed,qualifiedMoves})
            b.append('|').append(n);
        b.append('|').append(modelLoss).append('|').append(referenceLoss);
        for(long[] bank:new long[][]{attempts,success,pairAttempts,pairSuccess})
            for(long n:bank)b.append('|').append(n);
        return b.toString();
    }
    public boolean restore(String saved){
        if(saved==null||saved.length()>30000||!saved.startsWith("cs23|"))return false;
        try{
            String[] v=saved.split("\\|",-1);
            if(v.length!=13+2*A+2*A*A)return false;
            int i=1;long[] h=new long[10];
            for(int k=0;k<10;k++){h[k]=Long.parseLong(v[i++]);
                if(h[k]<0||h[k]>1000000000L)return false;}
            double err=Double.parseDouble(v[i++]),base=Double.parseDouble(v[i++]);
            if(!Double.isFinite(err)||!Double.isFinite(base)||err<0||base<0||
               err>h[5]+.001||base>h[5]+.001)return false;
            long[][] data={new long[A],new long[A],new long[A*A],new long[A*A]};
            for(long[] bank:data)for(int j=0;j<bank.length;j++){
                bank[j]=Long.parseLong(v[i++]);
                if(bank[j]<0||bank[j]>1000000000L)return false;
            }
            long counted=0;
            for(int a=0;a<A;a++){
                if(data[1][a]>data[0][a])return false;
                counted+=data[0][a];
            }
            for(int j=0;j<A*A;j++)if(data[3][j]>data[2][j])return false;
            if(counted>h[9]||h[1]>h[9]||h[6]>h[5])return false;
            interventions=h[0];confirmedLinks=h[1];ambiguousLinks=h[2];
            unlinkedEnergy=h[3];expirations=h[4];holdouts=h[5];
            validLinks=h[6];invalid=h[7];completed=h[8];qualifiedMoves=h[9];
            modelLoss=err;referenceLoss=base;
            for(int j=0;j<4;j++)
                System.arraycopy(data[j],0,new long[][]{attempts,success,pairAttempts,pairSuccess}[j],0,data[j].length);
            resumeLife();return true;
        }catch(RuntimeException ex){return false;}
    }
}
