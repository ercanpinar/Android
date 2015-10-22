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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class SHBackgroundNotification extends NotificationBase {


    private final String SUBTAG = "SHBackgroundNotification ";
    private Context mContext;
    private PushNotificationData pushData;

    public SHBackgroundNotification(Context context, PushNotificationData data) {
        this.mContext = context;
        this.pushData = data;
    }

    /**
     * Function checks if a sounds file exists in app
     *
     * @param soundName
     * @return lights if sound is null, default if sound file is not available, else id of sound file requested
     */
    private Uri getNotificationSound(String soundName) {
        final String ANDROID_RESOURCE = "android.resource://";
        final String FORESLASH = "/";
        Uri uri = null;

        if (null == soundName)
            return null;
        if (soundName.isEmpty())
            return null;
        int id = 0;
        String packageName = mContext.getPackageName();
        id = mContext.getResources().getIdentifier(soundName, "raw", packageName);
        if (id == 0)
            return null;
        else
            return Uri.parse(ANDROID_RESOURCE + mContext.getPackageName() + FORESLASH + id);
    }

    /**
     * For android Lolipop, code decides to display headsup notifcation
     *
     * @param code
     * @return
     */
    private boolean shouldDisplayHeadsUpNotification(int code) {
        switch (code) {
            case CODE_RATE_APP:
            case CODE_UPDATE_APP:
            case CODE_LAUNCH_ACTIVITY:
            case CODE_CALL_TELEPHONE_NUMBER:
            case CODE_USER_REGISTRATION_SCREEN:
            case CODE_USER_LOGIN_SCREEN:
                return false;
            default:
                return true;
        }
    }

    private Boolean checkInvalidCode(int code) {
        switch (code) {
            case CODE_OPEN_URL:
            case CODE_LAUNCH_ACTIVITY:
            case CODE_RATE_APP:
            case CODE_UPDATE_APP:
            case CODE_CALL_TELEPHONE_NUMBER:
            case CODE_SIMPLE_PROMPT:
            case CODE_FEEDBACK:
            case CODE_IBEACON:
            case CODE_CUSTOM_JSON_FROM_SERVER:
            case CODE_ENABLE_LOCATION:
            case CODE_ACCEPT_PUSHMSG:
            case CODE_USER_LOGIN_SCREEN:
            case CODE_USER_REGISTRATION_SCREEN:
                return false;
            default:
                PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
                obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
                return true;
        }
    }

    private boolean isInAppSlide() {
        //pushData.displayMyData();
        Float portion;
        int orientation;
        int speed;
        if (null == pushData) {
            return false;
        }
        try {
            portion = Float.parseFloat(pushData.getPortion());
        } catch (NumberFormatException r) {
            portion = -1.0f;
        } catch (NullPointerException e) {
            portion = -1.0f;
        }
        try {
            orientation = Integer.parseInt(pushData.getOrientation());
        } catch (NumberFormatException r) {
            orientation = -1;
        } catch (NullPointerException e) {
            orientation = -1;
        }
        try {
            speed = Integer.parseInt(pushData.getSpeed());
        } catch (NumberFormatException r) {
            speed = -1;
        } catch (NullPointerException e) {
            speed = -1;
        }
        if ((portion > 0.0) || (orientation >= 0 && orientation < 4) || (speed >= 0))
            return true;
        return false;
    }


    public void display() {
        if (null == mContext) {
            Log.e(Util.TAG, SUBTAG + "Context is null in display()");
            PushNotificationBroadcastReceiver obj = new PushNotificationBroadcastReceiver();
            obj.clearPendingDialogFlagAndDB(mContext, pushData.getMsgId());
            return;
        }

        String tmpTitle = pushData.getTitle();
        tmpTitle = getUnicodeForEmoji(tmpTitle, true);
        String tmpMsg = pushData.getMsg();
        tmpMsg = getUnicodeForEmoji(tmpMsg, true);
        String msgId = pushData.getMsgId();

        Spanned title = null;
        Spanned msg = null;
        Spanned app_name = null;
        String app_name_str = Util.getAppName(mContext);

        if (tmpTitle != null) {
            if (!tmpTitle.isEmpty())
                title = Html.fromHtml(tmpTitle);
        }

        if (tmpMsg != null) {
            if (!tmpMsg.isEmpty())
                msg = Html.fromHtml(tmpMsg);
        }

        if (null != app_name_str)
            app_name = Html.fromHtml(app_name_str);
        int code = Integer.parseInt(pushData.getCode());
        if (checkInvalidCode(code)) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        String positiveButtonTitle = null;

        if (code == CODE_SIMPLE_PROMPT) {
            positiveButtonTitle = getStringtoDisplay(mContext, TYPE_SIMPLE_PUSH_NOTIFICATION_POSITIVE);
        } else {
            positiveButtonTitle = getPositiveButtonTitle(mContext, code);
        }
        if (code == CODE_OPEN_URL) {
            if (isInAppSlide()) {
                PushNotificationDB dbObject = PushNotificationDB.getInstance(mContext);
                dbObject.open();
                dbObject.forceStoreNoDialog(msgId);
                dbObject.close();
            }
        }
        String negativeButtonTitle = getNegativeButtonTitle(mContext, code);
        String neutralButtonTitle;

        Bundle extras = new Bundle();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(Constants.PENDING_DIALOG, msgId);
        e.commit();
        extras.putString(Constants.PENDING_DIALOG, msgId);
        extras.putBoolean(Constants.FROMBG, true);
        extras.putString(Constants.SHPACKAGENAME, mContext.getPackageName());


        Intent positiveIntent = new Intent();
        positiveIntent.putExtras(extras);
        positiveIntent.setAction(Constants.BROADCAST_STREETHAWK_ACCEPTED);

        Intent negativeIntent = new Intent();
        negativeIntent.putExtras(extras);
        if (code == CODE_RATE_APP)
            negativeIntent.setAction(Constants.BROADCAST_STREETHAWK_POSTPONED);
        else
            negativeIntent.setAction(Constants.BROADCAST_STREETHAWK_DECLINED);

        Intent neutralIntent = new Intent();
        neutralIntent.putExtras(extras);
        neutralIntent.setAction(Constants.BROADCAST_STREETHAWK_POSTPONED);

        PendingIntent neutralPendingIntent = null;
        PendingIntent positivePendingIntent = PendingIntent.getBroadcast(mContext, code, positiveIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent negativePendingIntent = PendingIntent.getBroadcast(mContext, code, negativeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (null == title) {
            title = app_name;
            app_name = null;                      // Blank
        }
        if (null == msg && Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            //title= app_name;                        // Blank
            msg = app_name;
            app_name = null;
        }

        //Floating notification for lolipop

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (shouldDisplayHeadsUpNotification(code))
                builder.setPriority(Notification.PRIORITY_HIGH);

        }
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
        builder.setContentTitle(title);
       // if (app_name != null)
       //     builder.setSubText(app_name);

        if (msg != null) {
            builder.setContentText(msg);
        }

        if (null != title)
            builder.setTicker(title);
        else {
            if (null != msg)
                builder.setTicker(msg);
            else
                builder.setTicker(mContext.getString(mContext.getApplicationInfo().labelRes));
        }
        builder.setAutoCancel(true);
        String soundFile = pushData.getSound();
        if (null == soundFile)
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
        else {
            Uri soundUri = getNotificationSound(soundFile);
            if (null == soundUri)
                builder.setDefaults(Notification.DEFAULT_SOUND);
            else
                builder.setSound(soundUri);
        }
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                Util.getAppIcon(mContext));
        builder.setLargeIcon(icon);

        builder.setSmallIcon(Util.getAppIcon(mContext));
        if (code == CODE_RATE_APP) {
            neutralPendingIntent = PendingIntent.getBroadcast(mContext, code, neutralIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        switch (code) {
            case CODE_SIMPLE_PROMPT:
                builder.setDeleteIntent(negativePendingIntent);
                break;
            case CODE_RATE_APP:
                builder.addAction(getIcon(mContext, code, Constants.STREETHAWK_DECLINED), negativeButtonTitle, neutralPendingIntent);
                builder.setDeleteIntent(neutralPendingIntent);
                break;
            default:
                builder.addAction(getIcon(mContext, code, Constants.STREETHAWK_DECLINED), negativeButtonTitle, negativePendingIntent);
                builder.setDeleteIntent(negativePendingIntent);
        }
        builder.addAction(getIcon(mContext, code, Constants.STREETHAWK_ACCEPTED), positiveButtonTitle, positivePendingIntent);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.contentIntent = positivePendingIntent;
        if (Integer.parseInt(pushData.getCode()) == CODE_FEEDBACK) {
            String data = pushData.getData();
            if (null == data) {
                e.putString(Constants.PENDING_DIALOG, null);
                e.commit();
            } else {
                if (data.isEmpty()) {
                    e.putString(Constants.PENDING_DIALOG, null);
                    e.commit();
                } else {
                    try {
                        String FEEDBACK_LIST_CONTENT = "c";
                        JSONObject json = new JSONObject(data);
                        JSONArray array = null;
                        array = json.getJSONArray(FEEDBACK_LIST_CONTENT);
                        // Check if list is empty. if so then launch feedbackactivity
                        if (null == array || 0 == array.length()) {
                            e.putString(Constants.PENDING_DIALOG, null);
                            e.commit();
                        }
                    } catch (JSONException exception) {
                        e.putString(Constants.PENDING_DIALOG, null);
                        e.commit();
                    }
                }
            }
        }
        if (notificationManager != null) {
            notificationManager.notify(Integer.parseInt(msgId), notification);
        }
    }

}