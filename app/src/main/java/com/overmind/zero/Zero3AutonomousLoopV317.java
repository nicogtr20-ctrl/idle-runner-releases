package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * V3.17 E3 EXPERIMENT ONLY.
 * A single sensory-recurrent actor chooses ONE intent per 12 real physics
 * steps. Existing trainable cortex converts it into 64 real gel muscle
 * controls and receives verified motor consequences. A small recurrent
 * predictor learns the next physical observation, never a fictional reward.
 *
 * This is NOT an actor-critic, full ZUAB, learned goal system or Android
 * integration. Objective: make autonomous choice / motor credit measurable.
 */
public final class Zero3AutonomousLoopV317 {
    public static final int HIDDEN=16,INPUTS=20,STEPS=12;
    public static final float DT=.025f;
    public static final String OWNER="zero3-e3-proto-sensory-actor";
    private Zero3AutonomousLoopV317(){}
    static void ensure(boolean b,String msg){
        if(!b)throw new IllegalArgumentException("ZERO3 V317 "+msg);
    }
    static float clamp(float x,float a,float b){
        return Math.max(a,Math.min(b,x));
    }
    static final class Rng {
        long state;
        Rng(long seed){state=seed==0?0x92AE6D13A44710BFL:seed;}
        float uniform(){
            state^=state<<13;state^=state>>>7;state^=state<<17;
            return (state>>>40)/16777216f;
        }
    }
    /** Real measured local body, touch and rays. No world object labels. */
    public static float[] encode(Zero3SensoryFrameV315 sample){
        ensure(sample!=null&&sample.intact(),"tampered pre-sensor frame");
        float[] x=new float[INPUTS];
        for(int sector=0;sector<8;sector++){
            float total=0;
            for(int j=0;j<12;j++){
                int i=sector*12+j;
                total+=Math.max(sample.patch(i,Zero3SensoryFrameV315.TOUCH_SOLID),
                    sample.patch(i,Zero3SensoryFrameV315.TOUCH_IMPULSE));
            }
            x[sector]=clamp(total/12f,0f,1f);
        }
        float[] ray=sample.membraneRays();
        for(int i=0;i<8;i++)
            x[8+i]=clamp(Math.max(ray[2*i],ray[2*i+1]),0,1);
        x[16]=clamp(sample.bodyVelX/120f,-1,1);
        x[17]=clamp(sample.bodyVelY/120f,-1,1);
        float[] inner=sample.inner74x7();
        float total=0;
        for(int k=0;k<74;k++)total+=inner[k*7+FluidInteroceptionV1.DENSITY];
        x[18]=clamp(total/74f,-1,1);
        x[19]=clamp(sample.heldSites()/96f,0,1);
        for(float v:x)ensure(Float.isFinite(v),"nonfinite actor input");
        return x;
    }
    public static final class Encoder {
        private final float[] w=new float[HIDDEN*INPUTS],u=new float[HIDDEN*HIDDEN],
                              bias=new float[HIDDEN],read=new float[3*HIDDEN],
                              state=new float[HIDDEN],lastInput=new float[INPUTS],
                              lastPrev=new float[HIDDEN],lastHidden=new float[HIDDEN],
                              lastPrediction=new float[3];
        private final float[] actorX=new float[HIDDEN],actorY=new float[HIDDEN],
                              actorEffort=new float[HIDDEN],actorGrip=new float[HIDDEN];
        private final Rng rng;
        private boolean pending;
        private int steps,updates;
        private float learningL2;
        public Encoder(long seed){
            rng=new Rng(seed);
            for(int i=0;i<w.length;i++)w[i]=(rng.uniform()-.5f)*.12f;
            for(int i=0;i<u.length;i++)u[i]=(rng.uniform()-.5f)*.07f;
            for(int i=0;i<read.length;i++)read[i]=(rng.uniform()-.5f)*.035f;
            for(int i=0;i<HIDDEN;i++){
                actorX[i]=(rng.uniform()-.5f)*1.3f;
                actorY[i]=(rng.uniform()-.5f)*1.3f;
                actorEffort[i]=(rng.uniform()-.5f)*.5f;
                actorGrip[i]=(rng.uniform()-.5f)*.5f;
            }
        }
        public int updates(){return updates;}
        public float learningL2(){return learningL2;}
        public Encoder evaluationClone(){
            Encoder next=new Encoder(0x317L);
            System.arraycopy(w,0,next.w,0,w.length);
            System.arraycopy(u,0,next.u,0,u.length);
            System.arraycopy(bias,0,next.bias,0,bias.length);
            System.arraycopy(read,0,next.read,0,read.length);
            System.arraycopy(actorX,0,next.actorX,0,actorX.length);
            System.arraycopy(actorY,0,next.actorY,0,actorY.length);
            System.arraycopy(actorEffort,0,next.actorEffort,0,actorEffort.length);
            System.arraycopy(actorGrip,0,next.actorGrip,0,actorGrip.length);
            return next;
        }
        public void resetEpisode(){
            Arrays.fill(state,0f);pending=false;
        }
        /** Only PRE is admitted. post information is *never* actor input. */
        public float[] before(Zero3SensoryFrameV315 pre){
            ensure(pre.preAction&&!pending,"recurrent encoder requires one fresh PRE");
            float[] x=encode(pre);
            System.arraycopy(state,0,lastPrev,0,HIDDEN);
            System.arraycopy(x,0,lastInput,0,INPUTS);
            for(int j=0;j<HIDDEN;j++){
                float z=bias[j];
                for(int i=0;i<INPUTS;i++)z+=w[j*INPUTS+i]*x[i];
                for(int i=0;i<HIDDEN;i++)z+=u[j*HIDDEN+i]*lastPrev[i];
                lastHidden[j]=(float)Math.tanh(z);
            }
            System.arraycopy(lastHidden,0,state,0,HIDDEN);
            for(int o=0;o<3;o++){
                float y=0;
                for(int h=0;h<HIDDEN;h++)y+=read[o*HIDDEN+h]*state[h];
                lastPrediction[o]=clamp(y,-1f,1f);
            }
            pending=true;
            return lastPrediction.clone();
        }
        public float[] choose(){
            ensure(pending,"intent requested without pre-decision sensing");
            float dx=0,dy=0,eff=0,grip=0;
            for(int h=0;h<HIDDEN;h++){
                dx+=state[h]*actorX[h];
                dy+=state[h]*actorY[h];
                eff+=state[h]*actorEffort[h];
                grip+=state[h]*actorGrip[h];
            }
            float length=(float)Math.hypot(dx,dy);
            // Sensory recurrent actor, not compass to gem, not prescribed gait.
            // Near-zero net command is allowed as a true stationary choice.
            if(length<.0001f){dx=dy=0f;}
            else{dx/=length;dy/=length;}
            float effort=clamp(.50f+eff,.10f,.85f);
            float adhesion=clamp(.45f+grip,0f,.95f);
            return new float[]{dx,dy,effort,adhesion};
        }
        public void after(Zero3SensoryFrameV315 post,boolean train){
            ensure(pending&&!post.preAction&&post.intact(),
                   "recurrent update requires authentic post state");
            float[] actual={clamp(post.bodyVelX/120f,-1f,1f),
                            clamp(post.bodyVelY/120f,-1f,1f),
                            clamp((post.touchedSites()+post.pulsedSites())/96f,0,1)};
            // Real one-step prediction target; do not make up energy or reward.
            if(train){
                for(int o=0;o<3;o++){
                    float delta=clamp(actual[o]-lastPrediction[o],-1f,1f);
                    for(int h=0;h<HIDDEN;h++){
                        int idx=o*HIDDEN+h;
                        float dw=clamp(.018f*delta*lastHidden[h],-.005f,.005f);
                        read[idx]=clamp(read[idx]+dw,-2f,2f);
                        learningL2+=dw*dw;
                    }
                    // Truncated causal gradient to recent input and recurrence,
                    // NOT end-to-end BPTT or a complete trainable GRU.
                    for(int h=0;h<HIDDEN;h++){
                        float dz=clamp(.0015f*delta*lastHidden[h]*
                            (1-lastHidden[h]*lastHidden[h]),-.01f,.01f);
                        for(int j=0;j<INPUTS;j++){
                            int ix=h*INPUTS+j;
                            float change=clamp(dz*lastInput[j],-.002f,.002f);
                            w[ix]=clamp(w[ix]+change,-1.5f,1.5f);
                            learningL2+=change*change;
                        }
                    }
                }
                updates++;
            }
            pending=false;steps++;
        }
    }
    public static final class Decision {
        public final Zero3MotorIntentV316 intent;
        public final Zero3SensoryFrameV315 before,after;
        public final int realPhysicsSteps;
        public final float displacementX,displacementY,gross,work,alignment;
        public final float[] predicted,actual;
        public final int realContacts,realGrip,adhesiveCommandSectors,
                         adhesiveCommandAtContactSites;
        public final long actionEvidenceHash;
        public final boolean trained;
        Decision(Zero3MotorIntentV316 intent,Zero3SensoryFrameV315 pre,
                 Zero3SensoryFrameV315 post,int steps,float dx,float dy,
                 float gross,float work,float alignment,float[] predicted,
                 int contacts,int grip,int commandedAdhesive,
                 int adhesiveAndTouch,long actionEvidenceHash,boolean trained){
            this.intent=intent;before=pre;after=post;realPhysicsSteps=steps;
            displacementX=dx;displacementY=dy;this.gross=gross;this.work=work;
            this.alignment=alignment;this.predicted=predicted.clone();
            this.actual=new float[]{clamp(post.bodyVelX/120f,-1,1),
                clamp(post.bodyVelY/120f,-1,1),
                clamp((post.touchedSites()+post.pulsedSites())/96f,0,1)};
            realContacts=contacts;realGrip=grip;
            adhesiveCommandSectors=commandedAdhesive;
            adhesiveCommandAtContactSites=adhesiveAndTouch;
            this.actionEvidenceHash=actionEvidenceHash;this.trained=trained;
        }
        public double predictionMse(){
            double sum=0;
            for(int j=0;j<3;j++){
                double v=actual[j]-predicted[j];sum+=v*v;
            }
            return sum/3.;
        }
        public double persistenceMse(){
            double[] p={clamp(before.bodyVelX/120f,-1,1),
                clamp(before.bodyVelY/120f,-1,1),
                clamp((before.touchedSites()+before.pulsedSites())/96f,0,1)};
            double sum=0;for(int j=0;j<3;j++)sum+=(actual[j]-p[j])*(actual[j]-p[j]);
            return sum/3.;
        }
    }
    public static final class Loop {
        public final Encoder encoder;
        public final Zero3LearningFoundation.AdaptiveCortex cortex;
        public final Zero3LearningFoundation.Coordinator coordinator;
        private final boolean training;
        private int selected,physicalSteps;
        public Loop(Encoder encoder,Zero3LearningFoundation.AdaptiveCortex cortex,
                    boolean train){
            ensure(encoder!=null&&cortex!=null,"missing actor or cortex");
            this.encoder=encoder;this.cortex=cortex;training=train;
            coordinator=new Zero3LearningFoundation.Coordinator(cortex);
        }
        public int selected(){return selected;}
        public int physicalSteps(){return physicalSteps;}
        /** Exactly 12 true world+gel steps, one chosen intention. */
        public Decision act(Zero3MotorIntentV316.Authority authority,long episode,
                            int decision,Zero3MotorImpulseResponseRegression.Scenario sc,
                            ArrayList<UnknownWorld.Entity> world,int mode,
                            float[] lastExecuted){
            ensure(authority.nextDecision()==decision,"duplicate/skip chosen intent");
            final double at=(sc.gel.ticks)*DT;
            final long startTick=sc.gel.ticks;
            Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
                episode,decision,at,true,sc.body,sc.skin,sc.gel,world,lastExecuted);
            float[] guess=encoder.before(pre);
            float[] choice=encoder.choose();
            Zero3MotorIntentV316 intent=authority.issue(OWNER,decision,
                (int)cortex.revision(),317,choice[0],choice[1],
                choice[2],choice[3],STEPS*DT,.35f);
            Zero3LearningFoundation.MotorIntent legacy=
                new Zero3LearningFoundation.MotorIntent(episode,decision,
                    cortex.revision(),choice[0],choice[1],choice[2],
                    choice[3],STEPS*DT);
            coordinator.begin(legacy,sc.body,training,!training,training);
            float startX=sc.body.cx,startY=sc.body.cy;
            float prevX=startX,prevY=startY,gross=0,work=0;
            int contact=0,grip=0,adhesiveSectors=0,adhesiveAndTouch=0;
            long tapeHash=0xcbf29ce484222325L;
            float[] applied=null;
            for(int step=0;step<STEPS;step++){
                float[] m=coordinator.actuate(sc.body,sc.skin,DT);
                applied=m.clone();
                for(float cmd:applied){
                    ensure(Float.isFinite(cmd)&&Math.abs(cmd)<=1.001f,"invalid 64D actuation");
                    tapeHash=(tapeHash^Float.floatToIntBits(cmd))*1099511628211L;
                }
                for(int sector=0;sector<16;sector++)
                    if(applied[sector*4+2]>Morphology.GRIP_THRESHOLD)
                        adhesiveSectors++;
                float[] oldSX=sc.skin.x.clone(),oldSY=sc.skin.y.clone(),
                        oldBX=sc.body.x.clone(),oldBY=sc.body.y.clone();
                float oldCX=sc.body.cx,oldCY=sc.body.cy;
                float beforeWork=sc.gel.work;
                sc.gel.step(sc.body,sc.skin,applied,DT);
                Zero3SensoryContractV315Regression.applyRealWorld(
                    mode,sc,world,oldSX,oldSY,oldBX,oldBY,oldCX,oldCY);
                ensure(sc.gel.invalid==0&&Float.isFinite(sc.body.cx)&&
                       Float.isFinite(sc.body.cy),"invalid post-collision gel");
                float newWork=Math.max(0f,sc.gel.work-beforeWork);
                coordinator.observe(sc.body,DT,newWork,
                    Math.max(0f,sc.gel.lastMuscleReactionImpulse),false,false);
                work+=newWork;
                gross+=(float)Math.hypot(sc.body.cx-prevX,sc.body.cy-prevY);
                prevX=sc.body.cx;prevY=sc.body.cy;
                for(int i=0;i<Morphology.POINTS;i++){
                    if(sc.skin.touchSense[i]>.01f||sc.skin.touchPulse[i]>.001f)contact++;
                    if(sc.skin.gripHold[i]>=Morphology.GRIP_HOLD)grip++;
                    if(sc.skin.gripCommand[i]>Morphology.GRIP_THRESHOLD&&
                      (sc.skin.touchSense[i]>.01f||sc.skin.touchPulse[i]>.001f))
                        adhesiveAndTouch++;
                }
                physicalSteps++;
            }
            // Consequence is observed AFTER all actual world solvers and
            // all motor commands, before any gradient or learned revision.
            Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
                episode,decision+1,at+STEPS*DT,false,
                sc.body,sc.skin,sc.gel,world,applied);
            ensure(sc.gel.ticks==startTick+STEPS,"incorrect real step count");
            ensure(pre.intact()&&post.intact(),"altered causal input/outcome");
            float dx=sc.body.cx-startX,dy=sc.body.cy-startY;
            float projection=dx*intent.directionX+dy*intent.directionY;
            // Only assess fulfillment of voluntary effort, not generic
            // distance moved. This never trains the actor/survival reward.
            float alignment=intent.desiredEffort>.01f?projection/(gross+1f):0;
            Zero3LearningFoundation.Experience e=coordinator.finish();
            ensure(e.frames==STEPS&&e.intent.decisionId==decision,
                   "motor credit did not belong to this decision");
            encoder.after(post,training);
            selected++;
            return new Decision(intent,pre,post,STEPS,dx,dy,gross,work,alignment,
                                guess,contact,grip,adhesiveSectors,
                                adhesiveAndTouch,tapeHash,training);
        }
    }
}
