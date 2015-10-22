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
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.streethawk.library.core.*;

import org.json.JSONException;
import org.json.JSONObject;

public class Beacons{
    private final String SUBTAG = "Beacons ";
    private static Beacons mInstance = null;
    private static Context mContext;
    private Beacons() {
    }

    public static Beacons getInstance(Context context) {
        mContext = context;
        if (null == mInstance)
            mInstance = new Beacons();
        return mInstance;
    }

    /**
     * API to notify streethawk server when device enters a beacon region.
     * Use this API if you are using third party library support for beacons
     * Please note that respective beacon's parameters (UUID major and minor) needs to be registered with Streethawk server
     *
     * @param UUID     UUID of the beacon detected
     * @param major    major number of beacon detected
     * @param minor    minor number of beacon detected
     * @param distance distance of beacon from device
     * @return 0 for successful reporting
     * -1 for error, Check logcat messages for details
     */
    public int shEnterBeacon(String UUID, int major, int minor, double distance) {
        if (null == mContext)
            return -1;
        if (null == UUID) {
            Log.e(Util.TAG, "UUID cannot be null");
            return -1;
        }
        BeaconDB beaconDB = BeaconDB.getInstance(mContext);
        String beaconId = beaconDB.getBeaconId(UUID, major, minor);
        if (null == beaconId) {
            Log.w(Util.TAG, "Beacon not found in streethawk list. Beacon is ignored from reporting");
            return -1;
        } else {
            final JSONObject object = new JSONObject();
            try {
                object.put(beaconId,distance);
            } catch (JSONException e) {
                e.printStackTrace();
                return -1;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Logging manager = Logging.getLoggingInstance(mContext);
                    Bundle params = new Bundle();
                    params.putString(Util.CODE, Integer.toString(Constants.CODE_IBEACON_UPDATES));
                    params.putString(Util.SHMESSAGE_ID, null);
                    params.putString("json", object.toString());
                    manager.addLogsForSending(params);
                }
            }).start();
        }
        return -1;
    }

    /**
     * API to notify streethawk server when device exits a beacon region.
     * Use this API if you are using third party library support for beacons
     *
     * @param UUID  UUID of the beacon detected
     * @param major major number of beacon detected
     * @param minor distance of beacon from device
     * @return 0 for successful reporting
     * -1 for error, Check logcat messages for details
     */
    public int shExitBeacon(String UUID, int major, int minor) {
        if (null == mContext)
            return -1;
        if (null == UUID) {
            Log.e(Util.TAG, "UUID cannot be null");
            return -1;
        }
        BeaconDB beaconDB = BeaconDB.getInstance(mContext);
        String beaconId = beaconDB.getBeaconId(UUID, major, minor);
        if (null == beaconId) {
            Log.w(Util.TAG, "Beacon not found in streethawk list. Beacon is ignored from reporting");
            return -1;
        } else {
            final JSONObject object = new JSONObject();
            try {
                object.put(beaconId,-1);
            } catch (JSONException e) {
                e.printStackTrace();
                return -1;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Logging manager = Logging.getLoggingInstance(mContext);
                    Bundle params = new Bundle();
                    params.putString(Util.CODE, Integer.toString(Constants.CODE_IBEACON_UPDATES));
                    params.putString(Util.SHMESSAGE_ID, null);
                    params.putString("json", object.toString());
                    manager.addLogsForSending(params);
                }
            }).start();
        }
        return -1;
    }


    private boolean isDeviceSupportBLE() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
            return false;
        return true;
    }

    /**
     * Use stopBeaconService API to stop scanning of beacons
     * @return
     */
    public void stopBeaconMonitoring(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(Constants.BEACON_MONITOR_FLAG,false);
        e.commit();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Intent beaconService = new Intent(mContext, BeaconServiceKK.class);
            Log.e(Util.TAG,SUBTAG +" Stopping beacon monitoring service");
            mContext.stopService(beaconService);
            new BeaconServiceKK(mContext).unRegisterBeconTask(mContext);
        } else {
            Intent beaconService = new Intent(mContext, BeaconServiceL.class);
            Log.e(Util.TAG,SUBTAG +" Stopping beacon monitoring service");
            mContext.stopService(beaconService);
            new BeaconServiceKK(mContext).unRegisterBeconTask(mContext);
        }
    }

    /**
     * Use startBeaconService API to start scanning of beacons
     * @return
     */
    public boolean startBeaconService() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(Constants.BEACON_MONITOR_FLAG, true);
        e.commit();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.e(Util.TAG,SUBTAG +" Starting beacon monitoring service");
            Intent beaconService = new Intent(mContext, BeaconServiceKK.class);
            mContext.startService(beaconService);
            BeaconServiceKK beaconServiceObj = new BeaconServiceKK(mContext);
            beaconServiceObj.initiateFirstScan();
        } else {
            Log.e(Util.TAG,SUBTAG +" Starting beacon monitoring service");
            Intent beaconService = new Intent(mContext, BeaconServiceL.class);
            mContext.startService(beaconService);
            BeaconServiceL beaconServiceObj = new BeaconServiceL(mContext);
            beaconServiceObj.initiateFirstScan();
        }
        return true;
    }

    /**
     * StartBeaconMonitoring by application
     * @param app
     * @return
     */
    public boolean startBeaconMonitoring(Application app){
        if (!isDeviceSupportBLE()) {
            Log.e(Util.TAG, SUBTAG + "Device doesn't support BLE");
            return false;
        }
        return startBeaconService();
    }
}
