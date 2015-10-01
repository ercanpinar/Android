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
class Constants {
   public static final String BROADCAST_BEACON_SCAN_TRIGGER = "com.streethawk.intent.action.gcm.STREETHAWK_APP_BEACON_WIFI_MODE";
   public static final  int ONE_SECOND = 1000;
   public static final  int ONE_MINUTE = 60 * ONE_SECOND;
   public static final  int BLE_SCAN_INTERVAL_BG = 2 * ONE_MINUTE;         // 2 minutes
   public static final  int BLE_SCAN_INTERVAL_FG = 1 * ONE_MINUTE;         // 1 minute
   public static final String BEACON_MONITOR_FLAG = "beaconmonitorflag";

}
