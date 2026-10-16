package com.overmind.zero;

/**
 * OTA165: duration must learn whether the body follows its OWN selected
 * direction. Laboratory movements are outcomes only, not a gameplay gait.
 */
public final class Zero165MotorPersistenceRegression {
    private static void ok(boolean v,String message){
        if(!v)throw new AssertionError("ZERO165 duration: "+message);
    }
    private static String asMd160(String current){
        String[] raw=current.split("\\|",-1);
        raw[0]="md160";
        for(int i=5;i<raw.length;i++){
            String[] row=raw[i].split(",",-1);
            raw[i]=row[0]+","+row[1]+","+row[2]+","+row[3];
        }
        return String.join("|",raw);
    }
    public static void main(String[] args){
        MotorDurationV22 learned=new MotorDurationV22();
        float[] neutral=new float[RecurrentCoreV22.A];
        int earlyShort=0,lateShort=0,longCount=0;
        // Action 0 is self-selected +X. A one-tick oscillation keeps moving
        // backwards, while sustained genuine world motion heads forward.
        // No brain reward, colour, food, target or privileged motor forces.
        for(int trial=0;trial<1500;trial++){
            learned.resetLife();
            float x=0f;
            int chosen=learned.select(0,0,neutral,false,.8f,0f,x,0f);
            ok(chosen==0,"duration selected an independent direction");
            int ticks=learned.selectedTicks();
            ok(ticks==1||ticks==2||ticks==4,"invalid learned time option");
            if(trial<100&&ticks==1)earlyShort++;
            if(trial>=1200&&ticks==1)lateShort++;
            if(ticks>=2)longCount++;
            for(int i=0;i<ticks;i++){
                x+=(ticks==1?-3f:3f);
                learned.interval(0,x,0f,0f,0f,.01f,0f);
                if(i<ticks-1)
                    ok(learned.select(0,0,neutral,false,.8f,0f,x,0f)==0,
                        "safe continuation changed a chosen action");
            }
            // Next decision completes the old observed option; no fabricated
            // success and no hidden reset of historical sample counters.
            learned.select(0,0,neutral,false,.8f,0f,x,0f);
        }
        ok(longCount>1000,"old md160 one-tick preference still dominates");
        ok(lateShort<60,"heldout directional learning still favors reversal");
        ok(learned.directionalSamples(0,1)>20 &&
            learned.directionalHoldouts(0,1)>5,
            "controlled short-duration comparison was never held out");
        ok(learned.directionalSamples(0,2)+
            learned.directionalSamples(0,1)>250,
            "no living duration outcomes were observed");

        String saved=learned.encode();
        ok(saved.startsWith("md165|"),"new directional calibration not persisted");
        MotorDurationV22 restored=new MotorDurationV22();
        ok(restored.restore(saved)&&saved.equals(restored.encode()),
            "new directional holdout data failed exact save/reload");
        String before=restored.encode();
        ok(!restored.restore(saved+"corrupt")&&before.equals(restored.encode()),
            "malformed snapshot corrupted learned duration");

        MotorDurationV22 migrated=new MotorDurationV22();
        String legacy=asMd160(saved);
        ok(migrated.restore(legacy),"all old md160 save slots must load intact");
        ok(migrated.evaluated()==learned.evaluated(),
            "old duration experiences disappeared on migration");
        for(int k=0;k<MotorDurationV22.OPTIONS;k++){
            ok(migrated.trained(0,k)==learned.trained(0,k)&&
                migrated.heldout(0,k)==learned.heldout(0,k),
                "historical learned energy and contact outcomes discarded");
            ok(migrated.directionalSamples(0,k)==0&&
                migrated.directionalHoldouts(0,k)==0,
                "old md160 score was falsely relabeled as directional skill");
        }
        String clean=migrated.encode();
        ok(clean.startsWith("md165|")&&new MotorDurationV22().restore(clean),
            "migration did not produce restart-safe new records");

        // A real danger interrupts a sustained option even if the preference
        // was learned. A strong new recurrent proposal overrides too.
        for(int i=0;i<100;i++){
            restored.resetLife();
            restored.select(0,0,neutral,false,.8f,0f,0f,0f);
            if(restored.selectedTicks()>1){
                int emergency=restored.select(6,6,neutral,false,.8f,7f,1f,0f);
                ok(emergency==6,"injury failed to interrupt a motor hold");
                break;
            }
        }
        System.out.println("ZERO165 PASS: short early="+earlyShort+
            " late="+lateShort+" long="+longCount+
            "; genuine directional holdout, md160 migration, corruption and injury");
    }
}
