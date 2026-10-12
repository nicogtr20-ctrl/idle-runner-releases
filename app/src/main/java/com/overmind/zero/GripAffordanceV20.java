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
    // No trigonometric work or per-frame allocation on the S21 35ms path.
    private static final float[] SIN=new float[Morphology.POINTS];
    private static final float[] COS=new float[Morphology.POINTS];
    static {
        for(int i=0;i<Morphology.POINTS;i++){
            double a=2.0*Math.PI*i/Morphology.POINTS;
            SIN[i]=(float)Math.sin(a);
            COS[i]=(float)Math.cos(a);
        }
    }
    private final int[] count=new int[CELLS],moved=new int[CELLS];
    private final int[] contextCount=new int[STATES],contextMoved=new int[STATES];
    private int pendingAction=-1,pendingState=-1;
    private int peakState=-1,intervalSamples=0,lastMicroState=-1;
    private boolean sawHeldRelease=false,sawSpreadRelease=false;
    // Honest frequency-resolved, globally bounded counters, no frame arrays.
    private long physicsSamples=0,physicsHeldFrames=0,physicsSpreadFrames=0;
    private long fleetingHeldIntervals=0,fleetingSpreadIntervals=0;
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
                cos+=COS[i];
                sin+=SIN[i];
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
        // Starting posture is an observation at the decision boundary,
        // not evidence of a grip during the NEW action's physical frames.
        peakState=-1;intervalSamples=0;lastMicroState=-1;
        sawHeldRelease=false;sawSpreadRelease=false;
        if(state>=3)realHeldSnapshots++;
        if(state>=5)spreadHeldSnapshots++;
    }
    /** One complete physical frame AFTER true collision/grip composition.
     * Samples 96 sites simultaneously, never merging held points from
     * different moments into one fictional distributed grip. No motor effect.
     * Executes without heap allocation at each subframe. */
    public void physicsFrame(Morphology.Surface skin){
        if(pendingAction<0)return;
        int state=selfState(skin);
        if(state<0){invalid++;return;}
        // A momentary hold is only counted as transient if its genuine
        // release is observed BEFORE the same action interval ends.
        // A newly engaged grip that remains held is not a "brief" one.
        if(lastMicroState>=3&&state<3)sawHeldRelease=true;
        if(lastMicroState>=5&&state<5)sawSpreadRelease=true;
        lastMicroState=state;
        physicsSamples++;intervalSamples++;
        peakState=Math.max(peakState,state);
        if(state>=3)physicsHeldFrames++;
        if(state>=5)physicsSpreadFrames++;
    }
    public long physicsSamples(){return physicsSamples;}
    public long physicsHeldFrames(){return physicsHeldFrames;}
    public long physicsSpreadFrames(){return physicsSpreadFrames;}
    public long fleetingHeldIntervals(){return fleetingHeldIntervals;}
    public long fleetingSpreadIntervals(){return fleetingSpreadIntervals;}
    public void consequence(int action,float actualExternalDisplacement,
                            boolean training,boolean holdout){
        if(pendingAction<0)return;
        int ctx=intervalSamples>0?peakState:pendingState;
        int chosenAction=pendingAction;
        boolean heldReleased=sawHeldRelease,spreadReleased=sawSpreadRelease;
        resetLife();
        if(ctx<0||action<0||action>=ACTIONS||action!=chosenAction||
           !Float.isFinite(actualExternalDisplacement)||
           actualExternalDisplacement<0||actualExternalDisplacement>1e7f){
            invalid++;
            return;
        }
        // Learning-disabled intervals are not training or validation
        // examples. Otherwise saved observed!=trained+heldout after pausing
        // learning, making an intact named brain fail its own restore.
        if(!training&&!holdout)return;
        if(heldReleased)fleetingHeldIntervals++;
        if(spreadReleased)fleetingSpreadIntervals++;
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
    public void resetLife(){
        pendingAction=-1;pendingState=-1;peakState=-1;intervalSamples=0;
        lastMicroState=-1;sawHeldRelease=false;sawSpreadRelease=false;
    }
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
        StringBuilder out=new StringBuilder("ga21|").append(observed)
            .append('|').append(trained).append('|').append(heldout)
            .append('|').append(moveEvents).append('|').append(realHeldSnapshots)
            .append('|').append(spreadHeldSnapshots).append('|').append(invalid)
            .append('|').append(Float.isFinite(modelErr)?modelErr:-1f)
            .append('|').append(Float.isFinite(referenceErr)?referenceErr:-1f);
        for(int n:count)out.append('|').append(n);
        for(int n:moved)out.append('|').append(n);
        for(int n:contextCount)out.append('|').append(n);
        for(int n:contextMoved)out.append('|').append(n);
        out.append('|').append(physicsSamples).append('|').append(physicsHeldFrames)
            .append('|').append(physicsSpreadFrames)
            .append('|').append(fleetingHeldIntervals)
            .append('|').append(fleetingSpreadIntervals);
        return out.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>24000)return false;
        boolean legacy=raw.startsWith("ga20|"),newVersion=raw.startsWith("ga21|");
        if(!legacy&&!newVersion)return false;
        String[] p=raw.split("\\|",-1);
        if(p.length!=10+2*CELLS+2*STATES+(newVersion?5:0))return false;
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
            long[] micro=new long[5];
            if(newVersion){
                for(int i=0;i<5;i++){
                    micro[i]=Long.parseLong(p[k++]);
                    if(micro[i]<0)return false;
                }
                if(micro[1]>micro[0]||micro[2]>micro[1]||
                   micro[3]>h[0]||micro[4]>h[0]||
                   micro[4]>micro[2])return false;
            }
            System.arraycopy(cn,0,count,0,CELLS);
            System.arraycopy(mv,0,moved,0,CELLS);
            System.arraycopy(cc,0,contextCount,0,STATES);
            System.arraycopy(cm,0,contextMoved,0,STATES);
            observed=h[0];trained=h[1];heldout=h[2];moveEvents=h[3];
            realHeldSnapshots=h[4];spreadHeldSnapshots=h[5];invalid=h[6];
            modelErr=error<0?Float.NaN:error;referenceErr=base<0?Float.NaN:base;
            physicsSamples=micro[0];physicsHeldFrames=micro[1];
            physicsSpreadFrames=micro[2];
            fleetingHeldIntervals=micro[3];fleetingSpreadIntervals=micro[4];
            resetLife();return true;
        }catch(RuntimeException bad){return false;}
    }
    public String summary(){
        return String.format(Locale.US,
            "ZERO2 grasp self-model: %d observed, %d real environmental moves, "+
            "%d held snapshots (%d distributed), subframes %d held %d spread %d, "+
            "heldout %.5f vs %.5f "+
            "confidence %.3f (SHADOW, NO ACTUATION)",
            observed,moveEvents,realHeldSnapshots,spreadHeldSnapshots,
            physicsSamples,physicsHeldFrames,physicsSpreadFrames,
            modelErr,referenceErr,confidence());
    }
}
