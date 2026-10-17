package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * V3.19 preregistered three-arm comparison on physical new worlds:
 * A: a contextual trainable adhesion neuron head
 * B: TRAINED V3.18 incumbent
 * C: untrained V3.18 control.
 *
 * Actor/cortex 48 other muscles, real BOX/DISK physics, world inputs, and
 * trajectories are matched; NO exploration or training on BLIND.
 */
public final class Zero3ContextAdhesionV319Regression {
    static final int WARMUP=96,DECISIONS=96,STEPS=12;
    static final float DT=.025f;
    static final int FREE=0,BOX=1,DISK=2,FAR=3;
    static final String OWNER=Zero3AutonomousLoopV317.OWNER;
    private Zero3ContextAdhesionV319Regression(){}
    static void require(boolean good,String problem){
        if(!good)throw new AssertionError("ZERO3 V319 "+problem);
    }
    interface MotorHead {
        void begin();
        void actuate(float[] motor,Zero3MotorIntentV316 intent,
                     MotorBrain.Body body,Morphology.Surface skin,
                     ArrayList<UnknownWorld.Entity> world);
        void observe(Morphology.Surface skin,UnknownWorld.Entity box,
                     float actualGelWorkDelta);
        void finish();
    }
    static MotorHead modern(Zero3ContextAdhesionV319 m){
        return new MotorHead(){
            public void begin(){m.begin();}
            public void actuate(float[] a,Zero3MotorIntentV316 b,
                  MotorBrain.Body c,Morphology.Surface d,
                  ArrayList<UnknownWorld.Entity> w){m.actuate(a,b,c,d,w);}
            public void observe(Morphology.Surface s,UnknownWorld.Entity b,
                  float work){m.observe(s,b,work);}
            public void finish(){m.end();}
        };
    }
    static MotorHead incumbent(Zero3LearnedAdhesionV318 m){
        return new MotorHead(){
            public void begin(){m.begin();}
            public void actuate(float[] a,Zero3MotorIntentV316 b,
                  MotorBrain.Body c,Morphology.Surface d,
                  ArrayList<UnknownWorld.Entity> w){m.actuate(a,b,c,d,w);}
            public void observe(Morphology.Surface s,UnknownWorld.Entity b,
                  float work){m.observe(s,b);}
            public void finish(){m.end();}
        };
    }
    static final class Stats {
        int episodes,decisions,physicalSteps,boxLivesWithGrip;
        int realHeldFrames,bilateralFrames,boxContactFrames,
            realContactFrames,falseGrips,overThreshold,gripCredit;
        final int[] familyLives=new int[4],familyGrip=new int[4],
                    familyAbove=new int[4],familyTouch=new int[4];
        final long[] familyMotorFrames=new long[4];
        final double[] familyWork=new double[4];
        double totalWork,realAdhesionWork,netDisplacement;
        double outsideDuty(){
            long out=familyMotorFrames[FREE]+familyMotorFrames[DISK]+
                     familyMotorFrames[FAR];
            return (double)(familyAbove[FREE]+familyAbove[DISK]+
                            familyAbove[FAR])/Math.max(1L,out);
        }
        String report(){
            return String.format(Locale.US,
                "episodes=%d actorChosenDecisions=%d physicalGelSteps=%d "+
                "nearBoxContactFrames=%d boxLivesWithGrip=%d "+
                "boxRealGripFrames=%d bilateralFrames=%d "+
                "adhesionMotorAboveThreshold=%d outsideBoxDuty=%.8f "+
                "workBox=%.9f workTotal=%.9f "+
                "adhesionWorkFromRealExecutedMotor=%.9f "+
                "distance=%.6f",
                episodes,decisions,physicalSteps,boxContactFrames,
                boxLivesWithGrip,familyGrip[BOX],bilateralFrames,
                overThreshold,outsideDuty(),familyWork[BOX],totalWork,
                realAdhesionWork,netDisplacement);
        }
    }
    static void life(MotorHead head,Stats stat,int ordinal,boolean holdout){
        int family=ordinal%4,rep=ordinal/4;
        long episode=(holdout?990000L:880000L)+ordinal;
        float angle=holdout?(float)(.17+ordinal*.327):
                            (float)(-.51+ordinal*.209);
        int condition=holdout?(rep+1)%4:(rep+2)%4;
        Zero3MotorImpulseResponseRegression.Scenario sc=
            new Zero3MotorImpulseResponseRegression.Scenario(
                (ordinal+3)%4,angle,condition);
        float[] still=new float[64];
        for(int k=0;k<WARMUP;k++){
            sc.gel.step(sc.body,sc.skin,still,DT);
            require(sc.gel.invalid==0,"bad warmup body");
        }
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        UnknownWorld.Entity obstacle=null;
        if(family!=FREE){
            float distance=family==FAR?340f:
                holdout?(family==BOX?(float)(45.3+rep*.72):
                                  (float)(46.2+rep*.93)):
                        (family==BOX?(float)(45.7+(rep%4)*.62):
                                      (float)(46.9+(rep%4)*.61));
            float azimuth=holdout?(float)(.115*(rep%3-1)):
                                  (float)(.075*(rep%3-1));
            obstacle=new UnknownWorld.Entity(
                sc.body.cx+distance*(float)Math.cos(azimuth),
                sc.body.cy+distance*(float)Math.sin(azimuth),
                2,family==DISK?2:5);
            world.add(obstacle);
        }
        final UnknownWorld.Entity box=obstacle;
        // Identical frozen actor and identical unlearned other-48 cortex
        // in all three arms, so V3.19 isolates adhesive head learning.
        Zero3AutonomousLoopV317.Encoder actor=
            new Zero3AutonomousLoopV317.Encoder(0x318FEEDL);
        actor.resetEpisode();
        Zero3LearningFoundation.AdaptiveCortex other48=
            new Zero3LearningFoundation.AdaptiveCortex(0f);
        int thisLifeHold=0;
        float[] last=null;
        try(Zero3MotorIntentV316.Authority authority=
                new Zero3MotorIntentV316.Authority(episode,OWNER)){
            for(int decision=0;decision<DECISIONS;decision++){
                Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
                    episode,decision,sc.gel.ticks*(double)DT,true,
                    sc.body,sc.skin,sc.gel,world,last);
                actor.before(pre);
                float[] selected=actor.choose();
                Zero3MotorIntentV316 intent=authority.issue(OWNER,decision,0,319,
                    selected[0],selected[1],selected[2],selected[3],
                    STEPS*DT,.35f);
                Zero3LearningFoundation.MotorIntent lower=
                    new Zero3LearningFoundation.MotorIntent(
                      episode,decision,0,selected[0],selected[1],selected[2],
                      selected[3],STEPS*DT);
                head.begin();
                float originX=sc.body.cx,originY=sc.body.cy;
                float[] motor=null;
                for(int step=0;step<STEPS;step++){
                    motor=other48.command(lower,sc.body,sc.skin,DT).clone();
                    head.actuate(motor,intent,sc.body,sc.skin,world);
                    float adhesionWork=0;
                    for(int s=0;s<16;s++){
                        float adhesive=motor[4*s+2];
                        require(Float.isFinite(adhesive)&&adhesive>=0&&
                            adhesive<=1.00001f,"neural adhesive output out of bounds");
                        if(adhesive>Morphology.GRIP_THRESHOLD){
                            stat.overThreshold++;
                            stat.familyAbove[family]++;
                        }
                        adhesionWork+=adhesive*
                             Zero3ContextAdhesionV319.ADHESION_WORK_AT_FULL_PER_SECTOR;
                    }
                    stat.familyMotorFrames[family]+=16;
                    float[] sx=sc.skin.x.clone(),sy=sc.skin.y.clone(),
                            bx=sc.body.x.clone(),by=sc.body.y.clone();
                    float oldCX=sc.body.cx,oldCY=sc.body.cy;
                    float beforeWork=sc.gel.work;
                    sc.gel.step(sc.body,sc.skin,motor,DT);
                    Zero3SensoryContractV315Regression.applyRealWorld(
                        family,sc,world,sx,sy,bx,by,oldCX,oldCY);
                    stat.physicalSteps++;
                    require(sc.gel.invalid==0&&Float.isFinite(sc.body.cx)&&
                        Float.isFinite(sc.body.cy),"world physics invalid");
                    float measuredDelta=Math.max(0,sc.gel.work-beforeWork);
                    head.observe(sc.skin,box,measuredDelta);
                    stat.realAdhesionWork+=adhesionWork;
                    stat.totalWork+=measuredDelta;
                    stat.familyWork[family]+=measuredDelta;
                    boolean hold=false,touch=false;
                    for(int i=0;i<Morphology.POINTS;i++){
                        if(sc.skin.touchSense[i]>.01f||sc.skin.touchPulse[i]>.001f)
                            touch=true;
                        if(sc.skin.gripHold[i]>=Morphology.GRIP_HOLD){
                            require(family==BOX&&box!=null&&
                                box.boxGripHold[i]>=Morphology.GRIP_HOLD&&
                                sc.skin.gripCommand[i]>Morphology.GRIP_THRESHOLD,
                                "false simulated grip or missing owner");
                            hold=true;
                        }
                    }
                    if(touch){
                        stat.realContactFrames++;stat.familyTouch[family]++;
                        if(family==BOX)stat.boxContactFrames++;
                    }
                    if(hold){
                        thisLifeHold++;stat.realHeldFrames++;
                        stat.familyGrip[family]++;
                    }
                    if(box!=null&&box.isBilateralBox()&&box.bilateralHeldNow)
                        stat.bilateralFrames++;
                }
                head.finish();
                Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
                    episode,decision+1,sc.gel.ticks*(double)DT,false,
                    sc.body,sc.skin,sc.gel,world,motor);
                require(pre.intact()&&post.intact()&&
                    post.solverTicks==pre.solverTicks+STEPS,
                    "fake body progression");
                actor.after(post,false);
                stat.netDisplacement+=Math.hypot(sc.body.cx-originX,
                                                  sc.body.cy-originY);
                last=motor;stat.decisions++;
            }
            require(authority.nextDecision()==DECISIONS,
                    "actor decision owner missing or duplicated");
        }
        if(family==BOX&&thisLifeHold>0)stat.boxLivesWithGrip++;
        stat.familyLives[family]++;stat.episodes++;
    }
    static Stats run(MotorHead head,int episodes,boolean holdout){
        Stats s=new Stats();
        for(int id=0;id<episodes;id++)life(head,s,id,holdout);
        require(s.episodes==episodes&&s.decisions==episodes*DECISIONS,
                "missing preregistered decisions");
        return s;
    }
    public static void main(String[] argv){
        Zero3ContextAdhesionV319 modernTrain=
            new Zero3ContextAdhesionV319(0x31920261017L,true);
        Stats train=run(modern(modernTrain),32,false);
        require(train.decisions==3072&&train.physicalSteps==36864,
                "TRAIN preregistered solver decisions did not finish");
        // Incumbent is trained ONLY on its OLD V3.18 training universe.
        // The new V3.19 holdout cannot influence any learned weights.
        Zero3LearnedAdhesionV318 oldTrain=
            new Zero3LearnedAdhesionV318(0x318ABCDEF012L,true);
        Zero3LearnedAdhesionV318Regression.run(oldTrain,32,false);
        Zero3LearnedAdhesionV318 oldTrained=oldTrain.frozenCopy();
        Zero3LearnedAdhesionV318 oldOriginal=
            new Zero3LearnedAdhesionV318(0x318ABCDEF012L,false);
        Stats fresh=run(modern(modernTrain.frozenCopy()),24,true);
        Stats incumbent=run(incumbent(oldTrained),24,true);
        Stats original=run(incumbent(oldOriginal),24,true);
        require(fresh.decisions==2304&&incumbent.decisions==2304&&
            original.decisions==2304&&fresh.physicalSteps==27648&&
            incumbent.physicalSteps==27648&&original.physicalSteps==27648,
            "missing actual blind physics");
        for(int f=0;f<4;f++){
            require(train.familyLives[f]==8&&fresh.familyLives[f]==6&&
                incumbent.familyLives[f]==6&&original.familyLives[f]==6,
                "not all families represented fairly");
            require(f==BOX||fresh.familyGrip[f]==0,
                "illicit hold in non-grippable scene");
            System.out.printf(Locale.US,
                "ZERO3 V319 FAMILY=%d gripNew=%d gripOld=%d gripFrozen=%d "+
                "contactNew=%d contactOld=%d "+
                "aboveNew=%d aboveOld=%d workNew=%.9f workOld=%.9f%n",
                f,fresh.familyGrip[f],incumbent.familyGrip[f],original.familyGrip[f],
                fresh.familyTouch[f],incumbent.familyTouch[f],
                fresh.familyAbove[f],incumbent.familyAbove[f],
                fresh.familyWork[f],incumbent.familyWork[f]);
        }
        double gripFloor=Math.max(100.,.60*incumbent.familyGrip[BOX]);
        double workPerGripNew=fresh.familyWork[BOX]/(1+fresh.familyGrip[BOX]),
               workPerGripOld=incumbent.familyWork[BOX]/(1+incumbent.familyGrip[BOX]);
        boolean retained=fresh.boxLivesWithGrip>=4&&
            fresh.familyGrip[BOX]>=gripFloor;
        boolean selective=fresh.outsideDuty()<=.25&&
            fresh.outsideDuty()<=.70*incumbent.outsideDuty();
        boolean workOK=fresh.familyWork[BOX]<=incumbent.familyWork[BOX]&&
                       fresh.totalWork<=incumbent.totalWork;
        boolean efficiency=workPerGripNew<=.85*workPerGripOld;
        boolean valid=fresh.boxContactFrames>=.10*
                (6*DECISIONS*STEPS)&&
            fresh.familyGrip[FREE]==0&&fresh.familyGrip[DISK]==0&&
            fresh.familyGrip[FAR]==0;
        boolean learned=modernTrain.updates()==3072&&
            modernTrain.positiveGripEvents()>0&&
            modernTrain.weightChangesL2()>1e-9;
        boolean gate=retained&&selective&&workOK&&efficiency&&valid&&learned;
        System.out.println("ZERO3 V319 TRAIN "+train.report());
        System.out.println("ZERO3 V319 BLIND NEW "+fresh.report());
        System.out.println("ZERO3 V319 BLIND INCUMBENT "+incumbent.report());
        System.out.println("ZERO3 V319 BLIND UNTRAINED "+original.report());
        System.out.printf(Locale.US,
            "ZERO3 V319 SUMMARY trainActorChosen=%d "+
            "trainTrueSolverSteps=%d blindEachArmActorChoices=%d "+
            "blindTrueSolverStepsPerArm=%d learnedUpdates=%d "+
            "positiveHeldCredits=%d cumulativeUpdateL2=%.9f "+
            "boxGripNew=%d boxGripOld=%d boxLivesNew=%d "+
            "outsideDutyNew=%.8f outsideDutyOld=%.8f "+
            "physicalBoxWorkNew=%.9f physicalBoxWorkOld=%.9f "+
            "workPerGripNew=%.12f workPerGripOld=%.12f "+
            "gripRetained=%s selective=%s workNotWorse=%s "+
            "efficient=%s realCoverage=%s actualLearning=%s "+
            "scientificGripGate=%s learningNeverSeesObjectIdentity=true "+
            "physicsUnchanged=true onlyMotorAdhesionHeadTrained=true "+
            "NO_GAIT G3_BLOCKED G5_BLOCKED NO_OTA%n",
            train.decisions,train.physicalSteps,fresh.decisions,
            fresh.physicalSteps,modernTrain.updates(),
            modernTrain.positiveGripEvents(),modernTrain.weightChangesL2(),
            fresh.familyGrip[BOX],incumbent.familyGrip[BOX],
            fresh.boxLivesWithGrip,fresh.outsideDuty(),incumbent.outsideDuty(),
            fresh.familyWork[BOX],incumbent.familyWork[BOX],
            workPerGripNew,workPerGripOld,retained,selective,workOK,
            efficiency,valid,learned,gate?"PASS":"FAIL");
    }
}
