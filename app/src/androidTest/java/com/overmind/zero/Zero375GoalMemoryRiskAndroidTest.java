package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import static org.junit.Assert.*;

/** Regression from the actual OTA374 late-life behavior:
 * valid long goals must checkpoint and long-lived straight options must
 * not ignore physically experienced danger in the same visible scene.
 * No authored movement rule, training data reset or second policy.
 */
@RunWith(AndroidJUnit4.class)
public final class Zero375GoalMemoryRiskAndroidTest {
    private static Field field(Class<?> type,String name)throws Exception {
        Field f=type.getDeclaredField(name);f.setAccessible(true);return f;
    }
    private static float[] perception(){
        float[] p=new float[194];
        p[20+4]=.90f; // wavelength 0 seen WEST
        p[28+0]=.99f; // wavelength 1 seen EAST
        p[188]=-.3f;
        return p;
    }
    private static float[] action(float dx){
        return new float[]{dx,0f,.6f,.5f,.30f};
    }
    @Test public void twentyFourRealGoalAgeCheckpointRoundTripsWithoutReset()throws Exception {
        Zero3ExecutiveV354 e=new Zero3ExecutiveV354();
        field(Zero3ExecutiveV354.class,"goalAge").setInt(e,24);
        byte[] raw=e.encode();
        Zero3ExecutiveV354 restored=Zero3ExecutiveV354.decode(raw);
        assertEquals(24,field(Zero3ExecutiveV354.class,"goalAge").getInt(restored));
        assertArrayEquals("existing options and neural values must not change",raw,restored.encode());
        // Historic age4 and all intermediate ages still decode exactly.
        field(Zero3ExecutiveV354.class,"goalAge").setInt(e,4);
        byte[] prior=e.encode();
        assertArrayEquals(prior,Zero3ExecutiveV354.decode(prior).encode());
    }
    @Test public void invalidFutureGoalAgeFailsClosed()throws Exception{
        Zero3ExecutiveV354 e=new Zero3ExecutiveV354();
        field(Zero3ExecutiveV354.class,"goalAge").setInt(e,25);
        try{Zero3ExecutiveV354.decode(e.encode());fail("Invalid 25+ goal silently accepted");}
        catch(IOException expected){assertTrue(expected.getMessage().contains("goal age"));}
    }
    @Test public void oldStraightOptionCannotReassertCommandedDangerAfterRealContacts()throws Exception{
        Zero3ExecutiveV354 e=new Zero3ExecutiveV354();
        float[] visual=perception();
        for(int d=0;d<32;d++){
            e.propose(visual,action(+1f),d,false);
            e.complete(.8f,+3f,0f,.02f,false,false,null,true);
        }
        Field list=field(Zero3ExecutiveV354.class,"options");
        @SuppressWarnings("unchecked") ArrayList<Object> options=(ArrayList<Object>)list.get(e);
        assertEquals(1,options.size());
        Object o=options.get(0);Class<?> type=o.getClass();
        field(type,"state").setInt(o,Zero3ExecutiveV354.CHAMPION);
        field(type,"train").setInt(o,8);
        field(type,"optionN").setInt(o,8);
        field(type,"controlN").setInt(o,8);
        field(type,"optionSum").setDouble(o,7d);
        field(type,"controlSum").setDouble(o,2d);
        field(type,"optionEnergy").setDouble(o,6d);
        float[] safe=action(-1f);
        // The old system DID have a reason to reuse this option:
        // it was rewarded for motor progress, despite newer true harm.
        float[] unsafelyRecalled=e.propose(visual,safe,32,false);
        assertEquals(1f,unsafelyRecalled[0],.001f);
        e.abort();
        Zero3CognitionV353 cognition=new Zero3CognitionV353();
        float[] optical=new float[24];optical[4]=.90f;optical[8]=.99f;
        for(int i=0;i<2;i++){
            cognition.materialContactV374(0,optical,+22f,false);
            cognition.materialContactV374(1,optical,-7f,false);
        }
        assertTrue("learned risky heading has REAL negative preference",
                   cognition.realContactPreferenceV374(visual,action(+1f))<-.025f);
        float[] guarded=e.propose(visual,safe,32,false,cognition);
        assertEquals("V375 option must yield to learned actual danger", -1f,guarded[0],.001f);
        assertFalse("V375 no physically harmful memory replay",e.usedMemory());
        e.abort();
        assertEquals(32,e.snapshot().decisions);
        assertEquals("the previously learned option stays in memory",1,e.snapshot().skills.length);
    }
}
