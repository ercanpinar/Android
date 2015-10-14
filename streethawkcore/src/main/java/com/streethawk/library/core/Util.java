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

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Util {


    public static final String TAG = "StreetHawk";

    /*Constants*/
    public static final String SUBTAG = "Util ";
    public static final String UNKNOWN = "Unknwon";
    public static final String MARKET_GOOGLE_PLAY = "com.android.vending";
    public static final String DEV_INSTALL = "Dev install";
    public static final String APP_STATUS = "app_status";
    public static final String APP_STATUS_ANSWER = "app_status_answer";
    public static final String JSON_VALUE = "value";
    public static final String MSGID = "msgid";
    public static final String CODE = "code";
    public static final String SHMESSAGE_ID = "message_id";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_NUMERIC = "numeric";
    public static final String TYPE_DATETIME = "datetime";
    private static final String ACCESS_DATA = "access_data";
    public static final String SHGCMREGISTERED = "shgcmregistered";

    public static final String INSTALL_ID = "installid";

    /*Params*/
    public static final String SHAPP_KEY = "app_key";


    /*Shared preferences*/
    public static final String SHSHARED_PREF_PERM = "shstoreperm";      // stores data associated with install permanently

    public static final String BROADCAST_SH_APP_STATUS_NOTIFICATION = "com.streethawk.intent.action.APP_STATUS_NOTIFICATION";


    /**
     * Function returns network status of the device
     *
     * @param context
     * @return true if device is connected to network, false otherwise
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager)
            return false;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (null == networkInfo)
            return false;
        else
            return true;
    }

    /**
     * Function to get AppIcon
     *
     * @param context
     * @return resid of appicon
     */
    public static int getAppIcon(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Function to get name of the application
     *
     * @param context
     * @return name of the application
     */
    public static String getAppName(Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != appInfo)
                return String.valueOf(pm.getApplicationLabel(appInfo));
            else {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL getFeedbackUrl(Context context) {
        try {
            return new URL(Logging.buildUri(context, LoggingBase.ApiMethod.USER_FEEDBACK, null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static URL getGeofenceUrl(Context context) {
        try {
            return new URL(Logging.buildUri(context, LoggingBase.ApiMethod.FETCH_GEOFENCE_TREE, null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL getBeaconUrl(Context context) {
        try {
            return new URL(Logging.buildUri(context, LoggingBase.ApiMethod.FETCH_IBEACON_LIST, null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static URL getFeedUrl(Context context){
        try {
            return new URL(Logging.buildUri(context, LoggingBase.ApiMethod.FETCH_FEED_ITEMS, null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL getCrashReportingUrl(Context context) {
        try {
            return new URL(Logging.buildUri(context, LoggingBase.ApiMethod.INSTALL_REPORT_CRASH, null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if an application has given permission
     *
     * @param context
     * @param permission
     * @return 0: if permission is granted, -1 if permission is denied
     */
    public static int isPermissionAvailable(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission);
    }


    /**
     * Function to query for StreetHawk Library function
     *
     * @return returns library function
     */
    public static String getLibraryVersion() {
        return Constants.SHLIBRARY_VERSION;
    }


    /**
     * Get the StreetHawk cloud ID for this device (phone, tablet etc). It is a
     * unique ID and is assigned when initially starting the application for the
     * first time.
     *
     * @param context
     * @return id is a string that is provided by the StreetHawk cloud.
     */
    public static String getInstallId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String id = prefs.getString(Constants.INSTALL_ID, null);
        return id;
    }

    public static final String getDistributionType() {
        return Constants.DISTRIBUTION_TYPE;
    }


    /**
     * Function return app_key as defined in application.
     *
     * @param context
     * @return appkey if defined in Manifest/API, else returns null
     */
    public static String getAppKey(Context context) {
        final String METADATA_APP_KEY = "streethawk_app_key";
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String tmpAppKey = sharedPreferences.getString(SHAPP_KEY, null);
        if (null != tmpAppKey) {
            return tmpAppKey;
        }
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            tmpAppKey = bundle.getString(METADATA_APP_KEY);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, SUBTAG + "Exception reading app_key");
            e.printStackTrace();
        }
        if (tmpAppKey != null && (!tmpAppKey.isEmpty())) {
            return tmpAppKey.replace(" ", "");
        } else {
            Log.e(TAG, SUBTAG + "No App key specified");
            return null;
        }
    }

    /**
     * Function to get version name of application
     *
     * @param context
     * @return version name
     */
    public static String getAppVersionName(Context context) {
        try {
            String app_version = null;
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            app_version = packageInfo.versionName;
            if (null == app_version)
                Log.e(TAG, "Application's version name is missing in AndroidManifest.xml");
            if (app_version.isEmpty())
                Log.e(TAG, "Application's version name is empty in AndroidManifest.xml");
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Exception in getAppVersionName");
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Function returns local time of device
     *
     * @param time  System.currentTimeMillis(),false for current time
     * @param isUTC set to true if time is in utc
     * @return
     */
    public static final String getFormattedDateTime(long time, boolean isUTC) {
        if (-1 == time) {
            time = System.currentTimeMillis(); // This is to avoid 404 in 8105 (happens when an install is updated)
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (isUTC)
            dateFormat.setTimeZone(TimeZone.getTimeZone("utc"));
        String dateTime = dateFormat.format(time);
        return dateTime.toString();
    }

    /**
     * Function checks if application is backgrounded
     *
     * @param context
     * @return true if application is in background
     */
    public static boolean isAppBG(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return false; //indicates activity belongs to same same package
            }
            return false;
        } else
            return true;
    }

    /**
     * Function returns carrierName
     *
     * @return
     */
    public static String getCarrierName(Context context) {
        if (context != null) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null) {
                String carrierName = manager.getNetworkOperatorName();
                return carrierName;
            }
        }
        return null;
    }

    /**
     * Function to get screen resolution of the device
     *
     * @param context
     * @return
     */
    public static String getScreenResolution(Context context) {
        if (null == context) {
            Log.e(TAG, SUBTAG + "Context is null in getScreenResolution. returning");
            return null;
        }
        StringBuilder result = new StringBuilder();
        if (context != null) {
            final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                result.append(display.getWidth()).append('x').append(display.getHeight());
            } else {
                try {
                    Point size = new Point();
                    display.getRealSize(size);
                    result.append(size.x).append('x').append(size.y);
                } catch (Exception e) {
                    return "";
                }
            }
            return result.toString();
        }
        return null;
    }

    /**
     * Function to get name of development platform
     *
     * @return name of development platform
     */
    public static String getPlatformName() {
        switch (getPlatformType()) {
            case Constants.PLATFORM_ANDROID_NATIVE:
                return "native";
            case Constants.PLATFORM_XAMARIN:
                return "xamarin";
            case Constants.PLATFORM_TITANIUM:
                return "titanium";
            case Constants.PLATFORM_PHONEGAP:
                return "phonegap";
            case Constants.PLATFORM_UNITY:
                return "unity";
            default:
                return "native";
        }
    }

    /**
     * Function to get numeric type for development platform, where type is
     * ANDROID_NATIVE = 0;
     * PHONEGAP       = 1;
     * TITANIUM       = 2;
     * XAMARIN        = 3;
     * UNITY          = 4;
     *
     * @return integer representating platform type for development platform.
     */
    public static int getPlatformType() {
        return Constants.RELEASE_PLATFORM;
    }


    /**
     * Function return name of the app which installed the application (Say google play services)
     * It wont work with other play stores (amazon) as they just download app apk and install it. System still considers it as dev_install
     *
     * @param context
     * @return
     */
    public static boolean isAppInstalledFromPlayStore(Context context) {
        String market = getMarketName(context);
        if (market == DEV_INSTALL)
            return false;
        if (market.equals(MARKET_GOOGLE_PLAY))
            return true;
        return true; // Changing to true to support custom market place
    }

    /**
     * returns name of market place which installed the app
     *
     * @param context
     * @return
     */
    public static String getMarketName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        if (packageName == null) {
            return UNKNOWN;
        }
        String marketName = packageManager.getInstallerPackageName(packageName);
        if (null != marketName) {
            if (marketName.isEmpty()) {
                marketName = DEV_INSTALL;
            }
        } else {
            marketName = DEV_INSTALL;
        }
        return marketName;
    }

    /**
     * Function returns version code
     *
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        try {

            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, SUBTAG + "Exception in getAppVersion");
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Function returns advertising id
     *
     * @param context
     * @return
     */

    public static String getAdvertisingIdentifier(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.SHADVERTISEMENTID,null);
    }

    /**
     * Formats data in http post call
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            String key      = entry.getKey();
            String value    = entry.getValue();
            if(null!=key) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                if(null!=value) {
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }else{
                    result.append(URLEncoder.encode("", "UTF-8"));
                }
            }
        }
        return result.toString();
    }
    /**
     * Function to get StreetHawk state
     *
     * @param context
     * @return true is streethawk is enable, else false
     */
    public static boolean getStreethawkState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        boolean current_state = sharedPreferences.getBoolean(Constants.KEY_STREETHAWK, true);
        return current_state;
    }

    /**
     * Function to get session id
     *
     * @param context
     * @return session id as string
     */
    public static String getSessionId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        int sessionId = sharedPreferences.getInt(Constants.SESSIONIDCNT, 1);
        return Integer.toString(sessionId);
    }

    /**
     * Api returns timezone offset in minutes
     *
     * @return timezone offset in minutes
     */
    public static int getTimeZoneOffsetInMinutes() {
        Calendar c2 = Calendar.getInstance();
        c2.getTimeZone();
        Date date = new Date();
        boolean timezone = c2.getTimeZone().inDaylightTime(date);
        if (timezone)
            return ((c2.getTimeZone().getRawOffset() / (1000 * 60)) + (c2.getTimeZone().getDSTSavings() / (1000 * 60)));
        else
            return ((c2.getTimeZone().getRawOffset() / (1000 * 60)));
    }

    public static void updateAccessData(Context context, String registrationId){
        Install.getInstance(context).updateInstall(ACCESS_DATA, registrationId, Install.INSTALL_CODE_PUSH_TOKEN);
    }

}
