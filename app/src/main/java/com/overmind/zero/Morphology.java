package com.overmind.zero;

import java.util.Locale;
import java.util.Random;

/**
 * ZERO-007: no predefined arms, legs or tentacle positions. Ninety-six
 * adjacent elastic surface masses cover the ENTIRE organism perimeter.
 * Each mass receives the SAME learned local controller and may extend,
 * turn sideways or retract based on local state and perceived stimulus.
 * Surface points transmit real contact impulses to movable objects.
 *
 * The established ten-mass MotorBrain is preserved underneath: the old
 * 62 motor weights and 18 manipulation weights remain byte-for-byte valid.
 */
public final class Morphology {
    public static final int POINTS=96;
    public static final int INPUTS=9,HIDDEN=6,PARAMS=INPUTS*HIDDEN+HIDDEN+2*HIDDEN+2;
    public static final float MAX_REACH=105f,MAX_RADIAL=155f;
    private Morphology(){}

    static float clamp(float v,float lo,float hi){return Math.max(lo,Math.min(hi,v));}
    public static float[] virgin(Random rnd){
        float[] w=new float[PARAMS];
        // Random signal pathways, but initial output channels are exactly zero;
        // ZERO has not yet learnt to extend anything.
        for(int i=0;i<INPUTS*HIDDEN+HIDDEN;i++)
            w[i]=(float)rnd.nextGaussian()*.20f;
        return w;
    }
    static void output(float[] w,float[] signal,float[] out){
        float h0=0,h1=0,h2=0,h3=0,h4=0,h5=0;
        for(int j=0;j<HIDDEN;j++){
            float a=w[INPUTS*HIDDEN+j];
            for(int k=0;k<INPUTS;k++)a+=signal[k]*w[j*INPUTS+k];
            float h=(float)Math.tanh(a);
            switch(j){
                case 0:h0=h;break;case 1:h1=h;break;
                case 2:h2=h;break;case 3:h3=h;break;
                case 4:h4=h;break;default:h5=h;break;
            }
        }
        int p=INPUTS*HIDDEN+HIDDEN;
        out[0]=(float)Math.tanh(w[p+2*HIDDEN]+
                h0*w[p]+h1*w[p+1]+h2*w[p+2]+h3*w[p+3]+h4*w[p+4]+h5*w[p+5]);
        out[1]=(float)Math.tanh(w[p+2*HIDDEN+1]+
                h0*w[p+HIDDEN]+h1*w[p+HIDDEN+1]+h2*w[p+HIDDEN+2]+
                h3*w[p+HIDDEN+3]+h4*w[p+HIDDEN+4]+h5*w[p+HIDDEN+5]);
    }

    public static final class Surface {
        public final float[] x=new float[POINTS],y=new float[POINTS];
        public final float[] vx=new float[POINTS],vy=new float[POINTS];
        public final float[] extension=new float[POINTS];
        private final float[] ax=new float[POINTS],ay=new float[POINTS];
        private final float[] nx=new float[POINTS],ny=new float[POINTS];
        private final float[] gx=new float[POINTS],gy=new float[POINTS];
        private final float[] fx=new float[POINTS],fy=new float[POINTS];
        private final float[] out=new float[2];
        private final float[] input=new float[INPUTS];
        public int stretchedPoints=0;
        public float maxExtension=0,totalEffort=0,peakExtension=0;
        public float t=0;

        public Surface(MotorBrain.Body body){reset(body);}
        private void anchors(MotorBrain.Body body){
            for(int i=0;i<POINTS;i++){
                // Continuous interpolation over the existing soft body; there
                // are no special joints, limb slots, or fixed arm locations.
                float u=i*(float)MotorBrain.N/POINTS;
                int j=(int)u;float f=u-j;int next=(j+1)%MotorBrain.N;
                ax[i]=body.x[j]*(1-f)+body.x[next]*f;
                ay[i]=body.y[j]*(1-f)+body.y[next]*f;
                float dx=ax[i]-body.cx,dy=ay[i]-body.cy;
                float d=(float)Math.hypot(dx,dy);
                if(d<.00001f){nx[i]=(float)Math.cos(Math.PI*2*i/POINTS);
                               ny[i]=(float)Math.sin(Math.PI*2*i/POINTS);}
                else{nx[i]=dx/d;ny[i]=dy/d;}
            }
        }
        public void reset(MotorBrain.Body body){
            anchors(body);t=0;totalEffort=0;peakExtension=0;maxExtension=0;stretchedPoints=0;
            for(int i=0;i<POINTS;i++){
                x[i]=ax[i];y[i]=ay[i];
                vx[i]=vy[i]=extension[i]=0;
            }
        }
        public void step(MotorBrain.Body body,float[] brain,float targetX,float targetY,float dt){
            t+=dt;anchors(body);
            float toX=targetX-body.cx,toY=targetY-body.cy;
            float targetD=(float)Math.hypot(toX,toY);
            float dx=targetD>.001f?toX/targetD:0;
            float dy=targetD>.001f?toY/targetD:0;
            float requested=0;
            maxExtension=0;stretchedPoints=0;
            for(int i=0;i<POINTS;i++){
                float radial=(x[i]-ax[i])*nx[i]+(y[i]-ay[i])*ny[i];
                float radialV=vx[i]*nx[i]+vy[i]*ny[i];
                float tx=-ny[i],ty=nx[i];
                float a=(float)(2*Math.PI*i/POINTS);
                input[0]=dx*nx[i]+dy*ny[i];
                input[1]=dx*tx+dy*ty;
                input[2]=clamp((targetD-40f)/140f,-1,2);
                input[3]=clamp(radial/90f,-1,2);
                input[4]=clamp(radialV/90f,-2,2);
                input[5]=(float)Math.sin(t*2.8f+a);
                input[6]=(float)Math.cos(t*2.8f+a);
                input[7]=clamp((float)Math.hypot(targetX-x[i],targetY-y[i])/125f,0,2);
                input[8]=1;
                output(brain,input,out);
                // One locally learned activation per membrane particle.
                // There is no action named "grow arm" or fixed arm count.
                extension[i]=Math.max(0,out[0])*MAX_REACH;
                requested+=extension[i];
                gx[i]=out[1]*28f; // lateral steering, not a fixed joint
            }
            // Finite synthetic material: prevent the network from inflating
            // all 32 nodes into a permanently enormous bubble.
            // Budget scales with the number of material particles so existing
            // v0.7 muscle weights retain similar behavior after 32→96 refinement.
            float budget=Math.min(1f,(385f*POINTS/32f)/Math.max(1,requested));
            for(int i=0;i<POINTS;i++){
                extension[i]*=budget;
                float tx=-ny[i],ty=nx[i];
                float targetSurfaceX=ax[i]+extension[i]*nx[i]+gx[i]*tx;
                float targetSurfaceY=ay[i]+extension[i]*ny[i]+gx[i]*ty;
                fx[i]=clamp((targetSurfaceX-x[i])*56f-(vx[i]-body.cvx)*7f,-3300,3300);
                fy[i]=clamp((targetSurfaceY-y[i])*56f-(vy[i]-body.cvy)*7f,-3300,3300);
                maxExtension=Math.max(maxExtension,extension[i]);
                if(extension[i]>22f)stretchedPoints++;
                totalEffort+=extension[i]*extension[i]*dt*.000005f;
            }
            // Interparticle tension: neighbors resist tearing but can yield
            // for a thin transient pseudopod.
            for(int i=0;i<POINTS;i++){
                int j=(i+1)%POINTS;
                float edx=x[j]-x[i],edy=y[j]-y[i];
                float d=(float)Math.hypot(edx,edy);
                if(d<.0001f)continue;
                float rest=(float)Math.hypot(ax[j]-ax[i],ay[j]-ay[i]);
                float rel=((vx[j]-vx[i])*edx+(vy[j]-vy[i])*edy)/d;
                float force=clamp((d-rest)*18f+rel*1.2f,-2400,2400);
                float px=force*edx/d,py=force*edy/d;
                fx[i]+=px;fy[i]+=py;fx[j]-=px;fy[j]-=py;
            }
            float damping=(float)Math.exp(-3f*dt);
            for(int i=0;i<POINTS;i++){
                vx[i]=clamp((vx[i]+fx[i]*dt)*damping,-230,230);
                vy[i]=clamp((vy[i]+fy[i]*dt)*damping,-230,230);
                x[i]+=vx[i]*dt;y[i]+=vy[i]*dt;
                float offX=x[i]-body.cx,offY=y[i]-body.cy;
                float radius=(float)Math.hypot(offX,offY);
                if(radius>MAX_RADIAL){
                    x[i]=body.cx+offX*MAX_RADIAL/radius;
                    y[i]=body.cy+offY*MAX_RADIAL/radius;
                    vx[i]*=.35f;vy[i]*=.35f;
                }
                if(!Float.isFinite(x[i])||!Float.isFinite(y[i]))reset(body);
                peakExtension=Math.max(peakExtension,Math.max(0,
                    (x[i]-ax[i])*nx[i]+(y[i]-ay[i])*ny[i]));
            }
        }
    }
    /** Collision against the ACTUAL deforming membrane, not a fake reach. */
    public static float collide(Surface s,UnknownWorld.Entity disk,float dt){
        if(!disk.isMovable())return 0;
        float moved=0,r=disk.radius+2.4f;
        for(int i=0;i<POINTS;i++){
            int j=(i+1)%POINTS;
            float ex=s.x[j]-s.x[i],ey=s.y[j]-s.y[i];
            float div=Math.max(1e-5f,ex*ex+ey*ey);
            float t=clamp(((disk.x-s.x[i])*ex+(disk.y-s.y[i])*ey)/div,0,1);
            float px=s.x[i]+ex*t,py=s.y[i]+ey*t;
            float dx=disk.x-px,dy=disk.y-py;
            float distance=(float)Math.hypot(dx,dy);
            if(distance>=r)continue;
            float nx=distance>.0001f?dx/distance:1f;
            float ny=distance>.0001f?dy/distance:0;
            float penetration=clamp(r-distance,0,11);
            float correction=penetration*.55f;
            disk.x+=nx*correction;disk.y+=ny*correction;
            s.x[i]-=nx*correction*(1-t);
            s.y[i]-=ny*correction*(1-t);
            s.x[j]-=nx*correction*t;
            s.y[j]-=ny*correction*t;
            float rv=(disk.vx-s.vx[i]*(1-t)-s.vx[j]*t)*nx+
                    (disk.vy-s.vy[i]*(1-t)-s.vy[j]*t)*ny;
            float impulse=clamp(Math.max(0,-rv)*.23f+penetration*dt*25f,0,44);
            disk.vx+=nx*impulse;disk.vy+=ny*impulse;
            s.vx[i]-=nx*impulse*.34f*(1-t);
            s.vy[i]-=ny*impulse*.34f*(1-t);
            s.vx[j]-=nx*impulse*.34f*t;
            s.vy[j]-=ny*impulse*.34f*t;
            moved+=impulse;
        }
        disk.vx=clamp(disk.vx,-240,240);disk.vy=clamp(disk.vy,-240,240);
        return moved;
    }
    public static float rollout(float[] motor,float[] hand,float[] morph,float angle){
        MotorBrain.Body body=new MotorBrain.Body();
        Surface surface=new Surface(body);
        float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
        UnknownWorld.Entity disk=new UnknownWorld.Entity(dx*86,dy*86,2,2);
        float start=disk.x*dx+disk.y*dy;
        float bestDistance=90,impulseSum=0;
        for(int frame=0;frame<110;frame++){
            float dt=MotorBrain.STEP;
            body.step(motor,dx,dy,dt,hand,disk.x,disk.y);
            surface.step(body,morph,disk.x,disk.y,dt);
            impulseSum+=collide(surface,disk,dt);
            impulseSum+=ContactLearning.collide(body,disk,dt);
            disk.tick(dt);
            // Measure nearest physical membrane point, not a scripted hand.
            for(int i=0;i<POINTS;i++){
                float d=(float)Math.hypot(surface.x[i]-disk.x,surface.y[i]-disk.y);
                bestDistance=Math.min(bestDistance,d);
            }
        }
        float forward=(disk.x*dx+disk.y*dy)-start;
        float sideways=Math.abs(disk.x*dy-disk.y*dx);
        float approach=90-bestDistance;
        float result=.75f*approach+.7f*forward-.18f*sideways+
            .008f*Math.min(300,impulseSum)-.25f*surface.totalEffort;
        if(!Float.isFinite(result)||!Float.isFinite(surface.peakExtension))return -100000;
        return result;
    }
    public static float fitness(float[] motor,float[] hand,float[] morph){
        return (rollout(motor,hand,morph,0)+rollout(motor,hand,morph,2.094395f)+
                rollout(motor,hand,morph,4.188790f))/3f;
    }
    public static float unseen(float[] motor,float[] hand,float[] morph){
        return (rollout(motor,hand,morph,.7f)+rollout(motor,hand,morph,2.8f)+
                rollout(motor,hand,morph,5.03f))/3f;
    }
    public static final class Trainer{
        private final Random rnd=new Random(7192026);
        public volatile float[] best;
        public volatile int attempts,accepted;
        public volatile int gateAttempts,gateAccepted;
        public volatile float bestFitness=Float.NaN;
        private float mutation=.26f;
        public Trainer(float[] loaded){
            best=loaded==null?virgin(new Random(14790)):loaded.clone();
        }
        /**
         * Separate curriculum for genuinely constrained manipulation.
         * The baseline and candidate both face the SAME closed chamber.
         * A candidate must improve actual measured door displacement,
         * bilateral contact or food access, and remain stable on holdout.
         */
        public boolean attemptGate(float[] motor,float[] hand){
            float[] baseline=best;
            float baselineScore=ScarcityLab.trainingFitness(motor,hand,baseline);
            float[] candidate=baseline.clone();
            int changed=2+rnd.nextInt(9);
            for(int i=0;i<changed;i++){
                int k=rnd.nextInt(candidate.length);
                candidate[k]=clamp(candidate[k]+(float)rnd.nextGaussian()*mutation,-3.2f,3.2f);
            }
            float score=ScarcityLab.trainingFitness(motor,hand,candidate);
            gateAttempts++;
            attempts++;
            if(!Float.isFinite(score)||score<=baselineScore+.04f)return false;
            float holdOld=ScarcityLab.heldoutFitness(motor,hand,baseline);
            float holdNew=ScarcityLab.heldoutFitness(motor,hand,candidate);
            if(holdNew<holdOld-.12f)return false;
            best=candidate;
            gateAccepted++;
            accepted++;
            bestFitness=score;
            return true;
        }
        public boolean attempt(float[] motor,float[] hand){
            float[] base=best;
            float oldFitness=fitness(motor,hand,base);
            bestFitness=oldFitness;
            float[] candidate=base.clone();
            int changed=2+rnd.nextInt(8);
            for(int i=0;i<changed;i++){
                int k=rnd.nextInt(candidate.length);
                candidate[k]=clamp(candidate[k]+(float)rnd.nextGaussian()*mutation,-3.2f,3.2f);
            }
            float test=fitness(motor,hand,candidate);
            attempts++;
            if(Float.isFinite(test)&&test>oldFitness+.015f){
                best=candidate;bestFitness=test;accepted++;
                if(accepted%8==0)mutation=Math.max(.08f,mutation*.96f);
                return true;
            }
            if(attempts%120==0)mutation=Math.min(.6f,mutation*1.08f);
            return false;
        }
    }
    /** Standard-JVM physics and safety invariants checked by CI. */
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Surface surface=new Surface(body);
        float[] blank=virgin(new Random(1));
        for(int i=0;i<80;i++)surface.step(body,blank,80,0,MotorBrain.STEP);
        if(surface.peakExtension>.5f||surface.maxExtension>.0001f)
            throw new IllegalStateException("Virgin body fabricated pre-programmed arms");
        float[] programmed=blank.clone();
        // Test a LOCAL deformation without predefined limb positions.
        programmed[INPUTS*HIDDEN+HIDDEN+2*HIDDEN]=1.7f;
        for(int i=0;i<80;i++)surface.step(body,programmed,80,0,MotorBrain.STEP);
        float max=0;
        for(int i=0;i<POINTS;i++){
            float radial=(float)Math.hypot(surface.x[i]-body.cx,surface.y[i]-body.cy);
            max=Math.max(max,radial);
        }
        if(max<32||max>MAX_RADIAL+.1f)
            throw new IllegalStateException("Surface extension not physical");
        UnknownWorld.Entity disk=new UnknownWorld.Entity(400,400,2,2);
        float far=collide(surface,disk,MotorBrain.STEP);
        if(far!=0||disk.vx!=0||disk.vy!=0)
            throw new IllegalStateException("Noncontact accelerated disk");
        disk=new UnknownWorld.Entity(surface.x[0]+6,surface.y[0],2,2);
        float touched=collide(surface,disk,MotorBrain.STEP);
        if(touched<=0||!Float.isFinite(disk.vx))
            throw new IllegalStateException("Deformed surface failed contact");
        float[] motor=MotorBrain.virgin(new Random(918273));
        float[] hand=ContactLearning.virgin();
        float baseline=fitness(motor,hand,blank);
        Trainer trainer=new Trainer(blank);
        for(int i=0;i<16;i++)trainer.attempt(motor,hand);
        trainer.attemptGate(motor,hand);
        if(trainer.gateAttempts!=1)
            throw new IllegalStateException("Gate curriculum never ran");
        float learnt=fitness(motor,hand,trainer.best);
        if(!Float.isFinite(baseline)||learnt+1e-3f<baseline||trainer.best.length!=PARAMS)
            throw new IllegalStateException("Morphology trainer regression");
        System.out.printf(Locale.US,
          "ZERO-007 passed: motorPoints=%d shellPoints=%d morphWeights=%d extendedRadius=%.2f contacts=%.2f fitness=%.3f -> %.3f accepted=%d%n",
          MotorBrain.N,POINTS,PARAMS,max,touched,baseline,learnt,trainer.accepted);
    }
}
