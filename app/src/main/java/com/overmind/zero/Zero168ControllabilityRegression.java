package com.overmind.zero;

import java.util.Locale;

/**
 * Real-material causality laboratory, NEVER a gameplay gait generator:
 * fixed intent, static motor, physically alternating motor, explicit local
 * foothold, passive elasticity, and single-owner motor exploratory credit.
 */
public final class Zero168ControllabilityRegression {
    private static void ok(boolean x,String reason){
        if(!x)throw new AssertionError("ZERO168 CONTROLLABILITY: "+reason);
    }
    private static float speed(Morphology.Surface s,int start,int length){
        float x=0;
        for(int i=start;i<start+length;i++)
            x+=(float)Math.hypot(s.vx[i],s.vy[i]);
        return x/length;
    }
    private static float[] lab(int phase){
        float[] cmd=new float[64];
        for(int sector=0;sector<16;sector++){
            boolean right=Math.cos((sector+.5)*Math.PI/8)>0;
            cmd[sector*4]=right?(phase==0?.78f:-.78f):
                (phase==0?-.43f:.43f);
            cmd[sector*4+2]=right?(phase==0?-.95f:.95f):
                (phase==0?.95f:-.95f);
        }
        return cmd;
    }
    private static final class Result{
        float gross,net,bodyOmega,surfaceOmega,fluidOmega;
        int invalid,crossings;
    }
    private static Result sim(int scenario,int frames){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 m=new SymbioteMatterV1();m.reset(b,s);
        float x0=b.cx,y0=b.cy,px=x0,py=y0;
        Result result=new Result();
        for(int t=0;t<frames;t++){
            float[] cmd=scenario==0?new float[64]:
                lab(scenario==1?0:(t/20)&1);
            m.step(b,s,cmd,.025f);
            result.gross+=(float)Math.hypot(b.cx-px,b.cy-py);
            px=b.cx;py=b.cy;
            ok(m.invalid==0&&MaterialGeometry.selfCrossings(s)==0,
                "topology invalid in physical controllability test");
        }
        result.net=(float)Math.hypot(b.cx-x0,b.cy-y0);
        result.invalid=(int)m.invalid;
        result.crossings=MaterialGeometry.selfCrossings(s);
        return result;
    }
    public static void main(String[] args){
        // Pure constitutive law must preserve old quiet adhesion and allow
        // deliberate differential footholds at EXACTLY same muscle pressure.
        float t=SymbioteMatterV1.substrateTraction(0f,.5f,.5f);
        ok(Math.abs(t-.12f)<1e-6f,"uniform grip anchored the entire body");
        ok(SymbioteMatterV1.substrateTraction(0f,1f,.5f)>t+.25f,
            "local tissue tension is still yoked to muscle compression");
        ok(SymbioteMatterV1.substrateTraction(0f,0f,.5f)<t,
            "locally reduced tissue tension cannot reduce ground loading");
        ok(SymbioteMatterV1.substrateTraction(-.5f,.5f,.5f)>
            SymbioteMatterV1.substrateTraction(0f,.5f,.5f),
            "historic compressed foothold has disappeared");

        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 m=new SymbioteMatterV1();m.reset(b,s);
        // Distort one real shell site WITHOUT commanding any muscle.
        s.x[8]+=3f;
        float beforeX=b.cx,beforeY=b.cy;
        m.step(b,s,new float[64],.025f);
        ok(m.lastElasticForceMagnitude>20f,
            "strained membrane did not generate shell elasticity");
        ok(m.lastMuscleReactionImpulse==0f,
            "PASSIVE spring force was illegally used as fluid muscle reaction");
        ok(m.invalid==0&&MaterialGeometry.selfCrossings(s)==0,
            "passive relaxation damaged the membrane");

        b=new MotorBrain.Body();s=new Morphology.Surface(b);
        m=new SymbioteMatterV1();m.reset(b,s);
        float[] cmd=new float[64];cmd[1]=.85f;
        for(int k=0;k<16;k++)cmd[4*k+3]=0f;
        m.step(b,s,cmd,.025f);
        float ordinaryFoot=speed(s,0,6);
        b=new MotorBrain.Body();s=new Morphology.Surface(b);
        m=new SymbioteMatterV1();m.reset(b,s);
        cmd[3]=1f;cmd[7]=-1f;
        m.step(b,s,cmd,.025f);
        float deliberateFoot=speed(s,0,6);
        ok(Math.abs(ordinaryFoot-deliberateFoot)>.005f,
            "physically changed cortex tension has no effect on local substrate motion");

        // Controller can be measured independently from its brain. No gait
        // in the shipped controller; preset laboratory probes measure plant.
        Result still=sim(0,320),staticDrive=sim(1,320),
               alternating=sim(2,640);
        ok(still.net<2f,"unactuated body generates free propulsion");
        ok(alternating.net>5f&&alternating.net>still.net+5f,
            "same 96+74 body cannot physically translate under different motors");
        ok(alternating.gross>alternating.net,
            "gross/net path measurements are not distinguished");

        SymbioteMotorHeadV1 head=new SymbioteMotorHeadV1();
        MotorBrain.Body hb=new MotorBrain.Body();
        Morphology.Surface hs=new Morphology.Surface(hb);
        SymbioteMatterV1 hm=new SymbioteMatterV1();hm.reset(hb,hs);
        float[] sensed=new float[DistributedBodySense.FEATURES];
        float[] recurrent=new float[RecurrentCoreV22.Z];
        for(int trial=0;trial<2;trial++){
            head.chosen(0,sensed,recurrent,.5f,null,
                SymbioteMotorHeadV1.measuredSectorAngles(hb,hs));
            for(int i=0;i<40;i++){
                float x=hb.cx,y=hb.cy;
                head.fastTick(hb,hs,.025f);
                hm.step(hb,hs,head.commands(),.025f);
                head.observedPhysicalStep(x,y,hb,hs,hm.lastMuscleReactionImpulse,false,.025f);
            }
            head.consequence(0f,0f,0f,0f,true,false);
            if(trial==0)ok(head.fastCircuit().trained()==1,
                "the newborn fast circuit was denied its first exploratory trial");
            else ok(head.fastCircuit().trained()==1,
                "subsequent slow cortical probe contaminated fast synapses");
        }
        ok(hm.invalid==0&&MaterialGeometry.selfCrossings(hs)==0,
            "two-level live controller damaged the fluid body");
        System.out.printf(Locale.US,
            "ZERO168 PASS passiveNet=%.3f staticNet=%.3f alternatingNet=%.3f gross=%.3f "+
            "tensionFootSpeed=[%.4f %.4f], elasticImpulse=0, "+
            "one-owner neural plasticity and no programmed gait%n",
            still.net,staticDrive.net,alternating.net,alternating.gross,
            ordinaryFoot,deliberateFoot);
    }
}
