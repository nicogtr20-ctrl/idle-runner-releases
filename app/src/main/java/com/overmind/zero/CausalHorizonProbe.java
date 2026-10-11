package com.overmind.zero;

import java.util.Arrays;

/**
 * EXPERIMENT 0117. Read-only, empirical causal-horizon probe.
 *
 * A small graph of recurring *observed sensory configurations* and actual
 * transitions. No object labels, world coordinates, door identities, or
 * scripted actions. Edges are trained ONLY by real physical decisions, never
 * by imagined rollouts. Actual energy from lived contacts is the sole
 * outcome used by the counterfactual ranking; external displacement is
 * diagnostic, not a fabricated reward.
 *
 * This class NEVER controls ZERO. A hypothetical action is reported only
 * after a separate held-out predictor beats the unchanged-sensation baseline.
 * Generalization is limited by visual aliasing and nonstationary scenes.
 */
public final class CausalHorizonProbe {
    public static final String KEY="causal_horizon_shadow_v117";
    public static final int WIDTH=SensorimotorMind.S;
    public static final int ACTIONS=SensorimotorMind.ACTIONS;
    public static final int MAX_STATES=48;
    public static final int HORIZON=5;
    private static final float GAMMA=.78f;
    private static final float MATCH_RADIUS=.055f;
    private static final float MAX_CHANNEL_DELTA=.18f;

    private final float[][] states=new float[MAX_STATES][WIDTH];
    private final Edge[][] edges=new Edge[MAX_STATES][ACTIONS];
    private int stateCount=0;
    private static final class Edge {
        int count=0,next=-1,matches=0;
        float energySum=0,physicalEvents=0;
    }
    public int transitions=0,validations=0,shadowEligible=0,
               shadowDisagreements=0,unrepresented=0,ambiguous=0;
    public float predictionErrorSum=0,baselineErrorSum=0;
    public int lastSuggested=-1;
    public float lastMargin=0;

    public CausalHorizonProbe(){
        for(int i=0;i<MAX_STATES;i++)
            for(int a=0;a<ACTIONS;a++)edges[i][a]=new Edge();
    }
    private static boolean valid(float[] s){
        if(s==null||s.length!=WIDTH)return false;
        for(float v:s)if(!Float.isFinite(v)||v< -1.6f||v>1.6f)return false;
        return true;
    }
    /** Ignore energy/velocity as object identity: state is a raw tactile,
     * obstacle and optical configuration, without semantic labels. */
    private static float difference(float[] a,float[] b){
        float sum=0,max=0;
        for(int j=4;j<WIDTH;j++){
            float diff=Math.abs(a[j]-b[j]);
            sum+=diff*diff;max=Math.max(max,diff);
        }
        return max>MAX_CHANNEL_DELTA?2f:
            (float)Math.sqrt(sum/(WIDTH-4));
    }
    private int find(float[] s,boolean allowCreate){
        int best=-1;float dist=MATCH_RADIUS;
        for(int i=0;i<stateCount;i++){
            float d=difference(states[i],s);
            if(d<=dist){dist=d;best=i;}
        }
        if(best>=0||!allowCreate)return best;
        if(stateCount==MAX_STATES){unrepresented++;return -1;}
        int n=stateCount++;
        System.arraycopy(s,0,states[n],0,WIDTH);
        return n;
    }
    private static float reward(float energy){
        return Math.max(-1.5f,Math.min(1.5f,energy/22f));
    }
    /** All inputs are from one REAL, completed sensorimotor transition.
     * holdout samples are never used to fit nodes/edges or outcome means. */
    public void observe(float[] before,int action,float[] after,
                        float realEnergy,float physicalEffect,boolean train,
                        boolean holdout){
        if(!valid(before)||!valid(after)||action<0||action>=ACTIONS||
           !Float.isFinite(realEnergy)||!Float.isFinite(physicalEffect))return;
        transitions++;
        if(holdout){
            int source=find(before,false);
            if(source<0)return;
            Edge e=edges[source][action];
            if(e.count<3||e.next<0||e.matches*4<e.count*3)return;
            float actual=differenceUncapped(states[e.next],after);
            float stationary=differenceUncapped(before,after);
            if(stationary<.005f)return;
            validations++;
            predictionErrorSum+=actual;
            baselineErrorSum+=stationary;
            return;
        }
        if(!train)return;
        int source=find(before,true);
        int target=find(after,true);
        if(source<0||target<0)return;
        Edge e=edges[source][action];
        e.count++;
        if(e.next<0){e.next=target;e.matches=1;}
        else if(e.next==target)e.matches++;
        else ambiguous++;
        // A nonstationary, multi-modal edge loses reliability naturally.
        e.energySum+=reward(realEnergy);
        if(physicalEffect>.015f)e.physicalEvents++;
    }
    private static float differenceUncapped(float[] a,float[] b){
        float sum=0;
        for(int j=4;j<WIDTH;j++){
            float d=a[j]-b[j];sum+=d*d;
        }
        return (float)Math.sqrt(sum/(WIDTH-4));
    }
    /** Comparable held-out sensory transitions required before ANY hypothesis.
     * This remains diagnostic-only even after passing the confidence gate. */
    public float confidence(){
        if(validations<30||baselineErrorSum<=.001f)return 0;
        float improvement=1-predictionErrorSum/baselineErrorSum;
        if(!Float.isFinite(improvement)||improvement<.15f)return 0;
        return Math.min(.7f,improvement);
    }
    private boolean reliable(Edge e){
        return e.count>=3&&e.next>=0&&e.matches*4>=e.count*3;
    }
    private float rollout(int state,int depth,long visited){
        if(depth<=0||state<0)return 0;
        if((visited&(1L<<state))!=0)return 0;
        visited|=1L<<state;
        float best=0; // The agent may abstain; no imaginary positive future.
        for(int a=0;a<ACTIONS;a++){
            Edge e=edges[state][a];
            if(!reliable(e))continue;
            float v=e.energySum/e.count;
            if(e.next!=state&&(visited&(1L<<e.next))==0)
                v+=GAMMA*rollout(e.next,depth-1,visited);
            best=Math.max(best,v);
        }
        return best;
    }
    /** Hypothetical proposal only. NEVER changes the selected action. */
    public int shadowChoice(float[] senses,int chosen){
        lastSuggested=-1;lastMargin=0;
        if(!valid(senses)||confidence()<=0)return -1;
        int node=find(senses,false);
        if(node<0)return -1;
        int first=-1,second=-1;
        float best=-Float.MAX_VALUE,runner=-Float.MAX_VALUE;
        for(int a=0;a<ACTIONS;a++){
            Edge e=edges[node][a];
            if(!reliable(e))continue;
            float score=e.energySum/e.count;
            if(e.next!=node)
                score+=GAMMA*rollout(e.next,HORIZON-1,1L<<node);
            if(score>best){second=first;runner=best;first=a;best=score;}
            else if(score>runner){second=a;runner=score;}
        }
        if(first<0||second<0||best-runner<.03f)return -1;
        shadowEligible++;
        if(first!=chosen)shadowDisagreements++;
        lastSuggested=first;lastMargin=best-runner;
        return first;
    }
    /** No transient action buffer; no learnable experience crosses lives. */
    public void resetLife(){lastSuggested=-1;lastMargin=0;}

    public String encode(){
        StringBuilder b=new StringBuilder("ch1|").append(transitions).append('|')
          .append(validations).append('|').append(shadowEligible).append('|')
          .append(shadowDisagreements).append('|').append(unrepresented).append('|')
          .append(ambiguous).append('|').append(predictionErrorSum).append('|')
          .append(baselineErrorSum).append('|').append(stateCount);
        for(int i=0;i<stateCount;i++){
            b.append('|');
            for(int j=0;j<WIDTH;j++){
                if(j>0)b.append(',');
                b.append(states[i][j]);
            }
        }
        int used=0;
        for(int i=0;i<stateCount;i++)for(Edge e:edges[i])
            if(e.count>0)used++;
        b.append('|').append(used);
        for(int i=0;i<stateCount;i++)for(int a=0;a<ACTIONS;a++){
            Edge e=edges[i][a];
            if(e.count==0)continue;
            b.append('|').append(i).append(',').append(a).append(',')
              .append(e.count).append(',').append(e.next).append(',')
              .append(e.matches).append(',').append(e.energySum)
              .append(',').append(e.physicalEvents);
        }
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>150000||!raw.startsWith("ch1|"))
            return false;
        try{
            String[] p=raw.split("\\|",-1);
            if(p.length<11)return false;
            int t=Integer.parseInt(p[1]),v=Integer.parseInt(p[2]),
                se=Integer.parseInt(p[3]),sd=Integer.parseInt(p[4]),
                un=Integer.parseInt(p[5]),am=Integer.parseInt(p[6]);
            float pred=Float.parseFloat(p[7]),base=Float.parseFloat(p[8]);
            int n=Integer.parseInt(p[9]);
            if(t<0||v<0||v>t||se<0||sd<0||sd>se||un<0||am<0||
               n<0||n>MAX_STATES||!Float.isFinite(pred)||
               !Float.isFinite(base)||pred<0||base<0)return false;
            int index=10;
            CausalHorizonProbe candidate=new CausalHorizonProbe();
            candidate.stateCount=n;
            for(int i=0;i<n;i++){
                String[] row=p[index++].split(",",-1);
                if(row.length!=WIDTH)return false;
                for(int j=0;j<WIDTH;j++)
                    candidate.states[i][j]=Float.parseFloat(row[j]);
                if(!valid(candidate.states[i]))return false;
            }
            int count=Integer.parseInt(p[index++]);
            if(count<0||count>n*ACTIONS||p.length!=index+count)return false;
            for(int k=0;k<count;k++){
                String[] bits=p[index++].split(",",-1);
                if(bits.length!=7)return false;
                int s=Integer.parseInt(bits[0]),a=Integer.parseInt(bits[1]),
                    c=Integer.parseInt(bits[2]),dest=Integer.parseInt(bits[3]),
                    matches=Integer.parseInt(bits[4]);
                float energy=Float.parseFloat(bits[5]),phys=Float.parseFloat(bits[6]);
                if(s<0||s>=n||a<0||a>=ACTIONS||c<1||
                   dest<0||dest>=n||matches<1||matches>c||
                   !Float.isFinite(energy)||!Float.isFinite(phys)||
                   Math.abs(energy)>c*1.501f||phys<0||phys>c||
                   candidate.edges[s][a].count!=0)return false;
                Edge e=candidate.edges[s][a];
                e.count=c;e.next=dest;e.matches=matches;
                e.energySum=energy;e.physicalEvents=phys;
            }
            for(int i=0;i<MAX_STATES;i++){
                System.arraycopy(candidate.states[i],0,states[i],0,WIDTH);
                for(int a=0;a<ACTIONS;a++){
                    Edge src=candidate.edges[i][a],dst=edges[i][a];
                    dst.count=src.count;dst.next=src.next;dst.matches=src.matches;
                    dst.energySum=src.energySum;dst.physicalEvents=src.physicalEvents;
                }
            }
            stateCount=n;transitions=t;validations=v;shadowEligible=se;
            shadowDisagreements=sd;unrepresented=un;ambiguous=am;
            predictionErrorSum=pred;baselineErrorSum=base;
            resetLife();return true;
        }catch(RuntimeException invalid){return false;}
    }
    private static float[] state(float marker){
        float[] s=new float[WIDTH];s[6]=marker;return s;
    }
    private static void check(boolean ok,String message){
        if(!ok)throw new AssertionError(message);
    }
    public static void main(String[] args){
        CausalHorizonProbe m=new CausalHorizonProbe();
        float[] s0=state(0),s1=state(.55f),s2=state(1.10f),
                s3=state(-.55f);
        check(m.shadowChoice(s0,2)==-1,"Virgin module cannot suggest");
        // Real samples only: 0->1 gives no immediate energy, 1->2 yields
        // an actual delayed energy event. 0->3 is an unrewarded alternative.
        for(int n=0;n<60;n++){
            m.observe(s0,0,s1,0,0,true,false);
            m.observe(s1,1,s2,22,0,true,false);
            m.observe(s0,2,s3,0,0,true,false);
            m.observe(s3,3,s3,0,0,true,false);
        }
        check(m.shadowChoice(s0,2)==-1,"No holdout must imply no confidence");
        for(int n=0;n<40;n++){
            m.observe(s0,0,s1,0,0,false,true);
            m.observe(s1,1,s2,22,0,false,true);
            m.observe(s0,2,s3,0,0,false,true);
        }
        check(m.confidence()>0,"Real heldout transitions did not enable shadow");
        check(m.shadowChoice(s0,2)==0,
              "A verified delayed-energy chain should beat empty immediate action");
        check(m.shadowDisagreements==1&&m.shadowEligible==1,
              "Shadow must only count, never replace real motor decisions");
        String valid=m.encode();
        CausalHorizonProbe restored=new CausalHorizonProbe();
        check(restored.restore(valid)&&restored.shadowChoice(s0,2)==0,
              "Experiences must survive save slots without replaying a life");
        int old=restored.transitions;
        check(!restored.restore(valid+"|bad")&&restored.transitions==old,
              "Bad payload must not partially erase graph");
        CausalHorizonProbe inaccurate=new CausalHorizonProbe();
        for(int i=0;i<50;i++)inaccurate.observe(s0,0,s1,0,0,true,false);
        for(int i=0;i<50;i++)inaccurate.observe(s0,0,s3,0,0,false,true);
        check(inaccurate.confidence()==0&&inaccurate.shadowChoice(s0,0)==-1,
              "Wrong heldout world model must abstain");
        m.resetLife();
        check(m.shadowChoice(s0,2)==0,"Death must preserve learned causal graph");
        System.out.println("ZERO 0117 causal-horizon shadow graph: delayed outcomes, honest heldout, 5-step lookahead, no actuation, persistence passed");
    }
}
