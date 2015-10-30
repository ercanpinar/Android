package library.streethawk.com.appmarshmallow;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.streethawk.library.beacon.Beacons;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.growth.Growth;
import com.streethawk.library.growth.IGrowth;
import com.streethawk.library.locations.SHLocation;
import com.streethawk.library.push.ISHObserver;
import com.streethawk.library.push.Push;
import com.streethawk.library.push.PushDataForApplication;

import org.json.JSONObject;

public class MainActivity extends Activity implements ISHObserver,IGrowth {
    private final int PERMISSIONS_LOCATION = 0;
    private final String TAG = "STREETHAWK_DEMO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Application app = getApplication();
        Push.getInstance(this).shAlertSetting(30);

       Push.getInstance(this).registerSHObserver(this);  //Register this class as implementation of ISHObserver
        // Enter your project number here (https://streethawk.freshdesk.com/solution/articles/5000608997)
        Push.getInstance(this).registerForPushMessaging("393009194749");
        // Enter APP_KEY for your application registered with StreetHawk server
        Growth.getInstance(this).getShareUrlForAppDownload("1", "shsample://setparams?param1=30", "facebook", "medium", "term", "cc", "www.google.com", new IGrowth() {
            @Override
            public void onReceiveShareUrl(String shareUrl) {

            }

            @Override
            public void onReceiveErrorForShareUrl(JSONObject errorResponse) {

            }
        });

        StreetHawk.INSTANCE.setAppKey("SHSample");
        Push.getInstance(this).setUseCustomDialog(true);
        StreetHawk.INSTANCE.init(app);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tagging cuid
        StreetHawk.INSTANCE.tagCuid("support@streethawk.com");

        // Start Beacon Monitoring.
        //Beacons.INSTANCE.startBeaconService();

    }

    /**
     * Check for location permission for Android MarshMallow
     * @param context
     * @return
     */
    private boolean checkForLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int coarseLocation = context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SHLocation.getInstance(this).startLocationReporting();
                    //SHGeofence.getInstance(this).startGeofenceMonitoring();


                } else {

                    Log.e(TAG, "Permission not granted by user");
                }
                return;
            }
        }
    }

    /**
     * Sample code for StreetHawk growth
     * @param view
     */
    public void Growth(View view){
        // Call originateShare API to generate and share universal link

        /*
        Growth.getInstance(this).originateShareWithCampaign("1", "shdemoapp://setparams?param1=45", new IGrowth() {
            @Override
            public void onReceiveShareUrl(final String shareUrl) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Intent intent = new Intent(Intent.ACTION_SEND);
                       intent.setType("text/plain");
                       intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       startActivity(intent);

                   }
               });

            }

            @Override
            public void onReceiveErrorForShareUrl(final JSONObject errorResponse) {

            }
        });
        */

        //Growth.getInstance(this).originateShareWithCampaign("1", "shdemoapp://setparams?param1=45",null);

        Growth.getInstance(this).originateShareWithCampaign("1", "shdemoapp://setparams?param1=45", null, null, null, null, "http://streethawk.com", null);
    }



    /**
     * Use originateShareWithCampaign to get the share URL.
     *
     * @param utm_campaign     Id to be used in StreetHawk Analytics (optional)
     * @param URI              deeplink uri of page to be opened when referred user installs the application on his device (optional)
     * @param utm_source       Source on which url will be posted (Example facebook, twitter whatsapp etc)
     * @param utm_medium       medium as url will be posted. (Example cpc)
     * @param utm_term         keywords for campaing
     * @param campaign_content contents of campaign
     * @param default_url      Fallback url if user opens url on non mobile devices.
     * @param object           instance of IStreetHawkGrowth. If null, the API automatically fires and intent with Intent.ACTION_SEND
     */


    /**
     * Sample code for asking permission and enabling location reporting for Andorid M devices.
     * @param v
     */
    public void StartLocationReporting(View v) {

        SHLocation.getInstance(this).startLocationWithPermissionDialog("Please give us the permisison we are good people and wont harm you :)");
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "Requesting permission");
            if(!checkForLocationPermission(this)) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_LOCATION);
            }
        } else {
            Log.e(TAG,"Not requesting permission "+Build.VERSION.SDK_INT+" "+Build.VERSION_CODES.M);
        }
        */
    }


    /**
     * Sample code for next screen analytics
     * @param view
     */
    public void NextActivity(View view){
        Intent intent = new Intent(this,SecondActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void startBeaconMonitoring(View view){
        Beacons.getInstance(this).startBeaconMonitoring();
    }





    @Override
    public void shReceivedRawJSON(String title, String message, String json) {

    }

    @Override
    public void shNotifyAppPage(String pageName) {

    }

    @Override
    public void onReceivePushData(PushDataForApplication pushData) {




    }

    @Override
    public void onReceiveResult(PushDataForApplication resultData, int result) {


    }


    @Override
    public void onReceiveShareUrl(String shareUrl) {

    }

    @Override
    public void onReceiveErrorForShareUrl(JSONObject errorResponse) {

    }
}
