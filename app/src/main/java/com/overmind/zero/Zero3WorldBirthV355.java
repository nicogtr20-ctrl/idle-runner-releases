package com.overmind.zero;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Phone-facing birth: the V354 cognitive organism in the SAME initial
 * populated habitat that the original game's renderer expects.
 * Does not touch the 2.2 saves or create a second motor controller.
 */
final class Zero3WorldBirthV355 {
    private Zero3WorldBirthV355(){}
    static Zero3RuntimeV325 create(long episode,long seed)throws IOException{
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(body,skin);
        float[] still=new float[64];
        for(int i=0;i<96;i++){
            gel.step(body,skin,still,Zero3RuntimeV325.DT);
            if(gel.invalid!=0)throw new IOException("ZERO3 gel birth invalid");
        }
        ScarcityLab room=new ScarcityLab(body.cx+145f,body.cy);
        ArrayList<UnknownWorld.Entity> environment=new ArrayList<>();
        environment.add(room.food);
        environment.addAll(WorldPlacement.spawn(
            (int)((seed^(seed>>>32))&0x7fffffff),body.cx,body.cy,
            false,room,environment,skin));
        HabitatContinuity.excludeRoomOverlap(environment,room,body,skin);
        return Zero3RuntimeV325.newborn(episode,seed,body,skin,gel,
            environment,room,100f,0f);
    }
}
