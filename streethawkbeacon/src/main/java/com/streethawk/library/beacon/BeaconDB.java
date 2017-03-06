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

package com.streethawk.library.beacon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Database to store beacon data
 */
class BeaconDB {
    /**
     * Helper class for storing beacon data
     */
    class BeaconDBHelper extends SHSqliteBase {

        public static final String BEACON_TABLE_NAME = "beacondata";
        public static final String COLUMN_BEACONID = "beaconid";
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_MAJOR_NUMBER = "majorno";
        public static final String COLUMN_MINOR_NUMBER = "minorno";

        private BeaconDBHelper(Context context) {
            super(context);

        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            super.onCreate(database);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + BEACON_TABLE_NAME);
            onCreate(database);
        }
    } // End of BeaconDBHelper Class

    private SQLiteDatabase mDatabase;
    private BeaconDBHelper mDbHelper;
    private Context mContext;
    private static BeaconDB instance = null;
    private final String SINGLE_QUOTE = "'";
    private final String DOUBLE_QUOTE = "\"";


    /**
     * get instance of BeaconDB class
     *
     * @param context application context
     * @return instance of BeaconDB class
     */
    public static BeaconDB getInstance(Context context) {
        if (null == instance) {
            instance = new BeaconDB(context);
        }
        return instance;
    }

    private BeaconDB(Context context) {
        this.mContext = context;
        mDbHelper = new BeaconDBHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
        mDatabase.close();
    }

    public void storeBeaconData(List<BeaconData> objectList) {
        ContentValues values = new ContentValues();
        for (BeaconData object : objectList) {
            values.put(BeaconDBHelper.COLUMN_BEACONID, object.getBeaconId());
            values.put(BeaconDBHelper.COLUMN_UUID, object.getUUID().toLowerCase());
            values.put(BeaconDBHelper.COLUMN_MAJOR_NUMBER, object.getMajorNumber());
            values.put(BeaconDBHelper.COLUMN_MINOR_NUMBER, object.getMinorNumber());
            mDatabase.insert(BeaconDBHelper.BEACON_TABLE_NAME, null, values);
        }
        mDatabase.close();
    }

    public void deleteBeaconData() {
        mDatabase.delete(BeaconDBHelper.BEACON_TABLE_NAME, null, null);
    }

    public ArrayList<String> getListOfUniqueUUID() {
        HashSet<String> beaconUUID = new HashSet<String>(); //using hashset as it will avoid duplicate uuids
        BeaconDBHelper helper = new BeaconDBHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();
        String query = "select * from " + BeaconDBHelper.BEACON_TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                beaconUUID.add(cursor.getString(cursor.getColumnIndex(BeaconDBHelper.COLUMN_UUID)));
                cursor.moveToNext();
            }
            ArrayList<String> list = new ArrayList<String>(beaconUUID);
            cursor.close();
            return list;
        }
        cursor.close();
        return null;
    }


    public String getBeaconId(String UUID, int major, int minor) {
        BeaconDBHelper helper = new BeaconDBHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();
        if (null == UUID) {
            database.close();
            helper.close();
            return null;
        } else {
            UUID = UUID.toLowerCase();
            String query = "select * from " + BeaconDBHelper.BEACON_TABLE_NAME +
                    " where " + BeaconDBHelper.COLUMN_UUID + " = " + DOUBLE_QUOTE + UUID.trim() + DOUBLE_QUOTE +
                    " and " + BeaconDBHelper.COLUMN_MAJOR_NUMBER + " = " + SINGLE_QUOTE + Integer.toString(major) + SINGLE_QUOTE +
                    " and " + BeaconDBHelper.COLUMN_MINOR_NUMBER + " = " + SINGLE_QUOTE + Integer.toString(minor) + SINGLE_QUOTE;
            try {
                Cursor cursor = database.rawQuery(query, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String beaconId = cursor.getString(cursor.getColumnIndex(BeaconDBHelper.COLUMN_BEACONID));
                    database.close();
                    return beaconId;
                } else {
                    cursor.close();
                    database.close();
                    helper.close();
                    return null;
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
                database.close();
                helper.close();
                return null;
            }
        }
    }
}
