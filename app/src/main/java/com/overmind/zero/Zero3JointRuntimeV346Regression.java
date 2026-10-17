package com.overmind.zero;

import java.util.ArrayList;
import java.util.Locale;

/** V346: real V325 runtime with a true room/gate and independent cortex.
 * This is a mechanical research comparison, NOT a motor-skill promotion. */
public final class Zero3JointRuntimeV346Regression {
    private Zero3JointRuntimeV346Regression(){}
    private static void check(boolean ok,String message){
        if(!ok)throw new AssertionError("V346 "+message);
    }
    private static Zero3RuntimeV325 birth(int scenario,boolean candidate)
            throws Exception{
        long episode=346000L+scenario,seed=0x34653ABCDL+scenario*32771L;
        int pose=scenario%4;
        float angle=(float)(-.82+scenario*.27);
        Zero3MotorImpulseResponseRegression.Scenario p=
            new Zero3MotorImpulseResponseRegression.Scenario(
                pose,angle,scenario%4);
        float[] zero=new float[64];
        for(int n=0;n<96;n++)
            p.gel.step(p.body,p.skin,zero,.025f);
        check(p.gel.invalid==0,"newborn gel invalid");
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        float bx=p.body.cx+(scenario%2==0?46f:-46f),
              by=p.body.cy+(scenario%3-1)*5f;
        world.add(new UnknownWorld.Entity(bx,by,2,5));
        if(scenario==2){
            // Multi-object candidate in actual room and same 96-site gel.
            world.add(new UnknownWorld.Entity(p.body.cx+78f,
                p.body.cy+20f,2,5));
        }
        if(scenario>=1)
            world.add(new UnknownWorld.Entity(p.body.cx-63f,
                p.body.cy+42f,2,2));
        ScarcityLab room=new ScarcityLab(p.body.cx+147f,p.body.cy);
        world.add(room.food);
        Zero3RuntimeV325 r=Zero3RuntimeV325.newborn(
            episode,seed,p.body,p.skin,p.gel,world,room,100f,0f);
        if(candidate){
            r.enableJointPhysicsResearchV346();
            boolean rejected=false;
            try{r.snapshot();}
            catch(IllegalStateException expected){rejected=true;}
            check(rejected,"nonversioned lab mode persisted silently");
        }
        return r;
    }
    public static void main(String[] args)throws Exception{
        int complete=0,failed=0,baseline=0;
        long sumRepairs=0,sumUnresolved=0;
        for(int scene=0;scene<4;scene++){
            for(int arm=0;arm<2;arm++){
                boolean candidate=arm==1;
                int count=0;
                String reason="NONE";
                long[] fixes={0,0,0};
                double netDistance=0;
                try(Zero3RuntimeV325 r=birth(scene,candidate)){
                    float x0=r.display().cx,y0=r.display().cy;
                    for(int n=0;n<400;n++){
                        if(r.ended()){reason="ENERGY_TERMINAL";break;}
                        try{r.advanceOneDecision();}
                        catch(IllegalStateException fault){
                            reason="PHYSICAL_FAIL_"+
                                   fault.getMessage().replace(' ','_');
                            break;
                        }
                        count++;
                    }
                    fixes=r.jointPhysicsCountersV346();
                    netDistance=Math.hypot(r.display().cx-x0,
                                           r.display().cy-y0);
                    if(!candidate&&count>0){
                        check(r.snapshot()!=null,
                            "default actual V325 snapshot incompatible");
                    }
                }
                if(candidate){
                    if(count==400&&fixes[2]==0)complete++;
                    else failed++;
                    sumRepairs+=fixes[1];
                    sumUnresolved+=fixes[2];
                }else if(count==400)baseline++;
                System.out.printf(Locale.US,
                    "V346 SCENE scene=%d arm=%s completed=%d/400 "+
                    "realSubsteps=%d netCOM=%.7f jointAttempts=%d "+
                    "jointRepaired=%d jointUnresolved=%d stop=%s "+
                    "ROOM_PRESENT=TRUE NO_SCRIPTED_GRIP=TRUE%n",
                    scene,candidate?"POST_WORLD_CANDIDATE":"ORIGINAL_V325",
                    count,count*12,netDistance,
                    fixes[0],fixes[1],fixes[2],reason);
            }
        }
        System.out.printf(Locale.US,
          "V346 SCIENCE_GATE candidateComplete=%d/4 originalComplete=%d/4 "+
          "candidateFaults=%d totalJointRepaired=%d "+
          "totalJointUnresolved=%d "+
          "BILATERAL_RETENTION=NOT_TESTED_UNLESS_ACTUAL_POSITIVE_BOTH "+
          "NEVER_PROMOTE_TO_ANDROID=true G1_OPEN G3_G4_G5_OPEN NO_OTA%n",
          complete,baseline,failed,sumRepairs,sumUnresolved);
        // Technical CI asserts that the scientific verdict was produced,
        // NEVER silently treats green job as physics promotion.
    }
}
