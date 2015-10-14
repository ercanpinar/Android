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

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.streethawk.library.core.Util;

public class SHLocation{

    private static SHLocation mSHLocation;
    private static Context mContext;

    private static int VALUE_UPDATE_INTERVAL_BG = 0;
    private static int VALUE_UPDATE_DISTANCE_BG = 0;
    private static int VALUE_UPDATE_INTERVAL_FG = 0;
    private static int VALUE_UPDATE_DISTANCE_FG = 0;

    private static boolean activityLifecycleRegistered = false;
    private SHLocation() {}
    private void registerScheduledTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean taskRegistered = (PendingIntent.getBroadcast(mContext, 0,
                        new Intent(Constants.BROADCAST_APP_STATUS_CHK),
                        PendingIntent.FLAG_NO_CREATE) != null);
                if (taskRegistered) {
                    return;
                }
                SharedPreferences pref = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = pref.edit();
                e.putLong(Constants.SHTASKTIME, System.currentTimeMillis());
                e.commit();
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, LocationReceiver.class);
                intent.setAction(Constants.BROADCAST_APP_STATUS_CHK);
                intent.putExtra(Constants.SHPACKAGENAME,mContext.getPackageName());
                PendingIntent appStatusIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, appStatusIntent);
            }
        }).start();
    }

    /**
     * Returns Instance of SHLocation Class
     * @param context application context
     * @return instance of SHLocation class
     */
    public static SHLocation getInstance(Context context) {
        mContext = context;
        if (null == mSHLocation)
            mSHLocation = new SHLocation();
        return mSHLocation;
    }

    /**
     * Function to stop application to report location to StreetHawk. The function stops location reporting
     * till startLocationReporting is called again.
     */
    public void stopLocationReporting(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(Constants.SHLOCATION_FLAG, false);
        e.commit();
        Intent locationIntent = new Intent(mContext, StreethawkLocationService.class);
        mContext.stopService(locationIntent);
    }

    /**
     * Use reportWorkHomeLocationsOnly if you want to calculate work and home locations only
     */
    public void reportWorkHomeLocationsOnly(){
        updateLocationMonitoringParams(0, Constants.WORK_HOME_TIME_INTERVAL, 0, Constants.WORK_HOME_TIME_INTERVAL);
        restartLocationReporting();
    }

    /**
     * Core uses setActivityLifecycleCallbacks to register to activity lifecycle call backs
     * @param app
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void setActivityLifecycleCallbacks(Application app) {
        if (!activityLifecycleRegistered) {
            app.registerActivityLifecycleCallbacks(LocationActivityLifecycleCallback.getInstance());
            activityLifecycleRegistered = true;
        }
    }

    private boolean checkForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int coarseLocation = mContext.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int fineLocation = mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if ((coarseLocation == PackageManager.PERMISSION_GRANTED) || (fineLocation == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                    Log.e(Util.TAG, "Missing location permissions");
                return false;
            }
        } else {
            return true;
        }
    }


    /**
     * Call this function to start reporting user's location to StreetHawk server
     */
    public void startLocationReporting() {
        if (null == mContext)
            return;
        registerScheduledTask();
        restartLocationReporting();
    }

    /**
     * Restart location reporting from broadcast when location is enabled on device. Application need not
     * call this function at all.
     */
    public void restartLocationReporting(){
        if(!checkForLocationPermission()){
            return;
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(Constants.SHLOCATION_FLAG, true);
        e.commit();
        if((0==VALUE_UPDATE_INTERVAL_BG) && (0==VALUE_UPDATE_DISTANCE_BG)
            && (0==VALUE_UPDATE_INTERVAL_FG) && (0==VALUE_UPDATE_DISTANCE_FG)){
            Intent locationIntent = new Intent(mContext, StreethawkLocationService.class);
            locationIntent.putExtra(Constants.KEY_UPDATE_INTERVAL_BG, VALUE_UPDATE_INTERVAL_BG);
            locationIntent.putExtra(Constants.KEY_UPDATE_DISTANCE_BG, VALUE_UPDATE_DISTANCE_BG);
            locationIntent.putExtra(Constants.KEY_UPDATE_INTERVAL_FG, VALUE_UPDATE_INTERVAL_FG);
            locationIntent.putExtra(Constants.KEY_UPDATE_DISTANCE_FG, VALUE_UPDATE_DISTANCE_FG);
            mContext.startService(locationIntent);
            StreethawkLocationService.getInstance().startLocationReporting(mContext);
        }
    }

    /**
     * Update intervals for minimum distance and time update for locations.
     * @param UPDATE_INTERVAL_FG min time between two location reporting calls when app is in foreground
     * @param UPDATE_DISTANCE_FG min distance between two location reporting calls when app is in foreground
     * @param UPDATE_INTERVAL_BG min time between two location reporting calls when app is in backgrond
     * @param UPDATE_DISTANCE_BG min distance between two location reporting calls when app is in background
     */
    public void updateLocationMonitoringParams(int UPDATE_INTERVAL_FG, int UPDATE_DISTANCE_FG, int UPDATE_INTERVAL_BG, int UPDATE_DISTANCE_BG) {
        VALUE_UPDATE_INTERVAL_BG = UPDATE_INTERVAL_BG;
        VALUE_UPDATE_DISTANCE_BG = UPDATE_DISTANCE_BG;
        VALUE_UPDATE_INTERVAL_FG = UPDATE_INTERVAL_FG;
        VALUE_UPDATE_DISTANCE_FG = UPDATE_DISTANCE_FG;
    }
}
