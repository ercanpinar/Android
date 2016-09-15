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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Base class for service to detect beacons in proxmity
 */
public abstract class BeaconServiceBase extends Service implements Constants{
    public BeaconServiceBase() {
    }

    protected final int BLE_ACTIVE_SCAN_PERIOD = 10;
    private Map<String, ?> mBeaconMap = null;
    protected BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Context mContext;
    private final String SUBTAG = "BeaconServiceBase ";

    //TODO: Migrate hashmap logic to db as we can't send -1 when app is killed or in other words send beacon match all the time if app is killed in a beacon region.

    private static HashMap<String, Double> mVisibleBeacons = new HashMap<String, Double>();     // lists beacons which are visible in this scan
    private static HashMap<String, Double> mOldBeacons = new HashMap<String, Double>();         // lists beacons which were seen in previous iterarion

    public BeaconServiceBase(Context context) {
        this.mContext = context;
    }

    public abstract void initiateFirstScan();

    public abstract void scanForBeacon();

    public IBinder onBind(Intent intent){return null;}

    public void onDestroy() {
        super.onDestroy();
    }

    private static INotifyBeaconTransition mINotifyBeaconStatus=null;
    final static ArrayList<BeaconData> mDetectedBeaconList = new ArrayList<BeaconData>();


    public static void registerForBeaconStatus(INotifyBeaconTransition observer){
        mINotifyBeaconStatus = observer;
    }

    /**
     * Returns list of beacons detected
     * @return
     */
    public static ArrayList<BeaconData> getBeaconList(){
        return mDetectedBeaconList;
    }



    /**
     * Notify server when all beacons are invisible due to turning off bluetooth
     * @param context
     */
    protected void forceClearBeaconList(Context context){
        if (mOldBeacons == null) {
            return;
        }
        if (mVisibleBeacons != null) {
            mVisibleBeacons.clear();
        }
        JSONObject object = null;
        object = new JSONObject();
        for (Map.Entry<String, Double> entry : mOldBeacons.entrySet()) {
            try {
                object.put(entry.getKey(), -1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Logging manager = Logging.getLoggingInstance(context);
        Bundle params = new Bundle();
        if (object.length() == 0) {
            mOldBeacons.clear();
            return;
        }
        params.putInt(Util.CODE, CODE_IBEACON_UPDATES);
        params.putString(Util.SHMESSAGE_ID, null);
        params.putString("json", object.toString());
        manager.addLogsForSending(params);
        mOldBeacons.clear();
    }


    /**
     * Check if BLE permission is granted.
     * @return
     */
    private boolean isPermissionAvailable() {
        int bt = mContext.checkCallingOrSelfPermission("android.Manifest.permission.BLUETOOTH");
        int bt_admin = mContext.checkCallingOrSelfPermission("android.Manifest.permission.BLUETOOTH_ADMIN");
        if ((bt == PackageManager.PERMISSION_DENIED) && bt_admin == PackageManager.PERMISSION_DENIED) {
            Log.w(Util.TAG, SUBTAG + " App is missing Bluetooth permissions in AndroidManifest.xml");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Function to check if BT is enable
     * @param context
     * @return true if enable otherwise returns false
     */
    public boolean isBTEnable(Context context) {
        /*
        if (!isPermissionAvailable()) {
            return false;
        } else */{
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (null == bluetoothAdapter)
                return false;
            else {
                return bluetoothAdapter.isEnabled();
            }
        }
    }

    /**
     * Update visible beacon list adds list of beacons which are no longer visible
     */
    protected void updateVisibleBeaconList() {
        Set<String> oldBeacons = mOldBeacons.keySet();
        ArrayList<String> exitBeacon = new ArrayList<String>();
        // Step 1 tmp beacons which are not visible from old list
        for (String s : oldBeacons) {
            if (null == mVisibleBeacons.get(s))
                exitBeacon.add(s);
        }
        //Step 2 remove remove beacons which are already present in old list
        //       as they have been already reported
        for (String s : oldBeacons) {
            mVisibleBeacons.remove(s);
        }
        //Step 3 Copy if newly added beacons to old beacon list to monitor for future scan
        if (!mVisibleBeacons.isEmpty()) {
            Iterator iter = mVisibleBeacons.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry pair = (Map.Entry) iter.next();
                mOldBeacons.put(pair.getKey().toString(), 10.0);  // this is a dummy distance it wont matter as we will not report it to server
            }
        }
        //Step 4 // Add beacons which are no longer visible, Remove them from old beacon list
        for (String s : exitBeacon) {
            mVisibleBeacons.put(s, -1.0);
            mOldBeacons.remove(s);
        }
    }

    private void updateOldscanList() {
        mOldBeacons.putAll(mVisibleBeacons);
    }

    protected Boolean isBeaconListEmpty() {
        if (mBeaconMap == null)
            return false;
        return mBeaconMap.isEmpty();
    }

    /**
     * Unregister periodic task which triggers beacon scanning
     * @param context
     */
    protected void unRegisterBeconTask(Context context) {
        Intent mBeaconTask = new Intent(context, SHBeaconModuleBC.class);
        mBeaconTask.setAction(BROADCAST_BEACON_SCAN_TRIGGER);
        PendingIntent appStatusIntent = PendingIntent.getBroadcast(context, 0, mBeaconTask, PendingIntent.FLAG_UPDATE_CURRENT);
        if (null != appStatusIntent) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(appStatusIntent);
            appStatusIntent.cancel();
        }
    }

    /**
     * Register periodic task which triggers beacon scanning.
     * @param context
     * @param scanInterval
     */
    protected void registerBeaconTask(final Context context, int scanInterval) {
        Intent schedule = new Intent();
        schedule.putExtra(Util.INSTALL_ID, Util.getInstallId(context));
        schedule.setAction(BROADCAST_BEACON_SCAN_TRIGGER);
        boolean taskRegistered = (PendingIntent.getBroadcast(context, 0,
                schedule,
                PendingIntent.FLAG_NO_CREATE) != null);
        if (taskRegistered) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent appStatusIntent = PendingIntent.getBroadcast(context, 0, schedule, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), scanInterval, appStatusIntent);
    }

    /**
     * Parse for data from ble scanned packet
     * @param scanRecord
     * @param txPower
     */
    protected void BeaconDataParser(byte[] scanRecord, int txPower) {
        int n = 0;
        /*
        for (byte i : scanRecord) {
            Log.e("Anurag",n+" scan "+String.format("%02X",i));
            n++;
        }
        */
        byte[] Number = new byte[2];
        byte referenceRssi;                 // collected from device
        String UUID = "";
        int intMajorNo = 0;
        int intMinorNo = 0;
        double distance = 0.0;
        for (int i = 9; i < 25; i++) {
            UUID = UUID.concat(String.format("%02X", scanRecord[i]));
        }
        for (int i = 25; i < 27; i++) {
            Number[i - 25] = scanRecord[i];
        }
        ByteBuffer buffer = ByteBuffer.wrap(Number);
        buffer.order(ByteOrder.BIG_ENDIAN);
        intMajorNo = buffer.getShort();
        intMajorNo = (intMajorNo << 16) >>> 16;
        for (int i = 27; i < 29; i++) {
            Number[i - 27] = scanRecord[i];
        }
        buffer = ByteBuffer.wrap(Number);
        buffer.order(ByteOrder.BIG_ENDIAN);
        intMinorNo = buffer.getShort();
        intMinorNo = (intMinorNo << 16) >>> 16;
        referenceRssi = scanRecord[29];
        distance = beaconDistance(txPower, referenceRssi);
        BeaconDB beaconDB = BeaconDB.getInstance(mContext);
        UUID = UUID.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
        String beaconId = beaconDB.getBeaconId(UUID, intMajorNo, intMinorNo);
        if (null != beaconId) {
            mVisibleBeacons.put(beaconId, (double) distance);
        }
        BeaconData beaconDetected = new BeaconData();
        beaconDetected.setUUID(UUID);
        beaconDetected.setMajorNumber(intMajorNo);
        beaconDetected.setMinorNumber(intMinorNo);
        beaconDetected.setDistance(distance);
        beaconDetected.setBeaconId("UnKnown");
        mDetectedBeaconList.add(beaconDetected);
        beaconDetected = null;
    }

    /**
     * Compute approx distance of beacon from device
     * @param txPower
     * @param rssi
     * @return distance in meters
     */
    private double beaconDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return 0.0;
        }
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 0.5)
            ratio = 0.5;
        return ratio;
    }

    /**
     * Function notifies SH server about beacons detected in vicinity
     * @param context
     */
    protected void processDetectedBeacons(Context context) {
        if (mVisibleBeacons == null) {  // No beacons detected here
            return;
        } else {
            if (mOldBeacons.isEmpty()) {
                updateOldscanList();
            } else {
                updateVisibleBeaconList();
            }
            if (mVisibleBeacons != null) {
                if (mVisibleBeacons.isEmpty()) {
                    return;
                }
            }
            JSONObject object = null;
            object = new JSONObject();
            for (Map.Entry<String, Double> entry : mVisibleBeacons.entrySet()) {
                try {
                    object.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Logging manager = Logging.getLoggingInstance(context);
            Bundle params = new Bundle();
            if (object.length() == 0) {
                mVisibleBeacons.clear();
                return;
            }
            if (object.toString().equals("{ }")) {
                return;
            }
            params.putInt(Util.CODE,CODE_IBEACON_UPDATES);
            params.putString(Util.SHMESSAGE_ID, null);
            params.putString("json", object.toString());
            Log.i(Util.TAG, SUBTAG + "Notifying beacons detected" + object.toString());
            manager.addLogsForSending(params);
            notifyObserver();
            mVisibleBeacons.clear();                // clear list after logging
        }
    }

    private void notifyObserver() {
        if(null!=mINotifyBeaconStatus){
           if(null!=mDetectedBeaconList){
               if(!mDetectedBeaconList.isEmpty()){
                   mINotifyBeaconStatus.notifyBeaconDetected();
               }
           }
        }
    }
}
