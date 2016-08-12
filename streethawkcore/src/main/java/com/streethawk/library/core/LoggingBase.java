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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class LoggingBase implements Constants {
    private Context mContext;
    private final String SUBTAG         = "LoggingBase";
    private final String PRIORITY       = "priority";
    private final String RE_REGISTER    = "reregister";
    private final String STREETHAWK     = "streethawk";
    private final String HOST           = "host";
    private final String ACTIVITY_LIST  = "submit_views";
    private final String DISABLE_LOGS   = "disable_logs";

    protected LoggingBase(Context context) {
        this.mContext = context;
    }

    /*Member variables*/
    private static String mHostUrl = null;
    private  static final String PROD_DEFAULT_HOST_URL = "https://api.streethawk.com";
    //private  static final String PROD_DEFAULT_HOST_URL = "https://staging.streethawk.com";
    //private  static final String PROD_DEFAULT_HOST_URL = "https://hawk0.streethawk.com";

    protected enum ApiMethod {
        APP_GET_STATUS,
        USER_ALERT_SETTINGS,
        INSTALL_LIST,
        INSTALL_DETAILS,
        INSTALL_REGISTER,
        INSTALL_UPDATE,
        INSTALL_LOG,
        INSTALL_REPORT_CRASH,
        USER_FEEDBACK,
        SEND_ACTIVITY_LIST,
        CHECK_LIBRARY_VERSION,
        FETCH_IBEACON_LIST,
        FETCH_FEED_ITEMS,
        PUSH_MIRROR,
        FETCH_GEOFENCE_TREE,
        SUBMIT_INTERACTIVE_PUSH;
    }

    private static String getHostUrl(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        mHostUrl = sharedPreferences.getString(KEY_HOST, PROD_DEFAULT_HOST_URL);
        if (null == mHostUrl) {
            mHostUrl = PROD_DEFAULT_HOST_URL;
        }
        if (mHostUrl.isEmpty()) {
            mHostUrl = PROD_DEFAULT_HOST_URL;
        }
        return mHostUrl;
    }


    private static Uri getHostUri(Context context, ApiMethod method) {
        String API_VERSION = "v1";
        String API_VERSION_V2 = "v2";
        Uri hostUri = null;
        switch (method) {
            case INSTALL_LOG:
            case SUBMIT_INTERACTIVE_PUSH:
                hostUri = Uri.parse(getHostUrl(context)).buildUpon().appendPath(API_VERSION_V2).build();
                break;
            default:
                hostUri = Uri.parse(getHostUrl(context)).buildUpon().appendPath(API_VERSION).build();
        }
        return hostUri;
    }

    /**
     * Function returns route for a given install
     *
     * @param context Application context
     * @param method  method for loggig
     * @param query   query parameters
     * @return
     */
    protected  static String buildUri(Context context, ApiMethod method, Bundle query) {
        Uri.Builder uriBuilder = getHostUri(context, method).buildUpon();
        switch (method) {
            case APP_GET_STATUS: {
                uriBuilder.appendPath("apps");
                uriBuilder.appendPath("status");
                break;
            }
            case USER_ALERT_SETTINGS: {
                uriBuilder.appendPath("installs");
                uriBuilder.appendPath("alert_settings");
                uriBuilder.appendPath("");
                break;
            }
            case INSTALL_DETAILS: {
                uriBuilder.appendPath("installs");
                uriBuilder.appendPath("details");
                break;
            }
            case INSTALL_REGISTER: {
                uriBuilder.appendPath("installs");
                uriBuilder.appendPath("register");
                break;
            }
            case INSTALL_UPDATE: {
                uriBuilder.appendPath("installs");
                uriBuilder.appendPath("update");
                break;
            }
            case INSTALL_LOG: {
                uriBuilder.appendPath("installs");
                uriBuilder.appendPath("log");
                uriBuilder.appendQueryParameter(INSTALLID, Util.getInstallId(context));
                break;
            }
            case INSTALL_REPORT_CRASH: {
                uriBuilder.appendPath("installs");
                uriBuilder.appendPath("crash");
                break;
            }
            case USER_FEEDBACK:
                uriBuilder.appendPath("feedback");
                uriBuilder.appendPath("submit");
                break;
            case SEND_ACTIVITY_LIST:
                uriBuilder.appendPath("core");
                uriBuilder.appendPath("app");
                uriBuilder.appendPath("views");
                break;
            case CHECK_LIBRARY_VERSION:
                uriBuilder.appendPath("core");
                uriBuilder.appendPath("library");
                uriBuilder.appendQueryParameter(OPERATING_SYSTEM, "android");
                break;
            case FETCH_IBEACON_LIST:
                uriBuilder.appendPath("ibeacons");
                break;
            case FETCH_FEED_ITEMS:
                uriBuilder.appendPath("feed");
                break;
            case PUSH_MIRROR:
                uriBuilder.appendPath("push_mirror");
                break;
            case FETCH_GEOFENCE_TREE:
                uriBuilder.appendPath("geofences");
                uriBuilder.appendPath("tree");
                break;
            /*Interactive push Start*/
            case SUBMIT_INTERACTIVE_PUSH:
                uriBuilder.appendPath("apps");
                uriBuilder.appendPath("submit_interactive_button");
                break;
           /* Interactive push End*/
            default:
                Log.e(Util.TAG, "SUBTAG+No URL for given method " + method);
                break;
        }
        if (query != null) {
            Set<String> keys = query.keySet();
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    String value = query.getString(key);
                    if (!TextUtils.isEmpty(value)) {
                        uriBuilder.appendQueryParameter(key, value);
                    }
                }
            }
        }
        return uriBuilder.build().toString();
    }

    /**
     * Forcing install to reregister
     */
    private void forceReRegister() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = prefs.edit();
                e.putBoolean(SHINSTALL_STATE, false);
                e.putString(INSTALL_ID, null);
                e.commit();
                Install.getInstance(mContext).registerInstall();
            }
        }).start();
    }

    /**
     * Set list of codes for priority log lines
     * @param array
     */
    private void setPriority(JSONArray array) {
        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        if (null == array)
            e.putString(SHLOGPRIORITY, null);
        else
            e.putString(SHLOGPRIORITY, array.toString());
        e.commit();
    }

    /**
     * Set list of codes for disabled log line
     * @param array
     */
    private void setDisableLogs(JSONArray array) {
        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        if (null == array)
            e.putString(SHDISABLELOG, null);
        else
            e.putString(SHDISABLELOG, array.toString());
        e.commit();
    }


    /**
     * Returns true if code is in default priority list
     *
     * @param code
     * @return
     */

    private boolean getDefaultPriority(int code) {
        switch (code) {
            case CODE_LOCATION_UPDATES:         // 20
            case CODE_IBEACON_UPDATES:          // 21
            case CODE_GEOFENCE_UPDATES:         // 22
            case CODE_DEVICE_TIMEZONE:          // 8050
            case CODE_HEARTBEAT:                // 8051
            case CODE_APP_OPENED_FROM_BG:       // 8103
            case CODE_APP_TO_BG:                // 8104
            case CODE_USER_DISABLES_LOCATION:   // 8112
            case CODE_PUSH_ACK:                 // 8202  // Making 8202 priority logline as we have bundle logline now
            case CODE_PUSH_RESULT:              // 8203
            case CODE_INCREMENT_TAG:            // 8997
            case CODE_UPDATE_CUSTOM_TAG:        // 8999
            case CODE_DELETE_CUSTOM_TAG:        // 8998
                return true;
            default:
                return false;
        }
    }

    /**
     * Function to check if a log is a priority logline
     *
     * @param code
     * @return true if log is a priority logline
     */
    protected boolean isPriorityLogLine(int code) {
        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String priority = prefs.getString(SHLOGPRIORITY, null);
        if (null == priority) {
            return getDefaultPriority(code);
        } else {
            try {
                JSONArray array = new JSONArray(priority);
                for (int i = 0; i < array.length(); i++) {
                    try {
                        if (code == (int)array.get(i)) {
                            return true;
                        }
                    } catch (JSONException e) {
                        continue;
                    }
                }
            } catch (JSONException e) {
                return getDefaultPriority(code);
            }
            return false; // Nothing works then return false
        }
    }

    /**
     * Function checks logline code with list of disabled codes
     * @param code
     * @return
     */
    protected boolean isDisabledCode(int code){
        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String disabledLogs = prefs.getString(SHDISABLELOG, null);
        if (null == disabledLogs) {
            return false;  // All logs are enabled
        } else {
            try {
                JSONArray array = new JSONArray(disabledLogs);
                for (int i = 0; i < array.length(); i++) {
                    try {
                        if (code ==(int) array.get(i)) {
                            return true;  // disabled code return true˜
                        }
                    } catch (JSONException e) {
                        continue;
                    }
                }
            } catch (JSONException e) {
                return false;         // Not a disabled code, return false;
            }
            return false; // Nothing works then return false
        }
    }

    /**
     * Function process app status calls returns from server
     *
     * @param answer
     */
    protected void processAppStatusCall(String answer) {
        try {
            JSONObject object = new JSONObject(answer);
            if (object.has(Util.APP_STATUS)) {
                if (object.get(Util.APP_STATUS) instanceof JSONObject) {
                    JSONObject app_status = object.getJSONObject(Util.APP_STATUS);
                    if (app_status.has(PRIORITY) && !app_status.isNull(PRIORITY)) {
                        Object value_priority = app_status.get(PRIORITY);
                        if (value_priority instanceof JSONArray) {
                            setPriority((JSONArray) value_priority);
                        }
                    }
                    if (app_status.has(RE_REGISTER) && !app_status.isNull(RE_REGISTER)) {
                        Object value_reregister = app_status.get(RE_REGISTER);
                        if (value_reregister instanceof Boolean) {
                            if ((Boolean) value_reregister) {
                                forceReRegister();
                            }
                        }
                    }
                    if (app_status.has(STREETHAWK) && !app_status.isNull(STREETHAWK)) {
                        Object value_streethawk = app_status.get(STREETHAWK);
                        if (value_streethawk instanceof Boolean) {
                            setStreethawkState((Boolean) value_streethawk);
                        }
                    }
                    if (app_status.has(HOST) && !app_status.isNull(HOST)) {
                        Object value_host = app_status.get(HOST);
                        if (value_host instanceof String) {
                            setHost((String) value_host);
                        }
                    }
                    if (app_status.has(ACTIVITY_LIST) && !app_status.isNull(ACTIVITY_LIST)) {
                        Object value_activityList = app_status.get(ACTIVITY_LIST);
                        if (value_activityList instanceof Boolean) {
                            if ((Boolean) value_activityList) {
                                sendAppActivities(mContext);
                            }
                        }
                    }
                    if (app_status.has(DISABLE_LOGS) && !app_status.isNull(DISABLE_LOGS)) {
                        Object value_disable_logs = app_status.get(DISABLE_LOGS);
                        if (value_disable_logs instanceof JSONArray) {
                            setDisableLogs((JSONArray) value_disable_logs);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent notifyAppStatus = new Intent();
        notifyAppStatus.setAction(Util.BROADCAST_SH_APP_STATUS_NOTIFICATION);
        notifyAppStatus.putExtra(Util.INSTALL_ID, Util.getInstallId(mContext));
        notifyAppStatus.putExtra(Util.APP_STATUS_ANSWER, answer);
        mContext.sendBroadcast(notifyAppStatus);
    }

    private String getFriendlyNameFromclassName(Context context, final String fullyQualifiedName) {
        if (fullyQualifiedName == null)
            return null;
        if (null == context)
            return null;
        // Additional check for cases similar to Readerrewards
        String className = new StringBuilder(fullyQualifiedName).reverse().toString();
        int indexOfPeriod = className.indexOf(".");
        if (-1 == indexOfPeriod)
            return fullyQualifiedName;
        className = className.subSequence(0, className.indexOf(".")).toString();
        className = new StringBuilder(className).reverse().toString();
        return className;
    }

    protected void saveActivityNames(){
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activityInfo = packageInfo.activities;
            if (activityInfo != null) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_FRNDLST, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sharedPreferences.edit();
                for (ActivityInfo activity : activityInfo) {
                    String fullyQualifiedName = activity.name;
                    String className = new StringBuilder(fullyQualifiedName).reverse().toString();
                    int indexOfPeriod = className.indexOf(".");
                    if (-1 != indexOfPeriod) {
                        className = className.subSequence(0, className.indexOf(".")).toString();
                        className = new StringBuilder(className).reverse().toString();
                    }
                    String friendlyName = getFriendlyNameFromclassName(mContext, className);
                    e.putString(friendlyName,fullyQualifiedName);
                }
                e.commit();
                e = null;
                sharedPreferences = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context API sends friendly name to streethawk server
     */
    private void sendAppActivities(final Context context) {
        if (null == context)
            return;
        final String NAMES = "names";
        if (Util.isNetworkConnected(context)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String installId = Util.getInstallId(context);
                    String app_key = Util.getAppKey(context);
                    //JSONArray list = new JSONArray();
                    String list="[";
                    try {
                        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
                        ActivityInfo[] activityInfo = packageInfo.activities;
                        if (activityInfo != null) {
                            for (ActivityInfo activity : activityInfo) {
                                String fullyQualifiedName = activity.name;
                                if(fullyQualifiedName.contains("com.streethawk.library.")){
                                    continue;
                                }
                                String className = new StringBuilder(fullyQualifiedName).reverse().toString();
                                int indexOfPeriod = className.indexOf(".");
                                if (-1 != indexOfPeriod) {
                                    className = className.subSequence(0, className.indexOf(".")).toString();
                                    className = new StringBuilder(className).reverse().toString();
                                }
                                String friendlyName = getFriendlyNameFromclassName(context, className);
                                //list.put(getFriendlyNameFromclassName(context, className));
                                list+="\""+friendlyName+"\""+",";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int size = list.length();
                    if(size>1) {
                        list = list.substring(0,size-1);
                    }
                    list+="]";
                    if(list.equals("[]")) {
                        Log.i(Util.TAG,SUBTAG+"Returning due to empty logs");
                        return;
                    }
                    HashMap<String, String> logMap = new HashMap<String, String>();
                    logMap.put(INSTALL_ID, installId);
                    logMap.put(NAMES,list);
                    try {
                        URL url = new URL(buildUri(context, LoggingBase.ApiMethod.SEND_ACTIVITY_LIST, null));
                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        connection.setReadTimeout(10000);
                        connection.setConnectTimeout(15000);
                        connection.setRequestMethod("POST");
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setRequestProperty("X-Installid", installId);
                        connection.setRequestProperty("X-App-Key", app_key);
                        connection.setRequestProperty("User-Agent", app_key + "(" + SHLIBRARY_VERSION + ")");
                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        //String logs = Util.getPostDataString(logMap);
                        String logs="";
                        boolean first = true;
                        for (Map.Entry<String, String> entry : logMap.entrySet()) {
                            StringBuilder result = new StringBuilder();
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
                            logs+=result.toString();
                            result = null; //Force GC
                        }
                        writer.write(logs);
                        writer.flush();
                        writer.close();
                        os.close();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String answer = null;
                        if (null != reader) {
                            answer = reader.readLine();
                        }
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            Log.w(Util.TAG, "Failed to send friendly names " + connection.getResponseCode() + " " + answer);
                        }
                        connection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }).start();
        }
    }


    /**
     * Function handles error json returned from server
     *
     * @param answer
     */
    protected void processErrorAckFromServer(String answer) {
        try {
            String code = null;
            String value = null;
            JSONObject object = new JSONObject(answer);
            Iterator<?> keys = object.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (key instanceof String) {
                    switch (key) {
                        case CODE:
                            code = object.getString(CODE);
                            break;
                        case JSON_VALUE:
                            value = object.getString(JSON_VALUE);
                            break;
                    }
                }
            }

            Log.e(Util.TAG, SUBTAG + "Error " + code + " " + value);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }


    /**
     * Functions sets host url on which clients will be talking to server
     *
     * @param value_host
     */
    private void setHost(String value_host) {
        if (value_host == null)
            return;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        String currentHost = sharedPreferences.getString(KEY_HOST, null);
        if (null != currentHost && value_host.equals(currentHost))
            return;
        else {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(KEY_HOST, value_host);
            edit.commit();
        }
    }


    protected boolean getStreethawkState() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_STREETHAWK, true);
    }


    /**
     * Set Streethawk state
     *
     * @param value_streethawk
     */
    private void setStreethawkState(boolean value_streethawk) {

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        boolean current_state = sharedPreferences.getBoolean(KEY_STREETHAWK, true);
        if (current_state == value_streethawk)
            return;
        else {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean(KEY_STREETHAWK, value_streethawk);
            edit.apply();
        }
    }


}
