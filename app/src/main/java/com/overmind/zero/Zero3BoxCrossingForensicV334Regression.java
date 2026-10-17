package com.overmind.zero;

import java.util.*;
import java.util.Locale;

/**
 * V3.34 FORENSIC ONLY. Recreates V333's exact two censored BOX births, four
 * nearby same-family controls and separates scenario/preconditioning, all
 * 96 zero-effort warmup frames, box spawn, post-gel and post-world stages.
 * Does not repair, train, rebalance or alter any physical algorithm.
 */
public final class Zero3BoxCrossingForensicV334Regression {
    private static final int[] CASES={333,337,341,641,645,649};
    private static final float DT=.025f;
    private static final int WARMUP=96;
    private Zero3BoxCrossingForensicV334Regression(){}

    static void check(boolean b,String message){
        if(!b)throw new AssertionError("V334 "+message);
    }
    static int[] firstPair(Morphology.Surface s){
        int n=Morphology.POINTS;
        for(int i=0;i<n;i++){
            int in=(i+1)%n;
            for(int j=i+2;j<n;j++){
                int jn=(j+1)%n;
                if(jn==i)continue;
                float a=cross(s.x[i],s.y[i],s.x[in],s.y[in],s.x[j],s.y[j]);
                float b=cross(s.x[i],s.y[i],s.x[in],s.y[in],s.x[jn],s.y[jn]);
                float c=cross(s.x[j],s.y[j],s.x[jn],s.y[jn],s.x[i],s.y[i]);
                float d=cross(s.x[j],s.y[j],s.x[jn],s.y[jn],s.x[in],s.y[in]);
                if(((a>1e-4f&&b< -1e-4f)||(a< -1e-4f&&b>1e-4f))&&
                   ((c>1e-4f&&d< -1e-4f)||(c< -1e-4f&&d>1e-4f)))
                    return new int[]{i,j};
            }
        }
        return new int[]{-1,-1};
    }
    static float cross(float ax,float ay,float bx,float by,float cx,float cy){
        return (bx-ax)*(cy-ay)-(by-ay)*(cx-ax);
    }
    static double area(Morphology.Surface s){
        double sum=0;
        for(int i=0;i<Morphology.POINTS;i++){
            int j=(i+1)%Morphology.POINTS;
            sum+=(double)s.x[i]*s.y[j]-(double)s.x[j]*s.y[i];
        }
        return sum*.5;
    }
    static final class State{
        final int ordinal;
        final boolean blind;
        final Zero3MotorImpulseResponseRegression.Scenario scene;
        final Zero3OrganismStoreV322.Physical p;
        int firstWarmupCross=-1,anyWarmupCross;
        long lastRepair,lastSelfContact;
        State(int ordinal,boolean blind){
            this.ordinal=ordinal;this.blind=blind;
            float pose=blind?(float)(-.51+ordinal*.143):
                                  (float)(.23+ordinal*.103);
            int condition=(ordinal+3)%4;
            scene=new Zero3MotorImpulseResponseRegression.Scenario(
                (ordinal+3)%4,pose,condition);
            p=new Zero3OrganismStoreV322.Physical(
                scene.body,scene.skin,scene.gel,new ArrayList<UnknownWorld.Entity>());
            lastRepair=scene.gel.topologyRepairs;
            lastSelfContact=scene.gel.selfContactPairs;
        }
        void warmup(){
            for(int n=0;n<WARMUP;n++){
                p.gel.step(p.body,p.skin,new float[64],DT);
                check(p.gel.invalid==0,"invalid zero motor warmup");
                int crosses=MaterialGeometry.selfCrossings(p.skin);
                if(crosses>0){
                    if(firstWarmupCross<0)firstWarmupCross=n;
                    anyWarmupCross++;
                    System.out.println("V334 WARMUP_CROSS ordinal="+ordinal+
                        " step="+n+" crossings="+crosses+
                        " firstEdges="+Arrays.toString(firstPair(p.skin))+
                        " topologyRepairs="+p.gel.topologyRepairs);
                }
            }
        }
        void spawn(){
            int family=ordinal%4,rep=ordinal/4;
            if(family!=0){
                int law=family==2?2:5;
                float distance=family==3?280f:(float)(46.4+(rep%7)*.51);
                float yaw=(float)((rep%7-3)*.092);
                p.world.add(new UnknownWorld.Entity(
                    p.body.cx+distance*(float)Math.cos(yaw),
                    p.body.cy+distance*(float)Math.sin(yaw),2,law));
            }
        }
        float closestBoxGap(){
            for(UnknownWorld.Entity e:p.world)if(e.isBox())
                return (float)Math.sqrt(MaterialGeometry.surfaceDistanceSquared(
                    p.skin,e.x,e.y))-e.radius;
            return Float.NaN;
        }
        void stage(String label,int frame){
            int crosses=MaterialGeometry.selfCrossings(p.skin);
            int[] pair=firstPair(p.skin);
            check((crosses==0)==(pair[0]<0),"self-crossing and edge detector mismatch");
            System.out.println(String.format(Locale.US,
                "V334 STAGE ordinal=%d scene=%s label=%s sub=%d"+
                " crossing=%d pair=%d,%d solverInvalid=%d"+
                " repair=%d selfContact=%d ticks=%d"+
                " shellTime=%.6f signedArea=%.6f"+
                " boxShellGap=%.6f",
                ordinal,blind?"BLIND":"TRAIN",label,frame,crosses,
                pair[0],pair[1],p.gel.invalid,p.gel.topologyRepairs,
                p.gel.selfContactPairs,p.gel.ticks,p.skin.t,area(p.skin),
                closestBoxGap()));
        }
    }
    private static float[] command(State state,Zero3ContextAdhesionV319 grip,
                                    int ordinal,int iteration,
                                    Zero3LearningFoundation.Coordinator controller,
                                    Zero3MotorIntentV316 intent) {
        float[] a=controller.actuate(state.p.body,state.p.skin,DT);
        grip.actuate(a,intent,state.p.body,state.p.skin,state.p.world);
        return a.clone();
    }
    private static void integrate(State state,float[] motor,boolean world,
                                  int frame,String label){
        float[] sx=state.p.skin.x.clone(),sy=state.p.skin.y.clone();
        float[] bx=state.p.body.x.clone(),by=state.p.body.y.clone();
        float cx=state.p.body.cx,cy=state.p.body.cy;
        state.p.gel.step(state.p.body,state.p.skin,motor,DT);
        state.stage(label+"_AFTER_GEL",frame);
        if(world)Zero3MultiSceneRestartV322BRegression.actualWorldStep(
            state.p,sx,sy,bx,by,cx,cy);
        state.stage(label+"_AFTER_WORLD",frame);
    }
    private static String signature(State state){
        StringBuilder out=new StringBuilder();
        for(int i=0;i<Morphology.POINTS;i++){
            out.append(Float.floatToRawIntBits(state.p.skin.x[i])).append(':');
            out.append(Float.floatToRawIntBits(state.p.skin.y[i])).append(';');
        }
        out.append(state.p.gel.invalid).append(':')
           .append(state.p.gel.ticks).append(':')
           .append(state.p.gel.topologyRepairs);
        return out.toString();
    }
    static void audit(int ordinal,Zero3ContextAdhesionV319 trained){
        boolean blind=ordinal>=600;
        // A is EXACT V333 BLIND FROZEN cortical path. B,C,D reconstructed
        // at the identical birth, not a clone after a collision has occurred.
        State a=new State(ordinal,blind),b=new State(ordinal,blind),
              c=new State(ordinal,blind),d=new State(ordinal,blind),
              verify=new State(ordinal,blind);
        check(MaterialGeometry.selfCrossings(a.p.skin)==
              MaterialGeometry.selfCrossings(verify.p.skin),
              "initial membrane unexpectedly nondeterministic");
        a.stage("AFTER_SCENARIO", -1);
        State[] arms={a,b,c,d,verify};
        for(State s:arms)s.warmup();
        a.stage("AFTER_WARMUP", -1);
        check(signature(a).equals(signature(verify)),
              "warmup history / morphology nondeterministic");
        for(State s:arms)s.spawn();
        a.stage("BOX_SPAWN", -1);
        a.stage("BEFORE_MOTOR", -1);
        int initialCross=MaterialGeometry.selfCrossings(a.p.skin);
        for(State s:arms){
            check(MaterialGeometry.selfCrossings(s.p.skin)==initialCross,
                  "a supposedly matched pre-action body differed");
        }
        float[] zero=new float[64];
        long ep=(blind?3337000L:3331000L)+ordinal;
        Zero3AutonomousLoopV317.Encoder actor=
            new Zero3AutonomousLoopV317.Encoder(0x31720261017L);
        actor.resetEpisode();
        Zero3SensoryFrameV315 pre=Zero3SensoryFrameV315.capture(
            ep,0,a.p.gel.ticks*(double)DT,true,
            a.p.body,a.p.skin,a.p.gel,a.p.world,null);
        check(pre.intact(),"authentic V333 PRE does not hold");
        actor.before(pre);
        float[] choice=actor.choose();
        Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex(0f);
        Zero3LearningFoundation.Coordinator controller=
            new Zero3LearningFoundation.Coordinator(cortex);
        Zero3LearningFoundation.MotorIntent motorIntent=
            new Zero3LearningFoundation.MotorIntent(
                ep,0,cortex.revision(),choice[0],choice[1],
                choice[2],choice[3],12*DT);
        controller.begin(motorIntent,a.p.body,false,true,false);
        Zero3ContextAdhesionV319 head=trained.frozenCopy();
        head.begin();
        try(Zero3MotorIntentV316.Authority authority=
            new Zero3MotorIntentV316.Authority(
                ep,Zero3AutonomousLoopV317.OWNER)){
            Zero3MotorIntentV316 intent=authority.issue(
                Zero3AutonomousLoopV317.OWNER,0,0,333,
                choice[0],choice[1],choice[2],choice[3],12*DT,.35f);
            for(int sub=0;sub<2;sub++){
                float[] actual=command(a,head,ordinal,sub,controller,intent);
                float workBefore=a.p.gel.work;
                // Baseline: same actor, body, brain and true BOX collision.
                integrate(a,actual,true,sub,"REAL_ACTOR_BOX");
                float trueWork=Math.max(0,a.p.gel.work-workBefore);
                controller.observe(a.p.body,DT,trueWork,
                    Math.max(0,a.p.gel.lastMuscleReactionImpulse),false,false);
                head.observe(a.p.skin,firstBox(a.p.world),trueWork);
                // All control arms use IDENTICAL ACTUAL 64 commands from A,
                // not newly synthesized chosen directions.
                integrate(b,actual,false,sub,"SAME_MOTORS_NO_WORLD_COLLISION");
                integrate(c,zero,true,sub,"ZERO_MOTOR_REAL_BOX");
                integrate(d,actual,true,sub,"SAME_MOTOR_BOX_CONTROL");
                // D is a duplicate of A's physics trajectory on purpose
                // except it does not carry controller side effects.
                check(signature(a).equals(signature(d)),
                      "same issued muscles and true world step nondeterministic");
                int ac=MaterialGeometry.selfCrossings(a.p.skin),
                    bc=MaterialGeometry.selfCrossings(b.p.skin),
                    cc=MaterialGeometry.selfCrossings(c.p.skin);
                System.out.println("V334 COMPARISON ordinal="+ordinal+
                    " step="+sub+" preCross="+initialCross+
                    " trueActorBox="+ac+" sameMuscleNoBoxCollision="+bc+
                    " zeroMuscleBox="+cc+" deterministicDuplicate=true");
            }
        }
        controller.abort();head.end();
        System.out.println("V334 CASE ordinal="+ordinal+
            " status=MEASURED firstWarmupCross="+a.firstWarmupCross+
            " warmupCrossCount="+a.anyWarmupCross+
            " beforeMotorCross="+initialCross+
            " firstMotorSubsteps=2");
    }
    static UnknownWorld.Entity firstBox(List<UnknownWorld.Entity> w){
        for(UnknownWorld.Entity e:w)if(e.isBox())return e;
        return null;
    }
    public static void main(String[] args){
        Zero3ContextAdhesionV319 trained=
            Zero3FiveArmAblationV333Regression.gripPretraining();
        for(int ordinal:CASES)audit(ordinal,trained);
        System.out.println("V334 FINAL measuredCases="+CASES.length+
            " suspectCensorsAudited=337,645"+
            " realGelAndWorldUnmodified=true"+
            " diagnosticOnly=true G3_BLOCKED G5_BLOCKED NO_OTA");
    }
}
