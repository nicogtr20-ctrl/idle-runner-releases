package com.overmind.zero;

import java.io.*;
import java.util.Arrays;

/** Bounded action-conditioned physical model. Predictions are never replay events.
 * Online validation measures interpolation, not independent behavioral competence.
 */
public final class Zero3CognitionV353 {
    public static final int INPUTS=194,ACTIONS=5,HEADS=12,ENSEMBLE=3,BASE_UNITS=12,MAX_UNITS=20;
    public static final String[] HEAD_NAMES={"reserve delta","damage","motor cost","velocity x","velocity y",
        "touch","held contact","area error","optical change","terminal","resource intake","elapsed"};
    private static final int WIDTH=INPUTS+ACTIONS+1;
    private static final float LR=.006f;
    private Model champion=new Model(BASE_UNITS,353);
    // V369: associative neuron memory, not a second action issuer.
    private Zero3ContactValenceV369 contactValence=new Zero3ContactValenceV369();
    // V371 is an evidence cache INSIDE this same predictive cognition.
    private Zero3InstrumentalCreditV371 instrumental=new Zero3InstrumentalCreditV371();
    private transient Zero384BodyAffordance embodiedV384;
    void attachEmbodiedV384(Zero384BodyAffordance model){embodiedV384=model;}
    boolean learnedImpassV384(){return embodiedV384!=null&&embodiedV384.verifiedImpass();}
    private Model shadow;
    private long trains,validations,plannerUses,growthAccepted,growthRejected,shadowTrains,shadowValidations;
    private final double[] error=new double[HEADS],baseline=new double[HEADS],spread=new double[HEADS],
        pairedOld=new double[HEADS],pairedNew=new double[HEADS];
    private final float[] previousTarget=new float[HEADS];
    private boolean havePrevious;
    private float[] pendingInput;
    // Bounded episodic evidence: observed PRE + actual chosen action + physical outcome.
    // Raw authoritative PRE/commands/POST remain in the V329 disk journal.
    private static final int EPISODES=64,EPISODE_WIDTH=INPUTS+ACTIONS+HEADS+1;
    private final float[] episodes=new float[EPISODES*EPISODE_WIDTH];
    private long episodesSeen;
    static boolean heldout(long decision){return decision%5==4;}
    private static float clip(float x){return Math.max(-1f,Math.min(1f,x));}
    private static void finite(float[] x,int n){
        if(x==null||x.length!=n)throw new IllegalArgumentException("V353 shape");
        for(float v:x)if(!Float.isFinite(v))throw new IllegalArgumentException("V353 nonfinite observation");
    }
    private static final class Model {
        final int units;
        final float[][] in=new float[ENSEMBLE][],out=new float[ENSEMBLE][];
        Model(int units,long seed){
            this.units=units;
            for(int e=0;e<ENSEMBLE;e++){
                in[e]=new float[units*WIDTH];out[e]=new float[HEADS*(units+1)];
                for(int i=0;i<in[e].length;i++){
                    seed^=seed<<13;seed^=seed>>>7;seed^=seed<<17;
                    in[e][i]=((seed>>>40)/16777216f-.5f)*.06f;
                }
                for(int i=0;i<out[e].length;i++){
                    seed^=seed<<13;seed^=seed>>>7;seed^=seed<<17;
                    out[e][i]=((seed>>>40)/16777216f-.5f)*.03f;
                }
            }
        }
        Model grow(){
            Model larger=new Model(units+2,353+units);
            for(int e=0;e<ENSEMBLE;e++){
                System.arraycopy(in[e],0,larger.in[e],0,in[e].length);
                for(int h=0;h<HEADS;h++){
                    System.arraycopy(out[e],h*(units+1),larger.out[e],h*(larger.units+1),units);
                    larger.out[e][h*(larger.units+1)+units]=0;
                    larger.out[e][h*(larger.units+1)+units+1]=0;
                    larger.out[e][h*(larger.units+1)+larger.units]=out[e][h*(units+1)+units];
                }
            }
            return larger;
        }
        float[][] predict(float[] x,boolean learn,float[] target){
            float[][] predictions=new float[ENSEMBLE][HEADS];
            for(int e=0;e<ENSEMBLE;e++){
                float[] z=new float[units],dh=new float[units];
                for(int j=0;j<units;j++){
                    float a=0;for(int i=0;i<WIDTH;i++)a+=in[e][j*WIDTH+i]*x[i];
                    z[j]=(float)Math.tanh(a);
                }
                for(int h=0;h<HEADS;h++){
                    int at=h*(units+1);float p=out[e][at+units];
                    for(int j=0;j<units;j++)p+=out[e][at+j]*z[j];
                    predictions[e][h]=clip(p);
                    if(learn){
                        float delta=Math.max(-1f,Math.min(1f,target[h]-p));
                        for(int j=0;j<units;j++){
                            dh[j]+=delta*out[e][at+j];
                            out[e][at+j]=bounded(out[e][at+j]+LR*delta*z[j]);
                        }
                        out[e][at+units]=bounded(out[e][at+units]+LR*delta);
                    }
                }
                if(learn)for(int j=0;j<units;j++){
                    float d=Math.max(-1f,Math.min(1f,dh[j]))*(1-z[j]*z[j]);
                    for(int i=0;i<WIDTH;i++)in[e][j*WIDTH+i]=bounded(in[e][j*WIDTH+i]+LR*d*x[i]);
                }
            }
            return predictions;
        }
        static float bounded(float x){return Math.max(-2,Math.min(2,x));}
    }
    private static float[] input(float[] observation,float[] action){
        finite(observation,INPUTS);finite(action,ACTIONS);
        float[] x=new float[WIDTH];System.arraycopy(observation,0,x,0,INPUTS);
        for(int i=0;i<ACTIONS;i++)x[INPUTS+i]=clip(action[i]);x[WIDTH-1]=1;
        return x;
    }
    Zero3CognitionV353 inheritedV354(){
        Zero3CognitionV353 child=new Zero3CognitionV353();child.champion=new Model(champion.units,353);
        for(int e=0;e<ENSEMBLE;e++){
            System.arraycopy(champion.in[e],0,child.champion.in[e],0,champion.in[e].length);
            System.arraycopy(champion.out[e],0,child.champion.out[e],0,champion.out[e].length);
        }
        child.growthAccepted=growthAccepted;child.growthRejected=growthRejected;
        contactValence.copyTo(child.contactValence);
        instrumental.copyTo(child.instrumental);return child;
    }
    float[] baselineV354(){return previousTarget.clone();}
    float[] predictionV354(float[] observation,float[] action){
        float[][] p=champion.predict(input(observation,action),false,null);float[] result=new float[HEADS+1];
        for(int h=0;h<HEADS;h++)result[h]=mean(p,h);
        float variance=0;for(int e=0;e<ENSEMBLE;e++)for(int h=0;h<3;h++){float diff=p[e][h]-result[h];variance+=diff*diff/9;}
        result[HEADS]=(float)Math.sqrt(variance);return result;
    }
    private static float mean(float[][] p,int h){return (p[0][h]+p[1][h]+p[2][h])/ENSEMBLE;}
    /** Select only among supplied actor proposals, with a confidence-gated one-step comparison.
     * The selected proposal's noise remains the actor's on-policy sample.
     */
    /**
     * V366: cost-uncertainty is a physical risk, NOT an invented free
     * action. When cost is uninformative/near-constant, require a predicted
     * energy-and-harm improvement large enough to exceed BOTH alternatives'
     * measured held-out cost error bounds. No confidence => old actor choice.
     */
    static float conservativeCostMarginV366(double heldoutError,long validations){
        if(validations<64||!Double.isFinite(heldoutError)||heldoutError<0)
            return Float.POSITIVE_INFINITY;
        return (float)(4.0*Math.sqrt(heldoutError/validations));
    }
    /** Same measured heldout bound already used by V366's conservative
     * one-step comparison. V368 multi-step planner may use it, but never
     * relabel an uncertain cost predictor as trustworthy or zero cost. */
    float costSafetyMarginV368(){
        if(trusted(2))return 0f;
        return conservativeCostMarginV366(error[2],
            validations-reliabilityValidations);
    }
    void adoptOldContactEvidenceV369(float[] physicalMean,int[] contactCount){
        contactValence.adoptArchivedRealContacts(physicalMean,contactCount);
    }
    /** V374: contacted material's observable optical surface, not global brightest scene. */
    void materialContactV374(int touchedOpticalBand,float[] spectral24,
                              float physicalEnergyDelta,boolean heldout){
        if(pendingInput!=null)instrumental.materialContactV374(
            spectral24,touchedOpticalBand,physicalEnergyDelta,heldout);
        if(!heldout)contactValence.materialContact(touchedOpticalBand,physicalEnergyDelta);
    }
    /** V374 physically experienced colour must inform ALL actor candidates
     * even before a particular motor bank has accumulated 3 heldout trials.
     * Verified-motor response gives ADDITIONAL benefit, never a prerequisite
     * to knowing an experienced wound is harmful. */
    float physicalSpectralPreferenceV374(float[] observation,float dx,float dy){
        return contactValence.utility(observation,dx,dy);
    }
    void genuineContactV369(float[] spectral24,float physicalEnergyDelta,boolean heldout){
        if(pendingInput!=null)instrumental.contact(spectral24,physicalEnergyDelta,heldout);
        if(!heldout)contactValence.contact(spectral24,physicalEnergyDelta);
    }
    boolean hasVisibleContactKnowledgeV369(float[] observation){
        return contactValence.hasReliableVisibleEvidence(observation);
    }
    float contactValenceScoreV369(float[] observation,float dx,float dy){
        return contactValence.utility(observation,dx,dy);
    }
    /** V383: the sampled motor exploration WIDTH may grow only from
     * physically measured, heldout-validated active steerability. Knowing
     * the consequence of a coloured gem is cognitive evidence, not proof
     * that the body can rapidly steer in a new direction.
     */
    boolean motorExplorationReadyV383(float[] sensed194){
        return instrumental.wideMotorExplorationSupportedV383(sensed194);
    }
    int select(float[] observation,float[][] proposals){
        // A trusted one-step model is ONE source of evidence. The original
        // controller also demanded REAL, independently heldout movement
        // estimates before allowing light valence to bias a heading. The V3
        // path now does so without spawning a second decision-making brain.
        boolean reliableEnergy=trusted(0)&&trusted(1);
        boolean costTrusted=trusted(2);
        float costSafety=reliableEnergy&&!costTrusted?
            conservativeCostMarginV366(error[2],validations-reliabilityValidations):0f;
        boolean canPredict=reliableEnergy&&(costTrusted||Float.isFinite(costSafety));
        int best=0;float bestScore=-Float.MAX_VALUE,original=Float.NaN;
        for(int i=0;i<proposals.length;i++){
            float movementScore=instrumental.score(observation,proposals[i],contactValence);
            // V374: actual material injury is already learned in V369,
            // so it must affect selection even for headings that have not
            // accumulated enough clean motor trials to pass V371's gait
            // confidence threshold. V371 verified motion adds evidence.
            float score=movementScore+
                instrumental.opticalPreferenceV374(observation,proposals[i],contactValence)+
                (embodiedV384==null?0f:embodiedV384.learnedCost(proposals[i][0],proposals[i][1]));
            if(canPredict){
                float[][] p=champion.predict(input(observation,proposals[i]),false,null);
                // The V382 cost predictor may have MSE >2000x its own
                // heldout zero-motion baseline. Never subtract that raw
                // cost to RANK choices while trusted(2) is false; the
                // independently measured conservative safety margin below
                // remains in force. No claim of zero metabolic cost.
                float energy=mean(p,0),damage=mean(p,1),
                    cost=costTrusted?mean(p,2):0f,uncertainty=0;
                int heads=costTrusted?3:2;
                for(int e=0;e<ENSEMBLE;e++)for(int h=0;h<heads;h++){
                    float delta=p[e][h]-mean(p,h);uncertainty+=delta*delta;
                }
                score+=energy-damage-cost-2*(float)Math.sqrt(uncertainty/(3f*heads));
            }
            if(i==0)original=score;
            if(score>bestScore){bestScore=score;best=i;}
        }
        // No model = retain actor unless a motor direction is genuinely
        // verified AND visible experienced energy is large enough to matter.
        // V380: TWO real band contacts + visible photons are evidence, not
        // a motor illusion. Keep the normal .025 caution for unexplained
        // scenes, but never discard a verified contact's .0245 advantage
        // solely because its photons were divided by eight and downweighted
        // while the action-to-body mapping is still uncertain.
        boolean materialValenceObserved=contactValence.hasReliableVisibleEvidence(observation);
        float margin=materialValenceObserved?.005f:.025f;
        if(!canPredict)return bestScore>original+margin?best:0;
        plannerUses++;
        return bestScore>original+Math.max(margin,costSafety)?best:0;
    }
    float instrumentalScoreV371(float[] observation,float[] action){
        return instrumental.score(observation,action,contactValence);
    }
    float realContactPreferenceV374(float[] observation,float[] action){
        return instrumental.score(observation,action,contactValence)+
            instrumental.opticalPreferenceV374(observation,action,contactValence);
    }
    boolean verifiedDirectionalV371(float[] observation){
        return instrumental.hasVerifiedDirectionalEvidence(observation,contactValence);
    }
    boolean repeatedRealMotorStallV371(){return instrumental.repeatedRealMotorStall();}
    void physicalMotorV371(float dx,float dy,float elapsed,boolean physical,
                            boolean disturbed,boolean heldout){
        physicalMotorV371(dx,dy,elapsed,physical,disturbed,heldout,false);
    }
    void physicalMotorV371(float dx,float dy,float elapsed,boolean physical,
                            boolean disturbed,boolean heldout,boolean blocked){
        // Physical blockage is NOT clean motor skill evidence. Record a
        // witnessed inability to advance, never a positive force/reward.
        instrumental.complete(dx,dy,elapsed,physical&&!disturbed,heldout,blocked);
    }
    void begin(float[] observation,float[] actualProposal){
        begin(observation,actualProposal,heldout(trains+validations));
    }
    void begin(float[] observation,float[] actualProposal,boolean heldout){
        if(pendingInput!=null)throw new IllegalStateException("V353 pending physical experience");
        pendingInput=input(observation,actualProposal);
        instrumental.begin(observation,actualProposal,heldout);
    }
    void abort(){pendingInput=null;instrumental.abort();}
    void resetTransientV371(){instrumental.newLife();}
    /** Construct targets from committed physical sensors/metabolism, never labels/coordinates. */
    static float[] targets(float[] before,float[] after,Zero3CausalVitalBoundaryV344.After vital,float elapsed){
        finite(before,INPUTS);finite(after,INPUTS);
        if(vital==null||!Float.isFinite(elapsed)||elapsed<=0||elapsed>.651f)
            throw new IllegalArgumentException("V353 physical outcome missing");
        float touch=0,held=0,optical=0;
        for(int i=0;i<16;i++){touch+=after[60+i]/16;held+=after[92+i]/16;}
        for(int i=20;i<44;i++)optical+=Math.abs(after[i]-before[i])/48;
        return new float[]{clip(vital.netEnergy/100),clip(vital.actualDamage/100),clip(vital.actualMotor/100),
            after[191],after[192],touch,held,after[190],clip(optical),vital.afterReserve<=0?1:0,
            clip(vital.actualGained/100),clip(elapsed/.65f)};
    }
    void complete(float[] targets,boolean heldout){
        finite(targets,HEADS);
        if(pendingInput==null)throw new IllegalStateException("V353 missing PRE");
        for(float v:targets)if(v < -1||v>1)throw new IllegalArgumentException("V353 target range");
        float[][] p=champion.predict(pendingInput,!heldout,targets);
        float[][] q=shadow==null?null:shadow.predict(pendingInput,!heldout,targets);
        if(heldout){
            validations++;
            for(int h=0;h<HEADS;h++){
                double d=targets[h]-mean(p,h),b=targets[h]-(havePrevious?previousTarget[h]:0);
                error[h]+=d*d;baseline[h]+=b*b;
                for(int e=0;e<ENSEMBLE;e++){double s=p[e][h]-mean(p,h);spread[h]+=s*s/ENSEMBLE;}
                if(q!=null){double n=targets[h]-mean(q,h);pairedOld[h]+=d*d;pairedNew[h]+=n*n;}
            }
            if(q!=null)shadowValidations++;
        }else{
            trains++;if(q!=null)shadowTrains++;
            // Baseline learns its last target ONLY from training samples.
            System.arraycopy(targets,0,previousTarget,0,HEADS);havePrevious=true;
            int at=(int)(episodesSeen%EPISODES)*EPISODE_WIDTH;
            System.arraycopy(pendingInput,0,episodes,at,INPUTS+ACTIONS);
            System.arraycopy(targets,0,episodes,at+INPUTS+ACTIONS,HEADS);
            episodes[at+EPISODE_WIDTH-1]=trains;episodesSeen++;
        }
        pendingInput=null;
        if(!heldout&&shadow!=null&&shadowTrains>=256&&shadowValidations>=64){
            boolean accept=true;double a=0,b=0;
            for(int h=0;h<HEADS;h++){
                a+=pairedOld[h];b+=pairedNew[h];
                if(pairedNew[h]>pairedOld[h]*1.10+1e-8)accept=false;
            }
            if(accept&&a>1e-6&&b<a*.95){
                champion=shadow;growthAccepted++;
                // New topology has no independent per-head reliability evidence yet.
                Arrays.fill(error,0);Arrays.fill(baseline,0);Arrays.fill(spread,0);reliabilityValidations=validations;
            }else growthRejected++;
            shadow=null;shadowTrains=shadowValidations=0;
            Arrays.fill(pairedOld,0);Arrays.fill(pairedNew,0);
        }
        // Allocation/starting growth is forbidden on a validation event.
        if(!heldout&&shadow==null&&champion.units<MAX_UNITS&&trains%512==0&&needsGrowth()){
            shadow=champion.grow();shadowTrains=shadowValidations=0;
        }
    }
    private boolean needsGrowth(){
        long n=validations-reliabilityValidations;if(n<64)return false;
        for(int h=0;h<HEADS;h++)if(error[h]/n>.0001&&error[h]>=baseline[h]*.9)return true;
        return false;
    }
    private long reliabilityValidations;
    public boolean trusted(int h){
        if(h<0||h>=HEADS)throw new IllegalArgumentException("head");
        long n=validations-reliabilityValidations;
        return n>=64&&baseline[h]>1e-7&&error[h]<baseline[h]*.9&&spread[h]/n<.01;
    }
    public static final class Snapshot {
        public final long train,heldout,plannerUses,growthAccepted,growthRejected;
        public final int predictiveUnits,shadowUnits,episodicRecords;
        public final float[] mse,baselineMse,uncertainty;
        public final boolean[] reliable;
        public final int[] contactSamples=new int[3];
        public final int[] archivedContactSamples=new int[3];
        public final float[] contactValues=new float[3];
        public final long contactAccepted,contactAmbiguous;
        public final int verifiedMotorDirectionsV371;
        public final int validatedTrueInertialDirectionsV377;
        public final long linkedRealContactsV371,ambiguousMotorContactsV371,stalledActionsV371;
        Snapshot(Zero3CognitionV353 c){
            train=c.trains;heldout=c.validations;plannerUses=c.plannerUses;
            for(int i=0;i<3;i++){
                contactSamples[i]=c.contactValence.evidence(i);
                archivedContactSamples[i]=c.contactValence.archivedEvidence(i);
                contactValues[i]=c.contactValence.learnedValue(i);
            }
            contactAccepted=c.contactValence.accepted();
            contactAmbiguous=c.contactValence.ambiguous();
            verifiedMotorDirectionsV371=c.instrumental.verifiedDirections();
            validatedTrueInertialDirectionsV377=c.instrumental.verifiedInertialResponseV377();
            linkedRealContactsV371=c.instrumental.linkedContacts();
            ambiguousMotorContactsV371=c.instrumental.ambiguousContacts();
            stalledActionsV371=c.instrumental.stalledActions();
            growthAccepted=c.growthAccepted;growthRejected=c.growthRejected;
            predictiveUnits=c.champion.units*ENSEMBLE;shadowUnits=c.shadow==null?0:c.shadow.units*ENSEMBLE;
            episodicRecords=(int)Math.min(EPISODES,c.episodesSeen);
            mse=new float[HEADS];baselineMse=new float[HEADS];uncertainty=new float[HEADS];reliable=new boolean[HEADS];
            long n=c.validations-c.reliabilityValidations;
            for(int h=0;h<HEADS;h++){
                mse[h]=n==0?Float.NaN:(float)(c.error[h]/n);
                baselineMse[h]=n==0?Float.NaN:(float)(c.baseline[h]/n);
                uncertainty[h]=n==0?Float.NaN:(float)Math.sqrt(c.spread[h]/n);reliable[h]=c.trusted(h);
            }
        }
    }
    public Snapshot snapshot(){return new Snapshot(this);}
    byte[] encode(){
        if(pendingInput!=null)throw new IllegalStateException("V353 checkpoint mid-decision");
        try{
            ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream d=new DataOutputStream(b);
            d.writeInt(353);for(long n:new long[]{trains,validations,plannerUses,growthAccepted,growthRejected,
                shadowTrains,shadowValidations,episodesSeen,reliabilityValidations})d.writeLong(n);
            d.writeBoolean(havePrevious);write(d,previousTarget);write(d,episodes);
            for(double[] a:new double[][]{error,baseline,spread,pairedOld,pairedNew})for(double v:a)d.writeDouble(v);
            writeModel(d,champion);d.writeBoolean(shadow!=null);if(shadow!=null)writeModel(d,shadow);
            contactValence.write(d); // Optional V369 extension after legacy 353.
            instrumental.write(d); // Optional V371 extension after V369.
            d.flush();return b.toByteArray();
        }catch(IOException impossible){throw new IllegalStateException(impossible);}
    }
    private static void write(DataOutputStream d,float[] a)throws IOException{for(float x:a)d.writeFloat(x);}
    private static void writeModel(DataOutputStream d,Model m)throws IOException{
        d.writeInt(m.units);for(int e=0;e<ENSEMBLE;e++){write(d,m.in[e]);write(d,m.out[e]);}
    }
    private static void read(DataInputStream d,float[] a,float bound)throws IOException{
        for(int i=0;i<a.length;i++){a[i]=d.readFloat();if(!Float.isFinite(a[i])||Math.abs(a[i])>bound)throw new IOException("V353 invalid parameter");}
    }
    private static Model readModel(DataInputStream d)throws IOException{
        int n=d.readInt();if(n<BASE_UNITS||n>MAX_UNITS||(n-BASE_UNITS)%2!=0)throw new IOException("V353 topology");
        Model m=new Model(n,353);for(int e=0;e<ENSEMBLE;e++){read(d,m.in[e],2);read(d,m.out[e],2);}return m;
    }
    static Zero3CognitionV353 decode(byte[] raw)throws IOException{
        if(raw==null||raw.length<100||raw.length>250000)throw new IOException("V353 size");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(raw));
        if(d.readInt()!=353)throw new IOException("V353 schema");
        Zero3CognitionV353 c=new Zero3CognitionV353();long[] n=new long[9];
        for(int i=0;i<n.length;i++){n[i]=d.readLong();if(n[i]<0||n[i]>100000000)throw new IOException("V353 counter");}
        c.trains=n[0];c.validations=n[1];c.plannerUses=n[2];c.growthAccepted=n[3];c.growthRejected=n[4];
        c.shadowTrains=n[5];c.shadowValidations=n[6];c.episodesSeen=n[7];c.reliabilityValidations=n[8];
        if(c.episodesSeen!=c.trains||c.reliabilityValidations>c.validations||c.plannerUses>c.trains+c.validations||
            c.shadowTrains>c.trains||c.shadowValidations>c.validations)throw new IOException("V353 inconsistent counters");
        c.havePrevious=d.readBoolean();if(c.havePrevious!=(c.trains>0))throw new IOException("V353 baseline provenance");
        read(d,c.previousTarget,1);read(d,c.episodes,100000000);
        for(double[] a:new double[][]{c.error,c.baseline,c.spread,c.pairedOld,c.pairedNew})for(int i=0;i<a.length;i++){
            a[i]=d.readDouble();if(!Double.isFinite(a[i])||a[i]<0)throw new IOException("V353 invalid evidence");
        }
        c.champion=readModel(d);if(d.readBoolean())c.shadow=readModel(d);
        // Old V353 checkpoints end here. Only genuinely new checkpoints
        // contain this optional fixed binary; learned old matrices remain.
        if(d.available()>0)c.contactValence=Zero3ContactValenceV369.read(d);
        if(d.available()>0)c.instrumental=Zero3InstrumentalCreditV371.read(d);
        if(c.champion.units!=BASE_UNITS+2*c.growthAccepted||
           (c.shadow!=null&&c.shadow.units!=c.champion.units+2)||
           (c.shadow==null&&(c.shadowTrains!=0||c.shadowValidations!=0))||d.available()!=0)
            throw new IOException("V353 inconsistent growth/trailing bytes");
        return c;
    }
    void appendGraphV354(Zero3NeuralGraphV354.Builder b){
        for(int mode=0;mode<2;mode++){
            Model m=mode==0?champion:shadow;if(m==null)continue;
            for(int e=0;e<ENSEMBLE;e++){
                int base=b.nodes.size();
                for(int u=0;u<m.units;u++)b.node(mode==0?6:8,e*m.units+u,WIDTH,"predictor"+e+"/"+u,Float.NaN,m.in[e],u*WIDTH,WIDTH);
                for(int h=0;h<HEADS;h++)b.node(mode==0?7:9,e*HEADS+h,m.units+1,HEAD_NAMES[h]+"/"+e,Float.NaN,m.out[e],h*(m.units+1),m.units+1);
                for(int u=0;u<m.units;u++)for(int i=0;i<WIDTH;i++)b.edge(-1-i,base+u,0,m.in[e][u*WIDTH+i]);
                for(int h=0;h<HEADS;h++)for(int u=0;u<=m.units;u++)b.edge(u==m.units?-1000:base+u,base+m.units+h,0,m.out[e][h*(m.units+1)+u]);
            }
        }
    }
    /** Trainable matrices only, excluding validation evidence and neural activity. */
    float[] weights(){
        int size=0;for(int e=0;e<ENSEMBLE;e++)size+=champion.in[e].length+champion.out[e].length;
        if(shadow!=null)for(int e=0;e<ENSEMBLE;e++)size+=shadow.in[e].length+shadow.out[e].length;
        float[] result=new float[size];int at=0;
        for(Model m:new Model[]{champion,shadow})if(m!=null)for(int e=0;e<ENSEMBLE;e++)for(float[] a:new float[][]{m.in[e],m.out[e]}){
            System.arraycopy(a,0,result,at,a.length);at+=a.length;
        }
        return result;
    }
}
