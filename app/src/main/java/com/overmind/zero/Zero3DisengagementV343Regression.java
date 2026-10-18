package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * V3.39 authentic three independently closed-loop arms, V338 joint WORLD:
 * OLD global perturbation credit; NEW measured sector-local delayed credit;
 * FROZEN. The V317 actor is identical and fixed, V319 pretrained and fixed.
 * No lab heading, COM force, reward lookahead or synthetic contact signal.
 *
 * Distinct V317 sensorimotor loops and true V333 cortex updates; NO live OTA.
 */
public final class Zero3DisengagementV343Regression {
    static final int OLD=0,FULL=1,FROZEN=2,NO_DELAY=3,NO_LOCAL=4,STEPS=12,DECISIONS=10000;
    static final float DT=.025f;
    static final String[] FAMILIES={"FREE","BOX_NEAR","DISK_NEAR","BOX_FAR"};
    static final String[] ARMS={"OLD_GLOBAL","FULL_LOCAL_DELAY","FROZEN","NO_DELAY_LOCAL","NO_LOCAL_DELAY"};
    private Zero3DisengagementV343Regression(){}
    static void check(boolean yes,String msg){
        if(!yes)throw new AssertionError("V343 "+msg);
    }
    static class EpisodeStats {
        int decisions,realSteps,contacts,held,bilateral,valid,wrongWay,livesWithGrip,censoredLives;
        double signed,gross,work,netX,netY,positiveDistance;
        double boxGross,boxGrossBilateral,boxGrossNoBilateral,boxNet;
        long boxOwnerFrames,boxMovingFrames,boxMovingWithOwnership;
        float boxStartX,boxStartY,boxEndX,boxEndY;
        long firstBilateral=-1,firstBoxMove=-1;
        // V343: read-only spatial relation, no sensory/actor/reward change.
        double nearest=Double.POSITIVE_INFINITY,farthest=0,lastDistance;
        long nearSteps,nearTransitions,outTransitions,returnTransitions;
        long firstFarStep=-1,firstReturnStep=-1;
        boolean previousNear,seenNear,everLeft;
        long windowNear,windowBilateral,windowOwned;
        double windowBoxTravel;
        void add(EpisodeStats n){
            decisions+=n.decisions;realSteps+=n.realSteps;
            contacts+=n.contacts;held+=n.held;bilateral+=n.bilateral;
            valid+=n.valid;wrongWay+=n.wrongWay;livesWithGrip+=n.livesWithGrip;
            censoredLives+=n.censoredLives;
            signed+=n.signed;gross+=n.gross;work+=n.work;
            netX+=n.netX;netY+=n.netY;positiveDistance+=n.positiveDistance;
        }
        double efficiency(){return signed/(4+gross);}
        double cost(){return work/(1+Math.max(0,signed));}
        String summary(){
            return String.format(Locale.US,
                "decisions=%d physical=%d signed=%.7f gross=%.7f "+
                "efficiency=%.8f work=%.9f workPerUseful=%.9f "+
                "valid=%d wrongWay=%d heldFrames=%d bilateral=%d "+
                "contacts=%d gripLives=%d censoredLives=%d net=%.6f",
                decisions,realSteps,signed,gross,efficiency(),work,cost(),
                valid,wrongWay,held,bilateral,contacts,livesWithGrip,censoredLives,
                Math.hypot(netX,netY));
        }
    }
    static final class Model{
        final Zero3LearningFoundation.AdaptiveCortex cortex;
        final int arm;
        final Zero3ContextAdhesionV319 pretrained;
        int causalUpdates;
        Model(int arm,Zero3ContextAdhesionV319 pretrained){
            this.arm=arm;this.pretrained=pretrained;
            cortex=new Zero3LearningFoundation.AdaptiveCortex(
                arm==FROZEN?0f:.013f);
        }
        Model frozenEvaluation(){
            Model e=new Model(arm,pretrained);
            // The evaluation cold-starts local slow morphology state for ALL arms.
            // This is intentionally a standardized heldout feature distribution.
            e.cortex.restore(cortex.checkpoint());
            return e;
        }
    }
    /** One candidate intervention and its measured sector consequences. */
    static final class Pending {
        final Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe;
        final float[] local;
        Pending(Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe,
                float[] observed){
            this.probe=probe;local=observed.clone();
        }
    }
    static Zero3ContextAdhesionV319 gripPretraining(){
        Zero3ContextAdhesionV319 m=
            new Zero3ContextAdhesionV319(0x31920261017L,true);
        Zero3ContextAdhesionV319Regression.Stats counts=
            new Zero3ContextAdhesionV319Regression.Stats();
        for(int i=0;i<32;i++)
            Zero3ContextAdhesionV319Regression.life(
                Zero3ContextAdhesionV319Regression.modern(m),counts,i,false);
        check(counts.decisions==3072&&m.positiveGripEvents()>0,
              "authentic adhesion pretrained with no real grip");
        System.out.println("V343 FIXED_GRIP_PRETRAIN decisions="+counts.decisions+
            " realHeldFrames="+counts.realHeldFrames+
            " gripCredit="+m.positiveGripEvents());
        return m.frozenCopy();
    }
    static EpisodeStats life(Model model,int ordinal,boolean blind){
        int family=ordinal%4,rep=ordinal/4;
        long episode=(blind?3337000L:3331000L)+ordinal;
        float pose=blind?(float)(-.51+ordinal*.143):
                         (float)(.23+ordinal*.103);
        int condition=(ordinal+3)%4;
        Zero3MotorImpulseResponseRegression.Scenario sc=
            new Zero3MotorImpulseResponseRegression.Scenario(
                (ordinal+3)%4,pose,condition);
        for(int t=0;t<96;t++){
            sc.gel.step(sc.body,sc.skin,new float[64],DT);
            check(sc.gel.invalid==0,"bad warmup");
        }
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        if(family!=0){
            int law=family==2?2:5;
            float range=family==3?280f:
                (float)(46.4+(rep%7)*.51);
            float yaw=(float)((rep%7-3)*.092);
            objects.add(new UnknownWorld.Entity(
                sc.body.cx+range*(float)Math.cos(yaw),
                sc.body.cy+range*(float)Math.sin(yaw),2,law));
        }
        Zero3OrganismStoreV322.Physical p=new Zero3OrganismStoreV322.Physical(
            sc.body,sc.skin,sc.gel,objects);
        Zero3ContextAdhesionV319 adhesion=model.pretrained.frozenCopy();
        Zero3AutonomousLoopV317.Encoder actor=
            new Zero3AutonomousLoopV317.Encoder(0x31720261017L);
        actor.resetEpisode();
        Zero3LearningFoundation.Coordinator controller=
            new Zero3LearningFoundation.Coordinator(model.cortex);
        EpisodeStats result=new EpisodeStats();
        UnknownWorld.Entity trackedBox=firstBox(objects);
        check(trackedBox!=null&&trackedBox.isBilateralBox(),
              "V343 must be real physical BOX");
        result.boxStartX=trackedBox.x;result.boxStartY=trackedBox.y;
        result.previousNear=Math.hypot(p.body.cx-trackedBox.x,
                         p.body.cy-trackedBox.y)<=SquareGripPuzzle.HALF+64f;
        result.seenNear=result.previousNear;
        Pending pending=null;
        boolean heldInLife=false;
        float[] previous=null;
        try(Zero3MotorIntentV316.Authority authority=
            new Zero3MotorIntentV316.Authority(
                episode,Zero3AutonomousLoopV317.OWNER)){
            for(int n=0;n<DECISIONS;n++){
                Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
                    episode,n,p.gel.ticks*(double)DT,true,
                    p.body,p.skin,p.gel,p.world,previous);
                actor.before(pre);
                float[] choice=actor.choose();
                float initialX=p.body.cx,initialY=p.body.cy;
                Zero3MotorIntentV316 motor=
                    authority.issue(Zero3AutonomousLoopV317.OWNER,n,
                        (int)model.cortex.revision(),333,
                        choice[0],choice[1],choice[2],choice[3],
                        STEPS*DT,.35f);
                Zero3LearningFoundation.MotorIntent cortexIntent=
                    new Zero3LearningFoundation.MotorIntent(
                        episode,n,model.cortex.revision(),
                        choice[0],choice[1],choice[2],choice[3],STEPS*DT);
                boolean train=!blind&&model.arm==OLD;
                boolean explore=!blind;
                controller.begin(cortexIntent,p.body,train,!train,explore);
                adhesion.begin();
                float[] evidence=new float[16];
                float[] acted=null;
                float episodeWork=0f,localTravel=0;
                for(int frame=0;frame<STEPS;frame++){
                    float[] motor64=controller.actuate(p.body,p.skin,DT);
                    adhesion.actuate(motor64,motor,p.body,p.skin,objects);
                    float[] act=motor64.clone();
                    float[] sx=p.skin.x.clone(),sy=p.skin.y.clone();
                    float[] bx=p.body.x.clone(),by=p.body.y.clone();
                    float cx=p.body.cx,cy=p.body.cy,priorWork=p.gel.work;
                    float oldBoxX=trackedBox.x,oldBoxY=trackedBox.y;
                    p.gel.step(p.body,p.skin,act,DT);
                    float[] afterGelX=p.skin.x.clone(),
                            afterGelY=p.skin.y.clone();
                    Zero3JointWorldV339.step(
                        p,sx,sy,bx,by,cx,cy,afterGelX,afterGelY);
                    double moved=Math.hypot(trackedBox.x-oldBoxX,
                                            trackedBox.y-oldBoxY);
                    if(moved>1e-4){
                        result.boxGross+=moved;result.boxMovingFrames++;
                        if(result.firstBoxMove<0)
                            result.firstBoxMove=(long)n*STEPS+frame;
                        if(trackedBox.bilateralHeldNow)
                            result.boxGrossBilateral+=moved;
                        else result.boxGrossNoBilateral+=moved;
                    }
                    // True POST WORLD spatial observation. It cannot alter
                    // actor input, cortex/adhesion credit or box mechanics.
                    double distance=Math.hypot(p.body.cx-trackedBox.x,
                                               p.body.cy-trackedBox.y);
                    boolean near=distance<=SquareGripPuzzle.HALF+64f;
                    result.nearest=Math.min(result.nearest,distance);
                    result.farthest=Math.max(result.farthest,distance);
                    result.lastDistance=distance;
                    if(near){result.nearSteps++;result.windowNear++;}
                    if(near!=result.previousNear){
                        result.nearTransitions++;
                        if(!near){
                            result.everLeft=true;result.outTransitions++;
                            if(result.firstFarStep<0)
                                result.firstFarStep=(long)n*STEPS+frame;
                        }else if(result.seenNear&&result.everLeft){
                            result.returnTransitions++;
                            if(result.firstReturnStep<0)
                                result.firstReturnStep=(long)n*STEPS+frame;
                        }
                    }
                    if(near)result.seenNear=true;
                    result.previousNear=near;
                    if(moved>1e-4)result.windowBoxTravel+=moved;
                    if(trackedBox.bilateralHeldNow)
                        result.windowBilateral++;
                    int ownerCount=0;
                    for(int k=0;k<Morphology.POINTS;k++)
                        if(trackedBox.boxGripHold[k]>=Morphology.GRIP_HOLD&&
                           p.skin.gripCommand[k]>Morphology.GRIP_THRESHOLD)
                            ownerCount++;
                    if(ownerCount>0){
                        result.windowOwned++;
                        result.boxOwnerFrames++;
                        if(moved>1e-4)result.boxMovingWithOwnership++;
                    }
                    if(trackedBox.bilateralHeldNow&&result.firstBilateral<0)
                        result.firstBilateral=(long)n*STEPS+frame;
                    result.boxEndX=trackedBox.x;result.boxEndY=trackedBox.y;
                    result.boxNet=Math.hypot(result.boxEndX-result.boxStartX,
                                             result.boxEndY-result.boxStartY);
                    float physicalWork=Math.max(0f,p.gel.work-priorWork);
                    controller.observe(p.body,DT,physicalWork,
                        Math.max(0f,p.gel.lastMuscleReactionImpulse),false,false);
                    adhesion.observe(p.skin,firstBox(objects),physicalWork);
                    result.work+=physicalWork;
                    episodeWork+=physicalWork;
                    result.realSteps++;
                    float comDX=p.body.cx-cx,comDY=p.body.cy-cy;
                    double comAlong=comDX*choice[0]+comDY*choice[1];
                    float weightTotal=1e-6f;
                    float[] channelStrength=new float[16];
                    for(int sector=0;sector<16;sector++){
                        int k=sector*4;
                        channelStrength[sector]=Math.abs(act[k])+
                              Math.abs(act[k+1])+Math.abs(act[k+3]);
                        weightTotal+=channelStrength[sector];
                    }
                    // All inputs below are consequences *already measured*
                    // after the real physics step. Do not treat them as
                    // isolated sector forces or oracle counterfactuals.
                    for(int sector=0;sector<16;sector++){
                        float localX=0,localY=0;
                        int base=sector*6;
                        for(int site=base;site<base+6;site++){
                            localX+=(p.skin.x[site]-sx[site])-comDX;
                            localY+=(p.skin.y[site]-sy[site])-comDY;
                        }
                        float relativeAlong=
                            (localX*choice[0]+localY*choice[1])/6f;
                        float allocatedWork=physicalWork*
                            (channelStrength[sector]/weightTotal);
                        evidence[sector]+=(float)(
                            .5*comAlong/(4f+Math.abs(comDX)+Math.abs(comDY))+
                            .5*relativeAlong/(4f+Math.abs(relativeAlong))-
                            .01*allocatedWork);
                    }
                    boolean contacted=false,held=false;
                    for(int site=0;site<96;site++){
                        if(p.skin.touchSense[site]>.01f||
                           p.skin.touchPulse[site]>.001f)contacted=true;
                        if(p.skin.gripHold[site]>=Morphology.GRIP_HOLD){
                            boolean real=false;
                            for(UnknownWorld.Entity obj:objects)
                                if(obj.isBox()&&
                                   obj.boxGripHold[site]>=Morphology.GRIP_HOLD)
                                    real=true;
                            check(real,"false hold without physical BOX ownership");
                            held=true;
                        }
                    }
                    if(contacted)result.contacts++;
                    if(held){result.held++;heldInLife=true;}
                    UnknownWorld.Entity box=firstBox(objects);
                    if(box!=null&&box.bilateralHeldNow)result.bilateral++;
                    int crossings=MaterialGeometry.selfCrossings(p.skin);
                    if(Zero3JointWorldV339.invalid(p)){
                        // The previous tests counted a physical event as
                        // evidence even if the skin intersects itself. Here
                        // it is a genuine censored, *unfinished* episode,
                        // NEVER a fabricated 96-decision continuation.
                        controller.abort();
                        adhesion.end();
                        result.censoredLives=1;
                        if(heldInLife)result.livesWithGrip=1;
                        System.out.println("V343 PHYSICAL_CENSOR scene="+
                            FAMILIES[family]+" ordinal="+ordinal+
                            " arm="+ARMS[model.arm]+" blind="+blind+
                            " completed="+result.decisions+" sub="+frame+
                            " solverInvalid="+p.gel.invalid+
                            " selfCrossings="+crossings+
                            " jointAttempts="+Zero3JointWorldV339.attempted+
                            " jointUnresolved="+Zero3JointWorldV339.unresolved);
                        return result;
                    }
                    acted=act;
                }
                adhesion.end();
                Zero3LearningFoundation.AdaptiveCortex.LocalProbeV332 probe=null;
                if(model.arm!=OLD&&model.arm!=FROZEN&&!blind)
                    probe=model.cortex.captureProbeV332();
                Zero3LearningFoundation.Experience exp=controller.finish();
                check(exp.frames==STEPS&&exp.physicalEvidence&&
                      exp.intent.decisionId==n,
                      "unphysical or foreign post-action credit");
                if(model.arm!=OLD&&model.arm!=FROZEN&&!blind){
                    // The prior action receives credit only when the NEXT
                    // decision's measured physics exists; 0.82 delay weight.
                    if(pending!=null){
                        model.cortex.learnV333(
                            pending.probe,pending.local,evidence,
                            model.arm==FULL?0:(model.arm==NO_DELAY?1:2));
                        model.causalUpdates++;
                    }
                    pending=new Pending(probe,evidence);
                }
                Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
                    episode,n+1,p.gel.ticks*(double)DT,false,
                    p.body,p.skin,p.gel,p.world,acted);
                check(pre.intact()&&post.intact()&&
                      post.solverTicks==pre.solverTicks+STEPS,
                      "fake PRE/POST time or damaged frame");
                actor.after(post,false);
                float signed=exp.deltaX*choice[0]+exp.deltaY*choice[1];
                result.signed+=signed;
                result.positiveDistance+=Math.max(0,signed);
                result.gross+=exp.gross;
                result.netX+=exp.deltaX;result.netY+=exp.deltaY;
                if(signed<0)result.wrongWay++;
                if(exp.physicalEvidence)result.valid++;
                result.decisions++;
                if(result.decisions%1000==0){
                    System.out.printf(Locale.US,
                        "V343 SPATIAL_WINDOW arm=%s completed=%d "+
                        "distanceNow=%.6f nearestEver=%.6f "+
                        "farthestEver=%.6f nearFramesThis1000=%d "+
                        "bilateralThis1000=%d ownerThis1000=%d "+
                        "boxMoveThis1000=%.6f leaveEvents=%d "+
                        "returnEvents=%d firstFarStep=%d firstReturnStep=%d "+
                        "NO_POLICY_OR_REWARD_CHANGE=true%n",
                        ARMS[model.arm],result.decisions,
                        result.lastDistance,result.nearest,result.farthest,
                        result.windowNear,result.windowBilateral,
                        result.windowOwned,result.windowBoxTravel,
                        result.outTransitions,result.returnTransitions,
                        result.firstFarStep,result.firstReturnStep);
                    result.windowNear=result.windowBilateral=
                        result.windowOwned=0;
                    result.windowBoxTravel=0;
                }
                if(result.decisions%1000==0)
                    System.out.printf(Locale.US,
                        "V343 WINDOW arm=%s ordinal=%d completed=%d "+
                        "physical=%d BOX_xy=%.6f,%.6f BOX_net=%.6f "+
                        "BOX_gross=%.6f BOX_travelDuringBilateral=%.6f "+
                        "BOX_travelWithoutBilateral=%.6f "+
                        "BOX_movingFrames=%d BOX_movingOwned=%d "+
                        "BOX_ownedFrames=%d BOX_bilateralFrames=%d "+
                        "firstBilateral=%d BOX_firstMove=%d "+
                        "RAW_WORK=%.9f signed=%.7f cortexUpdates=%d "+
                        "jointAttempted=%d jointRepaired=%d "+
                        "jointUnresolved=%d NO_RESET_SAME_ORGANISM=true%n",
                        ARMS[model.arm],ordinal,result.decisions,
                        result.realSteps,result.boxEndX,result.boxEndY,
                        result.boxNet,result.boxGross,
                        result.boxGrossBilateral,result.boxGrossNoBilateral,
                        result.boxMovingFrames,result.boxMovingWithOwnership,
                        result.boxOwnerFrames,result.bilateral,
                        result.firstBilateral,result.firstBoxMove,
                        result.work,result.signed,model.cortex.updates(),
                        Zero3JointWorldV339.attempted,
                        Zero3JointWorldV339.repaired,
                        Zero3JointWorldV339.unresolved);
                previous=acted.clone();
            }
            check(authority.nextDecision()==DECISIONS,
                  "duplicate/missing single-owner actor intention");
        }
        if(heldInLife)result.livesWithGrip=1;
        check(result.realSteps==DECISIONS*STEPS&&
              result.decisions==DECISIONS,
              "incomplete 10000 same-life fully physical decisions");
        return result;
    }
    static UnknownWorld.Entity firstBox(ArrayList<UnknownWorld.Entity> world){
        for(UnknownWorld.Entity e:world)if(e.isBox())return e;
        return null;
    }
    static final class Aggregate{
        final EpisodeStats[] family=new EpisodeStats[4];
        Aggregate(){for(int i=0;i<4;i++)family[i]=new EpisodeStats();}
        void one(int id,EpisodeStats s){family[id%4].add(s);}
        long decisions(){long n=0;for(EpisodeStats s:family)n+=s.decisions;return n;}
    }
    static void requireSameReference(Aggregate a,Aggregate b){
        for(int f=0;f<4;f++)check(a.family[f].decisions==b.family[f].decisions,
            "paired scene missing decisions");
    }
    private static void restoreProof(Model src){
        Model recovered=new Model(src.arm,src.pretrained);
        Zero3LearningFoundation.AdaptiveCortex.State saved=src.cortex.checkpoint();
        recovered.cortex.restore(saved);
        float[] a=src.cortex.weights(),b=recovered.cortex.weights();
        check(a.length==b.length,"checkpoint weight count");
        for(int i=0;i<a.length;i++)
            check(Float.floatToIntBits(a[i])==Float.floatToIntBits(b[i]),
                  "checkpoint bitwise weights index "+i);
        check(src.cortex.updates()==recovered.cortex.updates()&&
              src.cortex.revision()==recovered.cortex.revision(),
              "checkpoint update/revision mismatch");
        System.out.println("V343 CORTEX_RESTART arm="+ARMS[src.arm]+
             " weightBitsEqual=true revision="+src.cortex.revision()+
             " updates="+src.cortex.updates()+
             " note=NO_FULL_WORLD_SAVE_CLAIM");
    }
    public static void main(String[] args){
        final int ordinal=1601; // V342 consumed: REPLICATION, NOT BLIND
        Zero3ContextAdhesionV319 pretrained=gripPretraining();
        Model[] models={new Model(FULL,pretrained),new Model(FROZEN,pretrained)};
        for(Model m:models){
            EpisodeStats e=life(m,ordinal,false);
            boolean full=e.decisions==DECISIONS&&
                     e.realSteps==DECISIONS*STEPS&&e.censoredLives==0;
            double expectedGross=m.arm==FULL?13.190067:58.221730;
            long expectedBilateral=m.arm==FULL?1931:7934;
            double expectedWork=m.arm==FULL?5.786657810:4.032796383;
            // Reproducibility gate prevents observing a different cortex
            // and calling a changed trajectory intentional disengagement.
            check(full&&Math.abs(e.boxGross-expectedGross)<.003&&
                  e.bilateral==expectedBilateral&&
                  Math.abs(e.work-expectedWork)<.003,
                  "V343 longlife diagnostic differed from V342 verified data");
            System.out.printf(Locale.US,
              "V343 SPATIAL_RESULT arm=%s decisions=%d "+
              "physicalSteps=%d originalBilateral=%d originalGross=%.6f "+
              "originalRawWork=%.9f distanceAt10000=%.6f "+
              "nearestDistance=%.6f farthestDistance=%.6f "+
              "nearPhysicalFrames=%d exitTransitions=%d "+
              "reentries=%d firstExitStep=%d firstReentryStep=%d "+
              "V342_REPLICATION=PASS consumedSceneOnly=true "+
              "noRewardOrMotorModification=true G3_BLOCKED G5_BLOCKED NO_OTA%n",
              ARMS[m.arm],e.decisions,e.realSteps,e.bilateral,
              e.boxGross,e.work,e.lastDistance,e.nearest,e.farthest,
              e.nearSteps,e.outTransitions,e.returnTransitions,
              e.firstFarStep,e.firstReturnStep);
        }
        System.out.println("V343 DISENGAGEMENT_FORENSIC_CONCLUDED "+
            "NO_CLAIM_OF_VOLUNTARY_CHOICE_OR_VALUE_LEARNING "+
            "SCENE_ALREADY_CONSUMED G3_BLOCKED G5_BLOCKED NO_OTA NO_MERGE");
    }
}
