package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * V3.18 preregistered closed-loop adhesion experiment.
 * ONLY adhesion sector motor head learns; other 48 cortex channels and the
 * recurring actor are frozen. Learned and baseline are evaluated WITHOUT
 * exploration on matched new worlds. No physical contact or grip injected.
 */
public final class Zero3LearnedAdhesionV318Regression {
    static final float DT=.025f;
    static final int STEPS=12,DECISIONS=96,WARMUP=96;
    static final int FREE=0,BOX=1,DISK=2,FAR=3;
    static final String OWNER=Zero3AutonomousLoopV317.OWNER;
    private Zero3LearnedAdhesionV318Regression(){}
    static void require(boolean yes,String message){
        if(!yes)throw new AssertionError("ZERO3 V318 "+message);
    }
    static class Result {
        int episodes,decisions,gelFrames,realContactFrames,nearBoxContactFrames;
        int trueHeldFrames,boxEpisodesWithHold,trueBilateralFrames;
        int overThresholdSectorFrames,adhesionAtTouchPatchFrames;
        long totalMotorSectorFrames;
        final int[] episodesByMode=new int[4],heldByMode=new int[4],
                    contactByMode=new int[4],aboveByMode=new int[4];
        final long[] motorFramesByMode=new long[4];
        final double[] physicalWorkByMode=new double[4];
        double worldWork,bodyDisplacement,netAlignment;
        String describe(){
            return String.format(Locale.US,
                "episodes=%d actorIntentDecisions=%d solverSteps=%d "+
                "contactFrames=%d boxContactFrames=%d boxEpisodesWithGrip=%d "+
                "trueHeldFrames=%d bilateralFrames=%d "+
                "motorAboveRealThreshold=%d adhesionAtPhysicalContact=%d "+
                "outOfContactDuty=%.8f boxGripFrames=%d "+
                "work=%.9f netDisplacement=%.6f alignment=%.6f",
                episodes,decisions,gelFrames,realContactFrames,
                nearBoxContactFrames,boxEpisodesWithHold,
                trueHeldFrames,trueBilateralFrames,
                overThresholdSectorFrames,adhesionAtTouchPatchFrames,
                (double)(aboveByMode[FREE]+aboveByMode[FAR]+aboveByMode[DISK])/
                Math.max(1L,motorFramesByMode[FREE]+motorFramesByMode[FAR]+
                         motorFramesByMode[DISK]),
                heldByMode[BOX],worldWork,bodyDisplacement,netAlignment);
        }
    }
    static void life(Zero3LearnedAdhesionV318 head,Result totals,
                     int ordinal,boolean eval){
        int mode=ordinal%4;
        int repetition=ordinal/4;
        long episode=(eval?700000:600000)+ordinal;
        float angle=eval?(float)(-.64+ordinal*.347):(float)(.23+ordinal*.191);
        int condition=eval?(repetition+2)%4:repetition%4;
        // Purely physical initial pose and deformation fixture.
        Zero3MotorImpulseResponseRegression.Scenario s=
            new Zero3MotorImpulseResponseRegression.Scenario(
                (ordinal+2)%4,angle,condition);
        float[] passive=new float[64];
        for(int i=0;i<WARMUP;i++){
            s.gel.step(s.body,s.skin,passive,DT);
            require(s.gel.invalid==0,"invalid warmup material");
        }
        ArrayList<UnknownWorld.Entity> solids=new ArrayList<>();
        UnknownWorld.Entity object=null;
        if(mode!=FREE){
            float distance=mode==FAR?350f:
                eval?(mode==BOX?(float)(46.4+(repetition%4)*.8):
                    (float)(46.7+(repetition%4)*1.2)):
                (mode==BOX?(float)(46.8+(repetition%5)*.75):
                    (float)(47.1+(repetition%4)));
            float bearing=eval?(float)(.09*(repetition%3-1)):
                (float)(.06*(repetition%3-1));
            object=new UnknownWorld.Entity(s.body.cx+distance*(float)Math.cos(bearing),
                s.body.cy+distance*(float)Math.sin(bearing),2,mode==DISK?2:5);
            solids.add(object);
        }
        final UnknownWorld.Entity physicalObject=object;
        Zero3AutonomousLoopV317.Encoder actor=
            new Zero3AutonomousLoopV317.Encoder(0x318FEEDL);
        actor.resetEpisode();
        Zero3LearningFoundation.AdaptiveCortex other48=
            new Zero3LearningFoundation.AdaptiveCortex(0f);
        int thisLifeHold=0;
        float[] lastAction=null;
        try(Zero3MotorIntentV316.Authority authority=
            new Zero3MotorIntentV316.Authority(episode,OWNER)){
            for(int decision=0;decision<DECISIONS;decision++){
                Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
                    episode,decision,s.gel.ticks*DT,true,
                    s.body,s.skin,s.gel,solids,lastAction);
                actor.before(pre);
                float[] chosen=actor.choose();
                Zero3MotorIntentV316 intent=authority.issue(OWNER,decision,
                    0,318,chosen[0],chosen[1],chosen[2],chosen[3],
                    STEPS*DT,.35f);
                require(intent.intact(),"the actor lost intention ownership");
                Zero3LearningFoundation.MotorIntent lower=
                    new Zero3LearningFoundation.MotorIntent(episode,decision,
                        0,chosen[0],chosen[1],chosen[2],chosen[3],STEPS*DT);
                head.begin();
                float oldX=s.body.cx,oldY=s.body.cy,work0=s.gel.work;
                float[] command=null;
                for(int step=0;step<STEPS;step++){
                    command=other48.command(lower,s.body,s.skin,DT).clone();
                    // V3.18 neural output is sole owner of *channel 2*.
                    // This changes real executed controls, never physical
                    // contact/grip flags or COM coordinates.
                    head.actuate(command,intent,s.body,s.skin,solids);
                    int active=0;
                    for(int k=0;k<16;k++)
                        if(command[4*k+2]>Morphology.GRIP_THRESHOLD)active++;
                    totals.overThresholdSectorFrames+=active;
                    totals.aboveByMode[mode]+=active;
                    totals.motorFramesByMode[mode]+=16;
                    totals.totalMotorSectorFrames+=16;
                    float[] sx=s.skin.x.clone(),sy=s.skin.y.clone(),
                            bx=s.body.x.clone(),by=s.body.y.clone();
                    float oldCoreX=s.body.cx,oldCoreY=s.body.cy;
                    s.gel.step(s.body,s.skin,command,DT);
                    Zero3SensoryContractV315Regression.applyRealWorld(
                        mode,s,solids,sx,sy,bx,by,oldCoreX,oldCoreY);
                    totals.gelFrames++;
                    require(s.gel.invalid==0&&
                        Float.isFinite(s.body.cx)&&Float.isFinite(s.body.cy),
                        "true gel became invalid");
                    head.observe(s.skin,physicalObject);
                    boolean anyTouch=false,anyHeld=false;
                    for(int i=0;i<Morphology.POINTS;i++){
                        boolean touch=s.skin.touchSense[i]>.01f||
                                      s.skin.touchPulse[i]>.001f;
                        if(touch&&s.skin.gripCommand[i]>
                           Morphology.GRIP_THRESHOLD)
                            totals.adhesionAtTouchPatchFrames++;
                        if(touch)anyTouch=true;
                        if(s.skin.gripHold[i]>=Morphology.GRIP_HOLD){
                            require(mode==BOX&&physicalObject!=null&&
                                physicalObject.boxGripHold[i]>=Morphology.GRIP_HOLD&&
                                s.skin.gripCommand[i]>Morphology.GRIP_THRESHOLD,
                                "nonphysical or unowned adhesion!");
                            anyHeld=true;
                        }
                    }
                    if(anyTouch){
                        totals.realContactFrames++;
                        totals.contactByMode[mode]++;
                        if(mode==BOX)totals.nearBoxContactFrames++;
                    }
                    if(anyHeld){
                        totals.trueHeldFrames++;thisLifeHold++;
                        totals.heldByMode[mode]++;
                    }
                    if(physicalObject!=null&&physicalObject.isBilateralBox()&&
                       physicalObject.bilateralHeldNow)
                        totals.trueBilateralFrames++;
                }
                head.end();
                Zero3SensoryFrameV315 after=Zero3SensoryFrameV315.capture(
                    episode,decision+1,s.gel.ticks*DT,false,
                    s.body,s.skin,s.gel,solids,command);
                actor.after(after,false); // recurrent model not trained here
                require(pre.intact()&&after.intact()&&
                    after.solverTicks==pre.solverTicks+STEPS,
                    "a purported action did not execute 12 physical ticks");
                totals.netAlignment+=(s.body.cx-oldX)*intent.directionX+
                                     (s.body.cy-oldY)*intent.directionY;
                totals.bodyDisplacement+=Math.hypot(s.body.cx-oldX,s.body.cy-oldY);
                double actualPhysicalWork=Math.max(0,s.gel.work-work0);
                totals.worldWork+=actualPhysicalWork;
                totals.physicalWorkByMode[mode]+=actualPhysicalWork;
                totals.decisions++;
                lastAction=command;
            }
            require(authority.nextDecision()==DECISIONS,
                    "duplicated or skipped actor decision");
        }
        if(mode==BOX&&thisLifeHold>0)totals.boxEpisodesWithHold++;
        totals.episodesByMode[mode]++;
        totals.episodes++;
    }
    static Result run(Zero3LearnedAdhesionV318 motor,int lives,boolean eval){
        Result r=new Result();
        for(int episode=0;episode<lives;episode++)
            life(motor,r,episode,eval);
        require(r.episodes==lives&&r.decisions==lives*DECISIONS,
                "some actor decisions were missing");
        return r;
    }
    public static void main(String[] args){
        Zero3LearnedAdhesionV318 trainable=
            new Zero3LearnedAdhesionV318(0x318ABCDEF012L,true);
        Zero3LearnedAdhesionV318 frozenInitial=trainable.frozenCopy();
        Result train=run(trainable,32,false);
        Zero3LearnedAdhesionV318 learnedEval=trainable.frozenCopy();
        Result learned=run(learnedEval,16,true);
        Result frozen=run(frozenInitial,16,true);
        require(train.decisions==3072&&train.gelFrames==36864&&
                learned.decisions==1536&&learned.gelFrames==18432&&
                frozen.decisions==1536&&frozen.gelFrames==18432,
                "pre-registered workload not completed");
        for(int i=0;i<4;i++){
            require(train.episodesByMode[i]==8&&
                learned.episodesByMode[i]==4&&frozen.episodesByMode[i]==4,
                "incomplete independent matched world families");
            require(learned.heldByMode[i]==0||i==BOX,
                "spurious grip in free/disk/far world");
            require(frozen.heldByMode[i]==0||i==BOX,
                "spurious frozen grip in free/disk/far world");
            System.out.printf(Locale.US,
                "ZERO3 V318 FAMILY=%d learnedTouched=%d frozenTouched=%d "+
                "learnedHeld=%d frozenHeld=%d learnedAboveThreshold=%d "+
                "frozenAboveThreshold=%d learnedWork=%.9f frozenWork=%.9f%n",
                i,learned.contactByMode[i],frozen.contactByMode[i],
                learned.heldByMode[i],frozen.heldByMode[i],
                learned.aboveByMode[i],frozen.aboveByMode[i],
                learned.physicalWorkByMode[i],
                frozen.physicalWorkByMode[i]);
        }
        double outsideDuty=(double)(learned.aboveByMode[FREE]+
            learned.aboveByMode[DISK]+learned.aboveByMode[FAR])/
            Math.max(1L,learned.motorFramesByMode[FREE]+
                learned.motorFramesByMode[DISK]+learned.motorFramesByMode[FAR]);
        boolean trainCovered=train.nearBoxContactFrames>=100&&
            train.trueHeldFrames>0&&trainable.positiveGripUpdates()>0&&
            trainable.learnedL2()>1e-10;
        boolean blindHeld=learned.boxEpisodesWithHold>=3&&
            learned.heldByMode[BOX]>=25&&
            learned.heldByMode[BOX]>=1.25*frozen.heldByMode[BOX];
        // Preregistered scientific cost criterion is BOX-specific;
        // global cost is displayed separately as a diagnostic.
        boolean workOK=learned.physicalWorkByMode[BOX]<=
                       1.2*frozen.physicalWorkByMode[BOX];
        boolean sceneSpecific=outsideDuty<=.30&&
            learned.heldByMode[FREE]==0&&
            learned.heldByMode[DISK]==0&&learned.heldByMode[FAR]==0;
        boolean gate=trainCovered&&blindHeld&&workOK&&sceneSpecific;
        System.out.println("ZERO3 V318 TRAIN "+train.describe());
        System.out.println("ZERO3 V318 BLIND LEARNED "+learned.describe());
        System.out.println("ZERO3 V318 BLIND FROZEN "+frozen.describe());
        System.out.printf(Locale.US,
            "ZERO3 V318 SUMMARY actorChosenTrainDecisions=%d "+
            "trainRealGelSteps=%d blindDecisionsPerArm=%d "+
            "blindRealGelStepsPerArm=%d learnedHeadUpdates=%d "+
            "realGripCreditUpdates=%d learnedHeadDeltaL2=%.10f "+
            "trainPhysicalGripFrames=%d blindPhysicalGripFrames=%d "+
            "frozenPhysicalGripFrames=%d blindBoxLivesWithGrip=%d "+
            "blindOutsideBoxMotorDuty=%.8f "+
            "trainingCoverage=%s blindGripImproved=%s "+
            "blindWorkNotWorse=%s blindSelective=%s "+
            "scientificGripGate=%s noSensorInjection=true "+
            "onlyAdhesionMotorHeadTrained=true "+
            "NO_GAIT G3_BLOCKED G5_BLOCKED NO_OTA%n",
            train.decisions,train.gelFrames,learned.decisions,learned.gelFrames,
            trainable.updates(),trainable.positiveGripUpdates(),
            Math.sqrt(trainable.learnedL2()),
            train.heldByMode[BOX],learned.heldByMode[BOX],
            frozen.heldByMode[BOX],learned.boxEpisodesWithHold,
            outsideDuty,trainCovered,blindHeld,workOK,sceneSpecific,
            gate?"PASS":"FAIL");
    }
}
