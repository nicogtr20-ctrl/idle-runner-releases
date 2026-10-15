package com.overmind.zero;

import java.lang.reflect.Field;
import java.util.Arrays;

/** Verified physical interoception: senses -> lived prediction -> ONE cortex. */
public final class FluidInteroceptionRegression {
    private static void check(boolean yes,String why){if(!yes)throw new AssertionError(why);}
    private static float diff(float[] a,float[] b){
        float sum=0;for(int i=0;i<a.length;i++)sum+=Math.abs(a[i]-b[i]);return sum;
    }
    private static float max(float[] a){float m=0;for(float x:a)m=Math.max(m,Math.abs(x));return m;}
    public static void main(String[] args)throws Exception{
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 fluid=new SymbioteMatterV1();
        fluid.reset(b,skin);
        FluidInteroceptionV1 senses=new FluidInteroceptionV1();
        float[] initial=FluidInteroceptionV1.sense(b,skin,fluid);
        check(initial.length==74*7,"did not sense all 74 real material parcels");
        senses.feed(initial,b,fluid);
        check(senses.localMap().length==74*7&&senses.summary().length==16*7,
            "lost real interior topology after sector pooling");
        check(max(senses.localMap())>.02f,
            "virgin fluid should have real density/boundary distances");
        float[] old=senses.localMap();
        // Change only one REAL interior fluid velocity, not a game target.
        fluid.vx[12]+=110f;
        float[] moved=FluidInteroceptionV1.sense(b,skin,fluid);
        check(Math.abs(moved[(MotorBrain.N+12)*7+FluidInteroceptionV1.AGITATION]-
               old[(MotorBrain.N+12)*7+FluidInteroceptionV1.AGITATION])>.2f,
            "physical internal fluid acceleration invisible");
        fluid.vx[12]-=110f;
        // Internal packing is sensed even when the outer skin does NOT move.
        for(int i=0;i<8;i++){fluid.x[i]=fluid.x[0];fluid.y[i]=fluid.y[0];}
        float[] compressed=FluidInteroceptionV1.sense(b,skin,fluid);
        check(diff(initial,compressed)>.1f,
            "compressing real internal material produced no interoception");
        check(senses.choiceBias(7)==0f,"untrained sensor chooses a movement");
        // Every interval must learn ONLY from the selected action.
        float[] muscle=new float[16*4];muscle[0]=.85f;muscle[4]=.65f;
        senses.feed(initial,b,fluid);senses.chosen(7);
        for(int i=0;i<25;i++)fluid.step(b,skin,muscle,.025f);
        senses.consequence(FluidInteroceptionV1.sense(b,skin,fluid),b,fluid,7,
            0f,0f,true,false);
        check(senses.transitions==1,"missed real liquid action outcome");
        check(senses.choiceBias(7)==0f,"prediction without validation has motor authority");
        String saved=senses.encode();
        FluidInteroceptionV1 restored=new FluidInteroceptionV1();
        check(restored.restore(saved)&&restored.encode().equals(saved),
            "new interoceptive memory cannot survive Android save/restore");
        check(!restored.restore("fli1|invalid")&&restored.encode().equals(saved),
            "corrupted saved fluid damaged learned network");
        restored.resetLife();
        check(restored.encode().equals(saved),"death erased internal-body learning");

        // Readouts must be wired into LIVE single-brain scores after valid
        // holdout, without adding physical movement as an intrinsic reward.
        SensorimotorMind brain=new SensorimotorMind();
        brain.attachActiveCore(new RecurrentCoreV22());
        brain.attachFluidInteroception(senses,fluid);
        float[] no=brain.liveBodyEvidence(7)==0?new RecurrentCoreV22().primaryScores(new float[16]):null;
        check(no!=null,"newborn inner sense changed live recurrent policy");
        Field val=FluidInteroceptionV1.class.getDeclaredField("values");
        val.setAccessible(true);
        float[] weights=(float[])val.get(senses);
        weights[7*(FluidInteroceptionV1.SUMMARY+1)+FluidInteroceptionV1.SUMMARY]=2f;
        check(brain.liveBodyEvidence(7)==0f,"unvalidated inner-body bias influences action");
        senses.validations=50;senses.unchangedError=.2f;senses.predictionError=.01f;
        float gain=brain.liveBodyEvidence(7);
        check(gain>.05f&&Math.abs(brain.liveBodyEvidence(5))<.0001f,
            "trusted internal feedback not delivered to real brain");
        float[] evidence=new float[16];evidence[7]=gain;
        float[] score=new RecurrentCoreV22().primaryScores(evidence);
        check(Math.abs(score[7]-no[7]-.40f*gain)<.0001f,
            "primary recurrent scorer dropped inner proprioception");
        // Legacy trained 14x4 motor head must migrate EXACTLY, with
        // zero impact from the newly attached 7 channels until learned.
        SymbioteMotorHeadV1 oldHead=new SymbioteMotorHeadV1();
        float[] local=new float[DistributedBodySense.FEATURES],recurrent=new float[RecurrentCoreV22.Z];
        oldHead.chosen(4,local,recurrent,0f);
        float[] oldCommands=oldHead.commands().clone();
        String newSave=oldHead.encode(),prefix=newSave.substring(0,newSave.indexOf('|',5)+1);
        String[] chunks=newSave.split("\\|",-1);
        StringBuilder prior=new StringBuilder("smh1");
        for(int i=1;i<=5;i++)prior.append('|').append(chunks[i]);
        for(int c=0;c<4;c++)for(int j=0;j<14;j++)
            prior.append('|').append(chunks[6+c*21+j]);
        SymbioteMotorHeadV1 migrated=new SymbioteMotorHeadV1();
        check(migrated.restore(prior.toString()),"actual pre-OTA151 motor save cannot migrate");
        migrated.chosen(4,local,recurrent,0f);
        check(diff(oldCommands,migrated.commands())<.0001f,
            "migration silently changed actual old cortex motor commands");
        float[] realSummary=senses.summary();
        migrated.chosen(4,local,recurrent,0f,realSummary);
        check(diff(oldCommands,migrated.commands())<.0001f,
            "new virgin internal connections changed trained old muscles");
        check(migrated.restore(newSave),"current extended cortex save cannot restore");
        check(migrated.encode().startsWith("smh1|"),"motor save key unexpectedly broken");
        System.out.println("PASS 151: 74 particles x 7, density/compression, true cortex feedback, heldout policy, migration, persistence");
    }
}
