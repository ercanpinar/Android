package com.streethawk.streethawkapp.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;
import com.streethawk.library.push.ISHObserver;
import com.streethawk.library.push.Push;
import com.streethawk.library.push.PushDataForApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class TestService extends Service implements ISHObserver {
    public TestService() {
    }

    private final String PREF_NAME = "shtestpref";
    private final String BOOL_SEND_ALERT = "shsendalert";
    private final String BOOL_SEM = "shsemaphore";

    private final String CONST_PING_TIME = "pingtime";


    private final String SERVER_PROD = "https://api.streethawk.com/v3/debug/?push_android&alert=1";
    private final String SERVER_STAGING = "https://staging.streethawk.com/v3/debug/?push_android&alert=1";
    private final String SERVER_HAWK = "https://hawk.streethawk.com/v3/debug/?push_android&alert=1";

    private final String TEST_SERVER = "http://www.google.com";

    private String SERVER = SERVER_HAWK;
    private final int WAIT_MINS = 1000 * 60 * 5;  // 5 minutes

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        Push.getInstance(this).registerSHObserver(this);
    }

    /**
     * Store value of alert flag
     *
     * @param flag
     */
    private void setAlertFlag(boolean flag) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(BOOL_SEND_ALERT, flag);
        e.commit();
    }

    private void holdSemaphore() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(BOOL_SEM, true);
        e.commit();
    }

    private void releaseSemaphore() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(BOOL_SEM, false);
        e.commit();
    }

    private boolean getSemaphoreValue() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(BOOL_SEM, false);
    }

    /**
     * Get current value of alert flag
     *
     * @return
     */
    private boolean getAlertFlag() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(BOOL_SEND_ALERT, true);
    }

    /**
     * Generate a new relic alert
     *
     * @param view
     */
    public void SendAlert(View view) {
        SendAlertToServer();
    }

    /**
     * Display a local notification when in error
     *
     * @param title
     * @param message
     */
    private void DisplayNotification(String title, String message) {

        Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentTitle(title);
        builder.setAutoCancel(true);

        Context context = activity.getApplicationContext();
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                Util.getAppIcon(context));
        builder.setLargeIcon(icon);

        builder.setSmallIcon(Util.getAppIcon(context));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(112233, notification);
        }

    }

    public void SendAlertToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Log.e("Anurag", "Sending alert to server");
                    URL url = new URL(SERVER);
                    final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.connect();
                    connection.getResponseCode();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.e("SH_PING_TEST", "Error reported to server");
                    } else {
                        Log.e("SH_PING_TEST", "Alert Failed " + connection.getResponseMessage());
                        DisplayNotification("Error", "NewRelic endpoint down");
                    }
                    connection.disconnect();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    @Override
    public void shReceivedRawJSON(String title, String message, String json) {
        try {
            Log.e("Anurag", json);
            JSONObject myJson = new JSONObject(json);
            String payload = myJson.getString("ping");
            if (null != payload) {
                setAlertFlag(false);
                if (getSemaphoreValue()) {
                    return;
                }
                //Display this in install info
                SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String timeNow = dtf.format(new Date());
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();
                e.putString(CONST_PING_TIME, timeNow);
                e.commit();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            holdSemaphore();
                            setAlertFlag(true);
                            wait(WAIT_MINS);
                            if (getAlertFlag()) {
                                SendAlertToServer();
                            }
                            releaseSemaphore();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shNotifyAppPage(String pageName) {

    }

    @Override
    public void onReceivePushData(PushDataForApplication pushData) {

    }

    @Override
    public void onReceiveResult(PushDataForApplication resultData, int result) {

    }

    @Override
    public void onReceiveNonSHPushPayload(Bundle pushPayload) {

    }
}
