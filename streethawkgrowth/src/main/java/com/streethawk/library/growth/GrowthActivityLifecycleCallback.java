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
package com.streethawk.library.growth;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.streethawk.library.core.Util;
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class GrowthActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private String REGISTERED = "flaggrowthregister";
    private static GrowthActivityLifecycleCallback instance = null;
    public static GrowthActivityLifecycleCallback getInstance() {
        if (null == instance) {
            instance = new GrowthActivityLifecycleCallback();
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
        if(null==activity)
            return;

        Growth.getInstance(activity);
        final Context context = activity.getApplicationContext();
        final Intent intent = activity.getIntent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE).getBoolean(REGISTERED, false)) {
                    String installId = Util.getInstallId(context);
                    if (null != installId) {
                        Register object = new Register(context);
                        object.registerStreetHawkGrowth();
                    }
                }
            }
        }).start();
        if(null!=intent) {
            String scheme = intent.getScheme();
            String deeplink_uri = intent.getDataString();
            String share_guidTmp = null;
            try {
                share_guidTmp = intent.getData().getQueryParameter("share_guid_url");
            } catch (Exception e) {
                share_guidTmp = null;
            }
            if (null != scheme) {
                int index;
                if (null == deeplink_uri)
                    deeplink_uri = null;
                try {
                    index = deeplink_uri.indexOf(':');
                    if (index > 0) {
                        scheme = deeplink_uri.substring(0, index);
                        deeplink_uri = deeplink_uri.replaceAll(scheme + "://", "");
                    } else {
                        scheme = null;
                    }
                } catch (Exception etr) {
                    etr.printStackTrace();
                    return;
                }
            }
            final String finScheme = scheme;
            final String finDeepLinkUri = deeplink_uri;
            final String share_guid = share_guidTmp;
            if (null != finScheme && null != finDeepLinkUri && null != share_guidTmp) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        IncreaseClicks object = new IncreaseClicks(context);
                        object.increaseClicks(finScheme, finDeepLinkUri, share_guid);
                    }
                }).start();

            } else {
                Log.i(Util.TAG,"App not launched using a deeplink URI");
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
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