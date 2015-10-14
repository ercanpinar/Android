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
package com.streethawk.library.locations;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

public class LocationReceiver extends BroadcastReceiver {
    private final int CODE_USER_DISABLES_LOCATION 		= 8112;
    public LocationReceiver() {}
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        boolean status = sharedPreferences.getBoolean(Constants.SHLOCATION_FLAG, false);
        if(!status)
            return;
        String action = intent.getAction();
        if (action.equals("android.location.PROVIDERS_CHANGED")) {
            ContentResolver contentResolver = context.getContentResolver();
            final boolean isGpsEnabled = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
            if (!(isGpsEnabled)) {
                try {
                    Bundle extras = new Bundle();
                    extras.putString(Util.CODE, Integer.toString(CODE_USER_DISABLES_LOCATION));
                    Logging.getLoggingInstance(context).addLogsForSending(extras);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                Location location = StreethawkLocationService.getInstance().getLastKnownLocation(context);
                if (null != location) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    if (lat == 0 && lng == 0)
                        return;
                    Bundle extras = new Bundle();
                    extras.putString(Util.CODE, Integer.toString(Constants.CODE_LOCATION_UPDATES));
                    extras.putString(Util.SHMESSAGE_ID, null);
                    extras.putString(Constants.LOCAL_TIME, Util.getFormattedDateTime(System.currentTimeMillis(), false));
                    extras.putString(Constants.SHLATTITUDE, Double.toString(location.getLatitude()));
                    extras.putString(Constants.SHLONGITUDE, Double.toString(location.getLongitude()));
                    Logging.getLoggingInstance(context).addLogsForSending(extras);
                }
            }
        }
        if (action.equals("com.streethawk.intent.action.gcm.STREETHAWK_LOCATIONS")) {
                if (null != intent) {
                    try {
                        String packageName = intent.getStringExtra(Constants.SHPACKAGENAME);
                        if (!packageName.equals(context.getPackageName())) {
                            return;
                        }
                    } catch (Exception e) {
                        return;
                    }
                    Location location = StreethawkLocationService.getInstance().getLastKnownLocation(context);
                    if (null != location) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        if (lat == 0 && lng == 0)
                            return;
                        Bundle extras = new Bundle();
                        extras.putString(Util.CODE, Integer.toString(Constants.CODE_PERIODIC_LOCATION_UPDATE));
                        extras.putString(Util.SHMESSAGE_ID, null);
                        extras.putString(Constants.LOCAL_TIME, Util.getFormattedDateTime(System.currentTimeMillis(), false));
                        extras.putString(Constants.SHLATTITUDE, Double.toString(location.getLatitude()));
                        extras.putString(Constants.SHLONGITUDE, Double.toString(location.getLongitude()));
                        Logging.getLoggingInstance(context).addLogsForSending(extras);
                    }
                }
            }
        }
    }
