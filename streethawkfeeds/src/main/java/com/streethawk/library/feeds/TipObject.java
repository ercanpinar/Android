package com.streethawk.library.feeds;

import android.app.Activity;

import org.json.JSONArray;

/**
 * Created by anuragkondeya on 1/09/2016.
 */
class TipObject implements Constants {

    private String mId;
    private String mTitle;
    private String mContent;
    private String mPlacement = BOTTOM;  //Direction of the widget
    private String mTarget;     // Target element of the widget
    private JSONArray mChild;

    private String mBackGroundColor = "#FFFFFF";   // Default white
    private String mTitleColor = "#000000";       // Default black
    private String mContentColor = "#000000";    //  Default black

    private String mAcceptedButtonTitle;
    private String mDelineButtonTitle;

    private int mDelay;
    private String mImageURL;
    private String mParent;
    private String mCloseButtonTitle;

    private String mDNDTitle;
    private String mDNDContent;
    private String mDNDB1;
    private String mDNDB2;
    private boolean mHasDND=false;

    public void setId(String id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setPlacement(String placement) {
        mPlacement = placement;
    }

    public void setTarget(String target) {
        mTarget = target;
    }

    public void setBackGroundColor(String color) {
        mBackGroundColor = color;
    }

    public void setTitleColor(String color) {
        mTitleColor = color;
    }

    public void setContentColor(String color) {
        mContentColor = color;
    }

    public void setAcceptedButtonTitle(String btntitle) {
        mAcceptedButtonTitle = btntitle;
    }

    public void setDeclinedButtonTitle(String btnTitle) {
        mDelineButtonTitle = btnTitle;
    }

    public void setDelay(int delay) {
        mDelay = delay;
    }

    public void setImageUrl(String imgURL) {
        mImageURL = imgURL;
    }

    public void setParent(String parent) {
        mParent = parent;
    }

    public void setCloseButtonTitle(String buttonTitle) {
        mCloseButtonTitle = buttonTitle;
    }

    public void setDNDTitle(String title) {
        mDNDTitle = title;
    }

    public void setDNDContent(String content) {
        mDNDContent = content;
    }

    public void setDNDB1(String b1) {
        mDNDB1 = b1;
    }

    public void setDNDB2(String b2) {
        mDNDB2 = b2;
    }

    public void setHasDND(boolean flag) {
        mHasDND = flag;
    }

    public void setChild(JSONArray child){mChild = child;}


    public String getBackGroundColor() {
        return mBackGroundColor;
    }

    public String getTitleColor() {
        return mTitleColor;
    }

    public String getContentColor() {
        return mContentColor;
    }

    public String getAcceptedButtonTitle() {
        return mAcceptedButtonTitle;
    }

    public String getDelineButtonTitle() {
        return mDelineButtonTitle;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public String getPlacement() {
        return mPlacement;
    }

    public String getTarget() {
        return mTarget;
    }

    public boolean getHasDND() {
        return mHasDND;
    }

    public int getDelay() {
        return mDelay;
    }

    public String getImageURL() {
        return mImageURL;
    }

    public String getParent() {
        return mParent;
    }

    public String getCloseButtonTitle() {
        return mCloseButtonTitle;
    }


    public String getDNDTitle() {
        return mDNDTitle;
    }

    public String getDNDContent() {
        return mDNDContent;
    }

    public String getDNDB1() {
        return mDNDB1;
    }

    public String getDNDB2() {
        return mDNDB2;
    }

    public JSONArray getChild() {return mChild;}
}
