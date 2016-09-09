package com.streethawk.library.feeds;

import android.util.Log;

/**
 * Class encapsulates tirgger objects
 * triggers are ways using which people can start tours / tips / models etc
 */
class SHTriger implements Constants {

    private String mDisplay;
    private String mTriger;
    private String mTarget;
    private String mView;
    private int mDelay;
    private String mTool;
    private String mWidgetType;
    private String mWidgetLabel;
    private String mWidgetCss;
    private String mbgColor;
    private String mToolId;     //Id of the tour/tip/modal to start
    private String mPlacement;


    String classDetails = "ToolID" + COLON + mToolId + NEWLINE +
            "mDisplay" + COLON + mDisplay + NEWLINE +
            "mTriger" + COLON + mTriger + NEWLINE +
            "mTarget" + COLON + mTarget + NEWLINE +
            "mView" + COLON + mView + NEWLINE +
            "mDelay" + COLON + mDelay + NEWLINE +
            "mTool" + COLON + mTool + NEWLINE +
            "mWidgetType" + COLON + mWidgetType + NEWLINE +
            "mWidgetLabel" + COLON + mWidgetLabel + NEWLINE +
            "mWidgetCss" + COLON + mWidgetCss + NEWLINE +
            "mbgColor" + COLON + mbgColor + NEWLINE +
            "mPlacement" + COLON + mPlacement + NEWLINE;


    /**
     * Function retuns String of class values for debugging purposes
     *
     * @return
     */
    public String getClassValuesForDebugging() {
        return classDetails;
    }

    public void setDisplay(String display) {
        mDisplay = display;
    }

    public void setTriger(String trigger) {
        mTriger = trigger;
    }

    public void setTarget(String target) {
        mTarget = target;
    }

    public void setView(String view) {
        mView = view;
    }

    public void setDelay(int delay) {
        mDelay = delay;
    }

    public void setTool(String tool) {
        mTool = tool;
    }

    public void setWidgetType(String type) {
        mWidgetType = type;
    }

    public void setWidgetLabel(String label) {
        mWidgetLabel = label;
    }

    public void setWidgetCss(String css) {
        mWidgetCss = css;
    }

    public void setBGColor(String bgcolor) {
        mbgColor = bgcolor;
    }

    public void setToolID(String id) {
        mToolId = id;
    }

    public void setPlacement(String placement) {mPlacement = placement;}


    public String getDisplay() {
        return mDisplay;
    }

    public String getTriger() {
        return mTriger;
    }

    public String getTarget() {
        return mTarget;
    }

    public String getView() {
        return mView;
    }

    public int getDelay() {
        return mDelay;
    }

    public String getTool() {
        return mTool;
    }

    public String getWidgetType() {
        return mWidgetType;
    }

    public String getWidgetLabel() {
        return mWidgetLabel;
    }

    public String getWidgetCss() {
        return mWidgetCss;
    }

    public String getBGColor() {
        return mbgColor;
    }

    public String getPlacement() {return mPlacement;}

    public String getToolId() {return mToolId;}

}
