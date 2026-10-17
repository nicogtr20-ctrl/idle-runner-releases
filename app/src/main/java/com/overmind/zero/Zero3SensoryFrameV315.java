package com.overmind.zero;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ZERO3 V3.15 experimental immutable causal sensory contract (E2).
 *
 * Reads production mechanical/optic sensors without mutating live physics.
 * 96 independent patches, true separate tactile channels, 74 internal
 * particles, true 16 membrane rays and last EXECUTED 64D efference.
 * No object identity, privileged target, physicalLaw or future outcome.
 *
 * NOT yet installed in gameplay, not a trained neural encoder.
 */
public final class Zero3SensoryFrameV315 {
    public static final int SCHEMA=315;
    public static final int PATCHES=Morphology.POINTS;
    public static final int PATCH_CHANNELS=12;
    public static final int RAYS=SpatialPerception.RAYS;
    public static final int EFFERENCE=64;
    public static final int INTERIOR_CHANNELS=FluidInteroceptionV1.CHANNELS;
    public static final int INTERIOR_PARTICLES=FluidInteroceptionV1.PARTICLES;
    public static final int PROPRIO=BodyProprioception.FEATURES;
    public static final String PATCH_SOURCE="Morphology.Surface + DistributedBodySense";
    public static final String RAY_SOURCE="MaterialGeometry.eye + SensorimotorMind.Senses.firstSolidDistance";
    public static final String INNER_SOURCE="FluidInteroceptionV1.sense";
    public static final String ACTUATOR_SOURCE="SymbioteMatterV1.step.executed";
    public static final int REL_X=0,REL_Y=1,REL_VX=2,REL_VY=3,
            TOUCH_SOLID=4,TOUCH_IMPULSE=5,GRIP_HELD=6,GRIP_COMMAND=7,
            EXTENSION=8,STRAIN=9,SHAPE=10,LOCAL_OPTIC=11;

    public final long episodeId,frameId;
    public final double simulationTime;
    public final boolean preAction;
    public final float bodyCenterX,bodyCenterY,bodyVelX,bodyVelY,bodyAngle;
    public final float gelAreaError,gelWork;
    public final boolean energyAvailable=false,damageAvailable=false;
    public final boolean lastExecutedAvailable;
    private final float[] patches;
    private final float[] rays;
    private final float[] proprioception;
    private final float[] interior;
    private final float[] lastExecuted;
    public final long hash;

    private Zero3SensoryFrameV315(long episode,long frame,double seconds,boolean before,
            MotorBrain.Body b,SymbioteMatterV1 gel,float[] local,
            float[] scan,float[] bodyFeatures,float[] inner,float[] last){
        require(episode>0&&frame>=0&&Double.isFinite(seconds)&&seconds>=0,
                "invalid episode/frame/time");
        episodeId=episode;frameId=frame;simulationTime=seconds;preAction=before;
        bodyCenterX=b.cx;bodyCenterY=b.cy;bodyVelX=b.cvx;bodyVelY=b.cvy;
        gelAreaError=gel.areaError;gelWork=gel.work;
        require(local.length==PATCHES*PATCH_CHANNELS &&
                scan.length==RAYS &&
                bodyFeatures.length==PROPRIO &&
                inner.length==INTERIOR_PARTICLES*INTERIOR_CHANNELS,
                "invalid real sensor shape");
        patches=local.clone();rays=scan.clone();proprioception=bodyFeatures.clone();
        interior=inner.clone();
        lastExecutedAvailable=last!=null;
        if(last!=null)require(last.length==EFFERENCE,
                            "last efference must have 64 outputs");
        lastExecuted=last==null?null:last.clone();
        float ax=0,ay=0;
        for(int i=0;i<PATCHES/16;i++){
            ax+=patches[i*PATCH_CHANNELS+REL_X];
            ay+=patches[i*PATCH_CHANNELS+REL_Y];
        }
        bodyAngle=(float)Math.atan2(ay,ax);
        verifyFinite(patches,"local 96x12");
        verifyFinite(rays,"material rays");
        verifyFinite(proprioception,"proprioception");
        verifyFinite(interior,"fluid inner");
        if(last!=null)verifyFinite(lastExecuted,"executed efference");
        require(Float.isFinite(bodyCenterX)&&Float.isFinite(bodyCenterY)&&
                Float.isFinite(bodyVelX)&&Float.isFinite(bodyVelY)&&
                Float.isFinite(gelAreaError)&&Float.isFinite(gelWork),
                "nonfinite body/gel measurement");
        hash=computeHash();
    }
    static void require(boolean yes,String message){
        if(!yes)throw new IllegalArgumentException("ZERO3 SENSOR V315 "+message);
    }
    static void verifyFinite(float[] a,String name){
        for(float v:a)require(Float.isFinite(v),name+" nonfinite");
    }
    /**
     * Capture at actual physics frame boundary.
     * Snapshot is READ ONLY. If values are missing, data is unavailable,
     * NOT an invented zero (energy/damage absent from E2 laboratory).
     */
    public static Zero3SensoryFrameV315 capture(long episode,long frame,double time,
            boolean pre,MotorBrain.Body b,Morphology.Surface skin,
            SymbioteMatterV1 gel,ArrayList<UnknownWorld.Entity> objects,
            float[] lastApplied){
        require(b!=null&&skin!=null&&gel!=null&&objects!=null,
                "real physical sources required");
        float[] patchFeatures=DistributedBodySense.sense(b,skin,null,objects);
        float[] local=new float[PATCHES*PATCH_CHANNELS];
        for(int i=0;i<PATCHES;i++){
            int at=i*PATCH_CHANNELS,base=i*DistributedBodySense.CHANNELS;
            local[at+REL_X]=(skin.x[i]-b.cx)/Morphology.MAX_RADIAL;
            local[at+REL_Y]=(skin.y[i]-b.cy)/Morphology.MAX_RADIAL;
            local[at+REL_VX]=(skin.vx[i]-b.cvx)/130f;
            local[at+REL_VY]=(skin.vy[i]-b.cvy)/130f;
            local[at+TOUCH_SOLID]=skin.touchSense[i];
            local[at+TOUCH_IMPULSE]=skin.touchPulse[i];
            local[at+GRIP_HELD]=skin.gripHold[i];
            local[at+GRIP_COMMAND]=skin.gripCommand[i];
            local[at+EXTENSION]=skin.extension[i]/Morphology.MAX_REACH;
            local[at+STRAIN]=patchFeatures[base+DistributedBodySense.STRAIN];
            local[at+SHAPE]=patchFeatures[base+DistributedBodySense.SHAPE];
            local[at+LOCAL_OPTIC]=patchFeatures[base+DistributedBodySense.OPTIC];
        }
        float[] vision=new float[RAYS];
        for(int r=0;r<RAYS;r++){
            float a=(float)(2.*Math.PI*r/RAYS);
            float dx=(float)Math.cos(a),dy=(float)Math.sin(a);
            float[] eye=MaterialGeometry.eye(skin,b.cx,b.cy,dx,dy);
            float dist=SensorimotorMind.Senses.firstSolidDistance(null,objects,
                eye[0],eye[1],dx,dy,SpatialPerception.RANGE);
            vision[r]=Float.isFinite(dist)?
                Math.max(0f,Math.min(1f,1f-dist/SpatialPerception.RANGE)):0f;
        }
        float[] proprio=BodyProprioception.sense(b,skin);
        float[] fluid=FluidInteroceptionV1.sense(b,skin,gel);
        return new Zero3SensoryFrameV315(episode,frame,time,pre,
                b,gel,local,vision,proprio,fluid,lastApplied);
    }
    public float[] local96x12(){return patches.clone();}
    public float patch(int site,int channel){
        require(site>=0&&site<PATCHES&&channel>=0&&channel<PATCH_CHANNELS,
                "site or channel out of range");
        return patches[site*PATCH_CHANNELS+channel];
    }
    public float[] membraneRays(){return rays.clone();}
    public float[] globalProprioception(){return proprioception.clone();}
    public float[] inner74x7(){return interior.clone();}
    public float[] lastExecuted64(){return lastExecuted==null?null:lastExecuted.clone();}
    public int touchedSites(){
        int n=0;
        for(int i=0;i<PATCHES;i++)
            if(patch(i,TOUCH_SOLID)>.01f)n++;
        return n;
    }
    public int pulsedSites(){
        int n=0;
        for(int i=0;i<PATCHES;i++)
            if(patch(i,TOUCH_IMPULSE)>.001f)n++;
        return n;
    }
    public int heldSites(){
        int n=0;
        for(int i=0;i<PATCHES;i++)
            if(patch(i,GRIP_HELD)>=Morphology.GRIP_HOLD)n++;
        return n;
    }
    public boolean seesObstacle(){
        for(float p:rays)if(p>0.0001f)return true;
        return false;
    }
    public boolean intact(){return hash==computeHash();}
    private static long fold(long hash,long value){
        return (hash^value)*1099511628211L;
    }
    private static long foldArray(long h,float[] a){
        h=fold(h,a==null?-1:a.length);
        if(a!=null)for(float v:a)h=fold(h,Float.floatToIntBits(v));
        return h;
    }
    private long computeHash(){
        long h=1469598103934665603L;
        h=fold(h,SCHEMA);h=fold(h,episodeId);h=fold(h,frameId);
        h=fold(h,Double.doubleToLongBits(simulationTime));
        h=fold(h,preAction?1:0);
        h=fold(h,Float.floatToIntBits(bodyCenterX));
        h=fold(h,Float.floatToIntBits(bodyCenterY));
        h=fold(h,Float.floatToIntBits(bodyVelX));
        h=fold(h,Float.floatToIntBits(bodyVelY));
        h=fold(h,Float.floatToIntBits(bodyAngle));
        h=fold(h,Float.floatToIntBits(gelAreaError));
        h=fold(h,Float.floatToIntBits(gelWork));
        h=fold(h,energyAvailable?1:0);h=fold(h,damageAvailable?1:0);
        h=fold(h,lastExecutedAvailable?1:0);
        h=foldArray(h,patches);h=foldArray(h,rays);
        h=foldArray(h,proprioception);h=foldArray(h,interior);
        return foldArray(h,lastExecuted);
    }
}
