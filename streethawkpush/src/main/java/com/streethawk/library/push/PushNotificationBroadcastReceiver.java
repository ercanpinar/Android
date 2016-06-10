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
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.streethawk.library.core.Logging;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PushNotificationBroadcastReceiver extends BroadcastReceiver implements Constants{

    private final String SUBTAG = "PushNotificationBroadcastReceiver";
    public PushNotificationBroadcastReceiver() {}
    private final String PROJECT_NUMBER = "project_number";  // project number as in GCM
    private final String SUBMIT_BTN_PAIR = "submit_interactive_button";  // interactive push
    private final String PUSH = "push";   //Smart push

    /**
     * Function displays badges to app icons.
     * Note that not all devices support badges and hence function is ignored for non supporting devices
     *
     * @param BadgeCount
     */
    public static void displayBadge(Context context, int BadgeCount) {
        String deviceManufacturer = Build.MANUFACTURER;
        String packgaeName = context.getPackageName().toString();
        ComponentName componentName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent();
        String launcherActivityName = componentName.getClassName();

        if (deviceManufacturer.equalsIgnoreCase("GENYMOTION")) {
            String modelStr = "(" + Build.MANUFACTURER + ") " + Build.BRAND + " " + Build.MODEL;
            if (modelStr.toLowerCase().contains("samsung"))
                deviceManufacturer = "SAMSUNG";
            else if (modelStr.toLowerCase().contains("sony"))
                deviceManufacturer = "SONY";
            else if (modelStr.toLowerCase().contains("htc"))
                deviceManufacturer = "HTC";
            else if (modelStr.toLowerCase().contains("lge"))
                deviceManufacturer = "lge";
            else
                deviceManufacturer = "UNKNOWN";
        }
        if (deviceManufacturer.equalsIgnoreCase("SONY")) {
            Intent badgeIntent = new Intent();
            try {
                badgeIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
                badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherActivityName);
                badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
                badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", Integer.toString(BadgeCount));
                badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", packgaeName);
                context.sendBroadcast(badgeIntent);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else if (deviceManufacturer.equalsIgnoreCase("SAMSUNG")) {
            try {
                ContentResolver samsungCR = context.getContentResolver();
                Uri badgeURI = Uri.parse("content://com.sec.badge/apps");
                ContentValues samsungCV = new ContentValues();
                samsungCV.put("package", packgaeName);
                samsungCV.put("class", launcherActivityName);
                samsungCV.put("badgecount", Integer.valueOf(BadgeCount));
                String str = "package=? AND class=?";
                String[] arrayOfString = new String[2];
                arrayOfString[0] = packgaeName;
                arrayOfString[1] = launcherActivityName;
                int update = samsungCR.update(badgeURI, samsungCV, str, arrayOfString);
                if (update == 0) {
                    samsungCR.insert(badgeURI, samsungCV);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else if (deviceManufacturer.equalsIgnoreCase("HTC")) {
            try {
                Intent htcIntent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
                htcIntent.putExtra("packagename", packgaeName);
                htcIntent.putExtra("count", BadgeCount);
                context.sendBroadcast(htcIntent);
                Intent notificationIntent = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
                ComponentName htcComponentName = new ComponentName(context, StreetHawk.INSTANCE.getCurrentActivity().toString());
                notificationIntent.putExtra("com.htc.launcher.extra.COMPONENT", htcComponentName.flattenToShortString());
                notificationIntent.putExtra("com.htc.launcher.extra.COUNT", 99);
                context.sendBroadcast(notificationIntent);
            } catch (Exception e) {

                e.printStackTrace();

                return;
            }
        } else if (deviceManufacturer.equalsIgnoreCase("LGE")) {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", BadgeCount);
            intent.putExtra("badge_count_package_name", packgaeName);
            intent.putExtra("badge_count_class_name", launcherActivityName);
            context.sendBroadcast(intent);
        } else {

            Log.i(Util.TAG, "Badges are not supported for " + Build.MANUFACTURER + " " + Build.MODEL);

            return;
        }
    }

    /**
     * Function returns true is permission is available in manifest
     * @param context
     * @param code
     * @return
     */
    public static boolean isPermissionAvailable(Context context, int code) {
        switch (code) {
            case CODE_IBEACON:
                if ((Util.isPermissionAvailable(context, android.Manifest.permission.BLUETOOTH) == -1) &&
                        (Util.isPermissionAvailable(context, android.Manifest.permission.BLUETOOTH) == -1)) {
                    Log.w(Util.TAG, "App is missing Bluetooth permissions in AndroidManifest.xml");
                    return false;
                }
                break;
            case CODE_CALL_TELEPHONE_NUMBER:
                if ((Util.isPermissionAvailable(context, android.Manifest.permission.CALL_PHONE) == -1)) {
                    Log.w(Util.TAG, "Please add CALL_PHONE permission in AndroidManifest.xml");
                    return false;
                }
                break;
            default:
                return true;
        }
        return true;
    }


    /**
     * API returns icon identifer. To be used along with API setInteractivePushBtnPairs
     * @param context
     * @param iconName
     * @return
     */
    private int getIcon(Context context,String iconName){
        String packageName = context.getPackageName();
        return (context.getResources().getIdentifier(iconName, "drawable", packageName));
    }


    /**
     * Set application specific button pairs for interactive push
     * @param context
     */
    public void setInteractivePushBtnPairs( final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String SHAPPVERSION = "shappversion";
                SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                String storedVersion = sharedPreferences.getString(SHAPPVERSION, null);
                String currentAppVersion = Util.getAppVersionName(context);
                final ArrayList<InteractivePush> predefinedPairs = new ArrayList<InteractivePush>();
                predefinedPairs.add(new InteractivePush("Yes", getIcon(context, "shaccept"), "No", getIcon(context, "shcancel"), "YesNo"));
                predefinedPairs.add(new InteractivePush("Accept", getIcon(context, "shaccept"), "Decline", getIcon(context, "shcancel"), "AcceptDecline"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Download", getIcon(context, "shcloud_download"), "ShareDownload"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Remind Me Later", getIcon(context, "shalarm"), "ShareRemind Me Later"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Opt-in", getIcon(context, "shaccept"), "ShareOpt-in"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Opt-out", getIcon(context, "shcancel"), "ShareOpt-out"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Follow", getIcon(context, "shperson"), "ShareFollow"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Unfollow", getIcon(context, "shcancel"), "ShareUnfollow"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Shop Now", getIcon(context, "shshoppingcart"), "ShareShop Now"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Buy Now", getIcon(context, "shshoppingcart"), "ShareBuy Now"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Like", getIcon(context, "shlike"), "ShareLike"));
                predefinedPairs.add(new InteractivePush("Share", getIcon(context, "shshare"), "Dislike", getIcon(context, "shdislike"), "ShareDislike"));
                predefinedPairs.add(new InteractivePush("Like", getIcon(context, "shlike"), "Dislike", getIcon(context, "shdislike"), "LikeDislike"));
                predefinedPairs.add(new InteractivePush("\uD83D\uDE00", -1, "\uD83D\uDE1E", -1, "shpre_happysad"));  //HappySad
                predefinedPairs.add(new InteractivePush("\ud83d\uDC4D", -1, "\uD83D\uDC4E", -1, "shpre_tutd"));  //thumps up thumps down
                InteractivePushDB btnPairdb = InteractivePushDB.getInstance(context);
                btnPairdb.open();
                btnPairdb.storeBtnPairsFromList(predefinedPairs);
                btnPairdb.close();
            }
        }).start();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent.getAction() == Util.BROADCAST_MSG_FROM_CORE){
            //Check for msg from core
            String objAsJson = intent.getStringExtra(Util.MSG_FROM_CORE);
            if(null!=objAsJson){
                try{
                    JSONObject obj = new JSONObject(objAsJson);
                    String version =(String)obj.get(Util.KEY_UPDATE_VERSION);
                    if(null!=version){
                        setInteractivePushBtnPairs(context);
                    }
                }catch(JSONException w){
                    w.printStackTrace();
                }
            }
        }
        // Check for appStatus
        if (intent.getAction() == Util.BROADCAST_SH_APP_STATUS_NOTIFICATION) {
            String installId = intent.getStringExtra(Util.INSTALL_ID);
            if(null==installId)
                return;
            if (installId.equals(Util.getInstallId(context))) {
                String answer = intent.getStringExtra(Util.APP_STATUS_ANSWER);
                try {
                    JSONObject object = new JSONObject(answer);
                    if (object.has(Util.APP_STATUS)) {
                        if (object.get(Util.APP_STATUS) instanceof JSONObject) {
                            JSONObject app_status = object.getJSONObject(Util.APP_STATUS);
                            if (app_status.has(PROJECT_NUMBER) && !app_status.isNull(PROJECT_NUMBER)) {
                                final Object value_project_number = app_status.get(PROJECT_NUMBER);
                                if (value_project_number instanceof String) {
                                    final String newSenderID = (String)value_project_number;
                                    if(value_project_number==null)
                                        return;
                                    if(newSenderID.isEmpty())
                                        return;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                                            String stored_sender_key = sharedPreferences.getString(SHGCM_SENDER_KEY_APP, null);
                                            if(null==stored_sender_key){
                                                SharedPreferences.Editor e = sharedPreferences.edit();
                                                e.putString(SHGCM_SENDER_KEY_APP, newSenderID);
                                                InstanceID instanceID = InstanceID.getInstance(context);
                                                String token=null;
                                                try {
                                                    token = instanceID.getToken(newSenderID,
                                                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                                                    if(null!=token){
                                                        e.putString(PUSH_ACCESS_DATA, token);
                                                        Push.getInstance(context).addPushModule();
                                                    }
                                                } catch (IOException ex) {
                                                    ex.printStackTrace();
                                                    return;
                                                }
                                                e.commit();
                                            }else{
                                                if(!(stored_sender_key.equals(value_project_number))){
                                                    SharedPreferences.Editor e = sharedPreferences.edit();
                                                    e.putString(SHGCM_SENDER_KEY_APP, newSenderID);
                                                    InstanceID instanceID = InstanceID.getInstance(context);
                                                    String token=null;
                                                    try {
                                                        token = instanceID.getToken(newSenderID,
                                                                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                                                        if(null!=token){
                                                            e.putString(PUSH_ACCESS_DATA, token);
                                                            Push.getInstance(context).addPushModule();
                                                        }
                                                    } catch (IOException ex) {
                                                        ex.printStackTrace();
                                                        return;
                                                    }
                                                    e.commit();
                                                    Push.getInstance(context).addPushModule();
                                                }
                                            }
                                        }
                                    }).start();
                                }
                            }
                            if (object.has(PUSH)) {
                                String error = Integer.toString(-1);
                                String code = error;
                                String messageId = error;
                                String orientation = error;
                                String speed = error;
                                String portion = error;
                                String noConfirm = null;
                                String installid = null;
                                String data = null;
                                String aps = null;
                                String titleLength = null;
                                if (object.get(PUSH) instanceof JSONObject) {
                                    JSONObject push = object.getJSONObject(PUSH);
                                    if (push.has(PUSH_CODE) && !push.isNull(PUSH_CODE)) {
                                        code = push.get(PUSH_CODE).toString();
                                    }
                                    if (push.has(PUSH_MSG_ID) && !push.isNull(PUSH_MSG_ID)) {
                                        messageId = push.get(PUSH_MSG_ID).toString();
                                    }
                                    if (push.has(PUSH_DATA) && !push.isNull(PUSH_DATA)) {
                                        data = push.get(PUSH_DATA).toString();
                                    }
                                    if (push.has(PUSH_TITLE_LENGTH) && !push.isNull(PUSH_TITLE_LENGTH)) {
                                        titleLength = push.get(PUSH_TITLE_LENGTH).toString();
                                    }
                                    if (push.has(PUSH_SHOW_DIALOG) && !push.isNull(PUSH_SHOW_DIALOG)) {
                                        noConfirm = push.get(PUSH_SHOW_DIALOG).toString();
                                    }
                                    if (push.has(PUSH_ORIENTATION) && !push.isNull(PUSH_ORIENTATION)) {
                                        orientation = push.get(PUSH_ORIENTATION).toString();
                                    }
                                    if (push.has(PUSH_PORTION) && !push.isNull(PUSH_PORTION)) {
                                        portion = push.get(PUSH_PORTION).toString();
                                    }
                                    if (push.has(PUSH_SPEED) && !push.isNull(PUSH_SPEED)) {
                                        speed = push.get(PUSH_SPEED).toString();
                                    }
                                    if (push.has(PUSH_INSTALLID) && !push.isNull(PUSH_INSTALLID)) {
                                        installid = push.get(PUSH_INSTALLID).toString();
                                    }
                                    if (push.has(PUSH_APS) && !push.isNull(PUSH_APS)) {
                                        aps = push.get(PUSH_APS).toString();
                                    }

                                    final Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction("com.google.android.c2dm.intent.RECEIVE");
                                    broadcastIntent.putExtra(PUSH_CODE, code);
                                    broadcastIntent.putExtra(PUSH_MSG_ID, messageId);
                                    broadcastIntent.putExtra(PUSH_APS, aps);
                                    broadcastIntent.putExtra(PUSH_DATA, data);
                                    broadcastIntent.putExtra(PUSH_ORIENTATION, orientation);
                                    broadcastIntent.putExtra(PUSH_PORTION, portion);
                                    broadcastIntent.putExtra(PUSH_SPEED, speed);
                                    broadcastIntent.putExtra(PUSH_SHOW_DIALOG, noConfirm);
                                    broadcastIntent.putExtra(PUSH_TITLE_LENGTH, titleLength);
                                    if (null == installid || installid.isEmpty())
                                        installid = Util.getInstallId(context);
                                    broadcastIntent.putExtra(PUSH_INSTALLID, installid);
                                    context.sendBroadcast(broadcastIntent);
                                }
                            }
                            if (app_status.has(SUBMIT_BTN_PAIR) && !app_status.isNull(SUBMIT_BTN_PAIR)) {
                                final Object value_submit_btn_par = app_status.get(SUBMIT_BTN_PAIR);
                                if (value_submit_btn_par instanceof Boolean) {
                                    final boolean isSubmitBtnPair = (Boolean)value_submit_btn_par;
                                    if(isSubmitBtnPair){
                                        InteractivePushDB.getInstance(context).submitButtonPairsToServer();
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        // Receive actual push notification from server
        if (intent.getAction().equals(BROADCAST_SH_PUSH_NOTIFICATION)) {
            final Bundle extras = intent.getExtras();
            boolean forceToBg = false;
            String msgID = extras.getString(Util.MSGID);
            if (null == msgID) {
                return;
            } else {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                boolean isCustomDialog = sharedPreferences.getBoolean(SHUSECUSTOMDIALOG_FLAG, false);
                if (sharedPreferences.getString(PENDING_DIALOG, null) != null) {
                    forceToBg = true;
                }
                if (sharedPreferences.getBoolean(SHUSECUSTOMDIALOG_FLAG, false)) {
                    forceToBg = false;
                }
                PushNotificationDB database = PushNotificationDB.getInstance(context);
                database.open();
                PushNotificationData pushData = new PushNotificationData();
                database.getPushNotificationData(msgID, pushData);
                database.close();
                if (null == pushData) {
                    clearPendingDialogFlagAndDB(context, msgID);
                    return;
                }

                ISHObserver instance = SHGcmListenerService.getISHObserver();
                if(null!=instance){
                    PushDataForApplication obj  = new PushDataForApplication();
                    obj.convertPushDataToPushDataForApp(pushData,obj);
                    instance.onReceivePushData(obj);
                }

                // Display badge
                int badgeCnt = 0;
                try {
                    badgeCnt = Integer.parseInt(pushData.getBadge());
                } catch (NumberFormatException e) {
                    badgeCnt = 0;
                }
                displayBadge(context, badgeCnt);
                int code = 0;
                try {
                    code = Integer.parseInt(pushData.getCode());
                } catch (NumberFormatException e) {
                    return;
                }
                if (code == CODE_GHOST_PUSH){
                    sendAcknowledgement(context, msgID);
                    return;
                }
                if (code == CODE_REQUEST_THE_APP_STATUS){
                    Logging.getLoggingInstance(context).checkAppState();
                    return;
                }
                if (!Push.getInstance(context).isUsePush()) {
                    Log.i(Util.TAG, "GCM is disabled in code.Developer has called shSetGcmSupport(false)");
                    return;
                }
                boolean enable_push = sharedPreferences.getBoolean(SHGCM_FLAG, true);
                if (enable_push) {
                    String title = pushData.getTitle();
                    String msg = pushData.getMsg();
                    String data = pushData.getData();
                    sendAcknowledgement(context, msgID);
                    if (code == CODE_CUSTOM_JSON_FROM_SERVER) {
                        if (null == instance) {
                            Log.e(Util.TAG, "No object registered for class implementing ISHObserver. Use registerSHObserver");
                            NotificationBase.sendResultBroadcast(context, msgID, STREETHAWK_DECLINED);
                            return;
                        } else {
                            NotificationBase.sendResultBroadcast(context, msgID, STREETHAWK_ACCEPTED);
                            handleCustomJsonFromServer(title, msg, data);
                        }
                    } else if (code == CODE_REQUEST_THE_APP_STATUS) {
                        Logging.getLoggingInstance(context).checkAppState();
                    } else {
                        // return if permission is missing
                        if (!(isPermissionAvailable(context, code))) {
                            clearPendingDialogFlagAndDB(context, msgID);
                            return;
                        }
                        // Storing msgid for pending dialog
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        e.putString(PENDING_DIALOG, pushData.getMsgId());
                        e.commit();
                        if (forceToBg || checkIfBG(context)) {
                            // storing package name to distinguish between broadcast received
                            extras.putString(SHPACKAGENAME, context.getPackageName());
                            switch (code) {
                                case CODE_IBEACON:
                                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    if (bluetoothAdapter != null) {
                                        boolean isEnabled = bluetoothAdapter.isEnabled();
                                        if (!isEnabled) {
                                            SHBackgroundNotification notificationBeacon = new SHBackgroundNotification(context, pushData);
                                            notificationBeacon.display();
                                        } else {
                                            clearPendingDialogFlagAndDB(context, msgID);
                                            NotificationBase.sendResultBroadcast(context, msgID, STREETHAWK_ACCEPTED);
                                        }
                                    }
                                    break;
                                case CODE_ENABLE_LOCATION:
                                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                                    if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                                        SHBackgroundNotification notificationBeacon = new SHBackgroundNotification(context, pushData);
                                        notificationBeacon.display();
                                    } else {
                                        clearPendingDialogFlagAndDB(context, msgID);
                                        NotificationBase.sendResultBroadcast(context, msgID, STREETHAWK_ACCEPTED);
                                    }
                                    break;
                                default:
                                    SHBackgroundNotification notification = new SHBackgroundNotification(context, pushData);
                                    notification.display();
                                    break;
                            }
                        } else {
                            switch (code) {
                                case CODE_IBEACON:
                                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    if (bluetoothAdapter != null) {
                                        boolean isEnabled = bluetoothAdapter.isEnabled();
                                        if (!isEnabled) {
                                            SHForegroundNotification alert = SHForegroundNotification.getDialogInstance(context);
                                            alert.display(pushData);
                                        } else {
                                            clearPendingDialogFlagAndDB(context, msgID);
                                            NotificationBase.sendResultBroadcast(context, msgID, STREETHAWK_ACCEPTED);
                                        }
                                    }
                                    break;
                                case CODE_ENABLE_LOCATION:
                                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                                    if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                                        SHForegroundNotification alert = SHForegroundNotification.getDialogInstance(context);
                                        alert.display(pushData);
                                    } else {
                                        //ignoring message and hence clearing
                                        clearPendingDialogFlagAndDB(context, msgID);
                                        NotificationBase.sendResultBroadcast(context, msgID, STREETHAWK_ACCEPTED);
                                    }
                                    break;
                                default:
                                    SHForegroundNotification alert = SHForegroundNotification.getDialogInstance(context);
                                    alert.display(pushData);
                                    break;
                            }
                        }
                    }
                }
                // Release the wake lock provided by the WakefulBroadcastReceiver.
                GCMReceiver.completeWakefulIntent(intent);
            }

        } else {

            int DISMISS_BADGE = 0;
            int code = 0;
            boolean sendResult = true;
            Bundle extras = intent.getExtras();
            String msgId = extras.getString(PENDING_DIALOG);
            String packageName = intent.getStringExtra(SHPACKAGENAME);
            if (!(context.getPackageName().equals(packageName)))
                return;
            if (msgId == null)
                return;
            boolean fromBG = extras.getBoolean(FROMBG, false);
            PushNotificationDB dbObject = PushNotificationDB.getInstance(context);
            dbObject.open();
            PushNotificationData dataObject = new PushNotificationData();
            boolean error = dbObject.getPushNotificationData(msgId, dataObject);
            dbObject.close();

            dbObject = null;
            if (null == dataObject) {
                return;
            }
            int result = -2;
            if (fromBG) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(Integer.parseInt(msgId));
            }
            try {
                code = Integer.parseInt(dataObject.getCode());
            } catch (NumberFormatException e) {
                code = 0;
            } catch (Exception e) {
                e.printStackTrace();
                code = 0;
            }
            if (intent.getAction().equals(BROADCAST_STREETHAWK_ACCEPTED)) {
                result = STREETHAWK_ACCEPTED;
                displayBadge(context, DISMISS_BADGE);
                if (fromBG) {
                    bgActionPositive(context, packageName, dataObject);
                    colapseNotification(context);
                    int tempCode = 0;
                    try {
                        tempCode = Integer.parseInt(dataObject.getCode());
                    } catch (NumberFormatException e) {
                        return;
                    }
                    if (tempCode == CODE_OPEN_URL || tempCode == CODE_SIMPLE_PROMPT)
                        sendResult = false;
                } else {
                    switch (code) {
                        case CODE_OPEN_URL:
                            float p = 0.0f;
                            int o = -1;
                            int s = -1;
                            try {
                                p = Float.parseFloat(dataObject.getPortion());
                            } catch (Exception e) {
                                p = 0.0f;
                            }
                            try {
                                o = Integer.parseInt(dataObject.getOrientation());
                            } catch (Exception e) {
                                o = -1;
                            }
                            try {
                                s = (int) Float.parseFloat(dataObject.getSpeed());
                            } catch (Exception e) {
                                s = -1;
                            }
                            if (!((p > 0 && p < 1) || (o > 0 && o < 4) || (s > 0))) {
                                clearPendingDialogFlagAndDB(context, msgId);
                                // clear if not in app slide

                            } else {
                                SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                                if (null == sharedPreferences.getString(PENDING_DIALOG, null)) {
                                    clearPendingDialogFlagAndDB(context, msgId);
                                } else {
                                    // this is to prevent sending of two push result in 8000 in app with confirmation

                                    return;
                                }
                            }
                            break;
                        case CODE_SIMPLE_PROMPT:
                            if (fromBG)
                                return;
                            else
                                clearPendingDialogFlagAndDB(context, msgId);
                            return;
                        case CODE_CUSTOM_ACTIONS:
                            return;
                        default:
                            clearPendingDialogFlagAndDB(context, msgId);
                    }
                }
            }
            if (intent.getAction().equals(BROADCAST_STREETHAWK_DECLINED)) {
                displayBadge(context, DISMISS_BADGE);
                clearPendingDialogFlagAndDB(context, msgId);
                result = STREETHAWK_DECLINED;
                sendResult = true;

            }
            if (intent.getAction().equals(BROADCAST_STREETHAWK_POSTPONED)) {
                displayBadge(context, DISMISS_BADGE);
                result = STREETHAWK_POSTPONED;
                clearPendingDialogFlagAndDB(context, msgId);
                sendResult = true;
            }
            // schedule sending of queued broadcast only if we have sent result of previous one.
            if (sendResult) {
                ISHObserver instance = SHGcmListenerService.getISHObserver();
                if (null == instance) {
                    Log.w(Util.TAG, "No object registered for class implementing ISHObserver. Use registerSHObserver");
                } else {
                    PushDataForApplication pushDataForApplication = new PushDataForApplication();
                    pushDataForApplication.convertPushDataToPushDataForApp(dataObject, pushDataForApplication);
                    instance.onReceiveResult(pushDataForApplication, result);
                }
                sendResultLog(context, msgId, result, code);
            }
            dataObject = null;
        }
    } //End of onReceive


    private Boolean isIncorrectPackage(Context context, String receivedPackageName) {
        if (null == receivedPackageName)
            return true;
        if (!(receivedPackageName.equals(context.getPackageName()))) {
            return true;
        }
        return false;

    }

    /**
     * Clear pending dialog clears db and pending dialog flag
     *
     * @param context
     * @param msgId
     */
    public void clearPendingDialogFlagAndDB(Context context, String msgId) {
        PushNotificationDB dbObject = PushNotificationDB.getInstance(context);
        dbObject.open();
        dbObject.deleteEntry(msgId);
        dbObject.close();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(PENDING_DIALOG, null);
        e.commit();
    }

    private void colapseNotification(Context context) {
        Intent colapseNotificationIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(colapseNotificationIntent);
    }

    private void bgActionPositive(Context context, String receivedPackageName, PushNotificationData pushObject) {
        int code = 0;
        try {
            code = Integer.parseInt(pushObject.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            code = 0;
        }
        String data = pushObject.getData();
        String msgId = pushObject.getMsgId();
        if (isIncorrectPackage(context, receivedPackageName)) {
            return;
        }
        switch (code) {
            case CODE_LAUNCH_ACTIVITY:
            case CODE_USER_REGISTRATION_SCREEN:
            case CODE_USER_LOGIN_SCREEN:
                clearPendingDialogFlagAndDB(context, msgId);
                if (data.isEmpty()) {
                    if (code == CODE_USER_REGISTRATION_SCREEN)
                        data = REGISTER_FRIENDLY_NAME;
                    if (code == CODE_USER_LOGIN_SCREEN)
                        data = LOGIN_FRIENDLY_NAME;
                }
                if (Util.getPlatformType() == PLATFORM_PHONEGAP ||
                        Util.getPlatformType() == PLATFORM_TITANIUM ||
                        Util.getPlatformType() == PLATFORM_UNITY) {
                    launchActivityPG(context, data);
                } else {
                    launchActivity(context, data);
                }
                break;
            case CODE_RATE_APP:
            case CODE_UPDATE_APP:
                clearPendingDialogFlagAndDB(context, msgId);
                handleRateUpdate(context);
                break;
            case CODE_CALL_TELEPHONE_NUMBER:
                clearPendingDialogFlagAndDB(context, msgId);
                handleCall(context, data);
                break;
            case CODE_IBEACON:
                clearPendingDialogFlagAndDB(context, msgId);
                final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter != null) {
                    boolean isEnabled = bluetoothAdapter.isEnabled();
                    if (!isEnabled) {
                        bluetoothAdapter.enable();
                        Toast.makeText(context, NotificationBase.getStringtoDisplay(context, NotificationBase.TYPE_BT_ENABLE_TOAST), Toast.LENGTH_LONG).show();
                    }

                }
                break;
            case CODE_OPEN_URL:
                startApp(context);
                break;
            case CODE_ENABLE_LOCATION:
                clearPendingDialogFlagAndDB(context, msgId);
                Intent locintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                locintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(locintent);
                break;
            case CODE_FEEDBACK:
                String fbdata = data;
                if (null == fbdata) {
                    if (fbdata.isEmpty()) {
                        handleFeedbackBg(context, null, msgId);
                    }
                } else {
                    try {
                        String FEEDBACK_LIST_CONTENT = "c";
                        JSONObject json = new JSONObject(fbdata);
                        JSONArray array = null;
                        array = json.getJSONArray(FEEDBACK_LIST_CONTENT);
                        // Check if list is empty. if so then launch feedbackactivity
                        if (null == array) {
                            if (array.length() == 0) {
                                handleFeedbackBg(context, null, pushObject.getMsgId());
                            }
                        } else {
                            // No exception, start the app
                            startApp(context);
                        }
                    } catch (JSONException e) {
                        handleFeedbackBg(context, null, pushObject.getMsgId());
                    }
                }
                break;
            case CODE_SIMPLE_PROMPT:
                startApp(context);
                break;
            case CODE_CUSTOM_ACTIONS:
                break;
            default:
                clearPendingDialogFlagAndDB(context, msgId);
                startApp(context);
                break;
        }

    }

    private String getRunningPackage(Context mContext) {
        String packageName = mContext.getPackageName();
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(10);
        for (ActivityManager.RunningTaskInfo str : taskInfo) {
            if (str.topActivity.toString().contains(packageName))
                return str.topActivity.getClassName();
        }
        return null;
    }

    /**
     * Just launch application in error condition
     * @param context
     */
    private void startLauncherActivity(Context context){
        Intent LauncherIntent = context.getPackageManager().getLaunchIntentForPackage(context.getApplicationContext().getPackageName());
        LauncherIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        LauncherIntent.putExtra(SHOW_PENDING_DIALOG, true);
        context.startActivity(LauncherIntent);

    }

    private void launchActivity(final Context mContext,String friendlyName) {
        if (null == friendlyName)
            friendlyName = mContext.getApplicationContext().getPackageName();
        final SharedPreferences activityPrefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_FRNDLST, Context.MODE_PRIVATE);
        String tempActivityName = activityPrefs.getString(friendlyName, null);
        if (null == tempActivityName) {
            if (friendlyName.contains("://")) {
                final String tmpName = friendlyName;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Intent deepLinkIntent = new Intent();
                            deepLinkIntent.setAction("android.intent.action.VIEW");
                            deepLinkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            deepLinkIntent.setData(Uri.parse(tmpName));
                            mContext.startActivity(deepLinkIntent);
                        } catch (ActivityNotFoundException e) {
                            startLauncherActivity(mContext);
                        }
                    }
                },100);

                // Either we have received FQName or ""
                tempActivityName = friendlyName;
            }else{
                startLauncherActivity(mContext);
            }
        } else {
            final String activityName = tempActivityName;
            final PackageManager pm = mContext.getPackageManager();
            Intent LauncherIntent = null;
            if (null == activityName) {
                // application needs to launched
                //1. check if application is running in BG
                String packageName = getRunningPackage(mContext);
                if (null == packageName) {
                    LauncherIntent = pm.getLaunchIntentForPackage(mContext.getPackageName());
                } else {
                    try {
                        final Class<?> classname = Class.forName(packageName);
                        LauncherIntent = new Intent(mContext.getApplicationContext(), classname);
                    } catch (ClassNotFoundException e1) {
                        LauncherIntent = pm.getLaunchIntentForPackage(mContext.getApplicationContext().getPackageName());
                        e1.printStackTrace();
                    }
                }
            } else {
                //Here we have a qualified name for we will try to launch it
                try {
                    final Class<?> classname = Class.forName(activityName);
                    LauncherIntent = new Intent(mContext.getApplicationContext(), classname);
                } catch (ClassNotFoundException e) {
                    String packageName = getRunningPackage(mContext);
                    if (null == packageName) {
                        //SendErrorLog(mContext.getApplicationContext(), extras, StreethawkText.STREETHAWK_ERROR_INVALID_ACTIVITY + activityName);
                        LauncherIntent = pm.getLaunchIntentForPackage(mContext.getApplicationContext().getPackageName());
                    } else {
                        try {
                            final Class<?> classname = Class.forName(packageName);
                            LauncherIntent = new Intent(mContext.getApplicationContext(), classname);
                        } catch (ClassNotFoundException e1) {
                            //SendErrorLog(mContext.getApplicationContext(), extras, StreethawkText.STREETHAWK_ERROR_INVALID_ACTIVITY + activityName);
                            LauncherIntent = pm.getLaunchIntentForPackage(mContext.getApplicationContext().getPackageName());
                        }
                    }
                }
            }
            LauncherIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            LauncherIntent.putExtra(SHOW_PENDING_DIALOG, true);
            mContext.startActivity(LauncherIntent);
        }
    }

    private void handleRateUpdate(Context mContext) {
        try {
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName()));
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(marketIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName()));
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(marketIntent);
        }
    }

    private void startApp(Context mContext) {
        Intent intent = null;
        String packageName = getRunningPackage(mContext);
        final PackageManager pm = mContext.getPackageManager();
        if (null == packageName) {
            intent = pm.getLaunchIntentForPackage(mContext.getApplicationContext().getPackageName());
        } else {
            try {
                final Class<?> classname = Class.forName(packageName);
                intent = new Intent(mContext.getApplicationContext(), classname);
            } catch (ClassNotFoundException e1) {
                intent = pm.getLaunchIntentForPackage(mContext.getApplicationContext().getPackageName());
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SHOW_PENDING_DIALOG, true);
        mContext.startActivity(intent);
    }

    private void launchActivityPG(Context context, String data) {
        final SharedPreferences activityPrefs = context.getSharedPreferences(Util.SHSHARED_PREF_FRNDLST, Context.MODE_PRIVATE);
        String tempactivityName = activityPrefs.getString(data, null);
        if (null == tempactivityName) {
            // Either we have received activityName or ""
            tempactivityName = data;
        }
        SharedPreferences.Editor e = activityPrefs.edit();
        e.putString(PHONEGAP_URL, tempactivityName);
        e.commit();
        ISHObserver instance = SHGcmListenerService.getISHObserver();
        if(null!=instance)
            instance.shNotifyAppPage(tempactivityName);;
        startApp(context);
    }

    private void handleCall(Context mContext, String PhoneNumber) {
        Intent callIntentDirect = new Intent(Intent.ACTION_CALL);
        callIntentDirect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntentDirect.setData(Uri.parse("tel:" + PhoneNumber));
        mContext.startActivity(callIntentDirect);
    }

    private void handleFeedbackBg(Context context, String data, String msgId) {
        PushNotificationDB dbObject = PushNotificationDB.getInstance(context);
        dbObject.open();
        dbObject.forceStoreNoDialog(msgId);
        dbObject.close();
        Intent intent = new Intent(context, SHFeedbackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle extras = new Bundle();
        extras.putString("SHFeedbackActyTitle", data);
        extras.putString("StreethawkText.MSGID", msgId);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    private void sendAcknowledgement(Context context, String msgId) {
        try {
            Bundle params = new Bundle();
            params.putString(Util.SHMESSAGE_ID, msgId);
            params.putString(Util.CODE, Integer.toString(CODE_PUSH_ACK));
            Logging manager = Logging.getLoggingInstance(context);
            manager.addLogsForSending(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCustomJsonFromServer(String title, String msg, String json) {
        ISHObserver instance = SHGcmListenerService.getISHObserver();
        if (null != instance) {
            instance.shReceivedRawJSON(title, msg, json);
        }else{
            Log.e(Util.TAG,SUBTAG+"no ISHObserver registered");
        }
    }

    private void sendResultLog(Context context, String msgId, int result, int code) {
        try {
            Bundle params = new Bundle();
            params.putString(Util.SHMESSAGE_ID, msgId);
            params.putString(Util.TYPE_NUMERIC, Integer.toString(code));
            params.putString(NotificationBase.SHRESULT, Integer.toString(result));
            params.putString(Util.CODE, Integer.toString(CODE_PUSH_RESULT));
            Logging manager = Logging.getLoggingInstance(context);
            manager.addLogsForSending(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to check is app is backgrounded
     *
     * @return
     */

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private boolean checkIfBG(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(SHFORCEPUSHTOBG, false))
            return true;
        ActivityManager activitymanager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activitymanager.getRunningTasks(Integer.MAX_VALUE);
        if (!(services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(context.getPackageName().toString()))) {
            return true;
        } else {
            if (null == context)
                return true;
            // app on top check if screen is live
            PowerManager powerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
            // if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (powerManager.isScreenOn())
                return false;
            else
                return true;
        }
    }

}


