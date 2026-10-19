package com.overmind.zero;

import java.io.*;
import java.util.*;

/** Learned options from actual actor intentions. No object labels, authored movement or second issuer.
 * Online validation is explicitly separate from final independent capability certification.
 */
public final class Zero3ExecutiveV354 {
    static final int CONTEXT=15,PRIOR_CONTEXT=12,LIMIT=16;
    static final int CANDIDATE=0,CHAMPION=1,PROBATION=2,RETIRED=3;
    private static final class Option {
        long id,born,blocks;int state,train,optionN,controlN,recalls,reverts,probationN;
        // Checkpoint 354 memories were trained on the OLD, interleaved
        // sensory layout. Compare them only in the layout they actually saw.
        int visionSchemaV366=366;
        final float[] context=new float[CONTEXT],policy=new float[5];
        double optionSum,controlSum,optionSq,controlSq,optionEnergy,controlEnergy,optionHarm,controlHarm;
    }
    private final ArrayList<Option> options=new ArrayList<>();
    private final ArrayList<long[]> retiredHistory=new ArrayList<>();
    private final float[] valueWeights=new float[CONTEXT+1];
    private long decisions,train,validation,observations,nextId=1,goalId,goalSince,goalInterruptions;
    private long historicalDecisions,historicalTrain,historicalValidation,historicalObservations,parentEpisode;
    private final long[] repairs=new long[3];
    private float lastEnergy,lastHarm,lastValue,informationProgress,previousPredictionError=Float.NaN;
    private long lastPredictionValidation;
    private int active=-1,goalAge;
    private String ecology=new ShelterEcology().encode();
    private float nextRoom=150f;
    private boolean pending,usedMemory;
    // Transient snapshot only: incomplete Android frames cannot become
    // remembered or counted options when app pauses or original ZERO dies.
    private long abortGoalId,abortGoalSince;
    private int abortGoalAge,abortRecallIndex=-1,abortRecallCount;
    private String abortReason;
    private float[] pendingContext,pendingLegacyContextV354,pendingV361ContextV366,pendingPolicy;
    private String reason="Nessuna intenzione osservata";
    private static float clamp(float x){return Math.max(-1,Math.min(1,x));}
    private static float[] context(float[] x){
        // Physical spectral8 is grouped [frequency][azimuth], not interleaved.
        // Keep OLD 12 features IDENTICAL; append frequency-specific horizontal
        // bearings without reinterpreting old weights or old saved options.
        return Zero3VisionContextV366.fromSensory194(x);
    }
    private static float[] legacyContextV354(float[] x){
        float[] c=new float[CONTEXT];
        for(int frequency=0;frequency<3;frequency++)
            for(int azimuth=0;azimuth<8;azimuth++)
                c[frequency]+=x[20+azimuth*3+frequency]/8f;
        for(int azimuth=0;azimuth<8;azimuth++)
            for(int frequency=0;frequency<3;frequency++)
                c[3]+=(azimuth<4?1f:-1f)*x[20+azimuth*3+frequency]/24f;
        for(int i=0;i<4;i++)c[4+i]=(x[i*2]+x[i*2+1])*.5f;
        c[8]=x[188];c[9]=x[189];c[10]=x[190];
        c[11]=(x[191]+x[192])*.5f;
        return c;
    }
    private static float[] contextForOptionV366(Option o,float[] current,float[] legacy,float[] old361){
        return o.visionSchemaV366==354?legacy:o.visionSchemaV366==361?old361:current;
    }
    /** Previously archived 12D options have NO evidence about which
     * of two different wavelengths lay on which opposite horizontal side.
     * In such scenes automatic old-option recall could replay the opposite
     * policy. Quarantine only this genuine information-loss situation:
     * nothing is deleted, same-colour scenes and new 15D options are valid.
     * This is NOT a rule about blue or red being good or bad.
     */
    static boolean mirroredOldContextRiskV366(float[] expanded){
        if(expanded==null||expanded.length!=CONTEXT)
            throw new IllegalArgumentException("V366 context size");
        boolean east=false,west=false;
        for(int channel=PRIOR_CONTEXT;channel<CONTEXT;channel++){
            if(expanded[channel]>.015f)east=true;
            if(expanded[channel]<-.015f)west=true;
        }
        return east&&west;
    }
    private static float distanceOptionV366(float[] observed,Option option){
        int matched=option.visionSchemaV366==366?CONTEXT:PRIOR_CONTEXT;
        float error=0;
        for(int i=0;i<matched;i++){
            float d=observed[i]-option.context[i];
            error+=d*d;
        }
        return error/matched; // preserve original V354/361 match thresholds
    }
    private static float distance(float[] a,float[] b){float d=0;for(int i=0;i<CONTEXT;i++){float v=a[i]-b[i];d+=v*v;}return d/CONTEXT;}
    private float value(float[] c){float a=valueWeights[CONTEXT];for(int i=0;i<CONTEXT;i++)a+=c[i]*valueWeights[i];return clamp(a);}
    /** Called by the sole actor after its own proposal. Returned option must be authorized there. */
    float[] propose(float[] observation,float[] actorProposal,long decision,boolean observe){
        if(pending||decision!=decisions)throw new IllegalStateException("V354 decision cursor");
        abortGoalId=goalId;abortGoalSince=goalSince;
        abortGoalAge=goalAge;abortReason=reason;abortRecallIndex=-1;
        float[] c=context(observation);
        float[] legacy=legacyContextV354(observation);
        float[] old361=Zero3VisionContextV366.originalV361Padded(observation);
        active=-1;usedMemory=false;lastValue=value(c);
        float nearest=.12f;
        for(int i=0;i<options.size();i++){
            Option o=options.get(i);
            // Historical V354/361 12D contexts conflate different source
            // signatures on opposite sides. The original learned memory
            // remains intact, but cannot authorize ambiguous mirror recall.
            if(o.visionSchemaV366!=366&&mirroredOldContextRiskV366(c))continue;
            float d=distanceOptionV366(contextForOptionV366(o,c,legacy,old361),o);
            if(o.state==RETIRED||o.train<4||d>=nearest)continue;
            boolean probe=Zero3ConfigurationV354.heldout(decision)&&((decision/15)&1)==1;
            boolean recall=!Zero3ConfigurationV354.heldout(decision)&&lastHarm==0&&(o.state==CHAMPION||o.state==PROBATION)&&o.optionN>0&&
                o.optionEnergy/o.optionN>=0&&o.optionSum/o.optionN>o.controlSum/Math.max(1,o.controlN);
            if(probe||recall){active=i;nearest=d;}
        }
        float[] chosen=actorProposal.clone();
        if(active>=0){
            Option o=options.get(active);System.arraycopy(o.policy,0,chosen,0,5);usedMemory=true;
            abortRecallIndex=active;abortRecallCount=o.recalls;
            o.recalls++;if(goalId==o.id){goalAge++;reason=observe?"Richiamo osservato; apprendimento congelato":"Richiamo contestuale: vantaggio misurato";}
            else{goalId=o.id;goalSince=decision;goalAge=1;reason=o.state==CANDIDATE?"Prova candidata su dati riservati":"Opzione convalidata richiamata";}
        }else{goalId=0;goalSince=decision;goalAge=1;reason="Nuova intenzione della politica GRU";}
        pending=true;pendingContext=c;pendingLegacyContextV354=legacy;pendingV361ContextV366=old361;
        pendingPolicy=chosen.clone();return chosen;
    }
    /** No reward, no decision, no chosen option on an interrupted physics frame. */
    void abort(){
        if(!pending)return;
        if(abortRecallIndex>=0&&abortRecallIndex<options.size())
            options.get(abortRecallIndex).recalls=abortRecallCount;
        goalId=abortGoalId;goalSince=abortGoalSince;
        goalAge=abortGoalAge;reason=abortReason;
        pending=false;pendingContext=null;pendingLegacyContextV354=null;pendingV361ContextV366=null;pendingPolicy=null;
        active=-1;usedMemory=false;abortRecallIndex=-1;
    }
    Zero3ExecutiveV354 inherited(long parent)throws IOException{
        Zero3ExecutiveV354 e=decode(encode());e.historicalDecisions+=e.decisions;e.historicalTrain+=e.train;e.historicalValidation+=e.validation;e.historicalObservations+=e.observations;
        e.decisions=e.train=e.validation=e.observations=0;e.goalId=e.goalSince=e.goalInterruptions=0;e.goalAge=0;e.parentEpisode=parent;
        e.lastHarm=e.lastEnergy=e.lastValue=e.informationProgress=0;e.previousPredictionError=Float.NaN;e.lastPredictionValidation=0;
        ShelterEcology old=new ShelterEcology();if(!old.restore(e.ecology))throw new IOException("V354 heritage ecology");old.newLife();e.ecology=old.encode();e.nextRoom=150f;
        Arrays.fill(e.repairs,0);e.reason="Nuova vita: pesi e opzioni ereditati, memoria temporale azzerata";return e;
    }
    long[] repairs(){return repairs.clone();}
    void repairs(long[] data){System.arraycopy(data,0,repairs,0,3);}
    String ecology(){return ecology;}
    float nextRoom(){return nextRoom;}
    void ecology(String state,float next){if(pending)throw new IllegalStateException("V354 ecology boundary");ecology=state;nextRoom=next;}
    long selectedId(){return active<0?0:options.get(active).id;}
    boolean usedMemory(){return usedMemory;}
    void complete(float motorScore,float realEnergy,float harm,float cost,boolean heldout,boolean observe,
                  Zero3CognitionV353.Snapshot prediction){complete(motorScore,realEnergy,harm,cost,heldout,observe,prediction,true);}
    void complete(float motorScore,float realEnergy,float harm,float cost,boolean heldout,boolean observe,
                  Zero3CognitionV353.Snapshot prediction,boolean motorEvidence){
        if(!pending||!Float.isFinite(motorScore)||!Float.isFinite(realEnergy)||!Float.isFinite(harm)||harm<0||!Float.isFinite(cost)||cost<0)
            throw new IllegalStateException("V354 missing/invalid physical outcome");
        motorScore=clamp(motorScore);pending=false;
        lastEnergy=realEnergy;lastHarm=harm;
        if(observe){
            observations++;if(harm>0||goalAge>=4){goalInterruptions++;goalId=0;goalAge=0;reason=harm>0?"Interruzione osservata: danno fisico":"Nuova valutazione in osservazione";}
            decisions++;active=-1;return;
        }
        if(heldout){
            validation++;
            for(int i=0;i<options.size();i++){
                Option o=options.get(i);if(!motorEvidence||o.state==RETIRED||
                    (o.visionSchemaV366!=366&&mirroredOldContextRiskV366(pendingContext))||
                    distanceOptionV366(contextForOptionV366(o,pendingContext,pendingLegacyContextV354,pendingV361ContextV366),o)>.12f)continue;
                if(i==active){o.optionN++;o.optionSum+=motorScore;o.optionSq+=motorScore*motorScore;o.optionEnergy+=realEnergy;o.optionHarm+=harm;}
                else if(active==-1){o.controlN++;o.controlSum+=motorScore;o.controlSq+=motorScore*motorScore;o.controlEnergy+=realEnergy;o.controlHarm+=harm;}
                else continue;
                o.blocks|=1L<<(((historicalDecisions+decisions)/64)%63);
            }
            if(prediction!=null&&prediction.heldout>lastPredictionValidation){
                float e=0;int count=0;
                for(float f:prediction.mse)if(Float.isFinite(f)){e+=f;count++;}
                if(count>0){e/=count;informationProgress=Float.isFinite(previousPredictionError)?Math.max(0,previousPredictionError-e):0;
                    previousPredictionError=e;lastPredictionValidation=prediction.heldout;}
            }
        }else{
            train++;
            float target=clamp(realEnergy/20),err=clamp(target-lastValue);
            for(int i=0;i<CONTEXT;i++)valueWeights[i]=clamp(valueWeights[i]+.005f*err*pendingContext[i]);
            valueWeights[CONTEXT]=clamp(valueWeights[CONTEXT]+.005f*err);
            for(Option o:options)if(motorEvidence&&o.state!=RETIRED&&
                (o.visionSchemaV366==366||!mirroredOldContextRiskV366(pendingContext))&&
                distanceOptionV366(contextForOptionV366(o,pendingContext,pendingLegacyContextV354,pendingV361ContextV366),o)<.12f&&
                o.policy[0]*pendingPolicy[0]+o.policy[1]*pendingPolicy[1]>.8f)o.train++;
            if(motorEvidence&&train%32==0&&(options.size()<LIMIT||hasRetired())&&motorScore>0&&harm==0){
                if(options.size()==LIMIT)for(int i=0;i<options.size();i++)if(options.get(i).state==RETIRED){
                    Option old=options.remove(i);retiredHistory.add(new long[]{old.id,old.born,old.train,old.optionN,old.controlN,old.reverts});
                    if(retiredHistory.size()>64)retiredHistory.remove(0);break;
                }
                Option o=new Option();o.id=nextId++;o.born=historicalDecisions+decisions;o.train=1;o.visionSchemaV366=366;
                System.arraycopy(pendingContext,0,o.context,0,CONTEXT);System.arraycopy(pendingPolicy,0,o.policy,0,5);options.add(o);
            }
            // Lifecycle changes occur only on TRAIN boundaries, never on validation/observe.
            for(Option o:options)if(o.state!=RETIRED&&o.optionN>=8&&o.controlN>=8&&Long.bitCount(o.blocks)>=2){
                double a=o.optionSum/o.optionN,b=o.controlSum/o.controlN;
                double variance=Math.max(0,o.optionSq/o.optionN-a*a)/o.optionN+
                    Math.max(0,o.controlSq/o.controlN-b*b)/o.controlN;
                boolean better=a-b>Math.max(.01,2*Math.sqrt(variance))&&o.optionHarm/o.optionN<=o.controlHarm/o.controlN+.001&&
                    o.optionEnergy/o.optionN>=o.controlEnergy/o.controlN-.02;
                if(o.state==CANDIDATE&&!better&&o.optionN>=16&&o.controlN>=16){o.state=RETIRED;o.reverts++;}
                else if(o.state==CANDIDATE&&better){o.state=PROBATION;o.probationN=o.optionN+o.controlN;}
                else if(o.state==PROBATION&&o.optionN+o.controlN>=o.probationN+16){
                    if(better)o.state=CHAMPION;else{o.state=RETIRED;o.reverts++;}
                }else if(o.state==CHAMPION&&a<b-.02){o.state=RETIRED;o.reverts++;}
            }
        }
        if(harm>0||goalAge>=4){goalInterruptions++;goalId=0;goalAge=0;reason=harm>0?"Interruzione: danno fisico osservato":"Nuova valutazione dell'opzione temporale";}
        decisions++;active=-1;
    }
    private boolean hasRetired(){for(Option o:options)if(o.state==RETIRED)return true;return false;}
    public static final class Skill {
        public final long id,born;public final int state,train,validation,controls,recalls,reverts;
        public final float score,baseline,energy;
        Skill(Option o){id=o.id;born=o.born;state=o.state;train=o.train;validation=o.optionN;controls=o.controlN;recalls=o.recalls;reverts=o.reverts;
            score=o.optionN==0?Float.NaN:(float)(o.optionSum/o.optionN);baseline=o.controlN==0?Float.NaN:(float)(o.controlSum/o.controlN);
            energy=o.optionN==0?Float.NaN:(float)(o.optionEnergy/o.optionN);}
        public String status(){return new String[]{"CANDIDATA","CONSOLIDATA ONLINE","PROBATION A/B","SCARTATA"}[state];}
    }
    public static final class Snapshot {
        public final long decisions,train,validation,observations,goalId,goalSince,interruptions,parentEpisode,historicalDecisions;
        public final float value,lastEnergy,lastHarm,informationProgress;public final String reason;public final Skill[] skills;public final long[][] retiredHistory;
        Snapshot(Zero3ExecutiveV354 e){parentEpisode=e.parentEpisode;historicalDecisions=e.historicalDecisions;decisions=e.decisions;train=e.train;validation=e.validation;observations=e.observations;
            goalId=e.goalId;goalSince=e.goalSince;interruptions=e.goalInterruptions;value=e.lastValue;lastEnergy=e.lastEnergy;lastHarm=e.lastHarm;
            informationProgress=e.informationProgress;reason=e.reason;retiredHistory=new long[e.retiredHistory.size()][];for(int i=0;i<retiredHistory.length;i++)retiredHistory[i]=e.retiredHistory.get(i).clone();skills=new Skill[e.options.size()];for(int i=0;i<skills.length;i++)skills[i]=new Skill(e.options.get(i));}
    }
    void appendGraphV354(Zero3NeuralGraphV354.Builder b){
        int id=b.node(10,0,CONTEXT+1,"valore contestuale",lastValue,valueWeights,0,valueWeights.length);
        for(int i=0;i<valueWeights.length;i++)b.edge(-2200-i,id,0,valueWeights[i]);
    }
    public Snapshot snapshot(){return new Snapshot(this);}
    byte[] encode()throws IOException{return encodeVersionV366(366);}
    /** QA ONLY: generate an actual prior V361-compatible binary with old
     * 12D context, 12 old weights + preserved historic bias. The legacy
     * format cannot express newly created V366 memories. Never used when
     * saving a normal game or named brain.
     */
    byte[] encodePriorV361ForQA()throws IOException{return encodeVersionV366(361);}
    byte[] encodePriorV354ForQA()throws IOException{return encodeVersionV366(354);}
    private byte[] encodeVersionV366(int targetSchema)throws IOException{
        if(targetSchema!=354&&targetSchema!=361&&targetSchema!=366)
            throw new IOException("V366 unsupported target schema");
        if(targetSchema!=366)
            for(Option o:options)if(o.visionSchemaV366==366||
                (targetSchema==354&&o.visionSchemaV366!=354))
                throw new IOException("Cannot downconvert a visual-memory option to an earlier learned schema");
        if(pending)throw new IOException("V354 pending option");
        ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream d=new DataOutputStream(b);d.writeInt(targetSchema);
        for(long v:new long[]{decisions,train,validation,observations,nextId,goalId,goalSince,goalInterruptions,lastPredictionValidation})d.writeLong(v);
        for(long h:new long[]{historicalDecisions,historicalTrain,historicalValidation,historicalObservations,parentEpisode,repairs[0],repairs[1],repairs[2]})d.writeLong(h);
        d.writeInt(goalAge);for(float f:new float[]{lastEnergy,lastHarm,lastValue,informationProgress,previousPredictionError})d.writeFloat(f);
        d.writeUTF(reason);d.writeUTF(ecology);d.writeFloat(nextRoom);
        if(targetSchema!=366){
            for(int i=0;i<PRIOR_CONTEXT;i++)d.writeFloat(valueWeights[i]);
            d.writeFloat(valueWeights[CONTEXT]); // previous scalar bias
        }else for(float f:valueWeights)d.writeFloat(f);
        d.writeInt(options.size());
        for(Option o:options){d.writeLong(o.id);d.writeLong(o.born);d.writeLong(o.blocks);
            for(int v:new int[]{o.state,o.train,o.optionN,o.controlN,o.recalls,o.reverts,o.probationN})d.writeInt(v);
            for(int j=0;j<(targetSchema==366?CONTEXT:PRIOR_CONTEXT);j++)d.writeFloat(o.context[j]);
            for(float f:o.policy)d.writeFloat(f);
            for(double v:new double[]{o.optionSum,o.controlSum,o.optionSq,o.controlSq,o.optionEnergy,o.controlEnergy,o.optionHarm,o.controlHarm})d.writeDouble(v);
            if(targetSchema==361)d.writeBoolean(o.visionSchemaV366==354);
            else if(targetSchema==366)d.writeInt(o.visionSchemaV366);
            // Real V354 had no option-provenance byte.
        }
        d.writeInt(retiredHistory.size());for(long[] history:retiredHistory)for(long f:history)d.writeLong(f);
        d.flush();return b.toByteArray();
    }
    private static float finite(DataInputStream d)throws IOException{float f=d.readFloat();if(!Float.isFinite(f))throw new IOException("V354 nonfinite");return f;}
    static Zero3ExecutiveV354 decode(byte[] raw)throws IOException{
        if(raw==null||raw.length<100||raw.length>120000)throw new IOException("V354 executive size");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(raw));
        int schema=d.readInt();
        if(schema!=354&&schema!=361&&schema!=366)throw new IOException("V366 executive schema");
        Zero3ExecutiveV354 e=new Zero3ExecutiveV354();long[] v=new long[9];for(int i=0;i<v.length;i++){v[i]=d.readLong();if(v[i]<0||v[i]>100000000)throw new IOException("V354 counter");}
        e.decisions=v[0];e.train=v[1];e.validation=v[2];e.observations=v[3];e.nextId=v[4];e.goalId=v[5];e.goalSince=v[6];e.goalInterruptions=v[7];e.lastPredictionValidation=v[8];
        if(e.decisions!=e.train+e.validation+e.observations||e.nextId<1||e.goalSince>e.decisions)throw new IOException("V354 counters disagree");
        long[] historical=new long[8];for(int i=0;i<historical.length;i++){historical[i]=d.readLong();if(historical[i]<0)throw new IOException("V354 historical counter");}
        e.historicalDecisions=historical[0];e.historicalTrain=historical[1];e.historicalValidation=historical[2];e.historicalObservations=historical[3];e.parentEpisode=historical[4];System.arraycopy(historical,5,e.repairs,0,3);
        if(e.historicalDecisions!=e.historicalTrain+e.historicalValidation+e.historicalObservations||e.repairs[1]+e.repairs[2]>e.repairs[0])throw new IOException("V354 heritage/repair evidence");
        e.goalAge=d.readInt();if(e.goalAge<0||e.goalAge>4)throw new IOException("V354 goal age");
        e.lastEnergy=finite(d);e.lastHarm=finite(d);e.lastValue=finite(d);e.informationProgress=finite(d);e.previousPredictionError=d.readFloat();
        if((!Float.isNaN(e.previousPredictionError)&&!Float.isFinite(e.previousPredictionError))||e.lastHarm<0||e.informationProgress<0)throw new IOException("V354 value evidence");
        e.reason=d.readUTF();if(e.reason.length()>180)throw new IOException("V354 reason size");
        e.ecology=d.readUTF();e.nextRoom=finite(d);if(e.nextRoom<0||!new ShelterEcology().restore(e.ecology))throw new IOException("V354 invalid ecology");
        int oldWidth=schema==366?CONTEXT:PRIOR_CONTEXT;
        for(int i=0;i<oldWidth;i++)e.valueWeights[i]=finite(d);
        e.valueWeights[CONTEXT]=finite(d); // historic bias was index 12, now index 15.
        int n=d.readInt();if(n<0||n>LIMIT)throw new IOException("V354 option capacity");
        long last=0;
        for(int i=0;i<n;i++){
            Option o=new Option();o.id=d.readLong();o.born=d.readLong();o.blocks=d.readLong();
            if(o.id<=last||o.id>=e.nextId||o.born<0||o.born>=e.historicalDecisions+e.decisions)throw new IOException("V354 option identity");last=o.id;
            o.state=d.readInt();o.train=d.readInt();o.optionN=d.readInt();o.controlN=d.readInt();o.recalls=d.readInt();o.reverts=d.readInt();o.probationN=d.readInt();
            if(o.state<0||o.state>3||o.train<1||o.optionN<0||o.controlN<0||o.recalls<0||o.reverts<0||o.probationN<0||o.optionN>e.validation+e.historicalValidation||o.controlN>e.validation+e.historicalValidation)throw new IOException("V354 option evidence");
            for(int j=0;j<oldWidth;j++)o.context[j]=finite(d);for(int j=0;j<5;j++)o.policy[j]=finite(d);
            double[] a=new double[8];for(int j=0;j<a.length;j++){a[j]=d.readDouble();if(!Double.isFinite(a[j]))throw new IOException("V354 option moments");}
            o.optionSum=a[0];o.controlSum=a[1];o.optionSq=a[2];o.controlSq=a[3];o.optionEnergy=a[4];o.controlEnergy=a[5];o.optionHarm=a[6];o.controlHarm=a[7];
            if(o.optionSq<0||o.controlSq<0||o.optionHarm<0||o.controlHarm<0)throw new IOException("V354 option signs");
            if(schema==354)o.visionSchemaV366=354;
            else if(schema==361)o.visionSchemaV366=d.readBoolean()?354:361;
            else {o.visionSchemaV366=d.readInt();
                if(o.visionSchemaV366!=354&&o.visionSchemaV366!=361&&o.visionSchemaV366!=366)
                    throw new IOException("V366 option vision provenance");}
            e.options.add(o);
        }
        int history=d.readInt();if(history<0||history>64)throw new IOException("V354 history size");
        for(int i=0;i<history;i++){long[] record=new long[6];for(int j=0;j<record.length;j++){record[j]=d.readLong();if(record[j]<0)throw new IOException("V354 history value");}e.retiredHistory.add(record);}
        if(d.available()!=0)throw new IOException("V354 trailing bytes");return e;
    }
}
