package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * ZERO21 approximate multi-view continuity, separate from simulator objects.
 * Input: sixteen OCCLUDED membrane-origin depth rays, self observed odometry,
 * ordinary self-touch fraction and chosen action. No Entity, hidden reward,
 * engine legal-grip predicate, world map or ground-truth object ID.
 *
 * Anonymous tracks are HYPOTHESES. Rays have displaced eye origins: world
 * projections are approximate, not verified geometric object centers.
 * Ambiguous assignments abstain; tracks expire and reset on real death.
 * Durable statistics are associative, NEVER promoted to causal truth/policy.
 */
public final class ObjectContinuityV21 {
    public static final String KEY="object_continuity_v21";
    private static final String FORMAT="oc21";
    public static final int RAYS=16, ACTIONS=16, MAX_TRACKS=12;
    private static final float STEP=22f, MAX_RAY=1f;
    private final ArrayList<Track> tracks=new ArrayList<>();
    private final long[] attempts=new long[ACTIONS],moves=new long[ACTIONS];
    private final long[] contactPositive=new long[ACTIONS],contactNegative=new long[ACTIONS];
    private long views,matches,ambiguous,unseen,physicalCandidates,delayedLinks,
                 unlinkedEnergy,ambiguousEnergy,heldout,invalid;
    private double heldoutError,baselineError;
    private int epoch,nextId=1,focusedId=-1,pendingId=-1,pendingAction=-1;
    private int previousLinkedId=-1,linkedAge=1000,competingMoves=0;
    private float focusedConfidence,lastTouch,pendingX,pendingY;
    private boolean pendingTouch,pendingStable;
    private int pendingPositive,pendingNegative;
    private static final class Track {
        int id,seen,updated;
        float x,y,depth,span;
        Track(int id,float x,float y,float d,float w,int t){
            this.id=id;this.x=x;this.y=y;depth=d;span=w;seen=1;updated=t;
        }
    }
    private static final class Hit {
        float x,y,depth,span;
        Hit(float x,float y,float d,float s){this.x=x;this.y=y;depth=d;span=s;}
    }
    private static boolean valid(float[] rays,float x,float y){
        if(rays==null||rays.length!=RAYS||!Float.isFinite(x)||!Float.isFinite(y)||
           Math.abs(x)>100000||Math.abs(y)>100000)return false;
        for(float r:rays)if(!Float.isFinite(r)||r<0||r>1.001f)return false;
        return true;
    }
    private static float clamp(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    private static float dist(float x,float y,float a,float b){
        return (float)Math.hypot(x-a,y-b);
    }
    /** Contiguous first-return clusters, circular wrap; do not segment walls. */
    private static ArrayList<Hit> clusters(float[] rays,float px,float py){
        ArrayList<Hit> out=new ArrayList<>();
        boolean[] occupied=new boolean[RAYS];int n=0;
        for(int i=0;i<RAYS;i++){occupied[i]=rays[i]<.999f;if(occupied[i])n++;}
        if(n==0||n==RAYS)return out;
        for(int i=0;i<RAYS;i++){
            if(!occupied[i]||occupied[(i+RAYS-1)%RAYS])continue;
            int length=0;float vx=0,vy=0,depth=0;
            for(int k=0;k<RAYS&&occupied[(i+k)%RAYS];k++){
                int j=(i+k)%RAYS;
                float angle=(float)(j*2*Math.PI/RAYS);
                float d=Math.max(STEP,Math.round(rays[j]*9f)*STEP);
                vx+=(float)Math.cos(angle)*d;
                vy+=(float)Math.sin(angle)*d;
                depth+=d;
                length++;
            }
            // A large wall or a wrap of most rays is not a discrete object.
            if(length<1||length>5)continue;
            // Approximate: physical ray eye lies off-centroid; 22U resolution.
            out.add(new Hit(px+vx/length,py+vy/length,depth/length,length));
        }
        return out;
    }
    /** New actual sightline. train=false may update ONLY transient tracks. */
    public void view(float[] rays,float odometryX,float odometryY,
                     float touchFraction,boolean train){
        if(!valid(rays,odometryX,odometryY)||
           !Float.isFinite(touchFraction)||touchFraction<0||touchFraction>1){
            invalid++;focusedId=-1;focusedConfidence=0;return;
        }
        epoch++;views++;
        lastTouch=touchFraction;
        focusedId=-1;focusedConfidence=0;
        ArrayList<Hit> hits=clusters(rays,odometryX,odometryY);
        if(hits.isEmpty()){unseen++;expire();return;}
        boolean[] taken=new boolean[tracks.size()];
        Track nearest=null;
        float nearestRange=Float.POSITIVE_INFINITY,nearestConf=0f;
        int candidates=0;
        for(Hit hit:hits){
            int bestIndex=-1;float best=Float.POSITIVE_INFINITY,second=Float.POSITIVE_INFINITY;
            for(int i=0;i<tracks.size();i++){
                Track t=tracks.get(i);
                if(taken[i]||epoch-t.updated>7)continue;
                float d=dist(t.x,t.y,hit.x,hit.y);
                float deltaSpan=Math.abs(t.span-hit.span)*8f;
                float score=d+deltaSpan+Math.abs(t.depth-hit.depth)*.12f;
                if(score<best){second=best;best=score;bestIndex=i;}
                else if(score<second)second=score;
            }
            Track assigned=null;
            if(bestIndex>=0&&best<65f && second-best>18f){
                assigned=tracks.get(bestIndex);taken[bestIndex]=true;matches++;
                assigned.x=.75f*assigned.x+.25f*hit.x;
                assigned.y=.75f*assigned.y+.25f*hit.y;
                assigned.depth=.8f*assigned.depth+.2f*hit.depth;
                assigned.span=.8f*assigned.span+.2f*hit.span;
                assigned.seen=Math.min(1000000,assigned.seen+1);assigned.updated=epoch;
            }else if(bestIndex>=0&&best<65f){
                ambiguous++;
                continue; // Never invent an identity after a near-tie.
            }else if(tracks.size()<MAX_TRACKS){
                assigned=new Track(nextId++,hit.x,hit.y,hit.depth,hit.span,epoch);
                tracks.add(assigned);
            }else{unseen++;continue;}
            float distance=dist(assigned.x,assigned.y,odometryX,odometryY);
            // Stable means independently observed across multiple sweeps.
            float confidence=assigned.seen<3?0:
                clamp((assigned.seen-2)/5f,0,1)*clamp(1f-best/105f,.2f,1f);
            if(distance<140f&&confidence>.20f){
                candidates++;
                if(distance<nearestRange){
                    nearest=assigned;nearestRange=distance;nearestConf=confidence;
                }
            }
        }
        if(candidates==1&&nearest!=null){
            focusedId=nearest.id;focusedConfidence=nearestConf;
        }else if(candidates>1)ambiguous++;
        expire();
    }
    private void expire(){
        for(int i=tracks.size()-1;i>=0;i--){
            if(epoch-tracks.get(i).updated>8)tracks.remove(i);
        }
    }
    private Track find(int id){
        for(Track t:tracks)if(t.id==id)return t;
        return null;
    }
    /** Only the final REAL action; never a proposed or imagined action. */
    public void chosen(int action){
        pendingId=-1;pendingAction=-1;pendingTouch=false;pendingStable=false;
        pendingPositive=pendingNegative=0;
        if(action<0||action>=ACTIONS){invalid++;return;}
        pendingAction=action;
        Track t=find(focusedId);
        if(t!=null&&focusedConfidence>=.40f&&lastTouch>.02f){
            pendingId=t.id;pendingX=t.x;pendingY=t.y;
            pendingTouch=true;pendingStable=true;
        }
    }
    /** One physical-contact energy event. Neither association proves cause. */
    public void energyContact(float delta){
        if(!Float.isFinite(delta)){invalid++;return;}
        if(delta==0)return;
        if(previousLinkedId>=0&&linkedAge<=8&&competingMoves==0){
            delayedLinks++;
        }else if(competingMoves>0)ambiguousEnergy++;
        else unlinkedEnergy++;
        if(pendingAction>=0&&pendingTouch){
            // Holdout status is known only at consequence(), so defer ALL
            // trained contact counts until that exact existing split.
            if(delta>0)pendingPositive++;
            else pendingNegative++;
        }
    }
    /**
     * AFTER a second real depth sweep. External motion from the old game is
     * merely independent corroboration: only same-anonymous-track movement
     * plus near-touch may become a candidate, not proven action causality.
     */
    public void consequence(float externalMotion,boolean train,boolean holdout){
        if(pendingAction<0)return;
        int action=pendingAction,id=pendingId;
        pendingAction=-1;pendingId=-1;
        if(!Float.isFinite(externalMotion)||externalMotion<0||externalMotion>100000){
            invalid++;return;
        }
        Track t=find(id);
        boolean comparable=pendingStable&&pendingTouch&&t!=null&&
                            t.updated==epoch&&t.seen>=3;
        float visibleMotion=comparable?dist(pendingX,pendingY,t.x,t.y):0f;
        // Noise due to off-centre membrane-mounted rays can exceed a cell.
        boolean candidate=comparable&&externalMotion>.015f&&visibleMotion>8f;
        if(holdout){
            if(comparable){
                heldout++;
                double prediction=(moves[action]+1d)/(attempts[action]+2d);
                double nullPrediction=.0;
                heldoutError+=(prediction-(candidate?1:0))*(prediction-(candidate?1:0));
                baselineError+=(nullPrediction-(candidate?1:0))*(nullPrediction-(candidate?1:0));
            }
        }else if(train&&comparable){
            attempts[action]++;
            if(candidate){moves[action]++;physicalCandidates++;}
            contactPositive[action]+=pendingPositive;
            contactNegative[action]+=pendingNegative;
        }
        pendingPositive=pendingNegative=0;
        linkedAge++;
        if(candidate){
            if(linkedAge<=8&&previousLinkedId!=id)competingMoves++;
            else if(linkedAge>8)competingMoves=0;
            previousLinkedId=id;linkedAge=0;
        }else if(linkedAge>8){previousLinkedId=-1;competingMoves=0;}
    }
    public float confidence(){
        double base=heldout==0?Double.NaN:baselineError/heldout;
        double err=heldout==0?Double.NaN:heldoutError/heldout;
        if(heldout<80||physicalCandidates<12||!Double.isFinite(base)||
           !Double.isFinite(err)||base<.02||err>=base*.92)return 0f;
        return clamp((float)((base-err)/base),0f,.5f);
    }
    public int visibleTracks(){return tracks.size();}
    public int currentAnonymousTrack(){return focusedId;}
    public float currentTrackConfidence(){return focusedConfidence;}
    public long trackedViews(){return matches;}
    public long ambiguousViews(){return ambiguous;}
    public long candidateEffects(){return physicalCandidates;}
    public long delayedEnergyAssociations(){return delayedLinks;}
    public long ambiguousEnergyAssociations(){return ambiguousEnergy;}
    public long unlinkedEnergyContacts(){return unlinkedEnergy;}
    public long invalidSamples(){return invalid;}
    public long holdouts(){return heldout;}
    public String summary(){
        return String.format(Locale.US,
          "ZERO21 object continuity SHADOW: views=%d matched=%d uncertain=%d tracks=%d effect hypotheses=%d delayed energy links=%d ambiguous energy=%d unlinked=%d heldout=%d confidence=%.3f",
          views,matches,ambiguous,tracks.size(),physicalCandidates,
          delayedLinks,ambiguousEnergy,unlinkedEnergy,heldout,confidence());
    }
    /** Durable cross-life evidence only, NO life-local coordinate/track ID. */
    public String encode(){
        StringBuilder b=new StringBuilder(FORMAT);
        for(long n:new long[]{views,matches,ambiguous,unseen,physicalCandidates,
            delayedLinks,unlinkedEnergy,ambiguousEnergy,heldout,invalid})
            b.append('|').append(n);
        b.append('|').append(heldoutError).append('|').append(baselineError);
        for(long[] a:new long[][]{attempts,moves,contactPositive,contactNegative})
            for(long n:a)b.append('|').append(n);
        return b.toString();
    }
    private static long nonneg(String s){
        long n=Long.parseLong(s);
        if(n<0||n>1000000000L)throw new IllegalArgumentException("counter");
        return n;
    }
    private static double finite(String s){
        double n=Double.parseDouble(s);
        if(!Double.isFinite(n)||n<0||n>1000000000d)throw new IllegalArgumentException("error");
        return n;
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>5500||!raw.startsWith(FORMAT+"|"))return false;
        try{
            String[] p=raw.split("\\|",-1);
            if(p.length!=13+4*ACTIONS)return false;
            int i=1;
            long[] h=new long[10];
            for(int j=0;j<h.length;j++)h[j]=nonneg(p[i++]);
            double e=finite(p[i++]),b=finite(p[i++]);
            long[][] values=new long[4][ACTIONS];
            for(int j=0;j<4;j++)for(int a=0;a<ACTIONS;a++)
                values[j][a]=nonneg(p[i++]);
            for(int a=0;a<ACTIONS;a++)
                if(values[1][a]>values[0][a])return false;
            // Multiple independent ray clusters can match on ONE sweep.
            if(h[1]>h[0]*MAX_TRACKS||h[8]>h[0]||h[4]>h[0]||
               e>h[8]||b>h[8])return false;
            views=h[0];matches=h[1];ambiguous=h[2];unseen=h[3];
            physicalCandidates=h[4];delayedLinks=h[5];unlinkedEnergy=h[6];
            ambiguousEnergy=h[7];heldout=h[8];invalid=h[9];
            heldoutError=e;baselineError=b;
            for(int a=0;a<ACTIONS;a++){
                attempts[a]=values[0][a];moves[a]=values[1][a];
                contactPositive[a]=values[2][a];contactNegative[a]=values[3][a];
            }
            resetLife();return true;
        }catch(RuntimeException ex){return false;}
    }
    public void resetLife(){
        tracks.clear();focusedId=pendingId=pendingAction=previousLinkedId=-1;
        nextId=1;epoch=0;focusedConfidence=0;lastTouch=0;
        pendingTouch=pendingStable=false;pendingPositive=pendingNegative=0;
        linkedAge=1000;competingMoves=0;
    }
    public void resumeLife(){resetLife();}
}
