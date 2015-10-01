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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import com.streethawk.library.core.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by anuragkondeya on 6/08/15.
 */
public class BeaconServiceL extends BeaconServiceBase {
    private final String SUBTAG = "BeaconServiceL ";

    private Context mContext;
    public BeaconServiceL(){super();}
    BeaconServiceL(Context context){
        super(context);
        this.mContext = context;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() { super.onDestroy();}

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("NewApi")
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            int txPower = result.getScanRecord().getTxPowerLevel();
            BeaconDataParser(result.getScanRecord().getBytes(), txPower);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(Util.TAG,SUBTAG+ "LE Scan Failed: " + errorCode);
        }

    };

    @SuppressLint("NewApi")
    private ScanCallback mScanStopCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {

        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(Util.TAG,SUBTAG+ "LE Scan Failed: " + errorCode);
        }

    };

    @Override
    public void initiateFirstScan() {
        final int scanInterval = (Util.isAppBG(mContext) ? Constants.BLE_SCAN_INTERVAL_BG : Constants.BLE_SCAN_INTERVAL_FG);
        registerBeaconTask(mContext, scanInterval);
        new Thread(new Runnable() {
            @Override
            public void run() {
                scanForBeacon();
            }
        }).start();
    }

    public void forceClearBeaconList(Context context){
        super.forceClearBeaconList(context);
    }


    @SuppressLint("NewApi")
    @Override
    public void scanForBeacon() {
        if (null == mBluetoothAdapter) {
            return;
        }
        final BluetoothLeScanner bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (null == bleScanner) {
            this.stopSelf();
            return;
        }
        ArrayList<ScanFilter> scanFilterList = new ArrayList<ScanFilter>();
        BeaconDB beaconDB = BeaconDB.getInstance(mContext);
        ArrayList<String> beaconList = beaconDB.getListOfUniqueUUID();
        if (null == beaconList) {
            return;
        }
        if (beaconList.isEmpty()) {
            return;
        }
        for (Iterator iterator = beaconList.iterator(); iterator.hasNext(); ) {
            ScanFilter.Builder builder = new ScanFilter.Builder();
            String uuid = iterator.next().toString();
            // Commenting as server with validate
            //uuid =  uuid.replaceFirst( "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5" );
            try {
                builder.setServiceUuid(ParcelUuid.fromString(uuid.toLowerCase()));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            scanFilterList.add(builder.build());
        }
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        try {
            ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
                    Executors.newScheduledThreadPool(5);
            Runnable oneShotTask = new Runnable() {
                @Override
                public void run() {
                    Log.i(Util.TAG,"Stopping beacon scan");
                    bleScanner.stopScan(mScanCallback);
                    processDetectedBeacons(mContext);

                }
            };
            ScheduledFuture<?> oneShotFuture = sch.schedule(oneShotTask, BLE_ACTIVE_SCAN_PERIOD, TimeUnit.SECONDS);
            Log.i(Util.TAG,"Starting actual scanning of beacons");
            bleScanner.startScan(null, scanSettings, mScanCallback);


        } catch (Exception e) {
            e.printStackTrace();
            this.stopSelf();
        }
    }

}
