package com.overmind.zero;

import java.io.*;
import java.nio.file.*;

/**
 * Research archive capacity stop must preserve the actual organism instead
 * of trying to write the impossible 257th V322B full experience.
 * This is a LIMITATION test, not the required scalable 2,000-life gate.
 */
public final class Zero3CapacityV327Regression {
    private static void ok(boolean valid,String reason){
        if(!valid)throw new AssertionError("V327 capacity "+reason);
    }
    public static void main(String[] args)throws Exception {
        if(args.length!=1)throw new IllegalArgumentException("private V3 path");
        Path root=Paths.get(args[0]);
        long episode=32724020261017L;
        String expected;
        try(Zero3RuntimeV325 living=Zero3WorldBirthV326.create(
                      episode,0x327CADAB223L)){
            while(living.decisions()<240){
                living.advanceOneDecision();
                if(living.decisions()==120)living.save(root);
            }
            ok(!living.ended()&&living.atArchiveCapacity()&&
                living.completeExperiences()==240,"wrong threshold");
            living.save(root);
            expected=living.fingerprint();
            boolean prevented=false;
            try{living.advanceOneDecision();}
            catch(IllegalStateException correct){prevented=true;}
            ok(prevented&&living.decisions()==240&&
                living.completeExperiences()==240,
                "over-limit experience got credited to unsavable life");
        }
        try(Zero3RuntimeV325 restored=Zero3RuntimeV325.load(root)){
            ok(restored!=null&&restored.decisions()==240&&
                restored.atArchiveCapacity()&&restored.episode()==episode&&
                expected.equals(restored.fingerprint()),
                "last valid physical/neural/metabolic journal lost after restart");
            boolean prevented=false;
            try{restored.advanceOneDecision();}
            catch(IllegalStateException correct){prevented=true;}
            ok(prevented,"restarted body bypassed capacity gate");
        }
        Zero3RoomArchiveV324 disk=new Zero3RoomArchiveV324(root);
        ok(disk.load()!=null&&disk.generation()==2&&
                disk.load().life.joint.experienceCount()==240,
                "two-generation V3 journal not durable at 240");
        System.out.println("V327 ACTUAL_240_EXPERIENCES_PERSIST=PASS "+
            "OVER_CAPACITY_REJECTED=PASS COLD_RESTART_NO_CREDIT=PASS "+
            "NO_SILENT_BRAIN_RESET=PASS");
    }
}
