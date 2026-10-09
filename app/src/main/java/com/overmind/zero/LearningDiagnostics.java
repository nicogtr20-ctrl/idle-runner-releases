package com.overmind.zero;

import java.util.Locale;

/** Small factual evaluation log. Every fifth live transition is HELD OUT:
 * it is evaluated before the model updates and is never used for gradients.
 * "No-change" baseline predicts next observation equals previous one. This
 * is a prediction baseline, NOT an unobserved random-agent simulation.
 */
public final class LearningDiagnostics {
    public static final int HISTORY=48;
    public static final int HOLDOUT_PERIOD=5;
    public final float[] modelCurve=new float[HISTORY],baselineCurve=new float[HISTORY];
    public long allTransitions=0,heldoutTransitions=0;
    public long modelActions=0,exploreActions=0;
    public float modelError=0,baselineError=0;
    public float modelRewardSum=0,exploreRewardSum=0;
    private int cursor=0,count=0,window=0;
    private float accumModel=0,accumBaseline=0;
    public int points(){return count;}
    public float errorAt(int i){
        if(i<0||i>=count)throw new IndexOutOfBoundsException();
        return modelCurve[(cursor-count+i+HISTORY)%HISTORY];
    }
    public float baselineAt(int i){
        if(i<0||i>=count)throw new IndexOutOfBoundsException();
        return baselineCurve[(cursor-count+i+HISTORY)%HISTORY];
    }
    public boolean shouldHoldout(){return (allTransitions+1)%HOLDOUT_PERIOD==0;}
    public void record(float predictedError,float naiveError,float reward,boolean explored){
        if(!Float.isFinite(predictedError)||!Float.isFinite(naiveError)||!Float.isFinite(reward))
            throw new IllegalArgumentException("Nonfinite evaluation");
        allTransitions++;
        if(explored){exploreActions++;exploreRewardSum+=reward;}
        else{modelActions++;modelRewardSum+=reward;}
        if(allTransitions%HOLDOUT_PERIOD!=0)return;
        heldoutTransitions++;
        modelError+=(predictedError-modelError)/heldoutTransitions;
        baselineError+=(naiveError-baselineError)/heldoutTransitions;
        accumModel+=predictedError;accumBaseline+=naiveError;
        if(++window>=5){
            modelCurve[cursor]=accumModel/window;
            baselineCurve[cursor]=accumBaseline/window;
            cursor=(cursor+1)%HISTORY;
            count=Math.min(HISTORY,count+1);
            window=0;accumModel=0;accumBaseline=0;
        }
    }
    public String encode(){
        StringBuilder out=new StringBuilder("ld1");
        out.append('|').append(allTransitions).append('|').append(heldoutTransitions)
           .append('|').append(modelActions).append('|').append(exploreActions)
           .append('|').append(modelError).append('|').append(baselineError)
           .append('|').append(modelRewardSum).append('|').append(exploreRewardSum)
           .append('|').append(cursor).append('|').append(count)
           .append('|').append(window).append('|').append(accumModel)
           .append('|').append(accumBaseline);
        for(int i=0;i<HISTORY;i++)out.append('|').append(modelCurve[i]);
        for(int i=0;i<HISTORY;i++)out.append('|').append(baselineCurve[i]);
        return out.toString();
    }
    public boolean restore(String raw){
        if(raw==null)return false;
        String[] a=raw.split("\\|");
        if(a.length!=14+2*HISTORY||!"ld1".equals(a[0]))return false;
        try{
            long transitions=Long.parseLong(a[1]),holdouts=Long.parseLong(a[2]),
                 model=Long.parseLong(a[3]),explore=Long.parseLong(a[4]);
            float mm=Float.parseFloat(a[5]),bb=Float.parseFloat(a[6]),
                  mr=Float.parseFloat(a[7]),er=Float.parseFloat(a[8]),
                  am=Float.parseFloat(a[12]),ab=Float.parseFloat(a[13]);
            int cur=Integer.parseInt(a[9]),num=Integer.parseInt(a[10]),win=Integer.parseInt(a[11]);
            if(transitions<0||holdouts!=transitions/HOLDOUT_PERIOD||
               model+explore!=transitions||cur<0||cur>=HISTORY||
               num<0||num>HISTORY||win<0||win>=5)return false;
            float[] m=new float[HISTORY],b=new float[HISTORY];
            for(int i=0;i<HISTORY;i++){
                m[i]=Float.parseFloat(a[14+i]);
                b[i]=Float.parseFloat(a[14+HISTORY+i]);
            }
            float[] scalars={mm,bb,mr,er,am,ab};
            for(float v:scalars)if(!Float.isFinite(v))return false;
            for(int i=0;i<HISTORY;i++)
                if(!Float.isFinite(m[i])||!Float.isFinite(b[i])||
                   m[i]<0||b[i]<0)return false;
            allTransitions=transitions;heldoutTransitions=holdouts;
            modelActions=model;exploreActions=explore;
            modelError=mm;baselineError=bb;modelRewardSum=mr;exploreRewardSum=er;
            cursor=cur;count=num;window=win;accumModel=am;accumBaseline=ab;
            System.arraycopy(m,0,modelCurve,0,HISTORY);
            System.arraycopy(b,0,baselineCurve,0,HISTORY);
            return true;
        }catch(RuntimeException ex){return false;}
    }
    public static void main(String[] args){
        LearningDiagnostics d=new LearningDiagnostics();
        for(int i=0;i<260;i++)d.record(.8f/(1+i/8f),.35f,.03f,i%5==0);
        if(d.heldoutTransitions!=52||d.points()<10)
            throw new IllegalStateException("History window not recorded");
        if(d.modelError>=d.baselineError||d.errorAt(0)<=d.errorAt(d.points()-1))
            throw new IllegalStateException("Controlled convergence not reflected in curves");
        LearningDiagnostics restored=new LearningDiagnostics();
        if(!restored.restore(d.encode())||
           restored.heldoutTransitions!=d.heldoutTransitions||
           Math.abs(restored.modelError-d.modelError)>.00001f)
            throw new IllegalStateException("History persistence failed");
        String invalid=d.encode().replace("ld1","BAD");
        if(restored.restore(invalid)||restored.heldoutTransitions!=52)
            throw new IllegalStateException("Corrupt data erased history");
        System.out.printf(Locale.US,
            "ZERO-009.1 diagnostics pass: holdouts=%d curvePoints=%d learned=%.4f naive=%.4f persistent=true%n",
            d.heldoutTransitions,d.points(),d.modelError,d.baselineError);
    }
}
