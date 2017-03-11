package com.streethawk.streethawkapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.streethawk.library.core.StreetHawk;
import com.streethawk.streethawkapp.R;

/*
import com.streethawk.library.feeds.SHTips;
import com.streethawk.library.feeds.SHTours;
*/
public class AnalyticsActivity extends AppCompatActivity {


    private Spinner mTagSpinner;
    private String mOptionSelected;
    EditText mKeyet;
    EditText mValuet;

    String TAG = "tag";                // 0
    String TAG_CUID = "tagcuid";            // 1
    String TAG_PHONE = "tagphone";           // 2
    String TAG_DATETIME = "tagDatetime";        // 3
    String TAG_INCREMENT = "tagincrement";       // 4
    String TAG_DELETE = "tagdelete";          // 5

    private Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAnalytics);
        fab.setOnClickListener(sendTag());
        //fab.setOnClickListener(unitTestTip());

        mTagSpinner = (Spinner) findViewById(R.id.tagSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.TagOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTagSpinner.setAdapter(adapter);
        mTagSpinner.setOnItemSelectedListener(spinnerSelect());
        mKeyet = (EditText) findViewById(R.id.tagKey);
        mValuet = (EditText) findViewById(R.id.tagval);
        mActivity = this;
    }

    private Spinner.OnItemSelectedListener spinnerSelect() {
        return new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mKeyet.setEnabled(true);
                mValuet.setEnabled(true);
                if (0 == position) {
                    mOptionSelected = TAG;
                    return;
                }
                if (1 == position) {
                    mOptionSelected = TAG_CUID;
                    mKeyet.setText("sh_cuid");
                    mKeyet.setEnabled(false);
                    mValuet.setHint("");
                    return;
                }
                if (2 == position) {
                    mOptionSelected = TAG_PHONE;
                    mKeyet.setText("sh_phone");
                    mKeyet.setEnabled(false);
                    mValuet.setHint("+61469342981");
                    mValuet.setEnabled(true);
                }
                if (3 == position) {
                    mOptionSelected = TAG_DATETIME;
                    mValuet.setText("2014-07-25 15:33:20");
                }
                if (4 == position) {
                    mOptionSelected = TAG_INCREMENT;
                    mValuet.setHint("Increment Value");
                    return;
                }
                if (5 == position) {
                    mOptionSelected = TAG_DELETE;
                    mValuet.setHint("");
                    mValuet.setEnabled(false);
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mOptionSelected = TAG;
            }
        };
    }

    private void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    private View.OnClickListener unitTestTip() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new Tip().unit_test_tip(mActivity);
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        //TODO: This method is not working and parameter problem.
        //new Tip().showTip_new(mActivity,findViewById(R.id.fabAnalytics));
    }


    private View.OnClickListener sendTag() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /*
                SHTours tours =new SHTours(mActivity);
                tours.startTour("454162");

                SHTips tips = new SHTips();
                tips.unit_test_tooltip(mActivity,"tagKey");
*/

                String key = mKeyet.getText().toString();
                if (key != null) {
                    if (!key.isEmpty()) {
                        if (mOptionSelected == TAG_DELETE) {
                            StreetHawk.INSTANCE.removeTag(key);
                            Toast.makeText(getApplicationContext(), "Removed tag " + key, Toast.LENGTH_LONG).show();
                        }
                        if (mOptionSelected == TAG_CUID) {
                            String value = mValuet.getText().toString();
                            if (null != value) {
                                if (!value.isEmpty()) {
                                    StreetHawk.INSTANCE.tagString("sh_cuid", value);
                                    Toast.makeText(getApplicationContext(), "Cuid tagged " + value, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        if (mOptionSelected == TAG_INCREMENT) {
                            String value = mValuet.getText().toString();
                            if (null != value) {
                                if (value.isEmpty()) {
                                    StreetHawk.INSTANCE.incrementTag(key);
                                    Toast.makeText(getApplicationContext(), "Increment tag value by 1", Toast.LENGTH_LONG).show();
                                } else {
                                    double dval = 1.0;
                                    try {
                                        dval = Double.parseDouble(value);
                                        StreetHawk.INSTANCE.incrementTag(key, dval);
                                        Toast.makeText(getApplicationContext(), "Increment tag value by " + dval, Toast.LENGTH_LONG).show();
                                    } catch (NumberFormatException e) {
                                        StreetHawk.INSTANCE.incrementTag(key);
                                        Toast.makeText(getApplicationContext(), "Increment tag value by 1", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                        if (mOptionSelected == TAG) {
                            String value = mValuet.getText().toString();
                            if (value != null) {
                                if (value.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Enter a value ", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        double val = Double.parseDouble(value);
                                        StreetHawk.INSTANCE.tagNumeric(key, val);
                                        Toast.makeText(getApplicationContext(), "Numeric tag " + key + " : " + value, Toast.LENGTH_LONG).show();
                                    } catch (NumberFormatException e) {
                                        StreetHawk.INSTANCE.tagString(key, value);
                                        Toast.makeText(getApplicationContext(), "String tag " + key + " : " + value, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                        if (mOptionSelected == TAG_PHONE) {
                            String value = mValuet.getText().toString();
                            if (value != null) {
                                if (value.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Enter a value ", Toast.LENGTH_LONG).show();
                                } else {
                                    StreetHawk.INSTANCE.tagString("sh_phone", value);
                                    Toast.makeText(getApplicationContext(), "Phone number tagged " + key + " : " + value, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        if (mOptionSelected == TAG_DATETIME) {
                            String value = mValuet.getText().toString();
                            if (value != null) {
                                if (value.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Enter a value ", Toast.LENGTH_LONG).show();
                                } else {
                                    StreetHawk.INSTANCE.tagDatetime(key, value);
                                    Toast.makeText(getApplicationContext(), "Phone number tagged " + key + " : " + value, Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Enter key", Toast.LENGTH_LONG).show();
                    }
                }
            }

        };
    }
}
