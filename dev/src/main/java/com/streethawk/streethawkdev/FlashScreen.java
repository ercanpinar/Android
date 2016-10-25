package com.streethawk.streethawkdev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class FlashScreen extends Activity implements Constants {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screem);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences prefs = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
                boolean isSetUpRequired = prefs.getBoolean(KEY_SETUP,true);
                Intent intent = new Intent(FlashScreen.this,MainActivity.class);
                intent.putExtra(KEY_SETUP,isSetUpRequired);
                FlashScreen.this.startActivity(intent);
                FlashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
