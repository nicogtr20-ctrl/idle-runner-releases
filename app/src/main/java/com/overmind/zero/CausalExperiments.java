package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * Experimental cause/effect estimates built ONLY from ZERO's lived sensor
 * observations, own odometry, randomized actions and actual consequences.
 *
 * This is not an oracle, planner with hardcoded route, or proof of causality:
 * comparisons are observational with randomized exploration as a limited
 * intervention. Suggestions are OFF until out-of-training predictions improve
 * and same-context randomized alternatives have been observed repeatedly.
 *
 * A world change = an occupied map cell becoming free / vice versa at the
 * SAME remembered location, never merely a distance-ray changing while ZERO
 * walks past a stationary wall. "Possible passage" is only confirmed if the
 * actual core subsequently enters a previously blocked-then-observed-free cell.
 */
public final class CausalExperiments {
    public static final String KEY="causal_experiments_v111";
    public static final int CONTEXTS=3, ACTIONS=SensorimotorMind.ACTIONS;
    public static final int CASES=CONTEXTS*ACTIONS, RADIUS=6;
    private static final int SIDE=RADIUS*2+1, SAMPLES=SIDE*SIDE;
    private static final int MAX_CANDIDATES=24;
    private final int[] tried=new int[CASES],randomTried=new int[CASES];
    private final int[] contextRandom=new int[CONTEXTS],contextLearned=new int[CONTEXTS];
    private final float[] meanMove=new float[CASES],meanChange=new float[CASES],
                          meanOutcome=new float[CASES],contextMove=new float[CONTEXTS],
                          contextChange=new float[CONTEXTS],contextOutcome=new float[CONTEXTS];
    private final byte[] before=new byte[SAMPLES];
    private final long[] candidateKeys=new long[MAX_CANDIDATES];
    private final int[] candidateSource=new int[MAX_CANDIDATES];
    private int candidateCount=0,pending=-1,context=0,originX=0,originY=0;
    private float anchorX=0,anchorY=0,energyDelta=0;
    private boolean randomized=false;
    private final float[] errors=new float[2]; // heldout: learned vs context-only
    public long experiments=0;
    public int validations=0,choiceInfluences=0,worldChanges=0,newlyVisible=0,
               tentativeOpenings=0,confirmedPassages=0,motionWithoutChange=0,
               changeWithoutMotion=0,observedCorrections=0;
    public float lastBodyMove=0,lastEnvironmentalChange=0,lastNewlyVisible=0;
    public float modelError=Float.NaN,referenceError=Float.NaN;
    private static float cap(float v,float min,float max){
        return Math.max(min,Math.min(max,v));
    }
    private static int at(int ctx,int action){return ctx*ACTIONS+action;}
    private static long key(int gx,int gy){return ((long)gx<<32)^(gy&0xffffffffL);}
    private static int x(long v){return (int)(v>>32);}
    private static int y(long v){return (int)v;}
    private static boolean finite(float x){return Float.isFinite(x);}
    private static int situation(SpatialPerception spatial){
        float nearest=1f;
        for(float v:spatial.ranges())nearest=Math.min(nearest,v);
        // Near/medium/far are sensor quantizations, not object categories.
        return nearest<.33f?0:nearest<.72f?1:2;
    }
    public void resumeLife(){
        pending=-1;energyDelta=0;
    }
    public void resetLife(){
        pending=-1;energyDelta=0;candidateCount=0;
        Arrays.fill(before,(byte)0);
    }
    /** A chosen motor command starts an intervention. The snapshot is
     * indexed by absolute *odometry-grid* cells, not egocentric ray indices. */
    public void chosen(SpatialPerception scene,int action,boolean exploratory){
        if(scene==null||action<0||action>=ACTIONS)return;
        context=situation(scene);
        pending=action;randomized=exploratory;energyDelta=0;
        originX=scene.perceivedCellX();originY=scene.perceivedCellY();
        anchorX=scene.perceivedX();anchorY=scene.perceivedY();
        for(int dy=-RADIUS;dy<=RADIUS;dy++)
            for(int dx=-RADIUS;dx<=RADIUS;dx++)
                before[(dy+RADIUS)*SIDE+dx+RADIUS]=
                    scene.observedCell(originX+dx,originY+dy);
    }
    public void energyOutcome(float actual){
        if(pending>=0&&finite(actual))energyDelta=cap(energyDelta+actual,-100,100);
    }
    /** The most recent observed would-be opening is only a candidate:
     * actual passage is recorded later, when ZERO's own body occupies it. */
    private void addCandidate(long cell,int slot){
        for(int i=0;i<candidateCount;i++)
            if(candidateKeys[i]==cell){candidateSource[i]=slot;return;}
        if(candidateCount==MAX_CANDIDATES){
            System.arraycopy(candidateKeys,1,candidateKeys,0,MAX_CANDIDATES-1);
            System.arraycopy(candidateSource,1,candidateSource,0,MAX_CANDIDATES-1);
            candidateCount--;
        }
        candidateKeys[candidateCount]=cell;
        candidateSource[candidateCount++]=slot;
    }
    private void testPassage(SpatialPerception scene){
        long occupied=key(scene.perceivedCellX(),scene.perceivedCellY());
        for(int i=0;i<candidateCount;i++){
            if(candidateKeys[i]!=occupied)continue;
            // A real crossing yields credit only once and only on a later
            // physical observation. It is not a label supplied by the room.
            if(scene.observedCell(x(occupied),y(occupied))<0){
                confirmedPassages++;
                int slot=candidateSource[i];
                meanOutcome[slot]=cap(meanOutcome[slot]+.25f,-2,2);
                candidateKeys[i]=candidateKeys[--candidateCount];
                candidateSource[i]=candidateSource[candidateCount];
                break;
            }
        }
    }
    /** Generic real observations after one elapsed motor action. No reward
     * is generated from the mere fact that a blocker changed location. */
    public void consequence(SpatialPerception scene,int action,float livedReward,
                            boolean learning,boolean heldout){
        if(scene==null||pending!=action||action<0||action>=ACTIONS)return;
        int slot=at(context,action);
        testPassage(scene);
        float moved=(float)Math.hypot(scene.perceivedX()-anchorX,
                                     scene.perceivedY()-anchorY);
        int openings=0,closures=0,newFree=0;
        for(int dy=-RADIUS;dy<=RADIUS;dy++)
            for(int dx=-RADIUS;dx<=RADIUS;dx++){
                int i=(dy+RADIUS)*SIDE+dx+RADIUS;
                byte old=before[i],now=scene.observedCell(originX+dx,originY+dy);
                // A change requires CONFIRMED opposite-sign observations
                // at the same physical grid cell, not a shifting viewpoint.
                if(old>=2&&now<0){
                    openings++;
                    if(learning&&!heldout)
                        addCandidate(key(originX+dx,originY+dy),slot);
                }
                if(old<=-2&&now>=2)closures++;
                if(old==0&&now<0)newFree++;
            }
        float move=cap(moved/65f,0,1);
        float change=cap((openings+closures)/4f,0,1);
        float novelty=cap(newFree/26f,0,1);
        lastBodyMove=moved;
        lastEnvironmentalChange=openings+closures;
        lastNewlyVisible=newFree;
        experiments++;
        worldChanges+=openings+closures;
        newlyVisible+=newFree;
        tentativeOpenings+=openings;
        if(moved>=12&&openings+closures==0)motionWithoutChange++;
        if(moved<8&&openings+closures>0)changeWithoutMotion++;
        if(heldout){
            validations++;
            float model=(float)Math.hypot(move-meanMove[slot],
                                        change-meanChange[slot]);
            float base=(float)Math.hypot(move-contextMove[context],
                                       change-contextChange[context]);
            float alpha=validations==1?1f:.065f;
            modelError=validations==1?model:modelError*(1-alpha)+model*alpha;
            referenceError=validations==1?base:referenceError*(1-alpha)+base*alpha;
        }else if(learning){
            float outcome=cap(.55f*livedReward+
                .20f*novelty+cap(energyDelta/40f,-1,1)*.7f,-2,2);
            // Only observed positive changes and later REAL passages may
            // ultimately support "this action opened a route". No gate
            // movement proxy is itself scored as reward.
            float alpha=tried[slot]==0?1f:.16f;
            float c=contextLearned[context]==0?1f:.08f;
            meanMove[slot]+=alpha*(move-meanMove[slot]);
            meanChange[slot]+=alpha*(change-meanChange[slot]);
            meanOutcome[slot]+=alpha*(outcome-meanOutcome[slot]);
            contextMove[context]+=c*(move-contextMove[context]);
            contextChange[context]+=c*(change-contextChange[context]);
            contextOutcome[context]+=c*(outcome-contextOutcome[context]);
            tried[slot]++;contextLearned[context]++;
        }
        if(randomized){
            randomTried[slot]++;
            contextRandom[context]++;
        }
        energyDelta=0;pending=-1;
    }
    public float confidence(){
        if(validations<30||!finite(modelError)||!finite(referenceError)||
           referenceError<.045f||modelError>=referenceError*.95f)return 0;
        return cap((referenceError-modelError)/referenceError,0,.42f);
    }
    public int testedAlternatives(){
        int n=0;for(int count:randomTried)if(count>=3)n++;return n;
    }
    public int confirmedHypotheses(){
        int n=0;
        for(int ctx=0;ctx<CONTEXTS;ctx++){
            int alternatives=0;
            for(int action=0;action<ACTIONS;action++)
                if(randomTried[at(ctx,action)]>=3)alternatives++;
            if(alternatives<2)continue;
            for(int action=0;action<ACTIONS;action++)
                if(randomTried[at(ctx,action)]>=3&&
                    tried[at(ctx,action)]>=5)n++;
        }
        return n;
    }
    /** Genuine intervention evidence for same-state action alternatives
     * is mandatory: a mere correlation never steers the creature. */
    public float choiceBias(SpatialPerception scene,int action){
        if(scene==null||action<0||action>=ACTIONS)return 0;
        float trust=confidence();
        if(trust==0)return 0;
        int ctx=situation(scene),slot=at(ctx,action);
        if(randomTried[slot]<3||tried[slot]<5||
           contextRandom[ctx]-randomTried[slot]<5)return 0;
        float difference=meanOutcome[slot]-contextOutcome[ctx];
        return cap(difference*trust*.40f,-.15f,.15f);
    }
    public void noteDecision(float largest){
        if(largest>.007f)choiceInfluences++;
    }
    public int learnedParameters(){return CASES*3+CONTEXTS*3;}
    public String encode(){
        StringBuilder b=new StringBuilder("ce1|").append(experiments).append('|')
          .append(validations).append('|').append(choiceInfluences).append('|')
          .append(worldChanges).append('|').append(newlyVisible).append('|')
          .append(tentativeOpenings).append('|').append(confirmedPassages).append('|')
          .append(motionWithoutChange).append('|').append(changeWithoutMotion).append('|')
          .append(observedCorrections).append('|')
          .append(finite(modelError)?modelError:-1f).append('|')
          .append(finite(referenceError)?referenceError:-1f).append('|')
          .append(lastBodyMove).append('|').append(lastEnvironmentalChange).append('|')
          .append(lastNewlyVisible);
        for(int a:tried)b.append('|').append(a);
        for(int a:randomTried)b.append('|').append(a);
        for(int a:contextRandom)b.append('|').append(a);
        for(int a:contextLearned)b.append('|').append(a);
        for(float a:meanMove)b.append('|').append(a);
        for(float a:meanChange)b.append('|').append(a);
        for(float a:meanOutcome)b.append('|').append(a);
        for(float a:contextMove)b.append('|').append(a);
        for(float a:contextChange)b.append('|').append(a);
        for(float a:contextOutcome)b.append('|').append(a);
        b.append('|').append(candidateCount);
        for(int i=0;i<candidateCount;i++)
            b.append('|').append(Long.toString(candidateKeys[i],36))
                .append(':').append(candidateSource[i]);
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>30000||!raw.startsWith("ce1|"))return false;
        try{
            String[] a=raw.split("\\|",-1);
            int count=16+CASES*5+CONTEXTS*5+1;
            if(a.length<count)return false;
            long exp=Long.parseLong(a[1]);
            int[] stats=new int[9];
            for(int i=0;i<9;i++)stats[i]=Integer.parseInt(a[i+2]);
            float model=Float.parseFloat(a[11]),base=Float.parseFloat(a[12]),
                move=Float.parseFloat(a[13]),change=Float.parseFloat(a[14]),
                novel=Float.parseFloat(a[15]);
            if(exp<0||!finite(model)||!finite(base)||!finite(move)||
               !finite(change)||!finite(novel)||model< -1||base< -1||
               model>5||base>5||move<0||change<0||novel<0)return false;
            for(int st:stats)if(st<0)return false;
            int[] t=new int[CASES],rt=new int[CASES],
                cr=new int[CONTEXTS],cl=new int[CONTEXTS];
            float[] mm=new float[CASES],mc=new float[CASES],mo=new float[CASES],
                    cm=new float[CONTEXTS],cc=new float[CONTEXTS],co=new float[CONTEXTS];
            int ix=16;
            for(int i=0;i<CASES;i++)t[i]=Integer.parseInt(a[ix++]);
            for(int i=0;i<CASES;i++)rt[i]=Integer.parseInt(a[ix++]);
            for(int i=0;i<CONTEXTS;i++)cr[i]=Integer.parseInt(a[ix++]);
            for(int i=0;i<CONTEXTS;i++)cl[i]=Integer.parseInt(a[ix++]);
            for(int i=0;i<CASES;i++)mm[i]=Float.parseFloat(a[ix++]);
            for(int i=0;i<CASES;i++)mc[i]=Float.parseFloat(a[ix++]);
            for(int i=0;i<CASES;i++)mo[i]=Float.parseFloat(a[ix++]);
            for(int i=0;i<CONTEXTS;i++)cm[i]=Float.parseFloat(a[ix++]);
            for(int i=0;i<CONTEXTS;i++)cc[i]=Float.parseFloat(a[ix++]);
            for(int i=0;i<CONTEXTS;i++)co[i]=Float.parseFloat(a[ix++]);
            if(ix>=a.length)return false;
            int n=Integer.parseInt(a[ix++]);
            if(n<0||n>MAX_CANDIDATES||a.length!=ix+n)return false;
            long[] keys=new long[n];int[] slot=new int[n];
            for(int i=0;i<n;i++){
                String[] p=a[ix++].split(":",-1);
                if(p.length!=2)return false;
                keys[i]=Long.parseLong(p[0],36);
                slot[i]=Integer.parseInt(p[1]);
                if(slot[i]<0||slot[i]>=CASES)return false;
            }
            for(int i=0;i<CASES;i++)if(t[i]<0||rt[i]<0||rt[i]>exp||
               !finite(mm[i])||!finite(mc[i])||!finite(mo[i])||
               mm[i]<0||mm[i]>1||mc[i]<0||mc[i]>1||Math.abs(mo[i])>2.01f)
                return false;
            for(int i=0;i<CONTEXTS;i++)if(cr[i]<0||cl[i]<0||
                !finite(cm[i])||!finite(cc[i])||!finite(co[i])||
                cm[i]<0||cm[i]>1||cc[i]<0||cc[i]>1||Math.abs(co[i])>2.01f)
                return false;
            System.arraycopy(t,0,tried,0,CASES);
            System.arraycopy(rt,0,randomTried,0,CASES);
            System.arraycopy(cr,0,contextRandom,0,CONTEXTS);
            System.arraycopy(cl,0,contextLearned,0,CONTEXTS);
            System.arraycopy(mm,0,meanMove,0,CASES);
            System.arraycopy(mc,0,meanChange,0,CASES);
            System.arraycopy(mo,0,meanOutcome,0,CASES);
            System.arraycopy(cm,0,contextMove,0,CONTEXTS);
            System.arraycopy(cc,0,contextChange,0,CONTEXTS);
            System.arraycopy(co,0,contextOutcome,0,CONTEXTS);
            candidateCount=n;
            System.arraycopy(keys,0,candidateKeys,0,n);
            System.arraycopy(slot,0,candidateSource,0,n);
            experiments=exp;validations=stats[0];choiceInfluences=stats[1];
            worldChanges=stats[2];newlyVisible=stats[3];
            tentativeOpenings=stats[4];confirmedPassages=stats[5];
            motionWithoutChange=stats[6];changeWithoutMotion=stats[7];
            observedCorrections=stats[8];
            modelError=model<0?Float.NaN:model;referenceError=base<0?Float.NaN:base;
            lastBodyMove=move;lastEnvironmentalChange=change;
            lastNewlyVisible=novel;
            resumeLife();return true;
        }catch(RuntimeException invalid){return false;}
    }
    private static void check(boolean b,String m){if(!b)throw new AssertionError(m);}
    public static void main(String[] args){
        SpatialPerception space=new SpatialPerception();
        CausalExperiments m=new CausalExperiments();
        check(m.choiceBias(space,0)==0,"Virgin model prescribed a behavior");
        // Pure self displacement across an empty previously observed field
        // must not be reported as an independent environmental change.
        java.util.ArrayList<UnknownWorld.Entity> objects=new java.util.ArrayList<>();
        MotorBrain.Body body=new MotorBrain.Body();
        space.observeEnvironment(body,null,objects);
        m.chosen(space,0,true);
        body.cx+=25;
        space.observeEnvironment(body,null,objects);
        m.consequence(space,0,.2f,true,false);
        check(m.lastBodyMove>20&&m.lastEnvironmentalChange==0,
            "Ego-motion incorrectly inferred stationary world to have moved");
        check(m.motionWithoutChange==1,"Missed genuine self-motion");
        String emptyEvidence=m.encode();
        CausalExperiments recovered=new CausalExperiments();
        check(recovered.restore(emptyEvidence)&&
              recovered.encode().equals(emptyEvidence),
              "Causal learning did not survive saved character restart");
        check(recovered.choiceBias(space,0)==0,
              "Learning acted without randomized alternatives or validation");
        recovered.resetLife();
        check(recovered.experiments==m.experiments,
              "Death erased scientific learning instead of transient hypotheses");
        check(!new CausalExperiments().restore(emptyEvidence.replaceFirst("ce1","ce0")),
              "Corrupt persistence format accepted");
        check(CASES==48&&m.learnedParameters()==153,
              "Action-conditioned hypotheses unexpected dimensions");
        System.out.println(String.format(Locale.US,
          "ZERO-0111 pass: ego/environment separation, cold-start neutrality, "+
          "actual randomized alternatives gate, life-limited hypotheses, persistence %d bytes",
          emptyEvidence.length()));
    }
}
