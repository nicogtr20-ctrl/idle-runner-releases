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
    // V476: extra LAB-only intermediate-drift bank, NEVER written to V377 saves.
    private static final int LIVE_N=N+DIRS;
    private final float[] meanX=new float[LIVE_N],meanY=new float[LIVE_N];
    private final int[] trains=new int[LIVE_N],validations=new int[LIVE_N];
    private final double[] loss=new double[LIVE_N],coastLoss=new double[LIVE_N];
    private float initialVx,initialVy;
    private int issued=-1;
    private boolean pending,heldout;
    private boolean midDriftRegimeLabV476;
    void enableMidDriftRegimeLabV476(){
        if(pending)throw new IllegalStateException("V476 only between real motor decisions");
        midDriftRegimeLabV476=true;
    }
    private int slotV476(float x,float y,float vx,float vy){
        int original=slot(x,y,vx,vy);
        double speed=Math.hypot(vx,vy);
        // 0.35 is the already measured V441 drift floor, NOT a new motor target.
        // Never reinterpret original low/high saved V377 evidence in place.
        return midDriftRegimeLabV476&&speed>=.35d&&speed<2d?
            N+(original%DIRS):original;
    }
    // V444 transient READ-ONLY counters: accepted CLEAN train/heldout samples.
    // These are never serialized and never participate in actor decisions.
    private long v444AcceptedTrain, v444AcceptedHeldout,
        v444LowTrain, v444LowHeldout, v444HighTrain, v444HighHeldout;
    String motorValidationDiagnosticsV444(){
        int train4=0,heldout3=0,both=0,coastStrong=0,lossPass=0,reliable=0;
        int lowReliable=0,highReliable=0;
        long storedTrain=0,storedHeldout=0;
        for(int i=0;i<N;i++){
            storedTrain+=trains[i];storedHeldout+=validations[i];
            boolean t=trains[i]>=4,v=validations[i]>=3;
            if(t)train4++;if(v)heldout3++;
            if(t&&v){
                both++;
                if(coastLoss[i]>.10d)coastStrong++;
                if(loss[i]<coastLoss[i]*.90d)lossPass++;
                if(coastLoss[i]>.10d&&loss[i]<coastLoss[i]*.90d){
                    reliable++;
                    if(i<DIRS)lowReliable++;else highReliable++;
                }
            }
        }
        // V445 READ-ONLY: per-bank actual action-conditioned forward response,
        // independent heldout loss and coast baseline, all in milli-units.
        // No reliability gate, prediction, training or serialized data changes.
        StringBuilder perBank=new StringBuilder();
        for(int i=0;i<N;i++){
            if(i>0)perBank.append(',');
            double angle=(i%DIRS)*Math.PI/4d;
            double forward=meanX[i]*Math.cos(angle)+meanY[i]*Math.sin(angle);
            perBank.append(i).append(':').append(trains[i]).append('/')
                .append(validations[i]).append(':')
                .append(Math.round(forward*1000d)).append(':')
                .append(Math.round(loss[i]*1000d)).append(':')
                .append(Math.round(coastLoss[i]*1000d));
        }
        int midTrain=0,midHeldout=0,midReliable=0;
        for(int i=N;i<LIVE_N;i++){
            midTrain+=trains[i];midHeldout+=validations[i];
            if(heldoutForwardAuthorityV446(i))midReliable++;
        }
        return "v476MidEnabled="+midDriftRegimeLabV476+
            "|v476MidTrain="+midTrain+"|v476MidHeldout="+midHeldout+
            "|v476MidReliable="+midReliable+"|v444AcceptedTrain="+v444AcceptedTrain+
            "|v444AcceptedHeldout="+v444AcceptedHeldout+
            "|v444LowTrain="+v444LowTrain+
            "|v444LowHeldout="+v444LowHeldout+
            "|v444HighTrain="+v444HighTrain+
            "|v444HighHeldout="+v444HighHeldout+
            "|v444StoredTrain="+storedTrain+
            "|v444StoredHeldout="+storedHeldout+
            "|v444Train4Banks="+train4+
            "|v444Heldout3Banks="+heldout3+
            "|v444BothBanks="+both+
            "|v444CoastStrongBanks="+coastStrong+
            "|v444LossPassBanks="+lossPass+
            "|v444ReliableBanks="+reliable+
            "|v444LowReliable="+lowReliable+
            "|v444HighReliable="+highReliable+
            "|v445BankTrainHeldoutForwardLossCoastMilli="+perBank;
    }
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
        issued=slotV476(action[0],action[1],initialVx,initialVy);
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
            v444AcceptedHeldout++;
            if(i<DIRS)v444LowHeldout++;else v444HighHeldout++;
        }else{
            v444AcceptedTrain++;
            if(i<DIRS)v444LowTrain++;else v444HighTrain++;
        }
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
    /**
     * V446 LAB: A fixed 0.10 squared-error floor discarded all 8 slow-speed
     * banks even when independent heldout trials beat passive coasting and
     * measured propulsion was positive. Require actual forward authority
     * >0.10 material units instead of a large *error variance*. This is
     * still learned from real TRAIN/HELDOUT physical transitions, never a
     * scripted direction or optimistic motor prediction.
     */
    private boolean heldoutForwardAuthorityV446(int i){
        if(trains[i]<4||validations[i]<3||
           !(loss[i]<coastLoss[i]*.90d))return false;
        double angle=(i%DIRS)*Math.PI/4d;
        double forward=meanX[i]*Math.cos(angle)+meanY[i]*Math.sin(angle);
        return forward>.10d;
    }
    boolean reliable(float[] observation,float[] action){
        if(observation==null||observation.length!=194||action==null||action.length<2)return false;
        int i=slotV476(action[0],action[1],observation[191]*130f,observation[192]*130f);
        return heldoutForwardAuthorityV446(i);
    }
    float[] expected(float[] observation,float[] action){
        if(!reliable(observation,action))return null;
        float vx=observation[191]*130f,vy=observation[192]*130f;
        int i=slotV476(action[0],action[1],vx,vy);
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
        double speed=Math.hypot(vx,vy);
        int speedBank=midDriftRegimeLabV476&&speed>=.35d&&speed<2d?
            N:(speed>=2d?DIRS:0);
        int count=0;
        for(int k=0;k<DIRS;k++){
            int index=k+speedBank;
            // Identical measured-forward/heldout evidence as reliable().
            // A predictable brake is never counted as propulsion.
            if(heldoutForwardAuthorityV446(index))count++;
        }
        return count;
    }
    int reliableBanks(){
        int n=0;for(int i=0;i<(midDriftRegimeLabV476?LIVE_N:N);i++)
            if(heldoutForwardAuthorityV446(i))n++;
        return n;
    }
    void abort(){pending=false;issued=-1;}
    void copyTo(Zero3InertialResponseV377 other){
        System.arraycopy(meanX,0,other.meanX,0,LIVE_N);
        System.arraycopy(meanY,0,other.meanY,0,LIVE_N);
        System.arraycopy(trains,0,other.trains,0,LIVE_N);
        System.arraycopy(validations,0,other.validations,0,LIVE_N);
        System.arraycopy(loss,0,other.loss,0,LIVE_N);
        System.arraycopy(coastLoss,0,other.coastLoss,0,LIVE_N);
        other.midDriftRegimeLabV476=midDriftRegimeLabV476;
        other.v444AcceptedTrain=v444AcceptedTrain;
        other.v444AcceptedHeldout=v444AcceptedHeldout;
        other.v444LowTrain=v444LowTrain;
        other.v444LowHeldout=v444LowHeldout;
        other.v444HighTrain=v444HighTrain;
        other.v444HighHeldout=v444HighHeldout;
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
