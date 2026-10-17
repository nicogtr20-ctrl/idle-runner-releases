package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/**
 * V3.32 three matched variants using the true production gel+world solver:
 * OLD global perturbation credit; NEW measured sector-local delayed credit;
 * FROZEN. The V317 actor is identical and fixed, V319 pretrained and fixed.
 * No lab heading, COM force, reward lookahead or synthetic contact signal.
 *
 * This is a CLOSED experiment: no changes to the live V325 training path.
 */
public final class Zero3FiveArmAblationV333Regression {
    static final int OLD=0,FULL=1,FROZEN=2,NO_DELAY=3,NO_LOCAL=4,STEPS=12,DECISIONS=96;
    static final float DT=.025f;
    static final String[] FAMILIES={"FREE","BOX_NEAR","DISK_NEAR","BOX_FAR"};
    static final String[] ARMS={"OLD_GLOBAL","FULL_LOCAL_DELAY","FROZEN","NO_DELAY_LOCAL","NO_LOCAL_DELAY"};
    private Zero3TemporalLocalMotorV332Regression(){}
    static void check(boolean yes,String msg){
        if(!yes)throw new AssertionError("V333 "+msg);
    }
    static class EpisodeStats {
        int decisions,realSteps,contacts,held,bilateral,valid,wrongWay,livesWithGrip,censoredLives;
        double signed,gross,work,netX,netY,positiveDistance;
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
        System.out.println("V333 FIXED_GRIP_PRETRAIN decisions="+counts.decisions+
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
                    Zero3MultiSceneRestartV322BRegression.actualWorldStep(
                        p,sx,sy,bx,by,cx,cy);
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
                    if(p.gel.invalid!=0||crossings!=0){
                        // The previous tests counted a physical event as
                        // evidence even if the skin intersects itself. Here
                        // it is a genuine censored, *unfinished* episode,
                        // NEVER a fabricated 96-decision continuation.
                        controller.abort();
                        adhesion.end();
                        result.censoredLives=1;
                        if(heldInLife)result.livesWithGrip=1;
                        System.out.println("V333 PHYSICAL_CENSOR scene="+
                            FAMILIES[family]+" ordinal="+ordinal+
                            " arm="+ARMS[model.arm]+" blind="+blind+
                            " completed="+result.decisions+" sub="+frame+
                            " solverInvalid="+p.gel.invalid+
                            " selfCrossings="+crossings);
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
    public static void main(String[] args){
        Zero3ContextAdhesionV319 grip=gripPretraining();
        Model[] learned=new Model[5];
        for(int a=0;a<5;a++)learned[a]=new Model(a,grip);
        for(int life=320;life<352;life++)
            for(Model model:learned)life(model,life,false);
        for(int a=0;a<5;a++){
            System.out.println("V333 TRAIN ARM="+ARMS[a]+
                " actualWeightUpdates="+learned[a].cortex.updates()+
                " localCreditCalls="+learned[a].causalUpdates);
            check((a==FROZEN)==(learned[a].cortex.updates()==0),
                "failed to validate actual cortex plasticity");
        }
        Model[] eval=new Model[5];
        for(int a=0;a<5;a++){
            eval[a]=new Model(a,grip);
            eval[a].cortex.setWeightsV332ForEvaluation(learned[a].cortex.weights());
        }
        Aggregate[] results=new Aggregate[5];
        for(int a=0;a<5;a++)results[a]=new Aggregate();
        for(int life=640;life<664;life++)
            for(int a=0;a<5;a++)
                results[a].one(life,life(eval[a],life,true));
        int familiesPassed=0,physicallyCompleteFamilies=0;
        for(int fam=0;fam<4;fam++){
            EpisodeStats f=results[FULL].family[fam],
              old=results[OLD].family[fam],z=results[FROZEN].family[fam],
              nd=results[NO_DELAY].family[fam],nl=results[NO_LOCAL].family[fam];
            double max=Math.max(Math.max(old.efficiency(),z.efficiency()),
                                  Math.max(nd.efficiency(),nl.efficiency()));
            boolean directional=f.efficiency()>=max+.10*Math.max(.001,Math.abs(max));
            boolean cost=f.cost()<=old.cost()*1.10&&f.cost()<=z.cost()*1.10;
            boolean complete=true;
            for(int a=0;a<5;a++){
                EpisodeStats e=results[a].family[fam];
                complete&=e.decisions==576&&e.valid==576&&e.censoredLives==0;
                System.out.println("V333 BLIND FAMILY="+FAMILIES[fam]+
                    " ARM="+ARMS[a]+" "+e.summary());
            }
            if(complete)physicallyCompleteFamilies++;
            if(directional&&cost&&complete)familiesPassed++;
            System.out.println("V333 TEST FAMILY="+FAMILIES[fam]+
                " improvedOverFour="+directional+" costWithin10pct="+cost+
                " physicallyComplete="+complete);
        }
        EpisodeStats box=results[FULL].family[1],old=results[OLD].family[1],
                     z=results[FROZEN].family[1],
                     noDelay=results[NO_DELAY].family[1],
                     noLocal=results[NO_LOCAL].family[1];
        boolean boxPositive=box.signed>0;
        boolean boxBest=box.efficiency()>Math.max(Math.max(old.efficiency(),z.efficiency()),
                                    Math.max(noDelay.efficiency(),noLocal.efficiency()));
        boolean bilateral=box.bilateral>0&&
            box.bilateral>=.8*Math.max(old.bilateral,z.bilateral);
        boolean grip=box.livesWithGrip==6&&
            box.held>=.8*Math.max(old.held,z.held)&&
            box.contacts>=.8*Math.max(old.contacts,z.contacts);
        boolean valid=physicallyCompleteFamilies==4;
        for(int a=0;a<5;a++){
            check(eval[a].cortex.updates()==0,"BLIND plasticity leak");
            System.out.println("V333 BLIND TOTAL ARM="+ARMS[a]+
                 " observedDecisions="+results[a].decisions()+
                 " trueSolverSteps="+(12L*results[a].decisions()));
        }
        boolean pass=valid&&familiesPassed>=3&&boxPositive&&boxBest&&bilateral&&grip;
        System.out.println("V333 SUMMARY technicalFiveArmBenchmark=PASS"+
            " preregCandidateGate="+(pass?"PASS_NOT_DEVICE":"FAIL")+
            " fullyValidFamilies="+physicallyCompleteFamilies+"/4"+
            " directionalFamilyWins="+familiesPassed+"/4"+
            " BOXPositive="+boxPositive+" BOXBestOfFive="+boxBest+
            " bilateralGuardrail="+bilateral+" genericGripGuardrail="+grip+
            " BOXBilateralFULL="+box.bilateral+
            " BOXBilateralOLD="+old.bilateral+
            " BOXBilateralFROZEN="+z.bilateral+
            " G3_GENERAL_BLOCKED G5_BLOCKED NO_OTA");
    }
}
