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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.streethawk.library.core.PluginBase;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Push extends PluginBase {
    private static Context mContext;
    private static String mSenderID;
    private static Push mPush;
    private final String PROPERTY_REG_ID = "registration_id";
    private final String PREFS_KEY = "gcm_util";
    private final String PROPERTY_APP_VERSION = "app_version";

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
    private boolean activityLifecycleRegistered = false;

    private Push() {
    }

    public static Push getInstance(Context context) {
        if (null == mPush)
            mPush = new Push();
        mContext = context;
        return mPush;
    }

    private SharedPreferences getGcmPreferences() {
        return mContext.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
    }

    /**
     * Gets the current registration ID for application on GCM service, if there
     * is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId() {
        final SharedPreferences prefs = getGcmPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (TextUtils.isEmpty(registrationId)) {
            Log.i(Util.TAG, SUBTAG + "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = Util.getAppVersion(mContext);
        if (registeredVersion != currentVersion) {
            Log.i(Util.TAG, SUBTAG + "App version changed.");
            return "";
        }
        return registrationId;
    }


    private boolean isRegistered() {
        return getRegistrationId().length() > 0;
    }

    private static void saveSenderId(String senderId) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(SHGCM_SENDER_KEY_APP, senderId);
        e.commit();
    }

    private static String getGcmSenderId() {
        String senderId = null;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        senderId = sharedPreferences.getString(SHGCM_SENDER_KEY_APP, null);
        if (null != senderId) {
            if (!senderId.isEmpty()) {
                return senderId;

            } else {
                Log.w(Util.TAG, SUBTAG + "Application is missing GCM senderKey. Please Refer Streethawk docs for details");
                return null;
            }
        }
        Log.e(Util.TAG, SUBTAG + "Application is missing GCM senderKey. Please Refer Streethawk docs for details");
        return null;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    private void setRegistrationId(String regId) {
        final SharedPreferences prefs = getGcmPreferences();
        int appVersion = Util.getAppVersion(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private boolean getGCMRegistered() {
        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return prefs.getBoolean(Util.SHGCMREGISTERED, false);
    }

    private void updateInstallWithGcmIdIfNeeded() {
        checkDevice();
        checkManifest();
        if (getGCMRegistered()) {
            return;
        }
        String registrationId = getRegistrationId();
        if (null == registrationId) {
            SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            SharedPreferences.Editor e = prefs.edit();
            e.putBoolean(Util.SHGCMREGISTERED, false);
            e.commit();
            return;
        }
        if (registrationId.isEmpty()) {
            SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            SharedPreferences.Editor e = prefs.edit();
            e.putBoolean(Util.SHGCMREGISTERED, false);
            e.commit();
            return;
        }
        Util.updateAccessDaa(mContext,registrationId);
    }

    /**
     * Checks if the device has the proper dependencies installed.
     * <p/>
     * This method should be called when the application starts to verify that
     * the device supports GCM.
     *
     * @throws UnsupportedOperationException if the device does not support GCM.
     */
    private void checkDevice() {
        int version = Build.VERSION.SDK_INT;
        if (version < 8) {
            throw new UnsupportedOperationException("Device must be at least " + "API Level 8 (instead of " + version + ")");
        }
        PackageManager packageManager = mContext.getPackageManager();
        try {
            packageManager.getPackageInfo(GSF_PACKAGE, 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new UnsupportedOperationException("Device does not have package " + GSF_PACKAGE);
        }
    }

    private void checkReceiver(Context context, Set<String> allowedReceivers, String action) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        Intent intent = new Intent(action);
        intent.setPackage(packageName);
        List<ResolveInfo> receivers = pm.queryBroadcastReceivers(intent, PackageManager.GET_INTENT_FILTERS);
        if (receivers.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "No receivers for action " + action);
        }
        Log.v(Util.TAG, SUBTAG + "Found " + receivers.size() + " receivers for action " + action);
        for (ResolveInfo receiver : receivers) {
            String name = receiver.activityInfo.name;
            if (!allowedReceivers.contains(name)) {
                //throw new IllegalStateException("Receiver " + name + " is not set with permission " + PERMISSION_GCM_INTENTS);
                Log.e(Util.TAG, SUBTAG + "Receiver " + name + " is not set with permission " + PERMISSION_GCM_INTENTS);
            }
        }
    }

    private void checkManifest() {
        PackageManager packageManager = mContext.getPackageManager();
        String packageName = mContext.getPackageName();
        // check receivers
        PackageInfo receiversInfo = null;
        try {
            receiversInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(Util.TAG, SUBTAG + "Could not get receivers for package " + packageName);
        }
        ActivityInfo[] receivers = receiversInfo.receivers;
        if (receivers == null || receivers.length == 0) {
            Log.w(Util.TAG, SUBTAG + "Could not get receivers for package " + packageName);
        }
        HashSet<String> allowedReceivers = new HashSet<String>();
        for (ActivityInfo receiver : receivers) {
            if (PERMISSION_GCM_INTENTS.equals(receiver.permission)) {
                allowedReceivers.add(receiver.name);
            }
        }
        if (allowedReceivers.isEmpty()) {
            Log.w(Util.TAG, SUBTAG + "No receiver allowed to receive " + PERMISSION_GCM_INTENTS);
        }
        checkReceiver(mContext, allowedReceivers, INTENT_FROM_GCM_MESSAGE);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setActivityLifecycleCallbacks(Application application) {
        if (!activityLifecycleRegistered) {
            application.registerActivityLifecycleCallbacks(PushActivityLifecycleCallback.getInstance());
            activityLifecycleRegistered = true;
        }
    }

    public boolean registerForPushMessaging(Application app,String project_number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setActivityLifecycleCallbacks(app);
        }
        return reRegister(project_number);
    }

    public boolean reRegister(String project_number){
        if(null==project_number){
            mSenderID=getGcmSenderId();
        }else {
            mSenderID = project_number;
        }
        if(null==mSenderID){
            Log.e(Util.TAG,SUBTAG+" null project_number/ sedner id in registerForPushMessaging");
        }
        setAppPageReceiver(mISHObserverObject);
        if (isRegistered()) {
            updateInstallWithGcmIdIfNeeded();
            return true;
        } else {
            Log.i(Util.TAG, SUBTAG + "Install not registered with StreetHawk");

            SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            final SharedPreferences.Editor e = prefs.edit();
            e.putBoolean(Util.SHGCMREGISTERED, false);
            e.commit();
            return false;
        }
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
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                String title = Util.getAppName(mContext);
                String message = GooglePlayServicesUtil.getErrorString(resultCode);
                PendingIntent intent = GooglePlayServicesUtil.getErrorPendingIntent(resultCode, mContext, PLAY_SERVICES_RESOLUTION_REQUEST);
                sendNotification(mContext, title, message, "", intent);
            } else {
                Log.i(Util.TAG, SUBTAG + "This device is not supported.");
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
        if(null==mSenderID){
            Log.e(Util.TAG,SUBTAG+"Sender id is null, returning");
        }
        saveSenderId(mSenderID);
        new Thread(new Runnable() {
            @Override
            public void run() {
                long backoff = BACKOFF_MILLI_SECONDS + new Random().nextInt(1000);
                for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                    try {
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
                        String senderId = getGcmSenderId();
                        String registrationId=null;
                        if(null!=senderId) {
                            registrationId = gcm.register(senderId);
                        }else{
                            Log.e(Util.TAG,SUBTAG+"snderid is null returning..");
                            return;
                        }
                        setRegistrationId(registrationId);
                        updateInstallWithGcmIdIfNeeded();
                    } catch (IOException ex) {
                        if (i == MAX_ATTEMPTS) {
                            break;
                        }
                        try {
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            Thread.currentThread().interrupt();
                        }
                        backoff *= 2;
                    }
                }
            }
        }).start();
    }

    public boolean isUsePush() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        if (sharedPreferences != null)
            return sharedPreferences.getBoolean(Constants.SHGCM_FLAG, true);
        else
            return true;
    }

    public void setUseCustomDialog(boolean answer){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(Constants.SHUSECUSTOMDIALOG_FLAG, answer);
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
        if (null != mContext) {
            SHForegroundNotification instance = SHForegroundNotification.getDialogInstance(mContext, null);
            instance.setAppPageReceiver(object);
            PushNotificationBroadcastReceiver.updateAppGcmReceiverList(object);

        }
    }

    public void registerSHObserver(ISHObserver object) {
        mISHObserverObject = object;
    }


    private void displayPendingDialog(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String msgId = sharedPreferences.getString(Constants.PENDING_DIALOG, null);
        boolean isCustomDialog = sharedPreferences.getBoolean(Constants.SHUSECUSTOMDIALOG_FLAG, false);
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
            e.putString(Constants.PENDING_DIALOG, null);
            e.commit();
            return;
        }

        if (isCustomDialog) {
            if (null == mISHObserverObject) {
                Log.e(Util.TAG,SUBTAG+ "mISHObserverObject cannot be null if implementing pending dialog");
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putString(Constants.PENDING_DIALOG, null);
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
        if (code == NotificationBase.CODE_OPEN_URL) {
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
                    e.putString(Constants.PENDING_DIALOG, null);
                    e.commit();
                    try {
                        NotificationBase.sendResultBroadcast(context, msgId, Constants.STREETHAWK_ACCEPTED);
                        context.startActivity(nativeBrowserIntent);
                    } catch (Exception ex) {
                        NotificationBase.sendResultBroadcast(context, msgId, Constants.STREETHAWK_DECLINED);
                        ex.printStackTrace();
                    }
                }
                return;
            }
        }
        hideSoftKeyboard();
        SHForegroundNotification alert = SHForegroundNotification.getDialogInstance(context, pushData);
        alert.display(pushData);
    }

    public void notifyAppForegrounded(Activity activity) {
        final Context context = activity.getApplicationContext();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayPendingDialog(context);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!isPushRegistered()){
                    register();
                }
            }
        }).start();
    }

    public void notifyAppBackgrounded(Activity activity) {
        SHForegroundNotification alert = SHForegroundNotification.getDialogInstance(null, null);
        alert.dismissForegroundDialog();
    }

    public void notifyChangeOrientation(Activity activity) {
        final Context context = activity.getApplicationContext();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayPendingDialog(context);
            }
        });
    }

    /**
     * Call addPushModule() to add growth modules in installs which have already been released with StreetHawk core module.
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
            }
            return;
        }
    }

    private boolean isPushRegistered(){
        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return prefs.getBoolean(Util.SHGCMREGISTERED, false);
    }

    private void hideSoftKeyboard() {
        Activity activity = StreetHawk.INSTANCE.getCurrentActivity();
        if (activity == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow((IBinder) activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void notifyInstallRegistered(Context context) {
        if(!isPushRegistered()){
            register();
        }
    }
}