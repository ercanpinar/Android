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

/**
 * Beacon paramaters
 */
public class BeaconData {
    private String mUUID;
    private int mMajorNumber;
    private int mMinorNumber;
    private String mBeaconId;
    private double mDistance;

    /**
     * Set UUID of the beacon to be monitored
     * @param UUID UUID of beacon
     */
    public void setUUID(String UUID){ this.mUUID = UUID;}

    /**
     * Set ID of beaon to be monitored
     * @param beaconId ID of beacon as assigned by StreetHawk server
     */
    public void setBeaconId(String beaconId){ this.mBeaconId = beaconId;}

    /**
     * set major number of beaon to be monitored
     * @param major major nmber of beacon
     */
    public void setMajorNumber(int major){this.mMajorNumber = major;}

    /**
     * set minor number of beacon to be monitored
     * @param minor minor number of beacon
     */
    public void setMinorNumber(int minor){this.mMinorNumber = minor;}

    /**
     * Set distance of the beacon to be monitored
     * @param distance
     */
    public void setDistance(double distance){this.mDistance = distance;}


    /**
     * Function returns UUID of beacon
     * @return UUID
     */
    public String getUUID(){return mUUID;}

    /**
     * Function returns beacon ID assigned by StreetHawk server
     * @return beaconID
     */
    public String getBeaconId(){return mBeaconId;}

    /**
     * Function returns major number of the beacon
     * @return majorNumber
     */
    public int getMajorNumber(){return mMajorNumber;}

    /**
     * Function returns minor number of beacon
     * @return minor number
     */
    public int getMinorNumber(){return mMinorNumber;}

    /**
     * Return distance of the beacon
     * @return distance
     */
    public double getDistance(){return mDistance;}

}
