package com.overmind.zero;

/**
 * OTA164 physical constitutive-law acceptance. These are ONLY lab electrodes,
 * not an autonomous brain action, a scripted gait or a reward.
 */
public final class Zero164TractionRegression {
    private static void ok(boolean v,String why){
        if(!v)throw new AssertionError("ZERO164 TRACTION: "+why);
    }
    private static float reach(MotorBrain.Body b,Morphology.Surface skin){
        float far=0;
        for(int i=0;i<Morphology.POINTS;i++)
            far=Math.max(far,(float)Math.hypot(skin.x[i]-b.cx,skin.y[i]-b.cy));
        return far;
    }
    public static void main(String[] args){
        final float dt=.025f;
        // This is the moderate extension range actually reached in the
        // exported phone scene (~42.7U): OTA163's R+32 gate missed it.
        float ordinary=SymbioteMatterV1.radialRetractionRelease(42f,-.9f,false);
        ok(ordinary>.30f,"moderate real-body contraction still friction-locked");
        ok(SymbioteMatterV1.radialRetractionRelease(42f,-.9f,true)==0f,
            "confirmed world-object hold became frictionless");
        ok(SymbioteMatterV1.radialRetractionRelease(32f,-.9f,false)==0f,
            "unstretched foothold lost ordinary substrate anchoring");
        ok(SymbioteMatterV1.radialRetractionRelease(42f,.8f,false)==0f,
            "growing tip lost friction without voluntary contraction");
        ok(SymbioteMatterV1.radialRetractionRelease(42f,-.15f,false)==0f,
            "small motor noise released ordinary grip");
        float original=(float)Math.exp(-(1.8f+66f*.95f)*dt);
        float radial=SymbioteMatterV1.radialRetractionDrag(.95f,ordinary,dt);
        ok(radial>original*1.25f&&radial<1f,
            "moderate radial pull is still over-damped");
        ok(Math.abs(SymbioteMatterV1.radialRetractionDrag(.95f,0f,dt)-
                    original)<1e-6f,
            "resting/anchored tissue changed baseline tangential physics");

        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(body,skin);
        float[] extend=new float[64];
        extend[0]=.96f;extend[4]=.86f;extend[8]=.65f;
        for(int k=0;k<135;k++)gel.step(body,skin,extend,dt);
        float before=reach(body,skin);
        ok(before>37f,"laboratory did not grow a measurable living tip");
        float[] contract=new float[64];
        for(int k=0;k<3;k++)contract[4*k]=-.93f;
        int eligibleFrames=0;
        for(int k=0;k<80;k++){
            float radius=reach(body,skin);
            if(SymbioteMatterV1.radialRetractionRelease(radius,-.93f,false)>0)
                eligibleFrames++;
            gel.step(body,skin,contract,dt);
        }
        float after=reach(body,skin);
        ok(eligibleFrames>=5,"ordinary retraction was never physically exercised");
        ok(after<before-1.0f,"real gel could not retract a commanded living tip");
        ok(gel.invalid==0 && MaterialGeometry.selfCrossings(skin)==0 &&
           gel.areaRelativeError(skin)<.16f,
           "ordinary radial release destabilized conserved gel");

        // No code here certifies autonomous goal-directed navigation.
        System.out.println("ZERO164 PASS: real ordinary retraction "+
            before+" -> "+after+"; eligible="+eligibleFrames+
            " anchored=unchanged free radial drag="+radial);
    }
}
