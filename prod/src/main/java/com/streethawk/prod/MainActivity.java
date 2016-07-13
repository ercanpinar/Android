package com.streethawk.prod;

import android.*;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;
import com.streethawk.library.geofence.SHCoreModuleReceiver;
import com.streethawk.library.geofence.SHGeofence;
import com.streethawk.library.push.ISHObserver;
import com.streethawk.library.push.Push;
import com.streethawk.library.push.PushDataForApplication;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * This app is only for testing push messages on different platform
 */
public class MainActivity extends AppCompatActivity implements ISHObserver{
    private final int PERMISSIONS_LOCATION = 112233;
    private String PREF_NAME  = "shtestpref";
    private String BOOL_SEND_ALERT = "shsendalert";
    private String BOOL_TOGGLE_STATE = "shtooglestate";

    private final String SERVER_PROD = "https://api.streethawk.com/v3/debug/?push_android&alert=";
    private final String SERVER_STAGING = "https://staging.streethawk.com/v3/debug/?push_android&alert=";
    private final String SERVER_HAWK0 = "https://hawk0.streethawk.com/v3/debug/?action=push_android&alert=";

    private final String TEST_SERVER = "http://www.google.com";

    private String SERVER = SERVER_HAWK0;
    private final String PUSH_MONITOR_EVENT = "com.streethawk.pushmonitorevent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        StreetHawk.INSTANCE.setAppKey("SHSample");
        Push.getInstance(context).registerForPushMessaging("491295755890");
        Push.getInstance(context).registerSHObserver(this);
        Push.getInstance(context).setUseCustomDialog(true);   // this will prevent push from displaying on screen
        StreetHawk.INSTANCE.init(getApplication());
        LocalBroadcastManager.getInstance(this).registerReceiver(pushMonitor,new IntentFilter(PUSH_MONITOR_EVENT));
    }

    public void StartGeofence(View view){
        displayPermissionDialog();
    }

    @Override
    public void onResume(){
        super.onResume();

        final String TAG_CUID = "testrunner@streethawk";

        //Display InstallID of the install
        TextView installID = (TextView)findViewById(R.id.installid);
        installID.setText(StreetHawk.INSTANCE.getInstallId(this));

        //Display server install is talking to
        TextView server = (TextView)findViewById(R.id.server);
        server.setText(SERVER);

        //Display app_key
        TextView appKey = (TextView)findViewById(R.id.appkey);
        appKey.setText(StreetHawk.INSTANCE.getAppKey(this));

        //Display app_key
        TextView tag = (TextView)findViewById(R.id.tag);
        tag.setText(TAG_CUID);
        tag.setText(TAG_CUID);
    }

    private void startPushMonitoring(){
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(PUSH_MONITOR_EVENT);
        PendingIntent appStatusIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long DEBUG_INTERVAL_5MINUTES = 300000l;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), DEBUG_INTERVAL_5MINUTES, appStatusIntent);
        setAlertFlag(false);
    }


    public void startPushMonitoring(View view){
        Toast.makeText(this,"Starting push monitoring",Toast.LENGTH_LONG).show();
        startPushMonitoring();
    }

    private BroadcastReceiver pushMonitor  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(!getAlertFlag()){
                SendAlertToServer(SERVER+"1");
                setToggleState(true);
            }
            setAlertFlag(true);  // reset for notifying server
        }
    };

    /**
     * Store value of alert flag
     * @param flag
     */
    private void setAlertFlag(boolean flag){
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e  = prefs.edit();
        e.putBoolean(BOOL_SEND_ALERT,flag);
        e.commit();
    }

    /**
     * Get current value of alert flag
     * @return
     */
    private boolean getAlertFlag(){
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(BOOL_SEND_ALERT,true);
    }


    private void setToggleState(boolean state){
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(BOOL_TOGGLE_STATE,state);
        e.commit();
    }


    private boolean getToggleState(){
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(BOOL_TOGGLE_STATE,false);

    }




    /**
     * Generate a new relic alert
     * @param view
     */
    public void SendAlert(View view){
        SendAlertToServer(SERVER+"1");
    }

    /**
     * Display a local notification when in error
     * @param title
     * @param message
     */
    private void DisplayNotification(String title, String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
    }
    private void SendAlertToServer(final String server){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(server);
                    final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.connect();
                    connection.getResponseCode();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.e("SH_PING_TEST", "Error reported to server");
                    }else{
                        Log.e("SH_PING_TEST", "Alert Failed " + connection.getResponseMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getApplicationContext(),connection.getResponseMessage(),Toast.LENGTH_LONG).show();
                                    DisplayNotification("Error","NewRelic endpoint down");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    connection.disconnect();
                }catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private boolean checkForLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int coarseLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int fineLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if ((coarseLocation == PackageManager.PERMISSION_GRANTED) || (fineLocation == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private DialogInterface.OnClickListener askPermission(){
        return new DialogInterface.OnClickListener(){
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_LOCATION);
            }
        };
    }

    private void displayPermissionDialog(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!checkForLocationPermission(this)) {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                    String DEFAULT_PERMISSION_TITLE   = "Permission Required";
                    String DEFAULT_PERMISSION_MESSAGE = "Would you like to grant us location permission for monitoring geofences?";
                    String DEFAULT_BUTTON_TEXT        = "Okay";
                    int id;
                    String title;
                    String message;
                    String buttonTitle;

                    Context context = getApplicationContext();
                    String packageName = context.getPackageName();
                    id = context.getResources().getIdentifier("SH_GEO_PERMISSION_TITLE", "string", packageName);
                    if (0 == id)
                        title = DEFAULT_PERMISSION_TITLE;
                    else
                        title = context.getString(id);

                    id = context.getResources().getIdentifier("SH_GEO_PERMISSION_MESSAGE", "string", packageName);
                    if (0 == id)
                        message = DEFAULT_PERMISSION_MESSAGE;
                    else
                        message = context.getString(id);

                    id = context.getResources().getIdentifier("SH_GEO_PERMISSION_BUTTON_TEXT", "string", packageName);
                    if (0 == id)
                        buttonTitle = DEFAULT_BUTTON_TEXT;
                    else
                        buttonTitle = context.getString(id);

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle(title);
                    builder.setMessage(message);
                    builder.setPositiveButton(buttonTitle,askPermission());
                }else{
                    this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_LOCATION);
                }
            }else{
                Log.i(Util.TAG,"App already has the permission");

            }
        } else {
            Log.e(Util.TAG,"Not requesting permission "+Build.VERSION.SDK_INT+" "+Build.VERSION_CODES.M);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SHGeofence.getInstance(this).startGeofenceMonitoring();
                } else {

                    Log.e(Util.TAG, "Permission not granted by user");
                }
            }
        }
    }

    @Override
    public void shReceivedRawJSON(String title, String message, String json) {

    }

    @Override
    public void shNotifyAppPage(String pageName) {

    }

    @Override
    public void onReceivePushData(PushDataForApplication pushData) {
        setAlertFlag(false);
        boolean taskRegistered = (PendingIntent.getBroadcast(this, 0,
                new Intent(PUSH_MONITOR_EVENT),
                PendingIntent.FLAG_NO_CREATE) != null);
        if (!taskRegistered) {
            startPushMonitoring();
        }
        if(getToggleState()) {
            setToggleState(false);
            SendAlertToServer(SERVER+"0");
        }
    }

    @Override
    public void onReceiveResult(PushDataForApplication resultData, int result) {

    }

    @Override
    public void onReceiveNonSHPushPayload(Bundle pushPayload) {

    }
}
