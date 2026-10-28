package com.overmind.zero;

/**
 * HUD-only totals for real collected energy contacts (not units of energy).
 * No classification is passed to ZERO's perception, cognition or policy.
 * Normal beneficial contacts = blue, harmful = red, physically rare pickups
 * = violet. Legacy positive contacts cannot be split into blue/violet
 * truthfully; retain them in an explicit unclassified historical bucket.
 */
public final class EnergyLifetimeCounters {
    public static final String KEY="energy_lifetime_types_v111";
    public long blue=0,red=0,violet=0,legacyPositive=0;
    public boolean complete=true;
    private static long safe(long x){return Math.max(0,x);}
    public void record(float actualEnergyDelta,boolean rare){
        if(!Float.isFinite(actualEnergyDelta))return;
        if(actualEnergyDelta>0){
            if(rare)violet++;else blue++;
        }else if(actualEnergyDelta<0)red++;
    }
    /**
     * V409: a count of real touched MATERIAL sources is not the same as
     * a delta of the physiological reserve after its [0,100] saturation.
     * A blue/violet source is actually collected even when reserve=100,
     * and a physically touched red source remains a genuine red contact
     * when an earlier same-frame contact has already exhausted reserve.
     *
     * IMPORTANT: this is HUD/QA book-keeping ONLY. Never feed a raw
     * hypothetical energy increase to GRU3, mortality or its 194 senses:
     * all of those continue to consume the original CLAMPED real delta.
     */
    public void recordPhysicalContact(float resolvedTouchLawDelta,boolean rare){
        // The law's signed effect is returned by an actually invoked
        // UnknownWorld.Entity.touch(), not an invented or projected reward.
        record(resolvedTouchLawDelta,rare);
    }
    private static long lifeTotal(LifeStatistics stats,int metric){
        if(stats==null)return 0;
        double mean=stats.lifetimeAverage(metric);
        double closed=Double.isFinite(mean)?mean*stats.lifetimeCount():0;
        double current=stats.current().value(metric);
        if(!Double.isFinite(closed)||!Double.isFinite(current))return 0;
        return safe(Math.round(closed+current));
    }
    public void migrate(LifeStatistics stats){
        blue=red=violet=legacyPositive=0;
        if(stats==null){complete=false;return;}
        legacyPositive=lifeTotal(stats,LifeStatistics.GOOD);
        // Negative resource contact is signature 1 under existing laws.
        red=lifeTotal(stats,LifeStatistics.HARM);
        complete=stats.lifetimeComplete()&&!stats.partial();
    }
    public String encode(){
        return "elt1|"+blue+"|"+red+"|"+violet+"|"+legacyPositive+"|"+(complete?1:0);
    }
    /** Parse fully before touching fields, including old named profiles. */
    public boolean restore(String raw){
        if(raw==null||raw.length()>160||!raw.startsWith("elt1|"))return false;
        try{
            String[] a=raw.split("\\|",-1);
            if(a.length!=6||(!"0".equals(a[5])&&!"1".equals(a[5])))return false;
            long b=Long.parseLong(a[1]),r=Long.parseLong(a[2]),
                 v=Long.parseLong(a[3]),u=Long.parseLong(a[4]);
            if(b<0||r<0||v<0||u<0)return false;
            blue=b;red=r;violet=v;legacyPositive=u;complete="1".equals(a[5]);
            return true;
        }catch(RuntimeException ignored){return false;}
    }
    public static void main(String[] args){
        EnergyLifetimeCounters t=new EnergyLifetimeCounters();
        LifeStatistics stats=new LifeStatistics();
        stats.contact(12);stats.contact(-7);stats.death();
        // A real completed life is necessary for lifetime aggregation.
        stats.tick(1);stats.contact(12);stats.contact(-7);stats.death();
        stats.tick(1);stats.contact(40);
        t.migrate(stats);
        if(t.legacyPositive!=2||t.red!=1||!t.complete)
            throw new AssertionError("Legacy cumulative contacts must be migrated");
        t.record(11,false);t.record(-7,false);t.record(35,true);
        t.record(0,true);t.record(Float.NaN,true);
        if(t.blue!=1||t.red!=2||t.violet!=1||t.legacyPositive!=2)
            throw new AssertionError("Real blue/red/rare source count, no duplicate at zero");
        String saved=t.encode();
        EnergyLifetimeCounters copy=new EnergyLifetimeCounters();
        if(!copy.restore(saved)||!saved.equals(copy.encode()))
            throw new AssertionError("Persistent counters round-trip");
        if(copy.restore("elt1|1|-1|0|0|1")||!copy.encode().equals(saved))
            throw new AssertionError("Invalid record corrupted valid counters");
        EnergyLifetimeCounters newborn=new EnergyLifetimeCounters();
        newborn.migrate(new LifeStatistics());
        if(newborn.blue!=0||newborn.red!=0||newborn.violet!=0||
           newborn.legacyPositive!=0)
            throw new AssertionError("A virgin ZERO inherited historical energy");
        System.out.println("ZERO HUD energy counters PASS");
    }
}
