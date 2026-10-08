package com.overmind.zero;

import java.util.Locale;
import java.util.Random;

/**
 * ZERO-003: mechanics-driven object handling.
 *
 * Movement of objects is calculated from collisions with the organism's
 * simulated elastic perimeter. Touching an object does not add a scripted push.
 * A small, initially zeroed muscle-modulation network is optimized by repeated
 * simulation trials. It receives raw distances, surface alignment, and phase;
 * it has no pre-recorded grasp, number of arms, or pushing animation.
 */
public final class ContactLearning {
    public static final int PARAMS=18; // two outputs, eight inputs and two biases
    public static final float DT=MotorBrain.STEP;
    private ContactLearning(){}

    static float clamp(float v,float min,float max){return Math.min(max,Math.max(min,v));}
    public static float[] virgin(){return new float[PARAMS];}
    public static float[] copy(float[] a){return a.clone();}

    static float[] modulation(float[] w,float nx,float ny,float dx,float dy,
                              float dist,float stretch,float phase,float radialSpeed){
        float[] f={
            1f,
            nx*dx+ny*dy,
            nx*dy-ny*dx,
            clamp(dist/95,-1f,1.5f),
            clamp(stretch/22,-2f,2f),
            (float)Math.sin(phase),
            (float)Math.cos(phase),
            clamp(radialSpeed/125,-2f,2f)
        };
        float a=w[16],b=w[17];
        for(int i=0;i<8;i++){a+=w[i]*f[i];b+=w[i+8]*f[i];}
        return new float[]{(float)Math.tanh(a),(float)Math.tanh(b)};
    }

    /**
     * Apply an elastic contact constraint between the movable disk and every
     * boundary segment. The object accelerates ONLY through relative movement,
     * deformation and non-penetration forces. If ZERO is not touching, no
     * impulsive contact forces exist.
     *
     * Returns measured impulse magnitude to support physical diagnostics.
     */
    public static float collide(MotorBrain.Body body,UnknownWorld.Entity disk,float dt){
        if(!disk.isMovable())return 0;
        float impulseTotal=0;
        float r=disk.radius+2.4f;
        for(int i=0;i<MotorBrain.N;i++){
            int j=(i+1)%MotorBrain.N;
            float ax=body.x[i],ay=body.y[i],bx=body.x[j],by=body.y[j];
            float edx=bx-ax,edy=by-ay;
            float t=clamp(((disk.x-ax)*edx+(disk.y-ay)*edy)/
                    Math.max(1e-6f,edx*edx+edy*edy),0,1);
            float sx=ax+t*edx,sy=ay+t*edy;
            float dx=disk.x-sx,dy=disk.y-sy;
            float distance=(float)Math.hypot(dx,dy);
            if(distance>=r)continue;
            float nx,ny;
            if(distance<1e-4f){
                float fallback=(float)Math.hypot(edx,edy);
                nx=fallback>1e-4f?-edy/fallback:1;
                ny=fallback>1e-4f?edx/fallback:0;
            }else{nx=dx/distance;ny=dy/distance;}
            float penetration=clamp(r-distance,0,11);
            float m0=1-t,m1=t;
            // Remove penetration by splitting correction between body and disk.
            float shift=penetration*.53f;
            disk.x+=nx*shift;disk.y+=ny*shift;
            body.x[i]-=nx*shift*m0;body.y[i]-=ny*shift*m0;
            body.x[j]-=nx*shift*m1;body.y[j]-=ny*shift*m1;
            float pvx=body.vx[i]*m0+body.vx[j]*m1;
            float pvy=body.vy[i]*m0+body.vy[j]*m1;
            float relative=(disk.vx-pvx)*nx+(disk.vy-pvy)*ny;
            // Pressure created by resolving compression over dt, damped to
            // prevent tunnelling/explosive numerical artifacts.
            float collisionSpeed=Math.max(0,-relative);
            float impulse=clamp(collisionSpeed*.23f+penetration*dt*25f,0,55);
            disk.vx+=nx*impulse;disk.vy+=ny*impulse;
            body.vx[i]-=nx*impulse*.33f*m0;
            body.vy[i]-=ny*impulse*.33f*m0;
            body.vx[j]-=nx*impulse*.33f*m1;
            body.vy[j]-=ny*impulse*.33f*m1;
            impulseTotal+=impulse;
        }
        // Core disk also collides. No magical effect when bodies are apart.
        float dx=disk.x-body.cx,dy=disk.y-body.cy;
        float d=(float)Math.hypot(dx,dy),limit=disk.radius+6;
        if(d<limit){
            float nx=d>1e-5f?dx/d:1,ny=d>1e-5f?dy/d:0;
            float overlap=clamp(limit-d,0,12);
            float shift=overlap*.62f;
            disk.x+=nx*shift;disk.y+=ny*shift;
            body.cx-=nx*shift*.35f;body.cy-=ny*shift*.35f;
            float rv=(disk.vx-body.cvx)*nx+(disk.vy-body.cvy)*ny;
            float impulse=clamp(Math.max(0,-rv)*.31f+overlap*dt*24f,0,50);
            disk.vx+=nx*impulse;disk.vy+=ny*impulse;
            body.cvx-=nx*impulse*.34f;body.cvy-=ny*impulse*.34f;
            impulseTotal+=impulse;
        }
        // No acceleration without physical contact: acceleration is calculated
        // only in the if-overlap blocks above.
        disk.vx=clamp(disk.vx,-240,240);disk.vy=clamp(disk.vy,-240,240);
        return impulseTotal;
    }

    /**
     * Training objective: make an initially static unknown object move toward
     * the sensed stimulus. Every trial calls the EXACT live elastic motor
     * simulation and contact solver. Test angles differ from train angles.
     */
    public static float rollout(float[] motor,float[] manipulation,float angle){
        MotorBrain.Body body=new MotorBrain.Body();
        float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
        float initialX=dx*71f,initialY=dy*71f;
        UnknownWorld.Entity disk=new UnknownWorld.Entity(initialX,initialY,2,2);
        float contacts=0;
        float referenceWork=0;
        for(int i=0;i<205;i++){
            body.step(motor,dx,dy,DT,manipulation,disk.x,disk.y);
            contacts+=collide(body,disk,DT);
            disk.tick(DT);
        }
        float forward=(disk.x-initialX)*dx+(disk.y-initialY)*dy;
        float sideways=Math.abs((disk.x-initialX)*dy-(disk.y-initialY)*dx);
        float effort=body.totalWork-referenceWork;
        if(!Float.isFinite(forward)||!Float.isFinite(effort))return -100000;
        return forward-.25f*sideways-.08f*effort+Math.min(8,contacts*.01f);
    }

    public static float trainingFitness(float[] motor,float[] manipulation){
        return (rollout(motor,manipulation,0)+
                rollout(motor,manipulation,2.0943951f)+
                rollout(motor,manipulation,4.1887902f))/3;
    }
    public static float heldout(float[] motor,float[] manipulation){
        return (rollout(motor,manipulation,.7f)+
                rollout(motor,manipulation,2.75f)+
                rollout(motor,manipulation,5.02f))/3;
    }
    public static final class Trainer {
        private final Random rnd=new Random(90315);
        public volatile float[] best;
        public volatile float bestFitness;
        public volatile int attempts,accepted;
        private float spread=.3f;
        public Trainer(float[] first){
            best=first==null?virgin():first.clone();
            // Fitness changes as the independently trained motor learns;
            // initialize when the motor snapshot is available.
            bestFitness=Float.NEGATIVE_INFINITY;
        }
        public boolean attempt(float[] motor){
            float base=trainingFitness(motor,best);
            bestFitness=base;
            float[] candidate=best.clone();
            int mutations=1+rnd.nextInt(4);
            for(int i=0;i<mutations;i++){
                int k=rnd.nextInt(candidate.length);
                candidate[k]=clamp(candidate[k]+(float)rnd.nextGaussian()*spread,-3,3);
            }
            float score=trainingFitness(motor,candidate);
            attempts++;
            if(score>base+1e-3f&&Float.isFinite(score)){
                best=candidate;
                bestFitness=score;
                accepted++;
                if(accepted%8==0)spread=Math.max(.055f,spread*.94f);
                return true;
            }
            if(attempts%200==0)spread=Math.min(.55f,spread*1.08f);
            return false;
        }
    }

    public static void main(String[] args){
        // A movable object cannot jump simply because it was touched.
        UnknownWorld.Entity disk=new UnknownWorld.Entity(38,0,2,2);
        disk.touch(1,0);
        if(disk.vx!=0||disk.vy!=0)throw new IllegalStateException("Scripted impulse remains");
        MotorBrain.Body body=new MotorBrain.Body();
        float moved=collide(body,disk,DT);
        if(moved<=0||disk.x<=38)
            throw new IllegalStateException("Physical collision did not move object");
        // No contact means no object acceleration.
        disk=new UnknownWorld.Entity(300,300,2,2);
        float noContact=collide(body,disk,DT);
        if(noContact!=0||disk.vx!=0||disk.vy!=0)
            throw new IllegalStateException("Object accelerated without contact");
        float[] motor=MotorBrain.virgin(new Random(918273));
        MotorBrain.Trainer motorTrainer=new MotorBrain.Trainer(motor,55131);
        for(int i=0;i<160;i++)motorTrainer.attempt();
        Trainer learner=new Trainer(null);
        float initial=trainingFitness(motorTrainer.best,learner.best);
        for(int i=0;i<70;i++)learner.attempt(motorTrainer.best);
        float trained=trainingFitness(motorTrainer.best,learner.best);
        float heldout=heldout(motorTrainer.best,learner.best);
        System.out.printf(Locale.US,
                "ZERO-003 tests passed: physicalCollision=%.3f attempts=%d accepted=%d baseline=%.3f trained=%.3f heldout=%.3f%n",
                moved,learner.attempts,learner.accepted,initial,trained,heldout);
        if(trained+1e-3f<initial||!Float.isFinite(heldout))
            throw new IllegalStateException("Manipulation optimizer invalid");
    }
}
