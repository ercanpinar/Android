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
package com.streethawk.library.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class StreethawkBroadcastReceiver extends BroadcastReceiver implements Constants {

    private final String SUBTAG = "StreethawkBroadcastReceiver ";

    public StreethawkBroadcastReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        int DURATION_APP_STATUS_LOG = 86400000;             // 1 day in milliseconds
        int DURATION_SIX_HRS = 21600000;                    // 6 hrs in milliseconds
        String action = intent.getAction();
        // Start streethawk core service when device reboots
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (Util.getStreethawkState(context)) {
                if (Util.getStreethawkState(context)) {
                    Intent bootIntent = new Intent(context, StreetHawkCoreService.class);
                    context.startService(bootIntent);
                }
            }
        }

        if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            int timezonelast = sharedPreferences.getInt(SHTIMEZONE, -1);
            int timezoneNow = Util.getTimeZoneOffsetInMinutes();
            if ((timezonelast != timezoneNow)) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putInt(SHTIMEZONE, timezoneNow);
                edit.commit();
                // 8050 is a priority log hence logs will flush
                Bundle logParams = new Bundle();
                logParams.putInt(Util.CODE, CODE_DEVICE_TIMEZONE);
                logParams.putString(Util.SHMESSAGE_ID, null);
                logParams.putString(TYPE_NUMERIC, Integer.toString(Util.getTimeZoneOffsetInMinutes()));
                Logging manager = Logging.getLoggingInstance(context);
                manager.addLogsForSending(logParams);
            }
            Logging manager = Logging.getLoggingInstance(context);
            manager.flushPendingFeedback();
            manager.ForceFlushLogsToServer();
        }

        // App status and heartbeat
        if (action.equals(BROADCAST_APP_STATUS_CHK)) {
            Bundle receivedParams = intent.getExtras();
            if (null != receivedParams) {
                if (!(receivedParams.getString(SHPACKAGENAME).equals(context.getPackageName()))) {
                    return;
                }
            } else {
                return;
            }
            long currentTime = System.currentTimeMillis();
            SharedPreferences pref = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            long storedTime = pref.getLong(SHTASKTIME, currentTime);
            int difference = (int) (currentTime - storedTime);
            if (!Util.getStreethawkState(context)) {
                if (difference >= DURATION_APP_STATUS_LOG) {
                    //Store current time for next task
                    SharedPreferences.Editor e = pref.edit();
                    e.putLong(SHTASKTIME, currentTime);
                    e.commit();
                    Log.e(Util.TAG, SUBTAG + "Checking app status for the day");
                    Logging.getLoggingInstance(context).checkAppState();
                }
            }
            // send heartbeat every 6 hrs
            if (difference >= DURATION_SIX_HRS) {
                Bundle extras = new Bundle();
                extras.putInt(Util.CODE,CODE_HEARTBEAT);
                extras.putString(Util.SHMESSAGE_ID, null);
                Logging manager = Logging.getLoggingInstance(context);
                manager.addLogsForSending(extras);
                SharedPreferences.Editor e = pref.edit();
                e.putLong(SHTASKTIME, currentTime);
                e.commit();
            }
        }
    }
}

