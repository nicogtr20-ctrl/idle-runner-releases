package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Locale;

/**
 * V3.16: 2048 real mechanical actions, each with a typed single-owner
 * LABORATORY intention (NOT a learned intent). Full-state durable replay.
 * Crash simulation verifies strict rollback, no invented extra decisions.
 */
public final class Zero3IntentPersistenceV316Regression {
    static final float DT=.025f;
    static final int EPISODES=16,FRAME_COUNT=128,WARMUP=96;
    static final String OWNER="e2-laboratory-controller-not-a-brain";
    private Zero3IntentPersistenceV316Regression(){}
    static void check(boolean pass,String problem){
        if(!pass)throw new AssertionError("ZERO3 V316 "+problem);
    }
    static boolean refused(Runnable f){
        try{f.run();return false;}
        catch(IllegalArgumentException expected){return true;}
    }
    static void authorityAttacks(){
        Zero3MotorIntentV316.Authority authority=
            new Zero3MotorIntentV316.Authority(22,OWNER);
        check(refused(()->new Zero3MotorIntentV316.Authority(22,
            "competing-controller")),"second owner authority created");
        check(refused(()->new Zero3MotorIntentV316.Authority(22,OWNER)),
            "duplicate authority created");
        check(refused(()->authority.issue("competing-controller",0,1,3,
            1,0,.5f,.8f,.025f,.4f)),
            "competing owner accepted");
        check(refused(()->authority.issue(OWNER,0,1,3,
            .7f,.7f,.5f,.8f,.025f,.4f)),
            "nonunit vector accepted");
        check(refused(()->authority.issue(OWNER,0,1,3,
            1,0,Float.NaN,.8f,.025f,.4f)),
            "nonfinite effort accepted");
        check(refused(()->authority.issue(OWNER,1,1,3,
            1,0,.5f,.8f,.025f,.4f)),
            "skipped decision accepted");
        Zero3MotorIntentV316 valid=authority.issue(OWNER,0,1,3,
            1,0,.5f,.8f,.025f,.4f);
        check(valid.intact()&&valid.decisionId==0&&authority.nextDecision()==1,
              "intent authority failed to issue exactly one");
        check(refused(()->authority.issue(OWNER,0,1,3,
            1,0,.5f,.8f,.025f,.4f)),
            "duplicate intent accepted");
        authority.close();
        check(refused(()->authority.issue(OWNER,1,1,3,
            1,0,.5f,.8f,.025f,.4f)),
            "closed authority issued a new intention");
    }
    static final class Evidence {
        int live,gelSteps,checkpoints,holdFrames,diskPulse,solidTouch;
        long lastHash;
        int[] counts=new int[3];
        ArrayList<Zero3ExperienceJournalV316.Record> originals=new ArrayList<>();
    }
    static void verifyRoundTrip(Zero3ExperienceJournalV316 journal,
             List<Zero3ExperienceJournalV316.Record> memory,int length,int generation)
             throws IOException {
        Zero3ExperienceJournalV316 loaded=
            new Zero3ExperienceJournalV316(journal.activePayloadForTest().getParent());
        check(loaded.load()&&loaded.size()==length&&loaded.generation()==generation,
            "durable checkpoint load count/generation wrong");
        List<Zero3ExperienceJournalV316.Record> restored=loaded.records();
        check(restored.size()==memory.size(),"incomplete full-replay recovery");
        for(int k=0;k<length;k++)
            check(restored.get(k).sameData(memory.get(k)) &&
                  restored.get(k).preHash==memory.get(k).preHash &&
                  restored.get(k).postHash==memory.get(k).postHash &&
                  restored.get(k).episodeId==memory.get(k).episodeId &&
                  restored.get(k).decisionId==memory.get(k).decisionId,
                  "lost/corrupted full sensory/actuator fields after disk roundtrip");
    }
    public static void main(String[] args)throws Exception {
        authorityAttacks();
        Path folder=Files.createTempDirectory("zero3-v316-EXPERIMENT-");
        // The directory is per-CI temporary; no Android/profile/save paths.
        Zero3ExperienceJournalV316 journal=new Zero3ExperienceJournalV316(folder);
        Evidence e=new Evidence();
        for(int batch=0;batch<4;batch++)
            for(int mode=0;mode<4;mode++){
                int id=batch*4+mode;long episode=3001L+id;
                Zero3CausalRecorderV315.Split split=
                    Zero3SensoryContractV315Regression.splitForBatch(batch);
                e.counts[split.ordinal()]++;
                Zero3MotorIntentV316.Authority owner=
                    new Zero3MotorIntentV316.Authority(episode,OWNER);
                Zero3CausalRecorderV315 causal=new Zero3CausalRecorderV315(episode,split);
                int seed=Zero3MotorLocalFrameV310Regression.scheduleSeed(
                    316031+batch*1649,id%4,(float)(id*.127),batch,id,1,
                    batch%2==0?1:-1);
                Zero3MotorImpulseResponseRegression.Scenario sc=
                    new Zero3MotorImpulseResponseRegression.Scenario(
                        id%4,(float)(id*.127),batch);
                ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
                for(int k=0;k<WARMUP;k++){
                    float[] cmd=sc.cortex.command(sc.intent,sc.body,sc.skin,DT);
                    Zero3MotorLocalFrameV310Regression.excitation(
                        k,seed,0,1,batch%2==0?1:-1,cmd);
                    sc.gel.step(sc.body,sc.skin,cmd,DT);
                    e.gelSteps++;
                    check(sc.gel.invalid==0,"nonphysical warmup");
                }
                if(mode!=0){
                    float distance=mode==3?350f:mode==1?47.5f:48f;
                    world.add(new UnknownWorld.Entity(sc.body.cx+distance,
                        sc.body.cy,2,mode==2?2:5));
                }
                float[] previous=null;
                for(int step=0;step<FRAME_COUNT;step++){
                    int f=WARMUP+step;
                    Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
                        episode,step,f*(double)DT,true,
                        sc.body,sc.skin,sc.gel,world,previous);
                    causal.begin(pre);
                    // Explicit lab-owned intent: NO brain chooses this.
                    Zero3MotorIntentV316 intent=owner.issue(OWNER,step,1,316,
                        0,0,.7f,batch%2==0?.92f:0f,DT,.35f);
                    float[] cmd=sc.cortex.command(sc.intent,sc.body,sc.skin,DT);
                    Zero3MotorLocalFrameV310Regression.excitation(
                        f,seed,0,1,batch%2==0?1:-1,cmd);
                    for(int sector=0;sector<16;sector++)
                        cmd[sector*4+2]=batch%2==0?.92f:-1f;
                    Zero3CausalRecorderV315.Action action=
                        new Zero3CausalRecorderV315.Action(episode,step,step,1,
                            316,seed,cmd,cmd);
                    float[] sx=sc.skin.x.clone(),sy=sc.skin.y.clone(),
                            bx=sc.body.x.clone(),by=sc.body.y.clone();
                    float cx=sc.body.cx,cy=sc.body.cy;
                    sc.gel.step(sc.body,sc.skin,action.executed64(),DT);
                    e.gelSteps++;
                    Zero3SensoryContractV315Regression.applyRealWorld(
                        mode,sc,world,sx,sy,bx,by,cx,cy);
                    Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
                        episode,step+1,(f+1)*(double)DT,false,
                        sc.body,sc.skin,sc.gel,world,action.executed64());
                    Zero3CausalRecorderV315.Experience lived=causal.commit(action,post);
                    check(lived.intact()&&intent.intact(),
                          "invalid accepted physical experience");
                    journal.add(intent,lived);
                    e.live++;
                    if(post.heldSites()>0)e.holdFrames++;
                    if(post.touchedSites()>0)e.solidTouch++;
                    if(mode==2&&post.pulsedSites()>0)e.diskPulse++;
                    previous=action.executed64();
                    if(step==0){
                        check(refused(()->journal.add(intent,lived)),
                              "duplicate experienced intention accepted");
                    }
                }
                check(owner.nextDecision()==FRAME_COUNT &&
                      causal.size()==FRAME_COUNT,
                      "lost intention or physics decision within episode");
                owner.close();
                int count=id+1;
                if(count%4==0){
                    journal.checkpoint();
                    e.checkpoints++;
                    check(journal.generation()==e.checkpoints &&
                          journal.size()==count*FRAME_COUNT,
                          "failed atomic checkpoint progression");
                    verifyRoundTrip(journal,journal.records(),count*FRAME_COUNT,e.checkpoints);
                }
        }
        check(e.live==2048&&e.gelSteps==3584&&e.checkpoints==4&&
              Arrays.equals(e.counts,new int[]{8,4,4}),
              "preregistered workload not met");
        check(e.solidTouch>0&&e.holdFrames>0&&e.diskPulse>0,
              "no authentic contact/grip/disk response");
        List<Zero3ExperienceJournalV316.Record> all=journal.records();
        check(all.size()==2048,"not all real experiences persisted");
        // Confirm that the returned array cannot alias the stored experience.
        byte[] leaked=all.get(0).encoded();
        leaked[3]^=17;
        check(!Arrays.equals(leaked,all.get(0).encoded()),
              "journal leaked writable raw bytes");
        long tailBefore=all.get(2047).postHash;
        // Directly damage FINAL full-state generation after it committed.
        Path newest=journal.activePayloadForTest();
        try(RandomAccessFile file=new RandomAccessFile(newest.toFile(),"rw")){
            file.seek(Math.max(64,file.length()/2));
            file.writeByte(0xF1);
            file.setLength(file.length()-11);
            file.getFD().sync();
        }
        Zero3ExperienceJournalV316 fallback=new Zero3ExperienceJournalV316(folder);
        check(fallback.load()&&fallback.generation()==3&&fallback.size()==1536&&
              fallback.recoveredOlderGeneration(),
              "corrupt current slot was not rolled back to 1536 committed records");
        for(int i=0;i<1536;i++)
            check(fallback.records().get(i).sameData(all.get(i)),
                  "corruption changed previously confirmed real sensory data");
        // Corrupt the primary pointer as well: the fully-verified previous
        // generation is the only safe fallback, NEVER a raw uncommitted slot.
        Files.write(journal.activeManifestForTest(),new byte[]{0,1,2,3,4});
        Zero3ExperienceJournalV316 manifestFallback=new Zero3ExperienceJournalV316(folder);
        check(manifestFallback.load()&&manifestFallback.generation()==3&&
              manifestFallback.size()==1536,
              "corrupt manifest recovered speculative or missing events");
        Path oldSlot=folder.resolve("zero3-v316-slot0.bin");
        Files.write(oldSlot,new byte[]{9,8,7});
        Zero3ExperienceJournalV316 noValid=new Zero3ExperienceJournalV316(folder);
        check(!noValid.load()&&noValid.size()==0,
              "journal claimed corrupted disk generations were valid");
        System.out.printf(Locale.US,
            "ZERO3 V316 RESULT episodes=16 fit=8 cal=4 blind=4 "+
            "realActions=%d gelSolverSteps=%d checkpoints=%d "+
            "fullSensoryRoundtrips=2048 typedSoleOwner=true "+
            "boxContactFrames=%d genuineGripFrames=%d diskPulseFrames=%d "+
            "finalTailHash=%016x rollbackAfterCorruptNewest=1536 "+
            "rollbackAfterCorruptManifest=1536 bothGenerationsDamagedFailClosed=true%n",
            e.live,e.gelSteps,e.checkpoints,e.solidTouch,e.holdFrames,
            e.diskPulse,tailBefore);
        System.out.println("ZERO3 V316 GATE=PASS durableTwoGenerations=true "+
            "sha256Verified=true atomicMoveRequired=true "+
            "realPhysicalCausality=true noAndroidProfileWrites=true "+
            "autonomousIntentSelection=false learnedMotorPolicy=false "+
            "G3_BLOCKED G5_BLOCKED NO_OTA");
    }
}
