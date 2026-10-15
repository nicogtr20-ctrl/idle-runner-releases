package com.overmind.zero;
/** Static-model proof that diagnostic parameter reads are bounded,
 * complete and have NO influence on learned state or serialization. */
public final class Zero157DashboardRegression {
    static void ok(boolean result,String issue){
        if(!result)throw new AssertionError(issue);
    }
    public static void main(String[] args){
        RecurrentCoreV22 core=new RecurrentCoreV22();
        SymbioteMotorHeadV1 muscle=new SymbioteMotorHeadV1();
        int expected=RecurrentCoreV22.dynamicsParameters()
            +3*RecurrentCoreV22.headParameters();
        ok(core.parameterCount()==expected,"Core parameter groups omitted or duplicated");
        ok(muscle.parameterCount()==84,"Motor UI is not showing 84 live muscle weights");
        String originalCore=core.encode(),originalMuscle=muscle.encode();
        float checksum=0;
        for(int i=0;i<core.parameterCount();i++)checksum+=Math.abs(core.parameterAt(i));
        for(int i=0;i<muscle.parameterCount();i++)checksum+=Math.abs(muscle.parameterAt(i));
        ok(Float.isFinite(checksum),"Diagnostic value out of bounds");
        ok(originalCore.equals(core.encode())&&originalMuscle.equals(muscle.encode()),
            "Read-only dashboard changed trained brain or cortex state");
        boolean coreBounds=false,motorBounds=false;
        try{core.parameterAt(core.parameterCount());}catch(IndexOutOfBoundsException expectedError){coreBounds=true;}
        try{muscle.parameterAt(-1);}catch(IndexOutOfBoundsException expectedError){motorBounds=true;}
        ok(coreBounds&&motorBounds,"Parameter inspector fails closed outside saved weights");
        System.out.println("PASS OTA157: actual 2.2 and cortex parameter indices read-only");
    }
}