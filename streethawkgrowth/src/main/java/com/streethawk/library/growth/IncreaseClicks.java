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

import android.content.Context;
import android.util.Log;

import com.streethawk.library.core.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

class IncreaseClicks {

    private final String INSTALL_ID = "sh_cuid";
    private final String SCHEME = "scheme";
    private final String URI = "uri";
    private final String SHARE_GUID_URL = "share_guid_url";
    private Context mContext;
    private final String SUBTAG = "IncreaseClicks";
    private final String INCREASE_CLICKS = "https://pointzi.streethawk.com/increase_clicks/";

    public IncreaseClicks(Context context) {
        this.mContext = context;
    }


    /**
     * Notify server if user has clicked the link again to access deeplinked page
     *
     * @param scheme
     * @param deeplinkUrl
     * @return
     */
    public boolean increaseClicks(String scheme, String deeplinkUrl, String share_guid) {
        if (null == mContext)
            return false;
        if (!Util.isNetworkConnected(mContext)) {
            Log.w(Util.TAG, SUBTAG + "Device is not connected to network");
            return false;
        }
        String installId = Util.getInstallId(mContext);
        if (null == installId) {
            Log.w(Util.TAG, "App is not registered with StreetHawk server");
            return false;
        }
        HashMap<String, String> logMap = new HashMap<String, String>();
        logMap.put(URI, deeplinkUrl);
        logMap.put(SCHEME, scheme);
        logMap.put(INSTALL_ID, installId);
        logMap.put(SHARE_GUID_URL, share_guid);

        BufferedReader reader = null;
        try {
            String app_key = Util.getAppKey(mContext);
            if (null == app_key) {
                Log.e(Util.TAG, "Appkey is not defined.. returning");
                return false;
            }
            if (null == installId) {
                Log.w(Util.TAG, "App is not registered with StreetHawk server");
                return false;
            }
            URL url = new URL(INCREASE_CLICKS);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("X-Installid", installId);
            connection.setRequestProperty("X-App-Key", app_key);
            String libVersion = Util.getLibraryVersion();
            connection.setRequestProperty("X-Version", libVersion);
            connection.setRequestProperty("User-Agent", app_key + "(" + libVersion + ")");
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            //String logs = Util.getPostDataString(logMap);
            String logs = "";
            boolean first = true;
            for (Map.Entry<String, String> entry : logMap.entrySet()) {
                StringBuilder result = new StringBuilder();
                if (first)
                    first = false;
                else
                    result.append("&");
                String key = entry.getKey();
                String value = entry.getValue();
                if (null != key) {
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    if (null != value) {
                        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    } else {
                        result.append(URLEncoder.encode("", "UTF-8"));
                    }
                }
                logs += result.toString();
                result = null; //Force GC
            }
            writer.write(logs);
            writer.flush();
            writer.close();
            os.close();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String answer = reader.readLine();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                Log.i(Util.TAG, "increaseClicks response" + connection.getResponseCode() + " " + answer);
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
