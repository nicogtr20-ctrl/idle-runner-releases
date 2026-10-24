package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * V384: optional, checkpoint-independent embodied affordance memory.
 * No named objects, hidden world geometry, commanded escape heading, muscle
 * force, new GRU input, or second actor. Learns which OWN attempted headings
 * fail to produce material displacement in a previously touched direction.
 *
 * Touch is measured from actual membrane geometry; commanded extension and
 * measured radial shape are deliberately kept separate. The 194 GRU inputs,
 * V356 CRC, V319 grip and the 170-node physics remain unchanged.
 */
final class Zero384BodyAffordance {
    static final String KEY="zero3_embodied_affordance_v384";
    private static final int BINS=8, WIDTH=BINS*BINS;
    private final int[] trained=new int[WIDTH], failed=new int[WIDTH];
    private final int[] validated=new int[WIDTH], validationFailed=new int[WIDTH];
    private final int[] stretchedWithoutTravel=new int[WIDTH];
    private final float[] touch=new float[BINS], radius=new float[BINS],
            command=new float[BINS], oldTouch=new float[BINS],
            oldRadius=new float[BINS], oldCommand=new float[BINS];
    private boolean pending;
    private float issuedX,issuedY;
    private long completed;
    private static float clip(float v,float lo,float hi){
        return Math.max(lo,Math.min(hi,v));
    }
    private static int direction(float x,float y){
        return Math.floorMod(Math.round((float)Math.atan2(y,x)/(float)(Math.PI/4)),BINS);
    }
    private static float dot(int a,int b){
        return (float)Math.cos((a-b)*Math.PI/4);
    }
    /** The six-sector old muscle readout is NOT an achieved body pose. */
    void perceive(MotorBrain.Body b,Morphology.Surface skin){
        if(b==null||skin==null)throw new IllegalArgumentException("missing physical body");
        float[] sensed=new float[BINS], radial=new float[BINS], efference=new float[BINS];
        float[] count=new float[BINS];
        for(int i=0;i<Morphology.POINTS;i++){
            float x=skin.x[i]-b.cx,y=skin.y[i]-b.cy;
            float rr=(float)Math.hypot(x,y);
            if(!Float.isFinite(rr))continue;
            int k=direction(x,y);
            float contact=clip(skin.touchSense[i],0f,1f);
            sensed[k]+=contact;
            radial[k]+=rr/Morphology.MAX_RADIAL;
            // This is desired extension, NOT radial distance actually reached.
            efference[k]+=clip(skin.extension[i]/Morphology.MAX_REACH,0f,1f);
            count[k]++;
        }
        for(int k=0;k<BINS;k++)if(count[k]>0){
            sensed[k]/=count[k];
            radial[k]/=count[k];
            efference[k]/=count[k];
        }
        measured(sensed,radial,efference);
    }
    /** Package-private measured features also make the observer unit-testable. */
    void measured(float[] sensed,float[] radial,float[] efference){
        if(sensed==null||radial==null||efference==null||
           sensed.length!=BINS||radial.length!=BINS||efference.length!=BINS)
            throw new IllegalArgumentException("bad 8-sector proprioception");
        for(int i=0;i<BINS;i++){
            if(!Float.isFinite(sensed[i])||!Float.isFinite(radial[i])||
               !Float.isFinite(efference[i]))throw new IllegalArgumentException("nonfinite body");
            touch[i]=clip(sensed[i],0,1);
            radius[i]=clip(radial[i],0,2);
            command[i]=clip(efference[i],0,1);
        }
    }
    void begin(float dx,float dy){
        if(pending||!Float.isFinite(dx)||!Float.isFinite(dy)||
           Math.hypot(dx,dy)<.5)throw new IllegalStateException("invalid bodily intention");
        issuedX=dx;issuedY=dy;
        System.arraycopy(touch,0,oldTouch,0,BINS);
        System.arraycopy(radius,0,oldRadius,0,BINS);
        System.arraycopy(command,0,oldCommand,0,BINS);
        pending=true;
    }
    /**
     * POST must be preceded by perceive() of the actual POST body.
     * Physically blocked material is OBSERVABLE even if its collision solver
     * changed center displacement; that correction is not clean motor
     * calibration and never supplies a fictitious positive reward.
     */
    void complete(float dx,float dy,float dt,float muscleImpulse,
                  boolean touchedSolid,boolean heldout){
        if(!pending)return;
        pending=false;completed++;
        if(!Float.isFinite(dx)||!Float.isFinite(dy)||!Float.isFinite(dt)||
           !Float.isFinite(muscleImpulse)||dt<=0f||dt>.66f)return;
        if(!touchedSolid||muscleImpulse<=.5f)return;
        int heading=direction(issuedX,issuedY);
        float forward=(dx*issuedX+dy*issuedY)/dt;
        boolean stalled=forward<.65f;
        for(int side=0;side<BINS;side++){
            // Only a physically sensed surface in the ACTED direction.
            if(Math.max(oldTouch[side],touch[side])<.23f||dot(side,heading)<.70f)continue;
            int idx=side*BINS+heading;
            int[] totals=heldout?validated:trained;
            int[] blocks=heldout?validationFailed:failed;
            if(totals[idx]<1000000)totals[idx]++;
            if(stalled&&blocks[idx]<1000000)blocks[idx]++;
            if(!heldout&&stalled){
                // Body actually changes shape while COM fails to advance.
                float actualChange=Math.abs(radius[side]-oldRadius[side]);
                float requested=Math.max(command[side],oldCommand[side]);
                if((actualChange>.025f||requested>.18f)&&
                   stretchedWithoutTravel[idx]<1000000)stretchedWithoutTravel[idx]++;
            }
        }
    }
    void abort(){pending=false;}
    /** No bias when the body has not LEARNED and independently confirmed failure. */
    private float evidence(int index){
        if(trained[index]<5||validated[index]<2)return 0f;
        float failure=failed[index]/(float)trained[index];
        float heldout=validationFailed[index]/(float)validated[index];
        if(failure<.72f||heldout<.60f)return 0f;
        return clip(Math.min(failure,heldout)*
            Math.min(1f,(trained[index]+validated[index])/14f),0f,1f);
    }
    float learnedCost(float dx,float dy){
        if(!Float.isFinite(dx)||!Float.isFinite(dy)||Math.hypot(dx,dy)<.5)return 0f;
        int heading=direction(dx,dy);
        float cost=0f;
        for(int side=0;side<BINS;side++){
            if(touch[side]<.23f||dot(side,heading)<.70f)continue;
            int idx=side*BINS+heading;
            cost=Math.max(cost,evidence(idx)*touch[side]);
        }
        return -.38f*clip(cost,0,1); // bounded learned feasibility cost, not steering.
    }
    boolean verifiedImpass(){
        for(int side=0;side<BINS;side++){
            if(touch[side]<.23f)continue;
            for(int heading=0;heading<BINS;heading++)
                if(dot(side,heading)>.70f&&evidence(side*BINS+heading)>.28f)
                    return true;
        }
        return false;
    }
    long observedDecisions(){return completed;}
    int confirmedFailures(){
        int n=0;
        for(int k=0;k<WIDTH;k++)if(evidence(k)>0)n++;
        return n;
    }
    /** Optional separate data: invalid sidecar never invalidates V356 brain. */
    String encode(){
        StringBuilder s=new StringBuilder(1152).append("eb384|1|").append(completed);
        for(int i=0;i<WIDTH;i++)s.append('|').append(trained[i]).append(',')
            .append(failed[i]).append(',').append(validated[i]).append(',')
            .append(validationFailed[i]).append(',').append(stretchedWithoutTravel[i]);
        return s.toString();
    }
    boolean restore(String raw){
        if(raw==null||raw.isEmpty())return true;
        try{
            String[] fields=raw.split("\\|",-1);
            if(fields.length!=WIDTH+3||!"eb384".equals(fields[0])||
               !"1".equals(fields[1]))return false;
            long count=Long.parseLong(fields[2]);
            if(count<0||count>100000000)return false;
            int[][] rows=new int[WIDTH][5];
            for(int i=0;i<WIDTH;i++){
                String[] values=fields[i+3].split(",",-1);
                if(values.length!=5)return false;
                for(int j=0;j<5;j++){
                    rows[i][j]=Integer.parseInt(values[j]);
                    if(rows[i][j]<0||rows[i][j]>1000000)return false;
                }
                if(rows[i][1]>rows[i][0]||rows[i][3]>rows[i][2]||
                   rows[i][4]>rows[i][0])return false;
            }
            for(int i=0;i<WIDTH;i++){
                trained[i]=rows[i][0];failed[i]=rows[i][1];
                validated[i]=rows[i][2];validationFailed[i]=rows[i][3];
                stretchedWithoutTravel[i]=rows[i][4];
            }
            completed=count;pending=false;
            return true;
        }catch(Exception invalid){return false;}
    }
}
