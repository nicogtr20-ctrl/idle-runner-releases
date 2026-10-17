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
public final class Zero3CoherenceAblationV341Regression {
    static final int OLD=0,FULL=1,FROZEN=2,NO_DELAY=3,NO_LOCAL=4,COHERENT=5,STEPS=12,DECISIONS=96;
    static final float DT=.025f;
    static final String[] FAMILIES={"FREE","BOX_NEAR","DISK_NEAR","BOX_FAR"};
    static final String[] ARMS={"OLD_GLOBAL","FULL_LOCAL_DELAY","FROZEN","NO_DELAY_LOCAL","NO_LOCAL_DELAY","LOCAL_COHERENCE"};
    private Zero3CoherenceAblationV341Regression(){}
    static void check(boolean yes,String msg){
        if(!yes)throw new AssertionError("V341 "+msg);
    }
    static class EpisodeStats {
        int decisions,realSteps,contacts,held,bilateral,geometricOpp,valid,wrongWay,livesWithGrip,censoredLives;
        double signed,gross,work,netX,netY,positiveDistance;
        void add(EpisodeStats n){
            decisions+=n.decisions;realSteps+=n.realSteps;
            contacts+=n.contacts;held+=n.held;bilateral+=n.bilateral;geometricOpp+=n.geometricOpp;
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
                "valid=%d wrongWay=%d heldFrames=%d bilateral=%d geometryOpp=%d "+
                "contacts=%d gripLives=%d censoredLives=%d net=%.6f",
                decisions,realSteps,signed,gross,efficiency(),work,cost(),
                valid,wrongWay,held,bilateral,geometricOpp,contacts,livesWithGrip,censoredLives,
                Math.hypot(netX,netY));
        }
    }
    static final class Model{
        final Zero3LearningFoundation.AdaptiveCortex cortex;
        final int arm;
        final Zero3ContextAdhesionV319 pretrained;
        int causalUpdates;
        double observedCoherencePenalty;
        long coherenceCount;
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
        System.out.println("V341 FIXED_GRIP_PRETRAIN decisions="+counts.decisions+
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
                    p.gel.step(p.body,p.skin,act,DT);
                    float[] afterGelX=p.skin.x.clone(),
                            afterGelY=p.skin.y.clone();
                    Zero3JointWorldV339.step(
                        p,sx,sy,bx,by,cx,cy,afterGelX,afterGelY);
                    float physicalWork=Math.max(0f,p.gel.work-priorWork);
                    controller.observe(p.body,DT,physicalWork,
                        Math.max(0f,p.gel.lastMuscleReactionImpulse),false,false);
                    adhesion.observe(p.skin,firstBox(objects),physicalWork);
                    result.work+=physicalWork;
                    episodeWork+=physicalWork;
                    result.realSteps++;
                    float comDX=p.body.cx-cx,comDY=p.body.cy-cy;
                    double comAlong=comDX*choice[0]+comDY*choice[1];
                    float[] localMoveX=new float[16],
                            localMoveY=new float[16];
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
                        localMoveX[sector]=localX/6f;
                        localMoveY[sector]=localY/6f;
                        float relativeAlong=
                            (localX*choice[0]+localY*choice[1])/6f;
                        float allocatedWork=physicalWork*
                            (channelStrength[sector]/weightTotal);
                        evidence[sector]+=(float)(
                            .5*comAlong/(4f+Math.abs(comDX)+Math.abs(comDY))+
                            .5*relativeAlong/(4f+Math.abs(relativeAlong))-
                            .01*allocatedWork);
                    }
                    // V341 ONLY intervention, solely in TRAIN and only
                    // for independently learning arm COHERENT. The penalty
                    // comes from real measured post-world local membrane
                    // motion, with NO BOX location, adhesion timer, oracle,
                    // pre-scripted gait or direct command override.
                    if(!blind&&model.arm==COHERENT){
                        for(int q=0;q<16;q++){
                            int l=(q+15)%16,r=(q+1)%16;
                            double diffX=localMoveX[q]-
                                .5*(localMoveX[l]+localMoveX[r]);
                            double diffY=localMoveY[q]-
                                .5*(localMoveY[l]+localMoveY[r]);
                            double norm=Math.hypot(localMoveX[q],localMoveY[q]);
                            float penalty=(float)Math.min(1.,
                                Math.hypot(diffX,diffY)/(4.+norm));
                            check(Float.isFinite(penalty)&&penalty>=0f,
                                "nonfinite proprioceptive coherence");
                            evidence[q]-=.12f*penalty;
                            model.observedCoherencePenalty+=.12*penalty;
                            model.coherenceCount++;
                        }
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
                    if(box!=null){
                        if(box.bilateralHeldNow)result.bilateral++;
                        boolean[] sides=new boolean[4];
                        for(int site=0;site<96;site++){
                            float dx=p.skin.x[site]-box.x,
                                  dy=p.skin.y[site]-box.y;
                            float h=SquareGripPuzzle.HALF;
                            boolean xf=Math.abs(Math.abs(dx)-h)<=4f&&
                                       Math.abs(dy)<=h+4f;
                            boolean yf=Math.abs(Math.abs(dy)-h)<=4f&&
                                       Math.abs(dx)<=h+4f;
                            if(xf||yf){
                                float[] near={Math.abs(dx+h),
                                     Math.abs(dx-h),Math.abs(dy+h),
                                     Math.abs(dy-h)};
                                int f=0;
                                for(int u=1;u<4;u++)
                                    if(near[u]<near[f])f=u;
                                sides[f]=true;
                            }
                        }
                        if((sides[0]&&sides[1])||
                           (sides[2]&&sides[3]))
                            result.geometricOpp++;
                    }
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
                        System.out.println("V341 PHYSICAL_CENSOR scene="+
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
                            model.arm==FULL||model.arm==COHERENT?0:(model.arm==NO_DELAY?1:2));
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
                previous=acted.clone();
            }
            check(authority.nextDecision()==DECISIONS,
                  "duplicate/missing single-owner actor intention");
        }
        if(heldInLife)result.livesWithGrip=1;
        check(result.realSteps==DECISIONS*STEPS&&
              result.decisions==DECISIONS,
              "incomplete 96 fully physical decisions");
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
        System.out.println("V341 CORTEX_RESTART arm="+ARMS[src.arm]+
             " weightBitsEqual=true revision="+src.cortex.revision()+
             " updates="+src.cortex.updates()+
             " note=NO_FULL_WORLD_SAVE_CLAIM");
    }
    public static void main(String[] args){
        Zero3ContextAdhesionV319 grip=gripPretraining();
        final int[] selected={OLD,FULL,FROZEN,COHERENT};
        Model[] trained=new Model[4];
        for(int i=0;i<4;i++)trained[i]=new Model(selected[i],grip);
        long trainDecisions=0;
        for(int ordinal=1240;ordinal<1272;ordinal++)
            for(Model m:trained){
                EpisodeStats episode=life(m,ordinal,false);
                trainDecisions+=episode.decisions;
                if(episode.censoredLives>0)
                    System.out.println("V341 TRAIN_CENSORED ordinal="+
                        ordinal+" arm="+ARMS[m.arm]+" "+episode.summary());
            }
        for(Model m:trained){
            System.out.printf(Locale.US,
                "V341 TRAIN_SUMMARY arm=%s updates=%d "+
                "localCreditEvents=%d coherenceCount=%d "+
                "coherencePenaltySum=%.8f%n",
                ARMS[m.arm],m.cortex.updates(),m.causalUpdates,
                m.coherenceCount,m.observedCoherencePenalty);
            check((m.arm==FROZEN&&m.cortex.updates()==0)||
                  (m.arm!=FROZEN&&m.cortex.updates()>0),
                  "invalid V341 learning/frozen update count");
        }
        check(trained[3].coherenceCount>0&&
              trained[3].observedCoherencePenalty>0&&
              trained[1].coherenceCount==0,
              "V341 candidate penalty not uniquely active");
        Model[] eval=new Model[4];
        for(int i=0;i<4;i++){
            eval[i]=new Model(selected[i],grip);
            eval[i].cortex.setWeightsV332ForEvaluation(
                        trained[i].cortex.weights());
        }
        Aggregate[] scores=new Aggregate[4];
        for(int i=0;i<4;i++)scores[i]=new Aggregate();
        for(int ordinal=1320;ordinal<1344;ordinal++)
            for(int i=0;i<4;i++){
                EpisodeStats episode=life(eval[i],ordinal,true);
                scores[i].one(ordinal,episode);
                if(episode.censoredLives>0)
                    System.out.println("V341 BLIND_CENSORED ordinal="+
                        ordinal+" arm="+ARMS[eval[i].arm]+" "+
                        episode.summary());
            }
        int physicalFamilies=0,effWins=0,validWins=0;
        for(int f=0;f<4;f++){
            EpisodeStats c=scores[3].family[f],full=scores[1].family[f],
                         old=scores[0].family[f],frozen=scores[2].family[f];
            double best=Math.max(full.efficiency(),
                        Math.max(old.efficiency(),frozen.efficiency()));
            boolean gait=c.efficiency()>=best+
                  .10*Math.max(.001,Math.abs(best));
            // STRICT raw (un-normalized) work cap, as pre-registered.
            boolean raw=c.work<=1.10*full.work+1e-6&&
                        c.work<=1.10*old.work+1e-6&&
                        c.work<=1.10*frozen.work+1e-6;
            boolean physics=true;
            for(int i=0;i<4;i++){
                EpisodeStats e=scores[i].family[f];
                physics&=e.decisions==6*DECISIONS&&
                    e.valid==6*DECISIONS&&
                    e.realSteps==6*DECISIONS*STEPS&&
                    e.censoredLives==0;
                System.out.println("V341 BLIND family="+FAMILIES[f]+
                    " arm="+ARMS[selected[i]]+" "+e.summary());
            }
            if(physics)physicalFamilies++;
            if(gait&&physics)effWins++;
            if(gait&&raw&&physics)validWins++;
            System.out.printf(Locale.US,
                "V341 FAMILY_GATE family=%s directional10pct=%s "+
                "RAW_WORK_110_PERCENT=%s complete=%s "+
                "FULL_RAW=%.9f OLD_RAW=%.9f FROZEN_RAW=%.9f "+
                "COHERENT_RAW=%.9f "+
                "FULL_NORMALIZED=%.9f COHERENT_NORMALIZED=%.9f "+
                "COHERENT_OPP_GEOMETRY=%d COHERENT_BILATERAL=%d%n",
                FAMILIES[f],gait,raw,physics,
                full.work,old.work,frozen.work,c.work,full.cost(),c.cost(),
                c.geometricOpp,c.bilateral);
        }
        EpisodeStats c=scores[3].family[1],full=scores[1].family[1],
                     old=scores[0].family[1],frozen=scores[2].family[1];
        boolean boxDirection=c.signed>0&&c.efficiency()>
            Math.max(full.efficiency(),Math.max(old.efficiency(),
                                               frozen.efficiency()));
        int maxBilateral=Math.max(full.bilateral,
                         Math.max(old.bilateral,frozen.bilateral));
        boolean realBilateral=maxBilateral>0&&c.bilateral>0&&
                     c.bilateral>=.8*maxBilateral&&c.geometricOpp>0;
        int maxHeld=Math.max(full.held,
                    Math.max(old.held,frozen.held));
        int maxContacts=Math.max(full.contacts,
                    Math.max(old.contacts,frozen.contacts));
        boolean gripTrue=c.livesWithGrip>=5&&c.held>=.8*maxHeld&&
                         c.contacts>=.8*maxContacts;
        boolean blindCounts=true;
        for(int i=0;i<4;i++){
            check(eval[i].cortex.updates()==0,
                  "BLIND cortex must have exactly zero updates");
            long decisions=scores[i].decisions();
            blindCounts&=decisions==24*DECISIONS;
            System.out.println("V341 BLIND_TOTAL arm="+ARMS[selected[i]]+
                " independentDecisions="+decisions+
                " physicalSubsteps="+(scores[i].family[0].realSteps+
                  scores[i].family[1].realSteps+
                  scores[i].family[2].realSteps+
                  scores[i].family[3].realSteps)+
                " cortexUpdates=0");
        }
        boolean gate=physicalFamilies==4&&validWins>=3&&
            boxDirection&&realBilateral&&gripTrue&&blindCounts&&
            Zero3JointWorldV339.unresolved==0&&trainDecisions>=4*2000;
        System.out.printf(Locale.US,
            "V341 SCIENCE_FINAL G3=%s physicalFamilies=%d/4 "+
            "directionalWins=%d/4 directionalPlusRAWwins=%d/4 "+
            "BOX_positiveBetter=%s BOX_trueBilateral=%s "+
            "BOX_geometry=%d COHERENT_bilateral=%d "+
            "FULL_bilateral=%d OLD_bilateral=%d FROZEN_bilateral=%d "+
            "trueGrip=%s BLIND_allAutonomous=%s "+
            "realTrainDecisionsAll=%d "+
            "coherencePenalty=%.8f "+
            "jointAttempts=%d jointRepaired=%d jointUnresolved=%d "+
            "G5_BLOCKED NO_OTA NO_MERGE%n",
            gate?"SCREEN_PASS_NOT_RELEASE":"BLOCKED_FAIL",
            physicalFamilies,effWins,validWins,boxDirection,realBilateral,
            c.geometricOpp,c.bilateral,full.bilateral,old.bilateral,
            frozen.bilateral,gripTrue,blindCounts,trainDecisions,
            trained[3].observedCoherencePenalty,
            Zero3JointWorldV339.attempted,
            Zero3JointWorldV339.repaired,Zero3JointWorldV339.unresolved);
    }
}
