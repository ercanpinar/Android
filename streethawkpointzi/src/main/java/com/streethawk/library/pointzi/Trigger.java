package com.streethawk.library.pointzi;

/**
 * Created by anuragkondeya on 31/10/16.
 */

public class Trigger {

    private String  mFeedID;
    private String  mTool;
    private String  mSetup;
    private String  mView;
    private int     mActioned;
    private String  mJson;
    private String  mTrigger;
    private String  mTriggerType;
    private String mTarget;
    private String mLauncherJSON;


    public void setFeedID(String feedID){mFeedID = feedID;}
    public void setTool(String tool){mTool = tool;}
    public void setSetup(String setup){mSetup = setup;}
    public void setView(String view){mView = view;}
    public void setActioned(int actioned){mActioned = actioned;}
    public void setJSON(String json){mJson = json;}
    public void setTrigger(String trigger){mTrigger = trigger;}
    public void setTriggerType(String mTriggerType) {this.mTriggerType = mTriggerType;}
    public void setTarget(String mTarget) {this.mTarget = mTarget;}
    public void setLauncherJSON(String mLauncherJSON) {this.mLauncherJSON = mLauncherJSON;}



    public String getTriggerType() {return mTriggerType;}
    public String getFeedID(){ return mFeedID;}
    public String getTool(){return mTool;}
    public String getSetup(){return mSetup;}
    public String getView(){return mView;}
    public int getActioned(){return mActioned;}
    public String getJSON(){return mJson;}
    public String getTrigger(){return mTrigger;}
    public String getTarget() {return mTarget;}
    public String getLauncherJSON() {return mLauncherJSON;}


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
