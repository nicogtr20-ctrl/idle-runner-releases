package com.overmind.zero;

import java.util.Locale;

/** OTA163 pure lab: real gel, real pressure and ALL solid material contacts.
 * Only JVM acceptance, never an actuator/goal/hand-coded rhythm in Android.
 */
public final class Zero163MaterialIntegrationRegression {
    static void ok(boolean v,String why){if(!v)throw new AssertionError("OTA163: "+why);}
    static float peak(MotorBrain.Body b,Morphology.Surface s){
        float k=0;
        for(int i=0;i<Morphology.POINTS;i++)
            k=Math.max(k,(float)Math.hypot(s.x[i]-b.cx,s.y[i]-b.cy));
        return Math.max(0,k-28f);
    }
    static float norm(float x,float y){return (float)Math.hypot(x,y);}
    public static void main(String[] args){
        float dt=.025f;
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        SymbioteMatterV1 matter=new SymbioteMatterV1();
        matter.reset(b,s);
        float[] arm=new float[64];
        arm[0]=1;arm[4]=.92f;arm[8]=.72f;
        for(int i=0;i<240;i++)matter.step(b,s,arm,dt);
        float extended=peak(b,s),historic=s.peakExtension;
        ok(historic>10&&Math.abs(historic-extended)<.01f,
            "proprioception does not equal shape NOW after active extension");
        float handedness=SymbioteMatterV1.signedArea(s)>=0?1f:-1f;
        float[] normal=new float[2];
        float worstOrthogonality=0;
        for(int i=0;i<Morphology.POINTS;i++){
            SymbioteMatterV1.normalAt(s,i,b.cx,b.cy,handedness,normal);
            int prev=(i+Morphology.POINTS-1)%Morphology.POINTS;
            int next=(i+1)%Morphology.POINTS;
            float tx=s.x[next]-s.x[prev],ty=s.y[next]-s.y[prev];
            float t=norm(tx,ty);
            if(t>.001f)worstOrthogonality=Math.max(worstOrthogonality,
                Math.abs((normal[0]*tx+normal[1]*ty)/t));
        }
        ok(worstOrthogonality<.001f,
            "motor normal no longer perpendicular to living contour");
        float[] zero=new float[64];
        for(int i=0;i<520;i++)matter.step(b,s,zero,dt);
        float retracted=peak(b,s);
        ok(Math.abs(s.peakExtension-retracted)<.01f,
            "body-sense still reports historical rather than current extension");
        ok(retracted<extended-3f,
            "relaxed muscle cannot retract physical membrane");
        ok(matter.invalid==0&&MaterialGeometry.selfCrossings(s)==0&&
           matter.areaRelativeError(s)<.16f,"unstable volume/topology");
        // A square is a genuine solid also for the 64 liquid particles.
        UnknownWorld.Entity cover=new UnknownWorld.Entity(500,500,2,5);
        matter.x[0]=500;matter.y[0]=500;
        matter.vx[0]=matter.vy[0]=0;
        SquareCollisionSolver.constrainInterior(matter,cover,dt);
        ok(Math.abs(matter.x[0]-500)>=SquareGripPuzzle.HALF+1.49f||
           Math.abs(matter.y[0]-500)>=SquareGripPuzzle.HALF+1.49f,
           "inner 64 liquid particles penetrate square");
        // Rotated construction plate with the exact real geometry.
        UnknownWorld.Entity panel=new UnknownWorld.Entity(-500,-500,2,6);
        panel.angle=.58f;
        matter.x[1]=panel.x;matter.y[1]=panel.y;
        matter.vx[1]=matter.vy[1]=0;
        PanelPhysics.constrainInterior(matter,panel,dt);
        float[] uv=PanelPhysics.local(panel,matter.x[1],matter.y[1]);
        ok(Math.abs(uv[0])>=PanelPhysics.halfLength(panel)+1.49f||
           Math.abs(uv[1])>=PanelPhysics.HALF_THICKNESS+1.49f,
           "inner liquid penetrates rotated plate");
        // The gate is a wall in the same chamber for both shell and fluid.
        ScarcityLab chamber=new ScarcityLab(0,0);
        matter.x[2]=chamber.gateX();matter.y[2]=chamber.gateCenterY();
        matter.vx[2]=matter.vy[2]=0;
        chamber.constrainInterior(matter,dt);
        ok(Math.abs(matter.x[2]-chamber.gateX())>=ScarcityLab.DOOR_HALF_W+1.49f||
           Math.abs(matter.y[2]-chamber.gateCenterY())>=ScarcityLab.DOOR_HALF_H+1.49f,
           "inner liquid penetrates active gate");
        // Non-symmetric 3-appendage geometry remains actual material,
        // not a scripted fixed limb count or fabricated sprite.
        System.out.printf(Locale.US,
            "ZERO163 MATTER PASS stretched=%.2f now=%.2f fallback=%d orthErr=%.5f areaErr=%.5f collisions=3%n",
            extended,retracted,matter.longTipReactionFallbacks,
            worstOrthogonality,matter.areaRelativeError(s));
    }
}
