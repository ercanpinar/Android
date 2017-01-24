package com.streethawk.library.pointzi;

import android.util.Log;

/**
 * Stores details about a given widget
 */
public class WidgetObject {

    private String mTextID;
    private String mParentView;
    private int mID;
    private String type;
    private float startX;
    private float startY;
    private float width;
    private float height;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setParentViewName(String viewName){
        mParentView = viewName;
    }

    public String getParentViewName(){
        return mParentView;
    }

    public void setTextID(String textID){
        mTextID = textID;
    }

    public String getTextID(){
        return mTextID;
    }

    public void setResID(int resID){
        mID = resID;
    }

    public int getResID(){
        return mID;
    }

    public WidgetObject(){}

    public void displayMyData(String tag){
        String NEWLINE = "\n";
        String myData = "TextID: "+this.mTextID + NEWLINE +
                "ParentView: "+this.mParentView + NEWLINE +
                "ResID: "+this.mID + NEWLINE +
                "Type : "+this.type + NEWLINE +
                "StartX: "+this.startX + NEWLINE +
                "StartY: "+this.startY + NEWLINE +
                "Width: "+this.width + NEWLINE +
                "Height: "+this.height;
        Log.d(tag,myData);
    }
}