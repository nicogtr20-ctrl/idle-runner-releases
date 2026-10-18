package com.overmind.zero;

import java.util.*;

/** Twenty predeclared new physical births; no hardcoded intentions, no fabricated gains. */
public final class Zero3IndependentV354Regression {
    public static void main(String[] args)throws Exception{
        int complete=0,holds=0,bilateral=0;
        for(int k=0;k<20;k++){
            int seed=101+k*37;
            Zero3MotorImpulseResponseRegression.Scenario p=new Zero3MotorImpulseResponseRegression.Scenario((seed*7)%4,(float)(.137*seed+.16),(seed*11)%4);
            for(int t=0;t<96;t++)p.gel.step(p.body,p.skin,new float[64],.025f);
            ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
            world.add(new UnknownWorld.Entity(p.body.cx+(k%2==0?1:-1)*(47+k%7),p.body.cy+(k%5-2)*5,2,5));
            if(k%3==0)world.add(new UnknownWorld.Entity(p.body.cx+84,p.body.cy+49,2,5));
            world.add(new UnknownWorld.Entity(p.body.cx-69,p.body.cy-40,2,2));ScarcityLab room=new ScarcityLab(p.body.cx+170,p.body.cy);world.add(room.food);
            try(Zero3RuntimeV325 r=Zero3RuntimeV325.newborn(3540100+k,0x354D0A11L+seed,p.body,p.skin,p.gel,world,room,100,0)){
                r.enableCompletionV354();r.enableSegmented();for(int n=0;n<400&&!r.ended();n++)r.advanceOneDecision();
                if(r.decisions()!=400)throw new AssertionError("V354 independent terminated early seed="+seed+" n="+r.decisions());
                for(Zero3OrganismStoreV322.Trace trace:r.completedTracesV354()){holds+=trace.gripFrames;bilateral+=trace.bilateralFrames;}
                if(r.jointPhysicsCountersV346()[2]!=0)throw new AssertionError("V354 independent unresolved");
                complete++;System.out.println("V354 INDEPENDENT seed="+seed+" complete=400 actualTicks="+r.gelTicks()+" repairs="+r.jointPhysicsCountersV346()[1]);
            }
        }
        if(holds==0||bilateral==0)throw new AssertionError("V354 independent lacked true held/bilateral contact coverage");
        System.out.println("V354 INDEPENDENT_PHYSICS=PASS births="+complete+" decisions=8000 heldFrames="+holds+" bilateralFrames="+bilateral+" NO_LEARNING_EFFICACY_CLAIM");
    }
}
