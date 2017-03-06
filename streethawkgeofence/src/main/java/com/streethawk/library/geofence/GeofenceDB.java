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
package com.streethawk.library.geofence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

class GeofenceDB {
    class GeofenceHelper extends SHSqliteBase {
        private GeofenceHelper(Context context) {
            super(context);
        }

        protected static final String GEOFENCE_TABLE_NAME = "geofence";
        protected static final String COLUMN_GEOFENCEID = "id";
        protected static final String COLUMN_LATITUDE = "latitude";
        protected static final String COLUMN_LONGITUDE = "longitude";
        protected static final String COLUMN_RADIUS = "radius";
        protected static final String COLUMN_PARENT = "parent";
        protected static final String COLUMN_NODE = "geofences";

        @Override
        public void onCreate(SQLiteDatabase database) {
            super.onCreate(database);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            super.onUpgrade(database, oldVersion, newVersion);
        }
    }// End class push notification helper

    private SQLiteDatabase mDatabase;
    private GeofenceHelper mDbHelper;
    private Context mContext;

    public GeofenceDB(Context context) {
        this.mContext = context;
        mDbHelper = new GeofenceHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
        mDatabase.close();
    }

    public void storeGeofenceData(GeofenceData object) {
        ContentValues values = new ContentValues();
        values.put(GeofenceHelper.COLUMN_GEOFENCEID, object.getGeofenceID());
        values.put(GeofenceHelper.COLUMN_LATITUDE, object.getLatitude());
        values.put(GeofenceHelper.COLUMN_LONGITUDE, object.getLongitude());
        values.put(GeofenceHelper.COLUMN_RADIUS, object.getRadius());
        values.put(GeofenceHelper.COLUMN_PARENT, object.getParentID());
        values.put(GeofenceHelper.COLUMN_NODE, object.getChildNode());
        mDatabase.insert(GeofenceHelper.GEOFENCE_TABLE_NAME, null, values);
    }

    /**
     * Call forceDeleteAllRecords when app status states to reset data
     */

    public void forceDeleteAllRecords() {
        mDatabase.execSQL("delete from " + GeofenceHelper.GEOFENCE_TABLE_NAME);
    }


    /**
     * Function will return all the nodes with parentId = null
     *
     * @param geofenceList
     * @return
     */
    public boolean getGeofenceListToMonitor(ArrayList<GeofenceData> geofenceList) {
        GeofenceHelper helper = new GeofenceHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();
        String query;
        query = "select * from " + GeofenceHelper.GEOFENCE_TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                double lat = cursor.getDouble(cursor.getColumnIndex(GeofenceHelper.COLUMN_LATITUDE));
                double lng = cursor.getDouble(cursor.getColumnIndex(GeofenceHelper.COLUMN_LONGITUDE));
                float radius = cursor.getFloat(cursor.getColumnIndex(GeofenceHelper.COLUMN_RADIUS));
                String hasNodes = cursor.getString(cursor.getColumnIndex(GeofenceHelper.COLUMN_NODE));
                String geofenceID = cursor.getString(cursor.getColumnIndex(GeofenceHelper.COLUMN_GEOFENCEID));
                if (!geofenceID.startsWith("_")) {
                    geofenceList.add(new GeofenceData()
                            .setGeofenceID(geofenceID)
                            .setLatitude(lat)
                            .setLongitude(lng)
                            .setRadius(radius)
                            .setParentID(null)
                            .setChildNodes(Boolean.parseBoolean(hasNodes))
                    );
                }
                cursor.moveToNext();
            }
            cursor.close();
            database.close();
            helper.close();
        } else {
            cursor.close();
            database.close();
            helper.close();
            return false;
        }
        return true;
    }

    /**
     * Call getMatchedGeofenceData when os triggers user entering a geofence
     *
     * @param geofenceID
     * @param obj
     * @return
     */
    public boolean getMatchedGeofenceData(final String geofenceID, final GeofenceData obj) {
        GeofenceHelper helper = new GeofenceHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();
        if (null == geofenceID) {
            database.close();
            helper.close();
            return false;
        } else {
            String query = "select * from " + GeofenceHelper.GEOFENCE_TABLE_NAME +
                    " where " + GeofenceHelper.COLUMN_GEOFENCEID + " = '" + geofenceID + "'";
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                double lat = cursor.getDouble(cursor.getColumnIndex(GeofenceHelper.COLUMN_LATITUDE));
                double lng = cursor.getDouble(cursor.getColumnIndex(GeofenceHelper.COLUMN_LONGITUDE));
                float radius = cursor.getFloat(cursor.getColumnIndex(GeofenceHelper.COLUMN_RADIUS));
                String hasNodes = cursor.getString(cursor.getColumnIndex(GeofenceHelper.COLUMN_NODE));
                String parent = cursor.getString(cursor.getColumnIndex(GeofenceHelper.COLUMN_PARENT));
                cursor.close();
                database.close();
                helper.close();
                obj.setGeofenceID(geofenceID)
                        .setLatitude(lat)
                        .setLongitude(lng)
                        .setRadius(radius)
                        .setParentID(parent)
                        .setChildNodes(Boolean.parseBoolean(hasNodes));
            } else {
                Log.e("getMatchedGeofenceData", "Geofence with " + geofenceID + " Not found");
                cursor.close();
                database.close();
                helper.close();
                return false;
            }
        }
        return true;
    }
}