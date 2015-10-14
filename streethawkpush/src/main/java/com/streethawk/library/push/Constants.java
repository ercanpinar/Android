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


class Constants {
    public static final String MSGID                    = "msgid";
    public static final String CODE                     = "code";
    public static final String TITLE                    = "title";
    public static final String MSG                      = "msg";
    public static final String DATA                     = "data";
    public static final String BADGE                    = "badge";
    public static final String FROMBG                   = "fromBG";
    public static final String PENDING_DIALOG           = "pendingDialog";
    public static final String PICKER_PENDING           = "pickerPendingDialog";
    public static final String SHUSECUSTOMDIALOG_FLAG   = "usecustomdialogFlag";
    public static final String SHPACKAGENAME            = "shpackagename";
    public static final String SHSHARED_PREF_FRNDLST    = "shstorefrndlist";
    public static final String SHOW_PENDING_DIALOG      = "shShowPendingDialog";
    public static final String SHFORCEPUSHTOBG          = "shforcepushtobg";            // if true, push message will always be displayed in notification bar
    public static final String PHONEGAP_URL             = "shphonegapurl";
    public static final String SHGCM_SENDER_KEY_APP     = "shgcmsenderkeyapp";
    //public static final String PROPERTY_REG_ID          = "registration_id";
    public static final String KEY_REGISTEREDREQUESTED  = "isPushRegistered";
    public static final String PROPERTY_APP_VERSION     = "app_version";
    public static final String IS_PUSH_FIRST_RUN        = "ispushfirstrun";
    public static final String PUSH_ACCESS_DATA         = "pushaccessData";


    //Results
    public static final int STREETHAWK_ACCEPTED = 1;
    public static final int STREETHAWK_DECLINED = -1;
    public static final int STREETHAWK_POSTPONED = 0;

    public static final String BROADCAST_STREETHAWK_ACCEPTED = "com.streethawk.intent.action.gcm.STREETHAWK_ACCEPTED";
    public static final String BROADCAST_STREETHAWK_DECLINED = "com.streethawk.intent.action.gcm.STREETHAWK_DECLINED";
    public static final String BROADCAST_STREETHAWK_POSTPONED = "com.streethawk.intent.action.gcm.STREETHAWK_POSTPONED";
    public static final String BROADCAST_SH_PUSH_NOTIFICATION = "com.streethawk.intent.action.pushnotification";


    public static final String REGISTER_FRIENDLY_NAME = "register";
    public static final String LOGIN_FRIENDLY_NAME = "login";

    public static final int PLATFORM_ANDROID_NATIVE = 0;
    public static final int PLATFORM_PHONEGAP       = 1;
    public static final int PLATFORM_TITANIUM       = 2;
    public static final int PLATFORM_XAMARIN        = 3;
    public static final int PLATFORM_UNITY          = 4;
    public static final String SHGCM_FLAG = "gcmFlag";

    public static final String PUSH_INSTALLID		= "installid";
    public static final String PUSH_CODE 			= "c";
    public static final String PUSH_MSG_ID 			= "i";
    public static final String PUSH_DATA 			= "d";
    public static final String PUSH_TITLE_LENGTH    = "l";
    public static final String PUSH_SHOW_DIALOG     = "n";
    public static final String PUSH_MSG 		    = "m";
    public static final String PUSH_TITLE 			= "t";
    public static final String PUSH_PORTION 		= "p";
    public static final String PUSH_ORIENTATION		= "o";
    public static final String PUSH_SPEED 			= "s";
    public static final String PUSH_APS 			= "aps";
    public static final String PUSH_ALERT 			= "alert";
    public static final String PUSH_BADGE 			= "badge";
    public static final String PUSH_SOUND 			= "sound";


}
