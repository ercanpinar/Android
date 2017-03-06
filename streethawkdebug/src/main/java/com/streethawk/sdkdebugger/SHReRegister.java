package com.streethawk.sdkdebugger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class SHReRegister extends Activity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shre_register);
    }


    public void onRadioButtonClicked(View view) {
        final String PROD_URL = "https://api.streethawk.com";
        final String DEV_URL = "https://dev.streethawk.com";
        final String KFACTOR_URL = "https://staging.streethawk.com";
        boolean checked = ((RadioButton) view).isChecked();

        String option = ((RadioButton) view).getText().toString();
        if (option != null) {
            switch (option) {
                case "Prod":
                    if (checked)
                        changeTargetUrl(PROD_URL);
                    break;
                case "Staging":
                    if (checked)
                        changeTargetUrl(KFACTOR_URL);
                    break;
                default:
                    break;
            }
        }
    }

    private void changeTargetUrl(String url) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("shKeyHost", url);
        e.commit();
    }

    public void reregister(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Are you sure?");
        builder.setMessage("Do you want to reset this install");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String KEY_ACCESS_DATA = "pushaccessData";
                EditText customUrlET = (EditText) findViewById(R.id.customUrl);
                String customUrl = customUrlET.getText().toString();
                if (null != customUrl) {
                    if (!customUrl.isEmpty()) {
                        changeTargetUrl(customUrl);
                    }
                    Context context = getApplicationContext();
                    SharedPreferences prefs = context.getSharedPreferences(SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = prefs.edit();
                    e.putBoolean("install_state", false);
                    e.putString("installid", null);
                    e.putString(KEY_ACCESS_DATA, null);
                    e.commit();
                    // kill the app and let user reopen it
                    finish();
                    System.exit(0);
                }
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

}
