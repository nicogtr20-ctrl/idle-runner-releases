package com.overmind.zero;

import java.util.Arrays;

/**
 * ZERO 2.0 research: partial silhouettes learned from the creature's existing
 * sixteen PHYSICAL membrane-origin obstacle rays. Never receives entity IDs,
 * world coordinates, "box", law IDs, gate state, hidden food or correct grip.
 *
 * A category is an uncertain, rotation-normalized perceptual prototype, NOT
 * proof that two sightings belong to the same physical object. Actions are
 * merely correlated with later REAL external changes, not assigned causality.
 * Strictly passive: no choiceBias(), actuator or environmental writes.
 */
public final class PerceptualAssociationsV20 {
    public static final String KEY="perceptual_associations_v20";
    public static final int RAYS=16, FEATURES=10, CATEGORIES=24;
    private static final int ACTIONS=SensorimotorMind.ACTIONS;
    private static final float MATCH=.105f, AMBIGUOUS_MARGIN=.012f;
    private final float[][] prototypes=new float[CATEGORIES][FEATURES];
    private final int[] visits=new int[CATEGORIES];
    private final int[][] tried=new int[CATEGORIES][ACTIONS],moved=new int[CATEGORIES][ACTIONS];
    private final int[] genericTried=new int[ACTIONS],genericMoved=new int[ACTIONS];
    private int used=0,current=-1,pending=-1,pendingAction=-1;
    private long views=0,unknownViews=0,ambiguousViews=0,heldout=0;
    private long observedMotionEvents=0,observedEnergyEvents=0;
    private double errorSum=0,baselineSum=0;

    private static boolean valid(float[] rays){
        if(rays==null||rays.length!=RAYS)return false;
        for(float x:rays)if(!Float.isFinite(x)||x<0f||x>1.001f)return false;
        return true;
    }
    private static int increment(int x){return x==Integer.MAX_VALUE?x:x+1;}
    private static long increment(long x){return x==Long.MAX_VALUE?x:x+1;}
    public int categories(){return used;}
    public int currentCategory(){return current;}
    public long observations(){return views;}
    public long unknown(){return unknownViews;}
    public long ambiguous(){return ambiguousViews;}
    public long validations(){return heldout;}
    public long realExternalEvents(){return observedMotionEvents;}
    public long realEnergyEvents(){return observedEnergyEvents;}
    public int evidence(int category,int action){
        return category<0||category>=used||action<0||action>=ACTIONS?
            0:tried[category][action];
    }
    public int observedMovements(int category,int action){
        return category<0||category>=used||action<0||action>=ACTIONS?
            0:moved[category][action];
    }
    public float heldoutError(){return heldout==0?Float.NaN:(float)(errorSum/heldout);}
    public float referenceError(){return heldout==0?Float.NaN:(float)(baselineSum/heldout);}
    /** Prediction-only metric; NEVER a license to steer motor decisions. */
    public float predictiveConfidence(){
        float e=heldoutError(),b=referenceError();
        if(heldout<80||observedMotionEvents<12||!Float.isFinite(e)||
           !Float.isFinite(b)||b<.02f||e>=b*.92f)return 0f;
        return Math.min(.5f,(b-e)/b);
    }
    private static float distance(float[] a,float[] b){
        float sum=0;
        for(int i=0;i<FEATURES;i++)sum+=Math.abs(a[i]-b[i]);
        return sum/FEATURES;
    }
    /**
     * Circularly anchor an 8-ray local contour at the closest sensed surface.
     * This tolerates a shift in viewing direction but not yet full 2D/3D
     * rotation or a change in distance/occlusion. If nearby rays suggest two
     * incompatible surfaces, ABSTAIN instead of fusing them into an object.
     */
    private float[] descriptor(float[] rays){
        if(!valid(rays))return null;
        float closest=1f;int center=-1,hitCount=0;
        for(int i=0;i<RAYS;i++){
            if(rays[i]<.999f){
                hitCount++;
                if(rays[i]<closest){closest=rays[i];center=i;}
            }
        }
        if(center<0)return null;
        for(int i=0;i<RAYS;i++){
            int d=Math.abs(i-center);d=Math.min(d,RAYS-d);
            if(d>4&&rays[i]<.999f&&rays[i]<=closest+.09f){
                ambiguousViews=increment(ambiguousViews);
                return null;
            }
        }
        float[] x=new float[FEATURES];
        for(int i=0;i<8;i++){
            float v=rays[(center+i+RAYS-3)%RAYS];
            x[i]=v>=.999f?1f:Math.min(1f,Math.max(0f,.40f+v-closest));
        }
        x[8]=closest;
        x[9]=Math.min(1f,hitCount/8f);
        return x;
    }
    /**
     * Called ONLY with rays produced by SpatialPerception (no object lookup).
     * The held-out split may recognize frozen prototypes but cannot create or
     * update them. "Category" is an arbitrary internal numeric hypothesis.
     */
    public void view(float[] rays,boolean train){
        views=increment(views);current=-1;
        float[] candidate=descriptor(rays);
        if(candidate==null){unknownViews=increment(unknownViews);return;}
        int nearest=-1;float best=Float.POSITIVE_INFINITY,second=Float.POSITIVE_INFINITY;
        for(int c=0;c<used;c++){
            float d=distance(prototypes[c],candidate);
            if(d<best){second=best;best=d;nearest=c;}
            else if(d<second)second=d;
        }
        if(nearest>=0&&best<=MATCH&&second-best<=AMBIGUOUS_MARGIN){
            ambiguousViews=increment(ambiguousViews);return;
        }
        if(nearest>=0&&best<=MATCH){
            current=nearest;
            if(train){
                int n=visits[nearest]=increment(visits[nearest]);
                float rate=1f/Math.min(24,n);
                for(int i=0;i<FEATURES;i++)
                    prototypes[nearest][i]+=(candidate[i]-prototypes[nearest][i])*rate;
            }
        }else if(train&&used<CATEGORIES){
            current=used++;
            visits[current]=1;
            System.arraycopy(candidate,0,prototypes[current],0,FEATURES);
        }else unknownViews=increment(unknownViews);
    }
    public void chosen(int actualAction){
        if(actualAction<0||actualAction>=ACTIONS){pending=-1;pendingAction=-1;return;}
        pending=current;pendingAction=actualAction;
    }
    /** Only the completed real decision interval labels the PRE-action view.
     * Movement may have come from another solid: this is scene correlation,
     * NOT object-specific credit or a proven grip technique.
     */
    public void consequence(float measuredExternalMotion,boolean train,boolean validation){
        int c=pending,a=pendingAction;pending=-1;pendingAction=-1;
        if(c<0||c>=used||a<0||a>=ACTIONS||
           !Float.isFinite(measuredExternalMotion)||measuredExternalMotion<0)return;
        boolean effect=measuredExternalMotion>.015f;
        if(validation&&!train){
            double p=(moved[c][a]+1d)/(tried[c][a]+4d);
            double base=(genericMoved[a]+1d)/(genericTried[a]+4d);
            double label=effect?1d:0d;
            errorSum+=(p-label)*(p-label);
            baselineSum+=(base-label)*(base-label);
            heldout=increment(heldout);
        }else if(train&&!validation){
            tried[c][a]=increment(tried[c][a]);
            genericTried[a]=increment(genericTried[a]);
            if(effect){
                moved[c][a]=increment(moved[c][a]);
                genericMoved[a]=increment(genericMoved[a]);
                observedMotionEvents=increment(observedMotionEvents);
            }
        }
    }
    /** Genuine energy contacts, counted separately; never identify a cause. */
    public void actualEnergy(float amount){
        if(Float.isFinite(amount)&&amount!=0)
            observedEnergyEvents=increment(observedEnergyEvents);
    }
    public void resetLife(){current=-1;pending=-1;pendingAction=-1;}
    public void resumeLife(){resetLife();}

    /** Exact bounded v1 state; all corrupt payloads are rejected atomically. */
    public String encode(){
        StringBuilder out=new StringBuilder("pa20|").append(views).append('|')
            .append(unknownViews).append('|').append(ambiguousViews).append('|')
            .append(heldout).append('|').append(observedMotionEvents).append('|')
            .append(observedEnergyEvents).append('|').append(errorSum).append('|')
            .append(baselineSum).append('|').append(used);
        for(int a=0;a<ACTIONS;a++)out.append('|').append(genericTried[a]);
        for(int a=0;a<ACTIONS;a++)out.append('|').append(genericMoved[a]);
        for(int c=0;c<CATEGORIES;c++){
            out.append('|').append(visits[c]);
            for(float x:prototypes[c])out.append('|').append(x);
            for(int a=0;a<ACTIONS;a++)out.append('|').append(tried[c][a]);
            for(int a=0;a<ACTIONS;a++)out.append('|').append(moved[c][a]);
        }
        return out.toString();
    }
    private static long nonnegativeLong(String raw){
        long n=Long.parseLong(raw);
        if(n<0)return -1;
        return n;
    }
    private static int nonnegativeInt(String raw){
        int n=Integer.parseInt(raw);
        if(n<0)throw new IllegalArgumentException("negative");
        return n;
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>70000||!raw.startsWith("pa20|"))return false;
        String[] s=raw.split("\\|",-1);
        if(s.length!=10+2*ACTIONS+CATEGORIES*(1+FEATURES+2*ACTIONS))return false;
        try{
            int at=1;
            long v=nonnegativeLong(s[at++]),u=nonnegativeLong(s[at++]),
                amb=nonnegativeLong(s[at++]),h=nonnegativeLong(s[at++]),
                motion=nonnegativeLong(s[at++]),energy=nonnegativeLong(s[at++]);
            double err=Double.parseDouble(s[at++]),baseline=Double.parseDouble(s[at++]);
            int count=nonnegativeInt(s[at++]);
            if(v<0||u<0||amb<0||h<0||motion<0||energy<0||
               count>CATEGORIES||!Double.isFinite(err)||!Double.isFinite(baseline)||
               err<0||baseline<0||motion>v||u>v||amb>v||h>v)return false;
            int[] gt=new int[ACTIONS],gm=new int[ACTIONS];
            for(int a=0;a<ACTIONS;a++)gt[a]=nonnegativeInt(s[at++]);
            for(int a=0;a<ACTIONS;a++){
                gm[a]=nonnegativeInt(s[at++]);
                if(gm[a]>gt[a])return false;
            }
            int[] n=new int[CATEGORIES];
            float[][] p=new float[CATEGORIES][FEATURES];
            int[][] t=new int[CATEGORIES][ACTIONS],m=new int[CATEGORIES][ACTIONS];
            for(int c=0;c<CATEGORIES;c++){
                n[c]=nonnegativeInt(s[at++]);
                if((c<count)!=(n[c]>0))return false;
                for(int i=0;i<FEATURES;i++){
                    p[c][i]=Float.parseFloat(s[at++]);
                    if(!Float.isFinite(p[c][i])||p[c][i]<0||p[c][i]>1.001f)return false;
                }
                for(int a=0;a<ACTIONS;a++)t[c][a]=nonnegativeInt(s[at++]);
                for(int a=0;a<ACTIONS;a++){
                    m[c][a]=nonnegativeInt(s[at++]);
                    if(m[c][a]>t[c][a]||(c>=count&&(t[c][a]!=0||m[c][a]!=0)))
                        return false;
                }
            }
            views=v;unknownViews=u;ambiguousViews=amb;heldout=h;
            observedMotionEvents=motion;observedEnergyEvents=energy;
            errorSum=err;baselineSum=baseline;used=count;
            System.arraycopy(gt,0,genericTried,0,ACTIONS);
            System.arraycopy(gm,0,genericMoved,0,ACTIONS);
            for(int c=0;c<CATEGORIES;c++){
                visits[c]=n[c];
                System.arraycopy(p[c],0,prototypes[c],0,FEATURES);
                System.arraycopy(t[c],0,tried[c],0,ACTIONS);
                System.arraycopy(m[c],0,moved[c],0,ACTIONS);
            }
            resetLife();
            return true;
        }catch(RuntimeException ignored){return false;}
    }
}
