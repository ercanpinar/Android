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

/**
 * String constants
 */
interface Constants {

    String TAG = "StreetHawk";
    String SUBTAG = "Beacon ";
    String BROADCAST_BEACON_SCAN_TRIGGER = "com.streethawk.intent.action.gcm.STREETHAWK_APP_BEACON_WIFI_MODE";
    int ONE_SECOND = 1000;
    int ONE_MINUTE = 60 * ONE_SECOND;
    int BLE_SCAN_INTERVAL_BG = 2 * ONE_MINUTE;         // 2 minutes
    int BLE_SCAN_INTERVAL_FG = 1 * ONE_MINUTE;         // 1 minute
    String BEACON_MONITOR_FLAG = "beaconmonitorflag";
    int CODE_IBEACON_UPDATES = 21;

    //Platform types
    int PLATFORM_ANDROID_NATIVE = 0;
    int PLATFORM_PHONEGAP       = 1;
    int PLATFORM_TITANIUM       = 2;
    int PLATFORM_XAMARIN        = 3;
    int PLATFORM_UNITY          = 4;


}
