package streethawk.com.pushping.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by anuragkondeya on 31/07/2016.
 */
public class FeedItem implements Parcelable {
    private String mInstallId;
    private String mCreated;
    private String mFeedTitle;
    private String mFeedMessage;
    private String mURL;
    private String mImage;
    private String mVideo;
    private String mData;
    private String mMessageId;
    private String mCampaign;
    private String mDeleted;
    private String mExpires;
    private String mModified;
    private String Activates;
    private int mCode;
    private String App;
    private String FeedId;

    private String Sound;
    private String Badge;
    private String Category;
    private int Length;
    private String mRawJSON;

    private Bitmap mImageId;


    public FeedItem() {
    }

    public void setImageId(Bitmap id) {
        mImageId = id;
    }

    public Bitmap getImageId() {
        return mImageId;
    }

    public void setLength(int i) {
        Length = i;
    }

    public void setCategory(String str) {
        Category = str;
    }

    public void setBadge(String badge) {
        Badge = badge;
    }

    public void setSound(String sound) {
        Sound = sound;
    }

    public void setFeedTitle(String str) {
        mFeedTitle = str;
    }

    public void setFeedId(String id) {
        FeedId = id;
    }

    public void setRawJSON(String json) {
        mRawJSON = json;
    }

    public void setInstallId(String installid) {
        mInstallId = installid;
    }

    public void setFeedMessage(String str) {
        mFeedMessage = str;
    }

    public void setActivates(String str) {
        Activates = str;
    }

    public void setFeedURL(String str) {
        mURL = str;
    }

    public void setImage(String str) {
        mImage = str;
    }

    public void setData(String data) {
        mData = data;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public void setMessageId(String id) {
        mMessageId = id;
    }

    public void setCampaign(String campaign) {
        mCampaign = campaign;
    }

    public void setDeleted(String deleted) {
        mDeleted = deleted;
    }

    public void setApp(String app) {
        App = app;
    }

    public void setExpires(String expires) {
        mExpires = expires;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    public void setModified(String modified) {
        mModified = modified;
    }

    public void setVideo(String str) {
        mVideo = str;
    }

    public String getActivates() {
        return Activates;
    }

    public String getFeedTitle() {
        return mFeedTitle;
    }

    public String getCreated() {
        return mCreated;
    }

    public String getFeedMessage() {
        return mFeedMessage;
    }

    public String getURL() {
        return mURL;
    }

    public String getApp() {
        return App;
    }

    public String getImage() {
        return mImage;
    }

    public String getFeedId() {
        return FeedId;
    }

    public String getVideo() {
        return mVideo;
    }

    public String getData() {
        return mData;
    }

    public int getCode() {
        return mCode;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public String getCampaign() {
        return mCampaign;
    }

    public String getDeleted() {
        return mDeleted;
    }

    public String getExpires() {
        return mExpires;
    }

    public String getModified() {
        return mModified;
    }

    public String getmInstallId() {
        return mInstallId;
    }

    public String getCategory() {
        return Category;
    }

    public String getBadge() {
        return Badge;
    }

    public String getSound() {
        return Sound;
    }

    public int getLength() {
        return Length;
    }

    public String getRawJSON() {
        return mRawJSON;
    }


    public String getObjectDetails() {
        String NEWLINE = "\n";

        return "Feed ID" + FeedId + NEWLINE
                + "Title " + mFeedTitle + NEWLINE
                + "Message " + mFeedMessage + NEWLINE
                + "URL " + mURL + NEWLINE
                + "Img " + mImage + NEWLINE
                + "Video " + mVideo + NEWLINE + NEWLINE
                + "JSON" + mRawJSON;


    }

    public void displayForDebugging(String TAG, String MSG) {

        Log.e(TAG, MSG + getObjectDetails());
    }

    protected FeedItem(Parcel in) {
        mInstallId = in.readString();
        mCreated = in.readString();
        mFeedTitle = in.readString();
        mFeedMessage = in.readString();
        mURL = in.readString();
        mImage = in.readString();
        mVideo = in.readString();
        mData = in.readString();
        mMessageId = in.readString();
        mCampaign = in.readString();
        mDeleted = in.readString();
        mExpires = in.readString();
        mModified = in.readString();
        Activates = in.readString();
        mCode = in.readInt();
        App = in.readString();
        FeedId = in.readString();
        Sound = in.readString();
        Badge = in.readString();
        Category = in.readString();
        Length = in.readInt();
        // mImageId = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mInstallId);
        dest.writeString(mCreated);
        dest.writeString(mFeedTitle);
        dest.writeString(mFeedMessage);
        dest.writeString(mURL);
        dest.writeString(mImage);
        dest.writeString(mVideo);
        dest.writeString(mData);
        dest.writeString(mMessageId);
        dest.writeString(mCampaign);
        dest.writeString(mDeleted);
        dest.writeString(mExpires);
        dest.writeString(mModified);
        dest.writeString(Activates);
        dest.writeInt(mCode);
        dest.writeString(App);
        dest.writeString(FeedId);
        dest.writeString(Sound);
        dest.writeString(Badge);
        dest.writeString(Category);
        dest.writeInt(Length);
        //dest.writeValue(mImageId);
    }

    @SuppressWarnings("unused")
    public static final Creator<FeedItem> CREATOR = new Creator<FeedItem>() {
        @Override
        public FeedItem createFromParcel(Parcel in) {
            return new FeedItem(in);
        }

        @Override
        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };
}