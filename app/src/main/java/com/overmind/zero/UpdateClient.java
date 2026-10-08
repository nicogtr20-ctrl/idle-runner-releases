package com.overmind.zero;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Locale;
import org.json.JSONObject;

/**
 * In-app Android update client.
 * Vercel hosts manifest and APK. This code does not install APKs silently:
 * Android Package Installer always presents a system approval screen.
 */
public final class UpdateClient {
    public interface StatusListener {
        void changed(String text);
    }

    public static final String MANIFEST_URL="https://overmind-zero-updates.vercel.app/latest.json";
    private static final String EXPECTED_HOST="overmind-zero-updates.vercel.app";
    private static final long MAX_APK_BYTES=200L*1024*1024;
    private final Activity activity;
    private final StatusListener listener;
    private volatile boolean busy=false;

    public UpdateClient(Activity activity,StatusListener listener){
        this.activity=activity;this.listener=listener;
    }
    private void status(String message){
        activity.runOnUiThread(()->listener.changed(message));
    }
    public void checkAndInstall(){
        if(busy){status("CONTROLLO GIA IN CORSO");return;}
        busy=true;
        status("RICERCA AGGIORNAMENTI...");
        new Thread(()->{
            try{
                JSONObject manifest=fetchManifest();
                if(!manifest.optBoolean("available",false)){
                    status("NESSUN AGGIORNAMENTO DISPONIBILE");return;
                }
                String appId=manifest.optString("applicationId","");
                if(!activity.getPackageName().equals(appId)){
                    status("PACCHETTO NON COMPATIBILE");return;
                }
                int newVersion=manifest.optInt("versionCode",0);
                if(newVersion<=versionCode()){
                    status("HAI GIA LA VERSIONE PIU RECENTE");return;
                }
                String uri=manifest.optString("apkUrl","");
                String expectedHash=manifest.optString("sha256","").toLowerCase(Locale.ROOT);
                long expectedSize=manifest.optLong("sizeBytes",0);
                if(!expectedHash.matches("[0-9a-f]{64}")||expectedSize<1000||expectedSize>MAX_APK_BYTES){
                    status("DATI AGGIORNAMENTO NON VALIDI");return;
                }
                URL url=new URL(uri);
                if(!"https".equalsIgnoreCase(url.getProtocol())||!EXPECTED_HOST.equalsIgnoreCase(url.getHost())){
                    status("SERVER DI DOWNLOAD NON AUTORIZZATO");return;
                }
                status("DOWNLOAD VERSIONE "+manifest.optString("versionName","NUOVA")+"...");
                File update=downloadVerified(url,expectedHash,expectedSize);
                if(update==null){status("INTEGRITA APK NON VALIDA");return;}
                activity.runOnUiThread(()->launchInstaller(update));
            }catch(Exception err){
                status("IMPOSSIBILE AGGIORNARE: "+cleanMessage(err));
            }finally{busy=false;}
        },"overmind-ota-check").start();
    }

    private JSONObject fetchManifest() throws Exception {
        URL target=new URL(MANIFEST_URL+"?t="+System.currentTimeMillis());
        HttpURLConnection connection=(HttpURLConnection)target.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(9000);connection.setReadTimeout(9000);
        connection.setRequestProperty("Accept","application/json");
        try{
            if(connection.getResponseCode()!=200)throw new IOException("manifest HTTP "+connection.getResponseCode());
            ByteArrayOutputStream buffer=new ByteArrayOutputStream();
            try(InputStream in=connection.getInputStream()){
                byte[] bytes=new byte[4096];int read,total=0;
                while((read=in.read(bytes))!=-1){
                    total+=read;if(total>20000)throw new IOException("manifest troppo grande");
                    buffer.write(bytes,0,read);
                }
            }
            JSONObject manifest=new JSONObject(buffer.toString("UTF-8"));
            if(manifest.optInt("schemaVersion",0)!=1)throw new IOException("schema non supportato");
            return manifest;
        }finally{connection.disconnect();}
    }

    private File downloadVerified(URL url,String wantedSha,long wantedSize)throws Exception{
        HttpURLConnection c=(HttpURLConnection)url.openConnection();
        c.setInstanceFollowRedirects(false);
        c.setConnectTimeout(12000);c.setReadTimeout(25000);
        File tmp=new File(activity.getCacheDir(),"overmind-update.part");
        File apk=new File(activity.getCacheDir(),"overmind-update.apk");
        try{
            if(c.getResponseCode()!=200)throw new IOException("APK HTTP "+c.getResponseCode());
            MessageDigest md=MessageDigest.getInstance("SHA-256");
            long got=0;byte[] buf=new byte[16384];
            try(InputStream in=c.getInputStream();FileOutputStream out=new FileOutputStream(tmp)){
                int n;
                while((n=in.read(buf))!=-1){
                    got+=n;if(got>MAX_APK_BYTES||got>wantedSize)throw new IOException("dimensione APK inattesa");
                    md.update(buf,0,n);out.write(buf,0,n);
                }
                out.getFD().sync();
            }
            StringBuilder actual=new StringBuilder();
            for(byte b:md.digest())actual.append(String.format(Locale.US,"%02x",b&0xff));
            if(got!=wantedSize||!actual.toString().equals(wantedSha))return null;
            if(apk.exists()&&!apk.delete())throw new IOException("cache APK occupata");
            if(!tmp.renameTo(apk))throw new IOException("impossibile finalizzare APK");
            return apk;
        }finally{c.disconnect();if(tmp.exists())tmp.delete();}
    }
    private void launchInstaller(File apk){
        try{
            if(Build.VERSION.SDK_INT>=26&&!activity.getPackageManager().canRequestPackageInstalls()){
                status("ABILITA INSTALLAZIONE PER QUESTA APP, POI RIPREMI AGGIORNA");
                Intent settings=new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                  Uri.parse("package:"+activity.getPackageName()));
                activity.startActivity(settings);
                return;
            }
            Uri content=Uri.parse("content://"+activity.getPackageName()+".updates/update.apk");
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(content,"application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(intent);
            status("CONFERMA L'INSTALLAZIONE IN ANDROID");
        }catch(Exception err){status("INSTALLAZIONE NON AVVIATA: "+cleanMessage(err));}
    }
    private long versionCode()throws PackageManager.NameNotFoundException{
        PackageInfo p=activity.getPackageManager().getPackageInfo(activity.getPackageName(),0);
        return Build.VERSION.SDK_INT>=28?p.getLongVersionCode():p.versionCode;
    }
    private String cleanMessage(Exception e){
        String m=e.getMessage();if(m==null)m=e.getClass().getSimpleName();
        m=m.replace('\n',' ').replace('\r',' ');
        return m.substring(0,Math.min(48,m.length()));
    }
}
