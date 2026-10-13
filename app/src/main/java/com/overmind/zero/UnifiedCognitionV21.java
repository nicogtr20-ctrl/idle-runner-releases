package com.overmind.zero;

import java.util.Locale;

/**
 * ZERO 2.1 / unified evidence ledger.
 *
 * ONE physically grounded action interval has ONE provenance record:
 * perceived own body + nearby geometry + provisional scene hypothesis,
 * freely chosen action, real external displacement, real energy contacts.
 *
 * It is intentionally a non-actuating shared cognitive state. An observed
 * consequence is NOT automatically caused by the nearest object or action.
 * No hidden game entity/type, resource location or grip legality is read.
 * Heldout intervals never update beliefs. Imagination never enters this log.
 *
 * Production-gate policy: neither this class nor its current integration
 * modifies action scores, rewards, physics or motor coefficients.
 */
public final class UnifiedCognitionV21 {
    public static final String KEY = "unified_cognition_v21";
    private static final String FORMAT = "uc21";
    public static final int SENSES = 26, ACTIONS = 16, CONTEXTS = 4;
    private static final int CELLS = CONTEXTS * ACTIONS;
    private static final long LIMIT = 1_000_000_000L;
    private final long[] attempts = new long[CELLS];
    private final long[] physical = new long[CELLS];
    private final long[] beneficial = new long[CELLS];
    private final long[] harmful = new long[CELLS];
    private final double[] energySum = new double[CELLS];
    private long actualIntervals, trainedIntervals, heldoutIntervals, invalidIntervals;
    private long actualEnergyContacts, actualPhysicalEvents;
    private double heldoutError, unchangedError;
    private int pendingCell = -1, pendingHypothesis = -1;
    private float pendingEnergy;
    private int pendingContacts;
    private Belief last = Belief.empty();

    /** A copy of sensor-derived beliefs, never a simulator-ground-truth object. */
    public static final class Belief {
        public final float reserve, touch, verifiedGrip, nearby, uncertainty;
        public final float worldConfidence, hypothesisConfidence;
        public final int provisionalHypothesis, context;
        private Belief(float e,float t,float g,float n,float u,
                       float w,float h,int id,int ctx){
            reserve=e;touch=t;verifiedGrip=g;nearby=n;uncertainty=u;
            worldConfidence=w;hypothesisConfidence=h;
            provisionalHypothesis=id;context=ctx;
        }
        private static Belief empty(){return new Belief(0,0,0,0,1,0,0,-1,0);}
    }
    private static boolean valid(float[] a,int n) {
        if(a==null||a.length!=n)return false;
        for(float f:a)if(!Float.isFinite(f)||Math.abs(f)>10000f)return false;
        return true;
    }
    private static float unit(float v){return Math.max(0f,Math.min(1f,v));}
    /**
     * Perceptual context is *only* the original 26 sensory values.
     * 0/1: reserve sufficient and no/yes near solid; 2/3: low reserve.
     * "Near" follows the physically observed rays, not entity identity.
     */
    public static int context(float[] sensed){
        if(!valid(sensed,SENSES))throw new IllegalArgumentException("sensory vector");
        float nearest=0;
        for(int i=6;i<14;i++)nearest=Math.max(nearest,unit(sensed[i]));
        return (sensed[0]<.35f?2:0)+(nearest>.30f||sensed[4]>.02f?1:0);
    }
    /** Call AFTER the original policy has already freely selected an action. */
    public void chosen(int action,float[] sensed,float[] rays,int hypothesis,
                       float worldTrust,float objectTrust){
        pendingCell=-1;pendingEnergy=0;pendingContacts=0;
        if(action<0||action>=ACTIONS||!valid(sensed,SENSES)||
           (rays!=null&&!valid(rays,16))||
           !Float.isFinite(worldTrust)||!Float.isFinite(objectTrust)||
           hypothesis < -1 || hypothesis>100000){
            invalidIntervals++;
            return;
        }
        float near=0;
        for(int i=6;i<14;i++)near=Math.max(near,unit(sensed[i]));
        int ctx=context(sensed);
        float uncertain=unit(1f-.5f*(unit(worldTrust)+unit(objectTrust)));
        last=new Belief(unit(sensed[0]),unit(sensed[4]),unit(sensed[5]),near,
            uncertain,unit(worldTrust),unit(objectTrust),hypothesis,ctx);
        pendingCell=ctx*ACTIONS+action;
        pendingHypothesis=hypothesis;
    }
    /** Only actual clamped contact energy, never ambient drain or imaginary gain. */
    public void energyContact(float realDelta){
        if(pendingCell<0)return;
        if(!Float.isFinite(realDelta)){invalidIntervals++;return;}
        if(realDelta!=0){
            pendingEnergy=Math.max(-1000f,Math.min(1000f,pendingEnergy+realDelta));
            pendingContacts++;
        }
    }
    /** One action -> one observation; physical displacement != a contact. */
    public void consequence(float[] observed,float externalDisplacement,
                            boolean train,boolean holdout){
        if(pendingCell<0)return;
        int cell=pendingCell;
        pendingCell=-1; // a second consequence must never double-count it
        if(!valid(observed,SENSES)||!Float.isFinite(externalDisplacement)||
           externalDisplacement<0f||externalDisplacement>100000f){
            invalidIntervals++;pendingContacts=0;pendingEnergy=0;return;
        }
        boolean moved=externalDisplacement>.015f;
        actualIntervals++;
        if(moved)actualPhysicalEvents++;
        actualEnergyContacts+=pendingContacts;
        if(holdout){
            heldoutIntervals++;
            // Out-of-training Brier loss, against no-external-change reference.
            double forecast=(physical[cell]+1.0)/(attempts[cell]+2.0);
            double actual=moved?1d:0d;
            heldoutError+=(forecast-actual)*(forecast-actual);
            unchangedError+=actual*actual;
        }else if(train){
            attempts[cell]++;
            if(moved)physical[cell]++;
            if(pendingEnergy>0)beneficial[cell]++;
            if(pendingEnergy<0)harmful[cell]++;
            energySum[cell]+=pendingEnergy;
            trainedIntervals++;
        }
        pendingContacts=0;pendingEnergy=0;pendingHypothesis=-1;
    }
    public void resetLife(){
        pendingCell=-1;pendingHypothesis=-1;pendingContacts=0;pendingEnergy=0;
        last=Belief.empty();
    }
    /** Restart never fabricates a completed action for an unfinished interval. */
    public void resumeLife(){resetLife();}
    public Belief lastBelief(){return last;}
    public long observations(){return actualIntervals;}
    public long training(){return trainedIntervals;}
    public long holdouts(){return heldoutIntervals;}
    public long invalid(){return invalidIntervals;}
    public long physicalEvents(){return actualPhysicalEvents;}
    public long energyContacts(){return actualEnergyContacts;}
    public long contextsObserved(){
        long c=0;for(long n:attempts)if(n>0)c++;return c;
    }
    /** Read-only, action/context-conditioned empirical statistics. No commands. */
    public long opportunities(int action,float[] sensory){
        if(action<0||action>=ACTIONS||!valid(sensory,SENSES))return 0;
        return attempts[context(sensory)*ACTIONS+action];
    }
    public float physicalProbability(int action,float[] sensory){
        long n=opportunities(action,sensory);
        if(n==0)return 0f;
        int c=context(sensory)*ACTIONS+action;
        return (float)((physical[c]+1d)/(n+2d));
    }
    public float netEnergyMean(int action,float[] sensory){
        long n=opportunities(action,sensory);
        if(n==0)return 0f;
        return (float)Math.max(-100d,Math.min(100d,
            energySum[context(sensory)*ACTIONS+action]/n));
    }
    public float harmfulRate(int action,float[] sensory){
        long n=opportunities(action,sensory);
        if(n==0)return 0f;
        return (float)(harmful[context(sensory)*ACTIONS+action]/(double)n);
    }
    public double heldoutBrier(){
        return heldoutIntervals==0?Double.NaN:heldoutError/heldoutIntervals;
    }
    public double noChangeBrier(){
        return heldoutIntervals==0?Double.NaN:unchangedError/heldoutIntervals;
    }
    /** Trust gate, not a policy weight. Requires real external-effect evidence. */
    public float confidence(){
        double e=heldoutBrier(),b=noChangeBrier();
        if(heldoutIntervals<80||actualPhysicalEvents<12||!Double.isFinite(e)||
           !Double.isFinite(b)||b<.01||e>=b*.92)return 0f;
        return unit((float)((b-e)/Math.max(.03,b)));
    }
    public String summary(){
        return String.format(Locale.US,
            "ZERO21 shared evidence: intervals=%d train=%d heldout=%d external=%d energy=%d contexts=%d Brier=%.5f unchanged=%.5f confidence=%.3f SHADOW",
            actualIntervals,trainedIntervals,heldoutIntervals,
            actualPhysicalEvents,actualEnergyContacts,contextsObserved(),
            heldoutBrier(),noChangeBrier(),confidence());
    }
    private static void array(StringBuilder sb,long[] a){
        for(long v:a)sb.append('|').append(v);
    }
    private static void array(StringBuilder sb,double[] a){
        for(double v:a)sb.append('|').append(Double.toString(v));
    }
    /** Bounded optional snapshot. Pending actions and identity are NOT serialized. */
    public String encode(){
        StringBuilder b=new StringBuilder(6000).append(FORMAT)
            .append('|').append(actualIntervals)
            .append('|').append(trainedIntervals)
            .append('|').append(heldoutIntervals)
            .append('|').append(invalidIntervals)
            .append('|').append(actualEnergyContacts)
            .append('|').append(actualPhysicalEvents)
            .append('|').append(Double.toString(heldoutError))
            .append('|').append(Double.toString(unchangedError));
        array(b,attempts);array(b,physical);array(b,beneficial);
        array(b,harmful);array(b,energySum);
        return b.toString();
    }
    /** Atomic fail-closed restore, no partial mutation on any corrupt field. */
    public boolean restore(String raw){
        if(raw==null||raw.length()==0)return false; // old save: virgin model
        if(raw.length()>20000)return false;
        try{
            String[] p=raw.split("\\|",-1);
            if(p.length!=9+CELLS*5||!FORMAT.equals(p[0]))return false;
            int j=1;
            long n=parseLong(p[j++]),t=parseLong(p[j++]),h=parseLong(p[j++]),
                 bad=parseLong(p[j++]),ec=parseLong(p[j++]),motion=parseLong(p[j++]);
            double he=parseDouble(p[j++]),base=parseDouble(p[j++]);
            if(t+h>n||motion>n||ec>LIMIT||he<0||base<0)return false;
            long[] a=new long[CELLS],m=new long[CELLS],
                pos=new long[CELLS],neg=new long[CELLS];
            double[] energy=new double[CELLS];
            long sum=0;
            for(int i=0;i<CELLS;i++)a[i]=parseLong(p[j++]);
            for(int i=0;i<CELLS;i++)m[i]=parseLong(p[j++]);
            for(int i=0;i<CELLS;i++)pos[i]=parseLong(p[j++]);
            for(int i=0;i<CELLS;i++)neg[i]=parseLong(p[j++]);
            for(int i=0;i<CELLS;i++)energy[i]=parseDouble(p[j++]);
            for(int i=0;i<CELLS;i++){
                if(m[i]>a[i]||pos[i]>a[i]||neg[i]>a[i]||
                   Math.abs(energy[i])>a[i]*1000d)return false;
                sum+=a[i];
            }
            if(sum!=t||he>h||base>h)return false;
            System.arraycopy(a,0,attempts,0,CELLS);
            System.arraycopy(m,0,physical,0,CELLS);
            System.arraycopy(pos,0,beneficial,0,CELLS);
            System.arraycopy(neg,0,harmful,0,CELLS);
            System.arraycopy(energy,0,energySum,0,CELLS);
            actualIntervals=n;trainedIntervals=t;heldoutIntervals=h;
            invalidIntervals=bad;actualEnergyContacts=ec;actualPhysicalEvents=motion;
            heldoutError=he;unchangedError=base;resetLife();return true;
        }catch(RuntimeException ex){return false;}
    }
    private static long parseLong(String s){
        long v=Long.parseLong(s);
        if(v<0||v>LIMIT)throw new IllegalArgumentException("counter");return v;
    }
    private static double parseDouble(String s){
        double v=Double.parseDouble(s);
        if(!Double.isFinite(v))throw new IllegalArgumentException("nonfinite");
        return v;
    }
}
