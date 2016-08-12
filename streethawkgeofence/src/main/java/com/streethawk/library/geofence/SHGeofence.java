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
package com.streethawk.library.geofence;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;

public class SHGeofence implements Constants
{
    private static Context mContext;
    private static SHGeofence mInstance;


    private PendingIntent mGeofencePendingIntent;

    private SHGeofence() {}

    /**
     * Returns instance of SHGeofence class
     * @param context
     * @return
     */
    public static SHGeofence getInstance(Context context) {
        mContext = context;
        if (null == mInstance)
            mInstance = new SHGeofence();
        return mInstance;
    }

    /**
     * Use stopGeofenceMonitoring insead
     */
    public void stopMonitoring() {
        stopGeofenceMonitoring();
    }

    /**
     * Stop geofence monitoring
     */
    public void stopGeofenceMonitoring(){
        StreetHawkLocationService.getInstance(mContext).stopMonitoring();
    }

    /**
     * Start geofence monitoring
     */
    public void startGeofenceMonitoring(){
        Intent intent = new Intent(mContext,StreetHawkLocationService.class);
        mContext.startService(intent);
        StreetHawkLocationService.getInstance(mContext).startGeofenceMonitoring();

    }


    /**
     * Use registerForGoefenceTransition when a device enters a geofence registered with StreetHawk
     * @param observer
     */
    public void registerForGoefenceTransition(INotifyGeofenceTransition observer){
        GeofenceService.registerGeofenceObserver(observer);
    }

    /**
     * Function retruns list of geofences a device entered
     * @return
     */
    public ArrayList<GeofenceData> getGeofenceEnteredList(){
        return GeofenceService.getGeoEnterList();
    }

    /**
     * Function returns list of geofences device left
     * @return
     */
    public ArrayList<GeofenceData> getGeofenceExitList(){
        return GeofenceService.getGeoExitList();
    }

    /**
     * startGeofenceWithPermissionDialog is deprecated.
     * Instead
     * 1. set SH_GEO_PERMISSION_BUTTON_TEXT, SH_GEO_PERMISSION_TITLE and SH_GEO_PERMISSION_MESSAGE in res/values/strings.xml of your app
     * 2. Use startGeofenceWithPermissionDialog();
     * @param message
     */
    @Deprecated
    public void startGeofenceWithPermissionDialog(String message) {
        startGeofenceWithPermissionDialog();
    }

    /**
     * use startGeofenceWithPermissionDialog to make SDK ask for location permission from user.
     */
    public void startGeofenceWithPermissionDialog(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(mContext, AskGeoPermission.class);
            Bundle extras = new Bundle();
            extras.putBoolean(PERMISSION_BOOL, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(extras);
            mContext.startActivity(intent);
        }else{
            startGeofenceMonitoring();
        }
    }
}