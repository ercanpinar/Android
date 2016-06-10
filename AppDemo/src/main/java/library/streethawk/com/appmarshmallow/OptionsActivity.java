package library.streethawk.com.appmarshmallow;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class OptionsActivity extends AppCompatActivity implements Constants,
        Setup.OnFragmentInteractionListener,ShowAppOptions.OnFragmentInteractionListener,Settings.OnFragmentInteractionListener,
        Analytics.OnFragmentInteractionListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.StreetHawkColor)));


        Intent intent = getIntent();
        boolean isSetupDone = false;
        if(null!=intent) {
             isSetupDone= intent.getBooleanExtra(KEY_SETUP,false);
        }
        if(findViewById(R.id.fragment_container)!=null){
            if(savedInstanceState!=null)
                return;
        }
        if(isSetupDone){
            ShowAppOptions options = new ShowAppOptions();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, options).commit();
        }else{
            Setup setupFragment = new Setup();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, setupFragment).commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.settings:
                Settings settings = new Settings();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, settings).commit();
                break;
            case R.id.documents:
                Intent docs = new Intent(Intent.ACTION_VIEW, Uri.parse("https://streethawk.freshdesk.com/solution/folders/5000273033"));
                startActivity(docs);
                break;
            case R.id.website:
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://streethawk.com"));
                startActivity(websiteIntent);
                break;
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.e("Anurag","OnFragment interaction listener");
    }
}
