package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ZERO3 V3.19: experimental trainable CONTEXTUAL object-adhesion head.
 * This owns only channel 2 of the 16-sector neural motor outputs.
 * Other muscles, object physics and body are never changed here.
 *
 * Actual gel "work" includes an independently accountable adhesion term:
 * for each skin patch (adhesive * .1f) * dt * .0002f.
 * The head computes that term from ACTUALLY EXECUTED adhesion outputs,
 * cross-checks its sum against the REAL gel.work delta, and applies
 * dimensionless cost-based local motor credit after true world collision.
 */
public final class Zero3ContextAdhesionV319 {
    static final int SECTORS=16,FEATURES=8,SITES_PER=6;
    static final float DT=.025f;
    static final float ADHESION_WORK_AT_FULL_PER_SECTOR=
        SITES_PER*.1f*DT*.0002f;
    // Fixed BEFORE blind; no feature hard gate, no object identity.
    static final float GRIP_CREDIT=.35f,COST_CREDIT=.065f,DECAY=.00012f;
    static final float STEP_RATE=.38f;
    private final float[] weights=new float[SECTORS*FEATURES];
    private final float[][] sumF=new float[SECTORS][FEATURES];
    private final float[][] heldF=new float[SECTORS][FEATURES];
    private final float[] lastCmd=new float[SECTORS],
        perturb=new float[SECTORS],heldCount=new float[SECTORS],
        sumCommand=new float[SECTORS],sumTrueCost=new float[SECTORS];
    private final Rng rng;
    private final boolean training;
    private boolean active,hasStep,heldoutDecision;
    private int decisions,realCreditEvents;
    private int steps;
    private double weightChangeSq,allMeasuredAdhesionWork,
                   realGelWorkMeasured;
    public Zero3ContextAdhesionV319(long seed,boolean training){
        this.rng=new Rng(seed);this.training=training;
    }
    public Zero3ContextAdhesionV319 frozenCopy(){
        Zero3ContextAdhesionV319 copy=new Zero3ContextAdhesionV319(0x319L,false);
        System.arraycopy(weights,0,copy.weights,0,weights.length);
        return copy;
    }

    /**
     * Experimental head-only checkpoint. Never a full BrainSnapshotV3.
     * Safe only after end(), before the next begin() and in the same
     * training/evaluation mode. Does NOT snapshot the body or actor state.
     */
    public static final class State {
        public static final int VERSION=320;
        public final long rngState;
        public final boolean training;
        public final int decisions,realCreditEvents;
        public final double weightChangeSq,accountedAdhesionWork,observedGelWork;
        private final float[] weights;
        State(float[] weights,long rng,boolean training,int decisions,
              int credits,double changes,double adhesion,double total){
            if(weights==null||weights.length!=SECTORS*FEATURES||
               rng==0||decisions<0||credits<0||credits>decisions*SECTORS||
               !Double.isFinite(changes)||changes<0||
               !Double.isFinite(adhesion)||adhesion<0||
               !Double.isFinite(total)||total<0)
                throw new IllegalArgumentException("invalid V319 snapshot state");
            for(float value:weights)
                if(!Float.isFinite(value)||Math.abs(value)>4.00001f)
                    throw new IllegalArgumentException("nonfinite/out of bounds neural weight");
            this.weights=weights.clone();rngState=rng;this.training=training;
            this.decisions=decisions;realCreditEvents=credits;
            weightChangeSq=changes;accountedAdhesionWork=adhesion;
            observedGelWork=total;
        }
        public float[] weights(){return weights.clone();}
    }
    public State captureAtDecisionBoundary(){
        if(active||hasStep)throw new IllegalStateException(
            "cannot checkpoint V319 in-flight physical decision");
        return new State(weights,rng.state,training,decisions,realCreditEvents,
            weightChangeSq,allMeasuredAdhesionWork,realGelWorkMeasured);
    }
    public void restoreAtDecisionBoundary(State s){
        if(s==null||active||hasStep||decisions!=0||
           realCreditEvents!=0||weightChangeSq!=0||
           allMeasuredAdhesionWork!=0||realGelWorkMeasured!=0)
            throw new IllegalStateException(
                "restore requires a fresh, idle motor head; no double replay");
        if(s.training!=training)
            throw new IllegalArgumentException(
                "checkpoint training flag mismatches requested motor head");
        System.arraycopy(s.weights(),0,weights,0,weights.length);
        rng.state=s.rngState;
        decisions=s.decisions;realCreditEvents=s.realCreditEvents;
        weightChangeSq=s.weightChangeSq;
        allMeasuredAdhesionWork=s.accountedAdhesionWork;
        realGelWorkMeasured=s.observedGelWork;
    }

    public int updates(){return decisions;}
    public int positiveGripEvents(){return realCreditEvents;}
    public double weightChangesL2(){return Math.sqrt(weightChangeSq);}
    public double actualAccountedAdhesionWork(){return allMeasuredAdhesionWork;}
    public double physicalTotalWorkMeasured(){return realGelWorkMeasured;}
    static float clip(float a,float min,float max){
        return Math.min(max,Math.max(min,a));
    }
    static float sigmoid(float x){
        return (float)(1.0/(1.0+Math.exp(-clip(x,-10,10))));
    }
    static final class Rng {
        long state;
        Rng(long seed){state=seed==0?0x32AFFEFD851BD37L:seed;}
        float u(){
            state^=state<<13;state^=state>>>7;state^=state<<17;
            return (state>>>40)/16777216f;
        }
    }
    public void begin(){begin(false);}
    public void begin(boolean heldout){
        heldoutDecision=heldout;
        if(active)throw new IllegalStateException("two active adhesions");
        active=true;steps=0;hasStep=false;
        for(int s=0;s<SECTORS;s++){
            Arrays.fill(sumF[s],0f);Arrays.fill(heldF[s],0f);
            heldCount[s]=sumCommand[s]=sumTrueCost[s]=lastCmd[s]=0f;
        }
        // Exploration of the neuron's LOGIT; not a hand-coded on/off grip.
        float coarse=training?(rng.u()*2f-1f)*1.35f:0f;
        for(int s=0;s<SECTORS;s++)
            perturb[s]=training?coarse+(rng.u()*2f-1f)*1.45f:0f;
    }
    public void actuate(float[] cmd,Zero3MotorIntentV316 intent,
                        MotorBrain.Body body,Morphology.Surface skin,
                        ArrayList<UnknownWorld.Entity> objects){
        if(!active||cmd==null||cmd.length!=64||intent==null||
           body==null||skin==null||objects==null)
            throw new IllegalStateException("missing pre-physics active intent");
        for(int s=0;s<SECTORS;s++){
            float solid=0,pulse=0,held=0,radialVelocity=0;
            int first=s*SITES_PER;
            for(int j=0;j<SITES_PER;j++){
                int i=first+j;
                solid+=clip(skin.touchSense[i],0f,1f);
                pulse+=clip(skin.touchPulse[i],0f,1f);
                if(skin.gripHold[i]>=Morphology.GRIP_HOLD)held++;
                float rx=skin.x[i]-body.cx,ry=skin.y[i]-body.cy;
                float norm=(float)Math.hypot(rx,ry);
                if(norm>.001f)radialVelocity+=
                    ((skin.vx[i]-body.cvx)*rx+
                     (skin.vy[i]-body.cvy)*ry)/(norm*130f);
            }
            int site=first+3;
            float ux=skin.x[site]-body.cx,uy=skin.y[site]-body.cy;
            float r=(float)Math.hypot(ux,uy);
            if(r>.0001f){ux/=r;uy/=r;}else{ux=1f;uy=0f;}
            float d=SensorimotorMind.Senses.firstSolidDistance(null,objects,
                skin.x[site],skin.y[site],ux,uy,132f);
            float vision=Float.isFinite(d)?clip(1-d/132f,0f,1f):0f;
            float touch=solid/SITES_PER;
            float[] f={1f,intent.adhesionPreference,touch,
                       pulse/SITES_PER,held/SITES_PER,vision,
                       clip(-radialVelocity/SITES_PER,-1,1),
                       touch*vision};
            float z=-1.6f+perturb[s];
            for(int i=0;i<FEATURES;i++){
                if(!Float.isFinite(f[i]))
                    throw new IllegalStateException("nonfinite body observation");
                sumF[s][i]+=f[i];
                z+=weights[s*FEATURES+i]*f[i];
            }
            float drive=sigmoid(z);
            cmd[4*s+2]=drive;
            lastCmd[s]=drive;
            sumCommand[s]+=drive;
            sumTrueCost[s]+=drive*ADHESION_WORK_AT_FULL_PER_SECTOR;
        }
        steps++;
        hasStep=true;
    }
    public void observe(Morphology.Surface skin,UnknownWorld.Entity box,
                        float actualGelWorkDelta){
        if(!active||!hasStep||!Float.isFinite(actualGelWorkDelta)||
           actualGelWorkDelta<0)
            throw new IllegalStateException("missing physical outcome or cost");
        // Read outcome strictly AFTER original gel and box/disk solver.
        float totalAdhesion=0;
        for(int s=0;s<SECTORS;s++){
            totalAdhesion+=lastCmd[s]*ADHESION_WORK_AT_FULL_PER_SECTOR;
            int first=s*SITES_PER;
            boolean trueHeld=false;
            for(int j=0;j<SITES_PER;j++){
                int i=first+j;
                if(skin.gripHold[i]>=Morphology.GRIP_HOLD){
                    if(box==null||!box.isBilateralBox()||
                       box.boxGripHold[i]<Morphology.GRIP_HOLD||
                       skin.gripCommand[i]<=Morphology.GRIP_THRESHOLD)
                        throw new IllegalStateException("non-world grip signal");
                    trueHeld=true;
                }
            }
            if(trueHeld){
                heldCount[s]++;
                // Local eligibility of features actually sampled BEFORE
                // action; never the current POST sensory values.
                for(int k=0;k<FEATURES;k++)
                    heldF[s][k]+=sumF[s][k]/steps;
            }
        }
        // The solver uses exactly this adhesion work contribution, plus
        // radial and tangential work. Check with small float tolerance.
        if(totalAdhesion>actualGelWorkDelta+0.000002f)
            throw new IllegalStateException("adhesion work not present in real gel work");
        allMeasuredAdhesionWork+=totalAdhesion;
        realGelWorkMeasured+=actualGelWorkDelta;
        hasStep=false;
    }
    public void end(){
        if(!active||steps==0||hasStep)
            throw new IllegalStateException("decision incomplete");
        active=false;
        if(!training)return;
        if(heldoutDecision){decisions++;return;}
        for(int s=0;s<SECTORS;s++){
            float meanDrive=sumCommand[s]/steps;
            // Derive negative gradient from the EXACT physical glue term
            // actually added by the solver, normalized to the same term
            // with full drive (no invented energy reading).
            float normalizedPhysicalCost=sumTrueCost[s]/
                (steps*ADHESION_WORK_AT_FULL_PER_SECTOR);
            if(!Float.isFinite(normalizedPhysicalCost)||
               Math.abs(normalizedPhysicalCost-meanDrive)>.0002f)
                throw new IllegalStateException("motor cost accounting mismatch");
            float gripRatio=heldCount[s]/steps;
            if(gripRatio>0)realCreditEvents++;
            // Local reinforcement ONLY for legal held contacts: logistic
            // policy can discover association with the solid/tactile
            // FEATURES, not a global command-on shift.
            for(int k=0;k<FEATURES;k++){
                float fmean=sumF[s][k]/steps;
                float gripFeature=(gripRatio>0 && k>=2)?
                    heldF[s][k]/Math.max(1f,heldCount[s]):0f;
                // Grip difference is credited via observed positive result
                // and sampled neural-logit exploration, not future vision.
                float credited=GRIP_CREDIT*gripRatio*gripFeature*
                    Math.max(.15f,Math.abs(perturb[s]));
                float cost=COST_CREDIT*normalizedPhysicalCost*
                    meanDrive*(1-meanDrive)*fmean;
                float gradient=credited-cost-DECAY*weights[s*FEATURES+k];
                float change=clip(STEP_RATE*gradient,-.06f,.06f);
                int ix=s*FEATURES+k;
                float old=weights[ix];
                weights[ix]=clip(weights[ix]+change,-4f,4f);
                float delta=weights[ix]-old;
                weightChangeSq+=delta*delta;
            }
        }
        decisions++;
    }
}
