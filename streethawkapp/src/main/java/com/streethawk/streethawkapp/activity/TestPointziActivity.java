package com.streethawk.streethawkapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.streethawk.streethawkapp.R;


public class TestPointziActivity extends AppCompatActivity {

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pointzi);
        mActivity = this;
    }

    public void unit_test_tip(View view) {
        // new Tip().unit_test_tip(mActivity);
    }


}
