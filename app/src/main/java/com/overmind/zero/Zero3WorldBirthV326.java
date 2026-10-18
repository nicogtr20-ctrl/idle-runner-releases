package com.overmind.zero;

import java.io.IOException;
import java.util.ArrayList;

/**
 * First explicit ZERO 3.0 experiment birthplace, not a regression Scenario.
 * A new organism uses the actual 96-site/74-inner material solver. No
 * trajectories, virtual rewards, heading/target pulls or hidden gait.
 * Later G5 must own world generation and evolving ecology; do NOT equate this
 * limited sandbox with the full legacy GameActivity habitat.
 */
public final class Zero3WorldBirthV326 {
    private Zero3WorldBirthV326(){}
    public static Zero3RuntimeV325 create(long episode,long worldSeed)
            throws IOException {
        if(episode<=0||worldSeed==0)throw new IOException("invalid V3 birth identity");
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 material=new SymbioteMatterV1();
        material.reset(b,s);
        float[] rest=new float[64];
        // Physically settle the newborn fluid without any externally imposed gait.
        for(int tick=0;tick<96;tick++) {
            material.step(b,s,rest,.025f);
            if(material.invalid!=0)throw new IOException("newborn gel invalid");
        }
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        // Contact obstacles are genuine physical entities; no goal coordinates
        // ever reach the decision actor.
        world.add(new UnknownWorld.Entity(b.cx+46f,b.cy+4f,2,5)); // box
        world.add(new UnknownWorld.Entity(b.cx-57f,b.cy-12f,2,2)); // disk
        ScarcityLab room=new ScarcityLab(b.cx+145f,b.cy);
        world.add(room.food);
        return Zero3RuntimeV325.newborn(episode,worldSeed,b,s,material,
                                        world,room,100f,0f);
    }
}
