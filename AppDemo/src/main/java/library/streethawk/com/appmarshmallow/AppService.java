package library.streethawk.com.appmarshmallow;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class AppService extends Service {
    public AppService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent deepLinkIntent = new Intent();
                deepLinkIntent.setAction("android.intent.action.VIEW");
                deepLinkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                deepLinkIntent.setData(Uri.parse("thirdactivity://setparams?param1=30"));
                getApplicationContext().startActivity(deepLinkIntent);
            }
        }, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
