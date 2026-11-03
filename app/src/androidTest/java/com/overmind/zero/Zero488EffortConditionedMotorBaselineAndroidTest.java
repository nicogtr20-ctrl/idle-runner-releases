package com.overmind.zero;

import static org.junit.Assert.*;
import org.junit.Test;

/** Original physical motor-effort bins; mathematical checks, not survival proof. */
public final class Zero488EffortConditionedMotorBaselineAndroidTest {
    @Test public void samePhysicalSpeedAndHeadingButDifferentIssuedEffortNeverSharePrior(){
        Zero488EffortConditionedMotorBaseline b=new Zero488EffortConditionedMotorBaseline();
        for(int i=0;i<3;i++)b.accept(1f,0f,.8f,0f,.25f,.2f);
        assertEquals(3,b.evidence(1,0,.8f,0f,.25f));
        assertEquals(0,b.evidence(1,0,.8f,0f,.75f));
        assertEquals(.3f,b.advantage(1,0,.8f,0f,.25f,.5f),1e-6f);
        assertEquals(0f,b.advantage(1,0,.8f,0f,.75f,.5f),0f);
        for(int i=0;i<3;i++)b.accept(1f,0f,.8f,0f,.75f,-.1f);
        assertEquals(.6f,b.advantage(1f,0f,.8f,0f,.75f,.5f),1e-6f);
        assertEquals(.3f,b.advantage(1f,0f,.8f,0f,.25f,.5f),1e-6f);
    }
    @Test public void alsoKeepsHeadingAndMeasuredPreMomentumDisjoint(){
        Zero488EffortConditionedMotorBaseline b=new Zero488EffortConditionedMotorBaseline();
        for(int i=0;i<3;i++)b.accept(1,0,1,0,.9f,.25f);
        assertEquals(.25f,b.advantage(1,0,1,0,.9f,.5f),1e-6f);
        assertEquals(0f,b.advantage(0,1,1,0,.9f,.5f),0f);
        assertEquals(0f,b.advantage(1,0,3,0,.9f,.5f),0f);
        assertEquals(0f,b.advantage(1,0,0,0,.9f,.5f),0f);
    }
    @Test public void noFutureLeakOrInheritedPhysicalBodyBaseline(){
        Zero488EffortConditionedMotorBaseline b=new Zero488EffortConditionedMotorBaseline();
        assertEquals(0f,b.advantage(1,0,1,0,.6f,.8f),0f);
        for(int i=0;i<3;i++)b.accept(1,0,1,0,.6f,.2f);
        assertEquals(3,b.accepted());
        assertEquals(.6f,b.advantage(1,0,1,0,.6f,.8f),1e-6f);
        b.newLife();
        assertEquals(0,b.evidence(1,0,1,0,.6f));
        assertEquals(0f,b.advantage(1,0,1,0,.6f,.8f),0f);
    }
    @Test public void rejectImpossibleMuscleEffortAndKeepBoundedPrior(){
        Zero488EffortConditionedMotorBaseline b=new Zero488EffortConditionedMotorBaseline();
        for(int i=0;i<3;i++)b.accept(1,0,1,0,1f,-1f);
        assertEquals(1f,b.advantage(1,0,1,0,1f,1f),0f);
        try{b.accept(1,0,1,0,Float.NaN,0f);fail("nan");}
        catch(IllegalArgumentException expected){}
        try{b.accept(1,0,1,0,1.01f,0f);fail("outside original effort");}
        catch(IllegalArgumentException expected){}
    }
}
