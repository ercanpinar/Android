package streethawk.com.pushping;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.streethawk.library.beacon.Beacons;
import com.streethawk.library.geofence.SHGeofence;
import com.streethawk.library.locations.SHLocation;

public class StartBGL extends AppCompatActivity implements Constants {


    private Button mBtn;
    private String mActivityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_bgl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String activity = getIntent().getStringExtra(ACTIVITY);
        mBtn = (Button) findViewById(R.id.startstop);
        if (null != activity) {
            mActivityName = activity;
            switch (activity) {
                case ACTiVITY_BEACON:
                    toolbar.setTitle("Beacon");
                    showBtnText(BEACON, getFlagStatus(STATUS_BEACON));
                    break;
                case ACTiVITY_LOCATION:
                    toolbar.setTitle("Location");
                    showBtnText(LOCATION, getFlagStatus(STATUS_LOCATION));
                    break;
                case ACTiVITY_GEOFENCE:
                    toolbar.setTitle("Geofence");
                    showBtnText(GEOFENCE, getFlagStatus(STATUS_GEOFENCE));
                    break;
                default:
                    break;
            }
        }
    }

    private void setFlagStatus(String flag, boolean status) {
        SharedPreferences.Editor e = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).edit();
        e.putBoolean(flag, status);
        e.commit();
    }

    private boolean getFlagStatus(String flag) {
        return getSharedPreferences(APP_PREF, Context.MODE_PRIVATE).getBoolean(flag, false);
    }


    private void showBtnText(String activity, boolean flag) {
        if (flag)
            mBtn.setText(STOP + activity);
        else
            mBtn.setText(START + activity);
    }

    public void toggleStatus(View v) {
        boolean flag = false;
        switch (mActivityName) {
            case ACTiVITY_BEACON:
                flag = getFlagStatus(STATUS_BEACON);
                if (flag) {
                    Beacons.getInstance(getApplicationContext()).stopBeaconMonitoring();
                    mBtn.setText(STOP + ACTiVITY_BEACON);
                    setFlagStatus(STATUS_BEACON, false);
                    showBtnText(ACTiVITY_BEACON, false);
                } else {
                    Beacons.getInstance(getApplicationContext()).startBeaconMonitoring();
                    mBtn.setText(START + ACTiVITY_BEACON);
                    setFlagStatus(STATUS_BEACON, true);
                    showBtnText(ACTiVITY_BEACON, true);
                }
                break;
            case ACTiVITY_LOCATION:
                flag = getFlagStatus(STATUS_LOCATION);
                if (flag) {
                    SHLocation.getInstance(getApplicationContext()).stopLocationReporting();
                    mBtn.setText(STOP + ACTiVITY_LOCATION);
                    setFlagStatus(STATUS_LOCATION, false);
                    showBtnText(ACTiVITY_LOCATION, false);
                } else {
                    SHLocation.getInstance(getApplicationContext()).startLocationWithPermissionDialog();
                    mBtn.setText(START + ACTiVITY_LOCATION);
                    setFlagStatus(STATUS_LOCATION, true);
                    showBtnText(ACTiVITY_LOCATION, true);
                }
                break;
            case ACTiVITY_GEOFENCE:
                flag = getFlagStatus(STATUS_GEOFENCE);
                if (flag) {
                    SHGeofence.getInstance(getApplicationContext()).stopMonitoring();
                    mBtn.setText(STOP + ACTiVITY_GEOFENCE);
                    setFlagStatus(STATUS_GEOFENCE, false);
                    showBtnText(ACTiVITY_GEOFENCE, false);
                } else {
                    Log.e("Anurag", "Start geofence with permission dialog");
                    SHGeofence.getInstance(getApplicationContext()).startGeofenceWithPermissionDialog();
                    mBtn.setText(START + ACTiVITY_GEOFENCE);
                    setFlagStatus(STATUS_GEOFENCE, true);
                    showBtnText(ACTiVITY_GEOFENCE, true);
                }
                break;
            default:
                break;
        }
    }
}
