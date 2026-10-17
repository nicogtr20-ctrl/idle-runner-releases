package com.overmind.zero;

import java.util.*;

/**
 * V3.35, forensic laboratory only. It DOES NOT correct contact, pick a gait,
 * tune old V333 blind scenes or train weights. Uses the original physical
 * BOX pipeline. An optional observer measures the exact first mutation
 * that changes a simple 96-point closed membrane into a self-crossing.
 */
public final class Zero3BoxSubstageForensicV335Regression {
    private static final int[] CASES={333,337,341,641,645,649};
    private static final float DT=.025f;
    private static final int SUBSTEPS=2;
    private Zero3BoxSubstageForensicV335Regression(){}

    private static void check(boolean value,String message){
        if(!value)throw new AssertionError("V335 "+message);
    }
    private static void bits(StringBuilder sb,float v){
        sb.append(Float.floatToRawIntBits(v)).append(',');
    }
    private static void vals(StringBuilder sb,float[] v){
        for(float x:v)bits(sb,x);
    }
    /**
     * Full matched state signature, not just the first 96 surface positions:
     * body, shell velocities and grip, all real fluid parcels, box ownership,
     * and physics counters. Every probe must be read-only.
     */
    private static String physicalSignature(
            Zero3BoxCrossingForensicV334Regression.State s){
        StringBuilder out=new StringBuilder(19000);
        Morphology.Surface m=s.p.skin;
        MotorBrain.Body b=s.p.body;
        SymbioteMatterV1 gel=s.p.gel;
        vals(out,m.x);vals(out,m.y);vals(out,m.vx);vals(out,m.vy);
        vals(out,m.gripHold);vals(out,m.gripCommand);
        vals(out,m.touchSense);vals(out,m.touchPulse);
        vals(out,b.x);vals(out,b.y);vals(out,b.vx);vals(out,b.vy);
        bits(out,b.cx);bits(out,b.cy);
        vals(out,gel.x);vals(out,gel.y);vals(out,gel.vx);vals(out,gel.vy);
        out.append(gel.invalid).append(',').append(gel.ticks).append(',')
           .append(gel.topologyRepairs).append(',').append(gel.selfContactPairs);
        for(UnknownWorld.Entity e:s.p.world){
            bits(out,e.x);bits(out,e.y);bits(out,e.vx);bits(out,e.vy);
            out.append(e.bilateralHeldNow).append(',').append(e.everCollided);
            vals(out,e.boxGripHold);
        }
        return out.toString();
    }
    private static UnknownWorld.Entity box(
           Zero3BoxCrossingForensicV334Regression.State s){
        for(UnknownWorld.Entity e:s.p.world)if(e.isBilateralBox())return e;
        throw new AssertionError("V335 fixture has no bilateral BOX");
    }
    private static int held(UnknownWorld.Entity box){
        int n=0;
        for(float t:box.boxGripHold)if(t>=Morphology.GRIP_HOLD)n++;
        return n;
    }
    private static final class Trace implements SquareGripPuzzle.StageObserver {
        final int ordinal,frame;
        final String mode;
        int previousCross,firstCross=-1,stageCount;
        String firstStage="NONE";
        Trace(int id,int step,String m,int initial){
            ordinal=id;frame=step;mode=m;previousCross=initial;
        }
        @Override public void stage(String name,MotorBrain.Body body,
                                    Morphology.Surface skin,
                                    UnknownWorld.Entity box){
            stageCount++;
            int cross=MaterialGeometry.selfCrossings(skin);
            int links=SquareCollisionSolver.intersections(skin,box);
            int[] pair=Zero3BoxCrossingForensicV334Regression.firstPair(skin);
            if(previousCross==0&&cross>0&&firstCross<0){
                firstCross=stageCount;
                firstStage=name;
            }
            int holds=0;
            for(float t:skin.gripHold)
                if(t>=Morphology.GRIP_HOLD)holds++;
            System.out.println(String.format(Locale.US,
                "V335 STAGE id=%d mode=%s sub=%d order=%02d name=%s "+
                "cross=%d pair=%d,%d throughWallLinks=%d skinHeld=%d "+
                "boxHeld=%d boxBilateral=%s com=%.5f,%.5f",
                ordinal,mode,frame,stageCount,name,cross,
                pair[0],pair[1],links,holds,held(box),
                box.bilateralHeldNow,body.cx,body.cy));
            previousCross=cross;
        }
    }
    /**
     * Same V322B world ordering, except the optional stage observer inside
     * the original constrain. Matched unobserved world must stay bitwise equal.
     */
    private static void observedWorld(
           Zero3BoxCrossingForensicV334Regression.State s,
           float[] sx,float[] sy,float[] bx,float[] by,
           float cx,float cy,Trace probe){
        boolean boxes=false;
        for(UnknownWorld.Entity e:s.p.world)
            if(e.isBilateralBox()){
                SquareGripPuzzle.constrain(s.p.body,s.p.skin,e,DT,
                    sx,sy,bx,by,cx,cy,probe);
                boxes=true;
            }
        if(boxes)
            MaterialContactConstraints.reconcile(
                s.p.body,s.p.skin,s.p.world,DT);
        SymbioteWorldStepV171.interior(
            s.p.gel,s.p.body,s.p.skin,null,s.p.world,DT);
        GripSurfaceComposition.compose(s.p.skin,null,s.p.world);
        for(UnknownWorld.Entity e:s.p.world){
            if(e.isMovable()){
                Morphology.collide(s.p.skin,e,DT);
                ContactLearning.collide(s.p.body,e,DT);
                s.p.gel.observeBody(s.p.body,s.p.skin);
            }
            e.tick(DT);
        }
    }
    private static void runCase(int id,boolean actualMotor,
                                Zero3ContextAdhesionV319 pretrained){
        boolean blind=id>=600;
        Zero3BoxCrossingForensicV334Regression.State ref=
            new Zero3BoxCrossingForensicV334Regression.State(id,blind);
        Zero3BoxCrossingForensicV334Regression.State trace=
            new Zero3BoxCrossingForensicV334Regression.State(id,blind);
        Zero3BoxCrossingForensicV334Regression.State noWorld=
            new Zero3BoxCrossingForensicV334Regression.State(id,blind);
        for(Zero3BoxCrossingForensicV334Regression.State s:
            new Zero3BoxCrossingForensicV334Regression.State[]{ref,trace,noWorld}){
            s.warmup();s.spawn();
            check(MaterialGeometry.selfCrossings(s.p.skin)==0,
                  "pre-world shell crossed id="+id);
        }
        check(physicalSignature(ref).equals(physicalSignature(trace)),
              "unmatched initial real physics");
        String mode=actualMotor?"ACTOR":"ZERO_MOTOR";
        Zero3LearningFoundation.Coordinator coordinator=null;
        Zero3ContextAdhesionV319 head=null;
        Zero3MotorIntentV316.Authority authority=null;
        Zero3MotorIntentV316 intent=null;
        if(actualMotor){
            long ep=(blind?3337000L:3331000L)+id;
            Zero3AutonomousLoopV317.Encoder actor=
                new Zero3AutonomousLoopV317.Encoder(0x31720261017L);
            actor.resetEpisode();
            Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
                ep,0,ref.p.gel.ticks*(double)DT,true,ref.p.body,
                ref.p.skin,ref.p.gel,ref.p.world,null);
            check(pre.intact(),"actual pre not intact");
            actor.before(pre);
            float[] choice=actor.choose();
            Zero3LearningFoundation.AdaptiveCortex cortex=
                new Zero3LearningFoundation.AdaptiveCortex(0f);
            coordinator=new Zero3LearningFoundation.Coordinator(cortex);
            coordinator.begin(new Zero3LearningFoundation.MotorIntent(
                ep,0,cortex.revision(),choice[0],choice[1],
                choice[2],choice[3],12*DT),ref.p.body,false,true,false);
            head=pretrained.frozenCopy();
            head.begin();
            authority=new Zero3MotorIntentV316.Authority(
                ep,Zero3AutonomousLoopV317.OWNER);
            intent=authority.issue(Zero3AutonomousLoopV317.OWNER,
                0,0,333,choice[0],choice[1],choice[2],choice[3],
                12*DT,.35f);
        }
        try{
            for(int sub=0;sub<SUBSTEPS;sub++){
                float[] motor=new float[64];
                if(actualMotor){
                    motor=coordinator.actuate(ref.p.body,ref.p.skin,DT);
                    head.actuate(motor,intent,ref.p.body,ref.p.skin,ref.p.world);
                    motor=motor.clone();
                }
                float work=ref.p.gel.work;
                float[] sx=ref.p.skin.x.clone(),sy=ref.p.skin.y.clone();
                float[] bx=ref.p.body.x.clone(),by=ref.p.body.y.clone();
                float cx=ref.p.body.cx,cy=ref.p.body.cy;
                ref.p.gel.step(ref.p.body,ref.p.skin,motor,DT);
                trace.p.gel.step(trace.p.body,trace.p.skin,motor,DT);
                noWorld.p.gel.step(noWorld.p.body,noWorld.p.skin,motor,DT);
                check(physicalSignature(ref).equals(physicalSignature(trace)),
                     "instrumented vs original diverged before BOX");
                int before=MaterialGeometry.selfCrossings(ref.p.skin);
                Trace observer=new Trace(id,sub,mode,before);
                Zero3MultiSceneRestartV322BRegression.actualWorldStep(
                    ref.p,sx,sy,bx,by,cx,cy);
                observedWorld(trace,sx,sy,bx,by,cx,cy,observer);
                check(observer.stageCount==10,
                    "missing observed substage count="+observer.stageCount);
                check(physicalSignature(ref).equals(physicalSignature(trace)),
                    "read-only instrumentation changed original physical output");
                check(MaterialGeometry.selfCrossings(noWorld.p.skin)==0,
                    "no-world negative control crossed");
                int after=MaterialGeometry.selfCrossings(ref.p.skin);
                if((id==337&&sub==1)||(id==645&&sub==0)){
                    check(before==0&&after==1&&observer.firstCross>0,
                        "failed to reproduce true V333 censors id="+id);
                }else if(id!=337&&id!=645){
                    check(after==0,"previously clean matched control crossed");
                }
                System.out.println("V335 MATCHED id="+id+
                    " mode="+mode+" sub="+sub+" before="+before+
                    " after="+after+" firstSubstage="+observer.firstStage+
                    " exactState=true noWorldCross=0"+
                    " held="+held(box(ref)));
                if(actualMotor){
                    float measured=Math.max(0,ref.p.gel.work-work);
                    coordinator.observe(ref.p.body,DT,measured,
                        Math.max(0,ref.p.gel.lastMuscleReactionImpulse),
                        false,false);
                    head.observe(ref.p.skin,box(ref.p.world),measured);
                }
            }
        }finally{
            if(coordinator!=null)coordinator.abort();
            if(head!=null)head.end();
            if(authority!=null)authority.close();
        }
        System.out.println("V335 CASE id="+id+" mode="+mode+
            " physicalOutputBitwiseEqual=true");
    }
    private static UnknownWorld.Entity box(
                             List<UnknownWorld.Entity> world){
        for(UnknownWorld.Entity e:world)if(e.isBilateralBox())return e;
        return null;
    }
    public static void main(String[] args){
        Zero3ContextAdhesionV319 pretrained=
            Zero3FiveArmAblationV333Regression.gripPretraining();
        for(int id:CASES){
            runCase(id,false,pretrained);
            runCase(id,true,pretrained);
        }
        System.out.println("V335 FINAL matchedCases=12 "+
            "actorAndZeroMuscles=true unchangedOriginalPhysics=true "+
            "V333_BLIND_NOT_RETUNED G3_BLOCKED G5_BLOCKED NO_OTA");
    }
}
