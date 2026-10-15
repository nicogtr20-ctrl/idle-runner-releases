package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * OTA162: a measured motor-effector credit channel, NOT a second brain.
 * Receives only the active 2.2 action, living cortex efference, actual world
 * displacement after collisions, and the 16 measured muscle orientations.
 *
 * Local-cortex training answers: "did my muscles physically realize the
 * direction I voluntarily selected?"  It never chooses the action, never
 * sees food coordinates, never alters core value, never rewards core travel,
 * never forces movement, and never replays a scripted gait.
 *
 * Its cancellation metric is the vector cancellation of NOMINAL muscle
 * effort, not a claim that all antagonistic reactions are wasted: footholds
 * require counterforces. The metric is OBSERVATIONAL.
 */
public final class SymbioteCoordinationV162 {
    public static final String KEY="symbiote_coordination_v162";
    private static final int ACTIONS=RecurrentCoreV22.A, SECTORS=SymbioteMatterV1.SECTORS;
    private static final int MOTOR=SECTORS*SymbioteMatterV1.OUTPUTS;
    private static final float LR=.014f;
    private final float[] policyBaseline=new float[ACTIONS];
    private final int[] policySamples=new int[ACTIONS];
    private final float[] ux=new float[SECTORS],uy=new float[SECTORS];
    private int action=-1,steps=0;
    private float dx=0f,dy=0f,gross=0f,cancellationSum=0f;
    private long frames=0,observed=0,trained=0,heldout=0;
    private double cancellationTotal=0,realizationTotal=0;
    private static float clip(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    public void begin(int selected,float[] angles){
        action=selected;steps=0;dx=dy=gross=cancellationSum=0;
        for(int i=0;i<SECTORS;i++){
            float a=(angles!=null&&angles.length==SECTORS&&Float.isFinite(angles[i]))
                ?angles[i]:(i+.5f)*(float)(2*Math.PI/SECTORS);
            ux[i]=(float)Math.cos(a);
            uy[i]=(float)Math.sin(a);
        }
    }
    /** Called exactly once per real, completed material/solid-world frame. */
    public void sample(float beforeX,float beforeY,float afterX,float afterY,
                       float[] output){
        if(action<0||output==null||output.length!=MOTOR||
           !Float.isFinite(beforeX)||!Float.isFinite(beforeY)||
           !Float.isFinite(afterX)||!Float.isFinite(afterY))return;
        float x=afterX-beforeX,y=afterY-beforeY;
        if(!Float.isFinite(x)||!Float.isFinite(y))return;
        // Explicit conservative cap prevents collision corrections/spawns
        // from ever being credited as a giant actuator success.
        float distance=(float)Math.hypot(x,y);
        if(distance>3f)return;
        dx+=x;dy+=y;gross+=distance;
        float sumX=0f,sumY=0f,magnitude=0f;
        for(int k=0;k<SECTORS;k++){
            float radial=clip(output[4*k],-1f,1f)*680f;
            float tangential=clip(output[4*k+1],-1f,1f)*210f;
            float xk=ux[k]*radial-uy[k]*tangential;
            float yk=uy[k]*radial+ux[k]*tangential;
            sumX+=xk;sumY+=yk;
            magnitude+=(float)Math.hypot(xk,yk);
        }
        float cancellation=magnitude>1f?
            clip(1f-(float)Math.hypot(sumX,sumY)/magnitude,0f,1f):0f;
        cancellationSum+=cancellation;
        steps++;
        frames++;
    }
    /**
     * Separate physical credit, no contamination of primary brain learning.
     * The brain's actions 0..7 and 8..15 BOTH contain the same learned
     * heading (action%8); 8..15 add adhesion. Do not discard physical
     * steering experience while the animal chooses to grip. Never alter
     * the grip CHANNEL using directional credit and never reward raw travel.
     * A no-motion interval provides no actuator credit.
     */
    public void consequence(boolean learn,boolean isHeldout,
                            float[] noise,float[] output,float[] localFeatures,
                            float[] regionalWeights,int channels,int nFeatures){
        if(action<0)return;
        if(steps==0){action=-1;return;}
        float cancel=cancellationSum/steps;
        cancellationTotal+=cancel;observed++;
        float angle=action%8*(float)(Math.PI/4);
        float forward=dx*(float)Math.cos(angle)+dy*(float)Math.sin(angle);
        float realization=clip(forward/Math.max(4f,gross),-1f,1f);
        realizationTotal+=realization;
        if(isHeldout)heldout++;
        if(learn&&!isHeldout&&action>=0&&action<ACTIONS&&gross>=.5f&&
            noise!=null&&output!=null&&localFeatures!=null&&regionalWeights!=null&&
            noise.length==MOTOR&&output.length==MOTOR&&
            localFeatures.length==regionalWeights.length&&
            nFeatures*channels*SECTORS==regionalWeights.length){
            float advantage=clip(realization-policyBaseline[action],-1f,1f);
            float lr=LR/((float)Math.sqrt(1+policySamples[action]/1500f));
            // The perturbation at each site gives that site's independent
            // gradient estimate. Previous OTA161 divided each regional
            // gradient by all 16 sectors despite per-site credit being unique.
            // Global forward evidence is causal only in expectation; it is
            // therefore clipped, slowly learned and heldout-checkable.
            for(int i=0;i<MOTOR;i++){
                // No motion-direction adaptation on the grip channel itself.
                if(i%channels==2)continue;
                float drive=clip(output[i],-1f,1f);
                float derivative=1f-drive*drive;
                float change=clip(lr*advantage*clip(noise[i],-.5f,.5f)*
                    derivative,-.005f,.005f);
                int base=i*nFeatures;
                for(int f=0;f<nFeatures;f++)
                    regionalWeights[base+f]=clip(regionalWeights[base+f]+
                        change*localFeatures[base+f],-1.5f,1.5f);
            }
            int count=policySamples[action];
            policyBaseline[action]=clip(policyBaseline[action]+
                (realization-policyBaseline[action])/(Math.min(140,count)+1f),-1f,1f);
            if(count<Integer.MAX_VALUE)policySamples[action]++;
            trained++;
        }
        action=-1;steps=0;dx=dy=gross=cancellationSum=0;
    }
    public void resetLife(){action=-1;steps=0;dx=dy=gross=cancellationSum=0;}
    public void resumeLife(){resetLife();}
    public long frames(){return frames;}
    public long intervals(){return observed;}
    public long trainedIntervals(){return trained;}
    public long heldoutIntervals(){return heldout;}
    public float cancellation(){return observed==0?0f:(float)(cancellationTotal/observed);}
    public float intentRealization(){return observed==0?0f:(float)(realizationTotal/observed);}
    public float actionBaseline(int a){return policyBaseline[a];}
    public int actionSamples(int a){return policySamples[a];}
    /** Atomic separate snapshot; legacy motor saves unchanged. */
    public String encode(){
        StringBuilder b=new StringBuilder("co162|").append(frames).append('|')
            .append(observed).append('|').append(trained).append('|')
            .append(heldout).append('|').append(cancellationTotal).append('|')
            .append(realizationTotal);
        for(int i=0;i<ACTIONS;i++)b.append('|').append(policyBaseline[i])
            .append('|').append(policySamples[i]);
        return b.toString();
    }
    public boolean restore(String text){
        if(text==null||text.length()>9000||!text.startsWith("co162|"))return false;
        try{
            String[] p=text.split("\\|",-1);
            if(p.length!=7+2*ACTIONS)return false;
            long f=Long.parseLong(p[1]),o=Long.parseLong(p[2]),
                t=Long.parseLong(p[3]),h=Long.parseLong(p[4]);
            double c=Double.parseDouble(p[5]),v=Double.parseDouble(p[6]);
            if(f<0||o<0||t<0||h<0||o>f||t>o||h>o||
                !Double.isFinite(c)||!Double.isFinite(v)||
                c<0||c>o+1e-3||Math.abs(v)>o+1e-3)return false;
            float[] b=new float[ACTIONS];int[] n=new int[ACTIONS];
            for(int i=0;i<ACTIONS;i++){
                b[i]=Float.parseFloat(p[7+i*2]);
                n[i]=Integer.parseInt(p[8+i*2]);
                if(!Float.isFinite(b[i])||Math.abs(b[i])>1.001f||n[i]<0||n[i]>t)
                    return false;
            }
            frames=f;observed=o;trained=t;heldout=h;
            cancellationTotal=c;realizationTotal=v;
            System.arraycopy(b,0,policyBaseline,0,ACTIONS);
            System.arraycopy(n,0,policySamples,0,ACTIONS);
            resetLife();return true;
        }catch(RuntimeException invalid){return false;}
    }
}
