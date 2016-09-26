package com.streethawk.streethawkapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.streethawk.library.push.ISHObserver;
import com.streethawk.library.push.Push;
import com.streethawk.library.push.PushDataForApplication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class PushPingService extends Service implements ISHObserver {

    private final String TAG = "PushPingApp";

    private final int DURATION_PING_CHECK = 1000 * 60 *5;  //5minutes
    private String SERVER = "https://api.streethawk.com/v3";
    static int ALERT_STATUS = 0;
    final String KEY_HOST = "shKeyHost";
    final String SHSHARED_PREF_PERM = "shstoreperm";

    String TIME_LAST_PUSH = "timeLastPush";
    String LAST_SERVER_MSG = "lastServerMessage";
    String APP_SHARED_PREF = "shsample_sharedPref";

    Context context;

    private static boolean FLAG_REPORT_NO_PUSH = false;  // true will report error to server


    //"https://api.streethawk.com/v3/debug/?action=push_android"&alert=1";

    public PushPingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setServerVariable(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SERVER = prefs.getString(KEY_HOST, null);
    }

    private void startPushMonitoring() {
        Log.e(TAG, "Start push monitoring");
        FLAG_REPORT_NO_PUSH = true;
        context = getApplicationContext();
        Push.getInstance(getApplicationContext()).registerSHObserver(this);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmTaskReceiver.class);
        intent.setAction("com.streethawk.pushtester.alarmtask");
        PendingIntent appStatusIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), DURATION_PING_CHECK, appStatusIntent);
    }

    public void reportToServer() {
        Log.e("Anurag", "Report to server");
        if (FLAG_REPORT_NO_PUSH) {
            Log.e("Anurag", "Report to server2");
            ALERT_STATUS = 1;
            sendComplaintToserver();
        }
    }

    private void sendComplaintToserver() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(SERVER + "/debug/?action=push_android&alert=" + ALERT_STATUS);
                    Log.e("Anurag", "Sending result to server");
                    Log.e(TAG, "URL " + url);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    long resultCode = (long) conn.getResponseCode();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String currentDateandTime = sdf.format(new Date());
                    String resultText = conn.getResponseMessage();
                    String message = currentDateandTime + resultCode + " " + resultText;
                    /*
                    SharedPreferences pref = PushPingService.this.getSharedPreferences(APP_SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor e = pref.edit();
                    e.putString(LAST_SERVER_MSG, message);
                    e.commit();
                    */
                   // Toast.makeText(PushPingService.this,message,Toast.LENGTH_SHORT).show();
                    Log.e("Anurag","Message "+message);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Starting service");
        Toast.makeText(PushPingService.this, "Starting push ping service", Toast.LENGTH_SHORT).show();
        setServerVariable(getApplicationContext());
        startPushMonitoring();
    }

    @Override
    public void shReceivedRawJSON(String title, String message, String json) {

    }

    @Override
    public void shNotifyAppPage(String pageName) {

    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(new Date());

    }

    @Override
    public void onReceivePushData(PushDataForApplication pushData) {
        Log.e(TAG, "Push received in service");
        if (1 == ALERT_STATUS) {
            ALERT_STATUS = 0;
            FLAG_REPORT_NO_PUSH = false;
            sendComplaintToserver();
        }
        String currentTime = getCurrentDateTime();
        Log.e("Anurag", "Context " + getApplicationContext());
        SharedPreferences pref = getApplicationContext().getSharedPreferences(APP_SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor e = pref.edit();
        e.putString(TIME_LAST_PUSH, currentTime);
        e.commit();
    }

    @Override
    public void onReceiveResult(PushDataForApplication resultData, int result) {

    }

    @Override
    public void onReceiveNonSHPushPayload(Bundle pushPayload) {

    }
}
