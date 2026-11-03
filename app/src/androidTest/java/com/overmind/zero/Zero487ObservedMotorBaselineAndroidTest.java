package com.overmind.zero;

import static org.junit.Assert.*;
import org.junit.Test;

/** Transient-source mathematical safety tests; not autonomous survival proof. */
public final class Zero487ObservedMotorBaselineAndroidTest {
    @Test public void warmupNeverCreditsUnobservedOrCurrentPhysicalSample(){
        Zero487ObservedMotorBaseline b=new Zero487ObservedMotorBaseline();
        assertEquals(0f,b.advantage(1f,0f,0f,0f,.7f),0f);
        for(int i=0;i<3;i++){
            b.accept(1f,0f,0f,0f,.2f);
            assertEquals(i<2?0f:.5f,b.advantage(1f,0f,0f,0f,.7f),.00001f);
        }
        assertEquals(.5f,b.advantage(1f,0f,0f,0f,.7f),.00001f);
        assertEquals(3,b.evidence(1f,0f,0f,0f));
    }
    @Test public void priorOutcomeIsDirectionAndMeasuredSpeedConditioned(){
        Zero487ObservedMotorBaseline b=new Zero487ObservedMotorBaseline();
        for(int i=0;i<3;i++)b.accept(1,0,1f,0f,.3f);
        assertEquals(.2f,b.advantage(1,0,1f,0f,.5f),.00001f);
        assertEquals(0f,b.advantage(0,1,1f,0f,.5f),0f);
        assertEquals(0f,b.advantage(1,0,3f,0f,.5f),0f);
        assertEquals(0f,b.advantage(1,0,0f,0f,.5f),0f);
    }
    @Test public void onlyAcceptedPastDataCanChangeBaselineAndLifeResets(){
        Zero487ObservedMotorBaseline b=new Zero487ObservedMotorBaseline();
        for(int i=0;i<3;i++)b.accept(0,1,1,0,-.4f);
        assertEquals(-.2f,b.advantage(0,1,1,0,-.6f),.00001f);
        assertEquals(3,b.accepted());
        b.newLife();
        assertEquals(0f,b.advantage(0,1,1,0,-.6f),0f);
        assertEquals(0,b.evidence(0,1,1,0));
    }
    @Test public void rejectsInvalidInputsAndClipsMeasuredAdvantage(){
        Zero487ObservedMotorBaseline b=new Zero487ObservedMotorBaseline();
        for(int i=0;i<3;i++)b.accept(1,0,1,0,-1f);
        assertEquals(1f,b.advantage(1,0,1,0,1f),0f);
        try{b.accept(0,0,0,0,0);fail("no heading");}
        catch(IllegalArgumentException expected){}
        try{b.advantage(1,0,Float.NaN,0,0);fail("bad physical speed");}
        catch(IllegalArgumentException expected){}
    }
}
