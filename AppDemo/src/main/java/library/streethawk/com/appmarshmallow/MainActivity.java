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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.streethawk.library.core.ISHEventObserver;
import com.streethawk.library.core.StreetHawk;
import com.streethawk.library.growth.Growth;
import com.streethawk.library.growth.IGrowth;
import com.streethawk.library.locations.SHLocation;
import com.streethawk.library.push.InteractivePush;
import com.streethawk.library.push.Push;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Activity implements ISHEventObserver,IGrowth{
    private final int PERMISSIONS_LOCATION = 0;
    private final String TAG = "STREETHAWK_DEMO";

    private final String APP_KEY = "MyFirstApp";
    private final String SERVER  = " DEV";
    TextView installId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Application app = getApplication();
        installId = (TextView) findViewById(R.id.installid);
       // Push.getInstance(this).shAlertSetting(30);

        // Enter your project number here (https://streethawk.freshdesk.com/solution/articles/5000608997)
       Push.getInstance(this).registerForPushMessaging("491295755890");


        ArrayList<InteractivePush> appPairs = new ArrayList<InteractivePush>();

        appPairs.add(new InteractivePush("Yo", Push.getInstance(this).getIcon("shaccept"), "TO", Push.getInstance(this).getIcon("shcancel"), "appPair"));
        appPairs.add(new InteractivePush("1o", Push.getInstance(this).getIcon("shaccept"), "O1", Push.getInstance(this).getIcon("shcancel"), "appPair2"));
        appPairs.add(new InteractivePush("2o", Push.getInstance(this).getIcon("shaccept"), "O2", Push.getInstance(this).getIcon("shcancel"), "appPair3"));
        appPairs.add(new InteractivePush("3o", Push.getInstance(this).getIcon("shaccept"), "O3", Push.getInstance(this).getIcon("shcancel"), "appPair4"));
        appPairs.add(new InteractivePush("4o", Push.getInstance(this).getIcon("shaccept"), "O4", Push.getInstance(this).getIcon("shcancel"), "appPair5"));
        appPairs.add(new InteractivePush("15o", Push.getInstance(this).getIcon("shaccept"), "1O4", Push.getInstance(this).getIcon("shcancel"), "appPair15"));
        appPairs.add(new InteractivePush("15o", "1O4", "appPair15"));
        appPairs.add(new InteractivePush("NoIcon", -1, "ICON", Push.getInstance(this).getIcon("shcancel"), "iconnoicon"));

        Push.getInstance(this).setInteractivePushBtnPairs(appPairs);
        Growth.getInstance(this).registerIGrowth(this);


        // Enter APP_KEY for your application registered with StreetHawk server


       // StreetHawk.INSTANCE.tagString("sh_cuid",Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

        StreetHawk.INSTANCE.registerEventObserver(this);
        StreetHawk.INSTANCE.setAppKey(APP_KEY);
        StreetHawk.INSTANCE.init(app);


        int UPDATE_INTERVAL_FG = 2;
        int UPDATE_DISTANCE_FG = 100;
        int UPDATE_INTERVAL_BG =  5;
        int UPDATE_DISTANCE_BG = 500;


        SHLocation.getInstance(this).updateLocationMonitoringParams( UPDATE_INTERVAL_FG,  UPDATE_DISTANCE_FG,  UPDATE_INTERVAL_BG,  UPDATE_DISTANCE_BG);
    }


    public void SendTag(View view){

        EditText keyEt = (EditText)findViewById(R.id.key);
        String key = keyEt.getText().toString();
        EditText ValueEt = (EditText)findViewById(R.id.value);
        String value = ValueEt.getText().toString();

        if(key!=null && value!=null){
            if((!key.isEmpty()) && (!value.isEmpty()) ){

                double dval;
                try{
                    dval = Double.parseDouble(value);
                    StreetHawk.INSTANCE.tagNumeric(key,dval);
                    Toast.makeText(this,"Tagged Numeric "+key+" "+dval,Toast.LENGTH_LONG).show();
                    return;
                }catch(NumberFormatException e){
                    Log.e(TAG,"Value is a string");
                }
                StreetHawk.INSTANCE.tagString(key,value);
                Toast.makeText(this,"Tagged String "+key+" "+value,Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        installId.setText( " "+StreetHawk.INSTANCE.getInstallId(this));

        TextView appkey = (TextView) findViewById(R.id.appkey);
        appkey.setText(" "+APP_KEY);

        TextView server = (TextView) findViewById(R.id.server);
        server.setText(SERVER);

        Intent intent = new Intent(this,AppService.class);
        startService(intent);

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
                    //SHLocation.getInstance(this).startLocationReporting();
                  //  SHGeofence.getInstance(this).startGeofenceMonitoring();


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
    public void Growth(View view) {
        Intent intent = new Intent(this,NewGrowthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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

        //SHLocation.getInstance(this).startLocationWithPermissionDialog("Please give us the permisison we are good people and wont harm you :)");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "Requesting permission");
            if(!checkForLocationPermission(this)) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_LOCATION);
            }
        } else {
            Log.e(TAG,"Not requesting permission "+Build.VERSION.SDK_INT+" "+Build.VERSION_CODES.M);
        }

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

    public void ThirdActivity(View view){
        Intent intent = new Intent(getApplicationContext(),ThirdActivity.class);
        startActivity(intent);
    }



    public void startBeaconMonitoring(View view){
//        Beacons.getInstance(this).startBeaconMonitoring();
    }

    @Override
    public void onInstallRegistered(String install_id){
       //installId.setText(" "+install_id);
    }

    @Override
    public void onReceiveShareUrl(String shareUrl) {

    }

    @Override
    public void onReceiveErrorForShareUrl(JSONObject errorResponse) {

    }

    @Override
    public void onReceiveDeepLinkUrl(String deeplinkUrl) {
        Log.e("Anurag","deeplinkUrl"+deeplinkUrl);
    }
}
