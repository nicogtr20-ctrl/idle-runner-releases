package com.overmind.zero;

/** Actual room + multiple BOX, matched historical birth. No authored motor commands. */
public final class Zero3JointV354Regression {
    public static void main(String[] args)throws Exception{
        System.setProperty("v354.joint","true");int complete=0;long baseBilateral=0,newBilateral=0;
        for(int scene=0;scene<4;scene++)for(int arm=0;arm<2;arm++){
            long holds=0,bilateral=0;int steps=0;String fault="none";
            try(Zero3RuntimeV325 r=Zero3JointRuntimeV346Regression.birth(scene,arm==1)){
                for(int n=0;n<400;n++){
                    try{r.advanceOneDecision();steps++;}catch(IllegalStateException bad){fault=bad.getMessage();break;}
                }
                if(arm==0){for(Zero3OrganismStoreV322.Trace t:r.segmentedSnapshot().organism.trace()){holds+=t.gripFrames;bilateral+=t.bilateralFrames;}}
                // Research readout is limited to completed traces, never grants save permission.
                else for(Zero3OrganismStoreV322.Trace t:r.completedTracesV354()){holds+=t.gripFrames;bilateral+=t.bilateralFrames;}
                if(arm==0)baseBilateral+=bilateral;else{newBilateral+=bilateral;if(steps==400)complete++;}
                System.out.println("V354 scene="+scene+" arm="+arm+" completed="+steps+" holds="+holds+" bilateral="+bilateral+
                    " repairs="+r.jointPhysicsCountersV346()[1]+" fault="+fault);
            }
        }
        if(complete!=4||baseBilateral==0||newBilateral<baseBilateral*.8)throw new AssertionError("V354 REJECTED complete="+complete+"/4 bilateral="+newBilateral+" baseline="+baseBilateral);
        System.out.println("V354 JOINT_MECHANICAL_SCREEN=PASS independent_gate_and_longer_stress_still_required");
    }
}
