package com.overmind.zero;
import java.util.ArrayList;

/** Physical visual photoreceptors rather than an eye inside an imaginary core.
 * Controlled fixtures, NOT movement commands or pretrained object semantics.
 */
public final class MaterialVisionRegression {
    private static void check(boolean ok,String message){
        if(!ok)throw new AssertionError(message);
    }
    public static void main(String[] args){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        ArrayList<UnknownWorld.Entity> objects=new ArrayList<>();
        UnknownWorld.Entity source=new UnknownWorld.Entity(142,0,0,0);
        objects.add(source);
        float[] visible=SensorimotorMind.Senses.read(b,s,null,objects,50);
        float light=0;
        for(int i=14;i<18;i++)light+=visible[i];
        check(light>.05f,"Unoccluded real sensor failed to see stimulus");
        UnknownWorld.Entity blocker=new UnknownWorld.Entity(65,0,2,5);
        objects.add(blocker);
        float[] hidden=SensorimotorMind.Senses.read(b,s,null,objects,50);
        float occluded=0;for(int i=14;i<18;i++)occluded+=hidden[i];
        check(occluded<.01f,"Visual wavelengths leaked through opaque box");
        objects.remove(blocker);
        float[] unblocked=SensorimotorMind.Senses.read(b,s,null,objects,50);
        float unblockedLight=0;for(int i=14;i<18;i++)unblockedLight+=unblocked[i];
        check(unblockedLight>occluded+.05f,
            "Removing obstruction failed to uncover photoreceptors");

        // The same physical obstacle is at a fixed coordinate in the world.
        // A genuine protrusion puts an eye closer: measured range must change,
        // while the logical reference does not exert any force.
        objects.clear();
        blocker.x=158;objects.add(blocker);
        SpatialPerception near=new SpatialPerception();
        near.observeEnvironment(b,s,null,objects);
        float original=near.ranges()[0];
        float prevX=b.cx,prevY=b.cy;
        s.x[0]=b.cx+95f;s.y[0]=b.cy;
        SpatialPerception extended=new SpatialPerception();
        extended.observeEnvironment(b,s,null,objects);
        float closer=extended.ranges()[0];
        check(closer<original&&Math.abs(b.cx-prevX)<.0001f&&
              Math.abs(b.cy-prevY)<.0001f,
            "Ray origin did not follow living tissue, or optical sampling moved center");
        check(extended.sweeps==1&&extended.mappedCells()>0,
            "Physical ray memory did not retain observed free/solid cells");
        float[] bodySignals=BodyProprioception.sense(b,s);
        check(bodySignals[32]>0&&bodySignals[40]>=0,
              "Protrusion was invisible to its own body map");
        System.out.println("ZERO 0120 optical: actual membrane photoreceptors, "+
            "opaque box occlusion, physical eye motion, 16-ray mapping and "+
            "body-aware morphology PERCEPTION PASS");
    }
}
