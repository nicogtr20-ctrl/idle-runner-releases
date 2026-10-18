package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Scientific contract for V3.27 partial vital interoception:
 * only measured PRE fuel + harm from PREVIOUS completed real physics enters
 * the same recurrent encoder which actually chooses the next motor intent.
 * No fake pain reward and no new or uncheckpointed neural parameters.
 */
public final class Zero3VitalV327Regression {
    private Zero3VitalV327Regression(){}
    private static void yes(boolean state,String error) {
        if(!state)throw new AssertionError("V327 vital "+error);
    }
    public static void main(String[] args)throws Exception {
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(body,skin);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        Zero3SurvivalLedgerV323 energy=
            new Zero3SurvivalLedgerV323(100f,0f,0f,false);
        Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
            3270101L,0,gel.ticks*Zero3RuntimeV325.DT,
            true,body,skin,gel,world,null);
        float[] physical=Zero3AutonomousLoopV317.encode(pre);
        float[] fullFuel=Zero3AutonomousLoopV317.encodeVital(
            pre,energy.reserve()/100f,energy.previousDecisionDamage());
        float[] lowFuel=Zero3AutonomousLoopV317.encodeVital(pre,.10f,0f);
        yes(Arrays.equals(physical,Zero3AutonomousLoopV317.encode(pre)),
            "legacy physical encoder modified");
        yes(Float.floatToRawIntBits(fullFuel[18])!=
            Float.floatToRawIntBits(lowFuel[18]),
            "real reserve cannot reach recurrent actor");
        for(int i=0;i<18;i++)yes(fullFuel[i]==physical[i],
                                   "vital sense rewrote unrelated somatosensory ray");
        yes(fullFuel[19]==.75f*physical[19],
            "zero prior contact harm invented pain");
        Zero3AutonomousLoopV317.Encoder high=
            new Zero3AutonomousLoopV317.Encoder(0x327L);
        Zero3AutonomousLoopV317.Encoder weak=
            new Zero3AutonomousLoopV317.Encoder(0x327L);
        high.before(pre,energy.reserve()/100f,
                    energy.previousDecisionDamage());
        weak.before(pre,.10f,0f);
        float[] highAction=high.choose(),weakAction=weak.choose();
        boolean changed=false;
        for(int i=0;i<4;i++)if(Float.floatToRawIntBits(highAction[i])!=
                                  Float.floatToRawIntBits(weakAction[i]))
            changed=true;
        yes(changed,"recurrent motor decision ignores fuel");
        yes(high.updates()==0&&weak.updates()==0,
            "PRE decision must not credit future learning");
        boolean rejected=false;
        Zero3SensoryFrameV315 post=Zero3SensoryFrameV315.capture(
            3270101L,1,gel.ticks*Zero3RuntimeV325.DT+Zero3RuntimeV325.DT,
            false,body,skin,gel,world,null);
        try{Zero3AutonomousLoopV317.encodeVital(post,.5f,0f);}
        catch(IllegalArgumentException expected){rejected=true;}
        yes(rejected,"POST/future sensed before physical action");
        // Build one physically debited survival decision: nothing is
        // assigned by hand to damage counter or physical object contact.
        UnknownWorld.Entity hazard=
            new UnknownWorld.Entity(skin.x[24],skin.y[24],1,1);
        world.add(hazard);
        energy.begin(0,gel.ticks);
        float basalBefore=energy.reserve();
        for(int i=0;i<12;i++){
            float previous=gel.work;
            gel.step(body,skin,new float[64],Zero3RuntimeV325.DT);
            energy.physicalStep(Zero3RuntimeV325.DT,
                Math.max(0f,gel.work-previous),body,skin,world);
        }
        energy.finish(0,gel.ticks);
        yes(energy.decisions()==1&&energy.reserve()<basalBefore,
            "real basal drain not observed");
        yes(Float.isFinite(energy.previousDecisionDamage())&&
            energy.previousDecisionDamage()>=0f,
            "prior contact loss missing");
        float[] withLastHarm=Zero3AutonomousLoopV317.encodeVital(
            Zero3SensoryFrameV315.capture(
                3270101L,1,gel.ticks*Zero3RuntimeV325.DT,true,
                body,skin,gel,world,null),
            energy.reserve()/100f,energy.previousDecisionDamage());
        yes(withLastHarm.length==Zero3AutonomousLoopV317.INPUTS,
            "encoder checkpoint dimensions unexpectedly changed");
        System.out.println("V327 VITAL_FROM_ACTUAL_METABOLISM=PASS "+
            "RECURRING_ACTION_CHANGED=PASS NO_FUTURE_LEAK=PASS "+
            "PREVIOUS_REAL_HARM="+energy.previousDecisionDamage()+
            " RESERVE="+energy.reserve()+" PHYSICAL_STEPS="+gel.ticks);
    }
}
