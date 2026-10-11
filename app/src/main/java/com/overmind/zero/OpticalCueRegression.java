package com.overmind.zero;
import java.util.ArrayList;

/** Experiment 0116.5: prove rays behave differently through glass/rim,
 * while the same opaque physical collisions and learned brain remain. */
public final class OpticalCueRegression {
    private static void ok(boolean value,String reason){
        if(!value)throw new AssertionError(reason);
    }
    private static float total(float[] readings,int start,int len){
        float sum=0;
        for(int i=start;i<start+len;i++)sum+=readings[i];
        return sum;
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        ScarcityLab room=new ScarcityLab(0,0);
        ArrayList<UnknownWorld.Entity> chamberWorld=new ArrayList<>();
        chamberWorld.add(room.food);
        // Sightline first passes above the locked gate, then through the
        // glass insert in the upper front wall. The source stays inside.
        body.cx=65;body.cy=-100;
        float[] dark=SensorimotorMind.Senses.read(body,skin,room,chamberWorld,60,false);
        float[] bright=SensorimotorMind.Senses.read(body,skin,room,chamberWorld,60,true);
        float[] octDark=SensorimotorMind.Senses.spectral8(body,room,chamberWorld,false);
        float[] octBright=SensorimotorMind.Senses.spectral8(body,room,chamberWorld,true);
        ok(total(dark,14,12)<.00001f,
            "Opaque original wall unexpectedly revealed rare energy");
        ok(total(bright,14,12)>.04f,
            "Transparent glass did not transmit old visible wavelength");
        ok(total(octBright,0,24)>total(octDark,0,24)+.04f,
            "Navigation's eight-sector optics cannot see through glass");
        // Glass changes only photons, NOT the obstacle rays or wall solidity.
        ok(SensorimotorMind.Senses.solid(room,room.entranceX(),-65,chamberWorld),
            "Glass accidentally became a physical opening");
        body.cx=90;body.cy=-65;
        float[] obstacles=SensorimotorMind.Senses.read(body,skin,room,chamberWorld,60,true);
        ok(obstacles[6]>.1f,
            "Obstacle ray falsely passed through physically solid glass");
        // Seen head-on, the unretracted gate itself still blocks the light.
        body.cx=45;body.cy=0;
        float[] straight=SensorimotorMind.Senses.read(body,skin,room,chamberWorld,60,true);
        ok(total(straight,14,12)<.001f,
            "Light mysteriously passed through opaque solid gate");
        // Faint new signature from beneath a real, stationary square,
        // only if there is clear air to the near external rim.
        ArrayList<UnknownWorld.Entity> covered=new ArrayList<>();
        UnknownWorld.Entity box=new UnknownWorld.Entity(105,0,2,5);
        UnknownWorld.Entity under=new UnknownWorld.Entity(105,0,0,4);
        under.concealed=true;
        covered.add(box);covered.add(under);
        body.cx=0;body.cy=0;
        float[] blank=SensorimotorMind.Senses.read(body,skin,null,covered,60,false);
        float[] glow=SensorimotorMind.Senses.read(body,skin,null,covered,60,true);
        ok(total(blank,14,4)<.001f,"Covered energy leaked in control world");
        ok(total(glow,14,4)>.01f&&total(glow,14,4)<.21f,
            "Faint glow should be perceivable but smaller than ordinary energy");
        ok(total(glow,22,4)>=total(blank,22,4),
            "The existing rare optical wavelength does not transmit");
        // A second physical square between ZERO and the emitting rim
        // blocks the photons; decorative glowing sprites alone cannot pass.
        covered.add(new UnknownWorld.Entity(48,0,2,5));
        float[] blocked=SensorimotorMind.Senses.read(body,skin,null,covered,60,true);
        ok(total(blocked,14,4)<.001f,
            "Glowing source improperly visible through an opaque obstacle");
        covered.remove(2);
        // The same cover still refuses simple pressure, and the same
        // concealed source cannot be collected before physical uncover.
        ok(under.concealed&&box.displacementFromSpawn()==0f,
            "Optical observation mutated world collision or cover position");
        ok(!SquareGripPuzzle.uncover(box,covered),
            "Seeing photons released the hidden reward without manipulation");
        // No additional sensor, decision or reward channels were introduced.
        ok(glow.length==26&&octBright.length==24,
            "Old brain shape changed and would corrupt a saved 11.6 ZERO");
        System.out.println("OpticalCueRegression PASS: glass transmits light not body, "
            +"square leaks faint real energy while covered, gate occludes, "
            +"opaque obstacle occludes, no unlock, no new sensor channels");
    }
}
