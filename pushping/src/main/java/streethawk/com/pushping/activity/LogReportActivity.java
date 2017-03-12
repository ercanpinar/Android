package streethawk.com.pushping.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import streethawk.com.pushping.R;

public class LogReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logreport);

    }


    @Override
    protected void onResume() {
        super.onResume();
        TextView logs = (TextView) this.findViewById(R.id.locprev);
        SharedPreferences prefs = this.getSharedPreferences("SHLogging", Context.MODE_PRIVATE);
        String share = prefs.getString("logger", "");
        logs.setText(share);
    }

    public void shareLogs(View view) {
        SharedPreferences prefs = this.getSharedPreferences("SHLogging", Context.MODE_PRIVATE);
        String share = prefs.getString("logger", "");
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, share);
        this.startActivity(intent);
    }


    public void clearLogs(View view) {

        SharedPreferences prefs = this.getSharedPreferences("SHLogging", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString("logger", "");
        e.commit();
        Toast.makeText(this, "Logs cleared", Toast.LENGTH_SHORT).show();
    }


}
