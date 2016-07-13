package com.streethawk.debugger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int INSTALL_INFO    = 0;
    int LOGCAT          = INSTALL_INFO + 1;
    int TAGS            = LOGCAT + 1;
    int GROWTH          = TAGS + 1;
    int PUSH            = GROWTH + 1;
    int FEEDS           = PUSH + 1;
    int GEOFENCE        = FEEDS + 1;
    int BEACONS         = GEOFENCE + 1;
    int FEEDBACK        = BEACONS + 1;
    int CRASHREPORT     = FEEDBACK + 1;
    int HELP            = CRASHREPORT + 1;

    private ListView mListView = null;
    private View mView = null;
    private final String[] mOptions = new String[]{
            "Install Info",
            "Tags",
            "Growth",
            "Push",
            "Feeds",
            "Geofence",
            "Beacons",
            "Feedback",
            "Crash App",
            "Help"
    };

    private class StableArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }
        
        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    public AdapterView.OnItemClickListener optionsOnclickListener(){
        return new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent=null;
                if(INSTALL_INFO==position){
                    intent = new Intent(getApplicationContext(),InstallInfo.class);
                }
                if(LOGCAT==position){
                    intent = new Intent(getApplicationContext(),GrowthActivity.class);
                }
                if(TAGS==position){
                    intent = new Intent(getApplicationContext(),PushActivity.class);
                }
                if(PUSH==position){

                }
                if(FEEDS==position){

                }
                if(GEOFENCE==position){

                }
                if(BEACONS==position){

                }
                if(GROWTH==position){

                }
                if(HELP == position){

                }
                if(CRASHREPORT == position){

                }
                if(FEEDBACK == position){

                }
                if(null!=intent){
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        };
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView)findViewById(R.id.options);
        final ArrayList<String> list = new ArrayList<String>();
        for(int i=0;i<mOptions.length;i++){
            list.add(mOptions[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                R.layout.customlistview, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(optionsOnclickListener());


    }
}
