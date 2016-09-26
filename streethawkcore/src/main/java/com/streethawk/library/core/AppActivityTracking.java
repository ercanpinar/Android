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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;


class AppActivityTracking implements Constants {

    private static AppActivityTracking mActivityTracking = null;
    private static final String SESSIONIDCNT = "session_id_cnt";
    private final String SESSION_TIME = "sessiontime";
    private String SESSION_START = "start";
    private String SESSION_END = "end";
    private String SESSION_LENGTH = "length";
    private String BG = "bg";
    private String SHARED_PREF_ACTIVITY = "sharedprefactivity";  // shared preference to keep a note of activity start time
    private JSONArray mWidgetList = null;
    private ArrayList<WidgetObject> mWidgetListForSaving;

    private void SaveSessionTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putLong(SESSION_TIME, System.currentTimeMillis());
        e.commit();
    }

    public void notifyChangedOrientation(final Activity activity) {
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
                        addPushModule.invoke(obj, activity);
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

        /*Notify orientation change to feeds module*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                Class noParams[] = {};
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;
                Class push = null;
                try {
                    push = Class.forName("com.streethawk.library.feeds.TrigerActivityTracker");
                    Method pushMethod = push.getMethod("getInstance", noParams);
                    Object obj = pushMethod.invoke(null);
                    if (null != obj) {
                        Method addPushModule = push.getDeclaredMethod("onOrientationChange", paramActivity);
                        addPushModule.invoke(obj, activity);
                    }
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Feed module is not present");
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

    /**
     * Send core'smessage to other module
     *
     * @param context
     * @param obj
     */
    private void SendCoreMsg(Context context, final JSONObject obj) {
        // Send broadcast to notify version update
        Intent coremsg = new Intent();
        coremsg.setAction(Util.BROADCAST_MSG_FROM_CORE);
        if (null != obj) {
            coremsg.putExtra(Util.MSG_FROM_CORE, obj.toString());
            context.sendBroadcast(coremsg);
        }
    }

    private void notifyAppForegroundedToChildModules(final Activity activity) {
        //Notify foreground to push module
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
                    Object obj = pushMethod.invoke(null, activity.getApplicationContext());
                    if (null != obj) {
                        Method addPushModule = push.getDeclaredMethod("notifyAppForegrounded", paramActivity);
                        addPushModule.invoke(obj, activity);
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
        //Notify foreground to feed modules
        new Thread(new Runnable() {
            @Override
            public void run() {
                Class noParams[] = {};
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;

                Class push = null;

                try {
                    push = Class.forName("com.streethawk.library.feeds.TrigerActivityTracker");
                    Method pushMethod = push.getMethod("getInstance", noParams);
                    Object obj = pushMethod.invoke(null);
                    if (null != obj) {
                        Method addPushModule = push.getDeclaredMethod("onApplicationForegronded", paramActivity);
                        addPushModule.invoke(obj, activity);
                    }
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Feed module is not present");
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


    public void notifyAppBackgroundedToChildModules(final Activity activity) {
        //Notify foreground to feed modules
        new Thread(new Runnable() {
            @Override
            public void run() {
                Class noParams[] = {};
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;
                Class push = null;
                try {
                    push = Class.forName("com.streethawk.library.feeds.TrigerActivityTracker");
                    Method pushMethod = push.getMethod("getInstance", noParams);
                    Object obj = pushMethod.invoke(null);
                    if (null != obj) {
                        Method addPushModule = push.getDeclaredMethod("onApplicationBackgrounded", paramActivity);
                        addPushModule.invoke(obj, activity);
                    }
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Feed module is not present");
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {


            //TODO

            @Override
            public void run() {
                Class noParams[] = {};
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;

                Class push = null;

                try {
                    push = Class.forName("streethawk.com.streethawkauthor.TrigerActivityTracker");
                    Method pushMethod = push.getMethod("getInstance", noParams);
                    Object obj = pushMethod.invoke(null);
                    if (null != obj) {
                        Method addPushModule = push.getDeclaredMethod("onApplicationBackgrounded", paramActivity);
                        addPushModule.invoke(obj, activity);
                    }
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Feed module is not present");
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Class noParams[] = {};
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;
                try {
                    Class cls = Class.forName("streethawk.com.streethawkauthor.ActivityTracker");
                    Object obj = cls.newInstance();
                    Method method = cls.getMethod("onApplicationBackgrounded", paramActivity);
                    method.invoke(obj,activity);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void notifyEnteringNewActivityToChildModules(final Activity activity) {
        //Notify foreground to feed modules
        new Thread(new Runnable() {
            @Override
            public void run() {
                Class noParams[] = {};
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Class push = null;

                try {
                    push = Class.forName("com.streethawk.library.feeds.TrigerActivityTracker");
                    Method pushMethod = push.getMethod("getInstance", noParams);
                    Object obj = pushMethod.invoke(null);
                    if (null != obj) {
                        Method addPushModule = push.getDeclaredMethod("onEnteringNewActivity", paramActivity);
                        addPushModule.invoke(obj, activity);
                    }
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Feed module is not present");
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Class noParams[] = {};
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;
                try {
                    Class cls = Class.forName("streethawk.com.streethawkauthor.ActivityTracker");
                    Object obj = cls.newInstance();
                    Method method = cls.getMethod("onEnteringNewActivity", paramActivity);
                    method.invoke(obj,activity);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void notifyLeavingNewActivityToChildModules(final Activity activity) {
        //Notify foreground to feed modules
        new Thread(new Runnable() {
            @Override
            public void run() {
                Class noParams[] = {};
                Class[] paramContext = new Class[1];
                paramContext[0] = Context.class;
                Class[] paramActivity = new Class[1];
                paramActivity[0] = Activity.class;

                Class push = null;

                try {
                    push = Class.forName("com.streethawk.library.feeds.TrigerActivityTracker");
                    Method pushMethod = push.getMethod("getInstance", noParams);
                    Object obj = pushMethod.invoke(null);
                    if (null != obj) {
                        Method addPushModule = push.getDeclaredMethod("onLeavingNewActivity", paramActivity);
                        addPushModule.invoke(obj, activity);
                    }
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Feed module is not present");
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

    /**
     * Task to be done when app goes foreground
     *
     * @param activity
     */
    public void onAppForegrounded(final Activity activity) {
        final Context context = activity.getApplicationContext();
        SaveSessionTime(context);
        Bundle extras = new Bundle();
        extras.putInt(CODE,CODE_APP_OPENED_FROM_BG);  //Sending 8103
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
                String userLocale = sharedPreferences.getString(DEVICE_LOCALE, null);
                String currentAppVersion = Util.getAppVersionName(context);
                //String storedAdvertisementId = sharedPreferences.getString(SHADVERTISEMENTID, null);
                //String currentAdvertisementId = Util.getAdvertisingIdentifier(context);
                if (null != storedVersion) {
                    if (!storedVersion.equals(currentAppVersion)) {
                        Bundle extras = new Bundle();
                        extras.putInt(CODE,CODE_CLIENT_UPGRADE);
                        extras.putString(TYPE_STRING, currentAppVersion);
                        final Logging shManager = Logging.getLoggingInstance(context);
                        shManager.addLogsForSending(extras);
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        e.putString(SHAPPVERSION, currentAppVersion);
                        e.commit();
                        shManager.sendModuleList();
                        runInstallUpdate = true;
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put(Util.KEY_UPDATE_VERSION, currentAppVersion);
                            SendCoreMsg(context, obj);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                } else {
                    // First run
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString(SHAPPVERSION, currentAppVersion);
                    e.commit();
                    shManager.sendModuleList();
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put(Util.KEY_UPDATE_VERSION, currentAppVersion);
                        SendCoreMsg(context, obj);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                //check and if necessary tag user's locale every time app goes foreground
                String devUserLocale = Locale.getDefault().getDisplayLanguage();
                if (null == userLocale) {
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putString(DEVICE_LOCALE, devUserLocale);
                    e.commit();
                    StreetHawk.INSTANCE.tagString("sh_language", devUserLocale);
                } else {
                    if (!devUserLocale.equals(userLocale)) {
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        e.putString(DEVICE_LOCALE, devUserLocale);
                        e.commit();
                        StreetHawk.INSTANCE.tagString("sh_language", devUserLocale);
                    }
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
                if (runInstallUpdate && (Util.getInstallId(context) != null)) {
                    try {
                        Install.getInstance(context).updateInstall(null, Install.INSTALL_CODE_IGNORE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        notifyAppForegroundedToChildModules(activity);
    }

    public void onAppBackgrounded(final Activity activity) {
        if (null == activity)
            return;
        final Context context = activity.getApplicationContext();
        long currentTime = System.currentTimeMillis();
        Bundle extras = new Bundle();
        // Seinding 8105 before 8109 as 8105 is non priority logline
        Long startSessionTime = getSessionTime(context);
        if (-1 == startSessionTime) {
            return;
        }
        extras.putInt(CODE,CODE_SESSIONS);                // Sending 8105
        extras.putString(SESSION_START, Util.getFormattedDateTime(startSessionTime, true));
        extras.putString(SESSION_END, Util.getFormattedDateTime(currentTime, true));
        extras.putString(SESSION_LENGTH, Long.toString(Math.round((currentTime - startSessionTime) / 1000.0)));  // length in seconds
        final Logging shManager = Logging.getLoggingInstance(context);
        shManager.addLogsForSending(extras);
        extras.clear();
        extras.putInt(CODE,CODE_APP_TO_BG);                // Sending 8104
        extras.putString(LOCAL_TIME, Util.getFormattedDateTime(currentTime, false));
        shManager.addLogsForSending(extras);
        incrementSessionId(context);
        notifyAppBackgroundedToChildModules(activity);
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

    /**
     * Function returns viewname by stripping package name from it
     *
     * @param fullyQualifiedName
     * @return
     */
    private String getViewName(String fullyQualifiedName) {
        String className = new StringBuilder(fullyQualifiedName).reverse().toString();
        int indexOfPeriod = className.indexOf(".");
        if (-1 != indexOfPeriod) {
            className = className.subSequence(0, className.indexOf(".")).toString();
            className = new StringBuilder(className).reverse().toString();
            return className;
        }
        return null;
    }

    /**
     * Function returns identifier of the widget by stripping package name from it
     *
     * @param fullyQualifiedName
     * @return
     */
    private String getTextID(String fullyQualifiedName) {
        String className = new StringBuilder(fullyQualifiedName).reverse().toString();
        int indexOfPeriod = className.indexOf("di:");
        if (-1 != indexOfPeriod) {
            className = className.subSequence(0, className.indexOf("di:")).toString();
            className = new StringBuilder(className).reverse().toString();
            className = className.substring(1);
            return className;
        }
        return null;
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

    private void fillViewList(Activity activity) {
        if (null == activity) {
            return;
        } else {
            mWidgetList = new JSONArray();
            mWidgetListForSaving = new ArrayList<>();
            ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();
            fillViewList(activity, rootView);
        }
    }

    private void fillViewList(Activity activity, View view) {
      if(null==view)
          return;
        JSONObject object = new JSONObject();
        try {
            int id = view.getId();
            if (-1 != id) {
                String viewTextID = null;

                Resources res = view.getResources();
                if(null==res)
                    return;
                viewTextID = res.getResourceName(id);
                WidgetObject obj = new WidgetObject();
                String viewName = getViewName(activity.getClass().getName());
                String widgetID = getTextID(viewTextID);
                obj.setParentViewName(viewName);
                obj.setTextID(widgetID);
                obj.setResID(id);
                mWidgetListForSaving.add(obj);
                object.put(ID, viewTextID);
                mWidgetList.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                fillViewList(activity, viewGroup.getChildAt(i));

            }
        }
    }

    private void saveWidgetList(Activity activity) {
        //TODO : add activity level checks to prevent recurssive storing of activity
        fillViewList(activity);
        saveWidgetsInfo(activity);
    }

    private void saveWidgetsInfo(Activity activity) {
        if (mWidgetListForSaving.isEmpty()) {
            return;
        } else {
            WidgetDB storeGeofenceDB = new WidgetDB(activity.getApplicationContext());
            storeGeofenceDB.open();
            for (WidgetObject obj : mWidgetListForSaving) {
                storeGeofenceDB.storeWidgetData(obj);
            }
            storeGeofenceDB.close();
        }
    }

    public void notifyNewActivity(Activity activity, String newActivity, String oldActivity) {
        Context context = activity.getApplicationContext();
        if (Util.getPlatformType() == Util.PLATFORM_ANDROID_NATIVE || Util.getPlatformType() == Util.PLATFORM_XAMARIN) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_ACTIVITY, Context.MODE_PRIVATE);
            boolean bg = false;
            if (null != newActivity) { //send 8108
                String friendlyName = getFriendlyNameFromclassName(context.getApplicationContext(), newActivity);
                Bundle extras = new Bundle();
                extras.putInt(CODE,CODE_USER_ENTER_ACTIVITY);
                extras.putString(SHMESSAGE_ID, null);
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putLong(newActivity, System.currentTimeMillis());      // Store start time of this activity
                e.commit();
                extras.putString(TYPE_STRING, friendlyName);
                final Logging shManager = Logging.getLoggingInstance(context);
                shManager.addLogsForSending(extras);
               // saveWidgetList(activity);  //
                notifyEnteringNewActivityToChildModules(activity);
            } else {
                bg = true; // indicates this is last activity before going to bg
            }
            if (null != oldActivity) { //send 8109
                String friendlyName = getFriendlyNameFromclassName(context.getApplicationContext(), oldActivity);
                Bundle extras = new Bundle();
                extras.putInt(CODE,CODE_USER_LEAVE_ACTIVITY);
                extras.putString(SHMESSAGE_ID, null);
                if (null == friendlyName) {
                    friendlyName = oldActivity;
                }
                extras.putString(TYPE_STRING, friendlyName);
                final Logging shManager = Logging.getLoggingInstance(context);
                shManager.addLogsForSending(extras);
                extras.clear();
                extras.putInt(CODE,CODE_COMPLETE_ACTIVITY);
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
                notifyLeavingNewActivityToChildModules(activity);
            }
        }
    }
}
