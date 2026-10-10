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

/** Grants read-only access to ONE user-generated private JSON report.
 * Not exported, no file traversal, no directory or upload endpoints.
 */
public final class GptReportProvider extends ContentProvider {
    @Override public boolean onCreate(){return true;}
    private File check(Uri uri)throws FileNotFoundException{
        if(getContext()==null||uri==null||
           !"content".equals(uri.getScheme())||
           !(getContext().getPackageName()+GptReport.AUTH_SUFFIX).equals(uri.getAuthority())||
           !"/report.json".equals(uri.getPath()))
            throw new FileNotFoundException("Invalid ZERO report URI");
        File f=new File(new File(getContext().getCacheDir(),"zero_gpt"),GptReport.NAME);
        if(!f.isFile())throw new FileNotFoundException("Export required before share");
        return f;
    }
    @Override public ParcelFileDescriptor openFile(Uri uri,String mode)
        throws FileNotFoundException{
        if(!"r".equals(mode))throw new FileNotFoundException("Read only");
        return ParcelFileDescriptor.open(check(uri),ParcelFileDescriptor.MODE_READ_ONLY);
    }
    @Override public String getType(Uri uri){return "application/json";}
    @Override public Cursor query(Uri uri,String[] projection,String selection,
                                  String[] args,String sortOrder){
        try{
            File f=check(uri);
            String[] names=projection==null
                ?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:projection;
            MatrixCursor cursor=new MatrixCursor(names);
            Object[] values=new Object[names.length];
            for(int i=0;i<names.length;i++){
                if(OpenableColumns.DISPLAY_NAME.equals(names[i]))values[i]=GptReport.NAME;
                else if(OpenableColumns.SIZE.equals(names[i]))values[i]=f.length();
                else values[i]=null;
            }
            cursor.addRow(values);return cursor;
        }catch(Exception e){return null;}
    }
    @Override public Uri insert(Uri uri,ContentValues values){
        throw new UnsupportedOperationException("Read only");}
    @Override public int update(Uri uri,ContentValues values,String selection,String[] args){
        throw new UnsupportedOperationException("Read only");}
    @Override public int delete(Uri uri,String selection,String[] args){
        throw new UnsupportedOperationException("Read only");}
}
