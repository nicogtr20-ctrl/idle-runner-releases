package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Independent V3.25 terminal life: no fake survival after actual energy zero. */
public final class Zero3RuntimeV325TerminalRegression {
    private static void check(boolean condition,String name){
        if(!condition)throw new AssertionError("V325 terminal "+name);
    }
    public static void main(String[] args)throws Exception{
        if(args.length!=1)throw new IllegalArgumentException("output dir");
        Path privateSlot=Paths.get(args[0]);
        Zero3MotorImpulseResponseRegression.Scenario physics=
            new Zero3MotorImpulseResponseRegression.Scenario(1,.07f,0);
        ScarcityLab room=new ScarcityLab(physics.body.cx+200,physics.body.cy);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        objects.add(room.food);
        try(Zero3RuntimeV325 runtime=Zero3RuntimeV325.newborn(211333L,
                0x333333L,physics.body,physics.skin,physics.gel,
                objects,room,.0001f,0f)){
            runtime.advanceOneDecision();
            check(runtime.decisions()==1&&runtime.ended()&&runtime.energy()==0,
                  "energy at terminal threshold");
            boolean rejected=false;
            try{runtime.advanceOneDecision();}
            catch(IllegalStateException expected){rejected=true;}
            check(rejected&&runtime.decisions()==1&&
                  runtime.completeExperiences()==1,
                  "terminal credited additional decision");
            runtime.save(privateSlot);
        }
        try(Zero3RuntimeV325 restored=Zero3RuntimeV325.load(privateSlot)){
            check(restored!=null&&restored.ended()&&restored.energy()==0&&
                    restored.decisions()==1,
                  "restart resurrected dead session");
            boolean rejected=false;
            try{restored.advanceOneDecision();}
            catch(IllegalStateException expected){rejected=true;}
            check(rejected,"cold restore executed after physical death");
        }
        // The same legacy/other-life file must NOT be overwritten by an
        // unrelated V3 episode, and NO snapshot gets reset implicitly.
        System.out.println("ZERO3 V325 TERMINAL_FUEL_EXHAUSTION=PASS "+
            "STOP_EXACTLY_ONE_DECISION=PASS PERSISTENT_DEATH=PASS");
    }
}
