package com.overmind.zero;

import java.util.Locale;

/**
 * ZERO 2.0 Gate F: learns whether ONE ACTUALLY EXECUTED action together
 * with ZERO's OWN 96-point material/grip configuration precedes a REAL
 * observed physical world displacement.
 *
 * Never accesses Entity, cover law, hidden energy, coordinates or matching
 * physics rules. Self configuration is rotation invariant; confidence is
 * heldout Brier improvement over a SAME-CONFIGURATION baseline.
 *
 * SHADOW ONLY. No motor output, no fabricated rewards, no grip override.
 */
public final class GripAffordanceV20 {
    public static final String KEY="grip_affordance_v20";
    static final int ACTIONS=SensorimotorMind.ACTIONS;
    static final int STATES=7,CELLS=ACTIONS*STATES;
    private final int[] count=new int[CELLS],moved=new int[CELLS];
    private final int[] contextCount=new int[STATES],contextMoved=new int[STATES];
    private int pendingAction=-1,pendingState=-1;
    private long observed=0,trained=0,heldout=0,moveEvents=0;
    private long realHeldSnapshots=0,spreadHeldSnapshots=0,invalid=0;
    private float modelErr=Float.NaN,referenceErr=Float.NaN;

    private static float clamp(float v,float min,float max){
        return Math.max(min,Math.min(max,v));
    }
    private static boolean validSurface(Morphology.Surface skin){
        if(skin==null)return false;
        for(int i=0;i<Morphology.POINTS;i++){
            if(!Float.isFinite(skin.touchSense[i])||
               !Float.isFinite(skin.gripCommand[i])||
               !Float.isFinite(skin.gripHold[i])||
               !Float.isFinite(skin.extension[i]))return false;
        }
        return true;
    }
    /**
     * 0 no body contact, 1 body contact only, 2 local command without held,
     * 3/4 genuinely held concentrated material compact/extended,
     * 5/6 genuinely held dispersed material compact/extended.
     * No labels saying "two opposing box faces": this classification
     * is only self-observed material distribution, regardless of objects.
     */
    static int selfState(Morphology.Surface skin){
        if(!validSurface(skin))return -1;
        int contact=0,commanded=0,held=0,extended=0;
        float cos=0,sin=0;
        for(int i=0;i<Morphology.POINTS;i++){
            if(skin.touchSense[i]>.16f||skin.gripHold[i]>.02f)contact++;
            if(skin.gripCommand[i]>Morphology.GRIP_THRESHOLD)commanded++;
            if(skin.extension[i]>22f)extended++;
            if(skin.gripHold[i]>=Morphology.GRIP_HOLD&&
               skin.gripCommand[i]>Morphology.GRIP_THRESHOLD){
                held++;
                float angle=(float)(2*Math.PI*i/Morphology.POINTS);
                cos+=(float)Math.cos(angle);
                sin+=(float)Math.sin(angle);
            }
        }
        if(held==0){
            if(contact==0)return 0;
            return commanded==0?1:2;
        }
        float concentration=(float)Math.hypot(cos,sin)/held;
        boolean dispersed=held>=2&&concentration<.68f;
        boolean stretched=extended>=3;
        return (dispersed?5:3)+(stretched?1:0);
    }
    private static float probability(int n,int m){
        // Strong prior for NO observed displacement. Training data alone
        // can move the estimate off that baseline.
        return (m+.5f)/(n+24f);
    }
    public float forecast(int action,Morphology.Surface skin){
        int state=selfState(skin);
        if(state<0||action<0||action>=ACTIONS)return Float.NaN;
        int slot=state*ACTIONS+action;
        return probability(count[slot],moved[slot]);
    }
    public void chosen(int action,Morphology.Surface skin){
        int state=selfState(skin);
        pendingAction=-1;pendingState=-1;
        if(state<0||action<0||action>=ACTIONS){
            // Initial life action has no preceding physical surface sample.
            if(skin!=null)invalid++;
            return;
        }
        pendingAction=action;pendingState=state;
        if(state>=3)realHeldSnapshots++;
        if(state>=5)spreadHeldSnapshots++;
    }
    public void consequence(int action,float actualExternalDisplacement,
                            boolean training,boolean holdout){
        if(pendingAction<0)return;
        int ctx=pendingState,chosenAction=pendingAction;
        pendingAction=-1;pendingState=-1;
        if(ctx<0||action<0||action>=ACTIONS||action!=chosenAction||
           !Float.isFinite(actualExternalDisplacement)||
           actualExternalDisplacement<0||actualExternalDisplacement>1e7f){
            invalid++;
            return;
        }
        boolean changed=actualExternalDisplacement>.015f;
        float label=changed?1f:0f;
        int slot=ctx*ACTIONS+action;
        if(holdout){
            float predicted=probability(count[slot],moved[slot]);
            float naive=probability(contextCount[ctx],contextMoved[ctx]);
            float e=(label-predicted)*(label-predicted);
            float b=(label-naive)*(label-naive);
            float alpha=heldout==0?1f:.045f;
            modelErr=heldout==0?e:modelErr*(1f-alpha)+e*alpha;
            referenceErr=heldout==0?b:referenceErr*(1f-alpha)+b*alpha;
            heldout++;
        }else if(training){
            if(count[slot]<Integer.MAX_VALUE&&contextCount[ctx]<Integer.MAX_VALUE){
                count[slot]++;contextCount[ctx]++;
                if(changed){moved[slot]++;contextMoved[ctx]++;}
                trained++;
            }
        }
        observed++;
        if(changed)moveEvents++;
    }
    public void resetLife(){pendingAction=-1;pendingState=-1;}
    public void resumeLife(){resetLife();}
    public long observed(){return observed;}
    public long trained(){return trained;}
    public long validations(){return heldout;}
    public long motionEvents(){return moveEvents;}
    public long realHeld(){return realHeldSnapshots;}
    public long spreadHeld(){return spreadHeldSnapshots;}
    public long invalid(){return invalid;}
    public float heldoutError(){return modelErr;}
    public float baselineError(){return referenceErr;}
    public int contextsWithAlternatives(){
        int n=0;
        for(int state=0;state<STATES;state++){
            int distinct=0;
            for(int a=0;a<ACTIONS;a++)
                if(count[state*ACTIONS+a]>=5)distinct++;
            if(distinct>=2&&contextCount[state]>=45)n++;
        }
        return n;
    }
    public float confidence(){
        if(heldout<80||moveEvents<12||contextsWithAlternatives()<1||
           !Float.isFinite(modelErr)||!Float.isFinite(referenceErr)||
           referenceErr<.035f||modelErr>=referenceErr*.94f)return 0;
        return clamp((referenceErr-modelErr)/referenceErr,0,.38f);
    }
    public String encode(){
        StringBuilder out=new StringBuilder("ga20|").append(observed)
            .append('|').append(trained).append('|').append(heldout)
            .append('|').append(moveEvents).append('|').append(realHeldSnapshots)
            .append('|').append(spreadHeldSnapshots).append('|').append(invalid)
            .append('|').append(Float.isFinite(modelErr)?modelErr:-1f)
            .append('|').append(Float.isFinite(referenceErr)?referenceErr:-1f);
        for(int n:count)out.append('|').append(n);
        for(int n:moved)out.append('|').append(n);
        for(int n:contextCount)out.append('|').append(n);
        for(int n:contextMoved)out.append('|').append(n);
        return out.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>24000||!raw.startsWith("ga20|"))return false;
        String[] p=raw.split("\\|",-1);
        if(p.length!=10+2*CELLS+2*STATES)return false;
        try{
            long[] h=new long[7];
            for(int i=0;i<h.length;i++){
                h[i]=Long.parseLong(p[i+1]);
                if(h[i]<0)return false;
            }
            float error=Float.parseFloat(p[8]),base=Float.parseFloat(p[9]);
            if(!Float.isFinite(error)||!Float.isFinite(base)||
               error< -1||base< -1||error>1||base>1||
               h[1]+h[2]!=h[0]||h[5]>h[4]||
               h[3]>h[0])return false;
            int[] cn=new int[CELLS],mv=new int[CELLS],
                cc=new int[STATES],cm=new int[STATES];
            int k=10;
            for(int i=0;i<CELLS;i++)cn[i]=Integer.parseInt(p[k++]);
            for(int i=0;i<CELLS;i++)mv[i]=Integer.parseInt(p[k++]);
            for(int i=0;i<STATES;i++)cc[i]=Integer.parseInt(p[k++]);
            for(int i=0;i<STATES;i++)cm[i]=Integer.parseInt(p[k++]);
            long total=0;
            for(int state=0;state<STATES;state++){
                if(cc[state]<0||cm[state]<0||cm[state]>cc[state])return false;
                long nc=0,nm=0;
                for(int a=0;a<ACTIONS;a++){
                    int pos=state*ACTIONS+a;
                    if(cn[pos]<0||mv[pos]<0||mv[pos]>cn[pos])return false;
                    nc+=cn[pos];nm+=mv[pos];
                }
                if(nc!=cc[state]||nm!=cm[state])return false;
                total+=nc;
            }
            if(total!=h[1])return false;
            System.arraycopy(cn,0,count,0,CELLS);
            System.arraycopy(mv,0,moved,0,CELLS);
            System.arraycopy(cc,0,contextCount,0,STATES);
            System.arraycopy(cm,0,contextMoved,0,STATES);
            observed=h[0];trained=h[1];heldout=h[2];moveEvents=h[3];
            realHeldSnapshots=h[4];spreadHeldSnapshots=h[5];invalid=h[6];
            modelErr=error<0?Float.NaN:error;referenceErr=base<0?Float.NaN:base;
            resetLife();return true;
        }catch(RuntimeException bad){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
            "ZERO2 grasp self-model: %d observed, %d real environmental moves, "+
            "%d held snapshots (%d distributed), heldout %.5f vs %.5f "+
            "confidence %.3f (SHADOW, NO ACTUATION)",
            observed,moveEvents,realHeldSnapshots,spreadHeldSnapshots,
            modelErr,referenceErr,confidence());
    }
}
