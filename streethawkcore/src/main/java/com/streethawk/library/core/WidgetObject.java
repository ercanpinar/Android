package com.streethawk.library.core;

import android.util.Log;

/**
 * Stores details about a given widget
 */
public class WidgetObject {
    String mTextID;
    String mParentView;
    int mID;


    public WidgetObject() {
    }

    public void setParentViewName(String viewName) {
        mParentView = viewName;
    }

    public void setTextID(String textID) {
        mTextID = textID;
    }

    public void setResID(int resID) {
        mID = resID;
    }


    public String getTextID() {
        return mTextID;
    }

    public int getResID() {
        return mID;
    }


    public String getParentViewName() {
        return mParentView;
    }

    public void displayMyData(String tag) {
        String NEWLINE = "\n";
        String myData = "TextID: " + this.mTextID + NEWLINE +
                "ParentView: " + this.mParentView + NEWLINE +
                "ResID: " + this.mID;
        Log.d(tag, myData);
    }
}
