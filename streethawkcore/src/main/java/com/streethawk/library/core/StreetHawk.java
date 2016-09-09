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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public enum StreetHawk implements Constants{
    INSTANCE;

    private Activity currentActivity;
    /**
     * use sh_email to tag user's email address
     */
    public static final String sh_email = "sh_email";                   //tagString
    /**
     * use sh_phone to tag user's phone number
     */
    public static final String sh_phone = "sh_phone";                   //tagString
    /**
     * use sh_gender to tag gender of user
     */
    public static final String sh_gender = "sh_gender";                //tagString
    /**
     * use sh_cuid to tag a unique identifier of the install
     */
    public static final String sh_cuid = "sh_cuid";                    //tagString
    /**
     * use sh_first_name to tag user's first name for sending personalised message
     */
    public static final String sh_first_name = "sh_first_name";            //tagString
    /**
     * use sh_last_name to tag user's last name for sending personalised message
     */
    public static final String sh_last_name = "sh_last_name";            //tagString
    /**
     * use sh_date_of_birth for tagging user's date of birth
     */
    public static final String sh_date_of_birth = "sh_date_of_birth";        //tagDatetime
    /**
     * use sh_utc_offset to notify user's UTC offset in minutes
     */
    public static final String sh_utc_offset = "sh_utc_offset";


    private String mAppKey = null;
    private ISHEventObserver mEventObserver=null;


    /**
     * Set current running activity
     *
     * @param currentActivity
     */

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    /**
     * Returns instance of current activity
     *
     * @return activity
     */
    public Activity getCurrentActivity() {
        return this.currentActivity;
    }

    /**
     * API to get version of StreetHawk Library
     *
     * @return version
     */
    public String getSHLibraryVersion() {
        return SHLIBRARY_VERSION;
    }

    /**
     * @deprecated
     * Set device's Advertisement ID into StreetHawk SDK
     * @param context application contect
     * @param advertisementId advertisement id
     */

    public void setAdvertisementId(final Context context, final String advertisementId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                String old = sharedPreferences.getString(SHADVERTISEMENTID, null);
                if (null != old) {
                    if (old.equals(advertisementId))
                        return;
                }
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putString(SHADVERTISEMENTID, advertisementId);
                e.commit();
                if (null != getInstallId(context)) {
                    if(null!=advertisementId) {
                        tagString(KEY_ADV_ID, advertisementId);
                    }
                }
            }
        }).start();
    }

    /**
     * Store locations to be included in logging
     */
    private void StoreLocationForLogging(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Class noParams[] = {};
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class growth = null;
                try {
                    growth = Class.forName("com.streethawk.library.geofence.StreetHawkLocationService");
                    Method growthMethod = growth.getMethod("getInstance", paramContext);
                    Object obj = growthMethod.invoke(null,mContext);
                    if (null != obj) {
                        Method addGrowthModule = growth.getDeclaredMethod("forceStoreCurrentLocation", paramContext);
                        addGrowthModule.invoke(obj,mContext);
                    }
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Geofence module is not  not present while storing locations for logging");
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }

            }
        }).start();
    }

    protected void activityResumedByService(final Activity activity) {
        if (null == activity) {
            Log.e(Util.TAG, SUBTAG + "Returning from activityResumedByService as activity is null");
            return;
        }
        StoreLocationForLogging();
        setCurrentActivity(activity);
        notifyAppStateResumed(activity);
        StreetHawkCoreService obj = new StreetHawkCoreService();
        obj.forceFlushCrashReportToServer(activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String advId = AdvertisingId.getAdvertisingIdInfo(mContext).getId();
                    setAdvertisementId(mContext, advId);
                } catch (Exception e) {
                    Log.e(Util.TAG, "Excpetion in setting adv id");
                }
            }
        }).start();
    }

    protected void activityPausedByService(final Activity activity) {
        setCurrentActivity(activity);
        if (null == activity) {
            Log.e(Util.TAG, SUBTAG + "Returning from activityResumedByService as activity is null");
            return;
        }
        setCurrentActivity(activity);
        notifyAppStatePaused(activity);
    }

    private static final String SUBTAG = "StreetHawk ";
    private static Context mContext;
    private boolean activityLifecycleRegistered = false;


    /**
     * Tag user's languge here. Please note that SDK can automatically detect device's default language and hence use this API only if
     * there is a difference in the application and device's language setttings
     * @param language
     */
    public void tagUserLanguage(String language){
        if(null==language) {
            Log.e(Util.TAG,"Laungauge is null in tagUserLanguage returning..");
            return;
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String userLocale = sharedPreferences.getString(USER_LOCALE , null);
        if(null==userLocale) {
            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putString(USER_LOCALE,language);
            e.commit();
            tagString("sh_language", language);
        }else{
            if(!userLocale.equals(language)){
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putString(USER_LOCALE,language);
                e.commit();
            }
        }
    }

    /**
     * Display badge count on app icon. Note that this function is not supported on all devices.
     * @param context
     * @param BadgeCount
     */
    public static void displayBadge(Context context, int BadgeCount) {
        String deviceManufacturer = Build.MANUFACTURER;
        String packgaeName = context.getPackageName().toString();
        ComponentName componentName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent();
        String launcherActivityName = componentName.getClassName();

        if (deviceManufacturer.equalsIgnoreCase("GENYMOTION")) {
            String modelStr = "(" + Build.MANUFACTURER + ") " + Build.BRAND + " " + Build.MODEL;
            if (modelStr.toLowerCase().contains("samsung"))
                deviceManufacturer = "SAMSUNG";
            else if (modelStr.toLowerCase().contains("sony"))
                deviceManufacturer = "SONY";
            else if (modelStr.toLowerCase().contains("htc"))
                deviceManufacturer = "HTC";
            else if (modelStr.toLowerCase().contains("lge"))
                deviceManufacturer = "lge";
            else
                deviceManufacturer = "UNKNOWN";
        }
        if (deviceManufacturer.equalsIgnoreCase("SONY")) {
            Intent badgeIntent = new Intent();
            try {
                badgeIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
                badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherActivityName);
                badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
                badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", Integer.toString(BadgeCount));
                badgeIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", packgaeName);
                context.sendBroadcast(badgeIntent);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else if (deviceManufacturer.equalsIgnoreCase("SAMSUNG")) {
            try {
                ContentResolver samsungCR = context.getContentResolver();
                Uri badgeURI = Uri.parse("content://com.sec.badge/apps");
                ContentValues samsungCV = new ContentValues();
                samsungCV.put("package", packgaeName);
                samsungCV.put("class", launcherActivityName);
                samsungCV.put("badgecount", Integer.valueOf(BadgeCount));
                String str = "package=? AND class=?";
                String[] arrayOfString = new String[2];
                arrayOfString[0] = packgaeName;
                arrayOfString[1] = launcherActivityName;
                int update = samsungCR.update(badgeURI, samsungCV, str, arrayOfString);
                if (update == 0) {
                    samsungCR.insert(badgeURI, samsungCV);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else if (deviceManufacturer.equalsIgnoreCase("HTC")) {
            try {
                Intent htcIntent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
                htcIntent.putExtra("packagename", packgaeName);
                htcIntent.putExtra("count", BadgeCount);
                context.sendBroadcast(htcIntent);
                Intent notificationIntent = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
                ComponentName htcComponentName = new ComponentName(context, StreetHawk.INSTANCE.getCurrentActivity().toString());
                notificationIntent.putExtra("com.htc.launcher.extra.COMPONENT", htcComponentName.flattenToShortString());
                notificationIntent.putExtra("com.htc.launcher.extra.COUNT", 99);
                context.sendBroadcast(notificationIntent);
            } catch (Exception e) {

                e.printStackTrace();

                return;
            }
        } else if (deviceManufacturer.equalsIgnoreCase("LGE")) {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", BadgeCount);
            intent.putExtra("badge_count_package_name", packgaeName);
            intent.putExtra("badge_count_class_name", launcherActivityName);
            context.sendBroadcast(intent);
        } else {

            Log.i(Util.TAG, "Badges are not supported for " + Build.MANUFACTURER + " " + Build.MODEL);

            return;
        }
    }


    /**
     * Function to set App key if not set in AndroidManifest.xaml
     * @param app_key
     */
    public void setAppKey(String app_key) {
        mAppKey = app_key;
    }

    /**
     * Function to initialise and Start StreetHawk SDK
     *
     * @param application
     */
    public void init(final Application application) {
        if (null == application) {
            Log.e(Util.TAG, SUBTAG + "StreetHawk is not initialised as application is null in init");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setActivityLifecycleCallbacks(application);
        }
        mContext = application.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Util.getInstallId(mContext) == null) {
                    if (null != mAppKey) {
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        e.putString(Util.SHAPP_KEY, mAppKey);
                        e.commit();
                    }
                    if(null!=mEventObserver){
                        Install.getInstance(mContext).registerInstallEventObserver(mEventObserver);
                    }
                    Install.getInstance(mContext).registerInstall();
                }
                Intent coreService = new Intent(mContext, StreetHawkCoreService.class);
                coreService.putExtra("fromInit", true);
                mContext.startService(coreService);
                Logging.getLoggingInstance(mContext).saveActivityNames();
                Util.setSHDebugFlag(mContext,true);
            }
        }).start();
    }

    /**
     * Call notifyEnterView when a view or fragment is visible to user
     *
     * @param viewName
     */
    public void notifyViewEnter(final String viewName) {
        if (null == viewName)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle extras = new Bundle();
                extras.putString(CODE, Integer.toString(CODE_USER_ENTER_ACTIVITY));
                extras.putString(SHMESSAGE_ID, null);
                extras.putString(TYPE_STRING, viewName);
                final Logging shManager = Logging.getLoggingInstance(mContext);
                shManager.addLogsForSending(extras);
            }
        }).start();

    }

    /**
     * Send simple feedback to server
     * @param title
     * @param message
     */
    public void sendSimpleFeedback(final String title, final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logging.getLoggingInstance(mContext).sendFeedbackToServer(title, message, 0);
            }
        }).start();
    }

    /**
     * Call notifyEnterView when user exits a view or fragment
     *
     * @param viewName
     */
    public void notifyViewExit(final String viewName) {
        if (null == viewName)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle extras = new Bundle();
                extras.putString(CODE, Integer.toString(CODE_USER_LEAVE_ACTIVITY));
                extras.putString(SHMESSAGE_ID, null);
                extras.putString(TYPE_STRING, viewName);
                final Logging shManager = Logging.getLoggingInstance(mContext);
                shManager.addLogsForSending(extras);
            }
        }).start();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setActivityLifecycleCallbacks(final Application application) {
        if (!activityLifecycleRegistered) {
            application.registerActivityLifecycleCallbacks(StreetHawkActivityLifecycleCallback.getInstance());
            activityLifecycleRegistered = true;
        }
        //Register all module's activityLifecycle
        Class noParams[] = {};
        Class[] appParams = new Class[1];
        appParams[0] = Application.class;
        Class growth = null;
        try {
            growth = Class.forName("com.streethawk.library.growth.Growth");
            Method growthMethod = growth.getMethod("setActivityLifecycleCallbacks", appParams);
            growthMethod.invoke(null, application);
        } catch (ClassNotFoundException e1) {
            Log.w(Util.TAG, "Growth module is not  not present");
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }


    /**
     * API to tag cuid of the install
     *
     * @param value unique identifier of the user (Example email address)
     * @return
     */
    public boolean tagCuid(String value) {
        return tagString("sh_cuid", value);
    }

    /**
     * API to numeric tag a profile
     *
     * @param key key
     * @param numeric_value value
     * @return true for success false if failed
     */
    public boolean tagNumeric(String key, double numeric_value) {
        if (null == key) {
            Log.e(Util.TAG, SUBTAG + "Key cannot be null in tagNumeric ");
            return false;
        }
        if (key.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "key cannot be empty in tagNumeric ");
            return false;
        }
        if (null == mContext) {
            Log.e(Util.TAG, SUBTAG + "Call to tagNumeric should be after streethawk's init");
            return false;
        }
        if (key.length() >= 30) {
            key = key.substring(0, 29);
            Log.w(Util.TAG, SUBTAG + "Key should be less than 30 chars. Modifying key to " + key);

        }
        final String checkKey = key;
        final double checkValue = numeric_value;
        boolean notifyResult = false;
        ExecutorService executorservice = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executorservice.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                Bundle extras = new Bundle();
                extras.putString(CODE, Integer.toString(CODE_UPDATE_CUSTOM_TAG));
                extras.putString(SHMESSAGE_ID, null);
                extras.putString(SHMESSAGE_ID, null);
                extras.putString(SH_KEY, checkKey);
                extras.putString(TYPE_NUMERIC, Double.toString(checkValue));
                Logging manager = Logging.getLoggingInstance(mContext);
                return manager.addLogsForSending(extras);
            }
        });
        try {
            notifyResult = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifyResult;

    }

    /**
     * API to string tag a profile
     *
     * @param key
     * @param value
     * @return true for success false if failed
     */
    public boolean tagString(String key, String value) {
        if (null == key) {
            Log.e(Util.TAG, SUBTAG + "Key cannot be null in tagString ");
            return false;
        }
        if (null == value) {
            Log.e(Util.TAG, SUBTAG + "value cannot null in tagString ");
            return false;
        }
        if (key.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "key cannot be empty in tagString ");
            return false;
        }
        if (value.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "value cannot be empty in tagString ");
            return false;
        }
        if (null == mContext) {
            Log.e(Util.TAG, SUBTAG + "tagString should be called after init");
            return false;
        }
        String STREETHAWK_WARNING_INVALID_PHONENUMBER = "Please provide phonenumber in following format " +
                "+<country code><national destination code(optional)<subscriber number>" +
                "Example +61XXXXXXXXX";

        if (StreetHawk.sh_phone.equals(key)) {
            char first = value.charAt(0);
            if (!(first == '+')) {
                Log.w(Util.TAG, SUBTAG + STREETHAWK_WARNING_INVALID_PHONENUMBER);
                return false;
            }
            try {
                Long.parseLong(value.substring(1));
            } catch (NumberFormatException exception) {

                Log.w(Util.TAG, SUBTAG + STREETHAWK_WARNING_INVALID_PHONENUMBER);
                return false;
            }
        }
        if (key.length() >= 30) {
            key = key.substring(0, 29);
            Log.w(Util.TAG, SUBTAG + "Key should be less than 30 chars. Modifying key to " + key);

        }
        final String checkKey = key;
        final String checkValue = value;
        boolean notifyResult = false;
        ExecutorService executorservice = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executorservice.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                Bundle extras = new Bundle();
                extras.putString(CODE, Integer.toString(CODE_UPDATE_CUSTOM_TAG));
                extras.putString(SHMESSAGE_ID, null);
                extras.putString(SH_KEY, checkKey);
                extras.putString(TYPE_STRING, checkValue);
                Logging manager = Logging.getLoggingInstance(mContext);
                return manager.addLogsForSending(extras);
            }
        });
        try {
            notifyResult = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifyResult;
    }

    /**
     * Function validates date entered by user
     *
     * @param dateTime
     * @return
     */
    private boolean isValidateDateTime(String dateTime) {
        if (null == dateTime)
            return false;
        String date;
        String month;
        String year;

        int yearInt;
        int dateInt;
        int monthInt;

        int indexFirstDash = dateTime.indexOf('-');
        if (indexFirstDash == -1) {
            Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd: hh:mm:ss");
            return false;
        }
        year = dateTime.substring(0, indexFirstDash);
        if (year.length() != 4) {
            Log.e(Util.TAG, "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
            return false;
        }
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            Log.e(Util.TAG, SUBTAG + "Invalid value for year");
            return false;
        }
        if (yearInt < 1900) {
            Log.e(Util.TAG, SUBTAG + "Value of year should be less than 1900");
            return false;
        }

        dateTime = dateTime.replace(year + '-', "");
        indexFirstDash = dateTime.indexOf('-');
        if (indexFirstDash == -1) {
            Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
            return false;
        }
        month = dateTime.substring(0, indexFirstDash);
        if (month.length() != 2) {
            Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
            return false;
        }
        try {
            monthInt = Integer.parseInt(month);
        } catch (NumberFormatException e) {
            Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
            return false;
        }
        if (monthInt > 12) {
            Log.e(Util.TAG, SUBTAG + "Month's value cannot be greater than 12");
            return false;
        }
        dateTime = dateTime.replace(month + '-', "");
        indexFirstDash = dateTime.indexOf(" ");
        if (indexFirstDash != -1) {
            date = dateTime.substring(0, indexFirstDash);
        } else {
            date = dateTime;
        }
        try {
            dateInt = Integer.parseInt(date);
        } catch (NumberFormatException e) {
            Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
            return false;
        }
        if (date.length() != 2) {
            Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
            return false;
        }
        switch (monthInt) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (dateInt > 31) {
                    Log.e(Util.TAG, SUBTAG + "Invalid date");
                    return false;
                }
                break;
            case 2:
                if (dateInt > 29) {
                    Log.e(Util.TAG, SUBTAG + "Invalid date");
                    return false;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (dateInt > 30) {
                    Log.e(Util.TAG, SUBTAG + "Invalid date");
                    return false;
                }
                break;
        }
        dateTime = dateTime.replace(date, "");
        if (dateTime.length() == 0) {
            return true;
        } else {
            //Validate for time now

            String hrs;
            String minutes;
            String seconds;

            int hrsInt;
            int minutesInt;
            int secondsInt;

            indexFirstDash = dateTime.indexOf(':');
            if (indexFirstDash == -1) {
                Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
                return false;
            }
            hrs = dateTime.substring(1, indexFirstDash);
            if (hrs.length() != 2) {
                Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
                return false;
            }
            try {
                hrsInt = Integer.parseInt(hrs);
            } catch (NumberFormatException e) {
                Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
                return false;
            }
            if (hrsInt > 24) {
                Log.e(Util.TAG, SUBTAG + "Invalid hrs cannot be more than 24");
                return false;
            }
            dateTime = dateTime.replace(hrs + ':', "");
            indexFirstDash = dateTime.indexOf(':');
            if (indexFirstDash == -1) {
                Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
                return false;
            }
            minutes = dateTime.substring(1, indexFirstDash);
            if (minutes.length() != 2) {
                Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
                return false;
            }
            try {
                minutesInt = Integer.parseInt(minutes);
            } catch (NumberFormatException e) {
                Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
                return false;
            }
            if (minutesInt > 60) {
                Log.e(Util.TAG, SUBTAG + "Invalid minutes cannot be more than 60");
                return false;
            }

            dateTime = dateTime.replace(minutes + ':', "");
            seconds = dateTime.replace(" ", "");
            if (seconds.length() != 2) {
                Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
                return false;
            }
            try {
                secondsInt = Integer.parseInt(seconds);
            } catch (NumberFormatException e) {
                Log.e(Util.TAG, SUBTAG + "Invalid date format,expected format = yyyy-mm-dd hh:mm:ss");
                return false;
            }
            if (secondsInt > 60) {
                Log.e(Util.TAG, SUBTAG + "Invalid seconds cannot be more than 60");
                return false;
            }
        }
        return true;
    }


    private void notifyAppStateResumed(final Activity activity) {
        final Context context = activity.getApplicationContext();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                AppActivityTracking appActivityTracking = AppActivityTracking.getActivityTrackingInstance();
                List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                if (!tasks.isEmpty()) {
                    ComponentName topActivity = tasks.get(0).topActivity;
                    if (topActivity.getPackageName().equals(context.getPackageName())) {
                        // indicateds we are still within app
                        SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                        String currentActivity = null;
                        currentActivity = activity.getPackageName() + '.' + activity.getLocalClassName();
                        String storedActivity = prefs.getString(SHPGPREVPAGE, null);
                        if (null == storedActivity) {
                            SharedPreferences.Editor e = prefs.edit();
                            e.putString(SHPGPREVPAGE, currentActivity);
                            e.commit();
                            appActivityTracking.notifyNewActivity(activity, currentActivity, storedActivity);
                            appActivityTracking.onAppForegrounded(activity);
                        }
                        else if (currentActivity.equals(storedActivity)) {
                                appActivityTracking.notifyChangedOrientation(activity);
                            return;
                        }
                        else {
                            // transact to new activity
                            SharedPreferences.Editor e = prefs.edit();
                            e.putString(SHPGPREVPAGE, currentActivity);
                            e.apply();
                            appActivityTracking.notifyNewActivity(activity, currentActivity, storedActivity);
                        }
                    }
                }
                return;
            }
        }, 500);
    }

    private void notifyAppStatePaused(final Activity activity) {
        final Context context = activity.getApplicationContext();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                AppActivityTracking appActivityTracking = AppActivityTracking.getActivityTrackingInstance();
                List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                if (!tasks.isEmpty()) {
                    ComponentName topActivity = tasks.get(0).topActivity;
                    if (!(topActivity.getPackageName().equals(context.getPackageName()))) {
                        SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                        String storedActivity = prefs.getString(SHPGPREVPAGE, null);
                        SharedPreferences.Editor e = prefs.edit();
                        e.putString(SHPGPREVPAGE, null);
                        e.commit();
                        appActivityTracking.notifyNewActivity(activity, null, storedActivity);
                        appActivityTracking.onAppBackgrounded(activity);
                    }
                } else {
                    SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                    String storedActivity = prefs.getString(SHPGPREVPAGE, null);
                    SharedPreferences.Editor e = prefs.edit();
                    e.putString(SHPGPREVPAGE, null);
                    e.commit();
                    appActivityTracking.notifyNewActivity(activity, null, storedActivity);
                    appActivityTracking.onAppBackgrounded(activity);
                }
                return;
            }
        }, 500);
    }

    /**
     * shActivityPaused API compensates for Activitylifecycle. Call if your application is supported on API 9
     *
     * @param activity
     */
    public void shActivityPaused(Activity activity) {
        if (null == activity)
            return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            activityPausedByService(activity);
        }
    }


    /**
     * shActivityResumed API compensates for Activitylifecycle. Call if your application is supported on API 9
     *
     * @param activity
     */
    public void shActivityResumed(Activity activity) {
        if (null == activity)
            return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setCurrentActivity(activity);
            activityResumedByService(activity);
        }
    }

    /**
     * API returns app_key used by StreetHawk to identify your application
     *
     * @return app_key for server conversation.
     */
    public String getAppKey(Context context) {
        return Util.getAppKey(context);
    }


    /**
     * API to current formatted dateTime in UTC
     *
     * @return current formatted dateTime in UTC
     */
    public static String getCurrentFormattedDateTime() {
        return Util.getFormattedDateTime(System.currentTimeMillis(), true);
    }

    /**
     * Api returns UTC time for given time
     *
     * @param time
     * @return UTC time
     */
    public static String getFormattedDateTime(long time) {
        return Util.getFormattedDateTime(time, true);
    }

    /**
     * API to tag using date-time
     *
     * @param key            to be tagged
     * @param datetime_value date time value in string. Alternatively use {@link #getFormattedDateTime(long)} API
     * @return true on success false on Failure
     */
    public boolean tagDatetime(String key, String datetime_value) {
        if (null == key) {
            Log.e(Util.TAG, SUBTAG + "Key cannot be null in tagDatetime ");
            return false;
        }
        if (null == datetime_value) {
            Log.e(Util.TAG, SUBTAG + "datetime_value cannot null in tagDatetime ");
            return false;
        }
        if (key.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "key cannot be empty in tagDatetime ");
            return false;
        }
        if (datetime_value.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "datetime_value cannot be empty in tagDatetime ");
            return false;
        }
        if (null == mContext) {
            Log.e(Util.TAG, SUBTAG + "Call to tagDatetime should be after StreetHawk's init");
            return false;
        }
        if (key.length() >= 30) {
            key = key.substring(0, 29);
            Log.w(Util.TAG, SUBTAG + "Key should be less than 30 chars. Modifying key to " + key);

        }
        if (!isValidateDateTime(datetime_value)) {
            Log.e(Util.TAG, SUBTAG + "Error in tagDatetime");
            return false;
        }
        final String checkKey = key;
        final String checkValue = datetime_value;
        boolean notifyResult = false;
        ExecutorService executorservice = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executorservice.submit(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                Bundle extras = new Bundle();
                extras.putString(CODE, Integer.toString(CODE_UPDATE_CUSTOM_TAG));
                extras.putString(CODE, Integer.toString(CODE_UPDATE_CUSTOM_TAG));
                extras.putString(SHMESSAGE_ID, null);
                extras.putString(SH_KEY, checkKey);
                extras.putString(TYPE_DATETIME, checkValue);
                Logging manager = Logging.getLoggingInstance(mContext);
                return manager.addLogsForSending(extras);
            }
        });
        try {
            notifyResult = result.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifyResult;

    }

    /**
     * Api to increment a tag by 1
     *
     * @param key to be incremented
     */
    public void incrementTag(String key) {
        incrementTag(key, 1);
    }

    /**
     * API to increment or decrement value of given tag by the given value
     *
     * @param key key
     * @param value value
     */
    public void incrementTag(String key, int value) {
        if (null == key) {
            Log.e(Util.TAG, SUBTAG + "Key cannot be null in incrementTag ");
            return;
        }

        if (key.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "key cannot be empty in incrementTag ");
            return;
        }
        if (key.length() >= 30) {
            key = key.substring(0, 29);
            Log.w(Util.TAG, SUBTAG + "Key should be less than 30 chars. Modifying key to " + key);
        }
        try {
            Bundle extras = new Bundle();
            Logging manager = Logging.getLoggingInstance(mContext);
            extras.putString(CODE, Integer.toString(CODE_INCREMENT_TAG));
            extras.putString(SHMESSAGE_ID, null);
            extras.putString(SH_KEY, key);
            extras.putString(TYPE_NUMERIC, Integer.toString(value));
            manager.addLogsForSending(extras);
        }catch(NumberFormatException e){
            e.printStackTrace();
        }
    }

    /**
     * API to increment or decrement value of given tag by the given value
     *
     * @param key key
     * @param value value
     */
    public void incrementTag(String key, double value) {
        if (null == key) {
            Log.e(Util.TAG, SUBTAG + "Key cannot be null in incrementTag ");
            return;
        }

        if (key.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "key cannot be empty in incrementTag ");
            return;
        }
        if (key.length() >= 30) {
            key = key.substring(0, 29);
            Log.w(Util.TAG, SUBTAG + "Key should be less than 30 chars. Modifying key to " + key);
        }
        Bundle extras = new Bundle();
        try {
            Logging manager = Logging.getLoggingInstance(mContext);
            extras.putString(CODE, Integer.toString(CODE_INCREMENT_TAG));
            extras.putString(SHMESSAGE_ID, null);
            extras.putString(SH_KEY, key);
            extras.putString(TYPE_NUMERIC, Double.toString(value));
            manager.addLogsForSending(extras);
        }catch(NumberFormatException e){
            e.printStackTrace();
        }
    }


    /**
     * Use registerEventObserver to register
     * @param instance
     */
    public void registerEventObserver(ISHEventObserver instance){
        this.mEventObserver = instance;
        if(null!=mContext){
            Install.getInstance(mContext).registerInstallEventObserver(mEventObserver);
        }
    }

    /**
     * Api to delete a custom profile tag
     *
     * @param key Key
     */
    public void removeTag(String key) {
        if (null == key) {
            Log.e(Util.TAG, SUBTAG + "Key cannot be null in removeTag ");
            return;
        }

        if (key.isEmpty()) {
            Log.e(Util.TAG, SUBTAG + "key cannot be empty in removeTag ");
            return;
        }
        if (null == mContext) {
            Log.e(Util.TAG, SUBTAG + "Call to removeTag should be after streethawk's init");
            return;
        }
        if (key.length() >= 30) {
            key = key.substring(0, 29);
            Log.w(Util.TAG, SUBTAG + "Key should be less than 30 chars. Modifying key to " + key);
        }
        Bundle extras = new Bundle();
        Logging manager = Logging.getLoggingInstance(mContext);
        extras.putString(CODE, Integer.toString(CODE_DELETE_CUSTOM_TAG));
        extras.putString(SHMESSAGE_ID, null);
        extras.putString(SH_KEY, key);
        manager.addLogsForSending(extras);
    }

    /**
     * getInstallId returns StreetHawk's unique identifier for the given install.
     * StreetHawk discourages use of this API.
     *
     * @param context application context
     * @return installid
     */
    public String getInstallId(Context context) {
        return Util.getInstallId(context);
    }

}
