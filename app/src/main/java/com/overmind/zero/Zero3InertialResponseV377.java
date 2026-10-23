package com.overmind.zero;

import java.io.*;

/**
 * V377: physically observed change to motion, not displacement from rest.
 * Passive inertial continuation is the baseline. The single GRU3 actor
 * remains the sole movement chooser; this class only reports predictions
 * after independent clean TRAIN and HELDOUT material outcomes.
 */
final class Zero3InertialResponseV377 {
    private static final int DIRS=8, REGIMES=2, N=DIRS*REGIMES, VERSION=377;
    private final float[] meanX=new float[N],meanY=new float[N];
    private final int[] trains=new int[N],validations=new int[N];
    private final double[] loss=new double[N],coastLoss=new double[N];
    private float initialVx,initialVy;
    private int issued=-1;
    private boolean pending,heldout;
    private static float clip(float x,float max){return Math.max(-max,Math.min(max,x));}
    private static int slot(float x,float y,float vx,float vy){
        if(!Float.isFinite(x)||!Float.isFinite(y)||!Float.isFinite(vx)||
           !Float.isFinite(vy)||Math.hypot(x,y)<.1d)
            throw new IllegalArgumentException("V377 invalid real action/velocity");
        int heading=Math.floorMod(Math.round((float)Math.atan2(y,x)/(float)(Math.PI/4)),DIRS);
        return heading+(Math.hypot(vx,vy)>=2d?DIRS:0);
    }
    void begin(float[] perception,float[] action,boolean independent){
        if(pending||perception==null||perception.length!=194||
           action==null||action.length<2)
            throw new IllegalStateException("V377 missing physical PRE");
        // 191/192 are actual COM velocity scaled /130 by original sensor.
        initialVx=perception[191]*130f;initialVy=perception[192]*130f;
        issued=slot(action[0],action[1],initialVx,initialVy);
        heldout=independent;pending=true;
    }
    void finish(float materialDx,float materialDy,float elapsed,boolean independent){
        if(!pending||independent!=heldout||
           !Float.isFinite(materialDx)||!Float.isFinite(materialDy)||
           !Float.isFinite(elapsed)||elapsed<=0||elapsed>.651f)
            throw new IllegalStateException("V377 incomplete motor transition");
        float scale=.30f/elapsed;
        float actualX=clip(materialDx*scale,120f),actualY=clip(materialDy*scale,120f);
        // Measured momentum BEFORE actuation predicts physically passive
        // continuation. Residual is the effect learned from muscle control
        // plus ordinary drag, not imaginary displacement-from-rest reward.
        float responseX=actualX-clip(.30f*initialVx,120f);
        float responseY=actualY-clip(.30f*initialVy,120f);
        int i=issued;
        if(independent){
            if(validations[i]<100000000)validations[i]++;
            double dx=responseX-meanX[i],dy=responseY-meanY[i];
            double err=dx*dx+dy*dy;
            double coast=responseX*responseX+responseY*responseY;
            float a=validations[i]==1?1f:.1f;
            loss[i]+=a*(err-loss[i]);coastLoss[i]+=a*(coast-coastLoss[i]);
        }else{
            float a=trains[i]==0?1f:.1f;
            meanX[i]+=a*(responseX-meanX[i]);
            meanY[i]+=a*(responseY-meanY[i]);
            if(trains[i]<100000000)trains[i]++;
        }
        abort();
    }
    boolean reliable(float[] observation,float[] action){
        if(observation==null||observation.length!=194||action==null||action.length<2)return false;
        int i=slot(action[0],action[1],observation[191]*130f,observation[192]*130f);
        return trains[i]>=4&&validations[i]>=3&&coastLoss[i]>.10d&&
            loss[i]<coastLoss[i]*.90d;
    }
    float[] expected(float[] observation,float[] action){
        if(!reliable(observation,action))return null;
        float vx=observation[191]*130f,vy=observation[192]*130f;
        int i=slot(action[0],action[1],vx,vy);
        // 0.3s physically predicted actual travel after this exact action.
        return new float[]{clip(.30f*vx+meanX[i],120f),
            clip(.30f*vy+meanY[i],120f)};
    }
    /**
     * V383 independent real-material steering authority. A positive causal
     * acceleration along at least TWO different commanded headings must
     * beat inertial coasting on heldout episodes in the body's CURRENT speed
     * regime. Vision/learned hue alone cannot enable wide motor exploration.
     * This only authorizes the existing GRU's wider sampling distribution;
     * no heading or motor impulse is manufactured by the estimator.
     */
    int reliableSteeringDirectionsV383(float[] observation){
        if(observation==null||observation.length!=194)return 0;
        float vx=observation[191]*130f,vy=observation[192]*130f;
        if(!Float.isFinite(vx)||!Float.isFinite(vy))return 0;
        int speedBank=Math.hypot(vx,vy)>=2d?DIRS:0;
        int count=0;
        for(int k=0;k<DIRS;k++){
            int index=k+speedBank;
            if(trains[index]<4||validations[index]<3||
               coastLoss[index]<=.10d||loss[index]>=coastLoss[index]*.90d)
                continue;
            double angle=k*Math.PI/4d;
            double activeForward=meanX[index]*Math.cos(angle)+
                                 meanY[index]*Math.sin(angle);
            // Must actually propel in the commanded direction, not merely
            // predict passive coasting or a statistically predictable brake.
            if(activeForward>.10d)count++;
        }
        return count;
    }
    int reliableBanks(){
        int n=0;for(int i=0;i<N;i++)
            if(trains[i]>=4&&validations[i]>=3&&coastLoss[i]>.10d&&
               loss[i]<coastLoss[i]*.90d)n++;
        return n;
    }
    void abort(){pending=false;issued=-1;}
    void copyTo(Zero3InertialResponseV377 other){
        System.arraycopy(meanX,0,other.meanX,0,N);
        System.arraycopy(meanY,0,other.meanY,0,N);
        System.arraycopy(trains,0,other.trains,0,N);
        System.arraycopy(validations,0,other.validations,0,N);
        System.arraycopy(loss,0,other.loss,0,N);
        System.arraycopy(coastLoss,0,other.coastLoss,0,N);
    }
    void write(DataOutputStream d)throws IOException{
        if(pending)throw new IOException("V377 pending neural choice");
        d.writeInt(VERSION);
        for(int i=0;i<N;i++){
            d.writeInt(trains[i]);d.writeInt(validations[i]);
            d.writeFloat(meanX[i]);d.writeFloat(meanY[i]);
            d.writeDouble(loss[i]);d.writeDouble(coastLoss[i]);
        }
    }
    static Zero3InertialResponseV377 read(DataInputStream d)throws IOException{
        if(d.readInt()!=VERSION)throw new IOException("V377 inertial schema");
        Zero3InertialResponseV377 m=new Zero3InertialResponseV377();
        for(int i=0;i<N;i++){
            m.trains[i]=d.readInt();m.validations[i]=d.readInt();
            m.meanX[i]=d.readFloat();m.meanY[i]=d.readFloat();
            m.loss[i]=d.readDouble();m.coastLoss[i]=d.readDouble();
            if(m.trains[i]<0||m.trains[i]>100000000||
               m.validations[i]<0||m.validations[i]>100000000||
               !Float.isFinite(m.meanX[i])||!Float.isFinite(m.meanY[i])||
               Math.abs(m.meanX[i])>240f||Math.abs(m.meanY[i])>240f||
               !Double.isFinite(m.loss[i])||!Double.isFinite(m.coastLoss[i])||
               m.loss[i]<0||m.coastLoss[i]<0)
                throw new IOException("V377 unphysical motor evidence");
        }
        return m;
    }
}
