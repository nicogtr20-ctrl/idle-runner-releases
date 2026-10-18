package com.overmind.zero;

import java.util.Arrays;
import java.io.*;
import java.util.zip.CRC32;

/**
 * ZERO3 experimental trainable 64-GRU actor/dual-critic/world-prediction core.
 * Runs locally without Android, external knowledge, target labels or servers.
 * Integrated in the isolated V325/V326 laboratory, not the stable 2.2 profile.
 * Scientific behavioral gates remain open.
 *
 * One-step truncated recurrent backprop is deliberately explicit: gradients
 * update all GRU gate matrices from real next-observation prediction error;
 * no fixed reservoir. V352 adds bounded actual-transition value replay and target critics.
 * Long BPTT and independent cognitive/behavioral validation remain open.
 */
public final class Zero3RecurrentBrain {
    public static final int SENSOR=SensorimotorMind.S, HIDDEN=64, MOTOR=5, ACTION=4;
    private static final float MODEL_LR=.008f, CRITIC_LR=.009f, ACTOR_LR=.003f;
    private final int inputs;
    private final boolean variableClock;
    private Zero3CognitionV353 cognition;
    private Zero3ExecutiveV354 executive;
    private Zero3PlannerV354 plannerV354;
    private long observationCount;
    private boolean observeV354,optionReplayV354;
    private boolean physicalTargetsReadyV353,physicalHeldoutV353;
    private final float[] wz,wr,wn,world,before;

    private final float[] actor=new float[MOTOR*(HIDDEN+1)];
    private final float[][] critic={new float[HIDDEN+1],new float[HIDDEN+1]};
    private final float[][] targetCritic;
    private final float[] valueReplay;

    private final float[] state=new float[HIDDEN],prior=new float[HIDDEN];
    private final float[] gatedZ=new float[HIDDEN],gatedR=new float[HIDDEN],
        candidate=new float[HIDDEN],decisionState=new float[HIDDEN];
    private final float[] lastNoise=new float[MOTOR],
        action=new float[ACTION],worldInput=new float[HIDDEN+ACTION+1];
    private long rng=0x34EA7CA51DA1L,trainCount,validationCount,recurrentUpdates;
    private boolean pending;
    private float heldoutModelError=Float.NaN,heldoutBaselineError=Float.NaN;
    private final float[] proposal=new float[MOTOR];
    private float storedValue;
    public Zero3RecurrentBrain(){this(SENSOR);}
    public Zero3RecurrentBrain(int width){this(width,false);}
    public Zero3RecurrentBrain(int width,boolean variable){
        if(width!=SENSOR&&width!=194)throw new IllegalArgumentException("unknown sensory schema");
        if(variable&&width!=194)throw new IllegalArgumentException("variable clock requires unified senses");
        inputs=width;variableClock=variable;
        targetCritic=variable?new float[2][HIDDEN+1]:new float[0][];
        valueReplay=new float[variable?128*(2*HIDDEN+2):0];
        wz=new float[HIDDEN*(inputs+HIDDEN+1)];
        wr=new float[wz.length];wn=new float[wz.length];
        world=new float[inputs*(HIDDEN+ACTION+1)];before=new float[inputs];
        for(int i=0;i<wz.length;i++){
            wz[i]=(random()-.5f)*.035f;
            wr[i]=(random()-.5f)*.035f;
            wn[i]=(random()-.5f)*.035f;
        }
        for(int i=0;i<actor.length;i++)actor[i]=(random()-.5f)*.025f;
        if(variableClock)for(int head=0;head<2;head++)for(int j=0;j<=HIDDEN;j++){
            critic[head][j]=(head==0?1f:-1f)*.0001f*(j%7-3);
            targetCritic[head][j]=critic[head][j];
        }
    }
    private float random(){
        rng^=rng<<13;rng^=rng>>>7;rng^=rng<<17;
        return (rng>>>40)/16777216f;
    }
    private float sample(){
        float x=0;for(int i=0;i<6;i++)x+=random();
        return (x-3f)*.20f;
    }
    private static float clip(float v,float lo,float hi){return Math.max(lo,Math.min(hi,v));}
    private static float sigmoid(float v){return 1f/(1f+(float)Math.exp(-clip(v,-16f,16f)));}
    private float[] sense(float[] raw){
        if(raw==null||raw.length!=inputs)throw new IllegalArgumentException("sensor width");
        float[] x=raw.clone();
        for(int i=0;i<x.length;i++){
            if(!Float.isFinite(x[i]))throw new IllegalArgumentException("invalid sensor");
            x[i]=clip(x[i],-1f,1f);
        }
        return x;
    }
    private void forward(float[] x,float[] from,float[] to,
        float[] z,float[] r,float[] n,float[] wz,float[] wr,float[] wn){
        int stride=inputs+HIDDEN+1;
        for(int i=0;i<HIDDEN;i++){
            int at=i*stride;
            float iz=wz[at+stride-1],ir=wr[at+stride-1];
            for(int j=0;j<inputs;j++){iz+=wz[at+j]*x[j];ir+=wr[at+j]*x[j];}
            for(int j=0;j<HIDDEN;j++){iz+=wz[at+inputs+j]*from[j];ir+=wr[at+inputs+j]*from[j];}
            z[i]=sigmoid(iz);r[i]=sigmoid(ir);
        }
        for(int i=0;i<HIDDEN;i++){
            int at=i*stride;
            float in=wn[at+stride-1];
            for(int j=0;j<inputs;j++)in+=wn[at+j]*x[j];
            for(int j=0;j<HIDDEN;j++)in+=wn[at+inputs+j]*r[j]*from[j];
            n[i]=(float)Math.tanh(in);
            to[i]=from[i]+z[i]*(n[i]-from[i]);
        }
    }
    private static float read(float[] w,float[] hidden){
        float v=w[HIDDEN];
        for(int k=0;k<HIDDEN;k++)v+=w[k]*hidden[k];
        return clip(v,-10,10);
    }
    private static float dot(float[] w,int at,float[] h,int size){
        float a=w[at+size];
        for(int i=0;i<size;i++)a+=w[at+i]*h[i];
        return a;
    }
    public boolean variableClock(){return variableClock;}
    public boolean cognitiveMode(){return cognition!=null;}
    public Zero3CognitionV353.Snapshot cognitionSnapshot(){return cognition==null?null:cognition.snapshot();}
    public void enableCognitionV353(){
        if(!variableClock||pending||trainCount+validationCount!=0||cognition!=null)throw new IllegalStateException("V353 fresh variable core required");
        cognition=new Zero3CognitionV353();
    }
    float measuredConfidenceV354(){
        if(cognition==null)return 0;Zero3CognitionV353.Snapshot s=cognition.snapshot();float quality=0;
        for(int h=0;h<3;h++)if(s.reliable[h]&&s.baselineMse[h]>0)quality+=clip(1-s.mse[h]/s.baselineMse[h],0,1)/3;
        return quality; // measured model advantage, not a success probability
    }
    public boolean completionModeV354(){return executive!=null;}
    public long observationsV354(){return observationCount;}
    public boolean observingV354(){return observeV354;}
    public void observingV354(boolean enabled){if(executive==null||pending)throw new IllegalStateException("V354 boundary required");observeV354=enabled;}
    public Zero3ExecutiveV354.Snapshot executiveSnapshot(){return executive==null?null:executive.snapshot();}
    void enableExecutiveV354(){if(cognition==null||pending||trainCount+validationCount!=0||executive!=null)throw new IllegalStateException("V354 fresh cognition required");executive=new Zero3ExecutiveV354();plannerV354=new Zero3PlannerV354();}
    void completeExecutiveV354(float motor,float energy,float harm,float cost,boolean heldout,boolean motorEvidence){
        if(executive==null)throw new IllegalStateException("V354 unavailable");
        executive.complete(motor,energy,harm,cost,heldout,observeV354,cognition.snapshot(),motorEvidence);
    }
    Zero3RecurrentBrain inheritedV354(long parentEpisode,long seed)throws IOException{
        if(executive==null||pending)throw new IOException("V354 completed parent required");
        Zero3RecurrentBrain child=new Zero3RecurrentBrain(inputs,true);child.enableCognitionV353();child.enableExecutiveV354();
        float[][] source={wz,wr,wn,actor,critic[0],critic[1],world,targetCritic[0],targetCritic[1]};
        float[][] target={child.wz,child.wr,child.wn,child.actor,child.critic[0],child.critic[1],child.world,child.targetCritic[0],child.targetCritic[1]};
        for(int i=0;i<source.length;i++)System.arraycopy(source[i],0,target[i],0,source[i].length);
        child.cognition=cognition.inheritedV354();child.executive=executive.inherited(parentEpisode);child.rng=rng^seed;if(child.rng==0)child.rng=0x354ABCDEFL;
        return child;
    }
    long[] repairCountersV354(){return executive.repairs();}
    void repairCountersV354(long[] counters){executive.repairs(counters);}
    String ecologyStateV354(){return executive.ecology();}
    float nextRoomV354(){return executive.nextRoom();}
    void ecologyStateV354(String state,float next){executive.ecology(state,next);}
    long selectedOptionV354(){return executive==null?0:executive.selectedId();}
    void physicalTargetsV353(float[] targets,boolean heldout){
        if(cognition==null||!pending||physicalTargetsReadyV353)throw new IllegalStateException("V353 missing/duplicate physical transition");
        if(heldout!=(executive!=null?Zero3ConfigurationV354.heldout(trainCount+validationCount+observationCount):Zero3CognitionV353.heldout(trainCount+validationCount+observationCount)))throw new IllegalArgumentException("V353 partition mismatch");
        if(plannerV354!=null)plannerV354.complete(targets,heldout,observeV354);
        if(observeV354)cognition.abort();else cognition.complete(targets,heldout);physicalHeldoutV353=heldout;physicalTargetsReadyV353=true;
    }
    public int sensorWidth(){return inputs;}
    public static Zero3RecurrentBrain fromCheckpoint(byte[] raw){
        if(raw==null||raw.length<100||raw.length>CHECK_MAX_BYTES_V345)
            throw new IllegalArgumentException("invalid GRU checkpoint size");
        try {
            DataInputStream in=new DataInputStream(new ByteArrayInputStream(raw));
            if(in.readInt()!=CHECK_MAGIC_V345)throw new IllegalArgumentException("GRU magic");
            int version=in.readInt(),width=in.readInt();
            if(!((version==345&&width==SENSOR)||((version==351||version==352||version==353||version==354)&&width==194)))
                throw new IllegalArgumentException("unknown GRU sensory configuration");
            Zero3RecurrentBrain restored=new Zero3RecurrentBrain(width,version>=352);
            if(version>=353)restored.enableCognitionV353();
            if(version==354)restored.enableExecutiveV354();
            restored.restoreAtBoundaryV345(raw);return restored;
        }catch(IOException bad){throw new IllegalArgumentException("truncated GRU",bad);}
    }
    public int realRecurrentUnits(){return HIDDEN;}
    public long trainingUpdates(){return trainCount;}
    float[] learningStateV353(){
        float[] p=parameterSnapshot(),result=Arrays.copyOf(p,p.length+valueReplay.length+targetCritic.length*(HIDDEN+1));
        int at=p.length;
        for(float[] a:targetCritic){System.arraycopy(a,0,result,at,a.length);at+=a.length;}
        System.arraycopy(valueReplay,0,result,at,valueReplay.length);return result;
    }
    public long recurrentUpdates(){return recurrentUpdates;}
    public long validations(){return validationCount;}
    public float heldoutModelError(){return heldoutModelError;}
    public float heldoutBaselineError(){return heldoutBaselineError;}
    public void resetLife(){
        if(pending)throw new IllegalStateException("censor unfinished experience first");
        Arrays.fill(state,0f);
    }
    public void abort(){
        pending=false;physicalTargetsReadyV353=false;if(cognition!=null)cognition.abort();
    }
    /**
     * The only source of high-level intentional directions in the experimental
     * autonomous loop. No object, world coordinates or reward class enters.
     */
    public Zero3LearningFoundation.MotorIntent choose(float[] observation,
            long episode,long decision,long cortexRevision){
        if(pending)throw new IllegalStateException("unfinished decision");
        if(cognition!=null&&decision!=trainCount+validationCount+observationCount)throw new IllegalArgumentException("V353 causal decision cursor");
        float[] x=sense(observation);
        System.arraycopy(state,0,prior,0,HIDDEN);
        forward(x,prior,state,gatedZ,gatedR,candidate,wz,wr,wn);
        System.arraycopy(state,0,decisionState,0,HIDDEN);
        System.arraycopy(x,0,before,0,inputs);
        storedValue=.5f*(read(critic[0],state)+read(critic[1],state));
        for(int out=0;out<MOTOR;out++){
            int at=out*(HIDDEN+1);
            proposal[out]=dot(actor,at,state,HIDDEN);
            lastNoise[out]=sample();
        }
        float[][] options=new float[cognition==null?1:3][5];
        float[][] noise=new float[options.length][MOTOR];
        for(int k=0;k<options.length;k++){
            for(int j=0;j<MOTOR;j++)noise[k][j]=k==0?lastNoise[j]:sample();
            float sx=clip(proposal[0]+noise[k][0],-1,1),sy=clip(proposal[1]+noise[k][1],-1,1);
            float len=(float)Math.hypot(sx,sy);if(len<.00001f){sx=1;sy=0;len=1;}
            options[k][0]=sx/len;options[k][1]=sy/len;
            options[k][2]=sigmoid(proposal[2]+noise[k][2]);options[k][3]=sigmoid(proposal[3]+noise[k][3]);
            options[k][4]=.25f+.4f*sigmoid(proposal[4]+noise[k][4]);
            if(cognition!=null)options[k][4]=Math.round(options[k][4]/.025f)*.025f;
        }
        int selected=cognition==null?0:cognition.select(x,options);
        if(plannerV354!=null){
            plannerV354.topology(cognition.snapshot().predictiveUnits);
            boolean ready=validationCount>=64&&heldoutModelError<heldoutBaselineError*.9f&&cognition.trusted(0)&&cognition.trusted(1)&&cognition.trusted(2);
            int planned=plannerV354.select(x,decisionState,options,modelV354(),ready);if(planned>=0)selected=planned;
        }
        System.arraycopy(noise[selected],0,lastNoise,0,MOTOR);
        float[] authorized=options[selected];
        optionReplayV354=false;
        if(executive!=null){authorized=executive.propose(x,authorized,decision,observeV354);optionReplayV354=executive.usedMemory();}
        float dx=authorized[0],dy=authorized[1],effort=authorized[2],
            adhesion=authorized[3],duration=authorized[4];
        if(cognition!=null)cognition.begin(x,authorized);
        if(plannerV354!=null)plannerV354.begin(decision,x,decisionState,authorized,modelV354(),Zero3ConfigurationV354.heldout(decision),observeV354,cognition.baselineV354());
        action[0]=dx;action[1]=dy;action[2]=effort;action[3]=adhesion;
        Arrays.fill(worldInput,0);
        System.arraycopy(decisionState,0,worldInput,0,HIDDEN);
        System.arraycopy(action,0,worldInput,HIDDEN,ACTION);
        worldInput[worldInput.length-1]=1f;
        pending=true;
        return new Zero3LearningFoundation.MotorIntent(episode,decision,cortexRevision,
                dx,dy,effort,adhesion,duration);
    }
    public Zero3PlannerV354.Snapshot plannerSnapshotV354(){return plannerV354==null?null:plannerV354.snapshot();}
    private Zero3PlannerV354.Predictor modelV354(){return new Zero3PlannerV354.Predictor(){
        public float[] meanAction(float[] h){
            float[] a=new float[5];for(int i=0;i<5;i++)a[i]=dot(actor,i*(HIDDEN+1),h,HIDDEN);
            float len=(float)Math.hypot(a[0],a[1]);if(len<.00001f){a[0]=1;a[1]=0;len=1;}a[0]/=len;a[1]/=len;
            a[2]=sigmoid(a[2]);a[3]=sigmoid(a[3]);a[4]=Math.round((.25f+.4f*sigmoid(a[4]))/.025f)*.025f;return a;
        }
        public Zero3PlannerV354.Step step(float[] o,float[] h,float[] a){
            float[] physical=cognition.predictionV354(o,a),next=new float[inputs],z=new float[HIDDEN];
            for(int d=0;d<inputs;d++){
                int at=d*worldInput.length;float delta=world[at+HIDDEN+ACTION];
                for(int i=0;i<HIDDEN;i++)delta+=world[at+i]*h[i];for(int i=0;i<ACTION;i++)delta+=world[at+HIDDEN+i]*a[i];
                next[d]=clip(o[d]+delta,-1,1);
            }
            forward(next,h,z,new float[HIDDEN],new float[HIDDEN],new float[HIDDEN],wz,wr,wn);
            return new Zero3PlannerV354.Step(next,z,Arrays.copyOf(physical,12),physical[12]);
        }
    };}
    void appendGraphV354(Zero3NeuralGraphV354.Builder b){
        int stride=inputs+HIDDEN+1;
        for(int u=0;u<HIDDEN;u++){
            float[] gates=new float[stride*3];System.arraycopy(wz,u*stride,gates,0,stride);System.arraycopy(wr,u*stride,gates,stride,stride);System.arraycopy(wn,u*stride,gates,2*stride,stride);
            b.node(0,u,stride,"GRU "+u+" Z/R/N",state[u],gates,0,gates.length);
        }
        for(int gate=0;gate<3;gate++){
            float[] w=gate==0?wz:gate==1?wr:wn;
            for(int u=0;u<HIDDEN;u++)for(int i=0;i<stride;i++)b.edge(i>=inputs&&i<inputs+HIDDEN?i-inputs:-1-i,u,gate,w[u*stride+i]);
        }
        int actorBase=b.nodes.size();
        for(int u=0;u<MOTOR;u++)b.node(1,u,HIDDEN+1,new String[]{"direction X","direction Y","effort","adhesion","duration"}[u],proposal[u],actor,u*(HIDDEN+1),HIDDEN+1);
        for(int u=0;u<MOTOR;u++)for(int i=0;i<=HIDDEN;i++)b.edge(i==HIDDEN?-1000:i,actorBase+u,0,actor[u*(HIDDEN+1)+i]);
        for(int u=0;u<2;u++){
            int id=b.node(2,u,HIDDEN+1,"critic "+u,read(critic[u],state),critic[u],0,HIDDEN+1);
            for(int i=0;i<=HIDDEN;i++)b.edge(i==HIDDEN?-1000:i,id,0,critic[u][i]);
        }
        for(int u=0;u<inputs;u++){
            int id=b.node(3,u,worldInput.length,"sensory prediction "+u,Float.NaN,world,u*worldInput.length,worldInput.length);
            for(int i=0;i<worldInput.length;i++)b.edge(i<HIDDEN?i:-1-i,id,0,world[u*worldInput.length+i]);
        }
        for(int u=0;u<targetCritic.length;u++){
            int id=b.node(11,u,HIDDEN+1,"target critic "+u,read(targetCritic[u],state),targetCritic[u],0,HIDDEN+1);
            for(int i=0;i<=HIDDEN;i++)b.edge(i==HIDDEN?-1000:i,id,0,targetCritic[u][i]);
        }
        if(cognition!=null)cognition.appendGraphV354(b);
        if(executive!=null)executive.appendGraphV354(b);
    }
    public float[] recurrentState(){return state.clone();}
    /** Flatten trainable parameters, excluding transient hidden state / RNG. */
    public float[] parameterSnapshot(){
        int size=wz.length+wr.length+wn.length+actor.length+world.length+2*(HIDDEN+1);
        float[] x=new float[size];int at=0;
        for(float[] a:new float[][]{wz,wr,wn,actor,critic[0],critic[1],world}){
            System.arraycopy(a,0,x,at,a.length);at+=a.length;
        }
        return x;
    }
    /**
     * Feed a REAL post-physics observation and REAL reward, never imagination.
     * The heldout branch evaluates but does not update any trainable weight.
     * Currently TD actor/dual critics and one-step recurrent world model.
     */
    public void consequence(float[] rawNext,float realSurvivalReward,boolean heldout){
        completeConsequence(rawNext,realSurvivalReward,heldout,true,false,.3f);
    }
    /**
     * V350 actual V325 runs exactly 12 physical substeps (=0.30s).
     * Until the joint physical/causal schema is upgraded to variable durations,
     * the sampled fifth actor logit has NO executed causal effect and MUST NOT
     * receive REINFORCE credit from the other four physical choices.
     *
     * Historical V345 GRU test semantics remain available through consequence().
     * GRU weights/topology/checkpoint encoding are unchanged.
     */
    public void consequenceFixedClockV350(float[] rawNext,float realSurvivalReward,
                                         boolean heldout){
        completeConsequence(rawNext,realSurvivalReward,heldout,false,false,.3f);
    }
    public void consequenceObservedV352(float[] next,float reward,boolean heldout,
            float elapsed,boolean terminal,boolean interrupted){
        if(!variableClock||!Float.isFinite(elapsed)||elapsed<.0249f||elapsed>.6501f)
            throw new IllegalArgumentException("V352 requires real physical elapsed time");
        completeConsequence(next,reward,heldout,!interrupted,terminal,elapsed);
    }
    private void completeConsequence(float[] rawNext,float realSurvivalReward,
                                    boolean heldout,boolean durationExecuted,boolean terminal,float elapsed){
        if(!pending)throw new IllegalStateException("missing decision");
        if(cognition!=null&&(!physicalTargetsReadyV353||physicalHeldoutV353!=heldout))
            throw new IllegalStateException("V353 outcome/learning partition mismatch");
        if(!Float.isFinite(realSurvivalReward))throw new IllegalArgumentException("nonfinite reward");
        float[] next=sense(rawNext);
        pending=false;physicalTargetsReadyV353=false;
        if(observeV354){observationCount++;return;}
        float[] predictionError=new float[inputs];
        float modelMse=0,baselineMse=0;
        float[] dh=new float[HIDDEN];
        final int WIDTH=worldInput.length;
        for(int d=0;d<inputs;d++){
            int at=d*WIDTH;
            float predicted=0;
            for(int j=0;j<WIDTH;j++)predicted+=world[at+j]*worldInput[j];
            float actual=next[d]-before[d];
            float err=clip(actual-predicted,-2,2);
            predictionError[d]=err;
            modelMse+=err*err;baselineMse+=actual*actual;
            if(!heldout){
                for(int j=0;j<HIDDEN;j++)dh[j]+=err*world[at+j];
                for(int j=0;j<WIDTH;j++)
                    world[at+j]=clip(world[at+j]+MODEL_LR*err*worldInput[j],-1.5f,1.5f);
            }
        }
        if(heldout){
            float err=modelMse/inputs,base=baselineMse/inputs;
            float a=validationCount==0?1f:.06f;
            heldoutModelError=validationCount==0?err:heldoutModelError*(1-a)+a*err;
            heldoutBaselineError=validationCount==0?base:heldoutBaselineError*(1-a)+a*base;
            validationCount++;
            return;
        }
        // Actual next-state bootstrap. Preview never consumes or trains the
        // next experience; it is NOT synthetic world physics.
        float[] preview=new float[HIDDEN];
        forward(next,state,preview,new float[HIDDEN],new float[HIDDEN],
                new float[HIDDEN],wz,wr,wn);
        float nextValue=variableClock?
            Math.min(read(targetCritic[0],preview),read(targetCritic[1],preview)):
            .5f*(read(critic[0],preview)+read(critic[1],preview));
        float discount=terminal?0f:(elapsed==.3f?.98f:(float)Math.pow(.98f,elapsed/.3f));
        float target=clip(realSurvivalReward+discount*nextValue,-3,3);
        float advantage=clip(target-storedValue,-2,2);
        for(int which=0;which<2;which++){
            float value=read(critic[which],decisionState);
            float err=clip(target-value,-2,2);
            for(int j=0;j<HIDDEN;j++)
                critic[which][j]=clip(critic[which][j]+CRITIC_LR*err*decisionState[j],-2,2);
            critic[which][HIDDEN]=clip(critic[which][HIDDEN]+CRITIC_LR*err,-2,2);
        }
        if(variableClock){
            final int strideValue=2*HIDDEN+2;
            int at=(int)(trainCount%128)*strideValue;
            System.arraycopy(decisionState,0,valueReplay,at,HIDDEN);
            System.arraycopy(preview,0,valueReplay,at+HIDDEN,HIDDEN);
            valueReplay[at+2*HIDDEN]=realSurvivalReward;
            valueReplay[at+2*HIDDEN+1]=discount;
            int count=(int)Math.min(128,trainCount+1);
            for(int sample=0;sample<4;sample++){
                int slot=(int)Math.floorMod(trainCount*8191L+sample*31L,count);
                int offset=slot*strideValue;
                float[] oldState=new float[HIDDEN],newState=new float[HIDDEN];
                System.arraycopy(valueReplay,offset,oldState,0,HIDDEN);
                System.arraycopy(valueReplay,offset+HIDDEN,newState,0,HIDDEN);
                float replayTarget=clip(valueReplay[offset+2*HIDDEN]+
                    valueReplay[offset+2*HIDDEN+1]*Math.min(read(targetCritic[0],newState),read(targetCritic[1],newState)),-3,3);
                for(int head=0;head<2;head++){
                    float error=clip(replayTarget-read(critic[head],oldState),-2,2);
                    for(int j=0;j<HIDDEN;j++)critic[head][j]=clip(critic[head][j]+.25f*CRITIC_LR*error*oldState[j],-2,2);
                    critic[head][HIDDEN]=clip(critic[head][HIDDEN]+.25f*CRITIC_LR*error,-2,2);
                }
            }
            for(int head=0;head<2;head++)for(int j=0;j<=HIDDEN;j++)
                targetCritic[head][j]+=.02f*(critic[head][j]-targetCritic[head][j]);
        }
        for(int k=0;k<MOTOR;k++){
            if(optionReplayV354)continue; // recalled action did not execute this sampled noise
            if(k==MOTOR-1&&!durationExecuted)continue; // V350 no fictitious duration credit
            float explorationGradient=clip(lastNoise[k]/(.2f*.2f),-3f,3f);
            int at=k*(HIDDEN+1);
            for(int j=0;j<HIDDEN;j++)
                actor[at+j]=clip(actor[at+j]+ACTOR_LR*advantage*explorationGradient*decisionState[j],-1.5f,1.5f);
            actor[at+HIDDEN]=clip(actor[at+HIDDEN]+ACTOR_LR*advantage*explorationGradient,-1.5f,1.5f);
        }
        // One-step truncated-BPTT through ALL GRU gates (the past hidden state
        // is detached). This does not falsely claim long-range BPTT.
        float[] dn=new float[HIDDEN],dz=new float[HIDDEN],dr=new float[HIDDEN];
        int stride=inputs+HIDDEN+1;
        for(int i=0;i<HIDDEN;i++){
            dn[i]=clip(dh[i]*gatedZ[i]*(1-candidate[i]*candidate[i]),-.75f,.75f);
            dz[i]=clip(dh[i]*(candidate[i]-prior[i])*gatedZ[i]*(1-gatedZ[i]),-.75f,.75f);
        }
        for(int j=0;j<HIDDEN;j++){
            float sum=0;
            for(int i=0;i<HIDDEN;i++)sum+=dn[i]*wn[i*stride+inputs+j]*prior[j];
            dr[j]=clip(sum*gatedR[j]*(1-gatedR[j]),-.75f,.75f);
        }
        float changed=0;
        for(int i=0;i<HIDDEN;i++){
            int at=i*stride;
            for(int j=0;j<inputs;j++){
                float old=wn[at+j];
                wn[at+j]=clip(old+MODEL_LR*dn[i]*before[j],-1.5f,1.5f);
                wz[at+j]=clip(wz[at+j]+MODEL_LR*dz[i]*before[j],-1.5f,1.5f);
                wr[at+j]=clip(wr[at+j]+MODEL_LR*dr[i]*before[j],-1.5f,1.5f);
                changed+=Math.abs(wn[at+j]-old);
            }
            for(int j=0;j<HIDDEN;j++){
                int ix=at+inputs+j;
                wn[ix]=clip(wn[ix]+MODEL_LR*dn[i]*gatedR[j]*prior[j],-1.5f,1.5f);
                wz[ix]=clip(wz[ix]+MODEL_LR*dz[i]*prior[j],-1.5f,1.5f);
                wr[ix]=clip(wr[ix]+MODEL_LR*dr[i]*prior[j],-1.5f,1.5f);
            }
            wn[at+stride-1]=clip(wn[at+stride-1]+MODEL_LR*dn[i],-1.5f,1.5f);
            wz[at+stride-1]=clip(wz[at+stride-1]+MODEL_LR*dz[i],-1.5f,1.5f);
            wr[at+stride-1]=clip(wr[at+stride-1]+MODEL_LR*dr[i],-1.5f,1.5f);
        }
        if(changed>0f)recurrentUpdates++;
        trainCount++;
    }

    // V345 G4 prerequisite: complete true recurrent actor/critic/GRU
    // checkpoint at decision boundaries only, separate from old V321/V328.
    // This does NOT claim that the brain is now plugged into V325 runtime.
    private static final int CHECK_MAGIC_V345=0x5A333435,
                             CHECK_VERSION_V345=345,
                             CHECK_MAX_BYTES_V345=1024*1024;
    private boolean restoredV345;
    private float[][] fullCheckpointArraysV345(){
        float[][] base={wz,wr,wn,actor,critic[0],critic[1],world,state,prior,
            gatedZ,gatedR,candidate,decisionState,before,lastNoise,action,worldInput,proposal};
        if(!variableClock)return base;
        float[][] complete=Arrays.copyOf(base,base.length+3);
        complete[base.length]=targetCritic[0];complete[base.length+1]=targetCritic[1];
        complete[base.length+2]=valueReplay;return complete;
    }
    public byte[] checkpointAtBoundaryV345(){
        if(pending)throw new IllegalStateException(
            "V345 cannot snapshot an unresolved real decision");
        try{
            ByteArrayOutputStream buffer=new ByteArrayOutputStream(128*1024);
            DataOutputStream out=new DataOutputStream(buffer);
            out.writeInt(CHECK_MAGIC_V345);
            out.writeInt(executive!=null?354:cognition!=null?353:variableClock?352:(inputs==SENSOR?CHECK_VERSION_V345:351));
            out.writeInt(inputs);
            out.writeInt(HIDDEN);
            out.writeInt(MOTOR);
            out.writeInt(ACTION);
            out.writeLong(rng);
            out.writeLong(trainCount);
            out.writeLong(validationCount);
            out.writeLong(recurrentUpdates);
            out.writeFloat(heldoutModelError);
            out.writeFloat(heldoutBaselineError);
            out.writeFloat(storedValue);
            float[][] values=fullCheckpointArraysV345();
            out.writeInt(values.length);
            for(float[] array:values){
                out.writeInt(array.length);
                for(float x:array){
                    if(!Float.isFinite(x))throw new IllegalStateException(
                        "V345 cannot persist nonfinite learned parameter");
                    out.writeFloat(x);
                }
            }
            if(cognition!=null){byte[] cognitive=cognition.encode();out.writeInt(cognitive.length);out.write(cognitive);}
            if(executive!=null){
                out.writeUTF(Zero3ConfigurationV354.HASH);out.writeLong(observationCount);out.writeBoolean(observeV354);
                byte[] options=executive.encode();out.writeInt(options.length);out.write(options);
                byte[] planner=plannerV354.encode();out.writeInt(planner.length);out.write(planner);
            }
            out.flush();
            byte[] payload=buffer.toByteArray();
            CRC32 crc=new CRC32();
            crc.update(payload);
            out.writeInt((int)crc.getValue());
            out.flush();
            byte[] result=buffer.toByteArray();
            if(result.length>CHECK_MAX_BYTES_V345)
                throw new IllegalStateException("V345 oversized GRU checkpoint");
            return result;
        }catch(IOException unexpected){
            throw new IllegalStateException("V345 encoder failed",unexpected);
        }
    }
    /**
     * Decode and check the WHOLE blob before mutating even one weight.
     * Only a fresh instance may be restored, once. No hidden retraining,
     * no silent reset, and no incomplete pending-decision credit.
     */
    public void restoreAtBoundaryV345(byte[] raw){
        if(pending||restoredV345||trainCount!=0||
           validationCount!=0||observationCount!=0||recurrentUpdates!=0)
            throw new IllegalStateException("V345 restoration only on virgin core");
        if(raw==null||raw.length<100||raw.length>CHECK_MAX_BYTES_V345)
            throw new IllegalArgumentException("V345 invalid checkpoint length");
        CRC32 crc=new CRC32();
        crc.update(raw,0,raw.length-4);
        int tail=raw.length-4;
        int stored=((raw[tail]&255)<<24)|((raw[tail+1]&255)<<16)|
                   ((raw[tail+2]&255)<<8)|(raw[tail+3]&255);
        if((int)crc.getValue()!=stored)
            throw new IllegalArgumentException("V345 corrupted checksum");
        try{
            DataInputStream in=new DataInputStream(
                new ByteArrayInputStream(raw,0,tail));
            if(in.readInt()!=CHECK_MAGIC_V345||
               in.readInt()!=(executive!=null?354:cognition!=null?353:variableClock?352:(inputs==SENSOR?CHECK_VERSION_V345:351))||
               in.readInt()!=inputs||in.readInt()!=HIDDEN||
               in.readInt()!=MOTOR||in.readInt()!=ACTION)
                throw new IllegalArgumentException(
                    "V345 unknown version / incompatible neuron dimensions");
            long nextRng=in.readLong(),
                 updates=in.readLong(),validations=in.readLong(),
                 recurrent=in.readLong();
            float modelErr=in.readFloat(),baselineErr=in.readFloat(),
                  value=in.readFloat();
            if(nextRng==0||updates<0||validations<0||recurrent<0||
               recurrent>updates||!Float.isFinite(value)||
               (validations==0&&
                 (!Float.isNaN(modelErr)||!Float.isNaN(baselineErr)))||
               (validations>0&&
                 (!Float.isFinite(modelErr)||!Float.isFinite(baselineErr)||
                  modelErr<0||baselineErr<0)))
                throw new IllegalArgumentException(
                    "V345 invalid RNG/counters/validation statistics");
            float[][] dest=fullCheckpointArraysV345();
            if(in.readInt()!=dest.length)
                throw new IllegalArgumentException(
                    "V345 incorrect neural topology");
            float[][] decoded=new float[dest.length][];
            for(int j=0;j<dest.length;j++){
                int width=in.readInt();
                if(width!=dest[j].length)
                    throw new IllegalArgumentException(
                        "V345 different array width "+j);
                decoded[j]=new float[width];
                for(int k=0;k<width;k++){
                    decoded[j][k]=in.readFloat();
                    if(!Float.isFinite(decoded[j][k]))
                        throw new IllegalArgumentException(
                            "V345 invalid learned weight "+j+"/"+k);
                }
            }
            Zero3CognitionV353 decodedCognition=null;
            if(cognition!=null){
                int size=in.readInt();if(size<100||size>250000||size>in.available())throw new IOException("V353 cognitive size");
                byte[] cognitive=new byte[size];in.readFully(cognitive);decodedCognition=Zero3CognitionV353.decode(cognitive);
                Zero3CognitionV353.Snapshot stats=decodedCognition.snapshot();
                if(stats.train!=updates||stats.heldout!=validations)throw new IOException("V353 mixed neural generations");
            }
            Zero3PlannerV354 decodedPlanner=null;
            Zero3ExecutiveV354 decodedExecutive=null;long decodedObservations=0;boolean decodedObserve=false;
            if(executive!=null){
                if(!in.readUTF().equals(Zero3ConfigurationV354.HASH))throw new IOException("V354 configuration mismatch");
                decodedObservations=in.readLong();decodedObserve=in.readBoolean();
                int size=in.readInt();if(decodedObservations<0||size<100||size>120000||size>in.available())throw new IOException("V354 executive size/counters");
                byte[] options=new byte[size];in.readFully(options);decodedExecutive=Zero3ExecutiveV354.decode(options);
                int plannerSize=in.readInt();if(plannerSize<1||plannerSize>4096||plannerSize>in.available())throw new IOException("V354 planner blob");
                byte[] planner=new byte[plannerSize];in.readFully(planner);decodedPlanner=Zero3PlannerV354.decode(planner);
                Zero3ExecutiveV354.Snapshot e=decodedExecutive.snapshot();
                if(e.decisions!=updates+validations+decodedObservations||e.train!=updates||e.validation!=validations||e.observations!=decodedObservations)
                    throw new IOException("V354 executive generation mismatch");
            }
            if(in.available()!=0)
                throw new IllegalArgumentException(
                    "V345 unexpected trailing checkpoint data");
            // All checks completed: only now commit the new state.
            for(int j=0;j<dest.length;j++)
                System.arraycopy(decoded[j],0,dest[j],0,dest[j].length);
            if(decodedCognition!=null)cognition=decodedCognition;
            if(decodedExecutive!=null){executive=decodedExecutive;plannerV354=decodedPlanner;observationCount=decodedObservations;observeV354=decodedObserve;}
            rng=nextRng;
            trainCount=updates;validationCount=validations;
            recurrentUpdates=recurrent;
            heldoutModelError=modelErr;
            heldoutBaselineError=baselineErr;
            storedValue=value;
            restoredV345=true;
        }catch(IOException truncated){
            throw new IllegalArgumentException(
                "V345 incomplete neural checkpoint",truncated);
        }
    }
}
