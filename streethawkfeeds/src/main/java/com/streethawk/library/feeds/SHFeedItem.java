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
import android.os.Bundle;
import android.util.Log;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Flow of events for feeds
 * 1. App may register for broadcast receiver to get notified for new feed item. (optional). App may optionally call badge API to display badge
 * 2. App should implement ISHFeedListener for receiving feed data and it's to the  app to process it
 * 3. App to call StreetHawk.INSTANCE.shGetFeedDataFromServer(context) for fetching feed data from server. This is the place where he can start displaying progress bar
 * 4. App to notify result with feedID
 */
public class SHFeedItem implements Constants {
    private static Context mContext;


    protected static final String BROADCAST_NEW_FEED = "com.streethawk.intent.action.newfeed";
    private static ISHFeedItemObserver mObserver = null;
    private static SHFeedItem mSHFeedItem;


    public void registerFeedItemObserver(ISHFeedItemObserver observer) {
        mObserver = observer;
    }

    public static SHFeedItem getInstance(Context context) {
        mContext = context;
        if (null == mSHFeedItem) {
            mSHFeedItem = new SHFeedItem();
        }
        if (Util.getPlatformType() == PLATFORM_XAMARIN) {
            StreetHawk.INSTANCE.tagString("sh_module_feeds", "true");
        }
        return mSHFeedItem;
    }

    private String getFeedTimeStamp() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SHFEEDTIMESTAMP, null);
    }

    private boolean NotifyFeedItemToApplication(String answer) {
        try {
            JSONObject jsonObject = new JSONObject(answer);
            JSONArray value = jsonObject.getJSONArray(Util.JSON_VALUE);
            if (null != mObserver) {
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
     *
     * @param feedId
     */
    public void sendFeedAck(int feedId) {
        if (null == mContext) {
            Log.e(Util.TAG, "notifyFeedResult: context==null returning..");
        }
        Bundle params = new Bundle();
        params.putInt(Util.CODE, CODE_FEED_ACK);
        params.putInt(SHFEEDID, feedId);
        Logging manager = Logging.getLoggingInstance(mContext);
        manager.addLogsForSending(params);
    }

    /**
     * Notify feed result for user action on feed item.
     *
     * @param feedId Id of feed item associated with result
     * @param result 1 Accepted, 0 postponed, -1 decline
     */
    @Deprecated
    public void notifyFeedResult(int feedId, int result) {
        if (1 == result) {
            notifyFeedResult(feedId, "accepted", true, false);
            return;
        }
        if (0 == result) {
            notifyFeedResult(feedId, "postponed", false, false);
            return;
        }
        if (-1 == result) {
            notifyFeedResult(feedId, "rejected", true, false);
            return;
        }
    }

    /**
     * @param feedId Id of the feed item associated with the result
     * @param result Feed result in String
     */
    private void notifyFeedResult_Old(int feedId, String result) {
        if (null == mContext) {
            Log.e(Util.TAG, "notifyFeedResult: context==null returning..");
        }
        Bundle params = new Bundle();
        params.putInt(Util.CODE, CODE_FEED_RESULT);
        params.putInt(SHFEEDID, feedId);

        if (result.equals("accepted")) {
            params.putInt(STATUS, 1);
        }
        if (result.equals("postponed")) {
            params.putInt(STATUS, 0);
        }
        if (result.equals("rejected")) {
            params.putInt(STATUS, 1);
        }
        Logging manager = Logging.getLoggingInstance(mContext);
        manager.addLogsForSending(params);
    }

    /**
     * @param feedId     Id of the feed item associated with the result
     * @param result     Feed result in String
     * @param feedDelete set to true if feed items should be deleted from server for the given install
     */
    public void notifyFeedResult(int feedId, String result, boolean feedDelete, boolean completed) {
        if (null == mContext) {
            Log.e(Util.TAG, "notifyFeedResult: context==null returning..");
        }
        Bundle params = new Bundle();
        params.putInt(Util.CODE, CODE_FEED_RESULT);
        params.putInt(SHFEEDID, feedId);
        JSONObject status = new JSONObject();
        try {
            status.put(RESULT_RESULT, result);
            status.put(RESULT_FEED_DELETE, feedDelete);
            status.put(RESULT_FEED_COMPLETED, completed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.putString(STATUS, status.toString());
        Logging manager = Logging.getLoggingInstance(mContext);
        manager.addLogsForSending(params);
    }


    /**
     * @param feedId     Identifier for the feeditem
     * @param step_id    Step identifier
     * @param result     accepted|rejected|postponed
     * @param feedDelete true is feed items needs to be deleted on server
     * @param completed  true if
     */
    public void notifyFeedResult(int feedId, String step_id, String result, boolean feedDelete, boolean completed) {
        if (null == mContext) {
            Log.e(Util.TAG, "notifyFeedResult: context==null returning..");
        }
        Bundle params = new Bundle();
        params.putInt(Util.CODE, CODE_FEED_RESULT);
        params.putInt(SHFEEDID, feedId);
        JSONObject status = new JSONObject();
        try {
            status.put(RESULT_ID, step_id);
            status.put(RESULT_RESULT, result);
            status.put(RESULT_FEED_DELETE, feedDelete);
            status.put(RESULT_FEED_COMPLETED, completed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.putString(STATUS, status.toString());
        Logging manager = Logging.getLoggingInstance(mContext);
        manager.addLogsForSending(params);
    }

    //TODO: change this to return feed from locally stored String
    //May be later

    public void readFeedData(final int offset) {
        if (null == mContext)
            return;
        final String INSTALL_ID = "installid";
        final String APP_KEY = "app_key";
        final String EQUALS = "=";
        final String AND = "&";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL baseurl = Util.getFeedUrl(mContext);
                    String urlParams = baseurl.toString();
                    urlParams += "?" + INSTALL_ID + EQUALS + Util.getInstallId(mContext) + AND +
                            APP_KEY + EQUALS + Util.getAppKey(mContext) + AND +
                            OFFSET + EQUALS + offset;
                    URL url = null;

                    try {
                        url = new URL(urlParams);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String answer = input.readLine();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.i("StreetHawk", "Received feed data from server");
                        NotifyFeedItemToApplication(answer);
                    }
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}