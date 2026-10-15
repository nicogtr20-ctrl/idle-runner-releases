package com.overmind.zero;
import java.util.ArrayList;
import java.util.Arrays;
/** OTA154: true tissue frames, retina parity, and terminal cortex credit. */
public final class Zero154SensorimotorRegression {
    private static void ok(boolean b,String why){if(!b)throw new AssertionError(why);}
    private static float diff(float[] a,float[] b){
        float v=0;for(int k=0;k<a.length;k++)v+=Math.abs(a[k]-b[k]);return v;
    }
    private static void rotate(MotorBrain.Body body,Morphology.Surface skin,SymbioteMatterV1 fluid){
        for(int i=0;i<Morphology.POINTS;i++){
            float x=skin.x[i]-body.cx,y=skin.y[i]-body.cy;
            skin.x[i]=body.cx-y;skin.y[i]=body.cy+x;
        }
        for(int i=0;i<MotorBrain.N;i++){
            float x=body.x[i]-body.cx,y=body.y[i]-body.cy;
            body.x[i]=body.cx-y;body.y[i]=body.cy+x;
            float vx=body.vx[i],vy=body.vy[i];body.vx[i]=-vy;body.vy[i]=vx;
        }
        for(int i=0;i<SymbioteMatterV1.INNER;i++){
            float x=fluid.x[i]-body.cx,y=fluid.y[i]-body.cy;
            fluid.x[i]=body.cx-y;fluid.y[i]=body.cy+x;
            float vx=fluid.vx[i],vy=fluid.vy[i];fluid.vx[i]=-vy;fluid.vy[i]=vx;
        }
        float v=body.cvx;body.cvx=-body.cvy;body.cvy=v;
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        SymbioteMatterV1 liquid=new SymbioteMatterV1();
        liquid.reset(body,skin);
        SensorimotorMind brain=new SensorimotorMind();
        float[] command=new float[SymbioteMatterV1.SECTORS*SymbioteMatterV1.OUTPUTS];
        command[0]=1f;
        brain.appliedSymbioteControls(command,body,skin);
        float[] original=brain.appliedEfferenceForTest();
        float[] parcel0=FluidInteroceptionV1.sense(body,skin,liquid);
        FluidInteroceptionV1 inside=new FluidInteroceptionV1();
        inside.feed(parcel0,body,liquid,skin);
        float[] pooled0=inside.summary();
        rotate(body,skin,liquid);
        brain.appliedSymbioteControls(command,body,skin);
        float[] transformed=brain.appliedEfferenceForTest();
        ok(Math.abs(transformed[1]+original[2])<.0001f&&
           Math.abs(transformed[2]-original[1])<.0001f,
           "Efference direction ignores tissue rotation");
        float[] parcel90=FluidInteroceptionV1.sense(body,skin,liquid);
        inside.feed(parcel90,body,liquid,skin);
        ok(diff(pooled0,inside.summary())<.03f,
           "Interoception lost material-sector invariance under rigid rotation");
        SensorimotorMind oldBrain=new SensorimotorMind();
        oldBrain.appliedSymbioteControls(command);
        ok(Math.abs(oldBrain.appliedEfferenceForTest()[1]-original[1])<.01f,
           "Old-body API unexpectedly broken");
        MotorBrain.Body other=new MotorBrain.Body();
        Morphology.Surface protrusion=new Morphology.Surface(other);
        ArrayList<UnknownWorld.Entity> scene=new ArrayList<>();
        UnknownWorld.Entity light=new UnknownWorld.Entity(235,0,0,0);
        scene.add(light);
        protrusion.x[0]=other.cx+125f;protrusion.y[0]=other.cy;
        float[] main=SensorimotorMind.Senses.read(other,protrusion,null,scene,70f);
        float[] nav=SensorimotorMind.Senses.spectral8(other,protrusion,null,scene);
        ok(main[14]+main[15]+main[16]+main[17]>.02f,
           "Main retina still culls at center range");
        float navLevel=0f;for(int k=0;k<8;k++)navLevel+=nav[k];
        ok(navLevel>.02f,"Navigator retina still culls at center range");
        ok(SensorimotorMind.Senses.spectral8(other,null,scene)[0]==0f,
           "Legacy central retina unexpectedly sees past original range");
        SensorimotorMind terminal=new SensorimotorMind();
        SymbioteMotorHeadV1 motor=new SymbioteMotorHeadV1();
        terminal.attachActiveCore(new RecurrentCoreV22());
        terminal.attachSymbioteMotorHead(motor);
        terminal.attachDistributedBody(new DistributedBodySense());
        terminal.updateDistributedSense(other,protrusion,null,scene);
        terminal.begin(SensorimotorMind.Senses.read(other,protrusion,null,scene,70f));
        terminal.choose(terminal.stateCopy());
        terminal.motorMetabolicCost(.25f);
        terminal.recordTerminal(terminal.stateCopy(),0f,other);
        ok(motor.summary().contains("trained=1"),
           "Terminal consequence lost last living muscle action");
        terminal.recordTerminal(terminal.stateCopy(),0f,other);
        ok(motor.summary().contains("trained=1"),
           "Terminal outcome was credited to cortex twice");
        System.out.println("PASS OTA154: live tissue efference, fluid rotation, retinal ranges, terminal once, no gait/reward");
    }
}
