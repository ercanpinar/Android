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

package com.streethawk.library.push;


interface Constants {
    String MSGID                    = "msgid";
    String CODE                     = "code";
    String TITLE                    = "title";
    String MSG                      = "msg";
    String DATA                     = "data";
    String BADGE                    = "badge";
    String FROMBG                   = "fromBG";
    String PENDING_DIALOG           = "pendingDialog";
    String PICKER_PENDING           = "pickerPendingDialog";
    String SHUSECUSTOMDIALOG_FLAG   = "usecustomdialogFlag";
    String SHPACKAGENAME            = "shpackagename";
    String SHOW_PENDING_DIALOG      = "shShowPendingDialog";
    String SHFORCEPUSHTOBG          = "shforcepushtobg";            // if true, push message will always be displayed in notification bar
    String PHONEGAP_URL             = "shphonegapurl";
    String SHGCM_SENDER_KEY_APP     = "shgcmsenderkeyapp";
    //String PROPERTY_REG_ID          = "registration_id";
    String KEY_REGISTEREDREQUESTED  = "isPushRegistered";
    String PROPERTY_APP_VERSION     = "app_version";
    String IS_PUSH_FIRST_RUN        = "ispushfirstrun";
    String PUSH_ACCESS_DATA         = "pushaccessData";
    String SHPAUSETIME              = "shPauseTime";
    String SHSAVEDTIME              = "shSavedTime";
    String PAUSE_MINUTES            = "pause_minutes";

    String RES_ID_FOR_BG_NOTFICATION_LARGE    = "residforbgnotificationlarge";
    String RES_ID_FOR_BG_NOTFICATION_SMALL    = "residforbgnotificationsmall";


    //Codes

    int CODE_ERROR                      = -1;
    int CODE_OPEN_URL                   = 8000;
    int CODE_REQUEST_THE_APP_STATUS     = 8003;
    int CODE_LAUNCH_ACTIVITY            = 8004;
    int CODE_RATE_APP                   = 8005;
    int CODE_USER_REGISTRATION_SCREEN   = 8006;
    int CODE_USER_LOGIN_SCREEN          = 8007;
    int CODE_UPDATE_APP                 = 8008;
    int CODE_CALL_TELEPHONE_NUMBER      = 8009;
    int CODE_SIMPLE_PROMPT              = 8010;
    int CODE_FEEDBACK                   = 8011;
    int CODE_IBEACON                    = 8012;
    int CODE_ACCEPT_PUSHMSG             = 8013;
    int CODE_ENABLE_LOCATION            = 8014;
    int CODE_CUSTOM_JSON_FROM_SERVER    = 8049;
    int CODE_CUSTOM_ACTIONS             = 8100;
    int CODE_FEED_ACK                   = 8200;
    int CODE_FEED_RESULT                = 8201;
    int CODE_PUSH_ACK                   = 8202; // Added in v2
    int CODE_PUSH_RESULT                = 8203; // Added in v2
    int CODE_GHOST_PUSH                 = 8042;



    //Results
    int STREETHAWK_ACCEPTED     = 1;
    int STREETHAWK_DECLINED     = -1;
    int STREETHAWK_POSTPONED    = 0;

    String BROADCAST_STREETHAWK_ACCEPTED = "com.streethawk.intent.action.gcm.STREETHAWK_ACCEPTED";
    String BROADCAST_STREETHAWK_DECLINED = "com.streethawk.intent.action.gcm.STREETHAWK_DECLINED";
    String BROADCAST_STREETHAWK_POSTPONED = "com.streethawk.intent.action.gcm.STREETHAWK_POSTPONED";
    String BROADCAST_SH_PUSH_NOTIFICATION = "com.streethawk.intent.action.pushnotification";


    String REGISTER_FRIENDLY_NAME = "register";
    String LOGIN_FRIENDLY_NAME = "login";

    int PLATFORM_ANDROID_NATIVE = 0;
    int PLATFORM_PHONEGAP       = 1;
    int PLATFORM_TITANIUM       = 2;
    int PLATFORM_XAMARIN        = 3;
    int PLATFORM_UNITY          = 4;
    String SHGCM_FLAG = "gcmFlag";

    String PUSH_INSTALLID		= "installid";
    String PUSH_CODE 			= "c";
    String PUSH_MSG_ID 			= "i";
    String PUSH_DATA 			= "d";
    String PUSH_TITLE_LENGTH    = "l";
    String PUSH_SHOW_DIALOG     = "n";
    String PUSH_MSG 		    = "m";
    String PUSH_TITLE 			= "t";
    String PUSH_PORTION 		= "p";
    String PUSH_ORIENTATION		= "o";
    String PUSH_SPEED 			= "s";
    String PUSH_APS 			= "aps";
    String PUSH_ALERT 			= "alert";
    String PUSH_BADGE 			= "badge";
    String PUSH_SOUND 			= "sound";

    //Interactive push
    String PUSH_CONTENT_AVAILABLE = "content-available";
    String PUSH_CATEGORY          = "category";

    /*Start custom buttons*/
    String PUSH_BTN_TITLE       = "t";
    String PUSH_BTN_ICON        = "i";
    String PUSH_BUTTON1         = "b1";
    String PUSH_BUTTON2         = "b2";
    String PUSH_BUTTON3         = "b3";
    /*End custom buttons*/

}
