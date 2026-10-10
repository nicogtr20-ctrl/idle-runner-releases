package com.overmind.zero;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Locale;
import org.json.JSONObject;

/**
 * In-app OTA client. Two INDEPENDENT manifest origins: live Vercel endpoint
 * and GitHub main's signed-release manifest. Pick the newest valid build,
 * then independently validate downloaded bytes against the signed manifest's
 * SHA-256 and exact size. Android package signature must still match.
 *
 * Old versions had only Vercel and rejected EVERY HTTP redirect. Legitimate
 * GitHub releases use a short-lived redirect to a binary asset CDN. Allow
 * bounded HTTPS redirects ONLY among pinned first-party distribution hosts.
 */
public final class UpdateClient {
    public interface StatusListener { void changed(String text); }

    public static final String MANIFEST_URL=
        "https://overmind-zero-updates.vercel.app/latest.json";
    public static final String BACKUP_MANIFEST_URL=
        "https://raw.githubusercontent.com/nicogtr20-ctrl/idle-runner-releases/main/updater/public/latest.json";
    private static final String RELEASE_PREFIX=
        "https://github.com/nicogtr20-ctrl/idle-runner-releases/releases/download/overmind-zero-v";
    private static final long MAX_APK_BYTES=200L*1024*1024;
    private static final int REDIRECT_LIMIT=6;
    private final Activity activity;
    private final StatusListener listener;
    private volatile boolean busy=false;

    private static final class ManifestChoice {
        final JSONObject manifest;
        final String source;
        ManifestChoice(JSONObject m,String name){manifest=m;source=name;}
    }
    public UpdateClient(Activity context,StatusListener callback){
        activity=context;listener=callback;
    }
    private void status(String s){
        activity.runOnUiThread(()->listener.changed(s));
    }
    private static boolean permittedHost(String host){
        if(host==null)return false;
        String h=host.toLowerCase(Locale.ROOT);
        return h.equals("overmind-zero-updates.vercel.app")||
               h.equals("raw.githubusercontent.com")||
               h.equals("github.com")||
               h.equals("release-assets.githubusercontent.com")||
               h.equals("objects.githubusercontent.com")||
               h.equals("github-releases.githubusercontent.com");
    }
    private static boolean trusted(URL u){
        return "https".equalsIgnoreCase(u.getProtocol())&&
               permittedHost(u.getHost())&&u.getUserInfo()==null;
    }
    /** Use exact status/Location so a redirect or an HTML error cannot be
     * mistaken for a 200 and silently abort automatic updates.
     */
    private HttpURLConnection openHttps(URL start,int readTimeout)throws Exception{
        if(!trusted(start))throw new IOException("Host aggiornamento non autorizzato");
        URL current=start;
        for(int hops=0;hops<=REDIRECT_LIMIT;hops++){
            HttpURLConnection c=(HttpURLConnection)current.openConnection();
            c.setInstanceFollowRedirects(false);
            c.setConnectTimeout(9500);
            c.setReadTimeout(readTimeout);
            c.setRequestProperty("User-Agent","OVERMIND-ZERO-OTA/0.10.2");
            c.setRequestProperty("Cache-Control","no-cache, no-store");
            c.setRequestProperty("Pragma","no-cache");
            int status=c.getResponseCode();
            if(status==301||status==302||status==303||status==307||status==308){
                String next=c.getHeaderField("Location");
                if(next==null||hops==REDIRECT_LIMIT){
                    c.disconnect();throw new IOException("Troppi reindirizzamenti");
                }
                URL redirect=new URL(current,next);
                c.disconnect();
                if(!trusted(redirect))
                    throw new IOException("Reindirizzamento esterno bloccato");
                current=redirect;
                continue;
            }
            if(status!=200){
                c.disconnect();throw new IOException("HTTP "+status);
            }
            return c;
        }
        throw new IOException("Redirect non valido");
    }
    private JSONObject manifestFrom(String target)throws Exception{
        URL url=new URL(target+"?v="+System.currentTimeMillis());
        HttpURLConnection c=openHttps(url,13000);
        try{
            ByteArrayOutputStream buffer=new ByteArrayOutputStream();
            try(InputStream in=c.getInputStream()){
                byte[] bytes=new byte[4096];
                int n,total=0;
                while((n=in.read(bytes))!=-1){
                    total+=n;
                    if(total>22000)throw new IOException("Manifest troppo grande");
                    buffer.write(bytes,0,n);
                }
            }
            JSONObject m=new JSONObject(buffer.toString("UTF-8"));
            if(m.optInt("schemaVersion",0)!=1 ||
               !m.optBoolean("available",false))
                throw new IOException("Manifest non disponibile");
            if(!activity.getPackageName().equals(m.optString("applicationId","")))
                throw new IOException("APK di altra applicazione");
            int code=m.optInt("versionCode",0);
            long size=m.optLong("sizeBytes",0);
            String hash=m.optString("sha256","").toLowerCase(Locale.ROOT);
            String name=m.optString("versionName","");
            if(code<=0||size<1000||size>MAX_APK_BYTES||
               !hash.matches("[0-9a-f]{64}")||
               !name.matches("[0-9A-Za-z][0-9A-Za-z.\\-]{2,89}"))
                throw new IOException("Metadati OTA non validi");
            return m;
        }finally{c.disconnect();}
    }
    private ManifestChoice newestManifest()throws Exception{
        ManifestChoice preferred=null;
        String lastError="";
        // Compare BOTH when available. A stale but reachable Vercel endpoint
        // should not prevent fetching a newer signed GitHub version.
        for(String source:new String[]{MANIFEST_URL,BACKUP_MANIFEST_URL}){
            try{
                JSONObject m=manifestFrom(source);
                if(preferred==null ||
                   m.optInt("versionCode")>preferred.manifest.optInt("versionCode"))
                    preferred=new ManifestChoice(m,
                        source.equals(MANIFEST_URL)?"VERCEL":"GITHUB");
            }catch(Exception fail){
                lastError=source.equals(MANIFEST_URL)?
                    "VERCEL "+brief(fail):"GITHUB "+brief(fail);
            }
        }
        if(preferred==null)
            throw new IOException("Manifest non raggiungibile: "+lastError);
        return preferred;
    }
    public void checkAndInstall(){
        if(busy){status("CONTROLLO GIA IN CORSO");return;}
        busy=true;
        status("VERIFICA VERCEL E GITHUB...");
        new Thread(()->{
            try{
                ManifestChoice picked=newestManifest();
                int code=picked.manifest.optInt("versionCode",0);
                long installed=versionCode();
                if(code<=installed){
                    status("GIA AGGIORNATO • APP "+installed+" / SERVER "+code);
                    return;
                }
                String version=picked.manifest.getString("versionName");
                String sha=picked.manifest.getString("sha256").toLowerCase(Locale.ROOT);
                long bytes=picked.manifest.getLong("sizeBytes");
                URL preferred=new URL(picked.manifest.optString("apkUrl",""));
                String fallback=RELEASE_PREFIX+version+"/OVERMIND-ZERO.apk";
                // On a newer GitHub manifest, avoid relying on Vercel's
                // possibly stale cached binary as the first attempt.
                URL[] origins="GITHUB".equals(picked.source)?
                    new URL[]{new URL(fallback),preferred}:
                    new URL[]{preferred,new URL(fallback)};
                Exception error=null;
                for(int i=0;i<origins.length;i++){
                    if(!trusted(origins[i]))continue;
                    try{
                        status("DOWNLOAD "+version+" • "+(i==0?picked.source:"RISERVA"));
                        File apk=downloadVerified(origins[i],sha,bytes);
                        if(apk==null)throw new IOException("SHA-256 / DIMENSIONE DIVERSI");
                        activity.runOnUiThread(()->launchInstaller(apk));
                        return;
                    }catch(Exception e){
                        error=e;
                    }
                }
                throw new IOException("Download falliti: "+
                    (error==null?"nessun'origine valida":brief(error)));
            }catch(Exception error){
                status("ERRORE AGGIORNA: "+brief(error));
            }finally{busy=false;}
        },"overmind-ota-verified-fallback").start();
    }
    private File downloadVerified(URL url,String sha,long expectedSize)throws Exception{
        HttpURLConnection c=openHttps(url,30000);
        File tmp=new File(activity.getCacheDir(),"overmind-update.part");
        File apk=new File(activity.getCacheDir(),"overmind-update.apk");
        try{
            MessageDigest hash=MessageDigest.getInstance("SHA-256");
            long total=0;
            try(InputStream in=c.getInputStream();
                FileOutputStream out=new FileOutputStream(tmp)){
                byte[] buf=new byte[16384];int n;
                while((n=in.read(buf))!=-1){
                    total+=n;
                    if(total>expectedSize||total>MAX_APK_BYTES)
                        throw new IOException("Dimensione APK inattesa");
                    hash.update(buf,0,n);
                    out.write(buf,0,n);
                }
                out.getFD().sync();
            }
            StringBuilder actual=new StringBuilder();
            for(byte b:hash.digest())
                actual.append(String.format(Locale.US,"%02x",b&0xff));
            if(total!=expectedSize||!sha.equals(actual.toString()))return null;
            if(apk.exists()&&!apk.delete())throw new IOException("Cache APK occupata");
            if(!tmp.renameTo(apk))throw new IOException("Cache download non finalizzata");
            return apk;
        }finally{c.disconnect();if(tmp.exists())tmp.delete();}
    }
    private void launchInstaller(File apk){
        try{
            if(Build.VERSION.SDK_INT>=26 &&
               !activity.getPackageManager().canRequestPackageInstalls()){
                status("CONSENTI INSTALLAZIONE DA QUESTA APP E RIPREMI AGGIORNA");
                activity.startActivity(new Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:"+activity.getPackageName())));
                return;
            }
            Uri content=Uri.parse("content://"+activity.getPackageName()+
                                  ".updates/update.apk");
            Intent i=new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(content,"application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(i);
            status("CONFERMA L'INSTALLAZIONE IN ANDROID");
        }catch(Exception ex){status("INSTALLAZIONE NON AVVIATA: "+brief(ex));}
    }
    private long versionCode()throws PackageManager.NameNotFoundException{
        PackageInfo p=activity.getPackageManager()
            .getPackageInfo(activity.getPackageName(),0);
        return Build.VERSION.SDK_INT>=28?p.getLongVersionCode():p.versionCode;
    }
    private static String brief(Exception error){
        String s=error.getMessage();
        if(s==null)s=error.getClass().getSimpleName();
        s=s.replace('\n',' ').replace('\r',' ');
        return s.substring(0,Math.min(75,s.length()));
    }
}
