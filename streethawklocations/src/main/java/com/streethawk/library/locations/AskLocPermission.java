package com.streethawk.library.locations;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.streethawk.library.core.Util;

public class AskLocPermission extends Activity implements Constants {
    Activity mActivity;
    boolean showDialog = false;
    private final String SUBTAG = "AskLocPermission ";
    private final int PERMISSIONS_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_loc_permission);
        showDialog = getIntent().getBooleanExtra(PERMISSION_BOOL,false);
    }

    public void onResume(){
        super.onResume();
        if(showDialog) {
            displayPermissionDialog();
        }
        mActivity = this;
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

    private DialogInterface.OnClickListener askPermission(){
        return new DialogInterface.OnClickListener(){
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_LOCATION);
            }
        };
    }


    private void displayPermissionDialog(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!checkForLocationPermission(this)) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    String DEFAULT_PERMISSION_TITLE   = "Permission Required";
                    String DEFAULT_PERMISSION_MESSAGE = "Would you like to grant us location permission for monitoring geofences?";
                    String DEFAULT_BUTTON_TEXT        = "Okay";
                    int id;
                    String title;
                    String message;
                    String buttonTitle;

                    Context context = mActivity.getApplicationContext();
                    String packageName = context.getPackageName();
                    id = context.getResources().getIdentifier("SH_LOC_PERMISSION_TITLE", "string", packageName);
                    if (0 == id)
                        title = DEFAULT_PERMISSION_TITLE;
                    else
                        title = context.getString(id);

                    id = context.getResources().getIdentifier("SH_LOC_PERMISSION_MESSAGE", "string", packageName);
                    if (0 == id)
                        message = DEFAULT_PERMISSION_MESSAGE;
                    else
                        message = context.getString(id);

                    id = context.getResources().getIdentifier("SH_LOC_PERMISSION_BUTTON_TEXT", "string", packageName);
                    if (0 == id)
                        buttonTitle = DEFAULT_BUTTON_TEXT;
                    else
                        buttonTitle = context.getString(id);

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(title);
                    builder.setMessage(message);
                    builder.setPositiveButton(buttonTitle,askPermission());
                }else{
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_LOCATION);
                }
            }else{
                Log.i(Util.TAG, SUBTAG + "App already has the permission");
                finish();
            }
        } else {
            Log.e(Util.TAG,SUBTAG+"Not requesting permission "+Build.VERSION.SDK_INT+" "+Build.VERSION_CODES.M);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SHLocation.getInstance(this).startLocationReporting();
                } else {

                    Log.e(Util.TAG, "Permission not granted by user");
                }
            }
        }
        finish();
    }

}
