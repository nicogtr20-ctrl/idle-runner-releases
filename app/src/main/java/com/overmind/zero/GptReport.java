package com.overmind.zero;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.ClipData;
import android.net.Uri;
import android.content.pm.PackageInfo;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/** User-invoked READ-ONLY report of ZERO, never a telemetry background
 * service. Zero gameplay, motor or goal code paths call this class.
 *
 * Reports contain a SNAPSHOT of this particular working character's
 * learning weights/statistics in JSON. No slot names or other characters,
 * credentials, device identifiers, account details, or telemetry uploads.
 * Sharing grants temporary read access ONLY to a user-selected Android app.
 */
public final class GptReport {
    public static final int FORMAT=1;
    public static final String NAME="OVERMIND-ZERO-GPT-REPORT.json";
    public static final String AUTH_SUFFIX=".gptreport";
    private static final int MAX_BYTES=6*1024*1024;
    private static final Pattern SENSITIVE=Pattern.compile(
        "(?i)(password|passphrase|secret|token|credential|api.key|auth|email|phone|device.id|\bname\b)");
    private GptReport(){}

    static boolean includeKey(String key){
        return key!=null&&!SENSITIVE.matcher(key).find()&&
            !key.equals(SaveSlotStore.PENDING_RELOAD)&&
            !key.equals(ResetDomains.REQUEST)&&key.length()<=150;
    }
    private static Object encoded(Object value){
        if(value instanceof Boolean||value instanceof String||
           value instanceof Integer||value instanceof Long)return value;
        if(value instanceof Number){
            double n=((Number)value).doubleValue();
            return Double.isFinite(n)?value:JSONObject.NULL;
        }
        if(value instanceof Set){
            TreeSet<String> sorted=new TreeSet<>();
            for(Object item:(Set<?>)value)if(item instanceof String)sorted.add((String)item);
            return new JSONArray(sorted);
        }
        return JSONObject.NULL;
    }
    static String report(Map<String,?> working,String experiment,
                         String version,long when)throws Exception{
        JSONObject root=new JSONObject();
        root.put("schema","overmind.zero.export");
        root.put("schema_version",FORMAT);
        root.put("app_version",version);
        root.put("exported_at_utc",new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.US){{
               setTimeZone(TimeZone.getTimeZone("UTC"));
            }}.format(new Date(when)));
        root.put("source","ONE currently selected working ZERO; snapshot, not continuous monitoring");
        root.put("purpose","GPT analysis of saved training states, episodic memory, life statistics, motor/hand/membrane weights, physical scene, visual energy valence and model quality");
        root.put("privacy","Explicit user share only. Does not include other named save slots or authentication keys. Nothing uploaded automatically.");
        JSONObject profile=new JSONObject();
        TreeMap<String,?> sorted=new TreeMap<>(working);
        int count=0;
        for(Map.Entry<String,?> entry:sorted.entrySet()){
            String key=entry.getKey();
            if(!includeKey(key))continue;
            profile.put(key,encoded(entry.getValue()));
            count++;
        }
        root.put("profile_keys",count);
        root.put("working_profile",profile);
        root.put("last_behavioral_lab_report",experiment==null?"":experiment);
        String json=root.toString(1);
        if(json.getBytes(StandardCharsets.UTF_8).length>MAX_BYTES)
            throw new IOException("Report oltre 6 MB: interrompere e segnalare il limite.");
        return json;
    }
    public static File create(Context ctx)throws Exception{
        SharedPreferences working=ctx.getSharedPreferences(
            SaveSlotStore.WORKING,Context.MODE_PRIVATE);
        SharedPreferences lab=ctx.getSharedPreferences(
            "zero_behavior_lab_v113",Context.MODE_PRIVATE);
        String version="unknown";
        try{
            PackageInfo info=ctx.getPackageManager()
                .getPackageInfo(ctx.getPackageName(),0);
            version=info.versionName;
        }catch(Exception ignored){}
        String json=report(working.getAll(),lab.getString("last",""),
            version,System.currentTimeMillis());
        File folder=new File(ctx.getCacheDir(),"zero_gpt");
        if(!folder.isDirectory()&&!folder.mkdirs())
            throw new IOException("Impossibile creare la cartella privata del report");
        File temporary=new File(folder,NAME+".tmp");
        File target=new File(folder,NAME);
        try(FileOutputStream output=new FileOutputStream(temporary)){
            output.write(json.getBytes(StandardCharsets.UTF_8));
            output.getFD().sync();
        }
        if(target.exists()&&!target.delete())
            throw new IOException("Impossibile sostituire il vecchio report");
        if(!temporary.renameTo(target))
            throw new IOException("Impossibile completare il report");
        return target;
    }
    public static Intent share(Context ctx,File file){
        Uri uri=Uri.parse("content://"+ctx.getPackageName()+
            AUTH_SUFFIX+"/report.json");
        Intent send=new Intent(Intent.ACTION_SEND);
        send.setType("application/json");
        send.putExtra(Intent.EXTRA_STREAM,uri);
        send.putExtra(Intent.EXTRA_TEXT,
            "Report completo di OVERMIND ZERO, generato su richiesta. "+
            "Analizza l'apprendimento e confrontalo con il mio ultimo checkpoint. "+
            "Se puoi, archivialo nel repository privato del progetto.");
        send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        send.setClipData(ClipData.newUri(ctx.getContentResolver(),
            NAME,uri));
        return Intent.createChooser(send,"Condividi dati ZERO con GPT");
    }
    public static Intent saveToDocuments(){
        Intent save=new Intent(Intent.ACTION_CREATE_DOCUMENT);
        save.addCategory(Intent.CATEGORY_OPENABLE);
        save.setType("application/json");
        save.putExtra(Intent.EXTRA_TITLE,NAME);
        return save;
    }
    /** Pure-Java test entrypoint can be invoked only in Android environment. */
}
