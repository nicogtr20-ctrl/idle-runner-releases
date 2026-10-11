package com.overmind.zero;

import java.util.Random;
import java.util.Locale;

/**
 * ZERO-001: a small randomly initialized neural motor controller.
 * No pretrained model, no labelled walking examples and no hard-coded gait.
 *
 * Sensor inputs are local soft-body state, a raw external chemical-gradient
 * direction, and an internal oscillator. Outputs are radial muscle tension
 * and ground adhesion at each perimeter mass. The objective rewards net
 * travel toward a chemical stimulus, not a particular limb arrangement.
 *
 * The network has 62 parameters; a simple evolutionary optimizer changes
 * them using rollouts of the SAME deterministic soft-body physics used
 * in the interactive arena. Objective scores are measurable and repeatable.
 */
public final class MotorBrain {
    public static final int N=10, INPUT=7, HIDDEN=6, PARAMS=INPUT*HIDDEN+HIDDEN+HIDDEN*2+2;
    public static final float R=27f, STEP=1f/40f;
    private MotorBrain(){}

    public static float[] virgin(Random r) {
        float[] w=new float[PARAMS];
        for(int i=0;i<w.length;i++)w[i]=(float)(r.nextGaussian()*.34);
        return w;
    }
    public static float[] copy(float[] w){return w.clone();}
    public static float[] mutated(float[] w,Random r,float scale){
        float[] out=w.clone();
        for(int i=0;i<out.length;i++){
            if(r.nextFloat()<.80f)out[i]=clamp(out[i]+(float)r.nextGaussian()*scale,-4.5f,4.5f);
        }
        return out;
    }
    private static float clamp(float v,float lo,float hi){return Math.min(hi,Math.max(lo,v));}
    private static float dot(float x,float y,float a,float b){return x*a+y*b;}
    static float[] commands(float[] w,float[] sensor){
        float[] h=new float[HIDDEN];
        int k=0;
        for(int j=0;j<HIDDEN;j++){
            float sum=0;
            for(int i=0;i<INPUT;i++)sum+=sensor[i]*w[k++];
            h[j]=tanh(sum+w[INPUT*HIDDEN+j]);
        }
        int outBase=INPUT*HIDDEN+HIDDEN;
        float[] out=new float[2];
        for(int channel=0;channel<2;channel++){
            float s=w[outBase+HIDDEN*2+channel];
            for(int i=0;i<HIDDEN;i++)s+=h[i]*w[outBase+channel*HIDDEN+i];
            out[channel]=tanh(s);
        }
        return out;
    }
    private static float tanh(float v){return (float)Math.tanh(v);}

    /** Soft, self-deforming ring. The ring masses are material particles, NOT limbs. */
    public static final class Body {
        public float cx,cy,cvx,cvy,time,totalWork;
        public final float[] x=new float[N],y=new float[N],vx=new float[N],vy=new float[N],act=new float[N],grip=new float[N];
        final float[] fx=new float[N],fy=new float[N];
        public Body(){reset();}
        public void reset(){
            cx=cy=cvx=cvy=time=totalWork=0;
            for(int i=0;i<N;i++){
                double a=2*Math.PI*i/N;
                x[i]=(float)Math.cos(a)*R;
                y[i]=(float)Math.sin(a)*R;
                vx[i]=vy[i]=act[i]=grip[i]=0;
            }
        }
        public void step(float[] weights,float dirX,float dirY,float dt){
            step(weights,dirX,dirY,dt,null,0,0);
        }
        /**
         * Optional additional learned muscular responses near a movable disk.
         * The motor memory is left unchanged. A separate 18-parameter brain
         * modulates individual particle contraction/grip when a disk is sensed.
         */
        public void step(float[] weights,float dirX,float dirY,float dt,
                         float[] contactWeights,float objectX,float objectY){
            time+=dt;
            for(int i=0;i<N;i++){fx[i]=0;fy[i]=0;}
            float fcx=0,fcy=0;
            for(int i=0;i<N;i++){
                float dx=x[i]-cx,dy=y[i]-cy;
                float dist=(float)Math.sqrt(dx*dx+dy*dy+1e-5f);
                float nx=dx/dist,ny=dy/dist;
                float goalF=dot(nx,ny,dirX,dirY);
                float goalSide=dot(-ny,nx,dirX,dirY);
                float a=(float)(Math.PI*2*i/N);
                float ph=time*5.5f+a;
                float radialVelocity=(vx[i]-cvx)*nx+(vy[i]-cvy)*ny;
                float[] sensors={
                    goalF,goalSide,
                    (float)Math.sin(ph),(float)Math.cos(ph),
                    clamp((dist-R)/R,-2,2),
                    clamp(radialVelocity/150,-2,2),
                    1f
                };
                float[] result=commands(weights,sensors);
                act[i]=result[0];
                grip[i]=(.5f+.5f*result[1]);
                if(contactWeights!=null){
                    float ox=objectX-cx,oy=objectY-cy;
                    float objectDist=(float)Math.hypot(ox,oy);
                    float near=clamp((115-objectDist)/58,0,1);
                    if(near>0){
                        float[] influence=ContactLearning.modulation(contactWeights,
                          nx,ny,ox/Math.max(1,objectDist),oy/Math.max(1,objectDist),
                          objectDist,dist-R,ph,radialVelocity);
                        act[i]=clamp(act[i]+influence[0]*.92f*near,-1,1);
                        grip[i]=clamp(grip[i]+influence[1]*.39f*near,0,1);
                    }
                }
                float length=R+act[i]*17f;
                float stiffness=125f;
                float springForce=(dist-length)*stiffness+radialVelocity*8.5f;
                float sx=-springForce*nx,sy=-springForce*ny;
                fx[i]+=sx;fy[i]+=sy;
                fcx-=sx;fcy-=sy;
                totalWork+=Math.abs(dist-length)*.00018f*dt;
            }
            // Adjacent particle springs keep the material cohesive while allowing
            // its outline to stretch, pinch, split into lobes and form protrusions.
            float neighbor=(float)(2*R*Math.sin(Math.PI/N));
            for(int i=0;i<N;i++){
                int j=(i+1)%N;
                float dx=x[j]-x[i],dy=y[j]-y[i];
                float d=(float)Math.sqrt(dx*dx+dy*dy+1e-5f);
                float rv=((vx[j]-vx[i])*dx+(vy[j]-vy[i])*dy)/d;
                float f=36*(d-neighbor)+rv*3.4f;
                float sx=f*dx/d,sy=f*dy/d;
                fx[i]+=sx;fy[i]+=sy;fx[j]-=sx;fy[j]-=sy;
            }
            // The only permitted forces are elastic forces, muscle contraction
            // and variable adhesion. No scripted displacement is added.
            cvx+=(fcx/3.7f)*dt;
            cvy+=(fcy/3.7f)*dt;
            float drag=(float)Math.exp(-2.0f*dt);
            cvx=clamp(cvx*drag,-280,280);
            cvy=clamp(cvy*drag,-280,280);
            cx+=cvx*dt;cy+=cvy*dt;
            for(int i=0;i<N;i++){
                vx[i]+=fx[i]*dt;
                vy[i]+=fy[i]*dt;
                float adhesion=grip[i]*grip[i]*100;
                float damp=(float)Math.exp(-(2.1f+adhesion)*dt);
                vx[i]=clamp(vx[i]*damp,-280,280);
                vy[i]=clamp(vy[i]*damp,-280,280);
                x[i]+=vx[i]*dt;y[i]+=vy[i]*dt;
                float dx=x[i]-cx,dy=y[i]-cy;
                float d=(float)Math.hypot(dx,dy);
                if(d>88){x[i]=cx+dx*88/d;y[i]=cy+dy*88/d;vx[i]*=.6f;vy[i]*=.6f;}
            }
            // Passive cohesive tissue keeps the nucleus inside its own
            // ten-mass body without altering any learned actuator outputs.
            BodyIntegrity.core(this,dt);
            // Prevent training instability from producing a fake high reward.
            if(!Float.isFinite(cx)||!Float.isFinite(cy)||Math.abs(cx)>10000||Math.abs(cy)>10000)reset();
        }
    }
    /** Measured progress, lateral error, and deformation work, in world units. */
    public static float rollout(float[] w,float angle){
        Body body=new Body();
        float dx=(float)Math.cos(angle),dy=(float)Math.sin(angle);
        for(int j=0;j<240;j++)body.step(w,dx,dy,STEP);
        float forward=body.cx*dx+body.cy*dy;
        float sideways=Math.abs(body.cx*dy-body.cy*dx);
        return forward-.28f*sideways-.22f*body.totalWork;
    }
    /** Train using fixed directions; test on held-out, interpolated directions. */
    public static float fitness(float[] w){
        return (rollout(w,0)+rollout(w,2.094395f)+rollout(w,4.188790f))/3f;
    }
    public static float benchmark(float[] w){
        return (rollout(w,.63f)+rollout(w,2.72f)+rollout(w,4.87f))/3f;
    }
    /** One never-pretrained generation of trial-and-error evolutionary learning. */
    public static final class Trainer {
        final Random random;
        public volatile float[] best;
        public volatile float bestFitness, initialFitness;
        public volatile int attempts,accepted;
        public volatile boolean enabled=true;
        public float mutation=.38f;
        public Trainer(float[] seed,long randomSeed){
            random=new Random(randomSeed);
            best=copy(seed);
            bestFitness=fitness(best);
            initialFitness=bestFitness;
        }
        public boolean attempt(){
            float[] candidate=mutated(best,random,mutation);
            if(random.nextInt(18)==0) candidate=virgin(random);
            float value=fitness(candidate);
            attempts++;
            if(value>bestFitness+0.00001f){
                best=copy(candidate);
                bestFitness=value;
                accepted++;
                if(accepted%10==0)mutation=Math.max(.045f,mutation*.95f);
                return true;
            }
            if(attempts%180==0)mutation=Math.min(.7f,mutation*1.35f);
            return false;
        }
    }
    /** Runnable on standard JVM in CI; verifies physics, randomness, and learning. */
    public static void main(String[] args){
        float[] seed=virgin(new Random(918273));
        Trainer t=new Trainer(seed,55131);
        float b=benchmark(seed);
        for(int i=0;i<180;i++)t.attempt();
        float after=benchmark(t.best);
        System.out.printf(Locale.US,"ZERO-001 selftest: parameters=%d attempts=%d accepted=%d train_before=%.3f train_after=%.3f heldout_before=%.3f heldout_after=%.3f%n",PARAMS,t.attempts,t.accepted,t.initialFitness,t.bestFitness,b,after);
        if(PARAMS>256||!Float.isFinite(t.bestFitness)||t.bestFitness<t.initialFitness||t.attempts!=180||t.accepted<1)
            throw new IllegalStateException("Neural learning / physics smoke test failed");
    }
}
