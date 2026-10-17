package com.overmind.zero;

import java.nio.file.*;
import java.util.Arrays;

/** Independently process-restarted V3.26 actual newborn physics, no test Scenario. */
public final class Zero3BirthV326Regression {
    private static final long EPISODE=32620261017L,SEED=0x326B176ABCDL;
    private Zero3BirthV326Regression(){}
    private static void check(boolean condition,String error){
        if(!condition)throw new AssertionError("V326 android-birth "+error);
    }
    private static Zero3RuntimeV325 virgin()throws Exception{
        Zero3RuntimeV325 r=Zero3WorldBirthV326.create(EPISODE,SEED);
        Zero3RuntimeV325.Display d=r.display();
        check(d.decisions==0&&d.objects==3&&d.membraneX.length==96&&
            d.membraneY.length==96&&d.reserve==100f&&d.solverTicks==96&&
            d.neurons==16&&!d.dead,
            "newbirth fluid/sensors/objects/cortex");
        // UI cannot move the organism by changing a defensive copy.
        float actual=d.membraneX[0];
        d.membraneX[0]=123456f;
        check(Float.floatToRawIntBits(r.display().membraneX[0])==
              Float.floatToRawIntBits(actual),"UI mutated physical material");
        return r;
    }
    private static void run(Zero3RuntimeV325 r,int count)throws Exception{
        while(r.decisions()<count)r.advanceOneDecision();
        check(r.completeExperiences()==count&&r.encoderUpdates()==count&&
              r.gripUpdates()==count&&r.gelTicks()==96+12*count&&
              r.roomFoodCanonical(),"real neonatal neural/physics ledger");
    }
    private static void result(Path path,Zero3RuntimeV325 r)throws Exception{
        String contents="whole="+r.fingerprint()+"\n"+
            "decisions="+r.decisions()+"\n"+
            "gelTicks="+r.gelTicks()+"\n"+
            "energyBits="+Float.floatToIntBits(r.energy())+"\n"+
            "objects="+r.objects()+"\n"+
            "neural="+r.encoderUpdates()+"\n";
        Files.write(path,contents.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
    private static void fault(Path dir)throws Exception{
        Zero3RoomArchiveV324 disk=new Zero3RoomArchiveV324(dir);
        check(disk.load()!=null&&disk.generation()==3,"missing latest lab slot");
        java.io.RandomAccessFile corrupted=
            new java.io.RandomAccessFile(disk.latestFile().toFile(),"rw");
        try{corrupted.setLength(corrupted.length()/2);}finally{corrupted.close();}
        try(Zero3RuntimeV325 r=Zero3RuntimeV325.load(dir)){
            check(r!=null&&r.decisions()==12&&r.completeExperiences()==12,
                  "wrong V326 recovery generation");
        }
        // Valid previous checkpoint is never erased due to a failed new birth.
        try(Zero3RuntimeV325 different=Zero3WorldBirthV326.create(
                EPISODE+999,SEED+1)){
            different.advanceOneDecision();
            boolean blocked=false;
            try{different.save(dir);}catch(java.io.IOException expected){blocked=true;}
            check(blocked,"foreign V326 organism overwrote checkpoint");
        }
        System.out.println("V326 NEWBORN_FAULT_ROLLBACK_12=PASS NO_LEGACY_REWRITE=PASS");
    }
    public static void main(String[] args)throws Exception {
        check(args.length>=2,"phase args");
        switch(args[0]){
            case "prefix":
                try(Zero3RuntimeV325 r=virgin()){
                    run(r,6);r.save(Paths.get(args[1]));
                    run(r,12);r.save(Paths.get(args[1]));
                }
                System.out.println("V326 FRESH_BIRTH_SAVED_12=PASS");
                break;
            case "resume":
                try(Zero3RuntimeV325 r=Zero3RuntimeV325.load(Paths.get(args[1]))){
                    check(r!=null&&r.decisions()==12,"cold restart incorrect");
                    run(r,24);r.save(Paths.get(args[1]));
                    result(Paths.get(args[2]),r);
                }
                System.out.println("V326 FRESH_BIRTH_RESUMED_24=PASS");
                break;
            case "reference":
                try(Zero3RuntimeV325 r=virgin()){
                    run(r,24);result(Paths.get(args[1]),r);
                }
                System.out.println("V326 FRESH_BIRTH_CONTINUOUS_24=PASS");
                break;
            case "fault":
                fault(Paths.get(args[1]));break;
            default:throw new IllegalArgumentException("invalid V326 phase");
        }
    }
}
