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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.streethawk.library.core.*;

import java.util.List;
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class PushActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private final String SHPGPREVPAGE         = "shpgprevpage";
    private static PushActivityLifecycleCallback instance = null;
    public static PushActivityLifecycleCallback getInstance() {
        if (null == instance) {
            instance = new PushActivityLifecycleCallback();
        }
        return instance;
    }

    private void notifyAppStateResumed(final Activity activity) {
        final Context context = activity.getApplicationContext();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
                            Push.getInstance(context).notifyAppForegrounded(activity);
                        } else if (currentActivity.equals(storedActivity)) {
                            Push.getInstance(context).notifyChangeOrientation(activity);
                            return;
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
                List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                if (!tasks.isEmpty()) {
                    ComponentName topActivity = tasks.get(0).topActivity;
                    if (!(topActivity.getPackageName().equals(context.getPackageName()))) {
                        Push.getInstance(context).notifyAppBackgrounded(activity);
                    }
                } else {
                    Push.getInstance(context).notifyAppBackgrounded(activity);
                }
                return;
            }
        }, 500);
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        notifyAppStateResumed(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        SHForegroundNotification.dismissCurrentShowingDialog();
        notifyAppStatePaused(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
