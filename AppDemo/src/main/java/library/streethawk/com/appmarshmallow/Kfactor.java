package library.streethawk.com.appmarshmallow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.streethawk.library.core.Util;

public class Kfactor extends Activity {
    private void changeTargetUrl(String url){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("shKeyHost",url);
        e.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kfactor);
    }


    public void onRadioButtonClicked(View view){
        final String PROD_URL   = "https://api.streethawk.com";
        final String DEV_URL    = "https://dev.streethawk.com";
        final String KFACTOR_URL = "https://api.kfacta.com";
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.dev:
                if (checked)
                    changeTargetUrl(DEV_URL);
                break;
            case R.id.prod:
                if (checked)
                    changeTargetUrl(PROD_URL);
                break;
            case R.id.kfactor:
                if (checked)
                    changeTargetUrl(KFACTOR_URL);
                break;
        }
    }

    public void reregister(View view){
        EditText customUrlET = (EditText)findViewById(R.id.customUrl);
        String customUrl = customUrlET.getText().toString();
        if(null!=customUrl){
            if(!customUrl.isEmpty()){
                changeTargetUrl(customUrl);
            }
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(Util.SHSHARED_PREF_PERM, Context.MODE_PRIVATE);
            SharedPreferences.Editor e = prefs.edit();
            e.putBoolean("install_state", false);
            e.putString("installid", null);
            e.commit();

            // kill the app and let user reopen it
            finish();
            System.exit(0);
        }






    }





}
