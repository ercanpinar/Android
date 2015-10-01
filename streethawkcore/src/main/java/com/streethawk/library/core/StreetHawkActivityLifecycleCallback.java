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
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class StreetHawkActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private static StreetHawkActivityLifecycleCallback instance = null;

    private StreetHawkActivityLifecycleCallback() {
    }

    public static StreetHawkActivityLifecycleCallback getInstance() {
        if (null == instance) {
            instance = new StreetHawkActivityLifecycleCallback();
        }
        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        StreetHawk.INSTANCE.activityResumedByService(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        StreetHawk.INSTANCE.activityPausedByService(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}

