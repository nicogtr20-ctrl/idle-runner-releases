package com.overmind.zero;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/** Twelve V3 profiles, isolated from SharedPreferences and all twelve legacy profiles. */
public final class Zero3ProfileStoreV354 {
    private final Path root;
    public Zero3ProfileStoreV354(Path root)throws IOException{this.root=root;Files.createDirectories(root);}
    public Path profile(int slot){if(slot<1||slot>12)throw new IllegalArgumentException("V3 slot1..12");return root.resolve(String.format(Locale.ROOT,"profile-%02d",slot));}
    public String status(int slot)throws IOException{
        Path p=profile(slot);if(!Files.exists(p))return "VUOTO";
        Zero3SegmentedArchiveV328.Bundle b=new Zero3SegmentedArchiveV328(p).load();
        return b==null?(Zero3SegmentedArchiveV328.hasAny(p)?"CORROTTO — recupero richiesto":"VUOTO"):
            "VITA "+b.organism.episodeId+" · "+b.records()+" decisioni";
    }
    private static void name(String name)throws IOException{
        if(!name.matches("v328-[a-zA-Z0-9.-]+")||name.contains(".."))throw new IOException("Profilo: nome archivio non valido");
    }
    private void requireEmpty(int slot)throws IOException{
        Path p=profile(slot);if(!Files.exists(p))return;
        try(DirectoryStream<Path> files=Files.newDirectoryStream(p)){if(files.iterator().hasNext())throw new IOException("Slot esistente: originale protetto");}
        Files.delete(p);
    }
    private void publish(Path candidate,int slot)throws IOException{
        Path dest=profile(slot);if(Files.exists(dest))throw new IOException("Slot esistente: nessuna sovrascrittura");
        if(new Zero3SegmentedArchiveV328(candidate).load()==null)throw new IOException("Backup non integro");
        try{Files.move(candidate,dest,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException noAtomic){Files.move(candidate,dest);}
    }
    public void exportArchive(int slot,OutputStream output)throws IOException{
        Path p=profile(slot);if(new Zero3SegmentedArchiveV328(p).load()==null)throw new IOException("Nessun checkpoint valido");
        try(ZipOutputStream zip=new ZipOutputStream(output);DirectoryStream<Path> files=Files.newDirectoryStream(p,"v328-*")){
            for(Path file:files){if(!Files.isRegularFile(file))continue;String n=file.getFileName().toString();name(n);
                zip.putNextEntry(new ZipEntry(n));Files.copy(file,zip);zip.closeEntry();}
        }
    }
    public void importArchive(int slot,InputStream input)throws IOException{
        requireEmpty(slot);
        Path candidate=Files.createTempDirectory(root,"import-");boolean published=false;long total=0;int entries=0;
        try{
            try(ZipInputStream zip=new ZipInputStream(input)){
                ZipEntry e;byte[] buffer=new byte[65536];Set<String> names=new HashSet<>();
                while((e=zip.getNextEntry())!=null){
                    String n=e.getName();name(n);if(e.isDirectory()||!names.add(n)||++entries>1024)throw new IOException("Backup duplicato o troppo grande");
                    long size=0;try(OutputStream out=Files.newOutputStream(candidate.resolve(n),StandardOpenOption.CREATE_NEW)){
                        int count;while((count=zip.read(buffer))!=-1){size+=count;total+=count;
                            if(size>4L*1024*1024||total>512L*1024*1024)throw new IOException("Backup oltre il budget V3");out.write(buffer,0,count);}
                    }
                }
            }
            publish(candidate,slot);published=true;
        }finally{if(!published)deleteTemporary(candidate);}
    }
    public void cloneProfile(int source,int destination)throws IOException{
        // Streaming to a temporary archive bounds memory and reuses the validated import path.
        Path temporary=Files.createTempFile(root,"clone-",".zip");
        try{exportArchive(source,Files.newOutputStream(temporary));importArchive(destination,Files.newInputStream(temporary));}
        finally{Files.deleteIfExists(temporary);}
    }
    private static void deleteTemporary(Path p)throws IOException{
        try(DirectoryStream<Path> s=Files.newDirectoryStream(p)){for(Path f:s)Files.deleteIfExists(f);}Files.deleteIfExists(p);
    }
}
