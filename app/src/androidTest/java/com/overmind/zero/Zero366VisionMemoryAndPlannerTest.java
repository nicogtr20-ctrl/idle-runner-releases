package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * V366 original Android: no new brain, keep 194 senses, all V354 memory
 * options, old saved weights and GRU authority.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero366VisionMemoryAndPlannerTest {
    private static float[] sight(boolean blueWest) {
        float[] obs=new float[194];
        obs[20+(blueWest?4:0)]=.8f;       // spectrum 0
        obs[28+(blueWest?0:4)]=.8f;       // spectrum 1
        obs[188]=1f;                      // real interoception
        return obs;
    }
    private static float[] action(){return new float[]{1f,0f,.5f,.5f,.3f};}
    @Test public void leftBlueRightRedAndMirroredSituationsStayDistinct(){
        float[] a=sight(true),b=sight(false);
        assertFalse(Arrays.equals(a,b));
        assertArrayEquals(Zero3VisionContextV361.fromSensory194(a),
            Zero3VisionContextV361.fromSensory194(b),0f); // reproduce BUG
        float[] va=Zero3VisionContextV366.fromSensory194(a);
        float[] vb=Zero3VisionContextV366.fromSensory194(b);
        assertEquals(15,va.length);
        for(int i=0;i<12;i++){
            assertEquals(Zero3VisionContextV361.fromSensory194(a)[i],va[i],0);
            assertEquals(Zero3VisionContextV361.fromSensory194(b)[i],vb[i],0);
        }
        assertTrue("spectrum0 WEST must remain WEST for context",va[12]<0);
        assertTrue("spectrum1 EAST must remain EAST for context",va[13]>0);
        assertTrue("spectrum0 mirrored EAST",vb[12]>0);
        assertTrue("spectrum1 mirrored WEST",vb[13]<0);
        assertTrue("old 12D option has mirror-risk in A",
            Zero3ExecutiveV354.mirroredOldContextRiskV366(va));
        assertTrue("old 12D option has mirror-risk in B",
            Zero3ExecutiveV354.mirroredOldContextRiskV366(vb));
        float[] onlyOne=sight(true);
        onlyOne[28]=0f;
        assertFalse("single observed wavelength not falsely quarantined",
            Zero3ExecutiveV354.mirroredOldContextRiskV366(
                Zero3VisionContextV366.fromSensory194(onlyOne)));
    }
    @Test public void legacyV361CheckpointMigratesWithoutForgettingLearnedValue()throws Exception {
        Zero3ExecutiveV354 original=new Zero3ExecutiveV354();
        float[] before=sight(true);
        for(int d=0;d<31;d++){
            original.propose(before,action(),d,false);
            original.complete(.8f,3f,0f,.01f,false,false,null,true);
        }
        assertEquals(31,original.snapshot().decisions);
        byte[] old=original.encodePriorV361ForQA();
        assertEquals(361,java.nio.ByteBuffer.wrap(old).getInt());
        Zero3ExecutiveV354 upgraded=Zero3ExecutiveV354.decode(old);
        assertArrayEquals("V361 untouched fields stay byte-identical",
            old,upgraded.encodePriorV361ForQA());
        // Authentic V361 weight format contains twelve projection weights
        // plus ONE scalar bias; it never had V366 horizontal or V367 vertical axes.
        Field weightsField=Zero3ExecutiveV354.class.getDeclaredField("valueWeights");
        weightsField.setAccessible(true);
        float[] previous=(float[])weightsField.get(original);
        float[] migrated=(float[])weightsField.get(upgraded);
        for(int i=0;i<12;i++)
            assertEquals("historic V361 weight "+i,previous[i],migrated[i],0f);
        assertEquals("historic scalar bias preserved",previous[18],migrated[18],0f);
        for(int i=12;i<18;i++)
            assertEquals("new channels must start neutral on V361 import",0f,migrated[i],0f);
        upgraded.propose(before,action(),31,false);
        float[] originalTwelve=Zero3VisionContextV366.originalV361Padded(before);
        float predicted=migrated[18];
        for(int i=0;i<12;i++)predicted+=originalTwelve[i]*migrated[i];
        predicted=Math.max(-1f,Math.min(1f,predicted));
        assertEquals("prior-context learned value is unchanged on V361 migration",
            predicted,upgraded.snapshot().value,1e-7f);
        upgraded.abort();
        byte[] modern=upgraded.encode();
        assertEquals(367,java.nio.ByteBuffer.wrap(modern).getInt());
        Zero3ExecutiveV354 reloaded=Zero3ExecutiveV354.decode(modern);
        assertEquals(31,reloaded.snapshot().decisions);
        assertArrayEquals(modern,reloaded.encode());
    }
    @Test public void priorOptionsKeepOriginalContextsAndFutureOptionsGetNewSchema()throws Exception {
        Zero3ExecutiveV354 trained=new Zero3ExecutiveV354();
        float[] before=sight(true);
        for(int d=0;d<32;d++){
            trained.propose(before,action(),d,false);
            trained.complete(.8f,3f,0f,.01f,false,false,null,true);
        }
        assertTrue(trained.snapshot().skills.length>=1);
        Field f=Zero3ExecutiveV354.class.getDeclaredField("options");f.setAccessible(true);
        @SuppressWarnings("unchecked")
        ArrayList<Object> options=(ArrayList<Object>)f.get(trained);
        Object o=options.get(0);
        Field type=o.getClass().getDeclaredField("visionSchemaV366");type.setAccessible(true);
        Field context=o.getClass().getDeclaredField("context");context.setAccessible(true);
        // This is a QA fixture representing an EXISTING V361 learnt option,
        // not a production migration which would change trained memories.
        type.setInt(o,361);
        float[] old=Zero3VisionContextV366.originalV361Padded(before);
        System.arraycopy(old,0,(float[])context.get(o),0,old.length);
        byte[] legacy=trained.encodePriorV361ForQA();
        Zero3ExecutiveV354 converted=Zero3ExecutiveV354.decode(legacy);
        assertEquals(trained.snapshot().skills.length,converted.snapshot().skills.length);
        assertArrayEquals(legacy,converted.encodePriorV361ForQA());
        byte[] newData=converted.encode();
        Zero3ExecutiveV354 after=Zero3ExecutiveV354.decode(newData);
        assertEquals(converted.snapshot().skills[0].id,after.snapshot().skills[0].id);
        assertEquals(converted.snapshot().skills[0].train,after.snapshot().skills[0].train);
        assertArrayEquals(newData,after.encode());
    }
    @Test public void trustedEnergyAndHarmCanImproveActorProposalWithoutBlindCostGuess()throws Exception{
        Zero3CognitionV353 model=new Zero3CognitionV353();
        // Only this test instrument fixes measured validation statistics and
        // trained-value readout. Production model still learns ONLY from
        // original physical PRE/POST measurements and actual heldout errors.
        Field count=Zero3CognitionV353.class.getDeclaredField("validations");
        count.setAccessible(true);count.setLong(model,80L);
        for(String k:new String[]{"baseline","error","spread"}){
            Field f=Zero3CognitionV353.class.getDeclaredField(k);f.setAccessible(true);
            double[] a=(double[])f.get(model);
            Arrays.fill(a,0);
            if(k.equals("baseline")){a[0]=100;a[1]=100;a[2]=1e-9;}
            if(k.equals("error")){a[0]=1;a[1]=1;a[2]=5.6178385e-7*80;}
        }
        assertTrue(model.trusted(0));assertTrue(model.trusted(1));
        assertFalse("physical cost head remains demonstrably untrusted",model.trusted(2));
        Field championField=Zero3CognitionV353.class.getDeclaredField("champion");
        championField.setAccessible(true);Object champion=championField.get(model);
        Field inputsField=champion.getClass().getDeclaredField("in");
        Field outputsField=champion.getClass().getDeclaredField("out");
        Field unitsField=champion.getClass().getDeclaredField("units");
        inputsField.setAccessible(true);outputsField.setAccessible(true);unitsField.setAccessible(true);
        float[][] in=(float[][])inputsField.get(champion);
        float[][] out=(float[][])outputsField.get(champion);
        int units=unitsField.getInt(champion);
        for(int e=0;e<3;e++){
            Arrays.fill(in[e],0);Arrays.fill(out[e],0);
            in[e][194]=1.5f; // outcome learned from own ACTION X (not food colour)
            out[e][0*(units+1)]=1f; // measured reserve outcome head
        }
        float[] observation=new float[194];
        float[][] realActorProposals={
            {-1f,0f,.5f,.5f,.3f},
            {1f,0f,.5f,.5f,.3f},
            {0f,1f,.5f,.5f,.3f}
        };
        assertEquals("energy and harm are reliable and beat verified cost uncertainty",
            1,model.select(observation,realActorProposals));
        Field costError=Zero3CognitionV353.class.getDeclaredField("error");
        costError.setAccessible(true);
        ((double[])costError.get(model))[2]=40;
        assertEquals("real cost uncertainty is large, so KEEP ORIGINAL GRU SAMPLE",
            0,model.select(observation,realActorProposals));
        Field baselineField=Zero3CognitionV353.class.getDeclaredField("baseline");
        baselineField.setAccessible(true);
        ((double[])baselineField.get(model))[0]=0;
        assertEquals("without reliable energy evidence never override GRU",
            0,model.select(observation,realActorProposals));
    }
    @Test public void costUncertaintyCannotBeIgnoredOrRelabelledAsZero(){
        assertEquals(Float.POSITIVE_INFINITY,
            Zero3CognitionV353.conservativeCostMarginV366(0,63),0);
        assertEquals(Float.POSITIVE_INFINITY,
            Zero3CognitionV353.conservativeCostMarginV366(Double.NaN,120),0);
        float seen=Zero3CognitionV353.conservativeCostMarginV366(
            5.6178385e-7*100,100);
        assertTrue("real V363 physical cost mismatch must have a positive margin",seen>.002f);
        assertTrue("more uncertain candidate needs larger proven energy/harm advantage",
            Zero3CognitionV353.conservativeCostMarginV366(.01,100)>seen);
    }
}
