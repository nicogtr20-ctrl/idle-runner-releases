package com.overmind.zero;
import java.util.*;

/** Full actual solver stress: rigid gate + rigid square, with the nucleus
 * already touching the gate at spawn. Only real physics may change positions.
 * No fake activation of any of ZERO's 96 neural adhesive outputs.
 */
public final class BodyIntegrityObstacleRegression {
    private static void ok(boolean x,String message){
        if(!x)throw new AssertionError(message);
    }
    private static void simulate(int scenario){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        // Consistent coordinate translation; nucleus begins near physical gate.
        body.cx=55;for(int i=0;i<MotorBrain.N;i++)body.x[i]+=55;
        for(int i=0;i<Morphology.POINTS;i++)skin.x[i]+=55;
        ScarcityLab gate=new ScarcityLab(0,0);
        UnknownWorld.Entity box=new UnknownWorld.Entity(5,0,1,5);
        float[] motor=MotorBrain.virgin(new Random(1200+scenario));
        float[] shape=Morphology.virgin(new Random(1400+scenario));
        float[] hand=ContactLearning.virgin();
        float[] oldSX=new float[Morphology.POINTS],oldSY=new float[Morphology.POINTS];
        float[] oldBX=new float[MotorBrain.N],oldBY=new float[MotorBrain.N];
        int detachedInner=0,detachedOuter=0;
        int count=0;
        for(int frame=0;frame<2200;frame++){
            System.arraycopy(skin.x,0,oldSX,0,oldSX.length);
            System.arraycopy(skin.y,0,oldSY,0,oldSY.length);
            System.arraycopy(body.x,0,oldBX,0,oldBX.length);
            System.arraycopy(body.y,0,oldBY,0,oldBY.length);
            float px=body.cx,py=body.cy;
            // Deterministic environment test stimulus, NOT a live policy.
            float angle=scenario==0?0f:scenario==1?
                (float)Math.PI/3:(float)Math.sin(frame*.009)*2f;
            float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
            float tx=body.cx+dx*145,ty=body.cy+dy*145;
            body.step(motor,dx,dy,.025f,hand,tx,ty);
            skin.step(body,shape,tx,ty,.025f,0);
            gate.constrain(body,skin,.025f,0);
            SquareGripPuzzle.constrain(body,skin,box,.025f,
                oldSX,oldSY,oldBX,oldBY,px,py);
            if(frame>=250){
                count++;
                if(!BodyIntegrity.innerEncloses(body))detachedInner++;
                if(!BodyIntegrity.outerEncloses(body,skin))detachedOuter++;
            }
            ok(Float.isFinite(body.cx)&&Float.isFinite(body.cy),"Nonfinite core");
            ok(Float.isFinite(box.x)&&Float.isFinite(box.y),"Nonfinite box");
            // No grants of bilateral traction or artificial box translation.
            ok(box.displacementFromSpawn()<.1f,"Illegal passive square motion");
            ok(gate.maxDoorTravel<.1f,"Illegal passive gate translation");
        }
        System.out.println("Obstacles case "+scenario+" stable "+count+
            " outside inner="+detachedInner+" outside membrane="+detachedOuter+
            " real wall corrections="+gate.solidLinkCorrections);
        ok(detachedInner<count/12,"Nucleus escapes internal mass polygon at obstacles");
        ok(detachedOuter<count/12,"Nucleus escapes membrane while pressing a solid");
    }
    public static void main(String[] args){
        for(int i=0;i<3;i++)simulate(i);
        System.out.println("Physical rigid-obstacle integrity passed");
    }
}
