/*
 * Copyright (c) StreetHawk, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.streethawk.library.push;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Push implements Constants{
    private static Context mContext;
    private static Push mPush;


    private final String GSF_PACKAGE = "com.google.android.gsf";
    private final String PERMISSION_GCM_INTENTS = "com.google.android.c2dm.permission.SEND";
    private final String INTENT_FROM_GCM_MESSAGE = "com.google.android.c2dm.intent.RECEIVE";
    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int GOOGLE_PLAY_SERVICES_ISSUE_NOTOFOCATION_ID = -1;
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final String SHGCM_SENDER_KEY_APP = "shgcmsenderkeyapp";
    private static final String SUBTAG = "PUSH ";
    private static ISHObserver mISHObserverObject = null;

    private Push() {
    }

    /**
     * Get instance of Push Class
     * @param context Application context
     * @return instance of push class
     */
    public static Push getInstance(Context context) {
        if (null == mPush)
            mPush = new Push();
        mContext = context;
        return mPush;
    }

    /**
     * Use forcePushToNotificationBar if you want to forcefully display push notifications in notification bar in spite of your application
     * running in foreground. A typical use case will be for gaming applications, where user is playing the game and you may not want to interrupt the user
     * by displaying a dialog inside application's context. Remember to reset status to avoid all messages from being displayed in notification bar only.
     *
     * @param status true to force notification to notification bar. False to reset the setting
     */
    public void forcePushToNotificationBar(boolean status) {
        if(null==mContext)
            return;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(SHFORCEPUSHTOBG, status);
        e.commit();
    }

    /**
     * Save senderID
     * @param senderId
     */
    private static void saveSenderId(String senderId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(SHGCM_SENDER_KEY_APP, senderId);
        e.commit();
    }

    private boolean isGCMRegistered() {
        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return prefs.getBoolean(Util.SHGCMREGISTERED, false);
    }

    private void updateInstallWithGcmIdIfNeeded(String token) {
        if (isGCMRegistered()) {
            return;
        }else{
            if(token==null){
                SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();
                e.putBoolean(Util.SHGCMREGISTERED, false);
                e.commit();
                return;
            }
            if (token.isEmpty()) {
                SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();
                e.putBoolean(Util.SHGCMREGISTERED, false);
                e.commit();
                return;
            }
        }
        Util.updateAccessData(mContext, token);
    }

    /**
     * Checks if the device has the proper dependencies installed.
     * <p/>
     * This method should be called when the application starts to verify that
     * the device supports GCM.
     *
     * @throws UnsupportedOperationException if the device does not support GCM.
     */
    private boolean checkDevice() {
        int version = Build.VERSION.SDK_INT;
        if (version < 8) {
            Log.e(Util.TAG, "Device doesn't support push notification");
            return false;
        }
        PackageManager packageManager = mContext.getPackageManager();
        try {
            packageManager.getPackageInfo(GSF_PACKAGE, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Util.TAG,"Device does not have package " + GSF_PACKAGE);
        }
        return true;
    }

    private boolean checkReceiver(Context context, Set<String> allowedReceivers, String action) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        Intent intent = new Intent(action);
        intent.setPackage(packageName);


        List<ResolveInfo> receivers = pm.queryBroadcastReceivers(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (receivers.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "No receivers for action " + action);
            return false;
        }

        Log.v(Util.TAG, SUBTAG + "Found " + receivers.size() + " receivers for action " + action);
        for (ResolveInfo receiver : receivers) {
            String name = receiver.activityInfo.name;
            if (!allowedReceivers.contains(name)) {
                //throw new IllegalStateException("Receiver " + name + " is not set with permission " + PERMISSION_GCM_INTENTS);
                Log.e(Util.TAG, SUBTAG + "Receiver " + name + " is not set with permission " + PERMISSION_GCM_INTENTS);
                return false;
            }
        }
        return true;
    }

    private boolean checkManifest() {
        PackageManager packageManager = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
        // check receivers
        PackageInfo receiversInfo = null;
        try {
            receiversInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Util.TAG, SUBTAG + "Could not get receivers for package " + packageName);
            return false;
        }
        ActivityInfo[] receivers = receiversInfo.receivers;
        if (receivers == null || receivers.length == 0) {
            Log.e(Util.TAG, SUBTAG + "Could not get receivers for package " + packageName);
            return false;
        }
        HashSet<String> allowedReceivers = new HashSet<String>();
        for (ActivityInfo receiver : receivers) {
            if (PERMISSION_GCM_INTENTS.equals(receiver.permission)) {
                allowedReceivers.add(receiver.name);
            }
        }
        if (allowedReceivers.isEmpty()) {
            Log.w(Util.TAG, SUBTAG + "No receiver allowed to receive " + PERMISSION_GCM_INTENTS);
            return false;
        }
        /*
        if(!checkReceiver(mContext, allowedReceivers, INTENT_FROM_GCM_MESSAGE)){
            return false;
        }
        */
        return true;
    }

    /**
     * Call this API to register for StreetHawk push messaging service
     * @param project_number Project number as obtained for Google for your project
     */
    public void registerForPushMessaging(final String project_number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!checkDevice())
                    return;

                if(!checkManifest()){
                    return;
                }
                // Get Registration id
                InstanceID instanceID = InstanceID.getInstance(mContext);
                String token=null;
                try {
                    token = instanceID.getToken(project_number,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                // Save Registration id
                SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                final SharedPreferences.Editor e = prefs.edit();
                e.putString(PUSH_ACCESS_DATA, token);
                e.putString(SHGCM_SENDER_KEY_APP, project_number);
                e.commit();
                addPushModule();
                return;
            }
        }).start();



    }

    /**
     * Call addPushModule() to add push modules in installs which have already been released with StreetHawk core module.
     */
    public void addPushModule(){
        String installId = Util.getInstallId(mContext);
        if(null==installId) {
            // For this case
            Log.e(Util.TAG,SUBTAG+" install not registered when init was called");
            return;
        }
        else{
            if(!isPushRegistered()){
                register();
                if(Util.getPlatformType()== PLATFORM_XAMARIN){
                    StreetHawk.INSTANCE.tagString("sh_module_push","true");
                }
            }
            setAppPageReceiver(mISHObserverObject);
            return;
        }
    }

    private boolean isPushRegistered(){
        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return prefs.getBoolean(Util.SHGCMREGISTERED, false);
    }

    private static void sendNotification(Context context, String title, String message, String submessage, PendingIntent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(Util.getAppIcon(context));
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.addAction(0, submessage, intent);
        builder.setContentIntent(intent);
        notificationManager.notify(GOOGLE_PLAY_SERVICES_ISSUE_NOTOFOCATION_ID, builder.build());
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                String title = Util.getAppName(mContext);
                String message = GooglePlayServicesUtil.getErrorString(resultCode);
                PendingIntent intent = GooglePlayServicesUtil.getErrorPendingIntent(resultCode, mContext, PLAY_SERVICES_RESOLUTION_REQUEST);
                sendNotification(mContext, title, message, "", intent);
            } else {
                Log.e(Util.TAG, SUBTAG + "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private void register() {
        if (!checkPlayServices()) {
            Log.e(Util.TAG, SUBTAG + "Error in google play services.. returning");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                long backoff = BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
                for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                    SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                    String token  = prefs.getString(PUSH_ACCESS_DATA,null);
                    if(null!=token) {
                        updateInstallWithGcmIdIfNeeded(token);
                        return;
                    }else{
                        Log.e(Util.TAG,"Access data is null");
                    }
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }
                    backoff *= 2;
                }
            }
        }).start();
    }

    /**
     * return true if push is enabled for the device.
     * @return
     */
    public boolean isUsePush() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        if (sharedPreferences != null)
            return sharedPreferences.getBoolean(SHGCM_FLAG, true);
        else
            return true;
    }

    /**
     * use this function if you want to dont want to use default dialogs theme and instead use you own cutom theme
     * @param answer
     */
    public void setUseCustomDialog(boolean answer){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(SHUSECUSTOMDIALOG_FLAG, answer);
        e.commit();
    }

    /**
     * use this API if app is developed on cross platform
     * Register your class which implements ISHObserver
     *
     * @param object ISHObserver object
     * @deprecated use {@link #registerSHObserver(ISHObserver)} instead.
     */
    @Deprecated
    public void setAppPageReceiver(ISHObserver object) {
        if (null == object)
            return;
        SHGcmListenerService.setISHObserver(object);
    }

    /**
     * Register class which implements ISHObserver
     * @param object
     */
    public void registerSHObserver(ISHObserver object) {

        if(null==object){
            Log.e(Util.TAG,"ISHObserver instance cannot be null");
            return;
        }
        mISHObserverObject = object;
        SHGcmListenerService.setISHObserver(object);

    }


    private void displayPendingDialog(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String msgId = sharedPreferences.getString(PENDING_DIALOG, null);
        boolean isCustomDialog = sharedPreferences.getBoolean(SHUSECUSTOMDIALOG_FLAG, false);
        if (null == msgId)
            return;
        if (msgId.isEmpty())
            return;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            notificationManager.cancel(Integer.parseInt(msgId));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        PushNotificationDB dbObject = PushNotificationDB.getInstance(context);
        dbObject.open();
        PushNotificationData pushData = new PushNotificationData();
        dbObject.getPushNotificationData(msgId, pushData);
        dbObject.close();
        if (null == pushData) {
            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putString(PENDING_DIALOG, null);
            e.commit();
            return;
        }
        if (isCustomDialog) {
            if (null == mISHObserverObject) {
                Log.e(Util.TAG,SUBTAG+ "mISHObserverObject cannot be null if implementing pending dialog");
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putString(PENDING_DIALOG, null);
                e.commit();
            } else {
                PushDataForApplication pushDataForApplication = new PushDataForApplication();
                pushDataForApplication.convertPushDataToPushDataForApp(pushData, pushDataForApplication);
                mISHObserverObject.onReceivePushData(pushDataForApplication);
            }
            // return if user has opted for custom dialogs.
            return;
        }
        int code = Integer.parseInt(pushData.getCode());
        if (code == CODE_OPEN_URL) {
            Float p = -1.0f;
            int o = -1;
            int s = -1;
            try {
                p = Float.parseFloat(pushData.getPortion());
            } catch (NumberFormatException e) {
            } catch (NullPointerException e) {
            }
            try {
                o = Integer.parseInt(pushData.getOrientation());
            } catch (NumberFormatException e) {
            } catch (NullPointerException e) {
            }
            try {
                s = Integer.parseInt(pushData.getSpeed());
            } catch (NumberFormatException e) {
            } catch (NullPointerException e) {
            }

            if (Boolean.parseBoolean(pushData.getNoDialog())) {
                pushData.setSpeed("0");
            } else if (!((p > 0 && p < 1) || (o > 0 && o < 4) || (s > 0))) {
                String data = pushData.getData();
                if (null != data) {
                    if (!data.startsWith("http://") && !data.startsWith("https://")) {
                        data = "http://" + data;
                    }
                    Intent nativeBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                    nativeBrowserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    dbObject.open();
                    dbObject.deleteEntry(msgId);
                    dbObject.close();
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString(PENDING_DIALOG, null);
                    e.commit();
                    try {
                        NotificationBase.sendResultBroadcast(context, msgId, STREETHAWK_ACCEPTED);
                        context.startActivity(nativeBrowserIntent);
                    } catch (Exception ex) {
                        NotificationBase.sendResultBroadcast(context, msgId, STREETHAWK_DECLINED);
                        ex.printStackTrace();
                    }
                }
                return;
            }
        }
        hideSoftKeyboard();
        SHForegroundNotification alert = SHForegroundNotification.getDialogInstance(context);
        alert.display(pushData);
    }

    /**
     * Send push result if you are not extending streetHawk's onclicklistener
     * @param msgID msgID of the push message
     * @param pushResult 1 = accepted | 0 = postponed | -1=decline
     */
    public void sendPushResult(String msgID,int pushResult){
        NotificationBase.sendResultBroadcast(mContext, msgID,pushResult);
    }


    /**
     * Application need not use this call. Notify observers when app is foregrounded
     * @param activity
     */
    public void notifyAppForegrounded(Activity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!isPushRegistered()){
                    register();
                }
            }
        }).start();
        displayPendingDialog(activity.getApplicationContext());
    }

    /**
     * Application need not use this call. Notify observers when app is paused
     * @param activity
     */
    public void onPause(Activity activity){
        SHForegroundNotification alert = SHForegroundNotification.getDialogInstance(activity.getApplicationContext());
        alert.dismissForegroundDialog();
    }

    /**
     * Application need not use this call. Notify observers when app is backgrounded
     * @param activity
     */
    public void notifyAppBackgrounded(Activity activity) {
        SHForegroundNotification alert = SHForegroundNotification.getDialogInstance(activity.getApplicationContext());
        alert.dismissForegroundDialog();
    }
    /**
     * Application need not use this call. Notify observers when orientation is changed
     * @param activity
     */
    public void notifyChangeOrientation(Activity activity) {
        final Context context = activity.getApplicationContext();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayPendingDialog(context);
            }
        });
    }
    private void hideSoftKeyboard() {
        Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
        if (activity == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow((IBinder) activity.getWindow().getDecorView().getWindowToken(), 0);
    }
    /**
     * shGetAlertSettings returns the time remaining in minutes before pause minutes set in {@link #shAlertSetting(int)} expires
     *
     * @return time remaining before alerts will be enabled again
     */
    public int shGetAlertSettings() {
        if (null == mContext) {
            Log.e(Util.TAG, SUBTAG + "Streethawk is not initialized properly in your app.Possible reason is calling this function before Streethawk.init()");
            return 0;
        } else {
            SharedPreferences sharedPreferences = mContext.getApplicationContext().getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            long currentMins = (System.currentTimeMillis() / 60000);
            long savedMins = sharedPreferences.getLong(SHSAVEDTIME, -1);
            long pauseMins = sharedPreferences.getInt(SHPAUSETIME, -1);
            long remainingMins = (pauseMins - (currentMins - savedMins));
            if (0 >= remainingMins) {
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putLong(SHSAVEDTIME, 0);
                e.putInt(SHPAUSETIME, 0);
                e.apply();
                return 0;
            } else {
                return (int) remainingMins;
            }
        }
    }

    /**
     * call shAlertSetting() for pausing push messages for given duration of time in minutes.
     *
     * @param pauseMinutes minutes to be paused
     */
    public void shAlertSetting(final int pauseMinutes) {
        if (null == mContext) {
            Log.e(Util.TAG, SUBTAG + "Streethawk is not initialized properly in your app");
            return;
        }
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AlertSettings.getInstance(mContext).setAlertSettings(pauseMinutes);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Use this API only for Cross platform apps
     * returns name of the page if any requested by streethawk server when app is in BG
     * Use this API to check for page to be displayed. Returns null is streethawk has not requested any page
     *
     * @return app page to be displayed else returns null
     */
    public String getAppPage() {
        SharedPreferences frnd = mContext.getSharedPreferences(Util.SHSHARED_PREF_FRNDLST, Context.MODE_PRIVATE);
        String url = frnd.getString(PHONEGAP_URL, null);
        SharedPreferences.Editor e = frnd.edit();
        e.remove(PHONEGAP_URL);
        e.commit();
        return url;
    }

    /**
     * API returns icon identifer. To be used along with API setInteractivePushBtnPairs
     * @param iconName
     * @return
     */
    public int getIcon(String iconName){
        String packageName = mContext.getPackageName();
        return (mContext.getResources().getIdentifier(iconName, "drawable", packageName));
    }

    /**
     * @deprecated
     * API to return button pair from the given title
     */
    public void getButtonPairFromId(final String pairTitle,final InteractivePush obj){
        InteractivePushDB btnPairdb = InteractivePushDB.getInstance(mContext);
        btnPairdb.getBtnPairData(pairTitle,obj);
    }

    /**
     * Set application specific button pairs for interactive push
     * @param appPairs
     */
    public void setInteractivePushBtnPairs( final ArrayList<InteractivePush> appPairs){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(null==appPairs){
                    Log.e(Util.TAG,"app pairs is null in setInteractivePushBtnPairs. returning..");
                    return;
                }
                InteractivePushDB btnPairdb = InteractivePushDB.getInstance(mContext);
                btnPairdb.open();
                btnPairdb.storeBtnPairsFromList(appPairs);
                btnPairdb.close();
            }
        }).start();
    }
}