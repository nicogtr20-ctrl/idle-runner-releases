package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * Non-semantic body-centered sensing from the ACTUAL deformable membrane.
 * Eight world-aligned angular sectors, four mechanical channels each:
 * tactile pressure, sustained physical grip, extension, radial surface speed.
 *
 * No privileged entity category, target coordinate, doorway location,
 * simulated "correct" action or encoded winning maneuver enters the network.
 * The original 26-dim SensorimotorMind and all its trained weights remain
 * unchanged. The new advice is gated by out-of-training prediction accuracy.
 */
public final class BodyProprioception {
    public static final String KEY="body_proprioception_v120";
    public static final int SECTORS=8,CHANNELS=6,FEATURES=SECTORS*CHANNELS,ACTIONS=SensorimotorMind.ACTIONS;
    // A predicted feature depends on the other mechanical channels at that
    // same surface sector, not on a label or a predefined object geometry.
    private static final int HEAD=CHANNELS+1;
    private static final int PRED_WEIGHTS=ACTIONS*FEATURES*HEAD;
    private static final int VALUE_WIDTH=FEATURES+1;
    private static final int VALUE_WEIGHTS=ACTIONS*VALUE_WIDTH;
    public static final int PARAMETERS=PRED_WEIGHTS+VALUE_WEIGHTS;
    private static final float PRED_LR=.014f,VALUE_LR=.030f;
    private static float clamp(float x,float lo,float hi){return Math.max(lo,Math.min(hi,x));}

    private final float[] prediction=new float[PRED_WEIGHTS];
    private final float[] values=new float[VALUE_WEIGHTS];
    private final float[] current=new float[FEATURES],before=new float[FEATURES];
    private int lastAction=-1;
    public long transitions=0;
    public int validations=0,choiceInfluences=0,contactObservations=0;
    public float predictionError=Float.NaN,unchangedError=Float.NaN;

    /** Physical world axes, consistent with eight proximity rays (y goes down).
     * Index 0=right, 2=down, 4=left, 6=up. Body center follows the creature. */
    private static int sector(float x,float y){
        double angle=Math.atan2(y,x);
        return ((int)Math.floor((angle+Math.PI/8)/(Math.PI/4))+8)%8;
    }
    private static boolean valid(float[] a){
        if(a==null||a.length!=FEATURES)return false;
        for(float v:a)if(!Float.isFinite(v)||v< -1.01f||v>1.01f)return false;
        return true;
    }
    /** Read genuine membrane masses after physics, not a collision/game event.
     * Re-allocating the 32-float sample only on a new decision (~0.65s of
     * SIMULATION time) avoids scanning the 96 masses on every x20 subframe. */
    public static float[] sense(MotorBrain.Body body,Morphology.Surface skin){
        if(body==null||skin==null)throw new IllegalArgumentException("body");
        float[] v=new float[FEATURES],count=new float[SECTORS];
        float[] near=new float[SECTORS];
        java.util.Arrays.fill(near,Float.POSITIVE_INFINITY);
        for(int i=0;i<Morphology.POINTS;i++){
            float dx=skin.x[i]-body.cx,dy=skin.y[i]-body.cy;
            if(!Float.isFinite(dx)||!Float.isFinite(dy))continue;
            int k=sector(dx,dy);
            count[k]++;
            v[k]+=clamp(Math.max(skin.touchSense[i],skin.touchPulse[i]),0,1);
            v[SECTORS+k]+=clamp(skin.gripHold[i]/.5f,0,1);
            // Deliberate commanded motor EFFERENCE, not an assertion of
            // achieved extension; preserve learned weight semantics. Physical
            // radius is measured independently in channels 4/5 below.
            v[2*SECTORS+k]+=clamp(skin.extension[i]/110f,0,1);
            float radius=(float)Math.hypot(dx,dy);
            near[k]=Math.min(near[k],radius);
            v[4*SECTORS+k]=Math.max(v[4*SECTORS+k],clamp(radius/155f,0,1));
            float radial=radius>.01f?
                ((skin.vx[i]-body.cvx)*dx+(skin.vy[i]-body.cvy)*dy)/(radius*130f):0;
            v[3*SECTORS+k]+=clamp(radial,-1,1);
        }
        for(int k=0;k<SECTORS;k++){
            if(count[k]==0)continue;
            for(int channel=0;channel<4;channel++)
                v[channel*SECTORS+k]=clamp(v[channel*SECTORS+k]/count[k],-1,1);
            v[5*SECTORS+k]=clamp(near[k]/155f,0,1);
        }
        return v;
    }
    public float[] sensed(){return current.clone();}
    public void feed(float[] senses){
        if(valid(senses))System.arraycopy(senses,0,current,0,FEATURES);
    }
    public void resetLife(){
        Arrays.fill(current,0);Arrays.fill(before,0);lastAction=-1;
    }
    public void resumeLife(){
        // Keep learned readouts; no fabricated history across app interruptions.
        lastAction=-1;Arrays.fill(before,0);
    }
    public void chosen(int action){
        if(action<0||action>=ACTIONS)return;
        lastAction=action;
        System.arraycopy(current,0,before,0,FEATURES);
    }
    private float predictedDelta(float[] context,int action,int feature){
        int base=(action*FEATURES+feature)*HEAD;
        int k=feature%SECTORS;
        float v=prediction[base+CHANNELS];
        for(int c=0;c<CHANNELS;c++)v+=prediction[base+c]*context[c*SECTORS+k];
        return clamp(v,-1.5f,1.5f);
    }
    private float value(float[] context,int action){
        int base=action*VALUE_WIDTH;
        float v=values[base+FEATURES];
        for(int j=0;j<FEATURES;j++)v+=values[base+j]*context[j];
        return clamp(v,-3,3);
    }
    public float confidence(){
        if(validations<30||!Float.isFinite(predictionError)||
           !Float.isFinite(unchangedError)||unchangedError<.016f||
           predictionError>=unchangedError*.96f)return 0f;
        return clamp((unchangedError-predictionError)/unchangedError,0,.5f);
    }
    public float choiceBias(int action){
        if(action<0||action>=ACTIONS)return 0;
        float c=confidence();
        if(c==0)return 0;
        return clamp(value(current,action)*c*.32f,-.23f,.23f);
    }
    public void noteDecision(float maximumAbsBias){
        if(maximumAbsBias>.008f)choiceInfluences++;
    }
    /** Only one actually executed action's before/after membrane feedback.
     * Validation transitions are never used to update prediction/value weights.
     * Positive external motion is generic and physically measured: it can
     * reinforce exploration, but does not encode the door as a target. */
    public void consequence(float[] after,int action,float livedReward,
                            float externalMotion,boolean learning,boolean heldout){
        if(!valid(after)||action<0||action>=ACTIONS)return;
        if(lastAction!=action){feed(after);return;}
        float error=0,naive=0;
        for(int j=0;j<FEATURES;j++){
            float actual=after[j]-before[j];
            float residual=actual-predictedDelta(before,action,j);
            error+=residual*residual;naive+=actual*actual;
            if(learning&&!heldout){
                int base=(action*FEATURES+j)*HEAD,k=j%SECTORS;
                float g=PRED_LR*clamp(residual,-1,1);
                for(int c=0;c<CHANNELS;c++)
                    prediction[base+c]=clamp(prediction[base+c]+g*
                        before[c*SECTORS+k],-2.5f,2.5f);
                prediction[base+CHANNELS]=clamp(prediction[base+CHANNELS]+g,-2.5f,2.5f);
            }
        }
        float rmse=(float)Math.sqrt(error/FEATURES);
        float baseline=(float)Math.sqrt(naive/FEATURES);
        if(heldout){
            validations++;
            float mix=validations==1?1f:.09f;
            predictionError=validations==1?rmse:predictionError*(1-mix)+rmse*mix;
            unchangedError=validations==1?baseline:unchangedError*(1-mix)+baseline*mix;
        }
        if(learning&&!heldout&&Float.isFinite(livedReward)&&Float.isFinite(externalMotion)){
            float reward=clamp(livedReward+.025f*Math.max(0,Math.min(12,externalMotion)),-2,2);
            float td=clamp(reward-value(before,action),-1.5f,1.5f);
            int base=action*VALUE_WIDTH;
            for(int j=0;j<FEATURES;j++)
                values[base+j]=clamp(values[base+j]+VALUE_LR*td*before[j],-3,3);
            values[base+FEATURES]=clamp(values[base+FEATURES]+VALUE_LR*td,-3,3);
        }
        for(int j=0;j<SECTORS;j++)if(after[j]>.03f){contactObservations++;break;}
        transitions++;
        feed(after);
        lastAction=-1;
    }
    public void actualEnergy(float gained){
        if(lastAction<0||!Float.isFinite(gained)||Math.abs(gained)<.01f)return;
        float impact=clamp(gained/30f,-1,1);
        int base=lastAction*VALUE_WIDTH;
        float rate=VALUE_LR*.55f;
        for(int j=0;j<FEATURES;j++)
            values[base+j]=clamp(values[base+j]+rate*impact*before[j],-3,3);
        values[base+FEATURES]=clamp(values[base+FEATURES]+rate*impact,-3,3);
    }
    public int parameterCount(){return PARAMETERS;}
    public String encode(){
        StringBuilder b=new StringBuilder("bp1|").append(transitions).append('|')
            .append(validations).append('|').append(choiceInfluences).append('|')
            .append(contactObservations).append('|')
            .append(Float.isFinite(predictionError)?predictionError:-1f).append('|')
            .append(Float.isFinite(unchangedError)?unchangedError:-1f);
        for(float x:current)b.append('|').append(x);
        for(float x:prediction)b.append('|').append(x);
        for(float x:values)b.append('|').append(x);
        return b.toString();
    }
    public boolean restore(String raw){
        if(raw==null||raw.length()>350000||!raw.startsWith("bp1|"))return false;
        String[] a=raw.split("\\|",-1);
        if(a.length!=7+FEATURES+PARAMETERS)return false;
        try{
            long trans=Long.parseLong(a[1]);
            int n=Integer.parseInt(a[2]),used=Integer.parseInt(a[3]),
                contact=Integer.parseInt(a[4]);
            float ep=Float.parseFloat(a[5]),eb=Float.parseFloat(a[6]);
            if(trans<0||n<0||used<0||contact<0||n>trans||
               !Float.isFinite(ep)||!Float.isFinite(eb)||ep< -1||eb< -1||
               ep>5||eb>5)return false;
            float[] sensed=new float[FEATURES],p=new float[PRED_WEIGHTS],
                    v=new float[VALUE_WEIGHTS];
            int ix=7;
            for(int k=0;k<sensed.length;k++)sensed[k]=Float.parseFloat(a[ix++]);
            if(!valid(sensed))return false;
            for(int k=0;k<p.length;k++){
                p[k]=Float.parseFloat(a[ix++]);
                if(!Float.isFinite(p[k])||Math.abs(p[k])>2.51f)return false;
            }
            for(int k=0;k<v.length;k++){
                v[k]=Float.parseFloat(a[ix++]);
                if(!Float.isFinite(v[k])||Math.abs(v[k])>3.01f)return false;
            }
            System.arraycopy(sensed,0,current,0,FEATURES);
            System.arraycopy(p,0,prediction,0,p.length);
            System.arraycopy(v,0,values,0,v.length);
            transitions=trans;validations=n;choiceInfluences=used;
            contactObservations=contact;predictionError=ep<0?Float.NaN:ep;
            unchangedError=eb<0?Float.NaN:eb;
            resumeLife();return true;
        }catch(RuntimeException e){return false;}
    }
    private static void must(boolean ok,String message){
        if(!ok)throw new AssertionError(message);
    }
    public static void main(String[] args){
        MotorBrain.Body body=new MotorBrain.Body();
        Morphology.Surface skin=new Morphology.Surface(body);
        float[] blank=sense(body,skin);
        must(blank.length==48,"Wrong proprioceptive field width");
        int near=0,opposite=48;
        skin.x[near]=body.cx+100;skin.y[near]=body.cy;
        skin.x[opposite]=body.cx-100;skin.y[opposite]=body.cy;
        skin.touchPulse[near]=1f;
        skin.gripHold[near]=.5f;skin.extension[near]=60f;
        float[] read=sense(body,skin);
        must(read[0]>blank[0]&&read[8]>0&&read[16]>0&&
             read[32]>blank[32]&&read[40]<read[32],
            "Right-side pressure, holding and stretch not sensed");
        must(read[4]==0,"Other side falsely registered tactile collision");
        must(read[0]!=read[4],"Spatial sectors collapsed into one touch count");
        BodyProprioception m=new BodyProprioception();
        must(m.choiceBias(2)==0,"Virgin map influencing actions");
        float[] from=read.clone(),to=read.clone();to[0]=0;to[2]=.8f;
        for(int j=0;j<330;j++){
            m.feed(from);m.chosen(2);
            boolean heldout=j%6==0;
            m.consequence(to,2,.35f,2f,!heldout,heldout);
        }
        must(m.transitions==330&&m.validations==55,"Decision intervals or heldout counting broken");
        must(m.confidence()>0f,"Learned map did not beat unchanged forecast");
        must(m.choiceBias(2)!=0,"Observed contact never influenced choice after validation");
        String persisted=m.encode();
        BodyProprioception copy=new BodyProprioception();
        must(copy.restore(persisted)&&copy.transitions==m.transitions&&
             copy.validations==m.validations&&copy.encode().equals(persisted),
             "Saved learned tactile map not preserved after process restore");
        copy.resetLife();
        must(copy.transitions==m.transitions&&copy.confidence()>0,
            "Death erased learned body schema");
        BodyProprioception corrupt=new BodyProprioception();
        must(!corrupt.restore(persisted.replaceFirst("bp1","wrong"))&&
             corrupt.transitions==0,"Bad saved map accepted");
        System.out.println(String.format(Locale.US,
            "ZERO-0109 PASS: 96-node tactile map, distinct 8 physical sectors, grip/extension/slip, heldout confidence %.3f, persisted learned readouts",m.confidence()));
    }
}
