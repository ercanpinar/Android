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
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.streethawk.library.core.Util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Beacon service for devices running Android KK
 */
public class BeaconServiceKK extends BeaconServiceBase {

    private Context mContext;
    public BeaconServiceKK(){super();}
    BeaconServiceKK(Context context){
        super(context);
        this.mContext = context;
    }

    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    };
    @Override
    public void onDestroy() { super.onDestroy();}

    public void forceClearBeaconList(Context context){
        super.forceClearBeaconList(context);
    }


    @Override
    public  void initiateFirstScan(){
        final int scanInterval = (Util.isAppBG(mContext) ? BLE_SCAN_INTERVAL_BG : BLE_SCAN_INTERVAL_FG);
        registerBeaconTask(mContext, scanInterval);
        new Thread(new Runnable() {
            @Override
            public void run() {
                scanForBeacon();
            }
        }).start();
    }

    @SuppressLint("NewApi")
    @Override
    public void scanForBeacon() {
        try {
            if (isBeaconListEmpty())
                return;
            if (mBluetoothAdapter == null)
                return;
            ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
                    Executors.newScheduledThreadPool(5);
            Runnable oneShotTask = new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    processDetectedBeacons(mContext);
                }
            };
            ScheduledFuture<?> oneShotFuture = sch.schedule(oneShotTask, BLE_ACTIVE_SCAN_PERIOD, TimeUnit.SECONDS);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }catch(Exception e){
            e.printStackTrace();
            this.stopSelf();
        }
    }

    @SuppressLint("NewApi")
    private LeScanCallback mLeScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            BeaconDataParser(scanRecord, rssi);
        }
    };
}
