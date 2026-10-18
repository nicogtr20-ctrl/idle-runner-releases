package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/**
 * ZERO3 V3.28 — immutable, content-addressed causal replay SEGMENTS.
 * One atomic two-generation manifest links the current neural+physical body,
 * room, 323 survival trace and ALL chronological PRE/12x64/POST records.
 *
 * Previously sealed 32-record segments NEVER get rewritten; only the current
 * tail segment changes. The manifest is replaced atomically at the completed
 * decision boundary. Never touches V2.2 or V324 filenames.
 *
 * Lab prototype: fsync regular files and ATOMIC_MOVE; true Android power-loss
 * tests (including directory fsync) remain a G9 device gate.
 */
public final class Zero3SegmentedArchiveV328 {
    static final int MAGIC=0x5A333238, VERSION=328, CHUNK=32;
    private static final int MAX_MANIFEST=2*1024*1024,MAX_CHUNK=2*1024*1024;
    // The old V317 data format remains capped at its tested 3500 boundary.
    static final int LEGACY_MAX_DECISIONS=3500;
    // V349: Only GRU348 can store 10000 genuine consecutive decisions.
    static final int MAX_DECISIONS=10000;
    static int maxFor(boolean hasGru){return hasGru?MAX_DECISIONS:LEGACY_MAX_DECISIONS;}
    private static void require(boolean ok,String reason)throws IOException{
        if(!ok)throw new IOException("V328 "+reason);
    }
    private static byte[] digest(byte[] b){
        try{return MessageDigest.getInstance("SHA-256").digest(b);}
        catch(Exception e){throw new IllegalStateException(e);}
    }
    static String hex(byte[] bytes){
        StringBuilder s=new StringBuilder(bytes.length*2);
        for(byte x:bytes)s.append(Character.forDigit((x>>>4)&15,16))
                             .append(Character.forDigit(x&15,16));
        return s.toString();
    }
    public static final class Bundle{
        public final Zero3OrganismStoreV322.State organism;
        public final Zero3SurvivalLedgerV323.State survival;
        public final Zero3RoomStateV324 room;
        private final List<Zero3FullCausalExperienceV322B> journal;
        public Bundle(Zero3OrganismStoreV322.State organism,
               Zero3SurvivalLedgerV323.State survival,
               Zero3RoomStateV324 room,
               List<Zero3FullCausalExperienceV322B> experiences)throws IOException{
            require(organism!=null&&survival!=null&&room!=null&&
                    experiences!=null&&organism.nextDecision>=1&&
                    organism.nextDecision<=maxFor(organism.hasGru())&&
                    survival.next==organism.nextDecision&&
                    experiences.size()==organism.nextDecision,
                    "inconsistent world/energy/replay decision");
            Zero3OrganismStoreV322.Physical world=organism.physical();
            room.restore(world.world);
            Zero3OrganismStoreV322.Trace[] trace=organism.trace();
            for(int n=0;n<experiences.size();n++){
                Zero3FullCausalExperienceV322B e=experiences.get(n);
                require(e!=null&&e.decision==n,"missing/duplicate experience");
                require(e.hasPartitionV353()==organism.cognitiveMode(),"mixed cognitive replay provenance");
                e.validate(trace[n],organism.episodeId);
                Zero3SurvivalLedgerV323.Trace fuel=survival.history.get(n);
                require(fuel.decision==n&&fuel.startTick==trace[n].startTick&&
                        fuel.endTick==trace[n].endTick,
                        "mixed physical/energy experience timeline");
            }
            this.organism=organism;this.survival=survival;this.room=room;
            // Fixed-length immutable VIEW avoids copying 2000+ full 9 KiB records.
            // The only mutable source is owned by the single V3 runtime thread.
            final List<Zero3FullCausalExperienceV322B> source=experiences;
            final int frozen=experiences.size();
            journal=Collections.unmodifiableList(new AbstractList<Zero3FullCausalExperienceV322B>(){
                @Override public int size(){return frozen;}
                @Override public Zero3FullCausalExperienceV322B get(int i){
                    if(i<0||i>=frozen)throw new IndexOutOfBoundsException();
                    return source.get(i);
                }
            });
        }
        public int decisions(){return organism.nextDecision;}
        public int records(){return journal.size();}
        public List<Zero3FullCausalExperienceV322B> experiences(){
            return Collections.unmodifiableList(journal);
        }
    }
    static final class Segment{
        final int start,count,size;
        final byte[] sha;
        Segment(int from,int n,int size,byte[] hash){
            start=from;count=n;this.size=size;sha=hash.clone();
        }
        String file(){return "v328-segment-"+hex(sha)+".bin";}
        void write(DataOutputStream o)throws IOException{
            o.writeInt(start);o.writeInt(count);o.writeInt(size);o.write(sha);
        }
        static Segment read(DataInputStream d)throws IOException{
            int start=d.readInt(),n=d.readInt(),size=d.readInt();
            byte[] sha=new byte[32];d.readFully(sha);
            require(start>=0&&start%CHUNK==0&&n>0&&n<=CHUNK&&
                    size>100&&size<=MAX_CHUNK,"invalid segment reference");
            return new Segment(start,n,size,sha);
        }
    }
    private static byte[] pack(List<Zero3FullCausalExperienceV322B> all,
                               int from,int count)throws IOException{
        ByteArrayOutputStream m=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(m);
        d.writeInt(MAGIC);d.writeInt(VERSION);
        d.writeInt(from);d.writeInt(count);
        for(int i=0;i<count;i++){
            Zero3FullCausalExperienceV322B e=all.get(from+i);
            require(e!=null&&e.decision==from+i,"bad segment records");
            byte[] raw=e.encoded();
            require(raw.length>100&&raw.length<=Zero3FullCausalExperienceV322B.MAX_RECORD,
                    "oversized real sensory record");
            d.writeInt(raw.length);d.write(raw);
        }
        d.flush();
        require(m.size()<=MAX_CHUNK,"segment too large");return m.toByteArray();
    }
    private static void verifySegment(Segment s,byte[] bytes,
           Zero3OrganismStoreV322.State organism,
           boolean decodeAll)throws IOException{
        require(bytes.length==s.size&&Arrays.equals(digest(bytes),s.sha),
                "corrupt referenced immutable segment");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(bytes));
        require(d.readInt()==MAGIC&&d.readInt()==VERSION&&
                d.readInt()==s.start&&d.readInt()==s.count,
                "segment schema/start/count");
        Zero3OrganismStoreV322.Trace[] trace=organism.trace();
        for(int k=0;k<s.count;k++){
            int len=d.readInt();
            require(len>100&&len<=Zero3FullCausalExperienceV322B.MAX_RECORD,
                    "invalid full record length");
            byte[] raw=new byte[len];d.readFully(raw);
            Zero3FullCausalExperienceV322B e=
                Zero3FullCausalExperienceV322B.decode(raw);
            require(e.decision==s.start+k,"out of order segmented record");
            e.validate(trace[e.decision],organism.episodeId);
            // Validate content in streaming fashion, never retain all records.
        }
        require(d.available()==0,"trailing segment data");
    }
    private static final class Generation{
        final long number;final Bundle data;
        final ArrayList<Segment> segments;
        Generation(long n,Bundle b,ArrayList<Segment> s){
            number=n;data=b;segments=s;
        }
    }
    private final Path dir;
    private Generation committed;
    private int active=-1;
    public Zero3SegmentedArchiveV328(Path root){
        if(root==null)throw new IllegalArgumentException("V328 requires private folder");
        dir=root;
    }
    private Path pointer(int index){return dir.resolve("v328-generation-"+index+".mf");}
    private Path file(Segment s){return dir.resolve(s.file());}
    public static boolean hasAny(Path root){
        return Files.exists(root.resolve("v328-generation-0.mf"))||
               Files.exists(root.resolve("v328-generation-1.mf"));
    }
    private static void atomic(Path file,byte[] raw)throws IOException {
        Files.createDirectories(file.getParent());
        Path pending=file.resolveSibling(file.getFileName()+".tmp");
        try{
            try(FileOutputStream out=new FileOutputStream(pending.toFile())){
                out.write(raw);out.flush();out.getFD().sync();
            }
            Files.move(pending,file,StandardCopyOption.ATOMIC_MOVE,
                       StandardCopyOption.REPLACE_EXISTING);
        }finally{Files.deleteIfExists(pending);}
    }
    private static void put(DataOutputStream out,byte[] b)throws IOException {
        out.writeInt(b.length);out.write(b);
    }
    private static byte[] get(DataInputStream d,int maximum)throws IOException{
        int n=d.readInt();require(n>20&&n<=maximum,"checkpoint component size");
        byte[] value=new byte[n];d.readFully(value);return value;
    }
    private static byte[] manifest(long generation,Bundle b,
                   List<Segment> segments)throws IOException{
        ByteArrayOutputStream m=new ByteArrayOutputStream();
        DataOutputStream d=new DataOutputStream(m);
        d.writeInt(MAGIC);d.writeInt(b.organism.completionModeV354()?354:b.organism.cognitiveMode()?353:b.organism.variableClock()?352:(b.organism.hasGru()?348:VERSION));
        d.writeLong(generation);
        d.writeLong(b.organism.episodeId);d.writeInt(b.decisions());
        put(d,Zero3OrganismStoreV322.encode(b.organism));
        put(d,b.survival.encoded());put(d,b.room.encoded());
        d.writeInt(segments.size());
        for(Segment s:segments)s.write(d);
        d.flush();byte[] src=m.toByteArray();
        require(src.length+32<=MAX_MANIFEST,"manifest oversized");
        d.write(digest(src));d.flush();
        return m.toByteArray();
    }
    private Generation inspect(int slot)throws IOException{
        byte[] manifest=Files.readAllBytes(pointer(slot));
        require(manifest.length>250&&manifest.length<=MAX_MANIFEST,
                "manifest length");
        byte[] source=Arrays.copyOf(manifest,manifest.length-32);
        require(Arrays.equals(digest(source),Arrays.copyOfRange(
                manifest,manifest.length-32,manifest.length)),
                "manifest SHA mismatch");
        DataInputStream d=new DataInputStream(new ByteArrayInputStream(source));
        require(d.readInt()==MAGIC,"manifest magic mismatch");
        int schema=d.readInt();
        require(schema==VERSION||schema==348||schema==352||schema==353||schema==354,
                "manifest schema mismatch");
        long generation=d.readLong(),episode=d.readLong();
        int cursor=d.readInt();
        require(generation>0&&episode>0&&cursor>0&&
                cursor<=maxFor(schema==348||schema==352||schema==353||schema==354),
                "generation/cursor invalid");
        Zero3OrganismStoreV322.State organism=Zero3OrganismStoreV322.decode(
            get(d,Zero3OrganismStoreV322.MAX_STATE_BYTES));
        Zero3SurvivalLedgerV323.State survival=
            Zero3SurvivalLedgerV323.State.decoded(
                get(d,(schema==348||schema==352||schema==353||schema==354)?600053:262144));
        Zero3RoomStateV324 room=Zero3RoomStateV324.decode(get(d,16384));
        require(organism.episodeId==episode&&organism.nextDecision==cursor&&
                survival.next==cursor&&
                (schema==348||schema==352||schema==353||schema==354)==organism.hasGru()&&
                organism.variableClock()==survival.variableClock&&organism.cognitiveMode()==(schema==353||schema==354)&&organism.completionModeV354()==(schema==354)&&organism.variableClock()==(schema==352||schema==353||schema==354),"torn manifest life/GRU schema");
        int n=d.readInt();
        require(n==(cursor+CHUNK-1)/CHUNK,"wrong number of immutable chunks");
        ArrayList<Segment> chunks=new ArrayList<>();
        ArrayList<Zero3FullCausalExperienceV322B> experiences=null;
        for(int i=0;i<n;i++){
            Segment chunk=Segment.read(d);
            require(chunk.start==i*CHUNK&&chunk.count==Math.min(CHUNK,cursor-i*CHUNK),
                    "segment gap, overlap or wrong tail");
            verifySegment(chunk,Files.readAllBytes(file(chunk)),
                          organism,false);
            chunks.add(chunk);
        }
        require(d.available()==0,"trailing manifest bytes");
        return new Generation(generation,
            new Bundle(organism,survival,room,
                new Zero3ReplayJournalV329(dir,chunks,cursor)),chunks);
    }
    public Bundle load()throws IOException{
        Generation best=null;int index=-1;
        for(int slot=0;slot<2;slot++)try{
            Generation g=inspect(slot);
            if(best==null||g.number>best.number){
                best=g;index=slot;
            }
        }catch(IOException ignored){}
        committed=best;active=index;
        return best==null?null:best.data;
    }
    public long generation(){return committed==null?0:committed.number;}
    /** V329: references committed disk blocks without retaining decoded history. */
    public Zero3ReplayJournalV329 openReplay()throws IOException{
        require(committed!=null,"V329 no verified committed generation");
        return new Zero3ReplayJournalV329(dir,committed.segments,
                                          committed.data.decisions());
    }
    public Path currentManifest(){return active<0?null:pointer(active);}
    public Path oldManifest(){return active<0?null:pointer(1-active);}
    public Path currentTail(){
        return committed==null?null:file(committed.segments.get(committed.segments.size()-1));
    }
    /** Called ONLY with a complete and already tested decision boundary.
     * All immutable sealed segment hashes are reused, not re-written.
     */
    public void save(Bundle data)throws IOException{
        require(data!=null,"null decision boundary");
        if(committed!=null)
            require(data.organism.episodeId==committed.data.organism.episodeId&&
                    data.decisions()>=committed.data.decisions()&&
                    Files.exists(pointer(active))&&
                    Arrays.equals(digest(Files.readAllBytes(pointer(active))),
                                  digest(manifest(committed.number,
                                            committed.data,committed.segments))),
                    "wrong episode or corrupted current generation");
        else require(!hasAny(dir),"existing but no validated V328 archive: fail closed");
        int count=data.decisions();
        ArrayList<Segment> segments=new ArrayList<>();
        // Reuse *sealed* immutable segments from the last committed generation.
        int reusable=committed==null?0:committed.data.decisions()/CHUNK;
        for(int i=0;i<reusable;i++)segments.add(committed.segments.get(i));
        for(int start=reusable*CHUNK;start<count;start+=CHUNK){
            int n=Math.min(CHUNK,count-start);
            byte[] raw=pack(data.journal,start,n);
            Segment s=new Segment(start,n,raw.length,digest(raw));
            Path path=file(s);
            if(Files.exists(path))
                require(Arrays.equals(Files.readAllBytes(path),raw),
                        "existing content-addressed segment changed");
            else atomic(path,raw);
            segments.add(s);
        }
        long number=committed==null?1:committed.number+1;
        byte[] mf=manifest(number,data,segments);
        // Guarantee a fully decodable checkpoint BEFORE publication.
        int next=active==0?1:0;
        atomic(pointer(next),mf);
        // Resolve the newly installed manifest and every referenced segment.
        Generation verified=inspect(next);
        require(verified.number==number&&verified.data.decisions()==count,
                "cannot verify committed V328 generation");
        committed=verified;active=next;
    }
    /** Prune unreferenced immutable records AFTER a successful commit, while
     * keeping BOTH generations, including rollback tail segments.
     */
    public int pruneOrphans()throws IOException{
        require(committed!=null,"no committed generation");
        HashSet<String> used=new HashSet<>();
        for(int slot=0;slot<2;slot++)try{
            Generation g=inspect(slot);
            for(Segment s:g.segments)used.add(s.file());
        }catch(IOException ignored){}
        int removed=0;
        try(DirectoryStream<Path> stream=Files.newDirectoryStream(dir,
                    "v328-segment-*.bin")){
            for(Path p:stream)if(!used.contains(p.getFileName().toString())){
                Files.delete(p);removed++;
            }
        }
        return removed;
    }
}
