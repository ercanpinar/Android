package streethawk.com.pushping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.streethawk.library.feeds.SHFeedItem;

public class FeedViewerActivity extends Activity implements Constants {

    private FeedItem mFeedItem = null;
    private FrameLayout mBaseFrame;

    private int ACCEPTED = 1;
    private int DECLINED = -1;
    private int LATER   =  0;

    private String mJSON="NODATA";

    private static String  mFeedId = null;

    public void SendLike(View view){
        try {
            int id = Integer.parseInt(mFeedId);
            SHFeedItem.getInstance(getApplicationContext()).notifyFeedResult(id,"accepted",true,true);

            Toast.makeText(getApplicationContext(),"Sent feed result accepted",Toast.LENGTH_LONG).show();
        }catch(NumberFormatException e){
            Toast.makeText(getApplicationContext(),"FeedId is not a int",Toast.LENGTH_LONG).show();
        }
    }

    public void SendDislike(View view){
        try {
            int id = Integer.parseInt(mFeedId);
            SHFeedItem.getInstance(getApplicationContext()).notifyFeedResult(id,"rejected",true,true);
            Toast.makeText(getApplicationContext(),"Sent feed result rejected",Toast.LENGTH_LONG).show();
        }catch(NumberFormatException e){
            Toast.makeText(getApplicationContext(),"FeedId is not a int",Toast.LENGTH_LONG).show();
        }
    }

    public void SendLater(View view){
        try {
            int id = Integer.parseInt(mFeedId);
            SHFeedItem.getInstance(getApplicationContext()).notifyFeedResult(id,"postponed",false,false);
            Toast.makeText(getApplicationContext(),"Sent feed result postponed",Toast.LENGTH_LONG).show();
        }catch(NumberFormatException e){
            Toast.makeText(getApplicationContext(),"FeedId is not a int",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_viewer);
        Intent intent = getIntent();
        if (null != intent) {
            mFeedItem = intent.getParcelableExtra("FEEDITEM_PARCEL");
            mJSON = intent.getStringExtra("FEEDITEM_PARCEL_JSON");
            if(null!=mFeedItem) {

            }
        }
    }

    private void sendFeedAckLog(){
        if(mFeedId==null) {
            return;
        }
        try {
            int int_feed_id = Integer.parseInt(mFeedId);
            SHFeedItem.getInstance(this).sendFeedAck(int_feed_id);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFeedItem != null) {
            mFeedId = mFeedItem.getFeedId();
            sendFeedAckLog();
            TextView feed = (TextView)findViewById(R.id.jsonText);
            feed.setText(mFeedItem.getObjectDetails()+ "/n" + mJSON);
            }
        }

    }
