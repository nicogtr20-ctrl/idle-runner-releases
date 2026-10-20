package com.overmind.zero;

import java.io.*;

/**
 * Sole-GRU V353 companion: learn REAL material displacement relative to
 * PRE-decision COM velocity. Unlike V371's zero-motion baseline, the null
 * prediction is continued inertial travel over the same measured duration.
 *
 * Eight ACTOR commands x four physical pre-motion regimes. No hard-coded
 * heading, world target, hidden resource type, magical COM thrust, or second
 * motor controller. Train/heldout are disjoint.
 */
final class Zero3InertialMotorV377 {
    static final int MAGIC=377, HEADINGS=8, REGIMES=4, CELLS=HEADINGS*REGIMES;
    static final float STANDARD_SECONDS=.30f;
    private final float[] correctionX=new float[CELLS],correctionY=new float[CELLS];
    private final int[] trained=new int[CELLS],heldout=new int[CELLS];
    private final double[] error=new double[CELLS],coastError=new double[CELLS];
    private int pendingCell=-1;
    private float priorVx,priorVy;
    private boolean pendingHeldout;
    private long validActions,contaminatedActions;
    private static float clamp(float v,float l,float h){return Math.max(l,Math.min(h,v));}
    static int sector(float x,float y){
        if(!Float.isFinite(x)||!Float.isFinite(y)||Math.hypot(x,y)<.1)
            throw new IllegalArgumentException("V377 bad real motor heading");
        return Math.floorMod(Math.round((float)Math.atan2(y,x)/(float)(Math.PI/4)),HEADINGS);
    }
    /** 0 at rest, 1 already travelling that way, 2 inertia opposing,
     * 3 transverse motion. Entirely material, not an actor label. */
    static int regime(float vx,float vy,float dx,float dy){
        if(!Float.isFinite(vx)||!Float.isFinite(vy)||!Float.isFinite(dx)||
           !Float.isFinite(dy))throw new IllegalArgumentException("V377 nonfinite regime");
        float speed=(float)Math.hypot(vx,vy);
        if(speed<.50f)return 0;
        float projection=(vx*dx+vy*dy)/(speed*Math.max(.00001f,(float)Math.hypot(dx,dy)));
        return projection>=.55f?1:projection<=-.55f?2:3;
    }
    private static float readVelocity(float[] x,int index){
        if(x==null||x.length!=194||!Float.isFinite(x[index])||
           Math.abs(x[index])>1.001f)
            throw new IllegalArgumentException("V377 velocity sensor");
        return x[index]*130f; // unchanged genuine physical V356 velocity normalization
    }
    private static int cell(float[] x,float[] a){
        if(a==null||a.length<2)throw new IllegalArgumentException("V377 action");
        return sector(a[0],a[1])*REGIMES+regime(readVelocity(x,191),
            readVelocity(x,192),a[0],a[1]);
    }
    void begin(float[] view194,float[] action,boolean isHeldout){
        if(pendingCell!=-1)throw new IllegalStateException("V377 pending real action");
        pendingCell=cell(view194,action);pendingHeldout=isHeldout;
        priorVx=readVelocity(view194,191);
        priorVy=readVelocity(view194,192);
    }
    /**
     * This measures the observed change in displacement against passive
     * straight-line continuation of the actual velocity at action start.
     * This coast proxy is not a fully isolated drag-controlled experiment,
     * so heldout advantage is necessary (not sufficient) for usable evidence.
     */
    void complete(float actualDx,float actualDy,float elapsed,
                  boolean clean,boolean isHeldout){
        if(pendingCell<0||pendingHeldout!=isHeldout||
           !Float.isFinite(actualDx)||!Float.isFinite(actualDy)||
           !Float.isFinite(elapsed)||elapsed<=0||elapsed>.651f)
            throw new IllegalStateException("V377 impossible completed motor experience");
        int slot=pendingCell;
        if(clean){
            float scale=STANDARD_SECONDS/elapsed;
            float materialChangeX=clamp(actualDx*scale-priorVx*STANDARD_SECONDS,-75f,75f);
            float materialChangeY=clamp(actualDy*scale-priorVy*STANDARD_SECONDS,-75f,75f);
            if(isHeldout){
                heldout[slot]++;
                double err=Math.pow(materialChangeX-correctionX[slot],2)+
                    Math.pow(materialChangeY-correctionY[slot],2);
                double nullErr=Math.pow(materialChangeX,2)+Math.pow(materialChangeY,2);
                double alpha=heldout[slot]==1?1:.1;
                error[slot]+=alpha*(err-error[slot]);
                coastError[slot]+=alpha*(nullErr-coastError[slot]);
            }else{
                float alpha=trained[slot]==0?1f:.08f;
                correctionX[slot]+=alpha*(materialChangeX-correctionX[slot]);
                correctionY[slot]+=alpha*(materialChangeY-correctionY[slot]);
                trained[slot]++;
            }
            validActions++;
        }else contaminatedActions++;
        abort();
    }
    boolean verified(float[] observation,float[] action){
        int i=cell(observation,action);
        return trained[i]>=5&&heldout[i]>=3&&coastError[i]>.09d&&
            error[i]<.80d*coastError[i];
    }
    /** Predict short-term ACTUAL direction of travel, including inertia,
     * not the heading wished by the actor. Only trust qualified heldout cells.
     */
    float opticalScore(float[] observation,float[] action,
                       Zero3ContactValenceV369 learned){
        if(learned==null)throw new IllegalArgumentException("V377 valence");
        if(!verified(observation,action))return .35f*
            learned.utility(observation,action[0],action[1]);
        int i=cell(observation,action);
        float px=clamp(readVelocity(observation,191)*STANDARD_SECONDS+
            correctionX[i],-90,90);
        float py=clamp(readVelocity(observation,192)*STANDARD_SECONDS+
            correctionY[i],-90,90);
        float magnitude=(float)Math.hypot(px,py);
        if(magnitude<.10f)return 0f;
        return learned.utility(observation,px/magnitude,py/magnitude);
    }
    boolean hasVerifiedVisible(float[] observation,Zero3ContactValenceV369 learned){
        for(int heading=0;heading<HEADINGS;heading++){
            float angle=(float)(heading*Math.PI/4);
            float[] a={(float)Math.cos(angle),(float)Math.sin(angle)};
            if(verified(observation,a)&&Math.abs(opticalScore(observation,a,learned))>.01f)
                return true;
        }
        return false;
    }
    int verifiedContexts(){
        int result=0;
        for(int i=0;i<CELLS;i++)if(trained[i]>=5&&heldout[i]>=3&&
            coastError[i]>.09d&&error[i]<.80d*coastError[i])result++;
        return result;
    }
    long validActions(){return validActions;}
    long contaminatedActions(){return contaminatedActions;}
    void abort(){pendingCell=-1;pendingHeldout=false;priorVx=priorVy=0;}
    void copyTo(Zero3InertialMotorV377 other){
        System.arraycopy(correctionX,0,other.correctionX,0,CELLS);
        System.arraycopy(correctionY,0,other.correctionY,0,CELLS);
        System.arraycopy(trained,0,other.trained,0,CELLS);
        System.arraycopy(heldout,0,other.heldout,0,CELLS);
        System.arraycopy(error,0,other.error,0,CELLS);
        System.arraycopy(coastError,0,other.coastError,0,CELLS);
        other.validActions=validActions;other.contaminatedActions=contaminatedActions;
    }
    void write(DataOutputStream d)throws IOException{
        if(pendingCell!=-1)throw new IOException("V377 cannot checkpoint pending action");
        d.writeInt(MAGIC);d.writeLong(validActions);d.writeLong(contaminatedActions);
        for(int i=0;i<CELLS;i++){
            d.writeInt(trained[i]);d.writeInt(heldout[i]);
            d.writeFloat(correctionX[i]);d.writeFloat(correctionY[i]);
            d.writeDouble(error[i]);d.writeDouble(coastError[i]);
        }
    }
    static Zero3InertialMotorV377 read(DataInputStream d)throws IOException{
        if(d.readInt()!=MAGIC)throw new IOException("V377 bad schema");
        Zero3InertialMotorV377 m=new Zero3InertialMotorV377();
        m.validActions=d.readLong();m.contaminatedActions=d.readLong();
        if(m.validActions<0||m.contaminatedActions<0||m.validActions>100000000L||
           m.contaminatedActions>100000000L)throw new IOException("V377 motor counters");
        for(int i=0;i<CELLS;i++){
            m.trained[i]=d.readInt();m.heldout[i]=d.readInt();
            m.correctionX[i]=d.readFloat();m.correctionY[i]=d.readFloat();
            m.error[i]=d.readDouble();m.coastError[i]=d.readDouble();
            if(m.trained[i]<0||m.heldout[i]<0||m.trained[i]>100000000||
               m.heldout[i]>100000000||!Float.isFinite(m.correctionX[i])||
               !Float.isFinite(m.correctionY[i])||Math.abs(m.correctionX[i])>75||
               Math.abs(m.correctionY[i])>75||!Double.isFinite(m.error[i])||
               !Double.isFinite(m.coastError[i])||m.error[i]<0||m.coastError[i]<0)
                throw new IOException("V377 nonphysical learned motor state");
        }
        return m;
    }
}
