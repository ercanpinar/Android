package library.streethawk.com.appmarshmallow;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.streethawk.library.push.ISHObserver;
import com.streethawk.library.push.Push;
import com.streethawk.library.push.PushDataForApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class AppService extends Service implements ISHObserver{
    public AppService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("Anurag","App registering service as push notification listener");
        Push.getInstance(this).registerSHObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void shReceivedRawJSON(String title, String message, String json) {

    }

    @Override
    public void shNotifyAppPage(String pageName) {

    }

    @Override
    public void onReceivePushData(PushDataForApplication pushData) {
        Log.e("Anurag","Received push data");
        pushData.displayDataForDebugging("Anurag");
    }

    @Override
    public void onReceiveResult(PushDataForApplication resultData, int result) {
        resultData.displayDataForDebugging("Anurag");
        Log.e("Anurag","Push result "+result);
    }


    @Override
    public void onReceiveNonSHPushPayload(Bundle pushPayload) {
        Log.e("Anurag","Received 3rd party payload");

        JSONObject json = new JSONObject();
        Set<String> keys = pushPayload.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, pushPayload.get(key));
            } catch(JSONException e) {
                //Handle exception here
            }
        }
    }
}
