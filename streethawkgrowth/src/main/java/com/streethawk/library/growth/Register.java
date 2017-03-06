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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.streethawk.library.core.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Observable;

import javax.net.ssl.HttpsURLConnection;

public class Register extends BroadcastReceiver {

    private Context mContext;
    private String REFERRER = "referrer";
    private String REGISTERED = "flaggrowthregister";
    private final String SUBTAG = "Register ";

    private final String DEVICE_UNIQUE = "installid";
    private final String INSTALL_ID = "sh_cuid";
    private final String APP_KEY = "app_key";
    private final String OS = "os";
    private final String TIME_ZONE = "timezone";
    private final String VERSION = "version";
    private final String DEVICE = "device";
    private final String SHARE_GUID_URL = "share_guid_url";
    private final String WIDHT = "width";
    private final String HEIGHT = "height";


    private static IGrowth mGrowthObserver = null;

    private String KEY_GROWTH_HOST = "shKeyHostGrowth";
    private final String FALLBACK = "growth.streethawk.com";

    private String getGrowhtHost() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String url = sharedPreferences.getString(KEY_GROWTH_HOST, null);
        if (null == url) {
            url = FALLBACK;
        }
        int index = url.indexOf("https://");
        if (index >= 0) {
            url = url.substring(index);
        }
        return url;

    }


    private String getReferrer() {
        String url = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE).getString(REFERRER, null);
        // setting url as null so that when some one simply opens app, he gets null
        mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE).
                edit().putString(REFERRER, null).commit();
        return url;
    }

    public Register() {
    }

    /**
     * Register IGrowthObserver
     *
     * @param observer
     */
    public void registerIGrowthObserver(IGrowth observer) {
        mGrowthObserver = observer;
    }

    /**
     * Constructor
     *
     * @param context
     */
    public Register(Context context) {
        this.mContext = context;
    }

    /**
     * Function registers install with StreetHawk Growth
     */
    public void registerStreetHawkGrowth() {
        if (null == mContext)
            return;
        if (!Util.isNetworkConnected(mContext)) {
            Log.w(Util.TAG, SUBTAG + "Device is not connected to network");
            return;
        }
        final String app_key = Util.getAppKey(mContext);
        final String installId = Util.getInstallId(mContext);
        if (null == installId) {
            Log.w(Util.TAG, SUBTAG + "App is not registered with StreetHawk server");
            return;
        }
        int mWidth = 0;
        int mHeight = 0;
        final WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mWidth = display.getWidth();
            mHeight = display.getHeight();

        } else {
            try {
                Point size = new Point();
                display.getRealSize(size);
                mWidth = size.x;
                mHeight = size.y;
            } catch (Exception e) {
                mWidth = 0;
                mHeight = 0;
            }
        }
        String width;
        String height;
        try {
            width = Integer.toString(mWidth);
            height = Integer.toString(mHeight);
        } catch (NumberFormatException e) {
            width = Integer.toString(0);
            height = Integer.toString(0);
        }
        String imei; // try fetching imei, if not then advertisementid and if not then emptystring
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (SecurityException exception) {
            Log.w(Util.TAG, SUBTAG + "Add  android.permission.READ_PHONE_STATE in AndroidManifest.xml");
            imei = Util.getAdvertisingIdentifier(mContext);
        }
        if (null == imei) {
            imei = ""; // imei is empty String
        }
        String share_guid_url = getReferrer();
        BufferedReader reader = null;
        try {
            if (null == app_key) {
                Log.e(Util.TAG, SUBTAG + "Appkey is not defined.. returning");
                return;
            }
            if (null == installId) {
                Log.w(Util.TAG, SUBTAG + "App is not registered with StreetHawk server");
                return;
            }
            String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
            deviceName = deviceName.trim();
            deviceName = deviceName.replaceAll("\\s", "");
            deviceName = deviceName.toLowerCase();
            String uri = new Uri.Builder()
                    .scheme("https")
                    .authority(getGrowhtHost())
                    .path("i/")
                    .appendQueryParameter(APP_KEY, app_key)
                    .appendQueryParameter(DEVICE_UNIQUE, imei)
                    .appendQueryParameter(INSTALL_ID, Util.getInstallId(mContext))
                    .appendQueryParameter(OS, "Android")
                    .appendQueryParameter(DEVICE, deviceName)
                    .appendQueryParameter(VERSION, Build.VERSION.RELEASE)
                    .appendQueryParameter(TIME_ZONE, Integer.toString(Util.getTimeZoneOffsetInMinutes()))
                    .appendQueryParameter(WIDHT, width)
                    .appendQueryParameter(HEIGHT, height)
                    .appendQueryParameter(SHARE_GUID_URL, share_guid_url)
                    .build().toString();
            URL url = new URL(uri);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(true);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String answer = reader.readLine();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE).
                        edit().putBoolean(REGISTERED, true).commit();
                try {
                    JSONObject object = new JSONObject(answer);
                    if (null != object) {
                        String reply = object.getJSONObject("message").getString("uri");
                        String scheme = object.getJSONObject("message").getString("scheme");
                        String Url = null;
                        Log.i("TAG", "Deeplink URL received" + scheme + Url);
                        if (null != scheme) {
                            Url = scheme + "://";
                            if (null != reply) {
                                Url += reply;
                            }
                        }
                        if (null != Url) {
                            if (null != mGrowthObserver) {
                                mGrowthObserver.onReceiveDeepLinkUrl(Url);
                            }
                            Intent deepLinkIntent = new Intent();
                            deepLinkIntent.setAction("android.intent.action.VIEW");
                            deepLinkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            deepLinkIntent.setData(Uri.parse(Url));
                            mContext.startActivity(deepLinkIntent);
                        }
                    }
                } catch (JSONException e) {
                    try {
                        JSONObject object = new JSONObject(answer);
                        Log.e(Util.TAG, SUBTAG + object.getString("message"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                Log.e(Util.TAG, SUBTAG + "Growth register install Response" + connection.getResponseCode());
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.e(Util.TAG, SUBTAG + " " + line);
                }
                mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE).
                        edit().putBoolean(REGISTERED, false).commit();
            }
            connection.disconnect();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static class ObservableChanged extends Observable {
        @Override
        public boolean hasChanged() {
            return true;
        }
    }

    private static final ObservableChanged _observable = new ObservableChanged();


    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
            return;
        }

        /**
         * Note: Command to test broadcast using adb
         * adb shell am broadcast -a com.android.vending.INSTALL_REFERRER --es referrer "hi anurag" --es app_key "shsample"
         */
        try {
            if ((null != intent)) {
                String rawReferrer = intent.getStringExtra(REFERRER);
                String app_key = intent.getStringExtra(APP_KEY);
                Log.e(Util.TAG, "rawReferrer" + rawReferrer);
                if (null != rawReferrer) {
                    if (app_key.equalsIgnoreCase(Util.getAppKey(context))) {
                        String referrer = URLDecoder.decode(rawReferrer, "UTF-8");
                        context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE).
                                edit().putString(REFERRER, referrer).commit();
                        // Let any listeners know about the change.

                        _observable.notifyObservers(referrer);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(Util.TAG, SUBTAG + e.getMessage());
        }
    }

}
