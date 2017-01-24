package com.streethawk.library.pointzi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import com.streethawk.library.core.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Utility functions for authoring
 */
public class Authoring extends WidgetList {


    final String MSG_ENTER_AUTHORING_MODE = "Device entered authoring mode";
    private Activity mActivity;

    /**
     * Function displayes simple toast messages
     * @param message
     */
    private void displayToastMessage(String message){
        if(null==mActivity)
            return;
        if(null==message)
            return;
        Context context = mActivity.getApplicationContext();
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    private void sendScreenShotToServer(final Context context, final File file,final String json) {
        if(null==context)
            return;
        final String EXCEPTION_FILE = "exception_file";
        if (Util.isNetworkConnected(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "*****";
                        int bytesRead, bytesAvailable, bufferSize;
                        final int maxBufferSize = 1 * 1024 * 1024;
                        byte[] buffer;
                        FileInputStream fileInputStream = new FileInputStream(file);

                        java.net.URL url = Util.getCrashReportingUrl(context);

                        HttpsURLConnection conn = null;
                        final String installId =Util.getInstallId(context);
                        if(null==installId)
                            return;
                        final String app_key  = Util.getAppKey(context);
                        conn = (HttpsURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestProperty("X-Installid", installId);
                        conn.setRequestProperty("X-App-Key", app_key);
                        String libVersion = Util.getLibraryVersion();
                        conn.setRequestProperty("X-Version",libVersion);
                        conn.setRequestProperty("User-Agent", app_key + "(" + libVersion + ")");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Cache-Control", "no-cache");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("charset", "utf-8");
                        DataOutputStream request = new DataOutputStream(conn.getOutputStream());
                        request.writeBytes(twoHyphens + boundary + lineEnd);
                        request.writeBytes("Content-Disposition: form-data; name=\"installid \""+ lineEnd);
                        request.writeBytes(lineEnd);
                        request.writeBytes(installId);
                        request.writeBytes(lineEnd);
                        request.writeBytes(twoHyphens + boundary + lineEnd);
                        request.writeBytes("Content-Disposition: form-data; name=\"created \"" + lineEnd);
                        request.writeBytes(lineEnd);
                        request.writeBytes(twoHyphens + boundary + lineEnd);
                        request.writeBytes("Content-Disposition: form-data; name=\"" + EXCEPTION_FILE + "\"; filename=\"" + file.getName() + "\"" + lineEnd);
                        request.writeBytes(lineEnd);
                        // create a buffer of  maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        while (bytesRead > 0) {
                            request.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        }
                        request.writeBytes(lineEnd);
                        request.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        request.flush();
                        request.close();
                        InputStream error = conn.getErrorStream();
                        if(null==error){
                            BufferedReader reader = null;
                            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String answer = reader.readLine();
                            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                file.delete();
                                if (null == answer)
                                    return;
                                if (answer.isEmpty())
                                    return;
                                //manager.processAppStatusCall(answer);
                                // TODO: add routine to process appstatus call here
                            } else {
                                //manager.processErrorAckFromServer(answer);
                                // TODO: add routine to process error appstatus call here
                            }
                        }
                        conn.disconnect();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void enterAuthoringMode(Activity activity){
        if(null==activity)
            return;
        mActivity = activity;
        fillViewList(activity);
        displayWidgetListForSaving();
        Context context = activity.getApplicationContext();
        displayToastMessage(MSG_ENTER_AUTHORING_MODE);

        View rootView = mActivity.getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        try {
            //TODO file names
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(context.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");

        String authority = context.getPackageName()+"pointzi.fileprovider";

        Uri contentUri = FileProvider.getUriForFile(context,authority, newFile);

        if (contentUri != null) {

            sendScreenShotToServer(context,newFile,null);  //TODO

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, mActivity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            mActivity.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }
}
