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
package com.streethawk.library.core;

class Constants {

    /*Push notification codes*/

    public static final int CODE_PUSH_ACK                       = 8202; // Added in v2
    public static final int CODE_PUSH_RESULT                    = 8203; // Added in v2

    /*Location codes*/
    public static final int CODE_LOCATION_UPDATES 				= 20;  // place is separate location modules
    public static final int CODE_IBEACON_UPDATES 				= 21;  // place in beacon modules
    public static final int CODE_GEOFENCE_UPDATES				= 22;  // place in geofence modules

    /*Logging codes*/
    public static final int CODE_APP_OPENED_FROM_BG 			= 8103;
    public static final int CODE_APP_TO_BG 						= 8104;
    public static final int CODE_SESSIONS                       = 8105; //Added for v2
    public static final int CODE_USER_ENTER_ACTIVITY 			= 8108;
    public static final int CODE_USER_LEAVE_ACTIVITY 			= 8109;
    public static final int CODE_COMPLETE_ACTIVITY              = 8110;
    public static final int CODE_USER_DISABLES_LOCATION 		= 8112;
    public static final int CODE_DEVICE_TIMEZONE 				= 8050;
    public static final int CODE_HEARTBEAT						= 8051;
    public static final int CODE_CLIENT_UPGRADE                 = 8052; // Added in v2
    public static final int CODE_INCREMENT_TAG                  = 8997; // Added in v2
    public static final int CODE_DELETE_CUSTOM_TAG 				= 8998;
    public static final int CODE_UPDATE_CUSTOM_TAG 				= 8999;

    /*Logging constants*/
    public static final String CODE             = "code";
    public static final String INSTALL_ID       = "installid";
    public static final String SHTIMEZONE       = "shtimezone";

    /*Boolean states*/
    public static final String SHINSTALL_STATE = "install_state";

    /*Params*/
    public static final String KEY_HOST             = "shKeyHost"; // Read in UriUtil.java
    public static final String JSON_VALUE           = "value";
    public static final String SHMESSAGE_ID         = "message_id";            // assign null if sending manually
    public static final String LOCAL_TIME           = "created_local_time";
    public static final String TYPE_STRING          = "string";
    public static final String TYPE_NUMERIC         = "numeric";
    public static final String TYPE_DATETIME        = "datetime";
    public static final String SH_KEY               = "key";
    public static final String SHPAUSETIME          = "shPauseTime";
    public static final String SHSAVEDTIME          = "shSavedTime";
    public static final String PAUSE_MINUTES        = "pause_minutes";
    public static final String KEY_STREETHAWK       = "shKeyStreethawk";
    public static final String SESSIONIDCNT         = "session_id_cnt";
    public static final String SHAPPVERSION         = "shappversion";
    public static final String SHADVERTISEMENTID    = "advertisementid";
    public static final String SHLOGPRIORITY        = "logpriority";
    public static final String SHPGPREVPAGE         = "shpgprevpage";
    public static final String OPERATING_SYSTEM     = "operating_system";
    public static final String INSTALLID            = "installid";
    public static final String SHLOG_ALLOWED        = "shlogallowed";
    public static final String SHPACKAGENAME        = "shpackagename";
    public static final String SHTASKTIME           = "shTaskTime";


    //Release type
    public static final String DISTRIBUTION_REFERENCE_LIB = "Reference_Library";
    public static final String DISTRIBUTION_AAR = "aar";

    //Platform types
    public static final int PLATFORM_ANDROID_NATIVE = 0;
    public static final int PLATFORM_PHONEGAP       = 1;
    public static final int PLATFORM_TITANIUM       = 2;
    public static final int PLATFORM_XAMARIN        = 3;
    public static final int PLATFORM_UNITY          = 4;

    /*Library version*/
    public final static String SELLING_ARGUMENT    = "1";
    public final static String FEATURE_NUMBER      = "7";
    public final static String BUGFIX_NUMBER       = "2";

    /*Release parameters */
    public static final String SHLIBRARY_VERSION = SELLING_ARGUMENT + "." + FEATURE_NUMBER + "." + BUGFIX_NUMBER;
    public static final int RELEASE_PLATFORM = PLATFORM_ANDROID_NATIVE;
    public static final String DISTRIBUTION_TYPE = DISTRIBUTION_AAR;
    public static final String BROADCAST_APP_STATUS_CHK = "com.streethawk.intent.action.gcm.STREETHAWK_APP_STATUS_CHK";
}
