package streethawk.com.pushping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmTaskReceiver extends BroadcastReceiver {
    private final String TAG = "PushTester";

    public AlarmTaskReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.streethawk.pushtester.alarmtask")) {
            new PushPingService().reportToServer();
        }
        if (intent.getAction().equals("com.streethawk.pushtester.heartbeat")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss");
            final String currentDateandTime = sdf.format(new Date());
            new PushPingService().sendSlackMessage("#monitoring_prod", "Android Heart beat " + currentDateandTime);
        }
    }
}
