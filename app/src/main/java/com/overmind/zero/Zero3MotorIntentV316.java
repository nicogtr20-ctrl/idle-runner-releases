package com.overmind.zero;

/** Typed motor intention; never a physical COM force or gem-space target. */
public final class Zero3MotorIntentV316 {
    public static final int SCHEMA=316;
    public final long episodeId,decisionId;
    public final int controllerRevision,sourceRevision;
    public final float directionX,directionY,desiredEffort,adhesionPreference;
    public final float durationSimSeconds,confidence;
    public final String actorOwner;
    private final long seal;
    private Zero3MotorIntentV316(long ep,long decision,int revision,int source,
                                float x,float y,float effort,float grip,float duration,
                                float estimate,String actor) {
        check(ep>0&&decision>=0&&revision>=0&&source>=0,"IDs and revisions");
        check(actor!=null&&!actor.isEmpty()&&actor.length()<=64,"owner identity");
        float[] values={x,y,effort,grip,duration,estimate};
        for(float v:values)check(Float.isFinite(v),"nonfinite intention");
        double norm=Math.hypot(x,y);
        check(norm<=1.00001&&((norm<1e-5)||(Math.abs(norm-1)<1e-3)),
              "direction must be unit length or zero");
        check(effort>=0&&effort<=1&&grip>=0&&grip<=1&&
              duration>0&&duration<=10&&estimate>=0&&estimate<=1,
              "intent out of declared physical ranges");
        episodeId=ep;decisionId=decision;controllerRevision=revision;
        sourceRevision=source;directionX=x;directionY=y;desiredEffort=effort;
        adhesionPreference=grip;durationSimSeconds=duration;
        confidence=estimate;actorOwner=actor;seal=signature();
    }
    static void check(boolean v,String m){
        if(!v)throw new IllegalArgumentException("ZERO3 V316 Intent "+m);
    }
    private long signature(){
        long h=0xcbf29ce484222325L;
        long[] vals={SCHEMA,episodeId,decisionId,controllerRevision,sourceRevision,
            Float.floatToIntBits(directionX),Float.floatToIntBits(directionY),
            Float.floatToIntBits(desiredEffort),Float.floatToIntBits(adhesionPreference),
            Float.floatToIntBits(durationSimSeconds),Float.floatToIntBits(confidence),
            actorOwner.hashCode()};
        for(long v:vals)h=(h^v)*1099511628211L;
        return h;
    }
    public boolean intact(){return seal==signature();}
    public static final class Authority implements AutoCloseable {
        // One in-process issuer per episode. Persistent identity is
        // restored separately from replay, not an in-memory guarantee.
        private static final java.util.HashMap<Long,Authority> LIVE=new java.util.HashMap<>();
        private final long episode;
        private final String owner;
        private long next=0;
        private boolean closed;
        public Authority(long episode,String owner){
            check(episode>0&&owner!=null&&!owner.isEmpty(),"authority owner");
            synchronized(LIVE){
                check(!LIVE.containsKey(episode),"second actor authority for episode");
                this.episode=episode;this.owner=owner;
                LIVE.put(episode,this);
            }
        }
        public void close(){
            synchronized(LIVE){
                if(!closed){closed=true;LIVE.remove(episode,this);}
            }
        }
        public Zero3MotorIntentV316 issue(String actor,long decision,int revision,
                  int source,float dirX,float dirY,float effort,float grip,
                  float duration,float confidence){
            check(!closed,"motor authority closed");
            check(owner.equals(actor),"a competing actor cannot issue a motor intent");
            check(decision==next,"duplicate, out-of-order or missing intention");
            Zero3MotorIntentV316 v=new Zero3MotorIntentV316(episode,decision,
                revision,source,dirX,dirY,effort,grip,duration,confidence,owner);
            next++;
            return v;
        }
        /**
         * V3.22: resume only on a verified, completed decision boundary.
         * The caller must first verify its joint neural/physics/replay generation.
         * Never call on an Authority that has already issued an intention.
         */
        public void restoreCompletedDecisions(long count){
            check(!closed&&next==0&&count>0&&count<=100000,
                  "authority resume requires a fresh owner and verified count");
            next=count;
        }
        public long nextDecision(){return next;}
        public String owner(){return owner;}
    }
}
