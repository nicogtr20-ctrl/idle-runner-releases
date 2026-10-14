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
        // Same direction, same sensed body: high action bank must actually
        // express voluntary adhesion, not silently map to action % 8.
        motor.chosen(4,readings,primary.state(),0f);
        float normalGrip=0f;
        for(int i=2;i<motor.commands().length;i+=4)
            normalGrip+=Math.max(0,motor.commands()[i]);
        motor.chosen(12,readings,primary.state(),0f);
        float intentionalGrip=0f;
        int usableGrip=0;
        for(int i=2;i<motor.commands().length;i+=4){
            intentionalGrip+=Math.max(0,motor.commands()[i]);
            if(motor.commands()[i]>Morphology.GRIP_THRESHOLD)usableGrip++;
        }
        require(intentionalGrip>normalGrip+2.5f&&usableGrip>=8,
            "2.2 action bank grip intent never reaches physical grip solver");

        // An organism's apparent position is the mass-weighted full body,
        // not the legacy ten convenient parcels. No special target/food.
        MotorBrain.Body probeBody=new MotorBrain.Body();
        Morphology.Surface probeSkin=new Morphology.Surface(probeBody);
        SymbioteMatterV1 probeMatter=new SymbioteMatterV1();
        probeMatter.reset(probeBody,probeSkin);
        for(int i=0;i<SymbioteMatterV1.INNER;i++)
            probeMatter.x[i]+=10f;
        probeMatter.observeBody(probeBody,probeSkin);
        require(probeBody.cx>3.0f&&probeBody.cx<5.0f,
            "whole-mass position ignored the 64 fluid particles");

        // Verify there is an actual physics-grounded substrate reaction.
        // Unlike extension-only tests, real asymmetric material commands
        // must be able to change organism position without a food reward.
        MotorBrain.Body crawler=new MotorBrain.Body();
        Morphology.Surface crawlSkin=new Morphology.Surface(crawler);
        SymbioteMatterV1 crawlMatter=new SymbioteMatterV1();
        crawlMatter.reset(crawler,crawlSkin);
        float[] crawl=new float[SymbioteMatterV1.SECTORS*SymbioteMatterV1.OUTPUTS];
        for(int tick=0;tick<320;tick++){
            boolean phase=(tick/20)%2==0;
            for(int sector=0;sector<SymbioteMatterV1.SECTORS;sector++){
                boolean forward=Math.cos((sector+.5)*Math.PI/8)>0;
                crawl[4*sector]=forward?(phase?.78f:-.78f)
                                      :(phase?-.43f:.43f);
                // Opposed substrate contact makes contraction produce
                // external friction instead of free-floating internal shear.
                crawl[4*sector+2]=forward?(phase?-.95f:.95f)
                                        :(phase?.95f:-.95f);
            }
            crawlMatter.step(crawler,crawlSkin,crawl,.025f);
            require(crawlMatter.invalid==0&&
                    MaterialGeometry.selfCrossings(crawlSkin)==0,
                "substrate-actuated gel lost physical integrity");
        }
        require(Math.abs(crawler.cx)>.8f,
            "symbiote still cannot locomote despite substrate traction");
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
