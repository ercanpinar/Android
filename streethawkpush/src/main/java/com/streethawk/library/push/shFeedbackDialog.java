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

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

class shFeedbackDialog {
    private Context mContext;
    private static shFeedbackDialog instance = null;
    private final String FEEDBACK_TITLE = "shStaggedFBTitle";
    private final String FEEDBACK_CONTENT = "shStaggedFBContent";
    private final String FEEDBACK_TYPE    = "shfeedbacktype";

    private shFeedbackDialog(Context context) {
        this.mContext = context;
    }

    public static shFeedbackDialog getIntance(Context context) {
        if (null == instance) {
            instance = new shFeedbackDialog(context);
            return instance;
        } else {
            return instance;
        }
    }

    public void flushPendingFeedback() {
        String title = null;
        String content = null;
        int type =0;
        if (null != mContext) {
            SharedPreferences prefs = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            title = prefs.getString(FEEDBACK_TITLE, null);
            content = prefs.getString(FEEDBACK_CONTENT, null);
            type = prefs.getInt(FEEDBACK_TYPE,0);
            if (null == title && null == content)
                return;
            else {
                sendFeedbackToServer(title, content,type);
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
                    String logs = Util.getPostDataString(logMap);
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
                        Logging.getLoggingInstance(mContext).processAppStatusCall(answer);
                    } else {
                        Logging.getLoggingInstance(mContext).processErrorAckFromServer(answer);
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}