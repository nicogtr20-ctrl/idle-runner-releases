package com.overmind.zero;

import java.util.*;
import java.util.Locale;

/**
 * V3.31 physical matched counterfactual. Three independently recreated gel
 * worlds start bit-for-bit equal, share a REAL V317 actor-selected intention
 * and have the same frozen V319 adhesive head. Only ONE non-grip motor output
 * is changed by +.20, zero or -.20 for 12 real solver frames. The subsequent
 * 24 frames use the ordinary sensory motor feedback with NO intervention.
 *
 * This is research measurement, never a live actor's clairvoyant reward.
 */
public final class Zero3LocalCausalFieldV331 {
    public static final float DT=.025f,AMPLITUDE=.20f;
    static final int PULSE=12,RELAX=24,TOTAL=PULSE+RELAX;
    private Zero3LocalCausalFieldV331(){}
    static void require(boolean valid,String reason){
        if(!valid)throw new AssertionError("ZERO3 V331 "+reason);
    }
    static final class World{
        final Zero3MotorImpulseResponseRegression.Scenario source;
        final Zero3OrganismStoreV322.Physical phys;
        final Zero3LearningFoundation.AdaptiveCortex cortex=
            new Zero3LearningFoundation.AdaptiveCortex(0f);
        final Zero3ContextAdhesionV319 adhesion=
            new Zero3ContextAdhesionV319(0x33119L,false);
        float earlyX,earlyY,finalX,finalY;
        double work,impulse;
        int contactFrames,gripFrames,physicalFrames,appliedFrames;
        final float startX,startY;
        World(int family,int poseIndex){
            // Fresh poses/seeds, not the same 20 scenarios consumed by V330.
            float theta=(float)(.19+poseIndex*.313+family*.027);
            int condition=(poseIndex+family)%4;
            source=new Zero3MotorImpulseResponseRegression.Scenario(
                (poseIndex+family+1)%4,theta,condition);
            float[] noMotor=new float[64];
            for(int i=0;i<96;i++){
                source.gel.step(source.body,source.skin,noMotor,DT);
                require(source.gel.invalid==0&&
                        MaterialGeometry.selfCrossings(source.skin)==0,
                        "bad real physical warmup");
            }
            ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
            if(family>0){
                int law=family==2?2:5;
                float distance=family==3?300f:45.6f+(poseIndex%4)*.71f;
                float yaw=(poseIndex%3-1)*.083f;
                objects.add(new UnknownWorld.Entity(
                    source.body.cx+distance*(float)Math.cos(yaw),
                    source.body.cy+distance*(float)Math.sin(yaw),
                    2,law));
            }
            phys=new Zero3OrganismStoreV322.Physical(
                source.body,source.skin,source.gel,objects);
            startX=phys.body.cx;startY=phys.body.cy;
        }
        Zero3SensoryFrameV315 pre(long episode){
            return Zero3SensoryFrameV315.capture(
                episode,0,phys.gel.ticks*(double)DT,true,
                phys.body,phys.skin,phys.gel,phys.world,null);
        }
        void execute(Zero3LearningFoundation.MotorIntent cortexIntent,
                     Zero3MotorIntentV316 gripIntent,
                     int sector,int channel,int polarity) {
            require(channel==0||channel==1||channel==3,"grip channel excluded");
            cortex.begin(false,cortexIntent);adhesion.begin();
            final int target=sector*4+channel;
            for(int frame=0;frame<TOTAL;frame++){
                float[] action=cortex.command(cortexIntent,phys.body,phys.skin,DT);
                adhesion.actuate(action,gripIntent,phys.body,phys.skin,phys.world);
                if(frame<PULSE&&polarity!=0){
                    float before=action[target];
                    action[target]=Math.max(-1f,
                        Math.min(1f,before+polarity*AMPLITUDE));
                    if(Float.floatToRawIntBits(action[target])!=
                       Float.floatToRawIntBits(before))appliedFrames++;
                }
                float[] oldSkinX=phys.skin.x.clone(),oldSkinY=phys.skin.y.clone(),
                        oldBodyX=phys.body.x.clone(),oldBodyY=phys.body.y.clone();
                float cx=phys.body.cx,cy=phys.body.cy,beforeWork=phys.gel.work;
                phys.gel.step(phys.body,phys.skin,action,DT);
                Zero3MultiSceneRestartV322BRegression.actualWorldStep(phys,
                    oldSkinX,oldSkinY,oldBodyX,oldBodyY,cx,cy);
                float actualWork=Math.max(0f,phys.gel.work-beforeWork);
                adhesion.observe(phys.skin,firstBox(),actualWork);
                work+=actualWork;
                impulse+=Math.max(0f,phys.gel.lastMuscleReactionImpulse);
                physicalFrames++;
                boolean contact=false,held=false;
                for(int patch=0;patch<Morphology.POINTS;patch++){
                    if(phys.skin.touchSense[patch]>.01f||
                       phys.skin.touchPulse[patch]>.001f)contact=true;
                    if(phys.skin.gripHold[patch]>=Morphology.GRIP_HOLD) {
                        boolean owner=false;
                        for(UnknownWorld.Entity obj:phys.world)if(obj.isBox()&&
                           obj.boxGripHold[patch]>=Morphology.GRIP_HOLD)owner=true;
                        require(owner,"material grip without real BOX owner");
                        held=true;
                    }
                }
                if(contact)contactFrames++;
                if(held)gripFrames++;
                require(phys.gel.invalid==0&&
                        Float.isFinite(phys.body.cx)&&Float.isFinite(phys.body.cy),
                        "nonfinite true gel movement");
                if(frame==PULSE-1){earlyX=phys.body.cx;earlyY=phys.body.cy;}
            }
            finalX=phys.body.cx;finalY=phys.body.cy;
            adhesion.end();cortex.discard();
        }
        UnknownWorld.Entity firstBox(){
            for(UnknownWorld.Entity e:phys.world)if(e.isBox())return e;
            return null;
        }
    }
    public static final class Delta{
        public final int family,pose,sector,channel;
        public final boolean movementIntent;
        public final double earlyX,earlyY,finalX,finalY,
            earlyAlong,finalAlong,lateAlong,extraWork,curvature;
        public final int contactFrames,gripFrames;
        public final double controlWork;
        Delta(int family,int pose,int sector,int channel,
              World baseline,World plus,World minus,float intentX,float intentY){
            this.family=family;this.pose=pose;this.sector=sector;this.channel=channel;
            movementIntent=intentX!=0||intentY!=0;
            earlyX=plus.earlyX-minus.earlyX;earlyY=plus.earlyY-minus.earlyY;
            finalX=plus.finalX-minus.finalX;finalY=plus.finalY-minus.finalY;
            earlyAlong=earlyX*intentX+earlyY*intentY;
            finalAlong=finalX*intentX+finalY*intentY;
            lateAlong=finalAlong-earlyAlong;
            extraWork=plus.work-minus.work;
            curvature=Math.hypot(plus.finalX+minus.finalX-2*baseline.finalX,
                                 plus.finalY+minus.finalY-2*baseline.finalY);
            contactFrames=baseline.contactFrames;gripFrames=baseline.gripFrames;
            controlWork=baseline.work;
            for(double value:new double[]{earlyX,earlyY,finalX,finalY,
                 earlyAlong,finalAlong,lateAlong,extraWork,curvature,controlWork})
                require(Double.isFinite(value),"nonfinite measured differential");
        }
        public double netDifference(){return Math.hypot(finalX,finalY);}
    }
    public static Delta compare(int family,int pose,int sector,int channel){
        require(family>=0&&family<4&&pose>=0&&pose<6&&
                sector>=0&&sector<16&&
                (channel==0||channel==1||channel==3),
                "invalid preregistered intervention");
        World base=new World(family,pose),plus=new World(family,pose),
              minus=new World(family,pose);
        require(Float.floatToRawIntBits(base.startX)==
                Float.floatToRawIntBits(plus.startX)&&
                Float.floatToRawIntBits(base.startY)==
                Float.floatToRawIntBits(minus.startY)&&
                base.phys.gel.ticks==plus.phys.gel.ticks&&
                plus.phys.gel.ticks==minus.phys.gel.ticks,
                "paired worlds were not born identically");
        long episode=3310000L+family*100+pose;
        Zero3SensoryFrameV315 pre=base.pre(episode);
        require(pre.intact(),"actor PRE frame not authentic");
        Zero3AutonomousLoopV317.Encoder actor=
            new Zero3AutonomousLoopV317.Encoder(0x331012L+family*31+pose);
        actor.resetEpisode();actor.before(pre);
        float[] intent=actor.choose(); // ONE real actor, no scripted direction
        Zero3LearningFoundation.MotorIntent motor=
            new Zero3LearningFoundation.MotorIntent(episode,0,0,
                intent[0],intent[1],intent[2],intent[3],TOTAL*DT);
        try(Zero3MotorIntentV316.Authority authority=
            new Zero3MotorIntentV316.Authority(episode,
                                               Zero3AutonomousLoopV317.OWNER)){
            Zero3MotorIntentV316 brainIntent=authority.issue(
                Zero3AutonomousLoopV317.OWNER,0,0,331,
                intent[0],intent[1],intent[2],intent[3],TOTAL*DT,.35f);
            base.execute(motor,brainIntent,sector,channel,0);
            plus.execute(motor,brainIntent,sector,channel,+1);
            minus.execute(motor,brainIntent,sector,channel,-1);
        }
        require(base.physicalFrames==TOTAL&&plus.physicalFrames==TOTAL&&
                minus.physicalFrames==TOTAL&&plus.appliedFrames==PULSE&&
                minus.appliedFrames==PULSE,
                "local intervention was not actually executed by material solver");
        return new Delta(family,pose,sector,channel,
                         base,plus,minus,intent[0],intent[1]);
    }
    /** A duplicate physical CONTROL (no intervention) must be bit deterministic. */
    static void pairedControlDeterminism(int family,int pose){
        World first=new World(family,pose),second=new World(family,pose);
        long episode=3320000L+family*100+pose;
        Zero3AutonomousLoopV317.Encoder actor=
            new Zero3AutonomousLoopV317.Encoder(0x331012L+family*31+pose);
        actor.resetEpisode();actor.before(first.pre(episode));
        float[] a=actor.choose();
        Zero3LearningFoundation.MotorIntent motor=
            new Zero3LearningFoundation.MotorIntent(episode,0,0,
                a[0],a[1],a[2],a[3],TOTAL*DT);
        try(Zero3MotorIntentV316.Authority authority=
            new Zero3MotorIntentV316.Authority(episode,
                                               Zero3AutonomousLoopV317.OWNER)){
            Zero3MotorIntentV316 intent=authority.issue(
                Zero3AutonomousLoopV317.OWNER,0,0,331,
                a[0],a[1],a[2],a[3],TOTAL*DT,.35f);
            first.execute(motor,intent,0,0,0);
            second.execute(motor,intent,0,0,0);
        }
        require(Float.floatToRawIntBits(first.finalX)==
                Float.floatToRawIntBits(second.finalX)&&
                Float.floatToRawIntBits(first.finalY)==
                Float.floatToRawIntBits(second.finalY)&&
                Double.doubleToRawLongBits(first.work)==
                Double.doubleToRawLongBits(second.work)&&
                first.contactFrames==second.contactFrames,
                "same-motor-control physical solver is nondeterministic");
    }
}
