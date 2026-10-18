package com.overmind.zero;

import java.util.*;

/** Real perception and actual V325 causal input/replay regression; no scripted actor. */
public final class Zero3UnifiedSensoryV351Regression {
    private static void yes(boolean v,String why){if(!v)throw new AssertionError("V351 "+why);}
    private static void rejected(Runnable r){boolean no=false;try{r.run();}catch(IllegalArgumentException|IllegalStateException ok){no=true;}yes(no,"invalid source accepted");}
    public static void main(String[] args)throws Exception {
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(body,skin);
        for(int i=0;i<96;i++)gel.step(body,skin,new float[64],.025f);
        ScarcityLab room=new ScarcityLab(body.cx+150f,body.cy);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();world.add(room.food);
        UnknownWorld.Entity light=new UnknownWorld.Entity(body.cx+110f,body.cy,0,0);
        world.add(light);
        Zero3SurvivalLedgerV323 vital=new Zero3SurvivalLedgerV323(55f,0f,0f,false);
        Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(351001,0,gel.ticks*.025d,true,body,skin,gel,world,null);
        Zero3CausalVitalBoundaryV344.Before before=Zero3CausalVitalBoundaryV344.before(0,pre,vital);
        Zero3GruSensoryV347.Unified sensed=Zero3GruSensoryV347.unifiedBefore(pre,before,body,skin,room,world);
        float[] x=sensed.neural(),spectral=SensorimotorMind.Senses.spectral8(body,skin,room,world);
        yes(x.length==194&&x[193]==0f&&Math.abs(x[188]-.1f)<1e-6f,"width/availability/real reserve");
        for(int i=0;i<24;i++)yes(x[20+i]==spectral[i],"optics differs from actual physical eye");
        yes(spectral[0]>0f,"near physical light invisible");
        x[20]=999f;yes(sensed.neural()[20]!=999f&&sensed.intact()&&pre.intact(),"snapshot alias");
        // A real opaque BOX between the same retina and light changes photons;
        // no box flag or resource coordinates are passed to the neural core.
        world.add(new UnknownWorld.Entity(body.cx+66f,body.cy,2,5));
        Zero3SensoryFrameV315 obscured=Zero3SensoryFrameV315.capture(351001,0,gel.ticks*.025d,true,body,skin,gel,world,null);
        Zero3GruSensoryV347.Unified occluded=Zero3GruSensoryV347.unifiedBefore(obscured,Zero3CausalVitalBoundaryV344.before(0,obscured,vital),body,skin,room,world);
        yes(occluded.neural()[20]<spectral[0],"opaque physical obstacle does not occlude");
        // Runtime capture rejects a mismatched phase/source, rather than reading future data.
        rejected(()->Zero3GruSensoryV347.unifiedAfter(pre,null,body,skin,room,world));
        Zero3RecurrentBrain old=new Zero3RecurrentBrain();
        yes(Zero3RecurrentBrain.fromCheckpoint(old.checkpointAtBoundaryV345()).sensorWidth()==26,"legacy schema lost");
        Zero3RecurrentBrain brain=new Zero3RecurrentBrain(194);
        brain.choose(sensed.neural(),351002,0,0);
        brain.consequenceFixedClockV350(sensed.neural(),-.01f,false);
        byte[] saved=brain.checkpointAtBoundaryV345();
        Zero3RecurrentBrain restored=Zero3RecurrentBrain.fromCheckpoint(saved);
        yes(Arrays.equals(saved,restored.checkpointAtBoundaryV345()),"GRU194 state mismatch");
        rejected(()->new Zero3RecurrentBrain().restoreAtBoundaryV345(saved));
        byte[] bad=saved.clone();bad[bad.length/2]^=1;
        rejected(()->Zero3RecurrentBrain.fromCheckpoint(bad));
        try(Zero3RuntimeV325 life=Zero3WorldBirthV326.create(351003,0x351123456L)){
            life.enableUnifiedResearchV351();life.enableSegmented();
            for(int i=0;i<32;i++)life.advanceOneDecision();
            Zero3SegmentedArchiveV328.Bundle bundle=life.segmentedSnapshot();
            yes(life.neuralInputWidth()==194&&life.encoderUpdates()==0&&life.gruLearningUpdatesV347()==32,"not single real actor");
            for(int i=0;i<32;i++){
                Zero3FullCausalExperienceV322B e=bundle.experiences().get(i);
                yes(e.hasUnifiedSensesV351()&&e.neuralBeforeV351().length==194,"neural perception absent from replay");
                yes(Arrays.equals(e.encoded(),Zero3FullCausalExperienceV322B.decode(e.encoded()).encoded()),"noncanonical replay");
                yes(e.neuralBeforeV351()[193]==(i==0?0f:1f)&&e.neuralAfterV351()[193]==1f,"efference validity");
                if(i>0){
                    float[] previous=bundle.experiences().get(i-1).neuralAfterV351();
                    yes(Arrays.equals(previous,e.neuralBeforeV351()),"POST -> next PRE not same physical perception");
                }
            }
        }
        System.out.println("V351 UNIFIED_REAL_SENSORY=PASS width=194 immutable=true opticalSourceExact=true opaqueOcclusion=true prePostCausality=true actualActor=true fullNeuralReplay=true legacy26Preserved=true NO_G3_G5_G6_CLAIM");
    }
}
