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

package com.streethawk.library.beacon;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SHBeaconModuleBC extends BroadcastReceiver {
    private final String IBEACON = "ibeacon";
    private final String KEY_IBEACON = "shKeyIBeacon";
    private final String SHBEACON_FLAG = "iBeaconFlag";
    public static final String SHSHARED_PREF_IBEACON = "shstoreibeacon";
    public static final String JSON_VALUE = "value";

    public SHBeaconModuleBC() {
    }

    public boolean isBeaconsSuppoted(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
            return false;
        Boolean beaconSupport = false;
        SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        beaconSupport = prefs.getBoolean(SHBEACON_FLAG, true); //Assuming true for first time
        return beaconSupport;
    }

    private void setBeaconTimeStamp(Context context, String value_iBeacon) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String currentTimeStamp = sharedPreferences.getString(KEY_IBEACON, null);
        if (null != currentTimeStamp && value_iBeacon.equals(currentTimeStamp)) {
            return;
        } else {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(KEY_IBEACON, value_iBeacon);
            edit.commit();
            /*Fetch new ibeacon list */
            if (value_iBeacon != null) {
                if (isBeaconsSuppoted(context))
                    fetchBeaconList(context);
            } else {
                //Clearing beaconList
                SharedPreferences prefs = context.getSharedPreferences(SHSHARED_PREF_IBEACON, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
            }
        }
    }

    private void fetchBeaconList(final Context context) {
        if (null == context)
            return;
        if (Util.isNetworkConnected(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    {
                        String installId = Util.getInstallId(context);
                        if (null == installId) {
                            SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                            SharedPreferences.Editor e = sharedPreferences.edit();
                            e.putString(KEY_IBEACON, null);
                            e.commit();
                            return;
                        }
                        if (installId.isEmpty()) {
                            SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                            SharedPreferences.Editor e = sharedPreferences.edit();
                            e.putString(KEY_IBEACON, null);
                            e.commit();
                            return;
                        }
                        String app_key = Util.getAppKey(context);
                        HashMap<String, String> logMap = new HashMap<String, String>();
                        logMap.put(Util.INSTALL_ID, installId);
                        BufferedReader reader = null;
                        try {
                            URL url = Util.getBeaconUrl(context);
                            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                            String libVersion = Util.getLibraryVersion();
                            connection.setReadTimeout(10000);
                            connection.setConnectTimeout(15000);
                            connection.setRequestMethod("GET");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.setRequestProperty("X-Installid", installId);
                            connection.setRequestProperty("X-App-Key", app_key);
                            connection.setRequestProperty("X-Version",libVersion);
                            connection.setRequestProperty("User-Agent", app_key + "(" + libVersion + ")");
                            OutputStream os = connection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            String logs="";
                            boolean first = true;
                            for (Map.Entry<String, String> entry : logMap.entrySet()) {
                                StringBuilder result = new StringBuilder();
                                if (first)
                                    first = false;
                                else
                                    result.append("&");
                                String key      = entry.getKey();
                                String value    = entry.getValue();
                                if(null!=key) {
                                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                                    result.append("=");
                                    if(null!=value) {
                                        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                                    }else{
                                        result.append(URLEncoder.encode("", "UTF-8"));
                                    }
                                }
                                logs+=result.toString();
                                result = null; //Force GC
                            }
                            writer.write(logs);
                            writer.flush();
                            writer.close();
                            os.close();
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String answer = reader.readLine();
                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                storeBeaconList(context, answer);
                                final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                if (isBeaconsSuppoted(context)) {
                                    if (null == bluetoothAdapter)
                                        return;
                                    if (bluetoothAdapter.isEnabled()) {
                                        // bluetooth is enabled start the service
                                        Intent iBeaconService = null;
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                            iBeaconService = new Intent(context, BeaconServiceKK.class);
                                        } else {
                                            iBeaconService = new Intent(context, BeaconServiceL.class);
                                        }
                                        if (checkBeaconServiceStatus(context)) {
                                            // if service is running, stop it and start it again
                                            context.stopService(iBeaconService);
                                        }
                                        context.startService(iBeaconService);
                                    }
                                    Logging.getLoggingInstance(context).processAppStatusCall(answer);
                                }

                            } else {
                                SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                                SharedPreferences.Editor e = sharedPreferences.edit();
                                e.putString(KEY_IBEACON, null);
                                e.commit();
                                Logging manager = Logging.getLoggingInstance(context);
                                manager.processErrorAckFromServer(answer);
                            }
                            connection.disconnect();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }


    private boolean checkBeaconServiceStatus(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String beaconClassName = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            beaconClassName = BeaconServiceKK.class.getName();
        } else {
            beaconClassName = BeaconServiceL.class.getName();
        }
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (beaconClassName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void storeBeaconList(Context context, String answer) {
        List<BeaconData> beaconDataList = new ArrayList<BeaconData>();
        try {
            JSONObject jsonObject = new JSONObject(answer);
            JSONObject value = jsonObject.getJSONObject(JSON_VALUE);
            Iterator<?> UUIDKeys = value.keys();
            while (UUIDKeys.hasNext()) {
                String idUUID = UUIDKeys.next().toString();
                JSONObject major = value.getJSONObject(idUUID);
                Iterator<?> majorKeys = major.keys();
                while (majorKeys.hasNext()) {
                    String majorJson = majorKeys.next().toString();
                    int idMajor = Integer.parseInt(majorJson);
                    JSONObject minor = major.getJSONObject(majorJson);
                    Iterator<?> minorKeys = minor.keys();
                    while (minorKeys.hasNext()) {
                        String minorJson = minorKeys.next().toString();
                        int idMinor = Integer.parseInt(minorJson);
                        String ID = Integer.toString(minor.getInt(minorJson));
                        BeaconData object = new BeaconData();
                        object.setBeaconId(ID);
                        object.setUUID(idUUID);
                        object.setMajorNumber(idMajor);
                        object.setMinorNUmber(idMinor);
                        beaconDataList.add(object);
                    }
                }
                //Storing beacon list here
                if (null != beaconDataList) {
                    if (!beaconDataList.isEmpty()) {
                        BeaconDB beaconDB = BeaconDB.getInstance(context);
                        beaconDB.open();
                        beaconDB.deleteBeaconData();   // clearing past data
                        beaconDB.storeBeaconData(beaconDataList);
                        beaconDB.close();
                    }
                }
            }
        } catch (JSONException e) {
            SharedPreferences prefs = context.getSharedPreferences(SHSHARED_PREF_IBEACON, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // returning as beacons are not supported at all
            return;
        }
        String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            boolean beaconMonitor = sharedPreferences.getBoolean(Constants.BEACON_MONITOR_FLAG,false);
            if(!beaconMonitor)
                return;
            if (bluetoothAdapter.isEnabled()) {
                Log.i(Util.TAG, "Starting beacon service");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Intent beaconService = new Intent(context, BeaconServiceKK.class);
                    context.startService(beaconService);
                    BeaconServiceKK beaconServiceObj = new BeaconServiceKK(context);
                    beaconServiceObj.initiateFirstScan();
                } else {
                    Intent beaconService = new Intent(context, BeaconServiceL.class);
                    context.startService(beaconService);
                    BeaconServiceL beaconServiceObj = new BeaconServiceL(context);
                    beaconServiceObj.initiateFirstScan();
                }
            } else {
                Log.i(Util.TAG, "Stopping beacon service");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    BeaconServiceKK obj = new BeaconServiceKK(context);
                    obj.forceClearBeaconList(context);
                    obj.unRegisterBeconTask(context);
                    obj=null;
                    Intent beaconService = new Intent(context, BeaconServiceKK.class);
                    context.stopService(beaconService);

                } else {
                    BeaconServiceL obj = new BeaconServiceL(context);
                    obj.forceClearBeaconList(context);
                    obj.unRegisterBeconTask(context);
                    obj=null;
                    Intent beaconService = new Intent(context, BeaconServiceL.class);
                    context.stopService(beaconService);
                }
            }
        }
        String installId = intent.getStringExtra(Util.INSTALL_ID);
        if (null == installId) {
            return;
        }
        if (!installId.equals(Util.getInstallId(context))) {
            return;
        }

        if (action.equals(Util.BROADCAST_SH_APP_STATUS_NOTIFICATION)) {
            String answer = intent.getStringExtra(Util.APP_STATUS_ANSWER);
            try {
                JSONObject object = new JSONObject(answer);
                if (object.has(Util.APP_STATUS)) {
                    if (object.get(Util.APP_STATUS) instanceof JSONObject) {
                        JSONObject app_status = object.getJSONObject(Util.APP_STATUS);
                        if (app_status.has(IBEACON) && !app_status.isNull(IBEACON)) {
                            Object value_iBeacon = app_status.get(IBEACON);
                            if (value_iBeacon instanceof String) {
                                setBeaconTimeStamp(context, (String) value_iBeacon);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (action.equals(Constants.BROADCAST_BEACON_SCAN_TRIGGER)) {
            BeaconServiceBase obj;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                obj = new BeaconServiceKK(context);
            } else {
                obj = new BeaconServiceL(context);
            }
            if (obj.isBTEnable(context)) {
                obj.unRegisterBeconTask(context);
                boolean bg = Util.isAppBG(context);
                int scanInterval = bg ? Constants.BLE_SCAN_INTERVAL_BG : Constants.BLE_SCAN_INTERVAL_FG;
                obj.registerBeaconTask(context, scanInterval);
                obj.scanForBeacon();
            }
        }
    }
}

