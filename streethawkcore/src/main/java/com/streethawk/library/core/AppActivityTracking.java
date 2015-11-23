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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


class AppActivityTracking implements Constants{

    private static AppActivityTracking mActivityTracking = null;
    private static final String SESSIONIDCNT = "session_id_cnt";
    private final String SESSION_TIME = "sessiontime";
    private String SESSION_START = "start";
    private String SESSION_END = "end";
    private String SESSION_LENGTH = "length";
    private String BG = "bg";
    private String SHARED_PREF_ACTIVITY = "sharedprefactivity";  // shared preference to keep a note of activity start time

    private void SaveSessionTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putLong(SESSION_TIME, System.currentTimeMillis());
        e.commit();
    }

    public void notifyChangedOrientation(final Activity activity){
        final Context context = activity.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;

                Class push = null;

                try {
                    push = Class.forName("com.streethawk.library.push.Push");
                    Method pushMethod = push.getMethod("getInstance", paramContext);
                    Object obj = pushMethod.invoke(null, context);
                    if (null != obj) {
                        Method addPushModule = push.getDeclaredMethod("notifyChangeOrientation", paramActivity);
                        addPushModule.invoke(obj,activity);
                    }
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Push module is not  not present");
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

    private long getSessionTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        Long sessionTime = sharedPreferences.getLong(SESSION_TIME, -1);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.remove(SESSION_TIME);
        e.commit();
        return sessionTime;
    }

    public void onAppForegrounded(final Activity activity){
        final Context context = activity.getApplicationContext();
        SaveSessionTime(context);
        Bundle extras = new Bundle();
        extras.putString(CODE, Integer.toString(CODE_APP_OPENED_FROM_BG));  //Sending 8103
        extras.putString(LOCAL_TIME, Util.getFormattedDateTime(System.currentTimeMillis(), false));
        final Logging shManager = Logging.getLoggingInstance(context);
        shManager.addLogsForSending(extras);
        extras.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean runInstallUpdate = false;
                SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                String storedVersion = sharedPreferences.getString(SHAPPVERSION, null);
                String currentAppVersion = Util.getAppVersionName(context);
                //String storedAdvertisementId = sharedPreferences.getString(SHADVERTISEMENTID, null);
                //String currentAdvertisementId = Util.getAdvertisingIdentifier(context);
                if (null != storedVersion) {
                    if (!storedVersion.equals(currentAppVersion)) {
                        Bundle extras = new Bundle();
                        extras.putString(CODE, Integer.toString(CODE_CLIENT_UPGRADE));
                        extras.putString(TYPE_STRING, currentAppVersion);
                        final Logging shManager = Logging.getLoggingInstance(context);
                        shManager.addLogsForSending(extras);
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        e.putString(SHAPPVERSION, currentAppVersion);
                        e.commit();
                        shManager.sendModuleList();
                        runInstallUpdate = true;
                    }

                } else {
                    // First run
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString(SHAPPVERSION, currentAppVersion);
                    e.commit();
                    shManager.sendModuleList();
                }
                /*
                if (null != storedAdvertisementId) {
                    if (!storedAdvertisementId.equals(currentAdvertisementId)) {
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        e.putString(SHADVERTISEMENTID, currentAdvertisementId);
                        e.commit();
                        runInstallUpdate = true;
                    }
                } else {
                    // First run
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString(SHADVERTISEMENTID, currentAdvertisementId);
                    e.commit();
                }
                */
                if (runInstallUpdate && (Util.getInstallId(context)!=null) ) {
                    try {
                        Install.getInstance(context).updateInstall(null,Install.INSTALL_CODE_IGNORE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;

                Class push = null;

                try {
                    push = Class.forName("com.streethawk.library.push.Push");
                    Method pushMethod = push.getMethod("getInstance", paramContext);
                    Object obj = pushMethod.invoke(null, context);
                    if (null != obj) {
                        Method addPushModule = push.getDeclaredMethod("notifyAppForegrounded", paramActivity);
                        addPushModule.invoke(obj,activity);
                    }
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Push module is not  not present");
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

    public void onAppBackgrounded(final Activity activity){
       if(null==activity)
           return;
        final Context context = activity.getApplicationContext();
        long currentTime = System.currentTimeMillis();
        Bundle extras = new Bundle();
        // Seinding 8105 before 8109 as 8105 is non priority logline
        Long startSessionTime = getSessionTime(context);
        if (-1 == startSessionTime) {
            return;
        }
        extras.putString(CODE, Integer.toString(CODE_SESSIONS));                // Sending 8105
        extras.putString(SESSION_START, Util.getFormattedDateTime(startSessionTime, true));
        extras.putString(SESSION_END, Util.getFormattedDateTime(currentTime, true));
        extras.putString(SESSION_LENGTH, Long.toString(Math.round((currentTime - startSessionTime) / 1000.0)));  // length in seconds
        final Logging shManager = Logging.getLoggingInstance(context);
        shManager.addLogsForSending(extras);
        extras.clear();
        extras.putString(CODE, Integer.toString(CODE_APP_TO_BG));                // Sending 8104
        extras.putString(LOCAL_TIME, Util.getFormattedDateTime(currentTime, false));
        shManager.addLogsForSending(extras);
        incrementSessionId(context);
    }


    public static AppActivityTracking getActivityTrackingInstance() {
        if (null == mActivityTracking) {
            mActivityTracking = new AppActivityTracking();
        }
        return mActivityTracking;
    }

    private void incrementSessionId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        int sessionId = sharedPreferences.getInt(SESSIONIDCNT, 1);
        sessionId++;
        e.putInt(SESSIONIDCNT, sessionId);
        e.commit();
    }


   private String getFriendlyNameFromclassName(Context context, final String fullyQualifiedName) {
        if (fullyQualifiedName == null)
            return null;
        if (null == context)
            return null;
        String className = new StringBuilder(fullyQualifiedName).reverse().toString();
        int indexOfPeriod = className.indexOf(".");
        if (-1 == indexOfPeriod)
            return fullyQualifiedName;
        className = className.subSequence(0, className.indexOf(".")).toString();
        className = new StringBuilder(className).reverse().toString();
        return className;
    }


    public void notifyNewActivity(Context context, String newActivity, String oldActivity) {
        if (Util.getPlatformType() == PLATFORM_ANDROID_NATIVE || Util.getPlatformType() == PLATFORM_XAMARIN) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_ACTIVITY, Context.MODE_PRIVATE);
            boolean bg = false;
            if (null != newActivity) { //send 8108
                String friendlyName = getFriendlyNameFromclassName(context.getApplicationContext(), newActivity);
                Bundle extras = new Bundle();
                extras.putString(CODE, Integer.toString(CODE_USER_ENTER_ACTIVITY));
                extras.putString(SHMESSAGE_ID, null);
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putLong(newActivity, System.currentTimeMillis());      // Store start time of this activity
                e.commit();
                extras.putString(TYPE_STRING, friendlyName);
                final Logging shManager = Logging.getLoggingInstance(context);
                shManager.addLogsForSending(extras);
            } else {
                bg = true; // indicates this is last activity before going to bg
            }
            if (null != oldActivity) { //send 8109
                String friendlyName = getFriendlyNameFromclassName(context.getApplicationContext(), oldActivity);
                Bundle extras = new Bundle();
                extras.putString(CODE, Integer.toString(CODE_USER_LEAVE_ACTIVITY));
                extras.putString(SHMESSAGE_ID, null);
                if (null == friendlyName) {
                    friendlyName = oldActivity;
                }
                extras.putString(TYPE_STRING, friendlyName);
                final Logging shManager = Logging.getLoggingInstance(context);
                shManager.addLogsForSending(extras);
                extras.clear();
                extras.putString(CODE, Integer.toString(CODE_COMPLETE_ACTIVITY));
                extras.putString(TYPE_STRING, friendlyName);
                Long storedTime = sharedPreferences.getLong(friendlyName, -1);
                extras.putString(SESSION_START, Util.getFormattedDateTime(storedTime, true));
                long currentTime = System.currentTimeMillis();
                extras.putString(SESSION_END, Util.getFormattedDateTime(currentTime, true));
                long Duration = Math.round((currentTime - storedTime) / 1000.0);
                extras.putString(SESSION_LENGTH, Long.toString(Duration));  // length in seconds
                extras.putString(BG, Boolean.toString(bg)); // true if app wnet to bg after this activity;
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.remove(friendlyName);  // remove reported shared preference
                if (bg)
                    e.clear();          // To save shared pref from growing in case app crashes
                e.commit();
                shManager.addLogsForSending(extras);
            }
        }
    }
}
