package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Locale;

/**
 * V3.22 independent JVM processes: true 96-patch/74-inner gel and rigid box,
 * 64 actuator commands per physical substep, same-life restart at decision 48.
 * The test never assigns a gait, contacts, gripHold or reward.
 */
public final class Zero3SameLifeRestartV322Regression {
    static final float DT=.025f;
    static final int STEP=12,WARMUP=96,END=96;
    static final int ORDINAL=5,MODE=1;
    static final long EPISODE=950005L, WORLD_RNG=0x332220261017L;
    static final String OWNER=Zero3AutonomousLoopV317.OWNER;
    private Zero3SameLifeRestartV322Regression(){}
    static void require(boolean v,String msg){
        if(!v)throw new AssertionError("ZERO3 V322 "+msg);
    }
    static class Brain {
        final Zero3AutonomousLoopV317.Encoder e=
            new Zero3AutonomousLoopV317.Encoder(0x31720261017L);
        final Zero3LearningFoundation.AdaptiveCortex c=
            new Zero3LearningFoundation.AdaptiveCortex(.013f);
        final Zero3ContextAdhesionV319 h=
            new Zero3ContextAdhesionV319(0x31920261017L,true);
    }
    static Zero3OrganismStoreV322.Physical initialWorld(){
        int rep=ORDINAL/4;
        float angle=(float)(-.73+ORDINAL*.219);
        int condition=(rep+1)%4;
        Zero3MotorImpulseResponseRegression.Scenario scene=
            new Zero3MotorImpulseResponseRegression.Scenario(
                (ORDINAL+1)%4,angle,condition);
        float[] silent=new float[64];
        for(int k=0;k<WARMUP;k++){
            scene.gel.step(scene.body,scene.skin,silent,DT);
            require(scene.gel.invalid==0,"warmup invalid");
        }
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        float gap=(float)(45.3+(rep%6)*.69);
        float yaw=(float)(.06*(rep%3-1));
        world.add(new UnknownWorld.Entity(
            scene.body.cx+gap*(float)Math.cos(yaw),
            scene.body.cy+gap*(float)Math.sin(yaw),2,5));
        return new Zero3OrganismStoreV322.Physical(
            scene.body,scene.skin,scene.gel,world);
    }
    static void actualWorldStep(Zero3OrganismStoreV322.Physical p,
            float[] oldSkinX,float[] oldSkinY,float[] oldBodyX,
            float[] oldBodyY,float cx,float cy){
        UnknownWorld.Entity obj=p.world.get(0);
        SquareGripPuzzle.constrain(p.body,p.skin,obj,DT,
            oldSkinX,oldSkinY,oldBodyX,oldBodyY,cx,cy);
        MaterialContactConstraints.reconcile(p.body,p.skin,p.world,DT);
        SymbioteWorldStepV171.interior(p.gel,p.body,p.skin,null,p.world,DT);
        GripSurfaceComposition.compose(p.skin,null,p.world);
        obj.tick(DT);
        require(p.gel.invalid==0,"invalid real world after collision");
    }
    static class Session {
        final Brain brain;
        final Zero3OrganismStoreV322.Physical p;
        final ArrayList<Zero3OrganismStoreV322.Trace> tape;
        float[] last;
        int next;
        Session(Brain b,Zero3OrganismStoreV322.Physical p,
                int next,Zero3OrganismStoreV322.Trace[] old,float[] last){
            this.brain=b;this.p=p;this.next=next;
            tape=new ArrayList<>(Arrays.asList(old));
            this.last=last==null?null:last.clone();
        }
        Zero3OrganismStoreV322.State save()throws IOException {
            require(!tape.isEmpty()&&next==tape.size(),"missing traces");
            return Zero3OrganismStoreV322.capture(
                EPISODE,next,WORLD_RNG,brain.e,brain.c,brain.h,
                p.body,p.skin,p.gel,p.world,last,tape);
        }
    }
    static Session virgin(){
        Brain b=new Brain();b.e.resetEpisode();
        return new Session(b,initialWorld(),0,
             new Zero3OrganismStoreV322.Trace[0],null);
    }
    static Session recovered(Zero3OrganismStoreV322.State state)throws IOException{
        require(state!=null&&state.episodeId==EPISODE&&
            state.lastRngState==WORLD_RNG,"wrong world/actor lineage");
        Brain b=new Brain();b.e.resetEpisode();
        // All physical data has already been decoded into fresh material objects.
        Zero3OrganismStoreV322.Physical p=state.physical();
        state.restoreModules(b.e,b.c,b.h);
        require(b.e.updates()==state.nextDecision&&
                b.c.updates()<=state.nextDecision&&
                b.h.updates()==state.nextDecision,
                "neural checkpoint not consistent with recovered authority");
        return new Session(b,p,state.nextDecision,state.records,state.last64());
    }
    static void run(Session s,int until) {
        require(until>=s.next&&until<=END,"bad upper bound");
        Brain brain=s.brain;Zero3OrganismStoreV322.Physical p=s.p;
        Zero3LearningFoundation.Coordinator control=
            new Zero3LearningFoundation.Coordinator(brain.c);
        try(Zero3MotorIntentV316.Authority authority=
            new Zero3MotorIntentV316.Authority(EPISODE,OWNER)){
            if(s.next>0)authority.restoreCompletedDecisions(s.next);
            for(int n=s.next;n<until;n++){
                Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
                    EPISODE,n,p.gel.ticks*(double)DT,true,
                    p.body,p.skin,p.gel,p.world,s.last);
                brain.e.before(pre);
                float[] chosen=brain.e.choose();
                Zero3MotorIntentV316 intent=authority.issue(OWNER,n,
                    (int)brain.c.revision(),322,chosen[0],chosen[1],
                    chosen[2],chosen[3],STEP*DT,.35f);
                Zero3LearningFoundation.MotorIntent motor=
                    new Zero3LearningFoundation.MotorIntent(EPISODE,n,
                        brain.c.revision(),chosen[0],chosen[1],
                        chosen[2],chosen[3],STEP*DT);
                control.begin(motor,p.body,true,false,true);
                brain.h.begin();
                long actionTape=0xcbf29ce484222325L;
                int gripFrames=0,bilateralFrames=0;
                double work=0;
                float[] motors=null;
                for(int sub=0;sub<STEP;sub++){
                    motors=control.actuate(p.body,p.skin,DT);
                    brain.h.actuate(motors,intent,p.body,p.skin,p.world);
                    for(float a:motors){
                        require(Float.isFinite(a)&&Math.abs(a)<=1.00001,
                                "invalid actual action");
                        actionTape=(actionTape^Float.floatToIntBits(a))*1099511628211L;
                    }
                    float[] sx=p.skin.x.clone(),sy=p.skin.y.clone();
                    float[] bx=p.body.x.clone(),by=p.body.y.clone();
                    float cx=p.body.cx,cy=p.body.cy,prevWork=p.gel.work;
                    p.gel.step(p.body,p.skin,motors,DT);
                    actualWorldStep(p,sx,sy,bx,by,cx,cy);
                    float trueWork=Math.max(0f,p.gel.work-prevWork);
                    control.observe(p.body,DT,trueWork,
                        Math.max(0f,p.gel.lastMuscleReactionImpulse),false,false);
                    brain.h.observe(p.skin,p.world.get(0),trueWork);
                    work+=trueWork;
                    boolean held=false;
                    for(int j=0;j<Morphology.POINTS;j++)
                        if(p.skin.gripHold[j]>=Morphology.GRIP_HOLD){
                            require(p.world.get(0).boxGripHold[j]>=Morphology.GRIP_HOLD,
                                    "invented grip without its true box owner");
                            held=true;
                        }
                    if(held)gripFrames++;
                    if(p.world.get(0).bilateralHeldNow)bilateralFrames++;
                }
                brain.h.end();
                Zero3LearningFoundation.Experience result=control.finish();
                require(result.frames==STEP&&result.intent.decisionId==n,
                        "credited decision mismatch");
                Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
                    EPISODE,n+1,p.gel.ticks*(double)DT,false,
                    p.body,p.skin,p.gel,p.world,motors);
                require(pre.intact()&&post.intact()&&
                        post.solverTicks==pre.solverTicks+STEP,
                        "censored / invalid causal physics");
                brain.e.after(post,true);
                s.tape.add(new Zero3OrganismStoreV322.Trace(
                    n,pre.solverTicks,post.solverTicks,pre.hash,post.hash,
                    actionTape,gripFrames,bilateralFrames,work));
                s.last=motors.clone();s.next++;
                require(s.next==authority.nextDecision(),
                        "authority cursor duplicated or skipped");
            }
        }
        require(!control.pending()&&brain.c.updates()<=s.next&&
                brain.e.updates()==s.next&&brain.h.updates()==s.next,
                "unbalanced learning module updates");
    }
    static String evidence(Session s)throws Exception{
        Zero3OrganismStoreV322.State joint=s.save();
        long grip=0,bilateral=0;double work=0;
        for(Zero3OrganismStoreV322.Trace t:s.tape){
            grip+=t.gripFrames;bilateral+=t.bilateralFrames;
            work+=Double.longBitsToDouble(t.workBits);
        }
        require(joint.nextDecision==END&&s.p.gel.ticks>=WARMUP+END*STEP,
                "same-life did not continue enough real physical frames");
        return "jointFingerprint="+Zero3OrganismStoreV322.fingerprint(joint)+
            "\nphysicsFingerprint="+joint.materialHash()+
            "\nautonomousDecisions="+joint.nextDecision+
            "\nencoderUpdates="+s.brain.e.updates()+
            "\ncortexUpdates="+s.brain.c.updates()+
            "\nadhesionUpdates="+s.brain.h.updates()+
            "\ngelTicks="+s.p.gel.ticks+
            "\nrealGripFrames="+grip+
            "\nrealBilateralFrames="+bilateral+
            "\nrealMechanicalWork="+String.format(Locale.ROOT,"%.12f",work)+
            "\nbodyX="+Float.floatToRawIntBits(s.p.body.cx)+
            "\nboxX="+Float.floatToRawIntBits(s.p.world.get(0).x)+"\n";
    }
    static void file(Path dest,String data)throws Exception {
        Files.write(dest,data.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
    }
    static void prefix(Path dir)throws Exception{
        Zero3OrganismStoreV322 store=new Zero3OrganismStoreV322(dir);
        require(store.load()==null,"nonempty test snapshot directory");
        Session s=virgin();
        run(s,24);store.save(s.save());
        run(s,48);store.save(s.save());
        require(store.generation()==2&&s.next==48,
                "two valid generations not saved at same life");
        // Actively pending neural decisions cannot be checkpointed.
        Brain live=new Brain();
        Zero3OrganismStoreV322.Physical p=initialWorld();
        Zero3SensoryFrameV315 before=Zero3SensoryFrameV315.capture(
            EPISODE,0,p.gel.ticks*DT,true,p.body,p.skin,p.gel,p.world,null);
        live.e.before(before);
        boolean rejected=false;
        try{live.e.checkpoint();}catch(IllegalStateException good){rejected=true;}
        require(rejected,"in-flight PRE checkpoint allowed");
        System.out.println("V322 PREFIX SAME_LIFE cursor=48 gen=2 "+
                           "solverTicks="+s.p.gel.ticks);
    }
    static void resume(Path dir,Path result)throws Exception{
        Zero3OrganismStoreV322 store=new Zero3OrganismStoreV322(dir);
        Zero3OrganismStoreV322.State saved=store.load();
        require(saved!=null&&store.generation()==2&&saved.nextDecision==48,
                "second generation not found");
        // Wrong learning config cannot partially restore a neural module.
        Brain mismatch=new Brain();
        boolean rejected=false;
        try{saved.restoreModules(mismatch.e,
                new Zero3LearningFoundation.AdaptiveCortex(0f),mismatch.h);}
        catch(IOException good){rejected=true;}
        require(rejected&&mismatch.e.updates()==0&&mismatch.h.updates()==0,
                "bad config partially rewrote live neural memory");
        Session s=recovered(saved);
        run(s,END);store.save(s.save());
        file(result,evidence(s));
        System.out.println("V322 RESUME SAME_LIFE cursor=96 gen=3 "+
                           "realGelTicks="+s.p.gel.ticks);
    }
    static void reference(Path result)throws Exception {
        Session s=virgin();run(s,END);
        file(result,evidence(s));
        System.out.println("V322 REFERENCE SAME_LIFE cursor=96 "+
                           "realGelTicks="+s.p.gel.ticks);
    }
    static void damage(Path path)throws Exception{
        try(RandomAccessFile f=new RandomAccessFile(path.toFile(),"rw")){
            require(f.length()>200,"no payload for fault");
            f.seek(f.length()/2);f.writeByte(0x56);
            f.setLength(f.length()-5);f.getFD().sync();
        }
    }
    static void corrupt(Path dir)throws Exception{
        Zero3OrganismStoreV322 disk=new Zero3OrganismStoreV322(dir);
        require(disk.load()!=null&&disk.generation()==3,
                "missing latest for injected crash");
        damage(disk.activeFileForTest());
        Zero3OrganismStoreV322 second=new Zero3OrganismStoreV322(dir);
        Zero3OrganismStoreV322.State rollback=second.load();
        require(rollback!=null&&second.generation()==2&&
                rollback.nextDecision==48&&rollback.records.length==48,
                "rollback merged torn replay or invented decisions");
        Session fresh=recovered(rollback);
        require(fresh.next==48,"wrong authority after rollback");
        second.save(fresh.save()); // writes ONLY genuine recovered experiences
        Zero3OrganismStoreV322 current=new Zero3OrganismStoreV322(dir);
        require(current.load()!=null&&current.generation()==3&&
                current.load().records.length==48,
                "recommit after rollback invented experiences");
        Files.write(current.manifestForTest(),new byte[]{1,2,3});
        Zero3OrganismStoreV322 fallback=new Zero3OrganismStoreV322(dir);
        require(fallback.load()!=null&&fallback.generation()==2,
                "manifest backup not honoured");
        damage(fallback.activeFileForTest());
        damage(fallback.previousFileForTest());
        require(new Zero3OrganismStoreV322(dir).load()==null,
                "double corruption did not fail closed");
        System.out.println("V322 FAULT rollbackCursor=48 noDuplicateCredit=true "+
            "bothGenerationsBadFailClosed=true");
    }
    public static void main(String[] args)throws Exception{
        require(args.length>=2,"missing phase");
        Path dir=Paths.get(args[1]);
        switch(args[0]){
            case "prefix":prefix(dir);break;
            case "resume":resume(dir,Paths.get(args[2]));break;
            case "reference":reference(Paths.get(args[1]));break;
            case "corrupt":corrupt(dir);break;
            default:throw new IllegalArgumentException("bad V322 phase");
        }
    }
}
