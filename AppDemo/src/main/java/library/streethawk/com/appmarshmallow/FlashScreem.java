package library.streethawk.com.appmarshmallow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class FlashScreem extends Activity implements Constants {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screem);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                SharedPreferences prefs = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
                boolean isSetupDone = prefs.getBoolean(KEY_SETUP,false);
                Intent intent = new Intent(FlashScreem.this,OptionsActivity.class);
                intent.putExtra(KEY_SETUP,isSetupDone);
                FlashScreem.this.startActivity(intent);
                FlashScreem.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
