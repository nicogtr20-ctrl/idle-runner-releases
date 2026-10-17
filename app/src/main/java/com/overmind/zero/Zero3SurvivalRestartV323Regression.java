package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Locale;

/**
 * V3.23: same-life 48→96 restart carrying the original full 322B experience
 * tape, whole physics world, live fixture RNG AND measured metabolism.
 * Independent JVM runs; production GameActivity remains untouched.
 */
public final class Zero3SurvivalRestartV323Regression {
    static final int MODE=Integer.parseInt(System.getProperty("zero3.mode","1"));
    static final int ORDINAL=Integer.parseInt(System.getProperty("zero3.ordinal","36"));
    private Zero3SurvivalRestartV323Regression(){}
    static void ok(boolean v,String message){
        if(!v)throw new AssertionError("ZERO3 V323 "+message);
    }
    static Zero3SurvivalLedgerV323 virginMetabolism(){
        float initial=60f+(ORDINAL%7)*3f;
        // Deliberately start close to the LIVE GameActivity scarcity boundary
        // so actual simulated time must cross 70 seconds in this scenario.
        return new Zero3SurvivalLedgerV323(initial,68.7f,68.7f,false);
    }
    static void placeRealMaterialContacts(Zero3MultiSceneRestartV322BRegression.Session s){
        // This is a controlled collision fixture, not an actor reward.
        // Source/hazard is located on a REAL piece of membrane at birth.
        int kind=ORDINAL%4;
        if(kind!=0&&kind!=1)return;
        int patch=24+(ORDINAL%4)*7;
        float x=s.p.skin.x[patch],y=s.p.skin.y[patch];
        int law=kind==0?0:1;
        s.p.world.add(new UnknownWorld.Entity(x,y,kind==0?0:1,law));
    }
    static final class Case {
        final Zero3MultiSceneRestartV322BRegression.Session session;
        final Zero3SurvivalLedgerV323 energy;
        Case(Zero3MultiSceneRestartV322BRegression.Session session,
             Zero3SurvivalLedgerV323 energy){
            this.session=session;this.energy=energy;
        }
        Zero3SurvivalArchiveV323.Bundle snapshot()throws IOException{
            return new Zero3SurvivalArchiveV323.Bundle(session.save(),energy.checkpoint());
        }
        void run(int target){
            Zero3MultiSceneRestartV322BRegression.run(session,target,
                new Zero3MultiSceneRestartV322BRegression.PhysicsObserver(){
                    public void beforeDecision(int n,long tick){energy.begin(n,tick);}
                    public void afterPhysics(float dt,float actualWork,
                            Zero3OrganismStoreV322.Physical p){
                        energy.physicalStep(dt,actualWork,p.body,p.skin,p.world);
                    }
                    public void afterDecision(int n,long tick){energy.finish(n,tick);}
                });
            ok(energy.decisions()==session.next,"metabolism and neural cursors diverged");
        }
        String evidence()throws Exception{
            Zero3SurvivalArchiveV323.Bundle b=snapshot();
            Zero3SurvivalLedgerV323.State state=b.survival;
            ok(state.next==96&&state.scarcity&&state.elapsed>70,
                "did not cross actual scarcity time threshold");
            // Costs are computed on the actual real physics work per substep.
            double actualWork=0;
            for(Zero3OrganismStoreV322.Trace t:session.tape)
                actualWork+=Double.longBitsToDouble(t.workBits);
            return "jointSurvivalFingerprint="+Zero3SurvivalArchiveV323.fingerprint(b)+
                "\nfullRecords="+b.joint.experienceCount()+
                "\nscene="+MODE+" ordinal="+ORDINAL+
                "\nnextDecision="+state.next+
                "\nworldRng="+session.worldRng+
                "\nbodyHash="+b.joint.organism.materialHash()+
                "\nreserveBits="+Float.floatToRawIntBits(state.reserve)+
                "\nscarcity="+state.scarcity+
                "\npositiveTouches="+state.positive+
                "\nnegativeTouches="+state.negative+
                "\nconsumedSources="+state.consumed+
                "\nbasalSpent="+String.format(Locale.ROOT,"%.8f",state.basal)+
                "\nmotorFuel="+String.format(Locale.ROOT,"%.8f",state.motor)+
                "\nenergyGained="+String.format(Locale.ROOT,"%.8f",state.gained)+
                "\ncontactLoss="+String.format(Locale.ROOT,"%.8f",state.damage)+
                "\ntruePhysicsWork="+String.format(Locale.ROOT,"%.10f",actualWork)+
                "\nworldObjects="+session.p.world.size()+"\n";
        }
    }
    static Case newborn(){
        Zero3MultiSceneRestartV322BRegression.Session s=
            Zero3MultiSceneRestartV322BRegression.virgin();
        placeRealMaterialContacts(s);
        return new Case(s,virginMetabolism());
    }
    static Case resume(Zero3SurvivalArchiveV323.Bundle b)throws Exception{
        Zero3MultiSceneRestartV322BRegression.Session s=
            Zero3MultiSceneRestartV322BRegression.recovered(b.joint);
        Zero3SurvivalLedgerV323 e=virginMetabolism();e.restore(b.survival);
        return new Case(s,e);
    }
    static void write(Path p,String v)throws IOException{
        Files.write(p,v.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
    }
    static void prefix(Path root)throws Exception{
        Zero3SurvivalArchiveV323 store=new Zero3SurvivalArchiveV323(root);
        ok(store.load()==null,"prefix directory not empty");
        Case c=newborn();
        c.run(24);store.save(c.snapshot());
        c.run(48);store.save(c.snapshot());
        ok(store.generation()==2&&c.energy.decisions()==48,"partial metabolic archive");
        // No energy file may commit before a full physical decision.
        Zero3SurvivalLedgerV323 negative=virginMetabolism();
        negative.begin(0,0);
        boolean rejected=false;
        try{negative.checkpoint();}catch(IllegalStateException expected){rejected=true;}
        ok(rejected,"partial metabolism checkpoint was not rejected");
        System.out.println("ZERO3 V323 PREFIX cursor=48 gen=2");
    }
    static void restore(Path root,Path outfile)throws Exception{
        Zero3SurvivalArchiveV323 disk=new Zero3SurvivalArchiveV323(root);
        Zero3SurvivalArchiveV323.Bundle saved=disk.load();
        ok(saved!=null&&saved.survival.next==48&&
           disk.generation()==2,"mixed or missing checkpoint after restart");
        Case c=resume(saved);c.run(96);
        disk.save(c.snapshot());
        write(outfile,c.evidence());
        System.out.println("ZERO3 V323 RESUME cursor=96 gen="+disk.generation()+
            " positive="+c.energy.positiveTouches()+
            " negative="+c.energy.negativeTouches()+
            " scarcity="+c.energy.scarcity());
    }
    static void uninterrupted(Path out)throws Exception{
        Case c=newborn();c.run(96);write(out,c.evidence());
        System.out.println("ZERO3 V323 REFERENCE cursor=96 reserve="+c.energy.reserve());
    }
    static void damage(Path p)throws Exception{
        try(RandomAccessFile f=new RandomAccessFile(p.toFile(),"rw")){
            ok(f.length()>300,"short blob");f.seek(f.length()/3);
            f.writeByte(0x55);f.setLength(f.length()-13);f.getFD().sync();
        }
    }
    static void fault(Path root)throws Exception{
        Zero3SurvivalArchiveV323 a=new Zero3SurvivalArchiveV323(root);
        ok(a.load()!=null&&a.generation()==3,"no committed life to corrupt");
        damage(a.latestFile());
        Zero3SurvivalArchiveV323 recover=new Zero3SurvivalArchiveV323(root);
        Zero3SurvivalArchiveV323.Bundle prior=recover.load();
        ok(prior!=null&&recover.generation()==2&&
           prior.survival.next==48&&prior.joint.organism.nextDecision==48,
           "corruption replayed uncommitted neuron or energy");
        recover.save(prior);
        Zero3SurvivalArchiveV323 b=new Zero3SurvivalArchiveV323(root);
        ok(b.load()!=null&&b.generation()==3&&b.load().survival.next==48,
           "recommit invented energy or body transitions");
        Files.write(b.currentManifest(),new byte[]{1,2,3,4});
        Zero3SurvivalArchiveV323 fallback=new Zero3SurvivalArchiveV323(root);
        ok(fallback.load()!=null&&fallback.generation()==2,
           "invalid manifest recovered mixed state");
        damage(fallback.latestFile());damage(fallback.olderFile());
        ok(new Zero3SurvivalArchiveV323(root).load()==null,
           "both generations failed to close");
        System.out.println("ZERO3 V323 FAULT rollbackCursor=48 "+
            "mixedMetabolicGenerationRefused=true bothBadFailClosed=true");
    }
    public static void main(String[] args)throws Exception{
        ok(args.length>=2&&MODE>=0&&MODE<=4,"bad run arguments");
        Path dir=Paths.get(args[1]);
        switch(args[0]){
            case "prefix":prefix(dir);break;
            case "resume":restore(dir,Paths.get(args[2]));break;
            case "reference":uninterrupted(dir);break;
            case "fault":fault(dir);break;
            default:throw new IllegalArgumentException("invalid V323 mode");
        }
    }
}
