package com.overmind.zero;

/**
 * OTA167 hardware-rate neural loop acceptance on the REAL 96+74 material.
 * The laboratory drive is a controlled actuator electrode, NEVER a gait or
 * training trajectory in the actual application.
 */
public final class Zero167SomaticLoopRegression {
    private static void ok(boolean yes,String why){
        if(!yes)throw new AssertionError("ZERO167 FAST MOTOR: "+why);
    }
    private static float[] parameters(FastSomaticCircuitV167 loop){
        float[] p=new float[loop.parameterCount()];
        for(int i=0;i<p.length;i++)p[i]=loop.parameterAt(i);
        return p;
    }
    private static float maxDelta(float[] a,float[] b){
        float result=0;
        for(int i=0;i<a.length;i++)result=Math.max(result,Math.abs(a[i]-b[i]));
        return result;
    }
    private static float[] electrode(int phase){
        float[] commands=new float[64];
        for(int s=0;s<16;s++){
            boolean positive=Math.cos((s+.5)*Math.PI/8)>0;
            commands[4*s]=positive?(phase==0?.78f:-.78f):
                (phase==0?-.43f:.43f);
            commands[4*s+2]=positive?(phase==0?-.95f:.95f):
                (phase==0?.95f:-.95f);
            commands[4*s+3]=-.3f;
        }
        return commands;
    }
    public static void main(String[] args){
        final float dt=.025f;
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(body,skin);
        SymbioteMotorHeadV1 motor=new SymbioteMotorHeadV1();
        float[] state=new float[RecurrentCoreV22.Z];
        float[] sensed=new float[DistributedBodySense.FEATURES];
        motor.chosen(0,sensed,state,.25f,null,
            SymbioteMotorHeadV1.measuredSectorAngles(body,skin));
        float[] oldMotor=motor.encode().split("\\|").length==0?
            new float[0]:motor.commands().clone();
        String sharedCheckpoint=motor.encode(),localCheckpoint=motor.encodeLocal();
        float[] first=null;int changed=0;
        for(int i=0;i<110;i++){
            motor.fastTick(body,skin,dt);
            if(i==0)first=motor.commands().clone();
            if(maxDelta(first,motor.commands())>.002f)changed++;
            ok(Math.abs(motor.commands()[2]-oldMotor[2])<1e-6f,
                "fast motor overwrote the learned adhesion command");
            ok(Math.abs(motor.commands()[3]-oldMotor[3])<1e-6f,
                "fast motor overwrote the learned stiffness command");
            float beforeX=body.cx,beforeY=body.cy;
            gel.step(body,skin,motor.commands(),dt);
            motor.observedPhysicalStep(beforeX,beforeY,body,skin,gel.lastMuscleReactionImpulse,false,dt);
        }
        ok(changed>10,"no recurrent sub-decision motor adjustment at real physics rate");
        ok(motor.encode().equals(sharedCheckpoint)&&
           motor.encodeLocal().equals(localCheckpoint),
            "fast low-level motion mutated the old slow cortex checkpoint");
        ok(gel.invalid==0&&MaterialGeometry.selfCrossings(skin)==0&&
           gel.areaRelativeError(skin)<.16f,
            "new continuously updated muscle damaged physical gel");
        FastSomaticCircuitV167 fast=motor.fastCircuit();
        ok(fast.physicalFrames()==110&&fast.activeAction()==0,
            "somatic state did not run 110 times for ONE chosen brain action");
        float[] untouched=parameters(fast);
        motor.consequence(0f,0f,0f,0f,false,true);
        ok(fast.validated()==1&&fast.trained()==0&&
            maxDelta(untouched,parameters(fast))==0,
            "heldout physics secretly trained the fast neurons");

        // True material simulation, no fake reward or direct centroid nudge:
        // a laboratory electrode tests positive/negative actuator sequences,
        // while FAST neurons can modulate them with observed skin feedback.
        body=new MotorBrain.Body();skin=new Morphology.Surface(body);
        gel=new SymbioteMatterV1();gel.reset(body,skin);
        FastSomaticCircuitV167 standalone=new FastSomaticCircuitV167();
        float[] applied=new float[64];
        standalone.chosen(0);
        float x0=body.cx,y0=body.cy,net=0,gross=0;
        for(int i=0;i<400;i++){
            float[] input=electrode((i/20)&1);
            standalone.advance(body,skin,input,applied,dt);
            float px=body.cx,py=body.cy;
            gel.step(body,skin,applied,dt);
            standalone.observed(px,py,body);
            gross+=(float)Math.hypot(body.cx-px,body.cy-py);
        }
        net=(float)Math.hypot(body.cx-x0,body.cy-y0);
        ok(gross>.8f&&standalone.physicalFrames()==400,
            "real fluid exercise failed to produce measured material displacement");
        ok(gel.invalid==0&&MaterialGeometry.selfCrossings(skin)==0&&
           gel.areaRelativeError(skin)<.16f,
            "fast plastic actuation destabilized living membrane");
        float[] before=parameters(standalone);
        standalone.consequence(true,false);
        ok(standalone.trained()==1&&standalone.validated()==0,
            "physical action did not close exactly one training sample");
        ok(Float.isFinite(standalone.lastDirectionalAgreement()),
            "motor score did not originate from valid realized heading");
        // A negative or near-zero signed effect may legitimately not change
        // weights: do not fabricate a success score to turn this test green.
        for(float w:parameters(standalone))
            ok(Float.isFinite(w)&&Math.abs(w)<=1.101f,
                "real feedback destabilized trainable recurrent connection");
        String saved=standalone.encode();
        FastSomaticCircuitV167 restored=new FastSomaticCircuitV167();
        ok(restored.restore(saved)&&saved.equals(restored.encode()),
            "new trained somatic synapses could not be saved and restored");
        ok(!restored.restore(saved+"tamper")&&saved.equals(restored.encode()),
            "corrupt saved motor neurons replaced valid learned synapses");
        restored.resetLife();
        ok(maxDelta(parameters(restored),parameters(standalone))==0,
            "life reset destroyed previously learned fast somatic weights");
        FastSomaticCircuitV167 virgin=new FastSomaticCircuitV167();
        ok(!virgin.restore("")&&virgin.trained()==0,
            "old save slot without fast module cannot start safely");
        System.out.println("ZERO167 PASS: 110 living fast ticks/one intent, "+
            "true non-scripted recurrence, heldout isolation, protected grip, "+
            "real gel training gross="+gross+" net="+net+
            " change="+maxDelta(before,parameters(standalone))+
            ", no drift/area leakage, bytewise saved synapses");
    }
}
