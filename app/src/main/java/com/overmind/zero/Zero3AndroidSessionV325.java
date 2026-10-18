package com.overmind.zero;

import android.content.Context;
import java.io.IOException;
import java.nio.file.*;

/**
 * Explicit opt-in Android lifecycle adapter for the V3.25 research runtime.
 * No Activity registers this by default, and no 2.2 preference, named slot,
 * GameSceneSnapshot, backup or OTA file is read or written.
 *
 * This is a separate V3 seam for a FUTURE debug/research screen and has not
 * yet passed an on-device crash/power-loss instrumented test.
 */
public final class Zero3AndroidSessionV325 implements AutoCloseable {
    private static final String ROOT="zero3-research-v325";
    private final Path directory;
    private Zero3RuntimeV325 session;
    private Zero3AndroidSessionV325(Path location){directory=location;}
    public static Zero3AndroidSessionV325 create(Context context,String slot)
            throws IOException {
        if(context==null||slot==null||!slot.matches("[a-zA-Z0-9_-]{1,24}"))
            throw new IOException("invalid V3 research slot");
        // getNoBackupFilesDir prevents automatic Android/cloud backup of
        // unvalidated V3 research state and isolates all 12 legacy slots.
        Path dir=context.getApplicationContext().getNoBackupFilesDir().toPath()
                .resolve(ROOT).resolve("v3-"+slot);
        return new Zero3AndroidSessionV325(dir);
    }
    public static Zero3ProfileStoreV354 profilesV354(Context context)throws IOException{
        return new Zero3ProfileStoreV354(context.getApplicationContext().getNoBackupFilesDir().toPath().resolve("zero3-profiles-v354"));
    }
    public static Zero3AndroidSessionV325 profileV354(Context context,int slot)throws IOException{
        return new Zero3AndroidSessionV325(profilesV354(context).profile(slot));
    }
    public synchronized void startNewCompletionV354(Zero3RuntimeV325 organism)throws IOException{
        if(session!=null||organism==null||organism.decisions()!=0||Zero3SegmentedArchiveV328.hasAny(directory))throw new IOException("V354 use an empty profile");
        organism.enableCompletionV354();startNew(organism);
    }
    public synchronized void startInheritedV354(Zero3RuntimeV325 organism,Zero3OrganismStoreV322.State parent)throws IOException{
        if(session!=null||organism==null||organism.decisions()!=0||Zero3SegmentedArchiveV328.hasAny(directory))throw new IOException("V354 empty child profile required");
        organism.inheritCompletionV354(parent);startNew(organism);
    }
    public synchronized Zero3DashboardSnapshotV354 dashboardV354(boolean graph)throws IOException{
        if(session==null)throw new IOException("Profilo non attivo");return session.dashboardV354(graph);
    }
    public synchronized void observationModeV354(boolean enabled)throws IOException{
        if(session==null)throw new IOException("Profilo non attivo");session.observationModeV354(enabled);
    }
    /** Reject a new life if any existing V3 checkpoint may contain history. */
    public synchronized void startNew(Zero3RuntimeV325 organism)throws IOException {
        if(session!=null||organism==null||organism.decisions()!=0)
            throw new IOException("new research life must be fresh");
        if(Files.exists(directory.resolve("v324-current.mf"))||
           Files.exists(directory.resolve("v324-backup.mf"))||
           Files.exists(directory.resolve("v324-room-0.bin"))||
           Files.exists(directory.resolve("v324-room-1.bin"))||
           Zero3SegmentedArchiveV328.hasAny(directory))
            throw new IOException("existing V3 research history requires explicit resume");
        organism.enableSegmented();
        session=organism;
    }
    /** Experimental GRU newborn; same isolated V3 folder, no legacy writes. */
    public synchronized void startNewGruResearch(Zero3RuntimeV325 organism)
            throws IOException {
        if(session!=null||organism==null||organism.decisions()!=0)
            throw new IOException("V348 requires a virgin organism");
        if(Files.exists(directory.resolve("v324-current.mf"))||
           Files.exists(directory.resolve("v324-backup.mf"))||
           Zero3SegmentedArchiveV328.hasAny(directory))
            throw new IOException("V348 existing history must be resumed");
        organism.enableGruResearchV347();
        startNew(organism);
    }
    public synchronized void startNewUnifiedResearch(Zero3RuntimeV325 organism)throws IOException {
        if(session!=null||organism==null||organism.decisions()!=0||
           Files.exists(directory.resolve("v324-current.mf"))||
           Files.exists(directory.resolve("v324-backup.mf"))||
           Zero3SegmentedArchiveV328.hasAny(directory))
            throw new IOException("V351 existing V3 history requires explicit resume");
        organism.enableUnifiedResearchV351();startNew(organism);
    }
    public synchronized void startNewVariableResearch(Zero3RuntimeV325 organism)throws IOException {
        if(session!=null||organism==null||organism.decisions()!=0||
           Files.exists(directory.resolve("v324-current.mf"))||
           Files.exists(directory.resolve("v324-backup.mf"))||
           Zero3SegmentedArchiveV328.hasAny(directory))
            throw new IOException("V352 existing V3 history requires explicit resume");
        organism.enableVariableResearchV352();startNew(organism);
    }
    public synchronized void startNewCognitiveResearch(Zero3RuntimeV325 organism)throws IOException {
        if(session!=null||organism==null||organism.decisions()!=0||
           Files.exists(directory.resolve("v324-current.mf"))||
           Files.exists(directory.resolve("v324-backup.mf"))||
           Zero3SegmentedArchiveV328.hasAny(directory))
            throw new IOException("V353 existing history requires explicit resume");
        organism.enableCognitiveResearchV353();startNew(organism);
    }
    public synchronized Zero3CognitionV353.Snapshot cognitionSnapshot(){
        return session==null?null:session.cognitionSnapshot();
    }
    public synchronized float lastDecisionSeconds(){return session==null?0f:session.lastDecisionSeconds();}
    /** App lifecycle onResume, never silently generate a replacement brain. */
    public synchronized void resume()throws IOException {
        if(session!=null)throw new IOException("V3 organism already active");
        session=Zero3RuntimeV325.loadSegmented(directory);
        if(session==null)
            throw new IOException("no valid complete V3 checkpoint; no reset performed");
    }
    /** Advances exactly one genuine neural-and-physics decision. */
    public synchronized void step()throws IOException {
        if(session==null)throw new IOException("V3 session is suspended");
        session.advanceOneDecision();
    }
    /**
     * App onPause/onStop at a completed decision only:
     * persist verified physical+neural+metabolic+room+replay generation.
     * On I/O failure keep session alive; never mark it saved by assumption.
     */
    public synchronized void suspend()throws IOException {
        if(session==null)return;
        session.saveSegmented(directory);
        session.close();
        session=null;
    }
    /** Real physics + neural counters captured only by the session owner. */
    public synchronized Zero3RuntimeV325.Display display()throws IOException {
        if(session==null)throw new IOException("V3 runtime not active");
        return session.display();
    }
    /** Read-only while the original Android painter owns the UI thread. */
    synchronized Zero3RuntimeV325.CanvasStateV355 canvasStateV355()throws IOException {
        if(session==null)throw new IOException("V3 organism not active");
        return session.canvasStateV355();
    }
    public synchronized boolean ended(){return session!=null&&session.ended();}
    public synchronized boolean atArchiveCapacity(){
        return session!=null&&session.atArchiveCapacity();
    }
    public synchronized int durableDecisionLimit(){
        return session==null?0:session.durableDecisionLimit();
    }
    public synchronized Path researchPath(){return directory;}
    public synchronized boolean active(){return session!=null;}
    public synchronized int decisions(){return session==null?0:session.decisions();}
    public synchronized float energy(){return session==null?Float.NaN:session.energy();}
    /** Any unsaved incomplete life is never claimed as durably persisted. */
    @Override public synchronized void close(){
        if(session!=null){session.close();session=null;}
    }
}
