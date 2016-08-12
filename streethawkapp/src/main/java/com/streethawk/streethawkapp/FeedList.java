package com.streethawk.streethawkapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.streethawk.library.feeds.ISHFeedItemObserver;
import com.streethawk.library.feeds.SHFeedItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FeedList extends AppCompatActivity implements ISHFeedItemObserver, Constants {

    private List<FeedItem> mFeedList;

    private final String INSTALLID = "installid";
    private final String CAMPAIGN = "campaign";
    private final String CREATED = "created";
    private final String DELETED = "deleted";
    private final String ACTIVATES = "activates";
    private final String FEEDID = "id";
    private final String APP = "app";
    private final String EXPIRES = "expires";
    private final String MODIFIED = "modified";
    private final String CONTENTS = "contents";
    private final String APS = "aps";
    private final String SOUND = "sound";
    private final String CATEGORY = "category";
    private final String BADGE = "badge";
    private final String ALERT = "alert";
    private final String CODE = "c";
    private final String TITLE = "t";
    private final String DATA = "d";
    private final String LENGTH = "l";
    private final String FEED_TITLE = "title";
    private final String FEED_MSG = "msg";
    private final String FEED_JSON = "json";
    private final String FEED_URL = "link";
    private final String FEED_IMAGE = "img";
    private final String FEED_VIDEO = "video";
    private final String CONTENT = "content";

    private ListView mFeedItemListView;
    FeedItemAdapter mListViewAdapter;

    class fetchFeedListTask extends AsyncTask<JSONArray,Void,Void>{
        @Override
        protected Void doInBackground(JSONArray... jsonArrays) {
            JSONArray feeds = jsonArrays[0];
            for (int i = 0; i < feeds.length(); i++) {
                try {
                    JSONObject feedObject = feeds.getJSONObject(i);
                    FeedItem feedItemObject = new FeedItem();
                    payLoadParser(feedItemObject, feedObject);
                    mFeedList.add(feedItemObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("Anurag","Notify dataset change");
            mListViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void shFeedReceived(final JSONArray feeds) {
        if (null == feeds)
            return;

        String title = null;
        String message = null;
        String url = null;
        String image = null;
        String video = null;
        if (mFeedList == null) {
            mFeedList = new ArrayList<FeedItem>();
        }
        mFeedList.clear();
        new fetchFeedListTask().execute(feeds);
    }

    private void payLoadParser(final FeedItem feedItemObject, final JSONObject feedJsonObject) {
        if (null != feedJsonObject) {
            try {
                feedItemObject.setInstallId(feedJsonObject.getString(INSTALLID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                feedItemObject.setCampaign(feedJsonObject.getString(CAMPAIGN));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                feedItemObject.setCreated(feedJsonObject.getString(CREATED));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                feedItemObject.setDeleted(feedJsonObject.getString(DELETED));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                feedItemObject.setExpires(feedJsonObject.getString(EXPIRES));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                feedItemObject.setModified(feedJsonObject.getString(MODIFIED));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                feedItemObject.setApp(feedJsonObject.getString(APP));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                feedItemObject.setActivates(feedJsonObject.getString(ACTIVATES));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                feedItemObject.setFeedId(feedJsonObject.getString(FEEDID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                String alert = null;
                int length = 0;
                JSONObject content = feedJsonObject.getJSONObject(CONTENT);
                if (null != content) {
                    JSONObject aps = content.getJSONObject(APS);
                    if (aps != null) {

                        try {
                            feedItemObject.setFeedId(aps.getString(SOUND));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            feedItemObject.setCategory(aps.getString(CATEGORY));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            feedItemObject.setCategory(aps.getString(BADGE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            alert = aps.getString(ALERT);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            alert = null;
                        }
                    }
                    try {
                        String strLengh = content.getString(LENGTH);
                        length = Integer.parseInt(strLengh);
                        feedItemObject.setLength(length);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        feedItemObject.setLength(0);
                    }
                }
                try {
                    String strCode = content.getString(CODE);
                    int code = Integer.parseInt(strCode);
                    feedItemObject.setCode(code);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    feedItemObject.setCode(0);
                }
                try {

                    String data = content.getString(DATA);
                    try {
                        JSONObject dataObject = new JSONObject(data);
                        try {
                            final String imageUrl = dataObject.getString(FEED_IMAGE);
                            feedItemObject.setImage(imageUrl);
                            if (null != imageUrl) {
                                try {
                                    InputStream in = new java.net.URL(imageUrl).openStream();
                                    feedItemObject.setImageId(BitmapFactory.decodeStream(in));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            Log.e("Anurag", "Saving video");
                            feedItemObject.setVideo(dataObject.getString(FEED_VIDEO));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            Log.e("Anurag", "Saving url");
                            feedItemObject.setFeedURL(dataObject.getString(FEED_URL));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {

                    }
                    feedItemObject.setData(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (null != alert) {
                    String title = alert.substring(0, length);
                    String message = null;
                    feedItemObject.setFeedTitle(title);
                    if (alert.length() > length) {
                        message = alert.substring(length + 1);
                    } else {
                        message = null;
                    }
                    feedItemObject.setFeedMessage(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class FeedItemAdapter extends ArrayAdapter<FeedItem> {
        Context context;

        public FeedItemAdapter(Context context, int resource,
                               List<FeedItem> objects) {
            super(context, resource, objects);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.feedsummarydisplay, parent, false);
            FeedItem obj = mFeedList.get(position);
            TextView title = (TextView) rowView.findViewById(R.id.feedtitle);
            TextView message = (TextView) rowView.findViewById(R.id.feedmessage);
            ImageView img = (ImageView) rowView.findViewById(R.id.thumbnail);

            String feedTitle = obj.getFeedTitle();
            if (null != feedTitle)
                title.setText(feedTitle);
            String feedMessage = obj.getFeedMessage();
            if (null != feedMessage)
                message.setText(feedMessage);
            img.setImageBitmap(obj.getImageId());
            return rowView;
        }
    }

    public AdapterView.OnItemClickListener feedItemClickListener() {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FeedItem obj = mFeedList.get(position);
                Context context = getApplicationContext();
                Intent intent = new Intent(context, FeedViewerActivity.class);
                intent.putExtra("FEEDITEM_PARCEL",obj);
                startActivity(intent);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);
        FeedItem startObj = new FeedItem();
        startObj.setFeedTitle("No Feeds");
        startObj.setFeedMessage("Empty");
        mFeedList = new ArrayList<FeedItem>();
        mFeedList.add(startObj);
        mFeedItemListView = (ListView) findViewById(R.id.feeditemlistview);
        mListViewAdapter = new FeedItemAdapter(this,
                R.layout.feedsummarydisplay, mFeedList);
        mFeedItemListView.setAdapter(mListViewAdapter);
        mFeedItemListView.setOnItemClickListener(feedItemClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SHFeedItem.getInstance(this).registerFeedItemObserver(this);
        SHFeedItem.getInstance(this).readFeedData(0);
    }

}
