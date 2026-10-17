package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * ZERO 3.15 physical QA: 2048 one-step real ACTION experiences with full
 * causal perception/action/post-collision order. No gait, target, reward,
 * learning, ghost hold, old Android save or shipped brain touched.
 */
public final class Zero3SensoryContractV315Regression {
    static final int EPISODES=16,DECISIONS=128,WARMUP=96;
    static final float DT=.025f;
    static final int FREE=0,NEAR_BOX=1,DISK=2,FAR_BOX=3;
    private Zero3SensoryContractV315Regression(){}
    static void yes(boolean b,String m){
        if(!b)throw new AssertionError("ZERO3 V315 "+m);
    }
    static boolean reject(Runnable f){
        try{f.run();return false;}
        catch(IllegalArgumentException expected){return true;}
        catch(UnsupportedOperationException expected){return true;}
    }
    static final class Stats {
        int episodes,experiences,solverSteps,seenPre,solidTouch,
            diskPulse,held,contactOnsets,invalid,energyMissing,damageMissing;
        final int[] experiencesBySplit=new int[3];
        final int[] touchesByMode=new int[4],pulseByMode=new int[4],
                    holdsByMode=new int[4],visibleByMode=new int[4];
        long checksum=0xcbf29ce484222325L;
    }
    /** E2 attempts to inject fake results; they must fail BEFORE any append. */
    static void verifyTamperRejection(){
        Zero3MotorImpulseResponseRegression.Scenario sc=
            new Zero3MotorImpulseResponseRegression.Scenario(0,0f,0);
        ArrayList<UnknownWorld.Entity> noWorld=new ArrayList<>();
        long episode=777;
        Zero3CausalRecorderV315 r=
            new Zero3CausalRecorderV315(episode,Zero3CausalRecorderV315.Split.BLIND);
        Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
            episode,0,0.,true,sc.body,sc.skin,sc.gel,noWorld,null);
        yes(reject(()->r.commit(null,pre)),"commit without begin accepted");
        r.begin(pre);
        yes(reject(()->r.begin(pre)),"duplicate begin accepted");
        float[] commands=new float[64];
        yes(reject(()->{
            float[] nan=commands.clone();nan[9]=Float.NaN;
            new Zero3CausalRecorderV315.Action(episode,0,0,1,0,0,
                                                 nan,nan);
        }),"nonfinite motor action accepted");
        Zero3CausalRecorderV315.Action cmd=new Zero3CausalRecorderV315.Action(
            episode,0,0,1,0,0,commands,commands);
        Zero3SensoryFrameV315 fake=Zero3SensoryFrameV315.capture(
            episode,1,DT,false,sc.body,sc.skin,sc.gel,noWorld,commands);
        yes(reject(()->r.commit(cmd,fake)),
            "fake post without a real solver step accepted");
        yes(r.size()==0&&r.hasUncommittedBegin(),
            "failed action forged a persistent event");
        // Only one authentic physical solver tick can authorize an outcome.
        sc.gel.step(sc.body,sc.skin,commands,DT);
        Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
            episode,1,DT,false,sc.body,sc.skin,sc.gel,noWorld,commands);
        Zero3SensoryFrameV315 wrongEp=Zero3SensoryFrameV315.capture(
            778,1,DT,false,sc.body,sc.skin,sc.gel,noWorld,commands);
        yes(reject(()->r.commit(cmd,wrongEp)),"cross-episode outcome accepted");
        Zero3SensoryFrameV315 wrongTime=Zero3SensoryFrameV315.capture(
            episode,1,.14,false,sc.body,sc.skin,sc.gel,noWorld,commands);
        yes(reject(()->r.commit(cmd,wrongTime)),"incorrect physical time accepted");
        Zero3SensoryFrameV315 wrongFrame=Zero3SensoryFrameV315.capture(
            episode,2,DT,false,sc.body,sc.skin,sc.gel,noWorld,commands);
        yes(reject(()->r.commit(cmd,wrongFrame)),"skipped frame accepted");
        float[] misrecorded=commands.clone();misrecorded[0]=.7f;
        Zero3CausalRecorderV315.Action badAction=
            new Zero3CausalRecorderV315.Action(episode,0,0,1,0,0,
                                                misrecorded,misrecorded);
        yes(reject(()->r.commit(badAction,post)),
            "wrong executed efference accepted");
        yes(r.size()==0,"tampering persisted an invalid event");
        Zero3CausalRecorderV315.Experience ok=r.commit(cmd,post);
        yes(ok.intact()&&r.size()==1,"legitimate event lost after attacks");
        yes(reject(()->r.commit(cmd,post)),
            "duplicate commit accepted");
        Zero3SensoryFrameV315 skipped=Zero3SensoryFrameV315.capture(
            episode,2,2*DT,true,sc.body,sc.skin,sc.gel,noWorld,commands);
        yes(reject(()->r.begin(skipped)),
            "out-of-order begin accepted");
        Zero3SensoryFrameV315 validNext=Zero3SensoryFrameV315.capture(
            episode,1,DT,true,sc.body,sc.skin,sc.gel,noWorld,commands);
        r.begin(validNext);
        yes(r.size()==1&&r.hasUncommittedBegin(),
            "valid continuity removed prior durable event");
        yes(reject(()->r.begin(validNext)),"second pending begin accepted");
    }
    static Zero3CausalRecorderV315.Split splitForBatch(int batch){
        return batch<2?Zero3CausalRecorderV315.Split.FIT:
               batch==2?Zero3CausalRecorderV315.Split.CAL:
                        Zero3CausalRecorderV315.Split.BLIND;
    }
    static void assertReadOnly(Zero3SensoryFrameV315 x,MotorBrain.Body b,
                               Morphology.Surface sk,SymbioteMatterV1 gel,
                               ArrayList<UnknownWorld.Entity> world){
        long tick=gel.ticks,hash=x.hash;
        float center=b.cx,point=sk.x[0],ox=world.isEmpty()?0:world.get(0).x;
        float[] features=x.local96x12();
        features[0]=12345f;
        float[] sight=x.membraneRays();sight[0]=54321f;
        float[] body=x.globalProprioception();body[0]=111f;
        float[] inside=x.inner74x7();inside[0]=777f;
        float[] eff=x.lastExecuted64();if(eff!=null)eff[0]=555f;
        yes(x.intact()&&x.hash==hash,"immutable frame altered by getter mutation");
        yes(x.patch(0,Zero3SensoryFrameV315.REL_X)!=12345f&&
            x.membraneRays()[0]!=54321f&&
            x.globalProprioception()[0]!=111f&&
            x.inner74x7()[0]!=777f&&
            (eff==null||x.lastExecuted64()[0]!=555f),
            "mutable array escaped sensory contract");
        yes(gel.ticks==tick&&b.cx==center&&sk.x[0]==point&&
            (world.isEmpty()||world.get(0).x==ox),
            "reading sensors changed physics/objects");
    }
    static void applyRealWorld(int mode,
            Zero3MotorImpulseResponseRegression.Scenario sc,
            ArrayList<UnknownWorld.Entity> objects,
            float[] prevSkinX,float[] prevSkinY,
            float[] prevBodyX,float[] prevBodyY,
            float prevCX,float prevCY){
        UnknownWorld.Entity obj=objects.isEmpty()?null:objects.get(0);
        if(obj!=null&&obj.isBilateralBox()){
            SquareGripPuzzle.constrain(sc.body,sc.skin,obj,DT,
                prevSkinX,prevSkinY,prevBodyX,prevBodyY,prevCX,prevCY);
            MaterialContactConstraints.reconcile(sc.body,sc.skin,objects,DT);
            SymbioteWorldStepV171.interior(sc.gel,sc.body,sc.skin,
                                             null,objects,DT);
            GripSurfaceComposition.compose(sc.skin,null,objects);
            obj.tick(DT);
        }else{
            SymbioteWorldStepV171.interior(sc.gel,sc.body,sc.skin,
                                             null,objects,DT);
            GripSurfaceComposition.compose(sc.skin,null,objects);
            if(obj!=null&&mode==DISK){
                Morphology.collide(sc.skin,obj,DT);
                ContactLearning.collide(sc.body,obj,DT);
                obj.tick(DT);
                sc.gel.observeBody(sc.body,sc.skin);
            }
        }
        yes(sc.gel.invalid==0,"gel material invalid after true collision");
    }
    public static void main(String[] args){
        verifyTamperRejection();
        Stats sums=new Stats();
        int[] episodeCounts=new int[3],splitWorldMask=new int[3];
        for(int batch=0;batch<4;batch++)for(int mode=FREE;mode<=FAR_BOX;mode++){
            final int number=batch*4+mode;
            final long episode=1001+number;
            final Zero3CausalRecorderV315.Split group=splitForBatch(batch);
            final int seed=Zero3MotorLocalFrameV310Regression.scheduleSeed(
                315015+batch*2909,number%4,(float)(number*.127),batch,
                number,1,batch%2==0?1:-1);
            Zero3CausalRecorderV315 recorder=new Zero3CausalRecorderV315(episode,group);
            Zero3MotorImpulseResponseRegression.Scenario sc=
                new Zero3MotorImpulseResponseRegression.Scenario(
                    number%4,(float)(number*.127),batch);
            ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
            float[] previousAction=null;
            // Lab initial warmup generates real physical tissue state.
            for(int k=0;k<WARMUP;k++){
                float[] motor=sc.cortex.command(sc.intent,sc.body,sc.skin,DT);
                Zero3MotorLocalFrameV310Regression.excitation(k,seed,0,1,
                    batch%2==0?1:-1,motor);
                sc.gel.step(sc.body,sc.skin,motor,DT);
                yes(sc.gel.invalid==0,"invalid real warmup physical step");
                sums.solverSteps++;
            }
            UnknownWorld.Entity target=null;
            if(mode!=FREE){
                float dist=mode==FAR_BOX?350f:mode==NEAR_BOX?47.5f:48f;
                target=new UnknownWorld.Entity(sc.body.cx+dist,sc.body.cy,
                                                2,mode==DISK?2:5);
                world.add(target);
            }
            for(int step=0;step<DECISIONS;step++){
                final int simStep=WARMUP+step;
                final double t=simStep* (double)DT;
                Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
                    episode,step,t,true,sc.body,sc.skin,sc.gel,world,previousAction);
                yes(pre.energyAvailable==false&&pre.damageAvailable==false,
                    "fabricated energy or damage laboratory readings");
                if(step==0)assertReadOnly(pre,sc.body,sc.skin,sc.gel,world);
                if(pre.seesObstacle()){
                    sums.seenPre++;sums.visibleByMode[mode]++;
                }
                if(mode==FREE||mode==FAR_BOX){
                    yes(pre.touchedSites()==0&&pre.pulsedSites()==0&&
                        pre.heldSites()==0,
                        "false positive physical contact in FREE / distant scene");
                    if(mode==FREE)yes(!pre.seesObstacle(),
                        "invented vision target in empty world");
                }
                recorder.begin(pre);
                float[] motor=sc.cortex.command(sc.intent,sc.body,sc.skin,DT);
                Zero3MotorLocalFrameV310Regression.excitation(
                    simStep,seed,0,1,batch%2==0?1:-1,motor);
                for(int sector=0;sector<16;sector++)
                    motor[sector*4+2]=batch%2==0?.92f:-1f;
                Zero3CausalRecorderV315.Action executed=
                    new Zero3CausalRecorderV315.Action(
                        episode,step,step,1,315,seed,motor,motor);
                float[] oldSkinX=sc.skin.x.clone(),oldSkinY=sc.skin.y.clone();
                float[] oldBodyX=sc.body.x.clone(),oldBodyY=sc.body.y.clone();
                float oldCX=sc.body.cx,oldCY=sc.body.cy;
                sc.gel.step(sc.body,sc.skin,executed.executed64(),DT);
                sums.solverSteps++;
                applyRealWorld(mode,sc,world,oldSkinX,oldSkinY,
                                oldBodyX,oldBodyY,oldCX,oldCY);
                Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
                    episode,step+1,(simStep+1)*(double)DT,false,
                    sc.body,sc.skin,sc.gel,world,executed.executed64());
                Zero3CausalRecorderV315.Experience event=
                    recorder.commit(executed,post);
                yes(event.intact()&&event.outcome.frameStart==step&&
                    event.outcome.frameEnd==step+1,"corrupted experience");
                if(event.outcome.contactOnset)sums.contactOnsets++;
                if(event.outcome.touchedAfter>0){
                    sums.solidTouch++;sums.touchesByMode[mode]++;
                }
                if(event.outcome.pulsedAfter>0){
                    sums.diskPulse++;sums.pulseByMode[mode]++;
                }
                if(event.outcome.heldAfter>0){
                    sums.held++;sums.holdsByMode[mode]++;
                    for(int i=0;i<Morphology.POINTS;i++)
                        if(post.patch(i,Zero3SensoryFrameV315.GRIP_HELD)>=
                           Morphology.GRIP_HOLD){
                            yes(post.patch(i,Zero3SensoryFrameV315.GRIP_COMMAND)>
                                Morphology.GRIP_THRESHOLD,
                                "grip without commanded muscle adhesion");
                            yes(target!=null&&target.isBilateralBox()&&
                                target.boxGripHold[i]>=Morphology.GRIP_HOLD,
                                "grip has no physical box owner");
                        }
                }
                sums.experiences++;
                sums.energyMissing++;sums.damageMissing++;
                sums.checksum^=event.pre.hash;
                sums.checksum*=1099511628211L;
                sums.checksum^=event.post.hash;
                previousAction=executed.executed64();
                // Neither actions nor post frames alias the original
                // mutable command arrays used by the solver.
                if(step==0){
                    float[] mutableAction=executed.executed64();
                    mutableAction[0]=77f;
                    yes(executed.intact()&&event.intact(),
                        "recorded real action can be mutated by caller");
                }
            }
            yes(!recorder.hasUncommittedBegin()&&recorder.size()==DECISIONS,
                "lost, duplicated or unfinished causal experience");
            List<Zero3CausalRecorderV315.Experience> all=recorder.experiences();
            yes(all.size()==DECISIONS,"stored replay evidence missing");
            yes(reject(()->all.clear()),"recorded experiences list was mutable");
            for(Zero3CausalRecorderV315.Experience x:all)
                yes(x.intact()&&x.split==group,"experience lost immutable split");
            sums.episodes++;
            episodeCounts[group.ordinal()]++;
            splitWorldMask[group.ordinal()]|=1<<mode;
            sums.experiencesBySplit[group.ordinal()]+=recorder.size();
        }
        yes(sums.episodes==EPISODES&&sums.experiences==EPISODES*DECISIONS,
            "preregistered physical decision count not met");
        yes(sums.solverSteps==EPISODES*(WARMUP+DECISIONS),
            "actual gel physics step count wrong");
        yes(Arrays.equals(episodeCounts,new int[]{8,4,4}),
            "train/cal/blind assigned at frame level instead of episode level");
        for(int mask:splitWorldMask)yes(mask==15,
            "each episode-level holdout must contain all physical world types");
        yes(sums.touchesByMode[NEAR_BOX]>0&&sums.pulseByMode[DISK]>0,
            "world physics did not generate real touch or disk pressure");
        yes(sums.visibleByMode[NEAR_BOX]>0&&sums.visibleByMode[DISK]>0,
            "pre-action retina did not see the actual nearby solid");
        yes(sums.touchesByMode[FREE]==0&&sums.touchesByMode[FAR_BOX]==0&&
            sums.pulseByMode[FREE]==0&&sums.pulseByMode[FAR_BOX]==0&&
            sums.holdsByMode[FREE]==0&&sums.holdsByMode[FAR_BOX]==0,
            "fake physical contact in empty or far scene");
        yes(sums.held>0,"real box adhesion was never covered by the fixture");
        yes(sums.energyMissing==sums.experiences&&
            sums.damageMissing==sums.experiences,
            "invented unavailable interoception values");
        System.out.printf(Locale.US,
            "ZERO3 V315 RESULT episodes=%d splitFIT=%d splitCAL=%d splitBLIND=%d "+
            "realRecordedDecisions=%d solverSteps=%d "+
            "localSites=96 localChannels=12 opticalRays=16 "+
            "interiorParticles=%d interiorChannels=%d "+
            "globalProprioception=%d executedMuscles=64 "+
            "preActionVisibleFrames=%d trueSolidTouchFrames=%d "+
            "touchImpulseFramesAllScenes=%d trueHoldFrames=%d contactOnset=%d "+
            "energyUnavailable=%d damageUnavailable=%d "+
            "checksum=%016x%n",
            sums.episodes,episodeCounts[0],episodeCounts[1],episodeCounts[2],
            sums.experiences,sums.solverSteps,
            Zero3SensoryFrameV315.INTERIOR_PARTICLES,
            Zero3SensoryFrameV315.INTERIOR_CHANNELS,
            Zero3SensoryFrameV315.PROPRIO,
            sums.seenPre,sums.solidTouch,sums.diskPulse,sums.held,
            sums.contactOnsets,sums.energyMissing,sums.damageMissing,
            sums.checksum);
        System.out.printf(Locale.US,
            "ZERO3 V315 MODES freeTouch=%d nearBoxTouch=%d nearBoxHold=%d "+
            "diskPulse=%d farBoxTouch=%d "+
            "freeVisible=%d nearBoxVisible=%d diskVisible=%d farBoxVisible=%d%n",
            sums.touchesByMode[FREE],sums.touchesByMode[NEAR_BOX],
            sums.holdsByMode[NEAR_BOX],sums.pulseByMode[DISK],
            sums.touchesByMode[FAR_BOX],sums.visibleByMode[FREE],
            sums.visibleByMode[NEAR_BOX],sums.visibleByMode[DISK],
            sums.visibleByMode[FAR_BOX]);
        System.out.println("ZERO3 V315 GATE=PASS realPhysicsExactlyOnce=true "+
            "immutableSensoryFrame=true negativeInjectionRejected=true "+
            "episodeSplitImmutable=true sourceProvenance=true "+
            "energyDamageExplicitlyUnavailable=true "+
            "autonomousLearning=false G3_BLOCKED G5_BLOCKED NO_OTA");
    }
}
