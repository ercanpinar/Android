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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class Logging extends LoggingBase {

    private static final String SUBTAG = "Logging ";


    private final int MAX_LOG_CNT = 50;            //Size of log buffer
    private final int WAIT_BUFFER = 1;
    private static boolean semHeld = false;

    public static final String SHSHARED_PREF_LOGGING    = "shlogbugger";      // to buffer logs
    public static final String SHSHARED_PREF_STAGGING   = "shbufferstagging"; // temp cache of logs

    /*String constants used in logging*/
    private final String LOGIDCOUNT = "log_id_count";
    private final String BUFFER_COUNT = "buffer_count";
    private final String KEY_COUNT = "key_count";
    private final String LOG_ID = "log_id";
    private final String CREATED = "created_on_client";
    private final String SESSION_ID = "session_id";
    private final String RECORDS = "records";
    private final String BUNDLE_ID = "bundle_id";
    private final String BUNDLE_CNT = "bundle_cnt";
    protected static final String JSON_VALUE = "value";

    private static Logging mInstance;
    private static Context mContext;
    private volatile int logIdCounter;


    private Logging(Context context){
        super(context);
        mContext = context;

    }

    public Logging getInstance(Context context){
        if(null==mInstance)
            mInstance = new Logging(context);
        return mInstance;
    }



    private int getLogId(Context context) {
        synchronized (context) {
            try {
                SharedPreferences pref = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                logIdCounter = pref.getInt(LOGIDCOUNT, 0);
                logIdCounter++;
                SharedPreferences.Editor e = pref.edit();
                e.putInt(LOGIDCOUNT, logIdCounter);
                e.commit();
            } catch (ConcurrentModificationException exception) {
                exception.printStackTrace();
            }
            return logIdCounter;
        }
    }

    /**
     * Function to check status of StreetHawk logs.
     * @param context
     * @return false will disable sending of logs to streethawk server
     */
    public boolean isInstallLogAllowed(Context context) {
        boolean result = false;
        SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        result = prefs.getBoolean(Constants.SHLOG_ALLOWED, true);
        return result;
    }

    /**
     * Add logs in queue with forced priority
     * @param params
     * @param forcedPriority
     * @return
     */
    public boolean addLogsForSending(Bundle params,boolean forcedPriority){
        if(!getStreethawkState())  // Return if StreetHawk is disabled
            return false;
        return addLogstoBuffer(params,forcedPriority);
    }


    /**
     * Add logs in the queue for sending to server
     * @param params
     */
    public boolean addLogsForSending(Bundle params) {
        if(!getStreethawkState())  // Return if StreetHawk is disabled
            return false;
        int code = 0;
        try {
            code = Integer.parseInt(params.getString(CODE));
        } catch (NumberFormatException e) {
            code = 0;
        }
        if(isDisabledCode(code)){
            Log.i(Util.TAG,"Log line with code "+code+" is disabled by server");
            return false;
        }
        boolean priority = isPriorityLogLine(code);
        return addLogstoBuffer(params, priority);

    }

    public void checkAppState() {
        if (null == mContext)
            return;
        if (Util.isNetworkConnected(mContext)) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    String installId = Util.getInstallId(mContext);
                    String app_key = Util.getAppKey(mContext);
                    try {
                        Bundle query = new Bundle();
                        query.putString(Util.SHAPP_KEY, app_key);
                        query.putString(Util.INSTALL_ID, installId);
                        URL url = new URL(buildUri(mContext, ApiMethod.FETCH_IBEACON_LIST,query));
                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        connection.setReadTimeout(10000);
                        connection.setConnectTimeout(15000);
                        connection.setRequestMethod("GET");
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setRequestProperty("X-Installid", installId);
                        connection.setRequestProperty("X-App-Key", app_key);
                        String libVersion = Util.getLibraryVersion();
                        connection.setRequestProperty("X-Version",libVersion);
                        connection.setRequestProperty("User-Agent", app_key + "(" + libVersion + ")");
                        connection.connect();
                        BufferedReader reader = null;
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String answer = reader.readLine();
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            processAppStatusCall(answer);
                        } else {
                            processErrorAckFromServer(answer);
                        }
                        connection.disconnect();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
    }

    public void processAppStatusCall(String answer){
        super.processAppStatusCall(answer);
    }

    public void processErrorAckFromServer(String answer){
        super.processErrorAckFromServer(answer);
    }


    public void sendModuleList(){
        if(PLATFORM_XAMARIN==Util.getPlatformType()){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String PUSH     = "sh_module_push";
                final String BEACON   = "sh_module_beacon";
                final String GEOFENCE = "sh_module_geofence";
                final String LOCATION = "sh_module_location";
                final String FEEDS    = "sh_module_feeds";
                final String GROWTH   = "sh_module_growth";
                final String TRUE     = "true";

                Class[] paramContext = new Class[1];
                paramContext[0] = Activity.class;
                //Growth Module
                try {
                    Class.forName("com.streethawk.library.growth.Growth");
                    Bundle extras = new Bundle();
                    extras.putString(CODE, Integer.toString(Constants.CODE_UPDATE_CUSTOM_TAG));
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SH_KEY, GROWTH);
                    extras.putString(TYPE_STRING,TRUE);
                    addLogsForSending(extras,false);
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Growth module is not  not present");
                }
                //Push Module
                try {
                    Class.forName("com.streethawk.library.push.Push");
                    Bundle extras = new Bundle();
                    extras.putString(CODE, Integer.toString(Constants.CODE_UPDATE_CUSTOM_TAG));
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SH_KEY, PUSH);
                    extras.putString(TYPE_STRING,TRUE);
                    addLogsForSending(extras,false);
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Growth module is not  not present");
                }
                // Beacon Module
                try {
                    Class.forName("com.streethawk.library.beacon.Beacons");
                    Bundle extras = new Bundle();
                    extras.putString(CODE, Integer.toString(Constants.CODE_UPDATE_CUSTOM_TAG));
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SH_KEY, BEACON);
                    extras.putString(TYPE_STRING,TRUE);
                    addLogsForSending(extras,false);
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Growth module is not  not present");
                }
                // Geofence Module
                try {
                    Class.forName("com.streethawk.library.geofence.SHGeofence");
                    Bundle extras = new Bundle();
                    extras.putString(CODE, Integer.toString(Constants.CODE_UPDATE_CUSTOM_TAG));
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SH_KEY, GEOFENCE);
                    extras.putString(TYPE_STRING,TRUE);
                    addLogsForSending(extras,false);
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Growth module is not  not present");
                }
                //Location Module
                try {
                    Class.forName("com.streethawk.library.locations.SHLocation");
                    Bundle extras = new Bundle();
                    extras.putString(CODE, Integer.toString(Constants.CODE_UPDATE_CUSTOM_TAG));
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SH_KEY, LOCATION);
                    extras.putString(TYPE_STRING,TRUE);
                    addLogsForSending(extras,false);
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Growth module is not  not present");
                }
                //Feed module
                try {
                    Class.forName("com.streethawk.library.feeds.SHFeedItem");
                    Bundle extras = new Bundle();
                    extras.putString(CODE, Integer.toString(Constants.CODE_UPDATE_CUSTOM_TAG));
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SHMESSAGE_ID, null);
                    extras.putString(SH_KEY, FEEDS);
                    extras.putString(TYPE_STRING,TRUE);
                    addLogsForSending(extras,false);
                } catch (ClassNotFoundException e1) {
                    Log.w(Util.TAG, "Growth module is not  not present");
                }

            }
        }).start();
    }

    /**
     * Function returns key count which acts as key for logs in logbuffer shared pref.
     * @return
     */
    private int getKeyCnt() {
        SharedPreferences prefs = mContext.getSharedPreferences(SHSHARED_PREF_LOGGING, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        int cnt = prefs.getInt(KEY_COUNT, 0);
        ++cnt;
        e.putInt(KEY_COUNT, cnt);
        e.apply();
        return cnt;
    }


    /**
     * Add logs to buffer
     * @param params
     * @param priority
     * @return
     */
    private boolean addLogstoBuffer(Bundle params, boolean priority) {
        if (!Util.getStreethawkState(mContext))
            return false;
        if (!isInstallLogAllowed(mContext)) {
            return true;
        }
        String sessionId = Util.getSessionId(mContext);
        int code = 0;
        if (Util.isAppBG(mContext)) {
            try {
                code = Integer.parseInt(params.getString(CODE));
            } catch (NumberFormatException e) {
                code = 0;
            }
            switch (code) {
                case CODE_APP_OPENED_FROM_BG:
                case CODE_APP_TO_BG:
                case CODE_SESSIONS:
                case CODE_USER_ENTER_ACTIVITY:
                case CODE_USER_LEAVE_ACTIVITY:
                case CODE_COMPLETE_ACTIVITY:
                    break;
                default:
                    sessionId = null;
            }
        }


        String logid = Integer.toString(getLogId(mContext));
        params.putString(LOG_ID, logid);
        params.putString(CREATED, Util.getFormattedDateTime(System.currentTimeMillis(), true));
        params.putString(SESSION_ID, sessionId);
        JSONObject dictionary = new JSONObject();
        Set<String> names = params.keySet();
        for (String name : names) {
            Object value = params.get(name);
            if (value != null) {
                if (value instanceof String) {
                    try {
                        if (name.equals("json")) {
                            JSONObject object = new JSONObject(params.getString(name));
                            dictionary.put(name, object);
                        } else {
                            dictionary.put(name, params.getString(name));
                        }

                        continue;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (value instanceof Integer) {
                    try {
                        dictionary.put(name, params.getInt(name));
                        continue;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    try {
                        dictionary.put(name, value);
                        continue;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String keyCount = Integer.toString(getKeyCnt());
        SharedPreferences prefs = mContext.getSharedPreferences(SHSHARED_PREF_LOGGING, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString(keyCount, dictionary.toString());
        e.commit();
        if (MAX_LOG_CNT == getBufferCnt() || priority) {
            return copyLogsinCycleBuffer();
        }
        return true;
    }


    /**
     * Two buffer logic to collect logs which needs to be flushed to server.
     * @return
     */
    private boolean copyLogsinCycleBuffer() {
        if (semHeld)
            return true;
        ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
                Executors.newScheduledThreadPool(5);
        semHeld = true;
        Runnable oneShotTask = new Runnable() {
            @Override
            public void run() {
                SharedPreferences sourceBuffer = mContext.getSharedPreferences(SHSHARED_PREF_LOGGING, Context.MODE_PRIVATE);
                SharedPreferences.Editor sourceEdit = sourceBuffer.edit();
                String logs="[";
                int keyCount = 1 + sourceBuffer.getInt(KEY_COUNT, 0);
                String str;
                for (int i = 0; i < keyCount; i++) {
                    JSONObject dictionary = null;
                    str = sourceBuffer.getString(Integer.toString(i), null);
                    try {
                        if (null != str)
                            dictionary = new JSONObject(str);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (null != dictionary) {
                        logs+=dictionary.toString()+",";
                    }
                }
                //remove last comma
                int size = logs.length();
                if(size>1) {
                    logs = logs.substring(0,size-1);
                }
                logs+="]";
                sourceEdit.clear();
                sourceEdit.commit();
                if(logs.equals("[]")) {
                    return;
                }
                SharedPreferences DestinationBuffer = mContext.getSharedPreferences(SHSHARED_PREF_STAGGING, Context.MODE_PRIVATE);
                SharedPreferences.Editor destinationEdit = DestinationBuffer.edit();
                String key = getBundleId();
                destinationEdit.putString(key,logs);
                destinationEdit.commit();
                flushLogsToServer(key);
                semHeld = false;
            }
        };
        ScheduledFuture<?> oneShotFuture = sch.schedule(oneShotTask, WAIT_BUFFER, TimeUnit.SECONDS);
        return true;
    }

    public void saveActivityNames(){
        super.saveActivityNames();
    }

    /**
     * Flush cached logs to server
     * @param key
     * @return
     */
    private boolean flushLogsToServer(final String key) {
        try {
            if (Util.isNetworkConnected(mContext)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String records = null;
                        String bundle_id;
                        String installId = Util.getInstallId(mContext);
                        if (null == installId)
                            return;
                        if (installId.isEmpty())
                            return ;
                        String app_key = Util.getAppKey(mContext);
                        SharedPreferences prefs = mContext.getSharedPreferences(SHSHARED_PREF_STAGGING, Context.MODE_PRIVATE);
                        SharedPreferences.Editor staggingEdit = prefs.edit();
                        if (key != null) {
                            records = prefs.getString(key, null);
                            bundle_id = key;
                        } else {
                            //JSONArray mainArray = new JSONArray();
                            String mainLogs="[";
                            Map<String, ?> keysPrefs = prefs.getAll();
                            if (keysPrefs == null) {
                                return;
                            }
                            for (Map.Entry<String, ?> eachKey : keysPrefs.entrySet()) {
                                // Iterate over sharedpreference and collect all keys in array
                                String inUsekey = eachKey.getKey();
                                JSONArray subArray;
                                try {
                                    String arryStr = prefs.getString(inUsekey, null);
                                    if (arryStr == null)
                                        continue;
                                    subArray = new JSONArray(arryStr);
                                    for (int i = 0; i < subArray.length(); i++) {
                                        JSONObject subobject = subArray.getJSONObject(i);
                                        //mainArray.put(subobject);
                                        mainLogs+=subobject.toString()+",";
                                    }
                                    staggingEdit.remove(inUsekey);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            int size = mainLogs.length();
                            if(size>1) {
                                mainLogs = mainLogs.substring(0,size-1);
                            }
                            mainLogs+="]";
                            if(mainLogs.equals("[]")) {
                                Log.i(Util.TAG,SUBTAG+"Returning due to empty logs");
                                return;
                            }

                            records = mainLogs;
                            bundle_id = getBundleId();
                            staggingEdit.putString(bundle_id, records);
                            staggingEdit.commit();
                        }
                        if (records == null) {
                            staggingEdit.remove(bundle_id);
                            staggingEdit.commit();
                            return;
                        }
                        if (records.isEmpty()) {
                            staggingEdit.remove(bundle_id);
                            staggingEdit.commit();
                            return;
                        }
                        HashMap<String, String> logMap = new HashMap<String, String>();
                        logMap.put(RECORDS, records);
                        logMap.put(BUNDLE_ID, bundle_id);
                        BufferedReader reader = null;
                        try {
                            URL url = new URL(buildUri(mContext, ApiMethod.INSTALL_LOG, null));
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setReadTimeout(10000);
                            connection.setConnectTimeout(15000);
                            connection.setRequestMethod("POST");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.setRequestProperty("X-Installid", installId);
                            connection.setRequestProperty("X-App-Key", app_key);
                            String libVersion = Util.getLibraryVersion();
                            connection.setRequestProperty("X-Version",libVersion);
                            connection.setRequestProperty("User-Agent", app_key + "(" + libVersion + ")");
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
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String answer = reader.readLine();
                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                if (null == answer)
                                    return;
                                if (answer.isEmpty())
                                    return;
                                try {
                                    JSONObject object = new JSONObject(answer);
                                    String ack_key = object.getString(JSON_VALUE);
                                    staggingEdit.remove(ack_key);
                                    staggingEdit.commit();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                processAppStatusCall(answer);
                                //Recursion for flushing failed bundles
                                flushLogsToServer(null);
                            } else {
                                processErrorAckFromServer(answer);
                            }
                            connection.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }).start();
            }
        } catch (Exception e) {
                Log.e(Util.TAG, "SUBTAG+Exception " + e);
        }
        return false;
    }
    public boolean ForceFlushLogsToServer() {
        return flushLogsToServer(null);
    }

    /**
     * Function returns bundle ID which server echos back as ack and indicated install to clear the cached logs associated with that install.
     * @return
     */
    private String getBundleId() {
        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        int cnt = prefs.getInt(BUNDLE_CNT, 0);
        ++cnt;
        e.putInt(BUNDLE_CNT, cnt);
        e.commit();
        return Integer.toString(cnt);
    }


    /**
     * Function returns count of buffered logs which needs to be flushed to server
     * @return int as buffer count
     */
    private int getBufferCnt() {
        SharedPreferences prefs = mContext.getSharedPreferences(SHSHARED_PREF_LOGGING, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        int cnt = prefs.getInt(BUFFER_COUNT, 0);
        ++cnt;
        e.putInt(BUFFER_COUNT, cnt);
        e.commit();
        return cnt;
    }
    /**
     * Function resets session id
     * @param context
     */
    public static void resetSessionId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putInt(Constants.SESSIONIDCNT, 1);
        e.commit();
    }

    /**
     * Function returns singleton instance of logging class
     * @param context
     * @return
     */
    public static Logging getLoggingInstance(Context context) {
        if (null == context) {
            Log.e(Util.TAG, SUBTAG + "context is null in getStreethawkLogManagerInstance. returning");
            return null;
        }
        if (null == mInstance)
            return new Logging(context);
        else
            return mInstance;
    }

    public void flushPendingFeedback() {
        String title = null;
        String content = null;
        int type =0;
        if (null != mContext) {
            SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            title = prefs.getString(Constants.FEEDBACK_TITLE, null);
            content = prefs.getString(Constants.FEEDBACK_CONTENT, null);
            type = prefs.getInt(Constants.FEEDBACK_TYPE,0);
            if (null == title && null == content)
                return;
            else {
                Logging.getLoggingInstance(mContext).sendFeedbackToServer(title, content,type);
            }
        }
    }

    public void sendFeedbackToServer(final String title, final String content, final int feedbacktype) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String CONTENTS = "contents";
                final String TITLE = "title";
                final String BUILT_AT = "built_at";
                final String FEEDBACK_TYPE = "feedback_type";
                Calendar calender = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(calender.getTime());

                HashMap<String, String> logMap = new HashMap<String, String>();
                logMap.put(BUILT_AT, formattedDate);
                logMap.put(Util.INSTALL_ID, Util.getInstallId(mContext));
                if (content != null)
                    logMap.put(CONTENTS, content);
                if (title != null)
                    logMap.put(TITLE, title);
                String type;
                try{
                    type = Integer.toString(feedbacktype);
                }catch(NumberFormatException e){
                    type = null;
                }
                if(null!=type)
                    logMap.put(FEEDBACK_TYPE,Integer.toString(feedbacktype));

                BufferedReader reader = null;
                try {
                    String app_key = Util.getAppKey(mContext);
                    URL url = (Util.getFeedbackUrl(mContext));
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("X-Installid", Util.getInstallId(mContext));
                    connection.setRequestProperty("X-App-Key", app_key);
                    String libVersion = Util.getLibraryVersion();
                    connection.setRequestProperty("X-Version",libVersion);
                    connection.setRequestProperty("User-Agent", app_key + "(" + libVersion + ")");
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
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String answer = reader.readLine();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        if (null == answer)
                            return;
                        if (answer.isEmpty())
                            return;
                        processAppStatusCall(answer);
                    } else {
                        SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                        SharedPreferences.Editor e = prefs.edit();
                        e.putString(Constants.FEEDBACK_TITLE, title);
                        e.putString(Constants.FEEDBACK_CONTENT,content);
                        e.putString(Constants.FEEDBACK_TYPE,type);
                        e.commit();
                        processErrorAckFromServer(answer);
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
