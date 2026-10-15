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
    public static void main(String[] args) throws Exception{
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

        // Exact 96-point contour geometry from the user's explicitly
        // exported recent ZERO scene, centered to (0,0); no player or phone
        // identifiers, neuron weights, location, or private contents.
        // The real saved membrane had 1 intersecting edge pair and 0/96
        // nonzero x/y velocities while the internal matter was still active.
        float[] badX={44.0776f,44.9218f,44.977f,43.6195f,44.4972f,44.1282f,43.322f,42.3637f,41.2841f,40.0532f,38.6121f,36.8994f,34.8531f,32.5914f,30.1151f,27.4597f,24.6596f,21.7295f,18.6289f,15.4716f,12.2532f,8.9998f,5.7319f,2.4686f,-0.8094f,-4.1054f,-7.4485f,-10.8347f,-14.2535f,-17.7065f,-21.1044f,-24.1141f,-26.6909f,-28.8556f,-30.6366f,-32.0717f,-33.1793f,-34.3158f,-35.4768f,-36.6691f,-37.9093f,-39.1617f,-39.9738f,-39.2934f,-37.9125f,-36.3213f,-34.6682f,-33.0747f,-31.6941f,-30.1056f,-28.3877f,-26.5733f,-24.6689f,-22.6942f,-20.7062f,-18.7373f,-16.8315f,-15.0186f,-13.3235f,-11.758f,-10.328f,-9.2149f,-8.4134f,-7.8927f,-7.6168f,-7.5397f,-7.6266f,-7.7661f,-8.1376f,-8.8976f,-9.8527f,-11.8138f,-12.7523f,-10.9192f,-7.7022f,-5.849f,-4.1065f,-2.27f,-0.174f,1.9474f,4.0948f,6.2653f,8.4528f,10.6534f,12.9227f,15.4576f,18.2669f,21.2906f,24.435f,27.5853f,30.6093f,33.5105f,36.1974f,38.6156f,40.7314f,42.5444f};
        float[] badY={21.1888f,22.2734f,22.9768f,23.0265f,24.2859f,24.8999f,25.3643f,25.7244f,26.009f,26.2244f,26.377f,26.4851f,26.5064f,26.5059f,26.4671f,26.4066f,26.3095f,26.1156f,25.7433f,25.2651f,24.6731f,23.9645f,23.1325f,22.184f,21.0803f,19.8641f,18.5345f,17.1301f,15.6858f,14.2465f,12.8609f,11.4244f,9.9772f,8.5615f,7.2094f,5.9535f,4.7859f,3.7817f,2.936f,2.2576f,1.7638f,1.4635f,1.2188f,0.7163f,-0.0138f,-0.8989f,-1.9256f,-3.0816f,-4.352f,-5.6966f,-7.1375f,-8.6904f,-10.3745f,-12.1987f,-14.1635f,-16.2533f,-18.3873f,-20.4564f,-22.3747f,-24.0616f,-25.5398f,-27.1446f,-28.8975f,-30.77f,-32.7236f,-34.7019f,-36.5617f,-38.2199f,-39.4802f,-40.143f,-40.3683f,-39.7529f,-39.3778f,-39.549f,-41.0545f,-40.5023f,-39.0745f,-36.8639f,-33.9806f,-31.0009f,-27.9341f,-24.7285f,-21.3537f,-17.7834f,-14.0544f,-10.338f,-6.6644f,-3.023f,0.5784f,4.1175f,7.5116f,10.6426f,13.4438f,15.8999f,18.0029f,19.7554f};
        MotorBrain.Body realBody=new MotorBrain.Body();
        Morphology.Surface realSkin=new Morphology.Surface(realBody);
        SymbioteMatterV1 realMatter=new SymbioteMatterV1();
        realMatter.reset(realBody,realSkin);
        System.arraycopy(badX,0,realSkin.x,0,Morphology.POINTS);
        System.arraycopy(badY,0,realSkin.y,0,Morphology.POINTS);
        require(MaterialGeometry.selfCrossings(realSkin)==1,
            "live-scene witness must have exactly one crossed edge pair");
        for(int k=0;k<130;k++)realMatter.step(realBody,realSkin,calm,.025f);
        require(MaterialGeometry.selfCrossings(realSkin)==0,
            "actual user-exported scene cannot untangle under real matter");
        for(int k=0;k<50;k++)realMatter.step(realBody,realSkin,tMove,.025f);
        int activeReal=0;
        for(int i=0;i<Morphology.POINTS;i++)
            if(Math.hypot(realSkin.vx[i],realSkin.vy[i])>.015f)activeReal++;
        require(activeReal>=8,
            "actual formerly locked 96-site contour stayed immobilized");
        require(realMatter.invalid==0,
            "live-scene topology recovery produced invalid matter");

        // OTA149: physical senses must sample REAL newly actuated gel.
        // The body runs exactly the same physics as OTA148; sensors only.
        MotorBrain.Body sensedBody=new MotorBrain.Body();
        Morphology.Surface sensedSkin=new Morphology.Surface(sensedBody);
        SymbioteMatterV1 sensedMatter=new SymbioteMatterV1();
        sensedMatter.reset(sensedBody,sensedSkin);
        require(!sensedSkin.hasValidMotorTargets(),
            "newborn invents a pre-action motor target");
        for(int i=0;i<90;i++)sensedMatter.step(sensedBody,sensedSkin,directed,.025f);
        require(sensedSkin.hasSymbioteMotorTargets(),
            "symbiote never registered physical cortex motor target");
        float[] imp=LocalImpedanceSense.sense(sensedBody,sensedSkin);
        float demand=0f;
        for(int i=0;i<Morphology.POINTS;i++)
            demand+=imp[i*LocalImpedanceSense.CHANNELS+
                LocalImpedanceSense.MOTOR_DEMAND];
        require(demand>.01f,
            "motor demand absent despite executed fluid commands");
        int disturbed=4,base=disturbed*LocalImpedanceSense.CHANNELS;
        sensedSkin.x[disturbed]+=6f; // fixture: real collision projection
        float[] resisted=LocalImpedanceSense.sense(sensedBody,sensedSkin);
        require(Math.abs(resisted[base+LocalImpedanceSense.RADIAL_ERROR])+
                Math.abs(resisted[base+LocalImpedanceSense.TANGENT_ERROR])>.08f,
            "actual local collision strain invisible to impedance receptor");
        require(sensedSkin.peakExtension>0f,
            "actual pseudopod extension never reaches primary body channel");
        float[] primarySenses=SensorimotorMind.Senses.read(
            sensedBody,sensedSkin,null,new java.util.ArrayList<UnknownWorld.Entity>(),70f);
        require(primarySenses[3]>0f,
            "primary 26-channel brain remains blind to achieved body shape");
        // Both the full 96-particle sensory map and the 8-sector readout
        // must react to physical membrane shape, not a desired endpoint.
        float[] distributed=DistributedBodySense.sense(
            sensedBody,sensedSkin,null,new java.util.ArrayList<UnknownWorld.Entity>());
        float[] proprio=BodyProprioception.sense(sensedBody,sensedSkin);
        require(distributed.length==96*9&&proprio.length==8*6,
            "body sensory width broke old saved cortex state");
        float shape=0f;
        for(int i=0;i<Morphology.POINTS;i++)
            shape+=distributed[i*DistributedBodySense.CHANNELS+
                DistributedBodySense.SHAPE];
        require(shape>1f,"distributed membrane shape absent");

        // Tactile transients must carry real temporal meaning. One event,
        // no new contact: exponentially decay by elapsed simulated time,
        // not by UI refresh/frame count or the obsolete legacy membrane.
        MotorBrain.Body pulseBody=new MotorBrain.Body();
        Morphology.Surface pulseSkin=new Morphology.Surface(pulseBody);
        SymbioteMatterV1 pulseMatter=new SymbioteMatterV1();
        pulseMatter.reset(pulseBody,pulseSkin);
        pulseSkin.touchPulse[27]=1f;
        for(int i=0;i<80;i++)pulseMatter.step(pulseBody,pulseSkin,calm,.025f);
        float expected=(float)Math.exp(-2.5f*2f);
        require(Math.abs(pulseSkin.touchPulse[27]-expected)<.002f,
            "symbiote retained an ancient wind/collision pulse forever");
        require(pulseSkin.touchSense[27]==0f&&pulseSkin.gripHold[27]==0f,
            "tactile decay manufactured an obstacle or a hold");
        require(Math.abs(pulseSkin.t-2f)<.01f,
            "new body's material proprioceptive time did not advance");

        // The active primary 2.2 must see the ACTUAL 64 cortex outputs
        // through unchanged six-dimensional channels, never unused legacy
        // motor/hand/membrane weights. No brain learning is needed here.
        SensorimotorMind actionSense=new SensorimotorMind();
        float[] ctlA=new float[64],ctlB=new float[64];
        for(int q=0;q<16;q++){
            ctlB[q*4]=.6f;
            ctlB[q*4+1]=-.3f;
            ctlB[q*4+2]=.75f;
            ctlB[q*4+3]=.45f;
        }
        actionSense.appliedSymbioteControls(ctlA);
        float[] copyA=actionSense.appliedEfferenceForTest();
        actionSense.appliedSymbioteControls(ctlB);
        float[] copyB=actionSense.appliedEfferenceForTest();
        require(copyA.length==6&&copyB.length==6,
            "core 2.2 readout width changed, breaking existing brain saves");
        require(Math.abs(copyB[0]-.6f)<.0001f&&
                Math.abs(copyB[3]+.3f)<.0001f&&
                Math.abs(copyB[4]-.75f)<.0001f&&
                Math.abs(copyB[5]-.45f)<.0001f,
            "primary efference is not genuine cortex pressure/shear/grip/stiffness");
        require(Math.abs(copyB[0]-copyA[0])>.2f,
            "different actually executed muscles looked identical to brain");
        RecurrentCoreV22 recipient=new RecurrentCoreV22();
        recipient.sense(new float[26],null,null,copyB);
        float[] realState=recipient.state();
        for(int q=0;q<6;q++)require(Math.abs(realState[138+q]-copyB[q])<.00001f,
            "primary core did not receive applied symbiote efference");

        // Legacy body must keep original motor scaling and sensor
        // timing: the new snapshot format cannot silently upgrade it.
        MotorBrain.Body legacyBody=new MotorBrain.Body();
        Morphology.Surface legacySkin=new Morphology.Surface(legacyBody);
        require(!legacySkin.hasValidMotorTargets()&&
                !legacySkin.hasSymbioteMotorTargets(),
            "legacy limb targets enabled before physical actuation");
        float[] legacyMuscle=Morphology.virgin(new Random(143));
        legacySkin.step(legacyBody,legacyMuscle,65f,15f,.025f);
        require(legacySkin.hasValidMotorTargets()&&
                !legacySkin.hasSymbioteMotorTargets(),
            "original membrane targets corrupted by Symbiote sensing");
        float[] legacyRead=LocalImpedanceSense.sense(legacyBody,legacySkin);
        require(legacyRead.length==LocalImpedanceSense.FEATURES,
            "legacy impedance schema was modified");

        // OTA150: do the sensors actually INFLUENCE the sole active
        // recurrent action rank, or are they merely accumulating logs?
        // Inject a tiny learned heldout-validated choice value directly
        // into the existing model readout, without moving the game body.
        SensorimotorMind functional=new SensorimotorMind();
        BodyProprioception bp=new BodyProprioception();
        DistributedBodySense db=new DistributedBodySense();
        LocalImpedanceSense li=new LocalImpedanceSense();
        functional.attachBodySchema(bp);
        functional.attachDistributedBody(db);
        functional.attachImpedance(li);
        // Unvalidated memories never bias live decisions.
        java.lang.reflect.Field wb=BodyProprioception.class.getDeclaredField("values");
        java.lang.reflect.Field wd=DistributedBodySense.class.getDeclaredField("values");
        java.lang.reflect.Field wi=LocalImpedanceSense.class.getDeclaredField("values");
        wb.setAccessible(true);wd.setAccessible(true);wi.setAccessible(true);
        float[] bWeights=(float[])wb.get(bp);
        float[] dWeights=(float[])wd.get(db);
        float[] iWeights=(float[])wi.get(li);
        int desiredAction=7,otherAction=5;
        bWeights[desiredAction*(BodyProprioception.FEATURES+1)+BodyProprioception.FEATURES]=2.5f;
        dWeights[desiredAction*(DistributedBodySense.SUMMARY+1)+DistributedBodySense.SUMMARY]=2.5f;
        iWeights[desiredAction*(LocalImpedanceSense.SUMMARY+1)+LocalImpedanceSense.SUMMARY]=2.5f;
        require(functional.liveBodyEvidence(desiredAction)==0f,
            "unvalidated tissue predictions hijacked the 2.2 policy");
        // Heldout statistics become trustworthy solely from real experience;
        // the injection here is a test fixture, never enabled in production.
        bp.validations=db.validations=li.validations=40;
        bp.predictionError=db.predictionError=li.predictionError=.001f;
        bp.unchangedError=db.unchangedError=li.unchangedError=.2f;
        float physicalEvidence=functional.liveBodyEvidence(desiredAction);
        require(physicalEvidence>.15f&&
                Math.abs(functional.liveBodyEvidence(otherAction))<.00001f,
            "trained proprioception and actual impedance still ignored");
        float[] learned=new float[RecurrentCoreV22.A];
        learned[desiredAction]=physicalEvidence;
        float[] noEvidence=new RecurrentCoreV22().primaryScores(new float[RecurrentCoreV22.A]);
        float[] withEvidence=new RecurrentCoreV22().primaryScores(learned);
        require(Math.abs(withEvidence[desiredAction]-noEvidence[desiredAction]-
                         .4f*physicalEvidence)<.0001f,
            "the sole recurrent action scorer discarded trained body evidence");
        require(Math.abs(withEvidence[otherAction]-noEvidence[otherAction])<.00001f,
            "one localized body observation fabricated unrelated action values");

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
