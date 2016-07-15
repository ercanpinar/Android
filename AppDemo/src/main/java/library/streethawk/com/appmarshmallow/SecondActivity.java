package library.streethawk.com.appmarshmallow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.streethawk.library.core.StreetHawk;

public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    /**
     * Tagging examples
     * @param view
     */
    public void TagFName(View view){
        StreetHawk.INSTANCE.tagString("sh_first_name","StreetHawk");
        StreetHawk.INSTANCE.tagNumeric("BidValue", 50);
        StreetHawk.INSTANCE.tagDatetime("sh_date_of_birth", "1985-06-08");
        Toast.makeText(this,"Tagged",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
