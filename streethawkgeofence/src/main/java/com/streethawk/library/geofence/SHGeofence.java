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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;




import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SHGeofence implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,Constants
        //Comemnt this line for Xamarin
        ,ResultCallback<Status> {
    private final String SUBTAG = "Geofence ";
    private static Context mContext;
    private static SHGeofence mInstance;

    private GoogleApiClient mGoogleApiClient;         // Change to private
    private static ArrayList<com.google.android.gms.location.Geofence> mGeofenceList;        // change to private
    private PendingIntent mGeofencePendingIntent;

    private SHGeofence() {
    }

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

    protected synchronized void buildGoogleApiClient() {
                   mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(mContext, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        if (null == mGeofenceList) {
            ArrayList<GeofenceData> geofenceList = new ArrayList<GeofenceData>();
            SHGeofence client = SHGeofence.getInstance(mContext);
            client.getNodesToMonitor(null, geofenceList);
            client.populateGeofenceList(geofenceList);
        }
        if (mGeofenceList != null) {
            if (!mGeofenceList.isEmpty()) {
                GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
                // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
                // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
                // is already inside that geofence.
                builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
                // Add the geofences to be monitored by geofencing service.
                builder.addGeofences(mGeofenceList);
                // Return a GeofencingRequest.
                return builder.build();
            }
        }
        return null;
    }

    /**
     * Use registerForGoefenceTransition when a device enters a geofence registered with StreetHawk
     * Implement {@link @INotifyGeofenceTransition}
     * @param observer
     */
    public void registerForGoefenceTransition(INotifyGeofenceTransition observer){
        GeofenceService.registerGeofenceObserver(observer);
    }


    /**
     * Function to stop monitoring of geofences
     */
    public void stopMonitoring() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(IS_GEOFENCE_ENABLE,false);
        e.commit();
        if (null != mGoogleApiClient) {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            )/*.setResultCallback(this)*/; // Result processed in onResult().
        } else {
            Log.e(Util.TAG, SUBTAG + "mGoogleApiClient is null in stopMonitoringExistingGeofence.Check...");
        }
    }

    /**
     * Function to start monitoring geofences
     */
    public void startGeofenceMonitoring() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                buildGoogleApiClient();
                mGoogleApiClient.connect();
                if(Util.getPlatformType()== PLATFORM_XAMARIN){
                    StreetHawk.INSTANCE.tagString("sh_module_geofence","true");
                }
            }
        }).start();

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
        if(Util.PLATFORM_XAMARIN==Util.RELEASE_PLATFORM){
            Log.i(Util.TAG,"startGeofenceWithPermissionDialog is not supported on Xamarin");
            return;
        }
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


    protected void monitorGeofence() {
        GeofencingRequest request = getGeofencingRequest();
        if (null == request) {
            Log.e(Util.TAG,SUBTAG+"getGeofencingRequest returned null");
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        // The GeofenceRequest object.
                        request,
                        // A pending intent that that is reused when calling removeGeofences(). This
                        // pending intent is used to generate an intent when a matched geofence
                        // transition is observed.
                        getGeofencePendingIntent()
                )/*.setResultCallback(this)*/; // Result processed in onResult().
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                securityException.printStackTrace();
            }
        } else {
            Log.e(Util.TAG, SUBTAG + "mGoogleApiClient not connected in startGeofenceMonitoring");
        }
    }

    private void parseAndStoreGeofences(String parent, GeofenceDB storeGeofenceDB, JSONArray geofenceArray) {
        for (int i = 0; i < geofenceArray.length(); i++) {
            try {
                Object tmpObject = geofenceArray.get(i);

                if (tmpObject instanceof JSONObject) {
                    GeofenceData geofenceData = new GeofenceData();
                    JSONObject tmp = (JSONObject) tmpObject;
                    String geofenceId = tmp.getString(GeofenceDB.GeofenceHelper.COLUMN_GEOFENCEID);
                    geofenceData.setGeofenceID(geofenceId);
                    geofenceData.setLatitude(tmp.getDouble(GeofenceDB.GeofenceHelper.COLUMN_LATITUDE));
                    geofenceData.setLongitude(tmp.getDouble(GeofenceDB.GeofenceHelper.COLUMN_LONGITUDE));
                    geofenceData.setRadius((float) tmp.getDouble(GeofenceDB.GeofenceHelper.COLUMN_RADIUS));
                    geofenceData.setParentID(parent);
                    Object nodeObject = null;
                    try {
                        nodeObject = tmp.get(GeofenceDB.GeofenceHelper.COLUMN_NODE);
                    } catch (JSONException e) {
                        nodeObject = null;
                    }
                    if (null != nodeObject) {
                        if (nodeObject instanceof JSONArray) {
                            geofenceData.setChildNodes(true);
                            storeGeofenceDB.storeGeofenceData(geofenceData);
                            // Calling function recursively
                            parseAndStoreGeofences(geofenceId, storeGeofenceDB, (JSONArray) nodeObject);
                        } else {
                            geofenceData.setChildNodes(false);
                            storeGeofenceDB.storeGeofenceData(geofenceData);
                        }
                    } else {
                        geofenceData.setChildNodes(false);
                        storeGeofenceDB.storeGeofenceData(geofenceData);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void populateGeofenceList(ArrayList<GeofenceData> geofenceList) {
        mGeofenceList = new ArrayList<com.google.android.gms.location.Geofence>();
        for (GeofenceData obj : geofenceList) {
            mGeofenceList.add(new com.google.android.gms.location.Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(obj.getGeofenceID())

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            obj.getLatitude(),
                            obj.getLongitude(),
                            obj.getRadius()
                    )
                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(com.google.android.gms.location.Geofence.NEVER_EXPIRE)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT)

                            // Create the geofence.
                    .build());
        }
    }

    public void getNodesToMonitor(String parentId, ArrayList<GeofenceData> geofenceList) {
        GeofenceDB database = new GeofenceDB(mContext);
        database.open();
        database.getGeofencesListToMonitor(parentId, geofenceList);
        database.close();
    }

    public void storeGeofenceList(ArrayList<GeofenceData> geofenceList) {
        getNodesToMonitor(null, geofenceList);
        populateGeofenceList(geofenceList);
    }

    public void storeGeofenceList(JSONArray geofenceArray) {
        GeofenceDB storeGeofenceDB = new GeofenceDB(mContext);
        storeGeofenceDB.open();
        parseAndStoreGeofences(null, storeGeofenceDB, geofenceArray);
        ArrayList<GeofenceData> geofenceList = new ArrayList<GeofenceData>();
        storeGeofenceList(geofenceList);
        storeGeofenceDB.close();
    }

    @Override
    public void onConnected(Bundle bundle) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(IS_GEOFENCE_ENABLE, true);
        e.commit();
        monitorGeofence();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    // Comment this line for Xamarin
    @Override
    public void onResult(Status status) {

    }

}
