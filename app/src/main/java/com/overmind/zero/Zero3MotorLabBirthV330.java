package com.overmind.zero;

import java.io.IOException;
import java.util.ArrayList;

/**
 * G3 matched diagnostic environment only. Unlike the Android V326 birth,
 * varies initial MATERIAL orientation and object families. This fixture
 * never supplies a desired heading, reward, center impulse or gait.
 */
public final class Zero3MotorLabBirthV330 {
    private Zero3MotorLabBirthV330(){}
    public static Zero3RuntimeV325 newborn(long episode,long seed,int family,
            int ordinal,boolean plastic)throws IOException{
        if(family<0||family>3||ordinal<0||ordinal>=100||episode<=0||seed==0)
            throw new IOException("V330 invalid scenario");
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 matter=new SymbioteMatterV1();
        matter.reset(b,s);
        float rotation=(ordinal%5-2)*.37f+(family==3?.12f:0f);
        if(rotation!=0f){
            float cs=(float)Math.cos(rotation),sn=(float)Math.sin(rotation);
            Zero3MotorRetentionRegression.rotate(s.x,s.y,b.cx,b.cy,cs,sn);
            Zero3MotorRetentionRegression.rotate(b.x,b.y,b.cx,b.cy,cs,sn);
            Zero3MotorRetentionRegression.rotate(matter.x,matter.y,b.cx,b.cy,cs,sn);
            matter.observeBody(b,s);
        }
        float[] zero=new float[64];
        for(int i=0;i<96;i++){
            matter.step(b,s,zero,Zero3RuntimeV325.DT);
            if(matter.invalid!=0||MaterialGeometry.selfCrossings(s)!=0)
                throw new IOException("V330 physical newborn invalid");
        }
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        if(family!=0){
            int law=family==2?2:5;
            float dist=family==3?220f:47f;
            float sideways=(ordinal%5-2)*5f;
            objects.add(new UnknownWorld.Entity(b.cx+dist,b.cy+sideways,2,law));
        }
        ScarcityLab room=new ScarcityLab(b.cx+390f,b.cy+22f);
        objects.add(room.food);
        return Zero3RuntimeV325.newbornMotorBench(episode,seed,b,s,matter,
            objects,room,100f,plastic?.013f:0f);
    }
}
