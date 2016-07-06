package com.streethawk.streethawkapp;

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

public class Analytics extends AppCompatActivity {


    private Spinner mTagSpinner;
    private String mOptionSelected;
    EditText mKeyet;
    EditText mValuet;

    String TAG = "tag";                     // 0
    String TAG_CUID = "tagcuid";            // 1
    String TAG_INCREMENT = "tagincrement";  // 2
    String TAG_DELETE = "tagdelete";        // 3


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

        mTagSpinner = (Spinner) findViewById(R.id.tagSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.TagOptions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTagSpinner.setAdapter(adapter);
        mTagSpinner.setOnItemSelectedListener(spinnerSelect());
        mKeyet = (EditText) findViewById(R.id.tagKey);
        mValuet = (EditText) findViewById(R.id.tagval);
    }

    private Spinner.OnItemSelectedListener spinnerSelect(){
        return new Spinner.OnItemSelectedListener(){

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
                    return;
                }
                if (2 == position) {
                    mOptionSelected = TAG_INCREMENT;
                    mValuet.setHint("Increment Value");
                    return;
                }
                if (3 == position) {
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

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }


    private View.OnClickListener sendTag() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String key = mKeyet.getText().toString();
                if (key != null) {
                    if (!key.isEmpty()) {
                        String value = mValuet.getText().toString();
                        if (value.isEmpty()) {
                            if (mOptionSelected.equals(TAG_DELETE)) {
                                StreetHawk.INSTANCE.removeTag(key);
                                displayToast("Removed tag "+key);
                            } else {
                                Toast.makeText(getApplicationContext(), "Enter Value", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            try {
                                double dval = Double.parseDouble(value);
                                StreetHawk.INSTANCE.tagNumeric(key, dval);
                                displayToast("Sent numeric tag "+key+ " "+dval);
                            } catch (NumberFormatException e) {
                                if(mOptionSelected.equals(TAG)){
                                    StreetHawk.INSTANCE.tagString(key, value);
                                    displayToast("Sent String tag "+key+ " "+value);
                                }else if (mOptionSelected.equals(TAG_CUID)){
                                    StreetHawk.INSTANCE.tagCuid(value);
                                    displayToast("Tagged install with cuid "+value);
                                }else if(mOptionSelected.equals(TAG_INCREMENT)){
                                    try{
                                        int val = Integer.parseInt(value);
                                        StreetHawk.INSTANCE.incrementTag(key,val);
                                        displayToast("Incremented tag "+key+ " "+value);
                                    }catch(Exception exp) {
                                        StreetHawk.INSTANCE.incrementTag(key);
                                        displayToast("Incremented tag "+key + " by 1");
                                    }
                                }
                            }
                        }
                    } else
                        Toast.makeText(getApplicationContext(), "Enter key", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getApplicationContext(), "Enter key", Toast.LENGTH_LONG).show();
            }
        };
    }
}
