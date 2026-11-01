package com.overmind.zero;

/**
 * Transient ZERO454 scientific instrument. Compares the actual physical COM
 * velocity increment with the single V390 muscle probe ON / OFF / OFF / ON
 * during one real original-GRU3 action. Balanced phase weights cancel a
 * locally linear passive acceleration drift. NOT an exact counterfactual:
 * shape memory, internal forces and contact can still confound results.
 *
 * NO motor instructions, reward, velocity assignment or saved parameters.
 */
final class Zero454PairedMotorContrast {
    private int nextPhase, validQuads;
    private float running, total;
    private boolean contaminated;
    static boolean probeOn(int phase){
        if(phase<0||phase>3)throw new IllegalArgumentException("V454 phase");
        return phase==0||phase==3;
    }
    static float balancedContrast(float a,float b,float c,float d){
        if(!Float.isFinite(a)||!Float.isFinite(b)||
           !Float.isFinite(c)||!Float.isFinite(d))
            throw new IllegalArgumentException("V454 nonfinite response");
        return .5f*(a-b-c+d);
    }
    void reset(){nextPhase=0;validQuads=0;running=total=0f;contaminated=false;}
    void observe(int phase,float deltaVx,float deltaVy,float ux,float uy,
                 boolean physicalDisturbance){
        if(phase!=nextPhase||!Float.isFinite(deltaVx)||!Float.isFinite(deltaVy)||
           !Float.isFinite(ux)||!Float.isFinite(uy))
            throw new IllegalStateException("V454 physical phase/velocity mismatch");
        if(physicalDisturbance)contaminated=true;
        float along=deltaVx*ux+deltaVy*uy;
        running+=(probeOn(phase)?1f:-1f)*along;
        nextPhase=(nextPhase+1)&3;
        if(nextPhase==0){
            if(!contaminated){
                total+=.5f*running;
                validQuads++;
            }
            running=0f;contaminated=false;
        }
    }
    int validQuads(){return validQuads;}
    float mean(){
        return validQuads==0?Float.NaN:total/validQuads;
    }
}
