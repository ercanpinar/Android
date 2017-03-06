package com.streethawk.streethawkapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Pointzi extends AppCompatActivity {

    private Activity mActivity;

/*
    private class PointziArrayAdapter extends ArrayAdapter<PointziParams> {
        ArrayList<PointziParams> mIdMap = new ArrayList<PointziParams>();


        public PointziArrayAdapter(Context context, int textViewResourceId,
                                  List<PointziParams> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.add(objects.get(i));
            }
        }
        @Override
        public PointziParams getItemId(int position) {
            return mIdMap.get(position);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointzi);
        mActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(add());
    }

    private View.OnClickListener add() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
    }
/*
    @Override
    protected void onResume() {
        super.onResume();

        ListView listview = (ListView)findViewById(R.id.pointizilist);


        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < mOptions.length; i++) {

        }
        final Pointzi.PointziArrayAdapter adapter = new MainActivity.PointziArrayAdapter(this,
                R.layout.customlistview, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener();



    }
    */
}
