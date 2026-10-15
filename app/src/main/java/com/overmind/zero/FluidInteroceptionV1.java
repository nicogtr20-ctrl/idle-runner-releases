package com.overmind.zero;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Genuine local interoception of all 74 MATERIAL internal particles:
 * 10 old masses are liquid parcels, NOT joints, plus 64 fluid parcels.
 * No food vectors, body goals, reward for locomotion, or simulated sense.
 * Distinct intrinsic channels: packing, approaching pressure proxy, shear,
 * radial/tangential velocity, physical distance to skin, kinetic agitation.
 * Per-parcel evidence remains addressable; 16-sector pooling is the learned
 * decision/motor readout, not a loss of the real 74-source sensor substrate.
 */
public final class FluidInteroceptionV1 {
    public static final String KEY="fluid_interoception_v151";
    public static final int PARTICLES=MotorBrain.N+SymbioteMatterV1.INNER,
        CHANNELS=7,SECTORS=16,FEATURES=PARTICLES*CHANNELS,SUMMARY=SECTORS*CHANNELS;
    public static final int DENSITY=0,COMPRESSION=1,SHEAR=2,RADIAL_V=3,
        TANGENTIAL_V=4,BOUNDARY_GAP=5,AGITATION=6;
    private static final int A=SensorimotorMind.ACTIONS,HEAD=3,PRED=A*SUMMARY*HEAD,
        VAL=A*(SUMMARY+1);
    /** Fixed number of learned prediction/value parameters, not growth. */
    public static final int PARAMETERS=PRED+VAL;
    private final float[] local=new float[FEATURES],pooled=new float[SUMMARY],
        before=new float[SUMMARY],predictor=new float[PRED],values=new float[VAL];
    private int pending=-1;
    public long transitions=0;
    public int validations=0,choiceInfluences=0;
    public float predictionError=Float.NaN,unchangedError=Float.NaN;
    private static float clip(float x,float lo,float hi){return Math.max(lo,Math.min(hi,x));}

    /** Derive mechanosensory signals from the ACTUAL material scene.
     * Invoked once per 0.65s decision, not once per subframe at TEST MAX.
     * Pressure is a KINEMATIC APPROACH/packing proxy, not fictional Pascals.
     */
    public static float[] sense(MotorBrain.Body body,Morphology.Surface skin,
                                SymbioteMatterV1 liquid){
        float[] result=new float[FEATURES];
        if(body==null||skin==null||liquid==null)return result;
        final int n=PARTICLES;
        float[] px=new float[n],py=new float[n],ux=new float[n],uy=new float[n];
        for(int i=0;i<n;i++){
            if(i<MotorBrain.N){px[i]=body.x[i];py[i]=body.y[i];
                ux[i]=body.vx[i];uy[i]=body.vy[i];}
            else{int j=i-MotorBrain.N;px[i]=liquid.x[j];py[i]=liquid.y[j];
                ux[i]=liquid.vx[j];uy[i]=liquid.vy[j];}
        }
        for(int i=0;i<n;i++){
            float dx=px[i]-body.cx,dy=py[i]-body.cy;
            float radius=(float)Math.hypot(dx,dy);
            float nx=radius>.001f?dx/radius:0f,ny=radius>.001f?dy/radius:0f;
            float vx=ux[i]-body.cvx,vy=uy[i]-body.cvy;
            int nearby=0;float approach=0f,shear=0f;
            for(int j=0;j<n;j++){
                if(i==j)continue;
                float x=px[j]-px[i],y=py[j]-py[i];
                float d2=x*x+y*y;
                if(d2>=144f)continue;
                // Coincident parcels still count as real local crowding.
                // Their direction is undefined, so do not invent a normal
                // for compression/shear, but NEVER pretend density is empty.
                nearby++;
                if(d2<.000001f)continue;
                float d=(float)Math.sqrt(d2);
                float dvx=ux[j]-ux[i],dvy=uy[j]-uy[i];
                approach+=Math.max(0f,-(dvx*x+dvy*y)/d)/75f;
                shear+=Math.abs(dvx*y-dvy*x)/(d*100f);
            }
            float gap=Float.POSITIVE_INFINITY;
            for(int k=0;k<Morphology.POINTS;k++){
                int nxt=(k+1)%Morphology.POINTS;
                float ex=skin.x[nxt]-skin.x[k],ey=skin.y[nxt]-skin.y[k];
                float wx=px[i]-skin.x[k],wy=py[i]-skin.y[k];
                float t=clip((wx*ex+wy*ey)/Math.max(1e-5f,ex*ex+ey*ey),0,1);
                float bx=wx-ex*t,by=wy-ey*t;
                gap=Math.min(gap,(float)Math.hypot(bx,by));
            }
            int off=i*CHANNELS;
            result[off+DENSITY]=clip(nearby/18f,0,1);
            result[off+COMPRESSION]=clip(approach/Math.max(1,nearby),0,1);
            result[off+SHEAR]=clip(shear/Math.max(1,nearby),0,1);
            result[off+RADIAL_V]=clip((vx*nx+vy*ny)/140f,-1,1);
            result[off+TANGENTIAL_V]=clip((-vx*ny+vy*nx)/140f,-1,1);
            result[off+BOUNDARY_GAP]=clip(gap/35f,0,1);
            result[off+AGITATION]=clip((float)Math.hypot(vx,vy)/180f,0,1);
        }
        return result;
    }
    public float[] localMap(){return local.clone();}
    public float[] summary(){return pooled.clone();}
    private static boolean valid(float[] a,int n){
        if(a==null||a.length!=n)return false;
        for(float f:a)if(!Float.isFinite(f)||Math.abs(f)>1.001f)return false;
        return true;
    }
    /** Each material parcel contributes once to its current physical sector.
     * Empty sectors remain 0, not a fabricated sensory value. */
    public void feed(float[] reading,MotorBrain.Body b,SymbioteMatterV1 matter){
        feed(reading,b,matter,null);
    }
    /** Pool parcel data by its nearest REAL tissue sector (6 skin nodes),
     * never by a world-fixed azimuth unrelated to muscle IDs. */
    public void feed(float[] reading,MotorBrain.Body b,SymbioteMatterV1 matter,
                     Morphology.Surface skin){
        if(!valid(reading,FEATURES)||b==null||matter==null)return;
        System.arraycopy(reading,0,local,0,FEATURES);
        Arrays.fill(pooled,0f);
        int[] n=new int[SECTORS];
        for(int i=0;i<PARTICLES;i++){
            float x=i<MotorBrain.N?b.x[i]:matter.x[i-MotorBrain.N];
            float y=i<MotorBrain.N?b.y[i]:matter.y[i-MotorBrain.N];
            int sector=0;
            if(skin!=null){
                float best=Float.POSITIVE_INFINITY;
                for(int k=0;k<Morphology.POINTS;k++){
                    int next=(k+1)%Morphology.POINTS;
                    float ex=skin.x[next]-skin.x[k],ey=skin.y[next]-skin.y[k];
                    float dx=x-skin.x[k],dy=y-skin.y[k];
                    float length2=ex*ex+ey*ey;
                    float t=length2>1e-6f?clip((dx*ex+dy*ey)/length2,0f,1f):0f;
                    float fx=dx-ex*t,fy=dy-ey*t,d2=fx*fx+fy*fy;
                    // Stable tie-breaking by physical material index:
                    // nearly equidistant segments around the liquid centre
                    // must not remap during a rigid world rotation.
                    if(Float.isFinite(d2)&&d2<best-1e-3f){
                        best=d2;
                        sector=k/(Morphology.POINTS/SECTORS);
                    }
                }
            }else{
                sector=((int)Math.floor((Math.atan2(y-b.cy,x-b.cx)+Math.PI*2)
                                   /(Math.PI*2/SECTORS)))%SECTORS;
                if(sector<0)sector+=SECTORS;
            }
            n[sector]++;
            for(int ch=0;ch<CHANNELS;ch++)
                pooled[ch*SECTORS+sector]+=reading[i*CHANNELS+ch];
        }
        for(int k=0;k<SECTORS;k++)if(n[k]>0)
            for(int ch=0;ch<CHANNELS;ch++)
                pooled[ch*SECTORS+k]/=n[k];
    }
    public void resetLife(){Arrays.fill(local,0);Arrays.fill(pooled,0);
        Arrays.fill(before,0);pending=-1;}
    public void resumeLife(){pending=-1;Arrays.fill(before,0);}
    public void chosen(int action){
        if(action<0||action>=A)return;
        pending=action;System.arraycopy(pooled,0,before,0,SUMMARY);
    }
    private float expected(float[] s,int action,int j){
        int k=j%SECTORS,off=(action*SUMMARY+j)*HEAD;
        float pred=predictor[off]*s[j]+
            predictor[off+1]*s[j-j%SECTORS+(k+1)%SECTORS]+predictor[off+2];
        return clip(pred,-1.5f,1.5f);
    }
    private float value(float[] s,int action){
        int off=action*(SUMMARY+1);
        float v=values[off+SUMMARY];
        for(int j=0;j<SUMMARY;j++)v+=values[off+j]*s[j]/SECTORS;
        return clip(v,-3f,3f);
    }
    public float confidence(){
        if(validations<32||!Float.isFinite(predictionError)||
           !Float.isFinite(unchangedError)||unchangedError<.014f||
           predictionError>=unchangedError*.96f)return 0;
        return clip((unchangedError-predictionError)/unchangedError,0,.45f);
    }
    /** THIS affects the sole recurrent action scores, after heldout trust. */
    public float choiceBias(int action){
        if(action<0||action>=A)return 0;
        return clip(value(pooled,action)*confidence()*.24f,-.12f,.12f);
    }
    public void noteDecision(float magnitude){if(magnitude>.008f)choiceInfluences++;}
    public void consequence(float[] next,MotorBrain.Body body,SymbioteMatterV1 matter,
        int action,float realEnergy,float realHarm,boolean train,boolean heldout){
        consequence(next,body,matter,null,action,realEnergy,realHarm,train,heldout);
    }
    public void consequence(float[] next,MotorBrain.Body body,SymbioteMatterV1 matter,
        Morphology.Surface skin,int action,float realEnergy,float realHarm,
        boolean train,boolean heldout){
        if(!valid(next,FEATURES)||body==null||matter==null)return;
        if(pending!=action){feed(next,body,matter,skin);return;}
        float[] prior=before.clone();
        feed(next,body,matter,skin);
        float e=0,baseline=0;
        for(int j=0;j<SUMMARY;j++){
            float change=pooled[j]-prior[j],diff=change-expected(prior,action,j);
            e+=diff*diff;baseline+=change*change;
            if(train&&!heldout){
                int k=j%SECTORS,off=(action*SUMMARY+j)*HEAD;
                float g=.009f*clip(diff,-1,1);
                predictor[off]=clip(predictor[off]+g*prior[j],-2.5f,2.5f);
                predictor[off+1]=clip(predictor[off+1]+
                    g*prior[j-k+(k+1)%SECTORS],-2.5f,2.5f);
                predictor[off+2]=clip(predictor[off+2]+g,-2.5f,2.5f);
            }
        }
        if(heldout){
            float mse=(float)Math.sqrt(e/SUMMARY),base=(float)Math.sqrt(baseline/SUMMARY);
            predictionError=validations==0?mse:.92f*predictionError+.08f*mse;
            unchangedError=validations==0?base:.92f*unchangedError+.08f*base;
            validations++;
        }
        // Only actual external energy or injury gives internal tissue
        // value. Not translation distance, a motion proxy or novel sensations.
        if(train&&!heldout&&Float.isFinite(realEnergy)&&
           Float.isFinite(realHarm)&&realHarm>=0f){
            float reward=clip(realEnergy/22f-realHarm/70f,-2,2);
            float delta=clip(reward-value(prior,action),-1.5f,1.5f);
            int off=action*(SUMMARY+1);
            for(int j=0;j<SUMMARY;j++)
                values[off+j]=clip(values[off+j]+.012f*delta*prior[j]/SECTORS,-3,3);
            values[off+SUMMARY]=clip(values[off+SUMMARY]+.012f*delta,-3,3);
        }
        transitions++;pending=-1;
    }
    public void actualEnergy(float energy){
        if(pending<0||!Float.isFinite(energy)||Math.abs(energy)<.01f)return;
        float delta=clip(energy/30f,-1,1);int off=pending*(SUMMARY+1);
        values[off+SUMMARY]=clip(values[off+SUMMARY]+.006f*delta,-3,3);
    }
    public String encode(){try{
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(bytes);
        d.writeInt(0x464c4931);d.writeLong(transitions);d.writeInt(validations);
        d.writeInt(choiceInfluences);
        d.writeFloat(Float.isFinite(predictionError)?predictionError:-1);
        d.writeFloat(Float.isFinite(unchangedError)?unchangedError:-1);
        for(float[] list:new float[][]{predictor,values})
            for(float f:list)d.writeFloat(f);
        d.flush();byte[] plain=bytes.toByteArray();
        CRC32 crc=new CRC32();crc.update(plain);
        ByteArrayOutputStream compressed=new ByteArrayOutputStream();
        DeflaterOutputStream zip=new DeflaterOutputStream(compressed);zip.write(plain);zip.close();
        return "fli1|"+crc.getValue()+"|"+Base64CodecV22.encode(compressed.toByteArray());
    }catch(IOException e){throw new IllegalStateException(e);}}
    public boolean restore(String saved){
        if(saved==null||saved.length()>250000||!saved.startsWith("fli1|"))return false;
        try{
            String[] parts=saved.split("\\|",-1);if(parts.length!=3)return false;
            InflaterInputStream zin=new InflaterInputStream(
                new ByteArrayInputStream(Base64CodecV22.decode(parts[2])));
            ByteArrayOutputStream raw=new ByteArrayOutputStream();byte[] buf=new byte[2048];
            int n;while((n=zin.read(buf))!=-1){
                if(raw.size()+n>65000)return false;
                raw.write(buf,0,n);
            }zin.close();
            byte[] b=raw.toByteArray();CRC32 crc=new CRC32();crc.update(b);
            if(crc.getValue()!=Long.parseLong(parts[1]))return false;
            DataInputStream in=new DataInputStream(new ByteArrayInputStream(b));
            if(in.readInt()!=0x464c4931)return false;
            long t=in.readLong();int v=in.readInt(),used=in.readInt();
            float error=in.readFloat(),base=in.readFloat();
            if(t<0||t>1000000000000L||v<0||v>t||used<0||
               !Float.isFinite(error)||error< -1||error>5||
               !Float.isFinite(base)||base< -1||base>5)return false;
            float[] p=new float[PRED],q=new float[VAL];
            for(float[] list:new float[][]{p,q})for(int i=0;i<list.length;i++){
                float z=in.readFloat();
                if(!Float.isFinite(z)||Math.abs(z)>3.001f)return false;
                list[i]=z;
            }
            if(in.available()!=0)return false;
            System.arraycopy(p,0,predictor,0,PRED);
            System.arraycopy(q,0,values,0,VAL);
            transitions=t;validations=v;choiceInfluences=used;
            predictionError=error<0?Float.NaN:error;
            unchangedError=base<0?Float.NaN:base;
            resumeLife();return true;
        }catch(IOException|RuntimeException e){return false;}
    }
}
