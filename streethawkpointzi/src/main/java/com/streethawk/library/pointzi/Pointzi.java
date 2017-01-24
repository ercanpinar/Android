package com.streethawk.library.pointzi;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.streethawk.library.core.Util;
import com.streethawk.library.core.WidgetDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

/*
*  Entry point of Pointzi
*  Fetches Payload from server
*  Parse and store payload
* */
public class Pointzi implements Constants {
    private Activity mActivity;
    public Pointzi(Activity activity) {
        mActivity = activity;
    }
    public Pointzi() {}
    private void parseAndSaveResponse(Context context, String answer) {
        PointziDB db = new PointziDB(context);
        db.open();
        Trigger trigger = new Trigger();
        try {
            JSONObject feedItem = new JSONObject(answer);
            Object val = feedItem.get(VALUE);
            if (val instanceof JSONArray) {
                JSONArray arr = (JSONArray) val;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    trigger.setFeedID(obj.getString(ID));
                    JSONObject content = obj.getJSONObject(CONTENT);
                    if (null != content) {
                        JSONObject data = new JSONObject(content.getString(DATA));
                        JSONObject init = data.getJSONObject(SETUP);
                        trigger.setSetup(init.toString());
                        String tool = init.getString(TOOL);

                        trigger.setTool(tool);
                        String trgr = init.getString(TRIGGER);
                        JSONObject type = new JSONObject(trgr);
                        trigger.setTriggerType(type.getString(TYPE));
                        trigger.setTrigger(trgr);
                        trigger.setView(init.getString(VIEW));
                        trigger.setTarget(init.getString(TARGET));
                        trigger.setLauncherJSON(trgr);
                            JSONArray toolObject = data.getJSONArray(PAYLOAD);
                            String toolArary = toolObject.toString();
                            trigger.setJSON(toolArary);
                        trigger.setActioned(0);
                    }
                    db.storeTriggerData(trigger);
                }
            }
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
            db.close();
        }
    }

    /**
     * Function fetches pointzi payload from server
     */
    public void fetchPointziPayload(final Context context) {
        if (null == context)
            return;
        final String INSTALL_ID = "installid";
        final String APP_KEY = "app_key";
        final String EQUALS = "=";
        final String OFFSET = "offset";
        final String AND = "&";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL baseurl = Util.getFeedUrl(context);
                    String urlParams = baseurl.toString();
                    urlParams += "?" + INSTALL_ID + EQUALS + Util.getInstallId(context) + AND +
                            APP_KEY + EQUALS + Util.getAppKey(context) + AND +
                            OFFSET + EQUALS + 0;
                    URL url = null;
                    try {
                        url = new URL(urlParams);
                        Log.e("Anurag","URL "+url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String answer = input.readLine();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        parseAndSaveResponse(context, answer);
                        Log.e("Anurag","answer "+answer);
                    }
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private class displayTriggerAsyncTask extends AsyncTask<HashSet<Trigger>, Void, Void> {
        private HashSet<Trigger> objSet;

        @Override
        protected Void doInBackground(HashSet<Trigger>... shTrigers) {
            objSet = shTrigers[0];
            return null;
        }

        private String getViewName(String fullyQualifiedName) {
            String className = new StringBuilder(fullyQualifiedName).reverse().toString();
            int indexOfPeriod = className.indexOf(".");
            if (-1 != indexOfPeriod) {
                className = className.subSequence(0, className.indexOf(".")).toString();
                className = new StringBuilder(className).reverse().toString();
                return className;
            }
            return null;
        }

        private int getResIdFromWidgetName(Activity activity, String widgetName) {
            if (null == widgetName)
                return -1;
            WidgetDBHelper helper = new WidgetDBHelper(activity.getApplicationContext());
            SQLiteDatabase database = helper.getReadableDatabase();

            String parent = getViewName(activity.getClass().getName());
            String WHERE = " where ";
            String EQUALS = " = ";
            String AND = " and ";
            String DOUBLE_QUOTE = "\"";

            String query = "select * from " + WidgetDBHelper.TOOLTIP_TABLE_NAME +
                    WHERE + WidgetDBHelper.COLUMN_TEXT_ID + EQUALS + DOUBLE_QUOTE + widgetName.trim() + DOUBLE_QUOTE +
                    AND + WidgetDBHelper.COLUMN_PARENT_VIEW + EQUALS + DOUBLE_QUOTE + parent.trim() + DOUBLE_QUOTE;
            try {
                Cursor cursor = database.rawQuery(query, null);
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndex(WidgetDBHelper.COLUMN_RES_ID));
                } else {
                    cursor.close();
                    database.close();
                    helper.close();
                    return -1;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                database.close();
                helper.close();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (null == objSet) {
                return;
            }
        }
    }//End of AsyncTaskClass
}
