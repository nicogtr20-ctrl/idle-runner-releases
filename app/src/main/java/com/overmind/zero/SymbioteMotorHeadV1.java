package com.overmind.zero;

import java.util.Arrays;
import java.util.Locale;

/**
 * Learnable distributed motor readout OF the sole RecurrentCoreV22 controller.
 * Not an action chooser: the 2.2's selected action is the only intent.
 * Sixteen anonymous membrane sectors have four differentiable outputs each:
 * pressure bias, tangential drive, voluntary local adhesion, cortex tension.
 * No target/entity identity or scripted pseudopod command.
 */
public final class SymbioteMotorHeadV1 {
    public static final String KEY="symbiote_motor_head_v1";
    /** Optional sector-specific living cortex. Old 84 shared weights stay byte-identical. */
    public static final String LOCAL_KEY="symbiote_cortex_local_v161";
    private static final int N=SymbioteMatterV1.SECTORS,CHANNELS=SymbioteMatterV1.OUTPUTS;
    private static final int LEGACY_FEATURE=14,FEATURE=LEGACY_FEATURE+FluidInteroceptionV1.CHANNELS,
        OUTPUTS=N*CHANNELS;
    private static final int LOCAL_FEATURES=5;
    private final float[] weights=new float[CHANNELS*FEATURE];
    private final float[] localWeights=new float[OUTPUTS*LOCAL_FEATURES];
    private final float[] localFeatures=new float[OUTPUTS*LOCAL_FEATURES];
    private final SymbioteCoordinationV162 coordination=new SymbioteCoordinationV162();
    private int priorPhysicalAction=-1;
    private long localUpdates=0;
    private final float[] feature=new float[N*FEATURE],commands=new float[OUTPUTS];
    private final float[] noise=new float[OUTPUTS],pending=new float[OUTPUTS];
    private float[] previousState;
    private int lastAction=-1;
    private long seed=0x3910b15dceef41L,choices,updates,heldout;
    private float meanReward=0f;
    public SymbioteMotorHeadV1(){
        // Small unbiased initial pathways. This is not a gait or hand recipe.
        // Preserve the precise old virgin seed sequence in the original
        // 14 features of each output; the 7 new internal afferents begin at
        // ZERO, so OTA151 does not suddenly alter newborn locomotion.
        for(int channel=0;channel<CHANNELS;channel++)
            for(int j=0;j<LEGACY_FEATURE;j++)
                weights[channel*FEATURE+j]=random()*.056f;
    }
    private static float clip(float x,float a,float b){return Math.max(a,Math.min(b,x));}
    private float random(){
        seed^=seed<<13;seed^=seed>>>7;seed^=seed<<17;
        return ((seed>>>40)/16777216f-.5f)*2;
    }
    private static boolean valid(float[] a,int n){
        if(a==null||a.length!=n)return false;
        for(float v:a)if(!Float.isFinite(v)||Math.abs(v)>5)return false;
        return true;
    }
    /** The 96×9 patch map is grounded exclusively in local touch, strain,
     * velocity, shape and optic observations. 164D state provides generic
     * interoceptive context. The action's angle is a bodily heading cue,
     * not a named object location or an automatic chosen "arm".
     */
    public void chosen(int action,float[] bodySensors,float[] recurrent,
                       float exploration){
        chosen(action,bodySensors,recurrent,exploration,null,null);
    }
    /** The SAME single decision; learned cortex sees actual living inside. */
    public void chosen(int action,float[] bodySensors,float[] recurrent,
                       float exploration,float[] internal){
        chosen(action,bodySensors,recurrent,exploration,internal,null);
    }

    /**
     * Read ACTUAL world azimuths for the 16 material cortex sectors.
     * Material point #17 is not permanently "north-east": a fluid body
     * twists and stretches. This is proprioception, NOT a target direction.
     * No energy/food/object coordinates and no gait are read.
     */
    public static float[] measuredSectorAngles(MotorBrain.Body body,
                                               Morphology.Surface skin){
        if(body==null||skin==null)return null;
        float[] angles=new float[N];
        int width=Morphology.POINTS/N;
        for(int sector=0;sector<N;sector++){
            float x=0f,y=0f;
            for(int k=0;k<width;k++){
                int i=sector*width+k;
                float dx=skin.x[i]-body.cx,dy=skin.y[i]-body.cy;
                float len=(float)Math.hypot(dx,dy);
                if(Float.isFinite(len)&&len>.001f){
                    x+=dx/len;y+=dy/len;
                }
            }
            // A perfect local cancellation means no meaningful direction.
            // Fall back to the most distant actual material patch, never
            // the historic sector angle or a privileged desired heading.
            if(Math.hypot(x,y)<.04f){
                float d2=-1f;float bestX=0,bestY=0;
                for(int k=0;k<width;k++){
                    int i=sector*width+k;
                    float dx=skin.x[i]-body.cx,dy=skin.y[i]-body.cy;
                    float rr=dx*dx+dy*dy;
                    if(Float.isFinite(rr)&&rr>d2){d2=rr;bestX=dx;bestY=dy;}
                }
                x=bestX;y=bestY;
            }
            angles[sector]=(float)Math.atan2(y,x);
        }
        return angles;
    }

    /** The same single recurrent chosen action, now aligned to real tissue. */
    public void chosen(int action,float[] bodySensors,float[] recurrent,
                       float exploration,float[] internal,float[] sectorAngles){
        if(action<0||action>=RecurrentCoreV22.A||
           !valid(recurrent,RecurrentCoreV22.Z)||
           bodySensors==null||bodySensors.length!=DistributedBodySense.FEATURES)
            return;
        choices++;
        boolean samePhysicalCommand=priorPhysicalAction==action;
        priorPhysicalAction=action;
        lastAction=action;
        previousState=recurrent.clone();
        float goal=action%8*(float)Math.PI/4f;
        for(int sector=0;sector<N;sector++){
            float a=sectorAngles!=null&&sectorAngles.length==N&&
                Float.isFinite(sectorAngles[sector])
                ?sectorAngles[sector]
                :(sector+.5f)*(float)Math.PI*2/N;
            int base=sector*FEATURE;
            for(int j=0;j<DistributedBodySense.CHANNELS;j++){
                float value=0;
                for(int k=0;k<Morphology.POINTS/N;k++)
                    value+=bodySensors[(sector*(Morphology.POINTS/N)+k)*
                        DistributedBodySense.CHANNELS+j]/(Morphology.POINTS/N);
                feature[base+j]=clip(value,-1,1);
            }
            feature[base+9]=(float)Math.cos(a-goal);
            feature[base+10]=(float)Math.sin(a-goal);
            feature[base+11]=clip(recurrent[0],0,1);
            feature[base+12]=clip(recurrent[RecurrentCoreV22.F+sector%
                                    RecurrentCoreV22.H],-1,1);
            feature[base+13]=1;
            // Seven genuinely measured local fluid channels. Only freshly
            // sampled real tissue is eligible; null from legacy leaves 0.
            for(int ch=0;ch<FluidInteroceptionV1.CHANNELS;ch++)
                feature[base+LEGACY_FEATURE+ch]=internal!=null&&
                    internal.length==FluidInteroceptionV1.SUMMARY&&
                    Float.isFinite(internal[ch*N+sector])
                    ?clip(internal[ch*N+sector],-1f,1f):0f;
            for(int channel=0;channel<CHANNELS;channel++){
                float z=0;
                for(int j=0;j<FEATURE;j++)z+=weights[channel*FEATURE+j]*feature[base+j];
                int at=sector*CHANNELS+channel;
                // Five independently learnable real-tissue features per site.
                // Threefold angular basis is a representational capability,
                // NOT a prewired three-limb behavior; every new weight is zero.
                int localBase=at*LOCAL_FEATURES;
                localFeatures[localBase]=1f;
                localFeatures[localBase+1]=feature[base+DistributedBodySense.SHAPE];
                localFeatures[localBase+2]=feature[base+DistributedBodySense.OPTIC];
                localFeatures[localBase+3]=(float)Math.cos(3f*(a-goal));
                localFeatures[localBase+4]=(float)Math.sin(3f*(a-goal));
                for(int j=0;j<LOCAL_FEATURES;j++)
                    z+=localWeights[localBase+j]*localFeatures[localBase+j];
                float explore=clip(exploration,0,1)*.48f;
                noise[at]=random()*explore;
                // Exploration is localized and smooth enough not to make a
                // single 96-node patch teleport relative to its neighbours.
                // Actuator gain makes newborn cortex commands physically usable.
                // The active 2.2 action bank 8..15 explicitly requests grip;
                // action%8 alone in OTA143 had discarded this motor intention.
                float gripIntent=channel==2&&action>=8?.55f:0f;
                float proposed=(float)Math.tanh(2.8f*z+1.4f*noise[at]+gripIntent);
                // Hold a real muscle contraction while the SAME 2.2 action
                // continues. New action, danger-driven policy change and
                // episode start may replace it immediately (no persistent gait).
                commands[at]=samePhysicalCommand?
                    clip(.55f*commands[at]+.45f*proposed,-1f,1f):proposed;
                pending[at]=commands[at];
            }
        }
        coordination.begin(action,sectorAngles);
    }
    /** Real physical frame AFTER world contact and all solid collisions. */
    public void observedPhysicalStep(float beforeX,float beforeY,
                                     MotorBrain.Body now,Morphology.Surface skin){
        if(now!=null&&skin!=null)
            coordination.sample(beforeX,beforeY,now.cx,now.cy,commands);
    }
    public SymbioteCoordinationV162 coordination(){return coordination;}
    public float observedNominalCancellation(){return coordination.cancellation();}
    public float observedIntentRealization(){return coordination.intentRealization();}
    public String encodeCoordination(){return coordination.encode();}
    public boolean restoreCoordination(String data){return coordination.restore(data);}
    public float[] commands(){return commands;}
    /**
     * Only lived reserve gained/lost, real injury, and measured effort train
     * the downstream neural motor channel. Heldout intervals cannot train.
     * REINFORCE with an updated reward baseline; no imagined success data.
     */
    /** Kept for old JVM callers; the five-input form has no ambient loss.
     * "effort" must be a REAL charged motor energy cost, never distance. */
    public void consequence(float actualContactEnergy,float actualDamage,
                            float actualMotorEnergy,boolean train,boolean holdout){
        consequence(actualContactEnergy,actualDamage,0f,actualMotorEnergy,
                    train,holdout);
    }
    /**
     * Every non-zero motor learning outcome corresponds to REAL externally
     * credited resource gain, contact harm, storm energy loss, or physically
     * debited muscle metabolism. Passive/base hunger is deliberately NOT
     * attributed to the chosen muscles. No distance/forward/gait reward.
     */
    public void consequence(float actualContactEnergy,float actualDamage,
                            float actualAmbientEnergyLoss,float actualMotorEnergy,
                            boolean train,boolean holdout){
        if(lastAction<0)return;
        if(!Float.isFinite(actualContactEnergy)||!Float.isFinite(actualDamage)||
           !Float.isFinite(actualAmbientEnergyLoss)||!Float.isFinite(actualMotorEnergy)||
           actualDamage<0||actualAmbientEnergyLoss<0||actualMotorEnergy<0){
            lastAction=-1;coordination.resetLife();return;
        }
        float reward=clip(actualContactEnergy/35f-actualDamage/90f-
            actualAmbientEnergyLoss/35f-actualMotorEnergy/35f,-2,2);
        // In a purely eventless interval, the cortex has no evidence
        // that ANY perturbation was better. Keep virgin muscles neutral;
        // a rising count is not evidence of learning.
        boolean experienced=actualContactEnergy!=0f||actualDamage>0f||
             actualAmbientEnergyLoss>0f||actualMotorEnergy>0f;
        float advantage=experienced?clip(reward-meanReward,-2,2):0f;
        if(holdout)heldout++;
        else if(train){
            float lr=.0018f;
            float[] gradient=new float[weights.length];
            for(int sector=0;sector<N;sector++){
                for(int channel=0;channel<CHANNELS;channel++){
                    int at=sector*CHANNELS+channel;
                    float derivative=1-pending[at]*pending[at];
                    // Use the actual bounded exploratory perturbation as an
                    // unbiased score-function direction, not privileged info.
                    float g=lr*advantage*noise[at]*derivative/N;
                    int base=sector*FEATURE;
                    for(int j=0;j<FEATURE;j++)
                        gradient[channel*FEATURE+j]+=g*feature[base+j];
                    // Independent local plasticity from exactly the same
                    // REAL survived/paid consequence; heldout never trains.
                    int localBase=at*LOCAL_FEATURES;
                    for(int j=0;j<LOCAL_FEATURES;j++)
                        localWeights[localBase+j]=clip(localWeights[localBase+j]+
                            8f*g*localFeatures[localBase+j],-1.5f,1.5f);
                }
            }
            localUpdates++;
            for(int i=0;i<weights.length;i++)
                weights[i]=clip(weights[i]+gradient[i],-2.5f,2.5f);
            meanReward=meanReward*.995f+reward*.005f;
            updates++;
        }
        coordination.consequence(train,holdout,noise,pending,localFeatures,
            localWeights,CHANNELS,LOCAL_FEATURES);
        lastAction=-1;
    }
    public void resetLife(){lastAction=-1;priorPhysicalAction=-1;
        coordination.resetLife();
        Arrays.fill(commands,0);Arrays.fill(noise,0);}
    public void resumeLife(){resetLife();}
    /** Safe counters, sourced from the same authenticated saved cortex. */
    public long decisionCount(){return choices;}
    public long trainedOutcomes(){return updates;}
    public long heldoutOutcomes(){return heldout;}
    /** Read-only learned muscle-cortex weights; 84 trainable scalars. */
    public int parameterCount(){return weights.length;}
    public int localParameterCount(){return localWeights.length;}
    public long localTrainedOutcomes(){return localUpdates;}
    public float localParameterAt(int index){
        if(index<0||index>=localWeights.length)throw new IndexOutOfBoundsException();
        return localWeights[index];
    }
    /** Separate optional save; never changes the legacy smh1 format. */
    public String encodeLocal(){
        StringBuilder out=new StringBuilder("cl161|").append(localUpdates);
        for(float w:localWeights)out.append("|").append(w);
        return out.toString();
    }
    public boolean restoreLocal(String raw){
        if(raw==null||raw.length()>17000||!raw.startsWith("cl161|"))return false;
        try{
            String[] parts=raw.split("\\|",-1);
            if(parts.length!=2+localWeights.length)return false;
            long steps=Long.parseLong(parts[1]);
            if(steps<0||steps>1000000000L)return false;
            float[] saved=new float[localWeights.length];
            for(int i=0;i<saved.length;i++){
                saved[i]=Float.parseFloat(parts[2+i]);
                if(!Float.isFinite(saved[i])||Math.abs(saved[i])>1.501f)return false;
            }
            System.arraycopy(saved,0,localWeights,0,saved.length);
            localUpdates=steps;return true;
        }catch(RuntimeException malformed){return false;}
    }
    public float parameterAt(int index){
        if(index<0||index>=weights.length)throw new IndexOutOfBoundsException();
        return weights[index];
    }
    public String summary(){return String.format(Locale.US,
        "SYMBIOTE cortex motor head: decisions=%d trained=%d heldout=%d",choices,updates,heldout);}
    /** Compact backward-compatible schema, no side effects on malformed data. */
    public String encode(){
        StringBuilder s=new StringBuilder("smh1").append('|').append(seed)
            .append('|').append(choices).append('|').append(updates)
            .append('|').append(heldout).append('|').append(meanReward);
        for(float w:weights)s.append('|').append(w);
        return s.toString();
    }
    public boolean restore(String text){
        if(text==null||text.length()>12000||!text.startsWith("smh1|"))return false;
        try{
            String[] p=text.split("\\|",-1);
            boolean old=p.length==6+CHANNELS*LEGACY_FEATURE;
            if(!old&&p.length!=6+weights.length)return false;
            long newSeed=Long.parseLong(p[1]);
            long decisions=Long.parseLong(p[2]),learnt=Long.parseLong(p[3]),hold=Long.parseLong(p[4]);
            float baseline=Float.parseFloat(p[5]);
            if(decisions<0||learnt<0||hold<0||learnt>decisions||
               hold>decisions||learnt+hold>decisions||!Float.isFinite(baseline)||
               Math.abs(baseline)>2.01f)return false;
            float[] newWeights=new float[weights.length];
            for(int channel=0;channel<CHANNELS;channel++){
                int width=old?LEGACY_FEATURE:FEATURE;
                for(int j=0;j<width;j++){
                    float v=Float.parseFloat(p[6+channel*width+j]);
                    if(!Float.isFinite(v)||Math.abs(v)>2.501f)return false;
                    newWeights[channel*FEATURE+j]=v;
                }
                // Newly added internal-fluid connections start EXACTLY 0,
                // preserving every pre-existing muscle output on migration.
            }
            seed=newSeed;choices=decisions;updates=learnt;heldout=hold;meanReward=baseline;
            System.arraycopy(newWeights,0,weights,0,weights.length);
            resetLife();return true;
        }catch(RuntimeException e){return false;}
    }
}
