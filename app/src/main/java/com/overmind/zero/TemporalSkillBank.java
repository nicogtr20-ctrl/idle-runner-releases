package com.overmind.zero;

import java.util.ArrayList;

/**
 * ZERO 0.10.7: autonomous temporal skill/chunk discovery.
 *
 * A skill is not authored by the program. It is a suffix of ZERO's own recent
 * action/context history that repeatedly preceded a REAL positive energetic or
 * external-physics consequence. Only recurrent temporal context from
 * TemporalWorkingMemory and ZERO's own 16 actions are stored.
 *
 * Skills never replace the policy. A mature skill supplies a bounded bias for
 * its next learned action. Execution is abandoned if ZERO chooses something
 * else or the resulting temporal context diverges from the context originally
 * experienced. Failed replays reduce future confidence.
 */
public final class TemporalSkillBank {
    public static final int MAX_SKILLS=24,MIN_LEN=2,MAX_LEN=8;
    private static final int H=TemporalWorkingMemory.HIDDEN;
    private static final float START_MATCH=.46f,CONTINUE_MATCH=.58f;

    public static final class Advice {
        public final int action;
        public final float bias;
        public final int skillIndex;
        public final int step;
        Advice(int action,float bias,int skillIndex,int step){
            this.action=action;this.bias=bias;this.skillIndex=skillIndex;this.step=step;
        }
    }
    private static final class Skill {
        int len,confirmations=1,failures=0,activations=0,completions=0;
        float utility=.1f;
        long lastUsed=0;
        final int[] actions=new int[MAX_LEN];
        final float[][] contexts=new float[MAX_LEN][H];
        float confidence(){
            if(confirmations<2)return 0;
            float repetition=Math.min(1f,(confirmations-1)/4f);
            float reliability=(float)confirmations/(confirmations+failures+1f);
            return repetition*reliability;
        }
    }

    private final ArrayList<Skill> skills=new ArrayList<>();
    private final int[] recentActions=new int[MAX_LEN];
    private final float[][] recentContexts=new float[MAX_LEN][H];
    private int recentSize=0,recentCursor=0;

    private int active=-1,step=0,pendingOutcome=0;
    private boolean actionStarted=false,dirty=false;
    private long clock=0;

    public int discovered=0,suggestions=0,activations=0,completions=0;
    public int failedReplays=0,interruptions=0;

    private static float clamp(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }
    private static boolean validContext(float[] c){
        if(c==null||c.length!=H)return false;
        for(float v:c)if(!Float.isFinite(v)||Math.abs(v)>1.01f)return false;
        return true;
    }
    private static float distance(float[] a,float[] b){
        float sum=0;
        for(int i=0;i<H;i++){float d=a[i]-b[i];sum+=d*d;}
        return (float)Math.sqrt(sum/H);
    }
    public int stored(){return skills.size();}
    public int consolidated(){
        int n=0;for(Skill s:skills)if(s.confirmations>=2)n++;return n;
    }
    public int longest(){
        int n=0;for(Skill s:skills)n=Math.max(n,s.len);return n;
    }
    public int strongestConfirmations(){
        int n=0;for(Skill s:skills)n=Math.max(n,s.confirmations);return n;
    }
    public float bestConfidence(){
        float c=0;for(Skill s:skills)c=Math.max(c,s.confidence());return c;
    }
    /** Read-only inspection. An action number is a learned primitive, NOT a
     * hand-coded target or tutorial. Copies prevent UI mutation of the learner. */
    private Skill inspected(int index){
        if(index<0||index>=skills.size())throw new IndexOutOfBoundsException();
        return skills.get(index);
    }
    public int skillLengthAt(int index){return inspected(index).len;}
    public int skillConfirmationsAt(int index){return inspected(index).confirmations;}
    public int skillFailuresAt(int index){return inspected(index).failures;}
    public int skillCompletionsAt(int index){return inspected(index).completions;}
    public float skillConfidenceAt(int index){return inspected(index).confidence();}
    public int[] skillActionsAt(int index){
        Skill s=inspected(index);
        return java.util.Arrays.copyOf(s.actions,s.len);
    }
    public boolean takeDirty(){boolean d=dirty;dirty=false;return d;}

    /** New life: discard only unfinished sequence/context. Learned chunks live on. */
    public void resetLife(){
        recentSize=recentCursor=0;
        active=-1;step=0;pendingOutcome=0;actionStarted=false;
    }
    /** App resume is not a death. Learned skills remain, but an interrupted
     * micro-sequence is not silently continued without its recent trace. */
    public void resumeLife(){
        recentSize=recentCursor=0;
        active=-1;step=0;pendingOutcome=0;actionStarted=false;
    }

    /**
     * Return at most one learned next-action suggestion. Temporal confidence
     * comes from held-out validation in TemporalWorkingMemory; if it is zero,
     * skill replay is disabled even when chunks are stored.
     */
    public Advice advice(float[] context,float temporalConfidence){
        if(!validContext(context)||temporalConfidence<=0)return null;
        if(active>=0){
            Skill s=skills.get(active);
            if(pendingOutcome>0||actionStarted)return null;
            float conf=s.confidence();
            if(conf<=0){abort(false);return null;}
            suggestions++;
            float bias=clamp(.14f+.50f*conf*Math.min(1f,temporalConfidence/.35f),
                             .14f,.64f);
            return new Advice(s.actions[step],bias,active,step);
        }
        int best=-1;float bestScore=0,bestMatch=0;
        for(int i=0;i<skills.size();i++){
            Skill s=skills.get(i);
            float conf=s.confidence();
            if(conf<=0)continue;
            float d=distance(context,s.contexts[0]);
            if(d>START_MATCH)continue;
            float match=1-d/START_MATCH;
            float score=match*conf*(.65f+.35f*s.utility)*
                        (1f+.035f*(s.len-MIN_LEN));
            if(score>bestScore){bestScore=score;best=i;bestMatch=match;}
        }
        if(best<0||bestScore<.035f)return null;
        active=best;step=0;pendingOutcome=0;actionStarted=false;
        Skill s=skills.get(best);
        suggestions++;
        float bias=clamp(.12f+.48f*s.confidence()*bestMatch*
                         Math.min(1f,temporalConfidence/.35f),.12f,.62f);
        return new Advice(s.actions[0],bias,best,0);
    }

    /** Record the action actually selected by the existing policy. */
    public void chosen(float[] context,int action){
        if(!validContext(context)||action<0||action>=SensorimotorMind.ACTIONS)return;
        clock++;
        pushRecent(context,action);
        if(active<0||pendingOutcome>0)return;
        Skill s=skills.get(active);
        if(action!=s.actions[step]){
            // ZERO independently chose something else. This is an interruption,
            // not evidence that the stored skill itself failed.
            interruptions++;dirty=true;abort(false);return;
        }
        if(step==0){
            s.activations++;activations++;s.lastUsed=clock;dirty=true;
        }
        actionStarted=true;
    }

    /** Called after the chosen motor action has produced its real sensory result. */
    public void transition(float[] newContext){
        if(active<0||!validContext(newContext))return;
        Skill s=skills.get(active);
        if(actionStarted){
            actionStarted=false;
            if(step+1<s.len){
                float d=distance(newContext,s.contexts[step+1]);
                if(d>CONTINUE_MATCH){
                    // The learned sequence no longer predicts the lived context.
                    s.failures++;failedReplays++;interruptions++;
                    dirty=true;abort(false);return;
                }
                step++;
                return;
            }
            // Last learned action completed. Give the real world a few further
            // decisions to produce the consequence; no imagined success.
            pendingOutcome=3;
            return;
        }
        if(pendingOutcome>0){
            pendingOutcome--;
            if(pendingOutcome==0){
                s.failures++;failedReplays++;dirty=true;abort(false);
            }
        }
    }

    public void energyOutcome(float actualEnergyDelta){
        if(!Float.isFinite(actualEnergyDelta)||Math.abs(actualEnergyDelta)<.01f)return;
        outcome(clamp(actualEnergyDelta/22f,-1f,1f));
    }
    public void physicalOutcome(float displacement){
        if(!Float.isFinite(displacement)||displacement<.8f)return;
        outcome(clamp(displacement/14f,0f,1f));
    }

    private void outcome(float utility){
        if(utility<-.08f){
            if(active>=0){
                Skill s=skills.get(active);
                s.failures++;failedReplays++;dirty=true;abort(false);
            }
            return;
        }
        if(utility<=.08f)return;

        int skip=-1;
        if(active>=0){
            Skill s=skills.get(active);
            if((step==s.len-1&&actionStarted)||pendingOutcome>0){
                s.confirmations++;s.completions++;completions++;
                s.utility=.82f*s.utility+.18f*utility;
                s.lastUsed=clock;dirty=true;skip=active;
                abort(false);
            }
        }
        discoverFromRecent(utility,skip);
    }

    private void pushRecent(float[] context,int action){
        int at=recentCursor;
        recentActions[at]=action;
        System.arraycopy(context,0,recentContexts[at],0,H);
        recentCursor=(recentCursor+1)%MAX_LEN;
        if(recentSize<MAX_LEN)recentSize++;
    }
    private int recentIndexFromEnd(int age){
        return (recentCursor-1-age+MAX_LEN)%MAX_LEN;
    }

    /** Mine every available suffix length 2..8. Single discoveries stay
     * dormant; only repeated matching action/context sequences consolidate. */
    private void discoverFromRecent(float utility,int skipIndex){
        int max=Math.min(MAX_LEN,recentSize);
        for(int len=MIN_LEN;len<=max;len++){
            int[] actions=new int[len];
            float[][] contexts=new float[len][H];
            for(int pos=0;pos<len;pos++){
                int age=len-1-pos;
                int idx=recentIndexFromEnd(age);
                actions[pos]=recentActions[idx];
                System.arraycopy(recentContexts[idx],0,contexts[pos],0,H);
            }
            int found=findMatch(actions,contexts,len,skipIndex);
            if(found>=0){
                Skill s=skills.get(found);
                s.confirmations++;
                s.utility=.84f*s.utility+.16f*utility;
                // Slow prototype adaptation allows the skill to generalize
                // across nearby contexts without erasing its origin.
                for(int p=0;p<len;p++)for(int h=0;h<H;h++)
                    s.contexts[p][h]=.92f*s.contexts[p][h]+.08f*contexts[p][h];
                dirty=true;
            }else{
                Skill fresh=new Skill();fresh.len=len;fresh.utility=utility;
                for(int p=0;p<len;p++){
                    fresh.actions[p]=actions[p];
                    System.arraycopy(contexts[p],0,fresh.contexts[p],0,H);
                }
                addOrReplace(fresh);
                discovered++;dirty=true;
            }
        }
    }

    private int findMatch(int[] actions,float[][] contexts,int len,int skipIndex){
        int best=-1;float bestDistance=Float.MAX_VALUE;
        for(int i=0;i<skills.size();i++){
            if(i==skipIndex)continue;
            Skill s=skills.get(i);
            if(s.len!=len)continue;
            boolean same=true;
            for(int p=0;p<len;p++)if(s.actions[p]!=actions[p]){same=false;break;}
            if(!same)continue;
            float d=0;
            for(int p=0;p<len;p++)d+=distance(s.contexts[p],contexts[p]);
            d/=len;
            if(d<.34f&&d<bestDistance){bestDistance=d;best=i;}
        }
        return best;
    }
    private void addOrReplace(Skill fresh){
        if(skills.size()<MAX_SKILLS){skills.add(fresh);return;}
        int worst=0;float worstScore=Float.MAX_VALUE;
        for(int i=0;i<skills.size();i++){
            Skill s=skills.get(i);
            float score=s.confirmations*1.4f-s.failures*.9f+s.utility+
                        (s.confirmations>=2?.8f:0);
            if(score<worstScore){worstScore=score;worst=i;}
        }
        skills.set(worst,fresh);
        if(active==worst)abort(false);
    }
    private void abort(boolean failure){
        active=-1;step=0;pendingOutcome=0;actionStarted=false;
    }

    public String encode(){
        StringBuilder b=new StringBuilder("ts1|").append(discovered).append('|')
            .append(suggestions).append('|').append(activations).append('|')
            .append(completions).append('|').append(failedReplays).append('|')
            .append(interruptions).append('|').append(clock).append('|')
            .append(skills.size());
        for(Skill s:skills){
            b.append('|').append(s.len).append('|').append(s.confirmations)
             .append('|').append(s.failures).append('|').append(s.activations)
             .append('|').append(s.completions).append('|').append(s.utility)
             .append('|').append(s.lastUsed);
            for(int p=0;p<s.len;p++){
                b.append('|').append(s.actions[p]);
                for(int h=0;h<H;h++)b.append('|').append(s.contexts[p][h]);
            }
        }
        return b.toString();
    }

    public boolean restore(String raw){
        if(raw==null||raw.isEmpty()||raw.length()>500000)return false;
        String[] p=raw.split("\\|",-1);
        if(p.length<9||!"ts1".equals(p[0]))return false;
        try{
            int disc=Integer.parseInt(p[1]),sugg=Integer.parseInt(p[2]),
                act=Integer.parseInt(p[3]),comp=Integer.parseInt(p[4]),
                fail=Integer.parseInt(p[5]),intr=Integer.parseInt(p[6]);
            long savedClock=Long.parseLong(p[7]);
            int count=Integer.parseInt(p[8]),at=9;
            if(disc<0||sugg<0||act<0||comp<0||fail<0||intr<0||
               savedClock<0||count<0||count>MAX_SKILLS)return false;
            ArrayList<Skill> loaded=new ArrayList<>();
            for(int i=0;i<count;i++){
                if(at+7>p.length)return false;
                Skill s=new Skill();
                s.len=Integer.parseInt(p[at++]);
                s.confirmations=Integer.parseInt(p[at++]);
                s.failures=Integer.parseInt(p[at++]);
                s.activations=Integer.parseInt(p[at++]);
                s.completions=Integer.parseInt(p[at++]);
                s.utility=Float.parseFloat(p[at++]);
                s.lastUsed=Long.parseLong(p[at++]);
                if(s.len<MIN_LEN||s.len>MAX_LEN||s.confirmations<1||
                   s.failures<0||s.activations<0||s.completions<0||
                   s.completions>s.confirmations||!Float.isFinite(s.utility)||
                   s.utility<0||s.utility>1.01f||s.lastUsed<0)return false;
                for(int q=0;q<s.len;q++){
                    if(at+1+H>p.length)return false;
                    s.actions[q]=Integer.parseInt(p[at++]);
                    if(s.actions[q]<0||s.actions[q]>=SensorimotorMind.ACTIONS)return false;
                    for(int h=0;h<H;h++){
                        s.contexts[q][h]=Float.parseFloat(p[at++]);
                        if(!Float.isFinite(s.contexts[q][h])||
                           Math.abs(s.contexts[q][h])>1.01f)return false;
                    }
                }
                loaded.add(s);
            }
            if(at!=p.length)return false;
            skills.clear();skills.addAll(loaded);
            discovered=disc;suggestions=sugg;activations=act;completions=comp;
            failedReplays=fail;interruptions=intr;clock=savedClock;
            dirty=false;resetLife();return true;
        }catch(RuntimeException invalid){return false;}
    }

    private static float[] context(float x){
        float[] c=new float[H];
        for(int i=0;i<H;i++)c[i]=(float)Math.tanh(x*(.15+i*.025));
        return c;
    }
    public static void main(String[] args){
        TemporalSkillBank bank=new TemporalSkillBank();
        int[] seq={3,11,11,7};
        float[][] ctx={context(.3f),context(.8f),context(1.2f),context(1.8f),context(2.4f)};

        // One success creates only dormant candidates.
        for(int r=0;r<2;r++){
            bank.resetLife();
            for(int i=0;i<seq.length;i++)bank.chosen(ctx[i],seq[i]);
            bank.physicalOutcome(12);
        }
        if(bank.stored()==0||bank.consolidated()==0||bank.longest()<4)
            throw new AssertionError("Repeated lived sequence did not consolidate");
        Advice a=bank.advice(ctx[0],.55f);
        if(a==null||a.action!=seq[0])
            throw new AssertionError("Consolidated skill not recalled in matching context");

        int completeBefore=bank.completions;
        for(int i=0;i<seq.length;i++){
            Advice step=bank.advice(ctx[i],.55f);
            if(step==null||step.action!=seq[i])
                throw new AssertionError("Skill did not continue at step "+i);
            bank.chosen(ctx[i],seq[i]);
            bank.transition(ctx[i+1]);
        }
        bank.physicalOutcome(13);
        if(bank.completions<=completeBefore)
            throw new AssertionError("Real replay consequence did not confirm skill");

        // Divergent lived state must stop the chunk rather than force it.
        bank.resetLife();
        Advice start=bank.advice(ctx[0],.55f);
        if(start==null)throw new AssertionError("Skill missing before divergence test");
        bank.chosen(ctx[0],start.action);
        float[] far=context(-4f);
        int interrupted=bank.interruptions;
        bank.transition(far);
        if(bank.interruptions<=interrupted)
            throw new AssertionError("Context divergence did not abort skill");

        TemporalSkillBank negative=new TemporalSkillBank();
        negative.chosen(ctx[0],2);negative.chosen(ctx[1],4);
        negative.energyOutcome(-20);
        if(negative.stored()!=0)
            throw new AssertionError("Harmful sequence became a positive skill");

        String saved=bank.encode();
        TemporalSkillBank copy=new TemporalSkillBank();
        if(!copy.restore(saved)||copy.stored()!=bank.stored()||
           copy.consolidated()!=bank.consolidated()||
           copy.strongestConfirmations()!=bank.strongestConfirmations())
            throw new AssertionError("Temporal skills persistence failed");
        int learned=copy.stored();copy.resetLife();
        if(copy.stored()!=learned)
            throw new AssertionError("Death erased learned temporal skills");
        if(copy.restore(saved.replaceFirst("ts1","bad")))
            throw new AssertionError("Corrupt temporal skill bank accepted");

        System.out.println("ZERO 0.10.7 temporal skills: autonomous discovery, repeated consolidation, context-validated replay, divergence abort, persistence passed");
    }
}
