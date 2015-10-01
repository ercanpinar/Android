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

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.streethawk.library.core.Util;

import java.io.Serializable;

public class GCMIntentService extends IntentService implements Serializable {
    private static final long serialVersionUID = -3614546558239691009L;

    private final String SUBTAG = "GCMIntentService ";

    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Bundle extras = intent.getExtras();
        String msgId = extras.getString(Constants.PUSH_MSG_ID);
        if(null==msgId){
            Log.e(Util.TAG,SUBTAG+ "Invalid messageId " + msgId);
            return;
        }
        GcmMessage message = new GcmMessage();
        if (!message.storePushMessageData(getApplicationContext(), extras)) {
            return;
        }
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        boolean enable_push = sharedPreferences.getBoolean(Constants.SHGCM_FLAG, true);
        if (enable_push) {
            String messageType = gcm.getMessageType(intent);
            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    Log.i(Util.TAG, GCMIntentService.class.getName() + " " + "Received error");
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                    Log.i(Util.TAG,SUBTAG+ GCMIntentService.class.getName() + " " + "Received deleted messages notification");
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    Intent pushNotificationIntent = new Intent();
                    pushNotificationIntent.setAction(Constants.BROADCAST_SH_PUSH_NOTIFICATION);
                    pushNotificationIntent.putExtra(Constants.MSGID, msgId);
                    sendBroadcast(pushNotificationIntent);
                    // Release the wake lock provided by the WakefulBroadcastReceiver.
                    GCMReceiver.completeWakefulIntent(intent);
                }
            }

        }
    }
}