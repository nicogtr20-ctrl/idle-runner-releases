package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.ByteBuffer;
import static org.junit.Assert.*;

/**
 * V368 compatibility and science-safety regression.
 * Does not invent resource coordinates, fixed color preferences or a gait.
 * Tests the existing V354 memory and V353/V354 uncertainty interfaces.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero368CausalIntegrationTest {
    private static float[] vertical(boolean sourceZeroNorth) {
        float[] x=new float[194];
        x[20+(sourceZeroNorth?2:6)]=.8f;
        x[28+(sourceZeroNorth?6:2)]=.8f;
        x[188]=.7f;
        return x;
    }
    private static float[] proposal(){return new float[]{.6f,.8f,.5f,.5f,.3f};}
    @Test public void verticalSourcesRemainDistinctWithoutChangingOldVisualBytes(){
        float[] a=vertical(true),b=vertical(false);
        float[] va=Zero3VisionContextV367.fromSensory194(a);
        float[] vb=Zero3VisionContextV367.fromSensory194(b);
        assertEquals(18,va.length);
        assertArrayEquals("V366 wrongly aliases these mirrored north/south sources",
            Zero3VisionContextV366.fromSensory194(a),
            Zero3VisionContextV366.fromSensory194(b),1e-7f);
        assertArrayEquals(Arrays.copyOf(va,15),
            Zero3VisionContextV366.fromSensory194(a),0f);
        assertArrayEquals(Arrays.copyOf(vb,15),
            Zero3VisionContextV366.fromSensory194(b),0f);
        assertTrue("frequency 0 physically NORTH",va[15]>.09f);
        assertTrue("frequency 1 physically SOUTH",va[16]<-.09f);
        assertTrue("frequency 0 mirrored SOUTH",vb[15]<-.09f);
        assertTrue("frequency 1 mirrored NORTH",vb[16]>.09f);
        assertTrue("V366 memory is ambiguous on opposite vertical signatures",
            Zero3ExecutiveV354.verticalOldContextRiskV367(va));
        assertTrue(Zero3ExecutiveV354.verticalOldContextRiskV367(vb));
        float[] one=vertical(true);one[28+6]=0;
        assertFalse("single source must not be quarantined",
            Zero3ExecutiveV354.verticalOldContextRiskV367(
                Zero3VisionContextV367.fromSensory194(one)));
    }
    @Test public void trueV366SavedOptionsPreserveBytesBiasAndPreviousValue()throws Exception {
        Zero3ExecutiveV354 original=new Zero3ExecutiveV354();
        float[] sight=vertical(true);
        for(int d=0;d<32;d++){
            original.propose(sight,proposal(),d,false);
            original.complete(.8f,2f,0f,.02f,false,false,null,true);
        }
        Field optionsField=Zero3ExecutiveV354.class.getDeclaredField("options");
        optionsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ArrayList<Object> options=(ArrayList<Object>)optionsField.get(original);
        assertEquals(1,options.size());
        // Simulate a PRE-V367 checkpoint: option has only original 15D
        // bearing knowledge, and no trained 16-18 values yet.
        Field schemaField=options.get(0).getClass().getDeclaredField("visionSchemaV366");
        schemaField.setAccessible(true);schemaField.setInt(options.get(0),366);
        Field weights=Zero3ExecutiveV354.class.getDeclaredField("valueWeights");
        weights.setAccessible(true);
        float[] state=(float[])weights.get(original);
        Arrays.fill(state,15,18,0f);
        byte[] old=original.encodePriorV366ForQA();
        assertEquals(366,ByteBuffer.wrap(old).getInt());
        Zero3ExecutiveV354 upgraded=Zero3ExecutiveV354.decode(old);
        assertArrayEquals("authentic old format must survive unchanged",old,
            upgraded.encodePriorV366ForQA());
        float[] restored=(float[])weights.get(upgraded);
        for(int i=0;i<15;i++)assertEquals("V366 learned weight preserved",state[i],restored[i],0f);
        assertEquals("scalar value bias preserved",state[18],restored[18],0f);
        for(int i=15;i<18;i++)assertEquals("no imaginary old Y knowledge",0f,restored[i],0f);
        byte[] now=upgraded.encode();
        assertEquals(367,ByteBuffer.wrap(now).getInt());
        Zero3ExecutiveV354 again=Zero3ExecutiveV354.decode(now);
        assertArrayEquals("V367 roundtrip must be bytewise stable",now,again.encode());
        assertEquals(32,again.snapshot().decisions);
        assertEquals(1,again.snapshot().skills.length);
        assertEquals(original.snapshot().skills[0].id,again.snapshot().skills[0].id);
    }
    @Test public void unsafeOrUnvalidatedFutureCostCannotOverrideActor() {
        Zero3PlannerV354 planner=new Zero3PlannerV354();
        float[] observation=new float[194],hidden=new float[64];
        float[][] options={proposal(),{-1f,0f,.5f,.5f,.3f}};
        Zero3PlannerV354.Predictor predictor=new Zero3PlannerV354.Predictor(){
            public Zero3PlannerV354.Step step(float[] x,float[] h,float[] action){
                float[] p=new float[12];p[0]=action[0]*.1f;
                return new Zero3PlannerV354.Step(x,h,p,0f);
            }
            public float[] meanAction(float[] h){return proposal();}
        };
        assertEquals("0 real 2-step validations must block, even if risk is 0",
            -1,planner.select(observation,hidden,options,predictor,true,false,0f));
        assertEquals("nonfinite measured cost must be rejected",
            -1,planner.select(observation,hidden,options,predictor,true,false,Float.NaN));
        assertEquals("unreliable legacy five-arg prerequisite cannot bypass validations",
            -1,planner.select(observation,hidden,options,predictor,true));
        assertEquals(Float.POSITIVE_INFINITY,
            Zero3CognitionV353.conservativeCostMarginV366(0,63),0f);
    }
}
