package streethawk.com.pushping.service;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import streethawk.com.pushping.receiver.AlarmTaskReceiver;
import streethawk.com.pushping.util.MailAuthenticator;

public class PushPingService extends Service implements ISHObserver {

    private final String TAG = "PushPingApp";
    private final int DURATION_PING_CHECK = 1000 * 60 * 5;  //5 minutes
    private final int DURATION_HEARTBEAT = 1000 * 60 * 60;  //1 hour
    private String SERVER = "https://api.streethawk.com/v3";
    static int ALERT_STATUS = 0;
    final String KEY_HOST = "shKeyHost";
    final String SHSHARED_PREF_PERM = "shstoreperm";

    String TIME_LAST_PUSH = "timeLastPush";
    String LAST_SERVER_MSG = "lastServerMessage";
    String APP_SHARED_PREF = "shsample_sharedPref";

    static Context context;
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
        Push.getInstance(getApplicationContext()).registerSHObserver(this);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        AlarmManager alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmTaskReceiver.class);
        intent.setAction("com.streethawk.pushtester.alarmtask");
        PendingIntent appStatusIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent trackingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.cancel(trackingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), DURATION_PING_CHECK, appStatusIntent);

        AlarmManager alarmManager2 = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(getApplicationContext(), AlarmTaskReceiver.class);
        intent2.setAction("com.streethawk.pushtester.heartbeat");
        PendingIntent trackingIntent2 = PendingIntent.getBroadcast(getBaseContext(), 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.cancel(trackingIntent2);
        PendingIntent appStatusIntent2 = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager2.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), DURATION_HEARTBEAT, appStatusIntent2);

    }

    public void reportToServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://bdd.streethawk.com/lastrun.txt");
                    Log.e("Anurag", "BDD URL " + url);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    BufferedReader reader = null;
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String answer = reader.readLine();
                    long serverTime = -1;
                    final String PREF_NAME = "shsample_sharedPref";
                    SharedPreferences prefss = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    long lastPing = prefss.getLong(TIME_LAST_PUSH, -1);
                    long diff;
                    long currentTime = (long) (System.currentTimeMillis() / 1000);
                    try {
                        serverTime = (long) Double.parseDouble(answer);

                        diff = serverTime - currentTime;
                        if (-1 > diff) {
                            diff *= -1;
                        }
                        Log.e("Anurag", "currentTime " + currentTime);
                        Log.e("Anurag", "Server Pinf " + serverTime);
                        Log.e("Anurag", "Diff " + diff);
                        if (diff > 600) {
                            long minutes = (currentTime - serverTime) / 60;
                            String message = "Android Didnt received push for last " + minutes + " minutes";
                            sendComplaintToserver(message);
                            return;
                        }
                        diff = serverTime - lastPing;
                        if (-1 > diff) {
                            diff *= -1;
                        }
                    } catch (NumberFormatException e) {
                        return;
                    }
                    Log.e("Anurag", "lastPing " + lastPing);
                    Log.e("Anurag", "Server Pinf " + serverTime);
                    Log.e("Anurag", "Diff " + diff);
                    long minutes = (currentTime - lastPing) / 60;
                    String message = "Android Didnt received push for last " + minutes + " minutes";
                    if (diff > 600) {
                        sendComplaintToserver(message);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Anurag", "exception " + e.getMessage());
                    sendComplaintToserver("Android exception " + e.getMessage());
                }
            }
        }).start();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }


    public void sendSlackMessage(final String channel, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://slack.com/api/chat.postMessage");
                    Log.e("Anurag", "Sending result to server");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    OutputStream os = conn.getOutputStream();
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("token", "xoxp-2189686668-2189426407-96307831140-068dc1354fff25a332141eb090b0cf5a");
                    params.put("channel", channel);
                    params.put("text", message);
                    params.put("username", "pushping");
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(params));
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();
                    BufferedReader reader = null;
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String answer = reader.readLine();
                    long resultCode = (long) conn.getResponseCode();
                    String resultText = conn.getResponseMessage();
                    Log.e("Anurag", "resultCode " + answer);
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void sendEmail(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.e("Anurag", "Sending email");
                MailAuthenticator m = new MailAuthenticator("parserdemosh2@gmail.com", "Reliance@123");
                String[] toArr = {"devops@streethawk.com"};
                m.setTo(toArr);
                m.setFrom("parserdemosh2@gmail.com");
                m.setSubject(message);
                m.setBody(message);
                try {
                    if (m.send()) {
                        Log.e("Anurag", "Email was sent");
                    } else {
                        Log.e("Anurag", "Error in sending email");
                    }
                } catch (Exception e) {
                    Log.e("MailApp", "Could not send email", e);
                }
            }
        }).start();
    }

    private void sendComplaintToserver(final String message) {
        sendEmail(message);
        sendSlackMessage("#devops", message + " <@U0KPM4X42|jagguli> <@U02MJ441R|yichang> <@U025KL6KQ|david> <@U025KCJBZ|anurag>  ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Starting service");
        context = getApplicationContext();
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

    @Override
    public void onReceivePushData(PushDataForApplication pushData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss");
        final String currentDateandTime = sdf.format(new Date());

        final String PREF_NAME = "shsample_sharedPref";
        SharedPreferences prefss = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefss.edit();
        long time = (long) (System.currentTimeMillis() / 1000);
        e.putLong(TIME_LAST_PUSH, time);
        e.commit();
        Log.e("Anurag", "Store time " + time);

    }

    @Override
    public void onReceiveResult(PushDataForApplication resultData, int result) {

    }

    @Override
    public void onReceiveNonSHPushPayload(Bundle pushPayload) {

    }
}
