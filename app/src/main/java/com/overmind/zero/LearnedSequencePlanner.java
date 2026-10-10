package com.overmind.zero;

/**
 * Tiny, receding-horizon, model-based lookahead using ZERO's OWN learned
 * one-action forward model and learned action values. There is no map,
 * object ID, symbolic gate, hand-written heading, or action to "open".
 *
 * Planning is disabled unless HOLDOUT sensory prediction beats the
 * unchanged-state reference. Predictions are counterfactual estimates, NOT
 * simulated real physics and NOT rewarded as actual experience.
 */
public final class LearnedSequencePlanner {
    public interface Model {
        float[] predict(float[] state,int action);
        float value(float[] state,int action);
    }
    public static final int ACTIONS=16,DEPTH=3,BEAM=2;
    public int attempts=0,enabled=0,abstained=0,changed=0;
    public float lastConfidence=0,lastLargestAdjustment=0;
    private static float clamp(float x,float min,float max){
        return Math.max(min,Math.min(max,x));
    }
    public static float confidence(LearningDiagnostics d){
        if(d==null||d.heldoutTransitions<30||d.points()<4||
           !Float.isFinite(d.modelError)||!Float.isFinite(d.baselineError))
            return 0;
        // RE-CHECK the most recent held-out windows. Lifetime average may
        // conceal new learning regressions or broken predictions.
        float error=0,baseline=0;
        int samples=Math.min(6,d.points());
        for(int i=d.points()-samples;i<d.points();i++){
            error+=d.errorAt(i);
            baseline+=d.baselineAt(i);
        }
        error/=samples;baseline/=samples;
        if(baseline<=.018f||error>=baseline*.94f)return 0;
        return clamp((baseline-error)/baseline,0,.75f);
    }
    private static boolean valid(float[] s){
        if(s==null||s.length!=SensorimotorMind.S)return false;
        for(float v:s)if(!Float.isFinite(v)||Math.abs(v)>1.6f)return false;
        return true;
    }
    private static float safeValue(Model m,float[] state,int action){
        float v=m.value(state,action);
        return Float.isFinite(v)?clamp(v,-9f,9f):0f;
    }
    private static int[] topTwo(Model m,float[] state){
        int first=-1,second=-1;float top=-Float.MAX_VALUE,next=-Float.MAX_VALUE;
        for(int action=0;action<ACTIONS;action++){
            float v=safeValue(m,state,action);
            if(v>top){second=first;next=top;first=action;top=v;}
            else if(v>next){second=action;next=v;}
        }
        return new int[]{first,second};
    }
    private static float best(Model m,float[] state){
        float v=-Float.MAX_VALUE;
        for(int i=0;i<ACTIONS;i++)v=Math.max(v,safeValue(m,state,i));
        return v;
    }
    private static float[] safePredict(Model m,float[] state,int a){
        float[] out=m.predict(state,a);
        return valid(out)?out:null;
    }
    /**
     * Zero adjustment if evidence is inadequate. Once validated, computes
     * all 16 potential first actions, two best learned continuations, then
     * one more predicted state for each continuation (depth three).
     * Every value is independently learned in SensorimotorMind/ExperienceCredit.
     * The bounded adjustment is ADVICE, never a forced decision.
     */
    public float[] evaluate(float[] current,Model model,LearningDiagnostics evidence){
        float[] adjustment=new float[ACTIONS];
        attempts++;
        float c=confidence(evidence);
        lastConfidence=c;
        lastLargestAdjustment=0;
        if(c==0||!valid(current)||model==null){
            abstained++;return adjustment;
        }
        float[] futures=new float[ACTIONS];
        try{
            for(int first=0;first<ACTIONS;first++){
                float[] next=safePredict(model,current,first);
                if(next==null){abstained++;return adjustment;}
                float immediateFuture=best(model,next);
                int[] follow=topTwo(model,next);
                float longFuture=-Float.MAX_VALUE;
                for(int action:follow){
                    if(action<0)continue;
                    float[] two=safePredict(model,next,action);
                    if(two==null){abstained++;return adjustment;}
                    float following=safeValue(model,next,action);
                    longFuture=Math.max(longFuture,following+
                        .70f*best(model,two));
                }
                // No hand-crafted reward for a specific pixel, color or gate.
                futures[first]=.45f*immediateFuture+.55f*longFuture;
            }
        }catch(RuntimeException predictionFailed){
            abstained++;return adjustment;
        }
        float average=0;
        for(float f:futures)average+=f/ACTIONS;
        if(!Float.isFinite(average)){abstained++;return adjustment;}
        float sum=0;
        for(int a=0;a<ACTIONS;a++){
            adjustment[a]=clamp((futures[a]-average)*.25f*c,-.42f,.42f);
            sum+=Math.abs(adjustment[a]);
            lastLargestAdjustment=Math.max(lastLargestAdjustment,Math.abs(adjustment[a]));
        }
        enabled++;
        if(sum>.001f)changed++;
        return adjustment;
    }
    public String encode(){
        return "lp1|"+attempts+"|"+enabled+"|"+abstained+"|"+changed;
    }
    public boolean restore(String raw){
        if(raw==null||!raw.startsWith("lp1|")||raw.length()>90)return false;
        try{
            String[] p=raw.split("\\|",-1);
            if(p.length!=5)return false;
            int a=Integer.parseInt(p[1]),e=Integer.parseInt(p[2]),
                s=Integer.parseInt(p[3]),c=Integer.parseInt(p[4]);
            if(a<0||e<0||s<0||c<0||e+s>a||c>e)return false;
            attempts=a;enabled=e;abstained=s;changed=c;
            lastConfidence=lastLargestAdjustment=0;
            return true;
        }catch(RuntimeException e){return false;}
    }
    public static void main(String[] args){
        LearnedSequencePlanner planner=new LearnedSequencePlanner();
        LearningDiagnostics poor=new LearningDiagnostics();
        for(int i=0;i<300;i++)poor.record(.35f,.20f,0,false);
        float[] blank=new float[SensorimotorMind.S];
        Model toy=new Model(){
            public float[] predict(float[] state,int action){
                float[] out=state.clone();
                if(action==5)out[6]=.8f;
                else if(action==2&&state[6]>.5f)out[7]=.8f;
                return out;
            }
            public float value(float[] state,int action){
                // This is a synthetic TEST of delayed utility; no target
                // or hardcoded action exists in production learned values.
                return action==8&&state[7]>.5f?3f:
                       action==2&&state[6]>.5f?.7f:0f;
            }
        };
        for(float n:planner.evaluate(blank,toy,poor))
            if(n!=0)throw new AssertionError("Untrusted model was allowed to steer ZERO");
        LearningDiagnostics good=new LearningDiagnostics();
        for(int i=0;i<300;i++)good.record(.07f,.28f,0,false);
        float[] plan=planner.evaluate(blank,toy,good);
        if(plan[5]<=plan[0]||planner.enabled!=1||planner.lastConfidence<=0)
            throw new AssertionError("Two actions of learned consequence not compared");
        LearningDiagnostics mediocre=new LearningDiagnostics();
        for(int i=0;i<300;i++)mediocre.record(.273f,.28f,0,false);
        for(float n:planner.evaluate(blank,toy,mediocre))
            if(n!=0)throw new AssertionError("Unreliable predictions were trusted");
        String stored=planner.encode();
        LearnedSequencePlanner loaded=new LearnedSequencePlanner();
        if(!loaded.restore(stored)||loaded.attempts!=3||loaded.enabled!=1)
            throw new AssertionError("Planner statistics lost");
        if(loaded.restore(stored+"garbage")||!stored.equals(loaded.encode()))
            throw new AssertionError("Corrupt planner state damaged earlier statistics");
        System.out.println("ZERO 0.10.2 three-step learned lookahead, empirical holdout gating, bounds and legacy-safe persistence passed");
    }
}
