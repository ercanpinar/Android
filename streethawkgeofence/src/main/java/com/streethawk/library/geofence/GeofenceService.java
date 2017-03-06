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
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.streethawk.library.core.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import com.streethawk.library.core.Logging;

public class GeofenceService extends IntentService {
    private static final String TAG = "geofenceService";

    public GeofenceService() {
        super(TAG);
    }

    private static Set<String> triggeredParent = new HashSet<String>();

    private static INotifyGeofenceTransition mINotifyGeofenceTransition = null;

    private static ArrayList<GeofenceData> geoEnterList = new ArrayList<GeofenceData>();
    private static ArrayList<GeofenceData> geoExitList = new ArrayList<GeofenceData>();


    @Override
    public void onCreate() {
        super.onCreate();
    }


    public static void registerGeofenceObserver(INotifyGeofenceTransition observer) {
        mINotifyGeofenceTransition = observer;
    }

    /**
     * Return list of geofences device entered
     *
     * @return
     */
    public static ArrayList<GeofenceData> getGeoEnterList() {
        return geoEnterList;
    }

    /**
     * Returns list of geofences device leaves
     *
     * @return
     */
    public static ArrayList<GeofenceData> getGeoExitList() {
        return geoExitList;
    }


    public void updateVisibleGeofence(Context context, String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            String key = "tmpGepfence";
            SharedPreferences sharedPreferences = context.getSharedPreferences("SHGeofenceCache", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = sharedPreferences.edit();
            Set<String> tmp = sharedPreferences.getStringSet(key, null);
            if (null == tmp) {
                tmp = new HashSet<String>();
            }
            tmp.add(id);
            e.putStringSet(key, tmp);
            e.commit();
        }
    }

    public static void notifyAllGeofenceExit(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            String key = "tmpGepfence";
            SharedPreferences sharedPreferences = context.getSharedPreferences("SHGeofenceCache", Context.MODE_PRIVATE);
            Set<String> tmp = sharedPreferences.getStringSet(key, null);
            if (null == tmp) {
                return;
            } else {
                String logs = "[";
                for (String str : tmp) {
                    try {
                        logs += ((new JSONObject().put(str, "-1")).toString()) + ",";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                int size = logs.length();
                if (size > 1) {
                    logs = logs.substring(0, size - 1);
                }
                logs += "]";
                if (logs.equals("[]")) {
                    return;
                }
                Bundle params = new Bundle();
                params.putInt(Util.CODE, Constants.CODE_GEOFENCE_UPDATES);
                params.putString(Util.SHMESSAGE_ID, null);
                params.putString("json", logs);
                GeofenceLogging.getInstance().sendLogs(context, params);
            }
        }
        if (null != triggeredParent)
            triggeredParent.clear();
    }

    /**
     * Register new geofence list for monitoring when the user exists geofence
     */
    private void refreshGeofenceMonitoring() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<GeofenceData> geofenceList = new ArrayList<GeofenceData>();
                StreetHawkLocationService client = StreetHawkLocationService.getInstance(getApplicationContext());
                client.stopMonitoring();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                client.storeGeofenceListForMonitoring(geofenceList);
                client.startGeofenceMonitoring();
            }
        }).start();
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
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if ((geofenceTransition == com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER) ||
                (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL)) {
            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<com.google.android.gms.location.Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            GeofenceDB database = new GeofenceDB(context);
            database.open();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            ArrayList<GeofenceData> geolist = new ArrayList<GeofenceData>();
            for (com.google.android.gms.location.Geofence geofence : triggeringGeofences) {
                final GeofenceData object = new GeofenceData();
                String geofenceID = geofence.getRequestId();
                database.getMatchedGeofenceData(geofenceID, object);
                try {
                    Bundle params = new Bundle();
                    params.putInt(Util.CODE, Constants.CODE_GEOFENCE_UPDATES);
                    params.putString(Util.SHMESSAGE_ID, null);
                    JSONObject matchGeofence = new JSONObject();
                    geofenceID = spitGeofenceId(geofenceID);
                    matchGeofence.put(geofenceID, object.getRadius());
                    params.putString("json", matchGeofence.toString());
                    updateVisibleGeofence(context, geofenceID);
                    GeofenceLogging.getInstance().sendLogs(context, params);
                    geolist.add(object);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
            if (null != mINotifyGeofenceTransition) {
                geoEnterList = geolist;
                // mINotifyGeofenceTransition.onDeviceEnteringGeofence(geolist);
            }
            geolist = null;
            database.close();
        }
        if (geofenceTransition == com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<com.google.android.gms.location.Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            GeofenceDB database = new GeofenceDB(getApplicationContext());
            database.open();
            ArrayList<GeofenceData> geolist = new ArrayList<GeofenceData>();
            for (com.google.android.gms.location.Geofence geofence : triggeringGeofences) {
                final GeofenceData object = new GeofenceData();
                String geofenceID = geofence.getRequestId();
                database.getMatchedGeofenceData(geofenceID, object);
                try {
                    Bundle params = new Bundle();
                    params.putInt(Util.CODE, Constants.CODE_GEOFENCE_UPDATES);
                    params.putString(Util.SHMESSAGE_ID, null);
                    JSONObject matchGeofence = new JSONObject();
                    geofenceID = spitGeofenceId(geofenceID);
                    matchGeofence.put(geofenceID, "-1");
                    params.putString("json", matchGeofence.toString());
                    GeofenceLogging.getInstance().sendLogs(context, params);
                    geolist.add(object);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
            if (null != mINotifyGeofenceTransition) {
                //mINotifyGeofenceTransition.onDeviceLeavingGeofence(geoExitList);
                geoExitList = geolist;
            }
            geolist = null;
            database.close();
            //TODO check this for location disable condition
            refreshGeofenceMonitoring();
        }
    }

    private String spitGeofenceId(String id) {
        String geofencid = id.substring(0, id.indexOf("-"));
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

