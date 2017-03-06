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

import android.util.Log;

import com.streethawk.library.core.Util;

public class GeofenceData {
    private final String SUBTAG = "GeofenceData ";
    private String mGeofenceID;        // Unique identifier of geofence
    private double mLatitude;          // Latitude of geofence
    private double mLongitude;         // Longitude of geofence
    private float mRadius;            // Radius of geofence
    private String mParent;            // ID of parent node of geofence. Null is Root node
    private boolean mHasChildNodes;     // Boolean specifying if current geofence has nodes.
    private double mDistance = 0.0;      // Distance from current location


    public GeofenceData setGeofenceID(String ID) {
        this.mGeofenceID = ID;
        return this;
    }

    public GeofenceData setLatitude(double latitude) {
        this.mLatitude = latitude;
        return this;
    }

    public GeofenceData setLongitude(double longitude) {
        this.mLongitude = longitude;
        return this;
    }

    public GeofenceData setRadius(float radius) {
        this.mRadius = radius;
        return this;
    }

    public GeofenceData setParentID(String ID) {
        this.mParent = ID;
        return this;
    }

    public GeofenceData setChildNodes(boolean answer) {
        this.mHasChildNodes = answer;
        return this;
    }

    public GeofenceData setDistance(double distance) {
        this.mDistance = distance;
        return this;
    }

    public String getGeofenceID() {
        return this.mGeofenceID;
    }

    public double getLatitude() {
        return this.mLatitude;
    }

    public double getLongitude() {
        return this.mLongitude;
    }

    public float getRadius() {
        return this.mRadius <= 0 ? 10 : this.mRadius;
    }

    public String getParentID() {
        return this.mParent;
    }

    public String getChildNode() {
        return Boolean.toString(this.mHasChildNodes);
    }

    public double getDistance() {
        return mDistance;
    }


    public void displayMyData() {
        String NEWLINE = "\n";
        String myData = "ID: " + this.mGeofenceID + NEWLINE +
                "Latitude: " + this.mLatitude + NEWLINE +
                "Longitude: " + this.mLongitude + NEWLINE +
                "Radius: " + this.mRadius + NEWLINE +
                "ParentID: " + this.mParent + NEWLINE +
                "HasChild: " + this.mHasChildNodes + NEWLINE +
                "Distance from current location: " + this.mDistance;
        Log.i(Util.TAG, SUBTAG + myData);
    }
}
