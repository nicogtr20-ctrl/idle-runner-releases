package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * V3.29 bounded-resident historical causal journal.
 * The underlying V328 SHA-256 immutable chunk bytes are unchanged.
 *
 * Only ONE already-committed segment (max 32 experiences) and the
 * UNSAVED tail remain decoded in RAM. No learned experience is deleted:
 * all PRE/12x64/POST records stay accessible through checked reads.
 *
 * Appending is legal only at a finished decision boundary, and this class
 * is owned by the single-threaded V3 physical runtime. Not thread safe.
 */
public final class Zero3ReplayJournalV329
        extends AbstractList<Zero3FullCausalExperienceV322B> {
    private static final int CHUNK=Zero3SegmentedArchiveV328.CHUNK;
    private final Path dir;
    private final ArrayList<Zero3SegmentedArchiveV328.Segment> segments;
    private final int committedCount;
    private final ArrayList<Zero3FullCausalExperienceV322B> pending=
        new ArrayList<>();
    private int cachedStart=-1;
    private List<Zero3FullCausalExperienceV322B> cached=
        Collections.emptyList();
    private int peakResident;
    private long reads;

    Zero3ReplayJournalV329(Path root,
            List<Zero3SegmentedArchiveV328.Segment> parts,
            int count)throws IOException {
        if(root==null||parts==null||count<1||
           parts.size()!=(count+CHUNK-1)/CHUNK)
            throw new IOException("V329 incomplete replay reference");
        dir=root;segments=new ArrayList<>(parts);committedCount=count;
        for(int i=0;i<segments.size();i++){
            Zero3SegmentedArchiveV328.Segment s=segments.get(i);
            if(s.start!=i*CHUNK||
               s.count!=Math.min(CHUNK,count-i*CHUNK))
                throw new IOException("V329 unexpected segment boundary");
        }
    }
    @Override public int size(){return committedCount+pending.size();}
    @Override public Zero3FullCausalExperienceV322B get(int index){
        if(index<0||index>=size())throw new IndexOutOfBoundsException();
        if(index>=committedCount)return pending.get(index-committedCount);
        int chunk=index/CHUNK;
        if(cachedStart!=chunk*CHUNK){
            try{load(chunk);}catch(IOException bad){
                throw new UncheckedIOException("V329 verified segment unreadable",bad);
            }
        }
        return cached.get(index-cachedStart);
    }
    private static byte[] sha(byte[] bytes){
        try{return MessageDigest.getInstance("SHA-256").digest(bytes);}
        catch(Exception impossible){throw new IllegalStateException(impossible);}
    }
    private void load(int index)throws IOException {
        Zero3SegmentedArchiveV328.Segment s=segments.get(index);
        byte[] raw=Files.readAllBytes(dir.resolve(s.file()));
        if(raw.length!=s.size||!Arrays.equals(sha(raw),s.sha))
            throw new IOException("V329 historical segment changed");
        DataInputStream in=new DataInputStream(new ByteArrayInputStream(raw));
        if(in.readInt()!=Zero3SegmentedArchiveV328.MAGIC||
           in.readInt()!=Zero3SegmentedArchiveV328.VERSION||
           in.readInt()!=s.start||in.readInt()!=s.count)
            throw new IOException("V329 historical schema/cursor mismatch");
        ArrayList<Zero3FullCausalExperienceV322B> read=
            new ArrayList<>(s.count);
        for(int j=0;j<s.count;j++){
            int len=in.readInt();
            if(len<=100||len>Zero3FullCausalExperienceV322B.MAX_RECORD)
                throw new IOException("V329 historical record length");
            byte[] e=new byte[len];in.readFully(e);
            Zero3FullCausalExperienceV322B record=
                Zero3FullCausalExperienceV322B.decode(e);
            if(record.decision!=s.start+j)
                throw new IOException("V329 reordered full causal record");
            read.add(record);
        }
        if(in.available()!=0)throw new IOException("V329 trailing bytes");
        // Never retain more than one decoded historical segment.
        cached=read;cachedStart=s.start;reads++;
        peakResident=Math.max(peakResident,residentRecords());
    }
    @Override public boolean add(Zero3FullCausalExperienceV322B next){
        if(next==null||next.decision!=size())
            throw new IllegalArgumentException("V329 noncontiguous experience");
        pending.add(next);
        peakResident=Math.max(peakResident,residentRecords());
        return true;
    }
    public int residentRecords(){return cached.size()+pending.size();}
    public int peakResidentRecords(){return peakResident;}
    public int historicalCount(){return committedCount;}
    public int unsavedCount(){return pending.size();}
    public long segmentReads(){return reads;}
    public int sealedSegments(){return segments.size();}
    /** Return a constant-length immutable view, without materializing. */
    public List<Zero3FullCausalExperienceV322B> fixedView(){
        final int limit=size();
        return Collections.unmodifiableList(new AbstractList<Zero3FullCausalExperienceV322B>(){
            @Override public int size(){return limit;}
            @Override public Zero3FullCausalExperienceV322B get(int i){
                if(i<0||i>=limit)throw new IndexOutOfBoundsException();
                return Zero3ReplayJournalV329.this.get(i);
            }
        });
    }
}
