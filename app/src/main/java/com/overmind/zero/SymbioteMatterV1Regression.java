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
        // OTA146 real-phone regression: global object-grip intention
        // inadvertently pinned ALL floor contacts even without an object.
        // A voluntary grip is for a REAL contacted obstacle; it must not
        // change free-space floor friction when the active shape commands
        // are otherwise identical. The two organic bodies get exactly
        // the same muscle commands, except voluntary obstacle grip.
        MotorBrain.Body freeBody=new MotorBrain.Body();
        Morphology.Surface freeSkin=new Morphology.Surface(freeBody);
        SymbioteMatterV1 freeMatter=new SymbioteMatterV1();
        freeMatter.reset(freeBody,freeSkin);
        MotorBrain.Body gripBody=new MotorBrain.Body();
        Morphology.Surface gripSkin=new Morphology.Surface(gripBody);
        SymbioteMatterV1 gripMatter=new SymbioteMatterV1();
        gripMatter.reset(gripBody,gripSkin);
        float[] freeCmd=new float[crawl.length],gripCmd=new float[crawl.length];
        for(int sector=0;sector<SymbioteMatterV1.SECTORS;sector++){
            float angle=(float)((sector+.5f)*Math.PI/8f);
            freeCmd[4*sector]=.65f*(float)Math.cos(angle);
            freeCmd[4*sector+1]=.20f*(float)Math.sin(angle);
            freeCmd[4*sector+3]=.40f;
        }
        System.arraycopy(freeCmd,0,gripCmd,0,freeCmd.length);
        for(int sector=0;sector<SymbioteMatterV1.SECTORS;sector++)
            gripCmd[4*sector+2]=.90f;
        for(int tick=0;tick<120;tick++){
            freeMatter.step(freeBody,freeSkin,freeCmd,.025f);
            gripMatter.step(gripBody,gripSkin,gripCmd,.025f);
        }
        for(int i=0;i<Morphology.POINTS;i++)
            require(Math.abs(freeSkin.x[i]-gripSkin.x[i])<1e-4f&&
                    Math.abs(freeSkin.y[i]-gripSkin.y[i])<1e-4f,
                "uncontacted object-grip intention froze membrane on the floor");
        require(Math.abs(freeBody.cx-gripBody.cx)<1e-4f&&
                Math.abs(freeBody.cy-gripBody.cy)<1e-4f,
            "gripping empty space changed locomotion physics");

        // A long-lived organism must still respond to NEW local muscular
        // commands after 40s of simulated time. Extending a pseudopod is
        // an external laboratory motor input, not an in-game gait/reward.
        MotorBrain.Body olderBody=new MotorBrain.Body();
        Morphology.Surface olderSkin=new Morphology.Surface(olderBody);
        SymbioteMatterV1 olderMatter=new SymbioteMatterV1();
        olderMatter.reset(olderBody,olderSkin);
        for(int t=0;t<1640;t++)olderMatter.step(olderBody,olderSkin,calm,.025f);
        float oldTip=radius(olderSkin,olderBody.cx,olderBody.cy,0,12);
        for(int t=0;t<75;t++)olderMatter.step(olderBody,olderSkin,directed,.025f);
        require(radius(olderSkin,olderBody.cx,olderBody.cy,0,12)>oldTip+4f,
            "symbiote did not respond to muscles after 41 seconds");
        require(olderMatter.invalid==0&&
                MaterialGeometry.selfCrossings(olderSkin)==0,
            "late-life commands damaged fluid topology");

        // OTA148 actual-phone topology-lock reproducer. Real exported
        // working Symbiote had one crossed edge and ALL 96 shell velocities
        // exactly zero, even while 74 internal parcels kept moving.
        // Swapping two adjacent membrane points simulates a one-crossing
        // persisted scene without food, scripted gait or learning reward.
        MotorBrain.Body tBody=new MotorBrain.Body();
        Morphology.Surface tSkin=new Morphology.Surface(tBody);
        SymbioteMatterV1 tMatter=new SymbioteMatterV1();
        tMatter.reset(tBody,tSkin);
        int a=17,b=18;
        float temp=tSkin.x[a];tSkin.x[a]=tSkin.x[b];tSkin.x[b]=temp;
        temp=tSkin.y[a];tSkin.y[a]=tSkin.y[b];tSkin.y[b]=temp;
        require(MaterialGeometry.selfCrossings(tSkin)>0,
            "invalid-scene topology fixture not crossed");
        for(int k=0;k<90;k++)tMatter.step(tBody,tSkin,calm,.025f);
        require(MaterialGeometry.selfCrossings(tSkin)==0,
            "persisted membrane crossing never physically recovers");
        float[] tMove=new float[calm.length];
        tMove[0]=.88f;tMove[4]=.64f;tMove[8]=.32f;
        for(int k=0;k<60;k++)tMatter.step(tBody,tSkin,tMove,.025f);
        int activeSites=0;
        for(int i=0;i<Morphology.POINTS;i++)
            if(Math.hypot(tSkin.vx[i],tSkin.vy[i])>.015f)activeSites++;
        require(activeSites>=8,
            "crossed-scene recovery still freezes all 96 membrane velocities");
        require(tMatter.invalid==0,"recovering crossed scene caused invalid fluid");

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
