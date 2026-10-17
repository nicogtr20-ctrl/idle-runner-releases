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
public final class Zero3MultiSceneRestartV322BRegression {
    static final float DT=.025f;
    static final int STEP=12,WARMUP=96,END=96;
    static final int ORDINAL=Integer.parseInt(System.getProperty("zero3.ordinal","5"));
    static final int MODE=Integer.parseInt(System.getProperty("zero3.mode","1"));
    static final long EPISODE=950000L+ORDINAL;
    static final long WORLD_RNG=0x332220261017L^((long)ORDINAL<<24)^MODE;
    static final String OWNER=Zero3AutonomousLoopV317.OWNER;
    private Zero3MultiSceneRestartV322BRegression(){}
    static void require(boolean v,String msg){
        if(!v)throw new AssertionError("ZERO3 V322B "+msg);
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
        require(MODE>=0&&MODE<=4,"unknown scene fixture");
        int rep=ORDINAL/4;
        float angle=(float)(-.73+ORDINAL*.219);
        int condition=(rep+1)%4;
        Zero3MotorImpulseResponseRegression.Scenario scene=
            new Zero3MotorImpulseResponseRegression.Scenario((ORDINAL+1)%4,angle,condition);
        float[] silent=new float[64];
        for(int k=0;k<WARMUP;k++){
            scene.gel.step(scene.body,scene.skin,silent,DT);
            require(scene.gel.invalid==0,"warmup invalid");
        }
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        if(MODE!=0){
            float gap=MODE==3?340f:MODE==2?47.8f:45.7f;
            float yaw=(float)(.06*(rep%3-1));
            objects.add(new UnknownWorld.Entity(
                scene.body.cx+gap*(float)Math.cos(yaw),
                scene.body.cy+gap*(float)Math.sin(yaw),2,MODE==2?2:5));
            if(MODE==4)
                objects.add(new UnknownWorld.Entity(scene.body.cx-48,
                    scene.body.cy+10,2,2));
        }
        return new Zero3OrganismStoreV322.Physical(
            scene.body,scene.skin,scene.gel,objects);
    }
    static UnknownWorld.Entity firstBox(List<UnknownWorld.Entity> world){
        for(UnknownWorld.Entity e:world)if(e.isBilateralBox())return e;
        return null;
    }
    static void actualWorldStep(Zero3OrganismStoreV322.Physical p,
            float[] oldSkinX,float[] oldSkinY,float[] oldBodyX,
            float[] oldBodyY,float cx,float cy){
        boolean boxes=false;
        for(UnknownWorld.Entity object:p.world)if(object.isBilateralBox()){
            SquareGripPuzzle.constrain(p.body,p.skin,object,DT,
                oldSkinX,oldSkinY,oldBodyX,oldBodyY,cx,cy);
            boxes=true;
        }
        if(boxes)MaterialContactConstraints.reconcile(p.body,p.skin,p.world,DT);
        SymbioteWorldStepV171.interior(p.gel,p.body,p.skin,null,p.world,DT);
        GripSurfaceComposition.compose(p.skin,null,p.world);
        for(UnknownWorld.Entity object:p.world){
            if(object.isMovable()){
                Morphology.collide(p.skin,object,DT);
                ContactLearning.collide(p.body,object,DT);
                p.gel.observeBody(p.body,p.skin);
            }
            object.tick(DT);
        }
        require(p.gel.invalid==0,"invalid real collision physics");
    }
    // RNG drives an ACTUAL future world-spawn event. Its live state, rather than
    // the birth seed, must be saved at decision 48 and consumed after restart.
    static long advance(long value){
        value^=value<<13;value^=value>>>7;value^=value<<17;
        return value==0?0x3322BBEEFEDL:value;
    }

    static class Session {
        final Brain brain;
        final Zero3OrganismStoreV322.Physical p;
        final ArrayList<Zero3OrganismStoreV322.Trace> tape;
        final ArrayList<Zero3FullCausalExperienceV322B> experiences;
        long worldRng;
        float[] last;
        int next;
        Session(Brain b,Zero3OrganismStoreV322.Physical phys,int next,
                Zero3OrganismStoreV322.Trace[] old,
                List<Zero3FullCausalExperienceV322B> journal,
                float[] efference,long worldRng){
            brain=b;p=phys;this.next=next;this.worldRng=worldRng;
            tape=new ArrayList<>(Arrays.asList(old));
            experiences=new ArrayList<>(journal);
            last=efference==null?null:efference.clone();
        }
        Zero3FullArchiveStoreV322B.Bundle save()throws IOException {
            require(!tape.isEmpty()&&next==tape.size()&&
                    next==experiences.size(),"incomplete full replay");
            Zero3OrganismStoreV322.State physical=Zero3OrganismStoreV322.capture(
                EPISODE,next,worldRng,brain.e,brain.c,brain.h,
                p.body,p.skin,p.gel,p.world,last,tape);
            return new Zero3FullArchiveStoreV322B.Bundle(
                physical,experiences,worldRng);
        }
        void worldEventAtDecision(int n){
            if(MODE==0||n!=24&&n!=60)return;
            worldRng=advance(worldRng);
            float theta=(worldRng>>>40)/16777216f*(float)(Math.PI*2.);
            worldRng=advance(worldRng);
            float distance=MODE==3?210f:MODE==2?48f:140f+
                (worldRng>>>40)/16777216f*45f;
            p.world.add(new UnknownWorld.Entity(
                p.body.cx+distance*(float)Math.cos(theta),
                p.body.cy+distance*(float)Math.sin(theta),2,2));
        }
    }
    static Session virgin(){
        Brain b=new Brain();b.e.resetEpisode();
        return new Session(b,initialWorld(),0,
            new Zero3OrganismStoreV322.Trace[0],
            Collections.emptyList(),null,WORLD_RNG);
    }
    static Session recovered(Zero3FullArchiveStoreV322B.Bundle joint)
              throws IOException{
        Zero3OrganismStoreV322.State state=joint.organism;
        require(state!=null&&state.episodeId==EPISODE&&
                joint.actualWorldRngState()==state.lastRngState,
                "mixed world RNG and organism identity");
        Brain b=new Brain();b.e.resetEpisode();
        Zero3OrganismStoreV322.Physical material=state.physical();
        state.restoreModules(b.e,b.c,b.h);
        require(b.e.updates()==state.nextDecision&&
                b.c.updates()<=state.nextDecision&&
                b.h.updates()==state.nextDecision,
                "neural checkpoint inconsistent with recovered life");
        return new Session(b,material,state.nextDecision,
            state.trace(),joint.experiences(),state.last64(),
            joint.actualWorldRngState());
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
                s.worldEventAtDecision(n);
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
                ArrayList<float[]> actualCommands=new ArrayList<>();
                ArrayList<Float> measuredCosts=new ArrayList<>();
                float[] motors=null;
                for(int sub=0;sub<STEP;sub++){
                    motors=control.actuate(p.body,p.skin,DT);
                    brain.h.actuate(motors,intent,p.body,p.skin,p.world);
                    actualCommands.add(motors.clone());
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
                    brain.h.observe(p.skin,firstBox(p.world),trueWork);
                    work+=trueWork;
                    measuredCosts.add(trueWork);
                    boolean held=false;
                    for(int j=0;j<Morphology.POINTS;j++)
                        if(p.skin.gripHold[j]>=Morphology.GRIP_HOLD){
                            UnknownWorld.Entity box=firstBox(p.world);
                            require(box!=null&&box.boxGripHold[j]>=Morphology.GRIP_HOLD,
                                    "invented grip without its true box owner");
                            held=true;
                        }
                    if(held)gripFrames++;
                    UnknownWorld.Entity box=firstBox(p.world);
                    if(box!=null&&box.bilateralHeldNow)bilateralFrames++;
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
                Zero3OrganismStoreV322.Trace trace=new Zero3OrganismStoreV322.Trace(
                    n,pre.solverTicks,post.solverTicks,pre.hash,post.hash,
                    actionTape,gripFrames,bilateralFrames,work);
                try{
                    s.experiences.add(Zero3FullCausalExperienceV322B.capture(
                        trace,pre,post,actualCommands,measuredCosts,
                        gripFrames,bilateralFrames));
                }catch(IOException error){
                    throw new IllegalStateException("failed causal full replay",error);
                }
                s.tape.add(trace);
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
        Zero3FullArchiveStoreV322B.Bundle archived=s.save();
        Zero3OrganismStoreV322.State joint=archived.organism;
        long grip=0,bilateral=0;double work=0;
        for(Zero3OrganismStoreV322.Trace t:s.tape){
            grip+=t.gripFrames;bilateral+=t.bilateralFrames;
            work+=Double.longBitsToDouble(t.workBits);
        }
        require(joint.nextDecision==END&&s.p.gel.ticks>=WARMUP+END*STEP&&
                archived.experienceCount()==END,
                "did not complete full same-life replay");
        return "jointFingerprint="+Zero3FullArchiveStoreV322B.fingerprint(archived)+
            "\nphysicsFingerprint="+joint.materialHash()+
            "\nscene="+MODE+" ordinal="+ORDINAL+
            "\nautonomousDecisions="+joint.nextDecision+
            "\nfullSensoryActionRecords="+archived.experienceCount()+
            "\nworldRngState="+s.worldRng+
            "\nworldEntities="+s.p.world.size()+
            "\nencoderUpdates="+s.brain.e.updates()+
            "\ncortexUpdates="+s.brain.c.updates()+
            "\nadhesionUpdates="+s.brain.h.updates()+
            "\ngelTicks="+s.p.gel.ticks+
            "\nrealGripFrames="+grip+
            "\nrealBilateralFrames="+bilateral+
            "\nrealMechanicalWork="+String.format(Locale.ROOT,"%.12f",work)+
            "\nbodyX="+Float.floatToRawIntBits(s.p.body.cx)+"\n";
    }

    static void file(Path dest,String data)throws Exception {
        Files.write(dest,data.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
    }
    static void prefix(Path dir)throws Exception{
        Zero3FullArchiveStoreV322B store=new Zero3FullArchiveStoreV322B(dir);
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
        System.out.println("V322B PREFIX SAME_LIFE cursor=48 gen=2 "+
                           "solverTicks="+s.p.gel.ticks);
    }
    static void resume(Path dir,Path result)throws Exception{
        Zero3FullArchiveStoreV322B store=new Zero3FullArchiveStoreV322B(dir);
        Zero3FullArchiveStoreV322B.Bundle saved=store.load();
        require(saved!=null&&store.generation()==2&&saved.organism.nextDecision==48,
                "second generation not found");
        // Wrong learning config cannot partially restore a neural module.
        Brain mismatch=new Brain();
        boolean rejected=false;
        try{saved.organism.restoreModules(mismatch.e,
                new Zero3LearningFoundation.AdaptiveCortex(0f),mismatch.h);}
        catch(IOException good){rejected=true;}
        require(rejected&&mismatch.e.updates()==0&&mismatch.h.updates()==0,
                "bad config partially rewrote live neural memory");
        Session s=recovered(saved);
        run(s,END);store.save(s.save());
        file(result,evidence(s));
        System.out.println("V322B RESUME SAME_LIFE cursor=96 gen=3 "+
                           "realGelTicks="+s.p.gel.ticks);
    }
    static void reference(Path result)throws Exception {
        Session s=virgin();run(s,END);
        file(result,evidence(s));
        System.out.println("V322B REFERENCE SAME_LIFE cursor=96 "+
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
        Zero3FullArchiveStoreV322B disk=new Zero3FullArchiveStoreV322B(dir);
        require(disk.load()!=null&&disk.generation()==3,
                "missing latest for injected crash");
        damage(disk.activeFileForTest());
        Zero3FullArchiveStoreV322B second=new Zero3FullArchiveStoreV322B(dir);
        Zero3FullArchiveStoreV322B.Bundle rollback=second.load();
        require(rollback!=null&&second.generation()==2&&
                rollback.organism.nextDecision==48&&rollback.organism.trace().length==48&&
                 rollback.experienceCount()==48,
                "rollback merged torn replay or invented decisions");
        Session fresh=recovered(rollback);
        require(fresh.next==48,"wrong authority after rollback");
        second.save(fresh.save()); // writes ONLY genuine recovered experiences
        Zero3FullArchiveStoreV322B current=new Zero3FullArchiveStoreV322B(dir);
        require(current.load()!=null&&current.generation()==3&&
                current.load().organism.trace().length==48&&
                current.load().experienceCount()==48,
                "recommit after rollback invented experiences");
        Files.write(current.currentManifestForTest(),new byte[]{1,2,3});
        Zero3FullArchiveStoreV322B fallback=new Zero3FullArchiveStoreV322B(dir);
        require(fallback.load()!=null&&fallback.generation()==2,
                "manifest backup not honoured");
        damage(fallback.activeFileForTest());
        damage(fallback.oldFileForTest());
        require(new Zero3FullArchiveStoreV322B(dir).load()==null,
                "double corruption did not fail closed");
        System.out.println("V322B FAULT rollbackCursor=48 noDuplicateCredit=true "+
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
            default:throw new IllegalArgumentException("bad V322B phase");
        }
    }
}
