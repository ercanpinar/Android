package com.streethawk.library.geofence;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.streethawk.library.core.Util;

/**
 * Location service to fetch user's location
 */
public class StreetHawkLocationService extends Service implements Constants{
        LocationManager locationManager;
        Context mContext;
        private static StreetHawkLocationService mStreethawkLocationService;
        public static StreetHawkLocationService getInstance() {
            if (null == mStreethawkLocationService)
                mStreethawkLocationService = new StreetHawkLocationService();
            return mStreethawkLocationService;
        }

        public StreetHawkLocationService() {
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
                return sharedPreferences.getBoolean(SHLOCATION_FLAG, false);
            else
                return false;
        }

        @Override
        public void onCreate() {
            mContext = getApplicationContext();
            if (getUseLocation(mContext)) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                if (!checkForLocationPermission(mContext)) {
                    this.stopSelf();
                } else {
                    this.stopSelf();
                }
            }
        }

    public void StoreLocationsForLogging(Context context){
        Location location = getLastKnownLocation(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(Util.LOG_LAT,Double.toString(location.getLatitude()));
        e.putString(Util.LOG_LNG,Double.toString(location.getLongitude()));
        e.commit();
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

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
