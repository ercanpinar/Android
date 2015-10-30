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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.streethawk.library.core.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class hosts function for alert settings. Alert settings can be used to disable and re enable push messaging on user's device.
 */
class AlertSettings{
    public static class AlertSettingsInfo {

        public Long pause_minutes;
        public String pause_until;

        public void fillFromJson(JSONObject item) throws JSONException {
            if (item == null) {
                return;
            }

            if (item.has("pause_minutes")) {
                pause_minutes = item.getLong("pause_minutes");
            }
            if (item.has("pause_until")) {
                pause_until = item.getString("pause_until");
            }
        }

        public Date getPauseUntilAsDate() {
            if (TextUtils.isEmpty(pause_until)) {
                return null;
            }
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("utc"));
                Date dateResult = dateFormat.parse((String) pause_until);
                return dateResult;
            } catch (ParseException e) {
                return null;
            }
        }

        public Long getPauseUntilAsLong() {
            Date date = getPauseUntilAsDate();

            if (date == null) {
                return null;
            } else {
                return date.getTime();
            }
        }

    } //End of class AlertSettings

    public static AlertSettingsInfo parseAlertSettingsInfo(String json) {
        AlertSettingsInfo result = new AlertSettingsInfo();

        if (json == null) {
            return result;
        }

        try {
            JSONObject object = new JSONObject(json);
            JSONObject settings = object.getJSONObject("value");

            result.fillFromJson(settings);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static final String JSON_CODE = "code";
    private static final String PAUSE_MINUTES = "pause_minutes";
    private static final String SESSION_ID_KEY = "sessionid";
    private static final String COOKIE_KEY = "Cookie";
    public static final String INSTALL_ID = "installid";

    private static AlertSettings mUserManager=null;
    private AlertSettings(Context context) {
        mContext = context;
    }

    private static Context mContext;

    public static AlertSettings getInstance(Context context){
        mContext = context;
        if(null==mUserManager){
            mUserManager = new AlertSettings(context);
        }
        return mUserManager;
    }

    public AlertSettingsInfo getAlertSettings(){
        if(!Util.getStreethawkState(mContext)){
            return null;
        }
        Bundle query = new Bundle();
        String installId = Util.getInstallId(mContext);
        String app_key = Util.getAppKey(mContext);
        query.putString(Util.INSTALL_ID,installId);
        BufferedReader reader = null;
        try {
            URL url = Util.getAlertSettingUrl(mContext);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            String libVersion = Util.getLibraryVersion();
            connection.setRequestProperty("X-Installid", installId);
            connection.setRequestProperty("X-App-Key", app_key);
            connection.setRequestProperty("X-Version",libVersion);
            connection.setRequestProperty("User-Agent", app_key + "(" + libVersion+ ")");
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String answer = reader.readLine();
                if (checkAlertSettingsResponse(answer)) {
                    AlertSettingsInfo alertSettings = parseAlertSettingsInfo(answer);
                    return alertSettings;
                } else {
                    return null;
                }
            }
            connection.disconnect();
        }catch(Exception e){}
        return null;
    }

    private boolean checkAlertSettingsResponse(String response) {
        if (TextUtils.isEmpty(response)) {
            return false;
        }

        try {
            JSONObject object = new JSONObject(response);
            if (!object.has(JSON_CODE)) {
                return false;
            }
            int code = object.getInt(JSON_CODE);
            if (code == 0) {
                return true;
            }
            if (code == -1) {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }
    public boolean setAlertSettings(int pauseMinutes){
        if(!Util.getStreethawkState(mContext))
            return false;
        if(pauseMinutes>129600)
            pauseMinutes=-1;

        String installId = Util.getInstallId(mContext);
        String app_key =Util.getAppKey(mContext);
        try {
            HashMap<String, String> logMap = new HashMap<String, String>();
            logMap.put(INSTALL_ID, installId);
            logMap.put(PAUSE_MINUTES, Integer.toString(pauseMinutes));
            URL url = Util.getAlertSettingUrl(mContext);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            String libVersion = Util.getLibraryVersion();
            connection.setRequestProperty("X-Installid", installId);
            connection.setRequestProperty("X-App-Key", app_key);
            connection.setRequestProperty("X-Version",libVersion);
            connection.setRequestProperty("User-Agent", app_key + "(" + libVersion + ")");
            connection.setRequestProperty(COOKIE_KEY, SESSION_ID_KEY + "=" + Util.getSessionId(mContext));
            connection.connect();
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            String logs = Util.getPostDataString(logMap);
            writer.write(logs);
            writer.flush();
            writer.close();
            os.close();
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String answer = reader.readLine();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constants.SHPAUSETIME, pauseMinutes);
                long savedMins  = (long)(System.currentTimeMillis() / 60000);
                editor.putLong(Constants.SHSAVEDTIME,savedMins);
                editor.apply();
                return true;
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }




}
