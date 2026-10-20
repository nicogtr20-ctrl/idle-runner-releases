package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.lang.reflect.Field;
import java.util.ArrayList;
import static org.junit.Assert.*;

/** V375 preserves the original GRU authority, option identities and saved
 * memory while rejecting scientifically unsupported straight-line recalls.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero375GoalMemorySafetyAndroidTest {
    private static Field field(Class<?> cls,String name)throws Exception {
        Field f=cls.getDeclaredField(name);f.setAccessible(true);return f;
    }
    @Test public void genuineLongGoalAgeCanBeSavedAndLoadedLosslessly()throws Exception {
        Zero3ExecutiveV354 e=new Zero3ExecutiveV354();
        // V371 permits 24 completed choices in one continuous intent.
        // This exact value could be written by V374 but refused at load.
        Field age=field(Zero3ExecutiveV354.class,"goalAge");
        for(int duration:new int[]{0,4,5,12,23,24}) {
            age.setInt(e,duration);
            byte[] bytes=e.encode();
            Zero3ExecutiveV354 restored=Zero3ExecutiveV354.decode(bytes);
            assertEquals(duration,age.getInt(restored));
            assertArrayEquals("No rewrites of learned memory bytes",
                bytes,restored.encode());
        }
        age.setInt(e,25);
        try{
            Zero3ExecutiveV354.decode(e.encode());
            fail("Unsupported goal ages must fail closed");
        }catch(java.io.IOException expected){}
    }

    @Test public void learnedHarmPreventsHarmfulOldStraightOptionRecall()throws Exception {
        Zero3ExecutiveV354 e=new Zero3ExecutiveV354();
        float[] scene=Zero374RealTouchOpticalIdentityAndroidTest.full194();
        float[] straight=Zero374RealTouchOpticalIdentityAndroidTest.action(1f);
        for(int i=0;i<32;i++){
            assertEquals(i,e.snapshot().decisions);
            e.propose(scene,straight,i,false);
            e.complete(.8f,2f,0f,.02f,false,false,null,true);
        }
        Field os=field(Zero3ExecutiveV354.class,"options");
        @SuppressWarnings("unchecked")
        ArrayList<Object> options=(ArrayList<Object>)os.get(e);
        assertEquals(1,options.size());
        Object o=options.get(0);
        Class<?> t=o.getClass();
        field(t,"state").setInt(o,Zero3ExecutiveV354.CHAMPION);
        field(t,"train").setInt(o,16);
        field(t,"optionN").setInt(o,8);
        field(t,"controlN").setInt(o,8);
        field(t,"optionSum").setDouble(o,7.0);
        field(t,"controlSum").setDouble(o,0.5);
        field(t,"optionEnergy").setDouble(o,15.0);
        // Phase 1: old unmodified executive WOULD recall.
        long d=32;
        float[] old=e.propose(scene,straight,d,false);
        assertTrue("Old champion wins in its remembered context",e.usedMemory());
        assertEquals(straight[0],old[0],0f);
        e.abort();
        // Phase 2: after actual, train-world MATERIAL contact evidence
        // establishes that the visible direction chosen by that option is
        // net harmful. This only vetoes option recall, not GRU actor.
        Zero3CognitionV353 cognition=new Zero3CognitionV353();
        float[] photons=Zero374RealTouchOpticalIdentityAndroidTest.mixed(.90f,.99f);
        for(int i=0;i<3;i++){
            cognition.materialContactV374(0,photons,+22f,false);
            cognition.materialContactV374(1,photons,-7f,false);
        }
        assertTrue(cognition.hasVisibleContactKnowledgeV369(scene));
        assertTrue("Old straight path has learned harm",
            cognition.realContactPreferenceV374(scene,straight)<-.025f);
        float[] chosen=e.propose(scene,straight,d,false,cognition);
        assertFalse("A harmful remembered option cannot silently reissue itself",
            e.usedMemory());
        assertArrayEquals("Do not author a new reflex: sole GRU proposal remains",
            straight,chosen,0f);
        e.abort();
        // This targeted veto fixture deliberately marks the candidate as
        // CHAMPION through reflection without simulating its 16 heldout
        // outcomes. Such synthetic counters cannot be checkpointed as an
        // authentic V354 life. Verify the original option identity remains;
        // real checkpoint roundtrips are independently exercised above.
        assertEquals(1,e.snapshot().skills.length);
        assertEquals(field(t,"id").getLong(o),e.snapshot().skills[0].id);
    }
}
