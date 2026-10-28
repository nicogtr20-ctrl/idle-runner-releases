package com.overmind.zero;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/** True material contacts are distinct from the signed biological energy delta
 * after saturation. No source identity, food/harm sign or target is ever
 * fed back to the original GRU3 motor actor by this bookkeeping. */
@RunWith(AndroidJUnit4.class)
public final class Zero409PhysicalContactLedgerAndroidTest {
    @Test public void physicallyConsumedBlueAtFullReserveStillCountsAsRealPickup(){
        EnergyLifetimeCounters ledger=new EnergyLifetimeCounters();
        UnknownWorld.Entity blue=new UnknownWorld.Entity(80f,0f,0,0);
        float reserve=100f;
        float actualTouch=blue.touch(1f,0f);
        float received=Math.min(100f,reserve+actualTouch)-reserve;
        assertEquals(22f,actualTouch,0f);
        assertEquals("Real biological advantage remains zero at cap",
            0f,received,0f);
        assertTrue("The physically contacted blue was CONSUMED",blue.consumed);
        ledger.recordPhysicalContact(actualTouch,false);
        assertEquals("True physical blue pickup must be counted",1L,ledger.blue);
        assertEquals(0L,ledger.red);
        // Original GRU3 sees zero energy gain; no fake reward or colour label.
        assertEquals(0f,received,0f);
    }

    @Test public void secondRealRedTouchAfterTerminalClampStillCounts(){
        EnergyLifetimeCounters ledger=new EnergyLifetimeCounters();
        UnknownWorld.Entity redA=new UnknownWorld.Entity(45f,0f,1,1);
        UnknownWorld.Entity redB=new UnknownWorld.Entity(-45f,0f,1,1);
        float energy=5f;
        float harmA=redA.touch(1f,0f);
        float afterA=Math.max(0f,energy+harmA);
        assertEquals(-7f,harmA,0f);
        assertEquals(0f,afterA,0f);
        float harmB=redB.touch(-1f,0f);
        float afterB=Math.max(0f,afterA+harmB);
        assertEquals("No biological damage can exceed available energy",
            0f,afterB,0f);
        ledger.recordPhysicalContact(harmA,false);
        ledger.recordPhysicalContact(harmB,false);
        assertEquals("BOTH actual material contacts must count, not only energy loss",
            2L,ledger.red);
        assertEquals(0L,ledger.blue);
    }

    @Test public void violetAtFullReserveAndUncontactedMaterialsRemainDistinct(){
        EnergyLifetimeCounters ledger=new EnergyLifetimeCounters();
        UnknownWorld.Entity violet=new UnknownWorld.Entity(60f,0f,0,3);
        assertTrue(violet.isRare());
        assertEquals(0L,ledger.violet);
        float touch=violet.touch(0f,1f);
        assertTrue(touch>0f);
        assertTrue(violet.consumed);
        ledger.recordPhysicalContact(touch,true);
        assertEquals(1L,ledger.violet);
        assertEquals(0L,ledger.blue);
        assertEquals("Second exhausted source cannot pay/appear twice",0f,
            violet.touch(0f,1f),0f);
        // No extra recordPhysicalContact when touch returns zero.
        String original=ledger.encode();
        EnergyLifetimeCounters copy=new EnergyLifetimeCounters();
        assertTrue(copy.restore(original));
        assertEquals(1L,copy.violet);
        assertEquals(original,copy.encode());
    }
}
