package com.streethawk.base;

/**
 * Constants used by StreetHawk modules
 */
public interface Constants {

    int PLATFORM_ANDROID_NATIVE = 0;
    int PLATFORM_PHONEGAP       = 1;
    int PLATFORM_TITANIUM       = 2;
    int PLATFORM_XAMARIN        = 3;
    int PLATFORM_UNITY          = 4;

    String DISTRIBUTION_REFERENCE_LIB   = "Reference_Library";
    String DISTRIBUTION_AAR             = "aar";

    /*Release variables*/

    String SH_LIBRARY_VERSION = "1.8.2";
    int RELEASE_PLATFORM = PLATFORM_ANDROID_NATIVE;

    /*Shared preferences*/
    public static final String SHSHARED_PREF_PERM       = "shstoreperm";      // stores data associated with install permanently
    public static final String SHSHARED_PREF_FRNDLST    = "shstorefrndlist";  // Stores names of activity in an application


    //Params
    String SHLOG_ALLOWED        = "shlogallowed";
    String KEY_HOST             = "shKeyHost";
    String JSON_VALUE           = "value";

    /*Logging constants*/
    String CODE             = "code";
    String INSTALL_ID       = "installid";
    String INSTALLID        = "installid";
    String SHTIMEZONE       = "shtimezone";
    String OPERATING_SYSTEM = "operating_system";
    String TAG              = "StreetHawk";

    /*Logging codes*/
    int CODE_APP_OPENED_FROM_BG 			= 8103;
    int CODE_APP_TO_BG 						= 8104;
    int CODE_SESSIONS                       = 8105; //Added for v2
    int CODE_USER_ENTER_ACTIVITY 			= 8108;
    int CODE_USER_LEAVE_ACTIVITY 			= 8109;
    int CODE_COMPLETE_ACTIVITY              = 8110;
    int CODE_USER_DISABLES_LOCATION 		= 8112;
    int CODE_DEVICE_TIMEZONE 				= 8050;
    int CODE_HEARTBEAT						= 8051;
    int CODE_CLIENT_UPGRADE                 = 8052; // Added in v2
    int CODE_INCREMENT_TAG                  = 8997; // Added in v2
    int CODE_DELETE_CUSTOM_TAG 				= 8998;
    int CODE_UPDATE_CUSTOM_TAG 				= 8999;



}
