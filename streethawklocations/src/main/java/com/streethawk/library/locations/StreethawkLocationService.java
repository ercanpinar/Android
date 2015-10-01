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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.streethawk.library.core.Logging;
import com.streethawk.library.core.Util;

public class StreethawkLocationService extends Service {
    private static GoogleApiClient mGoogleApiClient;

    private final int DEFAULT_UPDATE_DISTANCE_BG = 500;                // 	500 meters in background
    private final int DEFAULT_UPDATE_INTERVAL_FG = 2 * 60 * 1000;        // 2 minute in Foreground
    private final int DEFAULT_UPDATE_DISTANCE_FG = 100;                // 	100 meters in background
    private final int DEFAULT_UPDATE_INTERVAL_BG = 6 * 60 * 1000;        // 6 minutes in background

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
        }else {
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
        Context context = getApplicationContext();
        if (getUseLocation(context)) {
            if (checkForLocationPermission(context)) {
                startLocationReporting(context);
            } else {
                this.stopSelf();
            }
        } else {
            this.stopSelf();
        }
    }

    private com.google.android.gms.location.LocationListener getLocationListener(final Context context) {
        return new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                if (null == context)
                    return;
                if (!Util.isNetworkConnected(context))
                    return;
                Bundle extras = new Bundle();
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                extras.putString(Util.CODE, Integer.toString(Constants.CODE_LOCATION_UPDATES));
                extras.putString(Util.SHMESSAGE_ID, null);
                extras.putString(Constants.LOCAL_TIME, Util.getFormattedDateTime(System.currentTimeMillis(), false));
                extras.putString(Constants.SHLATTITUDE, Double.toString(lat));
                extras.putString(Constants.SHLONGITUDE, Double.toString(lng));
                Logging manager = Logging.getLoggingInstance(context);
                manager.addLogsForSending(extras);
            }
        };
    }

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(Util.TAG, SUBTAG + "Connection failed Error code" + connectionResult.getErrorCode());
        }
    };

    private GoogleApiClient.ConnectionCallbacks getConnectionCallback(final Context context) {
        return new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                boolean isFg = !(Util.isAppBG(context));
                try {
                    LocationRequest locationRequest = new LocationRequest().create()
                            .setInterval(isFg ? DEFAULT_UPDATE_INTERVAL_FG : DEFAULT_UPDATE_INTERVAL_BG)
                            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                            .setSmallestDisplacement(isFg ? DEFAULT_UPDATE_DISTANCE_FG : DEFAULT_UPDATE_DISTANCE_BG);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, getLocationListener(context));
                } catch (Exception e) {
                    // Handling exception as issue location returns true for mContext = null
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        };
    }

    public Location getLastKnownLocation() {
        Location location = null;
        if (null != mGoogleApiClient) {
            if (mGoogleApiClient.isConnected()) {
                location = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
            }
        }
        return location;
    }


    public void startLocationReporting(Context context) {
        if (null != mGoogleApiClient) {
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(getConnectionCallback(context))
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .build();
        mGoogleApiClient.connect();
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
