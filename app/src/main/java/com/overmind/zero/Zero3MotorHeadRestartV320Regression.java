package com.overmind.zero;

import java.nio.file.*;
import java.io.*;
import java.util.*;

/** V3.20 separate JVM processes: prefix, resume, uninterrupted, fault. */
public final class Zero3MotorHeadRestartV320Regression {
    static final long SEED=0x31920261017L;
    static void require(boolean b,String m){if(!b)throw new AssertionError("ZERO3 V320 "+m);}
    static boolean refused(Runnable task){
        try{task.run();return false;}
        catch(IllegalStateException|IllegalArgumentException e){return true;}
    }
    static Zero3ContextAdhesionV319.State trainRange(
          Zero3ContextAdhesionV319 motor,int a,int end){
        Zero3ContextAdhesionV319Regression.Stats stats=
            new Zero3ContextAdhesionV319Regression.Stats();
        for(int id=a;id<end;id++)
            Zero3ContextAdhesionV319Regression.life(
                Zero3ContextAdhesionV319Regression.modern(motor),
                stats,id,false);
        require(stats.decisions==(end-a)*96 &&
                stats.physicalSteps==(end-a)*96*12,
                "not all actual actor decisions executed");
        return motor.captureAtDecisionBoundary();
    }
    static String metrics(Zero3ContextAdhesionV319 motor){
        Zero3ContextAdhesionV319 eval=motor.frozenCopy();
        Zero3ContextAdhesionV319Regression.Stats stats=
            new Zero3ContextAdhesionV319Regression.Stats();
        // Fresh physical poses and obstacle gaps (ordinals 24..47);
        // this is not a second tuning pass over the consumed V3.19 blind.
        for(int id=24;id<48;id++)
            Zero3ContextAdhesionV319Regression.life(
                Zero3ContextAdhesionV319Regression.modern(eval),
                stats,id,true);
        require(stats.episodes==24&&stats.physicalSteps==24*96*12,
                "retention evaluation physics incomplete");
        require(eval.updates()==0 && stats.familyGrip[0]==0&&
                stats.familyGrip[2]==0&&stats.familyGrip[3]==0,
                "evaluation mutated or invented grips outside BOX");
        return stats.report();
    }
    static void writeText(Path path,String text)throws Exception{
        Files.write(path,text.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
    }
    static String evidence(Zero3ContextAdhesionV319 m)throws Exception{
        return "fingerprint="+Zero3MotorHeadStoreV320.fingerprint(
                   m.captureAtDecisionBoundary())+"\n"+
               "trainingUpdates="+m.updates()+"\n"+
               "retention="+metrics(m)+"\n";
    }
    static void prefix(Path dir)throws Exception{
        Zero3MotorHeadStoreV320 disk=new Zero3MotorHeadStoreV320(dir);
        require(disk.load()==null,"dirty prefix path");
        Zero3ContextAdhesionV319 model=new Zero3ContextAdhesionV319(SEED,true);
        trainRange(model,0,8);disk.save(model);
        require(disk.generation()==1,"first checkpoint lost");
        trainRange(model,8,16);disk.save(model);
        require(disk.generation()==2&&model.updates()==1536,"second generation wrong");
        float[] exported=model.captureAtDecisionBoundary().weights();
        exported[0]=42f;
        require(model.captureAtDecisionBoundary().weights()[0]!=42f,
                "live weights escaped through checkpoint getter");
        // Snapshot refused while a real motor decision is in progress.
        model.begin();
        require(refused(()->model.captureAtDecisionBoundary()),
                "partial intention allowed as a durable snapshot");
        require(refused(()->model.restoreAtDecisionBoundary(
                model.frozenCopy().captureAtDecisionBoundary())),
                "in-flight restore accepted");
        System.out.println("ZERO3 V320 PREFIX gen=2 updates=1536 physicalSteps=18432");
    }
    static void resume(Path dir,Path output)throws Exception{
        Zero3MotorHeadStoreV320 disk=new Zero3MotorHeadStoreV320(dir);
        Zero3ContextAdhesionV319.State saved=disk.load();
        require(saved!=null&&disk.generation()==2&&saved.decisions==1536,
                "cross-process failed to restore prefix generation");
        Zero3ContextAdhesionV319 resumed=
            new Zero3ContextAdhesionV319(123456789L,true);
        resumed.restoreAtDecisionBoundary(saved);
        require(resumed.updates()==1536,"optimizer time not restored");
        require(refused(()->resumed.restoreAtDecisionBoundary(saved)),
                "double restore accepted");
        trainRange(resumed,16,32);
        require(resumed.updates()==3072&&resumed.positiveGripEvents()>0,
                "no retained model learning after restart");
        disk.save(resumed);
        require(disk.generation()==3,"post-resume generation missing");
        writeText(output,evidence(resumed));
        System.out.println("ZERO3 V320 RESUMED gen=3 updates=3072 "+ 
            "fingerprint="+Zero3MotorHeadStoreV320.fingerprint(
                resumed.captureAtDecisionBoundary()));
    }
    static void reference(Path output)throws Exception{
        Zero3ContextAdhesionV319 continuous=
            new Zero3ContextAdhesionV319(SEED,true);
        trainRange(continuous,0,32);
        require(continuous.updates()==3072,"uninterrupted training incomplete");
        writeText(output,evidence(continuous));
        System.out.println("ZERO3 V320 UNINTERRUPTED updates=3072 "+
            "fingerprint="+Zero3MotorHeadStoreV320.fingerprint(
                continuous.captureAtDecisionBoundary()));
    }
    static void damage(Path file)throws Exception{
        try(RandomAccessFile d=new RandomAccessFile(file.toFile(),"rw")){
            require(d.length()>80,"cannot fault test empty blob");
            d.seek(d.length()/2);d.writeByte(0xAA);
            d.setLength(d.length()-5);
            d.getFD().sync();
        }
    }
    static void fault(Path dir)throws Exception{
        Zero3MotorHeadStoreV320 latest=new Zero3MotorHeadStoreV320(dir);
        require(latest.load()!=null&&latest.generation()==3,"missing final checkpoint");
        damage(latest.latestFile());
        Zero3MotorHeadStoreV320 rollback=new Zero3MotorHeadStoreV320(dir);
        Zero3ContextAdhesionV319.State prior=rollback.load();
        require(prior!=null&&rollback.generation()==2&&prior.decisions==1536,
                "corrupt newest brain escaped / valid old generation lost");
        Zero3ContextAdhesionV319 survivor=new Zero3ContextAdhesionV319(1,true);
        survivor.restoreAtDecisionBoundary(prior);
        rollback.save(survivor);
        Zero3MotorHeadStoreV320 recommitted=new Zero3MotorHeadStoreV320(dir);
        require(recommitted.load()!=null&&recommitted.generation()==3&&
                recommitted.load().decisions==1536,
                "recheckpoint invented unrecoverable learning");
        Files.write(recommitted.manifestFile(),new byte[]{1,2,3,4});
        Zero3MotorHeadStoreV320 fallback=new Zero3MotorHeadStoreV320(dir);
        require(fallback.load()!=null&&fallback.generation()==2&&
                fallback.load().decisions==1536,
                "corrupt primary manifest did not recover predecessor");
        damage(fallback.oldFile()); // current fallback slot=old; this damages newer only
        // Destructively make both committed payloads invalid.
        damage(fallback.latestFile());
        require(new Zero3MotorHeadStoreV320(dir).load()==null,
                "two damaged generations did not fail closed");
        System.out.println("ZERO3 V320 FAULT rollbackUpdates=1536 "+
            "recommitAfterRollback=1536 corruptManifestFallback=true "+
            "bothGenerationsInvalidFailClosed=true");
    }
    public static void main(String[] args)throws Exception{
        require(args.length>=2,"mode and isolated path required");
        String mode=args[0];Path root=Paths.get(args[1]);
        switch(mode){
            case "prefix":prefix(root);break;
            case "resume":require(args.length==3,"output required");
                          resume(root,Paths.get(args[2]));break;
            case "reference":require(args.length==3,"output required");
                             reference(Paths.get(args[2]));break;
            case "fault":fault(root);break;
            default:throw new IllegalArgumentException("unknown V320 mode");
        }
    }
}
