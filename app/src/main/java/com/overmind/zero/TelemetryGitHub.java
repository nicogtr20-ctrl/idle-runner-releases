package com.overmind.zero;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * ZERO 0.11.6 — voluntary read-only data export, no brain mutations.
 *
 * Uses an explicitly user-supplied fine-grained GitHub PAT (single repository,
 * Contents read/write). Secret is encrypted with Android Keystore, never
 * baked into the APK, uploaded, logged or transferred to ChatGPT.
 *
 * The private data branch remains separate from code, named save slots and
 * Vercel OTA. Zero owns the report and the app has no background upload.
 */
final class TelemetryGitHub {
    static final String REPO="nicogtr20-ctrl/idle-runner-releases";
    static final String BRANCH="zero-telemetry";
    static final String LATEST="zero-telemetry/current.json";
    static final String SETTINGS="zero_share_gpt_v116";
    private static final String KEY_ALIAS="zero_github_report_key_v116";
    private static final String API="https://api.github.com/repos/"+REPO+"/contents/";
    // Safety bound on the complete memory snapshot; NO truncation to 900 KB.
    private static final int MAX_EXPORT_BYTES=32*1024*1024;
    private TelemetryGitHub(){}

    static boolean hasToken(Context c){
        return c.getSharedPreferences(SETTINGS,Context.MODE_PRIVATE)
            .contains("cipher");
    }
    private static SecretKey aesKey()throws Exception {
        KeyStore ks=KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        if(!ks.containsAlias(KEY_ALIAS)){
            KeyGenerator g=KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
            g.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256).build());
            g.generateKey();
        }
        return (SecretKey)ks.getKey(KEY_ALIAS,null);
    }
    static void storeToken(Context c,String supplied)throws Exception{
        String token=supplied==null?"":supplied.trim();
        if(token.length()<25||token.length()>400||token.contains(" ")||
           token.contains("\n")||token.contains("\r"))
            throw new IllegalArgumentException(
                "Incolla un token GitHub fine-grained valido.");
        Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE,aesKey());
        String coded=Base64.encodeToString(cipher.getIV(),Base64.NO_WRAP)+":"+
            Base64.encodeToString(
                cipher.doFinal(token.getBytes(StandardCharsets.UTF_8)),
                Base64.NO_WRAP);
        if(!c.getSharedPreferences(SETTINGS,Context.MODE_PRIVATE).edit()
            .putString("cipher",coded).commit())
            throw new IllegalStateException("Protezione del token non riuscita");
    }
    static void forget(Context c){
        c.getSharedPreferences(SETTINGS,Context.MODE_PRIVATE)
            .edit().remove("cipher").commit();
    }
    private static String secret(Context c)throws Exception{
        String encoded=c.getSharedPreferences(SETTINGS,Context.MODE_PRIVATE)
            .getString("cipher","");
        String[] chunks=encoded.split(":",-1);
        if(chunks.length!=2)throw new IllegalStateException(
            "Token non configurato: ripeti la configurazione.");
        Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE,aesKey(),new GCMParameterSpec(
            128,Base64.decode(chunks[0],Base64.DEFAULT)));
        return new String(cipher.doFinal(Base64.decode(chunks[1],Base64.DEFAULT)),
                          StandardCharsets.UTF_8);
    }
    private static boolean excluded(String key){
        String k=key.toLowerCase(Locale.US);
        return key.equals(ResetDomains.REQUEST)||
            key.equals(SaveSlotStore.PENDING_RELOAD)||
            k.contains("token")||k.contains("secret")||
            k.contains("password")||k.contains("api_key")||
            k.contains("authorization");
    }
    static JSONObject report(Context c)throws Exception{
        Map<String,?> all=c.getSharedPreferences(
            SaveSlotStore.WORKING,Context.MODE_PRIVATE).getAll();
        JSONObject values=new JSONObject();
        int copied=0;
        for(Map.Entry<String,?> item:all.entrySet()){
            String key=item.getKey();
            if(excluded(key))continue;
            Object v=item.getValue();
            if(v instanceof Float && !Float.isFinite((Float)v))continue;
            if(v instanceof Double && !Double.isFinite((Double)v))continue;
            if(v instanceof Number||v instanceof Boolean||v instanceof String)
                values.put(key,v);
            else if(v instanceof Set){
                JSONArray list=new JSONArray();
                for(Object entry:(Set<?>)v)
                    if(entry instanceof String)list.put(entry);
                values.put(key,list);
            }else continue;
            copied++;
        }
        JSONObject result=new JSONObject();
        result.put("schema","overmind-zero-telemetry-v1");
        result.put("recordedAtUtc",System.currentTimeMillis());
        result.put("source","ANDROID_EXPLICIT_SHARE");
        // The saved V3 checkpoint is the ONLY source of truth for the
        // current actor. Legacy motor data may remain in working preferences,
        // but it never acts as a second brain when the V3 player is active.
        String v3Checkpoint=all.get(Zero3OriginalBrainV356.KEY) instanceof String?
            (String)all.get(Zero3OriginalBrainV356.KEY):"";
        result.put("gameplayBaseline",v3Checkpoint.isEmpty()?
            "NO_VALID_V3_CHECKPOINT_YET; historical working profile only":
            "ZERO 3.0 GRU64 sole original-game motor actor; V2.2 legacy data archival only");
        android.content.pm.PackageInfo installed=c.getPackageManager().getPackageInfo(c.getPackageName(),0);
        result.put("runtimeRevision","zero3-original-game-ota360");
        result.put("installedVersionName",installed.versionName);
        result.put("installedVersionCode",android.os.Build.VERSION.SDK_INT>=28?
            installed.getLongVersionCode():installed.versionCode);
        result.put("predictionDiagnostic","ACTIVE: ZERO3 V353/V354 predictive GRU modules in zero3Summary and full checkpoint. V2.2 diagnostics in currentZero are HISTORICAL.");
        result.put("liveProfileKeys",copied);
        result.put("dataPolicy","Current ZERO working game data only; no named slots, device identifiers, private contacts, user account, or GitHub token");
        result.put("currentZero",values);
        // This compact, factual summary is directly visible in the current
        // GitHub index (without manually reconstructing segmented checkpoints).
        // Its counts come ONLY from inspectSaved() of the exact exported bytes.
        // Never manufacture decision counts, sensor evidence or a valid brain.
        JSONObject v3=new JSONObject();
        v3.put("checkpointKey",Zero3OriginalBrainV356.KEY);
        v3.put("checkpointPresent",!v3Checkpoint.isEmpty());
        v3.put("checkpointIncludedInCurrentZero",values.has(Zero3OriginalBrainV356.KEY));
        if(!v3Checkpoint.isEmpty()){
            try{
                Zero3OriginalBrainV356.Readout neural=
                    Zero3OriginalBrainV356.inspectSaved(v3Checkpoint);
                v3.put("checkpointValid",true);
                v3.put("role","SOLE_ACTIVE_NEURAL_ACTOR_ORIGINAL_GAME");
                v3.put("sensoryInputs",neural.sensoryInputs);
                v3.put("recurrentUnits",neural.hiddenUnits);
                v3.put("muscleOutputs",neural.actionOutputs);
                v3.put("adhesionSectors",neural.adhesionSectors);
                v3.put("completedDecisions",neural.decisions);
                v3.put("trainingDecisions",neural.trained);
                v3.put("heldoutDecisions",neural.heldout);
                v3.put("observationDecisions",neural.observations);
                v3.put("cortexUpdates",neural.motorUpdates);
                v3.put("previousPhysicalDamage",neural.previousDamageV359);
                if(neural.cognition!=null){
                    v3.put("predictiveUnits",neural.cognition.predictiveUnits);
                    v3.put("shadowUnits",neural.cognition.shadowUnits);
                    v3.put("growthAccepted",neural.cognition.growthAccepted);
                    v3.put("growthRejected",neural.cognition.growthRejected);
                }
                if(neural.executive!=null){
                    v3.put("candidateOptions",neural.executive.skills.length);
                    v3.put("historicalDecisions",neural.executive.historicalDecisions);
                }
            }catch(Exception badCheckpoint){
                v3.put("checkpointValid",false);
                v3.put("reason","Saved V3 checkpoint is corrupt or incomplete; no V3 metrics inferred");
            }
        }else{
            v3.put("checkpointValid",false);
            v3.put("reason","Game has not yet saved the active 3.0 profile");
        }
        result.put("zero3Summary",v3);
        // ZERO 2.0 observer metrics are reconstructed from the saved copy,
        // only on a user-initiated upload thread. No live brain is touched.
        result.put("zero20Passive",new JSONObject(Zero20PassiveSnapshot.fromSaved(all)));
        String grip=c.getSharedPreferences(GripContactObserver.PREFS,
            Context.MODE_PRIVATE).getString(GripContactObserver.KEY,"");
        if(grip.startsWith("go1|")&&grip.length()<=60000)
            result.put("gripContactObserver",grip);
        // Session boundaries are intentional. Export the archived preceding
        // session separately only when user explicitly shares diagnostic data.
        String pastGrip=c.getSharedPreferences(GripContactObserver.PREFS,
            Context.MODE_PRIVATE).getString(GripContactObserver.PREVIOUS_SESSION,"");
        if(pastGrip.startsWith("go1|")&&pastGrip.length()<=60000)
            result.put("previousGripContactSession",pastGrip);
        // The controlled 18-trial A/B report is read-only and separate.
        String lab=c.getSharedPreferences("zero_behavior_lab_v113",
            Context.MODE_PRIVATE).getString("last","");
        if(!lab.isEmpty())result.put("lastBehaviorLab",lab);
        String coreLab=c.getSharedPreferences("zero_behavior_lab_v22",
            Context.MODE_PRIVATE).getString("last","");
        if(!coreLab.isEmpty())result.put("lastCoreLabV22",coreLab);
        String extended=c.getSharedPreferences(CoreComparisonV22.RESULTS,
            Context.MODE_PRIVATE).getString("last","");
        if(!extended.isEmpty())result.put("lastCoreExtendedV22",extended);
        return result;
    }
    // Each independent upload is <=256 KiB; metadata for a previous legacy
    // current.json can nevertheless still exceed 1 MiB in GitHub's Base64 GET.
    private static final int MAX_SUCCESS_RESPONSE=2*1024*1024;
    private static final int MAX_ERROR_RESPONSE=12*1024;
    private static String readStream(InputStream in,int maxBytes)throws Exception{
        if(in==null)return "";
        try(InputStream stream=in;ByteArrayOutputStream out=new ByteArrayOutputStream()){
            byte[] buffer=new byte[8192];int n;
            while((n=stream.read(buffer))!=-1){
                if(n>maxBytes-out.size())
                    throw new IllegalStateException(
                        "Risposta GitHub troppo grande: caricamento non confermato.");
                out.write(buffer,0,n);
            }
            return new String(out.toByteArray(),StandardCharsets.UTF_8);
        }
    }
    private static JSONObject call(String method,String path,String token,
                                   JSONObject payload)throws Exception{
        HttpURLConnection http=(HttpURLConnection)new URL(API+path).openConnection();
        if(!http.getURL().getProtocol().equals("https"))
            throw new IllegalStateException("GitHub richiede HTTPS");
        http.setRequestMethod(method);
        http.setConnectTimeout(14000);http.setReadTimeout(28000);
        http.setRequestProperty("Authorization","Bearer "+token);
        http.setRequestProperty("Accept","application/vnd.github+json");
        http.setRequestProperty("X-GitHub-Api-Version","2022-11-28");
        http.setRequestProperty("User-Agent","Overmind-Zero-0.11.6");
        http.setRequestProperty("Cache-Control","no-store");
        if(payload!=null){
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type","application/json; charset=utf-8");
            byte[] data=payload.toString().getBytes(StandardCharsets.UTF_8);
            http.setFixedLengthStreamingMode(data.length);
            try(OutputStream out=http.getOutputStream()){out.write(data);}
        }
        try{
            int code=http.getResponseCode();
            String body=readStream(
                code>=400?http.getErrorStream():http.getInputStream(),
                code>=400?MAX_ERROR_RESPONSE:MAX_SUCCESS_RESPONSE);
            if(code==404&&method.equals("GET"))return null;
            if(code<200||code>=300){
                String reason="HTTP "+code;
                if(code==401)reason="Token GitHub non valido o scaduto";
                else if(code==403)reason="Permessi GitHub insufficienti: abilita Contents Read and write sul repository ZERO";
                else if(code==404)reason="Repository/ramo non accessibile con questo token";
                else if(code==409)reason="Caricamento in conflitto: riprova";
                else if(code==413||code==422)reason="Report troppo grande o non accettato da GitHub";
                throw new IllegalStateException(reason);
            }
            return new JSONObject(body);
        }finally{http.disconnect();}
    }
    private static JSONObject commitBody(String json,String label,String sha){
        JSONObject req=new JSONObject();
        try{
            req.put("message",label);
            req.put("branch",BRANCH);
            req.put("content",Base64.encodeToString(
                json.getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP));
            if(sha!=null&&!sha.isEmpty())req.put("sha",sha);
        }catch(Exception e){throw new IllegalStateException(e);}
        return req;
    }

    private static final class Piece {
        final String json,kind,field,key;
        final int number,total;
        final ArrayList<String> names;
        Piece(String j,String type,String group,String name,
              int part,int count,ArrayList<String> included){
            json=j;kind=type;field=group;key=name;
            number=part;total=count;names=included;
        }
    }
    private static JSONObject page(String id,JSONObject main,JSONObject extra)
        throws Exception{
        JSONObject p=new JSONObject();
        p.put("schema","overmind-zero-telemetry-part-v2");
        p.put("reportId",id);
        p.put("currentZero",main);
        p.put("extras",extra);
        return p;
    }
    private static void emitPage(List<Piece> out,String id,
                                 JSONObject main,JSONObject extra,
                                 ArrayList<String> keys)throws Exception{
        if(keys.isEmpty())return;
        String text=page(id,main,extra).toString();
        if(TelemetryChunks.size(text)>TelemetryChunks.PART_BYTES)
            throw new IllegalStateException("Sezione dati troppo grande");
        out.add(new Piece(text,"values","","",-1,0,new ArrayList<>(keys)));
    }
    private static String digest(String value)throws Exception{
        byte[] checksum=MessageDigest.getInstance("SHA-256").digest(
            value.getBytes(StandardCharsets.UTF_8));
        StringBuilder b=new StringBuilder(64);
        for(byte part:checksum)b.append(String.format(Locale.US,"%02x",part&255));
        return b.toString();
    }
    /** Immutable, independently readable JSON parts: each full preference
     * belongs to exactly one file (or an ordered lossless fragment sequence).
     * The old complete live profile remains INTACT in Android SharedPrefs.
     */
    private static List<Piece> partition(JSONObject data,JSONObject quick,
                                          String id)throws Exception{
        ArrayList<Piece> parts=new ArrayList<>();
        JSONObject current=new JSONObject(),extra=new JSONObject();
        ArrayList<String> included=new ArrayList<>();
        for(int group=0;group<2;group++){
            String groupName=group==0?"currentZero":"extras";
            JSONObject source;
            if(group==0)source=data.getJSONObject("currentZero");
            else {
                source=new JSONObject();
                // Keep ZERO 2.0's compact explanatory digest as part of the
                // multipart archive, not merely in the in-memory report().
                // The previous whitelist silently dropped this optional
                // section while still uploading all raw model preferences.
                if(data.has("zero20Passive"))
                    source.put("zero20Passive",data.get("zero20Passive"));
                if(data.has("gripContactObserver"))
                    source.put("gripContactObserver",data.get("gripContactObserver"));
                if(data.has("lastBehaviorLab"))
                    source.put("lastBehaviorLab",data.get("lastBehaviorLab"));
                if(data.has("lastCoreExtendedV22"))
                    source.put("lastCoreExtendedV22",data.get("lastCoreExtendedV22"));
                if(data.has("lastCoreLabV22"))
                    source.put("lastCoreLabV22",data.get("lastCoreLabV22"));
            }
            ArrayList<String> names=new ArrayList<>();
            java.util.Iterator<String> iterator=source.keys();
            while(iterator.hasNext())names.add(iterator.next());
            Collections.sort(names);
            for(String name:names){
                Object value=source.get(name);
                JSONObject entry=new JSONObject();entry.put(name,value);
                String encoded=entry.toString();
                // Preserve small scalar measurements directly in the index;
                // full large models and historical strings go into the parts.
                if(group==0&&TelemetryChunks.size(encoded)<=280){
                    quick.put(name,value);continue;
                }
                JSONObject destination=group==0?current:extra;
                destination.put(name,value);
                if(TelemetryChunks.size(page(id,current,extra).toString())<=
                    TelemetryChunks.PART_BYTES){
                    included.add(groupName+"."+name);continue;
                }
                destination.remove(name);
                emitPage(parts,id,current,extra,included);
                current=new JSONObject();extra=new JSONObject();
                included=new ArrayList<>();
                destination=group==0?current:extra;
                destination.put(name,value);
                if(TelemetryChunks.size(page(id,current,extra).toString())<=
                    TelemetryChunks.PART_BYTES){
                    included.add(groupName+"."+name);continue;
                }
                // Arbitrarily large single value, never silently truncated.
                destination.remove(name);
                List<String> fragments=TelemetryChunks.split(
                    encoded,TelemetryChunks.FRAGMENT_BYTES);
                for(int i=0;i<fragments.size();i++){
                    JSONObject fragment=new JSONObject();
                    fragment.put("schema","overmind-zero-telemetry-fragment-v2");
                    fragment.put("reportId",id);
                    fragment.put("field",groupName);
                    fragment.put("key",name);
                    fragment.put("fragmentIndex",i);
                    fragment.put("fragmentCount",fragments.size());
                    fragment.put("jsonFragment",fragments.get(i));
                    String content=fragment.toString();
                    if(TelemetryChunks.size(content)>TelemetryChunks.PART_BYTES)
                        throw new IllegalStateException("Frammento troppo grande");
                    ArrayList<String> one=new ArrayList<>();
                    one.add(groupName+"."+name);
                    parts.add(new Piece(content,"fragment",groupName,name,
                                         i,fragments.size(),one));
                    if(parts.size()>TelemetryChunks.MAX_PARTS)
                        throw new IllegalStateException(
                            "Troppe sezioni: nessun report parziale pubblicato");
                }
            }
        }
        emitPage(parts,id,current,extra,included);
        if(parts.isEmpty()||parts.size()>TelemetryChunks.MAX_PARTS)
            throw new IllegalStateException("Numero sezioni non valido");
        return parts;
    }

    private static final class Archive {
        final String index,history;
        final ArrayList<String> paths;
        final List<Piece> parts;
        Archive(String full,String old,ArrayList<String> files,List<Piece> payloads){
            index=full;history=old;paths=files;parts=payloads;
        }
    }
    /** Construct the COMPLETE manifest and verify every part BEFORE upload. */
    private static Archive prepare(JSONObject data)throws Exception{
        int rawSize=TelemetryChunks.size(data.toString());
        if(rawSize>MAX_EXPORT_BYTES)
            throw new IllegalStateException("Dati oltre il limite di 32 MiB");
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd-HHmmss",Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String id=format.format(new Date())+"-"+Long.toHexString(System.nanoTime());
        JSONObject inline=new JSONObject();
        List<Piece> pieces=partition(data,inline,id);
        ArrayList<String> paths=new ArrayList<>();
        JSONArray entries=new JSONArray();
        for(int i=0;i<pieces.size();i++){
            Piece part=pieces.get(i);
            String path="zero-telemetry/reports/"+id+"/part-"+
                String.format(Locale.US,"%03d",i+1)+".json";
            paths.add(path);
            JSONObject item=new JSONObject();
            item.put("path",path);item.put("kind",part.kind);
            item.put("bytes",TelemetryChunks.size(part.json));
            item.put("sha256",digest(part.json));
            JSONArray names=new JSONArray();
            for(String n:part.names)names.put(n);
            item.put("keys",names);
            if("fragment".equals(part.kind)){
                item.put("field",part.field);item.put("key",part.key);
                item.put("fragmentIndex",part.number);
                item.put("fragmentCount",part.total);
            }
            entries.put(item);
        }
        JSONObject index=new JSONObject();
        index.put("schema","overmind-zero-telemetry-index-v2");
        index.put("reportId",id);
        index.put("recordedAtUtc",data.getLong("recordedAtUtc"));
        index.put("source",data.getString("source"));
        index.put("dataPolicy",data.getString("dataPolicy"));
        index.put("gameplayBaseline",data.getString("gameplayBaseline"));
        for(String meta:new String[]{"runtimeRevision","installedVersionName","installedVersionCode","predictionDiagnostic"})
            if(data.has(meta))index.put(meta,data.get(meta));
        index.put("liveProfileKeys",data.getInt("liveProfileKeys"));
        // Always include this summary in the FIRST current.json, even when
        // the full GRU checkpoint is fragmented into immutable report parts.
        if(data.has("zero3Summary"))index.put("zero3Summary",data.getJSONObject("zero3Summary"));
        index.put("currentZero",inline);
        index.put("parts",entries);
        index.put("partCount",pieces.size());
        index.put("fullReportBytes",rawSize);
        index.put("assembly","Merge currentZero and extras from listed values parts. "+
            "For oversized keys concatenate jsonFragment in fragmentIndex order "+
            "and parse the one-key JSON. Verify each SHA-256.");
        String text=index.toString();
        if(TelemetryChunks.size(text)>TelemetryChunks.PART_BYTES)
            throw new IllegalStateException("Indice report troppo grande");
        return new Archive(text,"zero-telemetry/history/"+id+".json",
                           paths,pieces);
    }
    interface Progress {void update(String label);}
    static String upload(Context context,Progress progress)throws Exception{
        String token=secret(context);
        if(progress!=null)progress.update("Raccolta dati in sola lettura...");
        JSONObject data=report(context);
        Archive archive=prepare(data);
        // Publish ALL immutable shards before switching either index.
        // Failed partial uploads leave the previous current.json untouched.
        for(int i=0;i<archive.parts.size();i++){
            if(progress!=null)progress.update("Invio sezione "+(i+1)+"/"+
                archive.parts.size()+" su GitHub...");
            call("PUT",archive.paths.get(i),token,
                commitBody(archive.parts.get(i).json,
                           "ZERO complete telemetry part "+(i+1),null));
        }
        if(progress!=null)progress.update("Salvataggio indice storico...");
        call("PUT",archive.history,token,
            commitBody(archive.index,"ZERO full private report index",null));
        if(progress!=null)progress.update("Verifica ultimo report...");
        JSONObject previous=call("GET",LATEST+"?ref="+BRANCH,token,null);
        String oldSha=previous==null?null:previous.optString("sha","");
        call("PUT",LATEST,token,
            commitBody(archive.index,"ZERO latest complete report",oldSha));
        context.getSharedPreferences(SETTINGS,Context.MODE_PRIVATE).edit()
            .putLong("last_success",System.currentTimeMillis()).apply();
        // Stable path can always be fetched by a connected GitHub plugin.
        return "https://github.com/"+REPO+"/blob/"+BRANCH+"/"+LATEST;
    }
}
