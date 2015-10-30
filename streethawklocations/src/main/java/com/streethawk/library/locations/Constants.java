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
package com.streethawk.library.locations;

class Constants {

    public static final String KEY_UPDATE_INTERVAL_BG       = "KEY_UPDATE_INTERVAL_BG";
    public static final String KEY_UPDATE_DISTANCE_BG       = "KEY_UPDATE_DISTANCE_BG";
    public static final String KEY_UPDATE_INTERVAL_FG       = "KEY_UPDATE_INTERVAL_FG";
    public static final String KEY_UPDATE_DISTANCE_FG       = "KEY_UPDATE_DISTANCE_FG";
    public static final String BROADCAST_APP_STATUS_CHK     = "com.streethawk.intent.action.gcm.STREETHAWK_LOCATIONS";
    public static final String SHTASKTIME                   = "shTaskTime";
    public static final String SHPACKAGENAME                = "shpackagename";
    public static final String LOCAL_TIME                   = "created_local_time";
    public static final String SHLATTITUDE                  = "latitude";
    public static final String SHLONGITUDE                  = "longitude";
    public static final String SHLOCATION_FLAG              = "locationFlag";
    public static final int CODE_LOCATION_UPDATES           = 20;
    public static final int WORK_HOME_TIME_INTERVAL         = 3480000;  // 58 minutes so that we dont have sync problems
    public static final int CODE_PERIODIC_LOCATION_UPDATE   = 19;
    public static final String PERMISSION_MSG               = "msg";
    public static final String PERMISSION_BOOL              = "permission_bool";

}
