package com.overmind.zero;
import java.util.*;
import java.lang.reflect.Method;
/** Production-path adversarial regression. No synthetic movement is used
 * as evidence of endogenous skill: artificial fixtures only test rejection. */
public final class Zero171IntegratedRegression {
    static void check(boolean yes,String why){if(!yes)throw new AssertionError("ZERO171: "+why);}
    static void geometry()throws Exception{
        UnknownWorld.Entity p=new UnknownWorld.Entity(0,0,2,6);
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();world.add(p);
        float tip=PanelPhysics.halfLength(p)-2f;
        check(SensorimotorMind.Senses.solid(null,tip,0,world),"real plate tip invisible");
        check(!SensorimotorMind.Senses.solid(null,0,15,world),"phantom square visible");
        MotorBrain.Body b=new MotorBrain.Body();Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 m=new SymbioteMatterV1();m.reset(b,s);
        m.x[0]=tip;m.y[0]=0;
        SymbioteWorldStepV171.interior(m,b,s,null,world,PhysicsClockV171.DT);
        float[] local=PanelPhysics.local(p,m.x[0],m.y[0]);
        check(Math.abs(local[0])>PanelPhysics.halfLength(p)||Math.abs(local[1])>PanelPhysics.HALF_THICKNESS,"production dispatch let liquid through plate");
        m.x[0]=0;m.y[0]=15;m.vx[0]=m.vy[0]=0;
        SymbioteWorldStepV171.interior(m,b,s,null,world,PhysicsClockV171.DT);
        check(m.x[0]==0&&m.y[0]==15,"phantom square collision");
        p.angle=(float)Math.PI/2;
        check(SensorimotorMind.Senses.solid(null,0,tip,world)&&!SensorimotorMind.Senses.solid(null,15,0,world),"rotation ignored by sensor");
        p.angle=0;p.y=33;
        float hit=SensorimotorMind.Senses.firstSolidDistance(null,world,0,0,0,1,100);
        check(Math.abs(hit-26f)<.001f,"thin plate skipped between 22U ray samples");
        check(SpatialPerception.sense(0,0,null,world)[4]<1,"spatial eye failed same obstacle");
        float[][] v={{0,0},{80,0},{80,80},{60,80},{60,20},{20,20},{20,80},{0,80}};
        m.reset(b,s);
        for(int j=0;j<8;j++)for(int t=0;t<12;t++){int i=j*12+t;float u=t/12f;
            s.x[i]=v[j][0]*(1-u)+v[(j+1)%8][0]*u;s.y[i]=v[j][1]*(1-u)+v[(j+1)%8][1]*u;}
        for(int i=0;i<MotorBrain.N;i++){b.x[i]=5+i*.1f;b.y[i]=5;}
        for(int i=0;i<SymbioteMatterV1.INNER;i++){m.x[i]=5;m.y[i]=5+i*.1f;}
        m.x[0]=20.2f;m.y[0]=50;
        Method project=SymbioteMatterV1.class.getDeclaredMethod("projectInterior",MotorBrain.Body.class,Morphology.Surface.class);
        Method inside=SymbioteMatterV1.class.getDeclaredMethod("inside",Morphology.Surface.class,float.class,float.class);
        project.setAccessible(true);inside.setAccessible(true);project.invoke(m,b,s);
        check((Boolean)inside.invoke(null,s,m.x[0],m.y[0]),"concave projection remains outside tissue");
    }
    static void control(){
        PhysicsClockV171 a=new PhysicsClockV171(),b=new PhysicsClockV171();int at=0,bt=0;
        for(int i=0;i<120;i++)at+=a.steps(1f/120f,20);
        for(int i=0;i<30;i++)bt+=b.steps(1f/30f,20);
        check(at==1200&&bt==at,"physics depends on display frame rate");
        check(a.steps(0f,20)==0,"resume fabricated physics time");
        MotorDurationV22 duration=new MotorDurationV22();float[] scores=new float[16];
        duration.select(0,0,scores,false,.1f,0f,0,0);
        check(duration.select(1,1,scores,false,.1f,0,0,0)==0,"hunger still cancels voluntary duration");
        check(duration.select(2,2,scores,false,.1f,1f,0,0)==2,"real harm fails to interrupt");
    }
    static void causality(){
        MotorBrain.Body b=new MotorBrain.Body();Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 m=new SymbioteMatterV1();m.reset(b,skin);SymbioteMotorHeadV1 h=new SymbioteMotorHeadV1();
        float[] ds=new float[DistributedBodySense.FEATURES],state=new float[RecurrentCoreV22.Z];
        h.chosen(0,ds,state,.3f,null,SymbioteMotorHeadV1.measuredSectorAngles(b,skin),b,skin);
        float[] weights=h.verifiedMotorSnapshot();
        for(int i=0;i<40;i++){float x=b.cx,y=b.cy;h.fastTick(b,skin,.025f);m.step(b,skin,h.commands(),.025f);
            h.observedPhysicalStep(x,y,b,skin,m.lastMuscleReactionImpulse,true,.025f);}
        h.consequence(0f,0f,0f,0f,true,false);
        check(Arrays.equals(weights,h.verifiedMotorSnapshot()),"external movement trained motor synapses");
        check(h.skillMemory().verifiedCount()==0,"externally contaminated skill certified");
        SensorimotorMind mind=new SensorimotorMind();BodyProprioception body=new BodyProprioception();
        mind.attachActiveCore(new RecurrentCoreV22());mind.attachBodySchema(body);
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        mind.observe(SensorimotorMind.Senses.read(b,skin,null,empty,70),0,0,b,skin,null,empty);
        mind.activeDiagnostics.allTransitions=4;
        String before=body.encode();mind.contactOutcome(22,0);
        check(before.equals(body.encode()),"energy value weights mutated before heldout boundary");
        mind.observe(SensorimotorMind.Senses.read(b,skin,null,empty,92),.7f,0,b,skin,null,empty);
        String[] encoded=body.encode().split("\\|");
        for(int i=7+BodyProprioception.FEATURES;i<encoded.length;i++)
            check(Float.parseFloat(encoded[i])==0f,"body predictor/value trained on heldout energy");
    }
    static void contextual(){
        ContextualNavigationV171 n=new ContextualNavigationV171();MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);SymbioteMatterV1 m=new SymbioteMatterV1();m.reset(b,s);
        float[] weights=new float[724];n.context(b,s,weights);
        for(int i=0;i<12;i++){n.chosen(0);n.consequence(0,5f,0f,i<8,i>=8,true);}
        check(n.verified(0)&&n.x(0)>4.9f,"valid contextual movement did not validate");
        String stored=n.encode();ContextualNavigationV171 restored=new ContextualNavigationV171();
        check(restored.restore(stored),"contextual checkpoint rejected");restored.context(b,s,weights);
        check(restored.verified(0)&&restored.encode().equals(stored),"contextual checkpoint changed");
        check(!restored.restore(stored+"|broken")&&restored.encode().equals(stored),"malformed navigation destroyed checkpoint");
        for(int i=0;i<96;i++)s.x[i]*=2.5f;
        restored.context(b,s,weights);check(!restored.verified(0),"incompatible posture inherited motion trust");
        m.reset(b,s);Arrays.fill(weights,.2f);restored.context(b,s,weights);
        check(!restored.verified(0),"changed actuator inherited obsolete motion trust");
        check(Arrays.asList(ResetDomains.keys(ResetDomains.NAVIGATION)).contains(ContextualNavigationV171.KEY),"navigation reset omitted contextual memory");
        check(Arrays.asList(ResetDomains.keys(ResetDomains.MEMBRANE)).contains(SymbioteMotorSkillMemoryV169.MANIPULATION_KEY),"muscle reset omitted manipulation archive");
    }
    static void manipulation(){
        SymbioteMotorHeadV1 h=new SymbioteMotorHeadV1();
        String[] shared=h.encode().split("\\|");for(int i=6;i<shared.length;i++)shared[i]="0.0";
        check(h.restore(String.join("|",shared)),"controlled neural fixture shared load");
        StringBuilder local=new StringBuilder("cl161|0");
        for(int sector=0;sector<16;sector++)for(int ch=0;ch<4;ch++)for(int f=0;f<5;f++){
            float radial=Math.cos((sector+.5)*Math.PI/8)>0?.78f:-.43f;
            float v=ch==0&&f==0?(float)(.5*Math.log((1+radial)/(1-radial))/2.8):0f;
            local.append('|').append(v);
        }
        check(h.restoreLocal(local.toString()),"controlled neural fixture regional load");
        // A physical benchmark configuration, executed THROUGH the production
        // head. No separate electrode replaces the output being archived.
        float totalTravel=0f;
        for(int life=0;life<3;life++){
            MotorBrain.Body b=new MotorBrain.Body();Morphology.Surface skin=new Morphology.Surface(b);
            SymbioteMatterV1 matter=new SymbioteMatterV1();matter.reset(b,skin);
            UnknownWorld.Entity p=new UnknownWorld.Entity(29f,0,2,6);p.angle=(float)Math.PI/2;
            float[] ds=DistributedBodySense.sense(b,skin,null,new ArrayList<>());
            h.chosen(8,ds,new float[RecurrentCoreV22.Z],0f,null,
                SymbioteMotorHeadV1.measuredSectorAngles(b,skin),b,skin);
            for(int frame=0;frame<40;frame++){
                float x=b.cx,y=b.cy,px=p.x,py=p.y;
                float[] sx=skin.x.clone(),sy=skin.y.clone(),bx=b.x.clone(),by=b.y.clone();
                p.tick(.025f);h.fastTick(b,skin,.025f);matter.step(b,skin,h.commands(),.025f);
                PanelPhysics.constrain(b,skin,p,.025f,sx,sy,bx,by);
                ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();objects.add(p);
                SymbioteWorldStepV171.interior(matter,b,skin,null,objects,.025f);
                boolean held=PanelPhysics.anyHeld(p);float moved=(float)Math.hypot(p.x-px,p.y-py);
                totalTravel+=moved;h.observedManipulation(moved,held);
                h.observedPhysicalStep(x,y,b,skin,matter.lastMuscleReactionImpulse,true,.025f);
            }
            h.consequence(0f,0f,0f,0f,life==0,life>0);
            if(life<2)h.resetLife();
        }
        check(totalTravel>.45f,"real commanded plate never moved");
        check(h.manipulationMemory().verifiedCount()>0,"real same-controller manipulation did not archive across heldout lives");
        check(h.skillMemory().verifiedCount()==0,"plate interaction mislabeled locomotion");
        String stored=h.encodeManipulation();
        Map<String,Object> profile=new LinkedHashMap<>();
        profile.put(SymbioteMotorSkillMemoryV169.MANIPULATION_KEY,stored);
        profile.put(ContextualNavigationV171.KEY,new ContextualNavigationV171().encode());
        profile.put(LearningDiagnostics.ACTIVE_KEY,new LearningDiagnostics().encode());
        profile.put(SymbioteMotorHeadV1.KEY,h.encode());profile.put(SymbioteMatterV1.ENABLED,true);
        check(SaveProfileCodec.decode(SaveProfileCodec.encode(profile)).equals(profile),"named-slot codec lost new stores");
        SymbioteMotorHeadV1 restored=new SymbioteMotorHeadV1();
        check(restored.restoreManipulation(stored)&&restored.encodeManipulation().equals(stored),"manipulation skill save roundtrip failed");
        // Frozen comparisons must not silently restore archived synapses.
        MotorBrain.Body body=new MotorBrain.Body();Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(body,skin);skin.touchSense[0]=1f;
        String[] changed=h.encodeLocal().split("\\|");changed[2]="0.4";
        check(h.restoreLocal(String.join("|",changed)),"frozen altered controller fixture");
        float[] frozen=h.verifiedMotorSnapshot();h.setAdaptationEnabled(false);
        for(int trial=0;trial<6;trial++){
            h.chosen(8,new float[DistributedBodySense.FEATURES],new float[RecurrentCoreV22.Z],0f,null,
                SymbioteMotorHeadV1.measuredSectorAngles(body,skin),body,skin);
            for(int frame=0;frame<10;frame++)h.observedPhysicalStep(body.cx,body.cy,body,skin,1f,false,.025f);
            h.consequence(0f,0f,0f,0f,true,false);
        }
        check(Arrays.equals(frozen,h.verifiedMotorSnapshot()),"frozen motor used live skill recovery/training");
        System.out.println("ZERO171 actual neural membrane moved held plate, independent manipulation archive, travel="+totalTravel);
    }
    static void liveLoop(){
        Map<String,Object> virgin=new HashMap<>();virgin.put(SymbioteMatterV1.ENABLED,true);
        String unchanged=virgin.toString();
        SymbioteLaboratoryV171.Trial frozen=SymbioteLaboratoryV171.run(virgin,0,0,8f,false,null);
        check(frozen.invalid==0&&frozen.decisions>=10&&frozen.gross>.5f,"actual neural->muscle->material loop inactive");
        check(frozen.motorBefore.equals(frozen.motorAfter),"frozen comparison trained motor weights");
        SymbioteLaboratoryV171.Trial learned=SymbioteLaboratoryV171.run(virgin,0,0,8f,true,null);
        check(learned.invalid==0&&learned.decisions>=10&&!learned.motorBefore.equals(learned.motorAfter),"actual lived feedback failed to update neural motor weights");
        SymbioteLaboratoryV171.Trial repeated=SymbioteLaboratoryV171.run(virgin,0,0,8f,false,null);
        check(frozen.net==repeated.net&&frozen.gross==repeated.gross,"cloned physical comparison not repeatable");
        check(virgin.toString().equals(unchanged),"laboratory altered live saved profile");
        System.out.printf(Locale.US,"ZERO171 real policy 8s frozen/training: net %.3f/%.3f gross %.3f/%.3f signed %.3f/%.3f; NOT evidence of uplift%n",frozen.net,learned.net,frozen.gross,learned.gross,frozen.signed,learned.signed);
    }
    public static void main(String[] a)throws Exception{
        geometry();control();causality();contextual();manipulation();liveLoop();
        System.out.println("ZERO171 PASS actual dispatch/vision/concavity, fixed time, low-energy duration, causal credit, heldout, contextual saved motion, actual frozen-vs-learning symbiote loop");
    }
}
