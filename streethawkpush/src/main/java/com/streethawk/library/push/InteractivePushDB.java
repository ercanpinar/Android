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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.streethawk.library.core.Util;

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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

class InteractivePushDB implements Constants{
    private SQLiteDatabase mDatabase;
    private PushNotificationHelper mDbHelper;
    private Context mContext;
    private static InteractivePushDB instance = null;

    private static final String BUTTON_PAIR_TABLE_NAME    = "interactivepush";
    private static final String COLUMN_BTNPAIRID          = "id";

    private static final String COLUMN_B1TITLE            = "b1title";
    private static final String COLUMN_B1ICON             = "b1icon";

    private static final String COLUMN_B2TITLE            = "b2title";
    private static final String COLUMN_B2ICON             = "b2icon";

    private static final String COLUMN_B3TITLE            = "b3title";
    private static final String COLUMN_B3ICON             = "b3icon";

    public static InteractivePushDB getInstance(Context context){
        if(null==instance){
            instance = new InteractivePushDB(context);
        }
        return instance;
    }

    private InteractivePushDB(Context context) {
        this.mContext = context;
        mDbHelper = new PushNotificationHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
        if(mDatabase.isOpen())
            mDatabase.close();
    }

    /**
     * TODO: Implement this function
     * Function returns true if database is empty
     */
    public boolean checkForEmptyDB(){
        return false;
    }

    public void storeBtnPairsFromList(ArrayList<InteractivePush> objectList) {
        ContentValues values = new ContentValues();
        if(!mDatabase.isOpen()){
            mDatabase.isOpen();
        }
        for(InteractivePush object:objectList){
            values.put(COLUMN_BTNPAIRID,object.getPairTitle());

            values.put(COLUMN_B1TITLE,object.getB1Title());
            values.put(COLUMN_B1ICON,object.getB1Icon());

            values.put(COLUMN_B2TITLE,object.getB2Title());
            values.put(COLUMN_B2ICON,object.getB2Icon());

            values.put(COLUMN_B3TITLE,object.getB3Title());
            values.put(COLUMN_B3ICON,object.getB3Icon());

            mDatabase.insert(BUTTON_PAIR_TABLE_NAME, null, values);
        }
        mDatabase.close();
    }

    /**
     * Function stores single object
     * @param object
     * @throws IllegalStateException
     */
    public void storeInteractivePushBtnPair(InteractivePush object) throws IllegalStateException {
        ContentValues values = new ContentValues();
        values.put(COLUMN_BTNPAIRID, object.getPairTitle());
        values.put(COLUMN_B1TITLE, object.getB1Title());
        values.put(COLUMN_B1ICON, object.getB1Icon());
        values.put(COLUMN_B2TITLE, object.getB2Title());
        values.put(COLUMN_B2ICON, object.getB2Icon());
        values.put(COLUMN_B3TITLE, object.getB3Title());
        values.put(COLUMN_B3ICON, object.getB3Icon());

        // Checking agian as it crashed once due to sync issue.
        if(!mDatabase.isOpen()){
            mDatabase.isOpen();
        }
        try {
            mDatabase.insert(BUTTON_PAIR_TABLE_NAME, null, values);
        }catch(IllegalStateException e){
            e.printStackTrace();
            return;
        }
        mDatabase.close();
    }

    public void forceDeleteAllRecords(){
        mDatabase.execSQL("delete from " + BUTTON_PAIR_TABLE_NAME);
    }


    /**
     * Function returns button pair based on pair title. Apps will require this function to know button pair
     * @param pairTitle par title as in content type
     * @param obj
     * @return
     */
    public boolean getBtnPairData(final String pairTitle,final InteractivePush obj) {
        PushNotificationHelper helper = new PushNotificationHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();
        if (null == pairTitle) {
            database.close();
            helper.close();
            return false;
        } else {
            String query = "select * from " + BUTTON_PAIR_TABLE_NAME +
                    " where " + COLUMN_BTNPAIRID + " = " + "'" + pairTitle + "'";
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {

                String B1Title = cursor.getString(cursor.getColumnIndex(COLUMN_B1TITLE));
                int B1Icon = cursor.getInt(cursor.getColumnIndex(COLUMN_B1ICON));

                String B2Title = cursor.getString(cursor.getColumnIndex(COLUMN_B2TITLE));
                int B2Icon = cursor.getInt(cursor.getColumnIndex(COLUMN_B2ICON));

                String B3Title = cursor.getString(cursor.getColumnIndex(COLUMN_B3TITLE));
                int B3Icon = cursor.getInt(cursor.getColumnIndex(COLUMN_B3ICON));

                cursor.close();
                database.close();
                helper.close();
                obj.setPairTitle(pairTitle);

                obj.setB1Title(B1Title);
                obj.setB1Icon(B1Icon);

                obj.setB2Title(B2Title);
                obj.setB2Icon(B2Icon);

                obj.setB3Title(B3Title);
                obj.setB3Icon(B3Icon);
            } else {
                Log.e(Util.TAG,"Interactive push button pair  " + pairTitle + " Not found");
                cursor.close();
                database.close();
                helper.close();
                return false;
            }
        }
        return true;
    }


    /**
     * Function when called send button pairs to server
     * call this function from access_data
     */
    public void submitButtonPairsToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject obj = new JSONObject();
                getButtonPairsForSending(obj);
                sendButtonPairsToServer(obj);
            }
        }).start();
    }

    /**
     * Functions returns json in a format which can be sent to server
     * @param btnPairs
     */
    private void getButtonPairsForSending(JSONObject btnPairs) {
        if (null == btnPairs) {
            Log.e(Util.TAG, "btnpairs is null in getButtonPairsForSending. returning..");
            return;
        }
        PushNotificationHelper helper = new PushNotificationHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();
        String query = "select * from " + BUTTON_PAIR_TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                String pairName = cursor.getString(cursor.getColumnIndex(COLUMN_BTNPAIRID));
                String b1 = cursor.getString(cursor.getColumnIndex(COLUMN_B1TITLE));
                String b2 = cursor.getString(cursor.getColumnIndex(COLUMN_B2TITLE));
                try {
                    JSONArray tmpArray = new JSONArray();
                    tmpArray.put(0, b1);
                    tmpArray.put(1, b2);
                    btnPairs.put(pairName, tmpArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }
        }
    }

    /**
     * Function send button pairs to server
     * @param btnPairs
     */
    private void sendButtonPairsToServer(final JSONObject btnPairs) {
        if (null == mContext)
            return;
        if(null==btnPairs)
            return;
        final String BUTTONPAIR = "button";
        if (Util.isNetworkConnected(mContext)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String app_key = Util.getAppKey(mContext);
                    String installid = Util.getInstallId(mContext);

                    JSONObject payload  = new JSONObject();
                    try {
                        payload.put(BUTTONPAIR, btnPairs);
                        payload.put(Util.INSTALL_ID, installid);
                    }catch(JSONException e){
                        Log.e(Util.TAG,"Exception while sending button pairs to server. returning");
                        e.printStackTrace();
                        return;
                    }
                    try {
                        String libVersion = Util.getLibraryVersion();
                        URL url = Util.getInteractivePushUrl(mContext);
                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        connection.setReadTimeout(10000);
                        connection.setConnectTimeout(15000);
                        connection.setRequestMethod("POST");
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setRequestProperty("X-Installid", installid);
                        connection.setRequestProperty("X-App-Key", app_key);
                        connection.setRequestProperty("User-Agent", app_key + "(" + libVersion + ")");
                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        if(payload.length()<=0){
                            return;
                        }
                        writer.write(payload.toString());
                        writer.flush();
                        writer.close();
                        os.close();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String answer = null;
                        if (null != reader) {
                            answer = reader.readLine();
                        }
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            Log.w(Util.TAG, "Failed to send Interactive push button pairs " + connection.getResponseCode() + " " + answer);
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
}

