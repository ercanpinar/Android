package streethawk.com.pushping.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.streethawk.library.core.StreetHawk;

import streethawk.com.pushping.R;

public class FeedbackActivity extends AppCompatActivity {
    EditText mTitleET;
    EditText mContentET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mTitleET = (EditText) findViewById(R.id.fbtitle);
        mContentET = (EditText) findViewById(R.id.fbcontent);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitleET.getText().toString();
                String content = mContentET.getText().toString();
                if (title.isEmpty() && content.isEmpty()) {
                    Snackbar.make(view, "Please enter title or description", Snackbar.LENGTH_LONG).show();
                    return;
                } else {
                    StreetHawk.INSTANCE.sendSimpleFeedback(title, content);
                    Snackbar.make(view, "Thanks for your feedback!!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}