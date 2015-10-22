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

package com.streethawk.library.locations;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

public class StreethawkLocationService extends Service {
    private final int DEFAULT_UPDATE_DISTANCE_BG = 500;                // 	500 meters in background
    private final int DEFAULT_UPDATE_INTERVAL_FG = 2 * 60 * 1000;        // 2 minute in Foreground
    private final int DEFAULT_UPDATE_DISTANCE_FG = 100;                // 	100 meters in background
    private final int DEFAULT_UPDATE_INTERVAL_BG = 6 * 60 * 1000;        // 6 minutes in background
    private static Context mContext;

    LocationManager locationManager;
    LocationListener locationListener;

    private static int VALUE_UPDATE_INTERVAL_BG = 0;                    // 6 minutes in background
    private static int VALUE_UPDATE_DISTANCE_BG = 0;                    // 	500 meters in background
    private static int VALUE_UPDATE_INTERVAL_FG = 0;                    // 2 minute in Foreground
    private static int VALUE_UPDATE_DISTANCE_FG = 0;                    // 	500 meters in background

    private static StreethawkLocationService mStreethawkLocationService;
    private final String SUBTAG = "StreethawkLocationService ";


    public static StreethawkLocationService getInstance() {
        if (null == mStreethawkLocationService)
            mStreethawkLocationService = new StreethawkLocationService();
        return mStreethawkLocationService;
    }

    public StreethawkLocationService() {
    }


    private boolean checkForLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int coarseLocation = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int fineLocation = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if ((coarseLocation == PackageManager.PERMISSION_GRANTED) || (fineLocation == PackageManager.PERMISSION_GRANTED)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    private boolean getUseLocation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        if (sharedPreferences != null)
            return sharedPreferences.getBoolean(Constants.SHLOCATION_FLAG, false);
        else
            return false;
    }


    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        if (getUseLocation(mContext)) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (checkForLocationPermission(mContext)) {
                startLocationReporting(mContext);
            } else {
                this.stopSelf();
            }
        } else {
            this.stopSelf();
        }
    }


    public Location getLastKnownLocation(Context context){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider=null;
        try {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationProvider = LocationManager.GPS_PROVIDER;
            }
        } catch(Exception ex) {
            locationProvider = null;
        }

        try {
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }
        } catch(Exception ex) {
            locationProvider = null;
        }
        if(null==locationProvider)
            return null;
        try {
            return locationManager.getLastKnownLocation(locationProvider);
        }catch(SecurityException e){
            return null;
        }
    }

    public LocationListener setLocationListener() {
        if (null == locationListener) {
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Bundle extras = new Bundle();
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    extras.putString(Util.CODE, Integer.toString(Constants.CODE_LOCATION_UPDATES));
                    extras.putString(Util.SHMESSAGE_ID, null);
                    extras.putString(Constants.LOCAL_TIME, Util.getFormattedDateTime(System.currentTimeMillis(), false));
                    extras.putString(Constants.SHLATTITUDE, Double.toString(lat));
                    extras.putString(Constants.SHLONGITUDE, Double.toString(lng));
                    Logging manager = Logging.getLoggingInstance(mContext);
                    manager.addLogsForSending(extras);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }
        return locationListener;
    }


    public void startLocationReporting(final Context context) {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider=null;
        try {
            if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                locationProvider = LocationManager.GPS_PROVIDER;
            }
        } catch(Exception ex) {
            locationProvider = null;
        }

        try {
            if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }
        } catch(Exception ex) {
            locationProvider = null;
        }
        if(null==locationProvider)
            return;
        try {
            if (null == locationManager) {
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            }
            if (null == locationManager) {
                Log.e(Util.TAG, SUBTAG + "Device doesn't support locations");
                return;
            }
            if (VALUE_UPDATE_INTERVAL_BG <= 0) {
                VALUE_UPDATE_INTERVAL_BG = DEFAULT_UPDATE_INTERVAL_BG;
            }
            if (VALUE_UPDATE_INTERVAL_FG <= 0) {
                VALUE_UPDATE_INTERVAL_FG = DEFAULT_UPDATE_INTERVAL_FG;
            }
            if (VALUE_UPDATE_DISTANCE_BG <= 0) {
                VALUE_UPDATE_DISTANCE_BG = DEFAULT_UPDATE_DISTANCE_BG;
            }
            if (VALUE_UPDATE_DISTANCE_FG <= 0) {
                VALUE_UPDATE_DISTANCE_FG = DEFAULT_UPDATE_DISTANCE_FG;
            }
            int distance;
            int interval;
            if (Util.isAppBG(context)) {
                distance = VALUE_UPDATE_INTERVAL_BG;
                interval = DEFAULT_UPDATE_DISTANCE_BG;
            } else {
                distance = VALUE_UPDATE_INTERVAL_FG;
                interval = DEFAULT_UPDATE_DISTANCE_FG;
            }
            locationManager.requestLocationUpdates(locationProvider, distance, interval, setLocationListener());
        } catch (SecurityException e) {
            Log.e(Util.TAG, SUBTAG + "Location permission are not provided by yser");
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        if (null != intent) {
            VALUE_UPDATE_INTERVAL_BG = intent.getIntExtra(Constants.KEY_UPDATE_INTERVAL_BG, 0);
            VALUE_UPDATE_DISTANCE_BG = intent.getIntExtra(Constants.KEY_UPDATE_DISTANCE_BG, 0);
            VALUE_UPDATE_INTERVAL_FG = intent.getIntExtra(Constants.KEY_UPDATE_INTERVAL_FG, 0);
            VALUE_UPDATE_DISTANCE_FG = intent.getIntExtra(Constants.KEY_UPDATE_DISTANCE_FG, 0);
        }
        return null;
    }
}
