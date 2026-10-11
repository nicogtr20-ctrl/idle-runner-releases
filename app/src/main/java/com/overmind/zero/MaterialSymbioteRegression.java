package com.overmind.zero;
import java.util.ArrayList;
import java.util.Random;

/**
 * ZERO 0120 material-only acceptance checks: no physical nucleus; shape,
 * vision, tactile energy and actuators refer to REAL living tissue.
 */
public final class MaterialSymbioteRegression {
    private static void check(boolean b,String reason){
        if(!b)throw new AssertionError(reason);
    }
    private static void refCheck(MotorBrain.Body b){
        float px=0,py=0,vx=0,vy=0;
        for(int j=0;j<MotorBrain.N;j++){
            px+=b.x[j];py+=b.y[j];vx+=b.vx[j];vy+=b.vy[j];
        }
        check(Math.abs(b.cx-px/MotorBrain.N)<.015f,
              "Observed center drifted from physical material x");
        check(Math.abs(b.cy-py/MotorBrain.N)<.015f,
              "Observed center drifted from physical material y");
        check(Math.abs(b.cvx-vx/MotorBrain.N)<.02f,
              "Observed center velocity does not match physical tissue");
    }
    private static void materialAndSenses(){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface s=new Morphology.Surface(b);
        float[] motor=MotorBrain.virgin(new Random(12041));
        float[] morph=Morphology.virgin(new Random(12042));
        // No hard-coded reach; a trained-looking local actuator should
        // physically transmit reaction force to inner tissue nodes.
        morph[72]=.82f;morph[0]=.63f;
        float[] oldVx=b.vx.clone(),oldVy=b.vy.clone();
        for(int t=0;t<4800;t++){
            float phase=(float)(t*.013),ux=(float)Math.cos(phase),
                uy=(float)Math.sin(phase);
            b.step(motor,ux,uy,MotorBrain.STEP);
            s.step(b,morph,b.cx+ux*145,b.cy+uy*145,MotorBrain.STEP);
            refCheck(b);
            for(int i=0;i<MotorBrain.N;i++)check(
                Float.isFinite(b.x[i])&&Float.isFinite(b.y[i]),
                "Inner mass became nonfinite");
            for(int i=0;i<Morphology.POINTS;i++)check(
                Float.isFinite(s.x[i])&&Float.isFinite(s.y[i]),
                "Outer membrane became nonfinite");
        }
        check(SensorimotorMind.S==26&&SensorimotorMind.ACTIONS==16,
              "Changed main brain's original input or action count");
        float[] proprio=BodyProprioception.sense(b,s);
        check(proprio.length==48,
              "Distributed body does not perceive its dimensional extent");
        check(BodyProprioception.PARAMETERS>2500,
              "Geometry head has no actual trainable predictor");
        float[] senses=SensorimotorMind.Senses.read(b,s,null,new ArrayList<>(),50);
        check(senses.length==26,"Main view width changed unexpectedly");
        // The logical center can be on another part of the bounding plane,
        // but no energy/solid contact may be faked at it.
        float x=b.cx+320,y=b.cy+190;
        check(!MaterialGeometry.touches(b,s,x,y,6),
              "Phantom energy collected by a mathematical center");
        check(MaterialGeometry.touches(b,s,s.x[0],s.y[0],1),
              "Real membrane patch failed to sense its own contact");
        // Manual corruption of cached readout MUST NOT move tissue.
        float[] previous=b.x.clone();
        b.cx+=500;b.cy-=200;b.cvx+=55;
        MaterialGeometry.observe(b);
        for(int i=0;i<MotorBrain.N;i++)
            check(previous[i]==b.x[i],"Moving coordinate estimate moved physical body");
        refCheck(b);
        System.out.println("Symbiote: 4800 material-only real actuator steps, fixed "+
            "26/16 policy, 48 physical proprioception, no logical collider");
    }
    private static void obstacleContacts(){
        for(int scenario=0;scenario<3;scenario++){
            MotorBrain.Body b=new MotorBrain.Body();
            Morphology.Surface s=new Morphology.Surface(b);
            float[] motor=MotorBrain.virgin(new Random(160+scenario));
            float[] morph=Morphology.virgin(new Random(260+scenario));
            UnknownWorld.Entity box=new UnknownWorld.Entity(100,0,1,5);
            ScarcityLab gate=new ScarcityLab(0,0);
            float[] xs=new float[Morphology.POINTS],ys=new float[Morphology.POINTS],
                    bx=new float[MotorBrain.N],by=new float[MotorBrain.N];
            for(int t=0;t<1800;t++){
                System.arraycopy(s.x,0,xs,0,xs.length);
                System.arraycopy(s.y,0,ys,0,ys.length);
                System.arraycopy(b.x,0,bx,0,bx.length);
                System.arraycopy(b.y,0,by,0,by.length);
                float oldX=b.cx,oldY=b.cy;
                float angle=scenario==0?0f:scenario==1?
                  (float)Math.PI/3f:(float)Math.sin(t*.018);
                float mx=(float)Math.cos(angle),my=(float)Math.sin(angle);
                b.step(motor,mx,my,MotorBrain.STEP);
                s.step(b,morph,b.cx+mx*145,b.cy+my*145,MotorBrain.STEP);
                if(scenario==1)gate.constrain(b,s,MotorBrain.STEP,0);
                if(scenario!=1)
                    SquareGripPuzzle.constrain(b,s,box,MotorBrain.STEP,
                        xs,ys,bx,by,oldX,oldY);
                MaterialGeometry.observe(b);
                refCheck(b);
                check(Math.abs(box.vx)<.0001f&&Math.abs(box.vy)<.0001f,
                      "Box moved without two real sustained neural contacts");
                check(!Float.isNaN(s.x[0]),"Invalid membrane");
            }
        }
        System.out.println("Symbiote: 5400 real rigid-solid physical solver steps "+
                           "no core impulse and no uncommanded square motion");
    }
    public static void main(String[] args){
        MaterialGeometry.main(args);
        materialAndSenses();
        obstacleContacts();
        System.out.println("ZERO 0120: distributed energetic-material organism PASS");
    }
}
