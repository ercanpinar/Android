package com.streethawk.sdkdebugger;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SHLogCat extends AppCompatActivity implements Constants {


    class LogCatAsyc extends AsyncTask<String, Void, Void> {
        String mLogs;

        @Override
        protected Void doInBackground(String... params) {
            try {
                Process process = Runtime.getRuntime().exec("logcat -d " + TAG_STREETHAWK + ":V *:S");
                if (params[0].equals("CLEAR"))
                    process = Runtime.getRuntime().exec("logcat -c");
                if (params[0].equals("LOGS"))
                    process = Runtime.getRuntime().exec("logcat -d " + params[1] + ":V *:S");
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                StringBuilder log = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    log.append(line);
                }
                mLogs = log.toString();
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView tv = (TextView) findViewById(R.id.logcat);
            tv.setText("Loading logcat, please wait...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            TextView tv = (TextView) findViewById(R.id.logcat);
            tv.setText(mLogs);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shlog_cat);

    }

    public void clearLogs(View view) {
        new LogCatAsyc().execute("CLEAR", TAG_STREETHAWK);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LogCatAsyc().execute("LOGS", TAG_STREETHAWK);
    }


}

