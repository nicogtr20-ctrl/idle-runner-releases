package com.overmind.zero;

import java.util.ArrayList;

/** Zero-production-change detection of sensor/actuator-frame mismatches.
 * These checks intentionally characterize current OTA153 behavior, NOT
 * approval of it. Run in isolated audit branch, never ship as a "fix".
 */
public final class Zero153WholeBodyAuditRegression {
    private static void yes(boolean v,String why){if(!v)throw new AssertionError(why);}
    private static float absDiff(float[] a,float[] b){
        float d=0;for(int i=0;i<a.length;i++)d+=Math.abs(a[i]-b[i]);return d;
    }
    private static void rotate90(MotorBrain.Body b,Morphology.Surface s,
                                  SymbioteMatterV1 m){
        float x0=b.cx,y0=b.cy;
        for(int i=0;i<Morphology.POINTS;i++){
            float x=s.x[i]-x0,y=s.y[i]-y0;
            s.x[i]=x0-y;s.y[i]=y0+x;
            float vx=s.vx[i],vy=s.vy[i];s.vx[i]=-vy;s.vy[i]=vx;
        }
        for(int i=0;i<MotorBrain.N;i++){
            float x=b.x[i]-x0,y=b.y[i]-y0;
            b.x[i]=x0-y;b.y[i]=y0+x;
            float vx=b.vx[i],vy=b.vy[i];b.vx[i]=-vy;b.vy[i]=vx;
        }
        for(int i=0;i<SymbioteMatterV1.INNER;i++){
            float x=m.x[i]-x0,y=m.y[i]-y0;
            m.x[i]=x0-y;m.y[i]=y0+x;
            float vx=m.vx[i],vy=m.vy[i];m.vx[i]=-vy;m.vy[i]=vx;
        }
        m.observeBody(b,s);
    }
    public static void main(String[] args){
        MotorBrain.Body b=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(b);
        SymbioteMatterV1 fluid=new SymbioteMatterV1();
        fluid.reset(b,skin);
        yes(MaterialGeometry.selfCrossings(skin)==0,"newborn polygon invalid");
        float[] cortex=new float[64];cortex[0]=1f;
        SensorimotorMind mind=new SensorimotorMind();
        mind.appliedSymbioteControls(cortex);
        float[] initial=SymbioteMotorHeadV1.measuredSectorAngles(b,skin);
        float[] feedbackAtBirth=mind.appliedEfferenceForTest();
        rotate90(b,skin,fluid);
        mind.appliedSymbioteControls(cortex);
        float[] feedbackAt90=mind.appliedEfferenceForTest();
        float[] physicalAngles=SymbioteMotorHeadV1.measuredSectorAngles(b,skin);
        float physicallyProjectedX=(float)Math.cos(physicalAngles[0])/16f;
        float physicallyProjectedY=(float)Math.sin(physicalAngles[0])/16f;
        float err=(float)Math.hypot(feedbackAt90[1]-physicallyProjectedX,
                                   feedbackAt90[2]-physicallyProjectedY);
        yes(Math.abs(physicalAngles[0]-initial[0])>1.2f,
            "controlled physical rotation not observed by updated head");
        yes(absDiff(feedbackAtBirth,feedbackAt90)<.000001f&&err>.05f,
            "OTA153 control copy unexpectedly follows true rotating skin");
        System.out.printf("CONFIRMED-EFFERENCE-BUG projected-world-XY error=%.5f "+
            "(state remains identical despite rotating real muscle)%n",err);

        // Actual receptor map 74x7: sample an intentionally asymmetric
        // parcel then rotate all tissue. Body-relative metrics should
        // rotate WITH body; its cortex labels (fixed membrane IDs) do not.
        MotorBrain.Body q=new MotorBrain.Body();
        Morphology.Surface sq=new Morphology.Surface(q);
        SymbioteMatterV1 gel=new SymbioteMatterV1();
        gel.reset(q,sq);
        gel.x[1]+=3f;gel.y[1]+=5f;gel.vx[1]=93f;gel.vy[1]=-11f;
        gel.observeBody(q,sq);
        FluidInteroceptionV1 read=new FluidInteroceptionV1();
        read.feed(FluidInteroceptionV1.sense(q,sq,gel),q,gel);
        float[] pre=read.summary();
        rotate90(q,sq,gel);
        read.feed(FluidInteroceptionV1.sense(q,sq,gel),q,gel);
        float[] after=read.summary();
        float rotatedDifference=absDiff(pre,after);
        yes(rotatedDifference>.05f,
            "world-azimuth-binned fluid accidentally follows rotating cortex labels");
        System.out.printf("CONFIRMED-INTERNAL-SECTOR-BUG rotated-equivalent "+
            "cortex sensory-map absolute difference=%.5f%n",rotatedDifference);

        // Spectral NAVIGATION still sees through an invented centroid eye.
        // The 26D main cortex sees from a real protruding membrane patch,
        // bypassing the box; the navigation model sees a central obstruction.
        MotorBrain.Body r=new MotorBrain.Body();
        Morphology.Surface sr=new Morphology.Surface(r);
        new SymbioteMatterV1().reset(r,sr);
        sr.x[0]=r.cx+125f;sr.y[0]=r.cy;
        ArrayList<UnknownWorld.Entity> world=new ArrayList<>();
        UnknownWorld.Entity light=new UnknownWorld.Entity(r.cx+190f,r.cy,0,0);
        UnknownWorld.Entity blocker=new UnknownWorld.Entity(r.cx+78f,r.cy,2,5);
        world.add(light);world.add(blocker);
        float[] mainEye=SensorimotorMind.Senses.read(r,sr,null,world,70f);
        float primary=0;for(int i=14;i<18;i++)primary+=mainEye[i];
        InstrumentalNavigation navigation=new InstrumentalNavigation();
        navigation.sense(r,null,world);
        float nav=navigation.visible()[0];
        yes(primary>.07f&&nav<.00001f,
            "controlled actual membrane photoreceptor and phantom centre eye matched");
        System.out.printf("CONFIRMED-NAV-VISION-BUG true eye signal=%.4f "+
            "centroid-only nav signal=%.4f%n",primary,nav);

        // Actual photoreceptor is 110 units from stimulus, yet main 26D
        // input entirely suppresses it because the centroid is >210 away.
        world.clear();light.x=r.cx+235f;world.add(light);
        float minEye=(float)Math.hypot(light.x-sr.x[0],light.y-sr.y[0]);
        float[] far=SensorimotorMind.Senses.read(r,sr,null,world,70f);
        float mainFar=0;for(int i=14;i<18;i++)mainFar+=far[i];
        yes(minEye<125f&&mainFar==0f,
            "primary spectrum no longer clipped by fictional centroid distance");
        System.out.printf("CONFIRMED-PRIMARY-VISION-RANGE-BUG surface-distance=%.1f "+
            "centre-distance=%.1f main spectral=%.4f%n",
            minEye,light.x-r.cx,mainFar);

        // Brief wind/impact impulses enter proprioception and 16×5
        // distributed cortex, but NOT the main 26D contact summary.
        world.clear();sr.touchSense[0]=0f;sr.touchPulse[0]=1f;
        float[] tactile=SensorimotorMind.Senses.read(r,sr,null,world,70f);
        float[] touch=DistributedBodySense.sense(r,sr,null,world);
        yes(tactile[4]==0f&&touch[DistributedBodySense.TOUCH]>0f,
            "main contact channel now conflates transient impulse intentionally");
        System.out.printf("TACTILE-SEMANTIC-GAP main-contact=%f local-tactile=%f%n",
            tactile[4],touch[DistributedBodySense.TOUCH]);
        System.out.println("AUDIT PASS: findings reproducible on active OTA153 sources; "+
          "this test changes NO production code, reward, saved model or physics.");
    }
}
