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

interface Constants {
    String PARENT_GEOFENCE_ID           = "parentgeofenceid";
    String IS_GEOFENCE_ENABLE           = "shgeofenceenable";
    int CODE_USER_DISABLES_LOCATION     = 8112;
    int CODE_GEOFENCE_UPDATES           = 22;       // place in geofence modules

    String KEY_UPDATE_INTERVAL_BG       = "KEY_UPDATE_INTERVAL_BG";
    String KEY_UPDATE_DISTANCE_BG       = "KEY_UPDATE_DISTANCE_BG";
    String KEY_UPDATE_INTERVAL_FG       = "KEY_UPDATE_INTERVAL_FG";
    String KEY_UPDATE_DISTANCE_FG       = "KEY_UPDATE_DISTANCE_FG";
    String BROADCAST_APP_STATUS_CHK     = "com.streethawk.intent.action.gcm.STREETHAWK_LOCATIONS";
    String SHTASKTIME                   = "shTaskTime";
    String SHPACKAGENAME                = "shpackagename";
    String LOCAL_TIME                   = "created_local_time";
    String SHLATTITUDE                  = "latitude";
    String SHLONGITUDE                  = "longitude";
    String SHLOCATION_FLAG              = "locationFlag";
    int CODE_LOCATION_UPDATES           = 20;
    int WORK_HOME_TIME_INTERVAL         = 3480000;  // 58 minutes so that we dont have sync problems
    int CODE_PERIODIC_LOCATION_UPDATE   = 19;
    String PERMISSION_MSG               = "msg";
    String PERMISSION_BOOL              = "permission_bool";

}
