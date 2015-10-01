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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.GeofencingEvent;
import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GeofenceService extends IntentService{
    private static final String TAG = "geofenceService";
    private final int CODE_GEOFENCE_UPDATES				= 22;       // place in geofence modules
    public GeofenceService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    /**
     * Handles incoming intents.
     *
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(Util.TAG,TAG+"Error");
            return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<com.google.android.gms.location.Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            GeofenceDB database = new GeofenceDB(context);  //TODO replace getApplicationContext with context of application in SDK
            database.open();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            //TODO: Store and check for parent geofence ID
            for (com.google.android.gms.location.Geofence geofence : triggeringGeofences) {
                final GeofenceData object = new GeofenceData();
                String geofenceID = geofence.getRequestId();
                String parentId = sharedPreferences.getString(Constants.PARENT_GEOFENCE_ID,null);
                if(null!=parentId){
                    if(parentId.equals(geofenceID)) {
                        continue;
                    }
                }
                database.getMatchedGeofenceData(geofenceID, object);
                if(object.getChildNode().equals("true")){
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString(Constants.PARENT_GEOFENCE_ID,geofenceID);
                    e.commit();
                    ArrayList<GeofenceData> geofenceList = new ArrayList<GeofenceData>();
                    SHGeofence client= SHGeofence.getInstance(getApplicationContext());
                    client.stopMonitoring();
                    client.getNodesToMonitor(object.getGeofenceID(),geofenceList);
                    geofenceList.add(object); //Adding current geofence as well to monitor exit
                    client.populateGeofenceList(geofenceList);
                    client.monitorGeofence();
                }else{
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString(Constants.PARENT_GEOFENCE_ID,null);
                    e.commit();
                    // TODO, place this logic outside of forloop to bundle geofences
                    try {
                        Logging manager = Logging.getLoggingInstance(context);
                        Bundle params = new Bundle();
                        params.putString(Util.CODE, Integer.toString(CODE_GEOFENCE_UPDATES));
                        params.putString(Util.SHMESSAGE_ID, null);
                        JSONObject matchGeofence = new JSONObject();
                        geofenceID = spitGeofenceId(geofenceID);
                        matchGeofence.put(geofenceID, object.getRadius());
                        params.putString("json",matchGeofence.toString());
                        manager.addLogsForSending(params);
                    }catch(JSONException jsonException){
                        jsonException.printStackTrace();
                    }
                }
            }
            database.close();

        } if (geofenceTransition == com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<com.google.android.gms.location.Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            GeofenceDB database = new GeofenceDB(getApplicationContext());  //TODO replace getApplicationContext with context of application in SDK
            database.open();
            for (com.google.android.gms.location.Geofence geofence : triggeringGeofences) {
                final GeofenceData object = new GeofenceData();
                String geofenceID = geofence.getRequestId();
                database.getMatchedGeofenceData(geofence.getRequestId(), object);
                if(object.getChildNode().equals("true")){
                    ArrayList<GeofenceData> geofenceList = new ArrayList<GeofenceData>();
                    SHGeofence client= SHGeofence.getInstance(getApplicationContext());
                    client.getNodesToMonitor(object.getParentID(), geofenceList);
                    client.stopMonitoring();
                    client.populateGeofenceList(geofenceList);
                    client.startGeofenceMonitoring();
                }else{
                    try {
                        Logging manager = Logging.getLoggingInstance(context);
                        Bundle params = new Bundle();
                        params.putString(Util.CODE, Integer.toString(CODE_GEOFENCE_UPDATES));
                        params.putString(Util.SHMESSAGE_ID, null);
                        JSONObject matchGeofence = new JSONObject();
                        geofenceID = spitGeofenceId(geofenceID);
                        matchGeofence.put(geofenceID,"-1");
                        params.putString("json",matchGeofence.toString());
                        manager.addLogsForSending(params);
                    }catch(JSONException jsonException){
                        jsonException.printStackTrace();
                    }
                }
            }
            database.close();
        }
    }

    private String spitGeofenceId(String id){
        String geofencid = id.substring(0,id.indexOf("-"));
        return geofencid;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
    }
}

