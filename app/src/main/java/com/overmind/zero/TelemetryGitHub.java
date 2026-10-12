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
        result.put("gameplayBaseline","ZERO 0.11.4 behavior; no 0.11.5 pain change");
        result.put("liveProfileKeys",copied);
        result.put("dataPolicy","Current ZERO working game data only; no named slots, device identifiers, private contacts, user account, or GitHub token");
        result.put("currentZero",values);
        String grip=c.getSharedPreferences(GripContactObserver.PREFS,
            Context.MODE_PRIVATE).getString(GripContactObserver.KEY,"");
        if(grip.startsWith("go1|")&&grip.length()<=60000)
            result.put("gripContactObserver",grip);
        // The controlled 18-trial A/B report is read-only and separate.
        String lab=c.getSharedPreferences("zero_behavior_lab_v113",
            Context.MODE_PRIVATE).getString("last","");
        if(!lab.isEmpty())result.put("lastBehaviorLab",lab);
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
                if(data.has("gripContactObserver"))
                    source.put("gripContactObserver",data.get("gripContactObserver"));
                if(data.has("lastBehaviorLab"))
                    source.put("lastBehaviorLab",data.get("lastBehaviorLab"));
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
    interface Progress {void update(String label);}
    static String upload(Context context,Progress progress)throws Exception{
        String token=secret(context);
        if(progress!=null)progress.update("Raccolta dati in sola lettura...");
        JSONObject data=report(context);
        String json=data.toString();
        int size=json.getBytes(StandardCharsets.UTF_8).length;
        if(size>LIMIT)throw new IllegalStateException(
            "Il report supera 900 KB ("+size+"): nessun dato caricato.");
        SimpleDateFormat fmt=new SimpleDateFormat("yyyyMMdd-HHmmss",Locale.US);
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String suffix=Long.toHexString(System.nanoTime());
        String history="zero-telemetry/history/"+fmt.format(new Date())+"-"+suffix+".json";
        if(progress!=null)progress.update("Upload storico privato su GitHub...");
        call("PUT",history,token,commitBody(json,
            "ZERO voluntary telemetry snapshot: "+fmt.format(new Date()),null));
        if(progress!=null)progress.update("Aggiornamento ultimo report...");
        JSONObject current=call("GET",LATEST+"?ref="+BRANCH,token,null);
        String oldSha=current==null?null:current.optString("sha","");
        call("PUT",LATEST,token,commitBody(json,
            "ZERO latest voluntary gameplay report",oldSha));
        context.getSharedPreferences(SETTINGS,Context.MODE_PRIVATE).edit()
            .putLong("last_success",System.currentTimeMillis()).apply();
        // Stable path can always be fetched by a connected GitHub plugin.
        return "https://github.com/"+REPO+"/blob/"+BRANCH+"/"+LATEST;
    }
}
