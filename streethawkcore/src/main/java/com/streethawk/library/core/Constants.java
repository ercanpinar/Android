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

interface Constants {

    /*Push notification codes*/

    int CODE_PUSH_ACK = 8202; // Added in v2
    int CODE_PUSH_RESULT = 8203; // Added in v2

    /*Location codes*/
    int CODE_LOCATION_UPDATES = 20;  // place is separate location modules
    int CODE_IBEACON_UPDATES = 21;  // place in beacon modules
    int CODE_GEOFENCE_UPDATES = 22;  // place in geofence modules

    /*Logging codes*/
    int CODE_APP_OPENED_FROM_BG = 8103;
    int CODE_APP_TO_BG = 8104;
    int CODE_SESSIONS = 8105; //Added for v2
    int CODE_USER_ENTER_ACTIVITY = 8108;
    int CODE_USER_LEAVE_ACTIVITY = 8109;
    int CODE_COMPLETE_ACTIVITY = 8110;
    int CODE_USER_DISABLES_LOCATION = 8112;
    int CODE_DEVICE_TIMEZONE = 8050;
    int CODE_HEARTBEAT = 8051;
    int CODE_CLIENT_UPGRADE = 8052; // Added in v2
    int CODE_INCREMENT_TAG = 8997; // Added in v2
    int CODE_DELETE_CUSTOM_TAG = 8998;
    int CODE_UPDATE_CUSTOM_TAG = 8999;

    /*Logging constants*/
    String CODE = "code";
    String INSTALL_ID = "installid";
    String SHTIMEZONE = "shtimezone";

    /*Boolean states*/
    String SHINSTALL_STATE = "install_state";

    /*Params*/
    String KEY_GROWTH_HOST = "shKeyHostGrowth";
    String KEY_HOST = "shKeyHost"; // Read in UriUtil.java
    String JSON_VALUE = "value";
    String SHMESSAGE_ID = "message_id";            // assign null if sending manually
    String LOCAL_TIME = "created_local_time";
    String TYPE_STRING = "string";
    String TYPE_NUMERIC = "numeric";
    String TYPE_DATETIME = "datetime";
    String SH_KEY = "key";
    String KEY_STREETHAWK = "shKeyStreethawk";
    String SESSIONIDCNT = "session_id_cnt";
    String SHAPPVERSION = "shappversion";
    String SHADVERTISEMENTID = "advertisementid";
    String SHLOGPRIORITY = "logpriority";
    String SHDISABLELOG = "disablelog";
    String SHPGPREVPAGE = "shpgprevpage";
    String OPERATING_SYSTEM = "operating_system";
    String INSTALLID = "installid";
    String SHLOG_ALLOWED = "shlogallowed";
    String SHPACKAGENAME = "shpackagename";
    String SHTASKTIME = "shTaskTime";
    String FEEDBACK_TITLE = "shStaggedFBTitle";
    String FEEDBACK_CONTENT = "shStaggedFBContent";
    String FEEDBACK_TYPE = "shfeedbacktype";
    String DEVICE_LOCALE = "shdevicelocale";
    String USER_LOCALE = "shuserlocale";
    String SHLATTITUDE = "latitude";
    String SHLONGITUDE = "longitude";


    int CODE_FEED_ACK = 8200;
    int CODE_FEED_RESULT = 8201;

    String ERROR_CODE = "code";


    //Release type
    String DISTRIBUTION_REFERENCE_LIB = "Reference_Library";
    String DISTRIBUTION_AAR = "aar";

    //Platform types
    int PLATFORM_ANDROID_NATIVE = 0;
    int PLATFORM_PHONEGAP = 1;
    int PLATFORM_TITANIUM = 2;
    int PLATFORM_XAMARIN = 3;
    int PLATFORM_UNITY = 4;

    /*Library version*/
    String SELLING_ARGUMENT = "1";
    String FEATURE_NUMBER = "8";
    String BUGFIX_NUMBER = "9";

    /*Release parameters */
    String SHLIBRARY_VERSION = SELLING_ARGUMENT + "." + FEATURE_NUMBER + "." + BUGFIX_NUMBER;
    String DISTRIBUTION_TYPE = DISTRIBUTION_AAR;
    String BROADCAST_APP_STATUS_CHK = "com.streethawk.intent.action.gcm.STREETHAWK_APP_STATUS_CHK";
    String KEY_ADV_ID = "sh_advertising_identifier";


    String ID = "id";
    String PARENT = "parent";


    String SUPERTAG_NAME = "name";
    String SUPERTAG_TYPE = "type";
    String SUPERTAG_VALUE = "value";

}
