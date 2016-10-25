package com.streethawk.library.feeds;

import android.util.Log;

/**
 * Class encapsulates tirgger objects
 * triggers are ways using which people can start tours / tips / models etc
 */
class SHTriger implements Constants {

    private String  mFeedID;
    private String  mTool;
    private String  mSetup;
    private String  mView;
    private int     mActioned;
    private String  mJson;
    private String  mTrigger;

    public void setFeedID(String feedID){mFeedID = feedID;}
    public void setTool(String tool){mTool = tool;}
    public void setSetup(String setup){mSetup = setup;}
    public void setView(String view){mView = view;}
    public void setActioned(int actioned){mActioned = actioned;}
    public void setJSON(String json){mJson = json;}
    public void setTrigger(String trigger){mTrigger = trigger;}

    public String getFeedID(){ return mFeedID;}
    public String getTool(){return mTool;}
    public String getSetup(){return mSetup;}
    public String getView(){return mView;}
    public int getActioned(){return mActioned;}
    public String getJSON(){return mJson;}
    public String getTrigger(){return mTrigger;}

    public String getDebugString(){
        return "mFeedID "+mFeedID+" "+
                "mTool "+mTool+" "+
                "mSetup "+mSetup+" "+
                "mView "+mView+" "+
                "mActioned "+mActioned+" "+
                "mJson "+mJson+
                "mTrigger "+mTrigger+" ";
    }
}
