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
package com.streethawk.library.feeds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Flow of events for feeds
 * 1. App may register for broadcast receiver to get notified for new feed item. (optional). App may optionally call badge API to display badge
 * 2. App should implement ISHFeedListener for receiving feed data and it's to the  app to process it
 * 3. App to call StreetHawk.INSTANCE.shGetFeedDataFromServer(context) for fetching feed data from server. This is the place where he can start displaying progress bar
 * 4. App to notify result with feedID
 */
public class SHFeedItem{
    private static Context mContext;
    private final String OFFSET         = "offset";
    private final int CODE_FEED_ACK     = 8200;
    private final int CODE_FEED_RESULT  = 8201;
    private final String SHFEEDID       = "feed_id";
    private final String SHRESULT       = "result";

    protected static final String BROADCAST_NEW_FEED ="com.streethawk.intent.action.newfeed";
    private static ISHFeedItemObserver mObserver = null;
    private static SHFeedItem mSHFeedItem;


    public void registerFeedItemObserver(ISHFeedItemObserver observer){
        mObserver = observer;
    }

    public static SHFeedItem getInstance(Context context) {
        mContext = context;
        if(null==mSHFeedItem){
            mSHFeedItem = new SHFeedItem();
        }
        return mSHFeedItem;
    }

    private String getFeedTimeStamp() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.SHFEEDTIMESTAMP, null);
    }

    private boolean NotifyFeedItemToApplication(String answer) {
        try {
            JSONObject jsonObject = new JSONObject(answer);
            JSONArray value = jsonObject.getJSONArray(Util.JSON_VALUE);
            if(null!=mObserver) {
                mObserver.shFeedReceived(value);
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send acknowledgement for the received feed item
     * @param feedId
     */
    public void sendFeedAck(int feedId) {
        if (null == mContext) {
            Log.e(Util.TAG, "notifyFeedResult: context==null returning..");
        }
        Bundle params = new Bundle();
        params.putString(Util.CODE, Integer.toString(CODE_FEED_ACK));
        params.putInt(SHFEEDID, feedId);
        Logging manager = Logging.getLoggingInstance(mContext);
        manager.addLogsForSending(params);
    }

    /**
     * Notify feed result for user action on feed item.
     * @param feedId Id of feed item associated with result
     * @param result 1 Accepted, 0 postponed, -1 decline
     */
    public void notifyFeedResult(int feedId, int result) {
        if (null == mContext) {
            Log.e(Util.TAG, "notifyFeedResult: context==null returning..");
        }
        Bundle params = new Bundle();
        params.putString(Util.CODE, Integer.toString(CODE_FEED_RESULT));
        params.putInt(SHFEEDID, feedId);
        params.putInt(SHRESULT, result);
        Logging manager = Logging.getLoggingInstance(mContext);
        manager.addLogsForSending(params);
    }

    /**
     * Call this function to start fetching feeds from server
     */
    public void readFeedData(final int offset) {
        if (null == mContext)
            return;
        final String APP_KEY = "app_key";
        final String INSTALL_ID = "installid";

        if (Util.isNetworkConnected(mContext)) {
            new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... params) {
                    BufferedReader reader = null;
                    try {
                        Bundle query = new Bundle();
                        String installId = Util.getInstallId(mContext);
                        String app_key = Util.getAppKey(mContext);
                        HashMap<String, String> logMap = new HashMap<String, String>();
                        logMap.put(INSTALL_ID, installId);
                        logMap.put(APP_KEY, app_key);
                        logMap.put(OFFSET, Integer.toString(offset));
                        URL url = null;
                        url = Util.getFeedUrl(mContext);
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
                            NotifyFeedItemToApplication(answer);
                            Logging.getLoggingInstance(mContext).processAppStatusCall(answer);
                        }else{
                            Logging.getLoggingInstance(mContext).processErrorAckFromServer(answer);
                        }
                        connection.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
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
}