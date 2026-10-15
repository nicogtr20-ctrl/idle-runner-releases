package com.overmind.zero;

import java.util.Arrays;

/**
 * ZERO 0123: NONINVASIVE tissue motor-intention vs achieved-shape sensing.
 *
 * Every node has a read-only geometric motor target calculated by Morphology,
 * a physically achieved position after all collisions, and links to its real
 * neighboring tissue. A local resistance can exist while the organism's
 * centroid keeps moving and other nodes are fully mobile.
 *
 * We learn consequences of EXECUTED actions in 16 consecutive body sectors.
 * The full 96-node evidence stays independently addressable. No predefined
 * "release grip", "escape", hand, jaw, box or correct movement is prescribed.
 */
public final class LocalImpedanceSense {
    public static final String KEY="local_impedance_v123";
    public static final int POINTS=Morphology.POINTS,CHANNELS=10,SECTORS=16;
    public static final int FEATURES=POINTS*CHANNELS,SUMMARY=SECTORS*CHANNELS;
    public static final int RADIAL_ERROR=0,TANGENT_ERROR=1,STRETCH=2,
        COMPRESSION=3,RADIAL_SPEED=4,TANGENT_SPEED=5,
        HARD_CONTACT=6,IMPACT=7,HELD=8,MOTOR_DEMAND=9;
    private static final int ACTIONS=SensorimotorMind.ACTIONS,HEAD=4;
    private static final int PRED=ACTIONS*SUMMARY*HEAD, VALUE_STRIDE=SUMMARY+1,
                             VALUES=ACTIONS*VALUE_STRIDE;
    public static final int PARAMETERS=PRED+VALUES;
    private static final float PRED_LR=.009f,VALUE_LR=.008f;
    private final float[] local=new float[FEATURES],sectors=new float[SUMMARY],
        before=new float[SUMMARY],predictor=new float[PRED],values=new float[VALUES];
    private final byte[] resistanceDuration=new byte[POINTS];
    private int pending=-1;
    public long transitions=0,localResistances=0;
    public int validations=0,choiceInfluences=0,activeResistances=0,
               persistentlyRestricted=0;
    public float predictionError=Float.NaN,unchangedError=Float.NaN;
    private static float limit(float x,float lo,float hi){
        return Math.max(lo,Math.min(hi,x));
    }
    /**
     * Reads geometry AFTER existing physics and collision projections.
     * The distance to the real actuator target measures an inability to
     * reach the requested shape, not a privileged knowledge of its cause.
     * Neighbor stretch uses CURRENT and actual muscle-anchor arc lengths,
     * NOT distance from centroid (which misses thin tethered pseudopods).
     */
    public static float[] sense(MotorBrain.Body body,Morphology.Surface tissue){
        if(body==null||tissue==null)throw new IllegalArgumentException("Missing body");
        float[] result=new float[FEATURES];
        // After an old profile or live-scene resume, physical coordinates
        // restore before the NEXT actual actuator step. Never invent targets.
        if(!tissue.hasValidMotorTargets())return result;
        for(int i=0;i<POINTS;i++){
            int prev=(i+POINTS-1)%POINTS,next=(i+1)%POINTS;
            float nx=tissue.sensorNormalX(i),ny=tissue.sensorNormalY(i);
            float targetDx=tissue.targetX(i)-tissue.x[i];
            float targetDy=tissue.targetY(i)-tissue.y[i];
            float radial=targetDx*nx+targetDy*ny;
            float tangent=-targetDx*ny+targetDy*nx;
            float curPrev=(float)Math.hypot(tissue.x[i]-tissue.x[prev],
                                            tissue.y[i]-tissue.y[prev]);
            float curNext=(float)Math.hypot(tissue.x[i]-tissue.x[next],
                                            tissue.y[i]-tissue.y[next]);
            float restPrev=(float)Math.hypot(tissue.sensorAnchorX(i)-tissue.sensorAnchorX(prev),
                                            tissue.sensorAnchorY(i)-tissue.sensorAnchorY(prev));
            float restNext=(float)Math.hypot(tissue.sensorAnchorX(i)-tissue.sensorAnchorX(next),
                                            tissue.sensorAnchorY(i)-tissue.sensorAnchorY(next));
            float delta=((curPrev-restPrev)+(curNext-restNext))*.5f;
            float vx=tissue.vx[i]-body.cvx,vy=tissue.vy[i]-body.cvy;
            int p=i*CHANNELS;
            // For symbiote, target is the physically computed one-step
            // free material position (typically 0-5 units), not an old
            // 80-unit spring extension. The same normalized feature width
            // remains stable; legacy body's scales are unchanged.
            float errorScale=tissue.hasSymbioteMotorTargets()?10f:80f;
            result[p+RADIAL_ERROR]=limit(radial/errorScale,-1,1);
            result[p+TANGENT_ERROR]=limit(tangent/(tissue.hasSymbioteMotorTargets()?10f:65f),-1,1);
            result[p+STRETCH]=limit(Math.max(0,delta)/24f,0,1);
            result[p+COMPRESSION]=limit(Math.max(0,-delta)/14f,0,1);
            result[p+RADIAL_SPEED]=limit((vx*nx+vy*ny)/130f,-1,1);
            result[p+TANGENT_SPEED]=limit((-vx*ny+vy*nx)/130f,-1,1);
            // Separate solid-touch and environmental force: storms may
            // raise touchPulse without manufacturing an actual collision.
            result[p+HARD_CONTACT]=limit(tissue.touchSense[i],0,1);
            result[p+IMPACT]=limit(tissue.touchPulse[i],0,1);
            result[p+HELD]=limit(tissue.gripHold[i]/.5f,0,1);
            float demandedX=tissue.targetX(i)-tissue.sensorAnchorX(i);
            float demandedY=tissue.targetY(i)-tissue.sensorAnchorY(i);
            result[p+MOTOR_DEMAND]=limit(
                (float)Math.hypot(demandedX,demandedY)/
                    (tissue.hasSymbioteMotorTargets()?5f:100f),0,1);
        }
        return result;
    }
    public float node(int i,int ch){
        if(i<0||i>=POINTS||ch<0||ch>=CHANNELS)return 0f;
        return local[i*CHANNELS+ch];
    }
    public float[] localMap(){return local.clone();}
    public float[] summary(){return sectors.clone();}
    private static boolean valid(float[] raw){
        if(raw==null||raw.length!=FEATURES)return false;
        for(float f:raw)if(!Float.isFinite(f)||f< -1.001f||f>1.001f)return false;
        return true;
    }
    /** Read-only stuck evidence: three SEPARATE consecutive samples must
     * exhibit commanded displacement that isn't realized under real contact.
     * We count and expose it but NEVER trigger any hard-coded motor response.
     */
    public void feed(float[] raw){
        if(!valid(raw))return;
        System.arraycopy(raw,0,local,0,FEATURES);
        Arrays.fill(sectors,0);
        activeResistances=persistentlyRestricted=0;
        for(int i=0;i<POINTS;i++){
            int offset=i*CHANNELS,sector=i*SECTORS/POINTS;
            boolean contact=raw[offset+HARD_CONTACT]>.32f;
            float mismatch=(float)Math.hypot(raw[offset+RADIAL_ERROR],
                                               raw[offset+TANGENT_ERROR]);
            float speed=(float)Math.hypot(raw[offset+RADIAL_SPEED],
                                            raw[offset+TANGENT_SPEED]);
            boolean resistance=contact&&mismatch>.22f&&speed<.28f;
            if(resistance){
                activeResistances++;
                if(resistanceDuration[i]<8)resistanceDuration[i]++;
                localResistances++;
            }else if(resistanceDuration[i]>0)resistanceDuration[i]--;
            if(resistanceDuration[i]>=3)persistentlyRestricted++;
            for(int ch=0;ch<CHANNELS;ch++){
                int j=ch*SECTORS+sector;
                float f=raw[offset+ch];
                // Sparse pressure/hold/strain must survive when only one
                // small trailing piece is trapped; mean would erase it.
                if(ch==STRETCH||ch==COMPRESSION||ch==HARD_CONTACT||
                   ch==IMPACT||ch==HELD)
                    sectors[j]=Math.max(sectors[j],f);
                else sectors[j]+=f/(POINTS/SECTORS);
            }
        }
    }
    public void resetLife(){
        Arrays.fill(local,0);Arrays.fill(sectors,0);Arrays.fill(before,0);
        Arrays.fill(resistanceDuration,(byte)0);
        pending=-1;activeResistances=persistentlyRestricted=0;
    }
    public void resumeLife(){
        Arrays.fill(before,0);Arrays.fill(resistanceDuration,(byte)0);
        pending=-1;activeResistances=persistentlyRestricted=0;
    }
    public void chosen(int action){
        if(action<0||action>=ACTIONS)return;
        System.arraycopy(sectors,0,before,0,SUMMARY);pending=action;
    }
    private float predict(float[] state,int action,int j){
        int k=j%SECTORS,off=j-k,at=(action*SUMMARY+j)*HEAD;
        return limit(predictor[at]*state[j]+
            predictor[at+1]*state[off+(k+SECTORS-1)%SECTORS]+
            predictor[at+2]*state[off+(k+1)%SECTORS]+
            predictor[at+3],-1.5f,1.5f);
    }
    private float value(float[] state,int action){
        int base=action*VALUE_STRIDE;
        float out=values[base+SUMMARY];
        for(int j=0;j<SUMMARY;j++)out+=values[base+j]*state[j]/SECTORS;
        return limit(out,-3,3);
    }
    public float confidence(){
        if(validations<32||!Float.isFinite(predictionError)||
           !Float.isFinite(unchangedError)||unchangedError<.025f||
           predictionError>=unchangedError*.95f)return 0;
        return limit((unchangedError-predictionError)/unchangedError,0,.4f);
    }
    /** No built-in penalty for being stuck, no automatic release maneuver.
     * Only earned, action-conditioned valence after independent validation.
     */
    public float choiceBias(int action){
        if(action<0||action>=ACTIONS)return 0;
        return limit(value(sectors,action)*confidence()*.22f,-.08f,.08f);
    }
    public void noteDecision(float peak){if(peak>.008f)choiceInfluences++;}
    public void consequence(float[] next,int action,float reward,float moved,
                            boolean train,boolean heldout){
        if(!valid(next)||action<0||action>=ACTIONS)return;
        if(pending!=action){feed(next);return;}
        feed(next);
        float err=0,base=0;
        for(int j=0;j<SUMMARY;j++){
            float delta=sectors[j]-before[j];
            float residual=delta-predict(before,action,j);
            err+=residual*residual;base+=delta*delta;
            if(train&&!heldout){
                int k=j%SECTORS,off=j-k,idx=(action*SUMMARY+j)*HEAD;
                float g=PRED_LR*limit(residual,-1,1);
                predictor[idx]=limit(predictor[idx]+g*before[j],-2.5f,2.5f);
                predictor[idx+1]=limit(predictor[idx+1]+
                    g*before[off+(k+SECTORS-1)%SECTORS],-2.5f,2.5f);
                predictor[idx+2]=limit(predictor[idx+2]+
                    g*before[off+(k+1)%SECTORS],-2.5f,2.5f);
                predictor[idx+3]=limit(predictor[idx+3]+g,-2.5f,2.5f);
            }
        }
        if(heldout){
            float e=(float)Math.sqrt(err/SUMMARY),b=(float)Math.sqrt(base/SUMMARY);
            float alpha=validations==0?1:.075f;
            predictionError=validations==0?e:predictionError*(1-alpha)+e*alpha;
            unchangedError=validations==0?b:unchangedError*(1-alpha)+b*alpha;
            validations++;
        }
        if(train&&!heldout&&Float.isFinite(reward)&&Float.isFinite(moved)){
            // Real-world experience, never an invented bonus for "unsticking".
            float outcome=limit(reward+.015f*limit(moved,0,12),-2,2);
            float residual=limit(outcome-value(before,action),-1.5f,1.5f);
            int idx=action*VALUE_STRIDE;
            for(int j=0;j<SUMMARY;j++)
                values[idx+j]=limit(values[idx+j]+
                    VALUE_LR*residual*before[j]/SECTORS,-3,3);
            values[idx+SUMMARY]=limit(values[idx+SUMMARY]+
                VALUE_LR*residual,-3,3);
        }
        transitions++;pending=-1;
    }
    public void actualEnergy(float observed){
        if(pending<0||!Float.isFinite(observed)||Math.abs(observed)<.01f)return;
        float outcome=limit(observed/30f,-1,1);
        int idx=pending*VALUE_STRIDE;
        for(int j=0;j<SUMMARY;j++)
            values[idx+j]=limit(values[idx+j]+
                VALUE_LR*.5f*outcome*before[j]/SECTORS,-3,3);
        values[idx+SUMMARY]=limit(values[idx+SUMMARY]+VALUE_LR*.5f*outcome,-3,3);
    }
    public String encode(){
        StringBuilder b=new StringBuilder("li1|").append(transitions).append('|')
            .append(validations).append('|').append(choiceInfluences).append('|')
            .append(localResistances).append('|')
            .append(Float.isFinite(predictionError)?predictionError:-1).append('|')
            .append(Float.isFinite(unchangedError)?unchangedError:-1);
        for(float x:predictor)b.append('|').append(x);
        for(float x:values)b.append('|').append(x);
        return b.toString();
    }
    public boolean restore(String serial){
        if(serial==null||serial.length()>750000||!serial.startsWith("li1|"))return false;
        String[] fields=serial.split("\\|",-1);
        if(fields.length!=7+PARAMETERS)return false;
        try{
            long count=Long.parseLong(fields[1]),resist=Long.parseLong(fields[4]);
            int validation=Integer.parseInt(fields[2]),used=Integer.parseInt(fields[3]);
            float err=Float.parseFloat(fields[5]),base=Float.parseFloat(fields[6]);
            if(count<0||validation<0||validation>count||used<0||resist<0||
               !Float.isFinite(err)||!Float.isFinite(base)||err< -1||base< -1||
               err>5||base>5)return false;
            float[] p=new float[PRED],v=new float[VALUES];
            int k=7;
            for(int i=0;i<PRED;i++){
                p[i]=Float.parseFloat(fields[k++]);
                if(!Float.isFinite(p[i])||Math.abs(p[i])>2.501f)return false;
            }
            for(int i=0;i<VALUES;i++){
                v[i]=Float.parseFloat(fields[k++]);
                if(!Float.isFinite(v[i])||Math.abs(v[i])>3.001f)return false;
            }
            System.arraycopy(p,0,predictor,0,PRED);
            System.arraycopy(v,0,values,0,VALUES);
            transitions=count;validations=validation;
            choiceInfluences=used;localResistances=resist;
            predictionError=err<0?Float.NaN:err;
            unchangedError=base<0?Float.NaN:base;
            resetLife();
            return true;
        }catch(RuntimeException fail){return false;}
    }
}
