package com.overmind.zero;

import java.io.IOException;
import java.util.ArrayList;

/** Full game birth. No near-body laboratory obstacle/feeding fixture. */
public final class Zero3WorldBirthV354 {
    private Zero3WorldBirthV354(){}
    public static Zero3RuntimeV325 create(long episode,long seed)throws IOException{
        MotorBrain.Body body=new MotorBrain.Body();Morphology.Surface skin=new Morphology.Surface(body);SymbioteMatterV1 gel=new SymbioteMatterV1();gel.reset(body,skin);
        float[] zero=new float[64];for(int i=0;i<96;i++){gel.step(body,skin,zero,.025f);if(gel.invalid!=0)throw new IOException("V354 invalid newborn material");}
        ScarcityLab room=new ScarcityLab(body.cx+145,body.cy);ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();objects.add(room.food);
        return Zero3RuntimeV325.newborn(episode,seed,body,skin,gel,objects,room,100,0);
    }
}
