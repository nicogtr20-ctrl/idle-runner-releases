package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Locale;

/** Four independent JVM processes, one integrated TRAINING circuit. */
public final class Zero3IntegratedRestartV321Regression {
    static final float DT=.025f;
    static final int STEPS=12,WARMUP=96;
    static final String OWNER=Zero3AutonomousLoopV317.OWNER;
    private Zero3IntegratedRestartV321Regression(){}
    static void require(boolean b,String why){
        if(!b)throw new AssertionError("ZERO3 V321 "+why);
    }
    static final class Modules{
        final Zero3AutonomousLoopV317.Encoder encoder;
        final Zero3LearningFoundation.AdaptiveCortex cortex;
        Zero3ContextAdhesionV319 head;
        Modules(){
            encoder=new Zero3AutonomousLoopV317.Encoder(0x31720261017L);
            cortex=new Zero3LearningFoundation.AdaptiveCortex(.013f);
            head=new Zero3ContextAdhesionV319(0x31920261017L,true);
        }
        void restore(Zero3NeuralBundleStoreV321.Bundle saved){
            saved.restore(encoder,cortex,head);
        }
        Zero3NeuralBundleStoreV321.Bundle capture(int next){
            return Zero3NeuralBundleStoreV321.capture(next,encoder,cortex,head);
        }
    }
    static final class Stats{
        int lives,decisions,physicalSteps,trainedCortexCredit;
        int boxLivesWithHold,heldBoxFrames,bilateralFrames,contactBoxFrames;
        long tape=0xcbf29ce484222325L;
        double work,netMovement;
        final int[] gripByMode=new int[4];
        String report(){
            return String.format(Locale.US,
                "lives=%d autonomousDecisions=%d realGelSteps=%d "+
                "boxContactFrames=%d boxLivesWithGrip=%d "+
                "boxHeldFrames=%d bilateralFrames=%d "+
                "motorTapeHash=%016x workTotal=%.10f displacement=%.8f",
                lives,decisions,physicalSteps,contactBoxFrames,
                boxLivesWithHold,heldBoxFrames,bilateralFrames,
                tape,work,netMovement);
        }
    }
    static void oneLife(Modules brain,Stats total,int ordinal,
                        int decisions,boolean train){
        int mode=ordinal%4,rep=ordinal/4;
        long episode=(train?950000L:975000L)+ordinal;
        float angle=train?(float)(-.73+ordinal*.219):
                          (float)(.37+ordinal*.173);
        int condition=(rep+(train?1:3))%4;
        Zero3MotorImpulseResponseRegression.Scenario scene=
            new Zero3MotorImpulseResponseRegression.Scenario(
                (ordinal+1)%4,angle,condition);
        float[] silent=new float[64];
        for(int k=0;k<WARMUP;k++){
            scene.gel.step(scene.body,scene.skin,silent,DT);
            require(scene.gel.invalid==0,"invalid preconditioning");
        }
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        UnknownWorld.Entity object=null;
        if(mode!=0){
            float gap=mode==3?340f:mode==1?
                (float)(45.3+(rep%6)*.69):
                (float)(46.8+(rep%5)*.6);
            float yaw=(float)(.06*(rep%3-1));
            object=new UnknownWorld.Entity(
                scene.body.cx+gap*(float)Math.cos(yaw),
                scene.body.cy+gap*(float)Math.sin(yaw),
                2,mode==2?2:5);
            world.add(object);
        }
        brain.encoder.resetEpisode(); // only temporal state resets at new life
        Zero3LearningFoundation.Coordinator controller=
            new Zero3LearningFoundation.Coordinator(brain.cortex);
        float[] last=null;
        int hadGrip=0;
        try(Zero3MotorIntentV316.Authority authority=
            new Zero3MotorIntentV316.Authority(episode,OWNER)){
            for(int n=0;n<decisions;n++){
                Zero3SensoryFrameV315 before=Zero3SensoryFrameV315.capture(
                    episode,n,scene.gel.ticks*(double)DT,true,
                    scene.body,scene.skin,scene.gel,world,last);
                long tick=before.solverTicks;
                brain.encoder.before(before);
                float[] chosen=brain.encoder.choose();
                Zero3MotorIntentV316 intent=authority.issue(OWNER,n,
                    (int)brain.cortex.revision(),321,chosen[0],chosen[1],
                    chosen[2],chosen[3],STEPS*DT,.35f);
                Zero3LearningFoundation.MotorIntent motorIntent=
                    new Zero3LearningFoundation.MotorIntent(episode,n,
                        brain.cortex.revision(),chosen[0],chosen[1],
                        chosen[2],chosen[3],STEPS*DT);
                controller.begin(motorIntent,scene.body,train,!train,train);
                brain.head.begin();
                float startX=scene.body.cx,startY=scene.body.cy;
                float[] motors=null;
                for(int sub=0;sub<STEPS;sub++){
                    motors=controller.actuate(scene.body,scene.skin,DT);
                    brain.head.actuate(motors,intent,
                        scene.body,scene.skin,world);
                    for(float v:motors){
                        require(Float.isFinite(v)&&Math.abs(v)<=1.00001,
                                "nonfinite actual musculoskeletal actuation");
                        total.tape=(total.tape^Float.floatToIntBits(v))*
                            1099511628211L;
                    }
                    float[] sx=scene.skin.x.clone(),sy=scene.skin.y.clone(),
                            bx=scene.body.x.clone(),by=scene.body.y.clone();
                    float cx=scene.body.cx,cy=scene.body.cy;
                    float previousWork=scene.gel.work;
                    scene.gel.step(scene.body,scene.skin,motors,DT);
                    Zero3SensoryContractV315Regression.applyRealWorld(
                        mode,scene,world,sx,sy,bx,by,cx,cy);
                    require(scene.gel.invalid==0,"invalid true solver result");
                    float actualWork=Math.max(0,scene.gel.work-previousWork);
                    controller.observe(scene.body,DT,actualWork,
                        Math.max(0,scene.gel.lastMuscleReactionImpulse),false,false);
                    brain.head.observe(scene.skin,object,actualWork);
                    total.work+=actualWork;
                    total.physicalSteps++;
                    boolean hold=false,touch=false;
                    for(int i=0;i<96;i++){
                        if(scene.skin.touchSense[i]>.01f||
                           scene.skin.touchPulse[i]>.001f)touch=true;
                        if(scene.skin.gripHold[i]>=Morphology.GRIP_HOLD){
                            require(mode==1&&object!=null&&
                                object.boxGripHold[i]>=Morphology.GRIP_HOLD,
                                "invalid grip without true physical box");
                            hold=true;
                        }
                    }
                    if(mode==1&&touch)total.contactBoxFrames++;
                    if(hold){hadGrip++;total.heldBoxFrames++;total.gripByMode[mode]++;}
                    if(object!=null&&object.isBilateralBox()&&object.bilateralHeldNow)
                        total.bilateralFrames++;
                }
                brain.head.end();
                Zero3LearningFoundation.Experience outcome=controller.finish();
                require(outcome.frames==STEPS&&outcome.intent.decisionId==n,
                        "wrong causal motor credit");
                if(brain.cortex.revision()>motorIntent.revision)
                    total.trainedCortexCredit++;
                Zero3SensoryFrameV315 after=Zero3SensoryFrameV315.capture(
                    episode,n+1,scene.gel.ticks*(double)DT,false,
                    scene.body,scene.skin,scene.gel,world,motors);
                require(before.intact()&&after.intact()&&
                        after.solverTicks==tick+STEPS,
                        "faked sensory transition");
                brain.encoder.after(after,train);
                total.netMovement+=Math.hypot(scene.body.cx-startX,
                                               scene.body.cy-startY);
                last=motors.clone();total.decisions++;
            }
            require(authority.nextDecision()==decisions,
                    "missing/duplicate decision");
            require(!controller.pending(),"pending learning coordinator");
        }
        if(mode==1&&hadGrip>0)total.boxLivesWithHold++;
        total.lives++;
    }
    static Stats range(Modules m,int start,int end,int decisions,boolean train){
        Stats stats=new Stats();
        for(int id=start;id<end;id++)
            oneLife(m,stats,id,decisions,train);
        require(stats.decisions==(end-start)*decisions&&
                stats.physicalSteps==stats.decisions*STEPS,
                "not enough real motor choices");
        return stats;
    }
    static String evidence(Zero3NeuralBundleStoreV321.Bundle b)throws Exception{
        Modules m=new Modules();
        m.restore(b);
        m.head=m.head.frozenCopy();
        Stats test=range(m,80,88,64,false);
        require(test.lives==8&&test.decisions==512&&
                test.physicalSteps==6144&&
                m.encoder.updates()==b.encoder.updates,
                "holdout contamination");
        for(int k:new int[]{0,2,3})
            require(test.gripByMode[k]==0,"false real-world grip");
        return "fingerprint="+Zero3NeuralBundleStoreV321.fingerprint(b)+
            "\nencoderUpdates="+b.encoder.updates+
            "\ncortexUpdates="+b.cortex.updates+
            "\nadhesionUpdates="+b.adhesive.decisions+
            "\nretention="+test.report()+"\n";
    }
    static void write(Path target,String text)throws Exception{
        Files.writeString(target,text,StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);
    }
    static void prefix(Path dir)throws Exception{
        Zero3NeuralBundleStoreV321 disk=new Zero3NeuralBundleStoreV321(dir);
        require(disk.load()==null,"unexpected previous brain bundle");
        Modules m=new Modules();
        range(m,0,4,96,true);
        disk.save(m.capture(4));
        range(m,4,8,96,true);
        disk.save(m.capture(8));
        require(disk.generation()==2&&m.encoder.updates()==768&&
            m.head.updates()==768&&m.cortex.updates()>0,
            "failed true 3-component learning");
        // Do not allow a mixed in-flight recurrent, motor or cortex bundle.
        m.head.begin();
        boolean rejected=false;
        try{m.capture(8);}catch(IllegalStateException expected){rejected=true;}
        require(rejected,"saved head while its decision was pending");
        System.out.println("ZERO3 V321 PREFIX trainingEpisodes=8 actorChoices=768 "+
            "physicalGelSteps=9216 encoderUpdates=768 cortexUpdates="+
            m.cortex.updates()+" motorUpdates=768 gen=2");
    }
    static void resume(Path dir,Path output)throws Exception{
        Zero3NeuralBundleStoreV321 disk=new Zero3NeuralBundleStoreV321(dir);
        Zero3NeuralBundleStoreV321.Bundle saved=disk.load();
        require(saved!=null&&disk.generation()==2&&saved.nextEpisode==8,
                "cross JVM bundle not found");
        Modules m=new Modules();m.restore(saved);
        require(m.encoder.updates()==768&&m.head.updates()==768,
                "neural update counters not restored");
        range(m,8,16,96,true);
        Zero3NeuralBundleStoreV321.Bundle completed=m.capture(16);
        require(completed.encoder.updates==1536&&
                completed.adhesive.decisions==1536&&
                completed.cortex.updates>0,"no resumed neuroplastic learning");
        disk.save(completed);
        write(output,evidence(completed));
        System.out.println("ZERO3 V321 RESUME gen=3 actorChoices=1536 "+
            "fingerprint="+Zero3NeuralBundleStoreV321.fingerprint(completed));
    }
    static void reference(Path output)throws Exception{
        Modules m=new Modules();range(m,0,16,96,true);
        Zero3NeuralBundleStoreV321.Bundle full=m.capture(16);
        write(output,evidence(full));
        System.out.println("ZERO3 V321 REFERENCE uninterruptedChoices=1536 "+
            "fingerprint="+Zero3NeuralBundleStoreV321.fingerprint(full));
    }
    static void damage(Path path)throws Exception{
        try(RandomAccessFile f=new RandomAccessFile(path.toFile(),"rw")){
            require(f.length()>100,"snapshot already missing");
            f.seek(f.length()/2);f.writeByte(0x42);
            f.setLength(f.length()-11);f.getFD().sync();
        }
    }
    static void corrupt(Path root)throws Exception{
        Zero3NeuralBundleStoreV321 disk=new Zero3NeuralBundleStoreV321(root);
        require(disk.load()!=null&&disk.generation()==3,
                "missing latest complete bundle");
        damage(disk.activeFileForTest());
        Zero3NeuralBundleStoreV321 fallback=new Zero3NeuralBundleStoreV321(root);
        Zero3NeuralBundleStoreV321.Bundle rollback=fallback.load();
        require(rollback!=null&&fallback.generation()==2&&
                rollback.nextEpisode==8&&rollback.encoder.updates==768,
                "corrupt latest promoted invalid/inconsistent modules");
        Modules m=new Modules();m.restore(rollback);
        fallback.save(m.capture(8));
        Zero3NeuralBundleStoreV321 current=new Zero3NeuralBundleStoreV321(root);
        require(current.load()!=null&&current.generation()==3&&
                current.load().nextEpisode==8,
                "recommit after rollback invented learning");
        Files.write(current.currentManifestForTest(),new byte[]{3,2,1});
        Zero3NeuralBundleStoreV321 backup=new Zero3NeuralBundleStoreV321(root);
        require(backup.load()!=null&&backup.generation()==2,
                "invalid primary manifest not rolled back");
        damage(backup.activeFileForTest());
        damage(backup.oldFileForTest());
        require(new Zero3NeuralBundleStoreV321(root).load()==null,
                "fully invalid neural snapshot did not fail closed");
        System.out.println("ZERO3 V321 FAULT rolledBackChoices=768 "+
            "recommitChoices=768 corruptManifestRecovered=true "+
            "bothGenerationsBadFailClosed=true");
    }
    public static void main(String[] args)throws Exception{
        require(args.length>=2,"mode/path missing");
        Path dir=Path.of(args[1]);
        switch(args[0]){
            case "prefix":prefix(dir);break;
            case "resume":resume(dir,Path.of(args[2]));break;
            case "reference":reference(Path.of(args[2]));break;
            case "corrupt":corrupt(dir);break;
            default:throw new IllegalArgumentException("unknown V321 step");
        }
    }
}
