package com.streethawk.library.geofence;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Location service to fetch user's location
 */
public class StreetHawkLocationService extends Service implements Constants,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static Context mContext;
    private static StreetHawkLocationService mStreethawkLocationService;
    private PendingIntent mGeofencePendingIntent;
    private final int MAX_GEOFENCE_CNT = 20;
    private GoogleApiClient mGoogleApiClient;
    private static ArrayList<com.google.android.gms.location.Geofence> mGeofenceList;
    LocationManager locationManager;


    public static StreetHawkLocationService getInstance(Context context) {
        mContext = context;
        if (null == mStreethawkLocationService)
            mStreethawkLocationService = new StreetHawkLocationService();
        return mStreethawkLocationService;
    }

    public StreetHawkLocationService() {
    }

    private void registerScheduledTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean taskRegistered = (PendingIntent.getBroadcast(mContext, 0,
                        new Intent(BROADCAST_APP_STATUS_CHK),
                        PendingIntent.FLAG_NO_CREATE) != null);
                if (taskRegistered) {
                    return;
                }
                SharedPreferences pref = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = pref.edit();
                e.putLong(SHTASKTIME, System.currentTimeMillis());
                e.commit();
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, SHCoreModuleReceiver.class);
                intent.setAction(BROADCAST_APP_STATUS_CHK);
                intent.putExtra(SHPACKAGENAME, mContext.getPackageName());
                PendingIntent appStatusIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //long DEBUG_INTERVAL_2MINUTES = 120000l;
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, appStatusIntent);
                //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), DEBUG_INTERVAL_2MINUTES, appStatusIntent);

                if (Util.getSHDebugFlag(mContext)) {
                    Log.d(Util.TAG, "*** Running hourly task for locations ");
                }
            }
        }).start();
    }

    public void populateGeofenceList(ArrayList<GeofenceData> geofenceList) {
        mGeofenceList = new ArrayList<com.google.android.gms.location.Geofence>();
        getGeofenceInVicinity(geofenceList);
        for (GeofenceData obj : geofenceList) {
            mGeofenceList.add(new com.google.android.gms.location.Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(obj.getGeofenceID())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            obj.getLatitude(),
                            obj.getLongitude(),
                            obj.getRadius()
                    )
                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(com.google.android.gms.location.Geofence.NEVER_EXPIRE)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER | com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }

    public void storeGeofenceList(ArrayList<GeofenceData> geofenceList) {
        getNodesToMonitor(geofenceList);
    }

    /**
     * Function to stop monitoring of geofences
     */
    public void stopMonitoring() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(IS_GEOFENCE_ENABLE, false);
        e.commit();
        if (null != mGoogleApiClient) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.GeofencingApi.removeGeofences(
                        mGoogleApiClient,
                        // This is the same pending intent that was used in addGeofences().
                        getGeofencePendingIntent()
                )/*.setResultCallback(this)*/; // Result processed in onResult().
            }
        } else {
            Log.e(Util.TAG, "mGoogleApiClient is null in stopMonitoringExistingGeofence.Check...");
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    public void startGeofenceMonitoring() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                buildGoogleApiClient();
                mGoogleApiClient.connect();
                if (Util.getPlatformType() == Util.PLATFORM_XAMARIN) {
                    StreetHawk.INSTANCE.tagString("sh_module_geofence", "true");
                }
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sharedPreferences.edit();
                e.putBoolean(IS_GEOFENCE_ENABLE, true);
                e.commit();

            }
        }).start();

    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        registerScheduledTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != mGoogleApiClient)
            mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    public double[] getLocationForLogline(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
        }
        if (null != mGoogleApiClient) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                if (Util.getSHDebugFlag(context)) {
                    Log.d(Util.TAG, "  " + "Device Location" + lat + "," + lng);
                }
                return new double[]{lat, lng};
            }
            return null;
        } else {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putBoolean(IS_GEOFENCE_ENABLE, false);
        e.commit();
    }

    public void storeGeofenceData(JSONArray geofenceArray) {
        GeofenceDB storeGeofenceDB = new GeofenceDB(mContext);
        storeGeofenceDB.open();
        parseAndStoreGeofences(null, storeGeofenceDB, geofenceArray);
        ArrayList<GeofenceData> geofenceList = new ArrayList<GeofenceData>();
        storeGeofenceList(geofenceList);
        storeGeofenceDB.close();
    }

    private void getNodesToMonitor(ArrayList<GeofenceData> geofenceList) {
        GeofenceDB database = new GeofenceDB(mContext);
        database.open();
        database.getGeofenceListToMonitor(geofenceList);
        database.close();
    }

    /**
     * Converts degrees to radians
     *
     * @param deg
     * @return
     */
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * Converts radians to degrees
     *
     * @param rad
     * @return
     */
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * Calculates distance between two points using Haversine formula
     *
     * @param lat1 latitude of point 1
     * @param lng1 longitude of point 1
     * @param lat2 latitude of point 2
     * @param lng2 longitude of point 2
     * @return distance in meters
     */
    private double calculateDistanceUsingHaversine(double lat1, double lng1, double lat2, double lng2) {
        double theta = lng1 - lng2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344 * 1000;
        // Log.e("Anurag","Distance "+dist+" "+lat1+lat2+lng1+lng2);
        return (dist);
    }

    private void parseAndStoreGeofences(String parent, GeofenceDB storeGeofenceDB, JSONArray geofenceArray) {
        for (int i = 0; i < geofenceArray.length(); i++) {
            try {
                Object tmpObject = geofenceArray.get(i);

                if (tmpObject instanceof JSONObject) {
                    GeofenceData geofenceData = new GeofenceData();
                    JSONObject tmp = (JSONObject) tmpObject;
                    String geofenceId = tmp.getString(GeofenceDB.GeofenceHelper.COLUMN_GEOFENCEID);
                    geofenceData.setGeofenceID(geofenceId);
                    geofenceData.setLatitude(tmp.getDouble(GeofenceDB.GeofenceHelper.COLUMN_LATITUDE));
                    geofenceData.setLongitude(tmp.getDouble(GeofenceDB.GeofenceHelper.COLUMN_LONGITUDE));
                    geofenceData.setRadius((float) tmp.getDouble(GeofenceDB.GeofenceHelper.COLUMN_RADIUS));
                    geofenceData.setParentID(parent);
                    Object nodeObject = null;
                    try {
                        nodeObject = tmp.get(GeofenceDB.GeofenceHelper.COLUMN_NODE);
                    } catch (JSONException e) {
                        nodeObject = null;
                    }
                    if (null != nodeObject) {
                        if (nodeObject instanceof JSONArray) {
                            geofenceData.setChildNodes(true);
                            storeGeofenceDB.storeGeofenceData(geofenceData);
                            // Calling function recursively
                            parseAndStoreGeofences(geofenceId, storeGeofenceDB, (JSONArray) nodeObject);
                        } else {
                            geofenceData.setChildNodes(false);
                            storeGeofenceDB.storeGeofenceData(geofenceData);
                        }
                    } else {
                        geofenceData.setChildNodes(false);
                        storeGeofenceDB.storeGeofenceData(geofenceData);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Returns sorted list of total geofeneces
     * TODO: try to optimise for loops
     * TODO: Improve sorting algorithm
     *
     * @param geofenceList
     */
    private void getGeofenceInVicinity(ArrayList<GeofenceData> geofenceList) {
        if (null == geofenceList)
            return;
        if (geofenceList.size() <= MAX_GEOFENCE_CNT)
            return;
        else {

            double[] location = getLocationForLogline(mContext);

            int size = geofenceList.size();
            //Store distance from current location
            for (int i = 0; i < size; i++) {
                GeofenceData obj = geofenceList.get(i);
                double distance = calculateDistanceUsingHaversine(obj.getLatitude(), obj.getLongitude(), location[0], location[1]);
                obj.setDistance(distance);
            }

            // Bubble sort in increasing order of distance
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size - 1; j++) {
                    if (geofenceList.get(j).getDistance() > geofenceList.get(j + 1).getDistance()) {
                        GeofenceData swapObject = new GeofenceData();
                        swapObject = geofenceList.get(j);
                        geofenceList.set(j, geofenceList.get(j + 1));
                        geofenceList.set(j + 1, swapObject);
                    }
                }
            }
            geofenceList.subList(MAX_GEOFENCE_CNT, size).clear();
            return;
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(mContext, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    /**
     * Return time required to travel that distance
     *
     * @param distance
     * @return
     */
    private double getTimeToTravel(double distance) {
        final double avgSpeed = 16.6667;
        return (distance / avgSpeed);

    }

    private GeofencingRequest getGeofencingRequest() {
        ArrayList<GeofenceData> geofenceList = new ArrayList<GeofenceData>();
        getNodesToMonitor(geofenceList);
        populateGeofenceList(geofenceList);
        if (mGeofenceList != null) {
            if (!mGeofenceList.isEmpty()) {
                int size = mGeofenceList.size();
                if (size >= MAX_GEOFENCE_CNT) {
                    GeofenceData obj = geofenceList.get(size - 1);
                    if (getTimeToTravel(obj.getDistance()) < 3600) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean taskRegistered = (PendingIntent.getBroadcast(mContext, 0,
                                        new Intent(BROADCAST_APP_STATUS_CHK),
                                        PendingIntent.FLAG_NO_CREATE) != null);
                                if (taskRegistered) {
                                    return;
                                }
                                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(mContext, SHCoreModuleReceiver.class);
                                intent.setAction(BROADCAST_APP_STATUS_CHK);
                                intent.putExtra(SHPACKAGENAME, mContext.getPackageName());
                                PendingIntent appStatusIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                //long DEBUG_INTERVAL_2MINUTES = 120000l;
                                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, appStatusIntent);
                                //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), DEBUG_INTERVAL_2MINUTES, appStatusIntent);
                                if (Util.getSHDebugFlag(mContext)) {
                                    Log.d(Util.TAG, "*** Running hourly task for locations ");
                                }
                            }
                        }).start();
                    }
                }
                GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
                // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
                // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
                // is already inside that geofence.
                builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
                // Add the geofences to be monitored by geofencing service.
                builder.addGeofences(mGeofenceList);
                // Return a GeofencingRequest.
                return builder.build();
            }
        }
        return null;
    }

    public void storeGeofenceListForMonitoring(ArrayList<GeofenceData> geofenceList) {
        if (null != geofenceList) {
            getNodesToMonitor(geofenceList);
            populateGeofenceList(geofenceList);
        }
    }

    protected void monitorGeofence() {
        GeofencingRequest request = getGeofencingRequest();
        if (null == request) {
            Log.e(Util.TAG, "getGeofencingRequest returned null");
            return;
        }
        if (mGoogleApiClient.isConnected()) {
            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        // The GeofenceRequest object.
                        request,
                        // A pending intent that that is reused when calling removeGeofences(). This
                        // pending intent is used to generate an intent when a matched geofence
                        // transition is observed.
                        getGeofencePendingIntent()
                )/*.setResultCallback(this)*/; // Result processed in onResult().
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                securityException.printStackTrace();
            }
        } else {
            Log.e(Util.TAG, "mGoogleApiClient not connected in startGeofenceMonitoring");
        }
    }

    public Location getLastKnownLocation(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = null;
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
            }
        } catch (Exception ex) {
            locationProvider = null;
        }
        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }
        } catch (Exception ex) {
            locationProvider = null;
        }
        if (null == locationProvider)
            return null;
        try {
            return locationManager.getLastKnownLocation(locationProvider);
        } catch (SecurityException e) {
            return null;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        monitorGeofence();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect();
        }
    }
}
