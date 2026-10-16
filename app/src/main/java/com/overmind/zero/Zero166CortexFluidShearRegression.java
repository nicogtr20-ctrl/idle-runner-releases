package com.overmind.zero;

/** Genuine cortex/liquid shear and mass-scaled reaction, no prescribed gait. */
public final class Zero166CortexFluidShearRegression {
    private static void ok(boolean good,String msg){
        if(!good)throw new AssertionError("ZERO166 SHEAR: "+msg);
    }
    private static final int FLUID=MotorBrain.N+SymbioteMatterV1.INNER;
    static float shellMomentum(Morphology.Surface s,int axis){
        float v=0;
        for(int i=0;i<Morphology.POINTS;i++)
            v+=axis==0?s.vx[i]:s.vy[i];
        return .5f*v/Morphology.POINTS;
    }
    static float liquidMomentum(float[] vx,float[] vy,int axis){
        float v=0;
        for(int i=0;i<FLUID;i++)v+=axis==0?vx[i]:vy[i];
        return .5f*v/FLUID;
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(body,skin);
        float[] px=new float[FLUID],py=new float[FLUID],
                ux=new float[FLUID],uy=new float[FLUID];
        // 73 parcels are too distant to couple; one is physically 6U
        // under the unheld living membrane, moving transversely.
        for(int j=0;j<FLUID;j++){px[j]=2000f;py[j]=2000f;}
        px[0]=22f;py[0]=0;uy[0]=40f;
        float momentumBefore=shellMomentum(skin,1)+liquidMomentum(ux,uy,1);
        SymbioteMatterV1.exchangeCortexFluidShear(skin,px,py,ux,uy,.025f);
        float momentumAfter=shellMomentum(skin,1)+liquidMomentum(ux,uy,1);
        ok(Math.abs(momentumBefore-momentumAfter)<.00001f,
            "unheld shear injected free center momentum");
        ok(skin.vy[0]>0&&uy[0]<40f&&uy[0]>0f,
            "fast spinning liquid still does not exchange momentum with cortex");
        ok(Math.abs(skin.vy[22])<1e-6f,
            "distant membrane was accelerated by remote fluid");
        // Actual object attachment transfers torque to real contacted solid.
        gel.reset(body,skin);
        skin.gripHold[0]=Morphology.GRIP_HOLD+.2f;
        uy[0]=40f;
        SymbioteMatterV1.exchangeCortexFluidShear(skin,px,py,ux,uy,.025f);
        ok(Math.abs(skin.vy[0])<1e-6f&&uy[0]<40f,
            "actually held object contact was translated by fluid shear");

        // No actuation => no phantom drift; previous physical invariants.
        gel.reset(body,skin);
        float[] calm=new float[64];
        float originX=body.cx,originY=body.cy;
        for(int tick=0;tick<100;tick++)
            gel.step(body,skin,calm,.025f);
        ok(Math.hypot(body.cx-originX,body.cy-originY)<1f,
            "viscosity caused spontaneous locomotion with silent muscles");
        ok(gel.invalid==0&&MaterialGeometry.selfCrossings(skin)==0&&
            gel.areaRelativeError(skin)<.10f,
            "two-phase viscous solver damaged resting material");

        // Stress large signed tangential shear without any goal/food.
        float[] twist=new float[64];
        for(int k=0;k<16;k++)
            twist[4*k+1]=(k%2==0?.65f:-.65f);
        for(int tick=0;tick<120;tick++)
            gel.step(body,skin,twist,.025f);
        ok(gel.invalid==0&&MaterialGeometry.selfCrossings(skin)==0&&
            gel.areaRelativeError(skin)<.16f,
            "passive shear coupling destabilized membrane under torque");
        System.out.println("ZERO166 PASS: weighted reaction, solid hold, "+
            "locality, passive neutrality and active torque topology");
    }
}
