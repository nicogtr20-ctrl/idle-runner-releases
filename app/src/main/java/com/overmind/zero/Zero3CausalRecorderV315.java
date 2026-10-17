package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * E2 ZERO3 V3.15 — exactly-once physical experience transaction.
 *
 * No simulated reward and no hidden world labels are accepted. A pre-action
 * immutable snapshot must be followed by one actually executed 64D efference
 * and the true post-collision snapshot at t+0.025s. The sample split is
 * fixed PER EPISODE before the first scene starts.
 *
 * Not a recurrent brain, not persistent checkpoints, not gameplay.
 */
public final class Zero3CausalRecorderV315 {
    public enum Split {FIT,CAL,BLIND}
    public static final int SCHEMA=315,ACTUATORS=64;
    public static final double DT=.025;
    public final long episodeId;
    public final Split split;
    private final ArrayList<Experience> records=new ArrayList<>();
    private Zero3SensoryFrameV315 pending;
    private Zero3SensoryFrameV315 lastPost;
    private long expectedFrame;

    public static final class Action {
        public final long episodeId,decisionId,physicsFrameId;
        public final int cortexRevision,perturbationId;
        public final long randomSeed;
        private final float[] requested,executed;
        public final long executedHash;

        public Action(long episode,long decision,long frame,int revision,
                      int perturbation,long seed,float[] requested64,
                      float[] executed64){
            require(episode>0&&decision>=0&&frame>=0&&revision>=0,
                    "action episode/decision/revision invalid");
            require(requested64!=null&&executed64!=null&&
                    requested64.length==ACTUATORS&&executed64.length==ACTUATORS,
                    "actual/requested 64D action required");
            for(float v:requested64)require(Float.isFinite(v)&&Math.abs(v)<=1.0001f,
                                           "illegal requested action");
            for(float v:executed64)require(Float.isFinite(v)&&Math.abs(v)<=1.0001f,
                                          "illegal executed action");
            episodeId=episode;decisionId=decision;physicsFrameId=frame;
            cortexRevision=revision;perturbationId=perturbation;randomSeed=seed;
            requested=requested64.clone();executed=executed64.clone();
            executedHash=hash(executed);
        }
        public float[] requested64(){return requested.clone();}
        public float[] executed64(){return executed.clone();}
        public boolean intact(){return executedHash==hash(executed);}
        static long hash(float[] a){
            long h=0xcbf29ce484222325L;
            for(float v:a)h=(h^Float.floatToIntBits(v))*1099511628211L;
            return h;
        }
    }
    public static final class PhysicsOutcome {
        public final long episodeId,decisionId,frameStart,frameEnd;
        public final double elapsedSimSeconds;
        public final float dx,dy,actualDistance;
        public final int touchedBefore,touchedAfter,pulsedAfter,heldAfter;
        public final boolean contactOnset;
        public final long beforeHash,afterHash;
        PhysicsOutcome(long ep,long decision,Zero3SensoryFrameV315 pre,
                       Zero3SensoryFrameV315 post){
            episodeId=ep;decisionId=decision;
            frameStart=pre.frameId;frameEnd=post.frameId;
            elapsedSimSeconds=post.simulationTime-pre.simulationTime;
            dx=post.bodyCenterX-pre.bodyCenterX;
            dy=post.bodyCenterY-pre.bodyCenterY;
            actualDistance=(float)Math.hypot(dx,dy);
            touchedBefore=pre.touchedSites()+pre.pulsedSites();
            touchedAfter=post.touchedSites();
            pulsedAfter=post.pulsedSites();
            heldAfter=post.heldSites();
            contactOnset=touchedBefore==0&&(touchedAfter>0||pulsedAfter>0);
            beforeHash=pre.hash;afterHash=post.hash;
        }
    }
    public static final class Experience {
        public final int schemaVersion=SCHEMA;
        public final long episodeId,decisionId;
        public final Split split;
        public final Zero3SensoryFrameV315 pre,post;
        public final Action action;
        public final PhysicsOutcome outcome;
        Experience(Split split,long episode,long decision,
                   Zero3SensoryFrameV315 before,Action action,
                   Zero3SensoryFrameV315 after){
            this.split=split;episodeId=episode;decisionId=decision;
            pre=before;post=after;this.action=action;
            outcome=new PhysicsOutcome(episode,decision,before,after);
        }
        public boolean intact(){
            return pre.intact()&&post.intact()&&action.intact()&&
                outcome.beforeHash==pre.hash&&outcome.afterHash==post.hash;
        }
    }

    public Zero3CausalRecorderV315(long episode,Split assignedBeforeExperience){
        require(episode>0&&assignedBeforeExperience!=null,
                "episode ID/split must be assigned in advance");
        episodeId=episode;split=assignedBeforeExperience;
    }
    static void require(boolean ok,String why){
        if(!ok)throw new IllegalArgumentException("ZERO3 CAUSAL V315 "+why);
    }
    static boolean samePhysicalState(Zero3SensoryFrameV315 a,
                                     Zero3SensoryFrameV315 b){
        // A POST and the NEXT PRE must sample the identical settled world.
        // No extra unrecorded tick, output, sensor refresh or data edit.
        return a.episodeId==b.episodeId&&a.frameId==b.frameId&&
            Double.compare(a.simulationTime,b.simulationTime)==0 &&
            a.solverTicks==b.solverTicks &&
            Float.compare(a.bodyCenterX,b.bodyCenterX)==0 &&
            Float.compare(a.bodyCenterY,b.bodyCenterY)==0 &&
            Float.compare(a.bodyVelX,b.bodyVelX)==0 &&
            Float.compare(a.bodyVelY,b.bodyVelY)==0 &&
            Arrays.equals(a.local96x12(),b.local96x12()) &&
            Arrays.equals(a.membraneRays(),b.membraneRays()) &&
            Arrays.equals(a.globalProprioception(),b.globalProprioception()) &&
            Arrays.equals(a.inner74x7(),b.inner74x7()) &&
            Arrays.equals(a.lastExecuted64(),b.lastExecuted64());
    }
    public void begin(Zero3SensoryFrameV315 pre){
        require(pending==null,"duplicate begin before commit");
        require(pre!=null&&pre.preAction&&pre.episodeId==episodeId,
                "pre frame origin or phase wrong");
        require(pre.frameId==expectedFrame,"missing or duplicated frame");
        require(pre.intact(),"pre snapshot checksum invalid");
        if(lastPost!=null)
            require(samePhysicalState(lastPost,pre),
                    "post of previous experience != current pre");
        else
            require(!pre.lastExecutedAvailable,
                    "first frame cannot inherit undocumented prior action");
        pending=pre; // immutable reference; no aliasing into raw physics.
    }
    public Experience commit(Action action,Zero3SensoryFrameV315 post){
        require(pending!=null,"commit without pending pre or duplicate commit");
        require(action!=null&&post!=null&&post.intact()&&pending.intact(),
                "missing or modified causal inputs");
        require(post.episodeId==episodeId&&!post.preAction&&
                post.frameId==pending.frameId+1&&
                action.episodeId==episodeId&&
                action.physicsFrameId==pending.frameId&&
                action.decisionId==expectedFrame,
                "episode/frame/decision mismatch");
        require(Math.abs((post.simulationTime-pending.simulationTime)-DT)<1e-7,
                "not one real 0.025-second physics step");
        require(post.solverTicks==pending.solverTicks+1,
                "post snapshot was not produced by exactly one real gel step");
        require(post.lastExecutedAvailable&&action.intact() &&
                Arrays.equals(action.executed64(),post.lastExecuted64()),
                "post snapshot efference differs from actually applied action");
        require(records.size()==expectedFrame,
                "duplicate or out-of-order persistent event");
        Experience event=new Experience(split,episodeId,expectedFrame,
                                        pending,action,post);
        require(event.intact(),"incomplete immutable experience");
        // Atomic logical append: only once *after* all validations pass.
        records.add(event);
        lastPost=post;
        pending=null;
        expectedFrame++;
        return event;
    }
    public int size(){return records.size();}
    public boolean hasUncommittedBegin(){return pending!=null;}
    public List<Experience> experiences(){
        return Collections.unmodifiableList(new ArrayList<>(records));
    }
}
