package com.overmind.zero;

import java.util.Arrays;
import java.util.Random;

/** Physically grounded no-reward/no-world acceptance gate for liquid matter. */
public final class SymbioteMatterV1Regression {
    static void require(boolean v,String message){if(!v)throw new AssertionError(message);}
    static float radius(Morphology.Surface s,float cx,float cy,int start,int count){
        float max=0;
        for(int i=start;i<start+count;i++){
            int j=i%Morphology.POINTS;
            max=Math.max(max,(float)Math.hypot(s.x[j]-cx,s.y[j]-cy));
        }
        return max;
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 blob=new SymbioteMatterV1();
        blob.reset(body,skin);
        require(blob.restArea>2300&&blob.restArea<2600,"mass is not allocated");
        require(Math.abs(body.cx)<.01f&&Math.abs(body.cy)<.01f,"ghost nucleus");
        float[] calm=new float[SymbioteMatterV1.SECTORS*SymbioteMatterV1.OUTPUTS];
        for(int i=0;i<120;i++)blob.step(body,skin,calm,.025f);
        require(blob.invalid==0,"passive solver diverged");
        require(blob.areaRelativeError(skin)<.075f,"passive area drift too large");
        require(MaterialGeometry.selfCrossings(skin)==0,"fluid contour folded without actuation");

        float[] directed=calm.clone();
        // External physics-laboratory command, NOT a behavior coded in AI.
        directed[0]=.96f;
        directed[SymbioteMatterV1.OUTPUTS]=.80f;
        directed[2*SymbioteMatterV1.OUTPUTS]=.53f;
        float startCenter=body.cx,tipStart=radius(skin,body.cx,body.cy,0,12);
        for(int i=0;i<75;i++){
            blob.step(body,skin,directed,.025f);
            require(blob.invalid==0,"controlled extension diverged");
            require(blob.areaRelativeError(skin)<.12f,"pseudopod creates mass");
            require(MaterialGeometry.selfCrossings(skin)==0,
                    "cortex ripped/folded while extending pseudopod");
        }
        float tip=radius(skin,body.cx,body.cy,0,12);
        float opposite=radius(skin,body.cx,body.cy,43,14);
        float drift=(float)Math.hypot(body.cx-startCenter,body.cy);
        require(tip>tipStart+5,"no independent pseudopod exists");
        require(tip>opposite+3,"whole body inflated instead of local extension");
        require(drift<tip-tipStart+15,"entire organism follows the pseudopod");

        for(int i=0;i<140;i++)blob.step(body,skin,calm,.025f);
        require(blob.areaRelativeError(skin)<.075f,
                "relaxed viscofluid cannot recover conserved volume");
        require(MaterialGeometry.selfCrossings(skin)==0,
                "relaxation collapsed into a crossed membrane");
        require(blob.invalid==0,"unstable relaxation");

        RecurrentCoreV22 primary=new RecurrentCoreV22();
        SymbioteMotorHeadV1 motor=new SymbioteMotorHeadV1();
        float[] readings=new float[DistributedBodySense.FEATURES];
        primary.sense(new float[26],readings,null,null);
        motor.chosen(4,readings,primary.state(),.25f);
        for(float command:motor.commands())
            require(Float.isFinite(command)&&Math.abs(command)<=1f,
                "neural motor command out of physical bounds");
        motor.consequence(20,0,blob.work,true,false);
        String saved=motor.encode();
        SymbioteMotorHeadV1 copy=new SymbioteMotorHeadV1();
        require(copy.restore(saved)&&copy.encode().equals(saved),
            "motoring cortex persistence lost");
        require(!copy.restore("smh1|corrupted")&&copy.encode().equals(saved),
            "corrupted save mutated surviving motor weights");
        copy.resetLife();
        require(copy.encode().equals(saved),"death destroyed trained motor weights");
        System.out.println("PASS SYMBIOTE matter96+64+10, area, pseudopod, cortex continuity, no floating nucleus, motor persistence");
    }
}
