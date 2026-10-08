package com.overmind.zero;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;

/** Exposes just the SHA-256-verified update APK to Android's installer. */
public final class UpdateFileProvider extends ContentProvider {
    @Override public boolean onCreate(){return true;}
    private File valid(Uri uri)throws FileNotFoundException{
        if(getContext()==null||uri==null||
            !("content".equals(uri.getScheme()))||
            !(getContext().getPackageName()+".updates").equals(uri.getAuthority())||
            !"/update.apk".equals(uri.getPath()))throw new FileNotFoundException("Not a valid update URI");
        File apk=new File(getContext().getCacheDir(),"overmind-update.apk");
        if(!apk.isFile())throw new FileNotFoundException("APK not downloaded");
        return apk;
    }
    @Override public ParcelFileDescriptor openFile(Uri uri,String mode)throws FileNotFoundException{
        if(!"r".equals(mode))throw new FileNotFoundException("Read only");
        return ParcelFileDescriptor.open(valid(uri),ParcelFileDescriptor.MODE_READ_ONLY);
    }
    @Override public String getType(Uri uri){return "application/vnd.android.package-archive";}
    @Override public Cursor query(Uri uri,String[] projection,String selection,String[] args,String sortOrder){
        try{
            File apk=valid(uri);
            String[] columns=projection==null?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:projection;
            MatrixCursor cursor=new MatrixCursor(columns);
            Object[] row=new Object[columns.length];
            for(int i=0;i<columns.length;i++){
                if(OpenableColumns.DISPLAY_NAME.equals(columns[i]))row[i]="OVERMIND-ZERO.apk";
                else if(OpenableColumns.SIZE.equals(columns[i]))row[i]=apk.length();
                else row[i]=null;
            }
            cursor.addRow(row);return cursor;
        }catch(Exception e){return null;}
    }
    @Override public Uri insert(Uri uri,ContentValues values){throw new UnsupportedOperationException();}
    @Override public int delete(Uri uri,String selection,String[] args){throw new UnsupportedOperationException();}
    @Override public int update(Uri uri,ContentValues values,String selection,String[] args){throw new UnsupportedOperationException();}
}
