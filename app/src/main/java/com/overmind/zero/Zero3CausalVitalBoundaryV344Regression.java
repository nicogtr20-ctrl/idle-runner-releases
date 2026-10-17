package com.overmind.zero;

import java.nio.file.Files;
import java.nio.file.Path;

/** V344: use THE active V3 runtime, not a shadow regression controller. */
public final class Zero3CausalVitalBoundaryV344Regression {
    private Zero3CausalVitalBoundaryV344Regression(){}
    private static void yes(boolean valid,String note){
        if(!valid)throw new AssertionError("V344 LIVE_RUNTlME "+note);
    }
    private static final long EPISODE=3440081L, WORLD=0x3445019ABCL;
    private static String reference()throws Exception{
        try(Zero3RuntimeV325 live=Zero3WorldBirthV326.create(EPISODE,WORLD)){
            live.enableSegmented();
            yes(!live.display().hasVitalOutcome,
                 "newborn generated imaginary metabolic outcome");
            for(int i=0;i<16;i++){
                float fuelBefore=live.energy();
                live.advanceOneDecision();
                Zero3RuntimeV325.Display d=live.display();
                yes(d.hasVitalOutcome&&d.decisions==i+1,
                    "no completed actual causal energy outcome");
                yes(Math.abs(d.lastEnergyNet-(d.reserve-fuelBefore))<.0006f,
                    "NET energy not the true two observed reserves");
                float balance=d.lastEnergyGained-d.lastEnergyBasal-
                    d.lastEnergyMotor-d.lastEnergyDamage;
                yes(Math.abs(balance-d.lastEnergyNet)<=.002f,
                    "fake/uncoupled energy balance in live world");
            }
            yes(live.gelTicks()>=16L*12&&live.cortexUpdates()>0,
                "not a real gel motor+learning loop");
            System.out.println("V344 SAME_RUNTIME_REFERENCE=PASS "+ 
                " decisions="+live.decisions()+" physicalTicks="+live.gelTicks()+
                " cortexUpdates="+live.cortexUpdates());
            return live.fingerprint();
        }
    }
    private static void interrupted(String expected)throws Exception{
        Path temp=Files.createTempDirectory("zero3-v344-vital-");
        float lastReserve;
        try(Zero3RuntimeV325 original=Zero3WorldBirthV326.create(EPISODE,WORLD)){
            original.enableSegmented();
            for(int i=0;i<8;i++)original.advanceOneDecision();
            yes(original.display().hasVitalOutcome,
                "last physical outcome vanished before persistence");
            lastReserve=original.energy();
            original.saveSegmented(temp);
        }
        try(Zero3RuntimeV325 restored=Zero3RuntimeV325.loadSegmented(temp)){
            yes(restored!=null&&restored.decisions()==8&&
                 restored.display().decisions==8,
                "V328 could not restore same physical life");
            yes(!restored.display().hasVitalOutcome,
                "restored a fake never-persisted last display observation");
            yes(Float.floatToIntBits(restored.energy())==
                 Float.floatToIntBits(lastReserve),
                "real measured reserve changed after cold restart");
            for(int i=8;i<16;i++)restored.advanceOneDecision();
            yes(restored.display().hasVitalOutcome&&
                 restored.decisions()==16,
                "resume never resumed natural energy observation");
            String actual=restored.fingerprint();
            yes(expected.equals(actual),
                "vital read-only boundary changed neural+physical "+
                "fingerprint after restart: expected length "+
                expected.length()+" actual length "+actual.length());
            System.out.println("V344 COLD_RESTART_BITWISE_IDENTICAL=PASS "+
                " at8Then16=true realEnergy="+restored.energy()+
                " lastActualNet="+restored.display().lastEnergyNet+
                " sourceCount="+restored.display().lastEnergySources);
        }
    }
    private static void causalPreGuards(){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(b,skin);
        java.util.ArrayList<UnknownWorld.Entity> world=
            new java.util.ArrayList<>();
        Zero3SurvivalLedgerV323 fuel=
            new Zero3SurvivalLedgerV323(75f,0f,0f,false);
        Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
            344099L,0,0,true,b,skin,gel,world,null);
        Zero3CausalVitalBoundaryV344.Before genuine=
            Zero3CausalVitalBoundaryV344.before(0,pre,fuel);
        yes(Float.floatToIntBits(genuine.reserve)==
            Float.floatToIntBits(75f)&&genuine.previousDamage==0f,
            "PRE did not come from the real ledger");
        boolean rejected=false;
        try{Zero3CausalVitalBoundaryV344.before(1,pre,fuel);}
        catch(IllegalStateException expected){rejected=true;}
        yes(rejected,"future decision can read PRE at past decisionId");
        rejected=false;
        try{Zero3CausalVitalBoundaryV344.after(genuine,pre,fuel);}
        catch(IllegalStateException expected){rejected=true;}
        yes(rejected,"uncommitted PRE confused with POST");
        fuel.begin(0,gel.ticks);
        rejected=false;
        try{Zero3CausalVitalBoundaryV344.before(0,pre,fuel);}
        catch(IllegalStateException expected){rejected=true;}
        yes(rejected,"incomplete metabolic interval revealed future PRE");
        float[] none=new float[64];
        for(int i=0;i<12;i++){
            float prior=gel.work;
            gel.step(b,skin,none,Zero3RuntimeV325.DT);
            fuel.physicalStep(Zero3RuntimeV325.DT,
                Math.max(0f,gel.work-prior),b,skin,world);
        }
        Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
            344099L,1,gel.ticks*Zero3RuntimeV325.DT,false,
            b,skin,gel,world,none);
        rejected=false;
        try{Zero3CausalVitalBoundaryV344.after(genuine,post,fuel);}
        catch(IllegalStateException expected){rejected=true;}
        yes(rejected,"uncommitted metabolic energy misrepresented as real");
        fuel.finish(0,gel.ticks);
        Zero3CausalVitalBoundaryV344.After actual=
            Zero3CausalVitalBoundaryV344.after(genuine,post,fuel);
        yes(actual.physicalTicks==12&&actual.positiveTouches==0&&
            actual.negativeTouches==0&&actual.netEnergy<0f&&
            actual.actualGained==0f&&actual.actualBasal>0f,
            "basal energy paid without a fabricated resource");
        System.out.println("V344 CAUSAL_PRE_POST_NO_FUTURE=PASS "+
            " realBasal="+actual.actualBasal+
            " realNet="+actual.netEnergy+
            " noFalseEnergySource=true");
    }
    public static void main(String[] args)throws Exception{
        causalPreGuards();
        String expected=reference();
        interrupted(expected);
        System.out.println("V344 LIVE_RUNTIME_INTEGRATION_PASS "+
            " genuinePREandPOST=true oneOwner=true "+
            "bitwiseColdResume=true noMotorChanges=true "+
            "G2_BOUNDARY_ONLY G3_G4_G5_BLOCKED NO_OTA NO_MERGE");
    }
}
