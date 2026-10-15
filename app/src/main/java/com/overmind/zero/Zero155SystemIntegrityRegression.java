package com.overmind.zero;
import java.util.ArrayList;
import java.util.Arrays;

/** Acceptance evidence for corrected living motor and sensor pathways. */
public final class Zero155SystemIntegrityRegression {
    private static void ok(boolean success,String why){
        if(!success)throw new AssertionError(why);
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 matter=new SymbioteMatterV1();matter.reset(body,skin);
        float[] idle=new float[SymbioteMatterV1.SECTORS*SymbioteMatterV1.OUTPUTS];
        for(int k=0;k<100;k++)matter.step(body,skin,idle,.02f);
        float workBefore=matter.work,legacyBefore=body.totalWork;
        float[] active=idle.clone();active[0]=.7f;active[1]=.3f;
        matter.step(body,skin,active,.02f);
        float freshWork=matter.work-workBefore;
        float freshTotal=body.totalWork-legacyBefore;
        ok(freshWork>0&&Math.abs(freshWork-freshTotal)<1e-7f,
           "Old-body effort still double-integrates cumulative work");

        MotorBrain.Body newborn=new MotorBrain.Body();
        Morphology.Surface tissue=new Morphology.Surface(newborn);
        float[] previous=BodyProprioception.sense(newborn,tissue);
        Arrays.fill(tissue.extension,105f);
        float[] after=BodyProprioception.sense(newborn,tissue);
        ok(after[2*BodyProprioception.SECTORS]>
           previous[2*BodyProprioception.SECTORS]+.5f,
           "Intended efference was silently replaced, invalidating trained weights");
        ok(Math.abs(after[4*BodyProprioception.SECTORS]
                   -previous[4*BodyProprioception.SECTORS])<1e-6f,
           "Unexecuted extension fabricated an achieved radius");

        tissue.x[0]=145f;tissue.y[0]=0f;
        UnknownWorld.Entity resource=new UnknownWorld.Entity(152,0,0,0);
        ArrayList<UnknownWorld.Entity> empty=new ArrayList<>();
        ok(WorldPlacement.free(resource,newborn.cx,newborn.cy,null,empty,empty),
           "Centroid-only test fixture not reproduced");
        ok(!WorldPlacement.free(resource,newborn.cx,newborn.cy,null,empty,empty,tissue),
           "New resource spawned touching extended live tissue");

        SensorimotorMind agent=new SensorimotorMind();
        RecurrentCoreV22 core=new RecurrentCoreV22();
        SymbioteMotorHeadV1 motor=new SymbioteMotorHeadV1();
        agent.attachActiveCore(core);
        agent.attachSymbioteMotorHead(motor);
        agent.attachDistributedBody(new DistributedBodySense());
        agent.begin(new float[SensorimotorMind.S]);
        agent.resumeEpisode(new float[SensorimotorMind.S],8);
        agent.updateDistributedSense(newborn,tissue,null,empty);
        int beforeDecisions=agent.decisionCount();
        agent.rearmAfterResume(newborn,tissue);
        ok(agent.decisionCount()==beforeDecisions+1 &&
           motor.summary().contains("decisions=1"),
           "Living brain did not command muscles before first resumed tick");
        long beforeTransitions=core.transitions();
        agent.rearmAfterResume(newborn,tissue);
        ok(agent.decisionCount()==beforeDecisions+1 &&
           core.transitions()==beforeTransitions,
           "Rearming fabricated training on a censored pre-restart interval");
        ok(GripContactObserver.PREVIOUS_SESSION.equals("previous_session_report"),
           "Separate passive diagnostic archive changed key/schema");
        System.out.println("PASS OTA155: active living rearm, demand vs shape, tissue-safe spawn, incremental effort");
    }
}
